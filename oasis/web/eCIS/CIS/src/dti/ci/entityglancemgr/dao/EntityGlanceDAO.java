package dti.ci.entityglancemgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for entityGlance.
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
public interface EntityGlanceDAO {

    /**
     * Get Entity Demographic from DB
     *
     * @param record
     * @return
     */
    Record loadEntityDemographic(Record record);

    /**
     * Get Entity Relationships from DB
     *
     * @param record
     * @return
     */
    RecordSet loadRelationships(Record record);

    /**
     * Get Entity Policy/Quote from DB
     *
     * @param record
     * @return
     */
    RecordSet loadPolicyQuote(Record record);

    /**
     * Get Policy Transactions from DB
     *
     * @param record
     * @return
     */
    RecordSet loadTransactions(Record record);
    /**
     * Get Policy Transactions Forms from DB
     *
     * @param record
     * @return
     */
    RecordSet loadTransactionForms(Record record);
    /**
     * Get Entity Finances from DB
     *
     * @param record
     * @return
     */
    RecordSet loadFinances(Record record);

    /**
     * Get Entity Finance Invoices from DB
     *
     * @param record
     * @return
     */
    RecordSet loadInvoices(Record record);

    /**
     * Get Entity Finance Forms from DB
     *
     * @param record
     * @return
     */
    RecordSet loadFinanceForms(Record record);

    /**
     * Get Entity Claims from DB
     *
     * @param record
     * @return
     */
    RecordSet loadClaims(Record record);

    /**
     * Get Entity Participants from DB
     *
     * @param record
     * @return
     */
    RecordSet loadParticipants(Record record);

}
