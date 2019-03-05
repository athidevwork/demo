package dti.oasis.filter;

import dti.oasis.app.ApplicationContext;
import dti.oasis.util.LogUtils;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * This action class provides methods to set character encoding
 * <p/>
 * <p>(C) 2010 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 08, 2010
 *
 * @author James
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class CharacterEncodingFilter implements javax.servlet.Filter {

    /**
     * init
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.encoding = ApplicationContext.getInstance().getProperty(CHARACTER_ENCODING_DEFAULT);
    }

    /**
     * set character encoding
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        l.entering(getClass().getName(), "doFilter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (this.encoding != null && request.getAttribute(ALREADY_FILTERED) == null) {
            if (request.getCharacterEncoding() == null) {
                // set character encoding for reuqest if it is not specified.
                request.setCharacterEncoding(this.encoding);
            }
            //set default character encoding for response
            response.setCharacterEncoding(this.encoding);
            //set already filtered
            request.setAttribute(ALREADY_FILTERED, Boolean.TRUE);
        }
        filterChain.doFilter(servletRequest, servletResponse);
        l.exiting(getClass().getName(), "doFilter");
    }

    //Public Methods
    /**
     * Take this filter out of service.
     */
    public void destroy() {
        this.encoding = null;
        this.filterConfig = null;
    }


    /**
     * The default character encoding to set for requests that pass through
     * this filter.
     */
    protected String encoding = null;

    /**
     * The filter configuration object we are associated with.   If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    protected static String ALREADY_FILTERED = "ALREADY_FILTERED";

    public static String CHARACTER_ENCODING_DEFAULT = "character.encoding.default";
    private final Logger l = LogUtils.getLogger(getClass());
}
