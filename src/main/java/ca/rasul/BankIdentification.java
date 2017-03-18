package ca.rasul;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@AllArgsConstructor
@Value
@Getter
public class BankIdentification {
    private final String cardType;
    private final String bankName;
    private final String accountType;
    private final String locale;
}
