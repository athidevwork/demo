package dti.oasis.tags;

import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates information needed to build an OasisGrid.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 * @see dti.oasis.tags.OasisGrid
 * @see dti.oasis.util.DisconnectedResultSet
 *      <p/>
 *      <p/>
 *      Date:   Jul 3, 2003
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
*  2/8/2004        jbe         Added Logging & toString
*  5/29/2004       jbe         Add rows & cols attributes for textareas
*  6/25/2004       jbe         Add title
*  8/24/2004       jbe         Add TYPE_UPDATEONLYNUMBER, TYPE_UPDATEONLYDATE
*                              TYPE_UPDATEONLY_DATETIME, TYPE_UPDATEONLYMONEY
*  10/7/2004       jbe         Add CN_DECIMALPLACES
*  10/28/2004      jbe         Fix handling of read-only fields
*  4/6/2005         jbe        Add CN_FIELDID, CN_PROTECTED, refactor
*  4/28/2005	   jbe		   Revise logic for areadonly fields. switch to UPDATE_ONLY instead of DEFAULT
*  5/19/2005		jbe		   Add TYPE_UPDATEONLYURL
*  01/23/2007       lmm        Added support for column width;
*  01/23/2007       wer        Added support for displaying a readonly codelookup as label;
*                              Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 01/31/2007        wer        Enhanced to support defining Grid Column Order by the Grid Header
* 02/01/2007        wer         Fix setting up grid header type to UPDATEONLYDROPDOWN if form field is on page, not in grid header layer.
* 09/26/2007        sxm        Replaced CN_CELLWIDTH w/ CN_STYLE
* 01/09/2008        wer         Added support to specify hrefkey as the data column name
* 02/26/2008        wer         Added storage of xml column index in addition to data column index.
* 03/03/2008        James      Issue#79614 eClaims architectural enhancement to 
*                              take advantage of ePolicy architecture
*                              Handling HREF is a new enhancement in WebWB
* 03/24/2008        Fred       Added data type TYPE_PERCENTAGE.
* 06/12/2008        Kenney     Modified postProcessType function. 
*                              If the checkbox field is read-only, change the type to TYPE_CHECKBOXREAD
* 10/31/2008        Joe        Enhanced decodeDisplayType() method to handle money type
* 04/13/2009        mxg        82494: Changes to handle Date Format Internationalization
* 08/26/2009        kshen      Added type TYPE_UPDATEONLYPERCENTAGE.
* 09/03/2006        mxg        Added type DATATYPE_PHONE
* 09/23/2009       Fred        Issue 96884. Extend Internationalization to Date / Time fields
* 10/08/2009       mgitelm     95437: Added the ability to strip off the prefix of the fieldId to match the data column
*                              (to support the old style of defining fieldIs in eClaims/eCIS) on the Action Class level
* 10/09/2009       fcb         Issue# 96764: added logic for masked fields.
* 11/27/2009       kenney      enh to support phone format
* 03/22/2010       James       Issue#105489 Added gridHeaderLayerId
* 03/29/2010       James       Issue#105489 Added hadGridHeaderLayerId
* 11/09/2010       Kenney      issue#114120 - enh to support displaying multiple select field in grid
* 09/20/2011       mxg         Issue #100716: Added Display Type FORMATTEDNUMBER, Format Pattern
* 08/08/2013       jshen       issue 145027 - Change the multiple select popup field display type to TYPE_MULTIPLE_DROPDOWN
* 08/07/2013       Parker      Issue#134836: Use the lov in detail filed to display the value when the field in Grid is not set lov
* 02/11/2014       jyang       Issue#150391 - Add logic to check type 'TYPE_UPDATEONLYMONEY' in createHeaderForDataColumn().
* 04/02/2014       Elvin       Issue 149361: set type attribute for percentage data type field, in order to
*                                   correct percentage data field display error when loading page
* 12/31/2014       jxgu        Issue 159735: check "null" column and add it to grid header
* 08/07/2015       kshen       Issue 164735. Changed addHeader to process the type of hidden fields.
* 08/03/2016       huixu       Issue#169625 Add check to all of the Logger.log* methods to return immediately if the log level is not loggable.
* 01/10/2016       mlm         181684 - Refactored to enforce change for issue 134836 only for fields hidden in grid, but visible in detail section.
* 05/10/2017       ylu         169144: pass the field's mask property to addHeader
* 07/20/2017       mlm         186748 - Reverted fix for 181684, but refactored to publish "code" or "description" in the
*                              grid based on grid field's display type configuration.
* 12/05/2017       cesar       190017 - Added getHeaderIndexesByColumnName() to return  headerIndexesByColumnName;
* 04/04/2018       cesar       #191962 - added col_width and min_col_width
* 07/04/2018       kshen       194134. Add col width and min col width for fields in grid xml.
* 08/06/2018       dpang       194641 - added col_aggregate
* ---------------------------------------------------
*/
public class XMLGridHeader implements Serializable {

    private ArrayList header = new ArrayList();
    /**
     * Key to grid column label
     */
    public static final String CN_NAME = "name";
    /**
     * Key to column fieldname (aka. fieldId)
     */
    public static final String CN_FIELDNAME = "fieldname";
    /**
     * Key to grid column type
     */
    public static final String CN_TYPE = "type";
    /**
     * Key to grid column length
     */
    public static final String CN_LENGTH = "length";
    /**
     * Key to grid column display type
     */
    public static final String CN_DISPLAY = "display";
    /**
     * Key to grid column alignment
     */
    public static final String CN_ALIGN = "align";
    /**
     * Key to grid column visibility
     */
    public static final String CN_VISIBLE = "visible";
    /**
     * Key to grid column data lov
     */
    public static final String CN_LISTDATA = "listdata";
    /**
     * Key to grid column HREF - the URL
     */
    public static final String CN_HREF = "href";
    /**
     * Key to grid column HREFKEY - The key to add to the URL
     */
    public static final String CN_HREFKEY = "hrefkey";
    /**
     * Key to grid column HREFKEYNAME - The key fieldId to add to the URL
     */
    public static final String CN_HREFKEYNAME = "hrefkeyname";
    /**
     * Key to grid column HREF in WebWB- the URL
     */
    public static final String CN_FIELD_HREF = "fieldHref";
    /**
     * Key to grid column maxlength
     */
    public static final String CN_MAXLENGTH = "maxl";
    /**
     * Key to grid column # rows in listbox or textarea
     */
    public static final String CN_ROWS = "rows";
    /**
     * Key to grid column # cols in textbox or textarea
     */
    public static final String CN_COLS = "cols";
    /**
     * Key to grid column title - title attribute of input element
     */
    public static final String CN_TITLE = "title";
    /**
     * Key to grid column # decimal places
     */
    public static final String CN_DECIMALPLACES = "decimalplaces";
    /**
     * Key to grid column style
     */
    public static final String CN_STYLE = "style";
    /**
     * Key to grid column protection
     */
    public static final String CN_PROTECTED = "protected";
    /**
     * Key to grid column fieldId.
     */
    public static final String CN_FIELDID = "fieldid";
    /**
     * Key to detail column detailFieldId.
     */
    public static final String CN_DETAIL_FIELDID = "detailFieldid";

    /**
     * Key to data column name.
     * This value is automatically setup by the XMLGridHeader, so it should NOT be specified in the Grid Header XML file.
     */
    public static final String CN_DATACOLUMNNAME = "datacolumnname";
    /**
     * Key to grid column mask protection
     */
    public static final String CN_MASKED = "masked";

    /**
     * Key to grid column format pattern
     */
    public static final String CN_PATTERN = "pattern";

    /**
     * Read only text
     */
    public static final int TYPE_DEFAULT = 0;
    /**
     * Text box
     */
    public static final int TYPE_TEXT = 1;
    /**
     * Read only date
     */
    public static final int TYPE_FORMATDATE = 2;
    /**
     * Read only datetime
     */
    public static final int TYPE_FORMATDATETIME = 3;
    /**
     * Text box allowing number entry only
     */
    public static final int TYPE_NUMBER = 4;
    /**
     * Dropdown listbox
     */
    public static final int TYPE_DROPDOWN = 5;
    /**
     * Hyperlink
     */
    public static final int TYPE_URL = 6;
    /**
     * Read only text, programatically updateable
     */
    public static final int TYPE_UPDATEONLY = 7;
    /**
     * Text box allowing date entry only
     */
    public static final int TYPE_DATE = 8;
    /**
     * Checkbox
     */
    public static final int TYPE_CHECKBOX = 9;
    /**
     * Readonly checkbox
     */
    public static final int TYPE_CHECKBOXREAD = 10;
    /**
     * Radiobutton
     */
    public static final int TYPE_RADIOBUTTON = 11;
    /**
     * Image
     */
    public static final int TYPE_IMG = 12;
    /**
     * Hidden row id
     */
    public static final int TYPE_ANCHOR = 13;
    /**
     * Read only currency
     */
    public static final int TYPE_FORMATMONEY = 14;
    /**
     * Percentage data type
     */
    public static final int TYPE_PERCENTAGE = 30;

    /**
     * Multiline textarea
     */
    public static final int TYPE_TEXTAREA = 15;
    /**
     * Readonly date, programatically updateable
     */
    public static final int TYPE_UPDATEONLYDATE = 16;
    /**
     * Readonly datetime, programatically updateable
     */
    public static final int TYPE_UPDATEONLYDATETIME = 17;
    /**
     * Readonly currency, programatically updateable
     */

    public static final int TYPE_UPDATEONLYMONEY = 18;
    /**
     * Readonly number, programatically updateable
     */

    public static final int TYPE_UPDATEONLYNUMBER = 19;

    /**
     * URL, programatically updateable
     */
    public static final int TYPE_UPDATEONLYURL = 20;

    /**
     * Dropdown, programatically updateable
     */
    public static final int TYPE_UPDATEONLYDROPDOWN = 21;

    /**
     * Uppercase Text updateable
     */
    public static final int TYPE_UPPERCASE_TEXT = 22;

    /**
     * Uppercase Text updateable
     */
    public static final int TYPE_LOWERCASE_TEXT = 23;

    public static final int TYPE_UPDATEONLYPERCENTAGE = 24;

    /**
     * Readonly phone, programatically updateable
     */
    public static final int TYPE_UPDATEONLYPHONE = 25;

    /**
     * Local phone number data type
     */
    public static final int TYPE_PHONE = 26;

    /**
     * Multiple Select Dropdown listbox
     */
    public static final int TYPE_MULTIPLE_DROPDOWN = 27;

    /**
     * Multiple Select Dropdown, programatically updateable
     */
    public static final int TYPE_UPDATEONLY_MULTIPLE_DROPDOWN = 28;

    /**
     * Display as text
     */
    public static final int DISPLAY_DEFAULT = 0;
    /**
     * Display as currency
     */
    public static final int DISPLAY_MONEY = 1;

    /**
     * Display as formated number
     */
    public static final int DISPLAY_FORMATTED_NUMBER = 2;

    /**
     * Display as dropdown
     */
    public static final int DISPLAY_SELECT = 3;

    public static final String COL_WIDTH = "colWidth";
    public static final String COL_MIN_WIDTH = "colMinWidth";
    public static final String COL_AGGREGATE = "colAggregate";

    private boolean isInitialized = false;
    private boolean isFieldnameSpecifiedForAllHeaders = true;
    private Map fields = null;
    private Map layerFields = null;
    private Map headersByFieldOrColumnName = new HashMap();
    private Map headerIndexesByColumnName = new HashMap();
    private Map dataColumnIndexesByColumnName = new HashMap();
    private Map xmlColumnIndexesByColumnName = new HashMap();
    private String gridHeaderFieldnameSuffix;
    private String anchorColumnName;
    private int m_dataColumnIndexForAnchorColumn = 1; // Set to the first column, given a 1-based index
    private TreeMap m_visibleHeaderOrderMap = new TreeMap();
    private TreeMap m_hiddenHeaderOrderMap = new TreeMap();
    private Boolean m_gridHeaderDefinesDisplayableColumnOrder;
    private String gridHeaderLayerId = null;

    private static Map c_typeMap;
    static {
        c_typeMap = new HashMap();
        // Add Header Type for the corresponding OasisFormField Display Types
        c_typeMap.put("TEXT", Integer.valueOf(TYPE_TEXT));
        c_typeMap.put("FORMATTEDNUMBER", Integer.valueOf(TYPE_TEXT));
        c_typeMap.put("TEXTAREA", Integer.valueOf(TYPE_TEXTAREA));
        c_typeMap.put("SELECT", Integer.valueOf(TYPE_DROPDOWN));
        c_typeMap.put("MULTISELECT", Integer.valueOf(TYPE_MULTIPLE_DROPDOWN));
        c_typeMap.put("MULTISELECTPOPUP", Integer.valueOf(TYPE_MULTIPLE_DROPDOWN));
        c_typeMap.put("RADIOBUTTON", Integer.valueOf(TYPE_RADIOBUTTON));
        c_typeMap.put("CHECKBOX", Integer.valueOf(TYPE_CHECKBOX));
        c_typeMap.put("MULTIBOX", Integer.valueOf(TYPE_CHECKBOX));

        // Add Header Type for the corresponding OasisFormField DataTypes
        c_typeMap.put(OasisFields.TYPE_DATE, Integer.valueOf(TYPE_DATE));
        c_typeMap.put(OasisFields.TYPE_NUMBER, Integer.valueOf(TYPE_NUMBER));
        c_typeMap.put(OasisFields.TYPE_TEXT, Integer.valueOf(TYPE_TEXT));
        c_typeMap.put(OasisFields.TYPE_UPPERCASE_TEXT, Integer.valueOf(TYPE_UPPERCASE_TEXT));
        c_typeMap.put(OasisFields.TYPE_LOWERCASE_TEXT, Integer.valueOf(TYPE_LOWERCASE_TEXT));
        c_typeMap.put(OasisFields.TYPE_CURRENCY, Integer.valueOf(TYPE_FORMATMONEY));
        c_typeMap.put(OasisFields.TYPE_CURRENCY_FORMATTED, Integer.valueOf(TYPE_FORMATMONEY));
        c_typeMap.put(OasisFields.TYPE_PERCENTAGE, Integer.valueOf(TYPE_PERCENTAGE));
        c_typeMap.put(OasisFields.TYPE_PHONE, Integer.valueOf(TYPE_PHONE));
        c_typeMap.put(OasisFields.TYPE_TIME, Integer.valueOf(TYPE_FORMATDATETIME));
    }

    public Map getFields() {
        return fields;
    }

    public void setFields(Map fields) {
        this.fields = fields;
    }

    public Map getLayerFields() {
        return layerFields;
    }

    public void setLayerFields(Map layerFields) {
        this.layerFields = layerFields;
    }

    public String getAnchorColumnName() {
        return anchorColumnName;
    }

    public void setAnchorColumnName(String anchorColumnName) {
        this.anchorColumnName = anchorColumnName;
        isInitialized = true;
    }

    /**
     * @param field
     * @param type
     * @param length
     * @param display
     * @param align
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param cellWidth
     * @param fieldHref
     */
    public void addHeader(OasisFormField field, int type, String length,
                          int display, String align, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, String cellWidth, String fieldHref) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader",
                    new Object[]{field, Integer.valueOf(type), length, Integer.valueOf(display), align, listData,
                            href, hrefkey, maxLength, rows, cols, title, decimalPlaces});
        }

        String visible = YesNoFlag.getInstance(field.getIsVisible()).getName();

        type = postProcessType(type, display, field.getIsReadOnly() || !field.getIsVisible());

        // If no rows are passed and we have a value in the field, use it
        if (StringUtils.isBlank(rows) && !StringUtils.isBlank(field.getRows()))
            rows = field.getRows();

        // If no cols are passed and we have a value in the field, use it
        if (StringUtils.isBlank(cols) && !StringUtils.isBlank(field.getCols()))
            cols = field.getCols();

        // Take the allignment set in the OasisFormField if it is not empty.
        if (!StringUtils.isBlank(field.getAlignment())) {
            align = field.getAlignment();
        }

        String colWidth = field.getColWidth();

        String colMinWidth = field.getColMinWidth();

        addHeader(field.getFieldId(), field.getLabel(), type, length, display, align, visible, listData,
                href, hrefkey, maxLength, rows, cols, title, decimalPlaces,
                field.getIsProtected(), field.getFieldId(), cellWidth, field.getStyleInlineForCell(), fieldHref, field.getIsMasked(),
                colWidth, colMinWidth);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addHeader");
        }
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength) {
        addHeader(name, type, length, display, align, visible, listData, href, hrefkey, maxLength,
                false);
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param isProtected
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, boolean isProtected) {
        addHeader(name, type, length, display, align, visible, listData, href,
                hrefkey, maxLength, isProtected, false);
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param isProtected
     * @param isMasked
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, boolean isProtected, boolean isMasked) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader",
                    new Object[]{name, Integer.valueOf(type), length, Integer.valueOf(display), align, listData,
                            href, hrefkey, maxLength, isProtected, isMasked});
        }
        HashMap map = new HashMap();
        map.put(CN_NAME, name);
        map.put(CN_TYPE, Integer.valueOf(type));
        map.put(CN_LENGTH, length);
        map.put(CN_DISPLAY, Integer.valueOf(display));
        map.put(CN_ALIGN, align);
        map.put(CN_VISIBLE, visible);
        map.put(CN_LISTDATA, listData);
        map.put(CN_HREF, href);
        map.put(CN_HREFKEY, hrefkey);
        if (!StringUtils.isNumeric(hrefkey)) {
            map.put(CN_HREFKEYNAME, hrefkey);
        }
        map.put(CN_MAXLENGTH, maxLength);
        map.put(CN_PROTECTED, Boolean.valueOf(isProtected));
        map.put(CN_MASKED, Boolean.valueOf(isMasked));
        addHeader(map);
        isInitialized = true;
        isFieldnameSpecifiedForAllHeaders = false;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addHeader");
        }
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param cellWidth
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, String cellWidth) {
        addHeader(null, name, type, length, display, align, visible, listData, href, hrefkey, maxLength,
                rows, cols, title, decimalPlaces, false, null, cellWidth);
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces) {
        addHeader(null, name, type, length, display, align, visible, listData, href, hrefkey, maxLength,
                rows, cols, title, decimalPlaces, false, null, "");
    }

    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param isProtected
     * @param fieldId
     * @param cellWidth
     */
    public void addHeader(String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, boolean isProtected, String fieldId, String cellWidth) {
        addHeader(null, name, type, length, display,
                align, visible, listData, href,
                hrefkey, maxLength, rows, cols,
                title, decimalPlaces, isProtected, fieldId, cellWidth);
    }
    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param isProtected
     * @param fieldId
     * @param cellWidth
     */
    public void addHeader(String fieldname, String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, boolean isProtected, String fieldId, String cellWidth) {
        addHeader(fieldname, name, type, length, display,
                align, visible, listData, href,
                hrefkey, maxLength, rows, cols,
                title, decimalPlaces, isProtected, fieldId, cellWidth, null, null);
    }
    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param isProtected
     * @param fieldId
     * @param cellWidth
     * @param style
     */
    public void addHeader(String fieldname, String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, boolean isProtected, String fieldId, String cellWidth,
                          String style, String fieldHref) {
        addHeader(fieldname,name,type,length,display,align,visible,listData,href,hrefkey,
                maxLength,rows,cols,title,decimalPlaces,isProtected,fieldId,cellWidth,
                style,fieldHref,false, null, null);
    }
    /**
     * @param name
     * @param type
     * @param length
     * @param display
     * @param align
     * @param visible
     * @param listData
     * @param href
     * @param hrefkey
     * @param maxLength
     * @param rows
     * @param cols
     * @param title
     * @param decimalPlaces
     * @param isProtected
     * @param fieldId
     * @param cellWidth
     * @param style
     * @param fieldHref
     * @param isMasked
     */
    public void addHeader(String fieldname, String name, int type, String length, int display,
                          String align, String visible, ArrayList listData, String href,
                          String hrefkey, String maxLength, String rows, String cols,
                          String title, String decimalPlaces, boolean isProtected, String fieldId, String cellWidth,
                          String style, String fieldHref, boolean isMasked,
                          String colWidth, String colMinWidth) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader",
                    new Object[]{fieldname, Integer.valueOf(type), length, Integer.valueOf(display), align, listData,
                            href, hrefkey, maxLength, rows, cols, title, decimalPlaces, isProtected, fieldId, cellWidth,
                            style, fieldHref, isMasked, colWidth, colMinWidth});
        }

        HashMap map = new HashMap();
        map.put(CN_NAME, name);
        map.put(CN_FIELDNAME, fieldname);
        map.put(CN_TYPE, Integer.valueOf(type));
        map.put(CN_LENGTH, length);
        map.put(CN_DISPLAY, Integer.valueOf(display));
        map.put(CN_ALIGN, align);
        map.put(CN_VISIBLE, visible);
        map.put(CN_LISTDATA, listData);
        map.put(CN_HREF, href);
        map.put(CN_HREFKEY, hrefkey);
        if (!StringUtils.isNumeric(hrefkey)) {
            map.put(CN_HREFKEYNAME, hrefkey);
        }
        map.put(CN_FIELD_HREF, fieldHref);
        map.put(CN_MAXLENGTH, maxLength);
        map.put(CN_ROWS, rows);
        map.put(CN_COLS, cols);
        map.put(CN_TITLE, title);
        map.put(CN_DECIMALPLACES, decimalPlaces);
        map.put(CN_PROTECTED, Boolean.valueOf(isProtected));
        map.put(CN_MASKED, Boolean.valueOf(isMasked));
        map.put(CN_FIELDID, fieldId);

        // override cell width if we got style
        if (style != null)
            map.put(CN_STYLE, style);
        else if (!StringUtils.isBlank(cellWidth))
            map.put(CN_STYLE, "WIDTH:" + cellWidth + ";");

        if (!StringUtils.isBlank(colWidth)) {
            map.put(COL_WIDTH, colWidth);
        }

        if (!StringUtils.isBlank(colMinWidth)) {
            map.put(COL_MIN_WIDTH, colMinWidth);
        }

        addHeader(fieldname,  map);
        isInitialized = true;
        if (StringUtils.isBlank(fieldname))
            isFieldnameSpecifiedForAllHeaders = false;
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addHeader");
        }
    }

    private int postProcessType(int type, int display, boolean isReadOnly) {

        if (isReadOnly) {
            switch (type) {
                case TYPE_DATE:
                    type = TYPE_UPDATEONLYDATE;
                    break;
                case TYPE_PHONE:
                    type = TYPE_UPDATEONLYPHONE;
                    break;
                case TYPE_NUMBER:
                    type = (display == DISPLAY_MONEY) ? TYPE_UPDATEONLYMONEY : TYPE_UPDATEONLYNUMBER;
                    break;
                case TYPE_DROPDOWN:
                    type = OasisTagHelper.displayReadonlyCodeLookupAsLabel() ? TYPE_UPDATEONLYDROPDOWN : TYPE_UPDATEONLY;
                    break;
                case TYPE_MULTIPLE_DROPDOWN:
                    type = OasisTagHelper.displayReadonlyCodeLookupAsLabel() ? TYPE_UPDATEONLY_MULTIPLE_DROPDOWN : TYPE_UPDATEONLY;
                    break;
                case TYPE_TEXT:
                case TYPE_UPPERCASE_TEXT:
                case TYPE_LOWERCASE_TEXT:
                case TYPE_RADIOBUTTON:
                case TYPE_TEXTAREA:
                    type = (display == DISPLAY_MONEY) ? TYPE_UPDATEONLYMONEY : TYPE_UPDATEONLY;
                    break;
                case TYPE_CHECKBOX:
                    type = TYPE_CHECKBOXREAD;
                    break;
                case TYPE_URL:
                case TYPE_UPDATEONLYURL:
                    if (!GridHelper.getGridDisplayReadonlyUrlAsUrl()) {
                        type = (display == DISPLAY_MONEY) ? TYPE_FORMATMONEY : TYPE_DEFAULT;
                    }
                    break;
                case TYPE_FORMATMONEY:
                case TYPE_UPDATEONLYMONEY:
                    type = TYPE_UPDATEONLYMONEY;
                    break;
                case TYPE_PERCENTAGE:
                    type = TYPE_UPDATEONLYPERCENTAGE;
                    break;
                default:
                    break;
            }
        }
        return type;
    }

    /**
     * Add the Map of header attributes to the list, and returns the index of the added element.
     */
    protected int addHeader(Map headerAttributes) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader", new Object[]{headerAttributes});
        }

        header.add(headerAttributes);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addHeader");
        }
        return header.size();
    }

    protected void addHeader(String fieldOrColumnName, Map headerAttributes) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addHeader", new Object[]{fieldOrColumnName, headerAttributes});
        }

        int headerIdx = addHeader(headerAttributes);
        if (!StringUtils.isBlank(fieldOrColumnName) || "null".equals(fieldOrColumnName)) {
            putHeader(fieldOrColumnName, headerAttributes, headerIdx);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "addHeader");
        }
    }

    /**
     * Keep track of the header keyed by the fieldOrColumnName.
     * The headerIndex is expected to be 1-based
     */
    protected void putHeader(String fieldOrColumnName, Map headerAttributes, int headerIndex) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "putHeader", new Object[]{fieldOrColumnName, headerAttributes, String.valueOf(headerIndex)});
        }

        // Use a case insensitive fieldOrColumnName when mapping things to fieldOrColumnName
        String ciFieldOrColumnName = fieldOrColumnName.toUpperCase();

        headersByFieldOrColumnName.put(ciFieldOrColumnName, headerAttributes);
        headerIndexesByColumnName.put(ciFieldOrColumnName, Integer.valueOf(headerIndex));

        // If there is a grid header fieldOrColumnName suffix defined for this grid,
        // associate the header attributes for both the base fieldOrColumnName and the grid header fieldOrColumnName
        if (hasGridHeaderFieldnameSuffix()) {
            String gridHeaderFieldnameSuffix = getGridHeaderFieldnameSuffix();
            if (ciFieldOrColumnName.endsWith(gridHeaderFieldnameSuffix)) {
                // The fieldOrColumnName already has the suffix; strip off the suffix to get the columnName
                String columnName = ciFieldOrColumnName.substring(0, ciFieldOrColumnName.length() - gridHeaderFieldnameSuffix.length());
                headersByFieldOrColumnName.put(columnName, headerAttributes);
                headerIndexesByColumnName.put(columnName, Integer.valueOf(headerIndex));

                // Add the Data ColumnName to the header attributes
                headerAttributes.put(CN_DATACOLUMNNAME, columnName);
            }
            else {
                // The fieldOrColumnName is the base fieldOrColumnName; add the suffix to get the grid header fieldOrColumnName
                headersByFieldOrColumnName.put(ciFieldOrColumnName + gridHeaderFieldnameSuffix, headerAttributes);
                headerIndexesByColumnName.put(ciFieldOrColumnName + gridHeaderFieldnameSuffix, Integer.valueOf(headerIndex));

                // Add the Data ColumnName to the header attributes
                headerAttributes.put(CN_DATACOLUMNNAME, ciFieldOrColumnName);
            }
        }
        else {
            // Add the Data ColumnName to the header attributes
            headerAttributes.put(CN_DATACOLUMNNAME, ciFieldOrColumnName);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "putHeader");
        }
    }

    public boolean hasHeader(String fieldOrColumnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasHeader", new Object[]{fieldOrColumnName});
        }

        // Use a case insensitive fieldOrColumnName when mapping things to fieldOrColumnName
        String ciFieldOrColumnName = fieldOrColumnName.toUpperCase();
        boolean hasHeader = headersByFieldOrColumnName.containsKey(ciFieldOrColumnName);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hasHeader", String.valueOf(hasHeader));
        }
        return hasHeader;
    }

    public Map getHeader(String fieldOrColumnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getHeader", new Object[]{fieldOrColumnName});
        }

        // Use a case insensitive fieldOrColumnName when mapping things to fieldOrColumnName
        String ciFieldOrColumnName = fieldOrColumnName.toUpperCase();
        Map headerAttributes = (Map) headersByFieldOrColumnName.get(ciFieldOrColumnName);
        if (headerAttributes == null) {
            throw new IllegalArgumentException("The Header for field '" + fieldOrColumnName + "' is not defined.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getHeader", headerAttributes);
        }
        return headerAttributes;
    }

    public Integer getHeaderIndex(String columnName) {
        // Use a case insensitive columnName when mapping things to columnName
        String ciFieldname = columnName.toUpperCase();
        Integer headerIdx = (Integer) headerIndexesByColumnName.get(ciFieldname);
        if (headerIdx == null) {
            throw new IllegalArgumentException("The header index for columnName'" + columnName + "' is unknown.");
        }

        return headerIdx;
    }

    /**
     * Set the data column index for the anchor column.
     * The data column index is 1-based.
     */
    protected void setDataColumnIndexForAnchorColumn(int dataColumnIndex) {
        m_dataColumnIndexForAnchorColumn = dataColumnIndex;
    }

    /**
     * Get the data column index for the anchor column.
     * The data column index is 1-based.
     */
    public int getDataColumnIndexForAnchorColumn() {
        return m_dataColumnIndexForAnchorColumn;
    }

    /**
     * Set the data column index for the column name.
     * The data column index is 1-based.
     */
    protected void setDataColumnIndex(String columnName, int i) {
        dataColumnIndexesByColumnName.put(columnName.toUpperCase(), Integer.valueOf(i));
    }

    /**
     * Get the data column index for the column name.
     * The data column index is 1-based.
     */
    public int getDataColumnIndex(String columnName) {
        Integer dataColumnIndex = ((Integer)dataColumnIndexesByColumnName.get(columnName.toUpperCase()));
        if (dataColumnIndex == null) {
            l.logp(Level.SEVERE, getClass().getName(), "getDataColumnIndex", "dataColumnIndexesByColumnName = " + dataColumnIndexesByColumnName);
            throw new IllegalArgumentException("The data column index for columnName'" + columnName.toUpperCase() + "' is unknown.");
        }

        return dataColumnIndex.intValue();
    }

    /**
     * Set the xml column index for the column name.
     * The xml column index is 1-based.
     */
    protected void setXmlColumnIndex(String columnName, int i) {
        xmlColumnIndexesByColumnName.put(columnName.toUpperCase(), Integer.valueOf(i));
    }

    /**
     * Get the xml column index for the column name.
     * The xml column index is 1-based.
     */
    public int getXmlColumnIndex(String columnName) {
        Integer xmlColumnIndex = ((Integer)xmlColumnIndexesByColumnName.get(columnName.toUpperCase()));
        if (xmlColumnIndex == null) {
            l.logp(Level.SEVERE, getClass().getName(), "getXmlColumnIndex", "xmlColumnIndexesByColumnName = " + xmlColumnIndexesByColumnName);
            throw new IllegalArgumentException("The xml column index for columnName'" + columnName.toUpperCase() + "' is unknown.");
        }

        return xmlColumnIndex.intValue();
    }

    /**
     * Iterate through the data columns, ensuring all headers are mapped by the fieldname.
     * If the system is configured so the grid header defines the column order, reorder the grid headers
     * according the column sequence in the corresponding OasisFormField, if one is specified.
     * If two grid headers have the same column sequence, the order in the Grid Header XML takes precedence.
     */
    public void processDataColumns(BaseResultSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processDataColumns", new Object[]{rs});
        }
        if(useMapWithoutPrefixes())
            generateNewMapWithPrefixesRemoved();

        // If the column order is defined by the data result set, verify there are enough headers defined.
        if (!columnOrderIsDefinedByThisGridHeader() && header.size() < rs.getColumnCount()) {
            throw new IllegalArgumentException("The column order is defined by the Data Result Set, " +
                    "but the headers are not defined for all columns. There are " + (rs.getColumnCount() - header.size()) +
                    " missing headers.");
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "processDataColumns", "Headers before processing data columns: " + debugHeaders(new StringBuffer()).toString());
        }
        // If the column order is defined by the data columns, verify there are enough headers defined to handle the result set columns
        if (!columnOrderIsDefinedByThisGridHeader()) {
            if (header.size() < rs.getColumnCount()) {
                throw new ConfigurationException("The grid column order is defined by the data columns, and there are less Grid Headers defined than there are columns in the result set. There are " + (rs.getColumnCount()-header.size()) + " header(s) missing.");
            }
            else if (header.size() > rs.getColumnCount()) {
                l.warning("There are more headers defined than there are columns in the data result set. The last "+(header.size()-rs.getColumnCount())+" header(s) are extra.");
                while (header.size() > rs.getColumnCount()) {
                    header.remove(header.size()-1);
                }
            }
        }

        List dataColumnNames = new ArrayList(rs.getColumnCount());
        int updateonlyDropdownOffset = 0;    //TODO: Rename to reflect the new meaning
        for (int i = 1; i <= rs.getColumnCount(); i++) {

            String columnName = rs.getColumnName(i); // BaseResultSet.getColumnName() uses 1-based column indexes.
            dataColumnNames.add(columnName.toUpperCase());
            Map headerAttributes = null;

            setDataColumnIndex(columnName, i);
            setXmlColumnIndex(columnName, i - 1 + updateonlyDropdownOffset);

            boolean fieldDefinedInGridHeaderLayer = isFieldDefinedInGridHeaderLayer(columnName);
            if (!hasHeader(columnName)) {
                // There is no header mapped to this column name.
                if (columnOrderIsDefinedByThisGridHeader()) {
                    // If the column order is defined by the grid header, then no XML Header was defined for this column,
                    // so create a new header
                    headerAttributes = createHeaderForDataColumn(columnName, fieldDefinedInGridHeaderLayer);
                    // add it to the end of the header list and map it to the columnName
                    addHeader(columnName, headerAttributes);
                }
                else {
                    // The column order is defined by the result set,
                    // so map the header at the current column index to the columnName
                    headerAttributes = (Map) header.get(i - 1);
                    putHeader(columnName, headerAttributes, i);
                }
            }
            else {
                headerAttributes = getHeader(columnName);
            }

            int type = ((Integer)headerAttributes.get(CN_TYPE)).intValue();
            int display = ((Integer) headerAttributes.get(CN_DISPLAY)).intValue();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "processDataColumns", "!!!!!!!!!!!!!!!!!!!!!!!!!!DATA TYPE: " + type);
            }
            String name = ((String) headerAttributes.get(CN_NAME));
            if (type == TYPE_ANCHOR) {
                // Set the anchor data column index if this is the anchor field
                setDataColumnIndexForAnchorColumn(i);
            } else if (type == TYPE_UPDATEONLYDROPDOWN) {
                // Increment the data column index by one to account for the LOVLABEL
                updateonlyDropdownOffset++;
            } else if (type == TYPE_UPDATEONLY_MULTIPLE_DROPDOWN) {
                updateonlyDropdownOffset++;
            } else if (!FormatUtils.isDateFormatUS() && (type == TYPE_FORMATDATE ||
                    type == TYPE_DATE ||
                    type == TYPE_UPDATEONLYDATE ||
                    type == TYPE_FORMATDATETIME ||
                    type == TYPE_UPDATEONLYDATETIME)) {
                // Date Internationalization: Increment the data column index by one to account for extra-coulumn for dates
                updateonlyDropdownOffset++;
            } else if (type == TYPE_PHONE || type == TYPE_UPDATEONLYPHONE) {
                updateonlyDropdownOffset++;
            } else if (display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER &&(type == XMLGridHeader.TYPE_NUMBER || type == XMLGridHeader.TYPE_UPDATEONLYNUMBER)) {
                updateonlyDropdownOffset++;
            }

            // Set the order if it is defined by this grid header
            if (columnOrderIsDefinedByThisGridHeader()) {

                Integer order = null;
                if (((Integer)headerAttributes.get(CN_TYPE)).intValue() == TYPE_ANCHOR) {
                    order = c_minimumInteger;
                }
                else if (fieldDefinedInGridHeaderLayer) {
                    OasisFormField field = getOasisFormField(columnName);
                    if (field != null && !StringUtils.isBlank(field.getColNum())) {
                        // If the column order is defined by the grid header, and the form field has a column sequence set,
                        // overload the column order from the OasisFormField
                        order = Integer.valueOf(field.getColNum());
                    }
                }
                if (order == null) {
                    // Otherwise, use the order from the XMLGridHeader as the order.
                    order = getHeaderIndex(columnName);
                }
                addHeaderToOrderMap(headerAttributes, order);
            }
        }

        if (header.size() > rs.getColumnCount()) {
            // Warn that there are extra headers defined
            l.logp(Level.WARNING, getClass().getName(), "processDataColumns", "The following header(s) defined in XML will not be used since there are no corresponding data column(s): " + getExtraHeaderNames(dataColumnNames) + "; The following is a list of all data column names: " + dataColumnNames);
        }

        if (columnOrderIsDefinedByThisGridHeader()) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "processDataColumns", "Headers before reordering data columns: " + debugHeaders(new StringBuffer()).toString());
            }
            sortHeadersByDefinedGridHeaderOrder();
        }

        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "processDataColumns", "Headers after processing data columns: " + debugHeaders(new StringBuffer()).toString());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "processDataColumns");
        }
    }

    private String getExtraHeaderNames(List dataColumnNames) {
        StringBuffer buf = new StringBuffer();
        String sep = "";
        Iterator iter = header.iterator();
        while (iter.hasNext()) {
            Map headerAttributes = (Map) iter.next();
            boolean matchingDataColumnForHeader = false;
            for (int i = 0; i < dataColumnNames.size(); i++) {
                if (dataColumnNames.get(i).equals(headerAttributes.get(CN_DATACOLUMNNAME))) {
                    matchingDataColumnForHeader = true;
                    break;
                }
            }
            if (!matchingDataColumnForHeader) {
                buf.append(sep).append(headerAttributes.get(CN_DATACOLUMNNAME));
                sep = ", ";
            }
        }

        return buf.toString();
    }

    private StringBuffer debugHeaders(StringBuffer buf) {
        buf.append("GridHeaders[").append(header.size()).append("]:\n");
        Iterator headerAttributes = header.iterator();
        while (headerAttributes.hasNext()) {
            Map map = (Map) headerAttributes.next();
            buf.append(map).append("\n");
        }
        return buf;
    }

    private StringBuffer debugHeaders(StringBuffer buf, String attributeName) {
        buf.append("Headers:\n");
        Iterator headerAttributes = header.iterator();
        while (headerAttributes.hasNext()) {
            Map map = (Map) headerAttributes.next();
            buf.append(map.get(CN_NAME)).append(": ").append(attributeName).append("='").append(map.get(attributeName)).append("'\n");
        }
        return buf;
    }

    protected Map createHeaderForDataColumn(String columnName, boolean fieldDefinedInGridHeaderLayer) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "createHeaderForDataColumn", new Object[]{columnName, String.valueOf(fieldDefinedInGridHeaderLayer)});
        }

        // Default the attributes from the field
        Map map = new HashMap();
        map.put(CN_NAME, columnName);
        map.put(CN_TYPE, c_defaultType);
        map.put(CN_LENGTH, null);
        map.put(CN_DISPLAY, c_defaultDisplay);
        map.put(CN_ALIGN, null);
        map.put(CN_VISIBLE, c_defaultVisibility);
        map.put(CN_LISTDATA, null);
        map.put(CN_HREF, null);
        map.put(CN_HREFKEY, null);
        map.put(CN_FIELD_HREF, null);
        map.put(CN_MAXLENGTH, null);
        map.put(CN_ROWS, null);
        map.put(CN_COLS, null);
        map.put(CN_TITLE, null);
        map.put(CN_DECIMALPLACES, null);
        map.put(CN_PROTECTED, c_defaultProtected);
        map.put(CN_MASKED, c_defaultMasked);
        map.put(CN_STYLE, null);
        map.put(COL_WIDTH,null);
        map.put(COL_MIN_WIDTH,null);
        map.put(COL_AGGREGATE, null);

        // Override the defaults with values from the OasisFormField if it is defined in a Grid Header layer
        if (fieldDefinedInGridHeaderLayer) {
            OasisFormField field = getOasisFormField(columnName);
            OasisFormField detailField = (OasisFormField) getFields().get(columnName);
            int display = decodeDisplayType(field);
            int type = postProcessType(decodeType(field), display, field.getIsReadOnly() || !field.getIsVisible());
            map.put(COL_AGGREGATE, field.getColAggregate());
            map.put(COL_WIDTH, field.getColWidth());
            map.put(COL_MIN_WIDTH, field.getColMinWidth());
            map.put(CN_NAME, field.getLabel());
            map.put(CN_LENGTH, field.getCols());
            map.put(CN_VISIBLE, YesNoFlag.getInstance(field.getIsVisible()).getName());
            map.put(CN_FIELDNAME, field.getFieldId());
            map.put(CN_TYPE, Integer.valueOf(type));
            map.put(CN_DISPLAY, Integer.valueOf(display));
            map.put(CN_ALIGN, field.getAlignment());
            map.put(CN_MAXLENGTH, field.getMaxLength());
            map.put(CN_ROWS, field.getRows());
            map.put(CN_COLS, field.getCols());
            map.put(CN_TITLE, field.getLabel());
            map.put(CN_FIELDID, field.getFieldId()); // set the fieldid so we use the LOV SQL from the form field
            map.put(CN_PROTECTED, Boolean.valueOf(field.getIsProtected()));
            map.put(CN_MASKED, Boolean.valueOf(field.getIsMasked()));
            map.put(CN_STYLE, field.getStyleInlineForCell());
            map.put(CN_FIELD_HREF, field.getHref());
            map.put(CN_PATTERN, field.getFormatPattern());
            if (detailField != null) {
                int detailDisplay = decodeDisplayType(detailField);
                if (type != TYPE_DROPDOWN && type != TYPE_MULTIPLE_DROPDOWN && getFields().get(columnName) != null) {
                    int detailFieldType = decodeType(detailField);
                    if (detailFieldType == TYPE_DROPDOWN) {
                        map.put(CN_TYPE, Integer.valueOf(TYPE_UPDATEONLYDROPDOWN));
                        map.put(CN_DETAIL_FIELDID, detailField.getFieldId());
                    } else if (detailFieldType == TYPE_MULTIPLE_DROPDOWN) {
                        map.put(CN_TYPE, Integer.valueOf(TYPE_UPDATEONLY_MULTIPLE_DROPDOWN));
                        map.put(CN_DETAIL_FIELDID, detailField.getFieldId());
                    }
                }
                if (detailDisplay == DISPLAY_FORMATTED_NUMBER) {
                    map.put(CN_DISPLAY, Integer.valueOf(detailDisplay));
                    map.put(CN_PATTERN, detailField.getFormatPattern());
                }
            }
        } else if (isFieldDefinedOnPage(columnName)) {
            // If there is a page field defined for this column, and it is a SELECT list, set the type to UPDATEONLYDROPDOWN
            OasisFormField field = getOasisFormField(columnName);
            int type = decodeType(field);
            int display = decodeDisplayType(field);
            if (type == TYPE_DROPDOWN && OasisTagHelper.displayReadonlyCodeLookupAsLabel()) {
                map.put(CN_TYPE, Integer.valueOf(TYPE_UPDATEONLYDROPDOWN));
                map.put(CN_FIELDID, field.getFieldId()); // set the fieldid so we use the LOV SQL from the form field
            }else if(type == TYPE_MULTIPLE_DROPDOWN && OasisTagHelper.displayReadonlyCodeLookupAsLabel()){
                map.put(CN_TYPE, Integer.valueOf(TYPE_UPDATEONLY_MULTIPLE_DROPDOWN));
                map.put(CN_FIELDID, field.getFieldId()); // set the fieldid so we use the LOV SQL from the form field
            }else if (type == TYPE_DATE || type == TYPE_FORMATDATE){
                type = postProcessType(decodeType(field), DISPLAY_DEFAULT, true);
                map.put(CN_TYPE, Integer.valueOf(type));
                map.put(CN_FIELDID, field.getFieldId());
            } else if((type == TYPE_NUMBER || type == TYPE_UPDATEONLYNUMBER) && display == DISPLAY_FORMATTED_NUMBER) {
                map.put(CN_TYPE, Integer.valueOf(type));
                map.put(CN_DISPLAY, Integer.valueOf(display));
                map.put(CN_FIELDID, field.getFieldId());
            }else if (type == TYPE_FORMATMONEY || type == TYPE_UPDATEONLYMONEY) {
                map.put(CN_TYPE, Integer.valueOf(type));
            } else if (type == TYPE_PERCENTAGE || type == TYPE_UPDATEONLYPERCENTAGE) {
                map.put(CN_TYPE, Integer.valueOf(type));
            }
            map.put(CN_STYLE, field.getStyleInlineForCell());
            map.put(CN_MASKED, Boolean.valueOf(field.getIsMasked()));
            map.put(CN_PATTERN, field.getFormatPattern());
        } else {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "createHeaderForDataColumn", "" + columnName + " - Field NOT Defined on Page or Grid Header Layer");
            }
        }

        // Set the type as anchor if the anchorColumnName is set, and it matches this columnName
        if (columnName.equalsIgnoreCase(getAnchorColumnName())) {
            map.put(CN_TYPE, c_typeAnchor);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "createHeaderForDataColumn", map);
        }
        return map;
    }

    private int decodeType(OasisFormField field) {
        int type = TYPE_DEFAULT;
        if (c_typeMap.containsKey(field.getDisplayType())) {
            String fieldDisplayType = field.getDisplayType();
            if (fieldDisplayType != null) {
                type = ((Integer)c_typeMap.get(fieldDisplayType)).intValue();
            }
        }

        if (type == TYPE_DROPDOWN && StringUtils.isBlank(field.getLovSql())) {
            type = TYPE_DEFAULT;
        } else if(type == TYPE_MULTIPLE_DROPDOWN && StringUtils.isBlank(field.getLovSql())){
            type = TYPE_DEFAULT;
        } else if (type == TYPE_TEXT) {
            // If the field display type is TEXT, then check the field datatype to see if a special TYPE is required.
            // For example, DATE, NUMBER, CURRENCY
            String fieldDataType = field.getDatatype();
            if (fieldDataType != null) {
                type = ((Integer)c_typeMap.get(fieldDataType)).intValue();
            }
        }
        //if href is not null, set type to UPDATEONLYURL
        if (!StringUtils.isBlank(field.getHref()) && (type != TYPE_DROPDOWN && type != TYPE_MULTIPLE_DROPDOWN)) {
            type = TYPE_UPDATEONLYURL;
        }

        if (field.getDisplayType()!=null && field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER)
                && OasisFields.TYPE_NUMBER.equals(field.getDatatype())){
            if(field.getIsReadOnly())
                type = TYPE_UPDATEONLYNUMBER;
            else
                type = TYPE_NUMBER;
        }

        return type;
    }

    private int decodeDisplayType(OasisFormField field) {
        // handle money type
        if (field != null && field.getDatatype() != null) {
            if (field.getDatatype().equals(OasisFields.TYPE_CURRENCY_FORMATTED) ||
                    field.getDatatype().equals(OasisFields.TYPE_CURRENCY)) {
                return DISPLAY_MONEY;
            }
        }

        if (field != null && OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER.equals(field.getDisplayType())
                && OasisFields.TYPE_NUMBER.equals(field.getDatatype())){
            return DISPLAY_FORMATTED_NUMBER;
        }

        if (field != null && (OasisFields.DISPLAY_TYPE_SELECT.equals(field.getDisplayType()) ||
                OasisFields.DISPLAY_TYPE_MULTISELECT.equals(field.getDisplayType()) ||
                OasisFields.DISPLAY_TYPE_MULTISELECTPOPUP.equals(field.getDisplayType()))){
            return DISPLAY_SELECT;
        }

        return DISPLAY_DEFAULT;
    }

    /**
     * Return true if there is a field defined on the Page for the given data column name.
     */
    protected boolean isFieldDefinedOnPage(String columnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isFieldDefinedOnPage", new Object[]{columnName});
        }
        boolean defined = false;

        if(isFieldDefinedOnPage(columnName, getFields())) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isFieldDefinedOnPage", columnName + " FOUND IN ORIGINAL MAP");
            }
            defined = true;
        } else if(useMapWithoutPrefixes() && isFieldDefinedOnPage(columnName.toLowerCase(), m_mapWithoutPrefixes)){
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isFieldDefinedOnPage", columnName + " FOUND IN DUPLICATE MAP");
            }
            defined = true;
        } else {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "isFieldDefinedOnPage", columnName + " NOT FOUND");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isFieldDefinedOnPage", Boolean.valueOf(defined));
        }
        return defined;
    }

    private boolean isFieldDefinedOnPage(String columnName, Map map) {
        return (map.get(columnName) != null);
    }

    public boolean useMapWithoutPrefixes() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "useMapWithoutPrefixes");
        }
        boolean isEmpty = this.getGenerateMapWithoutPrefixes() && getFields() != null;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateNewMapWithPrefixesRemoved", isEmpty);
        }
        return isEmpty;
    }

    private void generateNewMapWithPrefixesRemoved() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "generateNewMapWithPrefixesRemoved");
        }

        Iterator iterator = getFields().keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String)iterator.next();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "generateNewMapWithPrefixesRemoved", "key = " + key + " Value: " + fields.get(key));
            }

            if(key.endsWith(getGridHeaderFieldnameSuffix())) {
                continue;
            }
            if(!key.contains("_")) {
                continue;
            }

            if(key.toLowerCase().contains("layer")) {
                continue;
            }

            String newKey = key.substring(key.indexOf("_")+1).toLowerCase();
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "generateNewMapWithPrefixesRemoved", "newKey = " + newKey);
            }

            if(m_mapWithoutPrefixes.containsKey(newKey)) {
                l.logp(Level.WARNING, getClass().getName(), "isFieldDefinedOnPage", "Attempted to remove Field Prefix from Column "+key
                        +" However, an abbreviated key with the same value("+newKey+") already exists. The danger of confusing two different fields exists.");
                continue;
            }

            m_mapWithoutPrefixes.put(newKey, fields.get(key));
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "generateNewMapWithPrefixesRemoved", "ADDED NEW KEY(\" + newKey + \") TO NEW MAP");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "generateNewMapWithPrefixesRemoved");
        }
    }

    /**
     * Return true if there is a field defined on a Grid Header Layer for the given data column name,
     * using the gridHeaderFieldnameSuffix as the suffix for the columnName
     * to find the corresponding OasisFormField.
     */
    protected boolean isFieldDefinedInGridHeaderLayer(String columnName) {
        Object field = null;
        // Look for the OasisFormField called columnName + gridHeaderSuffix if there is a suffix.
        if (hasGridHeaderFieldnameSuffix()) {
            Map fields = getLayerFields() != null ? getLayerFields() : getFields();
            field = fields.get(columnName + getGridHeaderFieldnameSuffix());
        }
        return field != null;
    }

    /**
     * Get the OasisFormField for the given columnName, if one exists.
     * If there is a grid header fieldname suffix defined, the field named "columnName + gridHeaderSuffix" searched for first.
     * If there is not suffix defined, or the corresponding field does not exist, the field matching the columnName is searched for.
     * The search is case-insensitive.
     * If there is no field for the given columnName, null is returned.
     */
    protected OasisFormField getOasisFormField(String columnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOasisFormField", new Object[]{columnName});
        }

        OasisFormField field = null;
        // Look for the OasisFormField called columnName + gridHeaderSuffix if there is a suffix.
        if (hasGridHeaderFieldnameSuffix()) {
            if (getLayerFields() != null) {
                // Check in the layer fields first if they are defined
                field = (OasisFormField) getLayerFields().get(columnName + getGridHeaderFieldnameSuffix());
            }
            if (field == null) {
                // If the field is not found in the layer fields, check the entire set of page and layer fields
                field = (OasisFormField) getFields().get(columnName + getGridHeaderFieldnameSuffix());
            }
        }
        // If no OasisFormField is found for the columnName + gridHeaderFieldnameSuffix,
        // get the one for the columnName
        if (field == null) {
            field = (OasisFormField) getFields().get(columnName);
        }

        //If still not found, try the Map with Prefixes Removed
        if (field == null && m_mapWithoutPrefixes != null && m_mapWithoutPrefixes.size()>0) {
            field = (OasisFormField) m_mapWithoutPrefixes.get(columnName.toLowerCase());
        }

        if (field == null) {
            StringBuffer msg = new StringBuffer("OasisFormField with fieldId: ").
                    append(columnName).append(" not found in OasisFields. ").
                    append("Did you spell the fieldid correctly in your grid xml?");
            String sMsg = msg.toString();
            throw new IllegalArgumentException(sMsg);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOasisFormField", field);
        }
        return field;
    }

    protected void addHeaderToOrderMap(Map headerAttributes, Integer order) {
        boolean isVisible = YesNoFlag.getInstance((String) headerAttributes.get(CN_VISIBLE)).booleanValue();
        if (((Integer)headerAttributes.get(CN_TYPE)).intValue() == TYPE_ANCHOR || isVisible) {
            // Add anchor header to visible so that it is written 1st in the table.
            addHeaderToOrderMap(headerAttributes, order, m_visibleHeaderOrderMap);
        }
        else {
            addHeaderToOrderMap(headerAttributes, order, m_hiddenHeaderOrderMap);
        }
    }
    protected void addHeaderToOrderMap(Map headerAttributes, Integer order, TreeMap headerOrderMap) {
        Object obj = headerOrderMap.get(order);
        if (obj == null) {
            obj = headerAttributes;
        }
        else {
            List list = null;
            if (obj instanceof List) {
                list = (List) obj;
            }
            else {
                list = new ArrayList();
                list.add(obj);
            }
            list.add(headerAttributes);
            obj = list;
        }
        headerOrderMap.put(order, obj);
    }

    protected void sortHeadersByDefinedGridHeaderOrder() {
        ArrayList newHeaderList = new ArrayList();

        sortHeadersByDefinedGridHeaderOrder(newHeaderList, m_visibleHeaderOrderMap);
        sortHeadersByDefinedGridHeaderOrder(newHeaderList, m_hiddenHeaderOrderMap);

        header = newHeaderList;
    }
    protected void sortHeadersByDefinedGridHeaderOrder(ArrayList newHeaderList, TreeMap headerOrderMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "sortHeadersByDefinedGridHeaderOrder", new Object[]{headerOrderMap,});
        }

        Iterator iter = headerOrderMap.values().iterator();
        while (iter.hasNext()) {
            Object value = iter.next();
            if (value instanceof List) {
                List currentHeaderList = (List) value;
                for (int i = 0; i < currentHeaderList.size(); i++) {
                    Map headerAttributes = (Map) currentHeaderList.get(i);
                    newHeaderList.add(headerAttributes);
                    if (headerAttributes.containsKey(CN_DATACOLUMNNAME)) {
                        //  Update the header index for the header
                        putHeader((String) headerAttributes.get(CN_DATACOLUMNNAME), headerAttributes, newHeaderList.size());
                    }
                    else {
                        l.logp(Level.WARNING, getClass().getName(), "sortHeadersByDefinedGridHeaderOrder", "There is no data column in the result set associated with the grid header: " + headerAttributes);
                    }
                }
            }
            else {
                Map headerAttributes = (Map) value;
                newHeaderList.add(headerAttributes);
                if (headerAttributes.containsKey(CN_DATACOLUMNNAME)) {
                    //  Update the header index for the header
                    putHeader((String) headerAttributes.get(CN_DATACOLUMNNAME), headerAttributes, newHeaderList.size());
                }
                else {
                    l.logp(Level.WARNING, getClass().getName(), "sortHeadersByDefinedGridHeaderOrder", "There is no data column in the result set associated with the grid header: " + headerAttributes);
                }
            }
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "sortHeadersByDefinedGridHeaderOrder", "Reordering the header list from header: " + header + " \nto newHeaderList: " + newHeaderList);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "sortHeadersByDefinedGridHeaderOrder");
        }
    }

    /**
     * has grid header layer id
     * @return
     */
    public boolean hadGridHeaderLayerId() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hadGridHeaderLayerId");
        }
        boolean hadGridHeaderLayerId = false;
        if (!StringUtils.isBlank(gridHeaderLayerId)) {
            hadGridHeaderLayerId = true;
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "hadGridHeaderLayerId", Boolean.valueOf(hadGridHeaderLayerId));
        }
        return hadGridHeaderLayerId;
    }

    /**
     * @return int
     */
    public int size() {
        return header.size();
    }

    /**
     * Determines whether this object has been initialized and
     * grid can be built based on it.
     *
     * @return Is it initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Keeps track of if all defined headers have the fieldname attribute set.
     */
    public boolean isFieldnameSpecifiedForAllHeaders() {
        return isFieldnameSpecifiedForAllHeaders;
    }

    /**
     * Determines if the column order for THIS grid is defined by the Grid Header.
     */
    public boolean columnOrderIsDefinedByThisGridHeader() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "columnOrderIsDefinedByThisGridHeader");
        }

        boolean columnOrderIsDefinedByThisGridheader =
                gridHeaderDefinesDisplayableColumnOrder() && isFieldnameSpecifiedForAllHeaders;

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "columnOrderIsDefinedByThisGridHeader", String.valueOf(columnOrderIsDefinedByThisGridheader));
        }
        return columnOrderIsDefinedByThisGridheader;
    }

    public void setGridHeaderDefinesDisplayableColumnOrder(boolean gridHeaderDefinesDisplayableColumnOrder) {
        m_gridHeaderDefinesDisplayableColumnOrder = Boolean.valueOf(gridHeaderDefinesDisplayableColumnOrder);
    }

    protected boolean gridHeaderDefinesDisplayableColumnOrder() {
        if (m_gridHeaderDefinesDisplayableColumnOrder == null) {
            m_gridHeaderDefinesDisplayableColumnOrder = Boolean.valueOf(GridHelper.gridHeaderDefinesDisplayableColumnOrder());
        }
        return m_gridHeaderDefinesDisplayableColumnOrder.booleanValue();
    }

    public boolean hasGridHeaderFieldnameSuffix() {
        return !StringUtils.isBlank(getGridHeaderFieldnameSuffix());
    }

    public String getGridHeaderFieldnameSuffix() {
        if (gridHeaderFieldnameSuffix == null) {
            gridHeaderFieldnameSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
        }
        return gridHeaderFieldnameSuffix;
    }

    public void setGridHeaderFieldnameSuffix(String gridHeaderFieldnameSuffix) {
        if (gridHeaderFieldnameSuffix != null) {
            this.gridHeaderFieldnameSuffix = gridHeaderFieldnameSuffix.toUpperCase();
        }
    }

    /**
     * Return value of entry
     *
     * @param i    column index
     * @return Object
     */
    public HashMap getHeaderMap(int i) {
        return (HashMap) header.get(i - 1);
    }

    /**
     * Put an entry into the underlying HashMap
     *
     * @param i     column index
     * @param parm  key to entry
     * @param value value to replace
     */
    public void put(int i, Object parm, Object value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "put", new Object[]{Integer.valueOf(i), parm, value});
        }
        ((HashMap) header.get(i - 1)).put(parm, value);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "put");
        }
    }

    /**
     * Empty default constructor
     */
    public XMLGridHeader() {

    }

    /**
     * Generate a mapping between the updateable columns and the resultset from
     * which they came.
     *
     * @return An XMLGridUpdateMap.
     */
    public XMLGridUpdateMap getUpdateMap() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getUpdateMap");
        }
        int sz = header.size();
        XMLGridUpdateMap map = new XMLGridUpdateMap();
        for (int i = 0; i < sz; i++) {
            int type = ((Integer) ((HashMap) header.get(i)).get(CN_TYPE)).intValue();
            switch (type) {
                case TYPE_TEXT:
                case TYPE_UPPERCASE_TEXT:
                case TYPE_LOWERCASE_TEXT:
                case TYPE_NUMBER:
                case TYPE_DROPDOWN:
                case TYPE_UPDATEONLYDROPDOWN:
                case TYPE_MULTIPLE_DROPDOWN:
                case TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                case TYPE_UPDATEONLY:
                case TYPE_DATE:
                case TYPE_CHECKBOX:
                case TYPE_RADIOBUTTON:
                case TYPE_TEXTAREA:
                case TYPE_UPDATEONLYDATE:
                case TYPE_UPDATEONLYDATETIME:
                case TYPE_UPDATEONLYMONEY:
                case TYPE_UPDATEONLYNUMBER:
                case TYPE_UPDATEONLYURL:
                case TYPE_PHONE:
                case TYPE_UPDATEONLYPHONE:
                    map.addColumn(i + 1);
                    break;
                case TYPE_ANCHOR:
                    map.setIdColumn(i + 1);
                    break;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getUpdateMap", map);
        }
        return map;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("XMLGridHeader");
        buf.append("{# of cols=").append(header.size());
        buf.append(", header=").append(header);
        buf.append(",isInitialized=").append(isInitialized);
        buf.append('}');
        return buf.toString();
    }

    /**
     * get grid header layer id
     * @return
     */
    public String getGridHeaderLayerId() {
        if (StringUtils.isBlank(gridHeaderLayerId)) {
            throw new IllegalStateException("Grid Header Layer Id is blank");
        }
        return gridHeaderLayerId;
    }

    public void setGridHeaderLayerId(String gridHeaderLayerId) {
        this.gridHeaderLayerId = gridHeaderLayerId;
    }

    public boolean getGenerateMapWithoutPrefixes() {
        return m_generateMapWithoutPrefixes;
    }

    public void setGenerateMapWithoutPrefixes(boolean generateMapWithoutPrefixes) {

        m_generateMapWithoutPrefixes = generateMapWithoutPrefixes;
    }

    public Map getHeaderIndexesByColumnName(){
        return headerIndexesByColumnName;
    }

    private Map m_mapWithoutPrefixes = new HashMap();

    private static Integer c_defaultType = Integer.valueOf(TYPE_DEFAULT);
    private static Integer c_typeAnchor = Integer.valueOf(TYPE_ANCHOR);
    private static Integer c_defaultDisplay = Integer.valueOf(DISPLAY_DEFAULT);
    private static String c_defaultVisibility = "N";
    private static Boolean c_defaultProtected = Boolean.valueOf(false);
    private static Boolean c_defaultMasked = Boolean.valueOf(false);
    private static Integer c_minimumInteger = Integer.valueOf(Integer.MIN_VALUE);
    private boolean m_generateMapWithoutPrefixes =  true;
    private final Logger l = LogUtils.getLogger(getClass());
}
