package dti.pm.riskmgr.insuredmgr.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.insuredmgr.InsuredTrackingFields;

/**
 * <p>(C) 2015 Delphi Technology, inc. (dti)</p>
 * Date:   April 08, 2015
 *
 * @author wdang
 */
/*
 *
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * 04/08/2015    wdang      157211 - Initial version.
 * 11/19/2015    eyin       167171 - Modified getInitialEntitlementValues(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class InsuredTrackingEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    @Override
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record});

        String closingTransLogId = InsuredTrackingFields.getClosingTransLogId(record);
        if (StringUtils.isBlank(closingTransLogId) ||
            !StringUtils.isSame(getPolicyHeader().getLastTransactionId(), closingTransLogId)) {
            record.setDisplayIndicator(YesNoFlag.Y);
        }
        else {
            record.setDisplayIndicator(YesNoFlag.N);
        }

        String recordModeCode = InsuredTrackingFields.getRecordModeCode(record);
        YesNoFlag isDeleteVisible = null;
        if (StringUtils.isSame(recordModeCode, RecordMode.TEMP.getName())) {
            isDeleteVisible = YesNoFlag.Y;
        }
        else {
            isDeleteVisible = YesNoFlag.N;
        }
        record.setFieldValue(InsuredTrackingFields.IS_DELETE_VISIBLE, isDeleteVisible);
        record.setFieldValue(InsuredTrackingFields.IS_INSURED_TYPE_EDITABLE , YesNoFlag.N);

        l.exiting(getClass().getName(), "postProcessRecord, isDeleteVisible=" + isDeleteVisible);
        return super.postProcessRecord(record, rowIsOnCurrentPage);
    }

    @Override
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            List<String> pageEntitlementFields = new ArrayList<String>();
            pageEntitlementFields.add(InsuredTrackingFields.IS_DELETE_VISIBLE);
            pageEntitlementFields.add(InsuredTrackingFields.IS_INSURED_TYPE_EDITABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        YesNoFlag isPageEditable = YesNoFlag.Y;
        try {
            if (!RecordMode.TEMP.equals(getPolicyHeader().getRecordMode())) {
                isPageEditable = YesNoFlag.N;
            }
            else if (getPolicyHeader().getRiskHeader().getBaseRiskStatusCode().isCancelled()
                && !getPolicyHeader().getLastTransactionInfo().getTransactionCode().isReissue()) {
                isPageEditable = YesNoFlag.N;
            }
            else if (getPolicyHeader().getRiskHeader().getRiskStatusCode().isCancelled()) {
                isPageEditable = YesNoFlag.N;
            }
            else if (DateUtils.daysDiff(getPolicyHeader().getRiskHeader().getRiskEffectiveFromDate(),
                getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate()) < 0
                || DateUtils.daysDiff(getPolicyHeader().getRiskHeader().getRiskEffectiveToDate(),
                getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate()) >= 0) {
                isPageEditable = YesNoFlag.N;
            }
            recordSet.getSummaryRecord().setFieldValue(InsuredTrackingFields.IS_PAGE_EDITABLE, isPageEditable);
        }
        catch (ParseException e) {
            l.severe("parsing error: " + getInputRecord());
            AppException ae = new AppException(e.toString());
            throw ae;
        }
        // hide delete button if the whole page is read-only. 
        if (!isPageEditable.booleanValue()) {
            recordSet.setFieldValueOnAll(InsuredTrackingFields.IS_DELETE_VISIBLE, YesNoFlag.N);
            recordSet.setFieldValueOnAll(InsuredTrackingFields.IS_INSURED_TYPE_EDITABLE, YesNoFlag.N);
        }
        l.exiting(getClass().getName(), "postProcessRecordSet,isPageEditable=" + isPageEditable);
    }
    
    /**
     * Return a Record of initial entitlement values.
     */
    public synchronized static Record getInitialEntitlementValues() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            InsuredTrackingFields.setRecordModeCode(c_initialEntitlementValues, RecordMode.TEMP.getName());
            c_initialEntitlementValues.setFieldValue(InsuredTrackingFields.IS_DELETE_VISIBLE, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue(InsuredTrackingFields.IS_INSURED_TYPE_EDITABLE, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }
    
    public InsuredTrackingEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord){
        m_policyHeader = policyHeader;
        m_inputRecord = inputRecord;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private static Record c_initialEntitlementValues;
    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;

}
