package dti.ci.agentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;

import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class extends the default record load processor .
 * <p/>
 * <p>Any field that requires entitlement enforcement is determined by an indicator field that gets appended to the list
 * of fields in a record. This record is published as xml data island, which then gets intercepted by pageEntitlements
 * oasis tag to auto-generate javascript that enforces the entitlements.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 1, 2008
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Apr 17, 2008     James       Issue#75265 Modify code according to code review
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * 12/22/2015       jshen       Issue 168341 - Fix the Incorrect lazy initialization and update of static field problem.
 * ---------------------------------------------------
 */
public class AgentGridRecordLoadProcessor extends DefaultRecordLoadProcessor {

    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        record.setFieldValue(AgentGridRecordLoadProcessor.IS_DELETE_AVAILABLE, YesNoFlag.N);

        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }

    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
        // If no record is fetched, add the page entitlement fields to the recordset field collection
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add(AgentGridRecordLoadProcessor.IS_DELETE_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }

    /**
     * Return a Record of initial entitlement values for a new Agent record.
     */
    public static Record getInitialEntitlementValuesForAgent() {
        return InitialEntitlementValuesHolder.c_initialEntitlementValues;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public AgentGridRecordLoadProcessor() {
    }

    private static class InitialEntitlementValuesHolder {
        public static final Record c_initialEntitlementValues = new Record();
        static {
            c_initialEntitlementValues.setFieldValue(AgentGridRecordLoadProcessor.IS_DELETE_AVAILABLE, YesNoFlag.Y);
        }
    }

    private static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";

}
