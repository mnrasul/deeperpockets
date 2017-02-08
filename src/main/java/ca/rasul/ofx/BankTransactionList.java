package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BankTransactionList {

    @XmlElement(name = "STMTTRN")
    List<BankTransaction> bankTransactions;

    public List<BankTransaction> getBankTransactions() {
        return bankTransactions;
    }

    public BankTransactionList setBankTransactions(final List<BankTransaction> bankTransactions) {
        this.bankTransactions = bankTransactions;
        return this;
    }
}
