package dti.pm.workflowmgr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;

/**
 * Helper constants and set/get methods to access WorkFlow Fields.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 24, 2012
 *
 * @author andy
 */

/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 * ---------------------------------------------------
 */
public class WorkFlowFields {

    public static final String CONTINUE_SAVE_OFFICIAL = "continueSaveOfficial";

    public static void setIsContinueSaveOfficial(Record record, YesNoFlag isContinueSaveOfficial) {
        record.setFieldValue(CONTINUE_SAVE_OFFICIAL, isContinueSaveOfficial);
    }

    public static YesNoFlag getIsContinueSaveOfficial(Record record) {
        return YesNoFlag.getInstance(record.getStringValue(CONTINUE_SAVE_OFFICIAL));
    }

}
