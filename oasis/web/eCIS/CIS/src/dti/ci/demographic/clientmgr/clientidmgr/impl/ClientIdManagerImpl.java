package dti.ci.demographic.clientmgr.clientidmgr.impl;

import dti.ci.demographic.clientmgr.clientidmgr.ClientIdManager;
import dti.ci.demographic.clientmgr.clientidmgr.dao.ClientIdDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.error.ValidationException;
import dti.oasis.struts.AddSelectIndLoadProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Date;

/**
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 12, 2008
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       kshen       Removed class prefix "CI".
 * ---------------------------------------------------
 */
public class ClientIdManagerImpl implements ClientIdManager {
    public RecordSet loadAllClientIds(Long entityPk) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllClientIds", new Object[]{entityPk});
        }

        Record input = new Record();
        input.setFieldValue("entityId", entityPk);

        /* Gets underwriters record set */
        RecordSet rs = getClientIdDAO().loadAllClientIds(input, new AddSelectIndLoadProcessor());

        // EntitlementFields.setReadOnly(rs.getSummaryRecord(), YesNoFlag.Y);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllClientIds", rs);
        }
        return rs;
    }

    public int saveAllClientIds(Long entityPk, RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "saveAllClientIds", new Object[]{entityPk, inputRecords});

        int updateCount = 0;

        // Determine if anything has changed
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        if ((insertedRecords.getSize() + updatedRecords.getSize() + deletedRecords.getSize()) > 0) {
            // Validate the input underwriters prior to saving them.
            validateAllClientIds(entityPk, inputRecords);

            // Get the summary record out and use it for createTransaction
            Record inputRecord = inputRecords.getSummaryRecord();

            // Add the inserted WIP records in batch mode
            if (insertedRecords.getSize() > 0) {
                insertedRecords.setFieldValueOnAll("rowStatus", "NEW");
                updateCount += getClientIdDAO().addAllClientIds(insertedRecords);
            }

            if (deletedRecords.getSize() > 0) {
                updateCount += getClientIdDAO().deleteAllClientIds(deletedRecords);
            }

            if (updatedRecords.getSize() > 0) {
                updatedRecords.setFieldValueOnAll("rowStatus", "MODIFIED");
                updateCount += getClientIdDAO().updateAllClientIds(updatedRecords);
            }
        }
        l.exiting(getClass().getName(), "saveAllClientIds", new Integer(updateCount));
        return updateCount;

    }

    public void verifyConfig() {
        if (getClientIdDAO() == null)
            throw new ConfigurationException("The required property 'clientIdDAO' is missing.");
    }

    public ClientIdManagerImpl() {
    }

    public ClientIdDAO getClientIdDAO() {
        return clientIdDAO;
    }

    public void setClientIdDAO(ClientIdDAO clientIdDAO) {
        this.clientIdDAO = clientIdDAO;
    }

    protected void validateAllClientIds(Long entityPk, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllClientIds", new Object[]{entityPk, inputRecords});
        }

        Iterator iter = inputRecords.getRecords();

        Date lastEndDate = DateUtils.parseDate("01/01/3000");
        int primaryId = 0;

        while (iter.hasNext()) {
            Record rec = (Record) iter.next();
            String rowNum = String.valueOf(rec.getRecordNumber() + 1);
            String rowId = rec.getStringValue("entityIdNumberId");
            // If modified perform first set of validations
            if (rec.isUpdateIndicatorInserted() || rec.isUpdateIndicatorUpdated()) {
                if (StringUtils.isBlank(rec.getStringValue("entityIdNoTypeCode"))
                        || "-1".equals(rec.getStringValue("entityIdNoTypeCode"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.clientIdType.required",
                            new String[]{rowNum}, "entityIdNoTypeCode", rowId);
                }

                if (StringUtils.isBlank(rec.getStringValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.effectiveFromDate.required",
                            new String[]{rowNum}, "entityIdNoTypeCode", rowId);
                } else if (!FormatUtils.isDate(rec.getStringValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.invalidEffectiveFromDate.error",
                            new String[]{rowNum, rec.getStringValue("effectiveFromDate")}, "effectiveFromDate", rowId);
                    rec.setFieldValue("effectiveFromDate", "");
                }

                if (StringUtils.isBlank(rec.getStringValue("effectiveToDate"))) {
                    rec.setFieldValue("effectiveToDate", lastEndDate);
                } else if (!FormatUtils.isDate(rec.getStringValue("effectiveToDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.invalidEffectiveToDate.error",
                            new String[]{rowNum, rec.getStringValue("effectiveToDate")}, "effectiveToDate", rowId);
                    rec.setFieldValue("effectiveToDate", "");
                }

                if (StringUtils.isBlank(rec.getStringValue("externalId"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.externalId.required",
                            new String[]{rowNum}, "externalId", rowId);
                }

                if (MessageManager.getInstance().hasErrorMessages())
                    throw new ValidationException("Invalid Data in Client ID Grid.");

                // Validation:  End Date must be greater than or equal to Start Date
                if (rec.getDateValue("effectiveToDate").before(rec.getDateValue("effectiveFromDate"))) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.EndDateBeforeStartDate.error",
                            new String[]{rowNum}, "effectiveToDate", rowId);
                    throw new ValidationException("End Date before Start Date.");
                }
            }

            if (!rec.isUpdateIndicatorDeleted() && "Y".equals(rec.getStringValue("primaryB"))) {
                primaryId++;
                if (primaryId > 1) {
                    MessageManager.getInstance().addErrorMessage("ci.maintainClientId.moreThanOnePrimaryId.error", "primaryB");
                    throw new ValidationException("More than one primary ID.");
                }
            }


        }
    }

    private ClientIdDAO clientIdDAO;
}
