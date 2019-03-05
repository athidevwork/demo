package dti.ci.correspondencemgr.struts;

import dti.ci.core.struts.MaintainEntityFolderBaseAction;
import dti.ci.correspondencemgr.CorrespondenceManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
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
 * Action Class for Correspondence
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 17, 2006
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added call to CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 04/11/2018       dzhang      Issue 109204: correspondence refactor
 * ---------------------------------------------------
*/
public class MaintainCorrespondenceAction extends MaintainEntityFolderBaseAction {
    private Logger l = LogUtils.getLogger(getClass());

    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadCorrespondence(mapping, form, request, response);
    }

    /**
     * load correspondence
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadCorrespondence(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCorrespondence", new Object[]{mapping, form, request, response});
        }

        String forwardString = SUCCESS;

        try {

            securePage(request, form);

            Record inputRecord = getInputRecord(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY);

            RecordSet corresRecordSet = (RecordSet) request.getAttribute(RequestIds.DATA_BEAN);
            if (corresRecordSet == null) {

                Record ParameterRecord = new Record();
                ParameterRecord.setFieldValue(ENTITY_ID, entityFK);
                corresRecordSet = getCorrespondenceManager().loadCorrespondenceList(ParameterRecord);
            }

            /* Set grid header */
            loadGridHeader(request);

            /* Set data bean */
            setDataBean(request, corresRecordSet);

            // publish the record
            publishOutputRecord(request, inputRecord);

            //load lov
            loadListOfValues(request, form);

            // set js messages
            addJsMessages();
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to load correspondence data.", e, request, mapping);
            l.throwing(getClass().getName(), "loadCorrespondence", e);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCorrespondence", af);
        }
        return af;
    }
    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
    }

    public void verifyConfig() {
        if (getCorrespondenceManager() == null) {
            throw new ConfigurationException("The required property 'correspondenceManager' is missing");
        }
    }

    public CorrespondenceManager getCorrespondenceManager() {
        return m_correspondenceManager;
    }

    public void setCorrespondenceManager(CorrespondenceManager correspondenceManager) {
        this.m_correspondenceManager = correspondenceManager;
    }

    private CorrespondenceManager m_correspondenceManager;
}
