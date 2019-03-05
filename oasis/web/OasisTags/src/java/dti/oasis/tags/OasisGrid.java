package dti.oasis.tags;

import dti.oasis.app.ApplicationContext;
import dti.oasis.http.Module;
import dti.oasis.recordset.BaseResultSetRecordSetAdaptor;
import dti.oasis.recordset.RecordSet;
import dti.oasis.recordset.XMLRecordSetMapper;
import dti.oasis.struts.ActionHelper;
import dti.oasis.tags.jqxgrid.JqxGridHelper;
import dti.oasis.tags.ogcachemgr.GridData;
import dti.oasis.tags.ogcachemgr.OasisGridCacheManager;
import dti.oasis.util.BaseResultSet;
import dti.oasis.util.CollectionUtils;
import dti.oasis.util.DateUtils;
import dti.oasis.util.FormatUtils;
import dti.oasis.util.LogUtils;
import dti.oasis.util.StringUtils;
import dti.oasis.codelookupmgr.CodeLookupManager;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.ResponseUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSP Custom tag that provides a
 * sortable, editable, data-bound grid.
 *
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 *
 * Date:   Jun 20, 2003
 *
 * @author jbe
 */
/* Revision Date    Revised By  Description
* -----------------------------------------------------------------------------
* 2/7/2004     jbe         Add Logging
* 5/28/2004     jbe         Add handling for textareas
* 9/29/2004     jbe         Fix sorting on URL columns
* 10/7/2004     jbe         Support CN_DECIMALPLACES
* 10/14/2004    jbe         Add UPDATE_IND variation from DisconnectedResultSet
* 3/17/2005		jbe			Fix URL and add new attributes (headerName and dataName)
* 3/18/2005		jbe			Add new hidden element with PK value in grid table.
*							Add onclick handler for image types to ensure that image clicks cause row clicks.
* 4/6/2005      jbe         Check protected fields in getxmldata
*                           Find LOV in scope by fieldId if not provided otherwise.
* 5/19/2005		jbe			Support TYPE_UPDATEONLYURL
* 7/29/2005     jbe         Support TYPE_DATE, TYPE_DATETIME & TYPE_URL when there is no data.
* 8/19/2005		jbe			Change xmlns:xsl to "http://www.w3.org/1999/XSL/Transform"
*                           and revise XSL so that it is up to date
* 8/22/2005     jbe         Change eventhandler from onclick to onfocus, fix xsl:sort
* 9/2/2005      jbe         Support Struts 1.2, Replace RequestUtils & ResponseUtils with TagUtils.
* 9/12/2005     jbe         Add row selection logic to onfocus
* 9/13/2005     jbe         Replace language='javascript' with type='text/javascript'
* 9/19/2005     jbe         Only provide <xsl:sort IF sortable is true
* 11/09/2005    jbe         Sort on normal field for dates.
* 01/17/2006    sxm         Replace 'Select All' and 'Deselect All' buttons with a checkbox
*                           in the grid header that toggles
* 02/14/2006    sxm         Handle keyword "rowCount" for attributes pageSize
* 11/22/2006    sxm         Handle new attributes sortItem, sortType and sortOrder for sortable grid
* 12/11/2006    GCC         Added new attributes saveAsExcelCsv, saveAsExcelHtml,
*                           dispositionTypeExcelCsvFile, and
*                           dispositionTypeExcelHtmlFile.
* 01/12/2007    GCC         Changed initial value of saveAsExcelCsv to true.
* 01/16/2007    GCC         Changed initial value of saveAsExcelCsv back
*                           to false.
* 01/23/2007    mlm         Added support for caching the result set, and setting the url to retrieve it
* 01/23/2007    wer         Changed DisconnectedResultSet references to BaseResultSet;
*                           Refactored verification logic into verifyConfig();
*                           Added support to write updateonlydropdown as 2 columns (code and label), and display/sort the label;
*                           Added support for filtering on the DISPLAY_IND and writing the EDIT_IND;
* 01/31/2007    wer         Enhanced to support defining Grid Column Order by the Grid Header
* 02/01/2007    wer         Enhanced the default sort to select on the new index attribute of each ROW
* 02/09/2007    mlm         Enhancement for multi grid support
* 05/16/2007    sxm         Skip xxxLOVLABEL columns in resultSetToXml() since the result set may already contain the LOV labels
* 05/17/2007    sxm         Skip xxxLOVLABEL columns in getxmldata() since the result set may already contain the LOV labels
* 07/23/2007    gjl         consider SelectInd or select_ind to show table header as checkBox
* 08/20/2007    bhong       revert last change gjl made, always use "SELECT_IND" instead of "selectInd".
* 09/26/2007    sxm         replaced handling of CN_CELWIDTH w/ CN_STYLE
* 10/17/2007    sxm         default money field to right and others to left if we don't have alignment defined
* 12/4/2007     wer         Added check to hasNonDeletedRows to determine if an empty row should be added.
* 01/09/2008    wer         Added support to specify hrefkey as a data column name
* 01/11/2008    wer         Move the style definition to the div tag if the field has style set.
* 02/26/2008    wer         Fixed update columns to match the xml column index.
* 03/03/2008   James        Issue#79614 eClaims architectural enhancement to
*                           take advantage of ePolicy architecture
*                           Handling HREF is a new enhancement in WebWB
* 03/03/2008    wer         Added XMLTemplate to use for adding a new row when there are no rows in the XMLData (including no dummy rows)
* 03/13/2008    wer         Added UPDATE_IND != 'D' to the default filter.
* 03/24/2008    Fred        Added code to format percentage datatype;
*                           Added code to format currency even the field is invisible.
* 04/07/2008    yhchen      set UPDATE_IND to N for XMLTemplate
* 07/21/2008    kshen       Added code to display nothing instead of "-SELECT-" when the code
*                           of a readonly lov field in grid is "-1".
* 10/17/2008    yhyang      Add gridId as prefix to function initXMLIsland.
* 04/13/2009    mxg         82494: Changes to handle Date Format Internationalization
* 08/03/2009    Fred        Added showRowCntOnePage property
* 08/26/2009    kshen       Added codes to handle precent field.
* 09/01/2009    kshen       Added codes to select row when drop-down field is selected.
* 09/03/2009    kshen       Added onfocus event for dropdow field.
* 09/03/2006    mxg         Added handling for PHONE datatype
* 09/23/2009    Fred        Issue 96884. Extend Internationalization to Date / Time fields
* 10/09/2009    fcb         Issue# 96764: added logic for masked fields.
* 10/23/2009    Fred        Issue 100064: formatted percentage fields in grid
* 11/27/2009    kenney      enh to support phone format
* 07/26/2010    gzeng       issue#110068 - set lang attribute to <xsl:sort>,in the method named resultSetToXml(boolean isCacheResultSet)
* 08/12/2010    gzeng       issue#110187 - use general parameter 'dti.locale' as locale setting.
* 08/17/2010    gzeng       issue#110187 - change the variable from 'dti.locale' to 'oasis.locale'
* 08/17/2010    gzeng       issue#110187 - make the value of 'oasis.locale' case-insensitive
* 09/03/2010    dzhang      isssue#103800 - add method getAlignVal() to covert the 'L','C','R' to correct alignment.
* 11/09/2010    Kenney      issue#114120 - enh to support displaying multiple select field in grid
* 09/20/2011    mxg         Issue #100716: Added Display Type FORMATTEDNUMBER, added Format Pattern
* 09/29/2011    wfu         125316 - Added datatype attribute for currency and percent type field.
* 12/08/2011    bhong       128053 - Fixed an issue in row change event handler for IE9
* 09/20/2012    jxgu        133982 support special character in OBR_ENFORCED_RESULT
* 08/07/2013    Parker      Issue#134836: Use the lov in detail filed to display the value when the field in Grid is not set lov
* 08/30/2013    jxgu        Issue#147685: fix the wrong call to skipUnloadPageForCurrentAndIFrame
* 05/082014     mlm         154343 - Refactored to include gridId for grid's cache key
* 10/06/2014    awu         157694 - Modified getxmldata to get the data from records by column name instead of index.
* 12/03/2015    Elvin       Issue 165861: set border to 0 for IMG grid columns
* 03/18/2014    huixu       Issue 176582. add another export excel button to export all grid columns.
* 01/10/2016    mlm         181684 - Refactored to enforce change for issue 134836 only for fields hidden in grid, but visible in detail section.
* 07/20/2017    mlm         186748 - Reverted fix for 181684, but refactored to publish "code" or "description" in the
*                                    grid based on grid field's display type configuration.
*                           If the grid field's display type is configured as Text, but the linked form field is Select,
*                           then "code" will be shown in the grid, "description" in the form field.
*                           If both fields are configured as select, then "description" will be shown in both places.
* 10/24/2017    cesar       #186335 - Modified getxmldata() to check if the value need to be decoded before showing on the grid.
* 11/202/2017   cesar       #186297 - masked field for type TYPE_UPDATEONLYPHONE
* 11/29/2017    cesar       #190017 - rollback 186335. all the logic has been moved to ActionHelper.encodeMaskedField()
* 12/19/2017    ylu         #190396 -
*                           1) add Mask for secured grid column which has type default to 0
*                           2) add Mask for secured grid column which has href url link
* 02/27/2018    cesar       #191524 - display encoded values for dates.
* 11/13/2018    wreeder     196147 - Support deferredLoadDataProcess, virtualPaging and virtualScrolling.
 * -----------------------------------------------------------------------------
*/
public class OasisGrid extends BodyTagSupport {

    // Public Fields
    public static final String ATTACH_DISP_TYPE = "attachment";
    public static final String INLINE_DISP_TYPE = "inline";

    // Private Fields
    private String actionName;
    private String formName;
    private String gridId;
    private BaseResultSet data;
    private XMLGridHeader header;
    private String pageSize;
    private int gridPageNav;
    private int totalDataSets;
    private int dataSetSize;
    private boolean selectable;
    private boolean showHeader;
    private boolean gridInsert;
    private boolean sortable;
    private String sortItem;
    private String sortType;
    private String sortOrder;
    private boolean navPages = true;
    private boolean navSets = true;
    private String gridWidth;
    private String gridHeight;
    private String tableWidth;
    private String cellSpacing;
    private String cellPadding;
    private String dataName;
    private String headerName;
    private boolean cacheResultset=false;
    private boolean saveAsExcelCsv = false;
    private boolean saveAsExcelHtml = false;
    private String dispositionTypeExcelCsvFile = ATTACH_DISP_TYPE;
    private String dispositionTypeExcelHtmlFile = ATTACH_DISP_TYPE;
    private String gridDetailDivId = "detailDiv";
    private String deferredLoadDataProcess;
    private boolean virtualPaging;
    private boolean virtualScrolling;

    //Setting it to true indicating to show the rows count in the grid
    //even there is only one page.
    //Default is false.
    private boolean showRowCntOnePage = false;

    // Private Fields
    private static final String strOpen = "<";
    private static final String strClose = ">";
    private static final String strEndOpen = "</";

    /**
     * @return
     * @throws JspException
     * @see javax.servlet.jsp.tagext.Tag#doStartTag
     */
    public int doStartTag() throws JspException {
        l.entering(getClass().getName(), "doStartTag");

        if (l.isLoggable(Level.FINE)) {
            l.fine(toString());
        }

        int rc = 0;

        verifyConfig();

        if (!header.isInitialized())
            rc = EVAL_BODY_BUFFERED;
        else {
            if (navSets && !navPages && gridInsert) {
                throw new JspException("Invalid parameters. navSets=true, navPages=false, gridInsert=true. " +
                        "Grids that navigate purely with datasets should not be updateable.");
            }
            try {
                if (formName == null)
                    deriveFormName();

                header.processDataColumns(data);

                boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);

                if (useJqxGrid) {
                    PrintWriter out = pageContext.getResponse().getWriter();
                    try {
                        JqxGridHelper.getInstance().buildJqxGrid(this, (HttpServletRequest) pageContext.getRequest(), out);
                    } finally {
                        out.flush();
                    }
                } else {
                    buildDataGrid(isCacheResultset());
                }

                rc = EVAL_BODY_BUFFERED;
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new JspException(e);
            }
        }
        l.exiting(getClass().getName(), "doStartTag", new Integer(rc));
        return rc;

    }

    protected void verifyConfig() throws JspException {
        l.entering(getClass().getName(), "verifyConfig");

        // Verify the header or headerName has been configured
        if (header == null && headerName == null) {
            throw new JspException("Either header or headerName attributes must be provided.");
        }
        if (header == null) {
            // Lookup the header by the headerName bean name
            try {
                header = (XMLGridHeader) TagUtils.getInstance().lookup(pageContext, headerName, null);
            }
            catch (ClassCastException ce) {
                l.warning("Caught: " + ce.toString());
                header = null;
            }
            if (header == null) {
                throw new JspException("Expecting, but did not find XMLGridHeader in scope, named " + headerName);
            }
        }

        // Verify the data or dataName has been configured
        if (data == null && dataName == null) {
            throw new JspException("Either data or dataName attributes must be provided.");
        }
        if (data == null) {
            // Lookup the data by the dataName bean name
            try {
                data = (BaseResultSet) TagUtils.getInstance().lookup(pageContext, dataName, null);
            }
            catch (ClassCastException ce) {
                l.warning("Caught: " + ce.toString());
                data = null;
            }
            if (data == null) {
                throw new JspException("Expecting, but did not find BaseResultSet in scope, named " + dataName);
            }
        }

        l.exiting(getClass().getName(), "verifyConfig");
    }

    /**
     * @throws JspException
     */
    private void deriveFormName() throws JspException {
        l.entering(getClass().getName(), "deriveFormName");
        if (actionName == null)
            throw new JspException("Either actionName or formName property must be supplied.");
        formName = ActionHelper.getFormFromAction(actionName, pageContext);
        l.exiting(getClass().getName(), "deriveFormName");
    }

    /**
     * @throws JspException
     * @throws ParseException
     */
    private void buildDataGrid(boolean isCacheResultset) throws JspException, ParseException {
        l.entering(getClass().getName(), "buildDataGrid");

        if(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDisplayFormName")!=null) {
            setFormName(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDisplayFormName").toString());
        } else {
            if(StringUtils.isBlank(getFormName())) {
                setFormName("gridlist");
            }
        }
        if(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDisplayGridId")!=null) {
            setGridId(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDisplayGridId").toString());
        } else {
            if(StringUtils.isBlank(getGridId())) {
                setGridId("testgrid");
            }
        }
        if(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDetailDivId")!=null) {
            this.setGridDetailDivId(((HttpServletRequest) pageContext.getRequest()).getAttribute("gridDetailDivId").toString());
        } else {
            if(StringUtils.isBlank(getGridDetailDivId())) {
                setGridDetailDivId("");
            }
        }

        // Setup the page size
        pageSize = (pageSize == null) ? "10" : pageSize;
        if (pageSize.equals("rowCount"))
            pageSize = String.valueOf(data.getRowCount()); // TODO: Add attribute to set the row count
        else if (!FormatUtils.isInt(pageSize))
            throw new JspException("pageSize must be an integer or a keyword .");

        cellPadding = (cellPadding == null || cellPadding.trim().length() == 0) ? "0" : cellPadding;
        cellSpacing = (cellSpacing == null || cellSpacing.trim().length() == 0) ? "0" : cellSpacing;
        TagUtils util = TagUtils.getInstance();
        util.write(pageContext, GridHelper.createHidden("txtXML", ""));
        gridPageNav = (gridPageNav == 0) ? 1 : gridPageNav;
        util.write(pageContext, GridHelper.createHidden("txtGridPageNav", String.valueOf(gridPageNav)));
        util.write(pageContext, GridHelper.createHidden("txtCurrentRowId", "-1"));

        ArrayList setArray = null;
        boolean isData = data.getRowCount() > 0;
        if (gridInsert || isData) {
            if (totalDataSets > 0) {
                setArray = new ArrayList(totalDataSets);
                for (int i = 0; i < totalDataSets; i++) {
                    String si = String.valueOf(i + 1);
                    setArray.add(new LabelValueBean(si, si));
                }
            }
            HashMap map = this.resultSetToXml(isCacheResultset);

            //Moved it after writeGrid function call, so that all required variables are set and ready for javascript consumption
            //GridHelper.writeJavaScript(this, pageContext, isCacheResultset);

            GridHelper.writeNavButtons(this, setArray, pageContext, isCacheResultset);

            if (tableWidth == null || tableWidth.trim().length() == 0)
                tableWidth = "100%";

            GridHelper.writeGrid(this, map, pageContext, isCacheResultset);

            GridHelper.writeJavaScript(this, map, pageContext, isCacheResultset);

            if(!isCacheResultset) {
                util.write(pageContext, new StringBuffer("").
                    append("<script type='text/javascript'>\n").
                    append("    if(isMultiGridSupported) {\n").
                    append("       setTableProperty(").append(gridId).append(", 'updateablecolset', '\"").append(map.get("updateablecolset").toString()).append("\"'); \n").
                    append("       setTableProperty(").append(gridId).append(", 'anchorfieldname', '\"").append(map.get("anchorfieldname").toString()).append("\"'); \n").
                    append("    } else {\n").
                    append("       updateablecolset = '").append(map.get("updateablecolset").toString()).append("'; \n").
                    append("       anchorfieldname = '").append(map.get("anchorfieldname").toString()).append("'; \n").
                    append("    } \n").
                    // apply the XSLStyleSheet to the XMLData
                    append("  filter(").append(gridId).append("1StyleSheet,orig").append(gridId).append("1,").append(gridId).append("1,'');\n").
                    append("</script>\n").toString());
            }
            else {
                ServletRequest request = pageContext.getRequest();
                // Add the cachedGridFormFieldsHidden class immediately for the case when the form fields are written before the grid,
                // and add the cachedGridFormFieldsHidden class later in compiledFormFields for the case when the form fields are written after the grid.
                request.setAttribute("formFieldsTableForpanelContentIdFor" + gridDetailDivId + "IsCachedGrid", Boolean.TRUE);
                util.write(pageContext, new StringBuffer("").
                    append("<script type='text/javascript'>\n").
                    append("  $('#formFieldsTableForpanelContentIdFor").append(gridDetailDivId).append("').addClass('cachedGridFormFieldsHidden');\n").
                    append("</script>\n").toString());
            }

            // hide the table
            if (!isData && !isCacheResultset) {
                util.write(pageContext, new StringBuffer("<script type=\"text/javascript\"> hideEmptyTable(").
                        append(gridId).append(");</script>").toString());
            }
        }
        l.exiting(getClass().getName(), "buildDataGrid");
    }

    /**
     * @param isCacheResultSet boolean to indicate whether to cache the resultset data.
     * @return HashMap containing columns, headers, xml, & hdr array
     * @throws JspException
     * @throws ParseException
     */

    private HashMap resultSetToXml(boolean isCacheResultSet) throws JspException, ParseException {
        l.entering(getClass().getName(), "resultSetToXml");
        boolean useJqxGrid = OasisTagHelper.isUseJqxGrid(pageContext);
        boolean bAnchor = false;
        String langAttr = ApplicationContext.getInstance().getProperty("oasis.locale");
        if(!StringUtils.isBlank(langAttr))  langAttr = langAttr.toLowerCase();

        String anchorfieldname = "", sColName;
        StringBuffer strXMLURL = new StringBuffer();
        StringBuffer strXMLDate = new StringBuffer();
        Set updateCols = new TreeSet();
        StringBuffer sUpdtCol = new StringBuffer();
        StringBuffer sColDataTypes = new StringBuffer();
        StringBuffer sColNames = new StringBuffer();
        StringBuffer hiddenColumnNames = new StringBuffer();
        StringBuffer hiddenColumnIds = new StringBuffer();
        String colDataTypesSep = "";
        String colNamesSep = "";
        StringBuffer strCol = new StringBuffer();
        StringBuffer sHdr = new StringBuffer();
        StringBuffer sColReq = new StringBuffer();
        TagUtils util = TagUtils.getInstance();
        strCol.append("<TR onmouseover=\"hiliteRow(this)\" onmouseout=\"unhiliteRow(this)\" onClick=\"hiliteSelectRow(this)\" onkeydown=\"hiliteSelectAnotherRow(this)\" >\n");
        sColReq.append("<script type='text/javascript'>\n");
        int colCount = data.getColumnCount();
        ArrayList hdrArray = new ArrayList(colCount);

        data.first();
        sColName = getDataIslandColumnName(1);

        util.write(pageContext, new StringBuffer("\n<XML id=\"").append(gridId).
                append("1XSL\">\n").
                append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n").
                append("<xsl:template match=\"/\" >\n<ROWS>\n").
                append("<xsl:for-each select=\"//ROW[DISPLAY_IND = ").append( (isCacheResultSet==true?"$DISPLAYIND_Y":"'Y'") ).append(" and UPDATE_IND != ").append( (isCacheResultSet==true?"$UPDATEIND_D":"'D'") ).append("]\">\n").toString());
        if(sortable) {
            sortItem = sortItem == null ? "@index" : sortItem;
            sortType = sortType == null ? "number" : sortType;
            sortOrder = sortOrder == null ? "ascending" : sortOrder;

            if(!StringUtils.isBlank(langAttr)){
            util.write(pageContext, new StringBuffer("<xsl:sort select=\"").append(sortItem).append("\" lang=\"").append(langAttr).
                append("\" order=\"").append(sortOrder).
                append("\" data-type=\"").append(sortType).append("\" />\n").toString());
            }else {
            util.write(pageContext, new StringBuffer("<xsl:sort select=\"").append(sortItem).
                append("\" order=\"").append(sortOrder).
                append("\" data-type=\"").append(sortType).append("\" />\n").toString());
            }
        }
        util.write(pageContext,"<ROW>\n<xsl:attribute name=\"id\"><xsl:value-of select=\"@id\"/></xsl:attribute>\n");
        util.write(pageContext," <xsl:attribute name=\"index\"><xsl:value-of select=\"@index\"/></xsl:attribute>\n");
        util.write(pageContext," <xsl:attribute name=\"col\"><xsl:value-of select=\"@col\"/></xsl:attribute>\n");

        String align, name;
        int display, type;
        String length, maxLength, fieldId;
        String rows, cols, title;
        String style;
        boolean isProtected;
        String sColNameAlt;
        boolean isMasked;
        String pattern;

        // Iterate through the data columns, writing the XSL and XML information for them
        for (int i = 1; i <= colCount; i++) {

            type = ((Integer) header.getHeader(data.getColumnName(i)).get(XMLGridHeader.CN_TYPE)).intValue();
            display = ((Integer) header.getHeader(data.getColumnName(i)).get(XMLGridHeader.CN_DISPLAY)).intValue();
            sColName = getDataIslandColumnName(i);

            // skip xxxLOVLABEL columns
            if (sColName.endsWith("LOVLABEL"))
                continue;
            // skip xxx_FORMATTED columns            
            if (sColName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                continue;

            sColNameAlt = (type == XMLGridHeader.TYPE_UPDATEONLYDROPDOWN || type == XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN ? sColName + "LOVLABEL" : sColName);

            util.write(pageContext, new StringBuffer("<").
                    append(sColName).append("><xsl:value-of select=\"").
                    append(sColName).append("\" />").append("</").
                    append(sColName).append(">\n").toString());
            if (type == XMLGridHeader.TYPE_UPDATEONLYDROPDOWN &&
                    OasisTagHelper.displayReadonlyCodeLookupAsLabel()) {
                util.write(pageContext, new StringBuffer("<").
                        append(sColNameAlt).append("><xsl:value-of select=\"").
                        append(sColNameAlt).append("\" />").append("</").
                        append(sColNameAlt).append(">\n").toString());
            } else if (type == XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN &&
                    OasisTagHelper.displayReadonlyCodeLookupAsLabel()) {
                util.write(pageContext, new StringBuffer("<").
                        append(sColNameAlt).append("><xsl:value-of select=\"").
                        append(sColNameAlt).append("\" />").append("</").
                        append(sColNameAlt).append(">\n").toString());
            }
            /*
            *
            * TODO: Additional Types: XMLGridHeader.TYPE_UPDATEONLYDATETIME
            *
            */
            if(!FormatUtils.isDateFormatUS()) {
                if (type == XMLGridHeader.TYPE_UPDATEONLYDATE ||
                    type == XMLGridHeader.TYPE_FORMATDATE ||
                    type == XMLGridHeader.TYPE_DATE ||
                    type == XMLGridHeader.TYPE_FORMATDATETIME ||
                    type == XMLGridHeader.TYPE_UPDATEONLYDATETIME
                   )
                {
                    sColNameAlt = sColName + FormatUtils.DISPLAY_FIELD_EXTENTION;
                    util.write(pageContext, new StringBuffer("<").
                        append(sColNameAlt).append("><xsl:value-of select=\"").
                        append(sColNameAlt).append("\" />").append("</").
                        append(sColNameAlt).append(">\n").toString());
                }
            }

            if (type == XMLGridHeader.TYPE_UPDATEONLYPHONE || type == XMLGridHeader.TYPE_PHONE) {
                sColNameAlt = sColName + FormatUtils.DISPLAY_FIELD_EXTENTION;
                util.write(pageContext, new StringBuffer("<").
                    append(sColNameAlt).append("><xsl:value-of select=\"").
                    append(sColNameAlt).append("\" />").append("</").
                    append(sColNameAlt).append(">\n").toString());
            }

            if (display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER &&(type == XMLGridHeader.TYPE_NUMBER || type == XMLGridHeader.TYPE_UPDATEONLYNUMBER)) {
                sColNameAlt = sColName + FormatUtils.DISPLAY_FIELD_EXTENTION;
                util.write(pageContext, new StringBuffer("<").
                    append(sColNameAlt).append("><xsl:value-of select=\"").
                    append(sColNameAlt).append("\" />").append("</").
                    append(sColNameAlt).append(">\n").toString());
            }
            //Add here *********************************
        }

        // Iterate through the headers, and write the HTML Page information for them to String Buffers
        for (int i = 1; i <= header.size(); i++) {
            HashMap headerMap = header.getHeaderMap(i);
            String dataColumnName = (String) headerMap.get(XMLGridHeader.CN_DATACOLUMNNAME);
            int dataColumnIdx = header.getDataColumnIndex(dataColumnName);
            int xmlColumnIdx = header.getXmlColumnIndex(dataColumnName);

            type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
            sColName = getDataIslandColumnName(dataColumnIdx);

            // skip xxxLOVLABEL columns
            if (sColName.endsWith("LOVLABEL"))
                continue;

            if (sColName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                continue;

            sColNameAlt = (type == XMLGridHeader.TYPE_UPDATEONLYDROPDOWN || type == XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN ? sColName + "LOVLABEL" : sColName);

            isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();
            isMasked = ((Boolean) headerMap.get(XMLGridHeader.CN_MASKED)).booleanValue();
            name = (String) (headerMap.get(XMLGridHeader.CN_NAME) == null ? "" : headerMap.get(XMLGridHeader.CN_NAME));

            // Begin Visible Columns
            if (!isProtected && !headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {

                sColDataTypes.append(colDataTypesSep).append(type); colDataTypesSep = ",";

                align = (String) headerMap.get(XMLGridHeader.CN_ALIGN);
                length = (String) headerMap.get(XMLGridHeader.CN_LENGTH);
                pattern = (String) headerMap.get(XMLGridHeader.CN_PATTERN);
                if(pattern==null)
                    pattern = "";
                display = ((Integer) headerMap.get(XMLGridHeader.CN_DISPLAY)).intValue();
                maxLength = (String) headerMap.get(XMLGridHeader.CN_MAXLENGTH);
                rows = (String) headerMap.get(XMLGridHeader.CN_ROWS);
                cols = (String) headerMap.get(XMLGridHeader.CN_COLS);
                title = (String) headerMap.get(XMLGridHeader.CN_TITLE);
                fieldId = (String) headerMap.get(XMLGridHeader.CN_FIELDID);
                style = (String) headerMap.get(XMLGridHeader.CN_STYLE);
                if (title == null)
                    title = name;
                if (align == null) {
                    // if we don't have alignment defined, default money to right and others to left
                    if (display == XMLGridHeader.DISPLAY_MONEY)
                        align = "right";
                    else
                        align = "left";
                }
                else {
                    align = getAlignVal(align);
                }

                String appPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();
                String corePath = Module.getCorePath((HttpServletRequest) pageContext.getRequest());
                if (type == XMLGridHeader.TYPE_URL || type == XMLGridHeader.TYPE_UPDATEONLYURL||
                    ((type == XMLGridHeader.TYPE_UPDATEONLYDROPDOWN || type == XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN) && !StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF)))) {
                    strXMLURL.append("<URL_").append(xmlColumnIdx).append("><xsl:value-of select=\"URL_").
                            append(xmlColumnIdx).append("\" /></URL_").append(xmlColumnIdx).append(">\n");

                }
                else if (type == XMLGridHeader.TYPE_FORMATDATE ||
                         type == XMLGridHeader.TYPE_DATE ||
                         type == XMLGridHeader.TYPE_UPDATEONLYDATE ||
                         type == XMLGridHeader.TYPE_FORMATDATETIME ||
                         type == XMLGridHeader.TYPE_UPDATEONLYDATETIME) {
                    strXMLDate.append("<DATE_").append(xmlColumnIdx).append("><xsl:value-of select=\"DATE_").
                            append(xmlColumnIdx).append("\" />").append("</DATE_").append(xmlColumnIdx).append(">\n");
                }

                sColNames.append(colNamesSep).append(name.replaceAll("&nbsp;","%20").replaceAll(",",":;:")); colNamesSep = ",";
                // Add Visible Columns to hdrArray, append to sHdr (StringBuffer for grid header)
                // write checkbox in header if selectable
                if (selectable && (sColName.startsWith("CSELECT_IND"))) {
                    String selectAllName = "chkCSELECT_ALL" + sColName.substring(11);
                    hdrArray.add(new StringBuffer("<input type=\"checkbox\" name=\"").append(selectAllName).
                            append("\" onclick=\"").append(gridId).append("_selectall(this);\" ").
                            append(" title=\"Select or de-select all\" /> ").toString());

                    sHdr.append("<th id=\"H").append(sColName).append("\"") ;
                    if(style!=null)
                        sHdr.append(" STYLE=\"").append(style).append("\"");
                    sHdr.append("> ").
                            append("<input type=\"checkbox\" name=\"").append(selectAllName).
                            append("\" onclick=\"").append(gridId).append("_selectall(this);\" ").
                            append(" title=\"Select or de-select all\" /> ").
                            append("</th>");
                    sColReq.append(" var REQ_").append(selectAllName).append("=false;\n");
                }
                else if (sortable) {
                    if (name == null || name.trim().length() == 0) {
                        if(useJqxGrid)
                            hdrArray.add(new StringBuffer("<a class=\"gridheader\" href=\"javascript:").
                                    append(gridId).append("_sort('").append(sColNameAlt).
                                    append("');\"><img id=\"wfsort").append(sColNameAlt).append("\" SRC=\"").
                                    append(corePath).append("/images/asc.gif\" WIDTH=\"7\" HEIGHT=\"7\" BORDER=\"0\" CLASS=\"dti-hide\">&nbsp;").
                                    append(sColName).append("</a>").toString());
                        else
                            hdrArray.add(new StringBuffer("<a class=\"gridheader\" href=\"javascript:").
                                    append(gridId).append("_sort('").append(sColNameAlt).
                                    append("');\"><img id=\"wfsort").append(sColNameAlt).append("\" SRC=\"").
                                    append(corePath).append("/images/asc.gif\" WIDTH=\"7\" HEIGHT=\"7\" BORDER=\"0\" STYLE=\"display:none\">&nbsp;").
                                    append(sColName).append("</a>").toString());
                        sHdr.append("<th id=\"H").append(sColNameAlt).append("\"") ;;
                        if(style!=null)
                            sHdr.append(" STYLE=\"").append(style).append("\"");
                        if(useJqxGrid)
                            sHdr.append("> <a class=\"gridheader\" href=\"javascript:").
                                    append(gridId).append("_sort('").append(sColNameAlt).append("');\"><img id=\"wfsort").
                                    append(sColNameAlt).append("\" SRC=\"").append(corePath).
                                    append("/images/asc.gif\" WIDTH=\"7\" HEIGHT=\"7\" BORDER=\"0\" CLASS=\"dti-hide\">&nbsp;").
                                    append(sColName).append("</a></th>");
                        else
                            sHdr.append("> <a class=\"gridheader\" href=\"javascript:").
                                    append(gridId).append("_sort('").append(sColNameAlt).append("');\"><img id=\"wfsort").
                                    append(sColNameAlt).append("\" SRC=\"").append(corePath).
                                    append("/images/asc.gif\" WIDTH=\"7\" HEIGHT=\"7\" BORDER=\"0\" STYLE=\"display:none\">&nbsp;").
                                    append(sColName).append("</a></th>");
                    }
                    else {
/*                        switch (type) {

                            case XMLGridHeader.TYPE_FORMATDATE:
                            case XMLGridHeader.TYPE_FORMATDATETIME:
                            case XMLGridHeader.TYPE_UPDATEONLYDATE:
                            case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                                hdrArray.add(new StringBuffer("<a class=\"gridheader\" href=\"javascript:").append(gridId).
                                        append("_sort('DATE_").append(i).append("','").append(type).append("');\"><img id=\"wfsortDATE_").
                                        append(i).append("\" SRC=\"").append(appPath).
                                        append("/Images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 STYLE=\"display:none\">&nbsp;").
                                        append(name).append("</a>").toString());
                                sHdr.append("<th id=\"H").append(sColName).append("\"> <a class=\"gridheader\" href=\"javascript:").
                                        append(gridId).append("_sort('DATE_").append(i).append("','").append(type).
                                        append("');\"><img id=\"wfsortDATE_").append(i).
                                        append("\" SRC=\"").append(appPath).
                                        append("/Images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 STYLE=\"display:none\">&nbsp;").
                                        append(name).append("</a></th>");
                                strXMLDate.append("<DATE_").append(i).append("><xsl:value-of select=\"DATE_").
                                        append(i).append("\" />").append("</DATE_").append(i).append(">\n");

                                break;
                             default :*/
                                if(useJqxGrid)
                                    hdrArray.add(new StringBuffer("<a class=\"gridheader\" href=\"javascript:").
                                            append(gridId).append("_sort('").append(sColNameAlt).append("','").append(type).
                                            append("');\"><img id=\"wfsort").append(sColNameAlt).append("\" SRC=\"").append(corePath).
                                            append("/images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 CLASS=\"dti-hide\">&nbsp;").
                                            append(name).append("</a>").toString());
                                else
                                    hdrArray.add(new StringBuffer("<a class=\"gridheader\" href=\"javascript:").
                                            append(gridId).append("_sort('").append(sColNameAlt).append("','").append(type).
                                            append("');\"><img id=\"wfsort").append(sColNameAlt).append("\" SRC=\"").append(corePath).
                                            append("/images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 STYLE=\"display:none\">&nbsp;").
                                            append(name).append("</a>").toString());
                                sHdr.append("<th id=\"H").append(sColNameAlt).append("\"") ;;
                                if(style!=null)
                                    sHdr.append(" STYLE=\"").append(style).append("\"");
                                    if(useJqxGrid)
                                        sHdr.append("> <a class=\"gridheader\" href=\"javascript:").
                                                append(gridId).append("_sort('").append(sColNameAlt).append("','").append(type).
                                                append("');\"><img id=\"wfsort").append(sColNameAlt).
                                                append("\" class=\"dti-hide\" SRC=\"").append(corePath).
                                                append("/images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 CLASS=\"dti-hide\">&nbsp;").
                                                append(name).append("</a></th>");
                                    else
                                        sHdr.append("> <a class=\"gridheader\" href=\"javascript:").
                                                append(gridId).append("_sort('").append(sColNameAlt).append("','").append(type).
                                                append("');\"><img id=\"wfsort").append(sColNameAlt).
                                                append("\" SRC=\"").append(corePath).
                                                append("/images/asc.gif\" WIDTH=7 HEIGHT=7 BORDER=0 STYLE=\"display:none\">&nbsp;").
                                                append(name).append("</a></th>");
  /*                              break;
                        }*/
                    }
                }
                else {
                    if (name == null) {
                        hdrArray.add(new StringBuffer("<font class=\"bodystyle boldwhite\">").
                                append(sColName).append("</font>").toString());
                        sHdr.append("<th id=\"H").append(sColNameAlt).append("\"") ;;
                        if(style!=null)
                            sHdr.append(" STYLE=\"").append(style).append("\"");
                        sHdr.append("> <font class=\"bodystyle boldwhite\">").
                                append(sColName).append("</font></th>");
                    }
                    else {
                        hdrArray.add(new StringBuffer("<font class=\"bodystyle boldwhite\">").
                                append(name).append("</font>").toString());
                        sHdr.append("<th id=\"H").append(sColNameAlt).append("\"") ;;
                        if(style!=null)
                            sHdr.append(" STYLE=\"").append(style).append("\"");
                        sHdr.append("> <font class=\"bodystyle boldwhite\">").
                                append(name).append("</font>");
                    }
                }
                //End Of Add Visible Columns to hdrArray, append to sHdr (StringBuffer for grid header)

                headerMap.put(XMLGridHeader.CN_NAME, sColName);
                switch (type) {
                    case XMLGridHeader.TYPE_TEXT:
                    case XMLGridHeader.TYPE_UPPERCASE_TEXT:
                    case XMLGridHeader.TYPE_LOWERCASE_TEXT:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                            length, align, null, maxLength,
                            " onchange=rowchange(this) onfocus=\"callHrefOnRow(this);this.select();\" ",
                            sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_PHONE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append("<INPUT type='hidden' name='txt" + sColName + "' datafld='" + sColName + "' datasrc='#" + gridId + "1'>");
                        strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                                length, align, GridHelper.LOCAL_PHONE_NUMBER_TYPE, maxLength,
                                "onkeydown=\"baseOnKeyDown('PH');\" onkeyup=\"baseOnKeyUp('PH');\" onkeypress=\"baseOnKeyPress('PH');\"onblur=\"baseOnBlur('PH');\" datatype=\"PH\" onchange=rowchange(this) onfocus=\"callHrefOnRow(this);this.select();\" ",
                                sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_TEXTAREA:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append(GridHelper.createTextArea(title, new StringBuffer("txt").append(sColName).toString(),
                                rows, cols, align, null,
                                " onchange=rowchange(this) onfocus=\"callHrefOnRow(this);this.select();\" ",
                                sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append("<td align='").append(align).append("'");
                        if (!isMasked)
                            strCol.append("><div id=\"").append(sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" datafld=\"").append(sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" ");
                        else
                            strCol.append("><div id=\"").append(sColName).append("\" ");

                        if (style != null)
                            strCol.append("STYLE=\"").append(style).append("\" ");

                        if (!isMasked)
                            strCol.append("></div></td>\n");
                        else
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");

                        break;
                    case XMLGridHeader.TYPE_UPDATEONLYDATE:
                    case XMLGridHeader.TYPE_FORMATDATE:
                    case XMLGridHeader.TYPE_FORMATDATETIME:
                    case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append("<td align='").append(align).append("'");
                        if (!isMasked) {
                            if(FormatUtils.isDateFormatUS())
                                strCol.append("><div id=\"").append(sColName).append("\" datafld=\"").append(sColName).append("\" ");
                            else
                                strCol.append("><div id=\"").append(sColName+FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" datafld=\"").append(sColName+FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" ");
                        }
                        else {
                                strCol.append("><div id=\"").append(sColName).append("\" ");
                        }
                        if(style!=null)
                            strCol.append("STYLE=\"").append(style).append("\" ");
                        if (!isMasked)
                            strCol.append("></div></td>\n");
                        else
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append("<td align='").append(align).append("'");
                        if (!isMasked){
                            if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                                strCol.append("><div id=\"").append(sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" datafld=\"").append(sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\" ");
                                strCol.append(" dispType=\"FORMATTEDNUMBER\"").append(" formatPattern=\""+pattern+"\"");
                            } else
                                strCol.append("><div id=\"").append(sColName).append("\" datafld=\"").append(sColName).append("\" ");
                        } else
                            strCol.append("><div id=\"").append(sColName).append("\" ");
                        if(style!=null)
                            strCol.append("STYLE=\"").append(style).append("\" ");
                        if (!isMasked){
                            strCol.append("></div></td>\n");
                            if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER){
                                strCol.append("<script type='text/javascript'>\n");
                                strCol.append("jQuery(function($) {\n");
                                strCol.append("\tsetNumberColorInGrid(").append("$('div[id=\"").append(sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\"]'), ");
                                strCol.append("'").append(pattern).append("');\n");
                                strCol.append("});\n");
                                strCol.append("</script>\n");
                            }                           
                        } else
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLY:
                    //case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                    case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                    case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append("<td align='").append(align).append("'");
                        if (!isMasked)
                            strCol.append("><div id=\"").append(sColName).append("\" datafld=\"").append(sColName).append("\" ");
                        else
                            strCol.append("><div id=\"").append(sColName).append("\" ");
                        if(style!=null)
                            strCol.append("STYLE=\"").append(style).append("\" ");
                        if (!isMasked)
                            strCol.append("></div></td>\n");
                        else
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN:
                    case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                        updateCols.add(new Integer(xmlColumnIdx));
                        if (isMasked) {
                            strCol.append("<td align='").append(align).append("'");
                            strCol.append("><div id=\"").append(sColName).append("LOVLABEL\" ");
                            if (style != null)
                                strCol.append("STYLE=\"").append(style).append("\" ");
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                        }
                        else if (display == XMLGridHeader.DISPLAY_DEFAULT) {
                            // display type is Text. So, display the code in the grid instead of lov description - fix for issue 181684
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "", "sColName:" + sColName + " - Display type for grid header field is Text, but the linked form field is Select. Show <<Code>> value in Grid, <<Description>> value in form field.");
                            }
                            strCol.append("<td align='").append(align).append("'") ;
                            strCol.append("><div id=\"").append(sColName).append("\" datafld=\"").append(sColName).append("\" ");
                            if(style!=null)
                                strCol.append("STYLE=\"").append(style).append("\" ");
                            strCol.append("></div></td>\n");
                        } else if (StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                            strCol.append("<td align='").append(align).append("'");
                            strCol.append("><div id=\"").append(sColName).append("LOVLABEL\" datafld=\"").append(sColName).append("LOVLABEL\" ");
                            if (style != null)
                                strCol.append("STYLE=\"").append(style).append("\" ");
                            strCol.append("></div></td>\n");
                        }
                        else {
                            strCol.append("<td align='").append(align).
                                append("'><a class=\"gridcontent\" id=\"URL_").
                                append(sColName).append("LOVLABEL").append("\" onclick=\"callHrefOnRow(this);\" datafld=\"URL_").
                                append(xmlColumnIdx).append("\" dataformatas=\"html\"><span id=\"").
                                append(sColName).append("LOVLABEL\" datafld=\"").append(sColName).append("LOVLABEL\" ");
                            if (style != null)
                                strCol.append("STYLE=\"").append(style).append("\" ");
                            strCol.append("></span></a></td>\n");
                        }
                        break;
                    case XMLGridHeader.TYPE_DATE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        if (length != null && length.equals("-")) {
                            length = "";
                            headerMap.put(XMLGridHeader.CN_LENGTH, length);
                        }
                        String dblClick = " ondblclick=\"openCalendarInGrid('txt" + sColName + "')\"";
                        if (!FormatUtils.isDateFormatUS()) {
                            dblClick = " ondblclick=\"openCalendarInGrid('txt" + sColName+FormatUtils.DISPLAY_FIELD_EXTENTION + "')\"";
                            strCol.append("<INPUT type='hidden' name='txt" + sColName + "' datafld='" + sColName + "' datasrc='#" + gridId + "1'>");
                        }
                        strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                                length, align, GridHelper.DATE_TYPE, maxLength,
                                dblClick + " onchange=\"javascript:if ( datemaskclear() ) rowchange(this); else {event.returnValue = false; this.focus();this.select();}\" onblur=\"datemaskclear();\" onclick=\"javascript:datemask()\" onfocus=\"callHrefOnRow(this);this.select()\" onkeydown=\"javascript:XMLdateformat();\" ",
                                sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_NUMBER:
                        updateCols.add(new Integer(xmlColumnIdx));
                        if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER){
                            strCol.append("<INPUT type='hidden' name='txt" + sColName + "' datafld='" + sColName + "' datasrc='#" + gridId + "1'>");
                            strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                                length, align, GridHelper.NUM_TYPE_FORMATTED, maxLength, " dispType=\"FORMATTEDNUMBER\" formatPattern=\""+pattern+"\""+
                                " onchange=rowchange(this) onfocus=\"callHrefOnRow(this);this.select();\" onkeydown=\"javascript:XMLnumformat();\" ",
                                sColName));
                        }else{
                            strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                                length, align, GridHelper.NUM_TYPE, maxLength,
                                " onchange=rowchange(this) onfocus=\"callHrefOnRow(this);this.select();\" onkeydown=\"javascript:XMLnumformat();\" ",
                                sColName));
                        }
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER){
                            sColReq.append("jQuery(function($) {\n");
                            sColReq.append("\tsetNumberColorInGrid(").append("$('input[name=\"").append("txt"+sColName + FormatUtils.DISPLAY_FIELD_EXTENTION).append("\"]'), ");
                            sColReq.append("'").append(pattern).append("');\n");
                            sColReq.append("\t$('input[name=").append("\"").append("txt"+sColName+FormatUtils.DISPLAY_FIELD_EXTENTION).append("\"]').on('focus', function () {\n");
                            sColReq.append("\t\tunformatNumberFormatted($(this),").append("\"").append(pattern).append("\");\n");
                            sColReq.append("\t\t$(this).select();\n");                            
                            sColReq.append("\t});\n");
                            sColReq.append("\t$('input[name=").append("\"").append("txt"+sColName+FormatUtils.DISPLAY_FIELD_EXTENTION).append("\"]').on('blur', function () {\n");
                            sColReq.append("\t\tsetAllNumbersColorFields();\n");
                            sColReq.append("\t\tformatNumberFormatted($(this),").append("\"").append(pattern).append("\");\n");
                            sColReq.append("\t});\n");
                            sColReq.append("});\n");
                        }
                        break;
                    case XMLGridHeader.TYPE_CHECKBOX:
                        updateCols.add(new Integer(xmlColumnIdx));
                        String datafld = sColName;
                        if (datafld.startsWith("CSELECT_IND")) {
                            datafld = "CSELECT_IND";
                        }
                        strCol.append(GridHelper.createCheckBox(title, new StringBuffer("chk").append(sColName).toString(),
                                false, " onclick=\"callHrefOnRow(this); rowchange(this); \"", datafld));
                        sColReq.append(" var REQ_chk").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_RADIOBUTTON:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append(GridHelper.createRadio(title, new StringBuffer("rb").append(sColName).toString(),
                                " onclick=rowchange(this)", sColName));
                        sColReq.append(" var REQ_rb").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_DROPDOWN:
                    case XMLGridHeader.TYPE_MULTIPLE_DROPDOWN://TODO: Edit multiple select in Grid
                        updateCols.add(new Integer(xmlColumnIdx));
                        // get lov
                        ArrayList lovData = (ArrayList) headerMap.get(XMLGridHeader.CN_LISTDATA);
                        // if lov is null and we have a fieldId, look it up
                        if (lovData == null && fieldId != null)
                            lovData = (ArrayList) util.lookup(pageContext, fieldId + "LOV", null);
                        strCol.append(GridHelper.loadCombo(title, new StringBuffer("cbo").append(sColName).toString(),
                                lovData, "-1", " onchange=rowchange(this) onfocus=\"callHrefOnRow(this);\" ", sColName));
                        sColReq.append(" var REQ_cbo").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_CHECKBOXREAD:
                        strCol.append(GridHelper.createCheckBox(title, new StringBuffer("chk").append(sColName).toString(),
                                true, null, sColName));
                        sColReq.append(" var REQ_chk").append(sColName).append("=false;\n");
                        break;
                    case XMLGridHeader.TYPE_IMG:
                        strCol.append("<td align='").append(align).append("'><img id=\"").
                                append(sColName).append("\" datafld=\"").append(sColName).
                                append("\" onclick=\"callHrefOnRow(this);\" border=\"0\"></td>\n");
                        break;
                    case XMLGridHeader.TYPE_URL:
                    case XMLGridHeader.TYPE_UPDATEONLYURL:
                        strCol.append("<td align='").append(align);
                        if (!isMasked) {
                            strCol.append("'><a class=\"gridcontent\" id=\"URL_").
                                append(sColName).append("\" onclick=\"callHrefOnRow(this);\" datafld=\"URL_").
                                append(xmlColumnIdx).append("\" dataformatas=\"html\"><span id=\"").
                                append(sColName).append("\" datafld=\"").append(sColName).
                                append("\"></span></a></td>\n");
                        if (type == XMLGridHeader.TYPE_UPDATEONLYURL)
                            updateCols.add(new Integer(xmlColumnIdx));
                        } else {
                            strCol.append("'><div id=\"").append(sColName).append("\" ");
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                        }
                        break;
                    case XMLGridHeader.TYPE_FORMATMONEY:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                                length, align, null, maxLength,
                                " onkeypress=baseOnKeyPress('CF') onkeydown=baseOnKeyDown('CF') onblur=baseOnBlur('CF') " +
                                        "datatype=\"CF\" onchange=\"rowchange(this); baseOnChange('CF');\" " +
                                        "onfocus=\"callHrefOnRow(this);this.select();baseOnFocus('CF');\" ",
                                sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");

                        break;
                    case XMLGridHeader.TYPE_PERCENTAGE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        strCol.append(GridHelper.createText(title, new StringBuffer("txt").append(sColName).toString(),
                               length, align, null, maxLength,
                               " onkeypress=baseOnKeyPress('PT') onkeydown=baseOnKeyDown('PT') onblur=baseOnBlur('PT') " +
                                       "datatype=\"PT\" onchange=rowchange(this) " +
                                       "onfocus=\"callHrefOnRow(this);this.select();baseOnFocus('PT');\" ",
                               sColName));
                        sColReq.append(" var REQ_txt").append(sColName).append("=false;\n");
                        break;

                    default :
                        strCol.append("<td align='").append(align).append("'") ;
                        if (!isMasked)
                            strCol.append("><div id=\"").append(sColName).append("\" datafld=\"").append(sColName).append("\" ");
                        else
                            strCol.append("><div id=\"").append(sColName).append("\" ");
                        if(style!=null)
                            strCol.append("STYLE=\"").append(style).append("\" ");
                        if (!isMasked)
                            strCol.append("></div></td>\n");
                        else
                            strCol.append(">").append(FormatUtils.getFieldMask()).append("</div></td>\n");
                }
                
            } //End of Visible Columns
            else {	// hidden
                if(headerMap.get(XMLGridHeader.CN_FIELDNAME)!= null){
                    String fieldName = headerMap.get(XMLGridHeader.CN_FIELDNAME).toString();
                    String gridHeaderSuffix = GridHelper.getGridHeaderOasisFieldNameSuffix();
                    if(fieldName.endsWith(gridHeaderSuffix)) {
                        hiddenColumnNames.append(name.replaceAll("&nbsp;", "%20").replaceAll(",", ":;:")).append(",");
                        hiddenColumnIds.append(sColName).append(",");
                    }
                }
                switch (type) {
                    case XMLGridHeader.TYPE_ANCHOR:
                        bAnchor = true;
                        anchorfieldname = sColName;
                        strCol.append("<a datasrc=\"#").append(gridId).append("1\" datafld=\"").
                                append(sColName).append("\">\n");
                        strCol.append("<input name=\"CROWID\" type=\"hidden\" datafld=\"ID\">\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLY:
                    case XMLGridHeader.TYPE_UPDATEONLYDATE:
                    case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                    case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                    case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                    case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                    case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                        updateCols.add(new Integer(xmlColumnIdx));
                        if(useJqxGrid)
                            strCol.append("<div id=\"").append(sColName).append("\" datafld=\"").
                                    append(sColName).append("\" class=\"dti-hide\"></div>\n");
                        else
                            strCol.append("<div id=\"").append(sColName).append("\" datafld=\"").
                                    append(sColName).append("\" STYLE=\"display:none\"></div>\n");
                        break;
                    case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN:
                    case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN:
                        updateCols.add(new Integer(xmlColumnIdx));
                        if(useJqxGrid)
                            strCol.append("<div id=\"").append(sColName).append("LOVLABEL\" datafld=\"").
                                    append(sColName).append("LOVLABEL\" class=\"dti-hide\"></div>\n");
                        else
                            strCol.append("<div id=\"").append(sColName).append("LOVLABEL\" datafld=\"").
                                    append(sColName).append("LOVLABEL\" STYLE=\"display:none\"></div>\n");
                        break;

                } //End Of Hidden

                headerMap.put(XMLGridHeader.CN_NAME, sColName);
            }
        }
        sColReq.append("</script>\n");
        util.write(pageContext, strXMLDate.toString());
        util.write(pageContext, strXMLURL.toString());
        util.write(pageContext, "<UPDATE_IND><xsl:value-of select=\"UPDATE_IND\"/></UPDATE_IND>\n");
        util.write(pageContext, "<DISPLAY_IND><xsl:value-of select=\"DISPLAY_IND\"/></DISPLAY_IND>\n");
        util.write(pageContext, "<EDIT_IND><xsl:value-of select=\"EDIT_IND\"/></EDIT_IND>\n");
        util.write(pageContext, "<OBR_ENFORCED_RESULT><xsl:value-of select=\"OBR_ENFORCED_RESULT\"/></OBR_ENFORCED_RESULT>\n");
        util.write(pageContext, "</ROW>\n");
        util.write(pageContext,"</xsl:for-each></ROWS></xsl:template></xsl:stylesheet></XML>\n");
        //End Of Stylesheet
        
        if (updateCols.size() > 0) {        //Writes col="1,2....
            Iterator iter = updateCols.iterator();
            String sep = "";
            while (iter.hasNext()) {
                Integer updateCol = (Integer) iter.next();
                sUpdtCol.append(sep).append(updateCol);
                sep = ",";
            }
        }

        StringBuffer strXML = new StringBuffer("<XML id=\"").append(gridId).append("1\" xml:space=\"preserve\" xmlns:dataType=").
                append("\"urn:schemas-microsoft-com:datatypes\" ");
        if ((data.getRowCount() == 0 || !data.hasNonDeletedRows()) && gridInsert) {
            data.addEmptyRow();
            strXML.append("empty=\"true\" ");
        }
        else
            strXML.append("empty=\"false\" ");

        if(isCacheResultSet==true) {
            String cacheKey = getxmldata(sUpdtCol.toString(), isCacheResultSet); 

            //strXML.append("src=\"http://wsnjpc172-mlm:7001/m1/PM/riskmgr/riskdata.xml\" ");
            //strXML.append("src=\"" + "http://wsnjpc172-mlm:7001/m1/PM/" + "getOasisGridData.do?process=loadOasisGridData&key=" + cacheKey + "\" ");
            strXML.append("src=\"" + ((HttpServletRequest) pageContext.getRequest()).getContextPath() + "/getOasisGridData.do?process=loadOasisGridData&gridId=" + gridId + "&key=" + cacheKey + "\" ");
            strXML.append("ondatasetcomplete=\"");
            strXML.append(gridId+"_initXMLIsland();\" ");
        }
        strXML.append(">");

/*        if(isCacheResultSet==true) {
            strXML.append("<?xml-stylesheet type=\"text/xsl\" href=\"").append(gridId).append("1XSL\"").append("?>" + "\n") ;
        }*/

        util.write(pageContext, strXML.append('\n').toString());

        if(isCacheResultSet==false) {
            getxmldata(sUpdtCol.toString(), isCacheResultSet);
        }

        util.write(pageContext, "</XML>\n");
        util.write(pageContext,
            "<XML id=\"XMLTemplate\" xml:space=\"preserve\" xmlns:dataType=\"urn:schemas-microsoft-com:datatypes\" empty=\"false\">\n" +
            "    <ROWS>\n" +
            "        <ROW id=\"-9999\">\n" +
            "            <UPDATE_IND>N</UPDATE_IND>\n" +
            "            <DISPLAY_IND>N</DISPLAY_IND>\n" +
            "            <EDIT_IND>N</EDIT_IND>\n" +
            "            <OBR_ENFORCED_RESULT></OBR_ENFORCED_RESULT>\n" +
            "        </ROW>\n" +
            "    </ROWS>\n" +
            "</XML>");

        strCol.append((bAnchor) ? "</a></tr>" : "</tr>");

        if(isCacheResultSet==true) {

            StringBuffer xmlIslandStateCheck = new StringBuffer("");
            xmlIslandStateCheck.append("function ").
                                append(gridId+"_initXMLIsland() {\n").
                                append("    ").append("if (").append(gridId).append("1.readyState=='complete') {" + "\n").
                                append("         if (" + gridId + ".getAttribute('InitXMLIslandIsInitialized')) { \n").
                                append("             return;\n").
                                append("         } else {\n").
                                append("            var InitXMLIslandIsInitializedAttribute = document.createAttribute('InitXMLIslandIsInitialized');  \n").
                                append("            InitXMLIslandIsInitializedAttribute.nodeValue = \"true\" \n").
                                append("            " + gridId + ".setAttributeNode(InitXMLIslandIsInitializedAttribute); \n").
                                append("         }\n").
                                append("         dti.oasis.grid.setProperty(\"").append(gridId).append("\", \"loadingDeferredObj\", $.Deferred());\n").
                                append("         dti.oasis.grid.setProperty(\"").append(gridId).append("\", \"sortingDeferredObj\", $.Deferred().resolve());\n").
                                append("         ").append(gridId).append("1StyleSheet = ").append(gridId).append("1XSL.documentElement; \n").
                                append("         if(!isMultiGridSupported) {\n").
                                append("             xmlSource = ").append(gridId).append("1.documentElement; \n").
                                append("         }\n").
                                append("         var gridDetailDivIdAttribute = document.createAttribute('gridDetailDivId');  \n").
                                append("         gridDetailDivIdAttribute.nodeValue = \"" + gridDetailDivId +  "\" \n").
                                append("         " + gridId + ".setAttributeNode(gridDetailDivIdAttribute); \n").
                                append("          setTable(").append(gridId).append(",").append(gridId).append("1); \n").
                                append("         orig").append(gridId).append("1=").append(gridId).append("1.cloneNode(true);\n").
                                append("         filter(").append(gridId).append("1StyleSheet, orig").append(gridId).append("1,").append(gridId).append("1,'');\n").
                                append("         $('#formFieldsTableForpanelContentIdFor").append(gridDetailDivId).append("').removeClass('cachedGridFormFieldsHidden');\n").
                                append("         if(isMultiGridSupported) {\n").
                                append("             setTableProperty(").append(gridId).append(", 'updateablecolset', '\"").append(sUpdtCol).append("\"'); \n").
                                append("             setTableProperty(").append(gridId).append(", 'anchorfieldname', '\"").append(anchorfieldname).append("\"'); \n").
                                append("         } else {\n").
                                append("             updateablecolset = '").append(sUpdtCol).append("'; \n").
                                append("	         anchorfieldname = '").append(anchorfieldname).append("'; \n").
                                append("         } \n");
            if (data.getRowCount() == 0) {
                xmlIslandStateCheck.append("         hideEmptyTable(").append(gridId).append(");\n");
            }

            if (isGridInsert() && data.getRowCount() == 0) {
                xmlIslandStateCheck.append("        ").append(gridId).append("1.recordset.movefirst();\n").
                                    append(gridId).append("1.recordset(\"UPDATE_IND\").value = \"D\";\n");
            }
            xmlIslandStateCheck.append("   }\n");
            xmlIslandStateCheck.append("}\n");

            util.write(pageContext, new StringBuffer("<script type='text/javascript'>\n\n").
                    append(xmlIslandStateCheck.toString()).
                    append("</script>\n").toString());
        }
//This javascript is moved after section that generates javascript for grid handling
//Uses map for storing the values for updateablecolset and anchorfieldname variables.
/*
        else {
            util.write(pageContext, new StringBuffer("<script type='text/javascript'>\n").
                    append(" updateablecolset = '").append(sUpdtCol).append("'; \n").
                    append("	anchorfieldname = '").append(anchorfieldname).
                    append("'; \n").append("</script>\n").toString());
        }
*/
        HashMap map = new HashMap();
        map.put("cols", strCol);
        map.put("header", sHdr.toString());
        map.put("xml", strXML);
        map.put("headerArray", hdrArray);
        map.put("req", sColReq.toString());
        map.put("colDataTypes", sColDataTypes.toString());
        map.put("colNames", sColNames.toString());
        map.put("hiddenColumnNames", hiddenColumnNames.toString());
        String hiddenColumnIdString = hiddenColumnIds.toString();
        if (hiddenColumnIdString.length() > 0) {
            hiddenColumnIdString = hiddenColumnIdString.substring(0, hiddenColumnIds.length() - 1);
        }
        map.put("hiddenColumnIds", hiddenColumnIdString);
        if(!isCacheResultSet) {
            map.put("updateablecolset", sUpdtCol);
            map.put("anchorfieldname", anchorfieldname);
        }
        map.put("gridDetailDivId", (gridDetailDivId==null ? "" : gridDetailDivId));
        l.exiting(getClass().getName(), "resultSetToXml", map);
        return map;
    }

    private String getDataIslandColumnName(int dataColumnIndex) {
        String sColName;
        sColName = new StringBuffer("C").append(data.getColumnName(dataColumnIndex).trim().toUpperCase().replace(']', ' ').trim().
                replace('[', ' ').trim().replace(' ', '_').replace('#', 'N').replace('/', ' ').trim().
                replace('\'', '_')).toString();
        return sColName;
    }

    /**
     * @param sUpdtCol
     * @throws JspException
     * @throws ParseException
     */
    private String getxmldata(String sUpdtCol, boolean isCacheData) throws JspException, ParseException {
        if (l.isLoggable(Level.FINER)) {
            l.entering(getClass().getName(), "getxmldata", new Object[]{sUpdtCol});
        }
        String cacheKey = "";
        if (isCacheData) {
            if (data instanceof BaseResultSetRecordSetAdaptor) {
                String sessionId = pageContext.getSession().getId();
                HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

                OasisGridCacheManager ogCacheMgr = new OasisGridCacheManager();
                GridData gridData = new GridData(gridId, data);
                gridData.setUpdateColumns(sUpdtCol);

                // Add the requried attributes
                String gridHeaderBeanName =  gridId + "HeaderBean";
                gridData.setAttribute(gridHeaderBeanName, header);
                Enumeration<String> attributeNames = request.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String s = attributeNames.nextElement();
                    if (s.endsWith("LOV")) {
                        gridData.setAttribute(s, request.getAttribute(s));
                    }
                }

                cacheKey = ogCacheMgr.putData(sessionId, gridData);
                return cacheKey;
            }
            else {
                l.logp(Level.INFO, getClass().getName(), "getxmldata", "data is not a RecordSet; must marshall data to XML synchronously");
            }
        }
        StringBuffer strXML;
        StringBuffer strXMLDate;
        String strTab = "  ";
        boolean bDateInd, bDateTimeInd, bUrlInd, isDataPresent;

        int colCount = data.getColumnCount();
        TagUtils util = TagUtils.getInstance();

        StringBuffer xmlData = null;
        if(isCacheData==false) {
            util.write(pageContext, "<ROWS>\n");
        } else {
            xmlData = new StringBuffer("");
            xmlData.append("<ROWS>\n");
        }
        data.beforeFirst();
        // Start loop through rows
        int rowIndex = -1;
        while (data.next()) {
            // row header
            strXML = new StringBuffer("\n<ROW id=\"").
                    append( (StringUtils.isBlank(data.getString(header.getDataColumnIndexForAnchorColumn())) ? "-9999" : data.getString(header.getDataColumnIndexForAnchorColumn())) ).
                    append("\" index=\"").append(++rowIndex).
                    append("\" col=\"").append(sUpdtCol).append("\" >\n");
            strXMLDate = new StringBuffer();

            // Start loop through columns
            int headerIdx = 0;
            for (int i = 1; i <= colCount; i++) {
                String dataColumnName = data.getColumnName(i);

                // skip xxxLOVLABEL columns
                if (dataColumnName.endsWith("LOVLABEL"))
                    continue;

                // skip xxx_FORMATTED columns
                if (dataColumnName.endsWith(FormatUtils.DISPLAY_FIELD_EXTENTION))
                    continue;

                bDateInd = false;
                bDateTimeInd = false;
                bUrlInd = false;

                headerIdx = header.getHeaderIndex(data.getColumnName(i)).intValue();
                String dataItem = data.getString(dataColumnName, "");
                isDataPresent = (dataItem != null && dataItem.trim().length() > 0);

                int xmlColumnIdx = header.getXmlColumnIndex(dataColumnName);

                HashMap headerMap = header.getHeaderMap(headerIdx);
                int type = ((Integer) headerMap.get(XMLGridHeader.CN_TYPE)).intValue();
                Integer iD = (Integer) headerMap.get(XMLGridHeader.CN_DISPLAY);
                int display = (iD == null) ? 0 : iD.intValue();
                String name = (String) headerMap.get(XMLGridHeader.CN_NAME);
                ArrayList lov = (ArrayList) headerMap.get(XMLGridHeader.CN_LISTDATA);
                String fieldId = (String) headerMap.get(XMLGridHeader.CN_FIELDID);
                // if lov is null and we have a fieldId, look it up
                if (lov == null && fieldId != null)
                    lov = (ArrayList) util.lookup(pageContext, fieldId + "LOV", null);
                if (lov == null && headerMap.get(XMLGridHeader.CN_DETAIL_FIELDID) != null) {
                    String detailFieldId = (String) headerMap.get(XMLGridHeader.CN_DETAIL_FIELDID);
                    if (!StringUtils.isBlank(detailFieldId)) {
                        lov = (ArrayList) util.lookup(pageContext, detailFieldId + "LOV", null);
                    }
                }
                String iDec = (String) headerMap.get(XMLGridHeader.CN_DECIMALPLACES);
                boolean isProtected = ((Boolean) headerMap.get(XMLGridHeader.CN_PROTECTED)).booleanValue();

                // If this field is protected, set the data item to null as it should not appear in the xml
                if (isProtected)
                    dataItem = "";
                if (!StringUtils.isBlank(iDec))
                    if (!FormatUtils.isLong(iDec))
                        iDec = null;

                //Pattern
                String pattern = (String) headerMap.get(XMLGridHeader.CN_PATTERN);
                if(pattern==null)
                    pattern = "";
                // tag start
                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).
                        append(strClose);

                boolean isFieldMasked = (boolean)headerMap.get(XMLGridHeader.CN_MASKED);

                // Start visible items
                if (!headerMap.get(XMLGridHeader.CN_VISIBLE).equals("N")) {
                    switch (type) {
                        //Percentage field type
                        case XMLGridHeader.TYPE_PERCENTAGE:
                        case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatPercentage(dataItem));
                            else
                                strXML.append(FormatUtils.formatPercentage(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_NUMBER:
                        case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                            if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                                strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(FormatUtils.formatNumber(dataItem,pattern));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            } else {
                                if (isDataPresent) {
                                        strXML.append(ResponseUtils.filter(dataItem));
                                }                                
                            }
                            break;
                        case XMLGridHeader.TYPE_PHONE:
                        case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                            strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                            strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                            strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                append(strClose);
                            strXML.append(FormatUtils.formatPhoneNumberForDisplay(dataItem));
                            name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            break;
                        case XMLGridHeader.TYPE_FORMATMONEY:
                        case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatCurrency(dataItem));
                            else
                                strXML.append(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_FORMATDATE:
                        case XMLGridHeader.TYPE_DATE:
                        case XMLGridHeader.TYPE_UPDATEONLYDATE: //TODO: Figure out conditions
                            java.util.Date dte = null;
                            if(isFieldMasked) {
                                strXML.append(dataItem);
                            }else{
                                dte = data.getDate(i);
                                strXML.append(OasisTagHelper.formatDateAsXml(dte));
                            }

                            if(!FormatUtils.isDateFormatUS()) {
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                   append(strClose);
                                if(isFieldMasked) {
                                    strXML.append(dataItem);
                                } else{
                                    strXML.append(OasisTagHelper.formatCustomDateAsXml(dte));
                                }
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            }
                            bDateInd = true;
                            break;
                        case XMLGridHeader.TYPE_FORMATDATETIME:
                        case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                            java.util.Date dtTime = data.getDate(i);
                            strXML.append(OasisTagHelper.formatDateTimeAsXml(dtTime));
                            if(!FormatUtils.isDateFormatUS()) {
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateTimeAsXml(dtTime));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            }
                            bDateTimeInd = true;
                            break;
                        case XMLGridHeader.TYPE_URL:
                        case XMLGridHeader.TYPE_UPDATEONLYURL:
                            bUrlInd = true;
                            if (isDataPresent) {
                                if (display == XMLGridHeader.DISPLAY_MONEY) {
                                    if (iDec == null)
                                        strXML.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem)));
                                    else
                                        strXML.append(ResponseUtils.filter(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec))));
                                }
                                else
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }

                            break;
                        case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');

                                // tag start for LOVLABEL
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValue(lov, dataItem);
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
                                name += "LOVLABEL";
                            }
                            // Populate url column in data island if there's href defined for this drop down list field
                            if (!StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                                bUrlInd = true;
                            }
                            break;
                        case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');

                                // tag start for LOVLABEL
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValues(lov, dataItem.split(","));
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
                                name += "LOVLABEL";
                            }
                            if (!StringUtils.isBlank((String) headerMap.get(XMLGridHeader.CN_FIELD_HREF))) {
                                bUrlInd = true;
                            }
                            break;
                        default :
                            if (isDataPresent) {
                                // if a list of values is present for a readonly field, decode
                                if (type == XMLGridHeader.TYPE_DEFAULT && lov != null)
                                    strXML.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                                else if (!FormatUtils.isDateFormatUS() && FormatUtils.isDate(dataItem))
                                    strXML.append(ResponseUtils.filter(FormatUtils.formatDateForDisplay(dataItem)));
                                else
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }
                            break;
                    }
                }
                // End Visible items
                // Start hidden items
                else {
                    switch (type) {
                        //Percentage field type
                         case XMLGridHeader.TYPE_PERCENTAGE:
                         case XMLGridHeader.TYPE_UPDATEONLYPERCENTAGE:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatPercentage(dataItem));
                            else
                                strXML.append(FormatUtils.formatPercentage(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_NUMBER:
                        case XMLGridHeader.TYPE_UPDATEONLYNUMBER:
                            if(display == XMLGridHeader.DISPLAY_FORMATTED_NUMBER) {
                                strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(FormatUtils.formatNumber(dataItem,pattern));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(dataItem));
                            }
                            break;
                        case XMLGridHeader.TYPE_PHONE:
                        case XMLGridHeader.TYPE_UPDATEONLYPHONE:
                            strXML.append(StringUtils.isBlank(dataItem) ? "" : dataItem);
                            strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                            strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                append(strClose);
                            strXML.append(FormatUtils.formatPhoneNumberForDisplay(dataItem));
                            name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                            break;
                        case XMLGridHeader.TYPE_FORMATMONEY:
                        case XMLGridHeader.TYPE_UPDATEONLYMONEY:
                            if (iDec == null)
                                strXML.append(FormatUtils.formatCurrency(dataItem));
                            else
                                strXML.append(FormatUtils.formatCurrency(dataItem, Integer.parseInt(iDec)));
                            break;
                        case XMLGridHeader.TYPE_ANCHOR :
                            if (isDataPresent)
                                strXML.append("javascript:selectRowWithProcessingDlg('").append(gridId).
                                    append("','").append(ResponseUtils.filter(dataItem).
                                    replaceAll("'", "''")).append("');");
                            else
                                strXML.append("javascript:selectRowWithProcessingDlg('").append(gridId).
                                    append("','-');");
                            break;
                        case XMLGridHeader.TYPE_FORMATDATE:
                        case XMLGridHeader.TYPE_DATE:
                        case XMLGridHeader.TYPE_UPDATEONLYDATE:
                            if(!FormatUtils.isDateFormatUS()) {
                                java.util.Date dte = data.getDate(i);
                                strXML.append(OasisTagHelper.formatDateAsXml(dte));
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateAsXml(dte));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                bDateInd = true;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(dataItem));   
                            }
                            break;
                        case XMLGridHeader.TYPE_FORMATDATETIME:
                        case XMLGridHeader.TYPE_UPDATEONLYDATETIME:
                            if(!FormatUtils.isDateFormatUS()) {
                                java.util.Date dte = data.getDate(i);
                                strXML.append(OasisTagHelper.formatDateTimeAsXml(dte));
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append(FormatUtils.DISPLAY_FIELD_EXTENTION).
                                    append(strClose);
                                strXML.append(OasisTagHelper.formatCustomDateTimeAsXml(dte));
                                name += FormatUtils.DISPLAY_FIELD_EXTENTION;
                                bDateInd = true;
                            } else {
                                if (isDataPresent)
                                    strXML.append(ResponseUtils.filter(FormatUtils.formatDateTime(data.getDate(i))));
                            }
                            break;
                        case XMLGridHeader.TYPE_UPDATEONLYDROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');

                                // tag start for LOVLABEL
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValue(lov, dataItem);
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
//                                strXML.append(ResponseUtils.filter(CollectionUtils.getDecodedValue(lov, dataItem)));
                                name += "LOVLABEL";
                            }
                            break;
                        case XMLGridHeader.TYPE_UPDATEONLY_MULTIPLE_DROPDOWN :
                            if (isDataPresent) {
                                strXML.append(ResponseUtils.filter(dataItem));
                            }
                            if (OasisTagHelper.displayReadonlyCodeLookupAsLabel() && lov != null) {
                                // closing tag for code
                                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');

                                // tag start for LOVLABEL
                                strXML.append(strTab).append(strOpen).append(name.trim().toUpperCase()).append("LOVLABEL").
                                        append(strClose);
                                String decodedValue = CollectionUtils.getDecodedValues(lov, dataItem.split(","));
                                if (CodeLookupManager.getInstance().getSelectOptionLabel().equals(decodedValue)) {
                                    decodedValue = "";
                                }
                                strXML.append(ResponseUtils.filter(decodedValue));
                                name += "LOVLABEL";
                            }
                            break;
                        default :
                            if (isDataPresent)
                                strXML.append(ResponseUtils.filter(dataItem));
                            if (l.isLoggable(Level.FINE)) {
                                l.logp(Level.FINE, getClass().getName(), "getxmldata", "dataColumnName = " + dataColumnName);
                            }
                    }
                }
                // End Hidden items
                // Start special tags
                if (bDateInd) {
                    strXMLDate.append("<DATE_").append(xmlColumnIdx).append('>');
                    if (isDataPresent)
                        strXMLDate.append(DateUtils.dateDiff(DateUtils.DD_DAYS,
                                "01/01/1993", data.getDate(i)));
                    strXMLDate.append("</DATE_").append(xmlColumnIdx).append('>');
                }
                else if (bDateTimeInd) {
                    strXMLDate.append("<DATE_").append(xmlColumnIdx).append('>');
                    if (isDataPresent)
                        strXMLDate.append(DateUtils.dateDiff(DateUtils.DD_SECS,
                                "01/01/1993 00:00:00", data.getDate(i)));
                    strXMLDate.append("</DATE_").append(xmlColumnIdx).append('>');
                }
                else if (bUrlInd) {
                    String fieldHref = (String) headerMap.get(XMLGridHeader.CN_FIELD_HREF);
                    //if there is href on field in webwb, use it. Otherwise use the href in xml header file.
                    if (!StringUtils.isBlank(fieldHref)) {
                        strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                        if (isDataPresent)
                            strXMLDate.append("javascript:handleOnGridHref('").append(gridId).append("','")
                                    .append(ResponseUtils.filter(fieldHref.replaceAll("'", "\\\\'")))
                                    .append("');");
                        strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                    } else {
                        String href = (String) headerMap.get(XMLGridHeader.CN_HREF);
                        String hrefKey = (String) headerMap.get(XMLGridHeader.CN_HREFKEY);
                        String hrefKeyName = (String) headerMap.get(XMLGridHeader.CN_HREFKEYNAME);
                        String hrefKeyValue =
                                (hrefKeyName == null ? data.getString(Integer.parseInt(hrefKey)) : data.getString(hrefKeyName));
                        if (href.indexOf("javascript:") >= 0) {
                            strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                            if (isDataPresent)
                                strXMLDate.append(ResponseUtils.filter(href)).append('\'').
                                        append(hrefKeyValue).
                                        append("');");
                            strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                        } else {
                            strXMLDate.append("<URL_").append(xmlColumnIdx).append('>');
                            if (isDataPresent)
                                strXMLDate.append(ResponseUtils.filter(href)).
                                        append(hrefKeyValue);
                            strXMLDate.append("</URL_").append(xmlColumnIdx).append('>');
                        }
                    }
                }
                // End special tags

                // closing tag
                strXML.append(strEndOpen).append(name.trim().toUpperCase()).append(strClose).append('\n');

            }
            // End looping through columns
            strXML.append(strXMLDate).append("\n<UPDATE_IND>").
                    append(data.getUpdateInd()).append("</UPDATE_IND>\n") ;
            strXML.append("<DISPLAY_IND>").append(data.getDisplayInd()).append("</DISPLAY_IND>\n");
            strXML.append("<EDIT_IND>").append(data.getEditInd()).append("</EDIT_IND>\n");
            strXML.append("<OBR_ENFORCED_RESULT>")
                .append(XMLRecordSetMapper.encodeWhenNecessary(data.getOBREnforcedResult()))
                .append("</OBR_ENFORCED_RESULT>\n");
            strXML.append("</ROW>\n");

            if(isCacheData==false) {
                util.write(pageContext, strXML.toString());
            } else {
                xmlData.append(strXML.toString());
            }

        }
        // End looping through rows
        if(isCacheData==false) {
            util.write(pageContext, "</ROWS>\n");
        } else {
            xmlData.append("</ROWS>\n");
            String sessionId = pageContext.getSession().getId();
            OasisGridCacheManager ogCacheMgr = new OasisGridCacheManager();
            cacheKey = ogCacheMgr.putData(sessionId, gridId, xmlData.toString());
        }

        l.exiting(getClass().getName(), "getxmldata");
        return cacheKey;
    }

    /**
     * Returns value to use for alignment of a field.
     *
     * @param alignment
     * @return String alignmentVal
     */
    private String getAlignVal(String alignment) {
        String alignmentVal;

        if ("R".equals(alignment)) {
            alignmentVal = "right";
        }
        else if ("C".equals(alignment)) {
            alignmentVal = "center";
        }
        else {
            alignmentVal = "left";
        }
        return alignmentVal;
    }

    /**
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
        l.entering(getClass().getName(), "release");
        super.release();
        formName = null;
        gridId = null;
        data = null;
        header = null;
        pageSize = null;
        gridPageNav = 0;
        totalDataSets = 0;
        dataSetSize = 0;
        selectable = false;
        showHeader = false;
        gridInsert = false;
        sortable = false;
        sortItem = null;
        sortType = null;
        sortOrder = null;
        gridWidth = null;
        gridHeight = null;
        tableWidth = null;
        cellSpacing = null;
        cellPadding = null;
        l.exiting(getClass().getName(), "release");
    }


    /**
     * @return # of rows per page to display
     */
    public int getPageSize() {
        return StringUtils.isBlank(pageSize) ? 10 : Integer.parseInt(pageSize);
    }

    /**
     * @param i - # rows per page to display
     */
    public void setPageSize(int i) {
        pageSize = String.valueOf(i);
    }

    /**
     * @param string - # rows per page to display or keywords
     */
    public void setPageSize(String string) {
        pageSize = string;
    }

    /**
     * @return current page in grid
     */
    public int getGridPageNav() {
        return gridPageNav;
    }

    /**
     * @param i - current page in grid
     */
    public void setGridPageNav(int i) {
        gridPageNav = i;
    }

    /**
     * @return # of datasets (groups of records from db)
     */
    public int getTotalDataSets() {
        return totalDataSets;
    }

    /**
     * @param i - # of datasets (groups of records from db)
     */
    public void setTotalDataSets(int i) {
        totalDataSets = i;
    }

    /**
     * @return - whether selectall/deselectall buttons are present
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * @param b - whether selectall/deselectall buttons are present
     */
    public void setSelectable(boolean b) {
        selectable = b;
    }

    /**
     * @return - # of records in dataset
     */
    public int getDataSetSize() {
        return dataSetSize;
    }

    /**
     * @param i - # of records in dataset
     */
    public void setDataSetSize(int i) {
        dataSetSize = i;
    }

    /**
     * @return css width of table
     */
    public String getTableWidth() {
        return tableWidth;
    }

    /**
     * @param string - css width of table
     */
    public void setTableWidth(String string) {
        tableWidth = string;
    }

    /**
     * @return show header on grid
     */
    public boolean isShowHeader() {
        return showHeader;
    }

    /**
     * @param b - show header on grid
     */
    public void setShowHeader(boolean b) {
        showHeader = b;
    }

    /**
     * @return css width of grid layer
     */
    public String getGridWidth() {
        return gridWidth;
    }

    /**
     * @param string - css width of grid layer
     */
    public void setGridWidth(String string) {
        gridWidth = string;
    }

    /**
     * @return css height of grid layer
     */
    public String getGridHeight() {
        return gridHeight;
    }

    /**
     * @param string - css height of grid layer
     */
    public void setGridHeight(String string) {
        gridHeight = string;
    }

    /**
     * @return cell padding of grid's html table
     */
    public String getCellPadding() {
        return cellPadding;
    }

    /**
     * @return cell spacing of grid html table
     */
    public String getCellSpacing() {
        return cellSpacing;
    }

    /**
     * @param string - cell padding of grid's html table
     */
    public void setCellPadding(String string) {
        cellPadding = string;
    }

    /**
     * @param string - cell spacing of grid's html table
     */
    public void setCellSpacing(String string) {
        cellSpacing = string;
    }

    /**
     * @return whether grid will be sortable by clicking on col headers
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * @param b - whether grid will be sortable by clicking on col headers
     */
    public void setSortable(boolean b) {
        sortable = b;
    }

    /**
     * @param string -  name of the item to be sorted on
     */
    public void setSortItem(String string) {
        sortItem = string;
    }

    /**
     * @return name of the item to be sorted on
     */
    public String getSortItem() {
        return sortItem;
    }

    /**
     * @param string - value type of sort item
     */
    public void setSortType(String string) {
        sortType = string;
    }

    /**
     * @return value type of sort item
     */
    public String getSortValye() {
        return sortType;
    }

    /**
     * @param string - sort order
     */
    public void setSortOrder(String string) {
        sortOrder = string;
    }

    /**
     * @return sort order
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * @return supporting grid data
     */
    public BaseResultSet getData() {
        return data;
    }

    /**
     * @return name of html form that grid is within
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @return name of grid
     */
    public String getGridId() {
        return gridId;
    }

    /**
     * @return XMLGridHeader object describing columns in grid
     */
    public XMLGridHeader getHeader() {
        return header;
    }

    /**
     * @param string name of html form that grid is withing
     */
    public void setFormName(String string) {
        formName = string;
    }

    /**
     * @param string name of grid
     */
    public void setGridId(String string) {
        gridId = string;
    }

    /**
     * @param header object describing columns in grid
     */
    public void setHeader(XMLGridHeader header) {
        this.header = header;
    }

    /**
     * @param set data to support grid
     */
    public void setData(BaseResultSet set) {
        data = set;
    }

    /**
     * @return whether grid allows row inserts
     */
    public boolean isGridInsert() {
        return gridInsert;
    }

    /**
     * @param b whether grid allows row inserts
     */
    public void setGridInsert(boolean b) {
        gridInsert = b;
    }

    /**
     * @return STRUTS action from which form name can be derived
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName STRUTS action from which form name can be derived
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Getter, if true, the <, <<, >, >> links will navigate through pages
     * on the databound html table
     *
     * @return true/false
     */
    public boolean isNavPages() {
        return navPages;
    }

    /**
     * Setter, if true, the <, <<, >, >> links will navigate through pages
     * on the databound html table
     *
     * @param navPages
     */
    public void setNavPages(boolean navPages) {
        this.navPages = navPages;
    }

    /**
     * getter, if true and navPages is false, the <, <<, >, >> links will
     * navigate through data sets. If navPages is true, a dropdown list
     * of sets will navigate through the data sets.
     *
     * @return  true/false
     */
    public boolean isNavSets() {
        return navSets;
    }

    /**
     * getter, if true and navPages is false, the <, <<, >, >> links will
     * navigate through data sets. If navPages is true, a dropdown list
     * of sets will navigate through the data sets.
     *
     * @param navSets
     */
    public void setNavSets(boolean navSets) {
        this.navSets = navSets;
    }


    /**
     * getter, if true, the resultset will be cached into cache manager instead of writing it to pagecontext
     *
     * @return  true/false
     */
    public boolean isCacheResultset() {
        return cacheResultset;
    }

    /**
     * @param  cacheResultset
     */
    public void setCacheResultset(boolean cacheResultset) {
        this.cacheResultset = cacheResultset;
    }

    /**
     * Get the name by which we can find a DisconnectedResultSet in scope.
     *
     * @return the name
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * Set the name by which we can find a BaseResultSet in scope.
     *
     * @param dataName the name
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * Get the name by which we can find an XMLGridHeader in scope.
     *
     * @return the name
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * Set the name by which we can find an XMLGridHeader in scope.
     *
     * @param headerName the name
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public boolean isSaveAsExcelCsv() {
        return saveAsExcelCsv;
    }

    public void setSaveAsExcelCsv(boolean saveAsExcelCsv) {
        this.saveAsExcelCsv = saveAsExcelCsv;
    }

    public boolean isSaveAsExcelHtml() {
        return saveAsExcelHtml;
    }

    public void setSaveAsExcelHtml(boolean saveAsExcelHtml) {
        this.saveAsExcelHtml = saveAsExcelHtml;
    }

    public String getDispositionTypeExcelCsvFile() {
        return dispositionTypeExcelCsvFile;
    }

    public void setDispositionTypeExcelCsvFile(String dispositionTypeExcelCsvFile) {
        if (!StringUtils.isBlank(dispositionTypeExcelCsvFile) &&
                (dispositionTypeExcelCsvFile.equals(ATTACH_DISP_TYPE) ||
                dispositionTypeExcelCsvFile.equals(INLINE_DISP_TYPE))) {
            this.dispositionTypeExcelCsvFile = dispositionTypeExcelCsvFile;
        }
    }

    public String getDispositionTypeExcelHtmlFile() {
        return dispositionTypeExcelHtmlFile;
    }

    public void setDispositionTypeExcelHtmlFile(String dispositionTypeExcelFile) {
        if (!StringUtils.isBlank(dispositionTypeExcelFile) &&
                (dispositionTypeExcelFile.equals(ATTACH_DISP_TYPE) ||
                dispositionTypeExcelFile.equals(INLINE_DISP_TYPE))) {
            this.dispositionTypeExcelHtmlFile = dispositionTypeExcelFile;
        }
    }

    public String getGridDetailDivId() {
        return gridDetailDivId;
    }

    public void setGridDetailDivId(String gridDetailDivId) {
        this.gridDetailDivId = gridDetailDivId;
    }

    public String getDeferredLoadDataProcess() {
        return deferredLoadDataProcess;
    }

    public void setDeferredLoadDataProcess(String deferredLoadDataProcess) {
        this.deferredLoadDataProcess = deferredLoadDataProcess;
    }

    public boolean isVirtualPaging() {
        return virtualPaging;
    }

    public void setVirtualPaging(boolean virtualPaging) {
        this.virtualPaging = virtualPaging;
    }

    public boolean isVirtualScrolling() {
        return virtualScrolling;
    }

    public void setVirtualScrolling(boolean virtualScrolling) {
        this.virtualScrolling = virtualScrolling;
    }

    public boolean isShowRowCntOnePage() {
        return showRowCntOnePage;
    }

    public void setShowRowCntOnePage(boolean showRowCntOnePage) {
        this.showRowCntOnePage = showRowCntOnePage;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("dti.oasis.tags.OasisGrid");
        buf.append("{actionName=").append(actionName);
        buf.append(",formName=").append(formName);
        buf.append(",gridId=").append(gridId);
        buf.append(",data=").append(data);
        buf.append(",header=").append(header);
        buf.append(",pageSize=").append(pageSize);
        buf.append(",gridPageNav=").append(gridPageNav);
        buf.append(",totalDataSets=").append(totalDataSets);
        buf.append(",dataSetSize=").append(dataSetSize);
        buf.append(",selectable=").append(selectable);
        buf.append(",showHeader=").append(showHeader);
        buf.append(",gridInsert=").append(gridInsert);
        buf.append(",sortable=").append(sortable);
        buf.append(",sortItem=").append(sortItem);
        buf.append(",sortType=").append(sortType);
        buf.append(",sortOrder=").append(sortOrder);
        buf.append(",navPages=").append(navPages);
        buf.append(",navSets=").append(navSets);
        buf.append(",gridWidth=").append(gridWidth);
        buf.append(",gridHeight=").append(gridHeight);
        buf.append(",tableWidth=").append(tableWidth);
        buf.append(",cellSpacing=").append(cellSpacing);
        buf.append(",cellPadding=").append(cellPadding);
        buf.append(",dataName=").append(dataName);
        buf.append(",headerName=").append(headerName);
        buf.append(",cacheResultset=").append(cacheResultset);
        buf.append(",saveAsExcelCsv=").append(saveAsExcelCsv);
        buf.append(",saveAsExcelHtml=").append(saveAsExcelHtml);
        buf.append(",dispositionTypeExcelCsvFile=").append(dispositionTypeExcelCsvFile);
        buf.append(",dispositionTypeExcelHtmlFile=").append(dispositionTypeExcelHtmlFile);
        buf.append(",gridDetailDivId=").append(gridDetailDivId);
        buf.append(",showRowCntOnePage=").append(showRowCntOnePage);
        buf.append(",deferredLoadDataProcess=").append(deferredLoadDataProcess);
        buf.append(",virtualPaging=").append(virtualPaging);
        buf.append(",virtualScrolling=").append(virtualScrolling);
        buf.append('}');
        return buf.toString();
    }

    private final Logger l = LogUtils.getLogger(getClass());
}
