package dti.pm.soapmgr.tritechsoft.allocatoragent.allocateaddress;

import dti.pm.soapmgr.BaseSoapParameters;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.*;

/**
 * This class provides a definition of the parameters of the AllocateAddress Web Service.
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
public class AddressWebServiceParameters extends BaseSoapParameters {

    /**
     * Public constructor.
     */
    public AddressWebServiceParameters() {
        initialize();
    }

    /**
     * Method to initialize service from configuration.
     */
    public void initialize() {

    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSourceStreet() {
        return sourceStreet;
    }

    public void setSourceStreet(String sourceStreet) {
        this.sourceStreet = sourceStreet;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getSourceZipCode() {
        return sourceZipCode;
    }

    public void setSourceZipCode(String sourceZipCode) {
        this.sourceZipCode = sourceZipCode;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * Method to set the input parameters for the Address Web Service
     * @param bodyElement
     * @param envelope
     * @throws SOAPException
     */
    public void setInputParameters(SOAPBodyElement bodyElement, SOAPEnvelope envelope) throws SOAPException {

        SOAPElement eCompanyID = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.COMPANY_ID));
        eCompanyID.addTextNode(companyID);

        SOAPElement ePassword = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.PASSWORD));
        ePassword.addTextNode(password);

        SOAPElement eSourceStreet = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.SOURCE_STREET));
        eSourceStreet.addTextNode(sourceStreet);

        SOAPElement eSourceCity = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.SOURCE_CITY));
        eSourceCity.addTextNode(sourceCity);

        SOAPElement eSourceState = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.SOURCE_STATE));
        eSourceState.addTextNode(sourceState);

        SOAPElement eSourceZipCode = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.SOURCE_ZIP_CODE));
        eSourceZipCode.addTextNode(sourceZipCode);

        SOAPElement eLineOfBusiness = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.LINE_OF_BUSINESS));
        eLineOfBusiness.addTextNode(lineOfBusiness);

        SOAPElement eEffectiveDate = bodyElement.addChildElement(envelope.createName(AddressWebServiceRequestElement.EFFECTIVE_DATE));
        eEffectiveDate.addTextNode(effectiveDate);

    }

    private String companyID;
    private String password;
    private String sourceStreet;
    private String sourceCity;
    private String sourceState;
    private String sourceZipCode;
    private String lineOfBusiness;
    private String effectiveDate;
}
