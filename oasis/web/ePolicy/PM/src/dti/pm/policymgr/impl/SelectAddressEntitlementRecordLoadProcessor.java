package dti.pm.policymgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.core.http.RequestIds;
import dti.pm.policymgr.PolicyFields;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.riskmgr.RiskFields;

import java.security.Policy;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class extends the default record load processor to enforce entitlements for select address web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published to request as request attributes, which then gets intercepted by
 * pageEntitlements oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 25, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/26/2011       dzhang      121130 - Modified postProcessRecordSet: added page entitlement logic for risk level.
 * 08/17/2012       xnie        120683 - Modified postProcessRecordSet: added page entitlement logic for Done button.
 * 09/21/2017       eyin        169483 - Modified postProcessRecordSet() to process isFromExposure indicator.
 * ---------------------------------------------------
 */
public class SelectAddressEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return super.postProcessRecord(record, rowIsOnCurrentPage);
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isSaveAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        // check if there is no record is selected then mark the first record with primary address indicator set to "Y"
        Iterator recIter = recordSet.getRecords();
        boolean hasSelectedAddr = false;
        Record primaryAddrRec = null;
        boolean fromExposureB = false;
        while (recIter.hasNext()) {
            Record r = (Record) recIter.next();
            if(r.hasFieldValue(PolicyFields.FROM_EXPOSURE_B)){
                fromExposureB = PolicyFields.getFromExposureB(r).booleanValue();
            }
            // check if the record is selected address
            if (PolicyFields.getSelectedAddressB(r).booleanValue()) {
                r.setFieldValue(RequestIds.SELECT_IND, new Long(-1));
                hasSelectedAddr = true;
            }
            if (PolicyFields.getPrimaryAddressB(r).booleanValue()) {
                primaryAddrRec = r;
            }
        }

        // no record is selected
        if (!fromExposureB && !hasSelectedAddr && primaryAddrRec != null) {
            primaryAddrRec.setFieldValue(RequestIds.SELECT_IND, new Long(-1));
        }

        Record summaryRecord = recordSet.getSummaryRecord();
        if (getType().equals(PolicyFields.RoleTypeValues.POLICYHOLDER)) {
            if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote()
                || getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()) {
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.N);
            }
            else {
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.Y);
            }

            summaryRecord.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else if (getType().equals(PolicyFields.RoleTypeValues.COIHOLDER)) {
            if (getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()) {
                summaryRecord.setFieldValue("isDoneAvailable", YesNoFlag.N);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.N);
            }
            else {
                summaryRecord.setFieldValue("isDoneAvailable", YesNoFlag.Y);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.Y);
            }

            summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
        }
        else if (getType().equals(PolicyFields.RoleTypeValues.RISK)) {
            Record record = getInputRecord();
            PMStatusCode riskStatus = null;
            if (record.hasStringValue(RiskFields.RISK_STATUS)) {
                riskStatus = RiskFields.getRiskStatus(record);
            }

            if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote()
                || getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()
                || (riskStatus != null && riskStatus.isCancelled())) {
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.N);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.N);
            }
            else {
                summaryRecord.setFieldValue("isSaveAvailable", YesNoFlag.Y);
                summaryRecord.setFieldValue("isSelectAvailable", YesNoFlag.Y);
            }

            summaryRecord.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
    }

    public SelectAddressEntitlementRecordLoadProcessor(ScreenModeCode screenModeCode, String type, Record inputRecord) {
        setScreenModeCode(screenModeCode);
        setType(type);
        setInputRecord(inputRecord);
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private ScreenModeCode m_screenModeCode;
    private String m_type;
    private Record m_inputRecord;
}
