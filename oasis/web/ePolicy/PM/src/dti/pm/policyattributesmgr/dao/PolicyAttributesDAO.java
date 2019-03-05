package dti.pm.policyattributesmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * An interface that provides DAO operation for Policy Attributes.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  July 1, 2016
 *
 * @author wdang
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/01/16 wdang   167534 - Initial version.
 * ---------------------------------------------------
 */
public interface PolicyAttributesDAO {

    /**
     * Load PmAttribute by type code.
     * @param inputRecord contains type code to specify a set of PmAttribute.
     * @return RecordSet of PmAttribute.
     */
    public RecordSet loadPmAttribute(Record inputRecord);
}
