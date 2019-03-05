package dti.oasis.tags.jqxgrid;

/**
* <p>(C) 2003 Delphi Technology, inc. (dti)</p>
* Date:   4/26/2016
*
* @author kshen
*/
public class JqxGridConfig {
    public JqxGridConfig() {
    }

    public String getHeight() {
        return m_height;
    }

    public void setHeight(String height) {
        m_height = height;
    }

    public String getWidth() {
        return m_width;
    }

    public void setWidth(String width) {
        m_width = width;
    }

    public int getPageSize() {
        return m_pageSize;
    }

    public void setPageSize(int pageSize) {
        m_pageSize = pageSize;
    }

    public boolean isSortable() {
        return m_sortable;
    }

    public void setSortable(boolean sortable) {
        m_sortable = sortable;
    }

    public boolean isSelectable() {
        return m_selectable;
    }

    public void setSelectable(boolean selectable) {
        m_selectable = selectable;
    }

    public String getFormName() {
        return m_formName;
    }

    public void setFormName(String formName) {
        m_formName = formName;
    }

    public String getAnchorColumnName() {
        return m_anchorColumnName;
    }

    public void setAnchorColumnName(String anchorColumnName) {
        m_anchorColumnName = anchorColumnName;
    }

    public boolean isSaveGridAsExcelCsv() {
        return m_saveGridAsExcelCsv;
    }

    public void setSaveGridAsExcelCsv(boolean saveGridAsExcelCsv) {
        m_saveGridAsExcelCsv = saveGridAsExcelCsv;
    }

    public boolean isSaveGridAsExcelHtml() {
        return m_saveGridAsExcelHtml;
    }

    public void setSaveGridAsExcelHtml(boolean saveGridAsExcelHtml) {
        m_saveGridAsExcelHtml = saveGridAsExcelHtml;
    }

    public String getDispositionTypeExcelHtmlFile() {
        return m_dispositionTypeExcelHtmlFile;
    }

    public void setDispositionTypeExcelHtmlFile(String dispositionTypeExcelHtmlFile) {
        m_dispositionTypeExcelHtmlFile = dispositionTypeExcelHtmlFile;
    }

    public String getDispositionTypeExcelCsvFile() {
        return m_dispositionTypeExcelCsvFile;
    }

    public void setDispositionTypeExcelCsvFile(String dispositionTypeExcelCsvFile) {
        m_dispositionTypeExcelCsvFile = dispositionTypeExcelCsvFile;
    }

    public String getGridDetailDivId() {
        return m_gridDetailDivId;
    }

    public void setGridDetailDivId(String gridDetailDivId) {
        m_gridDetailDivId = gridDetailDivId;
    }

    public String getExportType() {
        return m_exportType;
    }

    public void setExportType(String exportType) {
        m_exportType = exportType;
    }

    private String m_height;
    private String m_width;
    private int m_pageSize;
    private boolean m_sortable;
    private boolean m_selectable;
    private String m_formName;
    private String m_anchorColumnName;
    private String m_gridDetailDivId;
    private boolean m_saveGridAsExcelCsv;
    private boolean m_saveGridAsExcelHtml;
    private String m_dispositionTypeExcelCsvFile;
    private String m_dispositionTypeExcelHtmlFile;
    private String m_exportType;
}
