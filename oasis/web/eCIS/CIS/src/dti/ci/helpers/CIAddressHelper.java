package dti.ci.helpers;

import dti.ci.helpers.data.CIAddressDAO;
import dti.ci.helpers.data.DAOFactory;
import dti.ci.helpers.data.DAOInstantiationException;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.DisconnectedResultSet;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.ArrayList;
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
 *                                     USA Address
 *         ----------------------------------------------------------------------
 */

public class CIAddressHelper extends CIHelper implements ICIAddressConstants, Serializable {

    private CIAddressDAO DAO = null;

    /**
     * Get an instance of a CIAddressDAO.
     *
     * @return a CIAddressDAO
     * @throws DAOInstantiationException
     */
    protected CIAddressDAO getDAO() throws DAOInstantiationException {
        String methodName = "getDAO";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName);
        if (DAO == null) {
            DAO = (CIAddressDAO) DAOFactory.getDAOFactory().getDAO("CIAddressDAO");
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return DAO;
    }

    /**
     * Retrieves a HashMap containing data for a particular address.
     *
     * @param conn Connection object.
     * @param pk   Address PK.
     * @return Map with the data.
     * @throws Exception
     */
    public Map retrieveAddressDataMap(Connection conn, String pk)
            throws Exception {
        String methodName = "retrieveAddressDataMap";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, pk});
        Map dataMap = getDAO().retrieveDataMap(conn, pk);
        lggr.exiting(this.getClass().getName(), methodName, dataMap);
        return dataMap;
    }

    /**
     * Identifies if the primary address flag for an address should be read-only.
     *
     * @param newAddress     Is the address a new address.
     * @param expiredAddress Is the address an expired address.
     * @param primaryAddress Is the address the primary address.
     * @return boolean
     */
    public boolean isPrimaryAddrBReadOnly(boolean newAddress, boolean expiredAddress, boolean primaryAddress) {
        if (newAddress) {
            return true;
        } else if (expiredAddress) {
            return true;
        } else {
            if (primaryAddress) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Identifies if the post office address flag for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isPOAddrBReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if address line 1 for an address should be read-only.
     *
     * @param newAddress Is the address new address.
     * @return boolean
     */
    public boolean isAddrLine1ReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if address line 2 for an address should be read-only.
     *
     * @param newAddress Is the address new address.
     * @return boolean
     */
    public boolean isAddrLine2ReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if address line 3 for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isAddrLine3ReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if city for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isCityReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if state for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isStateReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if country for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isCountryReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if ZIP code for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isZIPReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if ZIP plus four for an address should be read-only.
     *
     * @param newAddress Is the address a new address.
     * @return boolean
     */
    public boolean isZIPPlusFourReadOnly(boolean newAddress) {
        if (newAddress) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Identifies if type for an address should be read-only.
     *
     * @param expiredAddress Is the address an expired address.
     * @return boolean
     */
    public boolean isTypeReadOnly(boolean expiredAddress) {
        if (expiredAddress) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Identifies if name for an address should be read-only.
     *
     * @param expiredAddress Is the address an expired address.
     * @return boolean
     */
    public boolean isNameReadOnly(boolean expiredAddress) {
        if (expiredAddress) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Identifies if county for an address should be read-only.
     *
     * @param expiredAddress Is the address an expired address.
     * @return boolean
     */
    public boolean isCountyReadOnly(boolean expiredAddress) {
        if (expiredAddress) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Saves address data to the database.
     *
     * @param conn             Connection object.
     * @param addressPK        The address PK;  -1 for a new address.
     * @param expiredAddressFK The PK of the address being expired;  -1 if none.
     * @param sqlOperation     INSERT or UPDATE.
     * @param addressData      The map containing the data.
     * @return saved address PK
     * @throws Exception
     */
    public String saveAddressData(Connection conn, String addressPK,
                                  String expiredAddressFK, String sqlOperation, Map addressData)
            throws Exception {
        return this.saveAddressData(conn, addressPK, expiredAddressFK, sqlOperation,
                addressData, true);
    }

    /**
     * Saves address data to the database.
     *
     * @param conn             Connection object.
     * @param addressPK        The address PK;  -1 for a new address.
     * @param expiredAddressFK The PK of the address being expired;  -1 if none.
     * @param sqlOperation     INSERT or UPDATE.
     * @param addressData      The map containing the data.
     * @param limitedUpdate    Boolean indicating if update is limited to certain columns.
     * @return saved address PK
     * @throws Exception
     */
    public String saveAddressData(Connection conn, String addressPK,
                                  String expiredAddressFK, String sqlOperation, Map addressData,
                                  boolean limitedUpdate)
            throws Exception {

        String methodName = "saveAddressData";
        String methodDesc = "Class " + this.getClass().getName() +
                ", " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, addressPK, expiredAddressFK,
                sqlOperation, addressData});
        String savedAddressPk = null;
        boolean autoCommit = conn.getAutoCommit();
        try {
            if (sqlOperation == null) {
                lggr.info(methodDesc + ":  SQL operation argument is null.");
                return null;
            } else if (addressData == null) {
                lggr.info(methodDesc + ":  Address data map argument is null.");
                return null;
            }
            conn.setAutoCommit(false);
            if (sqlOperation.equalsIgnoreCase(UPDATE_CODE)) {
                savedAddressPk = getDAO().saveUpdate(conn, addressPK, addressData, limitedUpdate);
            } else if (sqlOperation.equalsIgnoreCase(INSERT_CODE)) {
                savedAddressPk = getDAO().saveInsert(conn, expiredAddressFK, addressData);
            }
            conn.commit();
            lggr.exiting(this.getClass().getName(), methodName, savedAddressPk);
            return savedAddressPk;
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Retrieves a HashMap containing vendor address data for an entity.
     *
     * @param conn Connection object.
     * @param pk   Address PK.
     * @return Map with the data.
     * @throws Exception
     */
    public Map getVendorAddressDataMap(Connection conn, String pk)
            throws Exception {
        String methodName = "getVendorAddressDataMap";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, pk});
        Map dataMap = getDAO().retrieveVendorAddressDataMap(conn, pk);
        lggr.exiting(this.getClass().getName(), methodName, dataMap);
        return dataMap;
    }

    /**
     * Retrieves a HashMap containing vendor address type info.
     *
     * @param conn Connection object.
     * @return Map with the data.
     * @throws Exception
     */
    public Map getVendorAddressTypeInfo(Connection conn)
            throws Exception {
        String methodName = "getVendorAddressTypeInfo";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn});
        Map dataMap = getDAO().retrieveVendorAddressTypeInfo(conn);
        lggr.exiting(this.getClass().getName(), methodName, dataMap);
        return dataMap;
    }

    /**
     * Saves vendor address data to the database.
     *
     * @param conn         Connection object.
     * @param addressPK    The address PK;  -1 for a new address.
     * @param sqlOperation INSERT or UPDATE.
     * @param addressData  The map containing the data.
     * @return saved address PK
     * @throws Exception
     */
    public String saveVendorAddressData(Connection conn, String addressPK,
                                        String sqlOperation, Map addressData)
            throws Exception {

        String methodName = "saveVendorAddressData";
        String methodDesc = "Class " + this.getClass().getName() +
                ", " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, addressPK, sqlOperation, addressData});
        String savedAddressPk = null;
        boolean autoCommit = conn.getAutoCommit();
        try {
            validateVendorAddressInfo(addressData);
            if (sqlOperation == null) {
                lggr.info(methodDesc + ":  SQL operation argument is null.");
                return null;
            } else if (addressData == null) {
                lggr.info(methodDesc + ":  Address data map argument is null.");
                return null;
            }
            conn.setAutoCommit(false);
            if (sqlOperation.equalsIgnoreCase(UPDATE_CODE)) {
                savedAddressPk = getDAO().saveVendorAddressUpdate(conn, addressPK, addressData);
            } else if (sqlOperation.equalsIgnoreCase(INSERT_CODE)) {
                addressData.put(PRIMARY_ADDR_B_ID, "N");
                savedAddressPk = getDAO().saveInsert(conn, null, addressData);
            }
            conn.commit();
            lggr.exiting(this.getClass().getName(), methodName, savedAddressPk);
            return savedAddressPk;
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    /**
     * Validate vendor address info
     * address line 1, city and state are required on vendor page.
     * sourceRecordFK is long and required by DB.
     * sourceTableName is required by DB
     *
     * @param addressData contains field / value mapping.
     */
    protected void validateVendorAddressInfo(Map addressData) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validate",
                new Object[]{addressData});
        validateField(LINE_1_ID, addressData.get(LINE_1_ID), false);
        validateField(CITY_ID, addressData.get(CITY_ID), false);
        if (VALUE_FOR_YES.equals(addressData.get(USA_ADDR_B_ID))) {
            validateField(STATE_ID, addressData.get(STATE_ID), false);
        }
        validateField(SOURCE_REC_FK_PROPERTY, addressData.get(SOURCE_REC_FK_PROPERTY), true);
        validateField(SOURCE_TBL_NAME_PROPERTY, addressData.get(SOURCE_TBL_NAME_PROPERTY), false);
        l.exiting(getClass().getName(), "validate");
    }

    /**
     * Validate field.
     * (1) The field is a required field.
     * (2) Check whether the field is long if isLong is true.
     *
     * @param fieldID  fieldID to check
     * @param valueObj fieldID's value
     * @param isLong   true if the fieldID field should be long
     */
    protected void validateField(String fieldID, Object valueObj, boolean isLong) throws Exception {
        Logger l = LogUtils.enterLog(getClass(), "validateField",
                new Object[]{fieldID, valueObj, (new Boolean(isLong)).toString()});
        if (StringUtils.isBlank((String) valueObj)) {
            Exception ex = new Exception(fieldID + " is null");
            l.throwing(getClass().getName(), "validateField", ex);
            throw ex;
        }
        if (isLong) {
            if (!FormatUtils.isLong(valueObj.toString())) {
                Exception ex = new Exception(fieldID + "cannot be converted to long");
                l.throwing(getClass().getName(), "validateField", ex);
                throw ex;
            }
        }
        l.exiting(getClass().getName(), "validateField");
    }

    /**
     * Return address list on Address Search / Add page.
     *
     * @param conn                connection
     * @param addressPK           address pk
     * @param sourceRecordFK      source record FK
     * @param dummySourceRecordFK source record FK
     * @param cmClmsOnlyAddrCod   address type code to sort by
     * @return DisconnectedResultSet of address list
     * @throws Exception
     */
    public DisconnectedResultSet getSearchAddAddressList(Connection conn,
                                                         long addressPK,
                                                         long sourceRecordFK,
                                                         long dummySourceRecordFK,
                                                         String cmClmsOnlyAddrCod,
                                                         String cmSelEffAddrOnly,
                                                         String cmAddrSortList) throws Exception {
        return getDAO().getSearchAddAddressList(conn, addressPK, sourceRecordFK, dummySourceRecordFK, cmClmsOnlyAddrCod, cmSelEffAddrOnly, cmAddrSortList);
    }

    /**
     * update address list for search / add  address list grid.
     *
     * @param conn                Connection
     * @param data                XML data
     * @param sourceRecordFK      source record FK
     * @param dummySourceRecordFK source record FK
     * @throws Exception
     */
    public void updateSearchAddAddressList(Connection conn, String data, long sourceRecordFK, long dummySourceRecordFK)
            throws Exception {
        String methodName = "updateSearchAddAddressList";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, data, new Long(sourceRecordFK), new Long(dummySourceRecordFK)});

        boolean auto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            getDAO().updateSearchAddAddressList(conn, data, sourceRecordFK, dummySourceRecordFK);
            conn.commit();
            lggr.exiting(this.getClass().getName(), methodName);
        } finally {
            conn.setAutoCommit(auto);
        }
    }

    /**
     * Return a new sequence number
     *
     * @param conn connection
     * @return new sequence number
     * @throws Exception
     */
    public long getNewPK(Connection conn) throws Exception {
        return getDAO().getNewPK(conn);
    }

    /**
     * Return a new String Addressdec
     *
     * @param conn connection
     * @return new sequence number
     * @throws Exception
     */
    public String getAddressDec(Connection conn, String pk) throws Exception {
        String methodName = "getAddressDec";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{conn, pk});
        String AddressDec = getDAO().getAddressDec(conn, pk);
        lggr.exiting(this.getClass().getName(), methodName, AddressDec);
        return AddressDec;
    }

    /**
     * Return String Addressdec
     *
     * @param conn connection
     * @return Address desc
     * @throws Exception
     */
    public String[] getFirstAddress(Connection conn,
                                  long addressPK,
                                  long sourceRecordFK,
                                  long dummySourceRecordFK,
                                  String cmClmsOnlyAddrCod,
                                  String cmSelEffAddrOnly,
                                  String cmAddrSortList) throws Exception {
        String methodName = "getFirstAddress";
        Logger lggr = LogUtils.enterLog(this.getClass(),
            methodName, new Object[]{conn, cmSelEffAddrOnly});
        String AddressDec[] = new String[2];
        DisconnectedResultSet drs = this.getSearchAddAddressList( conn,
                                   addressPK,
                                   sourceRecordFK,
                                   dummySourceRecordFK,
                                   cmClmsOnlyAddrCod,
                                   cmSelEffAddrOnly,
                                   cmAddrSortList);
        if (drs.next()){
            long addPK = drs.getLong("addressPK");
            AddressDec[0] = String.valueOf(addPK);
            AddressDec[1] = drs.getString("concatenatedAddress");
        }

        lggr.exiting(this.getClass().getName(), methodName, AddressDec);
        return AddressDec;
    }
}

