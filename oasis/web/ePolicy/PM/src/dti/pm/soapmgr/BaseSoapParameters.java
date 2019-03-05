package dti.pm.soapmgr;

import javax.xml.soap.*;

/**
 * This class provides base soap parameters for the web service.
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
public class BaseSoapParameters {

    /**
     * Method to get the Web Service prefix.
     * @return
     */
    public String getWebMethodPrefix() {
        return webMethodPrefix;
    }

    /**
     * Method to set the Web Service prefix.
     * @param webMethodPrefix
     */
    public void setWebMethodPrefix(String webMethodPrefix) {
        this.webMethodPrefix = webMethodPrefix;
    }

    /**
     * Method to get the Web Service URL
     * @return
     */
    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    /**
     * Method to set the Web Service URL
     * @return
     */
    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    /**
     * Set base input parameters to be used for all web services.
     * @param bodyElement
     * @param envelope
     * @throws SOAPException
     */
    public void setInputParameters(SOAPBodyElement bodyElement, SOAPEnvelope envelope) throws SOAPException {
    }

    public void setInputParameters(SOAPElement element, SOAPEnvelope envelope) throws SOAPException {
    }

    protected String webServiceUrl;
    protected String webMethodPrefix;

}

