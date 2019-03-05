package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.busobjs.SysParmIds;
import dti.pm.policymgr.PolicyHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/17/2017
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class PolicyWebServiceHelper {
    private final Logger l = LogUtils.getLogger(getClass());

    private static final PolicyWebServiceHelper INSTANCE = new PolicyWebServiceHelper();

    private PolicyWebServiceHelper(){};

    public static PolicyWebServiceHelper getInstance() {
        return INSTANCE;
    }

    /**
     * When Copy/Deny/Accept quote from Web Service,
     * it should validate the same as the logic in method isCopyDenyAcceptQuoteAvailable() of page entitlement.
     * @param quotePolicyHeader
     * @return
     */
    public boolean validateQuoteBeforeProcess(PolicyHeader quotePolicyHeader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateQuoteBeforeProcess", quotePolicyHeader);
        }

        MessageManager mm = MessageManager.getInstance();
        String quoteNo = quotePolicyHeader.getPolicyNo();

        // Validate that if it is a official Quote.
        if (quotePolicyHeader.isWipB()) {
            mm.addErrorMessage("ws.policy.quote.in.official", new Object[]{quoteNo});
            throw new AppException("Quote: " + quoteNo + " is not in official when processing copy/deny/accept.");
        }

        // Validate that if this quote is eligible to do copy.
        if (!quotePolicyHeader.getTermStatus().isActive() || quotePolicyHeader.isShortTermB() ||
            quotePolicyHeader.getProcessStatusCode().isProcessStatusCancelOnly()) {
            mm.addErrorMessage("ws.policy.quote.processCopyDenyAccept.ineligibleQuote.error", new Object[]{quoteNo});
            throw new AppException("Quote: " + quoteNo + " should be eligible when processing copy/deny/accept.");
        }

        // Validate that if there exists quote temp version.
        if (quotePolicyHeader.isQuoteTempVersionExists()) {
            mm.addErrorMessage("ws.policy.quote.processCopyDenyAccept.quoteTempVersionExists.error", new Object[]{quoteNo});
            throw new AppException("Quote: " + quoteNo + " can not exist other quote temp version when processing Copy/Deny/Accept.");
        }

        // Validate that if the quote is in endorsement transaction.
        if (!SysParmProvider.getInstance().getSysParm(SysParmIds.PM_ACCEPT_AGAIN, "N").equals("Y") && quotePolicyHeader.isQuoteEndorsementExists()) {
            mm.addErrorMessage("ws.policy.quote.processCopyDenyAccept.quoteEndorsementExists.error", new Object[]{quoteNo});
            throw new AppException("Quote: " + quoteNo + " can not in endorsement transaction when processing Copy/Deny/Accept.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateQuoteBeforeProcess");
        }

        return true;
    }

    /**
     * Set validation error to output.
     * @param orgMessageStatusType
     * @param policyNumber
     * @param newMessageStatusType
     */
    public void setValidationErrorToOutput(MessageStatusType orgMessageStatusType, String policyNumber, MessageStatusType newMessageStatusType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setValidationErrorToOutput", new Object[]{orgMessageStatusType, policyNumber, newMessageStatusType});
        }

        List<ExtendedStatusType> orgExtendedStatusTypes = orgMessageStatusType.getExtendedStatus();
        List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
        for (ExtendedStatusType orgExtendedStatusType: orgExtendedStatusTypes){
            ExtendedStatusType extendedStatusType = new ExtendedStatusType();
            extendedStatusType.setExtendedStatusCode(orgExtendedStatusType.getExtendedStatusCode());
            extendedStatusType.setExtendedStatusType(orgExtendedStatusType.getExtendedStatusType());
            extendedStatusType.setExtendedStatusDescription(orgExtendedStatusType.getExtendedStatusDescription()+ " [" + policyNumber +"]");
            extendedStatusTypes.add(extendedStatusType);
        }

        newMessageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
        newMessageStatusType.setMessageStatusCode(orgMessageStatusType.getMessageStatusCode());

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setValidationErrorToOutput");
        }
    }

    /**
     * If there exists soft/hard validation, it will return true.
     * @param e
     * @return
     */
    public Boolean getValidationFailureB(Exception e){
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getValidationFailureB", e);
        }
        Boolean validationFailureB = false;
        if (e instanceof AppException) {
            AppException validationAe = (AppException) e;
            if (validationAe.hasMessageParameters()) {
                Object[] messageParameters = validationAe.getMessageParameters();
                if (messageParameters.length == 1) {
                    Object messageParameter = validationAe.getMessageParameters()[0];
                    if(messageParameter instanceof Boolean){
                        validationFailureB = (boolean) validationAe.getMessageParameters()[0];
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getValidationFailureB", validationFailureB);
        }

        return validationFailureB;
    }
}
