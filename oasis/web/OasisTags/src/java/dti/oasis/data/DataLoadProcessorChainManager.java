package dti.oasis.data;

import dti.oasis.util.ChainManager;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the DataLoadProcessor and ChainManager interfaces
 * to act as a ChainManager of DataLoadProcessor objects. Specifically, this class is configured with a
 * List of DataLoadProcessor objects. It implements the methods of the DataLoadProcessor
 * by forwarding the calls to the contained DataLoadProcessor objects, in the order they appear in the List.
 * If any of the contained DataLoadProcessor objects returns false from the postProcessDataRow method,
 * processing halts and false is returned immediately.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class DataLoadProcessorChainManager implements DataLoadProcessor, ChainManager {

    /**
     * Construct an empty Chain Manager.
     */
    public DataLoadProcessorChainManager() {
    }

    /**
     * Construct this Chain Manager with a Load Processor.
     */
    public DataLoadProcessorChainManager(DataLoadProcessor loadProcessor) {
        addDataRecordLoadProcessor(loadProcessor);
    }

    /**
     * Set the Load Processors for this Chain Manager.
     */
    public void setDataLoadProcessors(List loadProcessors) {
        m_loadProcessors = loadProcessors;
    }

    /**
     * Add the given load processor to this Chain Manager.
     */
    public void addDataRecordLoadProcessor(DataLoadProcessor loadProcessor) {
        m_loadProcessors.add(loadProcessor);
    }

    /**
     * Delegate the postProcessDataRow invokation to all contained DataLoadProcessors.
     * If any of the contained DataLoadProcessors throw an exception, it is not caught.
     *
     * @return true if all contained DataLoadProcessors returned true,
     * or false if any contained DataLoadProcessor returned false;
     */
    public boolean postProcessDataRow(DisconnectedResultSet.DataRow dataRow) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "postProcessDataRow", new Object[]{dataRow});
        }

        boolean addDataRow = true;
        Iterator iter = m_loadProcessors.iterator();
        while (addDataRow && iter.hasNext()) {
            DataLoadProcessor dataLoadProcessor = (DataLoadProcessor) iter.next();
            addDataRow = dataLoadProcessor.postProcessDataRow(dataRow);
        }

        l.exiting(getClass().getName(), "postProcessDataRow", String.valueOf(addDataRow));
        return addDataRow;
    }

    /**
     * Delegate the postProcessDisconnectedResultSet invokation to all contained DataLoadProcessors.
     * If any of the contained DataLoadProcessors throw an exception, it is not caught.
     */
    public void postProcessDisconnectedResultSet(DisconnectedResultSet rs) {
        l.entering(getClass().getName(), "postProcessDisconnectedResultSet");

        Iterator iter = m_loadProcessors.iterator();
        while (iter.hasNext()) {
            DataLoadProcessor dataLoadProcessor = (DataLoadProcessor) iter.next();
            dataLoadProcessor.postProcessDisconnectedResultSet(rs);
        }

        l.exiting(getClass().getName(), "postProcessDisconnectedResultSet");
    }

    private List m_loadProcessors = new ArrayList();
    private final Logger l = LogUtils.getLogger(getClass());
}