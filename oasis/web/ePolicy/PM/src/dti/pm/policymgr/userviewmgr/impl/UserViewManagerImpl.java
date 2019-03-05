package dti.pm.policymgr.userviewmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.policymgr.userviewmgr.UserViewManager;
import dti.pm.policymgr.userviewmgr.dao.UserViewDAO;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details for UserViewManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 27, 2007
 *
 * @author rlli
 */

public class UserViewManagerImpl implements UserViewManager {


    /**
     * load  user view info
     *
     * @param inputRecord (pmUserViewId)
     * @return recordset (only one record contained)
     */
    public RecordSet loadUserView(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadUserView", new Object[]{inputRecord});
        }
        RecordSet rs = getUserViewDAO().loadUserView(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllUserView", rs);
        }
        return rs;
    }

    /**
     * save user view(update or create)
     *
     * @param inputRecord (info get from page)
     * @return pmUserViewId
     */
    public String saveUserView(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveUserView", new Object[]{inputRecord});
        }
        String updateind = inputRecord.getStringValue("updateind");
        String pmUserViewId = inputRecord.getStringValue("pmUserViewId");
        String existedUserViewId = inputRecord.getStringValue("existedUserViewId");
        if (updateind.equals("Y")) {
            //from select
            if (StringUtils.isBlank(pmUserViewId)) {
                if (!StringUtils.isBlank(existedUserViewId)) {
                    inputRecord.setFieldValue("pmUserViewId", existedUserViewId);
                }
            }
            //form existing user view
            else {
                if (!StringUtils.isBlank(existedUserViewId)&&(!existedUserViewId.equals(pmUserViewId))) {
                    Record record = new Record();
                    record.setFieldValue("pmUserViewId", existedUserViewId);
                    getUserViewDAO().deleteUserView(record);
                }
            }
        }
        String userviewId = getUserViewDAO().saveUserView(inputRecord).getStringValue("pmUserViewId");
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveUserView", userviewId);
        }
        return userviewId;
    }

    /**
     * delete selected user view
     *
     * @param inputRecord (pmUserViewId)
     * @return
     */
    public void deleteUserView(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "deleteUserView", new Object[]{inputRecord});
        }
        getUserViewDAO().deleteUserView(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteUserView");
        }
    }

    /**
     * validate additional sql
     *
     * @param inputRecord
     * @return
     */
    public void validateAdditionalSql(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAdditionalSql", new Object[]{inputRecord});
        }
        Record record = new Record();
        String refSql = (new StringBuffer().append(inputRecord.getStringValue("existingSql"))
            .append(inputRecord.getStringValue("additionalSql"))).toString();
        record.setFieldValue("refSql", refSql);
        String result = getUserViewDAO().validateAdditionalSql(record);
        if (!StringUtils.isBlank(result)) {
            MessageManager.getInstance().addErrorMessage("pm.userview.validateAdditionalSql.error", new Object[]{result});
            throw new ValidationException();
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAdditionalSql", result);
        }

    }

    public UserViewDAO getUserViewDAO() {
        return m_userViewDAO;
    }

    public void setUserViewDAO(UserViewDAO userViewDAO) {
        m_userViewDAO = userViewDAO;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getUserViewDAO() == null) {
            throw new ConfigurationException("The required property 'userViewDAO' is missing.");
        }
    }

    private UserViewDAO m_userViewDAO;
}
