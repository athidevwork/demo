package dti.oasis.struts;

import dti.oasis.util.LogUtils;
import dti.oasis.util.PageBean;
import dti.oasis.util.PageDefLoadProcessor;
import dti.oasis.navigationmgr.NavigationManager;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Menuing Helper object
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 16, 2003
 *
 * @author jbe
 */
/*
* Revision Date    Revised By  Description
* ---------------------------------------------------
* 12/08/2003       jbe         Change menuQuery sql to support
*                              one level of menu inheritance
* 2/6/2004         jbe         Add Logging
* 2/13/2004        jbe         Fix sql for security
* 3/5/2004         jbe         Fix sql for status
* 4/7/2004         jbe         Add id to MenuBean
* 4/19/2004        jbe         Allow topnavmenu to not have a full path
* 9/30/2004        jbe         get rid of the extra rs.close()
* 11/11/2004       jbe         Use PreparedStatement
* 3/30/2005        jbe         Change security SQL
* 06/08/2006       sxm         remove the parameter 'selectedmi' from request and get the selected
*                              top nav menu from topnav.xml to prevent possible Cross Site Scripting
* 01/23/2007       mlm         Added support for help url;
*                              Enhanced setMenu and processMenu to use the PageDefLoadProcessor
* 01/23/2007       wer         Changed use of InitialContext to using ApplicationContext;
* 04/02/2007       sxm         Use outer join in titleQuery for eApp since technically Web Applications
*                              do not belong to any application in pf_web_application.
* 05/03/2007       mlm         Code refactor to make use of NavigationManager.
* ---------------------------------------------------
*/

public class NavHelper {

    /**
     * Constructs the top nav and left nav menus, returning them inside
     * a PageBean.
     *
     * @param conn      JDBC Connection
     * @param request
     * @param className Class name of STRUTS Action Class
     * @param userId    userId
     * @return a PageBean containing the menus and a title
     * @throws Exception
     */
    public PageBean setMenu(Connection conn, HttpServletRequest request,
                            String className, String userId, PageDefLoadProcessor pageDefLoadProcessor) throws Exception {

        Logger l = LogUtils.enterLog(getClass(), "setMenu", new Object[]{conn, request, className, userId, pageDefLoadProcessor});

        PageBean pageBean = NavigationManager.getInstance().getPageBean(conn, request, className, userId, pageDefLoadProcessor);

        l.exiting(getClass().getName(), "setMenu", pageBean);
        return pageBean;
    }

}
