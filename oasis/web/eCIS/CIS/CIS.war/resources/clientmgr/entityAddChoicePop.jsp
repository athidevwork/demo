<%@ page import="dti.ci.clientmgr.EntityAddFields" %>
<%--
  Description:

  Author: HXY
  Date: Nov 8, 2005

  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/04/2007       James       Added UI2 Changes
  09/05/2007       James       remove UIStyleEdition;
                               change to panel tag;
                               change to compiledFormField page
  12/08/2008       Leo         change for issue 88609.
  11/10/2011       kshen       Issue 126394.
  03/20/2014                   Issue 151540
  10/30/2014       Elvin       Issue 158667: pass in country code/email address from search
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@include file="/core/headerpopup.jsp" %>
<script type='text/javascript' src="<%=cisPath%>/clientmgr/js/entityAddChoicePop.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<FORM name="CIEntityAddChoicePopForm" method="POST">
    <input type="hidden" name="<%=EntityAddFields.LNM%>" value="<%= request.getParameter(EntityAddFields.LNM)%>">
    <input type="hidden" name="<%=EntityAddFields.FNM%>" value="<%= request.getParameter(EntityAddFields.FNM)%>">
    <input type="hidden" name="<%=EntityAddFields.TAXID%>" value="<%= request.getParameter(EntityAddFields.TAXID)%>">
    <input type="hidden" name="<%=EntityAddFields.DOB%>" value="<%= request.getParameter(EntityAddFields.DOB)%>">
    <input type="hidden" name="<%=EntityAddFields.CLS%>" value="<%= request.getParameter(EntityAddFields.CLS)%>">
    <input type="hidden" name="<%=EntityAddFields.SUB_CLS%>" value="<%= request.getParameter(EntityAddFields.SUB_CLS)%>">
    <input type="hidden" name="<%=EntityAddFields.SUB_TYPE%>" value="<%= request.getParameter(EntityAddFields.SUB_TYPE)%>">
    <input type="hidden" name="<%=EntityAddFields.CITY%>" value="<%= request.getParameter(EntityAddFields.CITY)%>">
    <input type="hidden" name="<%=EntityAddFields.ST%>" value="<%= request.getParameter(EntityAddFields.ST)%>">
    <input type="hidden" name="<%=EntityAddFields.ZIP%>" value="<%= request.getParameter(EntityAddFields.ZIP)%>">
    <input type="hidden" name="<%=EntityAddFields.PAGE_SOURCE%>" value="<%= request.getParameter(EntityAddFields.PAGE_SOURCE)%>">
    <input type="hidden" name="<%=EntityAddFields.DBA_NAME%>" value="<%= request.getParameter(EntityAddFields.DBA_NAME)%>">
    <input type="hidden" name="<%=EntityAddFields.COUNTRY_CODE%>" value="<%= request.getParameter(EntityAddFields.COUNTRY_CODE)%>">
    <input type="hidden" name="<%=EntityAddFields.EMAIL_ADDRESS%>" value="<%= request.getParameter(EntityAddFields.EMAIL_ADDRESS)%>">
    <input type="hidden" name="<%=EntityAddFields.COUNTY%>" value="<%= request.getParameter(EntityAddFields.COUNTY)%>">
<tr>
    <td colspan="6">
        <jsp:include page="/core/compiledFormFields.jsp">
            <jsp:param name="headerText" value=""/>
            <jsp:param name="isGridBased" value="false"/>
            <jsp:param name="divId" value="EntityType"/>
            <jsp:param name="excludeAllLayers" value="true"/>
        </jsp:include>
    </td>
</tr>

<tr><td align="center" colspan='6' style="padding-top:5px">
    <oweb:actionGroup actionItemGroupId="CI_ENT_ADD_POP_AIG"
                    cssColorScheme="blue" layoutDirection="horizontal">
    </oweb:actionGroup>
</td></tr>
    
</FORM>
<jsp:include page="/core/footerpopup.jsp"/>
