package dti.ci.summarymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 16, 2008
 */
/*
 * CIS Summary Data Access Object Oracle Implementation
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/24/2009       Jacky       Modified for issue 97894
 * 09/22/2014       htwang      154459 - Modify loadAllAccountsByEntity to not auto-commit due to SQL cursor is opened
 *                                       based on a global temporary table.
 * ---------------------------------------------------
 */
public class SummaryJdbcDAO extends BaseDAO implements SummaryDAO {
    private static final String STORED_PROC_SEL_POLQTE_INFO = "Pm_Sel_Policy_Current_List";
    private static final String STORED_PROC_SEL_ACCOUNT_INFO = "WB_CI_TABS.sel_summary_account";
    private static final String STORED_PROC_SEL_ACCOUNT_BILLING_INFO = "WB_CI_TABS.sel_account_billings";
    private static final String STORED_PROC_SEL_CLAIMS_INFO = "WB_CI_TABS.sel_summary_claims";      
    private static final String STORED_PROC_SEL_COMBINED_POLQTE_INFO = "WB_CI_TABS.get_combined_policy_list";
    private static final String STORED_PROC_SEL_COMBINED_RISK_INFO = "WB_CI_TABS.get_combined_risk_list";

    /**
     * Load Policy CurrentList through entity PK , used by CI Summary page
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadPolicyCurrentListByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllPolandQteByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_POLQTE_INFO);
            rs = spDao.execute(record, recordLoadProcessor);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Policy and Quote information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }
   /**
     * Load Policy CurrentList through entity PK , used to launch popup page from entity list
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllPolandQteByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllPolandQteByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            // new policy holder search function
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("WB_CI_TABS.get_ent_policy_holder_list");
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Policy and Quote information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

    /**
     * Load all Entity's Policy and Quote through entity PK including MiniPolicy
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadCombinedPolandQteByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadCombinedPolandQteByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_COMBINED_POLQTE_INFO);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Combined Policy and Quote information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

     /**
     * Load all Entity's Risks through entity PK including MiniPolicy
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadCombinedRiskByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadCombinedRisksByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_COMBINED_RISK_INFO);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Combined Risk information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

    /**
     * Load all Entity's Account through entity PK
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllAccountsByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllAccountsByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_ACCOUNT_INFO);
            rs = spDao.executeReadonly(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Account information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

    /**
     * Load all Account's Billings through billing account PK and policy term history fk
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet performAllBillingsByAccountAndPolicy(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "performAllBillingsByAccountAndPolicy";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_ACCOUNT_BILLING_INFO);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Billing information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

    /**
     * Load all Entity's Claims through entity PK
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllClaimsByEntity(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllClaimsByEntity";
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(STORED_PROC_SEL_CLAIMS_INFO);
            rs = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, rs);
            }
            return rs;

        } catch (
                SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to load Summary Claims information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }   
}
