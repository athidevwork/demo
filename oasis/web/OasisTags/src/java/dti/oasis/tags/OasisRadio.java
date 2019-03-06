package dti.oasis.tags;

import dti.oasis.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.taglib.html.RadioTag;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.*;

import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability. Simplifies use of
 * Radio tag by iterating through
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
* 6/28/2004    jbe             Add datasrc and datafld
* 8/20/2004    jbe             Add showLabel attribute
* 12/22/2004   jbe             Add checkListOfValues to pick up LOV
* 9/2/2005     jbe             Support Struts 1.2, replace RequestUtils & ResponseUtils with TagUtils.
* 02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
* 04/20/2011   James           Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
* 12/23/2015   jyang2          168386 - Added isHeaderField field and get/set methods.
* ---------------------------------------------------
*/

public class OasisRadio extends RadioTag implements IOasisTag {

    protected OasisFormField field;
    protected String fieldName;
    protected String mapName;
    protected String isNewValue;
    protected boolean isInTable = true;
    protected String fieldColSpan;
    protected String datasrc;
    protected String datafld;
    protected String datatype;    
    protected boolean showLabel = true;
    protected boolean isHeaderField = false;

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    public String getDatasrc() {
        return datasrc;
    }

    public void setDatasrc(String datasrc) {
        this.datasrc = datasrc;
    }

    public String getDatafld() {
        return datafld;
    }

    public void setDatafld(String datafld) {
        this.datafld = datafld;
    }

    public String getFieldColSpan() {
        return fieldColSpan;
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
        }
        catch (NumberFormatException e) {
            throw new JspException("In OasisRadio.validate(), invalid value passed for attribute [maxInRow]. " +
                    " Value passed was [" + maxInRow + ']');
        }
        l.exiting(getClass().getName(), "validate");
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

    /**
     * Iterate through the OasisFormField.listOfValues
     * Present a radio button for each
     *
     * @return
     * @throws JspException
     */
    private int iterate() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "iterate");
        int count = listOfValues.size();
        int rc = EVAL_BODY_BUFFERED;
        TagUtils util = TagUtils.getInstance();

        StringBuffer buffer = new StringBuffer("<span");
        prepareAttribute(buffer, "class", OasisTagHelper.getLabelStyle(field));
        if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
            prepareAttribute(buffer, "style", field.getStyleInlineForCell());
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
            // put the current LabelValueBean in the pagecontext
            // for use by org.apache.struts.taglib.html.RadioTag
            pageContext.setAttribute("rbi", bean);
            // set the name of the LabelValueBean in the page context
            setIdName("rbi");
            // set the property of the LabelValueBean
            setValue("value");

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

    public String getDecodedValue() {
        return CollectionUtils.getDecodedValue(listOfValues, value);
    }

    public boolean getShowLabel() {
        return showLabel;
    }

    // If the value is null, use the default value
    public void checkValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "checkValue");
        TagUtils util = TagUtils.getInstance();
        // if we don't have a value passed in
        if (value == null) {
            // get the value from the bean
            Object o = util.lookup(pageContext, name, property, null);
            String beanValue = (o == null) ? "" : String.valueOf(o);

            // update the value
            value = beanValue;
            // We need to store the value in the bean for the STRUTS tag to pick it up
            Object bean = util.lookup(pageContext, name, null);
            try {
                l.fine(new StringBuffer("beanValue=").append(value).toString());
                BeanUtils.setProperty(bean, property, value);
            }
            catch (IllegalAccessException e) {
                throw new JspException(e.getMessage());
            }
            catch (InvocationTargetException e) {
                throw new JspException(e.getMessage());
            }
        }
        l.exiting(getClass().getName(), "checkValue");
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
        value = null;
        maxInRow = null;
        idName = null;
        l.exiting(getClass().getName(), "release", new Integer(EVAL_PAGE));
        return EVAL_PAGE;
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
     * Add attributes before getting style
     *
     * @return
     * @throws JspException
     */
    protected String prepareStyles() throws JspException {
        StringBuffer buff = new StringBuffer();

        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);

        if (datasrc != null) {
            if (useJqxGrid) {
                buff.append(" data-dti-datasrc=\"").append(datasrc).append("\"");
            } else {
                buff.append(" datasrc=\"").append(datasrc).append("\"");
            }
        }

        if (datafld != null) {
            if (useJqxGrid) {
                buff.append(" data-dti-datafld=\"").append(datafld).append("\"");
            } else {
                buff.append(" datafld=\"").append(datafld).append("\"");
            }
        }

        return buff.append(super.prepareStyles()).toString();
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisRadio");
        buf.append("{field=").append(field);
        buf.append(",fieldName=").append(fieldName);
        buf.append(",mapName=").append(mapName);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",datasrc=").append(datasrc);
        buf.append(",datafld=").append(datafld);
        buf.append(",showLabel=").append(showLabel);
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