package dti.ci.wipinquirymgr.impl;

import dti.ci.wipinquirymgr.WIPInquiryManager;
import dti.ci.wipinquirymgr.dao.WIPInquiryDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle WIP Inquiry.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 12, 2005
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  02/13/2015       bzhu        Issue 160886. Add AccessControlFilterManager.
 *  04/17/2018       dpang       Issue 192648. Refactor WIP Inquiry.
 * ---------------------------------------------------
 */

public class WIPInquiryManagerImpl implements WIPInquiryManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadWIPInquiry(String entityId) {
        String methodName = "loadWIPInquiry";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{entityId});
        }

        Record inputRecord = new Record();
        inputRecord.setFieldValue("entityId", entityId);
        RecordSet rs = getWipInquiryDAO().loadWIPInquiry(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    public void verifyConfig() {
        if (getWipInquiryDAO() == null) {
            throw new ConfigurationException("The required property 'wipInquiryDAO' is missing.");
        }
    }

    public WIPInquiryDAO getWipInquiryDAO() {
        return m_wipInquiryDAO;
    }

    public void setWipInquiryDAO(WIPInquiryDAO wipInquiryDAO) {
        this.m_wipInquiryDAO = wipInquiryDAO;
    }

    private WIPInquiryDAO m_wipInquiryDAO;
}
