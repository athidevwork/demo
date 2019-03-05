package dti.ci.entityhistoricaldatamgr.dao;


import dti.ci.entityhistoricaldatamgr.EntityHistoricalDataFields;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.ci.core.dao.BaseDAO;
import dti.oasis.app.AppException;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the EntityHistoricalDataDAO interface.
 * This is consumed by any business logic objects that requires information about Discount Points Hist.
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * User: Michael
 * Date: August 08, 2010
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
*
 * ---------------------------------------------------
 */
public class EntityHistoricalDataJdbcDAO extends BaseDAO implements EntityHistoricalDataDAO {

    /**
     * Get all Discount Points Hist from DB
     *
     * @param record
     * @param recordLoadProcessor
     * @return
     */
    public RecordSet loadAllAvailableEntityHistoricalDatas(Record record, RecordLoadProcessor recordLoadProcessor) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", new Object[]{record, recordLoadProcessor});
        }

        RecordSet rs;
        try {
            DataRecordMapping mapping = new DataRecordMapping();
            mapping.addFieldMapping(new DataRecordFieldMapping("filterStartPolicyTermEff", "filterStartPolicyTermEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("filterEndPolicyTermEffec", "filterEndPolicyTermEffectiveDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("filterStartPolicyTermExp", "filterStartPolicyTermExpirationDate"));
            mapping.addFieldMapping(new DataRecordFieldMapping("filterEndPolicyTermExpir", "filterEndPolicyTermExpirationDate"));
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Historical_Data.Sel_Entity_Historical_Data",mapping);
            rs = spDao.execute(record, recordLoadProcessor);
        } catch (SQLException e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Unable to load Entity Historical information", e);
            l.throwing(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", ae);
            throw ae;
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAllAvailableEntityHistoricalDatas", rs);
        }
        return rs;
    }


}
