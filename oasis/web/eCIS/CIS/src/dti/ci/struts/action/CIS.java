package dti.ci.struts.action;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.LogUtils;

import java.util.logging.Logger;
import java.sql.Connection;

/**
 * CIS Application Action Class.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Gerald C. Carney
 * Date:   Dec 9, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------------------------------
 *  03/29/2005      HXY         Extends CIBaseAction; Removed CISystemDAO call
 *                              for initializing DB package level variables.
 *
 * ---------------------------------------------------------------------------
 */

public class CIS extends CIBaseAction {

  /**
   * Execute method for action class.
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return ActionForward
   */
  public ActionForward execute(ActionMapping mapping, ActionForm form,
    HttpServletRequest request, HttpServletResponse response)
  {
    String methodName = "execute";
    Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                                    new Object[]{mapping, form, request, response});
    Connection conn = null;
    try {
      conn = ActionHelper.getConnection(request);
      ActionHelper.securePage(request, getClass().getName());
      lggr.exiting(this.getClass().getName(), methodName);
      return mapping.findForward("success");
    }
    catch (Exception ex) {
      try {
        lggr.throwing(this.getClass().getName(), methodName, ex);
      }
      catch (Exception ignore) { }
      request.setAttribute(IOasisAction.KEY_ERROR, ex);
      return mapping.findForward("error");
    }
    finally {
      closeConnection(conn);
    }
  }
}
