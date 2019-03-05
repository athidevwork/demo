package dti.pm.riskmgr.ibnrriskmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for IBNR risk information.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 07, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface IbnrRiskDAO {

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllIbnrRiskType(Record inputRecord);

    /**
     * Change associated risk
     *
     * @param inputRecord Record contains input values
     */
    public void processChangeAssociatedRisk(Record inputRecord);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key infomation
     * @return
     */
    public RecordSet loadAllAssociatedRisk(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key infomation
     * @return
     */
    public RecordSet loadAllIbnrInactiveRisk(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * load all the risks exist in a given policy that have IBNR inactive risks associated to them
     *
     * @param inputRecord input records that contains key infomation
     * @return
     */
    public RecordSet loadAllAssociatedRiskForIbnrInactiveRisk(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save all data in IBNR Inactive Risk page
     *
     * @param inputRecords
     */
    public void saveAllInactiveRisk(RecordSet inputRecords);

    /**
     * Cancel Active IBNR risk
     *
     * @param inputRecord Record contains input values
     */
    public void processCancelActiveIbnrRisk(Record inputRecord);

}