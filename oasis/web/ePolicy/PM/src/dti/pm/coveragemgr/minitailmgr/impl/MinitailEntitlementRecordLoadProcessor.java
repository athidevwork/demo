package dti.pm.coveragemgr.minitailmgr.impl;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Field;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * This class is a mini tail load processor.See "load process pattern for more info"
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 27, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * Jul 27, 2007     zlzhu       Created
 * 01/04/2007       fcb         postProcessRecordSet: logic added for 0 size recordSet. 
 * ---------------------------------------------------
 */

public class MinitailEntitlementRecordLoadProcessor implements RecordLoadProcessor {
    /**
     * do some process after loading data
     *
     * @param record  input record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.enterLog(getClass(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        //set a flag,to show if this record is ajax checked,at the beginning it has no value
        record.setField("hasChecked",new Field(""));
        l.exiting(getClass().getName(), "postProcessRecord");
        return true;
    }
    /**
     * do some process on whole record set
     *
     * @param recordSet input record set
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (recordSet.getSize() == 0) {
            ArrayList pageEntitlementFields = new ArrayList();
            pageEntitlementFields.add("hasChecked");
            recordSet.addFieldNameCollection(pageEntitlementFields);
        }
    }
}
