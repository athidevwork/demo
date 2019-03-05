package dti.ci.entityminipopupmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.ci.entityminipopupmgr.EntityMiniPopupFields;
import dti.ci.core.dao.BaseDAO;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

/**
 * Dao class to handle logics of Entity Mini Popup Manager
 * <p/>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 28, 2010
 *
 * @author bchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/17/2018       dzhang      Issue 192649: entity mini popup refactor
 * ---------------------------------------------------
 */
public class EntityMiniPopupJdbcDAO extends BaseDAO implements EntityMiniPopupDAO {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Load entity address list
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadEntityAddressList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityAddressList", new Object[]{inputRecord});
        }

        RecordSet addrList = null;

        StoredProcedureDAO storedProcedureDAO = StoredProcedureDAO.getInstance("ci_web_entity_mini_popup.sel_entity_address_list");
        try {
            addrList = storedProcedureDAO.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadEntityAddressList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityAddressList", addrList);
        }
        return addrList;
    }

    @Override
    public RecordSet loadAddressPhoneList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressPhoneList", new Object[]{inputRecord});
        }

        RecordSet addressPhoneList = null;

        StoredProcedureDAO storedProcedureDAO = StoredProcedureDAO.getInstance("ci_web_entity_mini_popup.get_address_phone_list");
        try {
            addressPhoneList = storedProcedureDAO.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, "ci.generic.error", getClass().getName(), "loadAddressPhoneList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressPhoneList", addressPhoneList);
        }
        return addressPhoneList;
    }

    /**
     * Load entity general phone list
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadEntityGeneralPhoneList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityGeneralPhoneList", new Object[]{inputRecord});
        }

        RecordSet generalPhoneList = null;

        StoredProcedureDAO storedProcedureDAO = StoredProcedureDAO.getInstance("ci_web_entity_mini_popup.get_general_phone_list");
        try {
            generalPhoneList = storedProcedureDAO.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e,"ci.generic.error", getClass().getName(), "loadEntityGeneralPhoneList");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityGeneralPhoneList", generalPhoneList);
        }
        return generalPhoneList;
    }
}
