<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    Description: Front-End for Spellex Spell Checker Engine

  Author: mgitelman
  Date: September 2, 2009


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
    01/30/2013     htwang      issue 139459, put a backlash before single quote
                                   in misspelled word’s corresponding otherword
                                   and show a processing dialog during checking
    08/30/2013       jxgu        Issue#147685 upgrade jquery to 1.7.2
  -----------------------------------------------------------------------------
  (C) 2009 Delphi Technology, inc. (dti)
--%>

<%@ page import="java.util.Properties" %>
<%@ page import="com.spellex.ssce.PropSpellingSession" %>
<%@ page import="dti.oasis.spellchecker.SpellexSpellChecker" %>
<%@ page import="com.spellex.ssce.SuggestionSet" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="com.spellex.ssce.StringWordParser" %>
<%@ page import="com.spellex.ssce.HTMLStringWordParser" %>
<%@ page import="dti.oasis.struts.ActionHelper" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>

<%
    String corePath = Module.getCorePath(request);
    String runProcess = MessageManager.getInstance().formatMessage("label.process.info.processing");
%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="<%=corePath%>/css/dti.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="../css/spellexSpellerStyle.css"/>
    <script language="javascript" src="../js/spellexWordWindow.js"></script>
    <script type="text/javascript" src="<%=corePath%>/js/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="<%=corePath%>/js/jquery-ui-1.10.3.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#processingDialog").hide();
        });
    </script>
</head>

<body onLoad="init_spell();" bgcolor="#ffffff">

<div id="processingDialog" title="" align="center" style="display:block">
    <body topmargin=0 leftmargin=0>
    <img src="<%=corePath%>/images/running.gif" alt="processing"/>
    <span class="txtOrange">&nbsp;<%=runProcess%></span>
    </body>
</div>

<script language="javascript">

    var suggs = new Array();
    var words = new Array();
    //MG
    var problems = new Array();
    var positions = new Array();
    var otherWords = new Array();
    var textinputs = new Array();
    var error;
    <%
        SpellexSpellChecker ssch = SpellexSpellChecker.getInstance(request.getSession().getServletContext());
        //get parameter
        String textinputs = request.getParameter("textinputs[]");
        String[] txtInputs  = request.getParameterValues("textinputs[]");
        for(int idx=0; idx<txtInputs.length; idx++)
        {
           out.write("textinputs[" + idx + "] = decodeURIComponent(\"" + txtInputs[idx] + "\");\n");
        }

        textinputs = java.net.URLDecoder.decode(textinputs, "utf-8");

       //NOT NEEDED WITH HTMLStringWordParser
       //textinputs = textinputs.replaceAll("\\<.*?\\>", " ");

        textinputs = textinputs.replaceAll("&nbsp;", " ");

        PropSpellingSession speller = ssch.getSpellingSession(request);

        HTMLStringWordParser wordParser = new HTMLStringWordParser(textinputs, false);
        SuggestionSet suggestionSet = new SuggestionSet(16);
        StringBuffer otherWord = new StringBuffer();
        int index = 0;

        int text_input_idx = 0;

        //If no errors, create "fake" empty arrays
        out.write("words[0] = [];\n");
        out.write("suggs[0] = [];\n");
        out.write("problems[0] = [];\n");
        out.write("positions[0] = [];\n");
        out.write("otherWords[0] = [];\n");
        int result;
        while ((result = speller.check(wordParser, otherWord)) != speller.END_OF_TEXT_RSLT) {
            char problemType;
            if ((result & speller.UNCAPPED_WORD_RSLT) != 0) {
                problemType = 'U';
            } else if ((result & speller.CONDITIONALLY_CHANGE_WORD_RSLT) != 0) {
                problemType = 'C';
            } else if ((result & speller.DOUBLED_WORD_RSLT) != 0) {
                problemType = 'D';
            } else if ((result & speller.AUTO_CHANGE_WORD_RSLT) != 0) {
                problemType = 'R';
            } else {
                problemType = 'M';	// misspelled word
            }

            out.write("words[0][" + index + "] = '" + wordParser.getWord().replace("\'", "\\'") + "';\n");
            String varOtherWord = otherWord.toString().replace("\'", "\\'");
            if (problemType == 'D') {	// doubled word
                otherWord.setLength(0);
                out.write("suggs[" + text_input_idx + "][" + index + "] = [' '");
                out.write("];\n");
            }
            if (problemType == 'R' || problemType == 'C') {	// Auto Change
                out.write("suggs[" + text_input_idx + "][" + index + "] = ['"+varOtherWord);
                out.write("'];\n");
            }
            if (problemType == 'U' || problemType == 'M') {
                speller.suggest(wordParser.getWord(), speller.getMinSuggestDepth(), speller.getComparator(), suggestionSet);
            }

            if(suggestionSet.size() > 1){
                out.write("suggs[" + text_input_idx + "][" + index + "] = [");
                for (int i = 0; i < suggestionSet.size(); ++i) {
                    out.write("'" + suggestionSet.wordAt(i).trim().replace("\'", "") + "'");
                    if (i < suggestionSet.size() - 1) {
                        out.write(",");
                    }
                }
                out.write("];\n");
            }

            out.write("problems[" + text_input_idx + "][" + index + "] = '"+problemType+"';\n");
            out.write("positions[" + text_input_idx + "][" + index + "] = "+wordParser.getCursor()+";\n");
            out.write("otherWords[" + text_input_idx + "][" + index + "] = '"+varOtherWord+"';\n");
            index++;
            suggestionSet.clear();
            wordParser.nextWord();
        }
    %>

    var wordWindowObj = new wordWindow();
    wordWindowObj.originalSpellings = words;
    wordWindowObj.suggestions = suggs;
    wordWindowObj.textInputs = textinputs;
    //MG
    wordWindowObj.problems = problems;
    wordWindowObj.positions = positions;
    wordWindowObj.otherWords = otherWords;

    wordWindowObj.writeBody();

    function init_spell()
    {
//    alert('spellexSpellChecker.jsp:init_spell()');
        // check if any error occured during server-side processing
        if (error)
        {
            alert(error);
        }
        else
        {
            // call the init_spell() function in the parent frameset
            if (parent.frames.length)
            {
                parent.init_spell(wordWindowObj);
            }
            else
            {
                alert('This page was loaded outside of a frameset. It might not display properly');
            }
        }
    }

</script>


</body>

</html>