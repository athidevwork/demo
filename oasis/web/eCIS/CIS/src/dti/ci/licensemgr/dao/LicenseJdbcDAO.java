package dti.ci.licensemgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for License information
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 17, 2012
 *
 * @author parker
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/30/2016       dpang       Issue 149588: retrieve hub data if needed.
 * ---------------------------------------------------
*/
public class LicenseJdbcDAO extends BaseDAO implements LicenseDAO {

    /**
     * load license information.
     * @param record
     * @return
     */
    @Override
    public RecordSet loadLicense(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadLicense", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance(isHubEnabled() ? "Ci_Web_License_H.load_license" : "ci_web_license.load_license");
        try {
            RecordSet rs = sp.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
            l.entering(getClass().getName(), "loadLicense", new Object[]{rs});
            return rs;
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load License", se);
            l.throwing(getClass().getName(), "loadLicense", ae);
            throw ae;
        }
    }
    
    /**
     * save license information.
     * @param record
     * @return
     */
    @Override
    public int saveLicense(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveLicense", new Object[]{rs});
        }
        int updateCount = StoredProcedureTemplate.doBatchUpdate("ci_web_license.update_license", rs);
        l.exiting(getClass().getName(), "saveLicense", new Integer(updateCount));

        return updateCount;
    }

    /**
     * Save License
     *
     * @param inputRecord
     */
    public Record saveLicense(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "saveLicense";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_license.update_license_for_service");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save License : " + inputRecord, se);
            l.throwing(getClass().getName(), "saveLicense", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }

}
