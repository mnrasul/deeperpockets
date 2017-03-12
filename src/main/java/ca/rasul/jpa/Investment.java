package ca.rasul.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "investments")
public class Investment {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    @EmbeddedId
    private InvestmentsPrimaryKey id;
    private String type;
    @Column(precision = 1000,  scale = 5)
    private BigDecimal units;
    @Column(precision = 1000,  scale = 10)
    private BigDecimal unitPrice;
    @Column(precision = 1000,  scale = 10)
    private BigDecimal investmentReturn;
    @Column(precision = 10,  scale = 4)
    private BigDecimal investmentPercentage;
    @Column(precision = 1000,  scale = 10)
    private BigDecimal marketValue;
    private Date marketValueDate;

    public Investment(){

    }

    public Investment(final String id, final String type, final BigDecimal units, final BigDecimal unitPrice, final BigDecimal marketValue, final Date marketValueDate, final Long accountId) {
        this.id = new InvestmentsPrimaryKey(id, accountId);
        this.type = type;
        this.units = units;
        this.unitPrice = unitPrice;
        this.marketValue = marketValue;
        this.marketValueDate = marketValueDate;
    }

    public InvestmentsPrimaryKey getId() {
        return id;
    }

    public void setId(final InvestmentsPrimaryKey id) {
        this.id = id;
    }

    public Long getAccountId(){
        return getId().getAccountId();
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public void setUnits(final BigDecimal units) {
        this.units = units;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(final BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(final BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public Date getMarketValueDate() {
        return marketValueDate;
    }

    public void setMarketValueDate(final Date marketValueDate) {
        this.marketValueDate = marketValueDate;
    }

    public BigDecimal getInvestmentReturn(){
        return getMarketValue().subtract(getUnits().multiply(getUnitPrice()));
    }

    public void setInvestmentReturn(final BigDecimal investmentReturn) {
        this.investmentReturn = investmentReturn;
    }

    public BigDecimal getInvestmentPercentage() {
        return getInvestmentReturn().multiply(ONE_HUNDRED).divide(getUnits().multiply(getUnitPrice()), BigDecimal.ROUND_HALF_UP);
    }

    public void setInvestmentPercentage(final BigDecimal investmentPercentage) {
        this.investmentPercentage = investmentPercentage;
    }
}
