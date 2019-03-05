package dti.oasis.security;

import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ExceptionHelper;

import java.util.logging.Logger;

/**
 * J2EE Security Selector class.  Use this class
 * to get a class that implements the IJ2EESecurityFactory interface.
 * From there you can get a class that implements the IJ2EESecurity interface.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2003
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/6/2004         jbe     Added Logging
 * 01/23/2007       wer     Changed use of InitialContext to using ApplicationContext
 * 04/09/2008       wer     Changed thrown exceptions to ConfigurationException, and removed them from the method declarations.
 * ---------------------------------------------------
 */

public final class J2EESecuritySelector {
    /**
     * Get an object whose class implements IJ2EESecurityFactory
     * @return an IJ2EESecurityFactory object
     */
    public static IJ2EESecurityFactory getJ2EESecurityFactory() {
        Logger l = LogUtils.enterLog(J2EESecuritySelector.class, "getJ2EESecurityFactory");
        String factory = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVJ2EESECFACTORY);

        IJ2EESecurityFactory fac = null;
        try {
            Class cls = Class.forName(factory);
            fac = (IJ2EESecurityFactory) cls.newInstance();
        }
        catch (Exception e) {
            ConfigurationException ce = new ConfigurationException("Failed to locate the configured " + IOasisAction.KEY_ENVJ2EESECFACTORY + ".", e);
            l.throwing(J2EESecuritySelector.class.getName(), "getJ2EESecurityFactory", ce);
            throw ce;
        }
        l.exiting(J2EESecuritySelector.class.getName(),"getJ2EESecurityFactory", fac);
        return fac;
    }
}
