package dti.pm.transactionmgr.impl;

import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 29, 2007
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *10/25/2012        awu         Issue137764 - 1. Added saveContinueTransactionAsOfficial.
 *                                            2. Modified the return type of method saveTransactionAsOfficial.
 *12/06/2013        fcb         Issue 148037 - Changes for performance.
 * ---------------------------------------------------
 */
public interface TransactionSaveProcessor {
    /**
     * Process the common save functionality for all types of save actions.
     *
     * @param inputRecord Record containing policy header summary information about the
     *                    transaction information being saved
     */
    void saveTransaction(Record inputRecord);

    /**
     * Process the save as WIP specific functionality.
     *
     * @param inputRecord Record containing policy header summary information about the
     *                    transaction information being saved
     */
    void saveTransactionAsWip(Record inputRecord);

    /**
     * Process the save as Official specific functionality.
     *
     * @param policyHeader instance of the PolicyHeader for the current policy/transaction
     * @param inputRecord Record containing policy header summary information about the
     *                    transaction information being saved
     */
    void saveTransactionAsOfficial(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Process the save as Endorsment Quote specific functionality.
     *
     * @param policyHeader instance of the PolicyHeader for the current policy/transaction       
     */
    String saveTransactionAsEndorsementQuote(PolicyHeader policyHeader);

}
