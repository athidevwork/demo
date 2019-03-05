package dti.ci.clientmgr.struts;

import dti.ci.addressmgr.AddressFields;
import dti.ci.clientmgr.EntityAddFields;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.clientmgr.EntityAddManager;
import dti.ci.entityclassmgr.EntityClassFields;
import dti.ci.entitymgr.EntityFields;
import dti.ci.entitysearch.EntitySearchFields;
import dti.ci.helpers.ICIAddressConstants;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.ci.struts.action.CIBaseAction;
import dti.cs.ziplookupmgr.ZipLookupFields;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.error.ExpectedException;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.apache.commons.lang.WordUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dti.ci.helpers.ICIEntityConstants.*;

/**
 * Entity Add Action Class.
 * </p>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Feb 18, 2004
 */
/**
 * Revision Date   Revised By  Description
 * ----------------------------------------------------------------------------
 * 04/01/2005      HXY         Extends CIBaseAction;
 * 05/10/2005      HXY         Added system parameter "CM_CHK_VENDOR_VERIFY".
 * 11/09/2005      HXY         Added logic for adding entity on the pop up page.
 * 02/24/2006      HXY         issue 56236 - premise address type county_code not null
 * 01/11/2007      GCC         Added logging messages to execute method.
 * 02/05/2007      kshen       Support refresh LOV with AJAX (Issue 61440)
 * 10/20/2008      kshen       Changed the logic of check if it's a usa address.
 *                             Changed the position where invoke the process LOV function.
 * 12/02/2008      kshen       Add system parameter "ZIP_CODE_ENABLE", "ZIP_OVERRIDE_ADDR",
 *                             and "CS_SHOW_ZIPCD_LIST" into request.
 * 12/08/2008      Leo         change for issue 88609.
 * 03/13/2009      Fred        Keep the form values into cloned map
 * 09/08/2009      Kenney      Modified for issue 97135
 * 09/17/2009      Jacky       Modified for issue 97474
 * 08/20/2010      kenny       Issue 110474: Added TOKEN_KEY
 * 07/04/2011      Michael     Issue 117347
 * 11/10/2011      kshen       Issue 126394
 * 11/28/2011      Leo         Issue 127288
 * 03/09/2012      Michael     Issue 129509
 * 04/03/2013      kshen       Issue 141547
 * 05/17/2013      Elvin       Issue 144456: add JS message for DOB/decease today date validation
 * 08/23/2013      kshen       Issue 142975.
 * 09/06/2013      Parker      Issue 146181.Support multi classfication when add an entity
 * 10/14/2013      kshen       Issue 143051
 * 10/29/2013      ldong       Issue 138932
 * 03/12/2014      hxk         Issue 152518
 *                             1)  If the entity type is Person, populate SSN, otherwise populate TIN.
 * 03/20/2014                  Issue 151540
 *                             1) add DBA Name field's data into the form field
 * 10/30/2014      Elvin       Issue 158621: map tax id from search page to TIN no matter P or O
 * 10/30/2014      Elvin       Issue 158667: pass in country code/email address from search
 * 04/15/2015      bzhu        Issue 159178: 1. load LOV after dealWithAddedData called.
 *                                           2. remain province for other country.
 * 03/15/2016      Elvin       Issue 170036: set back entered data if duplicate entity found
 * 03/17/2016      ylu         Issue 170042
 * 04/20/2018      dpang       Issue 192743: Refactor Add Person/ Add Organization
 * 09/08/2018      dpang       Issue 195518: Call publishOutputRecord before loadListOfValues to fix field dependency issue.
 * 09/18/2018      ylu         Issue 195835: save the added new entity in Activity List.
 * ----------------------------------------------------------------------------
 */

public class EntityAddBaseAction extends CIBaseAction {

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
     * Initialize page
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
        String methodName = INIT_PAGE;
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            commonLoadPage(mapping, request, form, true);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to initialize page.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Load page after Saving entity
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward loadPageAfterSave(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        String methodName = "loadPageAfterSave";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = ICIConstants.SUCCESS;
        try {
            commonLoadPage(mapping, request, form, false);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to load page after saving entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * Save entity
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
                              HttpServletRequest request, HttpServletResponse response) {
        String methodName = "save";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{mapping, form, request, response});
        }

        String actionForward = "";
        try {
            actionForward = commonSave(request, form, mapping, true, true);
        } catch (ValidationException ve) {
            actionForward = INIT_PAGE;
            l.throwing(this.getClass().getName(), methodName, ve);
        } catch (ExpectedException ee) {
            actionForward = INIT_PAGE;
            l.throwing(getClass().getName(), methodName, ee);
        } catch (Exception e) {
            actionForward = handleError(AppException.UNEXPECTED_ERROR, "Failed to save entity.", e, request, mapping);
            l.throwing(this.getClass().getName(), methodName, e);
        }

        ActionForward af = mapping.findForward(actionForward);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, af);
        }
        return af;
    }

    /**
     * This method will be used by popup and non-popup pages to load page.
     *
     * @param mapping
     * @param request
     * @param form
     * @param isPageInit
     * @throws Exception
     */
    protected void commonLoadPage(ActionMapping mapping, HttpServletRequest request, ActionForm form, boolean isPageInit) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "commonLoadPage", new Object[]{mapping, request, form});
        }

        // Secure the page and get the fields.
        securePage(request, form);

        //Added by Fred, remove the result from session
        request.getSession(false).removeAttribute(ICIConstants.ENTITY_SELECT_RESULTS);

        Record inputRecord = null;
        if (isPageInit) {
            inputRecord = getInputRecord(request);
            //Issue 88609: if user presses "Add" button on search page, relevant search criteria will be populated to the Add page.
            setInputRecordByRequestParamFromSearchPage(request, inputRecord);

            setRequestAttr(request, inputRecord, mapping);
        } else {
            inputRecord = (Record) request.getAttribute("inputRecord");
        }

        publishOutputRecord(request, inputRecord);

        loadListOfValues(request, form);

        saveToken(request);

        addJsMessages();

        l.exiting(getClass().getName(), "commonLoadPage");
    }

    /**
     * This method will be used by popup and non-popup pages to save entity
     *
     * @param mapping
     * @param request
     * @param form
     * @throws Exception
     */
    protected String commonSave(HttpServletRequest request, ActionForm form, ActionMapping mapping, boolean shouldSaveActivityHist, boolean shouldGoToModifyPage) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "commonSave", new Object[]{request, form, mapping, shouldSaveActivityHist, shouldGoToModifyPage});
        }

        if (!hasValidSaveToken(request)) {
            return INIT_PAGE;
        }

        //Load fields in securePage. They may be used in setReusedFieldsForContinueAddEntity
        securePage(request, form);
        Record inputRecord = getInputRecord(request);

        //Some attribute values set in setRequestAttr will be overridden.
        setRequestAttr(request, inputRecord, mapping);

        EntityAddInfo addInfo = getEntityAddManager().validateAddrAndSaveEntity(inputRecord, shouldSaveActivityHist);

        if (addInfo.isEntityAdded()) {
            request.setAttribute(PK_PROPERTY, addInfo.getEntityPK());
            //ClientId can be used to search entity after leaving popup page
            request.setAttribute(EntityFields.CLIENT_ID, addInfo.getClientId());
            request.setAttribute(EntityFields.ENTITY_TYPE, inputRecord.getStringValue("entity_entityType"));
            request.setAttribute(ICIConstants.IS_NEW_VAL_PROPERTY, "N");

            if (shouldContinueAddEntity()) {
                //Issue 117347: set specified address, phone and classification fields to be published.
                inputRecord = setReusedFieldsForContinueAddEntity(inputRecord, request);
            } else {
                if (shouldGoToModifyPage) {
                    return MODIFY_PROCESS_DESC;
                }
            }
        } else {
            request.setAttribute(ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY, "Y");
            if (addInfo.isUserCanDupTaxID()) {
                request.setAttribute(ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY, "Y");
            }
        }

        request.setAttribute("inputRecord", inputRecord);
        request.setAttribute("duplicatedEntityExists", addInfo.getMergedDupsInfo().size() > 0);

        //Remove entity search criteria that may be stored in session. If save entity and go to entity modify page, then return to entity list,
        //the entity list may display inconsistently if exists previous search criteria in session.
        request.getSession(false).removeAttribute(genCriteriaIDForSession(request, EntitySearchFields.SEARCH_CRITERIA_FOR_SESSION_PREFIX));

        l.exiting(getClass().getName(), "commonSave");
        return "saveEntitySuccess";
    }

    /**
     * Set form fields values that can be populated to continue adding entity.
     *
     * @param inputRecord
     * @param request
     */
    private Record setReusedFieldsForContinueAddEntity(Record inputRecord, HttpServletRequest request) {
        String methodName = "setReusedFieldsForContinueAddEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, request});
        }

        Record record = new Record();
        OasisFields fields = ActionHelper.getFields(request);

        String fieldId = null;
        for (OasisFormField formField : fields.getAllFieldList()) {
            fieldId = formField.getFieldId();

            if (CI_ENTY_ADD_REUSE_FIELDS_ADDRESS.contains(fieldId) ||
                    CI_ENTY_ADD_REUSE_FIELDS_PHONE.contains(fieldId) ||
                    CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION.contains(fieldId)) {
                record.setFieldValue(formField.getFieldId(), inputRecord.getStringValueDefaultEmpty(formField.getFieldId()));
            } else {
                record.setFieldValue(formField.getFieldId(), formField.getDefaultValue());
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, record);
        }
        return record;
    }

    /**
     * Set form fields value.
     * <p>
     * Populate values sent from 'Entity Search' or 'Entity Select Search' pages.
     *
     * @param request
     */
    private void setInputRecordByRequestParamFromSearchPage(HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setInputRecordByRequestParamFromSearchPage", new Object[]{request});
        }

        for (Map.Entry<String, String> entry : fieldRequestParamMap.entrySet()) {
            setFieldValueByRequestParameter(request, inputRecord, entry.getKey(), entry.getValue());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setInputRecordByRequestParamFromSearchPage", inputRecord);
        }
    }

    /**
     * Set record field value.
     *
     * @param request
     * @param reqParam
     * @param inputRecord
     * @param field
     */
    private void setFieldValueByRequestParameter(HttpServletRequest request, Record inputRecord, String field, String reqParam) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldValueByRequestParameter", new Object[]{request, inputRecord, field, reqParam});
        }

        if (ActionHelper.getFields(request).get(field) == null) {
            return;
        }

        String fieldValue = null;
        if (reqParam.equalsIgnoreCase(EntityAddFields.LNM) || reqParam.equalsIgnoreCase(EntityAddFields.FNM)) {
            fieldValue = WordUtils.capitalize(request.getParameter(reqParam));
        } else {
            fieldValue = request.getParameter(reqParam);
        }

        if (!StringUtils.isBlank(fieldValue, true)) {
            if (ICIEntityConstants.FED_TAX_ID_ID.equalsIgnoreCase(field) && "P".equalsIgnoreCase(request.getParameter("entType"))) {
                inputRecord.setFieldValue(ICIEntityConstants.SSN_ID, fieldValue);
            } else {
                inputRecord.setFieldValue(field, fieldValue);
            }
        }

        l.exiting(this.getClass().getName(), "setFieldValueByRequestParameter");
    }

    private void setRequestAttr(HttpServletRequest request, Record inputRecord, ActionMapping mapping) {
        request.setAttribute(ICIConstants.INCLUDE_MULTI_ENTITY, ICIConstants.VALUE_FOR_NO);
        request.setAttribute("duplicatedEntityExists", false);

        request.setAttribute(ICIConstants.IS_NEW_VAL_PROPERTY, ICIConstants.VALUE_FOR_YES);

        request.setAttribute(ICIConstants.FORM_ACTION_PROPERTY, mapping.getPath().substring(1) + ".do");
        request.setAttribute(ICIConstants.INCLUDE_MULTI_ENTITY, ICIConstants.VALUE_FOR_NO);

        request.setAttribute(ICIEntityConstants.OK_TO_SKIP_ENTITY_DUPS_PROPERTY, "N");
        request.setAttribute(ICIEntityConstants.OK_TO_SKIP_TAX_ID_DUPS_PROPERTY, "N");

        request.setAttribute(ICIEntityConstants.VENDOR_VERIFY_SYS_PARAM, SysParmProvider.getInstance().getSysParm(ICIEntityConstants.VENDOR_VERIFY_SYS_PARAM, "N"));
        request.setAttribute(ZipLookupFields.KEY_ZIP_CODE_ENABLE, SysParmProvider.getInstance().getSysParm(ZipLookupFields.SYS_PARAM_ZIP_CODE_ENABLE, "N"));
        request.setAttribute(ZipLookupFields.KEY_ZIP_OVERRIDE_ADDR, SysParmProvider.getInstance().getSysParm(ZipLookupFields.SYS_PARAM_ZIP_OVERRIDE_ADDR, "N"));
        request.setAttribute(ZipLookupFields.KEY_CS_SHOW_ZIPCD_LIST, SysParmProvider.getInstance().getSysParm(ZipLookupFields.SYS_PARAM_CS_SHOW_ZIPCD_LIST, "N"));
        request.setAttribute("CI_REUSE_ADDRESS_CLEAR", SysParmProvider.getInstance().getSysParm(CI_REUSE_ADDRESS_CLEAR, "N"));
        request.setAttribute("CI_REUSE_PHONE_CLEAR", SysParmProvider.getInstance().getSysParm(CI_REUSE_PHONE_CLEAR, "N"));
        request.setAttribute("CI_REUSE_CLASSIFICATION_CLEAR", SysParmProvider.getInstance().getSysParm(CI_REUSE_CLASSIFICATION_CLEAR, "N"));
        request.setAttribute(ICIEntityConstants.CI_ENTITY_CONTINUE_ADD, SysParmProvider.getInstance().getSysParm(ICIEntityConstants.CI_ENTITY_CONTINUE_ADD, "N"));

        request.setAttribute("CI_ENTY_ADD_REUSE_FIELDS_ADDRESS", CI_ENTY_ADD_REUSE_FIELDS_ADDRESS);
        request.setAttribute("CI_ENTY_ADD_REUSE_FIELDS_PHONE", CI_ENTY_ADD_REUSE_FIELDS_PHONE);
        request.setAttribute("CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION", CI_ENTY_ADD_REUSE_FIELDS_CLASSIFICATION);

        request.setAttribute(ICIConstants.CHECKBOX_SPAN_PROPERTY, "2");

        setProvinceToRequestAttr(inputRecord, request);
    }

    /**
     * Backup province value if it is other country.
     *
     * @param inputRecord
     * @param request
     */
    private void setProvinceToRequestAttr(Record inputRecord, HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setProvinceToRequestAttr", new Object[]{request, inputRecord});
        }

        String countryCode = inputRecord.getStringValueDefaultEmpty("address_countryCode");

        boolean isOtherCountry = true;
        String codeUSA = SysParmProvider.getInstance().getSysParm(ICIConstants.SYS_PARA_COUNTRY_CODE_USA, AddressFields.COUNTRY_CODE_USA);
        if (codeUSA.equals(countryCode)) {
            isOtherCountry = false;
        } else {
            String countryCodeConfig = SysParmProvider.getInstance().getSysParm(ICIConstants.SYS_PARA_COUNTRY_CODE_CONFIG, AddressFields.COUNTRY_CODE_USA);

            String[] countryCodes = countryCodeConfig.split(",");
            for (int i = 0; i < countryCodes.length; i++) {
                if (countryCodes[i].equals(countryCode)) {
                    isOtherCountry = false;
                    break;
                }
            }
        }
        if (isOtherCountry) {
            request.setAttribute("provinceForOtherCountry", inputRecord.getStringValueDefaultEmpty("address_province"));
        }

        l.exiting(this.getClass().getName(), "setProvinceToRequestAttr");
    }

    private boolean shouldContinueAddEntity() {
        return YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm(ICIEntityConstants.CI_ENTITY_CONTINUE_ADD)).booleanValue();
    }

    //add js message
    private void addJsMessages() {
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.after");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.beforeToday");
        MessageManager.getInstance().addJsMessage("ci.entity.message.dateValue.afterToday");
        MessageManager.getInstance().addJsMessage("ci.entity.class.invalidNetworkDiscount");
        MessageManager.getInstance().addJsMessage("message.popup.opener.error");
    }

    @Override
    public void verifyConfig() {
        if (getEntityAddManager() == null) {
            throw new ConfigurationException("The required property 'entityAddManager' is missing.");
        }
    }

    public EntityAddManager getEntityAddManager() {
        return m_entityAddManager;
    }

    public void setEntityAddManager(EntityAddManager entityAddManager) {
        this.m_entityAddManager = entityAddManager;
    }

    private EntityAddManager m_entityAddManager;

    private static final Map<String, String> fieldRequestParamMap;

    static {
        fieldRequestParamMap = new HashMap<>();
        // lnm field set to entity_organizationName or entity_lastName
        fieldRequestParamMap.put(ICIEntityConstants.LAST_NAME_ID, EntityAddFields.LNM);
        fieldRequestParamMap.put(ICIEntityConstants.ORG_NAME_ID, EntityAddFields.LNM);
        // fnm field set to entity_firstName
        fieldRequestParamMap.put(ICIEntityConstants.FIRST_NAME_ID, EntityAddFields.FNM);
        // taxId set to entity_federalTaxID
        fieldRequestParamMap.put(ICIEntityConstants.FED_TAX_ID_ID, EntityAddFields.TAXID);
        // dob set to entity_dateOfBirth
        fieldRequestParamMap.put(ICIEntityConstants.DATE_OF_BIRTH_ID, EntityAddFields.DOB);
        // city set to address_city
        fieldRequestParamMap.put(ICIAddressConstants.CITY_ID, EntityAddFields.CITY);
        // st set to address_stateCode
        fieldRequestParamMap.put(ICIAddressConstants.STATE_ID, EntityAddFields.ST);
        // cnty set to address_countyCode
        fieldRequestParamMap.put(ICIAddressConstants.COUNTY_CODE_ID, EntityAddFields.COUNTY);
        // zip set to address_zipCode or address_zipCodeForeign
        fieldRequestParamMap.put(ICIAddressConstants.ZIP_CODE_ID, EntityAddFields.ZIP);
        fieldRequestParamMap.put(ICIAddressConstants.ZIP_CODE_FOREIGN_ID, EntityAddFields.ZIP);
        // cls set to entityClass_entityClassCode
        fieldRequestParamMap.put(EntityClassFields.ENT_CLS_CODE_ID, EntityAddFields.CLS);
        fieldRequestParamMap.put(EntityClassFields.ENT_CLS_SUB_CLASS_CODE_ID, EntityAddFields.SUB_CLS);
        fieldRequestParamMap.put(EntityClassFields.ENT_CLS_SUB_TYPE_CODE_ID, EntityAddFields.SUB_TYPE);
        fieldRequestParamMap.put(ICIEntityConstants.ENTITY_DBA_NAME_ID, EntityAddFields.DBA_NAME);
        fieldRequestParamMap.put(ICIEntityConstants.COUNTRY_CODE, EntityAddFields.COUNTRY_CODE);
        fieldRequestParamMap.put(ICIEntityConstants.EMAIL_ADDRESS_1_ID, EntityAddFields.EMAIL_ADDRESS);
    }

    protected static final String INIT_PAGE = "initPage";

}
