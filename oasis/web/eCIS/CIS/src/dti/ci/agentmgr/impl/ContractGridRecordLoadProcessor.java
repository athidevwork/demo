package dti.ci.agentmgr.impl;

import dti.oasis.recordset.RecordSet;
import dti.oasis.busobjs.YesNoFlag;

/**
 * The record load processor for Contract grid.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 3, 2010
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ContractGridRecordLoadProcessor extends AgentGridRecordLoadProcessor {


    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
        recordSet.setFieldValueOnAll(IS_SUB_PRODUCER_AVAILABLE, YesNoFlag.Y);
    }

    public ContractGridRecordLoadProcessor() {
    }

    private static final String IS_SUB_PRODUCER_AVAILABLE = "isSubProducerAvailable";
}
