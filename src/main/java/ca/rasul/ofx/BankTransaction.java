package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BankTransaction {

    @XmlElement(name = "TRNTYPE")
    private String transactionType;

    @XmlElement(name = "DTPOSTED")
    private String datePosted;

    @XmlElement(name = "TRNAMT")
    private String transactionAmount;

    @XmlElement(name = "FITID")
    private String transactionId;

    @XmlElement(name = "NAME")
    private String name;

    @XmlElement(name = "MEMO")
    private String memo;

    public String getTransactionType() {
        return transactionType;
    }

    public BankTransaction setTransactionType(final String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public BankTransaction setDatePosted(final String datePosted) {
        this.datePosted = datePosted;
        return this;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public BankTransaction setTransactionAmount(final String transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BankTransaction setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public String getName() {
        return name;
    }

    public BankTransaction setName(final String name) {
        this.name = name;
        return this;
    }

    public String getMemo() {
        return memo;
    }

    public BankTransaction setMemo(final String memo) {
        this.memo = memo;
        return this;
    }

    @Override
    public String toString() {
        return "BankTransaction{" +
                "transactionType='" + transactionType + '\'' +
                ", datePosted='" + datePosted + '\'' +
                ", transactionAmount='" + transactionAmount + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", name='" + name + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
