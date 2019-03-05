package dti.pm.policymgr.service;

import com.delphi_tech.ows.common.ExtendedStatusType;
import com.delphi_tech.ows.common.MessageStatusType;
import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationRequestType;
import com.delphi_tech.ows.initiateelectronicapplicationservice.InitiateElectronicApplicationResultType;
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
 * Date:   1/4/2017
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
    name="InitiateElectronicApplicationService",
    serviceName="InitiateElectronicApplicationService",
    targetNamespace="http://www.delphi-tech.com/ows/InitiateElectronicApplicationService",
    wsdlLocation="/wsdls/InitiateElectronicApplicationService.wsdl")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL,parameterStyle=SOAPBinding.ParameterStyle.BARE)
public class InitiateElectronicApplicationServiceImpl {
    private final Logger l = LogUtils.getLogger(getClass());

    public static final String SERVICE_MANAGER_BEAN_NAME = "InitiateElectronicApplicationServiceManager";

    @WebMethod(
        action = "initiateElectronicApplicationRequest",
        operationName = "initiateElectronicApplicationRequest"
    )
    @WebResult(
        name = "InitiateElectronicApplicationResult",
        targetNamespace = "http://www.delphi-tech.com/ows/InitiateElectronicApplicationService"
    )
    public InitiateElectronicApplicationResultType initiateElectronicApplicationRequest(
        @WebParam(
            name = "InitiateElectronicApplicationRequest",
            targetNamespace = "http://www.delphi-tech.com/ows/InitiateElectronicApplicationService"
        )
        InitiateElectronicApplicationRequestType initiateEAppRequest)
    {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "initiateElectronicApplicationRequest", initiateEAppRequest);
        }

        InitiateElectronicApplicationResultType initiateEAppResult = new InitiateElectronicApplicationResultType();
        if (initiateEAppRequest.getUserId() == null || initiateEAppRequest.getUserId().length() == 0) {
            MessageStatusType messageStatusType = new MessageStatusType();
            messageStatusType.setMessageStatusCode(MessageStatusHelper.STATUS_CODE_REJECTED);
            List<ExtendedStatusType> extendedStatusTypes = new ArrayList<ExtendedStatusType>();
            ExtendedStatusType extendedStatusType = new ExtendedStatusType();
            extendedStatusType.setExtendedStatusDescription("UserId is required.");
            extendedStatusTypes.add(extendedStatusType);
            messageStatusType.getExtendedStatus().addAll(extendedStatusTypes);
            initiateEAppResult.setMessageStatus(messageStatusType);
        }
        else {
            WebServiceHelper.getInstance().setWebServiceUser(initiateEAppRequest.getUserId());
            InitiateElectronicApplicationServiceManager initiateEAppServiceManager = (InitiateElectronicApplicationServiceManager) ApplicationContext.getInstance().getBean(SERVICE_MANAGER_BEAN_NAME);
            initiateEAppServiceManager.initiateEApp(initiateEAppRequest, initiateEAppResult);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "initiateElectronicApplicationRequest", initiateEAppResult);
        }
        return initiateEAppResult;
    }
}
