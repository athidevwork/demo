package dti.ci.addressmgr.impl;

import dti.ci.addressmgr.AddressFields;
import dti.ci.addressmgr.AddressManager;
import dti.ci.addressmgr.dao.AddressDAO;
import dti.ci.core.CIFields;
import dti.cs.data.dbutility.DBUtilityManager;
import dti.oasis.app.AppException;
import dti.oasis.app.ConfigurationException;
import dti.oasis.busobjs.OasisRecordSetHelper;
import dti.oasis.busobjs.UpdateIndicator;
import dti.oasis.busobjs.WorkbenchConfiguration;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.data.DataRecordFieldMapping;
import dti.oasis.data.DataRecordMapping;
import dti.oasis.error.ValidationException;
import dti.oasis.messagemgr.MessageManager;
import dti.oasis.recordset.*;
import dti.oasis.struts.AddSelectIndLoadProcessor;
import dti.oasis.util.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for adding and modifying Addresses.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Jan 27, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         ----------------------------------------------------------------------
 *         04/01/2005      HXY         Removed singleton implementation.
 *         04/14/2005      HXY         Added transaction commit logic.
 *         04/19/2005      HXY         Created one instance DAO.
 *         04/22/2005      HXY         Returned address pk while saving addr.
 *         04/26/2005      HXY         Added logic for vendor address page.
 *         09/21/2006       ligj        Issue #62554
 *         08/13/2007      FWCH        Modified validateVendorAddressInfo() to suit for
 *         USA Address
 *         08/11/2011       kshen      Added method updateAddressDetailForBulkModify,
 *                                     loadAddressDetailInfoForAddAddressCopy,
 *                                     and loadAddressDetailInfoForBulkModifyAddress for issue 99502.
 *         03/05/2018       dzhang      Issue 109177: Add methods for vendor address refactor
 *         09/18/2018       dzhang     Issue 195835: Add expiringAddressId data mapping.
 *         09/28/2018       Elvin      Issue 195344: set effectiveFromDate conditionally when loading address detail
 *         10/04/2018       jdingle    Issue 195920: Correction for 195691 mod.
 *         10/22/2018       Elvin      Issue 196584: the pass in pk -1 should be considered as an insert action when loading address detail
 *         11/09/2018       Elvin      Issue 195835: invoke address province/otherProvince logic
 *         ----------------------------------------------------------------------
 */

public class AddressManagerImpl implements AddressManager {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public Record getFieldDefaultValues(Record inputRecord) {
        l.entering(getClass().getName(), "getFieldDefaultValues");

        String actionClassName = inputRecord.getStringValueDefaultEmpty("actionClassName");
        if (StringUtils.isBlank(actionClassName)) {
            throw new AppException("No action class name.");
        }

        Record outRecord = getWorkbenchConfiguration().getDefaultValues(actionClassName);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "getFieldDefaultValues", outRecord);
        }
        return outRecord;
    }

    /**
     * Load all Address Search Add List.
     *
     * @param inputRecord
     * @return
     */
    public RecordSet loadAddressSearchAddList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressSearchAddList", new Object[]{inputRecord});
        }

        String dummySourceRecordId = inputRecord.getStringValueDefaultEmpty("dummySourceRecordId");
        if (StringUtils.isBlank(dummySourceRecordId)) {
            dummySourceRecordId = String.valueOf(getDbUtilityManager().getNextSequenceNo());
            inputRecord.setFieldValue("dummySourceRecordId", dummySourceRecordId);
        }

        RecordSet rs = getAddressDAO().loadAddressSearchAddList(inputRecord);

        if (rs != null) {
            for (Record record : rs.getRecordList()) {
                handleCountryFields(record, false);
            }

            String origAddressId = inputRecord.getStringValueDefaultEmpty("origAddressId");
            if (!StringUtils.isBlank(origAddressId, true)) {
                boolean found = false;
                for (Record record : rs.getRecordList()) {
                    if (origAddressId.equals(record.getStringValue("addressId"))) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    MessageManager.getInstance().addInfoMessage("ci.address.searchAdd.warning.addressNotFound", new String[]{origAddressId});
                }
            }
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressSearchAddList", rs);
        }
        return rs;
    }

    /**
     * Save all data for Address Search Add List
     *
     * @param inputRs
     */
    public void updateAddressSearchAddList(RecordSet inputRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressSearchAddList", new Object[]{inputRs});
        }

        RecordSet changedRecords = OasisRecordSetHelper.setRowStatusOnModifiedRecords(inputRs);
        if (changedRecords.getSize() > 0) {
            // validate insert rows
            RecordSet insertedRecords = changedRecords.getSubSet(new UpdateIndicatorRecordFilter(UpdateIndicator.INSERTED));
            for (Record record : insertedRecords.getRecordList()) {
                handleCountryFields(record, true);
                checkCountyCode(record);
            }

            getAddressDAO().updateAddressSearchAddList(changedRecords);
        }

        l.exiting(getClass().getName(), "updateAddressSearchAddList");
    }

    /**
     * Load the Vendor Address Type Info.
     *
     * @return
     */
    public Record loadVendorAddressTypeInfo() {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddressTypeInfo");
        }

        Record outRecord = null;
        RecordSet rs = getAddressDAO().loadVendorAddressTypeInfo();
        if (rs.getSize() > 0) {
            outRecord = rs.getFirstRecord();
        } else {
            throw new AppException("Vendor address type code is not properly configured.");
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddressTypeInfo", outRecord);
        }
        return outRecord;
    }

    /**
     * for a given address pk, return the address data
     *
     * @param inputRecord
     * @return
     */
    public Record loadAddressDetailInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressDetailInfo", new Object[]{inputRecord});
        }

        String copyAddressId = inputRecord.getStringValueDefaultEmpty("copyAddressId");
        String bulkModifyAddressId = inputRecord.getStringValueDefaultEmpty("bulkModifyAddressId");
        // expiredAddressId is duplicated with address_base column, rename to expiringAddressId
        String expiringAddressId = inputRecord.getStringValueDefaultEmpty("expiringAddressId");

        String addressId = "";
        if (!StringUtils.isBlank(copyAddressId) && StringUtils.isNumeric(copyAddressId)) {
            // if copyAddressId is not empty (by clicking Add Address Copy button on Address List page)
            // we create a new address by coping data from an existing address
            addressId = copyAddressId;
        } else if (!StringUtils.isBlank(bulkModifyAddressId)) {
            // if bulkModifyAddressId is not empty (by clicking Bulk Modify button on Address List page)
            // we try to update several addresses at a time
            // we display the address information of the first one
            String[] addressIds = bulkModifyAddressId.split(",");
            for (String currentId : addressIds) {
                if (!StringUtils.isBlank(currentId)) {
                    addressId = currentId;
                    break;
                }
            }

            if (StringUtils.isBlank(addressId)) {
                AppException ae = new AppException("No address id found for Bulk Modify addresses.");
                l.throwing(getClass().getName(), "loadAddressDetail", ae);
                throw ae;
            }

            // Field Primary, Effective From Date, Address Type should be hidden for bulk modify
            // status: done
            // add field dependency to field addressTypeCode, effectiveFromDate, primaryAddressB
            // field dependency: bulkModifyAddressId[IN]

        } else if (!StringUtils.isBlank(expiringAddressId, true) && StringUtils.isNumeric(expiringAddressId)) {
            // if expiringAddressId is not empty (by clicking the Change hyperlink of an address on Address List page)
            // this means we are trying to change a primary (or future primary) address
            // we display the address data on Address Detail page
            addressId = expiringAddressId;

            // when changing an existing primary address, if it is not a future primary address
            // then the primaryAddressB field need to be set to read only, since there cannot be no primary address
            // status: done, added rule DisablePrimary
        }

        Record outRecord = null;
        String effectiveFromDateOriginal = "";
        // need to accomodate call from Party Change service that has pk field and not addressId
        if(inputRecord.hasStringValue("pk") && !"-1".equals(inputRecord.getStringValue("pk"))) {
            addressId = inputRecord.getStringValue("pk");
        }

        if (StringUtils.isBlank(addressId)) {
            // it is an add transaction (by clicking Add Address button), get fields default values
            outRecord = getFieldDefaultValues(inputRecord);
            outRecord.setFields(inputRecord, true);
        } else {
            Record queryRecord = new Record();
            queryRecord.setFieldValue("addressId", addressId);
            outRecord = getAddressDAO().loadAddressDetailInfo(queryRecord, null);
            effectiveFromDateOriginal = outRecord.getStringValueDefaultEmpty("effectiveFromDate");

            if (!StringUtils.isBlank(expiringAddressId)) {
                // we have 2 situations that the expiringAddressId is not empty
                // 1. change primary address. set effective from date to today
                // 2. when expiring an address, click save and click ok to add new address to expire old one, set effective from date to the one user entered in expire address page
                String primaryAddressB = outRecord.getStringValue("primaryAddressB");
                String isFuturePrimaryAddressB = outRecord.getStringValue("isFuturePrimaryAddressB");
                if ("Y".equalsIgnoreCase(primaryAddressB)) {
                    //don't set effective from date when address is future address
                    if (!("Y".equalsIgnoreCase(isFuturePrimaryAddressB))) {
                        outRecord.setFieldValue("effectiveFromDate", DateUtils.formatDate(Calendar.getInstance().getTime()));
                    }
                } else {
                    outRecord.setFieldValue("effectiveFromDate", inputRecord.getStringValue("effectiveToDate"));
                }
            }

            handleCountryFields(outRecord, false);
        }

        // set related id column to Address Detail page to indicate current transaction type, used in OBR
        outRecord.setFieldValue("copyAddressId", copyAddressId);
        outRecord.setFieldValue("bulkModifyAddressId", bulkModifyAddressId);
        outRecord.setFieldValue("expiringAddressId", expiringAddressId);

        // set original effective from date for js validation, if user enters a new date before the original one
        // used in js if expiringAddressId is not empty
        if (!StringUtils.isBlank(effectiveFromDateOriginal)) {
            outRecord.setFieldValue("effectiveFromDateOriginal", effectiveFromDateOriginal);
        }

        // if entity locked by policy, and CS_ALLOWADDLOCKEDPOL is not set to Y
        // disable primary field, for all cases
        // status: done, added rule DisablePrimaryField
        // Todo
        // CS_ALLOWADDLOCKEDPOL and CS_CHK4LOCKED_POL seems duplicated?

        // remove field addrTypeHidePOBox, make field postOfficeAddressB depends on addressTypeCode
        // currently there is no list value configured of addrTypeHidePOBox, so no field dependency
        // we can add such value if necessary: usaAddressB=Y;addressTypeCode[IN]POLICY,BILLING
        // status: done

        // when adding new address, vendor address should be removed from the address type dropdown list (Add copy address, Add address)
        // when modify address, vendor address should be able to display, but disable all fields and save button (vendor address cannot edit here)
        // other address type, still need to remove the vendor address item from address type dropdown
        // status: done, added rule DisableVendorAddressEditing
        // if any vendor address is opened here in Address detail page, set all fields to read only and disable save button
        // keep the vendor address in the Address Type dropdown, since the OBR is not working if we removed the vendor item
        Record vendorAddressRecord = loadVendorAddressTypeInfo();
        outRecord.setFieldValue("vendorAddressTypeCode", vendorAddressRecord.getStringValueDefaultEmpty("code"));

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressDetailInfo", outRecord);
        }
        return outRecord;
    }

    /**
     * save Address Detail Info
     *
     * @param inputRecord
     */
    public Record updateAddressDetailInfo(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailInfo", new Object[]{inputRecord});
        }

        // Todo - remove below get pk from inputRecord later
        String pk = CIFields.getPk(inputRecord);
        if (!StringUtils.isBlank(pk, true)) {
            AddressFields.setAddressId(inputRecord, pk);
        }

        DataRecordMapping dataRecordMapping = new DataRecordMapping();
        dataRecordMapping.addFieldMapping(new DataRecordFieldMapping("expiredAddressFK", "expiringAddressId"));

        Record recResult = getAddressDAO().updateAddressDetailInfo(inputRecord, dataRecordMapping);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAddressDetailInfo", recResult);
        }
        return recResult;
    }

    @Override
    public Record updateAddressDetailInfoForWS(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailInfoForWS", new Object[]{inputRecord});
        }

        // Todo - remove below get pk from inputRecord later
        String pk = CIFields.getPk(inputRecord);
        if (!StringUtils.isBlank(pk, true)) {
            AddressFields.setAddressId(inputRecord, pk);
        }
        Record recResult = getAddressDAO().updateAddressDetailInfoForWS(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "updateAddressDetailInfoForWS", recResult);
        }
        return recResult;
    }

    /**
     * Bulk modify address.
     *
     * @param inputRecord
     */
    public void updateAddressDetailForBulkModify(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressDetailForBulkModify", new Object[]{inputRecord});
        }

        String bulkModifyAddressId = AddressFields.getBulkModifyAddressId(inputRecord);
        String[] addressIds = bulkModifyAddressId.split(",");

        RecordSet rs = new RecordSet();
        for (String currentId:addressIds) {
            if (!StringUtils.isBlank(currentId)) {
                Record record = new Record();
                AddressFields.setAddressId(record, currentId);
                rs.addRecord(record);
            }
        }

        if (rs.getSize() > 0) {
            rs.setFieldsOnAll(inputRecord, false);
            getAddressDAO().updateAddressDetailForBulkModify(rs);
        }

        l.exiting(getClass().getName(), "updateAddressDetailForBulkModify");
    }

    @Override
    public void saveAddressDetail(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveAddressDetail", new Object[]{inputRecord});
        }

        handleCountryFields(inputRecord, true);
        checkCountyCode(inputRecord);

        String bulkModifyAddressId = inputRecord.getStringValueDefaultEmpty("bulkModifyAddressId");
        if (!StringUtils.isBlank(bulkModifyAddressId)) {
            updateAddressDetailForBulkModify(inputRecord);
        } else {
            updateAddressDetailInfo(inputRecord);
        }

        l.exiting(getClass().getName(), "saveAddressDetail");
    }

    private void handleCountryFields(Record inputRecord, boolean isSaving) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "handleCountryFields", new Object[]{inputRecord, isSaving});
        }

        String countryCode = inputRecord.getStringValue("countryCode");
        String configuredUsaCountryCode = SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_USA, "USA");

        if (countryCode.equalsIgnoreCase(configuredUsaCountryCode)) {
            inputRecord.setFieldValue("usaAddressB", "Y");
        } else {
            inputRecord.setFieldValue("usaAddressB", "N");

            // if selected country is not configured in system parameter, we submit a otherProvince field instead of province
            // we need to set province from otherProvince in this case
            if (!isCountryCodeConfigured(countryCode)) {
                if (isSaving) {
                    // we are saving data into database, set value to province from otherProvince
                    inputRecord.setFieldValue("province", inputRecord.getStringValueDefaultEmpty("otherProvince"));
                } else {
                    // we are displaying data from database, set value to otherProvince from province
                    inputRecord.setFieldValue("otherProvince", inputRecord.getStringValueDefaultEmpty("province"));
                }
            } else {
                if (!isSaving) {
                    inputRecord.setFieldValue("otherProvince", "");
                }
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

    @Override
    public Record loadAddressForExpire(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadAddressForExpire", new Object[]{inputRecord});
        }

        Record outRecord = getAddressDAO().loadAddressForExpire(inputRecord);

        // set default effective to date
        Record defaultValueRecord = getFieldDefaultValues(inputRecord);
        if (defaultValueRecord.hasStringValue("effectiveToDate")) {
            outRecord.setFieldValue("effectiveToDate", defaultValueRecord.getStringValue("effectiveToDate"));
        } else {
            outRecord.setFieldValue("effectiveToDate", FormatUtils.formatDateForDisplay(Calendar.getInstance().getTime()));
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadAddressForExpire", outRecord);
        }
        return outRecord;
    }

    @Override
    public void expireNonPrimaryAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireNonPrimaryAddress", new Object[]{inputRecord});
        }

        getAddressDAO().expireNonPrimaryAddress(inputRecord);

        l.exiting(getClass().getName(), "expireNonPrimaryAddress");
    }

    @Override
    public void expireNonPrimaryAddressForWS(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "expireNonPrimaryAddressForWS", new Object[]{inputRecord});
        }

        getAddressDAO().expireNonPrimaryAddressForWS(inputRecord);

        l.exiting(getClass().getName(), "expireNonPrimaryAddressForWS");
    }

    /**
     * Load entity vendor address
     * @param inputRecord
     * @return
     */
    @Override
    public Record loadVendorAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadVendorAddress", new Object[]{inputRecord});
        }

        Record vendorAddressRecord = getAddressDAO().loadVendorAddress(inputRecord);
        if (vendorAddressRecord == null) {
            vendorAddressRecord = getFieldDefaultValues(inputRecord);
            vendorAddressRecord.setFieldValue(AddressFields.SOURCE_RECORD_ID, inputRecord.getStringValueDefaultEmpty(AddressFields.ENTITY_ID));
            vendorAddressRecord.setFieldValue(AddressFields.SOURCE_TABLE_NAME, AddressFields.ENTITY);

            // set vendor address type code as default
            Record vendorAddress = loadVendorAddressTypeInfo();
            String vendorAddressTypeCode = vendorAddress.getStringValueDefaultEmpty(AddressFields.CODE);
            vendorAddressRecord.setFieldValue(AddressFields.ADDRESS_TYPE_CODE, vendorAddressTypeCode);
        } else {
            handleCountryFields(vendorAddressRecord, false);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadVendorAddress", vendorAddressRecord);
        }
        return vendorAddressRecord;
    }

    /**
     * save vendor address detail
     * @param inputRecord
     */
    @Override
    public Record saveVendorAddress(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "saveVendorAddress", new Object[]{inputRecord});
        }

        handleCountryFields(inputRecord, true);
        checkCountyCode(inputRecord);

        Record outRecord = getAddressDAO().saveVendorAddress(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "saveVendorAddress", outRecord);
        }
        return outRecord;
    }

    @Override
    public RecordSet loadEntityRelation(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEntityRelation", new Object[]{inputRecord});
        }

        RecordSet outRs = getAddressDAO().loadEntityRelation(inputRecord, AddSelectIndLoadProcessor.getInstance());

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEntityRelation", outRs);
        }
        return outRs;
    }

    @Override
    public void performAddressCopy(RecordSet inputRs) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performAddressCopy", new Object[]{inputRs});
        }

        getAddressDAO().performAddressCopy(inputRs);

        l.exiting(getClass().getName(), "performAddressCopy");
    }

    public void checkCountyCode(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "checkCountyCode", new Object[]{inputRecord});
        }

        boolean usaAddressB = YesNoFlag.getInstance(inputRecord.getStringValue("usaAddressB", "")).booleanValue();
        boolean checkPremiseAddressValid = YesNoFlag.getInstance(SysParmProvider.getInstance().getSysParm("CI_CHK_PREMISE_ADDR", "Y")).booleanValue();
        String addressType = inputRecord.getStringValue("addressTypeCode", "");

        // only do validation if it is usa address, and system parameter CI_CHK_PREMISE_ADDR turns on, and address type is PREMISE
        if (usaAddressB && checkPremiseAddressValid && "PREMISE".equalsIgnoreCase(addressType)) {
            String countyCode = inputRecord.getStringValue("countyCode", "");
            if (StringUtils.isBlank(countyCode, true)) {
                MessageManager.getInstance().addErrorMessage("error.usaaddress.addresstypepremise.county");
                throw new ValidationException("Invalid Address Save.");
            } else {
                // validate county code by supplied zip code, or combination of city and state
                Record outRecord = getAddressDAO().loadCountyCode(inputRecord);
                String returnCountyCode = outRecord.getStringValue("countyCode", "");
                if (StringUtils.isBlank(returnCountyCode, true)) {
                    MessageManager.getInstance().addErrorMessage("error.usaaddress.addresstypepremise");
                    throw new ValidationException("Invalid Address Save.");
                }
            }
        }

        l.exiting(getClass().getName(), "checkCountyCode");
    }

    @Override
    public RecordSet loadChangeAddressRoles(Record inputRecord, RecordLoadProcessor loadProcessor) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadChangeAddressRoles", new Object[]{inputRecord, loadProcessor});
        }

        RecordSet outRs = getAddressDAO().loadChangeAddressRoles(inputRecord, loadProcessor);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadChangeAddressRoles", outRs);
        }
        return outRs;
    }

    @Override
    public void performTransferAddressRoles(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "performTransferAddressRoles", new Object[]{inputRecord});
        }

        getAddressDAO().performTransferAddressRoles(inputRecord);

        l.exiting(getClass().getName(), "performTransferAddressRoles");
    }

    @Override
    public void updateAddressRoles(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "updateAddressRoles", new Object[]{inputRecord});
        }

        RecordSet inputRecords = new RecordSet();
        String[] arrEntityRoleIdList = inputRecord.getStringValue("entityRoleIdList").split(",");
        for (int i = 0; i < arrEntityRoleIdList.length; i++) {
            inputRecord.setFieldValue("entityRoleId", arrEntityRoleIdList[i]);

            Record tempRecord = new Record();
            tempRecord.setFields(inputRecord);
            inputRecords.addRecord(tempRecord);
        }

        if (inputRecords.getSize() > 0) {
            getAddressDAO().updateAddressRoles(inputRecords);
        }

        l.exiting(getClass().getName(), "updateAddressRoles");
    }

    @Override
    public RecordSet loadEffectAddressList(Record inputRecord) {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "loadEffectAddressList", new Object[]{inputRecord});
        }

        RecordSet outRs = getAddressDAO().loadEffectAddressList(inputRecord);

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "loadEffectAddressList", outRs);
        }
        return outRs;
    }

    public void verifyConfig() {
        if (getAddressDAO() == null) {
            throw new ConfigurationException("The required property 'addressDAO' is missing.");
        }
        if (getDbUtilityManager() == null) {
            throw new ConfigurationException("The required property 'dbUtilityManager' is missing.");
        }

        if (getWorkbenchConfiguration() == null) {
            throw new ConfigurationException("The required property 'workbenchConfiguration' is missing.");
        }
    }

    public WorkbenchConfiguration getWorkbenchConfiguration() {
        return m_workbenchConfiguration;
    }

    public void setWorkbenchConfiguration(WorkbenchConfiguration workbenchConfiguration) {
        this.m_workbenchConfiguration = workbenchConfiguration;
    }

    public DBUtilityManager getDbUtilityManager() {
        return m_dbUtilityManager;
    }

    public void setDbUtilityManager(DBUtilityManager dbUtilityManager) {
        m_dbUtilityManager = dbUtilityManager;
    }

    public AddressDAO getAddressDAO() {
        return m_addressDAO;
    }

    public void setAddressDAO(AddressDAO addressDAO) {
        this.m_addressDAO = addressDAO;
    }

    private AddressDAO m_addressDAO;
    private DBUtilityManager m_dbUtilityManager;
    private WorkbenchConfiguration m_workbenchConfiguration;
}
