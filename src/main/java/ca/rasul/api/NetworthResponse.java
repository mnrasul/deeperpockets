package ca.rasul.api;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Data
@AllArgsConstructor
public class NetworthResponse {
    private final String name;
    private final Double balance;
    private final AccountType type;
}
