package dti.oasis.userpreference;

/**
 * This class provides the definition for UserPreferenceManager.
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

public interface UserPreferenceManager {
   
    /**
     * Get user preference based on given preference code
     *
     * @param preferenceCode
     * @param defaultValue
     * @return
     */
    public String getUserPreference(String preferenceCode, String defaultValue);
}
