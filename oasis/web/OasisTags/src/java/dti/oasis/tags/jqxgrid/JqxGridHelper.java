package dti.oasis.tags.jqxgrid;

import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.tags.OasisGrid;
import dti.oasis.util.LogUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The helper class for building jqx grid.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/20/2016
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/13/2018       wreeder     196147 - Support building jqxGrid Data
 * ---------------------------------------------------
 */
public class JqxGridHelper {
    private final Logger l = LogUtils.getLogger(getClass());

    private static volatile JqxGridHelper c_instance = null;;

    private JqxGridHelper() {
    }

    public static JqxGridHelper getInstance() {
        if (c_instance == null) {
            synchronized (JqxGridHelper.class) {
                if (c_instance == null) {
                    c_instance = new JqxGridHelper();
                }
            }
        }
        return c_instance;
    }

    public void buildJqxGrid(OasisGrid oasisGrid, HttpServletRequest request, PrintWriter out) throws ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildJqxGrid", new Object[]{oasisGrid, request});
        }

        // Build grid info.
        JqxGridInfo gridInfo = new JqxGridInfoBuilder(oasisGrid, request).build();

        // Render grid.
        JqxGridWriter.getInstance().writeGrid(gridInfo, out);

        l.exiting(getClass().getName(), "buildJqxGrid");
    }

    public void buildJqxGridData(OasisGrid oasisGrid, HttpServletRequest request, PrintWriter out) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildJqxGrid", new Object[]{oasisGrid, request});
        }

        try {
            // Build grid info.
            JqxGridInfo gridInfo = new JqxGridInfoBuilder(oasisGrid, request).build();

            // Render grid data.
            JqxGridWriter.getInstance().writeGridDataForAjax(gridInfo, out);
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to build the jqxGrid data", e);
            l.throwing(getClass().getName(), "buildJqxGridData", ae);
            throw ae;
        }

        l.exiting(getClass().getName(), "buildJqxGrid");
    }
}
