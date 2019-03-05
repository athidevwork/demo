package dti.oasis.userpreference.impl;

import dti.oasis.userpreference.UserPreferenceManager;
import dti.oasis.userpreference.dao.UserPreferenceDAO;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.recordset.Record;
import dti.oasis.app.ConfigurationException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class provides the implementation details for UserPreferenceManager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:  Apr 10, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
    
public class UserPreferenceManagerImpl implements UserPreferenceManager {

    /**
     * Get user preference based on given preference code
     *
     * @param preferenceCode
     * @param defaultValue
     * @return
     */
    public String getUserPreference(String preferenceCode,String defaultValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUserPreference", new Object[]{preferenceCode,defaultValue});
        }
        Record inputRecord = new Record();
        inputRecord.setFieldValue("prefCode", preferenceCode);
        String result = getUserPreferenceDAO().getUserPreference(inputRecord);
        if (StringUtils.isBlank(result)){
            result=defaultValue;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUserPreference", result);
        }
        return result;
    }

    public UserPreferenceDAO getUserPreferenceDAO() {
        return m_userPreferenceDAO;
    }

    public void setUserPreferenceDAO(UserPreferenceDAO userPreferenceDAO) {
        m_userPreferenceDAO = userPreferenceDAO;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getUserPreferenceDAO() == null)
            throw new ConfigurationException("The required property 'userPreferenceDAO' is missing.");
    }

    private UserPreferenceDAO m_userPreferenceDAO;
    private final Logger l = LogUtils.getLogger(getClass());
}
