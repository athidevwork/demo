package dti.pm.policymgr.underwritermgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.policymgr.underwritermgr.UnderwriterManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Action class for adding Underwriter.
 * <p/>
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   06/05/2013
 *
 * @author Awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class AddUnderwriterAction extends PMBaseAction {

    /**
     * This method is used to display the add underwriter page.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward display(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "display",
            new Object[]{mapping, form, request, response});

        String forwardString = "display";

        try {
            securePage(request, form);
            PolicyHeader policyHeader = getPolicyHeader(request);
            Record inputRecord = getInputRecord(request);
            inputRecord.setFields(policyHeader.toRecord());
            // Get the initial values
            Record output = getUnderwriterManager().getInitialValues(inputRecord);

            publishOutputRecord(request, output);
            // Load LOVs
            loadListOfValues(request, form);
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the add underwriter page.", e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "display", af);
        return af;
    }

    /**
     * verifyConfig
     */
    public void verifyConfig() {
        if (getUnderwriterManager() == null)
            throw new ConfigurationException("The required property 'underwriterManager' is missing.");
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.maintainUnderwriter.addUnderwriter.addTeamB");
    }

    public UnderwriterManager getUnderwriterManager() {
        return m_underwriterManager;
    }

    public void setUnderwriterManager(UnderwriterManager underwriterManager) {
        m_underwriterManager = underwriterManager;
    }

    private UnderwriterManager m_underwriterManager;
}
