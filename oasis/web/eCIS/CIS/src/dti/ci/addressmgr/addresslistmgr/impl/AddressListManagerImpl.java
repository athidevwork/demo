package dti.ci.addressmgr.addresslistmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.addresslistmgr.AddressListManager;
import dti.ci.addressmgr.addresslistmgr.dao.AddressListDAO;
import dti.ci.addressmgr.dao.AddressDAO;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for Address List.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 23, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------
 *         04/01/2005       HXY         Removed singleton implementation.
 *         04/19/2005       HXY         Created one instance DAO.
 *         02/05/2007       kshen
 *         11/27/2008       Leo         For issue 88568.
 *         07/25/2011       kshen       Added method changePrimaryAddress.
 *         08/09/2011       kshen       Changed for issue 123063.
 *         04/18/2013       Elvin       Issue 141148: Add primaryAddressB field check
 *         05/02/2013       kshen       Issue 141148.
 *         09/21/2016       ylu         Issue 179400: update primary address
 *         ----------------------------------------------------------------
 */

public class AddressListManagerImpl implements AddressListManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public RecordSet loadAddressList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressList", new Object[]{inputRecord});
        }

        RecordSet rs = getAddressListDAO().loadAddressList(inputRecord, AddSelectIndLoadProcessor.getInstance());
        processFuturePrimaryAddress(rs);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressList", rs);
        }
        return rs;
    }

    /**
     * Display corresponding future address messages base on primaryAddressB
     *
     * @param rs
     */
    private void processFuturePrimaryAddress(RecordSet rs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "processFuturePrimaryAddress", new Object[]{rs});
        }

        if (rs != null && rs.getSize() != 0) {
            boolean warningFound = false;
            boolean errorFound = false;

            for (Record record : rs.getRecordList()) {
                String primaryAddressB = record.getStringValue("primaryAddressB");
                if (!warningFound && "F".equals(primaryAddressB)) {
                    warningFound = true;
                    MessageManager.getInstance().addWarningMessage("ci.entity.addressesList.waring.futurePrimaryAddressExist");
                } else if (!errorFound && "E".equals(primaryAddressB)) {
                    errorFound = true;
                    MessageManager.getInstance().addErrorMessage("ci.entity.addressesList.error.futurePrimaryAddressErrorExist");
                }
            }
        }

        l.exiting(getClass().getName(), "processFuturePrimaryAddress");
    }

    @Override
    public Record getEntityLockFlag(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getEntityLockFlag", new Object[]{inputRecord});
        }

        String check4LockedPolicy = SysParmProvider.getInstance().getSysParm("CS_CHK4LOCKED_POL", "N");
        Record outRecord = new Record();
        if (YesNoFlag.getInstance(check4LockedPolicy).booleanValue()) {
            outRecord = getAddressListDAO().getEntityLockFlag(inputRecord);
            AddressFields.setEntityLockFlag(outRecord, outRecord.getStringValue(AddressFields.RETURN_VALUE));
        } else {
            AddressFields.setEntityLockFlag(outRecord, YesNoFlag.N.toString());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getEntityLockFlag", outRecord);
        }
        return outRecord;
    }

    @Override
    public Record getNumOfAddrRole(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNumOfAddrRole", new Object[]{inputRecord});
        }

        RecordSet rs = getAddressDAO().loadChangeAddressRoles(inputRecord, new DefaultRecordLoadProcessor());
        Record record = new Record();
        if (rs == null) {
            record.setFieldValue(AddressFields.RETURN_VALUE, "0");
        } else {
            record.setFieldValue(AddressFields.RETURN_VALUE, rs.getSize());
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNumOfAddrRole", record);
        }
        return record;
    }

    @Override
    public Record getNumOfPrimaryAddrRoleInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getNumOfPrimaryAddrRoleInfo", new Object[]{inputRecord});
        }

        Record record = new Record();
        RecordSet rsPrimaryAddress = getAddressListDAO().loadPrimaryAddress(inputRecord, new DefaultRecordLoadProcessor());
        if (null != rsPrimaryAddress) {
            if (rsPrimaryAddress.getSize() == 1) {
                //Set primary address pk
                AddressFields.setAddressId(inputRecord, AddressFields.getAddressId(rsPrimaryAddress.getFirstRecord()));
                AddressFields.setPrimaryAddressId(record, AddressFields.getAddressId(rsPrimaryAddress.getFirstRecord()));
                RecordSet rs = getAddressDAO().loadChangeAddressRoles(inputRecord, new DefaultRecordLoadProcessor());
                if (rs == null) {
                    record.setFieldValue(AddressFields.RETURN_VALUE, "0");
                } else {
                    record.setFieldValue(AddressFields.RETURN_VALUE, rs.getSize());
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getNumOfPrimaryAddrRoleInfo", record);
        }
        return record;
    }

    @Override
    public void changePrimaryAddress(Record inputRecord) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "changePrimaryAddress", new Object[]{inputRecord});
        }

        Record record = new Record();
        record.setFieldValue(AddressFields.ADDRESS_ID, inputRecord.getStringValue("newPrimaryAddressId"));
        record.setFieldValue(AddressFields.EFFECTIVE_TO_DATE, "01/01/3000");
        record.setFieldValue(AddressFields.PRIMARY_ADDRESS_B, "Y");
        getAddressListDAO().changePrimaryAddress(record);

        l.exiting(getClass().getName(), "changePrimaryAddress");
    }

    /**
     * Save the changed Address to DB
     *
     * @param inputRecords
     * @return
     */
    public int saveAllAddress(RecordSet inputRecords) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllAddress", new Object[]{inputRecords});
        }

        RecordSet changedAddresses = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRecords);

        Record inputRecord = null;
        int recordIdx = 0;
        for (recordIdx = 0; recordIdx < changedAddresses.getSize(); recordIdx++) {
            inputRecord = changedAddresses.getRecord(recordIdx);
            if ("Y".equals(inputRecord.getStringValue("PRIMARYADDRESSB", "N"))) {
                AddressFields.setExpiredAddressFK(inputRecord, AddressFields.getAddressId(inputRecord));
                AddressFields.setAddressId(inputRecord,null);
                Record result = getAddressListDAO().savePrimaryAddress(inputRecord);
                if (!StringUtils.isBlank(AddressFields.getNewAddressId(result))) {
                    changedAddresses.removeRecord(inputRecord,true);
                }
            }
        }

        int processCount = 0;
        if (changedAddresses.getSize() > 0) {
            processCount = getAddressListDAO().saveAllAddress(changedAddresses);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveAllAddress", new Integer(processCount));
        }
        return processCount;
    }

    /**
     * Get AddressListDAO Configuration
     *
     * @return
     */
    public AddressListDAO getAddressListDAO() {
        return m_addressListDAO;
    }

    /**
     * Set AddressListDAO Configuration
     *
     * @param m_addressListDAO
     */
    public void setAddressListDAO(AddressListDAO m_addressListDAO) {
        this.m_addressListDAO = m_addressListDAO;
    }

    /**
     * Get Workbench Configuration
     *
     * @return WorkbenchConfiguration
     */
    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    /**
     * Set Workbench Configuration
     *
     * @param workbenchConfiguration
     */
    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public AddressDAO getAddressDAO() {
        return m_addressDAO;
    }

    public void setAddressDAO(AddressDAO addressDAO) {
        this.m_addressDAO = addressDAO;
    }

    private AddressDAO m_addressDAO;
    private WorkbenchConfiguration m_workbenchConfiguration;
    private AddressListDAO m_addressListDAO;
    private final String ACTION_CLASS_NAME = "dti.ci.addressmgr.addresslistmgr.struts.MaintainAddressListAction";
    private static final String ROW_STATUS = "rowStatus";
    private static final String NEW = "NEW";
    private static final String MODIFIED = "MODIFIED";
    private static final String DELETED = "DELETED";

}
