package ca.rasul.jpa;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@Builder
public class Transaction {
//    @Transient
//    private final NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
//
//    @Transient
//    private final static SimpleDateFormat dateformat = new SimpleDateFormat("yyyymmddhhmmss");
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

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String category, final String merchant, final String memo, final String type, final Long accountNumber) {
        this.id = id;
        this.datePosted = datePosted;
        this.name = name;
        this.memo = memo;
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.merchant = merchant;
        this.category = category;
//        format.setGroupingUsed(true);
    }

    public Transaction(final String id, final BigDecimal amount, final Date datePosted, final String name, final String memo, final String type, final Long accountNumber) {
        this(id, amount, datePosted, name, null, null, memo, type, accountNumber);
    }
}
