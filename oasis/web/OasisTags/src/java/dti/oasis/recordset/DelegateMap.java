package dti.oasis.recordset;

import dti.oasis.util.LogUtils;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class extends LinkedHashMap.
 * It overrides method get. Return a Field object with a null value if the super.get() returns null
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Api 22, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/12/2018       wreeder     196160 - Ensure the order of contents match the input order so iterating through fields is consistent with iterating through field names in a Record
 * ---------------------------------------------------
 */
public class DelegateMap extends LinkedHashMap {

    public DelegateMap() {
    }

    public DelegateMap(boolean useForRule) {
        m_useForRule = useForRule;
    }

    public DelegateMap(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public Object get(Object key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "get", new Object[]{key});
        }
        Object object = null;
        if (isUseForRule()) {
            object = getObjectForRule(key);
        } else {
            object = super.get(key);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "get", object);
        }
        return object;
    }

    /**
     * get object, it is only used for rule
     * @param key
     * @return
     */
    public Object getObjectForRule(Object key) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getObjectForRule", new Object[]{key});
        }

        String keyString = ((String) key).toUpperCase();
        Object object = super.get(keyString);

        // if object not found, try to remove grid header suffix if exists
        if (object == null) {
            keyString = Record.stripGHSuffix(keyString);
            object = super.get(keyString);
        }

        // if object not found, try the key without prefix if m_tryFieldIdWithoutPrefix is true
        if (object == null && isTryFieldIdWithoutPrefix()) {
            keyString = Record.stripTablePrefix(keyString);
            object = super.get(keyString);
        }

        if (object == null) {
            object = new Field(null);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getObjectForRule", object);
        }
        return object;
    }

    public boolean isTryFieldIdWithoutPrefix() {
        return m_tryFieldIdWithoutPrefix;
    }

    public void setTryFieldIdWithoutPrefix(boolean tryFieldIdWithoutPrefix) {
        this.m_tryFieldIdWithoutPrefix = tryFieldIdWithoutPrefix;
    }

    public boolean isUseForRule() {
        return m_useForRule;
    }

    public void setUseForRule(boolean useForRule) {
        this.m_useForRule = useForRule;
    }

    private boolean m_tryFieldIdWithoutPrefix = false;

    private boolean m_useForRule = false;
    private final Logger l = LogUtils.getLogger(getClass());
}
