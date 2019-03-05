package dti.ci.mergehistory.impl;

import dti.ci.mergehistory.EntityMergeHistoryManager;
import dti.ci.mergehistory.dao.EntityMergeHistoryDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object for Merge History
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:  10/09/15
 *
 * @author
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 10/09/2015       ylu         Issue 164517
 * ---------------------------------------------------
 */
public class EntityMergeHistoryImpl implements EntityMergeHistoryManager {


    /**
     * load entity merge history
     * @param inputRecord
     * @return
     */
    @Override
    public RecordSet loadMergeHistory(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadMergeHistory", new Object[]{});
        }

        RecordSet rs = getEntityMergeHistoryDAO().loadMergeHistory(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadMergeHistory", rs);
        }

        return rs;
    }

    /**
     * un-merge history record
     * @param inputRecord
     * @return
     */
    @Override
    public String unMergeProcess(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "unMergeProcess", new Object[]{inputRecord});
        }

        /* call DAO to merge entities */
        String rslt = getEntityMergeHistoryDAO().unMergeProcess(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "unMergeProcess", rslt);
        }
        return rslt;
    }



    public EntityMergeHistoryDAO getEntityMergeHistoryDAO() {
        return entityMergeHistoryDAO;
    }

    public void setEntityMergeHistoryDAO(EntityMergeHistoryDAO entityMergeHistoryDAO) {
        this.entityMergeHistoryDAO = entityMergeHistoryDAO;
    }

    private EntityMergeHistoryDAO entityMergeHistoryDAO;
}
