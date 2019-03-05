package dti.ci.struts.action;

import dti.ci.helpers.ICICertificationConstants;
import dti.ci.helpers.CISalesForceHelper;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.XMLGridHeader;
import dti.oasis.tags.XMLGridHeaderDOMLoader;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.logging.Logger;
import java.util.HashMap;

/**
 * Action Class for import contact from salesforce
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 12, 2007
 *
 * @author James
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 *
 * ---------------------------------------------------
*/
public class CISalesforceList extends CIBaseAction{
    /**
     * execute
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String methodName = "execute";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{mapping, form, request, response});
        Connection conn = null;
        String message = "";
        try {
            ActionHelper.securePage(request, getClass().getName());
            conn = ActionHelper.getConnection(request);

            OasisFields fields = ActionHelper.getFields(request);
            CISalesForceHelper helper = new CISalesForceHelper();
            String process = ActionHelper.getFormString(form, ICICertificationConstants.PROCESS_PROPERTY);

            /* Following steps are to import contact to CIS. */
            if ("IMPORT".equals(process)) {
                boolean success = helper.addNewEntity(conn, ActionHelper.getFormString(form, "contactID"));
                if (!success) {
                    message = helper.getErrorMessage();
                } else {
                    message = "Import " + helper.getCount() + " contact(s) successfully!";
                }
            }

            DisconnectedResultSet rs = helper.getSalesforceContactList(conn);
            if (helper.getErrorMessage() != null && !"IMPORT".equals(process)) {
                message = helper.getErrorMessage();
            }

            /* Set data bean */
            request.setAttribute(GRID_DATA_BEAN, rs);
            /* Set grid header */
            XMLGridHeaderDOMLoader loader = new XMLGridHeaderDOMLoader(servlet.getServletContext());
            loader.load("./CISalesforceListGrid.xml", fields, conn);
            XMLGridHeader h = loader.getHeader();
            request.setAttribute(GRID_HEADER_BEAN, h);

            // set js messages
            addJsMessages();

            request.setAttribute(MSG_PROPERTY, message);
            lggr.exiting(this.getClass().getName(), methodName, SUCCESS);
            return mapping.findForward(SUCCESS);
        } catch (Throwable e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            request.setAttribute(IOasisAction.KEY_ERROR, e);
            return mapping.findForward(ERROR);
        } finally {
            closeConnection(conn);
        }
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");

        MessageManager.getInstance().addJsMessage("ci.entity.message.contact.select");
        MessageManager.getInstance().addJsMessage("ci.entity.message.contact.import");

    }

}
