package dti.pm.soapmgr.tritechsoft.allocatoragent.allocateaddress;

import dti.oasis.app.AppException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.policymgr.PolicyHeaderFields;
import dti.pm.soapmgr.WebServiceConfigManager;
import dti.pm.soapmgr.WebServiceModule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.soap.SOAPException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dti.pm.soapmgr.BaseWebService;

/**
 * This class provides an implementation of the AllocateAddress Web Service.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 31, 2011
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddressWebService extends BaseWebService {

    /**
     * Default Constructor
     * @throws javax.xml.transform.TransformerConfigurationException
     */
    private AddressWebService() {
        configureService();
    }

    /**
     * Method to get one instance of the Web Service.
     * @return
     */
    public static AddressWebService getAddressWebService() {
       if (addressWebService == null) {
           addressWebService = new AddressWebService();
       }
       return addressWebService;
    }

    /**
     * Method to configure the AddressWebService.
     */
    private void configureService() {
        WebServiceConfigManager webServiceConfigManager =
            (WebServiceConfigManager) dti.oasis.app.ApplicationContext.getInstance().getBean(BEANNAME);
        WebServiceModule webServiceModule =
            webServiceConfigManager.getWebServiceModuleByName(SERVICENAME);

        baseSoapParameters = new AddressWebServiceParameters();
        ((AddressWebServiceParameters)baseSoapParameters).setCompanyID(webServiceModule.getUserId());
        ((AddressWebServiceParameters)baseSoapParameters).setPassword(webServiceModule.getPassword());
        baseSoapParameters.setWebMethodPrefix(webServiceModule.getWebMethodPrefix());
        baseSoapParameters.setWebServiceUrl(webServiceModule.getWebServiceUrl());
        setEnable(webServiceModule.getEnable());
    }

    /**
     * Method to run the Web Service.
     * @param inputRecord
     * @return
     */
    public Record runWebService(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "runWebService", new Object[]{inputRecord});
        }

        Record outputRecord = new Record();
        initializeService();

        AddressWebServiceParameters addressWebServiceParameters =
            (AddressWebServiceParameters)addressWebService.getWebServiceParameters();

        String sourceStreet = inputRecord.getStringValue("sourceStreet");
        String sourceCity = inputRecord.getStringValue("city");
        String sourceState = inputRecord.getStringValue("stateCode");
        String zipCode = inputRecord.getStringValue("zipcode");

        addressWebServiceParameters.setSourceStreet(sourceStreet);
        addressWebServiceParameters.setSourceCity(sourceCity);
        addressWebServiceParameters.setSourceState(sourceState);
        addressWebServiceParameters.setSourceZipCode(zipCode);
        addressWebServiceParameters.setLineOfBusiness("C");
        addressWebServiceParameters.setEffectiveDate(inputRecord.getStringValue(PolicyHeaderFields.TERM_EFFECTIVE_FROM_DATE));
        String fullAddress = sourceStreet + ", " + sourceCity + ", " + sourceState + " " + zipCode;

        try {
            Document document = invoke(addressWebServiceParameters);
            outputRecord = processDocument(document);
            outputRecord.setFieldValue("returnCode", "SUCCESS");
        }
        catch (Exception e) {
            e.printStackTrace();
            String messageKey = "pm.validateAndRateTransaction.error.tax.rate.invoke";
            MessageManager.getInstance().addErrorMessage(messageKey, new String[]{fullAddress, e.getLocalizedMessage()});
            outputRecord.setFieldValue("returnCode", "FAILED");

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "processDocument", outputRecord);
            }

            return outputRecord;
        }

        String matchCode = outputRecord.getStringValue(AddressWebServiceResponseElement.MATCH_CODE);
        String errorCode = outputRecord.getStringValue(AddressWebServiceResponseElement.ERROR_CODE);
        if (!"0".equalsIgnoreCase(matchCode)) {
            String messageKey = "pm.validateAndRateTransaction.error.tax.rate.match";
            String matchDescription = outputRecord.getStringValue(AddressWebServiceResponseElement.MATCH_DESCRIPTION);
            MessageManager.getInstance().addErrorMessage(messageKey, new String[]{fullAddress, matchDescription});
            outputRecord.setFieldValue("returnCode", "FAILED");
        }
        else if (!"0".equalsIgnoreCase(errorCode)) {
            String messageKey = "pm.validateAndRateTransaction.error.tax.rate.invoke";
            String errorDescription = outputRecord.getStringValue(AddressWebServiceResponseElement.ERROR_DESCRIPTION);
            MessageManager.getInstance().addErrorMessage(messageKey, new String[]{fullAddress, errorDescription});
            outputRecord.setFieldValue("returnCode", "FAILED");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDocument", outputRecord);
        }

        return outputRecord;
    }

    /**
     * Method to process an XML Document with the Address Web Service result data
     * @param document
     * @return
     * @throws SOAPException
     * @throws TransformerException
     */
    public Record processDocument(Document document) throws SOAPException, TransformerException {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDocument", new Object[]{document});
        }

        Record record = new Record();
        Element root = document.getDocumentElement();

        if (l.isLoggable(Level.FINE)) {
            //System.out.println(getStringXmlFromNode(root));
            l.logp(Level.FINE, getClass().getName(), "processDocument", getStringXmlFromNode(root));
        }

        NodeList n1 = null;
        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.COUNTY_CODE);
        record.setFieldValue(AddressWebServiceResponseElement.COUNTY_CODE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.CITY_CODE);
        record.setFieldValue(AddressWebServiceResponseElement.CITY_CODE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.EFFECTIVE_CITY_RATE);
        record.setFieldValue(AddressWebServiceResponseElement.EFFECTIVE_CITY_RATE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.EFFECTIVE_COUNTY_RATE);
        record.setFieldValue(AddressWebServiceResponseElement.EFFECTIVE_COUNTY_RATE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.EFFECTIVE_DATE);
        record.setFieldValue(AddressWebServiceResponseElement.EFFECTIVE_DATE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.TAX_CODE);
        record.setFieldValue(AddressWebServiceResponseElement.TAX_CODE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.MATCH_CODE);
        record.setFieldValue(AddressWebServiceResponseElement.MATCH_CODE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.MATCH_DESCRIPTION);
        record.setFieldValue(AddressWebServiceResponseElement.MATCH_DESCRIPTION, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.ERROR_CODE);
        record.setFieldValue(AddressWebServiceResponseElement.ERROR_CODE, n1.item(0).getTextContent());

        n1 = root.getElementsByTagName(AddressWebServiceResponseElement.ERROR_DESCRIPTION);
        record.setFieldValue(AddressWebServiceResponseElement.ERROR_DESCRIPTION, n1.item(0).getTextContent());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDocument", record);
        }

        return record;
    }

    /**
     * Method to set the enable property of the Address Web Service.
     * @param enable
     */
    public static void setEnable(String enable) {
       m_enable = "true".equalsIgnoreCase(enable);
    }

    /**
     * Method to return true/false depending whether the Address Web Service is configured to run or not.
     * @return
     */
    public boolean isEnable() {
        return m_enable;
    }

    public String toString() {
        return "AddressWebService{" +
            " name=" + getServiceName() +
            ", enable=" + m_enable +
            '}';
    }

    /**
     * Returns Web Service Name (used by BaseWebService)
     * @return SERVICENAME
     */
    public String getServiceName() {
        return SERVICENAME;
    }

    private static AddressWebService addressWebService;
    private final static String SERVICENAME = "AllocateAddress";
    private final static String BEANNAME = "WebServiceConfigManager";

    private static boolean m_enable;

}

