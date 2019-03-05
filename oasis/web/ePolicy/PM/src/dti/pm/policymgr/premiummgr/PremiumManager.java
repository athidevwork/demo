package dti.pm.policymgr.premiummgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

import java.sql.Connection;
/**
 * Interface to handle Implementation of Premium Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 15, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/26/2008       yhyang      loadAllPayment added for issue 78337
 * 12/25/2008       yhyang      Add validateTransactionForPremiumWorksheet for issue #88884
 * ---------------------------------------------------
 */
public interface PremiumManager {
    /**
     * Retrieves all premium information
     *
     * @param policyHeader policy header
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadAllPremium(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Retrieves all rating log information
     *
     * @param policyHeader policy header
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadAllRatingLog(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Retrieves all member contribution info
     *
     * @param inputRecord   (transactionId and riskId and termId)
     * @return RecordSet
     */
    RecordSet loadAllMemberContribution (Record inputRecord);

     /**
     * Retrieves all layer detail info
     *
     * @param inputRecord   (transactionId and coverageId and termId)
     * @return RecordSet
     */
    RecordSet loadAllLayerDetail(Record inputRecord);
    
    /**
     * Retrieves all fund information
     *
     * @param policyHeader policy header
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet loadAllFund(PolicyHeader policyHeader, Record inputRecord);

     /**
     * Retrieves all payment information
     *
     * @param policyHeader policy header
     * @return RecordSet
     */
    RecordSet loadAllPayment(PolicyHeader policyHeader);

    /**
     * Validate if the term base id is the current term base id and whether the data is empty.
     *
     * @param inputRecord  inputRecord
     * @param conn         live JDBC Connection
     * @return boolean
     */
    void validateTransactionForPremiumWorksheet(Record inputRecord, Connection conn);

    /**
     * Get the default values for premium accounting date fields
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    RecordSet getInitialValuesForPremiumAccounting(Record inputRecord);

   /**
     * Generate the premium accounting data for selected transaction
     *
     * @param inputRecord input Record
    *  @return RecordSet
     */
    RecordSet generatePremiumAccounting(Record inputRecord);
}
