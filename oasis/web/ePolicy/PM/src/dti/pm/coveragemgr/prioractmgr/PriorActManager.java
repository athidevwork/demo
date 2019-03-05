package dti.pm.coveragemgr.prioractmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.coveragemgr.prioractmgr.impl.PriorActRiskRecordLoadProcessor;

/**
 * Interface to handle Implementation of Prior Acts.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 29, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2011       wqfu        103799 - Added copyPriorActsStats.
 * 08/26/2016       wdang       167534 - Added isEditableForRenewalQuote.
 * ---------------------------------------------------
 */
public interface PriorActManager {
    /**
     * Load all prior act risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorActRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Load all prior act coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllPriorActCoverage(PolicyHeader policyHeader, Record inputRecord);



    /**
     * validate prior act coverage date
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActCoverageDate(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate prior act comopnent date
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActComponentDate(PolicyHeader policyHeader, Record inputRecord);

    /**
     * validate prior act coverage count
     *
     * @param policyHeader
     * @param inputRecord
     */
    public void validatePriorActCoverageCount(PolicyHeader policyHeader, Record inputRecord);


    /**
     * validate all prior acts data
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @param compRs
     */
    public void validateAllPriorAct(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs, RecordSet compRs);


    /**
     * get common values for Prior Acts' retreaval
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public Record getCommonValues(PolicyHeader policyHeader, Record inputRecord);


    /**
     * To get initial values for a newly inserted Prior Act Risk record
     *
     * @param policyHeader
     * @param inputRecord
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForPriorActRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To get initial values for a newly inserted Prior Act Coverage record
     *
     * @param policyHeader
     * @param inputRecord
     * @param covgRecords
     * @return Record with initial vlaues
     */
    public Record getInitialValuesForPriorActCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet covgRecords);

    /**
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public void validateForDelete(PolicyHeader policyHeader, Record inputRecord);

    /**
     * save all prior acts risk and coverage
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @return
     */
    public int saveAllPriorActRiskAndCoverage(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs);

    /**
     * save all prior acts data
     *
     * @param policyHeader
     * @param inputRecord
     * @param riskRs
     * @param covgRs
     * @param compRs
     * @return
     */
    public int saveAllPriorAct(PolicyHeader policyHeader, Record inputRecord, RecordSet riskRs, RecordSet covgRs, RecordSet compRs);

    /**
     * get prior coverage count for delete risk page entitlement
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    int getPriorActCoverageCount(PolicyHeader policyHeader, Record inputRecord);

    /**
     * get the minimal retro data of selected risk
     *
     * @param policyHeader
     * @param inputRecord
     * @return the minimal retro data of selected risk
     */
    String getMinRetroDate(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Copy prior acts stats
     *
     * @param inputRecord
     * @return
     */
    public void copyPriorActsStats(Record inputRecord);

    /**
     * Delete pending prior acts
     *
     * @param inputRecord
     * @return
     */
    public void deleteAllPendPriorActs(Record inputRecord);

    /**
     * Get if Prior Acts editable
     * @param policyHeader
     */
    public boolean isEditableForRenewalQuote(PolicyHeader policyHeader);
}
