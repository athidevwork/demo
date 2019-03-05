package dti.pm.transactionmgr.auditmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.transactionmgr.auditmgr.AuditManager;
import dti.pm.transactionmgr.auditmgr.struts.AuditFields;
import dti.pm.transactionmgr.auditmgr.dao.AuditDAO;
import dti.pm.policymgr.PolicyHeader;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for AuditManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 24, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/15/2015       tzeng       167532 - Add audit for renewal flag.
 * ---------------------------------------------------
 */
public class AuditManagerImpl implements AuditManager {

    /**
     * Retrieves all audit' information
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllAudit(PolicyHeader policyHeader,Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAudit", new Object[]{inputRecord});
        }
        inputRecord.setFieldValue("transactionLogId", policyHeader.getLastTransactionId());
                //get input param for load audit
                Record auditInputRecord = getInputRecordByPageAndAuditLevel(inputRecord);
                inputRecord.setFields(auditInputRecord);
        RecordSet rs = getAuditDAO().loadAllAudit(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAudit", rs);
        }
        return rs;
    }

    public AuditDAO getAuditDAO() {
        return m_auditDAO;
    }

    public void setAuditDAO(AuditDAO auditDAO) {
        m_auditDAO = auditDAO;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAuditDAO() == null)
            throw new ConfigurationException("The required property 'auditDAO' is missing.");
    }

    private AuditDAO m_auditDAO;
     /**
     * get input criteria according auditlevel and formPage
     * @param inputRecord
     * @return
     */
    private Record getInputRecordByPageAndAuditLevel( Record inputRecord) {
         String auditLevel=inputRecord.getStringValue("auditLevel");
        String fromPage = inputRecord.getStringValue("fromPage");
        String transactionLogId = inputRecord.getStringValue("transactionLogId");
        String addiSql = null;
        String filterTable = null;
        String sourceId = null;
        Record resultRecord = new Record();
        //policy page - policy form
        if (fromPage.equals("policy-policy")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('POLICY','POLICY_TERM_HISTORY')";
                addiSql = AuditFields.ADDI_SQL_FOR_POl_POl_ALL.replaceAll(":l_trans_fk", transactionLogId);
            }
            else {
                filterTable = "('POLICY','POLICY_TERM_HISTORY')";
                sourceId = transactionLogId;
            }
        }
        //policy page - component form
        else if (fromPage.equals("policy-component")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                addiSql = AuditFields.ADDI_SQL_FOR_POL_COM_ALL.replaceAll(":l_trans_fk", transactionLogId);

            }
            else if (auditLevel.equals("TRANSACTION")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                sourceId = transactionLogId;
                addiSql = AuditFields.ADDI_SQL_FOR_POl_COM_TRA.replaceAll(":l_trans_fk", transactionLogId);
            }
            else if (auditLevel.equals("CONTEXT")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                String componentId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_POl_COM_CON.replaceAll(":l_trans_fk", transactionLogId).replaceAll(":l_policy_cov_component_pk", componentId);
            }
        }
        //risk page - risk form
        else if (fromPage.equals("risk-risk")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('RISK')";
                addiSql = AuditFields.ADDI_SQL_FOR_RISK_RISK_ALL.replaceAll(":l_trans_fk", transactionLogId);

            }
            else if (auditLevel.equals("TRANSACTION")) {
                filterTable = "('RISK')";
                sourceId = transactionLogId;
            }
            else if (auditLevel.equals("CONTEXT")) {
                filterTable = "('RISK')";
                String riskId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_RISK_RISK_CON.replaceAll(":l_risk_pk", riskId);
            }
        }
        //coverage page - coverage form
        else if (fromPage.equals("coverage-coverage")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('COVERAGE')";
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COVG_ALL.replaceAll(":l_trans_fk", transactionLogId);
            }
            else if (auditLevel.equals("TRANSACTION")) {
                filterTable = "('COVERAGE')";
                sourceId = transactionLogId;
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COVG_TRA.replaceAll(":l_trans_fk", sourceId);

            }
            else if (auditLevel.equals("CONTEXT")) {
                filterTable = "('COVERAGE')";
                String coverageId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COVG_CON.replaceAll(":l_trans_fk", transactionLogId).replaceAll(":l_coverage_pk", coverageId);
            }
        }
        //coverage page - component form
        else if (fromPage.equals("coverage-component")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COM_ALL.replaceAll(":l_trans_fk", transactionLogId);

            }
            else if (auditLevel.equals("TRANSACTION")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                sourceId = transactionLogId;
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COM_TRA.replaceAll(":l_trans_fk", sourceId);
            }
            else if (auditLevel.equals("CONTEXT")) {
                filterTable = "('POLICY_COVERAGE_COMPONENT')";
                String policyCovComponentId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_COVG_COM_CON.replaceAll(":l_trans_fk", transactionLogId).replaceAll(":l_policy_cov_component_pk", policyCovComponentId);
            }
        }
        //coverage class page - class form
        else if (fromPage.equals("coverageclass-class")) {
            if (auditLevel.equals("ALL")) {
                filterTable = "('COVERAGE')";
                addiSql = AuditFields.ADDI_SQL_FOR_COVGCLASS_CLASS_ALL.replaceAll(":l_trans_fk", transactionLogId);

            }
            else if (auditLevel.equals("TRANSACTION")) {
                filterTable = "('COVERAGE')";
                sourceId = transactionLogId;
                addiSql = AuditFields.ADDI_SQL_FOR_COVGCLASS_CLASS_TRA.replaceAll(":l_trans_fk", sourceId);
            }
            else if (auditLevel.equals("CONTEXT")) {
                filterTable = "('COVERAGE')";
                String coverageId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_COVGCLASS_CLASS_CON.replaceAll(":l_trans_fk", transactionLogId).replaceAll(":l_coverage_pk", coverageId);
            }
        }
        //transaction page- transaction info form
        else if (fromPage.equals("transaction-transaction")) {
            sourceId = inputRecord.getStringValue("contextId");
        }
        //renewal flag page- renewal flag form
        else if (fromPage.equals("renewalFlag-renewalFlag")) {
            filterTable = "('POLICY_RENEWAL_FLAG')";
            if (auditLevel.equals("ALL")) {
                addiSql = AuditFields.ADDI_SQL_FOR_RENEWAL_FLAG_FLAG_ALL.replaceAll(":l_trans_fk", transactionLogId);
            }
            else if (auditLevel.equals("TRANSACTION")) {
                sourceId = transactionLogId;
            }
            else if (auditLevel.equals("CONTEXT")) {
                String renewalFlagId = inputRecord.getStringValue("contextId");
                addiSql = AuditFields.ADDI_SQL_FOR_RENEWAL_FLAG_FLAG_CON.replaceAll(":l_renewal_flag_pk", renewalFlagId);
            }
        }
        resultRecord.setFieldValue("addiSql", addiSql);
        resultRecord.setFieldValue("operationTable", filterTable);
        resultRecord.setFieldValue("sourceId", sourceId);
        return resultRecord;
    }

}
