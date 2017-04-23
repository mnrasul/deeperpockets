package ca.rasul.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountId {
    @Getter
    private final String accountId;
    @Getter
    private final String bankId;
}
