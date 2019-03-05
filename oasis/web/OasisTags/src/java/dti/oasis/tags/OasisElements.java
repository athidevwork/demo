package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.util.DatabaseUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A collection of OasisWebElement objects in the
 * form of a LinkedHashMap.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Aug 29, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 2/7/2004      jbe     Add Logging
* 11/11/2004    jbe     Use PreparedStatements
* 9/20/2005     jbe     Include INACTIVE elements, but
*                       set status in OasisWebElement.
* 09/25/2008    Larry   Issue 86826 DB connection leakage change
* 10/28/2011    mlm     126732 - Refactored to treat NULL web application security value as "Y".
* ---------------------------------------------------
*/

public class OasisElements extends LinkedHashMap implements Serializable {

    /**
     * Factory that will construct an object of type OasisElements
     */
    private static IOasisElementsFactory factory;

    /**
     * Query to get list of elements for page/user
     */
    private static final String ElementsQuery =
            "select distinct wpe.type, wpe.code, wpe.text, " +
            "wpe.url, wpe.style, wpe.long_description, DECODE(wa.security_b,'N','N', NVL(wpe.security_b, 'N')) security_b, " +
            "cs_get_auth_web_element(wpe.pf_web_page_element_pk,?) auth, nvl(wpe.sequence,999), nvl(wpe.status,'I') " +
            "from pf_web_page wp, pf_web_page_element wpe,pf_web_application wa " +
            "where wp.struts_action = ? and wa.pf_web_application_pk= wp.pf_web_application_fk and wp.pf_web_page_pk = wpe.pf_web_page_fk " +
            "order by nvl(wpe.sequence,999)";

    /**
     * Prevent direct instantiation
     */
    protected OasisElements() {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisElements(int capacity) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisElements(int capacity, float loadFactor) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisElements(int capacity, float loadFactor, boolean accessOrder) {
    }

    /**
     * Prevent direct instantiation
     */
    protected OasisElements(Map m) {
    }

    /**
     * Create a new instance, using the default constructor unless
     * an IOasisFieldsFactory has been defined
     *
     * @return OasisFields object
     */
    public static OasisElements newInstance() {
        if (factory == null)
            return new OasisElements();
        else
            return factory.newInstance();
    }

    /**
     * Getter for factory
     *
     * @return an object that implements IOasisElementsFactory
     */
    public static IOasisElementsFactory getFactory() {
        return factory;
    }

    /**
     * Setter for factory
     *
     * @param factory
     */
    public static void setFactory(IOasisElementsFactory factory) {
        OasisElements.factory = factory;
    }

    /**
     * Process all the elements for a page, user
     *
     * @param els valid OasisElements object
     * @param rs  JDBC ResultSet with Element data
     * @throws SQLException
     */
    protected static void processElements(OasisElements els, ResultSet rs, PageDefLoadProcessor pageDefLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(OasisElements.class, "processElements",
                new Object[]{els, rs});
        try {
            // cycle through rows, adding OasisWebElement to OasisElements
            while (rs.next()) {
                String auth = rs.getString(8);
                String secure = rs.getString(7);
                String el = rs.getString(2);
                //System.out.println("Adding " +el + " secure="+secure+" auth="+auth);
                boolean isAvailable =
                        ((secure == null || !secure.equals("Y")) ||
                        (auth != null && auth.equals("RW")));
                OasisWebElement owe = new OasisWebElement(el, rs.getString(1), isAvailable, rs.getString(3),
                                            rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(10)) ;
                pageDefLoadProcessor.postProcessWebElement(owe);
                els.put(el, owe);
            }
            pageDefLoadProcessor.postProcessWebElements(els);
            l.exiting(OasisElements.class.getName(), "processElements");
        }
        finally {
                if (rs != null) DatabaseUtils.close(rs);              
        }

    }

    /**
     * Creates an instance of OasisElements
     *
     * @param className maps to PF_WEB_PAGE.struts_action
     * @param userId    maps to PFUSER.userid
     * @param conn      A live JDBC Connection
     * @return OasisElements object
     * @throws java.sql.SQLException
     */
    public static OasisElements createInstance(String className, String userId, Connection conn, PageDefLoadProcessor pageDefLoadProcessor) throws SQLException {
        Logger l = LogUtils.enterLog(OasisElements.class, "createInstance",
                new Object[]{className, userId, conn});
        //Page elements are not supported from 2011.1.0. All Page Elements must be migrated into navigation util table."
        if (true) {
            return null;
        }
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(ElementsQuery);
            stmt.setString(1,userId);
            stmt.setString(2,className);
            OasisElements els = newInstance();
            l.fine(new StringBuffer("Executing: ").append(ElementsQuery).append(" with ").
                    append(userId).append(',').append(className).toString());
            processElements(els, stmt.executeQuery(), pageDefLoadProcessor);
            // If there is nothing in the OasisElements object, set it to null
            if (els.size() == 0)
                els = null;
            l.exiting(OasisElements.class.getName(), "createInstance", els);
            return els;
        }
        finally {
                if (stmt != null) DatabaseUtils.close(stmt);             
        }
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(super.toString()).append(" ::: ");
        buf.append("dti.oasis.tags.OasisElements");
        buf.append("{}");
        return buf.toString();
    }

}
