package dti.ci.certificationmgr;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;


/**
 * The business component of Certification information.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   2012-02-17
 *
 * @author parker
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface CertificationManager {

    /**
     * load Certification information.
     * @param record
     * @return
     */
    public RecordSet loadCertification(Record record);

    /**
     * save Certification information.
     * @param record
     * @return
     */
    public int saveCertification(RecordSet inputRecords);

    /**
     * Retrieve the entity's date of birth.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  dateOfBirth
     * @throws Exception
     */
    public String getDateOfBirth(String entityId);

    /**
     * Retrieve the entity's certificationBoardCode.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  constantTypeCode
     * @throws Exception
     */
    public String getConstant(String constantTypeCode);

    /**
     * Save Certification
     *
     * @param inputRecord
     */
    public Record saveCertification(Record inputRecord);
}