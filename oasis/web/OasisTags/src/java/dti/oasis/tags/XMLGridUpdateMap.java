package dti.oasis.tags;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Holds a map between updateable columns in an XML data island
 * and a DisconnectedResultset
 *
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 *
 *      Date:   Oct 11, 2004
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
*
* ---------------------------------------------------
*/
public class XMLGridUpdateMap implements Serializable{
    private ArrayList drsCols = new ArrayList();
    private int drsId;

	/**
     * @return # of columns
     */
    public int getColumnCount() {
        return drsCols.size();
    }

	/**
     * @return 1 based column
     */    
    public int getColumn(int xmlCol) {
        return Integer.parseInt((String) drsCols.get(xmlCol));
    }

	/**
	 * @param drsCol 1 based column
	 */
    public void addColumn(int drsCol) {
        drsCols.add(String.valueOf(drsCol));
    }

	/**
     * @return 1 based id column
     */
    public int getIdColumn() {
        return drsId;
    }

	/**
	 * @param drsId 1 based id column
	 */
    public void setIdColumn(int drsId) {
        this.drsId = drsId;
    }

    public XMLGridUpdateMap() {
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("XMLGridUpdateMap");
        buf.append("{drsCols=").append(drsCols);
        buf.append(",drsId=").append(drsId);
        buf.append('}');
        return buf.toString();
    }

}
