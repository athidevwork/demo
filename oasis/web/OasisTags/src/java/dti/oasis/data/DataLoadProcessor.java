package dti.oasis.data;

import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LoadProcessor;

/**
 * This Interface extends the LoadProcessor interface to define the methods use by the DisconnectedResultSet
 * to initialize itself with DataRow objects while iterating through a JDBC ResultSet.
 * <p/>
 * <p>Implement this Interface to define some business logic that will get applied while loading a DisconnectedResultSet.
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
public interface DataLoadProcessor extends LoadProcessor {
    /**
     * Post-process a particular DisconnectedResultSet.DataRow.
     *
     * @param dataRow a newly created DisconnectedResultSet.DataRow
     * @return true if the DisconnectedResultSet.DataRow should be added; otherwise, false.
     */
    public boolean postProcessDataRow(DisconnectedResultSet.DataRow dataRow);

    /**
     * Post-process a particular DisconnectedResultSet.
     *
     * @param rs the DisconnectedResultSet.
     */
    public void postProcessDisconnectedResultSet(DisconnectedResultSet rs);
}