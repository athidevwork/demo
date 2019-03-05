package dti.pm.policymgr.quickquotemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for quick quote.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/08/2016       fcb         issue 177372 - Changed INT to BIGINT, and int to long
 * ---------------------------------------------------
 */
public class QuickQuoteJdbcDAO extends BaseDAO implements QuickQuoteDAO {
    /**
     * Get import file path
     *
     * @param inputRecord
     * @return String
     */
    public String getImportFilePath(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getImportFilePath", new Object[]{inputRecord,});
        }

        String filePath;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Web_Oasis_File.Get_File_Path");
        try {
            filePath = spDao.execute(inputRecord).getSummaryRecord().getStringValue("returnValue");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get import file path", e);
            l.throwing(getClass().getName(), "getImportFilePath", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getImportFilePath", filePath);
        }

        return filePath;
    }

    /**
     * Import file to quote
     *
     * @param inputRecord
     * @return Record
     */
    public Record importQuote(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "importQuote", new Object[]{inputRecord,});
        }
        Record rc;
        Connection conn = null;
        CallableStatement cs = null;
        FileInputStream fin = null;
        try {
            conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            String fileName = inputRecord.getStringValue("fullFilePath");

            // Open the file as a stream
            File file = new File(fileName);
            fin = new FileInputStream(file);

            // Call Stored DB procedure for updating clob column
            cs = conn.prepareCall("begin Pm_Web_Quick_Quote.Import_Quote(?,?,?,?,?,?,?,?,?,?,?); end;");

            // Set parameters
            cs.setLong(1, inputRecord.getLongValue("policyId").longValue());
            cs.setLong(2, inputRecord.getLongValue("transactionLogId").longValue());
            cs.setLong(3, inputRecord.getLongValue("policyTermBaseRecordId").longValue());
            cs.setString(4, inputRecord.getStringValue("policyType"));
            cs.setString(5, inputRecord.getStringValue("issueState"));
            cs.setString(6, inputRecord.getStringValue("issueCompanyId"));
            // use setAsciiStream to set the clob parameter.
            cs.setAsciiStream(7, fin, (int) file.length());
            cs.setLong(8, inputRecord.getLongValue("oasisFileId").longValue());
            cs.setString(9, inputRecord.getStringValue("hasHeader"));

            cs.registerOutParameter(10, java.sql.Types.BIGINT);
            cs.registerOutParameter(11, java.sql.Types.CHAR);
            cs.execute();

            rc = new Record();
            rc.setFieldValue("rc", String.valueOf(cs.getLong(10)));
            rc.setFieldValue("rmsg", cs.getString(11));

            conn.setAutoCommit(autoCommit);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to import quote", e);
            l.throwing(getClass().getName(), "importQuote", ae);
            throw ae;
        }
        catch (FileNotFoundException ex) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to open file", ex);
            l.throwing(getClass().getName(), "importQuote", ae);
            throw ae;
        }
        finally {
            DatabaseUtils.close(cs);
            closeConnection(conn);
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (Exception e) {
                    l.warning("File input stream closed fail.");
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "importQuote", inputRecord);
        }
        return rc;
    }

    /**
     * get load event header
     *
     * @param inputRecord
     * @return Record
     */
    public Record getLoadEventHeader(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLoadEventHeader", new Object[]{inputRecord});
        }

        Record rc;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quick_Quote.Get_Load_Event_Header");
        try {
            rc = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get load event header", e);
            l.throwing(getClass().getName(), "getLoadEventHeader", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLoadEventHeader", rc);
        }
        return rc;
    }

    /**
     * Load all import result
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllImportResult(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllImportResult", new Object[]{inputRecord});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Load_Results");
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load import results", e);
            l.throwing(getClass().getName(), "loadAllImportResult", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllImportResult", rs);
        }

        return rs;
    }

    /**
     * Unload quick quote
     *
     * @param inputRecord
     * @return Record
     */
    public Record undoImportQuote(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "undoImportQuote", new Object[]{inputRecord});
        }

        Record rc;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("loadEventId", "policyLoadEventHeaderId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Unload", mapping);
        try {
            rc = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to unload quick quote", e);
            l.throwing(getClass().getName(), "undoImportQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "undoImportQuote", rc);
        }
        return rc;
    }

    /**
     * Populate cis
     *
     * @param inputRecord
     * @return Record
     */
    public Record populateCis(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "populateCis", new Object[]{inputRecord,});
        }

        Record rc;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("loadEventHeaderId", "policyLoadEventHeaderId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quick_Quote.Populate_CIS", mapping);
        try {
            rc = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to populate cis", e);
            l.throwing(getClass().getName(), "populateCis", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "populateCis", rc);
        }
        return rc;
    }

}
