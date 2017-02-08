package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OFXHeader {

    @XmlElement(name = "SONRS")
    private SONRS sonrs;

    public SONRS getSonrs() {
        return sonrs;
    }

    public OFXHeader setSonrs(final SONRS sonrs) {
        this.sonrs = sonrs;
        return this;
    }
}
