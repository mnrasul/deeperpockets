package ca.rasul;

import ca.rasul.jpa.Account;
import ca.rasul.jpa.AccountRepository;
import ca.rasul.jpa.Transaction;
import ca.rasul.jpa.TransactionRepository;
import ca.rasul.ofx.*;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@EnableJpaRepositories
@SpringBootApplication
public class DeeperpocketsApplication  {
    private static final Logger log = LoggerFactory.getLogger(DeeperpocketsApplication.class);
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public static void main(String[] args) {
        SpringApplication.run(DeeperpocketsApplication.class, args);
    }

    @Bean
    public CommandLineRunner importData() {
        return parameters -> {
            List<String> tags = Arrays.asList("ORG", "FID", "BANKID", "ACCTID", "ACCTTYPE", "TRNTYPE", "DTPOSTED", "TRNAMT", "FITID", "NAME");

            List<BankTransaction> transactions = importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt.qfx")));
            transactions.addAll(importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt-bofa-2017.qfx"))));
            transactions.addAll(importTransactions(tags, Files.lines(Paths.get("src/main/resources/stmt-rent-account.qfx"))));
            System.out.println(transactions);
            System.out.println("Imported :"+transactions.size());
        };
    }

    private List<BankTransaction> importTransactions(final List<String> tags, final Stream<String> stream) throws ParseException {
        boolean skip = true;
        List<Pair<String, String>> output =
                stream  .filter(x -> x.contains("<"))   //ignore the headers
                        .map(s -> {
                    final int index = s.indexOf('>');
                    return new Pair<>(s.substring(1, index), s.substring(index + 1));
                })
                        .filter(x -> tags.contains(x.getKey()))
//                .forEach(System.out::println);
                        .collect(toList());

        List<BankTransaction> transactions = new ArrayList<>();
        BankTransaction transaction = null;
        OFXReport report = new OFXReport();
        OFXBody body = new OFXBody();
        body.setStmttrnrs(new STMTTRNRS());
        body.getStmttrnrs().setStmtrs(new STMTRS());
        OFXHeader header = new OFXHeader();
        header.setSonrs(new SONRS());
        FinancialInsitution fi = new FinancialInsitution();
        report.setHeader(header);
        Bank bank = new Bank();
        Account bankAccount = null;
        for (Pair<String, String> pair : output) {
            if (pair.getKey().equals("TRNTYPE")) {
                if (transaction == null) {
                    transaction = new BankTransaction();
                } else {
//                        System.out.println(transaction);
                    if (bankAccount == null) {
                        if (accountRepository.findByAccountIdAndBankId(bank.getAccountId(), bank.getBankId()) == null) {
                            bankAccount = new Account(bank.getAccountId(), bank.getBankId(), bank.getAccountType(), fi.getBank(), fi.getBankId());
                            accountRepository.save(bankAccount);
                        }
                        bankAccount = accountRepository.findByAccountIdAndBankId(bank.getAccountId(), bank.getBankId());
                    }
                    if (!transactionRepository.exists(transaction.getTransactionId())) {
                        transactionRepository.save(new Transaction(transaction.getTransactionId(), transaction.getTransactionAmount(),
                                transaction.getDatePosted(), transaction.getName(), transaction.getMemo(), transaction.getTransactionType(), bankAccount.getId()));
                        transactions.add(transaction);
                        transaction = new BankTransaction();
                    }
                }
                transaction.setTransactionType(pair.getValue());
            }
            if (pair.getKey().equals("ORG")) {
                fi.setBank(pair.getValue());
            }
            if (pair.getKey().equals("FID")) {
                fi.setBankId(pair.getValue());
            }

            if (pair.getKey().equals("DTPOSTED")) {
                transaction.setDatePosted(pair.getValue());
            }
            if (pair.getKey().equals("TRNAMT")) {
                transaction.setTransactionAmount(pair.getValue());
            }
            if (pair.getKey().equals("FITID")) {
                transaction.setTransactionId(pair.getValue());
            }
            if (pair.getKey().equals("NAME")) {
                transaction.setName(pair.getValue());
            }

            if (pair.getKey().equals("BANKID")) {
                bank.setBankId(pair.getValue());
            }
            if (pair.getKey().equals("ACCTID")) {
                bank.setAccountId(pair.getValue());
            }
            if (pair.getKey().equals("ACCTTYPE")) {
                bank.setAccountType(pair.getValue());
            }
        }
        header.getSonrs().setFinancialInsitution(fi);
        BankTransactionList list = new BankTransactionList();
        body.getStmttrnrs().getStmtrs().setTransactions(list);
        list.setBankTransactions(transactions);
        body.getStmttrnrs().getStmtrs().setBank(bank);
        report.setBody(body);
        return transactions;
    }

//    @Bean
//    public CommandLineRunner demo(){
//        return parameters -> {
//            accountRepository.save(new Account("abc", "bofa", "CHECKING", "111", "USD"));
//
//            log.info(String.valueOf(accountRepository.count()));
//        };
//    }
}
