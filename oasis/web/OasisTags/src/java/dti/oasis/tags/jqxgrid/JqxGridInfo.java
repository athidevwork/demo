package dti.oasis.tags.jqxgrid;

import dti.oasis.util.BaseResultSet;
import dti.oasis.util.LogUtils;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* <p>(C) 2003 Delphi Technology, inc. (dti)</p>
* Date:   4/26/2016
*
* @author kshen
*/
public class JqxGridInfo {
    public JqxGridInfo() {
    }

    public JqxColumnConfig getColumnConfigByDataColumnName(String dataColumnName) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getColumnConfigByDataColumnName", new Object[]{dataColumnName});
        }

        JqxColumnConfig gridColumnConfig = null;

        if (getColumnConfigMap().containsKey(dataColumnName)) {
            gridColumnConfig = getColumnConfigMap().get(dataColumnName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getColumnConfigByDataColumnName", gridColumnConfig);
        }
        return gridColumnConfig;
    }

    public Collection<JqxColumnConfig> getColumnConfigs() {
        l.entering(getClass().getName(), "getColumnConfigs");

        Collection<JqxColumnConfig> columnConfigs = getColumnConfigMap().values();

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getColumnConfigs", columnConfigs);
        }
        return columnConfigs;
    }

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getGridDetailDivId() {
        return m_gridDetailDivId;
    }

    public void setGridDetailDivId(String gridDetailDivId) {
        m_gridDetailDivId = gridDetailDivId;
    }

    public JqxGridConfig getGridConfig() {
        return m_gridConfig;
    }

    public void setGridConfig(JqxGridConfig gridConfig) {
        m_gridConfig = gridConfig;
    }

    public Map<String, JqxColumnConfig> getColumnConfigMap() {
        return m_columnConfigMap;
    }

    public void setColumnConfigMap(Map<String, JqxColumnConfig> columnConfigMap) {
        m_columnConfigMap = columnConfigMap;
    }

    public BaseResultSet getResultSet() {
        return m_resultSet;
    }

    public void setResultSet(BaseResultSet resultSet) {
        m_resultSet = resultSet;
    }

    public String getDeferredLoadDataProcess() {
        return deferredLoadDataProcess;
    }

    public void setDeferredLoadDataProcess(String deferredLoadDataProcess) {
        this.deferredLoadDataProcess = deferredLoadDataProcess;
    }

    public boolean isCacheResultSet() {
        return cacheResultSet;
    }

    public void setCacheResultSet(boolean cacheResultSet) {
        this.cacheResultSet = cacheResultSet;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public boolean isVirtualPaging() {
        return virtualPaging;
    }

    public void setVirtualPaging(boolean virtualPaging) {
        this.virtualPaging = virtualPaging;
    }

    public boolean isVirtualScrolling() {
        return virtualScrolling;
    }

    public void setVirtualScrolling(boolean virtualScrolling) {
        this.virtualScrolling = virtualScrolling;
    }

    private String m_id;
    private String m_gridDetailDivId;
    private JqxGridConfig m_gridConfig;
    private Map<String, JqxColumnConfig> m_columnConfigMap;
    private BaseResultSet m_resultSet;
    private String deferredLoadDataProcess;
    private boolean cacheResultSet = false;
    private boolean virtualPaging;
    private boolean virtualScrolling;
    private String cacheKey;
    private final Logger l = LogUtils.getLogger(getClass());
}
