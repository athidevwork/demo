package dti.pm.riskmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.busobjs.YesNoFlag;
import dti.pm.policymgr.PolicyHeader;

/**
 * An interface that provides DAO operation for risk relation.
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
 * 05/22/2008       fcb         80759: getMultiRiskRelation and loadAvailablePolicyForCompanyInsuredRisk added
 * 05/08/2017       xnie        180317 Added isRiskRelValAvailable().
 * ---------------------------------------------------
 */
public interface RiskRelationDAO {
    /**
     * To load all risk relation data.
     *
     * @param inputRecord   record with enough information to load risk relation.
     * @param loadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risk relations.
     */
    RecordSet loadAllRiskRelation(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To save all newly added or updated Risk Relation data.
     *
     * @param inputRecords a set of Risk Relation Records for saving.
     * @return the number of rows updated.
     */
    int saveAllRiskRelation(RecordSet inputRecords);

    /**
     * To update all Risk Relation data when the officially saved data changed.
     *
     * @param inputRecords a set of Risk Relation Records for updating.
     * @return the number of rows updated.
     */
    int updateAllRiskRelation(RecordSet inputRecords);

    /**
     * To delete all given input Risk Relation records.
     *
     * @param inputRecords a set of Risk Relation Records for deleting.
     * @return the number of rows deleted.
     */
    int deleteAllRiskRelation(RecordSet inputRecords);

    /**
     * To save all NI risk information.
     * 
     * @param inputRecords a set of Risk Records for saving.
     * @return the number of rows saved.
     */
    int saveAllNIRisk(RecordSet inputRecords);

    /**
     * To save all coverage information.
     *
     * @param inputRecords a set of Coverage Records for saving.
     * @return the number of rows saved.
     */
    int saveAllNICoverage(RecordSet inputRecords);

    /**
     * To add NI coverage.
     *
     * @param inputRecord a Record with information of NI coverage.
     * @return a String value with "Y" or "N".
     */
    //String saveNICoverage(Record inputRecord);

    /**
     * To get Non Insured Premium Count.
     *
     * @param inputRecord a Record with information of transaction effective from date, risk type and risk class.
     * @return the count of Non Insured Premium.
     */
    int getNIPremiumCount(Record inputRecord);

    /**
     * To get non-insured's default coverage code.
     *
     * @param inputRecord a Record with needed information.
     * @return a String value of coverage code.
     */
    String getNICoverage(Record inputRecord);

    /**
     * To get Non-Insured FTE count.
     *
     * @param inputRecord a Record with information of policy type.
     * @return the count of Non Insured FTE.
     */
    int getNIFteCount(Record inputRecord);

    /**
     * To get information of if company is insured.
     *
     * @param inputRecord a Record with needed information.
     * @return a String value "Y", "N" or "X".
     */
    String getCompanyInsured(Record inputRecord);

    /**
     * To load all available risks from other policies for adding company insured risk relation.
     *
     * @param inputRecord a Record with information to load the results.
     * @param loadProcessor an instance of data load processor
     * @return a set of Records with loaded available Policy data.
     */
    RecordSet loadAllAvailableRiskForCompanyInsuredRiskRelation(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To get initial values for adding company insured risk relation.
     *
     * @param inputRecord a Record with the needed information.
     * @return a Record with the loaded initial values.
     */
    Record getInitialValuesForAddCompINRiskRelation(Record inputRecord);

    /**
     * To load all available risks for adding policy insured risk relation.
     *
     * @param inputRecord a Record with all needed information to load data.
     * @param loadProcessor an instance of data load processor
     * @return a set of Records with all available risks.
     */
    RecordSet loadAllAvailableRiskForPolicyInsuredRiskRelation(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * To get Add NI Coverage value
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    String getAddNICoverageB(Record inputRecord);

    /**
     * To get Multi Risk Relation Indicator
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    YesNoFlag getMultiRiskRelation(Record inputRecord);

    /**
     * To get available policy for Company Insured Risk
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return string policy id
     */
    String loadAvailablePolicyForCompanyInsuredRisk(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Check if system needs to do risk relation attribute required validation.
     *
     * @param inputRecord a Record with all needed information to do the query
     * @return Y/N value
     */
    YesNoFlag isRiskRelValAvailable(Record inputRecord);

}
