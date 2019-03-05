package dti.pm.core.data;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.RecordMode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.Term;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * This class extends the RecordLoadProcessor to provide filter official row for end quote.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 20, 2010
 *
 * @author syang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/21/2014       jyang      155342 - Modified setEndQuote method, removed '!isWipAvailable' condition.
 * ---------------------------------------------------
 */
public class FilterOfficialRowForEndquoteRecordLoadProcessor implements RecordLoadProcessor {

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * Filter official row for end quote.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet");
        if (m_isEndQuote) {
            for (int i = 0; i < recordSet.getSize(); i++) {
                Record record = (Record) recordSet.getRecord(i);
                String pk = record.getStringValue(m_primaryKeyName);
                String officialRecordId = record.hasStringValue(m_officialRecordIdName) ? record.getStringValue(m_officialRecordIdName) : null;
                String recordModeString = record.hasStringValue(m_recordModeCodeName) ? record.getStringValue(m_recordModeCodeName) : null;
                RecordMode recordModeCode = StringUtils.isBlank(recordModeString) ? null : RecordMode.getInstance(recordModeString);
                // The officialRecordId of current record should be greater than zero and to find its official record.
                if (!StringUtils.isBlank(officialRecordId) && Long.parseLong(officialRecordId) > 0 &&
                    recordModeCode != null && !recordModeCode.isOfficial()) {
                    Iterator recordIt = recordSet.getRecords();
                    while (recordIt.hasNext()) {
                        Record innerRecord = (Record) recordIt.next();
                        String innerPk = innerRecord.getStringValue(m_primaryKeyName);
                        String innerRecordModeString = innerRecord.hasStringValue(m_recordModeCodeName) ? innerRecord.getStringValue(m_recordModeCodeName) : null;
                        RecordMode innerRecordModeCode = StringUtils.isBlank(innerRecordModeString) ? null : RecordMode.getInstance(innerRecordModeString);
                        // Find the official record and set display indicator to "N".
                        if (!pk.equals(innerPk) && officialRecordId.equals(innerPk) && innerRecordModeCode != null && innerRecordModeCode.isOfficial()) {
                            innerRecord.setDisplayIndicator(YesNoFlag.N);
                            // Update recordSet since a new column "displayIndicator" has been added to the record.
                            if (recordSet.getFieldCount() < innerRecord.getFieldCount()) {
                                recordSet.addFieldNameCollection(innerRecord.getFieldNames());
                            }
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * Overloaded constructor for initializing the member variables with provided parameters.
     *
     * @param policyHeader
     * @param primaryKeyName
     */
    public FilterOfficialRowForEndquoteRecordLoadProcessor(PolicyHeader policyHeader, String primaryKeyName) {
        m_primaryKeyName = primaryKeyName;
        m_recordModeCodeName = "recordModeCode";
        m_officialRecordIdName = "officialRecordId";
        setEndQuote(policyHeader);
    }

    /**
     * Overloaded constructor for initializing the member variables with provided parameters.
     *
     * @param policyHeader
     * @param primaryKeyName
     */
    public FilterOfficialRowForEndquoteRecordLoadProcessor(PolicyHeader policyHeader, String primaryKeyName,
                                                           String recordModeCodeName, String officialRecordIdName) {
        m_primaryKeyName = primaryKeyName;
        m_recordModeCodeName = recordModeCodeName;
        m_officialRecordIdName = officialRecordIdName;
        setEndQuote(policyHeader);
    }

    /**
     * Check the end quote mode.
     *
     * @param policyHeader
     */
    private void setEndQuote(PolicyHeader policyHeader) {
        PolicyViewMode policyViewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
        // The following are the same as RowAccessorRecordLoadProcessor.
        Term currentTerm = policyHeader.getPolicyTerm(policyHeader.getPolicyTermHistoryId());
        boolean isEndorsementQuoteAailable = currentTerm.isEndorsementQuoteExists() || currentTerm.isRenewalQuoteExists();
        if (policyViewMode.equals(PolicyViewMode.ENDQUOTE) && isEndorsementQuoteAailable) {
            m_isEndQuote = true;
        }
    }

    private String m_primaryKeyName;
    private String m_recordModeCodeName;
    private String m_officialRecordIdName;
    private boolean m_isEndQuote = false;
}