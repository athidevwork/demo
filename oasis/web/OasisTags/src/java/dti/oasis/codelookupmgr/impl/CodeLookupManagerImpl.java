package dti.oasis.codelookupmgr.impl;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.app.ConfigurationException;
import dti.oasis.app.RefreshParmsEventListener;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.cachemgr.Cache;
import dti.oasis.cachemgr.CacheManager;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.codelookupmgr.dao.CodeLookupDAO;
import dti.oasis.codelookupmgr.dao.CodeLookupJdbcDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.http.Module;
import dti.oasis.http.RequestIds;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.util.BeanDtiUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.OasisUser;
import dti.oasis.util.StringUtils;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;

/**
 * The CodeLookupManager maintains access to all Codelookup List of Values.
 * The following format is supported:<p/>
 * <tt>[delimString][prop*]lookupType lookupDetails</tt><p/>
 * where:
 * <ul>
 *  <li><b>delimString</b>   - an optional delimiter string to use for surrounding dependent field ids in the lookupDetails.<br>
 *                              Whenever there one or more dependent fields specified in the lookupDetails,
 *                              and the CodeLookupManager is configured to automatically reload field-dependent lookups
 *                              (setting the <b>automaticallyReloadFieldDependentLookups</b> to <b>true</b>),
 *                              the lookup list will automatically reload through AJAX.<br>
 *                              You can override this behavior by specifying on of the AJAX or NO_AJAX properties.<br>
 *                              The default is the '^' string.</li>
 *  <li><b>prop*</b>         - 0 to many properties. The supported properties are {AJAX, NO_AJAX, CACHE, NO_CACHE, DISPLAY_SELECTED_OPTIONS_FIRST, NO_DISPLAY_SELECTED_OPTIONS_FIRST }. See below for details on the supported properties.</li>
 *  <li><b>lookupType</b>    - the requested type of lookup. The supported lookup types are { LIST, LOOKUP, SELECT, EXEC } (see below for lookupType details).</li>
 *  <li><b>lookupDetails</b> - the details relevant to this lookupType, optionally specifying any number of field placeholders,
 *                              where each placeholder defines a valid fieldId surrounded with the delimiter string (ex. ^fieldId^, assuming '^' is the delimeter string)</li>
 * </ul>
 * <p/>
 * Properties:
 * <ul>
 *  <li><b>AJAX_RELOAD</b>         - force reloading the lookup list with AJAX if all of the following are true:
 *                              This property overrides the related configuration property.
 *   <ul>
 *    <li>the lookupDetails specify any field placeholders</li>
 *    <li>one of the dependant fields has changed</li>
 *   </ul>
 *  </li>
 *  <li><b>NO_AJAX_RELOAD</b>      - force the CodeLookupManager to NOT reload the lookup list with AJAX when a dependent field has changed.</li>
 *                              This property overrides the related configuration property.
 *  <li><b>CACHE</b></li>   - request the CodeLookupManager to cache the lookup list.
 *                              By default, all LOOKUP lovs are cached.
 *                              Note that caching of lovs is only supported for lookups that do NOT contain field placeholders.
 *                                Also, caching of LOVs are kept unique per dbPoolId, not per userId. 
 *  <li><b>NO_CACHE</b></li> - force the CodeLookupManager to NOT cache the lookup list.
 *  <li><b>ADD_SELECT_OPTION</b></li>   - force the CodeLookupManager to add a "-SELECT-" option as the first item in this list.
 *                              This property overrides the related configuration property.
 *  <li><b>NO_ADD_SELECT_OPTION</b></li>   - force the CodeLookupManager to NOT add a "-SELECT-" option as the first item in the list.
 *                              This property overrides the related configuration property.
 *  <li><b>ALL_SELECT_OPTION</b></li>   - force the CodeLookupManager to add a "-ALL-" option as the first item in this list.
 *                              This property overrides the related configuration property.
 * <li><b>DISPLAY_SELECTED_OPTIONS_FIRST</b></li>   - force the CodeLookupManager to display the selected options first for multi-selected fields.
 *                              This property overrides the related configuration property.
 *  <li><b>NO_DISPLAY_SELECTED_OPTIONS_FIRST </b></li>   - force the CodeLookupManager to NOT attempt to sort and display the selected options first for multi-selected fields.
 *                              This property overrides the related configuration property.
 *  <li><b>EXPIRED_OPTION </b></li>  - instruct system to identify expired options, so they are disabled from user's input
 * </ul>
 * <p/>
 * Lookup Types/Details:
 * <ul>
 *  <li><b>LIST</b>     - explicitly define a list of one or more 'code,label' pairs in the format: <br><tt>LIST:code1,label1,code2,label2,codeN,labelN</tt></li>
 *  <li><b>LOOKUP</b>   - specify the value of a LOOKUP_TYPE_CODE from the 'LOOKUP_CODE' table in the format: <br><tt>LOOKUP [lookupTypeCode][SHORT_DESC|LONG_DESC]</tt></li>
 *                          The lookupTypeCode parameter must match an existing LOOKUP_TYPE_CODE in the LOOKUP_CODE table.
 *                          The second parameter is optional, and is used to indicate which description to load as the label.
 *                          If SHORT_DESC is specified, the short description is used as the label.
 *                          If LONG_DESC is specified, the long description is used as the label.
 *                          By default, the short description is used as the label.
 *  <li><b>SELECT</b>   - a SQL SELECT statement that returns at least 2 columns, where the 1st column is the code and the 2nd column is the label.
 *                          The select statement may contain any number of field placeholders<br>
 *                          For example:<br>
 *                          <tt>SELECT code, label FROM mytable WHERE field1 = '^field1^' and field2 = '^field2^'</tt></li>
 *  <li><b>EXEC</b>     - execute the given stored procedure, using the specified code and label column indexes to retrieve the code/label pairs from the REF CURSOR.<br>
 *                          The format of this lookup type is:<br>
 *                          EXEC [codeColumnIndex][labelColumnIndex][storedProcName('^fieldId1^', '^fieldId2^','^fieldIdN^', ?)]<br>
 *                          where:
 *   <ul>
 *    <li>codeColumnIndex - the 1-based index of the result set column that contains the lookup code</li>
 *    <li>labelColumnIndex - the 1-based index of the result set column that contains the lookup label</li>
 *    <li>storedProcName - the name of the stored procedure</li>
 *    <li>fieldId1, fieldId2, fieldIdN - any number of input parameters that use Oasis fieldId values as input.</li>
 *    <li>? - placeholder for the OUT REF CURSOR</li>
 *   </ul>
 *  </li>
 * </ul>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 30, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/25/2007       wer         Added configuration properties to specify the code and label values to use when adding a SELECT item.
 * 01/31/2007       wer         Added check for invalid format of LIST type of lookup
 * 02/01/2007       wer         Moved the getSelectOptionLabel and getSelectOptionCode methods into the CodeLookupManager class.
 * 01/14/2008       Kenney      Replace BeanUtils.getProperty(form, fieldName) with BeanUtils.getArrayProperty(form, fieldName)
 *                              in getFieldValue method for multiple selection
 * 04//08/2008      Guang       Added moveUpSelectedOptions() to support setSelectedOptions() with displaySelectedOptionsFirst parameter
 * 04/10/2008       wer         Enhanced fields and LOVs to be cached per userId
 * 02/26/2010       kshen       Fixed the bug that if nothing is selected of a multi select field, the first one will be displayed as selected.
 * 01/11/2011       ryzhao      Fixed the bug in processFieldPlaceholders method that if the field value include ' character, replace ' with ''.
 * 01/25/2011       wfu         116540 - Removed the logic which left the SELECT option out of LOV if the field is read only 
 *                                       and there is only one item in the list.
 * 04/21/2011       James       Issue#119774 Remove logic from CodeLookupManager to use defaults
 * 11/18/2011       Jerry       Issue#126056 Filter drop down options which both key-value are Null Objects in the LOV option list.
 * 10/24/2012       jshen       Issue 136012 - Avoid the null value of the LabelValueBean object in method addSelectItem().
 * 12/16/2013       fcb         150767 - refactored to use RefreshParmsEventListener.
 * 02/14/2014       fcb         Added ALL select option.
 * 03/17/2014       Parker      Issue#148577 Support caching a read-only and non-read-only version of the LOVs.
 * 10/04/2017       cesar       #188804 - Modified processFieldPlaceholders() to check if the field needs to be Base64 decoded.
 * 02/09/2018       ylu         191383: reload lov field if it has dependency field value
 * 02/12/2018       ylu         191469: handle with fields is null when load Lov by ajaxcall
 * 02/28/2018       cesar       191524 - Modified processListOfValues() to decode field to check which option will be selected.
 * 10/19/2018       jdingle     196002 - Add userid to lov cache id.
 * ---------------------------------------------------
 */
public class CodeLookupManagerImpl extends CodeLookupManager implements RefreshParmsEventListener {
    /**
     * Get the List of Values for the given record with the fields:
     *  aclass
     *  fieldId
     */
    public ArrayList getListOfValues(Connection conn, HttpServletRequest request, Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getListOfValues", new Object[]{request, inputRecord});
        }

        String fieldId = inputRecord.getStringValue("fieldId");
        if (fieldId == null) {
            throw new AppException("The fieldId is required for this request");
        }
        String actionClassName = inputRecord.getStringValue("aclass");
        if (actionClassName == null) {
            throw new AppException("The aclass is required for this request");
        }

        OasisFormField fld = getFieldFromCache(request, actionClassName, fieldId);

        processListOfValues(conn, null, request, null, fld);

        ArrayList listOfValues = (ArrayList) request.getAttribute(fieldId + "LOV");

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getListOfValues", listOfValues);
        }
        return listOfValues;
    }

    /**
     * Creates ArrayList objects for the passed OasisFormField with
     * a corresponding "List of Values". The ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     *
     * @param conn        JDBC Connection
     * @param form        DynaActionForm
     * @param request     HttpServletRequest
     * @param fields      the OasisFields in context for this request
     * @param fld         form field
     */
    public void processListOfValues(Connection conn, ActionForm form, HttpServletRequest request, OasisFields fields, OasisFormField fld) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processListOfValues", new Object[]{form, request, fld});
        }

        // Only process the fields with a LovSql attribute defined.
        try {
            if (!StringUtils.isBlank(fld.getLovSql())) {

                ArrayList list = null;
                boolean  displaySelectedOptionsFirst = false;
                String originalLovSql = fld.getLovSql().trim();

                if (isLovCached(fld)) {
                    try {
                        list = getLovFromCache(fld);
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processListOfValues", "Retrieved the LOV from cache for lovSql: " + originalLovSql);
                        }
                    }
                    catch (Exception e) {
                        l.logp(Level.WARNING, getClass().getName(), "processListOfValues", "Faile to get the LOV SQL from cache.");
                        // The Cache must have been cleared after calling isLovCached. Fall through and let the LOV be reloaded.
                    }
                }
                if (list == null) {

                    Map placeholderFieldIds = new HashMap();
                    String lovSql = originalLovSql;

                    // Setyp the field and list delimeters to the defaults.
                    String fieldDelim = DEFAULT_FIELD_DELIMITER;
                    String listDelim = DEFAULT_LIST_DELIMITER;

                    // Setup the CodeLookup Properties
                    CodeLookupProperties codeLookupProps = new CodeLookupProperties(getAjaxReloadDefault(), getAddSelectOptionDefault(),getDisplaySeletedOptionsFirst());

                    // If the next character is '[', handle it as either a property or the delimter character
                    while (lovSql.charAt(0) == '[') {
                        if (lovSql.indexOf(']') < 0) {
                            throw new IllegalArgumentException("Invalid List of Values format. Each '[' character must be terminated by a corresponding ']'. lovSql string: " + lovSql);
                        }
                        String token = lovSql.substring(1, lovSql.indexOf(']')).trim();
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processListOfValues", "token = " + token);
                        }

                        parseProperty(token, codeLookupProps);

                        if (codeLookupProps.hasDelimString()) {
                            fieldDelim = listDelim = codeLookupProps.getDelimString();
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "processListOfValues", "token = " + token + "; fieldDelim = " + fieldDelim + "; listDelim = " + listDelim);
                            }
                        }

                        lovSql = lovSql.substring(lovSql.indexOf(']') + 1).trim();
                    }

                    // if this Lov has loaded but has dependency field, let's reload for it,
                    if (fields != null &&
                            fields.isLOVsLoaded()) {
                        boolean isHasDependency = !StringUtils.isBlank(fieldDelim) && lovSql.contains(fieldDelim);

                        if (!isHasDependency) {
                            return;
                        }
                    }

                    displaySelectedOptionsFirst = codeLookupProps.displaySelectedOptionsFirst(); // 81259
                    if (lovSql.startsWith("LIST")) {
                        // If the sql starts with LIST, then this isn't sql, but rather an actual list
                        list = parseLovSqlForList(fld, lovSql, listDelim);
                    //                System.out.println("field '"+fld.getFieldId() + "' list.size()1 = " + list.size());
                    }
                    else if (lovSql.startsWith("LOOKUP")) {
                        // Handle LOOKUP type of LOV sql
                        String lookupTypeCode = parseLookupTypeCode(fld, lovSql);
                        list = getLookupLov(fld.getFieldId(), conn, lovSql, lookupTypeCode, displayLongDescription(lovSql));

                        // If the CACHE or NO_CACHE property was not specified directly, default it to CACHE for LOOKUP lovs
                        if (cacheLOOKUPLovsByDefault() && !codeLookupProps.wasCacheLOVSpecified()) {
                            codeLookupProps.setCacheLOV(true);
                        }
                    }
                    else {
                        String sql = lovSql;

                        if (lovSql.indexOf(fieldDelim) >= 0) {
                            // Parse the SQL string, resolving all field placeholders with the actual values
                            sql = processFieldPlaceholders(request, form, fields, fld, lovSql, fieldDelim, placeholderFieldIds);
                        }

                        if (sql.toUpperCase().startsWith("EXEC")) {
                            list = getCodeLookupDAO().executeLovStoredProcedure(fld.getFieldId(), conn, sql);
                        } else  if (sql.toUpperCase().startsWith("SELECT")){
                            list = getCodeLookupDAO().executeLovSQL(fld.getFieldId(), conn, sql);
                        } else {
                            throw new IllegalArgumentException("Invalid List of Values format loading the Lookup List of Values. lovSql string: " + sql);
                        }
                    //                    System.out.println("field '"+fld.getFieldId() + "' list.size()1 = " + list.size());

                        if (codeLookupProps.ajaxReload() && placeholderFieldIds.size() > 0) {
                            // Create the AJAX URL with placeholders for each placeholder field id
                            StringBuffer buf = new StringBuffer();
                            buf.append(Module.getRelativePath(request, "~"))
                                .append("/codelookupmgr/loadListOfValues.do?fieldId=").append(fld.getFieldId())
                                .append("&aclass=").append(getActionClassName(request, fields));
                            Iterator iter = placeholderFieldIds.keySet().iterator();
                            while (iter.hasNext()) {
                                String fieldId = (String) placeholderFieldIds.get((String) iter.next()) ;
                                buf.append("&").append(fieldId).append("=^").append(fieldId).append("^");
                            }

                            buf.append("&_isAJAX=Y&_delim=^");
                            String ajaxUrl = buf.toString();
                            addAjaxUrl(request, ajaxUrl);

                            // fields will be null if this is a AJAX request to reload the list of values,
                            // in which case we don't need to append the Ajax URL to the dependent fields;
                            // it is already done on the initial request.
                            if (fields != null) {

                                // Cache the OasisFormField for this fieldId/actionClass
                                cacheField(getActionClassName(request, fields), fld);

                                // Add the AJAX URL to each dependent field so this lookup list is refreshed when the dependent field is changed
                                // Make sure not to add it to the current field to avoid an endless loop.
                                iter = placeholderFieldIds.keySet().iterator();
                                while (iter.hasNext()) {
                                    String fieldId = (String) placeholderFieldIds.get((String) iter.next()) ;
                                    if (!fieldId.equals(fld.getFieldId())) {
                                        OasisFormField dependentField = (OasisFormField) fields.get(fieldId);

                                        // Check for null due to potential for multiple fieldsMap like
                                        // sets of OasisFields attributes
                                        if (!(dependentField == null)) {
                                            dependentField.appendAjaxURL(ajaxUrl);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add the -ALL- item as the first item
                    if (codeLookupProps.addAllOption() ) {
                        String code = getAllOptionCode();

                        // If the field is readonly, do not show the -ALL- text; show it as an empth
                        String label = (fld.getIsReadOnly() || list.size() == 0) ? "" : getAllOptionLabel();

                        list.add(0, new LabelValueBean(label, code));
                    }

                    // add a storage manager to flag the label for empty option.
                    if (codeLookupProps.isUseLabelForEmptyOption()) {
                        RequestStorageManager.getInstance().set(fld.getFieldId() + RequestStorageIds.USE_LABEL_FOR_EMPTY_OPTION, true);
                    }

                    // Add the -SELECT- item as the first item
                    // unless the field is a radio button,
                    // or the field is readonly and list contains 1 item.
                    if (addSelectItem(codeLookupProps, fld, list)) {

                        String code = getSelectOptionCode();

                        // If the field is readonly, do not show the -SELECT- text; show it as an empth
                        String label = (fld.getIsReadOnly() || list.size() == 0) ? "" : getSelectOptionLabel();

                        list.add(0, new LabelValueBean(label, code));
                    }

                    // Cache the list of values if requested to do so, and there are no field placeholders in the lov sql.
                    if (codeLookupProps.cacheLOV() && placeholderFieldIds.size() == 0) {
                        cacheLov(fld, list);
                        if (l.isLoggable(Level.FINE)) {
                            l.logp(Level.FINE, getClass().getName(), "processListOfValues", "Cached the LOV for the lov sql: " + originalLovSql);
                        }
                    }
                    setFieldsWithExpiredOptions(request, fld.getFieldId(),codeLookupProps);
                }

                // get existing value from the form
                Object ov = getFieldValue(request, form, fields, fld.getFieldId());
                if (fld.getIsMasked()) {
                    ov = ActionHelper.decodeField(ov);
                }

                // If the field is multi-selection style, parse and populate it as String array
                if ("MULTISELECT".equals(fld.getDisplayType()) ||
                    "MULTISELECTPOPUP".equals(fld.getDisplayType()) ||
                    "MULTIBOX".equals(fld.getDisplayType())) {
                    if (ov == null) {
                        ov = new String[]{};
                    } else if (!(ov instanceof String[])) {
                        String sOv = (String) ov.toString();
                        String[] valueList;
                        if (StringUtils.isBlank(sOv)) {
                            ov = new String[]{};
                        } else {
                            valueList = sOv.split(",");
                            ov = valueList;
                        }
                    }
                }

                // Set the selected value(s)
                if (ov instanceof String[]) {
                    setSelectedOptions(form, request, fld, list, (String[]) ov,  displaySelectedOptionsFirst);
                }
                else {
                    setSelectedOption(form, request, fld, list, ov == null ? "" : ov.toString());
                }

                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "processListOfValues", "Storing LOV in request for " + fld.getFieldId());
                }
                request.setAttribute(fld.getFieldId() + "LOV", list);
                fld.setLovList(list);
            }
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to process the list of values for field <" + fld.getFieldId() + ">", e);
            l.throwing(getClass().getName(), "processListOfValues", ae);
            throw ae;
        }
        l.exiting(getClass().getName(), "processListOfValues");
    }

    private void setFieldsWithExpiredOptions(HttpServletRequest request, String fieldId, CodeLookupProperties codeLookupProps) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldsWithExpiredOptions");
        }
        if (codeLookupProps.processExpiredOptions()) {
            StringBuffer fieldsWithExpiredOptions = new StringBuffer(fieldId).append(",");
            if (request.getAttribute(RequestIds.SELECT_FIELDS_WITH_EXPIRED_OPTION) != null) {
                fieldsWithExpiredOptions.append((String) request.getAttribute(RequestIds.SELECT_FIELDS_WITH_EXPIRED_OPTION));
            } else {
                // first time.. let us store the system-wide indicator as well.
                request.setAttribute(RequestIds.EXPIRED_OPTION_SUFFIX, getSuffixIndicatorForExpiredOption());
            }
            l.logp(Level.FINE, getClass().getName(), "setFieldsWithExpiredOptions", fieldsWithExpiredOptions.toString());
            request.setAttribute(RequestIds.SELECT_FIELDS_WITH_EXPIRED_OPTION, fieldsWithExpiredOptions.toString());
        }
    }

    private String getSuffixIndicatorForExpiredOption() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSuffixIndicatorForExpiredOption");
        }
        String suffix = "(expired)";
        if (ApplicationContext.getInstance().hasProperty("expired.option.suffix")) {
            suffix = (String) ApplicationContext.getInstance().getProperty("expired.option.suffix");
        }
        l.logp(Level.FINE, getClass().getName(), "getSuffixIndicatorForExpiredOption",suffix) ;
        return suffix;
    }

    private boolean addSelectItem(CodeLookupProperties codeLookupProps, OasisFormField fld, ArrayList list) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addSelectItem", new Object[]{codeLookupProps, fld, list});
        }

        boolean addSelectItem = codeLookupProps.addSelectOption();

        if (addSelectItem) {

            // Don't add a SELECT option for radion buttons
            if (fld.getDisplayType().equals("RADIOBUTTON"))
                addSelectItem = false;

            // For issue 116540: System should not specifically leaves the SELECT option out of the LOV
            // when the field is read only and there's only one item in the list. So remove below logic.
            // Don't add a SELECT option if the field is readonly and there is exactly one match
            //else if (fld.getIsReadOnly() && list.size() == 1)
            //    addSelectItem = false;

            // Don't add a SELECT option if the first item is already the SELECT option
            else if (list.size() > 0) {
                LabelValueBean lvBean = ((LabelValueBean)list.get(0));
                if (lvBean != null && lvBean.getValue() != null && lvBean.getLabel() != null &&
                    (lvBean.getValue().trim().equals(getSelectOptionCode()) ||
                    lvBean.getValue().trim().equals(DEFAULT_SELECT_OPTION_CODE) ||
                    lvBean.getLabel().trim().toUpperCase().equals(getSelectOptionLabel()) ||
                    lvBean.getLabel().trim().toUpperCase().equals(DEFAULT_SELECT_OPTION_LABEL))) {

                    addSelectItem = false;
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addSelectItem", String.valueOf(addSelectItem));
        }
        return addSelectItem;
    }

    private void parseProperty(String token, CodeLookupProperties codeLookupProperties) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "parseProperty", new Object[]{token, codeLookupProperties});
        }

        String prop = token.toUpperCase();
        if ("AJAX_RELOAD".equals(prop)) {
             codeLookupProperties.setAjaxReload(true);
        }
        else if ("NO_AJAX_RELOAD".equals(prop)) {
            codeLookupProperties.setAjaxReload(false);
        }
        else if ("CACHE".equals(prop)) {
            codeLookupProperties.setCacheLOV(true);
        }
        else if ("NO_CACHE".equals(prop)) {
            codeLookupProperties.setCacheLOV(false);
        }
        else if ("ADD_SELECT_OPTION".equals(prop)) {
            codeLookupProperties.setAddSelectOption(true);
        }
        else if ("NO_ADD_SELECT_OPTION".equals(prop)) {
            codeLookupProperties.setAddSelectOption(false);
        }
        else if ("DISPLAY_SELECTED_OPTIONS_FIRST".equals(prop)) {
            codeLookupProperties.setDisplaySelectedOptionsFirst(true);
        }
        else if ("NO_DISPLAY_SELECTED_OPTIONS_FIRST".equals(prop)) {
            codeLookupProperties.setDisplaySelectedOptionsFirst(false);
        }
       else if ("EXPIRED_OPTION".equals(prop)) {
             codeLookupProperties.setProcessExpiredOptions(true);
        }
       else if ("ADD_ALL_OPTION".equals(prop)) {
            codeLookupProperties.setAddAllOption(true);
        } else if ("DISPLAY_RO_LABEL_FOR_EMPTY_CODE".equals(prop)) {
            codeLookupProperties.setUseLabelForEmptyOption(true);
        }
        else {
            codeLookupProperties.setDelimString(token);
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "parseProperty", "After parsing the property, codeLookupProperties = " + codeLookupProperties);
        }
        l.exiting(getClass().getName(), "parseProperty");
    }

    private String parseLookupTypeCode(OasisFormField field, String lookupLovSql) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "parseLookupTypeCode", new Object[]{lookupLovSql});
        }

        String lookupTypeCode = null;
        if (lookupLovSql.indexOf('[') >= 0 && lookupLovSql.indexOf(']') >= 0) {
            lookupTypeCode = lookupLovSql.substring(lookupLovSql.indexOf('[') + 1, lookupLovSql.indexOf(']')).trim();
        }
        else {
            throw new IllegalArgumentException("Invalid LOOKUP List of Values format for specifying the lookupTypeCode for field'"+field.getFieldId()+"'; lov sql string: " + lookupLovSql);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "parseLookupTypeCode", lookupTypeCode);
        }
        return lookupTypeCode;
    }

    private boolean displayLongDescription(String lookupLovSql) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "displayLongDescription", new Object[]{lookupLovSql,});
        }

        boolean displayLongDesc = lookupLovSql.toUpperCase().indexOf("LONG_DESC") >= 0;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "displayLongDescription", Boolean.valueOf(displayLongDesc));
        }

        return displayLongDesc;
    }

    public ArrayList getLookupLov(String lookupTypeCode, boolean displayLongDesc) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLookupLov", new Object[]{lookupTypeCode, displayLongDesc});
        }

        String fieldId = null;
        Connection conn = null;
        String lookupLovSql = null;

        //pass null for fieldId, conn and lookupLovSQL, to call overloaded method
        ArrayList codeList = getLookupLov(fieldId, conn,  lookupLovSql,  lookupTypeCode,  displayLongDesc );

        l.exiting(getClass().getName(), "getLookupLov",codeList);
        return codeList;
    }

    private ArrayList getLookupLov(String fieldId, Connection conn, String lookupLovSql, String lookupTypeCode, boolean displayLongDesc) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLookupLov", new Object[]{lookupTypeCode});
        }

        ArrayList list = getCodeLookupDAO().executeLovLookup(fieldId, conn, lookupLovSql, lookupTypeCode, displayLongDesc);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLookupLov", list);
        }

        return list;
    }

    private void cacheField(String actionClassName, OasisFormField fld) {
        Cache cache = getFieldCache();
        cache.put(getFieldCacheId(actionClassName, fld.getFieldId()), fld);
    }

    protected OasisFormField getFieldFromCache(HttpServletRequest request, String actionClassName, String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldFromCache", new Object[]{request, actionClassName, fieldId});
        }

        OasisFormField field = null;
        if (getFieldCache().contains(getFieldCacheId(actionClassName, fieldId))) {
            field = (OasisFormField) getFieldCache().get(getFieldCacheId(actionClassName, fieldId));
        }
        else {
            // The OasisFormField is not in cache. Try to load the OasisFields for this action class, and get the field from there.
            try {
                ActionHelper.securePage(request, actionClassName);
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException("Failed to secure the page for action class: " + actionClassName, e);
                l.throwing(getClass().getName(), "getFieldFromCache", ae);
                throw ae;
            }

            OasisFields fields = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
            if (fields == null) {
                throw new AppException("\"Failed to load the OasisFields for action class: " + actionClassName);
            }
            field = fields.getField(fieldId);
            cacheField(actionClassName, field);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldFromCache", field);
        }
        return field;
    }

    protected String getFieldCacheId(String actionClassName, String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldCacheId", new Object[]{actionClassName, fieldId});
        }

        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_DBPOOLID);
        String fieldCacheId = dbPoolId + ":" + actionClassName + ":" + fieldId;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldCacheId", fieldCacheId);
        }
        return fieldCacheId;
    }

    protected boolean isLovCached(OasisFormField fld) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isLovCached", new Object[]{fld});
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "isLovCached", "fld = " + fld);
        }

        Cache cache = getLovCache();
        boolean isLovCached = cache.contains(getLovCacheId(fld));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isLovCached", Boolean.valueOf(isLovCached));
        }
        return isLovCached;
    }

    protected void cacheLov(OasisFormField field, ArrayList lov) {
        Cache cache = getLovCache();
        cache.put(getLovCacheId(field), lov);
    }

    protected ArrayList getLovFromCache(OasisFormField field) {
        Cache cache = getLovCache();
        return (ArrayList) cache.get(getLovCacheId(field));
    }

    protected String getLovCacheId(OasisFormField field) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLovCacheId", new Object[]{field});
        }

        String dbPoolId = (String) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_DBPOOLID);
        OasisUser oasisuser = (OasisUser) UserSessionManager.getInstance().getUserSession().get(IOasisAction.KEY_OASISUSER);
        String userId =oasisuser.getUserId();
        String lovCacheId = dbPoolId + ":" + userId + ":" +field.getLovSql().trim() + ":"+ field.getIsReadOnly();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLovCacheId", lovCacheId);
        }
        return lovCacheId;
    }

    private String getActionClassName(HttpServletRequest request, OasisFields fields) {
        String actionClassName = null;
        if (fields != null) {
            actionClassName = fields.getActionClassName();
        }
        else {
            actionClassName = (String) getFieldValue(request, null, fields, "aclass");
        }
        return actionClassName;
    }

    private void addAjaxUrl(HttpServletRequest request, String ajaxUrl) {
        StringBuffer ajaxUrls = null;
        int urlIdx = 1;
        if (request.getAttribute("ajaxUrls") != null) {
            ajaxUrls = (StringBuffer) request.getAttribute("ajaxUrls");
            int urlStrIdx = ajaxUrls.lastIndexOf("URL[");
            if (urlStrIdx >= 0) {
                urlIdx = Integer.parseInt(ajaxUrls.substring(urlStrIdx + 4, urlStrIdx + 5)) + 1;
            }
        } else {
            ajaxUrls = new StringBuffer();
        }
        ajaxUrls.append("URL[").append(urlIdx).append("] ").append(ajaxUrl);
        request.setAttribute("ajaxUrls", ajaxUrls);
    }

    /**
     * Parse the LOV string, replacing all field placeholders with the actual field values
     *
     * @param placeholderFieldIds a list that will get populated with the placeholder fieldIds
     * @return the LOV string with field placeholders resolved to the matching field values.
     */
    protected String processFieldPlaceholders(HttpServletRequest request, ActionForm form,
                                              OasisFields fields, OasisFormField fld,
                                              String lovSql, String delim,
                                              Map placeholderFieldIds // OUTPUT Parameter
                                              ) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFieldPlaceholders", new Object[]{fld, form});
        }

        StringBuffer sql = new StringBuffer();

        // loop through the tokens
        StringTokenizer tok = new StringTokenizer(lovSql, delim, true);
        boolean isDelim = false;
        while (tok.hasMoreTokens()) {
            // get token
            String str = tok.nextToken();

            // we've encountered an opening delimiter before
            if (isDelim) {
                // we've found the closing delimiter, reset the flag
                if (str.equals(delim))
                    isDelim = false;
                else {
                    // we've found the text between the opening and closing
                    // delimiters. This should be an OasisFormField fieldid.
                    str = str.trim();
                    try {
                        // The ActionForm should contain a property that
                        // matches the fieldid
                        Object val = null;
                        val = getFieldValue(request, form, fields, str);
                        if (val == null) {
                            val = "";
                        }
                        else if (val instanceof String[]) {
                            val = StringUtils.arrayToDelimited((String[]) val, ",", false, false, true);
                        }

                        // Issue 116543
                        // If there is a ' in the val, we need to put an escape character ' before this ' so that the sql will be executed correctly.
                        // Replace ' with '' in the val.
                        val = String.valueOf(val.toString().replaceAll("'", "''"));

                        //check if field id is masked.
                        if (fields != null && fields.containsKey(str)) {
                            if (!StringUtils.isBlank(String.valueOf(val)) && fields.getField(str).getIsMasked() && ActionHelper.isBase64(val)) {
                                val = ActionHelper.decodeField(val);
                            }
                        }

                        // stick the value in the sql
                        sql.append(val.toString());

                        // Add the placeholder field id to the output list
                        placeholderFieldIds.put(str, str);

                    }
                    catch (Exception e) {
                        throw new AppException("Failed to find the property '" + str + "' in the ActionForm:" + form + " or the request attributes.", e);
                    }
                }
            } else {
                // we found a beginning delimiter
                if (str.equals(delim))
                    isDelim = true;
                else // we found some valid sql, append it
                    sql.append(str);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processFieldPlaceholders", sql);
        }
        return sql.toString();
    }

    /**
     * Parse a comma delimited String to get an list of label/value pairs
     *
     * @param lov    comma delimited string
     * @param sDelim Delimiter
     * @return ArrayList of LabelValueBean objects
     */
    protected ArrayList parseLovSqlForList(OasisFormField field, String lov, String sDelim) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "parseLovSqlForList", new Object[]{lov, sDelim});
        }

        ArrayList list = null;
        try {
            lov = lov.substring(5);
            list = new ArrayList();
            StringTokenizer tok = new StringTokenizer(lov, sDelim);
            while (tok.hasMoreTokens()) {
                String value = tok.nextToken();
                //convert NULL Object to empty 
                if (null == value) {
                    value = "";
                }
                String label = (tok.hasMoreTokens()) ? tok.nextToken() : value;
                list.add(new LabelValueBean(label, value));
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to parse the lov sql for field'"+field.getFieldId()+"', lov sql: " + lov, e);
            l.throwing(getClass().getName(), "parseLovSqlForList", ae);
            throw ae;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "parseLovSqlForList", list);
        }
        return list;
    }

    /**
     * Set the selected option for the given ListOfValues when only one item may be selected
     *
     * @param form    STRUTS ActionForm (DynaActionForm)
     * @param request current request
     * @param fld     Current form field
     * @param list    ArrayList of LabelValueBean objects
     * @param value   Item that may be selected among list
     */
    protected void setSelectedOption(ActionForm form, HttpServletRequest request, OasisFormField fld,
                                     ArrayList list, String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setSelectedOptions", new Object[]{form, request, fld, list, value});
        }

        if (value == null) value = "";
        int sz = list.size();

        // Just in case the given value doesn't exist in the list of values,
        // initialize the selectedValue to the first value from list, if available.
        String selectedValue = (sz == 0) ? null : ((LabelValueBean) (list.get(0))).getValue();

        // Loop through list, looking for a match on the value from the request.
        // If we have a match, then the value is still in the list of values.
        for (int i = 0; i < sz; i++) {
            String v = ((LabelValueBean) (list.get(i))).getValue();
            if (v == null) v = "";
            if (v.equals(value)) {
                selectedValue = value;
                break;
            }
        }

        // set the selected value
        setFieldValue(request, form, fld, selectedValue);

        l.exiting(getClass().getName(), "setSelectedOption");
    }

    /**
     * Set the selected option for the given ListOfValues when more than one item is selected
     *
     * @param form    STRUTS ActionForm (DynaActionForm)
     * @param request current request
     * @param fld     Current form field
     * @param list    ArrayList of LabelValueBean objects
     * @param values  Array of items that may be selected among list
     */
      protected void setSelectedOptions(ActionForm form, HttpServletRequest request, OasisFormField fld,
                                      ArrayList list, String[] values) {
           setSelectedOptions(form, request, fld, list, values, false);
      }

    /**
     * Set the selected option for the given ListOfValues when more than one item is selected
     *
     * @param form    STRUTS ActionForm (DynaActionForm)
     * @param request current request
     * @param fld     Current form field
     * @param list    ArrayList of LabelValueBean objects
     * @param values  Array of items that may be selected among list
     * @param displaySelectedOptionsFirst  display the selected options first
     */
    protected void setSelectedOptions(ActionForm form, HttpServletRequest request, OasisFormField fld,
                                      ArrayList list, String[] values, boolean displaySelectedOptionsFirst) {

        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setSelectedOptions", new Object[]{form, request, fld, list, values});
        }

        // store the matched options as a hashtable, with the key of the hashtable beging the index of the LOV: list
        // so later, we can manipulate the order of the LOV if needed.
        Hashtable selectedOptions = new Hashtable();
        if (values.length == 0 || values[0] == null) values = new String[]{""};

        int sz = list.size();
        int sz1 = values.length;
        ArrayList value2 = new ArrayList();
        StringBuffer multiSelectTxtBuffer = new StringBuffer();

        // Loop through list, looking for a match on the values from the request.
        // If we have a match, add the matching value to a new arraylist
        for (int i = 0; i < sz; i++) {
            for (int j = 0; j < sz1; j++) {
                if ((((LabelValueBean) (list.get(i))).getValue()).equals(values[j])) {

                    value2.add(values[j]);
                    selectedOptions.put(new Integer(i),list.get(i));

                    //for multiselectPopup
                    if(multiSelectTxtBuffer.length()>0)
						multiSelectTxtBuffer.append(",");
					multiSelectTxtBuffer.append((((LabelValueBean) (list.get(i))).getLabel()));
                }
            }
        }

        if (displaySelectedOptionsFirst) {
            moveUpSelectedOptions(list,selectedOptions );
        }

        // If we didn't find any matching values, get first value from list, if available
        // but only if this is a dropdown list.
        if (value2.size() == 0 && sz > 0 && fld.getDisplayType() != null && fld.getDisplayType().equals("SELECT")) {
            value2.add(((LabelValueBean) (list.get(0))).getValue());
        }

        // set the selected values
        setFieldValue(request, form, fld, value2.toArray(new String[value2.size()]));
        request.setAttribute(fld.getFieldId()+"MultiSelectText", multiSelectTxtBuffer.toString());

        l.exiting(getClass().getName(), "setSelectedOptions");
    }


   /** In order to Move up the selected options, we first delete the selectedOptions
     *  and then add them back to the begining
     * @param list : list of LabelValueBean
     * @param selectedOptions: the selected LabelValueBean, key'ed by Integers (the index from the list)
     */
    private void moveUpSelectedOptions(List list, Map selectedOptions) {
        // temporary arraylist to store the to-be-deleted options
        ArrayList selectedOptionsList = new ArrayList();

        // get a sorted copy (in descending order) to work with, so we can delete the "last" elements first
        TreeMap sortedOptions  = new TreeMap(new SelectedOptionsComparator());
        sortedOptions.putAll(selectedOptions);

        if (!selectedOptions.isEmpty() && !list.isEmpty()) {
            Iterator selectedOptionIndexes = sortedOptions.keySet().iterator();
            while (selectedOptionIndexes.hasNext()) {
                Integer index = (Integer) selectedOptionIndexes.next();
                selectedOptionsList.add(0, list.get(index.intValue() ));
                list.remove(index.intValue());
            }
            // add them back at the begining.
            list.addAll(0, selectedOptionsList);
        }
    }

    /**
     * Get the value for the given fieldName.
     * First check the ActionForm, if it is not null.
     * If no value is found, then check the request attributes.
     * If no value is found, then check the request parameters.
     */
    protected Object getFieldValue(HttpServletRequest request, ActionForm form, OasisFields fields, String fieldName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldValue", new Object[]{request, form, fieldName});
        }

        Object value = null;

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "getFieldValue", "Looking for value of '" + fieldName + "'");
        }

        // First look into the Form if one is provided
        if (form != null) {
            try {
                String[] paramValues = BeanUtils.getArrayProperty(form, fieldName);
                if (paramValues != null) {
                    if (paramValues.length == 1) {
                        value = paramValues[0];
                    } else {
                        value = StringUtils.arrayToDelimited((String[]) paramValues, ",", false, false, true);
                    }
                }
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "getFieldValue", "Found in Form: '" + fieldName + "'='" + value + "'");
                }
            } catch (Exception e) {
                // Form doesn't have the value; continue checking elswhere...
            }
        }

        // If there is no form, or the value is not set in the form, look to the request attributes.
        if (value == null) {
            value = request.getAttribute(fieldName);
            if (value instanceof DynaBean) {
                value = ((DynaBean) value).get(fieldName);
                value = (value == null ? "" : value);
            }
            if (value != null && l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getFieldValue", "Found in request attributes: '" + fieldName + "'='" + value + "'");
            }
        }

        // If there is no form, or the value is not set in the form, look to the request attributes.
        if (value == null) {
            String[] paramValues = request.getParameterValues(fieldName);
            if (paramValues != null) {
                if (paramValues.length == 1) {
                    value = paramValues[0];
                }
                else {
                    value = StringUtils.arrayToDelimited((String[]) paramValues, ",", false, false, true);
                }
            }
            if (value != null && l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "getFieldValue", "Found in request parameters: '" + fieldName + "'='" + value + "'");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldValue", value);
        }
        return value;
    }

    /**
     * Set the value for the given field.
     * First try the ActionForm, if it is not null.
     * Otherwise, set the value as a request attribute.
     */
    protected void setFieldValue(HttpServletRequest request, ActionForm form, OasisFormField fld, Object value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldValue", new Object[]{request, form, fld, value});
        }

        try {
            if (form != null) {
                // Set the value in the form
                try {
                    ((DynaActionForm) form).set(fld.getFieldId(), value);
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "setFieldValue", "Set in Form: '" + fld.getFieldId() + "'='" + value + "'");
                    }
                } catch (Exception e) {
                    // The property does not exist in the form; set the value as a request attribute instead
                    request.setAttribute(fld.getFieldId(), BeanDtiUtils.createValueBean(fld, value));
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "setFieldValue", "Set in request attributes: '" + fld.getFieldId() + "'='" + value + "'");
                    }
                }
            } else {
                // Set the value as a request attribute
                request.setAttribute(fld.getFieldId(), BeanDtiUtils.createValueBean(fld, value));
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "setFieldValue", "Set in request attributes: '" + fld.getFieldId() + "'='" + value + "'");
                }
            }
        } catch (JspException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to set the value in the request", e);
            l.throwing(getClass().getName(), "setFieldValue", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "setFieldValue");
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------

    public CodeLookupManagerImpl() {
    }

    public void verifyConfig() {
        if (getCodeLookupDAO() == null)
            throw new ConfigurationException("The required property 'codeLookupDAO' is missing.");
    }

    public CodeLookupDAO getCodeLookupDAO() {
        if (m_codeLookupDAO == null) {
            // Allow this class to not be configured through Spring
            m_codeLookupDAO = new CodeLookupJdbcDAO();
        }
        return m_codeLookupDAO;
    }

    public void setCodeLookupDAO(CodeLookupDAO codeLookupDAO) {
        m_codeLookupDAO = codeLookupDAO;
    }

    public Cache getFieldCache() {
        if (m_fieldCache == null) {
            // Allow this property to not be configured through Spring
            m_fieldCache = CacheManager.getInstance().getCache("dti.oasis.codelookupmgr.impl.CodeLookupManagerImpl.fieldCache");
        }
        return m_fieldCache;
    }

    public void setFieldCache(Cache fieldCache) {
        m_fieldCache = fieldCache;
    }

    public Cache getLovCache() {
        if (m_lovCache == null) {
            // Allow this property to not be configured through Spring
            m_lovCache = CacheManager.getInstance().getCache("dti.oasis.codelookupmgr.impl.CodeLookupManagerImpl.lovCache");
        }
        return m_lovCache;
    }

    public void setLovCache(Cache lovCache) {
        m_lovCache = lovCache;
    }

    /**
     * Setup the CodeLookupManager to automatically reload LOVs that have field id placeholders using AJAX.
     * The default is false.
     */
    public void setAjaxReloadDefault(boolean ajaxReloadDefault) {
        m_ajaxReloadDefault = Boolean.valueOf(ajaxReloadDefault);
    }

    protected boolean getAjaxReloadDefault() {
        if (m_ajaxReloadDefault == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_AJAX_RELOAD_DEFAULT)) {
                m_ajaxReloadDefault = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_AJAX_RELOAD_DEFAULT)).booleanValue());
            }
            else {
                m_ajaxReloadDefault = Boolean.FALSE;
            }
        }
        return m_ajaxReloadDefault.booleanValue();
    }

   /**
     * Setup the CodeLookupManager for displaySelectedOptionsFirst property indicating
    *   should mutil-selected fields display the selected options first
     * The default is false
     */
    public void setDisplaySeletedOptionsFirst(boolean displaySeletedOptionsFirst) {
        m_displaySelectedOptionsFirst = Boolean.valueOf(displaySeletedOptionsFirst);
    }

    protected boolean getDisplaySeletedOptionsFirst() {
        if (m_displaySelectedOptionsFirst == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_DISPLAY_SELECTED_OPTIONS_FIRST_DEFAULT)) {
                m_displaySelectedOptionsFirst = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_DISPLAY_SELECTED_OPTIONS_FIRST_DEFAULT)).booleanValue());
            }
            else {
                m_displaySelectedOptionsFirst = Boolean.FALSE;
            }
        }
        return m_displaySelectedOptionsFirst.booleanValue();
    }
    
    /**
     * Setup the CodeLookupManager to add -SELECT- as the first item in all LOVs.
     * The default is false.
     */
    public void setAddSelectOptionDefault(boolean addSelectOption) {
        m_addSelectOptionDefault = Boolean.valueOf(addSelectOption);
    }

    protected boolean getAddSelectOptionDefault() {
        if (m_addSelectOptionDefault == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_ADD_SELECT_OPTION_DEFAULT)) {
                m_addSelectOptionDefault = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_ADD_SELECT_OPTION_DEFAULT)).booleanValue());
            }
            else {
                m_addSelectOptionDefault = Boolean.FALSE;
            }
        }
        return m_addSelectOptionDefault.booleanValue();
    }

    /**
     * Setup the CodeLookupManager with the SELECT option's code value .
     */
    public void setSelectOptionCode(String selectOptionCode) {
        selectOptionCode = selectOptionCode == null ||
                           selectOptionCode.equals("\"\"") ||
                           selectOptionCode.equals("''")? "" : selectOptionCode;
        m_selectOptionCode = selectOptionCode;
    }

    /**
     * Get the code used when adding a SELECT option  As First in all LOVs.
     */
    public String getSelectOptionCode() {
        if (m_selectOptionCode == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_SELECT_OPTION_CODE)) {
                m_selectOptionCode = ApplicationContext.getInstance().getProperty(PROPERTY_SELECT_OPTION_CODE);
            }
            else {
                m_selectOptionCode = DEFAULT_SELECT_OPTION_CODE;
            }
        }
        return m_selectOptionCode;
    }

    /**
     * Setup the CodeLookupManager with the SELECT option's code value .
     */
    public void setSelectOptionLabel(String selectOptionLabel) {
        selectOptionLabel = selectOptionLabel == null ||
                           selectOptionLabel.equals("\"\"") ||
                           selectOptionLabel.equals("''")? "" : selectOptionLabel;
        m_selectOptionLabel = selectOptionLabel;
    }

    /**
     * Get the label used when adding a SELECT option  As First in all LOVs.
     */
    public String getSelectOptionLabel() {
        if (m_selectOptionLabel == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_SELECT_OPTION_LABEL)) {
                m_selectOptionLabel = ApplicationContext.getInstance().getProperty(PROPERTY_SELECT_OPTION_LABEL);
            }
            else {
                m_selectOptionLabel = DEFAULT_SELECT_OPTION_LABEL;
            }
        }
        return m_selectOptionLabel;
    }

    /**
     * Setup the CodeLookupManager with the SELECT option's code value .
     */
    public void setAllOptionCode(String allOptionCode) {
        allOptionCode = allOptionCode == null ||
            allOptionCode.equals("\"\"") ||
            allOptionCode.equals("''")? "" : allOptionCode;
        m_allOptionCode = allOptionCode;
    }

    /**
     * Get the code used when adding a ALL option  As First in all LOVs.
     */
    public String getAllOptionCode() {
        if (m_allOptionCode == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_ALL_OPTION_CODE)) {
                m_allOptionCode = ApplicationContext.getInstance().getProperty(PROPERTY_ALL_OPTION_CODE);
            }
            else {
                m_allOptionCode = DEFAULT_ALL_OPTION_CODE;
            }
        }
        return m_allOptionCode;
    }

    /**
     * Setup the CodeLookupManager with the SELECT option's code value .
     */
    public void setAllOptionLabel(String allOptionLabel) {
        allOptionLabel = allOptionLabel == null ||
            allOptionLabel.equals("\"\"") ||
            allOptionLabel.equals("''")? "" : allOptionLabel;
        m_allOptionLabel = allOptionLabel;
    }

    /**
     * Get the label used when adding a ALL option  As First in all LOVs.
     */
    public String getAllOptionLabel() {
        if (m_allOptionLabel == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_ALL_OPTION_LABEL)) {
                m_allOptionLabel = ApplicationContext.getInstance().getProperty(PROPERTY_ALL_OPTION_LABEL);
            }
            else {
                m_allOptionLabel = DEFAULT_ALL_OPTION_LABEL;
            }
        }
        return m_allOptionLabel;
    }

    /**
     * Setup the CodeLookupManager to cache LOOKUP lovs by default.
     * The default is to NOT cache the LOOKUP lovs.
     */
    public void setCacheLOOKUPLovsByDefault(boolean cacheLOOKUPLovsByDefault) {
        m_cacheLOOKUPLovsByDefault = Boolean.valueOf(cacheLOOKUPLovsByDefault);
    }


    /**
     * Clear any cached List Of Values.
     */
    public void clearListOfValuesCache() {
        getLovCache().clear();
    }

    public void clearFieldCache() {
        getFieldCache().clear();
    }

    protected boolean cacheLOOKUPLovsByDefault() {
        if (m_cacheLOOKUPLovsByDefault == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_CACHE_LOOKUP_LOVS_BY_DEFAULT)) {
                m_cacheLOOKUPLovsByDefault = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_CACHE_LOOKUP_LOVS_BY_DEFAULT)).booleanValue());
            }
            else {
                m_cacheLOOKUPLovsByDefault = Boolean.FALSE;
            }
        }
        return m_cacheLOOKUPLovsByDefault.booleanValue();
    }

    /**
     * Implements the refresh parameters listener.
     * @param request
     */
    public void refreshParms(HttpServletRequest request) {
        l.entering(getClass().getName(), "refreshParms");
        MessageManager messageManager = MessageManager.getInstance();
        try {
            clearListOfValuesCache();
            messageManager.addInfoMessage("core.refresh.codelookups.success");
            l.logp(Level.INFO, getClass().getName(), "refreshParms", "Code Lookups have been refreshed!");
        } catch (Exception e) {
            l.logp(Level.SEVERE, getClass().getName(), "refreshParms", "Failed to refresh the CodeLookupManager", e);
            messageManager.addErrorMessage("core.refresh.codelookups.fail");
        }
        l.exiting(getClass().getName(), "refreshParms");
    }

    private Boolean m_ajaxReloadDefault;
    private Boolean m_addSelectOptionDefault;
    private Boolean m_displaySelectedOptionsFirst; // for multi-selected, or multi-select popup
    private String m_selectOptionCode;
    private String m_selectOptionLabel;
    private String m_allOptionCode;
    private String m_allOptionLabel;
    private Boolean m_cacheLOOKUPLovsByDefault;
    private CodeLookupDAO m_codeLookupDAO;
    private Cache m_fieldCache;
    private Cache m_lovCache;
    private final Logger l = LogUtils.getLogger(getClass());
    private static final String DEFAULT_FIELD_DELIMITER = "^";
    private static final String DEFAULT_LIST_DELIMITER = ",";

    // this private inner class defines the descending order for storing selected options
    // so the elements with higher key values can be deleted first.
    private class SelectedOptionsComparator implements Comparator, Serializable {
        Logger l = LogUtils.getLogger(getClass());
        public int compare(Object option1, Object option2) {
            if ((option1 instanceof Integer) && (option2 instanceof Integer)) {
                return (((Integer) option2).intValue() - ((Integer) option1).intValue());
            } else {
                l.warning(getClass().getName()+"SelectedOptionsComparator only accept type of Integer  ");
                throw new IllegalArgumentException(getClass().getName()+"Object of type SelectedOptions can only accept type of Integer");
            }
        }
    }
}
