package dti.oasis.guidedrulemgr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class implementation for guided rule
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
public class GuidedRuleJdbcDAO implements GuidedRuleDAO {

    /**
     * Load all page fields
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPageFields(Record inputRecord) {
        String methodName = "loadAllPageFields";
        l.entering(getClass().getName(), "loadAllPageFields");
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CWB_Guided_Rule_Config.Select_Page_Fields");
            RecordSet recordSet = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            spDao = StoredProcedureDAO.getInstance("CWB_Guided_Rule_Config.Select_Page_Header_Fields");
            RecordSet headerSet = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            recordSet.addRecords(headerSet);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load page fields information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }

    /**
     * Load all page navigation item
     * @param inputRecord
     * @return
     */
    public RecordSet loadAllPageNavigationItem(Record inputRecord) {
        String methodName = "loadAllPageNavigationItem";
        l.entering(getClass().getName(), "loadAllPageNavigationItem");
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CWB_Guided_Rule_Config.Select_Page_Navigation_Item");
            RecordSet recordSet = spDao.execute(inputRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load page navigation item information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }


    /**
     * load page
     *
     * @param pageId
     */
    public Record loadPage(String pageId) {
        String methodName = "loadPage";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{pageId});
        }
        try {
            Record record = null;
            Record paramRecord = new Record();
            paramRecord.setFieldValue("pageId", pageId);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("CWB_Page_Rule_Config.Select_Page");
            RecordSet recordSet = spDao.execute(paramRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            if (recordSet.getSize() > 0) {
                record = recordSet.getRecord(0);
            }
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, record);
            }
            return record;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load page information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }
    private final Logger l = LogUtils.getLogger(getClass());
}