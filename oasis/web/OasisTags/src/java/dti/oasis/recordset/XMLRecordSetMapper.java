package dti.oasis.recordset;

import dti.oasis.app.AppException;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionIds;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.tags.XMLGridHeader;
import dti.oasis.util.*;
import org.apache.struts.Globals;
import org.apache.struts.util.ResponseUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Map between XML data and RecordSet.
 * When mapping from XML to a RecordSet, the provided rowIdFieldName is used as the field name for the ROW's 'id' attribute value,
 * and the field is mapped last, in case the ROW already has a column that uses that name.
 * That way, the ROW's 'id' attribute takes precidence over any column value with a matching field name.
 * <p/>
 * While mapping the columns, if the column name begins with a 'C' character, it is stripped off.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 24, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/30/2007       sxm         Modified XMLRecordSetMapper() to handle record with ID -9999
 * 02/08/2010       James       Issue#103608 This issue pertains to Production. Unable to add
 *                              a risk with french accents in the name. The system received an
 *                              unexpected error. When the accents were removed in CIS, we were
 *                              able to add the risk. We need to have the ability to add name
 *                              with accents.
 * 03/17/2011       fcb         112664 & 107021: mapXMLExactly added.
 * 09/20/2012       jxgu        133982 support special character in OBR_ENFORCED_RESULT
 * 08/07/2013       Parker      Issue#134836: Use the lov in detail filed to display the value when the field in Grid is not set lov
 * 10/06/2014       awu         157694 - 1. Modified getxmldata to get the data from records by column name instead of index.
 *                                       2. Change system.out to java FINEST level logging.
 * 01/10/2016       mlm         181684 - Refactored to enforce change for issue 134836 only for fields hidden in grid, but visible in detail section.
 * 03/19/2018       cesar       189605 - Modified map() to send the current token back to the browser.
 * 11/13/2018       wreeder     196147 - Update to use PrintWriter instead of OutputStream
 * 12/10/2018       cesar       197486 - moved sending back the token back to client to BaseAction.
 * ---------------------------------------------------
 */
public class XMLRecordSetMapper implements Mapper {

    public static XMLRecordSetMapper getInstance() {
        return new XMLRecordSetMapper();
    }

    public static XMLRecordSetMapper getInstance(String rowIdFieldName) {
        return new XMLRecordSetMapper(rowIdFieldName);
    }

    public void map(String xmlSource, RecordSet recordSetTarget) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{xmlSource, recordSetTarget});
        }

        // Create an XML DocumentBuilder for each mapping since it is not thread safe.
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
        docBuilderFac.setValidating(false);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFac.newDocumentBuilder();

        }
        catch (ParserConfigurationException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to create a XML DocumentBuilder.", e);
            l.throwing(getClass().getName(), "map", ae);
            throw ae;
        }

        // Parse the xml source
        Document doc = null;
        try {
            doc = docBuilder.parse(new InputSource(new StringReader(xmlSource)));

            // TODO: Is normalize required?
//            doc.normalize();

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the xmlSource: " + xmlSource, e);
            l.throwing(getClass().getName(), "map", ae);
            throw ae;
        }

        try {
            org.apache.xpath.CachedXPathAPI xpathapi = new org.apache.xpath.CachedXPathAPI();
            NodeList rowNodeList = xpathapi.selectNodeList(doc, "ROWS/ROW");
            l.logp(Level.FINE, getClass().getName(), "map", "Found " + rowNodeList.getLength() + " ROW nodes");

            for (int rowIdx = 0; rowIdx < rowNodeList.getLength(); rowIdx++) {
                Node rowNode = rowNodeList.item(rowIdx);
                Record record = new Record();
                Map lovLabelFieldsMap = new HashMap();
                // Iterate through the columns, creating a field for each named column
                NodeList columnNodeList = xpathapi.selectNodeList(rowNode, "*");
                l.logp(Level.FINE, getClass().getName(), "map", "Found " + columnNodeList.getLength() + " columns ");
                for (int colIdx = 0; colIdx < columnNodeList.getLength(); colIdx++) {
                    Node columnNode = columnNodeList.item(colIdx);

                    // Strip the 'C' prefix if it exists
                    String xmlNodeName = columnNode.getNodeName();
                    String columnName = columnNode.getNodeName();
                    if (columnName.startsWith("C")) {
                        columnName = columnName.substring(1);
                    }

                    // Handle the UPDATE_IND, DISPLAY_IND and EDIT_IND separately
                    if (xmlNodeName.equalsIgnoreCase("UPDATE_IND")) {
                        record.setUpdateIndicator(getNodeTextValue(columnNode));
                    }
                    else if (xmlNodeName.equalsIgnoreCase("DISPLAY_IND")) {
                        record.setDisplayIndicator(getNodeTextValue(columnNode));
                    }
                    else if (xmlNodeName.equalsIgnoreCase("EDIT_IND")) {
                        record.setEditIndicator(getNodeTextValue(columnNode));
                    }
                    else if (xmlNodeName.equalsIgnoreCase("OBR_ENFORCED_RESULT")) {
                        //skip OBR_ENFORCED_RESULT from client
                    }
                    else if (xmlNodeName.startsWith("DATE_") || xmlNodeName.startsWith("URL_")) {
                        continue;
                    }else if(columnName.endsWith("LOVLABEL")&&!lovLabelFieldsMap.containsKey(columnName)){
                        //set lov label fields map, the value will be set at the last
                        lovLabelFieldsMap.put(columnName,getNodeTextValue(columnNode));
                    }
                    else {
                        // Set the column value as a Field
                        record.setFieldValue(columnName, getNodeTextValue(columnNode));
                    }
                }

                //set lov label fields' value here. It will help to keep the column order.
                Iterator lovFldsIter = lovLabelFieldsMap.entrySet().iterator();;
                while(lovFldsIter.hasNext()){
                    Map.Entry lovFldEntry = (Map.Entry) lovFldsIter.next();
                    record.setFieldValue((String)lovFldEntry.getKey(), lovFldEntry.getValue());
                }

                // Set the Record id last in case the rowIdFieldName already exists as a column and must be overridden.
                Node rowId = rowNode.getAttributes().getNamedItem("id");
                if (rowId == null || StringUtils.isBlank(rowId.getNodeValue())) {
                    AppException ae = new AppException("Each ROW must contain a 'id' attribute.");
                    l.throwing(getClass().getName(), "map", ae);
                    throw ae;
                }
                record.setRowId(rowId.getNodeValue());
                record.setFieldValue(getRowIdFieldName(), rowId.getNodeValue());

                // Add the field names only if this is "dummy" record, add the record otherwise.
                if (rowId.getNodeValue().equals("-9999"))
                    recordSetTarget.addFieldNameCollection(record.getFieldNames());
                else
                    recordSetTarget.addRecord(record);
            }
        }
        catch (TransformerException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Invalid XML format. The XML string must contain <ROWS> as the root element, with 0-* nesteded <ROW> element, each containing 0-* <CXXX> elements where XXX is the column name.", e);
            l.throwing(getClass().getName(), "map", ae);
            throw ae;
        }

        l.logp(Level.FINE, getClass().getName(), "map", "recordSetTarget = " + recordSetTarget);

        l.exiting(getClass().getName(), "map");
    }

    /**
     * Method to parse an input XML that comes in <ROWS><ROW>...</ROW></ROWS> format without doing the
     * specialized logic done in map function above, related to _IND, LOVLABEL, DATE, etc.
     * mapXMLExactly maps the input XML to the RecordSet exactly the way the XML elements are.
     * @param xmlSource
     * @param recordSetTarget
     */
    public void mapXMLExactly(String xmlSource, RecordSet recordSetTarget) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapXMLExactly", new Object[]{xmlSource, recordSetTarget});
        }

        // Create an XML DocumentBuilder for each mapping since it is not thread safe.
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
        docBuilderFac.setValidating(false);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFac.newDocumentBuilder();

        }
        catch (ParserConfigurationException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to create a XML DocumentBuilder.", e);
            l.throwing(getClass().getName(), "mapXMLExactly", ae);
            throw ae;
        }

        // Parse the xml source
        Document doc = null;
        try {
            doc = docBuilder.parse(new InputSource(new StringReader(xmlSource)));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the xmlSource: " + xmlSource, e);
            l.throwing(getClass().getName(), "mapXMLExactly", ae);
            throw ae;
        }

        try {
            org.apache.xpath.CachedXPathAPI xpathapi = new org.apache.xpath.CachedXPathAPI();
            NodeList rowNodeList = xpathapi.selectNodeList(doc, "ROWS/ROW");
            l.logp(Level.FINE, getClass().getName(), "mapXMLExactly", "Found " + rowNodeList.getLength() + " ROW nodes");

            for (int rowIdx = 0; rowIdx < rowNodeList.getLength(); rowIdx++) {
                Node rowNode = rowNodeList.item(rowIdx);
                Record record = new Record();
                Map lovLabelFieldsMap = new HashMap();
                // Iterate through the columns, creating a field for each named column
                NodeList columnNodeList = xpathapi.selectNodeList(rowNode, "*");
                l.logp(Level.FINE, getClass().getName(), "mapXMLExactly", "Found " + columnNodeList.getLength() + " columns ");
                for (int colIdx = 0; colIdx < columnNodeList.getLength(); colIdx++) {
                    Node columnNode = columnNodeList.item(colIdx);
                    String columnName = columnNode.getNodeName();
                    record.setFieldValue(columnName, getNodeTextValue(columnNode));
                }
                recordSetTarget.addRecord(record);
            }
        }
        catch (TransformerException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Invalid XML format. The XML string must contain <ROWS> as the root element, with 0-* nesteded <ROW> element, each containing 0-* <CXXX> elements where XXX is the column name.", e);
            l.throwing(getClass().getName(), "mapXMLExactly", ae);
            throw ae;
        }

        l.logp(Level.FINE, getClass().getName(), "mapXMLExactly", "recordSetTarget = " + recordSetTarget);

        l.exiting(getClass().getName(), "mapXMLExactly");
    }

    private String getNodeTextValue(Node node) {
        String value = null;
        if (node.hasChildNodes()) {
            value = node.getChildNodes().item(0).getNodeValue();
        }
        return value;
    }

    /**
     * Default map
     *
     * @param recordSetSource
     * @param out
     */
    public void map(RecordSet recordSetSource, PrintWriter out) {
        map(recordSetSource, out, false);
    }

    /**
     * Convert recordSet to XML
     *
     * @param recordSetSource
     * @param out
     */
    public void map(RecordSet recordSetSource, PrintWriter out, boolean keepCase) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{recordSetSource, out});
        }

        final String strRowsOpen = "<ROWS>";
        final String strRowsClose = "</ROWS>";
        final String strRowOpen = "<ROW>";
        final String strRowClose = "</ROW>";
        final String strTab = "  ";
        final String strOpen = "<";
        final String strClose = ">";
        final String strEndOpen = "</";
        final String strEndClose = "/>";

        try {
            ArrayList columns = (ArrayList) recordSetSource.getFieldNameList();
            out.print(strRowsOpen);
            out.println();
            boolean isForOBROnChange = false;
            if (RequestStorageManager.getInstance().has("RecordSetForOBROnChange")) {
                isForOBROnChange = (Boolean) RequestStorageManager.getInstance().get("RecordSetForOBROnChange");
            }
            int rowCount = recordSetSource.getSize();
            int colCount = recordSetSource.getFieldCount();
            for (int i = 0; i < rowCount; i++) {
                Record record = recordSetSource.getRecord(i);
                out.print(strRowOpen);
                out.println();
                Set<String> uppercaseChangeFieldNames = new HashSet<String>();
                for (String name : record.getChangedFieldsInRule()) {
                    uppercaseChangeFieldNames.add(name.toUpperCase());
                }
                // Loop throw columns
                for (int j = 0; j < colCount; j++) {
                    String colName = (String) columns.get(j);
                    if (isForOBROnChange && !"ID".equals(colName) && !uppercaseChangeFieldNames.contains(colName.toUpperCase())) {
                        continue;
                    }
                    if (!StringUtils.isBlank(colName)) {
                        String dataItem = record.getStringValue(j, null);
                        // tag start
                        out.print(strTab);
                        out.print(strOpen);
                        if (keepCase) {
                            out.print(colName.trim());
                        }
                        else {
                            out.print(colName.trim().toUpperCase());
                        }
                        if (dataItem != null) {
                            out.print(strClose);
                            out.print((dataItem.indexOf('&') > -1 || dataItem.indexOf('<') > -1) ?
                                encode(dataItem) : dataItem);
                            /* closing tag */
                            out.print(strEndOpen);
                            if (keepCase) {
                                out.print(colName.trim());
                            }
                            else {
                                out.print(colName.trim().toUpperCase());
                            }
                            out.print(strClose);
                            out.println();
                        }
                        else {
                            out.print(strEndClose);
                            out.println();
                        }
                    }
                }

                if (!isForOBROnChange){
                    // Write the UPDATE_IND, DISPLAY_IND and EDIT_IND attributes separately
                    out.print(strTab);
                    out.print(strOpen); out.print("UPDATE_IND"); out.print(strClose);
                    out.print(record.getUpdateIndicator());
                    out.print(strEndOpen); out.print("UPDATE_IND"); out.print(strClose);
                    out.println();

                    out.print(strTab);
                    out.print(strOpen); out.print("DISPLAY_IND"); out.print(strClose);
                    out.print(record.getDisplayIndicator());
                    out.print(strEndOpen); out.print("DISPLAY_IND"); out.print(strClose);
                    out.println();

                    out.print(strTab);
                    out.print(strOpen); out.print("EDIT_IND"); out.print(strClose);
                    out.print(record.getEditIndicator());
                    out.print(strEndOpen); out.print("EDIT_IND"); out.print(strClose);
                    out.println();
                }

                out.print(strTab);
                out.print(strOpen); out.print("OBR_ENFORCED_RESULT"); out.print(strClose);
                out.print(record.getOBREnforcedResult());
                out.print(strEndOpen); out.print("OBR_ENFORCED_RESULT"); out.print(strClose);
                out.println();

                // End looping through columns
                out.print(strRowClose);
                out.println();
            }

            // End loop throw rows
            out.print(strRowsClose);
            out.println();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate the xmlTarget: "
                + recordSetSource, e);
            l.throwing(getClass().getName(), "map", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "map");
    }


    /**
     * Convert recordSet to XML: Used to Reftesh the entire grid
     *
     * @param request
     * @param rs
     * @param gridId
     * @param out
     */
    public void map(HttpServletRequest request, RecordSet rs, String gridId, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "map", new Object[]{request, rs, gridId, out});
        }

        try {
            out.print(getxmldata(request, rs, gridId));
            out.println();
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate the xmlTarget for gridId: "
                    + gridId + " " + rs, e);
            l.throwing(getClass().getName(), "map", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "map");
    }

    /**
     * A version OasisGrid getxmldata method. Generates an XML String from RecordSet to reload the Grid
     * @param request
     * @param rs
     * @param gridId
     * @throws Exception
     */
    private String getxmldata(HttpServletRequest request, RecordSet rs, String gridId) throws Exception {
        l.entering(getClass().getName(), "getxmldata");
        BaseResultSet data = BaseResultSetRecordSetAdaptor.getInstance(rs);
        String gridHeaderBeanName =  gridId + "HeaderBean";
        if (l.isLoggable(Level.FINEST)) {
            if (data != null) {
                l.finest(this.getClass().getName() + " data.getColumnCount: " + data.getColumnCount());
                for (int i = 1; i <= data.getColumnCount(); i++) {
                    l.finest(this.getClass().getName() + " data.getColumnName1(" + i + "): " + data.getColumnName(i));
                }
            }
            else {
                l.finest(this.getClass().getName() + " data is NULL");
            }
            l.finest(this.getClass().getName() + " gridHeaderBeanName: " + gridHeaderBeanName);
        }

        XMLGridHeader header = (XMLGridHeader)request.getAttribute(gridHeaderBeanName);
        if(header != null) {
            if (l.isLoggable(Level.FINEST)) {
                l.finest(this.getClass().getName() + " header.getAnchorColumnName: " + header.getAnchorColumnName());
                l.finest(this.getClass().getName() + " header.size: " + header.size());
                l.finest(this.getClass().getName() + " header.isInitialized: " + header.isInitialized());
            }
            header.processDataColumns(data);  //Added to comply with OASIS_GRID
            for (int i = 1; i <= header.size(); i++) {
                HashMap headerMap = header.getHeaderMap(i);
                int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
                Integer iD = (Integer) headerMap.get(XMLGridHeader.CN_DISPLAY);
                int display = (iD == null) ? 0 : iD.intValue();
                String name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                String fieldId = (String) headerMap.get(XMLGridHeader.CN_FIELDID);
                String visible = (String) headerMap.get(XMLGridHeader.CN_VISIBLE);
                if (l.isLoggable(Level.FINEST)) {
                    l.finest(this.getClass().getName() + ": Column:(" + i + ") FieldId: " + fieldId + " Name: " + name
                        + " Type: " + type + " Display: " + display + " Visible: " + visible);
                }
            String dataColumnName = (String) headerMap.get(XMLGridHeader.CN_DATACOLUMNNAME);
            int dataColumnIdx = header.getDataColumnIndex(dataColumnName);

            type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
            String sColName = getDataIslandColumnName(data, dataColumnIdx);
                 headerMap.put(XMLGridHeader.CN_NAME, sColName); //Added to comply with OASIS_GRID
            }
        }
        else {
            if (l.isLoggable(Level.FINEST)) {
                l.finest(this.getClass().getName() + " header is NULL");
            }
        }

        String updateColumns = (String) request.getAttribute("updateColumns");
        StringBuffer strXML = new StringBuffer("");
        StringBuffer strXMLDate;
        String strTab = "  ";     
        boolean bDateInd, bDateTimeInd, bUrlInd, isDataPresent;

        int colCount = data.getColumnCount();

        StringBuffer xmlData = null;

        //util.write(pageContext, "<ROWS>\n");
        strXML.append("<ROWS>");
        data.beforeFirst();
        // Start loop through rows
        int rowIndex = -1;
        while (data.next()) {
            // row header
            strXML.append("<ROW id=\"").
                    append( (StringUtils.isBlank(data.getString(header.getDataColumnIndexForAnchorColumn())) ? "-9999" : data.getString(header.getDataColumnIndexForAnchorColumn())) ).
                    append("\" index=\"").append(++rowIndex).
                    append("\" col=\"").append(updateColumns).append("\" >");  ///  sUpdtCol is not used
            strXMLDate = new StringBuffer();

            // Start loop through columns
            int headerIdx = 0;
            for (int i = 1; i <= colCount; i++) {

                String dataColumnName = data.getColumnName(i);

                // skip xxxLOVLABEL columns
                if (dataColumnName.endsWith("LOVLABEL"))
                    continue;

                // skip xxx_FORMATTED columns
                if (dataColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                    continue;

                bDateInd = false;
                bDateTimeInd = false;
                bUrlInd = false;
                if (l.isLoggable(Level.FINEST)) {
                    l.finest(this.getClass().getName() + " data.getColumnName2(" + i + "): " + data.getColumnName(i));
                }
                headerIdx = header.getHeaderIndex(data.getColumnName(i)).intValue();
                String dataItem = data.getString(dataColumnName, "");
                if (l.isLoggable(Level.FINEST)) {
                    l.finest(this.getClass().getName() + " data.getString(" + i + "): " + data.getString(i));
                }
                isDataPresent = (dataItem != null && dataItem.trim().length() > 0);

                int xmlColumnIdx = header.getXmlColumnIndex(dataColumnName);

                HashMap headerMap = header.getHeaderMap(headerIdx);
                int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
                Integer iD = (Integer) headerMap.get(XMLGridHeader.CN_DISPLAY);
                int display = (iD == null) ? 0 : iD.intValue();
                String name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                ArrayList lov = (ArrayList) headerMap.get(XMLGridHeader.CN_LISTDATA);
                String fieldId = (String) headerMap.get(XMLGridHeader.CN_FIELDID);
                // if lov is null and we have a fieldId, look it up
                if (lov == null && fieldId != null)
                    lov = (ArrayList)request.getAttribute(fieldId + "LOV");
                if (lov == null && headerMap.get(XMLGridHeader.CN_DETAIL_FIELDID) != null) {
                    String detailFieldId = (String) headerMap.get(XMLGridHeader.CN_DETAIL_FIELDID);
                    if (!StringUtils.isBlank(detailFieldId)) {
                        lov = (ArrayList) request.getAttribute(detailFieldId + "LOV");
                    }
                }

                String iDec = (String) headerMap.get(XMLGridHeader.CN_DECIMALPLACES);
                boolean isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();

                // If this field is protected, set the data item to null as it should not appear in the xml
                if (isProtected)
                    dataItem = "";
                if (!StringUtils.isBlank(iDec))
                    if (!FormatUtils.isLong(iDec))
                        iDec = null;

                //Pattern
                String pattern = (String) headerMap.get(XMLGridHeader.CN_PATTERN);
                if(pattern==null)
                    pattern = "";               
                // tag start
                strXML.append(strOpen).append(name.trim().toUpperCase()).
                        append(strClose);

                // Start visible items
                if (!headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
                    switch (type) {
                        //Percentage field type
                        case XMLGridHeader.TYPE_PERCENTAGE:
                        case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatPercentage(dataItem));
                            else
                                strXML.append(FormatUtils.formatPercentage(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_NUMBER:
                        case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                            if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                                strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(FormatUtils.formatNumber(dataItem,pattern));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            } else {
                                if (isDataPresent) {
                                        strXML.append(ResponseUtils.filter(dataItem));
                                }
                            }
                            break;                        
                        case XMLGridHeader.TYPE_PHONE:
                        case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                            strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                            strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                            strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                append(strClose);
                            strXML.append(FormatUtils.formatPhoneNumberForDisplay(dataItem));
                            name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            break;
                        case XMLGridHeader.TYPE_FORMATMONEY:
                        case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatCurrency(dataItem));
                            else
                                strXML.append(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_FORMATDATE:
                        case XMLGridHeader.TYPE_DATE:
                        case XMLGridHeader.TYPE_UPDATEONLYDATE: //TODO: Figure out conditions
                            java.util.Date dte = data.getDate(i);
                            strXML.append(OasisTagHelper.formatDateAsXml(dte));
                            if(!FormatUtils.isDateFormatUS()) {
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateAsXml(dte));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            }
                            bDateInd = true;
                            break;
                        case XMLGridHeader.TYPE_FORMATDATETIME:
                        case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                            java.util.Date dtTime = data.getDate(i);
                            strXML.append(OasisTagHelper.formatDateTimeAsXml(dtTime));
                            if(!FormatUtils.isDateFormatUS()) {
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateTimeAsXml(dtTime));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            }
                            bDateTimeInd = true;
                            break;
                        case XMLGridHeader.TYPE_URL:
                        case XMLGridHeader.TYPE_UPDATEONLYURL:
                            bUrlInd = true;
                            if (isDataPresent) {
                                if (display == XMLGridHeader.DISPLAY_MONEY) {
                                    if (iDec == null)
                                        strXML.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem)));
                                    else
                                        strXML.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec))));
                                }
                                else
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }

                            break;
                        case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);

                                // tag start for LOVLABEL
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValue(lov, dataItem);
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
                                name += "LOVLABEL";
                            }
                            // Populate url column in data island if there's href defined for this drop down list field
                            if (!StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                                bUrlInd = true;
                            }
                            break;
                        default :
                            if (isDataPresent) {
                                // if a list of values is present for a readonly field, decode
                                if (type == XMLGridHeader.TYPE_DEFAULT && lov != null)
                                    strXML.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                                else if (!FormatUtils.isDateFormatUS() && FormatUtils.isDate(dataItem))
                                    strXML.append(ResponseUtils.filter(FormatUtils.formatDateForDisplay(dataItem)));
                                else
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }
                            break;
                    }
                }
                // End Visible items
                // Start hidden items
                else {
                    switch (type) {
                        //Percentage field type
                         case XMLGridHeader.TYPE_PERCENTAGE:
                         case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatPercentage(dataItem));
                            else
                                strXML.append(FormatUtils.formatPercentage(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_NUMBER:
                        case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                            if (display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                                strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                        append(strClose);
                                strXML.append(FormatUtils.formatNumber(dataItem, pattern));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }
                            break;
                        case XMLGridHeader.TYPE_PHONE:
                        case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                            strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                            strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                            strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                append(strClose);
                            strXML.append(FormatUtils.formatPhoneNumberForDisplay(dataItem));
                            name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            break;
                        case XMLGridHeader.TYPE_FORMATMONEY:
                        case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatCurrency(dataItem));
                            else
                                strXML.append(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_ANCHOR :
                            if (isDataPresent)
                                strXML.append("javascript:selectRowWithProcessingDlg('").append(gridId).
                                    append("','").append(ResponseUtils.filter(dataItem).
                                    replaceAll("'", "''")).append("');");
                            else
                                strXML.append("javascript:selectRowWithProcessingDlg('").append(gridId).
                                    append("','-');");
                            break;
                        case XMLGridHeader.TYPE_FORMATDATE:
                        case XMLGridHeader.TYPE_DATE:
                        case XMLGridHeader.TYPE_UPDATEONLYDATE:
                            if(!FormatUtils.isDateFormatUS()) {
                                java.util.Date dte = data.getDate(i);
                                strXML.append(OasisTagHelper.formatDateAsXml(dte));
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateAsXml(dte));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                bDateInd = true;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }
                            break;
                        case XMLGridHeader.TYPE_FORMATDATETIME:
                        case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                            if(!FormatUtils.isDateFormatUS()) {
                                java.util.Date dte = data.getDate(i);
                                strXML.append(OasisTagHelper.formatDateTimeAsXml(dte));
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateTimeAsXml(dte));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                bDateInd = true;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(FormatUtils.formatDateTime(data.getDate(i))));
                            }
                            break;
                        case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);

                                // tag start for LOVLABEL
                                strXML.append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValue(lov, dataItem);
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
//                                strXML.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                                name += "LOVLABEL";
                            }
                            break;
                        default :
                            if (isDataPresent)
                                strXML.append(ResponseUtils.filter(dataItem));
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "getxmldata", "dataColumnName = " + dataColumnName);
                            }
                    }
                }
                // End Hidden items
                // Start special tags
                if (bDateInd) {
                    strXMLDate.append("<DATE_").append(xmlColumnIdx).append('>');
                    if (isDataPresent)
                        strXMLDate.append(DateUtils.dateDiff(DateUtils.DD_DAYS,
                                "01/01/1993", data.getDate(i)));
                    strXMLDate.append("</DATE_").append(xmlColumnIdx).append('>');
                }
                else if (bDateTimeInd) {
                    strXMLDate.append("<DATE_").append(xmlColumnIdx).append('>');
                    if (isDataPresent)
                        strXMLDate.append(DateUtils.dateDiff(DateUtils.DD_SECS,
                                "01/01/1993 00:00:00", data.getDate(i)));
                    strXMLDate.append("</DATE_").append(xmlColumnIdx).append('>');
                }
                else if (bUrlInd) {
                    String fieldHref = (String) headerMap.get(XMLGridHeader.CN_FIELD_HREF);
                    //if there is href on field in webwb, use it. Otherwise use the href in xml header file.
                    if (!StringUtils.isBlank(fieldHref)) {
                        strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                        if (isDataPresent)
                            strXMLDate.append("javascript:handleOnGridHref('").append(gridId).append("','")
                                    .append(ResponseUtils.filter(fieldHref.replaceAll("'", "\\\\'")))
                                    .append("');");
                        strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                    } else {
                        String href = (String) headerMap.get(XMLGridHeader.CN_HREF);
                        String hrefKey = (String) headerMap.get(XMLGridHeader.CN_HREFKEY);
                        String hrefKeyName = (String) headerMap.get(XMLGridHeader.CN_HREFKEYNAME);
                        String hrefKeyValue =
                                (hrefKeyName == null ? data.getString(Integer.parseInt(hrefKey)) : data.getString(hrefKeyName));
                        if (href.indexOf("javascript:") >= 0) {
                            strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                            if (isDataPresent)
                                strXMLDate.append(ResponseUtils.filter(href)).append('\'').
                                        append(hrefKeyValue).
                                        append("');");
                            strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                        } else {
                            strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                            if (isDataPresent)
                                strXMLDate.append(ResponseUtils.filter(href)).
                                        append(hrefKeyValue);
                            strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                        }
                    }
                }
                // End special tags

                // closing tag
                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose);

            }
            // End looping through columns
            strXML.append(strXMLDate).append("<UPDATE_IND>").
                    append(data.getUpdateInd()).append("</UPDATE_IND>") ;
            strXML.append("<DISPLAY_IND>").append(data.getDisplayInd()).append("</DISPLAY_IND>");
            strXML.append("<EDIT_IND>").append(data.getEditInd()).append("</EDIT_IND>");
            strXML.append("<OBR_ENFORCED_RESULT>").append(encodeWhenNecessary(data.getOBREnforcedResult())).append("</OBR_ENFORCED_RESULT>\n");
            strXML.append("</ROW>");


            //util.write(pageContext, strXML.toString());


        }
        // End looping through rows

        //util.write(pageContext, "</ROWS>\n");
        strXML.append("</ROWS>");

        l.exiting(getClass().getName(), "getxmldata");
        //return cacheKey;
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ OUTPUT @@@@@@@@@@@@@");
//        System.out.println(strXML.toString());
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@ END OUTPUT @@@@@@@@@@@@@");
        return strXML.toString();
    }

    /**
     * A version OasisGrid getDataIslandColumnName method.
     * @param data
     * @param dataColumnIndex
     * @throws Exception
     */
    private String getDataIslandColumnName(BaseResultSet data, int dataColumnIndex) {
        String sColName;
        sColName = new StringBuffer("C").append(data.getColumnName(dataColumnIndex).trim().toUpperCase().replace(']', ' ').trim().
                replace('[', ' ').trim().replace(' ', '_').replace('#', 'N').replace('/', ' ').trim().
                replace('\'', '_')).toString();
        return sColName;
    }

    /**
     * encode value if necessary
     * @param value
     * @return
     */
    public static String encodeWhenNecessary(String value) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(XMLRecordSetMapper.class.getName(), "encodeWhenNecessary", new Object[]{value});
        }

        String result = null;
        if (value != null) {
            if (value.indexOf('&') > -1 || value.indexOf('<') > -1) {
                result = encode(value);
            } else {
                result = value;
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(XMLRecordSetMapper.class.getName(), "encodeWhenNecessary", result);
        }
        return result;
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

    public String getRowIdFieldName() {
        if (m_rowIdFieldName == null) {
            throw new IllegalArgumentException("The rowIdFieldName is not set.");
        }
        return m_rowIdFieldName;
    }

    public void setRowIdFieldName(String rowIdFieldName) {
        m_rowIdFieldName = rowIdFieldName;
    }


    protected XMLRecordSetMapper() {
    }

    protected XMLRecordSetMapper(String rowIdFieldName) {
        m_rowIdFieldName = rowIdFieldName;
    }

    private String m_rowIdFieldName;
    // Private Fields
    private final Logger l = LogUtils.getLogger(getClass());
    private static Logger c_l = LogUtils.getLogger(XMLRecordSetMapper.class);
    private static final String strOpen = "<";
    private static final String strClose = ">";
    private static final String strEndOpen = "</";
}
