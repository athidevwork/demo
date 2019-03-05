package dti.oasis.util;

import dti.oasis.util.*;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Event utility methods
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 *
 * @author Sharon Ma
 * Date:   Apr 6, 2005
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/25/2008        Larry       Issue 86826 DB connection leakage change
 * 
 *
 * ---------------------------------------------------
 */

public class EventUtils {
    protected final static String clsName = EventUtils.class.getName();
    
    protected final static String SQL_PROFEVENT = 
            "SELECT LC.code, LC.long_description FROM lookup_code LC, pfprof_web_event PWE, pfuser_prof PP " +
             "WHERE LC.lookup_type_code='WBEVENT' AND LC.code=PWE.event AND PWE.profile = PP.profile AND upper(PP.userid) = upper(?) " +
             "ORDER BY 2";

    protected final static String SQL_USEREVENT =
            "SELECT LC.code, LC.long_description FROM lookup_code LC, pfuser_web_event PWE " +
            "WHERE LC.lookup_type_code='WBEVENT' AND LC.code=PWE.event AND upper(PWE.userid) = upper(?) " +
            "ORDER BY 2";
    protected final static String SQL_DELEVENT =
            "DELETE pfuser_web_event WHERE upper(userid) = upper(?) ";
    protected final static String SQL_INSEVENT =
            "INSERT INTO pfuser_web_event (pfuser_web_event_pk, userid, event) " +
            "SELECT oasis_sequence.nextval, upper(?), ? FROM DUAL";

    protected final static String SQL_USEREMAIL =
            "SELECT nvl(email_address1, nvl(email_address2, email_address3)) FROM entity, pfuser " +
             "WHERE entity_pk = entity_fk AND upper(userid) = upper(?)";
    protected final static String SQL_UPDEMAIL =
            "UPDATE entity SET email_address1 = ? WHERE entity_pk = (" +
            "SELECT entity_fk FROM pfuser WHERE upper(userid) = upper(?))";

    /**
     * Get list of events a user can sign up for
     *
     * @param dbPoolId
     * @param userId
     * @return ArrayList of pfprof events
     * @throws SQLException
     * @throws NamingException
     */
    public static ArrayList getProfEvents(String dbPoolId, String userId) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(EventUtils.class, "getProfEvents", new Object[] {dbPoolId, userId});
        Connection conn = null;
        try {
            conn = DBPool.getConnection(dbPoolId);
            ArrayList list = Querier.doListQuery(SQL_PROFEVENT, conn,
                    new Object[]{new QueryParm(Types.VARCHAR, userId)}, 2, 1, false);
            l.exiting(clsName, "getProfEvents", list);
            return list;
        }
        finally {
                if (conn != null) DatabaseUtils.close(conn);             
        }
    }

    /**
     * Get list of events a user signed up for
     *
     * @param dbPoolId
     * @param userId
     * @return ArrayList of user events
     * @throws SQLException
     * @throws NamingException
     */
    public static ArrayList getUserEvents(String dbPoolId, String userId) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(EventUtils.class, "getUserEvents", new Object[] {dbPoolId, userId});
        Connection conn = null;
        try {
            conn = DBPool.getConnection(dbPoolId);
            ArrayList list = Querier.doListQuery(SQL_USEREVENT, conn,
                    new Object[]{new QueryParm(Types.VARCHAR, userId)}, 2, 1, false);
            l.exiting(clsName, "getUserEvents", list);
            return list;
        }
        finally {
              if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Get email address of a user
     *
     * @param dbPoolId
     * @param userId
     * @return email address
     * @throws SQLException
     * @throws NamingException
     */
    public static String getUserEmail(String dbPoolId, String userId) throws SQLException, NamingException {
        Logger l = LogUtils.enterLog(EventUtils.class, "getUserEmail", new Object[] {dbPoolId, userId});
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_USEREMAIL);
            stmt.setString(1, userId);
            l.finer(new StringBuffer("Executing: ").append(SQL_USEREMAIL).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            String email = null;
            while (rs.next())
                email = rs.getString(1);
            l.exiting(clsName, "getUserEmail", email);
            return email;
        }
        finally {
                if (rs != null) DatabaseUtils.close(rs);
                if (stmt != null) DatabaseUtils.close(stmt);
                if (conn != null) DatabaseUtils.close(conn);              
        }
    }

    /**
     * Get list of events a user signed up for
     *
     * @param dbPoolId
     * @param userId
     * @param email     eMail address
     * @param events    Delimited List of events a user signed up for
     * @param delim     Delimiter to use when parsing events
     * @throws SQLException
     * @throws NamingException
     */
    public static void updUserEvents(String dbPoolId, String userId, String email, String events, String delim) throws Exception {
        Logger l = LogUtils.enterLog(EventUtils.class, "updUserEvents", 
                new Object[] {dbPoolId, userId, email, events, delim});
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean auto = true;
        try {
            conn = DBPool.getConnection(dbPoolId);
            auto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            // update email
            stmt = conn.prepareStatement(SQL_UPDEMAIL);
            stmt.setString(1, email);
            stmt.setString(2, userId);
            l.finer(new StringBuffer("Executing: ").append(SQL_UPDEMAIL).append(" with ").append(email)
                    .append(", ").append(userId).toString());
            stmt.executeUpdate();
            DatabaseUtils.close(stmt);
            // delete all events first
            stmt = conn.prepareStatement(SQL_DELEVENT);
            stmt.setString(1, userId);
            l.finer(new StringBuffer("Executing: ").append(SQL_DELEVENT).append(" with ").append(userId).toString());
            stmt.executeUpdate();
            DatabaseUtils.close(stmt);
            // now add selected events
            int count = 0;
            if (!StringUtils.isBlank(events)) {
                stmt = conn.prepareStatement(SQL_INSEVENT);
                stmt.setString(1, userId);
                StringTokenizer tok = new StringTokenizer(events, delim);
                while (tok.hasMoreTokens()) {
                    String event = tok.nextToken();
                    l.fine(new StringBuffer("Batching ").append(SQL_INSEVENT).
                            append(" with ").append(userId).append(",").append(event).toString());
                    stmt.setString(2, event);
                    stmt.addBatch();
                    count++;
                }
            }
            if (count > 0)
                stmt.executeBatch();
            // done
            conn.commit();
            l.exiting(clsName, "updUserEvents");
        }
        catch (Exception e) {
            if (conn != null) conn.rollback();
            l.throwing(clsName, "updateuser", e);
            throw e;
        }
        finally {
                if (stmt != null) DatabaseUtils.close(stmt);
                if (conn != null) {
                    conn.setAutoCommit(auto);
                    DatabaseUtils.close(conn);
                }
        }
    }
}