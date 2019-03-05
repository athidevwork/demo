<%@ page import="dti.ci.helpers.ICIConstants" %>
<%@ page import="dti.oasis.tags.WebLayer" %>
<%@ page import="org.apache.struts.Globals" %>
<%@ page import="org.apache.struts.taglib.html.Constants" %>
<%@ page import="dti.oasis.messagemgr.Message" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page language="java" %>
<%--
  Description:

  Author: ldong
  Date: Mar 19, 2008


  Revision Date    Revised By  Description
  ---------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance

  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:include page="/cicore/common.jsp"/>
<script type="text/javascript" src="<%=cisPath%>/demographic/clientmgr/mntduplicate/js/ciMaintainEntityDuplicate.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>

<form name="maintainEntityDuplicateForm" action="ciMaintainEntityDuplicate.do" method=post>
    <%@ include file="/cicore/commonFormHeader.jsp" %>
    <input type="hidden" name="<%=ICIConstants.PK_PROPERTY%>" value="<%=request.getParameter(ICIConstants.PK_PROPERTY)%>">
    <input type="hidden" name="duplicateEntityPk" value="">
    <tr>
        <td colspan=8>
            <oweb:message/>
        </td>
    </tr>

    <logic:iterate id="layer" collection="<%=fieldsMap.getLayers()%>" type="dti.oasis.tags.WebLayer">
        <%
            if (layer.getLayerId().equals("CI_ENT_MNT_DUP_CLIENT")) {
        %>
        <tr>
            <td align=left>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="includeLayerIds" value="CI_ENT_MNT_DUP_CLIENT"/>
                    <jsp:param name="headerTextLayerId" value="CI_ENT_MNT_DUP_CLIENT"/>
                    <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    <jsp:param name="divId" value="layerfields"/>
                    <jsp:param name="excludePageFields" value="true"/>
                </jsp:include>
            </td>
        </tr>
        <%
            }
            if (layer.getLayerId().equals("CI_ENT_MNT_DUP_MERGE_ITM")) {
        %>
        <tr>
            <td>
                <jsp:include page="/core/compiledFormFields.jsp">
                    <jsp:param name="isGridBased" value="false"/>
                    <jsp:param name="includeLayerIds" value="CI_ENT_MNT_DUP_MERGE_ITM"/>
                    <jsp:param name="headerTextLayerId" value="CI_ENT_MNT_DUP_MERGE_ITM"/>
                    <jsp:param name="isLayerVisibleByDefault" value="true"/>
                    <jsp:param name="divId" value="mergelayerfields"/>
                    <jsp:param name="excludePageFields" value="true"/>
                </jsp:include>
            </td>
        </tr>
        <%
            }
        %>
    </logic:iterate>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_ENT_DUP_PAGE_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>
    <jsp:include page="/core/footerpopup.jsp"/>
