package dti.pm.policymgr.taxmgr;

import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;
/**
 * An interface that provides basic operation for tax manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 21, 2007
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/13/2014       wdang       158112 - Add loadAllTaxHeader(), saveAllTaxHeader(), getTermAlgorithm() for Maintain Tax page.
 * ---------------------------------------------------
 */
public interface TaxManager {
    /**
     * Retrieves all tax information
     *
     * @param policyHeader policy header
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadAllTax(PolicyHeader policyHeader, Record inputRecord);
    
    /**
     * Retrieve all risk information for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecord input record
     * @param loadProcessor
     * @return RecordSet
     */
    RecordSet loadAllRisk(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);
    
    /**
     * Retrieve all tax definitions for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecord input record
     * @param loadProcessor
     * @return RecordSet
     */
    RecordSet loadAllTaxHeader(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);
    
    /**
     * Save/Change/Delete all tax definitions for Maintain Tax page.
     *
     * @param policyHeader policy header
     * @param inputRecords input records
     */
    void saveAllTaxHeader(PolicyHeader policyHeader,RecordSet inputRecords);

    /**
     * Get term algorithm by the given term effective date
     *
     * @param policyHeader policy header
     * @param inputRecord  inputRecord
     * @return TaxCalcAlgorithm
     */
    String getTermAlgorithm(PolicyHeader policyHeader, Record inputRecord);
    
    /**
     * To get initial values for a newly inserted Tax record
     * 
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial values
     */
    Record getInitialValuesForAddTax(PolicyHeader policyHeader, Record inputRecord);
    
    
}