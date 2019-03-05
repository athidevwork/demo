package dti.pm.componentmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.util.LoadProcessor;
import dti.pm.busobjs.ComponentOwner;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Component Manager.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 18, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/12/2007       sxm         Added ComponentOwner to saveAllDefaultComponent().
 * 01/15/2009       yhyang      #89108 Add five methods for Process RM component: loadAllProcessingEvent,
 *                              loadAllProcessingDetail,saveAllProcessingEvent,processEvent,processRmDiscount.
 * 01/20/2009       yhyang      #89109 Add four methods for Porcess Org/Corp Component:loadAllCorpOrgDiscountMember,
 *                              processCorpOrgDiscount,loadAllProcessEventHistory,loadAllProcessDetailHistory.
 * 09/07/2010       dzhang      #108261 Modified loadAllSourceComponent() to add a new parameter compGridFields
 * 05/18/2011       dzhang      #117246 Added isAddComponentAllowed.
 * 10/28/2011       ryzhao      122840 - Added isComponentTempRecordExist.
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 10/02/2013       fcb         145725 - changed the list of parameters for isProblemPolicy
 * 01/22/2014       jyang       150639 - Move getCoverageExpirationDate from ComponentManager to ComponentManager..
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_Component
 * 11/20/2014       awu         154316 - Added getComponentSequenceId for Policy Change Service using.
 * 08/28/2018       ryzhao      188891 - Added loadExpHistoryInfo() and loadClaimInfo() for new experience discount
 *                                       history page.
 * ---------------------------------------------------
 */
public interface ComponentManager {

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader policy header
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param inputRecord
     * @param ownerRecords owner record set
     * @param loadProcessor
     * @return RecordSet
     */
    RecordSet loadAllComponent(PolicyHeader policyHeader,Record inputRecord, ComponentOwner owner, RecordSet ownerRecords, RecordLoadProcessor loadProcessor);

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader policy header
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param inputRecord
     * @param ownerRecords owner record set
     * @return RecordSet
     */
    RecordSet loadAllComponent(PolicyHeader policyHeader,Record inputRecord, ComponentOwner owner, RecordSet ownerRecords);

    /**
     * Retrieves all source coverage components for risk copy all
     *
     * @param policyHeader policy header
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param inputRecord
     * @param ownerRecords owner record set
     * @param loadProcessor
     * @param compGridFields all the component gird fields
     * @return RecordSet
     */
    RecordSet loadAllSourceComponent(PolicyHeader policyHeader,Record inputRecord, ComponentOwner owner, RecordSet ownerRecords, RecordLoadProcessor loadProcessor, String compGridFields);

    /**
     * Wrapper to invoke the save of all inserted/updated Component records.
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'D' - deleted.
     *
     * @param policyHeader the summary policy information corresponding to the provided coverages.
     * @param inputRecords a set of Records, each with the updated Component Detail info
     *                     matching the fields returned from the loadAllComponents method.
     * @param owner        Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @param ownerRecords a set of owner's Records
     * @return the number of rows updated.
     */
    public int saveAllComponent(PolicyHeader policyHeader,
                                RecordSet inputRecords,
                                ComponentOwner owner,
                                RecordSet ownerRecords);

    /**
     * Get Cancel WIP Rule
     *
     * @param policyHeader
     * @return
     */
    public Record getCancelWipRule(PolicyHeader policyHeader);

    /**
     * Get the EFFECTIVE_DATE according with the Date Setup rule.
     *
     * @return
     */
    public String getEffectiveDateForAddComponent(PolicyHeader policyHeader);

    /**
     * Load all available components
     *
     * @param policyHeader
     * @param record
     * @return
     */
    public RecordSet loadAllAvailableComponent(PolicyHeader policyHeader, Record record);

    /**
     * Load all available components
     *
     * @param policyHeader
     * @param record
     * @param loadProcessor
     * @return
     */
    public RecordSet loadAllAvailableComponent(PolicyHeader policyHeader, Record record, LoadProcessor loadProcessor);

    /**
     * Load all dependent components
     *
     * @param policyHeader
     * @param record
     * @param loadProcessor
     * @return
     */
    public RecordSet loadDependentComponent(PolicyHeader policyHeader, Record record, LoadProcessor loadProcessor);

    /**
     * Get the default values for new added component(s)
     *
     * @param policyHeader
     * @param inputRecord
     * @return
     */

    public Record getInitialValuesForAddComponent(PolicyHeader policyHeader, Record inputRecord);

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
     * @param policyHeader
     * @param inputRecord
     * @return Surcharge points list
     */
    public RecordSet loadAllSurchargePoint(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save all surcharge point information
     *
     * @param inputRecords a set of Records, each with the updated special handling info
     * @return the number of rows updated
     */
    public int saveAllSurchargePoint(RecordSet inputRecords);

    /**
     * Save all default component
     *
     * @param policyHeader
     * @return update count
     */
    public int saveAllDefaultComponent(PolicyHeader policyHeader, Record record, ComponentOwner owner);

    /**
     * Get initial values for OOSE component
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return  a Record loaded with initial values
     */
    Record getInitialValuesForOoseComponent(PolicyHeader policyHeader, Record inputRecord);


    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Covg_Component stored procedure.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords  a recordSet loaded with user entered data
     * @return the number of rows updated.
     */
    int deleteAllComponents(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Validate all components
     *
     * @param policyHeader
     * @param inputRecords
     * @param owner
     * @param ownerRecords
     */
    public void validateAllComponents(PolicyHeader policyHeader,
                                         RecordSet inputRecords,
                                         ComponentOwner owner,
                                         RecordSet ownerRecords);

    /**
     * validate component copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllComponent(Record inputRecord);

    /**
     * delete all copied component
     * @param policyHeader
     * @param inputRecord
     * @param compRs
     */
    public void deleteAllCopiedComponent(PolicyHeader policyHeader, Record inputRecord, RecordSet compRs);

    /**
     * Load all processing event.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllProcessingEvent(Record inputRecord);

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
    public int performProcessingEvent(RecordSet inputRecords);

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
     */
    public void processRmDiscount(Record inputRecord);

    /**
     * Get initial values for the processing event.
     *
     * @return Record
     */
    public Record getInitialValuesForProcessingEvent();

    /**
     * Load all Corp/Org discount member.
     *
     * @param inputRecord
     * @param selIndLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllCorpOrgDiscountMember(Record inputRecord, RecordLoadProcessor selIndLoadProcessor);

    /**
     * Process Corp/Org discount
     *
     * @param inputRecord
     * @param inputRecords
     * @return Record
     */
    public Record processCorpOrgDiscount(Record inputRecord, RecordSet inputRecords);

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
     * Apply the components
     *
     * @param inputRecord
     * @param inputRecords
     * @return Record
     */
    public Record applyMassComponent(Record inputRecord, RecordSet inputRecords);

    /**
     * Get the default values for new added component(s)
     *
     * @param inputRecord
     * @return Record
     */
    public Record getInitialValuesForAddProcessComponent(Record inputRecord);

    /**
     * Check if it is a problem policy
     *
     * @param policyHeader
     * @return boolean
     */
    public boolean isProblemPolicy(PolicyHeader policyHeader);

    /**
     * Check if add component allowed
     *
     * @param inputRecord
     * @return
     */
    boolean isAddComponentAllowed(Record inputRecord);

    /**
     * Check if the official component record has a temp record exists for the specific transaction.
     *
     * @param inputRecord include component base record id and transaction id
     * @return true if component temp record exists
     *         false if component temp record does not exist
     */
    boolean isComponentTempRecordExist(Record inputRecord);    

    /**
     * Retrieves all coverage components
     *
     * @param policyHeader  policy header
     * @param insuredNumberId
     * @param owner         Represents the type of component owner. Can be "Policy", "Coverage" or "Tail" type.
     * @return RecordSet
     */
    public RecordSet loadAllComponentForWs(PolicyHeader policyHeader, String insuredNumberId, ComponentOwner owner);

    /**
     * Load coverage effective to date.
     *
     * @param inputRecord
     * @return
     */
    String getCoverageExpirationDate(Record inputRecord);

    /**
     * Generate the new component id.
     * @return
     */
    String getComponentSequenceId();

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
