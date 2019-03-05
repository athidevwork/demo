package dti.ci.helpers.data;

import dti.ci.helpers.ICIConstants;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;
import dti.oasis.util.QueryParm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

/**
 * DAO for Address copy
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 06, 2007
 *
 * @author bhong
 */

/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 09/22/2008       Larry      Issue 86826
 * 09/01/2009       Jacky      Issue 96609 
 *
 * ---------------------------------------------------
*/
public class CIAddressCopyDAO extends CIBaseDAO implements ICIConstants {
    /**
     * get relation List
     *
     * @param conn
     * @param entityFK
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public DisconnectedResultSet getRelationList(Connection conn, long entityFK)
            throws Exception {
        String methodName = "getRelationList";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, new Long(entityFK)});

        String strSql = getRelationListSQL();
        DisconnectedResultSet rs = Querier.doQuery(strSql, conn,
                new Object[]{new QueryParm(Types.NUMERIC, entityFK),
                        new QueryParm(Types.NUMERIC, entityFK),
                        new QueryParm(Types.NUMERIC, entityFK),
                        new QueryParm(Types.NUMERIC, entityFK),
                        new QueryParm(Types.NUMERIC, entityFK)}, false);

        lggr.exiting(this.getClass().getName(), methodName, rs);

        return rs;
    }

    /**
     * copyAddress
     *
     * @param conn
     * @param data
     * @throws Exception
     */
    public void copyAddress(Connection conn, String data, long addressPK)
            throws Exception {
        String methodName = "copyAddress";
        Logger lggr = LogUtils.enterLog(getClass(), methodName,
                new Object[]{conn, data, new Long(addressPK)});
        CallableStatement stmt = null;
        String strSql = getUpdateSql();
        String sqlMsg = new StringBuffer("Executing: ").append(strSql)
                .append(" with XML data document: ")
                .append(data)
                .append("; address PK:")
                .append(addressPK)
                .toString();
        lggr.fine(sqlMsg);

        try {
            stmt = conn.prepareCall(strSql);
            stmt.setString(1, data);
            stmt.setLong(2, addressPK);
            stmt.executeUpdate();
            lggr.exiting(this.getClass().getName(), methodName);
        } catch (SQLException e) {
            conn.rollback();
            lggr.severe(new StringBuffer().append(
                    "Exception occurred copying address.\nSQL statement:  ")
                    .append(sqlMsg).append("\nException:")
                    .append(e).toString());
            throw e;
        } finally {
            if(stmt!=null)
            close(stmt);
        }
    }

    /**
     * Get relation list SQL
     *
     * @return String
     */
    protected String getRelationListSQL() {
        String methodName = "getRelationListSQL";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName);

        String strSql = "SELECT DISTINCT \n" +
                "       ENTITY_RELATION.ENTITY_RELATION_PK,\n" +
                "       '0' select_ind,\n" +
                "       ENTITY_RELATION.ENTITY_PARENT_FK entityParentFk, \n" +
                "       ENTITY_RELATION.ENTITY_CHILD_FK entityChildFk, \n" +
                "       DECODE(?, \n" +
                "              ENTITY_RELATION.ENTITY_CHILD_FK, CS_GET_CLIENT_NAME(ENTITY_PARENT_FK),\n" +
                "              CS_GET_CLIENT_NAME(ENTITY_CHILD_FK)) NAME,\n" +
                "       cs_report.get_lookup_code('RELATION_TYPE_CODE',ENTITY_RELATION.RELATION_TYPE_CODE, 'SHORT') relationTypeCodeDesc , \n" +
                "       ENTITY_RELATION.EFFECTIVE_FROM_DATE, \n" +
                "       ENTITY_RELATION.EFFECTIVE_TO_DATE,               \n" +
                "       addl_data1,       \n" +
                "       DECODE(CS_GET_SYSTEM_PARAMETER('CM_CLIENTREL_ADDL2'), '', '',\n" +
                "              SUBSTR(CI_ADDL_INFO.CLIENT_RELATION(ENTITY_RELATION_PK,?,DECODE(?, \n" +
                "              ENTITY_RELATION.ENTITY_CHILD_FK, ENTITY_RELATION.ENTITY_PARENT_FK, ENTITY_RELATION.ENTITY_CHILD_FK),\n" +
                "              CS_GET_SYSTEM_PARAMETER('CM_CLIENTREL_ADDL2')), 1, 100)) ADDL_INFO2 \n" +
                "       ,'0' address_primaryAddressB \n " +
                "  FROM ENTITY_RELATION\n" +
                " WHERE (   ENTITY_RELATION.ENTITY_PARENT_FK = ?  \n" +
                "        OR ENTITY_RELATION.ENTITY_CHILD_FK = ?)\n" +
                " AND (   ENTITY_RELATION.ACCOUNTING_TO_DATE IS NULL\n" +
                "        OR (    ENTITY_RELATION.ACCOUNTING_TO_DATE = TO_DATE('01/01/3000','MM/DD/YYYY')\n" +
                "            AND ENTITY_RELATION.RECORD_MODE_CODE = 'OFFICIAL')) " +
                " AND (ENTITY_RELATION.EFFECTIVE_TO_DATE IS NULL OR ENTITY_RELATION.EFFECTIVE_TO_DATE >= sysdate)";

        lggr.exiting(this.getClass().getName(), methodName, strSql);

        return strSql;
    }

    /**
     * getUpdateSql
     *
     * @return String
     */
    public String getUpdateSql() {
        return "{ call WB_CLIENT_UTILITY.COPY_ADDRESS(?,?) } ";
    }
}