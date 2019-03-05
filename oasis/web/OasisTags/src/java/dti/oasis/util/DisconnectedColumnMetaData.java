package dti.oasis.util;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * MetaData for a disconnected JDBC column.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @see dti.oasis.util.DisconnectedResultSet
 *
 * @author jbe
 * Date:   Jul 3, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  2/6/2004        jbe     Added Logging
 *
 * ---------------------------------------------------
 */
public class DisconnectedColumnMetaData implements java.io.Serializable{
	private String columnLabel;
	private String columnName;
	private int columnType;
	private int precision;
	private int scale;


    public DisconnectedColumnMetaData(ResultSetMetaData rsmd, int col) throws SQLException{
        Logger l = LogUtils.enterLog(getClass(), "constructor", new Object[] {rsmd, new Integer(col)});
	    columnLabel = rsmd.getColumnLabel(col);
		columnName = rsmd.getColumnName(col);
		columnType = rsmd.getColumnType(col);
		precision = rsmd.getPrecision(col);
		scale = rsmd.getScale(col);
        l.exiting(getClass().getName(),"constructor", this);
	}
	
	
	/**
	 * @return
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @return
	 */
	public int getColumnType() {
		return columnType;
	}

	/**
	 * @return
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @return
	 */
	public int getScale() {
		return scale;
	}

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.util.DisconnectedColumnMetaData");
        buf.append("{columnLabel=").append(columnLabel);
        buf.append(",columnName=").append(columnName);
        buf.append(",columnType=").append(columnType);
        buf.append(",precision=").append(precision);
        buf.append(",scale=").append(scale);
        buf.append('}');
        return buf.toString();
    }


}
