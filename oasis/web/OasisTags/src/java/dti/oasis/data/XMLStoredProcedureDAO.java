package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultXMLRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.XMLRecordLoadProcessor;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.xml.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Extends the StoredProcedureDAO to enable XML format for all input and output values.
 * <p/>
 * The optional <b>paramMappingXML</b> element specifies the configuration definition mapping XML elements
 * to the stored procedure IN and IN/OUT parameters.
 * If this config element is missing, input parameters for the stored procedrue are retrieved from the
 * input XML by looking for names that match, case-insensitive.
 * All stored procedure parameter names are converted to Java-type names,
 * where any underscore characters ("_") are removed and the following character is upper cased,
 * converting the name to what is commonly referred to as "camel-case".
 * Also, several common parameter prefixes are stripped, including "i_", "p_", "o_", "in_", and "out_".
 * The specific prefixes to strip off is configurable in the Spring configuration of the StoredProcedureDAOHelper bean.
 * Any "_fk" and "_pk" suffixes are converted to "_id".
 * Lastly, the first character is upper cased.
 * For example, and input parameter named "i_account_pk" is converted to "AccountId".
 * The specific prefixes to strip off is configurable in the Spring configuration of the StoredProcedureDAOHelper bean.
 * <p/>
 * Within the <b>paramMappingXML</b> element, each parameter mapping is defined by a "param" element.
 * The param element requires a <b>"name"</b> attribute do identify the stored procedure parameter name,
 * and a <b>"select"</b> attribute to define the XPath expression to locate the value from the input.
 * For example, to map an input element named "/FindAllPolicyRequest/PolicyNo" to the "PolicyNoCriteria" parameter,
 * and "/FindAllPolicyRequest/Term/StartDate" to "TermEffectiveFromDate", the following param mapping can be used:
 * <spdao:paramMapping>
 * <spdao:param name="PolicyNoCriteria" select="/FindAllPolicyRequest/PolicyNo"/>
 * <spdao:param name="TermEffectiveFromDate" select="/FindAllPolicyRequest/Term/StartDate"/>
 * </spdao:paramMapping>
 * <p/>
 * &lt;spdao:paramMapping&gt;<br/>
 * &nbsp;&nbsp;&lt;spdao:param name="PolicyNoCriteria" select="/FindAllPolicyRequest/PolicyNo"/&gt;<br/>
 * &nbsp;&nbsp;&lt;spdao:param name="TermEffectiveFromDate" select="/FindAllPolicyRequest/Term/StartDate"/&gt;<br/>
 * &lt;/spdao:paramMapping&gt;<br/>
 * <p/>
 * Note: when the stored procedure is executed in batch, the "select" XPath expression
 * is evaluated with respect to the provided batchUpdateSelectXPath.
 * <p/>
 * The optional <b>outputConfig</b> element specifies the configuration definition mapping all
 * non-cursor OUT, INOUT or Function Return values, as well as the first result set in a row-iterator tag.
 * All out parameter and result set column names are converted to Java-type names, converting the name to "camel-case"
 * in the same manner as described for the parameter names in the paramMappingXML above.
 * Also, dates are by default converted to the string format "yyyy-MM-dd", and is configurable
 * in the Spring configuration of the StoredProcedureDAOHelper bean.
 * If this outputConfig parameter is not defined, the output XML is generated in the following format,
 * where all non-cursor OUT, INOUT or Function Return values are placed in the Response element,
 * and each result set row is placed within Row elements:
 <spdao:outputConfig>
     <Response>
        ... <!-- All non-cursor OUT, INOUT or Function Return values -->
        <Row>
            ... <!-- All column values -->
        </Row>
        ...
        <Row>
        </Row>
     </Response>
 </spdao:outputConfig>
 * <p/>
 &lt;spdao:outputConfig&gt;<br/>
 &nbsp;&nbsp;&lt;Response&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;... &lt;!-- All non-cursor OUT, INOUT or Function Return values --&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;Row&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;... &lt;!-- All column values --&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Row&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;...
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;Row&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Row&gt;<br/>
 &nbsp;&nbsp;&lt;/Response&gt;<br/>
 &lt;/spdao:outputConfig&gt;<br/>
 * <p/>
 * Within the outputConfig element, any XML element that matches the name of a parameter or result set column
 * (case insensitive) is used to define where the parameter/column value is placed in the output.
 * Alternatively, a get-column attribute can be defined for any element name that needs to be mapped to a
 * particular param/column name. When defined outside of a row-iterator element, it maps to a parameter.
 * When defined within the row-iterator, it maps to a result set column.
 * The get-remaining-columns element is used to retrieve all remaining parameters/columns that haven't been mapped.
 * The row-iterator element defines where the result set should be retrieved.
 * For example:
 <spdao:outputConfig xmlns:spdao="http://delphi-tech.com/xml/spdao">
     <FindAllPolicyResponse>
         <spdao:get-remaining-columns/>
         <spdao:row-iterator>
             <Policy>
                 <PolicyTermHistoryId/>
                 <PolicyNo/>
                 <PolicyId/>
                 <Term>
                     <StartDate spdao:get-column="termEffectiveFromDate"/>
                     <EndDate spdao:get-column="termeffectivetodate"/>
                 </Term>
             <spdao:get-remaining-columns/>
             </Policy>
        </spdao:row-iterator>
      </FindAllPolicyResponse>
 </spdao:outputConfig>
 * <p>
 &lt;spdao:outputConfig&gt;<br/>
 &nbsp;&nbsp;&lt;FindAllPolicyResponse&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;spdao:get-remaining-columns/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;spdao:row-iterator&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Policy&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;PolicyTermHistoryId/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;PolicyNo/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;PolicyId/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;Term&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;StartDate spdao:get-column="termEffectiveFromDate"/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;EndDate spdao:get-column="termeffectivetodate"/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/Term&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;spdao:get-remaining-columns/&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/Policy&gt;<br/>
 &nbsp;&nbsp;&nbsp;&nbsp;&lt;/spdao:row-iterator&gt;<br/>
 &nbsp;&nbsp;&lt;/FindAllPolicyResponse&gt;<br/>
 &lt;/spdao:outputConfig&gt;<br/>
 * <p/>
 * (C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2009       James       Issue#102265 Fix the StoredProcedureDAOHelper to leave
 *                              stored procedure parameters unset if there is not Field
 *                              in the inputRecord and the parameter has a default value
 *                              setting in the stored procedure definition
 * 05/07/2010       fcb         Issue# 107308: stored procedure name passed to mapInputXMLToRecord
 * ---------------------------------------------------
 */
public class XMLStoredProcedureDAO extends StoredProcedureDAO {
    /**
     * The bean name of this class in the ApplicationContext.
     */
    public static final String BEAN_NAME = "XMLStoredProcedureDAO";

    public static final String SPDAO_NS_PREFIX = "spdao";
    public static final String BATCH_UPDATE = "batchUpdate";
    public static final String SPDAO_NS_URI = "http://delphi-tech.com/xml/spdao";

    /**
     * Retrieves the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     * @return the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     */
    public static XMLStoredProcedureDAO getXMLInstance(String spName) {
        return (XMLStoredProcedureDAO) getStoredProcedureDAO(spName, BEAN_NAME, null);
    }

    /**
     * Retrieves the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     * @param beanName the name of the XMLStoredProcedureDAO bean defined the Spring configuration
     * @return the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     */
    public static XMLStoredProcedureDAO getXMLInstance(String spName, String beanName) {
        return (XMLStoredProcedureDAO) getStoredProcedureDAO(spName, beanName, null);
    }

    /**
     * Retrieves the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     * @param externalConnection the externalConnection to use for execution
     * @return the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     */
    public static XMLStoredProcedureDAO getXMLInstance(String spName, Connection externalConnection) {
        XMLStoredProcedureDAO xmlSPDao = (XMLStoredProcedureDAO) getStoredProcedureDAO(spName, BEAN_NAME, null);
        xmlSPDao.setExternalConnection(externalConnection);
        return xmlSPDao;
    }

    /**
     * Retrieves the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     * @param beanName the name of the XMLStoredProcedureDAO bean defined the Spring configuration
     * @param externalConnection the externalConnection to use for execution
     * @return the XMLStoredProcedureDAO for the specified, fully qualified stored procedure.
     */
    public static XMLStoredProcedureDAO getXMLInstance(String spName, String beanName, Connection externalConnection) {
        XMLStoredProcedureDAO xmlSPDao = (XMLStoredProcedureDAO) getStoredProcedureDAO(spName, beanName, null);
        xmlSPDao.setExternalConnection(externalConnection);
        return xmlSPDao;
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     *
     * @param inputXML input XML parameters
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(String inputXML) throws SQLException {
        return executeToXML(inputXML, null, null, c_defaultXMLLoadProcessor);
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     *
     * @param inputXML        input XML parameters
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(String inputXML, String paramMappingXML) throws SQLException {
        return executeToXML(inputXML, paramMappingXML, null, c_defaultXMLLoadProcessor);
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * The outputConfigXML specifies the configuration definition mapping all
     * non-cursor OUT, INOUT or Function Return values, as well as the first ResultSet in a row-iterator tag.
     *
     * @param inputXML        input XML parameters
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param outputConfigXML the configuration definition mapping all
     *                        non-cursor OUT, INOUT or Function Return values, as well as the first ResultSet in a row-iterator tag
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(String inputXML, String paramMappingXML, String outputConfigXML) throws SQLException {
        return executeToXML(inputXML, paramMappingXML, outputConfigXML, c_defaultXMLLoadProcessor);
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * The outputConfigXML specifies the configuration definition mapping all
     * non-cursor OUT, INOUT or Function Return values, as well as the first ResultSet in a row-iterator tag.
     * The XMLRecordLoadProcessor is used to post-process each XML Record Node, and the entire XML RecordSet Node.
     *
     * @param inputXML        input XML parameters
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param outputConfigXML the configuration definition mapping all
     * @param loadProcessor   used to post-process each XML Record Node, and the entire XML RecordSet Node
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(String inputXML, String paramMappingXML, String outputConfigXML, XMLRecordLoadProcessor loadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeToXML", new Object[]{inputXML, paramMappingXML, outputConfigXML, loadProcessor});
        }

        // Parse the input xml source
        Document inputDoc = null;
        if (inputXML != null) {
            inputDoc = DOMUtils.parseXML(inputXML, true);
        } else {
            inputDoc = DOMUtils.parseXML("<Dummy/>", true);
        }

        // Parse the param mapping xml source
        Document paramMappingDoc = null;
        if (paramMappingXML != null) {
            paramMappingDoc = DOMUtils.parseXML(paramMappingXML, true);
        }

        // Parse the output config xml source
        Document outputConfigDoc = null;
        if (outputConfigXML != null) {
            outputConfigDoc = DOMUtils.parseXML(outputConfigXML, true);
        }

        Document resultDoc = executeToXML(inputDoc, paramMappingDoc, outputConfigDoc, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeToXML", resultDoc);
        }
        return resultDoc;
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * The outputConfigXML specifies the configuration definition mapping all
     * non-cursor OUT, INOUT or Function Return values, as well as the first ResultSet in a row-iterator tag.
     *
     * @param inputXML        input XML parameters
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param outputConfigXML the configuration definition mapping all
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(Node inputXML, Node paramMappingXML, Node outputConfigXML) throws SQLException {
        return executeToXML(inputXML, paramMappingXML, outputConfigXML, c_defaultXMLLoadProcessor);
    }

    /**
     * Execute the Stored Procedure with the given input XML as input parameters.
     * Return a XML RecordSet Document with all rows from the first ResultSet if any,
     * adding any non-cursor OUT, INOUT or Function Return values to the root node as elements.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * The outputConfigXML specifies the configuration definition mapping all
     * non-cursor OUT, INOUT or Function Return values, as well as the first ResultSet in a row-iterator tag.
     * The XMLRecordLoadProcessor is used to post-process each XML Record Node, and the entire XML RecordSet Node.
     *
     * @param inputXML        input XML parameters
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param outputConfigXML the configuration definition mapping all
     * @param loadProcessor   used to post-process each XML Record Node, and the entire XML RecordSet Node
     * @return the resulting XML RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Document executeToXML(Node inputXML, Node paramMappingXML, Node outputConfigXML, XMLRecordLoadProcessor loadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "executeToXML", new Object[]{inputXML, paramMappingXML, outputConfigXML, loadProcessor});
        }

        Document resultDoc = null;
        long startTime = System.currentTimeMillis();

        // Get a Connection
        Connection conn = null;
        CallableStatementSupport css = new CallableStatementSupport();
        try {
            conn = getAppConnection();

            // Get Parameter ColumnDesc Vector
            Vector paramColumnDescVector = getStoredProcedureDAOHelper().
                    getParameterColumnDescVector(getSPDaoKeyName(), getStoredProcedureName(), getDataRecordMapping());
            // get input record from xml data
            Record inputRecord = getStoredProcedureDAOHelper().mapInputXMLToRecord(inputXML, paramMappingXML, paramColumnDescVector,
                    getStoredProcedureName().getProcedureName());

            Vector requiredParamVector = getRequiredParameterVector(inputRecord);

            executeStoredProcedureForXML(inputRecord, requiredParamVector, conn, css, false);

            // Process the result, placing any OUT parameters into the Summary Record, and returning a RecordSet with the first ResultSet (if one exists).
            resultDoc = processResultToXML(css, requiredParamVector, outputConfigXML, loadProcessor);

        }
        finally {
            if (hasExternalConnection()) {
                DatabaseUtils.close(css.getCallableStatement());
            }
            else {
                DatabaseUtils.close(css.getCallableStatement(), conn);
            }
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeToXML",
                "Executed the following procedure and processed the results in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeToXML", resultDoc);
        }
        return resultDoc;
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     *
     * @param inputXML input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(String inputXML, String batchUpdateSelectXPath) throws SQLException {
        return executeBatchXML(inputXML, batchUpdateSelectXPath, null, false, getExecuteBatchSize());
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(String inputXML, String batchUpdateSelectXPath, String paramMappingXML) throws SQLException {
        return executeBatchXML(inputXML, batchUpdateSelectXPath, paramMappingXML, false, getExecuteBatchSize());
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * Overrides the autoCommit property of the connection with the provided value.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param setAutoCommit   if true, sets AutoCommit to true for this execute statement.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(String inputXML, String batchUpdateSelectXPath, String paramMappingXML, boolean setAutoCommit) throws SQLException {
        return executeBatchXML(inputXML, batchUpdateSelectXPath, paramMappingXML, setAutoCommit, getExecuteBatchSize());
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * Overrides the autoCommit property of the connection with the provided value.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param setAutoCommit   if true, sets AutoCommit to true for this execute statement.
     * @param batchSize       the maximum size of each batch to be executed.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(String inputXML, String batchUpdateSelectXPath, String paramMappingXML, boolean setAutoCommit, int batchSize) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeBatchXML", new Object[]{inputXML, paramMappingXML, Boolean.valueOf(setAutoCommit)});
        }

        // Parse the input xml source
        Document inputDoc = null;
        if (inputXML != null) {
            inputDoc = DOMUtils.parseXML(inputXML, true);
        }

        // Parse the param mapping xml source
        Document paramMappingDoc = null;
        if (paramMappingXML != null) {
            paramMappingDoc = DOMUtils.parseXML(paramMappingXML, true);
        }

        int updateCount = executeBatchXML(inputDoc, batchUpdateSelectXPath, paramMappingDoc, setAutoCommit, batchSize);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeBatchXML", String.valueOf(updateCount));
        }
        return updateCount;
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(Node inputXML, String batchUpdateSelectXPath, Node paramMappingXML) throws SQLException {
        return executeBatchXML(inputXML, batchUpdateSelectXPath, paramMappingXML, false, getExecuteBatchSize());
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * Overrides the autoCommit property of the connection with the provided value.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param setAutoCommit   if true, sets AutoCommit to true for this execute statement.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(Node inputXML, String batchUpdateSelectXPath, Node paramMappingXML, boolean setAutoCommit) throws SQLException {
        return executeBatchXML(inputXML, batchUpdateSelectXPath, paramMappingXML, setAutoCommit, getExecuteBatchSize());
    }

    /**
     * Execute the update type of Stored Procedure with the given input XML as input parameters.
     * Return the updateCount.
     * The paramMappingXML specifies the configuration definition mapping XML elements to procedure parameters.
     * Overrides the autoCommit property of the connection with the provided value.
     *
     * @param inputXML        input XML parameters
     * @param batchUpdateSelectXPath the XPath expression to locate the sequence of rows for batch update
     * @param paramMappingXML the configuration definition mapping XML elements to procedure parameters.
     * @param setAutoCommit   if true, sets AutoCommit to true for this execute statement.
     * @param batchSize       the maximum size of each batch to be executed.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatchXML(Node inputXML, String batchUpdateSelectXPath, Node paramMappingXML, boolean setAutoCommit, int batchSize) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeBatchXML", new Object[]{inputXML, String.valueOf(setAutoCommit)});
        }

        int updateCount = 0;
        long startTime = System.currentTimeMillis();

        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();

        // Get a Connection
        Connection conn = null;
        CallableStatement cs = null;
        String spCall = null;
        boolean prevAutoCommit = false;
        try {
            conn = getAppConnection();

            if (setAutoCommit) {
                prevAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(true);
            }

            NodeList nl = org.apache.xpath.XPathAPI.selectNodeList(inputXML, batchUpdateSelectXPath);

            if (nl.getLength() > 0) {

                Node firstNode = nl.item(0);

                // Get Parameter ColumnDesc Vector
                Vector paramColumnDescVector = spDAOHelper.
                        getParameterColumnDescVector(getSPDaoKeyName(), getStoredProcedureName(), getDataRecordMapping());
                Record inputRecord = spDAOHelper.mapInputXMLToRecord(firstNode, paramMappingXML, paramColumnDescVector,
                        getStoredProcedureName().getProcedureName());

                Vector requiredParamVector = getRequiredParameterVector(inputRecord);

                // Create a CallableStatement
                spCall = getStoredProcedureCallSQL(requiredParamVector);
                l.logp(Level.FINE, getClass().getName(), "executeBatchXML", "Preparing batch statement'" + spCall + "' on connection: " + conn);
                cs = conn.prepareCall(spCall);
                CallableStatementSupport css = new CallableStatementSupport(cs);

                // Iterate through the InputRecordSet, adding each input record as a batch
                for (int recordIdx = 0; recordIdx < nl.getLength(); recordIdx++) {
                    long loopStartTime = System.currentTimeMillis();
                    Node recordNode = nl.item(recordIdx);

                    spDAOHelper.setCallableStatementParameters(css, requiredParamVector, recordNode, paramMappingXML, getStoredProcedureName(), getSPDaoKeyName());

                    // Add the batch
                    l.logp(Level.FINE, getClass().getName(), "executeBatchXML", "Adding a batch with input record'" + DOMUtils.formatNode(recordNode) + "'");
                    cs.addBatch();

                    // Execute the current batch
                    if ((++updateCount % batchSize) == 0) {
                        l.logp(Level.FINE, getClass().getName(), "executeBatchXML", "Executing batch statement'" + spCall + "'");
                        cs.executeBatch();
                    }

                    long endTime = System.currentTimeMillis();
                    Double elapsedTime = (endTime - loopStartTime) / 1000.0;
                    if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
                        logStoredProcedure(elapsedTime);
                    }
                }

                // Execute the batch, and return the total number of executed batch statements.
                if (updateCount > 0 && (updateCount % batchSize) > 0) {
                    long batchStartTime = System.currentTimeMillis();
                    l.logp(Level.FINE, getClass().getName(), "executeBatch", "Executing batch statement'" + spCall + "'");
                    cs.executeBatch();
                    long endTime = System.currentTimeMillis();
                    Double elapsedTime = (endTime - batchStartTime) / 1000.0;
                    if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
                        logStoredProcedure(elapsedTime);
                    }
                }

            }else{
                l.logp(Level.WARNING, getClass().getName(), "executeBatchXML", "No record in xml data");
            }

            if (setAutoCommit) {
                conn.setAutoCommit(prevAutoCommit);
            }

        }
        catch (SQLException se) {
            throw se;
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute '" + getStoredProcedureName().getFullyQualifiedSPName() + "' in batch as update.", e);
            l.throwing(getClass().getName(), "executeBatchXML", ae);
            throw ae;
        }
        finally {
            if (hasExternalConnection()) {
                DatabaseUtils.close(cs);
            }
            else {
                DatabaseUtils.close(cs, conn);
            }
        }

        long endTime = System.currentTimeMillis();
        if (l.isLoggable(Level.FINE)) {
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeBatchXML",
                "Executed the following batch procedure in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCall);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeBatchXML", String.valueOf(updateCount));
        }
        return updateCount;
    }


    /**
     * Internal method to execute the Stored Procedure with the given input Record,
     * and return the executed CallableStatement in the provided CallableStatementSupport.
     * If the stored procedure is executed for Update, the return value contains the number of rows updated,
     * as returned from the CallableStatement.executeUpdate() method.
     *
     * @param inputRecord           input data
     * @param requiredParamVector   parameters required
     * @param conn
     * @param css
     * @param forUpdate
     * @return
     * @throws SQLException
     */
    protected int executeStoredProcedureForXML(Record inputRecord, Vector requiredParamVector, Connection conn, CallableStatementSupport css, boolean forUpdate) throws SQLException {
        int returnValue = 0;
        try {
            returnValue = executeStoredProcedureForXMLInternal(inputRecord, requiredParamVector, conn, css, forUpdate);
        }
        catch (SQLException e) {
            boolean isRecovered = getErrorHandlerController().invokeErrorHandlers(e);
            if (isRecovered) {
                returnValue = executeStoredProcedureForXMLInternal(inputRecord, requiredParamVector, conn, css, forUpdate);
            }
            else {
                throw e;
            }
        }
        return returnValue;
    }

    /**
     * Private method to execute the Stored Procedure with the given input Record,
     * and return the executed CallableStatement in the provided CallableStatementSupport.
     *
     * @param inputRecord
     * @param requiredParamVector
     * @param conn
     * @param css
     * @param forUpdate
     * @return
     * @throws SQLException
     */
    protected int executeStoredProcedureForXMLInternal(Record inputRecord, Vector requiredParamVector, Connection conn, CallableStatementSupport css, boolean forUpdate) throws SQLException {
        l.entering(getClass().getName(), "executeStoredProcedureForXMLInternal");

        int updateCount = 0;
        long startTime = System.currentTimeMillis();

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureForXMLInternal", "Preparing to execute '" + getStoredProcedureName() + "'");
        }
        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();


        // Determine if this is an Oracle Function
        if (spDAOHelper.isOracleFunction(requiredParamVector)) {
            setIsOracleFunction(true);
        }

        // Create a CallableStatement
        String spCall = getStoredProcedureCallSQL(requiredParamVector);
        CallableStatement cs = conn.prepareCall(spCall);
        css.setCallableStatement(cs);

        // Add the Input Parameters to the CallableStatement
        spDAOHelper.setCallableStatementParameters(css, requiredParamVector, null, inputRecord, getStoredProcedureName(), getSPDaoKeyName());

        // Execute the CallableStatement
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureForXMLInternal", "Executing " + (forUpdate ? "for Update " : "") + "'" + spCall + "' with input record '" + inputRecord.toString() + "'");
            if (hasDataRecordMappings()) {
                l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureForXMLInternal", "Using the following DataRecordMapping: " + getDataRecordMapping());
            }
        }
        long stpStartTime = System.currentTimeMillis();
        if (forUpdate) {
            updateCount = cs.executeUpdate();
        }
        else {
            cs.execute();
        }

        long endTime = System.currentTimeMillis();
        if ((!forUpdate && getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis()) ||
            l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((!forUpdate && endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeStoredProcedureForXMLInternal",
                "Executed the following procedure in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        Double elapsedTime = (endTime - stpStartTime) / 1000.0;
        if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
            logStoredProcedure(elapsedTime);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeStoredProcedureForXMLInternal", String.valueOf(updateCount));
        }
        return updateCount;
    }

    /**
     * Iterate through the OUT and INOUT parameters of the statement,
     * placing the non-cursor results into the SummaryRecord,
     * loading the the first ResultSet into the resultant XML RecordSet.
     *
     * @param css
     * @param requiredParamVector
     * @param outputConfigXML
     * @param loadProcessor
     * @return
     * @throws SQLException
     */
    protected Document processResultToXML(CallableStatementSupport css, Vector requiredParamVector,
                                          Node outputConfigXML, XMLRecordLoadProcessor loadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processResultToXML", new Object[]{css, requiredParamVector, outputConfigXML, loadProcessor});
        }

        if (outputConfigXML == null) {
            outputConfigXML = getDefaultOutputConfigXMLAsNode();
        }

        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();

        // Build the Summary Record from the OUT, INOUT and RETURNVALUE sp parameters
        Record summaryRecord = buildSummaryRecord(css, requiredParamVector);

        // Get the ResultSet if one exists
        ResultSet rs = getResultSet(css, requiredParamVector);
        Vector rsColumnDescVector = null;

        if (rs != null) {
            // Get ResultSet ColumnDesc Vector
            rsColumnDescVector = spDAOHelper.getResultSetColumnDescVector(getSPDaoKeyName(), rs, getDataRecordMapping());
        }

        // Build the resulting RecordSet from the SummaryRecord and ResultSet
        XMLRecordSetBuilder xmlRsBuilder = XMLRecordSetBuilder.getXMLInstance(rs, rsColumnDescVector, summaryRecord);
        if (hasDataRecordMappings()) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "processResultToXML", "DataRecordMappings = " + getDataRecordMapping());
            }
            xmlRsBuilder.setDataRecordMapping(getDataRecordMapping());
        }
        Document resultDoc = xmlRsBuilder.buildToXML(outputConfigXML, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processResultToXML", resultDoc);
        }
        return resultDoc;
    }

    protected Node getDefaultOutputConfigXMLAsNode() {
        if (c_defaultOutputConfigXMLNode == null) {
            c_defaultOutputConfigXMLNode = DOMUtils.parseXML(getDefaultOutputConfigXML(), true);
        }
        return c_defaultOutputConfigXMLNode;
    }

    public String getDefaultOutputConfigXML() {
        if (m_defaultOutputConfigXML == null) {
            m_defaultOutputConfigXML = DEFAULT_OUTPUT_CONFIG_XML;
        }
        return m_defaultOutputConfigXML;
    }

    public void setDefaultOutputConfigXML(String defaultOutputConfigXML) {
        m_defaultOutputConfigXML = defaultOutputConfigXML;
    }

    protected String getBeanName() {
        return BEAN_NAME;
    }


    public Connection getAppConnection() throws SQLException {
        if (hasExternalConnection()) {
            return m_externalConnection;
        }
        else {
            return super.getAppConnection();
        }
    }

    private void setExternalConnection(Connection externalConnection) {
        m_externalConnection = externalConnection;
    }

    private boolean hasExternalConnection() {
        return m_externalConnection != null;
    }

    private String m_defaultOutputConfigXML;
    private Connection m_externalConnection;
    private final Logger l = LogUtils.getLogger(getClass());

    private static XMLRecordLoadProcessor c_defaultXMLLoadProcessor = new DefaultXMLRecordLoadProcessor();
    private static final String DEFAULT_OUTPUT_CONFIG_XML =
        "<" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":outputConfig xmlns:" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + "=\"" + SPDAO_NS_URI + "\">" +
            "<Response>" +
                "<" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":get-remaining-columns/>" +
                "<" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":row-iterator>" +
                "\n  <Row>" +
                "<" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":get-remaining-columns/>" +
                "\n  </Row>" +
                "</" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":row-iterator>" +
            "\n</Response>" +
        "</" + XMLStoredProcedureDAO.SPDAO_NS_PREFIX + ":outputConfig>";
    private static Node c_defaultOutputConfigXMLNode;
}
