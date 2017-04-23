package ca.rasul.jpa;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "positions")
@Data
@Builder(builderMethodName = "build")
public class Position {
    @Id
    private final long id;
    private final String account;
    private final String ticker;
    private final Date purchaseDate;
    private final BigDecimal purchasePrice;
    private final int quantity;
    private final BigDecimal costBasis;

    public static Position.PositionBuilder builder() {
        return build(); // Replace Builder constructor with _builder()
    }

}
