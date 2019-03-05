package dti.oasis.tags;

import dti.oasis.util.LogUtils;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * JavaBean that encapsulates information about an OASIS
 * Web Element (button, link, image)
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Aug 29, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 2/9/2004     jbe         Add Logging & toString
* 9/20/2005    jbe         Add status, getStatus, setStatus & isVisible
* 01/23/2007   wer         Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 07/13/2007   mlm         Add styleClass
* ---------------------------------------------------
*/

public class OasisWebElement implements Serializable {
    private String elementId;
    private String type;
    private boolean isAvailable;
    private String text;
    private String url;
    private String style;
    private String styleClass;
    private String description;
    private String status;
    
    /**
     * Getter
     *
     * @return status of element I means not visible, A means visible
     */
    public boolean isVisible() {
        return (status!=null && status.equals("A"));
    }
        
    /**
     * Getter
     *
     * @return status of element I means not visible, A means visible
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter
     *
     * @param status I means not visible, A means visible
     */
    public void setStatus(String status) {
        this.status = status;
    }    
    
    /**
     * Getter
     *
     * @return description, used for Hover text over element
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter
     *
     * @param description used for hover text over element
     */
    public void setDescription(String description) {
        this.description = description;
    }


  /**
   * Getter
   *
   * @return styleClass, used for overriding the class attribute
   */
    public String getStyleClass() {
      return styleClass;
    }

  /**
   * Setter
   *
   * @param styleClass, style class to override
   */
    public void setStyleClass(String styleClass) {
      this.styleClass = styleClass;
    }

  /**
     * Constructor
     * 
     * @param elementId
     * @param type        BUTTON, SUBMIT, IMAGE, LINK
     * @param isAvailable true if user can access it, false if it should be disabled
     * @param text        text of element (text of link, value of button)
     * @param url         url of element (url of img for image, onclick handler for button,
     *                    href for link)
     * @param style       CSS style sheet
     * @param description used for hover text over element
     */
    public OasisWebElement(String elementId, String type, boolean isAvailable,
                           String text, String url, String style, String description) {
        Logger l = LogUtils.enterLog(getClass(), "constructor",
                new Object[]{elementId, type, String.valueOf(isAvailable), text,
                             url, style, description});
        this.elementId = elementId;
        this.type = type;
        this.isAvailable = isAvailable;
        this.text = text;
        this.url = url;
        this.style = style;
        this.description = description;
        l.exiting(getClass().getName(), "constructor", toString());
    }

    /**
     * Constructor
     * 
     * @param elementId
     * @param type        BUTTON, SUBMIT, IMAGE, LINK
     * @param isAvailable true if user can access it, false if it should be disabled
     * @param text        text of element (text of link, value of button)
     * @param url         url of element (url of img for image, onclick handler for button,
     *                    href for link)
     * @param style       CSS style sheet
     * @param description used for hover text over element
     * @param status      I for hidden, A for visible
     */
    public OasisWebElement(String elementId, String type, boolean isAvailable,
                           String text, String url, String style, String description, String status) {
        Logger l = LogUtils.enterLog(getClass(), "constructor",
                new Object[]{elementId, type, String.valueOf(isAvailable), text,
                             url, style, description, status});
        this.elementId = elementId;
        this.type = type;
        this.isAvailable = isAvailable;
        this.text = text;
        this.url = url;
        this.style = style;
        this.description = description;
        this.status = status;
        l.exiting(getClass().getName(), "constructor", toString());
    }
    
    /**
     * Getter
     *
     * @return elementId
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * Setter
     *
     * @param elementId
     */
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    /**
     * Getter
     *
     * @return BUTTON, SUBMIT, LINK, IMAGE
     */
    public String getType() {
        return type;
    }

    /**
     * Setter
     *
     * @param type BUTTON, SUBMIT, LINK, IMAGE
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter
     *
     * @return true if element is available for use, false if to be disabled
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Setter
     *
     * @param available true if element is available for use, false if to be disabled
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Getter
     *
     * @return Text of element (value of button, text of link)
     */
    public String getText() {
        return text;
    }

    /**
     * Setter
     *
     * @param text Text of element (value of button, text of link)
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter
     *
     * @return URL (url of image, href of link, onclick handler for button)
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter
     *
     * @param url url of image, href of link, onclick handler for button
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter
     *
     * @return CSS Stylesheet
     */
    public String getStyle() {
        return style;
    }

    /**
     * Setter
     *
     * @param style CSS Stylesheet
     */
    public void setStyle(String style) {
        this.style = style;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisWebElement");
        buf.append("{elementId=").append(elementId);
        buf.append(",type=").append(type);
        buf.append(",isAvailable=").append(isAvailable);
        buf.append(",text=").append(text);
        buf.append(",url=").append(url);
        buf.append(",style=").append(style);
        buf.append(",styleClass=").append(styleClass);
        buf.append(",description=").append(description);
        buf.append(",status=").append(status);
        buf.append('}');
        return buf.toString();
    }


}
