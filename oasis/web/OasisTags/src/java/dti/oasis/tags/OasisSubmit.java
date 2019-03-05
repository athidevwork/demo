package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import org.apache.struts.taglib.html.SubmitTag;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Extends the STRUTS SubmitTag
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Aug 11, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 2/7/2004     jbe         Add Logging
* 9/20/2005    jbe         Use element.isVisible
* ---------------------------------------------------
*/

public class OasisSubmit extends SubmitTag implements IOasisElementTag {
    protected String elementName;
    protected String mapName;
    protected OasisWebElement element;

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
     * Disable this this element if the security framework says it's not available
     *
     * @return EVAL_PAGE
     * @throws JspException
     */
    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");
        l.fine(toString());
        OasisTagHelper.setElement(this, pageContext);
        // if we've got no OasisWebElement, then treat this like
        // a normal SubmitTag
        int rc = 0;
        if (element == null)
            rc = super.doEndTag();
        else {
            if (element.isVisible()) {
                // hang onto the disabled property
                boolean isDisabled = getDisabled();
                // disable if need be
                if (!element.isAvailable())
                    setDisabled(true);
                // let the SubmitTag do its thing
                rc = super.doEndTag();
                // restore the disabled tag
                setDisabled(isDisabled);
            }
            else
                rc = EVAL_PAGE;
        }
        l.exiting(getClass().getName(), "doEndTag", new Integer(rc));
        return rc;
    }

    /**
     * Setter, url not used
     *
     * @param url
     */
    public void setUrl(String url) {
        //
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
     * @param obj OasisWebElement
     */
    public void setElement(Object obj) {
        element = (OasisWebElement) obj;
    }

    /**
     * Getter
     *
     * @return name with which the OasisElements object may be found in context.
     */
    public String getMapName() {
        return mapName;
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
     * @param mapName name with which the OasisElements object may be found in context.
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisSubmit");
        buf.append("{elementName=").append(elementName);
        buf.append(",mapName=").append(mapName);
        buf.append(",element=").append(element);
        buf.append('}');
        return buf.toString();
    }


}
