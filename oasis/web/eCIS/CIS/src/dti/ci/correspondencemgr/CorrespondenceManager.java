package dti.ci.correspondencemgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/10/2018
 *
 * @author dzhang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 04/11/2018       dzhang      Issue 109204: correspondence refactor
 * ---------------------------------------------------
 */
public interface CorrespondenceManager {

    /**
     * Load correspondence list
     * @param inputRecord
     * @return
     */
    public RecordSet loadCorrespondenceList(Record inputRecord);
}
