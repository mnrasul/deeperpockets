package ca.rasul;

import ca.rasul.config.CategoryMapper;
import ca.rasul.config.Loan;
import ca.rasul.jpa.*;
import co.da.jmtg.amort.FixedAmortizationCalculator;
import co.da.jmtg.amort.FixedAmortizationCalculators;
import co.da.jmtg.amort.PmtKey;
import co.da.jmtg.amort.PmtKeys;
import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtCalculators;
import co.da.jmtg.pmt.PmtPeriod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcohesion.ofx4j.domain.data.ResponseEnvelope;
import com.webcohesion.ofx4j.domain.data.ResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import com.webcohesion.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.banking.BankingResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.common.Transaction;
import com.webcohesion.ofx4j.domain.data.common.TransactionType;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardAccountDetails;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import com.webcohesion.ofx4j.domain.data.investment.positions.BasePosition;
import com.webcohesion.ofx4j.domain.data.investment.positions.InvestmentPositionList;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponse;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseMessageSet;
import com.webcohesion.ofx4j.domain.data.investment.statements.InvestmentStatementResponseTransaction;
import com.webcohesion.ofx4j.io.AggregateUnmarshaller;
import com.webcohesion.ofx4j.io.OFXParseException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Stream;

@EnableJpaRepositories
@SpringBootApplication
@ComponentScan("ca.rasul")
@Slf4j
public class DeeperpocketsApplication {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public static void main(String[] args) {
        SpringApplication.run(DeeperpocketsApplication.class, args);
    }

    public Map<String, BankIdentification> bankIdentificationMap() throws IOException {
        HashMap<String, BankIdentification> map = new HashMap<>();
        Stream<String> stream = Files.lines(Paths.get("src/main/resources/bin.properties"));
        stream.parallel()
                .filter(s -> !s.contains("#"))
                .map(s -> s).forEach(s -> {
            String[] split = s.split("=");
            map.put(split[0], BankIdentification.createFromCSV(split[1]));
        });
        return map;
    }

    @Bean
    public NumberFormat getCurrencyFormat() {
        return NumberFormat.getCurrencyInstance(Locale.US);
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
                        processCreditCardAccount(message);
                    }
                }

            }
        };
    }

    private void processCreditCardAccount(final ResponseMessageSet message) throws IOException {
        if (!(message instanceof CreditCardResponseMessageSet)) {
            return;
        }
        Map<String, BankIdentification> stringBankIdentificationMap = bankIdentificationMap();
        CreditCardResponseMessageSet cardSet = (CreditCardResponseMessageSet) message;
        List<CreditCardStatementResponseTransaction> statementResponses = cardSet.getStatementResponses();
        for (CreditCardStatementResponseTransaction transaction : statementResponses) {
            Account account = createAccount(transaction.getMessage().getAccount());
            Account byAccountIdAndBankId = accountRepository.findByAccountNumberAndBankId(account.getAccountNumber(), account.getBankId());
            if (byAccountIdAndBankId == null) {
                byAccountIdAndBankId = accountRepository.save(account);
            }
            for (Transaction t : transaction.getMessage().getTransactionList().getTransactions()) {
                if (!transactionRepository.exists(t.getId())) {
                    ca.rasul.jpa.Transaction ccTransaction = ca.rasul.jpa.Transaction.builder()
                            .accountNumber(byAccountIdAndBankId.getId())
                            .amount(t.getBigDecimalAmount())
                            .datePosted(t.getDatePosted())
                            .id(t.getId())
                            .memo(t.getMemo())
                            .name(t.getName())
                            .type(t.getTransactionType().name())
                            .build();
                    transactionRepository.save(ccTransaction);
                }
            }
            log.info(String.valueOf(byAccountIdAndBankId.getId()));
        }
    }

    private Account createAccount(final CreditCardAccountDetails account) throws IOException {
        Map<String, BankIdentification> bankIdentificationMap = bankIdentificationMap();
        BankIdentification bankIdentification = bankIdentificationMap.get(account.getAccountNumber().substring(0, 6));
        return Account.builder()
                .accountNumber(account.getAccountNumber())
                .bankId(bankIdentification.getBankName())
                .accountType(bankIdentification.getAccountType())
                .currency(bankIdentification.getCurrency()).build();
    }

    @Bean
    public CommandLineRunner importOpeningBalances() throws Exception {
        return (String... intput) -> {
            Stream<String> stream = Files.lines(Paths.get("src/main/resources/opening-balance.properties"));
            stream.parallel()
                    .filter(s -> !s.contains("#"))
                    .filter(s -> s.contains(","))
                    .map(s -> s).forEach(s -> {
                String[] split = s.split(",");
                String accountId = split[0];
                String bankId = split[1].substring(0, split[1].indexOf("="));
                BigDecimal amount = new BigDecimal(s.substring(s.indexOf("=") + 1));
                Account byAccountIdAndBankId = accountRepository.findByAccountNumberAndBankId(accountId, bankId);
                if (byAccountIdAndBankId != null) {
                    ca.rasul.jpa.Transaction transaction = new ca.rasul.jpa.Transaction(s + "opening-balance",
                            amount, jan012017(), "Opening balance adjustment", "Opening balance adjustment", "DEBIT",
                            byAccountIdAndBankId.getId());
                    transaction.setCategory(categoryMapper.determineCategory(transaction.getMemo(), transaction.getName()));
                    if (amount.doubleValue() != 0.0) {
                        transactionRepository.save(transaction);
                    }
//                    transactionRepository.save()
                }
            });
        };
    }

    private Date jan012017() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

//    @Bean
//    public CommandLineRunner loadPositions(){
//        return (String... strings) -> {
//            File file = new File("/Users/nasir/Documents/stocks/Portfolio-My Portfolio.csv");
//            List<Position> inputList = new ArrayList<Position>();
//            Random random = new Random();
//            try{
////            File inputF = new File(inputFilePath);
//                InputStream inputFS = new FileInputStream(file);
//                BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
//                // skip the header of the csv
//                inputList = br.lines().skip(1).map((line) -> {
//
//                            String[] p = line.split(",");// a CSV has comma separated lines
//                            Position item = Position.builder()
//                                    .ticker(p[0])
//                                    .id(random.nextLong())
//                                    .quantity(Integer.parseInt(p[5]))
//                                    .build();
//                            return  item;
//                        }
//
//
//                )
//                        .collect(Collectors.toList());
//                br.close();
//            } catch (IOException e) {
//                log.error(e.getLocalizedMessage());
//            }
//        };
//
//    }

    private void processBankTransactions(ResponseMessageSet messageSet) throws ParseException, IOException {
        if (!(messageSet instanceof BankingResponseMessageSet)) {
            return;
        }
        int saved = 0;
        List<BankStatementResponseTransaction> statementResponses = ((BankingResponseMessageSet) messageSet).getStatementResponses();
        for (BankStatementResponseTransaction transaction : statementResponses) {
            Account account = createAccount(transaction.getMessage().getAccount());
            Account byAccountIdAndBankId = accountRepository.findByAccountNumberAndBankId(account.getAccountNumber(), account.getBankId());
            if (byAccountIdAndBankId == null) {
                byAccountIdAndBankId = accountRepository.save(account);
            }
            Map<AccountId, Loan> loanMap = readLoans();
            AccountId accountId = AccountId.builder().accountId(byAccountIdAndBankId.getAccountNumber()).bankId(byAccountIdAndBankId.getBankId()).build();

            for (Transaction t : transaction.getMessage().getTransactionList().getTransactions()) {
                if (!transactionRepository.exists(t.getId())) {
                    if (loanMap.containsKey(accountId) && t.getTransactionType() == TransactionType.CREDIT){
                        Loan loan = loanMap.get(accountId);
                        SortedMap<LocalDate, FixedAmortizationCalculator.Payment> ammortizationSchedule =
                                calculateSchedule(loan.getOriginalPrincipal(), loan.getInterestRate(), loan.getTerm(), 0);
                        log.info(ammortizationSchedule.toString());
                        FixedAmortizationCalculator.Payment payment = ammortizationSchedule.get(LocalDate.fromDateFields(t.getDatePosted()));
                        ca.rasul.jpa.Transaction interest = ca.rasul.jpa.Transaction.builder().accountNumber(byAccountIdAndBankId.getId())
                                .amount(new BigDecimal(- payment.getInterest()))
                                .category("interest")
                                .datePosted(t.getDatePosted())
                                .memo("interest charges")
                                .merchant(byAccountIdAndBankId.getInstitution())
                                .id("interest-"+t.getDatePosted())
                                .build();
                        transactionRepository.save(interest);

//                        ca.rasul.jpa.Transaction.builder().accountNumber(byAccountIdAndBankId.getId()).amount()
//                                .
                    } else {
                        log.warn("no loan data found");
                    }
                    ca.rasul.jpa.Transaction dbTransaction = new ca.rasul.jpa.Transaction(t.getId(),
                            new BigDecimal(t.getAmount()), t.getDatePosted(), t.getName(), t.getMemo(), t.getTransactionType().name(), byAccountIdAndBankId.getId());
                    dbTransaction.setCategory(categoryMapper.determineCategory(dbTransaction.getMemo(), dbTransaction.getName()));
                    transactionRepository.save(dbTransaction);
                    saved++;
                }
            }
//            if (transaction.getMessage().getAccount().getAccountType() == CREDITLINE) {
//
//
//                BigDecimal networthOfAccount = transactionRepository.findNetworthOfAccount(byAccountIdAndBankId.getId());
//                BalanceInfo ledgerBalance = transaction.getMessage().getLedgerBalance();
//                BigDecimal balance = new BigDecimal(ledgerBalance.getAmount());
//                Date date = ledgerBalance.getAsOfDate();
//                String interestPaidMemo = "interest paid " + byAccountIdAndBankId.getAccountNumber();
//                ca.rasul.jpa.Transaction interestAdjustment = new ca.rasul.jpa.Transaction(createId(byAccountIdAndBankId, date),
//                        networthOfAccount.add(balance).negate(), date, interestPaidMemo, null, "DEBIT", byAccountIdAndBankId.getId());
//                //only save if there's a significant difference
//                interestAdjustment.setCategory(categoryMapper.determineCategory(interestPaidMemo, null));
//                if (interestAdjustment.getAmount().doubleValue() < 0) {
//                    transactionRepository.save(interestAdjustment);
//                }
//
//            }
        }
        log.info("New Transactions " + saved);
    }

    private String createId(final Account account, final Date date) {
        return String.format("interestadjustment-%d-%d", account.getId(), date.getTime());
    }

    private Account createAccount(final BankAccountDetails account) {
        return Account.builder().accountNumber(account.getAccountNumber())
                .bankId(account.getBankId()).accountType(account.getAccountType().name())
                .institution(account.getRoutingNumber()).currency("USD").build();
    }

    private void processInvestmentAccountTransaction(ResponseMessageSet messageSet) {
        if (!(messageSet instanceof InvestmentStatementResponseMessageSet)) {
            return;
        }
        InvestmentStatementResponseMessageSet messages = (InvestmentStatementResponseMessageSet) messageSet;
        for (InvestmentStatementResponseTransaction investmentTransaction : messages.getStatementResponses()) {
            InvestmentStatementResponse message = investmentTransaction.getMessage();
            InvestmentPositionList positionList = message.getPositionList();
            Account byAccountIdAndBankId = accountRepository.findByAccountNumberAndBankId(message.getAccount().getAccountNumber(), message.getAccount().getBrokerId());
            if (byAccountIdAndBankId == null) {
                byAccountIdAndBankId = Account.builder()
                        .accountNumber(message.getAccount().getAccountNumber())
                        .bankId(message.getAccount().getBrokerId())
                        .accountType("INVESTMENT")
                        .institution(message.getAccount().getBrokerId())
                        .currency(message.getCurrencyCode()).build();
                byAccountIdAndBankId = accountRepository.save(byAccountIdAndBankId);
            }

            for (BasePosition position : positionList.getPositions()) {
                if (investmentRepository.findOne(new InvestmentsPrimaryKey(position.getSecurityId().getUniqueId(), byAccountIdAndBankId.getId())) == null) {
                    Investment investment = new Investment();
                    investment.setId(new InvestmentsPrimaryKey(position.getSecurityId().getUniqueId(), byAccountIdAndBankId.getId()));
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

    @Bean
    @Qualifier("categoryMap")
    public Map<String, String> getCategoryMap() throws IOException {
        Map<String, String> categoryMap = new HashMap<>(1000);
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/categories.properties")));
        for (String property : properties.stringPropertyNames()) {
            String value = properties.getProperty(property);
            String[] split = value.split(",");
            for (String s : split) {
                categoryMap.put(s, property);
            }
        }
        return categoryMap;
    }

    @Bean
    public Map<AccountId, Loan> readLoans() throws IOException {
            Loan[] loans = objectMapper.readValue(new File("src/main/resources/loans.json"), Loan[].class);
            log.info("" +loans.length);
            Map<AccountId, Loan> loanMap = new HashMap<>();
            for (Loan loan: loans) {
                AccountId id = AccountId.builder().accountId(loan.getAccountNumber()).bankId(loan.getBankId()).build();
                loanMap.put(id, loan);
            }
            return loanMap;
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
//                        if (accountRepository.findByAccountIdAndBankId(bank.getAccountNumber(), bank.getBankId()) == null) {
//                            bankAccount = new Account(bank.getAccountNumber(), bank.getBankId(), bank.getAccountType(), fi.getBank(), fi.getBankId());
//                            accountRepository.save(bankAccount);
//                        }
//                        bankAccount = accountRepository.findByAccountIdAndBankId(bank.getAccountNumber(), bank.getBankId());
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
//                bank.setAccountNumber(pair.getValue());
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

    private SortedMap<LocalDate, FixedAmortizationCalculator.Payment> calculateSchedule(double loanAmt, double interestRate, int term, double extraPayment ){
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, term);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, new LocalDate(2016, 6, 21) , term);

        // Create the calculator with extra payments.
        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(pmtCalculator, pmtKey);

        return amortCalculator.getTable();
    }
}
