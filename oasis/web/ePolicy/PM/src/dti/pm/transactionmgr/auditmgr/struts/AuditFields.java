package dti.pm.transactionmgr.auditmgr.struts;

/**
 * Constants for Audit
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2007
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
public class AuditFields {
    //additional sql for policy page component form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_POl_POl_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) ";
    //additional sql for policy page component form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_POL_COM_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) \n" +
        "AND oasis_audit_trail.operation_pk IN (SELECT pcc.POLICY_COV_COMPONENT_PK                FROM  policy p , policy_coverage_component pcc, transaction_log t2,   transaction_log  t1  \n" +
        "WHERE  pcc.coverage_base_record_fk  = p.policy_pk \n" +
        "AND  pcc.transaction_log_fk  = t2.transaction_log_pk  \n" +
        "AND  t2.policy_fk  = t1.policy_fk  \n" +
        "AND  t1.transaction_log_pk = :l_trans_fk ) ";
    //additional sql for policy page component form and audit level is "TRANSACTION"
    public static final String ADDI_SQL_FOR_POl_COM_TRA = "AND oasis_audit_trail.operation_pk IN (SELECT pcc.POLICY_COV_COMPONENT_PK                FROM  policy p , policy_coverage_component pcc, transaction_log t2,   transaction_log  t1  \n" +
        "WHERE  pcc.coverage_base_record_fk  = p.policy_pk \n" +
        "AND  pcc.transaction_log_fk  = t2.transaction_log_pk  \n" +
        "AND  t2.policy_fk  = t1.policy_fk  \n" +
        "AND  t1.transaction_log_pk = :l_trans_fk )  ";
    //additional sql for policy page component form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_POl_COM_CON = "AND oasis_audit_trail.operation_pk IN (SELECT pcc.POLICY_COV_COMPONENT_PK                FROM  policy p , policy_coverage_component pcc, transaction_log t2,   transaction_log  t1  \n" +
        "WHERE  pcc.coverage_base_record_fk  = p.policy_pk \n" +
        "AND  pcc.transaction_log_fk  = t2.transaction_log_pk  \n" +
        "AND  t2.policy_fk  = t1.policy_fk  \n" +
        "AND  t1.transaction_log_pk = :l_trans_fk ) \n" +
        "AND oasis_audit_trail.operation_pk in (SELECT pcc2.policy_cov_component_pk        FROM policy_coverage_component pcc2, policy_coverage_component pcc1   \n" +
        "WHERE pcc2.pol_cov_comp_base_rec_fk = pcc1.pol_cov_comp_base_rec_fk     \n" +
        "AND pcc1.policy_cov_component_pk = :l_policy_cov_component_pk) ";
    //additional sql for risk page risk form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_RISK_RISK_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) ";
    //additional sql for risk page risk form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_RISK_RISK_CON = "AND oasis_audit_trail.operation_pk in (SELECT r2.risk_pk   \n" +
        "FROM risk r2, risk r1  \n" +
        "WHERE r2.risk_base_record_fk = r1.risk_base_record_fk  \n" +
        "AND r1.risk_pk = :l_risk_pk )";
    //additional sql for coverage page coverage form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_COVG_COVG_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) \n" +
        "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk) ";

    //additional sql for coverage page coverage form and audit level is "TRANASACTION"
    public static final String ADDI_SQL_FOR_COVG_COVG_TRA = "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk)";
    //additional sql for coverage page coverage form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_COVG_COVG_CON = "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk)\t \n" +
        "AND oasis_audit_trail.operation_pk in (SELECT c2.coverage_pk   \n" +
        "FROM coverage c2, coverage c1  \n" +
        "WHERE c2.coverage_base_record_fk = c1.coverage_base_record_fk   \n" +
        "AND c1.coverage_pk = :l_coverage_pk ) ";
    //additional sql for coverage page component form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_COVG_COM_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk)" ;
    //additional sql for coverage page component form and audit level is "TRANSACTION"
    public static final String ADDI_SQL_FOR_COVG_COM_TRA = "AND oasis_audit_trail.operation_pk IN (SELECT pcc.POLICY_COV_COMPONENT_PK      FROM  coverage c , policy_coverage_component pcc, transaction_log t2,   transaction_log  t1  \n" +
        "WHERE  pcc.coverage_base_record_fk  = c.coverage_pk \n" +
        "AND  pcc.transaction_log_fk  = t2.transaction_log_pk  \n" +
        "AND  t2.policy_fk  = t1.policy_fk  \n" +
        "AND  t1.transaction_log_pk = :l_trans_fk ) ";
    //additional sql for coverage page component form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_COVG_COM_CON =  "AND oasis_audit_trail.operation_pk in (SELECT pcc2.policy_cov_component_pk        FROM policy_coverage_component pcc2, policy_coverage_component pcc1   \n" +
        "WHERE pcc2.pol_cov_comp_base_rec_fk = pcc1.pol_cov_comp_base_rec_fk   \n" +
        "AND pcc1.policy_cov_component_pk = :l_policy_cov_component_pk) ";
    //additional sql for coverage class page class form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_COVGCLASS_CLASS_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) \n" +
        "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is not null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk) ";
    //additional sql for coverage class page class form and audit level is "TRANSACTION"
    public static final String ADDI_SQL_FOR_COVGCLASS_CLASS_TRA = "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is not null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk)";

    //additional sql for coverage class page class form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_COVGCLASS_CLASS_CON = "AND oasis_audit_trail.operation_pk in (SELECT cc.coverage_pk   \n" +
        "FROM coverage cc, risk r,  transaction_log t   \n" +
        "WHERE cc.parent_coverage_base_record_fk is not null  \n" +
        "AND r.risk_pk            = cc.risk_base_record_fk  \n" +
        "AND t.policy_fk          = r.policy_fk     \n" +
        "AND t.transaction_log_pk = :l_trans_fk)\t \n" +
        "AND oasis_audit_trail.operation_pk in (SELECT c2.coverage_pk   \n" +
        "FROM coverage c2, coverage c1  \n" +
        "WHERE c2.coverage_base_record_fk = c1.coverage_base_record_fk   \n" +
        "AND c1.coverage_pk = :l_coverage_pk ) ";

    //additional sql for renewal flag page flag form and audit level is "ALL"
    public static final String ADDI_SQL_FOR_RENEWAL_FLAG_FLAG_ALL = "AND oasis_audit_trail.source_record_fk IN (SELECT t2.transaction_log_pk  \n" +
        "FROM transaction_log t2,  transaction_log t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND t1.transaction_log_pk = :l_trans_fk) ";
    //additional sql for renewal flag page flag form and audit level is "CONTEXT"
    public static final String ADDI_SQL_FOR_RENEWAL_FLAG_FLAG_CON = "AND oasis_audit_trail.operation_pk IN (SELECT t2.policy_renewal_flag_pk  \n" +
        "FROM policy_renewal_flag t2,  policy_renewal_flag t1 \n" +
        "WHERE t2.policy_fk = t1.policy_fk \n" +
        "AND NVL(t2.risk_base_record_fk, 0) = NVL(t1.risk_base_record_fk, 0) \n" +
        "AND t2.flag_code = t1.flag_code \n" +
        "AND t1.policy_renewal_flag_pk = :l_renewal_flag_pk) ";
}
