package dti.oasis.tags;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.Tag;

/**
 * Interface all OASIS JSP Custom tags should implement
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * @author jbe
 * Date:   Jun 20, 2003
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1/6/2004     jbe             Add getFieldColSpan
 * 12/23/2015       jyang2      Added getIsHeaderField and setIsHeaderField.
 * ---------------------------------------------------
 */
public interface IOasisTag extends IBaseOasisTag,Tag{
    /**
     * @return value of input field
     */
	public String getValue();

    /**
     *
     * @param string value of input field
     */
	public void setValue(String string);

    /**
     *
     * @return CSS class
     */
	public String getStyleClass();

    /**
     *
     * @param string CSS Class
     */
	public void setStyleClass(String string);

    /**
     * Sets the style attribute.
     * @param style
     */
    public void setStyle(String style);

    /**
     * Returns the style attribute.
     * @return
     */
    public String getStyle();

    /**
     *
     * @return STRUTS property (generally fieldId)
     */
	public String getProperty();

    /**
     *
     * @param string STRUTS property (generally fieldId)
     */
	public void setProperty(String string);

    /**
     * Returns the value of the field, decoding based on
     * a listOfValues property if one exists
     * @return decoded value of field
     * @throws JspException
     */
	public String getDecodedValue() throws JspException;

    /**
     *
     * @return whether to show the field's label
     */
	public boolean getShowLabel();

    /**
     * Checks the field's value as it relates to the default value
     * @throws JspException
     */
	public void checkValue() throws JspException;

    /**
     *
     * @return true/false whether tag should render table cells
     */
    public boolean getIsInTable();

    /**
     *
     * @param inTable true/false whether tag should render table cells
     */
    public void setIsInTable(boolean inTable);

    /**
     *
     * @return colspan to use when creating the field
     */
    public String getFieldColSpan();

    /**
     *
     * @return true/false whether it is a header field
     */
    public boolean getIsHeaderField();

    /**
     *
     * @param isHeaderField true/false whether it is a header field.
     */
    public void setIsHeaderField(boolean isHeaderField);


    String getDatafld();

    String getDatasrc();

    String getDatatype();
}
