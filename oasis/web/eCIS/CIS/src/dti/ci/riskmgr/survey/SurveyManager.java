package dti.ci.riskmgr.survey;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * The  Business Object for Risk Management Survey Tracking.
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
public interface SurveyManager {

    /** method to get the intial "default" value when adding a new survey data.
     *
     * @param input  a record containing entityId
     * @return  record containing the intial values.
     */
    public Record getInitialValuesForNewSurvey(Record input);


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
     * @return integer indicating the number of survey rows applied to RDBMS
     */
    public int saveAllSurvey(RecordSet rs);


}