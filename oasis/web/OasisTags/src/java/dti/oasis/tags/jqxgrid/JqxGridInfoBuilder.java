package dti.oasis.tags.jqxgrid;

import dti.oasis.recordset.BaseResultSetRecordSetAdaptor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.request.RequestStorageIds;
import dti.oasis.request.RequestStorageManager;
import dti.oasis.session.UserSessionManager;
import dti.oasis.tags.*;
import dti.oasis.tags.ogcachemgr.GridData;
import dti.oasis.tags.ogcachemgr.OasisGridCacheManager;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* <p>(C) 2003 Delphi Technology, inc. (dti)</p>
* Date:   4/26/2016
*
* @author kshen
*/
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/13/2018       wreeder     196147 - Support deferredLoadDataProcess, virtualPaging, virtualScrolling and cacheResultSet
 * ---------------------------------------------------
 */
public class JqxGridInfoBuilder {
    private final Logger l = LogUtils.getLogger(getClass());

    public JqxGridInfoBuilder(OasisGrid oasisGrid, HttpServletRequest request) {
        m_oasisGrid = oasisGrid;
        m_request = request;
    }

    public JqxGridInfo build() {
        l.exiting(getClass().getName(), "build");

        // Build grid info.
        buildGridInfo();

        // Build grid config.
        m_gridInfo.setGridConfig(new JqxGridConfigBuilder(m_oasisGrid, m_request).build());

        // Build columns config.
        m_gridInfo.setColumnConfigMap(new JqxColumnConfigBuilder(m_request, m_oasisGrid, m_gridInfo).buildColumnsConfig());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "build", m_gridInfo);
        }
        return m_gridInfo;
    }

    /**
     * Build grid config.
     */
    private void buildGridInfo() {
        l.entering(getClass().getName(), "buildGridInfo");

        m_gridInfo = new JqxGridInfo();
        // Grid Id
        m_gridInfo.setId(JqxGridInfoHelper.getInstance().getGridId(m_request, m_oasisGrid));
        // Grid detail div ID
        m_gridInfo.setGridDetailDivId(JqxGridInfoHelper.getInstance().getGridDetailDivId(m_request, m_oasisGrid));
        // Grid data
        m_gridInfo.setResultSet(m_oasisGrid.getData());
        // Should the data be loaded in a separate process
        m_gridInfo.setDeferredLoadDataProcess(m_oasisGrid.getDeferredLoadDataProcess());
        // Should the data be loaded in virtual paging mode
        m_gridInfo.setVirtualPaging(m_oasisGrid.isVirtualPaging());
        // Should the data be loaded in virtual scrolling mode
        m_gridInfo.setVirtualScrolling(m_oasisGrid.isVirtualScrolling());

        // Is the result set cached for asynchronous loading
        m_gridInfo.setCacheResultSet(m_oasisGrid.isCacheResultset());
        if (m_oasisGrid.isCacheResultset()) {
            // Setup the Oasis Grid Cache and store the cache key in the GridInfo
            HttpServletRequest request = (HttpServletRequest) RequestStorageManager.getInstance().get(RequestStorageIds.HTTP_SEVLET_REQUEST);

            String gridId = m_gridInfo.getId();
            BaseResultSet rs = m_gridInfo.getResultSet();
            GridData gridData = new GridData(gridId, rs);
            gridData.setGridInfo(m_gridInfo);

            // Add the requried attributes
            String gridHeaderBeanName = StringUtils.isBlank(gridId) ? "gridHeaderBean" : gridId + "HeaderBean";
            gridData.setAttribute(gridHeaderBeanName, request.getAttribute(gridHeaderBeanName));
            Enumeration<String> attributeNames = request.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String s = attributeNames.nextElement();
                if (s.endsWith("LOV")) {
                    gridData.setAttribute(s, request.getAttribute(s));
                }
            }

            OasisGridCacheManager ogCacheMgr = new OasisGridCacheManager();
            String sessionId = UserSessionManager.getInstance().getUserSession().getSessionId();
            String cacheKey = ogCacheMgr.putData(sessionId, gridData);
            m_gridInfo.setCacheKey(cacheKey);
        }

        l.exiting(getClass().getName(), "buildGridInfo");
    }

    private OasisGrid m_oasisGrid;
    private HttpServletRequest m_request;
    private JqxGridInfo m_gridInfo;
}
