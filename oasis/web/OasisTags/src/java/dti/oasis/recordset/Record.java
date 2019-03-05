package dti.oasis.recordset;

import dti.oasis.busobjs.Info;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.converter.Converter;
import dti.oasis.obr.RequestHelper;
import dti.oasis.tags.GridHelper;
import dti.oasis.util.ByteArray;
import dti.oasis.util.ListUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a Record of data with named fields.
 * Named access to fields is case in sensitive.
 * The getFieldNames method returns the field names in the provided order and case.
 * The getFieldMap method returns a set of field names in no particular order, in all upper case.
 * <p/>
 * The UPDATE_IND, DISPLAY_IND and EDIT_IND fields must be stored separately in this record.
 * Never set a field in this Record for any of these fields using the setField* methods.
 * Instead, use the explicit setUpdateIndicator(), setDisplayIndicator(), and setEditIndicator() methods.
 * The getFieldCount() method does not account for these fields.
 * <p/>
 * If a RecordSet has called the connectToRecordSet method,
 * then any field that is added or deleted is reflected
 * in the connected RecordSet's collection of Fields.
 * It is assumed that all Records have the same Fields and that they have been added in the same order.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/01/2007        JMP         Add getStringValue overloaded method with "null" parameter
 *                              default value.  Prevents dislplay of "null" keyword in the grid.
 * 12/15/2011       fcb         Add setNullFieldsToEmpty.
 * 07/23/2012       tcheng      135128 - added getReaderValue support for Oracle Clob type
 * 03/07/2018       dpang       109086 - added getStringValueDefaultEmpty
 * 11/12/2018       wreeder     196160 - Optimize iteration through Fields in a Record with getFields() / field.getStringValue() instead of getFieldNames() / record.hasFieldValue(fieldId) / record.getStringValue(fieldId)
 * ---------------------------------------------------
 */
public class Record implements Info, Serializable {

    /**
     * remove table prefix if it exists
     *
     * @param fieldId
     * @return
     */
    public static String stripTablePrefix(String fieldId) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(Record.class.getName(), "stripTablePrefix", new Object[]{fieldId});
        }
        String fieldIdWithoutTablePrefix = fieldId;
        if (fieldIdWithoutTablePrefix.indexOf("_") > 0) {
            fieldIdWithoutTablePrefix = fieldIdWithoutTablePrefix.substring(fieldIdWithoutTablePrefix.indexOf("_") + 1);
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(Record.class.getName(), "stripTablePrefix", fieldIdWithoutTablePrefix);
        }
        return fieldIdWithoutTablePrefix;
    }

    /**
     * remove _GH suffix if it exists
     *
     * @param fieldId
     * @return
     */
    public static String stripGHSuffix(String fieldId) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(Record.class.getName(), "stripGHSuffix", new Object[]{fieldId});
        }
        String fieldIdWithoutGHSuffix = fieldId;
        String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
        if (!StringUtils.isBlank(gridHeaderSuffix) && fieldIdWithoutGHSuffix.endsWith(gridHeaderSuffix)) {
            fieldIdWithoutGHSuffix = fieldIdWithoutGHSuffix.substring(0, fieldIdWithoutGHSuffix.length() - gridHeaderSuffix.length());
        }
        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(Record.class.getName(), "stripGHSuffix", fieldIdWithoutGHSuffix);
        }
        return fieldIdWithoutGHSuffix;
    }

    public Record() {
        m_fieldMap = new DelegateMap();
        m_fieldNames = new ArrayList();
    }

    public Record(int size) {
        m_fieldMap = new DelegateMap((int) Math.ceil(1.34 * size));
        m_fieldNames = new ArrayList((int) Math.ceil(1.34 * size));
    }

    public boolean hasField(String name) {
        return m_fieldMap.containsKey(name.toUpperCase());
    }

    public boolean hasFieldValue(String name) {
        return hasField(name) && getField(name).getValue() != null;
    }

    public boolean hasStringValue(String name) {
        return hasFieldValue(name) && !StringUtils.isBlank(getStringValue(name));
    }

    public Field getField(String name) {
        if (!hasField(name)) {
            throw new IllegalArgumentException("The field '" + name + "' is not found in the Record.");
        }
        return m_fieldMap.get(name.toUpperCase());
    }

    public Map<String, Field> getFieldMap() {
        return m_fieldMap;
    }

    public Object getFieldValue(String name) {
        Field fld = getField(name);
        return fld == null ? null : fld.getValue();
    }

    /**
     * Get the value of the specified field.
     * If the field does not exist, or the field's value is null, the given nullValue is returned.
     *
     * @param name field name
     * @param nullValue value to return if the field does not exist, or the field's value is null
     */
    public Object getFieldValue(String name, Object nullValue) {
        return hasFieldValue(name) ? getFieldValue(name) : nullValue;
    }

    protected Object getFieldValue(int fieldIndex) {
        return getFieldValue((String) m_fieldNames.get(fieldIndex));
    }

    public void setField(String name, Field field) {
        field.setName(name);
        if (!hasField(name)) {
            m_fieldNames.add(name);
        }
        m_fieldMap.put(name.toUpperCase(), field);

        // Keep the connected RecordSet up to date with the field names.
        if (isConnectedToRecordSet()) {
            m_connectedRecordSet.addFieldName(name);
        }
    }

    public void setFieldValue(String name, Object value) {
        setField(name, value instanceof Field ? (Field) value : new Field(name, value));
    }

    public Iterator<Field> getFields() {
        return m_fieldMap.values().iterator();
    }

    /**
     * Set the given fields in this record.
     * By default, any fields that already exist with the same name are overwritten.
     */
    public Record setFields(Record newFields) {
        return setFields(newFields, true);
    }

    /**
     * Set the given fields in this record.
     */
    public Record setFields(Record newFields, boolean overwriteFieldIfExists) {
        Iterator iter = newFields.getFields();
        while (iter.hasNext()) {
            Field newField = (Field) iter.next();
            if (overwriteFieldIfExists || !hasFieldValue(newField.getName())) {
                setField(newField.getName(), newField);
            }
        }
        return this;
    }

    /**
     * Sets all the null fields to empty string.
     */
    public void setNullFieldsToEmpty() {
        Iterator iter = getFields();
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            if (field.getValue() == null) {
                field.setValue("");
            }
        }
    }

    public int getFieldCount() {
        return m_fieldNames.size();
    }

    /**
     * Returns an iterator of the field names in the order they were set.
     */
    public Iterator getFieldNames() {
        return m_fieldNames.iterator();
    }

    public List<String> getFieldNameList() {
        return m_fieldNames;
    }

    public int getSize() {
        return m_fieldMap.size();
    }

    public String getStringValue(String name) {
        return getField(name).getStringValue();
    }

    /**
     * Get the String value of the specified field.
     * If the field does not exist, or the field's value is null or an empty string, the given nullValue is returned.
     *
     * @param name field name
     * @param nullValue value to return if the field does not exist, or the field's value is null or an empty string
     */
    public String getStringValue(String name, String nullValue) {
        String stringValue = nullValue;
        if (hasFieldValue(name)) {
            Field fld = getField(name);
            String strValue = fld.getStringValue();
            if (!StringUtils.isBlank(strValue)) {
                stringValue = strValue;
            }
        }
        return stringValue;
    }

    /**
     * Get the String value of the specified field. Return empty String as default nullValue.
     *
     * @param name
     * @return
     */
    public String getStringValueDefaultEmpty(String name) {
        return getStringValue(name, "");
    }

    protected String getStringValue(int fieldIndex) {
        return getStringValue((String) m_fieldNames.get(fieldIndex));
    }

    protected String getStringValue(int fieldIndex, String nullValue) {
        return getStringValue((String) m_fieldNames.get(fieldIndex), nullValue);
    }

    public String getStringValue(String name, Converter converter) {
        Field fld = getField(name);
        return fld.getStringValue(converter);
    }

    public Double getDoubleValue(String name) {
        return getField(name).getDoubleValue();
    }

    public Double getDoubleValue(String name, Converter converter) {
        return getField(name).getDoubleValue(converter);
    }

    public BigDecimal getBigDecimalValue(String name) {
        return getField(name).getBigDecimalValue();
    }

    public BigDecimal getBigDecimalValue(String name, Converter converter) {
        return getField(name).getBigDecimalValue(converter);
    }

    public Float getFloatValue(String name) {
        return getField(name).getFloatValue();
    }

    public Float getFloatValue(String name, Converter converter) {
        return getField(name).getFloatValue(converter);
    }

    public Long getLongValue(String name) {
        return getField(name).getLongValue();
    }

    public Long getLongValue(String name, Converter converter) {
        return getField(name).getLongValue(converter);
    }

    public Integer getIntegerValue(String name) {
        return getField(name).getIntegerValue();
    }

    public Integer getIntegerValue(String name, Converter converter) {
        return getField(name).getIntegerValue(converter);
    }

    public Short getShortValue(String name) {
        return getField(name).getShortValue();
    }

    public Short getShortValue(String name, Converter converter) {
        return getField(name).getShortValue(converter);
    }

    public Boolean getBooleanValue(String name) {
        return getField(name).getBooleanValue();
    }

    public Boolean getBooleanValue(String name, boolean nullValue) {
        Boolean booleanValue = null;
        if (hasFieldValue(name)) {
            booleanValue = getField(name).getBooleanValue();
        }
        else {
            booleanValue = Boolean.valueOf(nullValue);
        }
        return booleanValue;
    }

    public Boolean getBooleanValue(String name, Converter converter) {
        return getField(name).getBooleanValue(converter);
    }

    public Date getDateValue(String name) {
        return getField(name).getDateValue();
    }

    protected Date getDateValue(int fieldIndex) {
        return getDateValue((String) m_fieldNames.get(fieldIndex));
    }

    public Date getDateValue(String name, Converter converter) {
        return getField(name).getDateValue(converter);
    }

    public ByteArray getByteArrayValue(String name) {
        return getField(name).getByteArrayValue();
    }

    public InputStream getInputStreamValue(String name) {
        return getField(name).getInputStreamValue();
    }

    public OutputStream getOutputStreamValue(String name) {
        return getField(name).getOutputStreamValue();
    }
    
    public Reader getReaderValue(String name) {
        return getField(name).getReaderValue();
    }

    public void remove(String name) {
        if (hasField(name)) {
            m_fieldMap.remove(name.toUpperCase());

            // Remove the field name from the fieldNames list in a case insensitive manner.
            ListUtils.removeCaseInsensitive(m_fieldNames, name);
        }

        if (isConnectedToRecordSet()) {
            m_connectedRecordSet.removeFieldName(name);
        }
    }

    public void clear() {
        m_fieldMap.clear();
        m_enforcedResultMap.clear();
    }

    /**
     * Set the update indicator, specifying if this row has been inserted, deleted, updated or not changed.
     * Valid values are contained in the UpdateIndicator Interface.
     */
    public void setUpdateIndicator(String updateIndicator) {
        m_updateIndicator = updateIndicator.toUpperCase();
    }

    /**
     * Return the update indicator, specifying if this row has been inserted, deleted, updated or not changed.
     * If the update indicator is not found, UpdateIndicator.NOT_CHANGED is returned by default.
     */
    public String getUpdateIndicator() {
        return m_updateIndicator;
    }

    public boolean isUpdateIndicatorInserted() {
        return m_updateIndicator.equals(UpdateIndicator.INSERTED);
    }

    public boolean isUpdateIndicatorUpdated() {
        return m_updateIndicator.equals(UpdateIndicator.UPDATED);
    }

    public boolean isUpdateIndicatorDeleted() {
        return m_updateIndicator.equals(UpdateIndicator.DELETED);
    }

    public boolean isUpdateIndicatorNotChanged() {
        return m_updateIndicator.equals(UpdateIndicator.NOT_CHANGED);
    }

    /**
     * Set the 'Y'/'N' display indicator, specifying if this row can be displayed.
     */
    public void setDisplayIndicator(String displayIndicator) {
        m_displayIndicator = YesNoFlag.getInstance(displayIndicator);
    }

    /**
     * Set the 'Y'/'N' display indicator, specifying if this row can be displayed.
     */
    public void setDisplayIndicator(YesNoFlag displayInd) {
        m_displayIndicator = displayInd;
    }

    /**
     * Return the 'Y'/'N' display indicator, specifying if this row can be displayed.
     */
    public String getDisplayIndicator() {
        return m_displayIndicator.getName();
    }

    /**
     * Return true or false for the display indicator, specifying if this row can be displayed.
     */
    public boolean getDisplayIndicatorBooleanValue() {
        return m_displayIndicator.booleanValue();
    }

    /**
     * Set the 'Y'/'N' edit indicator, specifying if this row can be edited.
     */
    public void setEditIndicator(String editIndicator) {
        m_editIndicator = YesNoFlag.getInstance(editIndicator);
    }

    /**
     * Set the 'Y'/'N' edit indicator, specifying if this row can be edited.
     */
    public void setEditIndicator(YesNoFlag editInd) {
        m_editIndicator = editInd;
    }

    /**
     * Return the 'Y'/'N' edit indicator, specifying if this row can be edited.
     */
    public String getEditIndicator() {
        return m_editIndicator.getName();
    }

    /**
     * Return true or false for the edit indicator, specifying if this row can be edited.
     */
    public boolean getEditIndicatorBooleanValue() {
        return m_editIndicator.booleanValue();
    }

    public int getRecordNumber() {
        return m_recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        m_recordNumber = recordNumber;
    }

    public String getType() {
        return type;
    }

    public boolean isUseForRule() {
        return ((DelegateMap)m_fieldMap).isUseForRule();
    }

    public void setUseForRule(boolean useForRule) {
        ((DelegateMap)m_fieldMap).setUseForRule(useForRule);
        ((DelegateMap) m_originalFieldMap).setUseForRule(useForRule);
    }

    /**
     * set type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return toString(", ");
    }
    public String toString(String fieldSeparator) {
        StringBuffer buf = new StringBuffer("Record{m_recordNumber=" + m_recordNumber + ", type=" + type + ", m_fieldMap={");
        String sep = "";
        Iterator iter = getFields();
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            String fieldName = field.getName();
            buf.append(sep).append(fieldName).append("='").append(field.getStringValue()).append("'");
            sep = fieldSeparator;
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * Returns true if this Record is connected to a RecordSet; Otherwise, false.
     */
    protected boolean isConnectedToRecordSet() {
        return m_connectedRecordSet != null;
    }

    /**
     * Instruct this Record that it is connected to the given RecordSet.
     * Once connected, any field that is added or deleted is reflected
     * in the connected RecordSet's collection of Fields.
     * It is assumed that all Records have the same Fields and that they have been added in the same order.
     */
    protected void connectToRecordSet(RecordSet recordSet) {
        m_connectedRecordSet = recordSet;
    }

    /**
     * Instruct this Record that it is no longer connected to a RecordSet.
     */
    protected void disconnectFromRecordSet() {
        m_connectedRecordSet = null;
    }

    /**
     * This methos is used to set the field name to the specified index of the field name list.
     * If the field name doesn't exist in the field name list, system does nothing.
     * Zero is the first index of field name list.
     * 
     * @param name  field name
     * @param index specified index of this field name
     * @throws  IndexOutOfBoundsException if index is out of range.
     */
    protected void setFieldIndex(String name, int index) {
        if (!hasField(name)) {
            return;
        }
        // Remove the field name from field name list.
        ListUtils.removeCaseInsensitive(m_fieldNames, name);
        // Add the field name to the specified postion of field name list.
        m_fieldNames.add(index, name);
    }

    public Map getEnforcedResultMap() {
        return m_enforcedResultMap;
    }

    /**
     * Return OBR enforced result
     * @return
     */
    public String getOBREnforcedResult() {
        return RequestHelper.enforcedResultMapToString(m_enforcedResultMap);
    }

    public RecordSet getConnectedRecordSet() {
        return m_connectedRecordSet;
    }

    public Map<String, Field> getOriginalFieldMap() {
        return m_originalFieldMap;
    }

    public void setOriginalFieldMap(Map<String, Field> originalFieldMap) {
        m_originalFieldMap = originalFieldMap;
    }

    public String getRowId() {
        return m_rowId;
    }

    public void setRowId(String rowId) {
        m_rowId = rowId;
    }

    /**
     * This method is used for OBR rules
     *
     * @return return system date
     */
    public Date getToday() {
        return Calendar.getInstance().getTime();
    }

    public void addToChangedFieldsInRule(String fieldId) {
        m_changedFieldsInRule.add(fieldId);
    }

    public Set<String> getChangedFieldsInRule() {
        return m_changedFieldsInRule;
    }

    private String type = "Unknown";
    private Map m_enforcedResultMap = new HashMap();
    private Map<String, Field> m_fieldMap;
    private Map<String, Field> m_originalFieldMap = new DelegateMap(true);
    private List m_fieldNames;
    private RecordSet m_connectedRecordSet;
    private String m_updateIndicator = UpdateIndicator.NOT_CHANGED;
    private YesNoFlag m_displayIndicator = YesNoFlag.Y;
    private YesNoFlag m_editIndicator = YesNoFlag.Y;
    private int m_recordNumber = 0;
    private String m_rowId = null;
    private Set<String> m_changedFieldsInRule = new HashSet<String>();

    private final Logger l = LogUtils.getLogger(getClass());
    private static final Logger c_l = LogUtils.getLogger(Record.class);

    public static final String TYPE_HEADER = "Header";
    public static final String TYPE_NONGRID = "NonGrid";
    public static final String TYPE_GRID = "Grid";
}
