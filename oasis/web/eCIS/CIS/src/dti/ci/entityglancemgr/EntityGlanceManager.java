package dti.ci.entityglancemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of entityglance Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: September 08, 2011
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface EntityGlanceManager {


    /**
     * Get Entity Demographic 
     *
     * @param record
     * @return
     */
    Record loadEntityDemographic(Record record);

    /**
     * Get Entity Relationships 
     *
     * @param record
     * @return
     */
    RecordSet loadRelationships(Record record);

    /**
     * Get Entity Policy/Quote 
     *
     * @param record
     * @return
     */
    RecordSet loadPolicyQuote(Record record);

    /**
     * Get Policy Transactions 
     *
     * @param record
     * @return
     */
    RecordSet loadTransactions(Record record);

        /**
     * Get Policy Transactions Forms 
     *
     * @param record
     * @return
     */
    RecordSet loadTransactionForms(Record record);

    /**
     * Get Entity Finances 
     *
     * @param record
     * @return
     */
    RecordSet loadFinances(Record record);

    /**
     * Get Entity Finance Invoices 
     *
     * @param record
     * @return
     */
    RecordSet loadInvoices(Record record);

    /**
     * Get Entity Finance Forms 
     *
     * @param record
     * @return
     */
    RecordSet loadFinanceForms(Record record);

    /**
     * Get Entity Claims 
     *
     * @param record
     * @return
     */
    RecordSet loadClaims(Record record);

    /**
     * Get Entity Participants 
     *
     * @param record
     * @return
     */
    RecordSet loadParticipants(Record record);

   
}
