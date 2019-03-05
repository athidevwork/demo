package dti.ci.credentialrequestmgr.dao;

import dti.ci.core.error.ExpMsgConvertor;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DAO component of Credential Request Fee Adjustment.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  03/04/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CredentialFeeAdjustJdbcDAO implements CredentialFeeAdjustDAO {

    /**
     * Load Service Charges for the account
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllServiceCharges(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllServiceCharges", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.sel_cred_fees");
        try {
            rs = spDAO.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "loadAllServiceCharges", e);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllServiceCharges", rs);
        }

        return rs;
    }

    /**
     * Process Reversal for selected Service Charges.
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllServiceCharges(RecordSet inputRecords){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllServiceCharges", new Object[]{inputRecords});
        }

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("CI_Web_Cred_Req.Create_Fee_Reversal");
        int count = 0;
        try {
            count = spDAO.executeBatch(inputRecords);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "saveAllServiceCharges", count);
            }
        } catch (SQLException e) {
            MessageManager.getInstance().addErrorMessage("ci.generic.error",
                    new String[] {ExpMsgConvertor.trimSQLException(e)});
            l.throwing(getClass().getName(), "saveAllServiceCharges", e);
        }
        return count;
    }
}
