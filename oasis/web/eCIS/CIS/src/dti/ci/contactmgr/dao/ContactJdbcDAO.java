package dti.ci.contactmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for getting data about contacts.
 * <p/>
 * <p>(C) 2005 Delphi Technology, inc. (dti)</p>
 * Date: Sep 22, 2005
 *
 * @author Hong Yuan
 */
/*
* Revision Date    Revised By  Description
* --------------------------------------------------------------------
* 08/16/2007       kshen       Format phone, ssn to display.
* 09/25/2008       Jacky       the entity's primary address should be the default address of the contact information
* 03/23/2010      Kenney      Modified getRetrieveDataResultSetSQL to take advantage of phone number format enh
* 05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
* --------------------------------------------------------------------
*/

public class ContactJdbcDAO extends BaseDAO implements ContactDAO {
    /**
     * Load the contacts of an entity.
     *
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    @Override
    public RecordSet loadAllContact(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllContact", new Object[]{inputRecord, loadProcessor});
        }

        boolean isHubEnabled = isHubEnabled();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance(isHubEnabled ? "Ci_Web_Contact_H.Sel_Contact_List" : "Ci_Web_Contact.Sel_Contact_List");
        RecordSet rs = null;

        try {
            rs = spDao.execute(inputRecord, loadProcessor);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load contacts.", e);
            l.throwing(getClass().getName(), "loadAllContact", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllContact", rs);
        }

        return rs;
    }

    /**
     * Save the contacts of an entity.
     *
     * @param rs
     * @return
     */
    @Override
    public int saveAllContact(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllContact", new Object[]{rs});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Contact.Save_Contact");
        int count = 0;

        try {
            count = spDao.executeBatch(rs);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save contacts.", e);
            l.throwing(getClass().getName(), "saveAllContact", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllContact", count);
        }

        return count;
    }

    /**
     * Save contact number
     *
     * @param inputRecord
     */
    public Record saveContact(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "saveContact";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Contact.save_contact_for_service");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save ? : " + inputRecord, se);
            l.throwing(getClass().getName(), "saveContact", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }
}
