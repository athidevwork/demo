package dti.ci.propertymgr.impl;

import dti.ci.core.CIFields;
import dti.ci.propertymgr.PropertyFields;
import dti.ci.propertymgr.PropertyManager;
import dti.ci.propertymgr.dao.PropertyDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for Property
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 28, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/13/2009       kshen       Added codes to handle db error.
 * 04/29/2014       kshen       Issue 149462. Throw ValidationException if validation failed.
 * 07/29/2015       dpang       Issue 160504. Check visibility and necessity before validating propertyDescription.
 * ---------------------------------------------------
*/
public class PropertyManagerImpl implements PropertyManager {

    /**
     * Load all property of an entity.
     *
     * @param record
     * @return
     */
    @Override
    public RecordSet loadAllProperty(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProperty", new Object[]{record});
        }

        Record entityRecord = new Record();
        CIFields.setEntityId(entityRecord, CIFields.getPk(record));

        RecordSet rs = getPropertyDAO().loadAllProperty(entityRecord, new RecordLoadProcessor() {
            public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
                return true;
            }

            public void postProcessRecordSet(RecordSet recordSet) {
                AddSelectIndLoadProcessor.getInstance().postProcessRecordSet(recordSet);
                recordSet.setFieldValueOnAll("userSelectedB", "N");
            }
        });

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProperty", rs);
        }

        return rs;
    }

    /**
     * Save all property.
     *
     * @param rs
     * @return
     */
    @Override
    public int saveAllProperty(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllProperty", new Object[]{rs});
        }

        validateAllProperty(rs);

        // Get the changes
        RecordSet changedRecords = rs.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));

        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);

        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getPropertyDAO().saveAllProperty(changedRecords);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllProperty", updateCount);
        }

        return updateCount;
    }

    protected void validateAllProperty(RecordSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllProperty", new Object[]{rs});
        }

        Iterator records = rs.getRecords();
        OasisFields fields = (OasisFields) RequestStorageManager.getInstance().get(IOasisAction.KEY_FIELDS);

        boolean isDescRequired = false;
        if(fields.hasField(PropertyFields.FLD_PROPERTY_DESCRIPTION)) {
            OasisFormField descField = (OasisFormField) fields.getField(PropertyFields.FLD_PROPERTY_DESCRIPTION);
            isDescRequired = descField.getIsVisible() && descField.getIsRequired();
        }

        while (records.hasNext()) {
            Record record = (Record) records.next();

            if (record.getUpdateIndicator().equals(UpdateIndicator.INSERTED)
                    || record.getUpdateIndicator().equals(UpdateIndicator.UPDATED)) {
                String rowId = record.getRowId();

                String propertyDescription = PropertyFields.getPropertyDescription(record);
                if (isDescRequired && StringUtils.isBlank(propertyDescription)) {
                    MessageManager.getInstance().addErrorMessage("property.desc.empty.error",
                            "clientProperty_propertyDescription", rowId);
                }

                String acquisitionDate = PropertyFields.getAcquisitionDate(record);
                if (!StringUtils.isBlank(acquisitionDate)) {
                    Date lowDate = DateUtils.makeDate(1800, 1, 1);
                    if (DateUtils.parseDate(acquisitionDate).before(lowDate)
                            || DateUtils.parseDate(acquisitionDate).after(new Date())) {
                        MessageManager.getInstance().addErrorMessage("property.acquisitionDate.invalid.error",
                                "clientProperty_acquisitionDate", rowId);
                    }
                }

                String acquisitionPrice = PropertyFields.getAcquisitionPrice(record);
                if (!StringUtils.isBlank(acquisitionPrice)) {
                    if (NumberUtils.toDouble(acquisitionPrice) < 0) {
                        MessageManager.getInstance().addErrorMessage("property.acquisitionPrice.invalid.error",
                                "clientProperty_acquisitionPrice", rowId);
                    }
                }

                String yearBuilt = PropertyFields.getYearBuilt(record);
                if (!StringUtils.isBlank(yearBuilt)) {
                    if (NumberUtils.toInt(yearBuilt) < 1600
                            || NumberUtils.toInt(yearBuilt) > DateUtils.getYear(new Date())) {
                        MessageManager.getInstance().addErrorMessage("property.yearBuilt.invalid.error",
                                "clientProperty_acquisitionPrice", rowId);
                    }
                }
                
                String bldgSqFt = PropertyFields.getBldgSqFt(record);
                if (!StringUtils.isBlank(bldgSqFt)) {
                    if (NumberUtils.toDouble(bldgSqFt) < 0) {
                        MessageManager.getInstance().addErrorMessage("property.bldgSqFt.invalid.error",
                                "clientProperty_bldgSqFt", rowId);
                    }
                }

                String numberOfFloors = PropertyFields.getNumberOfFloors(record);
                if (!StringUtils.isBlank(numberOfFloors)) {
                    if (NumberUtils.toDouble(numberOfFloors) < 0) {
                        MessageManager.getInstance().addErrorMessage("property.numberOfFloors.invalid.error",
                                "clientProperty_numberOfFloors", rowId);
                    }
                }

                String numberOfBoilers = PropertyFields.getNumberOfBoilers(record);
                if (!StringUtils.isBlank(numberOfBoilers)) {
                    if (NumberUtils.toDouble(numberOfBoilers) < 0) {
                        MessageManager.getInstance().addErrorMessage("property.numberOfBoilers.invalid.error",
                                "clientProperty_numberOfBoilers", rowId);
                    }
                }

                String nfipFloodZone = PropertyFields.getNfipFloodZone(record);
                if (!StringUtils.isBlank(nfipFloodZone)) {
                    if (NumberUtils.toDouble(nfipFloodZone) < 0) {
                        MessageManager.getInstance().addErrorMessage("property.nfipFloodZone.invalid.error",
                                "clientProperty_nfipFloodZone", rowId);
                    }
                }

                if (MessageManager.getInstance().hasErrorMessages()) {
                    throw new ValidationException("Validation property record failed.");
                }
            }

        }

        l.exiting(getClass().getName(), "validateAllProperty");
    }

    /**
     * Save property
     *
     * @param inputRecord
     */
    public Record saveProperty(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePhoneNumber", new Object[]{inputRecord});
        }

        Record recResult = getPropertyDAO().saveProperty(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "savePhoneNumber");
        }
        return recResult;
    }

    public void verifyConfig() {
        if (getPropertyDAO() == null) {
            throw new ConfigurationException("The required property 'propertyDAO' is missing.");
        }
    }

    public PropertyDAO getPropertyDAO() {
        return m_propertyDAO;
    }

    public void setPropertyDAO(PropertyDAO propertyDAO) {
        m_propertyDAO = propertyDAO;
    }

    private PropertyDAO m_propertyDAO;
}
