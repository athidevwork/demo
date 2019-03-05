package dti.ci.educationmgr.struts;

import dti.ci.educationmgr.EducationFields;
import dti.ci.educationmgr.EducationManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CI action class for education institution popup.
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   July 20, 2007
 *
 * @author FWCH
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------------------
 *         <p/>
 *         ---------------------------------------------------------------------------
 */
public class SelectInstitutionNameAction extends CIBaseAction {
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
            String stateCode   = EducationFields.getStateCode(inputRecord);
            String countryCode = EducationFields.getCountryCode(inputRecord);
            Record record = new Record();
            EducationFields.setStateCode(record,stateCode);
            EducationFields.setCountryCode(record,countryCode); 
            List instifklist = getEducationManager().getInstitutionNameList(record);
            request.setAttribute(EducationFields.LOV_ENTITY_ROLE, instifklist);
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                    "Failed to load the selectInstitutionName page.",
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
        if (getEducationManager() == null) {
            throw new ConfigurationException("The required property 'educationManager' is missing.");
        }
    }

    public EducationManager getEducationManager() {
        return m_educationManager;
    }

    public void setEducationManager(EducationManager educationManager) {
        this.m_educationManager = educationManager;
    }

    private EducationManager m_educationManager;
}
