package dti.pm.agentmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.agentmgr.AgentFields;
import dti.pm.agentmgr.dao.AgentDAO;
import dti.pm.agentmgr.dao.AgentJdbcDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.entitlementmgr.EntitlementFields;
import dti.pm.busobjs.TransactionCode;
import dti.pm.transactionmgr.TransactionFields;

import java.util.logging.Logger;
import java.util.Date;
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
 * 11/19/2015       eyin        167171 - Modified getInitialEntitlementValuesForAgent(), Add synchronized lock on this function.
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

        record.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.N);

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
            pageEntitlementFields.add(IS_DELETE_AVAILABLE);
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }

    /**
     * Return a Record of initial entitlement values for a new Agent record.
     */
    public synchronized static Record getInitialEntitlementValuesForAgent() {
        if (c_initialEntitlementValues == null) {
            c_initialEntitlementValues = new Record();

            c_initialEntitlementValues.setFieldValue(IS_DELETE_AVAILABLE, YesNoFlag.Y);
        }
        return c_initialEntitlementValues;
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public AgentGridRecordLoadProcessor() {
    }


    private static Record c_initialEntitlementValues;
    private static final String IS_DELETE_AVAILABLE = "isDeleteAvailable";

}
