package dti.oasis.accesstrailmgr.impl;

import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.accesstrailmgr.dao.OwsAccessTrailDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestLifecycleAdvisor;
import dti.oasis.util.*;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.io.Serializable;
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
public class OwsAccessTrailManagerImpl<T> extends OwsAccessTrailManager<T> implements Serializable {

    public final static String SYS_PARAM_OWS_LOG_ACCESS = "OWS_LOG_ACCESS";
    public final static String OWS_LOG_ACCESS_VALUE_ALL = "All";
    public final static String OWS_LOG_ACCESS_VALUE_NONE = "None";

    /**
     * add the ows logger information to the database.
     *
     * @param owsLogRequest
     */
    public void processOwsLogRequest(OwsLogRequest owsLogRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processOwsLogRequest", new Object[]{owsLogRequest});
        }
        Record record;
        if (OwsLogRequest.INDICATOR_INSERT.equals(owsLogRequest.getIndicator())) {
            if (owsLogRequest.getRequestNode() != null) {
                owsLogRequest.setRequestXML(XMLUtils.formatNode(owsLogRequest.getRequestNode()));
            } else if (StringUtils.isBlank(owsLogRequest.getRequestXML())) {
                owsLogRequest.setRequestXML(XMLUtils.marshalJaxbToXML(owsLogRequest.getServiceRequest(), owsLogRequest.getServiceRequestQName()));
            }
            record = owsLogRequest.toInsertRecord();
            getOwsAccessTrailDAO().addOwsAccessTrail(record);
        } else {
            if (owsLogRequest.getResultNode() != null) {
                owsLogRequest.setResultXML(XMLUtils.formatNode(owsLogRequest.getResultNode()));
            } else if (StringUtils.isBlank(owsLogRequest.getResultXML())) {
                if (owsLogRequest.getServiceResult() != null && owsLogRequest.getServiceResultQName() != null) {
                    owsLogRequest.setResultXML(XMLUtils.marshalJaxbToXML(owsLogRequest.getServiceResult(), owsLogRequest.getServiceResultQName()));
                }
            }
            record = owsLogRequest.toUpdateRecord();
            getOwsAccessTrailDAO().updateOwsAccessTrail(record);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processOwsLogRequest", record);
        }
    }

    /**
     * add a ows access trail log with input QName
     *
     * @param serviceRequest
     * @param requestQName
     * @param messageId
     * @param correlationId
     * @param userId
     */
    @Override
    public OwsLogRequest addOwsAccessTrailLogger(T serviceRequest, QName requestQName, String messageId, String correlationId, String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLogger", new Object[]{serviceRequest, requestQName,messageId, correlationId, userId});
        }
        OwsLogRequest owsLogRequest = new OwsLogRequest(DatabaseUtils.getNewPK(OWS_SEQUENCE));
        if (isOwsAccessTrailLogEnable(requestQName.getLocalPart())) {
            owsLogRequest.setMessageId(messageId);
            owsLogRequest.setCorrelationId(correlationId);
            owsLogRequest.setUserId(userId);
            owsLogRequest.setIndicator(OwsLogRequest.INDICATOR_INSERT);
            owsLogRequest.setServiceRequest(serviceRequest);
            owsLogRequest.setServiceRequestQName(requestQName);
            if (Boolean.valueOf(logByOwsLogQueue)) {
                owsLogRequest.setOwsState(RequestLifecycleAdvisor.getInstance().getRequestState());
                getOwsLogQueueManager().schedule(owsLogRequest);
            } else {
                processOwsLogRequest(owsLogRequest);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLogger", owsLogRequest);
        }
        return owsLogRequest.copyOwsLogRequest();
    }

    /**
     * add a ows access trail log with input String
     *
     * @param requestXML
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     */
    @Override
    public OwsLogRequest addOwsAccessTrailLogger(String requestXML, String messageId, String correlationId, String userId, String requestName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLogger", new Object[]{requestXML, messageId, correlationId, userId, requestName});
        }
        OwsLogRequest owsLogRequest = new OwsLogRequest(DatabaseUtils.getNewPK(OWS_SEQUENCE));
        if (isOwsAccessTrailLogEnable(requestName)) {
            owsLogRequest.setMessageId(messageId);
            owsLogRequest.setCorrelationId(correlationId);
            owsLogRequest.setUserId(userId);
            owsLogRequest.setIndicator(OwsLogRequest.INDICATOR_INSERT);
            owsLogRequest.setRequestName(requestName);
            owsLogRequest.setRequestXML(requestXML);
            if (Boolean.valueOf(logByOwsLogQueue)) {
                owsLogRequest.setOwsState(RequestLifecycleAdvisor.getInstance().getRequestState());
                getOwsLogQueueManager().schedule(owsLogRequest);
            } else {
                processOwsLogRequest(owsLogRequest);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLogger", owsLogRequest);
        }
        return owsLogRequest.copyOwsLogRequest();
    }

    /**
     * add a ows access trail log with input String
     *
     * @param requestXML
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     * @param method
     */
    @Override
    public OwsLogRequest addOwsAccessTrailLogger(String requestXML, String messageId, String correlationId, String userId, String requestName, String method, String uri) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLogger", new Object[]{requestXML, messageId, correlationId, userId, requestName, method, uri});
        }
        OwsLogRequest owsLogRequest = new OwsLogRequest(DatabaseUtils.getNewPK(OWS_SEQUENCE));
        if (isOwsAccessTrailLogEnable(requestName)) {
            owsLogRequest.setMessageId(messageId);
            owsLogRequest.setCorrelationId(correlationId);
            owsLogRequest.setUserId(userId);
            owsLogRequest.setIndicator(OwsLogRequest.INDICATOR_INSERT);
            owsLogRequest.setRequestName(requestName);
            owsLogRequest.setRequestXML(requestXML);
            owsLogRequest.setMethod(method);
            owsLogRequest.setUri(uri);
            if (Boolean.valueOf(logByOwsLogQueue)) {
                owsLogRequest.setOwsState(RequestLifecycleAdvisor.getInstance().getRequestState());
                getOwsLogQueueManager().schedule(owsLogRequest);
            } else {
                processOwsLogRequest(owsLogRequest);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLogger", owsLogRequest);
        }
        return owsLogRequest.copyOwsLogRequest();
    }

    /**
     * add a ows access trail log with input Document
     *
     * @param requestNode
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     */
    @Override
    public OwsLogRequest addOwsAccessTrailLogger(Node requestNode, String messageId, String correlationId, String userId, String requestName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addOwsAccessTrailLogger", new Object[]{requestNode, userId, messageId, correlationId, requestName});
        }
        OwsLogRequest owsLogRequest = new OwsLogRequest(DatabaseUtils.getNewPK(OWS_SEQUENCE));
        if (isOwsAccessTrailLogEnable(requestName)) {
            owsLogRequest.setMessageId(messageId);
            owsLogRequest.setCorrelationId(correlationId);
            owsLogRequest.setUserId(userId);
            owsLogRequest.setIndicator(OwsLogRequest.INDICATOR_INSERT);
            owsLogRequest.setRequestName(requestName);
            owsLogRequest.setRequestNode(requestNode);
            if (Boolean.valueOf(logByOwsLogQueue)) {
                owsLogRequest.setOwsState(RequestLifecycleAdvisor.getInstance().getRequestState());
                getOwsLogQueueManager().schedule(owsLogRequest);
            } else {
                processOwsLogRequest(owsLogRequest);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addOwsAccessTrailLogger", owsLogRequest);
        }
        return owsLogRequest.copyOwsLogRequest();
    }

    /**
     * update the ows access trail log with input owsLogRequest
     *
     */
    public void updateOwsAccessTrailLogger(OwsLogRequest owsLogRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateOwsAccessTrailLogger", owsLogRequest);
        }
        if (isOwsAccessTrailLogEnable(owsLogRequest.getRequestName())) {
            owsLogRequest.setIndicator(OwsLogRequest.INDICATOR_UPDATE);
            if (Boolean.valueOf(logByOwsLogQueue)) {
                owsLogRequest.setOwsState(RequestLifecycleAdvisor.getInstance().getRequestState());
                getOwsLogQueueManager().schedule(owsLogRequest);
            } else {
                processOwsLogRequest(owsLogRequest);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateOwsAccessTrailLogger");
        }
    }

    /**
     * The method to check whether to log the ows information.
     *
     * @param requestName
     * @return boolean
     */
    private boolean isOwsAccessTrailLogEnable(String requestName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isOwsAccessTrailLogEnable", new Object[]{requestName});
        }
        //A system parameter called OWS_LOG_ACCESS will be used to configure the default behavior
        // for logging of all web service requests and change notification events,
        // supporting the options {"All" | "None"}, defaulted to "None".
        String logEnableSystemParameter = SysParmProvider.getInstance().getSysParm(SYS_PARAM_OWS_LOG_ACCESS, OWS_LOG_ACCESS_VALUE_NONE);
        Record inputRecord = new Record();
        inputRecord.setFieldValue("requestName", requestName);
        String logEnableForRequestName = getOwsAccessTrailDAO().checkTheConfigForRequestName(inputRecord);

        boolean isOwsAccessTrailLogEnable = false;
        if ((OWS_LOG_ACCESS_VALUE_ALL.equals(logEnableSystemParameter) && !"N".equalsIgnoreCase(logEnableForRequestName))||
        (OWS_LOG_ACCESS_VALUE_NONE.equals(logEnableSystemParameter) && "Y".equalsIgnoreCase(logEnableForRequestName))) {
            isOwsAccessTrailLogEnable = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isOwsAccessTrailLogEnable");
        }
        return isOwsAccessTrailLogEnable;
    }

    /**
     * verify config.
     */
    public void verifyConfig() {
        if (getOwsAccessTrailDAO() == null)
            throw new ConfigurationException("The required property 'owsTrailDAO' is missing.");
    }


    public OwsAccessTrailDAO getOwsAccessTrailDAO() {
        return m_owsAccessTrailDAO;
    }

    public void setOwsAccessTrailDAO(OwsAccessTrailDAO owsAccessTrailDAO) {
        this.m_owsAccessTrailDAO = owsAccessTrailDAO;
    }

    private OwsAccessTrailDAO m_owsAccessTrailDAO;

    public OwsLogQueueManagerImpl getOwsLogQueueManager() {
        return owsLogQueueManager;
    }

    public void setOwsLogQueueManager(OwsLogQueueManagerImpl owsLogQueueManager) {
        this.owsLogQueueManager = owsLogQueueManager;
    }

    public String getLogByOwsLogQueue() {
        return logByOwsLogQueue;
    }

    public void setLogByOwsLogQueue(String logByOwsLogQueue) {
        this.logByOwsLogQueue = logByOwsLogQueue;
    }

    private String logByOwsLogQueue = "true";

    private OwsLogQueueManagerImpl owsLogQueueManager;
    private final Logger l = LogUtils.getLogger(getClass());

    public final static String OWS_SEQUENCE ="oasis_access_trail_seq.nextval";
}
