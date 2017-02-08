package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FinancialInsitution {
    @XmlElement(name = "ORG")
    private String bank;

    @XmlElement(name = "FID")
    private String bankId;

    public String getBank() {
        return bank;
    }

    public FinancialInsitution setBank(final String bank) {
        this.bank = bank;
        return this;
    }

    public String getBankId() {
        return bankId;
    }

    public FinancialInsitution setBankId(final String bankId) {
        this.bankId = bankId;
        return this;
    }
}
