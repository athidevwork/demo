package dti.pm.policymgr.quickquotemgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.quickquotemgr.QuickQuoteManager;
import dti.pm.policymgr.quickquotemgr.dao.QuickQuoteDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import dti.pm.busobjs.SysParmIds;
import dti.oasis.util.SysParmProvider;


/**
 * Business component for quick quote.
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
 * 06/22/2016       SSHENG      ISSUE 164927 - 1.  Hidden import button when has imported
 *                                                 record into quick quote.
 * 12/13/2016       SJIN        ISSUE 181777 - 1.  modify the method named "importQuote" and add codes to verify
 *                                                 whether  the uploaded file is really a csv file or not.
 * 05/09/2017       ssheng      185360 - Add system parameter 'PM_NB_QUICK_QUOTE' to indicate
 *                                       if Populate CIS and Review Dup will display.
 * 03/08/2018       wrong       191786 - Modify importQuote() to change validate csv file logic.
 * ---------------------------------------------------
 */
public class QuickQuoteManagerImpl implements QuickQuoteManager {
    /**
     * Import quote
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record importQuote(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "importQuote";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "importQuote", new Object[]{policyHeader, inputRecord,});
        }

        inputRecord.setFieldValue("oasisFileId", inputRecord.getStringValue("uploadedOasisFileId"));
        String fullFilePath = getQuickQuoteDAO().getImportFilePath(inputRecord);

        //verify that the file is an CSV file
        Record rc = new Record();
        try {
            File file = new File(fullFilePath);
            //step1: validate the file size
            if (file.length() == 0) {
                rc.setFieldValue("rc",0);
                rc.setFieldValue("rmsg","The upload file is an empty file.");
                return rc;
            }
            //step2: validate the file name is end with '.csv'
            if (file.getName().endsWith(".csv")) {
                //step3: validate if file content is messy code
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[30];
                fis.read(b, 0, b.length);
                String content = new String(b, "UTF-8");
                if (content.matches("[\\d\\w\\s[#?,&%^@/()']]+")) {
                    //if the file is ".csv" type
                    inputRecord.setFieldValue("fullFilePath", fullFilePath);
                    inputRecord.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());
                    inputRecord.setFieldValue("policyTermBaseRecordId", policyHeader.getTermBaseRecordId());
                    inputRecord.setFieldValue("policyType", policyHeader.getPolicyTypeCode());
                    inputRecord.setFieldValue("issueState", policyHeader.getIssueStateCode());
                    inputRecord.setFieldValue("issueCompanyId", policyHeader.getIssueCompanyEntityId());
                    rc = getQuickQuoteDAO().importQuote(inputRecord);
                }
                else {
                    rc.setFieldValue("rc",0);
                    rc.setFieldValue("rmsg","Invalid file type, the upload file type is not a real csv file.");
                }
            }
            else {
                rc.setFieldValue("rc",0);
                rc.setFieldValue("rmsg","Invalid file type, the upload file type should be end with .csv.");
            }
        } catch (FileNotFoundException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Cann't find the file in the path.", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        } catch (IOException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to read file.", e);
            l.throwing(getClass().getName(), methodName, ae);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rc);
        }
        return rc;

    }

    /**
     * Load all import result
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllImportResult(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllImportResult", new Object[]{policyHeader, inputRecord});
        }
        RecordSet rs = null;
        Record rc = null;
        String eventHeaderId = "";
        if (inputRecord.hasStringValue("policyLoadEventHeaderId")) {
            inputRecord.getStringValue("policyLoadEventHeaderId");
        }
        if (StringUtils.isBlank(eventHeaderId)) {
            // Check if there's existing record in policy_load_event_header
            inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
            inputRecord.setFieldValue("termId", policyHeader.getTermBaseRecordId());
            rc = getQuickQuoteDAO().getLoadEventHeader(inputRecord);
            eventHeaderId = rc.getStringValue("policyLoadEventHeaderId");
        }

        inputRecord.setFieldValue("loadEventHeaderId", eventHeaderId);
        inputRecord.setFieldValue("transId", policyHeader.getLastTransactionId());
        rs = getQuickQuoteDAO().loadAllImportResult(inputRecord);
        rs.getSummaryRecord().setFields(rc);

        if (rs.getSize() == 0) {
            rs.getSummaryRecord().setFieldValue("hasFileLoaded", "N");
            rs.getSummaryRecord().setFieldValue("reviewDup", "N");
            rs.getSummaryRecord().setFieldValue("popCIS", "N");
            rs.getSummaryRecord().setFieldValue("noFileLoaded", "Y");
            rs.getSummaryRecord().setFieldValue("hasFileSavedCIS", "N");
        }
        else {
            rs.getSummaryRecord().setFieldValue("hasFileLoaded", "Y");
            rs.getSummaryRecord().setFieldValue("reviewDup", "Y");
            rs.getSummaryRecord().setFieldValue("popCIS", "Y");
            rs.getSummaryRecord().setFieldValue("noFileLoaded", "N");
            rs.getSummaryRecord().setFieldValue("hasFileSavedCIS", "Y");
        }

        YesNoFlag reviewDup = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_NB_QUICK_QUOTE, "Y"));
        if (reviewDup.booleanValue()) {
            rs.getSummaryRecord().setFieldValue("reviewDup", "N");
        } else {
            rs.getSummaryRecord().setFieldValue("popCIS", "N");
        }

        if (rs.getSize() !=0 && rs.getSubSet(new RecordFilter("cisSavedB", YesNoFlag.Y)).getSize() == 0) {
            rs.getSummaryRecord().setFieldValue("hasFileSavedCIS", "Y");
        }
        else {
            rs.getSummaryRecord().setFieldValue("hasFileSavedCIS", "N");
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

        Record rc = getQuickQuoteDAO().undoImportQuote(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "undoImportQuote", rc);
        }
        return rc;
    }

    /**
     * Populate cis
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record populateCis(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "populateCis", new Object[]{policyHeader, inputRecord});
        }

        inputRecord.setFieldValue("transId", policyHeader.getLastTransactionId());
        Record rc = getQuickQuoteDAO().populateCis(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "populateCis", rc);
        }
        return rc;
    }

    /**
     * Get import file path
     *
     * @param inputRecord
     * @return String
     */
    public String getImportFilePath(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getImportFilePath", new Object[]{inputRecord});
        }
        String fullFilePath = getQuickQuoteDAO().getImportFilePath(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getImportFilePath", fullFilePath);
        }
        return fullFilePath;

    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getQuickQuoteDAO() == null)
            throw new ConfigurationException("The required property 'quickQuoteDAO' is missing.");
    }


    public QuickQuoteDAO getQuickQuoteDAO() {
        return m_quickQuoteDAO;
    }

    public void setQuickQuoteDAO(QuickQuoteDAO quickQuoteDAO) {
        m_quickQuoteDAO = quickQuoteDAO;
    }

    private QuickQuoteDAO m_quickQuoteDAO;
}
