package dti.oasis.codelookupmgr.dao;

import java.util.ArrayList;
import java.sql.Connection;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Nov 30, 2006
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
public interface CodeLookupDAO {

    /**
     * Execute the named stored procedure. The expected format is:
     * [&]EXEC [keyColumnName][valueColumnName][storedProcName('&fieldId1&', '&fieldId2&','&fieldIdN&', ?)]
     * where:
     * [&] - the custom delimiter
     * keyColumnName - the name of the result set column that contains the lookup key
     * valueColumnName - the name of the result set column that contains the lookup value
     * storedProcName - the name of the stored procedure
     * fieldId1, fieldId2, fieldIdN - any number of input parameters that use Oasis fieldId values as input.
     * ? - placeholder for the OUT REF CURSOR
     *
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    ArrayList executeLovStoredProcedure(String fieldId, Connection conn, String storedProcSQL);

    /**
     * Execute a SQL statement that will return a ref cursor with key and value columns.
     *
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    ArrayList executeLovSQL(String fieldId, Connection conn, String sql);

    /**
     * Execute the query to load the List of Values from the LOOKUP_CODE table for the given lookup type code.
     *
     * TODO: Remove the Connection as a parameter when all application use Spring Configuration to configure the CodeLookupJdbcDAO with a DataSource.
     *
     * @return a List of LabelValueBeans
     */
    ArrayList executeLovLookup(String fieldId, Connection conn, String lookupLovSql, String lookupTypeCode, boolean displayLongDesc);
}
