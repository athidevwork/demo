package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.quickquoteservice.QuickQuoteResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.ows.common.MessageStatusHelper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * QuickQuoteServiceImpl class implements web service endpoint interface QuickQuoteService */

@WebService(
    name="QuickQuoteService",
    serviceName="QuickQuoteService",
    targetNamespace="http://www.delphi-tech.com/ows/QuickQuoteService",
    wsdlLocation="/wsdls/QuickQuoteService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class QuickQuoteServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "QuickQuoteServiceManager";

  public QuickQuoteServiceImpl() {
  
  }

    @WebMethod(action="quickQuoteRequest",
        operationName="quickQuoteRequest")
    @WebResult(name="QuickQuoteResult", targetNamespace="http://www.delphi-tech.com/ows/QuickQuoteService")
    public QuickQuoteResultType quickQuoteRequest(@WebParam(name = "quickQuoteRequest", targetNamespace = "http://www.delphi-tech.com/ows/QuickQuoteService") com.delphi_tech.ows.quickquoteservice.QuickQuoteRequestType quickQuoteRequest)
     
  {

      QuickQuoteResultType quickQuoteResult = new QuickQuoteResultType();

      if (quickQuoteRequest.getUserId() == null || quickQuoteRequest.getUserId().length() == 0) {
          MessageStatusType messageStatusType = new MessageStatusType();
          messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
          List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
          ExtendedStatusType extendedStatusType = new ExtendedStatusType();
          extendedStatusType.setExtendedStatusDescription("UserId is required.");
          extendedStatusTypes.add(extendedStatusType);
          messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
          quickQuoteResult.setMessageStatus(messageStatusType);
      }
      else {
          WebServiceHelper.getInstance().setWebServiceUser(quickQuoteRequest.getUserId());

          QuickQuoteServiceManager QuickQuoteServiceManager = (QuickQuoteServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
          quickQuoteResult = QuickQuoteServiceManager.getQuickQuote(quickQuoteRequest);
      }

      return quickQuoteResult;

  }
}  