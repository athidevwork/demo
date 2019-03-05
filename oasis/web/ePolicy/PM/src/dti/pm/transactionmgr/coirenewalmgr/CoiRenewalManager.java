package dti.pm.transactionmgr.coirenewalmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * this class is an interface for coi renewal manager
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 23, 2010
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/05/2010       dzhang      Rename this file & methods name.
 * ---------------------------------------------------
 */

public interface CoiRenewalManager {
    /**
     * save all the COI renewal data
     * <p/>
     *
     * @param record COI renewal data that needed to save
     */
    public void createCoiRenewal(Record record);

    /**
     * Get the initial values for COI event search criteria
     * <p/>
     *
     * @return the result met the condition
     */
    public Record getInitialValuesForSearchCriteria();

    /**
     * To load all COI renewal event data.
     * <p/>
     *
     * @param inputRecord input record with search criteria.
     * @return a record set met the condition.
     */
    public RecordSet loadAllCoiRenewalEvent(Record inputRecord);

    /**
     * To load all COI renewal event detail data.
     * <p/>
     *
     * @param inputRecord input record with search criteria.
     * @return a record set met the condition.
     */
    public RecordSet loadAllCoiRenewalEventDetail(Record inputRecord);
}
