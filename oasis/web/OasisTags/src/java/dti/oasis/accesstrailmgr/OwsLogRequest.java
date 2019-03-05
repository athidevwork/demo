package dti.oasis.accesstrailmgr;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestSession;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.XMLUtils;
import dti.oasis.util.ZipUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2014
 *
 * @author Parker Xu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 01/16/2019       athi        Issue 197346 - Support for Restful web services.
 * ---------------------------------------------------
 */
public class OwsLogRequest<T> implements Serializable, Cloneable {

    public OwsLogRequest(String pk) {
        setOwsAccessTrailId(pk);
        initializeOwsLogJob();
    }

    private void initializeOwsLogJob() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initializeOwsLogJob");
        }
        Date accessDate = new Date();
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        this.m_remoteUser = ActionHelper.getCurrentUser(request).getUserId();
        this.m_ipAddress = ActionHelper.getOriginalIP(request);
        this.m_subsystemCode = ApplicationContext.getInstance().getProperty("applicationId", "");
//        if (rsm.has(AccessTrailRequestIds.OWS_TRAIL_SOURCE_TABLE_NAME)) {
//            this.m_sourceTableName = (String) rsm.get(AccessTrailRequestIds.OWS_TRAIL_SOURCE_TABLE_NAME);
//        }
//        if (rsm.has(AccessTrailRequestIds.OWS_TRAIL_SOURCE_RECORD_NO)) {
//            this.m_sourceRecordNo = (String) rsm.get(AccessTrailRequestIds.OWS_TRAIL_SOURCE_RECORD_NO);
//        }
//        if (rsm.has(AccessTrailRequestIds.OWS_TRAIL_SOURCE_RECORD_FK)) {
//            this.m_sourceRecordFk = (String) rsm.get(AccessTrailRequestIds.OWS_TRAIL_SOURCE_RECORD_FK);
//        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        accessDate = (Date) request.getAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE);
        this.m_accessDate = dateFormat.format(accessDate);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initializeOwsLogJob");
        }
    }

    public Record toInsertRecord() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "toInsertRecord");
        }
        Record inputRecord = new Record();
        inputRecord.setFieldValue("remoteUser", this.m_remoteUser);
        inputRecord.setFieldValue("ipAddress", m_ipAddress);
        inputRecord.setFieldValue("subsystemCode", m_subsystemCode);
        inputRecord.setFieldValue("accessDate", m_accessDate);
        inputRecord.setFieldValue("requestName", m_requestName);
        inputRecord.setFieldValue("messageId", m_messageId);
        inputRecord.setFieldValue("correlationId", m_correlationId);
        inputRecord.setFieldValue("userId", m_userId);
        inputRecord.setFieldValue("owsAccessId", m_owsAccessTrailId);
        inputRecord.setFieldValue("requestXML", ZipUtils.compress(this.getRequestXML()));
        inputRecord.setFieldValue("method", m_method);
        inputRecord.setFieldValue("uri", m_uri);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "toInsertRecord");
        }
        return inputRecord;
    }

    public Record toUpdateRecord() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "toUpdateRecord");
        }
        Record inputRecord = new Record();
        inputRecord.setFieldValue("owsAccessId", m_owsAccessTrailId);
        inputRecord.setFieldValue("sourceTableName", m_sourceTableName);
        inputRecord.setFieldValue("sourceRecordNo", m_sourceRecordNo);
        inputRecord.setFieldValue("sourceRecordId", m_sourceRecordFk);
        inputRecord.setFieldValue("messageStatusCode", m_messageStatusCode);

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        Date accessDate = (Date) request.getAttribute(AccessTrailRequestIds.OWS_TRAIL_ACCESS_DATE);
        this.m_elapsedTime = new Date().getTime() - accessDate.getTime();
        inputRecord.setFieldValue("elapsedTime", m_elapsedTime);
        inputRecord.setFieldValue("resultXML", ZipUtils.compress(this.getResultXML()));
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "toUpdateRecord");
        }
        return inputRecord;
    }

    public synchronized OwsLogRequest copyOwsLogRequest() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyOwsLogRequest");
        }
        OwsLogRequest updateLogRequest = null;
        try {
            updateLogRequest = (OwsLogRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            l.throwing(getClass().getName(), "Failed to copy the OwsLogRequest with requestName:" + this.getRequestName(), e);
            throw new AppException("Failed to copy the OwsLogRequest with requestName:" + this.getRequestName());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "copyOwsLogRequest", updateLogRequest);
        }
        return updateLogRequest;
    }

    public synchronized void setOwsLogJobInitialized() {
        l.entering(getClass().getName(), "setOwsLogJobInitialized");
        m_requestLogQueueState = OwsLogQueueState.INITIALIZED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setOwsLogJobInitialized", "LRT - OwsLogJobRequest: owsJob state is " + m_requestLogQueueState);
        }
        l.exiting(getClass().getName(), "setOwsLogJobInitialized");
    }

    public synchronized boolean isOwsLogJobInitialized() {
        return m_requestLogQueueState.isInitialized();
    }

    public synchronized void setOwsLogJobProcessing() {
        l.entering(getClass().getName(), "setOwsLogJobProcessing");
        m_requestLogQueueState = OwsLogQueueState.PROCESSING;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setOwsLogJobProcessing", "LRT - OwsLogJobRequest: owsJob state is " + m_requestLogQueueState);
        }
        l.exiting(getClass().getName(), "setOwsLogJobProcessing");
    }

    public synchronized boolean isOwsLogJobProcessing() {
        return m_requestLogQueueState.isProcessing();
    }

    public synchronized void setOwsLogJobComplete() {
        l.entering(getClass().getName(), "setOwsLogJobComplete");
        m_requestLogQueueState = OwsLogQueueState.COMPLETED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setOwsLogJobComplete", "LRT - OwsLogJobRequest: owsJob state is " + m_requestLogQueueState);
        }
        l.exiting(getClass().getName(), "setOwsLogJobComplete");
    }

    public synchronized boolean isOwsLogJobComplete() {
        return m_requestLogQueueState.isCompleted();
    }

    public synchronized void setOwsLogJobFailed() {
        l.entering(getClass().getName(), "setOwsLogJobFailed");
        m_requestLogQueueState = OwsLogQueueState.FAILED;
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "setOwsLogJobFailed", "LRT - OwsLogJobRequest: owsJob state is " + m_requestLogQueueState);
        }
        l.exiting(getClass().getName(), "setOwsLogJobFailed");
    }

    @Override
    public String toString() {
        l.entering(getClass().getName(), "toString");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("OwsLogRequest{");
        stringBuffer.append("m_owsAccessTrailId=").append(getOwsAccessTrailId());
        stringBuffer.append(", m_requestName=").append(getRequestName());
        stringBuffer.append(", m_messageId=").append(getMessageId());
        stringBuffer.append(", m_correlationId=").append(getCorrelationId());
        stringBuffer.append(", m_userId=").append(getUserId());
        stringBuffer.append(", m_remoteUser=").append(getRemoteUser());
        stringBuffer.append(", m_ipAddress=").append(getIpAddress());
        stringBuffer.append(", m_indicator=").append(getIndicator());
        stringBuffer.append(", m_subsystemCode=").append(getSubsystemCode());
        stringBuffer.append(", m_sourceRecordFk=").append(getSourceRecordFk());
        stringBuffer.append(", m_sourceTableName=").append(getSourceTableName());
        stringBuffer.append(", m_sourceRecordNo=").append(getSourceRecordNo());
        stringBuffer.append(", m_accessDate=").append(getAccessDate());
        stringBuffer.append(", m_messageStatusCode=").append(getMessageStatusCode());
        stringBuffer.append(", m_elapsedTime=").append(getElapsedTime());
        stringBuffer.append(", m_requestNode=").append(XMLUtils.formatNode(getRequestNode()));
        stringBuffer.append(", m_resultNode=").append(XMLUtils.formatNode(getResultNode()));
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    public synchronized boolean isOwsLogJobFailed() {
        return m_requestLogQueueState.isFailed();
    }

    public synchronized boolean isOwsLogJobHandled() {
        return (isOwsLogJobComplete() || isOwsLogJobProcessing());
    }

    public OwsLogQueueState getState() {
        return m_requestLogQueueState;
    }

    public void setOwsState(RequestSession requestSession) {
        this.m_requestSession = requestSession;
    }

    public RequestSession getRequestSession() {
        return m_requestSession;
    }

    public void setIndicator(String indicator) {
        this.m_indicator = indicator;
    }

    public String getIndicator() {
        return m_indicator;
    }

    public void setException(AppException exception) {
        this.m_exception = exception;
    }

    public AppException getException() {
        return m_exception;
    }

    public long getStartExecutionTime() {
        return m_startExecutionTime;
    }

    public void setStartExecutionTime(long startExecutionTime) {
        this.m_startExecutionTime = startExecutionTime;
    }

    public long getFinishExecutionTime() {
        return m_finishExecutionTime;
    }

    public void setFinishExecutionTime(long finishExecutionTime) {
        this.m_finishExecutionTime = finishExecutionTime;
    }

    public long getElapsedExecutionTime() {
        return getFinishExecutionTime() - getStartExecutionTime();
    }

    public String getUserId() {
        return m_userId;
    }

    public void setUserId(String userId) {
        this.m_userId = userId;
    }

    public String getOwsAccessTrailId() {
        return m_owsAccessTrailId;
    }

    public void setOwsAccessTrailId(String owsAccessTrailId) {
        this.m_owsAccessTrailId = owsAccessTrailId;
    }

    public String getRequestName() {
        return m_requestName;
    }

    public void setRequestName(String requestName) {
        this.m_requestName = requestName;
    }

    public String getMessageId() {
        return m_messageId;
    }

    public void setMessageId(String messageId) {
        this.m_messageId = messageId;
    }

    public String getCorrelationId() {
        return m_correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.m_correlationId = correlationId;
    }

    public String getRemoteUser() {
        return m_remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.m_remoteUser = remoteUser;
    }

    public String getIpAddress() {
        return m_ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.m_ipAddress = ipAddress;
    }

    public String getSubsystemCode() {
        return m_subsystemCode;
    }

    public void setSubsystemCode(String subsystemCode) {
        this.m_subsystemCode = subsystemCode;
    }

    public String getSourceTableName() {
        return m_sourceTableName;
    }

    public void setSourceTableName(String sourceTableName) {
        this.m_sourceTableName = sourceTableName;
    }

    public String getSourceRecordNo() {
        return m_sourceRecordNo;
    }

    public void setSourceRecordNo(String sourceRecordNo) {
        this.m_sourceRecordNo = sourceRecordNo;
    }

    public T getServiceRequest() {
        return m_serviceRequest;
    }

    public void setServiceRequest(T serviceRequest) {
        this.m_serviceRequest = serviceRequest;
    }

    public QName getServiceResultQName() {
        return m_serviceResultQName;
    }

    public void setServiceResultQName(QName serviceResultQName) {
        this.m_serviceResultQName = serviceResultQName;
    }

    public String getSourceRecordFk() {
        return m_sourceRecordFk;
    }

    public void setSourceRecordFk(String sourceRecordFk) {
        this.m_sourceRecordFk = sourceRecordFk;
    }

    public String getAccessDate() {
        return m_accessDate;
    }

    public void setAccessDate(String accessDate) {
        this.m_accessDate = accessDate;
    }

    public String getMessageStatusCode() {
        return m_messageStatusCode;
    }

    public void setMessageStatusCode(String messageStatusCode) {
        this.m_messageStatusCode = messageStatusCode;
    }

    public long getElapsedTime() {
        return m_elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.m_elapsedTime = elapsedTime;
    }

    public String getMethod() {
        return m_method;
    }

    public void setMethod(String method) {
        this.m_method = method;
    }

    public String getUri() { return m_uri; }

    public void setUri(String m_uri) { this.m_uri = m_uri; }

    public T getServiceResult() {
        return m_serviceResult;
    }

    public void setServiceResult(T serviceResult) {
        this.m_serviceResult = serviceResult;
    }

    public QName getServiceRequestQName() {
        return m_serviceRequestQName;
    }

    public void setServiceRequestQName(QName serviceRequestQName) {
        this.m_serviceRequestQName = serviceRequestQName;
        this.setRequestName(serviceRequestQName.getLocalPart());
    }

    public String getRequestXML() {
        return m_requestXML;
    }

    public void setRequestXML(String requestXML) {
        this.m_requestXML = requestXML;
    }

    public String getResultXML() {
        return m_resultXML;
    }

    public void setResultXML(String resultXML) {
        this.m_resultXML = resultXML;
    }

    public Node getRequestNode() {
        return m_requestNode;
    }

    public void setRequestNode(Node requestNode) {
        this.m_requestNode = requestNode;
    }

    public Node getResultNode() {
        return m_resultNode;
    }

    public void setResultNode(Node resultNode) {
        this.m_resultNode = resultNode;
    }

    private RequestSession m_requestSession;
    private volatile OwsLogQueueState m_requestLogQueueState;
    private AppException m_exception;
    private long m_startExecutionTime;
    private long m_finishExecutionTime;

    private String m_owsAccessTrailId;
    private String m_requestName;
    private String m_messageId;
    private String m_correlationId;
    private String m_userId;
    private String m_remoteUser;
    private String m_ipAddress;
    private String m_indicator;
    private String m_subsystemCode;
    private String m_sourceRecordFk;
    private String m_sourceTableName;
    private String m_sourceRecordNo;
    private String m_accessDate;
    private String m_messageStatusCode;
    private long m_elapsedTime;
    private String m_requestXML;
    private String m_resultXML;
    private String m_method;
    private String m_uri;

    private Node m_requestNode;
    private Node m_resultNode;
    private T m_serviceRequest;
    private T m_serviceResult;
    private QName m_serviceRequestQName;
    private QName m_serviceResultQName;

    private final Logger l = LogUtils.getLogger(getClass());

    public static final String INDICATOR_INSERT = "INSERT";
    public static final String INDICATOR_UPDATE = "UPDATE";
}
