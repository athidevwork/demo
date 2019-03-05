package dti.ci.relationshipmgr.struts;

import dti.ci.helpers.ICIConstants;
import dti.ci.relationshipmgr.RelationshipManager;
import dti.ci.relationshipmgr.RelationshipFields;
import dti.ci.entitysearch.listrole.bo.EntityListRoleManager;
import dti.ci.struts.action.CIBaseAction;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.*;
import dti.oasis.recordset.RecordSet;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class CIS relationship modify page.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/12/2007       kshen       Issue 63166
 *                              When saving data successful, set SAVE_SUCCESS_DESC into process
 * 09/09/2009       Jacky       Issue 97105
 *                              'Data' field in 'RelationshipModify' page should be editable
 * 03/25/2010       kshen       Changed for 101585.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 02/15/2011       Michael     Changed for 112658.
 * 03/16/2011       Michael     Changed for 118503.
 * 12/22/2011       Michael     Changed for 127479 refact this page.
 * 05/07/2012       kshen       Isuse 131290. Added method getRelationshipDesc.
 * 06/08/2017       jdingle     Issue 190314. Save performance.
 * 11/09/2018       Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
*/

public class MaintainRelationshipModifyAction extends CIBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

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
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return modifyRelationship(mapping, form, request, response);
    }
    /**
     * Handle execute of relationship modify page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward modifyRelationship(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "modifyRelationship", new Object[]{mapping, form, request, response});
        }

        String forwardString = RelationshipFields.SUCCESS;

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            String entityId = inputRecord.getStringValueDefaultEmpty(ICIConstants.PK_PROPERTY);
            if (StringUtils.isBlank(entityId)) {
                entityId = inputRecord.getStringValueDefaultEmpty(ICIConstants.ENTITY_ID);
            } else {
                inputRecord.setFieldValue(ICIConstants.ENTITY_ID, entityId);
            }

            Record outputRecord = null;
            String entityRelationId = inputRecord.getStringValueDefaultEmpty("entityRelationId");
            String entityChildId = inputRecord.getStringValueDefaultEmpty("entityChildId");
            if (!StringUtils.isBlank(entityRelationId)) {
                outputRecord = (Record) request.getAttribute("RELATION_RECORD");

                if (outputRecord == null) {
                    outputRecord = getRelationshipManager().loadRelationship(inputRecord);
                }
            } else {
                inputRecord.setFieldValue("actionClassName", this.getClass().getName());
                outputRecord = getRelationshipManager().getFieldDefaultValues(inputRecord);

                // Issue 97105
                // check system parameter for customer(KAMMCO) only, then decide whether should fetch policy No. into field 'Info'
                String ciAddlInfo1ForKammco = SysParmProvider.getInstance().getSysParm("CI_ADDL_INFO1_4_KAMM", "");

                if("Y".equals(ciAddlInfo1ForKammco)) {
                    RecordSet rs = getEntityListRoleManager().loadEntityListRoleByEntity(entityChildId);
                    StringBuffer policyNumsStr = new StringBuffer();

                    String roleTypeCode;
                    if(rs.getSize() > 0) {
                        for(int i = 0,j = 0; i < rs.getSize(); i++) {
                            roleTypeCode = rs.getRecord(i).getStringValue("ROLETYPECODE");
                            if(null != roleTypeCode && "RISK".equals(roleTypeCode)) {
                                policyNumsStr.append(j++  == 0 ? "" : ",").append(rs.getRecord(i).getStringValue("EXTERNALID"));
                            }
                        }
                        //prepare for display in page field 'Info'
                        request.setAttribute(RelationshipFields.INFO_POLICY_NO, policyNumsStr.toString());
                    }
                }
            }

            String dateOfBirth = request.getParameter(RelationshipFields.DATE_OF_BIRTH);
            if (StringUtils.isBlank(dateOfBirth)) {
                setInitInfoForRelationship(request, getRelationshipManager().getInitInfoForRelationship(inputRecord));
            }

            inputRecord.setFields(outputRecord, true);
            publishOutputRecord(request, inputRecord);

            request.setAttribute(ICIConstants.PK_PROPERTY, entityId);

            loadListOfValues(request, form);

            addJsMessages();

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load modify relationship page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "modifyRelationship", af);
        }
        return af;
    }

    /**
     * Save relationship.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "save", new Object[]{mapping, form, request, response});
        }

        String forwardString = RelationshipFields.SAVE_RESULT;
        Record inputRecord = null;

        try {
            if (isTokenValid(request, false)) {
                securePage(request, form);

                inputRecord = getInputRecord(request);
                String countryCode = inputRecord.getStringValueDefaultEmpty("countryCode");
                String configuredUsaCountryCode = SysParmProvider.getInstance().getSysParm("COUNTRY_CODE_USA", "USA");
                if (!countryCode.equalsIgnoreCase(configuredUsaCountryCode)) {
                    inputRecord.setFieldValue(RelationshipFields.ZIPCODE, inputRecord.getStringValue(RelationshipFields.ZIPCODE_FOREIGN));

                    // if selected country is not configured in system parameter, we submit a otherProvince field instead of province
                    // we need to set province from otherProvince in this case
                    if (!isCountryCodeConfigured(countryCode)) {
                        inputRecord.setFieldValue("province", inputRecord.getStringValueDefaultEmpty("otherProvince"));
                    }
                }

                RecordSet rs = (RecordSet) UserSessionManager.getInstance().getUserSession(request).get("ENTITY_RELATION_LIST");
                String entityRelationId = getRelationshipManager().saveRelationship(inputRecord, rs).getStringValue("entityRelationId");

                if (!StringUtils.isBlank(entityRelationId)) {
                    UserSessionManager.getInstance().getUserSession(request).remove("ENTITY_RELATION_LIST");
                    request.setAttribute(RelationshipFields.SAVE_SUCCESS_DESC, YesNoFlag.Y);
                }
            }
        } catch (ValidationException v) {
            request.setAttribute("RELATION_RECORD", inputRecord);
            handleValidationException(v, request);
        } catch (Exception e) {
            forwardString = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to save relationship.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "save", af);
        }
        return af;
    }

    public ActionForward validateOscRelationshipCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateOscRelationshipCode", new Object[]{mapping, form, request, response});
        }

        Record inputRecord = getInputRecord(request);
        try {
            getRelationshipManager().validateOscRelationshipCode(inputRecord);
            Record record = new Record();
            record.setFieldValue("isValid", "Y");
            writeAjaxXmlResponse(response, record);
        } catch (ValidationException ve) {
            handleValidationExceptionForAjax(ve, response);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Unable to validate osc relationship.", e, response);
        }

        l.exiting(getClass().getName(), "validateOscRelationshipCode");
        return null;
    }

    /**
     * Process get relationship desc.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward getRelationshipDesc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRelationshipDesc", new Object[]{mapping, form, request, response});
        }

        try {
            securePage(request, form);
            Record inputRecord = getInputRecord(request);
            String relationshipDesc = getRelationshipManager().getRelationshipDesc(inputRecord);
            Record record = new Record();
            record.setFieldValue(RelationshipFields.RELATIONSHIP_DESC, relationshipDesc);
            writeAjaxResponse(response, record, true);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Unable to get relationship desc.", e, response);
        }

        l.exiting(getClass().getName(), "getRelationshipDesc");
        return null;
    }

    private boolean isCountryCodeConfigured(String countryCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCountryCodeConfigured", new Object[]{countryCode});
        }

        boolean result = false;
        String configuredCountryCodes = SysParmProvider.getInstance().getSysParm("COUNTRY_CODE_CONFIG", "USA");
        String[] configuredCountryCodeArray = configuredCountryCodes.split(",");
        for (String configuredCountryCode : configuredCountryCodeArray) {
            if (configuredCountryCode.equals(countryCode)) {
                result = true;
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCountryCodeConfigured", result);
        }
        return result;
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.relation.changes");
        MessageManager.getInstance().addJsMessage("ci.common.error.date.valid");
        MessageManager.getInstance().addJsMessage("ci.common.error.birthInception.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.relation.effectiveDate");
        MessageManager.getInstance().addJsMessage("ci.common.error.birthInception.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.certifiedDate.after");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.numberPercent");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.mustPercent");
        MessageManager.getInstance().addJsMessage("ci.entity.message.zipCode.invalid");
        MessageManager.getInstance().addJsMessage("ci.entity.message.nameAndRelationType.overlap");
        MessageManager.getInstance().addJsMessage("ci.entity.message.relation.active");
        MessageManager.getInstance().addJsMessage("ci.entity.message.relation.oneActive");
        MessageManager.getInstance().addJsMessage("ci.entity.message.relation.defined");
        MessageManager.getInstance().addJsMessage("js.is.required");
    }

    /* Configuration constructor and accessor methods */

    public void verifyConfig() {
        if (getRelationshipManager() == null)
            throw new ConfigurationException("The required property 'relationshipManager' is missing.");
         if (getEntityListRoleManager() == null)
            throw new ConfigurationException("The required property 'entityListRoleManager' is missing.");
    }


    /**
     * Set up initial info of relationships of the entity.
     *
     * @param request
     * @param initInfoRecord
     */
    protected void setInitInfoForRelationship(HttpServletRequest request,Record initInfoRecord) throws Exception {
        String dateOfBirth = initInfoRecord.getStringValue(RelationshipFields.DATE_OF_BIRTH, "");
        Integer entityHasPolicy = initInfoRecord.getIntegerValue(RelationshipFields.ENTITY_HAS_POLICY);

        request.setAttribute(RelationshipFields.DATE_OF_BIRTH, dateOfBirth);
        request.setAttribute(RelationshipFields.ENTITY_HAS_POLICY, entityHasPolicy.toString());
    }

    public EntityListRoleManager getEntityListRoleManager() {
        return entityListRoleManager;
    }

    public void setEntityListRoleManager(EntityListRoleManager entityListRoleMng) {
        this.entityListRoleManager = entityListRoleMng;
    }

    public RelationshipManager getRelationshipManager() {
        return m_relationshipManager;
    }

    public void setRelationshipManager(RelationshipManager relationshipManager) {
        m_relationshipManager = relationshipManager;
    }

    private EntityListRoleManager entityListRoleManager;
    private RelationshipManager m_relationshipManager;

}
