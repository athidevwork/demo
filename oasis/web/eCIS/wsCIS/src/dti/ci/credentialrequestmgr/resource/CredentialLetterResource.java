package dti.ci.credentialrequestmgr.resource;

import dti.ci.credentialrequestmgr.resource.service.CredentialRequestManager;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterRequest;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatus;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatuses;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.rest.UnprocessableValidationException;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/credential-letter")
public class CredentialLetterResource {

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public CredentialLetterStatus processCredentialLetterRequest(@RequestBody CredentialLetterRequest clRequest,
                                                                 HttpServletRequest request,
                                                                 @RequestParam(value = "userId", required = false) String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCredentialLetterRequest", new Object[]{clRequest, request, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatus status = null;

        try {
            userId = checkForAuthorizedUser(request, userId);
            if (validateInputCredentialRequest(clRequest)) {
                userId = WebServiceHelper.getInstance().getUserFromAuthorization(request.getHeader("Authorization"));
                owsLogRequest = getOwsLogRequest(request, clRequest, null, userId);
                status = getCredentialLetterManager().performCredentialLetterSubmit(clRequest);
                owsLogRequest.setResultXML(status.toString());
                updateOwsLogRequest(owsLogRequest, status, null, null);
            }
        } catch (Exception e) {
            updateOwsLogRequest(owsLogRequest, null, null, e.getMessage());
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processCredentialLetterRequest", (status != null ? status.toString() : ""));
        }

        return status;
    }

    @GetMapping(value = "/status",
            headers ={"Accept=application/json,application/xml"},
            produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public CredentialLetterStatuses getStatusForCredentialLetterRequestor(@RequestParam(value = "requestorId") String requestorId,
                                                          @RequestParam(value = "userId", required = false) String userId,
                                                          HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getStatusForCredentialLetterRequestor", new Object[]{requestorId, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatuses statuses = new CredentialLetterStatuses();

        try {
            userId = checkForAuthorizedUser(request, userId);
            owsLogRequest = getOwsLogRequest(request, null, requestorId, userId);
            statuses = getCredentialLetterManager().processRequestorStatus(requestorId, userId);
            owsLogRequest.setResultXML(statuses.toString());
            updateOwsLogRequest(owsLogRequest, null, statuses, null);
        } catch (Exception e) {
            updateOwsLogRequest(owsLogRequest, null, null, e.getMessage());
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getStatusForCredentialLetterRequestor", (statuses != null ? statuses.toString() : ""));
        }

        return statuses;
    }

    @GetMapping(value = "/{requestId}/status",
                headers ={"Accept=application/json,application/xml"},
                produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public CredentialLetterStatus getStatusForCredentialLetterRequest(@PathVariable(value = "requestId") String requestId,
                                                                      @RequestParam(value = "userId", required = false) String userId,
                                                                      HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getStatusForCredentialLetterRequest", new Object[]{requestId, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatus status = null;

        try {
            userId = checkForAuthorizedUser(request, userId);
            owsLogRequest = getOwsLogRequest(request, null, requestId, null);
            status = getCredentialLetterManager().processCredentialLetterStatus(requestId);
            owsLogRequest.setResultXML(status.toString());
            updateOwsLogRequest(owsLogRequest, status, null, null);
        } catch (Exception e) {
            updateOwsLogRequest(owsLogRequest, null, null, e.getMessage());
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getStatusForCredentialLetterRequest", (status != null ? status.toString() : ""));
        }

        return status;
    }

    private boolean validateInputCredentialRequest(CredentialLetterRequest request) {
        boolean isValid = true;
        String separator = "";
        StringBuilder sb = new StringBuilder();
        if (request.getOriginatingSystem() == null) {
            isValid = false;
            sb.append(separator).append("Originating System not found");
            separator = "; ";
        }
        if (request.getRequestor() == null) {
            isValid = false;
            sb.append(separator).append("Requestor detail not found");
            separator = "; ";
        }
        if (request.getInsuredDetail() == null) {
            isValid = false;
            sb.append(separator).append("Insured detail not found");
        }
        if (!isValid)
            throw new UnprocessableValidationException(sb.toString());
        return isValid;
    }

    public OwsLogRequest addOwsLogRequest(String userId, CredentialLetterRequest request, String requestOrRequestorId, String requestMethod, String expandedPath, String templatePath) {
        OwsLogRequest owsLogRequest = null;
        if (requestMethod.equalsIgnoreCase("POST")) {
            if (l.isLoggable(Level.FINEST)) {
                l.logp(Level.FINEST, getClass().getName(), "addOwsLogRequest", request.toString());
            }
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(request.toString(),
                    "", "", userId, templatePath, requestMethod, expandedPath);
            owsLogRequest.setSourceTableName("CI_CRED_REQ");
            owsLogRequest.setSourceRecordFk(String.valueOf(request.getRequestor().getRequestorId()));
        }
        if (requestMethod.equalsIgnoreCase("GET")) {
            if (l.isLoggable(Level.FINEST)) {
                l.logp(Level.FINEST, getClass().getName(), "addOwsLogRequest", requestOrRequestorId);
            }
            owsLogRequest = OwsAccessTrailManager.getInstance().addOwsAccessTrailLogger(requestOrRequestorId,
                    "", "", userId, templatePath, requestMethod, expandedPath);
            owsLogRequest.setSourceTableName("CI_CRED_REQ");
            owsLogRequest.setSourceRecordFk(String.valueOf(requestOrRequestorId));
        }

        return owsLogRequest;
    }

    public void updateOwsLogRequest(OwsLogRequest owsLogRequest, CredentialLetterStatus status, CredentialLetterStatuses statuses, String message) {
        if (owsLogRequest != null) {
            if (status != null) {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "updateOwsLogRequest", "status = " + status);
                }
                owsLogRequest.setResultXML(status.toString());
                owsLogRequest.setServiceResult(status);
            } else if (statuses != null) {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "updateOwsLogRequest", "statuses = " + statuses);
                }
                owsLogRequest.setResultXML(statuses.toString());
                owsLogRequest.setServiceResult(statuses);
            } else {
                if (l.isLoggable(Level.FINER)) {
                    l.logp(Level.FINER, getClass().getName(), "updateOwsLogRequest", "message = " + message);
                }
                owsLogRequest.setResultXML(message.toString());
                owsLogRequest.setServiceResult(message);
            }
            owsLogRequest.setMessageStatusCode("Success");
            owsLogRequest.setSourceRecordNo("");

            //owsLogRequest.setRequestName(owsLogRequest.getRequestName().substring(owsLogRequest.getRequestName().lastIndexOf('.') + 1));
            owsLogRequest.setRequestName(owsLogRequest.getRequestName());
            OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);
        }
        else {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "updateOwsLogRequest", " owsLogRequest is null.");
            }
        }
    }

    private OwsLogRequest getOwsLogRequest(HttpServletRequest request, CredentialLetterRequest clRequest, String requestOrRequestorId, String userId) {
        String expandedPath = "";
        if (request.getQueryString() == null)
            expandedPath = request.getPathInfo();
        else {
            StringBuilder epSb = new StringBuilder();
            epSb.append(request.getPathInfo()).append("&").append(request.getQueryString());
            expandedPath = epSb.toString();
        }

        String templatePath = "";
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String param = attributeNames.nextElement();
            if (param.equalsIgnoreCase("org.springframework.web.servlet.HandlerMapping.bestMatchingPattern")) {
                if (request.getMethod().equalsIgnoreCase("POST")) {
                    //String value = request.getAttribute(param).toString();
                    //templatePath = value.substring(0, value.lastIndexOf("/"));
                    templatePath = request.getAttribute(param).toString();
                }
                else
                    templatePath = request.getAttribute(param).toString();
            }
        }

        OwsLogRequest owsLogRequest = null;
        if (request.getMethod().equalsIgnoreCase("POST")) {
            owsLogRequest = addOwsLogRequest(userId, clRequest, null, request.getMethod(), expandedPath, templatePath);
        }
        else {
            if (userId == null)
                userId = WebServiceHelper.getInstance().getUserFromAuthorization(request.getHeader("Authorization"));
            owsLogRequest = addOwsLogRequest(userId, null, requestOrRequestorId, request.getMethod(), expandedPath, templatePath);
        }

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "getOwsLogRequest","owsLogRequest =" + owsLogRequest);
        }

        return owsLogRequest;
    }

    private String checkForAuthorizedUser(HttpServletRequest request, String userId) {
        try {
            String authToken = request.getHeader("Authorization");
            userId = WebServiceHelper.getInstance().setWebServiceUser(userId, authToken);
        }
        catch (Exception e) {
            throw new UnprocessableValidationException("The user id " +  userId + " does not exist in OASIS.");
        }
        if (userId == null)
            throw new UnprocessableValidationException("The user id is required from either the Authorization header or as a userId query parameter.");
        return userId;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getCredentialLetterManager() == null)
            throw new ConfigurationException("The required property 'getCredentialLetterManager' is missing.");
    }

    public CredentialRequestManager getCredentialLetterManager() {
        return credentialLetterManager;
    }

    public void setCredentialLetterManager(CredentialRequestManager credentialLetterManager) {
        this.credentialLetterManager = credentialLetterManager;
    }

    private CredentialRequestManager credentialLetterManager = (CredentialRequestManager) ApplicationContext.getInstance().getBean("credentialLetterManager");
    private Logger l = LogUtils.getLogger(getClass());
}
