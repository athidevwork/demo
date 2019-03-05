package dti.oasis.tags.jqxgrid;

import dti.oasis.tags.*;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/10/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  08/09/2018      jdingle     handle exception in getField when oasisGrid.getHeader().getFields() returns WebLayer
 *  08/13/2018      kshen       Changed to use getHeader().getFields().get(fieldId) to get field for both Web Layer and OasisFields\
 *  08/31/2018      kshen       Changed the default align for percentage column.
 *  11/07/2018      dpang       Issue 196632 - Changed to not set anchor column to be editable.
 * 11/13/2018       wreeder     196147 - Switch from using PageContext to using HttpServletRequest so it can be reused by non-JSP classes
 * ---------------------------------------------------
 */
public class JqxColumnConfigHelper {
    private final Logger l = LogUtils.getLogger(getClass());

    private static volatile JqxColumnConfigHelper c_instance = null;

    public static JqxColumnConfigHelper getInstance() {
        if (c_instance == null) {
            synchronized (JqxColumnConfigHelper.class) {
                if (c_instance == null) {
                    c_instance = new JqxColumnConfigHelper();
                }
            }
        }

        return c_instance;
    }

    public boolean isDisplayOnlyColumn(String dataColumnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isDisplayOnlyColumn", new Object[]{dataColumnName});
        }

        boolean displayOnlyColumn = dataColumnName.endsWith(LOV_LABEL) ||
                dataColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isDisplayOnlyColumn", displayOnlyColumn);
        }
        return displayOnlyColumn;
    }

    public String getDataIslandColumnName(OasisGrid oasisGrid, int dataColumnIndex) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDataIslandColumnName", new Object[]{oasisGrid, dataColumnIndex});
        }

        String columnName = "C" + oasisGrid.getData().getColumnName(dataColumnIndex).trim().toUpperCase()
                .replace(']', ' ').trim()
                .replace('[', ' ').trim()
                .replace(' ', '_')
                .replace('#', 'N')
                .replace('/', ' ').trim()
                .replace('\'', '_');

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDataIslandColumnName", columnName);
        }
        return columnName;
    }

    public OasisFormField getField(OasisGrid oasisGrid, Map headerMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getField", new Object[]{oasisGrid, headerMap});
        }
        OasisFormField field = null;

        String fieldId = (String) headerMap.get(XMLGridHeader.CN_FIELDNAME);
        if (!StringUtils.isBlank(fieldId)) {
            if (oasisGrid.getHeader().getLayerFields() != null) {
                field = (OasisFormField) oasisGrid.getHeader().getLayerFields().get(fieldId);
            }

            if (field == null && oasisGrid.getHeader().getFields() != null) {
                // Both OasisFields and WebLayer support to use get method to get field by id.
                field = (OasisFormField) oasisGrid.getHeader().getFields().get(fieldId);
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getField", field);
        }
        return field;
    }

    public String getLabel(Map headerMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLabel", new Object[]{headerMap});
        }

        String label = null;

        boolean isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();
        if (!isProtected && !headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
            label = (String) headerMap.get(XMLGridHeader.CN_NAME);
        } else {
            if (headerMap.get(XMLGridHeader.CN_FIELDNAME) != null) {
                String fieldName = headerMap.get(XMLGridHeader.CN_FIELDNAME).toString();
                String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
                if (fieldName.endsWith(gridHeaderSuffix)) {
                    label = ((String) headerMap.get(XMLGridHeader.CN_NAME))
                            .replaceAll("&nbsp;", "%20").replaceAll(",", ":;:")
                            + "(Hidden)";
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLabel", label);
        }
        return label;
    }

    public boolean isColumnVisible(Map headerMap) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isColumnVisible", new Object[]{headerMap});
        }

        boolean visible = false;

        boolean isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();
        if (!isProtected && !headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
            visible = true;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isColumnVisible", visible);
        }
        return visible;
    }

    public boolean isColumnEditable(Map headerMap, boolean visible) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isColumnEditable", new Object[]{headerMap, visible});
        }

        boolean editable = false;
        int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();

        if (visible) {
            switch (type) {
                case XMLGridHeader.TYPE_TEXT:
                case XMLGridHeader.TYPE_TEXTAREA:
                case XMLGridHeader.TYPE_NUMBER:
                case XMLGridHeader.TYPE_DROPDOWN:
                case XMLGridHeader.TYPE_DATE:
                case XMLGridHeader.TYPE_CHECKBOX:
                case XMLGridHeader.TYPE_UPPERCASE_TEXT:
                case XMLGridHeader.TYPE_LOWERCASE_TEXT:
                case XMLGridHeader.TYPE_PHONE:
                case XMLGridHeader.TYPE_PERCENTAGE:
                case XMLGridHeader.TYPE_MULTIPLE_DROPDOWN:
                case XMLGridHeader.TYPE_FORMATMONEY:
                    editable = true;
                    break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isColumnEditable", editable);
        }
        return editable;
    }

    public boolean isColumnRequired(Map headerMap, OasisFormField field, boolean visible, boolean editable) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isColumnRequired", new Object[]{headerMap, field, visible, editable});
        }

        boolean required = false;

        if (field != null && visible && editable) {
            if (field.getIsRequired()) {
                required = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isColumnRequired", required);
        }
        return required;
    }

    public String getOasisDataType(Map headerMap, OasisFormField field) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getOasisDataType", new Object[]{headerMap, field});
        }

        // Default data type is string.
        String dataType = JqxColumnConfig.DataType.STRING;

        int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
        switch (type) {
            case XMLGridHeader.TYPE_FORMATDATE:
            case XMLGridHeader.TYPE_DATE:
            case XMLGridHeader.TYPE_UPDATEONLYDATE:
                dataType = JqxColumnConfig.DataType.DATE;
                break;
            case XMLGridHeader.TYPE_FORMATDATETIME:
            case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                dataType = JqxColumnConfig.DataType.DATE_TIME;
                break;
            case XMLGridHeader.TYPE_NUMBER:
            case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                dataType = JqxColumnConfig.DataType.NUMBER;
                break;
            case XMLGridHeader.TYPE_FORMATMONEY:
            case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                dataType = JqxColumnConfig.DataType.CURRENCY;
                break;
            case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
            case XMLGridHeader.TYPE_PERCENTAGE:
                dataType = JqxColumnConfig.DataType.PERCENTAGE;
                break;
            case XMLGridHeader.TYPE_TEXT:
            case XMLGridHeader.TYPE_ANCHOR:
            case XMLGridHeader.TYPE_TEXTAREA:
                dataType = JqxColumnConfig.DataType.STRING;
                break;
            case XMLGridHeader.TYPE_UPDATEONLYPHONE:
            case XMLGridHeader.TYPE_PHONE:
                dataType = JqxColumnConfig.DataType.PHONE;
                break;
            case XMLGridHeader.TYPE_UPPERCASE_TEXT:
                dataType = JqxColumnConfig.DataType.UPPERCASE_STRING;
                break;
            case XMLGridHeader.TYPE_LOWERCASE_TEXT:
                dataType = JqxColumnConfig.DataType.LOWERCASE_STRING;
                break;
        }

        if (dataType.equals(JqxColumnConfig.DataType.STRING)) {
            if (field != null) {
                String fieldDataType = field.getDatatype();

                if (!StringUtils.isBlank(fieldDataType)) {
                    if (fieldDataType.equals(OasisFields.TYPE_NUMBER)) {
                        dataType = JqxColumnConfig.DataType.NUMBER;

                    } else if (fieldDataType.equals(OasisFields.TYPE_CURRENCY) ||
                            fieldDataType.equals(OasisFields.TYPE_CURRENCY_FORMATTED)) {
                        dataType = JqxColumnConfig.DataType.CURRENCY;

                    } else if (fieldDataType.equals(OasisFields.TYPE_DATE)) {
                        dataType = JqxColumnConfig.DataType.DATE;

                    } else if (fieldDataType.equals(OasisFields.TYPE_TIME)) {
                        dataType = JqxColumnConfig.DataType.DATE_TIME;

                    } else if (fieldDataType.equals(OasisFields.TYPE_UPPERCASE_TEXT)) {
                        dataType = JqxColumnConfig.DataType.UPPERCASE_STRING;

                    } else if (fieldDataType.equals(OasisFields.TYPE_LOWERCASE_TEXT)) {
                        dataType = JqxColumnConfig.DataType.LOWERCASE_STRING;

                    } else if (fieldDataType.equals(OasisFields.TYPE_PERCENTAGE)) {
                        dataType = JqxColumnConfig.DataType.PERCENTAGE;

                    } else if (fieldDataType.equals(OasisFields.TYPE_PHONE)) {
                        dataType = JqxColumnConfig.DataType.PHONE;
                    }
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOasisDataType", dataType);
        }
        return dataType;
    }

    public boolean isAnchorColumn(Map headerMap, String dataColumnName, String anchorColumnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAnchorColumn", new Object[]{headerMap, dataColumnName, anchorColumnName});
        }

        boolean anchorColumn = false;
        if (StringUtils.isBlank(anchorColumnName)) {
            int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();

            if (type == XMLGridHeader.TYPE_ANCHOR) {
                anchorColumn = true;
            }
        } else {
            if (anchorColumnName.toUpperCase().equals(dataColumnName.toUpperCase())) {
                anchorColumn = true;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isAnchorColumn", anchorColumn);
        }
        return anchorColumn;
    }

    public String getColumnDisplayType(Map headerMap, boolean visible) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getColumnDisplayType", new Object[]{headerMap, visible});
        }

        String displayType = JqxColumnConfig.DisplayType.TEXT;

        int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();

        switch (type) {
            case XMLGridHeader.TYPE_DEFAULT:
            case XMLGridHeader.TYPE_UPDATEONLY:
                // Default
                displayType = JqxColumnConfig.DisplayType.TEXT;
                break;
            case XMLGridHeader.TYPE_TEXT:
            case XMLGridHeader.TYPE_UPPERCASE_TEXT:
            case XMLGridHeader.TYPE_LOWERCASE_TEXT:
                // Text
                displayType = JqxColumnConfig.DisplayType.TEXT;
                break;
            case XMLGridHeader.TYPE_UPDATEONLYPHONE:
            case XMLGridHeader.TYPE_PHONE:
                // Phone
                displayType = JqxColumnConfig.DisplayType.TEXT;
                break;
            case XMLGridHeader.TYPE_FORMATDATE:
            case XMLGridHeader.TYPE_FORMATDATETIME:
            case XMLGridHeader.TYPE_DATE:
            case XMLGridHeader.TYPE_UPDATEONLYDATE:
            case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                // Date
                displayType = JqxColumnConfig.DisplayType.DATE_TIME_INPUT;
                break;
            case XMLGridHeader.TYPE_NUMBER:
            case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                // Number
                displayType = JqxColumnConfig.DisplayType.NUMBER_INPUT;
                break;
            case XMLGridHeader.TYPE_FORMATMONEY:
            case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                // Currency
                displayType = JqxColumnConfig.DisplayType.NUMBER_INPUT;
                break;
            case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
            case XMLGridHeader.TYPE_PERCENTAGE:
                // Percentage
                displayType = JqxColumnConfig.DisplayType.NUMBER_INPUT;
                break;
            case XMLGridHeader.TYPE_DROPDOWN:
            case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN:
                // Dropdown
                displayType = JqxColumnConfig.DisplayType.DROPDOWN_LIST;
                break;
            case XMLGridHeader.TYPE_URL:
            case XMLGridHeader.TYPE_UPDATEONLYURL:
                // URL
                // TODO TBD...
                displayType = JqxColumnConfig.DisplayType.TEXT;
                break;
            case XMLGridHeader.TYPE_CHECKBOX:
            case XMLGridHeader.TYPE_CHECKBOXREAD:
                // Checkbox
                displayType = JqxColumnConfig.DisplayType.CHECKBOX;
                break;
            case XMLGridHeader.TYPE_RADIOBUTTON:
                // TODO No usage.
                break;
            case XMLGridHeader.TYPE_IMG:
                // TODO No usage.
                displayType = JqxColumnConfig.DisplayType.IMG;
                break;
            case XMLGridHeader.TYPE_ANCHOR:
                // Anchor
                displayType = JqxColumnConfig.DisplayType.TEXT;
                break;
            case XMLGridHeader.TYPE_TEXTAREA:
                // Text area
                displayType = JqxColumnConfig.DisplayType.TEXTAREA;
                break;
            case XMLGridHeader.TYPE_MULTIPLE_DROPDOWN:
            case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                // Multi dropdown
                displayType = JqxColumnConfig.DisplayType.COMBOBOX;
                break;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getColumnDisplayType", displayType);
        }
        return displayType;
    }

    public String getDisplayFormat(Map headerMap, String dataType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDisplayFormat", new Object[]{headerMap, dataType});
        }

        String displayFormat = null;
        if (dataType.equals(JqxColumnConfig.DataType.DATE)) {
            if (!FormatUtils.isDateFormatUS()) {
                displayFormat = FormatUtils.getDateFormatForDisplayString();
            }
        } else if (dataType.equals(JqxColumnConfig.DataType.NUMBER)) {
            displayFormat = (String) headerMap.get(XMLGridHeader.CN_PATTERN);
            if (!StringUtils.isBlank(displayFormat)) {
                Integer iD = (Integer) headerMap.get(XMLGridHeader.CN_DISPLAY);
                int display = (iD == null) ? 0 : iD.intValue();

                if (display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                    displayFormat = (String) headerMap.get(XMLGridHeader.CN_PATTERN);
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getDisplayFormat", displayFormat);
        }
        return displayFormat;
    }

    public String getFieldAlign(Map headerMap, String dataType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldAlign", new Object[]{headerMap, dataType});
        }

        String align = (String) headerMap.get(XMLGridHeader.CN_ALIGN);
        if (align == null) {
            // if we don't have alignment defined, default money to right and others to left
            if (dataType.equals(JqxColumnConfig.DataType.CURRENCY) ||
                    dataType.equals(JqxColumnConfig.DataType.CURRENCY_FORMATTED) ||
                    dataType.equals(JqxColumnConfig.DataType.NUMBER) ||
                    dataType.equals(JqxColumnConfig.DataType.PERCENTAGE))
                align = "right";
            else
                align = "left";
        }
        else {
            if ("R".equals(align)) {
                align = "right";
            }
            else if ("C".equals(align)) {
                align = "center";
            }
            else {
                align = "left";
            }
        }

        return align;
    }

    public boolean isColumnUpdateable(Map headerMap, boolean visible) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isColumnUpdateable", new Object[]{headerMap, visible});
        }

        boolean updateable = true;

        int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();

        if (visible) {
            switch (type) {
                case XMLGridHeader.TYPE_DEFAULT:
                case XMLGridHeader.TYPE_URL:
                case XMLGridHeader.TYPE_CHECKBOXREAD:
                case XMLGridHeader.TYPE_IMG:
                    updateable = false;
                    break;
            }
        } else {
            updateable = false;
            switch (type) {
                case XMLGridHeader.TYPE_UPDATEONLY:
                case XMLGridHeader.TYPE_UPDATEONLYDATE:
                case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN:
                case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                    updateable = true;
                    break;
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isColumnUpdateable", updateable);
        }
        return updateable;
    }

    public List getListData(HttpServletRequest request, Map headerMap, OasisFormField field, String detailFieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getListData", new Object[]{request, headerMap, field, detailFieldId});
        }

        List lov = (List) headerMap.get(XMLGridHeader.CN_LISTDATA);

        // if lov is null and we have a fieldId, look it up
        if (lov == null && field != null) {
            lov = field.getLovList();
            if (lov == null) {
                lov = (ArrayList) request.getAttribute(field.getFieldId() + "LOV");
            }
        }

        if (lov == null) {
            if (!StringUtils.isBlank(detailFieldId)) {
                lov = (ArrayList) request.getAttribute(detailFieldId + "LOV");
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getListData", lov);
        }
        return lov;
    }

    private static final String LOV_LABEL = "LOVLABEL";
}
