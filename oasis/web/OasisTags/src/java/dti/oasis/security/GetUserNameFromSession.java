package dti.oasis.security;

import dti.oasis.struts.IOasisAction;
import dti.oasis.util.OasisUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Jun 27, 2008
 *
 * @author rlli
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class GetUserNameFromSession extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml;charset=utf-8");
        PrintWriter out = response.getWriter();
        String userName = "";
        HttpSession session = request.getSession(false);

        if ((session != null) && (session.getAttribute(IOasisAction.KEY_OASISUSER) != null)) {
            OasisUser user = (OasisUser) session.getAttribute(IOasisAction.KEY_OASISUSER);
            userName = user.getUserId();
        }
        out.print(userName);
        out.flush();
    }
}
