package dti.pm.policymgr.distributionmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.distributionmgr.DistributionFields;
import dti.pm.policymgr.distributionmgr.DistributionManager;
import dti.pm.policymgr.distributionmgr.dao.DistributionDAO;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of DistributionManager Interface.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/28            Jerry       issue #123179 The page shows a message of 'Distribution calculation job successfully
 *                                            submitted' after user click the 'process' button and successfully execute.
 * 11/15/2013       xnie        142674  a. Modified getInitialValuesForAddDistribution() to remove default value for
 *                                         transaction code and action code. They are handled by base webWB. Set initial
 *                                         values for risk type code and product coverage code.
 *                                      b. Modified validateAllDistribution() to
 *                                         1. Prevent saving record which effective to date is prior to effective from
 *                                            date.
 *                                         2. Skip isZero and isDup check for dividend calculation with action
 *                                            'CALC_TRANS'.
 *                                         3. Add a isCalcTransDup check which is just for dividend calculation with
 *                                            action 'CALC_TRANS' duplication validation.
 *                                      c. Added processCatchUp() to catch up dividend.
 * ---------------------------------------------------
 */


public class DistributionManagerImpl implements DistributionManager {

    /**
     * Returns a RecordSet loaded with list of distributions
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available distributions.
     */
    public RecordSet loadAllDistribution(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllDistribution", null);
        }

        RecordLoadProcessor load = new DistributionEntitlementRecordLoadProcessor();
        Record loadRecord = new Record();
        if (inputRecord.hasStringValue(DistributionFields.DISTRI_SEARCH_DATE)) {
            DistributionFields.setEffectiveFromDate(loadRecord,
                    DistributionFields.getDistriSearchDate(inputRecord));
        }

        RecordSet rs = getDistributionDAO().loadAllDistribution(loadRecord, load);

        // No data found message handled here
        if (rs.getSize() <= 0) {
            MessageManager.getInstance().addErrorMessage("pm.process.distribution.noDataFound.error");
        }

        // Generate seq no group by distribution year
        if (rs.getSize() > 0) {
            Record first = rs.getFirstRecord();
            String year = DistributionFields.getYear(first);
            RecordSet yearSub = rs.getSubSet(new RecordFilter(DistributionFields.YEAR, year));
            int len = yearSub.getSize();
            Iterator it = rs.getRecords();
            Record record = null;
            String newYear = null;
            while (it.hasNext()) {
                record = (Record)it.next();
                newYear = DistributionFields.getYear(record);
                if (newYear.equals(year)) {
                    DistributionFields.setSeqNo(record, Integer.toString(len));
                    len--;
                } else {
                    year = newYear;
                    yearSub = rs.getSubSet(new RecordFilter(DistributionFields.YEAR, year));
                    len = yearSub.getSize();
                    DistributionFields.setSeqNo(record, Integer.toString(len));
                    len--;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllDistribution", rs);
        }

        return rs;
    }

    /**
     * validate the input data for saving.
     *
     * @param inputRecords
     * @returm changedRecords
     */
    protected RecordSet validateAllDistribution(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllDistribution", new Object[]{inputRecords});
        }

        // Get the changed records to be validated
        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);

        boolean isDup = false;
        boolean isZero = false;
        boolean isCalcTransDup = false;

        Iterator cit = changedRecords.getRecords();
        Iterator ait = inputRecords.getRecords();

        Record cRecord = null;
        Record aRecord = null;
        Record calcTransRecord = null;
        String cEffDate = null;
        String cToDate = null;
        String aEffDate = null;
        String calcTransEffDate = null;
        String cIssComId = null;
        String aIssComId = null;
        String calcTransIssComId = null;
        String cIssStateCode = null;
        String calcTransIssStateCode = null;
        String cPolicyTypeCode = null;
        String calcTransPolicyTypeCode = null;
        String cRiskTypeCode = null;
        String calcTransRiskTypeCode = null;
        String cProductCoverageCode = null;
        String calcTransProductCoverageCode = null;
        String percentage = null;
        String cRuleId = null;
        String aRuleId = null;
        String calcTransRuleId = null;
        while (cit.hasNext()) {
            cRecord = (Record)cit.next();
            cEffDate = DistributionFields.getEffectiveFromDate(cRecord);
            cToDate = DistributionFields.getEffectiveToDate(cRecord);
            cIssComId = DistributionFields.getIssueCompanyEntityId(cRecord);
            cIssStateCode = DistributionFields.getIssueStateCode(cRecord);
            cPolicyTypeCode = DistributionFields.getPolicyTypeCode(cRecord);
            cRiskTypeCode = DistributionFields.getRiskTypeCode(cRecord);
            cProductCoverageCode = DistributionFields.getProductCoverageCode(cRecord);
            cRuleId = DistributionFields.getDividendRuleId(cRecord);
            percentage = DistributionFields.getPercentage(cRecord);

            if (!DistributionFields.getAction(cRecord).equals(DistributionFields.ActionValues.ACTION_CALC_TRANS)) {
                // Set effective to date as same to effective from date
                DistributionFields.setEffectiveToDate(cRecord, cEffDate);
                cToDate = cEffDate;
            }
            // Check if effective to date is prior to effective from date.
            if (DateUtils.parseDate(cToDate).before(DateUtils.parseDate(cEffDate))) {
                MessageManager.getInstance().addErrorMessage("pm.process.distribution.invalidEffectiveToDate.error");
            }

            // Check if duplicate data is exists for dividend calculation with action 'CALC_TRANS'.
            if (DistributionFields.getAction(cRecord).equals(DistributionFields.ActionValues.ACTION_CALC_TRANS)) {
                RecordSet calcTransRecords = inputRecords.getSubSet(new RecordFilter(DistributionFields.ACTION,
                    DistributionFields.ActionValues.ACTION_CALC_TRANS));

                Iterator calcTranst = calcTransRecords.getRecords();
                while (calcTranst.hasNext()) {
                    calcTransRecord = (Record)calcTranst.next();
                    calcTransRuleId = DistributionFields.getDividendRuleId(calcTransRecord);
                    if (cRuleId.equals(calcTransRuleId)) {
                        continue;
                    }
                    else {
                        calcTransEffDate = DistributionFields.getEffectiveFromDate(calcTransRecord);
                        calcTransIssComId = DistributionFields.getIssueCompanyEntityId(calcTransRecord);
                        calcTransIssStateCode = DistributionFields.getIssueStateCode(calcTransRecord);
                        calcTransIssStateCode = DistributionFields.getIssueStateCode(calcTransRecord);
                        calcTransPolicyTypeCode = DistributionFields.getPolicyTypeCode(calcTransRecord);
                        calcTransRiskTypeCode = DistributionFields.getRiskTypeCode(calcTransRecord);
                        calcTransProductCoverageCode = DistributionFields.getProductCoverageCode(calcTransRecord);

                        // Check if exists duplicated distribution data for action 'CALC_TRANS'.
                        if (cIssComId.equals(calcTransIssComId) &&
                            cIssStateCode.equals(calcTransIssStateCode) &&
                            cPolicyTypeCode.equals(calcTransPolicyTypeCode) &&
                            cRiskTypeCode.equals(calcTransRiskTypeCode) &&
                            cProductCoverageCode.equals(calcTransProductCoverageCode) &&
                            cEffDate.equals(calcTransEffDate)) {
                            isCalcTransDup = true;
                            break;
                        }
                    }
                }
                break;
            }

            // Check if not deleted record and percentage value is zero for action which is NOT 'CALC_TRANS'.
            if (Double.parseDouble(percentage) == 0 && !cRecord.getUpdateIndicator().equals(UpdateIndicator.DELETED)) {
                isZero = true;
                break;
            }

            // Check if duplicate data is exists for dividend calculation with action is NOT 'CALC_TRANS'.
            while (ait.hasNext()) {
                aRecord = (Record)ait.next();
                aRuleId = DistributionFields.getDividendRuleId(aRecord);
                if (cRuleId.equals(aRuleId)) {
                    continue;
                } else {
                    aEffDate = DistributionFields.getEffectiveFromDate(aRecord);
                    aIssComId = DistributionFields.getIssueCompanyEntityId(aRecord);

                    // Check if exists duplicated distribution date
                    if (cIssComId.equals(aIssComId) && cEffDate.equals(aEffDate)) {
                        isDup = true;
                        break;
                    }
                }
            }
        }

        if (isZero) {
            MessageManager.getInstance().addErrorMessage("pm.process.distribution.zeroData.error");
        }

        if (isDup) {
            MessageManager.getInstance().addErrorMessage("pm.process.distribution.dateExisted.error");
        }

        if (isCalcTransDup) {
            MessageManager.getInstance().addErrorMessage("pm.process.distribution.calcTrans.dateExisted.error");
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages())
            throw new ValidationException("Validate distribution data to save fail.");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllDistribution", null);
        }

        return changedRecords;
    }

    /**
     * Save selected distribution info
     *
     * @param inputRecords distribution info
     * @return
     */
    public void saveAllDistribution(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllDistribution", new Object[]{inputRecords});
        }

        // Validate the input data to check if date is duplicated and exist zero percentage.
        RecordSet changedDistribution = validateAllDistribution(inputRecords);

        if (changedDistribution.getSize() > 0) {
            getDistributionDAO().saveAllDistribution(changedDistribution);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllDistribution", null);
        }
    }

    /**
     * Method that gets the default values for adding distribution
     *
     * @return Record that contains default values
     */
    public Record getInitialValuesForAddDistribution() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForAddDistribution", null);
        }

        Record outputRecord = new Record();

        // set request context as initial values
        DistributionFields.setDividendRuleId(outputRecord, getDbUtilityManager().getNextSequenceNo().toString());
        DistributionFields.setSeqNo(outputRecord, "1");
        DistributionFields.setIssueCompanyEntityId(outputRecord, "1");
        DistributionFields.setIssueStateCode(outputRecord, "ALL");
        DistributionFields.setPolicyTypeCode(outputRecord, "ALL");
        DistributionFields.setPercentage(outputRecord, "0");
        DistributionFields.setIsProcessAvailable(outputRecord, YesNoFlag.Y);
        DistributionFields.setRiskTypeCode(outputRecord, "ALL");
        DistributionFields.setProductCoverageCode(outputRecord, "ALL");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForAddDistribution", outputRecord);
        }

        return outputRecord;
    }

    /**
     * validate the input data for process.
     *
     * @param inputRecord
     * @returm
     */
    protected void validateProcessDistribution(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateProcessDistribution", new Object[]{inputRecord});
        }

        // If update_ind is not N which means existing changes and unsaved, throw exception
        if (!DistributionFields.getIsSaved(inputRecord).equals(UpdateIndicator.NOT_CHANGED)) {
            MessageManager.getInstance().addErrorMessage("pm.process.distribution.unSaved.error");
            throw new ValidationException("Validate distribution data to process fail.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateProcessDistribution", null);
        }

    }

    /**
     * Process Distribution
     *
     * @param inputRecord
     * @return result record which contains parallel quote #
     */
    public void processDistribution(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDistribution", new Object[]{inputRecord});
        }

        validateProcessDistribution(inputRecord);

        // Process distribution
        getDistributionDAO().processDistribution(inputRecord);

        //successful message feed back 
        MessageManager.getInstance().addInfoMessage("pm.process.distribution.process.success");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDistribution", null);
        }

    }

    /**
     * Catch up dividend when new rule is declared in new calendar year.
     *
     * @param inputRecord
     * @return
     */
    public void processCatchUp(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCatchUp", new Object[]{inputRecord});
        }

        // Catch up dividend
        getDistributionDAO().processCatchUp(inputRecord);

        //successful message feed back
        MessageManager.getInstance().addInfoMessage("pm.process.distribution.catchUp.success");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCatchUp", null);
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    /**
     * Verify distributionDAO and dbUtilityManager in spring config
     */
    public void verifyConfig() {
        if (getDistributionDAO() == null)
            throw new ConfigurationException("The required property 'distributionDAO' is missing.");
        if (getDbUtilityManager() == null)
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public DistributionDAO getDistributionDAO() {
        return m_distributionDAO;
    }

    public void setDistributionDAO(DistributionDAO distributionDAO) {
        m_distributionDAO = distributionDAO;
    }

    private DistributionDAO m_distributionDAO;
    private DBUtilityManager m_dbUtilityManager;
}
