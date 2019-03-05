package dti.pm.componentmgr.experiencemgr.impl;

import dti.pm.componentmgr.experiencemgr.ExperienceComponentManager;
import dti.pm.componentmgr.experiencemgr.ExperienceComponentFields;
import dti.pm.componentmgr.experiencemgr.dao.ExperienceComponentDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.SysParmProvider;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.app.ConfigurationException;

import java.util.Date;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.Object;

/**
 * This Class provides the implementation details of Experience Component Manager Interface.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 24, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/15/2009       gxc         Modified to pass pass evaluation date as was entered by user
 * ---------------------------------------------------
 */
public class ExperienceComponentManagerImpl implements ExperienceComponentManager {
    /**
     * Initialize the search dates.
     * @param inputRecord
     * @return Record
     */

    public Record getInitialValuesForProcess(Record inputRecord)  {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForProcess",
            new Object[]{inputRecord});

        // add configuration defaults to the output record
        Record outputRecord = new Record();

        // Get StartSearchDate.
        int year = DateUtils.getYear(new Date());
        int month = DateUtils.getMonth(new Date());

        String startSearchDate = DateUtils.formatDate(DateUtils.makeDate(year,month,1));

        // Set the endSearchDate to be the last day of the month.
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month,1);

        int lastDate = calendar.getActualMaximum(Calendar.DATE);

        String endSearchDate = DateUtils.formatDate(DateUtils.makeDate(year,month,lastDate));

        ExperienceComponentFields.setStartSearchDate(outputRecord, startSearchDate);
        ExperienceComponentFields.setEndSearchDate(outputRecord, endSearchDate);

        l.exiting(getClass().getName(), "getInitialValuesForProcess", outputRecord);
        return outputRecord;
    }


    /**
     * To process and load policies with errors for a particular set of search criteria
     *
     * @param inputRecord with user entered search criteria
     * @return RecordSet
     */
    public RecordSet loadAllExperienceDetail(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExperienceDetail", new Object[]{inputRecord});
        }

        validateSearchCriteria(inputRecord);

        RecordSet rs = getExperienceComponentDAO().loadAllExperienceDetail(inputRecord);
      
        l.exiting(getClass().getName(), "loadAllExperienceDetail", rs);

        return rs;
    }

    /**
     * Public method to validate search criteria
     *
     * @param inputRecord
     */
    public void validateSearchCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateSearchCriteria", new Object[]{inputRecord});
        }

        // Search end date must be the same as or after search start date
        if (!StringUtils.isBlank(ExperienceComponentFields.getStartSearchDate(inputRecord)) &&
            !StringUtils.isBlank(ExperienceComponentFields.getEndSearchDate(inputRecord))) {
            Date fromDate = DateUtils.parseDate(ExperienceComponentFields.getStartSearchDate(inputRecord));
            Date toDate = DateUtils.parseDate(ExperienceComponentFields.getEndSearchDate(inputRecord));
            if (fromDate.after(toDate)) {
                MessageManager.getInstance().addErrorMessage("pm.processExperienceComponent.fromDateAfterTo");
            }
        }

        // throw validation exception if data is invalid
        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid search criteria data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateSearchCriteria");
        }
    }

//-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public void verifyConfig() {
        if (getExperienceComponentDAO() == null)
            throw new ConfigurationException("The required property 'experienceComponentDAO' is missing.");
    }   

    public ExperienceComponentDAO getExperienceComponentDAO() {
        return m_experienceComponentDAO;
    }

    public void setExperienceComponentDAO(ExperienceComponentDAO experienceComponentDAO) {
        m_experienceComponentDAO = experienceComponentDAO;
    }

    private ExperienceComponentDAO m_experienceComponentDAO;
}
