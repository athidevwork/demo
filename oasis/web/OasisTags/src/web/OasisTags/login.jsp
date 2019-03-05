<%@ page import="java.util.ArrayList,
                 dti.oasis.struts.*,
                 dti.oasis.util.*,
                 dti.oasis.security.Authenticator" %>

<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%@ page language="java" %>
<%--
  Description: Login page

  Author: jbe
  Date: Oct 20, 2003


  Revision Date    Revised By  Description
  ---------------------------------------------------
  9/13/2005         jbe       Use type='text/javascript' instead of language='javascript'
  9/27/2005         jbe       Add "enrollMessage"
  01/11/2006	    gxc	      Issue 54932 - Added label.login.problems
			      instead of hardcoding
  01/25/2006	    sjz	      Using parameter ("uc") from request for User Type code to look up
                              different key of message label.login.topcopy
  01/26/2006	    sjz	      Passing through parameter ("uc") to enroll.jsp
  06/15/2006	    sxm	      will not store/pass DB pool ID if there is only one
  08/31/2006        sxm       Include customer header
  08/31/2006        sxm       Remove the hidden DB poolID from the new UI style if there is only one
  01/23/2007        lmm       Reformated UI with tables
  04/09/2008        wer       Enhanced to support configuring a dbPoolId for a role associated with a user or it's group, and removed passing of DBPOOLID as request parameter
  10/11/2010        wfu       111776: String literals refactoring.
  12/19/2011        bhong     118066 - Allow forgot password page to be configured with external URL.
  09/26/2011        bhong     Redefine "basePageOnLoad" function as empty to avoid executing unnessary logics in page loading for old UI version.
  ---------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%
    try {
    ApplicationContext.getInstance().exposeMessageSourceForJstl(application, request);

    String userId = "";
    String error = "";
    String dbpool = "";
    String msgClass = "errortext";
    Object o = request.getAttribute(IOasisAction.KEY_ERROR);
    if (o instanceof Exception)
        error = ((Exception) o).getMessage();
    if (o instanceof String)
        error = (String) o;
    if (StringUtils.isBlank(error)) {
        error = (String) request.getAttribute("enrollMessage");
        if (!StringUtils.isBlank(error))
            msgClass = "successtext";
    }
    if (request.getParameter("j_username") != null) {
        userId = request.getParameter("j_username");
        dbpool = request.getParameter(IOasisAction.KEY_DBPOOLID);

        if (StringUtils.isBlank(error))
            error = MessageManager.getInstance().formatMessage("label.login.error.invalidInfo");
    }
    if (error == null) error = "";

    PageBean bean = new PageBean();
    bean.setLeftNavActions(new ArrayList());
    bean.setLeftNavMenu(new ArrayList());
    bean.setTopNavMenu(new ArrayList());
    String appTitle = ApplicationContext.getInstance().getProperty("applicationTitle", MessageManager.getInstance().formatMessage("label.login.error.pleaseLogin"));
    bean.setTitle(appTitle);
    request.setAttribute(IOasisAction.KEY_PAGEBEAN, bean);
    session.removeAttribute(IOasisAction.PARM_LOGIN);
    // Disable AUTOCOMPLETE if configured
    boolean disableAutoComplete = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVDISABLELOGINAUTOCOMPLETE,"N")).booleanValue();
    String autoComplete = (disableAutoComplete ? "AUTOCOMPLETE=\"OFF\"" : "");

    String UIStyle = (String) request.getAttribute("UIStyleEdition");
    if (UIStyle == null) {
      UIStyle = ApplicationContext.getInstance().getProperty("UIStyleEdition", "0");
      request.setAttribute("UIStyleEdition", UIStyle);
    }

    // Use the external.forgot.password.URL if it is defined as the URL for the fogot password link.
    String forgotPasswordURL = ApplicationContext.getInstance().getProperty("external.forgot.password.URL", "forgotpassword.jsp");
%>
<c:choose>
   <c:when test="${UIStyleEdition=='2'}">
     <jsp:include page="UI2Login.jsp"></jsp:include>
   </c:when>
   <c:otherwise>

<%--Do not execute keep session logic in this page--%>
<c:set var="skipSessionKeepAlive" value="true"/>
<c:set var="isLoginPage" value="true"/>
<%@ include file="header.jsp" %>

<form action="j_security_check" method="post" name="processor">
<tr><td colspan="2">
</td></tr>
<tr><td colspan="2">
    <c:choose>
        <c:when test='${empty param.uc}'>
            <fmt:message key="label.login.topcopy"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="label.login.topcopy.${param.uc}"/>
        </c:otherwise>
    </c:choose>
</td></tr>
<c:choose>
   <c:when test="${UIStyleEdition=='1'}">
<script type="text/javascript">
    if(useJqxGrid)
        hideShowElementByClassName(document.all.leftNav, true);
    else
        document.all.leftNav.style.display = "none";
</script>
<tr><td colspan="2">
    <div id="loginBlock">
      <div id="loginBlockContent">
        <div id="loginBlockCaption">Please login below to continue...</div>

        <div id="loginBlockCaptionRow">
            <br/>
            <table class="loginRow">
                <%
                    if (disableAutoComplete){
                        if(useJqxGrid){
                %>
                            <input type="password" class="dti-hide"/>
                <%
                        } else {
                %>
                            <input type="password" style="display:none;"/>
                <%
                        }
                    }
                %>
                <tr>
                    <td class="loginBlockUserIDLabel"> <fmt:message key="label.header.page.userId"/></td>
                    <td class="loginBlockUserID">
                        <input class="inputText" <%=autoComplete%> type="text" name="j_username" value="<%=userId%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="loginBlockPwdLabel"> <fmt:message key="label.header.page.password"/></td>
                    <td class="loginBlockPwd">
                        <input <%=autoComplete%> class="inputText" type="password" name="j_password"/>
                    </td>
                </tr>
<%          if (error!="") { %>
                <tr>
                    <td colspan=2 class="<%=msgClass%>">   <%=error%> </td>
                </tr>
<%          }%>
                <tr>
                    <td colspan=2 align=center>
                        <input type="submit" class="buttons" onmouseover="return handleMouseover();" onmouseout="return handleMouseout();" value="<fmt:message key='label.login.error.login'/>"></input>
                    </td>
                </tr>
            </table>
        </div>

        <br/>

        <div class="loginProblems">
            <div id="loginPproblemsLabel"><fmt:message key="label.login.problems"/></div>
            <li><a href="javascript:forgotPassword()"><fmt:message key="label.login.forgotPassword"/></a></li>
            <li><fmt:message key="label.login.error.click"/> <a href="mailto:<fmt:message key="label.login.support.mailto" />"><fmt:message key="label.login.error.here"/></a> <fmt:message key="label.login.error.emailSupport"/>
            <li><fmt:message key="label.login.error.phoneSupport"/>
                <fmt:message key="label.login.phonecontact"/>.
                <%

                    if(YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANENROLL,"N")).booleanValue()) {

                %>

            <p/><b><fmt:message key="label.enroll.loginprompt"/></b>
            <li><fmt:message key="label.login.error.havePolicy"/> <fmt:message key="label.login.error.click"/> <a href="enroll.jsp"><fmt:message key="label.login.error.here"/></a> <fmt:message key="label.login.error.obtainId"/></li>
            <%
                if (YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANREGISTERCIS, "N")).booleanValue()) {
                    String url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISPERSONURL, null);
                    if (!StringUtils.isBlank(url)) {
            %>
            <li><fmt:message key="label.login.error.noPolicy"/> <fmt:message key="label.login.error.canStill"/> <a href="<%=url%>"><fmt:message key="label.login.error.register"/></a>.
                <%
                    }
                    url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISORGURL, null);
                    if (!StringUtils.isBlank(url)) {
                %>
                <li></li><fmt:message key="label.login.error.orgRegister"/> <a href="<%=url%>"><fmt:message key="label.login.error.here"/></a>.</li>
            <%
                        }
                    }
                }
            %>
        </div>

        <div class="loginRow">
            <div id="loginBottomcopyLabel"><fmt:message key="label.login.bottomcopy"/></div>
        </div>

        <div class="loginRow"><!--  blank --></div>
      </div>
    </div>
</td></tr>
</c:when>
<c:otherwise>
<tr><td colspan="2" class="headerbox left right top bottom"><fmt:message key="label.login.error.pleaseLogin"/></td></tr>
    <%
        if (disableAutoComplete){
            if(useJqxGrid){
    %>
                <input type="password" class="dti-hide"/>
    <%
            } else {
    %>
                <input type="password" style="display:none;"/>
    <%
            }
        }
    %>
<tr><td width="75" align="right"><fmt:message key="label.header.page.userId"/></td>
    <td align="left"><input class="inputText" <%=autoComplete%> type="text" name="j_username"
                            value="<%=userId%>"></td>
</tr>
<tr><td width="75" align="right"><fmt:message key="label.header.page.password"/></td>
    <td align="left"><input <%=autoComplete%> class="inputText" type="password" name="j_password"></td>
</tr>
    <tr>
        <td width="75">&nbsp;</td>
        <td class="<%=msgClass%>" align="left">
            <%=error%>
        </td></tr>
    <tr><td>&nbsp;</td>
        <td align="left">
            <input type="submit" class="buttons" value="<fmt:message key='label.login.error.login'/>"></td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td width="75">&nbsp;</td>
        <td>
            <b><fmt:message key="label.login.problems"/></b>
            <li><a href="javascript:forgotPassword()"><fmt:message key="label.login.forgotPassword"/></a></li>
            <li><fmt:message key="label.login.error.click"/> <a href="mailto:<fmt:message key="label.login.support.mailto" />"><fmt:message key="label.login.error.here"/></a> <fmt:message key="label.login.error.emailSupport"/>
            <li><fmt:message key="label.login.error.phoneSupport"/>
                <fmt:message key="label.login.phonecontact"/>.
                <%

                    if(YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANENROLL,"N")).booleanValue()) {

                %>

            <p><b><fmt:message key="label.enroll.loginprompt"/></b>
            <li><fmt:message key="label.login.error.havePolicy"/> <fmt:message key="label.login.error.click"/> <a href="enroll.jsp?uc=<c:out value='${param.uc}'/>"><fmt:message key="label.login.error.here"/></a> <fmt:message key="label.login.error.obtainId"/></li>
            <%
                if (YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVCANREGISTERCIS, "N")).booleanValue()) {
                    String url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISPERSONURL, null);
                    if (!StringUtils.isBlank(url)) {
            %>
            <li><fmt:message key="label.login.error.noPolicy"/> <fmt:message key="label.login.error.canStill"/> <a href="<%=url%>"><fmt:message key="label.login.error.register"/></a>.
                <%
                    }
                    url = ApplicationContext.getInstance().getProperty(IOasisAction.KEY_ENVREGISTERCISORGURL, null);
                    if (!StringUtils.isBlank(url)) {
                %>
                <li></li><fmt:message key="label.login.error.orgRegister"/> <a href="<%=url%>"><fmt:message key="label.login.error.here"/></a>.</li>
            <%
                        }
                    }
                }
            %>
        </td></tr>
    <tr><td width="75">&nbsp;</td>
        <td>
            <fmt:message key="label.login.bottomcopy"/>
        </td></tr>
       </c:otherwise>
    </c:choose>
    <script type='text/javascript'>
        function forgotPassword() {
            var a = getFormActionAttribute();
            var t = document.forms[0].target;
            document.forms[0].action = '<%=forgotPasswordURL%>';
            document.forms[0].target = '_FGT';
            submitFirstForm();
            document.forms[0].action = a;
            document.forms[0].target = t;
        }
    </script>
<jsp:include page="footer.jsp"/>

   </c:otherwise>
</c:choose>
<%
    }  catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
    
%>
<script type="text/javascript">
function basePageOnLoad() {
    // Redefine this function and do nothing.
}
</script>
