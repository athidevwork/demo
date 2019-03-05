package dti.ci.entityquickaddmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.ci.clientmgr.EntityAddInfo;
import dti.ci.clientmgr.EntityAddManager;
import dti.ci.entityquickaddmgr.EntityQuickAddManager;
import dti.ci.entityquickaddmgr.dao.EntityQuickAddDAO;
import dti.ci.licensemgr.LicenseManager;
import dti.ci.priorcarriermgr.PriorCarrierManager;
import dti.oasis.app.ConfigurationException;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The business component of Quick Add Person.
 * <p/>
 * <p>(C) 2016 Delphi Technology, inc. (dti)</p>
 * Date:  08/15/2016
 *
 * @author jdingle
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *  11/06/2017      jdingle     Issue 188876. Fix typo of field name.
 *  11/16/2018      Elvin       Issue 195835: grid replacement
 * ---------------------------------------------------
 */
public abstract class EntityQuickAddManagerImpl implements EntityQuickAddManager {

    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Get the Record for matching prefix
     *
     * @param inputRecord
     * @return record
     */
    public Record getRecordForPrefix(Record inputRecord, String prefix) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getRecordForPrefix", new Object[]{inputRecord, prefix});
        }
        Record outRecord = new Record();

        List<String> fieldNameList = inputRecord.getFieldNameList();

        //Remove prefix for page fields.
        for (String fieldName : fieldNameList) {
            if (fieldName.startsWith(prefix)) {
                outRecord.setFieldValue(fieldName.substring(prefix.length()),
                        inputRecord.getFieldValue(fieldName));
            }
        }

        l.exiting(getClass().toString(), "getRecordForPrefix", outRecord);
        return outRecord;
    }

    public EntityAddInfo saveAllEntity(Record inputRecord) {
        String methodName = "saveAllEntity";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName);
        }
        EntityAddInfo addInfo = getEntityAddManager().validateAddrAndSaveEntity(inputRecord, true);

        // continue with rest of save if entity is added
        // otherwise skip and pass duplicate list back
        if (addInfo.isEntityAdded()) {
            String entId = addInfo.getEntityPK();

            if (!StringUtils.isBlank(entId)) {
                saveAddressAndPhoneNumber(inputRecord, entId);

                // Insert License
                saveLicense(inputRecord, entId);

                // Insert Denominator
                saveDenominator(inputRecord, entId);

                // Insert Prior Carrier
                saveAllPriorCarrier(inputRecord, entId);

                // Save other info. e.g education for person
                saveOtherInfo(inputRecord, entId);
            }
        }

        l.exiting(getClass().getName(), methodName);
        return addInfo;
    }

    protected void saveAddressAndPhoneNumber(Record inputRecord, String entId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressAndPhoneNumber", new Object[]{inputRecord,entId});
        }

        Record address1Record = getRecordForPrefix(inputRecord, "address_");
        Record address2Record = getRecordForPrefix(inputRecord, "address2_");
        Record phone2Record = getRecordForPrefix(inputRecord, "phoneNumber2_");

        if (!StringUtils.isBlank(address1Record.getStringValue("addressTypeCode"))
                && !StringUtils.isBlank(address1Record.getStringValue("addressLine1"))) {
            if (!StringUtils.isBlank(address2Record.getStringValue("addressTypeCode"))
                    && !StringUtils.isBlank(address2Record.getStringValue("addressLine1"))) {

                // Insert second Address
                handleCountryFields(address2Record);
                address2Record.setFieldValue("sourceRecordId", entId);
                Record rec = getEntityQuickAddDAO().saveAddress(address2Record);
                String addressId = rec.getStringValue("addressId");

                if (!StringUtils.isBlank(addressId) && !StringUtils.isBlank(phone2Record.getStringValue("phoneNumber"))) {
                    // Insert second phone number
                    phone2Record.setFieldValue("sourceRecordId", addressId);
                    rec = getEntityQuickAddDAO().savePhone(phone2Record);
                }
                ;
            }
        }
        l.exiting(getClass().getName(), "saveAddressAndPhoneNumber");
    }

    private void handleCountryFields(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleCountryFields", new Object[]{inputRecord});
        }

        String countryCode = inputRecord.getStringValue("countryCode");
        String configuredUsaCountryCode = SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_USA, "USA");

        if (countryCode.equalsIgnoreCase(configuredUsaCountryCode)) {
            inputRecord.setFieldValue("usaAddressB", "Y");
        } else {
            inputRecord.setFieldValue("usaAddressB", "N");

            if (!isCountryCodeConfigured(countryCode)) {
                inputRecord.setFieldValue("province", inputRecord.getStringValueDefaultEmpty("otherProvince"));
            }
        }

        l.exiting(getClass().getName(), "handleCountryFields");
    }

    private boolean isCountryCodeConfigured(String countryCode) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "isCountryCodeConfigured", new Object[]{countryCode});
        }

        boolean result = false;
        String configuredCountryCodes = SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_CONFIG, "USA");
        String[] configuredCountryCodeArray = configuredCountryCodes.split(",");
        for (String configuredCountryCode : configuredCountryCodeArray) {
            if (configuredCountryCode.equals(countryCode)) {
                result = true;
                break;
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "isCountryCodeConfigured", result);
        }
        return result;
    }

    protected void saveLicense(Record inputRecord, String entId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveLicense", new Object[]{inputRecord, entId});
        }

        Record licenseRecord = getRecordForPrefix(inputRecord, "licenseProfile_");

        if (!StringUtils.isBlank(licenseRecord.getStringValue("licenseNo"))) {
            licenseRecord.setFieldValue("entityId", entId);
            licenseRecord.setFieldValue("rowStatus", "NEW");
            licenseRecord.setUpdateIndicator("I");
            RecordSet licenseRS = new RecordSet();
            licenseRS.addRecord(licenseRecord);
            getLicenseManager().saveLicense(licenseRS);
        }
        l.exiting(getClass().getName(), "saveLicense");
    }

    protected void saveDenominator(Record inputRecord, String entId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveDenominator", new Object[]{inputRecord, entId});
        }

        Record denominatorRecord = getRecordForPrefix(inputRecord, "entityDenominator_");

        if (!StringUtils.isBlank(denominatorRecord.getStringValue("denominatorCode"))) {
            denominatorRecord.setFieldValue("entityId",entId);
            getEntityQuickAddDAO().saveDenominator(denominatorRecord);
        }
        l.exiting(getClass().getName(), "saveDenominator");
    }

    protected void saveAllPriorCarrier(Record inputRecord, String entId) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAllPriorCarrier", new Object[]{inputRecord, entId});
        }

        Record priorCarrierRecord = getRecordForPrefix(inputRecord, "externalClaimsReportSummary_");

        if (!StringUtils.isBlank(priorCarrierRecord.getStringValue("primaryInsuranceCompany"))) {
            priorCarrierRecord.setFieldValue("entityId", entId);
            priorCarrierRecord.setFieldValue("rowStatus", "NEW");
            priorCarrierRecord.setUpdateIndicator("I");
            RecordSet priorCarrierRS = new RecordSet();
            priorCarrierRS.addRecord(priorCarrierRecord);
            getPriorCarrierManager().saveAllPriorCarrier(priorCarrierRS);
        }

        l.exiting(getClass().getName(), "saveAllPriorCarrier");
    }

    protected abstract void saveOtherInfo(Record inputRecrd, String entId);

    public void verifyConfig() {
        if (getPriorCarrierManager() == null) {
            throw new ConfigurationException("The required property 'priorCarrierManager' is missing.");
        }

        if (getLicenseManager() == null) {
            throw new ConfigurationException("The required property 'licenseManager' is missing.");
        }

        if (getEntityQuickAddDAO() == null) {
            throw new ConfigurationException("The required property 'entityQuickAddDAO' is missing.");
        }
    }

    public LicenseManager getLicenseManager() {
        return m_licenseManager;
    }

    public void setLicenseManager(LicenseManager licenseManager) {
        this.m_licenseManager = licenseManager;
    }

    public PriorCarrierManager getPriorCarrierManager() {
        return m_priorCarrierManager;
    }

    public void setPriorCarrierManager(PriorCarrierManager priorCarrierManager) {
        m_priorCarrierManager = priorCarrierManager;
    }

    public EntityQuickAddDAO getEntityQuickAddDAO() {
        return m_entityQuickAddDAO;
    }

    public void setEntityQuickAddDAO(EntityQuickAddDAO entityQuickAddDAO) {
        this.m_entityQuickAddDAO = entityQuickAddDAO;
    }

    public EntityAddManager getEntityAddManager() {
        return m_entityAddManager;
    }

    public void setEntityAddManager(EntityAddManager entityAddManager) {
        this.m_entityAddManager = entityAddManager;
    }

    private PriorCarrierManager m_priorCarrierManager;
    private LicenseManager m_licenseManager;
    private EntityQuickAddDAO m_entityQuickAddDAO;
    private EntityAddManager m_entityAddManager;
}
