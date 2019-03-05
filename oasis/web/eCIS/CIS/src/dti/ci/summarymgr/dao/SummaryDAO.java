package dti.ci.summarymgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * User: cyzhao
 * Date: Jun 16, 2008
 */
/*
 * CIS Summary Data Access Object Interface
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface SummaryDAO {
    /**
     * Load Policy CurrentList through entity PK , used by CI Summary page
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadPolicyCurrentListByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

   /**
     * Load Policy CurrentList through entity PK , used to launch popup page from entity list
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadAllPolandQteByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

     /**
     * Load all Entity's Policy and Quote through entity PK including MiniPolicy
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadCombinedPolandQteByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

     /**
     * Load all Entity's Risks through entity PK including MiniPolicy
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadCombinedRiskByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all Entity's Account through entity PK
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadAllAccountsByEntity(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all Account's Billings through billing account PK and policy term history fk
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet performAllBillingsByAccountAndPolicy(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Load all Entity's Claims through entity PK
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    RecordSet loadAllClaimsByEntity(Record record, RecordLoadProcessor recordLoadProcessor);    
}
