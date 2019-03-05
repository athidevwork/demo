<%@ page import="java.util.*,
                 dti.oasis.tags.OasisFormField,
                 dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.struts.IOasisAction" %>
<%@ page import="dti.oasis.tags.OasisFields" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%--
  Description: Generates Javascript to handle the display of field based on
               the expression field dependency column.

  Author: James
  Date: Jan 2, 2007


  Revision Date    Revised By  Description
  ---------------------------------------------------
  01/03/2014       Parker      148027 - Reduce the call times of the processFieldDeps function.
  ---------------------------------------------------
  (C) 2007 Delphi Technology, inc. (dti)
--%>

<%
    OasisFields fieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_FIELDS);
    List dependencyFieldList = new ArrayList();
    if (fieldsMap != null) {
        List pageFieldList = fieldsMap.getPageFields();
        //get all page fields which have field dependency defined
        if (pageFieldList != null && pageFieldList.size() > 0) {
            for (int i = 0; i < pageFieldList.size(); i++) {
                OasisFormField field = (OasisFormField) pageFieldList.get(i);
                if (field.getIsVisible() &&
                    !StringUtils.isBlank(field.getFieldDependency())) {
                    dependencyFieldList.add(field);
                }
            }
        }
        //get all layer fields which have field dependency defined
        for (int i = 0; i < fieldsMap.getLayerIds().size(); i++) {
            String layerId = (String) fieldsMap.getLayerIds().get(i);
            List layerFieldList = fieldsMap.getLayerFields(layerId);
            for (int j = 0; j < layerFieldList.size(); j++) {
                OasisFormField field = (OasisFormField) layerFieldList.get(j);
                if (field.getIsVisible() &&
                    !StringUtils.isBlank(field.getFieldDependency())) {
                    dependencyFieldList.add(field);
                }
            }
        }
    }
    OasisFields headerPageFieldsMap = (OasisFields) request.getAttribute(IOasisAction.KEY_HEADER_PAGE_FIELDS);
    if (headerPageFieldsMap != null) {
        List headerPageFieldList = headerPageFieldsMap.getAllFieldList();
        if (headerPageFieldList != null && headerPageFieldList.size() > 0) {
            for (int i = 0; i < headerPageFieldList.size(); i++) {
                OasisFormField field = (OasisFormField) headerPageFieldList.get(i);
                if (field.getIsVisible() &&
                        !StringUtils.isBlank(field.getFieldDependency())) {
                    dependencyFieldList.add(field);
                }
            }
        }
    }

    //generate javascript if there is field dependency defined
    if (dependencyFieldList.size() > 0) {
%>
<script type="text/javascript">

    //show field if necessary
    function checkFieldDep(fieldname, isDisplay, isEnableDisable) {
        if (hasObject(fieldname)) {
            var obj = getObject(fieldname);
            if (isEnableDisable){
                enableDisableField(obj, !isDisplay);
            }else{
                hideShowField(obj, !isDisplay);
            }
        }
    }

    //process related field dependency
    function processFieldDeps(fieldId) {
        if(isProcessFieldDeps){
        <logic:iterate id="field" collection="<%=dependencyFieldList%>" type="dti.oasis.tags.OasisFormField">
        <%
        boolean isEnableDisable = false;
        String dependencyString = field.getFieldDependency();
        if (dependencyString.startsWith("[E]")){
           isEnableDisable = true;
           dependencyString =  dependencyString.substring(3);
        }
        String[] dependencyResult = buildDependencyCondition(dependencyString);
        String dependencyCondition = dependencyResult[0];
        String relatedFieldIds = dependencyResult[1].toUpperCase();
        %>
            if(hasObject("<%=field.getFieldId()%>")) {
                if (isUndefined(fieldId) || "<%=relatedFieldIds%>".indexOf("," + fieldId.toUpperCase() + ",") >= 0) {
                    checkFieldDep("<%=field.getFieldId()%>", <%=dependencyCondition%>, <%=isEnableDisable%>);
                }
            }
        </logic:iterate>
        }
    }
</script>
<%
    }
%>

<%--
    <%
        //Test Cases for parsing logic for field dependency expressions

        System.out.println("Test1 (Basic): " + debugDepCond(buildDependencyCondition("practiceStateCode=FL")));
        System.out.println("Test2 (Basic, ending semicolon): " + debugDepCond(buildDependencyCondition("practiceStateCode=FL;")));
        System.out.println("Test3 (Multi): " + debugDepCond(buildDependencyCondition("practiceStateCode=FL;issueStateCode=FL")));
        System.out.println("Test4 (Multi, ending semicolon): " + debugDepCond(buildDependencyCondition("practiceStateCode=FL;issueStateCode=FL;")));
        System.out.println("Test5 (Basic IN): " + debugDepCond(buildDependencyCondition("practiceStateCode[IN]FL,NY,NJ")));
        System.out.println("Test6 (Multiple IN): " + debugDepCond(buildDependencyCondition("practiceStateCode[IN]FL,NY,NJ;issueStateCode[IN]FL,CA")));
        System.out.println("Test7 (Basic NIN): " + debugDepCond(buildDependencyCondition("practiceStateCode[NIN]FL,NY,NJ")));
        System.out.println("Test8 (Multiple NIN): " + debugDepCond(buildDependencyCondition("practiceStateCode[NIN]FL,NY,NJ;issueStateCode[NIN]FL,CA")));
        System.out.println("Test9 (Not Equal): " + debugDepCond(buildDependencyCondition("practiceStateCode!=FL")));
        System.out.println("Test10 (^ delimiter): " + debugDepCond(buildDependencyCondition("[^]practiceStateCode!=FL^issueStateCode=FL")));
        System.out.println("Test11 (^*^ delimiter): " + debugDepCond(buildDependencyCondition("[^*^]practiceStateCode!=FL^*^issueStateCode=FL")));
    %>
--%>
<%!
    protected String[] buildDependencyCondition(String fieldDenpendency) {
        int loopIdx;
        int endDelimiterPos;
        boolean first = true;
        String pairValue;
        String delimiter = ";";
        String currentToken;
        String currentToken2;
        StringBuffer buf = new StringBuffer();
        StringBuffer fieldIdBuf = new StringBuffer();
        StringBuffer relatedFieldBuf = new StringBuffer();

        // First pull out the value
        pairValue = fieldDenpendency;

        // Step 1:  Check for optional special delimiter (must be at first)
        if (pairValue.indexOf("[") == 0) {
            endDelimiterPos = pairValue.indexOf("]", 0);
            delimiter += pairValue.substring(1, endDelimiterPos);
            pairValue = pairValue.substring(endDelimiterPos + 1);
        }

        // Step 2:  Tokenize the remainder
        StringTokenizer tok = new StringTokenizer(pairValue, delimiter);
        while (tok.hasMoreTokens()) {
            if (first) {
                first = false;
            } else {
                buf.append(" && ");
            }

            loopIdx = 0;
            fieldIdBuf = new StringBuffer();
            currentToken = tok.nextToken();
            StringTokenizer tok2 = new StringTokenizer(currentToken, "=[!");

            while (tok2.hasMoreTokens()) {
                loopIdx++;
                currentToken2 = tok2.nextToken();

                if (loopIdx == 1) {
                    // First time through its always the fieldId
                    fieldIdBuf.append("getObjectValue(\"");
                    fieldIdBuf.append(currentToken2);
                    fieldIdBuf.append("\")");
                    relatedFieldBuf.append(",").append(currentToken2);
                } else {
                    // Next time through handle IN, =, !=
                    if (currentToken.indexOf("NIN]") > -1) {
                        buf.append("\"");
                        buf.append(",");
                        buf.append(currentToken2.substring(4));
                        buf.append(",");
                        buf.append("\"");
                        buf.append(".indexOf(");
                        buf.append("\",\"+");
                        buf.append(fieldIdBuf);
                        buf.append("+\",\"");
                        buf.append(") < 0");
                    } else if (currentToken.indexOf("IN]") > -1) {
                        buf.append("\"");
                        buf.append(",");
                        buf.append(currentToken2.substring(3));
                        buf.append(",");
                        buf.append("\"");
                        buf.append(".indexOf(");
                        buf.append("\",\"+");
                        buf.append(fieldIdBuf);
                        buf.append("+\",\"");
                        buf.append(") >= 0");
                    } else if (currentToken.indexOf("!=") > 0) {
                        buf.append(fieldIdBuf);
                        buf.append(" != ");
                        buf.append("\"");
                        buf.append(currentToken2);
                        buf.append("\"");
                    } else {
                        buf.append(fieldIdBuf);
                        buf.append(" == ");
                        buf.append("\"");
                        buf.append(currentToken2);
                        buf.append("\"");
                    }
                }
            }
        }
        relatedFieldBuf.append(",");
        return new String[]{buf.toString(), relatedFieldBuf.toString()};
    }

    protected String debugDepCond(String[] dependencyResult) {
        return "condition[" + dependencyResult[0] + "]; related fields[" + dependencyResult[1] + "]";
    }
%>

