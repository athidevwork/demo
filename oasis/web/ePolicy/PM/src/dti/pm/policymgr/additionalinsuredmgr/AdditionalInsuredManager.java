package dti.pm.policymgr.additionalinsuredmgr;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Additional Insured
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 15, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/17/2010       syang       Issue 111445 - Added getAddInsCoverageData() to retrieve coverage data.
 * 02/27/2013       xnie        Issue 138026 - 1) Added validateAsOfDateForGenerateAddIns() to check if the As of Date
 *                                                is valid.
 *                                             2) Added generateAllAddIns() to call DAO's method generateAllAddIns to
 *                                                generate Additional Insured.
 *                                             3) Modified loadAllAdditionalInsured() to display select checkbox.
 * ---------------------------------------------------
 */
public interface AdditionalInsuredManager {
    /**
     * load all additioanl insured
     * @param policyHeader
     * @param inputRecord
     * @param loadProcessor an instance of data load processor
     * @return recordset of additional insured
     */
    RecordSet loadAllAdditionalInsured(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * save all additional insured data
     * @param policyHeader
     * @param inputRecords
     * @param inputRecord
     */
    void saveAllAdditionalInsured(PolicyHeader policyHeader,RecordSet inputRecords,Record inputRecord);

    /**
     * To get initial values for a newly inserted additional insured record
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForAdditionalInsured(PolicyHeader policyHeader, Record inputRecord);

   /**
     * To get coverage data
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record
     */
    public Record getAddInsCoverageData(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To validate the As of Date
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with the as of date value
     */
    void validateAsOfDateForGenerateAddIns(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Generate all Additional Insured.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a Record with the details of Additional Insured data.
     */
    void generateAllAddIns(PolicyHeader policyHeader, Record inputRecord);
}
