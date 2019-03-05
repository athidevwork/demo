package dti.pm.riskmgr.empphysmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianFields;
import dti.pm.riskmgr.empphysmgr.EmploymentStatusCode;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianManager;
import dti.pm.riskmgr.RiskFields;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.policymgr.PolicyHeader;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigDecimal;


/**
 * This class extends the default record load processor . It will filter out unvalid records and initialize fte value
 * and sum total fte for page
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/07/2010       dzhang      111902 - Modified postProcessRecord: always return true instead of remove invalid record.
 * 10/11/2010       syang       111902 - Modified postProcessRecord: hide the invalid record and set the FTEvalue to null.
 * 02/25/2011       sxm         111017 - 1) Change risk_pk to risk_base_reocrd_fk
 *                                       2) delete "remove official" logic that is no longer needed due to PDS change
 * ---------------------------------------------------
 */
public class EmployedPhysicianRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, new Boolean(rowIsOnCurrentPage)});

        }
        boolean isValid = true;

        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        Date effDate = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveFromDate(record));
        Date expDate = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveToDate(record));

        String officialRecordId = EmployedPhysicianFields.getOfficialRecordId(record);
        String riskOfficialRecordId = EmployedPhysicianFields.getRiskOfficialRecordId(record);
        RecordMode riskRecordMode = null;
        if (record.hasStringValue(EmployedPhysicianFields.RISK_RECORD_MODE_CODE))
            riskRecordMode = EmployedPhysicianFields.getRiskRecordModeCode(record);

        Date lastTransEffDate = DateUtils.parseDate(getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        String transactionLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();

        //discard any records where the effctvieFromDate = effectiveToDate
        if (effDate.equals(expDate)) {
            isValid = false;
        }
        //removes colsed employed physician records
        if (isValid && (screenMode.isOosWIP() || screenMode.isCancelWIP() || screenMode.isResinstateWIP() || screenMode.isManualEntry() || screenMode.isWIP() || screenMode.isRenewWIP() || screenMode.isViewEndquote())) {
            if (record.hasStringValue(EmployedPhysicianFields.CLOSING_TRANS_LOG_ID)
                && EmployedPhysicianFields.getClosingTransLogId(record).equals(transactionLogId)) {
                isValid = false;
            }else if (record.hasStringValue(EmployedPhysicianFields.RISK_CLOSING_TRANS_LOG_ID)
                && EmployedPhysicianFields.getRiskClosingTransLogId(record).equals(transactionLogId)) {
                isValid = false;
            }

        }

        //calculate and set fte value for every record
        if (isValid) {
            Record fteValuesRec = getEmployedPhysicianManager().getChangedValuesForEmployedPhysician(getPolicyHeader(), record, getInputRecord());
            record.setFields(fteValuesRec, true);
        }
        else {
            EmployedPhysicianFields.setFteValue(record, null);
            record.setDisplayIndicator(YesNoFlag.N);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", new Boolean(isValid));
        }
        return true;
    }


    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }

        //calculate and set total fte value
        Record totalFteRec = getEmployedPhysicianManager().calculateTotalFte(getPolicyHeader(), recordSet);
        recordSet.getSummaryRecord().setFields(totalFteRec, true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public EmployedPhysicianRecordLoadProcessor() {
    }

    public EmployedPhysicianRecordLoadProcessor(PolicyHeader
        policyHeader, EmployedPhysicianManager employedPhysicianManager, Record
        inputRecord) {
        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
        EmployedPhysicianFields.setFteTotal(inputRecord, "0");
        m_replacedOfficialRecordsMap = new HashMap();
        m_replacedRiskOfficialRecordsMap = new HashMap();
        m_employedPhysicianManager = employedPhysicianManager;
    }

    private PolicyHeader getPolicyHeader
        () {
        return m_policyHeader;
    }

    private void setPolicyHeader
        (PolicyHeader
            policyHeader) {
        m_policyHeader = policyHeader;
    }

    private Record getInputRecord
        () {
        return m_inputRecord;
    }

    private void setInputRecord
        (Record
            inputRecord) {
        m_inputRecord = inputRecord;
    }


    private EmployedPhysicianManager getEmployedPhysicianManager() {
        return m_employedPhysicianManager;
    }

    private Map m_replacedOfficialRecordsMap;
    private Map m_replacedRiskOfficialRecordsMap;
    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;
    private EmployedPhysicianManager m_employedPhysicianManager;
}
