package dti.ci.claimcodehistory.impl;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ConfigurationException;
import dti.ci.claimcodehistory.ClaimCodeHistoryManager;
import dti.ci.claimcodehistory.dao.ClaimCodeHistoryDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.app.ConfigurationException;


/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 5, 2009
 *
 * @author msnadar
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ClaimCodeHistoryManagerImpl implements ClaimCodeHistoryManager {
    /**
     * Get Claim Code History .
     *
     * @param inputRecord Source Table and FK
     * @return TThe Claim Code History
     */
    public RecordSet getClaimCodeHistory(Record inputRecord) {
        return getClaimCodeHistoryDao().getClaimCodeHistory(inputRecord);
    }

    public void verifyConfig() {
        if (getClaimCodeHistoryDao() == null) {
            throw new ConfigurationException("The required property 'claimCodeHistoryDao' is missing.");
        }
    }

//    public ClaimCodeHistoryManagerImpl() {
//    }

    public ClaimCodeHistoryDAO getClaimCodeHistoryDao() {
        return claimCodeHistoryDao;
    }

    public void setClaimCodeHistoryDao(ClaimCodeHistoryDAO claimCodeHistoryDAO) {
        this.claimCodeHistoryDao = claimCodeHistoryDAO;
    }

    private ClaimCodeHistoryDAO claimCodeHistoryDao;
}

