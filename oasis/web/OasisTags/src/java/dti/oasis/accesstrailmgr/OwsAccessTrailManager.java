package dti.oasis.accesstrailmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * This is an interface for recording user activities in eOASIS.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   08/25/2014
 *
 * @author Parker
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 08/29/2014       parker      Issue 138227. Enhancement to add the ows logs.
 * 01/16/2019       athi        Issue 197346 - Support for Restful web services.
 * ---------------------------------------------------
 */
public abstract class OwsAccessTrailManager<T> {


    /**
     * The bean name of a OwsAccessTrailManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "owsAccessTrailManager";

    /**
     * Return an instance of the OwsAccessTrailManager.
     */
    public synchronized static final OwsAccessTrailManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (OwsAccessTrailManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                throw new ConfigurationException("The required bean '" + BEAN_NAME + "' is missing.");
            }
        }
        return c_instance;
    }

    /**
     * add a ows access trail log with input String for restful web services.
     *
     * @param requestXML
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     * @param method
     */
    public abstract OwsLogRequest addOwsAccessTrailLogger(String requestXML, String messageId,
                                                          String correlationId, String userId, String requestName, String method, String uri);

    /**
     * add a ows access trail log with input String
     * @param requestXML
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     */
    public abstract OwsLogRequest addOwsAccessTrailLogger(String requestXML, String messageId,
                                                          String correlationId, String userId, String requestName);

    /**
     * add a ows access trail log with input Document
     * @param requestNode
     * @param messageId
     * @param correlationId
     * @param userId
     * @param requestName
     */
    public abstract OwsLogRequest addOwsAccessTrailLogger(Node requestNode, String messageId,
                                                          String correlationId, String userId, String requestName);

    /**
     * add a ows access trail log with input QName
     * @param serviceRequest
     * @param requestQName
     * @param messageId
     * @param correlationId
     * @param userId
     */
    public abstract OwsLogRequest addOwsAccessTrailLogger(T serviceRequest, QName requestQName,
                                                          String messageId, String correlationId, String userId);

    /**
     * update the ows access trail log with input owsLogRequest
     */
    public abstract void updateOwsAccessTrailLogger(OwsLogRequest owsLogRequest);

    /**
     * add the ows logger information to the database.
     *
     * @param owsLogRequest
     */
    public abstract void processOwsLogRequest(OwsLogRequest owsLogRequest);


    private static OwsAccessTrailManager c_instance;
}
