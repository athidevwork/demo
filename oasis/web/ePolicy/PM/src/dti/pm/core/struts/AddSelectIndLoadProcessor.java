package dti.pm.core.struts;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.recordset.Field;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.http.RequestIds;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class adds "selectInd" field to record set.
 * The purpose of this class is to display select checkbox for option selection page. 
 *
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   March 15, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddSelectIndLoadProcessor implements RecordLoadProcessor {
    public static final String BEAN_NAME = "AddSelectIndLoadProcessor";

    /**
     * Add Select Ind field
     *
     * @param record
     * @param rowIsOnCurrentPage
     * @return boolean
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessRecord",
                new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        record.setFieldValue(RequestIds.SELECT_IND, new Field(new Long(0)));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "postProcessRecord");
        }
        return true;
    }

    /**
     * get instance
     *
     * @return AddSelectIndLoadProcessor
     */
    public synchronized static AddSelectIndLoadProcessor getInstance() {
        Logger l = LogUtils.enterLog(AddSelectIndLoadProcessor.class, "getInstance");

        AddSelectIndLoadProcessor instance;
        instance = (AddSelectIndLoadProcessor) ApplicationContext.getInstance().getBean(BEAN_NAME);

        l.exiting(AddSelectIndLoadProcessor.class.getName(), "getInstance", instance);
        return instance;
    }

    public void postProcessRecordSet(RecordSet recordSet) {
    }
}
