<%--
  Description:

  Author: jmpotosky
  Date: Apr 25, 2007


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  -----------------------------------------------------------------------------
  (C) 2003 Delphi Technology, inc. (dti)
--%>

<% int buttonCounter = 1; %>
<tr>
    <td colspan="8" align="center">
        <table>
            <tr>
                <logic:iterate id="actionitem" name="pageBean" property="leftNavActions"
                  type="dti.oasis.util.MenuBean">
                    <% if(buttonCounter > 6) { %>
                        </tr><tr>
                    <%  buttonCounter = 1;} %>
                    <td id="R_actionitem_<%=actionitem.getId()%>">
                        <input type="button" name="actionitem_<%=actionitem.getId()%>" value="<%=actionitem.getLabel()%>" onclick="<%=actionitem.getUrl()%>" class="buttonText">
                    </td>
                    <% buttonCounter++; %>
                </logic:iterate>
            </tr>
        </table>
    </td>
</tr>

