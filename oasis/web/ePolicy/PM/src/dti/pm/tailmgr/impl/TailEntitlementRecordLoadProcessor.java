package dti.pm.tailmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailScreenMode;
import dti.pm.tailmgr.dao.TailDAO;
import dti.pm.core.http.RequestIds;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * * This class extends the default record load processor to enforce entitlements for risk web page. This class works in
 * conjunction with pageEntitlements.xml configuration.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 27, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2007       fcb         recordSet: added several missing entitlement fields for the
 *                              case when recordSet size is zero.
 * 04/03/2008       sxm         Issue 81453 - modified postProcessRecord() 
 *                              1) added handling of NULL values from dummy record for adding MATAIL/PATAIL
 *                              2) fixed logic of enabling Add/Delete Tail buttons
 * 08/18/2010       gxc         Issue 106055 - modified to add & set isFinChargeAvailable
 * 10/14/2010       syang       Issue 103811 - Add a new method getValidSelectB() and a new field "validSelectB" to record. 
 * 10/15/2010       syang       Issue 103811 - Modified getValidSelectB() to pass the tailCovBaseRecordId(Exists in record) to retrieve tailTermId.
 * 10/18/2010       dzhang      Issue 113002 - move the Accept/Decline/Activate/Cancellation/Reinstate options available logic from page level to Row level.
 * 10/20/2010       dzhang      Issue 113002 - Rollback the changes done by change list 190940.
 * 10/22/2010       dzhang      Issue 103811 - For TailDAO().getTailHistoryId when return null, set it as current termHistoryId.
 * 10/28/2010       dzhang      Issue 113002 - Correct isSelectAvailable & isViewAvailableby logic to using termhistoryId instead of termBaseRecordId.
 * 02/23/2011       dzhang      Issue 113062 - Modified isSelectAvailable & validSelectB logic for Norcal.
 * 02/25/2011       dzhang      Issue 113062 - Update per Joe's comments.
 * 04/26/2011       dzhang      Issue 119016 - Modified isAddComponentAvailable and isRateAvailable available logic.
 * 06/17/2011       wqfu        Issue 121714 - Modified getValidSelectB to handle MATAIL relation type coverage.
 * 04/25/2014       xnie        Issue 153450 - Modified postProcessRecord() to replace main coverage record mode code
 *                                             with tail coverage record mode code.
 * 03/08/2016       wdang       Issue 169688 - Modified postProcessRecord logic for PATAIL/MATAIL.
 * 07/27/2016       eyin        Issue 176557 - Modified postProcessRecord() and postProcessRecordSet() for ENABLE/DISABLE
 *                                             new drop field 'Ext. Rem. Limit'.
 * ---------------------------------------------------
 */

public class TailEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Returns a synchronized static instance of Tail Entitlement Record Load Processor that has the
     * implementation information.
     *
     * @param inputRecord, inuptRecord that provides basic information about selected policy
     * @return an instance of TailEntitlementRecordLoadProcessor class
     */
    public synchronized static TailEntitlementRecordLoadProcessor getInstance(Record inputRecord, PolicyHeader policyHeader, TailDAO tailDAO) {
        Logger l = LogUtils.enterLog(TailEntitlementRecordLoadProcessor.class,
            "getInstance", new Object[]{inputRecord});

        TailEntitlementRecordLoadProcessor instance;
        instance = new TailEntitlementRecordLoadProcessor();
        instance.setInputRecord(inputRecord);
        instance.setPolicyHeader(policyHeader);
        instance.setTailDAO(tailDAO);                             

        l.exiting(TailEntitlementRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record,new Boolean(rowIsOnCurrentPage)});
        }

        Record inputRecord = getInputRecord();
        TailScreenMode tailScreenMode = TailScreenMode.getInstance(TailFields.getTailScreenMode(inputRecord));
        PolicyHeader policyHeader = getPolicyHeader();
        PolicyCycleCode policyCycle = policyHeader.getPolicyCycleCode();
        RecordMode recordModeCode = RecordMode.getInstance(TailFields.getTailRecordModeCode(record));
        String ratingModuleCode = TailFields.getRatingModuleCode(record);
        String tailStatus = TailFields.getTailCurrPolRelStatTypeCd(record);
        YesNoFlag toRateB = TailFields.getToRateB(record);
        String effDate = TailFields.getEffectiveFromDate(record);
        String expDate = TailFields.getEffectiveToDate(record);
        String tailTermBaseRecordId = TailFields.getTermBaseRecordId(record);
        String tailProductCoverageCode = TailFields.getProductCoverageCode(record);
        String prodCovRelTypeCode = TailFields.getProdCovRelTypeCode(record);

        //init fileds to disable
        record.setFieldValue("isTailGrossPremiumEditable", YesNoFlag.N);
        record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.N);
        record.setFieldValue("isTailExtRemLimitBEditable", YesNoFlag.N);
        record.setFieldValue("isSelectAvailable", YesNoFlag.N);
        record.setFieldValue("isViewAvailable", YesNoFlag.N);
        record.setFieldValue("isAdjustLimiAvailable", YesNoFlag.N);
        record.setFieldValue("isAddTailAvailable", YesNoFlag.N);
        record.setFieldValue("isDeleteTailAvailable", YesNoFlag.N);
        record.setFieldValue("isAddComponentAvailable", YesNoFlag.N);
        record.setFieldValue("isDelCompAvailable", YesNoFlag.N);
        record.setFieldValue("isChgCompValueAvailable", YesNoFlag.N);
        record.setFieldValue("isChgCompDateAvailable", YesNoFlag.N);
        record.setFieldValue("isCycleDetailAvailable", YesNoFlag.N);
        record.setFieldValue("isSurchargePointsAvailable", YesNoFlag.N);
        record.setFieldValue("isFinChargeAvailable", YesNoFlag.N);

        TailFields.setTailScreenMode(record, tailScreenMode);

        //set select ind, from the previous request
        if(inputRecord.hasFieldValue("selectedIds")){
            String selectIds =  inputRecord.getStringValue("selectedIds");
            if(selectIds.indexOf(TailFields.getTailCovBaseRecordId(record)+"^")>=0){
                record.setFieldValue(RequestIds.SELECT_IND, "-1");
            }else{
                return false;
            }
        }

        //set options that do not depend on screen mode
        if (!recordModeCode.isOfficial() && !StringUtils.isBlank(tailProductCoverageCode) &&
            ("MATAIL".equals(prodCovRelTypeCode) || "PATAIL".equals(prodCovRelTypeCode))) {
                record.setFieldValue("isDeleteTailAvailable", YesNoFlag.Y);
        }
        //based on current screen mode
        if (tailScreenMode.isWIP()) {
            ScreenModeCode screenMode = policyHeader.getScreenModeCode();
            if ("PATAIL".equals(prodCovRelTypeCode)) {
                if (!StringUtils.isBlank(tailProductCoverageCode)) {
                    record.setFieldValue("isAddComponentAvailable", YesNoFlag.Y);
                }
            }
            else if ("MATAIL".equals(prodCovRelTypeCode)) {
                if (!StringUtils.isBlank(tailProductCoverageCode) && (screenMode.isCancelWIP() || screenMode.isRenewWIP())) {
                    record.setFieldValue("isAddComponentAvailable", YesNoFlag.Y);
                }
            }
            else if (screenMode.isCancelWIP() || screenMode.isRenewWIP()) {
                record.setFieldValue("isAddComponentAvailable", YesNoFlag.Y);
            }

            if (!recordModeCode.isOfficial() && !StringUtils.isBlank(ratingModuleCode) && ratingModuleCode.indexOf("M") == 0) {
                record.setFieldValue("isTailGrossPremiumEditable", YesNoFlag.Y);
            }
            if (!recordModeCode.isOfficial() && "OFFER".equals(tailStatus) && toRateB.booleanValue()) {
                record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
                record.setFieldValue("isTailExtRemLimitBEditable", YesNoFlag.Y);
            }

            if (StringUtils.isBlank(tailProductCoverageCode) &&
                ("MATAIL".equals(prodCovRelTypeCode) || "PATAIL".equals(prodCovRelTypeCode))) {
                record.setFieldValue("isAddTailAvailable", YesNoFlag.Y);
            }

            if ("B".equals(SysParmProvider.getInstance().getSysParm("FM_TAIL_FIN_CHARGE"))) {
                record.setFieldValue("isFinChargeAvailable", YesNoFlag.Y);
            }
        }
        else if (tailScreenMode.isUpdate()) {
            record.setFieldValue("isAddComponentAvailable", YesNoFlag.Y);
            record.setFieldValue("isAdjustLimiAvailable", YesNoFlag.Y);

            if (!StringUtils.isBlank(ratingModuleCode) && ratingModuleCode.indexOf("M") == 0 ||
                "CANCEL".equals(tailStatus) && DateUtils.parseDate(expDate).after(DateUtils.parseDate(effDate))) {
                record.setFieldValue("isTailGrossPremiumEditable", YesNoFlag.Y);
            }

            if ("OFFER".equals(tailStatus)) {
                record.setFieldValue("isCoverageLimitCodeEditable", YesNoFlag.Y);
                record.setFieldValue("isTailExtRemLimitBEditable", YesNoFlag.Y);
            }

            if (tailTermBaseRecordId.equals(policyHeader.getTermBaseRecordId()) &&
                !("ACTIVE".equals(tailStatus) || "OFFER".equals(tailStatus)) ||
                !tailTermBaseRecordId.equals(policyHeader.getTermBaseRecordId())) {
                    record.setFieldValue("isAddComponentAvailable", YesNoFlag.N);
            }
        }
        else if (tailScreenMode.isUpdatable() || tailScreenMode.isViewOnly()) {
            //all readonly, not editable
            if (tailScreenMode.isUpdatable()) {
                if (policyCycle.isPolicy()) {
                    record.setFieldValue("isAdjustLimiAvailable", YesNoFlag.Y);
                }
                record.setFieldValue("isSelectAvailable", YesNoFlag.Y);
            }

            // when changing row
            if (tailTermBaseRecordId.equals(policyHeader.getTermBaseRecordId())) {
                String pmCovCodes = SysParmProvider.getInstance().getSysParm("PM_TAIL_TERM_PK");
                String tailTermId = getTailDAO().getTailHistoryId(record);
                if(tailTermId == null) {
                    tailTermId = getPolicyHeader().getPolicyTermHistoryId();
                }
                if (pmCovCodes != null && pmCovCodes.indexOf(tailProductCoverageCode) > 0) {
                    if (tailTermId.equals(policyHeader.getPolicyTermHistoryId())) {
                        record.setFieldValue("isSelectAvailable", YesNoFlag.Y);
                        record.setFieldValue("isViewAvailable", YesNoFlag.N);
                    }
                    else {
                        record.setFieldValue("isSelectAvailable", YesNoFlag.N);
                        record.setFieldValue("isViewAvailable", YesNoFlag.N);
                    }
                }
                else {
                    record.setFieldValue("isSelectAvailable", YesNoFlag.Y);
                    record.setFieldValue("isViewAvailable", YesNoFlag.N);
                }
            }
            else {
                if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_TAIL_CXL_ANY_TERM, "N")).booleanValue()) {
                    record.setFieldValue("isSelectAvailable", YesNoFlag.Y);
                }
                else {
                    record.setFieldValue("isSelectAvailable", YesNoFlag.N);
                }
                record.setFieldValue("isViewAvailable", YesNoFlag.Y);
            }
        }

        // Issue 103811, add the new field validSelectB to determine whether the record can be selected when click "Select ALl".
        record.setFieldValue("validSelectB", getValidSelectB(record));

        //Issue 113062 for Norcal logic
        if(YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_TAIL_CXL_ANY_TERM, "N")).booleanValue()) {
            record.setFieldValue("validSelectB", YesNoFlag.Y);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", new Boolean(true));
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

        Record record = recordSet.getSummaryRecord();
        String tailScreenMode = TailFields.getTailScreenMode(inputRecord);
        PolicyCycleCode policyCycle = getPolicyHeader().getPolicyCycleCode();

        record.setFieldValue("isRateAvailable", YesNoFlag.N);
        record.setFieldValue("isUpdateAvailable", YesNoFlag.N);
        record.setFieldValue("isSaveAvailable", YesNoFlag.N);
        record.setFieldValue("isAcceptAvailable", YesNoFlag.N);
        record.setFieldValue("isTailGridEmpty", YesNoFlag.N);

        if (recordSet.getSize() > 0) {
            if (tailScreenMode.equals("WIP")) {
                ScreenModeCode screenMode = getPolicyHeader().getScreenModeCode();
                if (screenMode.isCancelWIP() || screenMode.isRenewWIP()) {
                    record.setFieldValue("isRateAvailable", YesNoFlag.Y);
                }
                record.setFieldValue("isSaveAvailable", YesNoFlag.Y);
            }
            else if (tailScreenMode.equals("UPDATE")) {
                record.setFieldValue("isRateAvailable", YesNoFlag.Y);
                record.setFieldValue("isSaveAvailable", YesNoFlag.Y);
            }
            else if (tailScreenMode.equals("UPDATABLE")) {
                if (policyCycle.isPolicy()) {
                    record.setFieldValue("isAcceptAvailable", YesNoFlag.Y);
                }
                if (policyCycle.isPolicy() || policyCycle.isQuote())
                    record.setFieldValue("isUpdateAvailable", YesNoFlag.Y);
            }
        }

        else if (recordSet.getSize() == 0) {
            record.setFieldValue("isTailGridEmpty", YesNoFlag.Y);

            List fieldNames = new ArrayList();

            fieldNames.add("isTailGridEmpty");
            fieldNames.add("isViewAvailable");
            fieldNames.add("isAdjustLimiAvailable");
            fieldNames.add("isAddTailAvailable");
            fieldNames.add("isDeleteTailAvailable");
            fieldNames.add("isAddComponentAvailable");
            fieldNames.add("isDelCompAvailable");
            fieldNames.add("isTailGrossPremiumEditable");
            fieldNames.add("isCoverageLimitCodeEditable");
            fieldNames.add("isTailExtRemLimitBEditable");
            fieldNames.add("isSelectAvailable");
            fieldNames.add("isChgCompValueAvailable");
            fieldNames.add("isChgCompDateAvailable");
            fieldNames.add("isCycleDetailAvailable");
            fieldNames.add("isSurchargePointsAvailable");
            fieldNames.add("isSurchargePointsAvailable");
            fieldNames.add("isFinChargeAvailable");
            recordSet.addFieldNameCollection(fieldNames);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecordSet");
        }
    }

    /**
     * Return the value of validSelectB.
     *
     * @param record
     * @return YesNoFlag
     */
    private YesNoFlag getValidSelectB(Record record) {
        YesNoFlag selectB = YesNoFlag.Y;
        String pmCovCodes = SysParmProvider.getInstance().getSysParm("PM_TAIL_TERM_PK", null);
        if (!StringUtils.isBlank(pmCovCodes) && record.hasStringValue("productCoverageCode")) {
            String tailProductCoverageCode = record.getStringValue("productCoverageCode");
            if (pmCovCodes.indexOf(tailProductCoverageCode) > -1) {
                String tailTermId = getTailDAO().getTailHistoryId(record);
                if(tailTermId == null) {
                    tailTermId = getPolicyHeader().getPolicyTermHistoryId();
                }
                // tailTermId should compare with the none base term record PK of the current term the Tail screen was opened from.
                String currentTermId = getPolicyHeader().getPolicyTermHistoryId();
                if (!currentTermId.equals(tailTermId)) {
                    selectB = YesNoFlag.N;
                }
            }
        }
        return selectB;
    }

    public Record getInputRecord() {
        return inputRecord;
    }

    public void setInputRecord(Record inputRecord) {
        this.inputRecord = inputRecord;
    }

    public PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    public void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    public TailDAO getTailDAO() {
        return m_TailDAO;
    }

    public void setTailDAO(TailDAO tailDAO) {
        m_TailDAO = tailDAO;
    }

    private TailDAO m_TailDAO;
    private Record inputRecord;
    private PolicyHeader m_policyHeader;
}
