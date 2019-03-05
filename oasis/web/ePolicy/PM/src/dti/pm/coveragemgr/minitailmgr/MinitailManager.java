package dti.pm.coveragemgr.minitailmgr;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * this class is an interface for mini tail manager
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2007
 *
 * @author zlzhu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/20/2007       zlzhu       Created
 * 11/25/2010       bhong       114074 - added additional parameter "inputRecord" in loadAllMinitailRiskCoverage
 *                              and loadAllMinitail methods.
 * 04/25/2012       xnie        132237 - 1) Removed validateAllMinitail.
 *                                       2) Passed parent grid to saveAllMinitail.
  * 11/05/2013      jyang2      158679 - Modified saveAllMinitail method, added parameter policyHeader.
 * ---------------------------------------------------
 */

public interface MinitailManager {
    /**
     * save all the mini tail data
     * <p/>
     * @param miniTails mini tail data that needed to save
     *        parentInputRecords mini tail parent data for validation
     */
    public void saveAllMinitail(RecordSet miniTails, RecordSet parentInputRecords, PolicyHeader policyHeader);

    /**
     * load all mini tail related risk coverage data
     * <p/>
     * @param inputRecord
     * @param policyHeader policy header
     * @return the result met the condition
     */
    public RecordSet loadAllMinitailRiskCoverage(Record inputRecord, PolicyHeader policyHeader);

    /**
     * load all the mini tail data
     * <p/>
     *
     * @param inputRecord
     * @param policyHeader policy header
     * @return the result met the condition
     */
    public RecordSet loadAllMinitail(Record inputRecord, PolicyHeader policyHeader);

    /**
     * There are two rules here(refer to [GDR55.4]),if only one of them is met,return true
     * <br>Rule 2:
     * if the mini tail effective date matches the current transaction effective date.
     * If this matches, the fields are editable
     * <br>Rule 3:
     * if the mini tail effective date is less than the current term effective date, this could be a mini tail generated
     * as a result of prior acts data.  The business requires the ability to edit these within the first term.  To
     * determine if this is due to a prior acts piece of data the following functions are called:
     * Function PM_Dates.NB_Risk_StartDt,which returns date1
     * Function  PM_GET_MIN_RISK_EFF_DATE ,which returns date2
     * If DATE1 and DATE2 are equal - row is not editable.If DATE2 < DATE1 and the mini tail effective date is between
     * DATE1 and DATE2, then the mini tail is caused by prior acts data.  In this case if DATE1 is between the current
     * term effective dates, then the mini tail is editable, otherwise readonly.
     * <p/>
     *
     * @param policyHeader
     * @param miniTails
     * @return return true if only one of them are met
     */
    public Record getMinitailEditable(PolicyHeader policyHeader, Record miniTails);

    /**
     * Load All free mini tail
     * @param policyHeader
     * @return RecordSet
     */
    public RecordSet loadAllFreeMiniTail(PolicyHeader policyHeader);

    /**
     * Check if free mini tail exist
     * @param policyHeader
     * @return boolean
     */
    public boolean isFreeMiniTailExist(PolicyHeader policyHeader);
}
