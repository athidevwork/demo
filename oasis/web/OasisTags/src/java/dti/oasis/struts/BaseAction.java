package dti.oasis.struts;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.error.ExpectedException;
import dti.oasis.error.ValidationException;
import dti.oasis.filter.CharacterEncodingFilter;
import dti.oasis.filter.XssFilter;
import dti.oasis.http.RequestIds;
import dti.oasis.json.JsonHelper;
import dti.oasis.messagemgr.Message;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.security.FileSanitizer;
import dti.oasis.security.FormFileWrapper;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.tags.*;
import dti.oasis.tags.jqxgrid.JqxGridHelper;
import dti.oasis.util.*;
import dti.oasis.workflowmgr.WorkflowFields;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class contains Stuts Action related methods to aid the development of Action classes.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 12, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/31/2007        wer        Enhanced to support defining Grid Column Order by the Grid Header
 * 08/21/2007       sxm         Moved writeListOfValuesAsXML() from CodeLookupAction
 * 08/30/2007       sxm         Not to set error in request in handleValidationException()
 * 04/01/2008       Joe         Add securePage() methods which are refactored from PMBaseAction.
 * 04/07/2008       Joe         Add setForwardParametersForWorkflow() method.
 * 04/24/2008       Leo         Add writeAjaxResponse, writeAjaxXmlResponse, handleErrorForAjax
 *                              and mapAnchorColumnForInitialValues functions
 * 06/16/2008       wer         Refactored to work with JDK 1.5
 * 08/01/2009       kshen       Added cache control for writeListOfValuesAsXML.
 * 10/08/2009       mgitelm     95437: Added the ability to strip off the prefix of the fieldId to match the data column
 *                              (to support the old style of defining fieldIs in eClaims/eCIS) on the Action Class level
 * 10/07/2009       fcb         96764: securePage: modified the exception block.
 * 12/04/2009       James       Add common methods from CMBaseAction, FMBaseAction, PMBaseAction
 * 02/08/2010       James       Issue#103608 This issue pertains to Production. Unable to add
 *                              a risk with french accents in the name. The system received an
 *                              unexpected error. When the accents were removed in CIS, we were
 *                              able to add the risk. We need to have the ability to add name
 *                              with accents.
 * 03/22/2010       James       Issue#105489 The latest change made in gridDisplay.jsp is causing
 *                              two problems in ePolicy:  1) Maintain Coverage page got crashed when
 *                              page reloaded with validation error  2) The order of rows in grid
 *                              was wrong when the page reloaded with validation error. Therefore,
 *                              the row number in error messages was not consistent with the row
 *                              order in the grid.
 * 03/24/2011       gxc         117554: Modified writeListOfValuesAsXML to encode "&" in the
 *                             code value with "&amp;".  Without this, when retrieving the list of values
 *                             containing "&" in the code value through AJAX, the lov list was not getting
 *                             populated correctly.
 * 04/06/2011       fcb         119324: overwrite method loadListOfValues.
 * 04/21/2011       James       Issue#119774 Create a new version of the BaseAction.getInputRecord()
 *                              that takes a "useDefaults" parameter, which will use the default value
 *                              for a field if it is not specified in the request parameters.
 * 12/09/2013       Parker      148034 - Retrieve HTML to replace LOV when loading LOV via AJAX.
 * 01/28/2014       Jyang       149204 - Modified writeListOfValuesAsXML(),added code to handle special characters.
 * 06/10/2014       Parker      155121 - Modified writeListOfValuesAsXML(),added code to replace indexof().
 * 06/20/2014       Parker      155121 - Use another implement to resolve problem one of issue 155121.
 * 08/06/2014       htwang      156257 - Revert getInputRecord() changes in 140035. It should get default value only
 *                                       when field id is not in request parameter. If users set the field to be empty,
 *                                       getInputRecord will not misuse default value to override the entered empty value.
 * 11/29/2016       dzhang      180722   add method to return json response.
 * 05/17/2017       cesar       182477 - Modified setDataBean() to encode masked fields.
 *                                       Modified getInputRecord() and getInputRecordset() to decode masked fields.
 *                                       Added encodeMaskedField() and decodeMaskedField() methods.
 * 10/02/2017       cesar       185295 - Added validateFileUpload() to validate the file been uploaded.
 *                                       Modified securePage to call validateFileUpload().
 * 12/05/2017       cesar       190017 - Added setDataBeanForDisconnectedResultSet() to check if any of the fields need to be encoded.
 * 01/26/2018       cesar       188691 - Modified getAllMaskedFields() to check for null pointer in fields object
 * 02/27/2018       cesar       191524 - Modified setDataBeanForDisconnectedResultSet() to encode all masked fields.
 * 03/02/2018       cesar       189605 - refactor to implement CSRF token. saveProcessSucceeded(), saveToken()
 * 04/17/2018       dzhang      190649 - add reload method handleError can accept forward string and return action forward.
 * 04/17/2018       cesar       192691 - created updateSessionToken() to update user session when request is an Ajax and expecting confirmation.
 * 05/15/2018       cesar       192983 - created getInputRecord() and getInputRecordSet() to set and retrieve xss filter overrides.
 * 05/15/2018       cesar       193003 - Modified hasValidSaveToken() removed isAjaxReqquest() so that it will validate the token all the time.
 * 05/18/2018       cesar       193003 - Modified hasValidSaveToken() to retrieve the csrf system parameter.
 * 07/11/2018       cesar       193446 - added updateCSRFTokenInForwardParamete() and getActionClassToken() for CSRF implementation.
 * 09/07/2018       dpang       195305 - add resetInputRecordWithLovSelectedValue().
 * 11/13/2018       wreeder     196147 - Support caching the input Record, XMLGridHeader and OasisFields in the page view cache
 *                                     - Propagate the queued page view data to the new page view data in case it's a load request and the page view id gets regenerated
 *                                     - Added support to write jqxGrid data via Ajax
 *                                     - Updated to use the JsonHelper to write JSON data
 * 12/10/2018       cesar       197486 - Added a new element <CSRF_TOKEN> inside AJAX_RESPONSE to be sent back to client.
 * ---------------------------------------------------
 */
public abstract class BaseAction extends DispatchAction {

    /**
     * The name of the Request Attribute where a Properties object is stored with the parameters that
     * should be added to a forward url.
     */
    public static final String FORWARD_PARAMETERS = RequestIds.FORWARD_PARAMETERS;
    public static final String INPUT_RECORD = "inputRecord";
    public static final String GRID_HEADER = "gridHeader";

    /**
     * Add all request parameters as fields in a Record.
     *
     * @return the Record containing fields for all request parameters
     */
    public Record getInputRecord(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInputRecord", new Object[]{});
        }

        Record inputRecord = null;
        if (hasCachedInputRecord()) {
            inputRecord = getCachedInputRecord();
        } else {
            inputRecord = new Record();

            Map mapMaskFields = ActionHelper.getMaskedPageViewCacheMap();
            request.setAttribute(RequestIds.CACHE_MASKED_FIELDS, mapMaskFields);

            Map xssOverridesFieldsMap = ActionHelper.getXssOverridesPageViewCacheMap();
            //store it in the request so that in can be used in XssFilter.
            request.setAttribute(RequestIds.OASIS_XSS_OVERRIDES_FIELDS, xssOverridesFieldsMap);

            // Add all request parameters, except the textXML, to the inputRecord Record
            Enumeration en = request.getParameterNames();
            while (en.hasMoreElements()) {
                String paramName = (String) en.nextElement();
                if (!paramName.equals(RequestIds.TEXT_XML)) {

                    // Add the request parameter as a field in the Summary Record

                    String[] values = request.getParameterValues(paramName);
                    if (values.length > 1) {

                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "getInputRecord", "Processing " + paramName + " as a MultiValueField");
                        }

                        MultiValueField mvf = new MultiValueField();
                        for (int i = 0; i < values.length; i++) {
                            mvf.addValue(values[i]);
                        }
                        inputRecord.setField(paramName, mvf);
                    } else {
                        Object o = ActionHelper.decodeMaskedField(paramName, values[0], mapMaskFields);
                        inputRecord.setFieldValue(paramName, o);
                    }
                }
            }
            RequestStorageManager.getInstance().set(INPUT_RECORD, inputRecord);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInputRecord", inputRecord);
        }
        return inputRecord;
    }

    /**
     * Add all request parameters as fields in a Record.
     *
     * @param request
     * @param useDefaultValue use default value if field id is not in request parameter
     * @return the Record containing fields for all request parameters
     */
    protected Record getInputRecord(HttpServletRequest request, boolean useDefaultValue) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInputRecord", new Object[]{useDefaultValue});
        }
        Record inputRecord = getInputRecord(request);
        if (useDefaultValue && request.getAttribute(LOADED_DEFAULT_VALUES_FOR_INPUT_RECORD) == null) {
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            if (fields != null) {
                try {
                    for (OasisFormField field : fields.getAllFieldList()) {
                        if (!inputRecord.hasFieldValue(field.getFieldId())
                                && !StringUtils.isBlank(field.getDefaultValue())) {
                            inputRecord.setFieldValue(field.getFieldId(), field.getDefaultValue());
                        }
                    }
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to get default value from OasisFields.", e);
                    l.throwing(getClass().getName(), "getInputRecord", ae);
                    throw ae;
                }
            }
            // get default value for only once
            request.setAttribute(LOADED_DEFAULT_VALUES_FOR_INPUT_RECORD, true);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInputRecord", inputRecord);
        }
        return inputRecord;
    }

    /**
     * Get the Input Record containing all request parameters as fields.
     * Will look in the PageView cache for the input record. If not found in the cache, will load it from the request and store it in the PageView cache.
     * @param request
     * @return the input record
     */
    protected Record getInputRecordUsingPageViewData(HttpServletRequest request) {
        return getInputRecordUsingPageViewData(request, false);
    }

    /**
     * Get the Input Record containing all request parameters as fields.
     * Will look in the PageView cache for the input record. If not found in the cache, will load it from the request  and store it in the PageView cache.
     * When createCopy is set to true, will create a copy of the record before storing it in the PageView cache.
     * @param request
     * @param createCopy
     * @return the input record (not the copy if one is created)
     */
    protected Record getInputRecordUsingPageViewData(HttpServletRequest request, boolean createCopy) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInputRecordUsingPageViewData", new Object[]{createCopy});
        }

        Record inputRecord = (Record) getFromPageViewData(INPUT_RECORD);
        if (null == inputRecord) {
            inputRecord = getInputRecord(request);
        }
        else {
            RequestStorageManager.getInstance().set(INPUT_RECORD, inputRecord);
        }

        Record rec = createCopy ? new Record().setFields(inputRecord) : inputRecord;
        addToPageViewData(INPUT_RECORD, rec);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getInputRecordUsingPageViewData", inputRecord);
        }
        return inputRecord;
    }

    protected Record removeInputRecordFromPageViewData() {
        l.entering(getClass().getName(), "removeInputRecordFromPageViewData");

        Record rec = (Record) removeFromPageViewData(INPUT_RECORD);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeInputRecordFromPageViewData", rec);
        }
        return rec;
    }

    /**
     * Build a RecordSet from the request parameters.
     * Add all request parameters, except the textXML, as the SummaryRecord
     * Build the input records from the txtXML request parameter, and add them to a RecordSet.
     * The anchorColumnName is used as the field name for the "id" attribute .
     */
    public RecordSet getInputRecordSet(HttpServletRequest request) {
        return getInputRecordSet(request, null);
    }

    /**
     * Build a RecordSet from the request parameters and gridId.
     * Add all request parameters, except the "gridId+textXML", as the SummaryRecord
     * Build the input records from the "gridId+txtXML" request parameter, and add them to a RecordSet.
     * The anchorColumnName is used as the field name for the "id" attribute .
     *
     * @param request
     * @param gridId
     * @return RecordSet
     * @author Joe
     * @date 04/27/2007
     */
    public RecordSet getInputRecordSet(HttpServletRequest request, String gridId) {
        Logger l = LogUtils.enterLog(getClass(), "getInputRecordSet", new Object[]{gridId});

        RecordSet inputRecordSet = null;
        String inputRecordSetKey = !StringUtils.isBlank(gridId) ? gridId + INPUT_RECORD_SET : INPUT_RECORD_SET;
        if (RequestStorageManager.getInstance().has(inputRecordSetKey)) {
            inputRecordSet = (RecordSet) RequestStorageManager.getInstance().get(inputRecordSetKey);
        } else {
            inputRecordSet = new RecordSet();

            Map mapMaskedFields = (Map)request.getAttribute(RequestIds.CACHE_MASKED_FIELDS);

            // Add all request parameters, except the textXML, as the SummaryRecord
            inputRecordSet.setSummaryRecord(getInputRecord(request));

            // Add the textXML as Records
            String textXMLName = gridId != null && !gridId.equals("") ? gridId + RequestIds.TEXT_XML : RequestIds.TEXT_XML;
            String textXML = request.getParameter(textXMLName);
            if (StringUtils.isBlank(textXML)) {
                throw new AppException("The required " + textXMLName + " is missing from the request.");
            }
            XMLRecordSetMapper.getInstance(getAnchorColumnName()).map(textXML, inputRecordSet);

            List<Pattern> patternsList = XssFilter.getXssPatternList(request);
            Map xssOverridesFieldsMap = ActionHelper.getXssOverridesPageViewCacheMap();

            Iterator iterator = inputRecordSet.getRecords();
            while (iterator.hasNext()) {
               Record r = (Record) iterator.next();
               Iterator it = r.getFields();
               while (it.hasNext()){
                  Field field = (Field) it.next();
                  String fieldName = field.getName();
                  Object value = null;

                  if (field.hasValue()) {
                      String val = field.getStringValue();
                      value = ActionHelper.decodeMaskedField( fieldName, val, mapMaskedFields);
                      value = XssFilter.sanitizeParameter(patternsList, xssOverridesFieldsMap, fieldName, String.valueOf(value));
                  }

                  r.setFieldValue(fieldName, value);
               }
            }

            inputRecordSet.setDataFromClient(true);

            RequestStorageManager.getInstance().set(inputRecordSetKey, inputRecordSet);
        }

        if (l.isLoggable(Level.FINER))
            l.exiting(getClass().getName(), "getInputRecordSet", inputRecordSet);
        return inputRecordSet;
    }

    /**
     * Secure that the user has access to the web page.
     * This method defaults to loading the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     * @throws Exception
     */
    public void securePage(HttpServletRequest request, ActionForm form) throws Exception {
        LogUtils.setPage("ActionClass:"+getClass().getName());
        securePage(request, form, getClass().getName(), true, DefaultPageDefLoadProcessor.getInstance());
    }

    /**
     * Secure that the user has access to the web page.
     * This method defaults to loading the Request with DynaBeans that represent the form fields defined in the OasisFields definition.
     * The values are taken from the given ActionForm.
     *
     * @param request
     * @param form
     * @param className
     * @param loadFields
     * @param pageDefLoadProcessor
     * @throws Exception
     */
    protected void securePage(HttpServletRequest request, ActionForm form, String className, boolean loadFields,
                              PageDefLoadProcessor pageDefLoadProcessor) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "securePage", new Object[]{request, form});

        LogUtils.setPage("ActionClass:"+getClass().getName());
        // Get input record from the request
        Record inputRecord = getInputRecord(request);

        // Secures a web page by instantiating OasisFields and OasisElements objects
        // for the Struts Action Class referenced by the className parm.
        // These objects are then stored in the request using
        // IOasisAction.KEY_FIELDS and IOasisAction.KEY_ELEMENTS, respectively.
        // If a userid cannot be determined,
        // the page will not be secured and the OasisFields and OasisElements objects will not be instantiated.

        try {
            ActionHelper.securePage(request, className, loadFields, loadFields, pageDefLoadProcessor);
        } catch (Exception e) {
            AppException ae = new AppException("appException.page.noaccess.error", "Failed to secure the page.", e);
            l.throwing(getClass().getName(), "securePage", ae);
            throw ae;
        }


        if (loadFields) {
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

            if (fields != null) {
                try {
                    // Setup beans for every field id, with input parameters as the default values
                    Map fieldsMap = new HashMap();
                    Iterator iter = fields.getFieldIds().iterator();
                    while (iter.hasNext()) {
                        String fieldId = (String) iter.next();
                        if (fieldId.indexOf(" on LayerId") > 0) {
                            fieldId = fieldId.substring(0, fieldId.indexOf(" on LayerId"));
                        }
                        // Only set the value as a request attribute if it doesn't already exist.
                        if (request.getAttribute(fieldId) == null) {
                            if (inputRecord.hasFieldValue(fieldId)) {
                                fieldsMap.put(fieldId, inputRecord.getStringValue(fieldId));
                            } else {
                                fieldsMap.put(fieldId, null);
                            }
                        }
                    }

                    // Cache the OasisFields in the PageView cache if this is not for the header fields so that AJAX requests don't need to re-query them
                    if (className.equals(this.getClass().getName())) {
                        addToPageViewData(IOasisAction.KEY_FIELDS, fields);
                    }

                    ActionHelper.mapToBeans(request, fieldsMap, fields);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to map the OasisFields to request attributes based on the Form bean.", e);
                    l.throwing(getClass().getName(), "securePage", ae);
                    throw ae;
                }
            } else {
                l.logp(Level.FINE, getClass().getName(), "securePage", "No Oasis fields were found for the Action " + getClass().getName());
            }
        }

        validateFileUpload(request, form);

        l.exiting(getClass().getName(), "securePage");
    }

    /**
     * Add an object to the PageViewData cache
     * @param key
     * @param data
     */
    protected void addToPageViewData(Object key, Object data) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addToPageViewData", new Object[]{key, data});
        }

        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        Map pageViewData = pageViewStateAdmin.getPageViewData();
        pageViewData.put(key, data);

        // Queue it in the RequestStorageManager in case it's a load request and the page view id gets regenerated
        RequestStorageManager rsm = RequestStorageManager.getInstance();
        Map queue = null;
        if (rsm.has(RequestStorageIds.QUEUED_DATA_FOR_PAGE_VIEW_CACHE)) {
            queue = (Map) rsm.get(RequestStorageIds.QUEUED_DATA_FOR_PAGE_VIEW_CACHE);
        }
        if (null == queue) {
            queue = new HashMap();
        }
        queue.put(key, data);
        rsm.set(RequestStorageIds.QUEUED_DATA_FOR_PAGE_VIEW_CACHE, queue);

        l.exiting(getClass().getName(), "addToPageViewData");
    }

    /**
     * Get the object from the PageViewData cache based on the provided key.
     * @param key
     * @return
     */
    protected Object getFromPageViewData(Object key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFromPageViewData", new Object[]{key});
        }

        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        Map pageViewData = pageViewStateAdmin.getPageViewData();

        Object result = pageViewData.get(key);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFromPageViewData", result);
        }
        return result;
    }

    /**
     * Removes the object from the PageViewData cache based on the provided key.
     *
     * <p>Returns the value to which this cache previously associated the key,
     * or <tt>null</tt> if the cache contained no mapping for the key.
     *
     * @param key
     * @return
     */
    protected Object removeFromPageViewData(Object key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeFromPageViewData", new Object[]{key});
        }

        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        Map pageViewData = pageViewStateAdmin.getPageViewData();

        Object result = pageViewData.remove(key);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "removeFromPageViewData", result);
        }
        return result;
    }

    /**
     * Publish the output record for use by the Oasis Tags and JSP.
     *
     * @param request
     * @param outputRecord
     * @throws Exception
     */
    protected void publishOutputRecord(HttpServletRequest request, Record outputRecord) throws Exception {
        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        ActionHelper.recordToBeans(request, outputRecord, fields);
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        boolean isAjaxRequest = false;
        Record inputRecord = getInputRecord(request);
        if (inputRecord.hasFieldValue("__isAjaxRequest")) {
            isAjaxRequest = inputRecord.getBooleanValue("__isAjaxRequest").booleanValue();
        }
        return isAjaxRequest;
    }

    /**
     * Load list of Values
     *
     * @param request
     * @param form
     */
    protected void loadListOfValues(HttpServletRequest request, ActionForm form) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues", new Object[]{form});
        }

        loadListOfValues(request, form, true);

        l.exiting(getClass().getName(), "loadListOfValues");
    }

    /**
     * Reset record field value according to selected option for dropdown fields.
     * <p>
     * If the inputRecord field value doesn't exist in the LOV of its corresponding dropdown field,
     * it will be set to the first value from LOV, if available.
     * <p>
     * If call this method, should call loadListOfValues at first.
     *
     * @param request
     */
    protected void resetInputRecordWithLovSelectedValue(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetFieldValuePerSelectedLovValue", new Object[]{});
        }

        Record record = getInputRecord(request);
        List<String> recFieldNames = record.getFieldNameList();
        OasisFields fields = ActionHelper.getFields(request);

        for (String recFieldName : recFieldNames) {
            if (fields.hasField(recFieldName)) {
                OasisFormField formField = fields.getField(recFieldName);

                if (formField != null && !StringUtils.isBlank(formField.getLovSql())) {
                    Object obj = request.getAttribute(recFieldName);
                    if (obj instanceof DynaBean) {
                        Object value = ((DynaBean) obj).get(recFieldName);
                        record.setFieldValue(recFieldName, (value instanceof String[]) ? StringUtils.arrayToDelimited((String[]) value, ",", false, false, true) : value.toString());
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "resetFieldValuePerSelectedLovValue");
    }

    /**
     * Load list of Values
     *
     * @param request
     * @param form
     * @param useDefaults
     * @deprecated
     */
    protected void loadListOfValues(HttpServletRequest request, ActionForm form, boolean useDefaults) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues", new Object[]{form});
        }

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        Connection conn = getReadOnlyConnection();
        try {
            fields.getListOfValues(conn, form, request, useDefaults);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the list of Values.", e);
            l.throwing(getClass().getName(), "loadListOfValues", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(conn);
        }

        l.exiting(getClass().getName(), "loadListOfValues");
    }

    /**
     * rename all fields containing "SELECT_IND" to "SELECT_IND".
     *
     * @param request
     */
    protected void resetSelectIndicator(HttpServletRequest request) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "resetSelectIndicator", new Object[]{});
        }

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
        List layers = fields.getLayers();
        for (int i = 0; i < layers.size(); i++) {
            WebLayer layer = (WebLayer) layers.get(i);
            Iterator fieldEntryIter = layer.entrySet().iterator();
            List removedFldIds = new ArrayList();
            while (fieldEntryIter.hasNext()) {
                Map.Entry fieldEntry = (Map.Entry) fieldEntryIter.next();
                OasisFormField field = (OasisFormField) fieldEntry.getValue();
                String fieldId = field.getFieldId();
                if (fieldId.toUpperCase().indexOf(RequestIds.SELECT_IND) > 1) {
                    removedFldIds.add(fieldId);
                }
            }

            for (int j = 0; j < removedFldIds.size(); j++) {
                String toRemoveFldId = (String) removedFldIds.get(j);
                OasisFormField field = (OasisFormField) layer.get(toRemoveFldId);
                int selectIndIndex = toRemoveFldId.toUpperCase().indexOf(RequestIds.SELECT_IND);

                layer.remove(toRemoveFldId);
                fields.removeFromLoadList(layer.getLayerId(), toRemoveFldId);

                String selectIndId = toRemoveFldId.substring(selectIndIndex, toRemoveFldId.length());
                field.setFieldId(selectIndId);
                fields.addToLoadList(layer.getLayerId(), selectIndId);
                layer.put(selectIndId, field);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "resetSelectIndicator");
        }

    }

    /**
     * Load the XML Grid Header corresponding to the given OasisFields.
     */
    protected void loadGridHeader(HttpServletRequest request) {
        loadGridHeader(request, null, false);
    }

    protected void loadGridHeader(HttpServletRequest request, boolean usePageViewCacheForAjax) {
        loadGridHeader(request, null, usePageViewCacheForAjax);
    }

    /**
     * Load the XML Grid Header corresponding to the given OasisFields.
     */
    protected void loadGridHeader(HttpServletRequest request, String gridHeaderFieldnameSuffix) {
        loadGridHeader(request, gridHeaderFieldnameSuffix, false);
    }

    /**
     * Load the XML Grid Header corresponding to the given OasisFields.
     * If usePageViewCacheForAjax is true, will look in the PageView cache for the grid header. If not found in the cache, will load it and store it in the PageView cache.
     *
     * @param request
     * @param gridHeaderFieldnameSuffix
     * @param usePageViewCacheForAjax
     */
    protected void loadGridHeader(HttpServletRequest request, String gridHeaderFieldnameSuffix, boolean usePageViewCacheForAjax) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadGridHeader", new Object[]{gridHeaderFieldnameSuffix, usePageViewCacheForAjax});
        }

        XMLGridHeader xmlHeader = null;
        if (usePageViewCacheForAjax) {
            xmlHeader = (XMLGridHeader) getFromPageViewData(BaseAction.GRID_HEADER);
        }

        if (null == xmlHeader) {
            if (!hasHeaderFileName() && !hasAnchorColumnName()) {
                throw new IllegalArgumentException("Both the headerFileName and the anchorColumnName are missing. One must be specified in order to load the Grid Header.");
            }

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

            XMLGridHeaderDOMLoader xmlLoader = new XMLGridHeaderDOMLoader(servlet.getServletContext()); /* Set grid header. */
            xmlHeader = xmlLoader.getHeader();
            xmlHeader.setGridHeaderFieldnameSuffix(gridHeaderFieldnameSuffix);
            xmlHeader.setFields(fields);
            xmlHeader.setGenerateMapWithoutPrefixes(this.getUseMapWithoutPrefixes());
            xmlHeader.setAnchorColumnName(getAnchorColumnName());
            xmlHeader.setGridHeaderDefinesDisplayableColumnOrder(gridHeaderDefinesDisplayableColumnOrder());

            if (hasHeaderFileName()) {
                Connection conn = getReadOnlyConnection();
                try {
                    xmlLoader.load(getHeaderFileName(), fields, conn);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the XML Grid Header information", e);
                    l.throwing(getClass().getName(), "loadGridHeader", ae);
                    throw ae;
                } finally {
                    DatabaseUtils.close(conn);
                }
            }

            if (usePageViewCacheForAjax) {
                // Cache the Grid Header for the deferred Ajax call to load the Participants data
                addToPageViewData(BaseAction.GRID_HEADER, xmlHeader);
            }
        }

        request.setAttribute("gridHeaderBean", xmlHeader);

        l.exiting(getClass().getName(), "loadGridHeader");
    }

    /**
     * This method is used to load other grids on one page except the first grid. It assumes the gridId should
     * have a suffix "ListGrid".
     *
     * @param request
     * @param gridHeaderFieldnameSuffix
     * @param gridId
     * @param layerId
     * @author Joe
     * @date 04/25/2007
     */
    protected void loadGridHeader(HttpServletRequest request,
                                  String gridHeaderFieldnameSuffix,
                                  String gridId,
                                  String layerId) {
        loadGridHeader(request, gridHeaderFieldnameSuffix, gridId, layerId, false);
    }
    protected void loadGridHeader(HttpServletRequest request,
                                  String gridHeaderFieldnameSuffix,
                                  String gridId,
                                  String layerId,
                                  boolean usePageViewCacheForAjax) {
        Logger l = LogUtils.enterLog(getClass(), "loadGridHeader", new Object[]{gridId, layerId});

        if (!hasHeaderFileName() && !hasAnchorColumnName()) {
            throw new IllegalArgumentException("Both the headerFileName and the anchorColumnName are missing. " +
                    "One must be specified in order to load the Grid Header.");
        }
        if (layerId == null || layerId.equals("")) {
            return;
        }

        XMLGridHeader xmlHeader = null;
        if (usePageViewCacheForAjax) {
            xmlHeader = (XMLGridHeader) getFromPageViewData(BaseAction.GRID_HEADER);
        }

        if (xmlHeader == null) {
            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            WebLayer layerFields = fields.getLayerFieldsMap(layerId);


            XMLGridHeaderDOMLoader xmlLoader = new XMLGridHeaderDOMLoader(servlet.getServletContext()); // Set grid header.
            xmlHeader = xmlLoader.getHeader();
            xmlHeader.setGridHeaderFieldnameSuffix(gridHeaderFieldnameSuffix);
            xmlHeader.setAnchorColumnName(getAnchorColumnName());
            xmlHeader.setGridHeaderDefinesDisplayableColumnOrder(gridHeaderDefinesDisplayableColumnOrder());
            xmlHeader.setGenerateMapWithoutPrefixes(this.getUseMapWithoutPrefixes());
            xmlHeader.setGridHeaderLayerId(layerId);
            if (hasHeaderFileName()) {
                Connection conn = getReadOnlyConnection();
                try {
                    // TODO: Pass the layerFields to stay backward compatable until we can verify there are no page/layer fields with the same field id on a page
                    xmlLoader.load(getHeaderFileName(), (HashMap) layerFields, conn);
                } catch (Exception e) {
                    AppException ae = ExceptionHelper.getInstance().handleException("Failed to load the XML Grid Header information", e);
                    l.throwing(getClass().getName(), "loadGridHeader", ae);
                    throw ae;
                } finally {
                    DatabaseUtils.close(conn);
                }
            }

            // Reset them since the XMLGridHeaderDOMLoader sets the fields.
            xmlHeader.setFields(fields);
            xmlHeader.setLayerFields(layerFields);
        }

        String gridHeaderBeanName = (gridId != null && !gridId.equals("")) ?
            gridId + "HeaderBean" : "gridHeaderBean";
        request.setAttribute(gridHeaderBeanName, xmlHeader);


        if (usePageViewCacheForAjax) {
            // Cache the Grid Header for the deferred Ajax call to load the Participants data
            addToPageViewData(BaseAction.GRID_HEADER, xmlHeader);
        }

        l.exiting(getClass().getName(), "loadGridHeader");
    }

    /**
     * Set recordset to request.
     *
     * @param request
     * @param rs
     */
    protected void setDataBean(HttpServletRequest request, RecordSet rs) {
        setDataBean(request, rs, null);
    }

    /**
     * Set recordset to request.
     * If the anchorColumnName exists and it is not the first column in recordset, system will reset the first column to it.
     *
     * @param request
     * @param rs
     * @param gridId
     */
    protected void setDataBean(HttpServletRequest request, RecordSet rs, String gridId) {
        String beanName = getDataBeanName(gridId);
        String prevGridId = "";

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

        // Set currentGridId to specified gridId before set the first column name.
        if (!StringUtils.isBlank(gridId)) {
            if (hasCurrentGridId()) {
                prevGridId = getCurrentGridId();
                removeCurrentGridId();
            }
            setCurrentGridId(gridId);
        }
        //Set the frist column to anchorColumnName in recordset.
        if (rs != null) {
            String anchorColumnName = getAnchorColumnName();
            if (!StringUtils.isBlank(anchorColumnName)) {
                List namesList = rs.getFieldNameList();
                if (namesList != null && namesList.size() > 0) {
                    String firstColumn = (String) namesList.get(0);
                    if (!anchorColumnName.equalsIgnoreCase(firstColumn)) {
                        rs.setFieldIndex(anchorColumnName, 0);
                    }
                }
                // set row id on each record
                Map maskedFieldsMap =  getAllMaskedFields(fields);
                for (Record record : rs.getRecordList()) {
                    if (maskedFieldsMap.size() > 0) {
                        Iterator it = maskedFieldsMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry)it.next();
                            String key = (String)entry.getKey();
                            Object keyValue = null;
                            String fieldName = key;

                            int i = key.lastIndexOf(GRID_HEADER_SUFFIX_GH);
                            if (i > 0) {
                                if (record.hasField(key.substring(0,i))){
                                    fieldName = key.substring(0,i);
                                }
                            }
                            if (record.hasField(fieldName)) {
                                keyValue = record.getFieldValue(fieldName);
                                Object oValue = ActionHelper.encodeMaskedField(fieldName, keyValue, fields);
                                record.setFieldValue(fieldName, oValue);
                            }
                        }
                    }

                    record.setRowId(record.getStringValue(anchorColumnName));
                }
            }
        }
        // Reset the current grid to the previous grid if it exists.
        if (!StringUtils.isBlank(gridId)) {
            if (hasCurrentGridId()) {
                removeCurrentGridId();
            }
            if (!StringUtils.isBlank(prevGridId)) {
                setCurrentGridId(prevGridId);
            }
        }
        request.setAttribute(beanName, (rs == null ? null : BaseResultSetRecordSetAdaptor.getInstance(rs)));

        ActionHelper.addGridFieldRecords(request, gridId, rs);
    }

    protected String getDataBeanName(String gridId) {
        String beanName = RequestIds.DATA_BEAN;
        if (!StringUtils.isBlank(gridId)) {
            beanName = gridId + "DataBean";
        }
        return beanName;
    }

    /**
     * set current grid id
     *
     * @param gridId
     */
    protected void setCurrentGridId(String gridId) {
        RequestStorageManager.getInstance().set(RequestStorageIds.CURRENT_GRID_ID, gridId);
    }

    /**
     * get current grid id
     *
     * @return
     */
    protected String getCurrentGridId() {
        String currentGridId = null;
        if (hasCurrentGridId()) {
            currentGridId = (String) RequestStorageManager.getInstance().get(RequestStorageIds.CURRENT_GRID_ID);
        }
        return currentGridId;
    }

    /**
     * check whether current grid id exists
     *
     * @return
     */
    protected boolean hasCurrentGridId() {
        return RequestStorageManager.getInstance().has(RequestStorageIds.CURRENT_GRID_ID);
    }

    /**
     * remove current grid id
     */
    protected void removeCurrentGridId() {
        if (hasCurrentGridId()) {
            RequestStorageManager.getInstance().remove(RequestStorageIds.CURRENT_GRID_ID);
        }
    }

    /**
     * Get the connection from the ReadOnlyDataSource.
     */
    protected Connection getReadOnlyConnection() {
        Logger l = LogUtils.enterLog(getClass(), "getReadOnlyConnection");

        Connection conn = null;
        try {
            conn = getReadOnlyDataSource().getConnection();
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to get a connection from the ReadOnlyDataSource", e);
            l.throwing(getClass().getName(), "getReadOnlyConnection", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getReadOnlyConnection", conn);
        }
        return conn;
    }

    /**
     * Set the forwardString, workflowInstanceId and workflowInstanceIdName in a Properties object stroed in the request attributes.
     * The Properties will added to the forward url as request parameters by the DefaultRequestParameter.
     * If the input record is already cached in the Request Storage Manager, add the Property directly.
     *
     * @param request                the HttpServletRequest
     * @param forwardString          the value of forward parameter
     * @param workflowInstanceId     the value of workflow unique Id
     * @param workflowInstanceIdName the name of workflow unique Id
     */
    protected void setForwardParametersForWorkflow(HttpServletRequest request, String forwardString,
                                                   String workflowInstanceId, String workflowInstanceIdName) {
        setForwardParameter(request, WorkflowFields.WORKFLOW_STATE, forwardString);
        setForwardParameter(request, WorkflowFields.WORKFLOW_INSTANCE_ID, workflowInstanceId);
        setForwardParameter(request, WorkflowFields.WORKFLOW_INSTANCE_ID_NAME, workflowInstanceIdName);
    }

    /**
     * Set the given parameter name/value pair in a Properties object stored in the request attributes.
     * The Properties will added to the forward url as request parameters by the DefaultRequestParameter.
     * If the input record is already cached in the Request Storage Manager, add the Property directly.
     *
     * @param request the HttpServletRequest
     * @param name    the name of the forward parameter
     * @param value   the value to set for the given forward parameter
     */
    protected void setForwardParameter(HttpServletRequest request, String name, String value) {
        Properties props = (Properties) request.getAttribute(FORWARD_PARAMETERS);
        if (props == null) {
            props = new Properties();
        }

        props.setProperty(name, value);
        request.setAttribute(name, value);
        request.setAttribute(FORWARD_PARAMETERS, props);

        if (hasCachedInputRecord()) {
            getCachedInputRecord().setFieldValue(name, value);
        }
    }

    /**
     * Set all the name/value pairs in the provided Properties object
     * into the Properties stored in the request attributes.
     * The Properties will added to the forward url as request parameters by the DefaultRequestParameter.
     * If the input record is already cached in the Request Storage Manager, add the Properties directly.
     *
     * @param request
     * @param forwardParameters
     */
    protected void setForwardParameters(HttpServletRequest request, Properties forwardParameters) {
        Properties props = (Properties) request.getAttribute(FORWARD_PARAMETERS);
        if (props == null) {
            props = new Properties();
        }

        boolean isCached = hasCachedInputRecord();

        Enumeration en = forwardParameters.propertyNames();
        while (en.hasMoreElements()) {
            String paramName = (String) en.nextElement();
            props.setProperty(paramName, forwardParameters.getProperty(paramName));
            request.setAttribute(paramName, forwardParameters.getProperty(paramName));
            if (isCached) {
                getCachedInputRecord().setFieldValue(paramName, forwardParameters.getProperty(paramName));
            }
        }
        request.setAttribute(FORWARD_PARAMETERS, props);
    }

    /**
     * Handle exceptions - store it in the request and return the
     * forward string for the error page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for the Error page
     */
    protected String handleError(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {
        Logger l = LogUtils.enterLog(getClass(), "handleError", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        request.setAttribute(IOasisAction.KEY_ERROR, ae);

        l.exiting(getClass().getName(), "handleError");
        return "error";

    }

    /**
     * Handle exceptions - store it in the request and return the
     * forward string for the error page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for the Error page
     */
    protected ActionForward handleError(String messageKey, String debugMessage, Exception e,
                                        HttpServletRequest request, ActionMapping mapping, String forwardString) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleError", new Object[]{messageKey, debugMessage, e, request, mapping, forwardString});
        }

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        request.setAttribute(IOasisAction.KEY_ERROR, ae);

        ActionForward af = mapping.findForward(IOasisAction.ERROR_ACTION_FWD);
        if (!StringUtils.isBlank(forwardString)) {
            af = mapping.findForward(forwardString);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "handleError", af);
        }
        return af;

    }

    /**
     * Handle exceptions on a popup - store it in the request and and return the
     * forward string for the error pop up page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for Error page for popups
     */
    protected String handleErrorPopup(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorPopup", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        request.setAttribute(IOasisAction.KEY_ERROR, ae);

        l.exiting(getClass().getName(), "handleErrorPopup");
        return "errorpopup";
    }

    /**
     * Handle exceptions on a iframe which is in a popup - store it in the request and and return the
     * forward string for the error iframe page
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param request      current request
     * @param mapping      ActionMapping
     * @return forward string for Error page for iframes
     */
    protected String handleErrorIFrame(String messageKey, String debugMessage, Exception e, HttpServletRequest request, ActionMapping mapping) {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorIFrame", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        request.setAttribute(IOasisAction.KEY_ERROR, ae);

        l.exiting(getClass().getName(), "handleErrorIFrame");
        return "erroriframe";
    }

    /**
     * Handle validation exception - store it in the request
     *
     * @param v       the validation exception
     * @param request current request
     */
    protected void handleValidationException(ValidationException v, HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "handleValidationException", new Object[]{v});

        // Comment out the line below to avoid the validation error being displayed when there is a JSP error.
        //request.setAttribute(IOasisAction.KEY_ERROR, v);

        l.exiting(getClass().getName(), "handleValidationException");
    }

    protected void handleExpectedException(ExpectedException e, HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "handleExpectedException", new Object[]{e});

        // Comment out the line below to avoid the validation error being displayed when there is a JSP error.
        //request.setAttribute(IOasisAction.KEY_ERROR, v);

        l.exiting(getClass().getName(), "handleExpectedException");
    }

    /**
     * Determine if the Request Storage Manager already has the inputRecord cached.
     *
     * @return boolean
     */
    private boolean hasCachedInputRecord() {
        return RequestStorageManager.getInstance().has(INPUT_RECORD);
    }

    /**
     * Get the reference to the inputRecord cached within the Request Storage Manager
     *
     * @return Record
     */
    private Record getCachedInputRecord() {
        l.entering(getClass().getName(), "getCachedInputRecord");

        Record record = (Record) RequestStorageManager.getInstance().get(INPUT_RECORD);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getCachedInputRecord", record);
        }
        return record;
    }

    /**
     * Write List Of Values As XML
     *
     * @param response
     * @param listOfValues
     * @param fieldId
     * @param currentValue
     * @throws Exception
     */
    protected void writeListOfValuesAsXML(HttpServletResponse response, ArrayList listOfValues, String fieldId, String currentValue) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "writeListOfValuesAsXML");
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues", new Object[]{response, listOfValues, fieldId, currentValue});
        }
        writeListOfValuesAsXML(response, listOfValues, fieldId, currentValue, false);

        l.exiting(getClass().getName(), "writeListOfValuesAsXML");
    }

    /**
     * Write List Of Values As XML
     *
     * @param response
     * @param listOfValues
     * @param fieldId
     * @param currentValue
     * @param isReadOnly
     * @throws Exception
     */
    protected void writeListOfValuesAsXML(HttpServletResponse response, ArrayList listOfValues, String fieldId, String currentValue, boolean isReadOnly) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "writeListOfValuesAsXML");
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadListOfValues", new Object[]{response, listOfValues, fieldId, currentValue});
        }

        StringBuffer currentLabel = new StringBuffer();
        StringBuffer newValue = new StringBuffer();
        StringBuffer optionBuffer = new StringBuffer();
        boolean valueExists = false;
        if (listOfValues.size() > 0) {
            String currentValueWithComma = "," + currentValue + ",";
            for (int i = 0; i < listOfValues.size(); i++) {
                LabelValueBean lb = (LabelValueBean) listOfValues.get(i);
                optionBuffer.append("<option value='").append(lb.getValue()).append("'");
                if (!StringUtils.isBlank(lb.getLabel()) && !CodeLookupManager.getInstance().getSelectOptionLabel().equals(lb.getLabel().toUpperCase())) {
                    if (!StringUtils.isBlank(currentValue) && !StringUtils.isBlank(lb.getValue()) && currentValueWithComma.indexOf("," + lb.getValue() + ",") > -1) {
                        optionBuffer.append(" selected='selected'");
                        valueExists = true;
                        currentLabel.append(lb.getLabel()).append(",");
                        newValue.append(lb.getValue()).append(",");
                    }
                }
                optionBuffer.append(">").append(lb.getLabel()).append("</option>");
            }
            if (!valueExists) {
                //Set selected for the first option.
                LabelValueBean firstOption = (LabelValueBean) listOfValues.get(0);
                optionBuffer.insert(8, " selected='selected' ");
                if (StringUtils.isBlank(firstOption.getValue())) {
                    boolean useLabelForEmptyOption = false;
                    if (RequestStorageManager.getInstance().has(fieldId + RequestStorageIds.USE_LABEL_FOR_EMPTY_OPTION)) {
                        useLabelForEmptyOption = Boolean.valueOf(RequestStorageManager.getInstance().get(fieldId + RequestStorageIds.USE_LABEL_FOR_EMPTY_OPTION).toString());
                    }
                    if (useLabelForEmptyOption && isReadOnly) {
                        newValue.append(firstOption.getValue()).append(",");
                        currentLabel.append(StringUtils.isBlank(firstOption.getLabel()) ? "" : firstOption.getLabel()).append(",");
                    }
                } else {
                    newValue.append(firstOption.getValue()).append(",");
                    currentLabel.append(firstOption.getLabel()).append(",");
                }
            }
        }
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        StringWriter strWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(strWriter);

        writer.println("<?xml version='1.0' encoding='utf-8' ?>");
        writer.print("<result>");
        writer.print("<fieldId>");
        writer.print(fieldId);
        writer.print("</fieldId>");
        writer.print("<currentValue>");
        int valueLength = newValue.length();
        if (valueLength > 0) {
            newValue.delete(valueLength - 1, valueLength);
        }
        writer.print(XMLUtils.encode(newValue.toString()));
        writer.print("</currentValue>");
        writer.print("<currentLabel>");
        int labelLength = currentLabel.length();
        if (labelLength > 0) {
            currentLabel.delete(labelLength - 1, labelLength);
        }
        writer.print(XMLUtils.encode(currentLabel.toString()));
        writer.print("</currentLabel>");
        writer.print("<options>");
        writer.print(XMLUtils.encode(optionBuffer.toString()));
        writer.print("</options>");
        writer.println("</result>");

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "writeListOfValuesAsXML", "strWriter = " + strWriter);
        }

        writer = response.getWriter();
        writer.print(strWriter);
        writer.flush();

        l.exiting(getClass().getName(), "writeListOfValuesAsXML");
    }

    /**
     * Consider using "JSON"? Browser parses JSON String significantly faster than xml string!
     * <p>
     * write Ajax response in the format of JSON object:
     * {property1:value1, property2:value2..proertyn:valuen}.
     * where the properties are the fields of record passed in
     * if field string value is true/false, the property value is true/false
     * else if field string value is numeric string, the property value is set as a number
     * else if field has string value, the property value is set as a string appended, prefixed with a quote
     * <p>
     * for example: {claimId:123, claimReadOnly:true, claimNo:'02334-1'}
     *
     * @param response
     * @param record
     * @throws IOException
     */
    protected void writeAjaxJsonResponse(HttpServletResponse response, Record record) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "writeAjaxJsonResponse");
        StringBuffer json = new StringBuffer("{");
        Iterator it = record.getFields();

        while (it.hasNext()) {
            Field field = (Field) it.next();
            String fieldName = field.getName();
            json.append("'" + fieldName + "':");
            if (record.hasStringValue(fieldName)) {
                String stringValue = field.getStringValue();
                if (stringValue.equalsIgnoreCase("TRUE")) {
                    json.append("true");
                } else if (stringValue.equalsIgnoreCase("FALSE")) {
                    json.append("false");
                } else if (StringUtils.isNumeric(stringValue)) {
                    json.append(stringValue);
                } else {
                    json.append("\"").append(stringValue).append("\"");
                }
            } else {
                json.append("''");
            }
            json.append(",");
        }
        if (record.getFieldCount() > 0) json.setLength(json.length() - 1); // remove final comma
        json.append("}");
        writeAjaxResponse(response, json.toString());
        l.exiting(getClass().getName(), "writeAjaxJsonResponse");
    }

    /**
     * Write JSON output to response.
     *
     * @param response
     * @param ajaxResponse
     * @throws IOException
     */
    protected void writeAjaxJsonResponse(HttpServletResponse response, String ajaxResponse) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        PrintWriter wri = response.getWriter();
        wri.write(ajaxResponse);
        wri.flush();
    }

    /**
     * Write XML output to response.
     *
     * @param response
     * @param ajaxResponse
     * @throws IOException
     */
    protected void writeAjaxResponse(HttpServletResponse response, String ajaxResponse) throws IOException {
        response.setContentType("text/html");
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        PrintWriter wri = response.getWriter();
        wri.write(ajaxResponse);
        wri.flush();
    }

    /**
     * Write XML output to response.
     *
     * @param request
     * @param response
     * @param recordSet
     * @param gridId
     * @param keepCase
     * @param isValidationException
     * @throws IOException
     */
    protected void writeAjaxResponse(HttpServletRequest request, HttpServletResponse response, RecordSet recordSet,
                                     String gridId, boolean keepCase, boolean isValidationException) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxdResponse", new Object[]{response, recordSet, gridId, keepCase});
        }

        final String strAjaxResponseOpen = "<AJAXRESPONSE>";
        final String strAjaxResponseClose = "</AJAXRESPONSE>";
        final String strMessagesOpen = "<MESSAGES>";
        final String strMessagesClose = "</MESSAGES>";
        final String strMessageOpen = "<MESSAGE>";
        final String strMessageClose = "</MESSAGE>";
        final String strCategoryOpen = "<CATEGORY>";
        final String strCategoryClose = "</CATEGORY>";
        final String strKeyOpen = "<KEY>";
        final String strKeyClose = "</KEY>";
        final String strTextOpen = "<TEXT>";
        final String strTextClose = "</TEXT>";
        final String strConfirmedAsYRequiredOpen = "<CONFIRMEDASYREQUIRED>";
        final String strConfirmedAsYRequiredClose = "</CONFIRMEDASYREQUIRED>";
        final String strFieldIdOpen = "<FIELDID>";
        final String strFieldIdClose = "</FIELDID>";
        final String strRowIdOpen = "<ROWID>";
        final String strRowIdClose = "</ROWID>";
        final String strCDataStart = "<![CDATA[";
        final String strCDataEnd = "]]>";
        final String strCSRFTokenOpen = "<CSRF_TOKEN>";
        final String strCSRFTokenClose = "</CSRF_TOKEN>";

        // write header
        // Fix the issue 103608, add character encoding "UTF-8" to PrintStream.
        String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
        PrintWriter out = response.getWriter();
/*
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bos);
*/
        response.setContentType("text/xml;charset=" + encoding);
        out.println("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
        out.println(strAjaxResponseOpen);
        if (isValidationException)
            out.println("<VALIDATIONEXCEPTION>YES</VALIDATIONEXCEPTION>");
        else
            out.println("<VALIDATIONEXCEPTION>NO</VALIDATIONEXCEPTION>");

        // write messages
        out.println(strMessagesOpen);
        Iterator it = MessageManager.getInstance().getMessagesAndConfirmationPrompts();
        while (it.hasNext()) {
            Message message = ((Message) it.next());
            String category = message.getMessageCategory().toString();
            String key = message.getMessageKey();
            String text = message.getMessage();
            String confirmedAsYRequired = YesNoFlag.getInstance(message.getConfirmedAsYRequired()).toString();
            String fieldId = message.getMessageFieldId();
            String rowId = message.getMessageRowId();

            out.println(strMessageOpen);
            out.println(strCategoryOpen);
            out.println(category);
            out.println(strCategoryClose);
            out.println(strKeyOpen);
            out.println(key);
            out.println(strKeyClose);
            out.println(strTextOpen);
            // Write text inside CDATA component to handle special character like & < >
            out.print(strCDataStart);
            out.print(text);
            out.println(strCDataEnd);
            out.println(strTextClose);
            out.println(strConfirmedAsYRequiredOpen);
            out.println(confirmedAsYRequired);
            out.println(strConfirmedAsYRequiredClose);
            out.println(strFieldIdOpen);
            out.println(fieldId);
            out.println(strFieldIdClose);
            out.println(strRowIdOpen);
            out.println(rowId);
            out.println(strRowIdClose);
            out.println(strMessageClose);
        }
        out.println(strMessagesClose);

        // add element for OBREnforcedResult
        if (RequestStorageManager.getInstance().has("OBREnforcedResult")) {
            out.println("<OBREnforcedResult>" + RequestStorageManager.getInstance().get("OBREnforcedResult") + "</OBREnforcedResult>");
            Record changedFieldRecord = (Record) RequestStorageManager.getInstance().get("OBRChangedFieldRecord");
            StringBuffer dataBuffer = new StringBuffer();
            dataBuffer.append("<OBRChangedFields>");
            for (Object object : changedFieldRecord.getFieldNameList()) {
                String fieldId = (String) object;
                String value = changedFieldRecord.getStringValue(fieldId);
                dataBuffer.append("<").append(fieldId).append(">");
                if (value.indexOf('&') > -1 || value.indexOf('<') > -1 || value.indexOf('>') > -1) {
                    dataBuffer.append("<![CDATA[").append(value).append("]]>");
                } else {
                    dataBuffer.append(value);
                }
                dataBuffer.append("</").append(fieldId).append(">");
            }
            dataBuffer.append("</OBRChangedFields>");
            out.println(dataBuffer.toString());
        }

        HttpServletRequest req = (HttpServletRequest)RequestStorageManager.getInstance().get(dti.oasis.request.RequestStorageIds.HTTP_SEVLET_REQUEST);
        req.getSession().setAttribute(UserSessionIds.NEW_TOKEN_GENERATED, "N");
        req.getSession().removeAttribute(UserSessionIds.PAGE_TOKEN);
        String csrfToken = "";
        if (isSaveInProgress()) {
            //generate token.
            csrfToken = this.generateToken(req);
            req.getSession().setAttribute(UserSessionIds.PAGE_TOKEN, csrfToken);
            req.getSession().setAttribute(UserSessionIds.NEW_TOKEN_GENERATED, "Y");
        } else {
            //make sure that all pages must pass the token either in form submit or ajax
            csrfToken = req.getParameter(Globals.TOKEN_KEY);
            if (!StringUtils.isBlank(csrfToken)) {
                req.getSession().setAttribute(UserSessionIds.PAGE_TOKEN, csrfToken);
            }
        }

        //send csrf token back to client
        out.println(strCSRFTokenOpen + csrfToken + strCSRFTokenClose);

        // write valid fild values
        if (recordSet != null) {
            if (request != null && !StringUtils.isBlank(gridId))
                XMLRecordSetMapper.getInstance().map(request, recordSet, gridId, out);
            else
                XMLRecordSetMapper.getInstance().map(recordSet, out, keepCase);

        }

        // write footer
        out.println(strAjaxResponseClose);
/*
        String output = bos.toString();
        System.out.println("   ***************    output = " + output);
        response.getWriter().print(output);
        response.getWriter().flush();
*/
        out.flush();

        l.exiting(getClass().getName(), "writeAjaxdResponse");
    }

    /**
     * Write XML output to response.
     *
     * @param response
     * @param recordSet
     * @param keepCase
     * @throws IOException
     */
    protected void writeAjaxResponse(HttpServletResponse response, RecordSet recordSet,
                                     boolean keepCase, boolean isValidationException) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxdResponse", new Object[]{response, recordSet});
        }
        writeAjaxResponse(null, response, recordSet, null, keepCase, isValidationException);
    }

    /**
     * Write XML output to response.
     *
     * @param response
     * @param record
     * @param keepCase
     * @param isValidationException
     * @throws IOException
     */
    protected void writeAjaxResponse(HttpServletResponse response, Record record, boolean keepCase, boolean isValidationException) throws IOException {
        RecordSet recordSet = new RecordSet();
        recordSet.addRecord(record);
        writeAjaxResponse(response, recordSet, keepCase, isValidationException);
    }

    /**
     * Write XML output to response.
     *
     * @param response
     * @param record
     * @param keepCase
     * @throws IOException
     */
    protected void writeAjaxResponse(HttpServletResponse response, Record record, boolean keepCase) throws IOException {
        writeAjaxResponse(response, record, keepCase, false);
    }

    /**
     * Write XML output to response from RecrodSet.
     *
     * @param response
     * @param recordSet
     * @param keepCase
     * @throws IOException
     */
    protected void writeAjaxXmlResponse(HttpServletResponse response, RecordSet recordSet, boolean keepCase) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxXmlResponse", new Object[]{response, recordSet});
        }

        // replace anchorColumn name with ID for presentation layer
        RecordSet newRecordSet = mapAnchorColumnForInitialValues(recordSet);

        // write XML
        writeAjaxResponse(response, newRecordSet, keepCase, false);

        l.exiting(getClass().getName(), "writeAjaxXmlResponse");
    }

    /**
     * Write XML output to response from RecrodSet.
     *
     * @param response
     * @param recordSet
     * @throws IOException
     */
    protected void writeAjaxXmlResponse(HttpServletResponse response, RecordSet recordSet) throws IOException {
        writeAjaxXmlResponse(response, recordSet, false);
    }

    /**
     * Write XML output to response from Record.
     *
     * @param response
     * @param record
     * @param keepCase
     * @throws IOException
     */
    protected void writeAjaxXmlResponse(HttpServletResponse response, Record record, boolean keepCase) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxXmlResponse", new Object[]{response, record});
        }

        RecordSet rs = new RecordSet();
        if (record != null) {
            rs.addRecord(record);
        }
        writeAjaxXmlResponse(response, rs, keepCase);

        l.exiting(getClass().getName(), "writeAjaxXmlResponse");
    }

    /**
     * Write XML response
     *
     * @param response
     * @param record
     * @throws IOException
     */
    protected void writeAjaxXmlResponse(HttpServletResponse response, Record record) throws IOException {
        writeAjaxXmlResponse(response, record, false);
    }

    /**
     * Write XML response. Used to reload the entire Grid
     *
     * @param request
     * @param response
     * @param recordSet
     * @param gridId
     * @param keepCase
     * @throws IOException
     */
    protected void writeAjaxXmlResponse(HttpServletRequest request, HttpServletResponse response, RecordSet recordSet,
                                        String gridId, boolean keepCase) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxXmlResponse", new Object[]{response, recordSet, gridId, keepCase});
        }

        // write XML
        writeAjaxResponse(request, response, recordSet, gridId, keepCase, false);

        l.exiting(getClass().getName(), "writeAjaxXmlResponse");
    }

    /**
     * Write empty XML response which only contain messages
     *
     * @param response
     * @throws IOException
     */
    protected void writeEmptyAjaxXMLResponse(HttpServletResponse response) throws IOException {
        Record record = null;
        writeAjaxXmlResponse(response, record);
    }


    /**
     * replace anchorColumn name with ID for presentation layer
     *
     * @param recordSet
     * @return
     */
    protected RecordSet mapAnchorColumnForInitialValues(RecordSet recordSet) {
        Logger l = LogUtils.enterLog(getClass(), "mapAnchorColumnForInitialValues");
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapAnchorColumnForInitialValues", new Object[]{recordSet});
        }

        Iterator iter = recordSet.getRecords();
        RecordSet newRecordSet = new RecordSet();
        while (iter.hasNext()) {
            Record record = (Record) iter.next();
            mapAnchorColumnForInitialValues(record);
            newRecordSet.addRecord(record);
        }

        l.exiting(getClass().getName(), "mapAnchorColumnForInitialValues", newRecordSet);
        return newRecordSet;
    }

    /**
     * replace anchorColumn name with ID for presentation layer
     *
     * @param record
     */
    protected void mapAnchorColumnForInitialValues(Record record) {
        // we can not assume the anchorColumn exists
        String anchorColumnName = getAnchorColumnName();

        if (!StringUtils.isBlank(anchorColumnName)) {
            // replace anchorColumn name with ID for presentation layer
            if (record.hasField(getAnchorColumnName())) {
                record.setFieldValue("ID", record.getFieldValue(getAnchorColumnName()));
                record.remove(getAnchorColumnName());
            }
        }
    }

    /**
     * Handle exceptions for Ajax - write XML in response
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param response     current response
     */
    protected void handleErrorForAjax(String messageKey, String debugMessage, Exception e,
                                      HttpServletResponse response) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorForAjax", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        l.logp(Level.SEVERE, getClass().getName(), "handleErrorForAjax", "Failed while invoking the Ajax Request", ae);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        writeAjaxResponse(response, new Record(), true);

        l.exiting(getClass().getName(), "handleErrorForAjax");
    }

    /**
     * Handle exceptions for Ajax - write JSON in response
     *
     * @param messageKey   a message key referencing a user displayable message describing this Exception.
     * @param debugMessage a debug message to further describe the context of the error
     * @param e            the exception
     * @param response     current response
     */
    protected void handleErrorForAjaxJson(String messageKey, String debugMessage, Exception e,
                                      HttpServletResponse response) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "handleErrorForAjaxJson", new Object[]{messageKey, debugMessage, e});

        if (e instanceof AppException && messageKey == AppException.UNEXPECTED_ERROR) {
            messageKey = ((AppException) e).getMessageKey();
        }
        AppException ae = ExceptionHelper.getInstance().handleException(messageKey, debugMessage, e);
        l.logp(Level.SEVERE, getClass().getName(), "handleErrorForAjaxJson", "Failed while invoking the Ajax Request", ae);
        if (!MessageManager.getInstance().hasMessage(ae.getMessageKey())) {
            MessageManager.getInstance().addErrorMessage(ae.getMessageKey(), ae.getMessageParameters());
        }

        writeAjaxJsonResponse(response, new RecordSet());

        l.exiting(getClass().getName(), "handleErrorForAjaxJson");
    }

    /**
     * Sets all fields to read-only.
     *
     * @param fields OasisFields object.
     */
    protected void setFieldsToReadOnly(OasisFields fields) {
        OasisFieldsUtility.setFieldsToReadOnly(fields);
    }

    /**
     * Sets all fields (including layers, if specified) to read-only.
     *
     * @param fields       OasisFields object.
     * @param handleLayers Set fields in layers to read/only, yes or no.
     */
    protected void setFieldsToReadOnly(OasisFields fields, boolean handleLayers) {
        OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers);
    }

    /**
     * Sets all fields (excluding specified fields) to read-only.
     *
     * @param fields             OasisFields object.
     * @param excludedFieldsList ArrayList of strings with IDs of fields to be excluded.
     */
    protected void setFieldsToReadOnly(OasisFields fields, ArrayList excludedFieldsList) {
        OasisFieldsUtility.setFieldsToReadOnly(fields, excludedFieldsList);
    }

    /**
     * Sets all fields (including layers, if specified, and excluding specified fields) to read-only.
     *
     * @param fields             OasisFields object.
     * @param handleLayers       Set fields in layers to read/only, yes or no.
     * @param excludedFieldsList ArrayList of strings with IDs of fields to be excluded.
     */
    protected void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
                                       ArrayList excludedFieldsList) {
        OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers, excludedFieldsList);
    }

    /**
     * Sets all fields (including non-layer fields if specified, including layers, if specified) to read-only.
     *
     * @param fields               OasisFields object.
     * @param handleLayers         Set fields in layers to read/only, yes or no.
     * @param handleNonLayerFields Set fields in map that are not in layers to read/only, yes or no.
     */
    protected void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
                                       boolean handleNonLayerFields) {
        ArrayList excludedFieldsList = null;
        OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers, excludedFieldsList, handleNonLayerFields);
    }

    /**
     * Sets all fields (including non-layer fields, if specified, including layers,
     * if specified, and excluding specified fields) to read-only.
     *
     * @param fields               OasisFields object.
     * @param handleLayers         Set fields in layers to read/only, yes or no.
     * @param excludedFieldsList   ArrayList of strings with IDs of fields to be excluded.
     * @param handleNonLayerFields Set fields in map that are not in layers to read/only, yes or no.
     */
    protected void setFieldsToReadOnly(OasisFields fields, boolean handleLayers,
                                       ArrayList excludedFieldsList, boolean handleNonLayerFields) {
        OasisFieldsUtility.setFieldsToReadOnly(fields, handleLayers,
                excludedFieldsList, handleNonLayerFields);
    }

    /**
     * Sets all fields (including non-layer fields, if specified, including layers,
     * if specified, and excluding specified fields) to read-only.
     * The OasisFields stored in the request as IOasisAction.KEY_FIELDS are used as the fields.
     *
     * @param request            The HttpServletRequest
     * @param excludedFieldsList String array with IDs of any fields to be excluded.
     * @param handleLayers       Set fields in layers to read/only, yes or no.
     * @param handlePageFields   Set fields in map that are not in layers to read/only, yes or no.
     */
    public void setFieldsToReadOnly(HttpServletRequest request, String[] excludedFieldsList,
                                    boolean handleLayers, boolean handlePageFields) {

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldsToReadOnly",
                    new Object[]{request, excludedFieldsList,
                            new Boolean(handleLayers),
                            new Boolean(handlePageFields)});
        }

        boolean excludeFields = false;

        OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);

        if (excludedFieldsList != null && excludedFieldsList.length >= 1) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "setFieldsToReadOnly", "excludeFields = " + excludeFields);
            }
            excludeFields = true;
        }

        if (fields != null) {
            // First handle page fields if specified
            if (handlePageFields) {

                // Loop through each page field
                Iterator itr = fields.keySet().iterator();
                while (itr.hasNext()) {
                    String mapKey = (String) itr.next();
                    if (mapKey != null) {
                        if (fields.get(mapKey) instanceof OasisFormField) {
                            boolean makeFldReadOnly = true;
                            OasisFormField frmFld = (OasisFormField) fields.get(mapKey);
                            if (excludeFields) {
                                for (int i = 0; i < excludedFieldsList.length; i++) {
                                    Object curExclFldNameObj = excludedFieldsList[i];
                                    if (curExclFldNameObj instanceof String) {
                                        String curExclFldNameStr = (String) curExclFldNameObj;
                                        if (!StringUtils.isBlank(curExclFldNameStr) && curExclFldNameStr.equals(mapKey)) {
                                            if (l.isLoggable(Level.FINE)) {
                                                l.logp(Level.FINE, getClass().getName(), "setFieldsToReadOnly",
                                                        new StringBuffer().append("field ").append(mapKey).append(" will be excluded").toString());
                                            }
                                            makeFldReadOnly = false;
                                        }
                                    }
                                }
                            }

                            // Set it to read only
                            if (frmFld != null && makeFldReadOnly) {
                                frmFld.setIsReadOnly(true);
                                if (l.isLoggable(Level.FINE)) {
                                    l.logp(Level.FINE, getClass().getName(), "setFieldsToReadOnly", new StringBuffer().append("field ").append(mapKey).append(" is now read only").toString());
                                }
                            }
                        }
                    }
                }
            }

            // Now handle the layers if specified
            if (handleLayers) {

                // Pull the layers out of the OasisFields
                ArrayList layers = fields.getLayers();
                if (layers != null && layers.size() >= 1) {
                    for (int i = 0; i < layers.size(); i++) {
                        if (layers.get(i) instanceof WebLayer) {
                            ArrayList curLayerFlds = fields.getLayerFields(((WebLayer) layers.get(i)).getLayerId());
                            if (curLayerFlds != null && curLayerFlds.size() >= 1) {
                                for (int j = 0; j < curLayerFlds.size(); j++) {
                                    if (curLayerFlds.get(j) instanceof OasisFormField) {
                                        boolean makeFldReadOnly = true;
                                        OasisFormField curFld = (OasisFormField) curLayerFlds.get(j);
                                        String curFldName = curFld.getFieldId();
                                        if (excludeFields) {
                                            for (int k = 0; k < excludedFieldsList.length; k++) {
                                                Object curExclFldNameObj = excludedFieldsList[k];
                                                if (curExclFldNameObj instanceof String) {
                                                    String curExclFldNameStr = (String) curExclFldNameObj;
                                                    if (!StringUtils.isBlank(curExclFldNameStr) && curExclFldNameStr.equals(curFldName)) {
                                                        if (l.isLoggable(Level.FINE)) {
                                                            l.logp(Level.FINE, getClass().getName(), "setFieldsToReadOnly",
                                                                    new StringBuffer().append("field ").append(curFldName).append(" will be excluded").toString());
                                                        }
                                                        makeFldReadOnly = false;
                                                    }
                                                }
                                            }
                                        }
                                        if (curFld != null && makeFldReadOnly) {
                                            curFld.setIsReadOnly(true);
                                            if (l.isLoggable(Level.FINE)) {
                                                l.logp(Level.FINE, getClass().getName(), "setFieldsToReadOnly",
                                                        new StringBuffer().append("field ").append(curFld.getFieldId()).append(" is now read-only").toString());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        l.exiting(getClass().getName(), "setFieldsToReadOnly");
    }


    /**
     * Set the arraylist fields to readonly
     *
     * @param fields ArraryList fields
     */
    protected void setFieldsToReadOnly(ArrayList fields) {
        Logger l = LogUtils.enterLog(getClass(), "setFieldsToReadOnly",
                new Object[]{fields});
        OasisFormField field = null;
        for (int i = 0; i < fields.size(); i++) {
            field = (OasisFormField) fields.get(i);
            field.setIsReadOnly(true);
        }
        l.exiting(getClass().getName(), "setFieldsToReadOnly");
    }

    /**
     * Set the arraylist fields except field in the excluded list to readonly
     *
     * @param fields         ArraryList fields
     * @param excludedFields ArrayList fields
     */
    protected void setFieldsToReadOnly(ArrayList fields, ArrayList excludedFields) {
        Logger l = LogUtils.enterLog(getClass(), "setFieldsToReadOnly",
                new Object[]{fields, excludedFields});
        OasisFormField field = null;
        OasisFormField excludedField = null;
        boolean isReadOnly = true;
        for (int i = 0; i < fields.size(); i++) {
            isReadOnly = true;
            field = (OasisFormField) fields.get(i);
            for (int j = 0; j < excludedFields.size(); j++) {
                excludedField = (OasisFormField) excludedFields.get(j);
                if (field.getFieldId().equals(excludedField.getFieldId())) {
                    isReadOnly = false;
                    break;
                }
            }
            if (isReadOnly)
                field.setIsReadOnly(true);
        }
        l.exiting(getClass().getName(), "setFieldsToReadOnly");
    }


    /**
     * Method that removes both the save token and save in-progress request attribute. This method is called only by
     * in-progress save request to indicate that the save process has been completed successfully.
     *
     * @param request, current HTTP request.
     * @param tokenConstant, name of the token constannt. a popup page may have a different token name than default.
     */
    protected void saveProcessSucceeded(HttpServletRequest request, String tokenConstant) {
        Logger l = LogUtils.enterLog(getClass(), "saveProcessSucceeded", new Object[]{});

        HttpServletRequest req = (HttpServletRequest)RequestStorageManager.getInstance().get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        String tokenGenerated = (String)req.getSession().getAttribute(UserSessionIds.NEW_TOKEN_GENERATED);

        if(YesNoFlag.getInstance(tokenGenerated).booleanValue()) {
            updateSessionToken(request, tokenConstant);
        } else {
            //generate a new token and save it.
            saveToken(request,tokenConstant);
        }
        //Remove the save in-progress session attribute.
        removeSaveInProgressIndicator();
    }

    public void updateSessionToken(HttpServletRequest request, String tokenConstant) {
        HttpServletRequest req = (HttpServletRequest)RequestStorageManager.getInstance().get(RequestStorageIds.HTTP_SEVLET_REQUEST);
        String tokenGenerated = (String)req.getSession().getAttribute(UserSessionIds.NEW_TOKEN_GENERATED);

        if(YesNoFlag.getInstance(tokenGenerated).booleanValue()) {
            if (StringUtils.isBlank(tokenConstant)) {
                tokenConstant = Globals.TRANSACTION_TOKEN_KEY;
            }
            String newToken = (String)req.getSession().getAttribute(UserSessionIds.PAGE_TOKEN);
            String superClassName = getSuperClassName();

            removeClassActionNameFromSession(request);

            request.getSession().setAttribute(superClassName + UserSessionIds.TOKEN_SUFFIX, newToken);
            request.getSession().setAttribute(tokenConstant, newToken);
            req.getSession().removeAttribute(UserSessionIds.NEW_TOKEN_GENERATED);
            req.getSession().removeAttribute(UserSessionIds.PAGE_TOKEN);
        }
    }
    /**
     * Method that removes the save in-progress request attribute. This method is called only by in-progress save request
     * to indicate that the save process has been exited successfully, although it is failed due to an exception.
     *
     * @param request, current HTTP request.
     */
    protected void saveProcessFailed(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "saveProcessFailed", new Object[]{});

        //Remove the save in-progress session attribute to indicate that the save process has been exited.
        removeSaveInProgressIndicator();
    }


    /**
     * Method that sets the provided boolean indicator in current http request session.
     */
    protected void setSaveInProgressIndicator() {
        UserSessionManager.getInstance().getUserSession().set(SAVE_IN_PROGRESS, "TRUE");
    }

    /**
     * Method that removes the save in-progress boolean indicator from the current http request session.
     */
    protected void removeSaveInProgressIndicator() {
        UserSessionManager.getInstance().getUserSession().remove(SAVE_IN_PROGRESS);
    }


    /**
     * Method that initializes the save token for the request. The save token is initialized only for requests that do
     * not have an existing save token and is not part of save process request.
     * <p/>
     * If the request is currently participating in save process, a "true" request attribute is set for
     * RequestIds.SAVE_INPROGRESS request attribute to indicate that it is currently participating in another save process.
     * <p/>
     *
     * @param request, current HTTP request.
     */
    protected void initializeLoadRequestForSave(HttpServletRequest request, String tokenConstant) {
        Logger l = LogUtils.enterLog(getClass(), "initializeLoadRequestForSave", new Object[]{});
        //Check if there is any prior in-progress save request.
        if (!isSaveInProgress()) {
            //No prior in-progress save request is found, issue a new save token for the request.
            // Move into Base Action
            if (!isSaveTokenAlreadyInitialized()) {
                saveToken(request, tokenConstant);
            }
        } else {
            request.setAttribute(RequestIds.SAVE_INPROGRESS, "true");
            MessageManager.getInstance().addErrorMessage("core.save.inprogress.msg");
        }
    }


    /**
     * Method that returns a boolean value to indicate whether the save token has been already initialized by
     * earlier request.
     */
    private boolean isSaveTokenAlreadyInitialized() {
        return (UserSessionManager.getInstance().getUserSession().has(Globals.TRANSACTION_TOKEN_KEY));
    }

    /**
     * Method that returns a boolean value that indicates whether any previous save request is still in-progress.
     *
     * @return boolean true, if previous save request is still in-progress; otherwise, false.
     */
    protected boolean isSaveInProgress() {
        Logger l = LogUtils.enterLog(getClass(), "isSaveInProgress");
        boolean isSaveInProgress = false;
        if (UserSessionManager.getInstance().getUserSession().has(SAVE_IN_PROGRESS)) {
            isSaveInProgress = Boolean.valueOf((String) UserSessionManager.getInstance().getUserSession().get(SAVE_IN_PROGRESS)).booleanValue();
        }
        l.exiting(getClass().getName(), "isSaveInProgress", String.valueOf(isSaveInProgress));
        return isSaveInProgress;
    }

    /**
     * Method that returns a boolean value indicating whether the save request has a valid save token to proceed.
     * If the request has a valid save token, then the save in-progress indicator is set, in order to avoid duplicate
     * save request.
     *
     * @param request, current HTTP Request
     * @return boolean true, if the save token is valid; otherwise, false.
     */
    protected boolean hasValidSaveToken(HttpServletRequest request) {
        Logger l = LogUtils.enterLog(getClass(), "hasValidSaveToken", new Object[]{});
        boolean isValidToken = false;
        isValidToken = isTokenValid(request, false);
        if (isValidToken) {
            //Set the save in-progress session attribute as true
            setSaveInProgressIndicator();
        } else {
            String classToken = getActionClassToken(request);
            String sessionToken = (String) request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY);

            if (!classToken.equalsIgnoreCase(sessionToken)) {
                String sysParmEnableXSS = SysParmProvider.getInstance().getSysParm(RequestIds.CSRF_PROTECTION, "N");
                if (YesNoFlag.getInstance(sysParmEnableXSS).booleanValue()) {
                    throw new AppException("core.security.csrf.token.verify.failed", "Invalid CSRF Token");
                }
            }
        }

        l.exiting(getClass().getName(), "hasValidSaveToken", String.valueOf(isValidToken));
        return isValidToken;
    }


    /**
     * Handle validation exception for Ajax - write XML in response
     *
     * @param response current response
     */
    protected void handleValidationExceptionForAjax(ValidationException e, HttpServletResponse response) throws IOException {
        Logger l = LogUtils.enterLog(getClass(), "handleValidationExceptionForAjax", new Object[]{e, response});

        writeAjaxResponse(response, e.getValidFields(), true, true);

        l.exiting(getClass().getName(), "handleValidationExceptionForAjax");
    }

    /**
     * write record set to json, include message info
     *
     * @param response
     * @param recordSet
     * @throws IOException
     */
    public void writeAjaxJsonResponse(HttpServletResponse response, RecordSet recordSet) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxJsonResponse", new Object[]{response, recordSet});
        }

        writeAjaxJsonResponse(response, recordSet, false);

        l.exiting(getClass().getName(), "writeAjaxJsonResponse");
    }

    /**
     * write record set to json, include message info
     *
     * @param response
     * @param recordSet
     * @param keepCase
     * @throws IOException
     */
    public void writeAjaxJsonResponse(HttpServletResponse response, RecordSet recordSet, boolean keepCase) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxJsonResponse", new Object[]{response, recordSet, keepCase});
        }
        // replace anchorColumn name with ID for presentation layer
        RecordSet newRecordSet = mapAnchorColumnForInitialValues(recordSet);

        RecordSetToJSONMapper recordSetMapper = RecordSetToJSONMapper.getInstance();

        String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
        response.setCharacterEncoding(encoding);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=" + encoding);
        response.setHeader("Cache-control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        // write json object start
        JsonHelper.addObjectStartTag(out);

        // write messages array
        writeMessageToJSONResponse(out);

        // write record set part
        if (recordSet != null) {
            JsonHelper.addCommaSeparator(out, true);
            out.print("    ");
            recordSetMapper.mapRecordSetToJSON(newRecordSet, out, keepCase);
        }
        out.println();

        //end writing json object
        JsonHelper.addObjectEndTag(out, false, true);
        out.flush();

        l.exiting(getClass().getName(), "writeAjaxJsonResponse");
    }

    public void writeAjaxJqxGridData(HttpServletRequest request, HttpServletResponse response, String gridId, RecordSet recordSet) throws IOException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeAjaxJqxGridData", new Object[]{recordSet});
        }

        OasisGrid grid = new OasisGrid();
        grid.setId(gridId);
        grid.setGridId(gridId);
        // Skip setGridDetailDivId since it's only needed to write grid info
        grid.setData((BaseResultSet) request.getAttribute(getDataBeanName(gridId)));
        String gridHeaderBeanName = StringUtils.isBlank(gridId) ? "gridHeaderBean" : gridId + "HeaderBean";
        XMLGridHeader gridHeader = (XMLGridHeader) request.getAttribute(gridHeaderBeanName);
        if (null == gridHeader) {
            gridHeader = (XMLGridHeader) request.getAttribute("gridHeaderBean");
            if (null == gridHeader) {
                throw new AppException("loadGridHeader() must be called before calling writeAjaxJqxGridData()");
            }
        }
        grid.setHeader(gridHeader);

        response.setContentType("application/json");
        String encoding = ApplicationContext.getInstance().getProperty(CharacterEncodingFilter.CHARACTER_ENCODING_DEFAULT);
        response.setCharacterEncoding(encoding);
        PrintWriter out = response.getWriter();
        JsonHelper.addObjectStartTag(out);
        try {
            // write messages array
            writeMessageToJSONResponse(out);
            JsonHelper.addCommaSeparator(out);

            String token = null;
            if (isSaveInProgress()) {
                //generate token.
                token = this.generateToken(request);
                request.getSession().setAttribute(UserSessionIds.PAGE_TOKEN, token);
                request.getSession().setAttribute(UserSessionIds.NEW_TOKEN_GENERATED, "Y");
            } else {
                //make sure that all pages must pass the token either in form submit or ajax
                token = request.getParameter(Globals.TOKEN_KEY);
                if (!StringUtils.isBlank(token)) {
                    request.getSession().setAttribute(UserSessionIds.PAGE_TOKEN, token);
                }
            }
            if (token != null) {
                JsonHelper.writeProperty(out, Globals.TOKEN_KEY, token, 1);
            }

            // Add totalRecords if summaryRecord has field value for "count"
            String totalRecords = recordSet.getSummaryRecord().getStringValue("count", (String) null);
            if (!StringUtils.isBlank(totalRecords)) {
                JsonHelper.writeProperty(out, "totalRecords", totalRecords, 1);
            }

            JqxGridHelper.getInstance().buildJqxGridData(grid, request, out);
        } finally {
            JsonHelper.addObjectEndTag(out, false);
            out.flush();
        }

        l.exiting(getClass().getName(), "writeAjaxJqxGridData");
    }

    /**
     * write message info to json response-"message":[{"category":"","key":"","text":"","confirmedAsYRequired":"N"field":"","rowId":""},...]
     *
     * @param out
     */
    public void writeMessageToJSONResponse(PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeMessageToJSONResponse", new Object[]{out});
        }

        RecordSetToJSONMapper recordSetMapper = RecordSetToJSONMapper.getInstance();

        try {

            //start writing:"message":[{message1},{message2}]
            JsonHelper.writePropertyName(out,"message", 1,true);
            JsonHelper.addArrayStartTag(out);

            // loop all the messages
            Iterator it = MessageManager.getInstance().getMessagesAndConfirmationPrompts();
            while (it.hasNext()) {
                // wrap the message value with " " ";
                Message message = ((Message) it.next());
                JsonHelper.addObjectStartTag(out, 2);
                JsonHelper.writeProperty(out, "category", message.getMessageCategory().toString(), 3, true);
                JsonHelper.writeProperty(out, "key", message.getMessageKey(), 3, true);
                JsonHelper.writeProperty(out, "text", message.getMessage(), 3, true);
                JsonHelper.writeProperty(out, "confirmedAsYRequired",YesNoFlag.getInstance(message.getConfirmedAsYRequired()).toString(), 3, true);
                JsonHelper.writeProperty(out, "field", message.getMessageFieldId(), 3, true);
                JsonHelper.writeProperty(out, "rowId", message.getMessageRowId(), 3, false);
                JsonHelper.addObjectEndTag(out, 2, it.hasNext());
            } //end writing messages part

            JsonHelper.addArrayEndTag(out, 1, false, false);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to generate the  message to json:", e);
            l.throwing(getClass().getName(), "writeMessageToJSONResponse", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "writeMessageToJSONResponse");
    }


    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    protected BaseAction() {
        super();
    }

    public void verifyConfig() {
        if (getReadOnlyDataSource() == null)
            throw new ConfigurationException("The required property 'readOnlyDataSource' is missing.");
    }

    public DataSource getReadOnlyDataSource() {
        return m_readOnlyDataSource;
    }

    public void setReadOnlyDataSource(DataSource readOnlyDataSource) {
        m_readOnlyDataSource = readOnlyDataSource;
    }

    public boolean hasHeaderFileName() {
        return !StringUtils.isBlank(m_headerFileName);
    }

    public String getHeaderFileName() {
        return m_headerFileName;
    }

    public void setHeaderFileName(String headerFileName) {
        m_headerFileName = headerFileName;
    }

    public boolean hasAnchorColumnName() {
        return !StringUtils.isBlank(m_anchorColumnName);
    }

    public String getAnchorColumnName() {
        return m_anchorColumnName;
    }

    public void setAnchorColumnName(String anchorColumnName) {
        m_anchorColumnName = anchorColumnName;
    }


    public boolean getUseMapWithoutPrefixes() {
        if (m_useMapWithoutPrefixes == null) {
            m_useMapWithoutPrefixes = Boolean.valueOf(true);
        }
        return m_useMapWithoutPrefixes.booleanValue();
    }

    public void setUseMapWithoutPrefixes(boolean useMapWithoutPrefixes) {
        m_useMapWithoutPrefixes = Boolean.valueOf(useMapWithoutPrefixes);
    }

    public void setGridHeaderDefinesDisplayableColumnOrder(boolean gridHeaderDefinesDisplayableColumnOrder) {
        m_gridHeaderDefinesDisplayableColumnOrder = Boolean.valueOf(gridHeaderDefinesDisplayableColumnOrder);
    }

    /**
     * To mark a select field which indicate it can be enterable
     *
     * @param request
     * @param fieldId
     */
    public void markFieldAsEnterableSelect(HttpServletRequest request, String fieldId) {
        if (!StringUtils.isBlank(fieldId)) {
            request.setAttribute(fieldId + ".enterableSelect", Boolean.TRUE);
        }
    }

    protected boolean gridHeaderDefinesDisplayableColumnOrder() {
        if (m_gridHeaderDefinesDisplayableColumnOrder == null) {
            m_gridHeaderDefinesDisplayableColumnOrder = Boolean.valueOf(GridHelper.gridHeaderDefinesDisplayableColumnOrder());
        }
        return m_gridHeaderDefinesDisplayableColumnOrder.booleanValue();
    }

    public String getObrExcludeProcessNames() {
        return m_obrExcludeProcessNames;
    }

    public void setObrExcludeProcessNames(String obrExcludeProcessNames) {
        m_obrExcludeProcessNames = obrExcludeProcessNames;
    }

    public String getObrNonGridFields() {
        return m_obrNonGridFields;
    }

    public void setObrNonGridFields(String obrNonGridFields) {
        m_obrNonGridFields = obrNonGridFields;
    }


    public void validateFileUpload(HttpServletRequest request, ActionForm form) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "validateFileUpload", new Object[]{form});
        }

        String contentType = request.getContentType();
        if (!StringUtils.isBlank(contentType) && contentType.toLowerCase().indexOf("multipart/form-data") > -1) {
            if (form != null) {
                Map map = ((DynaActionForm) form).getMap();
                if (map != null) {
                    Iterator it = map.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if (((DynaActionForm) form).get(key) instanceof FormFile) {
                            String subSystem = getSystemAcronym(request);
                            FormFileWrapper formFile = (FormFileWrapper) ActionHelper.getFormFile(form, key);
                            if (!StringUtils.isBlank(formFile.getFileName())) {
                                FileSanitizer fileSanitizer = new FileSanitizer(subSystem);
                                fileSanitizer.validateFile(formFile);
                                ((DynaActionForm) form).set(key, formFile);
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "validateFileUpload", form);
        }
    }

    private String getSystemAcronym(HttpServletRequest request) {
        String acronym = "";

        String contextPath[] = request.getContextPath().split("/");

        acronym = contextPath[2].toUpperCase().trim();
        if (acronym.substring(0,1).equalsIgnoreCase("e")) {
            acronym = acronym.substring(1);
        }

        switch (acronym) {
            case "POLICY":
                acronym = "PM";
                break;
            case "CLAIM":
                acronym = "CM";
                break;
            case "CIS":
                acronym = "CI";
                break;
            case "ADMIN":
                acronym = "AD";
                break;
            default:
                acronym = "";
        }
        return acronym;
    }

    /**
     * check if the disconnected result set fields need to be encoded.
     *
     * @param request
     * @param fields
     * @param dataBeanName
     * @param rs
     * @param xmlGridHeader
     */
    public boolean setDataBeanForDisconnectedResultSet(HttpServletRequest request, OasisFields fields, String dataBeanName, DisconnectedResultSet rs, XMLGridHeader xmlGridHeader) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setDataBeanForDisconnectedResultSet", new Object[]{fields, dataBeanName, rs});
        }
        boolean bRc = true;

        try{
            Map headerMap = xmlGridHeader.getHeaderIndexesByColumnName();
            Map maskedFields = getAllMaskedFields(fields);

            Iterator it = maskedFields.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                String key = (String)entry.getKey();
                if (headerMap.containsKey(key.toUpperCase())) {
                    int col = Integer.parseInt(String.valueOf(headerMap.get(key.toUpperCase())));
                    rs.beforeFirst();
                    while (rs.next()) {
                        Object value = (Object) rs.get(col);
                        Object oValue = ActionHelper.encodeMaskedField(key, value, fields);
                        rs.setString(col, String.valueOf(oValue));
                        l.logp(Level.FINER, BaseAction.class.getName(), "setDataBeanForDisconnectedResultSet", "value: " + value + " oValue: " + oValue + " col: " + col);
                    }
                }
            }

            request.setAttribute(dataBeanName, rs);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "setDataBeanForDisconnectedResultSet", new Object[]{rs});
            }
        }catch(Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to set data bean for disconnected result set", e);
            l.throwing(getClass().getName(), "setDataBeanForDisconnectedResultSet", ae);
            throw ae;
        }
        return bRc;
    }

    /**
     * retrieves only the fields that have been masked.
     *
     * @param  fields
     * @return maskFieldsMap
     */
    public Map getAllMaskedFields(OasisFields fields) {
        Map maskFieldsMap = new HashMap();

        if (fields != null) {
            ArrayList<OasisFormField> maskedFieldArray = fields.getAllFieldList();

            for (OasisFormField f : maskedFieldArray) {
                String fieldName = f.getFieldId();
                int index = fieldName.lastIndexOf(GRID_HEADER_SUFFIX_GH);
                if (index > 0) {
                    fieldName = fieldName.substring(0, index);
                }
                if (f.getIsMasked()) {
                    maskFieldsMap.put(fieldName, fieldName);
                }
            }
        }
        return maskFieldsMap;
    }

    /**
     * Save a new transaction token in the user's current session, creating
     * a new session if necessary.  If you override getTokenConstant, this will save
     * the token using the constant rather than the default STRUTS token.
     * Store the same token with the super class name, to be retrieved in CSRFInterceptor.
     *
     * @param request The servlet request we are processing
     * @param tokenConstant  attribute name where the token will be stored.
     */
    public void saveToken(HttpServletRequest request, String tokenConstant) {
        Logger l = LogUtils.enterLog(getClass(), "saveToken", request);
        // if no token constant was defined, use the default base token processing
        String token = null;
        if (StringUtils.isBlank(tokenConstant)) {
            super.saveToken(request);
            token = (String) request.getSession().getAttribute(Globals.TRANSACTION_TOKEN_KEY);
            tokenConstant = Globals.TRANSACTION_TOKEN_KEY;
        } else {
            token = generateToken(request);
        }

        removeClassActionNameFromSession(request);

        request.getSession().setAttribute(tokenConstant, token);
        String classActionName = getSuperClassName();
        request.getSession().setAttribute(classActionName + UserSessionIds.TOKEN_SUFFIX, token);
        request.getSession().setAttribute(UserSessionIds.SUPER_CLASS_ACTION_NAME, classActionName + UserSessionIds.TOKEN_SUFFIX);

        l.exiting(getClass().getName(), "saveToken");
    }

    public void removeClassActionNameFromSession(HttpServletRequest request) {
        String className = (String)request.getSession().getAttribute(UserSessionIds.SUPER_CLASS_ACTION_NAME);
        if (!StringUtils.isBlank(className)) {
            String test = (String)request.getSession().getAttribute(className);
            request.getSession().removeAttribute(className);
        }
    }

    /**
     * class name will be save in the user session along with the token to tbe retrieve in CSRFInterceptor.
     *
     * @return superClassName
     */
    public String getSuperClassName() {
        String superClassName = super.getClass().getCanonicalName();
        return superClassName;
    }

    /**
     * it will udpate the token been passed in FowardParameter.
     * In Policy, using the global search for a one term policy, after
     * finding the policy, it will invoke MaintainPolicyAction before
     * returning back to the browser.
     *
     * @return
     */
    public void updateCSRFTokenInForwardParameter(HttpServletRequest request) {
        String actionClassToken = getActionClassToken(request);
        if (!StringUtils.isBlank(actionClassToken)) {
            setForwardParameter(request, Globals.TOKEN_KEY, actionClassToken);
        }
    }

    public String getActionClassToken(HttpServletRequest request) {
        String className = getSuperClassName();
        String tokenClassValue = (String) request.getSession().getAttribute(className + UserSessionIds.TOKEN_SUFFIX);

        if (StringUtils.isBlank(tokenClassValue)) {
            tokenClassValue = "";
        }

        return tokenClassValue;
    }

    private DataSource m_readOnlyDataSource;
    private String m_headerFileName;
    private String m_anchorColumnName;
    private Boolean m_useMapWithoutPrefixes = new Boolean(true);
    private Boolean m_gridHeaderDefinesDisplayableColumnOrder;
    private String m_obrExcludeProcessNames;
    private String m_obrNonGridFields;

    private static final String INPUT_RECORD_SET = "inputRecordSet";
    private static final String SAVE_IN_PROGRESS = "saveInProgress";
    private static final String LOADED_DEFAULT_VALUES_FOR_INPUT_RECORD = "LOADED_DEFAULT_VALUES_FOR_INPUT_RECORD";
    private static final String GRID_HEADER_SUFFIX_GH = "_GH";
    private final Logger l = LogUtils.getLogger(getClass());
}
