package dti.oasis.obr;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageCategory;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;
import org.drools.event.rule.ActivationEvent;
import org.drools.runtime.rule.FactHandle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Context class provides objects that can be accessed in rule
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 29, 2011
 *
 * @author jxgu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 01/11/2012       jxgu        Issue#128994 Modify Context.setRecordValue. If field id doesn't exist
 *                              in record and field id without prefix exists iin record, use the field
 *                              id without prefix.
 * 08/03/2012       jxgu        Issue#134837 added method getFieldThatTriggeredChangeEvent
 * 11/17/2015       Elvin       Issue 167139: add parameter messageRowId when addVerbatimMessage
 * ---------------------------------------------------
 */
public class Context {

    public static final String OBR_ENFORCED_RESULT_MAP = "OBR_ENFORCED_RESULT_MAP";

    public static final Set<String> METHODS_FOR_SAVE_EVENT = new HashSet<String>();

    static {
        METHODS_FOR_SAVE_EVENT.add("print");
        METHODS_FOR_SAVE_EVENT.add("log");
        METHODS_FOR_SAVE_EVENT.add("addErrorMessage");
        METHODS_FOR_SAVE_EVENT.add("addWarningMessage");
        METHODS_FOR_SAVE_EVENT.add("addInfoMessage");
        METHODS_FOR_SAVE_EVENT.add("setValue");
        METHODS_FOR_SAVE_EVENT.add("setRecordValue");
        METHODS_FOR_SAVE_EVENT.add("removeRow");
        METHODS_FOR_SAVE_EVENT.add("hideRow");
    }

    /**
     * get system paramter
     *
     * @param paramCode
     * @return
     */
    public String getSystemParameter(String paramCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getSystemParameter", new Object[]{paramCode});
        }
        String paramValue = SysParmProvider.getInstance().getSysParm(paramCode);
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getSystemParameter", paramValue);
        }
        return paramValue;
    }

    /**
     * print a message
     * @param message
     * @return
     */
    public void print(String message) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "print", new Object[]{message});
        }
        System.out.println(message);
        l.exiting(getClass().getName(), "print");
    }

    /**
     * log a message
     * @param message
     * @return
     */
    public void log(String message) {
        l.logp(Level.INFO, getClass().getName(), "log", "message = " + message);
    }

    /**
     * add error message
     * @param message
     */
    public void addErrorMessage(String message) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addErrorMessage", new Object[]{message});
        }
        MessageManager.getInstance().addVerbatimMessage(message, MessageCategory.ERROR);
        l.exiting(getClass().getName(), "addErrorMessage");
    }

    /**
     * add error message with messageRowId
     *
     * @param message
     * @param messageRowId
     */
    public void addErrorMessage(String message, String messageRowId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addErrorMessage", new Object[]{message, messageRowId});
        }
        MessageManager.getInstance().addVerbatimMessage(message, messageRowId, MessageCategory.ERROR);
        l.exiting(getClass().getName(), "addErrorMessage");
    }

    /**
     * add warning message
     * @param message
     */
    public void addWarningMessage(String message) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addWarningMessage", new Object[]{message});
        }
        MessageManager.getInstance().addVerbatimMessage(message, MessageCategory.WARNING);
        l.exiting(getClass().getName(), "addWarningMessage");
    }

    /**
     * add info message
     * @param message
     */
    public void addInfoMessage(String message) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addInfoMessage", new Object[]{message});
        }
        MessageManager.getInstance().addVerbatimMessage(message, MessageCategory.INFORMATION);
        l.exiting(getClass().getName(), "addInfoMessage");
    }

    /**
     * get the number of error messages.
     * @return
     */
    public int getErrorMessageCount() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getErrorMessageCount");
        }
        int count = MessageManager.getInstance().getErrorMessageCount();
        l.exiting(getClass().getName(), "getErrorMessageCount", count);
        return count;
    }

    /**
     * get the number of warning messages.
     * @return
     */
    public int getWarningMessageCount() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getWarningMessageCount");
        }
        int count = MessageManager.getInstance().getWarningMessageCount();
        l.exiting(getClass().getName(), "getWarningMessageCount", count);
        return count;
    }

    /**
     * get the number of info messages.
     * @return
     */
    public int getInfoMessageCount() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInfoMessageCount");
        }
        int count = MessageManager.getInstance().getInfoMessageCount();
        l.exiting(getClass().getName(), "getInfoMessageCount", count);
        return count;
    }

    /**
     * get the number of all messages
     * @return
     */
    public int getMessageCount() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getInfoMessageCount");
        }
        int count = getErrorMessageCount() + getWarningMessageCount() + getInfoMessageCount();
        l.exiting(getClass().getName(), "getInfoMessageCount", count);
        return count;
    }

    /**
     * set hidden
     * @param fieldId
     */
    public void setHidden(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setHidden", new Object[]{fieldId});
        }
        setStyle(fieldId,"display", "none");
        l.exiting(getClass().getName(), "setHidden");
    }

    /**
     * set visible
     * @param fieldId
     */
    public void setVisible(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setVisible", new Object[]{fieldId});
        }
        setStyle(fieldId,"display", "inline");
        l.exiting(getClass().getName(), "setVisible");
    }

    /**
     * set a grid column to hide.
     * @param fieldId
     */
    public void setGridColumnHidden(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGridColumnHidden", new Object[]{fieldId});
        }
        setStyle(fieldId,"display", "none", true, false);
        l.exiting(getClass().getName(), "setGridColumnHidden");
    }

    /**
     * set a grid column to visible
     * @param fieldId
     */
    public void setGridColumnVisible(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGridColumnVisible", new Object[]{fieldId});
        }
        setStyle(fieldId,"display", "inline", true, false);
        l.exiting(getClass().getName(), "setGridColumnVisible");
    }

  /**
   * set a panel to collapse.
   * @param fieldId
   */
  public void setPanelCollapsed(String fieldId) {
      if (l.isLoggable(Level.FINER)) {
          l.entering(getClass().getName(), "setPanelCollapsed", new Object[]{fieldId});
      }
      setStyle(fieldId,"display", "none", false, true);
      l.exiting(getClass().getName(), "setPanelCollapsed");
  }

  /**
   * set a panel to expand.
   * @param fieldId
   */
  public void setPanelExpanded(String fieldId) {
      if (l.isLoggable(Level.FINER)) {
          l.entering(getClass().getName(), "setPanelExpanded", new Object[]{fieldId});
      }
      setStyle(fieldId,"display", "block", false, true);
      l.exiting(getClass().getName(), "setPanelExpanded");
  }

  /**
   * set a grid panel to collapse.
   * @param fieldId
   */
  public void setGridPanelCollapsed(String fieldId) {
      if (l.isLoggable(Level.FINER)) {
          l.entering(getClass().getName(), "setGridPanelCollapsed", new Object[]{fieldId});
      }
      setStyle(fieldId,"display", "none", true, true);
      l.exiting(getClass().getName(), "setGridPanelCollapsed");
  }

    /**
     * set a grid panel to expand.
     * @param fieldId
     */
    public void setGridPanelExpanded(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setGridPanelExpanded", new Object[]{fieldId});
        }
        setStyle(fieldId,"display", "block", true, true);
        l.exiting(getClass().getName(), "setGridPanelExpanded");
    }


    /**
     * set bold
     * @param fieldId
     */
    public void setBold(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setBold", new Object[]{fieldId});
        }
        setStyle(fieldId,"fontWeight", "bold");
        l.exiting(getClass().getName(), "setBold");

    }

    /**
     * set readonly
     *
     * @param fieldId
     */
    public void setReadonly(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setReadonly", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "readonly", "Y");
        l.exiting(getClass().getName(), "setReadonly");
    }

    /**
     * set editable (non readonly)
     *
     * @param fieldId
     */
    public void setEditable(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEditable", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "readonly", "N");
        l.exiting(getClass().getName(), "setEditable");
    }

    /**
     * set disabled
     *
     * @param fieldId
     */
    public void setDisabled(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setDisabled", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "disabled", "disabled");
        l.exiting(getClass().getName(), "setDisabled");
    }

    /**
     * set enabled
     *
     * @param fieldId
     */
    public void setEnabled(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setEnabled", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "disabled", "");
        l.exiting(getClass().getName(), "setEnabled");
    }

  /**
   * set setRequired
   *
   * @param fieldId
   */
    public void setRequired(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRequired", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "required", "Y");
        l.exiting(getClass().getName(), "setRequired");
    }

  /**
   * set setNotRequired
   *
   * @param fieldId
   */
    public void setNotRequired(String fieldId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setNotRequired", new Object[]{fieldId});
        }
        addEnforcedResult(fieldId, "required", "N");
        l.exiting(getClass().getName(), "setNotRequired");
    }

    /**
     * set value in first non header record
     * @param fieldId
     * @param value
     */
    public void setValue(String fieldId, String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setValue", new Object[]{fieldId, value});
        }
        setRecordValue(fieldId, value, getFirstNonHeaderRecord());
        l.exiting(getClass().getName(), "setValue");
    }

    /**
     * set field value
     * @param fieldId
     * @param value
     * @param record
     */
    public void setRecordValue(String fieldId, String value, Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setRecordValue", new Object[]{fieldId, value, record});
        }
        String valueToSet = value;
        if ("BLANK_VALUE".equals(value)) {
            valueToSet = "";
        }
        String fieldIdToSet = fieldId;
        if (!record.hasField(fieldId) && Record.TYPE_GRID.equals(record.getType())) {
            if (l.isLoggable(Level.FINE)) {
                l.logp(Level.FINE, getClass().getName(), "setRecordValue", "Field id [" + fieldId + "] doesn't exist in grid record");
            }
            String tempId = Record.stripGHSuffix(fieldId);
            if (record.hasField(tempId)) {
                fieldIdToSet = tempId;
                l.logp(Level.FINE, getClass().getName(), "setRecordValue", "Use field id without GH suffix [" + fieldIdToSet + "]");
            } else {
                tempId = Record.stripTablePrefix(tempId);
                if (record.hasField(tempId)) {
                    fieldIdToSet = tempId;
                    l.logp(Level.FINE, getClass().getName(), "setRecordValue", "Use field id without table prefix [" + fieldIdToSet + "]");
                }
            }
        }
        record.setFieldValue(fieldIdToSet, valueToSet);
        record.addToChangedFieldsInRule(fieldIdToSet);
        FactHandle factHandle = getActivationEvent().getKnowledgeRuntime().getFactHandle(record);
        getActivationEvent().getKnowledgeRuntime().update(factHandle, record);
        l.exiting(getClass().getName(), "setRecordValue");
    }

    /**
     * set setCSSClassName
     * @param fieldId
     * @param value
     */
    public void setCSSClassName(String fieldId, String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setCSSClassName", new Object[]{fieldId, value});
        }
        addEnforcedResult(fieldId, "className", value);
        l.exiting(getClass().getName(), "setCSSClassName");
    }

    /**
     * set setTooltip
     * @param fieldId
     * @param value
     */
    public void setTooltip(String fieldId, String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setTooltip", new Object[]{fieldId, value});
        }
        addEnforcedResult(fieldId, "title", value);
        l.exiting(getClass().getName(), "setTooltip");
    }

    /**
     * set label
     * @param fieldId
     * @param label
     */
    public void setLabel(String fieldId, String label) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setLabel", new Object[]{fieldId, label});
        }                                                                                                  
        addEnforcedResult(fieldId, "label", label);
        l.exiting(getClass().getName(), "setLabel");
    }


    /**
     * set text color
     * @param fieldId
     * @param color
     */
    public void setColor(String fieldId, String color) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setColor", new Object[]{fieldId, color});
        }
        setStyle(fieldId, "color", color);
        l.exiting(getClass().getName(), "setColor");
    }

    /**
     * set background color
     * @param fieldId
     * @param color
     */
    public void setBackgroundColor(String fieldId, String color) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setBackgroundColor", new Object[]{fieldId, color});
        }
        setStyle(fieldId, "backgroundColor", color);
        l.exiting(getClass().getName(), "setBackgroundColor");
    }

    /**
     * set style
     * @param fieldId
     * @param styleName
     * @param value
     */
    public void setStyle(String fieldId, String styleName, String value) {
        setStyle(fieldId, styleName, value, false, false);
        l.exiting(getClass().getName(), "setStyle");
    }

     /**
     * set style
     * @param fieldId
     * @param styleName
     * @param value
     */
    private void setStyle(String fieldId, String styleName, String value, boolean isGridColumn, boolean isPanel) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setStyle", new Object[]{fieldId, styleName, value});
        }
        String fieldIdForOBR = fieldId;
        if (isGridColumn) {
          fieldIdForOBR += ".gridColumn";
        }
        if (isPanel) {
          fieldIdForOBR += ".panel"; 
        }
        addEnforcedResult(fieldIdForOBR, "style." + styleName, value);
        l.exiting(getClass().getName(), "setStyle");
    }
  
    /**
     * remove record from grid
     *
     * <P><B>Note:</B></P>
     * <P>Rules with this method will be executed in a separate KnowledgeBase before executing other rules</P>
     *
     */
    public void removeRow() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "removeRow");
        }
        Record record = getGridRecord();
        if (record != null) {
            FactHandle factHandle = getActivationEvent().getKnowledgeRuntime().getFactHandle(record);
            getActivationEvent().getKnowledgeRuntime().retract(factHandle);
            if (!m_removedRecordList.contains(record)){
                m_removedRecordList.add(record);
            }
            RecordSet recordSet = record.getConnectedRecordSet();
            if (recordSet != null) {
                recordSet.removeRecord(record, false);
                if (!m_recordSetsEffectedByRemoveRecord.contains(recordSet)) {
                    m_recordSetsEffectedByRemoveRecord.add(recordSet);
                }
            } else {
                l.logp(Level.WARNING, getClass().getName(), "removeRow", "Trying to remove record which doesn't belong to grid");
            }
        } else {
            l.logp(Level.WARNING, getClass().getName(), "removeRow", "Grid record not found");
        }
        l.exiting(getClass().getName(), "removeRow");
    }

    /**
     * hide row
     *
     * <P><B>Note:</B></P>
     * <P>This method will change fact data directly. Please set rule priority to high</P>
     * <P>For example: salience 99<P>
     *
     * <P> salience: for rules with a specified value, they are fired in order of highest value to lowest<P>
     *
     * <P>Don't forget to add this filter displayIndicator=="Y" to avoid endless loop</P>
     * <P>For example: $record : Record( type=="Grid", displayIndicator=="Y")<P>
     *
     */
    public void hideRow() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "hideRow");
        }
        Record record = getGridRecord();
        if (record != null) {
            record.setDisplayIndicator(YesNoFlag.N);
            // update fact data
            FactHandle factHandle = getActivationEvent().getKnowledgeRuntime().getFactHandle(record);
            getActivationEvent().getKnowledgeRuntime().update(factHandle, record);
        } else {
            l.logp(Level.WARNING, getClass().getName(), "hideRow", "Grid record not found");
        }
        l.exiting(getClass().getName(), "hideRow");
    }

    /**
     * get field id that triggered change event
     * @return
     */
    public String getFieldThatTriggeredChangeEvent() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getFieldThatTriggeredChangeEvent");
        }
        String fieldId = getRequest().getParameter("_FieldIdTriggeredEvent");
        if (StringUtils.isBlank(fieldId)) {
            fieldId = "";
        }
        l.exiting(getClass().getName(), "getFieldThatTriggeredChangeEvent", fieldId);
        return fieldId;
    }

    /**
     * set page to ready only
     *
     * @return
     */
    public void setAllPageFieldsToReadOnly() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setAllPageFieldsToReadOnly");
        }
        getRequest().setAttribute(RequestHelper.IS_SET_PAGE_TO_READ_ONLY_IN_RULE, "true");
        l.exiting(getClass().getName(), "setAllPageFieldsToReadOnly");
    }

    /**
     * add to enforce result
     * @param fieldId
     * @param name
     * @param value
     */
    private synchronized void addEnforcedResult(String fieldId, String name, String value) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addEnforcedResult", new Object[]{fieldId, name, value});
        }
        Map enforcedResult = null;
        if (getGridRecord() != null) {
            enforcedResult = getGridRecord().getEnforcedResultMap();
        }
        else {
            enforcedResult = (HashMap) m_request.getAttribute(OBR_ENFORCED_RESULT_MAP);
            if (enforcedResult == null) {
                enforcedResult = new HashMap();
                m_request.setAttribute(OBR_ENFORCED_RESULT_MAP, enforcedResult);
            }
        }
        List list = null;
        if (enforcedResult.containsKey(fieldId)) {
            list = (List) enforcedResult.get(fieldId);
        }
        else {
            list = new ArrayList();
            enforcedResult.put(fieldId, list);
        }
        if ("readonly".equals(name)) {
            //always handle readonly first
            list.add(0, name + RequestHelper.SEPARATOR_FOR_VALUE + value);
        } else {
            list.add(name + RequestHelper.SEPARATOR_FOR_VALUE + value);
        }
        l.exiting(getClass().getName(), "addEnforcedResult");
    }

    Record getGridRecord() {
        return m_gridRecordLocal.get();
    }

    void setGridRecord(Record gridRecord) {
        m_gridRecordLocal.set(gridRecord);
    }

    void removeGridRecord() {
        m_gridRecordLocal.remove();
    }

    Record getFirstNonHeaderRecord() {
        return m_firstNonHeaderRecordLocal.get();
    }

    void setFirstNonHeaderRecord(Record firstNonHeaderRecord) {
        m_firstNonHeaderRecordLocal.set(firstNonHeaderRecord);
    }

    void removeFirstNonHeaderRecord() {
        m_firstNonHeaderRecordLocal.remove();
    }

    public PageBean getPageBean() {
        return m_pageBean;
    }

    public void setPageBean(PageBean pageBean) {
        this.m_pageBean = pageBean;
    }

    public HttpServletRequest getRequest() {
        return m_request;
    }

    public void setRequest(HttpServletRequest request) {
        this.m_request = request;
    }

    public HttpSession getSession() {
        return m_session;
    }

    public void setSession(HttpSession session) {
        this.m_session = session;
    }

    public List<Record> getRemovedRecordList() {
        return m_removedRecordList;
    }

    public List<RecordSet> getRecordSetsEffectedByRemoveRecord() {
        return m_recordSetsEffectedByRemoveRecord;
    }

    ActivationEvent getActivationEvent() {
        return m_activationEvent;
    }

    void setActivationEvent(ActivationEvent activationEvent) {
        this.m_activationEvent = activationEvent;
    }

    private PageBean m_pageBean;
    private HttpServletRequest m_request;
    private HttpSession m_session;
    private ActivationEvent m_activationEvent;
    private ThreadLocal<Record> m_gridRecordLocal = new ThreadLocal<Record>();
    private ThreadLocal<Record> m_firstNonHeaderRecordLocal = new ThreadLocal<Record>();
    private List<Record> m_removedRecordList = new ArrayList<Record>();
    private List<RecordSet> m_recordSetsEffectedByRemoveRecord = new ArrayList<RecordSet>();
    private final Logger l = LogUtils.getLogger(getClass());
}
