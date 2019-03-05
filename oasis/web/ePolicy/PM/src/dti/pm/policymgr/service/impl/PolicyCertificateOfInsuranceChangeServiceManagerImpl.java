package dti.pm.policymgr.service.impl;

import com.delphi_tech.ows.policycertificateofinsurancechangeservice.CertificateHolderType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeRequestType;
import com.delphi_tech.ows.policycertificateofinsurancechangeservice.MedicalMalpracticeCertificateOfLiabilityChangeResultType;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.XMLUtils;
import dti.ows.common.MessageStatusHelper;
import dti.pm.policymgr.service.PolicyCertificateOfInsuranceChangeFields;
import dti.pm.policymgr.service.PolicyCertificateOfInsuranceChangeServiceManager;
import dti.pm.riskmgr.coimgr.CoiManager;

import javax.xml.namespace.QName;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * User: wrong
 * Date: 09/08/2017
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/08/2017       wrong       187839 - Created for COI change service.
 * ---------------------------------------------------
 */
public class PolicyCertificateOfInsuranceChangeServiceManagerImpl implements PolicyCertificateOfInsuranceChangeServiceManager {
    private final Logger l = LogUtils.getLogger(getClass());

    public final static QName _MedicalMalpracticeCertificateOfLiabilityChangeRequest_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyCertificateOfInsuranceChangeService", "MedicalMalpracticeCertificateOfLiabilityChangeRequest");
    public final static QName _MedicalMalpracticeCertificateOfLiabilityChangeResult_QNAME = new QName("http://www.delphi-tech.com/ows/PolicyCertificateOfInsuranceChangeService", "MedicalMalpracticeCertificateOfLiabilityChangeResult");

    /**
     * Constructor.
     */
    public PolicyCertificateOfInsuranceChangeServiceManagerImpl() {
    }

    @Override
    public MedicalMalpracticeCertificateOfLiabilityChangeResultType generateCoi(MedicalMalpracticeCertificateOfLiabilityChangeRequestType medicalMalpracticeCertificateOfLiabilityChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateCoi", new Object[]{medicalMalpracticeCertificateOfLiabilityChangeRequest});
        }
        OwsLogRequest owsLogRequest = null;
        if (l.isLoggable(Level.FINER)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(medicalMalpracticeCertificateOfLiabilityChangeRequest, _MedicalMalpracticeCertificateOfLiabilityChangeRequest_QNAME);
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(xmlResult,
                medicalMalpracticeCertificateOfLiabilityChangeRequest.getMessageId(), medicalMalpracticeCertificateOfLiabilityChangeRequest.getCorrelationId(),
                medicalMalpracticeCertificateOfLiabilityChangeRequest.getUserId(), _MedicalMalpracticeCertificateOfLiabilityChangeRequest_QNAME.getLocalPart());
            l.logp(Level.FINEST, getClass().getName(), "generateCoi", xmlResult);
        } else {
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(medicalMalpracticeCertificateOfLiabilityChangeRequest, _MedicalMalpracticeCertificateOfLiabilityChangeRequest_QNAME,
                medicalMalpracticeCertificateOfLiabilityChangeRequest.getMessageId(), medicalMalpracticeCertificateOfLiabilityChangeRequest.getCorrelationId(),
                medicalMalpracticeCertificateOfLiabilityChangeRequest.getUserId());
        }

        MedicalMalpracticeCertificateOfLiabilityChangeResultType medicalMalpracticeCoiChangeResult = new MedicalMalpracticeCertificateOfLiabilityChangeResultType();

        try {
            Record entityNumberIdRecord = new Record();
            validateForGenCoi(medicalMalpracticeCertificateOfLiabilityChangeRequest, entityNumberIdRecord);

            Record inputRecord = setInputPolicyToRecord(medicalMalpracticeCertificateOfLiabilityChangeRequest);
            inputRecord.setFields(entityNumberIdRecord);

            String transactionLogId = getCoiManager().generateCoiForWS(inputRecord);

            medicalMalpracticeCoiChangeResult.setTransactionNumberId(transactionLogId);

            medicalMalpracticeCoiChangeResult.setMessageStatus(MessageStatusHelper.getInstance().getSuccessMessageStatus());
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR,
                "Failure invoking the CoiManagerImpl", e , false);
            l.logp(Level.SEVERE, getClass().getName(), "generateCoi", ae.getMessage(), ae);
            medicalMalpracticeCoiChangeResult.setMessageStatus(MessageStatusHelper.getInstance().getRejectedMessageStatus(ae));
        }

        owsLogRequest.setMessageStatusCode(medicalMalpracticeCoiChangeResult.getMessageStatus().getMessageStatusCode());

        if (l.isLoggable(Level.FINEST)) {
            String xmlResult = XMLUtils.marshalJaxbToXML(medicalMalpracticeCoiChangeResult, _MedicalMalpracticeCertificateOfLiabilityChangeResult_QNAME);
            owsLogRequest.setResultXML(xmlResult);
            l.logp(Level.FINEST, getClass().getName(), "generateCoi", xmlResult);
        } else {
            owsLogRequest.setServiceResult(medicalMalpracticeCoiChangeResult);
            owsLogRequest.setServiceResultQName(_MedicalMalpracticeCertificateOfLiabilityChangeResult_QNAME);
        }
        OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateCoi", medicalMalpracticeCoiChangeResult);
        }
        return medicalMalpracticeCoiChangeResult;
    }

    private Record setInputPolicyToRecord(MedicalMalpracticeCertificateOfLiabilityChangeRequestType medicalMalpracticeCoiChangeRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputPolicyToRecord", new Object[]{medicalMalpracticeCoiChangeRequest});
        }
        Record inputRecord = new Record();

        inputRecord.setFieldValue(PolicyCertificateOfInsuranceChangeFields.TRANSACTION_EFFECTIVE_DATE,
            DateUtils.parseXMLDateToOasisDate(medicalMalpracticeCoiChangeRequest.getTransactionEffectiveDate()));

        inputRecord.setFieldValue(PolicyCertificateOfInsuranceChangeFields.POLICY_TERM_NUMBER_ID,
            medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation().getPolicyTermNumberId());

        inputRecord.setFieldValue(PolicyCertificateOfInsuranceChangeFields.IS_GENERATE,
            medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation().getGenerate());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputPolicyToRecord", inputRecord);
        }
        return inputRecord;
    }

    private void validateForGenCoi(MedicalMalpracticeCertificateOfLiabilityChangeRequestType medicalMalpracticeCoiChangeRequest,
                                   Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateForGenCoi", new Object[]{medicalMalpracticeCoiChangeRequest});
        }

        String errorMsg = "";
        String certificateHolderReference = "";

        if (StringUtils.isBlank(medicalMalpracticeCoiChangeRequest.getTransactionEffectiveDate())) {
            errorMsg = errorMsg + "transaction effective date,";
        }

        if (medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation() == null) {
            errorMsg = errorMsg + "Certificate information,";
        } else {
            certificateHolderReference = medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation().getReferredCertificateHolder().getCertificateHolderReference();
            if (!"Y".equals(medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation().getGenerate().toUpperCase())) {
                String warningMsg = "Please set Generate indicator value to Y for COI generation.";
                AppException ae = new AppException("ws.policyCertificateOfInsuranceChange.generation.warning", warningMsg);
                l.throwing(getClass().getName(), "validateForGenCoi", ae);
                throw ae;
            }
            if (StringUtils.isBlank(medicalMalpracticeCoiChangeRequest.getMedicalMalpracticeCertificateInformation().getPolicyTermNumberId())) {
                errorMsg = errorMsg + "policy term number Id,";
            }
            if (StringUtils.isBlank(certificateHolderReference)) {
                errorMsg = errorMsg + "certificate holder reference,";
            } else {
                if (medicalMalpracticeCoiChangeRequest.getCertificateHolder() == null) {
                    errorMsg = errorMsg + "Certificate holder entity,";
                } else {
                    String certificateHolderNumberId = "";
                    for (CertificateHolderType certificateHolder: medicalMalpracticeCoiChangeRequest.getCertificateHolder()) {
                        if (certificateHolderReference.equals(certificateHolder.getKey())) {
                            certificateHolderNumberId = certificateHolder.getCertificateHolderNumberId();
                            break;
                        }
                    }
                    if (StringUtils.isBlank(certificateHolderNumberId)) {
                        errorMsg = errorMsg + "Certificate holder number Id,";
                    } else {
                        inputRecord.setFieldValue(PolicyCertificateOfInsuranceChangeFields.CERTIFICATE_HOLDER_NUMBER_ID,
                            certificateHolderNumberId);
                    }
                }
            }
        }

        if (errorMsg != "") {
            errorMsg = errorMsg.substring(0, errorMsg.length()-1);
            AppException ae = new AppException("ws.policyCertificateOfInsuranceChange.required", "", new String[]{errorMsg});
            l.throwing(getClass().getName(), "validateForGenCoi", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateForGenCoi");
        }
    }

    public void verifyConfig() {
        if (getCoiManager() == null)
            throw new ConfigurationException("The required property 'coiManager' is missing.");
    }

    public CoiManager getCoiManager() {
        return m_coiManager;
    }

    public void setCoiManager(CoiManager coiManager) {
        m_coiManager = coiManager;
    }

    private CoiManager m_coiManager;
}
