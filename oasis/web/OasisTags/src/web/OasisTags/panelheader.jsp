<%@ page import="dti.oasis.util.StringUtils" %>
<%--
  Description: This JSP will render the panel for the content enclosed within the panel.

  Following are the various JSTL variables that can be set to override the default behavior:

  1. "isPanelWithBorder"  - Boolean value that indicate whether the panel has a border.
                            Default value is true.

  2. "hasTitle"           - Boolean value that indicates whether the panel has a title.
                            Default value is true.

  3. "panelTitle"         - Title for the panel.
                            Default value is empty string.

  4. "panelTitleId"       - Id for panel title.
                            Default value is empty string.

  5. "hasTitleBorder"     - Boolean value that indicates whether the panel title has a bottom border.
                            Default value is true.

  6. "isTogglableTitle"   - Boolean value that indicates whether the panel is a togglable one.
                            Default value is true.

  7. "panelContentId"     - String value that indicates the id for the panel content. Used for toggle feature.
                            Default value is panelContent.

  8. "panelId"            - String value that indicates the id for the panel.
                            Default value is panel.

  Author: mmanickam
  Date: Apr 30, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib prefix="fmt" uri="/WEB-INF/fmt.tld" %>
<%@ taglib prefix="c" uri="/WEB-INF/c.tld" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<c:if test="${empty panelId}">
    <c:set var="panelId" value="panel"></c:set>
</c:if>

<c:if test="${empty isPanelWithBorder}">
    <c:set var="isPanelWithBorder" value="true"></c:set>
</c:if>

<c:if test="${empty hasTitle}">
    <c:set var="hasTitle" value="true"></c:set>
</c:if>

<c:if test="${empty panelTitle}">
    <c:set var="panelTitle" value=""></c:set>
</c:if>

<c:if test="${empty panelTitleId}">
    <c:set var="panelTitleId" value=""></c:set>
</c:if>

<c:if test="${empty hasTitleBorder}">
    <c:set var="hasTitleBorder" value="true"></c:set>
</c:if>

<c:if test="${empty isTogglableTitle}">
    <c:set var="isTogglableTitle" value="true"></c:set>
</c:if>

<c:if test="${empty panelContentId}">
    <c:set var="panelContentId" value="panelContent"></c:set>
</c:if>

<c:if test="${empty panelCollapseTitle}">
    <c:set var="panelCollapseTitle" value=""></c:set>
</c:if>

<c:if test="${empty isPanelCollaspedByDefault}">
    <c:set var="isPanelCollaspedByDefault" value="false"></c:set>
</c:if>

<c:set var="panelDefaultStyle" value="display:block;"></c:set>
<c:set var="panelDefaultCssClass" value=""></c:set>
<c:if test="${isPanelHiddenByDefault == true}">
    <c:set var="panelDefaultStyle" value="display:none;"></c:set>
    <c:set var="panelDefaultCssClass" value="dti-hide"></c:set>
</c:if>

<!-- Panel : Start -->
<c:if test="${isPanelWithBorder==true}">
  <div id="<c:out value='${panelId}'></c:out>" class="panel <c:out value="${panelDefaultCssClass}"></c:out>"  style='<c:out value="${panelDefaultStyle}"></c:out>'>
</c:if>
<c:if test="${isPanelWithBorder==false}">
  <div id="<c:out value='${panelId}'></c:out>"  class="panel <c:out value="${panelDefaultCssClass}"></c:out>" style='<c:out value="${panelDefaultStyle}"></c:out>border:none;" >
</c:if>

  <!-- Panel Title : Start -->
  <c:if test="${hasTitle==true}">

      <c:set var="panelTitleDivId" value=""></c:set>
      <c:if test="${hasTitleBorder==true}">
          <c:set var="panelTitleDivId" value="panelTitleWithBorder"></c:set>
      </c:if>

      <div id="<c:out value='${panelTitleDivId}'></c:out>" style="margin:0; padding-bottom: 0px; margin-bottom:0px;">
        <c:if test="${isTogglableTitle==true}">
          <c:set var="collapseIndicatorClassName" value="panelUpTitle"></c:set>
          <c:set var="panelTitleToDisplay" value="${panelTitle}"></c:set>
          <c:if test="${isPanelCollaspedByDefault}">
              <c:set var="collapseIndicatorClassName" value="panelDownTitle"></c:set>
              <c:set var="panelTitleToDisplay" value="${panelCollapseTitle}"></c:set>
          </c:if>
          <a href="javascript:void(0);" class='<c:out value='${collapseIndicatorClassName}'></c:out>'
             onclick="togglePanel(this, '<c:out value='${panelContentId}'></c:out>', '<c:out value='${panelTitleId}'></c:out>', '<c:out value='${panelTitle}'></c:out>', '<c:out value='${panelCollapseTitle}'></c:out>');">
              <span id="<c:out value='${panelTitleId}'></c:out>" class="panelTitle">
                  <c:out value='${panelTitleToDisplay}'></c:out>
                  <c:if test="${panelTitleToDisplay==''}">
                    &nbsp;
                  </c:if>
              </span>
          </a>
        </c:if>
        <c:if test="${isTogglableTitle==false}">
            <span id="<c:out value='${panelTitleId}'></c:out>" class="panelTitle">
                <c:out value='${panelTitle}'></c:out>
                <c:if test="${panelTitle==''}">
                  &nbsp;
                </c:if>
            </span>
        </c:if>
      </div>
  </c:if>
  <!-- Panel Title : End -->

  <!-- Panel Content : Start -->
  <c:set var="panelContentStyle" value="display:inline; width:100%"></c:set>
  <c:set var="panelContentCssClass" value=""></c:set>
  <c:if test="${isPanelCollaspedByDefault}">
      <c:set var="panelContentStyle" value="display:none; width:100%"></c:set>
      <c:set var="panelContentCssClass" value="dti-hide"></c:set>
  </c:if>
  <div id="<c:out value='${panelContentId}'></c:out>" style='<c:out value="${panelContentStyle}"></c:out>'  class='<c:out value="${panelContentCssClass}"></c:out>'>
      <table width=99%>


<%-- Remove All JSTL Variables that has been set --%>
<c:remove var="isPanelWithBorder"></c:remove>
<c:remove var="hasTitle"></c:remove>
<c:remove var="panelTitle"></c:remove>
<c:remove var="panelTitleId"></c:remove>
<c:remove var="hasTitleBorder"></c:remove>
<c:remove var="isTogglableTitle"></c:remove>
<c:remove var="panelContentId"></c:remove>
<c:remove var="panelCollapseTitle"></c:remove>
<c:remove var="isPanelCollaspedByDefault"></c:remove>
<c:remove var="isPanelHiddenByDefault"></c:remove>

