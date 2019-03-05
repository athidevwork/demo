package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.util.StringUtils;
import dti.oasis.util.LogUtils;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Aug 13, 2007
 *
 * @author mmanickam
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * 02/27/2008       wer         Fixed check if isNewValue is null to check use StringUtils.isBlank
 * 02/27/2008       mlm         90311 - Added support for multi-select popup.
 * 11/19/2009       Kenney      Add onkeyup
 * 04/20/2011       James       Issue#119774 remove logic to use the defaults if isNewValue = 'Y'
 * 10/12/2017       kshen       Grid replacement: pass event object to baseOnXxx methods for supporting firefox.
 * ---------------------------------------------------
 */
public class OasisTagFactory extends BodyTagSupport {
     public int doStartTag() throws JspException {
         Logger l = LogUtils.enterLog(getClass(), "doStartTag");
         try {
            StringBuffer tagFactory = new StringBuffer();
            if (field == null)
                throw new JspException("Invalid Form Field");

            String type = field.getDisplayType();
            ArrayList lov = null;
            String checkBoxValue = null;
            String maxInRow = null;
            String checkBoxSpan = "1";
            boolean showLabel = true;
            boolean isInTable = true;
            String datasrc = null;
            String datafld = null;
            String beanName = null;
            String numDecimals = null;
            String fieldColSpan = "1";

            /* If we are a SELECT or MULTISELECT, find the list of values ArrayList
               in some scope under the key "fieldIdLOV" */
            if (!(type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FINDERTEXT") || type.equals("TEXTAREA") || type.equals("NOTETEXT"))) {
                lov = null;
                try {
                    if (pageContext.getAttribute(field.getFieldId() + "LOV") != null) {
                        lov = (ArrayList) pageContext.getAttribute(field.getFieldId() + "LOV");
                    } else {
                        throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
                    }
                }
                catch (ClassCastException ce) {
                    throw new JspException("Expecting, but did not find list of values for field [" + field.getFieldId() + "]");
                }
                l.fine(getClass().getName() + ": lov=" + lov);
            }

            try {
                numDecimals = "";
                if (pageContext.getAttribute(field.getFieldId() + "NUMDEC") != null) {
                    numDecimals = (String) pageContext.getAttribute(field.getFieldId() + "NUMDEC");
                }
            }
            catch (ClassCastException e) {
                numDecimals = "";
            }

            try {
                fieldColSpan = "";
                if (pageContext.getAttribute(field.getFieldId() + "COLSPAN") != null) {
                    fieldColSpan = (String) pageContext.getAttribute(field.getFieldId() + "COLSPAN");
                }
            }
            catch (ClassCastException e) {
                fieldColSpan = "";
            }

            String formName = null;
            try {
                if (pageContext.getAttribute("formName") != null) {
                    formName = (String) pageContext.getAttribute("formName");
                }
                if (StringUtils.isBlank(formName))
                    formName = "forms[0]";
            }
            catch (ClassCastException ce) {
                formName = "forms[0]";
            }

            String dataType = field.getDatatype();
            String onFocus = new StringBuffer("baseOnFocus('" + dataType + "', event);").toString();
            String onKeyPress = new StringBuffer("baseOnKeyPress('" + dataType + "', event);").toString();
            String onKeyDown = new StringBuffer("baseOnKeyDown('" + dataType + "', event);").toString();
            String onBlur = new StringBuffer("baseOnBlur('" + dataType + "', event);").toString();
            String onChange = new StringBuffer("baseOnChange('" + dataType + "', event);").toString();
            String onKeyUp = new StringBuffer("baseOnKeyUp('" + dataType + "', event);").toString();

/*    String onChange = new StringBuffer("handleOnChange(document.").append(formName).append('.').
      append(field.getFieldId()).append(')').toString();*/
//    String onChange = "handleOnChange(this)";

            try {
                String b = null;
                if (pageContext.getAttribute(field.getFieldId() + "showLabel") != null) {
                    b = (String) pageContext.getAttribute(field.getFieldId() + "showLabel");
                }
                showLabel = (b != null && b.equalsIgnoreCase("false")) ? false : true;
            }
            catch (ClassCastException ce) {
                showLabel = true;
            }
            l.fine(getClass().getName() + ": showLabel=" + showLabel);

            try {
                String b = null;
                if (pageContext.getAttribute(field.getFieldId() + "isInTable") != null) {
                    b = (String) pageContext.getAttribute(field.getFieldId() + "isInTable");
                }
                isInTable = (b != null && b.equalsIgnoreCase("false")) ? false : true;
            }
            catch (ClassCastException ce) {
                isInTable = true;
            }
            l.fine(getClass().getName() + ": isInTable=" + isInTable);

            try {
                datasrc = null;
                if (pageContext.getAttribute("datasrc") != null) {
                    datasrc = (String) pageContext.getAttribute("datasrc");
                }

                if (!StringUtils.isBlank(datasrc)) {
                    if (datasrc.charAt(0) != '#') datasrc = "#" + datasrc;
                } else
                    datasrc = "";
            }
            catch (ClassCastException ce) {
                datasrc = "";
            }
            l.fine(getClass().getName() + ": datasrc=" + datasrc);

            try {
                datafld = null;
                if (pageContext.getAttribute("datafld") != null) {
                    datafld = (String) pageContext.getAttribute("datafld");
                }
            }
            catch (ClassCastException ce) {
                datafld = "";
            }
            l.fine(getClass().getName() + ": datafld=" + datafld);

            try {
                beanName = null;
                if (pageContext.getAttribute("beanName") != null) {
                    beanName = (String) pageContext.getAttribute("beanName");
                }
            }
            catch (ClassCastException ce) {
                //ignore it
            }
            // no beanName, use fieldId
            if (StringUtils.isBlank(beanName))
                beanName = field.getFieldId();

            if (type.equals("CHECKBOX")) {
                try {
                    checkBoxValue = null;
                    if (pageContext.getAttribute(field.getFieldId() + "CheckBoxValue") != null) {
                        checkBoxValue = (String) pageContext.getAttribute(field.getFieldId() + "CheckBoxValue");
                    }
                    if (checkBoxValue == null) checkBoxValue = "Y";
                }
                catch (ClassCastException ce) {
                    checkBoxValue = "Y";
                }
                l.fine(getClass().getName() + ": checkBoxValue=" + checkBoxValue);

                if (!field.getIsReadOnly() && field.getIsVisible()) {
                    try {
                        checkBoxSpan = null;
                        if (pageContext.getAttribute(field.getFieldId() + "CheckBoxSpan") != null) {
                            checkBoxSpan = (String) pageContext.getAttribute(field.getFieldId() + "CheckBoxSpan");
                        }

                        if (checkBoxSpan == null) {
                            if (pageContext.getAttribute("CheckBoxSpan") != null) {
                                checkBoxSpan = (String) pageContext.getAttribute("CheckBoxSpan");
                            }
                        }
                        if (checkBoxSpan == null)
                            checkBoxSpan = "1";
                    }
                    catch (ClassCastException ce) {
                        checkBoxSpan = "1";
                    }
                    l.fine(getClass().getName() + ": checkBoxSpan=" + checkBoxSpan);
                }

            }

            if (type.equals("MULTIBOX") || type.equals("RADIOBUTTON")) {
                try {
                    maxInRow = null; 
                    if (pageContext.getAttribute(field.getFieldId() + "maxInRow") != null) {
                        maxInRow = (String) pageContext.getAttribute(field.getFieldId() + "maxInRow");
                    }
                    if (maxInRow == null) maxInRow = String.valueOf(lov.size());
                }
                catch (ClassCastException ce) {
                    maxInRow = String.valueOf(lov.size());
                }
                l.fine(getClass().getName() + ": maxInRow=" + maxInRow);
            }

            if (type.equals("TEXT") || type.equals("FORMATTEDTEXT") || type.equals("FINDERTEXT")) {
                tagFactory.append("<oweb:text oasisFormField=" + field + " maxlength=" + field.getMaxLength()).
                           append(" size=" + field.getCols() + " name=" + beanName + " isInTable=" + isInTable).
                           append(" onfocus=" + onFocus + " onchange=" + onChange + " onkeypress=" + onKeyPress + " onkeydown=" + onKeyDown + " onkeyup=" + onKeyUp + " onblur=" + onBlur).
                           append(" showLabel=" + showLabel).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + " fieldColSpan=" + fieldColSpan).
                           append(" datafield=" + field.getDatatype()). 
                           append(" numDecimals=" + numDecimals + "/>");
            } else if (type.equals("TEXTAREA") || type.equals("NOTETEXT")) {
                tagFactory.append("<oweb:textarea oasisFormField=" + field + " cols=" + field.getCols()).
                           append(" size=" + field.getCols() + " name=" + beanName + " isInTable=" + isInTable).
                           append(" onfocus=" + onFocus + " onchange=" + onChange + " onkeypress=" + onKeyPress + " onkeydown=" + onKeyDown + " onkeyup=" + onKeyUp + " onblur=" + onBlur).
                           append(" showLabel=" + showLabel).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + " fieldColSpan=" + fieldColSpan + "/>");
            } else if (type.equals("SELECT")) {
                tagFactory.append("<oweb:select oasisFormField=" + field + " listOfValues=" + lov + " isInTable=" + isInTable).
                           append(" size=" + (field.getRows()==null ? "0" : field.getRows()) + " name=" + beanName).
                           append(" onfocus=" + onFocus + " onchange=" + onChange + " onkeypress=" + onKeyPress + " onkeydown=" + onKeyDown + " onkeyup=" + onKeyUp + " onblur=" + onBlur).
                           append(" showLabel=" + showLabel).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + " fieldColSpan=" + fieldColSpan + "/>");
            } else if (type.equals("MULTISELECT")||type.equals("MULTISELECTPOPUP")) {
                tagFactory.append("<oweb:select oasisFormField=" + field + " listOfValues=" + lov).
                           append(" size=" + (field.getRows()==null ? "0" : field.getRows()) ).
                           append(" multiple='yes' name=" + beanName + " isInTable=" + isInTable).
                           append(" onfocus=" + onFocus + " onchange=" + onChange + " onkeypress=" + onKeyPress + " onkeydown=" + onKeyDown + " onkeyup=" + onKeyUp + " onblur=" + onBlur).
                           append(" showLabel=" + showLabel).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + " fieldColSpan=" + fieldColSpan + "/>");
            } else if (type.equals("CHECKBOX")) {
                tagFactory.append("<oweb:checkbox fieldColSpan=" + checkBoxSpan + " oasisFormField=" + field + " listOfValues=" + lov).
                           append(" name=" + beanName + " onclick=" + onChange + " isInTable=" + isInTable ).
                           append(" value=" + checkBoxValue + " showLabel=" + showLabel ).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + "/>");
            } else if (type.equals("MULTIBOX")) {
                tagFactory.append("<oweb:multibox oasisFormField=" + field + " listOfValues=" + lov + " fieldColSpan=" + fieldColSpan).
                           append(" name=" + beanName + " onclick=" + onChange + " isInTable=" + isInTable).
                           append(" maxInRow=" + maxInRow + " showLabel=" + showLabel + "/>");
            } else if (type.equals("RADIOBUTTON")) {
                tagFactory.append("<oweb:radio oasisFormField=" + field + " listOfValues=" + lov).
                           append(" name=" + beanName + " onclick=" + onChange + " isInTable=" + isInTable).
                           append(" maxInRow=" + maxInRow + " showLabel=" + showLabel).
                           append(" datasrc=" + datasrc + " datafld=" + datafld + " fieldColSpan=" + fieldColSpan + "/>");
            } else throw new JspException(type + " not supported for field [" + field.getFieldId() + "]");

             this.pageContext.getOut().println(tagFactory.toString());
             l.exiting(getClass().getName(), "jsp");

        } catch (IOException e) {
            throw new JspException("Error: IOException while writing to client" + e.getMessage());
        }
        return EVAL_BODY_BUFFERED;
    }


    public int doEndTag() throws JspException {
        Logger l = LogUtils.enterLog(getClass(), "doEndTag");

        field = null;

        l.exiting(getClass().getName(), "doEndTag");
        return EVAL_PAGE;
    }


    public OasisFormField getField() {
        return field;
    }

    public void setField(OasisFormField field) {
        this.field = field;
    }

    private OasisFormField field;
}
