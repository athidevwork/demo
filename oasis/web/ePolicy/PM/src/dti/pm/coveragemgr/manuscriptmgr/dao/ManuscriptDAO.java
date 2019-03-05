package dti.pm.coveragemgr.manuscriptmgr.dao;

import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;

/**
 * An interface that provides DAO operation for Manuscript information.
 * <p/>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 17, 2007
 *
 * @author jshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 02/10/2012       wfu         125055 - Added saveAttachment and loadAttachment.
 * 06/25/2012       tcheng      134650 - Added changeAttachment.
 * 08/31/2016       tzeng       179057 - Added validateManuscriptOverlap to validate duplicate manuscripts in back end.
 * 09/09/2016       xnie        178813 - Added validateSameOffVersionExists().
 * ---------------------------------------------------
 */
public interface ManuscriptDAO {
    /**
     * Retrieves all Manuscript data
     * <p/>
     *
     * @param record input record
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a RecordSet with loaded list of Manuscript data.
     */
    RecordSet loadAllManuscript(Record record, RecordLoadProcessor loadProcessor);

    /**
     * Retrieves all Manuscript Detail data
     * <p/>
     *
     * @param record input record
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a RecordSet with loaded list of Manuscript Detail data.
     */
    RecordSet loadAllManuscriptDetail(Record record, RecordLoadProcessor loadProcessor);

    /**
     * Load label, width and visibility for Manuscript Detail data.
     * <p/>
     *
     * @param record input record
     * @return a RecordSet with loaded list of information to control Manuscript Detail data.
     */
    RecordSet loadManuscriptEndorsementDtl(Record record);

    /**
     * Save all newly added or updated Manuscript data with Pm_Manu_Endor.Save_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Mansucript Records for saving.
     * @return the number of rows updated.
     */
    int saveAllManuscript(RecordSet inputRecords);

    /**
     * Save the Manuscript data when the officially saved data changed with Pm_Manu_Endor.Change_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Records for updating.
     * @return the number of rows updated.
     */
    int updateAllManuscript(RecordSet inputRecords);

    /**
     * Save all newly added or updated Manuscript detail data with Pm_Manu_Endor.Save_Manuscript_Dtl stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Detail Records for saving.
     * @return the number of rows updated.
     */
    int saveAllManuscriptDetail(RecordSet inputRecords);

    /**
     * Delte all given input Manuscript records with Pm_Manu_Endor.Delete_Manuscript stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Mansucript Records for deleting.
     * @return the number of rows deleted.
     */
    int deleteAllManuscript(RecordSet inputRecords);

    /**
     * Delete all given input Manuscript Detail records with Pm_Manu_Endor.Delete_Manuscript_Dtl stored procedure.
     * <p/>
     *
     * @param inputRecords a set of Manuscript Detail records for deleting.
     * @return the number of rows deleted.
     */
    int deleteAllManuscriptDetail(RecordSet inputRecords);

    /**
     * Load all available Mansucript data for selection.
     * <p/>
     *
     * @param inputRecord a Record with information to load the results.
     * @param loadProcessor an instance of the load processor to set page entitlements
     * @return a set of Records with loaded available Manuscript data.
     */
    RecordSet loadAllAvailableManuscript(Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Save the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    void saveAttachment(Record inputRecord);

    /**
     * Load the attached RTF file to web page for downloading.
     * <p/>
     *
     * @param inputRecord input record
     * @return a Record with file input stream.
     */
    Record loadAttachment(Record inputRecord);
    
    /**
     * Change the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    void changeAttachment(Record inputRecord);

    /**
     * Check if manuscripts overlap in all terms.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    boolean hasManuscriptOverlap(Record inputRecord);

    /**
     * Check if any manuscript endorsement version which is from same official record and in same time period exists.
     * <p/>
     *
     * @param inputRecord input record
     * @return
     */
    String validateSameOffVersionExists(Record inputRecord);
}
