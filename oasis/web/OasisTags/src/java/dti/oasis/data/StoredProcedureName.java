package dti.oasis.data;

import dti.oasis.util.StringUtils;

import java.util.StringTokenizer;

/**
 * This class parses a fully qualified stored procedure name, and exposes its parts.
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
public class StoredProcedureName {

    public StoredProcedureName(String spName, String fullyQualifiedSPName) {
        m_spName = spName;
        m_fullyQualifiedSPName = fullyQualifiedSPName.trim().toUpperCase();

        try {
            StringTokenizer tokenizer = new StringTokenizer(m_fullyQualifiedSPName, ".", false);
            m_schemaName = tokenizer.nextToken();
            m_packageName = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                m_procedureName = tokenizer.nextToken();
            }
            else {
                m_procedureName = m_packageName;
                m_packageName = "";
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("The stored procedure name '"+fullyQualifiedSPName+"' must be in the format schemaName.procedureName or schemaName.packageName.procedureName");
        }
    }

    public String getFullyQualifiedSPName() {
        return m_fullyQualifiedSPName;
    }

    public String getSchemaName() {
        return m_schemaName;
    }

    public boolean hasPackageName() {
        return !StringUtils.isBlank(m_packageName);
    }
    public String getPackageName() {
        return m_packageName;
    }

    public String getProcedureName() {
        return m_procedureName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StoredProcedureName that = (StoredProcedureName) o;

        if (!m_fullyQualifiedSPName.equals(that.m_fullyQualifiedSPName)) return false;

        return true;
    }

    public int hashCode() {
        return m_fullyQualifiedSPName.hashCode();
    }

    public String toString() {
        return m_spName;
    }

    private String m_spName;
    private String m_fullyQualifiedSPName;
    private String m_schemaName;
    private String m_packageName;
    private String m_procedureName;
}
