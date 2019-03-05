<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page language="java" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.lang.String" %>
<%@ page import="dti.oasis.messagemgr.MessageManager" %>
<%@ page import="dti.ci.entitysecuritymgr.impl.EntitySecurityManagerImpl" %>
<%@ page import="dti.ci.entitysecuritymgr.dao.EntitySecurityJdbcDAO" %>
<%@ page import="dti.oasis.util.SysParmProvider" %>


<%--
  Description: The common security header for CIS jsp pages.

  Author: Herb Koenig
  Date: June 4, 2013.


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  02/16/2017       ylu         Issue 181099: handle with the PK when cannot cast it to String type
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
--%>
<%
    String isEntitySecurityEnabled = SysParmProvider.getInstance().getSysParm("CI_USE_ENT_SECURITY","N");
    String pkString = "";

    // Default to readonly No
    String isEntityReadOnlyYN = "N";

    if (isEntitySecurityEnabled.equals("Y")) {

        Object pkObject = request.getAttribute("pk");

        if (pkObject instanceof String || pkObject == null ) {
            pkString = (String) pkObject;
        } else {
            pkString = pkObject.toString();
        }

        // pkString can be null if the page has no pk attribute in it...
        if (pkString != null)   {

            Long   pk = null;

            if (!pkString.isEmpty() || pkString != ""){
                pk       = Long.parseLong(pkString);
            }
            if (EntitySecurityManagerImpl.getInstance().isEntityReadOnly(pk)) {
                isEntityReadOnlyYN = "Y";
                MessageManager.getInstance().addWarningMessage("ci.entity.security.readOnly");

            }


        }

    }
    request.setAttribute("isEntityReadOnlyYN",isEntityReadOnlyYN);

%>

<c:choose>
    <c:when test="${isEntityReadOnlyYN=='Y'}">


        <script type="text/javascript">
            var isEntityReadOnlyYN = 'Y';

            function doSecurity() {

                if (isEntityReadOnlyYN == "Y"){

                    var coll = document.forms[0].elements;
                    var i = 0;

                    var msg = '';
                    var type;
                    var id;
                    // Disable all elements that are not hidden and are not buttons
                    //   buttons will be handled by page entitlements.
                    for (i = 0; i < coll.length; i++) {
                        if (coll[i].type != 'button' && coll[i].type != 'hidden')   {
                            //   alert(coll[i].name)    ;
                            enableDisableField(coll[i],true);

                        }

                    }

                    // If we have a filter criteria, set the fields back on...
                    if  (getSingleObject('FilterCriteria'))  {
                        var filterCriteria = getSingleObject('FilterCriteria').getElementsByTagName('*');
                        for (i = 0; i < filterCriteria.length; i++) {
                            name =   filterCriteria[i].getAttribute('name');
                            if ( name && (name != "null") )   {

                                enableDisableField(filterCriteria[i],false);

                            }

                        }
                    }
                    // If we have a secondary filter criteria, set fields back on
                    if  (getSingleObject('FilterCriteria2'))  {
                        var filterCriteria = getSingleObject('FilterCriteria2').getElementsByTagName('*');
                        for (i = 0; i < filterCriteria.length; i++) {
                            name =   filterCriteria[i].getAttribute('name');
                            if ( name && (name != "null") )   {

                                enableDisableField(filterCriteria[i],false);

                            }

                        }
                    }


                }

            }

        </script>

    </c:when>
    <c:otherwise>
        <script type="text/javascript">
            var isEntityReadOnlyYN = "N";
        </script>
    </c:otherwise>
</c:choose>

