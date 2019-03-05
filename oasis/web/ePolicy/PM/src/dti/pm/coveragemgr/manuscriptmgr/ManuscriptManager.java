package dti.pm.coveragemgr.manuscriptmgr;

import dti.oasis.error.ValidationException;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.pm.policymgr.PolicyHeader;

/**
 * Interface to handle Implementation of Manuscript Manager.
 * <p/>
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
 * 05/16/2012       jshen       132118 - Added inputRecord parameter to method saveAllManuscript()
 * 06/27/2012       tcheng      134650 - Added a parameter policyHeader for saveAttachment amd made version for upload RTF
 * 09/05/2012       xnie        136023 - Roll backed 132118 fix.
 * 08/31/2016       tzeng       179057 - Added validateManuscriptOverlap to validate duplicate manuscripts in back end.
 * ---------------------------------------------------
 */
public interface ManuscriptManager {

    public static final String MAINTAIN_MANUSCRIPT_ACTION_CLASS_NAME = "dti.pm.coveragemgr.manuscriptmgr.struts.MaintainManuscriptAction";

    /**
     * Returns a RecordSet loaded with list of Manuscript data.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor
     * @param inputRecord  input record
     * @return a RecordSet loaded with list of Manuscript.
     */
    RecordSet loadAllManuscript(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor, Record inputRecord);

    /**
     * Returns a RecordSet loaded with list of Manuscript detail data.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param inputRecord   a Record with conditions for loading Manuscript detail data.
     * @param loadProcessor an instance of data load processor
     * @return a RecordSet loaded with list of Manuscript detail.
     */
    RecordSet loadAllManuscriptDetail(PolicyHeader policyHeader, Record inputRecord, RecordLoadProcessor loadProcessor);

    /**
     * Load label, width and visibility for Manuscript Detail data.
     * <p/>
     *
     * @param record input record
     * @return a RecordSet with loaded list of information to control Manuscript Detail data.
     */
    RecordSet loadManuscriptEndorsementDtl(Record record);

    /**
     * Save/Update/Delete all inserted/updated/deleted Manuscript data.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the added/updated Manuscript info.
     * @return the number of rows updated.
     */
    int saveAllManuscript(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Save/Update/Delete all inserted/updated/deleted Manuscript Detail data.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecords a set of Records, each with the added/updated Manuscript Detail info.
     * @return the number of rows updated.
     */
    int saveAllManuscriptDetail(PolicyHeader policyHeader, RecordSet inputRecords);

    /**
     * Load all available Manuscript data for selection.
     * <p/>
     *
     * @param policyHeader  policy header that contains all key policy information.
     * @param loadProcessor an instance of data load processor.
     * @return a RecordSet loaded with list of available Manuscript.
     */
    RecordSet loadAllAvailableManuscript(PolicyHeader policyHeader, RecordLoadProcessor loadProcessor);

    /**
     * Get the default values for newly added Manuscript(s).
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with conditions for getting default Manuscript data.
     * @return a Record with Manuscript default data.
     */
    Record getInitialValuesForAddManuscript(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Get the default values for newly added Manuscript Detail.
     * <p/>
     *
     * @param policyHeader policy header that contains all key policy information.
     * @param inputRecord  a Record with conditions for getting default Manuscript Detail data.
     * @return a Record with Manuscript Detail default data.
     */
    Record getInitialValuesForAddManuscriptDetail(PolicyHeader policyHeader, Record inputRecord);

    /**
     * Save the attached RTF file to db table.
     * <p/>
     *
     * @param inputRecord  a Record with selected manuscript pk and file path.
     * @return
     */
    void saveAttachment(PolicyHeader policyHeader,Record inputRecord);

    /**
     * Load the attached RTF file to download.
     * <p/>
     *
     * @param inputRecord  a Record with selected manuscript pk.
     * @return a Record with file input stream.
     */
    Record loadAttachment(Record inputRecord);

    /**
     * All terms overlap validation in back end.
     * @param policyHeader
     * @throws ValidationException
     */
    void validateManuscriptOverlap(PolicyHeader policyHeader) throws ValidationException;
}
