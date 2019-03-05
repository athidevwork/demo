package dti.pm.policyattributesmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class implements the PmAttributeDAO interface.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/01/16 wdang   167534 - Initial version.
 * ---------------------------------------------------
 */
public class PolicyAttributesJdbcDAO extends BaseDAO implements PolicyAttributesDAO {

    @Override
    public RecordSet loadPmAttribute(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPmAttribute", inputRecord);

        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Get_Pm_Attribute");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load PmAttribute.", e);
            l.throwing(getClass().getName(), "loadPmAttribute", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPmAttribute", rs);
        return rs;
    }
}
