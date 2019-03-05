package dti.ci.vendormgr.dao;

import dti.ci.vendormgr.impl.VendorManagerImpl;
import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIVendorConstants;
import dti.ci.helpers.data.CIBaseDAO;
import dti.ci.helpers.data.ICIPKDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LocaleUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.Querier;
import dti.oasis.util.QueryParm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>Data Access Object for Vendor.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Apr 14, 2004
 *         <p/>
 *         Revision Date    Revised By  Description
 *         -------------------------------------------------------------------
 *         04/01/2005        HXY        Removed singleton implementation.
 *         Changed Statement to PreparedStatement.
 *         04/14/2005        HXY        Moved commit logic back to BO.
 *         04/28/2009        Fred       Merged payment info into a single SQL
 *         10/11/2010        tzhao      Modified sqlStatement in the retrieveVendorPaymentTotals method to support multiple currency.
 *         04/03/2018        JLD        Issue 109176. Refactor Vendor.
 *         --------------------------------------------------------------------
 */

public interface VendorDAO {

    /**
     * Get the Vendor data of an entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadVendor(Record inputRecord);

    /**
     * Get the Vendor Address data of an entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadVendorAddress(Record inputRecord);


    /**
     * Get the Vendor Payment data of an entity.
     *
     * @param inputRecord
     * @return
     */
    RecordSet loadVendorPayment(Record inputRecord);

    /**
     * Save Vendor data of an entity.
     *
     * @param inputRecord
     * @return Record
     */
    Record saveVendor(Record inputRecord);

}
