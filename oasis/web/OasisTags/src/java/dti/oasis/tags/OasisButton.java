package dti.oasis.tags;

import dti.oasis.util.LogUtils;
import org.apache.struts.taglib.html.ButtonTag;

import javax.servlet.jsp.JspException;
import java.util.logging.Logger;

/**
 * Extends the STRUTS ButtonTag
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Aug 11, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 2/7/2004     jbe      Added Logging
 * 7/6/2004     jbe      Add release
 * 9/20/2005    jbe      Use element.isVisible 
 * ---------------------------------------------------
 */

public class OasisButton extends ButtonTag implements IOasisElementTag {
    protected String elementName;
    protected String mapName;
    protected String styleClass;
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
        // a normal ButtonTag
        int rc = 0;
        if (element == null)
            rc = super.doEndTag();
        else {
            if (element.isVisible()) {
                // Hold onto disabled property
                boolean isDisabled = getDisabled();
                // disable if need be
                if (!element.isAvailable())
                    setDisabled(true);

                // let ButtonTag do its thing
                rc = super.doEndTag();
                // Restore disabled property
                setDisabled(isDisabled);
            }
            else
                rc = EVAL_PAGE;
        }
        l.exiting(getClass().getName(),"doEndTag", new Integer(rc));
        return rc;
    }

    /**
     * Setter
     *
     * @param elementName ElementId
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Setter
     *
     * @param mapName Name with which the OasisElements object may be found
     *                in context
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * Sets the button's onClick handler
     *
     * @param url button's onClick event handler
     */
    public void setUrl(String url) {
        setOnclick(url);
    }


    /** Getter for styleClass attribute
     * @return styleClass String
     */
    public String getStyleClass() {
      return styleClass;
    }

    /** Setter for styleClass attribute */
    public void setStyleClass(String styleClass) {
      this.styleClass = styleClass;
    }

  /**
     * Getter
     *
     * @return OasisWebElement object
     */
    public Object getElement() {
        return element;
    }

    /**
     * Setter
     *
     * @param obj OasisWebElement object
     */
    public void setElement(Object obj) {
        element = (OasisWebElement) obj;
    }

    /**
     * Getter
     *
     * @return name with which the OasisElements object can be found
     *         in context
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Getter
     *
     * @return ElementId
     */
    public String getElementName() {
        return elementName;
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
        styleClass = null;
        l.exiting(getClass().getName(), "release");
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisButton");
        buf.append("{elementName=").append(elementName);
        buf.append(",mapName=").append(mapName);
        buf.append(",styleClass=").append(styleClass);
        buf.append(",element=").append(element);
        buf.append("}");
        return buf.toString();
    }


}
