package dti.ci.credentialrequestmgr.resource.jaxrs;

import dti.ci.credentialrequestmgr.resource.service.CredentialRequestManager;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterRequest;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatus;
import dti.ci.credentialrequestmgr.rest.pojo.CredentialLetterStatuses;
import dti.oasis.accesstrailmgr.OwsAccessTrailManager;
import dti.oasis.accesstrailmgr.OwsLogRequest;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.rest.UnprocessableValidationException;
import dti.oasis.jpa.BaseResource;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.uri.UriTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/credential-letter")
public class CredentialLetterJaxrsResource extends BaseResource {

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    public CredentialLetterStatus processCredentialLetterRequest(CredentialLetterRequest request,
                                                   @QueryParam(value = "userId") String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processCredentialLetterRequest", new Object[]{request, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatus status = null;

        try {
            userId = checkForAuthorizedUser(userId, httpHeaders);
            if (validateInputCredentialRequest(request)) {
                userId = WebServiceHelper.getInstance().getUserFromAuthorization(getAuthHeader(httpHeaders));
                owsLogRequest = getOwsLogRequest(request, null, userId);
                status = getCredentialLetterManager().performCredentialLetterSubmit(request);
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

    @GET
    @Path("/status")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public CredentialLetterStatuses getStatusForCredentialLetterRequestor(@QueryParam(value = "requestorId") String requestorId,
                                                          @QueryParam(value = "userId") String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getStatusForCredentialLetterRequestor", new Object[]{requestorId, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatuses statuses = new CredentialLetterStatuses();

        try {
            checkForAuthorizedUser(userId, httpHeaders);
            owsLogRequest = getOwsLogRequest(null, requestorId, userId);
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

    @GET
    @Path("/{requestId}/status")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public CredentialLetterStatus getStatusForCredentialLetterRequest(@PathParam(value = "requestId") String requestId,
                                                        @QueryParam(value = "userId") String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getStatusForCredentialLetterRequest", new Object[]{requestId, userId});
        }

        OwsLogRequest owsLogRequest = null;
        CredentialLetterStatus status = null;

        try {
            checkForAuthorizedUser(userId, httpHeaders);
            owsLogRequest = getOwsLogRequest(null, requestId, null);
            status = getCredentialLetterManager().processCredentialLetterStatus(requestId);
            owsLogRequest.setResultXML(status.toString());
            updateOwsLogRequest(owsLogRequest, status, null, null);
        } catch (Exception e) {
            updateOwsLogRequest(owsLogRequest, null, null, e.getMessage());
            throw e;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getStatusForCredentialLetterRequestor", (status != null ? status.toString() : ""));
        }

        return status;
    }

    /**
     * Utility method to create a request sample for Credit Letter Request.
     *
     * @param
     * @return CreditLetterRequest
     */
    /*@GET
    @Path("/data/{requestId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDataForCredentialLetterRequest(@PathParam(value = "requestId") String requestId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDataForCredentialLetterRequest", new Object[]{});
        }

        CredentialLetterRequest sampleRequest = new CredentialLetterRequest();
        sampleRequest.setUserId("testUser");
        sampleRequest.setRequestType("email");
        sampleRequest.setOriginatingSystem("DDP");
        Requestor requestor = new Requestor();
        requestor.setAddressId(35532);
        requestor.setRequestorId(45466);
        requestor.setEmail("test@domain.com");
        requestor.setBillingAcctId(768843);
        requestor.setAttnOf("Attention Smith");
        requestor.setNotes("Sample Request");
        sampleRequest.setRequestor(requestor);
        InsuredDetail insuredDetail = new InsuredDetail();
        insuredDetail.setEntityId(546633);
        insuredDetail.setPrefix("Mr.");
        insuredDetail.setFirstName("FirstName");
        insuredDetail.setLastName("LastName");
        insuredDetail.setSuffix("Jr.");
        insuredDetail.setExternalId("354454");
        insuredDetail.setIncludeCoi(false);
        insuredDetail.setIncludeCoverageHistory(true);
        insuredDetail.setIncludeClaimsHistory(true);
        insuredDetail.setDegree("MD");
        insuredDetail.setAddChargeFee(false);
        List<InsuredDetail> insuredDetailList = new ArrayList<InsuredDetail>();
        insuredDetailList.add(insuredDetail);
        sampleRequest.setInsuredDetail(insuredDetailList);
        Account account = new Account();
        account.setAcctNumberId(12455);
        account.setAcctName("Org 212");
        account.setAcctDesc("Main Account for org 212");
        account.setAcctType("General");
        account.setCurrentBalance(100.00);
        account.setNextBillingDate("01/01/2018");
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        sampleRequest.setAccount(accounts);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDataForCredentialLetterRequest");
        }
        return Response.ok(sampleRequest).build();
    }*/

    private String getAuthHeader(HttpHeaders headers) {
        final String authorizationHeader = headers.getHeaderString("Authorization");
        return authorizationHeader;
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

    private OwsLogRequest getOwsLogRequest(CredentialLetterRequest clRequest, String requestOrRequestorId, String userId) {
        String expandedPath = null;
        String templatePath = null;
        ContainerRequest request = (ContainerRequest) ((ContainerRequest) httpHeaders).getRequest();

        StringBuilder pathSb = new StringBuilder();
        if (request.getUriInfo().getQueryParameters().size() == 0) {
            pathSb.append("/").append(request.getUriInfo().getPath());
            expandedPath = pathSb.toString();
        }
        else {
            StringBuilder epSb = new StringBuilder();
            epSb.append("/").append(request.getUriInfo().getPath()).append("?");
            MultivaluedMap<String, String> params = request.getUriInfo().getQueryParameters();

            for(String key : params.keySet()){
                epSb.append(key).append("=").append(params.getFirst(key)).append("&");
                if (key.equalsIgnoreCase("requestorId"))
                    expandedPath = params.getFirst(key);
            }
            expandedPath = epSb.substring(0, epSb.lastIndexOf("&"));
        }

        List<UriTemplate> templatePathList = request.getUriInfo().getMatchedTemplates();
        StringBuilder templatePathSb = new StringBuilder();
        for (UriTemplate template : templatePathList) {
            if (template.isTemplateVariablePresent("requestId"))
                templatePathSb.append(template.getTemplate());
            else
                templatePathSb.insert(0, template.getTemplate());
        }
        templatePath = templatePathSb.toString();

        OwsLogRequest owsLogRequest = null;
        if (request.getMethod().equalsIgnoreCase("POST")) {
            owsLogRequest = addOwsLogRequest(userId, clRequest, null, request.getMethod(), expandedPath, templatePath);
        }
        else {
            if (userId == null)
                userId = WebServiceHelper.getInstance().getUserFromAuthorization(getAuthHeader(httpHeaders));
            owsLogRequest = addOwsLogRequest(userId, null, requestOrRequestorId, request.getMethod(), expandedPath, templatePath);
        }

        if (l.isLoggable(Level.FINER)) {
            l.logp(Level.FINER, getClass().getName(), "getOwsLogRequest","owsLogRequest =" + owsLogRequest);
        }

        return owsLogRequest;
    }

    public OwsLogRequest addOwsLogRequest(String userId, CredentialLetterRequest request, String requestOrRequestorId,
                                          String requestMethod, String expandedPath, String templatePath) {
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

            owsLogRequest.setRequestName(owsLogRequest.getRequestName());
            OwsAccessTrailManager.getInstance().updateOwsAccessTrailLogger(owsLogRequest);
        }
        else {
            if (l.isLoggable(Level.FINER)) {
                l.logp(Level.FINER, getClass().getName(), "updateOwsLogRequest", " owsLogRequest is null.");
            }
        }
    }

    private String checkForAuthorizedUser(String userId, HttpHeaders httpHeaders) {
        try {
            userId = WebServiceHelper.getInstance().setWebServiceUser(userId, httpHeaders);
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
    @Context HttpHeaders httpHeaders;
 }
