
package com.delphi_tech.ows.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.delphi_tech.ows.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _MessageStatus_QNAME = new QName("http://www.delphi-tech.com/ows/Common", "MessageStatus");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.delphi_tech.ows.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MessageStatusType }
     * 
     */
    public MessageStatusType createMessageStatusType() {
        return new MessageStatusType();
    }

    /**
     * Create an instance of {@link CustomStatusCodeType }
     * 
     */
    public CustomStatusCodeType createCustomStatusCodeType() {
        return new CustomStatusCodeType();
    }

    /**
     * Create an instance of {@link ExtendedStatusType }
     * 
     */
    public ExtendedStatusType createExtendedStatusType() {
        return new ExtendedStatusType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MessageStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.delphi-tech.com/ows/Common", name = "MessageStatus")
    public JAXBElement<MessageStatusType> createMessageStatus(MessageStatusType value) {
        return new JAXBElement<MessageStatusType>(_MessageStatus_QNAME, MessageStatusType.class, null, value);
    }

}
