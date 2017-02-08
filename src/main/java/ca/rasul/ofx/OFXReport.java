package ca.rasul.ofx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OFX")
public class OFXReport {

    @XmlElement(name = "SIGNONMSGSRSV1")
    OFXHeader header;

    @XmlElement(name = "BANKMSGSRSV1")
    OFXBody body;

    public OFXHeader getHeader() {
        return header;
    }

    public OFXReport setHeader(final OFXHeader header) {
        this.header = header;
        return this;
    }

    public OFXBody getBody() {
        return body;
    }

    public OFXReport setBody(final OFXBody body) {
        this.body = body;
        return this;
    }
}
