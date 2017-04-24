package ca.rasul.api;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public enum AccountType {
    HOME_LOAN(Type.LIABILITY),
    AUTO_LOAN(Type.LIABILITY);


    private final Type type;

    AccountType(Type type){
        this.type = type;
    }


}

enum Type{
    ASSET,
    LIABILITY,
    REVENUE,
    EXPENSE,
    EQUITY
}
