package dti.oasis.tags;

import dti.oasis.util.CollectionUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.MultiboxTag;
import org.apache.struts.util.LabelValueBean;

import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability. Simplifies use of
 * MultiBox tag by iterating through
 * all values in ListOfValues to produce all
 * radiobuttons at once.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 1/6/2004     jbe             Add getFieldColSpan
* 2/5/2004     jbe             Fix label style
* 2/7/2004     jbe             Add Logging
* 8/20/2004    jbe             Add showLabel attribute
* 12/22/2004   jbe             Add checkListOfValues to pick up LOV
* 9/2/2005     jbe             Support Struts 1.2 - replace RequestUtils & ResponseUtils with TagUtils.
* 02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
* 04/20/2011   James           Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
* 12/23/2015   jyang2          168386 - Added isHeaderField field and get/set methods.
* ---------------------------------------------------
*/
public class OasisMultibox extends MultiboxTag implements IOasisTag {

    protected String fieldName;
    protected String isNewValue;
    protected boolean isInTable = true;
    protected String fieldColSpan;
    protected boolean showLabel = true;
    protected boolean isHeaderField = false;

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    public String getFieldColSpan() {
        return fieldColSpan;
    }

    public String getDatafld() {
        return null;
    }

    public String getDatasrc() {
        return null;
    }

    public String getDatatype() {
        return null;
    }       

    public void setFieldColSpan(String fieldColSpan) {
        this.fieldColSpan = fieldColSpan;
    }

    public boolean getIsInTable() {
        return isInTable;
    }

    public void setIsInTable(boolean inTable) {
        isInTable = inTable;
    }

    public String getIsNewValue() {
        return isNewValue;
    }

    public void setIsNewValue(String newValue) {
        isNewValue = newValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    protected String mapName;

    protected OasisFormField field;
    /**
     * The maximum number of radiobuttons to put in one row.
     * To fit all radiobuttons on one row, leave this attribute
     * blank or set to 0
     */
    protected String maxInRow;

    /**
     * A collection of LabelValueBean objects representing
     * all the label/value pairs from which radiobuttons
     * will be created. Three entries will create three
     * radiobuttons.
     */
    protected ArrayList listOfValues;

    /* (non-Javadoc)
     * @see dti.oasis.tags.IOasisTag#getOasisFormField()
     */
    public Object getOasisFormField() {
        return field;
    }

    /* (non-Javadoc)
     * @see dti.oasis.tags.IOasisTag#setOasisFormField(dti.oasis.tags.OasisFormField)
     */
    public void setOasisFormField(Object obj) {
        field = (OasisFormField) obj;
    }

    /**
     * @throws JspException
     */
    private void validate() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "validate");

        try {
            // if not passed, default to size of LOV
            if (maxInRow == null) {
                if (listOfValues != null)
                    maxInRow = String.valueOf(listOfValues.size());
                else
                    maxInRow = "0";
            }
            else {
                int max = Integer.parseInt(maxInRow);
                if (max < 0)
                    throw new JspException("In OasisRadio.validate(), invalid value passed for attribute [maxInRow]. " +
                            " Value passed was [" + maxInRow + ']');
                // make sure it's a number
                maxInRow = String.valueOf(max);
            }
            l.exiting(getClass().getName(), "validate");
        }
        catch (NumberFormatException e) {
            throw new JspException("In OasisRadio.validate(), invalid value passed for attribute [maxInRow]. " +
                    " Value passed was [" + maxInRow + ']');
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag");
        l.fine(toString());
        int rc = EVAL_BODY_BUFFERED;
        OasisTagHelper.setField(this, pageContext);
        checkListOfValues();
        validate();

        // the property should always be the fieldId
        // from the OasisFormField. The name property
        // should always refer to a bean containing
        // the same property for the value
        setProperty(field.getFieldId());
        // make sure we've got a value
        checkValue();

        if (OasisTagHelper.doStartTag(field, pageContext, this))
            rc = iterate();
        OasisTagHelper.doEndTag(this, pageContext);
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;
    }


    // Iterate through the OasisFormField.listOfValues
    // Present a radio button for each
    //
    private int iterate() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "iterate");
        TagUtils util = TagUtils.getInstance();
        int count = listOfValues.size();
        int rc = EVAL_BODY_BUFFERED;

        StringBuffer buffer = new StringBuffer("<span");

        // set the field style unless the jsp
        // has passed us a specific style
        if (getStyleClass() == null)
            setStyleClass(field.getStyle());

        // Default style class for required fields.
        // It should not override current style class from WebWB or passed by JSP.
        // So do it only style class is null.
        if (getStyleClass() == null && field.getIsRequired()){
            setStyleClass(OasisTagHelper.getLabelStyle(field));
        }

        prepareAttribute(buffer, "class", getStyleClass());

        // set style on field
        if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
            if (getStyle() == null) {
                setStyle(field.getStyleInlineForCell());
            } else {
                setStyle(getStyle() + ";" + field.getStyleInlineForCell());
            }
        }

        if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
            prepareAttribute(buffer, "style", getStyle());
        }

        buffer.append(">");
        String strOut = buffer.toString();

        // Determine max # of radiobuttons in a row
        int sz = Integer.parseInt(maxInRow);
        int curr = -1;
        StringBuffer buff = null;

        // If we've been passed a number, open a table & a row
        // also initialize the current rb # variable
        if (sz > 0) {
            util.write(pageContext, "<table width='100%' class=\"");
            util.write(pageContext, OasisTagHelper.STYLE_FIELD_EDIT);
            util.write(pageContext, "\"><tr>");
            curr = 0;
        }

        // loop
        for (int x = 0; x < count; x++) {
            LabelValueBean bean = (LabelValueBean) listOfValues.get(x);

            setValue(bean.getValue());

            // If the current rb# variable has been initialized
            // create table cells
            if (curr >= 0) {
                buff = new StringBuffer();
                // if we've reached the max, start a new row
                if (curr == sz) {
                    buff.append("</TR><TR>");
                    curr = 0;
                }
                curr++;
                // write out the new cell
                util.write(pageContext, buff.append("<TD>").toString());
            }

            // let the RadioTag process
            rc = super.doStartTag();
            rc = super.doEndTag();
            // Add the radiobutton label
            buff = new StringBuffer(strOut).append(bean.getLabel()).append("</span>");
            // if the current rb# variable has been initialized,
            // close the cell
            if (curr >= 0)
                buff.append("</TD>");
            util.write(pageContext, buff.toString());
        }
        if (sz > 0)
            util.write(pageContext, "</tr></table>");
        l.exiting(getClass().getName(), "iterate", new Integer(rc));
        return rc;
    }

    public ArrayList getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(ArrayList listOfValues) {
        this.listOfValues = listOfValues;
    }

    public String getDecodedValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "getDecodedValue");

        Object bean = TagUtils.getInstance().lookup(pageContext, name, null);
        String values[] = null;
        if (bean == null)
            throw new JspException(messages.getMessage("getter.bean", name));
        try {
            values = BeanUtils.getArrayProperty(bean, property);
            if (values == null)
                values = new String[0];
        }
        catch (IllegalAccessException e) {
            throw new JspException(messages.getMessage("getter.access", property, name));
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw new JspException(messages.getMessage("getter.result", property, t.toString()));
        }
        catch (NoSuchMethodException e) {
            throw new JspException(messages.getMessage("getter.method", property, name));
        }
        String decodedValues = CollectionUtils.getDecodedValues(listOfValues, values);
        l.exiting(getClass().getName(), "getDecocedValue", decodedValues);
        return decodedValues;
    }

    public boolean getShowLabel() {
        return showLabel;
    }

    // If the value is null, use the default value
    public void checkValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "checkValue");

        // get the value from the bean
        Object beanValue = TagUtils.getInstance().lookup(pageContext, name, property, null);

        l.exiting(getClass().getName(), "checkValue");
    }


/* (non-Javadoc)
 * @see javax.servlet.jsp.tagext.Tag#release()
 */
    public void release() {
        Logger l = LogUtils.enterLog(getClass(), "release");

        super.release();
        field = null;
        listOfValues = null;
        maxInRow = null;
        fieldName = null;
        mapName = null;
        l.exiting(getClass().getName(), "release");
    }

    /**
     * @return
     */
    public String getMaxInRow() {
        return maxInRow;
    }

    /**
     * @param string
     */
    public void setMaxInRow(String string) {
        maxInRow = string;
    }

    /**
     * @return
     * @throws JspException
     * @see javax.servlet.jsp.tagext.Tag#doEndTag
     */
    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");

        // Need to reset the fields that
        // we may or may not programatically modify
        field = null;
        maxInRow = null;
        l.exiting(getClass().getName(), "doEndTag", new Integer(EVAL_PAGE));
        return EVAL_PAGE;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisMultibox");
        buf.append("{fieldName=").append(fieldName);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",showLabel=").append(showLabel);
        buf.append(",mapName=").append(mapName);
        buf.append(",field=").append(field);
        buf.append(",maxInRow=").append(maxInRow);
        buf.append(",listOfValues=").append(listOfValues);
        buf.append(",isHeaderField=").append(isHeaderField);
        buf.append('}');
        return buf.toString();
    }

    protected void checkListOfValues() throws JspException {
        if (listOfValues == null) {
            listOfValues = OasisTagHelper.findListOfValues(pageContext, field.getFieldId());
        }
    }

    public boolean getIsHeaderField() {
        return isHeaderField;
    }

    public void setIsHeaderField(boolean isHeaderField) {
        this.isHeaderField = isHeaderField;
    }
}
