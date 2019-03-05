package dti.oasis.data;

import dti.oasis.util.LogUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines the set of mapping info for mapping between data fields and <t>Record Fields</t>.
 * The data field names are stored in the form of the standard Java Property Names,
 * where any '_' characters are removed, and the following character is upper cased.
 * For example, the data field name "user_id" is stoerd here as "userId".
 * This means that DataRecordFieldMappings are not required to simply convert from the database format
 * to the standard Java Property Name format.
 * If the fieldConverter is provided, it must be able to convert to and from any JDBC type supported by the <t>StoredProcedureDAO</t>.
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
public class DataRecordMapping {

    public DataRecordMapping() {
    }

    public void setFieldMappings(Collection fieldMappings) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "setFieldMappings", new Object[]{fieldMappings});
        }

        Iterator iter = fieldMappings.iterator();
        while (iter.hasNext()) {
            DataRecordFieldMapping fieldMapping = (DataRecordFieldMapping) iter.next();
            addFieldMapping(fieldMapping);
        }

        l.exiting(getClass().getName(), "setFieldMappings");
    }

    public void addFieldMapping(DataRecordFieldMapping fieldMapping) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "addFieldMapping", new Object[]{fieldMapping});
        }

        m_hashCode = 0;
        m_dataFieldMappings.put(fieldMapping.getDataFieldName().toUpperCase(), fieldMapping);

        l.exiting(getClass().getName(), "addFieldMapping");
    }

    public boolean containsMappingForDataField(String dataFieldName) {
        return m_dataFieldMappings.containsKey(dataFieldName.toUpperCase());
    }

    public DataRecordFieldMapping getMappingForDataField(String dataFieldName) {
        return (DataRecordFieldMapping) m_dataFieldMappings.get(dataFieldName.toUpperCase());
    }

    public int size() {
        return m_dataFieldMappings.size();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DataRecordMapping that = (DataRecordMapping) o;

        if (!m_dataFieldMappings.equals(that.m_dataFieldMappings)) return false;

        return true;
    }

    public int hashCode() {
        if (m_hashCode == 0) {
            m_hashCode = Objects.hash(m_dataFieldMappings);
        }
        return m_hashCode;
    }


    public String toString() {
        return "DataRecordMapping{" +
            "m_hashCode=" + m_hashCode +
            ", m_dataFieldMappings=" + m_dataFieldMappings +
            '}';
    }

    private int m_hashCode;
    private Map m_dataFieldMappings = new HashMap();
    private final Logger l = LogUtils.getLogger(getClass());
}
