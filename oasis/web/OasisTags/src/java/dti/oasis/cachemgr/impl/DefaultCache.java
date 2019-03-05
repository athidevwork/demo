package dti.oasis.cachemgr.impl;

import dti.oasis.cachemgr.Cache;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;

/**
 * This class implements the cache interface and provides implementation detail for caching technique.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 1, 2006
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  03/18/2008      wer         Added ability to get the size and iterate through the keys.
 * ---------------------------------------------------
 */
public class DefaultCache implements Cache {

    /**
     * Stores the given value in the cache, maped to the specified key. Neither the key or the value can be null.
     */
    public void put(Object key, Object value) {
        m_cache.put(key, value);
    }

    /**
     * Return the value from the cache associated with the key.
     *
     * @throws IllegalArgumentException if there is not an object in the cache keyed by the provided cache key.
     */
    public Object get(Object key) {
        if(m_cache.get(key)==null){
            throw new IllegalArgumentException("Unable to retrieve the cache object value for the provided 'key'"+String.valueOf(key));
        } else {
            return m_cache.get(key);
        }
    }

    /**
     * Remove the cached object matching the provided cache key if it is found.
     * If the object does not exist in the cache, this method does nothing.
     */
    public void remove(Object key) {
        m_cache.remove(key);
    }

    /**
    * Returns a boolean value that indicates the existence of an object in the cache, maped to the provided key.
    */
   public boolean contains(Object key) {
        return m_cache.containsKey(key);
    }

    /**
     * Returns the number of items in this cache
     */
    public int getSize() {
        return m_cache.size();
    }


    /**
     * Return an Iterator of key names in this cache.
     */
    public Iterator keyIterator() {
        return m_cache.keySet().iterator();
    }

    /**
     * Clear any cached data.
     */
    public void clear() {
        m_cache.clear();
    }

    public String toString() {
        return "DefaultCache{" +
            "m_cache=" + m_cache +
            '}';
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public DefaultCache() {
    }

    private Map m_cache = new Hashtable();
}
