package dti.ci.emailaddressmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * The action classes for get email addresses of a client.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 1, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class EmailAddressJdbcDAO extends BaseDAO implements EmailAddressDAO {
    public String getClientEmailAddress(Long clientId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getClientEmailAddress", new Object[]{clientId});
        }
        String emailAddress = null;
        try {
            Record inputRecord = new Record();
            inputRecord.setFieldValue("entityId", clientId);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_client_utility.sel_email_address");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize()==1) {
                emailAddress = rs.getFirstRecord().getStringValue("emailAddress");
            }
        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to get client email address.", e);
            l.throwing(getClass().getName(), "getClientEmailAddress", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getClientEmailAddress", emailAddress);
        }
        return emailAddress;
    }

    /**
     * Get all email addresses of an client
     *
     * @param record
     * @return
     */
    public Record getAllClientEmailAddress(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllClientEmailAddress", new Object[]{record});
        }

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("wb_client_utility.sel_all_email_address");
            RecordSet rs = spDao.execute(record);
            if (rs.getSize()>0) {
                return rs.getFirstRecord();
            } else {
                return new Record();
            }

        } catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to get client email address.", e);
            l.throwing(getClass().getName(), "getClientEmailAddress", ae);
            throw ae;
        }
    }

    public EmailAddressJdbcDAO() {
    }
}
