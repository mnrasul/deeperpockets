package ca.rasul.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyymmddhhmmss");
    @Id
    private String id;

    private BigDecimal amount;
    private Date datePosted;
    private String name;
    private String memo;
    private String type;
    private Long accountId;

    public Transaction(){

    }
    public Transaction(final String id, final String amount, final Date datePosted, final String name, final String memo, final String type, final Long accountId) throws ParseException {
        this.id = id;
        this.amount = new BigDecimal(amount);
        this.datePosted = datePosted;
        this.name = name;
        this.memo = memo;
        this.type = type;
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(final Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(final String memo) {
        this.memo = memo;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }
}
