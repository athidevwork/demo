package dti.oasis.util;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Singleton class to manage Web Forms.  It also has a
 * few useful miscellaneous methods.
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 2, 2004
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  2/6/2004        jbe     Added Logging
 *  2/27/2004       jbe     Changed getForm
 *  3/3/2004        jbe     Implement IFormConstants
 *                          Add saveFormData and getFormFks
 *  3/10/2004       jbe     Logging tweak
 *  11/17/20004     jbe     Use mostly preparedstatements
 *                          Add getFormInfo
 *  4/4/2005        jbe     Use WebQuery for web queries.  Use DatabaseUtils to close resources.
 * ---------------------------------------------------
 */

public class WebForm implements IFormConstants {
    private static final WebForm INSTANCE = new WebForm();

    /**
     * Call this method to get the single instance of this class
     *
     * @return
     */
    public static WebForm getInstance() {
        return INSTANCE;
    }

    private WebForm() {
    }

    private Object readResolve() {
        return INSTANCE;
    }


    private static final String SQL_GETFORMPKS = "select web_form_pk, web_query_fk FROM web_form WHERE code = ?";

    private static final String SQL_GETMAPPING = "SELECT sql_column_name, form_field_name " +
            "FROM web_form_map m, web_form f WHERE f.code = ? AND f.web_form_pk = m.web_form_fk";

    private static final String SQL_SAVEDATADEL = "DELETE web_form_data WHERE web_form_fk = ? AND source_record_fk = ? AND " +
            "source_table_name = ?";

    private static final String SQL_SAVEDATA = "INSERT INTO web_form_data(web_form_data_pk," +
            "web_form_fk, source_record_fk, source_table_name, form_field_name, value)" +
            " VALUES(OASIS_SEQUENCE.nextval,?,?,?,?,?)";

    private static final String SQL_GETDATA = "SELECT form_field_name, value " +
            "FROM web_form_data WHERE web_form_fk = ? AND source_record_fk = ? AND " +
            "source_table_name = ?";

    private static final String SQL_GETFORMINFO = "SELECT f.web_form_pk, f.short_description, " +
            "f.form_type, f.form_path, f.form_filename, f.web_query_fk " +
            "FROM web_form f WHERE f.code = ? ";

    /**
     * Return a WebFormInfo bean containing info about a form
     *
     * @param conn     JDBC Connection
     * @param formCode web_form.code
     * @return WebFormInfo, null if form not found
     * @throws SQLException
     */
    public WebFormInfo getFormInfo(Connection conn, String formCode) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getFormInfo", new Object[]{conn, formCode});
        PreparedStatement stmt = null;
        ResultSet rs = null;
        WebFormInfo info = null;
        try {
            stmt = conn.prepareCall(SQL_GETFORMINFO);
            stmt.setString(1, formCode);
            l.fine(new StringBuffer("Executing: ").append(SQL_GETFORMINFO).append(" with ").
                    append(formCode).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                info = new WebFormInfo();
                info.setCode(formCode);
                info.setFileName(rs.getString(5));
                info.setFormPk(rs.getLong(1));
                info.setFormType(rs.getString(3));
                info.setPathName(rs.getString(4));
                info.setShortDescription(rs.getString(2));
                info.setQueryFk(rs.getLong(6));
                if (info.getQueryFk() > 0) {
                    try {
                        info.setWebQuery(WebQuery.getInstance().getQuery(conn, info.getQueryFk()));
                    }
                    catch (IllegalArgumentException ignore) {

                    }
                }
            }
            l.exiting(getClass().getName(), "getFormInfo", info);
            return info;

        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }


    /**
     * Gets a web_form_fk and web_query_fk from a web form id.  Returns it in the WebFormInfo object.
     *
     * @param conn     Live JDBC Connection
     * @param formCode web_form.code
     * @return WebFormInfo object
     * @throws SQLException
     */
    protected WebFormInfo getFormFks(Connection conn, String formCode) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getFormFks", new Object[]{conn, formCode});
        ResultSet rs = null;
        PreparedStatement stmt = null;
        WebFormInfo info = null;
        try {
            stmt = conn.prepareCall(SQL_GETFORMPKS);
            l.fine(new StringBuffer("Executing: ").append(SQL_GETFORMPKS).append(" with ").
                    append(formCode).toString());
            stmt.setString(1, formCode);
            rs = stmt.executeQuery();
            if (rs.next()) {
                info = new WebFormInfo();
                info.setFormPk(rs.getLong(1));
                info.setQueryFk(rs.getLong(2));
            }
            l.exiting(getClass().getName(), "getFormFks", info);
            return info;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }

    /**
     * Execute a web query and return a live ResultSet.  Note that this uses the old method
     * of doing parameter replacement.
     *
     * @param stmt       Live JDBC Statement
     * @param query      SQL Query
     * @param queryParms Array of parameters to substitute in the web query.  Parameters
     *                   are expected to be in the query sequenced 1..n.  We look for an ampersand (&) as the
     *                   prefix of the parameter number.  So the query might look like:
     *                   SELECT 1 FROM mytable WHERE col1 = &1 AND col2 = '&2' AND col3 = &3
     * @return Live JDBC ResultSet
     * @throws SQLException
     */
    protected ResultSet executeQuery(Statement stmt, String query, String[] queryParms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "executeQuery", new Object[]
        {stmt, query, queryParms});
        int sz = queryParms.length;
        for (int i = 1; i <= sz; i++) {
            query = query.replaceAll(new StringBuffer("&").append(i).toString(), queryParms[i - 1]);
        }
        l.finer(new StringBuffer("Executing: ").append(query).toString());
        ResultSet rs = stmt.executeQuery(query);
        l.exiting(getClass().getName(), "executeQuery", rs);
        return rs;
    }

    /**
     * Creates a HashMap from a ResultSet. The ResultSet must be positioned before the
     * first row.
     *
     * @param rs       ResultSet.
     * @param keyCol   column # in ResultSet that will be used for the HashMap Key
     * @param entryCol column # in ResultSet that will be used for HashMap Entry
     * @return HashMap
     * @throws SQLException
     */
    public HashMap mapFromResultSet(ResultSet rs, int keyCol, int entryCol) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "mapFromResultSet", new Object[]
        {rs, new Integer(keyCol), new Integer(entryCol)});
        HashMap map = new HashMap();
        while (rs.next()) {
            map.put(rs.getString(keyCol), rs.getString(entryCol));
        }
        l.exiting(getClass().getName(), "mapFromResultSet", map);
        return map;
    }

    /**
     * Return a map of SQL Column to Form Field names
     *
     * @param conn     Live JDBC Connection
     * @param formCode web_form.web_form_pk
     * @return HashMap
     * @throws SQLException
     */
    protected HashMap getMap(Connection conn, String formCode) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getMap", new Object[]{conn, formCode});
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_GETMAPPING);
            stmt.setString(1, formCode);
            l.fine(new StringBuffer("Executing: ").append(SQL_GETMAPPING).append(" with ").
                    append(formCode).toString());
            rs = stmt.executeQuery();
            HashMap map = mapFromResultSet(rs, 1, 2);
            l.exiting(getClass().getName(), "getMap", map);
            return map;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }

    /**
     * Returns a HashMap containing Form Field/value mappings.  If more than one row
     * comes back from the query associated with the form, then the row number is appended
     * to the end of the field (HashMap.key) for each row beyond the first.
     * <b>MultiRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     * firstName_2, 'Fred Gwynne'
     * lastName_2, 'Sally Munster'
     *
     * <b>SingleRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     *
     * The query expects two parameters named &1 and &2 where &1 is source_record_fk
     * and &2 is source_table_name - as follows:
     * SELECT 1 FROM mytable WHERE source_record_fk = &1 AND source_table_name = &2
     *
     * This version of getForm gets data from the web_query and web_form_mapping
     * It also looks for data in web_form_data given the sourceRecordFk and sourceTable
     *
     * @param conn           JDBC Connection
     * @param formCode       web_form.code
     * @param sourceRecordFk First Parameter to query
     * @param sourceTable    Second parameter to query
     * @return HashMap
     * @throws SQLException
     * @deprecated by the UFE
     */
    public HashMap getForm(Connection conn, String formCode, String sourceRecordFk, String sourceTable)
            throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getForm", new Object[]{conn, formCode, sourceRecordFk, sourceTable});
        HashMap parmsMap = new HashMap(2);
        parmsMap.put("SOURCE_RECORD_FK",sourceRecordFk);
        parmsMap.put("SOURCE_TABLE_NAME", sourceTable);
        HashMap map = getForm(conn, formCode, parmsMap);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_GETDATA);
            WebFormInfo info = getFormFks(conn, formCode);
            if (info != null) {
                stmt.setLong(1, info.getFormPk());
                stmt.setLong(2, Long.parseLong(sourceRecordFk));
                stmt.setString(3, sourceTable);
                l.finer(new StringBuffer("Executing: ").append(SQL_GETDATA).append(" with ").
                        append(info.getFormPk()).append(',').append(sourceRecordFk).append(',').
                        append(sourceTable).toString());
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String key = rs.getString(1);
                    // If the query already has the key, then add _C
                    //if(map.containsKey(key)) {
                    //    l.finer(getClass().getName() + ":getForm - FieldId ["+key +
                    //            " exists in query.  Appending _C to override field.");
                    //    key+="_C";
                    //}
                    map.put(key, rs.getString(2));
                }
            }
            l.exiting(getClass().getName(), "getForm", map);
            return map;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }

    }

    /**
     * Returns a HashMap containing Form Field/value mappings.  If more than one row
     * comes back from the query associated with the form, then the row number is appended
     * to the end of the field (HashMap.key) for each row beyond the first.
     * <b>MultiRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     * firstName_2, 'Fred Gwynne'
     * lastName_2, 'Sally Munster'
     *
     * <b>SingleRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     *
     * This version of getForm gets data from the web_query and web_form_mapping only.  This
     * uses the old parameter replacement technique.
     *
     * @param conn       JDBC Connection
     * @param formCode   web_form.code
     * @param queryParms Array of parameters to substitute in the web query.  Parameters
     *                   are expected to be in the query sequenced 1..n.  We look for an ampersand (&) as the
     *                   prefix of the parameter number.  So the query might look like:
     *                   SELECT 1 FROM mytable WHERE col1 = &1 AND col2 = '&2' AND col3 = &3
     * @return HashMap
     * @throws SQLException
     * @deprecated This is the old method of executing queries with parameters.
     * Replaced by {@link dti.oasis.util.WebForm#getForm(java.sql.Connection, java.lang.String, java.util.Map)}
     */
    public HashMap getForm(Connection conn, String formCode, String[] queryParms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getForm", new Object[]{conn, formCode, queryParms});
        HashMap fieldsMap = new HashMap();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            WebFormInfo info = getFormFks(conn, formCode);
            if (info != null) {
                WebQueryInfo qInfo = WebQuery.getInstance().getQuery(conn, info.getQueryFk());
                HashMap map = getMap(conn, formCode);
                rs = executeQuery(stmt, qInfo.getSql(), queryParms);
                String[] cols = metaDataToColumnArray(rs.getMetaData());
                int i = 0;
                while (rs.next()) {
                    processRow(rs, map, cols, ++i, fieldsMap);
                }
            }
            l.exiting(getClass().getName(), "getForm", fieldsMap);
            return fieldsMap;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }

   /**
     * Returns a HashMap containing Form Field/value mappings.  If more than one row
     * comes back from the query associated with the form, then the row number is appended
     * to the end of the field (HashMap.key) for each row beyond the first.
     * <b>MultiRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     * firstName_2, 'Fred Gwynne'
     * lastName_2, 'Sally Munster'
     *
     * <b>SingleRow Query</b>
     * firstName, 'Joe Smith'
     * lastName, 'Jane Dean'
     *
     * This version of getForm gets data from the web_query and web_form_mapping only.  This
     * uses the old parameter replacement technique.
     *
     * @param conn       JDBC Connection
     * @param formCode   web_form.code
     * @param queryParms Map of parameters to bind to the web query.
     * @return HashMap
     * @throws SQLException
     */
    public HashMap getForm(Connection conn, String formCode, Map queryParms) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "getForm", new Object[]{conn, formCode, queryParms});
        HashMap fieldsMap = new HashMap();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            WebFormInfo info = getFormFks(conn, formCode);
            if (info != null) {
                HashMap map = getMap(conn, formCode);
                stmt = WebQuery.getInstance().prepareStatement(conn,info.getQueryFk(), queryParms);
                rs = stmt.executeQuery();
                String[] cols = metaDataToColumnArray(rs.getMetaData());
                int i = 0;
                while (rs.next()) {
                    processRow(rs, map, cols, ++i, fieldsMap);
                }
            }
            l.exiting(getClass().getName(), "getForm", fieldsMap);
            return fieldsMap;
        }
        finally {
            DatabaseUtils.close(stmt, rs);
        }
    }
    /**
     * Converts ResultSetMetaData to an Array of column names
     *
     * @param rsmd Live ResultSetMetaData
     * @return String Array of column names from meta data
     * @throws SQLException
     */
    public String[] metaDataToColumnArray(ResultSetMetaData rsmd) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "metaDataToColumnArray", rsmd);
        int sz = rsmd.getColumnCount();
        String[] cols = new String[rsmd.getColumnCount()];
        for (int i = 1; i <= sz; i++)
            cols[i - 1] = rsmd.getColumnName(i);
        l.exiting(getClass().getName(), "metaDataToColumnArray", cols);
        return cols;
    }

    /**
     * Processes a single row in a Live ResultSet.  Creates a HashMap which links
     * a form field to a value
     *
     * @param rs        Live JDBC ResultSet
     * @param map       HashMap linking SQL Column names with form field names
     * @param cols      String Array of column names that would have been taken
     *                  from the ResultSet's ResultSetMetaData
     * @param fieldsMap Map containing fields and values
     * @throws SQLException
     */
    protected void processRow(ResultSet rs, HashMap map, String[] cols, int row, HashMap fieldsMap) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "processRow", new Object[]
        {rs, map, cols, new Integer(row), fieldsMap});
        int sz = cols.length;
        String rowStr = (row == 1) ? "" : new StringBuffer("_").append(row).toString();

        for (int i = 0; i < sz; i++) {
            // Get value
            String value = rs.getString(i + 1);
            // Find the form field related to this column name
            String fieldId = (String) map.get(cols[i]);
            if (!StringUtils.isBlank(fieldId))
                fieldsMap.put(new StringBuffer(fieldId).append(rowStr).toString(), value);
        }
        l.exiting(getClass().getName(), "processRow");

    }

    protected void saveFormDataRows(PreparedStatement pStmt, Map data) throws SQLException {
        Logger l = LogUtils.enterLog(getClass(), "saveFormDataRows", new Object[]{pStmt, data});

        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            String field = (String) it.next();
            if (field != null) {
                String value = (String) data.get(field);
                pStmt.setString(4, field);
                pStmt.setString(5, value);
                l.fine(new StringBuffer("Executing with ").append(field).append(',').append(value).toString());
                pStmt.executeUpdate();
            }
        }
        l.exiting(getClass().getName(), "saveFormData");
    }

    /**
     * Saves Form Data.  Expects formCode, sourceRecordFk, & sourceTableName
     * to be in the map.
     *
     * @param conn Live JDBC Connection
     * @param data Map of data
     * @throws Exception
     * @deprecated by the UFE
     */
    public void saveFormData(Connection conn, Map data) throws Exception {
        String formCode = (String) data.get(KEY_FORM_CODE);
        String sourceFk = (String) data.get(KEY_SOURCE_RECORD_FK);
        String sourceTbl = (String) data.get(KEY_SOURCE_TABLE_NAME);
        data.remove(KEY_FORM_CODE);
        data.remove(KEY_SOURCE_RECORD_FK);
        data.remove(KEY_SOURCE_TABLE_NAME);
        saveFormData(conn, formCode, sourceFk, sourceTbl, data);
    }

    /**
     * Saves form data
     *
     * @param conn            Live JDBC Connection
     * @param formCode        web_form.code
     * @param sourceRecordFk  web_form_data.source_record_fk
     * @param sourceTableName web_form_data.source_table_name
     * @param data            Map of field,value data
     * @throws SQLException
     * @throws IllegalArgumentException - invalid form code
     * @deprecated by the UFE
     */
    public void saveFormData(Connection conn, String formCode, String sourceRecordFk,
                             String sourceTableName, Map data) throws SQLException, IllegalArgumentException {
        Logger l = LogUtils.enterLog(getClass(), "saveFormData",
                new Object[]{conn, formCode, sourceRecordFk, sourceTableName, data});
        Statement stmt = null;
        PreparedStatement pStmt = null;
        IllegalArgumentException ie = null;
        if (StringUtils.isBlank(formCode))
            ie = new IllegalArgumentException("formCode is required");
        if (StringUtils.isBlank(sourceRecordFk))
            ie = new IllegalArgumentException("sourceRecordFk is required");
        if (StringUtils.isBlank(sourceTableName))
            ie = new IllegalArgumentException("sourceTableName is required");
        if (ie != null) {
            l.throwing(getClass().getName(), "saveFormData", ie);
            throw ie;
        }

        boolean autocommit = conn.getAutoCommit();
        try {

            WebFormInfo info = getFormFks(conn, formCode);
            if (info == null) {
                ie = new IllegalArgumentException("Invalid web_form.code [" + formCode + ']');
                l.throwing(getClass().getName(), "saveFormData", ie);
                throw ie;
            }
            conn.setAutoCommit(false);
            pStmt = conn.prepareStatement(SQL_SAVEDATADEL);
            pStmt.setLong(1, info.getFormPk());
            pStmt.setLong(2, Long.parseLong(sourceRecordFk));
            pStmt.setString(3, sourceTableName);
            l.finer(new StringBuffer("Executing: ").append(SQL_SAVEDATADEL).append(" with ").
                    append(info.getFormPk()).append(',').append(sourceRecordFk).append(',').
                    append(sourceTableName).toString());
            pStmt.executeUpdate();
            pStmt = conn.prepareStatement(SQL_SAVEDATA);
            pStmt.setLong(1, info.getFormPk());
            pStmt.setLong(2, Long.parseLong(sourceRecordFk));
            pStmt.setString(3, sourceTableName);
            l.fine(new StringBuffer("Preparing ").append(SQL_SAVEDATA).append(" with ").
                    append(info.getFormPk()).append(',').append(sourceRecordFk).append(',').
                    append(sourceTableName).toString());
            saveFormDataRows(pStmt, data);
            conn.commit();
            l.exiting(getClass().getName(), "saveFormData");
        }
        catch (SQLException e) {
            conn.rollback();
        }
        finally {
            conn.setAutoCommit(autocommit);
            DatabaseUtils.close(stmt);
            DatabaseUtils.close(pStmt);
        }

    }
}

