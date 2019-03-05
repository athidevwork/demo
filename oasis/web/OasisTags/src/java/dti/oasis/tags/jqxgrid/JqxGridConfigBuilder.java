package dti.oasis.tags.jqxgrid;

import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/23/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class JqxGridConfigBuilder {
    private final Logger l = LogUtils.getLogger(getClass());

    public JqxGridConfigBuilder(OasisGrid oasisGrid, HttpServletRequest request) {
        this.m_oasisGrid = oasisGrid;
        this.m_request = request;
    }

    public JqxGridConfig build() {
        l.entering(getClass().getName(), "build");

        JqxGridConfig gridConfig = new JqxGridConfig();

        gridConfig.setHeight(this.m_oasisGrid.getGridHeight());
        gridConfig.setWidth(this.m_oasisGrid.getGridWidth());
        gridConfig.setPageSize(this.m_oasisGrid.getPageSize());
        gridConfig.setSortable(this.m_oasisGrid.isSortable());
        gridConfig.setSelectable(this.m_oasisGrid.isSelectable());
        gridConfig.setFormName(JqxGridInfoHelper.getInstance().getFormName(m_request, m_oasisGrid));
        gridConfig.setAnchorColumnName(this.m_oasisGrid.getHeader().getAnchorColumnName());
        gridConfig.setGridDetailDivId(this.m_oasisGrid.getGridDetailDivId());

        JqxGridExportOption jqxGridExportOption = JqxGridInfoHelper.getInstance().getExportGridOption(m_request, m_oasisGrid);
        gridConfig.setSaveGridAsExcelCsv(jqxGridExportOption.isSaveGridAsExcelCsv());
        gridConfig.setSaveGridAsExcelHtml(jqxGridExportOption.isSaveGridAsExcelHtml());
        gridConfig.setExportType(jqxGridExportOption.getExportType());
        gridConfig.setDispositionTypeExcelCsvFile(this.m_oasisGrid.getDispositionTypeExcelCsvFile());
        gridConfig.setDispositionTypeExcelHtmlFile(this.m_oasisGrid.getDispositionTypeExcelHtmlFile());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "build", gridConfig);
        }
        return gridConfig;
    }

    private OasisGrid m_oasisGrid;
    private HttpServletRequest m_request;
}
