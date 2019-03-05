package dti.pm.core.data;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.riskmgr.RiskFields;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coverageclassmgr.CoverageClassFields;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the RecordLoadProcessor to provide row accessibility feature for risk.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/06/2007         MLM       Refactored to use addFieldNameCollection instead of setFieldNameCollection
 * 01/18/2007         fcb       postProcessRecordSet: logic for cancelwip removed.
 * 06/25/2010         syang     Issue 109172 - Modified RowAccessorRecordLoadProcessor, the m_isEndQuote
 *                              depend on Endorsement Quote and Renewal Quote.
 * 10/21/2013         fcb       145725 - some optimization in the overall flow.
 * 07/21/2014         jyang     Roll back 99173's change.
 * ---------------------------------------------------
 */
public class RowAccessorRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Keep track of Temp Records to enable editing feature.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        //A row with a status of CANCEL cannot be modified
        PMStatusCode status = null;
        if (CoverageClassFields.hasCoverageClassStatus(record)) {
            status = CoverageClassFields.getCoverageClassStatus(record);
        }
        else if (CoverageFields.hasCoverageStatus(record)) {
            status = CoverageFields.getCoverageStatus(record);
        }
        else if (RiskFields.hasRiskStatus(record)) {
            status = RiskFields.getRiskStatus(record);
        }

        if (m_isTermLastTransactionInWIP) {
            //WIP Mode View - Show only wip rows + untouched official rows
            String Pk = record.getStringValue(m_PrimaryKey_FieldName);
            if (m_isViewModeWIP) {
                String closingTransLogFk = record.getStringValue("closingTransLogId");
                if (!StringUtils.isBlank(closingTransLogFk)) {
                    if (closingTransLogFk.equalsIgnoreCase(m_transactionLogId)) {
                        m_recordsToHide.put(Pk, record);
                    }
                }
            }

            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            if (recordModeCode.isOfficial()) {
                String officialRecordFk = record.getStringValue("officialRecordId");
                m_officialRecords.put(officialRecordFk, record);
            }
            else {
                m_wipRecords.put(Pk, record);
            }

            if (m_isViewModeWIP) {
                if (status != null && status.isCancelled()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
                else {
                    // In WIP mode, all records that is not effected by the in-progress transaction are editiable.
                    record.setEditIndicator(YesNoFlag.Y);
                }
            }
            else {
                // In OFFICIAL mode, no records are editable.
                record.setEditIndicator(YesNoFlag.N);
            }

            // For both the official records and the in-progress transaction affected
            // rows, determine the edit ability by effective date of the transaction.
            try {
                if (record.getEditIndicatorBooleanValue()) {
                    //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    Date effFrom = record.getDateValue(m_effectiveFromDate_FieldName);
                    Date effTo = record.getDateValue(m_effectiveToDate_FieldName);

                    if (!((m_transactionEffectiveFromDate.equals(effFrom) || m_transactionEffectiveFromDate.after(effFrom)) &&
                        m_transactionEffectiveFromDate.before(effTo))) {
                        record.setEditIndicator(YesNoFlag.N);
                        // Dont allow to edit the piece whose effective dates fall
                        // before the transaction eff. date.
                    }
                }
            }
            catch (Exception e) {
                AppException ae = new AppException(AppException.UNEXPECTED_ERROR, "Cannot Parse the date passed");
                l.logp(Level.WARNING, getClass().getName(), "postProcessDataRow", "Cannot Parse the date passed");
                e.printStackTrace();
            }

            // If OOSWIP and official
            if (m_screenModeCode != null &&
                m_screenModeCode.isOosWIP() &&
                recordModeCode.isOfficial()) {
                record.setEditIndicator(YesNoFlag.N);
            }

        }
        else {
            record.setEditIndicator(YesNoFlag.N);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", Boolean.TRUE);
        }

        return true;
    }

    /**
     * Hide the Official DataRows to where there are associated WIP DataRows.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet");
        Record summeryRecord = recordSet.getSummaryRecord();
        if (m_isTermLastTransactionInWIP) {
            if (m_isViewModeWIP) {
                Iterator iter = m_recordsToHide.keySet().iterator();
                while (iter.hasNext()) {
                    String Id = (String) iter.next();
                    Record record = (Record) m_recordsToHide.get(Id);
                    record.setDisplayIndicator(YesNoFlag.N);

                    // since a new column has been added to the record
                    // update record set field information to stay in sync
                    if (recordSet.getFieldCount() < record.getFieldCount()) {
                        recordSet.addFieldNameCollection(record.getFieldNames());
                    }
                }
            }
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Overloaded constructor for initializing the member variables with provided parameters.
     *
     * @param PrimaryKey_FieldName
     * @param effectiveFromDate_FieldName
     * @param effectiveToDate_FieldName
     * @param policyHeader
     * @param screenModeCode
     */
    public RowAccessorRecordLoadProcessor(String PrimaryKey_FieldName,
                                          String effectiveFromDate_FieldName,
                                          String effectiveToDate_FieldName,
                                          PolicyHeader policyHeader,
                                          ScreenModeCode screenModeCode) {
        m_PrimaryKey_FieldName = PrimaryKey_FieldName;
        m_effectiveFromDate_FieldName = effectiveFromDate_FieldName;
        m_effectiveToDate_FieldName = effectiveToDate_FieldName;
        Transaction trans = policyHeader.getLastTransactionInfo();
        PolicyViewMode policyViewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        m_isTermLastTransactionInWIP = trans.getTransactionStatusCode().isInProgress();
        m_isViewModeWIP = !policyViewMode.equals(PolicyViewMode.OFFICIAL);
        m_transactionLogId = trans.getTransactionLogId();
        m_transactionEffectiveFromDate = DateUtils.parseDateTime(trans.getTransEffectiveFromDate());
        m_screenModeCode = screenModeCode;
    }

    /**
     * Overloaded constructor for initializing the member variables with provided parameters.
     */
    public RowAccessorRecordLoadProcessor(String PrimaryKey_FieldName,
                                          String effectiveFromDate_FieldName,
                                          String effectiveToDate_FieldName,
                                          boolean isTermLastTransactionInWIP,
                                          boolean isViewModeWIP,
                                          String transactionLogId,
                                          String transactionEffectiveFromDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "RowAccessorRecordLoadProcessor", new Object[]{PrimaryKey_FieldName, effectiveFromDate_FieldName, effectiveToDate_FieldName, String.valueOf(isTermLastTransactionInWIP), String.valueOf(isViewModeWIP), transactionEffectiveFromDate});
        }

        m_PrimaryKey_FieldName = PrimaryKey_FieldName;
        m_effectiveFromDate_FieldName = effectiveFromDate_FieldName;
        m_effectiveToDate_FieldName = effectiveToDate_FieldName;
        m_isTermLastTransactionInWIP = isTermLastTransactionInWIP;
        m_isViewModeWIP = isViewModeWIP;
        m_transactionLogId = transactionLogId;
        m_transactionEffectiveFromDate = DateUtils.parseDateTime(transactionEffectiveFromDate);

        l.exiting(getClass().getName(), "RowAccessorRecordLoadProcessor");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public RowAccessorRecordLoadProcessor() {

    }

    public String PrimaryKey_FieldName() {
        return m_PrimaryKey_FieldName;
    }

    private Map m_recordsToHide = new HashMap();
    private Map m_officialRecords = new HashMap();
    private Map m_wipRecords = new HashMap();

    private boolean m_isViewModeWIP;
    private boolean m_isTermLastTransactionInWIP;

    private String m_PrimaryKey_FieldName;
    private String m_effectiveFromDate_FieldName;
    private String m_effectiveToDate_FieldName;
    private Date m_transactionEffectiveFromDate;
    private String m_transactionLogId;
    private ScreenModeCode m_screenModeCode;
}
