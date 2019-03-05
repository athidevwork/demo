package dti.ci.importmgr.impl;

import dti.ci.addressmgr.AddressManager;
import dti.ci.addressmgr.addresslistmgr.dao.AddressListDAO;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.clientmgr.EntityAddManager;
import dti.ci.core.error.ExpMsgConvertor;
import dti.ci.importmgr.DataImportManager;
import dti.ci.licensemgr.LicenseManager;
import dti.ci.phonemgr.PhoneListManager;
import dti.oasis.app.AppException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.DefaultRecordLoadProcessor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   6/23/14
 *
 * @author ldong
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/26/2014       Elvin       Issue 157520: add isValidStateAndCounty
 * 02/07/2017       jld         Issue 181813. Corrections and additions for missing fields.
 * 06/02/2017       ddai        Issue 185818. Corrections for missing fields.
 * 08/14/2017       dpang       Issue 187318. Change to import legacy_data_id.
 * 05/03/2018       dpang       Issue 192743. Change to use getEntityAddManager().saveEntity
 * ---------------------------------------------------
 */
public class DataImportManagerImpl implements DataImportManager {
    /**
     * saveDataImport
     *
     * @param entityRs
     * @param addressRs
     * @param phoneRs
     */
    public boolean saveDataImport(RecordSet entityRs, RecordSet addressRs, RecordSet phoneRs, RecordSet licenseRs) {
        Logger l = LogUtils.getLogger(getClass());
        String methodName = "loadAddressSearchAddList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        boolean successFlag = true;
        try {
            for (int i = 0; i < entityRs.getSize(); i++) {
                Record entityRd = entityRs.getRecord(i);
                String entityId = entityRd.getStringValue("entityId");
                String newEntityPK = "-1";
                RecordSet addresses = addressRs.getSubSet(new RecordFilter("sourceRecordId", entityId));
                Record primaryAddress = null;
                String primaryAddressId = "";
                for (int j = 0; j < addresses.getSize(); j++) {
                    Record addressRd = addresses.getRecord(j);
                    if (addressRd.hasField("primaryAddressB")) {
                        if ("Y".equalsIgnoreCase(addressRd.getStringValue("primaryAddressB"))) {
                            primaryAddress = addressRd;
                        }
                    }
                }
                if (primaryAddress == null) {
                    primaryAddress = addresses.getFirstRecord();
                }
                if (primaryAddress.hasField("countryCode") && "USA".equalsIgnoreCase(primaryAddress.getStringValue("countryCode"))) {
                    primaryAddress.setFieldValue("usaAddressB", "Y");
                } else {
                    primaryAddress.setFieldValue("usaAddressB", "N");
                }

                primaryAddressId = primaryAddress.getStringValue("addressId");
                RecordSet phones = phoneRs.getSubSet(new RecordFilter("sourceRecordId", primaryAddressId));
                Record primaryPhone = null;
                String primaryPhoneId = "";
                for (int j = 0; j < phones.getSize(); j++) {
                    Record phoneRd = phones.getRecord(j);
                    if (phoneRd.hasField("primaryNumberB")) {
                        if ("Y".equalsIgnoreCase(phoneRd.getStringValue("primaryNumberB"))) {
                            primaryPhone = phoneRd;
                        }
                    }
                }
                if (primaryPhone == null) {
                    primaryPhone = phones.getFirstRecord();
                }

                Record entityInfoRecord = new Record();
                primaryPhone.setFieldValue("primaryNumberB", "Y");
                primaryPhoneId = primaryPhone.getStringValue("phoneNumberId");
                if (primaryPhone.hasField("relatedToAddress") && "Y".equalsIgnoreCase(primaryPhone.getStringValue("relatedToAddress"))) {
                    entityInfoRecord.setFieldValue("isPhnNumNotRltdToAddr", "N");
                } else {
                    entityInfoRecord.setFieldValue("isPhnNumNotRltdToAddr", "Y");
                }

                if (entityRd.hasField("ENTITYSELECT")) {
                    if ("-1".equalsIgnoreCase(entityRd.getStringValue("ENTITYSELECT"))) {
                        entityInfoRecord.setFieldValue("okToSkipEntityDups", "Y");
                    } else {
                        entityInfoRecord.setFieldValue("okToSkipEntityDups", "N");
                    }
                }

                entityInfoRecord.setFields(entityRd);
                entityInfoRecord.setFields(primaryAddress);
                entityInfoRecord.setFields(primaryPhone);
                //Remove pk fields as it's insertion
                entityInfoRecord.remove("entityId");
                entityInfoRecord.remove("addressId");
                entityInfoRecord.remove("phoneNumberId");

                EntityAddInfo addInfo = getEntityAddManager().saveEntity(entityInfoRecord);

                if (!addInfo.isEntityAdded()) {
                    if (addInfo.getEntityDupCount() != 0) {
                        entityRd.setFieldValue("entityStatus", "DUPLICATE");
                        successFlag = false;
                    }
                } else {
                    newEntityPK = addInfo.getEntityPK();

                    Record inputRecord = new Record();
                    inputRecord.setFieldValue("entityId", newEntityPK);
                    RecordSet rsPrimaryAddress = getAddressListDAO().loadPrimaryAddress(inputRecord, new DefaultRecordLoadProcessor());

                    for (int j = 0; j < addresses.getSize(); j++) {
                        Record addressRd = addresses.getRecord(j);
                        String addId = addressRd.getStringValue("addressId");
                        String srcId = addressRd.getStringValue("sourceRecordId");
                        addressRd.setFieldValue("addressId", "");
                        addressRd.setFieldValue("sourceRecordId", newEntityPK);
                        addressRd.setFieldValue("sourceTableName", "ENTITY");
                        if (addressRd.hasField("countryCode") && "USA".equalsIgnoreCase(addressRd.getStringValue("countryCode"))) {
                            addressRd.setFieldValue("usaAddressB", "Y");
                        } else {
                            addressRd.setFieldValue("usaAddressB", "N");
                        }
                        String newAddressId = "";
                        //handle primary address and Phone
                        if (primaryAddressId.equalsIgnoreCase(addId)) {
                            newAddressId = rsPrimaryAddress.getFirstRecord().getStringValue("addressId");
                        } else {
                            Record recAddressUpdateResult = getAddressManager().updateAddressDetailInfo(addressRd);
                            // Get the new address Id.
                            newAddressId = recAddressUpdateResult.getStringValue("newAddressId");
                        }
                        addressRd.setFieldValue("addressId", addId);
                        addressRd.setFieldValue("sourceRecordId", srcId);

                        RecordSet phone = phoneRs.getSubSet(new RecordFilter("sourceRecordId", addId));
                        for (int k = 0; k < phone.getSize(); k++) {
                            Record phoneRd = phone.getRecord(k);
                            String phSrcId = phoneRd.getStringValue("sourceRecordId");
                            String phoneId = phoneRd.getStringValue("phoneNumberId");

                            if (!primaryPhoneId.equalsIgnoreCase(phoneId)) {
                                phoneRd.setFieldValue("rowStatus", "NEW");
                                if (phoneRd.hasField("relatedToAddress") && "Y".equalsIgnoreCase(phoneRd.getStringValue("relatedToAddress"))) {
                                    phoneRd.setFieldValue("sourceTableName", "ADDRESS");
                                    phoneRd.setFieldValue("sourceRecordId", newAddressId);
                                } else {
                                    phoneRd.setFieldValue("sourceTableName", "ENTITY");
                                    phoneRd.setFieldValue("sourceRecordId", newEntityPK);
                                }
                                // Do Phone number insert
                                Record recPhoneUpdateResult = getPhoneListManager().savePhoneNumber(phoneRd);
                            }
                            phoneRd.setFieldValue("sourceRecordId", phSrcId);
                        }
                    }

                    // Process License
                    RecordSet licenses = licenseRs.getSubSet(new RecordFilter("entityId", entityId));
                    for (int j = 0; j < licenses.getSize(); j++) {
                        Record license = licenses.getRecord(j);
                        String lcSrcId = license.getStringValue("entityId");
                        String licenseId = license.getStringValue("licenseProfileId");
                        license.setUpdateIndicator("I");
                        license.setFieldValue("entityId", newEntityPK);
                        // Do License insert
                        RecordSet rs = new RecordSet();
                        rs.addRecord(license);
                        int count = getLicenseManager().saveLicense(rs);
                        license.setFieldValue("entityId", lcSrcId);
                        license.setFieldValue("licenseProfileId", licenseId);
                    }
                    entityRd.setFieldValue("entityStatus", "Success");
                }
            }
        } catch (Exception e) {
            l.throwing(getClass().getName(), methodName, e);
            String msg = ExpMsgConvertor.getBeautifyExpDetailForHtml(e).substring(1).trim();
            throw new AppException("ci.CIAddressDetail.error", "",
                    new String[]{MessageManager.getInstance().formatMessage(msg)});
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, successFlag);
        }
        return successFlag;
    }

    /**
     * isValidStateAndCounty
     * @param stateCode
     * @param countyCode
     */
    public boolean isValidStateAndCounty(String stateCode, String countyCode) {
        return getAddressListDAO().isValidStateAndCounty(stateCode, countyCode);
    }

    public DataSource getAppDataSource() {
        return m_appDataSource;
    }

    public void setAppDataSource(DataSource appDataSource) {
        m_appDataSource = appDataSource;
    }

    private DataSource m_appDataSource;

    public AddressManager getAddressManager() {
        return m_addressManager;
    }

    public void setAddressManager(AddressManager addressManager) {
        this.m_addressManager = addressManager;
    }

    private AddressManager m_addressManager;

    public PhoneListManager getPhoneListManager() {
        return m_phoneListManager;
    }

    public void setPhoneListManager(PhoneListManager phoneListManager) {
        this.m_phoneListManager = phoneListManager;
    }

    private PhoneListManager m_phoneListManager;

    public AddressListDAO getAddressListDAO() {
        return m_addressListDAO;
    }

    public void setAddressListDAO(AddressListDAO addressListDAO) {
        m_addressListDAO = addressListDAO;
    }

    private AddressListDAO m_addressListDAO;

    public LicenseManager getLicenseManager() {
        return m_licenseManager;
    }

    public void setLicenseManager(LicenseManager licenseManager) {
        this.m_licenseManager = licenseManager;
    }

    private LicenseManager m_licenseManager;

    public EntityAddManager getEntityAddManager() {
        return m_entityAddManager;
    }

    public void setEntityAddManager(EntityAddManager entityAddManager) {
        this.m_entityAddManager = entityAddManager;
    }

    private EntityAddManager m_entityAddManager;
}
