package ca.rasul.jpa;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public interface TransactionRepository extends CrudRepository<Transaction, String> {
}
