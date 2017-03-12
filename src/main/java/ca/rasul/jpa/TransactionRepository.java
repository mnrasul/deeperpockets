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

    @Query(value = "SELECT sum(amount) FROM transactions t", nativeQuery = true)
//    @Query(value = "SELECT sum(amount) FROM transactions t JOIN accounts a ON t.account_id = a.id WHERE a.account_type not in ('CREDITLINE')", nativeQuery = true)
    BigDecimal findNetworthOfAssets();

    List<Transaction> findByAccountId(Long accountId);
}
