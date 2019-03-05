package dti.pm.riskmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Risk Relation data.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 25, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/07/2008       Joe         Added method loadAllReverseRiskRelation()
 * 05/22/2008       fcb         80759: loadAllAvailableMultiRiskForCompanyInsuredRiskRelation added.
 * 07/06/2010       dzhang      103806: Added getInitialValuesForAddNonBaseCompanyInsured.
 * 05/06/2014       fcb         151632: Added isRefreshRequired.
 * ---------------------------------------------------
 */
public interface RiskRelationManager {

    public static final String MAINTAIN_RISKRELATION_ACTION_CLASS_NAME = "dti.pm.riskmgr.struts.MaintainRiskRelationAction";

    /**
     * To load all risk relation data.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risk relations.
     */
    RecordSet loadAllRiskRelation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To load all reverse risk relation data.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with original selected risk information
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of reverse risk relations.
     */
    RecordSet loadAllReverseRiskRelation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To save/delete/update all risk relation data.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a RecordSet with all need to be processed Risk Relation records
     * @return The number of updated records.
     */
    int saveAllRiskRelation(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * To delete all NI risks. This is because the NI risk relations are saved as risks,
     * so if wants to delete NI risk relation, we need to delete all NI risks.
     *
     * @param inputRecords a RecordSet with all need to be deleted Risk records
     * @return The number of deleted records.
     */
    int deleteAllNIRisk(RecordSet inputRecords);

    /**
     * To get initial values for adding Risk Relation record.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values.
     */
    Record getInitialValuesForAddRiskRelation(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To load all available risks from other policies for adding company insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a Record with entityId
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of all available policies.
     */
    RecordSet loadAllAvailableRiskForCompanyInsuredRiskRelation(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To load all available risks for adding policy insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    RecordSet loadAllAvailableRiskForPolicyInsuredRiskRelation(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * To check if the Non-Insured Premium can be editable or not.
     *
     * @param inputRecord a record loaded with query conditions
     * @return YesNoFlag to indicate field ratingBasis can be editable or not.
     */
    YesNoFlag isNiPremiumEditable(Record inputRecord);

    /**
     * To handle item 1 of Rate Enablement for GDR71.5 Row Level Attribute.
     * That is to check ratingBasis field's availability.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord a record loaded with query conditions
     * @return a record with availability of ratingBasis field
     */
    Record isRatingBasisEditable(PolicyHeader policyHeader, Record inputRecord);

    /**
     * To get product coverage code for NI coverage.
     *
     * @param inputRecord a record loaded with query conditions
     * @return product coverage code
     */
    String getNICoverage(Record inputRecord);

    /**
     * To load all available risks for adding multi risk company insured risk relation.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord record with neccessary input information.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllAvailableMultiRiskForCompanyInsuredRiskRelation(PolicyHeader policyHeader,
                                           Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To get initial values for adding Non-base Company Insured Risk Relation record.
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a record loaded with user entered data
     * @return a Record loaded with initial values.
     */
    public Record getInitialValuesForAddNonBaseCompanyInsured(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get company insured value
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputrecord  record with neccessary input information.
     * @return String value "Y" means that base company insured logic is configured, and "N" means non-base logic is configured.
     */
    public String getCompanyInsuredValue(PolicyHeader policyHeader, Record inputrecord);

    /**
     * Determines whether the parent window might need to be refreshed.
     * @param policyHeader
     * @param inputRecord
     * @return
     */
    public YesNoFlag isRefreshRequired(PolicyHeader policyHeader, Record inputRecord);
}
