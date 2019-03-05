//-----------------------------------------------------------------------------
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//
// THIS FILE IS MAINTAINED IN THE OasisTags PROJECT!!!
// IT IS PROPAGATED FROM THE OasisTags PROJECT TO THE OTHER PROJECTS!!!
// IT SHOULD ONLY BE MODIFIED IN THE OasisTags PROJECT!!!
// IT SHOULD NEVER BE MODIFIED IN THE CM PROJECT OR THE CIS PROJECT OR ANY
// PROJECT OTHER THAN OasisTags!!!
//
// NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!! NOTE !!!
//-----------------------------------------------------------------------------

// xmlproc.js
/**
 *The this library has a series of functions that allow you to manipulate the xml data island that is the basis for the grid tag
 XMLsort:	Sorts the grid.  There will be a dynamically generated function called "gridid_sort" as well.
 filter:	Filters the grid. There will be a dynamically generated function called "gridid_filter" as well.
 getChanges:	Returns an xml representation of the rows that have been inserted, updated, or deleted. The format will be:<RS><R id='[pk of row]' c0='foo' c1='foo2' update_ind='[Y/I/D]'/></RS>
 getChangesNonXML:	Returns a textual representation of the rows that have been inserted, updated, or deleted.  You tell it what the row and column delimiters will be.  Each row will be separated by the row delimiter.
 insertSelectedRecord:	Creates a copy of the currently selected row in the grid.
 getSelectedKeys:	Returns an array of pk values where the column 'SELECT_IND' is -1, meaning selected.
 getSelectedKeysString:	Returns an delimited String of pk values where the column 'SELECT_IND' is -1, meaning selected.
 getSelectedData:	Returns the underlying xml of the selected rows (SELECT_IND =-1)
 getSelectedDataString:	Returns the underlying data of the selected rows (SELECT_IND =-1) in String delimited format.
 getValues:	Returns an Array of all the values of a given column in the grid, even in the rows that are not visible
 getFilterValues:	Returns an Array of all the values of a given column in the grid where a filter criteria matches, even in the rows that are not visible.
 updateNode:	Updates a column in all rows, even the rows that are not visible.
 updateFilterNode:	Updates a column in all rows where a filter criteria matches, even the rows that are not visible.
 gotopage:	Navigates the grid's HTML table through the underlying XML Data Island.
 insertRow:	Inserts an empty row in the grid.
 deleteRow:	Deletes a row from the grid.
 undeleteRow:	Undeletes a row from the grid.
 getRow:	Positions the underlying xml data island to a specific row, like a ResultSet.
 setSelectedRow:	Selects a row in the grid.
 getSelectedRow:	Returns the selected row of the grid
 encodeXMLChar:	Encodes text to make it xml compliant, e.g. & becomes &amp;
 replace:	Globally replace a value with another.
 first:	Move to the first record of the underlying xml data island, like a ResultSet
 next:	Move to the next record of the underlying xml data island, like a ResultSet
 setItem:	Set value in xml island
 getitem:	Get value from xml island

 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------

 09/02/2006       Larry       added the functionality to unformat the money format data for
 storing purpose before submiting xml data
 11/14/2006       Bhong       Added functionality to support sort on formatmoney field
 12/11/2006       GCC         Corrected bug in gotopage function when type
 is N (code was not correctly indicating that last
 page had been visited).  Added new functions at
 end of file for CSV/HTML to Excel support (issue 64527).
 01/09/2007       GCC         Added functionality to support sort on
 updateonlymoney field.
 01/11/2007       Bhong       Fix a flaw when sort on formatmoney and updateonlymoney field
 01/18/2007       GCC         Changed form target in function
 postFormTextForCsvHtmlForExcel from "_blank" to
 "_self" (suggested by Kyle Shen).  This prevents a
 blank page from opening in a new browser window.
 01/23/2007        wer        Added baseOnBefore/AfterSort event handlers;
 Added selectFirstRowInGrid() function;
 Added getTableForXMLData(), getXMLDataForTable(), and getXMLDataForGridName() for
 more easily getting the XMLData associated with a Table and vice versa.
 01/26/2007      GCC          Added one line in insertRow to fix problem found
 and solved by Leo Dong.
 02/09/2007        mlm        Added setTableProperty(), getTableProperty(), getTableForGridElement() for adding
 multi-grid capability for oasis grid. Made relevent changes for the same.
 02/21/2007      GCC          Added code to displayGridNavButtons to prevent
 hiding of HDRTBL if Excel buttons are visible.
 02/21/2007        mlm        Added gridDetailDivId property and currentlySelectedGridId functionality.
 04/25/2007       GCC         Added code written by Leo Dong to fix problem
 with all rows from gird XML data not getting into
 Excel.
 09/25/2007       FWCH        Modified the displaying value of checkbox in exporting
 Excel file.
 11/29/2007        wer        Added scrollIntoView to fix Browser's scrollIntoView when header row is frozen in place
 12/04/2007        wer        Fixed inserRow to initialize the lastInsertedRowId from the origXMLData to allow for
 the grid being loaded with rows having id < 0.
 12/4/2007         wer        Fixed filter to include the hidden empty row if it exists
 to keep recordset.Fields collection in tact
 12/05/2007        wer        Fixed inserRow when table is empty to update the ID on both XMLData and origXMLData
 so that the filter/syncChanges method replaces instead of adds it's record
 12/06/2007        wer        Fixed setTableProperty to handle null and object values.
 01/28/2008        Joe        Fixed bug: When adding new row after deleting several rows,
 the new row gets inserted in the middle of grid instead of bottom.
 03/03/2008        wer        Added use of XMLTemplate to add a new row when there are no rows in the XMLData (including no dummy rows)
 03/13/2008        wer        Added support to specify USE_CURRENT_FILTER_VALUE to the filter() function.
 03/14/2008        joe        Added call of gridId_filter at the end of insertRow/deleteRow function with filterValue = 'USE_CURRENT_FILTER_VALUE'.
 03/18/2008        wer        Added isRecordSelected function.
 03/21/2008        joe        Added call of gridId_filter at the end of undeleteRow function with filterValue = 'USE_CURRENT_FILTER_VALUE'.
 04/07/2008        yhchen     dummy row enhancement
 05/26/2008        FWCH       Set the value to '' in the exported excel file if the dropdown field is unselected
 11/25/2008        khen       Corrected the script of unformat formated money when sorting by formated money.
 11/27/2008        kshen      Added script of unformat updateonly money when sorting by updateonly money.
 08/03/2009        Fred       Modified displayGridNavButtons to hide/show the grid navigation buttons independently
 09/16/2009        Fred       Modified function XMLsort to sort date-time in grid
 02/26/2010        Colton     Modified function displayGridNavButtons for display page number and current page
 05/20/2010        Wieland    Fixed the issue#104826, added "getCorePath()" in Line-183 and Line-179
 06/10/2010        Bhong      issue#108560 - Fixed a bug in deleteRow function and guarantee there always has a dummy row in origXMLData.
 06/15/2010        Syang      issue#108878 - Modified the function selectFirstRowInGrid to hide empty row in table if the recordset is empty.
 10/11/2010        wfu        111776: String literals refactoring.
 10/14/2010        tzhao      issue#109875 - Modified money format script to support multiple currency.
 10/20/2010        tzhao      issue#109875 - The pages included in the iframe don't include the header.jsp so there is a javascript error in those pages
 because of the undefinition of the currency_symbol variable.
 Move the the definition of currency_symbol to the xmlproc.js and set the initial value as $.
 10/21/2010        wer        issue#112038 - added a date parameter to the request to Export Excel to ensure the data is not cached.
 02/18/2011        ldong      112568 - Enchanced to handle the new date format DD/MON/YYYY
 09/01/2011        mlm        Moved reconnectAllFields from common.js of individual projects.
 09/20/2011        mxg         Issue #100716: Added Display Type FORMATTEDNUMBER
 09/29/2011        wfu        125316 - Modified tbl_onafterupdate to exclude currency and percent data type field.
 11/09/2011        wfu        125597 - Moved function getChangesOnly from common.js since it's duplicated used.
 11/09/2011        mlm        127676 - Refactored to fire selectFirstRowInGrid() after window.document.readyState is complete.
 01/05/2012        clm        126620 - Add function paginate, and set table property sorting to false
 in the end of readyStateReady function to ensure the flag is set to false in the end of sorting
 01/11/2013        skommi     136769 - Made changes to the 'filter' and 'addFilterCondition' functions to allow for the single and double quotes in the filter value.
                              This change was done keeping in mind, the search for 'checkNumber' field in ManageAgentCheckCommission page.
 07/16/2013        awu        146030 - 1. Modified insertRow to set a new parameter to tableProperty.
                                       2. Modified readyStateReady to scroll to bottom if necessary.
 07/22/2013        awu        146030 - 1. Modified insertRow to call executeWhenTestSucceeds for single page.
                                       2. Added scrollIntoViewWhenReady, isNewRowReady and startToScroll.
                                       3. Modified postSelectRowById to call scrollIntoViewWhenReady.
 07/26/2013        skommi     146072 - Modified rowchange() to fix the SelectALL checkbox to be deselected when any row in the grid is deselected.
 08/26/2013        kmv        145890 - Modify syncChanges to include records with CSELECT_IND='-1' in the filterValue variable. This is to keep the
                                       the checked records selected upon refiltering.
 04/21/2014        adeng      151192 - Modified selectRowById() to execute pageEntitlements when there is no row in grid.
 09/11/2014        wkong      155531 - Reset eof after update grid note.
 11/26/2014        wdang      158689 - Invoke setRowStyleForNewRow(if any) function on bottom of insertRow, since onreadystatechange event is not triggered 
                                       when inserting a new row without filtering but we need to change its style.
 01/09/2015        awu        157105 - Added new table properties isDeleteMultipleRow, DeletedMultipleRowsCount.
 08/07/2015        kshen      164735 - Used getElementsByName instead of document.all for getting in-grid eidt fields.
 04/21/2016        huixu      Issue#169769 provide another way to export excel from XMLData
 03/18/2014        huixu      Issue#176582 add another export excel button to export all grid columns.
 12/25/2017        kshen      190086. Changed getSelectedRow to return empty string if there is no records in the grid.
                                      Changed selectRowById to move to first row if there are no records in grid.
 06/06/2018        mlm        193723 - Refactored to promote getChanges() into framework.
 07/31/2018        mlm        193967 - Refactored to promote logic from commonOnBeforeGotoPage, selectFirstRowInTable
                                       and moveToFirstRowInTable into framework.
 08/01/2018        mlm        193968 - Refactored to promote setRecordsetByObject into framework as setCurrentRecordValues.
 08/08/2018        fhuang     194134 - Modified selectFirstRowInGrid to call first() function to avoid isEmptyRecordSet to throw exception
 08/20/2018        dpang      194134 - Added uncheckOtherCheckbox function.
 08/30/2018        ylu        194134 - add the missed if statement for 193968.
 09/03/2018        htwang     191837 - Modified setCurrentRecordValues to set the generated id to lastInsertedId correctly.
 09/07/2018        cesar      194886 - Modified generateRowDataFromXMLData() not to export column value when field is masked.
 09/19/2018        dpang      195835 - Added isColumnVisible() to check if a grid column is visible.
 09/26/2018        dpang      186058 - Modified getRow() to search row with specified rowId from the very first record if exists visible CSELECT_IND column.
 10/11/2018        cesar      193937 - Moved setRowStyle() from ePolicy/common.js to be part of the framework under baseSetRowStyle().
 10/16/2018        dpang      195835 - Change to check column element under specified grid in isColumnVisible().
 11/12/2018        wreeder    196147 - Create sortingDeferredObj property in baseOnBeforeSort() and resolve it in readyStateReady() if sorting
 11/20/2018        dzou       196922 - RM Grid replacement. Modified replace() to only replace the value when it is string type.
 11/29/2018        dpang      196632 - Changed updateFilterNode to not set UPDATE_IND to Y if update selectInd column.
 12/10/2018        dzhang     196632 - Change to use getElementDataType to get field data type.
 */
var begintime;
var endtime;

// xmlproc.js
var sortOrder = '-';
var xmlSource = null;
var attribNode = null;
var gridDataChange = false;
var filterflag = false;
var currentFilterValue = "//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D') and @id!='-9999']";
var gridrowselection = new Array();
var gridnames = new Array();
var updateablecolset;
var anchorfieldname;
var tblPropArray = new Array();
var tblCount = 0;
var bXMLSort = false;
var lastInsertedId = null;
var tablePropertyCacheKeyCode = null;
var tablePropertyFocusFieldName = null;
var isDeleteMultipleRow = false;
var DeletedMultipleRowsCount=0;

var isMultiGridSupported = false;
var currentlySelectedGridId = "";
var selectedTableRowNo = null;
var currency_symbol = '$';

var moneyFormatPattern = RegExp("^\\s*\\({0,1}\\"+currency_symbol+"\\d{1,3}(,\\d{3})*\\.\\d{2}\\){0,1}\\s*$");
var percentagePattern = RegExp("^\\s*((-|\\+)?\\d+)(\\.\\d+)*%{1}$");
var paraPattern = RegExp("^\\s*\\(.+\\)\\s*$");

// XML Constants
var XML_NUMBER = '4';
var XML_FORMATMONEY = '14';
var XML_FORMATDATE = '2';
var XML_FORMATDATETIME = '3';
var XML_UPDATEONLYDATE = '16';
var XML_UPDATEONLYDATETIME = '17';
var XML_UPDATEONLYMONEY = '18';
var XML_UPDATEONLYNUMBER = '19';
var XML_UPDATEONLYURL = '20';
var XML_DATE = '8';

// Vars needed for dealing with sending grid XML data to Excel.
var otxt;
var excelFlg = false;
var ogrid;
var ostartCol;
var otype;
var oretVal;
var otbl;
var ourl;
var odispType;
var exportVisibleColumnIds;
var exportHideColumnIds;
var isFullyExport = false;
var exportColumnsNames = "";
var origPageSize;
var origSelectedRow;

var DEFAULT_FORMATTED_MASKED_VALUE = "********";

function XMLsort(xslStyleSheet, XMLData, field, fieldtype)
{
    begintime = new Date();

    if (isMultiGridSupported) {
        sortOrder = "-";
        if (getTableProperty(getTableForXMLData(XMLData), "sortOrder")) {
            sortOrder = getTableProperty(getTableForXMLData(XMLData), "sortOrder");
        }
    }

    var gridTable = getTableForXMLData(XMLData);
    getDivForGrid(gridTable.id).scrollTop=0;

    var imageElements = $(gridTable).find("img");
    for (i = 0; i < imageElements.length; i++) {// Turn the arrows on and off depending on the sort column.
        var imageElement = imageElements[i];
        if (imageElement.id && imageElement.id.substring(0, 6) == "wfsort") {
            if (imageElement.id != "wfsort" + field) {
                hideShowElementByClassName(imageElement, true);
            } else {
                hideShowElementAsInlineByClassName(imageElement, false);
            }
        }
    }
    if (sortOrder == '+')
        sortOrder = '-';
    else
        sortOrder = '+';
    if (sortOrder == '+')
    {
        getObjectById('wfsort' + field).src =  getCorePath() + "/images/asc.gif";
        getObjectById('wfsort' + field).alt = "Sorted in ascending order";
    }
    else {
        getObjectById('wfsort' + field).src = getCorePath() + "/images/desc.gif";
        getObjectById('wfsort' + field).alt = "Sorted in descending order";
    }

    var sortNode = xslStyleSheet.selectSingleNode("//xsl:sort[0]");
    var fieldSortOrder = (sortOrder == "-") ? "descending" : "ascending";
    setSortNodeAtribute(sortNode, field, fieldtype, fieldSortOrder);

    var secondSortColumn = getTableProperty(getTableForXMLData(XMLData), "secondSortColumn");
    var secondSortType = getTableProperty(getTableForXMLData(XMLData), "secondSortType");
    var secondSortOrder = getTableProperty(getTableForXMLData(XMLData), "secondSortOrder");
    if (secondSortColumn != null && secondSortType != null && secondSortOrder != null) {
        var secondSortElement = xslStyleSheet.selectSingleNode("//xsl:sort[1]");
        if (secondSortElement == null) {
            //insert a new xsl:sort element
            secondSortElement = xslStyleSheet.selectSingleNode("//xsl:sort[0]").cloneNode(true);
            var loopElement = xslStyleSheet.selectSingleNode("//xsl:for-each");
            loopElement.insertBefore(secondSortElement, sortNode.nextSibling);
        }
        setSortNodeAtribute(secondSortElement, secondSortColumn, secondSortType, secondSortOrder);
    } else {
        //remove the second xsl;sort if it exists
        var secondSortElement = xslStyleSheet.selectSingleNode("//xsl:sort[1]");
        if (secondSortElement != null) {
            xslStyleSheet.selectSingleNode("//xsl:for-each").removeChild(secondSortElement);
        }
    }

    XMLData.loadXML(XMLData.transformNode(xslStyleSheet));
    bXMLSort = true;
    if (isMultiGridSupported) {
        setTableProperty(getTableForXMLData(XMLData), "bXMLSort", true)
        setTableProperty(getTableForXMLData(XMLData), "sortOrder", "'" + sortOrder + "'");
    }
    endtime = new Date();
}

function setSortNodeAtribute(sortNode, field, fieldtype, fieldSortOrder){
    var orderNode = sortNode.getAttributeNode("order");
    var itemNode = sortNode.getAttributeNode("select");
    var typeNode = sortNode.getAttributeNode("data-type");
    var type = "text";

    if (isMSXML30()) {
        switch (fieldtype) {
            case XML_FORMATDATE:
            case XML_UPDATEONLYDATE:
            case XML_DATE:
                field = 'concat(substring(' + field +
                    ',7,4),substring(' + field + ',1,2),substring(' + field + ',4,2))';
                break;

            case XML_FORMATDATETIME:
            case XML_UPDATEONLYDATETIME:
                field = 'concat(substring(' + field +
                    ',7,4),substring(' + field + ',1,2),' +
                    'substring(' + field + ',4,2),' +
                    'substring(' + field + ',18,2),' +
                    'substring(' + field + ',12,2),' +
                    'substring(' + field + ',15,2))';
                break;
            case XML_NUMBER:
            case XML_FORMATMONEY:
                field = "translate(translate(translate(translate(" + field + ",',',''),'$',''),')',''),'(','-')";
                type = "number";
                break;
            case XML_UPDATEONLYMONEY:
            case XML_UPDATEONLYNUMBER:
                field = "translate(translate(translate(translate(" + field + ",',',''),'$',''),')',''),'(','-')";
                type = "number";
                break;
        }
    }
    orderNode.value = fieldSortOrder;
    itemNode.value = field;
    typeNode.value = type;
}

/*
 Filter the XMLData with the provided stylesheed and filter value.
 All changes in XMLData synced to the provided ReferenceXML before filtering the data.
 If filterValue is empty, the following default filter is used:
 //ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D') and @id!='-9999']
 If the filterValue is set to USE_CURRENT_FILTER_VALUE, then the current filter defined in the stylesheet is used.
 */
var USE_CURRENT_FILTER_VALUE = "USE_CURRENT_FILTER_VALUE";
function filter(xslStyleSheet, ReferenceXML, XMLData, filterValue) {
    begintime = new Date();

    var table = getTableForXMLData(XMLData);
    getDivForGrid(table.id).scrollTop=0;

    filterflag = true;
    if (isMultiGridSupported) {
        setTableProperty(table, "filterflag", true);
        setTableProperty(table, "filtering", true);
        setTableProperty(table, "selectedTableRowNo", null);
    }

    // Sync all changes from XMLData to ReferenceXML before filtering ReferenceXML
    syncChanges(ReferenceXML, XMLData);

    // Build the XSL Filter
    attribNode = xslStyleSheet.selectSingleNode("//@select");
    // 1. Add the base filter always required.
    attribNode.value = "//ROW[(DISPLAY_IND = 'Y' and UPDATE_IND != 'D'";

    //This is added to see if the filter value has single quotes or double quotes in it.
    var singleQtExists = getTableProperty(table, "filterHasSingleQt");
    var doubleQtExists = getTableProperty(table, "filterHasDoubleQt");

    // 2. Add the current filter value if requested
    if (!isEmpty(filterValue) && filterValue == USE_CURRENT_FILTER_VALUE) {
        filterValue = getTableProperty(table, "currentFilterValue");
    }

    // 3. If the filterValue is not empty, add it to the XSL filter
    if (!isEmpty(filterValue)) {
        if (singleQtExists) {
            attribNode.value = '//ROW[(DISPLAY_IND = "Y" and UPDATE_IND != "D"';
        }
        attribNode.value += " and " + filterValue;
    }

    var tmpAttribNodeValue = attribNode.value;
    // 4. Always exclude the hidden empty row (@id=-9999) if exists to keep recordset.Fields collection in tact
    if (singleQtExists) {
        attribNode.value += ') and @id!="-9999"]';
    } else {
        attribNode.value += ") and @id!='-9999']";
    }
    // 5. Store the current filterValue
    if (singleQtExists || doubleQtExists) {
        setTableProperty(table, "currentFilterValue", filterValue);
    } else {
        setTableProperty(table, "currentFilterValue", "\"" + filterValue + "\"");
    }

    // 6. Filter the data by transforming the ReferenceXML with the XSL
    XMLData.loadXML(ReferenceXML.transformNode(xslStyleSheet));

    // There is no data add dummy row.
    if (XMLData.documentElement.selectNodes("//ROW").length == 0) {
        var template = XMLTemplate;
        if (isArray(template) && template.length > 0) {
            // There are multiple grids on the page. Use just one of the templates since they are all identical.
            template = template[0];
        }
        //include the hidden empty row (@id=-9999), but not store it to current filterValue
        attribNode.value = tmpAttribNodeValue + ") or @id='-9999']";
        XMLData.loadXML(template.transformNode(xslStyleSheet));
    }

    endtime = new Date();
}


function addFilterCondition(filterStr, fieldName, compareIndicator, fieldValue, fieldtype) {
    if (!isEmpty(fieldValue)) {
        if (!isEmpty(filterStr)) {
            filterStr = filterStr + " and ";
        }

        switch (fieldtype) {
            case XML_FORMATDATE:
            case XML_FORMATDATETIME:
            case XML_UPDATEONLYDATE:
            case XML_UPDATEONLYDATETIME:
            case XML_DATE:
                filterStr = filterStr + 'concat(substring(' + fieldName +
                    ',7,4),substring(' + fieldName + ',1,2),substring(' + fieldName + ',4,2))';
                filterStr = filterStr + compareIndicator;
                filterStr = filterStr + "concat(concat(substring('" + fieldValue +
                    "',7,4),substring('" + fieldValue + "',1,2)),substring('" + fieldValue + "',4,2))"
                break;
            case XML_NUMBER:
            case XML_FORMATMONEY:
            case XML_UPDATEONLYMONEY:
            case XML_UPDATEONLYNUMBER:
            default:
                if (fieldValue.indexOf("'")>-1)   {
                    filterStr = filterStr + fieldName + compareIndicator + '"' + fieldValue + '"';
                } else {
                    filterStr = filterStr + fieldName + compareIndicator + "'" + fieldValue + "'";
                }
        }
    }
    return filterStr;
}

function getChanges(ReferenceXML) {
    return getChangesInRSFormat(ReferenceXML);
}

function getChangesInRowsFormat(ReferenceXML, filter) {
    var result;
    if (dti.oasis.page.useJqxGrid()) {
        result = dti.oasis.grid.getGridChanges(ReferenceXML.getGridId());
    } else {
        if (isEmpty(filter)) {
            filter = "//ROW";
        }
        else {
            filter = "//ROW[" + filter + "]";
        }
        var modXML = ReferenceXML.documentElement.selectNodes(filter);
        var nodelen = modXML.length;
        var i;
        var j;
        var rowNode;
        var columnNode;
        var numColumnNodes;
        var result;
        var ID;
        var displayInd;
        var displayRows = "";
        var nonDisplayRows = "";

        for (i = 0; i < nodelen; i++) {
            rowNode = modXML.item(i);
            ID = rowNode.getAttribute("id");

            // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
            if (ID != "-9999" || nodelen == 1) {
                displayInd = "";

                result = '<ROW id="' + ID + '">'
                if (rowNode.hasChildNodes() == true) {
                    numColumnNodes = rowNode.childNodes.length;
                    for (j = 0; j < numColumnNodes; j++) {
                        columnNode = rowNode.childNodes.item(j);
                        var nodeValue = encodeXMLChar(columnNode.text);
                        if(moneyFormatPattern.test(nodeValue)){
                            nodeValue = unformatMoneyStrValAsStr(nodeValue);
                        } else if (percentagePattern.test(nodeValue)) {
                            nodeValue = convertPctToNumber(nodeValue);
                        }
                        result += "<" + columnNode.nodeName + ">" + nodeValue + "</" + columnNode.nodeName + ">";

                        if (columnNode.nodeName == "DISPLAY_IND")
                            displayInd = nodeValue;
                    }
                }
                result += "</ROW>";

                if (displayInd == "Y")
                    displayRows += result;
                else
                    nonDisplayRows += result;
            }
        }
        result = "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
    }
    return result;
}

function getChangesInRSFormat(ReferenceXML)
{
    begintime = new Date();

    var modXML = ReferenceXML.documentElement.selectNodes("//ROW[UPDATE_IND != 'N']");
    var modUPDATE_IND = ReferenceXML.documentElement.selectNodes("//ROW[UPDATE_IND != 'N']/UPDATE_IND");
    var nodelen = modXML.length;
    ;
    var i;
    var j;
    var node;
    var childnode;
    var childnodelen;
    var result;
    var ID;
    var UPDATE_IND;
    var updtcol;
    var updtcolarray;
    result = "<RS>";
    for (i = 0; i < nodelen; i++)
    {
        node = modXML.item(i);
        ID = node.getAttribute("id");
        if (ID != "-9999") {
            updtcol = node.getAttribute("col");
            updtcolarray = updtcol.split(',');
            result += '<R id="' + ID + '"'
            if (node.hasChildNodes() == true) {
                childnodelen = node.childNodes.length;
                for (j = 0; j < childnodelen; j++)
                {
                    childnode = node.childNodes.item(j);
                    for (var k = 0; k < updtcolarray.length; k ++) {
                        if (updtcolarray[k] == j) {
                            // added by Larry on 2006/09/02 for auto money format
                            var nodeValue = encodeXMLChar(childnode.text);
                            if (moneyFormatPattern.test(nodeValue)) {
                                nodeValue = unformatMoneyStrValAsStr(nodeValue);
                            }

                            result += " c" + k + '="' + nodeValue + '"';
                        }
                    }
                }
                result += ' update_ind="' + modUPDATE_IND.item(i).text + '"'
            }
            result += " />";
        }
    }

    result += "</RS>";

    endtime = new Date();

    return result;
}

/*
 Return just the changed columns as listed in the ROW.col attribute.
 */
function getChangesOnly(XMLData) {
    var modXML = XMLData.documentElement.selectNodes("//ROW[(UPDATE_IND='Y') or (UPDATE_IND='D') or (UPDATE_IND='I')]");
    var nodelen = modXML.length;
    var rowNode;
    var columnNode;
    var numColumnNodes;
    var result;
    var ID;
    var displayInd;
    var displayRows = "";
    var nonDisplayRows = "";

    for (var i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        ID = rowNode.getAttribute("id");

        // Exclude rows with id=-9999 only if there is at least one real row because they are newly added rows that were deleted.
        if (ID != "-9999" || nodelen == 1) {
            displayInd = "";

            result = '<ROW id="' + ID + '">'
            if (rowNode.hasChildNodes()) {
                numColumnNodes = rowNode.childNodes.length;
                for (var j = 0; j < numColumnNodes; j++) {
                    columnNode = rowNode.childNodes.item(j);
                    var nodeValue = encodeXMLChar(columnNode.text);
                    if(moneyFormatPattern.test(nodeValue)){
                        nodeValue = unformatMoneyStrValAsStr(nodeValue);
                    }
                    if(columnNode.nodeName.endsWith('DISP_ONLY')) {
                        continue;
                    }
                    result += "<" + columnNode.nodeName + ">" + nodeValue + "</" + columnNode.nodeName + ">";

                    if (columnNode.nodeName == "DISPLAY_IND")
                        displayInd = nodeValue;
                }
            }
            result += "</ROW>";

            if (displayInd == "Y")
                displayRows += result;
            else
                nonDisplayRows += result;
        }
    }

    result = "<ROWS>" + displayRows + nonDisplayRows + "</ROWS>";
    return result;
}

function getChangesNonXML(ReferenceXML, rowDelim, colDelim)
{
    begintime = new Date();
    var modXML = ReferenceXML.documentElement.selectNodes("//ROW[UPDATE_IND !='N']");
    var modUPDATE_IND = ReferenceXML.documentElement.selectNodes("//ROW[UPDATE_IND != 'N']/UPDATE_IND");
    var nodelen = modXML.length;
    ;
    var i;
    var j;
    var node;
    var childnode;
    var childnodelen;
    var result;
    var tmpstring;
    var ID;
    var UPDATE_IND;
    var updtcol;
    var updtcolarray;

    result = "";

    for (i = 0; i < nodelen; i++)
    {
        tmpstring = "";
        node = modXML.item(i);
        ID = node.getAttribute("id");
        if (ID != "-9999") {
            updtcol = node.getAttribute("col");
            updtcolarray = updtcol.split(',');
            tmpstring = ID + colDelim;
            if (node.hasChildNodes() == true) {
                childnodelen = node.childNodes.length;
                for (j = 0; j < childnodelen; j++)
                {
                    childnode = node.childNodes.item(j);
                    for (var k = 0; k < updtcolarray.length; k ++) {
                        if (updtcolarray[k] == j) {
                            tmpstring += childnode.text + colDelim;
                        }
                    }
                }
                tmpstring += modUPDATE_IND.item(i).text + colDelim;
            }
            result += tmpstring + rowDelim;
        }
    }

    endtime = new Date();
    return result;
}

/*
 Synchronize all updated (added/updated/deleted) records from the NewXML into the ReferenceXML.
 If the optional filterValue is provided, it is added to the filter string as an OR condition.
 */
function syncChanges(ReferenceXML, NewXML, filterValue)
{
    begintime = new Date();

    if (filterValue == undefined || filterValue == "") {
        filterValue = "UPDATE_IND != 'N' or CSELECT_IND = '-1' ";
    }
    else {
        filterValue += " or UPDATE_IND != 'N' or CSELECT_IND = '-1' ";
    }

    var modXML = NewXML.documentElement.selectNodes("//ROW[" + filterValue + "][UPDATE_IND != 'I-D']");
    var nodelen = modXML.length;
    var i;
    var j;
    var newNode;
    var refNode;
    var diff = "";
    var result = "<ROWS>" ;
    var ID;
    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i).cloneNode(true);
        ID = newNode.getAttribute("id");
        if (ID != "-9999" && newNode.hasChildNodes()) {
            refNode = ReferenceXML.documentElement.selectSingleNode("//ROW[@id='" + ID + "']");
            if (refNode == null) {
                ReferenceXML.documentElement.appendChild(newNode);
            }
            else {
                ReferenceXML.documentElement.replaceChild(newNode, refNode);
            }
        }
    }
    endtime = new Date();
}

// @obsolete
function insertSelectedRecord(ReferenceXML, NewXML)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    var nodelen = modXML.length;
    var i;
    var j;
    var newNode;
    var childnodelen;
    var childnode;

    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i);
        if (newNode.hasChildNodes() == true) {
            ReferenceXML.recordset.addnew();
            ReferenceXML.recordset.movelast();
            childnodelen = newNode.childNodes.length;
            for (j = 0; j < childnodelen; j++)
            {
                childnode = newNode.childNodes.item(j);
                ReferenceXML.recordset(j).value = childnode.text;
            }
        }
    }
    endtime = new Date();
}

function isRecordSelected(NewXML) {
    var selectedNodes = NewXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    return selectedNodes.length > 0;
}

function getSelectedKeys(NewXML)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    var nodelen = modXML.length;
    var i;
    var j = 0;
    var newNode;
    var childnodelen;
    var childnode;
    var ID;
    var DataArray = new Array();

    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i);
        ID = newNode.getAttribute("id");
        DataArray[j] = ID;
        j ++;
    }
    endtime = new Date();
    return DataArray;
}
function getSelectedKeysString(NewXML, SelectCol, rowDelim)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW[C" + SelectCol + "='-1']");
    var nodelen = modXML.length;
    var i;
    var j = 0;
    var newNode;
    var childnodelen;
    var childnode;
    var ID;
    var DataString = '';

    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i);
        ID = newNode.getAttribute("id");
        DataString = DataString + ID + rowDelim;
    }
    endtime = new Date();
    return DataString;
}
function getSelectedData(NewXML)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    var nodelen = modXML.length;
    var i;
    var j;
    var newNode;
    var childnodelen;
    var childnode;
    var ID;
    var DataArray = new Array(nodelen);

    for (i = 0; i < nodelen; i++)
        DataArray[i] = new Array()

    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i);
        ID = newNode.getAttribute("id");
        DataArray[i][0] = ID;
        if (newNode.hasChildNodes() == true) {
            childnodelen = newNode.childNodes.length;
            for (j = 0; j < childnodelen; j++)
            {
                childnode = newNode.childNodes.item(j);
                DataArray[i][(j + 1)] = childnode.text;
            }
        }

    }
    endtime = new Date();
    return DataArray;
}
function getSelectedDataString(NewXML, rowDelim, colDelim)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW[CSELECT_IND='-1']");
    var nodelen = modXML.length;
    var i;
    var j;
    var newNode;
    var childnodelen;
    var childnode;
    var tmpstring = '';
    var DataString = '';

    for (i = 0; i < nodelen; i++)
    {
        newNode = modXML.item(i);
        tmpstring = '';
        tmpstring = newNode.getAttribute("id") + colDelim;
        if (newNode.hasChildNodes() == true) {
            childnodelen = newNode.childNodes.length;
            for (j = 0; j < childnodelen; j++)
            {
                childnode = newNode.childNodes.item(j);
                tmpstring = tmpstring + childnode.text + colDelim;
            }
        }
        DataString = DataString + tmpstring + rowDelim;
    }
    endtime = new Date();
    return DataString;
}
function getValues(XMLData, nodeName)
{
    begintime = new Date();
    var i = 0;
    var nodeValue;
    var DataArray = new Array();
    XMLData.recordset.MoveFirst();
    while (! XMLData.recordset.EOF)
    {
        nodeValue = XMLData.recordset(nodeName).value;

        DataArray[i] = nodeValue;
        XMLData.recordset.MoveNext();
        i++;
    }
    endtime = new Date();
    return DataArray;
}
function getFilterValues(XMLData, nodeName, filterNode, filterValue)
{
    begintime = new Date();
    var i = 0;
    var nodeValue;
    var DataArray = new Array();
    XMLData.recordset.movefirst();
    while (! XMLData.recordset.eof)
    {
        if (XMLData.recordset(filterNode).value == filterValue)
        {
            nodeValue = XMLData.recordset(nodeName).value
            DataArray[i] = nodeValue;
            XMLData.recordset.movenext();
            i++;
        }
    }
    endtime = new Date();
    return DataArray;
}
function updateNode(XMLData, nodeName, nodeValue)
{
    begintime = new Date();
    var absolutePosition = XMLData.recordset.AbsolutePosition;
    var ignore = (nodeName == 'CSELECT_IND') || (nodeName == 'UPDATE_IND');
    XMLData.recordset.MoveFirst();
    while (! XMLData.recordset.EOF)
    {
        var ID = XMLData.recordset("id").value;
        // only do it if this is not one of those dummy rows
        if (ID != "-9999") {

            XMLData.recordset(nodeName).value = nodeValue;
            if ((!ignore) && (XMLData.recordset("UPDATE_IND").value != "I"))
                XMLData.recordset("UPDATE_IND").value = "Y";
        }
        XMLData.recordset.MoveNext();
    }
    //Move to the current row
    first(XMLData);
    XMLData.recordset.Move(absolutePosition - 1);
    endtime = new Date();
//    logDebug("Time spent in updateNode:" + (endtime.getTime() - begintime.getTime()) + "ms");
}

function updateFilterNode(XMLData, filterNode, filterNodeValue, updateNodeName, updateNodeValue, ignoreChangeUpdateInd)
{
    begintime = new Date();
    var gridId = XMLData.id.substring(0, XMLData.id.length - 1);

    var ignore = ignoreChangeUpdateInd || updateNodeName === 'CSELECT_IND'
        || updateNodeName === 'UPDATE_IND'
        || updateNodeName === dti.oasis.grid.getSelectIndColumnName(gridId);

    XMLData.recordset.MoveFirst();
    while (! XMLData.recordset.EOF)
    {
        var ID = XMLData.recordset("id").value;
        // only do it if this is not one of those dummy rows
        if (ID != "-9999") {

            if (XMLData.recordset(filterNode).value == filterNodeValue)
            {
                XMLData.recordset(updateNodeName).value = updateNodeValue;
                if ((!ignore) && XMLData.recordset("UPDATE_IND").value != "I")
                    XMLData.recordset("UPDATE_IND").value = "Y";
            }
        }
        XMLData.recordset.MoveNext();
    }
    endtime = new Date();
//    logDebug("Time spent in updateFilterNode:" + (endtime.getTime() - begintime.getTime()) + "ms");
}

function uncheckOtherCheckbox(XMLData, column, checkboxColumnObjName, checkboxDataFld, ignoreChangeUpdateInd) {
    var columnObjName = "chkCSELECT_IND";
    var dataFld = "CSELECT_IND";

    if (!isEmpty(checkboxColumnObjName)) {
        columnObjName = checkboxColumnObjName;
    }

    if (!isEmpty(checkboxDataFld)) {
        dataFld = checkboxDataFld;
    }

    if (isEmpty(column) || (column.name === columnObjName && column.checked)) {
        var rowID = XMLData.recordset("ID").value;
        updateFilterNode(XMLData, dataFld, '-1', dataFld, '0', ignoreChangeUpdateInd);
        first(XMLData);
        getRow(XMLData, rowID);
        XMLData.recordset(dataFld).value = '-1';
    }
}

function CompareElements(New, Reference, Element, ID)
{
    begintime = new Date();
    Is = New.text;
    Was = Reference.documentElement.selectSingleNode("//ROW[@id='" + ID + "']/" + Element).text;
    if (Was != Is)
        return "<" + Element + ">" + Is + "</" + Element + ">"
    else
        return ""
    endtime = new Date();
}

function getDiff(ReferenceXML, NewXML)
{
    begintime = new Date();
    var modXML = NewXML.documentElement.selectNodes("//ROW");
    var nodelen = modXML.length;
    var i;
    var j;
    var node;
    var childnode;
    var childnodelen;
    var diff = "";
    var result = "<ROWS>" ;
    var ID;
    var updtcol;
    var updtcolarray;
    for (i = 0; i < nodelen; i++)
    {
        node = modXML.item(i);
        ID = node.getAttribute("id");
        updtcol = node.getAttribute("col");
        updtcolarray = updtcol.split(',');

        if (node.hasChildNodes() == true) {
            childnodelen = node.childNodes.length;
            for (j = 0; j < childnodelen; j++)
            {
                childnode = node.childNodes.item(j);
                if (ID.substring(0, 2) == "-1")
                {
                    result += node.xml;
                    break;
                }
                else
                {
                    for (var k = 0; k < updtcolarray.length; k ++) {
                        if (updtcolarray[k] == j) {
                            diff = CompareElements(childnode, ReferenceXML, childnode.nodeName, ID);
                            if (diff != "") {
                                break;
                            }
                        }
                    }
                    if (diff != "") {
                        result += node.xml;
                        diff = "";
                        break;
                    }

                }

            }

        }

    }
    result += "</ROWS>";

    endtime = new Date();
    return result;
}

function rowchange(c)
{
    begintime = new Date();
    // stop hilighting the cell the element is in!
    //c.parentElement.bgColor='#073580';

    if(c.name.endsWith(DISPLAY_FIELD_EXTENTION)){
        if(c.className=='clsNumFmtd')
            syncDisplayableFormattedNumberToGrid(c.formatPattern);
        else if(c.className=='clsDate')
            syncDisplayableDateToGrid();
    }
    gridDataChange = true;
    if (isMultiGridSupported) {
        setTableProperty(getTableForGridElement(c), "gridDataChange", true);
    }
    // uncheck select-all if deselect a row
    if (c.name.indexOf('chkCSELECT_IND') == 0) {
        var suffix = c.name.substr(14);
        if (!c.checked) {
            if (hasObject('chkCSELECT_ALL' + suffix)) {
                var obj = getObject('chkCSELECT_ALL' + suffix);
                if (obj.checked)
                    obj.checked = false;
            }
        }
    }

    // Update the value of original date field.
    if (!dateFormatUS && c.name.endsWith(DISPLAY_FIELD_EXTENTION) && c.className=='clsDate') {
        var nodes = c.parentElement.parentElement.getElementsByTagName("INPUT");
        for (var i=0; i<nodes.length; i++) {
            if (nodes[i].name == normalizeFieldName(c) ) {
                updateHiddenFieldForDateField(c, nodes[i])
                c = nodes[i];
                break;
            }
        }
    }

    // Update the value of original number formatted field.
    if (c.name.endsWith(DISPLAY_FIELD_EXTENTION) && c.className=='clsNumFmtd') {
        var nodes = c.parentElement.parentElement.getElementsByTagName("INPUT");
        for (var i=0; i<nodes.length; i++) {
            if (nodes[i].name == normalizeFieldName(c) ) {
                updateHiddenFieldForNumberFormattedField(c, nodes[i], c.formatPattern)
                c = nodes[i];
                break;
            }
        }
    }

    if (window.commonRowchange)
        commonRowchange(c);

    if (window.userRowchange)
        userRowchange(c);

    endtime = new Date();
//    logDebug("Time spent in rowchange:" + (endtime.getTime() - begintime.getTime()) + "ms");     
}

function getTableProperty(tbl, propName) {
    var propValue = "";
    var isVariableExists = eval("window." + propName);
    if (isMultiGridSupported || !isVariableExists) {
        for (i = 0; i < tblCount; i ++) {
            if (tblPropArray[i].id == tbl.id) {
                propValue = eval("tblPropArray[" + i + "]." + propName);
                break;
            }
        }
    }
    else {
        propValue = eval(propName);
    }
    return propValue;
}

function setTableProperty(tbl, propName, propValue) {
    if (isMultiGridSupported) {
        for (i = 0; i < tblCount; i ++) {
            if (tblPropArray[i].id == tbl.id) {
                if (typeof propValue == "object") {
                    eval("tblPropArray[" + i + "]") [propName] = propValue;
                } else if (typeof propValue == "string") {
                    eval("tblPropArray[" + i + "]." + propName + "=" + addQuotesIfNotExist(propValue) );
                } else {
                    eval("tblPropArray[" + i + "]." + propName + "=" + propValue);
                }
                break;
            }
        }
    }
    else {
        try {
            if (propValue == null) {
                eval(propName + '= null');
            } else if (typeof propValue == "object") {
                eval(propName + '=propValue');
            } else if (typeof propValue == "string") {
                eval(propName + "=" + addQuotesIfNotExist(propValue) + ";");
            } else {
                eval(propName + "=" + propValue + ";");
            }
        }
        catch(e) {
        }
    }
}

function addQuotesIfNotExist(value) {
    var returnValue = null;
    if (value.startsWith("'") && value.endsWith("'")) {
        returnValue = value;
    } else if (value.startsWith("\"") && value.endsWith("\"")) {
        returnValue = value;
    } else {
        returnValue = "\"" + replace(value, "\"", "\\\"") + "\"";
    }
    return returnValue;
}

function tableProps(tableid, pageno, nrec, pagesize, pages, reclastpage, gridDetailDivId)
{
    begintime = new Date();
    this.id = tableid;
    this.pageno = pageno;
    this.nrec = nrec;
    this.pagesize = pagesize;
    this.pages = pages;
    this.reclastpage = reclastpage;
    this.lastpagevisited = false;
    this.hasrows = (nrec > 0);

    this.sortOrder = '-';
    if (getObject(tableid + "1")) {
        this.xmlSource = eval(tableid + "1.documentElement");
    }
    else {
        this.xmlSource = null;
    }
    this.gridDataChange = false;
    this.filterflag = false;
    this.filtering = false;
    this.currentFilterValue = "";
    this.updateablecolset = "";
    this.anchorfieldname = "";
    this.bXMLSort = false;
    this.lastInsertedId = 0;
    this.selectedRowId = null;
    this.previousSelectedRowId = null;
    this.gridDetailDivId = gridDetailDivId;
    this.selectedTableRowNo = null;
    this.oldClassName = "";
    this.rowobject = null;
    this.isUserReadyStateReadyComplete = false;
    this.isInCommonAddRow = false;
    this.isInSelectRowById = false;
    this.isAddMultipleRow = false;
    this.isDeleteMultipleRow = false;
    this.DeletedMultipleRowsCount = 0;
    this.firstAddedMulitpleRowId = null;
    this.tablePropertyCacheKeyCode = null;
    this.tablePropertyFocusFieldName = null;

    gridnames [gridnames.length] = tableid;

    endtime = new Date();
}
function setTable(tbl, XMLData)
{
    begintime = new Date();
    var i;
    var pageno = 1;
    var nrec = 0;
    var pagesize = 0;
    var pages = 0;
    var reclastpage = 0;

    pageno = 1;
    pagesize = tbl.dataPageSize;
    pages = 1;

    var modXML = XMLData.documentElement.selectNodes("//ROW[DISPLAY_IND = 'Y']");
    nrec = modXML.length;
    if (nrec > 0) {
        pages = Math.ceil(nrec / pagesize);
        reclastpage = (pages * pagesize) - nrec;
        // look at vcrNavBySet
        if (eval(tbl.id + "vcrNavBySet")) {
            document.all(tbl.id + "_pageno").innerHTML = getMessage("label.search.resultSet.pagination", new Array(eval(tbl.id + "currSet"), eval(tbl.id + "totalSets")));
        }
        else {
            var stringRecord = (nrec == 1 ? getMessage("label.search.record.pagination", new Array(nrec, 1, pages))
                : getMessage("label.search.records.pagination", new Array(nrec, 1, pages)));
            document.all(tbl.id + "_pageno").innerHTML = stringRecord;
        }
    }
    if (tblCount != 0) {
        for (i = 0; i < tblCount; i ++) {
            if (tblPropArray[i].id == tbl.id) {
                tblPropArray[i].pageno = pageno;
                tblPropArray[i].nrec = nrec;
                tblPropArray[i].pagesize = pagesize;
                tblPropArray[i].pages = pages;
                tblPropArray[i].reclastpage = reclastpage;
                return;
            }
        }
    }
    var gridDetailDivId = "";
    if (tbl.getAttributeNode("gridDetailDivId")) {
        gridDetailDivId = tbl.getAttributeNode("gridDetailDivId").nodeValue;
        tbl.removeAttribute("gridDetailDivId");
    }
    tblPropArray[tblCount] = new tableProps(tbl.id, pageno, nrec, pagesize, pages, reclastpage, gridDetailDivId);
    tblCount ++;
    endtime = new Date();
}

function paginate(tbl, type) {
    if (!getTableProperty(tbl, "sorting")) {
        baseOnBeforeGotoPage(tbl, type);
        gotopage(tbl, type);
        baseOnAfterGotoPage(tbl, type);
    }
}

function gotopage(tbl, type)
{
    begintime = new Date();
    getDivForGrid(tbl.id).scrollTop=0;

    var i;
    for (i = 0; i <= tblCount; i ++) {
        if (tblPropArray[i].id == tbl.id) {
            var pageno = tblPropArray[i].pageno;
            var nrec = tblPropArray[i].nrec;
            var pagesize = tblPropArray[i].pagesize;
            var pages = tblPropArray[i].pages;
            var reclastpage = tblPropArray[i].reclastpage;
            break;
        }
    }

    if (pages > 1) {
        switch (type) {
            case 'F':
                tbl.firstPage();
                pageno = 1;
                tblPropArray[i].lastpagevisited = false;
                document.all(tbl.id + "_pageno").innerHTML = getMessage("label.search.records.pagination", new Array(nrec, 1, pages));
                break;

            case 'N':
                if (pages > pageno) {
                    if (( parseInt(pageno) + 1 ) == pages) {
                        tbl.lastPage();
                        tblPropArray[i].lastpagevisited = true;
                    }
                    else {
                        tbl.nextPage();
                    }
                    pageno ++;
                    document.all(tbl.id + "_pageno").innerHTML = getMessage("label.search.records.pagination", new Array(nrec, pageno, pages));
                }
                break;

            case 'P':
                if (pageno > 1)
                {
                    tbl.previousPage();
                    pageno --;
                    document.all(tbl.id + "_pageno").innerHTML = getMessage("label.search.records.pagination", new Array(nrec, pageno, pages));
                }
                if (pageno == 1)
                    tblPropArray[i].lastpagevisited = false;
                break;

            case 'L':
                tbl.lastPage();
                pageno = pages;
                tblPropArray[i].lastpagevisited = true;
                document.all(tbl.id + "_pageno").innerHTML = getMessage("label.search.records.pagination", new Array(nrec, pageno, pages));
                break;
        }

        tblPropArray[i].pageno = pageno;
        tblPropArray[i].nrec = nrec;
        tblPropArray[i].pagesize = pagesize;
        tblPropArray[i].pages = pages;
        tblPropArray[i].reclastpage = reclastpage;
    }

    endtime = new Date();
//    logDebug("Time spent in gotopage:" + (endtime.getTime() - begintime.getTime()) + "ms");
}

function baseOnBeforeGotoPage(tbl, type) {
    var i;
    for (i = 0; i <= tblCount; i ++) {
        if (tblPropArray[i].id == tbl.id) {
            var pageno = tblPropArray[i].pageno;
            var nrec = tblPropArray[i].nrec;
            var pagesize = tblPropArray[i].pagesize;
            var pages = tblPropArray[i].pages;
            var reclastpage = tblPropArray[i].reclastpage;
            break;
        }
    }

    if (pages > 1) {
        var selectedRowNo = getTableProperty(tbl, "selectedTableRowNo");
        var moveTo = 0;

        switch (type) {
            case 'F':
                // move to the very first row in recordset
                moveRecordPointerInRecordset(tbl, moveTo, true);
                break;

            case 'N':
                if (pages > pageno) {
                    if (( parseInt(pageno) + 1 ) == pages) {
                        // move to the first row of last page --
                        //   When the user clicks on last page navigation button, with grid settings as:
                        //     1. total number of records retreived is 22,
                        //     2. the page size is set to 10
                        //   the grid will show the last 10 records (instead of 2).
                        //   So, position it by total records and page size.
                        moveTo = nrec - pagesize;
                        moveRecordPointerInRecordset(tbl, moveTo, true);
                    }
                    else {
                        // move to the first row of the next page
                        moveTo = pagesize - selectedRowNo + 1;
                        moveRecordPointerInRecordset(tbl, moveTo, false);
                    }
                }
                break;

            case 'P':
                if (pageno > 1)
                {
                    if (pageno == 2) {
                        // we're in the 2nd page, so move to the very first row in recordset
                        moveRecordPointerInRecordset(tbl, moveTo, true);
                    }
                    else {
                        // move to the first row of the prior page
                        moveTo = -(selectedRowNo + pagesize - 1);
                        moveRecordPointerInRecordset(tbl, moveTo, false);
                    }
                }
                break;

            case 'L':
                // move to the first row of last page
                moveTo = nrec - pagesize;
                moveRecordPointerInRecordset(tbl, moveTo, true);
                break;
        }
    }

    if (window.commonOnBeforeGotoPage) {
        commonOnBeforeGotoPage(tbl, type);
    }

    if (window.handleOnBeforeGotoPage) {
        handleOnBeforeGotoPage(tbl, type);
    }
}

function baseOnAfterGotoPage(table, type) {
    if (window.commonOnAfterGotoPage) {
        commonOnAfterGotoPage(table, type);
    }

    if (window.handleOnAfterGotoPage) {
        handleOnAfterGotoPage(table, type);
    }
}

function moveRecordPointerInRecordset(tbl, moveTo, isAbsolutePosition) {
    // move to the row as requested
    var XMLData = getXMLDataForTable(tbl);
    if (isAbsolutePosition)
        XMLData.recordset.MoveFirst();
    XMLData.recordset.Move(moveTo);

    // get the ID and set it as selected
    var id = XMLData.recordset("ID").value;
    setSelectedRow(tbl.id, id);

    // set the selected table row as the first
    setTableProperty(tbl, "selectedTableRowNo", 1);
    setTableProperty(tbl, "rowobject", null);
}

function resetRecordPointerToFirstRowInGridCurrentPage(tbl) {
    var nrec = getTableProperty(tbl, "nrec");
    var pagesize = getTableProperty(tbl, "pagesize");
    var pages = getTableProperty(tbl, "pages");
    var pageno = getTableProperty(tbl, "pageno");
    var lastpagevisited = getTableProperty(tbl, "lastpagevisited");

    var moveTo = pagesize * (pageno - 1);
    if (lastpagevisited) {
        moveTo -= (pagesize * pages - nrec);
    }

    var XMLData = getXMLDataForTable(tbl);
    XMLData.recordset.MoveFirst();
    XMLData.recordset.Move(moveTo);
}

function selectFirstRowInGrid(gridName) {
//    logDebug('selectFirstRowInGrid('+gridName+')');
    if ((window.document.readyState!="complete")) {
        //Select the first row in the grid, only after the document is ready 
        // - so that OBR will be fired and all fields will be available for OBR to act upon.

        //Collect the gridId, so that the basePageOnLoad will utilize to fire the logic, once the document readyState is complete.
        addGridIdToFireSelectFirstRowInGrid(gridName);
        return;
    }
    begintime = new Date();
    var id = -1;
    var XMLData = getXMLDataForGridName(gridName);
    first(XMLData);
    currentlySelectedGridId = gridName;
    if (isEmptyRecordset(XMLData.recordset) == false) {
        if (XMLData != null && XMLData.recordset.recordcount > 0) {
            XMLData.recordset.movefirst();
            id = XMLData.recordset("ID");

            // highlight the 1st row
            var tableDiv = getSingleObject("DIV_" + gridName);
            var children = tableDiv.childNodes;
            for (var i = 0; i < children.length; i++) {
                if (children[i].nodeName.toUpperCase() == "TABLE") {
                    var table = children[i];
                    hiliteSelectFirstRow(table);
                    break;
                }
            }
            baseOnRowSelected(gridName, id.value);
        }
    }
    else {
        // Hide the empty row in table.
        hideEmptyTable(getTableForGrid(gridName));
        hideGridDetailDiv(gridName);
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(true, gridName);
        }
    }
    endtime = new Date();
//    logDebug("Time spent in selectFirstRowInGrid:" + (endtime.getTime() - begintime.getTime()) + "ms");
}

function getElementClientRects(element) {
    var clientRects;
    if (element.getClientRects().length > 0) {
        clientRects = element.getClientRects()[0];
    }
    else {
        clientRects = element.getBoundingClientRect();
    }

    return clientRects;
}

function baseOnBeforeSort(table, XMLData, field, fieldtype) {
    setTableProperty(table, "currentSortColumn", "'" + field + "'")
    setTableProperty(table, "currentSortType", "'" + fieldtype + "'");
    setTableProperty(table, "sorting", true);
    dti.oasis.grid.setProperty(table.id, "sortingDeferredObj", $.Deferred());
    if (window.commonOnBeforeSort) {
        commonOnBeforeSort(table, XMLData);
    }

    if (window.handleOnBeforeSort) {
        handleOnBeforeSort(table, XMLData);
    }
}

function baseOnAfterSort(table, XMLData, field, fieldtype) {

    gotopage(table, 'F');

    if (window.commonOnAfterSort) {
        commonOnAfterSort(table, XMLData);
    }

    if (window.handleOnAfterSort) {
        handleOnAfterSort(table, XMLData);
    }
}

function tbl_onafterupdate(tbl, XMLData)
{
    begintime = new Date();
    var j;
    var k;
    for (k = 0; k <= tblCount; k ++) {
        if (tblPropArray[k].id == tbl.id) {
            var pageno = tblPropArray[k].pageno;
            var nrec = tblPropArray[k].nrec;
            var pagesize = tblPropArray[k].pagesize;
            var pages = tblPropArray[k].pages;
            var reclastpage = tblPropArray[k].reclastpage;
            var lastpagevisited = tblPropArray[k].lastpagevisited;
            break;
        }
    }
    var parentTrRow = findParentTrRow(window.event.srcElement);
    var i = parentTrRow.rowIndex;
    if (i > 0)
        i --;

    j = (pageno - 1) * pagesize;
    if (pageno == pages && pageno != 1) {
        i = i + j - reclastpage;
    }
    else {
        if (lastpagevisited) {
            i = i + j - reclastpage;
        }
        else {
            i = i + j;
        }
    }
    XMLData.recordset.movefirst();
    if (!isNaN(i))
        XMLData.recordset.move(i);

    var field = window.event.srcElement;
    // if this is just the checkbox, don't count this as an update
    if (field.name != 'chkCSELECT_IND') {
        if (XMLData.recordset("UPDATE_IND").value != "I") {
            var updated = true;
            var fldDataType = dti.oasis.ui.getElementDataType(field);
            if (fldDataType == DATATYPE_CURRENCY_FORMATTED) {
                updated = (valueBeforeFocus == formatMoneyStrValAsStr(field.value)) ? false : true;
            }
            if (fldDataType == DATATYPE_PERCENTAGE) {
                updated = (valueBeforeFocus == formatPctStrVal(field.value)) ? false : true;
            }
            if (updated) {
                XMLData.recordset("UPDATE_IND").value = "Y";
            }
        }
    }
    //test line
    gridDataChange = true;
    if (isMultiGridSupported) {
        setTableProperty(tbl, "gridDataChange", true);
    }
    endtime = new Date();
}
function tbl_onbeforeupdate(tbl, XMLData)
{
    return;
}

function insertRow(tbl, origXMLData, XMLData, addingDummyRecord)
{
    begintime = new Date();
    var newNode;
    var temp_id;

    // get the last index. added by Joe
    var temp_index = getLastIndex(origXMLData);
    temp_index ++;

    // if we've already inserted a row, then decrement the last id so we sequence the rows properly
    if (isMultiGridSupported) {
        if (getTableProperty(tbl, "lastInsertedId") != '' && parseInt(getTableProperty(tbl, "lastInsertedId")) < 0)
            temp_id = parseInt(getTableProperty(tbl, "lastInsertedId"));
        else //first time insert -3000, now there's a nice looking number!
            temp_id = getLastInsertedRowId(origXMLData);
    }
    else {
        if (lastInsertedId != '' && parseInt(lastInsertedId) < 0)
            temp_id = parseInt(lastInsertedId);
        else //first time insert -3000, now there's a nice looking number!
            temp_id = getLastInsertedRowId(origXMLData);
    }
    if (temp_id && temp_id <= -3000) {
        // If there were any added rows, decrement the last inserted row id
        temp_id--;
    }
    else {
        // Initialize the first inserted row id to -3000
        temp_id = '-3000';
    }
    //var temp_id = Math.round(-10000 * Math.random()).toString();
    var orig_grid_id = getOrigXMLDataId(XMLData);

    if (!showNonEmptyTable(tbl)) {
        XMLData.recordset.addnew();
        XMLData.recordset.movelast();
        addAllColumnsForNewRow(XMLData);
    }
    else {
        var refNode = XMLData.documentElement.selectSingleNode("//ROW[@id='-9999']");
        if (refNode != null) {
            // There is a dummy row. Use the exising dummy row as the new row.
            refNode.setAttribute("id", temp_id);
            refNode.selectSingleNode("./UPDATE_IND").text = "I";
            refNode.selectSingleNode("./DISPLAY_IND").text = "Y";
            refNode.selectSingleNode("./EDIT_IND").text = "Y";
        }
        else {
            // There is no dummy row.
            if (isEmptyRecordset(XMLData.recordset)) {
                // There is no dummy record, and the record set is empty.
                // Use the XMLTemplate to add a new record, and transform it from the grid's XSL to get all the columns.
                var gridXSL = eval(XMLData.id + "XSL");
                var template = XMLTemplate;
                if (isArray(template) && template.length > 0) {
                    // There are multiple grids on the page. Use just one of the templates since they are all identical.
                    template = template[0];
                }
                XMLData.loadXML(template.transformNode(gridXSL))
                refNode = XMLData.documentElement.selectSingleNode("//ROW[@id='-9999']");
            }
            else {
                // The recordset is not empty, and we are not showing a non-empty table.
                // This probably means the table was filtered, showing a subset of the data, and is was un-filtered to add the new row.
                // This may happen when 2 grids are displayed, and the contents of the second is filtered bases on the row selected in the 1st grid.
                // Simply add a new row
                XMLData.recordset.addnew();
                XMLData.recordset.movelast();
                addAllColumnsForNewRow(XMLData);
            }
        }
    }
    XMLData.recordset.movelast();
    lastInsertedId = temp_id;
    if (isMultiGridSupported) {
        setTableProperty(tbl, "lastInsertedId", temp_id);
    }

    XMLData.documentElement.selectNodes("//ROW").item(XMLData.recordset.recordCount - 1).setAttribute("id", temp_id);
    XMLData.documentElement.selectNodes("//ROW").item(XMLData.recordset.recordCount - 1).setAttribute("index", temp_index);
    XMLData.recordset(0).value = "javascript:selectRowWithProcessingDlg('" + orig_grid_id + "','" + temp_id + "');";
    if (isMultiGridSupported) {
        XMLData.documentElement.selectNodes("//ROW").item(XMLData.recordset.recordCount - 1).setAttribute("col", getTableProperty(tbl, "updateablecolset"))
    }
    else {
        XMLData.documentElement.selectNodes("//ROW").item(XMLData.recordset.recordCount - 1).setAttribute("col", updateablecolset)
    }
    //alert(XMLData.xml);
    //gridDataChange = true;
    if (!addingDummyRecord) {
        eval(orig_grid_id + '_setInitialValues()');

        if (XMLData.recordset.EOF) {
            // The _setInitialValues() sometimes causes the recordset to move past the end, so reset it to the end.
            XMLData.recordset.movelast();
        }
    }
    // Always set the UPDATE_IND to 'I'
    XMLData.recordset("UPDATE_IND").value = "I";

    // Set the DISPLAY_IND to the "Y" default if the _initialValues() function didn't set it
    if (!XMLData.recordset("DISPLAY_IND").value) {
        XMLData.recordset("DISPLAY_IND").value = "Y";
    }
    // Set the EDIT_IND to the "Y" default if the _initialValues() function didn't set it
    if (!XMLData.recordset("EDIT_IND").value) {
        XMLData.recordset("EDIT_IND").value = "Y";
    }

    if (!addingDummyRecord) {
        // OBR on add logic
        fireOBROnAdd(tbl.id);
    }

    var isAddMultipleRow = getTableProperty(tbl, "isAddMultipleRow");
    var firstAddedMulitpleRowId = getTableProperty(tbl, "firstAddedMulitpleRowId");
    //if it is inserting multiple row, and is inserting the first row
    if (isAddMultipleRow && firstAddedMulitpleRowId == null) {
        //set firstAddedMulitpleRowId flag to crrent row id
        setTableProperty(tbl, "firstAddedMulitpleRowId", XMLData.recordset("ID").value);
    }

    // update table information
    setTable(tbl,XMLData);

    if (addingDummyRecord) {
        setTableProperty(tbl, "selectedTableRowNo", null);
    } else {
        if (getTableProperty(tbl, "pageno") < getTableProperty(tbl, "pages")) {
            setTableProperty(tbl, "selectedTableRowNo", tbl.rows.length - 1);
            setSelectedRow(tbl.id, lastInsertedId);
            setTableProperty(tbl, "scrollLastRowIntoView", "Y");
            // go to last page if necessary
            gotopage(tbl, 'L');
        } else {
            var elem = tbl.rows(tbl.rows.length - 1);
            hiliteSelectRow(elem);
            if (!isAddMultipleRow) {
                // scroll the row into view
                handleScrollIntoViewWhenReady(tbl.id, tbl.rows.length - 1);
            }
            var isUserReadyStateReadyComplete = getTableProperty(tbl, "isUserReadyStateReadyComplete");
            if (isUserReadyStateReadyComplete == false) {
                if (window.setRowStyleForNewRow) {
                    // Make sure the recordset point to the newly-added row. 
                    setRowStyleForNewRow(tbl, elem);
                }
                // call insertRow from commonAddRow, postCommonAddRow will select the row. 
                setTableProperty(tbl, "isUserReadyStateReadyComplete", true);
            } else {
                // not from commonAddRow, it is only for old code which calls gridId_insertRow() directly.
                selectRow(tbl.id, lastInsertedId);
            }
        }
    }
    endtime = new Date();
}

function isNewRowReady(gridId, tableRowIndex) {
    var isReady = false;
    var tbl = getTableForGrid(gridId);
    var element = tbl.rows(tableRowIndex);
    var elementRect = getElementClientRects(element);
    if (elementRect.top != elementRect.bottom) {
        isReady = true;
    }
    return isReady;
}

function scrollIntoViewWhenReady(gridId, tableRowIndex) {
    var testCode = 'isNewRowReady(\"' + gridId + '\", ' + tableRowIndex + ')';
    var callbackCode = 'handleScrollIntoViewWhenReady(\"' + gridId + '\", ' + tableRowIndex + ');';
    executeWhenTestSucceeds(testCode, callbackCode, 50);
}

function handleScrollIntoViewWhenReady(gridId, tableRowIndex) {
    var tbl = getTableForGrid(gridId);
    var elementIntoDiv = tbl.rows(tableRowIndex);
    var parentDiv = getDivForGrid(tbl.id);
    var elementTop = $(elementIntoDiv).offset().top;
    var elementBottom = elementTop + $(elementIntoDiv).outerHeight();
    if (elementBottom == 0) {
        return;
    }
    var parentDivTop = $(parentDiv).offset().top;
    var parentDivBottom = parentDivTop + $(parentDiv).outerHeight();
    var headerHeight = $(parentDiv).find("th").height();

    // Calculate the horizontal scroll bar
    if (parentDiv.scrollWidth > $(parentDiv).innerWidth()) {
        parentDivBottom = parentDivBottom - 20;
    }

    if (window.handleOnBeforeScrollIntoView) {
        var continueFrameworkScroll = window.handleOnBeforeScrollIntoView(parentDiv, elementIntoDiv, parentDivTop + headerHeight, parentDivBottom, elementTop, elementBottom);
        if (!continueFrameworkScroll){
            return;
        }
    }

    if (elementTop < parentDivTop + headerHeight) {
        // the target element is above the parent div, need to move it up.
        parentDiv.scrollTop = parentDiv.scrollTop - (parentDivTop + headerHeight - elementTop);
    } else if (elementBottom > parentDivBottom) {
        // the target element is below the parent div, need to move it down.
        parentDiv.scrollTop = elementBottom - parentDivBottom + parentDiv.scrollTop;
    }

    if (window.handleOnAfterScrollIntoView) {
        window.handleOnAfterScrollIntoView (parentDiv, elementIntoDiv, parentDivTop + headerHeight, parentDivBottom, elementTop, elementBottom);
    }
}

/**
 * add all columns for the new row
 */
function addAllColumnsForNewRow(XMLData) {
    var firstRowElement = XMLData.documentElement.selectNodes("//ROW").item(0);
    for (var i = 0; i < firstRowElement.childNodes.length; i ++) {
        var node = firstRowElement.childNodes[i];
        if (node.nodeType == 1) {
            var elementName = node.tagName;
            XMLData.recordset(elementName).value = "";
        }
    }
}

function getLastInsertedRowId(origXMLData) {
    var modXML = origXMLData.documentElement.selectNodes("//ROW[@id < 0]");
    var nodelen = modXML.length;
    var rowNode;
    var rowId;
    // Default the last inserted row id to 0
    var lastInsertedRowId = 0;
    for (i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        rowId = parseInt(rowNode.getAttribute("id"));
        if (rowId < lastInsertedRowId &&
            rowId != -9999) { // do not use the empty row to set the last inserted row id
            lastInsertedRowId = rowId;
        }
    }
    return lastInsertedRowId;
}

function getLastIndex(origXMLData) {
    var modXML = origXMLData.documentElement.selectNodes("//ROW");
    var nodelen = modXML.length;
    var rowNode;
    var index;
    var lastIndex = 0;
    for (var i = 0; i < nodelen; i++) {
        rowNode = modXML.item(i);
        index = parseInt(rowNode.getAttribute("index"));
        if (index > lastIndex) {
            lastIndex = index;
        }
    }
    return lastIndex;
}

function deleteRow(tbl, origXMLData, XMLData)
{
    begintime = new Date();
    var refNode;
    var orig_grid_id = getOrigXMLDataId(XMLData);
    var selrowid = getSelectedRow(orig_grid_id);
    var i = getRow(XMLData, selrowid);
    var gonnaHide = false;

    if (isNaN(i) || i < 0)
        return false; //	no current record	i = 0;

    if (XMLData.recordset.recordcount == 1)
    {
        insertRow(tbl, origXMLData, XMLData, true);
        XMLData.recordset("ID").value = -9999;
        XMLData.recordset("DISPLAY_IND").value = "N";
        XMLData.recordset.movefirst();
        XMLData.recordset.move(i);
        gonnaHide = true;
        setTableProperty(tbl, "selectedTableRowNo", null);

        hideEmptyTable(tbl);
    }
    if (XMLData.recordset("UPDATE_IND").value == "I") {

        refNode = origXMLData.documentElement.selectSingleNode("//ROW[@id='" + selrowid + "']");
        if (refNode != null) {
            var modXML = origXMLData.documentElement.selectNodes("//ROW");
            var nodelen = modXML.length;
            if (nodelen == 1) {
                // If this is the last node, update the row id to -9999 and use it as dummy row
                refNode.setAttribute("id", "-9999");
                refNode.selectSingleNode("./UPDATE_IND").text = "N";
                refNode.selectSingleNode("./DISPLAY_IND").text = "N";
            }
            else {
                origXMLData.documentElement.removeChild(refNode);
            }
        }
        XMLData.recordset("UPDATE_IND").value = "I-D";
    }
    else {
        XMLData.recordset("UPDATE_IND").value = "D";
    }

    if (!gonnaHide) {
        setTableProperty(tbl, "selectedTableRowNo", null);
    }

    if (getTableProperty(tbl, "isDeleteMultipleRow") == true) {
        var count = getTableProperty(tbl, "DeletedMultipleRowsCount");
        setTableProperty(tbl, "DeletedMultipleRowsCount", count + 1);
        // Do nothing, developer should call endMultipleDelete after deleting all.
    } else {
        // filter with current filterValue
        eval(orig_grid_id + "_filter(" + USE_CURRENT_FILTER_VALUE + ")");
    }

    endtime = new Date();

    return gonnaHide;
}

function beginDeleteMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    setTableProperty(table, "isDeleteMultipleRow", true);
    setTableProperty(table, "DeletedMultipleRowsCount", 0);
}

function cancelDeleteMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    setTableProperty(table, "isDeleteMultipleRow", false);
    setTableProperty(table, "DeletedMultipleRowsCount", 0);
}

function endDeleteMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    var count = getTableProperty(table, "DeletedMultipleRowsCount");
    if (count > 0) {
        var orig_grid_id = getOrigXMLDataId(getXMLDataForTable(table));
        eval(orig_grid_id + "_filter(" + USE_CURRENT_FILTER_VALUE + ")");
    }
    setTableProperty(table, "isDeleteMultipleRow", false);
    setTableProperty(table, "DeletedMultipleRowsCount", 0);
}

function undeleteRow(XMLData)
{
    begintime = new Date();
    var orig_grid_id = getOrigXMLDataId(XMLData);
    var selrowid = getSelectedRow(orig_grid_id);
    var i = getRow(XMLData, selrowid);
    if (isNaN(i) || i < 0)
        return; //	no current record	i = 0;

    if (XMLData.recordset("UPDATE_IND").value == "D") {
        XMLData.recordset("UPDATE_IND").value = "Y";
    }

    // filter with current filterValue
    eval(orig_grid_id + "_filter(" + USE_CURRENT_FILTER_VALUE + ")");

    endtime = new Date();
}

function getRow(XMLData, rowId)
{
    begintime = new Date();
    if (!XMLData.recordset.eof && !isEmptyRecordset(XMLData.recordset)) {
        XMLData.recordset.movefirst();

        var gridId = XMLData.id.substring(0, XMLData.id.length - 1);
        //If exists visible CSELECT_IND column, in case the selected row is in previous page, just look for the row from the very first record.
        if (!isFieldDefinedForGrid(gridId, "CSELECT_IND") || !isColumnVisible(gridId, "CSELECT_IND")) {
            XMLData.recordset.Move(getMoveTo(XMLData));
        }

        var i = 0;
        while (!XMLData.recordset.eof)
        {
            if (XMLData.recordset(0).value == '')
                break;
            if (XMLData.recordset("ID").value == rowId)
                return i;
            XMLData.recordset.movenext();
            i++;
        }
    }
    endtime = new Date();

    return -1;
}

function getMoveTo(XMLData) {
    var pageNoDesc = document.all(XMLData.id.substring(0, XMLData.id.length - 1) + "_pageno").innerHTML.toUpperCase();

    var totalRecords = pageNoDesc.substring(0, pageNoDesc.indexOf(' '));
    var noOfRecordsPerPage = getSingleObject(XMLData.id.substring(0, XMLData.id.length - 1)).dataPageSize;

    //     When the user clicks on last page navigation button, with grid settings as :
    //            1. total number of records retreived is 22,
    //            2. the page size is set to 10
    //       the grid will still show 10 records (instead of showing 2 records). So, to position the record pointer correctly
    //       for search, use the roundOffRecords variable.

    var roundOffRecords = noOfRecordsPerPage - (totalRecords % noOfRecordsPerPage);

    var currentPageNo = pageNoDesc.substring(pageNoDesc.indexOf(getMessage("label.search.pagination.page") + " ") + (getMessage("label.search.pagination.page") + " ").length)
    var totalPages = currentPageNo.substring(currentPageNo.indexOf(" " + getMessage("label.search.pagination.of") + " ") + (" " + getMessage("label.search.pagination.of") + " ").length)
    currentPageNo = currentPageNo.substring(0, currentPageNo.indexOf(" " + getMessage("label.search.pagination.of")));

    var moveTo = ((currentPageNo * noOfRecordsPerPage) - noOfRecordsPerPage) ;
    moveTo = ((moveTo - roundOffRecords) > 0 ? (moveTo - roundOffRecords) : moveTo);
    return moveTo;
}

function setSelectedRow(gridId, rowId)
{
    begintime = new Date();
    if (isMultiGridSupported) {
        var currentlySelectedRowId = getTableProperty(getTableForGrid(gridId), "selectedRowId");
        if (currentlySelectedRowId) {  //otherwise, this is the first time a row is getting selected. No need to setup previous value.
            if (rowId != currentlySelectedRowId) { //make sure its a different row compared to the currently selected row
                setTableProperty(getTableForGrid(gridId), "previousSelectedRowId", currentlySelectedRowId);
            }
        }
        setTableProperty(getTableForXMLData(eval(gridId + "1")), "selectedRowId", rowId);
    }
    else {
        var i;
        i = gridrowselection.length;
        //find the grid id
        var k = 0;
        while (k < i && gridnames[k] != gridId)
        {
            k++;
        }
        if (k < i)    // grid id found; if not - will add new grid to array
        {
            i = k;
        }
        else
        {
            gridnames[i] = gridId;
        }
        gridrowselection[i] = rowId;
    }
    currentlySelectedGridId = gridId;
    endtime = new Date();
}

function getSelectedRow(gridId)
{
    var result;
    if (isMultiGridSupported) {
        if (getTableProperty(getTableForGrid(gridId), "hasrows")) {
            result = getTableProperty(getTableForXMLData(eval(gridId + "1")), "selectedRowId");
            if (result == null) {
                result = '';
            }
        } else {
            result = '';
        }
    }
    else {
        var i;
        i = gridrowselection.length;
        //find the grid id
        var k = 0;

        while (k < i && gridnames[k] != gridId)
        {
            k++;
        }
        if (k < i)    // grid id found; if not - return empty string
        {
            result = gridrowselection[k];
        }
        else
        {
            result = '';
        }
    }
    endtime = new Date();
    return result;
}

function getCurrentlySelectedGridId() {
    return currentlySelectedGridId;
}

function getTableForXMLData(XMLData) {
    var table = getObjectById(XMLData.id.substring(0, XMLData.id.length - 1));
    return table;
}

function getTableForGrid(gridId) {
    var table = getObjectById(gridId);
    return table;
}

function getDivForGrid(gridId) {
    var gridDiv = getObjectById("DIV_" + gridId);
    return gridDiv;
}

function getTableForGridElement(element) {
    var tableElement = null;
    var currentElement = element;
    while (currentElement.parentElement != null) {
        if (currentElement.parentElement.tagName.toUpperCase() == "TABLE") {
            if (currentElement.parentElement.className.toUpperCase() == "CLSGRID" && !currentElement.parentElement.className.dataSrc) {
                tableElement = currentElement.parentElement;
                break;
            }
        }
        currentElement = currentElement.parentElement;
    }
    return tableElement;
}

function hasXMLDataForTable(table) {
    return (getObject(table.id + "1") != null);
}

function getXMLDataForTable(table) {
    var XMLData = eval(table.id + "1");
    return XMLData;
}

function hasXMLDataForGridName(gridId) {
    return (getObject(gridId + "1") != null);
}

function getXMLDataForGridName(gridId) {
    var XMLData = window[gridId + "1"];
    return XMLData;
}

function getXSLForGridName(gridId) {
    var XSL = eval(gridId + "1XSL");
    return XSL;
}

function getOrigXMLData(XMLData) {
    var origXMLData = eval("orig" + XMLData.id);
    return origXMLData;
}

function getOrigXMLDataId(XMLData) {
    var origXMLDataId = XMLData.id.substring(0, XMLData.id.length - 1);
    return origXMLDataId;
}

function isEmptyRecordset(recordSet) {
    var isEmptyRecSet = true;
    // row tag attributes are added to the end of the field collection.
    if (recordSet && recordSet.Fields) {
        for (var i = recordSet.Fields.Count - 1; i >= 0 && isEmptyRecSet == true; i--) {
            if (recordSet.Fields(i).name.toUpperCase() == "ID") {
                if (recordSet("ID").value.toUpperCase() != "NULL" && recordSet("ID").value.toUpperCase() != "-9999") {
                    isEmptyRecSet = false;
                }
            }
        }
    }
    return isEmptyRecSet;
}

function isFieldDefinedForGrid(gridId, fieldName) {
    var isFieldDefined = false;

    var XSL = getXSLForGridName(gridId);

    isFieldDefined = XSL.documentElement.selectNodes("//" + fieldName).length == 1;

    return isFieldDefined;
}

function isFieldExistsInRecordset(recordSet, fieldName) {
    var isFieldExists = false;
    // row tag attributes are added to the end of the field collection.
    if (recordSet && recordSet.Fields) {
        for (var i = recordSet.Fields.Count - 1; i >= 0 && isFieldExists == false; i--) {
            if (recordSet.Fields(i).name.toUpperCase() == fieldName.toUpperCase()) {
                isFieldExists = true;
                break;
            }
        }
    }
    return isFieldExists;
}

function displayGridNavButtons(XMLData) {
    //Current rows count
    var rowsCnt = eval(XMLData.id + '1').documentElement.selectNodes("//ROW[DISPLAY_IND = 'Y']").length;
    var gridNavButtonsTable = XMLData.id + '_gridNavButtonsIndTable';
    //Show navigation buttons and record count
    if (rowsCnt > XMLData.dataPageSize) {
        document.all(XMLData.id + '_HDRTBL').style.display = 'block';
        document.all(gridNavButtonsTable).style.display = 'block';
        document.all(XMLData.id + "_pageno").style.visibility = "visible";
        var pageCnt = Math.ceil(rowsCnt / XMLData.dataPageSize);
        var pageno = getTableProperty(XMLData, "pageno");
        document.all(XMLData.id + "_pageno").innerHTML = getMessage("label.search.records.pagination", new Array(rowsCnt, pageno, pageCnt));
    } else {
        var excelButtonDisplayed;
        try {
            // Find out if there is a JS variable called
            // "<theGridId>_excelButtonDisplayed" (there should be) and what
            // its value is.
            eval("excelButtonDisplayed = " + XMLData.id + "_excelButtonDisplayed;");
        }
        catch (e) {
            // If the JS var is not there (should not happen), act as if the
            // Excel button is not present.
            excelButtonDisplayed = false;
        }
        if (!excelButtonDisplayed) {
            // If there are no Excel buttons, then hide the nav buttons by hiding
            // the "<theGridId>_HDRTBL" table.
            document.all(XMLData.id + '_HDRTBL').style.display = 'none';
        }
        else {
            //Hide the navigation buttons
            document.all(gridNavButtonsTable).style.display = 'none';
            var showRowCntOnePage;
            eval("showRowCntOnePage=" + XMLData.id + "_showRowCntOnePage;");
            //Show/Hide records flag based on records count
            if (rowsCnt > 0 && showRowCntOnePage) {
                document.all(XMLData.id + "_pageno").style.visibility = "visible";
                var stringRecord = (rowsCnt == 1 ? getMessage("label.search.record.pagination", new Array(rowsCnt, 1, 1))
                    : getMessage("label.search.records.pagination", new Array(rowsCnt, 1, 1)));
                document.all(XMLData.id + "_pageno").innerHTML = stringRecord;
            } else {
                document.all(XMLData.id + "_pageno").style.visibility = "hidden";
            }
        }
    }
}
function isMSXML30()
{
    var e = new Error();
    var oXML = null;

    try
    {
        // Try to create an MSXML 3.0 object using the version-dependent
        // PROGID
        oXML = new ActiveXObject("MSXML2.DOMDocument.3.0");
        // Made it here, so this client has the "right" version of MSXML;
        // return true after cleaning up.
        oXML = null;
        return true;
    }
    catch (e)
    {
        // Client is probably using MSXML 2.5 or 2.6;
        // return false after cleaning up.
        oXML = null;
        return false;
    }
}
function encodeXMLChar(sValue) {
    return replace(replace(replace(replace(replace(sValue, "&", "&amp;"), "'", "&apos;"), '"', '&quot;'), "<", "&lt;"), ">", "&gt;");
}
function replace(strVal, text, by) {
    //in some pages of RM, strVal is not string type.
    if(dti.oasis.string.isEmpty(strVal)) {
        return "";
    }else if("string" == typeof strVal) {
        return strVal.replace(new RegExp(text, "g"), by)
    }else {
        return strVal;
    }
}

function readyStateReady(tbl) {
    debug('in readyStateReady('+tbl.id+')');
    begintime = new Date();

    // Retrieve all data from data island to Excel file.
    if (excelFlg) {
        excelFlg = false;
        otxt = generateDataFromGrid();
        postFormTextForCsvHtmlForExcel(otxt, ourl, odispType);
    }

    if (window.userReadyStateReady)
        userReadyStateReady(tbl);

    //hiliteSelectRow(elem);
    if (getTableProperty(tbl, "filtering")) {
        setTableProperty(tbl, "filtering", false);
        dti.oasis.grid._protected.resolveFilteringDeferredObj(tbl.id);
    }

    if (getTableProperty(tbl, "sorting")) {
        setTableProperty(tbl, "sorting", false);
        dti.oasis.grid.getProperty(tbl.id, "sortingDeferredObj").resolve();
    }

    setAllNumbersColorInGrid(tbl);
    setAllNumbersColorFields();

    var isScroll = getTableProperty(tbl, "scrollLastRowIntoView");
    if (isScroll == "Y") {
        scrollIntoViewWhenReady(tbl.id, tbl.rows.length - 1);
        setTableProperty(tbl, "scrollLastRowIntoView", "N");
    } else {
        adjustHeaderWhenScroll(getDivForGrid(tbl.id));
    }

    if (window.setFocusBackToOriginalField) {
        setFocusBackToOriginalField(tbl);
    }
    endtime = new Date();
}

function showNonEmptyTable(tbl) {
    begintime = new Date();
    // loop through the tables
    for (var k = 0; k <= tblCount; k ++) {
        // look for our particular table
        if (tblPropArray[k].id == tbl.id) {
            if (!tblPropArray[k].hasrows) {
                tblPropArray[k].hasrows = true;
                tbl.style.display = 'block';
                return true;
            }
            break;
        }
    }

    endtime = new Date();

    return false;

}

function hideEmptyTable(tbl) {
    begintime = new Date();
    // loop through the tables
    for (var k = 0; k <= tblCount; k ++) {
        // look for our particular table
        if (tblPropArray[k].id == tbl.id) {
            tblPropArray[k].hasrows = false;
            // hide the table
            tbl.style.display = 'none';

            break;
        }
    }
    endtime = new Date();

}
function first(grid) {
    begintime = new Date();
    grid.recordset.MoveFirst();
    if (grid.recordset.EOF || isEmptyRecordset(grid.recordset))
        return false;
    if (grid.recordset("ID").value == "-9999")
        return next(grid);
    endtime = new Date();
//    logDebug("Time spent in first:" + (endtime.getTime() - begintime.getTime()) + "ms");
    return true;
}
function next(grid) {
    begintime = new Date();
    // if eof, then return
    if (grid.recordset.EOF)
        return false;
    // next row
    try {
        grid.recordset.MoveNext();
    }
    catch(ex) {
        // ignore this error and continue
    }
    if (grid.recordset.EOF)
        return false;
    // if this is a dummy row, go next again
    if (grid.recordset("ID").value == "-9999")
        return next(grid);
    endtime = new Date();

    return true;
}
function disconnectFields(frm, gridId) {
    begintime = new Date();
    var elems = frm.elements;
    var datasrc = "#" + gridId + "1";
    for (var i = 0; i < elems.length; i++) {
        if (elems[i].dataSrc == datasrc) {
            elems[i].dataSrc = "#nada";
        }
    }
    endtime = new Date();

}
function reconnectFields(frm, gridId, doRefreshFields) {
    begintime = new Date();
    var elems = frm.elements;
    var datasrc = "#" + gridId + "1";
    for (var i = 0; i < elems.length; i++) {
        if (elems[i].dataSrc == "#nada")
            elems[i].dataSrc = datasrc;
    }
    if (doRefreshFields)
        refreshFields(gridId);
    endtime = new Date();

}
function refreshFields(gridId) {
    begintime = new Date();
    var row = getSelectedRow(gridId);
    if (row == "") {
        var grd = document.all(gridId + "1");
        if (first(grd))
            row = grd.recordset("ID").value;
    }
    if (row != "")
        selectRow(gridId, row)
    endtime = new Date();

}
function setItem(grid, col, val) {
    grid.recordset(col).value = val;
}
function getItem(grid, col) {
    return grid.recordset(col).value;
}

//-----------------------------------------------------------------------------
// Prepares all the data in the XML data island for being sent to Excel.
//-----------------------------------------------------------------------------
function prepareDataFromGrid() {
    var grid = ogrid;
    var startCol = ostartCol;
    var type = otype;

    var gridTbl = getSingleObject(grid);
    var gridXML = getSingleObject(grid + '1');
    var retVal = '';
    var gridRecCount = eval(gridTbl.id + '1.recordset.RecordCount');

    // Make the data page size the same as the record count.
    gridTbl.dataPageSize = gridRecCount;
    // Go to the first page.
    gridTbl.firstPage();
}

//-----------------------------------------------------------------------------
// It generates data from the XMLData for Excel files.
//-----------------------------------------------------------------------------
function generateRowDataFromXMLData(xmlData, columnArray) {
    var xmlValue = "";
    var columnInfo = columnArray.split(",,,");
    var columnName = columnInfo[0];
    var columnType = columnInfo[1];

    var fldMasked = isFieldMasked(columnName);
    if (fldMasked) {
        xmlValue = DEFAULT_FORMATTED_MASKED_VALUE;
    } else {
        if (columnType == "SELECT") {
            try {
                xmlValue = xmlData.recordset(columnName + "LOVLABEL").value;
            }
            catch (ex) {
                xmlValue = xmlData.recordset(columnName).value;
            }
        }
        else {
            xmlValue = xmlData.recordset(columnName).value;
        }

        if (columnType && columnType != "" && columnType.toLowerCase() == "checkbox") {
            if (xmlValue == "1" || xmlValue == "-1" || xmlValue.toUpperCase() == "Y" || xmlValue.toUpperCase() == "YES") {
                xmlValue = "on";
            }
            else if (xmlValue == "0" || xmlValue.toUpperCase() == "N" || xmlValue.toUpperCase() == "NO") {
                xmlValue = "off";
            }
        }
    }
    return  '"' + xmlValue.replace(/^\s+/g, '').replace(/\s+$/g, '').replace(/\r\n+/g, ':;:').replace(/\n+/g, ':;:') + '"';
}

//-----------------------------------------------------------------------------
// It generates data from the XMLData for Excel files.
//-----------------------------------------------------------------------------
function exportExcelFromXMLData(gridId, startColumn) {
    var xmlData = getXMLDataForGridName(gridId);
    first(xmlData);
    var vCSVTxt = "\n";
    while (!xmlData.recordset.EOF) {
        for (var j = 0; j < exportVisibleColumnIds.length; j++) {
            if (j > 0) {
                vCSVTxt += ",";
            }
            vCSVTxt += generateRowDataFromXMLData(xmlData, exportVisibleColumnIds[j]);
        }
        if (isFullyExport) {
            for (var k = 0; k < exportHideColumnIds.length; k++) {
                vCSVTxt += ",";
                vCSVTxt += generateRowDataFromXMLData(xmlData, exportHideColumnIds[k]);
            }
        }
        vCSVTxt += "\n";
        next(xmlData);
    }
    first(xmlData);
    postFormTextForCsvHtmlForExcel(vCSVTxt, ourl, odispType);
}

//-----------------------------------------------------------------------------
// This function is called by the function readyStateReady.
// It generates data from the grid for Excel files.
//-----------------------------------------------------------------------------
function generateDataFromGrid() {
    var grid = ogrid;
    var startCol = ostartCol;
    var type = otype;

    var gridTbl = getSingleObject(grid);
    var gridXML = getSingleObject(grid + '1');
    var retVal = '';
    var gridRecCount = eval(gridTbl.id + '1.recordset.RecordCount');

    // Go to the first page.
    gridTbl.firstPage();
    var origPage = 1;
    var numPages = 1;
    var lastPageWasVisited = false;
    var curRow = getSelectedRow(gridTbl.id);
    if (tblCount != 0 && tblPropArray.length >= 1) {
        // Loop through the table property array.
        for (var i = 0; i < tblCount; i++) {
            if (tblPropArray[i].id == gridTbl.id) {
                // For the table we are sending to CSV or Excel,
                // find out from the array what page we were on originally,
                // how many pages are in the table, and whether or not the
                // last page was visited.
                origPage = tblPropArray[i].pageno;
                numPages = tblPropArray[i].pages;
                lastPageWasVisited = tblPropArray[i].lastpagevisited;
            }
        }
    }

    if (type == 'CSV') {
        // Format the table as CSV for Excel.
        retVal = formatTableAsCsv(gridTbl, startCol);
    }
    else if (type == 'HTML') {
        // Get the table innerHTML to pass to Excel.
        retVal = formatTableInnerHtmlAsHtmlDoc(gridTbl);
    }
    // Set the datapagesize back to what it was.
    gridTbl.dataPageSize = origPageSize;
    if (origSelectedRow) {
        // Hilite the originally selected row
        hiliteRow(origSelectedRow);
    }

    if (lastPageWasVisited) {
        // The last page was visited.
        // Go to the last page.
        gotopage(gridTbl, 'L');
        if (origPage < numPages) {
            // If the page we were originally on is not the last page,
            // scroll backward until we get to that page.
            for (var j = numPages - 1; j >= origPage; j--) {
                gotopage(gridTbl, 'P');
            }
        }
    }
    else {
        // The last page has not been visited.
        // Go to the first page.
        gotopage(gridTbl, 'F');
        if (origPage >= 2) {
            // The page we were originally on is not the first page.
            if (origPage == numPages) {
                // The page we were originally on is the last page.
                // Go to the last page.
                gotopage(gridTbl, 'L');
            }
            else {
                // Scroll forward until we get to the original page.
                for (var k = 2; k <= origPage; k++) {
                    gotopage(gridTbl, 'N');
                }
            }
        }
    }
    if (curRow != "") {
        // Select the current row in the grid.
        selectRow(gridTbl.id, curRow);
    }
    return retVal;

}

function exportFromXML(gridId){
    return true;
}

//-----------------------------------------------------------------------------
// Formats the contents of a grid as either a CSV file or as an HTML file
// for Excel.
//-----------------------------------------------------------------------------
function formatGridAsCsvOrHtml(grid, startCol, type) {

    ogrid = grid;
    ostartCol = startCol;
    otype = type;
    var gridTbl = getSingleObject(grid);
    var retVal = '';
    var gridRecCount = eval(gridTbl.id + '1.recordset.RecordCount');
        if(isFullyExport || exportFromXML(grid)) {
            excelFlg = true;
            try {
                // Call the exportExcelFromXMLData function to export the excel from XML.
                exportExcelFromXMLData(grid, startCol);
            } catch(ex) {
                //ignore the error and set the excelFlg back.
                excelFlg = false;
            }
            excelFlg = false;
        } else {
            if(gridRecCount > gridTbl.dataPageSize) {
                // The record count of the grid is greater than the page size.
                // That means we have to manipulate the datapagesize of the grid table.
                // Get the original data page size and store it for later use.
                origPageSize = gridTbl.dataPageSize;
                // Save the selectedRow inorder to restore it later (after data generation)
                origSelectedRow = null;
                if(getTableProperty(gridTbl, "rowobject") != null) {
                    origSelectedRow = getTableProperty(gridTbl, "rowobject");
                    // Since the page and row pointer is going to change anyway, null out the rowobject
                    setTableProperty(gridTbl, "rowobject", null);
                }
                excelFlg = true;
                // Call the prepareDataFromGrid function to retrieve all the data in data XML island.
                prepareDataFromGrid();

            } else {
                // The record count of the grid is the same as the page size.
                if(type == 'CSV') {
                    // Format the table as CSV for Excel.
                    retVal = formatTableAsCsv(gridTbl, startCol);
                }
                else if(type == 'HTML') {
                    // Get the table innerHTML to pass to Excel.
                    retVal = formatTableInnerHtmlAsHtmlDoc(gridTbl);
                }
                otxt = retVal;
                postFormTextForCsvHtmlForExcel(otxt, ourl, odispType);
            }
        }
    return retVal;
}

function generateTableCellData(rowCells, columnArray){
    var columnInfo = columnArray.split(",,,");
    var columnName = columnInfo[0];
    for (var j = 0; j < rowCells.length; j++) {
        var objCell = rowCells[j];
        var vtxt = objCell.innerText;
        for (var idx = 0; idx < objCell.childNodes.length; idx++) {
            var childNode = objCell.childNodes[idx];

            var vType = childNode.nodeName;
            if(vType == "A"){
                childNode = childNode.childNodes[0];
            }
            var dataFld = childNode.dataFld;
            if (dataFld == columnName) {
                var vType = childNode.nodeName;
                switch (vType) {
                    case "INPUT":
                        if (childNode.type == 'checkbox') {
                                //Column contents
                                if (childNode.checked) {
                                    vtxt = 'on';
                                }
                                else {
                                    vtxt = 'off';
                                }
                        }
                        else {
                            vtxt = childNode.value;
                        }
                        break;
                    case "SELECT":
                        if (childNode.options.selectedIndex > -1) {
                            vtxt = childNode.options[childNode.options.selectedIndex].text;
                        } else {
                            vtxt = '';
                        }
                        break;
                    case "SPAN":
                        vtxt = childNode.innerHTML;
                        break;
                }
                return '"' + vtxt.replace(/^\s+/g, '').replace(/\s+$/g, '') + '"';
            }
        }
    }
    return '""';
}

//-----------------------------------------------------------------------------
// Formats the contents of a table as a CSV file.
//-----------------------------------------------------------------------------
function formatTableAsCsv(tbl, startCol) {
    var rowCount = tbl.rows.length;
    var vCSVTxt = "";
    if (startCol == null) {
        startCol = 0;
    }
    for (var i = 1; i < rowCount; i++) {
        var rowCells = getCellsInRow(tbl.rows(i));
        for (var j = 0; j < exportVisibleColumnIds.length; j++) {
            if (j > 0) {
                vCSVTxt += ",";
            }
            vCSVTxt += generateTableCellData(rowCells, exportVisibleColumnIds[j]);
        }
        if (isFullyExport) {
            for (var k = 0; k < exportHideColumnIds.length; k++) {
                vCSVTxt += ",";
                vCSVTxt += generateTableCellData(rowCells, exportHideColumnIds[k]);
            }
        }
        vCSVTxt += "\n";
    }
    return vCSVTxt;
}

//-----------------------------------------------------------------------------
// Submits a form for the purpose of sending a CSV file or HTML to Excel
// in-line or as an attachment.
//-----------------------------------------------------------------------------
function postFormTextForCsvHtmlForExcel(txt, actionUrl, dispType) {
    var formName = "__form_sending_csv_html_to_excel__";
    var aNewInnerHTML = '<form id="' + formName + '" method="post" >' +
        '<input type="hidden" name="textForFile" />' +
        '<input type="hidden" name="colNames" />' +
        '<input type="hidden" name="dispositionType" />' +
        '</form>';
    var alreadyHasForm = document.forms[formName] ? true : false;
    if (!alreadyHasForm) {
        document.body.insertAdjacentHTML("BeforeEnd", aNewInnerHTML);
    }
    document.forms[formName].elements["textForFile"].value = txt;
    document.forms[formName].elements["dispositionType"].value = dispType;
    document.forms[formName].elements["colNames"].value = exportColumnsNames;
    document.forms[formName].action = actionUrl;
    document.forms[formName].method = "post";
    document.forms[formName].target = "_blank";
    baseOnSubmit(document.forms[formName]);
}

//-----------------------------------------------------------------------------
// Sends grid contents to server to be opened in Excel as CSV.
//-----------------------------------------------------------------------------
function sendGridToServerAsExcelCsv(grid, url, dispType) {
    ourl = url;
    odispType = dispType;
    formatGridAsCsvOrHtml(grid, 0, "CSV");
}

//-----------------------------------------------------------------------------
// Saves grid contents as CSV to be opened in Excel;  called by button
// in grid (see Java class Gridhelper, method writeNavButtons).
//-----------------------------------------------------------------------------
function saveGridAsExcelCsv(gridId, dispType, fullExport) {
    showProcessingImgIndicator();
    if (fullExport) {
        isFullyExport = fullExport;
    } else {
        isFullyExport = false;
    }
    var tbl = getSingleObject(gridId);
    if (tbl.rows.length > 1) {
        exportColumnsNames = generateExportColumns(gridId, tbl, fullExport);

        var exportType = eval(gridId + "_getExportType()");
        if (exportType == 'CSV') {
            sendGridToServerAsExcelCsv(gridId, getCorePath() + "/gridToExcelCSV.jsp?gridId=" + gridId + "&date=" + new Date(), dispType);
        } else {
            var pageName = pageTitle + '(' + pageCode + ')';
            pageName = encodeURIComponent(pageName);
            sendGridToServerAsExcelCsv(gridId, getCorePath() + "/gridToExcelXLS.jsp?exportType=" + exportType + "&gridId=" + gridId + "&date=" + new Date() + "&pageName=" + pageName, dispType);
        }
    } else {
        alert(getMessage("core.export.excel.nodata"));
    }
    hideProcessingImgIndicator();
    return false;
}

//-----------------------------------------------------------------------------
// The export columns separate 4 parts. we should all export of it.
// 1. Visible Columns:_GH field configured in eAdmin and visible set to Yes. It has column labels.
// 2. OBR hide Columns:_GH field configured in eAdmin and visible set to Yes,but it hide by OBR. It has column labels.
// 3. Hide Columns:_GH field configured in eAdmin but visible set to No. It has column labels.
// 4. Other Columns:_GH field is not configured and no column labels configured.
//-----------------------------------------------------------------------------
function generateExportColumns(gridId, tbl, isFullyExport) {
    var thRowCells = getTHCellsInRow(tbl.rows(0));
    var tdRowCells = getCellsInRow(tbl.rows(1));
    var exportVisibleColumnNames = "";
    var exportHideColumnNames = "";
    var exportVisibleIndex = 0;
    var exportHideIndex = 0;
    exportVisibleColumnIds = [];
    exportHideColumnIds = [];
    for (var j = 0; j < thRowCells.length; j++) {
        var cellID = thRowCells[j].id;
        var columnInformation = cellID.substring(1);
        var columnLabel = thRowCells[j].innerText;
        if(columnLabel.indexOf(',')!=-1) {
            var tempLabel = columnLabel.split(',').join(':;:');
            columnLabel = tempLabel;
        }
        var childCell = tdRowCells[j].childNodes;
        var columnType = childCell[0].type;
        var nodeName = childCell[0].nodeName;
        if (nodeName != undefined) {
            if (nodeName.toUpperCase() == "SELECT") {
                columnType = "SELECT";
            }
        }
        columnInformation += ",,," + columnType;
        var isVisibleField = $(thRowCells[j]).is(":visible");
        if (isVisibleField) {
            // 1. Visible Columns:_GH field configured in eAdmin and visible set to Yes. It has column labels.
            exportVisibleColumnIds[exportVisibleIndex++] = columnInformation;
            if (cellID.indexOf("SELECT_IND") > -1) {
                exportVisibleColumnNames += "Select" + ",";
            } else {
                exportVisibleColumnNames += columnLabel + ",";
            }
        } else {
            // 2. OBR hide Columns:_GH field configured in eAdmin and visible set to Yes,but it hide by OBR. It has column labels.
            exportHideColumnIds[exportHideIndex++] = columnInformation;
            if (cellID.indexOf("SELECT_IND") > -1) {
                exportHideColumnNames += "Select" + ",";
            } else {
                exportHideColumnNames += columnLabel + ",";
            }
        }
    }

    var colNames = exportVisibleColumnNames;
    if (isFullyExport) {
        // 3. Hide Columns:_GH field configured in eAdmin but visible set to No. It has column labels.
        colNames += exportHideColumnNames;
        var hiddenColumnNames = eval(gridId + "_getHiddenColumnNames()");
        var hiddenColumnIds = eval(gridId + "_getHiddenColumnIds()");
        //Make sure the array doesn't have empty elements
        var hideColumnArray = hiddenColumnIds.split(",").filter(function(el) {return el.length != 0});
        for (var k = 0; k < hideColumnArray.length; k++) {
            exportHideColumnIds[exportHideIndex++] = hideColumnArray[k];
        }
        colNames += hiddenColumnNames;

        // 4. Other Columns:_GH field is not configured and no column labels configured.
        colNames += generateExportColumnsForNoConfigurationFields(gridId, exportHideIndex, hideColumnArray);
    }
    colNames = colNames.substring(0, colNames.length - 1);
    return colNames;
}

//-----------------------------------------------------------------------------
//  4. Other Columns:_GH field is not configured and no column labels configured.
//-----------------------------------------------------------------------------
function generateExportColumnsForNoConfigurationFields(gridId, exportHideIndex, hideColumnArray) {
    var hiddenColumnName = "";
    var XMLData = getXMLDataForGridName(gridId);
    //check the xmldata is empty.
    if (XMLData.recordset && XMLData.recordset.Fields) {

        //loop the node array to get the field which not exist in visible/hide array.

        for (var i = 0; i < XMLData.recordset.Fields.Count; i++) {
            var nodeName = XMLData.recordset.Fields(i).Name.toUpperCase();

            //exclude attributes.
            var isExcludeColumns = nodeName == "ID" || nodeName == "INDEX" || nodeName == "COL";
            //exclude DATE_ and URL_.
            isExcludeColumns = isExcludeColumns || nodeName.startsWith("DATE_") || nodeName.startsWith("URL_");
            //exclude indicator.
            isExcludeColumns = isExcludeColumns || nodeName == "UPDATE_IND" || nodeName == "DISPLAY_IND" || nodeName == "EDIT_IND";
            //exclude obr fields.
            isExcludeColumns = isExcludeColumns || nodeName == "OBR_ENFORCED_RESULT";
            //exclude others.
            isExcludeColumns = isExcludeColumns || nodeName == "$TEXT";
            if(isExcludeColumns){
                continue;
            }

            var isDisposed = false;
            var excludeColumnArray = [];
            //Copy the visible and hide column array in a new exclude column array.
            excludeColumnArray = excludeColumnArray.concat(exportVisibleColumnIds).concat(hideColumnArray);
            //loop the exclude column array
            for (var j = 0; j < excludeColumnArray.length; j++) {
                if (excludeColumnArray[j].toUpperCase() == nodeName) {
                    isDisposed = true;
                    break;
                }
            }
            //if not found in visible and hidden column array. Add it to exportHideColumnIds array.
            if (!isDisposed) {
                exportHideColumnIds[exportHideIndex++] = nodeName;
                hiddenColumnName += nodeName + ",";
            }
        }
    }
    return hiddenColumnName;
}

//-----------------------------------------------------------------------------
// Formats the inner HTML of a grid table as a standalone HTML document.
//-----------------------------------------------------------------------------
function formatTableInnerHtmlAsHtmlDoc(tbl) {
    var txt = "<HTML><BODY><TABLE>" + tbl.innerHTML + "</TABLE></BODY></HTML>";
    return txt.replace(/href=/g, 'nothing=');
}

//-----------------------------------------------------------------------------
// Sends grid contents as HTML to server to be opened in Excel.
//-----------------------------------------------------------------------------
function sendGridToServerAsExcelHtml(grid, url, dispType) {
    ourl = url;
    odispType = dispType;
    formatGridAsCsvOrHtml(grid, 0, "HTML");
}

//-----------------------------------------------------------------------------
// Saves grid contents as HTML to be opened in Excel;  called by button
// in grid (see Java class Gridhelper, method writeNavButtons).
//-----------------------------------------------------------------------------
function saveGridAsExcelHtml(gridId, dispType) {
    sendGridToServerAsExcelHtml(gridId, getCorePath() + "/gridToExcelHTML.jsp?date=" + new Date(), dispType);
    return false;
}

function beginAddMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    setTableProperty(table, "isAddMultipleRow", true);
}

function endAddMultipleRow(gridId) {
    var table = getTableForGrid(gridId);
    var testCode = 'getTableProperty(getTableForGrid(\"' + gridId + '\"), "isUserReadyStateReadyComplete")';

    var callbackCode =
        'selectRowById(\"' + gridId + '\", getTableProperty(getTableForGrid(\"' + gridId + '\"), "firstAddedMulitpleRowId"));'+
            'setTableProperty(getTableForGrid(\"' + gridId + '\"), "isAddMultipleRow", false);' +
            'setTableProperty(getTableForGrid(\"' + gridId + '\"), "firstAddedMulitpleRowId", null);'
    executeWhenTestSucceeds(testCode, callbackCode, 50);
}

/*
 Executes the callbackCode when the testCode returns true.
 Waits for iIntervalMillis between checks of testCode.
 */
function executeWhenTestSucceeds(testCode, callbackCode, iIntervalMillis) {
    //    alert('In executeWhenTestSucceeds with testCode: ' + testCode);
    //    alert('In executeWhenTestSucceeds with callbackCode: ' + callbackCode);
    if (eval(testCode)) {
        //        alert("ready, calling: " + callbackCode);
        eval(callbackCode);
    }
    else {
        var timeoutCode = 'executeWhenTestSucceeds(\'' + testCode + '\', \'' + callbackCode + '\', ' + iIntervalMillis + ');';
        //        alert('the testCode returned false; setting a timeout with: ' + timeoutCode)
        window.setTimeout(timeoutCode, iIntervalMillis);
    }
}

//-----------------------------------------------------------------------------
// select the row that matches the given ID
//-----------------------------------------------------------------------------
function selectRowById(gridId, rowId, rowIndex) {
    //alert("gridId="+gridId+", rowId="+rowId+", rowIndex="+rowIndex+", lastpagevisited="+(getTableProperty(getTableForGrid(gridId), "lastpagevisited")?"true":"false"));

    // do nothing if we have no row to select
    if (rowId == null || gridId == null) {
        //alert("Invalid ID(s) or emplty grid");
        return;
    }
    // hide the grid if the grid has no row.
    if (!getTableProperty(getTableForGrid(gridId), "hasrows")) {
        currentlySelectedGridId = gridId;
        // Hide the empty row in table.
        hideEmptyTable(getTableForGrid(gridId));
        hideGridDetailDiv(gridId);
        var functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            // Goto first record to make sure grid is not in EOF or BOF status.
            first(eval(gridId + "1"));
            pageEntitlements(true, gridId);
        }
        return;
    }

    var xmlTable = getTableForGrid(gridId);
    var XMLData = getXMLDataForGridName(gridId);
    var currentRowId = getSelectedRow(gridId);
    var isAddMultipleRow = getTableProperty(xmlTable, "isAddMultipleRow");
    // identify the row in the recordset if the calling function has not done so
    if ((rowIndex == undefined || rowIndex == null || rowIndex < 0) && (rowId != currentRowId || isAddMultipleRow)) {
        rowIndex = 0;
        first(XMLData);
        while (!XMLData.recordset.eof) {
            if (XMLData.recordset("ID").value == rowId) {
                //alert("found the row "+rowIndex);
                break;
            }

            rowIndex ++;
            next(XMLData);
        }
    }

    // target row doesn't exist, select first row
    if (rowIndex >= XMLData.recordset.recordcount){
        first(XMLData);
        rowIndex = 0;
        rowId = XMLData.recordset("ID").value;
    }

    // we did not find the row, re-select the current since we've gone through the entire recordset
    if (rowId == currentRowId && !isAddMultipleRow) {
        //alert("re-select the current row");
        first(XMLData);
        selectRow(gridId, currentRowId);
        return;
    }

    // found the target row, set it as seleted
    setSelectedRow(gridId, rowId);

    // get table properties
    var pages = getTableProperty(xmlTable, "pages");
    var currentPageno = getTableProperty(xmlTable, "pageno");
    var pagesize = getTableProperty(xmlTable, "pagesize");
    var nrec = getTableProperty(xmlTable, "nrec");
    var lastpagevisited = getTableProperty(xmlTable, "lastpagevisited");

    var offset = pagesize * pages - nrec;

    // calculate the page no of the row
    var pageno;
    if (lastpagevisited) {
        pageno = Math.floor((rowIndex + offset) / pagesize) + 1;
    }
    else {
        pageno = Math.floor(rowIndex / pagesize) + 1;
    }
    //alert("pageno="+pageno);

    // calculate the table row index
    var tableRowIndex;
    if (pageno == 1) {
        tableRowIndex = rowIndex % pagesize + 1;
    }
    else if (pageno == pages || lastpagevisited) {
        tableRowIndex = (rowIndex + offset) % pagesize + 1;
    }
    else {
        tableRowIndex = rowIndex % pagesize + 1;
    }
    //alert("tableRowIndex="+tableRowIndex);

    // select the row
    if (pageno == currentPageno) {
        postSelectRowById(gridId, rowId, tableRowIndex, null);
    }
    else {
        // disable the commonReadyStatereasyfunction while we navigating to the page
        var tablePropertyName = "isInSelectRowById";
        setTableProperty(xmlTable, tablePropertyName, true);

        // go to the desired page
        if (pageno == 1) {
            //alert("first page");
            setTableProperty(xmlTable, "isUserReadyStateReadyComplete", false);
            gotopage(xmlTable, 'F');
        }
        else if (pageno == pages) {
            //alert("last page");
            setTableProperty(xmlTable, "isUserReadyStateReadyComplete", false);
            gotopage(xmlTable, 'L');
        }
        else if (pageno < currentPageno) {
            for (var i = 0; i < currentPageno - pageno; i++) {
                //alert("prior page");
                setTableProperty(xmlTable, "isUserReadyStateReadyComplete", false);
                gotopage(xmlTable, 'P');
            }
        }
        else {
            for (var i = 0; i < pageno - currentPageno; i++) {
                //alert("next page");
                setTableProperty(xmlTable, "isUserReadyStateReadyComplete", false);
                gotopage(xmlTable, 'N');
            }
        }

        // invoke the post process to select row when the table is ready
        var testCode = 'getTableProperty(getTableForGrid(\"' + gridId + '\"), "isUserReadyStateReadyComplete")';
        var callbackCode = 'postSelectRowById(\"' + gridId + '\", ' + rowId + ', ' + tableRowIndex + ', \"' + tablePropertyName + '\");';
        executeWhenTestSucceeds(testCode, callbackCode, 50);
    }
}

function postSelectRowById(gridId, rowId, tableRowIndex, tablePropertyName) {

    var xmlTable = getTableForGrid(gridId);
    var elem = xmlTable.rows(tableRowIndex);
    setTableProperty(xmlTable, "rowobject", null);
    hiliteSelectRow(elem);
    scrollIntoViewWhenReady(gridId, tableRowIndex);

    selectRow(gridId, rowId);

    if (tablePropertyName)
        setTableProperty(xmlTable, tablePropertyName, false);
}

//----------------------------------------------------------------------------------
// Check if the field is in specific grid
//----------------------------------------------------------------------------------
function isFieldInGrid(gridId, fieldId) {
    var fld = getObject(fieldId);
    var rs = false;
    var gridDataSrc = "#" + gridId;
    if (fld && fld.dataSrc && fld.dataSrc == gridDataSrc && fld.dataFld) {
        rs = true;
    }
    else if (fld && fld.dataSrc == undefined && fld.length && fld.length > 0) {
        // There are duplicated field Ids, and the fld object is fields array
        // Only check first field in the array
        if (fld[0].dataSrc && fld[0].dataSrc == gridDataSrc && fld[0].dataFld) {
            rs = true;
        }
    }
    return rs;
}


//----------------------------------------------------------------------------------
// Opens Calendar in grid
//----------------------------------------------------------------------------------
function openCalendarInGrid(sFieldName) {

    var parentTrRow = findParentTrRow(window.event.srcElement);
    var myrow = parentTrRow.rowIndex - 1;

    var myobj = document.getElementsByName(sFieldName)[myrow];

    if (typeof myobj == "undefined") {
        calendar(sFieldName);
    }
    else
    {
        calendar(sFieldName + "[" + myrow + "]");
    }
}


//----------------------------------------------------------------------------------
// Utility Methods
//----------------------------------------------------------------------------------

//<ROW id="10446997" index="4" col="2,4,5,7,8,14,20,22,24,25" >

function getRowIdByRowIndex(XMLData, rowIndex) {
//    logDebug('!!! getRowIdByRowIndex -- rowIndex: '+rowIndex);
    var xpathExpression = '//ROW[@index =' + rowIndex + ']';
    var rowNode = XMLData.documentElement.selectSingleNode(xpathExpression);
//    logDebug('getRowIdByRowIndex -- rowNode.getAttribute("id"): '+rowNode.getAttribute("id"));
    var rowId = parseInt(rowNode.getAttribute("id"));
//    logDebug('getRowIdByRowIndex -- rowId: '+rowId);
    return rowId;
}

//----------------------------------------------------------------------------------
// Ported from JScript. Complement first(grid) and next(grid)
//----------------------------------------------------------------------------------
function last(grid) {
    begintime = new Date();
    grid.recordset.MoveLast();
    if (isEmptyRecordset(grid.recordset) || grid.recordset("ID").value == "-9999")
        return false;
    endtime = new Date();

    return true;
}
function previous(grid) {
    begintime = new Date();
    // if eof, then return
    if (grid.recordset.AbsolutePosition < 1)
        return false;
    // previous row
    try {
        grid.recordset.MovePrevious();
    }
    catch(ex) {
        // ignore this error and continue
    }

    // if this is a dummy row, go next again
    if (grid.recordset("ID").value == "-9999"){
        if (grid.recordset.AbsolutePosition < 1)
            return false;
        return previous(grid);
    }

    endtime = new Date();

    return true;
}

function hasNext(grid){
    return grid.recordset.AbsolutePosition<grid.recordset.RecordCount?true:false;
}

function hasPrevious(grid){
    return grid.recordset.AbsolutePosition>1?true:false;
}

function convertPctToNumber(val) {
    var temp = unformatPctStrVal(val);
    if (!isStringValue(temp)) {
        return temp;
    } else {
        var sign;
        if (temp.indexOf("+") > -1 || temp.indexOf("-") > -1) {
            sign = temp.substring(0, 1);
            temp = temp.substring(1, temp.length);
        } else {
            sign = "";
        }
        //Move the dot to left
        var beforeDot;
        var afterDot
        if (temp.indexOf(".") > 0) {
            beforeDot = temp.substring(0, temp.indexOf("."));
            afterDot = temp.substring(temp.indexOf(".") + 1, temp.length);
        } else {
            beforeDot = temp;
            afterDot = "";
        }
        switch (beforeDot.length){
            case 1:
                beforeDot = "0.0" + beforeDot;
                break;
            case 2:
                beforeDot = "0." + beforeDot;
                break;
            default:
                beforeDot = beforeDot.substring(0, beforeDot.length - 2) + "." + beforeDot.substring(beforeDot.length - 2, beforeDot.length);
                break;
        }
        return sign + beforeDot + afterDot;
    }
}

function reconnectAllFields(form) {
    logDebug("reconnectAllFields(form)");
    var beginTime = new Date();
    //var elems = form.elements;

    // select only grid related fields.
    var elems = $(".oasis_formfield").filter('[dataSrc!=""]').filter('[dataFld!=""]');
    for (var i = 0; i < elems.length; i++) {
        if (elems[i].dataSrc != undefined) {
            elems[i].dataSrc = elems[i].dataSrc;
        }

        // taking care of read-only fields
        var fieldName = elems[i].name;
        if (hasObject(fieldName + "LOVLABELSPAN")) {
            try {
                var fieldLOVLABELSPAN = getObject(fieldName + "LOVLABELSPAN");
                if (fieldLOVLABELSPAN.dataSrc != undefined) {
                    fieldLOVLABELSPAN.dataSrc = fieldLOVLABELSPAN.dataSrc;
                }
            }
            catch(ex) {
            }
        }
        else if (hasObject(fieldName + "ROSPAN")) {
            var fieldROSPAN = getObject(fieldName + "ROSPAN");
            if (fieldROSPAN.dataSrc != undefined) {
                fieldROSPAN.dataSrc = fieldROSPAN.dataSrc;
            }
        }
    }
    var endTime = new Date();
    logDebug("Time spent in reconnectAllFields:" + (endTime.getTime() - beginTime.getTime()) + "ms");
}

function getCellsInRow(row){
    var cells = $(row).find("td");
    return cells;
}

function getTHCellsInRow(row){
    var cells = $(row).find("th");
    return cells;
}

function processPlusMinusImages(spanId) {
    if (hasObject(spanId)) {
        var objs = getObject(spanId);
        if (isArray(objs)) {
            var len = objs.length;
            for (var i = 0; i < len; i++) {
                processPlusMinusImagesOneLine(objs[i]);
            }
        } else {
            processPlusMinusImagesOneLine(objs);
        }
    }
}

function processPlusMinusImagesOneLine(spanElement){
    // System should remove the image first.
    var linkElement = spanElement.parentElement;
    var imgElements = linkElement.getElementsByTagName("img");
    for (var i = 0; i < imgElements.length; i++) {
        linkElement.removeChild(imgElements[i]);
    }

    var value = linkElement.parentElement.innerText;
    if (value == "+") {
        var imageElement = document.createElement("img");
        imageElement.src = getCorePath() + "/images/plus.gif";
        imageElement.border = "0";
        spanElement.style.display = "none";
        linkElement.appendChild(imageElement);
    } else if (value == "-") {
        var imageElement = document.createElement("img");
        imageElement.src = getCorePath() + "/images/minus.gif";
        imageElement.border = "0";
        spanElement.style.display = "none";
        linkElement.appendChild(imageElement);
    }
}

function getGridIds() {
    var gridIds = [];

    var len = tblPropArray.length;
    for (var i = 0; i < len; i++) {
        gridIds[gridIds.length] = tblPropArray[i].id;
    }

    return gridIds;
}

function getGridIdToFireSelectFirstRowInGrid() {
    return gridIdToFireSelectFirstRowInGrid;
}

function addGridIdToFireSelectFirstRowInGrid(gridId) {
    var _gridIdToFireSelectFirstRowInGrid = getGridIdToFireSelectFirstRowInGrid();

    for (var i = 0; i < _gridIdToFireSelectFirstRowInGrid.length; i++) {
        if (_gridIdToFireSelectFirstRowInGrid[i] == gridId) {
            return;
        }
    }

    _gridIdToFireSelectFirstRowInGrid[_gridIdToFireSelectFirstRowInGrid.length] = gridId;
}

function isGridReadyStateIsCompleted(gridId) {
    return (dti.oasis.page.useJqxGrid() && getTableProperty(getTableForGrid(gridId), "isUserReadyStateReadyComplete")) ||
        (!dti.oasis.page.useJqxGrid() && getTableProperty(getTableForGrid(gridId), "isUserReadyStateReadyComplete") && getTableForGrid(gridId).readyState == "complete");
}

function setCurrentRecordValues(XMLDataIsland, recordValues, excludeEditDisplayUpdateInd) {
    for (var prop in recordValues) {
        var fieldName = prop;
        var fieldValue = recordValues[prop];

        var fieldNameUpper = fieldName.toUpperCase();

        // handle ID
        if (fieldNameUpper == "ID") {
            var origGridId = getOrigXMLDataId(XMLDataIsland);
            var origId = XMLDataIsland.recordset("ID").value;

            // reset ID in XMLDataIsland
            XMLDataIsland.recordset("ID").value = fieldValue;

            if (dti.oasis.page.useJqxGrid()) {
                XMLDataIsland.recordset(0).value = fieldValue;
            } else {
                XMLDataIsland.recordset(0).value = "javascript:selectRowWithProcessingDlg('" + origGridId + "','" + fieldValue + "');";
            }

            setTableProperty(getTableForXMLData(XMLDataIsland), "lastInsertedId", fieldValue);

            // reset ID in original XMLDataIsland
            var refNode = getOrigXMLData(XMLDataIsland).documentElement.selectSingleNode("//ROW[@id='" + origId + "']");
            if (refNode != null) {
                refNode.setAttribute("id", fieldValue);
            }
        }
        // handle other fields
        else if (isFieldExistsInRecordset(XMLDataIsland.recordset, "C" + fieldNameUpper)) {
            XMLDataIsland.recordset("C" + fieldName.toUpperCase()).value = fieldValue;

            if (isFieldExistsInRecordset(XMLDataIsland.recordset, "C" + fieldNameUpper + DISPLAY_FIELD_EXTENTION)) {
                var displayFieldValue;
                if (checknum(fieldValue)) {
                    displayFieldValue = fieldValue;
                }
                else {
                    displayFieldValue = formatDateForDisplay(fieldValue);
                }
                XMLDataIsland.recordset("C" + fieldName.toUpperCase() + DISPLAY_FIELD_EXTENTION).value = displayFieldValue;
            }
        }
        // handle _IND fields
        else if (isFieldExistsInRecordset(XMLDataIsland.recordset, fieldNameUpper) &&
            (!excludeEditDisplayUpdateInd &&
            (fieldNameUpper == "EDIT_IND" || fieldNameUpper == "UPDATE_IND" || fieldNameUpper == "DISPLAY_IND"))) {
            XMLDataIsland.recordset(fieldName.toUpperCase()).value = fieldValue;
        }
    }
    return;
}


/**
 * If the column is masked, the column will not have a dtiDatafld attribute. it will assume then
 * the "*******" is in a div tag.
 * @returns {boolean}
 */
function isFieldMasked(fieldName) {
    var bRc = false;
    var obj = getObjectById(fieldName);

    try {
        if (obj != null) {
            var dataFld = getDataField(obj);
            if (dataFld == "") {
                if (obj.innerHTML == DEFAULT_FORMATTED_MASKED_VALUE) {
                    bRc = true;
                }
            }
        }
    } catch(e) {

    }
    return bRc;
}

/**
 * Check if a grid column is visible.
 *
 * @returns {boolean}
 */
function isColumnVisible(gridId, colName) {
    if (dti.oasis.page.useJqxGrid()) {
        var columnConfig = dti.oasis.grid.config.getOasisColumnConfig(gridId, colName);
        return columnConfig != null && columnConfig.visible;
    }

    var colElement = $('table[id=' + gridId + ']').find('#H' + colName);
    if (colElement.length > 0) {
        return colElement.is(":visible");
    }

    return false;
}

//-----------------------------------------------------------------------------
// Set row style
// table: <tr> html object
// rowStyle: css style. i.e. color:blue
//-----------------------------------------------------------------------------
function setRowStyle(table){
    if (!dti.oasis.page.useJqxGrid()) {
        var XMLData = getXMLDataForTable(table);
        var rowStyleColumnName = "C" + table.id.toUpperCase() + "ROWSTYLE";
        if (isFieldExistsInRecordset(XMLData.recordset, rowStyleColumnName)) {
            for (var i = 1; i < table.rows.length; i++) {
                var rowStyle = "";
                var oRowStyle = getObject(rowStyleColumnName);
                if (oRowStyle) {
                    if (table.rows.length == 2) {
                        rowStyle = oRowStyle.innerText;
                    }
                    else {
                        rowStyle = oRowStyle[i - 1].innerText;
                    }
                }
                if (!isEmpty(rowStyle)) {
                    setRowStyleForOneRow(table.rows[i], rowStyle);
                }
            }
        }
    }
}

//-----------------------------------------------------------------------------
// Set row style to one grid row.
// row: <tr> html object
// rowStyle: css style. i.e. color:blue
//-----------------------------------------------------------------------------
function setRowStyleForOneRow(row, rowStyle) {
    var rowCells = getCellsInRow(row);
    for (var j = 0; j < rowCells.length; j++) {
        if (rowCells[j].childNodes.length > 0) {
            var tagName = rowCells[j].childNodes[0].tagName;
            if (!isEmpty(tagName) && (tagName == "SPAN" || tagName == "DIV")) {
                rowCells[j].childNodes[0].style.cssText = rowStyle;
            }
            // Handle links
            if (rowCells[j].childNodes[0].childNodes.length > 0) {
                var childTagName = rowCells[j].childNodes[0].childNodes[0].tagName;
                if (!isEmpty(childTagName) && (childTagName == "SPAN" || childTagName == "DIV")) {
                    rowCells[j].childNodes[0].childNodes[0].style.cssText = rowStyle;
                }
            }
        }
    }
}