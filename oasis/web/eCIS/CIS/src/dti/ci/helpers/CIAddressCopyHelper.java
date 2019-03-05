package dti.ci.helpers;

import dti.ci.helpers.data.CIAddressCopyDAO;
import dti.ci.helpers.data.DAOFactory;
import dti.ci.helpers.data.DAOInstantiationException;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Business Object for AddressCopypondence
 * <p/>
 * <p>(C) 2007 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 06, 2007
 *
 * @author bhong
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 *
 * ---------------------------------------------------
*/

public class CIAddressCopyHelper extends CIHelper {
    private CIAddressCopyDAO dao;

    /**
     * getDAO
     *
     * @return CIAddressCopyDAO
     * @throws dti.ci.helpers.data.DAOInstantiationException
     *
     */
    protected CIAddressCopyDAO getDAO() throws DAOInstantiationException {
        String methodName = "getDAO";
        Logger lggr = LogUtils.enterLog(getClass(), methodName);

        if (dao == null) {
            dao = (CIAddressCopyDAO) DAOFactory.getDAOFactory().getDAO("CIAddressCopyDAO");
        }

        lggr.exiting(getClass().getName(), methodName, dao);

        return dao;
    }

    /**
     * Gets relations list for address copy
     *
     * @param conn
     * @param entityFK
     * @return DisconnectedResultSet
     * @throws Exception
     */
    public DisconnectedResultSet getRelationList(Connection conn, long entityFK)
            throws Exception {
        return getDAO().getRelationList(conn, entityFK);
    }

    /**
     * copyAddress
     *
     * @param conn
     * @param data
     * @param addressPK
     * @throws Exception
     */
    public void copyAddress(Connection conn, String data, long addressPK)
            throws Exception {
        String methodName = "copyAddress";
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, data, new Long(addressPK)});
        boolean auto = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            getDAO().copyAddress(conn, data, addressPK);
            conn.commit();
            lggr.exiting(this.getClass().getName(), methodName);
        } finally {
            conn.setAutoCommit(auto);
        }
    }
}