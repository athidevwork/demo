package dti.oasis.tags.ogcachemgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.tags.jqxgrid.JqxGridInfo;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   7/3/2018
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class GridData {

    public GridData() {
    }

    public GridData(String gridId, BaseResultSet rsData) {
        this.gridId = gridId;
        this.rsData = rsData;
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public String getUpdateColumns() {
        return updateColumns;
    }

    public void setUpdateColumns(String updateColumns) {
        this.updateColumns = updateColumns;
    }

    public void setAttribute(String key, Object value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setAttribute", new Object[]{key, value});
        }

        gridAtrributes.put(key, value);
    }

    public Object getAttribute(String key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getAttribute", new Object[]{key});
        }

        Object value = gridAtrributes.get(key);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAttribute", value);
        }
        return value;
    }

    public Iterator<String> getAttributeNames() {
        return gridAtrributes.keySet().iterator();
    }

    public BaseResultSet getResultSet() {
        return rsData;
    }

    public void setResultSet(BaseResultSet m_rsData) {
        this.rsData = m_rsData;
    }

    public boolean isForJqxGrid() {
        return isForJqxGrid;
    }

    public void setForJqxGrid(boolean forJqxGrid) {
        isForJqxGrid = forJqxGrid;
    }

    public JqxGridInfo getGridInfo() {
        return gridInfo;
    }

    public void setGridInfo(JqxGridInfo gridInfo) {
        isForJqxGrid = true;
        this.gridInfo = gridInfo;
    }

    private String gridId = "";
    private String updateColumns = "";
    private Map gridAtrributes = new HashMap();
    private BaseResultSet rsData;
    private boolean isForJqxGrid = false;
    private JqxGridInfo gridInfo;
    private final Logger l = LogUtils.getLogger(getClass());
}
