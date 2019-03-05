package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.policy.ContractPeriodType;
import com.delphi_tech.ows.policy.ReferredInsuredType;
import com.delphi_tech.ows.quickquoteservice.*;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.cachemgr.Cache;
import dti.oasis.cachemgr.CacheManager;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.componentmgr.ComponentManager;
import dti.pm.core.request.RequestStorageIds;
import dti.pm.coverageclassmgr.CoverageClassManager;
import dti.pm.coveragemgr.CoverageManager;
import dti.pm.policymgr.PolicyManager;
import dti.pm.policymgr.service.QuickQuoteServiceManager;
import dti.pm.policymgr.service.cache.QuickQuoteServiceDefaultCoverage;
import dti.pm.policymgr.service.cache.QuickQuoteServiceDefaultPolicy;
import dti.pm.policymgr.service.cache.QuickQuoteServiceDefaultRisk;
import dti.pm.policymgr.service.cache.QuickQuoteServiceDefaultsCache;
import dti.pm.policymgr.service.dao.QuickQuoteServiceDAO;
import dti.pm.riskmgr.RiskManager;
import org.apache.commons.lang3.StringUtils;

import javax.xml.namespace.QName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   10/02/18
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/02/2018       athi         194448: Added Quick Quote Web Service.
 * ---------------------------------------------------
 */

public class QuickQuoteServiceManagerImpl implements QuickQuoteServiceManager {
    public final static QName _QuickQuoteRequest_QNAME = new QName("http://www.delphi-tech.com/ows/QuickQuoteService", "QuickQuoteRequest");
    public final static QName _QuickQuoteResult_QNAME = new QName("http://www.delphi-tech.com/ows/QuickQuoteService", "QuickQuoteResult");

    public QuickQuoteServiceManagerImpl() {
    }

    @Override
    public QuickQuoteResultType getQuickQuote(QuickQuoteRequestType quickQuoteRequest) {
        Logger l = LogUtils.getLogger(getClass());
        Date startDate = new Date();

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getQuickQuote", new Object[]{quickQuoteRequest});
            startDate = new Date();
        }

        String xmlRequest = XMLUtils.marshalJaxbToXML(quickQuoteRequest, _QuickQuoteRequest_QNAME);
        OwsLogRequest owsLogRequest = addOwsLogRequest(quickQuoteRequest, xmlRequest);
        QuickQuoteResultType quickQuoteResult = setupQuickQuoteResult(quickQuoteRequest);

        List<ExtendedStatusType> extendedStatusTypes = validateQuickQuoteRequest(quickQuoteRequest, xmlRequest, quickQuoteResult);

        String msgCode = quickQuoteResult.getMessageStatus().getMessageStatusCode();
        if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(msgCode)) {
            l.finer("All validations successful. Starting quick quote processing...");
            if (!quickQuoteResult.getQuickQuoteId().isEmpty()) {
                if (performQuickQuote(quickQuoteResult)) {
                    quickQuoteResult.getMessageStatus().setMessageStatusCode(SUCCESS_MESSAGE_STATUS);
                    owsLogRequest.setMessageStatusCode(quickQuoteResult.getMessageStatus().getMessageStatusCode());
                } else {
                    //MessageManager.getInstance().addErrorMessage("ws.QuickQuote.premium.error");
                    setFailureMsg(quickQuoteResult,
                                  MessageManager.getInstance().formatMessage("ws.QuickQuote.premium.error"),
                                  extendedStatusTypes);
                    addFailureMessages(quickQuoteResult, extendedStatusTypes);
                }
            }
        }

        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(quickQuoteResult, _QuickQuoteResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            //log_xml_to_db("response", xmlResult, msgCode);
            l.logp(Level.FINEST, getClass().getName(), "getQuickQuote", xmlResult);
        } else {
            owsLogRequest.setServiceResult(quickQuoteResult);
            owsLogRequest.setServiceResultQName(_QuickQuoteResult_QNAME);
        }
        owsLogRequest.setRequestName(_QuickQuoteRequest_QNAME.getLocalPart());
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            String MessageId = quickQuoteRequest.getMessageId();
            String CorrelationId = quickQuoteRequest.getCorrelationId();
            Date endDate = new Date();

            l.logp(Level.FINER, getClass().getName(), "getQuickQuote", "***   Quick Quote Service " + MessageId + "-" + CorrelationId + ": start time: " + startDate.toString() + "   ***");
            l.logp(Level.FINER, getClass().getName(), "getQuickQuote", "***   Quick Quote Service " + MessageId + "-" + CorrelationId + ": end time: " + endDate.toString() + "   ***");
            l.logp(Level.FINER, getClass().getName(), "getQuickQuote", "***   Quick Quote Service " + MessageId + "-" + CorrelationId + ": time elapsed: " + (endDate.getTime() - startDate.getTime()) / 1000 + " seconds   ***");

            l.exiting(getClass().getName(), "getQuickQuote", quickQuoteResult);
        }
        return quickQuoteResult;
    }

    private QuickQuoteServiceDefaultsCache getQQDefaultsCache(QuickQuoteRequestType quickQuoteRequest) {
        Logger l = LogUtils.getLogger(getClass());

        QuickQuoteServiceDefaultsCache quickQuoteServiceDefaultsCache = null;
        l.finer("Is quickQuoteServiceDefaultsCache created already?:" + getDefaultsCache().contains(RequestStorageIds.QQ_DEFAULTS_CACHE));
        setDefaultDates(quickQuoteRequest);
        if (!getDefaultsCache().contains(RequestStorageIds.QQ_DEFAULTS_CACHE)) {
            if (l.isLoggable(Level.FINER)) {
                l.finer("Setting Defaults Cache for the first time...");
            }
            quickQuoteServiceDefaultsCache = getDefaults(quickQuoteRequest.getContractPeriod().getStartDate(),
                    quickQuoteRequest.getContractPeriod().getEndDate());
            l.finer("Default Cache : " + quickQuoteServiceDefaultsCache);
            //RequestStorageManager.getInstance().set(RequestStorageIds.QQ_DEFAULTS_CACHE, quickQuoteServiceDefaultsCache);
            getDefaultsCache().put(RequestStorageIds.QQ_DEFAULTS_CACHE, quickQuoteServiceDefaultsCache);
        } else {
            quickQuoteServiceDefaultsCache = (QuickQuoteServiceDefaultsCache) getDefaultsCache().get(RequestStorageIds.QQ_DEFAULTS_CACHE);
            l.finer("Using Existing Default Cache : " + quickQuoteServiceDefaultsCache);
        }
        return quickQuoteServiceDefaultsCache;
    }

    private List<ExtendedStatusType> validateQuickQuoteRequest(QuickQuoteRequestType quickQuoteRequest, String xmlRequest,
                                                               QuickQuoteResultType quickQuoteResult) {
        Logger l = LogUtils.getLogger(getClass());
        List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateQuickQuoteRequest", new Object[]{});
        }

        validateXmlRequest(xmlRequest, quickQuoteRequest, quickQuoteResult, extendedStatusTypes);

        //check for validity of xml tags
        String msgCode = quickQuoteResult.getMessageStatus().getMessageStatusCode();
        if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(msgCode)) {
            l.finer("Xml tags validation succeeded.");
            setOptionalData(quickQuoteRequest, quickQuoteResult, getQQDefaultsCache(quickQuoteRequest));
            //need to regenerate xml since request object was updated with default and optional data.
            xmlRequest = XMLUtils.marshalJaxbToXML(quickQuoteRequest, _QuickQuoteRequest_QNAME);

            //check for validity of xml data passed in the tags
            validateDbData(quickQuoteRequest, quickQuoteResult, extendedStatusTypes);
            msgCode = quickQuoteResult.getMessageStatus().getMessageStatusCode();
            if (MessageStatusHelper.STATUS_CODE_SUCCESS.equals(msgCode)) {
                l.finer("Xml data validation succeeded.");
                RecordSet rs = insertRequestToDb(xmlRequest);
                quickQuoteResult.setQuickQuoteId(rs.getSummaryRecord().getStringValue("quoteId"));
                quickQuoteResult.setQuickQuoteGenerationDate(rs.getSummaryRecord().getStringValue("quoteDate"));
            } else
                addFailureMessages(quickQuoteResult, extendedStatusTypes);
        } else
            addFailureMessages(quickQuoteResult, extendedStatusTypes);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateQuickQuoteRequest", new Object[]{msgCode});
        }
        return extendedStatusTypes;
    }

    private void addFailureMessages(QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        if (extendedStatusTypes.size() > 0) {
            quickQuoteResult.getMessageStatus().setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
        }
        quickQuoteResult.getMessageStatus().getExtendedStatus().addAll(extendedStatusTypes);
    }

    private OwsLogRequest addOwsLogRequest(QuickQuoteRequestType quickQuoteRequest, String xmlRequest) {
        Logger l = LogUtils.getLogger(getClass());
        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINEST)) {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlRequest,
                    quickQuoteRequest.getMessageId(), quickQuoteRequest.getCorrelationId(), quickQuoteRequest.getUserId(), _QuickQuoteRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "addOwsLogRequest", xmlRequest);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(quickQuoteRequest, _QuickQuoteRequest_QNAME,
                    quickQuoteRequest.getMessageId(), quickQuoteRequest.getCorrelationId(), quickQuoteRequest.getUserId());
        }
        return owsLogRequest;
    }

    private QuickQuoteResultType setupQuickQuoteResult(QuickQuoteRequestType quickQuoteRequest) {
        QuickQuoteResultType quickQuoteResult = new QuickQuoteResultType();
        quickQuoteResult.setMessageId(quickQuoteRequest.getMessageId());
        quickQuoteResult.setCorrelationId(quickQuoteRequest.getCorrelationId());

        quickQuoteResult.setContractPeriod(quickQuoteRequest.getContractPeriod());
        quickQuoteResult.setPolicyTypeCode(quickQuoteRequest.getPolicyTypeCode());

        quickQuoteResult.getInsured().addAll(quickQuoteRequest.getInsured());
        quickQuoteResult.getMedicalMalpracticeCoverage().addAll(quickQuoteRequest.getMedicalMalpracticeCoverage());
        quickQuoteResult.getCreditSurchargeDeductible().addAll(quickQuoteRequest.getCreditSurchargeDeductible());

        MessageStatusType messageStatusType = new MessageStatusType();
        quickQuoteResult.setMessageStatus(messageStatusType);

        return quickQuoteResult;
    }

    private void setDefaultDates(QuickQuoteRequestType quickQuoteRequest) {
        ContractPeriodType cp = quickQuoteRequest.getContractPeriod();
        if (cp != null && (!cp.getStartDate().isEmpty() || !cp.getStartDate().contains("?"))
                && (!cp.getEndDate().isEmpty() || !cp.getEndDate().contains("?"))) {
            setDefaultDates4CP(quickQuoteRequest.getContractPeriod());
        } else {
            ContractPeriodType cpt = new ContractPeriodType();
            setDefaultDates4CP(cpt);
            quickQuoteRequest.setContractPeriod(cpt);
        }
    }

    private void setDefaultDates4CP(ContractPeriodType cpt) {
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        cpt.setStartDate(startDate);
        cpt.setEndDate(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("MM/dd/yyyy")).plusYears(1)
                .format(DateTimeFormatter.BASIC_ISO_DATE.ofPattern("MM/dd/yyyy")).toString());
    }

    private void setOptionalData(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, QuickQuoteServiceDefaultsCache quickQuoteServiceDefaultsCache) {
        //policy
        if (quickQuoteRequest.getPolicyTypeCode() != null && quickQuoteRequest.getPolicyTypeCode().isEmpty()) {
            quickQuoteRequest.setPolicyTypeCode(quickQuoteServiceDefaultsCache.getPolicyType().getValue());
            quickQuoteResult.setPolicyTypeCode(quickQuoteServiceDefaultsCache.getPolicyType().getValue());
        }

        //risk
        List<InsuredType> insuredList = quickQuoteRequest.getInsured();
        for (InsuredType insured : insuredList) {
            if (insured.getInsuredTypeCode() != null) {
                List<QuickQuoteServiceDefaultRisk> risks = quickQuoteServiceDefaultsCache.getRiskType();
                for (QuickQuoteServiceDefaultRisk risk : risks) {
                    if (risk.getPolicyType().equalsIgnoreCase(quickQuoteRequest.getPolicyTypeCode())) {
                        if (insured.getInsuredTypeCode().isEmpty())
                            insured.setInsuredTypeCode(risk.getValue());
                        //coverage
                        List<MedicalMalpracticeCoverageType> covgList = quickQuoteRequest.getMedicalMalpracticeCoverage();
                        for (MedicalMalpracticeCoverageType covg : covgList) {
                            if (covg.getReferredInsured() == null || covg.getReferredInsured().getInsuredReference() == null
                                    || covg.getReferredInsured().getInsuredReference().isEmpty()) {
                                if (insuredList.size() == 1) {
                                    ReferredInsuredType insuredType = new ReferredInsuredType();
                                    insuredType.setInsuredReference(insured.getKey());
                                    covg.setReferredInsured(insuredType);
                                }
                            }
                            if (covg.getMedicalMalpracticeCoverageCode() != null && covg.getMedicalMalpracticeCoverageCode().isEmpty()) {
                                List<QuickQuoteServiceDefaultCoverage> coverages = quickQuoteServiceDefaultsCache.getCovgType();
                                for (QuickQuoteServiceDefaultCoverage coverage : coverages) {
                                    if (coverage.getPolicyType().equalsIgnoreCase(quickQuoteRequest.getPolicyTypeCode()) &&
                                            coverage.getRiskType().equalsIgnoreCase(risk.getValue())) {
                                        covg.setMedicalMalpracticeCoverageCode(coverage.getValue());
                                    }
                                }
                            }
                            //components
                            List<CreditSurchargeDeductibleType> compList = quickQuoteRequest.getCreditSurchargeDeductible();
                            for (CreditSurchargeDeductibleType comp : compList) {
                                if (comp.getReferredMedicalMalpracticeCoverage() == null ||
                                        comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference() == null ||
                                        comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().isEmpty()) {
                                    if (covgList.size() == 1) {
                                        ReferredMedicalMalpracticeCoverageType covgType = new ReferredMedicalMalpracticeCoverageType();
                                        covgType.setMedicalMalpracticeCoverageReference(covg.getKey());
                                        comp.setReferredMedicalMalpracticeCoverage(covgType);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private QuickQuoteServiceDefaultsCache getDefaults(String startDate, String endDate) {
        return getDefaultsCache(startDate, endDate);
    }

    private boolean performQuickQuote(QuickQuoteResultType quickQuoteResult) {
        String qqResult = getQuickQuoteServiceDAO().performQuickQuote(quickQuoteResult.getQuickQuoteId());
        if (qqResult.equalsIgnoreCase("0")) {
            getPremiumInfoForCoverages(quickQuoteResult);
            getPremiumInfoForComponents(quickQuoteResult);
            removeQuickQuoteRequestData(quickQuoteResult.getQuickQuoteId());
            return true;
        }
        return false;
    }

    private void getPremiumInfoForCoverages(QuickQuoteResultType quickQuoteResult) {
        RecordSet rs = getPremium(quickQuoteResult.getQuickQuoteId(), "coverage");
        if (rs.getSize() >= 1) {
            quickQuoteResult.getMedicalMalpracticeCoverage().clear();
            Iterator it = rs.getRecords();
            while (it.hasNext()) {
                Record record = (Record) it.next();

                MedicalMalpracticeCoverageType dbCovg = new MedicalMalpracticeCoverageType();
                dbCovg.setKey(record.getStringValue("coverageKey"));
                ReferredInsuredType referredInsured = new ReferredInsuredType();
                referredInsured.setInsuredReference(record.getStringValue("riskKey"));
                dbCovg.setReferredInsured(referredInsured);
                dbCovg.setPolicyFormCode(record.getStringValue("policyFormCode"));
                dbCovg.setMedicalMalpracticeCoverageCode(record.getStringValue("productCoverageCode"));
                ClaimMadeLiabilityPolicyInformationType clpit = new ClaimMadeLiabilityPolicyInformationType();
                clpit.setCurrentRetroactiveDate(record.getStringValue("retroactiveDate"));
                dbCovg.setClaimMadeLiabilityPolicyInformation(clpit);
                LimitType limit = new LimitType();
                limit.setLimitTypeCode(record.getStringValue("coverageLimitCode"));
                dbCovg.setLimit(limit);
                PremiumType premium = new PremiumType();
                premium.setCurrentClaimsMadeStep(record.getStringValue("currentCmStep", ""));
                premium.setGrossAmount(record.getStringValue("premiumAmount", ""));
                premium.setNetAmount(record.getStringValue("netPremiumAmount", ""));
                PremiumInformationType premiumInformation = new PremiumInformationType();
                premiumInformation.setPremium(premium);
                dbCovg.setPremiumInformation(premiumInformation);

                quickQuoteResult.getMedicalMalpracticeCoverage().add(dbCovg);
            }
        }
    }

    private void getPremiumInfoForComponents(QuickQuoteResultType quickQuoteResult) {
        RecordSet rs = getPremium(quickQuoteResult.getQuickQuoteId(), "component");
        if (rs.getSize() >= 1) {
            quickQuoteResult.getCreditSurchargeDeductible().clear();
            Iterator it = rs.getRecords();
            while (it.hasNext()) {
                Record record = (Record) it.next();

                CreditSurchargeDeductibleType dbComp = new CreditSurchargeDeductibleType();
                ReferredMedicalMalpracticeCoverageType covgRef = new ReferredMedicalMalpracticeCoverageType();
                covgRef.setMedicalMalpracticeCoverageReference(record.getStringValue("coverageKey"));
                dbComp.setReferredMedicalMalpracticeCoverage(covgRef);
                dbComp.setCreditSurchargeDeductibleCode(record.getStringValue("coverageComponentCode"));
                dbComp.setNumericValue(record.getStringValue("componentValue"));
                PremiumType premium = new PremiumType();
                premium.setCurrentClaimsMadeStep(record.getStringValue("currentCmStep", ""));
                premium.setGrossAmount(record.getStringValue("premiumAmount", ""));
                PremiumInformationType premiumInformation = new PremiumInformationType();
                premiumInformation.setPremium(premium);
                dbComp.setPremiumInformation(premiumInformation);

                quickQuoteResult.getCreditSurchargeDeductible().add(dbComp);
            }
        }
    }

    private void validateDbData(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateDbData", new Object[]{});
        }

        //validate risk_type_code
        List<InsuredType> insuredList = quickQuoteRequest.getInsured();
        for (InsuredType insured : insuredList) {
            //validate risk
            if (validateRiskType(quickQuoteRequest.getPolicyTypeCode(), insured.getInsuredTypeCode()).equalsIgnoreCase("N")) {
                setFailureMsg(quickQuoteResult,
                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.policytype.risktype.error",
                                                    new Object[]{quickQuoteRequest.getPolicyTypeCode(), insured.getInsuredTypeCode()}),
                        extendedStatusTypes);
            }
            if (!insured.getPostalCode().isEmpty()) {
                if (validatePostalCode(insured.getPostalCode()).equalsIgnoreCase("N")) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.postalcode.error",
                                    new Object[]{insured.getPostalCode()}), extendedStatusTypes);
                }
                if (validatePostalState(insured.getPostalCode(), insured.getPracticeStateOrProvinceCode()).equalsIgnoreCase("N")) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.postalcode.statecode.error",
                                    new Object[]{insured.getPostalCode(), insured.getPracticeStateOrProvinceCode()}),
                            extendedStatusTypes);
                }
                if (validateStateCounty(insured.getPracticeStateOrProvinceCode(), insured.getPracticeCountyCode(), insured.getPostalCode(),
                        quickQuoteRequest.getContractPeriod().getStartDate()).equalsIgnoreCase("N")) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.statecode.countycode.error",
                                    new Object[]{insured.getPracticeStateOrProvinceCode(), insured.getPracticeCountyCode()}),
                            extendedStatusTypes);
                }
            }
            if (validateInsuredClass(insured.getInsuredTypeCode(), insured.getInsuredClassCode()).equalsIgnoreCase("N")) {
                setFailureMsg(quickQuoteResult,
                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.risktype.riskclass.error",
                                new Object[]{insured.getInsuredTypeCode(), insured.getInsuredClassCode()}),
                        extendedStatusTypes);
            }
            //validate coverage
            List<MedicalMalpracticeCoverageType> covgList = quickQuoteRequest.getMedicalMalpracticeCoverage();
            for (MedicalMalpracticeCoverageType covg : covgList) {
                if (covg.getReferredInsured().getInsuredReference().equalsIgnoreCase(insured.getKey())) {
                    if (validateCoverageType(quickQuoteRequest.getPolicyTypeCode(), insured.getInsuredTypeCode(), covg.getMedicalMalpracticeCoverageCode()).equalsIgnoreCase("N")) {
                        setFailureMsg(quickQuoteResult,
                                MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.risk.policy.error",
                                        new Object[]{covg.getMedicalMalpracticeCoverageCode(),
                                                quickQuoteRequest.getPolicyTypeCode(), insured.getInsuredTypeCode()}),
                                extendedStatusTypes);
                    }
                    if (validateIndividualCoverage(covg.getMedicalMalpracticeCoverageCode()).equalsIgnoreCase("N")) {
                        setFailureMsg(quickQuoteResult,
                                MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.individual.covg.error",
                                        new Object[]{covg.getMedicalMalpracticeCoverageCode()}),
                                extendedStatusTypes);
                    }
                    if (validateCoverageForPolicyRiskType(quickQuoteRequest.getPolicyTypeCode(),
                            insured.getInsuredTypeCode(),
                            covg.getMedicalMalpracticeCoverageCode()).equalsIgnoreCase("N")) {
                        setFailureMsg(quickQuoteResult,
                                MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.policytype.risktype.error",
                                        new Object[]{covg.getMedicalMalpracticeCoverageCode(),
                                                quickQuoteRequest.getPolicyTypeCode(), insured.getInsuredTypeCode()}),
                                extendedStatusTypes);
                    }
                    if (validateLimitForCoverage(covg.getMedicalMalpracticeCoverageCode(),
                                                 covg.getLimit().getLimitTypeCode(),
                                                 quickQuoteRequest.getContractPeriod().getStartDate())
                                                    .equalsIgnoreCase("N")) {
                        setFailureMsg(quickQuoteResult,
                                MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.limittype.error",
                                        new Object[]{covg.getMedicalMalpracticeCoverageCode(), covg.getLimit().getLimitTypeCode()}),
                                extendedStatusTypes);
                    }
                    //validate component
                    List<CreditSurchargeDeductibleType> compList = quickQuoteRequest.getCreditSurchargeDeductible();
                    for (CreditSurchargeDeductibleType comp : compList) {
                        if (comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference()
                                .equalsIgnoreCase(covg.getKey())) {
                            if (validateComponentForCoverage(comp.getCreditSurchargeDeductibleCode(),
                                    covg.getMedicalMalpracticeCoverageCode(), quickQuoteRequest.getContractPeriod().getStartDate())
                                    .equalsIgnoreCase("N")) {
                                setFailureMsg(quickQuoteResult,
                                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.comp.covg.error",
                                                new Object[]{comp.getCreditSurchargeDeductibleCode(), covg.getMedicalMalpracticeCoverageCode()}),
                                        extendedStatusTypes);
                            }
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateDbData", new Object[]{});
        }
    }

    private String validateComponentForCoverage(String componentCode, String coverageCode, String effDate) {
        return getQuickQuoteServiceDAO().validateComponentForCoverage(componentCode, coverageCode, effDate);
    }

    private String validateLimitForCoverage(String coverageCode, String limitTypeCode, String effDate) {
        return getQuickQuoteServiceDAO().validateLimitForCoverage(coverageCode, limitTypeCode, effDate);
    }

    private String validateCoverageForPolicyRiskType(String policyTypeCode, String insuredTypeCode, String coverageCode) {
        return getQuickQuoteServiceDAO().validateCoverageForPolicyRiskType(policyTypeCode, insuredTypeCode, coverageCode);
    }

    private String validateInsuredClass(String insuredTypeCode, String insuredClassCode) {
        return getQuickQuoteServiceDAO().validateInsuredClass(insuredTypeCode, insuredClassCode);
    }

    private String validatePostalState(String postalCode, String stateCode) {
        return getQuickQuoteServiceDAO().validatePostalState(postalCode, stateCode);
    }

    private String validateStateCounty(String stateCode, String countyCode, String postalCode, String effDate) {
        return getQuickQuoteServiceDAO().validateStateCounty(stateCode, countyCode, postalCode, effDate);
    }

    @Override
    public RecordSet insertRequestToDb(String xml) {
        return getQuickQuoteServiceDAO().insertRequestToDb(xml);
    }

    @Override
    public String validateRiskType(String policyTypeCode, String riskTypeCode) {
        return getQuickQuoteServiceDAO().validateRiskType(policyTypeCode, riskTypeCode);
    }

    @Override
    public String validateCoverageType(String policyTypeCode, String riskTypeCode, String coverageCode) {
        return getQuickQuoteServiceDAO().validateCoverageType(policyTypeCode, riskTypeCode, coverageCode);
    }

    @Override
    public String validateIndividualCoverage(String coverageCode) {
        return getQuickQuoteServiceDAO().validateIndividualCoverage(coverageCode);
    }

    @Override
    public String validatePostalCode(String postalCode) {
        return getQuickQuoteServiceDAO().validatePostalCode(postalCode);
    }

    @Override
    public String performQuickQuote(String quoteId) {
        return getQuickQuoteServiceDAO().performQuickQuote(quoteId);
    }

    @Override
    public RecordSet getPremium(String quoteId, String recordType) {
        return getQuickQuoteServiceDAO().getPremium(quoteId, recordType);
    }

    @Override
    public void removeQuickQuoteRequestData(String quickQuoteId) {
        getQuickQuoteServiceDAO().removeQuickQuoteRequestData(quickQuoteId);
    }

    @Override
    public void logXml(String logType, String xml, String msgCode) {
        getQuickQuoteServiceDAO().logXml(logType, xml, msgCode);
    }

    @Override
    public QuickQuoteServiceDefaultsCache getDefaultsCache(String startDate, String endDate) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultsCache", new Object[]{});
        }

        QuickQuoteServiceDefaultsCache quickQuoteServiceDefaultsCache = null;
        RecordSet rs = null;
        Record record = null;

        rs = getQuickQuoteServiceDAO().getCacheDefaults();
        if (rs.getSize() > 0) {
            //create QuickQuoteServiceDefaultsCache for policy, risk and coverage.
            quickQuoteServiceDefaultsCache = new QuickQuoteServiceDefaultsCache();
            // Iterate through each cache record
            Iterator iter = rs.getRecords();

            List<QuickQuoteServiceDefaultRisk> riskList = new ArrayList<QuickQuoteServiceDefaultRisk>();
            List<QuickQuoteServiceDefaultCoverage> covgList = new ArrayList<QuickQuoteServiceDefaultCoverage>();
            while (iter.hasNext()) {
                record = (Record) iter.next();

                String level = record.getStringValue("defaultValueLevel");
                String fromDate = record.getStringValue("termEffectiveFromDate");
                String toDate = record.getStringValue("termEffectiveToDate");
                String defaultValue = record.getStringValue("defaultValue");

                if (level.equalsIgnoreCase("OWS_QQ_POLICY_TYPE")) {
                    if (isLevelConfigValid(fromDate, toDate, startDate, endDate)) {
                        l.finer("Adding default for policy : " + defaultValue);
                        quickQuoteServiceDefaultsCache.setPolicyType(new QuickQuoteServiceDefaultPolicy(level, fromDate, toDate, defaultValue));
                    }
                }
                if (level.equalsIgnoreCase("OWS_QQ_RISK_TYPE")) {
                    String policyType = record.getStringValue("VALUE1");
                    if (isLevelConfigValid(fromDate, toDate, startDate, endDate)) {
                        l.finer("Adding default for Risk : " + defaultValue);
                        riskList.add(new QuickQuoteServiceDefaultRisk(level, fromDate, toDate, policyType, defaultValue));
                    }
                }
                if (level.equalsIgnoreCase("OWS_QQ_PRODUCT_COVERAGE")) {
                    String policyType = record.getStringValue("VALUE1");
                    String riskType = record.getStringValue("VALUE2");
                    String state = record.getStringValue("VALUE3");
                    if (isLevelConfigValid(fromDate, toDate, startDate, endDate)) {
                        l.finer("Adding default for coverage : " + defaultValue);
                        covgList.add(new QuickQuoteServiceDefaultCoverage(level, fromDate, toDate, policyType, riskType, state, defaultValue));
                    }
                }
            }
            quickQuoteServiceDefaultsCache.setRiskType(riskList);
            quickQuoteServiceDefaultsCache.setCovgType(covgList);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultsCache", quickQuoteServiceDefaultsCache);
        }

        return quickQuoteServiceDefaultsCache;
    }

    private boolean isLevelConfigValid(String fromDate, String toDate, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date fDate = sdf.parse(fromDate);
            Date tDate = sdf.parse(toDate);
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);

            if ((sDate.before(fDate) || sDate.after(tDate)) && (eDate.before(fDate) || eDate.after(tDate))) {
                return false;
            }
            if (sDate.equals(fDate) || sDate.equals(tDate) || eDate.equals(fDate) || eDate.equals(tDate)) {
                return false;
            }
            return true;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void validateXmlRequest(String xmlRequest, QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateXmlRequest", new Object[]{});
        }

        if (xmlRequest.contains(">?</")) {
            setFailureMsg(quickQuoteResult,  MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.malformed.request.error"), extendedStatusTypes);
        }
        else {
            //validatePolicy(quickQuoteRequest, quickQuoteResult, extendedStatusTypes);
            validateInsured(quickQuoteRequest, quickQuoteResult, extendedStatusTypes);
            validateCoverage(quickQuoteRequest, quickQuoteResult, extendedStatusTypes);
            validateComponent(quickQuoteRequest, quickQuoteResult, extendedStatusTypes);
            if (quickQuoteResult.getMessageStatus().getMessageStatusCode() == null)
                quickQuoteResult.getMessageStatus().setMessageStatusCode(SUCCESS_MESSAGE_STATUS);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateXmlRequest", new Object[]{});
        }
    }

    /*private void validatePolicy(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        if (quickQuoteRequest.getContractPeriod().getStartDate().contains("?") ||
                quickQuoteRequest.getContractPeriod().getStartDate().isEmpty())
            setFailureMsg(quickQuoteResult, "Contract Start Date is required.", extendedStatusTypes);
        if (quickQuoteRequest.getContractPeriod().getEndDate().contains("?") ||
                quickQuoteRequest.getContractPeriod().getEndDate().isEmpty())
            setFailureMsg(quickQuoteResult, "Contract End Date is required.", extendedStatusTypes);
        if (quickQuoteRequest.getPolicyTypeCode().contains("?") ||
                quickQuoteRequest.getPolicyTypeCode().isEmpty())
            setFailureMsg(quickQuoteResult, "Policy Type Code is required.", extendedStatusTypes);
    }*/

    private void validateInsured(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        List<InsuredType> insuredList = quickQuoteRequest.getInsured();
        for (InsuredType insured : insuredList) {
            if (insuredList.size() > 1) {
                if (insured.getKey() == null ||
                        insured.getKey().contains("?") || insured.getKey().isEmpty()) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.risk.key.required.error"),
                            extendedStatusTypes);
                }
            } else {
                if ((insured.getPracticeStateOrProvinceCode() == null ||
                        insured.getPracticeStateOrProvinceCode().contains("?") ||
                        insured.getPracticeStateOrProvinceCode().isEmpty())
                        && (insured.getPracticeCountyCode() == null ||
                            insured.getPracticeCountyCode().contains("?") ||
                        insured.getPracticeCountyCode().isEmpty())) {
                    if (insured.getPostalCode() == null ||
                            insured.getPostalCode().contains("?") || insured.getPostalCode().isEmpty()) {
                        setFailureMsg(quickQuoteResult,
                                MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.postalcodeorstatecounty.required.error",
                                        new Object[]{insured.getKey()}),
                                extendedStatusTypes);
                    }
                }
                //mandatory fields
                if (insured.getInsuredClassCode() == null ||
                        insured.getInsuredClassCode().contains("?") || insured.getInsuredClassCode().isEmpty()) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.risk.class.required.error",
                                    new Object[]{insured.getKey()}),
                            extendedStatusTypes);
                }

                if (insuredList.size() == 1 && insured.getKey().isEmpty()) {
                    String insrKey = "Insured1";
                    insured.setKey(insrKey);
                    List<MedicalMalpracticeCoverageType> covgList = quickQuoteRequest.getMedicalMalpracticeCoverage();
                    for (MedicalMalpracticeCoverageType covg : covgList) {
                        covg.getReferredInsured().setInsuredReference(insrKey);
                    }
                }
                if (!StringUtils.isEmpty(insured.getPostalCode()) &&
                        StringUtils.isEmpty(insured.getPracticeStateOrProvinceCode()) &&
                        StringUtils.isEmpty(insured.getPracticeCountyCode())) {
                    RecordSet rs = getQuickQuoteServiceDAO().getDataForPostalCode(insured.getPostalCode());
                    insured.setPracticeStateOrProvinceCode(rs.getSummaryRecord().getStringValue("stateCode"));
                    insured.setPracticeCountyCode(rs.getSummaryRecord().getStringValue("countyCode"));
                }
                else if (!StringUtils.isEmpty(insured.getPostalCode()) && StringUtils.isEmpty(insured.getPracticeStateOrProvinceCode())) {
                    RecordSet rs = getQuickQuoteServiceDAO().getDataForPostalCode(insured.getPostalCode());
                    insured.setPracticeStateOrProvinceCode(rs.getSummaryRecord().getStringValue("stateCode"));
                }
                else if (StringUtils.isEmpty(insured.getPracticeCountyCode()) &&
                        !StringUtils.isEmpty(insured.getPostalCode()) &&
                        !StringUtils.isEmpty(insured.getPracticeStateOrProvinceCode())) {
                    insured.setPracticeCountyCode(getQuickQuoteServiceDAO().getDataForPostalAndState(insured.getPostalCode(), insured.getPracticeStateOrProvinceCode()));
                }
                else if (!StringUtils.isEmpty(insured.getPracticeCountyCode()) &&
                        !StringUtils.isEmpty(insured.getPostalCode()) &&
                        StringUtils.isEmpty(insured.getPracticeStateOrProvinceCode())) {
                    insured.setPracticeStateOrProvinceCode(getQuickQuoteServiceDAO().getDataForPostalAndCounty(insured.getPostalCode(), insured.getPracticeCountyCode()));
                }
            }
        }
        if (insuredList.size() == 0) {
            setFailureMsg(quickQuoteResult, MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.one.risk.required.error"), extendedStatusTypes);
        }
    }

    private void validateCoverage(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        List<MedicalMalpracticeCoverageType> covgList = quickQuoteRequest.getMedicalMalpracticeCoverage();
        for (MedicalMalpracticeCoverageType covg : covgList) {
            if (covgList.size() > 1) {
                if (covg.getKey() == null || covg.getKey().contains("?") || covg.getKey().isEmpty()) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.key.required.error"),
                            extendedStatusTypes);
                }
                if (covg.getReferredInsured() == null || covg.getReferredInsured().getInsuredReference() == null ||
                        covg.getReferredInsured().getInsuredReference().contains("?") ||
                        covg.getReferredInsured().getInsuredReference().isEmpty()) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.referredinsured.required.error",
                                    new Object[]{covg.getKey()}),
                            extendedStatusTypes);
                }
            }
            //mandatory fields
            if ((covg.getPolicyFormCode() == null ||
                    covg.getPolicyFormCode().contains("?") || covg.getPolicyFormCode().isEmpty()) &&
                    (covg.getMedicalMalpracticeCoverageCode() == null ||
                            covg.getMedicalMalpracticeCoverageCode().contains("?") ||
                            covg.getMedicalMalpracticeCoverageCode().isEmpty())) {
                setFailureMsg(quickQuoteResult,
                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covgcode.formcode.required.error",
                                new Object[]{covg.getKey()}),
                        extendedStatusTypes);
            }
            if (covg.getLimit() == null || covg.getLimit().getLimitTypeCode() == null ||
                    covg.getLimit().getLimitTypeCode().contains("?") || covg.getLimit().getLimitTypeCode().isEmpty()) {
                setFailureMsg(quickQuoteResult,
                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.covg.limit.required.error",
                                new Object[]{covg.getKey()}),
                        extendedStatusTypes);
            }
            if (covgList.size() == 1 && covg.getKey().isEmpty()) {
                String covgKey = "Coverage1";
                covg.setKey(covgKey);
                List<CreditSurchargeDeductibleType> compList = quickQuoteRequest.getCreditSurchargeDeductible();
                for (CreditSurchargeDeductibleType comp : compList) {
                    comp.getReferredMedicalMalpracticeCoverage().setMedicalMalpracticeCoverageReference(covgKey);
                }
            }
        }
        if (covgList.size() == 0) {
            setFailureMsg(quickQuoteResult,
                    MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.one.covg.required.error"),
                    extendedStatusTypes);
        }
    }

    private void validateComponent(QuickQuoteRequestType quickQuoteRequest, QuickQuoteResultType quickQuoteResult, List<ExtendedStatusType> extendedStatusTypes) {
        List<CreditSurchargeDeductibleType> compList = quickQuoteRequest.getCreditSurchargeDeductible();
        for (CreditSurchargeDeductibleType comp : compList) {
            if (comp.getCreditSurchargeDeductibleCode() == null ||
                    comp.getCreditSurchargeDeductibleCode().contains("?") ||
                    comp.getCreditSurchargeDeductibleCode().isEmpty()) {
                setFailureMsg(quickQuoteResult,
                        MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.comp.required.error"),
                        extendedStatusTypes);
            }
            if (compList.size() > 1) {
                if (comp.getReferredMedicalMalpracticeCoverage() == null ||
                        comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference() == null ||
                        comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().contains("?") ||
                        comp.getReferredMedicalMalpracticeCoverage().getMedicalMalpracticeCoverageReference().isEmpty()) {
                    setFailureMsg(quickQuoteResult,
                            MessageManager.getInstance().formatMessage("ws.QuickQuote.validation.comp.covgkey.required.error",
                                    new Object[]{comp.getCreditSurchargeDeductibleCode()}),
                            extendedStatusTypes);
                }
            }
        }
    }

    private void setFailureMsg(QuickQuoteResultType quickQuoteResult, String msg, List<ExtendedStatusType> extendedStatusTypes) {
        ExtendedStatusType extendedStatusType = new ExtendedStatusType();
        extendedStatusType.setExtendedStatusDescription(msg);
        extendedStatusType.setExtendedStatusCode(FAILURE_MESSAGE_STATUS);
        extendedStatusTypes.add(extendedStatusType);
        quickQuoteResult.getMessageStatus().setMessageStatusCode(FAILURE_MESSAGE_STATUS);
    }

    public QuickQuoteServiceDAO getQuickQuoteServiceDAO() {
        return quickQuoteServiceDAO;
    }

    public void setQuickQuoteServiceDAO(QuickQuoteServiceDAO quickQuoteServiceDAO) {
        this.quickQuoteServiceDAO = quickQuoteServiceDAO;
    }

    public PolicyManager getPolicyManager() {
        return m_policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        m_policyManager = policyManager;
    }

    public RiskManager getRiskManager() {
        return m_riskManager;
    }

    public void setRiskManager(RiskManager riskManager) {
        m_riskManager = riskManager;
    }

    public CoverageManager getCoverageManager() {
        return m_coverageManager;
    }

    public void setCoverageManager(CoverageManager coverageManager) {
        m_coverageManager = coverageManager;
    }

    public CoverageClassManager getCoverageClassManager() {
        return m_coverageClassManager;
    }

    public void setCoverageClassManager(CoverageClassManager coverageClassManager) {
        m_coverageClassManager = coverageClassManager;
    }

    public ComponentManager getComponentManager() {
        return m_componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        m_componentManager = componentManager;
    }

    public void clearDefaultsCache() {
        getDefaultsCache().clear();
    }

    public Cache getDefaultsCache() {
        if (m_defaultsCache == null) {
            // Allow this property to not be configured through Spring
            m_defaultsCache = CacheManager.getInstance().getCache("dti.policy.service.impl.QuickQuoteServiceManagerImpl.defaultsCache");
        }
        return m_defaultsCache;
    }

    public void setDefaultsCache(Cache m_defaultsCache) {
        this.m_defaultsCache = m_defaultsCache;
    }

    public RequestStorageManager getRequestStorageManager() {
        return m_requestStorageManager;
    }

    public void setRequestStorageManager(RequestStorageManager requestStorageManager) {
        m_requestStorageManager = requestStorageManager;
    }

    private Cache m_defaultsCache;
    private PolicyManager m_policyManager;
    private RiskManager m_riskManager;
    private CoverageManager m_coverageManager;
    private CoverageClassManager m_coverageClassManager;
    private ComponentManager m_componentManager;
    private RequestStorageManager m_requestStorageManager;
    private QuickQuoteServiceDAO quickQuoteServiceDAO;

    Logger l = LogUtils.getLogger(getClass());
    private final String SUCCESS_MESSAGE_STATUS = "Success";
    private final String FAILURE_MESSAGE_STATUS = "Failure";
    private final String REJECTED_MESSAGE_STATUS = "Rejected";
}
