package ca.rasul;

import lombok.*;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@AllArgsConstructor
@Value
@Builder
@Getter
@Setter
public class BankIdentification {
    private final String issuer;
    private final String bankName;
    private final String accountType;
    private final String currency;

    public static BankIdentification createFromCSV(String csv){
        String[] split = csv.split(",");
        if (split.length != 4){
            throw new IllegalArgumentException();
        }
        return BankIdentification.builder().issuer(split[0])
                .bankName(split[1])
                .accountType(split[2])
                .currency(split[3])
                .build();
    }
}
