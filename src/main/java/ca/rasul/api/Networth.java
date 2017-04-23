package ca.rasul.api;

import ca.rasul.jpa.AccountRepository;
import ca.rasul.jpa.InvestmentRepository;
import ca.rasul.jpa.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.text.NumberFormat;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */

@Component
@Path("/networth")
public class Networth {

    @Autowired private NumberFormat currencyFormatter;
    @Autowired private InvestmentRepository investmentRepository;

    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository  transactionRepository;

    @GET
    public String computeNetworth(){
        return  currencyFormatter.format(transactionRepository.findNetworthOfAssets().add(investmentRepository.findNetworthOfInvestments()));
    }

    @GET
    @Path("loans")
    public String loans(){
        return "9";
    }
}
