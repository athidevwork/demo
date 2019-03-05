package dti.oasis.idgeneratormgr.struts;

import dti.oasis.struts.BaseAction;
import dti.oasis.struts.ActionHelper;
import dti.oasis.http.RequestIds;
import dti.oasis.util.LogUtils;
import dti.oasis.util.DatabaseUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.Connection;

/**
 * This action class used for Ajax to get a new PK from db.
 * <p>(C) 2008 Delphi Technology, inc. (dti)</p>
 * Date:   May 20, 2009
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class IdGeneratorAction extends BaseAction {
    /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(RequestIds.PROCESS, "lookup");
        return getNextId(mapping, form, request, response);
    }

    /**
     * This method is used to get new pk by Ajax call.
     */
    public ActionForward getNextId(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNextId", new Object[]{mapping, form, request, response});
        }

        Connection conn = null;
        long newPk = -1;
        try {
            conn = ActionHelper.getConnection(request);
            newPk = DatabaseUtils.getNewPK(conn);
        } finally {
            DatabaseUtils.close(conn);
        }

        writeAjaxResponse(response, String.valueOf(newPk));

        l.exiting(getClass().getName(), "getNextId");
        return null;
    }
    private final Logger l = LogUtils.getLogger(getClass());
}
