package dti.pm.schedulemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;


/**
 * Interface to handle Implementation of Scheudle Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 14, 2007
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

public interface ScheduleManager {
    /**
     * Initial values defaults for a new schedule record
     * @param policyHeader contains policy header information      *
     * @return Record
     */
    Record getInitialValuesForSchedule(PolicyHeader policyHeader);
    /**
     * Load the RiskHerder Bean or CoverageHeader bean of the PolicyHeader object
     * construct a inputrecord and load schedules for risk or coverage
     * <p/>
     *
     * @param  policyHeader policy header that contains all key policy information.
     * @return RecordSet a RecordSet loaded with list of available sechedules for risk/coverage.
     */
    RecordSet loadAllSchedules(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, Record record);

     /**
     * Save all Schedule' information
     *
     * @param policyHeader  policy header
     * @param inputRecords a set of Records, each with the updated schedule info
     * @return the number of rows updated
     */
    int saveAllSchedules(PolicyHeader policyHeader,RecordSet inputRecords);

    /**
     * get syspara,and get value according to the key
     * the format is ^key#value^
     *
     * @param sysPara
     * @param key
     * @return value
     */
    public String getSystemParmKeyValue(String sysPara, String key) ;

     /**
     * Method to Copy all Schedules to target risk
     * @param inputRecords
     */
    public void copyAllSchedule(RecordSet inputRecords);

}


