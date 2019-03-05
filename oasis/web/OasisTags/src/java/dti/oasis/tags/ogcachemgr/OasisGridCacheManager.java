package dti.oasis.tags.ogcachemgr;

import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.cachemgr.Cache;
import dti.oasis.cachemgr.impl.CacheManagerImpl;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.json.JsonHelper;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.recordset.BaseResultSetRecordSetAdaptor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordSetToJSONMapper;
import dti.oasis.recordset.XMLRecordSetMapper;
import dti.oasis.tags.jqxgrid.JqxGridWriter;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.LogUtils;
import org.omnifaces.util.Json;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods for storing and retreiving data for Oasis Grid using cache manager.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 16, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 05/082014        mlm         154343 - Refactored to include gridId for grid's cache key
 * 11/13/2018       wreeder     196147 - Support writing jqxGrid cached data as JSON
 * ---------------------------------------------------
 */
public class OasisGridCacheManager {

     public static final String BEAN_NAME = "OasisGridCacheManager";

     /**
     * Returns a synchronized static instance of Oasis Grid Cache Manager that has the implementation of grid data.
     * @return OasisGridCacheManager, an instance of Oasis Grid Cache Manager with implemenation information.
     */
    public synchronized static final OasisGridCacheManager getInstance() {
        if (OasisGridCacheManager.c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(OasisGridCacheManager.BEAN_NAME)) {
                OasisGridCacheManager.c_instance = (OasisGridCacheManager) ApplicationContext.getInstance().getBean(OasisGridCacheManager.BEAN_NAME);
            }
            else{
                OasisGridCacheManager.c_instance = new OasisGridCacheManager();
            }
        }
        return OasisGridCacheManager.c_instance;
    }

    /**
     * Method that stores the provided data into cache
     * @param Data data to be cached
     * @return String representing the auto-generated cache key
     */
    public String putData(String webSessionId, String gridId, String Data) {
        String cacheKey = "OG:" + gridId + ":";
        Calendar cal = new GregorianCalendar();
        cacheKey += webSessionId + ":" + cal.getTimeInMillis();
        ogCache.put(cacheKey, Data);
        return cacheKey;
    }

    /**
     * Method that stores the provided data into cache
     * @param gridData data to be cached
     * @return String representing the auto-generated cache key
     */
    public String putData(String webSessionId, GridData gridData) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "putData", new Object[]{webSessionId, gridData});
        }

        String cacheKey = "OG:" + gridData.getGridId() + ":";
        cacheKey += webSessionId + ":" + new GregorianCalendar().getTimeInMillis();

        ogCache.put(cacheKey, gridData);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "putData", cacheKey);
        }
        return cacheKey;
    }

    public boolean isJsonGridData(String cacheKey) {
        return (ogCache.get(cacheKey) instanceof GridData);
    }

    public GridData getData(String cacheKey) {
        Object gridData = ogCache.get(cacheKey);
        if (!(gridData instanceof GridData)) {
            l.logp(Level.SEVERE, getClass().getName(), "getData", "cached data is not a GridData");
            gridData = null;
        }
        return (GridData) gridData;
    }

    /**
     * Method that writes the cached data for the provided cache key to the OutputStream. An Illegal argument expception is thrown, if the
     * provided cache key does not exists in the cache manager.
     * @param cacheKey key to find cached data
     * @return String representing the cached data
     * @throws Exception Illegal argument expception is raised when the provided cache key does not exists in the cache manager
     */
    public String writeData(HttpServletRequest request, String gridId, String cacheKey, PrintWriter out) throws Exception {
        return writeData(request, gridId, cacheKey, out, false);
    }

    /**
     * Overloaded method that writes and removes the cached data for the provided cache key to the OutputStream. An Illegal argument expception
     * is thrown, if the provided cache key does not exists in the cache manager.
     * @param cacheKey key to find cached data
     * @param removeItem boolean that indicates whether to remove the cached entry.
     * @return String representing the cached data
     * @throws Exception Illegal argument expception is raised when the provided cache key does not exists in the cache manager
     */
    public String writeData(HttpServletRequest request, String gridId, String cacheKey, PrintWriter out, boolean removeItem) throws Exception{
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getData", new Object[]{gridId, cacheKey, out, removeItem});
        }

        String result = null;
        try {
            result = "";
            Object data = ogCache.get(cacheKey);
            if (data instanceof GridData) {
                GridData gridData = (GridData) data;
                Iterator<String> iter = gridData.getAttributeNames();
                while (iter.hasNext()) {
                    String key = iter.next();
                    request.setAttribute(key, gridData.getAttribute(key));
                    l.logp(Level.FINE, getClass().getName(), "getData", "request.setAttribute: key = " + key + "; value = " + gridData.getAttribute(key));
                }

                request.setAttribute("updateColumns", gridData.getUpdateColumns());
                BaseResultSet rs = gridData.getResultSet();
                if (gridData.isForJqxGrid()) {
                    // Render grid data.
                    JsonHelper.addObjectStartTag(out);
                    // write messages array
                    out.println("    \"message\": [],");
                    // Write the grid data
                    try {
                        JqxGridWriter.getInstance().writeGridDataForAjax(gridData.getGridInfo(), out);
                    } finally {
                        JsonHelper.addObjectEndTag(out, false);
                        out.flush();
                    }
                }
                else {
                    RecordSet recordSet = ((BaseResultSetRecordSetAdaptor)rs).getRecordSet();
                    XMLRecordSetMapper.getInstance().map(request, recordSet, gridId, out);
                }
            }
            else {
                result = String.valueOf(ogCache.get(cacheKey));
                out.print(data);
            }

            if(removeItem) {
                remove(cacheKey);
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to write the cached grid data", e);
            l.logp(Level.SEVERE, getClass().getName(), "writeData", "", e);
            out.println("{");

            // Write the error message in the standard message format, such as "message":[{message1},{message2}]
            JsonHelper.writePropertyName(out,"message", true);
            out.print("[{");
            JsonHelper.writeProperty(out, "category", MessageCategory.ERROR_MESSAGE_STRING, true);
            JsonHelper.writeProperty(out, "key", ae.getMessageKey(), true);
            JsonHelper.writeProperty(out, "text", e.getMessage() + " :: " + ae.getMessage(), true);
            JsonHelper.writeProperty(out, "confirmedasyrequired", "N", true);
            JsonHelper.writeProperty(out, "field", "", true);
            JsonHelper.writeProperty(out, "rowid", "", false);
            out.println("}],");

            out.println("\"rawData\": []");

            out.println("}");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getData", result);
        }
        return result;
    }

    /**
     * Method the removes the cached key-value pair from cache manager.
     * @param cacheKey key to find the cached data
     */
     public void remove(String cacheKey) {
        if(ogCache.contains(cacheKey)) {
            ogCache.remove(cacheKey);
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
     public void verifyConfig() {
    }

    public OasisGridCacheManager() {
        ogCache = CacheManagerImpl.getInstance().getCache(this.getClass().getName());
    }

    private Cache ogCache;
    private final Logger l = LogUtils.getLogger(getClass());
    private static OasisGridCacheManager c_instance;
}
