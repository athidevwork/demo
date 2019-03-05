package dti.oasis.tags;

import dti.oasis.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.taglib.html.CheckboxTag;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability.
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
 * 2/5/2004     jbe             Fix getDecodedValue and fix label css
 * 2/7/2004     jbe             Add Logging
 * 6/28/2004    jbe             Add datasrc and datafld
 * 8/20/2004    jbe             Add showLabel attribute
 * 9/2/2005     jbe             Support Struts 1.2 - replace RequestUtils and ResponseUtils
 *                              with TagUtils.getInstance()
 * 02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
 * 07/14/2009   kenney          Add id='***FLDLABEL' for the checkbox span
 * 10/18/2011   Michael Li      for issue 126170
 * 12/23/2015   jyang2          168386 - Added isHeaderField field and get/set methods.
 * 07/11/2018   dpang           194134 - Grid replacement: add attributes for jqxgrid.
 * ---------------------------------------------------
 */
public class OasisCheckbox extends CheckboxTag implements IOasisTag {

    protected OasisFormField field;
    protected String fieldName;
    protected String mapName;
    protected ArrayList listOfValues;
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

    /* (non-Javadoc)
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doStartTag");
        l.fine(toString());
        int rc = EVAL_BODY_BUFFERED;
        OasisTagHelper.setField(this, pageContext);
        // the property should always be the fieldId
        // from the OasisFormField. The name property
        // should always refer to a bean containing
        // the same property for the value
        setProperty(field.getFieldId());
        // make sure we've got a value
        checkValue();

        if (OasisTagHelper.doStartTag(field, pageContext, this)) {
            TagUtils util = TagUtils.getInstance();
            rc = super.doStartTag();
            
            // add a hidden input for bean value on the page, it is for read-only or hidden field
            StringBuffer buff = new StringBuffer();
            buff.append("<input type='hidden' ");
            prepareAttribute(buff, "id", field.getFieldId() + "_READONLY_ID");
            prepareAttribute(buff, "name", field.getFieldId() + "_DUMMY_ID");
            prepareAttribute(buff, "value", ResponseUtils.filter(getBeanValue()));

            boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);

            if (!StringUtils.isBlank(getDatasrc())) {
                if (useJqxGrid) {
                    prepareAttribute(buff, "data-dti-datasrc", getDatasrc());
                } else {
                    prepareAttribute(buff, "datasrc", getDatasrc());
                }
            }

            if (!StringUtils.isBlank(getDatafld()))  {
                if (useJqxGrid) {
                    prepareAttribute(buff, "data-dti-datafld", getDatafld());
                } else {
                    prepareAttribute(buff, "datafld", getDatafld());
                }
            }

            buff.append(">\n");
            buff.append("<input type='hidden' ");
            prepareAttribute(buff, "name", field.getFieldId() + "_IS_CHECKBOX");
            prepareAttribute(buff, "value", "Y");
            buff.append(">\n");
            //execute Javascript to update the input name
            buff.append("<script type='text/javascript'>switchInputNameForCheckBox(\"").
                append(field.getFieldId()).append("\");</script>\n");

            util.write(pageContext, buff.toString());
        }
        OasisTagHelper.doEndTag(this, pageContext);
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;
    }

    @Override
    protected void prepareOtherAttributes(StringBuffer handlers) {
        // add id attribute
        prepareAttribute(handlers, "id", field.getFieldId() + "_FIELD_ID");
        super.prepareOtherAttributes(handlers);
    }

    public String getDecodedValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "getDecodedValue");
        String beanValue = (String) TagUtils.getInstance().lookup(pageContext, name, property, null);
        String val = null;
        if (listOfValues == null)
            val = beanValue;
        else
            val = CollectionUtils.getDecodedValue(listOfValues, beanValue);
        l.exiting(getClass().getName(), "getDecodedValue", val);
        return val;
    }

    public boolean getShowLabel() {
        return showLabel;
    }

    public ArrayList getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(ArrayList listOfValues) {
        this.listOfValues = listOfValues;
    }

    // If the value is null, use the default value
    public void checkValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "checkValue");
        TagUtils util = TagUtils.getInstance();
        // get the value from the bean
        Object o = util.lookup(pageContext, name, property, null);
        String beanValue = (o == null) ? "" : String.valueOf(o);

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
        l.exiting(getClass().getName(), "doEndTag", new Integer(EVAL_PAGE));
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
        fieldName = null;
        mapName = null;
        l.exiting(getClass().getName(), "release");

    }

    /**
     * get the value of bean
     * @return
     * @throws JspException
     */
    public String getBeanValue() throws JspException {
        Object o = TagUtils.getInstance().lookup(pageContext, name, property, null);
        String beanValue = (o == null) ? "" : String.valueOf(o);
        return beanValue;
    }

    /**
     * Add attributes before getting style
     *
     * @return
     * @throws JspException
     */
    protected String prepareStyles() throws JspException {
        StringBuilder builder = new StringBuilder();
        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);

        if (datasrc != null) {
            if (useJqxGrid) {
                builder.append(" data-dti-datasrc=\"").append(datasrc).append("\"");
            } else {
                builder.append(" datasrc=\"").append(datasrc).append("\"");
            }
        }

        if (datafld != null) {
            if (useJqxGrid) {
                builder.append(" data-dti-datafld=\"").append(datafld).append("\"");
            } else {
                builder.append(" datafld=\"").append(datafld).append("\"");
            }
        }

        return builder.append(super.prepareStyles()).toString();
    }
    
    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisCheckbox");
        buf.append("{field=").append(field);
        buf.append(",fieldName=").append(fieldName);
        buf.append(",mapName=").append(mapName);
        buf.append(",listOfValues=").append(listOfValues);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",datasrc=").append(datasrc);
        buf.append(",datafld=").append(datafld);
        buf.append(",showLabel=").append(showLabel);
        buf.append(",isHeaderField=").append(isHeaderField);
        buf.append('}');
        return buf.toString();
    }

    public boolean getIsHeaderField() {
        return isHeaderField;
    }

    public void setIsHeaderField(boolean isHeaderField) {
        this.isHeaderField = isHeaderField;
    }
}
