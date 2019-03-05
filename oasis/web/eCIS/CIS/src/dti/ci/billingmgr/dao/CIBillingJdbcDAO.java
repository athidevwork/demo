package dti.ci.billingmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * JDBC implementation of CIBillingDAO
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 27, 2009
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class CIBillingJdbcDAO extends BaseDAO implements CIBillingDAO {
    /**
     * To load all accounts by given entityId.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAccount(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAccount", new Object[]{inputRecord});

        RecordSet rs;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "pk"));

        StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("FM_Web_Account.Sel_All_Account", mapping);

        try {
            rs = spDAO.execute(inputRecord);

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all account", e);
            l.throwing(getClass().getName(), "loadAllAccount", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllAccount", rs);
        return rs;
    }
}
