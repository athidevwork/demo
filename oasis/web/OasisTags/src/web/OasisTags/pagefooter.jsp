<%--
  Description:

  Author: mmanickam
  Date: May 8, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>
<script type="text/javascript">

  function setPageTitle(pageTitleId, pageTitle) {
      if(!pageTitleId) {
          pageTitleId = "pageTitleForpageHeader";
      }
      if (getObject(pageTitleId)) {
          getObject(pageTitleId).innerText = pageTitle ;
      }
      return true;
  }

</script>

<c:if test="${UIStyleEdition=='2'}">
<c:if test="${empty pageTitle == false}">
    <script type="text/javascript">
        setPageTitle("<%= ((String) pageContext.getAttribute("pageTitle")) %>")
    </script>
</c:if>


<c:remove var="pageTitle"></c:remove>
</c:if>