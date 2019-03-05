<%@ page import="dti.oasis.util.LocaleUtils" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.oasis.app.AppException" %>
<%--
    Description: Common included contents by header.jsp/headerpopup.jsp.

    Author: wfu
    Date: Oct 11, 2010


    Revision Date Revised By Description
    ---------------------------------------------------------------------------
    10/11/2010  wfu         109875 - Chinese currency symbol.
    10/11/2010  wfu         111776 - String literals refactoring.
    10/18/2010  wfu         111776 - Added messages for calendar date input validate
                                     and move calendar variables to footer/footerpopup.jsp
    10/20/2010  tzhao       109875 - Move the definition of currency_symbol to xmlproc.js.
    10/24/2011  wer         125728 - Add getEnvironmentName() function.
    10/12/2012  jxgu        133401 - display an alert dialog for error when verboseErrors is true
    11/21/2012  Parker      137533 - Fix panel hidden logic to ensure the required validation when panel hidden
    05/07/2013  jxgu        140985 - use jquery dialog
    12/17/2013  Elvin       Issue 150348: add js message max length check for textarea field
    04/28/2015  cv          Issue 162430: add js message max length check for multi-select field
                                          core.validate.multiselect.maxlength.error
    04/21/2016  huixu       Issue#169769 provide another way to export excel from XMLData
    10/12/2017  kshen       Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
    08/06/2018  dpang       194641 - Set js messages for jqxgrid column aggregate
    11/13/2018  wreeder     196147 - Change alignment settings for the loading-div to be in the format for a DIV instead of a table row
    ---------------------------------------------------------------------------
    (C) 2010 Delphi Technology, inc. (dti)
--%>
<%
    boolean alertJSErrors = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVALERTJSERRORS, "FALSE")).booleanValue();
    boolean useJQueryDialog = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("oasis.popup.useJQueryDialog", "true")).booleanValue();
    boolean isFormatNumberRound = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("number.format.round","false")).booleanValue();
    String errorMessage = MessageManager.getInstance().formatMessage(AppException.UNEXPECTED_ERROR);
    String onMouseMoveFunction = "";
    String onMouseUpFunction = "";
    if (!useJQueryDialog) {
        onMouseMoveFunction = "basePageOnMouseMove(event)";
        onMouseUpFunction = "basePageOnMouseUp(event)";
    }

    String runProcess = MessageManager.getInstance().formatMessage("label.process.info.processing");
    String progressIndicatorPosition = ApplicationContext.getInstance().getProperty("processingDialog.Position","DEFAULT");
    String alignDivString = null;
    String loadingDivSize = "width:150px; height: 32px;";
    String loadingDivClass = "processingIndicator";
    if ("CENTER".equals(progressIndicatorPosition)){
        alignDivString = "position:absolute; right:50%; top:50%";
        loadingDivSize = "width:192px; height: 60px;";
        loadingDivClass = "savingIndicator";
    } else if ("UPRIGHT".equals(progressIndicatorPosition)){
        alignDivString = "position:absolute; right:0px";
    } else {
        alignDivString = "position:absolute; right:0px";
    }
%>
<script type="text/javascript">

    function getEnvironmentName() {
        return '<%=environmentName%>';
    }
    <!-- Get Chinese currency symbol -->
    currency_symbol = '<%=LocaleUtils.getOasisCurrencySymbol()%>';

    if (typeof currency_symbol == 'undefined' || currency_symbol == null) {
        currency_symbol = '$';
    }

    <!-- Get Pagination information from resource -->
    setMessage("label.search.records.pagination", "<%=MessageManager.getInstance().formatMessage("label.search.records.pagination")%>");
    setMessage("label.search.record.pagination", "<%=MessageManager.getInstance().formatMessage("label.search.record.pagination")%>");
    setMessage("label.search.resultSet.pagination", "<%=MessageManager.getInstance().formatMessage("label.search.resultSet.pagination")%>");
    setMessage("label.search.pagination.page", "<%=MessageManager.getInstance().formatMessage("label.search.pagination.page")%>");
    setMessage("label.search.pagination.of", "<%=MessageManager.getInstance().formatMessage("label.search.pagination.of")%>");

    <!-- Set js messages for header.jsp/headerpopup.jsp messages -->
    setMessage("core.help.error.notFound", "<%=MessageManager.getInstance().formatMessage("core.help.error.notFound")%>");
    setMessage("core.jumpTo.error.contextPath", "<%=MessageManager.getInstance().formatMessage("core.jumpTo.error.contextPath")%>");
    setMessage("core.jumpTo.error.sourceField", "<%=MessageManager.getInstance().formatMessage("core.jumpTo.error.sourceField")%>");
    setMessage("core.jumpTo.error.initialized", "<%=MessageManager.getInstance().formatMessage("core.jumpTo.error.initialized")%>");
    setMessage("core.jumpTo.error.translating", "<%=MessageManager.getInstance().formatMessage("core.jumpTo.error.translating")%>");
    setMessage("core.jumpTo.error.destination", "<%= MessageManager.getInstance().formatMessage("core.jumpTo.error.destination") %>");

    <!-- Set js messages for processing -->
    setMessage("label.process.info.processing", "<%=MessageManager.getInstance().formatMessage("label.process.info.processing")%>");

    <!-- Set js messages for date input validate -->
    setMessage("core.validate.error.required", "<%=MessageManager.getInstance().formatMessage("core.validate.error.required")%>");
    setMessage("core.validate.error.requiredInRow", "<%=MessageManager.getInstance().formatMessage("core.validate.error.requiredInRow")%>");
    setMessage("core.validate.error.textarea", "<%=MessageManager.getInstance().formatMessage("core.validate.error.textarea")%>");
    setMessage("core.validate.error.textareaInRow", "<%=MessageManager.getInstance().formatMessage("core.validate.error.textareaInRow")%>");

    <!-- Set js messages for calendor date input validate -->
    setMessage("cal.validate.error.length", "<%=MessageManager.getInstance().formatMessage("cal.validate.error.length")%>");
    setMessage("cal.validate.error.format", "<%=MessageManager.getInstance().formatMessage("cal.validate.error.format")%>");

    <!-- Set js messages for leaving page -->
    setMessage("core.page.leave.changed", "<%=MessageManager.getInstance().formatMessage("core.page.leave.changed")%>");

    <!-- Set js messages for excel export -->
    setMessage("core.export.excel.nodata", "<%=MessageManager.getInstance().formatMessage("core.export.excel.nodata")%>");

    <!-- Set js messages for multi-select validation -->
    setMessage("core.validate.multiselect.maxlength.error", "<%=MessageManager.getInstance().formatMessage("core.validate.multiselect.maxlength.error")%>");

    <!-- Set js messages for jqxgrid column aggregate -->
    setMessage("core.label.column.agg.total", "<%=MessageManager.getInstance().formatMessage("core.label.column.agg.total")%>");
    setMessage("core.label.column.agg.count", "<%=MessageManager.getInstance().formatMessage("core.label.column.agg.count")%>");

    var dropdownFieldsWithExpiredOptions = '<c:out value="${SELECT_FIELDS_WITH_EXPIRED_OPTION}"/>';
    var expiredOptionSuffixIndicator = '<c:out value="${EXPIRED_OPTION_SUFFIX}"/>';
    var expiredOptionTitle = '<fmt:message key="expired.option.tooltip"/>';
    var expiredOptionDisplayableSuffix= '<%=MessageManager.getInstance().formatMessage("expired.option.displayable.suffix")%>';

    // Panel Class
    var COLLAPSE_PANEL_CLASS = "<%= OasisTagHelper.COLLAPSE_PANEL_CLASS %>";

    <% if (alertJSErrors) {%>
    window.onerror = function (errorMessage, url, lineNumber) {
        var messageText = "<%=errorMessage%>" + "\n";
        messageText += "The error occurred at " + new Date() + "\n";
        messageText += errorMessage + "\n";
        messageText += "At line: " + lineNumber + "\n";
        messageText += url + "\n";
        alert(messageText);
        return true;
    }
    <%
    }
    %>

    function getFormatNumberRound() {
        return <%=isFormatNumberRound%> ;
    }

<c:if test="${useJqxGrid}">
    var enableExportAll = <%=Boolean.toString(YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("grid.button.exportall.enable", "FALSE")).booleanValue())%>;
</c:if>
</script>
