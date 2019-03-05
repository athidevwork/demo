package dti.ci.auditmgr.impl;

import dti.ci.auditmgr.AuditTrailFields;
import dti.ci.auditmgr.AuditTrailManager;
import dti.ci.auditmgr.dao.AuditTrailDAO;
import dti.ci.entitymgr.EntityFields;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle audit trails.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 31, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/09/2018       ylu         109179: refactor from CIAuditTrailHelper.java and CIAuditTrailPopupHelper.java
 * ---------------------------------------------------
*/

public class AuditTrailManagerImpl implements AuditTrailManager{
    private final Logger l = LogUtils.getLogger(getClass());


    /**
     * get default search criteria from workbench
     * @param actionClassName
     * @return
     */
    public Record getDefaultSearchCriteriaValue(String actionClassName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultSearchCriteriaValue", new Object[]{actionClassName});
        }

        if (StringUtils.isBlank(actionClassName)) {
            throw new AppException("Cannot find action class name.");
        }

        //get the default value from workbench
        Record defaultRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultSearchCriteriaValue", defaultRecord);
        }

        return defaultRecord;

    }

    /**
     * search and load all audit trial data in tab page for this entity
     *
     * refactor from CIAuditTrailHelper.java
     *      *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet searchAuditTrailData(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "searchAuditTrailData", new Object[]{inputRecord});
        }

        //get search criteria record
        Record searchCriteria = getFilterCriteria(inputRecord);
        EntityFields.setEntityId(searchCriteria, inputRecord.getStringValue(ICIConstants.PK_PROPERTY));

        //search and load data into tab page
        RecordSet rs = getAuditTrailDAO().searchAuditTrailData(searchCriteria);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "searchAuditTrailData", rs);
        }

        return rs;
    }

    /**
     * search and load audit history data in Popup page
     * @param inputRecord
     * @return
     */
    public RecordSet loadAuditTrailBySource(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAuditTrailBySource", new Object[]{inputRecord});
        }

        //get search criteria record
        Record searchCriteria = getFilterCriteria(inputRecord);
        searchCriteria.setFields(inputRecord,false);

        //search and load data into Popup page
        RecordSet rs = getAuditTrailDAO().loadAuditTrailBySource(searchCriteria);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAuditTrailBySource", rs);
        }
        return rs;
    }


    public Record getFilterCriteria(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterCriteria", new Object[]{inputRecord});
        }

        //get search criteria record
        Record searchCriteria = new Record();
        Iterator it = inputRecord.getFieldNames();
        while (it.hasNext()) {
            String fieldName = (String) it.next();
            if (fieldName.startsWith(AuditTrailFields.FILTER_CRITERIA_PREFIX)) {
                searchCriteria.setFieldValue(fieldName.substring(AuditTrailFields.FILTER_CRITERIA_PREFIX.length()), inputRecord.getStringValue(fieldName,""));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterCriteria", searchCriteria);
        }

        return searchCriteria;

    }

    public void verifyConfig() {
        if (getAuditTrailDAO() == null) {
            throw new ConfigurationException("The required property 'auditTrailDAO' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public AuditTrailDAO getAuditTrailDAO() {
        return m_auditTrailDAO;
    }

    public void setAuditTrailDAO(AuditTrailDAO m_auditTrailDAO) {
        this.m_auditTrailDAO = m_auditTrailDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration m_workbenchConfiguration) {
        this.m_workbenchConfiguration = m_workbenchConfiguration;
    }

    private AuditTrailDAO m_auditTrailDAO;

    private WorkbenchConfiguration m_workbenchConfiguration;
}