package dti.pm.riskmgr.empphysmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for employed physician.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EmployedPhysicianDAO {

    /**
     * load recordset of all Employed Physician infos
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return recordset of all Employed Physician infos
     */
    public RecordSet loadAllEmployedPhysician(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * delete all Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int deleteAllEmployedPhysician(RecordSet inputRecords);

    /**
     * Save all Pending Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int saveAllPendingEmployedPhysician(RecordSet inputRecords);

    /**
     * Save all Active Employed Physician information.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    int saveAllActiveEmployedPhysician(RecordSet inputRecords);


    /**
     * load recordset of all FTE risks for selection
     *
     * @param inputRecord
     * @param selectIndProcessor
     * @return recordset of all FTE Risks
     */
    public RecordSet loadAllFteRisk(Record inputRecord,RecordLoadProcessor selectIndProcessor);



}
