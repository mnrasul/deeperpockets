package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Bank {

    @XmlElement(name = "BANKID")
    private String bankId;
    @XmlElement(name = "ACCTID")
    private String accountId;
    @XmlElement(name = "ACCTTYPE")
    private String accountType;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(final String bankId) {
        this.bankId = bankId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }
}
