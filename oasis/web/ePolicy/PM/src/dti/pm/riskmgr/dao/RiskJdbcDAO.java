package dti.pm.riskmgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.riskmgr.RiskFields;
import dti.pm.riskmgr.RiskHeader;
import dti.pm.riskmgr.RiskRelationFields;
import dti.pm.transactionmgr.TransactionFields;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the RiskDAO interface. This is consumed by any business logic objects
 * that requires information about one or more risks.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         Moved getInitialDddwForRisk() into PMDefaultJdbcDAO
 * 11/18/2008       yhyang      Added loadAllInsuredHistory().
 * 06/05/2009       fcb         93139: getDefaultPracticeState , isRiskEntityTypeValid,
 *                              getRiskIdAndBaseId, getriskId, getLocationAddress, validateIBNR,
 *                              isDateChangeAllowed, isOosRiskValid, getRiskExpDate, getFteFacilityCount,
 *                              getRiskTypeDescription, getRiskBaseId, validateCopyAllRisk,
 *                              validateRiskCopySource: executeReadonly called instead of execute.
 * 06/10/2008       fcb         call new overloaded executeReadonly methods.
 * 06/07/2010       Dzhang      Added loadAllProcedureCode().
 * 08/03/2010       syang       103793 - Added getPrimaryCoverage(), loadAllRiskSurchargePoint() and saveAllRiskSurchargePoint().
 * 08/31/2010       dzhang      108261 - Added getAllFieldForCopyAll() and processCopyAll().
 * 01/19/2011       wfu         113566 - Added copyNewPolicyFromRisk() to handle copying policy from risk.
 * 01/21/2011       syang       105832 - Added getDisciplineDeclineEntityStatus() to retrive ddl status.
 * 06/29/2012       tcheng      133964 - Added loadAllInsuredInfo().
 * 07/17/2012       sxm         Issue 135029 - Added new logic to get Go To Risk List for Coverage class from back end
 *                                             to improve performance
 * 01/08/2013       fcb         137981 - changes related to Pm_Dates modifications.
 * 07/31/2013       hxu         146027 - Added new logic to get Go To Risk List for Coverage form back end to
 *                                       improve performance.
 * 08/06/2013       awu         146878 - Added getChainStatus.
 * 09/06/2013       adeng       147468 - Modified getChainStatus() to map endorsementQuoteId to endqId.
 * 10/24/2013       fcb         145725 - pm_web_risk.get_add_code_and_risk_type replaced with PM_Environment.Get_Risk_Type_Add_Code
 * 12/27/2013       xnie        148083 - 1) Added loadAllPracticeState() to load available practice state list.
 *                                       2) Added getRiskDetailId() to get risk detail id of updated record based on
 *                                          gaven risk id/transaction/term eff/exp date.
 * 04/28/2014       sxm         154227 - Remove call to PM_CopyAll_RiskStat
 * 09/16/2014       awu         157552 - Add validateRiskDuplicate;
 * 10/23/2014       jyang       158446 - Modified loadAllRiskWithCoverage and loadAllRiskWithCoverageClass to pass termPk
 *                                       to backend.
 * 11/10/2014       kxiang      158495 - 1) Modified getDefaultPracticeState() to add a formal parameter.
 *                                       2) Modified getLocationAddress() to change the returned type.
 *                                       3) Removed loadAllPracticeState, as it's not used any more.
 * 12/14/2014       wdang       159491 - Modified loadAllRiskWithCoverage() to add input parameter transactionLogId.
 * 12/24/2014       xnie        156995 - Added loadAllRiskByIds() to load all risks based on gaven ID list.
 * 09/04/2015       tzeng       164679 - 1) Added isAutoRiskRelConfigured() to check if auto risk relation is
 *                                          configured.
 *                                       2) Added processAutoRiskRelation() to process auto risk relation and return
 *                                          result.
 * 01/15/2016       tzeng       166924 - Added isAlternativeRatingMethodEditable.
 * 07/11/2016       lzhang      177681 - Modified 'RecordSet' return type for processCopyAll method instead of 'String'.
 * 08/12/2016       eyin        177410 - Added validateTempCovgExist() and performAutoDeleteTempCovgs().
 * 07/05/2017       wrong       168374 - 1) Modify addAllRisk() to add new mapping case for pcfCountyCode
 *                                          and pcfRiskClassCode.
 *                                       2) Modify updateAllRisk() to add new mapping case for pcfCountyCode and
 *                                          pcfRiskClassCode.
 *                                       3) Modify validateCopyAll() to add new mapping case for riskPcfCountyB and
 *                                          riskPcfSpecialtyB.
 *                                       4) Implements new method loadIsFundStateValue, getDefaultValueForPcfCounty
 *                                          and getDefaultValueForPcfRiskClass.
 * 06/08/2018       xnie        193805 - Added getRiskTypeDefinition() to get risk type description with slot/FTE ID.
 * 07/05/2018       ryzhao      187070 - Added three new methods.
 *                                       1) isGr1CompVisible 2) isGr1CompEditable 3) isGr2CompEditable
 * ---------------------------------------------------
 */
public class RiskJdbcDAO extends BaseDAO implements RiskDAO {

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param policyHeader an instance of the PolicyHeader object with the current policy/term details loaded
     * @param inputRecord  record containting input parameters including an option risk id.
     * @return RiskHeader an instance of the RiskHeader object loaded
     */
    public RiskHeader loadRiskHeader(PolicyHeader policyHeader, Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadRiskHeader", new Object[]{policyHeader, inputRecord});
        }

        RiskHeader riskHeader = null;
        RecordSet rs;

        // Setup the stored procedure
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Risk_Header");
        try {
            inputRecord.setFields(policyHeader.toRecord(), false);
            rs = spDao.execute(inputRecord);

            if (rs.getSize() != 1) {
                throw new AppException("Unable to get risk header for riskId: " + inputRecord.getStringValue("riskId"));
            }

            //Pull the first record to get ref cursor information
            Record outputRecord = rs.getFirstRecord();

            // Map the output to the RiskHeader
            RecordBeanMapper mapper = new RecordBeanMapper();
            riskHeader = new RiskHeader();
            mapper.map(outputRecord, riskHeader);

            // Manually set YesNoFlag
            riskHeader.setDateChangeAllowedB(YesNoFlag.getInstance(outputRecord.getStringValue("dateChangeAllowedB")));

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk header for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadRiskHeader", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadRiskHeader", riskHeader);
        }
        return riskHeader;
    }

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRisk(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRisk", new Object[]{recordLoadProcessor});
        RecordSet rs = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        // Setup an execute Pm_Sel_Risk_Info ref cursor
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Risk_Info", mapping);
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRisk", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of risks based on risk_base_record_id
     * for the provided policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskWithCoverageClass(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskWithCoverageClass", new Object[]{inputRecord});
        RecordSet rs = null;

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recMode", "recordModeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("eQtId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));

        // Setup an execute Pm_Sel_Risk_Info ref cursor
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Risk_SubCovg", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk w/ coverage class for policyNo:"
                              + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllRiskWithCoverageClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskWithCoverageClass", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of risks that have coverage defined or are available to add coverages
     * for the provided policy information.
     * <p/>
     *
     * @param inputRecord         record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllRiskWithCoverage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskWithCoverage", new Object[]{inputRecord});
        RecordSet rs = null;

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recMode", "recordModeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("eQtId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
        mapping.addFieldMapping(new DataRecordFieldMapping(TransactionFields.TRANSACTION_LOG_ID, TransactionFields.LAST_TRANSACTION_ID));

        // Setup an execute Pm_Sel_Risk_Info ref cursor
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Risk_Coverage", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk w/ coverage for policyNo:"
                + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllRiskWithCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskWithCoverage", rs);
        }
        return rs;
    }

    /**
     * load all risk summary
     *
     * @param inputRecord input records that contains key information
     * @param recordLoadProcessor an instance of data load processor
     * @return risk summary
     */
    public RecordSet loadAllRiskSummary(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSummary", new Object[]{inputRecord});
        }
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("lastTranId", "lastTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyTermId", "termBaseRecordId"));

        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Risk_Summary", mapping);
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllRiskSummary", se);
            l.throwing(getClass().getName(), "loadAllRiskSummary", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Returns a RecordSet loaded with list of available risks for the provided
     * policy information and entity name.
     * <p/>
     *
     * @param inputRecord record with policy key fields.
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet findRiskByEntityName(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "findRiskByEntityName", new Object[]{inputRecord});

        RecordSet rs = null;

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "policyTermHistoryId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));

        // Setup an execute Pm_Sel_Risk_Info ref cursor
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Find_Risk_By_Entity_Name ", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "findRiskByEntityName", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "findRiskByEntityName", rs);
        }
        return rs;
    }

    /**
     * Returns a RecordSet loaded with list of existing risks for the provided
     * policy information.
     * <p/>
     *
     * @param inputRecord input record that contains all key policy information.
     * @param processor   to add select check box
     * @return RecordSet a RecordSet loaded with list of available risks.
     */
    public RecordSet loadAllExistingRisk(Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExistingRisk", new Object[]{inputRecord});
        }
        // Setup an execute Pm_Sel_Risk_Info ref cursor
        DataRecordMapping mapping = new DataRecordMapping();
        //riskBaseId, transEff
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("retCanRisks", "sysparam"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_WEB_RISK.Sel_Existing_Risk", mapping);
        RecordSet rs = null;
        try {
            rs = spDao.execute(inputRecord, processor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk information for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllExistingRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExistingRisk", rs);
        }
        return rs;
    }

    /**
     * Returns a Record with the additional info 1,2,3 fields for the particular risk and term dates.
     * <p/>
     *
     * @param inputRecord record with riskId and term dates.
     * @return Record a Record with the Addl Info fields if configured via system parameters.
     */
    public Record loadRiskAddlInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadRiskAddlInfo", new Object[]{inputRecord});

        try {
            Record output = new Record();

            // Set some initial values
            inputRecord.setFieldValue("inputLevel", "RISK");

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("level", "inputLevel"));
            mapping.addFieldMapping(new DataRecordFieldMapping("value", "riskId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("fromDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("toDate", "termEffectiveToDate"));

            // Execute the stored procedure for additional info fields
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Addl_Info", mapping);
            output = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadRiskAddlInfo", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select risk additional information for: " + inputRecord.getStringValue("riskId"), e);
            l.throwing(getClass().getName(), "loadRiskAddlInfo", ae);
            throw ae;
        }
    }

    /**
     * Save all given input records with the Pm_Nb_End.Save_Risk stored procedure,
     * assuming they all have recordModeCode = TEMP, were added during this WIP transaction
     * (ie. have not been saved as Official yet).
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to NEW for records that are newly added in this request.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    public int addAllRisk(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "addAllRisk", new Object[]{inputRecords});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "RISKBASERECORDID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskStatus", "RISKSTATUS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "RISKEFFECTIVEFROMDATE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "RISKEFFECTIVETODATE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countyCode", "RISKCOUNTY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskClassCode", "RISKCLASS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expBasisCode", "EXPOSUREBASIS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expUnit", "EXPOSUREUNIT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("offRecId", "OFFICIALRECORDID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sqFootage", "SQUAREFOOTAGE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfEmplDoc", "NUMBEREMPLOYEDDOCTOR"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfVap", "NUMBERVAP"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfBed", "NUMBERBED"));
        mapping.addFieldMapping(new DataRecordFieldMapping("avgDailyCen", "AVERAGEDAILYCENSUS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annOutpatVst", "ANNUALOUTPATIENTVISIT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfQbDel", "NUMBERQBDELIVERY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("propId", "location"));
        mapping.addFieldMapping(new DataRecordFieldMapping("afterImageB", "AFTERIMAGERECORDB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskProcCode", "RISKPROCESSCODE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyCycle", "policyCycleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfExtBed", "NUMBEREXTBED"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfSkillBed", "NUMBERSKILLBED"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfInpatientSurg", "NUMBERINPATIENTSURG"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfOutpatientSurg", "NUMBEROUTPATIENTSURG"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfErVisit", "NUMBERERVISIT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskSociety", "RISKSOCIETYID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covPartBaseId", "COVERAGEPARTBASERECORDID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fte", "FTEEQUIVALENT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fteFull", "FTEFULLTIMEHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ftePart", "FTEPARTTIMEHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fteDiem", "FTEPERDIEMHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("frameTypeCode", "FRAMETYPE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("protectionClassCode", "PROTECTIONCLASS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("buildingClassCode", "BUILDINGCLASS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("alternateSpecialityCode", "ALTERNATESPECIALTYCODE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cityCode", "CITY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("location", "premiseLocation"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfCountyCode", "PCFRISKCOUNTY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfRiskClassCode", "PCFRISKCLASS"));

        // Insert the records in batch mode with 'Pm_Nb_End.Save_Risk'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Risk", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save inserted risks.", e);
            l.throwing(getClass().getName(), "addAllRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addAllRisk", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Update all given input records with the Pm_Endorse.Change_Risk stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    public int updateAllRisk(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "updateAllRisk", new Object[]{inputRecords});

        int updateCount = inputRecords.getSize();

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("baseRiskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "riskEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("countyCode", "riskCounty"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskClassCode", "riskClass"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expBasisCode", "exposureBasis"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expUnit", "exposureUnit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sqFootage", "squareFootage"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfEmplDoc", "numberEmployedDoctor"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfVap", "numberVap"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfBed", "numberBed"));
        mapping.addFieldMapping(new DataRecordFieldMapping("avgDailyCen", "AVERAGEDAILYCENSUS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("annOutpatVst", "ANNUALOUTPATIENTVISIT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfQbDel", "NUMBERQBDELIVERY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("propId", "location"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskProcCode", "RISKPROCESSCODE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfExtBed", "NUMBEREXTBED"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfSkillBed", "NUMBERSKILLBED"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfInpatientSurg", "NUMBERINPATIENTSURG"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfOutpatientSurg", "NUMBEROUTPATIENTSURG"));
        mapping.addFieldMapping(new DataRecordFieldMapping("noOfErVisit", "NUMBERERVISIT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskSociety", "RISKSOCIETYID"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fte", "FTEEQUIVALENT"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fteFull", "FTEFULLTIMEHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ftePart", "FTEPARTTIMEHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fteDiem", "FTEPERDIEMHRS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("frameTypeCode", "FRAMETYPE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("protectionClassCode", "PROTECTIONCLASS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("buildingClassCode", "BUILDINGCLASS"));
        mapping.addFieldMapping(new DataRecordFieldMapping("alternateSpecialityCode", "ALTERNATESPECIALTYCODE"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cityCode", "CITY"));
        mapping.addFieldMapping(new DataRecordFieldMapping("location", "premiseLocation"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfCountyCode", "pcfRiskCounty"));
        mapping.addFieldMapping(new DataRecordFieldMapping("pcfRiskClassCode", "pcfRiskClass"));

        // Endorse the records in batch mode with 'Pm_Endorse.Change_Risk'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Risk", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to endorse changed risks.", e);
            l.throwing(getClass().getName(), "updateAllRisk", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updateAllRisk", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Delete all given input records with the Pm_Nb_Del.Del_Risk stored procedure,
     * assuming they all have recordModeCode = TEMP, and were marked for delete.
     *
     * @param inputRecords a set of Records, each with the PolicyHeader, PolicyIdentifier,
     *                     and Risk Detail info matching the fields returned from the loadAllRisk method..
     * @return the number of rows updated.
     */
    public int deleteAllRisk(RecordSet inputRecords) {
        Logger l = LogUtils.enterLog(getClass(), "deleteAllRisk", new Object[]{inputRecords});

        int updateCount = inputRecords.getSize();

        // Delete the records in batch mode with 'Pm_Nb_Del.Del_Coverage'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Risk");
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete risks.", e);
            l.throwing(getClass().getName(), "deleteAllRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "deleteAllRisk", new Integer(updateCount));
        }

        return updateCount;
    }

    /**
     * Validate Risk Duplication
     * @param inputRecord
     * @return
     */
    public Record validateRiskDuplicate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateRiskDuplicate", new Object[]{inputRecord});

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Data.New_Risk_Duplicate");
        RecordSet rs = null;
        Record outputRec = null;
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to validate risk duplication.", e);
            l.throwing(getClass().getName(), "validateRiskDuplicate", ae);
            throw ae;
        }
        if (rs != null) {
            outputRec = rs.getSummaryRecord();
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskDuplicate", outputRec);
        }
        return outputRec;
    }

    /**
     * Returns a RecordSet loaded with list of available risk types for the provided
     * policy information.
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of available risk types.
     */
    public RecordSet loadAllRiskType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskType", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveFrom", "effectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effectiveTo", "effectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));

        inputRecord.setFieldValue("covgPartCode", "NONE");

        // Execute query
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Risk_Code_Shrt_Desc", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risk types for policyNo:" + PolicyHeaderFields.getPolicyNo(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllRiskType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskType", rs);
        }
        return rs;
    }

    /**
     * Returns a list of add codes for all risk types.
     *
     * @return RecordSet a RecordSet loaded with list of add codes for all risk types.
     */
    public RecordSet loadAllRiskTypeAddCode() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskTypeAddCode");
        }

        // Execute query
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Environment.Get_Risk_Type_Add_Code");
        try {
            rs = spDao.execute(new Record());
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load add code and risk types", e);
            l.throwing(getClass().getName(), "loadAllRiskTypeAddCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskTypeAddCode", rs);
        }
        return rs;
    }

    /**
     * Get "add code" for a given risk type.
     *
     * @param inputRecord Record contains input values
     * @return String a String contains an add code.
     */
    public String getAddCodeForRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddCodeForRisk", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Get_Add_Code_By_Risk_Type");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get add code. ", e);
            l.throwing(getClass().getName(), "getAddCodeForRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddCodeForRisk", returnValue);
        }
        return returnValue;
    }

    /**
     * Get the default risk type code for a newly created policy.
     *
     * @param inputRecord Record contains input values
     * @return String a String containing the riskTypeCode
     */
    public String getAddDefaultRiskTypeCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAddDefaultRiskTypeCode", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "policyHolderNameEntityId"));

        // Execute the stored proc
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Add_Default_Risk_Type_Code", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get add default risk type code. ", e);
            l.throwing(getClass().getName(), "getAddDefaultRiskTypeCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAddDefaultRiskTypeCode", returnValue);
        }
        return returnValue;
    }

    /**
     * check if the entity type of risk matches the required entity type of risk type.
     *
     * @param inputRecord Record contains Policy type code, Risk type code, and Risk entity ID.
     * @return String a String contains an add code.
     */
    public String isRiskEntityTypeValid(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRiskEntityTypeValid", new Object[]{inputRecord});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskEntityId", "entityId"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Check_Risk_Entity_Type", mapping);
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check entity type", e);
            l.throwing(getClass().getName(), "isRiskEntityTypeValid", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isRiskEntityTypeValid", returnValue);
        }
        return returnValue;
    }

    /**
     * Get risk Id and base ID.
     *
     * @param policyId     Policy ID.
     * @param riskTypeCode Risk type code.
     * @param entityId     Risk entity ID.
     * @param location     Location ID.
     * @param slotId       Slot ID.
     * @return Record that contains risk Id and base ID.
     */
    public Record getRiskIdAndBaseId(String policyId, String riskTypeCode,
                                     String entityId, String location, String slotId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskIdAndBaseId",
                new Object[]{policyId, riskTypeCode, entityId, location, slotId});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyId);
        inputRecord.setFieldValue("riskTypeCode", riskTypeCode);
        inputRecord.setFieldValue("entityId", entityId);
        inputRecord.setFieldValue("location", location);
        inputRecord.setFieldValue("slotId", slotId);

        // get the return value
        Record returnRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Get_Risk_PK_And_Base_FK");
        try {
            returnRecord = spDao.executeReadonly(inputRecord, true).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk ID and base ID", e);
            l.throwing(getClass().getName(), "getRiskIdAndBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskIdAndBaseId", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Get slot Id for a given risk type and policy ID
     *
     * @param policyId     Policy ID.
     * @param riskTypeCode Risk type code.
     * @return String a String contains a address.
     */
    public String getRiskId(String policyId, String riskTypeCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskId", new Object[]{policyId, riskTypeCode});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("policyId", policyId);
        inputRecord.setFieldValue("riskType", riskTypeCode);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Get_Risk_Id");
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get ID for risk type "
                + riskTypeCode, e);
            l.throwing(getClass().getName(), "getRiskId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskId", returnValue);
        }
        return returnValue;
    }

    /**
     * Get address for a given location ID
     *
     * @param location Location ID.
     * @return String a String contains a address.
     */
    public RecordSet getLocationAddress(String location) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLocationAddress", new Object[]{location});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("location", location);

        // get the return value
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.get_location_address");
        try {
            // This method was reverted to use back "execute" rather than "executeReadonly". The reason is that in case this
            // DAO call is done from Web Services we need to ensure that any location that was created during the Web Service
            // call can be seen by this call. If "executeReadonly" was used, then a separate connection was involved, and
            // the information that was just created by the Web Service call would not be visible to another connection.
            // If this creates in the future an issue with too many cursors open (which should not be the case), then
            // a solution where different calls are made based on whether the call is from Web Services or from ePolicy
            // can be implemented.
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get address for location "
                + location, e);
            l.throwing(getClass().getName(), "getLocationAddress", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLocationAddress", rs);
        }
        return rs;
    }

    /**
     * Get default practice state code.
     *
     * @param issueStateCode         Issue state code
     * @param regionalOffice         Regional office code.
     * @param entityId               Risk entity ID.
     * @param transEffectiveFromDate Transaction effective from date.
     * @param getPmDefaultB          indicator for if call Pm Default.
     * @return String a String contains a default practice state code.
     */
    public String getDefaultPracticeState(String issueStateCode, String regionalOffice, String entityId,
                                          String transEffectiveFromDate, String getPmDefaultB) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultPracticeState", new Object[]{issueStateCode,
                regionalOffice, entityId, transEffectiveFromDate});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("issueStateCode", issueStateCode);
        inputRecord.setFieldValue("regionalOffice", regionalOffice);
        inputRecord.setFieldValue("entityId", entityId);
        inputRecord.setFieldValue("transEffectiveFromDate", transEffectiveFromDate);
        inputRecord.setFieldValue("getPmDefaultB", getPmDefaultB);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.get_default_practice_state");
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get default practice state for entity "
                + entityId, e);
            l.throwing(getClass().getName(), "getDefaultPracticeState", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultPracticeState", returnValue);
        }
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with list of locations for the given entity ID
     *
     * @param inputRecord Record contains input values
     * @return RecordSet a RecordSet loaded with list of locations.
     */
    public RecordSet loadAllLocation(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllLocation", new Object[]{inputRecord, recordLoadProcessor});
        }

        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_web_risk.get_location");
        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load locations.", e);
            l.throwing(getClass().getName(), "loadAllLocation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllLocation", rs);
        }
        return rs;
    }

    /**
     * Validate IBNR Indicator
     *
     * @param inputRecord
     * @return String
     */
    public String validateIBNR(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateIBNR", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));
        String res;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_change_rolling_ibnr_b", mapping);

        try {
            res = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate IBNR Indicator Date", e);
            l.throwing(getClass().getName(), "validateIBNR", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateIBNR", res);
        }
        return res;
    }

    /**
     * check if the effective to date of a risk type can be changed.
     *
     * @param inputRecord
     * @return String a String contains an add code.
     */
    public String isDateChangeAllowed(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDateChangeAllowed", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.is_Date_Change_Allowed");
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get date change indicator", e);
            l.throwing(getClass().getName(), "isDateChangeAllowed", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDateChangeAllowed", returnValue);
        }
        return returnValue;
    }

    /**
     * Check if Change option is available or not by function Pm_Valid_Oos_Risk
     *
     * @param inputRecord
     * @return
     */
    public String isOosRiskValid(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOosRiskValid", new Object[]{inputRecord});
        }

        String isValid;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskEff", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Valid_Oos_Risk", mapping);
        try {
            isValid = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check oos risk is valid or not", e);
            l.throwing(getClass().getName(), "isOosRiskValid", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOosRiskValid", isValid);
        }
        return isValid;
    }

    /**
     * Change the effective to date on all related tables.
     *
     * @param inputRecord
     */
    public void changeTermForEndorseDates(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeTermForEndorseDates", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newRiskEff", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("newEffTo", "riskEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("oldRiskEff", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("oldRiskExp", "origRiskEffectiveToDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse_Dates.Change_Term", mapping);
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Unable to change effective to date on all related tables", e);
            l.throwing(getClass().getName(), "changeTermForEndorseDates", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changeTermForEndorseDates");
        }
    }

    /**
     * To get risk expiration date if risk is a date change allowed risk.
     *
     * @param inputRecord
     * @return
     */
    public String getRiskExpDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskExpDate", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("findDate", "riskEffectiveFromDate"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Dates.NB_Risk_ExpDt", mapping);
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable get risk expiration date", e);
            l.throwing(getClass().getName(), "getRiskExpDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskExpDate", returnValue);
        }
        return returnValue;
    }

    /**
     * get Fte facililty count for opion availability
     *
     * @param inputRecord intput record
     * @return the number of facility count
     */
    public int getFteFacilityCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFteFacilityCount", new Object[]{inputRecord});
        }
        int result = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Fte_Relation.Get_FTE_Facility_Count");
        try {
            result = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get FTE Facility count", e);
            l.throwing(getClass().getName(), "getFteFacilityCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFteFacilityCount", String.valueOf(result));
        }
        return result;
    }

    /**
     * To get risk type description
     *
     * @param riskBaseId
     * @return
     */
    public String getRiskTypeDescription(String riskBaseId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskTypeDescription", new Object[]{riskBaseId});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("riskBaseId", riskBaseId);

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_RISK_DEF");
        try {
            returnValue = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk type description "
                + riskBaseId, e);
            l.throwing(getClass().getName(), "getRiskTypeDescription", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskTypeDescription", returnValue);
        }
        return returnValue;
    }

    /**
     * To get risk type description with slot/FTE ID if applicable
     *
     * @param riskBaseId
     * @return the risk type description with id
     */
    public String getRiskTypeDefinition(String riskBaseId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskTypeDefinition", new Object[]{riskBaseId});
        }

        // Create the input data mapping
        Record inputRecord = new Record();
        inputRecord.setFieldValue("riskBase", riskBaseId);

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Risk_Def");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk type definition. ", e);
            l.throwing(getClass().getName(), "getRiskTypeDefinition", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskTypeDefinition", new Integer(returnValue));
        }
        return returnValue;
    }

    /**
     * To get risk base id
     *
     * @param record
     * @return
     */
    public String getRiskBaseId(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskBaseId", new Object[]{record});
        }

        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polId", "policyId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entId", "entityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "riskTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("propId", "propertyRiskId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("covPart", "coveragePartBaseRecordId"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Base_Fk.Get_Risk_Base_Fk", mapping);
        try {
            returnValue = spDao.executeReadonly(record, true).getSummaryRecord().getStringValue("baseId");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk base id ", e);
            l.throwing(getClass().getName(), "getRiskBaseId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskBaseId", returnValue);
        }
        return returnValue;
    }


    /**
     * validate risk copy
     *
     * @param inputRecord
     * @return validate status code statusCode
     */
    public String validateCopyAllRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCopyAllRisk", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "riskEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "riskEffectiveToDate"));        
        mapping.addFieldMapping(new DataRecordFieldMapping("fromRiskId", "riskId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskCountyB", "countyB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskStateB", "stateB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("ibnrStatus", "ibnrStatusB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("char4", "riskChar4B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("num4", "riskNum4B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("date4", "riskDate4B"));
        mapping.addFieldMapping(new DataRecordFieldMapping("specialtyB", "specialityB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("altSpecialty", "altSpecialtyB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("network", "networkB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskPcfCountyB", "pcfCountyB"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskPcfSpecialtyB", "pcfSpecialtyB"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Val_Cpy.Val_Risk", mapping);
        try {
            statusCode = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate copy all risk", e);
            l.throwing(getClass().getName(), "validateCopyAllRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateCopyAllRisk", statusCode);
        }


        return statusCode;
    }

    /**
     * load all target risk for copy all risk
     *
     * @param inputRecord
     * @param processor
     * @return recordset of all target risks
     */
    public RecordSet loadAllTargetRisk(Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllTargetRisk", new Object[]{inputRecord});
        }
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transEffDate", "transEffectiveFromDate"));
        
        //set constant values
        inputRecord.setFieldValue("recordModeCode", "TEMP");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_All_Risks", mapping);
        RecordSet rs = null;
        try {
            rs = spDao.execute(inputRecord, processor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load target risk information for copy all risk", e);
            l.throwing(getClass().getName(), "loadAllTargetRisk", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllTargetRisk", rs);
        }
        return rs;
    }

    

    /**
     * validate risk
     *
     * @param inputRecord
     * @return validate result
     */
    public String validateRiskCopySource(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateRiskCopySource", new Object[]{inputRecord});
        }

        String statusCode = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskBaseId", "riskBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        //set constant values
        inputRecord.setFieldValue("policyCycle", "POLICY");

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Required.Val_Risk", mapping);
        try {
            statusCode = spDao.executeReadonly(inputRecord, true).getSummaryRecord().getStringValue("statusCode");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate risk", e);
            l.throwing(getClass().getName(), "validateRiskCopySource", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateRiskCopySource", statusCode);
        }


        return statusCode;
    }


    /**
     * get to risk coverage
     *
     * @param inputRecord
     * @return coverage info and validate status
     */
    public Record getToRiskCoverage(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getToRiskCoverage", new Object[]{inputRecord});
        }

        Record outRec = null;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "transactionLogId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEffective", "transEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpiration", "termExp"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromCoverageId", "parentCoverageId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toRiskId", "toRiskBaseRecordId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_To_Risk_Covg", mapping);
        try {
            outRec = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get to risk coverage", e);
            l.throwing(getClass().getName(), "getToRiskCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getToRiskCoverage", outRec);
        }
        return outRec;

    }

    /**
     * get copy to coverage count for validation
     *
     * @return copy to coverage count
     */
    public int getToCoverageCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getToCoverageCount", new Object[]{inputRecord});
        }

        int count = 0;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("toCovgBaseId", "toCoverageBaseRecordId"));
        
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Get_To_Coverage_Count", mapping);
        try {
            count = spDao.execute(inputRecord).getSummaryRecord().
                getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get copy to coverage count", e);
            l.throwing(getClass().getName(), "getToCoverageCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getToCoverageCount", String.valueOf(count));
        }
        return count;
    }


    /**
     * get from risk type risk class count
     *
     * @return from risk type risk class count
     */
    public int getFromRiskClassCount(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFromRiskClassCount", new Object[]{inputRecord});
        }
        int count = 0;
        
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Get_From_Risk_Class_Count");
        try {
            count = spDao.execute(inputRecord).getSummaryRecord().
                getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD).intValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get get from risk class count", e);
            l.throwing(getClass().getName(), "getFromRiskClassCount", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFromRiskClassCount", String.valueOf(count));
        }
        return count;
    }

    /**
     * To load all source risk's addresses and phone numbers
     *
     * @param inputRecord
     * @param processor
     * @return
     */
    public RecordSet loadAllAddressPhone(Record inputRecord, RecordLoadProcessor processor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAddressPhone", new Object[]{inputRecord});
        }
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("argClientId", "entityId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Risk_Address_Phone", mapping);
        RecordSet rs = null;
        try {
            rs = spDao.execute(inputRecord, processor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all addresses and phone numbers for source risk", e);
            l.throwing(getClass().getName(), "loadAllAddressPhone", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAddressPhone", rs);
        }
        return rs;
    }

    /**
     * To copy all source risk's addresses and phone numbers to copy-to list risks
     *
     * @param inputRecord
     * @return
     */
    public Record copyAllAddressPhone(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyAllAddressPhone", new Object[]{inputRecord});
        }

        Record outputRecord;
        // Create the input data mapping
        DataRecordMapping mapping = new DataRecordMapping();

        mapping.addFieldMapping(new DataRecordFieldMapping("effDt", "changeEffectiveDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromFks", "addressPhoneIds"));
        mapping.addFieldMapping(new DataRecordFieldMapping("toEntityFks", "riskEntityIds"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Copy_Address_Phone", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to copy all addresses and phone numbers", e);
            l.throwing(getClass().getName(), "copyAllAddressPhone", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyAllAddressPhone", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Returns a RecordSet loaded with list of insured history.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredHistory", new Object[]{inputRecord});
        }
        // Setup the stored procedure
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Insured_History");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all insured history.", e);
            l.throwing(getClass().getName(), "loadAllInsuredHistory", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllInsuredHistory", rs);
        }
        return rs;
    }

    /**
     * Get risk type code by riskBaseRecordId
     *
     * @param inputRecord
     * @return String
     */
    public String getRiskTypeCode(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskTypeCode", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Get_Risk_Type");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk type code. ", e);
            l.throwing(getClass().getName(), "getRiskTypeCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskTypeCode", returnValue);
        }
        return returnValue;
    }

    /**
     * Get risk generic type
     *
     * @param inputRecord
     * @return String
     */
    public String getGenericRiskType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGenericRiskType", new Object[]{inputRecord});
        }

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Generic_Risk_Type");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk generic type. ", e);
            l.throwing(getClass().getName(), "getGenericRiskType", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGenericRiskType", returnValue);
        }
        return returnValue;
    }

    /**
     * Get entity owner id for location.
     *
     * @param inputRecord
     * @return int
     */
    public long getEntityOwnerId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityOwnerId", new Object[]{inputRecord});
        }

        // Get the return value.
        long returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Entity_Owner_Fk");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getLongValue(spDao.RETURN_VALUE_FIELD).longValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get entity owner id. ", e);
            l.throwing(getClass().getName(), "getEntityOwnerId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGenericRiskType", new Long(returnValue));
        }
        return returnValue;
    }

    /**
     * Validate If Any Temp Coverage exists under the Risk.
     *
     * @param inputRecord
     * @return Record
     */
    public Record validateTempCovgExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTempCovgExist", new Object[]{inputRecord});
        }

        // Get the return value.
        Record outputRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Val_Temp_Covg_Exist");
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check If Temp Coverage Exist.", e);
            l.throwing(getClass().getName(), "validateTempCovgExist", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTempCovgExist", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Delete temp coverages automatically after issue state was changed.
     *
     * @param inputRecord
     */
    public void performAutoDeleteTempCovgs(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAutoDeleteTempCovgs", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Auto_Del_Temp_Covg");
        try {
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to delete temp coverages.", e);
            l.throwing(getClass().getName(), "performAutoDeleteTempCovgs", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performAutoDeleteTempCovgs");
        }
    }

    /**
     * Returns a RecordSet loaded with list of procedure code.
     * <p/>
     *
     * @param inputRecord
     * @param recordLoadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllProcedureCode(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllProcedureCode", new Object[]{inputRecord,recordLoadProcessor});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "practiceStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("riskType", "riskTypeCode"));
        // Setup the stored procedure
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Procedure_Code", mapping);
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all procedure code.", e);
            l.throwing(getClass().getName(), "loadAllProcedureCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllProcedureCode", rs);
        }
        return rs;
    }

    /**
     * Returns the primary coverage id of current risk.
     *
     * @param inputRecord
     * @return String
     */
    public String getPrimaryCoverageId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryCoverageId", new Object[]{inputRecord});
        }

        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Primary_Coverage");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get primary coverage id. ", e);
            l.throwing(getClass().getName(), "getPrimaryCoverage", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrimaryCoverageId", returnValue);
        }
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with list of risk surcharge point.
     *
     * @param inputRecord
     * @param entitlementRLP
     * @return RecordSet
     */
    public RecordSet loadAllRiskSurchargePoint(Record inputRecord, RecordLoadProcessor entitlementRLP) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRiskSurchargePoint", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Risk_Surcharge_Points");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord, entitlementRLP);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all risk surcharge point.", e);
            l.throwing(getClass().getName(), "loadAllRiskSurchargePoint", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskSurchargePoint", rs);
        }
        return rs;
    }

    /**
     * Save all risk surcharge point.
     *
     * @param inputRecords
     * @return the number of rows updated.
     */
    public int saveAllRiskSurchargePoint(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllRiskSurchargePoint", new Object[]{inputRecords});
        }
        int updateCount = 0;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("compPointOverrideId", "pmComponentPointOverrideId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Save_Risk_Surcharge_Points", mapping);
        try {
            updateCount = spDao.executeBatch(inputRecords);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save all risk surcharge point.", e);
            l.throwing(getClass().getName(), "saveAllRiskSurchargePoint", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "saveAllRiskSurchargePoint", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Get all copy all configured fields.
     *
     * @return the RecordSet contains config fields.
     */
    public RecordSet getAllFieldForCopyAll() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAllFieldForCopyAll");
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Copy_All.Sel_Copy_All_Fields_By_Level");
        RecordSet rs = null;
        try {
            rs = sp.execute(new Record());
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get all field for copy all.", e);
            l.throwing(getClass().getName(), "getAllFieldForCopyAll", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllFieldForCopyAll", rs);
        }
        return rs;
    }

    /**
     * process copy all.
     * <p/>
     *
     * @param inputRecord
     */
    public RecordSet processCopyAll(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCopyAll");
        }

        RecordSet rs;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Process_Copyall.Pm_Copyall_Stats");
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to process copy all.", e);
            l.throwing(getClass().getName(), "processCopyAll", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCopyAll");
        }

        return rs;
    }

    /**
     * Method that copy policy to risk based on input Record.
     * <p/>
     *
     * @param inputRecord  Record that contains new policy information
     * @return RecordSet
     */
    public RecordSet copyNewPolicyFromRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyNewPolicyFromRisk", new Object[]{inputRecord});
        }

        RecordSet rs = null;
        // Create Data Record Mapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("newPolType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDate", "accountingDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("comment", "transactionComment"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompany", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "regionalOffice"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cycle.Web_Pm_Create_Next_Cycle", mapping);
        try {
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to copy new policy from risk.", e);
            l.throwing(getClass().getName(), "copyNewPolicyFromRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyNewPolicyFromRisk", rs);
        }
        return rs;
    }

    /**
     * Get the status of discipline decline entity.
     * <p/>
     *
     * @param inputRecord Record that contains entity id.
     * @return String
     */
    public String getDisciplineDeclineEntityStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDisciplineDeclineEntityStatus", new Object[]{inputRecord});
        }
        String ddlStatus;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CS_WEB_DISCIPLINE_DECLINE.Get_Entity_Status");
        try {
            ddlStatus = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get ddl status.", e);
            l.throwing(getClass().getName(), "getDisciplineDeclineEntityStatus", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDisciplineDeclineEntityStatus", ddlStatus);
        }
        return ddlStatus;
    }

    /**
     * To validate reinstate ibnr risk
     *
     * @param inputRecord
     * @return
     */
    public String valReinstateIbnrRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "valReinstateIbnrRisk", new Object[]{inputRecord});
        }

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Ibnr_Risk.Val_Reinstate_Ibnr_Risk");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD, "N").trim();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to validate reinstate ibnr risk. ", e);
            l.throwing(getClass().getName(), "valReinstateIbnrRisk", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "valReinstateIbnrRisk", new Integer(returnValue));
        }
        return returnValue;
    }

    /**
     * Returns a RecordSet loaded with list of insured information.
     * <p/>
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllInsuredInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllInsuredInfo", new Object[]{inputRecord});
        }
        // Setup the stored procedure
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Risk.Sel_Insured_Info");
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load all insured information.", e);
            l.throwing(getClass().getName(), "loadAllInsuredInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllInsuredInfo", rs);
        }
        return rs;
    }

    /**
     * Return the Chain status of the risk.
     *
     * @param inputRecord
     * @return
     */
    public Record getChainStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getChainStatus", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("endqId", "endorsementQuoteId"));
        // Setup the stored procedure
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("PM_CHAIN.Risk_Status",mapping);
        RecordSet rs = null;
        try {
            rs = sp.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get chain status.", e);
            l.throwing(getClass().getName(), "getChainStatus", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getChainStatus", rs);
        }
        return rs.getSummaryRecord();
    }

    /**
     * Returns risk detail id.
     * <p/>
     *
     * @param inputRecord
     * @return the risk detail id
     */
    public String getRiskDetailId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRiskDetailId", new Object[]{inputRecord});
        }

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("riskId", "riskDetailId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionId", "transactionLogId"));

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Risk_Detail_Id", mapping);
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get risk detail id. ", e);
            l.throwing(getClass().getName(), "getRiskDetailId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRiskDetailId", new Integer(returnValue));
        }
        return returnValue;
    }

    /**
     * Get all risks based on given ID list.
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllRiskByIds(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllRiskByIds", new Object[]{inputRecord});
        RecordSet rs = null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("pks", "ids"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fieldsList", "riskFieldsList"));
        mapping.addFieldMapping(new DataRecordFieldMapping("dbFieldsList", "riskDbFieldsList"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Misc.Sel_Data_By_Id_List", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load risks by gaven ID list:"
                + RiskFields.getRiskIds(inputRecord), e);
            l.throwing(getClass().getName(), "loadAllRiskByIds", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRiskByIds", rs);
        }
        return rs;
    }

    /**
     * Check the policy level combination is configured or not for auto risk relation.
     * If return value is greater than 0, it means this auto risk relation configuration is enable.
     * If return value is equals 0, it means this auto risk relation configuration is not enable.
     * @param inputRecord: Input record information.
     * @return PROD_RISK_RELATION primary key. If not exist, then return 0.
     */
    @Override
    public int isAutoRiskRelConfigured(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAutoRiskRelConfigured", new Object[]{inputRecord});
        int rtn = 0;

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Is_Auto_Risk_Rel_Available");
        try {
            rtn =  spDao.execute(inputRecord).getSummaryRecord().getIntegerValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to check if auto risk relation " +
                              "is configured.", e);
            l.throwing(getClass().getName(), "isAutoRiskRelConfigured", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAutoRiskRelConfigured", rtn);
        }
        return rtn;
    }

    /**
     *
     * @param inputRecord: policy header information and new inserted risk ids.
     * @return Y: At least one added successfully.
     *         N: No configured or unavailable.
     *         M: If no any auto risk relation is successful and at least one auto risk relation is failed by multiple
     *            owner risk types exist.
     *         P: When some relations were created, and some were not due to the multiple parents.
     */
    @Override
    public String processAutoRiskRelation(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isAutoRiskRelConfigured", new Object[]{inputRecord});
        String rtn = "N";

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(RiskFields.RISK_PKS, RiskFields.RISK_IDS));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Risk_Rel.Process_Auto_Risk_Relation", mapping);
        try {
            rtn =  spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process auto risk relation.", e);
            l.throwing(getClass().getName(), "processAutoRiskRelation", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processAutoRiskRelation", rtn);
        }
        return rtn;
    }

    /**
     * Determines if alternative rating method is editable.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public boolean isAlternativeRatingMethodEditable(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAlternativeRatingMethodEditable", new Object[]{inputRecord});
        }

        boolean isEditable = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Is_Alter_Rate_Method_Editable");
        try {
            isEditable = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find alternative rating editable", e);
            l.throwing(getClass().getName(), "loadAllRiskTypeAddCode", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAlternativeRatingMethodEditable", isEditable);
        }

        return isEditable;
    }

    /**
     * Load isFundState field value.
     *
     * @param inputRecord
     * @return Record
     */
    public Record loadIsFundStateValue(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadIsFundStateValue", new Object[]{inputRecord});
        }

        // Get the return value.
        Record outputRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Is_Fund_State");
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load isFundState value.", e);
            l.throwing(getClass().getName(), "loadIsFundStateValue", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadIsFundStateValue", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Get Default pcf county value.
     *
     * @param inputRecord
     * @return String
     */
    public String getDefaultValueForPcfCounty(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValueForPcfCounty", new Object[]{inputRecord});
        }

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Default_PCF_County");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get pcf county value.", e);
            l.throwing(getClass().getName(), "getDefaultValueForPcfCounty", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValueForPcfCounty", returnValue);
        }
        return returnValue;
    }

    /**
     * Get Default pcf risk class value.
     *
     * @param inputRecord
     * @return String
     */
    public String getDefaultValueForPcfRiskClass(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValueForPcfRiskClass", new Object[]{inputRecord});
        }

        // Get the return value.
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Risk.Get_Default_PCF_Specialty");
        try {
            returnValue = spDao.execute(inputRecord).getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get pcf county value.", e);
            l.throwing(getClass().getName(), "getDefaultValueForPcfRiskClass", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValueForPcfRiskClass", returnValue);
        }
        return returnValue;
    }

    /**
     * Determines if exclude_comp_gr1 field is visible.
     * @param inputRecord
     * @return
     */
    public boolean isGr1CompVisible (Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isGr1CompVisible", new Object[]{inputRecord});
        }

        boolean isVisible = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Is_Gr1_Comp_Visible");
        try {
            isVisible = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if exclude_comp_gr1 field is visible", e);
            l.throwing(getClass().getName(), "isGr1CompVisible", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isGr1CompVisible", isVisible);
        }

        return isVisible;
    }

    /**
     * Determines if exclude_comp_gr1 field is editable.
     * @param inputRecord
     * @return
     */
    public boolean isGr1CompEditable (Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isGr1CompEditable", new Object[]{inputRecord});
        }

        boolean isEditable = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Is_Gr1_Comp_Editable");
        try {
            isEditable = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if exclude_comp_gr1 field is editable", e);
            l.throwing(getClass().getName(), "isGr1CompEditable", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isGr1CompEditable", isEditable);
        }

        return isEditable;
    }

    /**
     * Determines if exclude_comp_gr2 field is editable.
     * @param inputRecord
     * @return
     */
    public boolean isGr2CompEditable (Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isGr2CompEditable", new Object[]{inputRecord});
        }

        boolean isEditable = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Risk.Is_Gr2_Comp_Editable");
        try {
            isEditable = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check if exclude_comp_gr2 field is editable", e);
            l.throwing(getClass().getName(), "isGr2CompEditable", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isGr2CompEditable", isEditable);
        }

        return isEditable;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public RiskJdbcDAO() {
    }
}
