package dti.ci.certificationmgr.struts;

import dti.ci.entityadditionalmgr.EntityAdditionalFields;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.helpers.ICICertificationConstants;
import dti.ci.helpers.ICIConstants;
import dti.ci.certificationmgr.CertificationManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS certification page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 22, 2006
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/17/2007       Fred        Added calling CILinkGenerator.generateLink()
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 01/20/2011       Michael Li  Issue:116335
 * ---------------------------------------------------
*/

public class MaintainCertificationAction extends CIBaseAction {
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
        return loadCertification(mapping, form, request, response);
    }

    /**
     * load Certification data
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadCertification(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCertification", new Object[]{mapping, form, request, response});
        }
        String forwardString = "loadCertificationResult";

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
            // load the Certification list
            inputRecord.setFieldValue("entityId",entityId);
            RecordSet rs = getCertificationManager().loadCertification(inputRecord);

            if (rs.getSize() == 0) {
                MessageManager.getInstance().addInfoMessage("ci.certificationmgr.noRecords.found.error");
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

            request.setAttribute(ICICertificationConstants.DATE_OF_BIRTH,getCertificationManager().getDateOfBirth(entityId));
            String certificationBoardCode = getCertificationManager().getConstant(ICICertificationConstants.CERTIFICATION_BOARD_CODE_CONSTANT);
            if("".equals(certificationBoardCode)){
               certificationBoardCode = ICICertificationConstants.CERTIFICATION_BOARD_CODE_CONSTANT_DEFAULT; 
            }
            request.setAttribute(ICICertificationConstants.ENTITY_CLASS_CODE,certificationBoardCode);
            
            saveToken(request);
            addJsMessages();

        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR,
                "Failed to load the certification page.",
                e, request, mapping);
        }
        ActionForward af = mapping.findForward(forwardString);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCertification", af);
        }
        return af;
    }

    /**
     * save Certification Data.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward saveCertification(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCertification",
                new Object[]{mapping, form, request, response});
        }
        String forwardString = "saveCertification";

        try {
            //If the request has valid save token, then proceed with save; if not forward to load page.
            if (hasValidSaveToken(request)) {
                securePage(request, form);
                getCertificationManager().saveCertification(getInputRecordSet(request));
            }
        }catch (ValidationException v) {
            // Handle the validation exception.
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleError(AppException.UNEXPECTED_ERROR, "Failed to Save the Certification page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        l.exiting(getClass().getName(), "saveCertification", af);
        return af;
    }

    public void verifyConfig() {
        if (getCertificationManager() == null) {
            throw new ConfigurationException("The required property 'certificationManager' is missing.");
        }
    }
    
    /**
     * Set fields to read only
     *
     * @param fields OasisFields form fields map
     */
    protected void setFieldsToReadOnly(OasisFields fields) {
        Logger l = LogUtils.enterLog(getClass(), "setFieldsToReadOnly", new Object[]{fields});
        // set form field riskClassProfile_policyNo to read only
        OasisFormField formField = (OasisFormField) fields.get(ICICertificationConstants.RCP_POLICY_NO);
        formField.setIsReadOnly(true);

        // set grid fields to readOnly
        ArrayList layerFields = (ArrayList) fields.getLayerFields(ICICertificationConstants.CERTIFICATION_LIST_GRID_HEADER_LAYER);
        setFieldsToReadOnly(layerFields);
        l.exiting(getClass().getName(), "setFieldsToReadOnly");
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("cs.changes.lost.confirm");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.dateOfBirth.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.Board.Name.CertType.excl");
        MessageManager.getInstance().addJsMessage("ci.common.error.Board.Date.CertExp.Date.excl");
        MessageManager.getInstance().addJsMessage("ci.common.error.Board.Name.Date.req");
        MessageManager.getInstance().addJsMessage("ci.common.error.Board.Date.Name.req");
        MessageManager.getInstance().addJsMessage("ci.common.error.Certified.Date.CertType.req");
        MessageManager.getInstance().addJsMessage("ci.common.error.Certified.CertType.Date.req");
    }

    public CertificationManager getCertificationManager() {
        return m_certificationManager;
    }

    public void setCertificationManager(CertificationManager certificationManager) {
        this.m_certificationManager = certificationManager;
    }

    private CertificationManager m_certificationManager;
}
