package dti.ci.agentmgr.impl;

import dti.oasis.validationmgr.impl.DateRecordValidator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.messagemgr.MessageManager;
import dti.ci.agentmgr.AgentFields;

import java.util.logging.Logger;

/**
 * This class implements validation of date.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2008
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/07/2008       yhyang      Issue#86934 Move CIS Agent to eCIS.
 * ---------------------------------------------------
 */
public class ContractCommissionDateRecordSetValidator extends DateRecordValidator {

    /**
     * Override this method to add rowid for parent grid (contract grid)
     *
     * @param inputRecord
     * @param dateFieldName
     */
    protected void addErrorMessage(Record inputRecord, String dateFieldName) {
        Logger l = LogUtils.enterLog(getClass(), "addErrorMessage", new Object[]{inputRecord, dateFieldName});
        String rowId = inputRecord.getStringValue(getIdFieldName());

        String agentLicenseId = AgentFields.getAgentLicenseId(inputRecord);

        //get contract rowid
        for (int i = 0; i < getParentResultSet().getSize(); i++) {
            Record contractRecord = getParentResultSet().getRecord(i);
            String contractRowId = AgentFields.getAgentLicenseId(contractRecord);
            if (contractRowId.equals(agentLicenseId)) {
                rowId = contractRowId + "," + rowId;
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
    public ContractCommissionDateRecordSetValidator() {
        super();
    }

    public ContractCommissionDateRecordSetValidator(String[] dateFieldNames, String messageKey) {
        super(dateFieldNames, messageKey);
    }

    public ContractCommissionDateRecordSetValidator(String[] dateFieldNames, String messageKey, String idFieldName) {
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
