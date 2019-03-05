package dti.oasis.util;

import java.io.Serializable;

/**
 * A Query Parameter
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 14, 2004 
 * @author jbe
 */
/* 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
*/

public class QueryParm implements Serializable {
    public int sqlType;
    public Object value;

    public QueryParm() {
        
    }

    /**
     * Constructor with float
     * @param sqlType
     * @param value
     */
    public QueryParm(int sqlType, float value) {
        this.sqlType = sqlType;
        this.value = new Float(value);
    }
    /**
     * Constructor with double
     * @param sqlType
     * @param value
     */
    public QueryParm(int sqlType, double value) {
        this.sqlType = sqlType;
        this.value = new Double(value);
    }
    /**
     * Constructor with int
     * @param sqlType
     * @param value
     */
    public QueryParm(int sqlType, int value) {
        this.sqlType = sqlType;
        this.value = new Integer(value);
    }
    /**
     * Constructor with long
     * @param sqlType
     * @param value
     */
    public QueryParm(int sqlType, long value) {
        this.sqlType = sqlType;
        this.value = new Long(value);
    }
    /**
     * Constructor with any type
     * @param sqlType
     * @param value
     */
    public QueryParm(int sqlType, Object value) {
        this.sqlType = sqlType;
        this.value = value;
    }
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("QueryParm");
        buf.append("{sqlType=").append(sqlType);
        buf.append(",value=").append(value);
        buf.append('}');
        return buf.toString();
    }
}
