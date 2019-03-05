<%@ page language="java" import="dti.oasis.struts.IOasisAction"%>
<%--
  Description:

  Author:
  Date:


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  07/05/2007       James       Added UI2 Changes
  09/05/2007       James       Remove UIStyleEdition
  09/28/2010       wfu         111776: Replaced hardcode string with resource definition
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%@include file="/core/header.jsp" %>
<form>
    <tr>
        <td>
            <oweb:panel hasTitle="false" panelContentId="cishome">
                <tr>
                    <td><b><fmt:message key="ci.home.page.description"/>
                    </b></td>
                </tr>
            </oweb:panel>
        </td>
    </tr>
<jsp:include page="/core/footer.jsp"/>
