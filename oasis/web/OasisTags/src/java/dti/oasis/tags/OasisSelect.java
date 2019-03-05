package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.codelookupmgr.CodeLookupManager;
import dti.oasis.http.Module;
import dti.oasis.util.CollectionUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.OptionsCollectionTag;
import org.apache.struts.taglib.html.SelectTag;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability. Simplifies use of
 * Select tag by iterating through
 * all values in ListOfValues to produce all
 * options at once.
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
* 2/7/2004     jbe             Add Logging
* 6/3/2004     jbe             Prevent super.doEndTag on hidden or readonly fields
* 6/28/2004    jbe             Add datasrc and datafld
* 8/20/2004    jbe             Add showLabel attribute
* 12/22/2004   jbe             Add checkListOfValues to pick up LOV
* 9/2/2005     jbe             Support Struts 1.2 - replace RequestUtils & ResponseUtils with TagUtils
* 01/23/2007   wer             Added to make SELECT field hidden if displaying the readonly codelookup as label
* 02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
* 07/21/2008   kshen           Added code to display nothing instead of "-SELECT-" when the code
*                              of a readonly lov field is "-1".
* 06/08/2010   clm             Line 243 use ">" instead of "/>" for html tag a
* 04/20/2011   James           Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
* 05/10/2011   clm             Issue#118994 set width for enterable drop down list
* 09/19/2011   jshen           Issue 125334 - remove invalid top/left class for enterable select component
* 09/15/2012   jxgu            Issue 135400 - always add class noEntryFinderReq/noEntryFinder for multiple select popup
* 04/02/2013   jshen           Issue 142992 - 1) Added onClick event baseOnFind to the "img" tag of multiple select field
*                                             2) Removed href attribute of "a" tag for the multiple select field
*                                             3) Added finderFunctionName attribute to the "a" tag for the multiple select field
* 08/26/2013   jxgu            Issue 147722 encode the value for MultiSelectText
* 04/15/2014   htwang          Issue 153729 - Decrease the width of top text field to make sure the whole down arrow of the dropdown are visible
* 04/27/2015   cv              Issue 162430 - Added maxLength so that the maxLength attribute can be used for validation.
* 12/23/2015   jyang2          168386 - Added isHeaderField field and get/set methods.
* 09/21/2017   kshen           Grid replacement: added css class btnFinderIcon to finder icon.
* 10/12/2017   kshen           Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
* ---------------------------------------------------
*/
public class OasisSelect extends SelectTag implements IOasisTag {

    protected OasisFormField field;
    protected String fieldName;
    protected String mapName;
    protected String isNewValue;
    protected boolean isInTable = true;
    protected String fieldColSpan;
    protected String datasrc;
    protected String datafld;
    protected String datatype;
    protected String maxLength;
    protected boolean showLabel = true;
    protected boolean isHeaderField = false;

    /**
     * Should the label values in the options collection be filtered for HTML sensitive characters?
     */
    protected boolean filter = true;

    public boolean getFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

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

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }


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

    protected void checkListOfValues() throws JspException {
        if (listOfValues == null) {
            listOfValues = OasisTagHelper.findListOfValues(pageContext, field.getFieldId());
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
        // the property should always be the fieldId
        // from the OasisFormField. The name property
        // should always refer to a bean containing
        // the same property for the value
        setProperty(field.getFieldId());
        // make sure we've got a value
        checkValue();

        TagUtils util = TagUtils.getInstance();

        boolean continueProcessing = OasisTagHelper.doStartTag(field, pageContext, this);
        if (continueProcessing) {
            // Create MSVALFieldIdList span for each MULTISELECT field
            if ("MULTISELECT".equals(field.getDisplayType())||isMultiSelectPopup(field.getDisplayType())) {
                boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
                if(useJqxGrid)
                    util.write(pageContext, "<span class='MSVALFieldIdList dti-hide' name='MSVALFieldIdList'>" +
                            field.getFieldId() + "</span>\n");
                else
                    util.write(pageContext, "<span class='MSVALFieldIdList' style='display:none;' name='MSVALFieldIdList'>" +
                            field.getFieldId() + "</span>\n");
            }
            boolean isEnterableDropdown = false;
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            if (request.getAttribute(field.getFieldId() + ".enterableSelect") != null) {
                isEnterableDropdown = ((Boolean) request.getAttribute(field.getFieldId() + ".enterableSelect")).booleanValue();
            }
            // Create the text field for Text and Dropdown combined component
            if (isEnterableDropdown) {
                if (!"MULTISELECT".equals(field.getDisplayType()) && !isMultiSelectPopup(field.getDisplayType())) {
                    util.write(pageContext, "<input name='" + field.getFieldId() + "EnterableDropdownText' type='text' id='" + field.getFieldId() +
                            "EnterableDropdownText' onChange=\"baseOnChange('ST');\" " +
                            " class=\"" + OasisTagHelper.STYLE_FIELD_EDIT + "\" " +
                            " style='position:absolute; z-index:2; width:126px;' />");
                }
                // Create EditableTxtFieldIdList span for each field
                util.write(pageContext, "<span class='EnterableDropdownTxtFieldIdList' style='display:none;' name='EnterableDropdownTxtFieldIdList'>"
                        + field.getFieldId() + "</span>\n");
            }



            if (isMultiSelectPopup(field.getDisplayType())) {
                setStyle("display: none");
            } else if (isEnterableDropdown) {
                setStyle("width: 150px; z-index:1");
                setStyleId(field.getFieldId());
            }
            rc = super.doStartTag();
            rc = processOptions();
        }
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;
    }

    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");
        int rc = EVAL_PAGE;

        super.doEndTag();

        TagUtils util = TagUtils.getInstance();
        if (isMultiSelectPopup(field.getDisplayType())) {
            String corePath = Module
                    .getCorePath((HttpServletRequest) this.pageContext
                            .getRequest());

            String UIStyleEdition = (String) ApplicationContext.getInstance()
                    .getProperty("UIStyleEdition", "0");
            String findImageFileName = (UIStyleEdition.equalsIgnoreCase("2") ? "Find2": "find") + ".gif";
            String comboDivId = field.getFieldId()+"LookupFieldDiv";
            String comboFieldId = field.getFieldId()+"MultiSelectText";
            String comboFieldText = pageContext.getRequest().getAttribute(comboFieldId)== null?"":(String)pageContext.getRequest().getAttribute(comboFieldId);
            util.write(pageContext, "<div id='");
            util.write(pageContext, comboDivId+"'");
            util.write(pageContext, " class=\"" + OasisTagHelper.STYLE_FIELD_EDIT + "\" >");

            if (field.getIsRequired()) {
                util.write(pageContext, "<input type='text' name='");
            }
            else {
                util.write(pageContext, "<input type='text' name='");
            }

            util.write(pageContext, comboFieldId+"' value='"+ ResponseUtils.filter(comboFieldText) +"'");

            if (!StringUtils.isBlank(field.getCols())) {
                util.write(pageContext, " size='" + field.getCols() + "'");
            }

            // add style
            String classNames = null;
            if (getStyleClass() != null) {
                classNames = this.getStyleClass();
            } else if (field.getStyle() != null) {
                classNames = field.getStyle();
            }
            String noDataEntryClassName = null;
            if (field.getIsRequired()) {
                if (OasisTagHelper.STYLE_FORM_FIELD_REQUIRED.equals(classNames)) {
                    classNames = null;
                }
                noDataEntryClassName = "noEntryMultipleSelectPopupReq";
            } else {
                noDataEntryClassName = "noEntryFinder";
            }
            if (classNames == null) {
                classNames = noDataEntryClassName;
            } else {
                classNames = classNames + " " + noDataEntryClassName;
            }
            util.write(pageContext, " class=\"" + classNames + "\" ");

            // set style on field
            if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
                if (getStyle() == null) {
                    setStyle(field.getStyleInlineForCell());
                } else {
                    setStyle(getStyle() + ";" + field.getStyleInlineForCell());
                }
            }

            if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
                util.write(pageContext, " style=\"" + getStyle() + "\"");
            }

            util.write(pageContext, " onkeydown=\"baseOnKeyDown('ST', event);\" onkeypress=\"baseOnKeyPress('ST', event);\"/>");
            util.write(pageContext, "&nbsp;<a id=\"AFD_");
            util.write(pageContext, field.getFieldId());
            util.write(pageContext, "\" finderFunctionName=\"popupMultiSelectDiv\" >");
            util.write(pageContext, "<img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" class=\"btnFinderIcon\" src=\""
                    + corePath + "/images/" + findImageFileName + "\"");
            util.write(pageContext, " onClick=\"baseOnFind('"+field.getFieldId()+"');\"");
            util.write(pageContext, " id=\"btnFind\">");
            util.write(pageContext, " </a></div>");

        }

        OasisTagHelper.doEndTag(this, pageContext);
        // Need to reset the fields that
        // we may or may not programatically modify
        field = null;
        value = null;
        l.exiting(getClass().getName(), "doEndTag", new Integer(rc));
        return rc;
    }

    protected int processOptions() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "processOptions");
        int rc = EVAL_BODY_BUFFERED;
        OptionsCollectionTag options = new OptionsCollectionTag();
        options.setFilter(filter);
        options.setPageContext(pageContext);
        pageContext.setAttribute("listOfValues", createOptionsBean());
        options.setProperty("listOfValues");
        options.setName("listOfValues");
        options.setValue("value");
        options.setLabel("label");
        rc = options.doStartTag();
        l.exiting(getClass().getName(), "processOptions", new Integer(rc));
        return rc;

    }


    protected boolean isMultiSelectPopup(String tagType){
        return "MULTISELECTPOPUP".equalsIgnoreCase(tagType);
    }

    // Dynamically creates a bean with a single property
    // called "listOfValues". This bean can be used as a
    // container for the struts OptionsCollection tag
    // We use Apache BeanUtils for this
    private DynaBean createOptionsBean() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "createOptionsBean");
        // Set up the single attribute
        DynaProperty[] props = new DynaProperty[]{
                new DynaProperty("listOfValues", ArrayList.class)
        };
        // Define a DynaClass
        BasicDynaClass dynaClass = new BasicDynaClass("options", null, props);

        // Instantiate the bean
        DynaBean options = null;
        try {
            options = dynaClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new JspException(ie.getMessage());
        }
        catch (IllegalAccessException iae) {
            throw new JspException(iae.getMessage());
        }

        // set the value in the single attribute
        options.set("listOfValues", listOfValues);
        l.exiting(getClass().getName(), "createOptionsBean", options);
        return options;
    }

    public ArrayList getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(ArrayList listOfValues) {
        this.listOfValues = listOfValues;
    }

    public String getDecodedValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "getDecodedValue");
        if (this.multiple == null) {
            String decodedValue = CollectionUtils.getDecodedValue(listOfValues, value);
            if (field.getIsReadOnly()
                    && CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                decodedValue = "";
            }

            l.exiting(getClass().getName(), "getDecodedValue", decodedValue);
            return decodedValue;
        }
        else {
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
            l.exiting(getClass().getName(), "getDecodedValue", decodedValues);
            return decodedValues;
        }
    }


    public boolean getShowLabel() {
        return showLabel;
    }

    // If the value is null, use the default value
    public void checkValue() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "checkValue");
        // if we don't have a value passed in
        if (value == null) {
            // get the value from the bean
            Object beanValue = TagUtils.getInstance().lookup(pageContext, name, property, null);

            // value is null, use blank
            if (beanValue == null)
                beanValue = "";

            // If not multiple, then this should be a String or primitive
            if (getMultiple() == null && !(beanValue instanceof Object[]))
                value = (beanValue == null) ? "" : String.valueOf(beanValue);
                // If not multiple, this should be a String array
            else if (getMultiple() != null) {
                // default value is null, use blank
                if (beanValue == null)
                    beanValue = new String[]{""};
                    // if a String, convert to array
                else if (beanValue instanceof String)
                    beanValue = new String[]{(String) beanValue};

                // We need to store the value in the bean for the STRUTS tag to pick it up
                Object bean = TagUtils.getInstance().lookup(pageContext, name, null);
                l.fine(new StringBuffer("beanValue=").append(beanValue).toString());
                try {
                    BeanUtils.setProperty(bean, property, beanValue);
                }
                catch (IllegalAccessException e) {
                    throw new JspException(e.getMessage());
                }
                catch (InvocationTargetException e) {
                    throw new JspException(e.getMessage());
                }
            }
        }
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
        fieldName = null;
        mapName = null;
        l.exiting(getClass().getName(), "release");
    }

    /**
     * Add additional attributes before getting styles
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

        if (maxLength != null)
            buff.append(" maxLength=\"").append(maxLength).append("\"");

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
        buf.append("dti.oasis.tags.OasisSelect");
        buf.append("{field=").append(field);
        buf.append(",fieldName=").append(fieldName);
        buf.append(",mapName=").append(mapName);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",datasrc=").append(datasrc);
        buf.append(",datafld=").append(datafld);
        buf.append(",showLabel=").append(showLabel);
        buf.append(",listOfValues=").append(listOfValues);
        buf.append(",maxLength=").append(maxLength);
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
