package dti.ci.riskmgr.survey.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The Data Access inteface for Risk Management Survey Tracking.
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
public interface SurveyDAO {

   /**
     * load survey data for an entity represented by entityId in the record
     *
     * @param input: a record containing entityId
     * @return Survey Data for an given entity
     */
    public RecordSet loadAllSurvey(Record input);

    /**
     * method to save all survey data
     *
     * @param rs: survey data represented as RecordSet
     * @return integer indicating the number of survey rows saved to RDBMS
     */
    public int saveAllSurvey(RecordSet rs);
}
