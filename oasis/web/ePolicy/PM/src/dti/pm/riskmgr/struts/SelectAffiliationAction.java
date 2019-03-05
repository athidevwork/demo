package dti.pm.riskmgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.struts.AddSelectIndLoadProcessor;
import dti.pm.policymgr.PolicyHeader;
import dti.pm.riskmgr.affiliationmgr.AffiliationManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Select Affiliation page
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 13, 2008
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
public class SelectAffiliationAction extends PMBaseAction {
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
        return loadAllAffiliation(mapping, form, request, response);
    }

    /**
     * This method is called when there the process parameter "loadAllAffiliation"
     * <p/>
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllAffiliation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAllAffiliation", new Object[]{mapping, form, request, response});
        String forwardString = "loadResult";
        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);
            //get policyHeader and inputRecord
            PolicyHeader policyHeader = getPolicyHeader(request, true);
            Record inputRecord = getInputRecord(request);
            RecordSet affiRs = null;
            AddSelectIndLoadProcessor processer = AddSelectIndLoadProcessor.getInstance();
            affiRs = getAffiliationManager().loadAllAffiliation(policyHeader, inputRecord, processer);


            setDataBean(request, affiRs);
            // load grid header
            loadGridHeader(request);
            //load list of values
            loadListOfValues(request, form);
            //add js messages to messagemanager for the current request
            addJsMessages();
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load affliations.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllAffiliation", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    /**
     * add js messages to messagemanager for the current request
     */
    private void addJsMessages() {
    }


    public AffiliationManager getAffiliationManager() {
        return m_affiliationManager;
    }

    public void setAffiliationManager(AffiliationManager affiliationManager) {
        m_affiliationManager = affiliationManager;
    }

    private AffiliationManager m_affiliationManager;
    protected static final String AFFILIATION_GRID_ID = "affiListGrid";
}
