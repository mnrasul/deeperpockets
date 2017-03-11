package ca.rasul;

import ca.rasul.jpa.*;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.BalanceInfo;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.investment.positions.BasePosition;
import com.webcohesion.ofx4j.domain.data.investment.positions.InvestmentPositionList;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponse;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseTransaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

import static com.webcohesion.ofx4j.domain.data.banking.AccountType.CREDITLINE;

@EnableJpaRepositories
@SpringBootApplication
public class DeeperpocketsApplication {
    private static final Logger LOG = LoggerFactory.getLogger(DeeperpocketsApplication.class);
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    public static void main(String[] args) {
        SpringApplication.run(DeeperpocketsApplication.class, args);
    }

    @Bean
    public CommandLineRunner testOfxImport() throws IOException, OFXParseException {

        return input -> {
            AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
            File folder = new File("src/main/resources/qfx");
            File[] files = folder.listFiles(pathname -> pathname.getAbsolutePath().endsWith(".qfx"));
            for (File file : files) {
                ResponseEnvelope unmarshal = unmarshaller.unmarshal(Files.newBufferedReader(Paths.get(file.getAbsolutePath())));
                if (unmarshal != null) {
                    SortedSet<ResponseMessageSet> messageSets = unmarshal.getMessageSets();
                    for (ResponseMessageSet message : messageSets) {
                        processBankTransactions(message);
                        processInvestmentAccountTransaction(message);
                    }
                }

            }
        };
    }

    @Bean
    public CommandLineRunner importOpeningBalances() throws  Exception{
        return (String... intput) -> {
            Stream<String> stream = Files.lines(Paths.get("src/main/resources/opening-balance.properties"));
            stream.parallel()
                    .filter(s -> !s.contains("#"))
                    .filter(s -> s.contains(","))
                    .map(s -> s).forEach(s -> {
                String[] split = s.split(",");
                String accountId = split[0];
                String bankId = split[1].substring(0, split[1].indexOf("="));
                BigDecimal amount = new BigDecimal(s.substring(s.indexOf("=")+1));
                Account byAccountIdAndBankId = accountRepository.findByAccountIdAndBankId(accountId, bankId);
                if (byAccountIdAndBankId != null){
                    try {
                        ca.rasul.jpa.Transaction transaction = new ca.rasul.jpa.Transaction(s+"opening-balance",
                                amount, new Date(), "Opening balance adjustment", "Opening balance adjustment", "DEBIT",
                                byAccountIdAndBankId.getId());
                        if (amount.doubleValue() != 0.0 ) {
                            transactionRepository.save(transaction);
                        }
                    } catch (ParseException e) {
                        LOG.error(e.getMessage());
                    }
//                    transactionRepository.save()
                }
            });
        };
    }

    private void processBankTransactions(ResponseMessageSet messageSet) throws ParseException {
        if (!(messageSet instanceof BankingResponseMessageSet)) {
            return;
        }
        int saved = 0;
        List<BankStatementResponseTransaction> statementResponses = ((BankingResponseMessageSet) messageSet).getStatementResponses();
        for (BankStatementResponseTransaction transaction : statementResponses) {
            Account account = createAccount(transaction.getMessage().getAccount());
            Account byAccountIdAndBankId = accountRepository.findByAccountIdAndBankId(account.getAccountId(), account.getBankId());
            if (byAccountIdAndBankId == null) {
                byAccountIdAndBankId = accountRepository.save(account);
            }

            for (Transaction t : transaction.getMessage().getTransactionList().getTransactions()) {
                if (!transactionRepository.exists(t.getId())) {
                    ca.rasul.jpa.Transaction dbTransaction = new ca.rasul.jpa.Transaction(t.getId(),
                            new BigDecimal(t.getAmount()), t.getDatePosted(), t.getName(), t.getMemo(), t.getTransactionType().name(), byAccountIdAndBankId.getId());
                    transactionRepository.save(dbTransaction);
                    saved++;
                }
            }
            if (transaction.getMessage().getAccount().getAccountType() == CREDITLINE){
                BigDecimal networthOfAccount = transactionRepository.findNetworthOfAccount(byAccountIdAndBankId.getId());
                BalanceInfo ledgerBalance = transaction.getMessage().getLedgerBalance();
                BigDecimal balance = new BigDecimal(ledgerBalance.getAmount());
                Date date = ledgerBalance.getAsOfDate();
                ca.rasul.jpa.Transaction interestAdjustment = new ca.rasul.jpa.Transaction(createId(byAccountIdAndBankId, date),
                        networthOfAccount.add(balance).negate(), date, "interest paid", "interest paid. This is an adjusting entry added when a sync occurs, so is dependent on how frequently that is done", "DEBIT", byAccountIdAndBankId.getId());
                //only save if there's a significant difference
                if (interestAdjustment.getAmount().doubleValue() < 0) {
                    transactionRepository.save(interestAdjustment);
                }

            }
        }
        LOG.info("New Transactions " + saved);
    }

    private String createId(final Account account, final Date date) {
        return String.format("interestadjustment-%d-%d",account.getId(),date.getTime());
    }

    private Account createAccount(final BankAccountDetails account) {
        return new Account(account.getAccountNumber(),
                account.getBankId(), account.getAccountType().name(), account.getRoutingNumber(), "USD");
    }

    private void processInvestmentAccountTransaction(ResponseMessageSet messageSet) {
        if (!(messageSet instanceof InvestmentStatementResponseMessageSet)) {
            return;
        }
        InvestmentStatementResponseMessageSet messages = (InvestmentStatementResponseMessageSet) messageSet;
        for (InvestmentStatementResponseTransaction investmentTransaction : messages.getStatementResponses()) {
            InvestmentStatementResponse message = investmentTransaction.getMessage();
            InvestmentPositionList positionList = message.getPositionList();
            Account byAccountIdAndBankId = accountRepository.findByAccountIdAndBankId(message.getAccount().getAccountNumber(), message.getAccount().getBrokerId());
            if (byAccountIdAndBankId == null) {
                byAccountIdAndBankId = new Account(message.getAccount().getAccountNumber(), message.getAccount().getBrokerId(),
                        "INVESTMENT", message.getAccount().getBrokerId(), message.getCurrencyCode());
                byAccountIdAndBankId = accountRepository.save(byAccountIdAndBankId);
            }

            for (BasePosition position : positionList.getPositions()) {
                if (investmentRepository.findByIdAndAccountId(position.getSecurityId().getUniqueId(), byAccountIdAndBankId.getId()) == null) {
                    Investment investment = new Investment();
                    investment.setId(position.getSecurityId().getUniqueId());
                    investment.setAccountId(byAccountIdAndBankId.getId());
                    investment.setMarketValue(new BigDecimal(position.getMarketValue()));
                    investment.setMarketValueDate(position.getMarketValueDate());
                    investment.setType(position.getPositionType());
                    investment.setUnits(new BigDecimal(position.getUnits()));
                    investment.setUnitPrice(new BigDecimal(position.getUnitPrice()));
                    investment.setInvestmentReturn(investment.getInvestmentReturn());
                    investment.setInvestmentPercentage(investment.getInvestmentPercentage());
                    investmentRepository.save(investment);
                }
            }
        }

    }
//    @Bean
//    public CommandLineRunner importData() {
//        return parameters -> {
//            List<String> tags = Arrays.asList("ORG", "FID", "BANKID", "ACCTID", "ACCTTYPE", "TRNTYPE", "DTPOSTED", "TRNAMT", "FITID", "NAME");
//
//            List<BankTransaction> transactions = importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt.qfx")));
//            transactions.addAll(importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt-bofa-2017.qfx"))));
//            transactions.addAll(importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt-rent-account.qfx"))));
//            System.out.println(transactions);
//            System.out.println("Imported :"+transactions.size());
//        };
//    }

//    private List<BankTransaction> importTransactions(final List<String> tags, final Stream<String> stream) throws ParseException {
//        boolean skip = true;
//        List<Pair<String, String>> output =
//                stream  .parallel().filter(x -> x.contains("<"))   //ignore the headers
//                        .map(s -> {
//                    final int index = s.indexOf('>');
//                    return new Pair<>(s.substring(1, index), s.substring(index + 1));
//                })
//                        .filter(x -> tags.contains(x.getKey()))
////                .forEach(System.out::println);
//                        .collect(toList());
//
//        List<BankTransaction> transactions = new ArrayList<>();
//        BankTransaction transaction = null;
//        OFXReport report = new OFXReport();
//        OFXBody body = new OFXBody();
//        body.setStmttrnrs(new STMTTRNRS());
//        body.getStmttrnrs().setStmtrs(new STMTRS());
//        OFXHeader header = new OFXHeader();
//        header.setSonrs(new SONRS());
//        FinancialInsitution fi = new FinancialInsitution();
//        report.setHeader(header);
//        Bank bank = new Bank();
//        Account bankAccount = null;
//        for (Pair<String, String> pair : output) {
//            if (pair.getKey().equals("TRNTYPE")) {
//                if (transaction == null) {
//                    transaction = new BankTransaction();
//                } else {
////                        System.out.println(transaction);
//                    if (bankAccount == null) {
//                        if (accountRepository.findByAccountIdAndBankId(bank.getAccountId(), bank.getBankId()) == null) {
//                            bankAccount = new Account(bank.getAccountId(), bank.getBankId(), bank.getAccountType(), fi.getBank(), fi.getBankId());
//                            accountRepository.save(bankAccount);
//                        }
//                        bankAccount = accountRepository.findByAccountIdAndBankId(bank.getAccountId(), bank.getBankId());
//                    }
//                    if (!transactionRepository.exists(transaction.getTransactionId())) {
//                        transactionRepository.save(new Transaction(transaction.getTransactionId(), transaction.getTransactionAmount(),
//                                transaction.getDatePosted(), transaction.getName(), transaction.getMemo(), transaction.getTransactionType(), bankAccount.getId()));
//                        transactions.add(transaction);
//                        transaction = new BankTransaction();
//                    }
//                }
//                transaction.setTransactionType(pair.getValue());
//            }
//            if (pair.getKey().equals("ORG")) {
//                fi.setBank(pair.getValue());
//            }
//            if (pair.getKey().equals("FID")) {
//                fi.setBankId(pair.getValue());
//            }
//
//            if (pair.getKey().equals("DTPOSTED")) {
//                transaction.setDatePosted(pair.getValue());
//            }
//            if (pair.getKey().equals("TRNAMT")) {
//                transaction.setTransactionAmount(pair.getValue());
//            }
//            if (pair.getKey().equals("FITID")) {
//                transaction.setTransactionId(pair.getValue());
//            }
//            if (pair.getKey().equals("NAME")) {
//                transaction.setName(pair.getValue());
//            }
//
//            if (pair.getKey().equals("BANKID")) {
//                bank.setBankId(pair.getValue());
//            }
//            if (pair.getKey().equals("ACCTID")) {
//                bank.setAccountId(pair.getValue());
//            }
//            if (pair.getKey().equals("ACCTTYPE")) {
//                bank.setAccountType(pair.getValue());
//            }
//        }
//        header.getSonrs().setFinancialInsitution(fi);
//        BankTransactionList list = new BankTransactionList();
//        body.getStmttrnrs().getStmtrs().setTransactions(list);
//        list.setBankTransactions(transactions);
//        body.getStmttrnrs().getStmtrs().setBank(bank);
//        report.setBody(body);
//        return transactions;
//    }

//    @Bean
//    public CommandLineRunner demo(){
//        return parameters -> {
//            accountRepository.save(new Account("abc", "bofa", "CHECKING", "111", "USD"));
//
//            log.info(String.valueOf(accountRepository.count()));
//        };
//    }
}
