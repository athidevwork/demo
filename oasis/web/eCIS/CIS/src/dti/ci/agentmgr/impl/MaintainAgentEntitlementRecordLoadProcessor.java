package dti.ci.agentmgr.impl;

import dti.ci.agentmgr.AgentFields;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * Page entitlement load processor for maintain agent
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 16, 2011
 *
 * @author yjmiao
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class MaintainAgentEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord",
                new Object[]{record, String.valueOf(rowIsOnCurrentPage)});

        if(record.hasStringValue(AgentFields.AGENT_ID)){
            record.setFieldValue(IS_OUTPUT_AVAILABLE, YesNoFlag.Y);
        }
        else{
            record.setFieldValue(IS_OUTPUT_AVAILABLE,YesNoFlag.N);
        }

        l.exiting(getClass().getName(), "postProcessRecord"); 
        return true;
    }

    public static void setEntitlementValuesForAgent(Record agentRecord) {
        MaintainAgentEntitlementRecordLoadProcessor loadProcessor =
                new MaintainAgentEntitlementRecordLoadProcessor(agentRecord);
        loadProcessor.postProcessRecord(agentRecord, true);
    }

    public Record getAgentRecord() {
        return m_agentRecord;
    }

    public void setAgentRecord(Record inputRecord) {
        this.m_agentRecord = inputRecord;
    }

    public MaintainAgentEntitlementRecordLoadProcessor(Record agentRecord) {
        setAgentRecord(agentRecord);
    }

    private Record m_agentRecord;
    public static final String IS_OUTPUT_AVAILABLE = "isOutputAvailable";
}
