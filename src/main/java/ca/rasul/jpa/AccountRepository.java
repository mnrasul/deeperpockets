package ca.rasul.jpa;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByAccountIdAndBankId(String accountId, String bankId);


}
