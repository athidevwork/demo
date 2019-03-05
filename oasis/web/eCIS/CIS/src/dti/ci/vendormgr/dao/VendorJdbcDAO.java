package dti.ci.vendormgr.dao;

import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2018 Delphi Technology, inc. (dti)</p>
 * Date:   4/3/2018
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class VendorJdbcDAO extends BaseDAO implements VendorDAO {

    /**
     * Get the Vendor data of an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadVendor(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendor", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vendor.Load_Vendor");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadVendor", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get vendor data.", e);
            l.throwing(getClass().getName(), "loadVendor", ae);
            throw ae;
        }
    }

    /**
     * Get the Vendor Address data of an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadVendorAddress(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddress", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vendor.Load_Vendor_Address");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadVendorAddress", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get vendor address data.", e);
            l.throwing(getClass().getName(), "loadVendorAddress", ae);
            throw ae;
        }
    }


    /**
     * Get the Vendor Payment data of an entity.
     *
     * @param inputRecord
     * @return RecordSet
     */
    @Override
    public RecordSet loadVendorPayment(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorPayment", new Object[]{inputRecord});
        }

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Vendor.Load_Vendor_Payment");

        try {
            RecordSet rs = spDao.execute(inputRecord);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), "loadVendorPayment", rs);
            }
            return rs;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get vendor payment data.", e);
            l.throwing(getClass().getName(), "loadVendorPayment", ae);
            throw ae;
        }
    }

    /**
     * save vendor data
     *
     * @param inputRecord
     * @return Record
     */
    public Record saveVendor(Record inputRecord) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveVendor", inputRecord);
        String methodName = "AddEntity";
        Record outputRecord = new Record();
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("entityId", "pk"));
            //mapping.addFieldMapping(new DataRecordFieldMapping("vendorId", "vendorPK"));
            StoredProcedureDAO spDao =  StoredProcedureDAO.getInstance("ci_web_vendor.save_vendor", mapping);
            outputRecord = spDao.executeUpdate(inputRecord);
        }
        catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute " + methodName, e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
        l.exiting(getClass().toString(), methodName, outputRecord);
        return outputRecord;
    }
}
