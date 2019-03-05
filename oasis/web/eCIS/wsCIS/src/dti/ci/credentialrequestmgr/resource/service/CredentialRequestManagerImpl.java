package dti.ci.credentialrequestmgr.resource.service;

import dti.ci.credentialrequestmgr.resource.service.dao.CredentialRequestDAO;
import dti.ci.credentialrequestmgr.rest.pojo.*;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.rest.NotFoundException;
import dti.oasis.error.rest.UnprocessableValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/12/2019
 *
 * @author athi
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CredentialRequestManagerImpl implements  CredentialRequestManager {

    ///////////////Credential Letter Restful Service Implementation Utility Methods /////////////
    public CredentialLetterStatus performCredentialLetterSubmit(CredentialLetterRequest request) {
        CredentialLetterStatus result = null;
        boolean isValidEntities = true;
        try {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "performCredentialLetterSubmit", "request = " + request);
            }

            if (setDefaultValuesForOrigSystem(request)) {
                for (InsuredDetail insuredDetail : request.getInsuredDetail()) {
                    if (getCredentialLetterRequestDAO().validateEntity(insuredDetail.getEntityId()).equalsIgnoreCase("N")) {
                        l.finer("Validating Entity " + insuredDetail.getEntityId());
                        isValidEntities = false;
                        throw new UnprocessableValidationException("Entity " + insuredDetail.getEntityId() + " should be a policy holder or a risk on a policy. Validation failed.");
                    }
                }

                if (isValidEntities) {
                    result = saveCredentialSubmitRequest(request);
                }

                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "performCredentialLetterSubmit", "result = " + result);
                }
            }
        }
        catch (Exception e) {
            throw new NotFoundException("Failure invoking submit request in CredentialRequestImpl");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "performCredentialLetterSubmit - result = " + result);
        }

        return result;
    }

    public CredentialLetterStatuses getRequestorStatus(String requestorId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRequestorStatus", new Object[]{requestorId});
        }

        RecordSet rs = getCredentialLetterRequestDAO().getRequestorStatus(requestorId);
        CredentialLetterStatuses statuses = new CredentialLetterStatuses();
        if (rs.getSize() > 0) {
            Iterator it = rs.getRecords();

            statuses = new CredentialLetterStatuses();
            while (it.hasNext()) {
                Record outputRecord = (Record) it.next();

                String reqId = outputRecord.getStringValue("CICREDREQID");
                CredentialLetterStatus status = new CredentialLetterStatus();
                status.setRequestNumberId(Long.valueOf(reqId));
                status.setStatus(outputRecord.getStringValue("STATUS"));
                status.setRequestDate(outputRecord.getStringValue("SYSUPDATETIME"));
                status.setNotes(outputRecord.getStringValue("NOTES"));
                //result.setRequestDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
                statuses.addStatus(status);
            }
        }
        else {
            throw new NotFoundException("Failure to get requestor status");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRequestorStatus");
        }

        return statuses;
    }

    public CredentialLetterStatuses processRequestorStatus (String requestorId, String userId) {
        CredentialLetterStatuses result = null;
        if (requestorId != null) {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "processRequestorStatus", "requestorId = " + requestorId + ", userId = " + userId);
            }
            result = getRequestorStatus(requestorId);
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "processRequestorStatus", "result = " + result);
            }
        }
        else {
            throw new NotFoundException("Requestor Id not found as a request parameter. Failure to get status request in CredentialRequestImpl");
        }

        return result;
    }

    @Override
    public CredentialLetterStatus getRequestStatus(String requestId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRequestStatus", new Object[]{requestId});
        }

        CredentialLetterStatus result = new CredentialLetterStatus();
        RecordSet rs = getCredentialLetterRequestDAO().getRequestStatus(requestId);

        String status = rs.getSummaryRecord().getStringValue("STATUS");
        if (status.contains("not found in db.")) {
            throw new NotFoundException(status);
        }
        else {
            result.setRequestNumberId(Long.valueOf(requestId));
            result.setStatus(status);
            result.setRequestDate(rs.getSummaryRecord().getStringValue("TIME"));
            result.setNotes(rs.getSummaryRecord().getStringValue("NOTES"));
            //result.setRequestDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRequestStatus");
        }

        return result;
    }

    public CredentialLetterStatus processCredentialLetterStatus (String requestId) {
        CredentialLetterStatus result = null;

        if (requestId != null) {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "processCredentialLetterStatus", "requestId = " + requestId);
            }
            result = getRequestStatus(requestId);
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "processCredentialLetterStatus", "result = " + result);
            }
        }
        else {
            throw new NotFoundException("RequestId not found as a request parameter. Failure to get status request in CredentialRequestImpl");
        }

        return result;
    }

    public boolean setDefaultValuesForOrigSystem(CredentialLetterRequest request) {
        if (!request.getOriginatingSystem().isEmpty()) {
            CredentialLetterDefaults defaults = getDefaultValues(request);
            if (defaults.getOriginatingSystem().isEmpty()) {
                throw new UnprocessableValidationException("OriginatingSystem = " + request.getOriginatingSystem() + " not found in db. Cannot determine default values");
            }
            else {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "setDefaultValuesForOrigSystem", "defaults = " + defaults);
                }
                //add default values if not given for optional fields
                for (InsuredDetail insuredDetail : request.getInsuredDetail()) {
                    if (l.isLoggable(Level.FINER)) {
                        l.logp(Level.FINER, getClass().getName(), "setDefaultValuesForOrigSystem", "insuredDetail = " + insuredDetail);
                    }
                    if (defaults.getCoi().equalsIgnoreCase("Y"))
                        insuredDetail.setIncludeCoi(true);
                    else
                        insuredDetail.setIncludeCoi(false);
                    if (defaults.getClaimsHistory().equalsIgnoreCase("Y"))
                        insuredDetail.setIncludeClaimsHistory(true);
                    else
                        insuredDetail.setIncludeClaimsHistory(false);
                    if (defaults.getCoverageHistory().equalsIgnoreCase("Y"))
                        insuredDetail.setIncludeCoverageHistory(true);
                    else
                        insuredDetail.setIncludeCoverageHistory(false);
                    if (defaults.getChargeFee().equalsIgnoreCase("Y"))
                        insuredDetail.setAddChargeFee(true);
                    else
                        insuredDetail.setAddChargeFee(false);
                }
            }
        }
        else {
            throw new NotFoundException("OriginatingSystem = " + request.getOriginatingSystem() + " not found in request. Cannot determine default values");
        }
        return true;
    }
    ///////////////Credential Letter Restful Service Implementation Utility Methods /////////////

    ///////////////Credential Letter Restful Service Implementation Methods /////////////
    @Override
    public CredentialLetterStatus saveCredentialSubmitRequest(CredentialLetterRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCredentialSubmitRequest", new Object[]{request});
        }

        RecordSet rc = new RecordSet();

        Record requestRecord = new Record();
        requestRecord.setFieldValue("requestorEntityId", request.getRequestor().getRequestorId());
        requestRecord.setFieldValue("addressId", request.getRequestor().getAddressId());
        requestRecord.setFieldValue("billingAccountId", request.getRequestor().getBillingAcctId());
        requestRecord.setFieldValue("attention", request.getRequestor().getAttnOf());
        requestRecord.setFieldValue("notes", request.getRequestor().getNotes());

        CredentialLetterStatus result = new CredentialLetterStatus();
        try {
            RecordSet rs = (RecordSet) getCredentialLetterRequestDAO().saveCredentialRequest(requestRecord);
            String output = rs.getSummaryRecord().getStringValue("CICREDREQID");
            if (output == null) {
                throw new AppException("Failure to get request status id from database.");
            } else {
                result.setRequestNumberId(Long.valueOf(output));
                result.setStatus(rs.getSummaryRecord().getStringValue("STATUS"));
                result.setRequestDate(rs.getSummaryRecord().getStringValue("TIME"));
                result.setNotes(rs.getSummaryRecord().getStringValue("NOTES"));
                //result.setRequestDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
            }
        }
        catch (Exception e) {
            throw new AppException("Failure to get request status", e);
        }

        List<InsuredDetail> insuredDetailList = request.getInsuredDetail();
        for (InsuredDetail insuredDetail : insuredDetailList) {
            Record record = new Record();
            record.setFieldValue("ciCredReqId", result.getRequestNumberId());
            record.setFieldValue("legacyDataId", insuredDetail.getExternalId());
            record.setFieldValue("detailEntityId", insuredDetail.getEntityId());
            record.setFieldValue("prefixName", insuredDetail.getPrefix());
            record.setFieldValue("firstName", insuredDetail.getFirstName());
            record.setFieldValue("lastName", insuredDetail.getLastName());
            record.setFieldValue("suffixName", insuredDetail.getSuffix());
            record.setFieldValue("profDesignation", insuredDetail.getDegree());
            if (insuredDetail.getIncludeClaimsHistory())
                record.setFieldValue("claimHistoryB", "Y");
            else
                record.setFieldValue("claimHistoryB", "N");
            if (insuredDetail.getIncludeCoi())
                record.setFieldValue("coiB", "Y");
            else
                record.setFieldValue("coiB", "N");
            if (insuredDetail.getIncludeCoverageHistory())
                record.setFieldValue("coverageHistoryB", "Y");
            else
                record.setFieldValue("coverageHistoryB", "N");
            if (insuredDetail.getAddChargeFee())
                record.setFieldValue("feeB", "Y");
            else
                record.setFieldValue("feeB", "N");
            rc.addRecord(record);
        }
        int updatedCount = getCredentialRequestDAO().saveAllRequestDetail(rc);
        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "saveCredentialSubmitRequest", "updatedCount = " + updatedCount);
        }

        Record request1Record = new Record();
        request1Record.setFieldValue("ciCredReqId", result.getRequestNumberId());
        getCredentialRequestDAO().saveProcessRequest(request1Record);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveCredentialSubmitRequest");
        }

        return result;
    }

    @Override
    public String validateEntity(long requestorId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateEntity", new Object[]{requestorId});
        }

        String response = getCredentialLetterRequestDAO().validateEntity(requestorId);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateEntity");
        }
        return response;
    }

    @Override
    public CredentialLetterDefaults getDefaultValues(CredentialLetterRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDefaultValues", new Object[]{request});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("origSystem", request.getOriginatingSystem());
        RecordSet rs = getCredentialLetterRequestDAO().getCacheDefaults(inputRecord);

        CredentialLetterDefaults defaults = new CredentialLetterDefaults();
        Record outputRecord = new Record();
        if (rs.getSize() > 0) {
            Iterator iter = rs.getRecords();

            while (iter.hasNext()) {
                outputRecord = (Record) iter.next();

                defaults.setOriginatingSystem(request.getOriginatingSystem());
                defaults.setCoi(outputRecord.getStringValue("VALUE1"));
                defaults.setClaimsHistory(outputRecord.getStringValue("VALUE2"));
                defaults.setCoverageHistory(outputRecord.getStringValue("VALUE3"));
                defaults.setChargeFee(outputRecord.getStringValue("VALUE4"));
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDefaultValues");
        }
        return defaults;
    }
    ///////////////Credential Letter Restful Service Implementation Methods /////////////

    public CredentialRequestDAO getCredentialLetterRequestDAO() {
        return credentialLetterRequestDAO;
    }

    public void setCredentialLetterRequestDAO(CredentialRequestDAO credentialLetterRequestDAO) {
        this.credentialLetterRequestDAO = credentialLetterRequestDAO;
    }

    public dti.ci.credentialrequestmgr.dao.CredentialRequestDAO getCredentialRequestDAO() {
        return credentialRequestDAO;
    }

    public void setCredentialRequestDAO(dti.ci.credentialrequestmgr.dao.CredentialRequestDAO credentialRequestDAO) {
        this.credentialRequestDAO = credentialRequestDAO;
    }

    private CredentialRequestDAO credentialLetterRequestDAO =  (CredentialRequestDAO) ApplicationContext.getInstance().getBean("credentialLetterRequestDAO");
    private dti.ci.credentialrequestmgr.dao.CredentialRequestDAO credentialRequestDAO = (dti.ci.credentialrequestmgr.dao.CredentialRequestDAO) ApplicationContext.getInstance().getBean("credentialRequestDAO");
    private final Logger l = LogUtils.getLogger(getClass());
}
