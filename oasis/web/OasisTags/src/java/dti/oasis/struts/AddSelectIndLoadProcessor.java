package dti.oasis.struts;

import dti.oasis.http.RequestIds;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class adds "selectInd" field to record set.
 * The purpose of this class is to display select checkbox for option selection page.
 * <p/>
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Apr 23, 2008
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       kshen       Moved codes about setting select_ind into
 *                              method postProcessRecordSet.
 * ---------------------------------------------------
 */
public class AddSelectIndLoadProcessor implements RecordLoadProcessor {
    public AddSelectIndLoadProcessor(){

    }
    public AddSelectIndLoadProcessor(String selectIndFieldName){
        this.selectIndColumnName = selectIndFieldName;
    }
    /**
     * Add Select Ind field
     *
     * @param record
     * @param rowIsOnCurrentPage
     * @return boolean
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        return true;
    }

    /**
     * get instance
     *
     * @return AddSelectIndLoadProcessor
     */
    public static AddSelectIndLoadProcessor getInstance() {
        l.entering(AddSelectIndLoadProcessor.class.getName(), "getInstance");
        l.exiting(AddSelectIndLoadProcessor.class.getName(), "getInstance", c_addSelectIndLoadProcessor);
        return c_addSelectIndLoadProcessor;
    }

    /**
     *
     * @param recordSet
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecordSet", new Object[]{recordSet});
        }
        recordSet.setFieldValueOnAll(RequestIds.SELECT_IND, new Field(new Long(0)));
        if(!StringUtils.isBlank(selectIndColumnName)){
            recordSet.setFieldValueOnAll(selectIndColumnName, new Field(new Long(0)));
        }
        l.exiting(getClass().getName(), "postProcessRecordSet");
    }

    private static AddSelectIndLoadProcessor c_addSelectIndLoadProcessor = new AddSelectIndLoadProcessor();
    private String selectIndColumnName = null;
    private static final Logger l = LogUtils.getLogger(AddSelectIndLoadProcessor.class);
}
