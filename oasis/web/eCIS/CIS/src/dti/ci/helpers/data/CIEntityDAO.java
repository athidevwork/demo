package dti.ci.helpers.data;

import dti.ci.helpers.ICIConstants;
import dti.ci.helpers.ICIEntityConstants;
import dti.ci.helpers.ICIPhoneNumberConstants;
import dti.ci.helpers.ICIVendorConstants;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>Data Access Object for Entity.</p>
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author Gerald C. Carney
 *         Date:   Dec 4, 2003
 */

/**
 * Revision Date    Revised By  Description
 * --------------------------------------------------------------------
 * 04/01/2005       HXY         Removed singleton implementation. Used
 * PreparedStatement instead of Statement.
 * 04/14/2005       HXY         Moved commit logic back to BO.
 * 02/24/2006       HXY         issue 56236 - premise address type county_code not null
 * added getCountyCode.
 * 08/02/2006     ligj        Add loss WEB_ADDRESS1 WEB_ADDRESS2 WEB_ADDRESS3.
 * 02/16/2007       kshen       Added dba name. (iss68160)
 * 07/02/2007       FWCH      Added method updateEntityType
 * 08/17/2007       FWCH      Update tax_info_effective_date
 * 12/14/2007       Leo        Issue 78009
 * 03/02/2009       Leo         Issue 87902
 * 09/08/2009       Kenney     Modified for issue 97135
 * 10/16/2009       hxk        Added userHasExpertWitnessClass function for issue 97591.
 * 10/26/2011       hxk        Issue 125683 - Add component field.
 * 04/03/2013      kshen       Issue 141547
 * 04/15/2013       bzhu       Issue 139501
 * 08/30/2013       kshen      Issue 143051.
 * 04/02/2014       jld        Issue 153427.
 * 07/27/2015       ylu        Issue 164527: add CHAR4/CHAR5 for display and updating
 * 02/22/2016       Elvin      Issue 167867: include minor_b column when updating entity
 * 05/24/2016       Elvin      Issue 176524: add external_data_id
 * 05/31/2016       hxk        Issue 168173: Replace hard coded SQL w/ call to piped function
 *                             to return entity data.
 * --------------------------------------------------------------------
 */

public class CIEntityDAO extends CIBaseDAO implements ICIConstants, ICIEntityConstants,
        ICIPhoneNumberConstants, ICIVendorConstants, ICIPKDAO {

    public DisconnectedResultSet retrieveDataResultSet(Connection conn, String pk) throws Exception {
        DisconnectedResultSet rs = null;
        return rs;
    }

    /**
     * Retrieves a HashMap with data for a specified entity.
     *
     * @param conn JDBC Connection object.
     * @param pk   Entity PK.
     * @return Map - Data for the entity.
     * @throws Exception
     */
    public Map retrieveDataMap(Connection conn, String pk) throws Exception {
        Map retMap = new HashMap();
        String methodName = "retrieveDataMap";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(), methodName,
                new Object[]{conn, pk});
        pk = this.checkPK(pk);
        long pkLong = -1;
        pkLong = Long.parseLong(pk);
        StringBuffer strbufSqlStatement = new StringBuffer();
        strbufSqlStatement.append("select * from table(ci_web_entity_h.get_entity_data(?)) where rownum <= 1");

        lggr.fine("SQL statement = " + strbufSqlStatement.toString());

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(strbufSqlStatement.toString());
            stmt.setLong(1, pkLong);
            rs = stmt.executeQuery();
            retMap = this.entityResultSetToMap(rs);
        } catch (Exception e) {
            try {
                lggr.throwing(this.getClass().getName(), methodName, e);
                String exceptMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "exception occurred updating entity:  " +
                        e.toString();
//        System.out.println(exceptMsg);
//        lggr.info(exceptMsg);
                exceptMsg = "Class " + this.getClass().getName() + ",  " +
                        methodDesc + ":  " +
                        "SQL statement:  " +
                        strbufSqlStatement.toString();
//        System.out.println(exceptMsg);
                lggr.info(exceptMsg);
            } catch (Exception ignore) {
            }
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ignore) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ignore) {
                }
            }
        }
        lggr.exiting(this.getClass().getName(), methodName, retMap);
        return retMap;
    }

    /**
     * Gets update SQL for adding a new entity.
     *
     * @return String - The insert proc call.
     */
    public String getUpdateSql() {
        return
                "BEGIN " +
                        "wb_client_utility.create_new_entity(  " +
                        "is_entity_data            => ?, " +
                        "is_phn_num_not_rltd_to_addr => ?, " +
                        "is_ok_to_skip_dup_ents    => ?, " +
                        "is_ok_to_skip_dup_tax_ids => ?, " +
                        "is_include_tax_id_dup_xml_doc => ?, " +
                        "os_entity_created         => ?, " +
                        "on_new_entity_pk          => ?, " +
                        "on_entity_dup_count       => ?, " +
                        "os_entity_dup_xml_doc     => ?, " +
                        "on_tax_id_dup_count       => ?, " +
                        "os_tax_id_dup_xml_doc     => ?, " +
                        "on_merged_dup_count       => ?, " +
                        "os_merged_dup_xml_doc     => ?, " +
                        "os_user_can_dup_tax_id    => ?, " +
                        "os_dup_tax_id_sysparm     => ? " +
                        "); " +
                        "END; ";
    }

    /**
     * Converts a result set with one row of entity data into a HashMap.
     *
     * @param rs Result set with one row of entity data.
     * @return Map with the entity data.
     */
    public Map entityResultSetToMap(ResultSet rs) {

        String methodName = "entityResultSetToMap";
        String methodDesc = "method " + methodName;
        Logger lggr = LogUtils.enterLog(this.getClass(),
                methodName, rs);

        HashMap retMap = new HashMap();
        String entType = "";

// Fields common to persons and organizations.
        int numCommonFlds = 56;
        String[] fldName = new String[numCommonFlds];

        fldName[0] = CLIENT_ID_ID;
        fldName[1] = ENTITY_TYPE_ID;
        fldName[2] = ENTITY_TYPE_DESC_COMPUTED_ID;
        fldName[3] = VERY_LONG_NAME_ID;
        fldName[4] = SSN_ID;
        fldName[5] = SSN_VERIFIED_B_ID;
        fldName[6] = FED_TAX_ID_ID;
        fldName[7] = FED_TAX_ID_VERIFIED_B_ID;
        fldName[8] = DEFAULT_TAX_ID_ID;
        fldName[9] = TAX_INFO_EFF_DATE_ID;
        fldName[10] = VIP_B_ID;
        fldName[11] = DATE_OF_BIRTH_ID;
        fldName[12] = DISCARDED_B_ID;
        fldName[13] = EMAIL_ADDRESS_1_ID;
        fldName[14] = EMAIL_ADDRESS_2_ID;
        fldName[15] = EMAIL_ADDRESS_3_ID;
        fldName[16] = LEGACY_DATA_ID_ID;
        fldName[17] = CHAR_1_ID;
        fldName[18] = CHAR_2_ID;
        fldName[19] = CHAR_3_ID;
        fldName[20] = NUM_1_ID;
        fldName[21] = NUM_2_ID;
        fldName[22] = NUM_3_ID;
        fldName[23] = DATE_1_ID;
        fldName[24] = DATE_2_ID;
        fldName[25] = DATE_3_ID;
        fldName[26] = SIC_CODE_ID;
        fldName[27] = SPECIAL_HANDLING_ID;
        fldName[28] = LOSS_FREE_DATE_ID;
        fldName[29] = CLAIMS_FREE_DATE_ID;
        fldName[30] = INSURED_SINCE_DATE_ID;
        fldName[31] = ENTITY_NAME_COMPUTED_ID;
        fldName[32] = VND_IRS_1099_TYPE_ID;
        fldName[33] = ENTITY_WEB_ADDRESS_1_ID;
        fldName[34] = ENTITY_WEB_ADDRESS_2_ID;
        fldName[35] = ENTITY_WEB_ADDRESS_3_ID;
        fldName[36] = ENTITY_DBA_NAME_ID;
        fldName[37] = ENTITY_LICENSE;
        fldName[38] = ENTITY_LICENSE_STATE;
        fldName[39] = VND_CODE;
        fldName[40] = MOREOB_START_DATE;
        fldName[41] = MOREOB_OFF_DATE;
        fldName[42] = MOREOB_MAINTENANCE_START;
        fldName[43] = MOREOB_GROUP_CODE;
        fldName[44] = ENTITY_HICN_ID;
        fldName[45] = ENTITY_LEGAL_NAME_ID;
        fldName[46] = ENTITY_REFERENCE_NUMBER;
        fldName[47] = ENTITY_COMPONENT;
        fldName[48] = ENTITY_ELECTRONIC_DISTRB_B;
        fldName[49] = CI_ADDL_INFO1;
        fldName[50] = CI_ADDL_INFO2;
        fldName[51] = CI_ADDL_INFO3;
        fldName[52] = ENTITY_LEGAL_NAME_EFF_DT_ID;
        fldName[53] = CHAR_4_ID;
        fldName[54] = CHAR_5_ID;
        fldName[55] = ENTITY_EXTERNAL_DATAID;
// Fields for persons.
        int numPersonFlds = 13;
        String[] personFldName = new String[numPersonFlds];

        personFldName[0] = GENDER_ID;
        personFldName[1] = FIRST_NAME_ID;
        personFldName[2] = MIDDLE_NAME_ID;
        personFldName[3] = LAST_NAME_ID;
        personFldName[4] = PREFIX_NAME_ID;
        personFldName[5] = SUFFIX_NAME_ID;
        personFldName[6] = TITLE_ID;
        personFldName[7] = MARITAL_STATUS_ID;
        personFldName[8] = DATE_OF_DEATH_ID;
        personFldName[9] = DECEASED_B_ID;
        personFldName[10] = MINOR_B_ID;
        personFldName[11] = MINOR_B_COMPUTED_ID;
        personFldName[12] = PROFESSIONAL_DESIGNATION_ID;

// Fields for organizations.
        int numOrgFlds = 1;
        String[] orgFldName = new String[numOrgFlds];

        orgFldName[0] = ORG_NAME_ID;

        try {
            if (rs != null && rs.next()) {
                try {
                    entType = rs.getString(ENTITY_TYPE_ID);
                } catch (Exception e) {
                    lggr.fine("Class " + this.getClass().getName() +
                            methodDesc + ":  exception getting " + ENTITY_TYPE_ID +
                            " from map:  " + e.toString());
                }
                if (StringUtils.isBlank(entType)) {
                    entType = "";
                }

// Loop through the common fields.
                for (int i = 0; i < numCommonFlds; i++) {
                    try {
                        retMap.put(fldName[i], rs.getString(fldName[i]));
                    } catch (Exception e) {
                        lggr.fine("Class " + this.getClass().getName() +
                                methodDesc + ":  exception getting field " + fldName[i] +
                                " from map:  " + e.toString());
                    }
                }
// entity_entityType in the hashMap is never null.
                if (entType.equals("")) {
                    retMap.put(fldName[1], entType);
                }

                if (entType.charAt(0) == 'P') {
// Loop through the person fields.
                    for (int i = 0; i < numPersonFlds; i++) {
                        try {
                            if (SUFFIX_NAME_ID.equals(personFldName[i]) ||
                                    PROFESSIONAL_DESIGNATION_ID.equals(personFldName[i])) {
                                if (null == rs.getString(personFldName[i]))
                                    retMap.put(personFldName[i], new String[]{"-1"});
                                else
                                    retMap.put(personFldName[i], rs.getString(personFldName[i]).split(","));
                            } else
                                retMap.put(personFldName[i], rs.getString(personFldName[i]));
                        } catch (Exception e) {
                            lggr.fine("Class " + this.getClass().getName() +
                                    methodDesc + ":  exception getting person field " +
                                    personFldName[i] + " from map:  " + e.toString());
                        }
                    }
                } else if (entType.charAt(0) == 'O') {
// Loop through the organization fields.
                    for (int i = 0; i < numOrgFlds; i++) {
                        try {
                            retMap.put(orgFldName[i], rs.getString(orgFldName[i]));
                        } catch (Exception e) {
                            lggr.fine("Class " + this.getClass().getName() +
                                    methodDesc + ":  exception getting org field " +
                                    orgFldName[i] + " from map:  " + e.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            lggr.fine("Class " + this.getClass().getName() +
                    methodDesc + ":  exception occurred:  " +
                    e.toString());
        }

        lggr.exiting(this.getClass().getName(), methodName);
        return retMap;
    }

}