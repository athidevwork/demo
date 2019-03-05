package dti.oasis.security;

import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.*;
import dti.oasis.app.AppException;
import dti.oasis.app.ApplicationContext;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.session.UserSessionManager;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionIds;
import dti.oasis.messagemgr.MessageManager;

import javax.naming.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import oracle.jdbc.OracleTypes;

/**
 * Authentication Class. It is used to authenticate
 * a user against a database.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 21, 2003
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/22/2003       jbe     Changed isUserValid to return OasisUser object
 *                          Added isPasswordExpired method,updateLoggedInDate & updatePasswordChanged
 * 1/28/2004        jbe     Add internal_user_b to OasisUser
 * 2/5/2004         jbe     Add logging
 * 2/10/2004        jbe     Added validateNewUser & createUser
 * 3/3/2004         jbe     Added isUserInProfile
 * 3/8/2004         jbe     validateNewUser - changes
 * 3/17/2004        jbe     New methods - getUsers, getUserProfs, getProfiles,
 *                          getUserData, removeUser, updateUser
 * 6/7/2004         jbe     Physically delete users and profiles
 *                          Refactor method updateProfiles
 *                          Call updateProfiles from createUser
 * 7/15/2004        jbe     Switch to PreparedStatements
 * 8/4/2004         jbe     Catch UserAlreadyExistsException instead of Weblogic's
 *                          AlreadyExistsException.
 * 9/7/2004         jbe     Added isUserExists
 * 12/10/2004       jbe     Get pfuser.entity_fk and store in OasisUser
 * 2/22/2005        jbe     Add password_reminder to SQL's. Add updatePasswordReminder
 *                          Change updateUser, createUser methods
 * 3/30/2005        jbe     Add isAgent and agent enroll logic. Support creating ORG users.
 * 6/23/2005		jbe		Add validateRequest
 * 11/1/2005        jbe     Add support for wildcards in validateRequest.  Change to "requireReferrer"
 *                          env setting - Add "C" for optional/validated. "N" or missing now means not
 *                          validated at all.
 * 06/12/2006       sxm     modify isUserValid to:
 *                          1. set db pool name to default if it's not passed in as parameter
 *                          2. validate db pool name against environment variable "dbPoolId"
 * 06/29/2006       sxm     add user name to SQL_GETUSERLOGGEDIN
 * 07/06/2006       sxm     modify user name logic in SQL_GETUSERLOGGEDIN
 * 07/18/2006       sxm     change user name format from <lastname, firstName> to <firstName lastName>
 * 09/01/2006       sxm     made isDBPoolIdValid() public and more generic 
 * 09/11/2006       sxm     add Synchronization for Maintain User fucntion
 * 12/05/2006       sxm     issue 66177 - display confirmation info for Synchronization
 * 01/23/2007       wer     Changed usage of new Boolean(x) in logging to String.valueOf(x);
 *                          Added loadUser() to load the OasisUser given a userId;
 *                          Changed use of InitialContext to using ApplicationContext
 * 05/29/2007       mdz     issue 69954 - update createUser(Map map, String entityFk, String dbPoolId),
 *                          which replaces direct SQL update with a call to wb_user.update_profile(?,?,?),
 *                          so that risks that are ONLY non-primary risks in Oasis
 *                          only receive the 'web insured' user profile while primary risks
 *                          receive 'web group practice administrator' profile
 * 03/07/2008       wer     Enhanced isUserInProfile to get userId and dbPoolId from UserSessionManager
 * 04/09/2008       wer     Enhanced to user ApplicationContext and MessageManager
 * 09/25/2008       Larry   Issue 86826 DB connection leakage change
 * 07/14/2009       James   Change return type of validateRequest
 * 08/14/2009       kshen   Added method isProfileExist to check if the given profile exists.
 * 04/21/2016       huixu   Issue#169769 Fix WebLogicSecurity.getAuthenticators to work in WebLogic 12.2.1
 * 10/10/2017       Elvin   Issue 188681: close connection
 * ---------------------------------------------------
 */

public class Authenticator {
    protected static final String DEFAULT_PASSWORDCHANGEJSP = "changepassword.jsp";
    protected final static char[] invalidChars = {' ', '\'', '"', '(', ')', '/', '\\', '*', '+', '<', '>', '&', ';'};
    protected final static String clsName = Authenticator.class.getName();
    protected final static String ENV_PROFILE_AGENT = "OasisProfileAgent";
    protected final static String SQL_USER = "SELECT 1 FROM pfuser WHERE upper(userid) = upper(?)";
    protected final static String SQL_USERPROF = "SELECT 1 FROM pfuser_prof " +
            "WHERE UPPER(userid)=UPPER(?) AND trunc(effective_date) <= sysdate AND status= 'A' " +
            "AND application = ? AND profile = ?";
    protected final static String SQL_USERS = "SELECT upper(userid) from pfuser where nvl(web_user_b,'N') = 'Y'" +
            " AND status = 'A' ORDER BY upper(userid)";
    protected final static String SQL_PROFS = "SELECT DISTINCT upper(profile),upper(profile) from pfprof " +
            "WHERE status = 'A' AND application = 'OASIS' ORDER BY upper(profile)";
    protected final static String SQL_IS_PROF_EXIST = "SELECT 1 from pfprof " +
            "WHERE status = 'A' AND profile = ? AND application = ? ORDER BY upper(profile)";
    protected final static String SQL_USERPROFS = "SELECT profile FROM pfuser_prof WHERE upper(userid) = upper(?)" +
            " AND application='OASIS' AND status = 'A' and effective_date <= sysdate";
    protected final static String SQL_UPDREMINDER = "UPDATE pfuser SET password_reminder = substr(?,1,100) " +
            "WHERE UPPER(userid) = upper(?)";
    protected final static String SQL_UPDUSER = "UPDATE pfuser SET first_name = substr(?,1,20), last_name = substr(?,1,20)," +
            " password_reminder = nvl(substr(?,1,100),password_reminder) " +
            "WHERE upper(userid) = upper(?) AND status = 'A'";

    protected final static String SQL_DELPROF = "DELETE pfuser_prof WHERE upper(userid) = upper(?)";
    protected final static String SQL_DELUSER = "DELETE pfuser WHERE upper(userid) = upper(?)";
    protected static final String SQL_USERSTATUS = "SELECT status FROM pfuser WHERE upper(userid) = upper(?)";
    protected static final String SQL_UPDFULLUSER = "UPDATE pfuser SET first_name=substr(?,1,20), last_name = substr(?,1,20)," +
            " status = 'A', " +
            "no_unsucc_logons=0, entity_fk=?, internal_user_b='N', web_user_b='Y', " +
            "password_update_date=sysdate, password_reminder = nvl(substr(password_reminder,1,100),?) " +
            "WHERE upper(userid)=upper(?)";
    protected static final String SQL_INSERTUSER = "INSERT INTO pfuser(first_name, last_name, status," +
            "no_unsucc_logons, entity_fk, password_reminder,userid, internal_user_b, web_user_b," +
            "password_update_date, audit_create_date) values (substr(?,1,20),substr(?,1,20),'A',0,?," +
            "substr(?,1,100),?,'N','Y',sysdate,sysdate)";
    protected static final String SQL_INSERTLOGINCHANGELOG = "INSERT INTO web_password_change_log(pw_change_log_pk," +
            "pw_user, changed_by_user, password_update_date) values (oasis_sequence.nextval,?,?,sysdate)";
    protected static final String SQL_GETUSERLOGGEDIN =
            "SELECT last_logged_in_date, password_update_date, " +
                    "upper(nvl(internal_user_b,'N')), entity_fk, " +
                    "decode(first_name, NULL, '', first_name||' ')||decode(middle_name, NULL, '', middle_name||' ')||last_name user_name " +
                    "from pfuser where upper(userid) = upper(?)";
    protected static final String SQL_USERVALID = "SELECT round(sysdate-nvl(last_logged_in_date,sysdate),0), status " +
            "from pfuser where upper(userid) = upper(?)";
    protected static final String SQL_UPDLOGGEDIN = "UPDATE pfuser set last_logged_in_date = sysdate WHERE upper(userid) = upper(?)";
    protected static final String SQL_UPDPASSUPDATE = "UPDATE pfuser set password_update_date = sysdate WHERE upper(userid) = upper(?)";
    protected static final String SQL_ISAGENT = "SELECT 1 FROM agent WHERE entity_fk = ? " +
            "AND nvl(effective_start_date,sysdate) <= sysdate AND " +
            "nvl(effective_end_date,to_date('01/01/3000','mm/dd/yyyy')) > trunc(sysdate)";
    protected static final String SQL_GETUSERNAME =
            "SELECT decode(e.entity_type, 'P', decode(e.first_name, NULL, '', e.first_name||' ')||" +
                    "decode(e.middle_name, NULL, '', e.middle_name||' ')||" +
                    "e.last_name, 'O', e.organization_name) user_name " +
                    "FROM pfuser u , entity e " +
                    "WHERE u.entity_fk = e.entity_pk AND upper(u.userid) = upper(?)";
    protected final static String SQL_USERGROUPS = "{call ? := wb_user.get_user_groups}";
    private static final String SQL_UPDATE_PROFILE = "{ call wb_user.update_profile(?,?,?) }";

    /**
     * Deactivates a user and his/her profiles
     *
     * @param dbPoolId
     * @param userId
     * @throws Exception
     */
    public static void removeUser(String dbPoolId, String userId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean auto = true;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "removeUser", new Object[]{dbPoolId, userId});
        }

        try {
            conn = DBPool.getConnection(dbPoolId);
            auto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(SQL_DELPROF);
            stmt.setString(1, userId);
            // Delete the profile first
            l.fine(new StringBuffer("Executing: ").append(SQL_DELPROF).append(" with ").append(userId).toString());
            stmt.executeUpdate();
            DatabaseUtils.close(stmt);
            // Next, delete the user
            stmt = conn.prepareStatement(SQL_DELUSER);
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_DELUSER).append(" with ").append(userId).toString());
            stmt.executeUpdate();
            conn.commit();
            l.exiting(clsName, "removeUser");
        }
        catch (Exception e) {
            if (conn != null) conn.rollback();
            l.throwing(clsName, "removeUser", e);
            throw e;
        }
        finally {
            try {
                if (conn != null) conn.setAutoCommit(auto);
            }
            catch (SQLException ignore) {
            }
            finally {
                if (stmt != null) DatabaseUtils.close(stmt);
                if (conn != null) DatabaseUtils.close(conn);
            }
        }
    }

    /**
     * Gets data from the pfuser table for a user. For security purposes this method
     * should never be called based on parameters provided by an outside source.  The
     * ArrayList of fields should not be externally populated.
     *
     * @param dbPoolId DB Connection pool id
     * @param fields   ArrayList of column names from pfuser
     * @param userId   Userid from pfuser
     * @return HashMap of column/value
     * @throws Exception
     */
    public static Map getUserData(String dbPoolId, ArrayList fields, String userId) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getUserData", new Object[]{dbPoolId, fields, userId});
        }

        if (!isUserIdValidChars(userId)) {
            String msg = "Invalid userid " + userId;
            l.warning(msg);
            return null;
        }
        Map data = new HashMap();
        try {
            conn = DBPool.getConnection(dbPoolId);

            int sz = fields.size();
            if (sz == 0)
                throw new IllegalArgumentException("One or more fields are required.");
            StringBuffer sql = new StringBuffer("SELECT ");
            for (int i = 0; i < sz; i++) {
                sql.append(fields.get(i)).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" FROM pfuser WHERE upper(userid) = upper(?)");
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(sql).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < sz; i++)
                    data.put(fields.get(i), rs.getString(i + 1));
            }
            l.exiting(clsName, "getUserData", data);
            return data;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Updates pfuser.password_reminder
     *
     * @param dbPoolId Database pool id
     * @param userId   UserId
     * @param reminder New Reminder
     * @throws Exception
     */
    public static void updatePasswordReminder(String dbPoolId, String userId, String reminder) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "updatePasswordReminder", new Object[]{dbPoolId, userId, reminder});
        }

        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_UPDREMINDER);
            if (StringUtils.isBlank(reminder))
                stmt.setNull(1, Types.VARCHAR);
            else
                stmt.setString(1, reminder);
            stmt.setString(2, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_UPDREMINDER).append(" with ").
                    append(reminder).append(',').append(userId).toString());
            stmt.execute();
            l.exiting(clsName, "updatePasswordReminder");
        }
        catch (Exception e) {
            l.throwing(clsName, "updatePasswordReminder", e);
            throw e;
        }
        finally {
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Updates user info
     *
     * @param dbPoolId
     * @param userId
     * @param firstName
     * @param lastName
     * @param profiles  Delimited List of profiles a user should be currently attached to.
     *                  All other profiles will be deactivated.
     * @param delim     Delimiter to use when parsing profiles
     * @param reminder  Reminder key to reset password
     * @throws Exception
     */
    public static void updateUser(String dbPoolId, String userId, String firstName, String lastName,
                                  String profiles, String delim, String reminder) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "updateUser", new Object[]{dbPoolId, userId, firstName, lastName, profiles, delim, reminder});
        }
        boolean auto = true;
        try {
            conn = DBPool.getConnection(dbPoolId);
            auto = conn.getAutoCommit();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(SQL_UPDUSER);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            if (StringUtils.isBlank(reminder))
                stmt.setNull(3, Types.VARCHAR);
            else
                stmt.setString(3, reminder);
            stmt.setString(4, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_UPDUSER).append(" with ").
                    append(firstName).append(',').append(lastName).append(',').append(reminder).append(',').
                    append(userId).toString());
            stmt.executeUpdate();
            DatabaseUtils.close(stmt);
            stmt = conn.prepareStatement(SQL_DELPROF);
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_DELPROF).append(" with ").append(userId).toString());
            stmt.executeUpdate();
            DatabaseUtils.close(stmt);
            // take care of profiles
            updateProfiles(profiles, delim, userId, conn);

            conn.commit();
            l.exiting(clsName, "updateuser");
        }
        catch (Exception e) {
            if (conn != null) conn.rollback();
            l.throwing(clsName, "updateuser", e);
            throw e;
        }
        finally {
            try {
                if (conn != null) conn.setAutoCommit(auto);
            }
            catch (SQLException ignore) {
            }
            finally {
                if (stmt != null) DatabaseUtils.close(stmt);
                if (conn != null) DatabaseUtils.close(conn);
            }
        }

    }

    /**
     * Update pfuser_prof
     *
     * @param profiles Delimiter Separated list of OASIS profiles
     * @param delim    Delimiter
     * @param userId   Userid
     * @param conn     Live JDBC Connection
     * @throws SQLException
     */
    protected static void updateProfiles(String profiles, String delim, String userId, Connection conn) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "updateProfiles", new Object[]{profiles, delim, userId, conn});
        }
        CallableStatement stmt = null;

        try {
            stmt = conn.prepareCall(SQL_UPDATE_PROFILE);
            stmt.setString(1, userId.toUpperCase());
            if (!StringUtils.isBlank(profiles))
                stmt.setString(2, profiles.trim());
            else
                stmt.setNull(2, Types.VARCHAR);
            stmt.setString(3, delim);
            l.fine("Executing: " + SQL_UPDATE_PROFILE + " with " +
                    userId.toUpperCase() + ";" + profiles + ";" + delim);
            stmt.execute();
            l.exiting(clsName, "updateProfiles");
        } finally {
            if (stmt != null) DatabaseUtils.close(stmt);
        }
    }

    /**
     * Get list of profiles a user is in
     *
     * @param dbPoolId
     * @param userId
     * @return ArrayList of pfprof's
     * @throws SQLException
     * @throws NamingException
     */
    public static ArrayList getUserProfs(String dbPoolId, String userId) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getUserProfs", new Object[]{dbPoolId, userId});
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_USERPROFS);
            stmt.setString(1, userId);
            l.finer(new StringBuffer("Executing: ").append(SQL_USERPROFS).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            ArrayList list = new ArrayList(5);
            while (rs.next())
                list.add(rs.getString(1));
            l.exiting(clsName, "getUserProfs", list);
            return list;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Get a list of users from pfuser
     *
     * @param dbPoolId
     * @return ArrayList of userIds
     * @throws SQLException
     * @throws NamingException
     */
    public static ArrayList getUsers(String dbPoolId) throws SQLException, NamingException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getUsers", new Object[]{dbPoolId});
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.createStatement();
            l.finer(new StringBuffer("Executing: ").append(SQL_USERS).toString());
            rs = stmt.executeQuery(SQL_USERS);
            ArrayList list = new ArrayList(15);
            while (rs.next())
                list.add(rs.getString(1));
            l.exiting(clsName, "getUsers", list);
            return list;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Get a list of profiles from pfprof
     *
     * @param dbPoolId
     * @return ArrayList of LabelValueBean objects
     * @throws SQLException
     * @throws NamingException
     */
    public static ArrayList getProfiles(String dbPoolId) throws SQLException, NamingException {
        Connection conn = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getProfiles", new Object[]{dbPoolId});
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            ArrayList list = Querier.doListQuery(SQL_PROFS, conn, 2, 1);
            l.exiting(clsName, "getProfiles", list);
            return list;
        }
        finally {
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Validates a user based on first name, last name, social security #,
     * and license.  Returns the entityPk if the user is valid, null if not.
     * Only the values should come from an external source, for security purposes.
     *
     * @param criteria Map of FROM to ArrayList of Enroller objects
     * @param dbPoolId DB Pool Id
     * @return entityPk (entity.entity_pk), -1 if already registered, or null if not valid
     * @throws java.lang.Exception
     */
    public static String validateNewUser(Map criteria, String dbPoolId) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "validateNewUser", new Object[]{criteria, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuffer buff1 = new StringBuffer("SELECT distinct entity_pk FROM entity ");
        StringBuffer buff = new StringBuffer(100);
        ArrayList list = (ArrayList) criteria.get("entity");
        if (list == null || list.size() < 1)
            throw new IllegalArgumentException("Invalid enrollment criteria.  No Entity information provided.");
        int sz = list.size();
        ArrayList values = new ArrayList(5);
        // Loop through the entity criteria
        for (int i = 0; i < sz; i++) {
            if (i == 0)
                buff.append("WHERE ");
            else
                buff.append("AND ");
            EnrollHelper.Enroller en = (EnrollHelper.Enroller) list.get(i);
            buff.append('(').append(en.where).append("=?)");
            values.add(en.value);
        }
        Iterator it = criteria.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!key.equals("entity")) {
                list = (ArrayList) criteria.get(key);
                if (list != null && list.size() > 0) {
                    buff1.append(",").append(key).append(" ");
                    sz = list.size();
                    for (int i = 0; i < sz; i++) {
                        buff.append("AND ");
                        EnrollHelper.Enroller en = (EnrollHelper.Enroller) list.get(i);
                        buff.append(en.where).append("=? ");
                        values.add(en.value);
                    }
                }
            }
        }
        String sql = buff1.append(buff).toString();
        long entityFk = -1;
        try {

            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(sql);
            sz = values.size();
            for (int i = 0; i < sz; i++) {
                stmt.setString(i + 1, (String) values.get(i));
            }
            l.fine(new StringBuffer("Executing: ").append(sql).append(" with ").append(values).toString());
            rs = stmt.executeQuery();
            if (rs.next())
                entityFk = rs.getLong(1);
            DatabaseUtils.close(rs);
            if (entityFk == -1) {
                l.exiting(clsName, "validateNewUser", null);
                return null;
            }
            sql = "select 1 from pfuser where entity_fk = ?";
            DatabaseUtils.close(stmt);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, entityFk);
            l.fine(new StringBuffer("Executing: ").append(sql).append(" with ").append(entityFk).toString());
            // make sure user isn't already registered.
            rs = stmt.executeQuery();
            if (rs.next())
                entityFk = -1;
            l.exiting(clsName, "validateNewUser", String.valueOf(entityFk));
            return String.valueOf(entityFk);
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);

        }

    }

    /**
     * Creates a user in the pfuser table.  If this is an agent, and we have
     * a default AGENT profile in the web.xml, then that is the only profile
     * we will set up.
     *
     * @param map      Map of firstName, lastName, userId, profiles, reminder
     * @param entityFk Entity.entity_pk
     * @param dbPoolId DB Pool Id
     * @return true if user was created, false if username already exists
     * @throws java.lang.Exception
     */

    public static boolean createUser(Map map, String entityFk, String dbPoolId)
            throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "createUser", new Object[]{map, entityFk, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // get parms
        String firstName = (String) map.get("firstName");
        String lastName = (String) map.get("lastName");
        String userId = (String) map.get("userId");
        String profiles = (String) map.get("profiles");
        String reminder = (String) map.get("reminder");
        // make sure userid has valid chars
        if (!isUserIdValidChars(userId)) {
            String msg = "Invalid userid " + userId;
            l.warning(msg);
            return false;
        }
        // make sure we've got a numeric entity fk
        long lEntity = 0;
        if (StringUtils.isBlank(entityFk))
            entityFk = null;
        else {
            try {
                lEntity = Long.parseLong(entityFk);
            }
            catch (NumberFormatException e) {
                String msg = "Invalid entityFk " + entityFk;
                l.warning(msg);
                return false;
            }
        }
        boolean created = false;
        try {
            if (!StringUtils.isBlank(lastName) && !StringUtils.isBlank(userId)) {
                conn = DBPool.getConnection(dbPoolId);
                // user status query
                stmt = conn.prepareStatement(SQL_USERSTATUS);
                stmt.setString(1, userId);
                l.fine(new StringBuffer("Executing: ").append(SQL_USERSTATUS).append(" with ").append(userId).toString());
                rs = stmt.executeQuery();
                String sql = null;
                // if we got a row and the user status is NOT active, then update stmt
                if (rs.next() && rs.getString(1).charAt(0) != 'A')
                    sql = SQL_UPDFULLUSER;
                else // else insert stmt
                    sql = SQL_INSERTUSER;
                // close resources
                DatabaseUtils.close(rs);
                DatabaseUtils.close(stmt);
                // prepare new sql
                stmt = conn.prepareStatement(sql);
                // set parms
                if (StringUtils.isBlank(firstName))
                    stmt.setNull(1, Types.VARCHAR);
                else
                    stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                if (entityFk == null)
                    stmt.setNull(3, Types.NUMERIC);
                else
                    stmt.setLong(3, lEntity);
                if (StringUtils.isBlank(reminder))
                    stmt.setNull(4, Types.VARCHAR);
                else
                    stmt.setString(4, reminder);
                stmt.setString(5, userId);
                l.fine(new StringBuffer("Executing: ").append(sql).append(" with ").
                        append(firstName).append(',').append(lastName).append(',').
                        append(entityFk).append(',').append(reminder).append(',').
                        append(userId).toString());
                stmt.executeUpdate();
                created = true;
                // Get our default agent profile
                String profileAgent = getEnvString(ENV_PROFILE_AGENT, "0");
                // if we have a default agent profile, and this is an agent,
                // // we should only set up the agent profile.
                if (!profileAgent.equals("0") && isAgent(conn, lEntity)) {
                    profiles = profileAgent;
                }
                // Update any profiles
                updateProfiles(profiles, ",", userId, conn);
            }
            l.exiting(clsName, "createUser", Boolean.valueOf(created));
            return created;
        }
        catch (Exception e) {
            l.throwing(clsName, "createUser", e);
            l.warning("Error creating user " + e);
            throw e;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Updates the pfuser table given the userId & sets the last_logged_in_date
     * to right now
     *
     * @param userId
     * @param dbPoolId
     * @throws java.lang.Exception
     */
    public static void updateLoggedInDate(String userId, String dbPoolId) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "updateLoggedInDate", new Object[]{userId, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        if (!isUserIdValidChars(userId)) {
            String msg = "Invalid userid " + userId;
            l.warning(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_UPDLOGGEDIN);
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_UPDLOGGEDIN).append(" with ").append(userId).toString());
            stmt.executeUpdate();
            l.exiting(clsName, "updateLoggedInDate");
        }
        finally {
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Updates the pfuser table given the userId & sets the password_update_date
     * to right now
     *
     * @param user
     * @param dbPoolId
     */
    public static void updatePasswordChanged(OasisUser user, String dbPoolId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "updatePasswordChanged", new Object[]{user, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        if (!isUserIdValidChars(user.getUserId())) {
            String msg = "Invalid userid " + user.getUserId();
            l.warning(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_UPDPASSUPDATE);
            stmt.setString(1, user.getUserId());
            l.fine(new StringBuffer("Executing: ").append(SQL_UPDPASSUPDATE).append(" with ").
                    append(user.getUserId()).toString());
            stmt.executeUpdate();
            user.setPasswordUpdated(new java.util.Date());
            //LOGGING PASSWORD CHANGE
            logPasswordChanged(user, conn);
            l.exiting(clsName, "updatePasswordChanged");
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to update the password.", e);
            l.throwing(Authenticator.class.getName(), "updatePasswordChanged", ae);
            throw ae;
        }
        finally {
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }

    }

    /**
     * Updates the web_password_change_log table given the userId & sets the password_update_date
     * to right now
     *
     * @param user
     * @param conn
     * @throws java.lang.Exception
     */
    private static void logPasswordChanged(OasisUser user, Connection conn) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "logPasswordChanged", new Object[]{user, conn});
        }
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(SQL_INSERTLOGINCHANGELOG);
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUserId());
            l.fine(new StringBuffer("Executing: ").append(SQL_INSERTLOGINCHANGELOG).append(" with ").
                    append(user.getUserId()).toString());
            stmt.executeUpdate();
            l.exiting(clsName, "logPasswordChanged");
        }
        finally {
            if (stmt != null) DatabaseUtils.close(stmt);
        }

    }

    /**
     * Loads the OasisUser for the specified user id.
     * The default Database Pool Id is used.
     * If the OasisUser can not be loaded, an AppException is thrown.
     *
     * @throws AppException if the Oasis User could not be determined.
     */
    public static OasisUser loadUser(String userId) throws AppException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "loadUser", new Object[]{userId});
        }

        OasisUser oasisUser = loadUser(userId, DatabaseUtils.getDefaultDBPoolId());

        l.exiting(Authenticator.class.getName(), "loadUser");
        return oasisUser;
    }

    /**
     * Loads the OasisUser for the specified user id.
     * If the OasisUser can not be loaded, an AppException is thrown.
     *
     * @throws AppException if the Oasis User could not be determined.
     */
    public static OasisUser loadUser(String userId, String dbPoolId) throws AppException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "loadUser", new Object[]{userId, dbPoolId});
        }

        // Load the OasisUser information
        OasisUser oasisUser = null;
        try {
            oasisUser = Authenticator.getUser(userId, dbPoolId);
            if (oasisUser == null) {
                AppException ae = new AppException("Failed to determine the Oasis User.");
                l.throwing(Authenticator.class.getName(), "getUserSession", ae);
                throw ae;
            }
        } catch (Exception e) {
            AppException ae = new AppException("Failed to determine the Oasis User.", e);
            l.throwing(Authenticator.class.getName(), "getUserSession", ae);
            throw ae;
        }

        l.exiting(Authenticator.class.getName(), "getUser");
        return oasisUser;
    }

    /**
     * Loads OasisUser object.
     *
     * @param userId
     * @param dbPoolId
     * @return OasisUser object if valid, NULL if invalid.
     * @throws java.lang.Exception
     */
    public static OasisUser getUser(String userId, String dbPoolId) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getUser", new Object[]{userId, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        OasisUser user = null;
        if (!isUserIdValidChars(userId)) {
            String msg = "Invalid userid " + userId;
            l.warning(msg);
            return null;
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_GETUSERLOGGEDIN);
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_GETUSERLOGGEDIN).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new OasisUser(userId, rs.getTimestamp(1), rs.getDate(2),
                        (rs.getString(3).charAt(0) == 'Y'));
                user.setEntityPk(rs.getLong(4));
                user.setUserName(rs.getString(5));
                if (user.getEntityPk() > 0) {
                    // replace the pfuser name with entity name
                    stmt = conn.prepareStatement(SQL_GETUSERNAME);
                    stmt.setString(1, userId);
                    l.fine(new StringBuffer("Executing: ").append(SQL_GETUSERNAME).append(" with ").append(userId).toString());
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        user.setUserName(rs.getString(1));
                    }
                }
            }
            l.exiting(clsName, "getUser", user);
            return user;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Validates user against OASIS database. If user is valid, return null,
     * else return a message..
     *
     * @param userId
     * @param dbPoolId
     * @return String null if ok, else a message
     * @throws java.lang.Exception
     */
    public static String isUserValid(String userId, String dbPoolId) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isUserValid", new Object[]{userId, dbPoolId});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String msg = null;
        String invalidLoginMsg = MessageManager.getInstance().formatMessage("login.invalid.error");
        if (!isUserIdValidChars(userId)) {
            l.logp(Level.WARNING, Authenticator.class.getName(), "isUserValid", "Invalid characters in User ID: " + userId);
            msg = invalidLoginMsg;
            l.exiting(clsName, "isUserValid", msg);
            return msg;
        }
        // set pool id to default if it's not passed in
        if (StringUtils.isBlank(dbPoolId)) {
            l.logp(Level.WARNING, Authenticator.class.getName(), "isUserValid", "Missing dbPoolId");
            msg = invalidLoginMsg;
            l.exiting(clsName, "isUserValid", msg);
            return msg;
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_USERVALID);
            stmt.setString(1, userId);
            l.fine(new StringBuffer("Executing: ").append(SQL_USERVALID).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString(2);
                if (!status.trim().toUpperCase().equals("A")) {
                    l.logp(Level.WARNING, Authenticator.class.getName(), "isUserValid", "User: " + userId + " is an inactive user for selected database<" + dbPoolId + ">.");
                    msg = invalidLoginMsg;
                } else {
                    int numDays = getEnvInt(IOasisAction.KEY_ENVINACTIVITYLOCKDAYS, 0);
                    if (numDays > 0 && rs.getInt(1) > numDays) {
                        msg = MessageManager.getInstance().formatMessage("login.inactivity.lock.error");
                    }
                }
            } else {
                l.logp(Level.WARNING, Authenticator.class.getName(), "isUserValid", "User: " + userId + " is not valid for selected database<" + dbPoolId + ">.");
                msg = invalidLoginMsg;
            }
            l.exiting(clsName, "isUserValid", msg);
            return msg;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    public static String getEnvString(String key, String dftVal) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getEnvString", new Object[]{key, dftVal});
        }
        String sVal = ApplicationContext.getInstance().getProperty(key, dftVal);
        l.exiting(clsName, "getEnvInt", sVal);
        return sVal;

    }

    public static int getEnvInt(String key, int dftVal) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "getEnvInt", new Object[]{key, dftVal});
        }
        int rc = Integer.parseInt(getEnvString(key, String.valueOf(dftVal)));

        l.exiting(clsName, "getEnvInt", new Integer(rc));
        return rc;
    }

    /**
     * Determines if the user's password has expired. If the ignorePasswordExp
     * attribute of the OasisUser has been set to true or the user is anonymous,
     * then simply return false.
     * If the value found in Context for IOasisAction.KEY_ENVPASSWORDEXPDAYS is
     * either 0, or no value is found, then return false.
     * If the user's password has yet to be updated, then it has expired.
     * Otherwise, we add [numDays] to the password's last update date.
     * If the result is before today, then the password has expired.
     *
     * @param user User object
     * @return true if expired
     */
    public static boolean isPasswordExpired(OasisUser user) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isPasswordExpired", new Object[]{user});
        }
        boolean rc = false;
        // If ignorePasswordExp is true, then password isn't expired right now
        // OR if the user is anonymous, there is no password to expire
        if (!(user.isIgnorePasswordExp() || user.isAnonymous())) {

            // Get the password expiration days count
            // Default to 0, which means it does not expire
            int numDays = getEnvInt(IOasisAction.KEY_ENVPASSWORDEXPDAYS, 0);

            // If numDays is 0, then passwords don't expire
            if (numDays != 0) {
                java.util.Date last = user.getPasswordUpdated();

                // If last update date is null, then password has expired
                if (last == null)
                    rc = true;
                else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(last);
                    cal.add(Calendar.DATE, numDays);
                    rc = (cal.getTime().before(new java.util.Date()));
                }
            }
        }

        l.exiting(clsName, "isPasswordExpired", String.valueOf(rc));
        return rc;
    }

    /**
     * Returns the password change jsp found in the web.xml
     *
     * @return String - password change JSP or "changepassword.jsp" if one is not found
     */
    public static String getPasswordChangeJSP() {
        l.entering(Authenticator.class.getName(), "getPasswordChangeJSP");

        // Get the password expiration days count
        String jsp = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVPASSWORDCHANEGJSP, DEFAULT_PASSWORDCHANGEJSP);

        l.exiting(clsName, "getPasswordChangeJSP", jsp);
        return jsp;

    }

    /**
     * Determines whether the user is assigned to a specific profile in OASIS
     * The userId is retrieved from the UserSessionManager, assumed to be setup for this request.
     *
     * @param profile profile (pfprof.profile)
     * @return true/false
     */
    public static boolean isUserInProfile(String profile) {
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        String userId = userSession.getUserId().toUpperCase();
        String dbPoolId = (String) userSession.get(UserSessionIds.DB_POOL_ID);
        return isUserInProfile(userId, profile, null, dbPoolId);
    }

    /**
     * Determines whether a user is assigned to a specific profile in OASIS
     *
     * @param userId   userid (pfuser.userid)
     * @param profile  profile (pfprof.profile)
     * @param dbPoolId Database Connection Pool Id to use
     * @return true/false
     */
    public static boolean isUserInProfile(String userId, String profile, String dbPoolId) {
        return isUserInProfile(userId, profile, null, dbPoolId);
    }

    /**
     * Determines whether a user is assigned to a specific profile in OASIS
     *
     * @param userId      userid (pfuser.userid)
     * @param profile     profile (pfprof.profile)
     * @param application application (pfprof.application)
     * @param dbPoolId    Database Connection Pool Id to use
     * @return true/false
     */
    public static boolean isUserInProfile(String userId, String profile,
                                          String application, String dbPoolId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isUserInProfile", new Object[]{userId, profile, application, dbPoolId});
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_USERPROF);
            stmt.setString(1, userId);
            if (StringUtils.isBlank(application))
                application = "OASIS";
            stmt.setString(2, application);
            stmt.setString(3, profile);
            l.finer(new StringBuffer("Executing: ").append(SQL_USERPROF).append(" with ").
                    append(userId).append(", ").append(application).append(", ").append(profile).toString());
            rs = stmt.executeQuery();
            boolean ok = rs.next();
            l.exiting(clsName, "isUserInProfile", String.valueOf(ok));
            return ok;

        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if the user is assigned to the profile '" + profile + "'.", e);
            l.throwing(Authenticator.class.getName(), "isUserInProfile", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Check if profile exists.
     * @param profile
     * @return
     */
    public static boolean isProfileExist(String profile) {
        UserSession userSession = UserSessionManager.getInstance().getUserSession();
        String dbPoolId = (String) userSession.get(UserSessionIds.DB_POOL_ID);
        return isProfileExist(profile, dbPoolId, null);
    }

    /**
     * Check if profile exists.
     * @param profile
     * @param dbPoolId
     * @return
     */
    public static boolean isProfileExist(String profile, String dbPoolId) {
        return isProfileExist(profile, dbPoolId, null);
    }

    /**
     * Check if profile exists.
     * @param profile
     * @param dbPoolId
     * @param application
     * @return
     */
    public static boolean isProfileExist(String profile, String dbPoolId, String application) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isProfileExist", new Object[]{profile, dbPoolId, application});
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_IS_PROF_EXIST);
            stmt.setString(1, profile);
            if (StringUtils.isBlank(application)) {
                application = "OASIS";
            }
            stmt.setString(2, application);

            l.finer(new StringBuffer("Executing: ").append(SQL_IS_PROF_EXIST).append(" with ")
                    .append(application).append(", ").append(profile).toString());
            rs = stmt.executeQuery();
            boolean ok = rs.next();
            l.exiting(clsName, "isProfileExist", String.valueOf(ok));
            return ok;
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to determine if profile '" + profile + " exists'.", e);
            l.throwing(Authenticator.class.getName(), "isProfileExist", ae);
            throw ae;
        } finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Does the user id have valid characters
     *
     * @param userId
     * @return true/false
     */
    protected static boolean isUserIdValidChars(String userId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isUserIdValidChars", new Object[]{userId});
        }

        int sz = invalidChars.length;
        for (int i = 0; i < sz; i++)
            if (userId.indexOf(invalidChars[i]) > -1) {
                l.logp(Level.WARNING, Authenticator.class.getName(), "isUserIdValidChars", "Use of invalid character in User ID: " + invalidChars[i]);
                return false;
            }
        l.exiting(Authenticator.class.getName(), "isUserIdValidChars");
        return true;
    }

    /**
     * Does the DBPool Id have valid characters
     * Valid Char set: a-z, ,A-Z, 0-9, /, space, _ and -
     * Regex: [a-zA-Z0-9/\x20_-]+
     *
     * @param dbPoolId
     * @return true
     */
    protected static boolean isDBPoolIdValidChars(String dbPoolId) {
        return dbPoolId.matches("[a-zA-Z0-9/\\x20_-]+");
    }

    /**
     * Determines if a user exists in OASIS (pfuser)
     *
     * @param userId
     * @param dbPoolId
     * @return
     * @throws SQLException
     * @throws NamingException
     */
    public static boolean isUserExists(String userId, String dbPoolId) throws SQLException, NamingException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isUserExists", new Object[]{userId, dbPoolId});
        }
        try {
            conn = DBPool.getConnection(dbPoolId);
            stmt = conn.prepareStatement(SQL_USER);
            stmt.setString(1, userId);
            l.finer(new StringBuffer("Executing: ").append(SQL_USER).append(" with ").append(userId).toString());
            rs = stmt.executeQuery();
            boolean ok = rs.next();
            l.exiting(clsName, "isUserExists", String.valueOf(ok));
            return ok;

        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
            if (conn != null) DatabaseUtils.close(conn);
        }
    }

    /**
     * Determines whether a given entity is currently an agent.
     *
     * @param conn     Live JDBC Connection
     * @param entityFk entity.entity_pk
     * @return true if current record found on agent with entity_fk
     * @throws SQLException
     */
    public static boolean isAgent(Connection conn, long entityFk) throws SQLException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "isAgent", new Object[]{entityFk});
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_ISAGENT);
            stmt.setLong(1, entityFk);
            l.fine(new StringBuffer("Executing: ").append(SQL_ISAGENT).append(" with ").append(entityFk).toString());
            rs = stmt.executeQuery();
            boolean rc = rs.next();
            l.exiting(clsName, "isAgent", String.valueOf(rc));
            return rc;
        }
        finally {
            if (rs != null) DatabaseUtils.close(rs);
            if (stmt != null) DatabaseUtils.close(stmt);
        }

    }

    /**
     * Validates that the current HTTP Request has come from an approved referrer.  If not,
     * it will send an HttpServletResponse.SC_FORBIDDEN.
     *
     * @param request  Current HttpServletRequest
     * @param response Current httpServletResponse
     * @return
     * @throws Exception
     */
    public static boolean validateRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (l.isLoggable(Level.FINER)) {
            l.entering(Authenticator.class.getName(), "validateRequest", new Object[]{request, response});
        }
        // How do we look at it.
        String referrerFlag = Authenticator.getEnvString(IOasisAction.KEY_ENVREQUIREREFERRER, "N");
        // is a referrer mandatory?
        boolean requireReferrer = referrerFlag.equals("Y");
        // Do we check the referrer
        boolean checkReferrer = (requireReferrer || referrerFlag.equals("C"));
        if (checkReferrer) {
            // who got us here
            String referrer = request.getHeader("Referer");

            // if a referrer is mandatory and we don't have one, we've got a problem!
            if (requireReferrer && StringUtils.isBlank(referrer)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                l.warning("Referrer required, but not provided.");
                return false;
            }

            // If we DO have a referrer, check it
            if (!StringUtils.isBlank(referrer)) {
                // where are we now
                String current = new StringBuffer(request.getScheme()).append("://").
                        append(request.getServerName()).toString();
                // If the referrer does not start with the current URL, we need to check if the referrer
                // is on our approved list.
                if (!referrer.startsWith(current)) {
                    // check if we have any approved referrers
                    String allowed = Authenticator.getEnvString(IOasisAction.KEY_ENVALLOWEDREFERRERS, "");
                    boolean foundReferrer = false;
                    if (!StringUtils.isBlank(allowed)) {
                        StringTokenizer tok = new StringTokenizer(allowed, ",");
                        while (tok.hasMoreTokens()) {
                            String allow = tok.nextToken();
                            // If it ends with a * (wildcard), then do a wildcard match
                            if (allow.endsWith("*") && referrer.startsWith(allow.substring(0, allow.length() - 1))) {
                                foundReferrer = true;
                                break;
                            } else {  // if we have a direct match, this is ok too.
                                if (referrer.equals(allow)) {
                                    foundReferrer = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!foundReferrer) {
                        String msg = (requireReferrer) ? "Referrer Required." : "Referrer Optional/Validated.";
                        l.warning(msg + " Referrer=" + referrer + ".  Not on approved list.");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return false;
                    }
                }
            }
        }
        l.exiting(clsName, "validateRequest", new Boolean(true));
        return true;
    }
    private static final Logger l = LogUtils.getLogger(Authenticator.class);
}
