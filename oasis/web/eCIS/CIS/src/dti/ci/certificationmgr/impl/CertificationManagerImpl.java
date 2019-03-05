package dti.ci.certificationmgr.impl;

import dti.ci.certificationmgr.CertificationManager;
import dti.ci.certificationmgr.dao.CertificationDAO;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.UpdateIndicatorRecordFilter;
import dti.oasis.util.LogUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Business Object to handle Certifications.
 * <p/>
 * <p>(C) 2004 Delphi Technology, inc. (dti)</p>
 * Date:   Mar 22, 2006
 *
 * @author Hong Yuan
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
*/
public class CertificationManagerImpl implements CertificationManager {

    /**
     * load certification information.
     * @param record
     * @return
     */
    @Override
    public RecordSet loadCertification(Record record) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadCertification", new Object[]{record});
        }
        RecordSet rs =  getCertificationDAO().loadCertification(record);

        l.exiting(getClass().getName(), "loadCertification", rs);
        return rs;
    }

     /**
     * Method to save Certification information
     *
     * @param inputRecords
     * @return int
     */
    public int saveCertification(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCertification", new Object[]{inputRecords});
        }
        // Get the changes
        RecordSet changedRecords = inputRecords.getSubSet(new UpdateIndicatorRecordFilter(
                new String[]{UpdateIndicator.DELETED, UpdateIndicator.INSERTED, UpdateIndicator.UPDATED}));
        changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(changedRecords);
        int updateCount = 0;
        if (changedRecords.getSize() > 0) {
            updateCount = getCertificationDAO().saveCertification(changedRecords);
        }
        l.exiting(getClass().getName(), "saveCertification", new Integer(updateCount));
        return updateCount;
    }
    
    /**
     * Retrieve the entity's date of birth.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  dateOfBirth
     * @throws Exception
     */
    @Override
    public String getDateOfBirth(String entityId) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getDateOfBirth", new Object[]{entityId});
        }
        String dateOfBirth = getCertificationDAO().getDateOfBirth(entityId);
        l.exiting(this.getClass().getName(), "getDateOfBirth", dateOfBirth);
        return dateOfBirth;
    }
    
    /**
     * Retrieve the entity's constantTypeCode.
     *
     * @param conn     JDBC connection object.
     * @param entityPK Entity PK.
     * @return String  constantType
     * @throws Exception
     */
    @Override
    public String getConstant(String constantTypeCode) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getConstant", new Object[]{constantTypeCode});
        }
        String constantType = getCertificationDAO().getConstant(constantTypeCode);
        l.exiting(this.getClass().getName(), "getConstant", constantTypeCode);
        return constantType;
    }

    /**
     * Save Certification
     *
     * @param inputRecord
     */
    public Record saveCertification(Record inputRecord){
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveCertification", new Object[]{inputRecord});
        }

        Record recResult = getCertificationDAO().saveCertification(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveCertification");
        }
        return recResult;
    }

    public void verifyConfig() {
        if (getCertificationDAO() == null) {
            throw new ConfigurationException("The required property 'getCertificationDAO' is missing.");
        }
    }

    public CertificationDAO getCertificationDAO() {
        return certificationDAO;
    }

    public void setCertificationDAO(CertificationDAO certificationDAO) {
        this.certificationDAO = certificationDAO;
    }

    private CertificationDAO certificationDAO;
}
