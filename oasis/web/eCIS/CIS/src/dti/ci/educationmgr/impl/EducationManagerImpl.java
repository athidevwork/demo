package dti.ci.educationmgr.impl;

import dti.ci.educationmgr.EducationManager;
import dti.ci.educationmgr.dao.EducationDAO;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for Education
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:  May 16, 2006
 *
 * @author gjli
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  04/3/2007       Jerry        entities based on the value entered in the system parameter 'CI_SCHOOL_CLASS'.
 *  07/14/2016      dpang        176370: set initial values when adding education.
 * ---------------------------------------------------
*/
public class EducationManagerImpl implements EducationManager {
    /**
     * Get Education List info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadEducationList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEducationList", new Object[]{inputRecord});
        }

        RecordSet rs = getEducationDAO().getEducationList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEducationList", rs);
        }
        return rs;
    }

    /**
     * Get entity Info for an entity (dateOfBirth, dateOfDead).
     *
     * @param inputRecord
     * @return Record
     */
    @Override
    public Record getEntityInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityInfoRecord", new Object[]{inputRecord});
        }

        inputRecord.setFieldValue("entityId", inputRecord.getStringValue(ICIConstants.PK_PROPERTY, ""));

        Record entityInfoRecord = getEducationDAO().getEntityInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityInfoRecord", entityInfoRecord);
        }
        return entityInfoRecord;
    }

    /**
     * Get Institution Name List for an entity
     *
     * @param inputRecord
     * @return List
     */
    @Override
    public List getInstitutionNameList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInstitutionNameList", new Object[]{inputRecord});
        }

        List instiName = getEducationDAO().getListOfInstitutionName(inputRecord);
        
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInstitutionNameList", new Object[]{instiName});
        }
        return instiName;
    }

    /**
    * Method to save Education information
    *
    * @param inputRecords
    * @return int
    */
    public int saveEducationData(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEducationData", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getEducationDAO().saveEducationData(changedRecords);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEducationData", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Save education
     *
     * @param inputRecord
     */
    public Record saveEducation(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveEducation", new Object[]{inputRecord});
        }

        Record recResult = getEducationDAO().saveEducation(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveEducation");
        }
        return recResult;
    }

    @Override
    public Record getInitialValuesForAddEducation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddEducation");
        String className = inputRecord.getStringValue("className");
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(className);

        List<String> fieldNameList = defaultValuesRecord.getFieldNameList();

        //Remove prefix for page fields.
        for (String fieldName : fieldNameList) {
            if (fieldName.startsWith(EDUCATION_FIELD_PREFIX)) {
                outRecord.setFieldValue(fieldName.substring(EDUCATION_FIELD_PREFIX.length()),
                        defaultValuesRecord.getFieldValue(fieldName));
            }
        }

        l.exiting(getClass().toString(), "getInitialValuesForAddEducation", outRecord);
        return outRecord;
    }

    public void verifyConfig() {
        if (getEducationDAO() == null) {
            throw new ConfigurationException("The required property 'getEducationDAO' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
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

    private EducationDAO m_educationDAO;

    private static String EDUCATION_FIELD_PREFIX = "educationProfile_";

}
