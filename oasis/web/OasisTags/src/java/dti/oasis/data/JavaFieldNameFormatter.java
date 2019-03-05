package dti.oasis.data;

/**
 * This Interface represents classes that format a database procedure or result set column name to a Java Field Name.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 26, 2006
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
public interface JavaFieldNameFormatter {

    /**
     * Format a database procedure or result set column name to a Java Field Name.
     */
    String format(String columnName);
}
