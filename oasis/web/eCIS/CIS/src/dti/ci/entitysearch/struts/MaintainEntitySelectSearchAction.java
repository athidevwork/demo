package dti.ci.entitysearch.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.helpers.ICIConstants;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.MenuBean;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action Class for Entity Select List.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 20, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ------------------------------------------------------------------------
 *         03/30/2005      HXY         Made changes due to CIEntityListHelper is not
 *         a singleton class.
 *         04/07/2005      HXY         Used OasisFields to set up grid header.
 *         05/04/2005      HXY         Role / Entity Class restriction changes.
 *         11/06/2007      Kenney    Add response for ajax lov
 *         09/09/2008      Jacky       Add account number parameter process
 *         09/03/2009      kshen       Added search paremeter FLD_ENT_REL_ENT_PK.
 *         05/13/2010      kshen       Removed the codes about setting eventName to
 *                                     output record for method peekAtSearchResult.
 *         07/13/2010       shchen      Provide search on CIS relationships and entitytype for issue 106849.
 *         08/13/2010      Joe         Set entityClientId to output record for method peekAtSearchResult.
 *         10/24/2012      Elvin       Set default values when a new entry for issue 138433.
 *         04/03/2013       ldong       issue 142971.
 *         01/27/2015       ylu         Issue 165742: support phone# wild search in popup page
 *         06/08/2016       Elvin       Issue 170396: set sourceField to page
 *         06/21/2016       dpang       Issue 170976: refactor to make the grid header fields configurable
 *         06/29/2018       ylu         Issue 194117: update for CSRF security:
 *                                                    not to saveToken for this Popup page,
 *                                                    so its main page's token will not be overriden,
 *                                                    then main page's save token validation will be pass,
 *         09/07/2018       dpang       Issue 195305: 1) Add searchWithDefaultCriteria() to be used on cases like when user clicks finder to search.
 *                                                    2) Use resetInputRecordWithLovSelectedValue() in case search criteria doesn't exist in LOV.
 *         11/09/2018       dpang       Issue 194801: In peekAtSearchResult, don't replace anchorColumn name with ID for backward-compability.
 *         ------------------------------------------------------------------------
 */

public class MaintainEntitySelectSearchAction extends MaintainEntitySearchBaseAction {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Handle unspecified action
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
        return initPage(mapping, form, request, response);
    }

    /**
     * Initialize Entity Search page
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward initPage(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String methodName = "initPage";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = LOAD_EMPTY_PAGE_FORWARD_NAME;
        try {
            /* Secure the page and get the fields. */
            securePage(request, form);

            Record inputRecord = commonOnEnteringProcess(request, form);

            request.setAttribute(ICIConstants.IS_NEW_VAL_PROPERTY, "Y");

            //Set an empty recordset
            setDataBean(request, new RecordSet());
            publishOutputRecord(request, inputRecord);

            loadListOfValues(request, form);
            addJsMessages();
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to load Entity Select Search page.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }
        ActionForward af = mapping.findForward(actionForward);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Search entity by using default value of search fields along with passed search criteria.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward searchWithDefaultCriteria(ActionMapping mapping, ActionForm form,
                                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodName = "searchWithDefaultCriteria";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = LOAD_SEARCH_RESULT_FORWARD_NAME;
        try {
            /* Secure the page and get the fields. */
            securePage(request, form);

            loadListOfValues(request, form);

            resetInputRecordWithLovSelectedValue(request);

            Record inputRecord = commonOnEnteringProcess(request, form);

            OasisFields fields = ActionHelper.getFields(request);
            List<OasisFormField> fieldList = fields.getAllFieldList();

            for (OasisFormField field : fieldList) {
                // Check if a field has default value.
                if (!StringUtils.isBlank(field.getDefaultValue())) {
                    // Check if a value passed in.
                    if (StringUtils.isBlank(inputRecord.getStringValueDefaultEmpty(field.getFieldId()))) {
                        inputRecord.setFieldValue(field.getFieldId(), field.getDefaultValue());
                    }
                }
            }

            //Retrieve the record set based on criteria
            searchEntity(request, inputRecord);
        } catch (AppException ae) {
            actionForward = LOAD_EMPTY_PAGE_FORWARD_NAME;
            l.throwing(this.getClass().getName(), methodName, ae);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to search entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Process search
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward search(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        String methodName = "search";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = LOAD_SEARCH_RESULT_FORWARD_NAME;
        try {
            /* Secure the page and get the fields. */
            securePage(request, form);

            Record inputRecord = commonOnEnteringProcess(request, form);

            loadListOfValues(request, form);

            searchEntity(request, inputRecord);
        } catch (AppException ae) {
            actionForward = LOAD_EMPTY_PAGE_FORWARD_NAME;
            l.throwing(this.getClass().getName(), methodName, ae);
        } catch (Exception e) {
            actionForward = handleErrorPopup(AppException.UNEXPECTED_ERROR, "Failed to search entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    private void searchEntity(HttpServletRequest request, Record inputRecord) throws Exception{
        String methodName = "searchEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{request, inputRecord});
        }

        RecordSet rs = getEntitySearchManager().searchEntitiesForPopup(getSearchCriteriaRecord(request, inputRecord));

        request.setAttribute(ICIConstants.LIST_DISPLAYED_PROPERTY, YesNoFlag.getInstance(rs.getSize() > 0).getName());

        setDataBean(request, rs);

        publishOutputRecord(request, inputRecord);

        loadGridHeader(request);

        addJsMessages();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
    }

    /**
     * Write peeking result out for ajax call:
     * The result consists recordCount, eventName and
     * 1):entity values (firstName,lastName etc with to the field names provided by the Search Criteria) if exact 1 record found.
     * 2):search criteria (for re-construct url if have to)  if not exact 1 record found
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    public ActionForward peekAtSearchResult(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        String methodName = "peekAtSearchResult";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        try {
            /* Secure the page and get the fields. */
            securePage(request, form);

            Record inputRecord = commonOnEnteringProcess(request, form);

            //Retrieve the record set based on criteria
            RecordSet rs = getEntitySearchManager().searchEntitiesForPopup(getSearchCriteriaRecord(request, inputRecord));
            Record outputRecord = new Record();
            outputRecord.setFieldValue("recordCount", rs.getSize());

            if (rs.getSize() == 1) {
                Record firstRecord = rs.getFirstRecord();

                for (Map.Entry<String, String> entry : getOutputRecordFieldMap().entrySet()) {
                    Object fieldValue = null;

                    if (AddressFields.COUNTRY_CODE.equals(entry.getValue())) {
                        fieldValue = firstRecord.getStringValue(entry.getValue(), AddressFields.COUNTRY_CODE_USA);
                    } else {
                        fieldValue = firstRecord.getField(entry.getValue());
                    }

                    setOutputRecordFieldValue(inputRecord, outputRecord, entry.getKey(), fieldValue);
                }
            } else {
                outputRecord.setFields(inputRecord); // so ajax can re-use the fieldname parmaters if have to.
            }

            RecordSet recordSet = new RecordSet();
            recordSet.addRecord(outputRecord);
            writeAjaxResponse(response, recordSet, true, false);
        } catch (Exception e) {
            handleErrorForAjax(AppException.UNEXPECTED_ERROR, "Failed to peek at search result.", e, response);
        }

        ActionForward af = null;
        l.exiting(getClass().getName(), methodName, af);
        return af;
    }

    private Record commonOnEnteringProcess(HttpServletRequest request, ActionForm form) throws Exception {
        String methodName = "commonOnEnteringProcess";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{request, form});
        }

        Record inputRecord = getInputRecord(request);

        // issue 170396 - set sourceField for page rules
        EntitySearchFields.setSearchCriteriaSourceField(inputRecord, inputRecord.getStringValueDefaultEmpty(EntitySearchFields.ENT_FULL_NAME_FLD_NAME_PROPERTY));

        setSearchCriteriaFieldsFromArgs(inputRecord, request);

        checkIfEnablePhoneNumberPartSearch(request);
        checkIfHideListPolicyBtn(request, inputRecord.getStringValueDefaultEmpty(EntitySearchFields.ENT_ID_FOR_POLICY_NO_PROPERTY));

        setConvertedInputRecordToRequest(inputRecord, request);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        return inputRecord;
    }

    private void setConvertedInputRecordToRequest(Record record, HttpServletRequest request) {
        String methodName = "setConvertedInputRecordToRequest";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, request});
        }

        for (String attrKey : getRequestAttributeKeyList()) {
            request.setAttribute(attrKey, ConvertFieldValue(record.getStringValueDefaultEmpty(attrKey)));
        }

        request.setAttribute(ICIConstants.CHECKBOX_SPAN_PROPERTY, "2");
        request.setAttribute(EntitySearchFields.ROLE_TYPE_CODE_ARG, "");
        request.setAttribute(EntitySearchFields.ENTITY_CLASS_CODE_ARG, "");
        l.exiting(getClass().getName(), methodName);
    }

    /**
     * Override below search criteria fields if specified in URL arguments:
     * searchCriteria_roleTypeCode,
     * searchCriteria_entityClassCode
     *
     * @param inputRecord
     * @param request
     */
    private void setSearchCriteriaFieldsFromArgs(Record inputRecord, HttpServletRequest request) {
        String methodName = "setSearchCriteriaFieldsFromArgs";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, request});
        }

        setSearchCriteriaFieldFromArg(inputRecord, request, EntitySearchFields.SEARCH_CRITERIA_ROLE_TYPE_CODE, EntitySearchFields.ROLE_TYPE_CODE_ARG, EntitySearchFields.ROLE_TYPE_CODE_ARG_READ_ONLY);
        setSearchCriteriaFieldFromArg(inputRecord, request, EntitySearchFields.SEARCH_CRITERIA_ENTITY_CLASS_CODE, EntitySearchFields.ENTITY_CLASS_CODE_ARG, EntitySearchFields.ENTITY_CLASS_CODE_ARG_READ_ONLY);
        l.exiting(getClass().getName(), methodName);
    }

    private void setSearchCriteriaFieldFromArg(Record inputRecord, HttpServletRequest request, String searchCriteriaFieldId, String fieldArg, String fieldArgReadOnly) {
        String methodName = "setSearchCriteriaFieldFromArg";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, request, searchCriteriaFieldId, fieldArg, fieldArgReadOnly});
        }

        boolean searchCriteriaFieldRO = false;
        int searchCriteriaRestrict = 0;

        String searchCriteriaFieldValue = inputRecord.getStringValueDefaultEmpty(searchCriteriaFieldId);
        String fieldArgValue = inputRecord.getStringValueDefaultEmpty(fieldArg);
        String fieldArgReadOnlyValue = inputRecord.getStringValueDefaultEmpty(fieldArgReadOnly);

        if (!StringUtils.isBlank(fieldArgValue)) {
            searchCriteriaFieldValue = fieldArgValue;

            inputRecord.setFieldValue(searchCriteriaFieldId, fieldArgValue);
            inputRecord.setFieldValue(fieldArg, "");
        }

        // if has multiple restriction
        if (!StringUtils.isBlank(searchCriteriaFieldValue) && searchCriteriaFieldValue.contains(",")) {
            searchCriteriaRestrict = EntitySearchFields.MULTIPLE;
        } else if (!StringUtils.isBlank(searchCriteriaFieldValue) && !searchCriteriaFieldValue.contains(",")) {
            searchCriteriaRestrict = EntitySearchFields.SINGLE;
            if ("Y".equals(fieldArgReadOnlyValue)) {
                searchCriteriaFieldRO = true;
            }
        }

        OasisFields fields = ActionHelper.getFields(request);
        if (fields.hasField(searchCriteriaFieldId)) {
            if (searchCriteriaRestrict == EntitySearchFields.SINGLE && searchCriteriaFieldRO) {
                fields.getField(searchCriteriaFieldId).setIsReadOnly(true);
            }
        }

        l.exiting(getClass().getName(), methodName);
    }

    private void checkIfHideListPolicyBtn(HttpServletRequest request, String entityIdForPolicyNoFieldName) {
        String methodName = "checkIfHideListPolicyBtn";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, request);
        }

        PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
        Map actionItemAL = pageBean.getActionItemGroups();
        ArrayList actionList = (ArrayList) actionItemAL.get("CI_ENTITY_SEARCH_LIST_AIG");
        MenuBean tempMenuBean = null;
        boolean isForPolicyNo = !StringUtils.isBlank(entityIdForPolicyNoFieldName);
        for (int i = 0; null != actionList && i < actionList.size(); i++) {
            tempMenuBean = (MenuBean) actionList.get(i);
            if (tempMenuBean.getId().equals("CI_ENTITY_SEARCH_LISTPOLI")) {
                tempMenuBean.setHidden(!isForPolicyNo);
            } else if (isForPolicyNo) {
                tempMenuBean.setHidden(true);
            }
        }

        l.exiting(getClass().getName(), methodName);
    }

    /**
     * Convert "null", "undefined" values to empty String.
     *
     * @param fieldValue
     * @return
     */
    private String ConvertFieldValue(String fieldValue) {
        if (StringUtils.isBlank(fieldValue) || fieldValue.equalsIgnoreCase("null") || fieldValue.equalsIgnoreCase(UNDEFINED)) {
            return "";
        }
        return fieldValue;
    }

    /**
     * Set outputRecord field by using inputRecord value as field name if exists.
     *
     * @param inputRecord
     * @param outputRecord
     * @param fieldName
     * @param value
     */
    private void setOutputRecordFieldValue(Record inputRecord, Record outputRecord, String fieldName, Object value) {
        if (inputRecord.hasStringValue(fieldName)) {
            String fieldValue = inputRecord.getStringValue(fieldName);

            if (!UNDEFINED.equalsIgnoreCase(fieldValue)) {
                outputRecord.setFieldValue(fieldValue, value);
            }
        }
    }

    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.common.error.entitySearch.lastName");
        MessageManager.getInstance().addJsMessage("ci.common.error.entitySearch.longName");
        MessageManager.getInstance().addJsMessage("ci.common.error.value.mustEntered");
        MessageManager.getInstance().addJsMessage("ci.entity.message.searchCriteria.enter");
        MessageManager.getInstance().addJsMessage("ci.entity.message.phoneNumber.invalid");
        MessageManager.getInstance().addJsMessage("cs.entity.information.error.notRecorded");
        MessageManager.getInstance().addJsMessage("ci.entity.message.noPolicy.associated");
        MessageManager.getInstance().addJsMessage("ci.entity.message.noRoles.associated");
        MessageManager.getInstance().addJsMessage("ci.common.error.row.noSelect");
        MessageManager.getInstance().addJsMessage("ci.common.error.onlyOneRow.noSelect");
        MessageManager.getInstance().addJsMessage("message.popup.opener.error");
        MessageManager.getInstance().addJsMessage("ci.entity.message.account.noSelect");
        MessageManager.getInstance().addJsMessage("ci.entity.message.policy.noSelect");
        MessageManager.getInstance().addJsMessage("ci.entity.message.organization.selectOne");
        MessageManager.getInstance().addJsMessage("ci.entity.message.person.selectOne");
        MessageManager.getInstance().addJsMessage("message.popup.opener.error");
    }

    @Override
    public void verifyConfig() {
        if (getOutputRecordFieldMap() == null) {
            throw new ConfigurationException("The required property 'outputRecordFieldMap' is missing.");
        }

        if (getRequestAttributeKeyList() == null) {
            throw new ConfigurationException("The required property 'requestAttributeKeyList' is missing.");
        }
    }

    public Map<String, String> getOutputRecordFieldMap() {
        return m_outputRecordFieldMap;
    }

    public void setOutputRecordFieldMap(Map<String, String> outputRecordFieldMap) {
        this.m_outputRecordFieldMap = outputRecordFieldMap;
    }

    public List<String> getRequestAttributeKeyList() {
        return m_requestAttributeKeyList;
    }

    public void setRequestAttributeKeyList(List<String> requestAttributeKeyList) {
        this.m_requestAttributeKeyList = requestAttributeKeyList;
    }

    private Map<String, String> m_outputRecordFieldMap;
    private List<String> m_requestAttributeKeyList;

    private static final String UNDEFINED = "undefined";
    private static final String LOAD_EMPTY_PAGE_FORWARD_NAME = "loadEmptyPage";
    private static final String LOAD_SEARCH_RESULT_FORWARD_NAME = "loadSearchResult";
}
