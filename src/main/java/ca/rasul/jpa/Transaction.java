package ca.rasul.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    @Transient
    private final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);

    private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyymmddhhmmss");
    @Id
    private String id;

    @Column(precision = 1000,  scale = 10)
    private BigDecimal amount;
    private Date datePosted;
    private String name;
    private String category;
    private String merchant;
    private String memo;
    private String type;
    private Long accountId;

    public Transaction(){

    }

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String category, final String merchant, final String memo, final String type, final Long accountId) throws ParseException {
        this.id = id;
//        this.amount = new BigDecimal(format.parse(amount).doubleValue());
        this.datePosted = datePosted;
        this.name = name;
        this.memo = memo;
        this.type = type;
        this.accountId = accountId;
        this.amount = amount;
        this.merchant = merchant;
        this.category = category;
        format.setGroupingUsed(true);
    }

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String memo, final String type, final Long accountId) throws ParseException {
        this(id, amount, datePosted, name, null, null, memo, type, accountId);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
    public void setAmount(final String amount) throws ParseException {
        this.amount = (BigDecimal)format.parse(amount);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getMerchant() {
        return merchant;
    }

//    public void setMerchant(final String merchant) {
//        this.merchant = merchant;
//    }
}
