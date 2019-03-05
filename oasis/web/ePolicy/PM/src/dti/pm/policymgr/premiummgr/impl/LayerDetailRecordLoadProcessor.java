package dti.pm.policymgr.premiummgr.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * This class extends the default record load processor a sum value for the premium layer detail web page. This
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 23, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class LayerDetailRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        double finalLayerPremium = 0;
        if (record.getDoubleValue("layerPremium") != null) {
            finalLayerPremium += record.getDoubleValue("layerPremium").doubleValue();
        }
        if (record.getDoubleValue("adjPremium") != null) {
            finalLayerPremium += record.getDoubleValue("adjPremium").doubleValue();
        }
        record.setFieldValue("finalLayerPremium", (finalLayerPremium + ""));
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
    }


}
