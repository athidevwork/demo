package dti.oasis.tags;

import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JavaBean that encapsulates characteristics of
 * an OASIS field.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 12/15/2003   jbe     Add rowNum properties & mod constructor
*                      Add colNum property
* 2/7/2004     jbe     Add logging & toString
* 4/5/2005     jbe     Add isProtected and javadocs.  New constructor
* 01/23/2007   lmm     Added ajax url property;
* 01/23/2007   wer     Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 01/31/2007   wer     Enhanced to support defining Grid Column Order by the Grid Header
* 05/01/2007   GCC     Added properties styeInline, fieldColspan,
 *                     emptyCellsAfterFld, emptyCellsBeforeFld, alignment,
 *                     and taborder.
* 09/28/2007   sxm     Added property tooltip
* 01/02/2007   James   Web Field dependency.
* 03/03/2008   James   Issue#79614 eClaims architectural enhancement to
*                      take advantage of ePolicy architecture
*                      Handling HREF is a new enhancement in WebWB
* 10/09/2009   fcb     96764: added logic for masked fields.
* 04/19/2010   James   Issue#105817 Migration of pf_web_page_field to pf_web_layer_field
* 09/20/2011   mxg         Issue #100716: Display Type FORMATTEDNUMBER: Added Format Pattern
* 04/04/2018  cesar    #191962 - added col_width and min_col_width
* 05/15/2018  cesar    #192983 - add useXssFilter
* 08/06/2018  dpang    #194641 - added col_aggregate
* -----------------------------------------------------------------------------
*/
public class OasisFormField implements Serializable, Cloneable {

    public static final String DEFAULT_DISPLAY_TYPE = "TEXT";

    private String fieldId;

    private String label;

    private String cols;

    private String maxLength;

    private boolean isVisible = false;

    private boolean isRequired;

    private String defaultValue;

    private String formatPattern;

    private String style;

    private boolean isReadOnly;

    private String displayType = DEFAULT_DISPLAY_TYPE;

    private String rows;

    private String lovSql;

    private List lovList;

    private String datatype;

    private String rowNum;

    private String colNum;

    private boolean isProtected;

    private String ajaxURL;

    private int ajaxCount;

    private String styleInlineForCell;

    private String fieldColSpan;

    private String emptyCellsAfterFld;

    private String emptyCellsBeforeFld;

    private String alignment;

    private String taborder;

    private String tooltip;

    private String fieldDependency;

    private String href;

    private boolean isMasked;

    private String colWidth;

    private String colMinWidth;

    private String useXssFilter = "";

    private String colAggregate;

    public static final String DISPLAY_TYPE_TEXT = "TEXT";
    public static final String DISPLAY_TYPE_TEXTAREA = "TEXTAREA";
    public static final String DISPLAY_TYPE_SELECT = "SELECT";
    public static final String DISPLAY_TYPE_RADIOBUTTON = "RADIOBUTTON";
    public static final String DISPLAY_TYPE_MULTISELECT = "MULTISELECT";
    public static final String DISPLAY_TYPE_MULTIBOX = "MULTIBOX";
    public static final String DISPLAY_TYPE_MULTISELECTPOPUP = "MULTISELECTPOPUP";

    public static final String DISPLAY_TYPE_FINDER_TEXT = "FINDERTEXT";
    public static final String DISPLAY_TYPE_EMAIL_TEXT = "EMAILTEXT";
    public static final String DISPLAY_TYPE_NOTE_TEXT = "NOTETEXT";
    public static final String DISPLAY_TYPE_CHECKBOX = "CHECKBOX";
    public static final String DISPLAY_TYPE_TEXTAREA_POPUP = "TEXTAREAPOPUP";
    public static final String DISPLAY_TYPE_FORMATTEDNUMBER = "FORMATTEDNUMBER";

    public static final Map<String, String> lovDisplayType = new HashMap<String, String>();
    static {
        lovDisplayType.put(DISPLAY_TYPE_MULTISELECT,DISPLAY_TYPE_MULTISELECT);
        lovDisplayType.put(DISPLAY_TYPE_MULTIBOX,DISPLAY_TYPE_MULTIBOX);
        lovDisplayType.put(DISPLAY_TYPE_RADIOBUTTON,DISPLAY_TYPE_RADIOBUTTON);
        lovDisplayType.put(DISPLAY_TYPE_SELECT,DISPLAY_TYPE_SELECT);
        lovDisplayType.put(DISPLAY_TYPE_MULTISELECTPOPUP,DISPLAY_TYPE_MULTISELECTPOPUP);
        lovDisplayType.put(DISPLAY_TYPE_CHECKBOX,DISPLAY_TYPE_CHECKBOX);
    }

    private static Boolean isLovDisplayType(String displayType){
        boolean checked = lovDisplayType.containsKey(displayType);
        return checked;
    }
    /**
     * Getter of # rows in multilineedit or listbox.
     * @return nbr_rows
     */
    public String getRows() {
        return rows;
    }

    /**
     * Setter of # rows in multilineedit or listbox.
     * @param rows nbr_rows
     */
    public void setRows(String rows) {
        this.rows = rows;
    }

    /**
     * Getter of # cols in multilineedit or textbox.
     * @return nbr_cols
     */
    public String getCols() {
        return cols;
    }

    /**
     * Setter of # cols in multilineedit or textbox.
     * @param cols nbr_cols
     */
    public void setCols(String cols) {
        this.cols = cols;
    }

    /**
     * Getter of max # chars allows in textbox.
     * @return maxlength
     */
    public String getMaxLength() {
        return maxLength;
    }

    /**
     * Setter of max # chars allowd in textbox.
     * @param maxLength maxlength
     */
    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Noarg constructor
     */
    public OasisFormField() {
    }

    /**
     * Getter of display type. e.g. SELECT, TEXT, TEXTAREA, RADIOBUTTON, CHECKBOX, MULTIBOX
     * @return display_type
     */
    public String getDisplayType() {
        return displayType;
    }

    /**
     * Setter of display type. e.g. SELECT, TEXT, TEXTAREA, RADIOBUTTON, CHECKBOX, MULTIBOX
     * @param displayType display_type
     */
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    /**
     * Getter of row # used to sort the retrieval and display of fields in a table layout.
     * @return row_num
     */
    public String getRowNum() {
        return rowNum;
    }

    /**
     * Setter of row # used to sort the retrieval and display of fields in a table layout.
     * @param rowNum row_num
     */
    public void setRowNum(String rowNum) {
        this.rowNum = (rowNum==null) ? "0" : rowNum;
    }

    /**
     * Getter of col # used to sort the retrieval and display of fields in a table layout.
     * @return sort_order
     */
    public String getColNum() {
        return colNum;
    }

    /**
     * Setter of col # used to sort the retrieval and display of fields in a table layout.
     * @param colNum sort_order
     */
    public void setColNum(String colNum) {
        this.colNum = (colNum==null) ? "0" : colNum;
    }

    /**
     * Getter of whether a field is protected from being sent to the browser.  Derived based on
     * security_b and pfprof.
     * @return derived
     */
    public boolean getIsProtected() {
        return isProtected;
    }

    /**
     * Setter of whether a field is protected from being sent to the browser.  Derived based on
     * security_b and pfprof.
     * @param aProtected derived
     */
    public void setIsProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    /**
     * Constructor
     * @param fieldId field_id
     * @param label label
     * @param isVisible visible_b
     * @param isRequired required_b
     * @param defaultValue default_value
     * @param style css_class
     * @param isReadOnly read_only_b
     */
    public OasisFormField(String fieldId, String label, boolean isVisible,
                          boolean isRequired, String defaultValue, String style, boolean isReadOnly) {
        Logger l = LogUtils.enterLog(getClass(), "constructor",
                new Object[]{fieldId, label, String.valueOf(isVisible), String.valueOf(isRequired),
                             defaultValue, style, String.valueOf(isReadOnly)});
        this.fieldId = fieldId;
        this.label = label;
        this.isVisible = isVisible;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.style = style;
        this.isReadOnly = isReadOnly;
        l.exiting(getClass().getName(), "constructor", toString());

    }

    /**
     * Constructor
     * @param fieldId field_id
     * @param label label
     * @param isVisible visible_b
     * @param isRequired required_b
     * @param defaultValue default_value
     * @param style css_class
     * @param isReadOnly read_only_b
     * @param displayType display_type
     * @param rows nbr_rows
     * @param cols nbr_cols
     * @param maxLength maxlength
     * @param lovSql lov_sql
     * @param datatype datatype
     * @param rowNum row_num
     * @param colNum sort_order
     * @param tooltip tooltip
     * @param fieldDependency field Dependency
     * @param href href
     * @param formatPattern format_pattern
     */
    public OasisFormField(String fieldId, String label, boolean isVisible,
                          boolean isRequired, String defaultValue, String style, boolean isReadOnly,
                          String displayType, String rows, String cols, String maxLength, String lovSql,
                          String datatype, String rowNum, String colNum, String tooltip, String fieldDependency,
                          String href, String formatPattern) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]
        {fieldId, label, String.valueOf(isVisible), String.valueOf(isRequired), defaultValue,
         style, String.valueOf(isReadOnly), displayType, rows, cols, maxLength, lovSql,
         datatype, rowNum, colNum, tooltip});
        this.fieldId = fieldId;
        this.label = label;
        this.isVisible = isVisible;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.style = style;
        this.isReadOnly = isReadOnly;
        this.displayType = displayType;
        this.rows = rows;
        this.cols = cols;
        this.maxLength = maxLength;
        if(isLovDisplayType(displayType))
            this.lovSql = lovSql;
        this.datatype = datatype;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.tooltip = tooltip;
        this.fieldDependency = fieldDependency;
        this.href = href;
        this.formatPattern = formatPattern;
        l.exiting(getClass().getName(), "constructor", toString());
    }

    /**
     * Constructor
     * @param fieldId field_id
     * @param label label
     * @param isVisible visible_b
     * @param isRequired required_b
     * @param defaultValue default_value
     * @param style css_class
     * @param isReadOnly read_only_b
     * @param displayType display_type
     * @param rows nbr_rows
     * @param cols nbr_cols
     * @param maxLength maxlength
     * @param lovSql lov_sql
     * @param datatype datatype
     * @param rowNum row_num
     * @param colNum sort_order
     * @param isProtected derived
     * @param tooltip tooltip
     * @param fieldDependency field Dependency
     * @param href href
     * @param formatPattern format_pattern
     */
    public OasisFormField(String fieldId, String label, boolean isVisible,
                          boolean isRequired, String defaultValue, String style, boolean isReadOnly,
                          String displayType, String rows, String cols, String maxLength, String lovSql,
                          String datatype, String rowNum, String colNum, boolean isProtected, String tooltip,
                          String fieldDependency, String href, boolean isMasked, String formatPattern) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]
        {fieldId, label, String.valueOf(isVisible), String.valueOf(isRequired), defaultValue,
         style, String.valueOf(isReadOnly), displayType, rows, cols, maxLength, lovSql,
         datatype, rowNum, colNum, String.valueOf(isProtected), tooltip});
        this.fieldId = fieldId;
        this.label = label;
        this.isVisible = isVisible;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.style = style;
        this.isReadOnly = isReadOnly;
        this.displayType = displayType;
        this.rows = rows;
        this.cols = cols;
        this.maxLength = maxLength;
        if(isLovDisplayType(displayType))
            this.lovSql = lovSql;
        this.datatype = datatype;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.isProtected = isProtected;
        this.tooltip = tooltip;
        this.fieldDependency = fieldDependency;
        this.href = href;
        this.isMasked = isMasked;
        this.formatPattern = formatPattern;
        l.exiting(getClass().getName(), "constructor", toString());
    }

    /**
     * Constructor
     * @param fieldId field_id
     * @param label label
     * @param isVisible visible_b
     * @param isRequired required_b
     * @param defaultValue default_value
     * @param style css_class
     * @param isReadOnly read_only_b
     * @param displayType display_type
     * @param rows nbr_rows
     * @param cols nbr_cols
     * @param maxLength maxlength
     * @param lovSql lov_sql
     * @param datatype datatype
     * @param rowNum row_num
     * @param colNum sort_order
     * @param isProtected derived
     * @param styleInlineForCell style
     * @param fieldColSpan colspan
     * @param emptyCellsAfterFld empty_cells_after_fld
     * @param emptyCellsBeforeFld empty_cells_before_fld
     * @param alignment alignment
     * @param taborder taborder
     * @param tooltip tooltip
     * @param fieldDependency field Dependency
     * @param href href
     * @param formatPattern format_pattern
     */
    public OasisFormField(String fieldId, String label, boolean isVisible,
                          boolean isRequired, String defaultValue, String style, boolean isReadOnly,
                          String displayType, String rows, String cols, String maxLength, String lovSql,
                          String datatype, String rowNum, String colNum, boolean isProtected,
                          String styleInlineForCell, String fieldColSpan, String emptyCellsAfterFld,
                          String emptyCellsBeforeFld, String alignment, String taborder, String tooltip,
                          String fieldDependency, String href, boolean isMasked, String formatPattern,
                          String fieldColWidth, String fieldColMinWidth, String fieldAggregate) {
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]
        {fieldId, label, String.valueOf(isVisible), String.valueOf(isRequired), defaultValue,
         style, String.valueOf(isReadOnly), displayType, rows, cols, maxLength, lovSql,
         datatype, rowNum, colNum, String.valueOf(isProtected), styleInlineForCell, fieldColSpan,
         emptyCellsAfterFld, emptyCellsBeforeFld, alignment, taborder, tooltip});
        this.fieldId = fieldId;
        this.label = label;
        this.isVisible = isVisible;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.style = style;
        this.isReadOnly = isReadOnly;
        this.displayType = displayType;
        this.rows = rows;
        this.cols = cols;
        this.maxLength = maxLength;
        if(isLovDisplayType(displayType))
            this.lovSql = lovSql;
        this.datatype = datatype;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.isProtected = isProtected;
        this.styleInlineForCell = styleInlineForCell;
        this.setFieldColSpan(fieldColSpan);
        this.setEmptyCellsAfterFld(emptyCellsAfterFld);
        this.setEmptyCellsBeforeFld(emptyCellsBeforeFld);
        this.alignment = alignment;
        this.taborder = taborder;
        this.tooltip = tooltip;
        this.fieldDependency = fieldDependency;
        this.href = href;
        this.isMasked = isMasked;
        this.formatPattern = formatPattern;
        this.colWidth = fieldColWidth;
        this.colMinWidth = fieldColMinWidth;
        this.colAggregate = fieldAggregate;

        l.exiting(getClass().getName(), "constructor", toString());
    }

    /**
     * Getter of unique alpha field identifier. For any field on a layer where the
     * layer_field_id is not provided, this will be pf_web_field.field_id.  For any field on a layer
     * where the layer_field_id is provided, this will be pf_web_layer_field.layer_field_id.
     * @return field_id
     */
    public String getFieldId() {
        return fieldId;
    }

    /**
     * Setter of unique alpha field identifier. For any field on a layer where the
     * layer_field_id is not provided, this will be pf_web_field.field_id.  For any field on a layer
     * where the layer_field_id is provided, this will be pf_web_layer_field.layer_field_id.
     * @param string field_id
     */
    public void setFieldId(String string) {
        fieldId = string;
    }

    /**
     * Getter for the label for the field.
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for the label for the field.
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for the field's default value.
     * @return default_value
     */
    public String getDefaultValue() {
        return (defaultValue == null) ? "" : defaultValue;
    }

    /**
     * Setter for the field's default value
     * @param defaultValue default_value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for the field's format pattern
     * @return format_pattern
     */
    public String getFormatPattern() {
        return formatPattern;
    }

    /**
     * Setter for the field's format pattern
     * @param formatPattern format_pattern
     */
    public void setFormatPattern(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    /**
     * Getter for the field's configured stylesheet.
     * @return css_class
     */
    public String getStyle() {
        return style;
    }

    /**
     * Setter for the field's configured stylesheet.
     * @param style css_class
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Getter for whether field is readonly.
     * @return read_only_b
     */
    public boolean getIsReadOnly() {
        return isReadOnly;
    }

    /**
     * Setter for whether the field is readonly.
     * @param isReadOnly read_only_b
     */
    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     * Getter for whether the field is required.
     * @return required_b
     */
    public boolean getIsRequired() {
        return isRequired;
    }

    /**
     * Set whether the field is required.
     * @param isRequired required_b
     */
    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * Get whether field the field is visible.
     * @return visible_b
     */
    public boolean getIsVisible() {
        return isVisible;
    }

    /**
     * Set the field's visibility.
     * @param isVisible visible_b
     */
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Get the list-of-values SQL/LIST.  This may be sql in the format: SELECT 1, 2 FROM table<br>
     * or it may be a configured list in the format: LIST,1,One,2,Two.
     * @return lov_sql
     */
    public String getLovSql() {
        return lovSql;
    }

    /**
     * Set the list-of-values SQL/LIST.  This may be sql in the format: SELECT 1, 2 FROM table<br>
     * or it may be a configured list in the format: LIST,1,One,2,Two.
     * @param lovSql lov_sql
     */
    public void setLovSql(String lovSql) {
        this.lovSql = lovSql;
    }

    /**
     * Get the field's datatype, e.g. NM - Numeric, DT - Date, CU - Currency
     * @return datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * Set the field's datatype, e.g. NM - Numeric, DT - Date, CU - Currency
     * @param datatype   Datatype for field
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * Get the field's AJAX url
     * @return AjaxURL
     */
    public String getAjaxURL() {
        return this.ajaxURL;
    }

    /**
     * Set the field's AJAX Url
     * @param ajaxURL   AJAX URL
     */
    public void setAjaxURL(String ajaxURL) {
        this.ajaxURL = ajaxURL;
    }

    public void appendAjaxURL(String ajaxURL){
        ajaxCount = (StringUtils.isBlank(String.valueOf(ajaxCount))? 1 : (ajaxCount + 1) );
        this.ajaxURL = (StringUtils.isBlank(this.ajaxURL)? "" : this.ajaxURL) + "URL[" + ajaxCount + "]" + ajaxURL;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OasisFormField that = (OasisFormField) o;

        if (fieldId != null ? !fieldId.equals(that.fieldId) : that.fieldId != null) return false;

        return true;
    }

    public int hashCode() {
        return (fieldId != null ? fieldId.hashCode() : 0);
    }


    public String getStyleInlineForCell() {
        return styleInlineForCell;
    }

    public void setStyleInlineForCell(String styleInlineForCell) {
        this.styleInlineForCell = styleInlineForCell;
    }

    public String getFieldColSpan() {
        return fieldColSpan;
    }

    public void setFieldColSpan(String fieldColSpan) {
        if (!FormatUtils.isInt(fieldColSpan)) {
            this.fieldColSpan = null;
        }
        else {
            int iColSpan = Integer.parseInt(fieldColSpan);
            if (iColSpan <= 0) {
                this.fieldColSpan = null;
            }
            else if (iColSpan == 1) {
                this.fieldColSpan = fieldColSpan;
            }
            else {
                // Set colspan as a logical value.
                // "2" really means "3":  make the field span 4 columns, 1 for the label and 3 for the field.
                // "3" really means "5":  make the field span 6 columns, 1 for the label and 5 for the field.
                // Etc.
                this.fieldColSpan = Integer.toString((iColSpan * 2) - 1);
            }
        }
    }

    public String getEmptyCellsAfterFld() {
        return emptyCellsAfterFld;
    }

    public void setEmptyCellsAfterFld(String emptyCellsAfterFld) {
        if (!FormatUtils.isInt(emptyCellsAfterFld)) {
            this.emptyCellsAfterFld = null;
        }
        else {
            int iEmptyCellsAfterFld = Integer.parseInt(emptyCellsAfterFld);
            // Set empty cells as a logical value.
            // "1" really means "2":  add 2 empty td tags, 1 label cell  and 1 value cell.
            // "2" really means "4":  add 4 empty td tags, 2 label cells and 2 value cells.
            // Etc.
            this.emptyCellsAfterFld = Integer.toString(iEmptyCellsAfterFld * 2);
        }
    }


    public String getEmptyCellsBeforeFld() {
        return emptyCellsBeforeFld;
    }

    public void setEmptyCellsBeforeFld(String emptyCellsBeforeFld) {
        if (!FormatUtils.isInt(emptyCellsBeforeFld)) {
            this.emptyCellsBeforeFld = null;
        }
        else {
            int iEmptyCellsBeforeFld = Integer.parseInt(emptyCellsBeforeFld);
            // Set empty cells as a logical value.
            // "1" really means "2":  add 2 empty td tags, 1 label cell  and 1 value cell.
            // "2" really means "4":  add 4 empty td tags, 2 label cells and 2 value cells.
            // Etc.
            this.emptyCellsBeforeFld = Integer.toString(iEmptyCellsBeforeFld * 2);
        }
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getTaborder() {
        return taborder;
    }

    public void setTaborder(String taborder) {
        this.taborder = taborder;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getFieldDependency() {
        return fieldDependency;
    }

    public void setFieldDependency(String fieldDependency) {
        this.fieldDependency = fieldDependency;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean getIsMasked() {
        return isMasked;
    }

    public void setIsMasked(boolean isMasked) {
        this.isMasked = isMasked;
    }

    /**
     * toString
     *
     * @return Only the fieldId
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisFormField");
        buf.append("{fieldId=").append(fieldId);
        buf.append(",label=").append(label);
        buf.append(",isProtected=").append(isProtected);
        buf.append(",isVisible=").append(isVisible);
        buf.append(",isReadOnly=").append(isReadOnly);
        buf.append(",isRequired=").append(isRequired);
        buf.append(",isMasked=").append(isMasked);
        buf.append('}');
        return buf.toString();
    }

    /**
     * Dumps all field attributes to a String.  This is a more useful String representation
     * of the object.
     * @return String representation of the object.
     */
    public String dumpFields() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisFormField");
        buf.append("{fieldId=").append(fieldId);
        buf.append(",label=").append(label);
        buf.append(",cols=").append(cols);
        buf.append(",maxLength=").append(maxLength);
        buf.append(",isVisible=").append(isVisible);
        buf.append(",isRequired=").append(isRequired);
        buf.append(",defaultValue=").append(defaultValue);
        buf.append(",formatPattern=").append(formatPattern);
        buf.append(",style=").append(style);
        buf.append(",isReadOnly=").append(isReadOnly);
        buf.append(",displayType=").append(displayType);
        buf.append(",rows=").append(rows);
        buf.append(",lovSql=").append(lovSql);
        buf.append(",datatype=").append(datatype);
        buf.append(",rowNum=").append(rowNum);
        buf.append(",colNum=").append(colNum);
        buf.append(",isProtected=").append(isProtected);
        buf.append(",ajaxURL=").append(ajaxURL);
        buf.append(",styleInlineForCell=").append(styleInlineForCell);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",emptyCellsAfterFld=").append(emptyCellsAfterFld);
        buf.append(",emptyCellsBeforeFld=").append(emptyCellsBeforeFld);
        buf.append(",alignment=").append(alignment);
        buf.append(",taborder=").append(taborder);
        buf.append(",tooltip=").append(tooltip);
        buf.append(",fieldDependency=").append(fieldDependency);
        buf.append(",href=").append(href);
        buf.append(",isMasked=").append(isMasked);
        buf.append(",colAggregate=").append(colAggregate);
        buf.append('}');
        return buf.toString();
    }

    /*
    *  OasisFormField doesn't require deep cloning: no collections
    * */
    public Object clone() {
        OasisFormField result = null;
        try {
            result = (OasisFormField)super.clone();
        } catch (CloneNotSupportedException e) {
            // assert false;
        }

        return result;
    }

    public String getColWidth() {
        return colWidth;
    }

    public void setColWidth(String colWidth) {
        this.colWidth = colWidth;
    }

    public String getColMinWidth() {
        return colMinWidth;
    }

    public void setColMinWidth(String colMinWidth) {
        this.colMinWidth = colMinWidth;
    }

    public String getUseXssFilter() {
        return useXssFilter;
    }

    public void setUseXssFilter(String useXssFilter) {
        this.useXssFilter = useXssFilter;
    }

    public String getColAggregate() {
        return colAggregate;
    }

    public void setColAggregate(String colAggregate) {
        this.colAggregate = colAggregate;
    }

    public void setLovList(List list) {
        lovList = list;
    }

    public List getLovList() {
        return lovList;
    }
}
