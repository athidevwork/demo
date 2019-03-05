package dti.pm.soapmgr;

import dti.oasis.recordset.Record;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPElement;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import dti.oasis.app.AppException;
import dti.oasis.util.LogUtils;

/**
 * This class provides base web service access functionality.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 31, 2011
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public abstract class BaseWebService {

    /**
     * Must return Web Service Name in concrete subclasses
     *
     * @return Web Service Name
     */
    public abstract String getServiceName();

    /**
     * Base constructor, creates Transformer
     *
     * @throws javax.xml.transform.TransformerConfigurationException
     *
     */
    public BaseWebService() {
        try {
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        }catch (TransformerConfigurationException exception) {
            throw new AppException(AppException.UNEXPECTED_ERROR, "Failed to create Transformer for Web Services.");
        }
    }

    /**
     * Base Web Service initialization method
     *
     */
    public void initializeService() {
        if (!serviceInitialized) {
            try {
                connectionFactory = SOAPConnectionFactory.newInstance();
                connection = connectionFactory.createConnection();
                messageFactory = MessageFactory.newInstance();
                serviceInitialized = true;
            }
            catch (Exception e) {
                throw new AppException(AppException.UNEXPECTED_ERROR, "Failed to initialize Web Services.");
            }
        }
    }

    /**
     * Base Web Service finalization method
     *
     */
    public void finalizeService() {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SOAPException ignore) {
            }
        }
    }

    /**
     * Method to configure a Web Service.
     * @param parameters
     */
    public void configureService(BaseSoapParameters parameters) {
    }

    /**
     * Method to get the Web Service Parameters.
     * @return
     */
    public BaseSoapParameters getWebServiceParameters() {
        return baseSoapParameters;
    }

    /**
     * Method to run the Web Service.
     * @param inputRecord
     * @return
     */
    public Record runWebService(Record inputRecord) {
        return new Record();
    }

    /**
     * Base Web Service invocation method
     *
     * @param baseSoapParameters SOAP Parameters
     * @return ArrayList of Case objects
     * @throws SOAPException
     * @throws TransformerException
     */
    public Document invoke(BaseSoapParameters baseSoapParameters ) throws SOAPException, TransformerException {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "invoke", new Object[]{baseSoapParameters});
        }

        // Create SOAPMessage, SOAPPart, SOAPEnvelope & SOAPBody
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart sp = message.getSOAPPart();
        SOAPEnvelope envelope = sp.getEnvelope();
        SOAPBody body = envelope.getBody();

        // Set message headers describing web service URL & content type
        message.getMimeHeaders().setHeader("SOAPAction", baseSoapParameters.webMethodPrefix + getServiceName());
        message.getMimeHeaders().setHeader("Content-Type", "text/xml");

        // Construct SOAPBodyElement for Web Service itself
        Name bodyName = envelope.createName(getServiceName(), "", baseSoapParameters.webMethodPrefix);
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);

        // Set the XML input structure and values
        setInputParameters(baseSoapParameters, bodyElement, envelope);

        // Persist the changes to the SOAPMessage
        message.saveChanges();

        if (l.isLoggable(Level.FINEST)) {
            try {
                l.logp(Level.FINEST, getClass().getName(), "invoke", "\nSending:\n" + message + "\n");
                //message.writeTo(System.out);
            }
            catch (Exception e) {
            }
        }

        // Call the Web Service
        String endPoint = baseSoapParameters.getWebServiceUrl();
        SOAPMessage reply = connection.call(message, endPoint);

        if (l.isLoggable(Level.FINEST)) {
            try {
                l.logp(Level.FINEST, getClass().getName(), "invoke", "\nReceived:\n" + reply + "\n");
                //reply.writeTo(System.out);
            }
            catch (Exception e) {
            }
        }

        // Get Reply and transform to DOM object
        Source src = reply.getSOAPPart().getContent();
        DOMResult domResult = new DOMResult();
        transformer.transform(src, domResult);
        Document document = (Document) domResult.getNode();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "invoke", document);
        }

        return document;

    }

    /**
     * Method to return value of element.
     *
     * @param element Parent element
     * @param tag     Name of element to find
     * @return value(text) of element
     */
    protected String getValue(Element element, String tag) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getValue", new Object[]{element, tag});
        }

        String value = getText(element.getElementsByTagName(tag));
        if(value != null && value.trim().length() == 0) {
            value = null;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "invoke", value);
        }

        return value;
    }

    /**
     * Method to recursively build the text of a node list's underlying text elements
     * @param nodeList
     * @return
     */
    protected String getText(NodeList nodeList) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getText", new Object[]{nodeList});
        }

        int sz = nodeList.getLength();
        String text = "";
        for (int i = 0; i < sz; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE)
                text += node.getNodeValue();
            else if (node.getNodeType() == Node.ELEMENT_NODE)
                text += getText(node.getChildNodes());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getText", text);
        }

        return text;
    }

    /**
     * Returns a String XML built from a NodeList.
     * @param nodeList
     * @return
     */
    public String getStringXmlFromNodeList(NodeList nodeList) {
        StringBuffer xml = new StringBuffer();
        getStringXml(nodeList, xml);

        return xml.toString();
    }

    /**
     * Method to write the XML of a node.
     * @param nodeList
     */
    private void getStringXml(NodeList nodeList, StringBuffer xml) {
        int sz = nodeList.getLength();
        for (int i = 0; i < sz; i++) {
            org.w3c.dom.Node n = nodeList.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                xml.append(n.getNodeValue()).append("\n");
            }
            else if (n.getNodeType() == Node.ELEMENT_NODE) {
                xml.append('<' + n.getNodeName() + '>').append("\n");
                NodeList nl2 = n.getChildNodes();
                getStringXml(nl2, xml);
                xml.append("</" + n.getNodeName() + '>').append("\n");
            }
        }
    }

    /**
     * Returns a String XML built from a Node.
     * @param node
     * @return
     */
    public String getStringXmlFromNode(Node node) {
        StringBuffer xml = new StringBuffer();
        getStringXml(node, "", xml);

        return xml.toString();
    }

    /**
     *  Method to write the XML of a node.
     * @param node
     * @param indent
     * @param xml
     */
    private void getStringXml(Node node, String indent, StringBuffer xml) {
        // The output depends on the type of the node
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE: {
            Document doc = (Document) node;
            xml.append(indent + "<?xml version='1.0'?>\n");
            Node child = doc.getFirstChild();
            while (child != null) {
                getStringXml(child, indent, xml);
                child = child.getNextSibling();
            }
            break;
        }
        case Node.DOCUMENT_TYPE_NODE: {
            DocumentType documentType = (DocumentType) node;
            xml.append("<!DOCTYPE " + documentType.getName() + ">\n");
            break;
        }
        case Node.ELEMENT_NODE: {
            Element elt = (Element) node;
            xml.append(indent + "<" + elt.getTagName());
            NamedNodeMap attributes = elt.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node a = attributes.item(i);
                xml.append(" " + a.getNodeName() + "='" + fixUp(a.getNodeValue()) + "'\n");
            }
            xml.append(">\n");

            String newIndent = indent + "   ";
            Node child = elt.getFirstChild();
            while (child != null) {
                getStringXml(child, newIndent, xml);
                child = child.getNextSibling();
            }

            xml.append(indent + "</" + elt.getTagName() + ">\n");
            break;
        }
        case Node.TEXT_NODE: {
            Text textNode = (Text) node;
            String text = textNode.getData().trim();
            if ((text != null) && text.length() > 0)
                xml.append(indent + fixUp(text)).append("\n");
            break;
        }
        case Node.PROCESSING_INSTRUCTION_NODE: {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            xml.append(indent + "<?" + pi.getTarget() + " " + pi.getData() + "?>\n");
            break;
        }
        case Node.ENTITY_REFERENCE_NODE: {
            xml.append(indent + "&" + node.getNodeName() + ";\n");
            break;
        }
        case Node.CDATA_SECTION_NODE: {
            CDATASection cdata = (CDATASection) node;
            xml.append(indent + "<" + "![CDATA[" + cdata.getData() + "]]" + ">\n");
            break;
        }
        case Node.COMMENT_NODE: {
            Comment c = (Comment) node;
            xml.append(indent + "<!--" + c.getData() + "-->\n");
            break;
        }
        default:
            xml.append("Ignoring node: " + node.getClass().getName()).append("\n");
            break;
        }
    }

    /**
     * Method to handle special characters for an XML string.
     * @param s
     * @return
     */
    String fixUp(String s) {
        StringBuffer sb = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                default:
                    sb.append(c);
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
            }
        }
        return sb.toString();
    }
    
    /**
     * Method to add parameters to SOAPMessage.
     *
     * @param baseSoapParameters    BaseSoapParameters object
     * @param bodyElement           Current SOAPBodyElement
     * @param envelope Current      SOAPEnvelope
     * @throws SOAPException
     */
    protected void setInputParameters(BaseSoapParameters baseSoapParameters, SOAPBodyElement bodyElement, SOAPEnvelope envelope)
            throws SOAPException {
        baseSoapParameters.setInputParameters(bodyElement, envelope);
    }

    protected void setInputParameters(BaseSoapParameters baseSoapParameters, SOAPElement bodyElement, SOAPEnvelope envelope)
            throws SOAPException {
        baseSoapParameters.setInputParameters(bodyElement, envelope);
    }

    protected Record processDocument(Document doc)
            throws SOAPException, TransformerException {
        return new Record();
    }

    protected boolean serviceInitialized;
    protected TransformerFactory transformerFactory;
    protected Transformer transformer;

    protected BaseSoapParameters baseSoapParameters;
    protected SOAPConnectionFactory connectionFactory;
    protected SOAPConnection connection;
    protected MessageFactory messageFactory;
}
