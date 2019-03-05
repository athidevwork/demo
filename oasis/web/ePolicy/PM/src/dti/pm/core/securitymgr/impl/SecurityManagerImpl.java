package dti.pm.core.securitymgr.impl;

import dti.oasis.app.ConfigurationException;
import dti.pm.core.securitymgr.SecurityManager;
import dti.pm.core.securitymgr.dao.DataSecurityDAO;

/**
 * This class implements the SecurityManager to provide implementation of security features handled in the application.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 12, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class SecurityManagerImpl implements SecurityManager {


    /**
     * Returns a boolean that indicates whether the data is secured for the provided parameters.
     * The securityPattern is used to define 1 or more code=value combinations of the format:
     * ^code1^code1value^code2^code2value^codeN^codeNvalue^
     *
     * @param subSystem       sub-system code
     * @param securityType    security type code
     * @param securityPattern a security pattern string in the format: ^code^codevalue^
     * @return boolean true, if the data is secured; otherwise false.
     */
    public boolean isDataSecured(String subSystem, String securityType, String securityPattern) {
        return isDataSecured(subSystem, securityType, securityPattern, "0");
    }

    /**
     * Returns a boolean that indicates whether the data is secured for the provided parameters.
     *
     * @param subSystem sub-system code
     * @param securityType security type code
     * @param sourceTable source table for the sub-system
     * @param sourceId source id for the source table.
     * @return boolean true, if the data is secured; otherwise false.
     */
    public boolean isDataSecured(String subSystem, String securityType, String sourceTable, String sourceId) {
        boolean isSecured=false;
        String returnValue = getDataSecurityDAO().getUserSecurity(subSystem, securityType, sourceTable, sourceId);
        if (returnValue != "") {
            isSecured = !("READWRITE".equalsIgnoreCase(returnValue) || "RW".equalsIgnoreCase(returnValue));
        }
        return isSecured;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getDataSecurityDAO() == null)
            throw new ConfigurationException("The required property 'dataSecurityDAO' is missing.");
    }

    public SecurityManagerImpl() {
    }

    public DataSecurityDAO getDataSecurityDAO() {
        return m_dataSecurityDao;
    }

    public void setDataSecurityDAO(DataSecurityDAO securityManager) {
        this.m_dataSecurityDao = securityManager;
    }

    private DataSecurityDAO m_dataSecurityDao;
}
