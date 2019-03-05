package dti.oasis.filter;

import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.session.UserSession;
import dti.oasis.session.UserSessionIds;
import dti.oasis.session.UserSessionManager;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.SysParmProvider;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to filter any xss scripts request is initializing or terminating.
 * <p/>
 * <p>(C) 2017 Delphi Technology, inc. (dti)</p>
 * Date:   Dec 07, 2017
 *
 * @author cvalencia
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 12/22/2017       cesar       #189603 - XSS filtering.
 * 05/14/2018       cesar       #192983 - to improve performance parse txtXML in BaseAction.
 * ---------------------------------------------------
 */
public final class XssFilter implements javax.servlet.Filter {
    static class FilterRequestWrapper extends HttpServletRequestWrapper {
        public FilterRequestWrapper(HttpServletRequest  request, List patternsList) {
            super((HttpServletRequest)request);
            this.request = request;
            this.patternsList = patternsList;
        }

        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);

            value = sanitizeParameter(paramName, value);

            return value;
        }

        public String[] getParameterValues(String paramName) {
            String[] values = super.getParameterValues(paramName);

            if (values != null) {
                int count = values.length;
                String[] encodedValues = new String[count];
                for (int i = 0; i < count; i++) {
                    encodedValues[i] = sanitizeParameter(paramName, values[i]);
                }
                values = encodedValues;
            }
            return values;
        }

        public String getHeader(String name) {
            String value = super.getHeader(name);
            return sanitizeParameter(name, value);
        }

        /**
         * sanitize the parameter value
         *
         * @param  paramName - the parameter name
         * @param  value - the value of the parameter name
         * @return txtXmlValue - sanitized value
         */
        protected String sanitizeParameter(String paramName, String value) {
            String sanitizedValue = value;

            //txtXML will be sanitized in the dti.oasis.struts.BaseAction.getInputRecordSet() method to improve performance.
            if (isPatternListExist() && !StringUtils.isBlank(value) && value.indexOf("ROWS><ROW") == -1) {
                Map map = getOasisFields();
                sanitizedValue = XssFilter.sanitizeParameter(patternsList, map, paramName, value);
            }
            return sanitizedValue;
        }

        /**
         * Checks if the xss pattern list.
         */
        private boolean isPatternListExist() {
            return (patternsList.size() > 0) ? true: false;
        }

        /**
         * retrieves Oasis fields stored in doFilter().
         *
         */
        private Map getOasisFields() {
            Map map = (Map) this.getAttribute(RequestIds.OASIS_XSS_OVERRIDES_FIELDS);
            return map;
        }

        @Override
        public String toString() {
            return this.getClass().getName() + ": " + request.toString();
        }

        private HttpServletRequest request = null;
        private List<Pattern> patternsList = new ArrayList();
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(getClass().getName(), "doFilter");
        }
        boolean bWrapRequest = false;

        HttpServletRequest req = (HttpServletRequest)servletRequest;

        try {
            UserSession userSession = UserSessionManager.getInstance().getUserSession(req);
            String dbPoolId = "";
            if (userSession != null) {
                dbPoolId = (String) userSession.get(UserSessionIds.DB_POOL_ID);

                if (!StringUtils.isBlank(dbPoolId)) {
                    patternsList = getXssPatternList(req);
                    bWrapRequest = true;
                }
            }
            if (c_l.isLoggable(Level.FINER)) {
                String s = "DbPoolId: " + dbPoolId;
                c_l.logp(Level.FINER, getClass().getName(), "doFilter", s);
            }
        } catch (Exception ex) {
            c_l.exiting(getClass().getName(), "UserSession many not be available yet: " + ex.getMessage());
        } finally {
            if (bWrapRequest) {
                chain.doFilter(new FilterRequestWrapper((HttpServletRequest)servletRequest,patternsList), servletResponse);
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig)throws ServletException  {

    }


    /**
     * Returns a list of xss patterns that will be used to filter .
     *
     * @param  req - the HttpServletRequest
     * @return txtXmlValue - sanitized value
     */
    public static List<Pattern> getXssPatternList(HttpServletRequest req) {
        List<Pattern> patternsList = new ArrayList();
        String sysParmEnableXSS = SysParmProvider.getInstance().getSysParm(req,"XSS_FILTER", "N");

        if (YesNoFlag.getInstance(sysParmEnableXSS).booleanValue()) {
            String XSSPatterns = SysParmProvider.getInstance().getSysParm(req,"XSS_FILTER_PATTERNS", "");

            if (!StringUtils.isBlank(XSSPatterns)) {
                String xssFilterPatternArray[] = null;

                xssFilterPatternArray = XSSPatterns.split(",");

                for (String p : xssFilterPatternArray) {
                    p = p.replaceAll("\n", "");
                    if (!StringUtils.isBlank(p)) {
                        Pattern pattern = Pattern.compile(p.trim(), Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                        patternsList.add(pattern);
                    }
                }
            }
        }
        if (c_l.isLoggable(Level.FINER)) {
            String s = "sysParmEnableXSS: " + sysParmEnableXSS;
            c_l.logp(Level.FINER, XssFilter.class.getName(), "getXssPatternList", s);
        }
        return patternsList;

    }

    /**
     * Returns a list of xss patterns that will be used to filter .
     *
     * @param  patternsList - A list of xss patterns list
     * @param  map - a list of oasis fields to override the system parameter2222
     * @return str - sanitized value
     */
    public static String sanitizeParameter(List<Pattern> patternsList, Map map, String paramName, String value) {
        String str = value;

        if (!StringUtils.isBlank(str)) {
            boolean filterValue = true;

            if (map != null && map.containsKey(paramName.toUpperCase())){
                filterValue = false;
            }
            if (filterValue) {
                // Remove all sections that match a pattern
                for (Pattern scriptPattern : patternsList) {
                    str = scriptPattern.matcher(str).replaceAll("");
                    if (StringUtils.isBlank(str)) {
                        break;
                    }
                }
            }
        }

        return str;
    }

    private List<Pattern> patternsList = new ArrayList();
    private static final Logger c_l = LogUtils.getLogger(XssFilter.class.getClass());

}