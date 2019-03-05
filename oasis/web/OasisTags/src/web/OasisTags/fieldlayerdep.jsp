<%@ page import="dti.oasis.struts.IOasisAction,
                 dti.oasis.tags.*,
                 java.util.*"%>
<%@ page import="dti.oasis.app.AppException"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<jsp:useBean id="fieldsMap" class="dti.oasis.tags.OasisFields" scope="request"/>
<%--
  Description: Generates Javascript to handle the display of layers based on
               the relationship between layer/fields/values to one or more layers.
               See pf_web_page_field_value_layer table.

  Author: jbe
  Date: Dec 16, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------
  9/13/2005         jbe       Use type='text/javascript' instead of language='javascript'
  2/09/2007         jmp       Add expression logic for the [IN] and [EXPR] keywords.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<%
    HashMap map = fieldsMap.getFieldDeps();
    if (map == null || map.size() == 0)
        return;

    ArrayList uniqueFields = new ArrayList();
    ArrayList uniqueLayers = new ArrayList();
    Iterator it = map.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry entry = (Map.Entry)it.next();

        FieldValuePair pair = (FieldValuePair) entry.getKey();
        if (!uniqueFields.contains(pair.getFieldId()))
            uniqueFields.add(pair.getFieldId());
        //only need to hide those configured field denpendent layers
        List layers = (List)entry.getValue();
        for(int i=0;i<layers.size();i++){
            String layerId = (String)layers.get(i);
            if(!uniqueLayers.contains(layerId)){
                uniqueLayers.add(layerId);
            }
        }
    }
%>
<script type="text/javascript">
	function hideAllLayers() {
<logic:iterate id="layerId" collection="<%=uniqueLayers%>" type="java.lang.String">     
        var obj = getObject("<%=layerId%>");
        if(obj) {
	        if(obj.length) {
	            for(var i=0;i<obj.length;i++)
                    hideShowElementByClassName(obj[i], true);
	        }
	        else
                hideShowElementByClassName(obj, true);
		}
</logic:iterate>
	}

	function checkFieldLayerDep(fieldObj) {
		if(!fieldObj) return;
		if(fieldObj.length) {
			for(var i=0;i<fieldObj.length;i++) {
				if(fieldObj[i].checked) {
					fieldObj = fieldObj[i];
					break;
				}
			}
		}	
	    var obj;
<logic:iterate id="pair" collection="<%=map.keySet().iterator()%>" type="dti.oasis.tags.FieldValuePair">
		if(fieldObj.name=="<%=pair.getFieldId()%>") {
            if(<%=buildLayerCondition(pair)%>) {
    <logic:iterate id="layerId" collection="<%=map.get(pair)%>" type="java.lang.String">
         	    obj= getObject("<%=layerId%>");
         	    if(obj) {
	            	if(obj.length) {
	            		for(var i=0;i<obj.length;i++)
	            			hideShowElementByClassName(obj[i], false);
	           		}
	           		else
                        hideShowElementByClassName(obj, false);
	          	}
	</logic:iterate>
            }
		}
</logic:iterate>
	}

    function processDeps() {
        hideAllLayers();
<logic:iterate id="fieldId" collection="<%=uniqueFields%>" type="java.lang.String">
        checkFieldLayerDep(getObject("<%=fieldId%>"));
</logic:iterate>
    }

<%--    <%
        //Test Cases for parsing logic for layer value expresions

        FieldValuePair testPair;
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode=FL");
        System.out.println("Test1 (Basic): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode=FL;");
        System.out.println("Test2 (Basic, ending semicolon): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode=FL;issueStateCode=FL");
        System.out.println("Test3 (Multi): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode=FL;issueStateCode=FL");
        System.out.println("Test4 (Multi, ending semicolon): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode[IN]FL,NY,NJ");
        System.out.println("Test5 (Basic IN): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode[IN]FL,NY,NJ;issueStateCode[IN]FL,CA");
        System.out.println("Test6 (Multiple IN): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR]practiceStateCode!=FL");
        System.out.println("Test7 (Not Equal): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR][^]practiceStateCode!=FL^issueStateCode=FL");
        System.out.println("Test8 (^ delimiter): " + buildLayerCondition(testPair));
        testPair = new FieldValuePair("riskTypeCode", "CHIRO[EXPR][^*^]practiceStateCode!=FL^*^issueStateCode=FL");
        System.out.println("Test9 (^*^ delimiter): " + buildLayerCondition(testPair));
    %>--%>
</script>

<%!
    protected String buildLayerCondition(FieldValuePair pair)
	{
        int exprPos;
        int loopIdx;
        int endDelimiterPos;
        String pairValue;
        String delimiter = ";";
        String currentToken;
        String currentToken2;
        StringBuffer buf = new StringBuffer();
        StringBuffer fieldIdBuf = new StringBuffer();

        // First pull out the value
        pairValue = pair.getValue();

        //Check for existence of [IN] keyword to determine logic
        exprPos = pairValue.indexOf("[IN]");

        //IN is only valid in the first position
        if (exprPos == 0) {
            buf.append("\"");
            buf.append(pairValue.substring(4));
            buf.append("\".indexOf(");
            buf.append("getObjectValue(\"");
            buf.append(pair.getFieldId());
            buf.append("\")) >= 0");
        } else {
            // Check for existence of [EXPR] keyword to determine logic
            exprPos = pairValue.indexOf("[EXPR]");

            if (exprPos > 0) {
                // Step 1:  Build the conditional for the initial fieldId
                buf.append("getObjectValue(\"");
                buf.append(pair.getFieldId());
                buf.append("\") == ");
                buf.append("\"");
                buf.append(pairValue.substring(0, exprPos));
                buf.append("\"");

                // Step2:  Remove the [EXPR] from the string
                pairValue = pairValue.substring(exprPos + 6);

                // Step 3:  Check for optional special delimiter (must be next string)
                if(pairValue.indexOf("[") == 0) {
                    endDelimiterPos = pairValue.indexOf("]", 0);
                    delimiter += pairValue.substring(1, endDelimiterPos);
                    pairValue = pairValue.substring(endDelimiterPos + 1);
                }

                // Step 4:  Tokenize the remainder
                StringTokenizer tok = new StringTokenizer(pairValue, delimiter);
                while(tok.hasMoreTokens()){
                    buf.append(" && ");

                    loopIdx = 0;
                    fieldIdBuf = new StringBuffer();
                    currentToken = tok.nextToken();
                    StringTokenizer tok2 = new StringTokenizer(currentToken, "=[!");

                    while(tok2.hasMoreTokens()){
                        loopIdx++;
                        currentToken2 = tok2.nextToken();

                        if (loopIdx == 1) {
                            // First time through its always the fieldId
                            fieldIdBuf.append("getObjectValue(\"");
                            fieldIdBuf.append(currentToken2);
                            fieldIdBuf.append("\")");
                        } else {
                            // Next time through handle IN, =, !=
                            if (currentToken.indexOf("IN]") > -1) {
                                buf.append("\"");
                                buf.append(currentToken2.substring(3));
                                buf.append("\"");
                                buf.append(".indexOf(");
                                buf.append(fieldIdBuf);
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

            // Simple case
            } else {
                buf.append("getObjectValue(\"");
                buf.append(pair.getFieldId());
                buf.append("\") == ");
                buf.append("\"");
                buf.append(pair.getValue());
                buf.append("\"");
            }
        }
        return buf.toString();
	}
%>

