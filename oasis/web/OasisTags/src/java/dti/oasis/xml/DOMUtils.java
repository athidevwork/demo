package dti.oasis.xml;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains a collection of functions to simplify and standardize use of DOM.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 10, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/11/2013       jxgu        Issue#147977 create DocumentBuilder every time
 * ---------------------------------------------------
 */
public class DOMUtils {

    public static Document parseXML(String xml, boolean normalize) {
        return parseXML(new StringReader(xml), normalize);
    }

    public static Document parseXML(InputStream xmlInputStream, boolean normalize) {
        return parseXML(new InputStreamReader(xmlInputStream), normalize);
    }

    public static Document parseXML(Reader xmlReader, boolean normalize) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DOMUtils.class.getName(), "parseXML", new Object[]{xmlReader, Boolean.valueOf(normalize)});
        }
        final DocumentBuilder docBuilder = getDocumentBuilder();

        // Parse the input xml source
        Document xmlDoc = null;
        try {
            xmlDoc = docBuilder.parse(new InputSource(xmlReader));

            if (normalize) {
                xmlDoc.normalize();
            }
        }
        catch (Exception e) {
            StringBuffer xml = new StringBuffer();
            try {
                LineNumberReader lr = new LineNumberReader(xmlReader);
                String line = null;
                while ((line = lr.readLine()) != null) {
                    xml.append(line);
                }
            } catch (IOException e1) {
                l.logp(Level.SEVERE, DOMUtils.class.getName(), "parseXML", "Failed to read the xml stream reader", e1);
            }

            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the XML: " + xml, e);
            l.throwing(DOMUtils.class.getName(), "parseXML", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DOMUtils.class.getName(), "parseXML", xmlDoc);
        }
        return xmlDoc;
    }

    public static String formatNode(Node node) {
        return formatNode(node, true, 2);
    }

    public static String formatNode(Node node, boolean indent) {
        return formatNode(node, indent, 2);
    }

    public static String formatNode(Node node, boolean indent, int indentAmount) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DOMUtils.class.getName(), "formatNode", new Object[]{node});
        }

        //  TODO: Switch to com.sun.org.apache.xml.internal.serialize.XMLSerializer
        // and com.sun.org.apache.xml.internal.serialize.OutputFormat from JDK 1.5 when running in WL 10
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final weblogic.apache.xml.serialize.OutputFormat outputFormat = new weblogic.apache.xml.serialize.OutputFormat();
        outputFormat.setIndenting(true);
        outputFormat.setEncoding("UTF-8");
        outputFormat.setIndent(indentAmount);
        final weblogic.apache.xml.serialize.XMLSerializer ser = new weblogic.apache.xml.serialize.XMLSerializer(bos, outputFormat);
        try {
            Element element = null;
            if (Node.DOCUMENT_NODE == node.getNodeType()){
                if (node.hasChildNodes()) {
                    ser.serialize((Element) ((Document) node).getDocumentElement());
                }
                else {
                    ser.serialize((Document)node);
                }
            }
            else {
                ser.serialize((Element)node);
            }
        }
        catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to format the node with XMLSerializer", e);
            l.throwing(DOMUtils.class.getName(), "formatNode", ae);
            throw ae;
        }
/*
        // Temporarily Use SerializerToXML
        Properties serializerProps = new Properties();
        if (indent) {
            serializerProps.setProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            serializerProps.setProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            serializerProps.setProperty(weblogic.apache.xalan.templates.OutputProperties.S_KEY_INDENT_AMOUNT, String.valueOf(indentAmount));
        }
        weblogic.apache.xalan.serialize.SerializerToXML ser = new weblogic.apache.xalan.serialize.SerializerToXML();
        ser.setOutputFormat(serializerProps);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ser.setOutputStream(bos);
        try {
            ser.serialize(node);
        }
        catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to format the node with SerializerToXML", e);
            l.throwing(getClass().getName(), "formatNode", ae);
            throw ae;
        }
*/
        String result = bos.toString();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DOMUtils.class.getName(), "formatNode", result);
        }
        return result;
    }

    public static String nodeToString(Node node) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(DOMUtils.class.getName(), "nodeToString", new Object[]{node});
        }

        String result = null;
        try {
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            Transformer idTransform = xformFactory.newTransformer();
            Source input = new DOMSource(node);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result output = new StreamResult(bos);
            idTransform.transform(input, output);
            result = bos.toString();
        } catch (TransformerException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to transform the input node", e);
            l.throwing(DOMUtils.class.getName(), "nodeToString", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(DOMUtils.class.getName(), "nodeToString", result);
        }
        return result;
    }

    public static Node getFirstChildElementNode(Node node) {
        Node firstChildElementNode = null;
        final NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node childNode = nl.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                firstChildElementNode = childNode;
                break;
            }
        }
        return firstChildElementNode;
    }

    public static String getNodeTextValue(Node node) {
        String value = null;
        if (node.hasChildNodes()) {
            value = node.getChildNodes().item(0).getNodeValue();
        }
        return value;
    }

    public static Node getNamespaceResolverNode(String namespacePrefix, String namespaceURI) {
        final DocumentBuilder docBuilder = DOMUtils.getDocumentBuilder();
        final Document tempDoc = docBuilder.newDocument();
        final Element resolverNode = tempDoc.createElementNS("", "spdaoContext");
        resolverNode.setAttribute("xmlns:" + namespacePrefix, namespaceURI);
        return resolverNode;
    }

    public static DocumentBuilder getDocumentBuilder() {
        l.entering(DOMUtils.class.getName(), "getDocumentBuilder");

        DocumentBuilder docBuilder = null;

        // Create an XML DocumentBuilder for each mapping since it is not thread safe.
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
        docBuilderFac.setNamespaceAware(true);
        docBuilderFac.setValidating(false);
        try {
            docBuilder = docBuilderFac.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to create a XML DocumentBuilder.", e);
            l.throwing(DOMUtils.class.getName(), "getDocumentBuilder", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DOMUtils.class.getName(), "getDocumentBuilder", docBuilder);
        }
        return docBuilder;
    }

    private static final Logger l = LogUtils.getLogger(DOMUtils.class);
}
