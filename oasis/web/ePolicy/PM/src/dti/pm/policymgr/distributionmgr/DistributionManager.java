package dti.pm.policymgr.distributionmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Process Distribution.
 * <p/>
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 10, 2011
 *
 * @author wfu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 11/14/2013       xnie        142674 1) Added a static variable ADD_DISTRIBUTION_ACTION_CLASS_NAME.
 *                                     2) Added processCatchUp() to catch up dividend.
 * ---------------------------------------------------
 */

public interface DistributionManager {

    public static final String ADD_DISTRIBUTION_ACTION_CLASS_NAME = "dti.pm.policymgr.distributionmgr.struts.ProcessDistributionAction";

    /**
     * Returns a RecordSet loaded with list of distributions
     *
     * @param inputRecord search criteria
     * @return RecordSet a RecordSet loaded with list of available distributions.
     */
    public RecordSet loadAllDistribution(Record inputRecord);

    /**
     * Save distribution info
     *
     * @param inputRecords distribution info
     * @return
     */
    public void saveAllDistribution(RecordSet inputRecords);

    /**
     * Get the initial values to add new distribution
     *
     * @return Record
     */
    public Record getInitialValuesForAddDistribution();

    /**
     * Process distribution for selected row
     *
     * @param inputRecord
     * @return
     */
    public void processDistribution(Record inputRecord);

    /**
     * Catch up dividend when new rule is declared in new calendar year.
     *
     * @param inputRecord
     * @return
     */
    public void processCatchUp(Record inputRecord);
}
