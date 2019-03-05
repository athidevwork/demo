package dti.oasis.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: huixu
 * Date: 10/16/13
 * Time: 4:24 PM
 * This class provides a implements for LinkedHashMap with a maxCacheSize.
 * when maxCacheSize == -1. It means no max Cache limit for the new MaxSizeHashMap
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/01/2014       Parker      148029 - Cache risk header, coverage header and policy navigation information to policy header.
 *
 * ---------------------------------------------------
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {

    int maxCacheSize;

    public MaxSizeHashMap() {
        this.maxCacheSize = -1;
    }

    public MaxSizeHashMap(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean removable = false;
        if (getMaxCacheSize() != -1 && this.size() > getMaxCacheSize()) {
            removable = true;
        }
        return removable;
    }
}
