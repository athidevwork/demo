package dti.pm.componentmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for component information.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/15/2009       yhyang      #89108 Add five methods for Process RM component: loadAllProcessingEvent,
 *                              loadAllProcessingDetail,saveAllProcessingEvent,processEvent,processRmDiscount.
 * 01/20/2009       yhyang      #89109 Add four methods for Porcess Org/Corp Component:loadAllCorpOrgDiscountMember,
 *                              processCorpOrgDiscount,loadAllProcessEventHistory,loadAllProcessDetailHistory.
 * 05/18/2011       dzhang      #117246 Added isAddComponentAllowed and getShortTermCompEffAndExpDates.
 * 06/10/2011       wqfu        103799 - Added loadAllPendPriorActComp.
 * 09/16/2011       ryzhao      122840 - Added isComponentTempRecordExist.
 * 04/26/2012       hxu         132111 - Removed getShortTermCompEffAndExpDates.
 * 04/27/2012       jshen       132111 - roll back the change
 * 10/14/2013       xnie        146082 - Added isOoseChangeDateAllowed() to check if component expiring date can be
 *                                       changed in out of sequence transaction.
 * 01/24/2013       jyang       150639 - Add getCoverageExpirationDate method.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Component
 * 09/16/2014       awu         157552 - Add validateComponentDuplicate.
 * 11/18/2014       fcb         157975 - Added getNddSkipValidateB.
 * 08/28/2018       ryzhao      188891 - Added loadExpHistoryInfo() and loadClaimInfo() for new experience discount
 *                                       history page.
 * ---------------------------------------------------
 */
public interface ComponentDAO {
    /**
     * Retrieves all coverage components
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    RecordSet loadAllComponents(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Retrieves all pending prior act coverage components
     *
     * @param record input record
     * @param recordLoadProcessor an instance of the load processor to set page entitlements
     * @return
     */
    RecordSet loadAllPendPriorActComp(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Save all input component records with the Pm_Nb_End.Save_Covg_Component stored procedure.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    int addAllComponents(RecordSet inputRecords);

    /**
     * Update all given input records with the Pm_Endorse.Change_Covg_Component stored procedure.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    int updateAllComponents(RecordSet inputRecords);

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Covg_Component stored procedure.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Component Detail info matching the fields returned from the loadAllComponents method.
     * @return the number of rows updated.
     */
    int deleteAllComponents(RecordSet inputRecords);

    /**
     * Get the Cancel WIP rule
     *
     * @param record
     * @return
     */
    public Record getCancelWipRule(Record record);

    /**
     * Get the component cycle years
     *
     * @param record
     * @return
     */
    public int getCycleYearsForComponent(Record record);

    /**
     * Get the component num days
     *
     * @param record
     * @return
     */
    public int getNumDaysForComponent(Record record);

    /**
     * To load all dependent components
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllAvailableComponent(Record record, RecordLoadProcessor recordLoadProcessor);

    /**
     * Get the earliest contiguous coverage effective date with the function Pm_Dates.Nb_Covg_Startdt(coverage_fk, check_dt)
     *
     * @param record
     * @return
     */
    public Record getCoverageContiguousEffectiveDate(Record record);

    /**
     * Get component PK and base record FK
     *
     * @param record
     * @return
     */
    public Record getComponentIdAndBaseId(Record record);

    /**
     * Load Cycle Detail
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCycleDetail(Record inputRecord);

    /**
     * Load Surcharge Points
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllSurchargePoint(Record inputRecord);

    /**
     * Save all surcharge points data.
     *
     * @param inputRecords intput record
     * @return the number of row updateds
     */
    public int saveAllSurchargePoint(RecordSet inputRecords);

    /**
     * validate component copy
     * @param inputRecord
     * @return validate status code statusCode
     */
    String validateCopyAllComponent(Record inputRecord);

    /**
     * delete all component from coverage for delete risk all
     * @param compRs
     */
    void deleteAllCopiedComponent(RecordSet compRs);

    /**
     * Load all processing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingEvent(Record inputRecord, RecordLoadProcessor entitlementRLP);

    /**
     * Load all processing detail.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingDetail(Record inputRecord);

    /**
     * Save all processing event.
     *
     * @param inputRecords
     * @return the number of row updated
     */
    public int saveAllProcessingEvent(RecordSet inputRecords);

    /**
     * Set RMT Classification indicator.
     *
     * @param inputRecord
     */
    public void setRMTIndicator(Record inputRecord);

    /**
     * Process RM Discount
     *
     * @param inputRecord
     * @return Record
     */
    public Record processRmDiscount(Record inputRecord);

    /**
     * Load all Corp/Org discount member.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllCorpOrgDiscountMember(Record inputRecord, RecordLoadProcessor entitlementRLP);

    /**
     * Process Corp/Org discount
     *
     * @param inputRecord
     * @return Record
     */
    public Record processCorpOrgDiscount(Record inputRecord);

    /**
     * Load all processing event history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessEventHistory(Record inputRecord);

    /**
     * Load all processing detail history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessDetailHistory(Record inputRecord);

    /**
     * Apply the component
     *
     * @param inputRecord
     * @return Record
     */
    public Record applyMassComponet(Record inputRecord);

    /**
     * Check if it is a problem policy
     *
     * @param inputRecord
     * @return String
     */
    public String isProblemPolicy(Record inputRecord);

    /**
     * Check if add component allowed
     *
     * @param inputRecord
     * @return
     */
    String isAddComponentAllowed(Record inputRecord);

    /**
     * Get short term component's effective from and effective to date
     *
     * @param inputRecord Input record containing risk and coverage level details
     * @return Record that contains component effective from date and effective to date.
     */
    Record getShortTermCompEffAndExpDates(Record inputRecord);

    /**
     * Check if the official component record has a temp record exists for the specific transaction.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if component temp record exists
     *         false if component temp record does not exist
     */
    boolean isComponentTempRecordExist(Record inputRecord);

    /**
     * Check if changing component expiring date in OOSE is allowed
     *
     * @param inputRecord
     * @return
     */
    String isOoseChangeDateAllowed(Record inputRecord);

    /**
     * Load effective to date with PM_Dates.NB_Covg_ExpDt stored procedure.
     * <p/>
     *
     * @param inputRecord a Record with information to load the effective to date.
     * @return Coverage effective to date.
     */
    String getCoverageExpirationDate(Record inputRecord);

    /**
     * Validate component duplicate.
     * @param inputRecord
     * @return
     */
    Record validateComponentDuplicate(Record inputRecord);

    /**
     * Check if the NDD expiration date is configured for the component.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if configured
     *         false if not configured
     */
    boolean getNddSkipValidateB(Record inputRecord);

    /**
     * Load experience discount history information.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadExpHistoryInfo(Record inputRecord);

    /**
     * Load claim information for a specific period of the risk.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadClaimInfo(Record inputRecord);
}
