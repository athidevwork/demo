package dti.oasis.security;

import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.IXMLGridHeaderLoader;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enrollment Functionality
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 8, 2004
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 3/30/2005        jbe         Add required attribute
 * 01/23/2007       wer         Changed usage of Logger.getLogger() to LogUtils.getLogger()
 * 02/12/2007       wer         Support loading resources from Classpath if they cannot be found on disk.
 * ---------------------------------------------------
*/

public class EnrollHelper implements ErrorHandler {
    /**
     * Inner Class for capturing Enrolling fields
     */
    public static class Enroller {
        public String from;
        public String where;
        public String value;
        public Enroller(String from, String where, String value) {
            this.from = from;
            this.where = where;
            this.value = value;
        }
    }

    /**
     * Returns a HashMap, one entry per "FROM/TABLE", the key is the table, the entry
     * is an ArrayList of Enroller objects
     * @param fieldsMap Map of field id/value
     * @param queryMap Map of field id/String array of FROM/WHERE
     * @return HashMap
     */
    public HashMap getEnrollByFrom(Map fieldsMap, Map queryMap) {
        Logger l = LogUtils.enterLog(getClass(),"getEnrollByFrom", new Object[] {fieldsMap,queryMap});
        HashMap byFrom = new HashMap(5);
        Iterator it = queryMap.keySet().iterator();
        while(it.hasNext()) {
            String fieldId = (String) it.next();
            String[] arry = (String[]) queryMap.get(fieldId);
            String from = arry[0];
            String where = arry[1];
            if(byFrom.containsKey(from)) {
                ArrayList list = (ArrayList) byFrom.get(from);
                list.add(new Enroller(from,where,(String)fieldsMap.get(fieldId)));
            }
            else {
                ArrayList list = new ArrayList(5);
                list.add(new Enroller(from,where,(String) fieldsMap.get(fieldId)));
                byFrom.put(from,list);
            }
        }
        l.exiting(getClass().getName(),"getEnrollByFrom",byFrom);
        return byFrom;
    }
    /**
     * Returns a HashMap where the key is the fieldId and the entry is a String array of
     * FROM & WHERE
     *
     * @param request Current request
     * @param xmlFile xml file
     * @param schema  xsd
     * @return HashMap
     * @throws Exception
     */
    public HashMap getEnrollLookup(HttpServletRequest request, String xmlFile, String schema) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getEnrollLookup", new Object[]{request, xmlFile, schema});
        NodeList nl = getFieldNodes(request, xmlFile, schema);
        HashMap map = new HashMap(5);
        int sz = nl.getLength();

        //loop through fields, adding each to Map
        for (int i = 0; i < sz; i++) {
            Node n = nl.item(i);
            String fieldId = XMLUtils.getChildElementValues(n, "fieldId");
            String from = XMLUtils.getChildElementValues(n, "from");
            String where = XMLUtils.getChildElementValues(n, "where");
            map.put(fieldId, new String[]{from, where});
        }
        l.exiting(getClass().getName(), "getEnrollLookup", map);
        return map;
    }

    /**
     * Parses the fields xml and returns a NodeList
     *
     * @param request current request
     * @param xmlFile xml file
     * @param schema  xsd
     * @return NodeList of fields
     * @throws Exception
     */
    protected NodeList getFieldNodes(HttpServletRequest request, String xmlFile, String schema) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getFieldNodes",
                new Object[]{request, xmlFile, schema});

        InputStream fis = null;
        Document menu = null;
        try {
            // open file
            fis = ActionHelper.getResourceAsInputStream(request.getSession().getServletContext(), xmlFile);
            InputStream xsdInputStream = ActionHelper.getResourceAsInputStream(schema);
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            // set to validate against schema
            fac.setNamespaceAware(true);
            fac.setValidating(true);
            fac.setAttribute(IXMLGridHeaderLoader.JAXP_SCHEMA_LANGUAGE, IXMLGridHeaderLoader.W3C_XML_SCHEMA);
            fac.setAttribute(IXMLGridHeaderLoader.JAXP_SCHEMA_SOURCE, xsdInputStream);
            // get DocumentBuilder and set up Error Handling for schema validation
            DocumentBuilder docbuilder = fac.newDocumentBuilder();
            docbuilder.setErrorHandler(this);
            // create DOM Document
            try {
                menu = docbuilder.parse(fis);
            } catch (SAXException se) {
                // schema validation failed
                throw new Exception("Error parsing enrollment file " + xmlFile + ".  " + se.getMessage());
            }
            // close file
            fis.close();
            // get field element
            NodeList nl = menu.getElementsByTagName("field");
            l.exiting(getClass().getName(), "getFieldNodes", nl);
            return nl;
        } finally {
            // close file if it is open
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Returns a LinkedHashMap of OasisFormField objects from an xml file of enrollment fields.
     * @param request Current request
     * @param xmlFile xml file
     * @param schema xsd
     * @return LinkedHashMap of OasisFormField objects, key is fieldId
     * @throws Exception
     */
    public LinkedHashMap getFields(HttpServletRequest request, String xmlFile, String schema) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getFields",
                new Object[]{request, xmlFile, schema});
        LinkedHashMap map = new LinkedHashMap(5);

        NodeList nl = getFieldNodes(request, xmlFile, schema);
        int sz = nl.getLength();

        //loop through fields, adding each to Map
        for (int i = 0; i < sz; i++) {
            Node n = nl.item(i);
            String fieldId = XMLUtils.getChildElementValues(n, "fieldId");
            String label = XMLUtils.getChildElementValues(n, "label");
            String maxLength = XMLUtils.getChildElementValues(n, "maxlength");
            String required = XMLUtils.getChildElementValues(n, "required");
            boolean isRequired = (required!=null && (required.equalsIgnoreCase("Y") ||
                    required.equalsIgnoreCase("YES") || required.equalsIgnoreCase("TRUE")));
            OasisFormField field = new OasisFormField(fieldId, label, true, isRequired, null, null, false);
            field.setMaxLength(maxLength);
            map.put(fieldId, field);
        }
        l.exiting(getClass().getName(), "getFields", map);
        return map;
    }

      /**
     * Build useful error message
     * @param exception Some kind of SAX Parsing error/warning
     * @param level e.g. "Error", "Fatal Error", "Warning"
     * @return Error message.
     */
    protected String getSAXErrorMessage(SAXParseException exception, String level) {
        return "**Parsing "+level + "**"+
            "\nLine:\t" + exception.getLineNumber() +
            "\nURI:\t" + exception.getSystemId() +
            "\nMessage:\t"+exception.getMessage();
    }
    /**
     * Receive notification of a recoverable error.
     * @param exception Some kind of SAX parsing error.
     * @throws SAXException
     */
    public void error(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Error");
        LogUtils.getLogger(getClass()).warning(msg);
    }

    /**
     * Receive notification of a non-recoverable error.
     * @param exception  Some kind of fatal SAX Parsing error
     * @throws SAXException
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Fatal Error");
        LogUtils.getLogger(getClass()).severe(getSAXErrorMessage(exception, " Fatal Error"));
        throw new SAXException(msg);
    }

    /**
     * Receive notification of a warning.
     * @param exception Some kind of recoverable SAX Parsing warning
     * @throws SAXException
     */
    public void warning(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Error");
        LogUtils.getLogger(getClass()).warning(msg);
    }

}
