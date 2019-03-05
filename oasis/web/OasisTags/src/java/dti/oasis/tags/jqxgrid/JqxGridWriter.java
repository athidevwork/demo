package dti.oasis.tags.jqxgrid;

import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.json.JsonHelper;
import dti.oasis.tags.GridHelper;
import dti.oasis.tags.OasisTagHelper;
import dti.oasis.tags.ogcachemgr.GridData;
import dti.oasis.tags.ogcachemgr.OasisGridCacheManager;
import dti.oasis.util.*;
import org.apache.struts.util.LabelValueBean;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;
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
 * 01/12/2018       kshen       Changed to set the grid info only in the JSP tag, and load grid after pageEntitlement
 *                              in footer.jsp/footerpopup.jsp.
 * 04/04/2018       cesar       #191962 - added col_width and min_col_width
 * 06/26/2018       cesar       #194019 - Modified writeColumnConfig() to set the column width with getColWidth() only.
 * 08/06/2018       dpang       #194641 - added col_aggregate
 * 10/10/2018       ylu         #195883 - item18: format the datetime type of column field, which similar with date type column.
 * 10/22/2018       cesar       #195712 - Modified writeRawData() to check if field is masked for date, currency and date time.
 * 11/13/2018       wreeder     196147 - Support writing grid data for ajax
 *                                     - Switch to using JsonHelper to write JSON data
 *                                     - Switch from using PageContext to using HttpServletRequest so it can be reused by non-JSP classes
 * 11/21/2018       clm         195889 - Using getString and getDate by name instead of by index in writeRawData
 * 11/23/2018       clm         195889 - remove skip CSELECT_IND value logic in writeRawData
 * ---------------------------------------------------
 */
public class JqxGridWriter {
    private final Logger l = LogUtils.getLogger(getClass());

    private static volatile JqxGridWriter c_instance = null;

    private JqxGridWriter() {
    }

    public static JqxGridWriter getInstance() {
        if (c_instance == null) {
            synchronized (JqxGridHelper.class) {
                if (c_instance == null) {
                    c_instance = new JqxGridWriter();
                }
            }
        }
        return c_instance;
    }

    /**
     * Render grid.
     * @param gridInfo
     * @throws ParseException
     */
    public void writeGrid(JqxGridInfo gridInfo, PrintWriter out) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGrid", new Object[]{gridInfo});
        }

        // Write the DIV of a grid.
        writeGridDiv(gridInfo, out);

        // Write the hidden txtXML field for grid.
        out.print(GridHelper.createHidden("txtXML", ""));

        // Start of grid info, configuration, and data...
        out.print("\n<script type=\"text/javascript\">\n");

        writeGridInfo(gridInfo, out);
        writeGridConfig(gridInfo, out);
        writeColumnConfig(gridInfo, out);
        writeData(gridInfo, out);
        writeGridInitScript(gridInfo, out);
        //writeGridFunctions(request, gridInfo);

        // End of writing grid.
        out.print("</script>\n");

        l.exiting(getClass().getName(), "writeGrid");
    }
    /**
     * Write only the data for the grid.
     * @param gridInfo
     * @param out
     * @throws ParseException
     */
    public void writeGridDataForAjax(JqxGridInfo gridInfo, PrintWriter out) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGrid", new Object[]{gridInfo});
        }

        writeData(gridInfo, out, true);

        l.exiting(getClass().getName(), "writeGrid");
    }

    /**
     * Render grid info.
     * The following are the properties:
     * {
     *     id: "{gridId}",
     *     properties: {
     *         gridDetailDivId: "{value}",
     *         OBREnforcingFieldList: "{value}",
     *         OBRConsequenceFieldList: "{value}",
     *         OBRAllAccessedFieldList: "{value}",
     *         OBREnforcingUpdateIndicator: "{value}"
     *     }
     * }
     * @param gridInfo
     * @param out
     */
    private void writeGridInfo(JqxGridInfo gridInfo, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGridInfo", new Object[]{gridInfo});
        }

        out.println("    var " + gridInfo.getId() + "GridInfo = {");

        JsonHelper.writeProperty(out, "id", gridInfo.getId(), 2, false);

        JsonHelper.addObjectEndTag(out, 1);

        out.println("    " + gridInfo.getId() + "GridInfo.properties = {");

        if (!StringUtils.isBlank(gridInfo.getGridDetailDivId())) {
            JsonHelper.writeProperty(out, "gridDetailDivId", gridInfo.getGridDetailDivId(), 2);
        }
        JsonHelper.writeProperty(out, "OBREnforcingFieldList", gridInfo.getResultSet().getOBREnforcingFieldList(), 2);
        JsonHelper.writeProperty(out, "OBRConsequenceFieldList", gridInfo.getResultSet().getOBRConsequenceFieldList(), 2);
        JsonHelper.writeProperty(out, "OBRAllAccessedFieldList", gridInfo.getResultSet().getOBRAllAccessedFieldList(), 2);
        JsonHelper.writeProperty(out, "OBREnforcingUpdateIndicator", gridInfo.getResultSet().getOBREnforcingUpdateIndicator(), 2);
        JsonHelper.writeProperty(out, "deferredLoadDataProcess", gridInfo.getDeferredLoadDataProcess(), 2);
        JsonHelper.writeProperty(out, "isCacheResultSet", gridInfo.isCacheResultSet(), 2);
        JsonHelper.writeProperty(out, "cacheKey", gridInfo.getCacheKey(), 2);
        JsonHelper.writeProperty(out, "virtualPaging", gridInfo.isVirtualPaging(), 2);
        JsonHelper.writeProperty(out, "virtualScrolling", gridInfo.isVirtualScrolling(), 2);
        JsonHelper.writeProperty(out, "loadingDeferredObj", "$.Deferred()", 2, true, false, true);
        JsonHelper.writeProperty(out, "sortingDeferredObj", "$.Deferred().resolve()", 2, false, false, true);

        JsonHelper.addObjectEndTag(out, 1);

        out.println("    " + gridInfo.getId() + "GridInfo.config = {};");

        l.exiting(getClass().getName(), "writeGridInfo");
    }

    /**
     * Grid config:
     * @param gridInfo
     * @param out
     */
    private void writeGridConfig(JqxGridInfo gridInfo, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGridConfig", new Object[]{gridInfo});
        }

        JqxGridConfig gridConfig = gridInfo.getGridConfig();

        if (gridConfig != null) {
            out.print("    " + gridInfo.getId() + "GridInfo.config.gridConfig = {\n");

            if (!StringUtils.isBlank(gridConfig.getHeight())) {
                JsonHelper.writeProperty(out, "height", gridConfig.getHeight(), 2);
            }

            if (!StringUtils.isBlank(gridConfig.getWidth())) {
                JsonHelper.writeProperty(out, "width", gridConfig.getWidth(), 2);
            }

            if (!StringUtils.isBlank(gridConfig.getFormName())) {
                JsonHelper.writeProperty(out, "formName", gridConfig.getFormName(), 2);
            }

            if (!StringUtils.isBlank(gridConfig.getAnchorColumnName())) {
                JsonHelper.writeProperty(out, "anchorColumnName", gridConfig.getAnchorColumnName(), 2);
            }

            if (!StringUtils.isBlank(gridConfig.getGridDetailDivId())) {
                JsonHelper.writeProperty(out, "gridDetailDivId", gridConfig.getGridDetailDivId(), 2);
            }

            JsonHelper.writeProperty(out, "pageSize", gridConfig.getPageSize(), 2);
            JsonHelper.writeProperty(out, "sortable", gridConfig.isSortable(), 2);
            JsonHelper.writeProperty(out, "selectable", gridConfig.isSelectable(), 2);
            JsonHelper.writeProperty(out, "saveGridAsExcelCsv", gridConfig.isSaveGridAsExcelCsv(), 2);
            JsonHelper.writeProperty(out, "saveGridAsExcelHtml", gridConfig.isSaveGridAsExcelHtml(), 2);
            JsonHelper.writeProperty(out, "exportType", gridConfig.getExportType(), 2);
            JsonHelper.writeProperty(out, "dispositionTypeExcelCsvFile", gridConfig.getDispositionTypeExcelCsvFile(), 2);
            JsonHelper.writeProperty(out, "dispositionTypeExcelHtmlFile", gridConfig.getDispositionTypeExcelHtmlFile(), 2, false);

            JsonHelper.addObjectEndTag(out, 1);
        }

        l.exiting(getClass().getName(), "writeGridConfig");
    }

    /**
     * Write column config:
     * @param gridInfo
     * @param out
     */
    private void writeColumnConfig(JqxGridInfo gridInfo, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeColumnConfig", new Object[]{gridInfo});
        }

        out.print("    " + gridInfo.getId() + "GridInfo.config.columnsConfig = [\n");

        Collection<JqxColumnConfig> columnConfigs = gridInfo.getColumnConfigs();
        int index = -1;

        for (JqxColumnConfig columnConfig: columnConfigs) {
            index++;

            JsonHelper.addObjectStartTag(out, 2);

            JsonHelper.writeProperty(out, "id", columnConfig.getId(), 3);

            if (!StringUtils.isBlank(columnConfig.getFieldId())) {
                JsonHelper.writeProperty(out, "fieldId", columnConfig.getFieldId(), 3);
            }

            if (!StringUtils.isBlank(columnConfig.getLabel())) {
                JsonHelper.writeProperty(out, "label", columnConfig.getLabel(), 3);
            }

            JsonHelper.writeProperty(out, "aggregates", columnConfig.getColAggregate(), 3);
            JsonHelper.writeProperty(out, "width", columnConfig.getColWidth(), 3);
            JsonHelper.writeProperty(out, "minWidth", columnConfig.getColMinWidth(), 3);
            JsonHelper.writeProperty(out, "visible", columnConfig.isVisible(), 3);
            JsonHelper.writeProperty(out, "editable", columnConfig.isEditable(), 3);
            JsonHelper.writeProperty(out, "required", columnConfig.isRequired(), 3);
            JsonHelper.writeProperty(out, "dataType", columnConfig.getDataType(), 3);
            JsonHelper.writeProperty(out, "displayType", columnConfig.getDisplayType(), 3);
            JsonHelper.writeProperty(out, "maxLength", columnConfig.getMaxLength(), 3);
            JsonHelper.writeProperty(out, "align", columnConfig.getAlign(), 3);
            JsonHelper.writeProperty(out, "masked", columnConfig.isMasked(), 3);
            JsonHelper.writeProperty(out, "protected", columnConfig.isProtected(), 3);

            if (!StringUtils.isBlank(columnConfig.getHref())) {
                JsonHelper.writeProperty(out, "href", columnConfig.getHref(), 3);
            }

            if (columnConfig.getListData() != null && columnConfig.getListData().size() > 0 ) {
                JsonHelper.writePropertyName(out, "listData", 3, true);
                JsonHelper.addArrayStartTag(out);

                for (int i = 0; i < columnConfig.getListData().size(); i++) {
                    LabelValueBean labelValueBean = (LabelValueBean) columnConfig.getListData().get(i);
                    JsonHelper.addObjectStartTag(out, 4, false);

                    JsonHelper.writeProperty(out, "code", labelValueBean.getValue(), true, false);
                    JsonHelper.writeProperty(out, "label", labelValueBean.getLabel(), false, false);

                    JsonHelper.addObjectEndTag(out, (i < columnConfig.getListData().size() -1));
                }

                JsonHelper.addArrayEndTag(out, 3, true, true);
            }

            JsonHelper.writeProperty(out, "displayFormat", columnConfig.getDisplayFormat(), 3, false);
            JsonHelper.addObjectEndTag(out, 2, (index < columnConfigs.size() - 1));
        }
        JsonHelper.addArrayEndTag(out, 1, false, true, true);

        l.exiting(getClass().getName(), "writeColumnConfig");
    }

    private void writeData(JqxGridInfo gridInfo, PrintWriter out) throws ParseException {
        writeData(gridInfo, out, false);
    }

    private void writeData(JqxGridInfo gridInfo, PrintWriter out, boolean forAjax) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeData", new Object[]{gridInfo});
        }

        if (!forAjax) {
            out.print("    " + gridInfo.getId() + "GridInfo.data = {};\n");
        }

        // Get column names and updateable columns.
        List<String> columnNames = new ArrayList<String>();
        List<String> dateAndUrlColumnNames = new ArrayList<String>();
        Set<String> updColIdxs = new TreeSet<String>();

        BaseResultSet rs = gridInfo.getResultSet();
        Map<String, JqxColumnConfig> columnConfigMap = gridInfo.getColumnConfigMap();
        int columnCount = rs.getColumnCount();
        int curColIdx = -1;
        for (int i = 1; i <= columnCount; i ++) {
            String columnName = getDataIslandColumnName(rs.getColumnName(i));
            JqxColumnConfig columnConfig = columnConfigMap.get(columnName);
            if (columnConfig != null) {
                curColIdx++;
                columnNames.add(columnName);

                if (columnConfig.isUpdateable()) {
                    updColIdxs.add(String.valueOf(curColIdx));
                }

                // Get date_{columnName} and url_{columnName}
                if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE) ||
                        columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE_TIME)) {
                    dateAndUrlColumnNames.add("DATE_" + curColIdx);
                } else {
                    String fieldHref = columnConfig.getHref();

                    if (!StringUtils.isBlank(fieldHref)) {
                        dateAndUrlColumnNames.add("URL_" + curColIdx);
                    }
                }

                // Get lov label column and display only column
                if (columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.COMBOBOX) ||
                        columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.DROPDOWN_LIST)) {
                    columnNames.add(columnName + "LOVLABEL");
                    curColIdx++;
                }
                else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.PHONE)) {
                    columnNames.add(columnName + FormatUtils.DISPLAY_FIELD_EXTENTION);
                    curColIdx++;
                }
                else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE) ||
                        columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE_TIME)) {
                    if(!FormatUtils.isDateFormatUS()) {
                        columnNames.add(columnName + FormatUtils.DISPLAY_FIELD_EXTENTION);
                        curColIdx++;
                    }
                }
                else if (!StringUtils.isBlank(columnConfig.getDisplayFormat())) {
                    if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.NUMBER)) {
                        columnNames.add(columnName + FormatUtils.DISPLAY_FIELD_EXTENTION);
                        curColIdx++;
                    }
                }
            }
        }

        columnNames.addAll(dateAndUrlColumnNames);

        // Build the updateable column index for legacy grid(Update data by column index).
        String[] updColIdxArray = updColIdxs.toArray(new String[updColIdxs.size()]);
        String updColIndexes = String.join(",", updColIdxArray);

        // Write model record (The template for adding records.)
        if (!forAjax) {
            writeModelRecord(gridInfo, columnNames, updColIndexes, out);
        }

        // Write data
        writeRawData(gridInfo, columnNames, updColIndexes, out, forAjax);

        l.exiting(getClass().getName(), "writeData");
    }

    private void writeModelRecord(JqxGridInfo gridInfo, List<String> columnNames, String updColInxStr, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeModelRecord", new Object[]{gridInfo, columnNames, updColInxStr});
        }

        out.print("    " + gridInfo.getId() + "GridInfo.data.MODEL = {\n");

        for (String columnName : columnNames) {
            JsonHelper.writeProperty(out, columnName, "", 2);
        }

        columnNames.add("UPDATE_IND");
        columnNames.add("DISPLAY_IND");
        columnNames.add("EDIT_IND");
        columnNames.add("OBR_ENFORCED_RESULT");
        columnNames.add("@id");
        columnNames.add("@index");
        columnNames.add("@col");

        JsonHelper.writeProperty(out, "UPDATE_IND", "N", 2);
        JsonHelper.writeProperty(out, "DISPLAY_IND", "Y", 2);
        JsonHelper.writeProperty(out, "EDIT_IND", "N", 2);
        JsonHelper.writeProperty(out, "OBR_ENFORCED_RESULT", "", 2);
        JsonHelper.writeProperty(out, "@id", "", 2);
        JsonHelper.writeProperty(out, "@index", "null", 2);
        JsonHelper.writeProperty(out, "@col", updColInxStr, 2, false);

        JsonHelper.addObjectEndTag(out, 1);

        out.print("    " + gridInfo.getId() + "GridInfo.data.columnNames = [");

        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                JsonHelper.addCommaSeparator(out, false);
            }
            JsonHelper.writePropertyName(out, columnNames.get(i), false);
        }

        JsonHelper.addArrayEndTag(out, false, true, true);

        l.exiting(getClass().getName(), "writeModelRecord");
    }

    private void writeRawData(JqxGridInfo gridInfo, List<String> columnNames, String updColInxStr, PrintWriter out, boolean forAjax) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeRawData", new Object[]{gridInfo, columnNames, updColInxStr});
        }

        if (forAjax) {
            JsonHelper.writeProperty(out, "gridId", gridInfo.getId(), 1);
            out.println("    \"rawData\": [");
        }
        else {
            out.print("    " + gridInfo.getId() + "GridInfo.data.rawData = [\n");
        }

        BaseResultSet rs = gridInfo.getResultSet();

        if (gridInfo.isCacheResultSet() && !forAjax) {
            // For initial load of grid
            OasisGridCacheManager ogCacheMgr = new OasisGridCacheManager();
            GridData gridData = ogCacheMgr.getData(gridInfo.getCacheKey());
            if (null == gridData) {
                l.logp(Level.SEVERE, getClass().getName(), "writeRawData", "cached data is not a GridData");
            } else {
                gridData.setUpdateColumns(updColInxStr);
            }
        } else {
            int rowIndex = -1;
            int colCount = rs.getColumnCount();
            int lastRowIndex = rs.getRowCount() - 1;

            rs.beforeFirst();
            while (rs.next()) {
                rowIndex++;

                JsonHelper.addObjectStartTag(out, 2);

                String id = "";
                for (int i = 1; i <= colCount; i++) {
                    String columnName = rs.getColumnName(i);
                    String dataIslandColumnName = getDataIslandColumnName(columnName);

                    if (dataIslandColumnName.endsWith("LOVLABEL") || dataIslandColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                        //|| dataIslandColumnName.equals("CSELECT_IND"))
                        continue;

                    JqxColumnConfig columnConfig = gridInfo.getColumnConfigByDataColumnName(dataIslandColumnName);

                    String value;
                    if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE)) {
                        if (columnConfig.isProtected()) {
                            value = "";
                        } else {
                            Date dte = rs.getDate(columnName);
                            value = OasisTagHelper.formatDateAsXml(dte);
                        }
                    } else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE_TIME)) {
                        if (columnConfig.isProtected()) {
                            value = "";
                        } else {
                            Date dte = rs.getDate(columnName);
                            value = OasisTagHelper.formatDateTimeAsXml(dte);
                        }
                    } else {
                        value = columnConfig.isProtected() ? "" : rs.getString(columnName);

                        if (!StringUtils.isBlank(value)) {
                            if ((columnConfig.getDataType().equals(JqxColumnConfig.DataType.NUMBER) ||
                                columnConfig.getDataType().equals(JqxColumnConfig.DataType.CURRENCY) ||
                                columnConfig.getDataType().equals(JqxColumnConfig.DataType.CURRENCY_FORMATTED)) &&
                                value.contains("$")) {
                                // Unformat currency values for sorting and filtering currency correctly.
                                value = FormatUtils.unformatCurrency(value);
                            }
                        }
                    }

                    if (columnConfig.isMasked() && !columnConfig.isProtected()) {
                        value = rs.getString(columnName);
                    }

                    if (columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.COMBOBOX)) {
                        JsonHelper.writeProperty(out, dataIslandColumnName, value, 2);
                    } else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.PERCENTAGE)) {
                        if (columnConfig.isMasked()) {
                            JsonHelper.writeProperty(out, dataIslandColumnName, value, 2);
                        } else {
                            if (StringUtils.isBlank(columnConfig.getDecimalPlaces())) {
                                JsonHelper.writeProperty(out, dataIslandColumnName, FormatUtils.formatPercentage(value), 2);
                            } else {
                                JsonHelper.writeProperty(out, dataIslandColumnName, FormatUtils.formatPercentage(value, Integer.valueOf(columnConfig.getDecimalPlaces())), 2);
                            }
                        }
                    } else {
                        JsonHelper.writeProperty(out, dataIslandColumnName, value, 2);
                    }

                    if (columnConfig != null) {
                        if (columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.DROPDOWN_LIST)) {
                            String labels = "";
                            if (columnConfig.isMasked()) {
                                labels = columnConfig.isProtected() ? "" : value;
                                JsonHelper.writeProperty(out, dataIslandColumnName + "LOVLABEL", labels, 2);
                            } else {
                                labels = columnConfig.isProtected() ? "" : getLovLabel(columnConfig, value);
                                JsonHelper.writeProperty(out, dataIslandColumnName + "LOVLABEL", labels, 2);
                            }
                        }
                        else if (columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.COMBOBOX)) {
                            String labels = "";
                            if (columnConfig.isMasked()) {
                                labels = columnConfig.isProtected() ? "" : value;
                                JsonHelper.writeProperty(out, dataIslandColumnName + "LOVLABEL", labels, 2);
                            } else {
                                labels = columnConfig.isProtected() ? "" : getLovLabels(columnConfig, value);
                                JsonHelper.writeProperty(out, dataIslandColumnName + "LOVLABEL", labels, 2);
                            }
                        }
                        else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.PHONE)) {
                            if (columnConfig.isMasked()) {
                                JsonHelper.writeProperty(out, dataIslandColumnName + FormatUtils.DISPLAY_FIELD_EXTENTION, value, 2);
                            } else {
                                String formattedPhone = columnConfig.isProtected() ? "" : FormatUtils.formatPhoneNumberForDisplay(value);
                                JsonHelper.writeProperty(out, dataIslandColumnName + FormatUtils.DISPLAY_FIELD_EXTENTION, formattedPhone, 2);
                            }
                        }
                        else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE)) {
                            if(!FormatUtils.isDateFormatUS()) {
                                String formattedDate = columnConfig.isProtected() ? "" : OasisTagHelper.formatCustomDateAsXml(rs.getDate(i));
                                if (columnConfig.isMasked()) {
                                    formattedDate = rs.getString(columnName);
                                }
                                JsonHelper.writeProperty(out, dataIslandColumnName + FormatUtils.DISPLAY_FIELD_EXTENTION, formattedDate, 2);
                            }
                        }
                        else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE_TIME)) {
                            if(!FormatUtils.isDateFormatUS()) {
                                String formattedDateTime = columnConfig.isProtected() ? "" : OasisTagHelper.formatCustomDateTimeAsXml(rs.getDate(i));
                                if (columnConfig.isMasked()) {
                                    formattedDateTime = rs.getString(columnName);
                                }
                                JsonHelper.writeProperty(out, dataIslandColumnName + FormatUtils.DISPLAY_FIELD_EXTENTION, formattedDateTime, 2);
                            }
                        }
                        else {
                            if (!StringUtils.isBlank(columnConfig.getDisplayFormat())) {
                                if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.NUMBER) ) {
                                    String formattedNumber = columnConfig.isProtected() ? "" : FormatUtils.formatNumber(value, columnConfig.getDisplayFormat());
                                    if (columnConfig.isMasked()) {
                                        formattedNumber = rs.getString(columnName);
                                    }
                                    JsonHelper.writeProperty(out, dataIslandColumnName + FormatUtils.DISPLAY_FIELD_EXTENTION, formattedNumber, 2);
                                }
                            }
                        }
                    }

                    if (columnConfig.isAnchorColumn()) {
                        id = value;
                    }
                }

                for (int i = 1; i <= colCount; i++) {
                    String columnName = rs.getColumnName(i);
                    String dataIslandColumnName = getDataIslandColumnName(rs.getColumnName(i));
                    if (dataIslandColumnName.endsWith("LOVLABEL") || dataIslandColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                        continue;

                    JqxColumnConfig columnConfig = gridInfo.getColumnConfigByDataColumnName(dataIslandColumnName);

                    if (columnConfig != null) {
                        int columnIndex = columnNames.indexOf(dataIslandColumnName);

                        if (columnIndex != -1) {
                            String dataItem = rs.getString(columnName);
                            boolean isDataPresent = (!columnConfig.isProtected() && dataItem != null && dataItem.trim().length() > 0);

                            if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE)) {
                                JsonHelper.writeProperty(out,
                                        "DATE_" + columnIndex,
                                        isDataPresent ? Long.toString(DateUtils.dateDiff(DateUtils.DD_DAYS, "01/01/1993", rs.getDate(columnName))) : "",
                                        2);
                            } else if (columnConfig.getDataType().equals(JqxColumnConfig.DataType.DATE_TIME)) {
                                JsonHelper.writeProperty(out,
                                        "DATE_" + columnIndex,
                                        isDataPresent ? Long.toString(DateUtils.dateDiff(DateUtils.DD_SECS, "01/01/1993 00:00:00", rs.getDate(columnName))): "",
                                        2);
                            } else {
                                String fieldHref = columnConfig.getHref();

                                if (!StringUtils.isBlank(fieldHref)) {
                                    // TODO handle configs from grid xml
                                    JsonHelper.writeProperty(out,
                                            "URL_" + columnIndex,
                                            fieldHref,
                                            2);
                                }
                            }
                        }
                    }
                }

                JsonHelper.writeProperty(out, "UPDATE_IND", String.valueOf(rs.getUpdateInd()), 2);
                JsonHelper.writeProperty(out, "DISPLAY_IND", String.valueOf(rs.getDisplayInd()), 2);
                JsonHelper.writeProperty(out, "EDIT_IND", String.valueOf(rs.getEditInd()), 2);

                JsonHelper.writeProperty(out, "OBR_ENFORCED_RESULT", "", 2);

                JsonHelper.writeProperty(out, "@id", id, 2);
                out.print("        \"@index\": " + Integer.toString(rowIndex) + ",\n");
                JsonHelper.writeProperty(out, "@col", updColInxStr, 2, false);

                JsonHelper.addObjectEndTag(out, 2, rowIndex != lastRowIndex, true);
            }
        }

        if (forAjax) {
            JsonHelper.addArrayEndTag(out);
        }
        else {
            JsonHelper.addArrayEndTag(out, 1, false, true, true);
        }


        l.exiting(getClass().getName(), "writeRawData");
    }

    private String getDataIslandColumnName(String resultSetColumnName) {
        String sColName;
        sColName = new StringBuilder("C").append(resultSetColumnName.trim().toUpperCase().replace(']', ' ').trim().
                replace('[', ' ').trim().replace(' ', '_').replace('#', 'N').replace('/', ' ').trim().
                replace('\'', '_')).toString();
        return sColName;
    }

    private String getLovLabel(JqxColumnConfig columnConfig, String code) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLovLabel", new Object[]{columnConfig, code});
        }

        String label = "";

        if (columnConfig != null &&
                (columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.DROPDOWN_LIST) ||
                        columnConfig.getDisplayType().equals(JqxColumnConfig.DisplayType.COMBOBOX))) {
            List lov = columnConfig.getListData();

            label = CollectionUtils.getDecodedValue(lov, code);
            if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(label)) {
                label = "";
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLovLabel", label);
        }
        return label;
    }

    private String getLovLabels(JqxColumnConfig columnConfig, String code) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getLovLabels", new Object[]{columnConfig, code});
        }

        String labels = "";

        if (!StringUtils.isBlank(code)) {
            String[] values = code.split(",");

            for (String value : values) {
                String label = getLovLabel(columnConfig, value);

                if (!StringUtils.isBlank(label)) {
                    if (!labels.equals("")) {
                        labels = labels + ",";
                    }

                    labels = labels + label;
                }
            }

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getLovLabels", labels);
        }
        return labels;
    }

    /**
     * Write the DIV of a grid.
     * @param gridInfo
     * @param out
     */
    private void writeGridDiv(JqxGridInfo gridInfo, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGridDiv", new Object[]{gridInfo});
        }

        out.println();
        out.println("<div id=\"DIV_" + gridInfo.getId() + "\" style=\"width:100%\"><div id=\"" + gridInfo.getId() + "\"></div></div>");

        l.exiting(getClass().getName(), "writeGridDiv");
    }

    private void writeGridInitScript(JqxGridInfo gridInfo, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "writeGridInit", new Object[]{gridInfo});
        }

        out.println();
        // Set the grid info to page scope. The grids on the page would be loaded in footer.jsp/footerpopup.jsp after pageEntitlements generated.
        out.println("    dti.oasis.page.setGridInfo(\"" + gridInfo.getId() + "\", " + gridInfo.getId() + "GridInfo);");

        l.exiting(getClass().getName(), "writeGridInit");
    }
}
