package ca.rasul.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public interface TransactionRepository extends CrudRepository<Transaction, String> {
    @Query(value = "SELECT sum(amount) FROM transactions WHERE account_id = :accountId", nativeQuery = true)
    BigDecimal findNetworthOfAccount(@Param("accountId") Long accountId);

    List<Transaction> findByAccountId(Long accountId);
}
