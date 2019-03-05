package dti.oasis.healthcheckmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.healthcheckmgr.HealthCheck;
import dti.oasis.util.LogUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class manages the health check of Web Services.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 07, 2010
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class WebServicesHealthCheckImpl implements HealthCheck {

    public void checkHealth(HttpServletRequest request, HttpServletResponse response, String moduleName) {

        Logger l = LogUtils.enterLog(getClass(), "checkHealth", new String[] {moduleName});

        // At this point this implementation does not do too much except taking the list of
        // WSDL and pass them back to JSP. However it is done for future addition of functionality.
        String[] wsdlURI = getWsdlURI();
        List wsdlList = new ArrayList();

        for (int i=0; i<wsdlURI.length; i++) {
            wsdlList.add(wsdlURI[i]);
        }

        request.setAttribute(moduleName+serviceList,wsdlList);

        l.exiting(getClass().getName(), "checkHealth");
    }

    public String[] getWsdlURI() {
        return m_wsdlURI;
    }

    public void setWsdlURI(String[] wsdlURI) {
        m_wsdlURI = wsdlURI;
    }

    public void verifyConfig() {
        if (getWsdlURI() == null)
            throw new ConfigurationException("The required property 'wsdlURI' is missing.");
    }

    public String toString() {
        return "DataSourceHealthCheckImpl{" +
            ", m_wsdlURI=" + (m_wsdlURI == null ? null : Arrays.asList(m_wsdlURI)) +
            '}';
    }

    private String[] m_wsdlURI;
    public static final String serviceList= "serviceList";

}
