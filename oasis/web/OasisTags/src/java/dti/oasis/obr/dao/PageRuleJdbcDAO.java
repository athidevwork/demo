package dti.oasis.obr.dao;

import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements methods for page rule;.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jul 20, 2011
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
public class PageRuleJdbcDAO implements PageRuleDAO{


    /**
     * Load all Page rule
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllPageRule(Record record, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "loadAllPageRule";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{record, recordLoadProcessor});
        }
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Page_Rules");
            RecordSet recordSet = spDao.execute(record, recordLoadProcessor);

            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load page rules information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

    }

    /**
     * Load all rule imports
     *
     * @return
     */
    public RecordSet loadAllRuleImport() {
        String methodName = "loadAllRuleImport";
        l.entering(getClass().getName(), "loadAllRuleImport");
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Rule_Imports");
            RecordSet recordSet = spDao.execute(new Record(), DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load rule imports information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

    }

    /**
     * get last modified time
     *
     * @param pageCode
     * @return
     */
    public Date getLastModifiedTime(String pageCode) {
        String methodName = "getLastModifiedTime";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{pageCode});
        }
        try {
            Record paramRecord = new Record();
            paramRecord.setFieldValue("pageCode", pageCode);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Last_Modified");
            RecordSet recordSet = spDao.execute(paramRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            Date date = null;
            String maxModifiedTime = recordSet.getRecord(0).getStringValue("maxModifiedTime");
            if (!StringUtils.isBlank(maxModifiedTime)){
               date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(maxModifiedTime);
            }
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return date;
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to get last modified information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        } catch (ParseException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to convert date information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }
    }


    /**
     * Load all rule mapping
     *
     * @return
     */
    public RecordSet loadAllRuleMapping() {
        String methodName = "loadAllRuleMapping";
        l.entering(getClass().getName(), "loadAllRuleMapping");
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Rule_Mappings");
            RecordSet recordSet = spDao.execute(new Record(), DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            if (l.isLoggable(Level.FINER)) {
                l.exiting(getClass().getName(), methodName, recordSet);
            }
            return recordSet;

        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load rule mapping information", e);
            l.throwing(getClass().getName(), methodName, ae);
            throw ae;
        }

    }

    /**
     * Load all page fields
     *
     * @return
     */
    public RecordSet loadAllPageFields(String pageCode) {
        String methodName = "loadAllPageFields";
        l.entering(getClass().getName(), "loadAllPageFields");
        try {
            Record paramRecord = new Record();
            paramRecord.setFieldValue("pageCode", pageCode);
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Page_Fields");
            RecordSet recordSet = spDao.execute(paramRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
            spDao = StoredProcedureDAO.getInstance("OBR_Rule.Select_Page_Header_Fields");
            RecordSet headerSet = spDao.execute(paramRecord, DefaultRecordLoadProcessor.DEFAULT_INSTANCE);
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

    private final Logger l = LogUtils.getLogger(getClass());
}
