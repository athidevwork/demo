<%@ page import="java.util.ArrayList" %>
<%@ page import="java.awt.*" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page language="java" %>
<%--
  Description: popup participant page

  Author: gjli
  Date: May 17, 2005


  Revision Date    Revised By  Description
  ---------------------------------------------------
  07/02/2007       James       Added UI2 Changes
  09/07/2007       Kenney      remove UIStyleEdition;
                               change to panel tag;
  10/06/2010       wfu         111776: Replaced hardcode string with resource definition
  10/06/2010       wfu         111776: Fixed set messages issue.
  ---------------------------------------------------
  (C) 2004 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file="/core/headerpopup.jsp" %>
<%
    ArrayList picklist = (ArrayList) request.getAttribute("lovEntityRole");
    int listsize = picklist.size();
    String entityFk="";
    String cityFieldId="";
    String entityNameFieldId="";
%>
<script language='javascript'>
    var entityFk = '';
    var entityName = '';
    var city = '';
    var stateCode = '';
    var countryCode = '';
    var educationpopupind = '';
    setMessage("ci.common.warn.page.close", "<%= MessageManager.getInstance().formatMessage("ci.common.warn.page.close") %>");
    if (<%=listsize%>==0) {
        //alert('No data was found for this list of values')
        baseCloseWindow();
        window.opener.focus();
        window.opener.alert('<fmt:message key="ciEducation.noData.msg" />');
        //        noDataAlertMsg = '<fmt:message key="ciEducation.noData.msg"/>';
    }else{
        window.resizeTo(630, 250 +<%=(listsize*25 > 250)?250:listsize*25%>);
        window.focus();
    }
    window.moveTo(10, 10);
    function setvalue(fcity, fstateCode, fcountryCode, fentityFk, fentityName, feducationpopupind) {
        city = fcity;
        stateCode = fstateCode;
        countryCode = fcountryCode;
        entityFk = fentityFk;
        entityName = fentityName;
        educationpopupind = feducationpopupind;
    }

    function ciEducationbtnClick() {
        // chose a row
        var flg = 0;
        if (document.all("chkCSELECT_IND") != null) {
            if (typeof document.all("chkCSELECT_IND").length == "undefined") {
                if (document.all("chkCSELECT_IND").checked == true) {
                    flg = 1;
                }
            } else {
                for (i = 0; i < document.all("chkCSELECT_IND").length; i++) {
                    if (document.all("chkCSELECT_IND")[i].checked == true) {
                        flg = 1;
                    }
                }
            }
            if (flg == 0) {
                alert('<fmt:message key="ciEducation.selectedData.msg" />');
                //                selectedDataAlertMsg = '<fmt:message key="ciEducation.selectedData.msg"/>';
                return;
            }
        }
        if (!opener.window.updateField) {
            alert(getMessage("ci.common.warn.page.close", new Array("\n")));
            //            closedpageAlertMsg = '<fmt:message key="ciEducation.closedpage.msg"/>';
            baseCloseWindow();
            return;
        }
        // add some parameters into education page
        window.opener.updateField(city, stateCode, countryCode, entityFk, entityName, educationpopupind);
        window.opener.focus();
        baseCloseWindow();

    }


</script>

<form>
    <tr>
        <td>
            <table width="99%" cellpadding="4" cellspacing="0" border="0">
                <tr>
                    <td colspan="6">
                        <oweb:panel panelContentId="panelContentForInstitutionName" hasTitle="false">
                            <tr>
                                <td class="top left right bottom">
                                    <table cellpadding="0" width="100%" border="0">
                                        <%
                                            if (listsize == 0) {
                                        %>
                                        <span class="oasis_formlabelreq"><fmt:message key="ciEducation.noData.msg" /></span>
                                        <%  } else {%>
                                        <logic:iterate id="map" collection="<%=picklist%>" type="java.util.HashMap">
                                            <tr>
                                                <td align="right">
                                                    <%  entityFk = (String)map.get("entityFk");   %>
                                                    <%-- create 2 hidden fields for each entity:  --%>
                                                    <input name="<%= (entityFk + "_city") %>" id=" <%= (entityFk + "_city") %>" type=hidden value="<%= (String)map.get("city") %>">
                                                    <input name="<%= (entityFk + "_entityName") %>" id="<%= (entityFk + "_entityName") %>" type=hidden value="<%= (String)map.get("entityName") %>">
                                                    <input type='radio' name="chkCSELECT_IND"
                                                           value="<%=entityFk%>"
                                                           onclick='javascript:setvalue(getSingleObject("<%=(entityFk + "_city")%>").value,
                                                                                        "<%=(String)map.get("stateCode")%>",
                                                                                        "<%=(String)map.get("countryCode")%>",
                                                                                        "<%=(String)map.get("entityFk")%>",
                                                                                        getSingleObject("<%=(entityFk + "_entityName")%>").value,
                                                                                        "<%=(String)map.get("EducationPopupIND")%>")'>
                                                </td>
                                                <td align="left">
                                                    <span class="oasis_formlabelreq">
                                                        <%=(String) map.get("entityName")%>
                                                    </span>
                                                </td>
                                            </tr>
                                        </logic:iterate>
                                        <%
                                           }                                                      
                                        %>
                                    </table>
                                </td>
                            </tr>
                        </oweb:panel>
                    </td>
                </tr>
                <tr>
                    <td class="headerbox top left right bottom">
                        <table cellpadding="0" width="100%">
                            <tr>
                                <td align="center"><input type="button" value="OK" class="buttons"
                                                          onClick="javascript:ciEducationbtnClick()">
                                    &nbsp;<input type="button" value="Cancel" class="buttons" 
                                                 onClick="javascript:baseCloseWindow()">
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
</form>
<jsp:include page="/core/footerpopup.jsp"/>
