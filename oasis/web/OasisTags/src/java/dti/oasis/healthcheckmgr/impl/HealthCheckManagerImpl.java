package dti.oasis.healthcheckmgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.oasis.healthcheckmgr.HealthCheckManager;
import dti.oasis.healthcheckmgr.HealthCheckWebAppModule;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

public class HealthCheckManagerImpl extends HealthCheckManager {

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public HealthCheckManagerImpl() {
    }

    /**
     * Method to get a Module by name of the module.
     * @param name
     * @return
     */
    public HealthCheckWebAppModule getModuleByName(String name) {
        HealthCheckWebAppModule module = null;
        Iterator it = getModuleIterator();
        while(it.hasNext()) {
            module = (HealthCheckWebAppModule)it.next();
            if(module.getName().equals(name)) {
                break;
            }
        }
        return module;
    }

    /**
     * Method to get an Iterator to the Module list.
     * @return
     */
    public Iterator getModuleIterator() {
        return m_moduleList.iterator();
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public List getModuleList() {
        return m_moduleList;
    }

    public void setModuleList(List moduleList) {
        m_moduleList = moduleList;
    }

    public void verifyConfig() {
        if (getName() == null)
            throw new ConfigurationException("The required property 'name' is missing.");
        if (getModuleList() == null)
            throw new ConfigurationException("The required property 'moduleList' is missing.");
    }

    public String toString() {
        return "HealthCheckManagerImpl{" +
            ", m_name=" + m_name +
            ", m_moduleList=" + (m_moduleList == null ? null : Arrays.asList(m_moduleList)) +
            '}';
    }

    private String m_name;
    private List m_moduleList;

}

