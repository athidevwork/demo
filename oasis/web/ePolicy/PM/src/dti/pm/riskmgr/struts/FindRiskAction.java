package dti.pm.riskmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.core.struts.PMBaseAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for search risk list
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 11, 2007
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

public class FindRiskAction extends PMBaseAction {


    /**
     * This method is triggered automatically when where is no process parameter
     * sent in along the requested url.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return displayPage(mapping, form, request, response);
    }

    /**
     * Method to load all risk info for requested policy.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward displayPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "displayPage", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            // Gets Policy Header
            RecordSet rs = getEmptyRecordSetForSearchRisk();
            setDataBean(request, rs);
            addJsMessages();
            Record record = new Record();
            record.setFieldValue("isDoneAvailable", YesNoFlag.N);
            publishOutputRecord(request, record);
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to display Search Risk page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "displayPage", af);
        return af;
    }

    /**
     * Method to load all risk name for requested entity name.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAllRisk(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllRisk", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {

            // Secures access to the page, loads the oasis fields without loading LOVs,
            // and map the input parameters into the fields.
            securePage(request, form);
            RecordSet rs = null;
            String txtXML = request.getParameter("txtXML");
            if (!StringUtils.isBlank(txtXML)) {
                rs = getInputRecordSet(request);
                Record record = new Record();
                if (rs.getSize() <= 0) {
                    rs = getEmptyRecordSetForSearchRisk();
                    record.setFieldValue("isDoneAvailable", YesNoFlag.N);
                    MessageManager.getInstance().addErrorMessage("pm.searchRisk.noMatchFound.error");
                }
                else {
                    record.setFieldValue("isDoneAvailable", YesNoFlag.Y);
                }
                // publish page field
                publishOutputRecord(request, record);
                setDataBean(request, rs);
            }
            // Loads list of values
            loadListOfValues(request, form);
            addJsMessages();
            // Load grid header bean
            loadGridHeader(request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load the loadAllRisk page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "loadAllRisk", af);
        return af;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public void verifyConfig() {       
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }
    //get empty reocrd set for data bean
    private RecordSet getEmptyRecordSetForSearchRisk() {
        RecordSet rs = new RecordSet();
        ArrayList fields = new ArrayList();
        fields.add("riskId");
        rs.addFieldNameCollection(fields);
        return rs;
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.searchRisk.noSearchCriteria.error");
        MessageManager.getInstance().addJsMessage("pm.searchRisk.personAndOrganization.error");
        MessageManager.getInstance().addJsMessage("pm.searchRisk.onlyFirstName.error");
    }
}
