package ca.rasul.jpa;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public interface InvestmentRepository extends CrudRepository<Investment, String> {
//    @Query(value = "SELECT * FROM investments WHERE id = :id account_id = :accountId", nativeQuery = true)
//    @Param("id") String id, @Param("accountId") Long accountId
    Investment findByIdAndAccountId(String id, Long accountId);

}
