package ca.rasul.jpa;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Data
@Getter
@Setter
@NoArgsConstructor
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
    private Long accountNumber;

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String category, final String merchant, final String memo, final String type, final Long accountNumber) throws ParseException {
        this.id = id;
//        this.amount = new BigDecimal(format.parse(amount).doubleValue());
        this.datePosted = datePosted;
        this.name = name;
        this.memo = memo;
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.merchant = merchant;
        this.category = category;
        format.setGroupingUsed(true);
    }

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String memo, final String type, final Long accountNumber) throws ParseException {
        this(id, amount, datePosted, name, null, null, memo, type, accountNumber);
    }

//    public void setAmount(final BigDecimal amount) {
//        this.amount = amount;
//    }
//    public void setAmount(final String amount) throws ParseException {
//        this.amount = (BigDecimal)format.parse(amount);
//    }
}
