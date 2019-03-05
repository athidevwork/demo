package dti.oasis.data;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.InfoGroup;
import dti.oasis.busobjs.PagingFields;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ErrorHandlerController;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.log.StoredProcedureLogLevel;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordSetToTXTLoadProcessor;
import dti.oasis.recordset.RecordToInfoMappingLoadProcessor;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import oracle.jdbc.OracleTypes;
import oracle.sql.OPAQUE;
import oracle.xdb.XMLType;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides ability to execute a named Stored Procedure, providing input parameters in the form of a Record.
 * If the query returns a Reference Cursor, it is loaded into a RecordSet and returned.
 * If the query is an update stored procedure, use the executeUpdate, which returns the number of updated records.
 * Use the executeBatch to execute an update stored procedure once per Record in the provided RecordSet,
 * returning the number of updated records.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/01/2008       sxm         Added executeReadonly().
 * 06/16/2008       wer         Refactored for use with the new XMLStoredProcedureDAO
 * 07/09/2008       yhchen      #83894 call getColumnValue to get column value.
 * 06/05/2008       fcb         93139: executeReadonly: setAutoCommit set to true to ensure that Oracle closes the
 *                              cursors and OPEN_CUSOR stays within reasonable range.
 * 06/10/2008       fcb         2 new overloaded executeReadonly methods added.
 * 12/23/2009       James       Issue#102265 Fix the StoredProcedureDAOHelper to leave
 *                              stored procedure parameters unset if there is not Field
 *                              in the inputRecord and the parameter has a default value
 *                              setting in the stored procedure definition
 * 09/21/2010       fcb         111824 - added support for Oracle XMLType
 * 8/5/2015         jxgu        Issue#164269 do not call setAutocommit if it is not necessary
 *                              because setAutoCommit will commit in Weblogic 12.1.3
 * 06/24/2016       wdang       Issue#170303 Refactor executeBatch() to adapt the recordset that
 *                              has different field list in each record.
 * ---------------------------------------------------
 */
public class StoredProcedureDAO {

    /**
     * The bean name of this class in the ApplicationContext.
     */
    public static final String BEAN_NAME = "StoredProcedureDAO";

    /**
     * Default batch size to use when executing batch updates.
     */
    public static final int DEFAULT_EXECUTE_BATCH_SIZE = 100;

    /**
     * Field name holding the update count if an update is executed.
     */
    public static final String UPDATE_COUNT_FIELD = "updateCount";

    /**
     * Field name holding the Oracle Function return value.
     * May be located in the output record for executeUpdate method,
     * or in the summary record for the execute method.
     */
    public static final String RETURN_VALUE_FIELD = "returnValue";

    /**
     * Retrieves the StoredProcedureDAO for the specified, fully qualified stored procedure.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     */
    public static StoredProcedureDAO getInstance(String spName) {
        return getStoredProcedureDAO(spName, BEAN_NAME, null);
    }

    /**
     * Retrieves the StoredProcedureDAO for the specified, fully qualified stored procedure,
     * using the given Data Record Mapping definitions to map between fields and parameters/columns.
     *
     * @param spName the stored procedure name, with the package prefix if in a package
     */
    public static StoredProcedureDAO getInstance(String spName, DataRecordMapping dataRecordMapping) {
        return getStoredProcedureDAO(spName, BEAN_NAME, dataRecordMapping);
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters.
     * Return the resulting ResultSet, if any, in a RecordSet,
     * adding any non-cursor OUT, INOUT or Function Return values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public RecordSet execute(Record inputRecord) throws SQLException {
        return execute(inputRecord, c_defaultLoadProcessor);
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters.
     * The RecordLoadProcessor is used to post-process each record, and the entire record set.
     * Return a RecordSet with all rows from the first ResultSet if any,
     * adding any non-cursor OUT or INOUT result values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public RecordSet execute(Record inputRecord, RecordLoadProcessor loadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "execute", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet resultSet = null;
        long startTime = System.currentTimeMillis();

        // Get a Connection
        Connection conn = null;
        CallableStatementSupport css = new CallableStatementSupport();
        try {
            conn = getAppConnection();

            Vector requiredParamVector = getRequiredParameterVector(inputRecord);

            executeStoredProcedure(inputRecord, requiredParamVector, conn, css, false);

            // Process the result, placing any OUT parameters into the Summary Record, and returning a RecordSet with the first ResultSet (if one exists).
            resultSet = processResult(css, inputRecord, requiredParamVector, loadProcessor);

        } finally {
            DatabaseUtils.close(css.getCallableStatement(), conn);
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "execute",
                "Executed the following procedure and processed the results in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "execute", resultSet);
        }
        return resultSet;
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters while keeping AutoCommit as false.
     * Return the resulting ResultSet, if any, in a RecordSet,
     * adding any non-cursor OUT or INOUT result values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public RecordSet executeReadonly(Record inputRecord) throws SQLException {
        return executeReadonly(inputRecord, c_defaultLoadProcessor, false);
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters.
     * The RecordLoadProcessor is used to post-process each record, and the entire record set.
     * Return a RecordSet with all rows from the first ResultSet if any,
     * adding any non-cursor OUT or INOUT result values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @param isAutoCommit sets the connection autocommit to the value that is passed. The default value is false.
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    @Deprecated
    public RecordSet executeReadonly(Record inputRecord, boolean isAutoCommit) throws SQLException {
        return executeReadonly(inputRecord, c_defaultLoadProcessor, isAutoCommit);
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters while keeping AutoCommit as false.
     * The RecordLoadProcessor is used to post-process each record, and the entire record set.
     * Return a RecordSet with all rows from the first ResultSet if any,
     * adding any non-cursor OUT or INOUT result values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public RecordSet executeReadonly(Record inputRecord, RecordLoadProcessor loadProcessor) throws SQLException {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "executeReadonly", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet resultSet = executeReadonly(inputRecord, loadProcessor, false);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeReadonly", resultSet);
        }
        return resultSet;
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters.
     * The RecordLoadProcessor is used to post-process each record, and the entire record set.
     * Return a RecordSet with all rows from the first ResultSet if any,
     * adding any non-cursor OUT or INOUT result values to the contained SummaryRecord.
     *
     * @param inputRecord input Record parameters
     * @loadProcessor the load processor
     * @isAutoCommit isAutoCommit sets the connection autocommit to the value that is passed. The default value is false.
     * @return the resulting RecordSet
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public RecordSet executeReadonly(Record inputRecord, RecordLoadProcessor loadProcessor, boolean isAutoCommit) throws SQLException {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "executeReadonly", new Object[]{inputRecord, loadProcessor, String.valueOf(isAutoCommit)});
        }

        RecordSet resultSet = null;
        boolean autoCommit = false;
        long startTime = System.currentTimeMillis();

        // Get a Connection
        Connection conn = null;
        CallableStatementSupport css = new CallableStatementSupport();
        try {
            conn = getStoredProcedureDAOHelper().getReadonlyConnection();

            autoCommit = conn.getAutoCommit();
            if (autoCommit != isAutoCommit){
                conn.setAutoCommit(isAutoCommit);
            }

            Vector requiredParamVector = getRequiredParameterVector(inputRecord);

            executeStoredProcedure(inputRecord, requiredParamVector, conn, css, false);

            // Process the result, placing any OUT parameters into the Summary Record, and returning a RecordSet with the first ResultSet (if one exists).
            resultSet = processResult(css, inputRecord, requiredParamVector, loadProcessor);

        } finally {
            if (conn != null){
                if (autoCommit != isAutoCommit){
                    conn.setAutoCommit(autoCommit);
                }
            }
            DatabaseUtils.close(css.getCallableStatement(), conn);
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeReadonly",
                "Executed the following procedure and processed the results in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeReadonly", resultSet);
        }
        return resultSet;
    }

    /**
     * For use when the resulting data in the Java Layer. It is easier to access the attributes of a record
     * with specific accessor methods than using the generic Record interface.
     * The user is set to a default user.
     * <p/>
     * Each record in the JDBC ResultSet is mapped first to a Record. Next, the RecordToInfoMappingLoadProcessor is used
     * to map the resulting Record to an instance of the specified infoClass, and to add it to the given infoGroup.
     * <p/>
     * The RecordToInfoMappingLoadProcessor can be used directly with any other execute() method that takes a RecordLoadProcessor
     * as a parameter. The only difference is that this method does not maintain a reference the the resulting Records.
     * So the Records are available for garbage collection as soon as they are mapped to the desired Info object.
     */
    public void execute(Record inputRecord, InfoGroup infoGroup, Class infoClass) throws SQLException {
        execute(inputRecord, c_defaultLoadProcessor, infoGroup, infoClass);
    }


    /**
     * For use when the resulting data in the Java Layer. It is easier to access the attributes of a record
     * with specific accessor methods than using the generic Record interface.
     * <p/>
     * Each record in the JDBC ResultSet is mapped first to a Record, and passed to the RecordLoadProcessor for any required manipulation.
     * For example, if you need to setup an additional attribute based on values returned from the result set,
     * use the RecordLoadProcessor to derive the new attribute(s), and add them to the Record. After executing the
     * RecordLoadProcessor.postProcessRecord(), the RecordToInfoMappingLoadProcessor is used to map the resulting Record
     * to an instance of the specified infoClass, and to add it to the given infoGroup.
     * <p/>
     * The RecordToInfoMappingLoadProcessor can be used directly with any other execute() method that takes a RecordLoadProcessor
     * as a parameter. The only difference is that this method does not maintain a reference the the resulting Records.
     * So the Records are available for garbage collection as soon as they are mapped to the desired Info object.
     */
    public void execute(Record inputRecord, RecordLoadProcessor loadProcessor, InfoGroup infoGroup, Class infoClass) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "execute", new Object[]{inputRecord, loadProcessor, infoGroup, infoClass.getName()});
        }

        long startTime = System.currentTimeMillis();

        // Get a Connection
        Connection conn = null;
        CallableStatementSupport css = new CallableStatementSupport();
        try {
            conn = getAppConnection();

            Vector requiredParamVector = getRequiredParameterVector(inputRecord);

            executeStoredProcedure(inputRecord, requiredParamVector, conn, css, false);

            // Process the result, placing any OUT parameters into the Summary Record, and returning a RecordSet with the first ResultSet (if one exists).
            processResult(css, inputRecord, requiredParamVector, loadProcessor, infoGroup, infoClass);

        } catch(SQLException se) {
            throw se;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute '" + getStoredProcedureName().getFullyQualifiedSPName() + "'.", e);
            l.throwing(getClass().getName(), "executeBatch", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(css.getCallableStatement(), conn);
        }

        long endTime = System.currentTimeMillis();
        if (getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis() || l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "execute",
                "Executed the following procedure and processed the results in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        l.exiting(getClass().getName(), "execute", infoGroup);
    }

    /**
     * Execute the Stored Procedure with the given input Record as input parameters,
     * and write the output as TXT to the proviced OutputStream.
     * Column Headers are written on the first line, and fields are tab delimited.
     * This is a convenience method to calling execute(...) with a RecordSetToTXTLoadProcessor.
     * If you'd like to change the column delimiter, or not write the column headers,
     * use the execute method with the appropriate setting on the RecordSetToTXTLoadProcessor.
     *
     * @param inputRecord input Record parameters
     * @param ostream
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public void executeToTXT(Record inputRecord, OutputStream ostream) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeToTXT", new Object[]{inputRecord, ostream});
        }

        // Create a RecordSetToTXTLoadProcessor
        RecordSetToTXTLoadProcessor txtLoadProcessor = new RecordSetToTXTLoadProcessor(ostream);

        // execute the sp
        execute(inputRecord, txtLoadProcessor);

        l.exiting(getClass().getName(), "executeToTXT");
    }

    /**
     * TODO: create "public Iterator executeWithMultipleResultSets(Record inputRecord, Vector loadProcessors)"
     */

    /**
     * Execute the update type of Stored Procedure with the given input Record as input parameters.
     * Return a Record containing any non-cursor OUT, INOUT or Function Return values,
     * and the updateCount (so long as there is no OUT parameter named updateCount).
     *
     * @param inputRecord input Record parameters
     * @return a Record containing any OUT parameters from the Stored Procedure,
     *  and the updateCount (so long as there is no OUT parameter named updateCount).
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Record executeUpdate(Record inputRecord) throws SQLException {
        return executeUpdate(inputRecord, false);
    }


    /**
     * Execute the update type of Stored Procedure with the given input Record as input parameters.
     * Return a Record containing any non-cursor OUT, INOUT or Function Return values,
     * and the updateCount (so long as there is no OUT parameter named updateCount).
     * Overrides the autoCommit property of the connection with the provided value.
     *
     * @param inputRecord input Record parameters
     * @param setAutoCommit if true, sets AutoCommit to true for this execute statement.
     * @return a Record containing any OUT parameters from the Stored Procedure,
     *  and the updateCount (so long as there is no OUT parameter named updateCount).
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public Record executeUpdate(Record inputRecord, boolean setAutoCommit) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeUpdate", new Object[]{inputRecord, String.valueOf(setAutoCommit)});
        }

        int updateCount = 0;
        Record result = null;
        long startTime = System.currentTimeMillis();

        // Get a Connection
        Connection conn = null;
        CallableStatementSupport css = new CallableStatementSupport();
        boolean prevAutoCommit = false;
        try {
            conn = getAppConnection();

            if (setAutoCommit) {
                prevAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(true);
            }

            Vector requiredParamVector = getRequiredParameterVector(inputRecord);

            updateCount = executeStoredProcedure(inputRecord, requiredParamVector, conn, css, true);

            // Process the result, placing any OUT parameters into the Summary Record, and returning a RecordSet with the first ResultSet (if one exists).
            RecordSet resultSet = processResult(css, inputRecord, requiredParamVector, c_defaultLoadProcessor);
            result = resultSet.getSummaryRecord();
            if (!result.hasFieldValue(UPDATE_COUNT_FIELD)) {
                result.setFieldValue(UPDATE_COUNT_FIELD, new Integer(updateCount));
            }

            if (setAutoCommit) {
                conn.setAutoCommit(prevAutoCommit);
            }

        } catch(SQLException se) {
            throw se;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute '" + getStoredProcedureName().getFullyQualifiedSPName() + "' as update.", e);
            l.throwing(getClass().getName(), "executeBatch", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(css.getCallableStatement(), conn);
        }

        long endTime = System.currentTimeMillis();
        if (l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG);
            l.logp(Level.FINE, getClass().getName(), "executeUpdate",
                "Executed the following procedure and processed the results in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeUpdate", result);
        }
        return result;
    }

    /**
     * Execute the Stored Procedure in batch mode with the given input RecordSet as input parameters.
     * The user is set to a default user.
     * The batch size is set to the default batch size as specified by the "db.default.batch.size" property.
     * Return the updateCount.
     *
     * @param inputRecordSet a collection of Records to execute in batch
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatch(RecordSet inputRecordSet) throws SQLException {
        return this.executeBatch(inputRecordSet, getExecuteBatchSize());
    }


    /**
     * Execute the Stored Procedure in batch mode with the given input RecordSet as input parameters.
     * Return the updateCount.
     *
     * @param inputRecordSet a collection of Records to execute in batch
     * @param batchSize the maximum size of each batch to be executed.
     * @return the update count
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    public int executeBatch(RecordSet inputRecordSet, int batchSize) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "executeBatch", new Object[]{inputRecordSet, String.valueOf(batchSize)});
        }

        long startTime = System.currentTimeMillis();

        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();

        // Get a Connection
        Connection conn = null;
        CallableStatement cs = null;
        String spCall = null;
        CallableStatementSupport css = null;
        int recordIdx = 0;
        try {
            conn = getAppConnection();
            if (inputRecordSet.getSize() > 0) {
                Record inputRecord = null;
                Vector preVector = null;
                Vector curVector = null;
                int batchCount = 0;
                // Iterate through the InputRecordSet, adding each input record as a batch
                for (recordIdx = 0; recordIdx < inputRecordSet.getSize(); recordIdx++) {
                    long loopStartTime = System.currentTimeMillis();
                    inputRecord = inputRecordSet.getRecord(recordIdx);
                    curVector = getRequiredParameterVector(inputRecord);

                    if (recordIdx == 0 || !preVector.equals(curVector)) {
                        // Execute previous batch if any
                        if (batchCount > 0) {
                            l.logp(Level.FINE, getClass().getName(), "executeBatch", "Executing batch statement'" + spCall + "'");
                            cs.executeBatch();
                            batchCount = 0;
                            // close previous statement if any
                            DatabaseUtils.close(cs);
                        }
                        // Create new CallableStatement
                        spCall = getStoredProcedureCallSQL(curVector);
                        l.logp(Level.FINE, getClass().getName(), "executeBatch", "Preparing batch statement'" + spCall + "' on connection: " + conn);
                        cs = conn.prepareCall(spCall);
                        css = new CallableStatementSupport(cs);
                    }

                    // Add the Input Parameters to the CallableStatement
                    spDAOHelper.setCallableStatementParameters(css, curVector, getDataRecordMapping(), inputRecord, getStoredProcedureName(), getSPDaoKeyName());

                    // Add the batch
                    l.logp(Level.FINE, getClass().getName(), "executeBatch", "Adding a batch with input record'" + inputRecord + "'");
                    cs.addBatch();
                    batchCount ++;

                    // Execute current batch if exceed batch size
                    if ((batchCount % batchSize) == 0) {
                        l.logp(Level.FINE, getClass().getName(), "executeBatch", "Executing batch statement'" + spCall + "'");
                        cs.executeBatch();
                        batchCount = 0;
                    }
                    preVector = curVector;
                    long endTime = System.currentTimeMillis();
                    Double elapsedTime = (endTime - loopStartTime) / 1000.0;
                    if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
                        logStoredProcedure(elapsedTime);
                    }
                }
                // Execute remain batch at the end of loop
                if (batchCount > 0) {
                    long batchStartTime = System.currentTimeMillis();
                    l.logp(Level.FINE, getClass().getName(), "executeBatch", "Executing batch statement'" + spCall + "'");
                    cs.executeBatch();
                    batchCount = 0;
                    long endTime = System.currentTimeMillis();
                    Double elapsedTime = (endTime - batchStartTime) / 1000.0;
                    if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
                        logStoredProcedure(elapsedTime);
                    }
                }
            }
            else{
                l.logp(Level.WARNING, getClass().getName(), "executeBatch", "There is no record in RecordSet");
            }
        } catch(SQLException se) {
            throw se;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute '" + getStoredProcedureName().getFullyQualifiedSPName() + "' in batch as update.", e);
            l.throwing(getClass().getName(), "executeBatch", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(cs, conn);
        }

        long endTime = System.currentTimeMillis();
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeBatch",
                "Executed the following batch procedure in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCall);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeBatch", String.valueOf(recordIdx));
        }
        return recordIdx;
    }

    /**
     * Internal method to execute the Stored Procedure with the given input Record as input parameters,
     * and return the executed CallableStatement in the provided CallableStatementSupport.
     * If the stored procedure is executed for Update, the return value contains the number of rows updated,
     * as returned from the CallableStatement.executeUpdate() method.
     * @param inputRecord           input Record parameters
     * @param requiredParamVector   equired parameters
     * @param conn
     * @param css                   an empty CallableStatementSupport object that will contain the executed CallableStatement upon return.
     * @param forUpdate
     * @return
     * @throws SQLException
     */
    protected int executeStoredProcedure(Record inputRecord, Vector requiredParamVector, Connection conn, CallableStatementSupport css, boolean forUpdate) throws SQLException {
        int returnValue = 0;
        try {
            returnValue = executeStoredProcedureInternal(inputRecord, requiredParamVector, conn, css, forUpdate);
        } catch (SQLException e) {
            boolean isRecovered = getErrorHandlerController().invokeErrorHandlers(e);
            if(isRecovered) {
                returnValue = executeStoredProcedureInternal(inputRecord, requiredParamVector, conn, css, forUpdate);
            }
            else {
                throw e;
            }
        }
        return returnValue;
    }


    /**
     * Private method to execute the Stored Procedure with the given input Record as input parameters,
     * and return the executed CallableStatement in the provided CallableStatementSupport.
     * @param inputRecord
     * @param requiredParamVector
     * @param conn
     * @param css
     * @param forUpdate
     * @return
     * @throws SQLException
     */
    protected int executeStoredProcedureInternal(Record inputRecord, Vector requiredParamVector, Connection conn, CallableStatementSupport css, boolean forUpdate) throws SQLException {
        l.entering(getClass().getName(), "executeStoredProcedureInternal");

        int updateCount = 0;
        long startTime = System.currentTimeMillis();

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureInternal", "Preparing to execute '" + getStoredProcedureName() + "'");
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
        spDAOHelper.setCallableStatementParameters(css, requiredParamVector, getDataRecordMapping(), inputRecord, getStoredProcedureName(), getSPDaoKeyName());

        // Execute the CallableStatement
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureInternal", "Executing " + (forUpdate ? "for Update " : "") + "'" + spCall + "' with input record '" + inputRecord + "'");
            if (hasDataRecordMappings()) {
                l.logp(Level.FINE, getClass().getName(), "executeStoredProcedureInternal", "Using the following DataRecordMapping: " + getDataRecordMapping());
            }
        }
        long stpStartTime = System.currentTimeMillis();
        if (forUpdate) {
            updateCount = cs.executeUpdate();
        } else {
            cs.execute();
        }

        long endTime = System.currentTimeMillis();
        if ((!forUpdate && getWarningTime() > 0 && endTime - startTime > getWarningTimeInMillis()) ||
            l.isLoggable(Level.FINE)) {
            String spCallDebugMsg = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_DEBUG_MSG, "");
            l.logp((!forUpdate && endTime - startTime > getWarningTimeInMillis() ? Level.WARNING : Level.FINE), getClass().getName(), "executeStoredProcedureInternal",
                "Executed the following procedure in " + ((endTime - startTime) / 1000.0) + " seconds: " + spCallDebugMsg);
        }

        Double elapsedTime = (endTime - stpStartTime) / 1000.0;
        if(!getStoredProcedureName().getProcedureName().equalsIgnoreCase(PROC_INSERT_STORED_PROC_LOG_ITEM) && isLogStoredProcedure()) {
            logStoredProcedure(elapsedTime);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "executeStoredProcedureInternal", String.valueOf(updateCount));
        }
        return updateCount;
    }

    /*
    TODO: Make sure it works if not Y or N is defined
     */
    private Boolean isStoredProcedureLoggable(){
        return YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("LOG_WEB_SP_CALLS", "N")).booleanValue();
    }

    public Boolean isLogStoredProcedure(){
        return StoredProcedureDAOHelper.isStoredProcedureLoggingEnabled() && isStoredProcedureLoggable();
    }

    private String getStoredProcedureLogType(){
        return SysParmProvider.getInstance().getSysParm("LOG_WEB_SP_TYPE", "ALL");
    }

    protected void logStoredProcedure(Double procTime){
        String source = LogUtils.getPage();
        String userName = LogUtils.getUserId();
        String storedProcedureName = getStoredProcedureName().getFullyQualifiedSPName();
        String arguments = (String) RequestStorageManager.getInstance().get(StoredProcedureDAOHelper.SP_CALL_LOG_MSG, "");
        logStoredProcedure(source, userName, storedProcedureName, procTime, arguments, "", "STPDAO");
    }

    public void logStoredProcedure(String source, String userName, String storedProcedureName, Double procTime, String arguments, String lovSqlOrProc,
                                   String logType){
        try {
            long startTime = System.currentTimeMillis();
            if (c_stpDaoLogger.isLoggable(StoredProcedureLogLevel.STORED_PROCEDURE)) {
                if (getStoredProcedureLogType().equalsIgnoreCase("ALL") || getStoredProcedureLogType().equalsIgnoreCase("FILE"))
                    logStoredProcedureToFile(source, userName, storedProcedureName, procTime, arguments, lovSqlOrProc);
                if (getStoredProcedureLogType().equalsIgnoreCase("ALL") || getStoredProcedureLogType().equalsIgnoreCase("DB"))
                    logStoredProcedureToDatabase(source, userName, storedProcedureName, procTime, arguments, lovSqlOrProc,
                        logType);

            }
            long endTime = System.currentTimeMillis();
            Double elapsedTime = (endTime - startTime) / 1000.0;
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "logStoredProcedure",
                    "Stored Procedure Logging Stats - Total Time: " + elapsedTime + " Stored Procedure: " + getStoredProcedureName().getFullyQualifiedSPName());
            }
        }  catch (Exception e) {
            //Do nothing: we want application to run without interuprion
            l.logp(Level.SEVERE , getClass().getName(), "logStoredProcedure",
                "STORED PROCEDURE LOGGING FAILED! "+e);
        }
    }

    private void logStoredProcedureToFile(String source, String userName, String storedProcedureName, Double procTime, String arguments, String lovSqlOrProc){
        long startTime = System.currentTimeMillis();
        c_stpDaoLogger.logp(StoredProcedureLogLevel.STORED_PROCEDURE, getClass().getName(), "logStoredProcedureToFile",
                "Logging Stored Procedure: " + getStoredProcedureName().getFullyQualifiedSPName() + " To: " + StoredProcedureDAOHelper.getStoredProcedureLogFilePattern() +
                        " Called from: " + LogUtils.getPage() + " Elapsed Time: " + procTime,
                new Object[]{source, userName, System.getProperty("user.name"), storedProcedureName, procTime, arguments, lovSqlOrProc});
        long endTime = System.currentTimeMillis();
        Double elapsedTime = (endTime - startTime) / 1000.0;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "logStoredProcedureToFile",
                    "Stored Procedure Logging Stats - Log to File Time: "+elapsedTime+" Stored Procedure: "+getStoredProcedureName().getFullyQualifiedSPName());
        }
    }

    private void logStoredProcedureToDatabase(String source, String userName, String storedProcedureName, Double procTime, String arguments, String lovSqlOrProc,
                                             String logType) {
        long startTime = System.currentTimeMillis();
        Record inputRecord = new Record();
        inputRecord.setFieldValue("datetime", new java.util.Date());
        inputRecord.setFieldValue("source", source);
        inputRecord.setFieldValue("userName", userName);
        inputRecord.setFieldValue("storedProcedureName", storedProcedureName);
        inputRecord.setFieldValue("processingTime", procTime);
        inputRecord.setFieldValue("arguments", arguments);
        inputRecord.setFieldValue("osUser", System.getProperty("user.name"));
        inputRecord.setFieldValue("logType", logType);
        inputRecord.setFieldValue("lovSqlOrProc", lovSqlOrProc);

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(PROC_INSERT_STORED_PROC_LOG_ITEM);
            spDao.executeUpdate(inputRecord);

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save Stored Procedure Log into " + PROC_INSERT_STORED_PROC_LOG_ITEM, e);
            l.throwing(getClass().getName(), "logStoredProcedureToDatabase", ae);
            throw ae;
        }
        long endTime = System.currentTimeMillis();
        Double elapsedTime = (endTime - startTime) / 1000.0;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "logStoredProcedureToDatabase",
                    "Stored Procedure Logging Stats - Log to Database Time: "+elapsedTime+" Stored Procedure: "+getStoredProcedureName().getFullyQualifiedSPName());
        }
    }

    protected static String PROC_INSERT_STORED_PROC_LOG_ITEM = "CS_INSERT_STORED_PROC_LOG_ITEM";

    /**
     * Iterate through the OUT and INOUT parameters of the statement,
     * placing the non-cursor results into the SummaryRecord,
     * loading the the first ResultSet into the resultant RecordSet.
     *
     * @param css
     * @param inputRecord
     * @param requiredParamVector
     * @param loadProcessor
     * @return
     * @throws SQLException
     */
    protected RecordSet processResult(CallableStatementSupport css, Record inputRecord, Vector requiredParamVector, RecordLoadProcessor loadProcessor) throws SQLException {
        l.entering(getClass().getName(), "processResult");

        RecordSet resultRecordSet = null;
        
        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();

        // Build the Summary Record from the OUT, INOUT and RETURNVALUE sp parameters
        Record summaryRecord = buildSummaryRecord(css, requiredParamVector);

        // Process the ResultSet if one exists
        ResultSet rs = getResultSet(css, requiredParamVector);
        if (rs != null) {

            // Get ResultSet ColumnDesc Vector
            Vector rsColumnDescVector = spDAOHelper.getResultSetColumnDescVector(getSPDaoKeyName(), rs, getDataRecordMapping());

            // Build the resulting RecordSet from the ResultSet
            RecordSetBuilder rsBuilder = RecordSetBuilder.getInstance(rs, rsColumnDescVector, summaryRecord);
            if (hasDataRecordMappings()) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "processResult", "DataRecordMappings = " + getDataRecordMapping());
                }
                rsBuilder.setDataRecordMapping(getDataRecordMapping());
            }
            if (PagingFields.hasRowsPerPage(inputRecord))
                resultRecordSet = rsBuilder.build(loadProcessor, PagingFields.getPageNum(inputRecord), PagingFields.getRowsPerPage(inputRecord));
            else
                resultRecordSet = rsBuilder.build(loadProcessor);

        }
        else {
            resultRecordSet = new RecordSet();
            resultRecordSet.setSummaryRecord(summaryRecord);
        }

        l.exiting(getClass().getName(), "processResult");
        return resultRecordSet;
    }

    /**
     * Iterate through the OUT and INOUT parameters of the statement,
     * placing the non-cursor results into the provided infoGroup
     * and loading the the first ResultSet as infoClass objects in the provided InfoGroup.
     * The non-cursor OUT and INOUT parameters are set in the provided infoGroup only if there is an accessor method
     *
     * @param css
     * @param inputRecord
     * @param requiredParamVector
     * @param loadProcessor
     * @param infoGroup
     * @param infoClass
     * @throws SQLException
     */
    protected void processResult(CallableStatementSupport css, Record inputRecord, Vector requiredParamVector, RecordLoadProcessor loadProcessor, InfoGroup infoGroup, Class infoClass) throws SQLException {
        l.entering(getClass().getName(), "processResult");

        // Create a RecordLoadProcessor to Build a InfoGroup from the resulting Records.
        RecordLoadProcessorChainManager loadProcessorChain = new RecordLoadProcessorChainManager(loadProcessor);
        RecordToInfoMappingLoadProcessor recordToInfoMapper = new RecordToInfoMappingLoadProcessor(infoGroup, infoClass);
        loadProcessorChain.addDataRecordLoadProcessor(recordToInfoMapper);

        RecordSet rs = processResult(css, inputRecord, requiredParamVector, loadProcessorChain);
        Record summaryRecord = rs.getSummaryRecord();

        // Map the SummaryRecord to the InfoGroup
        m_recordBeanMapper.map(summaryRecord, infoGroup);

        l.exiting(getClass().getName(), "processResult");
    }

    /**
     * Build the Summary Record from the OUT, INOUT and RETURNVALUE sp parameters.
     * @param css
     * @param paramColumnDescVector
     * @return
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    protected Record buildSummaryRecord(CallableStatementSupport css, Vector paramColumnDescVector) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildSummaryRecord", new Object[]{css, paramColumnDescVector});
        }

        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();

        // Initialize a SummaryRecord to hold INOUT, OUT, and RETURNVALUE parameters.
        Record summaryRecord = new Record();

        // Process the OUT, INOUT and RETURNVALUE sp parameters
        for (int i = 0; i < paramColumnDescVector.size(); i++) {
            ColumnDesc c = (ColumnDesc) paramColumnDescVector.elementAt(i);
            int spColumnIndex = i + 1;
            if (c.columnType == ColumnDesc.ColumnType.OUT || c.columnType == ColumnDesc.ColumnType.INOUT || c.columnType == ColumnDesc.ColumnType.RETURNVALUE) {
                try {
                    if (c.dataType != OracleTypes.OTHER && c.dataType != OracleTypes.CURSOR) {

                        // Add the output param to the summary record
                        Object outParamValue = css.getColumnValue(c);
                        if (c.dataType == ColumnDesc.OracleDataType.OPAQUE) {
                            Object xmlobject = css.getObject(c.colNumber);
                            if (xmlobject != null) {
                                OPAQUE xmlopaque= (OPAQUE) xmlobject;
                                outParamValue = XMLType.createXML(xmlopaque);
                            }
                        }
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processResult", "Retrieved OUT Parameter value '" + outParamValue + "' for column: " + c);
                        }

                        // Map the value if there is a mapping for the data field
                        if (hasDataRecordMappings() && getDataRecordMapping().containsMappingForDataField(c.javaColumnName))
                        {
                            // Map the output param name and value with the DataRecordFieldMapping.
                            DataRecordFieldMapping dataRecordFieldMapping = getDataRecordMapping().getMappingForDataField(c.javaColumnName);
                            dataRecordFieldMapping.mapDataFieldToRecord(outParamValue, summaryRecord);
                        } else {
                            summaryRecord.setField(c.javaColumnName, new Field(outParamValue));
                        }
                    }
                } catch (AppException ae) {
                    throw ae;
                } catch (Exception e) {
                    throw new AppException("Failed to process the stored procedure parameters to create the SummaryRecord.", e);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildSummaryRecord", summaryRecord);
        }
        return summaryRecord;
    }

    /**
     * Return the first result set from the Statement if one exists, or null if there is no result set.
     * @param css
     * @param paramColumnDescVector
     * @return
     * @throws SQLException if any JDBC System failure occurs or Stored Procedure Application error is raised
     */
    protected ResultSet getResultSet(CallableStatementSupport css, Vector paramColumnDescVector) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getResultSet", new Object[]{css, paramColumnDescVector});
        }

        StoredProcedureDAOHelper spDAOHelper = getStoredProcedureDAOHelper();
        ResultSet rs = null;

        // Look for the first Cursor SQL Type sp parameters
        try {
            for (int i = 0; i < paramColumnDescVector.size(); i++) {
                ColumnDesc c = (ColumnDesc) paramColumnDescVector.elementAt(i);
                int spColumnIndex = i + 1;
                if (spDAOHelper.isCursorSQLType(c)) {
                    // Get the cursor OUT param as a ResultSet
                    rs = (ResultSet) css.getObject(c.colNumber);
                    break;
                }
            }
        } catch (SQLException se) {
            throw se;
        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AppException("Failed to retrieve the Result Set.", e);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getResultSet", rs);
        }
        return rs;
    }

    /**
     * Create a string used to call the Stored Procedure.
     */
    protected String getStoredProcedureCallSQL(Vector inputColumnDescVector) {
        return "{" + (getIsOracleFunction() ? "? = " : "") + "call " + getStoredProcedureName().getFullyQualifiedSPName() + " " +
            generateQuestionMarks(inputColumnDescVector) + "}";
    }

    /**
     * Create a string with the correct format and number of question marks for executing a stored procedure.
     */
    protected String generateQuestionMarks(Vector inputColumnVector) {
        String questionMarksString = new String();
        ColumnDesc col;
        int startIdx = 0;

        if (inputColumnVector.size() > 0) {
            col = (ColumnDesc) inputColumnVector.elementAt(0);
            if (col.columnType == ColumnDesc.ColumnType.RETURNVALUE) {
                startIdx = 1;
            }
        }
        for (int i = startIdx; i < inputColumnVector.size(); i++) {

            if (i == startIdx) {
                questionMarksString += "(";
            }

            col = (ColumnDesc) inputColumnVector.elementAt(i);
            String parameter = "?";
            if (col.dataType == ColumnDesc.OracleDataType.OPAQUE) {
                parameter = "XMLTYPE(?)";
            }
            questionMarksString += col.columnName + StoredProcedureDAOHelper.ASSIGNMENT_SYMBOL + parameter;

            if (i != inputColumnVector.size() - 1) {
                questionMarksString += ",";
            } else {
                questionMarksString += ")";
            }
        }

        return (questionMarksString);
    }


    /**
     * Get parameters that will be used in SQL.
     * If the parameter has a default value and it doesn't have corresponding field in inputRecord, skip it.
     *
     * @param inputRecord
     * @return
     */
    protected Vector getRequiredParameterVector(Record inputRecord) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRequiredParameterVector", new Object[]{inputRecord});
        }

        Vector paramColumnDescVector = getStoredProcedureDAOHelper().
                getParameterColumnDescVector(getSPDaoKeyName(), getStoredProcedureName(), getDataRecordMapping());

        DataRecordMapping dataRecordMapping = getDataRecordMapping();
        Vector requiredParameterVector = new Vector();
        int sequenceNumber = 1;
        for (int i = 0; i < paramColumnDescVector.size(); i++) {

            ColumnDesc columnDesc = (ColumnDesc) paramColumnDescVector.elementAt(i);
            String fieldName = columnDesc.javaColumnName;

            // get fieldName from mapping
            if (dataRecordMapping != null &&
                    dataRecordMapping.containsMappingForDataField(columnDesc.javaColumnName)) {
                DataRecordFieldMapping fieldMapping = dataRecordMapping.getMappingForDataField(columnDesc.javaColumnName);
                fieldName = fieldMapping.getRecordFieldName();
            }
            //if the parameter has a default value and it doesn't have corresponding field in inputRecord
            //we don't generate the "?" in SQL. Oracle will use default value automatically.
            if (!(columnDesc.columnType == ColumnDesc.ColumnType.IN
                    && !inputRecord.hasField(fieldName) && columnDesc.hasDefaultValue())) {
                ColumnDesc columnDescCopy = columnDesc.getCopy();
                columnDescCopy.colNumber = sequenceNumber++;
                requiredParameterVector.add(columnDescCopy);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRequiredParameterVector", requiredParameterVector);
        }
        return requiredParameterVector;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public StoredProcedureDAO() {
        super();
    }

    public void verifyConfig() {
        if (getAppDataSource() == null)
            throw new ConfigurationException("The required property 'appDataSource' is missing.");
        if (getStoredProcedureDAOHelper() == null)
            throw new ConfigurationException("The required property 'storedProcedureDAOHelper' is missing.");
        if (getErrorHandlerController() == null)
                throw new ConfigurationException("The required property 'errorHandlerController' is missing.");
        if (getDbMechanic() == null)
                throw new ConfigurationException("The required property 'dbMechanic' is missing.");
    }

    public StoredProcedureName getStoredProcedureName() {
        return m_storedProcedureName;
    }

    public void setStoredProcedureName(StoredProcedureName storedProcedureName) {
        m_storedProcedureName = storedProcedureName;
    }

    public void setStoredProcedureName(String spName, String fullyQualifiedStoredProcedureName) {
        m_storedProcedureName = new StoredProcedureName(spName, fullyQualifiedStoredProcedureName);
    }

    protected boolean hasDataRecordMappings() {
        return m_dataRecordMapping != null && m_dataRecordMapping.size() > 0;
    }

    public DataRecordMapping getDataRecordMapping() {
        return m_dataRecordMapping;
    }

    public void setDataRecordMapping(DataRecordMapping dataRecordMapping) {
        if (dataRecordMapping != null) {
            m_dataRecordMapping = dataRecordMapping;
        }
    }

    protected String getBeanName() {
        return BEAN_NAME;
    }

    protected String getSPDaoKeyName() {
        if (m_spDaoKeyName == null) {
            m_spDaoKeyName = getSPDaoKeyName(getBeanName(), m_storedProcedureName.toString(), m_dataRecordMapping);
        }
        return m_spDaoKeyName;
    }

    public StoredProcedureDAOHelper getStoredProcedureDAOHelper() {
        return m_storedProcedureDAOHelper;
    }

    public void setStoredProcedureDAOHelper(StoredProcedureDAOHelper storedProcedureDAOHelper) {
        m_storedProcedureDAOHelper = storedProcedureDAOHelper;
    }

    protected int getExecuteBatchSize() {
        return m_executeBatchSize;
    }

    public void setExecuteBatchSize(int executeBatchSize) {
        m_executeBatchSize = executeBatchSize;
    }

    public synchronized Connection getAppConnection() throws SQLException {
        return getAppDataSource().getConnection();
    }

    public DataSource getAppDataSource() {
        return m_appDataSource;
    }

    public void setAppDataSource(DataSource appDataSource) {
        m_appDataSource = appDataSource;
    }

    public boolean getIsOracleFunction() {
        return m_isOracleFunction;
    }

    public void setIsOracleFunction(boolean isOracleFunction) {
        m_isOracleFunction = isOracleFunction;
    }


    protected synchronized static StoredProcedureDAO getStoredProcedureDAO(String spName, String beanName,
                                                                           DataRecordMapping dataRecordMapping) {

        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(StoredProcedureDAO.class.getName(), "getStoredProcedureDAO", new Object[]{spName, dataRecordMapping});
        }


        StoredProcedureDAO spDAO = null;
        String fullyQualifiedSPName = getFullyQualifiedSPName(spName);
        String spDaoKeyName = getSPDaoKeyName(beanName, fullyQualifiedSPName, dataRecordMapping);
        if (c_spDAOCache.containsKey(spDaoKeyName)) {
            spDAO = (StoredProcedureDAO) c_spDAOCache.get(spDaoKeyName);
            c_l.logp(Level.FINE, StoredProcedureDAO.class.getName(), "getStoredProcedureDAO", "Found cached " + beanName + " for '" + fullyQualifiedSPName + "'.");
        } else {
            spDAO = (StoredProcedureDAO) ApplicationContext.getInstance().getBean(beanName);
            spDAO.setStoredProcedureName(spName, fullyQualifiedSPName);
            spDAO.setDataRecordMapping(dataRecordMapping);
            c_spDAOCache.put(spDaoKeyName, spDAO);
            c_l.logp(Level.FINE, StoredProcedureDAO.class.getName(), "getStoredProcedureDAO", "Created a new " + beanName + " for '" + spDaoKeyName + "'.");
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(StoredProcedureDAO.class.getName(), "getStoredProcedureDAO", spDAO);
        }
        return spDAO;
    }

    private static String getFullyQualifiedSPName(String spName) {
        return getDefaultSchemaName() + "." + spName;
    }

    private static String getDefaultSchemaName() {
        c_l.entering(StoredProcedureDAO.class.getName(), "getDefaultSchemaName");
        String defaultSchemaName = null;

        if (UserSessionManager.isConfigured()) {
            UserSessionManager userSessionManager = UserSessionManager.getInstance();
            try {
                UserSession userSession = userSessionManager.getUserSession();
                if (userSession.has("defaultSchemaName")) {
                    defaultSchemaName = (String) userSession.get("defaultSchemaName");
                    c_l.logp(Level.FINE, StoredProcedureDAO.class.getName(), "getDefaultSchemaName", "Found the defaultSchemaName '" + defaultSchemaName + "' in the UserSession.");
                }
                else {
                    defaultSchemaName = getStoredProcedureDAOHelperRef().executeGetSchemaOwner();
                    userSession.set("defaultSchemaName", defaultSchemaName);
                }
            } catch (Exception e) {
                // The User Session is not setup for this request.
            }
        }

        if (defaultSchemaName == null) {
            defaultSchemaName = getStoredProcedureDAOHelperRef().executeGetSchemaOwner();
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(StoredProcedureDAO.class.getName(), "getDefaultSchemaName", defaultSchemaName);
        }
        return defaultSchemaName;
    }

    private synchronized static StoredProcedureDAOHelper getStoredProcedureDAOHelperRef() {
        if (c_storedProcedureDAOHelper == null) {
            StoredProcedureDAO spDao = (StoredProcedureDAO) ApplicationContext.getInstance().getBean(BEAN_NAME);
             c_storedProcedureDAOHelper = spDao.getStoredProcedureDAOHelper();
        }
        return c_storedProcedureDAOHelper;
    }

    protected static String getSPDaoKeyName(String beanName, String fullyQualifiedSPName, DataRecordMapping dataRecordMapping) {
        StringBuffer buf = new StringBuffer(beanName);
        buf.append(fullyQualifiedSPName.trim().toUpperCase());
        if (dataRecordMapping != null) buf.append(":").append(dataRecordMapping.hashCode());
        return buf.toString();
    }

    public ErrorHandlerController getErrorHandlerController() {
        return m_errorHandlerController;
    }

    public void setErrorHandlerController(ErrorHandlerController errorHandlerController) {
        m_errorHandlerController = errorHandlerController;
    }

    public float getWarningTime() {
        return m_warningTime;
    }

    public long getWarningTimeInMillis() {
        return (long) (m_warningTime * 1000);
    }

    public void setWarningTime(float warningTime) {
        m_warningTime = warningTime;
    }

    public DBMechanic getDbMechanic() {
        return m_dbMechanic;
    }

    public void setDbMechanic(DBMechanic dbMechanic) {
        m_dbMechanic = dbMechanic;
    }

    public static Logger getStpDaoLogger() {
        return c_stpDaoLogger;
    }

    public static void setStpDaoLogger( Logger stpDaoLogger) {
        c_stpDaoLogger = stpDaoLogger;
    }

    private ErrorHandlerController m_errorHandlerController;
    private StoredProcedureName m_storedProcedureName;
    private String m_spDaoKeyName;
    private DataRecordMapping m_dataRecordMapping = new DataRecordMapping();
    private RecordBeanMapper m_recordBeanMapper = new RecordBeanMapper();
    private DataSource m_appDataSource;
    private StoredProcedureDAOHelper m_storedProcedureDAOHelper;
    private int m_executeBatchSize = DEFAULT_EXECUTE_BATCH_SIZE;
    private boolean m_isOracleFunction = false;
    private float m_warningTime;
    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(StoredProcedureDAO.class);
    private static Logger c_stpDaoLogger;

    private static RecordLoadProcessor c_defaultLoadProcessor = new DefaultRecordLoadProcessor();
    private static Map c_spDAOCache = new Hashtable();
    private static StoredProcedureDAOHelper c_storedProcedureDAOHelper;

    private DBMechanic m_dbMechanic;

}
