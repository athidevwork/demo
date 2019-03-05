package dti.ci.licensemgr.struts;

import dti.ci.entityadditionalmgr.EntityAdditionalFields;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.helpers.ICIConstants;
import dti.ci.licensemgr.LicenseManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for License
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 17, 2012
 *
 * @author Parker
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 01/20/2011       Michael Li  Issue:116335
 * 02/17/2012       Parker      refactoring for license.
 * ---------------------------------------------------
*/
public class MaintainLicenseAction extends CIBaseAction{
    /**
     * Unspecified
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward unspecified(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        return loadLicense(mapping, form, request, response);
    }

    /**
     * loadLicense data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadLicense(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadLicense", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadLicenseResult";

        try {
            // Secures page
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            setEntityCommonInfoToRequest(request, inputRecord);

            String entityId =request.getParameter(EntityAdditionalFields.PK_PROPERTY);

            if (!FormatUtils.isLong(entityId)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityId)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ICIConstants.ENTITY_TYPE_PROPERTY);
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }
            // load the License list
            inputRecord.setFieldValue("entityId",entityId);
            RecordSet rs = getLicenseManager().loadLicense(inputRecord);

            if (rs.getSize() == 0) {
                MessageManager.getInstance().addInfoMessage("ci.licensemgr.noRecords.found.error");
            }

            new CILinkGenerator().generateLink(request, entityId, this.getClass().getName());

            setCisHeaderFields(request);
            setDataBean(request, rs);
            loadGridHeader(request);

            // Make the Summary Record available for output
            Record output = rs.getSummaryRecord();
            // Publish the input record
            publishOutputRecord(request, output);

            loadListOfValues(request, form);

            saveToken(request);
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the license page.",
                e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadLicense", af);
        }
        return af;
    }

    /**
     * save License Data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveLicense(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveLicense",
                new Object[]{mapping, form, request, response});
        }
        String forwardString = "saveLicense";

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                getLicenseManager().saveLicense(getInputRecordSet(request));
            }
        }catch (ValidationException v) {
            // Handle the validation exception.
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the License page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveLicense", af);
        return af;
    }

    public ActionForward getInitialValuesForAddLicense(ActionMapping mapping, ActionForm form,
                                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getInitialValuesForAddLicense");
        securePage(request, form);
        Record inputRecord = getInputRecord(request);
        inputRecord.setFieldValue("className", this.getClass().getName());
        Record initialValues = getLicenseManager().getInitialValuesForAddLicense(inputRecord);
        writeAjaxResponse(response, initialValues, true);
        l.exiting(getClass().getName(), "getInitialValuesForAddLicense", initialValues);
        return null;
    }

    public void verifyConfig() {
        if (getLicenseManager() == null) {
            throw new ConfigurationException("The required property 'licenseManager' is missing.");
        }
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.licenseDate.before");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
        MessageManager.getInstance().addJsMessage("ci.licensemgr.noRecords.found.error");
    }

    public LicenseManager getLicenseManager() {
        return m_licenseManager;
    }

    public void setLicenseManager(LicenseManager licenseManager) {
        this.m_licenseManager = licenseManager;
    }

    private LicenseManager m_licenseManager;
}
