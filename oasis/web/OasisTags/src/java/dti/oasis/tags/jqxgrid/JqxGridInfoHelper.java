package dti.oasis.tags.jqxgrid;

import dti.oasis.app.ApplicationContext;
import dti.oasis.tags.GridHelper;
import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
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
 * 11/13/2018       wreeder     196147 - Switch from using PageContext to using HttpServletRequest so it can be reused by non-JSP classes
 * ---------------------------------------------------
 */
public class JqxGridInfoHelper {
    private final Logger l = LogUtils.getLogger(getClass());
    private static volatile JqxGridInfoHelper c_instance = null;
    private static final String KEY_ENVGRIDALWAYSSAVEASEXCELCSV = "gridAlwaysSaveAsExcelCsv";
    private static final String KEY_ENVGRIDALWAYSSAVEASEXCELHTML = "gridAlwaysSaveAsExcelHtml";
    private static final String KEY_EXPORT_TYPE = "gridExportExcelCsvType";
    private static final String EXPORT_TYPE_XLS = "XLS";
    private static final String EXPORT_TYPE_XLSX = "XLSX";
    private static final String EXPORT_TYPE_CSV = "CSV";

    private JqxGridInfoHelper() {}

    public static JqxGridInfoHelper getInstance() {
        if (c_instance == null) {
            synchronized (JqxGridInfoHelper.class) {
                if (c_instance == null) {
                    c_instance = new JqxGridInfoHelper();
                }
            }
        }
        return c_instance;
    }

    /**
     * Get grid Id.
     * @return
     */
    public String getGridId(HttpServletRequest request, OasisGrid oasisGrid) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGridId", new Object[]{request, oasisGrid});
        }

        String gridId = null;

        if (request.getAttribute("gridDisplayGridId") != null) {
            gridId = (String) request.getAttribute("gridDisplayGridId");
        } else {
            if (StringUtils.isBlank(oasisGrid.getId())) {
                gridId = "testgrid";
            } else {
                gridId = oasisGrid.getId();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGridId", gridId);
        }
        return gridId;
    }

    public String getFormName(HttpServletRequest request, OasisGrid oasisGrid) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFormName", new Object[]{request, oasisGrid});
        }

        String fromName = null;
        if (request.getAttribute("gridDisplayFormName")!=null) {
            fromName = (String) request.getAttribute("gridDisplayFormName");
        } else {
            if(StringUtils.isBlank(oasisGrid.getFormName())) {
                fromName = "gridlist";
            } else {
                fromName = oasisGrid.getFormName();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFormName", fromName);
        }
        return fromName;
    }

    public String getGridDetailDivId(HttpServletRequest request, OasisGrid oasisGrid) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getGridDetailDivId", new Object[]{request, oasisGrid});
        }

        String gridDetailDivId = null;

        if (request.getAttribute("gridDetailDivId")!=null) {
            gridDetailDivId = (String) request.getAttribute("gridDetailDivId");
        } else {
            if(StringUtils.isBlank(oasisGrid.getGridDetailDivId())) {
                gridDetailDivId = "";
            } else {
                gridDetailDivId = oasisGrid.getGridDetailDivId();
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getGridDetailDivId", gridDetailDivId);
        }
        return gridDetailDivId;
    }

    public JqxGridExportOption getExportGridOption(HttpServletRequest request, OasisGrid oasisGrid) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getExportGridOption", new Object[]{request, oasisGrid});
        }

        boolean saveGridAsExcelCsv = false;
        boolean saveGridAsExcelHtml = false;

        Context env = null;
        String envVal = null;
        try {
            env = (Context) new InitialContext().lookup("java:comp/env");
        }
        catch (NamingException ignore) {
        }
        if (env != null) {
            try {
                // Look for entry "gridAlwaysSaveAsExcelCsv".
                envVal = (String) env.lookup(KEY_ENVGRIDALWAYSSAVEASEXCELCSV);
            }
            catch (NamingException ignore) {
                envVal = null;
            }
        }

        if (env == null || StringUtils.isBlank(envVal)) {
            envVal = ApplicationContext.getInstance().getProperty(KEY_ENVGRIDALWAYSSAVEASEXCELCSV, null) ;
        }

        if (!StringUtils.isBlank(envVal) && envVal.equalsIgnoreCase("true")) {
            // If entry "gridAlwaysSaveAsExcelCsv" is set to "true",
            // then all grids must show the Excel (CSV) button.
            saveGridAsExcelCsv = true;
        }
        else {
            // Otherwise, it is up to the individual grid's property to determine
            // if we should show the Excel (CSV) button.
            saveGridAsExcelCsv = oasisGrid.isSaveAsExcelCsv();
        }

        if (env != null) {
            try {
                // Look for entry "gridAlwaysSaveAsExcelHtml".
                envVal = (String) env.lookup(KEY_ENVGRIDALWAYSSAVEASEXCELHTML);
            }
            catch (NamingException ignore) {
                envVal = null;
            }
        }

        if (env == null || StringUtils.isBlank(envVal)) {
            envVal = ApplicationContext.getInstance().getProperty(KEY_ENVGRIDALWAYSSAVEASEXCELHTML, null) ;
        }

        if (!StringUtils.isBlank(envVal) && envVal.equalsIgnoreCase("true")) {
            // If entry "gridAlwaysSaveAsExcelHtml" is set to "true",
            // then all grids must show the Excel (HTML) button.
            saveGridAsExcelHtml = true;
        }
        else {
            // Otherwise, it is up to the individual grid's property to determine
            // if we should show the Excel (HTML) button.
            saveGridAsExcelHtml = oasisGrid.isSaveAsExcelHtml();
        }

        String exportType = ApplicationContext.getInstance().getProperty(KEY_EXPORT_TYPE, EXPORT_TYPE_CSV);
        if (!exportType.equalsIgnoreCase(EXPORT_TYPE_CSV) && !exportType.equalsIgnoreCase(EXPORT_TYPE_XLS)) {
            exportType = EXPORT_TYPE_XLSX;
        }

        JqxGridExportOption jqxGridExportOption = new JqxGridExportOption();
        jqxGridExportOption.setSaveGridAsExcelCsv(saveGridAsExcelCsv);
        jqxGridExportOption.setSaveGridAsExcelHtml(saveGridAsExcelHtml);
        jqxGridExportOption.setExportType(exportType);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getExportGridOption", jqxGridExportOption);
        }
        return jqxGridExportOption;
    }

}
