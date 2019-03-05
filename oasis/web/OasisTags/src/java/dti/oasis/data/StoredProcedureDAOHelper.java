package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.DefaultApplicationLifecycleHandler;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.Converter;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.log.StoredProcedureLogFormatter;
import dti.oasis.log.StoredProcedureLogLevel;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.xml.DOMUtils;
import oracle.jdbc.OracleTypes;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides helper functions for executing Stored Procedures.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 19, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/29/2008       wer         Added ability to use cs_dbms_describe to determine the procedure metadata instead of using the Database Metadata
 * 03/12/2008       yhchen      #80559 in postProcessColumnDesc, check data type, if is NUMERIC and scale is >0, set its java type to DOUBLE
 * 04/01/2008       sxm         Renamed getConnection() to getReadonlyConnection()
 * 06/16/2008       wer         Refactored for use with the new XMLStoredProcedureDAO
 *                              Refactored to work with JDK 1.5
 * 07/10/2008       yhchen      fix issue #83894
 *                              in postProcessColumnDesc() not change the dataType and dataTypeName for NUMERIC and DECIMAL
 *                              in getJavaTypeName() return "BigDecimal" for both DECIAML and NUMERIC data type
 *                              in setCallableStatementParameters() treat the NUMERIC type as a BigDecimal, calling inputRecord.getBigDecimalValue() and css.setDecimal()
 * 09/25/2009       Fred        Modified loadColumnDescriptionsFromFile to make precision and scale optional
 * 12/23/2009       James       Issue#102265 Fix the StoredProcedureDAOHelper to leave
 *                              stored procedure parameters unset if there is not Field
 *                              in the inputRecord and the parameter has a default value
 *                              setting in the stored procedure definition
 * 05/07/2010       fcb         Issue# 107308: stored procedure name passed to mapInputXMLToRecord
 *                              The stored procedure name is used when storing and retrieving values from caseInsensitiveNameMap.
 * 09/21/2010       fcb         111824 - added support for Oracle XMLType
 * 07/23/2012       tcheng      135128 - added support for Oracle Clob type
 * 07/11/2013       parker      140536 - StoredProcedureDAO doesn't work when using it to call a procedure without parameters.
 * 12/16/2013       fcb         150767 - refactored to use RefreshParmsEventListener.
 * 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
 * 11/13/2018       wreeder     196147 - Support virtualMode where and order by clauses and countB to request the total record count.
 * ---------------------------------------------------
 */
public class StoredProcedureDAOHelper extends DefaultApplicationLifecycleHandler implements RefreshParmsEventListener {
    /**
     * The name of the property to set to determine whether to use the Database Metadata
     * to discover the procedure parameters or to use dbms_describe.
     * This property defaults to true.
     */
    public static final String PROPERTY_USE_DATABASE_METADATA = "storedproceduredao.use.database.metadata";


    /**
     * Return the vector of ColumnDesc for the parameters of the given stored procedure.
     * If there are any default Output DataType Converters specified, and there are OUT parameters of the same data type,
     * corresponding DataRecordFieldMappings will be added/updated in the given DataRecordMapping
     */
    public synchronized Vector getParameterColumnDescVector(String spDaoKeyName,
                                                            StoredProcedureName spName,
                                                            DataRecordMapping dataRecordMapping) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getParameterColumnDescVector", new Object[]{spDaoKeyName, spName});
        }


        Vector paramColumnDescVector = null;

        if (m_paramColumnDescVectorMap.containsKey(spDaoKeyName)) {
            paramColumnDescVector = (Vector) m_paramColumnDescVectorMap.get(spDaoKeyName);
        } else {
            // If the parameter ColumnDesc Vector isn't cached, build it and cache it.
            paramColumnDescVector = buildParameterColumnDescVector(spName, dataRecordMapping);
            m_paramColumnDescVectorMap.put(spDaoKeyName, paramColumnDescVector);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParameterColumnDescVector", paramColumnDescVector);
        }
        return paramColumnDescVector;
    }

    /**
     * Return the vector of ColumnDesc for the result set columns of the given stored procedure.
     * If there are any default Output DataType Converters specified, and there are OUT parameters of the same data type,
     * corresponding DataRecordFieldMappings will be added/updated in the given DataRecordMapping
     */
    public synchronized Vector<ColumnDesc> getResultSetColumnDescVector(String spDaoKeyName,
                                                            ResultSet resultSet,
                                                            DataRecordMapping dataRecordMapping) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getResultSetColumnDescVector", new Object[]{spDaoKeyName});
        }

        // Do not use the cached copy because some procedures return a multiple result set formats
        Vector<ColumnDesc> rsColumnDescVector = buildResultSetColumnDescVector(resultSet, dataRecordMapping);
        // Cache the rsColumnDescVector for use when building the where and order by clacuses when in virtualmode
        m_rsColumnDescVectorMap.put(spDaoKeyName, rsColumnDescVector);
        if (l.isLoggable(Level.FINE)) {
            debugVector("Result Set ColumnDesc Vector:", rsColumnDescVector);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResultSetColumnDescVector", rsColumnDescVector);
        }
        return rsColumnDescVector;
    }

    /**
     * Setup the Callable Statement parameters from the given input record, adhering to the supplied vector of parameter ColumnDesc.
     */
    public void setCallableStatementParameters(CallableStatementSupport css, Vector paramColumnDescVector, Node inputXML, Node paramMappingXML, StoredProcedureName spName, String spDaoKeyName) throws SQLException {
        // Apply the dataRecordMapping while converting the XML to Record.
        Record inputRecord = mapInputXMLToRecord(inputXML, paramMappingXML, paramColumnDescVector, spName.getProcedureName());

        setCallableStatementParameters(css, paramColumnDescVector, null, inputRecord, spName, spDaoKeyName);
    }

    protected Record mapInputXMLToRecord(Node inputXML, Node paramMappingXML, Vector paramColumnDescVector, String spName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapInputXMLToRecord", new Object[]{DOMUtils.formatNode(inputXML), paramColumnDescVector});
        }

        // Copy the inputXML to make the search relative to the current node for org.apache.xpath.XPathAPI
        // TODO: Remove this logic to copy the node if using other XPath library that supports relative XPath
        if (inputXML.getNodeType() != Node.DOCUMENT_NODE) {
            DocumentBuilder docBuilder = DOMUtils.getDocumentBuilder();
            Document recordDocHolder = docBuilder.newDocument();
            inputXML = recordDocHolder.importNode(inputXML, true);
        }

        Record inputRecord = new Record();

        final Node spdaoContextNode = DOMUtils.getNamespaceResolverNode(XMLStoredProcedureDAO.SPDAO_NS_PREFIX, XMLStoredProcedureDAO.SPDAO_NS_URI);

        for (int i = 0; i < paramColumnDescVector.size(); i++) {
            ColumnDesc c = (ColumnDesc) paramColumnDescVector.elementAt(i);
            String fieldXPathExpr = null;
            String value = null;

            if ((c.columnType == ColumnDesc.ColumnType.IN || c.columnType == ColumnDesc.ColumnType.INOUT) && !isCursorSQLType(c)) {
                try {
                    Node xmlField = null;

                    // Look for a spdao:param element mapping for this parameter
                    if (paramMappingXML != null) {
                        String paramMappingXPathExpr = ".//" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":param[@name='" + c.javaColumnName + "']";
                        xmlField = org.apache.xpath.XPathAPI.selectSingleNode(paramMappingXML, paramMappingXPathExpr, spdaoContextNode);
                        if (xmlField != null) {
                            NamedNodeMap xmlFieldAttrs = xmlField.getAttributes();
                            fieldXPathExpr = DOMUtils.getNodeTextValue(xmlFieldAttrs.getNamedItem("select"));
                        }
                    }
                    if (!StringUtils.isBlank(fieldXPathExpr)) {
                        // Found a param mapping for this parameter
                        l.logp(Level.FINE, getClass().getName(), "mapInputXMLToRecord", "Found a param mapping for parameter \"" + c.javaColumnName + "\" to input with XPath \"" + fieldXPathExpr + "\"");

                        // Look for the element with fieldXPathExpr
                        value = getElementValueAsString(fieldXPathExpr, inputXML);
                    }
                    else {
                        // Use the parameter name to locate a matchimg element
                        String fieldName = c.javaColumnName;
                        fieldXPathExpr = ".//" + fieldName;
                        value = getElementValueAsString(fieldXPathExpr, inputXML);

                        if (value == null /* && no paramMappingXML defined for this field */) {
                            // Perform a case insensitive search through the XML document for a matching element or attribute
                            String caseInsensitiveParamElementName = getCaseInsensitiveParamElementName(inputXML, fieldName, spName);
                            if (caseInsensitiveParamElementName != null) {
                                l.logp(Level.FINE, getClass().getName(), "mapInputXMLToRecord", "Found a case-insensitive mapping for parameter \"" + c.javaColumnName + "\" to input element \"" + caseInsensitiveParamElementName + "\"");
                                fieldXPathExpr = ".//" + caseInsensitiveParamElementName;
                                value = getElementValueAsString(fieldXPathExpr, inputXML);
                            }
                        }
                    }

                    if (value != null) {
                        // Set the field value into the input record
                        inputRecord.setFieldValue(c.javaColumnName, value);
                    }

                }
                catch (Exception e) {
//                catch (TransformerException e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Invalid XML format of inputXML: " + DOMUtils.formatNode(inputXML), e);
                    l.throwing(getClass().getName(), "mapInputXMLToRecord", ae);
                    throw ae;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "mapInputXMLToRecord", inputRecord);
        }
        return inputRecord;
    }

    private String getCaseInsensitiveParamElementName(Node inputXML, String fieldName, String spName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCaseInsensitiveParamElementName", new Object[]{inputXML, fieldName});
        }

        Map caseInsensitiveNameMap = getCaseInsensitiveParamElementNamesMap();
        String fieldNameToUpper = fieldName.toUpperCase();
        String spNameUpper = spName.toUpperCase();
        String mapKey = spNameUpper.concat(fieldNameToUpper);
        String caseInsensitiveParamElementName = (String) caseInsensitiveNameMap.get(mapKey);
        if (caseInsensitiveParamElementName == null) {
            // Iterate through the child nodes to retrieve a matching case-insensitive element name
            caseInsensitiveParamElementName = getChildCaseInsensitiveElementName(inputXML, fieldNameToUpper);
            if (caseInsensitiveParamElementName != null) {
                caseInsensitiveNameMap.put(mapKey, caseInsensitiveParamElementName);
            }
        }
        else {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getCaseInsensitiveParamElementName", "found cached caseInsensitiveParamElementName = " + caseInsensitiveParamElementName);
            }
            if (!fieldName.equals(caseInsensitiveParamElementName)) {
                caseInsensitiveParamElementName = getChildCaseInsensitiveElementName(inputXML, fieldNameToUpper);
                caseInsensitiveNameMap.put(mapKey, caseInsensitiveParamElementName);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCaseInsensitiveParamElementName", caseInsensitiveParamElementName);
        }
        return caseInsensitiveParamElementName;
    }

    private String getChildCaseInsensitiveElementName(Node inputXML, String fieldName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChildCaseInsensitiveElementName", new Object[]{inputXML, fieldName});
        }

        String caseInsensitiveParamElementName = null;
        NodeList nl = inputXML.getChildNodes();
        for (int i = 0; caseInsensitiveParamElementName == null && i < nl.getLength(); i++) {
            Node childNode = nl.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = childNode.getNodeName();
                if (nodeName.equalsIgnoreCase(fieldName)) {
                    caseInsensitiveParamElementName = nodeName;
                } else {
                    // Recursively traverse descendant nodes
                    if (childNode.hasChildNodes()) {
                        caseInsensitiveParamElementName = getChildCaseInsensitiveElementName(childNode, fieldName);
                    }
                }
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChildCaseInsensitiveElementName", caseInsensitiveParamElementName);
        }
        return caseInsensitiveParamElementName;
    }

     private String getElementValueAsString(String xpathExpr, Node inputXML) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getElementValueAsString", new Object[]{xpathExpr, inputXML});
        }

        String value = null;
        try {
            Node xmlField = org.apache.xpath.XPathAPI.selectSingleNode(inputXML, xpathExpr);
            if (xmlField != null) {
                value = DOMUtils.getNodeTextValue(xmlField);
            }

            if (l.isLoggable(Level.FINE)) {
                if (value != null) {
                    // Set the field value into the input record
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Found an input element with value \"" + value + "\" using the XPath expression \"" + xpathExpr + "\"");
                }
                else {
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Did not find an input element using the XPath expression \"" + xpathExpr + "\"");
                }
            }
        }
        catch (TransformerException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply the XPath Expression '" + xpathExpr + "' with weblogic.xml.xpath.DOMXPath to inputXML: " + inputXML, e);
            l.throwing(getClass().getName(), "getElementValueAsString", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getElementValueAsString", value);
        }
        return value;
    }

/*
    private String getElementValueAsStringUsingJaxen(String elementName, Node inputXML) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getElementValueAsString", new Object[]{elementName, inputXML});
        }

        String value = null;
        String xpathExpr = null;
        try {
            xpathExpr = ".//" + elementName;
            org.jaxen.dom.DOMXPath xpath = new org.jaxen.dom.DOMXPath(xpathExpr);
            value = xpath.stringValueOf(inputXML);

            if (l.isLoggable(Level.FINE)) {
                if (value != null) {
                    // Set the field value into the input record
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Found an input element with name \"" + elementName + "\" with value \"" + value + "\" using the XPath expression \"" + xpathExpr + "\"");
                }
                else {
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Did not find an input element with name \"" + elementName + "\" using the XPath expression \"" + xpathExpr + "\"");
                }
            }
        }
        catch (org.jaxen.JaxenException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply the XPath Expression '" + xpathExpr + "' with org.jaxen.dom.DOMXPath to inputXML: " + inputXML, e);
            l.throwing(getClass().getName(), "getElementValueAsString", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getElementValueAsString", value);
        }
        return value;
    }
*/

/*
    private String getElementValueAsStringUsingWLJaxen(String elementName, Node inputXML) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getElementValueAsString", new Object[]{elementName, inputXML});
        }

        String value = null;
        String xpathExpr = null;
        try {
            xpathExpr = ".//" + elementName;
            value = new weblogic.xml.xpath.DOMXPath(xpathExpr).evaluateAsString(inputXML);

            if (l.isLoggable(Level.FINE)) {
                if (value != null) {
                    // Set the field value into the input record
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Found an input element with name \"" + elementName + "\" with value \"" + value + "\" using the XPath expression \"" + xpathExpr + "\"");
                }
                else {
                    l.logp(Level.FINE, getClass().getName(), "getElementValueAsString", "Did not find an input element with name \"" + elementName + "\" using the XPath expression \"" + xpathExpr + "\"");
                }
            }
        }
        catch (weblogic.xml.xpath.XPathException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to apply the XPath Expression '" + xpathExpr + "' with weblogic.xml.xpath.DOMXPath to inputXML: " + inputXML, e);
            l.throwing(getClass().getName(), "getElementValueAsString", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getElementValueAsString", value);
        }
        return value;
    }
*/

    /**
     * Setup the Callable Statement parameters from the given input record, adhering to the supplied vector of parameter ColumnDesc.
     */
    public void setCallableStatementParameters(CallableStatementSupport css, Vector paramColumnDescVector, Record inputRecord, StoredProcedureName spName, String spDaoKeyName) throws SQLException {
        setCallableStatementParameters(css, paramColumnDescVector, null, inputRecord, spName, spDaoKeyName);
    }

    /**
     * Setup the Callable Statement parameters from the given input record, adhering to the supplied vector of parameter ColumnDesc.
     * Apply the Record to Data mappings defined in the given DataRecordMapping.
     */
    public void setCallableStatementParameters(CallableStatementSupport css, Vector paramColumnDescVector, DataRecordMapping dataRecordMapping, Record inputRecord, StoredProcedureName spName, String spDaoKeyName) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCallableStatementParameters", new Object[]{spName});
        }

        java.util.Date d;
        java.sql.Date s;
        StringBuffer missingFieldValuesDebugMsg = new StringBuffer();
        StringBuffer spCallDebugMsg = null;
        String spCallSep = "";
        int missingFieldValues = 0;
        boolean isFunction = false;
        spCallDebugMsg = new StringBuffer();
        StringBuffer spCallLogMsg = new StringBuffer();
        String spLogSep = "";
        if (paramColumnDescVector.size()>0 ){
            ColumnDesc firstColumn = (ColumnDesc) paramColumnDescVector.elementAt(0);
            if (firstColumn.columnType == ColumnDesc.ColumnType.RETURNVALUE) {
                spCallDebugMsg.append(firstColumn.columnName).append("=");
                isFunction = true;
            }
        }
        spCallDebugMsg.append(spName.getFullyQualifiedSPName()).append("(");

        for (int i = 0; i < paramColumnDescVector.size(); i++) {
            try {
                ColumnDesc c = (ColumnDesc) paramColumnDescVector.elementAt(i);
                String fieldName = c.javaColumnName;
                Converter converter = null;

                // Set fieldName to the Mapped DataFieldName
                if (dataRecordMapping != null &&
                    dataRecordMapping.containsMappingForDataField(c.javaColumnName)) {
                    DataRecordFieldMapping fieldMapping = dataRecordMapping.getMappingForDataField(c.javaColumnName);
                    fieldName = fieldMapping.getRecordFieldName();
                }

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "setCallableStatementParameters", c.columnName + ", javaColumnName=" + fieldName + ", dataTypeName=" + c.dataTypeName + ", dataType=" + c.dataType + ", i=" + i + ", columnType=" + c.columnType);
                    l.logp(Level.FINE, getClass().getName(), "setCallableStatementParameters", "\tvalue=" + (inputRecord.hasFieldValue(fieldName) ? inputRecord.getFieldValue(fieldName) : null));
                    if ((c.columnType == ColumnDesc.ColumnType.IN || c.columnType == ColumnDesc.ColumnType.INOUT) &&
                        !isCursorSQLType(c)) {
                        if (!inputRecord.hasFieldValue(fieldName)) {
                            missingFieldValuesDebugMsg.append(", ").append(fieldName);
                            missingFieldValues++;
                        }
                    }
                }

                if (c.dataTypeName.equals("FLOAT")) {
                    if ((c.columnType == ColumnDesc.ColumnType.IN || c.columnType == ColumnDesc.ColumnType.INOUT) && c.dataTypeName.equals("FLOAT"))
                    {
                        if (inputRecord.hasField(fieldName) || !c.hasDefaultValue()) {
                            Float val = null;
                            if (inputRecord.hasFieldValue(fieldName))
                                val = (converter == null ? inputRecord.getFloatValue(fieldName) : inputRecord.getFloatValue(fieldName, converter));
                            css.setFloat(c.colNumber, val);
                            spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                            spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                        }
                    }

                    if ((c.columnType == ColumnDesc.ColumnType.OUT || c.columnType == ColumnDesc.ColumnType.RETURNVALUE) && c.dataTypeName.equals("FLOAT")) {
                        css.registerOutParameter(c.colNumber, Types.FLOAT);
                        spCallDebugMsg.append(spCallSep).append(c.columnName).append(" OUT");
                        spCallLogMsg.append(spLogSep).append(c.columnName).append(" OUT");
                    }
                }
                else if ("virtualModeWhereClause".equalsIgnoreCase(c.javaColumnName)) {
                    String val = prepareVirtualModeWhereClause(inputRecord, dataRecordMapping, spDaoKeyName);
                    css.setVarChar(c.colNumber, val);
                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                }
                else if ("virtualModeOrderBy".equalsIgnoreCase(c.javaColumnName)) {
                    String val = prepareVirtualModeOrderBy(inputRecord, dataRecordMapping, spDaoKeyName);
                    css.setVarChar(c.colNumber, val);
                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                }
                else if ("countB".equalsIgnoreCase(c.javaColumnName)) {
                    String val = inputRecord.getBooleanValue("_getTotalRecordCount", false) ? "Y" : "N";
                    css.setVarChar(c.colNumber, val);
                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                }
                else {

                    if ((c.columnType == ColumnDesc.ColumnType.IN || c.columnType == ColumnDesc.ColumnType.INOUT) && !isCursorSQLType(c)) {
                        if (inputRecord.hasField(fieldName) || !c.hasDefaultValue()) {
                            switch ((int) c.dataType) {
                                case (OracleTypes.TINYINT): {
                                    Short val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getShortValue(fieldName) : inputRecord.getShortValue(fieldName, converter));
                                    css.setTinyInt(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.SMALLINT): {
                                    Short val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getShortValue(fieldName) : inputRecord.getShortValue(fieldName, converter));
                                    css.setSmallInt(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.INTEGER): {
                                    Integer val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getIntegerValue(fieldName) : inputRecord.getIntegerValue(fieldName, converter));
                                    css.setInt(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.BIGINT): {
                                    Integer val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getIntegerValue(fieldName) : inputRecord.getIntegerValue(fieldName, converter));
                                    css.setBigInt(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.FLOAT): {
                                    Float val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getFloatValue(fieldName) : inputRecord.getFloatValue(fieldName, converter));
                                    css.setFloat(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.NUMERIC):
                                case (OracleTypes.DECIMAL): {
                                    BigDecimal val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getBigDecimalValue(fieldName) : inputRecord.getBigDecimalValue(fieldName, converter));
                                    css.setDecimal(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.REAL):
                                case (OracleTypes.DOUBLE): {
                                    Double val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getDoubleValue(fieldName) : inputRecord.getDoubleValue(fieldName, converter));
                                    css.setDouble(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.BIT): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setBit(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(val);
                                    break;
                                }
                                case (OracleTypes.CHAR):
                                case (OracleTypes.FIXED_CHAR): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setChar(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
    //                            case (OracleTypes.ROWID): Not Supported by Oracle JDBC as a procedure parameter
                                case (OracleTypes.VARCHAR): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setVarChar(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.LONGVARCHAR): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setLongVarChar(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.BOOLEAN): {
                                    Boolean val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getBooleanValue(fieldName) : inputRecord.getBooleanValue(fieldName, converter));
                                    css.setBoolean(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.BINARY): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setBinary(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.VARBINARY): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setVarBinary(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.LONGVARBINARY): {
                                    String val = null;
                                    if (inputRecord.hasFieldValue(fieldName))
                                        val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                    css.setLongVarBinary(c.colNumber, val);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.BLOB): {
                                    String val = null;
                                    InputStream in = null;
                                    if (inputRecord.hasFieldValue(fieldName)) {
                                        Object obj = inputRecord.getFieldValue(fieldName);
                                        if(obj instanceof InputStream){
                                            in = inputRecord.getInputStreamValue(fieldName);
                                            css.setBlob(c.colNumber, in);
                                        }
                                        else {
                                            val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                            css.setBlob(c.colNumber, val);
                                        }
                                    }
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.CLOB): {
                                    String val = null;
                                    Reader rd = null;
                                    if (inputRecord.hasFieldValue(fieldName)) {
                                        Object obj = inputRecord.getFieldValue(fieldName);
                                        if(obj instanceof Reader){
                                            rd = inputRecord.getReaderValue(fieldName);
                                            css.setClob(c.colNumber, rd);
                                        }
                                        else {
                                            val = (converter == null ? inputRecord.getStringValue(fieldName) : inputRecord.getStringValue(fieldName, converter));
                                            css.setClob(c.colNumber, val);
                                        }
                                    }
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(val));
                                    break;
                                }
                                case (OracleTypes.DATE):
                                case (OracleTypes.TIME):
                                case (OracleTypes.TIMESTAMP):
                                    if (inputRecord.hasFieldValue(fieldName)) {
                                        d = (converter == null ? inputRecord.getDateValue(fieldName) : inputRecord.getDateValue(fieldName, converter));
                                        if (d != null) {
                                            s = new java.sql.Date(d.getTime());
                                        } else {
                                            s = null;
                                        }
                                    } else {
                                        s = null;
                                    }
                                    css.setDate(c.colNumber, s);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append("to_date(").append(quoteStringVal(s == null ? null : DateUtils.formatDateTime(s))).append(", '").append(c_dbToDateFormatString).append("')");
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append("to_date(").append(quoteStringVal(s == null ? null : DateUtils.formatDateTime(s))).append(", '").append(c_dbToDateFormatString).append("')");
                                    break;
                                case (OracleTypes.CURSOR):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.CURSOR as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (OracleTypes.BFILE):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.BFILE as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (OracleTypes.STRUCT):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.STRUCT as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (OracleTypes.ARRAY):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.ARRAY as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (ColumnDesc.OracleDataType.OPAQUE): {
                                    Object obj = inputRecord.getFieldValue(fieldName);
                                    css.setXml(c.colNumber, obj);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(obj));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(obj));
                                    break;
                                }
                                case (OracleTypes.REF):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.REF as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (OracleTypes.NULL):
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                case (OracleTypes.OTHER):
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Cannot handle OracleTypes.OTHER as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                                default :
                                    l.logp(Level.INFO, getClass().getName(), "setCallableStatementParameters", "Unknown Oracle data type '"+c.dataType+"' as input parameter for '"+c.dataTypeName+"'. Setting parameter to null.");
                                    css.setNull(c.colNumber);
                                    spCallDebugMsg.append(spCallSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    spCallLogMsg.append(spLogSep).append(c.columnName).append(ASSIGNMENT_SYMBOL).append(quoteStringVal(null));
                                    break;
                            }
                        }
                        if (c.columnType == ColumnDesc.ColumnType.INOUT) {
                            css.registerOutParameter(c.colNumber, (int) c.dataType);
                        }
                    } else {
                        if (isCursorSQLType(c)) {
                            css.registerOutParameter(c.colNumber, getCursorSQLType(css.getConnection()));
                        } else {
                            if ((int)c.dataType == ColumnDesc.OracleDataType.OPAQUE) {
                                css.registerOutParameter (c.colNumber, OracleTypes.OPAQUE, "SYS.XMLTYPE");
                            }
                            else {
                                css.registerOutParameter(c.colNumber, (int) c.dataType);
                            }
                        }
                        if (!(isFunction && i == 0)) {
                            spCallDebugMsg.append(spCallSep).append(c.columnName);
                            spCallLogMsg.append(spLogSep).append(c.columnName);
                        }
                    }
                }
            } catch (SQLException e) {
                if (e.getMessage().indexOf("Invalid column reference") >= 0)
                    throw new AppException("Parameter Column Index<" + i + "' is out of bounds. Assuming that this column was removed from the query, and the stored procedure parameter metadata does not reflect the change yet.", e);
                else
                    throw e;
            }
            if (l.isLoggable(Level.FINE)) {
                if (isFunction && i == 0) {
                    spCallSep = "";
                } else {
                    spCallSep = ", ";
                }
            }
            spLogSep = "^";
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setCallableStatementParameters", "There are " + missingFieldValues + " missing record field values for calling '" + spName + "'; values for the following stored procedure parameters are missing:" + missingFieldValuesDebugMsg.toString());
            l.logp(Level.FINE, getClass().getName(), "setCallableStatementParameters", "Stored Procedure Call: " + spCallDebugMsg + ")");
            l.logp(Level.FINE, getClass().getName(), "setCallableStatementParameters", "Stored Procedure Log Parameters: " + spCallLogMsg + ")");
        }
        RequestStorageManager.getInstance().set(SP_CALL_DEBUG_MSG, spCallDebugMsg.toString());
        RequestStorageManager.getInstance().set(SP_CALL_LOG_MSG, spCallLogMsg.toString());
        l.exiting(getClass().getName(), "setCallableStatementParameters");
    }

    private String prepareVirtualModeWhereClause(Record inputRecord, DataRecordMapping dataRecordMapping, String spDaoKeyName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareVirtualModeWhereClause", new Object[]{inputRecord});
        }

        StringBuffer where = new StringBuffer();
        Integer filtersCount = 0;
        if (inputRecord.hasFieldValue("filterscount")) {
            try {
                filtersCount = inputRecord.getIntegerValue("filterscount");
            } catch (NumberFormatException nfe) {
                l.logp(Level.WARNING, getClass().getName(), "prepareVirtualModeWhereClause", "filterscount value is not an integer. Continuing without processing filters");
            }
        }

        Vector<ColumnDesc> rsColumnDescVector = null;
        if (m_rsColumnDescVectorMap.containsKey(spDaoKeyName)) {
            rsColumnDescVector = (Vector<ColumnDesc>) m_rsColumnDescVectorMap.get(spDaoKeyName);
        }
        else {
            l.logp(Level.WARNING, getClass().getName(), "prepareVirtualModeWhereClause", "Could not find the column description vector for procedure:" + spDaoKeyName);
        }

        if (filtersCount > 0) {
            where.append(" (");

            String tmpDataField = "";
            String tmpFilterOperator = "";
            for (Integer i = 0; i < filtersCount; i++) {
                String filterValue = inputRecord.getStringValue("filtervalue" + i);
                String filterCondition = inputRecord.getStringValue("filtercondition" + i);
                String filterDataField = inputRecord.getStringValue("filterdatafield" + i);
                filterDataField = filterDataField.replaceAll("([^A-Za-z0-9])", "");
                String filterOperator = inputRecord.getStringValue("filteroperator" + i);
                boolean foundMapping = false;

                // Strip the 'C' prefix if it exists
                if (filterDataField.startsWith("C")) {
                    filterDataField = filterDataField.substring(1);
                }

                // Strip off the "LOVLABEL" while looking for the data column mapping
                String lovSuffix = "";
                if (filterDataField.toUpperCase().endsWith("LOVLABEL")) {
                    lovSuffix = "LOVLABEL";
                    filterDataField = filterDataField.substring(0, filterDataField.indexOf("LOVLABEL"));
                }

                // Set dataRecordMapping to the Mapped DataFieldName
                if (dataRecordMapping != null &&
                    dataRecordMapping.containsMappingForDataField(filterDataField)) {
                    DataRecordFieldMapping fieldMapping = dataRecordMapping.getMappingForDataField(filterDataField);
                    // Skip the mapping if the data field name and the record field name are the same, due to a bug in initializeOutputDataTypeConverter() that sets the dataFieldName to the recordFieldName. Can't fix that bug now because it may break other existing logic.
                    if (!fieldMapping.getDataFieldName().equalsIgnoreCase(fieldMapping.getRecordFieldName())) {
                        filterDataField = fieldMapping.getDataFieldName();
                        foundMapping = true;
                    }
                }
                if (rsColumnDescVector != null) {
                    for (ColumnDesc colDesc : rsColumnDescVector) {
                        if (colDesc.javaColumnName.equalsIgnoreCase(filterDataField)) {
                            if (!foundMapping) {
                                filterDataField = colDesc.columnName;
                            }
                            if (ColumnDesc.DataTypeName.DATE.equals(colDesc.dataTypeName)) {
                                filterValue = "TO_DATE('" + filterValue + "','mm/dd/yyyy')";
                            }
                            break;
                        }
                    }
                }
                filterDataField += lovSuffix;

                if (tmpDataField.equals("")) {
                    tmpDataField = filterDataField;
                }
                else if (!tmpDataField.equals(filterDataField)) {
                    where.append(") AND (");
                }
                else if (tmpDataField.equals(filterDataField)) {
                    if (tmpFilterOperator.equals("0")) {
                        where.append(" AND ");
                    } else where.append(" OR ");
                }

                // build the "WHERE" clause depending on the filter's condition, value and datafield.
                switch (filterCondition) {
                    case "CONTAINS":    // case insensitive
                        where.append(" upper(" + filterDataField + ") LIKE '%" + filterValue.toUpperCase() + "%'");
                        break;
                    case "CONTAINS_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " LIKE '%" + filterValue + "%'");
                        break;
                    case "DOES_NOT_CONTAIN":    // case insensitive
                        where.append(" upper(" + filterDataField + ") NOT LIKE '%" + filterValue.toUpperCase() + "%'");
                        break;
                    case "DOES_NOT_CONTAIN_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " NOT LIKE '%" + filterValue + "%'");
                        break;
                    case "EQUAL":   // case insensitive
                        where.append(" upper(" + filterDataField + ") = '" + filterValue.toUpperCase() + "'");
                        break;
                    case "EQUAL_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " = '" + filterValue + "'");
                        break;
                    case "NOT_EQUAL":   // case insensitive 
                        where.append(" upper(" + filterDataField + ") != '" + filterValue.toUpperCase() + "'");
                        break;
                    case "NOT_EQUAL_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " != '" + filterValue + "'");
                        break;
                    case "GREATER_THAN":
                        where.append(" " + filterDataField + " > '" + filterValue + "'");
                        break;
                    case "LESS_THAN":
                        where.append(" " + filterDataField + " < '" + filterValue + "'");
                        break;
                    case "GREATER_THAN_OR_EQUAL":
                        where.append(" " + filterDataField + " >= '" + filterValue + "'");
                        break;
                    case "LESS_THAN_OR_EQUAL":
                        where.append(" " + filterDataField + " <= '" + filterValue + "'");
                        break;
                    case "STARTS_WITH":
                        where.append(" upper(" + filterDataField + ") LIKE '" + filterValue.toUpperCase() + "%'");
                        break;
                    case "STARTS_WITH_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " LIKE '" + filterValue + "%'");
                        break;
                    case "ENDS_WITH":
                        where.append(" upper(" + filterDataField + ") LIKE '%" + filterValue.toUpperCase() + "'");
                        break;
                    case "ENDS_WITH_CASE_SENSITIVE":
                        where.append(" " + filterDataField + " LIKE '%" + filterValue + "'");
                        break;
                    case "NULL":
                        where.append(" " + filterDataField + " IS NULL");
                        break;
                    case "NOT_NULL":
                        where.append(" " + filterDataField + " IS NOT NULL");
                        break;
                    case "EMPTY":
                        where.append(" trim(" + filterDataField + ") = \"\"");
                        break;
                    case "NOT_EMPTY":
                        where.append(" trim(" + filterDataField + ") != \"\"");
                        break;
                    default:
                        where.append(" true ");
                }

                if (i == filtersCount - 1) {
                    where.append(")");
                }

                tmpFilterOperator = filterOperator;
                tmpDataField = filterDataField;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareVirtualModeWhereClause", where.toString());
        }
        return where.toString();
    }

    private String prepareVirtualModeOrderBy(Record inputRecord, DataRecordMapping dataRecordMapping, String spDaoKeyName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "prepareVirtualModeOrderBy", new Object[]{inputRecord});
        }

        Vector<ColumnDesc> rsColumnDescVector = null;
        if (m_rsColumnDescVectorMap.containsKey(spDaoKeyName)) {
            rsColumnDescVector = (Vector<ColumnDesc>) m_rsColumnDescVectorMap.get(spDaoKeyName);
        }
        else {
            l.logp(Level.WARNING, getClass().getName(), "prepareVirtualModeOrderBy", "Could not find the column description vector for procedure:" + spDaoKeyName);
        }

        StringBuffer orderBy = new StringBuffer();
        if (inputRecord.hasFieldValue("sortdatafield")) {
            String sortDataField = inputRecord.getStringValue("sortdatafield");
            boolean foundMapping = false;
            String sortOrder = inputRecord.getStringValue("sortorder");
            if (sortDataField != null && sortOrder != null && (sortOrder.equals("asc") || sortOrder.equals("desc")))
            {
                sortDataField = sortDataField.replaceAll("([^A-Za-z0-9])", "");

                // Strip the 'C' prefix if it exists
                if (sortDataField.startsWith("C")) {
                    sortDataField = sortDataField.substring(1);
                }

                // Strip off the "LOVLABEL" while looking for the data column mapping
                String lovSuffix = "";
                if (sortDataField.toUpperCase().endsWith("LOVLABEL")) {
                    lovSuffix = "LOVLABEL";
                    sortDataField = sortDataField.substring(0, sortDataField.indexOf("LOVLABEL"));
                }

                // Set sortDataField to the Mapped DataFieldName
                if (dataRecordMapping != null &&
                    dataRecordMapping.containsMappingForDataField(sortDataField)) {
                    DataRecordFieldMapping fieldMapping = dataRecordMapping.getMappingForDataField(sortDataField);
                    // Skip the mapping if the data field name and the record field name are the same, due to a bug in initializeOutputDataTypeConverter() that sets the dataFieldName to the recordFieldName. Can't fix that bug now because it may break other existing logic.
                    if (!fieldMapping.getDataFieldName().equalsIgnoreCase(fieldMapping.getRecordFieldName())) {
                        sortDataField = fieldMapping.getDataFieldName();
                        foundMapping = true;
                    }
                }
                if (!foundMapping){
                    if (rsColumnDescVector != null) {
                        for (ColumnDesc colDesc : rsColumnDescVector) {
                            if (colDesc.javaColumnName.equalsIgnoreCase(sortDataField)) {
                                sortDataField = colDesc.columnName;
                                break;
                            }
                        }
                    }
                }

                sortDataField += lovSuffix;
                orderBy.append("order by ").append(sortDataField).append(" ").append(sortOrder);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "prepareVirtualModeOrderBy", orderBy.toString());
        }
        return orderBy.toString();
    }

    private String quoteStringVal(Object val) {
        return (val == null) ? null : "'" + val + "'";
    }

    /**
     * Build a vector of ColumnDesc for the parameters of the given stored procedure.
     * If there are any default Output DataType Converters specified, and there are OUT parameters of the same data type,
     * corresponding DataRecordFieldMappings will be added/updated in the given DataRecordMapping
     */
    protected Vector buildParameterColumnDescVector(StoredProcedureName spName, DataRecordMapping dataRecordMapping) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildParameterColumnDescVector", new Object[]{spName, dataRecordMapping});
        }

        Vector v = null;
        Connection conn = getReadonlyConnection();
        CallableStatement cs = null;
        try {
            ResultSet rs = null;
            long startTime = System.currentTimeMillis();
            if (getUseDatabaseMetadata()) {
                DatabaseMetaData dmd = conn.getMetaData();
                rs = getProcedureColumns(dmd, spName);
            } else {
                String spDescribeCall = "{call Cs_Dbms_Describe.Get_Footprint(?,?)}";
                cs = conn.prepareCall(spDescribeCall);
                CallableStatementSupport css = CallableStatementSupport.getInstance(cs);
                css.setString(1, spName.getFullyQualifiedSPName());
                css.registerOutParameter(2, getCursorSQLType(css.getConnection()));
                cs.execute();
                rs = (ResultSet) css.getObject(2);
            }
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "It took " + (System.currentTimeMillis() - startTime) + " milliseconds to load the metadata for " + spName.getFullyQualifiedSPName());
            }

            v = buildParameterColumnDescVector(rs, spName, dataRecordMapping);
        } finally {
            DatabaseUtils.close(cs, conn);
        }
        l.exiting(getClass().getName(), "buildParameterColumnDescVector");
        return v;
    }

    /**
     * Build a vector of ColumnDesc for the parameters of the given stored procedure.
     * If there are any default Output DataType Converters specified, and there are OUT parameters of the same data type,
     * corresponding DataRecordFieldMappings will be added/updated in the given DataRecordMapping
     */
    protected Vector buildParameterColumnDescVector(ResultSet metadataRs, StoredProcedureName spName, DataRecordMapping dataRecordMapping)
        throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildParameterColumnDescVector", new Object[]{spName});
        }


        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Retrieving the procedure column meta data from the database for '" + spName.getFullyQualifiedSPName() + "'");
            l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Building Procedure Column Description Vector");
        }
        JavaFieldNameFormatter javaFieldNameFormatter = getParameterColumnNameFormatter();
        int i = 0;
        Vector v = new Vector();
        // Interested in:
        // 1. column name
        // 2. column type (in, in-out, function return value)
        // 3. data type (3, 12 etc.) which is OracleTypes.XXX. If data type ==0. means it is a procedure without parameters.
        // 4. data type name (REF CURSOR, NUMBER etc.)
        // 5. overload info (so we don't mix up stored procs)
        while (metadataRs.next()) {
            i++;
            ColumnDesc c = new ColumnDesc();
            String columnName = metadataRs.getString("COLUMN_NAME");
            int columnType = metadataRs.getInt("COLUMN_TYPE");

            c.columnName = columnName;
            c.colNumber = i;
//            c.javaColumnName = javaFieldNameFormatter.format(c.columnName);
            c.columnType = columnType;
            c.dataType = metadataRs.getInt("DATA_TYPE");
            c.dataTypeName = metadataRs.getString("TYPE_NAME");
            c.overLoadValue = metadataRs.getString("OVERLOAD");
            c.precision = metadataRs.getLong("PRECISION");
            c.scale = metadataRs.getLong("SCALE");
            c.defaultValue = metadataRs.getInt("DEFAULT_VALUE");

            if (columnType == ColumnDesc.ColumnType.RETURNVALUE && c.dataType == OracleTypes.NULL) {
                // It is a procedure without parameters. ignore.
                continue;
            }

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Loaded : " + c);
            }

            if (columnType != ColumnDesc.ColumnType.RETURNVALUE && hasColumnDescriptionOverride(columnName)) {
                overrideColumnDescription(c); // only overrides scale, dataType,dataTypeName for a given columnName
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "ColumnDesc overriden as: " + c);
                }
            }
            // Set default name for Oracle function return value
            if (c.columnType == ColumnDesc.ColumnType.RETURNVALUE) {
                c.columnName = StoredProcedureDAO.RETURN_VALUE_FIELD;
            }

            if (c.columnName != null) {
                c.javaColumnName = javaFieldNameFormatter.format(c.columnName);

                postProcessColumnDesc(c);

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Adding: " + c);
                }

                v.add(c);

                // If this is an OUT parameter, and there is a default outout datatype converter specified for this column data type,
                // and the DataRecordMapper does not already have a converter specified for this column,
                // then add the default converter for this column.
                if (c.columnType == ColumnDesc.ColumnType.OUT && hasDefaultOutputDataTypeConverter(c.dataTypeName)) {
                    initializeOutputDataTypeConverter(dataRecordMapping, c);
                }
            }
            else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Received a procedure column meta data for a column with an empty column name: " + c);
                }
            }
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "buildParameterColumnDescVector", "Loaded " + v.size() + " procedure column descriptors.");
        }

        l.exiting(getClass().getName(), "buildParameterColumnDescVector");
        return (v);
    }


    protected ResultSet getProcedureColumns(DatabaseMetaData dmd, StoredProcedureName spName) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getProcedureColumns", new Object[]{spName});
        }

        ResultSet rs = null;

        String schemaName = spName.getSchemaName();
        String packageName = spName.hasPackageName() ? spName.getPackageName() : null;
        String procedureName = spName.getProcedureName();
        rs = dmd.getProcedureColumns(packageName, schemaName, procedureName, "%");

        l.exiting(getClass().getName(), "getProcedureColumns");
        return rs;
    }

    /**
     * Return the index of the next cursor column starting at the given column index..
     */
    protected int indexOfNextCursorColumn(CallableStatementSupport css, Vector columnDescVector, int startingColumnIndex) throws SQLException {

        int cursorColumnIndex = -1;

        for (int i = startingColumnIndex; i < columnDescVector.size(); i++) {
            ColumnDesc c = (ColumnDesc) columnDescVector.elementAt(i);

            if (isCursorSQLType(c)) {
                cursorColumnIndex = i;
            }
        }
        return cursorColumnIndex;
    }

    /**
     * Build a vector of ColumnDesc for the Result Set columns.
     * If there are any default Output DataType Converters specified, and there are OUT parameters of the same data type,
     * corresponding DataRecordFieldMappings will be added/updated in the given DataRecordMapping
     */
    protected Vector<ColumnDesc> buildResultSetColumnDescVector(ResultSet rs, DataRecordMapping dataRecordMapping) throws SQLException {
        l.entering(getClass().getName(), "buildResultSetColumnDescVector");

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "Building Result Set Column Description Vector");
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        Vector v = new Vector();
        JavaFieldNameFormatter javaFieldNameFormatter = getResultSetColumnNameFormatter();

        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            ColumnDesc c = null;
            String columnName = rsmd.getColumnName(i);
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "ResultSet column name: " + rsmd.getColumnName(i));
            }

            c = new ColumnDesc();
            c.columnName = columnName;
            c.colNumber = i;
            c.javaColumnName = javaFieldNameFormatter.format(c.columnName);
            c.columnType = rsmd.getColumnType(i);
            c.dataType = rsmd.getColumnType(i);
            c.dataTypeName = rsmd.getColumnTypeName(i);
            c.precision = rsmd.getPrecision(i);
            c.scale = rsmd.getScale(i);

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "Loaded: " + c);
            }

            if (hasColumnDescriptionOverride(columnName)) {
                overrideColumnDescription(c);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "ColumnDesc overridden: " + c);
                }
            }

            postProcessColumnDesc(c);

            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "Adding: " + c);
            }

            v.add(c);

            // If there is a default outout datatype converter specified for this column data type,
            // and the DataRecordMapper does not already have a converter specified for this column,
            // then add the default converter for this column.
            if (hasDefaultOutputDataTypeConverter(c.dataTypeName)) {
                initializeOutputDataTypeConverter(dataRecordMapping, c);
            }
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "buildResultSetColumnDescVector", "Loaded " + v.size() + " result set column descriptors.");
        }

        l.exiting(getClass().getName(), "buildResultSetColumnDescVector");
        return (v);
    }

    private void initializeOutputDataTypeConverter(DataRecordMapping dataRecordMapping, ColumnDesc c) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeOutputDataTypeConverter", new Object[]{dataRecordMapping, c});
        }

        // If there is a default outout datatype converter specified for this column data type,
        // and the DataRecordMapper does not already have a converter specified for this column,
        // then add the default converter for this column.
        DataRecordFieldMapping fieldMapping = null;
        if (dataRecordMapping.containsMappingForDataField(c.javaColumnName)) {
            fieldMapping = dataRecordMapping.getMappingForDataField(c.javaColumnName);
        }
        else {
            // 10/29/2018 - Bill Reeder: This looks like a bug to set the dataFieldName to the c.javaColumnName, but I can't fix that bug now because it may break other existing logic.
            fieldMapping = new DataRecordFieldMapping(c.javaColumnName, c.javaColumnName);
        }
        if (!fieldMapping.hasOutputFieldConverter()) {
            fieldMapping.setOutputFieldConverter(getDefaultOutputDataTypeConverter(c.dataTypeName));
            dataRecordMapping.addFieldMapping(fieldMapping);
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "initializeOutputDataTypeConverter", "Set the DataRecordFieldMapping with the default converter for column[" + c + "]; field mapping: " + fieldMapping);
            }
        }

        l.exiting(getClass().getName(), "initializeOutputDataTypeConverter");
    }

    /**
     * Determine if the ColumnDesc is a Cursor SQL Type.
     */
    public boolean isCursorSQLType(ColumnDesc c) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCursorSQLType", new Object[]{c});
        }

        boolean cursorType = false;
        if ((c.columnType == ColumnDesc.ColumnType.INOUT || c.columnType == ColumnDesc.ColumnType.OUT || c.columnType == ColumnDesc.ColumnType.RETURNVALUE) &&
            (c.dataType == OracleTypes.OTHER || c.dataType == OracleTypes.CURSOR) &&
            (c.dataTypeName.equals(ColumnDesc.DataTypeName.REF_CURSOR) || c.dataTypeName.equals(ColumnDesc.DataTypeName.TABLE))) {
            cursorType = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCursorSQLType", String.valueOf(cursorType));
        }
        return cursorType;
    }

    /**
     * Determine if the stored procedure call is an Oracle function
     */
    public boolean isOracleFunction(Vector paramColumnDescVector) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOracleFunction", new Object[]{paramColumnDescVector});
        }

        boolean isFunction = false;

        Iterator iter = paramColumnDescVector.iterator();
        while (iter.hasNext()) {
            ColumnDesc cols =  (ColumnDesc) iter.next();
            if (cols.columnType == ColumnDesc.ColumnType.RETURNVALUE && cols.dataType != OracleTypes.NULL) {
                isFunction = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOracleFunction", Boolean.valueOf(isFunction));
        }
        return isFunction;
    }

    protected int getCursorSQLType(Connection conn) {
        l.entering(getClass().getName(), "getCursorSQLType");

        int resultSQLType;
        resultSQLType = OracleTypes.CURSOR;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCursorSQLType", String.valueOf(resultSQLType));
        }
        return resultSQLType;
    }


    protected void postProcessColumnDesc(ColumnDesc c) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessColumnDesc", new Object[]{c});
        }

        if (c.dataType == OracleTypes.LONGVARCHAR) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.LONGVARCHAR with dataTypeName = " + c.dataTypeName + ". Changed data type name to " + ColumnDesc.DataTypeName.LONGVARCHAR);
            }
            c.dataTypeName = ColumnDesc.DataTypeName.LONGVARCHAR;
        }
        else if (c.dataType == ColumnDesc.OracleDataType.OPAQUE) {
            c.dataTypeName = ColumnDesc.DataTypeName.OPAQUE;            
        }
        else if (c.dataType == OracleTypes.OTHER) {
            if (ColumnDesc.DataTypeName.FLOAT.equals(c.dataTypeName)) {
                c.dataTypeName = ColumnDesc.DataTypeName.DOUBLE;
                c.dataType = OracleTypes.DOUBLE;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + ColumnDesc.DataTypeName.FLOAT + ". Changed data type to " + c.dataTypeName);
                }
            }
            else if (ColumnDesc.DataTypeName.BOOLEAN.equals(c.dataTypeName)) {
                c.dataType = OracleTypes.BOOLEAN;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + c.dataTypeName + ". Changed data type to " + c.dataTypeName);
                }
            }
            else if (ColumnDesc.DataTypeName.CLOB.equals(c.dataTypeName)) {
                c.dataType = OracleTypes.CLOB;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + c.dataTypeName + ". Changed data type to " + c.dataTypeName);
                }
            }
            else if (ColumnDesc.DataTypeName.BLOB.equals(c.dataTypeName)) {
                c.dataType = OracleTypes.BLOB;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + c.dataTypeName + ". Changed data type to " + c.dataTypeName);
                }
            }
            else if (ColumnDesc.DataTypeName.ROWID.equals(c.dataTypeName)) {
                c.dataType = OracleTypes.ROWID;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + c.dataTypeName + ". Changed data type to " + c.dataTypeName);
                }
            }
            else if (ColumnDesc.DataTypeName.BINARY_INTEGER.equals(c.dataTypeName)) {
                c.dataType = OracleTypes.INTEGER;
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "postProcessColumnDesc", "Column '" + c.columnName + "' is a OracleTypes.OTHER with dataTypeName = " + c.dataTypeName + ". Changed data type to " + c.dataTypeName);
                }
            }
        }

        // Set the Java Type Name
        c.javaTypeName = getJavaTypeName(c);

        l.exiting(getClass().getName(), "postProcessColumnDesc");
    }

    protected String getJavaTypeName(ColumnDesc c) {
        String javaTypeName = "";

        switch ((int) c.dataType) {
            // Note: NUMERIC and NUMBER are the same
            // Note: BINARY and RAW are the same
            case (OracleTypes.BIT):
            case (OracleTypes.TINYINT):
            case (OracleTypes.SMALLINT):
            case (OracleTypes.INTEGER):
                javaTypeName =  ("Integer");
                break;
            case (OracleTypes.BIGINT):
                javaTypeName =  ("Long");
                break;
            case (OracleTypes.FLOAT):
                javaTypeName =  ("Float");
                break;
            case (OracleTypes.REAL):
            case (OracleTypes.DOUBLE):
                javaTypeName =  ("Double");
                break;
            case (OracleTypes.DECIMAL):
            case (OracleTypes.NUMERIC):
                javaTypeName =  ("BigDecimal");
                break;
            case (OracleTypes.CHAR):
            case (OracleTypes.VARCHAR):
            case (OracleTypes.LONGVARCHAR):
            case (OracleTypes.FIXED_CHAR):
            case (OracleTypes.ROWID):
                javaTypeName =  ("String");
                break;
            case (OracleTypes.BOOLEAN):
                javaTypeName =  ("Boolean");
                break;
            case (OracleTypes.DATE):
            case (OracleTypes.TIME):
            case (OracleTypes.TIMESTAMP):
                javaTypeName =  ("Timestamp");
                break;
            //case (OracleTypes.BINARY): same as OracleTypes.RAW
            case (OracleTypes.VARBINARY):
            case (OracleTypes.LONGVARBINARY):
            case (OracleTypes.RAW):
            case (OracleTypes.BLOB):
                javaTypeName =  ("BinaryInputStream");
                break;
            case (OracleTypes.CLOB):
                javaTypeName =  ("AsciiInputStream");
                break;
            case (OracleTypes.CURSOR):
            case (OracleTypes.BFILE):
            case (OracleTypes.STRUCT):
            case (OracleTypes.ARRAY):
            case (OracleTypes.REF):
            case (OracleTypes.OTHER):
                javaTypeName =  ("Unknown");
                break;
            case (OracleTypes.NULL):
                javaTypeName =  ("Null");
                break;
            default :
                javaTypeName =  ("String");
                break;
        }

        return javaTypeName;
    }

    public synchronized void clearColumnDescVectorCache() {
        m_paramColumnDescVectorMap = new Hashtable();
        m_rsColumnDescVectorMap = new Hashtable();
    }

    protected boolean hasColumnDescriptionOverride(String columnName) {
        return m_columnDescriptionOverrides.containsKey(columnName);
    }

    // once a column is configured, the dataType and scale is also expected..
    //otherwise, they will be override with default values incorrectly
    protected void overrideColumnDescription(ColumnDesc columnDesc) {
      ColumnDesc desc = (ColumnDesc) m_columnDescriptionOverrides.get(columnDesc.columnName);
      if (desc.dataTypeName != null) columnDesc.dataTypeName = desc.dataTypeName;
      columnDesc.dataType = desc.dataType;
      columnDesc.scale = desc.scale;
      columnDesc.precision = desc.precision;
    }

    protected void loadColumnDescriptionOverrides() {

        Map columnDescriptions = new Hashtable();

        // Load Column Descriptions Overrides from file
        loadColumnDescriptionsFromFile(columnDescriptions);

        m_columnDescriptionOverrides = columnDescriptions;
    }

    protected void loadColumnDescriptionsFromFile(Map columnDescriptions) {
        l.entering(getClass().getName(), "loadColumnDescriptionsFromFile");

        JavaFieldNameFormatter javaFieldNameFormatter = getResultSetColumnNameFormatter();
        if (hasSpDAOOverrideColumns()) {

            Properties props = getSpDAOOverrideColumnProperties();
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements()) {
                String propName = (String) en.nextElement();
                if (propName.endsWith("columnName")) {
                    String propPrefix = propName.substring(0, propName.indexOf("columnName"));
                    ColumnDesc columnDesc = new ColumnDesc();
                    String columnName = props.getProperty(propPrefix + "columnName");
                    columnDesc.columnName = columnName;
                    columnDesc.javaColumnName = javaFieldNameFormatter.format(columnDesc.columnName);
                    columnDesc.dataTypeName = props.getProperty(propPrefix + "dataTypeName");
                    columnDesc.dataType = Integer.parseInt(props.getProperty(propPrefix + "dataType"));
                    if (FormatUtils.isLong(props.getProperty(propPrefix + "precision"))) {
                        columnDesc.precision = Integer.parseInt(props.getProperty(propPrefix + "precision"));
                    }
                    if (FormatUtils.isLong(props.getProperty(propPrefix + "scale"))) {
                        columnDesc.scale = Integer.parseInt(props.getProperty(propPrefix + "scale"));
                    }
                    columnDesc.javaTypeName = getJavaTypeName(columnDesc);
                    columnDescriptions.put(columnName, columnDesc);
                }
            }
        }
        l.exiting(getClass().getName(), "loadColumnDescriptionsFromFile");
    }

    protected String executeGetSchemaOwner() {
        l.entering(getClass().getName(), "executeGetSchemaOwner");
        String schemaOwner = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getReadonlyConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT  pm_table_owner('SYSTEM_PARAMETER_UTIL') FROM DUAL");
            if (rs.next()) {
                schemaOwner = rs.getString(1);
                l.logp(Level.FINE, StoredProcedureDAO.class.getName(), "getDefaultSchemaName", "Loaded the schemaOwner '" + schemaOwner + "' from the Connection.");
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get the default schema name for this connection.", e);
            l.throwing(StoredProcedureDAO.class.getName(), "getDefaultSchemaName", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(stmt, conn);
        }

        l.exiting(StoredProcedureDAO.class.getName(), "executeGetSchemaOwner", schemaOwner);
        return schemaOwner;
    }

    public void debugVector(String headerMessage, Vector v) {
        if (l.isLoggable(Level.FINE)) {
            StringBuffer buf = new StringBuffer();
            buf.append(headerMessage).append("\n");
            for (int i = 0; i < v.size(); i++) {
                buf.append((v.elementAt(i)).toString()).append("\n");
            }
            l.logp(Level.FINE, getClass().getName(), "debugVector", buf.toString());
        }
    }

    protected Connection getReadonlyConnection() throws SQLException {
        return getReadOnlyDataSource().getConnection();
    }

    protected boolean getUseDatabaseMetadata() {
        if (m_useDatabaseMetadata == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_USE_DATABASE_METADATA)) {
                m_useDatabaseMetadata = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_USE_DATABASE_METADATA)).booleanValue());
            }
            else {
                m_useDatabaseMetadata = Boolean.FALSE;
            }
        }
        return m_useDatabaseMetadata.booleanValue();
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public StoredProcedureDAOHelper() {
        super();
    }

    public DataSource getReadOnlyDataSource() {
        return m_readOnlyDataSource;
    }

    public void setReadOnlyDataSource(DataSource readOnlyDataSource) {
        m_readOnlyDataSource = readOnlyDataSource;
    }

    public void verifyConfig() {
        if (getReadOnlyDataSource() == null)
            throw new ConfigurationException("The required property 'oracleDataSource' is missing.");
    }

    /**
     * Initialize the bean.
     */
    @Override
    public void initialize() {
        if(isStoredProcedureLoggingEnabled()) {
            synchronized (this) {
                StoredProcedureDAO.setStpDaoLogger(LogUtils.addLogger(STORED_PROCEDURE_LOGGER_NAME, new StoredProcedureLogFormatter(), getStoredProcedureLogFilePattern(),
                        getStoredProcedureLogFileLimit(), getStoredProcedureLogFileCount(), true, StoredProcedureLogLevel.STORED_PROCEDURE, isLoggedToDefaultLogEnabled()));
            }
        }

        loadColumnDescriptionOverrides();
    }

    public boolean hasSpDAOOverrideColumns() {
        return m_spDAOOverrideColumnProperties != null;
    }

    public Properties getSpDAOOverrideColumnProperties() {
        return m_spDAOOverrideColumnProperties;
    }

    public void setSpDAOOverrideColumnProperties(Properties spDAOOverrideColumnProperties) {
        m_spDAOOverrideColumnProperties = spDAOOverrideColumnProperties;
    }

    public JavaFieldNameFormatter getParameterColumnNameFormatter() {
        return m_parameterColumnNameFormatter;
    }

    public void setParameterColumnNameFormatter(JavaFieldNameFormatter parameterJavaFieldNameFormatter) {
        m_parameterColumnNameFormatter = parameterJavaFieldNameFormatter;
    }

    public JavaFieldNameFormatter getResultSetColumnNameFormatter() {
        return m_resultSetColumnNameFormatter;
    }

    public void setResultSetColumnNameFormatter(JavaFieldNameFormatter resultSetJavaFieldNameFormatter) {
        m_resultSetColumnNameFormatter = resultSetJavaFieldNameFormatter;
    }

    /**
     * Set the default datatype converters for output parameters and columns, keyed by the data type of the database field,
     * as specified in the ColumnDesc.DataTypeName Interface constants.
     * For every OUT parameter and result set column that has a database data type matching the keyed data type,
     * the corresponding Converter will be used by default to convert the value to the desired data type.
     * If a specifid DataRecordFieldMapping is specified for the parameter or column, and it has a Converter,
     * the default Converter will be overridden by the one in the DataRecordFieldMapping.
     *
     * @param outputDataTypeConverters a map containing the converters keyed by the database data type name.
     * @see ColumnDesc.DataTypeName
     */
    public void setDefaultOutputDataTypeConverters(Map outputDataTypeConverters) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setDefaultOutputDataTypeConverters", new Object[]{outputDataTypeConverters});
        }

        if (outputDataTypeConverters != null) {
            Iterator iter = outputDataTypeConverters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                m_defaultOutputDataTypeConverters.put(entry.getKey(), entry.getValue());
                l.logp(Level.FINE, getClass().getName(), "setDefaultOutputDataTypeConverters", "entry = " + entry.getKey() + ":" + entry.getValue());
            }
        }

        l.exiting(getClass().getName(), "setDefaultOutputDataTypeConverters");
    }

    protected boolean hasDefaultOutputDataTypeConverter(String dataTypeName) {
        return m_defaultOutputDataTypeConverters.containsKey(dataTypeName);
    }

    protected Converter getDefaultOutputDataTypeConverter(String dataTypeName) {
        return (Converter) m_defaultOutputDataTypeConverters.get(dataTypeName);
    }

    public Map getCaseInsensitiveParamElementNamesMap() {
        if (m_caseInsensitiveParamElementNamesMap == null) {
            m_caseInsensitiveParamElementNamesMap = new HashMap();
        }
        return m_caseInsensitiveParamElementNamesMap;
    }

    /**
     * Implements the refresh parameters listener.
     * @param request
     */
    public void refreshParms(HttpServletRequest request) {
        l.entering(getClass().getName(), "refreshParms");
        MessageManager messageManager = MessageManager.getInstance();
        try {
            clearColumnDescVectorCache();
            messageManager.addInfoMessage("core.refresh.storeprocedure.columndesc.success");
            l.logp(Level.INFO, getClass().getName(), "refreshParms", "Store procedure column description have been refreshed!");
        }
        catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "refreshParms", "Failed to refresh Store procedure column description !", e);
            messageManager.addErrorMessage("core.refresh.storeprocedure.columndesc.fail");
        }
        l.exiting(getClass().getName(), "refreshParms");
    }

    @Override
    /**
     * Cleanup open resources and sizeable static variables
     */
    public void terminate() {
        synchronized (this) {
            l.logp(Level.INFO, getClass().getName(), "terminate", "REMOVE HANDLERS for " + STORED_PROCEDURE_LOGGER_NAME);
            LogUtils.removeHandlers(STORED_PROCEDURE_LOGGER_NAME);
            StoredProcedureDAO.setStpDaoLogger(null);
        }
    }

    public static Boolean isStoredProcedureLoggingEnabled(){
        return YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("log.stored.procedure.calls", "true")).booleanValue();
    }

    public static String getStoredProcedureLogFilePattern(){
        return ApplicationContext.getInstance().getProperty("log.stored.procedure.file.pattern", STORED_PROCEDURE_LOG_FILE_PATTERN);
    }

    public static int getStoredProcedureLogFileLimit(){
        int limit = 500000;
        try {
            limit = Integer.parseInt(ApplicationContext.getInstance().getProperty("log.stored.procedure.file.limit", "500000"));
        } catch (NumberFormatException nfe){
            //Do nothing
        }

        return limit;
    }

    public static int getStoredProcedureLogFileCount(){
        int count = 100;
        try {
            count = Integer.parseInt(ApplicationContext.getInstance().getProperty("log.stored.procedure.file.count", "100"));
        } catch (NumberFormatException nfe){
            //Do nothing
        }
        return count;
    }

    public static Boolean isLoggedToDefaultLogEnabled() {
        return YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("log.stored.procedure.to.default", "false")).booleanValue();
    }

    private Properties m_spDAOOverrideColumnProperties;
    private JavaFieldNameFormatter m_parameterColumnNameFormatter = c_noOpJavaFieldNameFormatter;
    private JavaFieldNameFormatter m_resultSetColumnNameFormatter = c_noOpJavaFieldNameFormatter;
    private Map m_columnDescriptionOverrides = new Hashtable();
    private Map m_paramColumnDescVectorMap = new Hashtable();
    private Map m_rsColumnDescVectorMap = new Hashtable();
    private DataSource m_readOnlyDataSource;
    private Map m_defaultOutputDataTypeConverters = new Hashtable();
    private Boolean m_useDatabaseMetadata;
    private Map m_caseInsensitiveParamElementNamesMap;
    private final Logger l = LogUtils.getLogger(getClass());

    private static JavaFieldNameFormatter c_noOpJavaFieldNameFormatter = new NoOpJavaFieldNameFormatter();

    private static String c_dbToDateFormatString = "MM/dd/yyyy hh24:mi:ss";

    public static final String ASSIGNMENT_SYMBOL = "=>";
    public static final String SP_CALL_DEBUG_MSG = "spCallDebugMsg";

    public static final String SP_CALL_LOG_MSG = "spCallLogMsg";
    public static final String STORED_PROCEDURE_LOGGER_NAME = "SpDAO Logger";
    public static final String STORED_PROCEDURE_LOG_FILE_PATTERN = "storedprocedurecalls%u.%g.log";
}
