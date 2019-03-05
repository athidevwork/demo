package dti.pm.riskmgr.impl;

import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.Field;
import dti.oasis.util.LogUtils;
import dti.pm.riskmgr.RiskFields;

import java.util.logging.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Record load processor for Risk list of Copy Address/Phone.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 6, 2008
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class RiskNameRecordLoadProcessor extends DefaultRecordLoadProcessor {
    /**
     * Process the RecordSet for enforcing page entitlements after all records have been loaded and processed.
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecordSet", new Object[]{recordSet});

        RecordSet newRs = new RecordSet();
        Iterator recIter = recordSet.getRecords();
        while (recIter.hasNext()) {
            Record r = (Record) recIter.next();
            if (!RiskFields.getEntityId(r).equals(getSourceEntityId()) && !RiskFields.getRiskStatus(r).isCancelled()) {
                List nameList = r.getFieldNameList();

                // filter out all fields except "entityId", "riskId", "riskName" and "SELECT_IND".
                Record rec = new Record();
                for (int i = 0; i < nameList.size(); i++) {
                    String name = (String) nameList.get(i);
                    if (name.equalsIgnoreCase("entityId") || name.equalsIgnoreCase("riskId")
                        || name.equalsIgnoreCase("riskName") || name.equalsIgnoreCase("SELECT_IND")) {
                        Field field = r.getField(name);
                        rec.setField(name, field);
                    }
                }

                newRs.addRecord(rec);
            }
        }
        Record summaryRec = recordSet.getSummaryRecord();
        List fieldNames = (List) ((ArrayList) newRs.getFieldNameList()).clone();
        if(newRs.getSize() == 0){
            fieldNames = (List) ((ArrayList) recordSet.getFieldNameList()).clone();
        }
        recordSet.clear();
        recordSet.addRecords(newRs);
        recordSet.addFieldNameCollection(fieldNames);
        recordSet.setSummaryRecord(summaryRec);

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    public RiskNameRecordLoadProcessor(String sourceEntityId) {
        setSourceEntityId(sourceEntityId);
    }

    public String getSourceEntityId() {
        return m_sourceEntityId;
    }

    public void setSourceEntityId(String sourceEntityId) {
        m_sourceEntityId = sourceEntityId;
    }

    private String m_sourceEntityId;
}
