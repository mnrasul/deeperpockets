package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OFXBody {
    @XmlElement(name = "STMTTRNRS")
    private STMTTRNRS stmttrnrs;

    public STMTTRNRS getStmttrnrs() {
        return stmttrnrs;
    }

    public OFXBody setStmttrnrs(final STMTTRNRS stmttrnrs) {
        this.stmttrnrs = stmttrnrs;
        return this;
    }
}