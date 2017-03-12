package ca.rasul;

import co.da.jmtg.amort.FixedAmortizationCalculator;
import co.da.jmtg.amort.FixedAmortizationCalculators;
import co.da.jmtg.amort.PmtKey;
import co.da.jmtg.amort.PmtKeys;
import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtCalculators;
import co.da.jmtg.pmt.PmtPeriod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class DeeperpocketsApplicationTests {
    private static final Logger LOG = LoggerFactory.getLogger(DeeperpocketsApplication.class);

    @Test
	public void contextLoads() {
        loan(165000.00, 3.375,30);
        loan(396720.00, 3.75,30);
        loan(74385.00, 4.0,30);
	}

    private void loan(double  amount, double interestRate, int years) {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = amount;
        PmtCalculator pmtCalculator1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);
        LOG.info(pmtKey1.getKeys().toString());
        LOG.info("Payment : " +pmtCalculator1.getPmt());
    }

    @Test
    public void primaryMortgage(){

    }
}
