package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class STMTTRNRS {
    @XmlElement(name = "STMTRS")
    private STMTRS stmtrs;

    public STMTRS getStmtrs() {
        return stmtrs;
    }

    public void setStmtrs(final STMTRS stmtrs) {
        this.stmtrs = stmtrs;
    }
}
