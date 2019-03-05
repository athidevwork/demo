package dti.ci.entitymgr.service;

import com.delphi_tech.ows.partychangeservice.PartyChangeResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PartyChangeServiceImpl class implements web service endpoint interface PartyChangeService */

@WebService(
    name="PartyChangeService",
    serviceName="PartyChangeService",
    targetNamespace="http://www.delphi-tech.com/ows/PartyChangeService",
    wsdlLocation="/wsdls/PartyChangeService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class PartyChangeServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "PartyChangeServiceManager";

    public PartyChangeServiceImpl() {

    }

    @WebMethod(action = "partyChangeRequest",
        operationName = "partyChangeRequest")
    @WebResult(name = "PartyChangeResult", targetNamespace = "http://www.delphi-tech.com/ows/PartyChangeService")
    public com.delphi_tech.ows.partychangeservice.PartyChangeResultType partyChangeRequest(@WebParam(name = "PartyChangeRequest", targetNamespace = "http://www.delphi-tech.com/ows/PartyChangeService") com.delphi_tech.ows.partychangeservice.PartyChangeRequestType partyChangeRequest) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveParty", new Object[]{partyChangeRequest});
        }
        if (partyChangeRequest.getUserId() != null) {
            WebServiceHelper.getInstance().setWebServiceUser(partyChangeRequest.getUserId());
        }
        PartyChangeResultType partyChangeResult = new PartyChangeResultType();
        try {
            PartyChangeServiceManager partyChangeServiceManager = (PartyChangeServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            partyChangeResult = partyChangeServiceManager.saveParty(partyChangeRequest);
        } catch (Exception e) {
            MessageStatusAppException msae = MessageStatusHelper.getInstance().handleException("Failure invoking the PartyChangeServiceManagerImpl", e);
            l.logp(Level.SEVERE, getClass().getName(), "PartyChangeServiceManagerImpl", msae.getMessage(), msae);
            partyChangeResult.setMessageStatus(msae.getMessageStatus());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveParty", partyChangeResult);
        }
        return partyChangeResult;
    }
}  