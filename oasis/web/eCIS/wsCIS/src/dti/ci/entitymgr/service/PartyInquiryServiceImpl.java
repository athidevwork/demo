package dti.ci.entitymgr.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusAppException;
import dti.ows.common.MessageStatusHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PartyInquiryServiceImpl class implements web service endpoint interface PartyInquiryService
 */

@WebService(
    name = "PartyInquiryService",
    serviceName = "PartyInquiryService",
    targetNamespace = "http://www.delphi-tech.com/ows/PartyInquiryService",
    wsdlLocation = "/wsdls/PartyInquiryService.wsdl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class PartyInquiryServiceImpl {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String SERVICE_MANAGER_BEAN_NAME = "PartyInquiryServiceManager";

    public PartyInquiryServiceImpl() {

    }

    @WebMethod(action = "partyInquiryRequest",
        operationName = "partyInquiryRequest")
    @WebResult(name = "PartyInquiryResult", targetNamespace = "http://www.delphi-tech.com/ows/PartyInquiryService")
    public com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType partyInquiryRequest(@WebParam(name = "PartyInquiryRequest", targetNamespace = "http://www.delphi-tech.com/ows/PartyInquiryService") com.delphi_tech.ows.partyinquiryservice.PartyInquiryRequestType partyInquiryRequest) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "partyInquiryRequest", new Object[]{partyInquiryRequest});
        }

        if (partyInquiryRequest.getUserId() != null) {
            WebServiceHelper.getInstance().setWebServiceUser(partyInquiryRequest.getUserId());
        }

        PartyInquiryResultType partyInquiryResult;
        try {
            PartyInquiryServiceManager partyInquiryServiceManager = (PartyInquiryServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            partyInquiryResult = partyInquiryServiceManager.loadParty(partyInquiryRequest);
        } catch (Exception e) {
            MessageStatusAppException msae = MessageStatusHelper.getInstance().handleException("Failure invoking the Party Inquiry service.", e);
            l.logp(Level.SEVERE, getClass().getName(), "partyInquiryRequest", msae.getMessage(), msae);

            partyInquiryResult = new PartyInquiryResultType();
            partyInquiryResult.setMessageStatus(msae.getMessageStatus());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "partyInquiryRequest", partyInquiryResult);
        }
        return partyInquiryResult;
    }
}  