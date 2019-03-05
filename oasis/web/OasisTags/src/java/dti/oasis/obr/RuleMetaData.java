package dti.oasis.obr;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RuleMetaData class provides meta data for a rule
 * 
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/19/2014       jxgu        Issue#158594 OBR cache header fields if it is accessed
 * ---------------------------------------------------
 */
public class RuleMetaData {

    /**
     *
     * @param recordType
     */
    public void setUpdateIndicator(String recordType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setUpdateIndicator", new Object[]{recordType});
        }

        if (Record.TYPE_NONGRID.equals(recordType))
            setOBREnforcingNonGridUpdateIndicator(true);
        else if (Record.TYPE_GRID.equals(recordType))
            setOBREnforcingGridUpdateIndicator(true);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "setUpdateIndicator");
        }
    }

    /**
     * add enforcing field id
     * @param fieldId
     * @param recordType
     */
    public void addEnforcingFieldId(String fieldId, String recordType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEnforcingFieldId", new Object[]{fieldId, recordType});
        }
        addEnforcingFieldId(fieldId, recordType, false);
        if (l.isLoggable(Level.FINER)) {
          l.exiting(getClass().getName(), "addEnforcingFieldId");
        }
    }

    /**
     * add enforcing field id
     * @param fieldId
     * @param recordType
     * @param isOriginalField
     */
    public void addEnforcingFieldId(String fieldId, String recordType, boolean isOriginalField) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEnforcingFieldId", new Object[]{fieldId, recordType, Boolean.toString(isOriginalField)});
        }
        if (Record.TYPE_NONGRID.equals(recordType)) {
            m_nonGridEnforcingFieldSet.add(fieldId);
        } else if (Record.TYPE_GRID.equals(recordType)) {
            m_gridEnforcingFieldSet.add(fieldId);
        } else {
            m_headerEnforcingFieldSet.add(fieldId);
        }
        m_allAccessedFieldSet.add(fieldId);
        if (isOriginalField) {
          m_originalFieldSet.add(fieldId);
        }
        l.exiting(getClass().getName(), "addEnforcingFieldId");
    }

    /**
     * add consequence field id
     * @param fieldId
     */
    public void addConsequenceFieldId(String fieldId) {
        addConsequenceFieldId(fieldId, false);
    }

    /**
     * add consequence field id
     * @param fieldId
     * @param isOriginalField
     */
    public void addConsequenceFieldId(String fieldId, boolean isOriginalField) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addConsequenceFieldId", new Object[]{fieldId});
        }
        m_consequenceFieldSet.add(fieldId);
        m_allAccessedFieldSet.add(fieldId);
        if (isOriginalField) {
          m_originalFieldSet.add(fieldId);
        }
        l.exiting(getClass().getName(), "addConsequenceFieldId");
    }

    /**
     * get related system parameters set
     * @return
     */
    public Set<String> getRelatedSystemParamSet() {
        return m_relatedSystemParamSet;
    }

    public Set<String> getRelatedProfileSet() {
        return m_relatedProfileSet;
    }

    /**
     * get set of enforcing field ids
     *
     * @return
     */
    public Set<String> getEnforcingFieldSet() {
        l.entering(getClass().getName(), "getEnforcingFieldSet");
        Set<String> enforcingFieldSet = new HashSet<String>();
        enforcingFieldSet.addAll(m_headerEnforcingFieldSet);
        enforcingFieldSet.addAll(m_nonGridEnforcingFieldSet);
        enforcingFieldSet.addAll(m_gridEnforcingFieldSet);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEnforcingFieldSet", enforcingFieldSet);
        }
        return enforcingFieldSet;
    }

    /**
     * add record
     * @param recordType
     */
    public void addRecord(String recordType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addRecord", new Object[]{recordType});
        }
        if (Record.TYPE_NONGRID.equals(recordType)) {
            m_nonGridRecordCount++;
        } else if (Record.TYPE_GRID.equals(recordType)) {
            m_gridRecordCount++;
        } else {
            m_headerRecordCount++;
        }
        l.exiting(getClass().getName(), "addRecord");
    }

    /**
     * get set of consequence field
     *
     * @return
     */
    public Set<String> getConsequenceFieldSet() {
        return m_consequenceFieldSet;
    }

    /**
     * get all accessed field list
     * @return
     */
    public Set<String> getAllAccessedFieldSet() {
        return m_allAccessedFieldSet;
    }

    /**
     * get all accessed original field list
     * @return
     */
    public Set<String> getOriginalFieldSet() {
        return m_originalFieldSet;
    }

    /**
     * get all accessed field list
     * @return
     */
    public boolean hasOriginalField(String originalFieldId) {
        return m_originalFieldSet.contains(originalFieldId);
    }

    public boolean isAccessOriginal() {
        return (m_originalFieldSet.size() > 0);
    }

    public boolean isOnLoadAddOrChangeEvent() {
        return m_OnLoadAddOrChangeEvent;
    }

    public void setOnLoadAddOrChangeEvent(boolean isOnLoadAddOrChangeEvent) {
        this.m_OnLoadAddOrChangeEvent = isOnLoadAddOrChangeEvent;
    }

    public boolean isOnSaveEvent() {
        return m_OnSaveEvent;
    }

    public void setOnSaveEvent(boolean isOnSaveEvent) {
        this.m_OnSaveEvent = isOnSaveEvent;
    }

    public String getRuleId() {
        return m_ruleId;
    }

    public void setRuleId(String ruleId) {
        this.m_ruleId = ruleId;
    }

    public int getNonGridRecordCount() {
        return m_nonGridRecordCount;
    }

    public int getGridRecordCount() {
        return m_gridRecordCount;
    }

    public int getHeaderRecordCount() {
        return m_headerRecordCount;
    }

    public Set<String> getHeaderEnforcingFieldSet() {
        return m_headerEnforcingFieldSet;
    }

    public Set<String> getNonGridEnforcingFieldSet() {
        return m_nonGridEnforcingFieldSet;
    }

    public Set<String> getGridEnforcingFieldSet() {
        return m_gridEnforcingFieldSet;
    }

    public void addFieldIdInSetValueMethod(String methodName, String fieldId) {
        m_setValueFieldSet.add(fieldId);
        if ("setValue".equals(methodName)) {
            m_setValueMethod = true;
        } else if ("setRecordValue".equals(methodName)) {
            m_setRecordValueMethod = true;
        }
    }

    public Set<String> getFieldIdSetInSetValueMethod() {
        return m_setValueFieldSet;
    }

    public boolean hasSetValueMethod() {
        return m_setValueMethod;
    }

    public boolean hasSetRecordValueMethod() {
        return m_setRecordValueMethod;
    }

    public boolean hasConditionDisplayIndicator() {
        return m_hasConditionDisplayIndicator;
    }

    public void setConditionDisplayIndicator(boolean hasConditionDisplayIndicator) {
        this.m_hasConditionDisplayIndicator = hasConditionDisplayIndicator;
    }

    public boolean hasActionHideRow() {
        return m_hasActionHideRow;
    }

    public void setActionHideRow(boolean hasActionHideRow) {
        this.m_hasActionHideRow = hasActionHideRow;
    }

    public boolean isAccessHeaderRecord() {
        return m_accessHeaderRecord;
    }

    public void setAccessHeaderRecord(boolean accessHeaderRecord) {
        m_accessHeaderRecord = accessHeaderRecord;
    }

    public boolean isOBREnforcingNonGridUpdateIndicator() { return m_OBREnforcingNonGridUpdateIndicator; }

    public void setOBREnforcingNonGridUpdateIndicator(boolean OBREnforcingNonGridUpdateIndicator) {
        m_OBREnforcingNonGridUpdateIndicator = OBREnforcingNonGridUpdateIndicator;
    }

    public boolean isOBREnforcingGridUpdateIndicator() { return m_OBREnforcingGridUpdateIndicator; }

    public void setOBREnforcingGridUpdateIndicator(boolean OBREnforcingGridUpdateIndicator) {
        m_OBREnforcingGridUpdateIndicator = OBREnforcingGridUpdateIndicator;
    }

    private Set<String> m_relatedSystemParamSet = new HashSet<String>();
    private Set<String> m_relatedProfileSet = new HashSet<String>();
    private Set<String> m_headerEnforcingFieldSet = new HashSet<String>();
    private Set<String> m_nonGridEnforcingFieldSet = new HashSet<String>();
    private Set<String> m_gridEnforcingFieldSet = new HashSet<String>();
    private Set<String> m_consequenceFieldSet = new HashSet<String>();
    private Set<String> m_allAccessedFieldSet = new HashSet<String>();
    private Set<String> m_setValueFieldSet = new HashSet<String>();
    private Set<String> m_originalFieldSet = new HashSet<String>();
    private boolean m_accessOriginal = false;
    private String m_ruleId = null;
    private boolean m_OnLoadAddOrChangeEvent = false;
    private boolean m_OnSaveEvent = false;
    private int m_nonGridRecordCount = 0;
    private int m_gridRecordCount = 0;
    private int m_headerRecordCount = 0;
    private boolean m_setValueMethod = false;
    private boolean m_setRecordValueMethod = false;
    private boolean m_hasConditionDisplayIndicator = false;
    private boolean m_OBREnforcingGridUpdateIndicator = false;
    private boolean m_OBREnforcingNonGridUpdateIndicator = false;
    private boolean m_hasActionHideRow = false;
    private boolean m_accessHeaderRecord = false;
    private final Logger l = LogUtils.getLogger(getClass());

}