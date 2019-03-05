package dti.oasis.tags;

/**
 * Base Interface for OasisWebElement related JSP Custom tags
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p> 
 * @author jbe
 * Date:   Sep 2, 2003
 * 
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 
 *
 * ---------------------------------------------------
 */

public interface IOasisElementTag {

    /**
     * Getter
     * @return The OasisWebElement
     */
    public Object getElement();

    /**
     * Setter
     * @param obj the OasisWebElement
     */
    public void setElement(Object obj);

    /**
     *  Getter
     * @return Returns the name with which the OasisElements object
     * may be found in context.
     */
    public String getMapName();

    /**
     * Getter
     * @return ElementId
     */
    public String getElementName();

    /**
     * Setter
     * @return StyleClass
     */
    public String getStyleClass();

    /**
     * Setter
     * @param val The CSS style name
     */
    public void setStyleClass(String val);

    /**
     * Setter
     * @param val The title used in hover text by a web browser
     */
    public void setTitle(String val) ;

    /**
     * Setter
     * @param val Value of element
     */
    public void setValue(String val);

    /**
     * Setter
     * @param val URL from database
     */
    public void setUrl(String val);

    /**
     * Setter
     * @param val property
     */
    public void setProperty(String val);
}


