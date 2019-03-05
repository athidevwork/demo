package dti.pm.tailmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.tailmgr.TailManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class matains select tail coverages
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 29, 2008
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
public class SelectTailAction extends PMBaseAction {

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
        return loadAllAvailableTail(mapping, form, request, response);
    }

    /**
     * load page to show available manual tails for selecting to add new tail coverage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllAvailableTail(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAvailableTail", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            //get parameters from request
            Record inputRecord = getInputRecord(request);

            // Get the policy header from the request
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Load the available tails for selecting
            RecordSet tailRs = getTailManager().loadAllManualTail(policyHeader, inputRecord);

            //add "no data found" message
            if (tailRs.getSize() == 0) {
                MessageManager.getInstance().addErrorMessage("pm.selectTail.noData.error");
            }
            
            setDataBean(request, tailRs);

            Record output = tailRs.getSummaryRecord();

            // Publish the output record for use by the Oasis Tags and JSP
            publishOutputRecord(request, output);

            // Load the list of values after loading the data
            loadListOfValues(request, form);

            // Load tail grid header
            loadGridHeader(request);

            //add js messages
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the select tail page.",
                e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAvailableTail", af);
        return af;
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.selectTail.noSelectTail.error");
    }


    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        if (getTailManager() == null)
            throw new ConfigurationException("The required property 'tailManager' is missing.");
    }


    public TailManager getTailManager() {
        return m_tailManager;
    }

    public void setTailManager(TailManager tailManager) {
        m_tailManager = tailManager;
    }

    private TailManager m_tailManager;
}
