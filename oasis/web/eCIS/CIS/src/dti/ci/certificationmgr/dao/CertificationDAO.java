package dti.ci.certificationmgr.dao;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;

import java.sql.Connection;

/**
 * DAO for Certification
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   2012-02-03
 *
 * @author parker
 */

/*
 * Revision Date      Revised By       Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public interface CertificationDAO {

    /**
     * load Certification information.
     * @param record
     * @return
     */
    public RecordSet loadCertification(Record inputRecord);

    /**
     * save Certification information.
     * @param record
     * @return
     */
    public int saveCertification(RecordSet rs);
    
    /**
     * Retrieve the entity's date of birth.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  dateOfBbirth
     * @throws Exception
     */
    public String getDateOfBirth(String entityId);

    /**
     * Retrieve the constantTypeCode.
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