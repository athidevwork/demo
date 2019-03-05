package dti.ci.trainingmgr.impl;

import dti.ci.educationmgr.dao.EducationDAO;
import dti.ci.trainingmgr.TrainingFields;
import dti.ci.trainingmgr.TrainingManager;
import dti.ci.trainingmgr.dao.TrainingDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Training.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 12, 2006
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/

public class TrainingManagerImpl implements TrainingManager {

    /**
     * Get Education List info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadTrainingList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadTrainingList", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getTrainingDAO().getTrainingList(inputRecord);
        Record summmaryRec = rs.getSummaryRecord();
        TrainingFields.setDateOfBirth(summmaryRec,getDateOfBirth(Long.parseLong(entityId)));
        rs.setSummaryRecord(summmaryRec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadTrainingList", rs);
        }
        return rs;
    }

    /**
     * Method to save Training information
     *
     * @param inputRecords
     * @return int
     */
    public int saveTrainingData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveTrainingData", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getTrainingDAO().saveTrainingData(changedRecords);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveTrainingData", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Get Institution Name List for an entity
     *
     * @param inputRecord
     * @return List
     */
    public List getInstitutionNameList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInstitutionNameList", new Object[]{inputRecord});
        }

        List instiName = getTrainingDAO().getListOfInstitutionName(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInstitutionNameList", new Object[]{instiName});
        }
        return instiName;
    }

    /**
     * Retrieve the entity's date of birth.
     *
     * @param entityId Entity PK.
     * @return String  date of birth
     * @throws Exception
     */
    public String getDateOfBirth(long entityId) {
        String methodName = "getDateOfBirth";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{new Long(entityId)});

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId",entityId);
        Record entityInfoRecord = getEducationDAO().getEntityInfo(inputRecord);
        String dateOfBirth = TrainingFields.getDateOfBirth(entityInfoRecord);

        lggr.exiting(this.getClass().getName(), methodName, dateOfBirth);
        return dateOfBirth;
    }

    /**
     * Get the initial values when adding training
     *
     * @param inputRecord
     * @return record
     */
    public Record getInitialValuesForAddTraining(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddTraining");
        String className = inputRecord.getStringValue("className");
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(className);

        //start with the default record
        outRecord.setFields(defaultValuesRecord);

        //overlay it with inputRecord
        outRecord.setFields(inputRecord);

        l.exiting(getClass().toString(), "getInitialValuesForAddTraining", outRecord);
        return outRecord;
    }

    public void verifyConfig() {
        if (getTrainingDAO() == null) {
            throw new ConfigurationException("The required property 'getTrainingDAO' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public TrainingDAO getTrainingDAO() {
        return m_trainingDAO;
    }

    public void setTrainingDAO(TrainingDAO m_trainingDAO) {
        this.m_trainingDAO = m_trainingDAO;
    }

    public EducationDAO getEducationDAO() {
        return m_educationDAO;
    }

    public void setEducationDAO(EducationDAO m_educationDAO) {
        this.m_educationDAO = m_educationDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;

    private TrainingDAO m_trainingDAO;
    private EducationDAO m_educationDAO;
}
