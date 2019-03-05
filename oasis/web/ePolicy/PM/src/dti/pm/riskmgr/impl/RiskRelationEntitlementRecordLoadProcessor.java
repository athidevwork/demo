package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.busobjs.PMStatusCode;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.entitlementmgr.EntitlementFields;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for Risk Relation web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 5, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/08/2008       Joe         Handle reverse relation page entitlements
 * 12/09/2008       yhyang      #88780 Modify enableRelationshipData() to handle the new requirement.
 * 05/25/2010       syang       107771 - Modified postProcessRecord, if the screen mode is "VIEW_POLICY" or
 *                              "VIEW_ENDQUOTE", we should use the risk status of the base record.
 * 07/07/2010       dzhang      103806: Added logic for non-base company insured risk relation.
 * 08/03/2010       dzhang      103806: Fix the incorrect use of  YesNoFlag.
 * 01/18/2011       dzhang      116263 - Modified postProcessRecordSet: add new logic for get risk dates.
 * 11/29/2011       wfu         127124 - Modified postProcessRecord/enableRelationshipAndRatingData to disable rating info fields for base CI.
 * 06/25/2012       sxm         134889 - Set screen to readonly in prior term during renewal WIP
 * 11/09/2012       tcheng      138731 - Modified postProcessRecord to hide the Cancellation button on Reverse Relationship page.
 * 12/03/2012       tcheng      139330 - Modified postProcessRecord to hide the Reinstate button on Reverse Relationship page.
 * 03/27/2013       tcheng      142700 - 1) Modified postProcessRecord to make sure the field policyNoReadonly will be populated
 *                                          with value when adding CI risk relation.
 *                                       2) Modified replacePolicyNo to set policyNoReadonly field to recordSet.
 * 12/12/2014       awu         159368 - Modified postProcessRecordSet to support the count of PI/CI/NI on Reverse page.
 * 09/18/2015       lzhang      165941 - Modify postProcessRecordSet(RecordSet recordSet): add screenModeCode
 *                                                                                        field to recordSet.
 * 06/08/2016       fcb         177372 - Changed int to long
 * 12/07/2017       lzhang      182769 - Invisible ADD and SAVE button when transaction date
 *                                       is not located in risk period
 *                                       and not editable field when transaction date
 *                                       is not located in risk relation period
 * ---------------------------------------------------
 */
public class RiskRelationEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
            new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        // data are all readonly if in VIEW_POLICY, VIEW_ENDQUOTE, CANCEL_WIP  or REINSTATE_WIP mode
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        RecordMode recordMode = PMCommonFields.getRecordModeCode(record);
        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        String riskRelEffFromDateStr = RiskRelationFields.getRiskRelEffectiveFromDate(record);
        String riskRelEffToDateStr = RiskRelationFields.getRiskRelEffectiveToDate(record);
        boolean isUpdateMode = true;
        if (screenMode.isViewPolicy() || screenMode.isViewEndquote() ||
            screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            isUpdateMode = false;
            record.setEditIndicator(YesNoFlag.N);
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
            if (recordMode.isTemp()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            }
            record.setEditIndicator(YesNoFlag.Y);
        }

        // GDR71.5 Row Level Attributes
        if (isUpdateMode) {
            // #1
            if (recordMode.isOfficial()) {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            }
            else {
                record.setFieldValue("isDeleteAvailable", YesNoFlag.Y);
            }
            // #2
            Date transEffFromDate = DateUtils.parseDate(transEffFromDateStr);
            String riskEffFromDateStr = RiskFields.getRiskEffectiveFromDate(record);
            String riskEffToDateStr = RiskFields.getRiskEffectiveToDate(record);
            Date riskEffFromDate = DateUtils.parseDate(riskEffFromDateStr);
            Date riskEffToDate = DateUtils.parseDate(riskEffToDateStr);
            Date riskRelEffFromDate = DateUtils.parseDate(riskRelEffFromDateStr);
            Date riskRelEffToDate = DateUtils.parseDate(riskRelEffToDateStr);
            if (record.hasStringValue(RiskRelationFields.OFFICIAL_RECORD_ID) &&
                Long.parseLong(RiskRelationFields.getOfficialRecordId(record)) > 0 && !recordMode.isOfficial()) {
                if (record.hasStringValue(RiskFields.AFTER_IMAGE_RECORD_B) &&
                    RiskFields.getAfterImageRecordB(record).booleanValue()) {
                    enableRelationshipAndRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
                }
                else { // afterImageB is N
                    // disables the Relationship Info data and Rating Info data.
                    disableRelationshipData(record);
                    disableRatingData(record);
                }
            }
            else { // officialRecordId <= 0 or is in OFFICIAL mode
                // status is PENDING
                if (RiskRelationFields.getRiskRelationStatus(record).isPending()) {
                    enableRelationshipAndRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
                }
                // status is ACTIVE
                else if (RiskRelationFields.getRiskRelationStatus(record).isActive()) {
                    if ((screenMode.isWIP() || screenMode.isRenewWIP()) && !transEffFromDate.before(riskRelEffFromDate) &&
                        !transEffFromDate.after(riskRelEffToDate)) {
                        enableRelationshipData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
                    }
                    else {
                        // diables the Relationship Info data
                        disableRelationshipData(record);
                    }

                    if (screenMode.isOosWIP() ||
                        RiskRelationFields.isPolicyInsured(record) &&
                            !record.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) ||
                        transEffFromDate.before(riskRelEffFromDate) ||
                        transEffFromDate.after(riskRelEffToDate) ||
                        RiskRelationFields.isCompanyInsured(record) &&
                            !RiskRelationFields.getOverrideStatsB(record).booleanValue()) {
                        if (!YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue() &&
                            RiskRelationFields.isCompanyInsured(record)) {
                            enableRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
                        }
                        else {
                            // diables Rating Info data
                            disableRatingData(record);
                        }
                    }
                    else if (YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue() &&
                            RiskRelationFields.isCompanyInsured(record)){
                        // disable Rating Info data if it is base CI
                        disableRatingData(record);
                    }
                    else {
                        enableRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
                    }
                }
                // all the other status
                else {
                    // diables Relationship Info data and Rating Info data
                    disableRelationshipData(record);
                    disableRatingData(record);
                }
            }
        }
        else {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }

        // UC112.15 Retrieve Process #5
        if (RiskRelationFields.isCompanyInsured(record) && YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue()) {
            RiskRelationFields.setOldPracticeStateCode(record, RiskRelationFields.getPracticeStateCode(record));
            RiskRelationFields.setOldCountyCodeUsedToRate(record, RiskRelationFields.getCountyCodeUsedToRate(record));
            RiskRelationFields.setOldRiskClassCode(record, RiskRelationFields.getRiskClassCode(record));
        }

        // Cancellation & Reinstate
        boolean transInProgress = getPolicyHeader().isWipB();
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();
        PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
        if(screenMode.isViewPolicy() || screenMode.isViewEndquote()){
            riskStatus = getPolicyHeader().getRiskHeader().getBaseRiskStatusCode();
        }
        if (screenMode.isViewPolicy()) {
            if (riskStatus.isActive() &&
                RiskRelationFields.getRiskRelationStatus(record).isActive() &&
                !transInProgress && policyCycle.isPolicy() && !isSoloOwner()) {
                record.setFieldValue("isCancellationAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isCancellationAvailable", YesNoFlag.N);
            }
            // Fix issue 104755, the UC has been updated for this option, system should defaut this parameter to N.
            String rrelValSysPara = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_RREL_VALIDATION, "N");
            if (riskStatus.isActive() && !YesNoFlag.getInstance(rrelValSysPara).booleanValue() &&
                RiskRelationFields.getRiskRelationStatus(record).isCancelled() &&
                !transInProgress && policyCycle.isPolicy()) {
                record.setFieldValue("isReinstateAvailable", YesNoFlag.Y);
            }
            else {
                record.setFieldValue("isReinstateAvailable", YesNoFlag.N);
            }
        }
        else {
            record.setFieldValue("isCancellationAvailable", YesNoFlag.N);
            record.setFieldValue("isReinstateAvailable", YesNoFlag.N);
        }

        // always set isAnnualPremiumEditable to X after loading the risk relation data, when clicking the row first time,
        // system will send an ajax call to check this field's availability.
        record.setFieldValue("isAnnualPremiumEditable", "X");

        // If it is view reverse relation disable Delete button
        if (isReverse()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isCancellationAvailable", YesNoFlag.N);
            record.setFieldValue("isReinstateAvailable", YesNoFlag.N);
        }
        // Issue 93384
        String policyNo = "";
        if (record.hasStringValue("policyNo")) {
            policyNo = record.getStringValue("policyNo");
        }
        else if (record.hasStringValue(RiskRelationFields.CHILD_POLICY_NO)) {
            policyNo = RiskRelationFields.getChildPolicyNo(record);
        }
        record.setFieldValue("policyNoReadOnly", policyNo);

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
            (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
        }

        // 105611, the following buttons should be hidden if it is opened from view cancellation detail page.
        if (isSnapshotB()) {
            record.setFieldValue("isDeleteAvailable", YesNoFlag.N);
            record.setFieldValue("isCancellationAvailable", YesNoFlag.N);
            record.setFieldValue("isReinstateAvailable", YesNoFlag.N);
        }

        //103806 If the policy is in WIP mode, and if the insured type is INSOWNER, and non-base company insured is configured,
        //then the County, Specialty, and Premium are editable. And Temporary set the Practice State to not editable.
        if (isUpdateMode && !(YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue())) {
            if (record.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) &&
                (RiskRelationFields.getRiskProcessCode(record).equals("INSOWNER"))
                ) {
                record.setFieldValue("isAnnualPremiumEditable", YesNoFlag.Y);
            }
        }

        if (isNotDateChangeAllowedAndTransDateNotInDatesPeriod(transEffFromDateStr, riskRelEffFromDateStr, riskRelEffToDateStr)) {
            record.setEditIndicator(YesNoFlag.N);
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
            pageEntitlementFields.add("isAddPolicyInsuredAvailable");
            pageEntitlementFields.add("isAddCompanyInsuredAvailable");
            pageEntitlementFields.add("isAddNonInsuredAvailable");
            pageEntitlementFields.add("isAddNonInsuredFteAvailable");
            pageEntitlementFields.add("isDeleteAvailable");
            pageEntitlementFields.add("isDoneAvailable");
            pageEntitlementFields.add("isRiskRelationTypeEditable");
            pageEntitlementFields.add("isAnnualPremiumEditable");
            pageEntitlementFields.add("isPracticeStateCodeEditable");
            pageEntitlementFields.add("isCountyCodeEditable");
            pageEntitlementFields.add("isSpecialtyEditable");
            pageEntitlementFields.add("isNumberOfEmployedDoctorEditable");
            pageEntitlementFields.add("isNiRiskTypeCodeEditable");
            pageEntitlementFields.add("isNiRetroDateEditable");
            pageEntitlementFields.add("isNiCoverageLimitCodeEditable");
            pageEntitlementFields.add("isNiCurrentCarrierIdEditable");
            pageEntitlementFields.add("isNiPolicyNoEditable");
            pageEntitlementFields.add("isCancellationAvailable");
            pageEntitlementFields.add("isReinstateAvailable");
            pageEntitlementFields.add("isReverseRelAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }

        recordSet.setFieldValueOnAll("isDeleted", "N");
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        int rsSize = recordSet.getSize();
        for (int i = 0; i < rsSize; i++) {
            Record rec = recordSet.getRecord(i);
            // UC112.15 item 2
            if (RiskFields.getRiskEffectiveFromDate(rec).equals(RiskFields.getRiskEffectiveToDate(rec)) &&
                RiskRelationFields.getRiskRelationStatus(rec).equals("CANCEL") &&
                !StringUtils.isBlank(RiskRelationFields.getClosingTransLogId(rec)) ) {
                addDeleteMark(rec);
            }
            // UC112.15 item 3
            if ((screenMode.isOosWIP() ||
                screenMode.isCancelWIP() ||
                screenMode.isResinstateWIP() ||
                screenMode.isManualEntry()) && isClosedRecord(rec)) {
                addDeleteMark(rec);
            }
            // UC112.15 item 4.1
            if ((screenMode.isWIP() ||
                screenMode.isRenewWIP() ||
                screenMode.isViewEndquote()) && isRiskCovgClosedRecord(rec)) {
                addDeleteMark(rec);
            }
        }

        // UC112.15 item 4.2, 4.3 and 4.4
        if ((screenMode.isWIP() ||
                screenMode.isRenewWIP() ||
                screenMode.isViewEndquote())) {
            for (int i = rsSize - 1; i >= 0; i--) {
                Record rec = recordSet.getRecord(i);
                if (rec.hasStringValue(RiskRelationFields.OFFICIAL_RECORD_ID)) {
                    String officialRecordId = RiskRelationFields.getOfficialRecordId(rec);
                    if (Long.parseLong(officialRecordId) > 0 &&
                        !PMCommonFields.getRecordModeCode(rec).isOfficial()) {
                        for (int j = i - 1; j >= 0; j--) {
                            Record rec2 = recordSet.getRecord(j);
                            String riskRelationId2 = RiskRelationFields.getRiskRelationId(rec2);
                            if (riskRelationId2.equals(officialRecordId) &&
                                PMCommonFields.getRecordModeCode(rec2).isOfficial()) {
                                addDeleteMark(rec2);
                            }
                        }
                    }
                }

                if (rec.hasStringValue(RiskRelationFields.RISK_OFFICIAL_RECORD_ID)) {
                    String riskOffcialRecordId = RiskRelationFields.getRiskOfficialRecordId(rec);
                    if (Long.parseLong(riskOffcialRecordId) > 0 &&
                        !RiskRelationFields.getRiskRecordModeCode(rec).isOfficial()) {
                        for (int j = i - 1; j >= 0; j--) {
                            Record rec2 = recordSet.getRecord(j);
                            String riskId = RiskFields.getRiskId(rec2);
                            if (riskId != null && riskId.equals(riskOffcialRecordId) &&
                                RiskRelationFields.getRiskRecordModeCode(rec2).isOfficial()) {
                                addDeleteMark(rec2);
                            }
                        }
                    }
                }

                if (rec.hasStringValue(RiskRelationFields.COVERAGE_OFFICIAL_RECORD_ID)) {
                    String coverageOffcialRecordId = RiskRelationFields.getCoverageOfficialRecordId(rec);
                    if (Long.parseLong(coverageOffcialRecordId) > 0 &&
                        !RiskRelationFields.getCoverageRecordModeCode(rec).isOfficial()) {
                        for (int j = i - 1; j >= 0; j--) {
                            Record rec2 = recordSet.getRecord(j);
                            String coverageId = CoverageFields.getCoverageId(rec2);
                            if (coverageId != null && coverageId.equals(coverageOffcialRecordId) &&
                                RiskRelationFields.getCoverageRecordModeCode(rec2).isOfficial()) {
                                addDeleteMark(rec2);
                            }
                        }
                    }
                }
            }
        }

        RecordSet newRs = new RecordSet();
        Iterator iter = recordSet.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            if (!YesNoFlag.getInstance(rec.getStringValue("isDeleted")).booleanValue()) {
                newRs.addRecord(rec);
            }
        }
        Record summaryRec = recordSet.getSummaryRecord();
        List fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
        recordSet.clear();
        recordSet.addRecords(newRs);
        recordSet.addFieldNameCollection(fieldNames);
        recordSet.setSummaryRecord(summaryRec);

        // UC112.15 item 6: count the relation records per companyInsured type
        int piCount = 0, ciCount = 0, niCount = 0;

        //issue 86892
        int piActCount = 0, ciActCount = 0, niActCount = 0;
        int piCxlCount = 0, ciCxlCount = 0, niCxlCount = 0;
        long currentCountId, prevCountId = 0;
        Iterator newRsIter = recordSet.getRecords();
        while (newRsIter.hasNext()) {
            Record rec = (Record) newRsIter.next();
            if (isReverse()){
                currentCountId = Long.parseLong(RiskFields.getRiskBaseRecordId(rec));
            }else {
                currentCountId = Long.parseLong(RiskRelationFields.getRiskParentId(rec));
            }
            if (currentCountId != prevCountId)  {

                if (RiskRelationFields.isPolicyInsured(rec)) {
                    piCount++;
                    if (RiskRelationFields.getRiskRelationStatus(rec).isCancelled()) {
                        piCxlCount++;
                    }
                    else {
                         piActCount++;
                    }
                }
                else if (RiskRelationFields.isCompanyInsured(rec)) {
                    ciCount++;
                    if (RiskRelationFields.getRiskRelationStatus(rec).isCancelled()) {
                        ciCxlCount++;
                    }
                    else {
                         ciActCount++;
                    }
                }
                else if (RiskRelationFields.isNonInsured(rec)) {
                    niCount++;
                    if (RiskRelationFields.getRiskRelationStatus(rec).isCancelled()) {
                        niCxlCount++;
                    }
                    else {
                         niActCount++;
                    }
    
                }
            }

            prevCountId = currentCountId;
        }

        // set count into summary record
        RiskRelationFields.setCountOfPolicyInsured(summaryRec, String.valueOf(piCount));
        RiskRelationFields.setCountOfCompanyInsured(summaryRec, String.valueOf(ciCount));
        RiskRelationFields.setCountOfNonInsured(summaryRec, String.valueOf(niCount));

        RiskRelationFields.setCountOfActPolicyInsured(summaryRec, String.valueOf(piActCount));
        RiskRelationFields.setCountOfActCompanyInsured(summaryRec, String.valueOf(ciActCount));
        RiskRelationFields.setCountOfActNonInsured(summaryRec, String.valueOf(niActCount));

        RiskRelationFields.setCountOfCxlPolicyInsured(summaryRec, String.valueOf(piCxlCount));
        RiskRelationFields.setCountOfCxlCompanyInsured(summaryRec, String.valueOf(ciCxlCount));
        RiskRelationFields.setCountOfCxlNonInsured(summaryRec, String.valueOf(niCxlCount));

        //86892 - end

        // GDR 71.4 Option Availability A
        String transEffFromDateStr = getPolicyHeader().getLastTransactionInfo().getTransEffectiveFromDate();
        Date transEffFromDate = DateUtils.parseDate(transEffFromDateStr);
        Date riskEffFromDate = null;
        Date riskEffToDate = null;
        riskEffFromDate = DateUtils.parseDate(getPolicyHeader().getRiskHeader().getRiskEffectiveFromDate());
        String effToDate = getPolicyHeader().getRiskHeader().getRiskEffectiveToDate();
        if (DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate()).before(DateUtils.parseDate(effToDate))){
            riskEffToDate = DateUtils.parseDate(getPolicyHeader().getTermEffectiveToDate());
        }else{
            riskEffToDate = DateUtils.parseDate(effToDate);
            }
        if (screenMode.isViewPolicy()) {
            // Disable Add Policy Insured, Add Company Insured, Add Non-Insured, Add Non-Insured FTE and Delete options
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else if (screenMode.isViewEndquote() || screenMode.isCancelWIP() || screenMode.isResinstateWIP()) {
            // Disable Add Policy Insured, Add Company Insured, Add Non-Insured, Add Non-Insured FTE and Delete options
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        else if (screenMode.isManualEntry()) {
                if (isDateChangeAllowedAndTransDateNotInRiskDates(transEffFromDate, riskEffFromDate, riskEffToDate)) {
                    summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
                }
                else {
                    summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.Y);
                    summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.Y);
                    summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.Y);
                    summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.Y);
                    summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.Y);
                }
            }
        else if (screenMode.isOosWIP()){
            PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
            if ((riskStatus.isActive() || riskStatus.isPending()) && getPolicyHeader().isInitTermB() ) {
                if (isDateChangeAllowedAndTransDateNotInRiskDates(transEffFromDate, riskEffFromDate, riskEffToDate)) {
                    summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
                }
            else {
                summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.Y);
                summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.Y);
                summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.Y);
                summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.Y);
                summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.Y);
            }
        }
        else {
                summaryRec.setFieldValue("screenModeCode", ScreenModeCode.VIEW_POLICY);
                summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
                summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
            }
        }
        else {
                PMStatusCode riskStatus = getPolicyHeader().getRiskHeader().getRiskStatusCode();
                if (riskStatus.isActive() || riskStatus.isPending()) {
                    if (isDateChangeAllowedAndTransDateNotInRiskDates(transEffFromDate, riskEffFromDate, riskEffToDate)) {
                        summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
                        summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                        summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                        summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
                        summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
                    }
                    else {
                        summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.Y);
                        summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.Y);
                        summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.Y);
                        summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.Y);
                        summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.Y);
                    }
                }
                else {
                summaryRec.setFieldValue("screenModeCode", ScreenModeCode.VIEW_POLICY);
                    summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
                    summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
                }
            }

        // GDR 71.4 options availability B
        if (getNiFteCount() > 0 && YesNoFlag.Y.equals(summaryRec.getFieldValue("isAddNonInsuredFteAvailable"))) {
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.Y);
        }
        else {
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
        }

        // If it is view reverse relation disable all buttons and set the page to be readonly
        if (isReverse()) {
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isReverseRelAvailable", YesNoFlag.N);
        }
        else {
            summaryRec.setFieldValue("isReverseRelAvailable", YesNoFlag.Y);
        }

        // During OOSWIP or RENEWWIP, the buttons should be invisible if the current term is not the initial term.
        if (screenMode.isViewPolicy() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        // For issue 100502, the policy no should be replaced since this page include the PolicyHeader.
        replacePolicyNo(recordSet);
        
        // 105611, the following buttons should be hidden if it is opened from view cancellation detail page.
        if (isSnapshotB()) {
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isReverseRelAvailable", YesNoFlag.N);
        }

        String contigRiskEffectiveDateStr = getPolicyHeader().getRiskHeader().getContigRiskEffectiveDate();
        String contigRiskExpireDateStr = getPolicyHeader().getRiskHeader().getContigRiskExpireDate();
        if (isNotDateChangeAllowedAndTransDateNotInDatesPeriod(DateUtils.formatDate(transEffFromDate),
            contigRiskEffectiveDateStr, contigRiskExpireDateStr)){
            summaryRec.setFieldValue("isAddPolicyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddCompanyInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isAddNonInsuredFteAvailable", YesNoFlag.N);
            summaryRec.setFieldValue("isDoneAvailable", YesNoFlag.N);
        }
        // Set readOnly attribute to summary record
        EntitlementFields.setReadOnly(summaryRec, isRecordSetReadOnly());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Set initial engitlement Values for Risk Relation
     *
     * @param policyHeader
     * @param isCompanyInsuredStr
     * @param record
     * @return
     */
    public static void setInitialEntitlementValuesForRiskRelation(PolicyHeader policyHeader,
                                                                  String isCompanyInsuredStr,
                                                                  Record record) {
        RiskRelationEntitlementRecordLoadProcessor entitlementRLP = new RiskRelationEntitlementRecordLoadProcessor(
            policyHeader, 0, isCompanyInsuredStr, false, false, false, null, null); // the 4th parameter isSoloOwner will not be used, pass yes or false are all acceptable.
        // Set page entitlement values
        entitlementRLP.postProcessRecord(record, true);
    }

    private void addDeleteMark(Record record) {
        record.setFieldValue("isDeleted", "Y");
    }

    private boolean isClosedRecord(Record record) {
        String transLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        return record.hasStringValue(RiskRelationFields.CLOSING_TRANS_LOG_ID) &&
            RiskRelationFields.getClosingTransLogId(record).equals(transLogId) ||
            isRiskCovgClosedRecord(record);
    }

    private boolean isRiskCovgClosedRecord(Record record) {
        String transLogId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();
        return record.hasStringValue(RiskRelationFields.RISK_CLOSING_TRANS_LOG_ID) &&
            RiskRelationFields.getRiskClosingTransLogId(record).equals(transLogId) ||
            record.hasStringValue(RiskRelationFields.COVERAGE_CLOSING_TRANS_LOG_ID) &&
                RiskRelationFields.getCoverageClosingTransLogId(record).equals(transLogId);
    }

    private boolean isDateChangeAllowedAndTransDateNotInRiskDates(Date transEffFromDate,
                                                                  Date riskEffFromDate,
                                                                  Date riskEffToDate) {
        if (getPolicyHeader().getRiskHeader().getDateChangeAllowedB().booleanValue()) {
            if (transEffFromDate.before(riskEffFromDate) ||
                transEffFromDate.after(riskEffToDate) || transEffFromDate.equals(riskEffToDate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * GDR71.6 Relationship Enablement
     *
     * @param record
     * @param transEffFromDate
     * @param riskEffFromDate
     * @param riskEffToDate
     */
    private void enableRelationshipData(Record record,
                                        Date transEffFromDate,
                                        Date riskEffFromDate,
                                        Date riskEffToDate) {
        if (!isDateChangeAllowedAndTransDateNotInRiskDates(transEffFromDate, riskEffFromDate, riskEffToDate)) {
            record.setFieldValue("isToRateEditable", YesNoFlag.Y);
            // The risk relationship type is editalbe except 
            // if (companyInsured = 'NI' and addNiCoverageB = 'Y' and the record has been previously saved)
            if (RiskRelationFields.isNonInsured(record) &&
                RiskRelationFields.getAddNiCoverageB(record).booleanValue() &&
                record.hasStringValue(RiskRelationFields.RISK_RELATION_ID)) {
                record.setFieldValue("isRiskRelationTypeEditable", YesNoFlag.N);
            }
            else {
                record.setFieldValue("isRiskRelationTypeEditable", YesNoFlag.Y);
            }
        }
        else {
            disableRelationshipData(record);
        }
    }

    /**
     * GDR71.7 Rate Enablement. The item 1 will not be handled here. It will be handled in an ajax call.
     *
     * @param record
     * @param transEffFromDate
     * @param riskEffFromDate
     * @param riskEffToDate
     */
    private void enableRatingData(Record record,
                                  Date transEffFromDate,
                                  Date riskEffFromDate,
                                  Date riskEffToDate) {
        if (!isDateChangeAllowedAndTransDateNotInRiskDates(transEffFromDate, riskEffFromDate, riskEffToDate)) {
            // #2
            record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.Y);
            record.setFieldValue("isCountyCodeEditable", YesNoFlag.Y);
            record.setFieldValue("isSpecialtyEditable", YesNoFlag.Y);
            record.setFieldValue("isNumberOfEmployedDoctorEditable", YesNoFlag.Y);

            // #3
            if (RiskRelationFields.getAddNiCoverageB(record).booleanValue()) {
                if (RiskRelationFields.isNonInsured(record)) {
                    if (!PMCommonFields.getRecordModeCode(record).isOfficial() &&
                        !record.hasStringValue(RiskRelationFields.RISK_RELATION_ID)) {
                        record.setFieldValue("isNiRiskTypeCodeEditable", YesNoFlag.Y);
                        record.setFieldValue("isNiRetroDateEditable", YesNoFlag.Y);
                    }
                    else {
                        record.setFieldValue("isNiRiskTypeCodeEditable", YesNoFlag.N);
                        record.setFieldValue("isNiRetroDateEditable", YesNoFlag.N);
                    }
                    record.setFieldValue("isNiCoverageLimitCodeEditable", YesNoFlag.Y);
                    record.setFieldValue("isNiCurrentCarrierIdEditable", YesNoFlag.Y);
                    record.setFieldValue("isNiPolicyNoEditable", YesNoFlag.Y);
                }
                else {
                    record.setFieldValue("isNiRiskTypeCodeEditable", YesNoFlag.N);
                    record.setFieldValue("isNiRetroDateEditable", YesNoFlag.N);
                    record.setFieldValue("isNiCoverageLimitCodeEditable", YesNoFlag.N);
                    record.setFieldValue("isNiCurrentCarrierIdEditable", YesNoFlag.N);
                    record.setFieldValue("isNiPolicyNoEditable", YesNoFlag.N);
                }
            }
            else {
                record.setFieldValue("isNiRiskTypeCodeEditable", YesNoFlag.N);
                record.setFieldValue("isNiRetroDateEditable", YesNoFlag.N);
                record.setFieldValue("isNiCoverageLimitCodeEditable", YesNoFlag.N);
                record.setFieldValue("isNiCurrentCarrierIdEditable", YesNoFlag.N);
                record.setFieldValue("isNiPolicyNoEditable", YesNoFlag.N);
            }
        }
        else {
            disableRatingData(record);
        }
    }

    private void enableRelationshipAndRatingData(Record record,
                                                 Date transEffFromDate,
                                                 Date riskEffFromDate,
                                                 Date riskEffToDate) {
        // GDR71.6 Relationship Enablement
        enableRelationshipData(record, transEffFromDate, riskEffFromDate, riskEffToDate);

        if (RiskRelationFields.isPolicyInsured(record) &&
            !record.hasStringValue(RiskRelationFields.RISK_PROCESS_CODE) ||
            RiskRelationFields.isCompanyInsured(record) &&
                !RiskRelationFields.getOverrideStatsB(record).booleanValue()) {
            if (!YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue() &&
                RiskRelationFields.isCompanyInsured(record)) {
                // GDR71.7 Rate Enablement: #1 will not be checked here, it will be checked in an ajax call when the row is first selected.
                enableRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
            }
            else {
                // disable the Rating Info data
                disableRatingData(record);
            }
        }
        else if (YesNoFlag.getInstance(getCompanyInsuredStr()).booleanValue() &&
                RiskRelationFields.isCompanyInsured(record)) {
            // disable the Rating Info data if it is base CI
            disableRatingData(record);
        } 
        else {
            enableRatingData(record, transEffFromDate, riskEffFromDate, riskEffToDate);
        }
    }

    private void disableRelationshipData(Record record) {
        record.setFieldValue("isToRateEditable", YesNoFlag.N);
        record.setFieldValue("isRiskRelationTypeEditable", YesNoFlag.N);
    }

    private void disableRatingData(Record record) {
        //record.setFieldValue("isAnnualPremiumEditable", YesNoFlag.N);
        record.setFieldValue("isPracticeStateCodeEditable", YesNoFlag.N);
        record.setFieldValue("isCountyCodeEditable", YesNoFlag.N);
        record.setFieldValue("isSpecialtyEditable", YesNoFlag.N);
        record.setFieldValue("isNumberOfEmployedDoctorEditable", YesNoFlag.N);

        record.setFieldValue("isNiRiskTypeCodeEditable", YesNoFlag.N);
        record.setFieldValue("isNiRetroDateEditable", YesNoFlag.N);
        record.setFieldValue("isNiCoverageLimitCodeEditable", YesNoFlag.N);
        record.setFieldValue("isNiCurrentCarrierIdEditable", YesNoFlag.N);
        record.setFieldValue("isNiPolicyNoEditable", YesNoFlag.N);
    }

    /**
     * Check if current recordSet is readOnly
     *
     * @return boolean
     */
    private boolean isRecordSetReadOnly() {
        boolean isReadOnly = false;

        // All available fields are readonly if it's in reverse mode
        if (isReverse()) {
            isReadOnly = true;
        }
         // During OOSWIP or RENEWWIP, this page should be read only if the current time is not the initial term.
        ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
        if (screenMode.isViewPolicy() ||
           (screenMode.isOosWIP() || screenMode.isRenewWIP()) && !getPolicyHeader().isInitTermB()) {
            isReadOnly = true;
        }

        // 105611, the page should be readonly if it is opened from view cancellation detail page.
        if(isSnapshotB()){
           isReadOnly = true;
        }

        return isReadOnly;
    }

    /**
     * This page include the PolicyHeader, in order to avoid duplicated Policy No,
     * we should replace the Policy No in recordset.
     *
     * @param recordSet
     */
    private void replacePolicyNo(RecordSet recordSet) {
        if (recordSet.getSize() > 0) {
            Iterator iter = recordSet.getRecords();
            while (iter.hasNext()) {
                Record record = (Record) iter.next();
                String policyNo = "";
                if (record.hasStringValue("policyNo")) {
                    policyNo = record.getStringValue("policyNo");
                }
                record.remove("policyNo");
                record.setFieldValue(RiskRelationFields.CHILD_POLICY_NO, policyNo);
            }
        }
        else {
           // Replace the field name.
           List childName = new ArrayList();
           childName.add(RiskRelationFields.CHILD_POLICY_NO);
           childName.add(RiskRelationFields.POLICY_NO_READONLY);
           recordSet.addFieldNameCollection(childName);
           recordSet.removeFieldName("policyNo");
        }
    }

    /**
     * Check if the transaction date is located in risk period
     *
     * @return YesNoFlag
     */
    public boolean isNotDateChangeAllowedAndTransDateNotInDatesPeriod(String transEffFromDate, String effFromDate, String effToDate) {
        Logger l = LogUtils.getLogger(DateUtils.class);
        if (l.isLoggable(Level.FINER)) {
            l.entering(DateUtils.class.getName(), "isNotDateChangeAllowedAndTransDateNotInDatesPeriod");
        }

        boolean retval = false;
        ScreenModeCode screenModeCode = getPolicyHeader().getScreenModeCode();
        if (!getPolicyHeader().getRiskHeader().getDateChangeAllowedB().booleanValue()){
            if ((screenModeCode.isManualEntry() || screenModeCode.isWIP() || screenModeCode.isRenewWIP() || screenModeCode.isOosWIP())
                && DateUtils.isTargetDateNotInDatesPeriod(transEffFromDate, effFromDate, effToDate)){
                retval = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(DateUtils.class.getName(), "isNotDateChangeAllowedAndTransDateNotInDatesPeriod", retval);
        }
        return retval;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
    }

    public RiskRelationEntitlementRecordLoadProcessor(PolicyHeader policyHeader,
                                                      int niFteCount,
                                                      String isCompanyInsuredStr,
                                                      boolean isSoloOwner,
                                                      boolean reverse,
                                                      boolean snapshotB,
                                                      String riskEffectiveFromDateStr,
                                                      String riskEffectiveToDateStr) {
        setPolicyHeader(policyHeader);
        setNiFteCount(niFteCount);
        setCompanyInsuredStr(isCompanyInsuredStr);
        setSoloOwner(isSoloOwner);
        setReverse(reverse);
        setSnapshotB(snapshotB);
        setRiskEffectiveFromDate(riskEffectiveFromDateStr);
        setRiskEffectiveToDate(riskEffectiveToDateStr);
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public int getNiFteCount() {
        return m_niFteCount;
    }

    public void setNiFteCount(int niFteCount) {
        m_niFteCount = niFteCount;
    }

    public String getCompanyInsuredStr() {
        return m_isCompanyInsuredStr;
    }

    public void setCompanyInsuredStr(String companyInsuredStr) {
        m_isCompanyInsuredStr = companyInsuredStr;
    }

    public boolean isSoloOwner() {
        return m_isSoloOwner;
    }

    public void setSoloOwner(boolean soloOwner) {
        m_isSoloOwner = soloOwner;
    }

    public boolean isReverse() {
        return m_reverse;
    }

    public void setReverse(boolean reverse) {
        m_reverse = reverse;
    }

    public boolean isSnapshotB() {
        return m_snapshotB;
    }

    public void setSnapshotB(boolean snapshotB) {
        m_snapshotB = snapshotB;
    }

    public String getRiskEffectiveFromDate() {
        return m_riskEffectiveFromDate;
    }

    public void setRiskEffectiveFromDate( String riskEffectiveFromDate) {
        m_riskEffectiveFromDate = riskEffectiveFromDate;
    }

    public String getRiskEffectiveToDate() {
        return m_riskEffectiveToDate;
    }

    public void setRiskEffectiveToDate( String riskEffectiveToDate) {
        m_riskEffectiveToDate = riskEffectiveToDate;
    }

    private PolicyHeader m_policyHeader;
    private int m_niFteCount;
    private String m_isCompanyInsuredStr;
    private boolean m_isSoloOwner;
    private boolean m_reverse;
    private boolean m_snapshotB;
    private String m_riskEffectiveFromDate;
    private String m_riskEffectiveToDate;
}
