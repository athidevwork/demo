package dti.pm.policymgr.struts;

import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.pm.core.struts.PMBaseAction;
import dti.oasis.util.LogUtils;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.pm.policymgr.PolicyFields;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   September 27, 2012
 *
 * @author xnie
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/27/2012       xnie        133766 - Initial version.
 * ---------------------------------------------------
 */
public class LoadReRateOptionsAction extends PMBaseAction {

       /**
     * This method is triggered automatically when there is no process parameter sent in along the requested url.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadReRateOptions(mapping, form, request, response);
    }

    /**
     * Method to load list rerate types available
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadReRateOptions(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadReRateOptions",
            new Object[]{mapping, form, request, response});

        String forwardString = "loadResult";

        try {
            /* Secures access to the page, load the Oasis Fields without loading the LOVs,
               and map the input parameters to the Fields */
            securePage(request, form);

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            OasisFormField field = (OasisFormField) fields.get(PolicyFields.SUBMIT_AS_CODE);
            field.setRows(String.valueOf(3));
            
            /* Load LOV */
            loadListOfValues(request, form);
            // add messages for javascript
            addJsMessages();
        }
        catch (Exception e) {
            forwardString =  handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the rerate options.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadReRateOptions", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRate.onDemandNonNumber.error");
        MessageManager.getInstance().addJsMessage("pm.reRatePolicy.reRate.maxOnDemandNumber.error");
    }
}
