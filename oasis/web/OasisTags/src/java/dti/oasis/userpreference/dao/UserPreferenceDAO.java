package dti.oasis.userpreference.dao;

import dti.oasis.recordset.Record;

/**
 * This class provides the definition for UserPreferenceDAO.
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
public interface UserPreferenceDAO {

    /**
     * Get user preference based on given preference code
     * @param inputRecord Record contains input values
     * @return String containing the user preference
     */
    public String getUserPreference(Record inputRecord);
}
