package dti.oasis.workflowmgr;

import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 1, 2007
 *
 * @author jmpotosky
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/02/2008       Joe         Refactor into dti.oasis.workflowmgr pacakge.
 *                              Remove field "policyNo" and add field "workflowInstanceId".
 * ---------------------------------------------------
 */

public class WorkflowFields {
    public static final String WORKFLOW_INSTANCE_ID = "workflowInstanceId";
    public static final String WORKFLOW_INSTANCE_ID_NAME = "workflowInstanceIdName"; 
    public static final String WORKFLOW_STATE = "workflowState";

    public static String getWorkflowInstanceId(Record record) {
        return record.getStringValue(WORKFLOW_INSTANCE_ID);
    }

    public static void setWorkflowInstanceId(Record record, String workflowInstanceId) {
        record.setFieldValue(WORKFLOW_INSTANCE_ID, workflowInstanceId);
    }

    public static String getWorkflowState(Record record) {
        return record.getStringValue(WORKFLOW_STATE);
    }

    public static void setWorkflowState(Record record, String workflowState) {
        record.setFieldValue(WORKFLOW_STATE, workflowState);
    }
}
