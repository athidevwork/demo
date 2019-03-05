package dti.ci.reportmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordMapper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.WebReport;
import dti.ci.reportmgr.CIReportManager;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of CIReportManager Interface.
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   June 16, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CIReportManagerImpl implements CIReportManager {

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
        // In FOP version 0.20.5, in order to show the image in PDF, we must set baseDir since we haven't config it in userConfig.xml.
        report.setFOPBaseDir(inputRecord.getStringValue("baseDir"));
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
        if (inputRecord.hasStringValue(REPORT_CODE)) {
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
