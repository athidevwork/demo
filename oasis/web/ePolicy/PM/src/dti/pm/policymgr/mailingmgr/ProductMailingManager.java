package dti.pm.policymgr.mailingmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Interface to handle Implementation of Policy Mailing.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Sep 25, 2013
 *
 * @author awu
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public interface ProductMailingManager {

    /**
     * Return a record set with a list of product mailing data.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadProductMailing(Record inputRecord);

    /**
     * This method used to save the product mailing data.
     *
     * @param inputRecordSet
     */
    public void saveProductMailing(RecordSet inputRecordSet);

    /**
     * This method used to set initial values for adding product mailing.
     *
     * @return
     */
    public Record getInitialValuesForProductMailing();

}
