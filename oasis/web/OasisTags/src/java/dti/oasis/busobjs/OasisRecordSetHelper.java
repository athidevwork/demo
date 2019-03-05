package dti.oasis.busobjs;

import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;

/**
 * This class returns all the changed records in the inputRecords
 * and adds a new field "rowStatus" into every changed record, the field value must be one of "NEW","MODIFIED" and "DELETED",
 * it is used to indicat the changed status of the record.
 * <p/>
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   March 20, 2009
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class OasisRecordSetHelper {

    /**
     * Set a new field "rowStatus" to every modified record.
     *
     * @param inputRecords the initial inputRecords
     * @return RecordSet the modified reocrds
     */
    public static RecordSet setRowStatusOnModifiedRecords(RecordSet inputRecords) {
        /* Determine if anything has changed */
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        /* Create a new RecordSet to include all added, modified and deleted records */
        RecordSet allRecords = new RecordSet();

        /* Add the inserted records into allRecords for batch mode update */
        if (insertedRecords.getSize() > 0) {
            insertedRecords.setFieldValueOnAll(ROW_STATUS, NEW);
            allRecords.addRecords(insertedRecords);
        }

        /* Add the updated records into allRecords for batch mode update */
        if (updatedRecords.getSize() > 0) {
            updatedRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);
            allRecords.addRecords(updatedRecords);
        }

        /* Add the deleted records into allRecords for batch mode delete */
        if (deletedRecords.getSize() > 0) {
            deletedRecords.setFieldValueOnAll(ROW_STATUS, DELETED);
            allRecords.addRecords(deletedRecords);
        }

        return allRecords;
    }

    public static final String ROW_STATUS = "rowStatus";
    public static final String NEW = "NEW";
    public static final String MODIFIED = "MODIFIED";
    public static final String DELETED = "DELETED";
}
