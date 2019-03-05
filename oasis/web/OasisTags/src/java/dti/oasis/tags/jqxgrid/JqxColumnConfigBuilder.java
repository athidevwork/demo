package dti.oasis.tags.jqxgrid;

import dti.oasis.tags.OasisFormField;
import dti.oasis.tags.OasisGrid;
import dti.oasis.tags.XMLGridHeader;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
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
 * 04/04/2018       cesar       #191962 - added col_width and min_col_width
 * 08/06/2018       dpang       #194641 - added col_aggregate
 * 11/13/2018       wreeder     196147 - Switch from using PageContext to using HttpServletRequest so it can be reused by non-JSP classes
 * ---------------------------------------------------
 */
public class JqxColumnConfigBuilder {
    private final Logger l = LogUtils.getLogger(getClass());

    public JqxColumnConfigBuilder(HttpServletRequest request, OasisGrid oasisGrid, JqxGridInfo gridInfo) {
        m_request = request;
        m_gridInfo = gridInfo;
        m_oasisGrid = oasisGrid;
    }

    public Map<String, JqxColumnConfig> buildColumnsConfig() {
        l.entering(getClass().getName(), "buildColumnsConfig");

        Map<String, JqxColumnConfig> columnConfigMap = new LinkedHashMap<String, JqxColumnConfig>();

        JqxColumnConfigHelper jqxColumnConfigHelper = JqxColumnConfigHelper.getInstance();

        XMLGridHeader gridHeader = this.m_oasisGrid.getHeader();

        for (int i = 1; i <= gridHeader.size(); i++) {
            Map headerMap = gridHeader.getHeaderMap(i);

            String dataColumnName = (String) headerMap.get(XMLGridHeader.CN_DATACOLUMNNAME);

            if (jqxColumnConfigHelper.isDisplayOnlyColumn(dataColumnName)) {
                continue;
            }

            JqxColumnConfig columnConfig = new JqxColumnConfig();

            int dataColumnIdx = gridHeader.getDataColumnIndex(dataColumnName);
            String dataIslandColumnName = jqxColumnConfigHelper.getDataIslandColumnName(m_oasisGrid, dataColumnIdx);

            // id (data column name)
            columnConfig.setId(dataIslandColumnName);

            OasisFormField field = jqxColumnConfigHelper.getField(m_oasisGrid, headerMap);

            if (field != null) {
                // Field Id
                columnConfig.setFieldId(field.getFieldId());

                //Max Length
                columnConfig.setMaxLength(field.getMaxLength());
            }

            // Label
            columnConfig.setLabel(jqxColumnConfigHelper.getLabel(headerMap));

            // Visible
            boolean visible = jqxColumnConfigHelper.isColumnVisible(headerMap);
            columnConfig.setVisible(visible);

            // Editable
            boolean editable = jqxColumnConfigHelper.isColumnEditable(headerMap, visible);
            columnConfig.setEditable(editable);

            // Required
            columnConfig.setRequired(jqxColumnConfigHelper.isColumnRequired(headerMap, field, visible, editable));

            // Data type
            String dataType = jqxColumnConfigHelper.getOasisDataType(headerMap, field);
            columnConfig.setDataType(dataType);

            // Anchor column?
            columnConfig.setAnchorColumn(jqxColumnConfigHelper.isAnchorColumn(
                    headerMap, dataColumnName, m_gridInfo.getGridConfig().getAnchorColumnName()));

            // Masked?
            columnConfig.setMasked((boolean) headerMap.get(XMLGridHeader.CN_MASKED));

            // Protected?
            columnConfig.setProtected((boolean) headerMap.get(XMLGridHeader.CN_PROTECTED));

            // Display type
            columnConfig.setDisplayType(jqxColumnConfigHelper.getColumnDisplayType(headerMap, visible));

            // Display format
            columnConfig.setDisplayFormat(jqxColumnConfigHelper.getDisplayFormat(headerMap, dataType));

            // Align
            columnConfig.setAlign(jqxColumnConfigHelper.getFieldAlign(headerMap, dataType));

            columnConfig.setWidth((String) headerMap.get(XMLGridHeader.CN_LENGTH));

            // Updateable
            columnConfig.setUpdateable(jqxColumnConfigHelper.isColumnUpdateable(headerMap, visible));

            // Detail field ID
            String detailFieldId = (String) headerMap.get(XMLGridHeader.CN_DETAIL_FIELDID);
            columnConfig.setDetailFieldId(detailFieldId);

            // URL
            columnConfig.setHref((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF));

            // List Data
            columnConfig.setListData(jqxColumnConfigHelper.getListData(m_request, headerMap, field, detailFieldId));

            // Decimal places
            columnConfig.setDecimalPlaces((String) headerMap.get(XMLGridHeader.CN_DECIMALPLACES));

            columnConfigMap.put(dataIslandColumnName, columnConfig);

            // col width
            columnConfig.setColWidth((String) headerMap.get(XMLGridHeader.COL_WIDTH));
            // col min width
            columnConfig.setColMinWidth((String) headerMap.get(XMLGridHeader.COL_MIN_WIDTH));

            //col aggregate
            columnConfig.setColAggregate((String) headerMap.get(XMLGridHeader.COL_AGGREGATE));

        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildColumnsConfig", columnConfigMap);
        }
        return columnConfigMap;
    }

    private OasisGrid m_oasisGrid;
    private JqxGridInfo m_gridInfo;
    private HttpServletRequest m_request;
}
