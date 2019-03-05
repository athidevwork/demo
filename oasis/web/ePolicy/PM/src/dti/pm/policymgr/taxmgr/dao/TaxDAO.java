package dti.pm.policymgr.taxmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
/**
 * An interface that provides DAO operation for view tax.
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
 * 10/13/2014       wdang       158112 - Add loadAllTaxHeader(), insertAllTaxHeader(), updateAllTaxHeader(), 
 *                              deleteAllTaxHeader(), getTermAlgorithm(), getManualExpiration() for Maintain Tax page.
 * 01/30/2015       fcb         160508 - added additional validation function for taxes.
 * ---------------------------------------------------
 */
public interface TaxDAO {
    /**
     * Retrieves all tax information.
     *
     * @param record              input record
     * @return recordSet
     */
    RecordSet loadAllTax(Record record);

    /**
     * get latest tax bearing transation of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return long
     */
    public String getLatestTaxTransaction(Record inputRecord);

    /**
     * Retrieve all risk information for Maintain Tax page.
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    RecordSet loadAllRisk(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Retrieve all tax definitions for Maintain Tax page.
     * @param inputRecord
     * @param loadProcessor
     * @return
     */
    RecordSet loadAllTaxHeader(Record inputRecord, RecordLoadProcessor loadProcessor);    
    
    /**
     * Insert all tax definitions for Maintain Tax page.
     *
     * @param inputRecords input records
     */
    void insertAllTaxHeader(RecordSet inputRecords);
    
    /**
     * Update all tax definitions for Maintain Tax page.
     *
     * @param inputRecords input records
     */
    void updateAllTaxHeader(RecordSet inputRecords);
    
    /**
     * Delete all tax definitions for Maintain Tax page.
     *
     * @param inputRecords input records
     */
    void deleteAllTaxHeader(RecordSet inputRecords);
    
    /**
     * Get term algorithm by the given term effective date.
     *
     * @param inputRecord input Record
     * @return algorithm
     */
    String getTermAlgorithm(Record inputRecord);
    
    /**
     * Get the maximum expiration date of manual tax by specific term.
     *
     * @param inputRecord input Record
     * @return expiration date
     */
    String getManualExpiration(Record inputRecord);

    /**
     * Get the maximum expiration date of manual tax by specific term.
     *
     * @param inputRecord input Record
     * @return validation message
     */
    String validateTaxRates(Record inputRecord);
}