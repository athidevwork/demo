package dti.oasis.tags.jqxgrid;

import dti.oasis.util.LogUtils;

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
 *
 * ---------------------------------------------------
 */
public class JqxGridExportOption {
    private final Logger l = LogUtils.getLogger(getClass());

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

    public String getExportType() {
        return m_exportType;
    }

    public void setExportType(String exportType) {
        m_exportType = exportType;
    }

    private boolean m_saveGridAsExcelCsv;
    private boolean m_saveGridAsExcelHtml;
    private String m_exportType;
}
