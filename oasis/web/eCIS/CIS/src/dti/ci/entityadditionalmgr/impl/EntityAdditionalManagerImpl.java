package dti.ci.entityadditionalmgr.impl;

import dti.ci.entityadditionalmgr.EntityAdditionalFields;
import dti.ci.entityadditionalmgr.EntityAdditionalManager;
import dti.ci.entityadditionalmgr.dao.EntityAdditionalDAO;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class provides the implementation details of EntityAdditionalManager Interface.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: February 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class EntityAdditionalManagerImpl implements EntityAdditionalManager {
    /**
     * Get the EntityAdditional recordset
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllAvailableEntityAdditionals(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityAdditionals", new Object[]{inputRecord});
        }
        RecordSet rs = getEntityAdditionalDAO().loadAllAvailableEntityAdditionals(inputRecord);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityAdditionals", rs);
        }
        return rs;
    }

    /**
     * Save the changed entityAdditional to DB
     *
     * @param inputRecords
     * @return
     */
    public int saveAllEntityAdditionals(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllEntityAdditionals", new Object[]{inputRecords});
        }

        validateAllEntityAdditional(inputRecords);

        RecordSet changedEntityAdditionals = setRowStatusOnModifiedRecords(inputRecords);

        int processCount = 0;
        if (changedEntityAdditionals.getSize() > 0) {
            processCount = getEntityAdditionalDAO().saveAllEntityAdditionals(changedEntityAdditionals);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllEntityAdditionals", new Integer(processCount));
        }
        return processCount;
    }

   
    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getEntityAdditionalDAO() == null)
            throw new ConfigurationException("The required property 'EntityAdditionalDAO' is missing.");
    }

    public EntityAdditionalDAO getEntityAdditionalDAO() {
        return entityAdditionalDAO;
    }

    public void setEntityAdditionalDAO(EntityAdditionalDAO entityAdditionalDAO) {
        this.entityAdditionalDAO = entityAdditionalDAO;
    }

    private EntityAdditionalDAO entityAdditionalDAO;

    /**
     * Set a new field "rowStatus" to every modified record.
     *
     * @param inputRecords the initial inputRecords
     * @return RecordSet the modified reocrds
     */
    public static RecordSet setRowStatusOnModifiedRecords(RecordSet inputRecords) {
        /* Determine if anything has changed */
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));

        /* Create a new RecordSet to include all added, modified and deleted records */
        RecordSet allRecords = new RecordSet();

        /* Add the inserted records into allRecords for batch mode update */
        if (insertedRecords.getSize() > 0) {
            insertedRecords.setFieldValueOnAll(ROW_STATUS, NEW);
            allRecords.addRecords(insertedRecords);
        }
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));

        /* Add the inserted records into allRecords for batch mode update */
        if (updatedRecords.getSize() > 0) {
            updatedRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);
            allRecords.addRecords(updatedRecords);
        }
        return allRecords;
    }

    /**
         * Validate all  Discount Points Hist
         *
         * @param inputRecords
         */
        protected void validateAllEntityAdditional(RecordSet inputRecords) {
            Logger l = LogUtils.getLogger(getClass());
            if (l.isLoggable(Level.FINER)) {
                l.entering(getClass().getName(), "validateAllEntityAdditional", new Object[]{inputRecords});
            }
            inputRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.INSERTED, UpdateIndicator.UPDATED, UpdateIndicator.NOT_CHANGED}));
            Record record = inputRecords.getRecord(0);
            if (record.hasField(EntityAdditionalFields.PCTWCPRACTICE)) {
                String pctWcPractice = (String)record.getFieldValue(EntityAdditionalFields.PCTWCPRACTICE);
                if (!StringUtils.isBlank(pctWcPractice)) {
                    if (Float.parseFloat(pctWcPractice) >0.1000001||Float.parseFloat(pctWcPractice) <0) {
                        MessageManager.getInstance().addErrorMessage("ci.entity.additional.pctWcPractice.error",new Object[]{EntityAdditionalFields.PCTWCPRACTICELABLE},EntityAdditionalFields.PCTWCPRACTICE);
                    }
                }
            }
            if (MessageManager.getInstance().hasErrorMessages()) {
                throw new ValidationException("Validate the Entity Additional data error.");
            }

            l.exiting(getClass().getName(), "validateAllEntityAdditional");
        }

    private static final String ROW_STATUS = "rowStatus";
    private static final String NEW = "NEW";
    private static final String MODIFIED = "MODIFIED";
    private static final String DELETED = "DELETED";
}
