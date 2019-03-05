package dti.oasis.util;

import dti.oasis.struts.IOasisAction;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.AppException;

import java.util.logging.Logger;

/**
 * CLOB Wrapper Selector class.  Use this class
 * to get a class that implements the IClobWrapperFactory interface.
 * From there you can get a class that implements the IClobWrapper interface.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 23, 2003
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/6/2004         jbe     Added Logging
 * 01/23/2007       wer     Changed use of InitialContext to using ApplicationContext;
 *
 * ---------------------------------------------------
 */

public final class ClobWrapperSelector {
    /**
     * Get an object whose class implements IClobFactory
     *
     * @return an IClobFactory object
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static IClobWrapperFactory getClobFactory() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Logger l = LogUtils.enterLog(ClobWrapperSelector.class, "getClobFactory");
        String factory = null;
        try {
            factory = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCLOBFACTORY);
        }
        catch (AppException ignore) {
        }
        // Default to the WebLogic factory
        if(StringUtils.isBlank(factory))
            factory = WeblogicOracleThinClobWrapperFactory.class.getName();

        Class cls = Class.forName(factory);

        IClobWrapperFactory fac = (IClobWrapperFactory) cls.newInstance();
        l.exiting(ClobWrapperSelector.class.getName(), "getClobFactory", fac);
        return fac;
    }
}
