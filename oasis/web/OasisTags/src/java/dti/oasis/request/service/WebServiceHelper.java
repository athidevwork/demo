package dti.oasis.request.service;

import dti.oasis.app.AppException;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 17, 2010
 *
 * @author fcb
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/11/2016       wdang       176749 - Set source context to WEBSERVICE when initializing Oasis User during web service.
 * ---------------------------------------------------
 */
public class WebServiceHelper {
    private static final WebServiceHelper INSTANCE = new WebServiceHelper();

    public static WebServiceHelper getInstance() {
        return INSTANCE;
    }

    private WebServiceHelper() {
    }

    public void setWebServiceUser(String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWebServiceUser", new Object[]{userId,});
        }

        OasisUser oasisUser = null;
        try {
            RequestStorageManager rsm = RequestStorageManager.getInstance();
            HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
            oasisUser = ActionHelper.initializeOasisUser(request, userId);
            oasisUser.setSourceContext(OasisUser.SourceContextEnum.OWS);
        } catch (Exception e) {
            AppException ae = new AppException("Failed to determine the Oasis User.", e);
            l.throwing(getClass().getName(), "setWebServiceUser", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWebServiceUser");
        }
    }

    public String setWebServiceUser(String userId, HttpHeaders headers) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWebServiceUser", new Object[]{userId,headers});
        }

        userId = performAuthorization(userId, headers, null);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWebServiceUser");
        }
        return userId;
    }

    public String setWebServiceUser(String userId, String authToken) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setWebServiceUser", new Object[]{userId,authToken});
        }

        userId = performAuthorization(userId, null, authToken);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setWebServiceUser");
        }
        return userId;
    }

    private String performAuthorization(String userId, HttpHeaders headers, String authToken) {
        OasisUser oasisUser = null;
        if (userId == null) {
            if (headers != null)
                userId = getUserFromAuthorization(getAuthHeader(headers));
            else
                userId = getUserFromAuthorization(authToken);
        }
        try {
            RequestStorageManager rsm = RequestStorageManager.getInstance();
            HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);
            oasisUser = ActionHelper.initializeOasisUser(request, userId);
            oasisUser.setSourceContext(OasisUser.SourceContextEnum.OWS);
        } catch (Exception e) {
            AppException ae = new AppException("Failed to determine the Oasis User for the request.", e);
            l.throwing(getClass().getName(), "setWebServiceUser", ae);
            throw ae;
        }
        return userId;
    }

    private String getAuthHeader(HttpHeaders headers) {
        final String authorizationHeader = headers.getHeaderString("Authorization");
        return authorizationHeader;
    }

    public String getUserFromAuthorization(String authToken) {
        String user = null;

        if (authToken != null && authToken.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authToken.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            user = values[0];
            if (l.isLoggable(Level.FINER)) {
                l.finer("User :" + values[0]);
                //l.finer("Password :" + values[1]);
            }
        }
        return user;
    }

    /**
     * Get the current request.
     * @return
     */
    public HttpServletRequest getCurrentRequest() {
        l.entering(getClass().getName(), "getCurrentRequest");

        RequestStorageManager rsm = RequestStorageManager.getInstance();
        HttpServletRequest request = (HttpServletRequest) rsm.get(RequestStorageIds.HTTP_SEVLET_REQUEST);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentRequest", request);
        }
        return request;
    }

    /**
     * Get current user.
     * @return
     */
    public OasisUser getCurrentUser() {
        l.entering(getClass().getName(), "getCurrentUser");

        OasisUser oasisUser = ActionHelper.getCurrentUser(getCurrentRequest());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCurrentUser", oasisUser);
        }
        return oasisUser;
    }
    
    public <T>String marshalObject(Class<T> type, T obj, QName qName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "marshalObject", new Object[]{type, obj, qName});
        }

        JAXBContext jc = null;
        StringWriter writer = new StringWriter();
        try {
            jc = JAXBContext.newInstance(type.getPackage().getName());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement element = new JAXBElement<T>(qName, type, null, obj);
            marshaller.marshal(element, writer);
        } catch (Exception e) {
            l.logp(Level.WARNING, getClass().getName(), "getPayloadMessage", "Fail to marshal ClaimInquiryResultType: " + e.getMessage());
        }

        String result = writer.getBuffer().toString();
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "marshalObject", result);
        }
        return result;
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
