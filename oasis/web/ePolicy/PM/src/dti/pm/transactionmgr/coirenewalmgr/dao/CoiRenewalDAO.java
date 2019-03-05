package dti.pm.transactionmgr.coirenewalmgr.dao;

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
 * 07/05/2010       dzhang     Renamed class & methods name.
 * ---------------------------------------------------
 */

public interface CoiRenewalDAO {

    /**
     * save the Coi renewal data
     * <p/>
     *
     * @param record input record with Coi renewal data.
     */
    public void createCoiRenewal(Record record);

    /**
     * To load all Coi renewal event data.
     * <p/>
     *
     * @param inputRecord input record
     * @return Coi renewal event recordset
     */
    public RecordSet loadAllCoiRenewalEvent(Record inputRecord);

    /**
     * To load all Coi renewal event detail data.
     * <p/>
     *
     * @param inputRecord input record
     * @return Coi renewal event detail recordset
     */
    public RecordSet loadAllCoiRenewalEventDetail(Record inputRecord);
}
