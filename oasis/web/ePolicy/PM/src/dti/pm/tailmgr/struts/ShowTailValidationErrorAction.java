package dti.pm.tailmgr.struts;

import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.http.RequestIds;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailFields;
import dti.pm.tailmgr.TailManager;
import dti.pm.tailmgr.impl.TailProcessCode;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for Show Tail Validation Errors.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 21, 2007
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class ShowTailValidationErrorAction extends PMBaseAction {

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
        return loadAllValidationError(mapping, form, request, response);
    }


    /**
     * decline tail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllValidationError(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllValidationError",
            new Object[]{mapping, form, request, response});
        // Secure access to the page, load the Oasis Fields without loading the LOVs,
        // and map the input parameters to the Fields
        securePage(request, form);

        PolicyHeader policyHeader = getPolicyHeader(request);

        //get parameters from request
        Record inputRecord = getInputRecord(request);
        //get process code from reuquest, the value is ACCEPT/DECLINE/UPDATE/CANCEL/REACTIVE/REINSTATE
        TailProcessCode tailProcessCode = TailProcessCode.getInstance(TailFields.getProcessCode(inputRecord));


        //retrieve the validation error data from request attribute
        RecordSet validationErrors = (RecordSet)request.getAttribute(RequestIds.GRID_RECORD_SET);
        setDataBean(request,validationErrors);

        // Load component grid header
        loadGridHeader(request);

        //publish output record
        publishOutputRecord(request,validationErrors.getSummaryRecord());

        // Set visibilities of coverage part and coverage class and amount
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        OasisFormField field;
        if (!policyHeader.isCoveragePartConfigured()) {
            field = (OasisFormField) fields.get("coveragePart_GH");
            if (field != null)
                field.setIsVisible(false);
        }
        if (!policyHeader.isCoverageClassConfigured()) {
            field = (OasisFormField) fields.get("coverageClass_GH");
            if (field != null)
                field.setIsVisible(false);
        }
        if (!tailProcessCode.isAccept()) {
            field = (OasisFormField) fields.get("amount_GH");
            if (field != null)
                field.setIsVisible(false);
        }

        // add js messages
        addJsMessages();

        ActionForward af = mapping.findForward("viewValidationErrors");

        l.exiting(getClass().getName(), "loadAllValidationError", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainTail.noTailSelectedError");
    }

    public void verifyConfig() {
        if (getTailManager() == null)
            throw new ConfigurationException("The required property 'tailManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public TailManager getTailManager() {
        return m_tailManager;
    }

    public void setTailManager(TailManager tailManager) {
        m_tailManager = tailManager;
    }

    private TailManager m_tailManager;


}
