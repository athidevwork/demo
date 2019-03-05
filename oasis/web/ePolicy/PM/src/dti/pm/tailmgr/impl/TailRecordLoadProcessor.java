package dti.pm.tailmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.tailmgr.TailFields;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;

/**
 * This load processor will construct a reocd map which will be used for detail record to bind with tail record
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 9, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/03/2008       sxm         Issue 81453 - added logic to set tail detail columns for dummy MATAIL/PATAIL record 
 *                              in postProcessRecord()
 * ---------------------------------------------------
 */

public class TailRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Returns a synchronized static instance of Tail Record Load Processor that will stores
     *  each tail record in a hash map keyed by the coverageBaseRecordId
     *
     * @return an instance of TailRecordLoadProcessor class
     */
    public synchronized static TailRecordLoadProcessor getInstance() {
        Logger l = LogUtils.enterLog(TailRecordLoadProcessor.class,"getInstance" );

        TailRecordLoadProcessor instance = new TailRecordLoadProcessor();
        instance.setTailRecordMap(new HashMap());

        l.exiting(TailRecordLoadProcessor.class.getName(), "getInstance", instance);
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

        //store each tail record in a hash map keyed by the tailCovBaseRecordId
        getTailRecordMap().put(TailFields.getTailCovBaseRecordId(record),record);

        // handle dummy records for adding MATAIL/PATAIL
        String tailProductCoverageCode = TailFields.getProductCoverageCode(record);
        String prodCovRelTypeCode = TailFields.getProdCovRelTypeCode(record);
        if (StringUtils.isBlank(tailProductCoverageCode) &&
            ("MATAIL".equals(prodCovRelTypeCode) || "PATAIL".equals(prodCovRelTypeCode))) {
            TailFields.setRatingBasis(record, "");
            TailFields.setCoverageLimitCode(record, "");
            TailFields.setTailStatus(record, "");
            TailFields.setTailGrossPremium(record, "");
            TailFields.setTailNetPremium(record, "");
            TailFields.setMainCoverageId(record, "");
            TailFields.setRetroactiveDate(record, "");
            TailFields.setSubCoverageB(record, YesNoFlag.N);
            TailFields.setAdjIncidentLimit(record, "");
            TailFields.setAdjAggregateLimit(record, "");
            TailFields.setTailAccountingFromDate(record, "");
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
