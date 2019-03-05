package dti.ci.riskmgr.survey.impl;

import dti.ci.riskmgr.survey.SurveyManager;
import dti.ci.riskmgr.survey.dao.SurveyDAO;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.UpdateIndicator;
import dti.cs.data.dbutility.DBUtilityManager;

import java.util.logging.Logger;

/**
 * The Business Object implementation for Risk Management Survey Tracking.
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SurveyManagerImpl implements SurveyManager {

    /**
     * method to get the intial "default" value when adding a new survey data.
     *
     * @param input a record containing entityId
     * @return record containing the intial values.
     */
    public Record getInitialValuesForNewSurvey(Record input) {
        Logger l = LogUtils.enterLog(this.getClass(), "getInitialValuesForSurvey");
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValues = getWorkbenchConfiguration().getDefaultValues(MAINTAIN_SURVEY_ACTION_CLASS_NAME);

        // get the next sequence from RDBMS as primary key 
        Long newRmSurveyId = getDbUtilityManager().getNextSequenceNo();
        defaultValues.setFieldValue("rmSurveyId",newRmSurveyId);

        l.exiting(this.getClass().getName(), "getInitialValuesForSurvey", defaultValues );
        return defaultValues;
    }

    /**
     * load survey data for an entity represented by entityId in the record
     *
     * @param input: a record containing entityId
     * @return Survey Data for an given entity
     */
    public RecordSet loadAllSurvey(Record input) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadAllSurvey");
        RecordSet rs = getSurveyDAO().loadAllSurvey(input);
        l.exiting(this.getClass().getName(), "loadAllSurvey", rs);
        return rs;
    }

    /**
     * method to save all survey data
     *
     * @param rs: survey data represented as RecordSet
     * @return integer indicating the number of survey rows applied to RDBMS
     */
    public int saveAllSurvey(RecordSet rs) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveAllSurvey");

        // validate the recordsets,
        RecordSet subset = ValidateSurveyBeforeSave(rs);

        int rowsSaved = getSurveyDAO().saveAllSurvey(subset);
        
        l.exiting(this.getClass().getName(), "saveAllSurvey", rowsSaved+"");
        return rowsSaved;

    }

    /** method to validate the survey data before save
     *
     * @param rs   survey data represented as RecordSet
     * @return rs. throws runtime exception validatationException if data did not pass validation
     */
   private RecordSet ValidateSurveyBeforeSave(RecordSet rs){
      Logger l = LogUtils.enterLog(this.getClass(), "ValidateSurveyBeforeSave");
      RecordSet validatedRows = new RecordSet();

      // only keep inserted, and updated records..per use case
      RecordSet inserts = rs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
      inserts.setFieldValueOnAll("updateInd",UpdateIndicator.INSERTED);

      RecordSet updates = rs.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
      updates.setFieldValueOnAll("updateInd",UpdateIndicator.UPDATED);

      validatedRows.addRecords(inserts);
      validatedRows.addRecords(updates);

      l.finest(this.getClass().getName()+".ValidateSurveyBeforeSave: good!");
      return validatedRows;
   }

    public void verifyConfig() {
        if (getDbUtilityManager() == null) {
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        }
        if (getSurveyDAO() == null) {
            throw new ConfigurationException("The required property 'surveyDAO' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public DBUtilityManager getDbUtilityManager() {
        return dbUtilityManager;
    }
    public SurveyDAO getSurveyDAO() {
        return surveyDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return workbenchConfiguration;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtility) {
        this.dbUtilityManager = dbUtility;
    }

    public void setSurveyDAO(SurveyDAO surveyDao) {
        this.surveyDAO = surveyDao;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfig) {
        this.workbenchConfiguration = workbenchConfig;
    }


    private DBUtilityManager  dbUtilityManager;
    private SurveyDAO surveyDAO;
    private WorkbenchConfiguration workbenchConfiguration;
    private static final String MAINTAIN_SURVEY_ACTION_CLASS_NAME = "dti.ci.riskmgr.survey.struts.MaintainSurveyAction";
}
