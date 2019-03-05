package dti.oasis.healthcheckmgr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public interface HealthCheck {

    /**
     * Method to check the health of the applications.
     * @param request
     * @param response
     * @param moduleName
     */
    public void checkHealth(HttpServletRequest request, HttpServletResponse response, String moduleName);

}
