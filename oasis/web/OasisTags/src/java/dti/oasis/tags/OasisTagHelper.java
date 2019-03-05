package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.http.RequestIds;
import dti.oasis.struts.ActionHelper;
import dti.oasis.struts.IOasisAction;
import dti.oasis.util.*;
import dti.oasis.request.RequestStorageManager;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility class used by all Oasis custom tags
 * <p/>
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   May 15, 2003
 *
 * @author jbe
 */
/*
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 1/5/2004		jbe				If label is "&nbsp;" don't append :
 * 1/6/2004     jbe             Handle column spanning
 * 2/5/2004     jbe             Add getLabelStyle
 * 2/8/2004     jbe             Add Logging
 * 2/20/2004    jbe             Refactor various methods out to other Util classes,
 *                              deprecating the ones in this class
 * 3/31/2004    jbe             Give ID to <span for readonly fields
 * 7/16/2004    jbe             Filter out sensitive characters
 * 8/2/2004     jbe             Handle datasrc & datafld for readonly fields
 * 8/23/2004    jbe             Give ID to <span for label
 * 12/22/2004   jbe             Add checkListOfValues to pick up LOV
 * 4/5/2005     jbe             Revise doStartField to check for protected status
 * 6/27/2005    jbe             Replace "." in xml dates with " "
 * 7/18/2005    jbe             Handle "null's" in hidden fields.
 * 9/2/2005     jbe             Support Struts 1.2 - replace RequestUtils & ResponseUtils with TagUtils.
 * 9/13/2005    jbe             Replace language='javascript' with type='text/javascript'
 * 10/17/2005   jbe             Get rid of empty space in formatDateAsXML
 * 01/23/2007   lmm             Added support for reloading a LOV using ajax;
 * 01/23/2007   wer             Added support for displaying a readonly codelookup as label;
 *                              Changed usage of new Boolean(x) in logging to String.valueOf(x);
 * 01/31/2007   wer             Enhanced to support defining Grid Column Order by the Grid Header
 * 02/06/2007   wer             Added support to set readonly finder fields as a disabled text field.
 * 05/04/2007   GCC             Added code to doStartField and doEndTag to
 *                              deal with empty fields before and after a cell,
 *                              alignment, and colspan.
 * 06/26/2007   GCC             Added new static methods getAlignVal and
 *                              getHtmlForEmptyFlds.
 * 07/19/2007   GCC             Corrected logic to deal with empty cells before
 *                              field.
 * 09/27/2007   sxm             Added prefix STYLE=\" and suffix \" to field style
 * 09/28/2007   sxm             Replaced field label with field tooltip as value for "title" attribute
 * 01/02/2007   James           Web Field dependency.
 * 01/10/2007   James           Remove the change for Web Field dependency.
 * 01/10/2007   wer             Moved hidden readonly input field to within a TD tag
 * 03/03/2008   James           Issue#79614 eClaims architectural enhancement to 
 *                              take advantage of ePolicy architecture
 *                              Handling HREF is a new enhancement in WebWB
 * 06/13/2008   kshen           If RequestStorageManager has property "display.readonly.code.lookup.as.label",
 *                              use the value of it as the return value of method displayReadonlyCodeLookupAsLabel.
 * 04/13/2009   mxg             Added method formatCustomDateAsXml() and updated doStartTag
 *                              used for Date Format Internationalization
 * 09/23/2009   Fred            Issue 96884. Extend Internationalization to Date / Time fields
 * 10/08/2009   fcb             Issue# 96764: added logic for masked fields.
 * 12/18/2009   Kenney          Enh to handle readonly phone number fields
 * 04/01/2010   kshen           Changed to support email text field.
 * 09/20/2011   mxg             Issue #100716: Added Display Type FORMATTEDNUMBER
 * 12/02/2011   jxgu            Issue #127094 The 'Y' indicator next to Notes field should be invisible.
 * 11/21/2012   Parker          137533 - Fix panel hidden logic to ensure the required validation when panel hidden
 * 08/26/2013   jxgu            Issue 147722 encode the value for MSVAL
 * 12/23/2015   jyang2          168386 - Modified doStartField and doEndTag to not call getHtmlForEmptyCells when
 *                              isHeaderField is true.
 * 09/11/2017   kshen           Grid replacement. Changed to check the useJqxGridB property in pageBean to determine
 *                              if the current page uses jqxGrid.
 * 11/02/2017   cesar           #186513 - Modified doStartField() TexArea tag was not properly formatted properly if field is masked.
 * 01/12/2018   ylu             Issue 190718: setup MASK_fieldid javascript variable,
 * 02/12/2018   ceasr           #191373 - Fix displaying read only lov fields. Made sure a span id gets created.
 * ----------------------------------------------------------------------------
 */

public class OasisTagHelper {

    /**
     * The name of the property to set to Display all readonly code lookups as a label,
     * This property defaults to false.
     */
    public static final String PROPERTY_DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL = "display.readonly.code.lookup.as.label";
    public static final Boolean DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL_DEFAULT = Boolean.FALSE;
    public static String STYLE_FIELD_EDIT = "oasis_formfield_edit";

    public static String FIELD_LABEL_CONTAINER_SUFFIX = "_LABEL_CONTAINER";
    public static String FIELD_VALUE_CONTAINER_SUFFIX = "_VALUE_CONTAINER";

    public static String ATTRIBUTE_IS_EDITABLE_WHEN_VISIBLE = "isEditableWhenVisible";
    public static String ATTRIBUTE_IS_EDITABLE= "isEditable";
    public static String ATTRIBUTE_IS_EDITABLE_ON_INITIAL_PAGE_LOAD = "isEditableOnInitialPageLoad";

    public static String FIELD_LABEL_HTML_CLASS_SUFFIX = "Label";
    public static String FIELD_VALUE_HTML_CLASS_SUFFIX = "Field";
    public static String FIELD_CLASS_HIDDEN = "hidden";
    public static String FIELD_CLASS_EDITABLE = "editable";
    public static String FIELD_CLASS_READONLY = "readonly";
    public static String COLLAPSE_PANEL_CLASS = "collapsePanel";

    public static String HEADER_FIELD_FIRSTCOL = "headerDataLabelCellSecondCol";
    public static String HEADER_FIELD_SECONDCOL = "headerDataLabelCellSecondCol";
    public static String HEADER_FIELD_THIRDCOL = "headerDataLabelCellThirdCol";

    public static String HEADER_FIELD_LABEL_NO_ALIGN = "HEADER_FIELD_LABEL_NO_ALIGN";
    public static String HEADER_FIELD_LABEL_TD_CLASS = "HEADER_FIELD_LABEL_TD_CLASS";
    public static String HEADER_FIELD_LABEL_SPAN_CLASS = "HEADER_FIELD_LABEL_SPAN_CLASS";

    public static String JS_VAR_READONLY_FIELD_PREFIX = "READONLY_FIELD_";

    private static String STYLE_LABEL = "oasis_formlabel";
    private static String STYLE_LABEL_REQUIRED = "oasis_formlabelreq";
    public static String STYLE_FORM_FIELD_REQUIRED = "oasis_formfieldreq";
    private static String STYLE_FIELD = "oasis_formfield";

    protected static final String clsName = OasisTagHelper.class.getName();
    /**
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#DD_SECS}
     */
    public static final short DD_SECS = DateUtils.DD_SECS;
    /**
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#DD_DAYS}
     */
    public static final short DD_DAYS = DateUtils.DD_DAYS;

    /**
     * Don't allow instantiation
     */
    protected OasisTagHelper() {
    }

    /**
     * @param listOfValues
     * @param value
     * @return String
     * @deprecated replaced by {@link dti.oasis.util.CollectionUtils#getDecodedValue(java.util.ArrayList,java.lang.String)}
     */
    public static String getDecodedValue(ArrayList listOfValues, String value) {
        return CollectionUtils.getDecodedValue(listOfValues, value);

    }

    /**
     * @param listOfValues
     * @param values
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.CollectionUtils#getDecodedValues(java.util.ArrayList,java.lang.String[])}
     */
    public static String getDecodedValues(ArrayList listOfValues, String[] values) {
        return CollectionUtils.getDecodedValues(listOfValues, values);
    }

    /**
     * Preprocess the tag.
     *
     * @param field
     * @param pageContext
     * @param tag
     * @return true if the tag should continue processing, false if not
     * @throws JspException
     */
    public static boolean doStartTag(OasisFormField field, PageContext pageContext,
                                     IOasisTag tag) throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "doStartTag",
            new Object[]{field.dumpFields(), pageContext, tag});
        boolean processField = doStartField(field, pageContext, tag);
        /////////////RETURNS true if Visible and Not READ ONLY
        if (processField) {

            // set the field style unless the jsp
            // has passed us a specific style
            if (tag.getStyleClass() == null)
                tag.setStyleClass(field.getStyle());

            // Default style class for required fields.
            // It should not override current style class from WebWB or passed by JSP.
            // So do it only style class is null.
            if (tag.getStyleClass() == null && field.getIsRequired()){
                tag.setStyleClass(STYLE_FORM_FIELD_REQUIRED);
            }

            // set style on field
            if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
                if (tag.getStyle() == null) {
                    tag.setStyle(field.getStyleInlineForCell());
                } else {
                    tag.setStyle(tag.getStyle() + ";" + field.getStyleInlineForCell());
                }
            }
            if (!StringUtils.isBlank(field.getTooltip()))
                ((BaseHandlerTag) tag).setTitle(field.getTooltip());
        }
        l.exiting(clsName, "doStartTag", String.valueOf(processField));
        return processField;
    }

    /**
     * Preprocess the tag given the OasisFormField
     *
     * @param field       OasisFormField
     * @param pageContext page context
     * @param tag         tag object (implements IOasisTag)
     * @return true if the tag should continue processing, false if not.
     * @throws JspException exception
     */
    public static boolean doStartField(OasisFormField field, PageContext pageContext,
                                       IOasisTag tag) throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "doStartField",
            new Object[]{field, pageContext, tag});

        boolean displayFieldAsVisible = field.getIsVisible();
        boolean displayFieldAsReadOnly = field.getIsReadOnly();
        boolean useJqxGrid = isUseJqxGrid(pageContext);

        // display all information. add class on TD to control visible/hidden or readonly/editable
        // Bottom-line, the className value will be either one of these values
        // - FIELD_CLASS_HIDDEN/FIELD_CLASS_READONLY/FIELD_CLASS_EDITABLE
        String isEditableCustomAttrName = (displayFieldAsVisible ? ATTRIBUTE_IS_EDITABLE : ATTRIBUTE_IS_EDITABLE_WHEN_VISIBLE);
        String classNameTD = displayFieldAsVisible ? "" : FIELD_CLASS_HIDDEN;
        if (displayFieldAsVisible) {
            classNameTD += displayFieldAsReadOnly ? FIELD_CLASS_READONLY : FIELD_CLASS_EDITABLE;
        }

        // If the field is protected, then simply stop processing it.  It should not
        // appear on the page.
        if (field.getIsProtected()) {
            l.exiting(clsName, "doStartField", "false");
            return false;
        }
        StringBuffer buff = new StringBuffer();

        // Check if we should add logic to display readonly Code Lookup fields as a label.
        boolean displayReadonlyCodeLookupAsLabel =
            displayReadonlyCodeLookupAsLabel() &&
                ("SELECT".equals(field.getDisplayType()) || "MULTISELECT".equals(field.getDisplayType()) ||
                    "MULTISELECTPOPUP".equals(field.getDisplayType()));

        // Include javascript variable REQ_fieldid to indicate
        // whether the field is required. It is only
        // really required if it is visible and editable
        buff.append("<script type='text/javascript'> var REQ_").append(field.getFieldId());

        if (field.getIsRequired() && displayFieldAsVisible && !displayFieldAsReadOnly)
            buff.append("=true;</script>\n");
        else
            buff.append("=false;</script>\n");

        // Include javascript variable MASK_fieldid to indicate
        // whether the field is masked. It is only when it is visible and readonly
        buff.append("<script type='text/javascript'> var MASK_").append(field.getFieldId());

        if (field.getIsMasked() && displayFieldAsVisible && displayFieldAsReadOnly)
            buff.append("=true;</script>\n");
        else
            buff.append("=false;</script>\n");

        if(OasisFields.TYPE_NUMBER.equals(field.getDatatype()) && OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER.equals(
                field.getDisplayType())){
            String pattern = field.getFormatPattern();
            if(pattern==null)
                pattern = "";
            buff.append("<script type='text/javascript'>\n");
            buff.append("jQuery(function($) {\n");
            buff.append("\tsetNumberColorInFields(").append("$('input[name=\"").append(field.getFieldId() + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\"]'), ");
            buff.append("'").append(pattern).append("');\n");
            buff.append("});\n");
            buff.append("</script>\n");
        }

        // Adding a clone for non-US Date Format
        // TODO: We may want to add a check for TEXT.
        if(!FormatUtils.isDateFormatUS() ) {
            l.logp(Level.FINE, OasisTagHelper.class.getName(), "doStartField", "Non-US Date Format");
            if (OasisFields.TYPE_DATE.equals(field.getDatatype()) || OasisFields.TYPE_TIME.equals(field.getDatatype())) {
                //isDate = true;
                buff.append("<script type='text/javascript'> var REQ_").append(field.getFieldId()).append(FormatUtils.DISPLAY_FIELD_EXTENTION);

                if (field.getIsRequired() && displayFieldAsVisible && !displayFieldAsReadOnly)
                    buff.append("=true;</script>\n");
                else
                    buff.append("=false;</script>\n");
            }
        }
        // Adding a clone for phone number format
        if (OasisFields.TYPE_PHONE.equals(field.getDatatype())) {
            buff.append("<script type='text/javascript'> var REQ_").append(field.getFieldId()).append(FormatUtils.DISPLAY_FIELD_EXTENTION);
            if (field.getIsRequired() && displayFieldAsVisible && !displayFieldAsReadOnly)
                buff.append("=true;</script>\n");
            else
                buff.append("=false;</script>\n");
        }

        // Adding a clone for formatted number format
        if (OasisFields.TYPE_NUMBER.equals(field.getDatatype()) && OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER.equals(field.getDisplayType())) {
            buff.append("<script type='text/javascript'> var REQ_").append(field.getFieldId()).append(FormatUtils.DISPLAY_FIELD_EXTENTION);
            if (field.getIsRequired() && displayFieldAsVisible && !displayFieldAsReadOnly)
                buff.append("=true;</script>\n");
            else
                buff.append("=false;</script>\n");
        }
         // Adding autoFinder here
        if ("FINDERTEXT".equalsIgnoreCase(field.getDisplayType()) ||"AUTOMATICFINDER".equalsIgnoreCase(field.getDisplayType())) {
            buff.append("<script type='text/javascript'> var autoFind_").append(field.getFieldId());
            if (!field.getIsProtected() && displayFieldAsVisible && !displayFieldAsReadOnly) {
                buff.append("=true;</script>\n"); }
            else
                buff.append("=false;</script>\n");
        }
        // add a javascript variable for note text and textarea popup
        if (OasisFields.DISPLAY_TYPE_NOTE_TEXT.equals(field.getDisplayType()) ||
            OasisFields.DISPLAY_TYPE_TEXTAREA_POPUP.equals(field.getDisplayType())) {
            buff.append("<script type='text/javascript'> var ").append(JS_VAR_READONLY_FIELD_PREFIX).append(field.getFieldId());
            if (field.getIsReadOnly()) {
                buff.append("=true;</script>\n");
            } else
                buff.append("=false;</script>\n");
        }

        //////////////////////////////////////////////////////////////
        //l.logp(Level.INFO, "", "doStartField", "Id : " + field.getFieldId() + " - Ajax URL: " + field.getAjaxURL());
        //if the lov is based on AJAX call, stick a hidden field for the call.
        if (!StringUtils.isBlank(field.getAjaxURL())) {
            if (field.getAjaxURL().toUpperCase().indexOf("AJAX") > 0) {
                String val = field.getAjaxURL();
                if (val == null) val = "";
                buff.append("<script type='text/javascript'> var ajaxInfoFor").append(field.getFieldId()).
                    append(" = '").append(val).append("';</script>\n");
            }
        }

        /* Display a label cell if:
           1. there is a label
           2. the tag supports labels
           3. the field is visible

          Only display the label itself
           1. the field is visible
        */
        if (tag.getShowLabel() && field.getLabel() != null) {
            if (tag.getIsInTable()) {
                if (!tag.getIsHeaderField()) {
                    // Deal with empty cells before field.
                    String szEmptyCellsBeforeField = getHtmlForEmptyCells(field, false);
                    if (!StringUtils.isBlank(szEmptyCellsBeforeField)) {
                        buff.append(szEmptyCellsBeforeField);
                    }
                }
                // label cell
                buff.append("<td ");
                if (!"false".equals(pageContext.getAttribute(HEADER_FIELD_LABEL_NO_ALIGN))) {
                    buff.append(" align='right'");
                }
            } else {
                // add a span with class for field not in table
                buff.append("<span ");
            }
            buff.append(" id=\"").append(field.getFieldId()).append(FIELD_LABEL_CONTAINER_SUFFIX).append("\"");
            buff.append(" class=\"").append(classNameTD);
            if (pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS) != null && tag.getIsInTable()) {
                buff.append(pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS).toString().toUpperCase().charAt(0));
                buff.append(pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS).toString().substring(1));
            }
            buff.append(FIELD_LABEL_HTML_CLASS_SUFFIX);
            buff.append("\" ").append(ATTRIBUTE_IS_EDITABLE_ON_INITIAL_PAGE_LOAD).append("=\"");
            buff.append(classNameTD.equalsIgnoreCase(FIELD_CLASS_EDITABLE) ? "Y" : "N");
            buff.append("\" ").append(isEditableCustomAttrName).append("=\"");
            buff.append(classNameTD.equalsIgnoreCase(FIELD_CLASS_EDITABLE) ? "Y" : "N").append("\">");

            // label itself
            String lb = field.getLabel();
            char lastC = lb.charAt(lb.length() - 1);
            buff.append("<span id='").append(field.getFieldId()).append("FLDLABEL' class='");
            if (pageContext.getAttribute(HEADER_FIELD_LABEL_SPAN_CLASS) != null) {
                buff.append(pageContext.getAttribute(HEADER_FIELD_LABEL_SPAN_CLASS));
            } else {
                buff.append(getLabelStyle(field));
            }
            buff.append("'>").append(lb).
                append((lastC == ':' || lastC == '?' || lastC == '.' || lastC == ';' || lastC == '>') ? "</span>" : ":</span>");

            if (tag.getIsInTable())
                buff.append("</td>\n");
            else
                buff.append("&nbsp;</span>");
        }
//////////////////////////////////////////////////////////////////////////******************************
        // otherwise open up a new cell
        if (tag.getIsInTable()) {
            String alignVal = getAlignVal(field);
            // Handle col spanning
            if (StringUtils.isBlank(tag.getFieldColSpan())) {
                buff.append("<td align='").append(alignVal).append("' ");
                if (FormatUtils.isInt(field.getFieldColSpan())) {
                    if (Integer.parseInt(field.getFieldColSpan()) >= 1) {
                        buff.append("colspan='").
                            append(field.getFieldColSpan()).append("' ");
                    }
                }
            } else {
                buff.append("<td align='").append(alignVal).append("' colspan='").
                    append(tag.getFieldColSpan()).append("' ");
            }
            // add style
            prepareAttribute(buff, "style", field.getStyleInlineForCell());

        } else {
            // add a span for field not in table
            buff.append("<span ");
        }
        buff.append(" id=\"").append(field.getFieldId()).append(FIELD_VALUE_CONTAINER_SUFFIX).append("\"");
        buff.append(" class=\"").append(classNameTD);
        if (pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS) != null && tag.getIsInTable()) {
            buff.append(pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS).toString().toUpperCase().charAt(0));
            buff.append(pageContext.getAttribute(HEADER_FIELD_LABEL_TD_CLASS).toString().substring(1));
        }
        buff.append(FIELD_VALUE_HTML_CLASS_SUFFIX);
        buff.append("\" ").append(ATTRIBUTE_IS_EDITABLE_ON_INITIAL_PAGE_LOAD).append("=\"");
        buff.append(classNameTD.equalsIgnoreCase(FIELD_CLASS_EDITABLE) ? "Y" : "N");
        buff.append("\" ").append(isEditableCustomAttrName).append("=\"");
        buff.append(classNameTD.equalsIgnoreCase(FIELD_CLASS_EDITABLE) ? "Y" : "N").append("\">");

        /////////////////// display the readonly text here
        String datafld = tag.getDatafld();
        if (field.getDatatype() != null &&
            (field.getDatatype().equals(OasisFields.TYPE_DATE) || field.getDatatype().equals(OasisFields.TYPE_TIME))&&
            datafld != null &&
            !datafld.equals("") &&
            !FormatUtils.isDateFormatUS() &&
            !datafld.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION)) {
                datafld = datafld+FormatUtils.DISPLAY_FIELD_EXTENTION;
        }
        if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_PHONE) &&
            datafld != null && !datafld.equals("") && !datafld.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION)) {
            datafld = datafld + FormatUtils.DISPLAY_FIELD_EXTENTION;
        }

        if (field.getDatatype() != null && field.getDatatype().equals(OasisFields.TYPE_NUMBER) &&
            datafld != null && !datafld.equals("") && !datafld.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION)) {
            if(field.getDisplayType() != null && OasisFields.DISPLAY_TYPE_FORMATTEDNUMBER.equals(field.getDisplayType()))
                datafld = datafld + FormatUtils.DISPLAY_FIELD_EXTENTION;
        }
        // check if we've got a url to display
        String url = field.getHref();
        if (StringUtils.isBlank(url)) {
            url = (String) TagUtils.getInstance().lookup(pageContext, field.getFieldId() + "ROURL", null);
        }
        // Email text will not display the urls on the text.
        if (StringUtils.isBlank(url) || OasisFields.DISPLAY_TYPE_EMAIL_TEXT.equals(field.getDisplayType())) {
            if (OasisFields.DISPLAY_TYPE_TEXTAREA.equals(field.getDisplayType()) ||
                OasisFields.DISPLAY_TYPE_TEXTAREA_POPUP.equals(field.getDisplayType())) {
                if (field.getIsMasked()) {
                    buff.append("<span id='");
                } else {
                    buff.append("<span><textarea id='");
                }
            } else {
                buff.append("<span id='");
            }
            buff.append(field.getFieldId()).append("ROSPAN' ");
            if (field.getIsMasked()) {
                buff.append(">").append(FormatUtils.getFieldMask()).append("</span>");
            } else {
                if (!StringUtils.isBlank(tag.getDatasrc())) {
                    if (useJqxGrid) {
                        buff.append("data-dti-datasrc=\"").append(tag.getDatasrc()).append("\" ");
                    } else {
                        buff.append("datasrc=\"").append(tag.getDatasrc()).append("\" ");
                    }
                }

                if (!StringUtils.isBlank(tag.getDatafld())) {
                    if (useJqxGrid) {
                        buff.append("data-dti-datafld=\"").append(datafld).append("\" ");
                    } else {
                        buff.append("datafld=\"").append(datafld).append("\" ");
                    }
                }

                boolean visible = true;
                if (displayReadonlyCodeLookupAsLabel || OasisFields.DISPLAY_TYPE_NOTE_TEXT.equals(field.getDisplayType())) {
                    visible = false;
                }

                if (OasisFields.DISPLAY_TYPE_TEXTAREA.equals(field.getDisplayType()) ||
                    OasisFields.DISPLAY_TYPE_TEXTAREA_POPUP.equals(field.getDisplayType())) {
                    if(useJqxGrid){
                        String cssClass = "disabledTextArea" + (visible ? "" : " dti-hide");
                        prepareAttribute(buff, "class", cssClass);
                    } else {
                        prepareAttribute(buff, "class", "disabledTextArea");
                    }
                    if (!StringUtils.isBlank(field.getCols())) {
                        prepareAttribute(buff, "cols", field.getCols());
                    }
                    if (!StringUtils.isBlank(field.getCols())) {
                        prepareAttribute(buff, "rows", field.getRows());
                    }
                } else {
                    String spanClassName = "";
                    if (tag.getStyleClass() != null)  {
                        spanClassName = tag.getStyleClass();
                    } else if (field.getStyle() != null && !isStyleClassSkippedForReadOnly(field.getStyle())){
                        spanClassName = field.getStyle();
                    }
                    if(useJqxGrid){
                        if (!visible) {
                            spanClassName += " dti-hide";
                        }
                    }

                    prepareAttribute(buff, "class", spanClassName);
                }

                // If we are displaying the readonly codelookup as a label, make this ROSPAN hidden
                // do not display it for note text
                if (displayReadonlyCodeLookupAsLabel || OasisFields.DISPLAY_TYPE_NOTE_TEXT.equals(field.getDisplayType())) {
                    if(!useJqxGrid)
                        prepareAttribute(buff, "style", "display: none");
                } else if (!StringUtils.isBlank(field.getStyleInlineForCell())) {
                    prepareAttribute(buff, "style", field.getStyleInlineForCell());
                }

                if (OasisFields.DISPLAY_TYPE_TEXTAREA.equals(field.getDisplayType()) ||
                    OasisFields.DISPLAY_TYPE_TEXTAREA_POPUP.equals(field.getDisplayType())) {
                    buff.append(" readonly >").append(ResponseUtils.filter(tag.getDecodedValue())).append("</textarea></span>");
                }
                else if (OasisFields.DISPLAY_TYPE_CHECKBOX.equals(field.getDisplayType()) &&
                         field.getIsReadOnly() && StringUtils.isBlank(pageContext.findAttribute(field.getFieldId()).toString())) {
                    buff.append("></span>");
                }
                else {
                    buff.append(">").append(ResponseUtils.filter(tag.getDecodedValue())).append("</span>");
                }
            }
        } else {
            buff.append("<a id='").append(field.getFieldId()).append("ROSPAN' ");
            if(useJqxGrid){
                boolean hidden = (OasisFields.DISPLAY_TYPE_NOTE_TEXT.equals(field.getDisplayType()));

                if (tag.getStyleClass() != null)
                    buff.append("class='").append(tag.getStyleClass()).append(hidden ? " dti-hidden" : "").append("'");
                else if (field.getStyle() != null)
                    buff.append("class='").append(field.getStyle()).append(hidden ? " dti-hidden" : "").append("'");
            } else {
                if (tag.getStyleClass() != null)
                    buff.append("class='").append(tag.getStyleClass()).append("'");
                else if (field.getStyle() != null)
                    buff.append("class='").append(field.getStyle()).append("'");

                if (OasisFields.DISPLAY_TYPE_NOTE_TEXT.equals(field.getDisplayType())) {
                    prepareAttribute(buff, "style", "display: none");
                }
            }

            if (!StringUtils.isBlank(field.getHref())){
                buff.append(" href=\"javascript:handleOnFieldHref('")
                        .append(ResponseUtils.filter(field.getHref().replaceAll("'", "\\\\'")))
                        .append("')\" >");
            } else{
                buff.append(" href=\"").append(url).append("\" >");
            }
            buff.append("<span id='").append(field.getFieldId()).append("ROSPAN1' ");

            if (!StringUtils.isBlank(tag.getDatasrc())) {
                if (useJqxGrid) {
                    buff.append("data-dti-datasrc=\"").append(tag.getDatasrc()).append("\" ");
                } else {
                    buff.append("datasrc=\"").append(tag.getDatasrc()).append("\" ");
                }
            }

            if (!StringUtils.isBlank(tag.getDatafld())) {
                if (useJqxGrid) {
                    buff.append("data-dti-datafld=\"").append(datafld).append("\" ");
                } else {
                    buff.append("datafld=\"").append(datafld).append("\" ");
                }
            }

            // add style
            prepareAttribute(buff, "style", field.getStyleInlineForCell());

            buff.append(">").append(ResponseUtils.filter(tag.getDecodedValue())).append("</span></a>");
        }

        ///////////////LOV AS READ ONLY
        // If we are displaying the readonly codelookup as a label,
        // and the label as a visible span with id="fieldId" + LOVLABELSPAN
        // The code will be written by the OasisSelect as a hidden select field.
        // However, it will not display the label if "href" attribute is defined for this field.
        // In this case, it displays link field instead of a visible span.
        if (displayReadonlyCodeLookupAsLabel && StringUtils.isBlank(field.getHref())) {
            // Add the label span
            buff.append("\n<span id='").append(field.getFieldId()).append("LOVLABELSPAN' ");

            if (field.getIsMasked()) {
                // add style
                prepareAttribute(buff, "style", "display:none");
                buff.append("></span>");
            } else {
                if (!StringUtils.isBlank(tag.getDatasrc())) {
                    if (useJqxGrid) {
                        buff.append("data-dti-datasrc=\"").append(tag.getDatasrc()).append("\" ");
                    }
                    else {
                        buff.append("datasrc=\"").append(tag.getDatasrc()).append("\" ");
                    }
                }

                if (!StringUtils.isBlank(tag.getDatafld())) {
                    if (useJqxGrid) {
                        buff.append("data-dti-datafld=\"").append(tag.getDatafld()).append("LOVLABEL\" ");
                    }
                    else {
                        buff.append("datafld=\"").append(tag.getDatafld()).append("LOVLABEL\" ");
                    }
                }

                String spanClassName = "";
                if (tag.getStyleClass() != null) {
                    spanClassName = tag.getStyleClass();
                }
                else if (field.getStyle() != null) {
                    spanClassName = field.getStyle();
                }
                buff.append(" class=\"").append(spanClassName).append("\" ");
                // add style
                prepareAttribute(buff, "style", field.getStyleInlineForCell());

                buff.append(">").append(ResponseUtils.filter(tag.getDecodedValue())).append("</span>");
            }
        }

        // Add hidden field for comma separated list of selected values
        if ("MULTISELECT".equals(field.getDisplayType())||"MULTISELECTPOPUP".equals(field.getDisplayType())) {
            buff.append("<input type='hidden' name='").append(field.getFieldId()).append("MSVAL").append("' ");

            if (!StringUtils.isBlank(tag.getDatasrc())) {
                if (useJqxGrid) {
                    buff.append("data-dti-datasrc='").append(tag.getDatasrc()).append("' ");
                } else {
                    buff.append("datasrc='").append(tag.getDatasrc()).append("' ");
                }
            }

            if (!StringUtils.isBlank(tag.getDatafld())) {
                if (useJqxGrid) {
                    buff.append("data-dti-datafld='").append(tag.getDatafld()).append("' ");
                } else {
                    buff.append("datafld='").append(tag.getDatafld()).append("' ");
                }
            }

            buff.append("value='");
            Object bean = pageContext.findAttribute(field.getFieldId());
            if (bean instanceof BasicDynaBean) {
                Object prop = ((BasicDynaBean) bean).get(field.getFieldId());
                if (prop instanceof String[]) {
                    String[] vals = (String[]) prop;
                    String sep = "";
                    for (int i = 0; i < vals.length; i++) {
                        String val = vals[i];
                        buff.append(sep).append(ResponseUtils.filter(val));
                        sep = ",";
                    }
                }
            }
            buff.append("' ");
            buff.append("/>\n");
        }

        // write it out
        TagUtils.getInstance().write(pageContext, buff.toString());

        // always return true, process tags.
        l.exiting(clsName, "doStartField", String.valueOf(true));
        return true;
    }

    /**
     * @param tag         tag object (implements IOasisTag)
     * @param pageContext page context
     * @throws JspException exception
     */
    static void doEndTag(IOasisTag tag, PageContext pageContext)
        throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "doEndTag",
            new Object[]{tag, pageContext});
        OasisFormField fld = (OasisFormField) tag.getOasisFormField();

        if (tag.getIsInTable()) {
            TagUtils.getInstance().write(pageContext, "</td>\n");
            if (!tag.getIsHeaderField()) {
                TagUtils.getInstance().write(pageContext, getHtmlForEmptyCells(fld));
            }
        }else{
            TagUtils.getInstance().write(pageContext, "</span>\n");
        }

        l.exiting(clsName, "doEndTag");
    }

    /**
     * Prepares an attribute if the value is not null, appending it to the the given StringBuffer.
     * @param handlers The StringBuffer that output will be appended to.
     * @param name
     * @param value
     */
    public static void prepareAttribute(StringBuffer handlers, String name, String value) {
        if (!StringUtils.isBlank(value)) {
            handlers.append(" ");
            handlers.append(name);
            handlers.append("=\"");
            handlers.append(value);
            handlers.append("\"");
        }
    }

    /**
     * Dynamically creates a bean with a single property
     * The property will be have the name of the OasisFormField.fieldId
     * The value will be the OasisFormField.value.
     * We use Apache BeanDtiUtils for this
     *
     * @param field The OasisFormField we are basing this on
     * @param value The value of the field
     * @return a DynaBean whose single attribute name will match the field.fieldId
     *         and whose value will match the value parameter.
     * @throws JspException
     * @deprecated Replaced by {@link dti.oasis.util.BeanDtiUtils#createValueBean(dti.oasis.tags.OasisFormField,java.lang.Object)}
     */
    public static DynaBean createValueBean(OasisFormField field, Object value)
        throws JspException {
        return BeanDtiUtils.createValueBean(field, value);

    }

    /**
     * @param value
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatCurrency(java.lang.String)}
     */
    public static String formatCurrency(String value) {
        return FormatUtils.formatCurrency(value);
    }

    /**
     * @param number
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatCurrency(double)}
     */
    public static String formatCurrency(double number) {
        return FormatUtils.formatCurrency(number);
    }

    /**
     * @param number
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatCurrency(long)}
     */
    public static String formatCurrency(long number) {
        return FormatUtils.formatCurrency(number);
    }

    /**
     * @param value
     * @return String
     */
    public static String formatDateAsXml(String value) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateAsXml", value);
        String rc = ".";
        if (!StringUtils.isBlank(value)) {
            rc = FormatUtils.formatDate(value);
            if (StringUtils.isBlank(rc))
                rc = ".";
        }
        l.exiting(clsName, "formatDateAsXml", rc);
        return rc;
    }

    /**
     * @param value
     * @return String
     */
    public static String formatDateTimeAsXml(String value) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateTimeAsXml", value);
        String rc = ".";
        if (!StringUtils.isBlank(value)) {
            rc = FormatUtils.formatDateTime(value);
            if (StringUtils.isBlank(rc))
                rc = ".";
        }
        l.exiting(clsName, "formatDateTimeAsXml", rc);
        return rc;
    }

    /**
     * @param date
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatDate(java.util.Date)}
     */
    public static String formatDate(Date date) {
        return FormatUtils.formatDate(date);
    }

    /**
     * @param date
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatDateTime(java.util.Date)}
     */
    public static String formatDateTime(Date date) {
        return FormatUtils.formatDateTime(date);
    }

    /**
     * @param date
     * @return String
     */
    public static String formatDateAsXml(Date date) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateAsXml", date);
        String rc = (date == null) ? "" : FormatUtils.formatDate(date);
        l.exiting(clsName, "formatDateAsXml", rc);
        return rc;
    }

    /**
     * @param date
     * @return String
     */
    public static String formatCustomDateAsXml(Date date) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateAsXml", date);
        String rc = (date == null) ? "" : FormatUtils.formatDateForDisplay(date);
        l.exiting(clsName, "formatDateAsXml", rc);
        return rc;
    }

    /**
     * @param date
     * @return String
     */
    public static String formatDateTimeAsXml(Date date) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateTimeAsXml", date);
        String rc = (date == null) ? "" : FormatUtils.formatDateTime(date);
        l.exiting(clsName, "formatDateTimeAsXml", rc);
        return rc;

    }

    /**
     *
     * @param date
     * @return
     */
     public static String formatCustomDateTimeAsXml(Date date) {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "formatDateAsXml", date);
        String rc = (date == null) ? "" : FormatUtils.formatDateTimeForDisplay(date);
        l.exiting(clsName, "formatDateAsXml", rc);
        return rc;
    }

    /**
     * @param value
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatDate(java.lang.String)}
     */
    public static String formatDate(String value) {
        return FormatUtils.formatDate(value);
    }

    /**
     * @param value
     * @return String
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#formatDateTime(java.lang.String)}
     */
    public static String formatDateTime(String value) {
        return FormatUtils.formatDateTime(value);

    }

    /**
     * @param c
     * @return int
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#toJulian(java.util.GregorianCalendar)}
     */
    public static int toJulian(GregorianCalendar c) {
        return DateUtils.toJulian(c);
    }

    /**
     * @param date
     * @return Date
     * @throws ParseException
     * @deprecated Replaced by {@link dti.oasis.util.FormatUtils#getDate(java.lang.String)}
     */
    public static Date getDate(String date) throws ParseException {
        return FormatUtils.getDate(date);
    }

    /**
     * @param dt1
     * @param dt2
     * @return int
     * @throws ParseException
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#daysDiff(java.lang.String,java.lang.String)}
     */
    public static int daysDiff(String dt1, String dt2) throws ParseException {
        return DateUtils.daysDiff(dt1, dt2);
    }

    /**
     * @param type
     * @param dt1
     * @param dt2
     * @return long
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#dateDiff(short,java.util.Date,java.util.Date)}
     */
    public static long dateDiff(short type, Date dt1, Date dt2) {
        return DateUtils.dateDiff(type, dt1, dt2);
    }

    /**
     * @param type
     * @param dt1
     * @param dt2
     * @return long
     * @throws ParseException
     * @deprecated Replaced by {@link dti.oasis.util.DateUtils#dateDiff(short,java.lang.String,java.util.Date)}
     */
    public static long dateDiff(short type, String dt1, Date dt2) throws ParseException {
        return DateUtils.dateDiff(type, dt1, dt2);
    }

    /**
     * @param tag
     * @param pageContext
     * @throws JspException
     */
    public static void setField(IBaseOasisTag tag, PageContext pageContext) throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "setField",
            new Object[]{tag, pageContext});
        OasisFormField fld = null;
        Map map = null;
        // only look for an OasisFormField if one is not already
        // set AND the tag has a valid fieldName
        if (tag.getOasisFormField() == null && tag.getFieldName() != null) {
            // if the tag has a mapName, look for the field in it
            if (tag.getMapName() != null) {
                try {
                    map = (Map) TagUtils.getInstance().lookup(pageContext, tag.getMapName(), null);
                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in scope using the identifier " +
                        tag.getMapName() + " is not a Map.");
                }
                if (map == null) {
                    throw new JspException("No valid object was found in any scope using the identifier " +
                        tag.getMapName() + '.');
                }
                try {
                    fld = (OasisFormField) map.get(tag.getFieldName());
                    if (fld == null) {
                        throw new JspException("No valid object was found in the Map using the identifier " +
                            tag.getFieldName() + '.');
                    }
                    tag.setOasisFormField(fld);
                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in the Map using the identifier " +
                        tag.getFieldName() + " is not an OasisFormField.");
                }

            }
            // the tag has no valid mapName, look for the field
            // directly in scope
            else {
                try {
                    fld = (OasisFormField) TagUtils.getInstance().lookup(pageContext, tag.getFieldName(), null);
                    if (fld == null) {
                        throw new JspException("No valid object was found in any scope using the identifier " +
                            tag.getFieldName() + '.');
                    }
                    tag.setOasisFormField(fld);

                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in scope using the identifier " +
                        tag.getFieldName() + " is not an OasisFormField.");
                }

            }
        }
        l.fine(tag.toString());
        l.exiting(clsName, "setField");
    }

    /**
     * Updates the OasisWebElement in the object implementing the IOasisElementTag interface.
     * If the OasisWebElement in the tag is null and a map name and element name have
     * been provided, then an OasisElements object is looked for in context using the map name.
     * If found, the OasisWebElement is pulled from it using the elementName. If found
     * the tag's element property is set to this element.
     * Finally, a series of setter methods are called on the tag given the values
     * in the OasisWebElement.
     *
     * @param tag         tag object implementing the IOasisElementTag interface
     * @param pageContext
     * @throws JspException
     */
    public static void setElement(IOasisElementTag tag, PageContext pageContext) throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "setElement",
            new Object[]{tag, pageContext});
        OasisWebElement el = (OasisWebElement) tag.getElement();
        Map map = null;

        // only look for an OasisWebElement if one is not already
        // set AND the tag has a valid fieldName
        if (el == null && tag.getElementName() != null) {
            // if the tag has a mapName, look for the field in it
            if (tag.getMapName() != null) {
                try {
                    map = (Map) TagUtils.getInstance().lookup(pageContext, tag.getMapName(), null);
                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in scope using the identifier " +
                        tag.getMapName() + " is not a Map.");
                }
                if (map == null) {
                    throw new JspException("No valid object was found in any scope using the identifier " +
                        tag.getMapName() + '.');
                }
                try {
                    el = (OasisWebElement) map.get(tag.getElementName());
                    if (el == null) {
                        throw new JspException("No valid object was found in the Map using the identifier " +
                            tag.getElementName() + '.');
                    }
                    tag.setElement(el);
                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in the Map using the identifier " +
                        tag.getElementName() + " is not an OasisFormField.");
                }

            }
            // the tag has no valid mapName, look for the field
            // directly in scope
            else if (tag.getElementName() != null) {
                try {
                    el = (OasisWebElement) TagUtils.getInstance().lookup(pageContext, tag.getElementName(), null);
                    if (el == null) {
                        throw new JspException("No valid object was found in any scope using the identifier " +
                            tag.getElementName() + '.');
                    }
                    tag.setElement(el);

                }
                catch (ClassCastException ce) {
                    throw new JspException("The object found in scope using the identifier " +
                        tag.getElementName() + " is not an OasisFormField.");
                }

            }
        }

        // If we have an OasisWebElement, use it
        if (el != null) {
            if (!StringUtils.isBlank(tag.getStyleClass())) {
                tag.setStyleClass(tag.getStyleClass());
            }
            else if (!StringUtils.isBlank(el.getStyle())) {
                tag.setStyleClass(el.getStyle());
            }
            if (!StringUtils.isBlank(el.getDescription()))
                tag.setTitle(el.getDescription());
            if (!StringUtils.isBlank(el.getText()))
                tag.setValue(el.getText());
            if (!StringUtils.isBlank(el.getUrl()))
                tag.setUrl(el.getUrl());
            if (!StringUtils.isBlank(el.getElementId()))
                tag.setProperty(el.getElementId());

        }
        l.fine(tag.toString());
        l.exiting(clsName, "setElement");
    }

    /**
     * @param actionName
     * @param pageContext
     * @return
     * @throws JspException
     * @deprecated Replaced by {@link dti.oasis.struts.ActionHelper#getFormFromAction(java.lang.String,javax.servlet.jsp.PageContext)}
     */
    public static String getFormFromAction(String actionName, PageContext pageContext) throws JspException {
        return ActionHelper.getFormFromAction(actionName, pageContext);
    }

    /**
     * Determines if a String value is empty
     *
     * @param val
     * @return
     * @deprecated Replaced by {@link dti.oasis.util.StringUtils#isBlank(java.lang.String)}
     */
    public static boolean isEmpty(String val) {
        return StringUtils.isBlank(val);
    }

    /**
     * Returns the style for a label given a field
     *
     * @param field
     * @return css
     */
    public static String getLabelStyle(OasisFormField field) {
        return (field.getIsRequired()) ? STYLE_LABEL_REQUIRED : STYLE_LABEL;
    }

    /**
     * Find a list of values ArrayList in some scope given a field.  It will look for
     * an ArrayList under the fieldId+LOV.  It will return null if it does not find it.
     *
     * @param pageContext
     * @param fieldId
     * @return ArrayList of LabelValueBeans or null
     * @throws JspException
     */
    public static ArrayList findListOfValues(PageContext pageContext, String fieldId)
        throws JspException {
        Logger l = LogUtils.enterLog(OasisTagHelper.class, "findListOfValues",
            new Object[]{pageContext, fieldId});
        ArrayList lov = null;
        Object o = TagUtils.getInstance().lookup(pageContext, fieldId + "LOV", null);
        if (o != null && o instanceof ArrayList)
            lov = (ArrayList) o;
        l.exiting(clsName, "findListOfValues", lov);
        return lov;
    }

    public static boolean displayReadonlyCodeLookupAsLabel() {
        // Get "display.readonly.code.lookup.as.label" in  RequestStorageManager first.
        if (RequestStorageManager.getInstance().has(PROPERTY_DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL)) {
            return Boolean.valueOf((String) RequestStorageManager.getInstance().get(PROPERTY_DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL)).booleanValue();
        }

        if (m_displayReadonlyCodeLookupAsLabel == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL)) {
                m_displayReadonlyCodeLookupAsLabel = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL)).booleanValue());
            }
            else {
                m_displayReadonlyCodeLookupAsLabel = DISPLAY_READONLY_CODE_LOOKUP_AS_LABEL_DEFAULT;
            }
        }
        return m_displayReadonlyCodeLookupAsLabel.booleanValue();
    }

    private static Boolean m_displayReadonlyCodeLookupAsLabel;

    /**
     * Returns value to use for alignment of a field.
     *
     * @param field OasisFormField
     * @return String
     */
    public static String getAlignVal(OasisFormField field) {
        String alignVal = "left";
        if (field != null) {
            if (!StringUtils.isBlank(field.getAlignment())) {
                if (field.getAlignment().equals("R")) {
                    alignVal = "right";
                }
                else if (field.getAlignment().equals("C")) {
                    alignVal = "center";
                }
            }
        }
        return alignVal;
    }

    /**
     * Creates appropriate HTML for empty cells after a field.
     *
     * @param field OasisFormField
     * @return String
     */
    public static String getHtmlForEmptyCells(OasisFormField field) {
        return getHtmlForEmptyCells(field, true);
    }

    /**
     * Creates appropriate HTML for empty cells after or before a field.
     *
     * @param field            OasisFormField
     * @param emptyFieldsAfter Handle empty cells after field or before?
     * @return String
     */
    public static String getHtmlForEmptyCells(OasisFormField field, boolean emptyFieldsAfter) {
        StringBuffer sbRetVal = new StringBuffer().append("");
        if (field != null) {
            int emptyCells = 0;
            String szEmptyCells = null;
            if (emptyFieldsAfter) {
                szEmptyCells = field.getEmptyCellsAfterFld();
            }
            else {
                szEmptyCells = field.getEmptyCellsBeforeFld();

            }
            if (FormatUtils.isInt(szEmptyCells)) {
                emptyCells = Integer.parseInt(szEmptyCells);
            }
            if (emptyCells >= 1) {
                for (int i = 1; i <= emptyCells; i++) {
                    sbRetVal.append("<td>&nbsp;</td>\n");
                }
            }
        }
        return sbRetVal.toString();
    }

    /**
     * Check if css style class is skipped when the field is read-only
     *
     * @param strStyleClass - style class name you want to check
     * @return
     */
    private static boolean isStyleClassSkippedForReadOnly(String strStyleClass) {
        boolean isSkipped = false;
        String[] skippedStyleClassArray = new String[]{"noEntryFinder", "noEntryFinderReq"};
        for (int i = 0; i < skippedStyleClassArray.length; i++) {
            if (skippedStyleClassArray[i].equals(strStyleClass)) {
                isSkipped = true;
                break;
            }
        }
        return isSkipped;
    }


    /**
     * Check if the current page uses jqxGrid.
     * @param pageContext
     * @return
     * @throws JspException
     */
    public static boolean isUseJqxGrid(PageContext pageContext) throws JspException {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(OasisTagHelper.class.getName(), "isUseJqxGrid", new Object[]{pageContext});
        }

        boolean useJqxGrid = isUseJqxGrid((HttpServletRequest) pageContext.getRequest());

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(OasisTagHelper.class.getName(), "isUseJqxGrid", useJqxGrid);
        }
        return useJqxGrid;
    }

    public static boolean isUseJqxGrid(HttpServletRequest request) {
        if (c_l.isLoggable(Level.FINER)) {
            c_l.entering(OasisTagHelper.class.getName(), "isUseJqxGrid", new Object[]{request});
        }

        PageBean pageBean = (PageBean) request.getAttribute(IOasisAction.KEY_PAGEBEAN);
        boolean useJqxGrid = "Y".equals(pageBean.getUseJqxGridB());

        if (c_l.isLoggable(Level.FINER)) {
            c_l.exiting(OasisTagHelper.class.getName(), "isUseJqxGrid", useJqxGrid);
        }
        return useJqxGrid;
    }

    private static final Logger c_l = LogUtils.getLogger(OasisTagHelper.class);
}
