package dti.ci.entitymgr.service.hubpartychange.processor;

import com.delphi_tech.ows.party.CertificationType;
import com.delphi_tech.ows.partychangeservice.PartyChangeRequestType;
import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.cs.partynotificationmgr.mgr.impl.HubCertificationManagerImpl;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.ows.util.FieldElementMap;
import dti.oasis.ows.validation.Validator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/23/2016
 *
 * @author dpang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class HubCertificationChangeProcessor extends BaseHubPartyChangeElementProcessor<CertificationType> {

    @Override
    public void processFromCisResult(PartyChangeRequestType partyChangeRequest, PartyChangeResultType partyChangeResult, List<CertificationType> cisResultElements,
                                     String entityId, List<CertificationType> changedElements) {

        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFromCisResult",
                    new Object[]{partyChangeRequest, partyChangeResult, cisResultElements, entityId, changedElements});
        }

        RecordSet recordSet = new RecordSet();
        for (CertificationType cisResultCertification : cisResultElements) {
            boolean foundChangedElement = false;
            String entityBoardId = null;
            for (CertificationType changedCertification : changedElements) {
                if (changedCertification.getCertificationNumberId().equals(cisResultCertification.getCertificationNumberId())) {
                    foundChangedElement = true;
                    entityBoardId = changedCertification.getCertificationBoard();
                    break;
                }
            }

            if (foundChangedElement) {
                //Use the board id of the party change request, because the PartyInquiryService returns the org name of a board
                cisResultCertification.setCertificationBoard(entityBoardId);

                Record inputRecord = getCertificationRecord(entityId, cisResultCertification);
                setCommonFieldsToRecord(inputRecord, partyChangeRequest, CISB_Y);
                recordSet.addRecord(inputRecord);
            }
        }

        if (recordSet.getSize() > 0) {
            getHubPartyManager().saveHubPartyInBatch(recordSet);
        }
        l.exiting(getClass().getName(), "processFromCisResult");
    }

    @Override
    public void processForHub(PartyChangeRequestType partyChangeRequest, String entityType, String entityId, List<CertificationType> changedElements,
                              List<CertificationType> originalElements) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processForHub", new Object[]{partyChangeRequest, partyChangeRequest, entityType, entityId, changedElements, originalElements});
        }

        RecordSet certificationRecordSet = getCertificationRecordSet(entityId, partyChangeRequest);

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
                setCommonFieldsToRecord(changedCertificationRecord, partyChangeRequest, "N");
                Record result = getHubPartyManager().saveHubParty(changedCertificationRecord);

                if (rowStatus.equals(ROW_STATUS_NEW)) {
                    changedCertification.setCertificationNumberId(result.getStringValue("newRiskClassProfileId"));
                }
            }
        }

        l.exiting(getClass().getName(), "processForHub");
    }


    private RecordSet getCertificationRecordSet(String entityId, PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getCertificationRecordSet", new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);
        inputRecord.setFieldValue("origin", getSourceSystem(partyChangeRequest));
        RecordSet rs = getHubPartyManager().loadCertification(inputRecord);

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
/*
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
    }*/

    public HubCertificationManagerImpl getHubPartyManager() {
        return m_hubPartyManager;
    }

    public void setHubPartyManager(HubCertificationManagerImpl hubPartyManager) {
        m_hubPartyManager = hubPartyManager;
    }

    public List<FieldElementMap> getFieldElementMaps() {
        return m_fieldElementMaps;
    }

    public void setFieldElementMaps(List<FieldElementMap> fieldElementMaps) {
        m_fieldElementMaps = fieldElementMaps;
    }

    private HubCertificationManagerImpl m_hubPartyManager;
    private List<FieldElementMap> m_fieldElementMaps;
}
