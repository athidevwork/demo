package dti.oasis.pageentitlementmgr;

import dti.oasis.util.StringUtils;

/**
 * This class represents the bean for pageEntitlements.xml configuration file.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 1, 2007
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
public class PageEntitlement {

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getIndFieldName() {
        return m_indFieldName;
    }

    public void setIndFieldName(String indFieldName) {
        this.m_indFieldName = indFieldName;
    }

    public IndicatorFieldLocation getIndFieldLocation() {
        return m_indFieldLocation;
    }

    public void setIndFieldLocation(String indFieldLocation) {
        m_indFieldLocation = IndicatorFieldLocation.getInstance(indFieldLocation);
    }

    public void setIndFieldLocation(IndicatorFieldLocation indFieldLocation) {
        m_indFieldLocation = indFieldLocation;
    }

    public boolean isGridIdDefined() {
        return !StringUtils.isBlank(m_gridId);
    }
    
    public String getGridId() {
        return m_gridId;
    }

    public void setGridId(String gridId) {
        m_gridId = gridId;
    }

    public EntitlementAction getAction() {
        return m_action;
    }

    public void setAction(String action) {
        m_action = EntitlementAction.getInstance(action);
    }

    public void setAction(EntitlementAction action) {
        m_action = action;
    }

    public DefaultEntitlementAction getDefaultActionForNoRows() {
        return m_defaultActionForNoRows;
    }

    public void setDefaultActionForNoRows(DefaultEntitlementAction defaultActionForNoRows) {
        m_defaultActionForNoRows = defaultActionForNoRows;
    }

    public void setDefaultActionForNoRows(String defaultActionForNoRows) {
        m_defaultActionForNoRows = DefaultEntitlementAction.getInstance(defaultActionForNoRows);
    }

    public String toString() {
        return "PageEntitlement{" +
            "m_id='" + m_id + '\'' +
            ", m_indFieldName='" + m_indFieldName + '\'' +
            ", m_indFieldLocation=" + m_indFieldLocation +
            ", m_gridId='" + m_gridId + '\'' +
            ", m_action=" + m_action +
            ", m_defaultActionForNoRows=" + m_defaultActionForNoRows +
            '}';
    }

    private String m_id;
    private String m_indFieldName;
    private IndicatorFieldLocation m_indFieldLocation;
    private String m_gridId;
    private EntitlementAction m_action;
    private DefaultEntitlementAction m_defaultActionForNoRows;
}
