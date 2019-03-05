package dti.pm.policyreportmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordMapper;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.WebQuery;
import dti.oasis.util.WebQueryInfo;
import dti.oasis.util.WebReport;
import dti.oasis.util.WebReportInfo;
import dti.pm.policyreportmgr.PolicyReportManager;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of PolicyReportManager Interface.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 26, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/25/2009       yhyang      Add getReportCode().
 * 03/02/2011       fcb         122664 & 107021: added generateXMLStream and generateCSVStream.
 * ---------------------------------------------------
 */
public class PolicyReportManagerImpl implements PolicyReportManager {

    /**
     * Generates report as PDF stream
     *
     * @param inputRecord input parameters
     * @param conn        live JDBC Connection
     * @return PDF stream
     */
    public ByteArrayOutputStream generatePDFStream(Record inputRecord, Connection conn) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePDFStream", new Object[]{inputRecord, conn});
        }
        ByteArrayOutputStream bos = null;
        // Get the Report Code.
        String policyReportCode = getReportCode(inputRecord);
        // Get the Map parameters.
        Map parmsMap = new HashMap();
        RecordMapper.getInstance().map(inputRecord, parmsMap);
        // Get pdf stream.
        WebReport report = WebReport.getInstance();
        try {
            bos = report.generatePDFStream(conn, policyReportCode, parmsMap);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get the pdf stream for the policy report.", e);
            l.throwing(getClass().getName(), "generatePDFStream", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generatePDFStream", bos);
        }
        return bos;
    }

    /**
     * Generates a XML stream based on the input data.
     * @param inputRecord
     * @param conn
     * @return
     */
    public String generateXMLStream(Record inputRecord, Connection conn) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateXMLStream", new Object[]{inputRecord, conn});
        }
        String reportData = null;
        // Get the Report Code.
        String policyReportCode = getReportCode(inputRecord);
        // Get the Map parameters.
        Map parmsMap = new HashMap();
        RecordMapper.getInstance().map(inputRecord, parmsMap);
        // Get xml stream.
        WebReport report = WebReport.getInstance();
        try {
            reportData = report.generateXML(conn, policyReportCode, parmsMap);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get the XML stream for the policy report.", e);
            l.throwing(getClass().getName(), "generateXMLStream", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateXMLStream", reportData);
        }
        return reportData;
    }

    /**
     * Generates a XML stream based on the input data.
     * @param inputRecord
     * @param conn
     * @return
     */
    public String generateCSVStream(Record inputRecord, Connection conn) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateCSVStream", new Object[]{inputRecord, conn});
        }
        String reportData = null;
        // Get the Report Code.
        String policyReportCode = getReportCode(inputRecord);
        // Get the Map parameters.
        Map parmsMap = new HashMap();
        RecordMapper.getInstance().map(inputRecord, parmsMap);
        // Get csv stream.
        WebReport report = WebReport.getInstance();
        try {
            reportData = report.generateCSV(conn, policyReportCode, parmsMap);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get the CSV stream for the policy report.", e);
            l.throwing(getClass().getName(), "generateCSVStream", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateCSVStream", reportData);
        }
        return reportData;
    }

    /**
     * Check whether the premium detail is empty for generateing premium worksheet.
     *
     * @param inputRecord input parameters
     * @param conn        live JDBC Connection
     * @return boolean    true is empty, false isn't empty.
     */
    public boolean isReportEmpty(Record inputRecord, Connection conn) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isReportEmpty", new Object[]{inputRecord, conn});
        }

        boolean rtnValue = false;
        // Get the Report Code.
        String policyReportCode = getReportCode(inputRecord);
        // Get the Map parameters.
        Map parmsMap = new HashMap();
        RecordMapper.getInstance().map(inputRecord, parmsMap);

        WebReport report = WebReport.getInstance();
        WebQuery query = WebQuery.getInstance();
        try {
            WebReportInfo reportInfo = report.getReportInfo(conn, policyReportCode);
            int querySize = reportInfo.getQueries().size();
            if (querySize > 1) {
                // The second query is for premium detail.
                // If the premium detail is empty, system determines there is no data to generate worksheet.
                long queryId = ((WebQueryInfo) reportInfo.getQueries().get(1)).getQueryPk();
                DisconnectedResultSet rs = query.getResultSet(conn, queryId, parmsMap);
                if (rs.getRowCount() == 0) {
                    rtnValue = true;
                }
            }
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate whether the report data is empty", e);
            l.throwing(getClass().getName(), "isReportEmpty", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isReportEmpty", Boolean.valueOf(rtnValue));
        }
        return rtnValue;
    }

    /**
     * Get the report code.
     * If the report code is empty, system throws an exception.
     *
     * @param inputRecord
     * @return reportCode
     */
    private String getReportCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getReportCode", new Object[]{inputRecord});
        }
        String policyReportCode = "";
        if(inputRecord.hasStringValue(REPORT_CODE)){
            // Since the map is got from request, the value is an array.
            policyReportCode = inputRecord.getStringValue(REPORT_CODE);
        }

        if (StringUtils.isBlank(policyReportCode)) {
            throw new ValidationException("Report code is empty.");
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getReportCode");
        }
        return policyReportCode;
    }

    private static final String REPORT_CODE = "reportCode";
}
