package dti.oasis.data;

/**
 * This class implements the JavaFieldNameFormatter by returning the column name untouched.
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
public class NoOpJavaFieldNameFormatter implements JavaFieldNameFormatter {

    /**
     * Return the column name untouched.
     */
    public String format(String columnName) {
        return columnName;
    }
}
