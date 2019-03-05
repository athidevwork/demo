<%@ page import="java.util.logging.Logger,
                 dti.oasis.util.LogUtils,
                 org.apache.struts.taglib.TagUtils,
                 dti.oasis.util.StringUtils"%>
<%@ page import="java.util.logging.Level" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dti.oasis.tags.OasisFields" %>
<%--
  Description:  Used by headerDataSpecialLayout to render a single field
  in a common header, for example, the case header and claim header in
  eCM case folder and claim folder pages, respectively.

  Author: GCCarney
  Date: Jul 16, 2007


  Revision Date    Revised By  Description
  ---------------------------------------------------


  02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
  11/04/2008   yhyang          #87710 Add SELECT type for policyheader.jsp
                               Since add a new
  04/13/2009   mxg             82494: Added check for dateType to handle Date Format Internationalization
  12/23/2015   jyang2          168386: Set isHeaderField to true when render oasis tags for header fields.
  10/12/2017   kshen           Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
  ---------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>

<%
    Logger l = LogUtils.enterLog(getClass(), "jsp", field);
    if (field == null)
        throw new JspException("Invalid Form Field");

    String type = field.getDisplayType();
    boolean showLabel = true;
    boolean isInTable = true;
    String datasrc = null;
    String datafld = null;
    ArrayList lov = null;
    String fieldColSpan = "1";

    TagUtils tagUtils = TagUtils.getInstance();
/* If we have no type, or type is not TEXT or TEXTAREA or FINDERTEXT or SELECT,  we will set it to TEXT and make it hidden. */
/* All the fields in the header are read-only, so TEXT, TEXTAREA, and FINDERTEXT should be the only types used. */
    if (type == null || (!type.equals("TEXT") && !type.equals("TEXTAREA") && !type.equals("FINDERTEXT") && !type.equals("SELECT"))) {
        field.setDisplayType("TEXT");
        type = "TEXT";
        field.setIsVisible(false);
    }

    String onChange = new StringBuffer().append("handleOnChange(document.forms[0].").append(field.getFieldId()).append(", event);").toString();
    String dataType = field.getDatatype();
    boolean isDate = false;
    if (dataType != null && dataType.equals(OasisFields.TYPE_DATE)) {
        isDate = true;
    }

    boolean isDateTime = false;
    if (OasisFields.TYPE_TIME.equals(dataType)) {
        isDateTime = true;
    }

    String onFocus = new StringBuffer("baseOnFocus('" + dataType + "', event);").toString();
    String onKeyPress = new StringBuffer("baseOnKeyPress('" + dataType + "', event);").toString();
    String onKeyDown = new StringBuffer("baseOnKeyDown('" + dataType + "', event);").toString();
    String onBlur = new StringBuffer("baseOnBlur('" + dataType + "', event);").toString();

    /* If we are a SELECT or MULTISELECT, find the list of values ArrayList
       in some scope under the key "fieldIdLOV" */
    if (!(type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FORMATTEDNUMBER") || type.equals("FINDERTEXT") || type.equals("TEXTAREA") || type.equals("NOTETEXT"))) {
        try {
            lov = (ArrayList) tagUtils.lookup(pageContext, field.getFieldId() + "LOV", null);
        }
        catch (ClassCastException ce) {
            throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
        }
        if (lov == null) {
            //Since eCM doesn't load lov as ePM, if the lov hasn't been loaded in eCM action,
            //system sets the type to TEXT and make it hidden rather than throw exception.
            field.setDisplayType("TEXT");
            type = "TEXT";
            field.setIsVisible(false);
            //throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
        }

        l.fine(getClass().getName() + ": lov=" + lov);
    }

    try {
        String b = (String) tagUtils.lookup(pageContext, field.getFieldId() + "showLabel", null);
        showLabel = (!(b != null && b.equalsIgnoreCase("false")));
    }
    catch (ClassCastException ce) {
        l.log(Level.FINER, ce.getMessage(), ce);
        showLabel = true;
    }
    catch (JspException jse) {
        l.log(Level.FINER, jse.getMessage(), jse);
        showLabel = true;
    }
    l.log(Level.FINE, new StringBuffer().append(getClass().getName()).append(": showLabel = ").append(showLabel).toString());

    try {
        String b = (String) tagUtils.lookup(pageContext, field.getFieldId() + "isInTable", null);
        isInTable = (!(b != null && b.equalsIgnoreCase("false")));
    }
    catch (ClassCastException ce) {
        l.log(Level.FINER, ce.getMessage(), ce);
        isInTable = true;
    }
    catch (JspException jse) {
        l.log(Level.FINER, jse.getMessage(), jse);
        isInTable = true;
    }
    l.log(Level.FINE, new StringBuffer().append(getClass().getName()).append(": isInTable = ").append(isInTable).toString());

    try {
        datasrc = (String) tagUtils.lookup(pageContext, "datasrc", null);
        if (!StringUtils.isBlank(datasrc)) {
            if (datasrc.charAt(0) != '#') datasrc = new StringBuffer().append("#").append(datasrc).toString();
        } else
            datasrc = "";
    }
    catch (ClassCastException ce) {
        l.log(Level.FINER, ce.getMessage(), ce);
        datasrc = "";
    }
    catch (JspException jse) {
        l.log(Level.FINER, jse.getMessage(), jse);
        datasrc = "";
    }
    l.log(Level.FINE, new StringBuffer().append(getClass().getName()).append(": datasrc = ").append(datasrc).toString());

    try {
        datafld = (String) tagUtils.lookup(pageContext, "datafld", null);
    }
    catch (ClassCastException ce) {
        l.log(Level.FINER, ce.getMessage(), ce);
        datafld = "";
    }
    catch (JspException jse) {
        l.log(Level.FINER, jse.getMessage(), jse);
        datafld = "";
    }
    l.log(Level.FINE, new StringBuffer().append(getClass().getName()).append(": datafld = ").append(datafld).toString());

    try {
        fieldColSpan = (String) tagUtils.lookup(pageContext, field.getFieldId() + "COLSPAN", null);
    }
    catch (ClassCastException e) {
        fieldColSpan = "";
    }

    try {
        beanName = (String) tagUtils.lookup(pageContext, "beanName", null);
    }
    catch (ClassCastException ce) {
        //ignore it
    }
    // no beanName, use fieldId
    if (StringUtils.isBlank(beanName)) {
        onChange = new StringBuffer("baseOnChange('" + dataType + "', event);").toString();
        beanName = field.getFieldId();
    }

    if (type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FORMATTEDNUMBER") || type.equals("FINDERTEXT")) { %>
<oweb:text oasisFormField="<%=field%>" maxlength="<%=field.getMaxLength()%>"
    size="<%=field.getCols()%>"  name="<%=beanName%>" isInTable="<%=isInTable%>"
    onchange="<%=onChange%>" showLabel="<%=showLabel%>"
    datasrc="<%=datasrc%>" datafld="<%=datafld%>"  datatype="<%=field.getDatatype()%>"  pattern="<%=field.getFormatPattern()%>"
    isHeaderField="true"/>
<%
}
else if(type.equals("TEXTAREA")) {
%>
<oweb:textarea oasisFormField="<%=field%>" cols="<%=field.getCols()%>"
    rows="<%=field.getRows()%>" name="<%=beanName%>" isInTable="<%=isInTable%>"
    onchange="<%=onChange%>" showLabel="<%=showLabel%>"
    datasrc="<%=datasrc%>" datafld="<%=datafld%>" maxlength="<%=field.getMaxLength()%>" isHeaderField="true"/>
<%
}
else if (type.equals("SELECT")||type.equals("MULTISELECTPOPUP")) {
%>
<oweb:select oasisFormField="<%=field%>" listOfValues="<%=lov%>" isInTable="<%=isInTable%>"
             size="<%=(field.getRows()==null) ? \"0\" : field.getRows()%>" name="<%=beanName%>"
             onfocus="<%=onFocus%>" onchange="<%=onChange%>" onkeypress="<%=onKeyPress%>" onkeydown="<%= onKeyDown%>" onblur="<%=onBlur%>"
             showLabel="<%=showLabel%>"
             datasrc="<%=datasrc%>" datafld="<%=datafld%>" fieldColSpan="<%=fieldColSpan%>" isHeaderField="true"/>
<%
}
else throw new JspException(type + " not supported for field [" + field.getFieldId() + "] in " +
  getClass().getName());
l.exiting(getClass().getName(), "jsp");
%>