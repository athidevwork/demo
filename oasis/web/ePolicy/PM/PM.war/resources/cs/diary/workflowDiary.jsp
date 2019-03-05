<%@ page language="java" %>
<%--
  Description:

  Author: mmanickam
  Date: Nov 10, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/29/2011       Michael     issue 127446.
  07/12/2017       lzhang      186847   Reflect grid replacement project changes
  12/12/2017       kshen       Grid replacement.
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<%@ taglib uri="/WEB-INF/oasis-web.tld" prefix="oweb" %>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ include file="/core/header.jsp" %>
<script type="text/javascript"> if (getObject("pageHeader")) { hideShowElementByClassName(getObject("pageHeader"), true); } </script>
<%@ include file="/pmcore/common.jsp" %>

<script type="text/javascript">
    function resizeIframe(){
        if (hasObject("iframeWorkflowDiary")) {
            resizeIframeById("iframeWorkflowDiary");
        }
    }
    function resizeIframeById(iframeId) {
       var oFrame = getSingleObject(iframeId);
        var oDocument = oFrame.document || oFrame.contentDocument;
       var oBody = oDocument.body;
        var globalHeader = getSingleObject('globalHeader');
        var globalMenu = getSingleObject('globalMenu');
       // oFrame.style.height = oBody.scrollHeight + (oBody.offsetHeight - oBody.clientHeight);
        var width=oBody.scrollWidth + (oBody.offsetWidth - oBody.clientWidth);
        oFrame.style.width =width;
        globalMenu.style.width = width;
        globalHeader.style.width =width ;       
    }

    function handlePageOnResize() {
        resizeIframe();
    }
    function handleOnLoad() {
        resizeIframe();
    }
</script>
<form action="maintainPolicy.do" method=post name="policyInfo">
<%@ include file="/pmcore/commonFormHeader.jsp" %>

    <div>
        <iframe id="iframeWorkflowDiary"style="margin:0; padding:0; overflow-y:hidden;" frameborder="no" scrolling="no"
                width="100%" height="820px"> </iframe>
        <script type="text/javascript">
            if (getObject("iframeWorkflowDiary")) {
                getObject("iframeWorkflowDiary").src = getCSPath() + "/workflowdiary.do?isForWorkCenter=true"
            }
        </script>
    </div>
    
<%@ include file="/core/pagefooter.jsp" %>
<jsp:include page="/core/footer.jsp"/>
