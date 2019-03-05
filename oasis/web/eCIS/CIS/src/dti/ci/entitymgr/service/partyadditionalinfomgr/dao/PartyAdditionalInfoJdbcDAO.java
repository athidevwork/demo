package dti.ci.entitymgr.service.partyadditionalinfomgr.dao;

import dti.ci.core.dao.BaseDAO;
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
 * Date:   5/3/2017
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
public class PartyAdditionalInfoJdbcDAO extends BaseDAO implements PartyAdditionalInfoDAO{
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record loadAddressAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressAdditionalInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Load_Address_Addl_Info");

        try {
            Record result = null;
            RecordSet rs = spDao.execute(inputRecord);

            if (rs.getSize() > 0) {
                result = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAddressAdditionalInfo", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load address additional info.", e);
            l.throwing(getClass().getName(), "loadAddressAdditionalInfo", ae);
            throw ae;
        }
    }

    @Override
    public Record loadPersonAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPersonAdditionalInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Load_Person_Addl_Info");

        try {
            Record result = null;
            RecordSet rs = spDao.execute(inputRecord);

            if (rs.getSize() > 0) {
                result = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadPersonAdditionalInfo", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load person additional info.", e);
            l.throwing(getClass().getName(), "loadPersonAdditionalInfo", ae);
            throw ae;
        }
    }

    @Override
    public Record loadOrganizationAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOrganizationAdditionalInfo", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Load_Org_Addl_Info");

        try {
            Record result = null;
            RecordSet rs = spDao.execute(inputRecord);

            if (rs.getSize() > 0) {
                result = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadOrganizationAdditionalInfo", result);
            }
            return result;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load org additional info.", e);
            l.throwing(getClass().getName(), "loadOrganizationAdditionalInfo", ae);
            throw ae;
        }
    }

    @Override
    public void saveAdditionalXmlData(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAdditionalXmlData", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Save_Addl_Xml_Data");

        try {
            spDao.executeUpdate(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save additional xml data.", e);
            l.throwing(getClass().getName(), "saveAdditionalXmlData", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAdditionalXmlData");
    }

    @Override
    public void saveAddressAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressAdditionalInfo", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Save_Address_Addl_Info");

        try {
            spDao.executeUpdate(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save address additional info.", e);
            l.throwing(getClass().getName(), "saveAddressAdditionalInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAddressAdditionalInfo");
    }

    @Override
    public void savePersonAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePersonAdditionalInfo", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Save_Person_Addl_Info");

        try {
            spDao.executeUpdate(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save person additional info.", e);
            l.throwing(getClass().getName(), "savePersonAdditionalInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "savePersonAdditionalInfo");
    }

    @Override
    public void saveOrganizationAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveOrganizationAdditionalInfo", new Object[]{record});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Addl_Info.Save_Org_Addl_Info");

        try {
            spDao.executeUpdate(record);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save org additional info.", e);
            l.throwing(getClass().getName(), "saveOrganizationAdditionalInfo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveOrganizationAdditionalInfo");
    }
}
