package dti.pm.coveragemgr.excesspremiummgr.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.pm.busobjs.PolicyViewMode;
import dti.pm.core.struts.PMBaseAction;
import dti.pm.coveragemgr.excesspremiummgr.ExcessPremiumManager;
import dti.pm.policymgr.PolicyHeader;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action class for Mannual Excess Premium
 * <p/>
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 01, 2009
 *
 * @author Bhong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MaintainExcessPremiumAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        return loadAllExcessPremium(mapping, form, request, response);
    }

    /**
     * Method to load all excess premum.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllExcessPremium(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExcessPremium", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Rate policy
            PolicyViewMode policyViewMode = policyHeader.getPolicyIdentifier().getPolicyViewMode();
            if (!policyViewMode.isOfficial() && !policyViewMode.isEndquote()) {
                getTransactionManager().performTransactionRating(policyHeader.toRecord());
            }

            // Firstly popuate grid header labels from pm_attribute table.
            RecordSet columns = getExcessPremiumManager().getAllExcessPremiumColumn();
            loadGridHeaderAndSetLabel(request, columns);

            // Secondly get all data for manual excess premium.
            RecordSet rs = (RecordSet) request.getAttribute("gridRecordSet");
            if (rs == null) {
                rs = getExcessPremiumManager().loadAllExcessPremium(policyHeader, getInputRecord(request));
            }
            getExcessPremiumManager().calculateAllExcessPremium(rs);
            setDataBean(request, rs);
            request.setAttribute("showSummary", "Y");
            publishOutputRecord(request, rs.getSummaryRecord());
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load manual excess premium data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExcessPremium", af);
        }
        return af;
    }

    /**
     * Method to load all manual excess premium layers
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllExcessPremiumSummary(ActionMapping mapping,
                                                     ActionForm form,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllExcessPremiumSummary", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);

            // Gets policy header
            PolicyHeader policyHeader = getPolicyHeader(request);

            // Firstly popuate grid header labels from pm_attribute table.
            RecordSet columns = getExcessPremiumManager().getAllExcessPremiumColumn();
            loadGridHeaderAndSetLabel(request, columns);

            // Secondly get all data for manual excess premium.
            RecordSet rs = getExcessPremiumManager().loadAllExcessPremiumSummary(policyHeader);
            getExcessPremiumManager().calculateAllExcessPremium(rs);
            setDataBean(request, rs);
            request.setAttribute("showSummary", "N");
            publishOutputRecord(request, rs.getSummaryRecord());
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to load manual excess premium summary data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllExcessPremiumSummary", af);
        }
        return af;
    }

    /**
     * Refresh and re-calculate manual excess premium layers.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "refresh", new Object[]{mapping, form, request, response});
        }

        String forwardString = "refreshResult";
        try {
            // Secures access to the page, load the Oasis Fields without loading the LOVs,
            //   and map the input parameters to the Fields
            securePage(request, form);
            RecordSet rs = getInputRecordSet(request);
            getExcessPremiumManager().calculateAllExcessPremium(rs);
            request.setAttribute("gridRecordSet", rs);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                AppException.UNEXPECTED_ERROR, "Failed to refresh manual excess premium summary data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "refresh", af);
        }
        return af;
    }

    /**
     * Refresh and re-calculate manual excess premium layers.
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveAllExcessPremium(ActionMapping mapping,
                                              ActionForm form,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllExcessPremium", new Object[]{mapping, form, request, response});
        }

        String forwardString = "saveResult";
        RecordSet inputRecords = null;
        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                // Secure page
                securePage(request, form, false);
                // Generate input records
                inputRecords = getInputRecordSet(request);
                getExcessPremiumManager().saveAllExcessPremium(getPolicyHeader(request), getInputRecord(request), inputRecords);
            }

        }
        catch (ValidationException v) {
            // Save the recordset into the request
            request.setAttribute("gridRecordSet", inputRecords);

            // Handle the validation exception
            handleValidationException(v, request);
        }
        catch (Exception e) {
            forwardString = handleErrorPopup(
                "pm.excessPremium.save.error", "Failed to save manual excess premium summary data.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllExcessPremium", af);
        }
        return af;
    }

    /**
     * This method populate grid headers' label from columns recordSet
     *
     * @param request
     * @param columns
     */
    protected void loadGridHeaderAndSetLabel(HttpServletRequest request, RecordSet columns) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadGridHeaderAndSetLabel", new Object[]{request, columns});
        }

        OasisFields oasisFields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        List layerFields = oasisFields.getLayerFields("PM_EXCESS_PREMIUM_GH");
        int size = layerFields.size();
        for (int i = 0; i < size; i++) {
            OasisFormField field = (OasisFormField) layerFields.get(i);
            String fieldId = field.getFieldId();
            fieldId = fieldId.substring(0, fieldId.indexOf("_GH"));
            // Serach field id in columns recorset, set its label if find matched row.
            Iterator it = columns.getRecords();
            while (it.hasNext()) {
                Record rec = (Record) it.next();
                String columnName = rec.getStringValue("columnName");
                if (!StringUtils.isBlank(fieldId) && fieldId.equals(columnName)) {
                    field.setLabel(rec.getStringValue("label"));
                    break;
                }
            }
        }

        // Load grid header
        loadGridHeader(request);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadGridHeaderAndSetLabel");
        }
    }

    /**
     * Verify configuration
     */
    public void verifyConfig() {
        if (getCoverageManager() == null) {
            throw new ConfigurationException("The required property 'excessPremiumManager' is missing.");
        }
        if (getAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
        }

    }

    /**
     * Get ExcessPremiumManager
     *
     * @return ExcessPremiumManager
     */
    public ExcessPremiumManager getExcessPremiumManager() {
        return m_excessPremiumManager;
    }

    /**
     * Set ExcessPremiumManager
     *
     * @param excessPremiumManager
     */
    public void setExcessPremiumManager(ExcessPremiumManager excessPremiumManager) {
        m_excessPremiumManager = excessPremiumManager;
    }

    private ExcessPremiumManager m_excessPremiumManager;
}
