package dti.ci.phonemgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

/**
 * Created by IntelliJ IDEA.
 * User: huixu
 * Date: Mar 13, 2012
 * Time: 10:00:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PhoneListDAO {


    /**
     * save all phone number
     *
     * @param inputRecords
     * @return int
     */
    public int saveAllPhoneNumber(RecordSet inputRecords);
    
     /**
     * To load PhoneNumberList
     *
     * @param inputRecord
     * @return
     */
     public RecordSet getPhoneNumberList(Record inputRecord);

        /**
     * future cis refactoring consideration:
     *  logic can be getting from CI_WEB_DEMOGRAPHIC.get_source_list_for_phone
     *
     * Creates a list of values of possible sources for phone numbers.
     *
     * @param conn       Connection object.
     * @param pk         PK of the entity who is the source of the phone numbers.
     * @param entityName Name of the entity who is the source of the phone numbers.
     * @return ArrayList - ArrayList of LabelValueBean.
     * @throws Exception
     */
    public RecordSet createSourceRecordLOV(Record inputRecord);

    /**
     * Save phone number
     *
     * @param inputRecords
     */
    public Record savePhoneNumber(Record inputRecords);

    /**
     * Save phone number for web service
     *
     * @param inputRecords
     */
    public Record savePhoneNumberWs(Record inputRecords);
}
