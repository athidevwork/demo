package dti.pm.policymgr.taxmgr.impl;

/**
 * This class extends the default record load processor to enforce entitlements for maintain tax web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2014 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 21, 2014
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/21/2014       wdang       158112 - Initial Version
 * 12/04/2014       wdang       159491 - Replace BaseRiskStatus with RiskStatus.
 * ---------------------------------------------------
 */
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.taxmgr.TaxFields;

public class MaintainTaxEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        setRiskFields(record);
        record.setFieldValue(TaxFields.IS_DELETE_VISIBLE, isDeleteVisible(record));
        record.setFieldValue(TaxFields.IS_FORM_FIELDS_EDITABLE, isFormFieldsEditable(record));
        
        if (StringUtils.isBlank(TaxFields.getClosingTransLogId(record))) {
            record.setDisplayIndicator(YesNoFlag.Y);
        }
        else {
            record.setDisplayIndicator(YesNoFlag.N);
        }
        return true;
    }
    
    public void postProcessRecordSet(RecordSet recordSet) {
        Record sumRecord = recordSet.getSummaryRecord();
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            List<String> pageEntitlementFields = new ArrayList<String>();
            pageEntitlementFields.add(TaxFields.IS_DELETE_VISIBLE);
            pageEntitlementFields.add(TaxFields.IS_FORM_FIELDS_EDITABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if (screenModeCode.isViewPolicy() 
                || screenModeCode.isViewEndquote()
                || screenModeCode.isCancelWIP()
                || screenModeCode.isResinstateWIP()) {
            recordSet.setFieldValueOnAll(TaxFields.IS_DELETE_VISIBLE, YesNoFlag.N);
            recordSet.setFieldValueOnAll(TaxFields.IS_FORM_FIELDS_EDITABLE, YesNoFlag.N);
            sumRecord.setFieldValue(TaxFields.IS_SAVE_VISIBLE, YesNoFlag.N);
        } 
        else {
            sumRecord.setFieldValue(TaxFields.IS_SAVE_VISIBLE, YesNoFlag.Y);
        }
    }
    
    public static void getInitialValuesForAddTax(Record record){
        record.setFieldValue(TaxFields.IS_DELETE_VISIBLE, YesNoFlag.Y);
        record.setFieldValue(TaxFields.IS_FORM_FIELDS_EDITABLE, YesNoFlag.Y);
    }

    /**
     * Return Delete button is shown/hidden for each record
     *
     * @return
     */
    private YesNoFlag isDeleteVisible(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isDeleteVisible");
        YesNoFlag isVisible = YesNoFlag.N;

        if (!RecordMode.OFFICIAL.getName().equals(TaxFields.getRecordModeCode(record))) {
            isVisible = YesNoFlag.Y;
        } 
        else {
            isVisible = YesNoFlag.N;
        }

        l.exiting(getClass().getName(), "isDeleteVisible", isVisible);
        return isVisible;
    }

    /**
     * Return form fields are editable/readonly for each record
     * @return
     */
    private YesNoFlag isFormFieldsEditable(Record record) {
        Logger l = LogUtils.enterLog(getClass(), "isFormFieldsEditable");
        YesNoFlag isEditable = YesNoFlag.N;

        try {
            if (!RecordMode.OFFICIAL.getName().equals(TaxFields.getRecordModeCode(record))
                    && StringUtils.isNumeric(TaxFields.getOfficialRecordId(record))) {
                if (YesNoFlag.Y.getName().equals(TaxFields.getAfterImageRecordB(record))) {
                    isEditable = YesNoFlag.Y;
                } 
                else {
                    isEditable = YesNoFlag.N;
                }
            } 
            else {
                if (PMStatusCode.PENDING.getName().equals(TaxFields.getRiskStatus(record))) {
                    isEditable = YesNoFlag.Y;
                } 
                else if (PMStatusCode.ACTIVE.getName().equals(TaxFields.getRiskStatus(record))) {
                    if ((getPolicyHeader().getScreenModeCode().isWIP()
                            || getPolicyHeader().getScreenModeCode().isOosWIP() 
                            || getPolicyHeader().getScreenModeCode().isRenewWIP())
                            && DateUtils.daysDiff(TaxFields.getEffectiveFromDate(record),
                                                  getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate()) >= 0
                            && DateUtils.daysDiff(TaxFields.getEffectiveToDate(record),
                                                  getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate()) <= 0) {
                        isEditable = YesNoFlag.Y;
                    } 
                    else {
                        isEditable = YesNoFlag.N;
                    }
                } 
                else {
                    isEditable = YesNoFlag.N;
                }
            }
        } catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed in parsing dates.", e);
            l.throwing(getClass().getName(), "isFormFieldsEditable", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isFormFieldsEditable", isEditable);
        return isEditable;
    }
    
    private void setRiskFields(Record record){
        for (int i = 0; i < getRiskRecordSet().getSize(); i++) {
            Record riskRecord = getRiskRecordSet().getRecord(i);
            String riskId = TaxFields.getRiskBaseRecordId(riskRecord);
            if (StringUtils.isSame(riskId, TaxFields.getRiskId(record))) {
                TaxFields.setRiskStatus(record, TaxFields.getRiskStatus(riskRecord));
                break;
            }
        }
    }
    
    
    public MaintainTaxEntitlementRecordLoadProcessor(PolicyHeader policyHeader, RecordSet riskRecordSet){
        m_policyHeader = policyHeader;
        m_riskRecordSet = riskRecordSet;
    }
    
    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private RecordSet getRiskRecordSet() {
        return m_riskRecordSet;
    }
    
    private RecordSet m_riskRecordSet;
    private PolicyHeader m_policyHeader;
}
