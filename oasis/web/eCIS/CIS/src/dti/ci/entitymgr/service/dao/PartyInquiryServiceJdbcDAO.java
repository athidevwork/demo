package dti.ci.entitymgr.service.dao;


import com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType;
import dti.oasis.app.AppException;
import dti.oasis.error.ExceptionHelper;
import dti.oasis.ows.util.FilterView;
import dti.oasis.util.DatabaseUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import org.springframework.jdbc.datasource.ConnectionProxy;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/2/12
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 06/10/2013       ldong       issue 145682
 * 04/25/2016       Elvin       Issue 149588: add cis hub logic
 * 05/24/2016       Elvin       Issue 176524: add external_data_id
 * 05/23/2017       kshen       Issue 184568. Changed back to return no elements for no results.
 * 06/15/2017       ddai        Add options "i" to Regexp_like to support cse insensitive match.
 * 08/15/2017       ylu         Issue 181989: support large policyInquiry
 * 01/31/2018       Elvin       Issue 190210: support load party by classification code
 * ---------------------------------------------------
 */
public class PartyInquiryServiceJdbcDAO implements PartyInquiryServiceDAO {
    private final Logger l = LogUtils.getLogger(getClass());

    private String parm1 = "parm1";
    private String parm2 = "parm2";
    private String parm3 = "parm3";
    private String parm4 = "parm4";
    private String parm5 = "parm5";
    private String parm6 = "parm6";
    private String parm7 = "parm7";
    private String addressFilterPrm = "addressFilterPrm";
    private String personFilterPrm = "personFilterPrm";
    private String organizationFilterPrm = "organizationFilterPrm";
    private String propertyFilterPrm = "propertyFilterPrm";
    private String relationshipFilterPrm = "relationshipFilterPrm";
    private String partyAdditionalInfoFilterPrm = "partyAdditionalInfoFilterPrm";
    private String partyAdditionalXmlInfoFilterPrm = "partyAdditionalXmlInfoFilterPrm";

    private String sql1 = "select PartyInquiryResultObj(entity_pk, " +
            "DECODE(upper(entity.entity_type),'P',last_name||', '||first_name,organization_name)" +
            ", NULL" +
            ", NULL" +
            ", NULL" +
            "," + addressFilterPrm + "," +
            personFilterPrm + "," +
            organizationFilterPrm + "," +
            propertyFilterPrm + "," +
            relationshipFilterPrm + "," +
            partyAdditionalInfoFilterPrm + "," +
            partyAdditionalXmlInfoFilterPrm +
            ").getPartyInquiryResultTypeList() " +
            "from entity where 1=1" + "\n";

    private String sql2 = "select PartyInquiryResultObj(entity_pk, " +
            "DECODE(upper(entity_type),'P',last_name||', '||first_name,organization_name)" +
            ", ':SOURCESYSTEM'" +
            ", :ASOFDATE" +
            ", NULL" +
            "," + addressFilterPrm + "," +
            personFilterPrm + "," +
            organizationFilterPrm + "," +
            propertyFilterPrm + "," +
            relationshipFilterPrm + "," +
            partyAdditionalInfoFilterPrm + "," +
            partyAdditionalXmlInfoFilterPrm +
            ").getPartyInquiryResult_HType() " +
            "from entity_h where " +
            "entity_h.h_from_date <= nvl(:ASOFDATE, sysdate) and nvl(:ASOFDATE, sysdate) < entity_h.h_to_date " +
            "and entity_h.h_origin = ':SOURCESYSTEM' " + "\n";

    private String commonSubquerySql1 = "entity_pk in (SELECT entity_pk FROM entity WHERE :conditions)" + "\n";

    private String commonSubquerySql2 = "entity_pk in (SELECT entity_pk FROM entity_h WHERE :conditions)" + "\n";

    private String entityCondition = "entity_pk in ( " + parm1 + ")";

    private String cleanTempDataSql = "delete from entities_gt ";

    private String insertPKsToTempTbl = " INSERT INTO entities_gt \n" +
                                        " SELECT regexp_substr(?,'[^,]+', 1, level) \n" +
                                        "   FROM dual \n" +
                                        "CONNECT BY instr(?, ',', 1, level -1) > 0 ";

    private String entityPksInTempTbl = "entity_pk in ( select entity_pk from entities_gt)";

    private String nameCondition = "DECODE(upper(entity.entity_type),'P',last_name||', '||first_name,organization_name) in (" + parm2 + ")";

    private String nameRegexpLikeCondition = "REGEXP_LIKE(DECODE(upper(entity.entity_type),'P',last_name||', '||first_name,organization_name)," + parm6 + ", 'i')";

    private String legacyDataCondition = "legacy_data_id in (" + parm3 + ")";

    private String clientIdCondition = "client_id in ( " + parm4 + ")";

    private String externalDataIdCondition = "external_data_id in (" + parm5 + ")";

    private String classificationCodeCondition = "entity_pk in (select entity_fk from entity_class where entity_class_code in (" + parm7 + ") " +
            " and sysdate between nvl(effective_from_date, to_date('01/01/1970', 'mm/dd/yyyy')) and nvl(effective_to_date, to_date('01/01/3000', 'mm/dd/yyyy')))";


    // the following will contain mapping of Oracle types to Java types. This is deal with Hungarian notation.
    private static Map mapOracleTypesToJavaTypes = new HashMap();

    static {
        mapOracleTypesToJavaTypes.put("PARTYINQUIRYRESULTTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PartyInquiryResultType");
        mapOracleTypesToJavaTypes.put("PERSONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PersonType");
        mapOracleTypesToJavaTypes.put("ADDRESSTYPE", "dti.ci.entitymgr.impl.jdbchelpers.AddressType");
        mapOracleTypesToJavaTypes.put("ORGANIZATIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.OrganizationType");
        mapOracleTypesToJavaTypes.put("PROPERTYTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PropertyType");
        mapOracleTypesToJavaTypes.put("STATEORPROVINCECODE", "dti.ci.entitymgr.impl.jdbchelpers.StateOrProvinceCodeType");
        mapOracleTypesToJavaTypes.put("ADDRESSPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.AddressPeriodType");
        mapOracleTypesToJavaTypes.put("ADDRESSTYPECODETYPE", "dti.ci.entitymgr.impl.jdbchelpers.AddressTypeCodeType");
        mapOracleTypesToJavaTypes.put("BASICADDRESSTYPE", "dti.ci.entitymgr.impl.jdbchelpers.BasicAddressType");
        mapOracleTypesToJavaTypes.put("BASICPHONENUMBERTYPE", "dti.ci.entitymgr.impl.jdbchelpers.BasicPhoneNumberType");
        mapOracleTypesToJavaTypes.put("BUSINESSEMAILTYPE", "dti.ci.entitymgr.impl.jdbchelpers.BusinessEmailType");
        mapOracleTypesToJavaTypes.put("COUNTRYCODETYPE", "dti.ci.entitymgr.impl.jdbchelpers.CountryCodeType");
        mapOracleTypesToJavaTypes.put("ORGANIZATIONNAMETYPE", "dti.ci.entitymgr.impl.jdbchelpers.OrganizationNameType");
        mapOracleTypesToJavaTypes.put("PERSONNAMETYPE", "dti.ci.entitymgr.impl.jdbchelpers.PersonNameType");
        mapOracleTypesToJavaTypes.put("PHONETYPECODETYPE", "dti.ci.entitymgr.impl.jdbchelpers.PhoneTypeCodeType");
        mapOracleTypesToJavaTypes.put("POSTOFFICEADDRESSINDICATORTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PostOfficeAddressIndicatorType");
        mapOracleTypesToJavaTypes.put("PRIMARYINDICATORTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PrimaryIndicatorType");
        mapOracleTypesToJavaTypes.put("PROPERTYNAMETYPE", "dti.ci.entitymgr.impl.jdbchelpers.PropertyNameType");
        mapOracleTypesToJavaTypes.put("STATEORPROVINCECODETYPE", "dti.ci.entitymgr.impl.jdbchelpers.StateOrProvinceCodeType");
        mapOracleTypesToJavaTypes.put("CERTIFICATIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.CertificationType");
        mapOracleTypesToJavaTypes.put("CERTIFICATIONPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.CertificationPeriodType");
        mapOracleTypesToJavaTypes.put("CONTACTTYPE", "dti.ci.entitymgr.impl.jdbchelpers.ContactType");
        mapOracleTypesToJavaTypes.put("CTLSTATEORPROVINCECODE", "dti.ci.entitymgr.impl.jdbchelpers.CtlStateOrProvinceCodeType");
        mapOracleTypesToJavaTypes.put("EDUCATIONALINSTITUTIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.EducationalInstitutionType");
        mapOracleTypesToJavaTypes.put("EDUCATIONINFORMATIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.EducationInformationType");
        mapOracleTypesToJavaTypes.put("LICENSEPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.LicensePeriodType");
        mapOracleTypesToJavaTypes.put("LICSUSPENSIONPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.LicenseSuspensionPeriodType");
        mapOracleTypesToJavaTypes.put("PROFESSIONALLICENSETYPE", "dti.ci.entitymgr.impl.jdbchelpers.ProfessionalLicenseType");
        mapOracleTypesToJavaTypes.put("SUSPREINSTINFORMATIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.SuspReinstInformationType");
        mapOracleTypesToJavaTypes.put("PARTYNOTETYPE", "dti.ci.entitymgr.impl.jdbchelpers.PartyNoteType");
        mapOracleTypesToJavaTypes.put("RELATIONSHIPTYPE", "dti.ci.entitymgr.impl.jdbchelpers.RelationshipType");
        mapOracleTypesToJavaTypes.put("REFERREDPARTYTYPE", "dti.ci.entitymgr.impl.jdbchelpers.ReferredPartyType");
        mapOracleTypesToJavaTypes.put("RELEFFECTIVEPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.EffectivePeriodType");
        mapOracleTypesToJavaTypes.put("PARTYCLASSIFICATIONTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PartyClassificationType");
        mapOracleTypesToJavaTypes.put("PARTYCLSEFFPERIODTYPE", "dti.ci.entitymgr.impl.jdbchelpers.EffectivePeriodType");
        mapOracleTypesToJavaTypes.put("PARTYADDITIONALINFOTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PartyAdditionalInfoType");
        mapOracleTypesToJavaTypes.put("PERSONADDITIONALINFOTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PersonAdditionalInfoType");
        mapOracleTypesToJavaTypes.put("ORGANIZATIONADDITIONALINFOTYPE", "dti.ci.entitymgr.impl.jdbchelpers.OrganizationAdditionalInfoType");
        mapOracleTypesToJavaTypes.put("ADDRESSADDITIONALINFOTYPE", "dti.ci.entitymgr.impl.jdbchelpers.AddressAdditionalInfoType");
        mapOracleTypesToJavaTypes.put("PARTYADDITIONALXMLINFOTYPE", "dti.ci.entitymgr.impl.jdbchelpers.PartyAdditionalXmlInfoType");
        mapOracleTypesToJavaTypes.put("ENTITYADDITIONALXMLDATATYPE", "dti.ci.entitymgr.impl.jdbchelpers.EntityAdditionalXmlDataType");
        mapOracleTypesToJavaTypes.put("ADDRESSADDITIONALXMLDATATYPE", "dti.ci.entitymgr.impl.jdbchelpers.AddressAdditionalXmlDataType");
    }


    /**
     *
     * @param conditionList Condition context
     * Conditions:
     * conditions ::= condition | conditions or condition
     * condition ::= simpleCondition | complexCondition
     * simpleCondition ::= entityCondition | nameCondition | nameRegLikeCondition | legacyDataCondition | clientIdCondition | externalDataIdCondition
     * complexCondition ::= simpleCondition and simpleCondition | complexCondition and simpleCondition
     *
     * @param asOfDate
     * @param sourceSystem
     * @param filterStringMap
     * @return
     */
    public PartyInquiryResultType loadPartyInquiryResult(Hashtable conditionList, String asOfDate, String sourceSystem, Map<String, String> filterStringMap) {
        {
            if (l.isLoggable(Level.FINER)) {
                l.entering(getClass().getName(), "loadPartyInquiryResult", new Object[]{conditionList, asOfDate, sourceSystem, filterStringMap});
            }
            String sql = null;

            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            PartyInquiryResultType partyInquiryResult = null;

            String subQuerySql = "";
            //construct SQL
            if (!StringUtils.isBlank(sourceSystem)) {
                sql = sql2;
                sql = sql.replace(":ASOFDATE", "to_date('" + asOfDate + "', '" + DateUtils.DATE_FORMAT_PATTERN + "')");
                sql = sql.replace(":SOURCESYSTEM", sourceSystem);
                subQuerySql = commonSubquerySql2;
            } else {
                sql = sql1;
                subQuerySql = commonSubquerySql1;
            }

            try {
                String conditions = "";
                Iterator it = conditionList.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
//                    l.info("key 1:" + key);
                    Object conditionValueObject = conditionList.get(key);
                    if (conditionValueObject instanceof String) {
                        //Single parameter condition
//                        l.info("condition 1:" + (String) conditionValueObject);

                        //handle with entity PK specially
                        if ("partyNumberIdList".equalsIgnoreCase(key)) {
                            String pkValueList = (String) conditionValueObject;
                            if (!StringUtils.isBlank(pkValueList)) {
                                buildDataInTempTbl(pkValueList);
                                conditions += (StringUtils.isBlank(conditions) ? "" : "   OR ") + entityPksInTempTbl;
                            }
                        } else {
                            // other single parameter condition
                            String condition = getCondition(key, (String) conditionValueObject);
                            if (!StringUtils.isBlank(condition)) {
                                conditions += (StringUtils.isBlank(conditions) ? "" : "   OR ") + StringUtils.replace(subQuerySql, ":conditions", condition);
                            }
                        }
                    } else {
                        //Multiple parameter condition
                        ArrayList<Map<String, String>> complexConditionList = (ArrayList<Map<String, String>>) conditionValueObject;
                        for (Map<String, String> complexCondition : complexConditionList) {
                            String complexConditions = "";
                            Iterator<String> complexIt = complexCondition.keySet().iterator();
                            while(complexIt.hasNext()) {
                                key = (String) complexIt.next();
//                                l.info("key 2:" + key);
//                                l.info("condition 2:" + (String) complexCondition.get(key) );
                                String condition = getCondition(key, (String) complexCondition.get(key));
                                if (!StringUtils.isBlank(condition)) {
                                    complexConditions += (StringUtils.isBlank(complexConditions) ? "" : "  AND ") + condition;
                                }
                            }
                            if (!StringUtils.isBlank(complexConditions)) {
                                conditions += (StringUtils.isBlank(conditions) ? "" : "   OR ") + StringUtils.replace(subQuerySql, ":conditions", complexConditions);
                            }
                        }
                    }
                }

                if (!StringUtils.isBlank(conditions)) {
                    sql += "  and (" + conditions + ")";
                } else {
                    throw new AppException("PartyInquiryServiceJdbcDAO:loadPartyInquiryResult:One or more parameter is required.");
                }


                // view Name
                String addressFilter = filterStringMap.get("addressFilter");
                String personFilter = filterStringMap.get("personFilter");
                String organizationFilter = filterStringMap.get("organizationFilter");
                String propertyFilter = filterStringMap.get("propertyFilter");
                String relationshipFilter = filterStringMap.get("relationshipFilter");
                String partyAdditionalInfoFilter = filterStringMap.get("partyAdditionalInfoFilter");
                String partyAdditionalXmlInfoFilter = filterStringMap.get("partyAdditionalXmlInfoFilter");

                sql = sql.replaceAll("addressFilterPrm", (dti.oasis.util.StringUtils.isBlank(addressFilter)) ? "''" : addressFilter);
                sql = sql.replaceAll("personFilterPrm", (dti.oasis.util.StringUtils.isBlank(personFilter)) ? "''" : personFilter);
                sql = sql.replaceAll("organizationFilterPrm", (dti.oasis.util.StringUtils.isBlank(organizationFilter)) ? "''" : organizationFilter);
                sql = sql.replaceAll("propertyFilterPrm", (dti.oasis.util.StringUtils.isBlank(propertyFilter)) ? "''" : propertyFilter);
                sql = sql.replaceAll("relationshipFilterPrm", (dti.oasis.util.StringUtils.isBlank(relationshipFilter)) ? "''" : relationshipFilter);
                sql = sql.replaceAll("partyAdditionalInfoFilterPrm", (dti.oasis.util.StringUtils.isBlank(partyAdditionalInfoFilter)) ? "''" : partyAdditionalInfoFilter);
                sql = sql.replaceAll("partyAdditionalXmlInfoFilterPrm", (dti.oasis.util.StringUtils.isBlank(partyAdditionalXmlInfoFilter)) ? "''" : partyAdditionalXmlInfoFilter);
//                l.info("Generated SQL:" + sql);
//                Object ds = getReadOnlyDataSource();
//                Connection conn;
//                //Check if ths was call from PM. They are using spring DS.
//                if (ds.getClass().getName() == "org.springframework.jdbc.datasource.DelegatingDataSource") {
//                    conn = (OracleConnection) (((DelegatingDataSource) ds).getTargetDataSource()).getConnection();
//                } else {
//                    conn = (OracleConnection) ((CachedPerRequestDataSource) getReadOnlyDataSource()).getTargetDataSource().getConnection();
//                }
//                conn = ((weblogic.jdbc.extensions.WLConnection) conn).getVendorConnection();

                DataSource ds = getAppDataSource();
                conn = ds.getConnection();

                Connection oracleConnection = ((weblogic.jdbc.extensions.WLConnection)
                        ((OracleConnection) ((ConnectionProxy) conn).getTargetConnection())).getVendorConnection();

                // conn  = DriverManager.getConnection(
                //          "jdbc:oracle:thin:@10.192.4.102:2521:se11gr23",
                //          "odev20122", "odev20122");
                l.fine("PartyInquiryServiceJdbcDAO:Start executing query:" + sql);
                stmt = oracleConnection.createStatement();
                rs = stmt.executeQuery(sql);
                partyInquiryResult = traverse(rs);
            } catch (Exception e) {
                AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure in PartyInquiryServiceJdbcDAO", e);
                l.logp(Level.SEVERE, getClass().getName(), "PartyInquiryServiceJdbcDAO", ae.getMessage(), ae);
            } finally {
                DatabaseUtils.close(stmt, rs, conn);
            }

            return partyInquiryResult;
        }
    }

    private String getCondition(String key, String value) {
        String condition = "";
        if (!StringUtils.isBlank(value)) {
            if (key.equalsIgnoreCase("partyNumberIdList")) {
                condition = entityCondition.replaceAll("parm1", value);
            } else if(key.equalsIgnoreCase("fullNameList")) {
                condition = nameCondition.replaceAll("parm2", value);
            } else if(key.equalsIgnoreCase("regexpFullNameList")) {
                condition = nameRegexpLikeCondition.replaceAll("parm6", value);
            } else if(key.equalsIgnoreCase("externalReferenceIdList")) {
                condition = legacyDataCondition.replaceAll("parm3", value);
            } else if(key.equalsIgnoreCase("clientIdList")) {
                condition = clientIdCondition.replaceAll("parm4", value);
            } else if (key.equalsIgnoreCase("externalDataIdList")) {
                condition = externalDataIdCondition.replaceAll("parm5", value);
            } else if (key.equalsIgnoreCase("classificationCodeList")) {
                condition = classificationCodeCondition.replaceAll("parm7", value);
            }
        }
        return condition;
    }

    private PartyInquiryResultType traverse(ResultSet rs) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "traverse", new Object[]{rs});
        }
        ResultSet tempRs = null;
        Object rootObject = null;
        Class rootClass = null;
        ARRAY tAr;
        try {
            while (rs.next()) { // scroll through the results
                try {
                    //get top object -- this should be PartyInquiryResult
                    ARRAY resultArray = (ARRAY) rs.getArray(1);

                    Object resultElems = resultArray.getOracleArray();
                    //PartyInquiryResultType
                    if (rootObject == null) {
                        tempRs = resultArray.getResultSet();
                        while (tempRs.next()) {
                            try {
                                String key = resultArray.getBaseTypeName().substring(resultArray.getBaseTypeName().indexOf(".") + 1);
                                rootClass = Class.forName((String) mapOracleTypesToJavaTypes.get(key));
                                STRUCT struct = (STRUCT) tempRs.getObject(2);
                                rootObject =
                                        struct.toClass(rootClass);
                            } catch (Exception e) {
                                l.fine(e.getMessage());
                            }
                        }
                    }
                    //get children of PartyInquiryResultType
                    Datum[] listElems = (Datum[]) resultElems;       //List level
                    for (int i = 0; i < listElems.length; i++) {
                        Datum[] objElems = (Datum[]) ((STRUCT) listElems[i]).getOracleAttributes(); //result Obj level
                        for (int k = 1; k < objElems.length; k++) {               // object field level
                            try {
                                ARRAY tempAr = (ARRAY) objElems[k];
                                String key = tempAr.getBaseTypeName().substring(tempAr.getBaseTypeName().indexOf(".") + 1);
                                tempRs = tempAr.getResultSet();
                                Object obj = null;
                                Class clazz = null;
                                while (tempRs.next()) {
                                    clazz = Class.forName((String) mapOracleTypesToJavaTypes.get(key));
                                    STRUCT struct = (STRUCT) tempRs.getObject(2);
                                    obj =
                                            struct.toClass(clazz);    //map to corresponding class

                                    setMethod(rootObject, obj);
                                }

                            } catch (Exception e) {//do nothing.cannot cast to array"
                                l.fine(e.getMessage());
                            }
                        }
                    }

                } catch (Exception e) {
                    l.fine(e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException(AppException.UNEXPECTED_ERROR, "Failure in PartyInquiryServiceManager.traverse", e);
            l.logp(Level.SEVERE, getClass().getName(), "PartyInquiryServiceManager", ae.getMessage(), ae);
        }

        if (rootObject != null) {
            return ((dti.ci.entitymgr.impl.jdbchelpers.PartyInquiryResultType) rootObject).getJaxbPartyInquiryResultType();

        }
        return new com.delphi_tech.ows.partyinquiryservice.PartyInquiryResultType();
    }


    public void buildDataInTempTbl(String values) {
        Logger l = LogUtils.getLogger(getClass());
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "buildDataInTempTbl", new Object[]{values});
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            //clear the table firstly
            conn = getAppDataSource().getConnection();
            stmt = conn.prepareStatement(cleanTempDataSql);
            stmt.executeUpdate();
            String valuesArray[] = values.split(",");
            String pks = "";
            stmt = conn.prepareStatement(insertPKsToTempTbl);
            for (int i =0 ; i < valuesArray.length; i++) {
                pks = pks + valuesArray[i] + ",";
                if ((i + 1) % 200 == 0) {
                    //insert into table by batch
                    pks = pks.substring(0, pks.lastIndexOf(","));
                    stmt.setString(1, pks);
                    stmt.setString(2, pks);
                    stmt.executeUpdate();
                    pks = "";
                }
            }
            if (!StringUtils.isBlank(pks)) {
                //insert remaining pks
                pks = pks.substring(0, pks.lastIndexOf(","));
                stmt.setString(1, pks);
                stmt.setString(2, pks);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            AppException ae = ExceptionHelper.getInstance().handleException("Failed to execute build data", e);
            l.throwing(getClass().getName(), "buildDataInTempTbl", ae);
            throw ae;
        } finally {
            DatabaseUtils.close(stmt, conn);
        }
        if (l.isLoggable(Level.FINER)) {
            l.exiting(getClass().getName(), "buildDataInTempTbl", values);
        }
    }

    /**
     * @param rootObj
     * @param obj
     * @throws Exception
     */
    private void setMethod(Object rootObj, Object obj) throws Exception {
        Class rootClass = rootObj.getClass();
        for (int meth = 0; meth < rootClass.getDeclaredMethods().length; meth++) {
            Method method = rootClass.getDeclaredMethods()[meth];
            Class[] classAr = method.getParameterTypes();
            if (classAr.length > 0 && classAr[0].isAssignableFrom(obj.getClass())) {
                try {
                    method.invoke(rootObj, obj);
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        }
    }

    public DataSource getAppDataSource() {
        return m_appDataSource;
    }

    public void setAppDataSource(DataSource appDataSource) {
        m_appDataSource = appDataSource;
    }

    private DataSource m_appDataSource;

}