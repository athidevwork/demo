<%@ page language="java" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.ci.addressmgr.AddressFields" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%--
  Description:

  Author: eouyang
  Date: 4/19/2018


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  06/28/2018       dpang       194157: Add buildNumber parameter to static file references to improve performance
  11/09/2018       Elvin       Issue 195835: add system parameter COUNTRY_CODE_CONFIG
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>

<script type='text/javascript' src="<%=Module.getCISPath(request)%>/addressmgr/js/addressCommon.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>
<script type="text/javascript" src="<%=Module.getCSPath(request)%>/js/csZipLookup.js?<%=ApplicationContext.getInstance().getBuildNumberParameter()%>"></script>

<oweb:constant constantClass="dti.ci.addressmgr.AddressFields"/>

<input type="hidden" name="<%=AddressFields.SYS_PARAM_CS_VALIDATE_ADDXREF%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_CS_VALIDATE_ADDXREF, "N")%>"/>
<input type="hidden" name="<%=AddressFields.SYS_PARAM_ADDR_EFF_ATTER_TODAY%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_ADDR_EFF_ATTER_TODAY, "N")%>"/>
<input type="hidden" name="<%=AddressFields.SYS_PARAM_COUNTRY_CODE_CONFIG%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_CONFIG, "USA")%>"/>

<input type="hidden" name="<%=AddressFields.KEY_ZIP_CODE_ENABLE%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_ZIP_CODE_ENABLE, "N")%>"/>
<input type="hidden" name="<%=AddressFields.KEY_ZIP_OVERRIDE_ADDR%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_ZIP_OVERRIDE_ADDR, "N")%>"/>
<input type="hidden" name="<%=AddressFields.KEY_CS_SHOW_ZIPCD_LIST%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_CS_SHOW_ZIPCD_LIST, "N")%>"/>
<input type="hidden" name="<%=AddressFields.KEY_COUNTRY_CODE_USA%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_USA, "USA")%>"/>
<input type="hidden" name="<%=AddressFields.KEY_COUNTRY_CODE_CAN%>" value="<%=SysParmProvider.getInstance().getSysParm(AddressFields.SYS_PARAM_COUNTRY_CODE_CAN, "CAN")%>"/>