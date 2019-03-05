package dti.pm.riskmgr.empphysmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.riskmgr.empphysmgr.EmployedPhysicianFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.transaction.Transaction;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class extends the default record load processor to enforce entitlements for the Rating Log web page. This
 * class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/24/2010       syang       Issue 108651 - Added isRenewBAvailable() to handle Renew indicator.
 * 10/26/2010       syang       Issue 113082 - System should hide the Delete button in Cancel and Reinstate WIP.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForEmployedPhysician(), Add synchronized lock on this function.
 * ---------------------------------------------------
 */
public class EmployedPhysicianEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        //initially set all pageEntitlement false
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);

        EmployedPhysicianFields.setIsInUpdateMode(getInputRecord(),YesNoFlag.getInstance(!(isRecordSetReadOnly())));
        YesNoFlag isInUpdateMode = EmployedPhysicianFields.getIsInUpdateMode(getInputRecord());

        Transaction lastTransaction = getPolicyHeader().getLastTransactionInfo();
        PMStatusCode currStatus = EmployedPhysicianFields.getCurrStatusCode(record);
        boolean isAfterImageB = EmployedPhysicianFields.getAfterImageRecordB(record).booleanValue();
        Date tranEff = DateUtils.parseDate(lastTransaction.getTransEffectiveFromDate());
        Date empExp = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveToDate(record));
        Date empEff = DateUtils.parseDate(EmployedPhysicianFields.getEffectiveFromDate(record));

        //row level indicators
        EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        record.setEditIndicator(YesNoFlag.N);
        // Official No Delete and Vice Versa
        EntitlementFields.setIsRowEligibleForDelete(record, (!(recordMode.isOfficial())));

        PMStatusCode empPhysicianStatus = EmployedPhysicianFields.getCurrStatusCode(record);

        if (empPhysicianStatus.isPending()) {
            // After Image Editable and ViceVersa
            record.setEditIndicator(YesNoFlag.getInstance(isAfterImageB));
        }
        else if (empPhysicianStatus.isActive()) {
            // EmpEff is <= tranEff and EmpExp > tranEff
            if (   isInUpdateMode.booleanValue()
                && (empEff.before(tranEff) || empEff.equals(tranEff))
                && ( empExp.after(tranEff) ))   {
                    record.setEditIndicator(YesNoFlag.Y);
                }
            else {
                record.setEditIndicator(YesNoFlag.N);
            }
        }
        else {
          record.setEditIndicator(YesNoFlag.N);
        }

        // Hide Delete button.
        if (screenMode.isViewPolicy() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            EntitlementFields.setIsRowEligibleForDelete(record, YesNoFlag.N);
        }
        // Issue 108651 - handle Renew indicator.
        record.setFieldValue("isRenewBAvailable", isRenewBAvailable(record));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", String.valueOf(true));
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

        Record summeryRecord = recordSet.getSummaryRecord();

        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        PMStatusCode statusCode = RiskFields.getRiskStatus(getInputRecord());

        //initially set all pageEntitlement false
        summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        summeryRecord.setFieldValue("isTransactionDateEditable", YesNoFlag.N);
        summeryRecord.setFieldValue("isCalculateAvailable", YesNoFlag.Y);

        if (screenMode.isViewPolicy() || screenMode.isViewEndquote()) {
            summeryRecord.setFieldValue("isTransactionDateEditable", YesNoFlag.Y);
        }
        else if (screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            //do nothing
        }
        else if (screenMode.isManualEntry()) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
        }
        else {
            if (statusCode.isActive() || statusCode.isPending()) {
                summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.Y);
            }
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
             summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        if (recordSet.getSize() == 0) {
            List fieldNames = new ArrayList();

            fieldNames.add(EntitlementFields.READ_ONLY);
            fieldNames.add(EntitlementFields.IS_ROW_ELIGIBLE_FOR_DELETE);
            fieldNames.add(EmployedPhysicianFields.FTE_VALUE);
            recordSet.addFieldNameCollection(fieldNames);

            if (isRecordSetReadOnly())
                summeryRecord.setFieldValue("isCalculateAvailable", YesNoFlag.N);
        }
        // Fix issue 102521. If the page is read only, system should disable the Add and Save buttons.
        if (isRecordSetReadOnly()) {
            summeryRecord.setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly());
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }

    }


    /**
     * Return a Record of initial entitlement values for a new Employed Physician.
     */
    public synchronized static Record getInitialEntitlementValuesForEmployedPhysician
        () {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();
            EntitlementFields.setIsRowEligibleForDelete(c_initialEntitlementValues, YesNoFlag.Y);
            c_initialEntitlementValues.setFieldValue("isRenewBAvailable", YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();

        // All available fields are readonly in following screen mode:
        // VIEW_POLICY, VIEW_ENDQUOTE, CANCELWIP, REINSTATEWIP
        // For OOSWIP or RENEWWIP, this page should be read only if the current time is not the initial term.
        if (screenModeCode.isViewPolicy() || screenModeCode.isViewEndquote() ||
            screenModeCode.isResinstateWIP() || screenModeCode.isCancelWIP() ||
           (screenModeCode.isOosWIP() || screenModeCode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }
        // Fix issue 102521. We'll make the FTE Name screen (in ePolicy only) read-only
        // if the trasnaction effective date is prior to the EF Risk effective date.
        String transEffectiveFromDate = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        RiskHeader riskHeader = getPolicyHeader().getRiskHeader();
        if(riskHeader != null){
            String riskEffectiveFromDate = riskHeader.getEarliestContigEffectiveDate();
            Date riskEffFromDate = DateUtils.parseDate(riskEffectiveFromDate);
            Date transEffFromDate = DateUtils.parseDate(transEffectiveFromDate);
            if(transEffFromDate.before(riskEffFromDate)){
               isReadOnly = true;
            }
        }
        return isReadOnly;
    }

    /**
     * Check if the renew indicator is available.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isRenewBAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;
        Date effectiveToDate = record.getDateValue("effectiveToDate");
        Date termExpirationDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        if (effectiveToDate.before(termExpirationDate)) {
            isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public EmployedPhysicianEntitlementRecordLoadProcessor() {
    }

    public EmployedPhysicianEntitlementRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setPolicyHeader(policyHeader);
        setInputRecord(inputRecord);
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }


    private Record getInputRecord() {
        return m_inputRecord;
    }

    private void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;

    private static Record c_initialEntitlementValues;
}
