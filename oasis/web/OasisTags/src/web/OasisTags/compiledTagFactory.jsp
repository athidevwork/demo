<%@ page language="java" %>
<%--
    1. This page should be included via an jsp:include directive
    <jsp:include page="compiledTagFactory.jsp" >

    2. An OasisFormField object must have been declared prior to the
    include directive and the object must be in a variable called
    "field".

    3. If the field is meant to be a dropdown, radiobutton, checkbox or multibox
    where a list of values is required, then the list of values will be searched
    for based on the fieldId + "LOV". For example, if the fieldId is "state" then
    the list of values should be found either in the request
    or session scope with the name "stateLOV".

    5. For CheckBoxes, the default value attribute will be set to 'Y'.  If you want
    to override that, populate a value named fieldId + "CheckBoxValue" in the
    request.  For example, if the fieldId is "yesNo" then set the value:
    request.setAttribute("yesNoCheckBoxValue","whateveryouwanthere");

    6. For Multiboxes and RadioButtons, the maxInRow attribute will be set to the
    number of records in the list of Values (see #2).  If you want to override that,
    populate a value named fieldId + "MaxInRow" in the request.  For
    example, if the fieldId is "interests" and you want, at most, 3 choices to appear
    in a single table row, then set the maxInRow attribute:
    request.setAttribute("interestsMaxInRow","3");

    7. Checkboxes are normally placed in a single table cell.  If, for the sake of
    good alignment, want a specific checkbox to span multiple table cells, populate
    a value named fieldId + "CheckBoxSpan" in the request.  For example, if the fieldId is
    "primaryAddress" and you want it to span 2 table cells, then set the value:
    request.setAttribute("primaryAddressCheckBoxSpan","2");

    Additionally, you can force all checkboxes to span multiple table cells by
    populating a value named "CheckBoxSpan" in the request.  For example, to force
    all checkboxes to span 2 table cells:
    request.setAttribute("CheckBoxSpan","2");
    Note that if you set the span for a specific field AND a span for all (CheckBoxSpan),
    the field specific span will be used for that field.

    Note: The logic of CheckBoxSpan is removed.

    8. The datasrc attribute should be in scope named "datasrc".  It should
    include a "#" in the beginning.  The datafld attribute should also be in the
    pageContext named "datafld".
    e.g. request.setAttribute("datasrc","#myxml");
         request.setAttribute("datafld","myFieldId");
    9. The showLabel attribute should be in scope named fieldId+"showLabel".
    e.g. request.setAttribute("myFieldIdshowLabel","false");
    10. The isInTable attribute should be in scope named fieldId+"isInTable".
    e.g. request.setAttribute("myFieldIdisInTable","false");
    11. The beanName attribute should be in scope named "beanName".  If it is not found
   	then the fieldId will be used for the beanName.
    eg. request.setAttribute("beanName", "myForm");
    12. The formName attribute should be in scope named "formName".  If it is not found
    then "forms[0]" will be used for the formName.  This is used by the onchange handler.
    13. The fieldColSpan attribute should be in scope named fieldId"COLSPAN".
        e.g. request.setAttribute("myFieldIdCOLSPAN","3");
    14. The numDecimals attribute should be in scope named fieldId"NUMDEC".
    e.g. request.setAttribute("myFieldIdNUMDEC","3");


    Revision Date    Revised By  Description
    -----------------------------------------------------------------------------

    -----------------------------------------------------------------------------
--%>
<%@ page import="dti.oasis.util.LogUtils,
                 dti.oasis.util.StringUtils,
                 org.apache.struts.taglib.TagUtils,
                 javax.servlet.jsp.JspException,
                 java.util.ArrayList,
                 java.util.logging.Logger" %>
<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<jsp:useBean id="field" class="dti.oasis.tags.OasisFormField" scope="request"/>

<%
    Logger l = LogUtils.enterLog(getClass(), "jsp", field);
    if (field == null)
        throw new JspException("Invalid Form Field");

    String type = field.getDisplayType();
    ArrayList lov = null;
    String checkBoxValue = null;
    String maxInRow = null;
    boolean showLabel = true;
    boolean isInTable = true;
    String datasrc = null;
    String datafld = null;
    String beanName = null;
    String numDecimals = null;
    String fieldColSpan = "1";

    TagUtils tagUtils = TagUtils.getInstance();

    /* If we have no type, we will set it to TEXT and make it hidden*/
    if (type == null) {
        field.setDisplayType("TEXT");
        type = "TEXT";
        field.setIsVisible(false);
    }
    /* If we are a SELECT or MULTISELECT, find the list of values ArrayList
       in some scope under the key "fieldIdLOV" */
    if (!(type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FORMATTEDNUMBER") || type.equals("FINDERTEXT") || type.equals("EMAILTEXT") || type.equals("TEXTAREA") || type.equals("NOTETEXT")  || type.equals("TEXTAREAPOPUP"))) {
        try {
            lov = (ArrayList) tagUtils.lookup(pageContext, field.getFieldId() + "LOV", null);
        }
        catch (ClassCastException ce) {
            throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
        }
        if (lov == null)
            throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
        l.fine(getClass().getName() + ": lov=" + lov);
    }
    try {
        numDecimals = (String) tagUtils.lookup(pageContext, field.getFieldId() + "NUMDEC", null);
    }
    catch (ClassCastException e) {
        numDecimals = "";
    }

    try {
        fieldColSpan = (String) tagUtils.lookup(pageContext, field.getFieldId() + "COLSPAN", null);
    }
    catch (ClassCastException e) {
        fieldColSpan = "";
    }

    String formName = null;
    try {
        formName = (String) tagUtils.lookup(pageContext, "formName", null);
        if (StringUtils.isBlank(formName))
            formName = "forms[0]";
    }
    catch (ClassCastException ce) {
        formName = "forms[0]";
    }

    String dataType = field.getDatatype();
    String onFocus = new StringBuffer("baseOnFocus('" + dataType + "', event);").toString();
    String onKeyPress = new StringBuffer("baseOnKeyPress('" + dataType + "', event);").toString();
    String onKeyDown = new StringBuffer("baseOnKeyDown('" + dataType + "', event);").toString();
    String onBlur = new StringBuffer("baseOnBlur('" + dataType + "', event);").toString();
    String onChange = new StringBuffer("baseOnChange('" + dataType + "', event);").toString();
    String onKeyUp = new StringBuffer("baseOnKeyUp('" + dataType + "', event);").toString();

/*    String onChange = new StringBuffer("handleOnChange(document.").append(formName).append('.').
        append(field.getFieldId()).append(')').toString();*/
//    String onChange = "handleOnChange(this)";
    try {
        String b = (String) tagUtils.lookup(pageContext, field.getFieldId() + "showLabel", null);
        showLabel = (b != null && b.equalsIgnoreCase("false")) ? false : true;
    }
    catch (ClassCastException ce) {
        showLabel = true;
    }
    l.fine(getClass().getName() + ": showLabel=" + showLabel);
    try {
        String b = (String) tagUtils.lookup(pageContext, field.getFieldId() + "isInTable", null);
        isInTable = (b != null && b.equalsIgnoreCase("false")) ? false : true;
    }
    catch (ClassCastException ce) {
        isInTable = true;
    }
    l.fine(getClass().getName() + ": isInTable=" + isInTable);
    try {
        datasrc = (String) tagUtils.lookup(pageContext, "datasrc", null);
        if (!StringUtils.isBlank(datasrc)) {
            if (datasrc.charAt(0) != '#') datasrc = "#" + datasrc;
        } else
            datasrc = "";
    }
    catch (ClassCastException ce) {
        datasrc = "";
    }
    l.fine(getClass().getName() + ": datasrc=" + datasrc);
    try {
        datafld = (String) tagUtils.lookup(pageContext, "datafld", null);
    }
    catch (ClassCastException ce) {
        datafld = "";
    }
    l.fine(getClass().getName() + ": datafld=" + datafld);
    try {
        beanName = (String) tagUtils.lookup(pageContext, "beanName", null);
    }
    catch (ClassCastException ce) {
        //ignore it
    }
    // no beanName, use fieldId
    if (StringUtils.isBlank(beanName))
        beanName = field.getFieldId();
    if (type.equals("CHECKBOX")) {
        try {
            checkBoxValue = (String) tagUtils.lookup(pageContext, field.getFieldId() + "CheckBoxValue", null);
            if (checkBoxValue == null) {
                checkBoxValue = "Y";
            }
        }
        catch (ClassCastException ce) {
            checkBoxValue = "Y";
        }
        l.fine(getClass().getName() + ": checkBoxValue=" + checkBoxValue);
    }

    if (type.equals("MULTIBOX") || type.equals("RADIOBUTTON")) {
        try {
            maxInRow = (String) tagUtils.lookup(pageContext, field.getFieldId() + "MaxInRow", null);
            if (maxInRow == null) maxInRow = String.valueOf(lov.size());
        }
        catch (ClassCastException ce) {
            maxInRow = String.valueOf(lov.size());
        }
        l.fine(getClass().getName() + ": maxInRow=" + maxInRow);
    }

    if (type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FORMATTEDNUMBER") || type.equals("FINDERTEXT") || type.equals("EMAILTEXT")) { %>
<oweb:text oasisFormField="<%=field%>" maxlength="<%=field.getMaxLength()%>"
           tabindex="<%=field.getTaborder()%>"
           size="<%=field.getCols()%>" name="<%=beanName%>" isInTable="<%=isInTable%>"
           onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>" onkeydown="<%= onKeyDown%>" onkeyup="<%=onKeyUp%>" onblur="<%=onBlur%>"
           showLabel="<%=showLabel%>"
           datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>" datatype="<%=field.getDatatype()%>"
           numDecimals="<%=numDecimals%>" pattern="<%=field.getFormatPattern()%>"/>
<% } else if (type.equals("TEXTAREA") || type.equals("NOTETEXT") || type.equals("TEXTAREAPOPUP")) { %>
<oweb:textarea oasisFormField="<%=field%>" cols="<%=field.getCols()%>"
               tabindex="<%=field.getTaborder()%>"
               rows="<%=field.getRows()%>" name="<%=beanName%>" isInTable="<%=isInTable%>"
               onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>" onkeydown="<%= onKeyDown%>" onkeyup="<%=onKeyUp%>" onblur="<%=onBlur%>"
               showLabel="<%=showLabel%>"
               datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>"/>
<% } else if (type.equals("SELECT")) { %>
<oweb:select oasisFormField="<%=field%>" listOfValues="<%=lov%>" isInTable="<%=isInTable%>"
             tabindex="<%=field.getTaborder()%>"
             size="<%=(field.getRows()==null) ? \"0\" : field.getRows()%>" name="<%=beanName%>"
             onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>" onkeydown="<%= onKeyDown%>" onkeyup="<%=onKeyUp%>" onblur="<%=onBlur%>"
             showLabel="<%=showLabel%>"
             datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>"/>
<% } else if (type.equals("MULTISELECT")||type.equals("MULTISELECTPOPUP")) { %>
<oweb:select oasisFormField="<%=field%>" listOfValues="<%=lov%>"
             tabindex="<%=field.getTaborder()%>"
             size="<%=(field.getRows()==null) ? \"0\" : field.getRows()%>"
             multiple="yes" name="<%=beanName%>" isInTable="<%=isInTable%>"
             onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>" onkeydown="<%= onKeyDown%>" onkeyup="<%=onKeyUp%>" onblur="<%=onBlur%>"
             showLabel="<%=showLabel%>"
             maxLength="<%=(field.getMaxLength () ==null) ? \"\" : field.getMaxLength()%>"
             datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>"/>
<% } else if (type.equals("CHECKBOX")) { %>
<oweb:checkbox fieldColSpan="<%=fieldColSpan%>" oasisFormField="<%=field%>" listOfValues="<%=lov%>"
               tabindex="<%=field.getTaborder()%>"
               name="<%=beanName%>" onclick="<%=onChange%>" isInTable="<%=isInTable%>"
               value="<%=checkBoxValue%>" showLabel="<%=showLabel%>"
               datasrc="<%=datasrc%>" datafld="<%=datafld%>"/>
<% } else if (type.equals("MULTIBOX")) { %>
<oweb:multibox oasisFormField="<%=field%>" listOfValues="<%=lov%>" fieldColSpan="<%=fieldColSpan%>"
               tabindex="<%=field.getTaborder()%>"
               name="<%=beanName%>" onclick="<%=onChange%>" isInTable="<%=isInTable%>"
               maxInRow="<%=maxInRow%>" showLabel="<%=showLabel%>"/>
<% } else if (type.equals("RADIOBUTTON")) { %>
<oweb:radio oasisFormField="<%=field%>" listOfValues="<%=lov%>"
            tabindex="<%=field.getTaborder()%>"
            name="<%=beanName%>" onclick="<%=onChange%>" isInTable="<%=isInTable%>"
            maxInRow="<%=maxInRow%>" showLabel="<%=showLabel%>"
            datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>"/>
<% } else throw new JspException(type + " not supported for field [" + field.getFieldId() + "]");
    l.exiting(getClass().getName(), "jsp");
%>


