package dti.ci.agentmgr.impl;

import dti.ci.agentmgr.AgentFields;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.validationmgr.impl.DateRecordValidator;

import java.util.logging.Logger;

/**
 * This class implements validation of date.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 18, 2016
 *
 * @author iwang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AgentStaffOverrideDateRecordSetValidator extends DateRecordValidator {

    /**
     * Override this method to add rowid for parent grid (Agent Staff grid)
     *
     * @param inputRecord
     * @param dateFieldName
     */
    protected void addErrorMessage(Record inputRecord, String dateFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "addErrorMessage", new Object[]{inputRecord, dateFieldName});
        String rowId = inputRecord.getStringValue(getIdFieldName());

        String agentStaffId = AgentFields.getAgentStaffId(inputRecord);

        //get override rowid
        for (int i = 0; i < getParentResultSet().getSize(); i++) {
            Record parentRecord = getParentResultSet().getRecord(i);
            String parentId = AgentFields.getAgentStaffId(parentRecord);
            if (parentId.equals(agentStaffId)) {
                rowId = parentId + "," + rowId;
                break;
            }
        }
        MessageManager.getInstance().addErrorMessage(getMessageKey(), getParmameters(inputRecord, dateFieldName),
            dateFieldName, rowId);
        l.exiting(getClass().getName(), "addErrorMessage");
    }

    //-------------------------------------------------
    // constructor
    //-------------------------------------------------
    public AgentStaffOverrideDateRecordSetValidator() {
        super();
    }

    public AgentStaffOverrideDateRecordSetValidator(String[] dateFieldNames, String messageKey) {
        super(dateFieldNames, messageKey);
    }

    public AgentStaffOverrideDateRecordSetValidator(String[] dateFieldNames, String messageKey, String idFieldName) {
        super(dateFieldNames, messageKey, idFieldName);
    }

    /**
     * get parent resultset
     * @return
     */
    public RecordSet getParentResultSet() {
        return m_parentResultSet;
    }

    /**
     * set parent resultset
     * @param parentResultSet
     */
    public void setParentResultSet(RecordSet parentResultSet) {
        m_parentResultSet = parentResultSet;
    }

    /**
     * parent result set
     */
    private RecordSet m_parentResultSet;

}
