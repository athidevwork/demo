package dti.oasis.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Container for Web Query info.
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Apr 4, 2005
 *
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class WebQueryInfo implements Serializable {

    /**
     * QueryParm class
     */
    public class QueryParm implements Serializable {
        /**
         * Getter of SQL Type of parameter
         *
         * @return web_query_parm.parm_type
         */
        public int getParmType() {
            return parmType;
        }

        /**
         * Getter of parm name
         *
         * @return web_Query_parm.parm_name
         */
        public String getName() {
            return name;
        }

        /**
         * Getter of parm description
         *
         * @return web_query_parm.parm_description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Getter of userEnterable
         * @return web_query_parm.user_enterable_b
         */
        public boolean isUserEnterable() {
            return userEnterable;
        }

        /**
         * SQL Type of parameter
         */
        private int parmType;
        /**
         * Parameter name, always upper case
         */
        private String name;
        /**
         * Description of parameter - can be used as label
         */
        private String description;

        /**
         * Indicates if a user may enter this value
         */
        private boolean userEnterable;
        /**
         * Constructor
         *
         * @param parmType web_query_parm.parm_type - SQL Type
         * @param name     web_query_parm.parm_name
         * @param description web_query_parm.parm_description
         * @param userEnterable web_query_parm.user_enterable_b
         */
        private QueryParm(int parmType, String name, String description, boolean userEnterable) {
            this.parmType = parmType;
            this.name = name;
            this.description = description;
            this.userEnterable = userEnterable;
        }

        /**
         * toString
         *
         * @return String representation of object
         */
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("dti.oasis.util.WebQueryInfo.QueryParm");
            buf.append("{parmType=").append(parmType);
            buf.append(",name=").append(name);
            buf.append(",description=").append(description);
            buf.append(",userEnterable=").append(userEnterable);
            buf.append('}');
            return buf.toString();
        }
    }

    /**
     * The SQL for this query
     */
    private String sql;

    /**
     * An ArrayList of QueryParm objects
     */
    private ArrayList parms = new ArrayList();

    /**
     * The primary key web_query.web_query_pk
     */
    private long queryPk;

    /**
     * Getter of query primary key.
     *
     * @return web_query.web_query_pk
     */
    public long getQueryPk() {
        return queryPk;
    }

    /**
     * Constructor with query primary key.
     *
     * @param queryPk web_query.web_query_pk
     */
    public WebQueryInfo(long queryPk) {
        this.queryPk = queryPk;
    }

    /**
     * Setter of query primary key
     *
     * @param queryPk web_query.web_query_pk
     */
    public void setQueryPk(long queryPk) {
        this.queryPk = queryPk;
    }

    /**
     * Noarg constructor
     */
    public WebQueryInfo() {

    }

    /**
     * Getter of query SQL
     *
     * @return SQL web_query.query_text
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setter of query SQL
     *
     * @param sql web_query.query_text
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Getter of list of WebQueryInfo.QueryParm objects.
     *
     * @return ArrayList of WebQueryInfo.QueryParm objects
     */
    public ArrayList getParms() {
        return parms;
    }

    /**
     * Add a parameter to the end of the list of parameters.  This constructs
     * a WebQueryInfo.QueryParm objects and adds it to the parms ArrayList. This
     * will use the name as the description.  The parm also will not be user enterable.
     *
     * @param parmType SQL Type web_query_parm.parm_type
     * @param name     Name of parm web_query_parm.parm_name
     */
    public void addParm(int parmType, String name) {
        parms.add(new QueryParm(parmType, name.toUpperCase(), name, false));
    }

    /**
     * Add a parameter to the end of the list of parameters.  This constructs
     * a WebQueryInfo.QueryParm objects and adds it to the parms ArrayList.
     *
     * @param parmType    SQL Type web_query_parm.parm_type
     * @param name        Name of parm web_query_parm.parm_name
     * @param description Description of parm web_query_parm.parm_description
     */
    public void addParm(int parmType, String name, String description, boolean userEnterable) {
        parms.add(new QueryParm(parmType, name.toUpperCase(), description, userEnterable));
    }

    /**
     * toString
     *
     * @return String representation of object.
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.WebQueryInfo");
        buf.append("{sql=").append(sql);
        buf.append(",parms=").append(parms);
        buf.append(",queryPk=").append(queryPk);
        buf.append('}');
        return buf.toString();
    }

}
