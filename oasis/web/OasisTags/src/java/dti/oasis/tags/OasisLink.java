package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.taglib.html.LinkTag;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Extends the STRUTS LinkTag
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Aug 11, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 2/7/2004      jbe      Add Logging
* 6/30/2004	    jbe	 	 Fix problem in doAfterBody
* 7/6/2004      jbe      Add release
* 9/20/2005     jbe      Use element.isVisible
* ---------------------------------------------------
*/

public class OasisLink extends LinkTag implements IOasisElementTag {

    protected String elementName;
    protected String mapName;
    protected OasisWebElement element;
    private String value;

    /**
     * 'Hook' to enable tags to be extended and
     * additional attributes added.
     *
     * @param handlers The StringBuffer that output will be appended to.
     */
    protected void prepareOtherAttributes(StringBuffer handlers) {
        super.prepareOtherAttributes(handlers);    //To change body of overridden methods use File | Settings | File Templates.
        prepareAttribute(handlers, "id", getProperty());
    }

    /**
     * Skip this element if the security framework says it's not available
     *
     * @return
     * @throws JspException
     */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag");
        l.fine(toString());
        OasisTagHelper.setElement(this, pageContext);
        int rc = 0;
        // if we've got an unavailable element, skip it
        if (element != null && (!element.isAvailable() || !element.isVisible()))
            rc = SKIP_BODY;
        else
        // Let the LinkTag do its thing
            rc = super.doStartTag();
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;

    }

    /**
     * Override the Ancestor doAfterBody to utilize the
     * value of the tag for text
     *
     * @return
     * @throws JspException
     */
    public int doAfterBody() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doAfterBody");
        // If we've got bodyContent, use it instead of the value
        if (bodyContent != null && !StringUtils.isBlank(bodyContent.getString().trim()))
            value = bodyContent.getString().trim();
        if (value.length() > 0)
            text = value;

        l.exiting(getClass().getName(), "doAfterBody", new Integer(SKIP_BODY));
        return (SKIP_BODY);

    }

    /**
     * Skip this element if the security framework says it's not available
     *
     * @return
     * @throws JspException
     */
    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");
        int rc = 0;
        if (element != null && (!element.isAvailable() || !element.isVisible()))
            rc = EVAL_PAGE;
        else
        // Let the LinkTag do its thing
            rc = super.doEndTag();
        l.exiting(getClass().getName(), "doEndTag");
        return rc;
    }

    /**
     * Set the href property
     *
     * @param url href property
     */
    public void setUrl(String url) {
        this.setHref(url);
    }

    /**
     * Getter
     *
     * @return OasisWebElement
     */
    public Object getElement() {
        return element;
    }

    /**
     * Setter
     *
     * @param elementName elementId
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Setter
     *
     * @param mapName name with which the OasisElements object may be found in context
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * Setter
     *
     * @param obj OasisWebElement
     */
    public void setElement(Object obj) {
        element = (OasisWebElement) obj;
    }

    /**
     * Getter
     *
     * @return name with which the OasisElements object may be found in context
     */
    public String getMapName() {
        return mapName;
    }

    /* (non-Javadoc)
   * @see javax.servlet.jsp.tagext.Tag#release()
   */
    public void release() {
        Logger l = LogUtils.enterLog(getClass(), "release");
        super.release();
        element = null;
        elementName = null;
        mapName = null;
        value = null;
        text = null;
        l.exiting(getClass().getName(), "release");
    }

    /**
     * Getter
     *
     * @return elementId
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Set the text for the link. Use instead of putting it in the body of the page
     *
     * @param val text of link
     */
    public void setValue(String val) {
        value = val;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisLink");
        buf.append("{elementName=").append(elementName);
        buf.append(",mapName=").append(mapName);
        buf.append(",element=").append(element);
        buf.append(",value=").append(value);
        buf.append('}');
        return buf.toString();
    }
}
