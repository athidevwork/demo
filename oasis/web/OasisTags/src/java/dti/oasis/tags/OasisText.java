package dti.oasis.tags;

import dti.oasis.http.Module;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.app.ApplicationContext;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.TextTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Extends the STRUTS TextTag. Provides OASIS
 * specific customizability. Provides date & number
 * masks.
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * <p/>
 * Date:   Jul 3, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 11/24/2003        jbe         Add an ID attribute to the date hyperlink
* 1/6/2004          jbe         Add getFieldColSpan
* 2/7/2004          jbe         Add Logging
* 6/28/2004         jbe         Add datasrc and datafld
* 8/20/2004         jbe         Add showLabel attribute
* 10/12/2004        jbe         Handle Currency Format, add numDecimals
* 11/29/2004        jbe         Support phone format
* 12/09/2004	    jbe         Handle Currency for editable text
* 7/6/2005          jbe         Handle numDecimals in editable text
* 9/2/2005          jbe         Support Struts 1.2 - replace RequestUtils with TagUtils.
* 06/13/2006        sxm         remove parameter 'event' from call to calendar()
* 09/02/2006        Larry       added the TYPE_CURRENCY_NEW to deal with the new auto money format.
* 10/31/2006        GCC         Refactored TYPE_CURRENCY_NEW to
*                               TYPE_CURRENCY_FORMATTED.  Changed code in
*                               doStartTag to use "formatMoneyFldVal" and
*                               "unformatMoneyFldVal" as JS function names
*                               for currency formatted fields.
* 01/23/2007        lmm         Moved logic to set on*** handlers to the tagfactory.jsp/edits.js     
* 01/23/2007        wer         Added support for inserting a link to a finder if the display type is finder;
* 02/06/2007        wer         Added support to set readonly finder fields as a disabled text field.
* 07/10/2008        Fred        Added Time field
* 02/06/2009        Jacky       Fix default value of text/date field can not display, for issue 89829
* 04/13/2009        mxg         82494: Changes  for Date Format Internationalization
* 08/07/2009        kshen       Fix the bug that defalut value can not display.
* 08/31/2009        kenney      Fix the bug that displaying percentage text
* 09/23/2009        Fred        Issue 96884. Extend Internationalization to Date / Time fields
* 11/17/2009        kenney      enh to support phone format
* 04/01/2010        kshen      Changed to support email text field.
* 10/15/2010        wfu         109875 - Change to support Chinese Date format.
* 10/18/2010        wfu         109875 - Changed code style for better read.
* 02/18/2011        ldong       112568 - Enchanced to handle the new date format DD/MON/YYYY
* 04/20/2011        James       Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
* 09/20/2011        mxg         Issue #100716: Display Type FORMATTEDNUMBER
* 04/02/2013        jshen       Issue 142992 - Added finderFunctionName attribute to the "a" tag for the Finder type field
* 12/23/2015        jyang2      Issue 168386 - Added isHeaderField field and get/set methods.
* 09/21/2017        kshen       Grid replacement: added css class btnFinderIcon to finder icon.
* 10/12/2017        kshen       Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
* -----------------------------------------------------------------------------
*/
public class OasisText extends TextTag implements IOasisTag {

    protected String fieldName;
    protected String mapName;
    protected OasisFormField field;
    protected String isNewValue;
    protected boolean isInTable = true;
    protected String fieldColSpan;
    protected String datasrc;
    protected String datafld;
    protected String datatype;
    protected boolean showLabel = true;
    protected String numDecimals;
    protected String pattern;
    protected boolean isHeaderField = false;

    public String getNumDecimals() {
        return numDecimals;
    }

    public void setNumDecimals(String numDecimals) {
        this.numDecimals = numDecimals;
    }

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

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
        OasisTagHelper.setField(this, pageContext);
        // the property should always be the fieldId
        // from the OasisFormField. The name property
        // should always refer to a bean containing
        // the same property for the value
        setProperty(field.getFieldId());
        // make sure we've got a value
        checkValue();
//        if (l.isLoggable(Level.FINE)) {
//            l.logp(Level.FINE, getClass().getName(), "doStartTag", " field.getFieldId() / field.getLabel() = " + field.getFieldId() + " / " + field.getLabel());
//        }
        boolean isPhoneNumber = false;
        if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_PHONE)) {
            isPhoneNumber = true;
            super.type="hidden";
        }

        boolean isFormattedNumber = false;
        if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_NUMBER) &&
                field.getDisplayType() != null && field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER)) {
            isFormattedNumber = true;
            super.type="hidden";
        }

        int rc = EVAL_BODY_BUFFERED;
        if (OasisTagHelper.doStartTag(field, pageContext, this)) {
            String onChange = getOnchange();
            String onKeydown = getOnkeydown();
            String maxLength = getMaxlength();
            // Add masks
            boolean isDate = false;
            if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_DATE)) {
                isDate = true;
            }
            boolean isTime = false;
            if (OasisFields.TYPE_TIME.equals(field.getDatatype())) {
                isTime = true;
            }
            boolean isFinder = false;
            if (OasisFields.DISPLAY_TYPE_FINDER_TEXT.equals(field.getDisplayType())) {
                isFinder = true;
            }

            if (!StringUtils.isBlank(field.getMaxLength()))
                setMaxlength(field.getMaxLength());

            // Add support for Date Format Internationalization
            if(FormatUtils.isDateFormatUS()) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "doStartTag", "US Date Format");
                }
                rc = super.doStartTag(); /////////////////////Writes <input............
            } else {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "doStartTag", "non-US Date Format");
                }
                if(!isDate && !isTime) {
                    rc = super.doStartTag();
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "doStartTag", " field.getFieldId() / field.getLabel() = " + field.getFieldId() + " / " + field.getLabel() + " IS NOT DATE");
                    }
                } else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "doStartTag", " field.getFieldId() / field.getLabel() = " + field.getFieldId() + " / " + field.getLabel() + " IS DATE");
                    }
                }
            }
            setMaxlength(maxLength);
            setOnkeydown(onKeydown);
            setOnchange(onChange);
            TagUtils util = TagUtils.getInstance();
            String corePath = Module.getCorePath((HttpServletRequest) this.pageContext.getRequest());

            // Add support for Date Format Internationalization
            if (isDate) {
                if(FormatUtils.isDateFormatUS()) {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "doStartTag", "Handling Dates - US Date Format");
                    }
                    util.write(pageContext, "&nbsp;<a id=\"ADT_");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "\" href=\"javascript:calendar('");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "');\" class='" + OasisTagHelper.STYLE_FIELD_EDIT + "' ");
                    util.write(pageContext, "><img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" src=\""+corePath+"/images/cal.gif\" id=\"btnCal\"></a>");
                }  else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "doStartTag", "Handling Dates - non-US Date Format");
                    }
                    //////////////    Writing a viewable input field
                    String originalProperty = super.getProperty();
                    String originalValue = super.getValue();
                    String newValue = "";
                    /**
                     * ToDO: Enforce calling baseXXX functions evrywhere
                     */
                    String originalOnBlur = super.getOnblur();
                    String customOnBlur = new StringBuffer("baseOnBlur('" + field.getDatatype() + "', event);").toString();

                    String originalOnChange = super.getOnchange();
                    String customOnChange = new StringBuffer("baseOnChange('" + field.getDatatype() + "', event);").toString();

                    // TODO: Make it more generic
                    if(originalValue!=null && !originalValue.trim().equals("")) {
                        String [] dateArr = originalValue.split("/");
                        try {
                            String m = dateArr[0];

                            Integer.parseInt(m);
                            String d = dateArr[1];
                            Integer.parseInt(d);
                            String y = dateArr[2];
                            Integer.parseInt(y);
                            newValue = d+"/"+m+"/"+y;
                            // Changed to support Chinese date format yyyy/MM/dd
                            // TODO: Make it more generic
			                String localeFormat = FormatUtils.getDateFormatForDisplayString();
                            if (!StringUtils.isBlank(localeFormat) && localeFormat.length() == 10 && ("yyyy").equals(localeFormat.substring(0,4))) {
                                newValue = y + "/" + m + "/" + d;
                            }
                            int i = m.length()+d.length()+y.length();
                            if (i != 8) {
                                 throw new Exception("Not a valid date");
                            }
                            if (localeFormat.indexOf("mon") > 0){
                                Calendar ca = Calendar.getInstance();
                                ca.setTime(DateUtils.parseDate(originalValue));
                                newValue = FormatUtils.formatDateForDisplay(ca.getTime());
                            }
                        }  catch (Exception e) {
                            // ArrayIndexOutOfBoundsException, NumberFormatException
                            //if any is not a number    or not dd/mm/yyyy
//                        l.throwing(getClass().getName(), "doStartTag", e);
                            l.logp(Level.WARNING, getClass().getName(), "doStartTag", "An Exception is thrown while parsing a Date Field that has to come in mm/dd/yyyy format: "+originalValue);
                        }

                    }

                    String originalDatafld = this.getDatafld();

                    setValue(newValue);
                    setProperty(originalProperty +FormatUtils.DISPLAY_FIELD_EXTENTION);
                    setOnblur(customOnBlur);
                    setOnchange(customOnChange);
                    if(originalDatafld!=null && !originalDatafld.equals(""))
                        this.setDatafld(originalDatafld+FormatUtils.DISPLAY_FIELD_EXTENTION);

                    rc = super.doStartTag();

                    util.write(pageContext, "&nbsp;<a id=\"ADT_");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "\" href=\"javascript:calendar('");
                    util.write(pageContext, field.getFieldId() +FormatUtils.DISPLAY_FIELD_EXTENTION);
                    util.write(pageContext, "');\" class='" + OasisTagHelper.STYLE_FIELD_EDIT + "' ");
                    util.write(pageContext, "><img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" src=\""+corePath+"/images/cal.gif\" id=\"btnCal\"></a>");

                    setProperty(originalProperty);
                    setValue(originalValue);
                    setOnblur(originalOnBlur);
                    setOnchange(originalOnChange);
                    setDatafld(originalDatafld);
                    super.type="hidden";

                    util.write(pageContext, "&nbsp;"+super.renderInputElement());

                }
            }
            else if (isTime) {
                if(FormatUtils.isDateFormatUS()) {
                    util.write(pageContext, "&nbsp;<a id=\"ADT_");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "\" href=\"javascript:calendar('");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "','time");
                    util.write(pageContext, "');\" class='" + OasisTagHelper.STYLE_FIELD_EDIT + "' ");
                    util.write(pageContext, "><img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" src=\"" + corePath + "/images/cal.gif\" id=\"btnCal\"></a>");
                } else {
                    if (l.isLoggable(Level.FINE)) {
                        l.logp(Level.FINE, getClass().getName(), "doStartTag", "Handling Dates - non-US Date Format");
                    }
                    //////////////    Writing a viewable input field
                    String originalProperty = super.getProperty();
                    String originalValue = super.getValue();
                    String newValue = "";
                    /**
                     * ToDO: Enforce calling baseXXX functions evrywhere
                     */
                    String originalOnBlur = super.getOnblur();
                    String customOnBlur = new StringBuffer("baseOnBlur('" + field.getDatatype() + "', event);").toString();

                    String originalOnChange = super.getOnchange();
                    String customOnChange = new StringBuffer("baseOnChange('" + field.getDatatype() + "', event);").toString();

                    if (!StringUtils.isBlank(originalValue)) {
                        try {
                            if (!FormatUtils.isDateTime(originalValue)) {
                                throw new Exception("Not a valid date time");
                            }
                            newValue = FormatUtils.formatDateTimeForDisplay(originalValue);
                        } catch (Exception e) {
                            l.logp(Level.WARNING, getClass().getName(), "doStartTag", "An Exception is thrown while parsing a Date-Time Field that has to come in mm/dd/yyyy hh:mm a format: " + originalValue);
                        }
                    }
                    String originalDatafld = this.getDatafld();
                    setValue(newValue);
                    setProperty(originalProperty +FormatUtils.DISPLAY_FIELD_EXTENTION);
                    setOnblur(customOnBlur);
                    setOnchange(customOnChange);
                    if(originalDatafld!=null && !originalDatafld.equals(""))
                        this.setDatafld(originalDatafld+FormatUtils.DISPLAY_FIELD_EXTENTION);

                    rc = super.doStartTag();

                    util.write(pageContext, "&nbsp;<a id=\"ADT_");
                    util.write(pageContext, field.getFieldId());
                    util.write(pageContext, "\" href=\"javascript:calendar('");
                    util.write(pageContext, field.getFieldId() +FormatUtils.DISPLAY_FIELD_EXTENTION);
                    util.write(pageContext, "','time");
                    util.write(pageContext, "');\" class='" + OasisTagHelper.STYLE_FIELD_EDIT + "' ");
                    util.write(pageContext, "><img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" src=\""+corePath+"/images/cal.gif\" id=\"btnCal\"></a>");

                    setProperty(originalProperty);
                    setValue(originalValue);
                    setOnblur(originalOnBlur);
                    setOnchange(originalOnChange);
                    setDatafld(originalDatafld);
                    super.type="hidden";
                    util.write(pageContext, "&nbsp;"+super.renderInputElement());
                }
            }
            else if (isFinder) { // changed the finder to baseOnFind instead of href, so srcElement can be obtained from manual clicks
                String UIStyleEdition = (String) ApplicationContext.getInstance().getProperty("UIStyleEdition", "0") ;
                String findImageFileName = "Find" + (UIStyleEdition.equalsIgnoreCase("2") ? UIStyleEdition : "")  + ".gif";
                util.write(pageContext, "&nbsp;<a id=\"AFD_");
                util.write(pageContext, field.getFieldId());
                util.write(pageContext, "\" finderFunctionName=\"find\"");
                util.write(pageContext, " class='" + OasisTagHelper.STYLE_FIELD_EDIT + "' >");
                util.write(pageContext,"<img align=\"middle\" border=\"0\" width=\"16\" height=\"16\" class=\"btnFinderIcon\" src=\""+corePath+"/images/" + findImageFileName + "\"" );
                util.write(pageContext, " onClick=\"baseOnFind('"+field.getFieldId()+"');\"");
                util.write(pageContext, " id=\"btnFind_"+field.getFieldId()+"\">");
                util.write(pageContext, " </a>");
            }
            else if (isPhoneNumber) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "doStartTag", "Handling Phone Number");
                }
                String originalProperty = super.getProperty();
                String originalValue = super.getValue();
                String newValue = "";
                String originalOnBlur = super.getOnblur();
                String customOnBlur = new StringBuffer("baseOnBlur('" + field.getDatatype() + "', event);").toString();

                String originalOnChange = super.getOnchange();
                String customOnChange = new StringBuffer("baseOnChange('" + field.getDatatype() + "', event);").toString();
                if (!StringUtils.isBlank(originalValue)) {
                    newValue = FormatUtils.formatPhoneNumberForDisplay(originalValue);
                } else {
                   newValue = FormatUtils.getLocalPhoneNumberFormat();
                }
                String originalDatafld = this.getDatafld();
                setValue(newValue);
                setProperty(originalProperty + FormatUtils.DISPLAY_FIELD_EXTENTION);
                setOnblur(customOnBlur);
                setOnchange(customOnChange);
                if (!StringUtils.isBlank(originalDatafld))
                    this.setDatafld(originalDatafld + FormatUtils.DISPLAY_FIELD_EXTENTION);
                super.type = "text";
                //Writing a viewable input field
                rc = super.doStartTag();
                setProperty(originalProperty);
                setValue(originalValue);
                setOnblur(originalOnBlur);
                setOnchange(originalOnChange);
                setDatafld(originalDatafld);
            } else if (isFormattedNumber) {
                if (l.isLoggable(Level.FINE)) {
                    l.logp(Level.FINE, getClass().getName(), "doStartTag", "Handling Formatted Number");
                }
                String originalProperty = super.getProperty();
                String originalValue = super.getValue();
                String newValue = "";
                String originalOnBlur = super.getOnblur();
                String customOnBlur = new StringBuffer("baseOnBlur('" + field.getDatatype() + "', event);").toString();

                String originalOnChange = super.getOnchange();
                String customOnChange = new StringBuffer("baseOnChange('" + field.getDatatype() + "', event);").toString();
                if (!StringUtils.isBlank(originalValue))
                    newValue = FormatUtils.formatNumber(originalValue, field.getFormatPattern()); 

                String originalDatafld = this.getDatafld();
                setValue(newValue);
                setProperty(originalProperty + FormatUtils.DISPLAY_FIELD_EXTENTION);
                setOnblur(customOnBlur);
                setOnchange(customOnChange);
                if (!StringUtils.isBlank(originalDatafld))
                    this.setDatafld(originalDatafld + FormatUtils.DISPLAY_FIELD_EXTENTION);
                super.type = "text";
                //Writing a viewable input field
                rc = super.doStartTag();
                setProperty(originalProperty);
                setValue(originalValue);
                setOnblur(originalOnBlur);
                setOnchange(originalOnChange);
                setDatafld(originalDatafld);
            }
        }

        if (OasisFields.DISPLAY_TYPE_EMAIL_TEXT.equals(field.getDisplayType())
            && !StringUtils.isBlank(field.getHref())) {
            String findImageFileName = "mail.gif";
            TagUtils util = TagUtils.getInstance();
            util.write(pageContext, "&nbsp;<a id=\"AFD_");
            util.write(pageContext, field.getFieldId());
            util.write(pageContext, "\" href=\"javascript:");
            util.write(pageContext, field.getHref());
            util.write(pageContext, "\" class='" + OasisTagHelper.STYLE_FIELD_EDIT + "'>");
            util.write(pageContext, "<img class='emailImg' align=\"middle\" border=\"0\" width=\"16\" height=\"16\"" +
                " src=\"" + Module.getCorePath((HttpServletRequest) this.pageContext.getRequest()) + "/images/" + findImageFileName +
                "\" id=\"btnEmailIcon_" + field.getFieldId() + "\"></a>");
        }

        OasisTagHelper.doEndTag(this, pageContext);
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;

    }

    public String getDecodedValue() {
        //To date-time field
        if (OasisFields.TYPE_TIME.equals(field.getDatatype())) {
            if (StringUtils.isBlank(value)) {
                return "";
            }
            if (FormatUtils.isDateFormatUS()) {
                return FormatUtils.formatDateTime(value);
            } else {
                return FormatUtils.formatDateTimeForDisplay(value);
            }
        }
        if (field.getDatatype() != null) {
            if ((field.getDatatype().equals(OasisFields.TYPE_CURRENCY) || field.getDatatype().equals(OasisFields.TYPE_CURRENCY_FORMATTED))) {
                if (!FormatUtils.isLong(numDecimals))
                    return FormatUtils.formatCurrency(value);
                else
                    return FormatUtils.formatCurrency(value, Integer.parseInt(numDecimals));
            } else if (!FormatUtils.isDateFormatUS() && value!= null && (field.getDatatype().equals(OasisFields.TYPE_DATE)) || FormatUtils.isDate(value)) {
                if(FormatUtils.isDate(value))
                    return FormatUtils.formatDateForDisplay(new java.util.Date(value));
                else
                    return value;
            } else if (field.getDatatype().equals(OasisFields.TYPE_PERCENTAGE)) {
                if (!FormatUtils.isLong(numDecimals))
                    return FormatUtils.formatPercentage(value);
                else
                    return FormatUtils.formatPercentage(value, Integer.parseInt(numDecimals));
            } else if (field.getDatatype().equals(OasisFields.TYPE_PHONE)) {
                return FormatUtils.formatPhoneNumberForDisplay(value);
            } else if (field.getDatatype().equals(OasisFields.TYPE_NUMBER) && field.getDisplayType() != null
                    && field.getDisplayType().equals(OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER)) {
                return FormatUtils.formatNumber(value, field.getFormatPattern());
            }  else
                return value;

        } else if (!FormatUtils.isDateFormatUS() && FormatUtils.isDate(value))
            return FormatUtils.formatDateForDisplay(new java.util.Date(value));
        else
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
            Object o = TagUtils.getInstance().lookup(pageContext, name, property, null);

            String beanValue = (o == null) ? "" : String.valueOf(o);

            // update the value
            value = beanValue;
            l.fine(new StringBuffer("beanValue=").append(beanValue).toString());
        }
        if (field.getDatatype() != null &&
                (field.getDatatype().equals(OasisFields.TYPE_CURRENCY) ||  field.getDatatype().equals(OasisFields.TYPE_CURRENCY_FORMATTED)) && StringUtils.isNumeric(value)) {
            if (!FormatUtils.isLong(numDecimals))
                value = FormatUtils.formatCurrency(value);
            else
                value = FormatUtils.formatCurrency(value, Integer.parseInt(numDecimals));
        } else if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_PERCENTAGE) && StringUtils.isNumeric(value)) {
            if (!FormatUtils.isLong(numDecimals))
                value = FormatUtils.formatPercentage(value);
            else
                value = FormatUtils.formatPercentage(value, Integer.parseInt(numDecimals));
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
     * Add additional attributes before returning styles
     *
     * @return String
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

        if (datatype != null)
            buff.append(" datatype=\"").append(datatype).append("\"");
        if (pattern != null)
            buff.append(" formatPattern=\"").append(pattern).append("\"");

        return buff.append(super.prepareStyles()).toString();
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisText");
        buf.append("{fieldName=").append(fieldName);
        buf.append(",mapName=").append(mapName);
        buf.append(",field=").append(field);
        buf.append(",isNewValue=").append(isNewValue);
        buf.append(",isInTable=").append(isInTable);
        buf.append(",fieldColSpan=").append(fieldColSpan);
        buf.append(",datasrc=").append(datasrc);
        buf.append(",datafld=").append(datafld);
        buf.append(",datatype=").append(datatype);
        buf.append(",showLabel=").append(showLabel);
        buf.append(",numDecimals=").append(numDecimals);
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
