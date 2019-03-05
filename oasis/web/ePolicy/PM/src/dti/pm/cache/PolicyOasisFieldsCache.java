package dti.pm.cache;

import dti.oasis.app.AppException;
import dti.oasis.tags.OasisFields;
import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>(C) 2012 Delphi Technology, inc. (dti)</p>
 * Date:   10/11/13
 *
 * @author mgitelman
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PolicyOasisFieldsCache {

    /**
     * Returns true if the specified value exists in the cache for the current user.
     * @param key
     * @return boolean
     */
    public boolean has(String key) {
        Logger l = LogUtils.enterLog(getClass(), "has", new Object[]{key});

        boolean valueExists = true;
        if (!getCachedOasisFields().containsKey(key)) {
            valueExists = false;
        }
        l.exiting(getClass().getName(), "has [key=" + key + "]", String.valueOf(valueExists));

        return valueExists;
    }

    /**
     * Returns the value specified by the given key for the current user.
     *
     * @param key
     * @return Object
     * @throws AppException if the key is not found
     */
    public OasisFields get(String key) {
        Logger l = LogUtils.enterLog(getClass(), "get", new Object[]{key});

        OasisFields oasisFields = getCachedOasisFields().get(key);
        if (oasisFields == null) {
            AppException e = new AppException("The PolicyOasisFieldsCache for policyId'"+this.getPolicyId()+"' does not contain a value for the key'"+key+"'.");
            l.throwing(getClass().getName(), "get", e);
        }

        l.exiting(getClass().getName(), "get [key=" + key + "]", oasisFields);

        return oasisFields;
    }

    public Iterator keyIterator() {
        return m_cachedOasisFields.keySet().iterator();
    }

    /**
     * Sets the key/value pair, for the current user.
     *
     * @param key
     * @param value
     */
    public void put(String key, OasisFields value) {
        Logger l = LogUtils.enterLog(getClass(), "put", new Object[]{key, value});

        getCachedOasisFields().put(key, value);

        l.exiting(getClass().getName(), "put [key=" + key + "]");
    }

    /**
     * Remove the cached object matching the provided cache key if it is found.
     * If the object does not exist in the cache, this method does nothing.
     */
    public void remove(Object key) {
        m_cachedOasisFields.remove(key);
    }

    /**
     * Returns a boolean value that indicates the existence of an object in the cache, maped to the provided key.
     */
    public boolean contains(Object key) {
        return m_cachedOasisFields.containsKey(key);
    }

    /**
     * Returns the number of items in this cache
     */
    public int getSize() {
        return m_cachedOasisFields.size();
    }

    /**
     * Clear any cached data.
     */
    public void clear() {
        m_cachedOasisFields.clear();
    }

    public Map<String, OasisFields> getCachedOasisFields() {
        return m_cachedOasisFields;
    }

    public void setCachedOasisFields(Map<String, OasisFields> cachedOasisFields) {
        m_cachedOasisFields = cachedOasisFields;
    }

    public String getPolicyId() {
        return m_policyId;
    }

    public void setPolicyId(String policyId) {
        m_policyId = policyId;
    }

    /**
     * Constructor
     *  @param policyId
     *
     * */
    public PolicyOasisFieldsCache(String policyId) {
        m_policyId = policyId;
    }
    
    private String m_policyId;
    private Map<String, OasisFields> m_cachedOasisFields = new ConcurrentHashMap<String, OasisFields>();

}
