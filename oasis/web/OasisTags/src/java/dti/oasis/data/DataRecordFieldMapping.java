package dti.oasis.data;

import dti.oasis.converter.Converter;
import dti.oasis.converter.ConverterFactory;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines the mapping info for mapping between a data field and a <t>Record Field</t>.
 * <p/>
 * The data field names should be stored in the format matching the JavaFieldNameFormatter used by the
 * StoredProcedureDAOHelper to format procedure and/or result set column names.
 * For example, if the ColumnNameToJavaFieldNameFormatter is specified,
 * any '_FK' or '_PK' suffix is replaced with '_ID',
 * any specified prefix strings will be stripped from the front of the column name,
 * and all '_' characters will be removed, capitalizing the following character.
 * For example, the data field name "policy_pk" is stoerd here as "policyId".
 * This means that DataRecordFieldMappings are not required to simply convert from the database format
 * to the standard Java Property Name format.
 * <p/>
 * If the outout fieldConverter is provided, it should be able to convert from any JDBC type supported by the <t>StoredProcedureDAO</t>.
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
 *
 * ---------------------------------------------------
 */
public class DataRecordFieldMapping {

    public DataRecordFieldMapping(String dataFieldName, String recordFieldName, Class outputTargetType, Converter outputFieldConverter) {
        setDataFieldName(dataFieldName);
        m_recordFieldName = recordFieldName;
        m_outputTargetType = outputTargetType;
        m_outputFieldConverter = outputFieldConverter;
    }

    public DataRecordFieldMapping(String dataFieldName, String recordFieldName, Converter outputFieldConverter) {
        m_dataFieldName = dataFieldName;
        m_recordFieldName = recordFieldName;
        m_outputFieldConverter = outputFieldConverter;
        m_outputTargetType = outputFieldConverter.getDefaultTargetType();
    }

    public DataRecordFieldMapping(String dataFieldName, String recordFieldName, Class outputTargetType) {
        setDataFieldName(dataFieldName);
        m_recordFieldName = recordFieldName;
        m_outputTargetType = outputTargetType;
    }

    public DataRecordFieldMapping(String dataFieldName, String recordFieldName) {
        setDataFieldName(dataFieldName);
        m_recordFieldName = recordFieldName;
    }

    public String getDataFieldName() {
        return m_dataFieldName;
    }

    public void setDataFieldName(String dataFieldName) {
        m_dataFieldName = dataFieldName;
    }

    public String getRecordFieldName() {
        return m_recordFieldName;
    }

    public void setRecordFieldName(String recordFieldName) {
        m_recordFieldName = recordFieldName;
    }

    public boolean hasOutputTargetType() {
        return m_outputTargetType != null;
    }

    public Class getOutputTargetType() {
        return m_outputTargetType;
    }

    public void setOutputTargetType(Class outputTargetType) {
        m_outputTargetType = outputTargetType;
    }

    public boolean hasOutputFieldConverter() {
        return m_outputFieldConverter != null;
    }

    public Converter getOutputFieldConverter() {
        return m_outputFieldConverter;
    }

    public void setOutputFieldConverter(Converter outputFieldConverter) {
        m_outputFieldConverter = outputFieldConverter;
        if (m_outputTargetType == null) {
            m_outputTargetType = outputFieldConverter.getDefaultTargetType();
        }
    }

    /**
     * Map the field name and optionally the field value, and set the Field in the given record.
     *
     * @param fieldValue
     * @param record
     */
    public void mapDataFieldToRecord(Object fieldValue, Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "mapDataFieldToRecord", new Object[]{fieldValue});
        }
        if (l.isLoggable(Level.FINE)) {
            l.logp(Level.FINE, getClass().getName(), "mapDataFieldToRecord", "Mapping dataField'" + getDataFieldName() + "' to recordField'" + getRecordFieldName() + "'");
        }

        // If a specific Field Type is specified; convert the field value
        if (hasOutputTargetType()) {
            Class fieldType = getOutputTargetType();
            Converter converter = null;
            if (hasOutputFieldConverter()) {
                // Use the custom converter
                converter = getOutputFieldConverter();
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "mapDataFieldToRecord", "Using custom converter'" + converter + "'");
                }
            } else {
                // Use the standard converter
                converter = ConverterFactory.getInstance().getConverter(fieldType);
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "mapDataFieldToRecord", "Using standard converter'" + converter + "'");
                }
            }
            fieldValue = converter.convert(fieldType, fieldValue);
        }

        // Map the field
        record.setFieldValue(getRecordFieldName(), fieldValue);

        l.exiting(getClass().getName(), "mapDataFieldToRecord");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DataRecordFieldMapping that = (DataRecordFieldMapping) o;

        if (!m_dataFieldName.equals(that.m_dataFieldName)) return false;
        if (!m_recordFieldName.equals(that.m_recordFieldName)) return false;
        if (m_outputTargetType != null ? !m_outputTargetType.equals(that.m_outputTargetType) : that.m_outputTargetType != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = m_dataFieldName.hashCode();
        result = 29 * result + m_recordFieldName.hashCode();
        result = 29 * result + (m_outputTargetType != null ? m_outputTargetType.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "DataRecordFieldMapping{" +
            "m_dataFieldName='" + m_dataFieldName + '\'' +
            ", m_recordFieldName='" + m_recordFieldName + '\'' +
            ", m_outputTargetType=" + m_outputTargetType +
            ", m_outputFieldConverter=" + m_outputFieldConverter +
            '}';
    }

    private String m_dataFieldName;
    private String m_recordFieldName;
    private Converter m_outputFieldConverter;
    private Class m_outputTargetType;
    private final Logger l = LogUtils.getLogger(getClass());
}
