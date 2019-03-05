package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.http.Module;
import dti.oasis.http.RequestIds;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.TextareaTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.logging.Logger;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* ---------------------------------------------------
* 1/6/2004     jbe             Add getFieldColSpan
* 2/7/2004     jbe             Add Logging
* 6/28/2004    jbe             Add datasrc and datafld
* 8/20/2004    jbe             Add showLabel attribute
* 9/2/2005     jbe             Support Struts 1.2 - replace RequestUtils with TagUtils.
* 7/24/2006    sjz             Add maxlength in prepareStyles
* 01/23/2007   lmm             Moved logic to set on*** handlers to the tagfactory.jsp/edits.js
* 02/27/2008   wer             Fixed check if isNewValue is null to check use StringUtils.isBlank
* 03/07/2008   yhchen          Support user defined url for notes type field, to fix issue 80433
* 02/24/2011   kshen           Changed to get notes icons path from application properties.
* 04/20/2011   James           Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
* 12/23/2015   jyang2          Issue#168386 add isHeaderField field and get/set methods.
* ---------------------------------------------------
*/
public class OasisTextArea extends TextareaTag implements IOasisTag {

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
        boolean isNote = false;
        boolean isTextAreaPopup = false;
        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
        OasisTagHelper.setField(this, pageContext);
        // the property should always be the fieldId
        // from the OasisFormField. The name property
        // should always refer to a bean containing
        // the same property for the value
        setProperty(field.getFieldId());
        // make sure we've got a value
        checkValue();
        if (OasisTagHelper.doStartTag(field, pageContext, this)) {
            if (!StringUtils.isBlank(field.getMaxLength())) {
                setMaxlength(field.getMaxLength());
            }

            if (field.getDisplayType() != null) {
                if (field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_NOTE_TEXT)) {
                    isNote = true;
                }
                if (field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_TEXTAREA_POPUP)) {
                    isTextAreaPopup = true;
                }
            }
            TagUtils util = TagUtils.getInstance();

            if (field.getDisplayType() != null && field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_NOTE_TEXT)) {
                util.write(pageContext, "&nbsp;<div id=\"d_");
                util.write(pageContext, field.getFieldId());
                if(useJqxGrid)
                    util.write(pageContext, "\" class=\"dti-hide\" style='width:100%'>");
                else
                    util.write(pageContext, "\" style='display:none;width:100%'>");
                util.write(pageContext, "<span id='NoteFieldIdList' name='NoteFieldIdList'>" + field.getFieldId() + "</span>");
            }

            if(isTextAreaPopup){
                String customOnBlur = new StringBuffer("maintainNoteImage('" + field.getFieldId() +
                        "', 'IMG_" + field.getFieldId() + "');").toString();
                setOnblur(customOnBlur);
            }
            
            this.renderTextareaElement();
            rc = super.doStartTag();

            String corePath = Module.getCorePath((HttpServletRequest) this.pageContext.getRequest());

            // check if we've got a url to display
            String url = field.getHref();
            if (StringUtils.isBlank(url)) {
                url = (String) TagUtils.getInstance().lookup(pageContext, field.getFieldId() + "ROURL", null);
            }
            if (isNote) {
                util.write(pageContext, "</div>");

                util.write(pageContext, "&nbsp;<a class=\"notes\" id=\"ANT_");
                util.write(pageContext, field.getFieldId());
                util.write(pageContext, "\"");

                if (StringUtils.isBlank(url)) {
                    util.write(pageContext, "href=\"javascript:if(eval('window.maintainNote')) maintainNote('");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "', 'IMG_" + field.getFieldId() + "', true, ");
                    util.write(pageContext, OasisTagHelper.JS_VAR_READONLY_FIELD_PREFIX + field.getFieldId());
                    util.write(pageContext, ");\" >");
                }
                else {
                    //use user defined url
                    util.write(pageContext, "href=\"");
                    util.write(pageContext, url);
                    util.write(pageContext, "\" >");
                }

                String noNotesImage = (String) pageContext.getAttribute(RequestIds.NO_NOTES_IMAGE, PageContext.REQUEST_SCOPE);
                if (StringUtils.isBlank(noNotesImage)) {
                    noNotesImage = ApplicationContext.getInstance().getProperty(
                            dti.oasis.http.RequestIds.NO_NOTES_IMAGE, "/images/nonotes.gif");
                }

                util.write(pageContext, "<img id=\"IMG_" + field.getFieldId() + "\" align=\"top\" border=\"0\" src=\"" + corePath + noNotesImage + "\">");
                util.write(pageContext, "</a>");
            }

            if (isTextAreaPopup) {
                /////
                util.write(pageContext, "&nbsp;<div id=\"d_");
                util.write(pageContext, field.getFieldId());
                if(useJqxGrid)
                    util.write(pageContext, "\" class=\"dti-hide\" style='width:100%'>");
                else
                    util.write(pageContext, "\" style='display:none;width:100%'>");
                util.write(pageContext, "<span id='NoteFieldIdList' name='NoteFieldIdList'>" + field.getFieldId() + "</span>");                

                ////
                util.write(pageContext, "</div>");

                util.write(pageContext, "&nbsp;<a class=\"notes\" id=\"ANT_");
                util.write(pageContext, field.getFieldId());
                util.write(pageContext, "\"");

                if (StringUtils.isBlank(url)) {
                    util.write(pageContext, "href=\"javascript:if(eval('window.maintainNote')) maintainNote('");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "', 'IMG_" + field.getFieldId() + "', true, ");
                    util.write(pageContext, OasisTagHelper.JS_VAR_READONLY_FIELD_PREFIX + field.getFieldId());
                    util.write(pageContext, ",true,'"+field.getLabel()+"'");
                    util.write(pageContext, ");\" >");
                }
                else {
                    //use user defined url
                    util.write(pageContext, "href=\"");
                    util.write(pageContext, url);
                    util.write(pageContext, "\" >");
                }

                String notesImage = (String) pageContext.getAttribute(RequestIds.NOTES_IMAGE, PageContext.REQUEST_SCOPE);
                if (StringUtils.isBlank(notesImage)) {
                    notesImage = ApplicationContext.getInstance().getProperty(
                         RequestIds.NOTES_IMAGE, "/images/notes.gif");
                }

                util.write(pageContext, "<img id=\"IMG_" + field.getFieldId() + "\" align=\"top\" border=\"0\" src=\"" + corePath + notesImage + "\">");
                util.write(pageContext, "</a>");
            }
        }

        OasisTagHelper.doEndTag(this, pageContext);
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;

    }

    public String getDecodedValue() {
        return value;
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
            Object beanValueObject = TagUtils.getInstance().lookup(pageContext, name, property, null);
            String beanValue = (beanValueObject == null) ? "" : String.valueOf(beanValueObject);

            // update the value
            value = beanValue;
            l.fine(new StringBuffer("beanValue=").append(beanValue).toString());
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

        if (getMaxlength() != null) {
            buff.append(" maxlength=\"");
            buff.append(getMaxlength());
            buff.append("\"");
        }
        return buff.append(super.prepareStyles()).toString();
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisTextArea");
        buf.append("{field=").append(field);
        buf.append(",fieldName=").append(fieldName);
        buf.append(",mapName=").append(mapName);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",datasrc=").append(datasrc);
        buf.append(",datafld=").append(datafld);
        buf.append(",showLabel=").append(showLabel);
        buf.append(",maxlength=").append(getMaxlength());
        buf.append(",isHeaderField=").append(isHeaderField);
        buf.append('}');
        return buf.toString();
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

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }
    public boolean getIsHeaderField() {
        return isHeaderField;
    }

    public void setIsHeaderField(boolean isHeaderField) {
        this.isHeaderField = isHeaderField;
    }
}
