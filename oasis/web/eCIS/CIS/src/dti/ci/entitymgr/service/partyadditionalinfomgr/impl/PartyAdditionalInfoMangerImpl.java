package dti.ci.entitymgr.service.partyadditionalinfomgr.impl;

import dti.ci.entitymgr.service.partyadditionalinfomgr.PartyAdditionalInfoManger;
import dti.ci.entitymgr.service.partyadditionalinfomgr.dao.PartyAdditionalInfoDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   5/3/2017
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class PartyAdditionalInfoMangerImpl implements PartyAdditionalInfoManger {
    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record loadPersonAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadPersonAdditionalInfo", new Object[]{inputRecord});
        }

        Record record = getPartyAdditionalInfoDAO().loadPersonAdditionalInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadPersonAdditionalInfo", record);
        }
        return record;
    }

    @Override
    public Record loadOrganizationAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadOrganizationAdditionalInfo", new Object[]{inputRecord});
        }

        Record record = getPartyAdditionalInfoDAO().loadOrganizationAdditionalInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadOrganizationAdditionalInfo", record);
        }
        return record;
    }

    @Override
    public Record loadAddressAdditionalInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressAdditionalInfo", new Object[]{inputRecord});
        }

        Record record = getPartyAdditionalInfoDAO().loadAddressAdditionalInfo(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressAdditionalInfo", record);
        }
        return record;
    }

    @Override
    public void savePersonAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "savePersonAdditionalInfo", new Object[]{record});
        }

        getPartyAdditionalInfoDAO().savePersonAdditionalInfo(record);

        l.exiting(getClass().getName(), "savePersonAdditionalInfo");
    }

    @Override
    public void saveOrganizationAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveOrganizationAdditionalInfo", new Object[]{record});
        }

        getPartyAdditionalInfoDAO().saveOrganizationAdditionalInfo(record);

        l.exiting(getClass().getName(), "saveOrganizationAdditionalInfo");
    }

    @Override
    public void saveAddressAdditionalInfo(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressAdditionalInfo", new Object[]{record});
        }

        getPartyAdditionalInfoDAO().saveAddressAdditionalInfo(record);

        l.exiting(getClass().getName(), "saveAddressAdditionalInfo");
    }

    @Override
    public void saveAdditionalXmlData(Record record) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAdditionalXmlData", new Object[]{record});
        }

        getPartyAdditionalInfoDAO().saveAdditionalXmlData(record);

        l.exiting(getClass().getName(), "saveAdditionalXmlData");
    }

    public void verifyConfig() {
        if (getPartyAdditionalInfoDAO() == null)
            throw new ConfigurationException("The required property 'partyAdditionalInfoDAO' is missing.");
    }

    public PartyAdditionalInfoDAO getPartyAdditionalInfoDAO() {
        return m_partyAdditionalInfoDAO;
    }

    public void setPartyAdditionalInfoDAO(PartyAdditionalInfoDAO partyAdditionalInfoDAO) {
        m_partyAdditionalInfoDAO = partyAdditionalInfoDAO;
    }

    private PartyAdditionalInfoDAO m_partyAdditionalInfoDAO;
}
