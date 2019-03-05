package dti.ci.licensemgr.impl;

import dti.ci.licensemgr.LicenseManager;
import dti.ci.licensemgr.dao.LicenseDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.OasisRecordSetHelper;

import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * Business Object for License
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   feb 17, 2012
 *
 * @author parker
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class LicenseManagerImpl implements LicenseManager {
    
    /**
     * load license information.
     * @param record
     * @return
     */
    @Override
    public RecordSet loadLicense(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadLicense", new Object[]{record});
        }
        RecordSet rs =  getLicenseDAO().loadLicense(record);

        l.exiting(getClass().getName(), "loadLicense", rs);
        return rs;
    }

     /**
     * Method to save License information
     *
     * @param inputRecords
     * @return int
     */
    public int saveLicense(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveLicense", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getLicenseDAO().saveLicense(changedRecords);
        }
        l.exiting(getClass().getName(), "saveLicense", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Save education
     *
     * @param inputRecord
     */
    public Record saveLicense(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveLicense", new Object[]{inputRecord});
        }

        Record recResult = getLicenseDAO().saveLicense(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveLicense");
        }
        return recResult;
    }

    public Record getInitialValuesForAddLicense(Record inputRecord) {

        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddLicense", new Object[]{inputRecord});
        String className = inputRecord.getStringValue("className");
        Record outRecord = new Record();

        // get the default values from the workbench configuration for this page
        Record defaultValuesRecord = getWorkbenchConfiguration().getDefaultValues(className);

        //start with the default record
        outRecord.setFields(defaultValuesRecord);

        //overlay it with inputRecord
        outRecord.setFields(inputRecord);

        l.exiting(getClass().toString(), "getInitialValuesForAddLicense", outRecord);
        return outRecord;
    }

    public void verifyConfig() {
        if (getLicenseDAO() == null) {
            throw new ConfigurationException("The required property 'getLicenseDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public LicenseDAO getLicenseDAO() {
        return licenseDAO;
    }

    public void setLicenseDAO(LicenseDAO licenseDAO) {
        this.licenseDAO = licenseDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;
    private LicenseDAO licenseDAO;
}
