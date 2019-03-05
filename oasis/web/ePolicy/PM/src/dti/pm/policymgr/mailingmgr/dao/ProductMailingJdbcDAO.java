package dti.pm.policymgr.mailingmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the DAO ProductMailingDAO
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class ProductMailingJdbcDAO extends BaseDAO implements ProductMailingDAO {

    /**
     * This method used to save product mailing data.
     *
     * @param inputRecordSet
     * @return
     */
    public int saveProductMailingInfo(RecordSet inputRecordSet) {
        Logger l = LogUtils.enterLog(getClass(), "saveProductMailingInfo", new Object[]{inputRecordSet});

        int updateCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyFkList", "issueCompanyIdList"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Product_Mailing.Save_Product_Mailing", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecordSet);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save product mailing.", e);
            l.throwing(getClass().getName(), "saveProductMailingInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveProductMailingInfo", new Integer(updateCount));
        return updateCount;
    }

    /**
     * This method used to load all product mailing.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllProductMailing(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllProductMailing", new Object[]{inputRecord});

        RecordSet rs = null;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Product_Mailing.Sel_Product_Mailing");
        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load product mailing information ", e);
            l.throwing(getClass().getName(), "loadAllProductMailing", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProductMailing", rs);
        }
        return rs;
    }

    public ProductMailingJdbcDAO() {
    }
}
