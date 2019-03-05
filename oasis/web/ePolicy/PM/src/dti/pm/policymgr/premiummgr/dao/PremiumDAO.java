package dti.pm.policymgr.premiummgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for view premium.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   June 15, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 03/26/2008       yyh         loadAllPayment added for issue 78337
 * 08/01/2011       ryzhao      118806 - Added getLatestTaxTransaction(),
 *                                             getLatestFeeSurchargeTransaction(),
 *                                             getLatestAllTransaction(). 
 * ---------------------------------------------------
 */

public interface PremiumDAO {
    /**
     * Retrieves all premium information.
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor
     * @return recordSet
     */
    RecordSet loadAllPremium(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all rating log infomation
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return recordSet
     */

    RecordSet loadAllRatingLog(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all layer detail infomation
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor
     * @return recordSet
     */

    RecordSet loadAllLayerDetail(Record record,  RecordLoadProcessor recordLoadProcessor);
    /**
     * Retrieves all member contribution infomation
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor to set the page field
     * @return recordSet
     */

    RecordSet loadAllMemberContribution(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all fund information.
     *
     * @param record              input record
     * @param recordLoadProcessor an instance of the load processor
     * @return recordSet
     */
    RecordSet loadAllFund(Record record, RecordLoadProcessor recordLoadProcessor);

     /**
     * Retrieves all payment information.
     *
     * @param record              input record
     * @return recordSet
     */
    RecordSet loadAllPayment(Record record);

    /**
     * get latest premium bearing transation of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return long
     */
    public long getLatestPremiumTransaction(Record inputRecord);

    /**
     * get latest tax bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return long
     */
    public long getLatestTaxTransaction(Record inputRecord);

    /**
     * get latest fee/surcharge bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return long
     */
    public long getLatestFeeSurchargeTransaction(Record inputRecord);

    /**
     * get latest fund/tax/fee/surcharge bearing transaction of policy
     *
     * @param inputRecord Record containing current policyId and term history record information
     * @return long
     */
    public long getLatestAllTransaction(Record inputRecord);
    
    /**
     * judge Rating log Exist or not by policy id
     *
     * @param inputRecord Record containing current policyId
     * @return boolean
     */
    public boolean isRatingLogExist(Record inputRecord);

    /**
     * Retrieves all premium accounting data
     *
     * @param inputRecord input Record
     * @return RecordSet
     */
    public RecordSet loadAllPremiumAccounting(Record inputRecord);

    /**
     * Generate the premium accounting data for selected transaction
     *
     * @param inputRecord input Record
     * @return Record
     */
    public Record generatePremiumAccounting(Record inputRecord);
}
