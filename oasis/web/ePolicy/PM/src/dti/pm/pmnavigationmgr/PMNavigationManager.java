package dti.pm.pmnavigationmgr;

import dti.pm.policymgr.PolicyHeader;
import dti.oasis.recordset.Record;
import java.util.ArrayList;

/**
 * An interface to handle implementation of PM navigation manager.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 22, 2007
 *
 * @author sxm
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 09/29/2008       sxm         Issue 86880 - append risk type to risk name, append "Selected" to current risk name,
 *                                            and set current value of selected record
 * 05/20/2009       yhyang      Issue 93385 - Keep the selected value for navigate section when page is re-loaded.
 * ---------------------------------------------------
 */
public interface PMNavigationManager {
    /**
     * Load navigate options for Coverage
     *
     * @param policyHeader Instance of the policy header
     * @param inputRecord Record containing policy/risk/term information
     * @param lovOptions ArrayList containing returned navigation options
     * @return String  Value of currently selected record
     */
    String loadNavigateSourceForCoverage(PolicyHeader policyHeader, Record inputRecord, ArrayList lovOptions);

    /**
     * Load navigate options for Coverage Class
     *
     * @param policyHeader Instance of the policy header
     * @param inputRecord Record containing policy/risk/term information
     * @param lovOptions ArrayList containing returned navigation options
     * @return String  Value of currently selected record
     */
    String loadNavigateSourceForCoverageClass(PolicyHeader policyHeader, Record inputRecord, ArrayList lovOptions);

    /**
     * Get policy navigation parameters "policyNavLevelCode" and "policyNavSourceId"
     *
     * @param inputRecord
     * @return Record
     */
    public Record getPolicyNavParameters(Record inputRecord);
}
