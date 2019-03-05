package dti.pm.componentmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.DateUtils;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.componentmgr.ComponentFields;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.coveragemgr.prioractmgr.PriorActFields;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailScreenMode;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.transactionmgr.TransactionFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the component web page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 28, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2008       fcb         postProcessRecordSet: isCompRenewEditable and isDataEditable
 *                              added to the pageEntitlementFields for 0 size recordset.
 * 01/16/2008       fcb         isCompEffectiveToDateAvailable and isCompValueEditable: modified logic.
 * 01/30/2008       fcb         isDelCompAvailable: extra logic added.
 * 03/27/2008       yhchen      #81217 Add logics to check if low value and high value is null
 * 05/05/2008       yhchen      #81217 get rid of the compoent record which hasn't owner prior coverage record
 * 11/22/2010       syang       Issue 113807 - Modified isCompRenewEditable() to enable the Renew field for non TEMP
 *                              record in regular endorsement.
 * 04/15/2011       ryzhao      116160 - set note column as hyperlink to common notes functionality.
 * 05/04/2011       syang       120017 - Rollback the changes of issue 116160.
 * 05/05/2011       dzhang      117614 - Modified isCompCycleDateEditable to disable the Cycel Begin Date field of cycled component when "Chg Date".
 * 10/28/2011       ryzhao      122840 - Modified isOosChangeValueAvailable() and isOosChangeDateAvailable() to check if
 *                              the official component record has a temp record exists for the same transaction.
 *                              Please see below comments for why we check temp record here.
 * 11/01/2011       ryzhao      122840 - Modified per Stephen's comments.
 * 04/09/2012       jshen       131569 - Modified isEditable() method to execute coverage related logic when owner recordSet's size > 0
 * 07/20/2012       sxm         135777 - Disable field if the record is not editable.
 * 09/10/2012       awu         137179 - Added a condition in isCompEffectiveToDateAvailable() to check if there was the field "tailStatus" in ownerRecord or not.
 * 11/22/2012       adeng       139185 - Modified the isEditable() method to correctly set the component record's edit
 *                              indicator according with its owner's record's edit indicator.
 * 12/06/2012       adeng       139185 - 1.Added the isEditableForTail() method to correctly set the component record's edit
 *                                       indicator for tail.
 *                                       2.Modified the postProcessRecord() method to execute isEditableForTail() when record's
 *                                       owner is tail.
 *                                       3.Modified the isDelCompAvailable() method to correctly set the delete button only available
 *                                       for newly added non-saved as official rows.
 *                                       4.Modified the isCompValueEditable() method to add condition that the tailStatus is 'OFFER' or 'ACTIVE'
 *                                       to correctly set component value to editable with the existing logic.
 * 01/08/2013       tcheng      140441 - Modified postProcessRecord() to make sure fields parentCoverageId  and compCovgDesc available if ownerRecord is null
 * 01/22/2013       adeng       141183 - Modified postProcessRecord() to set noteB to 'N' when note field is set to invisible in WebWB.
 * 01/25/2013       adeng       141183 - Roll backed last change.
 * 01/30/2013       xnie        140979 - Modified isEditable() to check size of matched coverages before getting record from it.
 * 03/12/2013       adeng       141851 - 1.Modified isEditable() to using component's record edit indicator to do the control during OOSE WIP.
 *                                       2.Solved some strange character output problems in comments.
 * 04/22/2013       xnie        142770 - Modified isDelCompAvailable() to hide delete button when the component's
 *                                       coverage's or policy's status code is canceled before cancel wip rule check.
 * 04/19/2013       tcheng      143784 - Modified isEditable() to retrieve editable coverage's record.
 * 04/25/2013       xnie        142770 - 1) Roll back prior fix.
 *                                       2) Modified isDelCompAvailable() to hide delete button when the component's
 *                                          coverage's or policy's status code is canceled before cancel wip rule check.
 *                                       3) Added a method getCoverageRec().
 *                                       4) Modified isEditable() to call the new method getCoverageRec().
 * 10/02/2013       fcb         145725 - isProblemPolicy: changed the parameter.
 *                                     - various optimizations.
 * 03/03/2014       adeng       148692 - 1) Added isRecordSetReadOnly() for component in prior acts page.
 *                                       2) Modified postProcessRecordSet() to hide the buttons when record set is read
 *                                       only.
 *                                       3) Modified postProcessRecord() to set fields to read only when record set is
 *                                       read only.
 * 12/14/2015       fcb         168152   1) Refactored the logic that uses system parameter to use local variable rather
 *                                       than class member variable.
 * 06/07/2017       xnie        185742   1) Modified isCompValueEditable() to make it consistent with powerbuilder.
 *                                       2) Modified isCompEffectiveToDateAvailable() to make it consistent with
 *                                          powerbuilder.
 *                                       3) Modified isCompCycleDateEditable() to make it consistent with powerbuilder.
 *                                       4) Modified isEditable() to set edit indicator based on both passed in record's
 *                                          indicator and coverage record's edit indicator.
 * 12/27/2017       lzhang      190565   Modified isCompValueEditable(): set scheduledB default value to "N"
 * ---------------------------------------------------
 */

public class ComponentEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        String componentCode = ComponentFields.getCoverageComponentCode(record);
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        Record ownerRecord = null;
        //for tail owner , only those has owner records are visible
        if (getOwner() != null && getOwner().isTailOwner() && !recordModeCode.isTemp()) {
            String tailCovBaseId = CoverageFields.getCoverageBaseRecordId(record);
            ownerRecord = (Record) getOwnerRecordMap().get(tailCovBaseId);
            if (ownerRecord == null) {
                return false;
            }
        }
        else if (getOwner() != null && getOwnerRecordMap() != null && getOwner().isCoverageOwner()) {
            String covBaseId = CoverageFields.getCoverageBaseRecordId(record);
            ownerRecord = (Record) getOwnerRecordMap().get(covBaseId);
            if (ownerRecord != null) {
                record.setFieldValue("parentCoverageId", CoverageFields.getCoverageId(ownerRecord));
            }
            else {
                record.setFieldValue("parentCoverageId", "");
            }
        }

        if (getOwner() != null && getOwner().isPriorActOwner()) {
            if (getOwnerRecords() != null) {
                Record ownerRec = getOwnerPriorActCoverage(record);
                if(ownerRec == null){
                    return false;
                }
                String covgId = CoverageFields.getCoverageId(ownerRec);
                CoverageFields.setCoverageId(record, covgId);
            }
            record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);
            record.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
            record.setFieldValue("isChgCompDateAvailable", YesNoFlag.N);
        }
        else {
            //set coverage description
            if (ownerRecord != null && ownerRecord.hasField(CoverageFields.PRODUCT_COVERAGE_DESC))  {
                record.setFieldValue("compCovgDesc", CoverageFields.getProductCoverageDesc(ownerRecord));
            }
            else {
                record.setFieldValue("compCovgDesc", "");
            }

            // Check Cycle detail option avaialbe
            if ("NEWDOCTOR".equals(componentCode) || record.getBooleanValue("cycledB").booleanValue()) {
                record.setFieldValue("isCycleDetailAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
            }

            // Check if component code to determine if surcharge points option is available
            if (isCodeMatchForSurcharge(componentCode)) {
                record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);
            }

            // OOSE logics are not applied for tail coverage/compoent
            if (getOwner() != null && !getOwner().isTailOwner()) {
                // Determine if Change Value option is available or not
                record.setFieldValue("isChgCompValueAvailable", isOosChangeValueAvailable(record));
                // Determine if Change Date option is available or not
                record.setFieldValue("isChgCompDateAvailable", isOosChangeDateAvailable(record));
            }
        }

        // Determine if Delete Component option is available or not
        record.setFieldValue("isDelCompAvailable", isDelCompAvailable(record));

        //set editIndicator
        if (getOwner() != null) {
            if (getOwner().isPriorActOwner()) {
                record.setEditIndicator(YesNoFlag.Y);
            }
            else if (getOwner().isCoverageOwner()) {
                record.setEditIndicator(isEditable(record));
            }
            else if (getOwner().isPolicyOwner()) {
                record.setEditIndicator(isEditable(record));
            }
            else if (getOwner().isTailOwner()) {
                record.setEditIndicator(isEditableForTail(record));
            }
        }

        // Determine if field is editable based on record edit indicator
        record.setFieldValue("isCompEffectiveToDateEditable", isCompEffectiveToDateAvailable(record));
        record.setFieldValue("isCompCycleDateEditable", isCompCycleDateEditable(record));
        record.setFieldValue("isCompRenewEditable", isCompRenewEditable(record));
        record.setFieldValue("isCompValueEditable", isCompValueEditable(record));
        record.setFieldValue("isDataEditable", isDataEditable(record));

        // Add fields for part time notes
        if (record.hasStringValue("ptnotes")) {
            String ptnotes = record.getStringValue("ptnotes");
            if ("Y".equals(ptnotes)) {
                record.setFieldValue("partTimeNotes", ptnotes);
            }
            else {
                record.setFieldValue("partTimeNotes", "");
            }
        }

        if (getOwner() != null && getOwner().isPriorActOwner() && getOwnerRecords() != null && isRecordSetReadOnly()) {
            record.setFieldValue("isDelCompAvailable", YesNoFlag.N);
            record.setFieldValue("isCompEffectiveToDateEditable", YesNoFlag.N);
            record.setFieldValue("isCompValueEditable", YesNoFlag.N);
            record.setFieldValue("isCompCycleDateEditable", YesNoFlag.N);
            record.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
            record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
            record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);
            record.setFieldValue("isDataEditable", YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }


    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isCycleDetailAvailable");
            pageEntitlementFields.add("isSurchargePointsAvailable");
            pageEntitlementFields.add("isDelCompAvailable");
            pageEntitlementFields.add("isChgCompValueAvailable");
            pageEntitlementFields.add("isChgCompDateAvailable");
            pageEntitlementFields.add("isCompCycleDateEditable");
            pageEntitlementFields.add("isCompEffectiveToDateEditable");
            pageEntitlementFields.add("isCompValueEditable");
            pageEntitlementFields.add("isCompRenewEditable");
            pageEntitlementFields.add("isDataEditable");

            if (getOwner() != null && getOwner().isPriorActOwner()) {
                pageEntitlementFields.add(CoverageFields.COVERAGE_ID);
            }

            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        // if coverage recordSet is empty, clear all component records
        if (getOwner() != null && getOwner().isPriorActOwner()) {
            if (getOwnerRecords() == null || getOwnerRecords().getSize() == 0) {
                List fieldNameList = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
                Record sumRec = recordSet.getSummaryRecord();
                recordSet.clear();
                recordSet.addFieldNameCollection(fieldNameList);
                recordSet.setSummaryRecord(sumRec);
            }
            else {
                boolean isRecordSetReadOnly = isRecordSetReadOnly();
                if (isRecordSetReadOnly) {
                    recordSet.getSummaryRecord().setFieldValue("isAddAvailable", YesNoFlag.N);
                }
                // Set readOnly attribute to summary record
                EntitlementFields.setReadOnly(recordSet.getSummaryRecord(), isRecordSetReadOnly);
            }
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }


    /**
     * Set initial engitlement Values for Component
     *
     * @param componentManager
     * @param policyHeader
     * @param screenModeCode
     * @param record
     */
    public static void setInitialEntitlementValuesForComponent(ComponentManager componentManager,
                                                               PolicyHeader policyHeader,
                                                               Record inputRecord,
                                                               ScreenModeCode screenModeCode,
                                                               ComponentOwner owner,
                                                               Record record) {
        ComponentEntitlementRecordLoadProcessor entitlementRLP = new ComponentEntitlementRecordLoadProcessor(
            componentManager, policyHeader, inputRecord, screenModeCode, owner, null);
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    /**
     * Check if the component record is editable
     *
     * @return edit indicator
     */
    private YesNoFlag isEditable(Record record) {
        YesNoFlag origEditIndicator = YesNoFlag.getInstance(record.getEditIndicatorBooleanValue());

        if (getScreenModeCode().isOosWIP() || origEditIndicator.equals(YesNoFlag.N)) {
            return origEditIndicator;
        }
        else{
            Long coverageBaseRecordId = new Long(CoverageFields.getCoverageBaseRecordId(record));
            YesNoFlag editIndicator = (YesNoFlag) getCompEditIndMap().get(coverageBaseRecordId);
            if (editIndicator == null && getOwnerRecords() != null && getOwnerRecords().getSize() > 0) {
                editIndicator = YesNoFlag.N;

                //get owner coverage record
                Record coverageRec = getCoverageRec(record);
                if (coverageRec != null) {
                    if (coverageRec.getEditIndicatorBooleanValue()) {
                        editIndicator = YesNoFlag.Y;
                    }
                }
                getCompEditIndMap().put(coverageBaseRecordId, editIndicator);
            }
            else if (editIndicator == null) {
                //if the status of owner coverage is PENDING, always set edit indicator to Y.
                editIndicator = YesNoFlag.Y;
            }
            return editIndicator;
        }
    }

    /**
     * Check if the component record is editable for tail
     *
     * @return edit indicator
     */
    private YesNoFlag isEditableForTail(Record record) {
        YesNoFlag editIndicator = YesNoFlag.N;
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
        String tailCovBaseId = CoverageFields.getCoverageBaseRecordId(record);
        Record ownerRecord = (Record) getOwnerRecordMap().get(tailCovBaseId);
        if (ownerRecord == null) {
            ownerRecord = getInputRecord();
        }
        TailScreenMode tailScreenMode = TailScreenMode.getInstance(TailFields.getTailScreenMode(ownerRecord));
        if (tailScreenMode.isWIP()) {
            editIndicator = YesNoFlag.Y;
        }
        else if (tailScreenMode.isUpdate()) {
            if (ownerRecord != null && ownerRecord.hasStringValue(TailFields.TAIL_STATUS)) {
                String tailStatus = ownerRecord.getStringValue(TailFields.TAIL_STATUS);
                if (tailStatus.equals(TAIL_STATUS_OFFER) || tailStatus.equals(TAIL_STATUS_ACTIVE)) {
                    editIndicator = YesNoFlag.Y;
                }
            }
        }
        else if (tailScreenMode.isUpdatable() || tailScreenMode.isViewOnly()) {
            editIndicator = YesNoFlag.N;
        }
        return editIndicator;
    }


    /**
     * Check if component code matches with system parameter PM_CVGCMPCD_MORE_BTN
     *
     * @param componentCode
     * @return
     */
    private boolean isCodeMatchForSurcharge(String componentCode) {
        boolean isMatch = false;
        String sysParm = "," + SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CVGCMPCD_MORE_BTN) + ",";

        if (!StringUtils.isBlank(sysParm)) {
            StringUtils.replace(sysParm, " ", "");
            String pat = "," + componentCode + ",";
            if (sysParm.indexOf(pat) != -1) {
                isMatch = true;
            }
        }
        return isMatch;
    }

    private YesNoFlag isDelCompAvailable(Record inputRecord) {
        YesNoFlag isAvailable = YesNoFlag.N;
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);

        //get owner coverage's status
        PMStatusCode coverageStatus = null;
        if (getOwner().isCoverageOwner()) {
            if (getOwnerRecords() != null && getOwnerRecords().getSize() > 0) {
                Record coverageRec = getCoverageRec(inputRecord);
                if (coverageRec != null) {
                    if (CoverageFields.hasCoverageStatus(coverageRec)) {
                        coverageStatus = CoverageFields.getCoverageStatus(coverageRec);
                    }
                }
            }
            // If add a new coverage level component or change a coverage levle component during OOSE,
            // get the coverage status.
            else {
                if (inputRecord.hasField(CoverageFields.COVERAGE_STATUS)) {
                    coverageStatus = CoverageFields.getCoverageStatus(inputRecord);
                }
            }
        }

        // issue#102310, the delete option is not available in REINSTATEWIP
        if ((!getScreenModeCode().isCancelWIP()&&!getScreenModeCode().isResinstateWIP())
            || (getScreenModeCode().isResinstateWIP() && getComponentManager().isProblemPolicy(getPolicyHeader()))) {
            if (recordModeCode.isTemp() || recordModeCode.isRequest()) {
                isAvailable = YesNoFlag.Y;
            }
        }
        // Delete component should be unavailable when it's coverage is canceled for coverage level component.
            if (getOwner().isCoverageOwner() && coverageStatus != null) {
            if (coverageStatus.isCancelled()) {
                isAvailable = YesNoFlag.N;
            }
        }
        // Delete component should be unavailable when policy is canceled for policy level component.
        else if (getOwner().isPolicyOwner()) {
            if (getPolicyHeader().getTermStatus().isCancelled()) {
                isAvailable = YesNoFlag.N;
            }
        }
        if (getScreenModeCode().isCancelWIP()
            && ((ComponentManagerImpl) m_componentManager).isCancWipEdit(m_policyHeader).equals(YesNoFlag.Y)) {
            if (recordModeCode.isTemp()) {
                isAvailable = YesNoFlag.Y;
            }
        }
        if (getOwner() != null && getOwner().isTailOwner()) {
            String officialRecordId = null;
            if (inputRecord.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                officialRecordId = CoverageFields.getOfficialRecordId(inputRecord);
            }
            // Delete option is available for temp record in tail coverage/componnent when policy is in cancelWIP
            if (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)) {
                isAvailable = YesNoFlag.Y;
            }
            else {
                isAvailable = YesNoFlag.N;
            }
        }

        // Fix issue 100751. Delete component is visible for the OOS initialized term in WIP mode.
        PolicyViewMode viewMode = getPolicyHeader().getPolicyIdentifier().getPolicyViewMode();
        if (viewMode.isOfficial() ||
            (isAvailable.booleanValue() && getScreenModeCode().isOosWIP() && viewMode.isWIP() && !getPolicyHeader().isInitTermB())) {
              isAvailable = YesNoFlag.N;
        }
        return isAvailable;
    }

    private YesNoFlag isOosChangeValueAvailable(Record inputRecord) {
        YesNoFlag isAvailable = YesNoFlag.N;
        if (getScreenModeCode().isOosWIP() && getPolicyHeader().isInitTermB() &&
            getPolicyHeader().getPolicyIdentifier().getPolicyViewMode().isWIP()) {
            // only if isChgCompValueAvailable is not already exist in inputRecord.
            // This happens when getInitialValuesForOoseComponent which will set isChgCompValueAvailable in ComponentManager.
            if (!inputRecord.hasStringValue("isChgCompValueAvailable")) {
                // 1. If the coverage selected is attached to a risk with a status of 'CANCEL', Change Value option is disabled
                if (getOwner() != null && getOwner().isPolicyOwner()) {
                    isAvailable = YesNoFlag.Y;
                }
                else {
                    if (getPolicyHeader().hasRiskHeader()) {
                        PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
                        if (!riskStatus.isCancelled()) {
                            isAvailable = YesNoFlag.Y;
                        }
                    }
                }
                // 2. If the component selected has low value <> high value, Change Value option is enabled
                // Fix issue 102568, system should determine the existence of low/high value.
                if (inputRecord.hasStringValue(ComponentFields.LOW_VALUE) && inputRecord.hasStringValue(ComponentFields.HIGH_VALUE)) {
                    int lowValue = inputRecord.getIntegerValue(ComponentFields.LOW_VALUE).intValue();
                    int highValue = inputRecord.getIntegerValue(ComponentFields.HIGH_VALUE).intValue();
                    if (lowValue != highValue) {
                        isAvailable = YesNoFlag.Y;
                    }
                    else {
                        isAvailable = YesNoFlag.N;
                    }
                }
                else {
                    isAvailable = YesNoFlag.N;
                }
            }

            // If add a new component, system will get coverage page fields' default values
            // and set them into the newly added component record. Take use of it to disable Change Value option.
            if (getOwner() != null && getOwner().isPolicyOwner()) {
                if (inputRecord.hasStringValue("recordModeCode") &&
                    "TEMP".equals(inputRecord.getStringValue("recordModeCode"))) {
                    isAvailable = YesNoFlag.N;
                }
            }
            else {
                PMStatusCode coverageStatus;
                if (CoverageFields.hasCoverageStatus(inputRecord)) {
                    coverageStatus = CoverageFields.getCoverageStatus(inputRecord);
                    if (coverageStatus.isPending()) {
                        isAvailable = YesNoFlag.N;
                    }
                }
            }

            /**
             * Issue 122840
             *
             * Check if the official component record has a temp record existing for the same transaction they are in.
             * If there is a temp record existing, the change value button should be invisible.
             * We will call the DB to check if temp record exists only when isAvailable flag is set to Y by logic above.
             *
             * We need to check the temp record for this unusual scenario described below.
             * The component has two records.
             * One belongs to the term right before the latest term.
             * The other belongs to the latest term and ends at the middle of the term.
             * When we do OOSE in the prior term and change the component exp date to another date
             * in the middle of the latest term.
             * System will close the record which belongs to the latest term
             * and create a temp record which also belongs to the latest term.
             * There is no temp record generated in the prior term and we can do change date action unending.
             * It will generate many overlapping records in the latest term.
             * To fix this issue, we will hide the change date/change value button if there is already one temp record
             * existing for the official component record in the same transaction.
             */
            if (isAvailable.booleanValue() && isComponentTempRecordExist(inputRecord).booleanValue()) {
                isAvailable = YesNoFlag.N;
            }
        }
        return isAvailable;
    }

    private YesNoFlag isOosChangeDateAvailable(Record inputRecord) {
        YesNoFlag isAvailable = YesNoFlag.N;
        if (getScreenModeCode().isOosWIP() && getPolicyHeader().isInitTermB() &&
            getPolicyHeader().getPolicyIdentifier().getPolicyViewMode().isWIP()) {
            // only if isChgCompDateAvailable is not already exist in inputRecord.
            // This happens when getInitialValuesForOoseComponent which will set isChgCompDateAvailable in ComponentManager.
            if (getOwner() != null && getOwner().isPolicyOwner()) {
                isAvailable = YesNoFlag.Y;
            }
            else {
                if (!inputRecord.hasStringValue("isChgCompDateAvailable")) {
                    // 1. If the coverage selected is attached to a risk with a status of 'CANCEL', Change Date option is disabled
                    if (getPolicyHeader().hasRiskHeader()) {
                        PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
                        if (!riskStatus.isCancelled()) {
                            isAvailable = YesNoFlag.Y;
                        }
                    }
                }
            }

            // If add a new component, system will get coverage page fields' default values
            // and set them into the newly added component record. Take use of it to disable Change Value option.
            if (getOwner() != null && getOwner().isPolicyOwner()) {
                // The option won't be available for TEMP record
                if (inputRecord.hasStringValue("recordModeCode") &&
                    "TEMP".equals(inputRecord.getStringValue("recordModeCode"))) {
                    isAvailable = YesNoFlag.N;
                }
            }
            else {
                PMStatusCode coverageStatus;
                if (CoverageFields.hasCoverageStatus(inputRecord)) {
                    coverageStatus = CoverageFields.getCoverageStatus(inputRecord);
                    if (coverageStatus.isPending()) {
                        isAvailable = YesNoFlag.N;
                    }
                }
            }

            /**
             * Issue 122840
             *
             * Check if the official component record has a temp record existing for the same transaction they are in.
             * If there is a temp record existing, the change value button should be invisible.
             * We will call the DB to check if temp record exists only when isAvailable flag is set to Y by logic above.
             *
             * We need to check the temp record for this unusual scenario described below.
             * The component has two records.
             * One belongs to the term right before the latest term.
             * The other belongs to the latest term and ends at the middle of the term.
             * When we do OOSE in the prior term and change the component exp date to another date
             * in the middle of the latest term.
             * System will close the record which belongs to the latest term
             * and create a temp record which also belongs to the latest term.
             * There is no temp record generated in the prior term and we can do change date action unending.
             * It will generate many overlapping records in the latest term.
             * To fix this issue, we will hide the change date/change value button if there is already one temp record
             * existing for the official component record in the same transaction.
             */
            if (isAvailable.booleanValue() && isComponentTempRecordExist(inputRecord).booleanValue()) {
                isAvailable = YesNoFlag.N;
            }
        }
        return isAvailable;
    }

    /**
     * Check if the official component record has a temp record existing for the same transaction they are in.
     *
     * @param inputRecord component record.
     * @return if temp record exist.
     */
    private YesNoFlag isComponentTempRecordExist(Record inputRecord) {
        RecordMode recordModeCode = PMCommonFields.getRecordModeCode(inputRecord);
        if (recordModeCode.isOfficial()) {
            Record checkRecord = new Record();
            ComponentFields.setPolCovCompBaseRecId(checkRecord, ComponentFields.getPolCovCompBaseRecId(inputRecord));
            TransactionFields.setTransactionLogId(checkRecord, getPolicyHeader().getLastTransactionInfo().getTransactionLogId());
            boolean isExist = getComponentManager().isComponentTempRecordExist(checkRecord);
            if (isExist) {
                return YesNoFlag.Y;
            }
        }
        return YesNoFlag.N;
    }

    private YesNoFlag isCompEffectiveToDateAvailable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (record.getEditIndicatorBooleanValue()) {
            String compTypeCode = ComponentFields.getComponentTypeCode(record);
            YesNoFlag afterImgB = CoverageFields.getAfterImageRecordB(record);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            String officialRecordId = null;
            ScreenModeCode smc = getScreenModeCode();

            if (smc.isViewPolicy() || smc.isViewEndquote() || smc.isResinstateWIP()) {
                return isEditable;
            }
            else if (smc.isCancelWIP()) {
                // issue#91203 The PM_CANC_WIP_MODE rules in PM_ATTRIBUTE table do not apply to tail component.
                if (getOwner() != null && getOwner().isTailOwner()) {
                    isEditable = YesNoFlag.Y;
                }
                else {
                    isEditable = ((ComponentManagerImpl) m_componentManager).isCancWipEdit(m_policyHeader);
                }
            }
            else if (getOwner() != null && getOwner().isPriorActOwner()) {
                isEditable = YesNoFlag.Y;
            }
            else if (getOwner() != null && getOwner().isTailOwner()) {
                String tailCovBaseId = CoverageFields.getCoverageBaseRecordId(record);
                Record ownerRecord = (Record) getOwnerRecordMap().get(tailCovBaseId);
                if (ownerRecord == null) {
                    ownerRecord = getInputRecord();
                }
                TailScreenMode tailScreenMode = TailScreenMode.getInstance(TailFields.getTailScreenMode(ownerRecord));
                if (tailScreenMode.isWIP()) {
                    isEditable = YesNoFlag.Y;
                }
                else if (tailScreenMode.isUpdate()) {
                    if (ownerRecord != null && ownerRecord.hasStringValue(TailFields.TAIL_STATUS)) {
                        String tailStatus = ownerRecord.getStringValue(TailFields.TAIL_STATUS);
                        if (tailStatus.equals(TAIL_STATUS_OFFER) || tailStatus.equals(TAIL_STATUS_ACTIVE)) {
                            isEditable = YesNoFlag.Y;
                        }
                    }
                }
                else if (tailScreenMode.isUpdatable() || tailScreenMode.isViewOnly()) {
                    isEditable = YesNoFlag.N;
                }
            }
            else {
                if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                    officialRecordId = CoverageFields.getOfficialRecordId(record);
                }
                if (getScreenModeCode().isOosWIP()) {
                    if (!"ADJUSTMENT".equals(compTypeCode)
                        && afterImgB.booleanValue()
                        && (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)
                        || recordModeCode.isRequest() && "0".equals(officialRecordId))) {
                        isEditable = YesNoFlag.Y;
                    }
                }
                else {
                    if (!"ADJUSTMENT".equals(compTypeCode)
                        &&(afterImgB.booleanValue()
                        && recordModeCode.isTemp() || !recordModeCode.isTemp())) {
                        isEditable = YesNoFlag.Y;
                    }
                }
            }
        }

        return isEditable;
    }

    private YesNoFlag isCompCycleDateEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (record.getEditIndicatorBooleanValue()) {
            String compTypeCode = ComponentFields.getComponentTypeCode(record);
            String covgCompCode = ComponentFields.getCoverageComponentCode(record);
            if (getOwner() != null && getOwner().isPriorActOwner()) {
                if (covgCompCode.equals("NEWDOCTOR"))
                    isEditable = YesNoFlag.Y;
            }
            else {
                YesNoFlag afterImgB = CoverageFields.getAfterImageRecordB(record);
                RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
                String officialRecordId = null;
                if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                    officialRecordId = CoverageFields.getOfficialRecordId(record);
                }

                YesNoFlag cycledB = ComponentFields.getCycledB(record);
                if (getScreenModeCode().isOosWIP()) {
                    if (!"ADJUSTMENT".equals(compTypeCode)
                        && afterImgB.booleanValue()
                        && (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)
                        || recordModeCode.isRequest() && "0".equals(officialRecordId))
                        && (covgCompCode.equals("NEWDOCTOR") || cycledB.booleanValue())
                        && !(record.hasFieldValue(ComponentFields.CHANGE_TYPE) && ComponentFields.CHG_COMP_DATE.equals(ComponentFields.getChangeType(record)))) {
                        isEditable = YesNoFlag.Y;
                    }
                }
                else {
                    if (!"ADJUSTMENT".equals(compTypeCode)
                        && StringUtils.isBlank(officialRecordId)
                        && recordModeCode.isTemp()
                        && (covgCompCode.equals("NEWDOCTOR") || cycledB.booleanValue())) {
                        isEditable = YesNoFlag.Y;
                    }
                }
            }
        }

        return isEditable;
    }

    private YesNoFlag isCompRenewEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (record.getEditIndicatorBooleanValue()) {
            YesNoFlag afterImgB = CoverageFields.getAfterImageRecordB(record);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            String officialRecordId = null;
            if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                officialRecordId = CoverageFields.getOfficialRecordId(record);
            }
            YesNoFlag expiryDateB = ComponentFields.getExpiryDateB(record);
            YesNoFlag percentB = ComponentFields.getPercentValueB(record);
            String expDate = ComponentFields.getEffectiveToDate(record);
            String termExpDate = getPolicyHeader().getTermEffectiveToDate();
            // Fix the issue 103158, the logic should be same as Policy_Folder_UI.doc 3.6.1 (Component Fields - Renew).
            if (!getScreenModeCode().isOosWIP()) {
                if ((!expiryDateB.booleanValue() || !percentB.booleanValue())
                    && termExpDate.equals(expDate)
                    && ((recordModeCode.isTemp() && afterImgB.booleanValue()) || !recordModeCode.isTemp())) {
                    isEditable = YesNoFlag.Y;
                }
            }
            else {
                if ((!expiryDateB.booleanValue() || !percentB.booleanValue())
                    && termExpDate.equals(expDate)
                    && afterImgB.booleanValue()
                    && (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)
                    || recordModeCode.isRequest() && "0".equals(officialRecordId))) {
                    isEditable = YesNoFlag.Y;
                }
            }
        }

        return isEditable;
    }

    private YesNoFlag isDataEditable(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDataEditable", new Object[]{record,});
        }

        YesNoFlag isEditable = YesNoFlag.N;
        if (record.getEditIndicatorBooleanValue()) {
            YesNoFlag afterImgB = CoverageFields.getAfterImageRecordB(record);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            String officialRecordId = null;
            if (record.hasField(CoverageFields.OFFICIAL_RECORD_ID)) {
                officialRecordId = CoverageFields.getOfficialRecordId(record);
            }

            if (getScreenModeCode().isOosWIP()) {
                if (((recordModeCode.isRequest() && "0".equals(officialRecordId)) ||
                    (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId))) && afterImgB.booleanValue()) {
                    isEditable = YesNoFlag.Y;
                }
            }
            else {
                if ((recordModeCode.isTemp() && afterImgB.booleanValue()) || recordModeCode.isOfficial()) {
                    isEditable = YesNoFlag.Y;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDataEditable", isEditable);
        }
        return isEditable;
    }

    private YesNoFlag isCompValueEditable(Record record) {
        YesNoFlag isEditable = YesNoFlag.N;

        if (record.getEditIndicatorBooleanValue()) {
            YesNoFlag afterImgB = CoverageFields.getAfterImageRecordB(record);
            RecordMode recordModeCode = PMCommonFields.getRecordModeCode(record);
            String officialRecordId = null;
            ScreenModeCode smc = getScreenModeCode();
            if (smc.isViewPolicy() || smc.isViewEndquote() || smc.isResinstateWIP()) {
                return isEditable;
            }
            else if (smc.isCancelWIP() &&
                (getOwner() == null || (getOwner() != null && !getOwner().isTailOwner()))) {
                // exclude tail component
                isEditable = ((ComponentManagerImpl) m_componentManager).isCancWipEdit(m_policyHeader);
            }
            else if (getOwner() != null && getOwner().isPriorActOwner()) {
                String lowValue = ComponentFields.getLowValue(record);
                String highValue = ComponentFields.getHighValue(record);
                if ((lowValue != null && highValue != null) && !lowValue.equals(highValue) && afterImgB.booleanValue()) {
                    isEditable = YesNoFlag.Y;
                }
            }
            else if (getOwner() != null && getOwner().isTailOwner()) {
                String lowValue = ComponentFields.getLowValue(record);
                String highValue = ComponentFields.getHighValue(record);
                String tailCovBaseId = CoverageFields.getCoverageBaseRecordId(record);
                Record ownerRecord = (Record) getOwnerRecordMap().get(tailCovBaseId);
                if (ownerRecord == null) {
                    ownerRecord = getInputRecord();
                }
                TailScreenMode tailScreenMode = TailScreenMode.getInstance(TailFields.getTailScreenMode(ownerRecord));
                if (tailScreenMode.isWIP()) {
                    if ((lowValue != null && highValue != null) && !lowValue.equals(highValue) && afterImgB.booleanValue()) {
                        isEditable = YesNoFlag.Y;
                    }
                }
                else if (tailScreenMode.isUpdate()) {
                    if (ownerRecord != null && ownerRecord.hasStringValue(TailFields.TAIL_STATUS)) {
                        String tailStatus = ownerRecord.getStringValue(TailFields.TAIL_STATUS);
                        if ((tailStatus.equals(TAIL_STATUS_OFFER) || tailStatus.equals(TAIL_STATUS_ACTIVE))
                            && (lowValue != null && highValue != null) && !lowValue.equals(highValue) && afterImgB.booleanValue()) {
                            isEditable = YesNoFlag.Y;
                        }
                    }
                }
                else if (tailScreenMode.isUpdatable() || tailScreenMode.isViewOnly()) {
                    isEditable = YesNoFlag.N;
                }
            }
            else {
                if (record.hasStringValue(CoverageFields.OFFICIAL_RECORD_ID)) {
                    officialRecordId = CoverageFields.getOfficialRecordId(record);
                }

                // Cycled or NDD components are not editable for value
                if (ComponentFields.getCycledB(record).booleanValue() ||
                    ComponentFields.getCoverageComponentCode(record).equals(ComponentFields.ComponentCodeValues.NEWDOCTOR)) {
                    isEditable = YesNoFlag.N;
                }
                else {
                    String lowValue = ComponentFields.getLowValue(record);
                    String highValue = ComponentFields.getHighValue(record);
                    String schdB = ComponentFields.getScheduledB(record);
                    String scheduledB = StringUtils.isBlank(schdB) ? "N":schdB;
                    if (smc.isOosWIP()) {
                        if ((lowValue != null && highValue != null) && !lowValue.equals(highValue)
                            && afterImgB.booleanValue()
                            && scheduledB.equals("N")
                            && (recordModeCode.isTemp() && StringUtils.isBlank(officialRecordId)
                            || recordModeCode.isRequest() && "0".equals(officialRecordId))) {
                            isEditable = YesNoFlag.Y;
                        }
                    }
                    else {
                        if (!lowValue.equals(highValue)
                            && scheduledB.equals("N")
                            && (afterImgB.booleanValue()
                            && recordModeCode.isTemp() || !recordModeCode.isTemp())) {
                            isEditable = YesNoFlag.Y;
                        }
                    }
                }
            }
        }

        return isEditable;
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;
        Date coverageEffectiveDate = DateUtils.parseDate(PriorActFields.getCoverageEffectiveDate(getInputRecord()));
        Date termEff = DateUtils.parseDate(getPolicyHeader().getTermEffectiveFromDate());
        Date termExp = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        //get owner coverage's status
        PMStatusCode coverageStatus = PMStatusCode.getInstance(getPolicyHeader().getCoverageHeader().getCurrentCoverageStatus());
        // All available fields are readonly in following scenarios:
        // 1. Owner coverage is not in current term .
        // 2. Screen mode is "VIEW_POLICY", "VIEW_ENDQUOTE", or "REINSTATEWIP"
        // 3. Screen mode is NOT "VIEW_POLICY", "VIEW_ENDQUOTE", or
        // "REINSTATEWIP", "MANUAL_ENTRY", "CANCELWIP" or "OOSWIP", and coverage status is NOT "ACTIVE" or "PENDING".
        if (coverageEffectiveDate.after(termExp) || coverageEffectiveDate.before(termEff)) {
            isReadOnly = true;
        }
        else if(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()){
            isReadOnly = true;
        }
        else if ((!(screenMode.isViewPolicy()||screenMode.isViewEndquote()||screenMode.isResinstateWIP()
            ||screenMode.isManualEntry()||screenMode.isCancelWIP()||screenMode.isOosWIP()))&&
            (!(coverageStatus.isActive()||coverageStatus.isPending()))) {
            isReadOnly = true;
        }

        return isReadOnly;
    }

    private Record getOwnerPriorActCoverage(Record compRec) {
        Date compEff = DateUtils.parseDate(ComponentFields.getEffectiveFromDate(compRec));
        Date compExp = DateUtils.parseDate(ComponentFields.getEffectiveToDate(compRec));
        String compCovgBaseId = ComponentFields.getCoverageBaseRecordId(compRec);
        Iterator it = getOwnerRecords().getRecords();
        while (it.hasNext()) {
            Record covgRec = (Record) it.next();
            String covgId = CoverageFields.getCoverageBaseRecordId(covgRec);
            Date covgEff = DateUtils.parseDate(CoverageFields.getCoverageEffectiveFromDate(covgRec));
            Date covgExp = DateUtils.parseDate(CoverageFields.getCoverageEffectiveToDate(covgRec));

            if (covgId.equals(compCovgBaseId) && ((compEff.before(covgExp) && compExp.after(covgEff)) ||
                (compEff.equals(compExp) && compEff.equals(covgEff)))) {
                return covgRec;
            }
        }

        return null;
    }

    /**
     * get owner coverage record of the component by coverageBaseRecordId.
     *
     * @param inputRecord
     * @return
     */
    private Record getCoverageRec(Record inputRecord) {
        RecordFilter recFilter = new RecordFilter(CoverageFields.COVERAGE_BASE_RECORD_ID, new Long(CoverageFields.getCoverageBaseRecordId(inputRecord)));
        RecordSet matchedCoverages = getOwnerRecords().getSubSet(recFilter);
        RecordFilter tmpFilter = new RecordFilter(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);
        RecordSet tmpCovgs = matchedCoverages.getSubSet(tmpFilter);
        Record coverageRec = null;
        if (tmpCovgs.getSize() > 0) {
            if (tmpCovgs.getSize() > 1) {
                // Retrieve editable temp coverage fields when mid change coverage record
                Iterator tmpCovgsIter = tmpCovgs.getRecords();
                while (tmpCovgsIter.hasNext()) {
                    Record covgRec = (Record) tmpCovgsIter.next();
                    if (covgRec.getEditIndicatorBooleanValue()) {
                        coverageRec = covgRec;
                        break;
                    }
                }
            }
            else {
                coverageRec = tmpCovgs.getRecord(0);
            }
        }
        else {
            if (matchedCoverages.getSize() > 0) {
                if (matchedCoverages.getSize() > 1) {
                    // Retrieve editable official coverage fields when mid change coverage record
                    Iterator matchedCovgsIter = matchedCoverages.getRecords();
                    while (matchedCovgsIter.hasNext()) {
                        Record covgRec = (Record) matchedCovgsIter.next();
                        if (covgRec.getEditIndicatorBooleanValue()) {
                            coverageRec = covgRec;
                            break;
                        }
                    }
                }
                else {
                    coverageRec = matchedCoverages.getRecord(0);
                }
            }
        }
        return coverageRec;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public ScreenModeCode getScreenModeCode() {
        return m_screenModeCode;
    }

    public void setScreenModeCode(ScreenModeCode screenModeCode) {
        m_screenModeCode = screenModeCode;
    }

    private TailScreenMode getTailScreenMode() {
        return m_tailScreenMode;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public ComponentOwner getOwner() {
        return m_owner;
    }

    public void setOwner(ComponentOwner owner) {
        m_owner = owner;
    }

    public RecordSet getOwnerRecords() {
        return m_ownerRecords;
    }

    public void setOwnerRecords(RecordSet ownerRecords) {
        m_ownerRecords = ownerRecords;
    }

    public ComponentEntitlementRecordLoadProcessor() {
    }

    private Map getCompEditIndMap() {
        return m_compEditIndMap;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private Map getOwnerRecordMap() {
        return m_ownerRecordMap;
    }

    public ComponentEntitlementRecordLoadProcessor(ComponentManager componentManager,
                                                   PolicyHeader policyHeader,
                                                   Record inputRecord,
                                                   ScreenModeCode screenModeCode,
                                                   ComponentOwner owner,
                                                   RecordSet ownerRecords) {
        setComponentManager(componentManager);
        setPolicyHeader(policyHeader);
        setScreenModeCode(screenModeCode);
        setOwner(owner);
        setOwnerRecords(ownerRecords);
        setInputRecord(inputRecord);
        m_compEditIndMap = new HashMap();
        m_ownerRecordMap = new HashMap();
        if (owner != null && owner.isTailOwner() && ownerRecords != null) {
            Iterator ownerRecordsIter = ownerRecords.getRecords();
            while (ownerRecordsIter.hasNext()) {
                Record ownerRecord = (Record) ownerRecordsIter.next();
                m_ownerRecordMap.put(TailFields.getTailCovBaseRecordId(ownerRecord), ownerRecord);
            }
        }
        else if (owner != null && owner.isCoverageOwner() && ownerRecords != null) {
            Iterator ownerRecordsIter = ownerRecords.getRecords();
            while (ownerRecordsIter.hasNext()) {
                Record ownerRecord = (Record) ownerRecordsIter.next();
                m_ownerRecordMap.put(CoverageFields.getCoverageBaseRecordId(ownerRecord), ownerRecord);
            }

        }

    }

    private TailScreenMode m_tailScreenMode;
    private ComponentOwner m_owner;
    private RecordSet m_ownerRecords;
    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;
    private ComponentManager m_componentManager;
    private ScreenModeCode m_screenModeCode;
    private Map m_compEditIndMap;
    private Map m_ownerRecordMap;
    private static final String TAIL_STATUS_OFFER = "OFFER";
    private static final String TAIL_STATUS_ACTIVE = "ACTIVE";
}

