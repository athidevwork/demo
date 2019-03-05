<%@ page import="dti.oasis.util.*" %>
<%@ page language="java" %>


<%--
  Description: Add  Page.

  Author: Michael Li
  Date: July 04, 2011

  Revision Date    Revised By  Description
  ---------------------------------------------------
  10/29/2013       ldong      Issue 138932
  3/9/2015         bzhu       Issue 160643. Make page title configurable.
  01/05/2017       ylu        Issue 180818: make buttons configuarable
  09/19/2018       dpang      Issue 195835: get parent window by using getParentWindow.
  ---------------------------------------------------
  (C) 2008 Delphi Technology, inc. (dti)
--%>
<%@ include file="/core/headerpopup.jsp" %>

<%@ include file="/CI_common.jsp" %>

<FORM action="" method="POST">
    <tr>
        <td width="100%" align="left">

            <oweb:panel panelContentId="panelContentForFilter" panelTitleId="panelTitleIdForFilter" hasTitle="false">
                <tr>
                    <td width="100%">
                        <table id="tableForAddEntityFieldSelect">
                            <tr>
                                <script type='text/javascript'> var REQ_defultFields = false;</script>
                                <td align='right'><span id='defaultFieldsFLDLABEL' class='oasis_formlabel'>Default Fields:</span></td>
                                <td align='left'>
                                    <table width='100%'>
                                        <tr>
                                            <TD><input type="checkbox" name="defaultFields" value="address" checked="checked"
                                                       onclick="baseOnChangeDefaultFields(this);"><span class='oasis_formlabel'>Address</span></TD>
                                            <TD><input type="checkbox" name="defaultFields" value="phone" checked="checked"
                                                       onclick=""><span class='oasis_formlabel'>Phone</span></TD>
                                            <TD><input type="checkbox" name="defaultFields" value="classification" checked="checked"
                                                       onclick=""><span class='oasis_formlabel'>Classification</span></TD>
                                        </tr>
                                    </table>
                                </td>

                            </tr>
                        </table>
                    </td>
                </tr>
            </oweb:panel>

        </td>
    </tr>

    <tr>
        <td align=center>
            <oweb:actionGroup actionItemGroupId="CI_CONTINUE_ADD_AIG" layoutDirection="horizontal"/>
        </td>
    </tr>

    <script type='text/javascript'>
//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function baseOnChangeDefaultFields(field) {
    if (field.name == "defaultFields")
        var obj = getObject('defaultFields');
    for (var i = 0; i < obj.length; i++) {
        if (obj[i]) {
            if (obj[i].value == 'address') {
                if (!obj[i].checked) {
                    obj[i + 1].checked = false;
                    obj[i + 1].disabled = true;
                    break;
                }
                if (obj[i].checked) {
                    obj[i + 1].disabled = false;
                    break;
                }
            }
        }
    }
}

function dealwith() {
    closeWindow(function () {
        var parentWindow = getParentWindow();
        if (parentWindow) {
            parentWindow.dealWithAddedFields(getObject('defaultFields'));
        }
    });
}

function dealwithCancel() {
    closeWindow(function () {
        var parentWindow = getParentWindow();
        if (parentWindow) {
            parentWindow.dealwithCancel();
        }
    });
}
    </script>

<jsp:include page="/core/footerpopup.jsp"/>
