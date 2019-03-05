package dti.pm.busobjs;

import dti.oasis.busobjs.DisplayIndicator;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.recordset.DisplayIndicatorRecordFilter;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;

import java.util.Iterator;

/**
 * Return the modified records in a RecordSet
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 27, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/20/2008       yhyang      Set rowStatus to DELETED for the change of deleted.
 * 02/25/2014       adeng       Add setDisplayRecordNumberOnRecords() to set the
 *                              display record number to all visible records.
 * ---------------------------------------------------
 */

public class PMRecordSetHelper {
    public static RecordSet setRowStatusOnModifiedRecords(RecordSet inputRecords) {
        /* Determine if anything has changed */
        RecordSet insertedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
        RecordSet updatedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.UPDATED));
        RecordSet deletedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.DELETED));

        /* Create an new RecordSet to include all added and modified records */
        RecordSet allRecords = new RecordSet();

        /* Add the inserted WIP records into allRecords for batch mode update */
        if (insertedRecords.getSize() > 0) {
            insertedRecords.setFieldValueOnAll(ROW_STATUS, NEW);
            allRecords.addRecords(insertedRecords);
        }

        /* Add the updated the OFFICIAL records into allRecords for batch mode update */
        if (updatedRecords.getSize() > 0) {
            updatedRecords.setFieldValueOnAll(ROW_STATUS, MODIFIED);
            allRecords.addRecords(updatedRecords);
        }

        /* Add the deleted the OFFICIAL records into allRecords for batch mode delete */
        if (deletedRecords.getSize() > 0) {
            deletedRecords.setFieldValueOnAll(ROW_STATUS, DELETED);
            allRecords.addRecords(deletedRecords);
        }

        return allRecords;

    }

    public static RecordSet setDisplayRecordNumberOnRecords(RecordSet inputRecords) {
        int displayRecordNumber = 1;
        Iterator rsIt = inputRecords.getRecords();
        while (rsIt.hasNext()) {
            Record record = (Record) rsIt.next();
            if (record.getDisplayIndicatorBooleanValue() && !record.isUpdateIndicatorDeleted()) {
                record.setFieldValue(DISPLAY_RECORD_NUMBER, displayRecordNumber++);
            }
            else {
                record.setFieldValue(DISPLAY_RECORD_NUMBER, 0);
            }

        }
        return inputRecords;
    }

    public static final String DISPLAY_RECORD_NUMBER = "displayRecordNumber";
    private static final String ROW_STATUS = "rowStatus";
    private static final String NEW = "NEW";
    private static final String MODIFIED = "MODIFIED";
    private static final String DELETED = "DELETED";
}
