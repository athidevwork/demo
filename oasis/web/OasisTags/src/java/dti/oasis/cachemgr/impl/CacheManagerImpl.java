package dti.oasis.cachemgr.impl;

import dti.oasis.cachemgr.Cache;
import dti.oasis.cachemgr.CacheManager;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;


/**
 * This class extends the abstract cache manager to provide implementation detail.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 16, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CacheManagerImpl extends CacheManager {

    /**
     * Gets the cache object associated with the cache name. If the cache object does not exists, it will automatically
     * create a default cache object and returns it; otherwise, the associated cache object will be returned.
     *
     * @param cacheName Name of the cache
     * @return Object Cache object for the provided cache name
     */
    public Cache getCache(String cacheName) {
        if(m_cacheManager.containsKey(cacheName)){
            return (Cache) m_cacheManager.get(cacheName);
        } else {
            m_cacheManager.put(cacheName, new DefaultCache());
            return (Cache) m_cacheManager.get(cacheName);
        }
    }

    /**
     * Removes the cache object associated with the given cache name
     *
     * @param cacheName name of the cache
     */
    public void removeCache(String cacheName) {
        m_cacheManager.remove(cacheName);
    }


    /**
     * Removes all cache objects with a cache name containing the given cacheNameSubstring
     *
     * @param cacheNameSubstring the substring portion of a cache name
     */
    public void removeAllCache(String cacheNameSubstring) {
        Iterator iter = m_cacheManager.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String cacheName = (String) entry.getKey();
            if (cacheName.indexOf(cacheNameSubstring) >= 0) {
                iter.remove();
            }
        }
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public CacheManagerImpl() {
    }

    private static Map m_cacheManager = new Hashtable();
}
