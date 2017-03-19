package ca.rasul;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public class BankIdentificationTest {

    @Test
    public void simpleTest(){
        BankIdentification bankIdentification = new BankIdentification("1","2","3","4");
        Assert.notNull(bankIdentification);
        Assert.notNull(bankIdentification.getAccountType());
        Assert.notNull(bankIdentification.getBankName());
        Assert.notNull(bankIdentification.getIssuer());
        Assert.notNull(bankIdentification.getCurrency());
    }
}