<%@ page import="java.util.Iterator" %>
<%@ page import="dti.oasis.util.MenuBean" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%--
  Description: This JSP renders the information for the page header.

  Following are the various JSTL variables that can be set to override the default behavior:

   1. "pageTitle"                     - Title for the page
                                        Default value is the configured page title.

   2. "globalActionItemGroupId"       - The action item that needs to be rendered as part of the page header.
                                        Default value is empty string, meaning there is no action group on the page header.

   3. "showActionItemsAsDropdownlist" - Boolean value that indicates whether to show the action group as dropdownlist
                                        or buttons.
                                        Default value is true - render as dropdownlist action group.

   4. "dropdownSelectFromDesc"        - If the action group is rendered as dropdownlist, the value of this variable will
                                        form the first option in the dropdownlist.
                                        Default value is "-Select-".

   5. "actionItemColorScheme"         - If the action group is rendered as button, this value will indicate the color scheme
                                        of button to be rendered.
                                        Default value is "Blue".

   6. "showNextPrev"                  - Boolean value to indicate whether to show the Next, Prev record navigation links.
                                        Default value is false.

   7. "showNextPrevWithLabel"         - Boolean value to indicate whether the show "Next", "Prev" labels along with the
                                        record navigation links.
                                        Default value is false.

   8. "recordLocationDescription"     - Label description for the next, prev record navigation link. (eg. List 1 Of 20)
                                        Default value is empty string.

   9. "showNextRecordLink"            - Boolean value to indicate whether to show the Next record navigation link.
                                        This link always fires navigateRecords() javascript function with "next" as a parameter.
                                        The implementation of this function should be provided by the application that
                                        uses this JSP.
                                        Default value is true.

  10. "showPreviousRecordLink"        - Boolean value to indicate whether to show the Prev record navigation link.
                                        This link always fires navigateRecords() javascript function with "previous" as
                                        a parameter.
                                        The implementation of this function should be provided by the application that uses this JSP.
                                        Default value is true.

  11. "pageBackLink"                  - String value that forms the label for back link. If set, it always calls a javascript
                                        function goBack(). The implementation of this function should be provided by the
                                        application that uses this JSP.
                                        Default value is empty string.

  Author: mmanickam
  Date: May 4, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  09/27/2007       sxm         Replaced call to function goBack() with handleOnPageBack()
  11/06/2007       fcb         Changed logic in enforcePEForPageHeaderNavigationDropdown to use hash map.
  04/09/2008       wer         removed passing of DBPOOLID as request parameter
  08/14/2008       Jacky       add result list back link
  10/11/2010       wfu         111776: String literals refactoring.
  12/01/2010       syang       114878: Set nowrap="true" to "Print|Help" div.
  08/19/2013       mlm         147571: Enhanced to generate ImageRight Icon in page header section.
  04/25/2017       ddai        183966: Check whether element is hidden by default or by obr if it is a page entitlement field.
  10/24/2018       cesar       196687 - Modified enforcePEForPageHeaderNavigationDropdown() to refactor hasObject()/getObject()
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<script type="text/javascript">
    function fireGlobalAction(id) {
        var url = id.value;
        if (url) {
            if(url.toUpperCase().substring(0, "'JAVASCRIPT:".length-1) == "JAVASCRIPT:") {
                url = url.substring("'JAVASCRIPT:".length-1) ;
                eval (url + ";");
            } else {
              eval (url);
            }
        }
        id.options[0].selected=true;
        return true;
    }
</script>


<c:if test="${UIStyleEdition=='2'}">

<c:if test="${empty pageHeaderDivId}">
    <c:set var="pageHeaderDivId" value="pageHeader"></c:set>
</c:if>

<c:if test="${empty pageTitle}">
    <c:set var="pageTitle" value="${ pageBean.title }"></c:set>
</c:if>

<c:if test="${empty globalActionItemGroupId}">
    <c:set var="globalActionItemGroupId" value=""></c:set>
</c:if>
<c:if test="${empty showActionItemsAsDropdownlist}">
    <c:set var="showActionItemsAsDropdownlist" value="true"></c:set>
</c:if>
<fmt:message key="label.dropDown.default.select" var="defaultSelect" scope="request"/>
<c:if test="${empty dropdownSelectFromDesc}">
    <c:set var="dropdownSelectFromDesc" value="${defaultSelect}"></c:set>
</c:if>
<c:if test="${empty actionItemColorScheme}">
    <c:set var="actionItemColorScheme" value="Blue"></c:set>
</c:if>

<c:if test="${empty showNextPrev}">
    <c:set var="showNextPrev" value="false"></c:set>
</c:if>
<c:if test="${empty showNextPrevWithLabel}">
    <c:set var="showNextPrevWithLabel" value="false"></c:set>
</c:if>
<c:if test="${empty recordLocationDescription}">
  <c:set var="recordLocationDescription" value=""></c:set>
</c:if>
<c:if test="${empty showNextRecordLink}">
    <c:set var="showNextRecordLink" value="true"></c:set>
</c:if>
<c:if test="${empty showPreviousRecordLink}">
    <c:set var="showPreviousRecordLink" value="true"></c:set>
</c:if>
<c:if test="${empty pageBackLink}">
  <c:set var="pageBackLink" value=""></c:set>
</c:if>

<c:if test="${empty resultBackLink}">
    <c:set var="resultBackLink" value=""></c:set>
</c:if>

<%-- Remove the following <tr><td>, once UI2 is fully converted to use DIV --%>
<tr><td colspan=8>
<div id="<c:out value='${pageHeaderDivId}'></c:out>" class="pageHeader">
  <div id="<c:out value='pageTitleFor${pageHeaderDivId}'></c:out>" class="pageTitle">
      <c:out value='${pageTitle}'></c:out>
  </div>
  <c:if test="${globalActionItemGroupId != ''}">
  <div class="globalActionItems actionItemGroupHidden" id='entityPagePrintHelp'>

      <c:if test="${showActionItemsAsDropdownlist == false}">
          <oweb:actionGroup actionItemGroupId='<%= (String) pageContext.getAttribute("globalActionItemGroupId")%>'
                            cssColorScheme='<%= (String) pageContext.getAttribute("actionItemColorScheme")%>'
                            layoutDirection="Horizontal"></oweb:actionGroup>
      </c:if>
      <c:if test="${showActionItemsAsDropdownlist}">
          <oweb:actionGroup actionItemGroupId='<%= (String) pageContext.getAttribute("globalActionItemGroupId")%>'
                            isDropDownActionItemGroup = "true"
                            dropDownSelectFromDesc='<%= (String) pageContext.getAttribute("dropdownSelectFromDesc")%>' >
          </oweb:actionGroup>
          <%
              String dropdownActionItemIds = ""; 
              if (request.getAttribute("dropdownActionItemIds") != null) {
                  dropdownActionItemIds = (String) request.getAttribute("dropdownActionItemIds");
              }
          %>

          <script type="text/javascript">
              // Create a master copy of available action items for the page.
              var cachedPageHeaderActionItemDDL = getObject(dropdownFieldId() + "_OPTIONS");

              // This will contain a comma delimited list of ids (which will be used to enforce page entitlements)
              // for corresponding options in master copy of available action items for the page
              function getDropdownActionItemIds() {
                  return '<%= dropdownActionItemIds %>' ;
              }

              // This will return the id for  global action item dropdownlist field
              function dropdownFieldId() {
                  return '<%= (String) pageContext.getAttribute("globalActionItemGroupId") %>'
              }

              function enforcePEForPageHeaderNavigationDropdown() {
                  var dropdownField = getObject(dropdownFieldId());

                  if (!isUndefined(dropdownField)) {
                      if (dropdownField.options && cachedPageHeaderActionItemDDL.options) {
                          if (cachedPageHeaderActionItemDDL.options.length > 0) {
                              // The below appears to be a IE error as there is no
                              // reason why the below statement should fail.
                              try {
                                  dropdownField.options.length = 0;
                              } catch (ex) {}

                              // We should always have at least 1 item in the list - This is either "-Select-"
                              // or configured dropdownSelectFromDesc
                              var newOption = document.createElement("option");
                              newOption.text = cachedPageHeaderActionItemDDL.options[0].text;
                              newOption.value = cachedPageHeaderActionItemDDL.options[0].value;
                              dropdownField.options.add(newOption);

                              var actionItemIds = getDropdownActionItemIds().split(",");
                              var securedPEMap = getHashMap("securedPEMap");
                              var securedPEMapEntries = securedPEMap.getEntries();
                              for (var i = 1; i < cachedPageHeaderActionItemDDL.options.length; i++) {
                                  var skip = false;
                                  var currentOption = cachedPageHeaderActionItemDDL.options[i];
                                  // remove prefix OPTION_
                                  var fieldId = currentOption.id.substring(7);
                                  var isPageEntitlementField = false;
                                  if (window.getPageEntitlementIdList) {
                                      if (window.getPageEntitlementIdList().indexOf("," + fieldId + ",") >= 0) {
                                          isPageEntitlementField = true;
                                      }
                                  }
                                  if (!isPageEntitlementField) {
                                      if (currentOption.getAttribute("isHiddenByDefault") == "true") {
                                          skip = true;
                                          if (currentOption.getAttribute("isDisplayedByOBR") == "Yes") {
                                               skip = false;
                                          }
                                      } else {
                                          if (currentOption.getAttribute("isHiddenByOBR") == "Yes") {
                                               skip = true;
                                          }
                                      }
                                  } else {
                                      // Go through all the lists with secured items.
                                      for (var j = 0; j < securedPEMapEntries.length; j++) {
                                          var hashMapName = securedPEMapEntries[j][0];
                                          var hashMap = securedPEMap.getElement(hashMapName);
                                          if (hashMap.hasElement(hashMapName + actionItemIds[i - 1])) {
                                              skip = true;
                                              break;
                                          }else{
                                              if (currentOption.getAttribute("isHiddenByDefault") == "true") {
                                                  skip = true;
                                                  if (currentOption.getAttribute("isDisplayedByOBR") == "Yes") {
                                                      skip = false;
                                                  }
                                              } else {
                                                  if (currentOption.getAttribute("isHiddenByOBR") == "Yes") {
                                                      skip = true;
                                                  }
                                              }
                                          }
                                      }
                                  }
                                  if (!skip) { // This action item is not secured, we can add it to the dropdown.
                                      newOption = document.createElement("option");
                                      newOption.text = currentOption.text;
                                      newOption.value = currentOption.value;
                                      dropdownField.options.add(newOption);
                                  }
                                  if (hasObject('IMG_IMAGE_RIGHT_' + fieldId)) {
                                      if (skip) {
                                          hideShowElementByClassName(getObject('IMG_IMAGE_RIGHT_' + fieldId), true);
                                      } else {
                                          hideShowElementAsInlineByClassName(getObject('IMG_IMAGE_RIGHT_' + fieldId), false);
                                      }
                                  }
                              }
                          }
                      }
                  }
                  return;
              }
          </script>
        </c:if>
    </div>

   </c:if>
  <c:if test="${showNextPrev == true || showNextPrevWithLabel == true}">
      <div class="pageNextPrevLinks">
          <c:out value="${recordLocationDescription}"></c:out> &nbsp;
          <c:if test="${showPreviousRecordLink}">
              <a id="previousRecord" class="pagePreviousLink" href="javascript:navigateRecords('previous')">
                  <c:if test="${showNextPrevWithLabel}">
                      <c:out value="Prev"></c:out>
                  </c:if>
                  <c:if test="${showNextPrevWithLabel==false}">
                      &nbsp;
                  </c:if>
                </a>
          </c:if>
          <c:if test="${showNextRecordLink}">
              <a id="nextRecord" class="pageNextLink" href="javascript:navigateRecords('next')">
                  <c:if test="${showNextPrevWithLabel}">
                      <c:out value="Next"></c:out>
                  </c:if>
                  <c:if test="${showNextPrevWithLabel==false}">
                      &nbsp;
                  </c:if>
                </a>
          </c:if>
      </div>
  </c:if>

  <c:if test="${resultBackLink !=''}">
        <div class="pageBackLink" id='resultBack'>
          <a class="pageBackLink" href="<%=request.getContextPath()%>/ciEntitySearch.do?process=returnToList"> &#8249;
            <c:out value='${resultBackLink}'></c:out>
          </a>
      </div>
  </c:if>

  <c:if test="${pageBackLink !=''}">
      <div class="pageBackLink">
          <a class="pageBackLink" href="javascript:handleOnPageBack();"> &#8249;
            <c:out value='${pageBackLink}'></c:out>
          </a>
      </div>
  </c:if>
  <div class="pagePrintHelpLinks" nowrap="true">
 <script type="text/javascript">
     function openEAdmin(url) {
         url = getEnvPath().substr(0, getEnvPath().lastIndexOf('/') + 1) + url;
         var winOptions = 'scrollbars=yes,resizable=yes,toolbar=yes,menubar=yes,status=yes,location=yes,titlebar=yes';
         var winName = 'EAdminWindow';
         var newwindow = window.open(url, winName, winOptions);
         if (newwindow != null && newwindow.focus)
            {newwindow.focus()}
     }

     function openForumURL(url) {
         var winOptions = 'scrollbars=yes,resizable=yes,toolbar=yes,menubar=yes,status=yes,location=yes,titlebar=yes';
         var winName = 'ForumWindow';
         var newwindow = window.open(url, winName, winOptions);
         if (newwindow != null && newwindow.focus)
            {newwindow.focus()}
     }
</script>
  <%
      boolean isEmployee = request.isUserInRole("EMPLOYEE") || request.isUserInRole("EMPLOYEEROLE");
      boolean isAdmin = request.isUserInRole("OASISSYSADMIN") || request.isUserInRole("OASISSYSADMINROLE");
      boolean isSysInfoAdmin = request.isUserInRole("OASISSYSINFOADMIN") || request.isUserInRole("OASISSYSINFOADMINROLE");
      boolean isWebWBAdmin = request.isUserInRole("OASISCUSTWEBWBADMIN") || request.isUserInRole("OASISCUSTWEBWBADMINROLE");

      boolean isLinkToActiveUserAllowed = isEmployee || isAdmin || isSysInfoAdmin;
      boolean isLinkToWorkbenchAllowed = isEmployee || isAdmin || isWebWBAdmin;
      if(applicationId.contains("eAdmin"))
          isLinkToWorkbenchAllowed = false;

      if(isLinkToActiveUserAllowed) {
          String relPath = "../eAdmin/SysInfo/accesstrail/viewActiveUsers.dti";
          String activeUsersUrl = "javascript:openEAdmin('"+relPath+"');";
 %>
      <a class="txtSmallBlue" href="<%= activeUsersUrl %>"><fmt:message key='label.header.page.activeusers'/></a> |
 <%
      }
      if(isLinkToWorkbenchAllowed) {
          String relPath = "../eAdmin/CustWebWB/pageconfigmgr/maintainPageConfig.do?process=handleRedirect&code="+pageBean.getId();
          String workbenchUrl = "javascript:openEAdmin('"+relPath+"');";
  %>

      <a class="txtSmallBlue" href="<%= workbenchUrl %>"><fmt:message key='label.header.page.workbench'/></a> |
  <%
      }
  %>
      <a class="txtSmallBlue" href="javascript:window.print();"><fmt:message key='label.header.page.print'/></a> |
      <%
          if(!StringUtils.isBlank(ApplicationContext.getInstance().getProperty("usersForum.URL", ""))) {
            String forumUrl = "javascript:openForumURL('" + ApplicationContext.getInstance().getProperty("usersForum.URL", "") + "');" ; 
      %>
      <a class="txtSmallBlue" href="<%= forumUrl %>" ><fmt:message key='label.header.page.forum'/></a> |
      <%
          }
      %>
      <% String helpUrl = "javascript:viewHelp('" + pageBean.getHelpUrl() + "');" ; %>
      <a class="txtSmallBlue" href="<%= helpUrl %>" ><fmt:message key='label.header.page.help'/></a>
  </div>
</div>
<%-- Remove the following </td></tr>, once UI2 is fully converted to use DIV --%>
</td></tr>
<%-- Remove All JSTL Variables that has been set --%>
<c:remove var="pageTitle"></c:remove>
<c:remove var="globalActionItemGroupId"></c:remove>
<c:remove var="showActionItemsAsDropdownlist"></c:remove>
<c:remove var="dropdownSelectFromDesc"></c:remove>
<c:remove var="actionItemColorScheme"></c:remove>
<c:remove var="showNextPrev"></c:remove>
<c:remove var="showNextPrevWithLabel"></c:remove>
<c:remove var="recordLocationDescription"></c:remove>
<c:remove var="showNextRecordLink"></c:remove>
<c:remove var="showPreviousRecordLink"></c:remove>
<c:remove var="pageBackLink"></c:remove>
</c:if>
