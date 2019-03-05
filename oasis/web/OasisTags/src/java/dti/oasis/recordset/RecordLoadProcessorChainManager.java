package dti.oasis.recordset;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.ChainManager;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the RecordLoadProcessor and ChainManager interfaces
 * to act as a ChainManager of RecordLoadProcessor objects. Specifically, this class is configured with a
 * List of RecordLoadProcessor objects. It implements the methods of the DataLoadProcessor
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
public class RecordLoadProcessorChainManager implements RecordLoadProcessor, ChainManager {

    /**
     * Return a RecordLoadProcessor representing both supplied RecordLoadProcessors.
     * If either of the given load processors is null, the other is returned.
     * Otherwise, a RecordLoadProcessorChainManager is returned containing both load processors.
     * If either or both of the given load processors is already a RecordLoadProcessorChainManager,
     * they are consolidated into a single RecordLoadProcessorChainManager.
     *
     * @param firstLoadProcessor the first RecordLoadProcessor
     * @param secondLoadProcessor the second RecordLoadProcessor
     * @return a RecordLoadProcessor representing both supplied RecordLoadProcessors
     */
    public static RecordLoadProcessor getRecordLoadProcessor(RecordLoadProcessor firstLoadProcessor, RecordLoadProcessor secondLoadProcessor) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(RecordLoadProcessorChainManager.class.getName(), "getRecordLoadProcessor", new Object[]{firstLoadProcessor, secondLoadProcessor});
        }

        RecordLoadProcessor loadProcessor = null;
        if (firstLoadProcessor == null) {
            loadProcessor = secondLoadProcessor;
        } else if (secondLoadProcessor == null) {
            loadProcessor = firstLoadProcessor;
        } else {
            RecordLoadProcessorChainManager cm = null;
            if (firstLoadProcessor instanceof RecordLoadProcessorChainManager) {
                cm = (RecordLoadProcessorChainManager) firstLoadProcessor;
            } else {
                cm = new RecordLoadProcessorChainManager(firstLoadProcessor);
            }

            if (secondLoadProcessor instanceof RecordLoadProcessorChainManager) {
                Iterator iter = ((RecordLoadProcessorChainManager)secondLoadProcessor).m_loadProcessors.iterator();
                while (iter.hasNext()) {
                    cm.addDataRecordLoadProcessor((RecordLoadProcessor) iter.next());
                }
            } else {
                cm.addDataRecordLoadProcessor(secondLoadProcessor);
            }

            loadProcessor = cm;
        }

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(RecordLoadProcessorChainManager.class.getName(), "getRecordLoadProcessor", loadProcessor);
        }
        return loadProcessor;
    }

    /**
     * Construct an empty Chain Manager.
     */
    public RecordLoadProcessorChainManager() {
    }

    /**
     * Construct this Chain Manager with a Load Processor.
     */
    public RecordLoadProcessorChainManager(RecordLoadProcessor loadProcessor) {
        addDataRecordLoadProcessor(loadProcessor);
    }

    /**
     * Construct this Chain Manager with a Load Processor.
     */
    public RecordLoadProcessorChainManager(RecordLoadProcessor firstLoadProcessor, RecordLoadProcessor secondLoadProcessor) {
        addDataRecordLoadProcessor(firstLoadProcessor);
        addDataRecordLoadProcessor(secondLoadProcessor);
    }

    /**
     * Set the Load Processors for this Chain Manager.
     */
    public void setDataRecordLoadProcessors(List loadProcessors) {
        m_loadProcessors = loadProcessors;
    }

    /**
     * Add the given load processor to this Chain Manager.
     */
    public void addDataRecordLoadProcessor(RecordLoadProcessor loadProcessor) {
        m_loadProcessors.add(loadProcessor);
    }

    /**
     * Process the given record after it's been loaded.
     *
     * @param record             the current record
     * @param rowIsOnCurrentPage true if this record is on the current page
     * @return true if this Record should be added to the RecordSet;
     *         false if this Record should be excluded from the RecordSet.
     */
    public boolean postProcessRecord(Record record, boolean rowIsOnCurrentPage) {
        if (l.isLoggable(Level.FINE)) {
            l.entering(getClass().getName(), "postProcessRecord", new Object[]{record, String.valueOf(rowIsOnCurrentPage)});
        }

        boolean addRecord = true;
        Iterator iter = m_loadProcessors.iterator();
        while (addRecord && iter.hasNext()) {
            RecordLoadProcessor loadProcessor = (RecordLoadProcessor) iter.next();
            addRecord = loadProcessor.postProcessRecord(record, rowIsOnCurrentPage);
        }

        if (l.isLoggable(Level.FINE)) {
            l.exiting(getClass().getName(), "postProcessRecord", String.valueOf(addRecord));
        }
        return addRecord;
    }

    /**
     * Process the RecordSet after all records have been loaded and processed..
     *
     * @param recordSet the record set.
     */
    public void postProcessRecordSet(RecordSet recordSet) {
        l.entering(getClass().getName(), "postProcessRecordSet");

        Iterator iter = m_loadProcessors.iterator();
        while (iter.hasNext()) {
            RecordLoadProcessor loadProcessor = (RecordLoadProcessor) iter.next();
            loadProcessor.postProcessRecordSet(recordSet);
        }

        l.exiting(getClass().getName(), "postProcessRecordSet");
    }


    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code>
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `<code>@</code>', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "RecordLoadProcessorChainManager{" +
            "m_loadProcessors=" + m_loadProcessors +
            '}';
    }

    private List m_loadProcessors = new ArrayList();
    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(RecordLoadProcessorChainManager.class);
}
