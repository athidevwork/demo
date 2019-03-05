package dti.oasis.data;

import dti.oasis.util.DisconnectedResultSet;

/**
 * This class provides a default implementation of the DataLoadProcessor interface that does nothing.
 * It is used by the DisconnectedResultSet if no DataLoadProcessor is provided.
 * Other classes may extend this class as a convenience if not all methods will be implemented.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2006
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
public class DefaultDataLoadProcessor implements DataLoadProcessor {
    /**
     * Do nothing but return true.
     */
    public boolean postProcessDataRow(DisconnectedResultSet.DataRow dataRow) {
        return true;
    }

    /**
     * Do nothing.
     */
    public void postProcessDisconnectedResultSet(DisconnectedResultSet rs) {
    }
}