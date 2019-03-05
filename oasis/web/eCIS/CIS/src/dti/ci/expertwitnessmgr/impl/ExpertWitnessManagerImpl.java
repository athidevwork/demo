package dti.ci.expertwitnessmgr.impl;

import dti.ci.expertwitnessmgr.ExpertWitnessManager;
import dti.ci.expertwitnessmgr.dao.ExpertWitnessDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for ExpertWitnesspondence
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 01, 2007
 *
 * @author bhong
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *   Issue  70335    eCIS/ Expert Witness  jerry
 *
 * ---------------------------------------------------
*/

public class ExpertWitnessManagerImpl implements ExpertWitnessManager {
    /**
     * Get Expert witness count of an entity.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public int getExpertWitnessCountOfEntity(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExpertWitnessCountOfEntity", new Object[]{inputRecord});
        }

        int count = getExpertWitnessDAO().getExpertWitnessCountOfEntity(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExpertWitnessCountOfEntity", Integer.valueOf(count));
        }
        return count;
    }

    /**
     * Get person info of Expert Witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public Record getPersonInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPersonInfo", new Object[]{inputRecord});
        }

        Record record = getExpertWitnessDAO().getPersonInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPersonInfo", record);
        }
        return record;
    }

    /**
     * Load Expert Witness addresses.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllAddress(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAddress", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllAddress(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAddress", rs);
        }
        return rs;
    }

    /**
     * Load Expert Witness addresses.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPhone", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllPhone(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPhone", rs);
        }

        return rs;
    }

    /**
     * Load Education info of Expert Witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllEducation(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEducation", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllEducation(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEducation", rs);
        }

        return rs;
    }

    /**
     * Load all classification of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllClassification(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClassification", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllClassification(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllClassification", rs);
        }

        return rs;
    }

    /**
     * Load all relationship of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllRelationship(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRelationship", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllRelationship(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRelationship", rs);
        }

        return rs;
    }

    /**
     * Load all claim of an expert witness.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllClaim(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClaim", new Object[]{inputRecord});
        }

        RecordSet rs = getExpertWitnessDAO().loadAllClaim(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllClaim", rs);
        }
        return rs;
    }

    /**
     * Change expert witness status.
     *
     * @param inputRecord
     */
    @Override
    public void changeStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeStatus", new Object[]{inputRecord});
        }

        getExpertWitnessDAO().changeStatus(inputRecord);

        l.exiting(getClass().getName(), "changeStatus");
    }


    public void verifyConfig() {
        if (getExpertWitnessDAO() == null) {
            throw new ConfigurationException("The required property 'expertWitnessDAO' is missing.");
        }
    }

    public ExpertWitnessDAO getExpertWitnessDAO() {
        return m_expertWitnessDAO;
    }

    public void setExpertWitnessDAO(ExpertWitnessDAO expertWitnessDAO) {
        m_expertWitnessDAO = expertWitnessDAO;
    }

    private ExpertWitnessDAO m_expertWitnessDAO;
}
