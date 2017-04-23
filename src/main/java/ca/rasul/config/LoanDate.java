package ca.rasul.config;

import java.time.LocalDate;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public class LoanDate {
    private final String localDate;
    private final int year;
    private final int month;
    private final int day;

    public LoanDate(String localDate){
        this.localDate = localDate;
        String[] split = localDate.split("-");
        year = Integer.parseInt(split[0]);
        month = Integer.parseInt(split[1]);
        day = Integer.parseInt(split[2]);
    }

    public LocalDate getLocalDate(){
        return  LocalDate.of(year, month, day);
    }
}
