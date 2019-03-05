package dti.pm.riskmgr.empphysmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface to handle CRUD operation on the Employed Physician information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/30/2010       syang       106758 - Add MAINTAIN_EMPOLYED_PHYSICIAN_ACTION_CLASS_NAME to retrieve the default values.
 * ---------------------------------------------------
 */
public interface EmployedPhysicianManager {

    /**
     * load recordset of all Employed Physician infos
     *
     * @param policyHeader
     * @param inputRecord
     * @return recordset of all Employed Physician infos
     */
    RecordSet loadAllEmployedPhysician(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To get initial values for a newly inserted Employed Physician record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForEmployedPhysician(PolicyHeader policyHeader, Record inputRecord);


    /**
     * save all Employed Physician infos
     *
     * @param policyHeader
     * @param inputRecord
     * @param inputRecords
     * @return process count to indicate how many records have been changed
     */
    int saveAllEmployedPhysician(PolicyHeader policyHeader, RecordSet inputRecords, Record inputRecord);

    /**
     * load recordset of all FTE risks for selection
     *
     * @param inputRecord
     * @param policyHeader
     * @param selectIndProcessor
     * @return recordset of all FTE Risks
     */
    public RecordSet loadAllFteRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor selectIndProcessor);

    /**
     * get changed values for changed record
     *
     * @param policyHeader
     * @param empPhysRec
     * @return output record with fte value
     */
    public Record getChangedValuesForEmployedPhysician(PolicyHeader policyHeader, Record empPhysRec);

    /**
     * get changed values for changed record
     *
     * @param policyHeader
     * @param empPhysRec
     * @param inputRecord
     * @return output record with fte value
     */
    public Record getChangedValuesForEmployedPhysician(PolicyHeader policyHeader, Record empPhysRec, Record inputRecord);

    /**
     * calculate total fte for input recordset
     *
     * @param policyHeader
     * @param inputRecords
     * @return output record with total fte value
     */
    public Record calculateTotalFte(PolicyHeader policyHeader, RecordSet inputRecords);

    public static final String MAINTAIN_EMPOLYED_PHYSICIAN_ACTION_CLASS_NAME = "dti.pm.riskmgr.empphysmgr.struts.MaintainEmployedPhysicianAction";
}
