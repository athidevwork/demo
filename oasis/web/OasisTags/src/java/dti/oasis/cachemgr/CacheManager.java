package dti.oasis.cachemgr;

import dti.oasis.app.ApplicationContext;
import dti.oasis.cachemgr.impl.CacheManagerImpl;

import java.util.Iterator;

/**
 * This class provides abstract information about methods used for implementing caching behavior. Each cache object is
 * associated with a cache name. The getCache method will automatically create a cache object, if one does not exists;
 * otherwise, will return the cache object associated with the cache name.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 17, 2006
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
public abstract class CacheManager {

    public static final String BEAN_NAME = "CacheManager";

    /**
     * Returns a synchronized static instance of cache manager that has the implementation information.
     * @return CacheManager, an instance of cache manager with implemenation information.
     */
    public synchronized static final CacheManager getInstance() {
        if (CacheManager.c_instance == null) {
            if (ApplicationContext.getInstance().hasBean(CacheManager.BEAN_NAME)) {
                CacheManager.c_instance = (CacheManager) ApplicationContext.getInstance().getBean(CacheManager.BEAN_NAME);
            }
            else{
                CacheManager.c_instance = new CacheManagerImpl();
            }
        }
        return CacheManager.c_instance;
    }

    /**
     * Gets the cache object associated with the cache name.
     * @param cacheName name of the cache
     * @return Cache object for the provided cache name
     */
    public abstract Cache getCache(String cacheName);

    /**
     * Removes the cache object associated with the given cache name
     * @param cacheName name of the cache
     */
    public abstract void removeCache(String cacheName);

    /**
     * Removes all cache objects with a cache name containing the given cacheNameSubstring
     * @param cacheNameSubstring the substring portion of a cache name
     */
    public abstract void removeAllCache(String cacheNameSubstring);

    private static CacheManager c_instance;
}
