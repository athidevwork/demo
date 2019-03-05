package dti.pm.riskmgr.nationalprogrammgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of National Program Manager.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   May 25, 2011
 *
 * @author Dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface NationalProgramManager {

    /**
     * Returns a RecordSet loaded with list of available national programs for the provided risk.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  input records that contains key information
     * @return RecordSet a RecordSet loaded with list of available national programs.
     */
    RecordSet loadAllNationalProgram(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get initial values for add national program
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record that contains key information
     * @return a Record loaded with initial values
     */
    Record getInitialValuesForAddNationalProgram(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all data in National Program page
     *
     * @param policyHeader   policy header that contains all key policy information.
     * @param inputRecordSet a record set with data to be saved
     */
    void saveAllNationalProgram(PolicyHeader policyHeader, RecordSet inputRecordSet);

}