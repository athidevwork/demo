package dti.ci.vendormgr.impl;

import dti.ci.vendormgr.PhoneLogManager;
import dti.ci.vendormgr.dao.PhoneLogDAO;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   8/13/14
 *
 * @author wkong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/08/2018       Elvin       Issue 195627: enable default values setting when adding phone log
 * ---------------------------------------------------
 */
public class PhoneLogManagerImpl implements PhoneLogManager {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Get the phone log list for an entity.
     * @param inputRecord the information of an entity.
     * @return The phone log list of the entity.
     */
    public RecordSet getPhoneLog(Record inputRecord){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPhoneLog", new Object[]{inputRecord});
        }

        RecordLoadProcessor loadProcessor = AddSelectIndLoadProcessor.getInstance();
        RecordSet rs = getPhoneLogDAO().getPhoneLog(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPhoneLog", rs);
        }
        return rs;
    }

    /**
     * Save phone Log.
     * @param recordSet the detail info.
     */
    public void savePhoneLog(RecordSet recordSet){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneLog", new Object[]{recordSet});
        }

        RecordSet changedRs = OasisRecordSetHelper.setRowStatusOnModifiedRecords(recordSet);
        getPhoneLogDAO().savePhoneLog(changedRs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePhoneLog");
        }
    }

    @Override
    public Record getFieldDefaultValues(Record inputRecord) {
        l.entering(getClass().getName(), "getFieldDefaultValues");

        String actionClassName = inputRecord.getStringValueDefaultEmpty("actionClassName");
        if (StringUtils.isBlank(actionClassName)) {
            throw new AppException("No action class name.");
        }

        Record outRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldDefaultValues", outRecord);
        }
        return outRecord;
    }

    public void verifyConfig() {
        if (getPhoneLogDAO() == null) {
            throw new ConfigurationException("The required property 'phoneLogDAO' is missing.");
        }
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public PhoneLogDAO getPhoneLogDAO() {
        return m_phoneLogDAO;
    }

    public void setPhoneLogDAO(PhoneLogDAO phoneLogDAO) {
        this.m_phoneLogDAO = phoneLogDAO;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;
    private PhoneLogDAO m_phoneLogDAO;
}
