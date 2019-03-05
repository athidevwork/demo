package dti.oasis.tags;

import dti.oasis.util.LogUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A LinkedHashMap that holds the layerId and description
 * of a record in pf_web_layer
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Nov 10, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 2/8/2004     jbe     Added Logging & toString
* 01/31/2006   wer     Enhanced to make storage/retrieval of fields case-insensitive.
* 01/29/2010   James   Issue#100713 Add attributes to Web Layer for setting grid sort
*                      order and grid dimensions, and migrate existing data to use this.
* 03/15/2017   ylu     183827:
*                      after JDK upgraded to 1.8, the putAll method is changed not to invoke put method finally,
*                      as a result, the dynamic generated field Id object can not be cast to bean in request,
*                      so rewrite the putAll method
* ---------------------------------------------------
*/

public class WebLayer extends LinkedHashMap {
    String layerId;
    String description;
    boolean isHidden;
    String gridSortOrder;
    String gridHeight;
    String gridContainerWidth;
    String gridContainerHeight;
    String gridPageSize;

    private Map caseSensitiveMap = new LinkedHashMap();

    public WebLayer(String id, String description, boolean isHidden) {
        super();
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[]{id, description, Boolean.valueOf(isHidden)});
        layerId = id;
        this.description = description;
        this.isHidden = isHidden;
        l.exiting(getClass().getName(), "constructor", this);
    }

    public String getLayerId() {
        return layerId;
    }

    void setLayerId(String layerId) {
        this.layerId = layerId;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getGridSortOrder() {
        return gridSortOrder;
    }

    public void setGridSortOrder(String gridSortOrder) {
        this.gridSortOrder = gridSortOrder;
    }

    public String getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(String gridHeight) {
        this.gridHeight = gridHeight;
    }

    public String getGridContainerWidth() {
        return gridContainerWidth;
    }

    public void setGridContainerWidth(String gridContainerWidth) {
        this.gridContainerWidth = gridContainerWidth;
    }

    public String getGridContainerHeight() {
        return gridContainerHeight;
    }

    public void setGridContainerHeight(String gridContainerHeight) {
        this.gridContainerHeight = gridContainerHeight;
    }

    public String getGridPageSize() {
        return gridPageSize;
    }

    public void setGridPageSize(String gridPageSize) {
        this.gridPageSize = gridPageSize;
    }

    /**
     * Override the ancestor method to make this a case-insensitive Map.
     */
    public Object get(Object key) {
        key = ((String)key).toUpperCase(); // Make this a case-insensitive Map
        return super.get(key);
    }

    /**
     * Override the ancestor method to make this a case-insensitive Map.
     */
    public Object put(Object key, Object value) {
        caseSensitiveMap.put(key, value);   // Store the case-sensitive key/value
        key = ((String)key).toUpperCase();  // Make this a case-insensitive Map
        return super.put(key, value);
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.  A <tt>null</tt> return can
     *         also indicate that the map previously associated <tt>null</tt>
     *         with the specified key.
     */
    public Object remove(Object key) {

        caseSensitiveMap.remove(key);   // Remove the case-sensitive key/value

        key = ((String)key).toUpperCase(); // Make this a case-insensitive Map
        return super.remove(key);
    }

    /**
     * Override the ancestor method to return the case-sensitive keys.
     */
    public Set keySet() {
        return caseSensitiveMap.keySet();
    }

    /**
     * Override the ancestor method to use the case-sensitive map.
     */
    public boolean containsKey(Object key) {
        return caseSensitiveMap.containsKey(key);
    }

    /**
     * Override the ancestor method to store in both case-sensitive and case-insensitive maps.
     */
    public void putAll(Map m) {
        for (Object obj : m.keySet()) {
            this.put(obj, m.get(obj));
        }
        caseSensitiveMap.putAll(m);
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        caseSensitiveMap.clear();
        super.clear();
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString()).append(" ::: ");
        buf.append("WebLayer");
        buf.append("{layerId=").append(layerId);
        buf.append(",description=").append(description);
        buf.append(",isHidden=").append(isHidden);
        buf.append(",gridSortOrder=").append(gridSortOrder);
        buf.append(",gridHeight=").append(gridHeight);
        buf.append(",gridContainerWidth=").append(gridContainerWidth);
        buf.append(",gridContainerHeight=").append(gridContainerHeight);
        buf.append(",gridPageSize=").append(gridPageSize);
        buf.append('}');
        return buf.toString();

    }

    /*
    *  Deep clone() method
    *
    * */
    @Override
    public Object clone() {
        WebLayer cloned = (WebLayer)super.clone();

        //Clears LinkedHashMap to avoid double count
        cloned.caseSensitiveMap = new LinkedHashMap();

        Iterator it = caseSensitiveMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object newValue = null;
            if(value instanceof OasisFormField) {
                newValue = ((OasisFormField) value).clone();
            }

            if(newValue!=null){
                cloned.put(key, newValue);
            }
        }

        return cloned;
    }
}
