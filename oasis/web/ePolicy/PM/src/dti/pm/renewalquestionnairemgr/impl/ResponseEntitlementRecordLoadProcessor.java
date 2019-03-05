package dti.pm.renewalquestionnairemgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.util.StringUtils;
import dti.pm.renewalquestionnairemgr.QuestionnaireResponseFields;

/**
 * This class extends the default record load processor to enforce entitlements for renewal questionnaire response web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   July 28, 2008
 *
 * @author yhyang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ResponseEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the given record after it's been loaded.
     * If the comments of current record is not empty,set note to Yes, else default it to No.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true              if this Record should be added to the RecordSet;
     *         false             if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        String status = record.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        QuestionnaireResponseFields.setStatus(record, status);
        record.remove(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (!StringUtils.isBlank(status) && (status.equals(WIP) || status.equals(COMPLETED))) {
            record.setFieldValue("isResponseReopenAvailable", YesNoFlag.N);
            record.setFieldValue("isResponseSaveAvailable", YesNoFlag.Y);
        }
        else {
            record.setFieldValue("isResponseReopenAvailable", YesNoFlag.Y);
            record.setFieldValue("isResponseSaveAvailable", YesNoFlag.N);
        }
        return true;
    }

    private final static String WIP = "WIP";
    private final static String COMPLETED = "COMPLETED";
}
