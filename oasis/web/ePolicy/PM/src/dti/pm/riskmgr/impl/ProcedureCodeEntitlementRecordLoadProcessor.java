package dti.pm.riskmgr.impl;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Field;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.ScreenModeCode;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.core.http.RequestIds;

import java.util.logging.Logger;
import java.util.logging.Level;

/* *
 * <p/>
 * This class extends the default record load processor to enforce entitlements for select procedure code web page.
 * This class works in conjunction with pageEntitlements.xml configuration.
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 8, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2010       dzhang    moved the initial checkbox's status from javacript to this class.
 * 10/10/2011       dzhang    Modified isDoneAvailable().
 * 11/13/2014       kxiang    157730 - Removed codes about policy header.
 * ---------------------------------------------------
 */

public class ProcedureCodeEntitlementRecordLoadProcessor extends DefaultRecordLoadProcessor {


    /**
     * Process the given record after it's been loaded to enforce page entitlements.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord",
                new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        String proCode = record.getStringValue("procedureCode");
        if(!StringUtils.isBlank(proCode)) {
            if(isinitialToSelected(proCode,getProcedureCodes())) {
               record.setFieldValue(RequestIds.SELECT_IND, new Field(new Long(-1)));
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }

    /**
     * Check if the checkbox need to initialize to selected.
     *
     * @param proItem
     * @param proCodes
     * @return
     */
    private boolean isinitialToSelected(String proItem, String proCodes) {
        boolean isSelected = false;
        if (!StringUtils.isBlank(proCodes)) {
            String[] procedureList = proCodes.split(",");
            for (int i = 0; i < procedureList.length; i++) {
                if (proItem.equalsIgnoreCase(procedureList[i])) {
                    isSelected = true;
                    break;
                }
            }
        }

        return isSelected;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});
        Record summaryRec = recordSet.getSummaryRecord();
        summaryRec.setFieldValue("isDoneAvailable", isDoneAvailable());
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    /**
     * check ok option avaiable
     *
     * @return boolean
     */
    private boolean isDoneAvailable() {

        boolean isAvailable = false;
        if (getInputRecord().hasStringValue(IS_READ_ONLY) && !YesNoFlag.getInstance(getInputRecord().getStringValue(IS_READ_ONLY)).booleanValue()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public ProcedureCodeEntitlementRecordLoadProcessor(Record inputRecord, String procedureCodes) {
        setInputRecord(inputRecord);
        setProcedureCodes(procedureCodes);
    }

    public void setProcedureCodes(String procedureCodes) {
        this.m_procedureCode = procedureCodes;
    }

    public String getProcedureCodes() {
        return m_procedureCode;
    }

    public void setInputRecord(Record inputRecord) {
        this.m_inputRecord = inputRecord;
    }

    public Record getInputRecord() {
        return m_inputRecord;
    }

    private String m_procedureCode;
    private Record m_inputRecord;
    private final String IS_READ_ONLY = "isReadOnly";

}
