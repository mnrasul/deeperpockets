package ca.rasul;

import com.webcohesion.ofx4j.OFXException;
import com.webcohesion.ofx4j.client.*;
import com.webcohesion.ofx4j.client.impl.FinancialInstitutionServiceImpl;
import com.webcohesion.ofx4j.domain.data.banking.AccountType;
import com.webcohesion.ofx4j.domain.data.banking.BankAccountDetails;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Slf4j
public class OFXClientTest {

//    @Test
    public void testDownloadBofa() throws OFXException {
        FinancialInstitutionData data = new FinancialInstitutionData() {
            @Override
            public String getId() {
//                return "5959";
                return "6805";
            }

            @Override
            public String getFinancialInstitutionId() {
//                return "5959";
                return "6805";
            }

            @Override
            public String getName() {
                return "Bank of America";
            }

            @Override
            public String getOrganization() {
                return "HAN";
            }

            @Override
            public URL getOFXURL() {
                try {
                    return new URL("https://ofx.bankofamerica.com/cgi-forte/ofx?servicename=ofx_2-3&pagename=bofa");
//                    return new URL("https://eftx.bankofamerica.com/eftxweb/access.ofx");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public String getBrokerId() {
                return null;
            }
        } ;

        FinancialInstitutionService service
                = new FinancialInstitutionServiceImpl();
        FinancialInstitution fi = service.getFinancialInstitution(data);
        // read the fi profile (note: not all institutions
// support this, and you normally don't need it.)
        FinancialInstitutionProfile profile = fi.readProfile();

//get a reference to a specific bank account at your FI
        BankAccountDetails bankAccountDetails
                = new BankAccountDetails();

//routing number to the bank.
        bankAccountDetails.setRoutingNumber("121000358");
//bank account number.
        bankAccountDetails.setAccountNumber("325036356050");
//it's a checking account
        bankAccountDetails.setAccountType(AccountType.CHECKING);

        BankAccount bankAccount
                = fi.loadBankAccount(bankAccountDetails, "nasir@rasul.ca", "PeSt4044");
        log.info(bankAccount.toString());
    }
}
