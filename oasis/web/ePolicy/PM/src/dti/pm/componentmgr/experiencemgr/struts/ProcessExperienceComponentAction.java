package dti.pm.componentmgr.experiencemgr.struts;

import dti.pm.core.struts.PMBaseAction;
import dti.pm.core.http.RequestIds;

import dti.pm.componentmgr.experiencemgr.ExperienceComponentManager;
import dti.oasis.util.LogUtils;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


/**
 * Action class for Process Experience Component.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 24, 2009
 *
 * @author gchitta
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/15/2009       gxc     Modified loadAllExperienceDetail, handling retmsg
 * ---------------------------------------------------
 */
public class ProcessExperienceComponentAction extends PMBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return displaySearchCriteria(mapping, form, request, response);
    }

    /**
     * Method to process experience component and load the policies that resulted in errors
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward loadAllExperienceDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "loadAllExperienceDetail", new Object[]{mapping, form, request, response});

        String forwardString = "loadSearchCriteria";

        RecordSet rs = new RecordSet();

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            getExperienceComponentManager().validateSearchCriteria(inputRecord);

            rs = getExperienceComponentManager().loadAllExperienceDetail(inputRecord);

            String retMsg = rs.getSummaryRecord().getFieldValue("retMsg").toString();

            if (retMsg.equalsIgnoreCase("NODATA")) {
                MessageManager.getInstance().addErrorMessage("pm.processExperienceComponent.process.nodata");
            }
            else if (retMsg.equalsIgnoreCase("FAILED")) {
                MessageManager.getInstance().addErrorMessage("pm.processExperienceComponent.process.error");
            }
            else if (retMsg.equalsIgnoreCase("SUCCESS")) {
                    MessageManager.getInstance().addInfoMessage("pm.processExperienceComponent.process.success");
            }

            // Load grid header bean
            loadGridHeader(request);

            // Sets data Bean
            setDataBean(request, rs);

        } catch (ValidationException ve) {
            // Sets data Bean
            setDataBean(request, rs);

            handleValidationException(ve, request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "", e, request, mapping);
        }

        l.exiting(getClass().getName(), "loadAllExperienceDetail", forwardString);
        return mapping.findForward(forwardString);
    }

    /**
     * Method to load the defaults
     * <p/>
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward displaySearchCriteria(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "displaySearchCriteria", new Object[]{mapping, form, request, response});

        String forwardString = "loadSearchCriteria";

        RecordSet rs = new RecordSet();

        try {
            // Secure access to the page, load the Oasis Fields without loading the LOVs,
            // and map the input parameters to the Fields
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            Record outRec = getExperienceComponentManager().getInitialValuesForProcess(inputRecord);

            // publish page field
            publishOutputRecord(request, outRec);

            // Load grid header bean
            loadGridHeader(request);

            // Sets data Bean
            setDataBean(request, rs);

        } catch (ValidationException ve) {
            // Sets data Bean
            setDataBean(request, rs);

            handleValidationException(ve, request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "", e, request, mapping);
        }

        l.exiting(getClass().getName(), "displaySearchCriteria", forwardString);
        return mapping.findForward(forwardString);
    }

    /* private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("pm.generateRenewalQuestionnaire.abandonGenerateQuestionnaire");
    }*/

    /**
     * Verify ExperienceComponentManager and anchorColumnName in spring config
     */
    public void verifyConfig() {
        if (getExperienceComponentManager() == null)
            throw new ConfigurationException("The required property 'experienceComponentManager' is missing.");
        if (getAnchorColumnName() == null)
            throw new ConfigurationException("The required property 'anchorColumnName' is missing.");
    }

    public ExperienceComponentManager getExperienceComponentManager() {
        return m_experienceComponentManager;
    }

    public void setExperienceComponentManager(ExperienceComponentManager experienceComponentManager) {
        m_experienceComponentManager = experienceComponentManager;
    }

    private ExperienceComponentManager m_experienceComponentManager;

}
