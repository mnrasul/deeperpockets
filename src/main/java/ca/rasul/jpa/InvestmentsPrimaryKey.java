package ca.rasul.jpa;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Embeddable
public class InvestmentsPrimaryKey implements Serializable {
    @Column(nullable = false)
    private String id;


    @Column(nullable = false)
    private Long accountId;

    public InvestmentsPrimaryKey() {
    }

    public InvestmentsPrimaryKey(final String id, final Long accountId) {
        this.id = id;
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }
}
