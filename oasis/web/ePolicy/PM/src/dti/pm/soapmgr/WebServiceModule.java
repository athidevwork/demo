package dti.pm.soapmgr;

import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;

/**
 * This class stores Web Service Configuration.
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
public class WebServiceModule {

    public void verifyConfig() {
        Logger l = LogUtils.enterLog(getClass(), "WebServiceModule.verifyConfig");

        if (getName() == null)
            throw new ConfigurationException("The required property 'name' is missing.");
        if (getEnable() == null)
            throw new ConfigurationException("The required property 'enable' is missing.");
        if (getWebMethodPrefix() == null)
            throw new ConfigurationException("The required property 'webMethodPrefix' is missing.");
        if (getWebServiceUrl() == null)
            throw new ConfigurationException("The required property 'webServiceUrl' is missing.");

        if (getUserId() == null)
            l.info("Warning: the property 'userId' is missing for Web Service = " + getName());
        if (getPassword() == null)
            l.info("Warning: the property 'password' is missing for Web Service = " + getName());

        l.exiting(getClass().getName(), "WebServiceModule.verifyConfig");
    }

    public String toString() {
        return "WebServiceModule{" +
            ", m_name=" + m_name +
            ", m_enable=" + m_enable +
            ", m_webMethodPrefix=" + m_webMethodPrefix +
            ", m_webServiceUrl=" + m_webServiceUrl +
            ", m_userId=" + m_userId +
            ", m_password=" + m_password +
            '}';
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public void setEnable(String enable) {
        m_enable = enable;
    }

    public String getEnable() {
        return m_enable;
    }

    public String getWebMethodPrefix() {
        return m_webMethodPrefix;
    }

    public void setWebMethodPrefix(String webMethodPrefix) {
        m_webMethodPrefix = webMethodPrefix;
    }

    public String getWebServiceUrl() {
        return m_webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        m_webServiceUrl = webServiceUrl;
    }

    public String getUserId() {
        return m_userId;
    }

    public void setUserId(String userId) {
        m_userId = userId;
    }

    public String getPassword() {
        return m_password;
    }

    public void setPassword(String password) {
        m_password = password;
    }

    private String m_name;
    private String m_enable;
    private String m_webMethodPrefix;
    private String m_webServiceUrl;
    private String m_userId;
    private String m_password;

}
