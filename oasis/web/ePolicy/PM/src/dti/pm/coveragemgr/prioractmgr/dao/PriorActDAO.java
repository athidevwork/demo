package dti.pm.coveragemgr.prioractmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface that provides DAO operation for Prior Act information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        103799 - Added copyPriorActsStats,deleteAllPendPriorActs,
 *                                       loadAllPendPriorActRisk,loadAllPendPriorActCovg.        
 * 01/29/2015       xnie        160614 - Renamed getCoveragePriorExpirationDate to getPriorActsRetroDate.
 * 08/26/2016       wdang       167534 - Added isEditableForRenewalQuote.
 * ---------------------------------------------------
 */
public interface PriorActDAO {

    /**
     * Load all prior act risk
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorActRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all prior act coverage
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPriorActCoverage(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

     /**
     * save all temp prior act risk
     * @param inputRecords
     * @return processed records count
     */
    int saveAllTempPriorActRisk(RecordSet inputRecords);

    /**
     * save all official prior act risk
     * @param inputRecords
     * @return processed records count
     */
    int saveAllOfficialPriorActRisk(RecordSet inputRecords);

    /**
     * save all temp prior act coverage
     * @param inputRecords
     * @return processed records count
     */
    int saveAllTempPriorActCoverage(RecordSet inputRecords);

    /**
     * save all official prior act coverage
     * @param inputRecords
     * @return processed records count
     */
    int saveAllOfficialPriorActCoverage(RecordSet inputRecords);

     /**
     * delete all prior act risk
     * @param inputRecords
     * @return
     */
    int deleteAllPriorActRisk(RecordSet inputRecords);

    /**
     * delete all prior act coverage
     * @param inputRecords
     * @return
     */
    int deleteAllPriorActCoverage(RecordSet inputRecords);

    

    /**
     * return a boolean value to indicate if the prior acts has break
     * @param inputRecord
     * @return boolean result
     */
    boolean isPriorActsBreak(Record inputRecord);

    /**
     * get risk start date
     * @param inputRecord
     * @return
     */
    String getRiskStartDate(Record inputRecord);

    /**
     * get coverage start date
     * @param inputRecord
     * @return
     */
    String getCoverageStartDate(Record inputRecord);

    /**
     * get prior acts coverage retroactive date
     * @param inputRecord
     * @return
     */
    String getPriorActsRetroDate(Record inputRecord);

    /**
     * get product coverage code
     * @param inputRecord
     * @return product coverage code
     */
    String getProductCoverageCode(Record inputRecord);

    /**
     * get active carrier count
     * @param inputRecord
     * @return
     */
    int getActiveCarrierCount(Record inputRecord);


    /**
     * get prior coverage count for delete risk page entitlement
     * @param inputRecord
     * @return prior coverage count
     */
    int getPriorActCoverageCount(Record inputRecord);


    /**
     * get prior risk coverage count for validate prior acts data
     * @param inputRecord
     * @return prior coverage count
     */
    int getPriorActRiskCoverageCount(Record inputRecord);

    /**
     * get the minimal retro data of selected risk
     * @param inputRecord
     * @return the minimal retro data of selected risk
     */
    String getMinRetroDate(Record inputRecord);

    /**
     * Copy prior acts stats
     * @param inputRecord
     * @return
     */
    public void copyPriorActsStats(Record inputRecord);

    /**
     * Delete pending prior acts
     * @param inputRecord
     * @return
     */
    public void deleteAllPendPriorActs(Record inputRecord);

    /**
     * Load all pending prior act risk
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPendPriorActRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all pending prior act coverage
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllPendPriorActCovg(Record inputRecord, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get if Prior Acts is editable.
     * @param inputRecord
     */
    public boolean isEditableForRenewalQuote(Record inputRecord);
}
