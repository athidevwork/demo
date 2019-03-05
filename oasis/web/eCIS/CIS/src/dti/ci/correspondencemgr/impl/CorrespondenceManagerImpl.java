package dti.ci.correspondencemgr.impl;

import dti.ci.correspondencemgr.CorrespondenceManager;
import dti.ci.correspondencemgr.dao.CorrespondenceDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

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
public class CorrespondenceManagerImpl implements CorrespondenceManager {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadCorrespondenceList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCorrespondenceList", new Object[]{inputRecord});
        }

        RecordSet correspondenceList = getCorrespondenceDAO().loadCorrespondenceList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadCorrespondenceList", correspondenceList);
        }
        return correspondenceList;
    }

    public void verifyConfig() {
        if (getCorrespondenceDAO() == null) {
            throw new ConfigurationException("The required property 'correspondenceDAO' is missing");
        }
    }

    public CorrespondenceDAO getCorrespondenceDAO() {
        return m_correspondenceDAO;
    }

    public void setCorrespondenceDAO(CorrespondenceDAO correspondenceDAO) {
        this.m_correspondenceDAO = correspondenceDAO;
    }

    private CorrespondenceDAO m_correspondenceDAO;
}
