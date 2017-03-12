package ca.rasul.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public interface InvestmentRepository extends CrudRepository<Investment, InvestmentsPrimaryKey> {
//    @Query(value = "SELECT * FROM investments WHERE id = :id account_id = :accountId", nativeQuery = true)
//    Investment findByIdAndAccountId(@Param("id") String id, @Param("accountId") Long accountId);

    @Query(value = "SELECT sum(market_value) FROM investments i", nativeQuery = true)
    BigDecimal findNetworthOfInvestments();

}
