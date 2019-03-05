package dti.ci.entitysearch.dao;

import dti.ci.core.dao.BaseDAO;
import dti.ci.helpers.ICIConstants;
import dti.oasis.data.StoredProcedureDAO;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordLoadProcessor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.LogUtils;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Data Access Object for Entity List.</p>
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author Gerald C. Carney
 * Date:   Nov 24, 2003
 *
 * Revision Date    Revised By  Description
 * -----------------------------------------------------------------
 * 03/30/2005       HXY         Removed singleton implementation. Used
 *                              PreparedStatement instead of Statement.
 * 01/19/2007       kshen       Modify the query logic about firstName
 *                              and lastOrgName
 * 05/10/2007       kshen       If the firstName field is not empty,
 *                              do not serach entity aganist org.
 * 05/18/2007       kshen       Added search cirteria about sub-class
 * 11/01/2007       FWCH        Added entity_pk criterion for issue 76512
 * 11/30/2007       kshen       Added phoneNum and phone num type in select SQL.
 * 10/08/2007       Leo         Add for issue 86040.
 * 2/10/2009        Guang       clean code, added additionalSearchSql for retrieveEntityList
 * 03/13/2009       Fred        Added column very_long_name
 * 04/07/2009       Kenney      Modify for issue 90855
 * 04/23/2009       Fred        Added country code criteria
 * 05/04/2009       Jacky       issue #92802
 * 05/14/2009       kshen       Added column Classification, Sub Class, Sub Type in entity list.
 * 07/06/2009       hxk         Change order by to Match C/S
 *                              A column was added that change column number sorting.
 * 08/27/2009       hxk         Add distinct to SQL to match C/S.
 * 09/03/2009       kshen       Added hidden search criteria FLD_ENT_REL_ENT_PK.
 * 07/13/2010       shchen      Provide search on CIS relationships and entitytype for issue 106849.
 * 11/02/2010       Michael Li  issue 113623
 * 01/19/2011       syang       105832 - Added the new column "ddlStatus" for discipline decline list.
 * 10/28/2011       wfu         126603 - Added Future DDL status.
 * 11/30/2011       Leo         Issue 117873.
 * 12/15/2011       kshen       Issue 126190.
 * 12/20/2011       Leo         Issue 128542.
 * 04/05/2012       kshen       Issue 131959
 * 10/10/2012       bzhu        Issue 136732.
 * 04/03/2013       ldong       issue 142971.
 * 01/29/2014       issue       150529 - for the given classCode, do accurate search, instead of "exists" search
 * 02/25/2014       issue       150416 - add prefix of "0" to client Id when CS_CLIENT_ID_FORMAT = 000000000#
 * 06/10/2014       ylu         150037 - For Entity Select List page, in order to make grid's column order as same as
 *                              their C/S, re-arrange the position of field legacy_data_id , to match the changed
 *                              CIEntitySelectListGrid.xml, be sure this change do not affect other pages,
 * 10/23/2014       Elvin       Issue 158162: dynamic join entity_role when retrieving by role type code
 *                                      or role external id, and remove upper on external id to improve performance.
 *                                      replace with REGEXP_LIKE to be case insensitive.
 * 06/26/2015       ylu         Issue 163296: performance tuning for Invoicer form field, according to 136732's logic
 * 07/27/2015       ylu         ISsue 164327: add CHAR4/CHAR5 for display
 * 07/31/2015       dpang       Issue 164777: Changed order by to do case-insensitive sort
 * 09/29/2015       ylu         Issue 166124: re-arrange the unused addl.Field grid column
 * 12/03/2015       ylu         Issue 165742: support phone number wild search
 * 12/16/2015       Elvin       Issue 164310: change DBA history search behavior base on parameter CIS_DBA_HIS_SEARCH
 * 12/16/2015       Elvin       Issue 169050: Query all 3 email address columns when searching by email.
 * 05/16/2016       jld         Issue 161565. Remove the split for last_name, first_name. Is now handled in SQL.
 * 05/18/2016       dpang       Issue 161565. Remove extra parenthesis.
 * 05/24/2016       Elvin       Issue 176524: add external_data_id
 * 05/31/2016       hxk         Issue 168173: Replace hard coded sql w/ select from piped function.
 * 07/04/2016       dpang       Issue 177795: Change sqlType to Types.DATE for date params.
 * 12/06/2016       ddai        Issue 180360: Add "0" before it to make the Client ID conformed to the 10-digit Client ID field format
 * 04/18/2017       ylu         Issue 184695: clear the criteria value in loop, in order not to pass entityPK value into ClientId field.
 * 09/07/2017       ylu         Issue 187991: re-pass dba name history checkbox field (which was missing since issue 168173)
 *                                            to db-end to co-operate search by dba name,
 * -----------------------------------------------------------------
 */

public class EntitySearchJdbcDAO extends BaseDAO implements EntitySearchDAO, ICIConstants {

    private final Logger l = LogUtils.getLogger(getClass());

    @Override
    public int getPolicyCnt(Record inputRecord) {
        String methodName = "getPolicyCnt";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }

        int policyCount = 0;
        StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Search_H.get_policy_cnt");

        try {
            RecordSet rs = spDao.execute(inputRecord);
            policyCount = rs.getSummaryRecord().getIntegerValue("policyCnt");
        } catch (SQLException e) {
            handleSQLException(e, CI_GENERIC_ERROR, getClass().getName(), methodName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, policyCount);
        }

        return policyCount;
    }

    @Override
    public RecordSet getEntityList(Record inputRecord, RecordLoadProcessor recordLoadProcessor) {
        String methodName = "getEntityList";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord, recordLoadProcessor});
        }
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Search_H.Sel_Entity_List");
            rs = spDao.execute(inputRecord, recordLoadProcessor);
        } catch (SQLException e) {
            handleSQLException(e, CI_GENERIC_ERROR, getClass().getName(), methodName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    @Override
    public RecordSet getEntityClaims(Record inputRecord) {
        String methodName = "getEntityClaims";
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), methodName, new Object[]{inputRecord});
        }
        RecordSet rs = null;
        try {
            StoredProcedureDAO spDao = StoredProcedureDAO.getInstance("Ci_Web_Entity_Search_H.get_entity_claims");
            rs = spDao.execute(inputRecord);
        } catch (SQLException e) {
            handleSQLException(e, CI_GENERIC_ERROR, getClass().getName(), methodName);
        }

        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), methodName, rs);
        }
        return rs;
    }

    private static final String CI_GENERIC_ERROR ="ci.generic.error";
}
