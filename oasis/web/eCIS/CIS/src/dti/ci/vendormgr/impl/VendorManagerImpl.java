package dti.ci.vendormgr.impl;

import dti.ci.educationmgr.dao.EducationDAO;
import dti.ci.helpers.CIHelper;
import dti.ci.helpers.ICIVendorConstants;
import dti.ci.trainingmgr.TrainingFields;
import dti.ci.vendormgr.VendorManager;
import dti.ci.vendormgr.dao.VendorDAO;
import dti.ci.helpers.data.DAOFactory;
import dti.ci.helpers.data.DAOInstantiationException;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.XMLUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for Vendor.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 15, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -----------------------------------------------------------------
 *         04/01/2005       HXY        Removed singleton implementation.
 *         04/14/2005       HXY        Added transaction commit logic.
 *         04/18/2005       HXY        Created one instance DAO.
 *         05/02/2005       HXY        Removed vendor address related fields
 *                                     from data map in transformMap method.
 *         04/28/2009       Fred       Removed method retrieveVendorPaymentInfo
 *         04/03/2018       JLD        Issue 109176. Refactor Vendor.
 *         -----------------------------------------------------------------
 */

public class VendorManagerImpl implements VendorManager {

    /**
     * Get Vendor data info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendor(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendor", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getVendorDAO().loadVendor(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendor", rs);
        }
        return rs;
    }

    /**
     * Get Vendor Address data info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendorAddress(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddress", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getVendorDAO().loadVendorAddress(inputRecord);


        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddress", rs);
        }
        return rs;
    }


    /**
     * Get Vendor Payment data info for an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    public RecordSet loadVendorPayment(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorPayment", new Object[]{inputRecord});
        }

        String entityId = inputRecord.getStringValue("entityId");
        if (!FormatUtils.isLong(entityId)) {
            throw new IllegalArgumentException("entity FK [" +
                    entityId +
                    "] should be a number.");
        }

        RecordSet rs = getVendorDAO().loadVendorPayment(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorPayment", rs);
        }
        return rs;
    }

    /**
     * Save Vendor data.
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveVendor(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVendor");
        }

        Record rd = getVendorDAO().saveVendor(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVendor", rd);
        }

        return rd;
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public VendorDAO getVendorDAO() {
        return m_vendorDAO;
    }

    public void setVendorDAO(VendorDAO vendorDAO) {
        this.m_vendorDAO = vendorDAO;
    }

    private WorkbenchConfiguration m_workbenchConfiguration;

    private VendorDAO m_vendorDAO;
}
