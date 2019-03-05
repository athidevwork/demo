package dti.pm.policyattributesmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordBeanMapper;
import dti.oasis.recordset.RecordSet;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.pm.busobjs.PolicyCycleCode;
import dti.pm.busobjs.PolicyStatus;
import dti.pm.busobjs.QuoteCycleCode;
import dti.pm.busobjs.RecordMode;
import dti.pm.busobjs.TransactionCode;
import dti.pm.busobjs.TransactionTypeCode;
import dti.pm.policyattributesmgr.PmAttributeBean;
import dti.pm.policyattributesmgr.PmAttributeFields;
import dti.pm.policyattributesmgr.PolicyAttributesManager;
import dti.pm.policyattributesmgr.dao.PolicyAttributesDAO;
import dti.pm.policymgr.PolicyHeader;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implement PmAttribute operation with cache.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:   April 19, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 19/04/16 wdang   167534 - Initial version.
 * 07/01/16 tzeng   167531 - Added isPhaseValidToBatch, isSaveOfficialPromptEnableForRenewalBatch,
 *                           isAutoRenewPromptEnableForRenewalBatch
 * 08/26/16 wdang   167534 - Added functions for Renewal Quote.
 * 03/20/16 lzhang  190357 - Added pmAttributeCacheMap: store separate
 *                           pmAttributeCache Map per dbPoolId
 * 03/21/18 tzeng   192015 - Modified isAutoPendingRenewalEnable to make the renewalTransCode condition effective.
 * 04/02/18 tzeng   192229 - Added isAddtlExposureAvailable.
 * ---------------------------------------------------
 */
public class PolicyAttributesManagerImpl implements PolicyAttributesManager, RefreshParmsEventListener {

    private final Logger l = LogUtils.getLogger(getClass());

    private boolean isValid(String effectiveDate, PmAttributeBean bean) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isValid", new Object[]{effectiveDate, bean});
        }

        boolean result = false;
        try {
            result = (DateUtils.daysDiff(bean.getEffectiveFromDate(), effectiveDate) >= 0)
                && (DateUtils.daysDiff(bean.getEffectiveToDate(), effectiveDate) < 0);
        }
        catch (ParseException e1) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse date. ", e1);
            l.throwing(getClass().getName(), "isValid", ae);
            return false;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValid", result);
        }
        return result;
    }

    private boolean contains(String config, String parm) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "contains", new Object[]{config, parm});
        }

        boolean result = (config == null || (parm != null && (","+config+",").contains(","+parm+",")));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isValid", result);
        }
        return result;
    }

    @Override
    public boolean isPhaseValidToBatch(PolicyHeader policyHeader){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isPhaseValidForBatch", new Object[]{policyHeader});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_AUTOREN_BAT_PHASE_CODE);
        boolean result = true;
        boolean hasConfiguration = false;
        try {
            hasConfiguration = list.stream().anyMatch(e -> (
                policyHeader.getTermEffectiveFromDate() != null && isValid(policyHeader.getTermEffectiveFromDate(), e)
            ));

            if (hasConfiguration) {
                result = list.stream().anyMatch(e -> (
                    policyHeader.getTermEffectiveFromDate() != null && isValid(policyHeader.getTermEffectiveFromDate(), e) &&
                    (ALL.equals(e.getValue1()) || contains(e.getValue1(), policyHeader.getPolPhaseCode()))
                ));
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to match PM_AUTOREN_BAT_PHASE_CODE attribute", e);
            l.throwing(getClass().getName(), "isPhaseValidForBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isPhaseValidForBatch", result);
        }
        return result;
    }

    @Override
    public boolean isSaveOfficialPromptEnableForRenewalBatch(PolicyHeader policyHeader){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isSaveOfficialPromptEnableForRenewalBatch", new Object[]{policyHeader});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_RENBATCH_SAVE_OFF_PROMPT);
        boolean result = false;
        try {
            result = list.stream().anyMatch(e -> (
                policyHeader.getTermEffectiveFromDate() != null && isValid(policyHeader.getTermEffectiveFromDate(), e) &&
                (ALL.equals(e.getValue1()) || contains(e.getValue1(), policyHeader.getIssueCompanyEntityId())) &&
                (ALL.equals(e.getValue2()) || contains(e.getValue2(), policyHeader.getIssueStateCode())) &&
                (ALL.equals(e.getValue3()) || contains(e.getValue3(), policyHeader.getPolicyTypeCode())) &&
                (ALL.equals(e.getValue4()) || contains(e.getValue4(), policyHeader.getLastTransactionInfo().getTransactionCode().getName()))
            ));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to match PM_RENBATCH_SAVE_OFF_PROMPT attribute", e);
            l.throwing(getClass().getName(), "isSaveOfficialPromptEnableForRenewalBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isSaveOfficialPromptEnableForRenewalBatch", result);
        }
        return result;
    }

    @Override
    public boolean isAutoRenewPromptEnableForRenewalBatch(PolicyHeader policyHeader){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAutoRenewPromptEnableForRenewalBatch", new Object[]{policyHeader});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_RENBATCH_AUTOREN_PROMPT);
        boolean result = false;
        try {
            result = list.stream().anyMatch(e -> (
                policyHeader.getTermEffectiveFromDate() != null && isValid(policyHeader.getTermEffectiveFromDate(), e) &&
                (ALL.equals(e.getValue1()) || contains(e.getValue1(), policyHeader.getIssueCompanyEntityId())) &&
                (ALL.equals(e.getValue2()) || contains(e.getValue2(), policyHeader.getIssueStateCode())) &&
                (ALL.equals(e.getValue3()) || contains(e.getValue3(), policyHeader.getPolicyTypeCode()))
            ));
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to match PM_RENBATCH_AUTOREN_PROMPT attribute", e);
            l.throwing(getClass().getName(), "isAutoRenewPromptEnableForRenewalBatch", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAutoRenewPromptEnableForRenewalBatch", result);
        }
        return result;
    }

    @Override
    public boolean isCopyDenyAcceptQuoteMenuDisable (String effectiveDate, String policyTypeCode, QuoteCycleCode quoteCycleCode){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCopyDenyAcceptQuoteMenuDisable", new Object[]{effectiveDate, quoteCycleCode});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_DIS_COPY_DENY_ACPT_QTE_MENU);
        boolean result = list.stream().anyMatch(e -> {
            return isValid(effectiveDate, e)
                && contains(e.getValue1(), policyTypeCode)
                && contains(e.getValue2(), quoteCycleCode == null ? null : quoteCycleCode.getName());
        });

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCopyDenyAcceptQuoteMenuDisable", result);
        }
        return result;
    }

    @Override
    public boolean isCopyDenyAcceptQuoteButtonDisable (String effectiveDate, String policyTypeCode, QuoteCycleCode quoteCycleCode){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCopyDenyAcceptQuoteButtonDisable", new Object[]{effectiveDate, quoteCycleCode});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_DIS_COPY_DENY_ACPT_QTE_BTN);
        boolean result = list.stream().anyMatch(e -> {
            return isValid(effectiveDate, e)
                && contains(e.getValue1(), policyTypeCode)
                && contains(e.getValue2(), quoteCycleCode == null ? null : quoteCycleCode.getName());
        });

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCopyDenyAcceptQuoteButtonDisable", result);
        }
        return result;
    }

    @Override
    public boolean isDisplayQuoteInViewModeEnable (String effectiveDate,
                                                   PolicyCycleCode policyCycleCode,
                                                   QuoteCycleCode quoteCycleCode,
                                                   TransactionTypeCode transactionTypeCode,
                                                   RecordMode recordModeCode,
                                                   PolicyStatus policyStatus) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDisplayQuoteInViewModeEnable", new Object[]{
                effectiveDate, policyCycleCode, quoteCycleCode, transactionTypeCode, recordModeCode});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_DIS_QTE_IN_VIEW_MODE_COND);
        boolean result = list.stream().anyMatch(e -> {
            return isValid(effectiveDate, e)
                && contains(e.getValue1(), policyCycleCode == null ? null : policyCycleCode.getName())
                && contains(e.getValue2(), quoteCycleCode == null ? null : quoteCycleCode.getName())
                && contains(e.getValue3(), transactionTypeCode == null ? null : transactionTypeCode.getName())
                && contains(e.getValue4(), recordModeCode == null ? null : recordModeCode.getName())
                && contains(e.getValue5(), policyStatus == null ? null : policyStatus.getName());
        });

        l.exiting(getClass().getName(), "isDisplayQuoteInViewModeEnable", result);
        return result;
    }

    @Override
    public String getDisplayQuoteInViewModeText (String effectiveDate,
                                                 PolicyCycleCode policyCycleCode,
                                                 QuoteCycleCode quoteCycleCode,
                                                 Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDisplayQuoteInViewModeText", new Object[]{
                effectiveDate, policyCycleCode, quoteCycleCode, inputRecord});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_DIS_QTE_IN_VIEW_MODE_RULE);
        Optional<PmAttributeBean> optional = list.stream().filter(e -> {
            return isValid(effectiveDate, e)
                && contains(e.getValue1(), policyCycleCode == null ? null : policyCycleCode.getName())
                && contains(e.getValue2(), quoteCycleCode == null ? null : quoteCycleCode.getName());
        }).findFirst();

        String formattedText = null;
        if (optional.isPresent()) {
            String spEL = optional.get().getValue3();
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext simpleContext = new StandardEvaluationContext(inputRecord);
            Expression ep = parser.parseExpression(spEL);
            formattedText = ep.getValue(simpleContext, String.class);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDisplayQuoteInViewModeText", formattedText);
        }
        return formattedText;
    }

    @Override
    public boolean isAutoPendingRenewalEnable(String effectiveDate,
                                              TransactionCode currentTransCode,
                                              TransactionCode renewalTransCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAutoPendingRenewalEnable", new Object[]{
                effectiveDate, renewalTransCode});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_AUTO_PENDING_RENEWAL);
        boolean result = list.stream().anyMatch(e -> {
            return isValid(effectiveDate, e)
                && (currentTransCode == null || contains(e.getValue1(), currentTransCode.getName()))
                && (renewalTransCode == null || contains(e.getValue2(), renewalTransCode.getName()));
        });

        l.exiting(getClass().getName(), "isAutoPendingRenewalEnable", result);
        return result;
    }

    @Override
    public boolean isAddtlExposureAvailable(String policyType,
                                            String riskType,
                                            String effectiveDate) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAddtlExposureAvailable", new Object[]{
                policyType, riskType, effectiveDate});
        }

        List<PmAttributeBean> list = loadPmAttribute(PmAttributeFields.PM_RISK_EXPOSURE_AVAILABLE);
        boolean result = list.stream().anyMatch(e -> {
            return isValid(effectiveDate, e)
                && (ALL.equals(e.getValue1()) || contains(e.getValue1(), policyType))
                && (ALL.equals(e.getValue2()) || contains(e.getValue2(), riskType));
        });

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAddtlExposureAvailable", result);
        }
        return result;
    }

    @Override
    public void refreshParms(HttpServletRequest request) {
        getPmAttributeCacheMap().clear();
        MessageManager.getInstance().addInfoMessage("core.refresh.policyattributes.cache.parameters");
    }

    public void verifyConfig() {
        if (getPolicyAttributesDAO() == null)
            throw new ConfigurationException("The required property 'pmAttributeDAO' is missing.");
    }

    public PolicyAttributesDAO getPolicyAttributesDAO() {
        return m_policyAttributesDAO;
    }

    public void setPolicyAttributesDAO(PolicyAttributesDAO policyAttributesDAO) {
        m_policyAttributesDAO = policyAttributesDAO;
    }

    @Override
    public List<PmAttributeBean> loadPmAttribute(String typeCode){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPmAttribute", typeCode);
        }

        boolean hasTypeCode = hasTypeCode(typeCode);
        List<PmAttributeBean> list = new ArrayList<>();
        if (!hasTypeCode) {
            Record record = new Record();
            record.setFieldValue(PmAttributeFields.TYPE_CODE, typeCode);
            RecordSet rs = getPolicyAttributesDAO().loadPmAttribute(record);

            RecordBeanMapper recordBeanMapper = new RecordBeanMapper();
            list = Arrays.asList(new PmAttributeBean[rs.getSize()]);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, new PmAttributeBean());
                recordBeanMapper.map(rs.getRecord(i), list.get(i));
            }
            setPmAttributeCache(typeCode, list);
        }

        list = getPmAttributeCache().get(typeCode);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPmAttribute", list);
        }
        return list;
    }

    /**
     * check if the cache contains the typeCode
     */
    protected boolean hasTypeCode(String typeCode){
        return hasPmAttributeCache() ? getPmAttributeCache().containsKey(typeCode) : false;
    }

    /**
     * Sets PmAttributeCache value.
     */
    protected void setPmAttributeCache(String cacheKey, List<PmAttributeBean> PmAttList) {
        if (hasPmAttributeCache()){
            getPmAttributeCache().put(cacheKey, PmAttList);
        }
        else{
            Map<String,List<PmAttributeBean>> pmAttributeCache = new ConcurrentHashMap<>();
            pmAttributeCache.put(cacheKey, PmAttList);
            getPmAttributeCacheMap().put(getCurrentDBPoolID(), pmAttributeCache);
        }
    }
    /**
     * Returns true/false depending on whether PmAttributeCacheMap has currentDBPoolID.
     * @return boolean
     */
    protected boolean hasPmAttributeCache() {
        return getPmAttributeCacheMap().containsKey(getCurrentDBPoolID());
    }

    /**
     * Return the PmAttributeCache map.
     */
    protected Map<String,List<PmAttributeBean>> getPmAttributeCache() {
        return getPmAttributeCacheMap().get(getCurrentDBPoolID());
    }

    /**
     * Return the current DBPoolID.
     */
    protected String getCurrentDBPoolID() {
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        return userSession.get(IOasisAction.KEY_DBPOOLID).toString();
    }

    /**
     * Return the cache map.
     */
    private Map<String, Map<String, List<PmAttributeBean>>> getPmAttributeCacheMap() {
        return m_pmAttributeCacheMap;
    }
    
    private Map<String, Map<String, List<PmAttributeBean>>> m_pmAttributeCacheMap = new ConcurrentHashMap<>();
    private PolicyAttributesDAO m_policyAttributesDAO;

    private static final String ALL = "ALL";
}
