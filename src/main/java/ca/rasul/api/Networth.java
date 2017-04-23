package ca.rasul.api;

import ca.rasul.config.Loan;
import ca.rasul.jpa.AccountRepository;
import ca.rasul.jpa.InvestmentRepository;
import ca.rasul.jpa.TransactionRepository;
import co.da.jmtg.amort.FixedAmortizationCalculator;
import co.da.jmtg.amort.FixedAmortizationCalculators;
import co.da.jmtg.amort.PmtKey;
import co.da.jmtg.amort.PmtKeys;
import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtCalculators;
import co.da.jmtg.pmt.PmtPeriod;
import co.da.jmtg.pmt.extra.ExtraPmt;
import co.da.jmtg.pmt.extra.ExtraPmts;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */

@Component
@Path("/networth")
@Slf4j
public class Networth {

    @Autowired private NumberFormat currencyFormatter;
    @Autowired private InvestmentRepository investmentRepository;

    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository  transactionRepository;
    @Autowired private ObjectMapper objectMapper;
//    @GET
//    public String computeNetworth(){
//        return  currencyFormatter.format(transactionRepository.findNetworthOfAssets().add(investmentRepository.findNetworthOfInvestments()));
//    }

    @GET
    @Path("loans")
    public String loans() throws IOException {
        List<Pair<String, Double>> result = new ArrayList<>();
        for(Loan loan: loadLoans()){
            result.add(determineCapitalizationSchedule(loan));
        }
        return objectMapper.writeValueAsString(result);
    }

    private List<Loan> loadLoans() throws IOException {
        File file = new File("src/main/resources/loans.json");
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Loan.class));
    }

//    private String determineCapitalizationSchedule(final BigDecimal principal, final BigDecimal payment, final BigDecimal interestRate, final int term, final PmtPeriod pmtPeriod) {
    private Pair<String, Double> determineCapitalizationSchedule(final Loan loan) {
        double loanAmt = loan.getOriginalPrincipal();
        final PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;

        PmtCalculator pmtCalculator1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, loan.getInterestRate(), loan.getTerm());
        LocalDate firstPaymentDate = new LocalDate(2017, 2, 1);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod,firstPaymentDate , loan.getTerm());
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);

        // Set a one time extra payment on the first payment.
        double extraPayment = loan.getPayment() - pmtCalculator1.getPmt();
        if (extraPayment > 0) {
            PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, firstPaymentDate, loan.getTerm());
            ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, extraPayment);
            amortCalculator1 = amortCalculator1.setExtraPayment(extraPmt);
//            log.info(pmtKey1.getKeys().toString());
//        Assert.assertEquals(payment.doubleValue(), pmtCalculator1.getPmt(), 0.0);
        }
        log.info("Payment : " + pmtCalculator1.getPmt());
        LocalDate previous  = null;
        LocalDate today = new LocalDate();
        for (LocalDate date : amortCalculator1.getTable().keySet()) {
            if (date.isAfter(today)){
                log.info(loan.getAccountNumber() + " " + today.toString("yyyy-MM-dd"));
                return new Pair<>(loan.getAccountNumber(), amortCalculator1.getTable().get(date).getBalance());
            }
        }
        return new Pair<>("", 0.0);
    }
}
