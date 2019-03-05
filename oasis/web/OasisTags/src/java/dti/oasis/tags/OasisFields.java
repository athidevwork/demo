package dti.oasis.tags;

import dti.oasis.app.AppException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.http.RequestIds;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.PageViewStateAdmin;
import dti.oasis.session.UserSessionManager;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.*;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A collection of OasisFormField objects in the
 * form of a LinkedHashMap. Provides accessor
 * functions to the fields on a page
 * versus fields on a layer.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 * @see OasisFormField
 *      <p/>
 *      <p/>
 *      Date:   Jul 3, 2003
 */
/* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 11/24/03         jbe         Override remove method
*                              Override equals & hashCode methods in inner class LayerFieldLoad
* 12/2/03          jbe			Add month/year date logic for default values
* 12/15/03         jbe         Add row_num to sql for web page field & web layer field
*                              pass it as new parm to OasisFormField constructor
*                              Add fieldDeps HashMap linking pagefields w/ values to layers
*                              Sort by row_num then sort_order
* 1/29/04          jbe         Add layer_field_id and page_field_id to queries
* 2/7/04           jbe         Add logging
* 3/11/04          jbe         Add new getListOfValues method that work by layerId
*                              Handle delimiters when LOV's are LIST types.
* 8/3/2004          jbe        Add SQL Expression in default values.
* 8/24/2004         jbe        Retrieve layers by layer sort order
* 9/27/2004         jbe        Handle null values in LOV
* 11/11/2004        jbe        Add new createInstance method to process by layer
*                              Use PreparedStatements
* 11/29/2004        jbe        Add TYPE_PHONE
* 12/10/2004        jbe        Add getLayerFieldsMap convenience method.
* 2/3/2005          jbe        Add getFieldIds
* 4/5/2005          jbe        Add protectedFields, processing & getter
* 09/02/2006        Larry      Add OasisFields.TYPE_CURRENCY_FORMATTED for auto money format
* 10/31/2006        GCC        Renamed TYPE_CURRENCY_NEW to
*                              TYPE_CURRENCY_FORMATTED.  Changed value of constant
*                              to "CF".
* 09/27/2006        MLM        1. Changed createInstance, processPage, processLayers methods to add new
*                                 PageDefLoadProcessor parameter.
* 9/28/2006         wer        Add PROTECTED_FIELDS constant
* 01/23/2007        wer        Added getField() to retrieve an OasisFormField from the OasisFields, or the first
*                              one found in the Layers;
*                              Added actionClassName;
*                              Changed usage of new Boolean(x) in logging to String.valueOf(x);
*                              Moved processListOfValues() implementation and related methods out to CodeLookupManager;
* 01/31/2007        wer        Enhanced to make storage/retrieval of fields case-insensitive.
* 05/04/2007        GCC        Added code to get new columns for colspan, empty
*                              cells before and after fields, style, alignment.
* 09/28/2007        sxm        Added code to get new column tooltip
* 01/02/2007        James      Web Field dependency.
* 03/03/2008        James      Issue#79614 eClaims architectural enhancement to
*                              take advantage of ePolicy architecture
*                              Handling HREF is a new enhancement in WebWB
* 03/24/2008        Fred       Added TYPE_PERCENTAGE.
* 07/10/2008        Fred       Added TYPE_TIME=TM
* 09/25/2008        Larry      Issue 86826 DB connection leakage change
* 10/09/2009        fcb        Issue# 96764: added logic for masked fields.
* 01/29/2010        James      Issue#100713 Add attributes to Web Layer for setting grid sort
*                              order and grid dimensions, and migrate existing data to use this.
* 03/29/2010       James       Issue#105489 Added hasLayer
* 04/01/2010        kshen      Changed to support email text field.
* 04/09/2010        James      Issue#105817 Migration of pf_web_page_field to pf_web_layer_field
* 08/25/2010       dzhang      Issue#105820 Change the pageLoadQuery to load the Lov page feilds
*                              sort by lov_sort_order instead of sort_order
* 09/06/2010       James       Issue#110256 Added method hasField
* 09/20/2011       mxg         Issue #100716: Display Type FORMATTEDNUMBER: Added Format Pattern
* 01/11/2013       jxgu        Issue#14152 move all security logic into function cs_get_auth_web_field_plus_pg
* 09/17/2015       Parker      Issue#165637 - Use ThreadLocal to make SimpleDateFormat thread safe.
* 03/30/2017       hxk         Issue 184490 - Change a few queries to use the pf_wweb_page_layer_fields
*                              view, which now uses a materialized view.
* 04/06/2017       cesar       Issue 184619 - Added "order by" to pageQuery and layerQuery.
* 05/17/2017       cesar       Issue 185553 - Rolled back #184490 and #184619.
*                                             Added isMaterializeViewFlagEnabled() to invoke
*                                             either the materialized view or the regular view
* 07/20/2017       mlm         186748 - Reverted fix for 181684, but refactored to publish "code" or "description" in the
*                              grid based on grid field's display type configuration.
* 02/09/2018       ylu         191383: reload lov field if it has dependency field value
* 04/04/2018       cesar       #191962 - added col_width and min_col_width
* 05/14/2018       cesar       #192983 - added storeOasisFieldsXssOverrides() and copyMaskedFields().
* 08/06/2018       dpang       #194641 - added col_aggregate
* -----------------------------------------------------------------------------
*/
public class OasisFields extends LinkedHashMap implements Serializable {
    private HashMap fieldDeps = null;
    protected int layerCount = 0;
    private static IOasisFieldsFactory factory;
    protected ArrayList loadList = new ArrayList();

    protected static final char protectedDelim = '^';
    protected StringBuffer protectedFields = new StringBuffer("^");
    private String actionClassName;
    private Map caseSensitiveMap = new LinkedHashMap();

    /**
     * Prefix for default values that are functions
     */
    protected static String defaultValueFuncPrefix = "[!";
    protected static final String clsName = OasisFields.class.getName();
    private static final String FUNC_TODAY = "TODAY";
    private static final String FUNC_USER = "USER";
    private static final String FUNC_SQL_EXPR = "SQL:";
    private static final String PAGE_FIELDS_LAYER = "PAGE_FIELDS_LAYER";

    /**
     * Key name for the Protected Fields
     */
    public static final String PROTECTED_FIELDS = "protectedFields";

    /**
     * Inner Class to hold a field on a Layer
     */
    class LayerFieldLoad implements Serializable, Cloneable {
        String layerId;
        String fieldId;

        /**
         * Constructor with layer & field
         *
         * @param layerId pf_web_layer.layer_id
         * @param fieldId field_id (or layer_field_id)
         */
        LayerFieldLoad(String layerId, String fieldId) {
            this.layerId = layerId;
            this.fieldId = fieldId;
        }

        /**
         * Always return 0
         *
         * @return 0
         */
        public int hashCode() {
            return 0;
        }

        /**
         * Determins if the passed object equals this object
         *
         * @param o
         * @return boolean
         */
        public boolean equals(Object o) {
            if (o instanceof LayerFieldLoad) {
                LayerFieldLoad lfl = (LayerFieldLoad) o;
                return (lfl.layerId.equals(layerId) && lfl.fieldId.equals(fieldId));
            }
            return false;
        }

        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("dti.oasis.tags.OasisFields.LayerFieldLoad");
            buf.append("{layerId=").append(layerId);
            buf.append(",fieldId=").append(fieldId);
            buf.append('}');
            return buf.toString();
        }

        public LayerFieldLoad clone() {
            LayerFieldLoad result = null;
            try {
                result = (LayerFieldLoad)super.clone();
            } catch (CloneNotSupportedException e) {
                // assert false;
            }

            return result;
        }
    }

    /**
     * Date datatype
     */
    public static final String TYPE_DATE = "DT";
    /**
     * Time datatype
     */
    public static final String TYPE_TIME = "TM";
    /**
     * Number datatype
     */
    public static final String TYPE_NUMBER = "NM";
    /**
     * Text datatype
     */
    public static final String TYPE_TEXT = "ST";
    /**
     * Uppercase Text datatype
     */
    public static final String TYPE_UPPERCASE_TEXT = "UT";
    /**
     * Lowercase Text datatype
     */
    public static final String TYPE_LOWERCASE_TEXT = "LT";
    /**
     * Currency Datatype
     */
    public static final String TYPE_CURRENCY = "CU";
    /**
     * New Currency Datatype for automatically Money Format
     */
    public static final String TYPE_CURRENCY_FORMATTED = "CF";
    /**
     * Percentage data type
     */
    public static final String TYPE_PERCENTAGE = "PT";

    /**
     * Phone # Datatype
     */
    public static final String TYPE_PHONE = "PH";

    /**
     * Finder Display Type
     */
    public static final String DISPLAY_TYPE_FINDER_TEXT = "FINDERTEXT";

    /**
     * Finder Display Type
     */
    public static final String DISPLAY_TYPE_EMAIL_TEXT = "EMAILTEXT";

    /**
     * Note Display Type
     */
    public static final String DISPLAY_TYPE_NOTE_TEXT = "NOTETEXT";

    /**
     * CheckBox Type
     */
    public static final String DISPLAY_TYPE_CHECKBOX = "CHECKBOX";

    /**
     * Note Display Type
     */
    public static final String DISPLAY_TYPE_TEXTAREA_POPUP = "TEXTAREAPOPUP";

    /**
     * Note Display Type
     */
    public static final String DISPLAY_TYPE_TEXTAREA = "TEXTAREA";

    /**
     * Formatted Number Display Type
     */
    public static final String DISPLAY_TYPE_FORMATTEDNUMBER = "FORMATTEDNUMBER";

    /**
     * Select Display Type
     */
    public static final String DISPLAY_TYPE_SELECT = "SELECT";

    /**
     * Multi-Select Display Type
     */
    public static final String DISPLAY_TYPE_MULTISELECT = "MULTISELECT";

    /**
     * Multi-Select Popup Display Type
     */
    public static final String DISPLAY_TYPE_MULTISELECTPOPUP = "MULTISELECTPOPUP";
    /**
     * The name of the class to define in the Web WorkBench for a FINDER Display Type if you don't want the user to be able to edit the field directly.
     */
    public static final String CLASS_NO_ENTRY_FINDER = "noEntryFinder";

    /**
     * Query to retrieve the fields on a page in dropdown load order
     */
    private final static String pageLoadQuery = "SELECT nvl(wplf.layer_field_id, wf.field_id) field_id " +
        "  FROM pf_web_page        wp, " +
        "       pf_web_layer       wpl, " +
        "       pf_web_layer_field wplf, " +
        "       pf_web_field       wf " +
        " WHERE wp.struts_action = ? " +
        "   AND wp.pf_web_page_pk = wpl.pf_web_page_fk " +
        "   AND wpl.pf_web_layer_pk = wplf.pf_web_layer_fk " +
        "   AND wplf.pf_web_field_fk = wf.pf_web_field_pk " +
        "   AND wpl.layer_id = '" + PAGE_FIELDS_LAYER + "' " +
        " ORDER BY nvl(wplf.lov_sort_order, 999)";

    private final static String layerOnlyQuery = "SELECT layer_id, description, DECODE(wl.hidden_b, 'Y', 'Y', nvl(wl.hidden_b,'N')) hidden_b, " +
            " wl.grid_sort_order, wl.grid_height, wl.grid_container_width, wl.grid_container_height, wl.grid_page_size " +
            " FROM pf_web_layer wl, pf_web_page wp " +
            "WHERE wp.struts_action = ? AND wp.pf_web_page_pk = wl.pf_web_page_fk " +
            " AND wl.layer_id != '" + PAGE_FIELDS_LAYER + "' " +
            " ORDER BY sort_order";

    /**
     * Common sql for select fields
     */
    private final static String SELECT_FIELDS_COMMON = "SELECT wplf.layer_id, " +
            "       wplf.field_id, " +
            "       wplf.label, " +
            "       wplf.visible_b, " +
            "       wplf.required_b, " +
            "       wplf.default_value, " +
            "       wplf.css_class, " +
            "       wplf.read_only_b, " +
            "       wplf.display_type, " +
            "       wplf.nbr_rows, " +
            "       wplf.nbr_cols, " +
            "       wplf.maxlength, " +
            "       wplf.lov_sql, " +
            "       wplf.datatype, " +
            "       wplf.security_b, " +
            "       wplf.access_authority, " +
            "       wplf.description, " +
            "       wplf.row_num, " +
            "       wplf.sort_order, " +
            "       NULL tblcol, " + //column dropped FROM 2011.1.0
            "       wplf.style, " +
            "       wplf.colspan, " +
            "       wplf.empty_cells_after_fld, " +
            "       wplf.empty_cells_before_fld, " +
            "       wplf.alignment, " +
            "       wplf.taborder, " +
            "       wplf.tooltip, " +
            "       wplf.field_dependency, " +
            "       wplf.href, " +
            "       wplf.hidden_b, " +
            "       wplf.grid_sort_order, " +
            "       wplf.grid_height, " +
            "       wplf.grid_container_width, " +
            "       wplf.grid_container_height, " +
            "       wplf.grid_page_size, " +
            "       wplf.format_pattern, " +
            "       wplf.col_width, " +
            "       wplf.col_min_width, " +
            "       wplf.col_aggregate " +
       "  FROM pf_web_page_layer_fields wplf " +
            " WHERE wplf.struts_action = ? ";

    /**
     * Query to retrieve fields directly on page
     */

    private final static String pageQuery = SELECT_FIELDS_COMMON +
            "   AND wplf.layer_id = '" + PAGE_FIELDS_LAYER + "' ";

    /**
     * Query to retrieve fields on all layers on page
     */
    private final static String layerQuery = SELECT_FIELDS_COMMON +
            "   AND wplf.layer_id != '" + PAGE_FIELDS_LAYER + "' ";

    /**
     * Query to retrieve the fields by layer in dropdown load order
     */
    private final static String layerLoadQuery = "SELECT wpl.layer_id, nvl(wplf.layer_field_id,wf.field_id) field_id " +
        "FROM pf_web_page wp, pf_web_layer wpl, pf_web_layer_field wplf, " +
        "pf_web_field wf " +
        "WHERE wp.struts_action = ? AND wp.pf_web_page_pk = wpl.pf_web_page_fk " +
        "AND wpl.pf_web_layer_pk = wplf.pf_web_layer_fk " +
        "AND wplf.pf_web_field_fk = wf.pf_web_field_pk " +
        "AND wpl.layer_id != '" + PAGE_FIELDS_LAYER + "' " +
        "ORDER BY layer_id, NVL(wplf.lov_sort_order,999)";


    /**
     * Prevent direct instantiation
     */
    protected OasisFields() {

    }

    /**
     * Prevent direct instantiation
     */
    protected OasisFields(int capacity) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisFields(int capacity, float loadFactor) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisFields(int capacity, float loadFactor, boolean accessOrder) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisFields(Map m) {
    }

    /**
     * Returns an ArrayList of WebLayer objects
     *
     * @return ArrayList of WebLayer objects
     */
    public ArrayList getLayers() {
        Logger l = LogUtils.enterLog(getClass(), "getLayers");
        ArrayList list = new ArrayList(layerCount);
        Iterator it = this.values().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof WebLayer)
                list.add(o);
        }
        l.exiting(getClass().getName(), "getLayers", list);
        return list;
    }

    /**
     * layer exists or not
     * @param layerId
     * @return  true if layer exists
     */
    public boolean hasLayer(String layerId) {
        Logger l = LogUtils.enterLog(getClass(), "hasLayer", layerId);
        boolean hasLayer = false;
        if (getLayerFieldsMap(layerId) != null) {
            hasLayer = true;
        }
        l.exiting(getClass().getName(), "hasLayer", new Boolean(hasLayer));
        return hasLayer;
    }

    /**
     * Returns a LinkedHashMap of OasisFormFields given a layer id.
     * It is similar to calling get(layerId).
     *
     * @param layerId
     * @return LinkedHashMap of OasisFormField objects for layer.  Null if layer not found.
     */
    public WebLayer getLayerFieldsMap(String layerId) {
        Logger l = LogUtils.enterLog(getClass(), "getLayerFieldsMap", layerId);
        Object o = get(layerId);
        WebLayer map = null;
        if (o instanceof WebLayer)
            map = (WebLayer) o;

        l.exiting(getClass().getName(), "getLayerFieldsMap", map);
        return map;
    }

    /**
     * Returns an ArrayList of OasisFormFields given a layer id
     *
     * @param layerId
     * @return ArrayList of OasisFormField objects
     */
    public ArrayList getLayerFields(String layerId) {
        Logger l = LogUtils.enterLog(getClass(), "getLayerFields", layerId);
        ArrayList list = null;
        if (layerCount > 0) {

            Object o = get(layerId);
            if (o instanceof WebLayer) {
                WebLayer webLayer = (WebLayer) o;
                list = new ArrayList(webLayer.size());
                Iterator it = webLayer.values().iterator();
                while (it.hasNext())
                    list.add(it.next());
            }
        }
        l.exiting(getClass().getName(), "getLayerFields", list);
        return list;
    }

    /**
     * check whether a field exists or not
     * @param fieldId
     * @return
     */
    public boolean hasField(String fieldId) {
        Logger l = LogUtils.enterLog(getClass(), "getLayerFields", new Object[]{fieldId});
        boolean hasField = false;
        Object obj = get(fieldId);
        if (obj != null && obj instanceof OasisFormField) {
            hasField = true;
        } else {
            ArrayList layers = getLayers();
            for (int i = 0; i < layers.size(); i++) {
                WebLayer webLayer = (WebLayer) layers.get(i);
                if (webLayer.containsKey(fieldId)) {
                    hasField = true;
                    break;
                }
            }
        }
        l.exiting(getClass().getName(), "getLayerFields", new Boolean(hasField));
        return hasField;
    }

    public OasisFormField getField(String fieldId) {
        OasisFormField field = null;

        Object obj = get(fieldId);
        if (obj != null && obj instanceof OasisFormField) {
            field = (OasisFormField) obj;
        }
        else {
            ArrayList layers = getLayers();
            if (layers != null) {
                Iterator iter = layers.iterator();
                while (field == null && iter.hasNext()) {
                    WebLayer webLayer = (WebLayer) iter.next();
                    webLayer.get(fieldId);
                }
            }
        }

        if (field == null) {
            throw new AppException("Failed to find the OasisFormField with fieldId'" + fieldId + "' on action class'" + getActionClassName() + "'");
        }

        return field;
    }

    /**
     * Return an OasisFormField given a layerId and fieldId
     *
     * @param layerId layerId whose key can be found in Map
     * @param fieldId fieldId whose key can be found in the related layer's Map
     * @return OasisFormField object
     */
    public Object get(String layerId, String fieldId) {
        Logger l = LogUtils.enterLog(getClass(), "get",
            new Object[]{layerId, fieldId});
        Object o = ((LinkedHashMap) get(layerId)).get(fieldId);
        l.exiting(getClass().getName(), "get", o);
        return o;
    }

    /**
     * Override the ancestor get method. Calls the ancestor get method
     * and if no object is found, it searches through any Maps embedded
     * in this Map.
     *
     * @param key
     * @return entry
     */
    public Object get(Object key) {
        Logger l = LogUtils.enterLog(getClass(), "get", key);

        key = ((String) key).toUpperCase(); // Make this a case-insensitive Map

        // call ancestor
        Object o = super.get(key);
        // if entry not found, look for Maps inside
        // this object, and look in those Maps for the entry
        if (o == null) {
            Iterator it = values().iterator();
            while (it.hasNext() && o == null) {
                Object o1 = it.next();
                if (o1 instanceof LinkedHashMap)
                    o = ((Map) o1).get(key);
            }
        }
        l.exiting(getClass().getName(), "get", o);
        return o;
    }

    /**
     * Override the ancestor method to make this a case-insensitive Map.
     */
    public Object put(Object key, Object value) {
        caseSensitiveMap.put(key, value);   // Store the case-sensitive key/value
        key = ((String) key).toUpperCase();  // Make this a case-insensitive Map
        return super.put(key, value);
    }

    /**
     * Removes a single instance of the specified element from this collection, if it is present (optional operation).
     * This is overridden to keep things consistent within the OasisFields object
     *
     * @param key
     * @return
     * @see java.util.AbstractCollection#remove
     */
    public Object remove(Object key) {
        Logger l = LogUtils.enterLog(getClass(), "remove", key);

        loadList.remove(key);
        caseSensitiveMap.remove(key);   // Remove the case-sensitive key/value

        key = ((String) key).toUpperCase(); // Make this a case-insensitive Map
        Object o = super.remove(key);

        l.exiting(getClass().getName(), "remove", o);
        return o;
    }


    /**
     * Returns an ArrayList of all OasisFormField's directly on the page
     *
     * @return ArrayList of OasisFormField objects
     */
    public ArrayList getPageFields() {
        Logger l = LogUtils.enterLog(getClass(), "getPageFields");
        ArrayList list = new ArrayList(size() - layerCount);
        Iterator it = this.values().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof OasisFormField)
                list.add(o);
        }
        l.exiting(getClass().getName(), "getPageFields", list);
        return list;
    }

    /**
     * get all fields on page, including page fields and layer fields
     * @return
     */
    public ArrayList<OasisFormField> getAllFieldList() {
        Logger l = LogUtils.enterLog(getClass(), "getAllFieldList");
        ArrayList<OasisFormField> list = new ArrayList();
        Iterator it = this.values().iterator();
        /* Loop through keys */
        while (it.hasNext()) {
            Object o = it.next();
            // If map, then this is a layer, iterator through the fields on the layer.
            if (o instanceof WebLayer) {
                WebLayer webLayer = (WebLayer) o;
                Iterator it1 = webLayer.values().iterator();
                while (it1.hasNext()) {
                    OasisFormField field = (OasisFormField) it1.next();
                    list.add(field);
                }
            } else {
                OasisFormField field = (OasisFormField) o;
                list.add(field);
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllFieldList", list);
        }
        return list;
    }

    /**
     * Create a new instance, using the default constructor unless
     * an IOasisFieldsFactory has been defined
     *
     * @return OasisFields object
     */
    public static OasisFields newInstance(String className) {
        OasisFields oasisFields = null;
        if (factory == null)
            oasisFields = new OasisFields();
        else
            oasisFields = factory.newInstance();
        oasisFields.setActionClassName(className);
        return oasisFields;
    }

    protected static void processLayers(OasisFields flds, ResultSet rs, PageDefLoadProcessor pageDefLoadProcessor)
        throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "processLayers",
            new Object[]{flds, rs});
        String layer = null;
        WebLayer map = null;
        String description = null;
        String hiddenB = null;
        try {
            // Cycle through rows. Create map of OasisFormFields for
            // each Layer found. Add map for each layer into OasisFields.
            while (rs.next()) {
                layer = rs.getString(1);
                description = rs.getString(2);
                hiddenB = rs.getString(3);
                map = new WebLayer(layer, description, YesNoFlag.getInstance(hiddenB).booleanValue());
                map.setGridSortOrder(rs.getString(4));
                map.setGridHeight(rs.getString(5));
                map.setGridContainerWidth(rs.getString(6));
                map.setGridContainerHeight(rs.getString(7));
                map.setGridPageSize(rs.getString(8));
                flds.put(layer, map);
                flds.layerCount++;
            }
            l.exiting(clsName, "processLayers");
        }
        finally {
                if (rs != null) DatabaseUtils.close(rs);           
        }
    }

    protected static void processLayers(OasisFields flds, ResultSet rs, String userId, PageDefLoadProcessor pageDefLoadProcessor)
        throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "processLayers",
            new Object[]{flds, rs, userId});
        String lastLayer = "";
        String layer = null;
        WebLayer map = null;
        String description = null;
        String hiddenB = null;
        Connection conn = rs.getStatement().getConnection();
        try {
            // Cycle through rows. Create map of OasisFormFields for
            // each Layer found. Add map for each layer into OasisFields.
            while (rs.next()) {
                layer = rs.getString(1);
                description = rs.getString(17);
                hiddenB = rs.getString(30);

                // check if we've got a new layer
                if (!layer.equals(lastLayer)) {
                    // if we've got a map, add it to OasisFields
                    if (map != null) {
                        flds.put(lastLayer, map);
                        flds.layerCount++;
                    }
                    // create new map
                    map = new WebLayer(layer, description, YesNoFlag.getInstance(hiddenB).booleanValue());
                    map.setGridSortOrder(rs.getString(31));
                    map.setGridHeight(rs.getString(32));
                    map.setGridContainerWidth(rs.getString(33));
                    map.setGridContainerHeight(rs.getString(34));
                    map.setGridPageSize(rs.getString(35));
                    // hang on to current layer until we find
                    // a new one
                    lastLayer = layer;
                }

                String fld = rs.getString(2);
                String auth = rs.getString(16);
                // String secure = rs.getString(15); This is not used. The logic is in function
                boolean isVisible = (rs.getString(4).equals("Y"));
                boolean isReadOnly = (rs.getString(8).equals("Y"));
                if (!isReadOnly)
                    isReadOnly = ("R".equals(auth) || "M".equals(auth));
                boolean isMasked = "M".equals(auth);

                String fieldColWidth = rs.getString("COL_WIDTH");
                String fieldColMinWidth = rs.getString("COL_MIN_WIDTH");
                String fieldColAggregate = rs.getString("COL_AGGREGATE");

                OasisFormField field = new OasisFormField(fld, rs.getString(3),
                    isVisible, (rs.getString(5).equals("Y")),
                    getDefaultValue(rs.getString(6), userId, conn), rs.getString(7), isReadOnly,
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                    rs.getString(13), rs.getString(14), rs.getString(18), rs.getString(19), false,
                    rs.getString(21), rs.getString(22), rs.getString(23),
                    rs.getString(24), rs.getString(25), rs.getString(26),
                    rs.getString(27), rs.getString(28), rs.getString(29), isMasked, rs.getString(36),
                    fieldColWidth, fieldColMinWidth, fieldColAggregate);

                pageDefLoadProcessor.postProcessField(field);

                map.put(fld, field);
            }

            pageDefLoadProcessor.postProcessFields(flds);

            // if we've got a map, add it OasisFields
            if (map != null) {
                flds.put(layer, map);
                flds.layerCount++;
            }
            /*
                End Layered Fields on Page Processing
            */
            l.exiting(clsName, "processLayers");
        }
        finally {
                if (rs != null) DatabaseUtils.close(rs);            
        }
    }

    protected static void processLayerLoad(OasisFields flds, ResultSet rs) throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "processLayerLoad",
            new Object[]{flds, rs});
        try {
            while (rs.next()) {
                flds.addToLoadList(rs.getString(1), rs.getString(2));
            }
            l.exiting(clsName, "processLayerLoad");
        }
        finally {
             if (rs != null) DatabaseUtils.close(rs);
        }

    }

    protected static void processPageLoad(OasisFields flds, ResultSet rs) throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "processPageLoad",
            new Object[]{flds, rs});
        try {
            while (rs.next()) {
                flds.addToLoadList(rs.getString(1));
            }
            l.exiting(clsName, "processPageLoad");
        }
        finally {
             if (rs != null) DatabaseUtils.close(rs);
        }

    }

    protected static void processPage(OasisFields flds, ResultSet rs, String userId, PageDefLoadProcessor pageDefLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "processPage",
            new Object[]{flds, rs, userId});
        Connection conn = rs.getStatement().getConnection();
        try {
            // cycle through rows, adding OasisFormField to OasisFields
            while (rs.next()) {
                String fld = rs.getString(2);
                String auth = rs.getString(16);
                // String secure = rs.getString(15); This is not used. The logic is in function
                boolean isVisible = (rs.getString(4).equals("Y"));
                boolean isReadOnly = (rs.getString(8).equals("Y"));
                if (!isReadOnly)
                    isReadOnly = ("R".equals(auth) || "M".equals(auth));
                boolean isMasked = "M".equals(auth);

                String fieldColWidth = rs.getString("COL_WIDTH");
                String fieldColMinWidth = rs.getString("COL_MIN_WIDTH");
                String fieldColAggregate = rs.getString("COL_AGGREGATE");

                OasisFormField field = new OasisFormField(fld, rs.getString(3),
                    isVisible, (rs.getString(5).equals("Y")),
                    getDefaultValue(rs.getString(6), userId, conn), rs.getString(7), isReadOnly,
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                    rs.getString(13), rs.getString(14), rs.getString(18), rs.getString(19),
                    false,
                    rs.getString(21), rs.getString(22), rs.getString(23),
                    rs.getString(24), rs.getString(25), rs.getString(26),
                    rs.getString(27), rs.getString(28), rs.getString(29), isMasked, rs.getString(36),
                   fieldColWidth, fieldColMinWidth, fieldColAggregate);

                pageDefLoadProcessor.postProcessField(field);

                flds.put(fld, field);
            }

            pageDefLoadProcessor.postProcessFields(flds);
            /*
                End Fields on Page Processing
            */
            l.exiting(clsName, "processPage");
        }
        finally {
             if (rs != null) DatabaseUtils.close(rs);
        }

    }

    /**
     * Add field to list of protected fields
     *
     * @param tabCol  table.column
     * @param fieldId fieldid
     */
    protected void protectField(String tabCol, String fieldId) {
        protectedFields.append(StringUtils.isBlank(tabCol) ? fieldId : tabCol).append(protectedDelim);
    }

    /**
     * Creates an instance of OasisFields for all layers
     *
     * @param className maps to PF_WEB_PAGE.struts_class
     * @param userId    maps to PFUSER.userid
     * @param conn      A live JDBC Connection
     * @return OasisFields object
     * @throws SQLException
     */
    public static OasisFields createInstance(String className, String userId, Connection conn) throws SQLException {
        return createInstance(className, userId, conn, true, DefaultPageDefLoadProcessor.getInstance());
    }

    /**
     * Creates an instance of OasisFields for all layers
     *
     * @param className            maps to PF_WEB_PAGE.struts_class
     * @param userId               maps to PFUSER.userid
     * @param conn                 A live JDBC Connection
     * @param pageDefLoadProcessor Instance of sub-system PageDefLoadProcessor to enforce sub-system level security
     * @return OasisFields object
     * @throws SQLException
     */
    public static OasisFields createInstance(String className, String userId, Connection conn, PageDefLoadProcessor pageDefLoadProcessor) throws SQLException {
        return createInstance(className, userId, conn, true, pageDefLoadProcessor);
    }

    /**
     * Creates an instance of OasisFields.
     *
     * @param className            maps to PF_WEB_PAGE.struts_class
     * @param userId               maps to PFUSER.userid
     * @param conn                 A live JDBC Connection
     * @param includeLayerFields   Whether to load all fields in all layers
     * @param pageDefLoadProcessor Instance of sub-system PageDefLoadProcessor to enforce sub-system level security
     * @return OasisFields object
     * @throws SQLException
     */
    public static OasisFields createInstance(String className, String userId, Connection conn, boolean includeLayerFields, PageDefLoadProcessor pageDefLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "createInstance",
            new Object[]{className, userId, conn, String.valueOf(includeLayerFields)});
        PreparedStatement stmt = null;

        try {

            stmt = conn.prepareStatement(getPageQuerySQL());
            stmt.setString(1, className);
            OasisFields flds = newInstance(className);
            l.fine(new StringBuffer("Executing: ").append(pageQuery).append(" with ").append(className).toString());
            processPage(flds, stmt.executeQuery(), userId, pageDefLoadProcessor);

            if (includeLayerFields) {
                stmt = conn.prepareStatement(getLayerQuerySQL());
                //stmt = conn.prepareStatement(layerQuery);
                stmt.setString(1, className);
                l.fine(new StringBuffer("Executing; ").append(layerQuery).append(" with ").append(className).toString());
                processLayers(flds, stmt.executeQuery(), userId, pageDefLoadProcessor);
            }
            else {
                stmt = conn.prepareStatement(layerOnlyQuery);
                stmt.setString(1, className);
                l.fine(new StringBuffer("Executing; ").append(layerOnlyQuery).append(" with ").
                    append(className).toString());
                processLayers(flds, stmt.executeQuery(), pageDefLoadProcessor);
            }

            // If there is nothing in the OasisFields object, set it to null
            if (flds.size() == 0)
                flds = null;
            else {
                // there is something in the OasisFields collection
                // If there are fields on the page
                if (flds.layerCount < flds.size()) {
                    stmt = conn.prepareStatement(pageLoadQuery);
                    stmt.setString(1, className);
                    l.fine(new StringBuffer("Executing: ").append(pageLoadQuery).append(" with ").
                        append(className).toString());
                    processPageLoad(flds, stmt.executeQuery());
                }
                // If there are layer/fields
                if (flds.layerCount > 0) {
                    stmt = conn.prepareStatement(layerLoadQuery);
                    stmt.setString(1, className);
                    l.fine(new StringBuffer("Executing: ").append(layerLoadQuery).append(" with ").
                        append(className).toString());
                    processLayerLoad(flds, stmt.executeQuery());
                }
            }

            storeOasisFieldsXssOverrides(flds);

            l.exiting(clsName, "createInstance", flds);
            return flds;
        }
        finally {
                if (stmt != null) DatabaseUtils.close(stmt);            
        }
    }

    /**
     * Creates ArrayList objects for each OasisFormField in a layer with
     * a corresponding "List of Values". Each ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     * Default values are not used.
     *
     * @param conn    Database Connection
     * @param form    STRUTS ActionForm
     * @param request HttpServletRequest
     * @param layerId Layer
     * @throws Exception
     */
    public void getListOfValues(Connection conn, ActionForm form,
                                HttpServletRequest request, String layerId) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getListOfValues",
            new Object[]{conn, form, request, layerId});
        getListOfValues(conn, form, request, layerId, false);
        l.exiting(clsName, "getListOfValues");
    }

    /**
     * Creates ArrayList objects for each OasisFormField with
     * a corresponding "List of Values". Each ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     * Default values are not used.
     *
     * @param conn    Database Connection
     * @param form    STRUTS ActionForm
     * @param request HttpServletRequest
     * @throws Exception
     */
    public void getListOfValues(Connection conn, ActionForm form,
                                HttpServletRequest request) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getListOfValues",
            new Object[]{conn, form, request});
        getListOfValues(conn, form, request, false);
        l.exiting(clsName, "getListOfValues");
    }


    /**
     * Creates ArrayList objects for each OasisFormField in a layer with
     * a corresponding "List of Values". Each ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     *
     * @param conn        Database Connection
     * @param form        STRUTS ActionForm
     * @param request     HttpServletRequest
     * @param layerId     Layer
     * @param useDefaults Use field default values of dependent fields when loading
     *                    LOV if no value exists. For example, if there is a default
     *                    value for STATE, and no value has come in via an ActionForm
     *                    you can use the default value of STATE to load the list of
     *                    COUNTIES if this parm is TRUE.
     * @throws Exception
     */
    public void getListOfValues(Connection conn, ActionForm form,
                                HttpServletRequest request, String layerId, boolean useDefaults) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getListOfValues",
            new Object[]{conn, form, request, layerId, String.valueOf(useDefaults)});
        OasisFormField fld = null;
        int sz = loadList.size();
        // loop through elements in loadList ArrayList
        for (int i = 0; i < sz; i++) {
            // get the object
            Object o = loadList.get(i);
            // Only look at fields inside layers
            if (o instanceof LayerFieldLoad) {
                LayerFieldLoad lfl = (LayerFieldLoad) o;
                if (lfl.layerId.equals(layerId)) {
                    fld = (OasisFormField) get(lfl.layerId, lfl.fieldId);
                    if (fld != null)
                        processListOfValues(conn, form, request, fld, useDefaults);
                }
            }
        }
        if (isRequestStorageManagerLOVCacheEnabled())
            RequestStorageManager.getInstance().set(getStrLOVsLoaded(), "Y");
        l.exiting(clsName, "getListOfValues");

    }

    /**
     * Creates ArrayList objects for each OasisFormField with
     * a corresponding "List of Values". Each ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     *
     * @param conn        Database Connection
     * @param form        STRUTS ActionForm
     * @param request     HttpServletRequest
     * @param useDefaults Use field default values of dependent fields when loading
     *                    LOV if no value exists. For example, if there is a default
     *                    value for STATE, and no value has come in via an ActionForm
     *                    you can use the default value of STATE to load the list of
     *                    COUNTIES if this parm is TRUE.
     * @throws Exception
     */
    public void getListOfValues(Connection conn, ActionForm form,
                                HttpServletRequest request, boolean useDefaults) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "getListOfValues",
            new Object[]{conn, form, request, String.valueOf(useDefaults)});
        OasisFormField fld = null;
        int sz = loadList.size();
        // loop through elements in loadList ArrayList
        for (int i = 0; i < sz; i++) {
            // get the object
            Object o = loadList.get(i);
            // if a String, this is a fieldId on a page
            if (o instanceof String) {
                // Get the OasisFormField
                fld = (OasisFormField) get(o);
                processListOfValues(conn, form, request, fld, useDefaults);
            } else {// o should be instanceof LayerFieldLoad
                LayerFieldLoad lfl = (LayerFieldLoad) o;
                fld = (OasisFormField) get(lfl.layerId, lfl.fieldId);
                processListOfValues(conn, form, request, fld, useDefaults);
            }
        }
        if (isRequestStorageManagerLOVCacheEnabled())
            RequestStorageManager.getInstance().set(getStrLOVsLoaded(), "Y");
        l.exiting(clsName, "getListOfValues");

    }

    private Boolean isRequestStorageManagerLOVCacheEnabled(){
        return YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("RSM_LOV_CACHE", "Y")).booleanValue();
    }

    public Boolean isLOVsLoaded(){
        if(!isRequestStorageManagerLOVCacheEnabled())
            return false;
        return YesNoFlag.getInstance((String)RequestStorageManager.getInstance().get(getStrLOVsLoaded(), "N")).booleanValue();
    }

    private String getStrLOVsLoaded(){
        return actionClassName + "_LOVsLoaded";
    }

    /**
     * Creates ArrayList objects for the passed OasisFormField with
     * a corresponding "List of Values". The ArrayList is placed
     * into the HttpServletRequest under the key name of "fieldId" + "LOV"
     *
     * @param conn        JDBC Connection
     * @param form        DynaActionForm
     * @param request     HttpServletRequest
     * @param fld         form field
     * @param useDefaults Use field default values of dependent fields when loading
     *                    LOV if no value exists. For example, if there is a default
     *                    value for STATE, and no value has come in via an ActionForm
     *                    you can use the default value of STATE to load the list of
     *                    COUNTIES if this parm is TRUE.
     * @throws Exception
     */
    public void processListOfValues(Connection conn, ActionForm form,
                                    HttpServletRequest request, OasisFormField fld, boolean useDefaults) throws Exception {

        // Only process the fields with a LovSql attribute defined.
        if (!StringUtils.isBlank(fld.getLovSql())) {
            CodeLookupManager.getInstance().processListOfValues(conn, form, request, this, fld);
        }
    }

    public static IOasisFieldsFactory getFactory() {
        return factory;
    }

    public static void setFactory(IOasisFieldsFactory factory) {
        OasisFields.factory = factory;
    }

    public void addToLoadList(String fieldId) {
        Logger l = LogUtils.enterLog(getClass(), "addToLoadList", fieldId);
        loadList.add(fieldId);
        l.exiting(clsName, "addToLoadList");
    }

    public void addToLoadList(String layerId, String fieldId) {
        Logger l = LogUtils.enterLog(getClass(), "addToLoadList",
            new Object[]{layerId, fieldId});
        loadList.add(new LayerFieldLoad(layerId, fieldId));
        l.exiting(clsName, "addToLoadList");
    }

    public void removeFromLoadList(String layerId, String fieldId) {
        Logger l = LogUtils.enterLog(getClass(), "removeFormLoadList",
            new Object[]{layerId, fieldId});
        for (int i = 0; i < loadList.size(); i++) {
            if (loadList.get(i).getClass().equals(LayerFieldLoad.class)) {
                LayerFieldLoad layerFieldLoad = (LayerFieldLoad) loadList.get(i);
                if (layerFieldLoad.layerId.equals(layerId) && layerFieldLoad.fieldId.equals(fieldId)) {
                    loadList.remove(layerFieldLoad);
                }
            }
        }
        l.exiting(clsName, "removeFormLoadList");
    }

    /**
     * Determine the default value for a field. If a function prefix
     * (see defaultValueFuncPrefix) is found at the beginning of the
     * default value, then the function is used to calculate a new
     * defaultValue. these functions are supported:
     * TODAY - returns today's date in mm/dd/yyyy format
     * TODAY+n - returns today + n days in the future in mm/dd/yyyy format
     * TODAY-n - returns today - n days in the past in mm/dd/yyyy format
     * USER - returns the current user (HttpSession User)
     * SQL: - Executes the SQL to the right of the colon and uses the value from the first
     * column in the first row as the default value.  If the SQL is invalid, you will get a SQLException.  If the
     * SQL returns 0 rows, you will get null.
     *
     * @param value  current default value
     * @param userId current session user
     * @param conn   JDBC Connection
     * @return new default value
     */
    protected static String getDefaultValue(String value, String userId, Connection conn) throws SQLException {
        Logger l = LogUtils.enterLog(OasisFields.class, "getDefaultValue",
            new Object[]{value, userId});
        String dft = null;
        /* If the default value does not start with the function prefix,
            return it as is. */

        if (value == null || !value.startsWith(defaultValueFuncPrefix)) {
            dft = value;
        }
        else {
            /* Get the function */
            String func = value.substring(defaultValueFuncPrefix.length());
            /* If this is a date function */
            if (func.startsWith(FUNC_TODAY)) {
                Calendar rightNow = Calendar.getInstance();
                /* Set to right now */
                rightNow.setTime(new Date());
                /* If we're supposed to do math, do the math */
                if (func.length() > 5 && (func.charAt(5) == '-' || func.charAt(5) == '+')) {
                    int cal = (func.charAt(6) == 'Y' || func.charAt(6) == 'y') ? Calendar.YEAR :
                        (func.charAt(6) == 'M' || func.charAt(6) == 'm') ? Calendar.MONTH :
                            (func.charAt(6) == 'D' || func.charAt(6) == 'd') ? Calendar.DATE : -99;
                    int pos = 7;
                    if (cal == -99) {
                        cal = Calendar.DATE;
                        pos = 6;
                    }
                    int days = Integer.parseInt(func.substring(pos)) * ((func.charAt(5) == '-') ? -1 : 1);
                    rightNow.add(cal, days);
                }
                /* Return the date */
                dft = DateUtils.formatDate(rightNow.getTime());
            }
            else if (func.equals(FUNC_USER)) {      /* Current User */
                dft = userId;
            }
            else if (func.startsWith(FUNC_SQL_EXPR)) { /* SQL Expression */
                dft = DatabaseUtils.evaluateSqlExpression(conn, func.substring(FUNC_SQL_EXPR.length()), 1);
            }
            else {
                /* Not one of our functions, return value as is */
                dft = value;
            }
        }
        l.exiting(clsName, "getDefaultValue", dft);
        return dft;
    }

    /**
     * Returns an ArrayList of fieldids.  For the fieldIds on a layer, it will look like this:
     * "myfieldId on myLayerId"
     *
     * @return ArrayList of Strings
     */
    public ArrayList getFieldIds() {
        Logger l = LogUtils.enterLog(getClass(), "getFieldIds");
        Iterator it = this.values().iterator();
        ArrayList list = new ArrayList();
        /* Loop through keys */
        while (it.hasNext()) {
            Object o = it.next();
            // If map, then this is a layer, iterator through the fields on the layer.
            if (o instanceof WebLayer) {
                WebLayer webLayer = (WebLayer) o;
                // loop
                Iterator it1 = webLayer.values().iterator();
                while (it1.hasNext()) {
                    OasisFormField field = (OasisFormField) it1.next(); // Use the OasisFormField be cause the key is a case-insenitive version of the fieldId
                    list.add(field.getFieldId() + " on LayerId:" + webLayer.getLayerId());   // add fieldid with layer info
                }
            }
            else {
                OasisFormField field = (OasisFormField) o; // Use the OasisFormField because the key is a case-insenitive version of the fieldId
                list.add(field.getFieldId()); //add fieldid
            }
        }
        l.exiting(clsName, "getFieldIds", list);
        return list;

    }

    /**
     * Returns an Arraylist of Layer Ids
     *
     * @return ArrayList
     */
    public ArrayList getLayerIds() {
        Logger l = LogUtils.enterLog(getClass(), "getLayerIds");
        Iterator it = this.keySet().iterator();
        ArrayList list = new ArrayList();
        /* Loop through keys */
        while (it.hasNext()) {
            String key = (String) it.next();
            Object o = this.get(key);
            /* If a Map (Check if we need to do LinkedHashMap instead), add to list */
            if (o instanceof WebLayer) {
                WebLayer webLayer = (WebLayer) o;
                list.add(webLayer.getLayerId());
            }
        }
        l.exiting(clsName, "getLayerIds", list);
        return list;
    }

    public int getLayerCount() {
        return layerCount;
    }

    public void setLayerCount(int layerCount) {
        this.layerCount = layerCount;
    }

    /**
     * Getter
     *
     * @return HashMap
     */
    public HashMap getFieldDeps() {
        return fieldDeps;
    }

    public String getActionClassName() {
        return actionClassName;
    }

    public void setActionClassName(String className) {
        actionClassName = className;
    }

    /**
     * Returns a delimited list of protected felds, delimited by a "^".  A protected field
     * is one that should neither be sent to a web browser in any form or updated by the current user.
     * The table.column list is returned here.  The fieldid will be used in place of the table.column
     * if the table.column is not provided.
     *
     * @return Delimited list of protected table.columns.
     */
    public String getProtectedFields() {
        // If all we have is '^', then return null because no fields were protected.
        if (protectedFields.length() == 1)
            return null;
        else
            return protectedFields.toString();
    }

    /**
     * Override the ancestor method to return the case-sensitive keys.
     */
    public Set keySet() {
        return caseSensitiveMap.keySet();
    }

    /**
     * Override the ancestor method to use the case-sensitive map.
     */
    public boolean containsKey(Object key) {
        return caseSensitiveMap.containsKey(key);
    }

    /**
     * Override the ancestor method to store in both case-sensitive and case-insensitive maps.
     */
    public void putAll(Map m) {
        super.putAll(m);
        caseSensitiveMap.putAll(m);
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        caseSensitiveMap.clear();
        super.clear();
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString()).append(" ::: ");
        buf.append("dti.oasis.tags.OasisFields");
        buf.append("{fieldDeps=").append(fieldDeps);
        buf.append(",layerCount=").append(layerCount);
        buf.append(",loadList=").append(loadList);
        buf.append(",protectedFields=").append(protectedFields);
        buf.append('}');
        return buf.toString();
    }


    /*
   *  Deep clone() method
   *
   * */
    @Override
    public Object clone() {
        OasisFields cloned = (OasisFields)super.clone();

        //Clears LinkedHashMap to avoid double count
        cloned.caseSensitiveMap = new LinkedHashMap();

        Iterator it = caseSensitiveMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object newValue = null;
            if(value instanceof OasisFormField) {
                newValue = ((OasisFormField) value).clone();
            } else if(value instanceof WebLayer) {
                newValue = ((WebLayer) value).clone();
            }
            if(newValue!=null){
                cloned.put(key, newValue);
            }

        }

        cloned.loadList = getLoadListDeepCopy();

        return cloned;
    }

    /*
    *  Deep copy of the ArrayList loadList
    *
    * */
    private ArrayList getLoadListDeepCopy(){
        ArrayList result = new ArrayList(loadList.size());
        for (Object entry : loadList) {
            if(entry instanceof LayerFieldLoad){
                Object newEntry = ((LayerFieldLoad)entry).clone();
                result.add(newEntry);
            } else {
                result.add(entry);
            }
        }
        return result;
    }

    private static String getPageQuerySQL(){
        return parseSQL(pageQuery);
    }

    private static String getLayerQuerySQL(){
        return parseSQL(layerQuery);
    }

    private static String parseSQL(String sql) {
        String str = "";
        str = sql;

        if (isMaterializeViewFlagEnabled()) {
            str = sql.replace("pf_web_page_layer_fields", "pf_web_page_layer_fields_w_mv");
        }
        return str;
    }

    private static boolean isMaterializeViewFlagEnabled(){
        String searchMVFlag = SysParmProvider.getInstance().getSysParm("CS_USE_PFWEB_MVS", "Y");
        return YesNoFlag.getInstance(searchMVFlag).booleanValue();
    }

    /**
     * Copy all the overrides xss filter fields.
     *
     * @param fields  OasisFields.
     */
    public static void storeOasisFieldsXssOverrides(OasisFields fields) {
        Logger l = LogUtils.enterLog(OasisFields.class, "storeOasisFieldsXssOverrides", new Object[]{fields});
        if (fields != null) {
            ArrayList<OasisFormField> oasisFieldArray = fields.getAllFieldList();
            Map xssOverrideFields = new HashMap();

            for (OasisFormField f : oasisFieldArray) {
                if (f.getUseXssFilter() != null && f.getUseXssFilter().equalsIgnoreCase(RequestIds.DO_NOT_USE_XSS_FILTER)) {
                    String fieldName = f.getFieldId();
                    xssOverrideFields.put(fieldName.toUpperCase(), f.getUseXssFilter());
                }
            }

            ActionHelper.getRequestHelper().getPageViewCacheMap().put(RequestIds.OASIS_XSS_OVERRIDES_FIELDS, xssOverrideFields);

            l.exiting(clsName, "storeOasisFieldsXssOverrides", xssOverrideFields);
        }
    }

    /**
     * Copy all the masked fields to the new pageViewStateId.
     *
     * @param pageViewStateId  key to retrieve the pageViewData map.
     */
    public static void copyMaskedFields (String pageViewStateId) {
        PageViewStateAdmin pageViewStateAdmin = (PageViewStateAdmin) UserSessionManager.getInstance().getUserSession();
        Map maskedFieldsCachedData = (Map)pageViewStateAdmin.getPageViewData(pageViewStateId).get(RequestIds.OASIS_MASKED_FIELDS);
        ActionHelper.getRequestHelper().getPageViewCacheMap().put(RequestIds.OASIS_MASKED_FIELDS, maskedFieldsCachedData);
    }
}
