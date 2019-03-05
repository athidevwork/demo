package dti.oasis.tags.ogcachemgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.tags.ogcachemgr.OasisGridCacheManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This action class provides methods to access cached xml data.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/08/2010       James       Issue#103608 This issue pertains to Production. Unable to add
 *                              a risk with french accents in the name. The system received an
 *                              unexpected error. When the accents were removed in CIS, we were
 *                              able to add the risk. We need to have the ability to add name
 *                              with accents.
 * ---------------------------------------------------
 */
public class MaintainCacheAction extends Action {
    /**
     * Default action method.
     * <p/>
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward af =  null;
        af = loadOasisGridData(mapping, form, request, response);
        return af;
    }

    /**
     * Method that loads the cached xml grid data.
     * <p/>
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    private ActionForward loadOasisGridData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadOasisGridData", new Object[]{mapping, form, request, response});

        String cacheKey = request.getParameter("key");
        String gridId = request.getParameter("gridId");

        OasisGridCacheManager ogCacheManager = new OasisGridCacheManager();
        l.logp(Level.FINE, getClass().getName(), "loadOasisGridData", "Cache Key:" + cacheKey );
        if (ogCacheManager.isJsonGridData(cacheKey)) {
            response.setContentType("application/json");
        }
        else {
            response.setContentType("text/xml");
        }
        PrintWriter out = response.getWriter();
        String data = "";
        if (!StringUtils.isBlank(cacheKey))
        {
            data = ogCacheManager.writeData(request, gridId, cacheKey, out, true);
            out.flush();
        } else {
            throw new AppException("Cache Key is missing. Cannot load data from cache manager.");
        }

        l.logp(Level.FINE, getClass().getName(), "loadOasisGridData", "data:" + data);
        l.exiting(getClass().getName(), "loadOasisGridData", data);
        return null;
    }
}
