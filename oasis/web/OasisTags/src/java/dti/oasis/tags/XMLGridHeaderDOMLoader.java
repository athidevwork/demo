package dti.oasis.tags;

import dti.oasis.obr.RequestHelper;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.XMLUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;

import dti.oasis.app.ConfigurationException;
import org.apache.struts.util.LabelValueBean;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Loads an XMLGridHeader object from an XML Document
 * using the DOM model.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 * @see dti.oasis.tags.XMLGridHeader
 *
 *
 *      Date:   Aug 1, 2003
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 1/8/2004 jbe     Added new constructors w/ String path instead
*                  of servletContext parms.  new init method too.
* 2/8/2004 jbe     Added Logging & toString
* 3/2/2004 jbe     Moved getAttributeValue out to XMLUtils
* 5/28/2004 jbe    Add textarea (rows/cols) handling.
* 10/7/2004 jbe    Support CN_DECIMALPLACES
* 2/3/2005  jbe    Add some diagnostic information to addHeader method.
* 4/6/2005  jbe    Revise to support XMLGridHeader refactoring.
* 5/19/2005	jbe	   Add updateonlyurl
* 1/17/2005	sxm	   Override href url with field default value if it exists
* 01/23/2007 lmm   Added support for column width;
* 01/23/2007 wer   Added support for displaying a readonly codelookup as label;
*                  Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 01/31/2007 wer   Enhanced to support defining Grid Column Order by the Grid Header
* 03/03/2008 James Issue#79614 eClaims architectural enhancement to 
*                  take advantage of ePolicy architecture
*                  Handling HREF is a new enhancement in WebWB
* 11/27/2009 kenney enh to support phone format
* ---------------------------------------------------
*/

public class XMLGridHeaderDOMLoader implements IXMLGridHeaderLoader, ErrorHandler, Serializable {
    protected HashMap xmlTypeMap;
    protected HashMap xmlDisplayTypeMap;

    protected String[] xmlTypes = {"default", "text", "formatdate", "formatdatetime",
                                   "number", "dropdown", "url", "updateonly", "date", "checkbox",
                                   "checkboxread", "radiobutton", "img", "anchor",
                                   "formatmoney", "textarea", "updateonlydate",
                                   "updateonlydatetime", "updateonlymoney", "updateonlynumber", "updateonlyurl", "updateonlydropdown",
                                   "uppercasetext","lowercasetext","updateonlypercentage","updateonlyphone","phone"};
    protected String[] xmlDisplayTypes = {"default", "money"};

    protected XMLGridHeader header;
    protected String realPath;
    private ServletContext servletContext;


    /**
     * Initializes some HashMaps used when loading
     * the header data from an xml file.
     *
     * @param servletContext
     */

    protected void init(ServletContext servletContext) {
        l.entering(getClass().getName(), "init");

        this.servletContext = servletContext;

        String basePath = "";
        try {
            basePath = ActionHelper.getRealPath(servletContext);
        } catch (ConfigurationException e) {
            // The application must not be deployed exploded. Default the basePath to ""
            basePath = "";
        }
        init(basePath);

        l.exiting(getClass().getName(), "init");
    }

    /**
     * Initializes some HashMaps used when loading
     * the header data from an xml file.
     *
     * @param path
     */

    protected void init(String path) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "init", new Object[]{path});
        }
        realPath = path;
        xmlTypeMap = new HashMap(xmlTypes.length);
        xmlDisplayTypeMap = new HashMap(xmlDisplayTypes.length);

        for (int i = 0; i < xmlTypes.length; i++)
            xmlTypeMap.put(xmlTypes[i], new Integer(i));

        for (int i = 0; i < xmlDisplayTypes.length; i++)
            xmlDisplayTypeMap.put(xmlDisplayTypes[i], new Integer(i));
        l.exiting(getClass().getName(), "init");
    }

    /**
     * Protected noarg constructor - do not use
     */
    protected XMLGridHeaderDOMLoader() {
    }

    /**
     * Constructor
     *
     * @param servletContext
     */
    public XMLGridHeaderDOMLoader(ServletContext servletContext) {
        l.entering(getClass().getName(), "XMLGridHeaderDOMLoader");
        header = new XMLGridHeader();
        init(servletContext);
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor with existing XMLGridHeader object
     *
     * @param header
     * @param servletContext
     */
    public XMLGridHeaderDOMLoader(XMLGridHeader header, ServletContext servletContext) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "XMLGridHeaderDOMLoader", new Object[]{header});
        }
        this.header = header;
        init(servletContext);
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor
     *
     * @param path
     */
    public XMLGridHeaderDOMLoader(String path) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "XMLGridHeaderDOMLoader", new Object[]{path});
        }
        header = new XMLGridHeader();
        init(path);
        l.exiting(getClass().getName(), "constructor", this);
    }

    /**
     * Constructor with existing XMLGridHeader object
     *
     * @param header
     * @param path
     */
    public XMLGridHeaderDOMLoader(XMLGridHeader header, String path) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "XMLGridHeaderDOMLoader", new Object[]{header, path});
        }
        this.header = header;
        init(path);
        l.exiting(getClass().getName(), "consructor", this);
    }

    /**
     * Return XMLGridHeader object
     *
     * @return XMLGridHeader
     */
    public XMLGridHeader getHeader() {
        return header;
    }

    /**
     * Set XMLGridHeader object
     *
     * @param header
     */
    public void setHeader(XMLGridHeader header) {
        this.header = header;
    }

    /**
     * Loads XMLGridHeader from XML Document found in headerFileName
     * XML file must meet XMLHeader.xsd schema
     *
     * @param headerFileName name of XML file
     * @param fields         OasisFields object as HashMap. Used if an entry in the XML
     *                       file contains a fieldname element
     * @param conn           JDBC Connection used in case the XML file contains a listsql element.
     * @throws Exception
     */
    public void load(String headerFileName, HashMap fields, Connection conn) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "load", new Object[]{headerFileName, fields, conn});
        }
        InputStream fis = null;
        InputStream xsdInputStream = null;
        Document menu = null;
        try {
            header.setFields(fields);
            if (headerFileName != null) {
                // open file
                try {
                    fis = new FileInputStream(realPath + headerFileName);
                } catch (FileNotFoundException e) {
                    // Could not find the header file in the Filesystem; try the classpath
                    l.logp(Level.FINE, getClass().getName(), "load", "Failed to find the header file " + realPath + headerFileName + " in the file system. Looking in the Web Application's classpath.");
                    fis = ActionHelper.getResourceAsInputStream(headerFileName);
                }
                try {
                    xsdInputStream = new FileInputStream(new File(realPath + schemaSource));
                } catch (FileNotFoundException e) {
                    // Could not find the xsd file in the Filesystem; try the classpath
                    l.logp(Level.FINE, getClass().getName(), "load", "Failed to find the xsd file " + realPath + schemaSource + " in the file system. Looking in the Web Application's classpath.");
                    xsdInputStream = ActionHelper.getResourceAsInputStream(schemaSource);
                }
                DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
                // set to validate against schema
                fac.setNamespaceAware(true);
                fac.setValidating(true);
                fac.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                fac.setAttribute(JAXP_SCHEMA_SOURCE, xsdInputStream);
                // get DocumentBuilder and set up Error Handling for schema validation
                DocumentBuilder docbuilder = fac.newDocumentBuilder();
                docbuilder.setErrorHandler(this);
                // create DOM Document
                try {
                    menu = docbuilder.parse(fis);
                }
                catch (SAXException se) {
                    // schema validation failed
                    throw new Exception("Error parsing headerFile " + realPath + headerFileName + ".  " + se.getMessage());
                }
                // close file
                fis.close();
                // get header element
                NodeList nl = menu.getElementsByTagName("header");
                int sz = nl.getLength();
                //loop through headers, adding each to XMLGridHeader
                for (int i = 0; i < sz; i++)
                    addHeader(nl.item(i), fields, conn);
                l.exiting(getClass().getName(), "load");
            }
            else {
                if (!header.gridHeaderDefinesDisplayableColumnOrder()) {
                    throw new IllegalArgumentException("The header filename is missing.");
                }
            }
        }
        finally {
            // close file if it is open
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {}
            }
            // close schema if it is open
            if (xsdInputStream != null) {
                try {
                    xsdInputStream.close();
                } catch (IOException ignore) {}
            }
        }
    }

    /**
     * Add contents of node to XMLGridHeader
     *
     * @param n      current node
     * @param fields OasisFields object as HashMap
     * @param conn   JDBC Connection
     * @throws Exception
     */
    protected void addHeader(Node n, HashMap fields, Connection conn) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader", new Object[]{fields, conn});
        }
        OasisFormField field = null;
        String name, visible, length, align, href, hrefkey = null;
        String maxLength, rows, cols, decimalPlaces = null, cellWidth ;
        String title = null;
        int type, displayType;
        ArrayList listData = null;

        // Process Elements first

        // look for the name element
        NodeList nl = ((Element) n).getElementsByTagName("name");
        // found it, use it
        if (nl.getLength() == 1) {
            name = nl.item(0).getFirstChild().getNodeValue();
            field = null;
        }
        // didn't find name element, look for the fieldname element
        else {
            nl = ((Element) n).getElementsByTagName("fieldname");
            // didn't find it, problem
            if (nl.getLength() != 1) {
                l.severe("name and fieldname elements are missing.");
                throw new IllegalArgumentException("name and fieldname elements are missing.");
            }
            field = (OasisFormField) fields.get(nl.item(0).getFirstChild().getNodeValue());
            // this is one of those irritating errors that we need to provide some debug
            // info about.
            if(field==null) {
                StringBuffer msg = new StringBuffer("OasisFormField with fieldId: ").
                    append(nl.item(0).getFirstChild().getNodeValue()).append(" not found in OasisFields. ").
                    append("Did you spell the fieldid correctly in your grid xml?");
                if(fields instanceof OasisFields) {
                    msg.append("The available fieldIds are: ");
                    ArrayList lst = ((OasisFields) fields).getPageFields();
                    int sz = lst.size();
                    for(int x=0;x<sz;x++)
                        msg.append('[').append(lst.get(x)).append(']');
                }
                String sMsg = msg.toString();
                l.severe(sMsg);
                throw new IllegalArgumentException(sMsg);
            }
            RequestHelper.addHeaderFieldId(field.getFieldId());
            name = field.getLabel();
        }

        // Look for the title element
        nl = ((Element) n).getElementsByTagName("title");
        if (nl.getLength() == 1)
            title = nl.item(0).getFirstChild().getNodeValue();

        // initialize optional stuff to null
        href = hrefkey = null;
        // check for listdata element
        nl = ((Element) n).getElementsByTagName("listdata");
        // found it
        if (nl.getLength() == 1) {
            // load up arraylist
            listData = getList(nl.item(0));
        }
        // did not find listdata, so we might then find listsql element
        else {
            // check for listsql
            nl = ((Element) n).getElementsByTagName("listsql");
            // found it
            if (nl.getLength() == 1) {
                // exec sql and load up arraylist
                listData = Querier.doListQuery(nl.item(0).getFirstChild().getNodeValue(), conn, 2, 1, false);
            }
        }
        // check for href element
        nl = ((Element) n).getElementsByTagName("href");
        // found href element
        if (nl.getLength() == 1) {
            // this is an href element with attributes, we pull the attributes out
            NamedNodeMap map = nl.item(0).getAttributes();
            href = map.getNamedItem("url").getNodeValue();
            hrefkey = map.getNamedItem("hrefkey").getNodeValue();
        }

        // process attributes
        NamedNodeMap atts = n.getAttributes();
        // Get type
        type = decodeType(XMLUtils.getAttributeValue(atts, "type"));
        // get length
        length = XMLUtils.getAttributeValue(atts, "length");
        // get display
        displayType = decodeDisplayType(XMLUtils.getAttributeValue(atts, "display"));
        // get align
        align = XMLUtils.getAttributeValue(atts, "align");
        // get visible
        visible = XMLUtils.getAttributeValue(atts, "visible");
        // get maxlength
        maxLength = XMLUtils.getAttributeValue(atts, "maxlength");
        // get rows
        rows = XMLUtils.getAttributeValue(atts, "rows");
        // get cols
        cols = XMLUtils.getAttributeValue(atts, "cols");
        // get cols
        decimalPlaces = XMLUtils.getAttributeValue(atts, "decimalplaces");
        // get cellWidth
        cellWidth = XMLUtils.getAttributeValue(atts, "width");

        // call different addHeader methods depending on if we have an OasisField id
        if (field == null) {
            header.addHeader(name, type, length, displayType, align, visible, listData,
                    href, hrefkey, maxLength, rows, cols, title, decimalPlaces, cellWidth);
        }
        else {
            header.addHeader(field, type, length, displayType, align, listData, href,
                    hrefkey, maxLength, rows, cols, title, decimalPlaces, cellWidth, field.getHref());
        }
        l.exiting(getClass().getName(), "addHeader");
    }

    /**
     * Return ArrayList of LabelValueBean objects given a Node
     *
     * @param n "listdata" node
     * @return ArrayList of LabelValueBean objects
     */
    protected ArrayList getList(Node n) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getList", new Object[]{n});
        }
        // get all <record> elements
        NodeList nl = ((Element) n).getElementsByTagName("record");
        int sz = nl.getLength();
        ArrayList list = new ArrayList(sz);
        // loop through child nodes
        for (int i = 0; i < sz; i++) {
            // add value & label
            Node n1 = ((Element) nl.item(i)).getElementsByTagName("value").
                    item(0).getFirstChild();
            String value = (n1 == null) ? "" : n1.getNodeValue().trim();
            String label = ((Element) nl.item(i)).getElementsByTagName("label").
                    item(0).getFirstChild().getNodeValue().trim();
            list.add(new LabelValueBean(label, value));
        }
        l.exiting(getClass().getName(), "getList", list);
        return list;

    }

    /**
     * Decode textual type from XML document into the integer datatype that
     * XMLGridHeader wants
     *
     * @param type textual type from XML document
     * @return One of the TYPE_xxx constants from XMLGridHeader
     */
    protected int decodeType(String type) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "decodeType", new Object[]{type});
        }
        int i = (type == null) ? XMLGridHeader.TYPE_DEFAULT : ((Integer) xmlTypeMap.get(type)).intValue();
        l.exiting(getClass().getName(), "decodeType", new Integer(i));
        return i;
    }

    /**
     * Decode textual display type from XML document into the integer datatype
     * that XMLGridHeader wants
     *
     * @param displayType textual display type from XML document
     * @return One of the DISPLAY_xxx constants from XMLGridHeader
     */
    protected int decodeDisplayType(String displayType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "decodeDisplayType", new Object[]{displayType});
        }
        int i = (displayType == null) ? XMLGridHeader.DISPLAY_DEFAULT : ((Integer) xmlDisplayTypeMap.get(displayType)).intValue();
        l.exiting(getClass().getName(), "decodeDisplayType", new Integer(i));
        return i;
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
        l.warning(msg);
    }

    /**
     * Receive notification of a non-recoverable error.
     * @param exception  Some kind of fatal SAX Parsing error
     * @throws SAXException
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Fatal Error");
        l.severe(getSAXErrorMessage(exception, " Fatal Error"));
        throw new SAXException(msg);
    }

    /**
     * Receive notification of a warning.
     * @param exception Some kind of recoverable SAX Parsing warning
     * @throws SAXException
     */
    public void warning(SAXParseException exception) throws SAXException {
        String msg = getSAXErrorMessage(exception, "Error");
        l.warning(msg);
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("XMLGridHeaderDOMLoader");
        buf.append("{xmlTypeMap=").append(xmlTypeMap);
        buf.append(",xmlDisplayTypeMap=").append(xmlDisplayTypeMap);
        buf.append(",xmlTypes=").append(xmlTypes == null ? "null" : Arrays.asList(xmlTypes).toString());
        buf.append(",xmlDisplayTypes=").append(xmlDisplayTypes == null ? "null" : Arrays.asList(xmlDisplayTypes).toString());
        buf.append(",header=").append(header);
        buf.append(",realPath=").append(realPath);
        buf.append('}');
        return buf.toString();
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
