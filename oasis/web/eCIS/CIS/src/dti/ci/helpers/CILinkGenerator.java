package dti.ci.helpers;

import dti.oasis.recordset.BaseResultSetRecordSetAdaptor;
import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordSet;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.DisconnectedResultSet;
import dti.oasis.util.LogUtils;
import dti.oasis.tags.OasisFields;
import dti.oasis.tags.OasisFormField;
import dti.oasis.struts.ActionHelper;
import dti.oasis.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import java.sql.Connection;

/**
 * <p>Class for saving query results and generating hyperlink.</p>
 * <p/>
 * <p>(C) 2006 Delphi Technology, inc. (dti)</p>
 *
 * @author Fred Wang
 * Date:   Jan 4, 2007
 * <p/>
 */
/*
 * Revision Date    Revised By  Description
 * ------------------------------------------------------------------
 * 02/12/2007       FWang       Eleminate hard-code sql string
 * 05/28/2007       FWCH        Set link field display type to null
 * 11/19/2012       Elvin       Modified for issue 136888:Replace & with %2526 in entityName field.
 *
 * ------------------------------------------------------------------
*/
public class CILinkGenerator implements ICIConstants {

    /**
     * Save the entity results into session.
     * The order is entity_pk!~client_name!~entity_type...
     *
     * @param request
     * @param rs
     */
    public void saveResultsToSession(HttpServletRequest request,
                                     RecordSet rs) {
        Logger lggr = LogUtils.enterLog(CILinkGenerator.class,
                "saveResultsToSession", new Object[]{request, rs});

        if (rs == null) {
            lggr.fine("Selected result is null, method exit.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rs.getSize(); i++) {
            Record row = rs.getRecord(i);

            sb.append(row.getStringValue("entityId")).append(ENTITY_SPLIT_SIGN)
                    .append(row.getStringValue("entityType")).append(ENTITY_SPLIT_SIGN)
                    .append(row.getStringValue("clientName")).append(ENTITY_SPLIT_SIGN);

        }

        if (sb.length() > 1) {
            String entityElements = sb.substring(0, sb.length() - ENTITY_SPLIT_SIGN.length());
            request.getSession().setAttribute(ENTITY_SELECT_RESULTS, entityElements);
        }

        lggr.finest("Method saveResultsToSession exited without error.");
    }

    /**
     * Generate hyperlink then put them into request
     * @param request
     * @param entityPk
     * @param actionClass
     */
    public void generateLink(HttpServletRequest request,
                             String entityPk,
                             String actionClass) {
        Logger lggr = LogUtils.enterLog(CILinkGenerator.class,
                       "generateLink", new Object[] { request, entityPk, actionClass });
        //To judge if the page have Previous/Next navigation
        OasisFields fields = ActionHelper.getFields(request);
        OasisFormField linkField = (OasisFormField) fields.get(NAVIGATION_PAGE_CODE);
        if (linkField == null || !linkField.getIsVisible()) {
            request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
            return;
        }
        //Set the display type of link field to null
        linkField.setDisplayType(null);
        //Create the hyperlink
        String result = (String) request.getSession(false).getAttribute(ENTITY_SELECT_RESULTS);
        String reqUrl = request.getRequestURI();
        String head = "-1";
        String end = "0";
        String orgUrl = "ciEntityOrgModify.do";
        String perUrl = "ciEntityPersonModify.do";

        if (result != null ) {
            String[] splitResult = result.split(ENTITY_SPLIT_SIGN);
            int total = splitResult.length / 3;
            //Only one record, needn't navigation
            if (total <= 1) {
                request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
                return;
            }
            int count = 0;
            try {
                // Identify the current position
                for (  ; count < splitResult.length - 3; count += 3) {
                    if (entityPk.equals(splitResult[count])) {
                        break;
                    }
                 }
                StringBuffer sb = new StringBuffer();
                //At first position
                if (count == 0) {
                    sb.append("1").append(ENTITY_SPLIT_SIGN).
                      append(String.valueOf(total)).append(ENTITY_SPLIT_SIGN).
                      append(head).append(ENTITY_SPLIT_SIGN).
                      append(splitResult[count]).append(ENTITY_SPLIT_SIGN);
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[4]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[4]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[5]));
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[4]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[5]));
                    }
                    request.setAttribute(ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_YES);
                }
                //At last position
                else if (count == ((total-1) * 3)) {
                    sb.append(String.valueOf(total)).append(ENTITY_SPLIT_SIGN).
                      append(String.valueOf(total)).append(ENTITY_SPLIT_SIGN);
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[count - 2]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 2]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                            append(ENTITY_SPLIT_SIGN);
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 2]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count -1])).
                            append(ENTITY_SPLIT_SIGN);
                    }
                    sb.append(splitResult[count]).append(ENTITY_SPLIT_SIGN).
                       append(end);
                    request.setAttribute(ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_YES);
                }
                //At middle position
                else {
                    sb.append(String.valueOf(count/3 + 1)).append(ENTITY_SPLIT_SIGN).
                       append(String.valueOf(total)).append(ENTITY_SPLIT_SIGN);
                    //Previous record
                    boolean isNextOrg = "O".equalsIgnoreCase(splitResult[count - 2]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 2]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count - 1])).
                            append(ENTITY_SPLIT_SIGN);
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count - 2]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count -1])).
                            append(ENTITY_SPLIT_SIGN);
                    }
                    //Current record
                    sb.append(splitResult[count]).append(ENTITY_SPLIT_SIGN);
                    //Next record
                    isNextOrg = "O".equalsIgnoreCase(splitResult[count + 4]);
                    if (isNextOrg) {
                        reqUrl = reqUrl.replaceFirst(perUrl, orgUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count+3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count+4]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count+5]));
                    } else {
                        reqUrl = reqUrl.replaceFirst(orgUrl, perUrl);
                        sb.append(reqUrl).append("?").
                            append(PK_PROPERTY).append(EQUAL_SIGN).append(splitResult[count+3]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_TYPE_PROPERTY).append(EQUAL_SIGN).append(splitResult[count+4]).append(URL_ELEMENT_SIGN).
                            append(ENTITY_NAME_PROPERTY).append(EQUAL_SIGN).append(encodeEntityName(splitResult[count+5]));
                    }
                    request.setAttribute(ENTITY_SELECT_RESULTS, sb.toString());
                    request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_YES);
                }
            } catch (Exception e) {
                request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
                lggr.finest("Split the results with error, method generateLink exit.");
            }
        } else {
             request.setAttribute(INCLUDE_MULTI_ENTITY, VALUE_FOR_NO);
             lggr.finest("The selected entity less than 2, needn't navigate, method generateLink exit.");
        }
    }

    /**
     * Encode & in entityName field
     * @param entityName
     * @return
     */
    private String encodeEntityName(String entityName) {
        if (!StringUtils.isBlank(entityName)) {
            //%26 will be transformed to & in js method navigateRecord, change to %2526
            entityName = entityName.replaceAll("&", "%2526");
        }
        return entityName;
    }
}
