package dti.ci.certificationmgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.core.dao.StoredProcedureTemplate;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for Certification information
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
public class CertificationJdbcDAO extends BaseDAO implements CertificationDAO {

    /**
     * load certification information.
     * @param record
     * @return
     */
    @Override
    public RecordSet loadCertification(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCertification", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance(isHubEnabled() ? "ci_web_risk_class_profile_h.load_certification" : "ci_web_certification.load_certification");
        try {
            RecordSet rs = sp.execute(inputRecord, AddSelectIndLoadProcessor.getInstance());
            l.entering(getClass().getName(), "loadCertification", new Object[]{rs});
            return rs;
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Certification", se);
            l.throwing(getClass().getName(), "loadCertification", ae);
            throw ae;
        }
    }
    
    /**
     * save certification information.
     * @param record
     * @return
     */
    @Override
    public int saveCertification(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCertification", new Object[]{rs});
        }
        int updateCount = StoredProcedureTemplate.doBatchUpdate("ci_web_certification.update_certification", rs);
        l.exiting(getClass().getName(), "saveCertification", new Integer(updateCount));

        return updateCount;
    }

    /**
     * Retrieve the entity's date of birth.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  dateOfBirth
     * @throws Exception
     */
    @Override
    public String getDateOfBirth(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDateOfBirth", new Object[]{entityId});
        }
        String  dateOfBirth = "";
        try {
            Record inputRecord = new Record();
            inputRecord.setFieldValue("entityId", entityId);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_certification.get_date_of_birth");
            Record summaryRecord =spDao.execute(inputRecord).getSummaryRecord(); 
            if (summaryRecord != null) {
                dateOfBirth = summaryRecord.getStringValue("dateOfBirth");
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get DateOfBirth.", e);
            l.throwing(getClass().getName(), "getDateOfBirth", ae);
            throw ae;
        }
        l.entering(getClass().getName(), "getDateOfBirth", new Object[]{dateOfBirth});
        return dateOfBirth;
    }

    /**
     * Retrieve the constantTypeCode.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  constantType
     * @throws Exception
     */
    @Override
    public String getConstant(String constantTypeCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getConstant", new Object[]{constantTypeCode});
        }
        String  constantType = "";
        try {
            Record inputRecord = new Record();
            inputRecord.setFieldValue("constantTypeCode", constantTypeCode);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_certification.get_constant");
            Record summaryRecord =spDao.execute(inputRecord).getSummaryRecord();
            if (summaryRecord != null) {
                constantType = summaryRecord.getStringValue("constantValue");
            }
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get constantTypeCode.", e);
            l.throwing(getClass().getName(), "getConstant", ae);
            throw ae;
        }
        l.entering(getClass().getName(), "getConstant", new Object[]{constantType});
        return constantType;
    }

    /**
     * Save Certification
     *
     * @param inputRecord
     */
    public Record saveCertification(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "saveCertification";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        Record recResult = new Record();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("ci_web_certification.update_certification_service");
        try {
            recResult = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to save Certification : " + inputRecord, se);
            l.throwing(getClass().getName(), "saveCertification", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName);
        }
        return recResult;
    }
}
