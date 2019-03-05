package dti.oasis.recordset;

import dti.oasis.util.LoadProcessor;
import org.w3c.dom.Node;

/**
 * This Interface extends the LoadProcessor interface to define the methods used while loading a XML Record Set.
 * <p/>
 * <p>Implement this Interface to define some business logic that will get applied while loading a XML Record Set.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 18, 2006
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
public interface XMLRecordLoadProcessor extends LoadProcessor {
    /**
     * Process the given XML Record after it's been loaded.
     *
     * @param record             the current XML Record
     * @return true if this XML Record should be added to the XML Record Set;
     *         false if this XML Record should be excluded from the XML Record Set.
     */
    public boolean postProcessRecord(Node record);

    /**
     * Process the XML Record Set after all XML Records have been loaded and processed.
     *
     * @param recordSet the XML Record Set.
     */
    public void postProcessRecordSet(Node recordSet);
}
