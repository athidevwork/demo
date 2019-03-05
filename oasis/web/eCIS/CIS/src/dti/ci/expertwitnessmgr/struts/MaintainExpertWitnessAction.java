package dti.ci.expertwitnessmgr.struts;

import dti.ci.expertwitnessmgr.ExpertWitnessFields;
import dti.ci.expertwitnessmgr.ExpertWitnessManager;
import dti.ci.helpers.CILinkGenerator;
import dti.ci.struts.action.CIBaseAction;
import dti.cs.securitymgr.AccessControlFilterManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import dti.oasis.messagemgr.MessageManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Correspondence
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 31, 2007
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * Issue  70335    eCIS/ Expert Witness      Jerry
 * 07/09/2009       Fred        Write N to response if data wasn't updated
 * 07/30/2009       hxk         Add logic to not retrieve "person" data when we have no
 *                              expert witness classifications.
 * 09/01/2009       hxk         Add no data message when we have no Exp Wit Data
 * 06/15/2010       kshen       For issue 108869.
 * 10/06/2010       wfu         111776: Replaced hardcode string with resource definition
 * 03/06/2012       Parker      130270. set CIS notes visiable for this business.
 * 04/16/2012       Parker      131444. load out the correct education list
 * ---------------------------------------------------
*/
public class MaintainExpertWitnessAction extends CIBaseAction {
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
        return loadExpertWitnessData(mapping, form, request, response);
    }

    /**
     * Load expert witness data of an entity.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadExpertWitnessData(ActionMapping mapping, ActionForm form,
                                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadExpertWitnessData", new Object[]{mapping, form, request, response});
        }

        String forwardString = "loadExpertWitnessDataResult";

        try {
            securePage(request, form);

            Record inputRecord = getInputRecord(request);
            OasisFields fields = ActionHelper.getFields(request);

            String entityFK = inputRecord.getStringValue(PK_PROPERTY, "");
            /* validate */
            if (!FormatUtils.isLong(entityFK)) {
                throw new IllegalArgumentException(new StringBuffer().append(
                        "entity FK [").append(entityFK)
                        .append("] should be a number.")
                        .toString());
            }

            String entityType = inputRecord.getStringValue(ENTITY_TYPE_PROPERTY, "");
            /* set menu beans Search & Select an entity type of  'organization'.*/
            if (entityType.charAt(0) == ENTITY_TYPE_ORG_CHAR) {
                checkCisFolderMenu(request);
            }

            Record record = new Record();
            long witnessCount = getExpertWitnessManager().getExpertWitnessCountOfEntity(inputRecord);
            if (witnessCount == 0) {
                record.setFieldValue(ENTITY_FK_PROPERTY, "-1");
            } else {
                record.setFieldValue(ENTITY_FK_PROPERTY, entityFK);
            }

            Record outputRecord = new Record();
            if (witnessCount > 0) {
                Record personInfoRecord = getExpertWitnessManager().getPersonInfo(record);
                Record personRecordWithPrefix = new Record();
                if (personInfoRecord != null) {
                    Iterator fieldNames = personInfoRecord.getFieldNames();
                    while (fieldNames.hasNext()) {
                        String fieldName = (String) fieldNames.next();
                        personRecordWithPrefix.setFieldValue(
                                ExpertWitnessFields.PERSON_FLD_PREFIX + fieldName,
                                personInfoRecord.getStringValue(fieldName));
                    }

                    outputRecord.setFields(personRecordWithPrefix);
                }
            } else {
                /* set up "no data message */
                MessageManager messageManager = MessageManager.getInstance();
                messageManager.addWarningMessage("ciExpertWitness.noData.msg");
            }

            RecordSet addressRs = getExpertWitnessManager().loadAllAddress(record);

            setDataBean(request, addressRs, ADDRESS_DATA_BEAN_NAME);
            loadGridHeader(request, ADDRESS_LAYER_FIELD_SUFFIX, ADDRESS_DATA_BEAN_NAME, ADDRESS_GRID_HEADER_LAYER);

            RecordSet phoneRs = getExpertWitnessManager().loadAllPhone(record);
            setDataBean(request, phoneRs, PHONE_DATA_BEAN_NAME);
            loadGridHeader(request, PHONE_LAYER_FIELD_SUFFIX, PHONE_DATA_BEAN_NAME, PHONE_GRID_HEADER_LAYER);

            RecordSet educationRs = getExpertWitnessManager().loadAllEducation(record);
            setDataBean(request, educationRs, EDUCATION_DATA_BEAN_NAME);
            loadGridHeader(request, EDUCATION_LAYER_FIELD_SUFFIX, EDUCATION_DATA_BEAN_NAME, EDUCATION_GRID_HEADER_LAYER);

            RecordSet classRs = getExpertWitnessManager().loadAllClassification(record);
            setDataBean(request, classRs, CLASS_DATA_BEAN_NAME);
            loadGridHeader(request, CLASS_LAYER_FIELD_SUFFIX, CLASS_DATA_BEAN_NAME, CLASS_GRID_HEADER_LAYER);

            RecordSet relationRs = getExpertWitnessManager().loadAllRelationship(record);
            setDataBean(request, relationRs, RELATION_DATA_BEAN_NAME);
            loadGridHeader(request, RELATION_LAYER_FIELD_SUFFIX, RELATION_DATA_BEAN_NAME, RELATION_GRID_HEADER_LAYER);

            RecordSet claimRs = getExpertWitnessManager().loadAllClaim(record);
            claimRs = getAccessControlFilterManager().filterRecordSetViaAccessControl(request, claimRs, "", "claimNo");
            setDataBean(request, claimRs, CLAIM_DATA_BEAN_NAME);
            loadGridHeader(request, CLAIM_LAYER_FIELD_SUFFIX, CLAIM_DATA_BEAN_NAME, CLAIM_GRID_HEADER_LAYER);

            /* Publishes out other parameters */
            request.setAttribute(ENTITY_FK_PROPERTY, inputRecord.getStringValue(PK_PROPERTY, ""));
            setEntityCommonInfoToRequest(request, inputRecord);

            publishOutputRecord(request, outputRecord);

            /* get LOV */
            loadListOfValues(request, form);

            /* Gets links for Paging */
            new CILinkGenerator().generateLink(request, entityFK, this.getClass().getName());

            // set js messages
            addJsMessages();
            setCisHeaderFields(request);  

            saveToken(request);
        } catch (Exception e) {
            forwardString = handleError(
                    AppException.UNEXPECTED_ERROR, "Failed to load Expert Witness page.", e, request, mapping);
        }

        ActionForward af = mapping.findForward(forwardString);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadExpertWitnessData", af);
        }
        return af;
    }

    /**
     * Change expert witness status.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception`
     */
    public ActionForward changeStatus(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changeStatus", new Object[]{mapping, form, request, response});
        }

        Record inputRecord = getInputRecord(request);
        try {
            getExpertWitnessManager().changeStatus(inputRecord);
            writeAjaxResponse(response, "Y");
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR,
                    "Unable to change expert witness stauts.", e, response);
        }

        return null;
    }

    @Override
    public String getAnchorColumnName() {
        if (StringUtils.isBlank(getCurrentGridId())) {
            return super.getAnchorColumnName();
        } else if (getCurrentGridId().equals(ADDRESS_DATA_BEAN_NAME)) {
            return getAddressListAnchorColumnName();
        } else if (getCurrentGridId().equals(PHONE_DATA_BEAN_NAME)) {
            return getPhoneListAnchorColumnName();
        } else if (getCurrentGridId().equals(EDUCATION_DATA_BEAN_NAME)) {
            return getEducationListAnchorColumnName();
        } else if (getCurrentGridId().equals(CLASS_DATA_BEAN_NAME)) {
            return getClassListAnchorColumnName();
        } else if (getCurrentGridId().equals(RELATION_DATA_BEAN_NAME)) {
            return getRelationListAnchorColumnName();
        } else if (getCurrentGridId().equals(CLAIM_DATA_BEAN_NAME)) {
            return getClaimListAnchorColumnName();
        } else {
            return super.getAnchorColumnName();
        }
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.rowSelect.delete");
        MessageManager.getInstance().addJsMessage("js.delete.confirmation");
        MessageManager.getInstance().addJsMessage("ci.common.error.newRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.existRecords.delete");
        MessageManager.getInstance().addJsMessage("ci.common.error.status.change");
        MessageManager.getInstance().addJsMessage("js.is.notes.notAvailable");
        MessageManager.getInstance().addJsMessage("js.refresh.lose.changes.confirmation");
    }

    public void verifyConfig() {
        if (getExpertWitnessManager() == null) {
            throw new ConfigurationException("The required property 'expertWitnessManager' is missing.");
        }
        if (getAddressListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'addressListAnchorColumnName' is missing.");
        }
        if (getPhoneListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'phoneListAnchorColumnName' is missing.");
        }
        if (getEducationListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'educationListAnchorColumnName' is missing.");
        }
        if (getClassListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'classListAnchorColumnName' is missing.");
        }
        if (getRelationListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'relationListAnchorColumnName' is missing.");
        }
        if (getClaimListAnchorColumnName() == null) {
            throw new ConfigurationException("The required property 'claimListAnchorColumnName' is missing.");
        }
    }

    public ExpertWitnessManager getExpertWitnessManager() {
        return m_expertWitnessManager;
    }

    public void setExpertWitnessManager(ExpertWitnessManager expertWitnessManager) {
        m_expertWitnessManager = expertWitnessManager;
    }

    public String getAddressListAnchorColumnName() {
        return m_addressListAnchorColumnName;
    }

    public void setAddressListAnchorColumnName(String addressListAnchorColumnName) {
        m_addressListAnchorColumnName = addressListAnchorColumnName;
    }

    public String getPhoneListAnchorColumnName() {
        return m_phoneListAnchorColumnName;
    }

    public void setPhoneListAnchorColumnName(String phoneListAnchorColumnName) {
        m_phoneListAnchorColumnName = phoneListAnchorColumnName;
    }

    public String getEducationListAnchorColumnName() {
        return m_educationListAnchorColumnName;
    }

    public void setEducationListAnchorColumnName(String educationListAnchorColumnName) {
        m_educationListAnchorColumnName = educationListAnchorColumnName;
    }

    public String getClassListAnchorColumnName() {
        return m_classListAnchorColumnName;
    }

    public void setClassListAnchorColumnName(String classListAnchorColumnName) {
        m_classListAnchorColumnName = classListAnchorColumnName;
    }

    public String getRelationListAnchorColumnName() {
        return m_relationListAnchorColumnName;
    }

    public void setRelationListAnchorColumnName(String relationListAnchorColumnName) {
        m_relationListAnchorColumnName = relationListAnchorColumnName;
    }

    public String getClaimListAnchorColumnName() {
        return m_claimListAnchorColumnName;
    }

    public void setClaimListAnchorColumnName(String claimListAnchorColumnName) {
        m_claimListAnchorColumnName = claimListAnchorColumnName;
    }
    public AccessControlFilterManager getAccessControlFilterManager() {
        return accessControlFilterManager;
    }

    public void setAccessControlFilterManager(AccessControlFilterManager accessControlFilterManager) {
        this.accessControlFilterManager = accessControlFilterManager;
    }

    private AccessControlFilterManager accessControlFilterManager;
    private ExpertWitnessManager m_expertWitnessManager;
    private String m_addressListAnchorColumnName;
    private String m_phoneListAnchorColumnName;
    private String m_educationListAnchorColumnName;
    private String m_classListAnchorColumnName;
    private String m_relationListAnchorColumnName;
    private String m_claimListAnchorColumnName;

    private static final String ADDRESS_DATA_BEAN_NAME = "addressList";
    private static final String ADDRESS_LAYER_FIELD_SUFFIX = "_AGH";
    private static final String ADDRESS_GRID_HEADER_LAYER = "EXPWTN_ADDRESS_GRIDHEADER";

    private static final String PHONE_DATA_BEAN_NAME = "phoneList";
    private static final String PHONE_LAYER_FIELD_SUFFIX = "_PGH";
    private static final String PHONE_GRID_HEADER_LAYER = "EXPWTN_PHONE_GRIDHEADER";

    private static final String EDUCATION_DATA_BEAN_NAME = "educationList";
    private static final String EDUCATION_LAYER_FIELD_SUFFIX = "_EGH";
    private static final String EDUCATION_GRID_HEADER_LAYER = "EXPWTN_EDUCATION_GRIDHEADER";

    private static final String CLASS_DATA_BEAN_NAME = "classificationList";
    private static final String CLASS_LAYER_FIELD_SUFFIX = "_CGH";
    private static final String CLASS_GRID_HEADER_LAYER = "EXPWTN_CLASSIFICATION_GRIDHEADER";

    private static final String RELATION_DATA_BEAN_NAME = "relationsList";
    private static final String RELATION_LAYER_FIELD_SUFFIX = "_RGH";
    private static final String RELATION_GRID_HEADER_LAYER = "EXPWTN_RELATIONS_GRIDHEADER";

    private static final String CLAIM_DATA_BEAN_NAME = "claimsList";
    private static final String CLAIM_LAYER_FIELD_SUFFIX = "_CLGH";
    private static final String CLAIM_GRID_HEADER_LAYER = "EXPWTN_CLAIMS_GRIDHEADER";
}
