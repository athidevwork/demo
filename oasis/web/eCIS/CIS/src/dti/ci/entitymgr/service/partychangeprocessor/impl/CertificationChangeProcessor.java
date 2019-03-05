package dti.ci.entitymgr.service.partychangeprocessor.impl;

import com.delphi_tech.ows.party.CertificationType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.ci.certificationmgr.CertificationManager;
import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   9/26/14
 *
 * @author kshen
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CertificationChangeProcessor extends BasePartyChangeElementProcessor<CertificationType> {
    /**
     * Process entity elements.
     *
     * @param partyChangeRequest
     * @param partyChangeResult
     * @param entityType
     * @param entityId
     * @param changedElements
     * @param originalElements
     */
    @Override
    public void process(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult,
                        String entityType, String entityId,
                        List<CertificationType> changedElements, List<CertificationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "process",
                    new Object[]{partyChangeRequest, partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        String entityKey = getEntityKey(partyChangeRequest, entityType, entityId);
        RecordSet certificationRecordSet = getCertificationRecordSet(entityId);
        
        for (CertificationType changedCertification : changedElements) {
            Validator.validateFieldRequired(changedCertification.getKey(),
                    "ci.partyChangeService.field.required.error", "Certification Key");

            CertificationType originalCertification = getOriginalCertification(originalElements, changedCertification);

            Record changedCertificationRecord = getCertificationRecord(entityId, changedCertification);
            Record originalCertificationRecord = getCertificationRecord(entityId, originalCertification);
            // The PartyInquiryService returns the org name of a board, so we need to get the board entity id from db.
            Record dbCertificationRecord = getDbCertificationRecord(certificationRecordSet, changedCertification);

            String rowStatus = getRowStatus(changedCertificationRecord, originalCertificationRecord, dbCertificationRecord);

            if (rowStatus.equals(ROW_STATUS_NEW) || rowStatus.equals(ROW_STATUS_MODIFIED)) {
                mergeRecordValues(changedCertificationRecord, dbCertificationRecord);
                validateCertification(changedCertificationRecord);

                Record changedValues = getChangedValues(changedCertificationRecord, originalCertificationRecord,
                        dbCertificationRecord, new String[]{"riskClassProfileId", "entityId"});
                changedValues.setFieldValue(ROW_STATUS, rowStatus);

                saveCertification(changedCertification, changedValues);
            }
        }

        l.exiting(getClass().getName(), "process");
    }
    
    private RecordSet getCertificationRecordSet(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCertificationRecordSet", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);
        RecordSet rs = getCertificationManager().loadCertification(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCertificationRecordSet", rs);
        }
        return rs;
    }

    private CertificationType getOriginalCertification(List<CertificationType> originalCertificationList,
                                                       CertificationType changedCertification) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOriginalCertification",
                    new Object[]{originalCertificationList, changedCertification});
        }

        CertificationType certification = null;

        if (originalCertificationList != null) {
            for (CertificationType tempCertification : originalCertificationList) {
                if (changedCertification.getKey().equals(tempCertification.getKey())) {
                    certification = tempCertification;
                    break;
                }
            }
        }

        if (certification == null) {
            if (!StringUtils.isBlank(changedCertification.getCertificationNumberId())) {
                MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                        new Object[]{"Cannot find original Certification in Previous Value Data Description" +
                                " with Certification number ID:" + changedCertification.getCertificationNumberId() + "."});
                throw new AppException("Cannot find original Certification in Previous Value Data Description.");
            }
        } else {
            Validator.validateFieldRequired(changedCertification.getCertificationNumberId(),
                    "ci.partyChangeService.field.required.error",
                    "Certification Number ID of an existing Certification");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalCertification", certification);
        }
        return certification;
    }

    private Record getCertificationRecord(String entityId, CertificationType certification) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCertificationRecord", new Object[]{entityId, certification});
        }

        Record record = null;

        if (certification != null) {
            record = new Record();
            record.setFieldValue("entityId", entityId);

            if (!StringUtils.isBlank(certification.getCertificationNumberId())) {
                record.setFieldValue("riskClassProfileId", certification.getCertificationNumberId());
            }
            mapObjectToRecord(getFieldElementMaps(), certification, record);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCertificationRecord", record);
        }

        return record;
    }
    
    private Record getDbCertificationRecord(RecordSet certificationRecordSet, CertificationType changedCertification) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDbCertificationRecord",
                    new Object[]{certificationRecordSet, changedCertification});
        }


        Record certificationRecord = null;

        if (!StringUtils.isBlank(changedCertification.getCertificationNumberId()) &&
                certificationRecordSet != null) {
            for (Record tempCertificationRecord : certificationRecordSet.getRecordList()) {
                if (tempCertificationRecord.getStringValue("riskClassProfileId", "").equals(
                        changedCertification.getCertificationNumberId())) {
                    certificationRecord = tempCertificationRecord;
                    break;
                }
            }
        }

        if (!StringUtils.isBlank(changedCertification.getCertificationNumberId()) && certificationRecord == null) {
            MessageManager.getInstance().addErrorMessage("ci.webservice.error",
                    new Object[]{"Cannot find Certification in DB with Certification Number ID:" + changedCertification.getCertificationNumberId() + "."});
            throw new AppException("Cannot find Certification in DB.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDbCertificationRecord", certificationRecord);
        }
        return certificationRecord;
    }

    private void validateCertification(Record changedCertificationRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateCertification",
                    new Object[]{changedCertificationRecord});
        }

        String certificationBoard = changedCertificationRecord.getStringValue("entityBoardId", "");
        Validator.validateFieldRequired(certificationBoard,
                "ci.partyChangeService.field.required.error", "Certification Board");


        String startDate = changedCertificationRecord.getStringValue("certifiedDate", "");
        String endDate = changedCertificationRecord.getStringValue("eligExprdate", "");

        if (!StringUtils.isBlank(startDate) && !StringUtils.isBlank(endDate)) {
            Validator.validateDate2EqualOrAfterDate1(startDate, endDate,
                    "ci.partyChangeService.certification.effectiveDates.error", null);
        }

        l.exiting(getClass().getName(), "validateCertification");
    }
    
    private void saveCertification(CertificationType changedCertification,
                                   Record changedCertificationRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCertification", 
                    new Object[]{changedCertification, changedCertificationRecord});
        }

        Record recUpdateResult = getCertificationManager().saveCertification(changedCertificationRecord);

        if (ROW_STATUS_NEW.equals(changedCertificationRecord.getStringValue(ROW_STATUS, ""))) {
            changedCertification.setCertificationNumberId(recUpdateResult.getStringValue("newRiskClassProfileId"));
        }
        
        l.exiting(getClass().getName(), "saveCertification");
    }

    public CertificationManager getCertificationManager() {
        return m_certificationManager;
    }

    public void setCertificationManager(CertificationManager certificationManager) {
        this.m_certificationManager = certificationManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private CertificationManager m_certificationManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
