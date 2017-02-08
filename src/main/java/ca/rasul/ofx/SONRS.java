package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SONRS {
    @XmlElement(name = "FI")
    private FinancialInsitution financialInsitution;

    @XmlElement(name = "LANGUAGE")
    private String language;

    public FinancialInsitution getFinancialInsitution() {
        return financialInsitution;
    }

    public SONRS setFinancialInsitution(final FinancialInsitution financialInsitution) {
        this.financialInsitution = financialInsitution;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public SONRS setLanguage(final String language) {
        this.language = language;
        return this;
    }
}
