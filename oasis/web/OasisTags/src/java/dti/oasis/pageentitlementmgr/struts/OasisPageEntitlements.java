package dti.oasis.pageentitlementmgr.struts;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.pageentitlementmgr.PageEntitlement;
import dti.oasis.pageentitlementmgr.PageEntitlementManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the struts BodyTagSupport to form a new tag that auto-generates javascript for defined page entitlements.
 * <p/>
 * This tag gets a list of page entitlements configured using PageEntitlementManager and auto-generates javascript based
 * on indFieldLocation attribute.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jan 31, 2007
 *
 * @author mlmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/06/07         fcb         Added logic to use hash map to store secured ids.
 * 02/15/2012       James       Issue#129150 add a list of page entitlement id.
 * 11/16/2017       kshen       Grid replacement. Use recordset(columnName).value instead of recordset(columnName) to get column value.
 * 09/14/2018       mgitelm     193649 - Add handling for buttons with duplicate IDs
 * 10/24/2018       cesar       196687 - Refactor when calling hasObject()/getObjects()
 * ---------------------------------------------------
 */
public class OasisPageEntitlements extends javax.servlet.jsp.tagext.BodyTagSupport {

    /* (non-Javadoc)
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag");
        int rc = EVAL_BODY_BUFFERED;
        TagUtils util = TagUtils.getInstance();

        util.write(pageContext, "\n<script language='javascript'>" + "\n");
        util.write(pageContext, "function pageEntitlements(isEnforceForGrid, gridId) {" + "\n");
        util.write(pageContext, "   var beginTime = new Date();\n");

        util.write(pageContext, "   isPageEntitlementInProgress = true;\n");
        util.write(pageContext, "   var targetObj, sourceObj;\n");
        util.write(pageContext, "   var securedPEMap = getHashMap(\"securedPEMap\");\n");
        util.write(pageContext, "   var prefix = ((isEnforceForGrid && gridId)?gridId:\"page\");\n");
        util.write(pageContext, "   var securedPEIds = getHashMap(prefix);\n");
        util.write(pageContext, "   if (securedPEMap.hasElement(prefix)) {\n");
        util.write(pageContext, "      securedPEIds.clean();\n");
        util.write(pageContext, "   } else {\n");
        util.write(pageContext, "      securedPEMap.putElement(prefix,securedPEIds);\n");
        util.write(pageContext, "   }\n");
        util.write(pageContext, "   var recSet = null;\n");
        util.write(pageContext, "   if (isEnforceForGrid && gridId) {\n");
        util.write(pageContext, "       recSet = getXMLDataForGridName(gridId).recordset;\n");
        util.write(pageContext, "   }\n");
        util.write(pageContext, "   var emptyRecordset = false; \n");
        util.write(pageContext, "   if (recSet && isEmptyRecordset(recSet)) {\n");
        util.write(pageContext, "       emptyRecordset = true;\n");
        util.write(pageContext, "   }\n");
//        util.write(pageContext, "   alert('pageEntitlements: isEnforceForGrid = ' + isEnforceForGrid + ', gridId = ' + gridId + ' recSet = ' + recSet);\n");

        String pageURI = "";
        if (pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI) != null) {
            pageURI = (String) pageContext.getRequest().getAttribute(RequestIds.REQUEST_URI);
        }
        l.logp(Level.FINE, getClass().getName(), "doStartTag", "pageURI:" + pageURI);

        //Get all page entitlements for the provided the current page URI.
        Iterator pageEntitlements = PageEntitlementManager.getInstance().iterator(pageURI);

        List<String> idList = new ArrayList<String>();

        while (pageEntitlements.hasNext()) {
            PageEntitlement pageEntitlement = (PageEntitlement) pageEntitlements.next();
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "**********************************************************");
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "1. Id:" + pageEntitlement.getId());
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "2. indFieldName:" + pageEntitlement.getIndFieldName());
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "3. indFieldLocation:" + pageEntitlement.getIndFieldLocation());
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "4. Action:" + pageEntitlement.getAction());
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "5. Default Action For No Rows:" + pageEntitlement.getDefaultActionForNoRows());

            idList.add(pageEntitlement.getId());

            // Determine the indicator field location to determine what kind of javascript needs to be generated -
            // Generate form type javascript or grid type javascript.

            // The action can either be configured as Disabled/Enabled, Enabled/Disabled, Hide/Show or Show/Hide.
            // The value of indFieldName actually derives what action needs to be taken and the javascript is generated
            // accordingly.
            //
            // 1. If the action is configured as "Disabled/Enabled",
            //      Yes value of indFieldName will disable the configured Id (which equates to tag id on the page)
            //      No value of indFieldName will enable the configured Id (which equates to tag id on the page)
            // 2. If the action is configured as "Enabled/Disabled",
            //      Yes value of indFieldName will enable the configured Id (which equates to tag id on the page)
            //      No value of indFieldName will disable the configured Id (which equates to tag id on the page)
            // 3. If the action is configured as "Hide/Show",
            //      Yes value of indFieldName will hide the configured Id (which equates to tag id on the page)
            //      No value of indFieldName will show the configured Id (which equates to tag id on the page)
            // 4. If the action is configured as "Show/Hide",
            //      Yes value of indFieldName will show the configured Id (which equates to tag id on the page)
            //      No value of indFieldName will hide the configured Id (which equates to tag id on the page)

            if (pageEntitlement.getIndFieldLocation().isPage()) {
                // If the attribute doesn't exist or is blank, default it it YesNoFlag.N
                YesNoFlag attributeValue = YesNoFlag.N;
                Object fieldValue = pageContext.getRequest().getAttribute(pageEntitlement.getIndFieldName());
                if (fieldValue != null) {
                    attributeValue = YesNoFlag.getInstance(fieldValue.toString());
                }
                else {
                    String paramValue = pageContext.getRequest().getParameter(pageEntitlement.getIndFieldName());
                    if (paramValue != null) {
                        attributeValue = YesNoFlag.getInstance(paramValue);
                    }
                    else {
                        l.logp(Level.WARNING, getClass().getName(), "doStartTag", "Failed to locate the field value for indFieldName '" + pageEntitlement.getIndFieldName() + "'; defaulting the value to N");
                    }
                }

                // Generate javascript that enforces page entitlments for page.
                l.logp(Level.FINE, getClass().getName(), "doStartTag", "5. Entitlement Setting:" + attributeValue);

                boolean isAllowed = false;
                l.logp(Level.FINE, getClass().getName(), "doStartTag", "Action:" + pageEntitlement.getAction().toString());
                if (((pageEntitlement.getAction().isEnabledDisabled() || pageEntitlement.getAction().isShowHide()) && attributeValue.booleanValue()) ||
                    ((pageEntitlement.getAction().isDisabledEnabled() || pageEntitlement.getAction().isHideShow()) && !attributeValue.booleanValue())) {
                    isAllowed = true;
                }
                else {
                    isAllowed = false;
                    util.write(pageContext, "   //Collecting secured field ids irrespective of whether the field exists on the document or not" + "\n");
                    util.write(pageContext, "   securedPEIds.putElement(prefix+\"" + pageEntitlement.getId() + "\",\"" + pageEntitlement.getId() + "\")" + ";" + "\n");
                }
                util.write(pageContext, "\n");
                util.write(pageContext, "   targetObj = getObject('" + pageEntitlement.getId() + "');\n");
                util.write(pageContext, "   if (targetObj) {\n");
                util.write(pageContext, "       sourceObj = getObject('" + pageEntitlement.getIndFieldName() + "');\n");
                util.write(pageContext, "       if (isUndefined(sourceObj)) {\n");
                util.write(pageContext, "           sourceObj = setInputFormField('" + pageEntitlement.getIndFieldName() + "', '" + attributeValue + "');\n");
                util.write(pageContext, "       }\n");
                util.write(pageContext, "       indValue=getObjectValue(sourceObj);\n");
                // Execute the action based on the indValue
                if(pageEntitlement.getAction().isDisabledEnabled()) {
                    util.write(pageContext,"       // calling function enableDisableField(field, isDisabled) for action Disable/Enable\n");
                    util.write(pageContext,"       enableDisableFieldById(targetObj, (indValue == 'Y'));\n");
                } else if (pageEntitlement.getAction().isEnabledDisabled()) {
                    util.write(pageContext,"       // calling function enableDisableField(field, isDisabled) for action Enabled/Disabled\n");
                    util.write(pageContext,"       enableDisableFieldById(targetObj, (indValue == 'N'));\n");
                } else if(pageEntitlement.getAction().isHideShow()) {
                    util.write(pageContext,"       // calling function hideShowField(field, isHidden) for action Hide/Show\n");
                    util.write(pageContext,"       hideShowFieldById(targetObj, (indValue == 'Y'));" + "\n");
                } else {
                    util.write(pageContext,"       // calling function hideShowField(field, isHidden) for action Show/Hide\n");
                    util.write(pageContext,"       hideShowFieldById(targetObj, (indValue == 'N'));" + "\n");
                }
                util.write(pageContext, "   }\n");
            }
            else {
                boolean isSecured = false;
                // Generate javascript that enforces page entitlments for grid.
                String columnName = pageEntitlement.getIndFieldName().toUpperCase();
                if ("EDIT_IND,UPDATE_IND,DISPLAY_IND".indexOf(columnName) == -1) {
                    columnName = "C" + columnName;
                }
                // If the gridId is defined, only perform this page entitlement logic if the provided gridId matches.
                String gridIdCheck = "";
                if (pageEntitlement.isGridIdDefined()) {
                    gridIdCheck = " && gridId.toUpperCase() == \"" + pageEntitlement.getGridId().toUpperCase() + "\"";
                }
                util.write(pageContext, "   if(isEnforceForGrid && gridId" + gridIdCheck + " && isFieldDefinedForGrid(gridId , \"" + columnName + "\")) {" + "\n");
                util.write(pageContext, "      var indValue = \"N\" ; \n");
                util.write(pageContext, "      if(recSet && (!emptyRecordset)) {" + "\n");
                util.write(pageContext, "         indValue = \"\" + recSet(\"" + columnName + "\").value; \n");
                util.write(pageContext, "         indValue = trim(indValue.toUpperCase());\n");
//                util.write(pageContext,"         alert('Field exists in the recordset: " + columnName + "=' + indValue);" + "\n");
                util.write(pageContext, "         if (indValue != \"Y\") {" + "\n");
                util.write(pageContext, "            // Default indValue to \"N\" if the column value is not \"Y\".\n");
                util.write(pageContext, "            indValue = \"N\";" + "\n");
                util.write(pageContext, "         }" + "\n");
                util.write(pageContext, "      }" + "\n");

                util.write(pageContext, "      //Collecting secured field ids irrespective of whether the field exists on the document or not" + "\n");
                util.write(pageContext, "      if (emptyRecordset) {" + "\n");
                if (pageEntitlement.getDefaultActionForNoRows().isDisabled() || pageEntitlement.getDefaultActionForNoRows().isHide()) {
                    util.write(pageContext, "         securedPEIds.putElement(prefix+\"" + pageEntitlement.getId() + "\",\"" + pageEntitlement.getId() + "\")" + ";" + "\n");
                }
                else {
                    util.write(pageContext, "         securedPEIds = securedPEIds;" + "\n");
                }
                util.write(pageContext, "      } else {" + "\n");
                if (pageEntitlement.getAction().isDisabledEnabled() || pageEntitlement.getAction().isHideShow()) {
                    util.write(pageContext, "        if (indValue == 'Y') {" + "\n");
                    util.write(pageContext, "            securedPEIds.putElement(prefix+\"" + pageEntitlement.getId() + "\",\"" + pageEntitlement.getId() + "\")" + ";" + "\n");
                    util.write(pageContext, "        }" + "\n");
                }
                else if (pageEntitlement.getAction().isEnabledDisabled() || pageEntitlement.getAction().isShowHide()) {
                    util.write(pageContext, "        if (indValue == 'N') {" + "\n");
                    util.write(pageContext, "            securedPEIds.putElement(prefix+\"" + pageEntitlement.getId() + "\",\"" + pageEntitlement.getId() + "\")" + ";" + "\n");
                    util.write(pageContext, "        }" + "\n");
                }
                util.write(pageContext, "      } " + "\n");

                util.write(pageContext, "      targetObj = getObject('" + pageEntitlement.getId() + "');\n");
                util.write(pageContext, "      if(targetObj) {" + "\n");
//                util.write(pageContext,"       alert('Control exists in the page');" + "\n");
//                util.write(pageContext,"       alert(indValue);\n");
//                util.write(pageContext,"       alert(indValue.substring(0,1));\n");
                //util.write(pageContext,"       alert('getObject found for:" + pageEntitlement.getId() + "');" + "\n");

                util.write(pageContext, "         if (emptyRecordset) {" + "\n");
//              If it is an empty recordset, execute the default action if no rows
                if (pageEntitlement.getDefaultActionForNoRows().isDisabled()) {
                    util.write(pageContext, "            // calling function enableDisableField(field, isDisabled) for default action Disable\n");
                    util.write(pageContext, "            enableDisableFieldById(targetObj, true);\n");
                }
                else if (pageEntitlement.getDefaultActionForNoRows().isEnabled()) {
                    util.write(pageContext, "            // calling function enableDisableField(field, isDisabled) for default action Enabled\n");
                    util.write(pageContext, "            enableDisableFieldById(targetObj, false);\n");
                }
                else if (pageEntitlement.getDefaultActionForNoRows().isHide()) {
                    util.write(pageContext, "            // calling function hideShowField(field, isHidden) for default action Hide\n");
                    util.write(pageContext, "            hideShowFieldById(targetObj, true);\n");
                }
                else {
                    util.write(pageContext, "            // calling function hideShowField(field, isHidden) for default action Show\n");
                    util.write(pageContext, "            hideShowFieldById(targetObj, false);\n");
                }

                util.write(pageContext, "         } else {" + "\n");
                // Execute the action based on the indValue
                if (pageEntitlement.getAction().isDisabledEnabled()) {
                    util.write(pageContext, "            // calling function enableDisableField(field, isDisabled) for action Disable/Enable\n");
                    util.write(pageContext, "            enableDisableFieldById(targetObj, (indValue == 'Y'));\n");
                }
                else if (pageEntitlement.getAction().isEnabledDisabled()) {
                    util.write(pageContext, "            // calling function enableDisableField(field, isDisabled) for action Enabled/Disabled\n");
                    util.write(pageContext, "            enableDisableFieldById(targetObj, (indValue == 'N'));\n");
                }
                else if (pageEntitlement.getAction().isHideShow()) {
                    util.write(pageContext, "            // calling function hideShowField(field, isHidden) for action Hide/Show\n");
                    util.write(pageContext, "            hideShowFieldById(targetObj, (indValue == 'Y'));" + "\n");
                }
                else {
                    util.write(pageContext, "            // calling function hideShowField(field, isHidden) for action Show/Hide\n");
                    util.write(pageContext, "            hideShowFieldById(targetObj, (indValue == 'N'));" + "\n");
                }

                util.write(pageContext, "         }" + "\n");
                util.write(pageContext, "      }" + "\n");
                util.write(pageContext, "   }" + "\n");
            }
            l.logp(Level.FINE, getClass().getName(), "doStartTag", "**********************************************************");
        }
        util.write(pageContext, "   var functionExists = eval(\"window.enforcePEForPageHeaderNavigationDropdown\");\n");
        util.write(pageContext, "   if (functionExists) {" + "\n");
        util.write(pageContext, "       window.enforcePEForPageHeaderNavigationDropdown();" + "\n");
        util.write(pageContext, "   }" + "\n");
        util.write(pageContext, " isPageEntitlementInProgress = false;\n");
        util.write(pageContext, "   // Next, hide some unused buttons according to UI Style for this row \n");
        util.write(pageContext, "   functionExists = eval(\"window.hideButtonsAccordingToUIStyle\");\n");
        util.write(pageContext, "   if (functionExists) {" + "\n");
        util.write(pageContext, "       window.hideButtonsAccordingToUIStyle();" + "\n");
        util.write(pageContext, "   }" + "\n");
        util.write(pageContext, " var endTime= new Date();\n");
        util.write(pageContext, " logDebug(\"function pageEntitlements executed in \"+(endTime.getTime() - beginTime.getTime())+\" milliseconds.\" );\n");
        util.write(pageContext, "}" + "\n");

        String[] idString = (String[]) idList.toArray(new String[idList.size()]);
        util.write(pageContext, "function getPageEntitlementIdList() {" + "\n");
        util.write(pageContext, "   return \"" + StringUtils.arrayToDelimited(idString, ",", true, true) + "\";\n");
        util.write(pageContext, "}" + "\n");

        util.write(pageContext, "</script>" + "\n");
        rc = super.doStartTag();
        l.exiting(getClass().getName(), "doStartTag");
        return rc;
    }

    //-------------------------------------------------
    // Configuration constructor and accessor methods
    //-------------------------------------------------
    public OasisPageEntitlements() {
    }

}
