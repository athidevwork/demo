package dti.ci.riskmgr.survey.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.app.AppException;
import dti.ci.core.dao.BaseDAO;

import java.util.logging.Logger;
import java.sql.SQLException;


/**
 * The Oracle Implementation for Risk Management Survey Tracking's Data Access
 * <p>(C) 2009 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 10, 2009
 *
 * @author gjlong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
    
public class SurveyJdbcDAO extends BaseDAO implements SurveyDAO  {

    /**
     * load survey data for an entity represented by entityId in the record
     *
     * @param input: a record containing entityId
     * @return Survey Data for an given entity
     */
    public RecordSet loadAllSurvey(Record input) {
        Logger l = LogUtils.enterLog(this.getClass(), "loadAllSurvey");
        RecordSet rs = null;
        StoredProcedureDAO sp = StoredProcedureDAO.getInstance("Wb_Ci_Risk.load_all_survey");
        try {
            rs= sp.execute(input);
        } catch (SQLException se) {
            String error = this.getClass().getName()+ ".loadAllSurvey:"+se.getMessage();
            l.warning(error);
            throw new AppException(error);
        }

        l.exiting(this.getClass().getName(), "loadAllSurvey", rs);
        return rs;
    }

    /**
     * method to save all survey data
     *
     * @param rs: survey data represented as RecordSet
     * @return integer indicating the number of survey rows saved to RDBMS
     */
    public int saveAllSurvey(RecordSet rs) {
        Logger l = LogUtils.enterLog(this.getClass(), "saveAllSurvey");
        int dataSaved = 0;

        // the max length of roracle parameter is 30,map the long java fields to a shorter oracle fields:
        DataRecordMapping mapping = new DataRecordMapping();
        mapping.addFieldMapping(new DataRecordFieldMapping("recommReceivedDate","recommendationReceivedDate"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recommFollowup1Date","recommendationFollowup1Date"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recommFollowup2Date","recommendationFollowup2Date"));
        mapping.addFieldMapping(new DataRecordFieldMapping("recommSentDate","recommendationSentDate"));

        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Wb_Ci_Risk.save_survey",mapping);
        try {
            dataSaved = spDao.executeBatch(rs);    
        }
        catch (SQLException se) {
            String error = this.getClass().getName()+ ".saveAllSurvey:"+se.getMessage();
            l.severe(error);
            throw new AppException(error);
        }

        l.exiting(this.getClass().getName(), "saveAllSurvey", dataSaved + "");
        return dataSaved;
    }
}
