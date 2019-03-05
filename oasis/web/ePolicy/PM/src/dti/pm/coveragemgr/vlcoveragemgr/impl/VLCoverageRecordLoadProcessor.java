package dti.pm.coveragemgr.vlcoveragemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.coveragemgr.vlcoveragemgr.VLCoverageFields;
import dti.pm.policymgr.PolicyHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * Record load processor for loading data in VL coverage component
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 7, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/19/16  lzhang use closing trans log to control DisplayIndicater
 * ---------------------------------------------------
 */
public class VLCoverageRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        ScreenModeCode vlScreenModeCode = VLCoverageFields.getVLScreenModeCode(getInputRecord());
        String tranId = getPolicyHeader().getLastTransactionInfo().getTransactionLogId();

        //filter data
        if (vlScreenModeCode.isWIP() || vlScreenModeCode.isViewEndquote() || vlScreenModeCode.isOosWIP()
            || vlScreenModeCode.isResinstateWIP() || vlScreenModeCode.isCancelWIP()) {
            String closingTransLogId = VLCoverageFields.getClosingTransLogId(record);
            if (closingTransLogId != null && closingTransLogId.equals(tranId)) {
                record.setDisplayIndicator(YesNoFlag.N);
            }
        }
        return true;
    }


    public VLCoverageRecordLoadProcessor(PolicyHeader policyHeader, Record inputRecord) {
        setInputRecord(inputRecord);
        setPolicyHeader(policyHeader);
    }

    private Record getInputRecord() {
        return m_inputRecord;
    }

    private void setInputRecord(Record inputRecord) {
        m_inputRecord = inputRecord;
    }

    private PolicyHeader getPolicyHeader() {
        return m_policyHeader;
    }

    private void setPolicyHeader(PolicyHeader policyHeader) {
        m_policyHeader = policyHeader;
    }

    private PolicyHeader m_policyHeader;
    private Record m_inputRecord;

}
