package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyRequestType;
import com.delphi_tech.ows.quotecopyservice.MedicalMalpracticeQuoteCopyResultType;
import dti.oasis.app.ApplicationContext;
import dti.oasis.request.service.WebServiceHelper;
import dti.oasis.util.LogUtils;
import dti.ows.common.MessageStatusHelper;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   12/22/2016
 *
 * @author tzeng
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/23/2016       tzeng       166929 - Initial version.
 * ---------------------------------------------------
 */

@WebService(
    name="QuoteCopyService",
    serviceName="QuoteCopyService",
    targetNamespace="http://www.delphi-tech.com/ows/QuoteCopyService",
    wsdlLocation="/wsdls/QuoteCopyService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class QuoteCopyServiceImpl {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String SERVICE_MANAGER_BEAN_NAME = "QuoteCopyServiceManager";

    @WebMethod (
        action = "quoteCopyRequest",
        operationName = "quoteCopyRequest"
    )
    @WebResult (
        name = "MedicalMalpracticeQuoteCopyResult",
        targetNamespace = "http://www.delphi-tech.com/ows/QuoteCopyService"
    )
    public MedicalMalpracticeQuoteCopyResultType quoteCopyRequest(
        @WebParam (
            name = "MedicalMalpracticeQuoteCopyRequest",
            targetNamespace = "http://www.delphi-tech.com/ows/QuoteCopyService"
        )
        MedicalMalpracticeQuoteCopyRequestType quoteCopyRequest)
    {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "quoteCopyRequest", quoteCopyRequest);
        }

        MedicalMalpracticeQuoteCopyResultType quoteCopyResult = new MedicalMalpracticeQuoteCopyResultType();
        if (quoteCopyRequest.getUserId() == null || quoteCopyRequest.getUserId().length() == 0) {
            MessageStatusType messageStatusType = new MessageStatusType();
            messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            ExtendedStatusType extendedStatusType = new ExtendedStatusType();
            extendedStatusType.setExtendedStatusDescription("UserId is required.");
            extendedStatusTypes.add(extendedStatusType);
            messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            quoteCopyResult.setMessageStatus(messageStatusType);
        }
        else {
            WebServiceHelper.getInstance().setWebServiceUser(quoteCopyRequest.getUserId());
            QuoteCopyServiceManager quoteCopyServiceManager = (QuoteCopyServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            quoteCopyServiceManager.doCopyToNewQuote(quoteCopyRequest, quoteCopyResult);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "quoteCopyRequest", quoteCopyResult);
        }

        return quoteCopyResult;
    }
}
