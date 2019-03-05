package dti.oasis.security;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.messagemgr.MessageManagerAdmin;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The purpose of this class is to verify that the user is authorized to access the Oasis Web Applications.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2008
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/24/2008       wer         Add excludes for j_security_check
 * 07/08/2008       sxm         Fix NULL pointer error when the user is anonymous
 * 11/23/2009       qlxie       Added ufe.do into excludes for issue 100308
 * 12/02/2009       qlxie       Added getUfeVersion.do into excludes for issue 100309
 * 03/05/2011       qlxie       Added spellcheck.bmp into excludes for issue 113648
 * 09/27/2011       bhong       Refactor logics that initialize OasisUser. Removed exclude logics.
 * 10/03/2011       bhong       Removed unused codes.
 * 10/06/2011       bhong       Send 500 error with generic messages instead of displaying debugging messages which exposes implementation details.
 * 10/07/2011       bhong       Log error for unexpected exception in "doFilter" method.
 * ---------------------------------------------------
 */
public class OasisAuthorizationServletFilter implements Filter {
    /**
     * Initialize
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        MessageManagerAdmin messageManager = ((MessageManagerAdmin) MessageManager.getInstance());
        messageManager.initResourceBundleForServletContext(filterConfig.getServletContext());
    }

    /**
     * Do filter
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "doFilter", new Object[]{servletRequest});
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            // Initialize oasis user
            ActionHelper.initializeOasisUser(request);
        }
        catch (IllegalAccessException e) {
            Principal userPrincipal = request.getUserPrincipal();
            l.logp(Level.SEVERE, getClass().getName(), "doFilter", "Failed to authorize the user '" + (userPrincipal == null ? "null" : userPrincipal.getName()) + "' for Oasis Web Applications.", e);
            // If the user is not authorized for Oasis, send a Forbidden Error (403) to the user.
            ((HttpServletResponse) servletResponse).sendError(403);
            return;
        }
        catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "doFilter", "Unexpected error in initialize Oasis User.", e);
            ((HttpServletResponse) servletResponse).sendError(500);
            return;
        }

        // The user is authorized; proceed.
        filterChain.doFilter(servletRequest, servletResponse);
        if(StringUtils.isBlank(LogUtils.getPage()))
            LogUtils.setPage("RequestURI:"+request.getRequestURI());
        l.exiting(getClass().getName(), "doFilter");
    }

    public void destroy() {
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
