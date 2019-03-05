<%@ page import="dti.ci.emailaddressmgr.EmailAddressManager" %>
<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.security.Authenticator" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.cs.bo.ICSNotes" %>
<%@ page import="dti.ci.core.CIFields" %>
<%@ page import="dti.oasis.tags.OasisFields" %>
<%@ page import="dti.oasis.tags.OasisFormField" %>
<%@ page language="java" %>
<%--
  Description:
  The JSP file for Entity Select jsp

  Author:
  Date:

  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  01/17/2018       ylu         Issue 189050: if CIS header fields are configured as visible, make sure them can display,
                                             which are Client ID, lagacyID, Reference number, notes
  05/02/2018       jld         Issue 109086: As part of CIS refactor, fix navigation position for varying length names.
  06/28/2018       dpang       Issue 194157: Add buildNumber parameter to static file references to improve performance
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<%
    String csPath = Module.getCSPath(request);
%>
<%@ include file="/CI_common.jsp" %>
<script type='text/javascript' src="<%=csPath%>/js/csLoadNotes.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<% // 97818:move away from applicationConfig.properties as we had for cis.organization.tab.excludelist
    // this way, at least  B.A. can configure it by using system parameter.  discussed with Mark.
    // 02/16/2011   Blake     Modified for issue 112690:Make Identifier Prominent .

    request.setAttribute("hideMenuForPerson", SysParmProvider.getInstance().getSysParm("HIDE_MENU_FOR_PERSON","CI_ORGGROUP_MI"));
    String expWit = (String) session.getAttribute("expWit");
%>
<c:choose>
    <c:when test="${entityType=='P'}">
        <c:set var="tabMenuIdsToExclude" value='${hideMenuForPerson},${tabMenuIdsToExclude},' scope="request"></c:set>
    </c:when>
</c:choose>

<c:choose>
    <c:when test="${expWit=='N'}">
        <c:set var="tabMenuIdsToExclude" value=',CI_EXPWIT_MI,${tabMenuIdsToExclude},' scope="request"></c:set>
    </c:when>
</c:choose>

<%
    String navigationRecordLabel = "";

    //System parameters which are used to configure the excluded urls for person/organization
    String excludeUrlForOrg = SysParmProvider.getInstance().getSysParm("CIS_EXC_URL_FOR_ORG","");
    String excludeUrlForPer = SysParmProvider.getInstance().getSysParm("CIS_EXC_URL_FOR_PER","");
    request.setAttribute("tabGroupId", Authenticator.getEnvString(ICIConstants.KEY_CIS_TAB_MENU_GROUP_ID, ""));
    //Entity type and entity name
    String entityName = (String) request.getAttribute(ICIConstants.ENTITY_NAME_PROPERTY);
    //150552: encode the entity name
    entityName = entityName.replaceAll("\"", "&quot;");
    String entityType = (String) request.getAttribute(ICIConstants.ENTITY_TYPE_PROPERTY);
    String clientId = (String) request.getAttribute("cisHeaderClientId");
    String pk = (String) request.getAttribute(ICIConstants.PK_PROPERTY);
    if ("O".equals(entityType)) {
        entityType = MessageManager.getInstance().formatMessage("ci.entity.type.organization");
    } else {
        entityType = MessageManager.getInstance().formatMessage("ci.entity.type.person");
    }
    String innerHtml = entityType + ": <b>" + entityName + "</b>";

    String emailAddress = EmailAddressManager.getInstance().getClientEmailAddress(new Long(pk));

    // Changed to use openEmailClient to send mail.
    if (!StringUtils.isBlank(emailAddress)) {
        innerHtml += "&nbsp;<a id=\\\"AFD_entity_entityNameComputed\\\"" +
            " href=\\\"javascript:openEmailClient(['[to][entity][^pk^]'])\\\">" +
            "<img border=\\\"0\\\" align=\\\"middle\\\" src=\\\"" + Module.getCorePath(request) + "/images/mail.gif?"+
                ApplicationContext.getInstance().getBuildNumberParameter() +"\\\""+ "id=\\\"btnEmailIcon_entity_entityNameComputed\\\"></a>";
    }
    OasisFields hdrFldsMap = (OasisFields) request.getAttribute("cisHeaderFieldsMap");
    if (hdrFldsMap!=null) {
        String[] headFieldIds = {"cisHeaderClientId", "cisHeaderLegacyDataId", "cisHeaderReferencenumber"};

        for (String field : headFieldIds) {
            OasisFormField headField = (OasisFormField) hdrFldsMap.getField(field);
            if (headField != null) {
                if (headField.getIsVisible()) {
                    innerHtml += "&nbsp;" + headField.getLabel() + ":&nbsp;" + (request.getAttribute(field) == null ? "&nbsp;" : "<b>" + (String) request.getAttribute(field) + "</b>") + "&nbsp;";
                }
            }
        }

        OasisFormField fld = (OasisFormField) hdrFldsMap.getField("entityHeaderNotesIndDescription");
        if (fld!=null) {
            if (fld.getIsVisible()) {
                innerHtml += "&nbsp;&nbsp;&nbsp;&nbsp;<div>Notes: <a id='entityHeaderNotesIndDescriptionROSPAN'  href='javascript:entityFolderOpenEntityHeaderNotes()' >" +
                            "<span id='entityHeaderNotesIndDescriptionROSPAN1' >" + (String) request.getAttribute("noteExistB") +
                            "</span></a></div>&nbsp;&nbsp;&nbsp;&nbsp;";
            }
        }
    }

    boolean isNeedNav = ICIConstants.VALUE_FOR_YES.equals(request.getAttribute(ICIConstants.INCLUDE_MULTI_ENTITY));
    String current = null;
    String total = null;
    String previous = null;
    String next = null;
    String navigationRecordControlPrevious="";
    String navigationRecordControlNext="";

    if (isNeedNav) {
        String navigationURL = (String) request.getAttribute(ICIConstants.ENTITY_SELECT_RESULTS);
        String[] splitURL = navigationURL.split(ICIConstants.ENTITY_SPLIT_SIGN);
        current = splitURL[0];
        total = splitURL[1];
        previous = splitURL[2];
        next = splitURL[4];
        //Filter the single quotes
        if (previous != null && previous.indexOf("'") > 0) {
            previous = previous.replaceAll("'", ICIConstants.ENTITY_SPLIT_SIGN);
        }
        if (next != null && next.indexOf("'") > 0) {
            next = next.replaceAll("'", ICIConstants.ENTITY_SPLIT_SIGN);
        }
        //150552: encode the double quotes for navigator Url
        if (previous != null && previous.indexOf("\"") > 0) {
            previous = previous.replaceAll("\"", ICIConstants.ENTITY_SPLIT_SIGN);
        }
        if (next != null && next.indexOf("\"") > 0) {
            next = next.replaceAll("\"", ICIConstants.ENTITY_SPLIT_SIGN);
        }

        navigationRecordLabel = "Entity " + MessageManager.getInstance().formatMessage("ci.entity.navigateRecords.records", new String[]{current, total});

        if (!previous.equals("-1")) {
            navigationRecordControlPrevious = "<a id='previous' class='pagePreviousLink' href='javascript:navigateRecord(&quot;" + previous + "&quot;)'></a>";
        }
        if (!next.equals("0")) {
            navigationRecordControlNext = "<a id='next' class='pageNextLink' href='javascript:navigateRecord(&quot;" + next + "&quot;)'></a>";
        }
    } else {
        navigationRecordLabel = "Entity 1 of 1";
    }
%>

<tr><td colspan='6' align='left'>&nbsp;</td></tr>



<script type="text/javascript">
    <%--var notesAndBackList = "Notes <a id='entityHeaderNotesIndDescriptionROSPAN'  href='javascript:entityFolderOpenEntityHeaderNotes()' >";--%>
    <%--notesAndBackList = notesAndBackList + "<span id='entityHeaderNotesIndDescriptionROSPAN1' ><%=(String)session.getAttribute(ICSNotes.NOTES_EXISTS)%></span></a>";--%>
    <%--notesAndBackList = notesAndBackList + "<input type='hidden' name='entityHeaderNotesIndDescription' value='Yes' >";--%>
    var resultBack = '';
    var navLabel;

    resultBack = resultBack + "  <a class='pageBackLink' href='<%=request.getContextPath()%>/ciEntitySearch.do?process=returnToList'> &#8249;";
    resultBack = resultBack + " <fmt:message key="ci.entity.search.label.backToList"/></a>";

    //make it look like the same as the other sub-system.
    navLabel = "<div class='pageNextPrevLinks'><%= navigationRecordLabel %>&nbsp;&nbsp;";
    navLabel += "<div><%= navigationRecordControlPrevious %>&nbsp;&nbsp;";
    navLabel += "<%= navigationRecordControlNext %></div>&nbsp;&nbsp;</div>";
    resultBack = navLabel + resultBack;

     //Change page header
    if (getObject("pageTitleForpageHeader")) {
        getObject("pageTitleForpageHeader").className = "pageHeaderText";
        getObject("pageTitleForpageHeader").innerHTML = "<%=innerHtml%>";
    }

    if (getObject("resultBack")) {
        getObject("resultBack").innerHTML = '';
        getObject("resultBack").className = '';

        // added by Jacky 10-08-2008
        getObject("resultBack").innerHTML = (typeof resultBack == 'undefined' || null  == resultBack ? '' :  resultBack);
    }


    /*
    * Handle the Previous/Next link
    */
    function navigateRecord(direction) {
        if (window.isPageDataChanged) {
            if (isPageDataChanged()) {
                if (!confirm(ciDataChangedConfirmation)) {
                    return;
                }
            }
        }
        while (direction.indexOf("!~") > 0) {
            direction = direction.replace(/!~/, "'");
        }
        //Added for issue 104037
        var url = direction.substring(direction.lastIndexOf("/") + 1, direction.indexOf("?"));
        var entityType = direction.substring(direction.indexOf("&") + 1).split("&")[0].split("=")[1];
        var exOrgUrl = "<%=excludeUrlForOrg%>";
        var exPerUrl = "<%=excludeUrlForPer%>";         
        if (entityType.toUpperCase() == "P") {
            if (exPerUrl.indexOf("," + url + ",") > -1) {
                alert('<fmt:message key="ci.entity.message.personalPage.navigated"/>');
                direction = direction.substring(0, direction.lastIndexOf("/")) +
                            "/ciEntityPersonModify.do" +
                            direction.substring(direction.indexOf("?"));
            }
        } else if (entityType.toUpperCase() == "O") {
            if (exOrgUrl.indexOf("," + url + ",") > -1) {
                alert('<fmt:message key="ci.entity.message.orgPage.navigated"/>');
                direction = direction.substring(0, direction.lastIndexOf("/")) +
                            "/ciEntityOrgModify.do" +
                            direction.substring(direction.indexOf("?"));
            }
        }
        showProcessingImgIndicator();
        setWindowLocation(direction);
    }

    var notesROSPAN = getObject("entityHeaderNotesIndDescriptionROSPAN");
    if (notesROSPAN != null) {
        notesROSPAN.className = "notes";
    }
    var obj = getObject("entityHeaderNotesIndDescriptionROSPAN1");
    if(obj) {
        var value = obj.innerText;
        obj.innerText = '';
        if (value == "Yes") {
            var imageElement = document.createElement("img");
            imageElement.name = "noteImg";
            imageElement.src = getCorePath() + notesImage;
            imageElement.border = "0";
            obj.appendChild(imageElement);

        } else {
            var imageElement = document.createElement("img");
            imageElement.name = "noteImg";
            imageElement.src = getCorePath() + noNotesImage;
            imageElement.border = "0";
            obj.appendChild(imageElement);
        }
    }
    //-----------------------------------------------------------------------------
    // Opens the notes popup for the entity notes from the entity header
    // field entityHeaderNotesIndDescription.
    //-----------------------------------------------------------------------------
    function entityFolderOpenEntityHeaderNotes() {
        if (document.forms[0].pk) {
            // entityPK is a field on the form.
            if (window.loadNotesWithReloadOption) {
                // Function loadNotesWithReloadOption is available from csLoadNotes.js.
                loadNotesWithReloadOption(document.forms[0].pk.value, 'ENTITY', 'ENTITY', true, false, 'entityHeaderHandleNotesExist');
            } else {
                alert('<fmt:message key="ci.entity.message.notes.notAvailable"/>');
            }
        } else {
            alert('<fmt:message key="ci.entity.message.notes.open"/>');
        }
    }
</script>
<%
    // Initialize Sys Parms for JavaScript to use
    String reverseImageRight = SysParmProvider.getInstance().getSysParm("CS_REVERSEIMAGERIGHT", "N");
%>
<script type="text/javascript">
    setSysParmValue("CS_REVERSEIMAGERIGHT", '<%= reverseImageRight %>');
    getSingleObject("pageTitleForpageHeader").style.width = "400px";
</script>
<script language="javascript" src="<%=csPath%>/js/csImaging.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<input type="hidden" value="<%=clientId%>" name="cisHeaderClientId"/>







