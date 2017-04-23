package ca.rasul.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Slf4j
@AllArgsConstructor
@Builder
public class Loan {
    @NonNull
    @Getter
    private final String accountNumber;

    @NonNull
    @Getter
    private final String bankId;

    @NonNull
    @Getter
    private final double originalPrincipal;

    @NonNull
    @Getter
    private final double interestRate;

    @NonNull
    @Getter
    private final int term;

    @NonNull
    @Getter
    private final LoanDate dateOfFirstPayment;


}
