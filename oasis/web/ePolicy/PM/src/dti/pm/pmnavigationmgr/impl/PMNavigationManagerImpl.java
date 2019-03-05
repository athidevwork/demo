package dti.pm.pmnavigationmgr.impl;

import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskManager;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.pmnavigationmgr.PMNavigationManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.session.UserSessionManager;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

import org.apache.struts.util.LabelValueBean;

/**
 * This class provides the implementation details for PM navigation manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/29/2008       sxm         Issue 86880 - append risk type to risk name, append "Selected" to current risk name,
 *                                            and set current value of selected record
 * 09/30/2008       sxm         Issue 86928 - fix slot risk
 * 05/20/2009       yhyang      Issue 93385 - Keep the selected value for navigate section when page is re-loaded.
 * 06/06/2012       awu         Issue 134186 - To set the correct risk id for slot risk in method
 *                                             loadNavigateSourceForCoverage
 * 07/17/2012       sxm         Issue 135029 - Moved the logic of building Risk List for Coverage class to back end
 *                                             to improve performance
 * 07/31/2013       hxu         146027 - Added new logic to get Go To Risk List for Coverage form back end to
 *                                       improve performance.
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 * 08/25/2014       awu         152034 - 1). Modified loadNavigateSourceForCoverage to check the risk header is null or not.
 *                                       2). Modified loadNavigateSourceForCoverageClass to check the risk header is null or not.
 * 10/29/2014       jyang       158446 - Modified loadNavigateSourceForCoverage, combine the risk name with risk type desc.
 * ---------------------------------------------------
 */
public class PMNavigationManagerImpl implements PMNavigationManager {
    /**
     * Load navigate options for Coverage
     *
     * @param policyHeader Instance of the policy header
     * @param inputRecord Record containing policy/risk/term information
     * @param lovOptions ArrayList containing returned navigation options
     * @return String  Value of currently selected record
     */
    public String loadNavigateSourceForCoverage(PolicyHeader policyHeader, Record inputRecord, ArrayList lovOptions) {
        Logger l = LogUtils.enterLog(getClass(), "loadNavigateSourceForCoverage",
            new Object[]{policyHeader, inputRecord});

        // init
        String currentValue = "";
        String currentBaseRecordId = "";

        RecordSet cacheRiskOption = policyHeader.getCacheRiskOption();
        if(cacheRiskOption.getRecordList().size() == 0){
            // get all risks
            cacheRiskOption = getRiskManager().loadAllRiskWithCoverage(policyHeader);
            policyHeader.setCacheRiskOption(cacheRiskOption);
        }

        if (policyHeader.hasRiskHeader()) {
            currentBaseRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
        }
        else if (cacheRiskOption.getSize() > 0) {
            currentBaseRecordId = RiskFields.getRiskBaseRecordId(cacheRiskOption.getRecord(0));
        }

        for (int i=0; i<cacheRiskOption.getRecordList().size(); i++) {
            Record r = cacheRiskOption.getRecord(i);
            String riskBaseRecordId = RiskFields.getRiskBaseRecordId(r);
            String riskId = RiskFields.getRiskId(r);
            String riskName = RiskFields.getRiskName(r);

            // add one entry per base
            if (currentBaseRecordId.equals(riskBaseRecordId)) {
                riskName += " (" + RiskFields.getRiskTypeDesc(r) + ") (Selected)";
                currentValue = riskId;
            }
            else {
                riskName += " (" + RiskFields.getRiskTypeDesc(r) + ")";
            }
            lovOptions.add(new LabelValueBean(riskName, riskId));

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadNavigateSourceForCoverage", new Object[] {currentValue, lovOptions});
        }
        return currentValue;
    }

    /**
     * Load navigate options for Coverage Class
     *
     * @param policyHeader Instance of the policy header
     * @param inputRecord Record containing policy/risk/term information
     * @param lovOptions ArrayList containing returned navigation options
     * @return String  Value of currently selected record
     */
    public String loadNavigateSourceForCoverageClass(PolicyHeader policyHeader, Record inputRecord, ArrayList lovOptions) {
        Logger l = LogUtils.enterLog(getClass(), "loadNavigateSourceForCoverageClass",
            new Object[]{policyHeader, inputRecord});

        // init
        String currentValue = "";
        String policyNavLevelCode = inputRecord.getStringValue("policyNavLevelCode");

        // handle risk level nav
        if (policyNavLevelCode.equals("RISK")) {
            String currentBaseRecordId = "";
            RecordSet cacheCoverageRiskOption = policyHeader.getCacheCoverageRiskOption();
            if (policyHeader.hasRiskHeader()) {
                currentBaseRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
            }
            else if (cacheCoverageRiskOption.getSize() > 0) {
                currentBaseRecordId = RiskFields.getRiskBaseRecordId(cacheCoverageRiskOption.getRecord(0));
            }

            if (cacheCoverageRiskOption.getRecordList().size() == 0) {
                // get all risks
                cacheCoverageRiskOption = getRiskManager().loadAllRiskWithCoverageClass(policyHeader);
                policyHeader.setCacheCoverageRiskOption(cacheCoverageRiskOption);
            }


            for (int i = 0; i < cacheCoverageRiskOption.getRecordList().size(); i++) {
                Record r = cacheCoverageRiskOption.getRecord(i);
                String riskBaseRecordId = RiskFields.getRiskBaseRecordId(r);
                String riskId = RiskFields.getRiskId(r);
                String riskName = RiskFields.getRiskName(r);
                String coverageId = CoverageFields.getCoverageId(r);

                if (currentBaseRecordId.equals(riskBaseRecordId)) {
                    riskName += " ("+RiskFields.getRiskTypeDesc(r)+") (Selected)";
                    currentValue = riskId+ delim +coverageId;
                }
                else {
                    riskName += " ("+RiskFields.getRiskTypeDesc(r)+")";
                }
                lovOptions.add(new LabelValueBean(riskName, riskId+ delim +coverageId));
            }
        }
        // handle coverage level nav
        else if (policyNavLevelCode.equals("COVERAGE")) {
            String riskBaseRecordId = "";
            String riskId = "";
            if (policyHeader.hasRiskHeader()) {
                riskBaseRecordId = policyHeader.getRiskHeader().getRiskBaseRecordId();
                riskId = policyHeader.getRiskHeader().getRiskId();
            }
            // get all coverages
            RecordSet cacheCoverageOption = policyHeader.getCacheCoverageOption();
            if (cacheCoverageOption.getRecordList().size() == 0) {
                if (policyHeader.hasRiskHeader()) {
                    policyHeader.getRiskHeader().setRiskBaseRecordId(null);
                }
                cacheCoverageOption = getCoverageManager().loadAllCoverage(policyHeader).
                    getSubSet(new RecordFilter(CoverageFields.SUB_COVERAGE_AVAILABLE_B, YesNoFlag.Y));
                policyHeader.setCacheCoverageOption(cacheCoverageOption);
            }

            int size = cacheCoverageOption.getSize();

            ArrayList sourceIds = new ArrayList();
            String currentBaseRecordId = "";
            if (policyHeader.hasCoverageHeader()) {
                currentBaseRecordId = policyHeader.getCoverageHeader().getCoverageBaseRecordId();
            }
            else if (cacheCoverageOption.getSize() > 0) {
                currentBaseRecordId = CoverageFields.getCoverageBaseRecordId(cacheCoverageOption.getRecord(0));
            }

            for (int i=0; i<size; i++) {
                Record r = cacheCoverageOption.getRecord(i);
                String coverageId = CoverageFields.getCoverageId(r);
                String coverageBaseRecordId = CoverageFields.getCoverageBaseRecordId(r);
                String coverageDescription = CoverageFields.getProductCoverageDesc(r);

                // add one entry per base excluding coverages that do not have coverage class defined
                if (!sourceIds.contains(coverageBaseRecordId) && riskBaseRecordId.equals(r.getStringValue("riskBaseRecordId"))) {
                    sourceIds.add(coverageBaseRecordId);
                    if (currentBaseRecordId.equals(coverageBaseRecordId)) {
                        coverageDescription += " (Selected)";
                        currentValue = riskId+ delim +coverageId;
                    }
                    lovOptions.add(new LabelValueBean(coverageDescription, riskId+ delim +coverageId));
                }
            }
            if (policyHeader.hasRiskHeader()) {
                policyHeader.getRiskHeader().setRiskBaseRecordId(riskBaseRecordId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadNavigateSourceForCoverageClass", new Object[] {currentValue, lovOptions});
        }
        return currentValue;
    }

    /**
     * Get policy navigation parameters "policyNavLevelCode" and "policyNavSourceId"
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPolicyNavParameters(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyNavParameters", new Object[]{inputRecord});
        }
        Record outputRecord = new Record();
        // Get policyNavLevelCode
        if(inputRecord.hasFieldValue(CoverageFields.POLICY_NAV_LEVEL_CODE)){
            CoverageFields.setPolicyNavLevelCode(outputRecord, CoverageFields.getPolicyNavLevelCode(inputRecord));
            UserSessionManager.getInstance().getUserSession().set(CoverageFields.POLICY_NAV_LEVEL_CODE, CoverageFields.getPolicyNavLevelCode(inputRecord));
        }
        else if(UserSessionManager.getInstance().getUserSession().has(CoverageFields.POLICY_NAV_LEVEL_CODE)){
            CoverageFields.setPolicyNavLevelCode(outputRecord, (String)UserSessionManager.getInstance().getUserSession().get(CoverageFields.POLICY_NAV_LEVEL_CODE));
        }
        // Get policyNavSourceId
        if(inputRecord.hasFieldValue(CoverageFields.POLICY_NAV_SOURCE_ID)){
            CoverageFields.setPolicyNavSourceId(outputRecord, CoverageFields.getPolicyNavSourceId(inputRecord));
            UserSessionManager.getInstance().getUserSession().set(CoverageFields.POLICY_NAV_SOURCE_ID, CoverageFields.getPolicyNavSourceId(inputRecord));
        }
        else if(UserSessionManager.getInstance().getUserSession().has(CoverageFields.POLICY_NAV_SOURCE_ID)){
            CoverageFields.setPolicyNavSourceId(outputRecord, (String)UserSessionManager.getInstance().getUserSession().get(CoverageFields.POLICY_NAV_SOURCE_ID));
        }
        // Get riskNavSourceId
        if(inputRecord.hasFieldValue(CoverageFields.RISK_NAV_SOURCE_ID)){
            CoverageFields.setRiskNavSourceId(outputRecord, CoverageFields.getRiskNavSourceId(inputRecord));
            UserSessionManager.getInstance().getUserSession().set(CoverageFields.RISK_NAV_SOURCE_ID, CoverageFields.getRiskNavSourceId(inputRecord));
        }
        else if(UserSessionManager.getInstance().getUserSession().has(CoverageFields.RISK_NAV_SOURCE_ID)){
            CoverageFields.setRiskNavSourceId(outputRecord, (String)UserSessionManager.getInstance().getUserSession().get(CoverageFields.RISK_NAV_SOURCE_ID));
        }
        // Get coverageNavSourceId
        if(inputRecord.hasFieldValue(CoverageFields.COVERAGE_NAV_SOURCE_ID)){
            CoverageFields.setCoverageNavSourceId(outputRecord, CoverageFields.getCoverageNavSourceId(inputRecord));
            UserSessionManager.getInstance().getUserSession().set(CoverageFields.COVERAGE_NAV_SOURCE_ID, CoverageFields.getCoverageNavSourceId(inputRecord));
        }
        else if(UserSessionManager.getInstance().getUserSession().has(CoverageFields.COVERAGE_NAV_SOURCE_ID)){
            CoverageFields.setCoverageNavSourceId(outputRecord, (String)UserSessionManager.getInstance().getUserSession().get(CoverageFields.COVERAGE_NAV_SOURCE_ID));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyNavParameters", outputRecord);
        }
        return outputRecord;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig
        () {
        if (getRiskManager() == null)
            throw new ConfigurationException("The required property 'riskManager' is missing.");
        if (getCoverageManager() == null)
            throw new ConfigurationException("The required property 'coverageManager' is missing.");
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    private RiskManager m_riskManager;
    private CoverageManager m_coverageManager;

    private static final String delim = ":";
}
