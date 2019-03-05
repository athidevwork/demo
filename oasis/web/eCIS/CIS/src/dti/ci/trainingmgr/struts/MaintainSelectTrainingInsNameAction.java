package dti.ci.trainingmgr.struts;

import dti.ci.struts.action.CIBaseAction;
import dti.ci.trainingmgr.TrainingFields;
import dti.ci.trainingmgr.TrainingManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CI action class for training institution popup.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 8, 2011
 *
 * @author jdingle
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------------------
 *         <p/>
 *         ---------------------------------------------------------------------------
 */
public class MaintainSelectTrainingInsNameAction extends CIBaseAction {
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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadInstitutionNameList(mapping, form, request, response);
    }

    /**
     * Load Institution List for entity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadInstitutionNameList(ActionMapping mapping, ActionForm form,
                                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadInstitutionNameList", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadInstitutionNameList";

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);

            // pass the parameters of state_code and country_code for getInstitutionNameList()
            String stateCode   = TrainingFields.getStateCode(inputRecord);
            String countryCode = TrainingFields.getCountryCode(inputRecord);
            Record record = new Record();
            TrainingFields.setStateCode(record,stateCode);
            TrainingFields.setCountryCode(record,countryCode);
            List instifklist = getTrainingManager().getInstitutionNameList(record);
            request.setAttribute(TrainingFields.LOV_ENTITY_ROLE, instifklist);
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                    "Failed to load the selectTrainingInstitutionName page.",
                    e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadInstitutionNameList", af);
        }
        return af;
    }

    private void addJsMessages() {

    }

    public void verifyConfig() {
        if (getTrainingManager() == null) {
            throw new ConfigurationException("The required property 'trainingManager' is missing.");
        }
    }

    public TrainingManager getTrainingManager() {
        return m_trainingManager;
    }

    public void setTrainingManager(TrainingManager trainingManager) {
        this.m_trainingManager = trainingManager;
    }

    private TrainingManager m_trainingManager;
}
