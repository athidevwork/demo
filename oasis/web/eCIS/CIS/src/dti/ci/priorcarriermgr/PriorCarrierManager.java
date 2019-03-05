package dti.ci.priorcarriermgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/27/12
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/08/2012       kshen       Added methods for refactor Prior Carrier page.
 * ---------------------------------------------------
 */
public interface PriorCarrierManager {
    /**
     * Load all prior carrier of a entity by filter criteria.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPriorCarrier(Record inputRecord);

    /**
     * Get the initial values for prior carrier.
     * @return
     */
    public Record getInitialValuesForPriorCarrier(Record inputRecord);

    /**
     * Get the default term year.
     * @return
     */
    public String getDefaultTermYear(Record inputRecord);

    /**
     * Save all the prior carrier records.
     * @param inputRecords
     * @return
     */
    public int saveAllPriorCarrier(RecordSet inputRecords);

     /**
     * Return prior carrier history info.
     *
     * @param input prior carrier history
     * @return RecordSet
     * @throws Exception e
     */
    public RecordSet loadPriorCarrierHistory(Record input) throws Exception;

     /**
     * Method to save prior carrier history information
     *
     * @param inputRecords
     * @return int
     */
    public int savePriorCarrierHistory(RecordSet inputRecords);

}
