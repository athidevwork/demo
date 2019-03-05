package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultXMLRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.XMLRecordLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the RecordSetBuilder to build an XML representation of a JDBC ResultSet,
 * returning an XML RecordSet Document with all rows from the first ResultSet if any,
 * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 4, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class XMLRecordSetBuilder extends RecordSetBuilder {
    public static XMLRecordSetBuilder getXMLInstance(ResultSet resultSet, Vector rsColumnDescVector, Record summaryRecord) {
        return new XMLRecordSetBuilder(resultSet, rsColumnDescVector, summaryRecord);
    }

    public Document buildToXML(Node outputConfigXML, XMLRecordLoadProcessor xmlLoadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildToXML", new Object[]{DOMUtils.formatNode(outputConfigXML), xmlLoadProcessor});
        }

        Document resultDoc = null;

        // Make a deep copy of the output configuration Document to the result document
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer xf = tf.newTransformer();
            DOMResult dr = new DOMResult();
            if (Node.DOCUMENT_NODE == outputConfigXML.getNodeType()){
                outputConfigXML = ((Document) outputConfigXML).getDocumentElement();
            }
            xf.transform(new DOMSource(DOMUtils.getFirstChildElementNode(outputConfigXML)), dr);
            resultDoc = (Document) dr.getNode();
        }
        catch (TransformerException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to copy the outputConfigXML to a new Document", e);
            l.throwing(getClass().getName(), "buildToXML", ae);
            throw ae;
        }

        Node rootResultNode = DOMUtils.getFirstChildElementNode(resultDoc);
        processOutput(resultDoc, rootResultNode, xmlLoadProcessor, 1);

        // Remove the "xmlns:spdao" namespace attribute
        if (rootResultNode.hasAttributes()) {
            NamedNodeMap attrMap = rootResultNode.getAttributes();
            for (int i = 0; i < attrMap.getLength(); i++) {
                Node attrNode = attrMap.item(i);
                if (XMLStoredProcedureDAO.SPDAO_NS_URI.equals(attrNode.getNodeValue())) {
                    attrMap.removeNamedItem(attrNode.getNodeName());
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildToXML", DOMUtils.formatNode(resultDoc));
        }
        return resultDoc;
    }

    private void processOutput(Document resultDoc, Node resultNode, XMLRecordLoadProcessor xmlLoadProcessor, int level) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOutput", new Object[]{DOMUtils.formatNode(resultDoc), DOMUtils.formatNode(resultNode)});
        }

        if (XMLStoredProcedureDAO.SPDAO_NS_URI.equals(resultNode.getNamespaceURI()) &&
            "row-iterator".equals(resultNode.getLocalName())) {
            m_rowIteratorNode = resultNode;
        }
        else {
            NodeList nl = resultNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node childNode = nl.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    String columnName = childNode.getLocalName();
                    if (XMLStoredProcedureDAO.SPDAO_NS_URI.equals(childNode.getNamespaceURI()) &&
                        "get-remaining-columns".equals(columnName)) {
                        // Save this node for processing later
                        m_remainingParamsNode = childNode;
                    }
                    else {
                        if (childNode.hasAttributes()) {
                            NamedNodeMap attrs = childNode.getAttributes();
                            Node columnNameAttr = attrs.getNamedItemNS(XMLStoredProcedureDAO.SPDAO_NS_URI, "get-column");
                            if (columnNameAttr != null) {
                                columnName = columnNameAttr.getNodeValue();
                                attrs.removeNamedItemNS(XMLStoredProcedureDAO.SPDAO_NS_URI, "get-column");
                            }
                        }

                        // get the value of the column
                        if (getSummaryRecord().hasField(columnName)) {
                            String value = getSummaryRecord().getStringValue(columnName, "");
                            childNode.appendChild(resultDoc.createTextNode(value));
                            addProcessedParam(columnName);
                        }

                        // Recursively traverse descendant nodes
                        if (childNode.hasChildNodes()) {
                            processOutput(resultDoc, childNode, xmlLoadProcessor, level + 1);
                        }
                    }
                }
            }
            if (level == 1) {
                if (m_remainingParamsNode != null) {
                    // Only process the remaining params at the end of processing all summary elements
                    Iterator fieldNames = getSummaryRecord().getFieldNameList().iterator();
                    while (fieldNames.hasNext()) {
                        String fieldName = (String) fieldNames.next();
                        if (!hasProcessedParam(fieldName)) {
                            Element newNode = resultDoc.createElementNS("", fieldName);
                            newNode.appendChild(resultDoc.createTextNode(getSummaryRecord().getStringValue(fieldName, "")));
                            m_remainingParamsNode.getParentNode().insertBefore(newNode, m_remainingParamsNode);
                        }
                    }
                    m_remainingParamsNode.getParentNode().removeChild(m_remainingParamsNode);
                }

                // Process Row Iterator after all output params
                if (m_rowIteratorNode != null) {
                    processRowIterator(resultDoc, m_rowIteratorNode, xmlLoadProcessor);
                }

                // Post Process the Output
                xmlLoadProcessor.postProcessRecordSet(resultNode);
            }
        }


        l.exiting(getClass().getName(), "processOutput");
    }

    private void processRowIterator(Document resultDoc, Node rowIteratorNode, XMLRecordLoadProcessor xmlLoadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processRowIterator", new Object[]{resultDoc, rowIteratorNode});
        }

        int cursorRowCount = 0;
        if (hasResultSet()) {
            while (next()) {
                cursorRowCount++;
                Node rowIteratorNodeRow = resultDoc.importNode(rowIteratorNode, true);
                boolean acceptRow = processRow(resultDoc, rowIteratorNodeRow, xmlLoadProcessor, 1);
                if (acceptRow) {
                    NodeList nl = rowIteratorNodeRow.getChildNodes();
                    while (nl.getLength() > 0) {
                        Node childNode = nl.item(0);
                        rowIteratorNode.getParentNode().insertBefore(childNode, rowIteratorNode);
                    }
                }
            }
        }
        rowIteratorNode.getParentNode().removeChild(rowIteratorNode);

        l.logp(Level.FINE, getClass().getName(), "processRowIterator", "Processed " + cursorRowCount + " rows.");

        l.exiting(getClass().getName(), "processRowIterator");
    }

    private boolean processRow(Document resultDoc, Node rowIteratorNode, XMLRecordLoadProcessor xmlLoadProcessor, int level) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processRow", new Object[]{resultDoc, rowIteratorNode});
        }

        boolean acceptRow = true;
        if (level == 1) {
            // Starting a new row.
            // Reset the processed columns map for this new row
            clearProcessedColumns();
            m_remainingColumnsNode = null;
        }

        NodeList nl = rowIteratorNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node childNode = nl.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                String columnName = childNode.getLocalName();
                if (XMLStoredProcedureDAO.SPDAO_NS_URI.equals(childNode.getNamespaceURI()) &&
                    "get-remaining-columns".equals(columnName)) {
                    // Save this node for processing later
                    m_remainingColumnsNode = childNode;
                }
                else {
                    if (childNode.hasAttributes()) {
                        NamedNodeMap attrs = childNode.getAttributes();
                        Node columnNameAttr = attrs.getNamedItemNS(XMLStoredProcedureDAO.SPDAO_NS_URI, "get-column");
                        if (columnNameAttr != null) {
                            columnName = columnNameAttr.getNodeValue();
                            attrs.removeNamedItemNS(XMLStoredProcedureDAO.SPDAO_NS_URI, "get-column");
                        }
                    }

                    // get the value of the column
                    if (hasColumnDescFor(columnName)) {
                        String value = getStringColumnValue(columnName, "");
                        childNode.appendChild(resultDoc.createTextNode((String) value));
                        addProcessedColumn(columnName);
                    }

                    // Recursively traverse descendant nodes
                    if (childNode.hasChildNodes()) {
                        acceptRow = processRow(resultDoc, childNode, xmlLoadProcessor, level + 1);
                    }
                }
            }
        }
        if (level == 1) {
            if (m_remainingColumnsNode != null) {
                // Only process the remaining columns at the end of processing all row-iterator elements
                Iterator columns = getRsColumnDescVector().iterator();
                while (columns.hasNext()) {
                    ColumnDesc c = (ColumnDesc) columns.next();
                    if (!hasProcessedColumn(c.javaColumnName)) {
                        Element newNode = resultDoc.createElementNS("", c.javaColumnName);
                        String value = getStringColumnValue(c.javaColumnName, "");
                        newNode.appendChild(resultDoc.createTextNode(value));
                        m_remainingColumnsNode.getParentNode().insertBefore(newNode, m_remainingColumnsNode);
                    }
                }
                m_remainingColumnsNode.getParentNode().removeChild(m_remainingColumnsNode);
            }

            // Post Process the row
            acceptRow = xmlLoadProcessor.postProcessRecord(rowIteratorNode);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processRow", Boolean.valueOf(acceptRow));
        }
        return acceptRow;
    }

    private String getStringColumnValue(String columnName, String nullValue) {
        ColumnDesc c = getColumnDescFor(columnName);
        Object value = getResultSet().getColumnValue(c);
        // Use the FieldMapper to convert to value with an optionally configured Type Converter
        FieldMapper fieldMapper = (FieldMapper) getFieldMappers().get(c.colNumber - 1);
        value = fieldMapper.convert(value);
        value = (String) m_stringConverter.convert(value, nullValue);
        return (String) value;
    }

    protected XMLRecordSetBuilder(ResultSet resultSet, Vector rsColumnDescVector, Record summaryRecord) {
        super(resultSet, rsColumnDescVector, summaryRecord);
    }


    protected void clearProcessedParams() {
        m_processedParams.clear();
    }

    protected void addProcessedParam(String paramName) {
        m_processedParams.put(paramName.toUpperCase(), paramName);
    }

    protected boolean hasProcessedParam(String paramName) {
        return m_processedParams.containsKey(paramName.toUpperCase());
    }

    protected void clearProcessedColumns() {
        m_processedColumns.clear();
    }

    protected void addProcessedColumn(String columnName) {
        m_processedColumns.put(columnName.toUpperCase(), columnName);
    }

    protected boolean hasProcessedColumn(String columnName) {
        return m_processedColumns.containsKey(columnName.toUpperCase());
    }

    private Node m_rowIteratorNode = null;
    private Map m_processedParams = new HashMap();
    private Node m_remainingParamsNode = null;
    private Map m_processedColumns = new HashMap();
    private Node m_remainingColumnsNode = null;
    private final Logger l = LogUtils.getLogger(getClass());

    private static Converter m_stringConverter = ConverterFactory.getInstance().getConverter(String.class);
    private static XMLRecordLoadProcessor c_defaultLoadProcessor = new DefaultXMLRecordLoadProcessor();
}
