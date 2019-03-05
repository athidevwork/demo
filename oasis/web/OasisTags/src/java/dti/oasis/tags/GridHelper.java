package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.busobjs.YesNoFlag;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.util.FormatUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.LabelValueBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class used by OASISGrid
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * @author jbe
 * @see dti.oasis.tags.OasisGrid
 *      Date:   Jun 20, 2003
 */
/*
* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 1/5/2004		jbe		Hide grid header if appropriate
* 2/7/2004      jbe     Add Logging
* 4/22/2004     jbe     Check for msxml3.0 and display msg if not found
* 5/28/2004     jbe     Add createTextArea and fix some quoting in elements
* 6/25/2004     jbe     Change the element creation methods to use label for
*                       the element title, and add a new datafld parm
* 8/19/2005		jbe		change XPATH query from $ne$ to !=
* 9/2/2005      jbe     Change to support Struts 1.2 - replace ResponseUtils.write
*                       with TagUtils.getInstance().write
* 9/13/2005    jbe      Replace language='javascript' with type='text/javascript'
* 01/17/2006    sxm     Replace 'Select All' and 'Deselect All' buttons with a checkbox
*                       in the grid header that toggles
* 01/25/2006    sxm     Hide "_HDRTBL' if there is no NavButtons
* 02/06/2006    sxm     Removed "-Select-" from the dropdownlist of a SELECT input form field
* 12/06/2006    sxm     Removed extra "</table></div>" tags from writeGrid()
* 12/11/2006    GCC     Changed method writeNavButtons to include save as CSV
*                       and save as Excel buttons.
* 01/12/2007    GCC     Changed text of "Excel (CSV)" button to "Excel".
* 01/16/2007    GCC     Changed code to look up whether or not to create the
*                       Excel (CSV) and Excel (HTML) buttons for every grid
*                       from environment variables.
* 01/23/2007    mlm     Added support for caching the result set, and setting the url to retrieve it
* 01/23/2007    wer     Changed usage of new Boolean(x) in logging to String.valueOf(x);
* 01/31/2007    wer     Enhanced to support defining Grid Column Order by the Grid Header
* 02/09/2007    mlm     Enhancement for multi grid support
* 02/21/2007    GCC     Changed writeNavButtons to create a JS variable called
*                       "<theGridId_excelButtonDisplayed>" and to set it to
*                       true or false based on whether or not the Excel button
*                       has been created.
* 08/02/2007    sxm     added baseOnBeforeGotoPage() and baseOnAfterGotoPage() before/after invoking gotopage().
* 09/27/2007    sxm     Replaced hard code grid div style with class "divGrid"
* 03/14/2008    joe     Modify writeJavaScript() method: gridId_insertRow, gridId_deleteRow and gridId_undeleteRow
*                       will not call gridId_filter anymore.
* 08/03/2009    Fred    Modified writeNavButtons to allow changing the grid navigation buttons independently
* 09/03/2009    kshen   Added display only field for editable date field.
* 11/27/2009    kenney  enh to support phone format
* 09/20/2011    mxg     Issue #100716: Added Display Type FORMATTEDNUMBER
* 01/05/2011    clm     Issue 126620, wrap up baseOnBeforeGotoPage, gotopage, and baseOnAfterGotoPage as one function paginate
* 03/18/2014    kshen   Issue 153050. Convert "'" to "\\'" for JS.
* 03/18/2014    huixu   Issue 176582. add another export excel button to export all grid columns.
* 11/13/2018    wreeder 196147 - Initialize the loadingDeferredObj and sortingDeferredObj $.Deferred() objects
 * -----------------------------------------------------------------------------
*/
public class GridHelper {

    /**
     * The name of the property to set to use the Grid Header to define to displayable column order.
     * This property defaults to false.
     */
    public static final String PROPERTY_GRID_HEADER_DEFINES_DISPLAYABLE_COLUMN_ORDER = "grid.header.defines.displayable.column.order";
    public static final Boolean GRID_HEADER_DEFINES_DISPLAYABLE_COLUMN_ORDER_DEFAULT = Boolean.FALSE;

    /**
     * The name of the property to set to use the Grid Header to define to displayable column order.
     * This property defaults to false.
     */
    public static final String PROPERTY_GRID_HEADER_OASIS_FIELD_NAME_SUFFIX = "grid.header.oasis.field.name.suffix";
    public static final String GRID_HEADER_OASIS_FIELD_NAME_SUFFIX_DEFAULT = "";

    /**
     * The name of the property to setto  display readonly URL columns as a URL.
     * This property defaults to false.
     */
    public static final String PROPERTY_GRID_DISPLAY_READONLY_URL_AS_URL = "grid.display.readonly.url.as.url";
    public static final Boolean GRID_DISPLAY_READONLY_URL_AS_URL_DEFAULT = Boolean.FALSE;

    public static final String NUM_TYPE = "clsNum";
    public static final String NUM_TYPE_FORMATTED = "clsNumFmtd";
    public static final String STRING_TYPE = "BodyR";
    public static final String DATE_TYPE = "clsDate";
    public static final String PHONE_TYPE = "clsPhone";
    public static final String SSN_TYPE = "clsSSN";
    public static final String MONEY_TYPE = "clsMoney";
    public static final String DEFAULT_TYPE = "BodyR";
    public static final String MSXML_URL = "http://www.microsoft.com/downloads/results.aspx?productID=&freetext=msxml&DisplayLang=en";
    protected static final String clsName = GridHelper.class.getName();
    private static final String newLine = System.getProperty("line.separator");;
    private static final String KEY_ENVGRIDALWAYSSAVEASEXCELCSV = "gridAlwaysSaveAsExcelCsv";
    private static final String KEY_ENVGRIDALWAYSSAVEASEXCELHTML = "gridAlwaysSaveAsExcelHtml";
    private static final String KEY_EXPORT_TYPE = "gridExportExcelCsvType";
    private static final String EXPORT_TYPE_XLS = "XLS";
    private static final String EXPORT_TYPE_XLSX = "XLSX";
    private static final String EXPORT_TYPE_CSV = "CSV";
    public static final String LOCAL_PHONE_NUMBER_TYPE = "PH";

    private static boolean isXLSX = false;
    private static boolean isXLS = false;

    /**
     * Don't allow instantiation
     */
    protected GridHelper() {
    }

    /**
     * Creates html for a text input form field within a table cell
     *
     * @param label     Label to display
     * @param name      Name for input field
     * @param length    width of input field
     * @param align     html alignment
     * @param format    css class
     * @param maxLength maxlength attribute of input field
     * @param misc      additional attribute name/value pairs to add
     * @param datafld   Value to assign to datafld
     * @return HTML
     */
    public static String createText(String label, String name, String length, String align,
                                    String format, String maxLength, String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createText", new Object[]
        {label, name, length, align, format, maxLength, misc, datafld});

        if (GridHelper.DATE_TYPE.equals(format) && !FormatUtils.isDateFormatUS()) {
            // Add the DISP_ONLY field.
            StringBuffer buff = new StringBuffer("<TD ALIGN='").append(align).
                append("'><INPUT type='text' name='").
                append(name).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("'");
            if (length != null)
                buff.append(" size=\"").append(length).append("\" ");
            if (format != null)
                buff.append(" Class=\"").append(format).append("\" ");
            if (maxLength != null)
                buff.append(" maxlength=\"").append(maxLength).append("\" ");

            if (misc != null)
                buff.append(" ").append(misc);
            buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("\">");

            String html = buff.append("</TD>\n").toString();
            l.exiting(clsName, "createText", html);
            return html;
        }else if(GridHelper.LOCAL_PHONE_NUMBER_TYPE.equals(format)){
            StringBuffer buff = new StringBuffer("<TD ALIGN='").append(align).
                append("'><INPUT type='text' name='").
                append(name).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("'");
            if (length != null)
                buff.append(" size=\"").append(length).append("\" ");
            if (format != null)
                buff.append(" Class=\"").append(format).append("\" ");
            if (maxLength != null)
                buff.append(" maxlength=\"").append(maxLength).append("\" ");

            if (misc != null)
                buff.append(" ").append(misc);
            buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("\">");

            String html = buff.append("</TD>\n").toString();
            l.exiting(clsName, "createText", html);
            return html;
        }else if(GridHelper.NUM_TYPE_FORMATTED.equals(format)){
            StringBuffer buff = new StringBuffer("<TD ALIGN='").append(align).
                append("'><INPUT type='text' name='").
                append(name).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("'");
            if (length != null)
                buff.append(" size=\"").append(length).append("\" ");
            if (format != null)
                buff.append(" Class=\"").append(format).append("\" ");
            if (maxLength != null)
                buff.append(" maxlength=\"").append(maxLength).append("\" ");

            if (misc != null)
                buff.append(" ").append(misc);
            buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append(FormatUtils.DISPLAY_FIELD_EXTENTION).append("\">");

            String html = buff.append("</TD>\n").toString();
            l.exiting(clsName, "createText", html);
            return html;
        } else {
            StringBuffer buff = new StringBuffer("<TD ALIGN='").append(align).
                append("'><INPUT type='text' name='").
                append(name).append("'");
            if (length != null)
                buff.append(" size=\"").append(length).append("\" ");
            if (format != null)
                buff.append(" Class=\"").append(format).append("\" ");
            if (maxLength != null)
                buff.append(" maxlength=\"").append(maxLength).append("\" ");

            if (misc != null)
                buff.append(" ").append(misc);
            String html = buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append("\"></TD>\n").toString();
            l.exiting(clsName, "createText", html);
            return html;
        }
    }

    /**
     * Creates html for a text area form field within a table cell
     *
     * @param label     Label to display
     * @param name      Name for input field
     * @param rows      # rows
     * @param cols      # cols
     * @param align     html alignment
     * @param format    css class
     * @param misc      additional attribute name/value pairs to add
     * @param datafld   value to assign to datafld attribute
     * @return HTML
     */
    public static String createTextArea(String label, String name, String rows, String cols,
                                    String align, String format, String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createTextArea", new Object[]
        {label, name, rows, cols, align, format, misc, datafld});
        StringBuffer buff = new StringBuffer("<TD ALIGN='").append(align).
                append("'><TEXTAREA name=\"").append(name).append("\" ");
        if (rows != null)
            buff.append(" rows=\"").append(rows).append("\" ");
        if (format != null)
            buff.append(" Class=\"").append(format).append("\" ");
        if (cols != null)
            buff.append(" cols=\"").append(cols).append("\" ");

        if (misc != null)
            buff.append(" ").append(misc);
        String html = buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append("\"></TEXTAREA></TD>\n").toString();
        l.exiting(clsName, "createTextArea", html);
        return html;
    }

    /**
     * Creates HTML for a checkbox input form field in a table cell
     *
     * @param label    Label to display
     * @param name     Name for input field
     * @param readOnly prevent clicking on field
     * @param misc     additional attribute name/value pairs to add
     * @param datafld  value to assign to datafld attribute
     * @return HTML
     */
    public static String createCheckBox(String label, String name, boolean readOnly,
                                        String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createCheckBox", new Object[]
        {label, name, String.valueOf(readOnly), misc, datafld});
        StringBuffer buff = new StringBuffer("<TD ALIGN='CENTER'><input type='checkbox' name=\"").
                append(name).append("\"");

        if (misc != null)
            buff.append(" ").append(misc);
        if (readOnly)
            buff.append(" ReadOnly Disabled");

        String html = buff.append(" title='").append(label).append("' DataFld='").
                append(datafld).append("'></TD>\n").toString();

        l.exiting(clsName, "createCheckBox", html);
        return html;
    }

    /**
     * Creates HTML for a radiobutton input form field in a table cell
     *
     * @param label Label to display
     * @param name  Name for input field
     * @param misc  additional attribute name/value pairs to add
     * @param datafld  value to assign to datafld attribute
     * @return HTML
     */
    public static String createRadio(String label, String name, String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createRadio", new Object[]{
            label, name, misc,datafld});
        StringBuffer buff = new StringBuffer("<TD ALIGN='LEFT'><input type='radio' name=\"").
                append(name).append("\"");

        if (misc != null)
            buff.append(" ").append(misc);

        String html = buff.append(" title='").append(label).append("' DataFld='").
                append(datafld).append("'></TD>\n").toString();
        l.exiting(clsName, "createRadio", html);
        return html;

    }

    /**
     * Creates HTML for a SELECT input form field (dropdownlist) in a table cell
     *
     * @param label    Label to display
     * @param name     Name for input field
     * @param data     ArrayList of LabelValueBeans which populate the option list
     * @param selValue Selected value
     * @param misc     additional attribute name/value pairs to add
     * @param datafld  value to assign to datafld attribute
     * @return HTML
     */
    public static String loadCombo(String label, String name, ArrayList data,
                                   String selValue, String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "loadCombo", new Object[]
        {label, name, data, selValue, misc, datafld});
        StringBuffer buff = new StringBuffer(createCombo(label, name, misc, datafld));

        if (data != null && data.size() > 0) {
            int sz = data.size();
            for (int i = 0; i < sz; i++) {
                LabelValueBean bean = (LabelValueBean) data.get(i);
                buff.append(addComboItem(bean.getLabel(), bean.getValue(), selValue));
            }
        } else
            buff.append(addComboItem("-- No Item Found --", "-1", selValue));

        String html = buff.append("\n</SELECT></TD>\n").toString();
        l.exiting(clsName, "loadCombo", html);
        return html;

    }

    /**
     * Creates HTML to create a SELECT element inside a table cell
     * without the option list
     *
     * @param label
     * @param name
     * @param misc
     * @param datafld  value to assign to datafld attribute
     * @return HTML
     */
    private static String createCombo(String label, String name, String misc, String datafld) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createCombo",
                new Object[]{label, name, misc, datafld});
        StringBuffer buff = new StringBuffer("<TD ALIGN='LEFT'>").
                append("<SELECT id=\"").append(name).append("\" name=\"").append(name).
                append("\" ");

        if (misc != null)
            buff.append(" ").append(misc);

        String html = buff.append(" title=\"").append(label).append("\" DataFld=\"").
                append(datafld).append("\">\n").toString();
        l.exiting(clsName, "createCombo", html);
        return html;

    }

    /**
     * Creates HTML of an OPTION element
     *
     * @param label
     * @param value
     * @param selValue
     * @return HTML
     */
    private static String addComboItem(String label, String value, String selValue) {
        Logger l = LogUtils.enterLog(GridHelper.class, "addComboItem", new Object[]{label, value, selValue});
        StringBuffer buff = new StringBuffer("<OPTION value=\"").
                append(value).append("\"");
        if (value.trim().equalsIgnoreCase(selValue.trim()))
            buff.append(" SELECTED ");
        String html = buff.append(">").append(label).append("</OPTION>\n").toString();

        l.exiting(clsName, "addComboItem", html);
        return html;
    }

    /**
     * Creates HTML for a hidden input field
     *
     * @param name  Name of field
     * @param value Value for field
     * @return HTML
     */
    public static String createHidden(String name, String value) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createHidden", new Object[]{name, value});
        String html = new StringBuffer("<INPUT TYPE='HIDDEN' ID='").
                append(name).append("' NAME='").append(name).append("' VALUE=\"").
                append(value).append("\">").toString();
        l.exiting(clsName, "createHidden", html);
        return html;
    }

    /**
     * Creates HTML for an empty cell
     *
     * @param numNbsp # of Non Breaking Spaces to put in cell
     * @return HTML
     */
    public static String createEmptyCell(int numNbsp) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createEmptyCell", new Integer(numNbsp));
        StringBuffer buff = new StringBuffer("<TD>");
        for (int i = 0; i < numNbsp; i++)
            buff.append("&nbsp;");
        String html = buff.append("</TD>\n").toString();
        l.exiting(clsName, "createEmptyCell", html);
        return html;
    }


    /**
     * Creates HTML for an Button
     *
     * @param name       Name of button
     * @param caption    Text to display on button
     * @param onClick    onClick event handler
     * @param createCell Whether button should be created in a new table cell
     * @param styleClass override the default style of clsGridNav
     * @return HTML
     */
    public static String createButton(String name, String caption, String onClick,
                                      boolean createCell, String styleClass) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createButton", new Object[]
        {name, caption, onClick, String.valueOf(createCell), styleClass});
        String html = createButton(name, caption, onClick, createCell, styleClass, "100px");
        l.exiting(clsName, "createButton", html);
        return html;
    }

    /**
     * Creates HTML for an Button
     *
     * @param name       Name of button
     * @param caption    Text to display on button
     * @param onClick    onClick event handler
     * @param createCell Whether button should be created in a new table cell
     * @param styleClass override the default style of clsGridNav
     * @param width      added as style="width:xxx" where this parm is xxx
     * @return HTML
     */
    public static String createButton(String name, String caption, String onClick,
                                      boolean createCell, String styleClass, String width) {
        Logger l = LogUtils.enterLog(GridHelper.class, "createButton", new Object[]
        {name, caption, onClick, String.valueOf(createCell), styleClass, width});
        StringBuffer buff = new StringBuffer();
        if (createCell)
            buff.append("<TD ALIGN='LEFT'>");
        if (styleClass == null)
            styleClass = "buttonText";
/*        buff.append("<a href=\"").append(onClick).append("\" class=\"").
                append(style).append("\" ").append("id=\"").append(name).
                append("\">").append(caption).append("</a>");*/
        String style = (width == null) ? "" : new StringBuffer("style=\"width:").append(width).append("\" ").toString();
        buff.append("<input type='button' name=\"").
                append(name).append("\" value=\"").append(caption).append("\"").
                append(" class=\"").append(styleClass).append("\" ").append(style).
                append("onClick=\"").append(onClick).append("\">");
        if (createCell)
            buff.append("</TD>\n");
        String html = buff.toString();
        l.exiting(clsName, "createButton", html);
        return html;

    }

    /**
     * Writes out links to JavaScript files as well as
     * embedded JavaScript for the grid
     *
     * @param grid        The OasisGrid object
     * @param pageContext JSP pageContext
     * @param isCacheResultset boolean that indicates whether the resultset is cached
     * @throws JspException
     */
    public static void writeJavaScript(OasisGrid grid, Map map, PageContext pageContext, boolean isCacheResultset)
            throws JspException {
        Logger l = LogUtils.enterLog(GridHelper.class, "writeJavaScript", new Object[]{grid, pageContext});

        String gridId = grid.getGridId();
        String functionGrid = "function " + gridId;
        TagUtils util = TagUtils.getInstance();
        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
//        String appPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
        if(useJqxGrid)
            util.write(pageContext,"<p id='xmlerr' class='errortext dti-hide'>\n");
        else
            util.write(pageContext,"<p id='xmlerr' class='errortext' style='display:none;'>\n");
        util.write(pageContext,"This page may not behave properly because you do not have ");
        util.write(pageContext,"Microsoft XML 3.0 installed.  You should download ");
        util.write(pageContext,"and install Microsoft XML (MSXML) 3.0 before continuing.  ");
        util.write(pageContext,"<a target='_new' class='gridcontent' href='"+MSXML_URL+"'>");
        util.write(pageContext,"Click here</a> to locate the latest version of MSXML 3.0.</p>\n");
/*        String js = "<script type=\"text/javascript\" src=\"" + appPath + "/";
        // include required js files
        util.write(pageContext, js + "js/xmlproc.js\"></script>\n");
        util.write(pageContext, js + "js/edits.js\"></script>\n");
        util.write(pageContext, js + "js/gui.js\"></script>\n");
        util.write(pageContext, js + "js/scriptlib.js\"></script>\n");*/
        util.write(pageContext, "<script type=\"text/javascript\">\n");
        util.write(pageContext, "if(!isMSXML30()) document.all('xmlerr').style.display='block';\n");
        util.write(pageContext, " function dataGrid_update(gridId) { return eval(gridId + '_update()'); }\n");
        util.write(pageContext, " function gotoSet(fieldname,gridId,setNumber) {fieldname.value = setNumber; eval(gridId + \"_gotoSet('\" + setNumber + \"')\"); return true;}\n</script>\n");

        // javascript definition
        StringBuffer buff = new StringBuffer("<script type=\"text/javascript\">\n");
        buff.append("   var ").append(gridId).append("1StyleSheet; \n");

        // set the datemask for use in javascript
        if (Locale.getDefault().equals(Locale.US))
            buff.append("   DATE_MASK='mm/dd/yyyy'; \n");
        else
            buff.append("   DATE_MASK='dd.mm.yyyy'; \n");

        if(!isCacheResultset) {
            buff.append(gridId).append("1StyleSheet = ").append(gridId).append("1XSL.documentElement; \n").
            append("   if(!isMultiGridSupported) {\n").
            append("      xmlSource = ").append(gridId).append("1.documentElement; \n").
            append("   }\n");
        }

        util.write(pageContext, buff.toString());

        util.write(pageContext, new StringBuffer("var orig").append(gridId).append("1;\n").toString());
        if(!isCacheResultset) {
            util.write(pageContext, new StringBuffer("var orig").append(gridId).
                    append("1=").append(gridId).append("1.cloneNode(true);\n").toString());
        }

        // grid_sort function
        util.write(pageContext, new StringBuffer(functionGrid).append("_sort(field,fieldtype){").
            append(" baseOnBeforeSort(").append(gridId).append(", ").append(gridId).append("1,field,fieldtype);").
            append("XMLsort(").append(gridId).append("1StyleSheet,").append(gridId).append("1,field,fieldtype);").
            append(" baseOnAfterSort(").append(gridId).append(", ").append(gridId).append("1,field,fieldtype);").
            append("}\n").toString());



        // grid_update function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_update() { \n var modValue = ''; \n").
                append("var isFilterFlag = filterflag; \n").
                append("if(isMultiGridSupported) {\n").
                append("    isFilterFlag = getTableProperty(getTableForXMLData(").append(gridId).append("1) ,'filterflag'); \n").
                append("}\n").
                append("if (isFilterFlag) { syncChanges(orig").
                append(gridId).append("1,").append(gridId).append("1); modValue = getChanges(orig").
                append(gridId).append("1); } else { modValue = getChanges(").
                append(gridId).append("1);} document.").append(grid.getFormName()).
                append(".txtXML.value = modValue;").append("baseOnSubmit(document.").
                append(grid.getFormName()).append(");}\n ").toString());

        // grid_filter function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_filter(filtervalue) { filter(").append(gridId).
                append("1StyleSheet,orig").append(gridId).append("1,").
                append(gridId).append("1,filtervalue); setTable(").append(gridId).
                append(",").append(gridId).append("1); displayGridNavButtons(").
                append(gridId).append(");}\n").toString());

        // grid_getvalues function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getvalues(nodename) { return getValues(").
                append(gridId).append("1,nodename);}\n").toString());

        String gridEnd = new StringBuffer(gridId).append("1,").append(gridId).
                append("1); ").append("displayGridNavButtons(").
                append(gridId).append(");}\n").toString();

        // grid_insertrow function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_insertrow() { insertRow(").append(gridId).append(",orig").
                append(gridEnd).append(newLine).toString());

        // grid_deleterow function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_deleterow() { var hideit=deleteRow(").append(gridId).
                append(",orig").append(gridId).append("1,").append(gridId).
                append("1); ").
                append("if(hideit) hideEmptyTable(").
                append(gridId).append("); displayGridNavButtons(").
                append(gridId).append(");}\n").toString());

        // grid_undeleterow function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_undeleterow() { undeleteRow(").append(gridEnd).toString());

        // grid_updatenode function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_updatenode(nodename,nodevalue) { updateNode(").
                append(gridId).append("1,nodename,nodevalue);}\n").toString());

        // grid_onafterupdate function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_onafterupdate() { tbl_onafterupdate(").
                append(gridId).append("1);}\n").toString());

        // grid_updatefilternode
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_updatefilternode(filternodename,filternodevalue,nodename,nodevalue) { updateFilterNode(").
                append(gridId).
                append("1,filternodename,filternodevalue,nodename,nodevalue);}\n").toString());

        // grid_insertselectedrecord function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_insertselectedrecord(newxml) { insertSelectedRecord(").
                append(gridId).append("1,newxml);}\n").toString());

        // grid_getchanges function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getchanges() { \n var modValue = ''; \n").
                append("var isFilterFlag = filterflag; \n").
                append("if(isMultiGridSupported) {\n").
                append("    isFilterFlag = getTableProperty(getTableForXMLData(").append(gridId).append("1) ,'filterflag'); \n").
                append("}\n").
                append("if (isFilterFlag) { syncChanges(orig").
                append(gridId).append("1,").append(gridId).
                append("1); modValue = getChanges(orig").append(gridId).
                append("1); } else { modValue = getChanges(").append(gridId).
                append("1);} return modValue; }\n").toString());

        // grid_settable
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_settable() { setTable(").append(gridId).append(",").
                append(gridId).append("1);}\n").toString());

        // grid_selectall
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_selectall(obj) {if (obj.checked) ").
                append(grid.getFormName()).append("_btnClick('SELECT'); else ").
                append(grid.getFormName()).append("_btnClick('DESELECT');}\n").toString());

        // grid_getOBREnforcingFieldList function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getOBREnforcingFieldList() { return '").append(grid.getData().getOBREnforcingFieldList()).append("';}\n").toString());
        // grid_getOBRConsequenceFieldList function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getOBRConsequenceFieldList() { return '").append(grid.getData().getOBRConsequenceFieldList()).append("';}\n").toString());
        // grid_getOBRAllAccessedFieldList function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getOBRAllAccessedFieldList() { return '").append(grid.getData().getOBRAllAccessedFieldList()).append("';}\n").toString());
        // grid_getOBREnforcingUpdateIndicator function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getOBREnforcingUpdateIndicator() { return '").append(grid.getData().getOBREnforcingUpdateIndicator()).append("';}\n").toString());

        // grid_getColumnDataTypes function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getColumnDataTypes() { return '").append(map.get("colDataTypes").toString()).append("';}\n").toString());

        // grid_getColumnNames function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getColumnNames() { return '").append(StringUtils.replace(map.get("colNames").toString(), "'", "\\'")).append("';}\n").toString());

        // grid_getHiddenColumnNames function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getHiddenColumnNames() { return '").append(StringUtils.replace(map.get("hiddenColumnNames").toString(), "'", "\\'")).append("';}\n").toString());

        // grid_getHiddenColumnIds function
        util.write(pageContext, new StringBuffer(functionGrid).
                append("_getHiddenColumnIds() { return '").append(StringUtils.replace(map.get("hiddenColumnIds").toString(), "'", "\\'")).append("';}\n").toString());

        // grid_getExportType function

        if(isXLSX) {
            util.write(pageContext, new StringBuffer(functionGrid).
                    append("_getExportType() { return '").append("XLSX").append("';}\n").toString());
        } else if(isXLS){
            util.write(pageContext, new StringBuffer(functionGrid).
                    append("_getExportType() { return '").append("XLS").append("';}\n").toString());
        } else {
            util.write(pageContext, new StringBuffer(functionGrid).
                    append("_getExportType() { return '").append("CSV").append("';}\n").toString());
        }

        if(!isCacheResultset) {
            if (grid.isGridInsert() && grid.getData().getRowCount() == 0) {
                util.write(pageContext, new StringBuffer(gridId).
                        append("1.recordset.movefirst();").append(gridId).
                        append("1.recordset(\"UPDATE_IND\").value = \"D\";\n").toString());

            }
        }

// Moved this filter call after grid creation
/*
        if(!isCacheResultset) {
            // apply the XSLStyleSheet to the XMLData
            util.write(pageContext, new StringBuffer("filter(").append(gridId).
                    append("1StyleSheet,orig").append(gridId).append("1,").
                    append(gridId).append("1,'');\n").toString());
        }
*/
        util.write(pageContext, "</script>\n");
        l.exiting(clsName, "writeJavaScript");
    }

    /**
     * Creates HTML for the grid's table's header
     *
     * @param hdrArray ArrayList of column header labels
     * @return HTML
     */
    public static String loadTableHeader(ArrayList hdrArray) {
        Logger l = LogUtils.enterLog(GridHelper.class, "loadTableHeader", hdrArray);
        StringBuffer buff = new StringBuffer("<thead><tr>");
        int sz = hdrArray.size();
        for (int i = 0; i < sz; i++) {
            String val = (String) hdrArray.get(i);
            if (val != null && val.trim().length() > 0)
                buff.append("<TH><B>").append(val).append("</B></TH>");
        }
        String html = buff.append("</tr></thead>").toString();
        l.exiting(clsName, "loadTableHeader", html);
        return html;
    }

    /**
     * Writes the Grid Navigation buttons
     *
     * @param grid        current OasisGrid
     * @param set         ArrayList of LabelValueBeans for the Dataset dropdown
     * @param pageContext JSP PageContext
     * @param isCacheResultset boolean that indicates whether the resultset is cached
     * @throws JspException
     */
    public static void writeNavButtons(OasisGrid grid, ArrayList set,
                                       PageContext pageContext, boolean isCacheResultset) throws JspException {
        Logger l = LogUtils.enterLog(GridHelper.class, "writeNavButtons", new Object[]{grid, set, pageContext});
        String gridId = grid.getGridId();
        String dispTypeCSV = grid.getDispositionTypeExcelCsvFile();
        String dispTypeExcel = grid.getDispositionTypeExcelHtmlFile();
        TagUtils util = TagUtils.getInstance();
        util.write(pageContext, new StringBuffer("<script type='text/javascript'>\n").
                append("  var ").append(gridId).append("vcrNavBySet=").append(grid.isNavSets() && !grid.isNavPages()).
                append(";\n  var ").append(gridId).append("totalSets=").append(grid.getTotalDataSets()).
                append(";\n  var ").append(gridId).append("currSet=").append(grid.getGridPageNav()).
                append(";\n</script>\n").toString());

        boolean saveGridAsExcelCsv = false;
        boolean saveGridAsExcelHtml = false;

        Context env = null;
        String envVal = null;
        try {
            env = (Context) new InitialContext().lookup("java:comp/env");
        }
        catch (NamingException ignore) {
        }
        if (env != null) {
            try {
                // Look for entry "gridAlwaysSaveAsExcelCsv".
                envVal = (String) env.lookup(KEY_ENVGRIDALWAYSSAVEASEXCELCSV);
            }
            catch (NamingException ignore) {
                envVal = null;
            }
        }

        if (env == null || StringUtils.isBlank(envVal)) {
            envVal = ApplicationContext.getInstance().getProperty(KEY_ENVGRIDALWAYSSAVEASEXCELCSV, null) ;
        }

        if (!StringUtils.isBlank(envVal) && envVal.equalsIgnoreCase("true")) {
            // If entry "gridAlwaysSaveAsExcelCsv" is set to "true",
            // then all grids must show the Excel (CSV) button.
            saveGridAsExcelCsv = true;
        }
        else {
            // Otherwise, it is up to the individual grid's property to determine
            // if we should show the Excel (CSV) button.
            saveGridAsExcelCsv = grid.isSaveAsExcelCsv();
        }
        if (env != null) {
            try {
                // Look for entry "gridAlwaysSaveAsExcelHtml".
                envVal = (String) env.lookup(KEY_ENVGRIDALWAYSSAVEASEXCELHTML);
            }
            catch (NamingException ignore) {
                envVal = null;
            }
        }

        if (env == null || StringUtils.isBlank(envVal)) {
            envVal = ApplicationContext.getInstance().getProperty(KEY_ENVGRIDALWAYSSAVEASEXCELHTML, null) ;
        }

        if (!StringUtils.isBlank(envVal) && envVal.equalsIgnoreCase("true")) {
            // If entry "gridAlwaysSaveAsExcelHtml" is set to "true",
            // then all grids must show the Excel (HTML) button.
            saveGridAsExcelHtml = true;
        }
        else {
            // Otherwise, it is up to the individual grid's property to determine
            // if we should show the Excel (HTML) button.
            saveGridAsExcelHtml = grid.isSaveAsExcelHtml();
        }

        String exportType = ApplicationContext.getInstance().getProperty(KEY_EXPORT_TYPE, null);

        if(!StringUtils.isBlank(exportType) && !exportType.equalsIgnoreCase(EXPORT_TYPE_CSV)){
            if(exportType.equalsIgnoreCase(EXPORT_TYPE_XLS))
                isXLS = true;
            else //If neither CSV or XLS, default to XLSX even if there is a typo
                isXLSX = true;
        }

        // Display the header if we have more than one page or more than one set
        // OR we are allowing the grid to be saved to Excel as CSV
        // OR we are allowing the grid to be saved to Excel as HTML.
        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);

        // Create a JS variable for the grid called
        // "<theGridId>_excelButtonDisplayed" and set it to true or false.
        util.write(pageContext, new StringBuffer("<script type='text/javascript'>\n").
                append("  var ").append(gridId).
                append("_excelButtonDisplayed=").
                append(saveGridAsExcelCsv || saveGridAsExcelHtml).
                append(";\n</script>\n").toString());

        util.write(pageContext, "<table id='");
        util.write(pageContext, gridId);
        if(useJqxGrid){
            String cssClass = (
                    (grid.isNavPages() && grid.getData().getRowCount() > grid.getPageSize()) ||
                            (grid.isNavSets() && grid.getTotalDataSets() > 1)
            ) || saveGridAsExcelCsv || saveGridAsExcelHtml ? "" : "dti-hide";
            util.write(pageContext, "_HDRTBL' class='");
            util.write(pageContext, cssClass);
        } else {
            String displayHeader = (
                    (grid.isNavPages() && grid.getData().getRowCount() > grid.getPageSize()) ||
                            (grid.isNavSets() && grid.getTotalDataSets() > 1)
            ) || saveGridAsExcelCsv || saveGridAsExcelHtml ? "block" : "none";
            util.write(pageContext, "_HDRTBL' style='display:");
            util.write(pageContext, displayHeader);
        }
        util.write(pageContext, "' border='0' width='100%' ");
        String UIStyleEdition = ApplicationContext.getInstance().getProperty("UIStyleEdition","0");
        if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
            util.write(pageContext, ">");
            util.write(pageContext, "<tr valign='middle'><td align='left'>\n");
            util.write(pageContext, "&nbsp;</td>\n");
        } else if (UIStyleEdition.equalsIgnoreCase("2")) {
            util.write(pageContext, " cellpadding=0 cellspacing=0>");
            util.write(pageContext, "<tr>\n");
        }

        if (saveGridAsExcelCsv || saveGridAsExcelHtml) {
            // We need to create the save CSV to Excel button
            // and/or the save HTML to Excel button.
            StringBuffer csvExcelButtons = new StringBuffer();
            if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                csvExcelButtons.append(("<td align='center'>")).
                                append("<span id='").append(gridId).
                                append("_XMLGridCSVExcelButtons' style='visibility: visible'>\n").
                                append("<table border='0' cellspacing='2'>").
                                append(("<tr>"));

                if (saveGridAsExcelCsv) {
                    // Create the save CSV to Excel button.
                    csvExcelButtons.
                            append(GridHelper.createButton("btnSaveAsCSV", "Excel",
                            new StringBuffer("javascript:saveGridAsExcelCsv('").
                            append(gridId).append("', '").append(dispTypeCSV).
                            append("')").toString(), true, null, "125px")).
                            append(newLine).append(createEmptyCell(2));
                }
                if (saveGridAsExcelHtml) {
                    // Create the save CSV to HTML button.
                    csvExcelButtons.
                            append(GridHelper.createButton("btnSaveAsExcel", "Excel (HTML)",
                            new StringBuffer("javascript:saveGridAsExcelHtml('").
                            append(gridId).append("', '").append(dispTypeExcel).
                            append("')").toString(), true, null, "125px")).
                            append(newLine).append(createEmptyCell(2));
                }
                csvExcelButtons.append("</tr></table></span></td>");
            } else if (UIStyleEdition.equalsIgnoreCase("2")) {
                csvExcelButtons.append("<td>").
                    append("<div id='DIV_gridExport' class='gridExport' >").
                    append("<table id='").append(gridId).append("_XMLGridCSVExcelButtons' class='gridExportButtonTable' >").
                    append("<tr>");

                if (saveGridAsExcelCsv) {
                    // Create the save CSV to Excel button.
                    csvExcelButtons.append("<td> <input type=button id='btnSaveAsCSV' name='btnSaveAsCSV' ").
                        append(" class='excelExport' onClick=\"javascript:saveGridAsExcelCsv(").
                        append("'").append(gridId).append("', '").append(dispTypeCSV).append("');\" ").
                        append(" value=\"Export (Excel)\" onmouseover=\"this.className='excelExportMO'\" ").
                        append(" onmouseout=\"this.className='excelExport'\" />").append("</td>");
                    boolean enableExportAll = YesNoFlag.getInstance(ApplicationContext.getInstance().getProperty("grid.button.exportall.enable", "FALSE")).booleanValue();
                    if (enableExportAll) {
                        csvExcelButtons.append("<td> <input type=button id='btnSaveAllAsCSV' name='btnSaveAllAsCSV' ").
                            append(" class='excelExportAll' onClick=\"javascript:saveGridAsExcelCsv(").
                            append("'").append(gridId).append("', '").append(dispTypeCSV).append("', 'true');\" ").
                            append(" value=\"Export All\" onmouseover=\"this.className='excelExportAllMO'\" ").
                            append(" onmouseout=\"this.className='excelExportAll'\" />").append("</td>");
                    }
                }
                if (saveGridAsExcelHtml) {
                    // Create the save CSV to HTML button.
                    csvExcelButtons.
                        append("<td> <input type=button id='btnSaveAsExcel' name='btnSaveAsExcel' ").
                        append(" class='htmlExport' onClick=\"javascript:saveGridAsExcelHtml(").
                        append("'").append(gridId).append("', '").append(dispTypeExcel).append("');\" ").
                        append(" value=\"Export (HTML)\" onmouseover=\"this.className='htmlExportMO'\" ").
                        append(" onmouseout=\"this.className='htmlExport'\" />").
                        append("</td>");
                }
                csvExcelButtons.append("</tr></table></div>");
            }
            util.write(pageContext, csvExcelButtons.toString());
        } else {
            if (UIStyleEdition.equalsIgnoreCase("2")) {
                util.write(pageContext, "<td>");
            }
        }

        /* If isNavPages is true, the VCR buttons will be used to navigate the pages. */
        if (grid.isNavPages()) {
            String AX_navbuttonsvisibility =
                (grid.isShowRowCntOnePage() ||(grid.getData().getRowCount() > grid.getPageSize())) ? "visible" : "hidden";
            // Create a JS variable for the grid called "<theGridId>_showRowCntOnePage"
            // and set it to true or false based on the grid property showRowCntOnePage.
            util.write(pageContext, new StringBuffer("<script type='text/javascript'>\n").
                append("  var ").append(gridId).
                append("_showRowCntOnePage=").
                append(grid.isShowRowCntOnePage()).
                append(";\n</script>\n").toString());
            if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                util.write(pageContext, new StringBuffer("\n<td align='center'>").
                    append("<span id='").
                    append(gridId).append("_XMLGridNavButtons' style='visibility: ").
                    append(AX_navbuttonsvisibility).append("'>\n<table border='0' cellspacing='2'><tr>").toString());
            } else if (UIStyleEdition.equalsIgnoreCase("2")) {
                util.write(pageContext, new StringBuffer("\n<div class='gridNavSection' ").
                    append(" id='").
                    append(gridId).append("_XMLGridNavButtons' style='visibility: ").
                    append(AX_navbuttonsvisibility).append("'>\n").
                    append("<table id='gridNavButtonsTable'><tr>").toString());
            }
            util.write(pageContext, new StringBuffer("<td><table id='").append(gridId).
                                    append("_gridNavButtonsIndTable' style='display:block'>\n<tr>").toString());
            if (grid.getData().getRowCount() > grid.getPageSize()) {
                util.write(pageContext, new StringBuffer().
                    append(GridHelper.createButton("btnFirst",
                        (UIStyleEdition.equalsIgnoreCase("2") ? "&nbsp;" : "&lt;&lt;"),
                        new StringBuffer("javascript:paginate(").append(gridId).append(",'F');").toString(),
                        true,
                        (UIStyleEdition.equalsIgnoreCase("2") ? "navFirst" : null),
                        (UIStyleEdition.equalsIgnoreCase("2") ? null : "24px"))).
                    append(newLine).append((UIStyleEdition.equalsIgnoreCase("2") ? "" : createEmptyCell(1))).
                    append(GridHelper.createButton("btnPrevious",
                        (UIStyleEdition.equalsIgnoreCase("2") ? "&nbsp;" : "&lt;"),
                        new StringBuffer("javascript:paginate(").append(gridId).append(",'P');").toString(),
                        true,
                        (UIStyleEdition.equalsIgnoreCase("2") ? "navPrevious" : null),
                        (UIStyleEdition.equalsIgnoreCase("2") ? null : "24px"))).
                    append(newLine).append((UIStyleEdition.equalsIgnoreCase("2") ? "" : createEmptyCell(1))).
                    append(GridHelper.createButton("btnNext",
                        (UIStyleEdition.equalsIgnoreCase("2") ? "&nbsp;" : "&gt;"),
                        new StringBuffer("javascript:paginate(").append(gridId).append(",'N');").toString(),
                        true,
                        (UIStyleEdition.equalsIgnoreCase("2") ? "navNext" : null),
                        (UIStyleEdition.equalsIgnoreCase("2") ? null : "24px"))).
                    append(newLine).append((UIStyleEdition.equalsIgnoreCase("2") ? "" : createEmptyCell(1))).
                    append(GridHelper.createButton("btnLast",
                        (UIStyleEdition.equalsIgnoreCase("2") ? "&nbsp;" : "&gt;&gt;"),
                        new StringBuffer("javascript:paginate(").append(gridId).append(",'L');").toString(),
                        true,
                        (UIStyleEdition.equalsIgnoreCase("2") ? "navLast" : null),
                        (UIStyleEdition.equalsIgnoreCase("2") ? null : "24px"))).
                    append(newLine).append(createEmptyCell(2)).toString());
            }

            util.write(pageContext, new StringBuffer("</tr></table></td>").
                       append("<td align='left'><span class=\"bodystyle\" style='visibility:visible' id=\"").
                       append(gridId).append("_pageno\"></span></td></tr></table>").toString());

            if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                util.write(pageContext, new StringBuffer("</span></td>\n").toString());
            } else if (UIStyleEdition.equalsIgnoreCase("2")) {
                util.write(pageContext, new StringBuffer("</div></td>\n").toString());
            }

            /* If isNavSets is true then a dropdown list will be used to navigate
               through the data sets. */
            if (grid.isNavSets()) {
                if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                    util.write(pageContext, "<td align='right'><table border='0'><tr>\n");
                } else if (UIStyleEdition.equalsIgnoreCase("2")) {
                    if(useJqxGrid)
                        util.write(pageContext, "<td align='right' style='display:inline;'" + (grid.getTotalDataSets() > 1 ? "" : " class='dti-hide'") + " > ");
                    else
                        util.write(pageContext, "<td align='right' style='display:" + (grid.getTotalDataSets() > 1 ? "inline" : "none") + ";' > ");
                    util.write(pageContext, "<table border='0'><tr>\n");
                }
                if (grid.getTotalDataSets() > 1) {

                    util.write(pageContext, new StringBuffer("<TD ALIGN='RIGHT'>").
                            append("<span class='bodystyle'>Data Set:").append("</span></TD>").
                            append(loadCombo("Data Set", "cboGridSet",
                                    set, String.valueOf(grid.getGridPageNav()),
                                    new StringBuffer("onchange=\"javascript:gotoSet(document.").
                            append(grid.getFormName()).append(".txtGridPageNav,'").append(gridId).
                            append("',this.value)\" style=\"font-size:8pt;\"").toString(),"")).
                            append("<td><span class='bodystyle'> of ").append(grid.getTotalDataSets()).append("</span></td>").toString());
                } else
                    util.write(pageContext, "<td colspan='2'>&nbsp;</td>\n");
                util.write(pageContext, "</tr></table></td>\n");

            }
            util.write(pageContext, "</tr></table>");
        }
        /* If isNavPages is not true & isNavSets is true, the VCR buttons will be used
           to navigate the data sets. */
        else if (grid.isNavSets()) {
            String AX_navbuttonsvisibility = (grid.getTotalDataSets() > 1) ? "visible" : "hidden";
            if (UIStyleEdition.equalsIgnoreCase("0") || UIStyleEdition.equalsIgnoreCase("1")) {
                util.write(pageContext, new StringBuffer("\n<td align='center'>").
                        append("<span id='").append(gridId).append("_XMLGridNavButtons' style='visibility: ").
                        append(AX_navbuttonsvisibility).append("'>\n").toString());
            } else if (UIStyleEdition.equalsIgnoreCase("2")) {
                util.write(pageContext, new StringBuffer("\n<div align='center' id='gridNavSection' ").
                    append(" id='").
                    append(gridId).append("_XMLGridNavButtons' style='visibility: ").
                    append(AX_navbuttonsvisibility).append("; text-align:center'>\n").toString());
            }
            util.write(pageContext, new StringBuffer("<table border='0' cellspacing='2'><tr>").toString());

            /* go to 1st set and go to previous set */
            if (grid.getGridPageNav() > 1) {
                util.write(pageContext, new StringBuffer(createButton("btnFirst", "&lt;&lt;",
                        new StringBuffer("javascript:gotoSet(document.").
                        append(grid.getFormName()).append(".txtGridPageNav,'").
                        append(gridId).append("',1)").toString(), true, null, "24px")).
                        append(newLine).toString());
                util.write(pageContext, createEmptyCell(1));
                util.write(pageContext, new StringBuffer(createButton("btnPrevious", "&lt;",
                        new StringBuffer("javascript:gotoSet(document.").
                        append(grid.getFormName()).append(".txtGridPageNav,'").
                        append(gridId).append("',").append(grid.getGridPageNav() - 1).
                        append(")").toString(), true, null, "24px")).
                        append(newLine).toString());
                util.write(pageContext, createEmptyCell(1));

            } else {
                util.write(pageContext, "<td><span class='bodystyle grey' style='width:24px'>&lt;&lt;</span></td>\n" +
                        "<td>&nbsp;</td><td><span class='bodystyle grey' style='width:24px'>&lt;</span></td><td>&nbsp;</td>\n");
            }
            /* go to next and go to last set */
            if (grid.getGridPageNav() < grid.getTotalDataSets()) {
                util.write(pageContext, new StringBuffer(createButton("btnNext", "&gt;",
                        new StringBuffer("javascript:gotoSet(document.").
                        append(grid.getFormName()).append(".txtGridPageNav,'").
                        append(gridId).append("',").append(grid.getGridPageNav() + 1).
                        append(")").toString(), true, null, "24px")).
                        append(newLine).toString());
                util.write(pageContext, createEmptyCell(1));
                util.write(pageContext, new StringBuffer(createButton("btnLast", "&gt;&gt;",
                        new StringBuffer("javascript:gotoSet(document.").
                        append(grid.getFormName()).append(".txtGridPageNav,'").
                        append(gridId).append("',").append(grid.getTotalDataSets()).
                        append(")").toString(), true, null, "24px")).
                        append(newLine).toString());
                util.write(pageContext, createEmptyCell(2));
            } else {
                util.write(pageContext, "<td><span class='bodystyle grey' style='width:24px'>&gt;</span></td>\n" +
                        "<td>&nbsp;</td><td><span class='bodystyle grey' style='width:24px'>&gt;&gt;</span></td><td>&nbsp;&nbsp;</td>\n");
            }
            util.write(pageContext, new StringBuffer(createEmptyCell(2)).
                    append("<td align='left'><span class=\"bodystyle\" id=\"").
                    append(gridId).append("_pageno\"></span></td></tr></table></span></td></tr></table>\n").toString());

        }
        l.exiting(clsName, "writeNavButtons");
    }

    /**
     * Writes the grid HTML out.
     *
     * @param grid        Current OasisGrid
     * @param map         HashMap containing relevant grid info
     * @param pageContext Jsp PageContext
     * @param isCacheResultset boolean that indicates whether the resultset is cached
     * @throws JspException
     */

    public static void writeGrid(OasisGrid grid, Map map, PageContext pageContext, boolean isCacheResultset)
            throws JspException {
        Logger l = LogUtils.enterLog(GridHelper.class, "writeGrid", new Object[]{grid, map, pageContext});
        String gridId = grid.getGridId();
        TagUtils util = TagUtils.getInstance();
        if (grid.isShowHeader()) {
            util.write(pageContext, new StringBuffer("<table width=\"").
                    append(grid.getTableWidth()).
                    append("\" class=\"clsGrid\" border=\"1\">").
                    append(loadTableHeader((ArrayList) map.get("headerArray"))).
                    append("</table>\n").toString());
        }
        util.write(pageContext, new StringBuffer("<DIV id=\"DIV_").
                append(gridId).
                append("\" CLASS=\"divGrid\" STYLE=\"WIDTH: ").
                append(grid.getGridWidth()).append(";  HEIGHT: ").
                append(grid.getGridHeight()).append("\">\n").toString());

        util.write(pageContext, new StringBuffer("<table  width=\"").
                append(grid.getTableWidth()).append("\" class=\"clsGrid\" cellPadding=\"").
                append(grid.getCellPadding()).append("\" cellSpacing=\"").
                append(grid.getCellSpacing()).append("\" border=\"0\" datasrc=\"#").
                append(gridId).append("1\" datapagesize=\"").
                append(grid.getPageSize()).append("\" id=").append(gridId).
                append(" onafterupdate=\"return tbl_onafterupdate(this,").append(gridId).
                append("1);\" onbeforeupdate=\"return tbl_onbeforeupdate(this,").append(gridId).
                append("1);\" onreadystatechange=\"return alternate_colors(this);\" >\n").toString());

        if (!grid.isShowHeader()) {
            util.write(pageContext, new StringBuffer("<thead><tr>").
                    append((String) map.get("header")).append("</tr></thead>\n").toString());
        }


        if(isCacheResultset) {
            util.write(pageContext, ((StringBuffer) map.get("cols")).
                        //append("</table></div>\n").
                    append("</table></div>\n").toString());
        } else {
            util.write(pageContext, ((StringBuffer) map.get("cols")).
                    append("</table></div>").
                    append("<script type='text/javascript'>\n").
                    append("   var gridDetailDivIdAttribute = document.createAttribute('gridDetailDivId');  \n").
                    append("   gridDetailDivIdAttribute.nodeValue = \"" + grid.getGridDetailDivId() +  "\" \n").
                    append("   " + gridId + ".setAttributeNode(gridDetailDivIdAttribute); \n").
                    append("    setTable(").append(gridId).append(",").append(gridId).append("1);\n").
                    append("    dti.oasis.grid.setProperty(\"").append(gridId).append("\", \"loadingDeferredObj\", $.Deferred());\n").
                    append("    dti.oasis.grid.setProperty(\"").append(gridId).append("\", \"sortingDeferredObj\", $.Deferred().resolve());\n").
                    append("</script>\n").toString());
        }
        util.write(pageContext, (String) map.get("req"));
        l.exiting(clsName, "writeGrid");
    }

    /**
     * Determins if the application is configured to let the grid header define the displayable column order.
     * If a Grid Header XML file is used, and the fieldname attribute is not specified for all headers,
     * then the order is determined by the columns in the data result set.
     */
    public static boolean gridHeaderDefinesDisplayableColumnOrder() {
        if (m_gridHeaderDefinesDisplayableColumnOrder == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_GRID_HEADER_DEFINES_DISPLAYABLE_COLUMN_ORDER)) {
                m_gridHeaderDefinesDisplayableColumnOrder = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_GRID_HEADER_DEFINES_DISPLAYABLE_COLUMN_ORDER)).booleanValue());
            }
            else {
                m_gridHeaderDefinesDisplayableColumnOrder = GRID_HEADER_DEFINES_DISPLAYABLE_COLUMN_ORDER_DEFAULT;
            }
        }
        return m_gridHeaderDefinesDisplayableColumnOrder.booleanValue();
    }

    public static String getGridHeaderOasisFieldNameSuffix() {
        if (m_gridHeaderOasisFieldNameSuffix == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_GRID_HEADER_OASIS_FIELD_NAME_SUFFIX)) {
                m_gridHeaderOasisFieldNameSuffix = ApplicationContext.getInstance().getProperty(PROPERTY_GRID_HEADER_OASIS_FIELD_NAME_SUFFIX).toUpperCase();
            }
            else {
                m_gridHeaderOasisFieldNameSuffix = GRID_HEADER_OASIS_FIELD_NAME_SUFFIX_DEFAULT.toUpperCase();
            }
        }
        return m_gridHeaderOasisFieldNameSuffix;
    }

    public static boolean getGridDisplayReadonlyUrlAsUrl() {
        if (m_gridDisplayReadonlyUrlAsUrl == null) {
            // Allow this property to not be configured through Spring.
            if (ApplicationContext.getInstance().hasProperty(PROPERTY_GRID_DISPLAY_READONLY_URL_AS_URL)) {
                m_gridDisplayReadonlyUrlAsUrl = Boolean.valueOf(YesNoFlag.getInstance(
                    ApplicationContext.getInstance().getProperty(PROPERTY_GRID_DISPLAY_READONLY_URL_AS_URL)).booleanValue());
            }
            else {
                m_gridDisplayReadonlyUrlAsUrl = GRID_DISPLAY_READONLY_URL_AS_URL_DEFAULT;
            }
        }
        return m_gridDisplayReadonlyUrlAsUrl.booleanValue();
    }

    private static Boolean m_gridHeaderDefinesDisplayableColumnOrder;
    private static String m_gridHeaderOasisFieldNameSuffix;
    private static Boolean m_gridDisplayReadonlyUrlAsUrl;
}
