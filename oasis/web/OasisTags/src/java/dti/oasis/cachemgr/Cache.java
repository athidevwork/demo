package dti.oasis.cachemgr;

import java.util.Iterator;

/**
 * This interface represents a class that implements the cache techinque.
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
public interface Cache {

    /**
     * Stores the given value in the cache, maped to the specified key. Neither the key or the value can be null.
     */
    void put(Object key, Object value);

    /**
     * Return the value from the cache associated with the key.
     *
     * @throws IllegalArgumentException if there is not an object in the cache keyed by the provided cache key.
     */
    Object get(Object key) ;

    /**
     * Remove the cached object matching the provided cache key if it is found.
     * If the object does not exist in the cache, this method does nothing.
     */
    void remove(Object key);

    /**
    * Returns a boolean value that indicates the existence of an object in the cache, maped to the provided key.
    */
    boolean contains(Object key);

    /**
     * Returns the number of items in this cache.
     */
    int getSize();

    /**
     * Return an Iterator of key names in this cache.
     */
    Iterator keyIterator();

    /**
     * Clear any cached data.
     */
    void clear();
}
