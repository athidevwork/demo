package dti.pm.policymgr.userviewmgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.error.ValidationException;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.policymgr.userviewmgr.UserViewManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * action class for manage additional sql
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   August 6, 2007
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

public class ManageAdditionalSqlAction extends PMBaseAction {

    /**
     * Prepare for and display the Home page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAdditionalSql(mapping, form, request, response);
    }

    /**
     * This method is called to display the page
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
    public ActionForward loadAdditionalSql(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "loadAdditionalSql",
            new Object[]{mapping, form, request, response});
        String forwardString = "success";
        securePage(request, form);

        //get default value from web work bench configuration
        Record configuredDefaultValues = getWorkbenchConfiguration().getDefaultValues(this.getClass().getName());
        if (configuredDefaultValues != null) {
            Record returnRecord = new Record();
            returnRecord.setFields(configuredDefaultValues);
            publishOutputRecord(request, returnRecord);
        }

        l.exiting(getClass().getName(), "loadAdditionalSql", forwardString);
        return mapping.findForward(forwardString);
    }

    /**
     * This method is used to validate the added addtional sql is valid or not
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateAdditionalSql(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateAdditionalSql",
            new Object[]{mapping, form, request, response});

        try {
            // Secure page
            securePage(request, form, false);
            // Get all request parameters into a record
            Record inputRecord = getInputRecord(request);
            getUserViewManager().validateAdditionalSql(inputRecord);
            writeEmptyAjaxXMLResponse(response);
        }
        catch (ValidationException e) {
                handleValidationExceptionForAjax(e, response);
            }
        catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to validate additional sql", e, response);
        }
        ActionForward af = null;
        l.exiting(getClass().getName(), "validateAdditionalSql", af);
        return af;
    }

    // getters..
    public UserViewManager getUserViewManager() {
        return m_userViewManager;
    }

    public void setUserViewManager(UserViewManager userViewManager) {
        m_userViewManager = userViewManager;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        m_workbenchConfiguration = workbenchConfiguration;
    }

    //verify spring config
    public void verifyConfig() {
        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'WorkbenchConfiguration' is missing.");
        }

        if (getUserViewManager() == null) {
            throw new ConfigurationException("The required property 'userViewManager' is missing.");
        }
    }

    private UserViewManager m_userViewManager;
    private WorkbenchConfiguration m_workbenchConfiguration;


}
