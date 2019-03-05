package dti.ci.phonemgr;

import dti.ci.helpers.ICIPhoneNumberConstants;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: huixu
 * Date: Mar 13, 2012
 * Time: 9:58:33 AM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/

public interface PhoneListManager extends ICIPhoneNumberConstants{
           
    /**
     * Save all phone number
     *
     * @param inputRecords
     */
    public void saveAllPhoneNumber(RecordSet inputRecords);
    
    /**
     * To load PhoneNumberList
     *
     * @param inputRecord
     * @return
     */
     public RecordSet getPhoneNumberList(Record inputRecord);
    
    /**
     * Removes the one character ("X") at the beginning of a source record FK that
     * indicates that the source is expired.
     *
     * @param srcRecFK The soruce record FK.
     * @return String
     */
    public String transformSourceFK(String srcRecFK);

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
    public ArrayList createSourceRecordLOV(Record inputRecord);

    /** for now, we use ApplicationContext.getBean to get the reference to WorkbenchConfiguration
     *  Once  we fully refactor the page, we can actually move it to be part of the spring configuration
     * @param input
     * @return
     */
    public Record getIntialValuesForAddingPhoneNumber(Record input);

    /**
     * Save phone number
     *
     * @param inputRecord
     */
    public Record savePhoneNumber(Record inputRecord);

    /**
     * Save phone number for web service.
     *
     * @param inputRecord
     */
    public Record savePhoneNumberWs(Record inputRecord);

}
