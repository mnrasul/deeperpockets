package ca.rasul.jpa;


import org.junit.Test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
public class TransactionTest {


    @Test
    public void testCurrency() throws ParseException {
        NumberFormat numberFormat =  NumberFormat.getNumberInstance(Locale.US);
        Number parse = numberFormat.parse("100,00");

        System.out.println(parse.intValue());
    }
}