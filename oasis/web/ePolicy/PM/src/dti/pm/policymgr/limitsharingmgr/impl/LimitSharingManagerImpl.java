package dti.pm.policymgr.limitsharingmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.SysParmIds;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.pm.core.data.FilterOfficialRowForEndquoteRecordLoadProcessor;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.limitsharingmgr.LimitSharingManager;
import dti.pm.policymgr.limitsharingmgr.SharedGroupFields;
import dti.pm.policymgr.limitsharingmgr.SharedDetailFields;
import dti.pm.policymgr.limitsharingmgr.dao.LimitSharingDAO;
import dti.pm.transactionmgr.TransactionFields;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of LimitSharingManager Interface.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 12, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/02/2008       fcb         79215: loadAllAvailableSharedDetail: added extra neccessary information.
 * 08/17/2010       fcb         97217  - validateSharedLimitGroup call added.
 * 09/23/2010       syang       Issue 110819 - Added FilterOfficialRowForEndquoteRecordLoadProcessor to
 *                                             loadAllSharedGroup() and loadAllSharedDetail().
 * 01/12/2011       dzhang     113568 - Added loadAllSharedLimit().
 * 07/28/2011       fcb         123503: fixed some issues with validations.
 * 08/01/2011       gxc         123591 - do not validate shared limit ind for flat records
 * 08/12/2011       ryzhao      123945 - Modified validateShareGroupSir().
 *                                       It only deal with the case the return message equals 'N', this is incorrect.
 *                                       Other return message should also be handled if no SIR components
 *                                       or multiple SIR components exist for risk/coverage.
 * 10/11/2011       gxc         125995 - Modified validateSharedLimitGroup to pass additional parameters to the procedure
 *                                       and display the returned message from backend.
 * 11/09/2011       xnie        125517 - Modified loadAllAvailableSharedDetail to check select and owner check box.
 * 08/24/2012       adeng       135972 - Call the new constructor of SharedGroupEntitlementRecordLoadProcessor which created for passing in
 *                                       hasRisk to handel the buttons of share detail section.
 * 08/31/2012       adeng       135972 - Roll backed changes,using new solution of hiding Limit Sharing button.
 * 12/05/2012       awu         139503 - Modified validateShareGroupDeduct(). Added validation for the case that there is
 *                                       only one limit shared group detail but overlapped.
 * 06/05/2013       adeng       144779 - 1)Modified validateShareGroupSir() to only validate records that overlap with the
 *                                       transaction effective date and closing trans log id is null.
 *                                       2)Modified validateShareGroupDeduct() to add overlap condition if closing trans log id is null.
 * 07/05/2013       adeng       145576 - Modified loadAllAvailableSharedDetail() to set ShareDtlOwnerB to "0"/"-1" by
 *                                       value of getShareOwnerB(). It's incorrect to set it to "Y" before, because the
 *                                       component of this column are checkboxes, any other character strings will be
 *                                       treated as "-1".
 * 10/15/2013       adeng       145622 - Modified getInitialValuesForSharedDetail(), if risk's effective to date equals
 *                                       to open date, set share detail's effective to date as share group's effective
 *                                       to date, otherwise keep to set it as the risk's effective to date.
 * 06/16/2015       wdang       163775 - Modified validateShareGroupDeduct and validateShareGroupSir to pass more params.
 * ---------------------------------------------------
 */

public class LimitSharingManagerImpl implements LimitSharingManager {

    /**
     * load all shared group info by policy info
     *
     * @param policyHeader
     * @return RecordSet a recordSet of shared group
     */

    public RecordSet loadAllSharedGroup(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSharedGroup", new Object[]{policyHeader});
        }
        Record inputRecord = policyHeader.toRecord();

        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, getLimitSharingRecordModeCode(policyHeader));

        // Issue 110819, filter official record for end quote.
        RecordLoadProcessor sharedGroupLoadProcessor = new SharedGroupEntitlementRecordLoadProcessor(policyHeader);
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor =
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "policySharedGroupMasterId", "shareGroupRecordModeCode" , "shareGroupOffRecordId");
        RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(sharedGroupLoadProcessor, endquoteLoadProcessor);
        RecordSet rs;
        rs = getLimitSharingDAO().loadAllSharedGroup(inputRecord, loadProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSharedGroup", rs);
        }
        return rs;
    }

    /**
     * load all separate limit info by policy infomation
     *
     * @param policyHeader
     * @return RecordSet   a record set loaded with list of separate limit
     */
    public RecordSet loadAllSeparateLimit(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSeparateLimit", new Object[]{policyHeader});
        }
        // Build the input record
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, getLimitSharingRecordModeCode(policyHeader));
        RecordSet rs;
        rs = getLimitSharingDAO().loadAllSeparateLimit(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSeparateLimit", rs);
        }
        return rs;
    }

    /**
     * load all shared detail info
     *
     * @param policyHeader
     * @return RecordSet a recordSet of shared detail
     */
    public RecordSet loadAllSharedDetail(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSharedDetail", new Object[]{policyHeader});
        }
        // Issue 110819, filter official record for end quote.
        RecordLoadProcessor sharedDetailLoadProcessor = new SharedDetailEntitlementRecordLoadProcessor(policyHeader);
        FilterOfficialRowForEndquoteRecordLoadProcessor endquoteLoadProcessor =
            new FilterOfficialRowForEndquoteRecordLoadProcessor(policyHeader, "policyShareGroupDtlId", "shareDtlRecordModeCode" , "shareDtlOffRecordId");
        RecordLoadProcessor loadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(sharedDetailLoadProcessor, endquoteLoadProcessor);

        // Build the input record
        Record inputRecord = policyHeader.toRecord();
        inputRecord.setFieldValue(PMCommonFields.RECORD_MODE_CODE, getLimitSharingRecordModeCode(policyHeader));
        RecordSet rs;
        rs = getLimitSharingDAO().loadAllSharedGroupDetail(inputRecord, loadProcessor);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSharedDetail", rs);
        }
        return rs;
    }

    /**
     * load all available shared detail
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAvailableSharedDetail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableSharedDetail", new Object[]{policyHeader, inputRecord});
        }
        // Build the input record
        Record input = new Record();
        SharedGroupFields.setShareLimitB(input, SharedGroupFields.getShareLimitB(inputRecord));
        SharedGroupFields.setPolicyId(input, policyHeader.getPolicyId());
        TransactionFields.setTransactionEff(input, policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        SharedDetailFields.setExpDate(input, policyHeader.getTermEffectiveToDate());
        SharedDetailFields.setTransLogId(input, policyHeader.getLastTransactionId());
        SharedDetailFields.setEffDate(input, policyHeader.getTermEffectiveFromDate());
        SharedGroupFields.setShareMasterId(input, SharedGroupFields.getPolicySharedGroupMasterId(inputRecord));
        SharedGroupFields.setShareDeductB(input, SharedGroupFields.getShareDeductB(inputRecord));
        SharedGroupFields.setShareSirB(input, SharedGroupFields.getShareSirB(inputRecord));

        RecordLoadProcessor selectIndProcessor = AddSelectIndLoadProcessor.getInstance();
        RecordLoadProcessor recordLoadProcessor = RecordLoadProcessorChainManager.getRecordLoadProcessor(
            selectIndProcessor, new SharedDetailRecordLoadProcessor());
        RecordSet rs;
        rs = getLimitSharingDAO().loadAllAvailableSharedDetail(input, recordLoadProcessor);

        // If records are already shared Details of a group which group type is same with current group,
        // the select check box needs to be checked.
        // If record is already shared Owner of a current group, the owner check box needs to be checked.
        RecordSet outputRecordSet = new RecordSet();
        Iterator it = rs.getRecords();
        YesNoFlag isGroupSharedLimit = YesNoFlag.getInstance(SharedGroupFields.getShareLimitB(inputRecord));
        YesNoFlag isGroupSharedDeduct = YesNoFlag.getInstance(SharedGroupFields.getShareDeductB(inputRecord));
        YesNoFlag isGroupSharedSir = YesNoFlag.getInstance(SharedGroupFields.getShareSirB(inputRecord));

        while (it.hasNext()) {
            Record outputRecord = (Record) it.next();
            YesNoFlag isCovgSharedLimit = SharedDetailFields.getShareLimitB(outputRecord);
            YesNoFlag isCovgSharedDeduct = SharedDetailFields.getShareDeductB(outputRecord);
            YesNoFlag isCovgSharedSir = SharedDetailFields.getShareSirB(outputRecord);
            YesNoFlag isCovgSharedOwner = SharedDetailFields.getShareOwnerB(outputRecord);

            if ((isGroupSharedLimit.booleanValue() && isCovgSharedLimit.booleanValue())
                ||(isGroupSharedDeduct.booleanValue() && isCovgSharedDeduct.booleanValue())
                ||(isGroupSharedSir.booleanValue() && isCovgSharedSir.booleanValue())) {
                SharedDetailFields.setSelectId(outputRecord, YesNoFlag.Y.toString());
            }

            if (isCovgSharedOwner.booleanValue()) {
                SharedDetailFields.setShareDtlOwnerB(outputRecord, "-1");
            }
            else {
                SharedDetailFields.setShareDtlOwnerB(outputRecord, "0");
            }

            outputRecordSet.addRecord(outputRecord);
        }

        Record sumRec = rs.getSummaryRecord();
        List fieldNames = (List) ((ArrayList) rs.getFieldNameList()).clone();
        // Replace the old record set by newly changed record set.
        rs.clear();
        rs.addRecords(outputRecordSet);
        rs.addFieldNameCollection(fieldNames);
        rs.setSummaryRecord(sumRec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableSharedDetail", rs);
        }

        return rs;
    }

    /**
     * save all share group data and share detail data
     *
     * @param policyHeader
     * @param sharedGroupRecords
     * @param sharedDetailRecords
     * @return updateCount
     */
    public int saveAllLimitSharing(PolicyHeader policyHeader, RecordSet sharedGroupRecords, RecordSet sharedDetailRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllLimitSharing", new Object[]{sharedGroupRecords, sharedDetailRecords});
        //get recordSet need to be validate(exclude the deleted record)
        RecordSet validateSharedGroupRecords = sharedGroupRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        RecordSet validateSharedDetailRecords = sharedDetailRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
        //do validation
        validateAllLimitSharing(policyHeader, validateSharedGroupRecords, validateSharedDetailRecords);
        //do update and save and delelte
        int updateCount = 0;
        RecordSet deletedSharedDetailRecords = sharedDetailRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED}));
        updateCount += getLimitSharingDAO().deleteAllSharedDetail(deletedSharedDetailRecords);
        //delete shared group
        RecordSet deletedSharedGroupRecords = sharedGroupRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.DELETED}));
        updateCount += getLimitSharingDAO().deleteAllSharedGroup(deletedSharedGroupRecords);
        // saving new/modified shared detail
        RecordSet updatedSharedDetailRecords = sharedDetailRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet modifedSharedDetailRecords = updatedSharedDetailRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        modifedSharedDetailRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
        modifedSharedDetailRecords.setFieldValueOnAll("shareDtlTransLogId", policyHeader.getLastTransactionId());
        modifedSharedDetailRecords.setFieldValueOnAll("shareDtlEffFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        RecordSet newSharedDetailRecords = updatedSharedDetailRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        newSharedDetailRecords.setFieldValueOnAll("rowStatus", "NEW");
        modifedSharedDetailRecords.addRecords(newSharedDetailRecords);
        String termExpDate = policyHeader.getTermEffectiveToDate();
        modifedSharedDetailRecords.setFieldValueOnAll("termExpirationDate",termExpDate);

        Iterator it1 = modifedSharedDetailRecords.getRecords();
        while (it1.hasNext()) {
            Record sharedGroupDetail = (Record) it1.next();
            String groupId = SharedDetailFields.getShareDtlGroupMasterId(sharedGroupDetail);
            Iterator it2 = sharedGroupRecords.getRecords();
            while (it2.hasNext()) {
                Record sharedGroup = (Record) it2.next();
                String shareGroupId = SharedGroupFields.getPolicySharedGroupMasterId(sharedGroup);
                if (shareGroupId.equals(groupId)) {
                    String groupExp = SharedGroupFields.getShareGroupEffToDate(sharedGroup);
                    String groupRenewB = SharedGroupFields.getRenewalB(sharedGroup);
                    if (DateUtils.parseDate(groupExp).equals(DateUtils.parseDate(termExpDate)) && groupRenewB.equalsIgnoreCase("Y")) {
                        sharedGroupDetail.setFieldValue("groupExpirationDate", "01/01/3000");
                    }
                    else {
                        sharedGroupDetail.setFieldValue("groupExpirationDate",groupExp);
                    }
                    break;
                }

            }
        }

        updateCount += getLimitSharingDAO().saveAllSharedDetail(modifedSharedDetailRecords);
        // saving new/modified shared group
        RecordSet updatedSharedGroupRecords = sharedGroupRecords.getSubSet(new UpdateIndicatorRecordFilter(
            new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        RecordSet modifedSharedGroupRecords = updatedSharedGroupRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        modifedSharedGroupRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
        RecordSet newSharedGroupRecords = updatedSharedGroupRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        newSharedGroupRecords.setFieldValueOnAll("rowStatus", "NEW");
        modifedSharedGroupRecords.addRecords(newSharedGroupRecords);
        modifedSharedGroupRecords.setFieldValueOnAll("shareGroupTransLogId", policyHeader.getLastTransactionId());
        modifedSharedGroupRecords.setFieldValueOnAll("shareGroupEffFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        modifedSharedGroupRecords.setFieldValueOnAll("termExpirationDate",termExpDate);
        updateCount += getLimitSharingDAO().saveAllSharedGroup(modifedSharedGroupRecords);
        l.exiting(getClass().getName(), "saveAllLimitSharing", new Long(updateCount));
        return updateCount;
    }

    /**
     * Get inital values for shared group
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSharedGroup(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSharedGroup", new Object[]{policyHeader, inputRecord});
        }
        //get default record from workbench
        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_LIMIT_SHARING_ACTION_CLASS_NAME);
        SharedGroupFields.setPolicySharedGroupMasterId(output,getDbUtilityManager().getNextSequenceNo().toString());
        SharedGroupFields.setPolicyId(output,policyHeader.getPolicyId());
        SharedGroupFields.setShareGroupEffFromDate(output,policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        SharedGroupFields.setShareGroupEffToDate(output,policyHeader.getTermEffectiveToDate());
        SharedGroupFields.setShareGroupTransLogId(output,policyHeader.getLastTransactionId());
        SharedGroupFields.setShareGroupAcctFromDate(output,policyHeader.getLastTransactionInfo().getTransAccountingDate());
        SharedGroupFields.setShareGroupNo(output,(String)inputRecord.getFieldValue("shareGroupNo"));
        SharedGroupFields.setShareGroupPolicyTypeCode(output,policyHeader.getPolicyTypeCode());
        SharedGroupFields.setShareGroupRecordModeCode(output,"TEMP");
        new SharedGroupEntitlementRecordLoadProcessor(policyHeader).setInitialEntitlementValuesForSharedGroup(output);
        l.exiting(getClass().getName(), "getInitialValuesForSharedGroup");
        return output;
    }

    /**
     * Get inital values for shared detail
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForSharedDetail(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSharedDetail", new Object[]{policyHeader, inputRecord});
        }
        Record output = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_LIMIT_SHARING_ACTION_CLASS_NAME);
        //set all fields got from page
        output.setFields(inputRecord);
        //set all fields got from server
        SharedDetailFields.setPolicyShareGroupDtlId(output,getDbUtilityManager().getNextSequenceNo().toString());
        SharedDetailFields.setShareDtlEffFromDate(output,policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        String sExpDate = inputRecord.getStringValue("shareDtlRiskEffectiveToDate");
        if (OPEN_DATE.equals(sExpDate)) {
            SharedDetailFields.setShareDtlEffToDate(output, inputRecord.getStringValue("shareGroupEffToDate"));
        }
        else {
            SharedDetailFields.setShareDtlEffToDate(output, inputRecord.getStringValue("shareDtlRiskEffectiveToDate"));
        }
        SharedDetailFields.setShareDtlRenewalB(output, inputRecord.getStringValue("renewalB"));
        SharedDetailFields.setShareDtlTransLogId(output,policyHeader.getLastTransactionId());
        SharedDetailFields.setShareDtlAcctFromDate(output,policyHeader.getLastTransactionInfo().getTransAccountingDate());
        String shareDeductLevel = SysParmProvider.getInstance().getSysParm(SysParmIds.PM_SHARE_DEDUCT_LEV);
        if ("RISK".equals(shareDeductLevel)) {
            SharedDetailFields.setShareDtlSourceRecordId(output,inputRecord.getStringValue("shareDtlRiskBaseRecordId"));
            SharedDetailFields.setShareDtlSourceTableName(output,"RISK");
        }
        else {
            SharedDetailFields.setShareDtlSourceRecordId(output,inputRecord.getStringValue("shareDtlCoverageBaseRecordId"));
            SharedDetailFields.setShareDtlSourceTableName(output,"COVERAGE");
        }
        SharedDetailFields.setShareDtlRecordModeCode (output,"TEMP");
        new SharedDetailEntitlementRecordLoadProcessor(policyHeader).setInitialEntitlementValuesForSharedDetail(output);
        l.exiting(getClass().getName(), "getInitialValuesForSharedDetail");
        return output;
    }

    /**
     * validate sir is visible or not.
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean validateSirVisibility(PolicyHeader policyHeader) {
        Logger l = LogUtils.enterLog(getClass(), "validateSirVisibility", new Object[]{policyHeader});
        boolean result = false;
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
        inputRecord.setFieldValue("transEffFromDate", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
        if ((getLimitSharingDAO().validateSirVisibility(inputRecord)).equals("Y")) {
            result = true;
        }
        l.exiting(getClass().getName(), "validateSirVisibility");
        return result;
    }

    /**
     * Validate all limit sharing
     *
     * @param policyHeader
     * @param inputSharedGroupRecords
     * @param inputSharedDetailRecords
     */
    protected void validateAllLimitSharing(PolicyHeader policyHeader, RecordSet inputSharedGroupRecords, RecordSet inputSharedDetailRecords) {
        Logger l = LogUtils.enterLog(getClass(), "validateAllLimitSharing", new Object[]{policyHeader, inputSharedGroupRecords, inputSharedDetailRecords});
        Iterator it1 = inputSharedGroupRecords.getRecords();
        //sir visibility
        boolean visibleFlag = validateSirVisibility(policyHeader);
        //do validation per share group
        while (it1.hasNext()) {
            Record sharedGroup = (Record) it1.next();
            String rowNum = String.valueOf(sharedGroup.getRecordNumber() + 1);
            String rowId = SharedGroupFields.getPolicySharedGroupMasterId(sharedGroup);
            Date sharedGroupEffDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffFromDate(sharedGroup));
            Date sharedGroupExpDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffToDate(sharedGroup));
            String shareLimitB = SharedGroupFields.getShareLimitB(sharedGroup);
            String shareDeductB =  SharedGroupFields.getShareDeductB(sharedGroup);
            String shareSirB = "";
            if (visibleFlag) {
                shareSirB = SharedGroupFields.getShareSirB(sharedGroup);
            }
            String shareGroupNo = SharedGroupFields.getShareGroupNo(sharedGroup);
            if (sharedGroupExpDate.before(sharedGroupEffDate)) {
                // Set back to orignial value
                MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.invalidSharedGroupEffectiveToDate.error",
                    new String[]{rowNum}, "shareGroupEffToDate", rowId);
            }
            if (!visibleFlag) {
                if (("N".equals(shareLimitB)) && ("N".equals(shareDeductB))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.shareOptionNotSelected.error",
                        new String[]{rowNum, shareGroupNo, ""}, "shareLimitB", rowId);
                }
            }
            else {
                if (("N".equals(shareLimitB)) && ("N".equals(shareDeductB)) && ("N".equals(shareSirB))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.shareOptionNotSelected.error",
                        new String[]{rowNum, shareGroupNo, " ,Share SIR"}, "shareLimitB", rowId);
                }
            }
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            //get related shared detail recordset per group
            RecordSet relatedSharedDetailRecords = getRelatedSharedDetailRecords(inputSharedDetailRecords, sharedGroup);

            // Begin add Share Type field to detail record for validate overlapping risk/coverage in two groups with the same share type.
            if (shareLimitB.equals("Y")) {
                relatedSharedDetailRecords.setFieldValueOnAll("limitType", "Y");
            }
            if (shareDeductB.equals("Y")) {
                relatedSharedDetailRecords.setFieldValueOnAll("deductType", "Y");
            }
            if (shareSirB.equals("Y")) {
                relatedSharedDetailRecords.setFieldValueOnAll("sirType", "Y");
            }
            // End add Share Type
            Iterator it2 = relatedSharedDetailRecords.getRecords();
            while (it2.hasNext()) {
                Record sharedDetail = (Record) it2.next();
                Date sharedDetailEffDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(sharedDetail));
                Date sharedDetailExpDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(sharedDetail));
                String detailRowNo = String.valueOf(sharedDetail.getRecordNumber() + 1);
                String detailRowId = rowId + "," + SharedDetailFields.getPolicyShareGroupDtlId(sharedDetail);

                //share effective  date should not be after expiration date
                if (sharedDetailExpDate.before(sharedDetailEffDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.invalidSharedDtlEffectiveToDate.error",
                        new String[]{detailRowNo}, "shareDtlEffToDate", detailRowId);
                    throw new ValidationException("Invalid Limit Sharing data.");
                }
                //share detail expiration date should not after share group expiration date
                if (sharedDetailExpDate.after(sharedGroupExpDate)) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.shareDtlExpDateAfterGroupDate.error",
                        new String[]{detailRowNo}, "shareDtlEffToDate", detailRowId);
                    throw new ValidationException("Invalid Limit Sharing data.");
                }

            }
            if (!validateOverLapSourceInSameGroup(relatedSharedDetailRecords)) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if (!validateOwnerPerShareGroup(relatedSharedDetailRecords)) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if (!validateShareGroupDeduct(policyHeader, relatedSharedDetailRecords)) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if (!validateShareGroupLimits(relatedSharedDetailRecords)) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if ((visibleFlag)&&(!validateShareGroupSir(policyHeader, relatedSharedDetailRecords))) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if ((visibleFlag)&&(!validateShareGroupRenewB(relatedSharedDetailRecords))) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }
            if (!validateSharedLimitGroup(policyHeader, sharedGroup, relatedSharedDetailRecords)) {
                throw new ValidationException("Invalid Limit Sharing data.");
            }

        }
        //do validation in whole shared detail record set(include all group)
        if (!validateOverLapSourceInTwoGroup(inputSharedDetailRecords)) {
            throw new ValidationException("Invalid Limit Sharing data.");
        }
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Limit Sharing data.");
        }
        l.exiting(getClass().getName(), "validateAllLimitSharing");

    }

    /**
     * get all shared detail record related to one shared group
     * @param inputSharedDetailRecords
     * @param sharedGroup
     * @return RecordSet
     */
    private RecordSet getRelatedSharedDetailRecords(RecordSet inputSharedDetailRecords, Record sharedGroup) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelatedSharedDetailRecords", new Object[]{inputSharedDetailRecords, sharedGroup});
        }

        String policySharedGroupMasterId =SharedGroupFields.getPolicySharedGroupMasterId(sharedGroup);
        Iterator it = inputSharedDetailRecords.getRecords();
        RecordSet rs = new RecordSet();
        while (it.hasNext()) {
            Record r = (Record) it.next();
            if (SharedDetailFields.getShareDtlGroupMasterId(r).equals(policySharedGroupMasterId)) {
                rs.addRecord(r);
            }
        }
        rs.setSummaryRecord(sharedGroup);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelatedSharedDetailRecords", rs);
        }
        return rs;
    }

    /**
     * validate overlapping time in one shared group for same risk/coverage
     * @param inputRecordSet
     * @return boolean
     */
    private boolean validateOverLapSourceInSameGroup(RecordSet inputRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOverLapSourceInSameGroup", new Object[]{inputRecordSet,});
        }

        boolean result = true;
        int length = inputRecordSet.getSize();
        Record shareGroupRecord = inputRecordSet.getSummaryRecord();
        String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
        String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
        String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
        for (int i = 0; i < length; i++) {
            Record detailRecord1 = inputRecordSet.getRecord(i);
            for (int j = i + 1; j < length; j++) {
                Record detailRecord2 = inputRecordSet.getRecord(j);

                String shareDtlSourceRecordId1 = SharedDetailFields.getShareDtlSourceRecordId (detailRecord1);
                String shareDtlSourceRecordId2 = SharedDetailFields.getShareDtlSourceRecordId (detailRecord2);
                if (shareDtlSourceRecordId1.equals(shareDtlSourceRecordId2)) {
                    Date sharedDetailEffFromDate1 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord1));
                    Date sharedDetailEffToDate1 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord1));
                    Date sharedDetailEffFromDate2 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord2));
                    Date sharedDetailEffToDate2 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord2));
                    if (isOverLappingDate(sharedDetailEffFromDate1, sharedDetailEffToDate1, sharedDetailEffFromDate2, sharedDetailEffToDate2)) {
                        String riskName = SharedDetailFields.getShareDtlRiskName(detailRecord1);
                        String coverageName = SharedDetailFields.getShareDtlCoverageShortDesc(detailRecord1);
                        MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.OverLapSourceWithinSameGroup.error",
                            new String[]{rowNum, riskName, coverageName, shareGroupNo}, "", rowId);
                        result = false;
                        break;
                    }
                }
            }
            if (result == false) {
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateOverLapSourceInSameGroup", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * check 2 share group has same shareType
     * @param detailRecord1
     * @param detailRecord2
     * @return boolean
     */
    private boolean hasSameShareType(Record detailRecord1, Record detailRecord2) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasSameShareType", new Object[]{detailRecord1,detailRecord2});
        }

        boolean result = false;
        if ((detailRecord1.hasStringValue("limitType")) && (detailRecord2.hasStringValue("limitType"))) {
            result = true;
        }
        else if ((detailRecord1.hasStringValue("deductType")) && (detailRecord2.hasStringValue("deductType"))) {
            result = true;
        }
        else if ((detailRecord1.hasStringValue("sirType")) && (detailRecord2.hasStringValue("sirType"))) {
            result = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasSameShareType", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * check overlapping Risk/Coverage in tow shared group with the same share type  with overlapping time period
     * @param inputSharedDetailRecords
     * @return boolean
     */
    private boolean validateOverLapSourceInTwoGroup(RecordSet inputSharedDetailRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOverLapSourceInTwoGroup", new Object[]{inputSharedDetailRecords,});
        }

        boolean result = true;
        int length = inputSharedDetailRecords.getSize();
        for (int i = 0; i < length; i++) {
            Record detailRecord1 = inputSharedDetailRecords.getRecord(i);
            for (int j = i + 1; j < length; j++) {
                Record detailRecord2 = inputSharedDetailRecords.getRecord(j);

                String shareDtlGroupMasterId1 = SharedDetailFields.getShareDtlGroupMasterId(detailRecord1);
                String shareDtlGroupMasterId2 = SharedDetailFields.getShareDtlGroupMasterId(detailRecord2);
                String shareDtlSourceRecordId1 = SharedDetailFields.getShareDtlSourceRecordId(detailRecord1);
                String shareDtlSourceRecordId2 = SharedDetailFields.getShareDtlSourceRecordId(detailRecord2);
                if ((shareDtlSourceRecordId1.equals(shareDtlSourceRecordId2)) && (!shareDtlGroupMasterId1.equals(shareDtlGroupMasterId2)) && (hasSameShareType(detailRecord1, detailRecord2))) {
                    Date sharedDetailEffFromDate1 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord1));
                    Date sharedDetailEffToDate1 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord1));
                    Date sharedDetailEffFromDate2 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord2));
                    Date sharedDetailEffToDate2 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord2));
                    if (isOverLappingDate(sharedDetailEffFromDate1, sharedDetailEffToDate1, sharedDetailEffFromDate2, sharedDetailEffToDate2)) {
                        String riskName = SharedDetailFields.getShareDtlRiskName(detailRecord1);
                        String coverageName = SharedDetailFields.getShareDtlCoverageShortDesc(detailRecord1);
                        MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.OverLapSourceWithinTwoGroup.error",
                            new String[]{riskName, coverageName});
                        result = false;
                        break;
                    }
                }
            }
            if (result == false) {
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateOverLapSourceInTwoGroup", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * validate owner record exists or overlap or have gap
     * @param detailRecordSet
     * @return boolean
     */
    private boolean validateOwnerPerShareGroup(RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOwnerPerShareGroup", new Object[]{detailRecordSet,});
        }

        boolean result = true;
        Record shareGroupRecord = detailRecordSet.getSummaryRecord();
        String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
        String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
        String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);

        Date sharedGroupEffFromDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffFromDate(shareGroupRecord));
        Date sharedGroupEffToDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffToDate(shareGroupRecord));
        Iterator it = detailRecordSet.getRecords();
        RecordSet ownerRecordSet = new RecordSet();
        while (it.hasNext()) {
            Record detailRecord = (Record) it.next();
            if ((SharedDetailFields.getShareDtlOwnerB(detailRecord).equals("Y"))) {
                ownerRecordSet.addRecord(detailRecord);
            }
        }
        int length = ownerRecordSet.getSize();
        if (length == 0) {
            MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.noOwnerExistsForSharedGroup.error",
                new String[]{rowNum, shareGroupNo}, "shareDtlOwnerB", rowId);
            result = false;
        }
        else if (length == 1) {
            Record ownerRecord = ownerRecordSet.getRecord(0);
            Date sharedDetailEffFromDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(ownerRecord));
            Date sharedDetailEffToDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(ownerRecord));

            if (!((sharedDetailEffFromDate.equals(sharedGroupEffFromDate)) && (sharedDetailEffToDate.equals(sharedGroupEffToDate)))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.ownerExistsPartPeriodOfSharedGroup.error",
                    new String[]{rowNum, shareGroupNo}, "", rowId);
                result = false;
            }
        }
        else if (length > 1) {
            //sort owner record set by shareDtlEffFromDate asc.
            RecordSet sortedOwnerRecordSet = ownerRecordSet.getSortedCopy(new Comparator() {
                public int compare(Object o1, Object o2) {
                    Record s1 = (Record) o1;
                    Record s2 = (Record) o2;
                    Date sharedDetailEffDate1 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(s1));
                    Date sharedDetailEffDate2 = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(s2));
                    return sharedDetailEffDate1.compareTo(sharedDetailEffDate2);
                }
            }
            );
            //get the earliest and latest detail record
            Record earliestDetailRecord = sortedOwnerRecordSet.getRecord(0);
            Record latestDetailRecord = (sortedOwnerRecordSet.getRecord(sortedOwnerRecordSet.getSize() - 1));
            Date sharedDetailEffFromDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(earliestDetailRecord));
            Date sharedDetailEffToDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(latestDetailRecord));
            if (!((sharedDetailEffFromDate.equals(sharedGroupEffFromDate)) && (sharedDetailEffToDate.equals(sharedGroupEffToDate)))) {
                MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.ownerExistsPartPeriodOfSharedGroup.error",
                    new String[]{rowNum, shareGroupNo}, "", rowId);
                result = false;
            }
            for (int i = 1; i < sortedOwnerRecordSet.getSize(); i++) {
                Date lastExpDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(sortedOwnerRecordSet.getRecord(i - 1)));
                Date nextEffDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(sortedOwnerRecordSet.getRecord(i)));
                //overlap
                if (!(lastExpDate.before(nextEffDate))) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.OverLapOwnersForOneGroup.error",
                        new String[]{rowNum, shareGroupNo}, "", rowId);
                    result = false;
                    break;
                }
                //gap
                else if (DateUtils.dateDiff(DateUtils.DD_DAYS, lastExpDate, nextEffDate) > 1) {
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.GapBetweenOwnersForOneGroup.error",
                        new String[]{rowNum, shareGroupNo}, "", rowId);
                    result = false;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateOwnerPerShareGroup", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * validate limits share group
     * @param detailRecordSet
     * @return boolean
     */
    private boolean validateShareGroupLimits(RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateShareGroupLimits", new Object[]{detailRecordSet,});
        }

        boolean result = true;
        Record shareGroupRecord = detailRecordSet.getSummaryRecord();
        Date shareGroupEffFromDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffFromDate(shareGroupRecord));
        Date shareGroupEffToDate = DateUtils.parseDate(SharedGroupFields.getShareGroupEffToDate(shareGroupRecord));

        if ("Y".equals(shareGroupRecord.getStringValue("shareLimitB")) && !(shareGroupEffFromDate.equals(shareGroupEffToDate))) {
            Iterator it = detailRecordSet.getRecords();
            while (it.hasNext()) {
                Record detailRecord = (Record) it.next();
                Date sharedDetailEffFromDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord));
                Date sharedDetailEffToDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord));

                if (!"Y".equals(SharedDetailFields.getShareDtlSharedLimitB(detailRecord)) && !(sharedDetailEffFromDate.equals(sharedDetailEffToDate))) {
                    String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
                    String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
                    String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
                    String riskName = SharedDetailFields.getShareDtlRiskName(detailRecord);
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidLimitsShareGroup.error",
                        new String[]{rowNum, shareGroupNo, riskName}, "shareLimitB", rowId);
                    result = false;
                    break;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateShareGroupLimits", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * validate share group deduct
     * @param policyHeader
     * @param detailRecordSet
     * @return boolean
     */
    private boolean validateShareGroupDeduct(PolicyHeader policyHeader, RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateShareGroupDeduct", new Object[]{policyHeader, detailRecordSet});
        }

        boolean result = true;
        if (YesNoFlag.getInstance(SharedGroupFields.getShareDeductB(detailRecordSet.getSummaryRecord())).booleanValue()) {
            StringBuffer sourceIdBuf = new StringBuffer();
            StringBuffer sourceTableBuf = new StringBuffer();
            StringBuffer effFromBuf = new StringBuffer();
            StringBuffer effToBuf = new StringBuffer();
            StringBuffer renewalIndBuf = new StringBuffer();
            Iterator it = detailRecordSet.getRecords();
            Date sharedDetailEffFromDate = null;
            Date sharedDetailEffToDate = null;
            String sharedDetailClosingTransLogId = null;
            Date transEffDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
            int totalCount = 0;
            while (it.hasNext()) {
                Record detailRecord = (Record) it.next();
                sharedDetailEffFromDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord));
                sharedDetailEffToDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord));
                sharedDetailClosingTransLogId =  SharedDetailFields.getShareDtlClosTransLogId(detailRecord);

                if (!isOverLappingDate(transEffDate, sharedDetailEffFromDate, sharedDetailEffToDate)
                    && sharedDetailClosingTransLogId == null) {
                    // We only validate records that overlap with the transaction effective date.
                    continue;
                }
                sourceIdBuf.append(SharedDetailFields.getShareDtlSourceRecordId(detailRecord)).append(",");
                sourceTableBuf.append(SharedDetailFields.getShareDtlSourceTableName(detailRecord)).append(",");
                effFromBuf.append(SharedDetailFields.getShareDtlEffFromDate(detailRecord)).append(",");
                effToBuf.append(SharedDetailFields.getShareDtlEffToDate(detailRecord)).append(",");
                renewalIndBuf.append(SharedDetailFields.getShareDtlRenewalB(detailRecord)).append(",");
                totalCount++;
            }
            if (totalCount > 0) {
                sourceIdBuf.deleteCharAt(sourceIdBuf.length() - 1);
                sourceTableBuf.deleteCharAt(sourceTableBuf.length() - 1);
                effFromBuf.deleteCharAt(effFromBuf.length() - 1);
                effToBuf.deleteCharAt(effToBuf.length() - 1);
                renewalIndBuf.deleteCharAt(renewalIndBuf.length() - 1);
                Record inputRecord = new Record();
                inputRecord.setFieldValue("sourceId", sourceIdBuf.toString());
                inputRecord.setFieldValue("sourceTable", sourceTableBuf.toString());
                inputRecord.setFieldValue("effFrom", effFromBuf.toString());
                inputRecord.setFieldValue("effTo", effToBuf.toString());
                inputRecord.setFieldValue("renewB", renewalIndBuf.toString());
                inputRecord.setFieldValue("totalCount", new Long(totalCount));
                inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
                inputRecord.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
                inputRecord.setFieldValue("transEff", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                inputRecord.setFieldValue("transLog", policyHeader.getLastTransactionId());
                String errMsg = getLimitSharingDAO().validateShareGroupDeduct(inputRecord);
                if (!(StringUtils.isBlank(errMsg))) {
                    Record shareGroupRecord = detailRecordSet.getSummaryRecord();
                    String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
                    String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
                    String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
                    MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidDedutibleShareGroup.error",
                        new String[]{rowNum, shareGroupNo, errMsg}, "shareDeductB", rowId);
                    result = false;
                }
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateShareGroupDeduct", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * validate share group sir
     * @param policyHeader
     * @param detailRecordSet
     * @return boolean
     */
    private boolean validateShareGroupSir(PolicyHeader policyHeader, RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateShareGroupSir", new Object[]{policyHeader, detailRecordSet});
        }

        boolean result = true;
        Record shareGroupRecord = detailRecordSet.getSummaryRecord();
        if (shareGroupRecord.hasStringValue("shareSirB")) {
            if (YesNoFlag.getInstance(SharedGroupFields.getShareSirB(shareGroupRecord)).booleanValue()) {
                StringBuffer sourceIdBuf = new StringBuffer();
                StringBuffer sourceTableBuf = new StringBuffer();
                StringBuffer effFromBuf = new StringBuffer();
                StringBuffer effToBuf = new StringBuffer();
                StringBuffer renewalIndBuf = new StringBuffer();
                Iterator it = detailRecordSet.getRecords();
                Date sharedDetailEffFromDate = null;
                Date sharedDetailEffToDate = null;
                String sharedDetailClosingTransLogId = null;
                Date transEffDate = DateUtils.parseDate(policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                int totalCount = 0;
                while (it.hasNext()) {
                    Record detailRecord = (Record) it.next();
                    sharedDetailEffFromDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffFromDate(detailRecord));
                    sharedDetailEffToDate = DateUtils.parseDate(SharedDetailFields.getShareDtlEffToDate(detailRecord));
                    sharedDetailClosingTransLogId =  SharedDetailFields.getShareDtlClosTransLogId(detailRecord);
                    if (!isOverLappingDate(transEffDate, sharedDetailEffFromDate, sharedDetailEffToDate)
                        && sharedDetailClosingTransLogId == null) {
                        // We only validate records that overlap with the transaction effective date.
                        continue;
                    }
                    sourceIdBuf.append(SharedDetailFields.getShareDtlSourceRecordId(detailRecord)).append(",");
                    sourceTableBuf.append(SharedDetailFields.getShareDtlSourceTableName(detailRecord)).append(",");
                    effFromBuf.append(SharedDetailFields.getShareDtlEffFromDate(detailRecord)).append(",");
                    effToBuf.append(SharedDetailFields.getShareDtlEffToDate(detailRecord)).append(",");
                    renewalIndBuf.append(SharedDetailFields.getShareDtlRenewalB(detailRecord)).append(",");
                    totalCount++;
                }
                if (totalCount > 0) {
                    sourceIdBuf.deleteCharAt(sourceIdBuf.length() - 1);
                    sourceTableBuf.deleteCharAt(sourceTableBuf.length() - 1);
                    effFromBuf.deleteCharAt(effFromBuf.length() - 1);
                    effToBuf.deleteCharAt(effToBuf.length() - 1);
                    renewalIndBuf.deleteCharAt(renewalIndBuf.length() - 1);
                    Record inputRecord = new Record();
                    inputRecord.setFieldValue("sourceId", sourceIdBuf.toString());
                    inputRecord.setFieldValue("sourceTable", sourceTableBuf.toString());
                    inputRecord.setFieldValue("effFrom", effFromBuf.toString());
                    inputRecord.setFieldValue("effTo", effToBuf.toString());
                    inputRecord.setFieldValue("renewB", renewalIndBuf.toString());
                    inputRecord.setFieldValue("totalCount", new Long(totalCount));
                    inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
                    inputRecord.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
                    inputRecord.setFieldValue("transEff", policyHeader.getLastTransactionInfo().getTransEffectiveFromDate());
                    inputRecord.setFieldValue("transLog", policyHeader.getLastTransactionId());
                    inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());

                    String errMsg = getLimitSharingDAO().validateShareGroupSir(inputRecord);
                    if ("N".equals(errMsg)) {
                        String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
                        String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
                        String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
                        MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidSirShareGroup.error",
                            new String[]{rowNum, shareGroupNo}, "shareSirB", rowId);
                        result = false;
                    }
                    else if (!StringUtils.isBlank(errMsg) && errMsg.length() > 1) {
                        String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
                        String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
                        String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
                        MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.noSirComponent.error",
                            new String[]{rowNum, shareGroupNo, errMsg}, "shareSirB", rowId);
                        result = false;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateShareGroupSir", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * Checks the existence of overlapping periods (fromDate1,toDate1) and (fromDate2,toDate2).
     * If exists, return true, else return false;
     * @param fromDate1
     * @param toDate1
     * @param fromDate2
     * @param toDate2
     * @return boolean
     */
    private boolean isOverLappingDate(Date fromDate1, Date toDate1, Date fromDate2, Date toDate2) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOverLappingDate", new Object[]{fromDate1, fromDate2, toDate2});
        }

        boolean result = false;
        if ( fromDate1.before(toDate2) && toDate1.after(fromDate2) ) {
            result = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOverLappingDate", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * Checks the existence of overlapping date with period effDate and (fromDate,toDate).
     * If exists, return true, else return false;
     * @param effDate
     * @param fromDate
     * @param toDate
     * @return boolean
     */
    private boolean isOverLappingDate(Date effDate, Date fromDate, Date toDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOverLappingDate", new Object[]{effDate, fromDate, toDate});
        }

        boolean result = false;
        if ( (fromDate.before(effDate)||fromDate.equals(effDate)) && toDate.after(effDate) ) {
            result = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOverLappingDate", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * get the record mode code to be passed for data retrieval based on whether the policy is in WIP/OFFICIAL/ENDQUOTE mode.
     *
     * @param policyHeader
     * @return String
     */
    private String getLimitSharingRecordModeCode(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLimitSharingRecordModeCode", new Object[]{policyHeader,});
        }

        String recordModeCode;
        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isWIP()) {
            recordModeCode = RecordMode.TEMP.getName();
        }
        else if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isEndquote()) {
            recordModeCode = RecordMode.ENDQUOTE.getName();
        }
        else {
            recordModeCode = RecordMode.OFFICIAL.getName();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLimitSharingRecordModeCode", recordModeCode);
        }
        return recordModeCode;
    }

    /**
     * validate renewal indicator in group and detail
     * @param detailRecordSet
     * @return boolean
     */
    private boolean validateShareGroupRenewB(RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateShareGroupRenewB", new Object[]{detailRecordSet,});
        }

        boolean result = true;
        Record shareGroupRecord = detailRecordSet.getSummaryRecord();
        String grpRenewalB = shareGroupRecord.getStringValue("renewalB");
        Iterator it = detailRecordSet.getRecords();
        int dtlRenCounter = 0;
        while (it.hasNext()) {
            Record detailRecord = (Record) it.next();
            if (SharedDetailFields.getShareDtlRenewalB(detailRecord).equalsIgnoreCase("Y")) {
                dtlRenCounter = dtlRenCounter + 1;
            }
            if ("Y".equals(SharedDetailFields.getShareDtlRenewalB(detailRecord)) && !grpRenewalB.equalsIgnoreCase("Y")) {
                String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
                String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
                String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
                String riskName = SharedDetailFields.getShareDtlRiskName(detailRecord);
                String covgDesc = SharedDetailFields.getShareDtlCoverageShortDesc(detailRecord);
                MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidRenewalIndShareDetail.error",
                    new String[]{rowNum, riskName, covgDesc, shareGroupNo}, "", rowId);
                result = false;
                break;
            }
        }

        if (dtlRenCounter == 0 && grpRenewalB.equalsIgnoreCase("Y")) {
            String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
            String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
            String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
            MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidRenewalIndShareGroup.error",
                new String[]{rowNum, shareGroupNo, ""}, "", rowId);
            result = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateShareGroupRenewB", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * Custom validations for shared limit groups
     * @param policyHeader policy header information
     * @param sharedGroup  shared group information
     * @param detailRecordSet detail shared group information
     * @return boolean
     */
    private boolean validateSharedLimitGroup (PolicyHeader policyHeader, Record sharedGroup, RecordSet detailRecordSet) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSharedLimitGroup", new Object[]{policyHeader, sharedGroup, detailRecordSet});
        }

        boolean result = true;
        Record shareGroupRecord = detailRecordSet.getSummaryRecord();
        StringBuffer sourceIdBuf = new StringBuffer();
        StringBuffer sourceTableBuf = new StringBuffer();
        StringBuffer renewalIndBuf = new StringBuffer();
        Iterator it = detailRecordSet.getRecords();
        Record firstRecord = (Record) it.next();

        String sharedGroupId = SharedGroupFields.getPolicySharedGroupMasterId(sharedGroup);
        String shareLimitB = SharedGroupFields.getShareLimitB(sharedGroup);
        String sharedeductB = SharedGroupFields.getShareDeductB(sharedGroup);
        String shareSirB = SharedGroupFields.getShareSirB(sharedGroup);
        String groupRenewalB = SharedGroupFields.getRenewalB(sharedGroup);
        String sharedGroupDescription = SharedGroupFields.getShareGroupDesc(sharedGroup);
        sourceIdBuf.append(SharedDetailFields.getShareDtlSourceRecordId(firstRecord));
        sourceTableBuf.append(SharedDetailFields.getShareDtlSourceTableName(firstRecord));
        renewalIndBuf.append(SharedDetailFields.getShareDtlRenewalB(firstRecord));
        while (it.hasNext()) {
            sourceIdBuf.append(",");
            sourceTableBuf.append(",");
            renewalIndBuf.append(",");
            Record detailRecord = (Record) it.next();
            sourceIdBuf.append(SharedDetailFields.getShareDtlSourceRecordId(detailRecord));
            sourceTableBuf.append(SharedDetailFields.getShareDtlSourceTableName(detailRecord));
            renewalIndBuf.append(SharedDetailFields.getShareDtlRenewalB(detailRecord));
        }
        Record inputRecord = new Record();

        inputRecord.setFieldValue("sharedGroupId", sharedGroupId);
        inputRecord.setFieldValue("sourceId", sourceIdBuf.toString());
        inputRecord.setFieldValue("sourceTable", sourceTableBuf.toString());
        inputRecord.setFieldValue("totalCount", new Long(detailRecordSet.getSize()));
        inputRecord.setFieldValue("termEff", policyHeader.getTermEffectiveFromDate());
        inputRecord.setFieldValue("termExp", policyHeader.getTermEffectiveToDate());
        inputRecord.setFieldValue("transLog", policyHeader.getLastTransactionId());
        inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
        inputRecord.setFieldValue("renewB", renewalIndBuf.toString());
        inputRecord.setFieldValue("shareGroupDesc", sharedGroupDescription);
        inputRecord.setFieldValue("shareLimitB", shareLimitB);
        inputRecord.setFieldValue("shareDeductB", sharedeductB);
        inputRecord.setFieldValue("shareSirB", shareSirB);
        inputRecord.setFieldValue("groupRenewalB", groupRenewalB);

        String errMsg = getLimitSharingDAO().validateSharedLimitGroup(inputRecord);
        if (!StringUtils.isBlank(errMsg) && !("Y".equals(errMsg))) {

            String rowNum = String.valueOf(shareGroupRecord.getRecordNumber() + 1);
            String rowId = SharedGroupFields.getPolicySharedGroupMasterId(shareGroupRecord);
            String shareGroupNo = SharedGroupFields.getShareGroupNo(shareGroupRecord);
            MessageManager.getInstance().addErrorMessage("pm.maintainLimitSharing.InvalidShareLimitGroup.error",
                new String[]{rowNum, shareGroupNo, errMsg}, "", rowId);
            result = false;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSharedLimitGroup", Boolean.valueOf(result));
        }
        return result;
    }

    /**
     * load all available shared limit detail
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordSet
     */
    public RecordSet loadAllSharedLimit(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllSharedLimit", new Object[]{policyHeader, inputRecord});
        }

        // Build the input record
        Record input = new Record();
        SharedDetailFields.setPolicyId(input,policyHeader.getPolicyId());
        SharedDetailFields.setEffDate(input, policyHeader.getTermEffectiveFromDate());
        SharedDetailFields.setExpDate(input, policyHeader.getTermEffectiveToDate());
        SharedDetailFields.setTransLogId(input,policyHeader.getLastTransactionId());

        if (policyHeader.getPolicyIdentifier().getPolicyViewMode().isOfficial()) {
            input.setFieldValue(PMCommonFields.RECORD_MODE_CODE, RecordMode.OFFICIAL);
        }
        else {
            input.setFieldValue(PMCommonFields.RECORD_MODE_CODE, RecordMode.TEMP);
        }

        RecordSet rs = getLimitSharingDAO().loadAllSharedLimit(input);

        if ((rs.getSize()) <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.viewSharedLimitInfo.sharedLimitList.noDataFound");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllSharedLimit", rs);
        }
        return rs;
    }

//-------------------------------------------------
// Configuration constructor and accessor methods
//-------------------------------------------------

    public void verifyConfig() {
        if (getLimitSharingDAO() == null)
            throw new ConfigurationException("The required property 'limitSharingDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }

    public LimitSharingManagerImpl() {
    }

    public LimitSharingDAO getLimitSharingDAO() {
        return m_limitSharingDAO;
    }

    public void setLimitSharingDAO(LimitSharingDAO limitSharingDAO) {
        m_limitSharingDAO = limitSharingDAO;
    }


    private LimitSharingDAO m_limitSharingDAO;

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
    protected static final String MAINTAIN_LIMIT_SHARING_ACTION_CLASS_NAME = "dti.pm.policymgr.limitsharingmgr.struts.MaintainLimitSharingAction";
    private static final String OPEN_DATE = "01/01/3000";
}
