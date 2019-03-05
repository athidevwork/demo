package dti.pm.policymgr.reviewduplicatemgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PMRecordSetHelper;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.quickquotemgr.dao.QuickQuoteDAO;
import dti.pm.policymgr.reviewduplicatemgr.ReviewDuplicateFields;
import dti.pm.policymgr.reviewduplicatemgr.ReviewDuplicateManager;
import dti.pm.policymgr.reviewduplicatemgr.dao.ReviewDuplicateDAO;
import dti.pm.policymgr.taxmgr.TaxFields;
import dti.pm.validationmgr.impl.ContinuityRecordSetValidator;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Renewal Candidate.
 * <p/>
 *
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   June 28, 2016
 *
 * @author ssheng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/28/2016        ssheng      164927 - Created this action class for Quote Import enhancement.
 * 03/13/2018        tzeng       189424 - Modified validateAllRosterRisk to add validations for suffix name and license
 *                                        state/type.
 * ---------------------------------------------------
 */
public class ReviewDuplicateManagerImpl implements ReviewDuplicateManager {
    /**
     * Load all roster risks
     *
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllRosterRisk(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRosterRisk", new Object[]{policyHeader});
        }
        RecordSet rs = null;
        Record rc = null;
        Record inputRecord = new Record();
        String eventHeaderId = "";
        if (inputRecord.hasStringValue("policyLoadEventHeaderId")) {
            inputRecord.getStringValue("policyLoadEventHeaderId");
        }
        if (StringUtils.isBlank(eventHeaderId)) {
            // Check if there's existing record in policy_load_event_header
            inputRecord.setFieldValue("policyId", policyHeader.getPolicyId());
            inputRecord.setFieldValue("termId", policyHeader.getTermBaseRecordId());
            rc = getQuickQuoteDAO().getLoadEventHeader(inputRecord);
            eventHeaderId = rc.getStringValue("policyLoadEventHeaderId");
        }

        inputRecord.setFieldValue("loadEventHeaderId", eventHeaderId);
        rs = getReviewDuplicateDAO().loadAllRosterRisk(inputRecord);
        rs.getSummaryRecord().setFields(rc);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllRosterRisk", rs);
        }
        return rs;
    }
    /**
     * Load all CIS Match entity
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllCISDuplicate(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllCISDuplicate", new Object[]{inputRecord});
        }
        RecordSet rs = null;
        Record rc = null;
        String eventHeaderId = "";
        if (inputRecord.hasStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID)) {
            inputRecord.getStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID);
        }
        if (StringUtils.isBlank(eventHeaderId)) {
            // Check if there's existing record in policy_load_event_header
            rc = getQuickQuoteDAO().getLoadEventHeader(inputRecord);
            eventHeaderId = rc.getStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID);
        }

        inputRecord.setFieldValue(ReviewDuplicateFields.LOAD_EVENT_HEADER_ID, eventHeaderId);
        rs = getReviewDuplicateDAO().loadAllCISDuplicate(inputRecord);
        rs.getSummaryRecord().setFields(rc);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllCISDuplicate", rs);
        }
        return rs;
}

    /**
     * Save all input records with UPDATE_IND set to 'Y' - updated, 'I' - inserted, or 'N' - Not changed.
     *
     * @param policyHeader
     * @param inputRecords
     * @param cisDupInputRecords
     * @return
     */
    public void saveReviewDuplicate(PolicyHeader policyHeader, RecordSet inputRecords, RecordSet cisDupInputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveReviewDuplicate", new Object[]{policyHeader, inputRecords, cisDupInputRecords});
        }
        // Set the rowStatus to all records.
        RecordSet changedRosterRiskInputRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);
        RecordSet changedCISDupInputRecords = PMRecordSetHelper.setRowStatusOnModifiedRecords(cisDupInputRecords);

        if (changedRosterRiskInputRecords.getSize() > 0) {
            changedRosterRiskInputRecords.setFieldValueOnAll(ReviewDuplicateFields.SAVE_ALL_B, ReviewDuplicateFields.NO);
        }

        if (changedRosterRiskInputRecords.getSize() > 0) {
            // validation
            validateAllRosterRisk(policyHeader, changedRosterRiskInputRecords);
        }

        if (changedCISDupInputRecords.getSize() > 0) {
            // validation
            validateAllCISDuplicate(policyHeader, changedCISDupInputRecords);
        }

        if (changedCISDupInputRecords.getSize() > 0) {
            getReviewDuplicateDAO().saveUseCISRecord(changedCISDupInputRecords);
        }

        if (changedRosterRiskInputRecords.getSize() > 0) {
            getReviewDuplicateDAO().savePopulateToCIS(changedRosterRiskInputRecords);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveReviewDuplicate");
        }
    }


    /**
     * Save review all Duplicate records to CIS
     *
     * @param policyHeader
     * @param inputRecords
     * @return
     */
    public void saveAllToCIS(PolicyHeader policyHeader, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllToCIS", new Object[]{policyHeader});
        }
        inputRecords.setFieldValueOnAll(ReviewDuplicateFields.SAVE_ALL_B, ReviewDuplicateFields.YES);
        if(inputRecords.getSize() > 0) {
            // validation
            validateAllRosterRisk(policyHeader, inputRecords);
            getReviewDuplicateDAO().savePopulateToCIS(inputRecords);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllToCIS");
        }
    }

    private void validateAllRosterRisk(PolicyHeader policyHeader, RecordSet inputRecords) throws ValidationException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllRosterRisk", new Object[] { inputRecords });
        }
        String invalidSuffixNameIds = null;
        String invalidLicenseTypeIds = null;
        String invalidLicenseStateIds = null;
        for (int i = 0, rowNo = 0; i < inputRecords.getSize(); i ++) {
            rowNo++;
            Record record = inputRecords.getRecord(i);
            if("-1".equalsIgnoreCase(ReviewDuplicateFields.getAddToCis(record)) ||
                ReviewDuplicateFields.YES.equalsIgnoreCase(ReviewDuplicateFields.getSaveAllB(record))){
                if(StringUtils.isBlank(ReviewDuplicateFields.getStateCode(record), true)){
                    MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.emptyState.error");
                }

                if(StringUtils.isBlank(ReviewDuplicateFields.getCity(record), true)){
                    MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.emptyCity.error");
                }

                if(StringUtils.isBlank(ReviewDuplicateFields.getAddress1(record), true)){
                    MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.emptyAddress1.error");
                }

                String invalidateTypes = getReviewDuplicateDAO().validateCISInfo(record);
                if (invalidateTypes.contains(ReviewDuplicateFields.INVALID_TO_CIS_SUFFIX_NAME)) {
                    if (StringUtils.isBlank(invalidSuffixNameIds)) {
                        invalidSuffixNameIds = ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                    else {
                        invalidSuffixNameIds += ID_SEPARATOR + ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                }

                if (invalidateTypes.contains(ReviewDuplicateFields.INVALID_TO_CIS_LICENSE_STATE)) {
                    if (StringUtils.isBlank(invalidLicenseStateIds)) {
                        invalidLicenseStateIds = ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                    else {
                        invalidLicenseStateIds += ID_SEPARATOR + ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                }

                if (invalidateTypes.contains(ReviewDuplicateFields.INVALID_TO_CIS_LICENSE_TYPE)) {
                    if (StringUtils.isBlank(invalidLicenseTypeIds)) {
                        invalidLicenseTypeIds = ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                    else {
                        invalidLicenseTypeIds += ID_SEPARATOR + ReviewDuplicateFields.getPolicyLoadEventDetailId(record);
                    }
                }
            }
        }

        if (!StringUtils.isBlank(invalidSuffixNameIds)) {
            MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.invalid.suffixName.error", new String[]{invalidSuffixNameIds});
        }

        if (!StringUtils.isBlank(invalidLicenseTypeIds)) {
            MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.invalid.licenseType.error", new String[]{invalidLicenseTypeIds});
        }

        if (!StringUtils.isBlank(invalidLicenseStateIds)) {
            MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.invalid.licenseState.error", new String[]{invalidLicenseStateIds});
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllRosterRisk");
        }
    }

    private void validateAllCISDuplicate(PolicyHeader policyHeader, RecordSet inputRecords) throws ValidationException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateAllCISDuplicate", new Object[] { inputRecords });
        }

        for (int i = 0; i < inputRecords.getSize(); i ++) {
            Record record = inputRecords.getRecord(i);
            if ("-1".equalsIgnoreCase(ReviewDuplicateFields.getUseCisRecord(record))) {
                // Check if there's existing record in policy_load_event_header
                record.setFieldValue(PolicyFields.EFFECTIVE_FROM_DATE, policyHeader.getTermEffectiveFromDate());
                record.setFieldValue(PolicyFields.EFFECTIVE_TO_DATE, policyHeader.getTermEffectiveToDate());
                record.setFieldValue(PolicyFields.POLICY_ID, policyHeader.getPolicyId());
                String returnMsg = getReviewDuplicateDAO().validateRisk(record);
                if (!StringUtils.isBlank(returnMsg)) {
                    MessageManager.getInstance().addErrorMessage("pm.reviewDuplicate.saveReviewDuplicateInfo.error", new String[]{returnMsg});
                }
            }
        }

        if (MessageManager.getInstance().hasErrorMessages()) {
            throw new ValidationException("Invalid Data.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateAllCISDuplicate");
        }
    }

    /**
     * Validate no process review duplicate
     *
     * @param policyHeader
     * @return
     */
    public String validateReviewDuplicate(PolicyHeader policyHeader) {
        Logger l = LogUtils.getLogger(getClass());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateReviewDuplicate", new Object[]{policyHeader});
        }
        String returnValue = null;
        Record rc = null;
        Record inputRecord = new Record();
        String eventHeaderId = "";
        if (inputRecord.hasStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID)) {
            inputRecord.getStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID);
        }
        if (StringUtils.isBlank(eventHeaderId)) {
            // Check if there's existing record in policy_load_event_header
            inputRecord.setFieldValue(PolicyFields.POLICY_ID, policyHeader.getPolicyId());
            inputRecord.setFieldValue(ReviewDuplicateFields.TERM_ID, policyHeader.getTermBaseRecordId());
            rc = getQuickQuoteDAO().getLoadEventHeader(inputRecord);
            eventHeaderId = rc.getStringValue(ReviewDuplicateFields.POLICY_LOAD_EVENT_HEADER_ID);
        }

        inputRecord.setFieldValue(ReviewDuplicateFields.LOAD_EVENT_HEADER_ID, eventHeaderId);
        returnValue = getReviewDuplicateDAO().validateReviewDuplicate(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateReviewDuplicate", returnValue);
        }
        return returnValue;
    }

    /**
     * verify config
     */
    public void verifyConfig() {
        if (getReviewDuplicateDAO() == null)
            throw new ConfigurationException("The required property 'reviewDuplicateDAO' is missing.");
        if (getQuickQuoteDAO() == null)
            throw new ConfigurationException("The required property 'quickQuoteDAO' is missing.");
    }


    public ReviewDuplicateDAO getReviewDuplicateDAO() {
        return m_reviewDuplicateDAO;
    }

    public void setReviewDuplicateDAO(ReviewDuplicateDAO reviewDuplicateDAO) {
        m_reviewDuplicateDAO = reviewDuplicateDAO;
    }

    public QuickQuoteDAO getQuickQuoteDAO() {
        return m_quickQuoteDAO;
    }

    public void setQuickQuoteDAO(QuickQuoteDAO quickQuoteDAO) {
        m_quickQuoteDAO = quickQuoteDAO;
    }

    private ReviewDuplicateDAO m_reviewDuplicateDAO;
    private QuickQuoteDAO m_quickQuoteDAO;

    private static final String ID_SEPARATOR = ",";
}
