package dti.ci.entityquickaddmgr.impl;

import dti.ci.educationmgr.EducationManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/30/2018
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityQuickAddPersonManagerImpl extends EntityQuickAddManagerImpl {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    protected void saveOtherInfo(Record inputRecord, String entId) {
        saveEducation(inputRecord, entId);
    }

    private void saveEducation(Record inputRecord, String entId) {
        String methodName = "saveEducation";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, entId});
        }

        Record education1Record = getRecordForPrefix(inputRecord, "educationProfile_");
        if (!StringUtils.isBlank(education1Record.getStringValue("institutionName"))) {
            RecordSet eduRecordSet = new RecordSet();
            getAndAddEduRecToRecordSet(inputRecord, "educationProfile_", eduRecordSet);
            getAndAddEduRecToRecordSet(inputRecord, "educationProfile2_", eduRecordSet);
            getAndAddEduRecToRecordSet(inputRecord, "educationProfile3_", eduRecordSet);

            eduRecordSet.setFieldValueOnAll("entityAttendeeId", entId);
            eduRecordSet.setFieldValueOnAll("rowStatus", "NEW");
            getEducationManager().saveEducationData(eduRecordSet);
        }

        l.exiting(getClass().getName(), methodName);
    }

    private void getAndAddEduRecToRecordSet(Record inputRecord, String fieldPrefix, RecordSet eduRecordSet) {
        Record eduRecord = getRecordForPrefix(inputRecord, fieldPrefix);
        if (!StringUtils.isBlank(eduRecord.getStringValue("institutionName"))) {
            eduRecord.setUpdateIndicator("I");
            eduRecordSet.addRecord(eduRecord);
        }
    }

    @Override
    public void verifyConfig() {
        super.verifyConfig();

        if (getEducationManager() == null) {
            throw new ConfigurationException("The required property 'educationManager' is missing.");
        }
    }

    public EducationManager getEducationManager() {
        return m_educationManager;
    }

    public void setEducationManager(EducationManager educationManager) {
        this.m_educationManager = educationManager;
    }

    private EducationManager m_educationManager;

}
