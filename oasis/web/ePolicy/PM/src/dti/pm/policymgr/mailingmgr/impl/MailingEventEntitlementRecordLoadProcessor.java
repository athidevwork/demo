package dti.pm.policymgr.mailingmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class extends the default record load processor to enforce entitlements for the mailnig event web page.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 24, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/04/2007       fcb         postProcessRecordSet: notGenerated added
 *                              when recordSet size is 0.
 * ---------------------------------------------------
 */

public class MailingEventEntitlementRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});


        String generateDate = record.getStringValue("generateDate");


        if (StringUtils.isBlank(generateDate)) {
            record.setFieldValue("isDelEventAvailable", YesNoFlag.Y);
            record.setFieldValue("notGenerated", YesNoFlag.Y);
            record.setFieldValue("isGenerateEventAvailable", YesNoFlag.Y);
        }

        else {
            record.setFieldValue("isDelEventAvailable", YesNoFlag.N);
            record.setFieldValue("notGenerated", YesNoFlag.N);
            record.setFieldValue("isGenerateEventAvailable", YesNoFlag.N);
        }
       record.setFieldValue("isAddAttributeAvailable",YesNoFlag.Y);


        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("isDelEventAvailable");
            pageEntitlementFields.add("isAddAttributeAvailable");
            pageEntitlementFields.add("notGenerated");
            pageEntitlementFields.add("isGenerateEventAvailable");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }
      /**
     * Set initial engitlement Values for mailing event
     *
     * @param record
     */
   public void setInitialEntitlementValuesForMailingEvent(Record record) {
           postProcessRecord(record, true);
           record.setFieldValue("isGenerateEventAvailable", YesNoFlag.N);
      }

    public MailingEventEntitlementRecordLoadProcessor() {
    }


}
