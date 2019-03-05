package dti.pm.policymgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.StringConverter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.PMCommonFields;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.busobjs.SysParmIds;
import dti.pm.core.dao.BaseDAO;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.PolicyIdentifier;
import dti.pm.policymgr.Term;
import dti.pm.dao.DataFieldNames;
import dti.pm.transactionmgr.TransactionFields;

import java.security.Policy;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation details of all DAO operations that are performed against the policy manager.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 9, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/10/2007       sxm         Moved getDefaultState() to PMDefaultJDBCDAO
 * 12/18/2007       fcb         Call to policyHeader.getPolicyIdentifier().setLockedByOther added.
 * 02/01/2008       fcb         setLockedByOther call removed.
 * 04/16/2008       fcb         81766: createNextCycle - modified mapping.
 * 04/17/2008       fcb         80758: loadAllQuoteRiskCovg added.
 * 04/30/2008       yyh         ProcessQuoteStatus:loadAllQuoteStatus and saveQuoteStatus added.
 * 07/25/2008       yyh         Add getPolicyId.
 * 06/11/2009       Joe         Remove the method deriveImageRightMapping() which has been refactored into Common Service.
 * 02/23/2010       fcb         104451:loadPolicyHeader - added logging and logic to resubmit the database request when
 *                              the cursor returns 0 records.
 * 12/23/2010       wfu         issue 103999 - Added logic in loadPolicyHeader to fetch address information.
 * 09/06/2011       dzhang      issue 121130 - Rename getEntityRoleIdForPolicyholder to getEntityRoleIdForEntity.
 * 11/21/2011       sxm         issue 126493 - added changePolicy().
 * 12/02/2011       syang       issue 127694 - Added a mapping field to isRecordExist().
 * 03/14/2011       fcb         129528 - Policy Web Services.
 * 09/06/2011       fcb         137198 - Added loadPolicyTermList.
 * 10/08/2012       xnie        133766 - Added reRateOnDemand(), reRateBatch(), loadAllReRateResult() , and
 *                                       loadAllReRateResultDetail().
 * 11/19/2012       xnie        138948 - Modified reRateOnDemand() to change 'On-demand' to 'On-Demand'.
 * 12/12/2012       xnie        139838 - Modified reRateBatch() to change return value from void to record.
 * 01/30/2013       fcb         141479 - loadPolicyTermList: removed the restriction to handle one term only.
 * 07/26/2013       adeng       117011 - Modified createNextCycle() to add field mapping transactionComment2
 *                                       to newTransactionComment2.
 * 09/11/2013       fcb         147713 - Added additional logging when loading the policy header to make it more
 *                                       clear in the log of what is the source of the error.
 * 08/25/2015       awu         164026 - Added loadPolicyDetailForWS.
 * 01/15/2016       tzeng       166924 - Added isPolicyRetroDateEditable.
 * 01/25/2016       eyin        168882 - Added loadPolicyBillingAccountInfoForWS.
 * 03/08/2016       wdang       168418 - Move updateEntityRoleAddress and saveEntityRoleAddress to EntityJdbcDAO.
 * 06/17/2016       eyin        177211 - Added generatePolicyNumberForWS();
 * 12/19/2016       tzeng       166929 - 1) Added loadSoftValidationB(), getLatestTerm(), isNewBusinessTerm().
 *                                       2) Modified findAllPolicyForWS() to map policyNumberId as policyId.
 *                                       3) Modified createNextCycle() to mapping polPhaseCode to policyPhaseCode.
 * 05/02/2017       tzeng       166929 - Modified loadSoftValidationB() to change DB calling from Pm_Web_Custom to
 *                                       Pm_Web_Transaction.
 * 02/06/2017       lzhang      190834 - 1) Modified findAllPolicyOrMinimalInformationForWs/loadPolicyDetailForWS:
 *                                       add transStatusCodeFilter map
 *                                       2) Added validatePolicyNosExist and validateTermBaseRecordIdsExist.
 * 04/12/2018       lzhang      191379 - Added loadPolicyHeaderForWS
 * 10/25/2018       xnie        196704 - Modified addPolicy to add field mapping for issCompanyEntityId.
 * 10/30/2018       tyang       196789 - Modified loadPolicyHeader() to change policyHeader.getPolicyNo()
 *                                       with input parameter policyNo
 * 11/02/2018       wrong       196790 - Added getEntityIdByClientId.
 * ---------------------------------------------------
 */
public class PolicyJdbcDAO extends BaseDAO implements PolicyDAO {
    private final Logger l = LogUtils.getLogger(getClass());
    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord an input record represents the search criteria
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet findAllPolicy(Record inputRecord) {
        return findAllPolicy(inputRecord, null);
    }


    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord         an input record represents the search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet findAllPolicy(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "findAllPolicy", new Object[]{inputRecord});
        RecordSet rs = new RecordSet();

        // DataRecordMapping mapping = new DataRecordMapping();
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Search_List");

        try {
            rs = spDao.execute(inputRecord,recordLoadProcessor);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criterias", e);
            l.throwing(getClass().getName(), "findAllPolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "findAllPolicy", rs);
        return rs;
    }

    /**
     * Load policy summary for one client
     *
     * @param inputRecord  input record that contains entity id.
     * @return policy summary
     */
    public RecordSet loadAllPolicySummary(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllPolicySummary", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Summary");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllPolicySummary", se);
            l.throwing(getClass().getName(), "loadAllPolicySummary", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllPolicySummary", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Load related Endorsment/Renewal Quote of policy
     * @param inputRecord input record that contains termBaseRecordId.
     * @return quote list
     */
    public RecordSet loadAllEndorsementQuote(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllEndorsementQuote", new Object[]{inputRecord});
        }
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Sel_Endorsement_Quote_Info");
        RecordSet outRecordSet = null;
        try {
            outRecordSet = sp.execute(inputRecord);
        }
        catch (SQLException se) {
            AppException ae = ExceptionHelper.getInstance().handleException("Error when executing loadAllEndorsementQuote", se);
            l.throwing(getClass().getName(), "loadAllEndorsementQuote", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllEndorsementQuote", outRecordSet);
        }
        return outRecordSet;
    }

    /**
     * Method that returns an instance of Policy Header with all its class member information loaded up for the provided
     * policy number and policy term id.
     * <p/>
     * Most recent term is considered as the default term, if the provided policy term history id is null.
     * <p/>
     *
     * @param policyNo              policy number
     * @param termBaseRecordId      policy term history base record ID
     * @param policyTermHistoryId   policy term history id.
     * @param desiredPolicyViewMode desired view mode of WIP or OFFICIAL
     * @param endQuoteId       endorsement quote Id
     * @param webSessionId          optional instance of the unique web session id for locking when the desired view mode is WIP
     * @param policyLockDuration    duration of the lock
     * @return PolicyHeader An instance of Policy Header object.
     */
    public PolicyHeader loadPolicyHeader(String policyNo, String termBaseRecordId, String policyTermHistoryId,
                                         PolicyViewMode desiredPolicyViewMode,String endQuoteId, String webSessionId, String policyLockDuration) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadPolicyHeader", new Object[]{policyNo, termBaseRecordId,
            policyTermHistoryId, desiredPolicyViewMode, webSessionId});

        PolicyHeader policyHeader = null;

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("policyNo", policyNo);
            input.setFieldValue("termBaseRecordId", termBaseRecordId);
            input.setFieldValue("policyTermHistoryId", policyTermHistoryId);
            input.setFieldValue("desiredViewMode", desiredPolicyViewMode.getName());
            input.setFieldValue("endorsementQuoteId", endQuoteId);
            input.setFieldValue("webSessionId", webSessionId);
            input.setFieldValue("lockDuration", policyLockDuration);

            //Execute the stored proc returning the ref cursor
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Policy_Header");
            RecordSet rs = spDao.execute(input);
            if (rs.getSize() == 0 && policyNo.length()>0) {
                // There is a possiblity that while loading the policy header, and analyzing the data inside this
                // procedure, we set the desired_view_mode as WIP and build the sql checking the TEMP status of
                // Policy_Term_History records. If another thread (for example LongRunnigTransaction runs saveAsOfficial)
                // sets the status as OFFICIAL between these statements, and the moment the cursor is open, the cursor
                // could be empty. We attempt to re-run the Get_Policy_Header one more time with the same parameters.
                l.logp(Level.WARNING, getClass().getName(), "loadPolicyHeader",
                    "First Attempt to get Policy Header: Found 0 records, system will try again. Info: policyNo=" + policyNo + ", policyTermHistoryId=" +
                    policyTermHistoryId + ", desiredPolicyViewMode=" + desiredPolicyViewMode + ", endQuoteId=" +
                    endQuoteId + ", webSessionId=" + webSessionId + ", policyLockDuration=" + policyLockDuration);

                rs = spDao.execute(input);

                if (rs.getSize() == 1) {
                    l.logp(Level.WARNING, getClass().getName(), "loadPolicyHeader",
                        "Second Attempt to get Policy Header: Successfully retrieved information. Info: policyNo=" + policyNo + ", policyTermHistoryId=" +
                        policyTermHistoryId + ", desiredPolicyViewMode=" + desiredPolicyViewMode + ", endQuoteId=" +
                        endQuoteId + ", webSessionId=" + webSessionId + ", policyLockDuration=" + policyLockDuration);
                }
            }

            if (rs.getSize() != 1) {
                l.logp(Level.SEVERE, getClass().getName(), "loadPolicyHeader",
                    "Policy Header Information: Found " + rs.getSize()+ " records. Info: policyNo=" + policyNo + ", policyTermHistoryId=" +
                        policyTermHistoryId + ", desiredPolicyViewMode=" + desiredPolicyViewMode + ", endQuoteId=" +
                        endQuoteId + ", webSessionId=" + webSessionId + ", policyLockDuration=" + policyLockDuration);
                throw new AppException("Found " + rs.getSize() + " policy headers for policyNo '" + policyNo + "', policyTermHistoryId '" + policyTermHistoryId + "'");
            }

            //Take the first record out
            Record output = rs.getFirstRecord();

            //Added for issue 103999: fetch policyholder address and insured since date info
            spDao = null;
            Record inputPol = new Record();
            inputPol.setFieldValue("policyId", output.getStringValue("policyId"));
            inputPol.setFieldValue("transEff", output.getStringValue("termEffectiveFromDate"));
            inputPol.setFieldValue("entityId", output.getStringValue("policyHolderNameEntityId"));
            spDao = StoredProcedureDAO.getInstance("PM_SEL_POLHOLDER_ADDRESS");
            RecordSet rsPol = spDao.execute(inputPol);
            if (rsPol.getSize() == 0) {
                l.logp(Level.SEVERE, getClass().getName(), "loadPolicyHeader",
                    "Policy Header Information: Could not find policy holder address. Info: policyNo=" + policyNo + ", policyTermHistoryId=" +
                        policyTermHistoryId + ", policyHolderNameEntityId=" + output.getStringValue("policyHolderNameEntityId"));
                throw new AppException("Unable to get policy header policyholder address information for policy: " + policyNo);
            }
            Record outputPol = rsPol.getFirstRecord();
            output.setFieldValue("polInsuredSince", outputPol.getStringValue("insuredSince"));
            output.setFieldValue("polAddressLine1", outputPol.getStringValue("addressLine1"));
            output.setFieldValue("polAddressLine2", outputPol.getStringValue("addressLine2"));
            output.setFieldValue("polAddressLine3", outputPol.getStringValue("addressLine3"));
            output.setFieldValue("polCity", outputPol.getStringValue("city"));
            output.setFieldValue("polStateCode", outputPol.getStringValue("stateCode"));
            output.setFieldValue("polZipCode", outputPol.getStringValue("zipcode"));

            // Map the output record to the new PolicyHeader
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            policyHeader = new PolicyHeader();
            recBeanMapper.map(output, policyHeader);

            // Retrieve additional data not auto mapped within the PolicyHeader
            String policyId = output.getStringValue("policyId");
            String currentTermId = output.getStringValue("policyTermHistoryId");
            String currentTermBaseId = output.getStringValue("termBaseRecordId");

            // Get the summary record to pull/set the other output values
            Record summary = rs.getSummaryRecord();
            String actualViewMode = summary.getStringValue("actualViewMode");
            policyHeader.setShowViewMode(summary.getBooleanValue("showViewMode").booleanValue());
            String lockedMessage = summary.getStringValue("lockedMessage");

            //  Next re-initialize common variables
            spDao = null;
            rs = null;

            // Now load the entire term list, reusing the same input record
            input.setFieldValue("policyId", policyId);
            spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Term_List");
            rs = spDao.execute(input);
            if (rs.getSize() == 0) {
                l.logp(Level.SEVERE, getClass().getName(), "loadPolicyHeader",
                    "Policy Header Information: Could not find the policy term list. Info: policyNo=" + policyNo + ", policyTermHistoryId=" +
                        policyTermHistoryId + ", policyHolderNameEntityId=" + output.getStringValue("policyHolderNameEntityId"));

                throw new AppException("Unable to get policy header term list information for policy: " + policyHeader.getPolicyNo());
            }

            // Iterate through each term record
            Iterator iter = rs.getRecords();
            while (iter.hasNext()) {
                output = null;
                output = (Record) iter.next();
                Term policyTerm = null;
                policyTerm = new Term();

                // Map the output record to the Term bean
                RecordBeanMapper termRecBeanMapper = new RecordBeanMapper();
                termRecBeanMapper.map(output, policyTerm);

                // Finally add the Term bean into the PolicyHeader
                policyHeader.addPolicyTerm(policyTerm);
            }

            // Initialize variables for re-use
            rs = null;
            output = null;
            rs = loadPolicyLockInfo(policyNo);

            //Take the first record out
            output = rs.getFirstRecord();

            // Map the output record to the PolicyIdentifier
            RecordBeanMapper idRecBeanMapper = new RecordBeanMapper();
            idRecBeanMapper.map(output, policyHeader.getPolicyIdentifier());

            // Set addtional PolicyIdentifier bean data
            policyHeader.getPolicyIdentifier().setPolicyNo(policyNo);
            policyHeader.getPolicyIdentifier().setPolicyId(policyId);
            policyHeader.getPolicyIdentifier().setPolicyTermHistoryId(currentTermId);
            policyHeader.getPolicyIdentifier().setTermBaseRecordId(currentTermBaseId);
            policyHeader.getPolicyIdentifier().setPolicyViewMode(PolicyViewMode.getInstance(actualViewMode));
            policyHeader.getPolicyIdentifier().setEndorsementQuoteId(null);

            // Set lock information for PolicyIdentifier bean
            if (PolicyViewMode.getInstance(actualViewMode).equals(PolicyViewMode.WIP)) {
                policyHeader.getPolicyIdentifier().setPolicyLockId(webSessionId);
            }
            else {
                policyHeader.getPolicyIdentifier().setPolicyLockId("");
            }
            policyHeader.getPolicyIdentifier().setPolicyLockMessage(lockedMessage);

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy header.", e);
            l.throwing(getClass().getName(), "loadPolicyHeader", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPolicyHeader", policyHeader);

        return policyHeader;
    }

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param policyNo policy number
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet loadPolicyLockInfo(String policyNo) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyLockInfo", new Object[]{policyNo});

        RecordSet rs = null;

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("policyNo", policyNo);

            //Execute the stored proc returning the ref cursor
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Lock_Info");
            rs = spDao.execute(input);
            if (rs.getSize() != 1) {
                throw new AppException("Unable to get policy lock information for policy: " + policyNo);
            }

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to retrieve Policy Lock Information.", e);
            l.throwing(getClass().getName(), "loadPolicyLockInfo", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "loadPolicyLockInfo", rs);
        return rs;
    }

    /**
     * Method that returns a boolean value that indicates whether the policy picture has been changed, ever since it has
     * been loaded initially.
     * <p/>
     *
     * @param policyIdentifier an instance of Policy Identifier with all information loaded.
     * @return boolean true, if the policy picture has been changed; otherwise, false.
     */
    public boolean isPolicyPictureChanged(PolicyIdentifier policyIdentifier) {
        Logger l = LogUtils.enterLog(getClass(), "isPolicyPictureChanged", new Object[]{policyIdentifier});
        boolean isPictureChanged = true;

        if (policyIdentifier == null) {
            throw new AppException("Invalid policy information (null) passed to PolicyJdbcDAO.isPolicyPictureChanged method.");
        }

        try {
            RecordSet rs = loadPolicyLockInfo(policyIdentifier.getPolicyNo());
            if (rs.getSize() != 0) {
                Record r = rs.getFirstRecord();

                String wipNum = r.getStringValue("policyWipNumber");
                String offNum = r.getStringValue("policyOffNumber");

                if (policyIdentifier.getPolicyOffNumber().equalsIgnoreCase(offNum) && policyIdentifier.getPolicyWipNumber().equalsIgnoreCase(wipNum))
                {
                    l.logp(Level.FINE, getClass().getName(), "isPolicyPictureChanged", "policy identifier off num :" + policyIdentifier.getPolicyOffNumber() + " wip num: " + policyIdentifier.getPolicyWipNumber());
                    l.logp(Level.FINE, getClass().getName(), "isPolicyPictureChanged", "current off num :" + offNum + " wip num: " + wipNum);
                    isPictureChanged = false;
                }

            }
            else {
                throw new AppException("Unable to load policy lock information for policy: " + policyIdentifier.getPolicyNo());
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy lock information for policy: " + policyIdentifier.getPolicyNo(), e);
            l.throwing(getClass().getName(), "isPolicyPictureUnchanged", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "isPolicyPictureChanged", String.valueOf(isPictureChanged));
        return isPictureChanged;
    }

    /**
     * Method that returns an instance of a recordset object with policy information for the provided
     * policyHeader.
     * <p/>
     *
     * @param policyHeader        an instance of the Policy Header object
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy information.
     */
    public RecordSet loadAllPolicy(PolicyHeader policyHeader, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(getClass(), "loadAllPolicy", new Object[]{policyHeader});

        try {
            //Map the PolicyHeader values to the input record
            Record input = new Record();
            RecordBeanMapper recBeanMapper = new RecordBeanMapper();
            recBeanMapper.map(policyHeader, input);

            TransactionFields.setEndorsementQuoteId(input, policyHeader.getLastTransactionInfo().getEndorsementQuoteId());
            PMCommonFields.setRecordModeCode(input, policyHeader.getRecordMode());
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("endQuoteId", "endorsementQuoteId"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Policy_Info", mapping);

            RecordSet rs = spDao.execute(input, recordLoadProcessor);
            if (rs.getSize() == 0) {
                throw new AppException("Unable to get policy information for policy: " + policyHeader.getPolicyNo());
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAllPolicy", rs);
            }
            return rs;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select policy " + policyHeader.getPolicyNo(), e);
            l.throwing(getClass().getName(), "loadAllPolicy", ae);
            throw ae;
        }
    }

    /**
     * Save the given input records with the Pm_Nb_End.Save_Policy stored procedure,
     * This assumes all input records have recordModeCode = TEMP.
     * Set the rowStatus field to MODIFIED for records that have already been saved in this WIP transaction,
     * and are just being updated not.
     *
     * @param inputRecord a Record, each with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    public int addPolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "addPolicy", new Object[]{inputRecord});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNoEdit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renIndicator", "renewalIndicatorCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("nonRenRsn", "nonRenewalReasonCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("statusCode", "termStatus"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "lastTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyCycleCode", "policyCycle"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("requestEffDate", "requestedEffDate"));
        if (inputRecord.hasFieldValue("issCompanyEntityId")) {
            // It is for change issue company during renewal wip case.
            mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyId", "issCompanyEntityId"));
        }
        else {
            // It is for create a new policy case.
            mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyId", "issueCompanyEntityId"));
        }
        mapping.addFieldMapping(new DataRecordFieldMapping("processLoc", "processLocationCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("orgTypeCode", "organizationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("paymentPlanId", "paymentPlanLst"));

        // Update the records with 'Pm_Nb_End.Save_Policy'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_End.Save_Policy", mapping);
        try {
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save changed policy record.", e);
            l.throwing(getClass().getName(), "addPolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addPolicy", new Integer(updateCount));
        }
        return updateCount;
    }

   /**
     * Update the given input record with the Pm_Update.Change_Policy stored procedure,
     * assuming they all have field recordModeCode = Official.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    public int changePolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "changePolicy", new Object[]{inputRecord});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyNo", "policyNoEdit"));
        mapping.addFieldMapping(new DataRecordFieldMapping("requestEffDate", "requestedEffDate"));

        // Update the records with 'Pm_Update.Update_Policy'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Update.Change_Policy", mapping);
        try {
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to save changed policy record.", e);
            l.throwing(getClass().getName(), "changePolicy", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "changePolicy", new Integer(updateCount));
        }
        return updateCount;
    }

    /**
     * Update the given input record with the Pm_Endorse.Change_Policy stored procedure,
     * assuming they all have field recordModeCode = Official, and were marked as updated.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method..
     * @return the number of rows updated.
     */
    public int updatePolicy(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "updatePolicy", new Object[]{inputRecord});

        int updateCount = 0;

        // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("polTermId", "policyTermHistoryId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "lastTransactionId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("renIndicator", "renewalIndicatorCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("nonRenRsn", "nonRenewalReasonCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("orgTypeCode", "organizationTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("paymentPlanId", "paymentPlanLst"));
        mapping.addFieldMapping(new DataRecordFieldMapping("regionalOffice", "processLocationCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termBaseId", "termBaseRecordId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effFromDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

        // Update the records with 'Pm_Endorse.Change_Policy'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Endorse.Change_Policy", mapping);
        try {
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to change endorsed policy term record.", e);
            l.throwing(getClass().getName(), "updatePolicy", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "updatePolicy", new Integer(updateCount));
        return updateCount;
    }

    /**
     * Remove policy term history records related to an OOS change.
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method.
     */
    public void deleteOosPolicyDetail(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "deleteOosPolicyDetail", new Object[]{inputRecord});

         // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("termId", "polTermHistoryId"));

        // Update the records with 'Pm_Nb_Del.Del_Term'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Nb_Del.Del_Term", mapping);
        try {
            Record output = spDao.executeUpdate(inputRecord);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to delete oos endorsed policy term record.", e);
            l.throwing(getClass().getName(), "deleteOosPolicyDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "deleteOosPolicyDetail");
    }

    /**
     * Validate that the user has entered a valid OOSE term expiration date
     *
     * @param inputRecord a Records with the PolicyHeader, PolicyIdentifier,
     *                    and Policy Detail info matching the fields returned from the loadAllPolicy method.
     * @return valid boolean indicator if the term expiration date is valid
     */
    public boolean validateOoseTermExpDate(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "validateOoseTermExpDate", new Object[]{inputRecord});

        boolean valid = false;

         // Create a DataRecordMapping for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("ooseExpDate", "ooseTermExpDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));

        // Update the records with 'Pm_Validate_Term_Oose_Expdate'
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Validate_Term_Oose_Expdate", mapping);

        try {
            Record output = spDao.executeUpdate(inputRecord);
            valid = output.getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to validate Term OOSE Exp Date", e);
            l.throwing(getClass().getName(), "validateOoseTermExpDate", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateOoseTermExpDate", Boolean.valueOf(valid));
        }
        return valid;
    }

    /**
     * To check if policy notes exist
     *
     * @param inputRecord a Record with query conditions
     * @return Y/N
     */
    public String isRecordExist(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isRecordExist", new Object[]{inputRecord});
        }

        // Create field mapping
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping(DataFieldNames.TERM_EFF, "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceId", "id"));

        String result;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Record_Exist", mapping);
        try {
            result = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to get policy notes information.", e);
            l.throwing(getClass().getName(), "isRecordExist", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "isRecordExist", result);
        return result;
    }

    /**
     * Method that returns an instance of a recordset object with policy detail information for the provided
     * policyHeader.
     * <p/>
     *
     * @param inputRecord an input record that contains all member variables for the PolicyHeader
     * @return RecordSet an instance of the recordset result object that contains policy detail information.
     */
    public Record loadAddlInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadAddlInfo", new Object[]{inputRecord});

        Record output = new Record();

        try {
            //Retrieve policy primary coverage information
            Record primaryCoverageRec = loadPolicyPrimaryCoverage(inputRecord);
            if (primaryCoverageRec != null) {
                output.setFields(primaryCoverageRec);
            }

            //Retrieve policy premium information
            Record primaryPremiumRec = loadPolicyPremiumInfo(inputRecord);
            if (primaryPremiumRec != null) {
                output.setFields(primaryPremiumRec);
            }

            //Retrieve additional info fields
            output.setFields(loadPolicyAddlInfo(inputRecord));

            //Retrieve special handling
            output.setFields(loadSpecialHandlingInfo(inputRecord));

            //Retrieve highestCovgLimit and highestShareLimitB
            output.setFields(loadHighestLimitAndShared(inputRecord));

            //Retrive softValidationB
            if (YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(SysParmIds.PM_CHECK_SOFT_VAL_B, "N")).booleanValue()) {
                Record softValidationRecord = loadSoftValidationB(inputRecord);
                PolicyFields.setSoftValidationB(softValidationRecord, PolicyFields.getSoftValidationB(softValidationRecord).equals("Y") ? LABEL_FOR_YES : LABEL_FOR_NO);
                output.setFields(softValidationRecord);
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadAddlInfo", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select policy addl information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadAddlInfo", ae);
            throw ae;
        }
    }

    /**
     * Validates the modified policy no with a stored procedure determining if the policy no
     * is already in use, if a billing acount has been setup for the policy, or if an account
     * uses the same number.  If any is met, the modified policy no is invalid (FALSE).
     *
     * @param modifiedPolicyNo user modified policy no value.
     * @param policyId         policy primary key value of the policy no being changed.
     * @return String containing any validation messages.
     */
    public String validateModifiedPolicyNo(String modifiedPolicyNo, String policyId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateModifiedPolicyNo", new Object[]{modifiedPolicyNo, policyId});
        }

        String validationMsg = null;

        //Map the values to the input record
        Record input = new Record();
        input.setFieldValue("newPolicyNo", modifiedPolicyNo);
        input.setFieldValue("policyId", policyId);

        // Validate with Pm_Web_Policy.Validate_Modified_Policy_No
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Validate_Modified_Policy_No");
        try {
            Record output = spDao.executeUpdate(input);
            boolean isValid = Boolean.getBoolean(output.getStringValue("valid"));

            // If invalid, stored the returned message
            if (!isValid) {
                validationMsg = output.getStringValue("msg");
            }

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Pm_Web_Policy.Validate_Modified_Policy_No.", e);
            l.throwing(getClass().getName(), "validateModifiedPolicyNo", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "validateModifiedPolicyNo", validationMsg);
        return validationMsg;
    }

    /**
     * Get default reginal office code based on given state code
     *
     * @param inputRecord Record contains input values
     * @return String containing the default regional office code
     */
    public String getDefaultRegionalOffice(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultRegionalOffice", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "issueStateCode"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Get_Default_Regional_Office", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute Pm_Web_Policy.Validate_Modified_Policy_No.", e);
            l.throwing(getClass().getName(), "getDefaultRegionalOffice", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "getDefaultRegionalOffice", returnValue);
        return returnValue;
    }

    /**
     * Get default term expiration date
     *
     * @param inputRecord Record contains input values
     * @return String contains the override indicator concatenated with the default term expiration date
     */
    public String getDefaultTermExpirationDate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultTermExpirationDate", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCoId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueSt", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("practSt", "practiceStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Get_Default_Exp_Date", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Get_Default_Exp_Date.", e);
            l.throwing(getClass().getName(), "getDefaultTermExpirationDate", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "getDefaultTermExpirationDate", returnValue);
        return returnValue;
    }

    /**
     * Find all policy types based on search criteria
     *
     * @param inputRecord Record contains input values
     * @return RecordSet contains all policy types
     */
    public RecordSet findAllPolicyType(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "findAllPolicyType", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("currTermEffDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("currTermExpDate", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("cycleCode", "policyCycleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompanyId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueStateCode", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityType", "policyHolderEntityType"));
        mapping.addFieldMapping(new DataRecordFieldMapping("regOffice", "regionalOffice"));
        mapping.addFieldMapping(new DataRecordFieldMapping("code", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("shortDescription", "policyTypeDesc"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyTermTypeCode", "termTypeCode"));

        // get the return value
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_Policy_Types", mapping);
        RecordSet outputRecordSet;
        try {
            outputRecordSet = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Sel_Policy_Types.", e);
            l.throwing(getClass().getName(), "findAllPolicyType", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "findAllPolicyType", outputRecordSet);
        return outputRecordSet;
    }

    /**
     * Check policy existence
     *
     * @param inputRecord Record contains input values
     * @return String indicates if policy with the same policy typeexists
     */
    public String checkPolicyExistence(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkPolicyExistence", new Object[]{inputRecord});
        }

        // map the values to the input record
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "policyHolderNameEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyTypeCode", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

        // get the return value
        String returnValue;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Policy_Exist", mapping);
        try {
            RecordSet outputRecordSet = spDao.execute(inputRecord);
            returnValue = outputRecordSet.getSummaryRecord().getStringValue(spDao.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Policy_Exist.", e);
            l.throwing(getClass().getName(), "checkPolicyExistence", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "checkPolicyExistence", returnValue);
        return returnValue;
    }

    /**
     * Create a new policy
     *
     * @param inputRecord Record contains all values for creating policy
     * @return Record contains return code and policy no etc
     */
    public Record createPolicy(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createPolicy", new Object[]{inputRecord});
        }

        // create policy
        Record outputRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Create_Policy");
        try {
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Policy.Create_Policy.", e);
            l.throwing(getClass().getName(), "createPolicy", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "createPolicy", outputRecord);
        return outputRecord;
    }

    /**
     * Generate Policy Number
     *
     * @param inputRecord Record contains all values for policy number generation
     * @return Record contains return code and policy no etc
     */
    public Record generatePolicyNumberForWS(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generatePolicyNumberForWS", new Object[]{inputRecord});
        }

        Record outputRecord;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Gen_Policy_Number_For_WS");
        try {
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute PM_Web_Policy.Gen_Policy_Number_For_WS.", e);
            l.throwing(getClass().getName(), "generatePolicyNumberForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "generatePolicyNumberForWS", outputRecord);
        return outputRecord;
    }

    private Record loadPolicyPrimaryCoverage(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyPrimaryCoverage", new Object[]{inputRecord});

        try {
            Record output = null;

            // Execute the stored procedure for primary coverage
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Prim_Covg_Info");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() != 0) {
                output = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadPolicyPrimaryCoverage", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select policy primary coverage information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadPolicyPrimaryCoverage", ae);
            throw ae;
        }
    }

    private Record loadPolicyPremiumInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyPremiumInfo", new Object[]{inputRecord});

        try {
            Record output = null;

            // Execute the stored procedure for primary coverage
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Premium_INfo");
            RecordSet rs = spDao.execute(inputRecord);
            if (rs.getSize() != 0) {
                output = rs.getFirstRecord();
            }

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadPolicyPremiumInfo", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select policy premium information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadPolicyPremiumInfo", ae);
            throw ae;
        }
    }

    private Record loadPolicyAddlInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyAddlInfo", new Object[]{inputRecord});

        try {
            Record output = new Record();

            // Set some initial values
            inputRecord.setFieldValue("inputLevel", "POLICY");

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("level", "inputLevel"));
            mapping.addFieldMapping(new DataRecordFieldMapping("value", "policyTermHistoryId"));
            mapping.addFieldMapping(new DataRecordFieldMapping("fromDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("toDate", "termEffectiveToDate"));

            // Execute the stored procedure for additional info fields
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Addl_Info", mapping);
            output = spDao.executeUpdate(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadPolicyAddlInfo", output);
            }
            return output;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select policy additional information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadPolicyAddlInfo", ae);
            throw ae;
        }
    }

    private Record loadSpecialHandlingInfo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadSpecialHandlingInfo", new Object[]{inputRecord});

        try {
            Record outputReturn = new Record();

            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("expDate", "termEffectiveToDate"));

            // Execute the function for special handling
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Special_Handling_B", mapping);
            Record output = spDao.executeUpdate(inputRecord);

            // Get special handling value and set for the record to return that matches field_id
            outputReturn.setFieldValue("specialHandlingB", output.getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadSpecialHandlingInfo", outputReturn);
            }
            return outputReturn;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select special handling information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadSpecialHandlingInfo", ae);
            throw ae;
        }
    }

    /**
     *  Retrieve the highestCovgLimit and highestShareLimitB.
     *
     * @param inputRecord
     * @return
     */
    private Record loadHighestLimitAndShared(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadHighestLimitAndShared", new Object[]{inputRecord});
        RecordSet rs;
        Record returnRecord;
        Record outputRecord = new Record();
        try {
            // The fields transEffectiveFromDate and termEffectiveToDate exist in the inputRecord since it is policyHeader.toRecord().
            inputRecord.setFieldValue("termEffToDate", inputRecord.getStringValue("termEffectiveToDate"));
            if (inputRecord.hasStringValue("transactionCode") && "CANCEL".equals(inputRecord.getStringValue("transactionCode"))) {
                inputRecord.setFieldValue("termEffToDate", inputRecord.getStringValue("transEffectiveFromDate"));
            }
            // Create a DataRecordMapping for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("termEffFromDate", "termEffectiveFromDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("transLogId", "lastTransactionId"));

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_SEL_HIGHEST_LMT_CVG", mapping);
            rs = spDao.execute(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load highest limit and shared information for: " + inputRecord.getStringValue("policyNo"), e);
            l.throwing(getClass().getName(), "loadHighestLimitAndShared", ae);
            throw ae;
        }
        
        String highestCovgLimit = "";
        String highestShareLimitB = "";
        if(rs.getSize() > 0){
           returnRecord = rs.getFirstRecord(); 
           highestCovgLimit = returnRecord.getStringValue("coverageLimitDesc");
           highestShareLimitB = returnRecord.getStringValue("sharedLimitB");
        }
        outputRecord.setFieldValue("highestCovgLimit", highestCovgLimit);
        outputRecord.setFieldValue("highestShareLimitB", highestShareLimitB);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadHighestLimitAndShared", outputRecord);
        }
        return outputRecord;
    }

    /**
     * deny quote
     *
     * @param inputRecord
     * @return record include reuslt
     */
    public Record denyQuote(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "denyQuote");

        Record outputRecord;
        //set field values
        inputRecord.setFieldValue("fromCycle", "QUOTE");
        inputRecord.setFieldValue("toCycle", "DENY");
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "denyEffDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("reasonCode", "denyReason"));

        // call Pm_Process_Transaction.Process_Renewal procedure to deny quote
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Quote.Pm_Deny_Quote", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to deny quote.", e);
            l.throwing(getClass().getName(), "denyQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "denyQuote", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Reactive quote
     *
     * @param inputRecord
     * @return record include reuslt
     */
    public Record reactiveQuote(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "reactiveQuote");

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Reactivate_Quote", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to reactive quote.", e);
            l.throwing(getClass().getName(), "reactiveQuote", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "reactiveQuote", outputRecord);
        }
        return outputRecord;
    }



    /**
     * create quote no.
     *
     * @param inputRecord
     * @return new quote no
     */
    public String getNewQuoteNo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getNewQuoteNo");

        Record outputRecord;
        String newQuoteNo;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("origQuoteNo", "policyNo"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Create_Quote_Version_No", mapping);
        try {
             newQuoteNo=spDao.execute(inputRecord).getSummaryRecord().getStringValue("newNo"); 
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get new quote No.", e);
            l.throwing(getClass().getName(), "getNewQuoteNo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNewQuoteNo", newQuoteNo);
        }
        return newQuoteNo;
    }

    /**
     * create next cycle for policy and quote
     *
     * @param inputRecord
     * @return record include reuslt which contains the new policy/quote #
     */
    public Record createNextCycle(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "createNextCycle");

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("acctDate", "newAccountingDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("comment", "newTransactionComment"));
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionComment2", "newTransactionComment2"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termEff", "termEffectiveFromDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("termExp", "termEffectiveToDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompany", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "processLocationCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("policyPhaseCode", "polPhaseCode"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Cycle.Web_Pm_Create_Next_Cycle", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to create next cycle.", e);
            l.throwing(getClass().getName(), "createNextCycle", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createNextCycle", outputRecord);
        }
        return outputRecord;
    }



    /**
     * create parallel policy no.
     *
     * @param inputRecord
     * @return record include new policy no
     */
    public Record getParallelPolicyNo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getParallelPolicyNo");

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromPolType", "policyCycleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueState", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "regionalOffice"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromPolNo", "policyNo"));
        mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "policyHolderNameEntityId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Parallel_Pol_No", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get new policy No.", e);
            l.throwing(getClass().getName(), "getParallelPolicyNo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParallelPolicyNo", outputRecord);
        }
        return outputRecord;
    }

    /**
     * create parallel quote no.
     *
     * @param inputRecord
     * @return record include new quote no
     */
    public Record getParallelQuoteNo(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getParallelQuoteNo");

        Record outputRecord;
        // Create DataRecordMappig for this stored procedure
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyType", "policyTypeCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("fromPolType", "policyCycleCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("stateCode", "issueStateCode"));
        mapping.addFieldMapping(new DataRecordFieldMapping("issueCompEntId", "issueCompanyEntityId"));
        mapping.addFieldMapping(new DataRecordFieldMapping("processLocCode", "regionalOffice"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Sel_Pol_No", mapping);
        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get new quote No.", e);
            l.throwing(getClass().getName(), "getParallelQuoteNo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getParallelQuoteNo", outputRecord);
        }
        return outputRecord;
    }


    /**
     * Determines if the coverage class item should be enabled
     *
     * @param inputRecord Record contains policy id, risk id and evaluation date
     * @return boolean indicating yes/no to enable the coverage class option
     */
    public boolean isCoverageClassAvailable(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "isCoverageClassAvailable", new Object[]{inputRecord});

        try {
            boolean result = false;

            // Execute the function
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Is_Coverage_Class_Available");
            Record output = spDao.executeUpdate(inputRecord);

            // Get special handling value and set for the record to return that matches field_id
            result = output.getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "isCoverageClassAvailable", Boolean.valueOf(result));
            }
            return result;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to select coverage class availability", e);
            l.throwing(getClass().getName(), "isCoverageClassAvailable", ae);
            throw ae;
        }
    }
    /**
     * Determines if rolling IBNR date can be changed 
     *
     * @param inputRecord Record contains policy id, risk id and evaluation date
     * @return boolean indicating yes/no to allow the rolling IBNR change
     */
    public boolean canRollingIbnrDateChange(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "canRollingIbnrDateChange", new Object[]{inputRecord});

        try {
            boolean result = false;

            // Create DataRecordMappig for this stored procedure
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));

            // Execute the function
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Change_Rolling_Ibnr_Date", mapping);
            Record output = spDao.executeUpdate(inputRecord);

            // Get special handling value and set for the record to return that matches field_id
            result = output.getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "canRollingIbnrDateChange", Boolean.valueOf(result));
            }
            return result;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to call pm_change_rolling_ibnr_date", e);
            l.throwing(getClass().getName(), "canRollingIbnrDateChange", ae);
            throw ae;
        }
    }

    /**
     * Method to get policy key info
     *
     * @param inputRecord record contains policy no and eff date
     * @return Record with quote data
     */
    public Record getPolicyKeyInfo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyKeyInfo", new Object[]{inputRecord});
        }

        Record outputRecord=null;

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("effDate", "termEffectiveFromDate"));

        // Execute the function
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Get_Term_Pol_Key_Info", mapping);

        try {
            outputRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to derive policy key data.", e);
            l.throwing(getClass().getName(), "getPolicyKeyInfo", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyKeyInfo", outputRecord);
        }
        return outputRecord;
    }

    /**
     * Method to load selected addess and all available address for the policyholder or COI Holder
     *
     * @param inputRecord a record with query information
     * @param recordLoadProcessor an instance of data load processor
     * @return a RecordSet with selected address and all available address records
     */
    public RecordSet loadAllAddress(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAddress", new Object[]{inputRecord});
        }

        // get the return value
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Get_All_Address");
        RecordSet outputRecordSet;
        try {
            outputRecordSet = spDao.execute(inputRecord, recordLoadProcessor);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(
                "Failed to load selected address and all available address for policyholder or COI Holder.", e);
            l.throwing(getClass().getName(), "loadAllAddress", ae);
            throw ae;
        }

        // done
        l.exiting(getClass().getName(), "loadAllAddress", outputRecordSet);
        return outputRecordSet;
    }

    /**
     * Method to get entity role Id for entity.
     *
     * @param inputRecord a Record with query information
     * @return the entityRoleId
     */
    public String getEntityRoleIdForEntity(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "getEntityRoleIdForEntity", new Object[]{inputRecord});

        String entityRoleId;
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transEff", "transEffectiveFromDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Sel_EntRole_FK", mapping);
        try {
            entityRoleId = spDao.execute(inputRecord).getSummaryRecord().getStringValue("returnValue");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get entity role Id for entity.", e);
            l.throwing(getClass().getName(), "getEntityRoleIdForEntity", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityRoleIdForEntity", entityRoleId);
        }
        return entityRoleId;
    }

    /**
     * Loads risk coverage list for quote
     * @param inputRecord input record that contains entity id.
     * @return risk coverage list
     */
    public RecordSet loadAllQuoteRiskCovg(Record inputRecord, RecordLoadProcessor lp) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuoteRiskCovg", new Object[]{inputRecord, lp});
        }

        // Execute query
        RecordSet rs;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Quote_Risk_Covg");
        try {
            rs = spDao.execute(inputRecord, lp);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load quote risk coverage list.", e);
            l.throwing(getClass().getName(), "loadAllQuoteRiskCovg", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuoteRiskCovg", rs);
        }

        return rs;

    }

     /**
     * Checks for errors during Copy Quote
     * @param inputRecord input record that contains entity id.
     * @return YesNoFlag
     */
    public YesNoFlag isCopyQuoteError(Record inputRecord) {
         Logger l = LogUtils.getLogger(getClass());
         if (l.isLoggable(Level.FINER)) {
             l.entering(getClass().getName(), "isCopyQuoteError", new Object[]{inputRecord});
         }

         String isError;
         // Execute query
         StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quote.Is_Copy_Quote_Error");
         try {
             isError = spDao.execute(inputRecord).getSummaryRecord().getStringValue("returnValue");
         }
         catch (SQLException e) {
             AppException ae = ExceptionHelper.getInstance().handleException("Unable to get copy quote errors.", e);
             l.throwing(getClass().getName(), "isCopyQuoteError", ae);
             throw ae;
         }

         if (l.isLoggable(Level.FINER)) {
             l.exiting(getClass().getName(), "isCopyQuoteError", isError);
         }

         return YesNoFlag.getInstance(isError);
    }

    /**
     * Gets the Copy Quote error transaction.
     * @param inputRecord input record.
     * @return transactionId
     */
    public String getCopyQuoteErrorTrans(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCopyQuoteErrorTrans", new Object[]{inputRecord});
        }

        String transactionId;
        // Execute query
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Quote.Get_Copy_Quote_Error_Trans");
        try {
            transactionId = spDao.execute(inputRecord).getSummaryRecord().getStringValue("returnValue");
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get copy quote error transaction.", e);
            l.throwing(getClass().getName(), "getCopyQuoteErrorTrans", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCopyQuoteErrorTrans", transactionId);
        }

        return transactionId;
    }

    /**
     * Load all status.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllQuoteStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllQuoteStatus", new Object[]{inputRecord});
        }
        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            DataRecordFieldMapping fieldFromMapping = new DataRecordFieldMapping("effectiveFromDate", "effectiveFromDate");
            DataRecordFieldMapping fieldToMapping = new DataRecordFieldMapping("effectiveToDate", "effectiveToDate");
            StringConverter converter = (StringConverter) ConverterFactory.getInstance().getConverter(String.class);
            converter.setSimpleDateFormatPattern("MM/dd/yyyy HH:mm:ss");
            fieldFromMapping.setOutputFieldConverter(converter);
            fieldToMapping.setOutputFieldConverter(converter);
            mapping.addFieldMapping(fieldFromMapping);
            mapping.addFieldMapping(fieldToMapping);

            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_QUOTE.Sel_Quote_Status", mapping);
            rs = spDao.executeReadonly(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to load quote status.", e);
            l.throwing(getClass().getName(), "loadAllQuoteStatus", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllQuoteStatus", rs);
        }
        return rs;
    }

    /**
     * Save a quote stauts.
     *
     * @param inputRecord
     */
    public void saveQuoteStatus(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveQuoteStatus", new Object[]{inputRecord});
        }
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_QUOTE.Save_Quote_Status");
            spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to save quote status.", e);
            l.throwing(getClass().getName(), "saveQuoteStatus", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveQuoteStatus");
        }
    }

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyId(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyId");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_get_policy_pk");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get policy Id.", e);
            l.throwing(getClass().getName(), "getPolicyId", ae);
            throw ae;
        }
        String policyId = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyId", policyId);
        }
        return policyId;
    }

    /**
     * Get policy no by policy id
     *
     * @param inputRecord
     * @return Sting
     */
    public String getPolicyNo(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyNo");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_get_policy_no");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get policy no.", e);
            l.throwing(getClass().getName(), "getPolicyNo", ae);
            throw ae;
        }
        String policyNo = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyNo", policyNo);
        }
        return policyNo;

    }

    /**
     * Get Policy holder
     *
     * @param inputRecord
     * @return String
     */
    public String getPolicyHolder(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPolicyHolder", new Object[]{inputRecord});
        }
        String policyHolder;
        RecordSet rs;

        inputRecord.setFieldValue("roleTypeCode", "POLHOLDER");
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("sourceRecordId", "policyId"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("pm_sel_entity_name", mapping);
        try {
            rs = spDao.execute(inputRecord);
            policyHolder = rs.getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get policy Id.", e);
            l.throwing(getClass().getName(), "getPolicyId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPolicyHolder", policyHolder);
        }
        return policyHolder;
    }

    /**
     * Get the policy Id by policy No.
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPrimaryRisk(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getPrimaryRisk");
        }
        RecordSet rs;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Primary_Risk");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae =
                ExceptionHelper.getInstance().handleException("Unable to get primary risk.", e);
            l.throwing(getClass().getName(), "getPrimaryRisk", ae);
            throw ae;
        }
        
        Record returnRecord = rs.getSummaryRecord();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getPrimaryRisk", returnRecord);
        }
        return returnRecord;
    }

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord         an input record represents the search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet findAllPolicyForWS(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "findAllPolicyForWS", new Object[]{inputRecord});
        RecordSet rs = new RecordSet();
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyId", "policyNumberId"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_List_For_WS", mapping);

        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criteria", e);
            l.throwing(getClass().getName(), "findAllPolicyForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "findAllPolicyForWS", rs);
        return rs;
    }

    /**
     * Method that returns an instance of a recordset object with policy lock information for the provided
     * policy number.
     * <p/>
     *
     * @param inputRecord         an input record represents the search criteria
     * @param recordLoadProcessor an instance of data load processor
     * @return RecordSet an instance of the recordset result object that contains policy lock information.
     */
    public RecordSet findAllPolicyOrMinimalInformationForWs(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "findAllPolicyMinimalInformationForWs", new Object[]{inputRecord});
        RecordSet rs = new RecordSet();

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transStatusCodeFilter", "transactionStatusCode"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Info_For_Ws", mapping);

        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find Policies based on search criteria", e);
            l.throwing(getClass().getName(), "findAllPolicyMinimalInformationForWs", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "findAllPolicyMinimalInformationForWs", rs);
        return rs;
    }

    /**
     * Method that returns an instance of a record set object with the list of terms.
     * <p/>
     *
     * @param policyId policy pk
     * @return RecordSet an instance of the record set result object object with the list of terms.
     */
    public RecordSet loadPolicyTermList(String policyId) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyTermList", new Object[]{policyId});

        RecordSet rs = null;

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("policyId", policyId);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Term_List");
            rs = spDao.execute(input);
            if (rs.getSize() == 0) {
                throw new AppException("Unable to get policy header term list information for policy id : " + policyId);
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to retrieve Policy Term List.", e);
            l.throwing(getClass().getName(), "loadPolicyTermList", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "loadPolicyTermList", rs);
        return rs;
    }

    /**
     * To rerate On-demand.
     *
     * @param inputRecord
     * @return record
     */
    public Record reRateOnDemand(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "reRateOnDemand", new Object[]{inputRecord});

        Record retRecord;
        PolicyFields.setSubmitAs(inputRecord, PolicyFields.SubmitAsCodeValues.ON_DEMAND);
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mass_Rerate.Rerate_Policy");

        try {
            retRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to rerate On-Demand ", e);
            l.throwing(getClass().getName(), "reRateOnDemand", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "reRateOnDemand", retRecord);
        return retRecord;
    }

    /**
     * To rerate On-demand.
     *
     * @param inputRecord
     * @return record
     */
    public Record reRateBatch(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "reRateBatch", new Object[]{inputRecord});

        Record retRecord;
        PolicyFields.setSubmitAs(inputRecord, PolicyFields.SubmitAsCodeValues.BATCH);
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mass_Rerate.Rerate_Policy");

        try {
            retRecord = spDao.execute(inputRecord).getSummaryRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to rerate Batch ", e);
            l.throwing(getClass().getName(), "reRateBatch", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "reRateBatch");
        return retRecord;
    }

    /**
     * Load mass rerate result
     * @param inputRecord
     * @return mass rerate result
     */
    public RecordSet loadAllReRateResult(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadAllReRateResult", new Object[]{inputRecord});
        RecordSet rs = new RecordSet();

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mass_Rerate.Sel_Rerate_Result");

        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find mass rerate result based on search criteria", e);
            l.throwing(getClass().getName(), "loadAllReRateResult", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllReRateResult", rs);
        return rs;
    }

    /**
     * Load mass rerate result detail
     * @param inputRecord
     * @return mass rerate result detail
     */
    public RecordSet loadAllReRateResultDetail(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadAllReRateResultDetail", new Object[]{inputRecord});
        RecordSet rs = new RecordSet();

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Mass_Rerate.Sel_Rerate_Result_Detail");

        try {
            rs = spDao.execute(inputRecord, recordLoadProcessor);

        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to find mass rerate result detail based on search criteria", e);
            l.throwing(getClass().getName(), "loadAllReRateResultDetail", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadAllReRateResultDetail", rs);
        return rs;
    }

    /**
     * Load all the policy detail information for the Policy Inquiry Service.
     * @param inputRecord
     * @return
     */
    public RecordSet loadPolicyDetailForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadPolicyDetailForWS", new Object[]{inputRecord});

        RecordSet rs = new RecordSet();
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transStatusCodeFilter", "transactionStatusCode"));
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Sel_Policy_Detail_For_WS", mapping);
        try {
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy detail information for Policy Inquiry Service.", e);
            l.throwing(getClass().getName(), "loadPolicyDetailForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPolicyDetailForWS", rs);
        return rs;
    }

    /**
     * Determines if policy retro date is editable.
     *
     * @param inputRecord
     * @return
     */
    @Override
    public boolean isPolicyRetroDateEditable(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyRetroDateEditable", new Object[]{inputRecord});
        }

        boolean isEditable = false;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Is_Policy_Retro_Date_Editable");
        try {
            isEditable = spDao.executeUpdate(inputRecord).getBooleanValue(StoredProcedureDAO.RETURN_VALUE_FIELD).booleanValue();
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to determine if policy retro date is editable.", e);
            l.throwing(getClass().getName(), "isPolicyPictureUnchanged", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyRetroDateEditable", isEditable);
        }
        return isEditable;
    }

    /**
     * To load all policy billing accounts for policy level
     *
     * @param inputRecord
     * @return a list of billing account relations
     */
    public RecordSet loadPolicyBillingAccountInfoForWS(Record inputRecord) {
        Logger l = LogUtils.enterLog(getClass(), "loadPolicyBillingAccountInfoForWS", new Object[]{inputRecord});

        RecordSet rs;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Fmn_Billing_Setup.Sel_Policy_Billing_Setup");
            rs = spDao.execute(inputRecord);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get policy billing accounts.", e);
            l.throwing(getClass().getName(), "loadPolicyBillingAccountInfoForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPolicyBillingAccountsForWS", rs);
        return rs;
    }

    /**
     * Determines if entity belongs to PM.
     *
     * @param inputRecord
     * @return
     */
    public String isPolicyEntity(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPolicyEntity", new Object[]{inputRecord});
        }

        String isPolicyEntity = "N";
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("PM_Web_Policy.Is_Policy_Entity");
        try {
            isPolicyEntity = spDao.executeUpdate(inputRecord).getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to determine if entity belongs to PM.", e);
            l.throwing(getClass().getName(), "isPolicyEntity", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPolicyEntity", isPolicyEntity);
        }
        return isPolicyEntity;
    }

    @Override
    public Record loadSoftValidationB(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadSoftValidationB", new Object[]{inputRecord});
        }

        Record output = new Record();

        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("transactionLogId", "lastTransactionId"));

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Transaction.Get_Soft_Validation_B", mapping);
            PolicyFields.setSoftValidationB(output, spDao.executeUpdate(inputRecord).getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD));
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get indication for soft validation exists or not", e);
            l.throwing(getClass().getName(), "loadSoftValidationB", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadSoftValidationB", output);
        }
        return output;
    }

    @Override
    public Record getLatestTerm(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLatestTerm", new Object[]{inputRecord});
        }

        Record record = new Record();
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("policyId", "policyNumberId"));
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Get_Latest_Term", mapping);
            record = spDao.execute(inputRecord).getFirstRecord();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get latest term", e);
            l.throwing(getClass().getName(), "getLatestTerm", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLatestTerm", record);
        }
        return record;
    }

    @Override
    public boolean isNewBusinessTerm(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isNewBusinessTerm", new Object[]{inputRecord});
        }

        boolean isNewBusinessTermB = false;

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Is_New_Business_Term");
            isNewBusinessTermB = YesNoFlag.getInstance(spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD)).booleanValue();
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check whether is new business term", e);
            l.throwing(getClass().getName(), "isNewBusinessTerm", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isNewBusinessTerm", isNewBusinessTermB);
        }
        return isNewBusinessTermB;
    }

    /**
     * Identify whether policyNos exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid policyNo
     */
    public String validatePolicyNosExist(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validatePolicyNosExist", new Object[]{inputRecord});
        }

        String invalPolicyNos = "";

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Validate_Policy_Nos_Exist");
            invalPolicyNos = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check whether Policy No is valid", e);
            l.throwing(getClass().getName(), "validatePolicyNosExist", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validatePolicyNosExist", invalPolicyNos);
        }
        return invalPolicyNos;
    }

    /**
     * Identify whether termBaseRecordIds exist in system
     * <p/>
     *
     * @param inputRecord
     * @return invalid termBaseRecordIds
     */
    public String validateTermBaseRecordIdsExist(Record inputRecord){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateTermBaseRecordIdsExist", new Object[]{inputRecord});
        }

        String invaltermBaseRecordIds = "";

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy.Valid_Term_Base_Rec_Ids_Exist");
            invaltermBaseRecordIds = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to check whether termBaseRecordId is valid", e);
            l.throwing(getClass().getName(), "validateTermBaseRecordIdsExist", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateTermBaseRecordIdsExist", invaltermBaseRecordIds);
        }
        return invaltermBaseRecordIds;
    }

    /**
     * Get entity id
     * <p/>
     *
     * @param inputRecord
     */
    public String getEntityIdByClientId(Record inputRecord){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityIdByClientId", new Object[]{inputRecord});
        }

        String entityId = "";

        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Client_Utility.Get_Entity_Pk");
            entityId = spDao.execute(inputRecord).getSummaryRecord().getStringValue(StoredProcedureDAO.RETURN_VALUE_FIELD);
        }
        catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get entity id", e);
            l.throwing(getClass().getName(), "getEntityIdByClientId", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityIdByClientId", entityId);
        }

        return entityId;
    }

    /**
     * Return policyHeader for webService
     *
     * @param policyNo              policy number
     * @param termBaseRecordId      policy term history base record ID
     * @param transactionStatusCode transactionStatusCode
     */
    public PolicyHeader loadPolicyHeaderForWS(String policyNo, String termBaseRecordId, String transactionStatusCode) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadPolicyHeaderForWS", new Object[]{policyNo, termBaseRecordId, transactionStatusCode});

        PolicyHeader policyHeader = null;

        try {
            //Map the input values
            Record input = new Record();
            input.setFieldValue("policyNo", policyNo);
            input.setFieldValue("termBaseRecordId", termBaseRecordId);
            input.setFieldValue("desiredViewMode", StringUtils.isBlank(transactionStatusCode)? PolicyViewMode.WIP.getName() : transactionStatusCode);
            input.setFieldValue("transStatusCd", transactionStatusCode);

            //Execute the stored proc returning the ref cursor
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Pm_Web_Policy_Header.Get_Policy_Header_For_WS");
            RecordSet rs = spDao.execute(input);
            if (rs.getSize() == 0 && policyNo.length()>0) {
                // There is a possiblity that while loading the policy header, and analyzing the data inside this
                // procedure, we set the desired_view_mode as WIP and build the sql checking the TEMP status of
                // Policy_Term_History records. If another thread (for example LongRunnigTransaction runs saveAsOfficial)
                // sets the status as OFFICIAL between these statements, and the moment the cursor is open, the cursor
                // could be empty. We attempt to re-run the Get_Policy_Header one more time with the same parameters.
                l.logp(Level.WARNING, getClass().getName(), "loadPolicyHeader",
                    "First Attempt to get Policy Header: Found 0 records, system will try again. Info: policyNo=" + policyNo +
                        ", transStatusCd=" + transactionStatusCode);

                rs = spDao.execute(input);

                if (rs.getSize() == 1) {
                    l.logp(Level.WARNING, getClass().getName(), "loadPolicyHeader",
                        "Second Attempt to get Policy Header: Successfully retrieved information. Info: policyNo=" + policyNo +
                           ", transStatusCd=" + transactionStatusCode);
                }
            }

            if (rs.getSize() == 1) {
                //Take the first record out
                Record output = rs.getFirstRecord();

                // Map the output record to the new PolicyHeader
                RecordBeanMapper recBeanMapper = new RecordBeanMapper();
                policyHeader = new PolicyHeader();
                recBeanMapper.map(output, policyHeader);

                // Get the summary record to pull/set the other output values
                Record summary = rs.getSummaryRecord();
                String actualViewMode = summary.getStringValue("actualViewMode");
                policyHeader.getPolicyIdentifier().setPolicyViewMode(PolicyViewMode.getInstance(actualViewMode));
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load policy header for webService.", e);
            l.throwing(getClass().getName(), "loadPolicyHeaderForWS", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "loadPolicyHeaderForWS", policyHeader);

        return policyHeader;
    }
    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public PolicyJdbcDAO() {
    }

    public static final String LABEL_FOR_YES = "Yes";
    public static final String LABEL_FOR_NO = "No";
}
