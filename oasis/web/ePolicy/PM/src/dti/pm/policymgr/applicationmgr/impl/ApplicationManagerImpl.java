package dti.pm.policymgr.applicationmgr.impl;

import com.trinisys.tdes.swat.client.AuthTokenBuilder;
import dti.cs.util.HttpTool;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordLoadProcessorChainManager;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import dti.pm.policymgr.PolicyFields;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.applicationmgr.ApplicationFields;
import dti.pm.policymgr.applicationmgr.ApplicationManager;
import dti.pm.policymgr.applicationmgr.dao.ApplicationDAO;
import dti.pm.policymgr.service.EApplicationInquiryFields;
import dti.pm.transactionmgr.TransactionFields;
import dti.pm.validationmgr.impl.AddOrigFieldsRecordLoadProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business components for application
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 17, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/04/2012       bhong       129528 - Added processQuestionnaireRequest
 * 03/28/2017       tzeng       166929 - Added hasApplicationOnTermB, initiateApp.
 * ---------------------------------------------------
 */
public class ApplicationManagerImpl implements ApplicationManager {
    private final Logger l = LogUtils.getLogger(getClass());
    /**
     * Get the initial data for the page.
     * Get the application list for the currently selected term.
     *
     * @param inputRecord
     */
    public RecordSet loadApplicationList(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadApplicationList", new Object[]{inputRecord});
        }

        RecordSet rs;
        rs = getApplicationDAO().loadApplicationList(inputRecord);
        rs.setSummaryRecord(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadApplicationList");
        }
        return rs;
    }

    /**
     * Porcess and generate questionnaire
     *
     * @param inputRecords
     */
    public void processQuestionnaireRequest(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processQuestionnaireRequest", new Object[]{inputRecords});
        }

        getApplicationDAO().processQuestionnaireRequest(inputRecords);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processQuestionnaireRequest");
        }
    }

    /**
     * Load all applications
     *
     * @param inputRecord
     * @param loadProcessor
     * @return RecordSet
     */
    public RecordSet loadAllApplication(Record inputRecord, RecordLoadProcessor loadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllApplication", new Object[]{inputRecord});
        }

        RecordLoadProcessor entitlementLp = new ApplicationEntitlementRecordLoadProcessor();
        RecordLoadProcessor lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(loadProcessor, entitlementLp);
        lp = RecordLoadProcessorChainManager.getRecordLoadProcessor(origFieldLoadProcessor, lp);
        RecordSet rs = getApplicationDAO().loadAllApplication(inputRecord, lp);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllApplication", rs);
        }
        return rs;
    }

    /**
     * Save all applications
     *
     * @param inputRecord
     * @param inputRecords
     */
    public void saveAllApplication(Record inputRecord, RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllApplication", new Object[]{inputRecord, inputRecords});
        }

        if (inputRecords.getSize() > 0) {
            // Save application data
            getApplicationDAO().saveAllApplication(inputRecords);

            // Save change history
            Iterator it = inputRecords.getRecords();
            while (it.hasNext()) {
                Record rec = (Record) it.next();
                ApplicationFields.setWebFormWorkItemId(inputRecord, ApplicationFields.getWebFormWorkItemId(rec));
                if (ApplicationFields.ChangeTypeCodeValues.REASSIGN.equals(ApplicationFields.getChangeTypeCode(inputRecord))
                    && !ApplicationFields.getReviewerId(rec).equals(ApplicationFields.getOrigReviewerId(rec))) {
                    ApplicationFields.setChangeInfo(inputRecord, "Reassigned from " + ApplicationFields.getOrigReviewerId(rec));
                }
                else if (ApplicationFields.ChangeTypeCodeValues.REWIP.equals(ApplicationFields.getChangeTypeCode(inputRecord))) {
                    ApplicationFields.setChangeInfo(inputRecord, "Set the application status to WIP");
                }
                else if (ApplicationFields.ChangeTypeCodeValues.SENDREMINDER.equals(ApplicationFields.getChangeTypeCode(inputRecord))) {
                    ApplicationFields.setChangeInfo(inputRecord, "Send reminder to email:  " + ApplicationFields.getPreparerEmailAddress(rec));
                }
                getApplicationDAO().saveHistory(inputRecord);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllApplication");
        }
    }

    /**
     * Load all change history
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadAllHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllHistory", new Object[]{inputRecord,});
        }
        RecordSet rs = getApplicationDAO().loadAllHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllHistory", rs);
        }
        return rs;
    }

    /**
     * Load all available app reviewer
     *
     * @return RecordSet
     */
    public RecordSet loadAllAvailableAppReviewer() {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableAppReviewer");
        }

        Record inputRecord = new Record();
        RecordSet rs = getApplicationDAO().loadAllAppReviewer(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableAppReviewer", rs);
        }
        return rs;
    }

    /**
     * Check if it exists application upon term base and form type.
     * @param inputRecord
     * @return
     */
    private boolean hasApplicationB(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasApplicationB", inputRecord);
        }

        boolean HasApplicationB = getApplicationDAO().hasApplicationB(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasApplicationB");
        }
        return HasApplicationB;
    }

    @Override
    public void initiateAppForUI(Record inputRecord, String userName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initiateAppForUI", new Object[]{inputRecord, userName});
        }

        Record initiateAppRecord = new Record();
        EApplicationInquiryFields.setPolicyTermNumberId(initiateAppRecord, PolicyHeaderFields.getTermBaseRecordId(inputRecord));
        EApplicationInquiryFields.setPolicyId(initiateAppRecord, PolicyHeaderFields.getPolicyNo(inputRecord));
        EApplicationInquiryFields.setPolicyNumberId(initiateAppRecord, PolicyHeaderFields.getPolicyId(inputRecord));
        EApplicationInquiryFields.setTypeCode(initiateAppRecord, EApplicationInquiryFields.getTypeCode(inputRecord));

        Record outputRecord = processInitiateApp(initiateAppRecord, userName);
        String result = EApplicationInquiryFields.getInitResult(outputRecord);

        boolean initiateEappAsyncB = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_PM_INIT_ASYNC, "Y")).booleanValue();
        if (initiateEappAsyncB) {
            EApplicationInquiryFields.setInitResult(inputRecord, result);
            getApplicationDAO().recordDiaryForApplication(inputRecord);
        }
        else {
            if (result.equals(EApplicationInquiryFields.APP_INIT_SUCCESS)) {
                MessageManager.getInstance().addInfoMessage("pm.eApp.initiate.process.success");
            }
            else if (result.equals(EApplicationInquiryFields.APP_INIT_EXISTED)) {
                MessageManager.getInstance().addWarningMessage("pm.eApp.initiate.applicationExists.info");
            }
            else if (result.equals(EApplicationInquiryFields.APP_INIT_ERROR)) {
                MessageManager.getInstance().addErrorMessage("pm.eApp.initiate.process.error");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initiateAppForUI");
        }
    }

    @Override
    public Record processInitiateApp(Record inputRecord, String userName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processInitiateApp", new Object[]{inputRecord, userName});
        }

        Record outputRecord = new Record();
        Long applicationId = 0L;
        String initResult = null;
        Exception exception = null;
        try {
            //Validation if the type of application has been existed in the current term.
            if (hasApplicationB(inputRecord)) {
                initResult = EApplicationInquiryFields.APP_INIT_EXISTED;
                return outputRecord;
            }

            //Prepare business parameters
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put(EApplicationInquiryFields.POLICY_ID, EApplicationInquiryFields.getPolicyId(inputRecord));
            paramMap.put(EApplicationInquiryFields.POLICY_NUMBER_ID, EApplicationInquiryFields.getPolicyNumberId(inputRecord));
            paramMap.put(EApplicationInquiryFields.POLICY_TERM_NUMBER_ID, EApplicationInquiryFields.getPolicyTermNumberId(inputRecord));
            paramMap.put(EApplicationInquiryFields.TYPE_CODE, EApplicationInquiryFields.hasTypeCode(inputRecord) ? EApplicationInquiryFields.getTypeCode(inputRecord) : null);
            paramMap.put(EApplicationInquiryFields.STATUS_CODE, EApplicationInquiryFields.hasStatusCode(inputRecord) ? EApplicationInquiryFields.getStatusCode(inputRecord) : EApplicationInquiryFields.STATUS_CODE_REQUESTED);

            //Get configuration
            String eAppBaseUrl = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_BASE_URL_OVERRIDE);
            if(StringUtils.isBlank(eAppBaseUrl)) {
                eAppBaseUrl = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_BASE_URL);
            }
            String eAppInitiateActionPath = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_PM_INIT_PATH_OVERRIDE);
            if (StringUtils.isBlank(eAppInitiateActionPath)) {
                eAppInitiateActionPath = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_PM_INIT_PATH);
            }
            String authKey = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_TOKEN_AUTH_KEY_OVERRIDE);
            if(StringUtils.isBlank(authKey)) {
                authKey = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_TOKEN_AUTH_KEY);
            }
            String authIv = SysParmProvider.getInstance().getSysParm(ApplicationFields.EAPP_TOKEN_AUTH_INIT_VECTOR_OVERRIDE);
            if(StringUtils.isBlank(authIv)) {
                authIv = ApplicationContext.getInstance().getProperty(ApplicationFields.EAPP_TOKEN_AUTH_INIT_VECTOR);
            }
            int eAppInitiateTimeOut = SysParmProvider.getInstance().getSysParmAsInt(ApplicationFields.EAPP_PM_INIT_TIMEOUT, EAPP_PM_INIT_TIMEOUT_DEFAULT);

            //Fill whole URL to call HTTP
            eAppBaseUrl = HttpTool.getUrlWithParameters(eAppBaseUrl += eAppInitiateActionPath, paramMap);
            AuthTokenBuilder builder = new AuthTokenBuilder();
            builder.setUsername(userName);
            builder.setExpiration(getUtcExpirationDate(30));
            String protectedSwatUrl = builder.makeProtectedSwatUrl(eAppBaseUrl, authKey, authIv);
            String result = HttpTool.get(protectedSwatUrl, eAppInitiateTimeOut, eAppInitiateTimeOut, null);

            applicationId = Long.parseLong(result);

            if (applicationId <= 0) {
                l.logp(Level.WARNING, getClass().getName(), "processInitiateApp", "The new application id = "+ applicationId +" is invalid.");
                initResult = EApplicationInquiryFields.APP_INIT_ERROR;
                return outputRecord;
            }

            if (l.isLoggable(Level.INFO)) {
                l.logp(Level.INFO, getClass().getName(), "processInitiateApp", "A new application was created successfully, " +
                       "new application id = " + applicationId + " in Webform_Work_Item table.");
            }

            initResult = EApplicationInquiryFields.APP_INIT_SUCCESS;
        }
        catch (NumberFormatException e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Error in number format the result from eApp system.", e, false);
            l.logp(Level.SEVERE, getClass().getName(), "processInitiateApp", ae.getMessage(), ae);
            initResult = EApplicationInquiryFields.APP_INIT_ERROR;
            exception = ae;
        }
        catch (Exception e){
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Invoke initiateApp unexpected error.", e);
            l.logp(Level.SEVERE, getClass().getName(), "processInitiateApp", ae.getMessage(), ae);
            initResult = EApplicationInquiryFields.APP_INIT_ERROR;
            exception = ae;
        }
        finally {
            EApplicationInquiryFields.setEApplicationId(outputRecord, applicationId > 0 ? String.valueOf(applicationId) : null);
            EApplicationInquiryFields.setInitResult(outputRecord, initResult);
            EApplicationInquiryFields.setInitException(outputRecord, exception);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processInitiateApp");
        }

        return outputRecord;
    }

    @Override
    public String getApplicationTypeCode(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getApplicationTypeCode", inputRecord);
        }

        String applicationTypeCode = null;

        boolean isNewBusinessTermB = getPolicyManager().isNewBusinessTerm(inputRecord);
        if (isNewBusinessTermB) {
            applicationTypeCode = EApplicationInquiryFields.TYPE_CODE_NBAPP;
        }
        else {
            applicationTypeCode = EApplicationInquiryFields.TYPE_CODE_RENAPP;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getApplicationTypeCode");
        }
        return applicationTypeCode;
    }

    @Override
    public Date getUtcExpirationDate(int timeoutInMinutes) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUtcExpirationDate", timeoutInMinutes);
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.MINUTE, timeoutInMinutes);
        SimpleDateFormat dateFormatUTC = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date exp = dateFormatLocal.parse(dateFormatUTC.format(c.getTime()));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUtcExpirationDate");
        }

        return exp;
    }

    public void verifyConfig() {
        if (getApplicationDAO() == null)
            throw new ConfigurationException("The required property 'getApplicationDAO' is missing.");
    }

    public ApplicationDAO getApplicationDAO() {
        return m_applicationDAO;
    }

    public void setApplicationDAO(ApplicationDAO applicationDAO) {
        m_applicationDAO = applicationDAO;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    private ApplicationDAO m_applicationDAO;
    private PolicyManager m_policyManager;

    private static AddOrigFieldsRecordLoadProcessor origFieldLoadProcessor = new AddOrigFieldsRecordLoadProcessor(
        new String[]{ApplicationFields.REVIEWER_ID});

    private static final int EAPP_PM_INIT_TIMEOUT_DEFAULT = 60000;
}
