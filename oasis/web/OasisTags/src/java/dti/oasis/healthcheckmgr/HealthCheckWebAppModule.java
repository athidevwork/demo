package dti.oasis.healthcheckmgr;

import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class manages the health check Modules.
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
public class HealthCheckWebAppModule {

    public void checkHealth(HttpServletRequest request, HttpServletResponse response, String appPath) {
        Logger l = LogUtils.enterLog(getClass(), "checkHealth", new String[] {appPath});

        Iterator healthCheckItems = getHealthCheckIterator();
        while (healthCheckItems.hasNext()) {
            HealthCheck healthCheck = (HealthCheck)healthCheckItems.next();
            healthCheck.checkHealth(request, response, getName());
        }

        l.exiting(getClass().getName(), "checkHealth");
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String displayName) {
        m_displayName = displayName;
    }

    public String getDisplayHeight() {
        return m_displayHeight;
    }

    public void setDisplayHeight(String displayHeight) {
        m_displayHeight = displayHeight;
    }

    public Iterator getHealthCheckIterator() {
        return m_healthCheckList.iterator();
    }

    public List getHealthCheckList() {
        return m_healthCheckList;
    }

    public void setHealthCheckList(List healthCheckList) {
        m_healthCheckList = healthCheckList;
    }

    public void verifyConfig() {
        if (getHealthCheckList() == null)
            throw new ConfigurationException("The required property 'healthCheckList' is missing.");
    }

    public String toString() {
        return "HealthCheckWebAppModule{" +
            ", m_wsdlURI=" + (m_healthCheckList == null ? null : Arrays.asList(m_healthCheckList)) +
            '}';
    }

    private List m_healthCheckList;
    private String m_name;
    private String m_displayName;
    private String m_displayHeight;

}
