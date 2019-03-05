package dti.oasis.tags.jqxgrid;

import java.util.List;

/**
* <p>(C) 2003 Delphi Technology, inc. (dti)</p>
* Date:   4/26/2016
*
* @author kshen
*/
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 04/04/2018       cesar        #191962 - added col_width and min_col_width
* 08/06/2018       dpang        #194641 - added col_aggregate
* ---------------------------------------------------
*/

public class JqxColumnConfig {
    public JqxColumnConfig() {
    }

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getJqxDataType() {
        return m_jqxDataType;
    }

    public void setJqxDataType(String jqxDataType) {
        m_jqxDataType = jqxDataType;
    }

    public String getFieldId() {
        return m_fieldId;
    }

    public void setFieldId(String fieldId) {
        m_fieldId = fieldId;
    }

    public String getDetailFieldId() {
        return m_detailFieldId;
    }

    public void setDetailFieldId(String detailFieldId) {
        m_detailFieldId = detailFieldId;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public boolean isEditable() {
        return m_editable;
    }

    public void setEditable(boolean editable) {
        m_editable = editable;
    }

    public boolean isVisible() {
        return m_visible;
    }

    public void setVisible(boolean visible) {
        m_visible = visible;
    }

    public boolean isRequired() {
        return m_required;
    }

    public void setRequired(boolean required) {
        m_required = required;
    }

    public String getDataType() {
        return m_dataType;
    }

    public void setDataType(String dataType) {
        m_dataType = dataType;
    }

    public String getDisplayType() {
        return m_displayType;
    }

    public void setDisplayType(String displayType) {
        m_displayType = displayType;
    }

    public String getDisplayFormat() {
        return m_displayFormat;
    }

    public void setDisplayFormat(String displayFormat) {
        m_displayFormat = displayFormat;
    }

    public boolean isUpdateable() {
        return m_updateable;
    }

    public void setUpdateable(boolean updateable) {
        m_updateable = updateable;
    }

    public String getHref() {
        return m_href;
    }

    public void setHref(String href) {
        m_href = href;
    }

    public List getListData() {
        return m_listData;
    }

    public void setListData(List listData) {
        m_listData = listData;
    }

    public boolean isAnchorColumn() {
        return m_anchorColumn;
    }

    public void setAnchorColumn(boolean anchorColumn) {
        m_anchorColumn = anchorColumn;
    }

    public String getDecimalPlaces() {
        return m_decimalPlaces;
    }

    public void setDecimalPlaces(String decimalPlaces) {
        m_decimalPlaces = decimalPlaces;
    }

    public boolean isMasked() {
        return m_masked;
    }

    public void setMasked(boolean masked) {
        m_masked = masked;
    }

    public boolean isProtected() {
        return m_protected;
    }

    public void setProtected(boolean aProtected) {
        m_protected = aProtected;
    }

    public String getAlign() {
        return m_align;
    }

    public void setAlign(String align) {
        m_align = align;
    }

    public String getOasisDataType() {
        return m_oasisDataType;
    }

    public void setOasisDataType(String oasisDataType) {
        m_oasisDataType = oasisDataType;
    }

    public String getWidth() {
        return m_width;
    }

    public void setWidth(String width) {
        m_width = width;
    }

    public String getMaxLength() {
        return m_maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.m_maxLength = maxLength;
    }

    public String getColWidth() {
        return m_colWidth;
    }

    public void setColWidth(String colWidth) {
        this.m_colWidth = colWidth;
    }

    public String getColMinWidth() {
        return m_colMinWidth;
    }

    public void setColMinWidth(String colMinWidth) {
        this.m_colMinWidth = colMinWidth;
    }

    public String getColAggregate() {
        return m_colAggregate;
    }

    public void setColAggregate(String colAggregate) {
        this.m_colAggregate = colAggregate;
    }

    private String m_id;
    private String m_jqxDataType;
    private String m_fieldId;
    private String m_detailFieldId;
    private String m_label;
    private boolean m_editable;
    private boolean m_visible;
    private boolean m_required;
    private boolean m_updateable;
    private String m_align;
    private String m_width;
    private String m_maxLength;

    private String m_dataType;
    private String m_oasisDataType;
    private String m_displayType;
    private String m_displayFormat;
    private String m_href;
    private String m_decimalPlaces;

    private List m_listData;
    private boolean m_anchorColumn;

    private boolean m_masked;
    private boolean m_protected;

    private String m_colWidth;
    private String m_colMinWidth;
    private String m_colAggregate;

    public static class DataType {
        public static final String CURRENCY = "CU";
        public static final String CURRENCY_FORMATTED = "CF";
        public static final String DATE = "DT";
        public static final String DATE_TIME = "TM";
        public static final String NUMBER = "NM";
        public static final String PERCENTAGE = "PT";
        public static final String PHONE = "PH";
        public static final String STRING = "ST";
        public static final String UPPERCASE_STRING = "UT";
        public static final String LOWERCASE_STRING = "LT";
    }

    public static class DisplayType {
        public static final String CHECKBOX = "checkbox";
        public static final String COMBOBOX = "combobox";
        public static final String DATE_TIME_INPUT = "datetimeinput";
        public static final String DROPDOWN_LIST = "dropdownlist";
        public static final String IMG = "img";
        public static final String NUMBER_INPUT = "numberinput";
        public static final String TEXT = "text";
        public static final String TEXTAREA = "textarea";
        public static final String TEXTBOX = "textbox";
        public static final String URL = "url";
    }
}
