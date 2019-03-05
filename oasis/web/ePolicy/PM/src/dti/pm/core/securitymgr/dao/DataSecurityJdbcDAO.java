package dti.pm.core.securitymgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class provides the implementation details of all DAO operations related to data security.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  04/17/2008      yhyang      Refactory it to use StoredProcedureDAO.
 * ---------------------------------------------------
 */
public class DataSecurityJdbcDAO extends BaseDAO implements DataSecurityDAO {

    /**
     * Returns a security rule that indicates whether the data is secured for the provided parameters.
     *
     *
     * @param subSystem sub-system code
     * @param securityType security type code
     * @param sourceTable source table for the sub-system
     * @param sourceId source id for the source table.
     * @return security rule {READONLY,READWRITE or RESTRICTED} if 3rd parameter is provided.
     *  Otherwise, if only 1st and 2nd parameters are provided, returns specialized user security addl sql to append to the query.
     */
    public String getUserSecurity(String subSystem, String securityType, String sourceTable, String sourceId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUserSecurity", new Object[]{subSystem,securityType,sourceTable,sourceId});
        }
        String returnValue = "";
        RecordSet rs;
        try {
            Record input = new Record();
            input.setFieldValue("subSystem",subSystem);
            input.setFieldValue("securityType",securityType);
            input.setFieldValue("sourceTableName",sourceTable);
            input.setFieldValue("sourceRecordId",sourceId);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Oasis_Security.Get_User_Security");
            rs = spDao.execute(input);
            returnValue = rs.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
         catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get user security", e);
            l.throwing(getClass().getName(), "getUserSecurity", ae);
            throw ae;
        }
        return returnValue;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public DataSecurityJdbcDAO(){

    }
}
