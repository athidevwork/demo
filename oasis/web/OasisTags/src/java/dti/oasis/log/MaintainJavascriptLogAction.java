package dti.oasis.log;

import dti.oasis.app.ConfigurationException;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Action class for maintain javascript log.
 * <p/>
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   September 14, 2007
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainJavascriptLogAction extends DispatchAction {
    /**
     * This method is triggered automatically when there is no process parameter
     * sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return info(mapping, form, request, response);
    }

    /**
     * Method to add javascript logs in INFO level.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward info(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "info", new Object[]{mapping, form, request, response});
        }

        try {
            if (getEnableJavascriptLogging()) {
                l.info("Javascript::" + request.getParameter("Message"));
            }
        }
        catch (Exception e) {
            // Do nothing simply log the exception
            l.severe("Error in logging javascript message, caused by:" + e.toString());
        }
        // Return null to server side
        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "info", af);
        }
        return af;
    }

    public boolean getEnableJavascriptLogging() {
        return m_enableJavascriptLogging;
    }

    public void setEnableJavascriptLogging(boolean enableJavascriptLogging) {
        m_enableJavascriptLogging = enableJavascriptLogging;
    }

    private boolean m_enableJavascriptLogging = false;
    private final Logger l = LogUtils.getLogger(getClass());
}
