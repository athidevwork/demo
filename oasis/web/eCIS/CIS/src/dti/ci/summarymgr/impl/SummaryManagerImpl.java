package dti.ci.summarymgr.impl;

import dti.ci.helpers.ICIConstants;
import dti.ci.claimsmgr.impl.ClaimsManagerImpl;
import dti.ci.summarymgr.SummaryFields;
import dti.ci.summarymgr.SummaryManager;
import dti.ci.summarymgr.dao.SummaryDAO;
import dti.cs.securitymgr.ClaimSecurityManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 16, 2008
 */
/*
 * CIS Summary Business Layer Object Implementation
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  10/14/2009       Jacky       Add 'Jurisdiction' logic for issue #97673
 *  10/29/2009       Fred        Refactor code based on review
 *  02/01/2011       Guang      116490: call loadAllPolandQteByEntity conditionally
 * ---------------------------------------------------
 */
public class SummaryManagerImpl implements SummaryManager {
    /**
     * Load all Entity's Policy and Quote
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPolandQteByEntity(Record inputRecord) {
        String methodName = "loadAllPolandQteByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }

        RecordSet rs = null;
        //Set correct parameter to invoke the DAO
        inputRecord.setFieldValue(SummaryFields.STORED_PROC_PARA_ENTITY_PK_NAME, inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;

        //116490: should really call loadAllPolandQteByEntity only if from entity list page.
        if (!inputRecord.hasStringValue("getPolicyListFromEntityList") || "N".equalsIgnoreCase(inputRecord.getStringValue("getPolicyListFromEntityList"))) {
            rs = getSummaryDAO().loadPolicyCurrentListByEntity(inputRecord, entitlementRLP);
        } else {
            rs = getSummaryDAO().loadAllPolandQteByEntity(inputRecord, entitlementRLP);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     * Load all Entity's Policy and Quote including MiniPolicy
     * @param inputRecord
     * @return
     */
    public RecordSet loadCombinedPolandQteByEntity(Record inputRecord) {
        String methodName = "loadCombinedPolandQteByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        //Set correct parameter to invoke the DAO
        inputRecord.setFieldValue(SummaryFields.STORED_PROC_PARA_ENTITY_PK_NAME, inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getSummaryDAO().loadCombinedPolandQteByEntity(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

     /**
     * Load all Entity's Risks including MiniPolicy
     * @param inputRecord
     * @return
     */
    public RecordSet loadCombinedRiskByEntity(Record inputRecord) {
        String methodName = "loadCombinedRiskByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        //Set correct parameter to invoke the DAO
        inputRecord.setFieldValue(SummaryFields.STORED_PROC_PARA_ENTITY_PK_NAME, inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getSummaryDAO().loadCombinedRiskByEntity(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     * Load all Entity's Account
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAccountsByEntity(Record inputRecord) {
        String methodName = "loadAllAccountsByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        //Set correct parameter to invoke the DAO
        inputRecord.setFieldValue(SummaryFields.STORED_PROC_PARA_ENTITY_PK_NAME, inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getSummaryDAO().loadAllAccountsByEntity(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     *  Load all Account's Billings. DB transaction needed, so start with perform_
     * @param inputRecord
     * @return
     */
    public RecordSet performAllBillingsByAccountAndPolicy(Record inputRecord) {
        String methodName = "performAllBillingsByAccountAndPolicy";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getSummaryDAO().performAllBillingsByAccountAndPolicy(inputRecord, entitlementRLP);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    /**
     * Load all Entity's Claims
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllClaimsByEntity(Record inputRecord) {
        String methodName = "loadAllClaimsByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        //Set correct parameter to invoke the DAO
        inputRecord.setFieldValue(SummaryFields.STORED_PROC_PARA_ENTITY_PK_NAME, inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));
        /* Setup the entitlements load processor */
        RecordLoadProcessor entitlementRLP = DefaultRecordLoadProcessor.DEFAULT_INSTANCE;
        RecordSet rs = getSummaryDAO().loadAllClaimsByEntity(inputRecord, entitlementRLP);

        // jld 10/22/09 - Issue 97673 Security filtering is not yet working. Comment out for build 2010.1.0.0.6
//        rs = filterRecordSetByJurisdiction("claimId", "cmJurisdictionCode", rs,true);
        for (int i = 0; i < rs.getSize(); i++) {
            Record rd = rs.getRecord(i);
            String claimNo = rd.getStringValue("claimNo"); // claim no
            rd.setFieldValue("sourceTableName", "CLAIM");
            rd.setFieldValue("sourceNo", claimNo);
        }

        rs = getClaimSecurityManager().filterRecordSetViaJurisdiction(rs);

        //Set total amount for claims into summary record
        int intCount = rs.getSize();
        double lngTotalIndPaid = 0, lngTotalOutInd = 0, lngTotalExpPaid = 0, lngTotalOutExp = 0;
        Iterator iter = rs.getRecords();
        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            lngTotalIndPaid += SummaryFields.getIndPaid(rec).doubleValue();
            lngTotalOutInd += SummaryFields.getOutInd(rec).doubleValue();
            lngTotalExpPaid += SummaryFields.getExpPaid(rec).doubleValue();
            lngTotalOutExp += SummaryFields.getOutExp(rec).doubleValue();
        }
        SummaryFields.setTotalClaimsCount(rs.getSummaryRecord(), new Integer(intCount));
        SummaryFields.setTotalExpPaid(rs.getSummaryRecord(), new Double(lngTotalExpPaid));
        SummaryFields.setTotalIndPaid(rs.getSummaryRecord(), new Double(lngTotalIndPaid));
        SummaryFields.setTotalOutExp(rs.getSummaryRecord(), new Double(lngTotalOutExp));
        SummaryFields.setTotalOutInd(rs.getSummaryRecord(), new Double(lngTotalOutInd));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {

        if (getSummaryDAO() == null) {
            throw new ConfigurationException("The required property 'SummaryDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null)
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
    }


    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    public SummaryDAO getSummaryDAO() {
        return summaryDAO;
    }

    public void setSummaryDAO(SummaryDAO summaryDAO) {
        this.summaryDAO = summaryDAO;
    }

    public ClaimSecurityManager getClaimSecurityManager() {
        return claimSecurityManager;
    }

    public void setClaimSecurityManager(ClaimSecurityManager claimSecurityManager) {
        this.claimSecurityManager = claimSecurityManager;
    }

    private SummaryDAO summaryDAO;

    private WorkbenchConfiguration m_workbenchConfiguration;

    private ClaimSecurityManager claimSecurityManager;
}
