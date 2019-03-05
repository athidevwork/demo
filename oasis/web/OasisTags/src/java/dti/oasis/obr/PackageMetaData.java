package dti.oasis.obr;

import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RuleMetaData class provides meta data for a package
 *
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 30, 2011
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
public class PackageMetaData{

    /**
     * get related system parameter list
     * @return
     */
    public List<String> getRelatedSystemParamList() {
        l.entering(getClass().getName(), "getRelatedSystemParamList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getRelatedSystemParamSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelatedSystemParamList", list);
        }
        return list;
    }

    /**
     * get related profile list
     * @return
     */
    public List<String> getRelatedProfileList() {
        l.entering(getClass().getName(), "getRelatedProfileList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getRelatedProfileSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getRelatedProfileList", list);
        }
        return list;
    }

    /**
     * get enforcing field list
     * @return
     */
    public List<String> getEnforcingFieldList() {
        l.entering(getClass().getName(), "getEnforcingFieldList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getEnforcingFieldSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEnforcingFieldList", list);
        }
        return list;
    }

    /**
     * get enforcing field list
     * @return
     */
    public List<String> getEnforcingFieldList(String recordType) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEnforcingFieldList", new Object[]{recordType});
        }
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            if (Record.TYPE_HEADER.equals(recordType)) {
                copyNotExistItem(list, ruleMetaData.getHeaderEnforcingFieldSet());
            } else if (Record.TYPE_NONGRID.equals(recordType)) {
                copyNotExistItem(list, ruleMetaData.getNonGridEnforcingFieldSet());
            } else if (Record.TYPE_GRID.equals(recordType)) {
                copyNotExistItem(list, ruleMetaData.getGridEnforcingFieldSet());
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEnforcingFieldList", list);
        }
        return list;
    }

    /**
     * get consequence field list
     * @return
     */
    public List<String> getConsequenceFieldList() {
        l.entering(getClass().getName(), "getConsequenceFieldList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getConsequenceFieldSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getConsequenceFieldList", list);
        }
        return list;
    }

    /**
     * get all accessed field list
     * @return
     */
    public List<String> getAllAccessedFieldList() {
        l.entering(getClass().getName(), "getAllAccessedFieldList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getAllAccessedFieldSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getAllAccessedFieldList", list);
        }
        return list;
    }

    /**
     * get all original field list
     * @return
     */
    public List<String> getOriginalFieldList() {
        l.entering(getClass().getName(), "getOriginalFieldList");
        List<String> list = new ArrayList<String>();
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            copyNotExistItem(list, ruleMetaData.getOriginalFieldSet());
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getOriginalFieldList", list);
        }
        return list;
    }

    /**
     * Validates if update indicator is set on grid field.
     * @return
     */
    public boolean isGridUpdateIndSet() {
        l.entering(getClass().getName(), "isGridUpdateIndSet");
        boolean isGridUpdateIndSet = false;
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            if (ruleMetaData.isOBREnforcingGridUpdateIndicator()) {
                isGridUpdateIndSet = true;
                break;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isGridUpdateIndicatorSet", new Object[]{isGridUpdateIndSet});
        }
        return isGridUpdateIndSet;
    }


    /**
     * Validates if update indicator is set on non grid field.
     * @return
     */
    public boolean isNonGridUpdateIndSet() {
        l.entering(getClass().getName(), "isNonGridUpdateIndSet");
        boolean isNonGridUpdateIndSet = false;
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            if (ruleMetaData.isOBREnforcingNonGridUpdateIndicator()) {
                isNonGridUpdateIndSet = true;
                break;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isNonGridUpdateIndicatorSet", new Object[]{isNonGridUpdateIndSet});
        }
        return isNonGridUpdateIndSet;
    }

    /**
     * whether access original
     * @return
     */
    public boolean isAccessOriginal() {
        l.entering(getClass().getName(), "isAccessOriginal");
        boolean isAccessOriginal = false;
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            if (ruleMetaData.isAccessOriginal()) {
                isAccessOriginal = true;
                break;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAccessOriginal", new Object[]{isAccessOriginal});
        }
        return isAccessOriginal;
    }

    /**
     * whether access header record
     * @return
     */
    public boolean isAccessHeaderRecord() {
        l.entering(getClass().getName(), "isAccessHeaderRecord");
        boolean isAccessHeaderRecord = false;
        for (RuleMetaData ruleMetaData : m_ruleMetaDataMap.values()) {
            if (ruleMetaData.isAccessHeaderRecord()) {
                isAccessHeaderRecord = true;
                break;
            }
        }
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isAccessHeaderRecord", new Object[]{isAccessHeaderRecord});
        }
        return isAccessHeaderRecord;
    }


    /**
     * add RuleMetaData
     */
    public void addRuleMetaData(String ruleId, RuleMetaData ruleMetaData) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addRuleMetaData", new Object[]{ruleId, ruleMetaData});
        }
        if (!m_ruleMetaDataMap.containsKey(ruleId)) {
            m_ruleMetaDataMap.put(ruleId, ruleMetaData);
        }
        l.exiting(getClass().getName(), "addRuleMetaData");
    }


    /**
     * copy items from one list to another, ignore the item which already exist in target list
     *
     * @param toList
     * @param fromSet
     */
    protected void copyNotExistItem(List<String> toList, Set<String> fromSet) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "copyNotExistItem", new Object[]{toList, fromSet});
        }
        for (String item : fromSet) {
            if (!toList.contains(item)) {
                toList.add(item);
            }
        }
        l.exiting(getClass().getName(), "copyNotExistItem");
    }

    public boolean isValidateComplete() {
        return validateComplete;
    }

    public void setValidateComplete(boolean validateComplete) {
        this.validateComplete = validateComplete;
    }

    public Map<String, RuleMetaData> getRuleMetaDataMap() {
        return m_ruleMetaDataMap;
    }

    private Map<String, RuleMetaData> m_ruleMetaDataMap = new HashMap<String, RuleMetaData>();
    private boolean validateComplete = false;

    public boolean hasRuleForSave() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hasRuleForSave");
        }
        boolean hasSaveRule = false;
        Iterator it = m_ruleMetaDataMap.keySet().iterator();
        while (it.hasNext()) {
            String ruleKey = (String) it.next();
            RuleMetaData ruleMetaData = (RuleMetaData) m_ruleMetaDataMap.get(ruleKey);
            if (ruleMetaData.isOnSaveEvent()) {
                hasSaveRule = true;
                break;
            }
        }
        l.exiting(getClass().getName(), "hasRuleForSave", hasSaveRule);
        return hasSaveRule;
    }
    private final Logger l = LogUtils.getLogger(getClass());
}