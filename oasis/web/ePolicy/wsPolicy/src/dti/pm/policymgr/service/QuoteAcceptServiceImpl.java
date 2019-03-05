package dti.pm.policymgr.service;


import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptResultType;
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
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   01/15/2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

@WebService(
    name = "QuoteAcceptService",
    serviceName = "QuoteAcceptService",
    targetNamespace = "http://www.delphi-tech.com/ows/QuoteAcceptService",
    wsdlLocation = "/wsdls/QuoteAcceptService.wsdl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class QuoteAcceptServiceImpl {

    public static final String SERVICE_MANAGER_BEAN_NAME = "QuoteAcceptServiceManager";

    public QuoteAcceptServiceImpl() {

    }

    /**
     * <B>medicalMalpracticeQuoteAcceptRequest</B> is exposed as a Web Service Operation
     */
    @WebMethod(action = "quoteAcceptRequest",
        operationName = "quoteAcceptRequest")
    @WebResult(name = "MedicalMalpracticeQuoteAcceptResult", targetNamespace = "http://www.delphi-tech.com/ows/QuoteAcceptService")
    public com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptResultType quoteAcceptRequest(@WebParam(name = "MedicalMalpracticeQuoteAcceptRequest", targetNamespace = "http://www.delphi-tech.com/ows/QuoteAcceptService", partName = "medicalMalpracticeQuoteAcceptRequest") com.delphi_tech.ows.quoteacceptservice.MedicalMalpracticeQuoteAcceptRequestType medicalMalpracticeQuoteAcceptRequest) {

        MedicalMalpracticeQuoteAcceptResultType medicalMalpracticeQuoteAcceptResult = new MedicalMalpracticeQuoteAcceptResultType();

        if (medicalMalpracticeQuoteAcceptRequest.getUserId() == null || medicalMalpracticeQuoteAcceptRequest.getUserId().length() == 0) {
            MessageStatusType messageStatusType = new MessageStatusType();
            messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            ExtendedStatusType extendedStatusType = new ExtendedStatusType();
            extendedStatusType.setExtendedStatusDescription("UserId is required.");
            extendedStatusTypes.add(extendedStatusType);
            messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            medicalMalpracticeQuoteAcceptResult.setMessageStatus(messageStatusType);
        }
        else {
            WebServiceHelper.getInstance().setWebServiceUser(medicalMalpracticeQuoteAcceptRequest.getUserId());

            QuoteAcceptServiceManager quoteAcceptServiceManager = (QuoteAcceptServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            medicalMalpracticeQuoteAcceptResult = quoteAcceptServiceManager.quoteAccept(medicalMalpracticeQuoteAcceptRequest);
        }

        return medicalMalpracticeQuoteAcceptResult;
    }
}
