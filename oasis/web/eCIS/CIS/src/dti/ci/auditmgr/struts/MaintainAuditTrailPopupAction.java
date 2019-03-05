package dti.ci.auditmgr.struts;

import dti.ci.auditmgr.AuditTrailFields;
import dti.ci.auditmgr.AuditTrailManager;
import dti.ci.struts.action.CIBaseAction;
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
 * Action Class CIS Transaction Audit History page.
 * (C) 2006 Delphi Technology, inc. (dti)</p>
 * Date:  Aug 08, 2007
 *
 * @author FWCH
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/20/2007       FWCH        Changed one query property id
                                from POLICY_NO to SOURCE_NO
 * 04/12/2018       ylu         109179: refactor from CIAuditTrailPopup.java
 * ---------------------------------------------------
*/
public class MaintainAuditTrailPopupAction extends CIBaseAction {
    private final Logger l = LogUtils.getLogger(getClass());

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadAuditTrailBySource(mapping, form, request, response);
    }

    /**
     * search and load audit trail history data in the Popup page
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadAuditTrailBySource(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAuditTrailBySource", new Object[]{mapping,form,request,response});
        }

        String forwardString = "loadAuditTrailBySourceResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            //load audit history data into Popup page
            RecordSet rs = getAuditTrailManager().loadAuditTrailBySource(inputRecord);

            /* set data bean for Grid*/
            setDataBean(request, rs);

            loadGridHeader(request);

            publishOutputRecord(request,inputRecord);

            loadListOfValues(request, form);

            //store input parameter in request
            request.setAttribute(AuditTrailFields.HISTORY_TYPE, AuditTrailFields.getHistoryType(inputRecord));
            request.setAttribute(AuditTrailFields.SOURCE_NO, AuditTrailFields.getSourceNo(inputRecord));
            request.setAttribute(AuditTrailFields.OPERATION_TABLE, AuditTrailFields.getOperationTable(inputRecord));
            request.setAttribute(AuditTrailFields.OPERATION_ID, AuditTrailFields.getOperationId(inputRecord));

        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR,
                    "Failed to load audit history data.",
                    e, request, mapping);
            l.throwing(getClass().getName(), "loadAuditTrailBySource", e);
        }

        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAuditTrailBySource", af);
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
