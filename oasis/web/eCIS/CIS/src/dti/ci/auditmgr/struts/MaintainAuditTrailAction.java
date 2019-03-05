package dti.ci.auditmgr.struts;

import dti.ci.auditmgr.AuditTrailManager;
import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.entitymgr.EntityFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS Audit Trail page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 31, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added calling CILinkGenerator.generateLink()
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 04/09/2018       ylu         109179: refactor from CIAuditTrail.java
 * 06/27/2018       ylu         194117: CSRF security change.
 * 09/22/2018       dpang       195835: extend MaintainEntityFolderBaseAction to fix incorrect navigation link.
 * 09/28/2018       dpang       195417: Keep the case of field names for default value record.
 * ---------------------------------------------------
 */

public class MaintainAuditTrailAction extends MaintainEntityFolderBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return searchAuditTrailData(mapping, form, request, response);
    }

    /**
     * get default search criteria for loading all of audit trail data
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getInitialValuesForSearchCriteria(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInitialValuesForSearchCriteria", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);

            //get default search criteria value from workbench
            Record defaultRecord = getAuditTrailManager().getDefaultSearchCriteriaValue(this.getClass().getName());
            publishOutputRecord(request, defaultRecord);
            writeAjaxXmlResponse(response, defaultRecord, true);

        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to get default values for Audit Trial Search Criteria.", e, response);
        }

        ActionForward af = null;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInitialValuesForSearchCriteria", af);
        }
        return af;
    }

    /**
     * search and load all of audit trail data for user entered criteria
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward searchAuditTrailData(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "searchAuditTrailData", new Object[]{mapping, form, request, response});
        }

        String forwardString = "searchAuditTrailDataResult";
        try {
            securePage(request, form);
            //get inputRecord
            Record inputRecord = getInputRecord(request,true);

            //load all of audit trail data
            RecordSet rs = getAuditTrailManager().searchAuditTrailData(inputRecord);

            //* hide some menu tabs if this entity is Org type
            String entityType = EntityFields.getEntityType(inputRecord);
            if (ENTITY_TYPE_ORG_STRING.equalsIgnoreCase(entityType)) {
                checkCisFolderMenu(request);
            }

            /* set data bean for Grid*/
            setDataBean(request, rs);

            loadGridHeader(request);

            publishOutputRecord(request, inputRecord);

            //load Lov
            loadListOfValues(request, form);

            saveToken(request);

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                    "Failed to search audit trail data.",
                    e, request, mapping);
            l.throwing(getClass().getName(), "searchAuditTrailData", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "searchAuditTrailData", af);
        }
        return af;
    }

    public void verifyConfig() {
        super.verifyConfig();

        if (getAuditTrailManager() == null) {
            throw new ConfigurationException("The required property 'auditTrailManager' is missing.");
        }
    }

    public AuditTrailManager getAuditTrailManager() {
        return m_auditTrailManager;
    }

    public void setAuditTrailManager(AuditTrailManager m_auditTrailManager) {
        this.m_auditTrailManager = m_auditTrailManager;
    }

    private AuditTrailManager m_auditTrailManager;
}
