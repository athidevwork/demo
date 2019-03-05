<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page language="java"%>
<%--
  Description:  Used to render the fields for a common header, for example,
  the case header and claim header in eCM case folder and claim folder pages,
  respectively.

  Author: GCCarney
  Date: Jul 16, 2007

  Parameters:

  "beanName" - Value to be set for bean name (passed as name attribute to text
      and text area tags;  see headerDataSpecialLayout.jsp and tagfactory.jsp).

  "headerFirstColDivStyleClass" - Style class to be used used for the first
      column div;  if not passed in, "headerDataFieldsFirstColBorder" will
      be used as value.

  "headerSecondColDivStyleClass" - Style class to be used used for the second
      column div;  if not passed in, "headerDataFieldsSecondColBorder" will
      be used as value.

  "headerThirdtColDivStyleClass" - Style class to be used used for the third
      column div;  if not passed in, "headerDataFieldsThirdColBorder" will
      be used as value.

  Revision Date    Revised By  Description
  ---------------------------------------------------


  ---------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean id="headerFieldsMap" class="dti.oasis.tags.OasisFieldsHeader" scope="request"/>
<%
    // Get the parameters.
    String beanName = request.getParameter("beanName");
    pageContext.setAttribute("beanName", beanName);
    String firstColDivStyleClass = request.getParameter("headerFirstColDivStyleClass");
    String secondColDivStyleClass = request.getParameter("headerSecondColDivStyleClass");
    String thirdColDivStyleClass = request.getParameter("headerThirdColDivStyleClass");
    if (StringUtils.isBlank(firstColDivStyleClass)) {
        firstColDivStyleClass = "headerDataFieldsFirstColBorder";
    }
    if (StringUtils.isBlank(secondColDivStyleClass)) {
        secondColDivStyleClass = "headerDataFieldsSecondColBorder";
    }
    if (StringUtils.isBlank(thirdColDivStyleClass)) {
        thirdColDivStyleClass = "headerDataFieldsThirdColNoBorder";
    }

    pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_NO_ALIGN, "false");
    pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_SPAN_CLASS, "headerDataLabelSpan");
%>

<tr>
    <td>
        <div>
            <table id="formFieldsTableForHeaderFieldsAll">
                <tr>
                    <td>
                        <!-- 1st column fields -->
                        <%
                            pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_TD_CLASS, OasisTagHelper.HEADER_FIELD_FIRSTCOL);
                        %>
                        <div class="<%=firstColDivStyleClass%>">
                            <table id="formFieldsTableForHeaderFieldsFirst">
                            <logic:iterate id="field" collection="<%=headerFieldsMap.getFieldsMapFirstCol().getPageFields()%>" type="dti.oasis.tags.OasisFormField" >
                                <%
                                    if (field.getDisplayType().equals("EMPTY")) {
                                        // Render empty field.
                                %>
                                <tr><td class="<%= OasisTagHelper.HEADER_FIELD_FIRSTCOL %>">&nbsp;</td><td>&nbsp;</td></tr>
                                <%
                                    }
                                    else {
                                %>
                                <tr>
                                    <%@include file="headerDataSpecialTagFactory.jsp"%>
                                </tr>
                                <%
                                    }
                                %>
                            </logic:iterate>
                            </table>
                        </div>
                    </td>
                    <!-- 2nd column fields -->
                    <%
                        pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_TD_CLASS, OasisTagHelper.HEADER_FIELD_SECONDCOL);
                    %>
                    <td>
                        <div class="<%=secondColDivStyleClass%>">
                            <table id="formFieldsTableForHeaderFieldsSecond">
                            <logic:iterate id="field" collection="<%=headerFieldsMap.getFieldsMapSecondCol().getPageFields()%>" type="dti.oasis.tags.OasisFormField" >
                                <%
                                    if (field.getDisplayType().equals("EMPTY")) {
                                        // Render empty field.
                                %>
                                <tr><td class="<%= OasisTagHelper.HEADER_FIELD_SECONDCOL %>">&nbsp;</td><td>&nbsp;</td></tr>
                                <%
                                    }
                                    else {
                                %>
                                <tr>
                                    <%@include file="headerDataSpecialTagFactory.jsp"%>
                                </tr>
                                <%
                                    }
                                %>
                            </logic:iterate>
                            </table>
                        </div>
                    </td>
                    <!-- 3rd column fields -->
                    <%
                        pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_TD_CLASS, OasisTagHelper.HEADER_FIELD_THIRDCOL);
                    %>
                    <td>
                        <div class="<%=thirdColDivStyleClass%>">
                            <table id="formFieldsTableForHeaderFieldsThird">
                            <logic:iterate id="field" collection="<%=headerFieldsMap.getFieldsMapThirdCol().getPageFields()%>" type="dti.oasis.tags.OasisFormField" >
                                <%
                                    if (field.getDisplayType().equals("EMPTY")) {
                                        // Render empty field.
                                %>
                                <tr><td class="<%= OasisTagHelper.HEADER_FIELD_THIRDCOL %>">&nbsp;</td><td>&nbsp;</td></tr>
                                <%
                                    }
                                    else {
                                %>
                                <tr>
                                    <%@include file="headerDataSpecialTagFactory.jsp"%>
                                </tr>
                                <%
                                    }
                                %>
                            </logic:iterate>
                            </table>
                        </div>
                    </td>
                </tr>
                <%
                    if (headerFieldsMap.getFieldsMapDisplayAtBottom() != null && headerFieldsMap.getFieldsMapDisplayAtBottom().size() >= 1) {
                        pageContext.setAttribute("displayAtBottomFields", headerFieldsMap.getFieldsMapDisplayAtBottom());
                        // Iterate through the fields to be shown at the bottom.
                        // In eCM case header, there will be one field:
                        // caseHeaderPrimaryClaimHeadline.
                        pageContext.setAttribute(OasisTagHelper.HEADER_FIELD_LABEL_TD_CLASS, "headerDataLabelCellDisplayAtBottom");
                %>
                <tr>
                    <td colspan="10">
                        <!-- fields displayed at botton of header -->
                        <div>
                            <table id="formFieldsTableForHeaderFieldsBottom">
                                <logic:iterate id="field" collection="<%=headerFieldsMap.getFieldsMapDisplayAtBottom().getPageFields()%>" type="dti.oasis.tags.OasisFormField" >
                                    <tr>
                                        <%@include file="headerDataSpecialTagFactory.jsp"%>
                                    </tr>
                                </logic:iterate>
                            </table>
                        </div>
                    </td>
                </tr>
                <%
                    }
                    pageContext.removeAttribute(OasisTagHelper.HEADER_FIELD_LABEL_NO_ALIGN);
                    pageContext.removeAttribute(OasisTagHelper.HEADER_FIELD_LABEL_TD_CLASS);
                    pageContext.removeAttribute(OasisTagHelper.HEADER_FIELD_LABEL_SPAN_CLASS);
                %>
            </table>
        </div>
    </td>
</tr>
<%
    boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(request);
    if(useJqxGrid){
%>
<tr class="dti-hide">
<%
    } else {
%>
<tr style="display:none">
<%
    }
%>
<!-- hidden header fields -->
<logic:iterate id="field" collection="<%=headerFieldsMap.getFieldsMapHidden().getPageFields()%>" type="dti.oasis.tags.OasisFormField" >
    <%@include file="headerDataSpecialTagFactory.jsp"%>
</logic:iterate>
</tr>
