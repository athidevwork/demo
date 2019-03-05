package dti.ci.priorcarriermgr.impl;

import dti.ci.core.CIFields;
import dti.ci.priorcarriermgr.PriorCarrierFields;
import dti.ci.priorcarriermgr.PriorCarrierManager;
import dti.ci.priorcarriermgr.dao.PriorCarrierHistoryDAO;
import dti.ci.core.error.ExpMsgConvertor;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/27/12
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/08/2012       kshen       Added methods for refactor Prior Carrier page.
 * 03/25/2014       kshen       Issue 153325
 * ---------------------------------------------------
 */
public class PriorCarrierManagerImpl implements PriorCarrierManager {
    /**
     * Load all prior carrier of a entity by filter criteria.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadAllPriorCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPriorCarrier", new Object[]{inputRecord});
        }

        Record filterCriteriaRecord = getFilterPriorCarrierCriteria(inputRecord);

        RecordSet rs = getPriorCarrierHistoryDAO().loadAllPriorCarrier(filterCriteriaRecord, new PriorCarrierLoadProcessor());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPriorCarrier", rs);
        }

        return rs;
    }

    /**
     * Get the initial values for prior carrier.
     *
     * @return
     */
    @Override
    public Record getInitialValuesForPriorCarrier(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForPriorCarrier", new Object[]{inputRecord});
        }

        Record record = getWorkbenchConfiguration().getDefaultValues(PriorCarrierFields.PRIOR_CARRIER_ACTION_CLASS_NAME);
        record.setFields(inputRecord);

        String idAuditEnabled = SysParmProvider.getInstance().getSysParm("CI_PRIOR_CARRIER_ADT", "N");
        if (idAuditEnabled.equals("Y")) {
            YesNoFlag hasAuditRecord = getPriorCarrierHistoryDAO().hasAuditRecord(inputRecord);
            record.setFieldValue("externalClaimsReportSummary_char5", hasAuditRecord.booleanValue()? "N": "Y");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForPriorCarrier", record);
        }

        return record;
    }

    /**
     * Get the default term year.
     *
     * @return
     */
    @Override
    public String getDefaultTermYear(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultTermYear", new Object[]{inputRecord});
        }

        String defaultTermYear = getPriorCarrierHistoryDAO().getDefaultTermYear(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultTermYear", defaultTermYear);
        }

        return defaultTermYear;
    }

    /**
     * Save all the prior carrier records.
     *
     * @param inputRecords
     * @return
     */
    @Override
    public int saveAllPriorCarrier(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorCarrier", new Object[]{inputRecords});
        }

        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        int count = getPriorCarrierHistoryDAO().saveAllPriorCarrier(changedRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllPriorCarrier", count);
        }

        return count;
    }

    /**
     * Load the audit history.
     * Parameters:
     *
     * @param record
     * @return
     */
    public RecordSet loadPriorCarrierHistory(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPriorCarrierHistory", new Object[]{record});
        }

        RecordSet rs = null;
        try {
            rs = getPriorCarrierHistoryDAO().loadAllPriorCarrierHistory(record);
        } catch (SQLException e) {
            l.throwing(getClass().getName(), "loadPriorCarrierHistory", e);
            throw new AppException("ci.priorcarriermgr.dbError", "", new String[]{ExpMsgConvertor.getExceptionDetail(e)});
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPriorCarrierHistory", rs);
        }
        return rs;
    }

    /**
     * Method to save prior carrier history information
     *
     * @param inputRecords
     * @return int
     */
    public int savePriorCarrierHistory(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePriorCarrierHistory", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            try {
                updateCount = getPriorCarrierHistoryDAO().saveAllPriorCarrierHistory(changedRecords);
            } catch (SQLException e) {
                l.throwing(getClass().getName(), "savePriorCarrierHistory", e);
                throw new AppException("ci.priorcarriermgr.dbError", "", new String[]{ExpMsgConvertor.getExceptionDetail(e)});
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePriorCarrierHistory", new Integer(updateCount));
        }
        return updateCount;
    }

    private Record getFilterPriorCarrierCriteria(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFilterPriorCarrierCriteria", new Object[]{inputRecord});
        }

        Record filterCriteriaRecord = new Record();
        filterCriteriaRecord.setFieldValue(CIFields.ENTITY_ID, inputRecord.getStringValue(CIFields.PK_PROPERTY));

        Iterator fieldNames = inputRecord.getFieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = (String) fieldNames.next();

            if (fieldName.startsWith(PriorCarrierFields.FILTER_CRITERIA_PREFIX)) {
                filterCriteriaRecord.setFieldValue(
                        fieldName.substring(PriorCarrierFields.FILTER_CRITERIA_PREFIX.length()),
                        inputRecord.getStringValue(fieldName, ""));
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFilterPriorCarrierCriteria", filterCriteriaRecord);
        }

        return filterCriteriaRecord;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getPriorCarrierHistoryDAO() == null) {
            throw new ConfigurationException("The required property 'priorCarrierHistoryDAO' is missing.");
        }
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public PriorCarrierHistoryDAO getPriorCarrierHistoryDAO() {
        return priorCarrierHistoryDAO;
    }

    public void setPriorCarrierHistoryDAO(PriorCarrierHistoryDAO priorCarrierHistoryDAO) {
        this.priorCarrierHistoryDAO = priorCarrierHistoryDAO;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    private PriorCarrierHistoryDAO priorCarrierHistoryDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
