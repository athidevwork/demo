package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.ProcessStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for risk web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 1, 2007
 *
 * @author sma
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/02/2007       fcb         isPrimaryRiskBEditable() method added.
 *                              isPrimaryRiskBEditable field set based on the above.
 * 11/18.2008       yhyang      isInsuredHistorylAvailable is added.
 * 08/03/2010       syang       103793 - Added isSurchargePointsAvailable() to determine if Surcharge Point is available.
 * 12/17/2010       syang       115238 - Added setEditIndicatorForSlot() to set the editIndicator for slot risk.
 * 01/19/2011       wfu         113566 - Added isCopyNewAvailable() to determine if Copy New is available.
 * 03/18/2011       ryzhao      113559 - Set ERP option available by default.
 * 05/01/2011       fcb         105791 - Added isConvertCoverageAvailable()
 * 07/04/2011       ryzhao      121160 - Modified isOccupantAvailable(), isCoiHolderAvailable(), setEditIndicatorForSlot()
 *                              to change the checking logic if a slot risk is occupied from checking the risk name
 *                              against "VACANT" to checking if the entity id is zero.
 * 08/05/2011       ryzhao      123475 - Modified postProcessRecord(). See below comments for detail.
 * 08/17/2011       ryzhao      121160 - Rollback the changes.
 * 08/26/2011       dzhang      121130 - Added isSelectAddressAvailable().
 * 04/13/2012       xnie        120683 - Roll backed 121130 fix. Change risk address should NOT have user profile check.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 07/02/2012       tcheng      133964 - 1) Modified postProcessRecord(). Show insured info button or not
 *                                       2) Modified postProcessRecordSet(). Added entitlement field isInsuredInfoAvailable
 * 07/20/2012       sxm         135777 - 1) Disable field if the record is not editable.
 *                                       2) Re-fix 123475 because TEMP record created from OFFICIAL version should NOT
 *                                          be editable during OOSE WIP
 * 01/09/2013       tcheng      140729 - 1) Modified isOccupantAvailable() to remove the condition if the slot is vacant or not.
 *                                       2) Set risk addCode for process in CommonTabEntitlementRecordLoadProcessor
 *                                       3) Modified isOccupantAvailable(),isSlotOccupant(),isCoiHolderAvailable() to get addCode from record
 * 08/06/2013       jshen       146027 - Modified isSurchargePointsAvailable() method to check if risk has primary coverage
 *                                       from the risk record directly instead of calling DAO method.
 * 10/21/2013       fcb         145725 - some optimization in the overall flow.
 * 02/24/2014       xnie        148083 - Modified postProcessRecordSet() to
 *                                       1) Add field isRiskSumAvailable and isRiskAvailable when passed in recordSet's
 *                                          size is 0.
 *                                       2) Make risk page is available and new risk page is unavailable.
 * 12/17/2014       awu         150201 - Modified postProcessRecord to set edit indicator, OBR need it.
 * 06/10/2015       wdang       157211 - Added entitlement field isInsuredTrackingAvailable.
 * 01/06/2016       wdang       168069 - Removed the logic of isRiskSumAvailable/isRiskAvailable.
 * 01/21/2015       tzeng       166924 - Added isAlternativeRatingMethodEditable default editable in postProcessRecord.
 * 06/08/2016       fcb         177372 - 1) Changed int to long
 * 09/06/2016       lzhang      179346 - Modified postProcessRecord/isCoiHolderAvailable:
 *                                       Affiliation/COI Holder buttons show/
 *                                       hide is controlled by base_code and entity_id.
 * 10/31/2017       eyin        169483 - Added isAddtlExposureAvailable.
 * 04/02/2018       tzeng       192229 - Modified postProcessRecord to added isAddtlExposureAvailable function.
 * ---------------------------------------------------
 */
public class RiskEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // Set risk addCode in record for process in CommonTabEntitlementRecordLoadProcessor
        if (RiskFields.hasRiskTypeCode(record)) {
            record.setFieldValue(RiskFields.ADD_CODE, getAddCode().get(RiskFields.getRiskTypeCode(record)));
            record.setFieldValue(RiskFields.BASE_CODE, getBaseCode().get(RiskFields.getRiskTypeCode(record)));
        }

        // Determine if Occupant is available
        record.setFieldValue("isOccupantAvailable", isOccupantAvailable(record));
        // Determine if a risk is a Slot occupant
        record.setFieldValue("isSlotOccupant", isSlotOccupant(record));
        // Determine if COI Holder is available
        record.setFieldValue("isCoiHolderAvailable", isCoiHolderAvailable(record));
        // Enable Multi-Exposure
        record.setFieldValue("isAddtlExposureAvailable", isAddtlExposureAvailable(record));
        // Determine if Emp Phys is available
        record.setFieldValue("isEmpPhysAvailable", isEmpPhysAvailable(getPolicyHeader(), record));
        // Determine if Insured History is available
        record.setFieldValue("isInsuredHistorylAvailable", isInsuredHistorylAvailable(record));
        // Determine if Surcharge Point is available
        record.setFieldValue("isSurchargePointsAvailable", isSurchargePointsAvailable(record));
        // Determine if Copy New is available
        record.setFieldValue("isCopyNewAvailable", isCopyNewAvailable(record));
        // Determine if Insured Information is available
        record.setFieldValue("isInsuredInfoAvailable", isInsuredInfoAvailable(record));
        // Enable suspension
        record.setFieldValue("isSuspensionAvailable", YesNoFlag.Y);
        // Enable ERP
        record.setFieldValue("isErpOptionAvailable", YesNoFlag.Y);
        // Enable Insured Tracking
        record.setFieldValue("isInsuredTrackingAvailable", YesNoFlag.Y);

        // Complex Rules (from [Policy_Folder_UI section 3.5.1.1]
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        ScreenModeCode screenModeCode = getScreenModeCode();
        if (!StringUtils.isBlank(RiskFields.getOfficialRecordId(record))
            && recordModeCode.isTemp()) {
            YesNoFlag afterImageB = CoverageFields.getAfterImageRecordB(record);
            String riskEffFromDate = RiskFields.getRiskEffectiveFromDate(record);
            String riskEffToDate = RiskFields.getRiskEffectiveToDate(record);
            if (riskEffFromDate.equals(riskEffToDate) && screenModeCode.isRenewWIP()) {
                record.setEditIndicator(YesNoFlag.N);
            }
            else if (afterImageB.booleanValue()) {
                if (screenModeCode.isOosWIP() && !recordModeCode.isRequest()) {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }
            else if (!afterImageB.booleanValue()) {
                record.setEditIndicator(YesNoFlag.N);
            }
        }
        else {
            if (screenModeCode.isOosWIP()) {
                PMStatusCode status = RiskFields.getRiskStatus(record);
                if (status.isPending() && getPolicyHeader().isInitTermB()) {
                    record.setEditIndicator(YesNoFlag.Y);
                }
                else {
                    record.setEditIndicator(YesNoFlag.N);
                }
            }
        }

        // Set edit indicator for slot risk.
        setEditIndicatorForSlot(record);

        if (isRecordSetReadOnly()) {
            record.setEditIndicator(YesNoFlag.N);
        }

        // Determine if field is editable based on record edit indicator
        record.setFieldValue("isRiskEffectiveToDateEditable", isRiskEffectiveToDateEditable(record));
        record.setFieldValue("isPrimaryRiskBEditable", isPrimaryRiskBEditable(record));
        record.setFieldValue("isPracticeStateCodeEditable", isPracticeStateEditable(record));

        // by default set coverage class to invisible
        record.setFieldValue("isCoverageClassAvailable", YesNoFlag.N);

        // by default set alternative rating method editable
        record.setFieldValue("isAlternativeRatingMethodEditable", YesNoFlag.Y);

        //set premium effective date which will be used to get premium class codes
        String premClassEffDt = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_PREM_CLASS_EFF_DT);
        if (StringUtils.isBlank(premClassEffDt) || premClassEffDt.equals("RISK_EFF_DT")) {
           record.setFieldValue("premClassEffectiveDate", RiskFields.getRiskEffectiveFromDate(record));
        }
        else if (premClassEffDt.equals("TERM_EFF_DT")) {
           record.setFieldValue("premClassEffectiveDate", getPolicyHeader().getTermEffectiveFromDate());
        }
        else if (premClassEffDt.equals("TRANS_EFF_DT")) {
           record.setFieldValue("premClassEffectiveDate",
               getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate());
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isRiskEffectiveToDateEditable");
            pageEntitlementFields.add("isPrimaryRiskBEditable");
            pageEntitlementFields.add("isOccupantAvailable");
            pageEntitlementFields.add("isSlotOccupant");
            pageEntitlementFields.add("isCoiHolderAvailable");
            pageEntitlementFields.add("isAddtlExposureAvailable");
            pageEntitlementFields.add("isEmpPhysAvailable");
            pageEntitlementFields.add("isCoverageClassAvailable");
            pageEntitlementFields.add("isInsuredHistorylAvailable");
            pageEntitlementFields.add("isSuspensionAvailable");
            pageEntitlementFields.add("isErpOptionAvailable");
            pageEntitlementFields.add("isInsuredInfoAvailable");
            pageEntitlementFields.add("isInsuredTrackingAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
            recordSet.getSummaryRecord().setFieldValue("isCoverageClassAvailable", YesNoFlag.N);
            recordSet.getSummaryRecord().setFieldValue("isErpOptionAvailable", YesNoFlag.Y);
        }

        // check add option avaiable
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote() ||
            getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP() ||
            (getScreenModeCode().isOosWIP() || getScreenModeCode().isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            recordSet.getSummaryRecord().setFieldValue("isAddAvailable", YesNoFlag.N);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isAddAvailable", YesNoFlag.Y);
        }

        // If OOSE, the changed risk record's effective to date should not be editable.
        if (getScreenModeCode().isOosWIP()) {
            Iterator recIter = recordSet.getRecords();
            while (recIter.hasNext()) {
                Record record = (Record) recIter.next();
                RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
                if (recordModeCode.isTemp()) {
                    String riskBaseRecordId = RiskFields.getRiskBaseRecordId(record);
                    Iterator iter = recordSet.getRecords();
                    while (iter.hasNext()) {
                        Record rec = (Record) iter.next();
                        String baseRecordId = RiskFields.getRiskBaseRecordId(rec);
                        // record has been changed
                        if (riskBaseRecordId.equals(baseRecordId)) {
                            record.setFieldValue("isRiskEffectiveToDateEditable", "N");
                        }
                    }
                }
            }
        }
        //set copy all available
        if (recordSet.getSize() == 0) {
            recordSet.getSummaryRecord().setFieldValue("isCopyAllAvailable", YesNoFlag.N);
        }
        else {
            recordSet.getSummaryRecord().setFieldValue("isCopyAllAvailable", isCopyAllAvailable());
        }

        // Set readOnly attribute to summary record
        if (isRecordSetReadOnly()) {
            EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), true);
        }
        recordSet.getSummaryRecord().setFieldValue("isConvertCoverageAvailable", isConvertCoverageAvailable());
    }

   /**
    * Check Convert Coverage option status
    *
    * @return
    */
    private YesNoFlag isConvertCoverageAvailable() {
        Logger l = LogUtils.enterLog(getClass(), "isConvertCoverageAvailable");
        YesNoFlag isVisible = YesNoFlag.N;

        PolicyHeader policyHeader = getPolicyHeader();
        PolicyViewMode viewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();

        if (!viewMode.isOfficial()) {
            return isVisible;
        }
        else if (policyHeader.getScreenModeCode().isViewPolicy()) {
            PolicyStatus policyStatus = policyHeader.getPolicyStatus();
            if (policyStatus.isActive()) {
                if (policyHeader.isWipB()) {
                    return isVisible;
                }
                else if (!policyHeader.isQuoteRenewalExists()) {
                    if (policyHeader.getProcessStatusCode().isProcessStatusCancelOnly()) {
                        if (policyHeader.getTermStatus().isActive()) {
                            isVisible = YesNoFlag.Y;
                        }
                    }
                    else if (policyHeader.isLastTerm() && !policyHeader.isShortTermB()) {
                        isVisible = YesNoFlag.Y;
                    }
                }
            }
            else if (policyStatus.isPending()) {
                if (!policyHeader.isQuoteRenewalExists() && !policyHeader.isWipB()) {
                    isVisible = YesNoFlag.Y;
                }
            }
        }

        l.exiting(getClass().getName(), "isConvertCoverageAvailable", isVisible);
        return isVisible;
    }

    /**
     * Set initial engitlement Values for Risk
     *
     * @param riskManager
     * @param policyHeader
     * @param screenModeCode
     * @param addCode
     * @param record
     */
    public static void setInitialEntitlementValuesForRisk(RiskManager riskManager, PolicyHeader policyHeader,
                                                          ScreenModeCode screenModeCode, RecordSet addCode, Record record) {
        RiskEntitlementRecordLoadProcessor entitlementRLP = new RiskEntitlementRecordLoadProcessor(
            riskManager, policyHeader, screenModeCode, addCode);
        entitlementRLP.postProcessRecord(record, true);

    }

    /**
     * Check if Risk effective date is editable
     *
     * @param inputRecord
     * @return String
     */
    private String isRiskEffectiveToDateEditable(Record inputRecord) {
        String isEditable = null;

        if (RiskFields.hasRiskTypeCode(inputRecord)) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);
            if (getScreenModeCode().isOosWIP() && (recordModeCode.isRequest() || recordModeCode.isTemp())) {
                isEditable = "Y";
            }
            else if (getScreenModeCode().isCancelWIP() || getScreenModeCode().isResinstateWIP()) {
                isEditable = "N";
            }
            else {
                isEditable = "X";
            }
        }

        return isEditable;
    }

    /**
     * check if Copy All/Delete All available for current policy
     *
     * @return YesNoFlag
     */
    private YesNoFlag isCopyAllAvailable() {
        YesNoFlag isAvailable = YesNoFlag.N;

        ScreenModeCode screenMode = getScreenModeCode();
        if (screenMode.isWIP() || screenMode.isManualEntry() ||
            screenMode.isRenewWIP() && getPolicyHeader().isInitTermB()) {
            isAvailable = YesNoFlag.Y;
        }

        return isAvailable;
    }

    /**
     * Check if primary risk indicator is editable
     *
     * @param inputRecord
     * @return String
     */
    private YesNoFlag isPrimaryRiskBEditable(Record inputRecord) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (inputRecord.getEditIndicatorBooleanValue()) {
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);

            if (StringUtils.isBlank(RiskFields.getOfficialRecordId(inputRecord))
                && recordModeCode.isTemp()) {
                isEditable = YesNoFlag.Y;
            }
            else {
                SysParmProvider sysParm = SysParmProvider.getInstance();
                String syspram = sysParm.getSysParm(SysParmIds.PM_CHG_PRIMARY_RISK, "N");
                if (YesNoFlag.getInstance(syspram).booleanValue()) {
                    isEditable = YesNoFlag.Y;
                }
            }
        }

        return isEditable;
    }

    /**
     * Check if Occupant is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isOccupantAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        String sTransEffDate = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();

        if (RiskFields.hasRiskTypeCode(record) && RiskFields.hasRiskName(record) && RiskFields.hasRiskStatus(record)) {
            String riskAddCode = (String) RiskFields.getAddCode(record);
            // The occupant option will be enable when
            // 1. Selected row's risk status is not CANCEL
            // 2. The slot risk's effective from date is less than or equal to last transaction effective date.
            // 3. The slot risk's effective to date is greater than last transaction effective date.
            if (!RiskFields.getRiskStatus(record).isCancelled() &&
                "SLOT".equals(riskAddCode) &&
                !record.getDateValue(RiskFields.RISK_EFFECTIVE_FROM_DATE).after(DateUtils.parseDate(sTransEffDate)) &&
                record.getDateValue(RiskFields.RISK_EFFECTIVE_TO_DATE).after(DateUtils.parseDate(sTransEffDate))) {
                isAvailable = YesNoFlag.Y;
            }
        }

        // Hide occupant option based on sceen Mode
        if (getScreenModeCode().isViewPolicy() || getScreenModeCode().isViewEndquote()) {
            isAvailable = YesNoFlag.N;
        }

        return isAvailable;
    }

    /**
     * Check if Copy New is available
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isCopyNewAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        PolicyHeader policyHeader = getPolicyHeader();
        boolean isPolicy = policyHeader.getPolicyCycleCode().isPolicy();

        // The Copy New option will be enable when
        // 1. Selected is a Policy.
        // 2. Screen mode is under View Policy, View Endquote, Cancel WIP, Reinstate WIP, Renewal WIP.
        // 3. Screen mode is not OOS WIP.
        // 4. Displayed is the last term.
        // 5. Selected risk is a person entity and has been saved as official at least once.
        if (!isPolicy) {
            return YesNoFlag.N;
        }
        
        // check copy new option based on screen mode
        if (!getScreenModeCode().isViewPolicy() && !getScreenModeCode().isViewEndquote()
                && !getScreenModeCode().isCancelWIP() && !getScreenModeCode().isResinstateWIP()
                && !getScreenModeCode().isRenewWIP()) {
            return YesNoFlag.N;
        } else if (!policyHeader.isLastTerm()) {
            return YesNoFlag.N;
        }

        if (getScreenModeCode().isOosWIP()) {
            return YesNoFlag.N;
        }

        String entityType = record.getStringValue(RiskFields.ENTITY_TYPE, "");
        long entityId = Long.parseLong(record.getStringValue(RiskFields.ENTITY_ID, "0"));
        if (!RiskFields.getRiskStatus(record).isPending() && entityType.equals("P") && entityId > 0) {
            isAvailable = YesNoFlag.Y;
        }

        return isAvailable;
    }

    /**
     * Check if the risk is a Slot Occupant
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag isSlotOccupant(Record record) {
        YesNoFlag isSlotOccupant = YesNoFlag.N;

        if (RiskFields.hasRiskTypeCode(record) && RiskFields.hasRiskName(record) && RiskFields.hasRiskStatus(record)) {
            String riskAddCode = (String) RiskFields.getAddCode(record);
            if ("SLOT".equals(riskAddCode) && record.hasStringValue(RiskFields.ENTITY_ID) &&
                !RiskFields.getEntityId(record).equals("0")) {
                isSlotOccupant = YesNoFlag.Y;
            }
        }

        return isSlotOccupant;
    }


    /**
     * Check if COI Holder option is available
     *
     * @param record
     * @return
     */
    private YesNoFlag isCoiHolderAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.Y;

        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        if (recordModeCode.isRequest()) {
            isAvailable = YesNoFlag.N;
        }
        else if (RiskFields.hasEntityId(record) && RiskFields.hasRiskTypeCode(record)){
          Long entityID = record.getLongValue(RiskFields.ENTITY_ID).longValue();
          String riskBaseCode = (String) RiskFields.getBaseCode(record);
           if (entityID == 0 && "SLOT".equals(riskBaseCode)){
               isAvailable = YesNoFlag.N;
           }
        }

        return isAvailable;
    }

    /**
     * Check if Emp Phys option is available
     *
     * @param policyHeader
     * @param record
     * @return
     */
    private YesNoFlag isEmpPhysAvailable(PolicyHeader policyHeader, Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        //this field will be set in RiskManger's loadRiskAddlInfo method when one risk is selected

        return isAvailable;
    }

    /**
     * Check if Practice State option is editable
     *
     * @param record
     * @return
     */
    private YesNoFlag isPracticeStateEditable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (record.getEditIndicatorBooleanValue() && PMCommonFields.getRecordModeCode(record).isTemp() &&
            StringUtils.isBlank(RiskFields.getOfficialRecordId(record))) {
            isAvailable = YesNoFlag.Y;
        }

        return isAvailable;
    }

    /**
     * Check if Insured History is available
     *
     * @param record
     * @return
     */
    private YesNoFlag isInsuredHistorylAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;

        if (record.hasField("entityID") && (record.getLongValue("entityID").longValue() > 0)) {
            isAvailable = YesNoFlag.Y;
        }

        return isAvailable;
    }

    /**
     * Check if Surcharge Points is available
     *
     * @param record
     * @return
     */
    private YesNoFlag isSurchargePointsAvailable(Record record) {
        return record.hasStringValue(RiskFields.PRIMARY_COVG_EXISTS) ? RiskFields.getPrimaryCovgExists(record) : YesNoFlag.N;
        }

    /**
     * Check if Exposure is available by risk type.
     *
     * @param record
     * @return
     */
    private YesNoFlag isAddtlExposureAvailable(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddtlExposureAvailable", record);
        }
        YesNoFlag isAvailable =  YesNoFlag.getInstance(getRiskManager().isAddtlExposureAvailable(record,
                                                                                                 getPolicyHeader()));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddtlExposureAvailable", isAvailable);
        }
        return isAvailable;
    }

    /**
     * Set edit indicator for slot risk.
     *
     * @param record
     */
    private void setEditIndicatorForSlot(Record record) {
        String editIndicator = record.getEditIndicator();
        if (!YesNoFlag.getInstance(editIndicator).booleanValue() && RiskFields.hasRiskName(record)) {
            String riskName = record.getStringValue(RiskFields.RISK_NAME);
            Date effFrom = record.getDateValue(RiskFields.RISK_EFFECTIVE_FROM_DATE);
            Date effTo = record.getDateValue(RiskFields.RISK_EFFECTIVE_TO_DATE);
            RecordMode recordModeCode = RecordMode.getInstance(record.getStringValue("recordModeCode"));
            if (recordModeCode.isTemp() && "VACANT".equalsIgnoreCase(riskName) && effFrom.equals(effTo)) {
                record.setEditIndicator(YesNoFlag.Y);
            }
        }
    }
      /**
     * Check if current recordSet is readOnly
     *
     * @return
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        boolean isInitTermB = getPolicyHeader().isInitTermB();
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        // All available fields are readonly in following screen mode:
        // CANCELWIP,  REINSTATEWIP (3.5.3)
        if (screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            isReadOnly = true;
        }

        if (!isReadOnly) {
        // All available fields are readonly in following view mode:
        // VIEW_POLICY Mode, VIEW_ENDQUOTE Mode(s) (3.5.3)
        // Non initial terms during OOSEWIP or RENEWWIP
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.equals(PolicyViewMode.OFFICIAL) ||
            viewMode.equals(PolicyViewMode.ENDQUOTE) ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !isInitTermB) {
            isReadOnly = true;
        }
        }
        // In OOSWIP screen Mode (3.4.5), it is handled by
        // It is controlled by EditIndicator in this module

        return isReadOnly;
    }

    /**
     * Check if Insured Information is available
     *
     * @param record
     * @return
     */
    private YesNoFlag isInsuredInfoAvailable(Record record) {
        YesNoFlag isAvailable = YesNoFlag.N;
        if (record.hasStringValue(RiskFields.ENTITY_ID) && record.getLongValue(RiskFields.ENTITY_ID) > 0){
            isAvailable = YesNoFlag.Y;
        }
        return isAvailable;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public RiskEntitlementRecordLoadProcessor() {
    }

    public RiskEntitlementRecordLoadProcessor(RiskManager riskManager, PolicyHeader policyHeader,
                                              ScreenModeCode screenModeCode, RecordSet addCode) {
        setPolicyHeader(policyHeader);
        setRiskManager(riskManager);
        setScreenModeCode(screenModeCode);
        setAddCode(addCode);
        setBaseCode(addCode);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    public Map getAddCode() {
        return m_addCode;
    }

    public void setAddCode(RecordSet addCode) {
        // Get Add Codes for all risk types
        Iterator it = addCode.getRecords();
        HashMap map = new HashMap();
        while (it.hasNext()) {
            Record addCodeAndRiskTypeRecord = (Record) it.next();
            map.put(addCodeAndRiskTypeRecord.getStringValue(RiskFields.RISK_TYPE_CODE),
                addCodeAndRiskTypeRecord.getStringValue(RiskFields.ADD_CODE));
        }
        m_addCode = map;
    }

    public Map getBaseCode() {
        return m_baseCode;
    }

    public void setBaseCode(RecordSet baseCode) {
        // Get Base Codes for all risk types
        Iterator it = baseCode.getRecords();
        HashMap map = new HashMap();
        while (it.hasNext()) {
            Record addCodeAndRiskTypeRecord = (Record) it.next();
            map.put(addCodeAndRiskTypeRecord.getStringValue(RiskFields.RISK_TYPE_CODE),
                addCodeAndRiskTypeRecord.getStringValue(RiskFields.BASE_CODE));
        }
        m_baseCode = map;
    }

    private PolicyHeader m_policyHeader;
    private RiskManager m_riskManager;
    private ScreenModeCode m_screenModeCode;
    private Map m_addCode;
    private Map m_baseCode;
}
