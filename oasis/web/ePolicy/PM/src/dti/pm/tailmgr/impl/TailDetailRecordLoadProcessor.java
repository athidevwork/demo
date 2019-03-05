package dti.pm.tailmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.UpdateIndicator;
import dti.pm.coveragemgr.CoverageFields;
import dti.pm.tailmgr.TailFields;

import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This load processor is used to bind the detail record to its tail coverage record
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 9, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class TailDetailRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Returns a synchronized static instance of Tail Record Load Processor that will stores
     * each tail record in a hash map keyed by the coverageBaseRecordId
     *
     * @return an instance of TailRecordLoadProcessor class
     */
    public synchronized static TailDetailRecordLoadProcessor getInstance(Map tailReocrdMap) {
        Logger l = LogUtils.enterLog(TailDetailRecordLoadProcessor.class, "getInstance");

        TailDetailRecordLoadProcessor instance = new TailDetailRecordLoadProcessor();
        instance.setTailRecordMap(tailReocrdMap);

        l.exiting(TailDetailRecordLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record,new Boolean(rowIsOnCurrentPage)});
        }


        String covBaseRecordId = CoverageFields.getCoverageBaseRecordId(record);
        //get tail record in a hash map keyed by the tailCovBaseRecordId
        Record tailRecord = (Record) getTailRecordMap().get(covBaseRecordId);
        if (tailRecord != null) {
            tailRecord.setFields(record, false);

            //set default field value
            String anuualBaseRate = TailFields.getAnnualBaseRate(tailRecord);
            String tailGrossPrem = TailFields.getTailGrossPremium(tailRecord);
            if (!StringUtils.isBlank(anuualBaseRate) && !anuualBaseRate.equals(tailGrossPrem)) {
                TailFields.setTailGrossPremium(tailRecord, anuualBaseRate);
            }else if(StringUtils.isBlank(anuualBaseRate)){
                String ratingModeCode = TailFields.getRatingModuleCode(tailRecord);
                if ("M".equals(ratingModeCode)) {
                    // If the rating module code is "M" and
                    // the annual base rate is null this is defaulted to 0.00
                    TailFields.setAnnualBaseRate(tailRecord, "0");
                }
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord", new Boolean(true));
        }        
        return true;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        super.postProcessRecordSet(recordSet);
    }

    public Map getTailRecordMap() {
        return m_tailRecordMap;
    }

    public void setTailRecordMap(Map tailRecordMap) {
        m_tailRecordMap = tailRecordMap;
    }

    private Map m_tailRecordMap;
}
