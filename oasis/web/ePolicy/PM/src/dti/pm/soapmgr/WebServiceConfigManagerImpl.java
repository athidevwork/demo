package dti.pm.soapmgr;

import dti.oasis.app.ConfigurationException;
import dti.oasis.healthcheckmgr.HealthCheckWebAppModule;
import dti.oasis.util.LogUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages the the Web Services configuration.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 01, 2011
 *
 * @author fcbibire
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */

public class WebServiceConfigManagerImpl extends WebServiceConfigManager {

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public WebServiceConfigManagerImpl() {
    }

    /**
     * Method to get a Module by name of the module.
     * @param name
     * @return
     */
    public WebServiceModule getWebServiceModuleByName(String name) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWebServiceModuleByName", new Object[]{name});
        }

        WebServiceModule module = null;
        Iterator it = getModuleIterator();
        while(it.hasNext()) {
            module = (WebServiceModule)it.next();
            if(module.getName().equals(name)) {
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getWebServiceModuleByName", module);
        }

        return module;
    }

    /**
     * Method to get an Iterator to the Module list.
     * @return
     */
    public Iterator getModuleIterator() {
        return m_webServiceList.iterator();
    }

    /**
     * Method to get the list of configured web services.
     * @return
     */
    public List getWebServiceList() {
        return m_webServiceList;
    }

    /**
     * Method to set the list of configured web services.
     * @param webServiceList
     */
    public void setWebServiceList(List webServiceList) {
        m_webServiceList = webServiceList;
    }


    public void verifyConfig() {
        if (getWebServiceList() == null)
            throw new ConfigurationException("The required property 'moduleList' is missing.");
    }

    public String toString() {
        return "WebServiceConfigManagerImpl{" +
            ", m_moduleList=" + (m_webServiceList == null ? null : Arrays.asList(m_webServiceList)) +
            '}';
    }

    private List m_webServiceList;

}

