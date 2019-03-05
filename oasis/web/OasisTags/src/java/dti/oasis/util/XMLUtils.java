package dti.oasis.util;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.tags.XMLGridHeader;
import dti.oasis.tags.XMLGridUpdateMap;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.ResponseUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import weblogic.xml.dom.TextNode;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * XML functionality Utility class
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Feb 20, 2004
 *
 * @author jbe
 */
/*
* Revision Date    Revised By  Description
* ---------------------------------------------------
* 3/2/2004      jbe         Added getAttributeValue
*                           Added getDocument
* 3/8/2004      jbe         Added getChildElementValues
* 6/10/2004     jbe         Added lovToXML
* 8/9/2004      jbe         Add resultsetToXML & getXmlData
* 10/14/2004    jbe         Added mergeXML
* 10/28/2004    jbe         Make sure mapToXML handles empty values w/ <KEY/>
* 3/4/2005      jbe         Add ParseException to updateRow and mergeXML
*                           Modify updateRow to handle different datatypes.
* 3/22/2005     jbe         Add new resultSettoXml for live JDBC ResultSets.
* 4/6/2005      jbe         Check protected fields in getXmlData
* 5/19/2005		jbe			Support TYPE_UPDATEONLYURL
* 6/3/2005		jbe			Fixed updateRow
* 7/14/2005     jbe         Handle String Arrays in mapToXML
* 8/19/2005		jbe			Change xmlns:xsl to "http://www.w3.org/1999/XSL/Transform"
*                           and revise XSL so that it is up to date
* 8/22/2005     jbe         Fix xsl:sort
* 8/25/2005     jbe         Add new resultSetToXML for DisconnectedResultSet
* 4/27/2009     kenney      Add new resultSetToXML for BaseResultSet
* 2/5/2010      Kenney      Modified recordSetToXml to handle Clob object
* 10/18/2010    Tony        109344 - Added function setElementValue()
* 10/06/2014    awu         157694 - Modified getXmlData, resultSetToXml,
*                                    to get the data from records by column name instead of index.
* 08/29/2014    parker      Issue 138227. Enhancement to add the ows logs.
* 05/04/2017    kshen       184568. Add method stringToElement to convert string value to org.w3c.dom.Element.
 * 02/07/2018   MLM         191395 - Added gzip compression and base64 encoding.
* 11/12/2018    wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
* ---------------------------------------------------
*/

public class XMLUtils {
    /**
     * Convert a DynaActionForm to an xml representation
     *
     * @param form DynaActionForm
     * @return String xml
     */
    public static String formToXML(DynaActionForm form) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "formToXML", new Object[]{form});
        }

        String xml = mapToXML(form.getMap());
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "formToXML", xml);
        }
        return xml;
    }

    /**
     * Convert a Map to an xml representation
     *
     * @param map
     * @return String xml
     */
    public static String mapToXML(Map map) {
        Iterator it = map.keySet().iterator();
        StringBuffer buff = new StringBuffer("<?xml version=\"1.0\"?><form>");
        while (it.hasNext()) {
            String key = (String) it.next();
            Object o = map.get(key);
            String value = null;
            // if this is an array, make it comma delimited (before and after)
            if(o instanceof String[]) {
                String[] vals = (String[]) o;
                int sz = vals.length;
                StringBuffer buffStr = new StringBuffer();
                for(int i=0;i<sz;i++) {
                    if(!StringUtils.isBlank(vals[i]))
                        buffStr.append(',').append(vals[i]);
                }
                if(buffStr.length()>0) buffStr.append(',');
                value = buffStr.toString();
            }
            else if (o instanceof String)
                value = (String) o;
            else {
                c_l.warning("Unable to parse value for map key [" + key + "] - Object of type:" + o.getClass().getName() +
                    ".  Skipping object.");
                continue;
            }

            if (!StringUtils.isBlank(key)) {
                if (!StringUtils.isBlank(value))
                    buff.append("<").append(key).append(">").append(encode(value)).
                            append("</").append(key).append(">");
                else
                    buff.append("<").append(key).append("/>");

            }
        }
        String xml = buff.append("</form>").toString();

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "mapToXML", xml);
        }
        return xml;
    }

    /**
     * Get value of a node's attribute
     *
     * @param atts map of a node's attributes
     * @param name name to look up in map
     * @return value of node's attribute
     */
    public static String getAttributeValue(NamedNodeMap atts, String name) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getAttributeValue", new Object[]{atts, name});
        }

        if (atts == null)
            return null;
        Node att = atts.getNamedItem(name);
        String s = (att == null) ? null : att.getNodeValue();
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "getAttributeValue", s);
        }
        return s;
    }

    /**
     * Constructs and returns a DOM Document from an XML String
     *
     * @param xml          XML String
     * @param errorHandler You must pass an object that implements ErrorHandler
     * @return Document object
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document getDocument(String xml, ErrorHandler errorHandler) throws ParserConfigurationException, IOException, SAXException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getDocument", new Object[]{xml, errorHandler});
        }

        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = fac.newDocumentBuilder();
        docbuilder.setErrorHandler(errorHandler);
        Document doc = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
        doc = docbuilder.parse(bis);
        bis.close();
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "getDocument", doc);
        }
        return doc;
    }

    /**
     * Returns a comma delimited list of values for any child element of Node n, whose name
     * matches the name parameter
     *
     * @param n    Node whose children are inspected
     * @param name Name of element
     * @return Comma delimited list of values
     */
    public static String getChildElementValues(Node n, String name) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getChildElementValues", new Object[]{n, name});
        }

        NodeList nl = ((Element) n).getElementsByTagName(name);
        StringBuffer buff = new StringBuffer();
        int sz = nl.getLength();
        for (int i = 0; i < sz; i++) {
            String val = "";
            if(nl.item(i).getFirstChild() != null) {
                val = nl.item(i).getFirstChild().getNodeValue();
            }
            if (i > 0) buff.append(",");
            buff.append(val);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "getChildElementValues", buff);
        }
        return buff.toString();
    }

    /**
     * Sets the element value with val for any child element of Node n, whose name
     * matches the name parameter
     *
     * @param n    Node whose children are inspected
     * @param name Name of element
     * @param val  The value needs to set
     */
    public static void setElementValue(Node n, String name, String val) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "setElementValue", new Object[]{n, name, val});
        }

        if (val == null) val = "";
        NodeList nl = ((Element) n).getElementsByTagName(name);
        int sz = nl.getLength();
        for (int i = 0; i < sz; i++) {
            if(nl.item(i).getFirstChild() != null) {
                nl.item(i).getFirstChild().setNodeValue(val);
            } else {
                nl.item(i).appendChild(n.getOwnerDocument().importNode(new TextNode(val), true));
            }
        }
        c_l.exiting(XMLUtils.class.getName(), "setElementValue");
    }

    /**
     * Encodes a text value by wrapping it in a CDATA:
     * <![CDATA[mytext]]>
     *
     * @param val The value to encode
     * @return encoded value
     */
    public static String encode(String val) {
        return new StringBuffer("<![CDATA[").append(val).append("]]>").toString();
    }

    /**
     * Generates xml for a given lov and field id
     *
     * @param fieldId
     * @param lov
     * @return
     */
    public static String lovToXML(String fieldId, ArrayList lov) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "lovToXML", new Object[]{fieldId, lov});
        }

        StringBuffer buff = new StringBuffer();
        if (lov != null) {
            int sz1 = lov.size();
            buff.append("<lov id=\"").append(fieldId).
                    append("\">");
            for (int j = 0; j < sz1; j++) {
                LabelValueBean lb = (LabelValueBean) lov.get(j);
                buff.append("<row><value>").
                        append(encode(lb.getValue())).
                        append("</value><label>").
                        append(encode(lb.getLabel())).
                        append("</label></row>");
            }
            buff.append("</lov>");
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "lovToXML", buff.toString());
        }
        return buff.toString();

    }

    /**
     * Convert a DisconnectedResultSet to an xml data island.
     *
     * @param out    Writer to which the XML Data Island will be written
     * @param xmlId  The id to use in the xml tag.
     * @param data   DisconnectedResultSet
     * @param header XMLGridHeader
     * @throws ParseException
     * @throws IOException
     */
    public static void resultSetToXml(Writer out, String xmlId, BaseResultSet data,
                                      XMLGridHeader header) throws ParseException, IOException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "resultSetToXml", new Object[]{xmlId, data, header});
        }

        String sColName;
        StringBuffer strXMLURL = new StringBuffer();
        StringBuffer strXMLDate = new StringBuffer();
        StringBuffer sUpdtCol = new StringBuffer();

        int colCount = data.getColumnCount();

        data.first();

        out.write(new StringBuffer("\n<XML id=\"").append(xmlId).append("1XSL\">").
                append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n").
				append("<xsl:template match=\"/\" >\n<ROWS>\n").
                append("<xsl:for-each select=\"//ROW\">\n").
				append("<xsl:sort select=\".\" order=\"ascending\" data-type=\"text\" />\n<ROW>\n").
                append(" <xsl:attribute name=\"id\"><xsl:value-of select=\"@id\"/></xsl:attribute>\n").
                append(" <xsl:attribute name=\"col\"><xsl:value-of select=\"@col\"/></xsl:attribute>\n").
                toString());

        String align, name;
        int display, type;

        for (int i = 1; i <= colCount; i++) {

            sColName = new StringBuffer("C").append(data.getColumnName(i).trim().toUpperCase().replace(']', ' ').trim().
                    replace('[', ' ').trim().replace(' ', '_').replace('#', 'N').replace('/', ' ').trim().
                    replace('\'', '_')).toString();

            out.write(new StringBuffer("<").
                    append(sColName).append("><xsl:value-of select=\"").
                    append(sColName).append("\" />").append("</").
                    append(sColName).append(">\n").toString());

            HashMap headerMap = header.getHeaderMap(i);
            type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
            if (!headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
                align = (String) headerMap.get(XMLGridHeader.CN_ALIGN);
                name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                display = ((Integer) headerMap.get(XMLGridHeader.CN_DISPLAY)).intValue();
                if (align == null)
                    align = "Center";
                if (type == XMLGridHeader.TYPE_DROPDOWN || type == XMLGridHeader.TYPE_URL || type == XMLGridHeader.TYPE_UPDATEONLYURL)
                    align = (display == XMLGridHeader.DISPLAY_MONEY && type != XMLGridHeader.TYPE_DROPDOWN) ? "right" : "left";

                if (!StringUtils.isBlank(name)) {
                    if (type == XMLGridHeader.TYPE_FORMATDATE || type == XMLGridHeader.TYPE_FORMATDATETIME) {
                        strXMLDate.append("<DATE_").append(i).append("><xsl:value-of select=\"DATE_").
                                append(i).append("\" />").append("</DATE_").append(i).append(">\n");

                    }
                    else if (type == XMLGridHeader.TYPE_URL || type == XMLGridHeader.TYPE_UPDATEONLYURL) {
                        strXMLURL.append("<URL_").append(i).append("><xsl:value-of select=\"URL_").
                                append(i).append("\" /></URL_").append(i).append(">\n");

                    }
                }
                headerMap.put(XMLGridHeader.CN_NAME, sColName);
                switch (type) {
                	case XMLGridHeader.TYPE_TEXT:
                    case XMLGridHeader.TYPE_UPPERCASE_TEXT:
                    case XMLGridHeader.TYPE_LOWERCASE_TEXT:
                    case XMLGridHeader.TYPE_TEXTAREA:
                	case XMLGridHeader.TYPE_UPDATEONLY:
                	case XMLGridHeader.TYPE_DATE:
                	case XMLGridHeader.TYPE_NUMBER:
                	case XMLGridHeader.TYPE_CHECKBOX:
                	case XMLGridHeader.TYPE_RADIOBUTTON:
                	case XMLGridHeader.TYPE_DROPDOWN:
                	case XMLGridHeader.TYPE_UPDATEONLYURL:
                    	sUpdtCol.append(i - 1).append(",");
                    	break;
				}

            }
            else {	// hidden
                if (type == XMLGridHeader.TYPE_UPDATEONLY) {
                    sUpdtCol.append(i - 1).append(",");
                }
                headerMap.put(XMLGridHeader.CN_NAME, sColName);
            }
        }
        out.write(strXMLDate.toString());
        out.write(strXMLURL.toString());
        out.write("<UPDATE_IND><xsl:value-of select=\"UPDATE_IND\"/></UPDATE_IND></ROW>\n");
        out.write("</xsl:for-each></ROWS></xsl:template></xsl:stylesheet></XML>\n");

        StringBuffer strXML = new StringBuffer("<XML id=\"").append(xmlId).append("1\" xml:space=\"preserve\" xmlns:dataType=").
                append("\"urn:schemas-microsoft-com:datatypes\" ");
        strXML.append("empty=\"false\" >");

        out.write(strXML.append("\n").toString());

        if (sUpdtCol != null & sUpdtCol.length() > 0)
            sUpdtCol.deleteCharAt(sUpdtCol.length() - 1);

        getXmlData(out, xmlId, data, header, sUpdtCol.toString());
        out.write("</XML>\n");

        c_l.exiting(XMLUtils.class.getName(), "resultSetToXml");
    }

    /**
     * Writes the xml content <i>ROWS</i> and <i>ROW</i> tags out to the Writer.
     *
     * @param out      Writer to which the XML will be written.
     * @param xmlId    Id used for javascript:selectRow
     * @param data     DisconnectedResultSet
     * @param header   XML GridHeader
     * @param sUpdtCol Updateable columns
     * @throws ParseException
     * @throws IOException
     */
    protected static void getXmlData(Writer out, String xmlId, BaseResultSet data, XMLGridHeader header, String sUpdtCol) throws ParseException, IOException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getXmlData", new Object[]{out, xmlId, data, header, sUpdtCol});
        }

        String strTab = "  ";
        String strOpen = "<";
        String strClose = ">";
        String strEndOpen = "</";
        StringBuffer strXML;
        StringBuffer strXMLDate;
        boolean bDateInd, bDateTimeInd, bUrlInd, isDataPresent;

        int colCount = data.getColumnCount();

        out.write("<ROWS>\n");
        data.beforeFirst();
        // Start loop through rows
        while (data.next()) {
            // row header
            strXML = new StringBuffer("\n<ROW id=\"").append(data.getString(1)).
                    append("\" col=\"").append(sUpdtCol).append("\" >\n");
            strXMLDate = new StringBuffer();

            // Start loop through columns
            for (int i = 1; i <= colCount; i++) {
                bDateInd = false;
                bDateTimeInd = false;
                bUrlInd = false;

                String dataColumnName = data.getColumnName(i);
                String dataItem = data.getString(dataColumnName, "");
                isDataPresent = (dataItem != null && dataItem.trim().length() > 0);

                HashMap headerMap = header.getHeaderMap(i);
                int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
                int display = ((Integer) headerMap.get(XMLGridHeader.CN_DISPLAY)).intValue();
                String name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                ArrayList lov = (ArrayList) headerMap.get(XMLGridHeader.CN_LISTDATA);
                boolean isProtected = ((Boolean)headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();

                // If this field is protected, set the data item to null as it should not appear in the xml
                if(isProtected)
                    dataItem = "";
                // tag start
                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).
                        append(strClose);

                // Start visible items
                if (!headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {

                    if (type == XMLGridHeader.TYPE_FORMATMONEY) {
                        strXML.append(FormatUtils.formatCurrency(dataItem));
                    }
                    else if (type == XMLGridHeader.TYPE_FORMATDATE || type == XMLGridHeader.TYPE_DATE) {
                        strXML.append(OasisTagHelper.formatDateAsXml(data.getDate(i)));
                        bDateInd = true;
                    }
                    else if (type == XMLGridHeader.TYPE_FORMATDATETIME) {
                        strXML.append(OasisTagHelper.formatDateTimeAsXml(data.getDate(i)));
                        bDateTimeInd = true;
                    }
                    else if (type == XMLGridHeader.TYPE_URL || type == XMLGridHeader.TYPE_UPDATEONLYURL) {
                        bUrlInd = true;
                        if (isDataPresent)
                            if (display == XMLGridHeader.DISPLAY_MONEY)
                                strXML.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem)));
                            else
                                strXML.append(ResponseUtils.filter(dataItem));

                    }
                    else {
                        if (isDataPresent) {
                            // if a list of values is present for a readonly field, decode
                            if (type == XMLGridHeader.TYPE_DEFAULT && lov != null)
                                strXML.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                            else
                                strXML.append(ResponseUtils.filter(dataItem));
                        }
                    }
                }
                // End Visible items
                // Start hidden items
                else {
                    if (type == XMLGridHeader.TYPE_ANCHOR) {
                        if (isDataPresent)
                            strXML.append("javascript:selectRowWithProcessingDlg('").append(xmlId).
                                    append("','").append(ResponseUtils.filter(dataItem).
                                    replaceAll("'", "''")).append("');");
                        else
                            strXML.append("javascript:selectRowWithProcessingDlg('").append(xmlId).
                                    append("','-');");

                    }
                    else {
                        if (isDataPresent)
                            strXML.append(ResponseUtils.filter(dataItem));
                    }
                }
                // End Hidden items

                // Start special tags
                if (isDataPresent) {
                    if (bDateInd)
                        strXMLDate.append("<DATE_").append(i).append(">").
                                append(DateUtils.dateDiff(DateUtils.DD_DAYS,
                                        "01/01/1993", data.getDate(i))).
                                append("</DATE_").append(i).append(">");
                    else if (bDateTimeInd)
                        strXMLDate.append("<DATE_").append(i).append(">").
                                append(DateUtils.dateDiff(DateUtils.DD_SECS,
                                        "01/01/1993 00:00:00", data.getDate(i))).
                                append("</DATE_").append(i).append(">");
                    else if (bUrlInd) {
                        String href = (String) headerMap.get(XMLGridHeader.CN_HREF);
                        String hrefKey = (String) headerMap.get(XMLGridHeader.CN_HREFKEY);
                        if (href.indexOf("javascript:") >= 0)
                            strXMLDate.append("<URL_").append(i).append(">").
                                    append(ResponseUtils.filter(href)).append("'").
                                    append(data.getString(Integer.parseInt(hrefKey))).
                                    append("');</URL_").append(i).append(">");
                        else
                            strXMLDate.append("<URL_").append(i).append(">").
                                    append(ResponseUtils.filter(href)).
                                    append(data.getString(Integer.parseInt(hrefKey))).
                                    append("</URL_").append(i).append(">");
                    }
                }
                // End special tags

                // closing tag
                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append("\n");

            }
            // End looping through columns
            strXML.append(strXMLDate).append("<UPDATE_IND>N</UPDATE_IND></ROW>");
            out.write(strXML.append("\n").toString());
        }
        // End looping through rows
        out.write("</ROWS>\n");
        c_l.exiting(XMLUtils.class.getName(), "getxmldata");
    }

    /**
     * Merges the update xml coming out of a databound grid with a DisconnectedResultSet.
     * This can be used if you need to merge the data a user entered into an updateable
     * grid back into the original DisconnectedResultSet so that you can redisplay the grid.
     * This method will insert new records in the DisconnectedResultSet and update existing
     * records, but will not delete records.
     *
     * @param rs      DisconnectedResultSet
     * @param header  XMLGridHeader with columns matching the rs
     * @param xmlData The update xml data.  This is generated by xmlproc.js#getChanges
     * @param handler An object which implements ErrorHandler. Specify one to catch any SAX Parse Exceptions.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ParseException               Bad date conversions cause this
     */
    public static void mergeXML(DisconnectedResultSet rs, XMLGridHeader header, String xmlData,
                                ErrorHandler handler) throws IOException, ParseException, ParserConfigurationException, SAXException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "mergeXML", new Object[]{rs, header, xmlData, handler});
        }

        // Get the map between XML updateable columns and the column indexes
        // in the DisconnectedResultSet
        XMLGridUpdateMap map = header.getUpdateMap();
        // Get an XML DOM Document
        Document doc = getDocument(xmlData, handler);
        // Get the list of rows
        NodeList nl = doc.getElementsByTagName("R");
        int sz = nl.getLength();
        // loop through the rows
        for (int i = 0; i < sz; i++) {
            // For each row, get the set of attributes (columns)
            Node n = nl.item(i);
            NamedNodeMap nnm = n.getAttributes();
            // get update column and id
            String update = nnm.getNamedItem("update_ind").getNodeValue();
            String id = nnm.getNamedItem("id").getNodeValue();
            //l.fine("Operation="+update+" id="+id);

            // based on the update operation
            switch (update.charAt(0)) {
                case 'D':
                    if (!rs.findRow(map.getIdColumn(), id))
                        throw new IllegalArgumentException("Illegal xml.  Id [" + id + "] not found in data.");
                    // delete the row and move on
                    rs.setUpdateInd('D');
                    break;
                case 'I':
                    // add a new row and then flow to the next logic
                    rs.addEmptyRow();
                    rs.last();
                    updateRow(map, rs, nnm);
                    rs.setString(1, id);
                    rs.setUpdateInd('I');
                    break;
                case 'Y':
                    if (!rs.findRow(map.getIdColumn(), id))
                        throw new IllegalArgumentException("Illegal xml.  Id [" + id + "] not found in data.");
                    updateRow(map, rs, nnm);
                    rs.setUpdateInd('Y');
                    break;
            }
        }
        c_l.exiting(XMLUtils.class.getName(), "mergeXML");
    }

    /**
     * Loop through the updateable columns and update the resultset with the xml data
     *
     * @param map
     * @param rs
     * @param nnm
     * @throws ParseException - bad date conversion
     */
    private static void updateRow(XMLGridUpdateMap map, DisconnectedResultSet rs, NamedNodeMap nnm)
            throws ParseException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "updateRow", new Object[]{map, rs, nnm});
        }

        int sz1 = map.getColumnCount();
        for (int j = 0; j < sz1; j++) {
            String val = nnm.getNamedItem("c" + j).getNodeValue();
            int colnum = map.getColumn(j);

            switch (rs.getColumn(colnum).getColumnType()) {
                case Types.TIMESTAMP:
                case Types.TIME:
                case Types.DATE:
                    rs.setDate(colnum, FormatUtils.getDate(val));
                    break;
                default:
                    rs.setString(colnum, val);
                    break;
            }

        }
        c_l.exiting(XMLUtils.class.getName(), "updateRow");
    }

    /**
     * Get unique column name - recursively if need be.
     *
     * @param name    Column name
     * @param columns ArrayList of columns.
     * @return A unique column name, we hope.
     */
    protected static String getUniqueColumn(String name, ArrayList columns) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getUniqueColumn", new Object[]{name, columns});
        }

        String retName = (columns.contains(name)) ? getUniqueColumn(name + "_1", columns) : name;
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "getUniqueColumn", retName);
        }
        return retName;
    }

    /**
     * Convert a live JDBC ResultSet to XML in the format:
     * <pre>
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL1NAME&gt;val&lt;/COL1NAME&gt;&lt;COL2NAME&gt;val
     * &lt;/COL2NAME&gt;&lt/ROW&gt;&lt/ROWS&gt
     * </pre>
     * <br>The ResultSetMetaData is used to determine the column name.  If duplicate
     * column names are found, the repeated names are made unique by recursively adding
     * "_1" to the name until it is truly unique.
     *
     * @param data Live JDBC ResultSet
     * @return XML String
     * @throws SQLException
     */
    public static String resultSetToXml(ResultSet data) throws SQLException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "resultSetToXml", new Object[]{data});
        }

        ResultSetMetaData rsmd = data.getMetaData();
        int colCount = rsmd.getColumnCount();
        ArrayList columns = new ArrayList(colCount);
        // Create an ArrayList of unique column names.  They may actually be
        // identical to the column names in the MetaData, but if there is a duplicate
        // column name in the metadata, we avoid it.
        for (int col = 1; col <= colCount; col++) {
            String name = rsmd.getColumnName(col);
            columns.add(getUniqueColumn(name.toUpperCase(), columns));
        }

        final String strTab = "  ";
        final String strOpen = "<";
        final String strClose = ">";
        final String strEndOpen = "</";
        final String strEndClose = "/>";
        StringBuffer xml = new StringBuffer("<ROWS>\n");

        // Start loop through rows
        while (data.next()) {
            xml.append("<ROW>\n");
            // Start loop through columns
            for (int i = 1; i <= colCount; i++) {
                String name = (String) columns.get(i - 1);
                String dataItem = data.getString(i);
                // tag start
                xml.append(strTab).append(strOpen).append(name.trim().toUpperCase());
                if (dataItem != null) {
                    xml.append(strClose);
                    xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                            encode(dataItem) : dataItem);
                    // closing tag
                    xml.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append("\n");
                }
                else
                    xml.append(strEndClose);
            }
            xml.append("</ROW>\n");
            // End looping through columns
        }
        // End looping through rows
        xml.append("</ROWS>\n");


        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "resultSetToXml", xml);
        }
        return xml.toString();
    }

    /**
     * Convert a DisconnectedResultSet to XML in the format:
     * <pre>
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL name="col1name"&gt;val&lt;/COL&gt;&lt;COL name="col2name"&gt;val
     * &lt;/COL&gt;&lt/ROW&gt;&lt/ROWS&gt
     * </pre>
     * <br>The DisconnectedColumnMetaData is used to determine the column name.
     *
     * @param data DisconnectedResultSet
     * @return XML String
     */
    public static String resultSetToXml(DisconnectedResultSet data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "resultSetToXml", new Object[]{data});
        }

        int colCount = data.getColumnCount();

        final String strTab = "  ";
        final String strOpen = "<COL name=\"";
        final String strClose = ">";
        final String strEndOpen = "</COL>\n";
        final String strEndClose = "/>\n";
        StringBuffer xml = new StringBuffer("<ROWS>\n");

        // Start loop through rows
        while (data.next()) {
            xml.append("<ROW>\n");
            // Start loop through columns
            for (int i = 1; i <= colCount; i++) {
                DisconnectedColumnMetaData col = data.getColumn(i);
                String dataColumnName = col.getColumnName();
                String dataItem = data.getString(dataColumnName, "");
                // tag start
                xml.append(strTab).append(strOpen).append(dataColumnName).append("\"");
                if (dataItem != null) {
                    xml.append(strClose);
                    xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                            encode(dataItem) : dataItem);
                    // closing tag
                    xml.append(strEndOpen);
                }
                else
                    xml.append(strEndClose);
            }
            xml.append("</ROW>\n");
            // End looping through columns
        }
        // End looping through rows
        xml.append("</ROWS>\n");


        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "resultSetToXml", xml);
        }
        return xml.toString();
    }

    /**
     * Convert a BaseResultSet to XML in the format:
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL name="col1name"&gt;val&lt;/COL&gt;&lt;COL name="col2name"&gt;val
     * &lt;/COL&gt;&lt/ROW&gt;&lt/ROWS&gt
     *
     * @param data BaseResultSet
     * @return XML String
     */
    public static String resultSetToXml(BaseResultSet data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "resultSetToXml", new Object[]{data});
        }

        int colCount = data.getColumnCount();
        
        final String strTab = "  ";
        final String strOpen = "<COL name=\"";
        final String strClose = ">";
        final String strEndOpen = "</COL>\n";
        final String strEndClose = "/>\n";
        StringBuffer xml = new StringBuffer("<ROWS>\n");

        // Start loop through rows
        while (data.next()) {
            xml.append("<ROW>\n");
            // Start loop through columns
            for (int i = 1; i <= colCount; i++) {
                String dataColumnName = data.getColumnName(i);
                String dataItem = data.getString(dataColumnName, "");
                // tag start
                xml.append(strTab).append(strOpen).append(dataColumnName).append("\"");
                if (dataItem != null) {
                    xml.append(strClose);
                    xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                        encode(dataItem) : dataItem);
                    // closing tag
                    xml.append(strEndOpen);
                } else
                    xml.append(strEndClose);
            }
            xml.append("</ROW>\n");
            // End looping through columns
        }
        // End looping through rows
        xml.append("</ROWS>\n");

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "resultSetToXml", xml);
        }
        return xml.toString();
    }

    /**
     * Convert a RecordSet to XML in the format:
     * <pre>
     * &lt;ROWS&gt;&lt;ROW&gt;&lt;COL1NAME&gt;val&lt;/COL1NAME&gt;&lt;COL2NAME&gt;val
     * &lt;/COL2NAME&gt;&lt/ROW&gt;&lt/ROWS&gt
     * </pre>
     *
     * @param data RecordSet
     * @return XML String
     */
    public static String recordSetToXml(RecordSet data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "recordSetToXml", new Object[]{data});
        }

        final String strTab = "  ";
        final String strOpen = "<";
        final String strClose = ">";
        final String strEndOpen = "</";
        final String strEndClose = "/>";
        StringBuffer xml = new StringBuffer("<ROWS>\n");
        Iterator rows = data.getRecords();
        // Start loop through rows
        while (rows.hasNext()) {
            Record row = (Record) rows.next();
            xml.append("<ROW>\n");
            // Start loop through columns
            Iterator cols = row.getFields();
            String dataItem = "";
            while (cols.hasNext()) {
                Field field = (Field) cols.next();
                String elementName = field.getName().trim().toUpperCase();
                if (field.getValue() instanceof Clob) {
                    try {
                        dataItem = DatabaseUtils.ClobToString((Clob) field.getValue());
                    } catch (Exception e) {
                        c_l.throwing(XMLUtils.class.getName(), "Failed to Convert Clob Object", e);
                    }
                } else
                    dataItem = field.getStringValue("");
                // tag start
                xml.append(strTab).append(strOpen).append(elementName);
                if (dataItem != null) {
                    xml.append(strClose);
                    xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                            encode(dataItem) : dataItem);
                    // closing tag
                    xml.append(strEndOpen).append(elementName).append(strClose).append("\n");
                } else
                    xml.append(strEndClose);
            }
            xml.append("</ROW>\n");
            // End looping through columns
        }
        // End looping through rows
        xml.append("</ROWS>\n");

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "recordSetToXml", xml);
        }
        return xml.toString();
    }

    /**
     * Convert a Record to XML in the format:
     * <pre>
     * &lt;ROW&gt;&lt;COL1NAME&gt;val&lt;/COL1NAME&gt;&lt;COL2NAME&gt;val
     * &lt;/COL2NAME&gt;&lt/ROW&gt;
     * </pre>
     *
     * @param data RecordSet
     * @return XML String
     */
    public static String recordToXml(Record data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "recordToXml", new Object[]{data});
        }

        final String strTab = "  ";
        final String strOpen = "<";
        final String strClose = ">";
        final String strEndOpen = "</";
        final String strEndClose = "/>";
        StringBuffer xml = new StringBuffer("<ROW>\n");


        // Start loop through columns
        Iterator cols = data.getFields();
        String dataItem = "";
        while (cols.hasNext()) {
            Field field = (Field) cols.next();
            String elementName = field.getName().trim().toUpperCase();
            if (field.getValue() instanceof Clob) {
                try {
                    dataItem = DatabaseUtils.ClobToString((Clob) field.getValue());
                } catch (Exception e) {
                    c_l.throwing(XMLUtils.class.getName(), "Failed to Convert Clob Object", e);
                }
            } else
                dataItem = field.getStringValue("");
            // tag start
            xml.append(strTab).append(strOpen).append(elementName);
            if (dataItem != null) {
                xml.append(strClose);
                xml.append((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                        encode(dataItem) : dataItem);
                // closing tag
                xml.append(strEndOpen).append(elementName).append(strClose).append("\n");
            } else
                xml.append(strEndClose);
        }

        // End looping through columns

        xml.append("</ROW>\n");

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "recordToXml", xml);
        }
        return xml.toString();
    }

    /**
     * ZipInputStream transform to String
     *
     * @param zipIn
     * @return String
     */
    public static String formatZipInputStream(ZipInputStream zipIn) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "formatZipInputStream", zipIn);
        }

        String xmlResult = "";
        Document doc = null;
        if(zipIn != null){
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                ZipEntry entry = zipIn.getNextEntry();
                doc = builder.parse(zipIn);
                xmlResult = formatNode(doc);
            } catch (Exception e) {
                c_l.throwing(XMLUtils.class.getName(), "Failed to format ZipInputStream", e);
                throw new AppException("Failed to format ZipInputStream");
            } finally {
                if (zipIn != null) {
                    try {
                        zipIn.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "formatZipInputStream", xmlResult);
        }
        return xmlResult;
    }

    /**
     * Document transform to String
     *
     * @param node
     * @return String
     */
    public static String formatNode(Node node) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "formatNode", node);
        }

        String xmlResult = "";
        StringWriter stringWriter = new StringWriter();
        try {
            if (node != null) {
                Document formatDocument = (node instanceof Document) ? (Document) node : node.getOwnerDocument();
                OutputFormat format = new OutputFormat(formatDocument, "UTF-8", true);
                format.setIndenting(true);
                format.setIndent(4);
                format.setPreserveSpace(false);
                format.setLineWidth(100);
                XMLSerializer serializer = new XMLSerializer(stringWriter, format);
                serializer.asDOMSerializer();
                serializer.serialize(node);
                xmlResult = stringWriter.toString();
            }
        } catch (Exception e) {
            c_l.throwing(XMLUtils.class.getName(), "Failed to format the document", e);
            throw new AppException("Failed to format the document");
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (IOException e) {
                }
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "formatNode", xmlResult);
        }
        return xmlResult;
    }


    /**
     * get the Node Vaulue from document according the tagName
     *
     * @param dom
     * @param tagName
     * @return String
     */

    public static String getNodeValueFromDocument(Document dom, String tagName) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "getNodeValueFromDocument", new Object[]{dom, tagName});
        }
        String result = "";
        try {
            if (dom != null && !StringUtils.isBlank(tagName)) {
                NodeList nodeList = null;
                Node node = null;
                nodeList = XPathAPI.selectNodeList(dom.getFirstChild(), tagName.trim());
                if (nodeList != null && nodeList.getLength() > 0) {
                    node = nodeList.item(0);
                    if (node != null) {
                        node = node.getFirstChild();
                        if (node != null){
                            result = node.getNodeValue();
                        }
                        result = result == null ? "" : result.trim();
                    }
                }
            }
        } catch (Exception e) {
            c_l.throwing(XMLUtils.class.getName(), "Fail to get the Node for tagName = " + tagName, e);
            throw new AppException("Fail to get the Node for tagName = " + tagName);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "getNodeValueFromDocument", result);
        }
        return result;
    }

    /**
     * Use Marshaller to transfer a Object to String.
     *
     * @param record
     * @param name
     * @return String
     */
    public static String marshalJaxbToXML(Object record, QName name) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(record.getClass().getName(), "marshalJaxbToXML", new Object[]{record, name});
        }
        JAXBContext jc = null;
        StringWriter writer = new StringWriter();
        try {
            jc = JAXBContext.newInstance(record.getClass().getPackage().getName());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement element = new JAXBElement(name, record.getClass(), null, record);
            marshaller.marshal(element, writer);
        } catch (Exception e) {
            c_l.logp(Level.WARNING, record.getClass().getName(), "marshalJaxbToXML", "Fail to marshalJaxbToXML: " + e.getMessage());
        }
        String result = writer.getBuffer().toString();
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(record.getClass().getName(), "marshalJaxbToXML", result);
        }
        return result;
    }

    /**
     * Convert string value to xml element.
     * @param str
     * @return
     */
    public static Element stringToElement(String str) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "stringToElement", new Object[]{str});
        }

        Element element = null;

        if (!StringUtils.isBlank(str)) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();

                element = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8"))).getDocumentElement();
            } catch (SAXException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to convert string to xml element.", e);
                c_l.throwing(XMLUtils.class.getName(), "stringToElement", ae);
                throw ae;
            } catch (IOException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to convert string to xml element.", e);
                c_l.throwing(XMLUtils.class.getName(), "stringToElement", ae);
                throw ae;
            } catch (ParserConfigurationException e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Unable to convert string to xml element.", e);
                c_l.throwing(XMLUtils.class.getName(), "stringToElement", ae);
                throw ae;
            }
        }

        return element;
    }

    public static byte[] GZIPCompress(byte[] data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "GZIPCompress", new Object[]{data});
        }
        byte[] result;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);

            gZIPOutputStream.write(data);
            gZIPOutputStream.close();

            result = byteArrayOutputStream.toByteArray();
        } catch(IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to compress the data.", e);
            c_l.throwing(XMLUtils.class.getName(), "GZIPCompress", ae);
            throw ae;
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "GZIPCompress", result);
        }
        return result;
    }

    public static byte[] GZIPDecompress(byte[] data) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLUtils.class.getName(), "GZIPDecompress", new Object[]{data});
        }
        byte[] result;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gzis = new GZIPInputStream(bais);
            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader in = new BufferedReader(reader);

            String buff = "";
            String b = "";
            while ((b = in.readLine()) != null) {
                buff += b;
            }

            result = buff.getBytes();
        } catch(IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to decompress the data.", e);
            c_l.throwing(XMLUtils.class.getName(), "GZIPDecompress", ae);
            throw ae;
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLUtils.class.getName(), "GZIPDecompress", result);
        }
        return result;
    }

    private static final Logger c_l = LogUtils.getLogger(XMLUtils.class);
}
