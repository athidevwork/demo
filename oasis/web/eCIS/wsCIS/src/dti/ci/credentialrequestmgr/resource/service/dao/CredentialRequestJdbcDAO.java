package dti.ci.credentialrequestmgr.resource.service.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/12/2019
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CredentialRequestJdbcDAO implements CredentialRequestDAO {
    ////////////////Credential Letter Restful Implementation/////////////////

    @Override
    public RecordSet saveCredentialRequest(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveRequest", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_cred_req.save_cred_req");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to save credential request.", e);
            l.throwing(getClass().getName(), "saveCredentialRequest", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveRequest");
        }

        return rs;
    }

    @Override
    public RecordSet getRequestStatus(String requestId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRequestStatus", new Object[]{requestId});
        }

        Record record = new Record();
        record.setFieldValue("requestId", requestId);

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_cred_req.get_cred_req_status");
            rs = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to get request status.", e);
            l.throwing(getClass().getName(), "getRequestStatus", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRequestStatus");
        }

        return rs;
    }

    @Override
    public RecordSet getRequestorStatus(String requestorId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRequestorStatus", new Object[]{requestorId});
        }

        Record record = new Record();
        record.setFieldValue("requestorId", requestorId);

        RecordSet rs = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_cred_req.GET_REQUESTOR_STATUS");
            rs = spDao.execute(record);
        }
        catch (SQLException e) {
            AppException ae =
                    ExceptionHelper.getInstance().handleException("Unable to get requestor status.", e);
            l.throwing(getClass().getName(), "getRequestStatus", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRequestorStatus");
        }

        return rs;
    }

    @Override
    public String validateEntity(long requestorId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEntity", new Object[]{requestorId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", requestorId);
        String result = null;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_cred_req.VALIDATE_ENTITY");
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            throw new AppException("Unable to validate requestor", e);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateEntity",  new Object[]{result});
        }
        return result;
    }

    @Override
    public RecordSet getCacheDefaults(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCacheDefaults", new Object[]{});
        }

        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_cred_req.Get_Cache_Defaults");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            throw new AppException("Unable to get policy defaults", e);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCacheDefaults");
        }
        return rs;
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
