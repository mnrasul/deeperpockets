package ca.rasul;

import co.da.jmtg.amort.FixedAmortizationCalculator;
import co.da.jmtg.amort.FixedAmortizationCalculators;
import co.da.jmtg.amort.PmtKey;
import co.da.jmtg.amort.PmtKeys;
import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtCalculators;
import co.da.jmtg.pmt.PmtPeriod;
import co.da.jmtg.pmt.extra.ExtraPmt;
import co.da.jmtg.pmt.extra.ExtraPmts;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Slf4j
public class InterestInferrerTest {

//    @Test
    public void inferInterest() {
        BigDecimal principal = new BigDecimal("34091.45");
        BigDecimal payment = new BigDecimal("1004.21");
        BigDecimal interestRate = new BigDecimal("2.045");
        int term = 35;
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        determineCapitalizationSchedule(principal, payment, interestRate, term, pmtPeriod);
    }

    @Test
    public void mortgage() {
        BigDecimal principal = new BigDecimal("396720");
        BigDecimal payment = new BigDecimal("2000");
        BigDecimal interestRate = new BigDecimal("3.75");
        int term = 360;
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        determineCapitalizationSchedule(principal, payment, interestRate, term, pmtPeriod);
    }
    @Test
    public void secondmortgage() {
        BigDecimal principal = new BigDecimal("74385.00");
        BigDecimal payment = new BigDecimal("505.13");
        BigDecimal interestRate = new BigDecimal("4.00");
        int term = 360;
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        determineCapitalizationSchedule(principal, payment, interestRate, term, pmtPeriod);
    }

    private void determineCapitalizationSchedule(final BigDecimal principal, final BigDecimal payment, final BigDecimal interestRate, final int term, final PmtPeriod pmtPeriod) {
        double loanAmt = principal.doubleValue();

        PmtCalculator pmtCalculator1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate.doubleValue(), term);
        LocalDate firstPaymentDate = new LocalDate(2017, 2, 1);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod,firstPaymentDate , term);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);

        // Set a one time extra payment on the first payment.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, firstPaymentDate, term);
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, payment.doubleValue() - pmtCalculator1.getPmt());
        amortCalculator1 = amortCalculator1.setExtraPayment(extraPmt);
        log.info(pmtKey1.getKeys().toString());
        log.info("Payment : " + pmtCalculator1.getPmt());
//        Assert.assertEquals(payment.doubleValue(), pmtCalculator1.getPmt(), 0.0);

        LocalDate previous  = null;
        for (LocalDate date : amortCalculator1.getTable().keySet()) {
            double currentBalance = amortCalculator1.getTable().get(date).getBalance();
            double previousBalance = 0;
            if (previous != null){
                previousBalance = amortCalculator1.getTable().get(previous).getBalance();
            }
            log.info(date + " : " + currentBalance + " : "+ (currentBalance - previousBalance));
            previous = date;
        }
    }
}
