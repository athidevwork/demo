package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import org.apache.struts.taglib.html.ImgTag;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Extends the STRUTS ImageTag
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
* 7/6/2004     jbe         Add release
* 9/20/2005    jbe         Use element.isVisible
* ---------------------------------------------------
*/

public class OasisImg extends ImgTag implements IOasisElementTag {

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
     * Skip this this element if the security framework says it's not available
     *
     * @return EVAL_PAGE
     * @throws JspException
     */

    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");
        l.fine(toString());
        OasisTagHelper.setElement(this, pageContext);
        // if we've got no OasisWebElement, then treat this like
        // a normal ImgTag
        int rc = 0;
        if (element == null)
            rc = super.doEndTag();
        else {
            if (element.isVisible()) {
                // set property to null, it was set to the elementId by
                // OasisTagHelper.setElement. The property is used
                // to determine url parameters in the image tag, which we don't
                // want to use. A bit inconsistent with the rest of the Struts HTML tags.
                property = null;
                // hang onto the disabled property
                boolean isDisabled = getDisabled();
                // disable if need be
                if (!element.isAvailable())
                    setDisabled(true);

                // let imgtag do its thing
                rc = super.doEndTag();

                // restore disabled property
                setDisabled(isDisabled);
            }
            else
                rc = EVAL_PAGE;
        }
        l.exiting(getClass().getName(), "doEndTag", new Integer(rc));
        return rc;
    }

    /**
     * Sets the src property of the Img tag
     *
     * @param url url of the image
     */
    public void setUrl(String url) {
        setSrc(url);
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
     * Setter
     *
     * @param elementName elementId
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * setter
     *
     * @param mapName Name with which the OasisElements object may be found in context
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * Getter
     *
     * @return Name with which the OasisElements object may be found in context
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
     * @param val We ignore the value property for this tag
     */
    public void setValue(String val) {
        //
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
        l.exiting(getClass().getName(), "release");
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisImg");
        buf.append("{elementName=").append(elementName);
        buf.append(",mapName=").append(mapName);
        buf.append(",element=").append(element);
        buf.append('}');
        return buf.toString();
    }

}
