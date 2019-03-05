package dti.oasis.app;

import javax.servlet.http.HttpServletRequest;

/**
 * This Interface represents a class that handles the Refresh Parameter Events.
 * Register an implementation of this interface to receive notification of the
 * Refresh Parameters Events.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 13, 2013
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
public interface RefreshParmsEventListener {

    /**
     * Refreshes the parameters.
     * @param request
     */
    void refreshParms(HttpServletRequest request);

}
