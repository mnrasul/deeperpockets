package ca.rasul.jpa;

import javax.persistence.*;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator = "accounts_id_seq")
    @SequenceGenerator(name="accounts_id_seq",sequenceName="accounts_id_seq")
    private Long id;

    private String accountId;
    private String bankId;
    private String accountType;
    private String institution;
    private String currency;

    public Account(){

    }
    public Account(final String accountId, final String bankId, final String accountType, final String institution, final String currency) {
        this.accountId = accountId;
        this.bankId = bankId;
        this.accountType = accountType;
        this.institution = institution;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(final String bankId) {
        this.bankId = bankId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(final String institution) {
        this.institution = institution;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }
}
