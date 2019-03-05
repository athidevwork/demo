package dti.pm.componentmgr.experiencemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.pm.core.dao.BaseDAO;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * This class implements the ExperienceComponentDAO interface. This is consumed by any business logic objects
 * that require information about ExperienceComponent.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 24, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ExperienceComponentJdbcDAO extends BaseDAO implements ExperienceComponentDAO {

  
    /**
        * To process experience component and load failed policies
        *
        * @param inputRecord with user entered search criteria
        * @return RecordSet
        */
       public RecordSet loadAllExperienceDetail(Record inputRecord) {
           Logger l = LogUtils.getLogger(getClass());
           if (l.isLoggable(Level.FINER)) {
               l.entering(getClass().getName(), "loadAllExperienceDetail", new Object[]{inputRecord});
           }

           RecordSet rs;
           StoredProcedureDAO spDAO = StoredProcedureDAO.getInstance("Pm_Exp_Discount.Process_Experience_Component");

           try {
               rs = spDAO.execute(inputRecord);

           } catch (SQLException e) {
               AppException ae = ExceptionHelper.getInstance().handleException("Unable to load failed policies", e);
               l.throwing(getClass().getName(), "loadAllExperienceDetail", ae);
               throw ae;
           }

           l.exiting(getClass().getName(), "loadAllExperienceDetail", rs);
           return rs;
       }
     
}
