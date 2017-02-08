package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class STMTRS {

    @XmlElement(name = "BANKTRANLIST")
    private BankTransactionList transactions;

    @XmlElement(name = "BANKACCTFROM")
    private Bank bank;

    public BankTransactionList getTransactions() {
        return transactions;
    }

    public STMTRS setTransactions(final BankTransactionList transactions) {
        this.transactions = transactions;
        return this;
    }

    public void addBankTransaction(final BankTransaction transaction){
        if (this.transactions == null){
            this.transactions = new BankTransactionList();
        }
        this.transactions.getBankTransactions().add(transaction);
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(final Bank bank) {
        this.bank = bank;
    }
}
