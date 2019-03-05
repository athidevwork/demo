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

//*********************************************************************
// Edit functions (edits.js)
// Purpose: Functions to help in the form data entry
//*********************************************************************

/**
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------

 03/28/2007       GCC         Commented out call to numformat function in
 baseOnKeyPress function;  call in baseOnKeyDown
 takes care of call to numformat and extra call
 in baseOnKeyPress causes problems dealing with
 the hyphen for minus sign.

 05/30/2007       LMM         Added new function changeActionItemDisplay.

 06/28/2007       kshen       Added new function getSelectedText and added
 call to getSelected text to baseOnKeyPress prior
 to maxlength check.
 07/28/2007       MLM         Enhanced for user entry data formatting.
 09/27/2007       sxm         Added baseHandleOnPageBack()
 10/08/2007       Leo         Modify for issue 76606.
 11/29/2007       wer         Fixed fireAjax to use the currentValue as the field value if the name of this field is the loadIntoField
 01/23/2008       Kenney      Modified for issue 61364, modified the pop-up message when inputting an invalid date
 03/03/2008       James       Issue#79614 eClaims architectural enhancement to
 take advantage of ePolicy architecture
 Handling HREF is a new enhancement in WebWB
 03/07/2008       yhchen      remove the the logic to call maintainNoteImageForAllNoteFields in basePageOnReadyStateChange
 03/18/2008       wer         Added get/setMessage and get/setSysParmValue to edits.js
 03/18/2008       wer         Added isPageDataChanged()
 03/24/2008       Fred        Added code to support percentage field
 04/09/2008       wer         removed passing of DBPOOLID as request parameter
 07/11/2008       Fred        Added code to support time field
 10/08/2008       Jacky      Modified by Jacky to fix js error
 04/06/2009       wer         Fixed getObjectValue to return a comma-separated list of values for multi select list and multi-checkbox
 04/13/2009       mxg         82494: Changes to handle Date Format Internationalization
 05/06/2009       mxg         93544: Enchanced dateformat function to handle Date Format Internationalization
 06/12/2009       mlm         94509: Refactored the code to show/hide the tab item (<A>) instead of its parentElement (<LI>)
 08/10/2009       kshen       Added codes to handle noEntryReq field key down.
 09/17/2009       Fred        Added functions getMoneyObjNumericValue(),
 getPctObjNumericValue() and getNumObjNumericValue().
 09/23/2009       Fred        Issue 96884. Extend Internationalization to Date / Time fields
 11/18/2009       Kenney      enh to support phone format
 01/13/2010       Fred        Validte the pasted value for numeric field in function baseOnChange(iss102417)
 03/17/2010       Kenney      Modified enableDisableField to handle the field with find text type
 05/12/2010       fcb         107461: baseOnLogOut() added.
 06/25/2010       kshen       Modified firAjax to use Datafld attribute to get column value instead of using "C" + field name,
 because the form fields may have prefix.
 07/06/2010       syang       103797 - Modified alternate_colors() to call handleRowReadyStateReady if it exists.
 07/14/2010       bhong       107682 - Encode values when populate ajax url in fireAjax function to deal with ampersand(&) character.
 09/28/2010       joe         111971 - Add functions to support an enterable dropdown Windows field.
 10/15/2010       wfu         109875 - Change to support Chinese Date format.
 10/18/2010       wfu         109875 - Changed logic if to else if to support Chinese Date format and replaced hardcode string messages .
 10/19/2010       gzeng       112909 - Set isScrolling to false for special pages.
 10/25/2010       Kenney      104315 - Modified getFieldName to get the correct name of array object
 11/17/2010       mxg         112298 - Enchanced getLabel to handle "cust" fields for Customer WebWB
 11/30/2010       mxg         115110 - Enchanced togglePanel to accept HTML tags
 01/26/2011       clm         113498 - Add baseOnBodyKeyDown(), getAllFormFieldsForGrid(gridId),getLastEditableFormField(gridId),
 getFirstEditableFormField(gridId) and setFocusToFirstEditableFormField(gridId) function.
 02/18/2011       ldong       112568 - Enchanced to handle the new date format DD/MON/YYYY
 02/24/2011       kshen       Changed to use the notesImage and noNotesImage
 variables created in header.jsp for notes icon path.
 04/20/2011       syang       116715 - Modified hideShowField() to handle the note field.
 04/20/2011       fcb         119793 - AjaxUrls passed to postAjaxRefresh.
 05/10/2011       clm         118994 - call processEnterableSelectFieldIfExists after handleOnChange.
 05/12/2011       clm         118723 - change baseOnBodyKeyDown method.
 09/20/2011       mxg         Issue #100716: Display Type FORMATTEDNUMBER: Added jQuery Plugins wrappers and utility functions
 09/29/2011       wfu         125316 - Add/merge variables for focus/key down logic of currency, percent and phone type fields.
 10/25/2011       parker      for iss126392. fix js error in IE8
 10/25/2011       jdingle     125446 - Add textarea to check for backspace key
 11/09/2011       bhong       112837 - Removed logics which check processingDivPopup & processingDivPopupId objects and use JQuery API
 11/10/2011       wfu         126624 - Modified fireAjax to remove the logic related with isAllDependentFieldNotInGrid.
 11/25/2011       parker      for issue127578.throw js error when click 'save' button in claim loss coding page
 11/09/2011       mlm         127676 - Refactored to fire selectFirstRowInGrid() after window.document.readyState is complete.
 12/09/2011       bhong       128085 - Added function findParentTrRow
 12/29/2011       wfu         127293 - Modified isGridDataChanged to correct search condition for new added records.
 01/05/2012       clm         126620 - Add showGridDetailDiv into baseOnRowSelected function to ensure the bound form is displayed if the row is found.
 01/16/2012       clm         129148 - Add new function getAllSelectedOptionsAsUrlEncodingString.
 Change if condition to check if loadIntoField belongs to current selected grid.
 Add new encoding logic to currentValue and value in fireAjax.
 05/02/2012       mlm         133218 - Refactor to check for type attribute to determine whether the field is an array or not.
 06/01/2012       kshen       124505. Added method getSubElementByClassName.
 06/06/2012       kshen       124505. Renamed method getSubElementByClassName to getChildElementByClassName.
 08/01/2012       tcheng      135806 - Modified date unified format when it isn't match     sDate with dtMask.
 08/20/2012       tcheng      132554 - Modified setFocusToFirstField to filter readonly input field to make the scroll bar jump
 to top screen when pop up div window.
 Added isReadOnlyTextField to validate whether it is read only text field or not.
 11/21/2012       Parker      137533 - Fix panel hidden logic to ensure the required validation when panel hidden
 11/28/2012       tcheng      139272 - Modified fireAjax to decode currentValue for getting correct option description.
 12/13/2012       adeng       139886 - Modified baseOnUnloadForDivPopup() to clear the global variable when finished using it.
 12/28/2012       Parker      139202 - Fix OBR to set value of a dependant LOV when option is not available until after reloading the dependant LOV.
 12/28/2012       Parker      140834 - Create a new javascript function setObjectValue for OBR to set values.It should support radio, checkbox and multiple select.
 03/27/2013       kmv         142992 - Fix enableDisableField to work for multi select popups
 04/02/2013       jshen       142992 - Modified the handle graying out the image logic of enableDisableField function
 04/03/2013       htwang      141913 - Modified setFieldFocus function to allow the same field to regain focus after closing a pop dialog
 06/14/2013       parker      139877 - Modified resetPageViewStateCache function to ignore the cache reset for jumpToOptionList page
 07/12/2013       adeng       144614 - Modified baseOnChange() to process auto-finder when field is not empty.
 12/06/2013       Parker      148036 - Refactor maintainRecordExists code to make one call per subsystem to the database.
 12/06/2013       awu         143846 - 1. Modified baseOnRowSelected to call maintainValueForAllMultiSelectFields twice.
 2. Modified setMultiSelection to get the innerText instead of innerHtml.
 12/09/2013       Parker      148034 - Retrieve HTML to replace LOV when loading LOV via AJAX.
 01/03/2014       Parker      148027 - Reduce the call times of the processFieldDeps function.
 02/14/2014       mlm         135376 - Refactor to move commonEnableDisableGridDetailFields to framework.
 02/18/2014       mlm         150025 - Refactor to replace disabled fields with read-only fields.
 03/05/2014       mlm         152497 - Refactor to address defects caused by issue 150025.
 03/14/2014       mlm         152933 - Refactor to address defects caused by issue 150025 for custWebWB.
 03/19/2014       mlm         152754 - Refactor to address defects caused by issue 150025 -
 Make hidden fields as editable upon selecting editable row in the grid.
 04/15/2014       htwang      153729 - Correct the wrong method lengh to length, which caused enterable dropdown not work
 05/16/2014       wkong       154553 - For textarea on IE10, can't use getAttribute("maxlength") to get the attribute "maxlength".
 Need use attributes.maxLength.value to get this attribute.
 02/06/2015       kshen       157966   Fix the error that when selecting a record in first page after a record in last page was deleted.
 08/22/2014       ldong       156786 - Fix the issue for can't delete the phone number.
 08/28/2014       Elvin       156768 - add isWindowOpenerExists -- function moved to footer.jsp/footerpopup.jsp
 08/28/2014       Elvin       156444 - add getURLColumnName
 09/18/2014       bzhu        154239 - Added handleFocusOnLoad call to set focus on custom field.
 08/17/2015       dpang       164889 - Changed handleErrorNextStep to handle errors with grids which have more than two levels hierarchy.
 17/09/2015       zyu         165946 - Delete 'selectFirstRowInGrid(tbl.id)' in function showColoredNegativeNumberInGrid()
 and fix a bug in it.
 01/14/2015       iwang       168592 - Modified function getObjectValue to check if obj is undefined or not.
 07/13/2016       Elvin       Issue 177515: add commonGoToSource
 07/27/2016       kmv         176948 - Add stageCode to commonEnableDisableGridDetailFields
 01/12/2017       Elvin       Issue 182136: Velocity Integration - modify commonGoToSource
 04/07/2017       kshen       184686. Added common methods for showing/hiding elements.
 04/27/2017       mlm         185029 - Refactored to handle hide/show and enable/disable for multiSelectText and single checkbox fields.
 05/16/2017       mlm         185455 - Refactored to get fieldId correctly from array based field collection.
 06/06/2017       xnie        185709 - Modified commonGoToSource() to call getRiskSumB and then decide which risk tab
 page system should show to user. Risk Information page or Risk Summary Information page.
 06/06/2017       cesar       184074 - Modified fireajax() to check for undefined.
 07/14/2017       Elvin       Issue 186453: remove setting grid width to px value when resizing window and initGrid
                                            now they are set directly in gridDisplay.jsp
 10/12/2017       kshen       Grid replacement. Support firefox event. Changed handleAjaxMessages to skip empty lines in XML for HTML5.
 11/16/2017       Elvin       Issue 185882: modify commonGoToSource - add navigation to CLAIM_ENTRY_LOG
 12/04/2017       wrong       190014 - Modified commonOnSetButtonItalics function to add new logic to change cached
                                       record information instead of removing cache and adding new ones.
 12/12/2017       kshen       Grid replacement. Changed showColoredNegativeNumberInGrid to do nothing for jqxGrid
                              since jqxGrid would use cellsrenderer function to display cell.
 12/27/2017       kshen       Use jQuery.text() to get inner text for fix the error on an early version of firefox.
 01/12/2018       ylu         Issue 190718: check MASK_fieldid javascript variable,
 01/23/2018       dzhang      Grid replacement:check if the eval exist after handleOnLoad and commonOnLoad. in case page closed on these functions. add comments.
 03/19/2018       cesar       189605 - craeted a new object  dti.csrf to be used mostly in asynchttp.js.
 04/13/2018       cesar       192259 - created dti.inpututils to handle event.keyCode
 04/24/2018       cesar       189872 - created dti.gridutils to handle browser page resize
 05/18/2018       cesar       193003 - modified setWindowLocation() to include the csrf token
 05/24/2018       mlm         193214 - Replaced .attributes('dataSrc') and .attributes('dataFld') with getDataSrc() and
                                       getDataField() respectively.
 06/13/2018       mxm         191837 - FM Browser Independence
 06/25/2018       mxm         191837 - Grid replacement - Check if the object found is a data island object as well,
                                       to avoid returning the global variable instead of an object
 08/16/2018       wrong       192865 - Modified maintainNote() to add getOpenCtxOfDivPopUp before open popups.
 08/28/2018       wrong       195447 - Modified maintainNote() to change functionExists expression.
 09/03/2018       dpang       194835 - Change to handle "CU" field the same as "CF" field.
 09/10/2018       dpang       194134 - Change syncSpanToField to check if exists field with the name 'field.name+ROSPAN1'
 09/14/2018       dzhang      194134 - Add try catch when handling current Selected Note Field Id.
 09/14/2018       mgitelm     193649 - Add handling for buttons with duplicate IDs
 10/17/2018       cesar       194821 - Refactor getSingleObject() for performance.
 10/17/2018       cesar       196161 - Modified formatNumberFormatted() to reset color
 10/17/2018       cesar       194821 - getSingleObject() - revert changes barck.
 10/19/2018       fhuang      196051 - Modified baseOnChange() to convert to upperCase and lowerCase for text and textarea field only.
 10/22/2018       ylu         196579 - count textarea's actual maxlength for IE11 due to special char, backward with IE8
 10/24/2018       cesar       196687 - Refactor functions calling hasObject()/getObjects().
 11/12/2018       wreeder     196160 - Support resolving the loadingDeferredObj after readyStateReady() is called
                                     - Wait until all visible grids are done loading before calling setFocusToFirstField()
                                     - Add setFocusToFirstFieldInGrid() and handleAjaxJsonMessages()
 11/16/2018       kshen       196922 - On a page, there could be several span with id noteFieldIdList.
                                     - Changed the function maintainNoteImageForAllNoteFields to use jQuery to get noteFieldIdList
                                     - so the function can be run on all browsers.
 12/07/2018       dzhang      196632 - Change to use getElementDatatype to get datatype value
 12/07/2018       jdingle     195825 - Modified autoHideTR to prevent stack overflow in windows 10.
 12/11/2018       kshen       194134 - Check if key event success before convert keycode to upper or lower case.
 */
/*
 *
 TODO: REPLACE EXISTING VALUES IN BASEXXX FUNCTIONS
 *
 */
var DATATYPE_FINDER = "FD";
var DATATYPE_DATE = "DT";
var DATATYPE_TIME = "TM";
var DATATYPE_NUMBER = "NM";
var DATATYPE_TEXT = "ST";
var DATATYPE_UPPERCASE_TEXT = "UT";
var DATATYPE_LOWERCASE_TEXT = "LT";
var DATATYPE_CURRENCY = "CU";
var DATATYPE_CURRENCY_FORMATTED = "CF";
var DATATYPE_PERCENTAGE = "PT";
var DATATYPE_PHONE = "PH";

var FIELDTYPE_SINGLESELECT = "select-one";
var FIELDTYPE_MULTISELECT = "select-multiple";
var FIELDTYPE_RADIO = "radio";
var FIELDTYPE_CHECKBOX ="checkbox";
var FIELDTYPE_TEXT = "text";
var FIELDTYPE_TEXTAREA = "textarea";

var DATE_MASK = "mm/dd/yyyy";
//Added for Date Format Internationalization
var DATE_MASK_INTERNATIONAL = 'dd/mm/yyyy';
if (typeof localeDataMask != 'undefined' && localeDataMask != null) {
    DATE_MASK_INTERNATIONAL = localeDataMask.toLowerCase();
}
var PHONE_MASK;
var doHilite = true;

var isChanged = false;
var httpreq = null;
var priorURL = new Array();
var lookupxmldoc = null;
var currentHttpIdx = 0;
var enableJavascriptLogging = false;
var currentAutoFinderFieldName="";
var valueBeforeFocus="";
var valueBeforeKeyDown="";

var userActivityTimer;
var isPageEntitlementInProgress = false;
var gridIdToFireSelectFirstRowInGrid = [];

var skipUnloadPageB = false;
var isProcessFieldDeps = true;

//definition the global fields for italics business.
var italicsCacheValueArray = new HashTable();
var italicsFieldIdList = null;
var italicsArrayInPageLevel = new Array();
var italicsCurrentGridName = "";

window.onbeforeunload = function(event) {
    event = fixEvent(event);

    if (!skipUnloadPageB) {
        baseOnBeforeUnload(event);
    } else {
        skipUnloadPageB = false;
    }
};

function skipUnloadPageForCurrentAndIFrame() {
    skipUnloadPageB = true;
    for (var i = 0; i < window.frames.length; i ++) {
        var iframeElement = window.frames[i];
        if (iframeElement.window.skipUnloadPageForCurrentAndIFrame) {
            iframeElement.window.skipUnloadPageForCurrentAndIFrame();
        }
    }
}



// Get checked value from radio button.

function getRadioButtonValue(radio)
{
    if (radio.length == undefined && radio.checked)
        return radio.value;

    for (var i = 0; i < radio.length; i++)
    {
        if (radio[i].checked)
            return radio[i].value;

    }
    return "";
}
function getLabel(field, index) {
    var label;
    var tmpFldLabel = getObject(fieldnm + 'FLDLABEL')[index];
    if (tmpFldLabel) {
        if (tmpFldLabel.innerText.substring(tmpFldLabel.innerText.length - 1) == ":") {
            // If the text ends with ":", trim off the colon.
            label = tmpFldLabel.innerText.substring(0, tmpFldLabel.innerText.length - 1);
        }
        else {
            label = tmpFldLabel.innerText;
        }
    }
    else {
        label = field[index].name;
    }
    if (label == "" || label == null) { // use title as a final resort
        label = (field[index].title) ? field[index].title : "";
    }
    return label;
}

function getLabel(fieldnm) {
    var label;
    //logDebug("fieldnm before: "+fieldnm);
    if (typeof fieldnm == "string") {
        if (fieldnm.endsWith(DISPLAY_FIELD_EXTENTION))
            fieldnm = fieldnm.substring(0,fieldnm.indexOf(DISPLAY_FIELD_EXTENTION));
        //For "cust" fields
        if (fieldnm.startsWith("cust")){
            //Find a matching Field to safegard against a "non-cust" field
            //that starts with "cust"
            var fieldnmBase = "base"+fieldnm.substring(4,fieldnm.length);
            if(!isUndefined(getObject(fieldnmBase))){
                fieldnm = fieldnmBase;
            }
        }
    } else {
        if (!isUndefined(fieldnm.name)) {
            if (fieldnm.name.endsWith(DISPLAY_FIELD_EXTENTION))
                fieldnm = getObject(fieldnm.name.substring(0, fieldnm.name.indexOf(DISPLAY_FIELD_EXTENTION)));
            //For "cust" fields
            if (fieldnm.name.startsWith("cust")) {
                //Find a matching Field to safegard against a "non-cust" field
                //that starts with "cust"
                var fieldnmBase = "base" + fieldnm.name.substring(4, fieldnm.name.length);
                if (!isUndefined(getObject(fieldnmBase))) {
                    fieldnm = fieldnmBase;
                }
            }
        }
    }
    //logDebug("fieldnm after: "+fieldnm);
    // modified by Jacky to fix js error

    var tmpFldLabel;
    //        logDebug("fieldnm.name before: "+fieldnm.name);
    //        if ((fieldnm.name).endsWith(DISPLAY_FIELD_EXTENTION))
    //            fieldnm.name = (fieldnm.name).substring(0,fieldnm.indexOf(DISPLAY_FIELD_EXTENTION));
    //        logDebug("fieldnm.name after: "+fieldnm.name);
    if (fieldnm.name) {
        tmpFldLabel = getObject(fieldnm.name + 'FLDLABEL');
    }
    else {
        tmpFldLabel = getObject(fieldnm + 'FLDLABEL');
    }
    if (tmpFldLabel) {
        if (tmpFldLabel.innerText.substring(tmpFldLabel.innerText.length - 1) == ":") {
            // If the text ends with ":", trim off the colon.
            label = tmpFldLabel.innerText.substring(0, tmpFldLabel.innerText.length - 1);
        }
        else {
            label = tmpFldLabel.innerText;
        }
    }
    if (label == "" || label == null) {  // use title as a final resort
        label = (getObject(fieldnm) && getObject(fieldnm).title) ? getObject(fieldnm).title : "";
    }
    return label;
}

/**
 * get the element object reference
 * @param objid     id and name are supported
 */
function getObject(objid, returnSingleObject) {
    var foundObject = null;
    if (typeof objid == "string") {
        var result = document.getElementsByName(objid);
        if (result.length == 0) {
            if (isIE8Mode()) {
                foundObject = undefined;
            } else {
                foundObject = getObjectById(objid);

                if (foundObject == null) {
                    if (window[objid] && window[objid].isOasisDataIsland) {
                        foundObject = window[objid];
                    }
                }

                if (foundObject == null) {
                    foundObject = undefined;
                }
            }
        } else if (result.length == 1 || (returnSingleObject && result.length>1)) {
            foundObject = result[0];
        } else {
            foundObject = result;
        }
    } else {
        foundObject = objid;
    }
    return foundObject;
}

function getChildElementByClassName(obj, elementName, className) {
    return $(obj).find(elementName + "." + className)[0];
}

/**
 * get the first object matched
 * @param objid     id and name are supported
 */
function getSingleObject(objid) {
    var foundObject = getObject(objid, true);
    return foundObject;
}

function getObjectById(objectId) {
    var foundObject = document.getElementById(objectId);
    return foundObject;
}

function hasObjectId(objectId) {
    var exists = false;
    if (objectId != ""){
        var obj = getObjectById(objectId);
        if (obj != null) {
            exists = true;
        }
    }
    return exists;
}

function getSingleObjectByName(objectName) {
    var results = null;
    var elements = document.getElementsByName(objectName);
    if (elements.length > 0) {
        results = elements[0];
    } else {
        results = null;
    }
    return results;
}

function getObjectsByName(objectName) {
    var results = document.getElementsByName(objectName);
    return results;
}
function hasObjectName(objectName){
    var exists = false;
    var objs = getObjectsByName(objectName);
    if (objs != null && objs.length > 0) {
        exists = true;
    }
    return exists;
}

//--------------------------------------------------------
// If Given an input element in the grid, find the corresponding
// input element in the same row given its name.
//--------------------------------------------------------
function getOriginalObject(obj, originalName) {
    var objlist = getObject(obj.name);
    // if length property exists & options does not, then this is an array of objects
    if (objlist.length && !objlist.options) {
        // find the current object in the array, then select the other object in the row
        for (var i = 0; i < objlist.length; i++) {
            if (objlist[i] == obj)
                return getObject(originalName)[i];
        }
        return null;
        // this is a problem right here.
    } else    // not an array, simply find the other object
        return getObject(originalName);
}

/**
 *
 * @param objid     id and name are supported
 */
function hasObject(objid) {
    var obj = getSingleObject(objid);
    if (obj) {
        return true;
    } else {
        return false;
    }
}

/**
 Returns the value of the object specified by the given object id.
 If the object is an array, the selected/checked/first value is returned.
 */
function getObjectValue(objid) {
    var value;
    var obj = getObject(objid);
    if (typeof obj != 'undefined') {
        if (isArray(obj) && obj.length > 0) {
            if (obj.type) {
                if (obj.type == "select-one" && obj.selectedIndex != -1) {
                    // if it is a select list, let us get the selectedIndex
                    value = obj[obj.selectedIndex].value;
                }
                else if (obj.type == "select-multiple") {
                    // If the type is multi select list, eturn a comma-separated list
                    var sep = "";
                    value = "";
                    for (var i = 0; i < obj.length; i++) {
                        if (obj[i].selected) {
                            value += sep + obj[i].value;
                            sep = ",";
                        }
                    }
                }
            }
            else if (obj[0].type) {
                if (obj[0].type == "radio") {
                    // if it's a radio button, get the checked
                    for (var i = 0; i < obj.length; i++) {
                        if (obj[i].checked) {
                            value = obj[i].value;
                            break;
                        }
                    }
                }
                else if (obj[0].type == "checkbox") {
                    // If the type is checkbox and it's an array, this is a multi-checkbox if it's a muti-checkbox.
                    // Return a comma-separated list
                    var sep = "";
                    value = "";
                    for (var i = 0; i < obj.length; i++) {
                        if (obj[i].checked) {
                            value += sep + obj[i].value;
                            sep = ",";
                        }
                    }
                }
                else if (obj[0].type == "select-one" && obj.selectedIndex != -1) {
                    var firstObj = obj[0];
                    // if it is a select list, let us get the selectedIndex
                    value = firstObj[firstObj.selectedIndex].value;
                }
                else if (obj[0].type == "select-multiple") {
                    var firstObj = obj[0];
                    // If the type is multi select list, eturn a comma-separated list
                    var sep = "";
                    value = "";
                    for (var i = 0; i < firstObj.length; i++) {
                        if (firstObj[i].selected) {
                            value += sep + firstObj[i].value;
                            sep = ",";
                        }
                    }
                }
                else {
                    // if the item is not a select list, radio or checkbox control,
                    // it must be a field defined more than once on the page. Return the value of the first field
                    value = obj[0].value;
                }
            }
        }
        else {
            value = obj.value;
            if (obj.type) {
                if (obj.type == "checkbox") {
                    value = obj.checked ? obj.value : "";
                }
            }
        }
    }
    return value;
}

/**
 Set the value of the object specified by the given object id.
 */
function setFieldValue(objid, objectValue, fireOnchange) {
    var success = false;
    var obj = getObject(objid);
    if (obj) {
        if (getObjectValue(objid) == objectValue) {
            success = true;
        } else {
            if (isArray(obj) && obj.length > 0) {
                if (obj.type) {
                    // select or multiple select
                    success = setSingleFieldValue(obj, objectValue, fireOnchange);
                } else if (obj[0].type) {
                    //for radio or multiple checkbox
                    if (obj[0].type == FIELDTYPE_RADIO || obj[0].type == FIELDTYPE_CHECKBOX) {
                        for (var i = 0; i < obj.length; i++) {
                            setSingleFieldValue(obj[i], objectValue, fireOnchange);
                        }
                        success = true;
                    } else {
                        // if the field is duplicate, only set value on the first one
                        success = setSingleFieldValue(obj[0], objectValue, fireOnchange);
                    }
                }
            } else {
                // input, single radio, single checkbox
                success = setSingleFieldValue(obj, objectValue, fireOnchange)
            }
        }
    }
    return success;
}

function setSingleFieldValue(object, objectValue, fireOnchange) {
    var isChanged = false;
    var isSuccess = true;
    switch (object.type) {
        case FIELDTYPE_SINGLESELECT:
            var orignalValue = object.value;
            object.value = objectValue;
            if (object.value == objectValue) {
                isChanged = true;
            } else {
                object.value = orignalValue;
                isSuccess = false;
            }
            break;
        case FIELDTYPE_MULTISELECT:
            var valueArray = objectValue.split(",");
            for (var i = 0; i < object.options.length; i++) {
                var isInValues = false;
                for (var j = 0; j < valueArray.length; j++) {
                    if (object.options[i].value == valueArray[j]) {
                        isInValues = true;
                        if (!object.options[i].selected) {
                            object.options[i].selected = true;
                            isChanged = true;
                        }
                        break;
                    }
                }
                if (!isInValues && object.options[i].selected) {
                    object.options[i].selected = false;
                    isChanged = true;
                }
            }
            processMultiSelectFieldIfExists();
            break;
        case FIELDTYPE_RADIO:
            if (object.value == objectValue) {
                if (!object.checked) {
                    isChanged = true;
                    object.checked = true;
                }
            } else if (object.checked) {
                isChanged = true;
                object.checked = false;
            }
            break;
        case FIELDTYPE_CHECKBOX:
            var valueArray = objectValue.split(",");
            var isInValues = false;
            for (var j = 0; j < valueArray.length; j++) {
                if (object.value == valueArray[j]) {
                    isInValues = true;
                    if (!object.checked) {
                        isChanged = true;
                        object.checked = true;
                    }
                    break;
                }
            }
            if (!isInValues && object.checked) {
                isChanged = true;
                object.checked = false;
            }
            break;
        default :
            isChanged = true;
            object.value = objectValue;
    }
    syncToReadonlyFields();
    if (isChanged && fireOnchange) {
        dispatchElementEvent(object, "change");
    }
    return isSuccess;
}

function formatFieldForDisplay(objid){
    //logDebug('formatFieldForDisplay(' + objid + ')');
    var obj = getObject(objid);
    var datatype = dti.oasis.ui.getElementDataType(obj);
    if (datatype != null && datatype != "") {
        switch (datatype) {
            case DATATYPE_DATE:
                return formatDateForDisplay(obj.value);
                break;
            case DATATYPE_TIME:
                return formatDateTimeForDisplay(obj.value);
                break;
            case DATATYPE_PHONE:
                return formatPhoneNumberForDisplay(obj.value);
                break;
            case DATATYPE_NUMBER:
            case DATATYPE_TEXT:
            case DATATYPE_UPPERCASE_TEXT:
            case DATATYPE_LOWERCASE_TEXT:
            case DATATYPE_CURRENCY:
            case DATATYPE_CURRENCY_FORMATTED:
            case DATATYPE_PERCENTAGE:
            case DATATYPE_FINDER:
                logDebug('NOT A DATE');
                break;
            default :
                logDebug('NONE OF THE ABOVE');
        }
    } else {
        logDebug('DATATYPE IS NULL OR EMPTY');
    }
}


function getFieldDataType(objid){
    //logDebug('getFieldDataType(' + objid + ')');
    var obj = getObject(objid);
    var datatype = dti.oasis.ui.getElementDataType(obj);
    if (datatype != null && datatype != "") {
        switch (datatype) {
            case DATATYPE_DATE:
                return DATATYPE_DATE;
                break;
            case DATATYPE_TIME:
                return DATATYPE_TIME;
                break;
            case DATATYPE_NUMBER:
                return DATATYPE_NUMBER;
                break;
            case DATATYPE_TEXT:
                return DATATYPE_TEXT;
                break;
            case DATATYPE_UPPERCASE_TEXT:
                return DATATYPE_UPPERCASE_TEXT;
                break;
            case DATATYPE_LOWERCASE_TEXT:
                return DATATYPE_LOWERCASE_TEXT;
                break;
            case DATATYPE_CURRENCY:
                return DATATYPE_CURRENCY;
                break;
            case DATATYPE_CURRENCY_FORMATTED:
                return DATATYPE_CURRENCY_FORMATTED;
                break;
            case DATATYPE_PHONE:
                return DATATYPE_PHONE;
                break;
            case DATATYPE_PERCENTAGE:
                return DATATYPE_PERCENTAGE;
                break;
            case DATATYPE_FINDER:
                return DATATYPE_FINDER;
                break;
            default :
                logDebug('NONE OF THE ABOVE');
                return "";
        }
    } else {
        logDebug('DATATYPE IS NULL OR EMPTY');
        return "";
    }
}

function getFieldName(formFieldOrObject) {
    try {
        if (formFieldOrObject.name == undefined)
            if (isArray(formFieldOrObject)) {
                return formFieldOrObject[0].name;
            }
        return formFieldOrObject.name;
    } catch(ex) {
        if (isArray(formFieldOrObject)) {
            return formFieldOrObject[0].name;
        }
    }
}

/**
 Returns a comma-separated String containing all selected options of the given multi-select field.
 */
function getAllSelectedOptionsAsString(objid, delim) {
    var obj = getObject(objid);
    var len = obj.length;
    var options = obj.options;
    var sep = "";
    if (delim == null) {
        delim = ",";
    }
    var selectedOptions = "";
    for (var i = 0; i < len; i++) {
        if (options[i].selected) {
            selectedOptions += sep + options[i].value;
            sep = delim;
        }
    }
    return selectedOptions;
}

/**
 Returns a comma-separated Encoding String containing all selected options of the given multi-select field.
 */
function getAllSelectedOptionsAsUrlEncodingString(objid, delim){
    var obj = getObject(objid);
    var len = obj.length;
    var options = obj.options;
    var sep = "";
    if (delim == null) {
        delim = ",";
    }
    var selectedOptions = "";
    for (var i = 0; i < len; i++) {
        if (options[i].selected) {
            selectedOptions += sep + encodeURIComponent(options[i].value);
            sep = delim;
        }
    }
    return selectedOptions;
}

/**
 Returns a comma-separated String containing all selected options' description of the given multi-select field.
 */
function getAllSelectedOptionsDescriptionAsString(objid, delim) {
    var obj = getObject(objid);
    var len = obj.length;
    var options = obj.options;
    var sep = "";
    if (delim == null) {
        delim = ",";
    }
    var selectedOptions = "";
    for (var i = 0; i < len; i++) {
        if (options[i].selected) {
            selectedOptions += sep + $(options[i]).text();
            sep = delim;
        }
    }
    return selectedOptions;
}

/**
 Add a hidden input field to the specified form, if one does not already exist,
 setting the id and name of the field to the specified fieldName.
 If the formIndex is not specified, or is null, it is defaulted to 0.

 Returns the input field.
 */
function addInputFormField(fieldName, formIndex) {
    var inputField = getObject(fieldName);
    if (isUndefined(inputField)) {
        formIndex = formIndex ? formIndex : 0;
        inputField = document.createElement("input");
        inputField.type = "hidden";
        inputField.name = fieldName;
        inputField.id = fieldName;
        document.forms[formIndex].appendChild(inputField);
    }
    return inputField;
}

/**
 Set the value of the specified form field to the given value.
 If the form field does not exist, it is created as a hidden input field.
 If the formIndex is not specified, or is null, it is defaulted to 0.

 Returns the input field.
 */
function setInputFormField(fieldName, value, formIndex, fireOnchange) {
    var inputField = getObject(fieldName);

    if(isUndefined(inputField)) {
        formIndex = formIndex ? formIndex : 0;
        inputField = addInputFormField(fieldName, formIndex);
    }
    setObjectValue(inputField, value, fireOnchange);
    return inputField;
}

function setObjectValue(objid, value, fireOnchange) {
    var object = getObject(objid);
    object.value = value;
    if (fireOnchange === true) {
        dispatchElementEvent(object, "change");
    } else {
        if (dti.oasis.page.useJqxGrid()) {
            dti.oasis.grid.syncDetailFieldToGrid(objid);
        }
    }
    syncSpanToField(object);
}

function setRadioval(radioname, val) {
    radioobj = getObject(radioname)

    if (radioobj) {
        var len = radioobj.length
        for (i = 0; i < len; i++) {
            if (radioobj[i].value == val) {
                radioobj[i].checked = true
                break;
            }
        }
    }
}

function getRadioval(radioname) {
    var counter,noval = ""
    radioobj = getObject(radioname)
    if (radioobj) {
        var len = radioobj.length
        for (counter = 0; counter < len; counter++) {
            if (radioobj[counter].checked == true) {
                break;
            }
        }
        if (counter < len) {
            return radioobj[counter].value
        }
        else {
            return noval;
        }

    }
}

function setTextAreaval(areaname, val) {
    var textobj = getObject(areaname)
    if (textobj) textobj.value = val
}

function getCheckBoxval(cbname) {
    var cbobj = getObject(cbname)
    if (cbobj) {
        return cbobj.value
    }
}

function isChecked(cbname) {
    var cbobj = getObject(cbname)
    if (cbobj) {
        return cbobj.checked
    }
}

function phoneformat(event) {
    event = event || window.event;

    phonemaskclear(event);

    var evt = event ;
    if (isOkKey(evt))
        return true;
    var code = evt.keyCode;
    if (code >= 96 && code <= 105)
        code -= 48;

    if (code < 48 || code > 57 || evt.shiftKey) {
        event.cancelBubble = true;
        event.returnValue = false;
        return false;
    }
    var val = evt.srcElement.value;
    if (val.length == 3 && (val.indexOf("(") == -1))  evt.srcElement.value = "(" + val + ")";
    if (val.length == 4 && (val.indexOf("(") != -1)) evt.srcElement.value = val + ")";
    if (val.length == 8) evt.srcElement.value = val + "-";
    return true;
}

function isOkKey(evt) {
    if (evt.ctrlKey)
        return true;
    switch (evt.keyCode) {
        case 8:
        case 9:
        case 16:
        case 35:
        case 36:
        case 37:
        case 39:
        case 46:
            return true;
    }
}

function numformat(event) {
    var evt = event = event || window.event;
    var val = evt.srcElement.value;
    var passKeyDown = false;
    if (!evt.shiftKey || evt.keyCode == 9) {
        if ((isOkKey(evt) || (evt.keyCode >= 48 && evt.keyCode <= 57 ) || // 0 - 9
            (evt.keyCode >= 96 && evt.keyCode <= 105 ) )) {
            passKeyDown = true;
        } else if (evt.keyCode == 190 || evt.keyCode == 110) {  // check the dot.
            if (val.indexOf('.') < 0) {
                passKeyDown = true;
            }
        } else if (evt.keyCode == 189 || evt.keyCode == 109) {  // check the minus.
            if (val.length == 0) {
                passKeyDown = true;
            } else if (val.indexOf('-') < 0) {
                if (getCursorPosition(evt.srcElement) == 0) {
                    passKeyDown = true;
                }
            }
        }
    }
    if (passKeyDown) {
        return true;
    } else {
        event.cancelBubble = true;
        event.returnValue = false;
        return false;
    }
}

function getCursorPosition(field) {
    var range = document.selection.createRange();
    var rangeLength = range.text.length;
    range.moveStart('character', -field.value.length);
    return range.text.length - rangeLength;
}

function XMLnumformat() {
    return numformat();
}
function dateformat(event) {
    var evt = event || window.event;

    datemaskeditclear(evt);
    var code = evt.keyCode;
    if (isOkKey(evt))
        return true;

    if (code >= 96 && code <= 105)
        code -= 48;

    // Changed to support Date format dd/mon/yyyy
    if (DATE_MASK_INTERNATIONAL.indexOf('mon') <= 0) {
        if (!evt.shiftKey && (code >= 48 && code <= 57 )) {
            var val = evt.srcElement.value
            var txt = "";

            if (document.getSelection)
                txt = document.getSelection();
            else if (document.selection)
                txt = document.selection.createRange().text;

            if ((val.length - txt.length) > 9) {
                evt.cancelBubble = true;
                evt.returnValue = false;
                return false;
            }
            if (dateFormatUS) {
                switch (val.length)
                {

                    case 1:
                        if (DATE_MASK.substring(0, 2) != 'dd') {
                            if (val.substring(0, 1) == "1") {
                                if (code > 50) {
                                    evt.cancelBubble = true;
                                    evt.returnValue = false;
                                    return false;
                                }
                            }
                            else if (val.substring(0, 1) != "0") {
                                evt.srcElement.value = "0" + val + "/";
                            }
                        }
                        break;
                    case 2:
                        if (DATE_MASK.substring(0, 2) != 'dd')
                            evt.srcElement.value = val + "/";
                        else
                            evt.srcElement.value = val + ".";
                        break;
                    case 4:
                        if (DATE_MASK.substring(0, 2) == 'dd') {
                            if (val.substring(3, 4) == "1") {
                                if (code > 50) {
                                    evt.cancelBubble = true;
                                    evt.returnValue = false;
                                    return false;
                                }
                            }
                            else {
                                if (val.substring(3, 4) != "0") {
                                    evt.srcElement.value = val.substring(0, 3) + "0" + val.substring(3, 4) + ".";
                                }
                            }
                        }
                        break;
                    case 5:
                        if (DATE_MASK.substring(0, 2) != 'dd')
                            evt.srcElement.value = val + "/";
                        else
                            evt.srcElement.value = val + ".";
                        break;
                }
            } else if (DATE_MASK_INTERNATIONAL.substring(0, 4)=='yyyy') {
                // Changed to support Chinese Date format yyyy/mm/dd
                switch (val.length) {
                    case 4:
                        evt.srcElement.value = val + '/';
                        break;
                    case 6:
                        if (val.substring(5, 6) == "1") {
                            if (code > 50) {
                                evt.cancelBubble = true;
                                evt.returnValue = false;
                                return false;
                            }
                        } else {
                            if (val.substring(5, 6) != "0") {
                                evt.srcElement.value = val.substr(0,5) + '0'+ val.substr(5,6) + '/';
                            }
                        }
                        break;
                    case 7:
                        evt.srcElement.value = val + '/';
                        break;
                }
            } else {
                switch (val.length)
                {

                    case 1:
                        break;
                    case 2:
                        evt.srcElement.value = val + "/";
                        break;
                    case 4:
                        if (val.substring(3, 4) == "1") {
                            if (code > 50) {
                                evt.cancelBubble = true;
                                evt.returnValue = false;
                                return false;
                            }
                        }
                        else {
                            if (val.substring(3, 4) != "0") {
                                evt.srcElement.value = val.substring(0, 3) + "0" + val.substring(3, 4) + "/";
                            }
                        }
                        break;
                    case 5:
                        evt.srcElement.value = val + "/";
                        break;
                }
            }
            return true;
        }
        else {
            evt.cancelBubble = true;
            evt.returnValue = false;
            return false;
        }

    } else {
        // Changed to support Date format dd/mon/yyyy
        if ((code >= 48 && code <= 57 ) || (code >= 65 && code <= 90)) {
            var val = evt.srcElement.value
            var txt = "";

            if (document.getSelection)
                txt = document.getSelection();
            else if (document.selection)
                txt = document.selection.createRange().text;

            if ((val.length - txt.length) > 10) {
                evt.cancelBubble = true;
                evt.returnValue = false;
                return false;
            }
//            alert(val.length)
            switch (val.length) {
                case 0:
                case 1:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
                case 2:
                    if (code < 65) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    evt.srcElement.value = val + "/";
                    break;
                case 3:
                case 4:
                case 5:
                    if (code < 65) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
                case 6:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    evt.srcElement.value = val + "/";
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                    if (code > 57) {
                        evt.cancelBubble = true;
                        evt.returnValue = false;
                        return false;
                    }
                    break;
            }
            return true;
        }
        else {
            evt.cancelBubble = true;
            evt.returnValue = false;
            return false;
        }
    }
}
function XMLdateformat() {
    return dateformat();
}

function keypress_number(decimal) {
    var keypressed = window.event.keyCode;
    var ElementText = window.event.srcElement.value ;
    if (keypressed == 46) {
        if (ElementText.length != 0) {
            window.event.keyCode = 0;
        }

    }
    else {
        if (decimal) {
            var Reg = /\./g;
            if (Reg.test(ElementText)) {
                if ((keypressed >= 48 && keypressed <= 57) == false) {
                    window.event.keyCode = 0;
                }
            }
            else {
                if ((keypressed >= 48 && keypressed <= 57 || keypressed == 46) == false) {
                    window.event.keyCode = 0;
                }
            }
        }

        else {
            if ((keypressed >= 48 && keypressed <= 57) == false) {
                window.event.keyCode = 0;
            }
        }
    }
}

function phonemask(event) {
    event = event || window.event;
    if (trim(event.srcElement.value).length == 0) event.srcElement.value = PHONE_MASK;
    return true
}

function datemask(event) {
    event = event || window.event;

    if (trim(event.srcElement.value).length == 0) {
        //Added for Date Format Internationalization
        if(dateFormatUS==true)
            event.srcElement.value = DATE_MASK;
        else
            event.srcElement.value = DATE_MASK_INTERNATIONAL;
        event.srcElement.select();
    }
    return true;
}

function phonemaskclear(event) {
    event = event || window.event;
    if (event.srcElement.value == PHONE_MASK) event.srcElement.value = '';
    return true;
}

function datemaskeditclear(event) {
    event = event || window.event;

//    if (window.event.srcElement.value == DATE_MASK) window.event.srcElement.value = ''
    //Added for Date Format Internationalization
    if (event.srcElement.value == DATE_MASK)
        event.srcElement.value = '';
    else if (event.srcElement.value == DATE_MASK_INTERNATIONAL)
        event.srcElement.value = '';
    return true;
}

function datemaskclear(event) {
    event = event || window.event;

//    if (window.event.srcElement.value == DATE_MASK) {
//        window.event.srcElement.value = '';
//        return true;
//    }
    var m;
    var d;
    var y;
    //Added for Date Format Internationalization
    if (event.srcElement.value == DATE_MASK) {
        event.srcElement.value = '';
        return true;
    } else if(event.srcElement.value == DATE_MASK_INTERNATIONAL)   {
        event.srcElement.value = '';
        return true;
    } else {
        var dtentry = event.srcElement.value;
        if (dtentry == '') {
            return true;
        }
        if (dtentry.length != 10 && dtentry.length != 11) {
            if(dateFormatUS)
                alert(getMessage("cal.validate.error.format", new Array(DATE_MASK)));
            else
                alert(getMessage("cal.validate.error.format", new Array(DATE_MASK_INTERNATIONAL)));
            event.srcElement.value='';
            return false;
        }
        if (DATE_MASK = 'mm/dd/yyyy') {
            var j = dtentry.split('/');
            m = j[0];
            d = j[1];
            y = j[2];
        }
        else {
            var j = dtentry.split('.');
            d = j[0];
            m = j[1];
            y = j[2];
        }

        //Added for Date Format Internationalization
        //This will overwrite the previos logic
        if(!dateFormatUS)   {
            if(event.srcElement.name.indexOf(DISPLAY_FIELD_EXTENTION)>-1){
                if(DATE_MASK_INTERNATIONAL == 'dd/mm/yyyy' || DATE_MASK_INTERNATIONAL == 'dd/mon/yyyy'){
                    var j = dtentry.split('/');
                    d = j[0];
                    m = j[1];
                    y = j[2];
                }
                // Added to support Chinese Date format yyyy/mm/dd
                // TODO make it more generic
                else if (DATE_MASK_INTERNATIONAL.substring(0, 4)=='yyyy') {
                    var j = dtentry.split(DATE_MASK_INTERNATIONAL.substring(4, 5));
                    y = j[0];
                    m = j[1];
                    d = j[2];
                }
            }
        }

        if (DATE_MASK_INTERNATIONAL.indexOf('mon') > 0 && !isInteger(m) ){
            for (j = 0; j < 12; ++j) {
                if (Calendar._SMN[j].toLowerCase() == m.toLowerCase()) { break; }
            }
            if (j < 10) {
                m = '0' + (j + 1);
            } else {
                m = '' + (j + 1);
            }
        }
        if (!isValidDate(y, m, d)) {
//            alert('Please enter a valid date of the format ' + DATE_MASK);
            //Added for Date Format Internationalization
            if(dateFormatUS)
                alert(getMessage("cal.validate.error.format", new Array(DATE_MASK)));
            else
                alert(getMessage("cal.validate.error.format", new Array(DATE_MASK_INTERNATIONAL)));
            event.srcElement.value='';
            return false;
        }
        else {
            postDateChange();
            return true;
        }
    }

}
function getRealDate(sDate, sMask) {
    if (sDate != '' && sDate != '.' && sDate != '-') {
        if (sMask == 'dd.mm.yyyy') {
            var j = sDate.split('.');
            var d = j[0];
            var m = j[1] - 1;
            var y = j[2];
        }
        else {
            var j = sDate.split('/');
            var m = j[0] - 1;
            var d = j[1];
            var y = j[2];
        }
        return new Date(y, m, d);
    }
    return null;
}
function formatDate(sDate, sMask) {
    //logDebug('formatDate(' + sDate + ',' + sMask + ')');
    if (sDate != '' && sDate != '.' && sDate != '-') {
        var d = sDate.getDate();
        var day = (d < 10) ? '0' + d : d;
        var m = sDate.getMonth() + 1;
        var month = (m < 10) ? '0' + m : m;
        var year = sDate.getFullYear();

        switch (sMask) {
            case 'mm/dd/yyyy':
                return month + "/" + day + "/" + year;
                break;
            case 'dd.mm.yyyy':
                return day + "." + month + "." + year;
                break;
            case 'dd/mm/yyyy':
                return day + "/" + month + "/" + year;
                break;
            case 'yyyy/mm/dd':
                return year + "/" + month + "/" + day;
                break;
            case 'dd/mon/yyyy':
                return day + "/" + Calendar._SMN[sDate.getMonth()]  + "/" + year;
                break;
        }
    }
    else
        return null;
}

function formatDateForDisplay(sDate) {
    //logDebug('formatDateForDisplay(' + sDate + ')');
    if (isEmpty(sDate)) {
        return "";
    }
    var date = new Date();
    var dtMask = DATE_MASK;
    var m, d, y, j;
    if (dateFormatUS) {
        //logDebug('Display US Date Format: ' + dateFormatUS);
        if (dtMask = 'mm/dd/yyyy') {
            j = sDate.split('/');
        }
        else {
            j = sDate.split('.');
        }
    } else {
        //logDebug('Display Non-US Date Format: ' + !dateFormatUS)
        dtMask = DATE_MASK_INTERNATIONAL;
        if (dtMask == 'dd/mm/yyyy') {
            j = sDate.split('/');
        }
        // Added to support date format dd/mon/yyyy
        else if (dtMask == 'dd/mon/yyyy') {
            j = sDate.split('/');
        }
        // Added to support Chinese date format yyyy/mm/dd
        else if (dtMask.substring(0, 4)=='yyyy') {
            j = sDate.split(dtMask.substring(4, 5));
        } else {
            j = sDate.split('.');
        }
    }
    m = j[0];
    d = j[1];
    y = j[2];

    //logDebug('formatDateForDisplay(' + d + "/" + (m - 1) + "/" + y + ')');
    var date = new Date();
    date.setFullYear(y, m - 1, d);
    date.setMonth(m - 1);
    date.setDate(d);
    //logDebug('formatDateForDisplay(' + date + ')');
    return formatDate(date, dtMask);
}

function formatDateTimeForDisplay(sDate) {
    //Split the date and time
    var dtVal = sDate.substring(0, 10);
    var tmVal = sDate.substring(10);

    return formatDateForDisplay(dtVal) + tmVal;
}

function postDateChange()
{
}
function setEdit()
{
    var iend = document.forms.length
    for (i = 0; i < iend; i++) {
        jend = document.forms[i].elements.length
        for (j = 0; j < jend; j++) {

            if (document.forms[i].elements[j].dataSrc == 'Required')
            {
                setRequired(document.forms[i].elements[j].name.replace(/:/, ""));

            }

            if (trim(document.forms[i].elements[j].title).length != 0)
            {
                setLabel(document.forms[i].elements[j].name, document.forms[i].elements[j].title);
            }

        }
    }

    return true;
}
/*
 var rowobject_backcolor="";
 var targetrow_backcolor="";
 */
//var rowobject_classname="";
var targetrow_classname = "";
var oldClassName = "";

//Drag and drop for Grid Columns
var bDragMode = false;
var objDragItem;
var objDragItemSrc;
var objDragItemId;
var saveid = '';
var origid = '';

function gridHeaderProps(colid, colhtml)
{
    this.id = colid;
    this.html = colhtml;
}

function onGridHeadMouseUp(e)
{
    var bSrcFound = false;
    var bDestFound = false;
    var iSave = 0;

    if (!bDragMode)    return;

    bDragMode = false;

    var src = e.srcElement;
    if (src.tagName != 'TH') {
        return false;
    }
    var tblid = src.parentElement.parentElement.parentElement.id ;
    var srcid = src.id.substring(1);

    if (objDragItemId == srcid)
        return false;
    for (i = 0; i < document.all.tags("div").length; i++) {
        if (document.all.tags("div")(i).id == objDragItemId) {
            bSrcFound = true;
            break;
        }
    }
    for (i = 0; i < document.all.tags("div").length; i++) {
        if (document.all.tags("div")(i).id == srcid) {
            bDestFound = true;
            break;
        }
    }

    if (bDestFound && bSrcFound) {
        for (i = 0; i < document.all.tags("div").length; i++) {
            if (document.all.tags("div")(i).id == objDragItemId) {
                document.all.tags("div")(i).dataFld = srcid;
                document.all.tags("div")(i).id = 'T' + srcid;
            }
            else if (document.all.tags("div")(i).id == srcid) {
                document.all.tags("div")(i).dataFld = objDragItemId;
                document.all.tags("div")(i).id = objDragItemId;
            }
        }

        for (i = 0; i < document.all.tags("div").length; i++) {
            if (document.all.tags("div")(i).id == 'T' + srcid) {
                document.all.tags("div")(i).id = srcid;
            }
        }
        objDragItem.innerHTML = src.innerHTML;
        objDragItem.id = 'H' + srcid;
        src.innerHTML = objDragItemSrc;
        src.id = 'H' + objDragItemId;
        saveGridHeader(eval(tblid))
    }
    else {
        return false;
    }
}

function onGridHeadMouseDown(e)
{
    objDragItem = e.srcElement
    if (objDragItem.tagName != 'TH') {
        return false;
    }
    bDragMode = true;
    objDragItemSrc = objDragItem.innerHTML;
    objDragItemId = objDragItem.id.substring(1);

}

function saveGridHeader(tblid)
{
    var i;

    saveid = '';

    // get TBODY - take the first TBODY for the table
    tbody = tblid.tBodies(0);
    if (!tbody) return;

    //Get THEAD
    var click = tblid.tHead;
    if (!click)  return;
    headRow = click.children[0];
    if (headRow.tagName != "TR") return;

    saveid = document.location.href + '|';
    ColumnCount = headRow.children.length;

    for (i = 0; i < ColumnCount; i++)
    {
        var clickCell = headRow.children[i];
        saveid = saveid + clickCell.id + '|';

    }
    setCookie('GRIDHEAD', saveid, 1);
}

function setGridHeader(tblid)
{
    if (origid.length == 0) {
        initHeader(tblid);
    }
    var isbXMLSort = bXMLSort;
    if (isMultiGridSupported) {
        isbXMLSort = getTableProperty(tblid, "bXMLSort");
    }
    if (!isbXMLSort)
        return true;
    bXMLSort = false;
    if (isMultiGridSupported) {
        setTableProperty(tblid, "bXMLSort", false)
    }
    if (saveid == null || saveid.length == 0)
        return false;

    var saverow = saveid.split("|");

    if (saverow[0] != document.location.href)
        return false;

    var origrow = origid.split("|");

    for (i = 0; i < (origrow.length - 1); i++) {
        if (origrow[i] != saverow[i + 1]) {
            for (j = 0; j < document.all.tags("div").length; j++) {
                if (document.all.tags("div")(j).id == origrow[i].substring(1)) {
                    document.all.tags("div")(j).dataFld = saverow[i + 1].substring(1);
                    document.all.tags("div")(j).id = 'T' + saverow[i + 1].substring(1);
                }
            }
        }
    }

    for (i = 0; i < (origrow.length - 1); i++) {
        if (origrow[i] != saverow[i + 1]) {
            for (j = 0; j < document.all.tags("div").length; j++) {
                if (document.all.tags("div")(j).id == 'T' + saverow[i + 1].substring(1)) {
                    document.all.tags("div")(j).id = saverow[i + 1].substring(1);
                }
            }

        }
    }

}
function initHeader(tblid)
{
    var i;
    var gridHeaderPropArray = new Array();

    // get TBODY - take the first TBODY for the table
    tbody = tblid.tBodies(0);
    if (!tbody) return;

    //Get THEAD
    var click = tblid.tHead;
    if (!click)  return;
    headRow = click.children[0];
    if (headRow.tagName != "TR") return;
    if (headRow.getElementsByTagName("A").length > 0)
        headRow.runtimeStyle.cursor = "pointer";

    ColumnCount = headRow.children.length;

    origid = '';
    saveid = getCookie('GRIDHEAD');
    if (saveid != null) {
        var saverow = saveid.split('|');
        if (saverow[0] != document.location.href)
            saveid = '';
    }


    for (i = 0; i < ColumnCount; i++)
    {
        var clickCell = headRow.children[i];
        // Commented out for issue 86064
        /*
         clickCell.selectIndex = i;
         clickCell.attachEvent("onmousedown", onGridHeadMouseDown);
         clickCell.attachEvent("onmouseup", onGridHeadMouseUp);
         origid = origid + clickCell.id + '|';
         */
        gridHeaderPropArray[i] = new gridHeaderProps(clickCell.id, clickCell.innerHTML);
    }
    if (saveid != null && saveid.length > 0)
    {
        for (i = 0; i < ColumnCount; i++)
        {
            var clickCell = headRow.children[i];
            clickCell.selectIndex = i;
            if (clickCell.id != 'H' + saverow[i + 1].substring(1)) {
                clickCell.id = 'H' + saverow[i + 1].substring(1);
                for (j = 0; j < ColumnCount; j ++) {
                    if (gridHeaderPropArray[j].id == 'H' + saverow[i + 1].substring(1)) {
                        clickCell.innerHTML = gridHeaderPropArray[j].html;
                        break;
                    }
                }
            }
        }
        bXMLSort = true;
        if (isMultiGridSupported) {
            setTableProperty(tblid, "bXMLSort", true)
        }
    }

}
function alternate_colors(tblid)
{

    if (tblid.readyState == "complete")
    {
        //Clear the rowobject when sort, filter and pagination.
        setTableProperty(tblid, "rowobject", null);
        for (j = 1; j < tblid.rows.length; j++)
        {
            if (j % 2)
            {
                tblid.rows[j].className = "alternate_colors_one";
            }
            else
                tblid.rows[j].className = "alternate_colors_two";
            // Call handleRowReadyStateReady if it exists.
            if (window.handleRowReadyStateReady) {
                handleRowReadyStateReady(tblid, j);
            }
        }
        setGridHeader(tblid);
        // Call readyStateReady if it exists
        if (window.readyStateReady)
            readyStateReady(tblid);
    }

    dti.oasis.grid.getProperty(tblid.id, "loadingDeferredObj").resolve();
}

function isNavigationalKey(keypressed) {
    //logDebug('isNavigationalKey('+keypressed+')');
    if(keypressed == 38 || keypressed == 40)
        return true;
    else
        return false;
}

function isNavigationalKeyWithEnter(keypressed) {
    // 38   Up key
    // 40   Down key
    // 13   Enter key
    if(keypressed == 38 || keypressed == 40 || keypressed == 13)
        return true;
    else
        return false;
}

function hiliteSelectAnotherRow(selectedRow) {
    //logDebug('hiliteSelectAnotherRow('+getCurrentRowId(selectedRow)+')');
    var isInputElementInGrid = false;
    var isSelectElementInGrid = false;
    var isTextareaElementInGrid = false;
    var sourceElement = window.event.srcElement;
    if (sourceElement) {
        var lowerTagName = sourceElement.tagName.toLowerCase();
        if (lowerTagName == "input" && sourceElement.type == 'text' && sourceElement.name.startsWith("txt")) {
            isInputElementInGrid = true;
        }
        if (lowerTagName == "select" && sourceElement.name.startsWith("cbo")) {
            isSelectElementInGrid = true;
        }
        if (lowerTagName == "textarea" && sourceElement.name.startsWith("txt")) {
            isTextareaElementInGrid = true;
        }
    }

    if (isInputElementInGrid && isNavigationalKeyWithEnter(window.event.keyCode)) {
        // editable input field in grid. Move the focus to TD element, then fire onkeydown again
        var isAllowed = false;
        if (window.isOKToNavigateFromField) {
            isAllowed = window.isOKToNavigateFromField(sourceElement);
        }
        if (isAllowed) {
            selectAnotherRowInEditableGrid(sourceElement);
        }
    } else if (isSelectElementInGrid) {
        // do nothing
    } else if (isTextareaElementInGrid) {
        // do nothing
    } else {
        var keypressed = window.event.keyCode;
        switch(keypressed){
            case 38:
                selectAnotherRow(selectedRow,'P');
                break;

            case 40:
                selectAnotherRow(selectedRow,'N');
                break;
        }
    }
}

function getCurrentRowId(selectedRow){
    var table = selectedRow.parentElement.parentElement;
    var XMLData = getXMLDataForTable(table);

    var rowId = XMLData.recordset("ID").value;
//    logDebug('getCurrentRowId:: rowId:'+rowId);

    return rowId;
}

function getPreviousRowId(selectedRow){
    var rowId = 0;
    var table = selectedRow.parentElement.parentElement;
    var XMLData = getXMLDataForTable(table);
    XMLData.recordset.moveprevious();
    if (!XMLData.recordset.eof)
    {
        rowId = XMLData.recordset("ID").value;
//        logDebug('getPreviousRowId:: rowId:'+rowId);
    }
    XMLData.recordset.movenext();
    return rowId;
}

function getNextRowId(selectedRow){
    var rowId = 0;
    var table = selectedRow.parentElement.parentElement;
    var XMLData = getXMLDataForTable(table);
    XMLData.recordset.movenext();
    if (!XMLData.recordset.eof)
    {
        rowId = XMLData.recordset("ID").value;
//        logDebug('getNextRowId:: rowId:'+rowId);
    }
    XMLData.recordset.moveprevious();
    return rowId;
}

function hasPreviousRow(selectedRow){
    var rowId = 0;
    var table = selectedRow.parentElement.parentElement;
    var XMLData = getXMLDataForTable(table);
    return hasPrevious(XMLData);
}

function hasNextRow(selectedRow){
    var rowId = 0;
    var table = selectedRow.parentElement.parentElement;
    var XMLData = getXMLDataForTable(table);
    return hasNext(XMLData);
}


function getAnotherRowId(selectedRow, direction){
    switch(direction) {
        case 'P':
            return getPreviousRowId(selectedRow);
            break;
        case 'N':
            return getNextRowId(selectedRow);
            break;
    }
}

function isOnFirstPage(table){
    var pageNum = getTableProperty(table, "pageno");
    return pageNum>1?false:true;
}

function isOnLastPage(table){
    var pageNum = getTableProperty(table, "pageno");
    var totalPages = getTableProperty(table, "pages");
    return pageNum==totalPages?true:false;
}

function selectAnotherRow(selectedRow, direction){
//    logDebug('@@@@@@@@@@@@@@@@@@@@@@@@ selectAnotherRow('+selectedRow+', '+direction+')');

    var table = selectedRow.parentElement.parentElement;
    var selectedIndex = getTableProperty(table, "selectedTableRowNo");
    var pageSize = getTableProperty(table, "pagesize");

//    logDebug('selectAnotherRow:: Row Id: ' +  getCurrentRowId(selectedRow) +
//            ' - isOnFirstPage: '+isOnFirstPage(table)+' - isOnLastPage: ' +  isOnLastPage(table) +
//            ' - hasPreviousRow(selectedRow): '+hasPreviousRow(selectedRow)+ ' - hasNextRow(selectedRow): '+hasNextRow(selectedRow));

    var targetIndex = selectedIndex;
    var newRowId = getCurrentRowId(selectedRow);

    switch(direction) {
        case 'P':
            if(isOnFirstPage(table) && !hasPreviousRow(selectedRow)) {
                alert('You have reached the first record in the grid.');
                setFocusBackToOriginalField(table);
                return;
            }
            targetIndex--;
            if(hasPreviousRow(selectedRow))
                newRowId = getPreviousRowId(selectedRow);
            break;
        case 'N':
            if(isOnLastPage(table) && !hasNextRow(selectedRow)) {
                alert('You have reached the last record in the grid.');
                setFocusBackToOriginalField(table);
                return;
            }
            targetIndex++;
            if(hasNextRow(selectedRow))
                newRowId = getNextRowId(selectedRow);
            break;
    }
    //logDebug('selectAnotherRow::targetIndex: '+targetIndex);

    if (targetIndex>0 && targetIndex <= pageSize) {
        var targetRow = table.rows[targetIndex];
        hiliteSelectRow(targetRow);
        handleScrollIntoViewWhenReady(table.id, targetIndex);
        baseOnRowSelected(table.id, newRowId);
        setFocusBackToOriginalField(table);
    }

    if (targetIndex<1) {
        selectRowOnAnotherPage(table, newRowId, 'P');
    }
    if (targetIndex > pageSize) {
        selectRowOnAnotherPage(table, newRowId, 'N');
    }
}

function selectRowOnAnotherPage(table, nextId, direction) {
    baseOnBeforeGotoPage(table, direction);
    var adjustmentRequired = getTableProperty(table, "adjustmentRequired");
    if (typeof adjustmentRequired == "undefined") {
        adjustmentRequired = "false";
        setTableProperty(table, "adjustmentRequired", adjustmentRequired);
    }

    var pageSize = getTableProperty(table, "pagesize");
    var pageno = getTableProperty(table, "pageno");
    var pages = getTableProperty(table, "pages");
    var targetIndex = 0;
    switch (direction) {
        case 'P':
            targetIndex = pageSize;
            if (parseInt(pageno) == 2 && adjustmentRequired == "true") {
                targetIndex = pageSize - calculateAdjustment(table);
                setTableProperty(table, "adjustmentRequired", "false");
            }
            break;
        case 'N':
            targetIndex = 1;
            if ((parseInt(pageno) + 1 ) == pages && adjustmentRequired == "false") {
                targetIndex = 1 + calculateAdjustment(table, direction);
                setTableProperty(table, "adjustmentRequired", "true");
            }
            break;
    }
    setTableProperty(table, "selectedTableRowNo", targetIndex);
    setSelectedRow(table.id, nextId);
    gotopage(table, direction);
    baseOnAfterGotoPage(table, direction);
    setTableProperty(table, "isUserReadyStateReadyComplete", false);
    scrollIntoViewWhenReady(table.id, targetIndex);
}

function testMyScroll(gridId, size) {
    var table = getTableForGrid(gridId);
    var myDiv = getDivForGrid(table.id);
    myDiv.scrollTop = size;
}

function simpleTest(gridId){
    var table = getTableForGrid(gridId);

    var XMLData = getXMLDataForTable(table);

    //logDebug('Record Count: '+XMLData.recordset.recordcount);
    //logDebug('Position 1: '+XMLData.recordset.absoluteposition);
    if(hasNext(XMLData)){
        XMLData.recordset.movenext();
        //logDebug('Position 2: '+XMLData.recordset.absoluteposition);
    }

    if(hasPrevious(XMLData)){
        XMLData.recordset.moveprevious();
        //logDebug('Position 3: '+XMLData.recordset.absoluteposition);
    }
    if(hasPrevious(XMLData)){
        XMLData.recordset.moveprevious();
        //logDebug('Position 4: '+XMLData.recordset.absoluteposition);
    }
    XMLData.recordset.movelast();
    if(hasPrevious(XMLData)){
        XMLData.recordset.moveprevious();
        //logDebug('Position 5: '+XMLData.recordset.absoluteposition);
    }
    if(hasNext(XMLData)){
        XMLData.recordset.movenext();
        //logDebug('Position 6: '+XMLData.recordset.absoluteposition);
    }
    if(hasNext(XMLData)){
        XMLData.recordset.movenext();
        //logDebug('Position 7: '+XMLData.recordset.absoluteposition);
    }
}

function calculateAdjustment(table){
    var pageSize = getTableProperty(table, "pagesize");
    var numRec = getTableProperty(table, "nrec");

    var roundOffRecords = 0;
    var adjustment = numRec % pageSize;
    if(adjustment>0)
        roundOffRecords = pageSize - adjustment;

    //logDebug('calculateAdjustment::roundOffRecords: '+roundOffRecords);
    return roundOffRecords;
}

var TABLE_PROPERTY_CACHE_KEYCODE =  "tablePropertyCacheKeyCode";
var TABLE_PROPERTY_FOCUS_FIELD_NAME =  "tablePropertyFocusFieldName";

function selectAnotherRowFromTextField() {
    var table = $(this).closest("table")[0];
    var keyCode = getTableProperty(table, TABLE_PROPERTY_CACHE_KEYCODE);
    if (keyCode) {
        setTableProperty(table, TABLE_PROPERTY_CACHE_KEYCODE, null);
        var e = document.createEventObject("KeyboardEvent");
        e.keyCode = keyCode;
        this.fireEvent("onkeydown", e);
        //user it only for once
        this.onfocus = null;
    }
}

function setFocusBackToOriginalField(table) {
    var fieldName = getTableProperty(table, TABLE_PROPERTY_FOCUS_FIELD_NAME);
    if (fieldName) {
        setTableProperty(table, TABLE_PROPERTY_FOCUS_FIELD_NAME, null);
        if (fieldName.startsWith("txt")) {
            // editable grid field
            var selectedIndex = getTableProperty(table, "selectedTableRowNo");
            var selectedRow = table.rows[selectedIndex];
            var inputFields = $(selectedRow).find("input[name='" + fieldName + "']");
            if (inputFields.length > 0) {
                inputFields[0].focus();
            }
        } else {
            if (hasObject(fieldName)) {
                getSingleObject(fieldName).focus();
            }
        }
    }
}

function selectAnotherRowInBoundGrid(field){
    //logDebug('selectAnotherRowInBoundGrid('+field+')');
    var datasrc = field.dataSrc;
    var gridId = datasrc.substring((datasrc.indexOf('#')+1),datasrc.indexOf('1'))
//    logDebug('gridId:'+gridId);
    var table = getTableForGrid(gridId);

    var selectedIndex = getTableProperty(table, "selectedTableRowNo");
//    logDebug('selectAnotherRowInBoundGrid::selectedIndex: '+selectedIndex);
    var selectedRow = table.rows[selectedIndex];
//    logDebug('selectAnotherRowInBoundGrid::selectedRow: '+selectedRow.outerHTML);

    setTableProperty(table, TABLE_PROPERTY_CACHE_KEYCODE, window.event.keyCode);
    setTableProperty(table, TABLE_PROPERTY_FOCUS_FIELD_NAME, field.name);

    var firstTD = $(selectedRow).find('td').first()[0];
    firstTD.onfocus = window.selectAnotherRowFromTextField;
    firstTD.focus();

}

function selectAnotherRowInEditableGrid(field) {
    var selectedRow = $(field).closest("tr")[0];
    var table = $(field).closest("table")[0];

    var keyCode = window.event.keyCode;
    // handle key "Enter" as key "Down"
    if (keyCode == 13) {
        keyCode = 40;
    }
    setTableProperty(table, TABLE_PROPERTY_CACHE_KEYCODE, keyCode);
    setTableProperty(table, TABLE_PROPERTY_FOCUS_FIELD_NAME, field.name);

    var firstTD = $(selectedRow).find('td').first()[0];
    firstTD.onfocus = window.selectAnotherRowFromTextField;
    firstTD.focus();

}

function hiliteSelectRow(targetRow)
{
//    logDebug('hiliteSelectRow('+getCurrentRowId(targetRow)+')');
    if (!doHilite) return;

    var table = targetRow.parentElement.parentElement;
    var rowobject = getTableProperty(table, "rowobject");
    if (typeof rowobject == "object") {
        if (rowobject == targetRow) {
            return;
        }
    }

    var lastHilitedRowNo = getTableProperty(table, "selectedTableRowNo");
    var lastHilitedRowClassName = "";
    if (lastHilitedRowNo && lastHilitedRowNo != targetRow.rowIndex) {
        // If any previous row is hilited, unhilite it by resetting the className to saved table property "oldClassName"
        lastHilitedRowClassName = getTableProperty(table, "oldClassName");
        if (!lastHilitedRowClassName) {
            lastHilitedRowClassName = "";
        }
        var lastHilitedRow = table.rows[lastHilitedRowNo]
        if (lastHilitedRow) {
            //unhilite the row and reset the cursor style.
            lastHilitedRow.className = lastHilitedRowClassName;
            targetRow.style.cursor = "";
        }
    }

    // we lose the original old className, because it gets resetted by mouseover event before this event is fired.
    var oldClassName = targetRow.className;
    if (oldClassName == "hiliteRow" || oldClassName == "hiliteSelectRow") {
        if (targetRow.rowIndex % 2)
            oldClassName = "alternate_colors_one";
        else
            oldClassName = "alternate_colors_two";
    }
    setTableProperty(table, "selectedTableRowNo", targetRow.rowIndex);
    setTableProperty(table, "oldClassName", "'" + oldClassName + "'");
    setTableProperty(table, "rowobject", targetRow);

    targetRow.className = "hiliteSelectRow";
    targetRow.style.cursor = "pointer";
}

function hiliteSelectFirstRow(table) {
    if (!doHilite) return;

    if (table.rows.length > 1) {
        var targetRow = table.rows[1];
        //logDebug('edits.js:hiliteSelectFirstRow::calls hiliteSelectRow');
        hiliteSelectRow(targetRow);
    }
}

function hiliteSelectLastRow(table) {
    if (!doHilite) return;

    if (table.rows.length > 1) {
        var targetRow = table.rows[table.rows.length - 1];
        //logDebug('edits.js:hiliteSelectLastRow::calls hiliteSelectRow');
        hiliteSelectRow(targetRow);
    }
}

function hiliteRow(targetRow)
{
    if (!doHilite) return;
    var table = targetRow.parentElement.parentElement;
    var rowobject = getTableProperty(table, "rowobject");
    if (typeof rowobject == "object") {
        if (rowobject == targetRow) {
            return;
        }
    }

    targetrow_classname = targetRow.className;
    targetRow.className = "hiliteRow";
    targetRow.style.cursor = "pointer";
}

function unhiliteRow(targetRow)
{
    if (!doHilite) return;
    var table = targetRow.parentElement.parentElement;
    var rowobject = getTableProperty(table, "rowobject");
    if (typeof rowobject == "object") {
        if (rowobject == targetRow)
            return;
    }
    targetRow.className = targetrow_classname;
}

function setPointer() {
    if (document.all)
        for (var i = 0; i < document.all.length; i++)
            document.all(i).style.cursor = 'wait';
}

function resetPointer() {
    if (document.all)
        for (var i = 0; i < document.all.length; i++)
            document.all(i).style.cursor = 'auto';
}

function formatMoney(number) {
    var cents = outputCents(number - 0);
    var incr = 0;
    if (cents == ".100") {
        incr = -1;
        cents = ".00";
    }
    return outputDollars(Math.floor(number - incr) + '') + cents;
}

function outputDollars(number) {
    if (number.length <= 3)
        return (number == '' ? '0' : number);
    else {
        var mod = number.length % 3;
        var output = (mod == 0 ? '' : (number.substring(0, mod)));
        for (i = 0; i < Math.floor(number.length / 3); i++) {
            if ((mod == 0) && (i == 0))
                output += number.substring(mod + 3 * i, mod + 3 * i + 3);
            else
                output += ',' + number.substring(mod + 3 * i, mod + 3 * i + 3);
        }
        return (output);
    }
}

function outputCents(amount) {
    amount = Math.round(( (amount) - Math.floor(amount) ) * 100);
    return (amount < 10 ? '.0' + amount : '.' + amount);
}

function toDollarsAndCents(n) {
    if (isNaN(n)) return "0.00";
    var s = "" + Math.round(n * 100) / 100
    var i = s.indexOf('.')
    if (i < 0) return s + ".00"
    var t = s.substring(0, i + 1) + s.substring(i + 1, i + 3)
    if (i + 2 == s.length) t += "0"
    return t
}

function decodeSelect(elem, value) {
    for (var i = 0; i < elem.length; i++) {
        if (elem.options[i].value == value)
            return elem.options[i].text;
    }
    return '';
}

/**
 * the TextArea type field in IE11 has new feature when executing functions of fld.value.length() & fld.value.substring(),
 * which means it only take care visible chars, without including invisible chars (e.g. \r\n)
 * so we have to count this special char,
 * @param field
 * @param maxLength
 * @returns {number}
 */
function getAdjustedTextareaTextLength(field, maxLength) {
    var rtnCharsLen = 0;
    if (useJqxGrid && dti.inpututils.isTextareaField(field)) {
        var rtnChars = $(field).val().match(/(\n)/g);
        if (rtnChars != null) {
            rtnCharsLen = rtnChars.length;
        }
    }
    return maxLength - rtnCharsLen;
}

/**
 *   Check if the the length value of the given field is within the number specified in maxlength attribute
 *   The function may be applied as event handler for onkeypress
 */
function isInMaxlength(fld) {
    var mlength = fld.getAttribute ? parseInt(fld.getAttribute("maxlength")) : "";
    mlength = getAdjustedTextareaTextLength(fld, mlength);
    if (fld.value.length >= mlength) {
        return false;
    }
    else {
        return true;
    }
}
/**
 * Check if the the length value of the given field is within the number specified in maxlength attribute.
 * Confirm with user if want to truncate the text to the max length
 * The function may be applied as event handler for onchange and onblur
 */
function checkMaxlength(fld) {
    var checkResult = true;
    if (hasMaxlength(fld)) {
        var mlengthCfg = getMaxlength(fld);
        var mlength = getAdjustedTextareaTextLength(fld, mlengthCfg);
        if (fld.value.length > mlength) {
            if (confirm("Text is too long to fit the field. Do you want to truncate the text to the max length:" + mlengthCfg + "?")) {
                // cut off
                fld.value = fld.value.substring(0, mlength);
            }
            else {
                checkResult = false;
            }
        }
    }
    return checkResult;
}

/**
 * If the given field has the maxlength attribute, return the value of this attribute.
 * Note: Method field.getAttribute("maxlength") doesn't work for textarea until IE10 standard mode.
 *       Need use field.attributes.maxLength.value to get this attribute.
 *
 * @param fld
 */
function getMaxlength(fld) {
    var mlength = fld.getAttribute("maxlength") ? fld.getAttribute("maxlength") : fld.attributes.maxLength.value;
    return parseInt(mlength);
}

/**
 * If the given field has the maxlength attribute, return true, otherwise return false.
 * Note: Method field.getAttribute("maxlength") doesn't work for textarea until IE10 standard mode.
 *       Need use field.attributes.maxLength.value to get this attribute.
 *
 * @param fld
 */
function hasMaxlength(fld) {
    var hasFlag = false;
    try{
        var mlength = fld.getAttribute("maxlength") ? fld.getAttribute("maxlength") : fld.attributes.maxLength.value;
        hasFlag = mlength ? true : false;
    } catch (e){
        //If fail on getting maxLength value. return false.
        hasFlag = false;
    }
    return hasFlag;
}

function basePageOnReadyStateChange(id) {
    var isPageOnReadyStateChangeSuccess = true;

    var functionExists = eval("window.commonPageOnReadyStateChange");
    if (functionExists) {
        isPageOnReadyStateChangeSuccess = commonPageOnReadyStateChange(id);

        isPageOnReadyStateChangeSuccess = nvl(isPageOnReadyStateChangeSuccess, true);
    }

    if (isOnUnloadForDivPopupSuccess) {
        var functionExists = eval("window.handlePageOnReadyStateChange");
        if (functionExists) {
            isPageOnReadyStateChangeSuccess = handlePageOnReadyStateChange(id);

            isPageOnReadyStateChangeSuccess = nvl(isPageOnReadyStateChangeSuccess, true);
        }
    }

    return isPageOnReadyStateChangeSuccess;
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function basePageOnMouseMove(evt) {
    var event = fixEvent(evt);
    event.returnValue = true;
    if (eval(window.popupMouseMove)) {
        event.returnValue = popupMouseMove();
        event.returnValue = nvl(event.returnValue, true);
    }
}

/**
 *  @deprecated   Do not use it any more. Only required for the non-JQuery dialogs.
 */
function basePageOnMouseUp(evt) {
    var event = fixEvent(evt);

    if ((window.frameElement) && (window.frameElement.document.parentWindow) && (window.frameElement.document.parentWindow.moveState == 1)) {
        window.frameElement.document.parentWindow.popupMouseUp();
    }
}

function baseOnBodyKeyDown(evt) {
    var event = fixEvent(evt);
    // remove the logic to check Backspace key
    // The logic to check page change status is done in function baseOnBeforeUnload
}

function autoHideTROnLoad() {
    //logDebug("autoHideTROnLoad()");
    var begintime = new Date();
    //The search starts from the Text Node, not the DIV node.
    // So, the search will locate the text node first, then the 1st parent will return the DIV node followed by another
    // parent() call to locate the TR object.
    if(window["useJqxGrid"]){
        $("tr.dti-autoHideTR").addClass("dti-hide");
    } else {
        var trObjects = $("div[name='autoHideTR']").parent().parent();
        for (var i = 0; i < trObjects.length; i++) {
            autoHideTR(trObjects[i], true);
        }
    }

    endtime = new Date();
    //logDebug("Time spent in autoHideTROnLoad:" + (endtime.getTime() - begintime.getTime()) + "ms");
    return true;
}

function autoHideTR(trObject, isHideByDefault) {
    //logDebug("autoHideTR(trObject)");
    var begintime = new Date();
    var isHideTR = isHideByDefault;
    if (!isHideTR) {
        /*
         //isHideTR = (trObject.innerHTML.indexOf("class=\"visibleField ") == -1 ? true : false);
         isHideTR = ($(trObject).children("." + fieldClassEditable).length == 0)
         if (isHideTR) {
         isHideTR = ($(trObject).children("." + fieldClassReadonly).length == 0)
         }
         */
        // If isEditable attribute exists, the row is visible because it might contain either editable or readonly elements.
        isHideTR = ($(trObject).has('td[isEditable]').length == 0)

    }
    if(!window["useJqxGrid"]){
        // If all the TDs are hidden, hide the corresponding TR as well.
        if (isHideTR) {
            // Set the previous display value, only if it is not set yet.
            // This function is called several times during page processing - so set it for the first time only.
            if (trObject.style.getAttribute('previousDisplayValue') == null) {
                trObject.style.setAttribute("previousDisplayValue", trObject.style.display);
            }
            if (trObject.style.display !== "none") {
                trObject.style.display = "none";
            }
        } else {
            // If atleast one TD is visible, reset the TR display to the best known value - only if the TR is previously auto-hidden.
            var prevDisplayValue = "";
            if (trObject.style.getAttribute("previousDisplayValue") != null) {
                prevDisplayValue = trObject.style.getAttribute("previousDisplayValue") ;
                trObject.style.removeAttribute("previousDisplayValue")
                if (trObject.style.display !== prevDisplayValue) {
                    trObject.style.display = prevDisplayValue;
                }
            }
        }
    }  else {
        // If all the TDs are hidden, hide the corresponding TR as well.
        if (isHideTR) {
            hideShowElementByClassName(trObject, true);
        }
        else {
            hideShowElementAsTableRowByClassName(trObject, false);
        }
    }
    // Function autoHideTROnLoad is called after function enforceOBRForPageFields when loading page.
    // If there is a rule to show a field in hidden TR, autoHideTR will be called in function hideShowField.
    // Remove the autoHideTR div here, so function autoHideTROnLoad won't hide this TR again.
    if(!window["useJqxGrid"]) {
        var divObjects = $(trObject).find("div[name='autoHideTR']");
        if (divObjects.length > 0) {
            $(divObjects[0]).remove();
        }
    }
    endtime = new Date();
//    if (!isHideByDefault) {
//        logDebug("Time spent in autoHideTR:" + (endtime.getTime() - begintime.getTime()) + "ms");
//    }
}

function baseOnPropertyChange() {
    var e = window.event.srcElement;
    var p = window.event.propertyName;
    if (e.tagName.toUpperCase() == "TR") {
        if (p.toLowerCase()=="style.display") {
            //If previousDisplayValue attribute is set, then all it's child elements are hidden; so this element must be hidden as well.
            //eg. if all TDs are hidden, then TR must be hidden as well - otherwise, unnecessary whitespace rows will be created.
            if (e.style.getAttribute("previousDisplayValue") != null && e.style.display !== "none") {
                e.style.display = "none";
            } else {
                // field dependency/processDeps will blindly make the <TR> display style as block/inline, without checking
                // whether there is atleast one display <TD> exist. Enforce that logic here.
                autoHideTR(e);
            }
        }
    }
    return;
}

function selectRowWithProcessingDlg(gridId, rowId) {
    var needPrcoessingDialog = false;
    if (window.shouldDisplayProcessingDlgOnRowSelect) {
        needPrcoessingDialog = window.shouldDisplayProcessingDlgOnRowSelect(gridId);
    }
    if (needPrcoessingDialog) {
        showProcessingImgIndicator();
        setTimeout("selectRow('" + gridId + "','" + rowId + "')", 50);
    } else {
        selectRow(gridId, rowId);
    }
}

function selectRow(gridId, rowId) {
    first(getXMLDataForGridName(gridId));

    baseOnRowSelected(gridId, rowId);
    if (window.shouldDisplayProcessingDlgOnRowSelect) {
        if (shouldDisplayProcessingDlgOnRowSelect(gridId)) {
            if (window.handleOnSelectRowAsyncComplete) {
                window.handleOnSelectRowAsyncComplete(gridId, rowId);
            }
            hideProcessingImgIndicator();
        }
    }
}

function fireAjaxForSelectedRow(gridId, rowId) {
    var ajaxUrls = getObject("ajaxUrls");
    if (ajaxUrls != null) {
        functionExists = eval("window." + gridId + "_beforeFireAjaxOnSelectRow");
        if (functionExists) {
            eval(gridId + "_beforeFireAjaxOnSelectRow('" + rowId + "')");
        }
        var t = new Date();
        var originalFieldDeps = isProcessFieldDeps;
        try {
            isProcessFieldDeps = false;
            var XMLData = getXMLDataForGridName(gridId);
            fireAjax(ajaxUrls.getAttribute("value"), XMLData);
        } finally {
            if (originalFieldDeps == true) {
                isProcessFieldDeps = true;
            }
        }
        var t0 = new Date();
//            logDebug("Time spent in baseOnRowSelected::fireAjax:" + (t0.getTime() - t.getTime()) + "ms");
    }
}

function baseOnRowSelected(gridId, rowId) {
    //logDebug(getCurrentTime()+'::baseOnRowSelected('+gridId+', '+rowId+')');

    var beginTime = new Date();
    var isOnSelectRowEventSuccess = true;
    var searchStatus = -1;
    var XMLData = null;
    var selectedRecordset = null;
    var functionExists;

    if (rowId) {
        XMLData = getXMLDataForGridName(gridId);
        searchStatus = getRow(XMLData, rowId)
    }

    if (searchStatus >= 0)
    {
        setSelectedRow(gridId, rowId);

        if (!hasObject("CWBHeaderRow")) {
            // Do not enable all fields based on EDIT_IND, if the page is for custWebWb.
            // CustWebWB has specific logic implemented to disable BASE fields and those fields shouldn't get enabled.
            baseEnableDisableGridDetailFields('PRE', gridId);
        }

        maintainValueForAllMultiSelectFields(gridId);
        selectedRecordset = XMLData.recordset;

        fireAjaxForSelectedRow(gridId, rowId);

        maintainNoteImageForAllNoteFields();
        maintainValueForAllMultiSelectFields(gridId);

        if (selectedRecordset) {
            // Check if the processFieldDeps and pageEntitlements needs to be invoked after _selectRow
            // The default is false
            var invokeFieldDepsAndPageEntitlementAfter_selectRow = false;
            functionExists = eval("window.isFieldDepsAndPageEntitlementsAfter_selectRow");
            if (functionExists) {
                invokeFieldDepsAndPageEntitlementAfter_selectRow = isFieldDepsAndPageEntitlementsAfter_selectRow(gridId);
            }

            //  By default, these functions should be executed before commonOnRowSelectedv/XXX_selectRow
            //      because of either of those methods change the visibility or enabled attributes of page elements,
            //      these functions may overwrite the custom changes.
            if (!invokeFieldDepsAndPageEntitlementAfter_selectRow) {
                //  Execute field dependency logic, if it exists
                functionExists = eval("window.processFieldDeps");
                if (functionExists) {
                    var t1 = new Date();
                    processFieldDeps();
                    var t2 = new Date();
//                    logDebug("Time spent in baseOnRowSelected::processFieldDeps:" + (t2.getTime() - t1.getTime()) + "ms");
                }

                if (window.document.readyState == "complete") {
                    // This baseOnRowSelected gets called as soon as the table is ready irrespective of the document is ready or not.
                    // Called by selectFirstRowInGrid via alternate_colors->readyStateReady->userStateReady->commonStateReady
                    // These events gets called as soon as the table is ready.
                    var startTime = new Date();
                    functionExists = eval("window.enforceOBRForGridFields");
                    if (functionExists) {
                        enforceOBRForGridFields(gridId, rowId);
                    }
                    var endTime = new Date();
                    //alert('Total timespent in baseOnRowSelected('+gridId+', '+rowId+').OBRForGrid is ' + (endTime.getTime() - beginTime.getTime()) + "ms");
                }

                // Next, execute the page entitlements for this row
                functionExists = eval("window.pageEntitlements");
                if (functionExists) {
                    var t3 = new Date();
                    pageEntitlements(true, gridId);
                    var t4 = new Date();
//                    logDebug("Time spent in baseOnRowSelected::pageEntitlements:" + (t4.getTime() - t3.getTime()) + "ms");
                }
            }
        }

        functionExists = eval("window.commonOnRowSelected");
        if (functionExists) {
            var t5 = new Date();
            isOnSelectRowEventSuccess = commonOnRowSelected(gridId, rowId);
            var t6 = new Date();
//            logDebug("Time spent in baseOnRowSelected::commonOnRowSelected:" + (t6.getTime() - t5.getTime()) + "ms");
        }

        if (isOnSelectRowEventSuccess) {
            functionExists = eval("window." + gridId + "_selectRow");
            if (functionExists) {
                isOnSelectRowEventSuccess = eval(gridId + "_selectRow('" + rowId + "')");
                isOnSelectRowEventSuccess = nvl(isOnSelectRowEventSuccess, true);
            }
        }
        //  These functions should be executed after commonOnRowSelectedv/XXX_selectRow
        //      if data changed that may effect the visibility or enabled attributes of page elements
        if (isOnSelectRowEventSuccess && invokeFieldDepsAndPageEntitlementAfter_selectRow && selectedRecordset) {
            //  Execute field dependency logic, if it exists
            functionExists = eval("window.processFieldDeps");
            if (functionExists) {
                processFieldDeps();
            }
            // execute OBR rule
            if (window.document.readyState == "complete") {
                functionExists = eval("window.enforceOBRForGridFields");
                if (functionExists) {
                    enforceOBRForGridFields(gridId, rowId);
                }
            }
            // Next, execute the page entitlements for this row
            functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(true, gridId);
            }
        }

        // Finally, Enable/disable grid detail fields based on the EDIT_IND.
        baseEnableDisableGridDetailFields('POST', gridId);

        showGridDetailDiv(gridId);
    }
    else {
        hideGridDetailDiv(gridId);
        setSelectedRow(gridId, null);
        isOnSelectRowEventSuccess = false;
    }

    var endTime = new Date();
    logDebug("Time spent in baseOnRowSelected:" + (endTime.getTime() - beginTime.getTime()) + "ms");
    setAllNumbersColorFields();
    return isOnSelectRowEventSuccess;
}

function baseEnableDisableGridDetailFields(stageCode, gridId) {
    var beginTime = new Date();

    // This needs to be executed for all grids because the page entitlement (or other app level logic) could executes
    // for all grid form fields and will make a non-editable row fields as editable.

    // Eg. Coverage-Component Scenario

    // When a row in Coverage grid is selected, it automatically selects the 1st row in the Component grid.

    // If we enforce the following logic only for the grid id passed to this function, then all fields for Coverage grid
    // will be disabled based on EDIT_IND successfully. However, since the process is going to select the 1st row in
    // the Component grid automatically, then this function will be executed for the Component grid, which fires
    // page entitlements. The page entitlement could potentially enable fields associated with the Coverage grid,
    // which is in-correct because the selected coverage row is non-editable.

    for (var tbl=0; tbl<gridnames.length; tbl++) {
        var dataGrid = getXMLDataForGridName(gridnames[tbl]);
        var gridTbl = getTableForXMLData(dataGrid);

        if (stageCode == "PRE" && gridId != gridTbl.id) {

            // The PRE stage is used to look for all fields that are currently hidden or readOnly, but was visible and
            // editable during initial page load and revert them back to the page load stage - ie. make them as visible
            // and editable again, so that the OBR or page entitlement or the page specific logic can determine
            // what to do for the currently selected row.

            // This must be done only for the current grid because the OBR, page entitlement and the page specific logic
            // will be executed again for the current grid only.
            //
            // This should not be done for all grids because the other grid's OBR, page entitlement and the page
            // specific logic may have already executed and some of the field's visibility/editable attribute might
            // have changed as per business requirement. Since the OBR, page entitlement and the page specific logic are
            // not going to be called again for other grids, if we revert the grid field's attribute to the initial page
            // load state, then we inadvertently remove the already enforced business requirement on those fields.

            continue;
        }

        functionExists = eval("window.commonEnableDisableGridDetailFields");
        if (functionExists) {
            commonEnableDisableGridDetailFields(dataGrid, getTableProperty(gridTbl, "gridDetailDivId"), getTableProperty(gridTbl, "selectedRowId"), stageCode);
        } else {
            var detailFormElements = null;
            var currEditInd = dataGrid.recordset('EDIT_IND').value.toUpperCase();
            var isDisabled = (currEditInd == 'N');
            if (isDisabled && stageCode == 'POST') {
                // The row is non-editable. Get all enabled fields associated with the grid and make them read-only them.
                // Using JQuery, find all "editable" input fields (irrespective of input, select etc tags) which has
                // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.
                detailFormElements = $("td[isEditable='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
            } else if(!isDisabled && stageCode == 'PRE') {
                // The row is an editable. Get all readonly fields associated with the grid and make them editable.
                // Using JQuery, find all "non-editable" input fields (irrespective of input, select etc tags) which has
                // the grid id as the "dataSrc" & an attribute "dataFld" associated with the field.

                //Look for all hidden fields that are visible & editable when the page was loaded initially and make them editable.
                detailFormElements = $("td[isEditableWhenVisible][isEditableOnInitialPageLoad='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
                for (var i=0; i<detailFormElements.length; i++) {
                    /*
                     Radio Option and Checkbox HTML elements are rendered within a table of its own.
                     So, locate the correct <TD> that holds the HTML elements to set "isEditableWhenVisible" attribute value to "Y".
                     */
                    var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
                    for (var j = 0; j < suffixArray.length; j++) {
                        var containerId = detailFormElements[i].name + suffixArray[j];
                        if (hasObject(containerId)) {
                            var container = $("#" + containerId);
                            container.attr("isEditableWhenVisible", "Y");
                        }
                    }
                    //Show the field as editable.
                    hideShowField(detailFormElements[i], false);
                }

                //Look for all read-only fields and make them editable.
                detailFormElements = $("td[isEditable='N'][isEditableOnInitialPageLoad='Y'] [dataSrc='#" + dataGrid.id + "'][dataFld]:input");
            }
            if (detailFormElements) {
                for (var i = 0; i < detailFormElements.length; i++) {
                    enableDisableField(detailFormElements[i], isDisabled);
                }
            }
        }
    }

    var endTime = new Date();
    logDebug("Time spent in baseOnRowSelected:" + (endTime.getTime() - beginTime.getTime()) + "ms");
    return true;
}


function baseOnGlobalSearch(field, evt) {
    var event = fixEvent(evt);

    event.returnValue = true;
    var isGlobalSearchEventSuccess = event.returnValue;
    if (field) {
        showProcessingImgIndicator();

        if (field.value) {
            field.value = field.value.trim();
        }

        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnGlobalSearch");
        if (functionExists) {
            event.returnValue = commonOnGlobalSearch(field, event);

            // Handle the common handleOnBlur function not setting the returnValue
            isGlobalSearchEventSuccess = nvl(event.returnValue, true);
        }

        if (isGlobalSearchEventSuccess) {
            functionExists = eval("window.handleOnGlobalSearch");
            if (functionExists) {
                event.returnValue = handleOnGlobalSearch(field, event);

                // Handle the custom handleOnBlur function not setting the returnValue
                isGlobalSearchEventSuccess = nvl(event.returnValue, true);
            }
        }
    }
    else {
        isGlobalSearchEventSuccess = false;
    }
    event.returnValue = isGlobalSearchEventSuccess;
}

function baseOnBlur(dataType, evt) {
    var event = fixEvent(evt);
    //logDebug(getCurrentTime()+'::baseOnBlur('+dataType+')');
    //var msg = "Test Debug Msg";
    //logDebug(msg);
    //debug(msg);
    event.returnValue = true;
    var eventSrcElement = event.srcElement;
    var field = event.srcElement;

    try {
        if (field.hasAttribute(ATTRIBUTE_ORIGINAL_VALUE)) {
            if (field.getAttribute(ATTRIBUTE_IS_ON_CHANGE_FIRED) == "N") {
                //check whether the current row is changed.
                var isGridField = false;
                var isCurrentRowChanged = false;
                if (field.hasAttribute(ATTRIBUTE_ORIGINAL_ROW_ID)) {
                    isGridField = true;
                    var originalRowId = field.getAttribute(ATTRIBUTE_ORIGINAL_ROW_ID);
                    try {
                        var dataSrc = getDataSrc(field);
                        var dataFld = getDataField(field);
                        if (dataSrc && dataFld) {
                            var gridId = dataSrc.substring(1, dataSrc.length - 1);
                            var dataGrid = getXMLDataForGridName(gridId);
                            var currentId = dataGrid.recordset("id").value;
                            if (currentId != originalRowId) {
                                isCurrentRowChanged = true;
                            }
                        }
                    } catch (ex) {
                        // catach the exception to avoid unexpected error related with grid.
                    }
                }
                if (isGridField && isCurrentRowChanged) {
                    // The current row is changed, do nothing
                } else {
                    if (window["useJqxGrid"]) {
                        if (dti.oasis.field.isFieldValueChanged(field)) {
                            dispatchElementEvent(field, "change");
                        }
                    } else {
                        if (field.getAttribute(ATTRIBUTE_ORIGINAL_VALUE) != field.value) {
                            dispatchElementEvent(field, "change");
                        }
                    }
                }
            }
        }
    } catch (ex) {
        // catch the exception to avoid unexpected error
    }

    field.removeAttribute(ATTRIBUTE_IS_ON_CHANGE_FIRED);
    field.removeAttribute(ATTRIBUTE_ORIGINAL_VALUE);
    field.removeAttribute(ATTRIBUTE_ORIGINAL_ROW_ID);
    selectedIndexesForDropdownField = null;


    //    logDebug("baseOnBlur("+dataType+") for field: "+field.name);
    if (field.name.endsWith(DISPLAY_FIELD_EXTENTION)) {
        field = getOriginalObject(field, normalizeFieldName(field));
    }
    var isPhoneField = (field.className == "clsPhone");
    var isDateField = (field.className == "clsDate") || (dataType == "DT");
    var isNumberField = (field.className == "clsNum") || (dataType == "NM" || dataType == "CU" || dataType == "CF" || dataType == "PT");
    var isNumberFormattedField = (dataType == "NM");
    var isCurrencyFormattedField = (dataType == "CF" || dataType == "CU");
    var isPercentageField = (dataType == "PT");
    var isAlpha = (dataType == "AO" || dataType == "UA" || dataType == "LA");
    var isAlphaNumeric = (dataType == "AN" || dataType == "UN" || dataType == "LN");
    var isUpperCase = (dataType == "UA" || dataType == "UN");
    var isLowerCase = (dataType == "LA" || dataType == "LN");
    var isUpperCaseText = (dataType == "UT");
    var isLowerCaseText = (dataType == "LT");
    var isDateTimeField = (dataType == DATATYPE_TIME);
    var isLocalPhoneNumberField = (dataType == DATATYPE_PHONE);


    var isBlurEventSuccess = event.returnValue;
    if (isBlurEventSuccess) {

        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnBlur");
        if (functionExists) {
            event.returnValue = commonOnBlur(getObject(field.name), event);

            // Handle the common handleOnBlur function not setting the returnValue
            isBlurEventSuccess = nvl(event.returnValue, true);
        }

        if (isBlurEventSuccess) {
            functionExists = eval("window.handleOnBlur");
            if (functionExists) {
                event.returnValue = handleOnBlur(getObject(field.name), event);

                // Handle the custom handleOnBlur function not setting the returnValue
                isBlurEventSuccess = nvl(event.returnValue, true);
            }
        }
    }

    event.returnValue = isBlurEventSuccess;

    if (isPhoneField) {
        isBlurEventSuccess = phonemaskclear(event);
    } else if (isCurrencyFormattedField) {
        isBlurEventSuccess = formatMoneyFldVal(field);
    } else if (isPercentageField) {
        isBlurEventSuccess = formatPctFldVal(field);
    } else if (isDateField) {
        isBlurEventSuccess = datemaskeditclear(event);
    } else if (isNumberFormattedField && eventSrcElement.getAttribute("formatPattern") != null){
        formatNumberFormatted($(eventSrcElement), eventSrcElement.getAttribute("formatPattern"));
        if (window["useJqxGrid"]) {
            dti.oasis.grid.syncDetailFieldToGrid(eventSrcElement.name);
        }
    }

    isBlurEventSuccess = nvl(isBlurEventSuccess, true);
    event.returnValue = isBlurEventSuccess;
}

var selectedIndexesForDropdownField = null;   // backup selected indexes for both select, multi-select fields

function getSelectedIndexesForDropdownField(field){
    var selectedIndexes = new Array();
    if (field.options){
        for (var i=0; i<field.options.length; i++) {
            if (field.options[i].selected) selectedIndexes.push(i);
        }
    }
    return selectedIndexes;
}

var ATTRIBUTE_IS_ON_CHANGE_FIRED = "ATTRIBUTE_IS_ON_CHANGE_FIRED";
var ATTRIBUTE_ORIGINAL_VALUE = "ATTRIBUTE_ORIGINAL_VALUE";
var ATTRIBUTE_ORIGINAL_ROW_ID = "ATTRIBUTE_ORIGINAL_ROW_ID";

function baseOnFocus(dataType, evt) {
    var event = fixEvent(evt);
    //logDebug(getCurrentTime()+'::baseOnFocus('+dataType+')');
    event.returnValue = true;
    var field = event.srcElement;
    var isPhoneField = (field.className == "clsPhone");
    var isDateField = (field.className == "clsDate") || (dataType == "DT");
    var isNumberField = (field.className == "clsNum") || (dataType == "NM" || dataType == "CU" || dataType == "CF" || dataType == "PT");
    var isCurrencyFormattedField = (dataType == "CF" || dataType == "CU");
    var isNumberFormattedField = (dataType == "NM");
    var isPercentageField = (dataType == "PT");
    var isSelectField = (field.tagName.toUpperCase() == 'SELECT');
    var isLocalPhoneNumberField = (dataType == DATATYPE_PHONE);

    valueBeforeFocus = field.value;
    var isFocusEventSuccess = event.returnValue;
    if (isFocusEventSuccess) {
        if (isSelectField) selectedIndexesForDropdownField = getSelectedIndexesForDropdownField(field); // store original value
        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnFocus");
        if (functionExists) {
            event.returnValue = commonOnFocus(getObject(field.name), event);

            // Handle the common handleOnFocus function not setting the returnValue
            isFocusEventSuccess = nvl(event.returnValue, true);
        }

        if (isFocusEventSuccess) {
            functionExists = eval("window.handleOnFocus");
            if (functionExists) {
                event.returnValue = handleOnFocus(getObject(field.name), event);

                // Handle the custom handleOnFocus function not setting the returnValue
                isFocusEventSuccess = nvl(event.returnValue, true);
            }
        }

    }
    event.returnValue = isFocusEventSuccess;

    if (isPhoneField) {
        phonemask(event);
    }
    if (isDateField) {
        datemask(event);
    }
    if (isCurrencyFormattedField) {
        unformatMoneyFldVal(field);
        field.select();
    }
    if (isPercentageField) {
        unformatPctFldVal(field);
        field.select();
    }

    //onFocus: Instead unformatting the visible field we are now simply replacing the value
    //with the value of the hidden field
    if(field.name.endsWith(DISPLAY_FIELD_EXTENTION) && isNumberFormattedField && field.getAttribute("formatPattern") != null) {
        var origField = getOriginalObject(field, normalizeFieldName(field));
        $(field).val($(origField).val());
        field.select();
    }


    var fieldTagName = field.tagName.toLowerCase();
    if (fieldTagName == "textarea" || (fieldTagName == "input" && field.type && field.type.toLowerCase() == "text")) {
        field.setAttribute(ATTRIBUTE_IS_ON_CHANGE_FIRED, "N");
        field.setAttribute(ATTRIBUTE_ORIGINAL_VALUE, field.value);
        try{
            //set current row id if exists
            var dataSrc = getDataSrc(field);
            var dataFld = getDataField(field);
            if (dataSrc && dataFld) {
                var gridId = dataSrc.substring(1, dataSrc.length - 1);

                if (window["useJqxGrid"]) {
                    currentlySelectedGridId = gridId;
                }

                var dataGrid = getXMLDataForGridName(gridId);
                var currentId = dataGrid.recordset("id").value;
                if (currentId) {
                    field.setAttribute(ATTRIBUTE_ORIGINAL_ROW_ID, currentId);
                    // The field.value may be different as xml column value because the data hasn't been synced
                    // Use the value in xml column as original value in this case.
                    field.setAttribute(ATTRIBUTE_ORIGINAL_VALUE, dataGrid.recordset(dataFld).value);
                }
            }
        } catch (ex) {
            // catach the exception to avoid unexpected error related with grid.
        }

    }

}

function callHrefOnRow(srcElement) {
    if (typeof srcElement.parentElement.parentElement.href != 'undefined') {
        var hrefOnRow = srcElement.parentElement.parentElement.href;
        hrefOnRow = replace(hrefOnRow, "selectRowWithProcessingDlg", "selectRow");
        eval(hrefOnRow);
    }
}

/*
 handle on href on field
 */
function handleOnGridHref(gridId, href) {
    var realscript = processHrefPlaceholders(href, gridId);
    if (realscript.indexOf("javascript:") >= 0) {
        var realscript = realscript.substring(realscript.indexOf("javascript:") + 11);
        eval(realscript);
    }
    else {
        setWindowLocation(realscript);
    }
}

/*
 handle on href on grid column
 */
function handleOnFieldHref(href) {
    var realscript = processHrefPlaceholders(href, null);
    if (realscript.indexOf("javascript:") >= 0) {
        var realscript = realscript.substring(realscript.indexOf("javascript:") + 11);
        eval(realscript);
    }
    else {
        setWindowLocation(realscript);
    }
}

/*
 repleace the place holder with record value/field value
 */
function processHrefPlaceholders(href, gridId) {
    var hrefString = href;
    var delimiter = "^";
    var scriptString = "";
    var isDelim = false;
    var fieldName = "";
    var isFieldInGrid = false;

    // Check for optional special delimiter (must be at first)
    if (hrefString.indexOf("[") == 0) {
        endDelimiterPos = hrefString.indexOf("]", 0);
        delimiter = hrefString.substring(1, endDelimiterPos);
        hrefString = hrefString.substring(endDelimiterPos + 1);
    }
    for (var i = 0; i < hrefString.length; i++) {
        // get character
        var str = hrefString.substring(i, i + 1);
        // we've encountered an opening delimiter before
        if (isDelim) {
            // we've found the closing delimiter, reset the flag
            if (str == delimiter) {
                isDelim = false;
                // we've found the text between the opening and closing
                // delimiters. This should be an column/field Name.
                isFieldInGrid = false;
                if (gridId != null) {
                    //check whether it is a column in grid
                    if (isFieldDefinedForGrid(gridId, "C" + fieldName.toUpperCase())) {
                        val = getXMLDataForGridName(gridId).recordset("C" + fieldName.toUpperCase()).value;
                        isFieldInGrid = true;
                    }
                    if (isFieldInGrid && val.indexOf("javascript:selectRowWithProcessingDlg(") >= 0) {
                        // The field id is the anchor column. Retrieve the row id instead.
                        val = getXMLDataForGridName(gridId).recordset("id").value;
                        isFieldInGrid = true;
                    }
                }
                if (!isFieldInGrid) {
                    //Get the value of field
                    val = getObjectValue(fieldName);
                }
                if (val == null) {
                    val = "";
                }
                if (!(href.indexOf("javascript:") >= 0)) {
                    val = encodeURIComponent(val);
                }
                // stick the value in the script
                scriptString += val;

                //reset field name
                fieldName = "";
            }
            else {
                //Add the character to field name
                fieldName += str;
            }
        }
        else {
            // we found a beginning delimiter
            if (str == delimiter) {
                isDelim = true;
            }
            else
            // we found some valid script, append it
                scriptString += str;
        }
    }
    return scriptString;
}


/**
 Gets selected text.
 */
function getSelectedText() {
    var activeElement = document.activeElement;
    if(window["useJqxGrid"] && (dti.inpututils.isTextareaField(activeElement) || dti.inpututils.isTextField(activeElement))){
        return activeElement.value.slice(activeElement.selectionStart, activeElement.selectionEnd);
    }
    else if (window.getSelection) {
        // This technique is the most likely to be standardized.
        return window.getSelection().toString();
    }
    else if (document.getSelection) {
        // This is an older, simpler technique that returns a string.
        return document.getSelection();
    }
    else if (document.selection) {
        // This is the IE-specific technique.
        return document.selection.createRange().text;
    }
}

function baseOnKeyPress(dataType, evt) {
    var event = fixEvent(evt);
    //logDebug(getCurrentTime()+'::baseOnKeyPress('+dataType+')');
    event.returnValue = true;
    var field = event.srcElement;
    var isPhoneField = (field.className == "clsPhone");
    var isDateField = (field.className == "clsDate") || (dataType == "DT");
    var isNumberField = (field.className == "clsNum") || (dataType == "NM" || dataType == "CU" || dataType == "CF" || dataType == "PT");
    var isCurrencyFormattedField = (dataType == "CF" || dataType == "CU");
    var isAlpha = (dataType == "AO" || dataType == "UA" || dataType == "LA");
    var isAlphaNumeric = (dataType == "AN" || dataType == "UN" || dataType == "LN");
    var isUpperCase = (dataType == "UA" || dataType == "UN");
    var isLowerCase = (dataType == "LA" || dataType == "LN");
    var isTimeField = dataType == "TM";
    var isUpperCaseText = (dataType == "UT");
    var isLowerCaseText = (dataType == "LT");

    var isCharTypedModified = false;

    // Check to see if there is selected text.
    if (getSelectedText() == "") {
        // If nothing is selected, then check the maxlength.
        if (field.getAttribute("maxlength")) {
            event.returnValue = isInMaxlength(field);
        }
    }

    var isKeyPressEventSuccess = event.returnValue;

    if (field.className) {
        // Do not honor the key press if this is a Finder field with class = noEntryFinder or noEntryFinderReq or noEntryReq
        if (hasClassName(field, "noEntryFinder")
            || hasClassName(field, "noEntryFinderReq")
            || hasClassName(field, "noEntryReq")
            || hasClassName(field, "noEntryMultipleSelectPopupReq")) {
            isKeyPressEventSuccess = false;
        }
    }

    // For the upper case and lower case text field, we want to uppcase/lowcase the text when user is entring.
    // These logics should be executed before commonOnKeyPress and handleOnKeyPress
    if (isKeyPressEventSuccess) {
        //Skip non-text or non-textarea field
        if(dti.inpututils.isTextField(field) || dti.inpututils.isTextareaField(field)) {
            if (isUpperCaseText) {
                var retVal = dti.inpututils.replaceTypedChar(event, dti.inpututils.toUpperCase);
                if (typeof retVal != "undefined") {
                    isCharTypedModified = true;
                }
            }
            else if (isLowerCaseText) {
                var retVal = dti.inpututils.replaceTypedChar(event, dti.inpututils.toLowerCase);
                if (typeof retVal != "undefined") {
                    isCharTypedModified = true;
                }
            }
        }
    }

    if (isKeyPressEventSuccess) {

        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnKeyPress");
        if (functionExists) {
            event.returnValue = commonOnKeyPress(getObject(field.name), event);

            // Handle the common handleOnKeyPress function not setting the returnValue
            isKeyPressEventSuccess = nvl(event.returnValue, true);

        }

        if (isKeyPressEventSuccess) {
            functionExists = eval("window.handleOnKeyPress");
            if (functionExists) {
                event.returnValue = handleOnKeyPress(getObject(field.name), event);

                // Handle the custom handleOnKeyPress function not setting the returnValue
                isKeyPressEventSuccess = nvl(event.returnValue, true);
            }
        }

    }
    event.returnValue = isKeyPressEventSuccess;

    if (isPhoneField) {
        phoneformat(event);
    }
    if (isDateField) {
        dateformat(event);
    }

    if (isKeyPressEventSuccess) {
        dti.inpututils.setWindowEvent(event);
        if (isAlpha && isUpperCase) {
            isKeyPressEventSuccess = alphaUpper(field);
        }
        else if (isAlpha && isLowerCase) {
            isKeyPressEventSuccess = alphaLower(field);
        }
        else if (isAlphaNumeric && isUpperCase) {
            isKeyPressEventSuccess = alphaNumericUpper(field);
        }
        else if (isAlphaNumeric && isLowerCase) {
            isKeyPressEventSuccess = alphaNumericLower(field);
        }
        else if (isAlpha) {
            isKeyPressEventSuccess = alpha(field);
        }
        else if (isAlphaNumeric) {
            isKeyPressEventSuccess = alphaNumeric(field);
        } else if (isTimeField) {
            //Block any inputting to avoid the invalid value
            isKeyPressEventSuccess = false;
        }
        dti.inpututils.setWindowEvent(null);
        event.returnValue = isKeyPressEventSuccess;
    }

    if (isCharTypedModified) {
        event.returnValue = false;
    }
}

function baseOnKeyDown(dataType, evt) {
    var event = fixEvent(evt);

    //logDebug(getCurrentTime()+'::baseOnKeyDown('+dataType+')');
    event.returnValue = true;
    var field = event.srcElement;
    var isPhoneField = (field.className == "clsPhone");
    var isDateField = (field.className == "clsDate") || (dataType == "DT");
    var isNumberField = (field.className == "clsNum") || (dataType == "NM" || dataType == "CU" || dataType == "CF" || dataType == "PT");
    var isCurrencyFormattedField = (dataType == "CF" || dataType == "CU");
    var isLocalPhoneNumberField = (dataType == DATATYPE_PHONE);
    var isKeyDownEventSuccess = event.returnValue;

    //logDebug('#################  field.type: '+field.type);
    valueBeforeKeyDown = field.value;
    var datasrc = getDataSrc(field);
    if (field.type == 'text' && typeof(datasrc) != 'undefined') {
        if (isNavigationalKey(event.keyCode) && datasrc.length > 0) {
            selectAnotherRowInBoundGrid(field);
        } else if (isNavigationalKeyWithEnter(event.keyCode) && field.name.startsWith("txt")) {
            // editable grid input field
            var isAllowed = false;
            if (window.isOKToNavigateFromField) {
                isAllowed = window.isOKToNavigateFromField(field);
            }
            if (isAllowed) {
                event.cancelBubble = true;
                selectAnotherRowInEditableGrid(field);
            }
        }
    }

    if (field.className) {
        // Do not honor the key press if this is a Finder field with class = noEntryFinder or noEntryFinderReq or noEntryReq
        if ((hasClassName(field, "noEntryFinder")
            || hasClassName(field, "noEntryFinderReq")
            || hasClassName(field, "noEntryReq")
            || hasClassName(field, "noEntryMultipleSelectPopupReq")) &&
            (event.keyCode == 8 || // backspace key
            event.keyCode == 46)    // delete key
        ) {
            isKeyDownEventSuccess = false;
        }
    }

    if (isKeyDownEventSuccess) {

        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnKeyDown");
        if (functionExists) {
            event.returnValue = commonOnKeyDown(getObject(field.name), event);

            // Handle the common handleOnKeyDown function not setting the returnValue
            isKeyDownEventSuccess = nvl(event.returnValue, true);

        }

        if (isKeyDownEventSuccess) {
            functionExists = eval("window.handleOnKeyDown");
            if (functionExists) {
                event.returnValue = handleOnKeyDown(getObject(field.name), event);

                // Handle the custom handleOnKeyDown function not setting the returnValue
                isKeyDownEventSuccess = nvl(event.returnValue, true);
            }
        }
    }
    event.returnValue = isKeyDownEventSuccess;
    if (isPhoneField) {
        phoneformat(event);
    }
    if (isDateField) {
        dateformat(event);
    }

    if (isNumberField) {
        // Fix issue 106429, system should skip number format if it is a select field.
        if(field.tagName.toUpperCase() != 'SELECT'){
            numformat(event);
        }
    }
}

function isOKToNavigateFromField(field) {
    // Turn on this by default. Developer can override this function to turn it off.
    return true;
}

function baseOnKeyUp(dataType, evt) {
    var event = fixEvent(evt);
    //logDebug(getCurrentTime()+'::baseOnKeyUp('+dataType+')');
    event.returnValue = true;
    var field = event.srcElement;
    var isLocalPhoneNumberField = (dataType == DATATYPE_PHONE);
    var isKeyUpEventSuccess = event.returnValue;

    if (isKeyUpEventSuccess) {
        //***  Execute system level logic, if one exists ***
        var functionExists = eval("window.commonOnKeyUp");
        if (functionExists) {
            event.returnValue = commonOnKeyUp(getObject(field.name), event);
            // Handle the common handleOnKeyUp function not setting the returnValue
            isKeyUpEventSuccess = nvl(event.returnValue, true);
        }
        if (isKeyUpEventSuccess) {
            functionExists = eval("window.handleOnKeyUp");
            if (functionExists) {
                event.returnValue = handleOnKeyUp(getObject(field.name), event);
                // Handle the custom handleOnKeyUp function not setting the returnValue
                isKeyUpEventSuccess = nvl(event.returnValue, true);
            }
        }
    }

    if (isLocalPhoneNumberField) {
        formatPhoneNumber(event);
    }

    isKeyUpEventSuccess = nvl(isKeyUpEventSuccess, true);
    event.returnValue = isKeyUpEventSuccess;
}


function baseOnChange(dataType, evt) {
    var event = fixEvent(evt);
    //logDebug(getCurrentTime()+'::baseOnChange('+dataType+')');
    /*
     Flow of logic:
     ~~~~~~~~~~~~~~
     1. Validation for input field.
     2. If the validation fails, set field value to the original value.
     3. If the validation succeeds, set the value to the hidden field DISPLAY_FIELD_EXTENTION.
     4. If system logic is successfully executed or system level logic doesnt exists, execute page level logic (if exists).
     5. If system level or page level logic doesnt exists, execute handleOnChange function (backward compatibility).
     */
    //try {
    // *** Execute core logic ***
    event.returnValue = true;
    var eventSrcElement = event.srcElement;
    var field = event.srcElement;

    var isOnchangeFired = field.getAttribute(ATTRIBUTE_IS_ON_CHANGE_FIRED);
    if (isOnchangeFired == "N") {
        field.setAttribute(ATTRIBUTE_IS_ON_CHANGE_FIRED, "Y");
    }

//    logDebug("baseOnChange("+dataType+") for field: "+field.name);
    if(field.name.endsWith(DISPLAY_FIELD_EXTENTION)){
        field = getObject(normalizeFieldName(field));
    }

    var isPhoneField = (field.className == "clsPhone");
    var isDateField = (field.className == "clsDate") || (dataType == "DT");
    var isNumberField = (field.className == "clsNum") || (dataType == "NM" || dataType == "CU" || dataType == "CF" || dataType == "PT");
    var isCurrencyFormattedField = (dataType == "CF" || dataType == "CU");
    var isNumberFormattedField = (dataType == "NM");
    var isSelectField = (field.tagName.toUpperCase() == 'SELECT');
    var isTimeField = (dataType == DATATYPE_TIME);
    var isPercentageField = (dataType == "PT");
    var isAlpha = (dataType == "AO" || dataType == "UA" || dataType == "LA");
    var isAlphaNumeric = (dataType == "AN" || dataType == "UN" || dataType == "LN");
    var isUpperCase = (dataType == "UA" || dataType == "UN");
    var isLowerCase = (dataType == "LA" || dataType == "LN");
    var isUpperCaseText = (dataType == "UT");
    var isLowerCaseText = (dataType == "LT");
    var isDateTimeField = (dataType == DATATYPE_TIME);
    var isLocalPhoneNumberField = (dataType == DATATYPE_PHONE);

    // BEGIN of validation on input field value.
    var passValidation = true;

    if (hasMaxlength(field)) {
        passValidation = checkMaxlength(eventSrcElement);
    }
    if (passValidation) {
        if (isNumberField) {
            var val = eventSrcElement.value;
            if (dataType == "CF" || dataType == "CU")
            {
                val = unformatMoneyStrValAsStr(eventSrcElement.value);
            }
            if(!checknum(val)){
                passValidation = false;
                alert("Enter a numeric value in "+getLabel(eventSrcElement)+" field");
            }
        } else if (isAlpha) {
            if (!isAlphabetic(eventSrcElement.value)) {
                passValidation = false;
                alert("Invalid data entry. Please enter only alphabets.");
            }
        } else if (isAlphaNumeric) {
            if (!isAlphanumeric(eventSrcElement.value)) {
                passValidation = false;
                alert("Invalid data entry. Please enter only alphabets and numbers.");
            }
        } else if (isTimeField) {
            passValidation = isReasonableDate(eventSrcElement);
        } else if (isPhoneField) {
            passValidation = checkUSPhone(eventSrcElement, true);
        } else if (isLocalPhoneNumberField) {
            passValidation = isValidPhoneNumberField(eventSrcElement, true);
        } else if (isDateField) {
            passValidation = datemaskclear(event);
        }
    }
    if (passValidation) {
        if (isSelectField) {
            passValidation = !isSelectedOptionExpired(eventSrcElement);
        }
    }
    // END of validation on input field value.

    // Restore the original value if validation fails, then do not execute the logic for change.
    // If there is no original value, reset the value to blank, continue to execute the logic for change.
    if (!passValidation) {
        if (isSelectField) {
            // de-select all options first
            for (var i = 0; i < eventSrcElement.options.length; i++) {
                eventSrcElement.options[i].selected = false;
            }
            // restore the selected options
            if (selectedIndexesForDropdownField != null) {
                for (var i = 0; i < selectedIndexesForDropdownField.length; i++) {
                    eventSrcElement.options[selectedIndexesForDropdownField[i]].selected = true;
                }
                event.returnValue = false;
                event.cancelBubble = true;
            }
        } else {
            var originalValue = eventSrcElement.getAttribute(ATTRIBUTE_ORIGINAL_VALUE);
            if (originalValue) {
                // set original value back.
                eventSrcElement.value = originalValue;
                event.returnValue = false;
                event.cancelBubble = true;
            } else {
                eventSrcElement.value = "";
            }
        }
    }

    // UPDATE related fields. For example:  DISPLAY_FIELD_EXTENTION, MSVAL etc.
    var isChangedEventSuccess = event.returnValue;
    if (isChangedEventSuccess) {
        if(dti.inpututils.isTextField(field) || dti.inpututils.isTextareaField(field)) {
            if (isUpperCase) {
                field.value = (isChangedEventSuccess ? field.value.toUpperCase() : field.value);
            }
            else if (isLowerCase) {
                field.value = (isChangedEventSuccess ? field.value.toLowerCase() : field.value);
            }
            // Automatically change to uppercase/lowercase if type is UT or LT
            if (isUpperCaseText) {
                field.value = field.value.toUpperCase();
            } else if (isLowerCaseText) {
                field.value = field.value.toLowerCase();
            }
        }

        // set DISPLAY_FIELD_EXTENTION value to hide field.
        if (isDateField || isDateTimeField) {
            postDateChange();
            if(!dateFormatUS && eventSrcElement.name.endsWith(DISPLAY_FIELD_EXTENTION)){
                updateHiddenFieldForDateField(eventSrcElement, field);
            }
        }

        if (isLocalPhoneNumberField) {
            if (eventSrcElement.name.endsWith(DISPLAY_FIELD_EXTENTION)) {
                newUpdateUnformattedPhoneNumberField(eventSrcElement, field);
            }
        }

        if(isNumberFormattedField && eventSrcElement.getAttribute("formatPattern") != null){
            updateNumberFormattedCompanionField($(eventSrcElement), $(field), eventSrcElement.getAttribute("formatPattern"));
        }

        // Transform multiple selected values to comma separated value and
        // sync it to fieldId+MSVAL field if it exists
        processMultiSelectFieldIfExists(field);
    }

    if (isChangedEventSuccess) {

        if (!getDataSrc(field) && hasObject("_isNonGridFieldChanged")) {
            var isNonGridFieldChangedObject = getObject("_isNonGridFieldChanged");
            if (isNonGridFieldChangedObject.value != "true") {
                isNonGridFieldChangedObject.value = "true";
            }
        }

        //  Execute field dependency logic, if one exists
        //  This should be executed before common/handleOnChange
        //      because of either of those methods change the visibility or enabled attributes of page elements,
        //      this function may overwrite the custom changes.
        var functionExists = eval("window.processFieldDeps");
        if (functionExists) {
            // window.event.returnValue = processFieldDeps(field.name);
            var returnVal = processFieldDeps(field.name);

            if (typeof returnVal != "undefined") {
                event.returnValue = returnVal;
            }

            // Handle the common handleOnLoad function not setting the returnValue
            isChangedEventSuccess = nvl(event.returnValue, true);
        }

        fireOBROnChange(field,true);

        // Next, execute the page entitlements
        if (isChangedEventSuccess) {
            functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                //window.event.returnValue = pageEntitlements(false);
                var returnVal = pageEntitlements(false);

                if (typeof returnVal != "undefined") {
                    event.returnValue = returnVal;
                }

                // Handle the common handleOnLoad function not setting the returnValue
                isChangedEventSuccess = nvl(event.returnValue, true);
            }
        }

        //***  Execute system level logic, if one exists ***
        if (isChangedEventSuccess) {
            functionExists = eval("window.commonOnChange");
            if (functionExists) {
                //window.event.returnValue = commonOnChange(getObject(field.name));
                var returnVal = commonOnChange(getObject(field.name), event);

                if (typeof returnVal != "undefined") {
                    event.returnValue = returnVal;
                }

                // Handle the common handleOnChange function not setting the returnValue
                isChangedEventSuccess = nvl(event.returnValue, true);
            }
        }

        if (isChangedEventSuccess) {
            functionExists = eval("window.handleOnChange");
            if (functionExists) {
                //window.event.returnValue = handleOnChange(getObject(field.name));
                var returnVal = handleOnChange(getObject(field.name), event);

                if (typeof returnVal != "undefined") {
                    event.returnValue = returnVal;
                }

                // Handle the custom handleOnChange function not setting the returnValue
                isChangedEventSuccess = nvl(event.returnValue, true);
            }
        }

        // 1. set selected value into the textbox field
        // 2. Create new option if user enters new value in textbox field
        processEnterableSelectFieldIfExists(field);

        syncToReadonlyFields(field);

        //  process auto-finder if any
        if (eval('window.autoFind_' + field.name)) {
            currentAutoFinderFieldName = field.name;
            if (hasObject(field.name) && !isEmpty(getObjectValue(field.name))) {
                window.baseOnFind(field.name);
            }
        }

        //*** Finally fire AJAX, if all events are completed successfully ***
        if (isChangedEventSuccess) {
            //                var ajaxInfoField = eval("ajaxInfoFor" + field.name);
            var ajaxInfoField = null;
            try {
                ajaxInfoField = eval("ajaxInfoFor" + field.name);
            }
            catch(ex) {
                ajaxInfoField = null;
            }
            if (ajaxInfoField != null) {

                var dataGrid = null;
                var dataSrcString = field.getAttribute("dataSrc");
                if (!isEmpty(dataSrcString)) {
                    dataSrcString = dataSrcString.substring(1);
                    dataGrid = eval(dataSrcString);
                }

                fireAjax(ajaxInfoField, dataGrid, field);

                // Handle the custom handleOnChange function not setting the returnValue
                isChangedEventSuccess = nvl(event.returnValue, true);
            }
            retryOBRSetValuesAfterFireAjax();
        }
        else {
            //either the commonOnChange() or the handleOnChange() return false,
            //we will revert the field dependency changes.
            functionExists = eval("window.processFieldDeps");
            if (functionExists) {
                processFieldDeps(field.name);
            }

            fireOBROnChange(field,false);

            // Next, execute the page entitlements
            functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                pageEntitlements(false);
            }
        }
    }

    if (isSelectField)  selectedIndexesForDropdownField = getSelectedIndexesForDropdownField(field);

    if (window["useJqxGrid"]) {
        dti.oasis.grid.syncDetailFieldToGrid(field.name);
    }

    event.returnValue = isChangedEventSuccess;
}

// if the selected option for a given field is expired */
function isSelectedOptionExpired(field) {
    var expired = false;
    var indicator = ((isEmpty(expiredOptionDisplayableSuffix))?expiredOptionSuffixIndicator:expiredOptionDisplayableSuffix);
    if (!isEmpty(dropdownFieldsWithExpiredOptions) && dropdownFieldsWithExpiredOptions.indexOf(field.name) != -1) {
        if (field.options) {
            for (var i = 0; i < field.options.length && !expired; i++) {
                if (field.options[i].selected) {
                    if (field.options[i].text.endsWith(indicator)) {
                        expired = true;
                    }
                }
            }
        }
    }
    return expired;
}

function basePageOnScroll(evt) {
    var event = fixEvent(evt);
    if ($("#savingDialog").dialog("isOpen")) {
        $("#savingDialog").dialog("option", "position", 'center');
    }
    if ($("#processingDialog").dialog("isOpen")) {
        $("#processingDialog").dialog("option", "position", ['right','top']);
    }

    //***  Execute system level logic, if one exists ***
    var functionExists = eval("window.commonPageOnScroll");
    if (functionExists) {
        event.returnValue = commonPageOnScroll(event);
    }

    functionExists = eval("window.handleOnPageScroll");
    if (functionExists) {
        event.returnValue = handlePageOnScroll(event);

    }
}

function basePageOnResize(evt) {
    var event = fixEvent(evt);
    if ($("#savingDialog").dialog("isOpen")) {
        $("#savingDialog").dialog("option", "position", 'center');
    }
    if ($("#processingDialog").dialog("isOpen")) {
        $("#processingDialog").dialog("option", "position", ['right','top']);
    }

    dti.gridutils.refreshAllGridWidth();

    //***  Execute system level logic, if one exists ***
    var functionExists = eval("window.commonPageOnResize");
    if (functionExists) {
        event.returnValue = commonPageOnResize(event);
    }

    functionExists = eval("window.handlePageOnResize");
    if (functionExists) {
        event.returnValue = handlePageOnResize(event);

    }
}

/*
 If there is a corresponding field with the name 'field.name+LOVLABELSPAN',
 Sync the text from field's selected option to the field's LOVLABELSPAN field,
 and sync the data to the corresponding data source if one exists for the LOVLABELSPAN field.
 */
function syncToLovLabelIfExists(field) {
    try {
        var fieldName = getFieldName(field);
        if (hasObject(fieldName + "LOVLABELSPAN")) {
            // If there is a span named fieldIdLOVLABELSPAN, and the field has a dataSrc and dataFld,
            // copy the value to the bound dataSrc.dataFld
            var fieldLOVLABELSPAN = getObject(fieldName + "LOVLABELSPAN");
            var newLabel = "";
            if (hasObject(fieldName + "MSVAL")) {
                newLabel = getAllSelectedOptionsDescriptionAsString(field);
                if (hasObject(fieldName + "MultiSelectText")) {
                    getObject(fieldName + "MultiSelectText").value = newLabel;
                }
            }
            else {
                newLabel = field.options[field.selectedIndex].text;
                newLabel = newLabel == getSelectOptionLabel() ? "" : newLabel;
            }

            fieldLOVLABELSPAN.innerText = newLabel;
            var dataSrc = getDataSrc(fieldLOVLABELSPAN);
            var dataFld = getDataField(fieldLOVLABELSPAN);
            dataSrc = dataSrc.substring(1);
            var dataGrid = eval(dataSrc);
            dataGrid.recordset(dataFld).value = newLabel;
        }
    }
    catch(ex) {
        // There is no corresponding LOVLABELSPAN field, so ignore this field.
        //                                    alert("There is no fieldLOVLABELSPAN for field " + fieldId);
    }
}

/*
 If there is a corresponding field with the name 'field.name+ROSPAN',
 Sync the text from field's hidden value to the field's ROSPAN field,
 and sync the data to the corresponding data source if one exists for the ROSPAN field.
 If exists field with the name 'field.name+ROSPAN1', use this field instead.
 */
function syncSpanToField(field) {
    try {
        var fieldName = getFieldName(field);
        if (hasObject(fieldName + "LOVLABELSPAN")) {
            syncToLovLabelIfExists(field);
            if (hasObject(fieldName + "ROSPAN")) {
                // If there is a span named fieldROSPAN, sync it to the LOVLABELSPAN
                var fieldROSPAN = getObject(fieldName + "ROSPAN");
                var fieldLOVLABELSPAN = getObject(fieldName + "LOVLABELSPAN");
                fieldROSPAN.innerText = fieldLOVLABELSPAN.innerText;
            }
        }
        else if (hasObject(fieldName + "ROSPAN")) {
            // If there is a span named fieldROSPAN or fieldROSPAN1, and the field has a dataSrc and dataFld,
            // copy the value to the bound dataSrc.dataFld;

            var fieldROSPAN;
            if (hasObject(fieldName + "ROSPAN1")) {
                fieldROSPAN = getObject(fieldName + "ROSPAN1");
            } else {
                fieldROSPAN = getObject(fieldName + "ROSPAN");
            }

            var newValue = getObjectValue(fieldName);
            fieldROSPAN.innerText = newValue;
            var dataSrc = getDataSrc(fieldROSPAN);
            var dataFld = getDataField(fieldROSPAN);
            dataSrc = dataSrc.substring(1);
            var dataGrid = eval(dataSrc);
            dataGrid.recordset(dataFld).value = newValue;
        }
    }
    catch(ex) {
        // There is no corresponding ROSPAN field, so ignore this field.
        // alert("There is no fieldROSPAN for field " + fieldId);
    }
}

function syncToReadonlyFields(editableField) {
    syncToLovLabelIfExists(editableField);

    try {
        var fieldName = getFieldName(editableField);
        if (hasObject(fieldName + "ROSPAN")) {
            var readonlyField = getObject(fieldName + "ROSPAN");
            // For hyperlinked span, ROSPAN is the <A> tag, while ROSPAN1 is the actual span that holds the field value.
            if (readonlyField.tagName.toUpperCase()!="SPAN") {
                if (hasObject(fieldName + "ROSPAN1")) {
                    readonlyField = getObject(fieldName + "ROSPAN1");
                }
            }
            if (readonlyField) {
                if (!hasObject(fieldName + "LOVLABELSPAN")) {
                    // For hyperlinked select type field, there is no LOVLABELSPAN exists.
                    // So, get the selected item's text as the readonly field value.
                    var fieldValue = "";
                    if (editableField.type.toUpperCase()=="SELECT-ONE") {
                        fieldValue = editableField.options[editableField.selectedIndex].innerText;
                    } else if (editableField.type.toUpperCase()=="RADIO") {
                        fieldValue = editableField.parentElement.innerText;
                    } else if (editableField.type.toUpperCase()=="CHECKBOX") {
                        var field = getObject(fieldName);
                        if (isUndefined(field.length)) {
                            var displayText = (field.innerText ? field.innerText : field.value);
                            if (displayText) {
                                fieldValue = displayText;
                            }
                            if (fieldValue == "" || fieldValue == "Y" || fieldValue == "N") {
                                if (field.checked) {
                                    fieldValue = (field.value=="Y" ? "Yes" : (field.value=="N" ? "No" : displayText));
                                } else {
                                    fieldValue = (field.value=="Y" ? "No" : (field.value=="N" ? "Yes" : displayText));
                                }
                            }
                        } else {
                            for (var i = 0; i<field.length; i++) {
                                if (field[i].checked)  {
                                    // each check box is always rendered within a <TD> followed by check box input element and a
                                    // <SPAN> for display text. So, get to <TD> and then get the innerText to get the display Text.
                                    var displayText = field[i].parentElement.innerText;
                                    if (!displayText) {
                                        displayText = (field[i].innerText ? field[i].innerText : field.value);
                                    }
                                    fieldValue += displayText + ",";
                                }
                            }
                            fieldValue = (fieldValue ? fieldValue.substring(0, fieldValue.length-1) : fieldValue);
                        }
                    } else {
                        fieldValue = getObjectValue(editableField);
                    }
                    readonlyField.innerText = fieldValue;
                }
            }
        }
    }
    catch(ex) {
        // There is no corresponding ROSPAN field, so ignore this field.
        // alert("There is no fieldROSPAN for field " + fieldId);
    }
}
//----------------------------------------------------------------------------------
// Transform multiple selected values to comma separated value and
// sync it to fieldId+MSVAL field if it exists
//----------------------------------------------------------------------------------
function processMultiSelectFieldIfExists(field) {
    try {
        if (hasObject(field.name + "MSVAL")) {
            var objMSVAL = getObject(field.name + "MSVAL");
            objMSVAL.value = getAllSelectedOptionsAsString(field);
        }
    }
    catch(ex) {
        // There is no corresponding MSVAL field, so ignore this field.
    }
}

function nvl(value, defaultValue) {
    if (null == value || "undefined" == typeof(value)) {
        return defaultValue;
    }
    else {
        return value;
    }
}

function processStateChange(ajax)
{
    //    try {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {

            var result = ajax.responseXML.getElementsByTagName('result');
            if (result == null || result.length == 0)
                return;
            else {
                var fieldId = $(result[0].childNodes[0]).text();
                var loadIntoCtrl = getObject(fieldId);

                // The below logic is to handle cases where a lookup is defined
                // in multiple layers in order to control label, required, read-only
                // properties, but having the same lovSql.
                if (typeof loadIntoCtrl.options == "undefined") {
                    var loadIntoCtrlLength = getObject(fieldId).length;
                    for (var ctrlIdx = 0; ctrlIdx < loadIntoCtrlLength; ctrlIdx++) {
                        loadIntoCtrl = getObject(fieldId)[ctrlIdx];
                        loadAJAXItemsCtrl(loadIntoCtrl, result);
                    }
                }
                else {
                    loadAJAXItemsCtrl(loadIntoCtrl, result);
                }
            }

        }
        else {
            alert("AJAX Error[" + ajax.status + "]: " + ajax.statusText);
        }
    }
    /*
     } catch(ex) {
     alert("processStateChange: An exception occurred in the script. Error name: " + ex.name + ". Error message: " + ex.message);
     }
     */
}

function loadAJAXItems(reqIdx)
{
    //    try {
    if (lookupxmldoc[reqIdx].readyState == 4) {
        var result = lookupxmldoc[reqIdx].getElementsByTagName('result');
        if (result == null || result.length == 0)
            return;
        else {
            var fieldId = result(0).childNodes(0).text;
            var loadIntoCtrl = getObject(fieldId);

            // The below logic is to handle cases where a lookup is defined
            // in multiple layers in order to control label, required, read-only
            // properties, but having the same lovSql.
            if (typeof loadIntoCtrl.options == "undefined") {
                var loadIntoCtrlLength = getObject(fieldId).length;
                for (var ctrlIdx = 0; ctrlIdx < loadIntoCtrlLength; ctrlIdx++) {
                    loadIntoCtrl = getObject(fieldId)[ctrlIdx];
                    loadAJAXItemsCtrl(loadIntoCtrl, result);
                }
            }
            else {
                loadAJAXItemsCtrl(loadIntoCtrl, result);
            }
        }
    }
    /*
     } catch(ex) {
     alert("loadAJAXItems: An exception occurred in the script. Error name: " + ex.name + ". Error message: " + ex.message);
     }
     */
}

function loadAJAXItemsCtrl(loadIntoCtrl, result) {
    if (loadIntoCtrl != null) {
        if (loadIntoCtrl.options != null) {
            var fieldId = $(result[0].childNodes[0]).text();
            var currentValue = $(result[0].childNodes[1]).text();
            var currentLabel = $(result[0].childNodes[2]).text();
            $(loadIntoCtrl).empty();
            if (result[0].childNodes.length > 0) {
                // Support multi select result
                if (currentValue == null) {
                    currentValue = "";
                }
                //reset select options with returned HTML options.
                $(loadIntoCtrl).append($(result[0].childNodes[3]).text());

                currentLabel = currentLabel == getSelectOptionLabel() ? "" : currentLabel;

                //reset multi select field Label
                if (hasObject(fieldId + "MultiSelectText")) {
                    getObject(fieldId + "MultiSelectText").value = currentLabel;
                }

                //reset multi select field value
                if (hasObject(fieldId + "MSVAL")) {
                    getObject(fieldId + "MSVAL").value = currentValue;
                }

                try {
                    //set currentLabel to LOVLABELSPAN field.
                    var fieldLOVLABELSPAN = getObject(fieldId + "LOVLABELSPAN");
                    fieldLOVLABELSPAN.innerText = currentLabel;

                    //set currentLabel to XML label field.
                    var dataSrc = getDataSrc(fieldLOVLABELSPAN);
                    var dataFld = getDataField(fieldLOVLABELSPAN);
                    dataSrc = dataSrc.substring(1);
                    var dataGrid = eval(dataSrc);
                    dataGrid.recordset(dataFld).value = currentLabel;

                    //set currentValue to XML field.
                    dataSrc = getDataSrc(loadIntoCtrl);
                    dataFld = getDataField(loadIntoCtrl);
                    dataSrc = dataSrc.substring(1);
                    dataGrid = eval(dataSrc);
                    dataGrid.recordset(dataFld).value = currentValue;
                } catch (EX) {
                    // There is no corresponding LOVLABELSPAN field, so ignore this field.
                }
            }
            distinguishExpiredOptionsForField(getObject(fieldId));
            //  Execute field dependency logic, if it exists
            var functionExists = eval("window.processFieldDeps");
            if (functionExists) {
                processFieldDeps(fieldId);
            }
        }
    }
}

function fireAjax(AjaxUrls, dataGrid, field)
{
    var beginTime = new Date();
    //    try {

        httpreq = new Array();
        lookupxmldoc = new Array();
        //alert("AjaxUrls: " + AjaxUrls);
        var url = "";
        var urls = AjaxUrls.split("URL");
//        logDebug("urls : " + urls + " length: " + urls.length);
        for (var idx = 0; idx <= urls.length - 1; idx++) {
            if (urls[idx] != '') {
                //                        alert(idx + ": " + urls[idx]);
                url = urls[idx].substring(urls[idx].indexOf("]") + 1);
                //alert(url);

                var delim = "";
                var delimLoc = url.toUpperCase().indexOf("_DELIM=");
                if (delimLoc > 0) {
                    delimLoc += "_DELIM=".length;
                    delim = url.substring(delimLoc);
                    if (delim.toUpperCase().indexOf("&") > 0) {
                        delim = delim.substring(0, delim.toUpperCase().indexOf("&"));
                    }
                }

                var currentValue = "";
                var loadIntoField = "";
                var loadIntoFieldLoc = url.toUpperCase().indexOf("FIELDID=");
                if (loadIntoFieldLoc > 0) {
                    loadIntoFieldLoc += "fieldId=".length;
                    loadIntoField = url.substring(loadIntoFieldLoc);
                    if (loadIntoField.toUpperCase().indexOf("&") > 0) {
                        loadIntoField = loadIntoField.substring(0, loadIntoField.toUpperCase().indexOf("&"));
                    }

                    // If the loadIntoField does not exist as a field on the page, skip this field
                    if (!hasObject(loadIntoField)) {
                        continue;
                    }
                    //If the field doesn't belong to the currently selected grid, skip this field
                    var loadIntoFieldObject = getObject(loadIntoField);
                if (dataGrid != null && isDefined(getDataSrc(loadIntoFieldObject)) &&
                    getDataSrc(loadIntoFieldObject) != "#" + dataGrid.id) {
                        continue;
                    }

                    if (dataGrid != null) {
                        try {
                        var dataFld = getDataField(loadIntoFieldObject).toUpperCase();
                            currentValue = encodeURIComponent(dataGrid.recordset(dataFld).value);
                        }
                        catch (ex) {
                            //If the field is not defined in xml; consider it from page fields
                        }
                    }
                    if (currentValue == "") {
                        if (isMultiSelect(loadIntoFieldObject)) {
                            currentValue = getAllSelectedOptionsAsUrlEncodingString(loadIntoFieldObject, "&currentValue=");
                        }
                        else {
                            currentValue = encodeURIComponent(getObjectValue(loadIntoField));
                        }
                    }
                    //                            alert(currentValue)
                }

                var urlInfo = url.split("&");
                url = "";

                for (var i = 0; i <= urlInfo.length - 1; i++) {
                    var param = urlInfo[i].split("=" + delim);
                    if (param.length > 1) {
                        var objName = param[1].substring(0, param[1].length - 1);
                        var value = "";
                        if (objName != "") {
                            if (objName == loadIntoField) {
                                // Use the currentValue if the name of this field is the loadIntoField
                                // to allow for taking the value from the grid.
                                // It fails if you use the value from the Select field when the current value is not in the current set of options
                                value = currentValue.replace(/currentValue/g, param[0]);
                            } else {
                                var obj = getObject(objName);
                                if (obj != null) {
                                    if (isMultiSelect(obj)) {
                                        value = getAllSelectedOptionsAsUrlEncodingString(obj, "&" + param[0] + "=");
                                    } else {
                                        value = getObjectValue(objName);
                                        if (typeof value == "undefined") {
                                            value = "";
                                        }
                                        value = encodeURIComponent(value);
                                    }
                                }
                            }
                            //                                    alert("objName: " + objName + " value: " + value);
                        }
                        url += param[0] + "=" + value + "&";
                    }
                    else {
                        url += urlInfo[i] + "&";
                    }
                }

                url = url.substring(0, url.length - 1);
                url = url + "&lastRefreshTime=" + lastRefreshTime;
                // add page view state id information, so that AJAX call will utilize the page state view cache correctly.
                if (isDefined(PAGE_VIEW_STATE_CACHE_KEY)) {
                    if (getObject(PAGE_VIEW_STATE_CACHE_KEY)) {
                        url += "&" + PAGE_VIEW_STATE_CACHE_KEY;
                        url += "=" + getObjectValue(PAGE_VIEW_STATE_CACHE_KEY);
                    }
                }
                if (priorURL[loadIntoField]) {
                    if (priorURL[loadIntoField] == url) {
                        // update the LOVLABELSPAN text and the LOVLABEL column in grid if they exist
                        if (hasObject(loadIntoField)) {
                            var fieldObject = getObject(loadIntoField);
                            if (fieldObject.tagName && fieldObject.tagName.toLowerCase() == "select") {
                                var optionDescription = getOptionDescription(fieldObject, decodeURIComponent(currentValue));
                                optionDescription = optionDescription == getSelectOptionLabel() ? "" : optionDescription;
                                // update LOVLABELSPAN
                                if (hasObject(loadIntoField + "LOVLABELSPAN")) {
                                    var fieldLOVLABELSPAN = getObject(loadIntoField + "LOVLABELSPAN");
                                    fieldLOVLABELSPAN.innerText = optionDescription;
                                }
                                // update LOVLABEL column
                            var dataSrcString = getDataSrc(fieldObject);
                                if (!isEmpty(dataSrcString)) {
                                var dataFldString = getDataField(fieldObject);
                                    dataSrcString = dataSrcString.substring(1);
                                    var dataGridObject = eval(dataSrcString);
                                    var labelDataFld = dataFldString + "LOVLABEL";
                                    if (isFieldExistsInRecordset(dataGridObject.recordset, labelDataFld)) {
                                        dataGridObject.recordset(labelDataFld).value = optionDescription;
                                    }
                                }
                            }
                        }
                        //skip ajax sync if URL is not changed
                        continue;
                    } else {
                        priorURL[loadIntoField] = url;
                    }
                } else {
                    priorURL[loadIntoField] = url;
                }
                url = url + "&currentValue=" + currentValue;
                url = url + "&isReadOnly=" + isFieldReadOnly(loadIntoField);

//                logDebug("fireAjax::fire ajax sync:" + url);
                //                        alert("final url : " + url );

            new AJAXRequest("GET", $.trim(url), "",  processStateChange, false);
            //var reqNumber = currentHttpIdx++;
            //httpreq[reqNumber] = new XMLHttpRequest();
            //httpreq[reqNumber].open("GET", url, false);
            //httpreq[reqNumber].onreadystatechange = new Function("processStateChange(" + reqNumber + ")");
            //httpreq[reqNumber].send();
        }
    }

    currentHttpIdx = 0;
    /*
     } catch (ex) {
     alert("fireAjax: An exception occurred in the script. Error name: " + ex.name + ". Error message: " + ex.message);
     }
     */
    var functionExists = eval("window.postAjaxRefresh");
    if (functionExists) {
        postAjaxRefresh(field, AjaxUrls);
    }
    var endTime = new Date();
//    logDebug("Time spent in fireAjax:" + (endTime.getTime() - beginTime.getTime()) + "ms");
    return true;
}

//----------------------------------------------------------------------------------
// The following Code provides functionality for logging debug messages in an array
//----------------------------------------------------------------------------------
var MAX_MESSAGES = 30;
var debugMessages = new Array();
function debug(message) {
    while (debugMessages.length >= MAX_MESSAGES) {
        debugMessages.shift();
    }
    debugMessages[debugMessages.length] = message;
}

function displayDebug() {
    var message = "";
    for (var i = 0; i < debugMessages.length; i++) {
        message += debugMessages[i] + "\n";
    }
    alert(message);
}

function clearDebug() {
    debugMessages.length = 0;
}

function logDebug(message) {
    if (enableJavascriptLogging && enableJavascriptLogging == "true" && !isEmpty(message)) {
        // Invoke Ajax call to log javascript debug messages
        var url = getCorePath() + "/log/maintainJavascriptLog.do" + "?process=info&date=" + new Date();
        var data = "Message=" + encodeURIComponent(message);
        new AJAXRequest("post", url, data, handleOnLogDebug);
    }
}

function handleOnLogDebug(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            // do nothing for now.
        }
    }

    // release ajax object
    ajax = null;
}

//----------------------------------------------------------------------------------
// End of debugging code
//----------------------------------------------------------------------------------
//In IE8 getObject(fieldId) returns a collection if there is more than 1 object with the same ID
//In IE11 getObject(fieldId) returns only the first object if there is more than 1 object with the same ID
//and so does $('#fieldId')
//Workaround is to use $('input[id=fieldId]')
function enableDisableFieldById(fieldId, isDisabled){
    var field = getObject(fieldId);
    if(window["useJqxGrid"] && isDefined(field.id) && field.tagName=="INPUT"){
        var $field = $("input[id='" + field.id + "']");
        if($field.length > 1) {
            field = $field;
        }
    }
    enableDisableField(field, isDisabled);
}

//----------------------------------------------------------------------------------
// Add condition field[i].type != undefined to fix issue 100784.
// In IE8, without this condition, there are some problems (maybe can't find the field in Action) when submit form.
//----------------------------------------------------------------------------------
// If the field is initially loaded as readonly, calling this function to enable the field (ie. make it as editable) will not
// work. For such cases, call setFieldEditable() directly.
function enableDisableField(field, isDisabled) {
    if (field != undefined) {
        var fieldType="";
        var fieldName="";

        var fieldObj;
        var obj;

        if (field.length > 0) {
            fieldObj = getObject(field[0]);
            if(fieldObj) {
                obj = getObject(field);
                fieldType = (obj.type != undefined ?  obj.type : fieldObj.type);
                fieldName = (obj.name != undefined ?  obj.name : fieldObj.name);
                if (!fieldName) {
                    fieldName = (obj.id != undefined ?  obj.id : fieldObj.id);
                }
            }
        } else {
            fieldObj = getObject(field);
            if (fieldObj) {
                fieldType = fieldObj.type;
                fieldName = (fieldObj.name != undefined ? fieldObj.name : fieldObj.id);
            }
        }

        //cesar__196687 -  the commented lines below will be removed. this just to compare old code with new code.
        // if (field.length > 0) {
        //     if (hasObject(field[0])) {
        //         fieldType = (getObject(field).type != undefined ?  getObject(field).type : getObject(field[0]).type);
        //         fieldName = (getObject(field).name != undefined ?  getObject(field).name : getObject(field[0]).name);
        //         if (!fieldName) {
        //             fieldName = (getObject(field).id != undefined ?  getObject(field).id : getObject(field[0]).id);
        //         }
        //     }
        // } else {
        //     if (hasObject(field)) {
        //         fieldType = getObject(field).type;
        //         fieldName = (getObject(field).name != undefined ? getObject(field).name : getObject(field).id);
        //     }
        // }

        if (fieldType == "button") {
            if (field.length > 0) {
                for (var i = 0; i < field.length; i++) {
                    enableDisableField(field[i], isDisabled);
                }
            } else {
                if (!isDisabled && isPageEntitlementInProgress && isDisabledByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
                    // Make sure it is not previously disabled by OBR.
                    // Change it back to disabled, even if application code changed it to be enabled before executing pageEntitlements.
                    // OBR always wins, if it sets it be disabled.
                    isDisabled = true;
                }
                field.disabled = isDisabled;
            }
        } else if (fieldName) {
            // A Field can be made editable, only if it was previously editable.
            // If the field is defined as read-only in WebWB; those fields should never be made editable via JS processing.
            if (isFieldCanBeMadeEditable(fieldName)) {
                setFieldEditableOrReadonly(field, isDisabled);
            } else if ((getIsEditableOnInitialPageLoadContainer(fieldName).length == 0)) {
                // This field is not a form field rendered via WebWB configuration.
                // This could be a field rendered directly in the grid as in-grid fields.
                // So, the field must be either enabled/disabled directly.
                setFieldToEnableOrDisableState(field, isDisabled);
            }
        }
    }
}

//In IE8 getObject(fieldId) returns a collection if there is more than 1 object with the same ID
//In IE11 getObject(fieldId) returns only the first object if there is more than 1 object with the same ID
//and so does $('#fieldId')
//Workaround is to use $('input[id=fieldId]')
function hideShowFieldById(fieldId, isHidden){
    var field = getObject(fieldId);
    if(window["useJqxGrid"] && isDefined(field.id) && field.tagName=="INPUT"){
        var $field = $("input[id='" + field.id + "']");
        if($field.length > 1) {
            field = $field;
        }
    }
    hideShowField(field, isHidden);
}

function hideShowField(field, isHidden) {

    if (!isHidden && isPageEntitlementInProgress && isHiddenByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
        // Make sure it is not previously hidden by OBR.
        // Change it back to hidden, even if application code changed it to visible before executing pageEntitlements.
        // OBR always wins, if it sets it be hidden.
        isHidden = true;
    }


    //    var roFldSuffixArray = new Array("FLDLABEL");
    if (field.length > 0 && isUndefined(field.id)) {   //field is an array
        for (var i = 0; i < field.length; i++) {
            hideShowField(field[i], isHidden);
        }
    }
    else {
        var fieldName = field.name;
        if (fieldName && fieldName.indexOf("MultiSelectText") > 0) {
            fieldName = fieldName.substr(0, fieldName.indexOf("MultiSelectText"));
        }

        if (!isUndefined(field.type) &&
            field.type.toUpperCase() == "BUTTON" &&
            field.parentElement.tagName.toUpperCase() == "SPAN" &&
            field.parentElement.className.indexOf("ButtonHolder") > 0) {   //Action Item
            hideShowActionItem(field, isHidden);
        }
        else if (field.tagName.toUpperCase() == "A" &&
            field.parentElement.tagName.toUpperCase() == "LI" &&
            (field.parentElement.className.indexOf("tab") != -1 ||
            field.parentElement.className.indexOf("selectedTab") != -1 ||
            field.parentElement.className.indexOf("firstTab") != -1 ||
            field.parentElement.className.indexOf("firstSelectedTab") != -1))  {   //Tab Item
            hideShowTabItem(field, isHidden);
        }
        else if (field.tagName.toUpperCase() == "DIV" &&
            field.id == "panel" &&
            field.className == "panel") {   //total panel
            if(window["useJqxGrid"])
                hideShowElementByClassName(field, isHidden);
            else
                field.style.display = (isHidden ? "none" : "block");

        } else if (field.tagName.toUpperCase() == "TR") {   //for layer fields
            if(window["useJqxGrid"])
                hideShowElementByClassName(field, isHidden);
            else
                field.style.display = (isHidden ? "none" : "block");

        } else {
            // Hide the Field's parent TD tag if it exists. This will cause the cell to collapse.
            // If all cells on this row are hidden, the row will collapse as well
            // Otherwise, hide the field.
            if (field.parentElement.tagName.toUpperCase() == "TD") {
                if (isHidden) {
                    hideShowElementByClassName(field.parentElement, true);
                } else {
                    hideShowElementAsInlineByClassName(field.parentElement, false);
                }
            }
            else {
                if (isHidden) {
                    hideShowElementByClassName(field, true);
                } else {
                    hideShowElementAsInlineByClassName(field, false);
                }
            }
            // Handle the note field.
            var targetObj = getSingleObject("ANT_" + fieldName);
            if (fieldName && targetObj) {
                targetObj.style.display = isHidden ? "none" : "inline";
            }
            // Handle the finder icon.
            targetObj = getSingleObject("AFD_" + fieldName);
            if (fieldName && targetObj) {
                targetObj.style.display = isHidden ? "none" : "inline";
            }
            // Hide the Field Label as well
            targetObj =  getObject(fieldName + "FLDLABEL");
            var targetObjIdLabel = getObject(field.id + "FLDLABEL");
            if (!isUndefined(fieldName) && targetObj) {
                hideShowField(targetObj, isHidden);
            }
            else if (!isUndefined(field.id) && targetObjIdLabel) {
                hideShowField(targetObjIdLabel, isHidden);
            }
        }

        var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
        for (var i = 0; i < suffixArray.length; i++) {
            var containerId = fieldName + suffixArray[i];
            if (hasObject(containerId)) {
                var container = $("#" + containerId);
                if (isHidden) {
                    if (container.attr('class') != (i==0 ? LABEL_CLASS_HIDDEN : FIELD_CLASS_HIDDEN) ) {
                        container.attr('class', (i==0 ? LABEL_CLASS_HIDDEN : FIELD_CLASS_HIDDEN) );
                    }
                    if (isDefined(container.attr("isEditable"))) {
                        container.attr('isEditableWhenVisible', container.attr("isEditable")) ;
                        container.removeAttr('isEditable');
                    }
                } else {
                    var isEditable = false;
                    if (isDefined(container.attr("isEditableWhenVisible"))) {
                        isEditable = (container.attr("isEditableWhenVisible")=="Y") ;
                        container.removeAttr('isEditableWhenVisible');
                    } else {
                        //This field was not previously hidden by this function - so, it must have isEditable attribute set.
                        if (isDefined(container.attr("isEditable"))) {
                            isEditable = (container.attr("isEditable")=="Y") ;
                        }
                    }
                    if (isEditable) {
                        setFieldEditable(fieldName);
                    } else {
                        setFieldReadonly(fieldName);
                    }
                }
                //Perform autohide TR logic after performing hide/show logic for all TDs
                if (i==suffixArray.length-1) {
                    for (var j = 0; j < container.length; j++) {
                        if (container[j].tagName.toUpperCase()=="TD") {
                            autoHideTR(container[j].parentElement);
                        }
                    }
                }
            }
        }
        switchInputNameForCheckBox(fieldName);
    }
}

function isHeaderLabelField(field) {
    if (field.hasClass(HEADER_FIRST_COL_LABEL_CLASS_EDITABLE) ||
        field.hasClass(HEADER_SECOND_COL_LABEL_CLASS_EDITABLE) ||
        field.hasClass(HEADER_THIRD_COL_LABEL_CLASS_EDITABLE) ||
        field.hasClass(HEADER_FIRST_COL_LABEL_CLASS_READONLY) ||
        field.hasClass(HEADER_SECOND_COL_LABEL_CLASS_READONLY) ||
        field.hasClass(HEADER_THIRD_COL_LABEL_CLASS_READONLY)) {
        return true;
    } else {
        return false;
    }
}

function isHeaderValueField(field) {
    if (field.hasClass(HEADER_FIRST_COL_FIELD_CLASS_EDITABLE) ||
        field.hasClass(HEADER_SECOND_COL_FIELD_CLASS_EDITABLE) ||
        field.hasClass(HEADER_THIRD_COL_FIELD_CLASS_EDITABLE) ||
        field.hasClass(HEADER_FIRST_COL_FIELD_CLASS_READONLY) ||
        field.hasClass(HEADER_SECOND_COL_FIELD_CLASS_READONLY) ||
        field.hasClass(HEADER_THIRD_COL_FIELD_CLASS_READONLY)) {
        return true;
    } else {
        return false;
    }
}

function isHeaderField(field) {
    return isHeaderLabelField(field) || isHeaderValueField(field)
}

//----------------------------------------------------------------------------------
// Add condition field[i].type != undefined to fix issue 100784.
// In IE8, without this condition, there are some problems (maybe can't find the field in Action) when submit form.
//----------------------------------------------------------------------------------
function setFieldEditableOrReadonly(field, isReadOnly) {
    var fieldId = (field.name=="" ? field.id : field.name);
    if (!isReadOnly && isPageEntitlementInProgress && isDisabledByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
        // Make sure it is not previously disabled by OBR.
        // Change it back to disabled, even if application code changed it to be enabled before executing pageEntitlements.
        // OBR always wins, if it sets it be disabled.
        isReadOnly = true;
    }

    if (field.length > 0) {
        for (var i = 0; i < field.length; i++) {
            if (field[i].type != undefined && field[i].type != "hidden") {
                fieldId = (field[i].name=="" ? field[i].id : field[i].name);
                if (isReadOnly) {
                    setFieldReadonly(fieldId);
                } else {
                    setFieldEditable(fieldId);
                }
            }
        }
    }
    if (field.type != "hidden" || (hasObject(field.name + "_READONLY_ID") && hasObject(field.name + "_FIELD_ID"))) {
        if (isReadOnly) {
            setFieldReadonly(fieldId);
        } else {
            setFieldEditable(fieldId);
        }
    } else if (hasObject(field.name + DISPLAY_FIELD_EXTENTION)) {
        setFieldEditableOrReadonly(getObject(field.name + DISPLAY_FIELD_EXTENTION), isReadOnly);
    }
}

/**
 * set field to readonly
 * @param fieldId
 */
/*
 Do not use isFieldCanBeMadeEditable() in this function, because OBR calls this function directly.
 If the field is loaded as readonly based on WebWB configuration, OBR should have the ability to change
 the field as editable.
 */
function setFieldReadonly(fieldId) {
    if (fieldId.length > 10 && fieldId.substring (fieldId.length - 10) == "_DISP_ONLY") {
        // If the field is a formatted field, then remove the _DISP_ONLY suffix.
        fieldId = fieldId.substring (0, fieldId.length - 10);
    }
    var newClass = "";
    var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
    for (var i = 0; i < suffixArray.length; i++) {
        var containerId = fieldId + suffixArray[i];
        if (hasObject(containerId)) {
            var container = $("#" + containerId);
            // If the field is hidden, dont honor the request.
            if (isDefined(container.attr("isEditableWhenVisible"))) {
                return;
            }
            var isHeaderContainer = (i==0 ? isHeaderLabelField(container) : isHeaderValueField(container)) ;
            if (isHeaderContainer) {
                var headerCol = container.attr('class');
                if (headerCol) {
                    headerCol = headerCol.substring(CLASS_EDITABLE_PREFIX.length);
                    headerCol = headerCol.substring(0, headerCol.length - CLASS_FIELD_SUFFIX.length);
                }
                newClass = CLASS_READONLY_PREFIX + headerCol + (i==0 ? CLASS_LABEL_SUFFIX : CLASS_FIELD_SUFFIX);
            } else {
                newClass = (i==0 ? LABEL_CLASS_READONLY : FIELD_CLASS_READONLY);
            }
            container.attr('class', newClass);
            container.attr('isEditable', 'N');
        }
    }
    // for note text and textarea popup
    if ((JS_VAR_READONLY_FIELD_PREFIX + fieldId) in window) {
        eval(JS_VAR_READONLY_FIELD_PREFIX + fieldId + "=true");
    }
    // for checkbox
    switchInputNameForCheckBox(fieldId);
}
/**
 * set field to editable
 * @param fieldId
 */
function setFieldEditable(fieldId) {
    var isDisplayOnlyField = false;
    var originalFieldId = fieldId;
    if (fieldId.length > 10 && fieldId.substring (fieldId.length - 10) == "_DISP_ONLY") {
        // If the field is a formatted field, then remove the _DISP_ONLY suffix.
        fieldId = fieldId.substring (0, fieldId.length - 10);
        isDisplayOnlyField = true;
    }
    var field = getObject(fieldId);
    if (field != undefined) {
        var newClass = "";
        var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
        for (var i = 0; i < suffixArray.length; i++) {
            var containerId = fieldId + suffixArray[i];
            if (hasObject(containerId)) {
                var container = $("#" + containerId);
                // If the field is hidden, dont honor the request.
                if (isDefined(container.attr("isEditableWhenVisible"))) {
                    return;
                }
                var isHeaderContainer = (i==0 ? isHeaderLabelField(container) : isHeaderValueField(container)) ;
                if (isHeaderContainer) {
                    var headerCol = container.attr('class');
                    if (headerCol) {
                        headerCol = headerCol.substring(CLASS_EDITABLE_PREFIX.length);
                        headerCol = headerCol.substring(0, headerCol.length - CLASS_FIELD_SUFFIX.length);
                    }
                    newClass = CLASS_EDITABLE_PREFIX + headerCol + (i==0 ? CLASS_LABEL_SUFFIX : CLASS_FIELD_SUFFIX);
                } else {
                    newClass = (i==0 ? LABEL_CLASS_EDITABLE : FIELD_CLASS_EDITABLE);
                }
                container.attr('class', newClass);
                container.attr('isEditable', 'Y');
            }
        }
        // for note text and textarea popup
        if ((JS_VAR_READONLY_FIELD_PREFIX + fieldId) in window) {
            eval(JS_VAR_READONLY_FIELD_PREFIX + fieldId + "=false");
        }
        switchInputNameForCheckBox(fieldId);

        if(isDisplayOnlyField){
            var displayField = getObject(originalFieldId);
            if (displayField != undefined) {
                if (displayField.disabled) {
                    displayField.disabled = false;
                }
            }
        } else {
            if (field.disabled) {
                field.disabled = false;
            }
        }
    }
}

/*
 All html input fields rendered by eOASIS will have following attributes attached automatically by the framework.

 1. isEditableOnInitialPageLoad - This attribute is initialized with a value that indicates whether the field is editable
 or readonly based on WebWB configuration. This value should never be changed by any of the javascript logic.

 [Y = editable; N = non-editable (readonly)]

 2. isEditableWhenVisible - Fields that are defined hidden in WebWB will have this attribute added with a value
 that indicates whether the field should be editable or read-only (based on WebWB configuration), whenever the
 becomes editable. This value should never be changed by any of the sub-system javascript logic.  The framework uses
 this attribute and changes the value as per the flow of events.

 For OBR, a hidden field must always be made visible first, followed by setting it to either readonly or editable.

 [Y = editable, when the field becomes visible; N = non-editable (readonly), when the field becomes visible]

 3. isEditable - This attribute is initialized with a value that indicates whether the rendered field is edtiable or not.
 During the initial page load by the tag, this value and "" will match. However the value may probably gets changed
 by any of the javascript logic - eg. due to OBR, pageElement or page level logic.

 [Y = editable; N = non-editable (readonly)]
 */
function isFieldCanBeMadeEditable(fieldName) {
    var isEditable = false;
    if (fieldName != undefined) {
        var isEditableOnInitialPageLoad = "";
        var container = getIsEditableOnInitialPageLoadContainer(fieldName);
        if (container != undefined) {
            if (container.length > 0) {
                if (isDefined(container.attr("isEditableOnInitialPageLoad"))) {
                    isEditableOnInitialPageLoad = container.attr("isEditableOnInitialPageLoad");
                }
            }
        }
        isEditable =  (isEditableOnInitialPageLoad == "Y");
    }
    return isEditable;
}

function getIsEditableOnInitialPageLoadContainer(fieldName) {
    var container = undefined;
    if (fieldName != undefined) {
        /*
         Radio Option and Checkbox HTML elements are rendered within a table of its own.
         So, locate the correct <TD> that holds the HTML elements to get "isEditableOnInitialPageLoad" attribute information.
         */
        container = $("#" + (fieldName + FIELD_VALUE_CONTAINER_SUFFIX));
    }
    return container;
}

function switchInputNameForCheckBox(fieldId) {
    if (hasObject(fieldId + "_READONLY_ID")){
        //only for checkbox
        var checkBoxInput = getObject(fieldId + "_FIELD_ID");
        var hiddenInput = getObject(fieldId + "_READONLY_ID");

        var containerId = fieldId + FIELD_VALUE_CONTAINER_SUFFIX;
        if (hasObject(containerId)) {
            var container = $("#" + containerId);
            var checkBoxInputCopy = null;
            var hiddenInputCopy = null;
            var isEditable = false;
            if (isDefined(container.attr("isEditable"))) {
                isEditable = (container.attr("isEditable")=="Y") ;
            }
            if (isEditable && container.attr('class')==FIELD_CLASS_EDITABLE) {
                // visible and editable
                if (checkBoxInput.name != fieldId) {
                    changeInputName(checkBoxInput, fieldId);
                }
                if (hiddenInput.name == fieldId) {
                    changeInputName(hiddenInput, fieldId + "_DUMMY_ID");
                }
            } else {
                // hidden or readonly
                if (checkBoxInput.name == fieldId) {
                    changeInputName(checkBoxInput, fieldId + "_DUMMY_ID");
                }
                if (hiddenInput.name != fieldId) {
                    changeInputName(hiddenInput, fieldId);
                }
            }
        }
    }
}

//----------------------------------------------------------------------------------
// Add condition field[i].type != undefined to fix issue 100784.
// In IE8, without this condition, there are some problems (maybe can't find the field in Action) when submit form.
//----------------------------------------------------------------------------------
function setFieldToEnableOrDisableState(field, isDisabled) {

    if (!isDisabled && isPageEntitlementInProgress && isDisabledByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
        // Make sure it is not previously disabled by OBR.
        // Change it back to disabled, even if application code changed it to be enabled before executing pageEntitlements.
        // OBR always wins, if it sets it be disabled.
        isDisabled = true;
    }

    if (field.length > 0) {
        for (var i = 0; i < field.length; i++) {
            if (field[i].type != undefined && field[i].type != "hidden") {
                field[i].disabled = isDisabled;
            }
        }
    }
    if (field.type != "hidden") {
        field.disabled = isDisabled;
        //handle the field with find text type
        var fieldId = ((isArray(field) && field.length > 0) ? (field.type ? field.name : field[0].name) : field.name);
        if (fieldId) {
            var multiSelectTextPos = fieldId.indexOf("MultiSelectText");
            if (multiSelectTextPos > 0) {
                // The Image <A> element for MultiSelectText field doesn't have "MultiSelectText" suffix.
                fieldId = fieldId.substring(0, multiSelectTextPos);
            }

            var objAFD = getSingleObject("AFD_" + fieldId);
            if (eval(objAFD)) {
                if (isDisabled) {
                    objAFD.children[0].style.filter = "gray";
                    if (objAFD.hasAttribute("finderFunctionName")) {
                        objAFD.removeAttribute("finderFunctionName");
                    }
                } else {
                    objAFD.children[0].style.filter = "";
                    if (!objAFD.hasAttribute("finderFunctionName")) {
                        objAFD.setAttribute("finderFunctionName", "popupMultiSelectDiv");
                    }
                }
            }
        }
    } else {
        if (hasObject(field.name + DISPLAY_FIELD_EXTENTION)) {
            enableDisableField(getObject(field.name + DISPLAY_FIELD_EXTENTION), isDisabled);
        }
    }
}

function addObjectClassName(element, className) {
    var jObject = $(element);
    if (!jObject.hasClass(className)) {
        jObject.addClass(className);
    }
}

function removeObjectClassName(element, className) {
    var jObject = $(element);
    if (jObject.hasClass(className)) {
        jObject.removeClass(className);
    }
}

function hasClassName(element, className) {
    var jObject = $(element);
    var hasClass = jObject.hasClass(className);
    return hasClass;
}

function isFieldHidden(fieldId) {
    return isFieldHasClassName(fieldId, FIELD_CLASS_HIDDEN);
}

function isFieldEditable(fieldId) {
    return isFieldHasClassName(fieldId, FIELD_CLASS_EDITABLE);
}

function isFieldReadOnly(fieldId) {
    return isFieldHasClassName(fieldId, FIELD_CLASS_READONLY);
}

function isFieldHasClassName(fieldId, className) {
    var hasClassNmae = false;
    var containerId = fieldId + FIELD_VALUE_CONTAINER_SUFFIX;
    if (hasObject(containerId)) {
        var container = $("#" + containerId);
        if (container.hasClass(className)) {
            hasClassNmae = true;
        }
    }
    return hasClassNmae;
}

function changeInputName(inputElement, newName) {
    var parentElement = inputElement.parentElement;
    if (parentElement) {
        var newInputElement = document.createElement("input");
        newInputElement.name = newName;
        newInputElement.id = inputElement.id;
        newInputElement.type = inputElement.type;

        $(inputElement).replaceWith(newInputElement);

        newInputElement.className = inputElement.className;
        newInputElement.value = inputElement.value;
        if (inputElement.dataSrc) {
            newInputElement.dataSrc = inputElement.dataSrc;
        }
        if (inputElement.dataFld) {
            newInputElement.dataFld = inputElement.dataFld;
        }
        if (inputElement.type == "checkbox" && inputElement.checked) {
            newInputElement.checked = true;
        }
        if (inputElement.onclick != null) {
            newInputElement.onclick = inputElement.onclick;
        }
    }
}


function hideShowActionItem(field, isHidden) {

    //There is no field found when action Item set to Inactive in eAdmin.

    if(isUndefined(field) || isNull(field)){
        return;
    }

    /*
     Action Items in actionItemGroup are rendered as:
     <dt><span><span><span><input /></span></span></span></dt> format.
     */
    var currentField = field;
    if (currentField.parentElement.tagName.toUpperCase() == "SPAN") {
        currentField = currentField.parentElement;
        if (currentField.parentElement.tagName.toUpperCase() == "SPAN") {
            currentField = currentField.parentElement;
            if (currentField.parentElement.tagName.toUpperCase() == "SPAN") {
                currentField = currentField.parentElement;
                if (currentField.parentElement.tagName.toUpperCase() == "DT") {
                    currentField = currentField.parentElement;
                }
            }
        }
    }
    if (isHidden) {
        if(window["useJqxGrid"])
            hideShowElementByClassName(currentField, true);
        else
            currentField.style.display = "none";
    } else {
        // Make sure it is not previously hidden by OBR.
        // Change it back to hidden, even if application code changed it to visible before executing pageEntitlements.
        // OBR always wins, if it sets it be hidden.
        if (isPageEntitlementInProgress && isHiddenByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
            if(window["useJqxGrid"])
                hideShowElementByClassName(currentField, true);
            else
                currentField.style.display = "none";
        } else {
            if(window["useJqxGrid"])
                hideShowElementAsInlineByClassName(currentField, false);
            else
                currentField.style.display = "inline";
        }
    }
    return;
}

function hideShowTabItem(field, isHidden) {
    /*
     Action Items in actionItemGroup are rendered as:
     <li><a></a></li> format.
     */
    var currentField = field;
    if (currentField.parentElement.tagName.toUpperCase() == "LI" && field.tagName.toUpperCase() == "A") {
        if(window["useJqxGrid"]) {
            if (isHidden) {
                hideShowElementByClassName(currentField, true);
                hideShowElementByClassName(currentField.parentElement, true);
            } else {
                // Make sure it is not previously hidden by OBR.
                // Change it back to hidden, even if application code changed it to visible before executing pageEntitlements.
                // OBR always wins, if it sets it be hidden.
                if (isPageEntitlementInProgress && isHiddenByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
                    hideShowElementByClassName(currentField, true);
                    hideShowElementByClassName(currentField.parentElement, true);
                }
                else {
                    hideShowElementByClassName(currentField, false);
                    hideShowElementByClassName(currentField.parentElement, false, "");
                }
            }
        }
        else {
            if (isHidden) {
                hideShowElementByClassName(currentField, true);
                hideShowElementByClassName(currentField.parentElement, true);
            } else {
                // Make sure it is not previously hidden by OBR.
                // Change it back to hidden, even if application code changed it to visible before executing pageEntitlements.
                // OBR always wins, if it sets it be hidden.
                if (isPageEntitlementInProgress && isHiddenByOBR((isArray(field) && field.length > 0) ? (field.type ? field : field[0]) : field)) {
                    currentField.style.display = "none";
                    currentField.parentElement.style.display = "none";
                } else {
                    currentField.style.display = "block";
                    currentField.parentElement.style.display = "";
                }
            }
        }
    }
    return;
}

function hideGridDetailDiv(gridId) {
    if (getTableProperty(eval(gridId), "gridDetailDivId")) {
        var detailDiv = getObject(getTableProperty(eval(gridId), "gridDetailDivId"));
        if (detailDiv != null) {
            if(window["useJqxGrid"])
                hideShowElementByClassName(detailDiv, true);
            else
                detailDiv.style.display = "none";
        }
    }
}

function showGridDetailDiv(gridId) {
    if (getTableProperty(eval(gridId), "gridDetailDivId")) {
        var detailDiv = getObject(getTableProperty(eval(gridId), "gridDetailDivId"));
        if (detailDiv != null) {
            if(window["useJqxGrid"])
                hideShowElementByClassName(detailDiv, false);
            else
                detailDiv.style.display = "block";
        }
    }
}


//----------------------------------------------------------------------------------
// Mini Note Related javascript functions.
//----------------------------------------------------------------------------------

var currentSelectedNoteContent = "";
var currentSelectedNoteFieldId = "";
var currentSelectedNoteImgFieldId = "";
var currentSelectedNoteIsSecured = false;

function maintainNoteImageForAllNoteFields() {
    var beginTime = new Date();
    //NoteFieldIdList is automatically added to the page by the framework - See OasisTextArea for autogeneration.
    // Changed to use jQuery selector to support all browser.
    $("[id='NoteFieldIdList']").each(function() {
        maintainNoteImage($(this).text(), "IMG_" + this.innerText);
    });

    var endTime = new Date();
//    logDebug("Time spent in maintainNoteImageForAllNoteFields:" + (endTime.getTime() - beginTime.getTime()) + "ms");
}

function maintainNoteImage(noteField, noteImgField) {
    if (getObject(noteImgField)) {
        MM_swapImage(getObject(noteImgField).id, '', getCorePath() + noNotesImage, 1)
    }
    if (getObject(noteField)) {
        if (getObject(noteField).value.length > 0) {
            MM_swapImage(getObject(noteImgField).id, '', getCorePath() + notesImage, 1)
        }
    }
    var functionExists = eval("window.handleMaintainNoteImage");
    if (functionExists) {
        handleMaintainNoteImage(noteField);
    }
}

function maintainNote(noteField, noteImgField, showNote, isNoteSecured, isTextAreaPopup, popupTitle) {

    if (showNote) {
        var functionExists = eval("window.getOpenCtxOfDivPopUp");
        currentSelectedNoteContent = getObject(noteField).value;
        currentSelectedNoteFieldId = noteField;
        currentSelectedNoteImgFieldId = noteImgField;
        currentSelectedNoteIsSecured = getObject(noteField).disabled ? true : isNoteSecured;
        if (getObject("d_" + noteField)) {
            if (isDivPopupEnabled()) {
                //openDivPopup(popupTitle, urlToOpen, isModel, isDragable, popupTop, popupLeft, popupWidth, popupHeight, contentWidth, contentHeight, popupId, isShowCloseLink)
                //openDivPopup("Notes", getObject("d_" + noteField).innerHTML, true, true, 100, 100, 500, 220, 200, 220);
                if(isTextAreaPopup) {
                    if (functionExists) {
                        getOpenCtxOfDivPopUp().openDivPopup(popupTitle, getCorePath() + "/note.html", true, true, 150, 250, 700, 550, 646, 474, null, false,"","",false);
                    } else {
                        openDivPopup(popupTitle, getCorePath() + "/note.html", true, true, 150, 250, 700, 550, 646, 474, null, false,"","",false);
                    }
                }
                else {
                    if (functionExists) {
                        getOpenCtxOfDivPopUp().openDivPopup("Notes", getCorePath() + "/note.html", true, true, 150, 250, 500, 250, 446, 174, null, false,"","",false);
                    } else {
                        openDivPopup("Notes", getCorePath() + "/note.html", true, true, 150, 250, 500, 250, 446, 174, null, false,"","",false);
                    }
                }
            }
            else {
                if(window["useJqxGrid"])
                    hideShowElementByClassName(getObject("d_" + noteField), false);
                else
                    getObject("d_" + noteField).style.display = "block";
            }
        }
    }
    else {
        maintainNoteImage(noteField, noteImgField);
    }
}

//----------------------------------------------------------------------------------
// Handle value for multi selection fields.
//----------------------------------------------------------------------------------
function maintainValueForAllMultiSelectFields(gridId) {
    var beginTime = new Date();
    // MSVALFieldIdList contains all Ids for multi-selection field
    var msFields = $(".MSVALFieldIdList");
    for (var i = 0; i < msFields.length; i++) {
        // Use jQuery.text() to get inner text for fix the error on an early version of firefox.
        setMultiSelection($(msFields[i]).text(), gridId);
    }
    var endTime = new Date();
//    logDebug("Time spent in maintainValueForAllMultiSelectedFields:" + (endTime.getTime() - beginTime.getTime()) + "ms");
}

//----------------------------------------------------------------------------------
// Set selection by comma separated value
//----------------------------------------------------------------------------------
function setMultiSelection(fieldId, gridId) {
    var msValue = "";
    var oMSField = getObject(fieldId + "MSVAL");
    var oField = getObject(fieldId);
    var msValueList;
    var len = 0;

    // Only set field that is bind into current selected grid
    var XMLData = getXMLDataForGridName(gridId);
    var datasrc = "#" + XMLData.id;

    // Do nothing if either datasrc or datafld is empty
    if (isEmpty(oField.dataSrc) || isEmpty(oField.dataFld)) {
        return;
    }

    // Check if datasrc is current grid
    if (oField.dataSrc != datasrc) {
        return;
    }

    // Firstly get comma separated value
    if (oMSField) {
        msValue = oMSField.value;
    }

    var options = oField.options;
    var optLen = options.length;

    if (!isEmpty(msValue)) {
        // Parse and set options' selected value
        msValueList = msValue.split(",");
        len = msValueList.length;
    }

    //reset multiSelectPopup field
    var comboTxtFld = getObject(fieldId + "MultiSelectText");
    if (comboTxtFld) {
        comboTxtFld.value = '';
    }
    for (var i = 0; i < optLen; i++) {
        // Unselect current option
        options[i].selected = false;

        // Set option's selected status if match is found
        for (var j = 0; j < len; j++) {
            if (msValueList[j] == options[i].value) {
                options[i].selected = true;

                //set multiSelectPopup field
                if (comboTxtFld) {
                    if (!comboTxtFld.value == '')
                        comboTxtFld.value = comboTxtFld.value + ",";
                    comboTxtFld.value = comboTxtFld.value + options[i].text;
                }
            }
        }
    }
}

function baseOnLoadForDivPopup(divPopFrame) {
    var isOnLoadForDivPopupSuccess = true;
    var functionExists = eval("window.commonOnLoadForDivPopup");
    if (functionExists) {
        isOnLoadForDivPopupSuccess = commonOnLoadForDivPopup(divPopFrame);

        isOnLoadForDivPopupSuccess = nvl(isOnLoadForDivPopupSuccess, true);
    }

    if (isOnLoadForDivPopupSuccess) {
        var functionExists = eval("window.handleOnLoadForDivPopup");
        if (functionExists) {
            isOnLoadForDivPopupSuccess = handleOnLoadForDivPopup(divPopFrame);

            isOnLoadForDivPopupSuccess = nvl(isOnLoadForDivPopupSuccess, true);

            //alert('Content:'+divPopFrame.document.parentWindow.currentSelectedNoteContent);
            //getObject(frameid).getElementById("txtNote").value = getObject(divPopFrame.document.parentWindow.currentNoteField).value;
        }
    }

    return isOnLoadForDivPopupSuccess;
}

function baseOnUnloadForDivPopup(divPopFrame) {

    var isOnUnloadForDivPopupSuccess = true;

    /**
     * 1. we have page cmClaimTxnPaymentLetter will get errors when getParentWindowOfDivPopupFrame in IE11.
     *    It works well in Chrome and IE8.
     * 2. add try catch and don't add error handle since the opened page just
     *    a pdf page.
     */
    try {
        var parentWindow = getParentWindowOfDivPopupFrame(divPopFrame);

        if (parentWindow.currentSelectedNoteFieldId){
            if (getObject(parentWindow.currentSelectedNoteFieldId).value != parentWindow.currentSelectedNoteContent) {
                getObject(parentWindow.currentSelectedNoteFieldId).value = parentWindow.currentSelectedNoteContent;
                dispatchElementEvent(getObject(parentWindow.currentSelectedNoteFieldId), "change");

                maintainNote(parentWindow.currentSelectedNoteFieldId,
                    parentWindow.currentSelectedNoteImgFieldId, false);
            }
            parentWindow.currentSelectedNoteFieldId = null;
        }
    } catch (e) {
        //do nothing now, add handle if it needs.
    }

    var functionExists = eval("window.commonOnUnloadForDivPopup");
    if (functionExists) {
        isOnUnloadForDivPopupSuccess = commonOnUnloadForDivPopup(divPopFrame);

        isOnUnloadForDivPopupSuccess = nvl(isOnUnloadForDivPopupSuccess, true);
    }

    if (isOnUnloadForDivPopupSuccess) {
        var functionExists = eval("window.handleOnUnloadForDivPopup");
        if (functionExists) {
            isOnUnloadForDivPopupSuccess = handleOnUnloadForDivPopup(divPopFrame);

            isOnUnloadForDivPopupSuccess = nvl(isOnUnloadForDivPopupSuccess, true);
        }
    }

    return isOnUnloadForDivPopupSuccess;

}

function baseHandleOnPageBack() {
    window.history.back();
}


function basePageOnLoad(evt) {
    var event = fixEvent(evt);

    // init grid
    initAllGrids();

    // Init search on enter.
    if (window.handleSearchOnEnter) {
        dti.oasis.ui.initSearchOnEnter(window.handleSearchOnEnter);
    }

    //initial the record exists fields information for handleOnLoad call.This need initial before first row selected.
    spliceTheRecordExistsFields();

    if (window.sortGridOnPageLoad) {
        dti.oasis.page.setProperty("sortingGridOnPageLoad", true);
        try {
            sortGridOnPageLoad();
        } catch (e) {
            // do nothing.
        }

        dti.oasis.page.setProperty("sortingGridOnPageLoad", false);
    }

    // Get the list of grids that needs the first row to be selected and select the first record.
    try {
        isProcessFieldDeps = false;
        if (window.selectRowInGridOnPageLoad) {
            selectRowInGridOnPageLoad();
        } else {
            var _gridIdToFireSelectFirstRowInGrid = getGridIdToFireSelectFirstRowInGrid();

            for (var gridIdx in _gridIdToFireSelectFirstRowInGrid) {
                if (typeof(_gridIdToFireSelectFirstRowInGrid[gridIdx]) == "string") {
                    $.when(dti.oasis.grid.getLoadingPromise(_gridIdToFireSelectFirstRowInGrid[gridIdx])).then(function(){
                        selectFirstRowInGrid(_gridIdToFireSelectFirstRowInGrid[gridIdx]);
                    });
                }
            }
        }
    } finally {
        isProcessFieldDeps = true;
    }

    // TODO: Hold off on displaying them until after the grid load is complete. This requires the grid and it's containing panel to be visible
    // Display grid based form fields
    // if (!dti.oasis.page.useJqxGrid()) {
    $(".gridFormFieldsHidden").removeClass("gridFormFieldsHidden");
    // }

    // Fire OBR for page fields.
    if (window.enforceOBRForPageFields) {
        enforceOBRForPageFields();
    }

    event.returnValue = true;
    MM_preloadImages(getCorePath() + '/images/orangebutton_f2.jpg');

    if (isPopup) {
        // reset Session Timeout Object
        resetSessionTimeoutObject();
        reAdjustIFrameSize();
    }
    else {
        // Initialize session time out object
        initializeSessionTimeout();
    }

    var isLoadEventSuccess = event.returnValue;
    if (isLoadEventSuccess) {

        //  Execute field dependency logic, if it exists
        //  This should be executed before common/handleOnLoad
        //      because of either of those methods change the visibility or enabled attributes of page elements,
        //      this function may overwrite the custom changes.
        var functionExists = eval("window.processFieldDeps");
        if (functionExists) {
            var returnVal = processFieldDeps();

            if (typeof returnVal != "undefined" || returnVal != null) {
                event.returnValue = returnVal;
                isLoadEventSuccess = returnVal;
            }
        }

        if (isLoadEventSuccess) {
            //  Execute pageEntitlements logic, if it exists
            //  This should be executed before common/handleOnLoad
            //      because of either of those methods change the visibility or enabled attributes of page elements,
            //      this function may overwrite the custom changes.
            var functionExists = eval("window.pageEntitlements");
            if (functionExists) {
                var returnVal = pageEntitlements(false);

                if (typeof returnVal != "undefined" || returnVal != null) {
                    event.returnValue = returnVal;
                    isLoadEventSuccess = returnVal;
                }
            }
        }

        autoHideTROnLoad();

        //***  Execute system level logic, if one exists ***
        if (isLoadEventSuccess) {
            functionExists = eval("window.commonOnLoad");
            if (functionExists) {
                var returnVal = commonOnLoad(event);

                if (typeof returnVal != "undefined" || returnVal != null) {
                    event.returnValue = returnVal;
                    isLoadEventSuccess = returnVal;
                }
                //check if window closed after commonOnLoad invoked. If current window opens new window
                // and close current window on commonOnLoad. current window objects will be undefined in ie11.Current window loading should be break.
                if (!window.eval) {
                    return;
                }
            }
        }

        if (isLoadEventSuccess) {
            functionExists = eval("window.handleOnLoad");
            if (functionExists) {
                var returnVal = handleOnLoad(event);

                if (typeof returnVal != "undefined" || returnVal != null) {
                    event.returnValue = returnVal;
                    isLoadEventSuccess = returnVal;
                }
                //check if window closed after handleOnLoad invoked.If current window opens new window
                // and close current window on handleOnLoad. current window objects will be undefined in ie11. Current window loading should be break.
                if (!window.eval) {
                    return;
                }
            }
        }
        if (isLoadEventSuccess) {
            maintainNoteImageForAllNoteFields();
        }

    }

    if (isPopup) {
        if (frameElement == null) {    //Not a divPopup
            if (eval("window.getPopupHeight")
                && eval("window.getPopupWidth")) {

                if (getPopupHeight() != null || getPopupWidth() != null) {
                    window.resizeTo((getPopupWidth() ? getPopupWidth() : window.width), (getPopupHeight() ? getPopupHeight() : window.height));
                }
            }

            if (eval("window.getPopupTop")
                && eval("window.getPopupLeft")) {
                if (getPopupTop() != null || getPopupLeft() != null) {
                    window.moveTo((getPopupLeft() ? getPopupLeft() : window.left), (getPopupTop() ? getPopupTop() : window.top));
                }
            }
        }
    }

    // Add hook for view special condition messages
    var functionExists = eval("window.preViewSpecialConditionMessages");
    if (functionExists) {
        window.preViewSpecialConditionMessages();
    }

    processEnterableDropdownTextField();
    processExpiredOptionsForDropdownFields();

    // remove the class on the button DIV
    $(".actionItemGroupHidden").removeClass("actionItemGroupHidden");

    // add logic to skip baseOnBeforeUnload on links which don't unload page
    $(document).on('click','a[href^="javascript:"]', function(event) {
        var continueB = false;
        // It's a jquery event, we can always use event.target.
        var srcElement = event.target;
        var tagName = srcElement.tagName;
        var elem = this;
        if (srcElement.dataFld) {
            // Make sure the firing element is not an INPUT or ANCHOR element inside the <TD> for the current <TR>
            if (tagName != "INPUT" && tagName != "SELECT" && tagName != "TEXTAREA") {
                if (srcElement.parentElement.tagName != "A") {
                    continueB = true;
                } else {
                    if (elem.dataSrc) {
                        continueB = true;
                    }
                }
            }
        } else {
            continueB = true;
        }

        if (continueB) {
            skipUnloadPageForCurrentAndIFrame();
        }
    });

    dti.oasis.grid.whenAllVisibleGridsLoadingComplete().then(function() {
        var functionExists = eval("window.handleFocusOnLoad");
        if (functionExists) {
            handleFocusOnLoad();
        } else {
            if (!window.activeElement){
                setFocusToFirstField();
            }
        }

        $("#overlay-div-for-loading").hide();
        $(".loading-div").hide();
    });

    event.returnValue = isLoadEventSuccess;
}

function initAllGrids() {
    var tables = $("div.divGrid table.clsGrid");
    for (var i = 0; i < tables.length; i++) {
        var gridId = tables[i].id;
        initGrid(gridId);
    }
}

function initGrid(gridId) {
    var gridTable = getTableForGrid(gridId);
    var tableDiv = $(getDivForGrid(gridId));
    var divHolder = tableDiv.parent();

    dti.gridutils.setGridWidth(tableDiv);

    tableDiv.show();
    tableDiv.scroll(function () {
        adjustHeaderWhenScroll(this);
    });
}

function adjustHeaderWhenScroll(gridDiv){
    var currentTop = gridDiv.scrollTop - 2;
    $(gridDiv).find("th").each(function () {
        this.style.top = currentTop;
    });
}

function addScrollHeaderForGrid(gridId) {
    var gridTable = getTableForGrid(gridId);
    if (gridTable != null) {
        var tableDiv = $(gridTable).parent();
        var originalHeader = $(gridTable).find("thead");
        var clonedHeader = originalHeader.clone();
        originalHeader.addClass('scrollTableHeaderOriginal');

        var scrollHeaderDiv = document.createElement("div");
        $(scrollHeaderDiv).addClass('scrollTableHeaderDiv');

        var scrollHeaderTable = document.createElement("table");
        $(scrollHeaderTable).addClass('clsGrid');
        $(scrollHeaderTable).addClass('scrollTableHeader');

        scrollHeaderTable.appendChild(clonedHeader[0]);
        scrollHeaderDiv.appendChild(scrollHeaderTable);
        $(gridTable).parent().before(scrollHeaderDiv);

        // for horizontal scroll bar
        tableDiv.scroll(function () {
            var scrollHeaderDiv = $(this).prev();
            var scrollHeaderTable = scrollHeaderDiv.find("table");
            scrollHeaderTable[0].style.left = 0 - this.scrollLeft;
        });

        setInterval("refreshScrollHeader('" + gridId + "')", 100);
    }
}

function refreshScrollHeader(gridId){
    var gridTable = getTableForGrid(gridId);
    if (gridTable != null) {
        var originalHeader = $(gridTable).find("thead");
        var tableDiv = $(gridTable).parent();
        var scrollHeaderDiv =  tableDiv.prev();
        var scrollHeaderTable =  scrollHeaderDiv.find("table");

        $(scrollHeaderDiv).width(tableDiv.width());
        $(scrollHeaderTable).width($(gridTable).width());
        gridTable.style.top = 0 - originalHeader.height();
        // adjust div height because the scroll header is outside of the div
        var originalHeight = tableDiv.attr("originalHeight");
        if (originalHeight == null) {
            originalHeight = tableDiv.height();
            tableDiv.attr("originalHeight", originalHeight);
        } else {
            originalHeight = parseInt(originalHeight);
        }
        tableDiv.height(originalHeight - scrollHeaderDiv.height());

        var oldths = $(gridTable).find("th");
        var newths = scrollHeaderTable.find("th");
        for (var i = 0; i < newths.length; i++) {
            var oldTH = $(oldths[i]);
            var newTH = $(newths[i]);
            newTH.width(oldTH.width());
        }
    }
}

function getPageViewStateCleanupURL() {
    var url = "";
    url = getAppPath() + "/session/pageViewState/cleanUp.do?process=scheduleCleanup";
    return url;
}

function baseOnBeforeUnload(evt) {
    var event = fixEvent(evt);

    //make a call to common handler to get any overridden common url to perform cleanup activity
    var functionExists = eval("window.commonOnPageBeforeUnload");
    if (functionExists) {
        event.returnValue = commonOnPageBeforeUnload(event);
    }

    //make a call to page handler to get any overridden page level url to perform cleanup activity
    functionExists = eval("window.handleOnPageBeforeUnload");
    if (functionExists) {
        event.returnValue = handleOnPageBeforeUnload(event);
    }

    if (promptUserIfIsOkToLeavePage == true && !window.frameElement) {
        var pageAndIframeDataChanged = false;
        if (window.isPageAndIFrameDataChanged) {
            pageAndIframeDataChanged = isPageAndIFrameDataChanged();
        }
        if (pageAndIframeDataChanged) {
            event.returnValue = getMessage("core.page.leave.changed");
        }
    }
    if (typeof (event.returnValue) == 'undefined') {
        var pageViewStateCleanupUrl = getPageViewStateCleanupURL();
        resetPageViewStateCache(pageViewStateCleanupUrl, "", "");
    }
}

var promptUserIfIsOkToLeavePage = true;
function skipPromptUserIfIsOkToLeavePage() {
    promptUserIfIsOkToLeavePage = false;
}

var hasResetPageViewStateCache = false;

function resetPageViewStateCache(cleanUpUrl, cacheIdForPageViewState, cacheId) {

    if (hasResetPageViewStateCache) {
        return;
    } else {
        hasResetPageViewStateCache = true;
    }
    var formName = "";
    // Page can be refreshed, even before the document got loaded by respective AJAX call.
    if (document) {
        if (document.forms) {
            if (document.forms.length > 0) {
                if (document.forms[0].name) {
                    formName = document.forms[0].name;
                }
            }
        }
    }

    /*
     Do not fire AJAX clean up activity request for login for forgotpassword page.

     There is no user session established yet and the AJAX request will fail;
     thereby the redirect url will be back to login page.

     After successful login, the browser will automaitcally take to the failed previsous request, which is the
     clean up ajax request instead of redirecting to the home page for the associated application.
     */

    if (formName!="processor" && formName != "forgotpassword" && formName != "jumpToOptionForm") {
        // fire ajax request to cleanup page view state for all requests other than for login page.
        var dt = new Date();
        var url = "";
        if (cleanUpUrl) {
            url = cleanUpUrl;
        } else {
            url = getPageViewStateCleanupURL();
        }

        if (cacheIdForPageViewState) {
            url += "&" + cacheIdForPageViewState;
        } else {
            url += "&" + PAGE_VIEW_STATE_CACHE_KEY;
        }
        if (cacheId)  {
            url += "=" + cacheId;
        } else {
            url += "=" + getPageViewStateId();
        }
        url += "&dt=" + dt.getTime() ;

        // initiate call
        new AJAXRequest("get", url, "", "", false);
    }
}

//set focus to first visible, editable field
function setFocusToFirstFieldInGrid(gridId) {
    var inputFields = $("input:visible:enabled[data-dti-datasrc='#".concat(gridId).concat("1']"));
    for (var j = 0; j < inputFields.length; j++) {
        var field = $(inputFields[j]);
        if (isInputField(inputFields[j]) && !isReadOnlyTextField(inputFields[j])) {
            setTimeout(function() {inputFields[j].focus()}, 30);
            break;
        }
    }
}

function setFocusToFirstField() {
    var found = false;
    var panelDivList = $("div[class='panel']");
    for (var i = 0; i < panelDivList.length; i++) {
        var panelDiv = $(panelDivList[i]);
        if (!panelDiv.is(':visible')) {
            continue;
        }
        if (panelDiv.closest("#policyHeaderInfo").length > 0
            || panelDiv.closest("#accountHeaderInfo").length > 0
            || panelDiv.closest("#claimHeaderInfo").length > 0
            || panelDiv.closest("#caseHeaderInfo").length > 0
            || panelDiv.closest("#cwbContextInfo").length > 0) {
            //skip fields in header div
            continue;
        }
        var inputFields = $(":input:visible:enabled", panelDiv);
        for (var j = 0; j < inputFields.length; j++) {
            var field = $(inputFields[j]);
            if (isInputField(inputFields[j]) && !isReadOnlyTextField(inputFields[j])) {
                inputFields[j].focus();
                found = true;
                break;
            }
        }
        if (found) {
            break;
        }
    }
}

// check whether the field is for edit.
function isInputField(inputField) {
    var field = $(inputField);
    var tagName = field[0].tagName.toLowerCase();
    if (tagName != "button") {
        if (tagName == "input") {
            var inputName = field.attr("name") || "";
            var inputType = field.attr("type").toLowerCase();
            if (inputType != "button" && inputType != "submit" && inputType != "reset"
                    && isStringValue(inputName) && inputName.indexOf("chkCSELECT_ALL") == -1 && inputName.indexOf("chkCSELECT_IND") == -1) {
                return true;
            }
        } else {
            return true;
        }
    }
    return false;
}

// check whether the text field is readonly.
function isReadOnlyTextField(inputField) {
    var field = $(inputField);
    var inputType = inputField.type.toLowerCase();
    if((inputType == "textarea" || inputType == "text") && field.attr("readOnly")) {
        return true;
    }
    return false;
}

function processEnterableDropdownTextField() {
    // Set values for the combo text field of dropdown list
    var etFields = $(".EnterableDropdownTxtFieldIdList");
    for (var i = 0; i < etFields.length; i++) {
        setDropdownTextFieldValue(etFields[i].innerText);
    }
}

function processExpiredOptionsForDropdownFields() {
    var dropdownFields = dropdownFieldsWithExpiredOptions;
    if (!isEmpty(dropdownFields) && !isEmpty(expiredOptionSuffixIndicator)) {
        var fields = dropdownFields.split(",");
        for (var i = 0; i < fields.length; i++) {
            if (!isEmpty(fields[i]) && getObject(fields[i])) {
                distinguishExpiredOptionsForField(getObject(fields[i]));
                syncToLovLabelIfExists(getObject(fields[i]));
            }
        }
    }
}

function distinguishExpiredOptionsForField(field){
    if (!isEmpty(expiredOptionSuffixIndicator)){
        if (field.options) {
            for (var i = 0; i < field.options.length; i++) {
                if (field.options[i].text.endsWith(expiredOptionSuffixIndicator)) {
                    field.options[i].title = expiredOptionTitle;
                    field.options[i].className = "expiredOption";
                    // field.options[i].style.display="none";   // not working for IE.
                    //replace label conditionally
                    if (!isEmpty(expiredOptionDisplayableSuffix)) {
                        var indexOfExpiredSuffix = field.options[i].text.lastIndexOf(expiredOptionSuffixIndicator);
                        if (indexOfExpiredSuffix > 0) field.options[i].text = field.options[i].text.substring(0, indexOfExpiredSuffix) + expiredOptionDisplayableSuffix;
                    }
                }
            }
        }
    }
}

function basePageOnUnload(evt) {
    var event = fixEvent(evt);

    var isUnloadEventSuccess = true;
    //***  Execute system level logic, if one exists ***
    var functionExists = eval("window.commonOnUnload");
    if (functionExists) {
        event.returnValue = commonOnUnload(event);

        // Handle the common handleOnUnload function not setting the returnValue
        isUnloadEventSuccess = nvl(event.returnValue, true);
    }

    if (isUnloadEventSuccess) {
        functionExists = eval("window.handleOnUnload");
        if (functionExists) {
            event.returnValue = handleOnUnload(event);

            // Handle the custom handleOnUnload function not setting the returnValue
            isUnloadEventSuccess = nvl(event.returnValue, true);
        }
    }
    event.returnValue = isUnloadEventSuccess;

    // Reset page view state data cached for the current page, before moving out of the current page.
    var pageViewStateCleanupUrl = getPageViewStateCleanupURL();
    resetPageViewStateCache (pageViewStateCleanupUrl, "", "");
}

//----------------------------------------------------------------------------------
// Mini Note Related javascript functions.
//----------------------------------------------------------------------------------

/**
 Hides or disables an action item based on which UI style edition is
 in use.
 */
function changeActionItemDisplay(objectId, isHide) {
    if (!isHide) {
        isHide = false;
    }
    if (document.all(objectId)) {
        if (document.all(objectId).length > 0) {
            for (var i = 0; i < document.all(objectId).length; i++) {
                if (getUIStyleEdition() == "2") {
                    document.all(objectId).item(i).disabled = isHide;
                }
                else {
                    if(window["useJqxGrid"])
                        hideShowElementByClassName(document.all(objectId).item(i), isHide);
                    else
                        document.all(objectId).item(i).style.display = (isHide ? "none" : "block");
                }
            }
        }
        else {
            if (getUIStyleEdition() == "2") {
                document.all(objectId).disabled = isHide;
            }
            else {
                if(window["useJqxGrid"])
                    hideShowElementByClassName(document.all(objectId), isHide);
                else
                    document.all(objectId).style.display = (isHide ? "none" : "block");
            }
        }
    }
}

//----------------------------------------------------------------------------------
// Panel Related javascript functions.
//----------------------------------------------------------------------------------
function togglePanel(src, panelToToggle, panelTitleId, expandTitle, collapseTitle) {
    //logDebug('togglePanel('+expandTitle+', '+collapseTitle);
    var panelTitleObj = null;
    if (panelTitleId) {
        panelTitleObj = getSingleObject(panelTitleId);
    }
    var panelObjToToggle = getSingleObject(panelToToggle);
    if (hasClassName(panelObjToToggle,COLLAPSE_PANEL_CLASS)) {
        removeObjectClassName(panelObjToToggle,COLLAPSE_PANEL_CLASS);
        src.className = "panelUpTitle";
        if (panelTitleObj && expandTitle) {
            panelTitleObj.innerHTML = expandTitle;
        }
    } else {
        addObjectClassName(panelObjToToggle,COLLAPSE_PANEL_CLASS);
        src.className = "panelDownTitle";
        if (panelTitleObj && collapseTitle) {
            panelTitleObj.innerHTML = collapseTitle;
        }
    }
    return true;
}

function getPanel(childControl) {
    var panelControl = null;
    if (childControl) {
        while (childControl.parentElement) {
            panelControl = childControl.parentElement;
            if (childControl.parentElement.id == "panel") {
                break;
            }
            childControl = panelControl;
        }
    }
    return panelControl;
}

//----------------------------------------------------------------------------------
// Panel Related javascript functions.
//----------------------------------------------------------------------------------
function openODSReportURL() {
    if (odsReportURL) {     //defined & initialized in header.jsp
        openWebApplication(odsReportURL, true);
    } else {
        alert('Please setup the system parameter [ODS_URL] to point to correct BOE infoview link.');
    }
}

function openWebApplication(url, isOpenInNewWindow) {
    if (!isOpenInNewWindow) {
        var functionExists = eval("window.commonIsOkToChangePages");
        if (functionExists) {
            isOkToProceed = commonIsOkToChangePages(null, url);
            if (!isOkToProceed) {
                return;
            }
        }
        if (window.isOkToChangePages)
            if (!isOkToChangePages(null, url))
                return;
    }

    if (url.indexOf("('~/") > 0) {
        url = url.replace("('~/", "('" + getCorePath() + "/");
    }
    if (url.indexOf('~envPath/') != -1) {
        url = getEnvPath() + url.substr(url.indexOf('~envPath/') + 8);
    }
    if (isOpenInNewWindow) {
        window.open(url);
    }
    else {
        if (getUIStyleEdition()!="0") {
            showProcessingImgIndicator();
        }
        setWindowLocation(url);
    }
}

// Get all input form fields including some invisible fields caused by field dependency and page entitlements
function getAllFormFieldsForGrid(gridId) {
    var table = getTableForGrid(gridId);
    var divId = getTableProperty(table, "gridDetailDivId");
    var inputFields = getTableProperty(table, "allFormFieldsForGrid");
    if (!inputFields) {
        if (divId == ""){
            inputFields = $(":input[datasrc='#" + gridId + "1']");
        }else{
            var gridDetailDiv = $("#" + divId);
            inputFields = $(":input", gridDetailDiv);
        }
        setTableProperty(table, "allFormFieldsForGrid", inputFields);
    }
    return inputFields;
}

// Get last editable form field in the form bound to a grid
function getLastEditableFormField(gridId) {
    var inputFields = getAllFormFieldsForGrid(gridId);
    for (var j = inputFields.length - 1; j > -1; j--) {
        if (isInputField(inputFields[j]) &&
            $(inputFields[j]).is(':visible') &&
            $(inputFields[j]).is(':enabled')) {
            return inputFields[j];
        }
    }
    return null;
}

// Get first editable form field in the form bound to a grid
function getFirstEditableFormField(gridId) {
    var inputFields = getAllFormFieldsForGrid(gridId);
    for (var j = 0; j < inputFields.length; j++) {
        if (isInputField(inputFields[j]) &&
            $(inputFields[j]).is(':visible') &&
            $(inputFields[j]).is(':enabled')) {
            return inputFields[j];
        }
    }
    return null;
}

// Position cursor to the first editable form field in the form bound to a grid
function setFocusToFirstEditableFormField(gridId) {
    var field = getFirstEditableFormField(gridId);
    if (field != null) {
        field.focus();
    }
}

function HashTable(obj) {
    this.length = 0;
    this.items = {};
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            this.items[p] = obj[p];
            this.length++;
        }
    }

    this.setItem = function (key, value) {
        var previous = undefined;
        if (this.hasItem(key)) {
            previous = this.items[key];
        }
        else {
            this.length++;
        }
        this.items[key] = value;
        return previous;
    }

    this.getItem = function (key) {
        return this.hasItem(key) ? this.items[key] : undefined;
    }

    this.hasItem = function (key) {
        return this.items.hasOwnProperty(key);
    }

    this.removeItem = function (key) {
        if (this.hasItem(key)) {
            previous = this.items[key];
            this.length--;
            delete this.items[key];
            return previous;
        }
        else {
            return undefined;
        }
    }

    this.keys = function () {
        var keys = [];
        for (var k in this.items) {
            if (this.hasItem(k)) {
                keys.push(k);
            }
        }
        return keys;
    }

    this.values = function () {
        var values = [];
        for (var k in this.items) {
            if (this.hasItem(k)) {
                values.push(this.items[k]);
            }
        }
        return values;
    }

    this.each = function (fn) {
        for (var k in this.items) {
            if (this.hasItem(k)) {
                fn(k, this.items[k]);
            }
        }
    }

    this.clear = function () {
        this.items = {}
        this.length = 0;
    }
}

//----------------------------------------------------------------------------------
// The following Code provides functionality of a HashMap Object
//----------------------------------------------------------------------------------
var hashMapObjects = new Array();


/**
 *  @deprecated   Do not use it any more. Please use the function HashTable(obj) instated.
 */
// TODO refactor usages to use the new HashTable function
function getHashMap(hashMapName) {
    var hashMap = null
    // Check if it exists
    for (var i = 0; i < hashMapObjects.length; i++) {
        if (hashMapObjects[i][0] == hashMapName) {
            hashMap = hashMapObjects[i][1];
        }
    }

    if (!hashMap) {
        hashMap = new HashMap(hashMapName);
        hashMapObjects[i] = [hashMapName, hashMap];
    }
    return hashMap;
}
/**
 *  @deprecated   Do not use it any more. Please use the function HashTable(obj) instated.
 */
// TODO refactor usages to use the new HashTable function
function HashMap(hashMapName) {
    this.m_hashMapName = hashMapName;
    this.m_hashMap = new Array();
}

HashMap.prototype.hasElement = function(key) {
    for (var i = 0; i < this.m_hashMap.length; i++) {
        if (this.m_hashMap[i][0] == key) {
            return true;
        }
    }
    return false;
}

HashMap.prototype.putElement = function(key, value) {
    var i = 0
    for (; i < this.m_hashMap.length; i++) {
        if (this.m_hashMap[i][0] == key) {
            break;
        }
    }
    this.m_hashMap[i] = [key, value];
}

HashMap.prototype.getElement = function(key) {
    var element = null;
    for (var i = 0; i < this.m_hashMap.length; i++) {
        if (this.m_hashMap[i][0] == key) {
            element = this.m_hashMap[i][1];
        }
    }
    // If no element found, return null since there is no good exception handling in code.
    if (element == null) {
        debug("Failed to find the element <" + key + "> in the hashmap <" + this.m_hashMapName + ">");
    }
    return element;
}

// Return the Array of Hash Map Array entries.
// Each entry in the Array is an Array where index 0 is the key, and index 1 is the value
HashMap.prototype.getEntries = function() {
    return this.m_hashMap;
}

HashMap.prototype.clean = function() {
    this.m_hashMap = new Array();
}

//----------------------------------------------------------------------------------
// End of JavaScript HashMap object
//----------------------------------------------------------------------------------


function getMessage(messageKey, messageParameters) {
    var messages = getHashMap("messageHashMap");
    var msg = messages.getElement(messageKey);
    /* No parameter is passed */
    if (messageParameters == undefined || messageParameters == null) {
        return msg;
    }
    /* Replace the parameter slot in the string */
    var len = messageParameters.length;
    for (var i = 0; i < len; i++) {
        msg = msg.replace('{' + i + '}', messageParameters[i]);
    }
    return msg;
}

function setMessage(messageKey, message) {
    var messages = getHashMap("messageHashMap");
    messages.putElement(messageKey, message);
}

function getSysParmValue(parameterName) {
    var sysParms = getHashMap("sysParmHashMap");
    return sysParms.getElement(parameterName);
}

function setSysParmValue(parameterName, parameterValue) {
    var sysParms = getHashMap("sysParmHashMap");
    sysParms.putElement(parameterName, parameterValue);
}

function isPageDataChanged() {
    if (isChanged || isPageGridsDataChanged())
        return true;
    else
        return false;
}

function isPageAndIFrameDataChanged() {
    var isChangedFlag = isPageDataChanged();
    if (!isChangedFlag) {
        var iframeElements = document.getElementsByTagName("iframe");
        for (var i = 0; i < iframeElements.length; i++) {
            var iframeWindow = iframeElements[i].contentWindow;
            if (iframeWindow.isPageAndIFrameDataChanged) {
                isChangedFlag = iframeWindow.isPageAndIFrameDataChanged();
                if (isChangedFlag) {
                    break;
                }
            }
        }
    }
    return isChangedFlag;
}

//-----------------------------------------------------------------------------
// if the grid data changed return true,otherwise return false
//-----------------------------------------------------------------------------
function isGridDataChanged(gridId) {
    var xmlData = getXMLDataForGridName(gridId);
    var origXmlData = getOrigXMLData(xmlData);
    var changedNodes = xmlData.documentElement.selectNodes("//ROW[UPDATE_IND != 'N']");
    var changedOrigNodes = origXmlData.documentElement.selectNodes("//ROW[UPDATE_IND != 'N']");
    if (changedOrigNodes.length > 0) {
        return true;
    }
    if (changedNodes.length > 0) {
        for (i = 0; i < changedNodes.length; i++) {
            if (changedNodes[i].getAttribute("id") != '-9999')
                return true;
        }
    }
    return false;
}

//-----------------------------------------------------------------------------
// if the grids' data in page changed return true,otherwise return false
//-----------------------------------------------------------------------------
function isPageGridsDataChanged() {
    if (typeof(tblCount) != 'undefined'){
        for (var i = 0; i < tblCount; i++) {
            if (isGridDataChanged(tblPropArray[i].id))
                return true;
        }
    }
    return false;
}

//-----------------------------------------------------------------------------
// Updates the hidden field value when the visible field is updated
//-----------------------------------------------------------------------------
function updateHiddenFieldForDateField(enteredField, originalField) {
    //logDebug('updateHiddenFieldForDateField('+enteredField.name+', '+originalField.name+')');
    var dtentry = enteredField.value;
    //logDebug("dtentry: "+dtentry);
    //logDebug("Checking Original Field Before: " + originalField.value);
    var newDate='';
    if (isStringValue(dtentry)) {
        var dtVal = dtentry.substring(0, 10);
        var tmVal = dtentry.substring(10);
        var j = dtVal.split('/');
        var d = j[0];
        var m = j[1];
        var y = j[2];
        // Added to support Chinese date format yyyy/mm/dd
        if (DATE_MASK_INTERNATIONAL.substring(0, 4)=='yyyy') {
            j = dtVal.split(DATE_MASK_INTERNATIONAL.substring(4, 5));
            y = j[0];
            m = j[1];
            d = j[2];
        }
        if (DATE_MASK_INTERNATIONAL.indexOf('mon') > 0 ){
            for (j = 0; j < 12; ++j) {
                if (Calendar._SMN[j].toLowerCase() == m.toLowerCase()) { m = j; break; }
            }

            if (j < 9) {
                m = '0' + (j + 1);
            } else {
                m = '' + (j + 1);
            }
        }
        newDate = m + '/' + d + '/' + y;
        originalField.value = newDate + tmVal;
    } else {
        originalField.value = "";
    }
    //logDebug("Checking Original Field After: " + originalField.value);
}

function normalizeFieldName(objid) {
    var obj = getObject(objid);
    if(obj.name.endsWith(DISPLAY_FIELD_EXTENTION))  {
        //logDebug('normalizeFieldName('+obj.name+'): '+obj.name.substring(0, (obj.name.length-10)));
        return obj.name.substring(0, (obj.name.length-10));
    } else
        return obj.name;
}

function setFieldSelect(objid) {
    var field = getObject(objid);
    if(!dateFormatUS) {
        field = getObject(field.name + DISPLAY_FIELD_EXTENTION);
        field.select();
        return;
    }
    field.select();
    return;
}

function setFieldFocus(objid) {
    var field = getObject(objid);
    if(!dateFormatUS) {
        field = getObject(field.name + DISPLAY_FIELD_EXTENTION);
        field.focus();
        return;
    }

    field.blur();
    field.focus();
    return;
}

/**
 * Return the numeric value of a currency field with the input id.
 * The returned value has been filtered all the non-numeric characters.
 * An error message will pop up if the field doesn't exist.
 * Functions in NumberFormatUtils.js and scriptlib.js are referred in this function.
 */
function getMoneyObjNumericValue(fldId) {
    if (!hasObject(fldId)) {
        alert("The element[ " + fldId + "] does NOT exist on the current page");
        return '';
    }
    return unformatMoneyStrValAsStr(getObjectValue(fldId));
}

/**
 * Return the numeric value of a percentage field with the input id.
 * The returned value has been filtered all the non-numeric characters and devided by 100.
 * An error message will pop up if the field doesn't exist.
 * Functions in NumberFormatUtils.js and scriptlib.js are referred in this function.
 */
function getPctObjNumericValue(fldId) {
    if (!hasObject(fldId)) {
        alert("The element[ " + fldId + "] does NOT exist on the current page");
        return '';
    }
    var nf = new NumberFormat(getObjectValue(fldId));
    return nf.toUnformatted();
}

/**
 * Return the numeric value of a number field with the input id.
 * The returned value will keep intact if it is not a number.
 * An error message will pop up if the field doesn't exist or the
 * value is invalid.
 * Functions in NumberFormatUtils.js and scriptlib.js are referred in this function.
 */
function getNumObjNumericValue(fldId) {
    if (!hasObject(fldId)) {
        alert("The element[ " + fldId + "] does NOT exist on the current page");
        return '';
    }
    var value = getObjectValue(fldId);
    if (!checknum(replace(value, ",", ""))) {
        alert("The value[" + value + "]  of " + fldId + " is an invalid number");
        return value;
    }
    var nf = new NumberFormat(value);
    return nf.toUnformatted();
}

//----------------------------------------------------------------------------------
// Syncs date changes in the visible field with invisible field and data in grid
// Used in Date Internationalization
//----------------------------------------------------------------------------------
function syncDisplayableDateToGrid() {

    try {
        var parentTrRow = findParentTrRow(window.event.srcElement);
        var myrow = parentTrRow.rowIndex - 1;
        var myname = window.event.srcElement.name;
        var displayedDate = document.all(myname)[myrow];

        var hiddenDate = document.all(normalizeFieldName(displayedDate))[myrow];
        updateHiddenFieldForDateField(displayedDate, hiddenDate);

        var dataSrc = hiddenDate.dataSrc.substring(1);
        var dataFld = hiddenDate.dataFld;
        var dataGrid = eval(dataSrc);
        dataGrid.recordset(dataFld).value = hiddenDate.value;

    }
    catch(ex) {
        logDebug("Exception: " + ex.name + " : " + ex.message);
    }

}

//-----------------------------------------------------------------------------
// Updates the hidden unformatted phone number field value when the visible field is updated
//-----------------------------------------------------------------------------
function updateUnformattedPhoneNumberField(enteredField, originalField) {
    //logDebug('updateUnformattedPhoneNumberField(' + enteredField.name + ', ' + originalField.name + ')');
    var phnumEntry = enteredField.value;
//    logDebug("phnumEntry: " + phnumEntry);
//    logDebug("Local Phone Number Format: " + PHONENUMBER_FORMAT);
//    logDebug("Checking Original Field Before: " + originalField.value);
    if (isStringValue(phnumEntry)) {
        //validate the phone format
        var isValidPhoneFormat = false;
        if (PHONENUMBER_FORMAT.length == phnumEntry.length) {
            var j = 0;
            for (i = 0; i < phnumEntry.length; i++) {
                if (PHONENUMBER_FORMAT.charAt(j) != phnumEntry.charAt(i) && PHONENUMBER_FORMAT.charAt(j) != "#") {
                    break;
                }
                if (/[^\d]/.test(phnumEntry.charAt(i)) && PHONENUMBER_FORMAT.charAt(j) == "#") {
                    break;
                }
                j++;
            }
            if (i == phnumEntry.length ||  enteredField.value ==PHONENUMBER_FORMAT)
                isValidPhoneFormat = true;
        }
        if (!isValidPhoneFormat) {
            var phoneformat = PHONENUMBER_FORMAT;
            alert('Please enter a valid phone of the format ' + phoneformat.replace(/#/g,'9'));
            enteredField.value = valueBeforeFocus;
            return;
        }
        originalField.value = phnumEntry.replace(/[^\d]/g,'');
        //src value changed on KeyUp event, onchange is not invoked, so fire onchange here
        if(originalField.fireEvent)
            originalField.fireEvent("onchange");
    } else {
        originalField.value = "";
    }
    logDebug("Checking Original Field After: " + originalField.value);
}

function isValidPhoneNumberField(field, emptyOK) {
    var isValidPhoneFormat = false;
    var phnumEntry = field.value;
    if (isStringValue(phnumEntry)) {
        //validate the phone format
        if (PHONENUMBER_FORMAT.length == phnumEntry.length) {
            var j = 0;
            for (i = 0; i < phnumEntry.length; i++) {
                if (PHONENUMBER_FORMAT.charAt(j) != phnumEntry.charAt(i) && PHONENUMBER_FORMAT.charAt(j) != "#") {
                    break;
                }
                if (/[^\d]/.test(phnumEntry.charAt(i)) && PHONENUMBER_FORMAT.charAt(j) == "#") {
                    break;
                }
                j++;
            }
            if (i == phnumEntry.length || field.value == PHONENUMBER_FORMAT)
                isValidPhoneFormat = true;
        }
        if (!isValidPhoneFormat) {
            var phoneformat = PHONENUMBER_FORMAT;
            alert('Please enter a valid phone of the format ' + phoneformat.replace(/#/g, '9'));
        }
    } else {
        if (emptyOK){
            isValidPhoneFormat = true;
        }
    }
    return isValidPhoneFormat;
}

//-----------------------------------------------------------------------------
// Updates the hidden unformatted phone number field value when the visible field is updated
//-----------------------------------------------------------------------------
function newUpdateUnformattedPhoneNumberField(enteredField, originalField) {
    var phnumEntry = enteredField.value;
    originalField.value = phnumEntry.replace(/[^\d]/g, '');
}

//-----------------------------------------------------------------------------
// automatically format the phone numbers when user input
//-----------------------------------------------------------------------------
function localPhoneNumberFormat() {
    var evt = window.event ;
    if (isOkKey(evt))
        return true;

    var val = evt.srcElement.value;
    var formattedPhoneNumber = "";
    var j = 0;
    for (i = 0; i < PHONENUMBER_FORMAT.length; i++) {
        if (PHONENUMBER_FORMAT.charAt(i) == "#") {
            if (j >= val.length)
                break;
            //if find a number or character
            if (isAlphanumeric(val.charAt(j), false)) {
                formattedPhoneNumber += val.charAt(j);
                // go to next character
                j++;
            } else {
                var k = 0;
                // find the next number or character
                for (k = j + 1; k < val.length; k++) {
                    if (isAlphanumeric(val.charAt(k), false)) {
                        formattedPhoneNumber += val.charAt(k);
                        break;
                    }
                }
                j = k + 1;
            }
        }else // display the custom format
            formattedPhoneNumber += PHONENUMBER_FORMAT.charAt(i);
    }
    evt.srcElement.value = formattedPhoneNumber;
    return true
}

function formatPhoneNumberForDisplay(sPhoneNumber) {
    var formatedPhoneNumber = '';
    if (!isEmpty(sPhoneNumber)) {
        var phoneNumberWithoutDash = sPhoneNumber.replace(/\-/g, "");
        //index of phone number string
        var j = 0;
        for (i = 0; i < PHONENUMBER_FORMAT.length; i++) {
            if (PHONENUMBER_FORMAT.charAt(i) == '#') {
                if (j >= phoneNumberWithoutDash.length)
                    break;
                formatedPhoneNumber += phoneNumberWithoutDash.charAt(j);
                //move to the next phone number
                j++;
            } else
                formatedPhoneNumber += PHONENUMBER_FORMAT.charAt(i);
        }
    }
    return formatedPhoneNumber;
}

function formatPhoneNumber(event) {
    event = event || window.event;

    if (isOkKey(event ))
        return true;

    var previousPhoneChars = valueBeforeKeyDown.split('');
    var formattedValue;
    if (event.keyCode < 48 || (event.keyCode > 57 && event.keyCode<96) || event.keyCode>105 ) { // alpha char entered
        formattedValue = valueBeforeKeyDown;
    } else {  // entered a numeric char
        var currentPhoneChars = event.srcElement.value.split('');
        var difference = currentPhoneChars.indexOfDiff(previousPhoneChars);
        if (difference != -1) {
            // delete the char located immediately after the difference
            currentPhoneChars.splice(difference + 1, 1);
        }
        formattedValue = getFormattedPhoneNumber(currentPhoneChars.join(''), PHONENUMBER_FORMAT);
    }
    //set the field with the formatted value.
    event.srcElement.value = formattedValue;
    // get the index for the first # char , so we can place cursor immediately after it
    var cursorPosition = formattedValue.indexOf("#");
    if (cursorPosition == -1) {
        cursorPosition = formattedValue.split("").indexOfDiff(previousPhoneChars) + 1;
        while(cursorPosition <formattedValue.length && !isDigit(formattedValue.charAt(cursorPosition))){
            cursorPosition +=1;
        }
    }
    if ( cursorPosition >0) setCursorForField(event.srcElement, cursorPosition);
    return true;
}


/** phoneNumberFormat uses # denotes numeric characters
 **/
function getFormattedPhoneNumber(phoneNumber, phoneNumberFormat) {
    var formatedPhoneNumber = phoneNumberFormat;
    if (!isEmpty(phoneNumber)) {
        var phoneNumberWithoutFormat = phoneNumber.replace(/[^\d]/g, "").split("");
        var numericChars = phoneNumberFormat.replace(/[^#]/g, '').length;
        for (var i = 0; i < numericChars && i < phoneNumberWithoutFormat.length; i++) {
            formatedPhoneNumber = formatedPhoneNumber.replace('#', phoneNumberWithoutFormat[i]);
        }
    }
    return formatedPhoneNumber;
}

function setCursorForField(field, start, end) {
    if (field.setSelectionRange) {
        field.focus();
        if (typeof end == "undefined") {
            end = start
        }
        field.setSelectionRange(start, end);
    } else if (field.createTextRange) {
        var range = field.createTextRange();
        range.collapse(true);
        range.moveEnd('character', end);
        range.moveStart('character', start);
        range.select();
    }
}

function baseOnLogOut(corePath, evt) {
    var event = fixEvent(evt);
    var isLogOutEventSuccess = false;

    var url = corePath + '/logout.jsp';

    if (window.commonIsOkToChangePages) {
        isOkToProceed = commonIsOkToChangePages("logoff", url);
        if (!isOkToProceed) {
            return;
        }
    }
    if (window.isOkToChangePages)
        if (!isOkToChangePages("logoff", url))
            return;

    if (getUIStyleEdition()!="0") {
        showProcessingImgIndicator();
    }
    var functionExists = eval("window.commonOnLogOut");
    if (functionExists) {
        event.returnValue = commonOnLogOut(event);
        isLogOutEventSuccess = nvl(event.returnValue, true);
    }

    if (isLogOutEventSuccess) {
        functionExists = eval("window.handleOnLogOut");
        if (functionExists) {
            event.returnValue = handleOnLogOut(event);
            isLogOutEventSuccess = nvl(event.returnValue, true);
        }
    }

    setWindowLocation(url);
}

function dropDownTextToBox(objDropdown, strTextboxId) {
    getSingleObject(strTextboxId).value = objDropdown.options[objDropdown.selectedIndex].value;
    //dropDownIndexClear(objDropdown.id);
    getSingleObject(strTextboxId).focus();
}

function dropDownIndexClear(strDropdownId) {
    if (getSingleObject(strDropdownId) != null) {
        getSingleObject(strDropdownId).selectedIndex = -1;
    }
}

//----------------------------------------------------------------------------------
// Set the value for the editable text field of Text&Dropdown list combo component
//----------------------------------------------------------------------------------
function setDropdownTextFieldValue(fieldId) {
    var oETField = getObject(fieldId + "EnterableDropdownText");
    var oField = getObject(fieldId);
    var options = oField.options;
    var optLen = options.length;
    for (var i = 0; i < optLen; i++) {
        if (options[i].selected) {
            oETField.value = options[i].value;
            break;
        }
    }
}

//----------------------------------------------------------------------------------
// CLear the value for the editable text field of Text&Dropdown list combo component
//----------------------------------------------------------------------------------
function clearDropdownTextFieldValue(fieldId) {
    var oETField = getObject(fieldId + "EnterableDropdownText");
    if (oETField) {
        oETField.value = "";
    }
}

//----------------------------------------------------------------------------------
// 1. Sync the selected dropdown field value to its related textbox field when user changes it
// 2. Clear dropdown field when user changes its related textbox field value
//----------------------------------------------------------------------------------
function processEnterableSelectFieldIfExists(field) {
    try {
        if ($(".EnterableDropdownTxtFieldIdList").length > 0) {
            var name = field.name;

            // If change the dropdown list field
            if (hasObject(name + "EnterableDropdownText")) {
                dropDownTextToBox(getObject(field), name + "EnterableDropdownText");
            }
            // If modify the textbox field of the enterable dropdown list
            else if (name.endsWith("EnterableDropdownText")) {
                var dropdownObjName = name.substring(0, name.lastIndexOf("EnterableDropdownText"));
                var dropdownElement = getSingleObject(dropdownObjName);
                dropDownIndexClear(dropdownObjName);

                // create an new option
                if (!isEmpty(field.value)) {
                    var dropdownObj = getObject(dropdownObjName);
                    var options = dropdownObj.options;
                    var optLen = options.length;
                    var dup = false;
                    for (var i = 0; i < optLen; i++) {
                        if (options[i].value == field.value) {
                            dup = true;
                            options[i].selected = true;
                            break;
                        }
                    }
                    if (!dup) {
                        var optionElement = document.createElement("option");
                        optionElement.setAttribute("value", field.value);
                        optionElement.innerHTML = field.value;
                        dropdownElement.appendChild(optionElement);
                        optionElement.selected = true;
                    }

                    // sync the grid column
//            var ds = dropdownObj.dataSrc;
//            if (!isEmpty(ds)) {
//                var gridId = ds.substring(1, ds.length-1);
//                //alert(gridId);
//                var xmlData = getXMLDataForGridName(gridId);
//                var datafld = dropdownElement.getAttribute("datafld");
//                xmlData.recordset(datafld).value = field.value;
//                xmlData.recordset("UPDATE_IND").value = "Y";
//                syncChanges(eval("orig"+gridId+"1"), eval(gridId+"1"));
//            }
                }
            }
            // Clear the dropdown text field value if user changes one list which needs to reload the dropdown list fields.
            else {
                var ajaxInfoField = null;
                try {
                    ajaxInfoField = eval("ajaxInfoFor" + name);
                }
                catch(ex) {
                    ajaxInfoField = null;
                }
                //alert(ajaxInfoField);
                if (ajaxInfoField != null) {
                    var url = "";
                    var urls = ajaxInfoField.split("URL");
                    for (var idx = 0; idx <= urls.length - 1; idx++) {
                        if (urls[idx] != '') {
                            url = urls[idx].substring(urls[idx].indexOf("]") + 1);
                            //alert(url);

                            var delim = "";
                            var delimLoc = url.toUpperCase().indexOf("_DELIM=");
                            if (delimLoc > 0) {
                                delimLoc += "_DELIM=".length;
                                delim = url.substring(delimLoc);
                                if (delim.toUpperCase().indexOf("&") > 0) {
                                    delim = delim.substring(0, delim.toUpperCase().indexOf("&"));
                                }
                            }

                            var loadIntoField = "";
                            var loadIntoFieldLoc = url.toUpperCase().indexOf("FIELDID=");
                            if (loadIntoFieldLoc > 0) {
                                loadIntoFieldLoc += "fieldId=".length;
                                loadIntoField = url.substring(loadIntoFieldLoc);
                                if (loadIntoField.toUpperCase().indexOf("&") > 0) {
                                    loadIntoField = loadIntoField.substring(0, loadIntoField.toUpperCase().indexOf("&"));
                                }

                                // If the loadIntoField exist as a field on the page, set its value to be empty
                                if (isDefined(getObject(loadIntoField))) {
                                    //alert(getObject(loadIntoField).name);
                                    clearDropdownTextFieldValue(getObject(loadIntoField).name);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    catch(ex) {
        // There is no corresponding EnterableDropdownText field, so ignore this field.
    }
}

function getCurrentTime() {
    var d = new Date();

    var curr_hour = d.getHours();
    var curr_min = d.getMinutes();
    var curr_sec = d.getSeconds();

    return curr_hour+':'+curr_min+':'+curr_sec;

}


//-----------------------------------------------------------------------------
// Process Ajax messages
//-----------------------------------------------------------------------------
var processedConfirmationMessages = false;
var processedAjaxErrorMessages = false;
var confirmMessages = [];
var confirmResponses = [];

function handleAjaxMessages(xmlDoc, confirmationHandler) {
    processedConfirmationMessages = false;
    processedAjaxErrorMessages = false;

    // report error if Ajax response is null
    root = xmlDoc.documentElement;
    if (root == null) {
        closeProcessingDivPopup();
        debug("The xml document is null.");
        alert(getMessage("appException.unexpected.error"));
        return false;
    }

    // do nothing if we don't have any messages
    var messages = root.getElementsByTagName("MESSAGE");
    var messageCount = messages.length;
    if (messageCount < 1) {
        closeProcessingDivPopup();
        return true;
    }

    // process each message
    var proceed = true;

    var errorMessages = "";
    var errorFieldId;
    var errorRowId;

    var warningMessages = "";

    var confirmIdx = 0;
    confirmMessages = [];
    confirmResponses = [];

    for (var i = 0; i < messageCount; i++) {
        var messageNodes = messages.item(i).childNodes;
        var category = "";
        var key = "";
        var text = "";
        var needConfirmedAsY = "";

        for (var j = 0; j < messageNodes.length; j++) {
            if (dti.oasis.node.isElementNode(messageNodes.item(j))) {
                var nodeName = messageNodes.item(j).tagName;

                if (nodeName.toUpperCase() == "CATEGORY") {
                    category = dti.oasis.string.trim($(messageNodes.item(j)).text());
                } else if (nodeName.toUpperCase() == "KEY") {
                    key = dti.oasis.string.trim($(messageNodes.item(j)).text());
                } else if (nodeName.toUpperCase() == "TEXT") {
                    text = dti.oasis.string.trim($(messageNodes.item(j)).text());
                } else if (nodeName.toUpperCase() == "CONFIRMEDASYREQUIRED") {
                    needConfirmedAsY = dti.oasis.string.trim($(messageNodes.item(j)).text());
                }
            }
        }
        //alert("category="+category+"\nkey="+key+"\ntext="+text+"\nflag="+needConfirmedAsY);

        //  parse messages
        if (category == "CONFIRMATION_PROMPT") {
            var oMessage = new Object();
            oMessage.messageKey = key;
            oMessage.message = text;
            oMessage.needConfirmedAsY = needConfirmedAsY;
            confirmMessages[confirmIdx] = oMessage;
            confirmIdx = confirmIdx + 1;
        }
        else if (category == "WARNING_MESSAGE" || category == "INFORMATION_MESSAGE") {
            warningMessages += text + "\n";
        }
        else {
            if (errorMessages.length < 1 && messages.item(i).childNodes.length > 4) {
                errorFieldId = messages.item(i).childNodes.item(4).text;
                errorRowId = messages.item(i).childNodes.item(5).text;
            }
            errorMessages += text + "\n";
        }
    }

    // show error messages first
    if (errorMessages.length > 0) {
        processedAjaxErrorMessages = true;
        proceed = false;
        closeProcessingDivPopup();
        handleError(errorMessages, errorFieldId, errorRowId);
    }

    // show other messages only if we don't have errors
    if (proceed) {
        // show warning messages first
        if (warningMessages.length > 0) {
            alert(warningMessages);
        }

        // handle confirmations
        if (confirmMessages.length == 0) {
            processedConfirmationMessages = false;
        }
        else {
            processedConfirmationMessages = true;
            closeProcessingDivPopup();

            // let the even handler do it if we got one
            if (confirmationHandler != null && confirmationHandler.length > 0 && eval(confirmationHandler)) {
                eval("proceed = " + confirmationHandler + "(confirmMessages);");
            }

            // do confirmation if we don't have a handler
            else {
                var size = confirmMessages.length;
                for (var i = 0; i < size; i++) {
                    proceed = confirm(confirmMessages[i].message);
                    setInputFormField(confirmMessages[i].messageKey + ".confirmed", proceed ? 'Y' : 'N');
                    confirmResponses[i] = proceed ? 'Y' : 'N';

                    if (!proceed) {
                        // stop if the response must be "yes"
                        if (confirmMessages[i].needConfirmedAsY == 'Y')
                            break;

                        // otherwise reset the proceed flag
                        else
                            proceed = true;
                    }
                }
            }
        }
    }

    if (!proceed) {
        closeProcessingDivPopup();
    }

    return proceed;
}

function handleAjaxJsonMessages(messages, confirmationHandler) {
    processedConfirmationMessages = false;
    processedAjaxErrorMessages = false;

    // report error if Ajax response is null
    if (messages === null) {
        closeProcessingDivPopup();
        debug("The messages object is null.");
        return false;
    }

    // do nothing if we don't have any messages
    var messageCount = messages.length;
    if (messageCount < 1) {
        closeProcessingDivPopup();
        return true;
    }

    // process each message
    var proceed = true;

    var errorMessages = "";
    var errorFieldId;
    var errorRowId;

    var warningMessages = "";

    var confirmIdx = 0;
    confirmMessages = [];
    confirmResponses = [];

    for (var i = 0; i < messageCount; i++) {
        var message = messages[i];
        var category = message.category;
        var key = message.key;
        var text = message.text;
        var needConfirmedAsY = message.confirmedAsYRequired;
        var field = message.field;
        var rowId = message.rowId;

        //  parse messages
        if (category == "CONFIRMATION_PROMPT") {
            var oMessage = {};
            oMessage.messageKey = key;
            oMessage.message = text;
            oMessage.needConfirmedAsY = needConfirmedAsY;
            confirmMessages[confirmIdx++] = oMessage;
        }
        else if (category == "WARNING_MESSAGE" || category == "INFORMATION_MESSAGE") {
            warningMessages = warningMessages.concat(text, "\n");
        }
        else {
            errorMessages = errorMessages.concat(text, "\n");
        }
    }

    // show error messages first
    if (errorMessages.length > 0) {
        processedAjaxErrorMessages = true;
        proceed = false;
        closeProcessingDivPopup();
        handleError(errorMessages, errorFieldId, errorRowId);
    }

    // show other messages only if we don't have errors
    if (proceed) {
        // show warning messages first
        if (warningMessages.length > 0) {
            alert(warningMessages);
        }

        // handle confirmations
        if (confirmMessages.length == 0) {
            processedConfirmationMessages = false;
        }
        else {
            processedConfirmationMessages = true;
            closeProcessingDivPopup();

            // let the even handler do it if we got one
            if (confirmationHandler != null && confirmationHandler.length > 0 && eval(confirmationHandler)) {
                eval("proceed = ".concat(confirmationHandler, "(confirmMessages);"));
            }

            // do confirmation if we don't have a handler
            else {
                var size = confirmMessages.length;
                for (var i = 0; i < size; i++) {
                    proceed = confirm(confirmMessages[i].message);
                    setInputFormField(confirmMessages[i].messageKey + ".confirmed", proceed ? 'Y' : 'N');
                    confirmResponses[i] = proceed ? 'Y' : 'N';

                    if (!proceed) {
                        // stop if the response must be "yes"
                        if (confirmMessages[i].needConfirmedAsY == 'Y')
                            break;

                        // otherwise reset the proceed flag
                        else
                            proceed = true;
                    }
                }
            }
        }
    }

    if (!proceed) {
        closeProcessingDivPopup();
    }

    return proceed;
}

function hasProcessedAjaxErrorMessages() {
    return processedAjaxErrorMessages;
}

function isConfirmationMessagesProcessed() {
    return processedConfirmationMessages;
}

function hasConfirmation(messageKey) {
    for (var i = 0; i < confirmMessages.length; i++) {
        if (confirmMessages[i].messageKey == messageKey) {
            return true;
        }
    }

    return false;
}

function getConfirmationResponse(messageKey) {
    for (var i = 0; i < confirmMessages.length; i++) {
        if (confirmMessages[i].messageKey == messageKey);
        {
            return confirmResponses[i];
        }
    }

    return "";
}

function isValidationException(xmlDoc) {
    var isValidationException = false;
    root = xmlDoc.documentElement;
    if (root != null) {
        var validationExceptions = root.getElementsByTagName("VALIDATIONEXCEPTION");
        if (validationExceptions.length > 0 && validationExceptions.item(0).childNodes.item(0).text == "YES")
            isValidationException = true;
    }
    return isValidationException;
}

/*
 handle error function for iframe
 */
function handleErrorForFrame(message, fieldId, rowId, gridId) {
    getIFrameWindow().handleError(message, fieldId, rowId, gridId);
}

/*
 Select row in grid, set focus on field and display error message.
 - The optional parameter fieldId can be a single ID or an array of IDs. Only the first will be set focus on.
 - The optional parameter rowId can be a single ID or a pair of parent/child IDs delimited by comma (",").
 - The optional parameter gridId can be a single ID or a pair of parent/child IDs delimited by comma (",").
 */
function handleError(message, fieldId, rowId, gridId) {
    // Get message from input and replace new line for passing it to executeWhenTestSucceeds()
    if (message != null && message.length > 0)
        message = message.replace(/\n/g, "~n~");

    // Take the first field ID if we got an array
    if (fieldId != null && isArray(fieldId) && fieldId.length > 0)
        fieldId = fieldId[0];

    // Get field ID from JS variable if it's not passed in
    if (fieldId == null || fieldId.length == 0)
        fieldId = validateFieldId;

    validateFieldId = fieldId;

    // Get row ID from JS variable if it's not passed in
    if (rowId == null || rowId.length == 0)
        rowId = validateRowId;

    validateRowId = rowId;

    // Split row ID into parent and child if we got a pair
    var index = rowId.indexOf(",");
    var parentRowId = "";
    var childRowId = "";
    if (index > 0) {
        parentRowId = trim(rowId.substr(0, index));
        childRowId = trim(rowId.substr(index + 1));
    }
    else {
        parentRowId = rowId;
    }

    // Handle grid if we got row ID
    if (rowId != null && rowId.length > 0) {
        // Get grid ID from JS variable if it's not passed in
        if (gridId == null || gridId.length == 0)
            gridId = validateGridId;

        // Get parent and child grid IDs
        var parentGridId = "";
        var childGridId = "";
        if (gridId == null || gridId.length == 0) {
            parentGridId = getParentGridId();
            childGridId = getChildGridId();
        }
        else {
            index = gridId.indexOf(",");
            if (index > 0) {
                parentGridId = trim(gridId.substr(0, index));
                childGridId = trim(gridId.substr(index + 1));
            }
            else {
                parentGridId = gridId;
            }
        }

        if (parentGridId.length > 0 && parentRowId.length > 0) {
            // select the parent row
            selectRowById(parentGridId, parentRowId);

            // invoke the next process when tables are ready
            var testCode = 'getTableProperty(getTableForGrid(\"' + parentGridId + '\"), "isUserReadyStateReadyComplete")';
            var callbackCode;
            // Have to wait for filtering in child grid before set focus in parent grid,
            // or we don't get the flashing cursor
            if (childGridId.length > 0 && childRowId.length > 0) {
                testCode += ' && !getTableProperty(getTableForGrid(\"' + childGridId + '\"), "filtering") ';
                callbackCode = 'handleErrorNextStep(\"' + message + '\", \"' + fieldId + '\", \"' + childRowId + '\", \"' + childGridId + '\");';
            }
            else {
                if (getChildGridId() != parentGridId)
                    testCode += ' && !getTableProperty(getTableForGrid(\"' + getChildGridId() + '\"), "filtering")';
                callbackCode = 'handleErrorLastStep(\"' + message + '\", \"' + fieldId + '\");';
            }
            executeWhenTestSucceeds(testCode, callbackCode, 50);
        }
        else {
            // Go directly to the last process if we don't have to deal with grid
            handleErrorLastStep(message, fieldId);
        }
    }
    else {
        // Go directly to the last process if we don't have to deal with grid
        handleErrorLastStep(message, fieldId);
    }
}

/*
 This is an internal function that should only be called by handleError().
 It's one of the three-part process.
 */
function handleErrorNextStep(message, fieldId, childRowId, childGridId) {
    if (childGridId.length > 0 && childRowId.length > 0) {
        var childGridIds = childGridId.split(",");
        var childRowIds = childRowId.split(",");

        // select first grid
        selectRowById(childGridIds[0], childRowIds[0]);

        if (childGridIds.length > 1) {
            // select next grid
            // invoke the next process when the table is ready
            // Get Test codes
            var testCode = 'getTableProperty(getTableForGrid(\"' + childGridIds[0] + '\"), "isUserReadyStateReadyComplete")';

            childGridIds.splice(0, 1);
            childRowIds.splice(0, 1);

            for (var j = 0; j < childGridIds.length; j++) {
                var nextChildGridId = childGridIds[j];
                testCode += ' && !getTableProperty(getTableForGrid(\"' + nextChildGridId + '\"), "filtering") ';
            }

            var callbackCode = 'handleErrorNextStep(\"' + message + '\", \"' + fieldId + '\", \"' + childRowIds + '\", \"' + childGridIds + '\");';
        } else {
            // Handle error last step
            // invoke the next process when the table is ready
            var testCode = 'getTableProperty(getTableForGrid(\"' + childGridId + '\"), "isUserReadyStateReadyComplete")';
            var callbackCode = 'handleErrorLastStep(\"' + message + '\", \"' + fieldId + '\");';
        }
        executeWhenTestSucceeds(testCode, callbackCode, 50);
    } else {
        // Go directly to the last process if we don't have to deal with grid
        handleErrorLastStep(message, fieldId);
    }
}

/*
 This is an internal function that should only be called by handleError() or handleErrorNextStep.
 It's one of the three-part process.
 */
function handleErrorLastStep(message, fieldId) {
    // Set focus if we got the field
    var obj = getObject(fieldId);
    if (fieldId != null && fieldId.length > 0 && obj) {
        try {
            if (!obj.disabled && $(obj).is(":visible"))
                getObject(fieldId).focus();
            //        else
            //            alert(fieldId + "is disabled or invisible!");
        }
        catch(ex) {
            // Failed to set focus. Ignore the error.
        }
    }

    // Display message if we got one
    if (message != null && message.length > 0)
        alert(message.replace(/~n~/g, "\n"));

    // Reset validation indicators
    validateFieldId = "";
    validateRowId = "";
    validateGridId = "";
}

function getParentGridId() {
    return getCurrentlySelectedGridId();
}

function getChildGridId() {
    return getCurrentlySelectedGridId();
}

///////////////////////////

function getLocale(){
    //logDebug("getLocale: "+LOCALE);
    return LOCALE;
}

function getOptions(pattern){
    var options = new Object();
    if(!isEmpty(pattern))
        options.format = pattern;
    options.locale = getLocale();
    options.round = false;
    if (window.getFormatNumberRound && window.getFormatNumberRound() === true) {
        options.round = true;
    }
    if (!options.round) {
        if (pattern.indexOf(".") == -1) {
            options.format = pattern + ".";
        }
    }
    return options;
}

function formatNumberFormatted(selector, pattern){
    //logDebug("Pattern: "+pattern);
    var hasColor = nbrFormatPatternHasColor(pattern);
    var hasParentheses = nbrFormatPatternHasParentheses(pattern);

    $(selector).css('color', 'black');

    if($(selector).val()!=null && $(selector).val()!=''){
        pattern = getJsNbrFormatterPattern(pattern);

        var options = getOptions(pattern);

        var num = $(selector).parseNumber(options);
        if(hasColor && num < 0){
            if (!isFieldAttributeSetByOBR(selector[0], 'style.color')) {
                $(selector).css('color', 'red');
            }
        }

        if(hasParentheses && num < 0){
            num = Math.abs(num);
            $(selector).val("("+$.formatNumber(num,options)+")");
        } else {
            $(selector).val($.formatNumber(num,options));
        }
    }
}

function unformatNumberFormatted(selector, pattern){
    //debug("unformatNumberFormatted("+$(selector).val()+","+pattern+")");
    if (!isFieldAttributeSetByOBR(selector[0], 'style.color')) {
        $(selector).css('color', 'black');
    }

    //logDebug($(selector).val());
    var text = $(selector).val();
    if(text != null && text != ''){
        var options = getOptions(pattern);

        var num = $(selector).parseNumber(options);
        //logDebug(text.indexOf("(")+" - - "+text.lastIndexOf(")") + " -- "+text.length);
        if(text.indexOf("(")==0 && (text.lastIndexOf(")")+1)==text.length ){
            $(selector).val(-num);
        }
    }
    //debug("unformatNumberFormattedxx"+$(selector).val()+"xx");
}

function updateNumberFormattedCompanionField(displaySelector, companionSelector, pattern) {
    //debug('updateNumberFormattedCompanionField: $(displaySelector).val():: '+$(displaySelector).val());
    $(companionSelector).val($(displaySelector).val());
    //debug('New Value: $(companionSelector).val():: '+$(companionSelector).val());
    unformatNumberFormatted($(companionSelector), pattern);
}

function getUnformattedNumberFormatted(value, pattern){
    var options = getOptions(pattern);

    //logDebug(value);
    return jQuery.parseNumber(value, options);
}

function updateHiddenFieldForNumberFormattedField(enteredField, originalField, pattern) {
    var dtentry = enteredField.value;
    //logDebug("dtentry: "+dtentry);
    //logDebug("Checking Original Field Before: " + originalField.value);

    if (isStringValue(dtentry)) {
        originalField.value = getUnformattedNumberFormatted(dtentry, pattern);
    } else {
        originalField.value = "";
    }
    //logDebug("Checking Original Field After: " + originalField.value);
}

function syncDisplayableFormattedNumberToGrid(pattern) {

    try {
        var parentTrRow = findParentTrRow(window.event.srcElement);
        var myrow = parentTrRow.rowIndex - 1;
        var myname = window.event.srcElement.name;
        var displayedNbr = $('input[name='+myname+']')[myrow];
        //logDebug('$$$$$ displayedNbr: '+displayedNbr.outerHTML);

        var hiddenNbr = $('input[name='+normalizeFieldName(displayedNbr)+']')[myrow];
        //logDebug('$$$$$ hiddenNbr: '+hiddenNbr.outerHTML);
        updateHiddenFieldForNumberFormattedField(displayedNbr, hiddenNbr, pattern);

        var dataSrc = hiddenNbr.dataSrc.substring(1);
        var dataFld = hiddenNbr.dataFld;
        var dataGrid = eval(dataSrc);
        dataGrid.recordset(dataFld).value = hiddenNbr.value;

    }
    catch(ex) {
        logDebug("Exception: " + ex.name + " : " + ex.message);
    }
}

function getJsNbrFormatterPattern(javaPattern){
    //logDebug("getJsNbrFormatterPattern::javaPattern: "+javaPattern);
    javaPattern = nbrFormatRemoveColor(javaPattern);
    var subPatternSeparator = ";";
    if(javaPattern.search(subPatternSeparator)!=-1){
        javaPattern = javaPattern.split(subPatternSeparator)[0];
    }

    return javaPattern;
}

function getNegativeNbrFormatSubPattern(javaPattern){
    //logDebug("getJsNbrFormatterPattern::javaPattern: "+javaPattern);
    javaPattern = nbrFormatRemoveColor(javaPattern);
    var subPatternSeparator = ";";
    if(javaPattern.search(subPatternSeparator)!=-1){
        javaPattern = javaPattern.split(subPatternSeparator)[1];
    }

    return javaPattern;
}

function nbrFormatPatternHasParentheses(javaPattern){
    //logDebug("nbrFormatPatternHasParentheses::javaPattern: "+javaPattern);
    var subPatternSeparator = ";";
    var result = false;
    //alert("nbrFormatPatternHasParentheses::search: "+javaPattern.search(subPatternSeparator));
    javaPattern = nbrFormatRemoveColor(javaPattern);
    if(javaPattern.search(subPatternSeparator)!=-1) {
        javaPattern = javaPattern.split(subPatternSeparator)[1];
        if(javaPattern.indexOf("(")==0
            && (javaPattern.lastIndexOf(")")+1)==javaPattern.length){
            //alert("nbrFormatPatternHasParentheses::true");
            result = true;
        }
    }
    return result;
}

function nbrFormatPatternMismatchedParentheses(javaPattern){
    //logDebug("nbrFormatPatternMismatchedParentheses::javaPattern: "+javaPattern);
    var subPatternSeparator = ";";
    var result = false;
    //alert("nbrFormatPatternHasParentheses::search: "+javaPattern.search(subPatternSeparator));
    javaPattern = nbrFormatRemoveColor(javaPattern);
    if(javaPattern.search(subPatternSeparator)!=-1) {
        javaPattern = javaPattern.split(subPatternSeparator)[1];
        if((javaPattern.indexOf("(")==0
            && (javaPattern.lastIndexOf(")")+1)!=javaPattern.length) ||
            (javaPattern.indexOf("(")!=0
            && (javaPattern.lastIndexOf(")")+1)==javaPattern.length)){
            //alert("nbrFormatPatternMismatchedParentheses::true");
            result = true;
        }
    }
    return result;
}

function nbrFormatPatternHasColor(javaPattern){
    //logDebug("nbrFormatPatternHasColor::javaPattern: "+javaPattern);
    var colorFlag = "[Red]";
    var result = false;
    if(javaPattern.search(colorFlag)!=-1){
        result = true;
        //alert('Color '+result);
    }
    return result;
}

function nbrFormatPatternHasSubPatterns(javaPattern){
    //logDebug("nbrFormatPatternHasSubpatterns::javaPattern: "+javaPattern);
    var subPatternSeparator = ";";
    var result = false;
    if(javaPattern.search(subPatternSeparator)!=-1) {
        result = true;
    }
    return result;
}

function nbrFormatRemoveColor(javaPattern){
    //logDebug("nbrFormatRemoveColor::javaPattern: "+javaPattern);
    var colorFlag = "[Red]";

    if(javaPattern.search(colorFlag)!=-1){
        javaPattern = javaPattern.replace(colorFlag,"");
        //alert(javaPattern);
    }

    return javaPattern;
}

function nbrFormatNegativeSubPatternRemoveParentheses(javaPattern){
    //logDebug("nbrFormatPatternRemoveParentheses::javaPattern: "+javaPattern);
    var subPatternSeparator = ";";
    //alert("nbrFormatPatternRemoveParentheses::search: "+javaPattern.search(subPatternSeparator));
    javaPattern = nbrFormatRemoveColor(javaPattern);
    if(javaPattern.search(subPatternSeparator)!=-1) {
        javaPattern = javaPattern.split(subPatternSeparator)[1];
        if(javaPattern.indexOf("(")==0
            && (javaPattern.lastIndexOf(")")+1)==javaPattern.length){
            javaPattern = javaPattern.substring(1, (javaPattern.length-1));
        }
    }
    return javaPattern;
}

function numberHasParentheses(numberString) {
    var result = false;
    if (numberString.indexOf("(") == 0
        && (numberString.lastIndexOf(")") + 1) == numberString.length) {
        result = true;
    }
    return result;
}

function setNumberColorInGrid(selector, pattern) {
    var selectorName = selector.attr("name");
    var selectorId = selector.attr("id");
    var debugStr = 'setNumberColorInGrid -';
    if (selectorName)
        debugStr += ' selector: name:: ' + selectorName;
    if (selectorId)
        debugStr += ' - id::  ' + selectorId;
    //debug(debugStr);
    //debug('Selector: ' + selector.selector + ' Length: ' + selector.length);
    //var beginTime = new Date();
    var hasColor = nbrFormatPatternHasColor(pattern);
    var hasParentheses = nbrFormatPatternHasParentheses(pattern);
    if (hasColor) {
        pattern = getJsNbrFormatterPattern(pattern);

        var options = getOptions(pattern);
        //debug(debugStr + ' - length:: ' + $(selector).length);
        $(selector).each(function (index, domEle) {
            // domEle == this
            var myTagName = $(domEle)[0].tagName;
            var myVal = null;
            if (myTagName == 'INPUT')
                myVal = $(domEle).val();
            else
                myVal = $(domEle).text();
            var myNum = jQuery.parseNumber(myVal, options);
            if (hasParentheses && numberHasParentheses(myVal))
                myNum = -myNum;
            //debug("pattern : " + $(domEle).attr("pattern"));
            if (!isFieldAttributeSetByOBR($(domEle)[0], 'style.color')) {
                if (myNum < 0) {
                    $(domEle).css('color', 'red');
                } else {
                    $(domEle).css('color', 'black');
                }
            }
        });
    }
    //var endTime = new Date();
    //debug("Time spent executing setNumberColorInGrid: " + (endTime.getTime() - beginTime.getTime()) + "ms");
    //displayDebugInConsole();
}

function setNumberColorInGridObtainPattern(selector) {
    //var beginTime = new Date();
    var selectorName = selector.attr("name");
    var selectorId = selector.attr("id");
    var debugStr = 'setNumberColorInGridObtainPattern -';
    if (selectorName)
        debugStr += ' selector: name:: ' + selectorName;
    if (selectorId)
        debugStr += ' - id::  ' + selectorId;
    //debug(debugStr);
    var size = $(selector).length;
    //debug('Selector: ' + selector.selector);
    if (size > 0) {
        //debug(debugStr + ' - length:: ' + size);
        $(selector).each(function (index, domEle) {
            // domEle == this
            var pattern = $(domEle).attr("formatPattern");
            var hasColor = nbrFormatPatternHasColor(pattern);
            var hasParentheses = nbrFormatPatternHasParentheses(pattern);
            if (hasColor) {
                pattern = getJsNbrFormatterPattern(pattern);

                var options = getOptions(pattern);
                var myTagName = $(domEle)[0].tagName;
                var myVal = null;
                if (myTagName == 'INPUT')
                    myVal = $(domEle).val();
                else
                    myVal = $(domEle).text();
                var myNum = jQuery.parseNumber(myVal, options);
                if (hasParentheses && numberHasParentheses(myVal))
                    myNum = -myNum;
                if (!isFieldAttributeSetByOBR($(domEle)[0], 'style.color')) {
                    if (myNum < 0) {
                        $(domEle).css('color', 'red');
                    }
                    else {
                        $(domEle).css('color', 'black');
                    }
                }
            }
        });
    } else {
        //debug(debugStr + ' - NOTHING SELECTED');
    }
    //var endTime = new Date();
    //debug("Time spent executing setNumberColorInGridObtainPattern: " + (endTime.getTime() - beginTime.getTime()) + "ms");
}

function setNumberColorInFields(selector, pattern) {    //Change to a single()?
    //debug('setNumberColorInFields - selector: name:: ' + selector.attr("name") + ' - id:: ' + selector.attr("id"));
    var selectorName = selector.attr("name");
    var visibleSpanId = '';
    if (!isEmpty(selectorName)) {
        visibleSpanId = '#' + selectorName.substring(0, selectorName.indexOf(DISPLAY_FIELD_EXTENTION)) + 'ROSPAN';
    }
    var hasColor = nbrFormatPatternHasColor(pattern);
    var hasParentheses = nbrFormatPatternHasParentheses(pattern);
    //add check for non-empty
    if (hasColor) {
        pattern = getJsNbrFormatterPattern(pattern);

        var options = getOptions(pattern);
        var myVal = $(selector).val();
        if (!isEmpty(myVal)) {
            var myNum = jQuery.parseNumber(myVal, options);
            if (hasParentheses && numberHasParentheses(myVal))
                myNum = -myNum;
            if (!isFieldAttributeSetByOBR(selector[0], 'style.color')) {
                if (myNum < 0) {
                    //alert(myNum);
                    $(selector).css('color', 'red');
                    if (!isEmpty(visibleSpanId)) {
                        $(visibleSpanId).css('color', 'red');
                    }
                } else {
                    $(selector).css('color', 'black');
                    if (!isEmpty(visibleSpanId)) {
                        $(visibleSpanId).css('color', 'black');
                    }
                }
            }
        }
    }
}

function displayDebugInConsole() {
    var message = "";
    for (var i = 0; i < debugMessages.length; i++) {
        message += debugMessages[i] + "\n";
    }
    logDebug(message);
}

function showColoredNegativeNumberInGrid(tbl, commaSeparatedGridFieldIdList, isBold) {
    if (dti.oasis.page.useJqxGrid()) {
        // This function needs to be implemented by setting custom page options by the method handleGetCustomPageOptions for jqxGrid.
        return;
    }

    if (!isBold) {
        isBold = false;
    }
    commaSeparatedGridFieldIdList = "," + commaSeparatedGridFieldIdList + ',';
    for (var i = 1; i < tbl.rows.length; i++) {
        var rowCells = getCellsInRow(tbl.rows[i]);
        for (var j = 0; j < rowCells.length; j++) {
            if (commaSeparatedGridFieldIdList.indexOf("," + rowCells[j].all[0].id.toUpperCase() + ",") >= 0) {
                if (!isEmpty(rowCells[j].all[0].innerText)) {
                    var cellDataContent = rowCells[j].all[0].innerText;
                    if (cellDataContent.charAt(0) == "(" ||
                        cellDataContent.charAt(0) == "-" ||
                        cellDataContent.charAt(cellDataContent.length - 1) == ")") {
                        var className = isBold ? "txtBoldNegativeNumber" : "txtNegativeNumber";
                        addObjectClassName(getObject(rowCells[j].all[0]), className);
                    } else {
                        removeObjectClassName(getObject(rowCells[j].all[0]),"txtBoldNegativeNumber");
                        removeObjectClassName(getObject(rowCells[j].all[0]),"txtNegativeNumber");
                    }
                }
            }
        }
    }
}

function setAllNumbersColorInGrid(tbl) {
    //var beginTime = new Date();
    //debug("Executing setAllNumbersColorInGrid... " + tbl.id);

    //It looks like splitting them works so much faster
    //setNumberColorInGridObtainPattern($('div[dispType="FORMATTEDNUMBER"],input[dispType="FORMATTEDNUMBER"]'));
    var tableSelector = 'table[id="' + tbl.id + '"] ';
    setNumberColorInGridObtainPattern($(tableSelector + 'div[dispType="FORMATTEDNUMBER"]'));
    setNumberColorInGridObtainPattern($(tableSelector + 'input[dispType="FORMATTEDNUMBER"]'));


    //var endTime = new Date();
    //debug("Time spent executing setAllNumbersColorInGrid::" + (endTime.getTime() - beginTime.getTime()) + "ms");
    //displayDebugInConsole();
    //clearDebug();
}

function setAllNumbersColorFields() {
    //debug('setAllNumbersColorFields');
    //var beginTime = new Date();
    var selectorStr = 'input[datatype="NM"][type="text"][name$="' + DISPLAY_FIELD_EXTENTION + '"]';

    $(selectorStr).each(function (index, domEle) {
        // domEle == this
        var pattern = $(domEle).attr("formatPattern");
        var name = $(domEle).attr("name");
        var fieldSelector = 'input[name=' + name + ']';
        setNumberColorInFields($(fieldSelector), pattern);
    });

    var endTime = new Date();
    //debug("Time spent executing trigger: customNumberColorInFields::" + (endTime.getTime() - beginTime.getTime()) + "ms");
    //displayDebugInConsole();
    //clearDebug();
}

//-----------------------------------------------------------------------------
// Find closest parent TR tag
//-----------------------------------------------------------------------------
function findParentTrRow(srcElement) {
    var curElement = srcElement;
    while (curElement && (curElement.tagName.toUpperCase() != "TR")) {
        curElement = curElement.parentElement;
    }
    return (curElement.tagName.toUpperCase() == 'TR' ? curElement : null);
}


//-----------------------------------------------------------------------------
// Get select options description by value
//-----------------------------------------------------------------------------
function getOptionDescription(obj, value) {
    var opts = obj.options;
    var len = opts.length;
    for (var i = 0; i < len; i++) {
        if (opts[i].value == value) {
            return opts[i].text;
        }
    }
    return "";
}

function submitFirstForm() {
    baseOnSubmit(document.forms[0]);
}

function baseOnSubmit(formElement) {
    skipPromptUserIfIsOkToLeavePage();
    formElement.submit();
}

function setWindowLocation(url){
    skipPromptUserIfIsOkToLeavePage();
    url =  dti.csrf.setupCSRFTokenForUrl(url);
    window.location.href = url;
}

function reloadWindowLocation(){
    skipPromptUserIfIsOkToLeavePage();
    window.location.reload();
}

function replaceWindowLocation(url){
    skipPromptUserIfIsOkToLeavePage();
    window.location.replace(url);
}

function baseCloseWindow(){
    skipPromptUserIfIsOkToLeavePage();
    window.close();
}

function italicsInfo(italicsKeyColumn, italicsActionItem, buttonItalicsFlag) {
    this.italicsKeyColumn = italicsKeyColumn;
    this.italicsActionItem = italicsActionItem;
    this.buttonItalicsFlag = buttonItalicsFlag;
}

function getCacheItalicsValue(cacheId) {
    return italicsCacheValueArray.getItem(cacheId);
}

function removeCacheItalicsValue(cacheId) {
    italicsCacheValueArray.removeItem(cacheId);
}

function spliceTheRecordExistsFields() {
    // Loop through any italics items for the page
    if(italicsArrayInPageLevel.length > 0) {
        for (var i = 0; i < italicsArrayInPageLevel.length; i++) {
            var italicsKeyColumn = italicsArrayInPageLevel[i].italicsKeyColumn;
            var italicsActionItem = italicsArrayInPageLevel[i].italicsActionItem;

            if (hasObject(italicsActionItem)) {
                if (isNull(italicsFieldIdList)) {
                    italicsFieldIdList = italicsKeyColumn;
                }
                else {
                    italicsFieldIdList += "," + italicsKeyColumn;
                }
            }
        }
    }
}

function setRecordExistsStyle(italicsArray, oValueList) {
    var hasCachedRecord = true;
    if (italicsArray == null || italicsArray == "") {
        italicsArray = italicsArrayInPageLevel;
        hasCachedRecord = false;
    }
    // Loop through any italics items for the page
    var tempItalicsArray = new Array();
    for (var i = 0; i < italicsArray.length; i++) {
        var italicsKeyColumn = italicsArray[i].italicsKeyColumn;
        var italicsActionItem = italicsArray[i].italicsActionItem;
        if (hasObject(italicsActionItem) && hasObject(italicsKeyColumn)) {
            // Does the column exist in the AJAX response
            var existsB = italicsArray[i].buttonItalicsFlag;
            if (oValueList != null && oValueList.length > 0) {
                existsB = oValueList[0][italicsKeyColumn];
                if (!hasCachedRecord) {
                    tempItalicsArray.push(new italicsInfo(italicsKeyColumn, italicsActionItem, existsB));
                } else {
                    if (typeof existsB != "undefined") {
                        italicsArray[i].buttonItalicsFlag = existsB;
                    }
                }
            } else {
                italicsArray[i].buttonItalicsFlag = existsB;
            }
            if (typeof existsB != "undefined") {
                // Set the column value
                setObjectValue(italicsKeyColumn, existsB);
                // Change the style based on the exists value
                var italicsElement = getObject(italicsActionItem);

                //processing for tab item
                if(italicsElement.tagName.toUpperCase() == "A" &&
                    italicsElement.parentElement.tagName.toUpperCase() == "LI" &&
                    (italicsElement.parentElement.className.indexOf("tab") != -1 ||
                    italicsElement.parentElement.className.indexOf("selectedTab") != -1 ||
                    italicsElement.parentElement.className.indexOf("firstTab") != -1 ||
                    italicsElement.parentElement.className.indexOf("firstSelectedTab") != -1)) {   //Tab Item
                    italicsElement = italicsElement.querySelectorAll("#" + italicsActionItem + ">span")[0];
                }

                if (existsB == "Y") {
                    italicsElement.style.fontStyle = 'italic';
                } else if (existsB == "N") {
                    italicsElement.style.fontStyle = 'normal';
                }
            }
        }
    }
    if (oValueList != null && oValueList.length > 0 && italicsCurrentGridName != "" && !hasCachedRecord) {
        italicsCacheValueArray.setItem(italicsCurrentGridName + getSelectedRow(italicsCurrentGridName), tempItalicsArray);
    }
}

function getElementStyle(element, styleName) {
    var style = $(element).css(styleName);

    if (typeof style == "undefined" || style == null) {
        return "";
    }

    return style;
}


function setCurrentSelectedTab(tabId) {
    var tabMenuDivs = $(".jTabMenuDiv");
    for (var i = 0; i < tabMenuDivs.length; i++) {
        var tabs = $(tabMenuDivs[i]).children("ul")[0].children;
        for (var j = 0; j < tabs.length; j++) {
            // find the current tab and add "selectedTab" class
            var tab = $(tabs[j]);
            if (tab.children()[0].id == tabId) {
                if (!tab.hasClass("selectedTab")) {
                    tab.addClass("selectedTab");
                }
                break;
            }
        }
    }
}

// note: from IE8 standard mode, forms[0].action return full url
function getFormActionAttribute(){
    var strAction = document.forms[0].getAttribute("action");
    return strAction;
}

function clickMenu(menuId) {
    if (menuId != null) {
        var liObject = getObjectById(menuId);
        if (liObject != null) {
            $(liObject).parent().hide();
            var links = $(liObject).children("a");
            if (links.length > 0) {
                dispatchElementEvent(links[0], "click");
//                links.first().click();


            }
        }
    }
}

function getURLColumnName(fieldColumnName) {
    // the fieldColumnName should be all upper case
    var urlColumnName = '';
    var linkObject = getSingleObject('URL_C' + fieldColumnName);
    if (linkObject && linkObject.dataFld) {
        urlColumnName = linkObject.dataFld;
    }
    return urlColumnName;
}

function openSourceWindow(actionUrl, openInNewWindow, fullWindow) {
    if (typeof openInNewWindow == "undefined") {
        openInNewWindow = true;
    }
    if (typeof fullWindow == "undefined") {
        fullWindow = false;
    }

    if (openInNewWindow) {
        if (fullWindow) {
            window.open(actionUrl);
        } else {
            var mainwin = window.open(actionUrl, 'CM', 'width=1000,height=650,resizable=yes,scrollbars=yes,status=yes,top=5,left=5');
            mainwin.focus();
        }
    } else {
        doMenuItem(null, actionUrl);
    }
}

// FIX for IE8: filter() was added in IE9
// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter
if (!Array.prototype.filter)
{
    Array.prototype.filter = function(fun /*, thisp */)
    {
        "use strict";

        if (this === void 0 || this === null)
            throw new TypeError();

        var t = Object(this);
        var len = t.length >>> 0;
        if (typeof fun !== "function")
            throw new TypeError();

        var res = [];
        var thisp = arguments[1];
        for (var i = 0; i < len; i++)
        {
            if (i in t)
            {
                var val = t[i]; // in case fun mutates this
                if (fun.call(thisp, val, i, t))
                    res.push(val);
            }
        }

        return res;
    };
}

function getDataSrc(field) {
    return field.dataSrc;
}

function getDataField(field) {
    return field.dataFld;
}

function getElementStyle(element, styleName) {
    var style = $(element).css(styleName);

    if (typeof style == "undefined" || style == null) {
        return "";
    }

    return style;
}

/**
 * This method will be changed to hide/show element by adding/removing css class "dti-hide" in the future.
 * The visible display sytle for the element. If not specified, use "block"  as default.
 * @param element
 * @param isHidden
 * @param visibleDisplayStyle
 */
function hideShowElementByClassName(element, isHidden, visibleDisplayStyle) {
    if (isHidden) {
        element.setAttribute("previousDisplayValue", $(element).css("display"));
        element.style.display = "none";
    } else {
        if (typeof visibleDisplayStyle == "undefined") {
            var previousDisplayValue = element.getAttribute("previousDisplayValue");

            if (isStringValue(previousDisplayValue) && previousDisplayValue != "none") {
                visibleDisplayStyle = previousDisplayValue;
            } else {
                visibleDisplayStyle = "block";
            }
        }

        element.style.display = visibleDisplayStyle;
    }
}

function hideShowElementAsBlockByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "block");
}

function hideShowElementAsInlineByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "inline");
}

function hideShowElementAsInlineBlockByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "inline-block");
}

function hideShowElementAsTableByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "table");
}

function hideShowElementAsTableRowByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "table-row");
}

function hideShowElementAsTableCellByClassName(element, isHidden) {
    hideShowElementByClassName(element, isHidden, "table-cell");
}

/**
 * This method is to check whether the element is hidden.
 * @param element
 * @returns {boolean}
 */
function isElementHidden(element) {
    var isHidden = false;
    if(window["useJqxGrid"]){
        isHidden = $(element).hasClass('dti-hide');
        if(!isHidden && element.parentNode)
            isHidden = isElementHidden(element.parentNode);
    } else {
        isHidden = (element.style.display == "none");
    }
    return isHidden;
}

function isIE8Mode() {
    var userAgent = navigator.userAgent;

    return (userAgent.indexOf("compatible;") > -1 && userAgent.indexOf("MSIE 8.0;") > -1);
}

function isFieldMasked (field) {
    var hasMasked = false;
    if (hasObject(field)) {
        if (field.type != "hidden" && !isFieldHidden(field)) {
            hasMasked = eval("typeof MASK_" + field.name + " != 'undefined' && MASK_" + field.name + ";");
            if (hasMasked) {
                return true;
            }
        }
    }
    return false;
}

//Temp solution before the generic issue is implemented
function transformTypedChar(charStr) {
    return charStr.toUpperCase();
}

/**
 * The function is for selecting a field after validation failed in onchange event.
 *
 * In Chrome the order of onblur and onfocus event is different with in IE if we call field.select() in onChange event.
 * The baseOnFocus function will reset the attribute ATTRIBUTE_IS_ON_CHANGE_FIRED. In this case, baseOnBlur will trigger
 * onChange event of the field again. So the validation message may be displayed twice.
 *
 * To select the field in setTimeout function will make sure the field to be selected after onblur event.
 *
 * @param field
 */
function postChangeReselectField(field) {
    window.setTimeout(function () {
        field.select();
    }, 0);
}

/**
 * This object will have all the necessary information on the token for CSRF.
 */
if (typeof dti == "undefined") {
    dti = {};
}
if (typeof dti.csrf == "undefined") {
    dti.csrf = (function() {
        var htmlToken = "org.apache.struts.taglib.html.TOKEN";
        return {
            getCSRFTokenLabel: function () {
                return htmlToken;
            },
            getPageCSRFTokenValue: function(){
                var value = "";
                if (getObject(this.getCSRFTokenLabel())) {
                    value = getObjectValue(this.getCSRFTokenLabel());
                }
                return value;
            },
            setupCSRFTokenForUrl: function(url){
                if (url.indexOf(this.getCSRFTokenLabel())== -1) {
                    url+= (url.indexOf('?') > -1) ? "&" : "?";

                    // add token id.
                    if (getObject(this.getCSRFTokenLabel())) {
                        url += this.getCSRFTokenLabel() + "=" + this.getPageCSRFTokenValue();
                    }
                }
                return url;
            },
            updatePageToken: function(token) {
                if (isDefined(token) && token != "") {
                    if (getObject(this.getCSRFTokenLabel())) {
                        getObject(this.getCSRFTokenLabel()).value = token;
                    }
                }
            }
        }
    })()
}


/**
 * This object will convert string to upper case or lower case.
 */
if (typeof dti == "undefined") {
    dti = {};
}
if (typeof dti.inpututils  == "undefined") {
    dti.inpututils  = (function() {
        var windowEvent;
        return {
            replaceTypedChar: function (evt, func) {
                var ev = evt || window.event;
                var keyCode = ev.which || ev.keyCode;

                if (keyCode) {
                    var field = ev.srcElement || ev.target;
                    var charStr = String.fromCharCode(keyCode);
                    var transformedChar = func(charStr);
                    if (transformedChar != charStr) {
                        var sel = this.getInputSelection(field), val = field.value;
                        field.value = val.slice(0, sel.start) + transformedChar + val.slice(sel.end);
                        // Move the caret
                        this.setInputSelection(field, sel.start + 1, sel.start + 1)

                        if (ev.stopPropagation) {
                            ev.stopPropagation();

                        } else{
                            ev.cancelBubble = true;
                            ev.returnValue = false;
                        }
                        if (ev.preventDefault) {
                            ev.preventDefault();
                        }

                        return false;
                    }
                }
            },
            getInputSelection: function (el) {
                var start = 0, end = 0, normalizedValue, range,
                    textInputRange, len, endRange;

                if (typeof el != "undefined" && typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
                    start = el.selectionStart;
                    end = el.selectionEnd;
                } else {
                    range = document.selection.createRange();

                    if (range && range.parentElement() == el) {
                        len = el.value.length;
                        normalizedValue = el.value.replace(/\r\n/g, "\n");

                        // Create a working TextRange that lives only in the input
                        textInputRange = el.createTextRange();
                        textInputRange.moveToBookmark(range.getBookmark());

                        // Check if the start and end of the selection are at the very end
                        // of the input, since moveStart/moveEnd doesn't return what we want
                        // in those cases
                        endRange = el.createTextRange();
                        endRange.collapse(false);

                        if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
                            start = end = len;
                        } else {
                            start = -textInputRange.moveStart("character", -len);
                            start += normalizedValue.slice(0, start).split("\n").length - 1;

                            if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
                                end = len;
                            } else {
                                end = -textInputRange.moveEnd("character", -len);
                                end += normalizedValue.slice(0, end).split("\n").length - 1;
                            }
                        }
                    }
                }

                return {
                    start: start,
                    end: end
                };
            },
            offsetToRangeCharacterMove: function (el, offset) {
                return offset - (el.value.slice(0, offset).split("\r\n").length - 1);
            },
            setInputSelection: function (el, startOffset, endOffset) {
                el.focus();
                if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
                    el.selectionStart = startOffset;
                    el.selectionEnd = endOffset;
                } else {
                    var range = el.createTextRange();
                    var startCharMove = this.offsetToRangeCharacterMove(el, startOffset);
                    range.collapse(true);
                    if (startOffset == endOffset) {
                        range.move("character", startCharMove);
                    } else {
                        range.moveEnd("character", this.offsetToRangeCharacterMove(el, endOffset));
                        range.moveStart("character", startCharMove);
                    }
                    range.select();
                }
            },
            toUpperCase: function (charStr) {
                return charStr.toUpperCase();
            },
            toLowerCase: function (charStr) {
                return charStr.toLowerCase();
            },
            setWindowEvent: function (evt) {
                this.windowEvent = evt;
            },
            getWindowEvent: function() {
                return this.windowEvent;
            },
            getFieldType: function(field) {
                if(field)
                    return field.type;
                return "";
            },
            isFieldOfType: function(field, type) {
                if(field && type)
                    return field.type.toLowerCase() === type.toLowerCase();
                return false;
            },
            isTextField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_TEXT);
                return false;
            },
            isTextareaField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_TEXTAREA);
                return false;
            },
            isSingleSelectField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_SINGLESELECT);
                return false;
            },
            isMultiSelectField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_MULTISELECT);
                return false;
            },
            isRadioField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_RADIO);
                return false;
            },
            isCheckBoxField: function(field){
                if(field)
                    return dti.inpututils.isFieldOfType(field, FIELDTYPE_CHECKBOX);
                return false;
            }
        }
    })()
}

/**
 * This object is used for grid utility.
 */
if (typeof dti == "undefined") {
    dti = {};
}
if (typeof dti.gridutils  == "undefined") {
    dti.gridutils = (function () {
        var DEFAULT_BROWSER_WIDTH = 900;
        var DEFAULT_SCROLL_BAR_WIDTH = 40;
        return {
            getDocumentWidthForGrid: function () {
                var documentWdith = this.getDocumentWindowWidth();
                return documentWdith;
            },
            setGridWidth: function (tableDiv) {
                var documentWidth = this.getDocumentWidthForGrid();
                var divHolder = tableDiv.parent();
                var divHolderWidth = divHolder.width();

                if (documentWidth > divHolderWidth) {
                    documentWidth = (divHolderWidth - DEFAULT_SCROLL_BAR_WIDTH);
                }
                tableDiv.width(documentWidth);
            },
            setAllGridWidth: function () {
                var tables = $("div.divGrid table.clsGrid");
                for (var i = 0; i < tables.length; i++) {
                    var gridId = tables[i].id;
                    var tableDiv = $(getDivForGrid(gridId));

                    this.setGridWidth(tableDiv);
                }
            },
            refreshAllGridWidth: function () {
                return this.setAllGridWidth();
            },
            getDocumentWindowWidth: function () {
                var documentWidth = window.document.documentElement.clientWidth + window.document.body.scrollLeft;

                if ((window.opener && !window.opener.closed) || window.frameElement) {
                    //it is a div popup or a window opener
                    return documentWidth - DEFAULT_SCROLL_BAR_WIDTH;
                } else {
                    if (typeof documentWidth == "undefined") {
                        documentWidth = DEFAULT_BROWSER_WIDTH;
                    } else {
                        documentWidth -= DEFAULT_SCROLL_BAR_WIDTH;
                        if (documentWidth < DEFAULT_BROWSER_WIDTH) {
                            documentWidth = DEFAULT_BROWSER_WIDTH;
                        }
                    }
                    return documentWidth;
                }
                return DEFAULT_BROWSER_WIDTH;
            }
        }
    })()
}
