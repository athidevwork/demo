<%@ page language="java" %>
<%--
  Description:

  Author: kshen
  Date: Mar 3, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/16/2018       dzhang      195835: Grid replacement
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ include file="/core/headerpopup.jsp" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<jsp:useBean id="dataBean" class="dti.oasis.util.BaseResultSet" scope="request"/>
<jsp:useBean id="gridHeaderBean" class="dti.oasis.tags.XMLGridHeader" scope="request"/>

<jsp:include page="/cicore/common.jsp"/>

<form action="maintainSubProducer.do" method="POST" name="SubProducerListForm">
    <tr>
        <td>
            <oweb:panel panelContentId="panelContentForHeader" hasTitle="false" panelTitleLayerId="CI_SUB_PRODUCER_GH">
                <tr>
                    <td>
                        <c:set var="gridDisplayFormName" value="SubProducerListForm" scope="request"/>
                        <%@ include file="/core/gridDisplay.jsp" %>
                    </td>
                </tr>
            </oweb:panel>
        </td>
    </tr>

    <tr>
        <td align='center' style="padding-top:6px">
            <oweb:actionGroup actionItemGroupId="CI_SUB_PRODUCER_AIG" cssColorScheme="blue"
                              layoutDirection="horizontal">
            </oweb:actionGroup>
        </td>
    </tr>

<jsp:include page="/core/footerpopup.jsp"/>