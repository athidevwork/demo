<%@ page import="dti.oasis.tags.OasisTagHelper" %>
<%@ page import="dti.oasis.util.StringUtils" %>
<%@ page import="dti.oasis.util.FormatUtils" %>
<%@ page import="dti.oasis.app.ApplicationContext" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>
<%@ page import="dti.oasis.busobjs.YesNoFlag" %>
<%@ page import="dti.oasis.util.LogUtils" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="dti.oasis.http.Module" %>
<%@ page language="java" %>

<%
    Logger lHeaderJsp = LogUtils.enterLog(getClass(), "headerVariables.jsp");
    boolean isLoginPage = false;
    String jsNotesImage = (String)request.getAttribute(dti.oasis.http.RequestIds.NOTES_IMAGE);
    if (StringUtils.isBlank(jsNotesImage))
        jsNotesImage = ApplicationContext.getInstance().getProperty(dti.oasis.http.RequestIds.NOTES_IMAGE, "/images/notes.gif");

    String jsNoNotesImage = (String)request.getAttribute(dti.oasis.http.RequestIds.NO_NOTES_IMAGE);
    if (StringUtils.isBlank(jsNoNotesImage))
        jsNoNotesImage = ApplicationContext.getInstance().getProperty(dti.oasis.http.RequestIds.NO_NOTES_IMAGE, "/images/nonotes.gif");

    // field label class
    String labelClassHidden = OasisTagHelper.FIELD_CLASS_HIDDEN + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX;
    String labelClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;
    String labelClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    // field class
    String fieldClassHidden = OasisTagHelper.FIELD_CLASS_HIDDEN + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;
    String fieldClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;
    String fieldClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    // Header Label classes
    String headerFirstCol = StringUtils.capitalizeFirstLetter(OasisTagHelper.HEADER_FIELD_FIRSTCOL);
    String headerSecondCol = StringUtils.capitalizeFirstLetter(OasisTagHelper.HEADER_FIELD_SECONDCOL);
    String headerThirdCol = StringUtils.capitalizeFirstLetter(OasisTagHelper.HEADER_FIELD_THIRDCOL);

    String headerFirstColLabelClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE;
    headerFirstColLabelClassEditable += headerFirstCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    String headerSecondColLabelClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE ;
    headerSecondColLabelClassEditable += headerSecondCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    String headerThirdColLabelClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE ;
    headerThirdColLabelClassEditable += headerThirdCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    String headerFirstColLabelClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerFirstColLabelClassReadonly += headerFirstCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    String headerSecondColLabelClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerSecondColLabelClassReadonly += headerSecondCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    String headerThirdColLabelClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerThirdColLabelClassReadonly += headerThirdCol + OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX ;

    // Header Field classes
    String headerFirstColFieldClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE ;
    headerFirstColFieldClassEditable += headerFirstCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    String headerSecondColFieldClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE ;
    headerSecondColFieldClassEditable += headerSecondCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    String headerThirdColFieldClassEditable = OasisTagHelper.FIELD_CLASS_EDITABLE ;
    headerThirdColFieldClassEditable += headerThirdCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    String headerFirstColFieldClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerFirstColFieldClassReadonly += headerFirstCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    String headerSecondColFieldClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerSecondColFieldClassReadonly += headerSecondCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;

    String headerThirdColFieldClassReadonly = OasisTagHelper.FIELD_CLASS_READONLY ;
    headerThirdColFieldClassReadonly += headerThirdCol + OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX ;
%>

<script type="text/javascript">
var localeDataMask = '<%=FormatUtils.getDateFormatForDisplayString()%>';
var notesImage = '<%=jsNotesImage%>';
var noNotesImage = '<%=jsNoNotesImage%>';
// Page View State Cache Key
var PAGE_VIEW_STATE_CACHE_KEY = "<%= dti.oasis.http.RequestIds.CACHE_ID_FOR_PAGE_VIEW%>";
// field container suffix
var FIELD_LABEL_CONTAINER_SUFFIX = "<%=OasisTagHelper.FIELD_LABEL_CONTAINER_SUFFIX%>";
var FIELD_VALUE_CONTAINER_SUFFIX = "<%=OasisTagHelper.FIELD_VALUE_CONTAINER_SUFFIX%>";
// label/field prefix/suffix
var CLASS_HIDDEN_PREFIX = "<%= OasisTagHelper.FIELD_CLASS_HIDDEN %>";
var CLASS_EDITABLE_PREFIX = "<%= OasisTagHelper.FIELD_CLASS_EDITABLE %>";
var CLASS_READONLY_PREFIX = "<%= OasisTagHelper.FIELD_CLASS_READONLY %>";
var CLASS_LABEL_SUFFIX = "<%= OasisTagHelper.FIELD_LABEL_HTML_CLASS_SUFFIX %>";
var CLASS_FIELD_SUFFIX = "<%= OasisTagHelper.FIELD_VALUE_HTML_CLASS_SUFFIX %>";
// field label class
var LABEL_CLASS_HIDDEN = "<%= labelClassHidden %>";
var LABEL_CLASS_EDITABLE = "<%= labelClassEditable %>";
var LABEL_CLASS_READONLY = "<%= labelClassReadonly %>";
// field class
var FIELD_CLASS_HIDDEN = "<%= fieldClassHidden %>";
var FIELD_CLASS_EDITABLE = "<%= fieldClassEditable %>";
var FIELD_CLASS_READONLY = "<%= fieldClassReadonly %>";
// Header Label classes
var HEADER_FIRST_COL_LABEL_CLASS_EDITABLE = "<%= headerFirstColLabelClassEditable %>";
var HEADER_SECOND_COL_LABEL_CLASS_EDITABLE = "<%= headerSecondColLabelClassEditable %>";
var HEADER_THIRD_COL_LABEL_CLASS_EDITABLE = "<%= headerThirdColLabelClassEditable %>";
var HEADER_FIRST_COL_LABEL_CLASS_READONLY = "<%= headerFirstColLabelClassReadonly%>";
var HEADER_SECOND_COL_LABEL_CLASS_READONLY = "<%= headerSecondColLabelClassReadonly %>";
var HEADER_THIRD_COL_LABEL_CLASS_READONLY = "<%= headerThirdColLabelClassReadonly %>";
// Header Field classes
var HEADER_FIRST_COL_FIELD_CLASS_EDITABLE = "<%= headerFirstColFieldClassEditable %>";
var HEADER_SECOND_COL_FIELD_CLASS_EDITABLE = "<%= headerSecondColFieldClassEditable %>";
var HEADER_THIRD_COL_FIELD_CLASS_EDITABLE = "<%= headerThirdColFieldClassEditable %>";
var HEADER_FIRST_COL_FIELD_CLASS_READONLY = "<%= headerFirstColFieldClassReadonly  %>";
var HEADER_SECOND_COL_FIELD_CLASS_READONLY = "<%= headerSecondColFieldClassReadonly  %>";
var HEADER_THIRD_COL_FIELD_CLASS_READONLY = "<%= headerThirdColFieldClassReadonly %>";

// variable prefix for readonly field
var JS_VAR_READONLY_FIELD_PREFIX = "<%=OasisTagHelper.JS_VAR_READONLY_FIELD_PREFIX%>";
</script>
<%
    String enableEUMStr = "N";
    if (SysParmProvider.getInstance().isAvailable()) {
        enableEUMStr = SysParmProvider.getInstance().getSysParm(request,"CS_ENABLE_APPD_EUM", "N");
    }
    Boolean enableEUM = YesNoFlag.getInstance(enableEUMStr).booleanValue();

    String eumAppKey = "";
    if (SysParmProvider.getInstance().isAvailable()) {
        eumAppKey = SysParmProvider.getInstance().getSysParm(request,"CS_APPD_EUM_APP_KEY", "");
    }

    if(enableEUM){
%>
<script type="text/javascript">(function (config) {
    (function (xd) {
        xd.enable = true;
    })(config.xd || (config.xd = {}));
})(window['adrum-config'] || (window['adrum-config'] = {}));
</script>
<script type="text/javascript">
    <%
            if(!StringUtils.isBlank(eumAppKey)){
    %>
    window["adrum-app-key"] = "<%=eumAppKey%>";
    <%
            }
    %>
    window['adrum-start-time']= new Date().getTime();
</script>
<script type="text/javascript" src="<%=Module.getCorePath(request)%>/js/adrum.js"></script>
<%
    }
%>