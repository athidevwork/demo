package dti.oasis.codelookupmgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.cachemgr.Cache;
import dti.oasis.codelookupmgr.impl.CodeLookupManagerImpl;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.recordset.Record;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.ArrayList;

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
 *
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
 * 02/01/2007       wer         Moved the getSelectOptionLabel and getSelectOptionCode methods into the CodeLookupManager class.
 * 04/07/2008       guang       Added displaySelectedOptionsFirst-related logic
 * 04/10/2008       wer         Enhanced fields and LOVs to be cached per userId
 * 04/21/2011       James       Issue#119774 Remove logic from CodeLookupManager to use defaults
 * 02/14/2014       fcb         Added ALL select option.
 * ---------------------------------------------------
 */
public abstract class CodeLookupManager {
    /**
     * The bean name of a CodeLookupManager extension if it is configured in the ApplicationContext.
     */
    public static final String BEAN_NAME = "CodeLookupManager";

    /**
     * The name of the property to set to automatically reload LOVs that have field id placeholders using AJAX by default.
     * This property defaults to false.
     */
    public static final String PROPERTY_AJAX_RELOAD_DEFAULT = "codelookupmgr.ajax.reload.default";

    /**
     * The name of the property to set to sort the lov by  the selected options first (for multi-select fields)
     */
    public static final String PROPERTY_DISPLAY_SELECTED_OPTIONS_FIRST_DEFAULT = "codelookupmgr.display.selected.options.first.default";

    /**
     * The name of the property to set to add -SELECT- as the first item in all LOVs by default.
     * This property defaults to false.
     */
    public static final String PROPERTY_ADD_SELECT_OPTION_DEFAULT = "codelookupmgr.add.select.option.default";

    /**
     * The name of the property to set to define the code value for the Add Select As First in all LOVs.
     * This property defaults to "-1"
     */
    public static final String PROPERTY_SELECT_OPTION_CODE = "codelookupmgr.select.option.code";

    /**
     * The default code value for the Add Select As First in all LOVs.
     */
    public static final String DEFAULT_SELECT_OPTION_CODE = "";

    /**
     * The name of the property to set to define the code value for the Add All As First in all LOVs.
     * This property defaults to "-1"
     */
    public static final String PROPERTY_ALL_OPTION_CODE = "codelookupmgr.all.option.code";

    /**
     * The default code value for the Add All As First in all LOVs.
     */
    public static final String DEFAULT_ALL_OPTION_CODE = "";

    /**
     * The name of the property to set to define the label value for the Add Select As First in all LOVs.
     * This property defaults to "-1"
     */
    public static final String PROPERTY_SELECT_OPTION_LABEL = "codelookupmgr.select.option.label";

    /**
     * The default label value for the Add Select As First in all LOVs.
     */
    public static final String DEFAULT_SELECT_OPTION_LABEL = "-SELECT-";

    /**
     * The name of the property to set to define the label value for the All Select As First in all LOVs.
     * This property defaults to "-1"
     */
    public static final String PROPERTY_ALL_OPTION_LABEL = "codelookupmgr.all.option.label";

    /**
     * The default label value for the Add All As First in all LOVs.
     */
    public static final String DEFAULT_ALL_OPTION_LABEL = "-ALL-";

    /**
     * The name of the property to set to determine if LOOKUP lovs should be cached by default.
     * This property defaults to false.
     */
    public static final String PROPERTY_CACHE_LOOKUP_LOVS_BY_DEFAULT = "codelookupmgr.cache.lookup.lovs.by.default";

    /**
     * Return an instance of the RequestStorageManager.
     */
    public synchronized static final CodeLookupManager getInstance() {
        if (c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(BEAN_NAME)) {
                c_instance = (CodeLookupManager) ApplicationContext.getInstance().getBean(BEAN_NAME);
            } else {
                c_instance = new CodeLookupManagerImpl();
            }
        }
        return c_instance;
    }

    /**
     * Get the List of Values for the given record with the fields:
     *  aclass
     *  fieldId
     */
    public abstract ArrayList getListOfValues(Connection conn, HttpServletRequest request, Record inputRecord);

    /**
     * Creates ArrayList objects for the passed OasisFormField with
     * a corresponding "List of Values". The ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @param conn        JDBC Connection
     * @param form        DynaActionForm
     * @param request     HttpServletRequest
     * @param fields      the OasisFields in context for this request
     * @param fld         form field
     */
    public abstract void processListOfValues(Connection conn, ActionForm form, HttpServletRequest request, OasisFields fields, OasisFormField fld);

    /**
     * Setup the CodeLookupManager to automatically reload LOVs that have field id placeholders using AJAX.
     * The default is false.
     */
    public abstract void setAjaxReloadDefault(boolean ajaxReloadDefault);

   /**
     * Setup the CodeLookupManager to display Selected Options First for mutil-selected fields
     * The default is false
     */
    public abstract void setDisplaySeletedOptionsFirst(boolean displaySeletedOptionsFirst);

    /**
     * Setup the CodeLookupManager to add -SELECT- as the first item in all LOVs.
     * The default is false.
     */
    public abstract void setAddSelectOptionDefault(boolean addSelectOption);

    /**
     * Get the code used when adding a SELECT option  As First in all LOVs.
     */
    public abstract String getSelectOptionCode();

    /**
     * Get the label used when adding a SELECT option  As First in all LOVs.
     */
    public abstract String getSelectOptionLabel();

    /**
     * Get the label used when adding a ALL option  As First in all LOVs.
     */
    public abstract String getAllOptionLabel();

    /**
     * Clear any cached List Of Values.
     */
    public abstract void clearListOfValuesCache();

    public abstract void clearFieldCache();


    private static CodeLookupManager c_instance;
    
    public abstract ArrayList getLookupLov(String lookupTypeCode, boolean displayLongDesc);

    public abstract Cache getLovCache();

    public abstract Cache getFieldCache();
}
