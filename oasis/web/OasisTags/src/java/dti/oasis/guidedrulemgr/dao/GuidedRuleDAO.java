package dti.oasis.guidedrulemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;

/**
 * DAO class for guided rule
 *
 * <p>(C) 2011 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2011
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public interface GuidedRuleDAO {

    /**
     * Load all page fields
     * @param inputRecord
     * @return
     */
    public abstract RecordSet loadAllPageFields(Record inputRecord);

    /**
     * Load all page navigation items
     * @param inputRecord
     * @return
     */
    public abstract RecordSet loadAllPageNavigationItem(Record inputRecord);
    
    /**
     * load page
     *
     * @param pageId
     */
    public abstract Record loadPage(String pageId);

}