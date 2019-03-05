package dti.ci.helpers;

import dti.ci.helpers.data.CIAddressDAO;
import dti.ci.helpers.data.DAOFactory;
import dti.ci.helpers.data.DAOInstantiationException;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.SysParmProvider;

import java.io.Serializable;
import java.sql.Connection;
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
 *         ----------------------------------------------------------------
 */

public class CIAddressListHelper extends CIHelper implements ICIAddressConstants, Serializable {

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
     * Obtains from DAO the address result set for a specified source..
     *
     * @param srcFK Source record FK for addresses.
     * @param conn  JDBC Connection object.
     * @return DisconnectedResultSet with the addresses.
     * @throws Exception
     */
    public DisconnectedResultSet retrieveAddressList(String srcFK, Connection conn)
            throws Exception {
        String methodName = "retrieveAddressList";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{srcFK, conn});
        DisconnectedResultSet result = null;
        try {
            result = getDAO().retrieveDataResultSet(conn, srcFK);
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return result;
    }

    /**
     * Obtains from DAO the address result set for a specified source..
     *
     * @param srcFK Source record FK for addresses.
     * @param conn  JDBC Connection object.
     * @return DisconnectedResultSet with the addresses.
     * @throws Exception
     */
    public DisconnectedResultSet retrieveAddressListWithCountyDesc(String srcFK, Connection conn)
            throws Exception {
        String methodName = "retrieveAddressList";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{srcFK, conn});
        DisconnectedResultSet result;
        try {
            result = getDAO().retrieveDataResultSetWithCountyDesc(conn, srcFK);
        } catch (Exception e) {
            lggr.throwing(this.getClass().getName(), methodName, e);
            throw e;
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return result;
    }

    /**
     * Get Entity lock by Policy flag.
     *
     * @param srcFK Source record FK.
     * @param conn  JDBC Connection object.
     * @return String Y/N.
     * @throws Exception
     */
    public String getEntityLockFlg(String srcFK, Connection conn)
            throws Exception {
             String methodName = "getEntityLockFlg";
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, new Object[]{srcFK, conn});
        String check4LockedPolicy = SysParmProvider.getInstance().getSysParm("CS_CHK4LOCKED_POL","N");

        String entityLockFlg = "N";
        if ("Y".equalsIgnoreCase(check4LockedPolicy)) {
            lggr.finer("parameter CS_CHK4LOCKED_POL configured:"+check4LockedPolicy+", check for locked policies..");
            entityLockFlg = getDAO().entityLockFlg(srcFK, conn);
        }
        lggr.exiting(this.getClass().getName(), methodName);
        return entityLockFlg;
    }

}
