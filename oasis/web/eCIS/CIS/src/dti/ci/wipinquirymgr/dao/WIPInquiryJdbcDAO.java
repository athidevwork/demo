package dti.ci.wipinquirymgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for getting data about WIP Inquiry.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Dec 12, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * --------------------------------------------------------------------
 * 04/17/2018       dpang       Issue 192648. Refactor WIP Inquiry.
 * --------------------------------------------------------------------
 */

public class WIPInquiryJdbcDAO extends BaseDAO implements WIPInquiryDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadWIPInquiry(Record inputRecord) {
        String methodName = "loadWIPInquiry";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Cs_Sel_Wip_Inquiry_List");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, CI_GENERIC_ERROR, getClass().getName(), methodName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    @Override
    public String getClientName(Record inputRecord) {
        String methodName = "getClientName";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        RecordSet rs = null;
        String clientName = null;
        try {
            StoredProcedureDAO sp = StoredProcedureDAO.getInstance("cs_ci_get_name");
            rs = sp.execute(inputRecord);
            clientName = rs.getSummaryRecord().getStringValue("name");
        } catch (SQLException e) {
            handleSQLException(e, CI_GENERIC_ERROR, getClass().getName(), methodName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, clientName);
        }
        return clientName;
    }

    private static final String CI_GENERIC_ERROR ="ci.generic.error";
}
