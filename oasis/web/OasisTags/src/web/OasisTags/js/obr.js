/**
 Oasis Business Rule functions (obr.js)

 Every OBR result will always contain 3 result information
 1. enforcingFieldList - List of fields that are responsible for the obr result. ie. List of fields participating in
    OBR rule condition
 2. enforcedFieldList - List of fields whose attribute are getting changed
 3. attributeChangeList - List of attributes with changed attribute value per enforcedField.

 OBRAttribute and OBRStyle function object (ie. classes) will hold list of attribute changes per enforced field.
 OBRCollection function object (ie. class) will hold all list of enforcing and enforced fields along with attribute changes
 per enforced field.

 The invoker for OBR must always create a new instance of OBRCollection and make calls to setOBREnforcingFieldList and
 setOBREnforcedResult, followed by call to fire OBR and pass the OBRCollection instance.

 Purpose: Functions to help enforce generated Oasis Business Rule result.
 Created By : MLM
 Created On : 04/11/2011

 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 10/24/2011       Leo         Fix the bug on Claim General page and Claim Participant page.
 11/23/2012       Parker      137533 - Fix panel hidden logic to ensure the required validation when panel hidden
 11/23/2012       Parker      139429 - 1. Create a OBR rule to change style on grid field on claim claimant page. The rule doesn't check field value in when section. The style is not changed.
                                       2. OBR rule to collapse panel doesn't work.
 12/28/2012       Parker      139202 - Fix OBR to set value of a dependant LOV when option is not available until after reloading the dependant LOV.
 12/28/2012       Parker      140834 - Create a new javascript function setObjectValue for OBR to set values.It should support radio, checkbox and multiple select.
 08/16/2013       mlm         147469 - Refactor to consider correct OBR attribute value.
 10/18/2015       Parker      166906 - The OBR rule on new inserted row doesn't work.Replace the "-" by using the "_" to fix this problem.
 04/27/2017       mlm         185029 - Refactored to handle hide/show and enable/disable for multiSelectText and single checkbox fields.
 06/20/2017       cesar       186229 - Modified OBRSetAttribute() function to check for classname "tab" to hide tab. Jquery appends "fNiv" to class name.
 08/15/2017       dpang       178733 - Modified getGridColumn function to get gridColumn for editable dropdown grid column.
 09/19/2018       dpang       195835 - Modified setGridRowEnforcedResult() for grid replacement.
 09/19/2018       jdingle     193748 - Skip fireObrOnAdd when there is no active OBR.
 -----------------------------------------------------------------------------
 **/

/*
 Java Script concept used in OBR.js

 In Java script every single variable or function is treated as object with object key as the variable name or function name
 and object value as the variable value or function content. For function content, image object containing object collection as
 object value.

 Consider OBRAttribute, OBRStyle and OBRCollection as Java classes with private and public members defined using var and this keywords.
 Any variable declaration using var or function keyword with-in these functions should be considered as private member variables or functions.
 Any function declaration or reference with "this" keyword should be considered as public member object or functions.

 In Java script, the following statement will dynamically create a member object with it's corresponding defined value -

      eg. this[variable_name] = variable_value.

 If a need arises to reference the member object dynamically using variable name, it can be acheived using the following
 statement -

      eg. this[variable_name]

 Another technique used is to convert a string into array list. This is acheived by added "[" and "]" surrounding the variable value.

      eg. var myArrayList = [variable_value]

 In order to remove the user defined attribute or property definition using variable name for the object, use delete  Java Script statement.

      eg. delete this[variable_name] will remove the attribute or property defined in the variable_name variable for the object.
          delete this.Style["userDefinedProperty"] will remove "userDefinedProperty" property from Style object.

 Function hasOwnProperty() is used to determine whether the property is an user defined property or java script defined system property
 belonging to the referenced object.

      eg. this.Style["userDefinedProperty"] = userDefinedPropertyValue.

 The above statement will create a new property/attribute/variable named "userDefinedProperty" and attaches it to the Style object.

      eg. this.Style.hasOwnProperty("userDefinedProperty") will return false -- meaning this is a user defined property.
      eg. this.Style.hasOwnProperty("height") will return true -- meaning this is a java script system property.
 */

var OBR_GridIdInRenderingOrder = "";
var OBR_EnforcedResult  = "OBREnforcedResult";
var OBR_EnforcingUpdateIndicator  = "OBREnforcingUpdateIndicator";
var OBR_EnforcingFieldList  = "OBREnforcingFieldList";
var OBR_ConsequenceFieldList  = "OBRConsequenceFieldList";
var OBR_AllAccessedFieldList  = "OBRAllAccessedFieldList";
var OBR_hasRuleForSave  = "OBRhasRuleForSave";
var OBR_GridIdList  = "OBRGridIdList";
var isOKToProceed = false;
var retryOBRSetValues = new Array();
var executingBeforeFireAjax = false;

/*
  An object that contains all OBR attributes that needs to be changed.
 */
function OBRAttribute() {
    this.style = new OBRStyle();
    var m_len = 0;
    var m_attributes = "";

    /* Private Functions - Begin */
    {
        function increaseLength() {
            m_len++;
        }

        function decreaseLength() {
            m_len--;
        }

        function getLength() {
            return m_len;
        }

        function getAttributes() {
            return m_attributes.substring(0, m_attributes.length - 1);
        }
    }
    /* Private Functions - End */

    this.hasAttribute = function(attrName) {
        if (typeof(this[attrName]) != 'undefined' && typeof(this[attrName]) != 'function') {
            if (attrName == 'style') {
                return (this.style.getLength() > 0);
            } else {
                return this.hasOwnProperty(attrName);
            }
        } else {
            return false;
        }
    }

    this.getLength = function() {
        var currentLength = getLength();
        return currentLength;
    }

    this.getAllAttributeList = function() {
        var attributes = this.getAttributeList(true);
        var styleAttributes = this.getStyleAttributeList(true);
        var allAttributes = attributes.concat(styleAttributes);
        return allAttributes;
    }

    this.getAttribute = function(attrName) {
        return (this.hasAttribute(attrName) ? this[attrName] : null);
    }

    this.getAttributes = function() {
        return getAttributes();
    }

    this.getAttributeList = function(ignoreStyleAttr) {
        var i = 0;
        var attributes = new Array();
        for (var attr in this) {
            if (this.hasAttribute(attr)) {
                if (!(attr == 'style' && ignoreStyleAttr) || attr != 'style')
                    attributes[i++] = attr;
            }
        }
        return attributes;
    }

    this.setAttribute = function (attrName) {
        var attrList = [attrName];
        this.setAttributeList(attrList);
    }

    this.setAttributeList = function (attrList) {
        for (var attr in attrList) {
            if (typeof(attrList[attr]) == 'string') {
                if (!this[attr]) {
                    increaseLength();
                    m_attributes += attr + ',';
                }
                this[attr] = attrList[attr];
            }
        }
    }

    this.removeAttribute = function (attrName) {
        var attrList = [attrName];
        this.removeAttributeList(attrList);
    }

    this.removeAttributeList = function (attrList) {
        for (var attr in attrList) {
            if (typeof(attrList[attr]) == 'string') {
                if (this[attrList[attr]]) {
                    decreaseLength();
                    m_attributes = (',' + m_attributes + ',').replace(',' + attr + ',', '');
                    delete this[attrList[attr]];
                }
            }
        }
    }

    this.getStyleAttribute = function(styleAttrName) {
        return this.style.getAttribute(styleAttrName);
    }

    this.getStyleAttributes = function() {
        return this.style.getAttributes();
    }

    this.getStyleAttributeList = function(prefixStyleWord) {
        return this.style.getAttributeList(prefixStyleWord);
    }

    this.setStyleAttribute = function (styleAttrName) {
        var attrList = [styleAttrName];
        this.setStyleAttributeList(attrList);
    }

    this.setStyleAttributeList = function (attrList) {
        if (this.style.getLength() == 0) {
            increaseLength();
        }
        this.style.setAttributeList(attrList);
    }

    this.removeStyleAttribute = function (styleAttrName) {
        var attrList = [styleAttrName];
        this.removeStyleAttributeList(attrList);
    }

    this.removeStyleAttributeList = function (attrList) {
        this.style.removeAttributeList(attrList);
        if (this.style.getLength() == 0) {
            decreaseLength();
        }
    }

    this.toString = function() {
        var attrStr = "";
        for (var attr in this) {
            if (typeof(this[attr]) == 'string') {
                if (this.hasAttribute(attr)) {
                    attrStr += attr + "=" + this[attr].toString() + ",";
                }
            }
        }
        if (attrStr) {
            attrStr = attrStr.substring(0, attrStr.length - 1);
        }
        return attrStr;
    }
};

/*
  An object that contains all OBR style attributes that needs to be changed.
 */
function OBRStyle() {
    var m_len = 0;
    var m_attributes = "";

    /* Private Functions - Begin */
    {
        function increaseLength() {
            m_len++;
        }

        function decreaseLength() {
            m_len--;
        }

        function getLength() {
            return m_len;
        }

        function getAttributes() {
            return m_attributes.substring(0, m_attributes.length - 1);
        }
    }
    /* Private Functions - End */

    this.hasAttribute = function(attrName) {
        if (typeof(this[attrName]) != 'undefined' && typeof(this[attrName]) != 'function') {
            return this.hasOwnProperty(attrName);
        } else {
            return false;
        }
    }

    this.getLength = function() {
        var currentLength = getLength();
        return currentLength;
    }

    this.getAttribute = function(attrName) {
        return (this.hasAttribute(attrName) ? this[attrName] : null);
    }

    this.getAttributes = function() {
        return getAttributes();
    }

    this.getAttributeList = function(prefixStyleWord) {
        var i = 0;
        var attributes = new Array();
        for (var attr in this) {
            if (typeof(this[attr]) == 'string') {
                if (this.hasAttribute(attr)) {
                    attributes[i++] = (prefixStyleWord ? "style." : "") + attr;
                }
            }
        }
        return attributes;
    }

    this.setAttribute = function (attr) {
        var attrList = [attr];
        this.setAttributeList(attrList);
    }

    this.setAttributeList = function (attrList) {
        for (var attr in attrList) {
            if (typeof(attrList[attr]) == 'string') {
                if (!this[attr]) {
                    increaseLength();
                    m_attributes += attr + ',';
                }
                this[attr] = attrList[attr];
            }
        }
    }

    this.removeAttribute = function (attr) {
        var attrList = [attr];
        this.removeAttributeList(attrList);
    }


    this.removeAttributeList = function (attrList) {
        for (var attr in attrList) {
            if (typeof(attrList[attr]) == 'string') {
                if (this[attrList[attr]]) {
                    decreaseLength();
                    m_attributes = (',' + m_attributes + ',').replace(',' + attr + ',', '');
                    delete this[attrList[attr]];
                }
            }
        }
    }

    this.toString = function() {
        var attrStr = "";
        for (var attr in this) {
            if (typeof(this[attr]) == 'string') {
                if (this.hasAttribute(attr)) {
                    attrStr += attr + ":" + this[attr].toString() + ";";
                }
            }
        }
        if (attrStr) {
            attrStr = "{" + attrStr.substring(0, attrStr.length - 1) + "}";
        }
        return attrStr;
    }
};

/*
  An object that contains all OBR result.
 */

function OBRCollection() {

    var m_enforcingFieldCollection = new Object();
    var m_enforcingFieldIds = "";
    var m_enforcingFieldCount = 0;

    var m_enforcedFieldCollection = new Object();
    var m_enforcedFieldIds = "";
    var m_enforcedFieldAttrCollection = new Object();
    var m_enforcedFieldCount = 0;

    /* Private Functions - Begin */
    {
        function OBRGetPanelField (fieldId) {
/*
            var field = getObject(fieldId);
            if (field) {
                while (field.parentElement) {
                    field  = field.parentElement;
                    if (field.id.toUpperCase() == "PANEL") {
                        gridPanel = field ;
                        break;
                    }
                }
            }
*/
            var panelTitle = $(getObject(fieldId)).closest("div[class='panel']").find("span[class='panelTitle']")[0];
            if (!panelTitle) {
                //This field could be a grid column.
                panelTitle = $('div[id="' + fieldId + '"]').closest("div[class='panel']").find("span[class='panelTitle']")[0];
            }
            var field = $(panelTitle).closest("a");
            if (!isUndefined(field)) {
                if (field.length > 0) {
                    field = field [0];
                }
            }
            return field;
        }

        function OBRGetTableColumnHeaderField (gridFieldId) {
            var gridFldId = gridFieldId.toUpperCase();
            if (gridFldId.substring(gridFldId.length-3) == "_GH") {
                gridFldId = gridFldId.substring(0, gridFldId.length-3);
            }
            var field = getObject("HC" + gridFldId);
            if (isUndefined(field)) {
                field = getObject("HC" + gridFldId + "LOVLABEL");
                if (isUndefined(field)) {
                    field = getObject("HC" + gridFldId + "_DISP_ONLY");
                }
            }
            if (!isUndefined(field)) {
                if (field.length > 0) {
                    field = field [0];
                }
            }
            return field;
        }

        function OBRGetField(fieldId) {
            var isMenuItem = false;
            var isOption = false;
            var isPageField = false;
            var isLayerField = false;
            var isGridField = false;
            var isTableHeaderField = false;
            var isPanelField = false;
            var isGridDetailPanelField = false;

            var fldId = fieldId;
            var layerId = "";
            var gridId = "";

            var fieldCollection ;
            var field;

            if (hasObject(fldId)) {
                // Direct specification of field id and the object is located.
                // The field can be an actual oasis field or grid or layer
                fieldCollection = getObject(fldId);
                if (fieldCollection.length > 1) {
                    field = fieldCollection[0];
                    if (field.tagName) {
                        // SELECT element is treated as array and the first child element is sent - ie. OPTION element.
                        if (field.tagName.toLowerCase() == "option") {
                            field = field.parentElement;
                        }
                    }
                } else {
                    field = fieldCollection;
                }
                if (field.tagName.toUpperCase()=="TR") {
                    isLayerField = true;
                } else if (field.tagName.toUpperCase()=="LI" || field.parentElement.tagName.toUpperCase()=="LI") {
                    // getObject() for Global navigation always locates <li>.
                    // But for tab menus, it will locate <A>, whose parent is always <LI>.
                    isMenuItem = true;
                } else {
                    if (field.dataSrc) {
                        isGridField = true;
                        gridId = field.dataSrc.substring(1).substring(0, field.dataSrc.substring(1).length-1);
                    } else {
                        isPageField = true;
                    }
                }
            } else if (hasObject("OPTION_" + fldId)) {
                field = getObject("OPTION_" + fldId);
                isOption = true;
            } else {
                //resolve field id to locate appropriate field.

                field = getObject(fldId.substring(0, fldId.indexOf(".")));
                var fldIndicator = "";
                if (fldId.indexOf(".") > 0) {
                    fldIndicator =  fldId.substring(fldId.indexOf(".")+1);
                    fldId = fldId.substring(0, fldId.indexOf("."));
                    if (field == null || fldIndicator.toUpperCase()=="GRIDCOLUMN") {
                        //If the reference is for the grid column, always get the grid header field because the resolved
                        //field could be form field.
                        field = OBRGetTableColumnHeaderField(fldId);
                        isGridField = true;
                    }
                    if(fldIndicator.toUpperCase()=="GRIDCOLUMN") {
                        isTableHeaderField = true;
                    } else if(fldIndicator.toUpperCase()=="GRIDCOLUMN.PANEL") {
                        isTableHeaderField = true;
                        isPanelField = true;
                    } else if(fldIndicator.toUpperCase()=="PANEL") {
                        isGridDetailPanelField = isDefined(field.dataSrc);
                        isPanelField = true;
                    }
                }
                if (field == null) {
                    return undefined;
                } else if (field.dataSrc || field.dataFld) {
                    isGridField = true;
                    if (field.dataSrc) {    //Grid Header Column will not have datasrc attribute set.
                        gridId = field.dataSrc.substring(1).substring(0, field.dataSrc.substring(1).length-1);
                    } else {
                        gridId = $(field).closest('table').attr("id");
                    }
                } else if (field.tagName.toUpperCase()=="TR") {
                    isLayerField = true;
                } else {
                    isPageField = true;
                }
            }

            if (isPanelField) {
                if (isGridDetailPanelField) {
                    /*var gridDetailDiv = eval(getTableProperty(getTableForGrid(gridId), "gridDetailDivId"));
                    field = $(gridDetailDiv).find('span[class="panelTitle"]').parent();
                    if (!isUndefined(field)) {
                        if (field.length > 0) {
                            field = field [0];
                        }
                    }*/
                    field = OBRGetPanelField(fldId);
                } else if (isTableHeaderField) {
                    field = OBRGetPanelField("DIV_" + gridId)
                } else {
                    field = OBRGetPanelField(fldId);
                }
            } else if (isTableHeaderField) {
                // field already got resolved to grid column element. No further action is required.
            } else if (isLayerField) {
                //field already got resolved to the <TR>. No further action is required.
            } else if (isOption) {
                //field already got resolved to the <OPTION>. No further action is required.
            } else if (fldId) {
                var containerClass = "";
                var containerId = fldId + FIELD_VALUE_CONTAINER_SUFFIX;
                if (hasObject(containerId)) {
                    var container = $("#" + containerId);
                    containerClass = container.attr("class")
                } else {
                    var parentTD = getObject(fldId).parentElement;
                    while (parentTD.tagName.toUpperCase() != "TD") {
                        parentTD = parentTD.parentElement;
                    }
                    containerClass =  parentTD.className;
                }

                var isReadonlyField = isFieldReadonly(field);
                var isVisibleField = false;
/*
                if (containerClass.length >= "readonlyField".length ) {
                    //starts with "readonly" and ends with "Field" (logic required to handle header field classes)
                    if (containerClass.substring(0, 8) == "readonly" && containerClass.substring(containerClass.length-5) == "Field") {
                        isReadonlyField = true;
                    }
                }
*/
                if ( (containerClass).indexOf("hiddenField") == -1) {
                    isVisibleField = true;
                }
                // Irrespective whether the field is visible or hidden, always locate the actual input OASIS field.
                field = getObject(fldId);

                if (isReadonlyField) {
                    field = getField(fldId, true);
                } else {
                    if (!isMenuItem) {
                        field = getField(fldId, false);
                    }
                }
            }
            return field;
        }

        function getField (fieldId, isGetReadonlyField) {
            var fldId = fieldId;
            // Irrespective of what fieldId has been sent in (either readonly field id or editable field id),
            // always get to the editable field, then proceed with the request.
            var readonlyFieldSuffix = new Array("ROSPAN1", "ROSPAN", "LOVLABELSPAN", "_DISP_ONLY");
            for (var i=0; i<readonlyFieldSuffix.length; i++) {
                fldId = fldId.replace(readonlyFieldSuffix[i], "");
            }
            var field = getObject(fldId);
            // "_DISP_ONLY" fields can be defined only for grid column as DIV html element.
            // There is no underlying editable field for such cases.
            if (field) {
                //Some of the action items are defined twice on the page.
                field = (field.length ? (field.tagName ? (field.tagName.toLowerCase()=="select" ? field : field[0]) : field[0]) : field);
            }

            if (isGetReadonlyField) {
                if (field != null) {
                    // For read-only fields, the actual input OASIS field is hidden, while ROSPAN field is visible.
                    // Locate the appropriate ROSPAN OASIS field.
                    field = getObject(fldId + "ROSPAN");
                    if (!field) {
                        // Unable to locate the field, assume the configured field id is correct and get the appropriate field.
                        field = getObject(fldId);
                    } else if (field.tagName == "A") {
                        // It's a read only anchor field - actual label is stored within the SPAN. Locate the SPAN field.
                        field = getObject(fldId + "ROSPAN1");
                    } else if (field.style.display.toLowerCase() == "none" || $(field).hasClass("dti-hide")) {
                        // It's a read only LOV field. The ROSPAN field contains only field code value - which is made hidden,
                        // while the LOVLABELSPAN contains the field text value - which is visible. Locate the appropriate
                        // LOVLABELSPAN field.
                        field = getObject(fldId + "LOVLABELSPAN");
                        // If LOVLABELSPAN cannot be located, then ROSPAN is the correct field as the field is in readOnly mode.
                        if (!field) {
                            field = getObject(fldId + "ROSPAN");
                        }
                    }
                }
            } else {
                var formattedField = getObject(fldId + "_DISP_ONLY");
                if (formattedField) {
                    if (formattedField.length) {
                        formattedField = formattedField[0];
                    }
                    if (formattedField.tagName == "DIV") {
                        // field is defined for grid column. It's not editable.
                        field = undefined;
                    } else {
                        field = formattedField;
                    }
                } else if (field) {
                    if (field.tagName) {
                        var checkHidden = (field.style.display.toLowerCase() == "none");
                        if(window["useJqxGrid"])
                            checkHidden=$(field).hasClass("dti-hide");
                        if (field.tagName.toLowerCase() == "select" && checkHidden) {
                            var multiSelectField = getObject(fldId + "MultiSelectText");
                            if (multiSelectField) {
                                // It's a multi-select field.
                                field = multiSelectField
                            }
                        }
                    }
                }
            }

            if (field) {
                field = (field.length ? (field.tagName ? (field.tagName.toLowerCase()=="select" ? field : field[0]) : field[0]) : field);
            }

            return field;
        }

        function setEnforcingField(fieldId) {
            if (!m_enforcingFieldCollection[fieldId]) {
                var field = OBRGetField(fieldId);
                // add fields that has been successfully resolved in the page.
                // eg. OBR configuration may set to hide a grid column, which do not exists in the rendered grid.
                if (field) {
                    m_enforcingFieldCollection[fieldId] = field;
                    m_enforcingFieldIds += fieldId + ",";
                    m_enforcingFieldCount ++;
                }
            }
        }

        function getEnforcingFields() {
            return m_enforcingFieldIds.substring(0, m_enforcingFieldIds.length - 1);
        }

        function getEnforcingFieldList() {
            var fieldList = [];
            var i = 0;
            for (var fld in m_enforcingFieldCollection) {
                fieldList[i] = fld;
                i++;
            }
            return fieldList;
        }

        function getEnforcingField(fieldId) {
            var enforcingFieldObj;
            if (m_enforcingFieldCollection[fieldId]) {
                enforcingFieldObj = m_enforcingFieldCollection[fieldId];
            }
            return enforcingFieldObj;
        }

        function removeEnforcingField(fieldId) {
            if (!m_enforcingFieldCollection[fieldId]) {
                m_enforcingFieldIds = (',' + m_enforcingFieldIds + ',').replace(',' + fieldId + ',', '');
                delete m_enforcingFieldCollection[fieldId];
                m_enforcingFieldCount --;
            }
        }

        function getEnforcingFieldCount() {
            return m_enforcingFieldCount;
        }

        function setEnforcedField(fieldId) {
            if (!m_enforcedFieldCollection[fieldId]) {
                var field = OBRGetField(fieldId);
                // add fields that has been successfully resolved in the page.
                // eg. OBR configuration may set to hide a grid column, which do not exists in the rendered grid.
                if (field) {
                    m_enforcedFieldCollection[fieldId] = field;
                    m_enforcedFieldIds += fieldId + ",";
                    m_enforcedFieldAttrCollection[fieldId] = new OBRAttribute();
                    m_enforcedFieldCount ++;
                }
            }
        }

        function getEnforcedFields() {
            return m_enforcedFieldIds.substring(0, m_enforcedFieldIds.length - 1);
        }

        function getEnforcedFieldList() {
            var fieldList = [];
            var i = 0;
            for (var fld in m_enforcedFieldCollection) {
                if (typeof(fld) == 'string') {
                    fieldList[i] = fld;
                    i++;
                }
            }
            return fieldList;
        }

        function getEnforcedField(fieldId) {
            var enforcedFieldObj;
            if (m_enforcedFieldCollection[fieldId]) {
                enforcedFieldObj = m_enforcedFieldCollection[fieldId];
            }
            return enforcedFieldObj;
        }

        function getEnforcedFieldAttributeCollection(fieldId) {
            var enforcedFieldAttrInfo;
            if (m_enforcedFieldAttrCollection[fieldId]) {
                enforcedFieldAttrInfo = m_enforcedFieldAttrCollection[fieldId];
            } else {
                enforcedFieldAttrInfo = new OBRAttribute();
            }
            return enforcedFieldAttrInfo;
        }

        function removeEnforcedField(fieldId) {
            if (!m_enforcedFieldCollection[fieldId]) {
                m_enforcingFieldIds = (',' + m_enforcingFieldIds + ',').replace(',' + fieldId + ',', '');
                delete m_enforcedFieldCollection[fieldId];
                delete m_enforcedFieldAttrCollection[fieldId];
                m_enforcedFieldCount --;
            }
        }

        function getEnforcedFieldCount() {
            return m_enforcedFieldCount;
        }
    }
    /* Private Functions - End */

    this.hasEnforcingField = function (fieldId) {
        return ((',' + getEnforcingFields() + ',').indexOf(',' + fieldId + ',') != -1);
    }

    this.hasEnforcedField = function (fieldId) {
        return ((',' + getEnforcedFields() + ',').indexOf(',' + fieldId + ',') != -1);
    }

    this.setEnforcingFieldList = function (enforcingFieldList) {
        for (var fld in enforcingFieldList) {
            if (typeof(enforcingFieldList[fld]) == 'string') {
                setEnforcingField(enforcingFieldList[fld]);
            }
        }
    }

    this.setEnforcedFieldList = function (enforcedFieldList) {
        for (var fld in enforcedFieldList) {
            if (typeof(enforcedFieldList[fld]) == 'string') {
                setEnforcedField(enforcedFieldList[fld]);
            }
        }
    }

    this.setEnforcedFieldAttributes = function (fieldId, attrList) {
        if (!this.hasEnforcedField(fieldId)) {
            this.setEnforcedFieldList([fieldId]);
        }
        // Honor the OBR configuration for the resolved fields only.
        // eg. OBR configuration may set to hide a grid column, which do not exists in the rendered grid.
        if (this.hasEnforcedField(fieldId)) {
            var attrCollection = getEnforcedFieldAttributeCollection(fieldId);

            if (attrCollection) {
                var attributeChanges = new Array();
                var styleAttributeChanges = new Array();
                var attrName;
                var attrValue;
                for (attr in attrList) {
                    if (typeof(attrList[attr]) == 'string') {
                        attrName = attr;
                        var isStyleAttribute = false;
                        if (attrName.indexOf('.') > 0) {
                            isStyleAttribute = true;
                            attrName = attrName.substring(attr.indexOf('.') + 1);
                        }
                        attrValue = attrList[attr];

                        if (isStyleAttribute) {
                            styleAttributeChanges[attrName] = attrValue;
                        } else {
                            attributeChanges[attrName] = attrValue;
                        }
                    }
                }
                attrCollection.setAttributeList(attributeChanges);
                attrCollection.setStyleAttributeList(styleAttributeChanges);
            }
        }
    }

    this.getEnforcingFieldCount = function () {
        return getEnforcingFieldCount();
    }

    this.getEnforcingFields = function() {
        return getEnforcingFields();
    }

    this.getEnforcingFieldList = function() {
        return getEnforcingFieldList();
    }

    this.getEnforcingFieldObject = function (enforcingFieldId) {
        return getEnforcingField(enforcingFieldId);
    }

    this.getEnforcedFieldCount = function () {
        return getEnforcedFieldCount();
    }

    this.getEnforcedFields = function() {
        return getEnforcedFields();
    }

    this.getEnforcedFieldList = function() {
        return getEnforcedFieldList();
    }

    this.getEnforcedFieldObject = function (enforcedFieldId) {
        return getEnforcedField(enforcedFieldId);
    }

    this.getEnforcedFieldAttributeCollection = function (fieldId) {
        return getEnforcedFieldAttributeCollection(fieldId)
    }


    this.removeEnforcingFieldList = function (enforcingFieldList) {
        for (var fld in enforcingFieldList) {
            if (typeof(enforcingFieldList[fld]) == 'string') {
                removeEnforcingField(enforcingFieldList[fld]);
            }
        }
    }

    this.removeEnforcedFieldList = function (enforcedFieldList) {
        for (var fld in enforcedFieldList) {
            if (typeof(enforcedFieldList[fld]) == 'string') {
                removeEnforcedField(enforcedFieldList[fld]);
            }
        }
    }

    this.removeEnforcedFieldAttributes = function (fieldId, attrList) {
        var attrCollection = getEnforcedFieldAttributeCollection(fieldId);
        var attributeChanges = new Array();
        var styleAttributeChanges = new Array();
        var attrName;
        var attrValue;
        for (attr in attrList) {
            if (typeof(attrList[attr]) == 'string') {
                attrName = attr;
                var isStyleAttribute = false;
                if (attrName.indexOf('.') > 0) {
                    isStyleAttribute = true;
                    attrName = attrName.substring(attr.indexOf('.') + 1);
                }
                attrValue = attrList[attr];

                if (isStyleAttribute) {
                    styleAttributeChanges[attrName] = attrValue;
                } else {
                    attributeChanges[attrName] = attrValue;
                }
            }
        }
        attrCollection.removeAttributeList(attributeChanges);
        attrCollection.removeStyleAttributeList(styleAttributeChanges)
    }


    this.getReadonlyField = function (editableFieldId) {
        var field = getField(editableFieldId, true);
        return field;
    }

    this.getEditableField = function (readonlyFieldId) {
        var field = getField(readonlyFieldId, false);
        return field;
    }
};

/*
 Function that resets field's attribute value to original value when grid is set to empty.
 This method is used when remove all row(s) in the grid.
 */
function OBRResetToOriginalInEmptyGrid(currentSelectedRowId, gridId) {
    var rs = getXMLDataForGridName(gridId).recordset;
    if (isEmptyRecordset(rs)){
        var OBRTableDiv = getDivForGrid(gridId);
        var OBREnforcedActions = getOBRActionsOnTable(OBRTableDiv, currentSelectedRowId);
        //Reset UI changes made by OBR.
        if (OBREnforcedActions != null) {
            OBRResetToOriginal(OBREnforcedActions);
        }
    }
}

/*
   Function that resets field's attribute value to original value.
 */
function OBRResetToOriginal(OBREnforcedActions, OBRToBeEnforcedActions) {

    if (!OBREnforcedActions) {
        OBREnforcedActions = getPageOBREnforcedActions();
        if (!OBREnforcedActions) {
            //Nothing to revert back as none of the attributes got changed by OBR.
            return;
        }
    }

    var resetFlag = true;
    var fieldAttrIsGettingChangedAgain = false;
    var alreadyChangedFieldList = OBREnforcedActions.getEnforcedFieldList();
    for (var fld in alreadyChangedFieldList) {
        if (typeof(alreadyChangedFieldList[fld]) == 'string') {
            var fldId = alreadyChangedFieldList[fld];
            var fld = OBREnforcedActions.getEnforcedFieldObject(fldId);
            if (OBRToBeEnforcedActions) {
                fieldAttrIsGettingChangedAgain = OBRToBeEnforcedActions.hasEnforcedField(fldId);
            }
            var alreadyChangedAttributeCollectionObject = OBREnforcedActions.getEnforcedFieldAttributeCollection(fldId);
            var alreadyChangedAttributeList = alreadyChangedAttributeCollectionObject.getAllAttributeList().reverse();
            /* Reset non style attributes */
            for (var attr in alreadyChangedAttributeList) {
                if (typeof(alreadyChangedAttributeList[attr]) == 'string') {
                    resetFlag = true;
                    var attrName = alreadyChangedAttributeList[attr];
                    var isStyleAttr = (attrName.indexOf('.') > 0);
                    var styleAttrName = (isStyleAttr ? attrName.substring(attrName.indexOf('.') + 1) : "");
                    var originalAttribute = "original-" + (isStyleAttr ? attrName.replace(".", "_") : attrName);
                    if (fieldAttrIsGettingChangedAgain) {
                        // one or more field attribute is getting changed. Check each already changed attributes to make sure
                        // whether that attribute needs to be restored or not.
                        if (!isStyleAttr) {
                            if (OBRToBeEnforcedActions.getEnforcedFieldAttributeCollection(fldId).hasAttribute(attrName)) {
                                //The attribute is going to get changed again. So, dont reset it to avoid flickering effect.
                                resetFlag = false;
                            }
                        } else {
                            if (OBRToBeEnforcedActions.getEnforcedFieldAttributeCollection(fldId).style.hasAttribute(styleAttrName)) {
                                //The attribute is going to get changed again. So, dont reset it to avoid flickering effect.
                                resetFlag = false;
                            }
                        }
                    }

                    if (resetFlag) {
                        if (fld.getAttribute(originalAttribute) || styleAttrName == 'display' || attrName == 'readonly' || attrName == 'disabled') {
                            var originalAttributeValue = fld.getAttribute(originalAttribute);
                            OBRSetAttribute(fld, attrName, originalAttributeValue);
                        } else {
                            if (!isStyleAttr) {
                                getDisplayedField(fld).removeAttribute(attrName);
                            } else {
                                var obr = new OBRCollection();
                                $(obr.getEditableField(fldId)).css(styleAttrName, "");
                                $(obr.getReadonlyField(fldId)).css(styleAttrName, "")
                            }
                        }
                    } else {
                        // Issue : 147469
                        /*
                         Scenario:
                         If a grid has 3 records and the obr makes a field as read-only on first 2 records, when the user goes
                         to 3rd record, the field is not made editable.

                         Cause:
                         When the user goes to the 2nd row, OBR is suppose to revert all OBR changes done to the 1st row and then
                         apply OBR changes for the 2nd row. However, if OBR is changing the same attribute value for both 1st row and
                         2nd row (ie. originally the field was in editable mode, but OBR will make the field as read-only for both
                         1st and 2nd row), in order to avoid flickering the OBR changes for the attribute is not reverted
                         (ie. field going back to original editable field and then to readonly). Due to this behavior, in-correct
                         original value gets attached to the OBR field (eg. the original value for read-only attribute gets changed to "Y",
                         instead of "N" - "N" is the correct value, since originally the field was editable).

                         Fix:
                         Attach the correct original value to the ROSPAN field (read-only field) during the 2nd row selection. This way,
                         the original value on both editable & read-only field will be same.

                         */

                        var toBeEnforcedFieldObject = OBRToBeEnforcedActions.getEnforcedFieldObject(fldId);
                        if (toBeEnforcedFieldObject.getAttribute(originalAttribute) == null) {
                            //Get the attribute value from the editable field - fld.getAttribute(originalAttribute) .
                            toBeEnforcedFieldObject.setAttribute(originalAttribute, fld.getAttribute(originalAttribute));
                        }
                    }
                }
            }
        }
    }
}

/*
  Function
 */
function OBRSetAttribute(field, attributeName, attributeValue) {
    var originalFieldObject = field;
    field = getDisplayedField(field);
    var originalAttributeNamePrefix = "original-"
    var currentAttributeValue;
    if (field) {
        if (attributeName.indexOf(".") >= 0) {
            var styleName = attributeName.substring(attributeName.indexOf(".") + 1);
            originalAttributeNamePrefix += "style_" + styleName;
            if(window["useJqxGrid"])
                currentAttributeValue = getElementStyle(field, styleName);
            else
                currentAttributeValue = field.style.getAttribute(styleName);
            if (attributeName == 'style.display') {
                if (field.tagName.toLowerCase() == 'th') {
                    var thId = $(field).attr("id");
                    var gridColumn = getGridColumn(thId);
                    if (isDefined(gridColumn)) {
                        var tdContainer = gridColumn.closest('td');
                        if (attributeValue.toLowerCase()=="none") {
                            if(window["useJqxGrid"]) {
                                tdContainer.addClass("dti-hide");
                                $(field).addClass("dti-hide");
                            } else {
                                tdContainer.hide();
                                $(field).hide();
                            }
                        } else {
                            if(window["useJqxGrid"]){
                                tdContainer.removeClass("dti-hide");
                                $(field).removeClass("dti-hide");
                            } else {
                                tdContainer.show();
                                $(field).show();
                            }
                        }
                    } else {
                        // Unable to locate grid column.
                        return;
                    }
                } else if (field.tagName.toLowerCase() == 'a' && field.className.length >= 3) {
                    if ((" " + field.className.toLowerCase() + " ").indexOf(" tab ") >= 0 ) {
                        hideShowTabItem(field, (attributeValue == 'none' ? true : false));
                    } else if (isValidPanel(field)) {
                        currentAttributeValue = (field.className == 'panelUpTitle' ? "inline" : "none");
                        // Make sure panel is in right state, before calling the onclick event.
                        // If the request is to hide the panel, make sure the className is "panelUpTitle" -- which means
                        // the panel is visible.
                        // If the request is to unhide the panel, make sure the className is "panelDownTitle" -- which means
                        // the panel is hidden.
                        if ( (attributeValue=="none" && field.className == "panelUpTitle") ||
                              (attributeValue!="none" && field.className == "panelDownTitle") ) {
                            $(field).click();
                        }
                    }

                } else if (field.tagName.toLowerCase() == 'tr') {
                    //Layer Field.
                    $(field).css(attributeName.substring(attributeName.indexOf(".") + 1), attributeValue);

                } else if (field.tagName.toLowerCase() == 'input' && field.type.toLowerCase() == 'button' && field.className.length >= 6) {
                    if (field.className.substring(field.className.length - 6).toLowerCase() == 'button') {
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
                        currentAttributeValue = currentField.style[styleName];
                        hideShowActionItem(field, (attributeValue == 'none' ? true : false));

                        if (attributeValue=="none") {
                            field.setAttribute("isHiddenByOBR", "Yes");
                        } else {
                            field.removeAttribute("isHiddenByOBR");
                        }
                    }
                } else if (field.tagName.toLowerCase() == 'option') {
                    if (field.getAttribute("isHiddenByDefault") == "true") {
                        if (attributeValue == "none") {
                            field.removeAttribute("isDisplayedByOBR");
                        } else {
                            field.setAttribute("isDisplayedByOBR", "Yes");
                        }
                    } else {
                        if (attributeValue == "none") {
                            field.setAttribute("isHiddenByOBR", "Yes");
                        } else {
                            field.removeAttribute("isHiddenByOBR");
                        }
                    }
                } else {
                    var fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
                    if (fieldId.indexOf("MultiSelectText") > 0) {
                        fieldId = fieldId.substr(0, fieldId.indexOf("MultiSelectText"));
                    }
                    // Input parameter <<field>> can be a read only or editable field based on the initial page load.
                    // Always get reference to the editableField and work based on that because the hideShowField
                    // function works based on editable field id.
                    var obr = new OBRCollection();

                    if (isFieldHidden(fieldId)) {
                        // for hidden field in WebWB, set current attribute value to "none" for reset logic
                        currentAttributeValue = "none";
                    }

                    field = obr.getEditableField(fieldId);
                    hideShowField(field, (attributeValue == 'none' ? true : false));

                    if (attributeValue=="none") {
                        field.setAttribute("isHiddenByOBR", "Yes");
                    } else {
                        field.removeAttribute("isHiddenByOBR");
                    }
                }
            } else {
                var fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
                var obr = new OBRCollection();
                field = obr.getReadonlyField(fieldId);
                setElementStyle(field, styleName, attributeValue);
                field = obr.getEditableField(fieldId);
                setElementStyle(field, styleName, attributeValue);
            }
        } else {
            originalAttributeNamePrefix += attributeName;
            currentAttributeValue = field.getAttribute(attributeName);
            if (attributeName=="required") {
                currentAttributeValue = (isFieldRequired(field) ? "Y" : "N");
                var editField = getEditableField(field);
                eval("REQ_" + editField.name + " = " + (attributeValue=="Y"? "true" : "false") + ";");
                var labelField = getObject(editField.name + "FLDLABEL");
                if (labelField) {
                    labelField.className = (attributeValue=="Y"? "oasis_formlabelreq" : "oasis_formlabel");
                }
                field.className = (attributeValue=="Y"? "oasis_formfieldreq" : "oasis_formfield");
            //If we start supporting multiple className, uncomment the following block of commented code.
            /*
            } else if (attributeName=="className") {
                addObjectClassName(field, attributeValue);
            */
            } else if (attributeName=="disabled") {
                currentAttributeValue = (isFieldDisabled (field) ? "disabled" : "");
                if (currentAttributeValue == attributeValue) {
                    //Field is already rendered as disabled field.
                } else {
                    var fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
                    // if the field is displayed as ROSPAN (previously made as disabled),
                    // make sure always to set and validate the original Attribute value based on editable field.
                    var obr = new OBRCollection();
                    field = obr.getEditableField(fieldId);

                    enableDisableField(field, (attributeValue!=""));

                    // Checkbox might have been switched by enableDisableField.
                    // Ensure isDisabledByOBR is set correctly on the editable field only.
                    field = obr.getEditableField(fieldId);
                    if (attributeValue=="") {
                        field.removeAttribute("isDisabledByOBR");
                    } else {
                        field.setAttribute("isDisabledByOBR", "Yes");
                    }
                }
            } else if (attributeName=="readonly") {
                var fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
                // Input parameter <<field>> can be a read only or editable field based on the initial page load.
                // Always get reference to the editableField and work based on that because the setFieldReadonly
                // and setFieldEditable functions work based on editable field id.
                // Also, make sure always to set and validate the original Attribute value based on editable field.
                var obr = new OBRCollection();
                field = obr.getEditableField(fieldId);

                if (isFieldHidden(fieldId)) {
                    var isEditableWhenVisible = false;
                    var suffixArray = new Array(FIELD_LABEL_CONTAINER_SUFFIX, FIELD_VALUE_CONTAINER_SUFFIX);
                    for (var i = 0; i < suffixArray.length; i++) {
                        var containerId = field.name + suffixArray[i];
                        if (hasObject(containerId)) {
                            var container = $("#" + containerId);
                            isEditableWhenVisible = (container.attr("isEditableWhenVisible") == "Y");
                            container.attr("isEditableWhenVisible", attributeValue == "Y" ? "N" : "Y");
                        }
                    }
                    currentAttributeValue = (isEditableWhenVisible ? "N" : "Y");
                } else {
                    var isFieldRenderedAsReadonly = isFieldReadonly(field);
                    currentAttributeValue = (isFieldRenderedAsReadonly ? "Y" : "N");
                    if (currentAttributeValue == attributeValue) {
                        //Field is already rendered as read only field.
                    } else {
                        fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
                        if (field.length > 0) {   //For radio option list, check box list etc.
                            if (field.tagName)  {
                                if (field.tagName.toLowerCase() != "select") {
                                    fieldId = (isUndefined(field[0].name) ? field[0].id : (field[0].name == "" ? field[0].id : field[0].name));
                                }
                            }
                        }
                        if (attributeValue=="Y") {
                            setFieldReadonly(fieldId);
                        } else {
                            setFieldEditable(fieldId);
                        }
                    }
                }
            } else if (attributeName=="label") {
                var editableField = getEditableField(field);
                var fieldId = (isUndefined(editableField.name) ? editableField.id : (editableField.name == "" ? editableField.id : editableField.name));
                var position = fieldId.indexOf("_DISP_ONLY");
                if(position > -1){
                    fieldId = fieldId.substring(0, position);
                }
                var labelSpan = $('td#' + fieldId + "_LABEL_CONTAINER span");
                if (labelSpan.length > 0) {
                    currentAttributeValue = labelSpan.text();
                    labelSpan.text(attributeValue);
                }
            } else {
                field.setAttribute(attributeName, attributeValue);
            }
        }
        if (originalFieldObject.getAttribute(originalAttributeNamePrefix) == null) {
            originalFieldObject.setAttribute(originalAttributeNamePrefix, currentAttributeValue);
        }
    }
}

function getGridColumn (headerColumnId) {
    var dataFld = headerColumnId.substring(1);
    var gridColumn = $('div[dataFld="' + dataFld + '"]');
    if (gridColumn.length == 0) {
        // Look for TYPE_UPDATEONLY_MULTIPLE_DROPDOWN type field.
        gridColumn = $('div[dataFld="' + dataFld + "LOVLABEL" + '"]');
        if (gridColumn.length == 0) {
            //Look for formatted display field.
            gridColumn = $('div[dataFld="' + dataFld + "_DISP_ONLY" + '"]');
            if (gridColumn.length == 0) {
                gridColumn = $('span[dataFld="' + dataFld + "_DISP_ONLY" + '"]');
                if (gridColumn.length == 0) {
                    gridColumn = $('span[dataFld="' + dataFld + "LOVLABEL" + '"]');
                    if (gridColumn.length == 0) {
                        //Look for span - eg. url based grid column is rendered as <A> with an embedded <SPAN> within it.
                        gridColumn = $('span[dataFld="' + dataFld + '"]');
                        if (gridColumn.length == 0) {
                            gridColumn = $('select[dataFld="' + dataFld + '"]');
                            if (gridColumn.length == 0) {
                                // cannot locate grid column.
                                return undefined;
                            }
                        }
                    }
                }
            }
        }
    }
    return gridColumn;
}

function getField(field, isGetReadonlyField) {
    if (isValidPanel(field)) {
        return field;
    }
    var fieldId = (isUndefined(field.name) ? field.id : (field.name == "" ? field.id : field.name));
    var obr = new OBRCollection();
    if (isGetReadonlyField) {
        field = obr.getReadonlyField(fieldId);
    } else {
        field = obr.getEditableField(fieldId);
    }
    return field;
}

function isValidPanel(field) {
    if (field.tagName.toLowerCase() == 'a') {
        if (field.className == 'panelUpTitle' || field.className == 'panelDownTitle') {
            return true;
        }
    }
}

function getEditableField(field) {
    return getField(field, false);
}

function getReadonlyField(field) {
    return getField(field, true);
}

function getDisplayedField(field) {
    if (isValidPanel(field)) {
        return field;
    }
    var displayedField = null;
    var isFieldCurrentlyRenderedAsReadonly = isFieldReadonly(field);
    if (isFieldCurrentlyRenderedAsReadonly) {
        displayedField = getReadonlyField(field);
    } else {
        displayedField = getEditableField(field);
    }
    return displayedField;
}

function isFieldAttributeSetByOBR(field, attributeName) {
    var isAttributeSetByOBR = false;
    var originalAttributeNamePrefix = "original-"
    var originalAttributeName = originalAttributeNamePrefix;
    if (attributeName.indexOf(".") >= 0) {
        originalAttributeName += "style_" + attributeName.substring(attributeName.indexOf(".") + 1);
    } else {
        originalAttributeName += attributeName;
    }
    field = getEditableField(field);
    if (field) {
        var originalAttributeValue = field.getAttribute(originalAttributeName);
        isAttributeSetByOBR = isDefined(originalAttributeValue)  && (originalAttributeValue != null);
        if (!isAttributeSetByOBR) {
            field = getReadonlyField(field);
            originalAttributeValue = field.getAttribute(originalAttributeName);
            isAttributeSetByOBR = isDefined(originalAttributeValue)  && (originalAttributeValue != null);
        }
    }
    return isAttributeSetByOBR;
}

function isDisabledByOBR(field) {
    field = getEditableField(field);
    return (field ? field.getAttribute("isDisabledByOBR") != null : false);
}

function isHiddenByOBR(field) {
    field = getEditableField(field);
    return (field ? field.getAttribute("isHiddenByOBR") != null : false);
}

function isFieldDisabled (field) {
    var returnValue = field.disabled;
    if (!returnValue) {
        //Check if it is disabled by OBR previously.
        returnValue = isDisabledByOBR(field);
    }
    return returnValue;
}

function isFieldRequired (field) {
    var returnValue = false;
    var editField = getEditableField(field);
    eval("returnValue = REQ_" + editField.name + ";");
    return;
}

function isFieldReadonly (field)  {
    var returnValue = false;
    if (field.parentElement) {
        var containerClass = field.parentElement.className;
        if (containerClass) {
            if (containerClass.length >= "readonlyField".length) {
                //starts with "readonly" and ends with "Field" (logic required to handle header field classes)
                if (containerClass.substring(0, 8) == "readonly" && containerClass.substring(containerClass.length - 5) == "Field") {
                    returnValue = true;
                }
            }

        }
    }
/*
    if (field.parentElement.getAttribute("isEditable") != null) {
        returnValue = (field.parentElement.getAttribute("isEditable") != "Y")
    }
*/
    return returnValue;
}

function OBRFireChanges(OBRActions) {
    var OBRFieldChangeList = OBRActions.getEnforcedFieldList();
    for (var fc in OBRFieldChangeList) {
        if (typeof(OBRFieldChangeList[fc]) == 'string') {
            var fldId = OBRFieldChangeList[fc];
            var fld = OBRActions.getEnforcedFieldObject(fldId);
            var OBRAttributeChangeCollection = OBRActions.getEnforcedFieldAttributeCollection(fldId);
            var OBRAttributeChangeList = OBRAttributeChangeCollection.getAllAttributeList();
            for (var attrName in OBRAttributeChangeList) {
                var attrName = OBRAttributeChangeList[attrName];
                if (typeof(attrName) == 'string') {
                    var isStyleAttr = (attrName.indexOf('.') > 0);
                    var styleAttrName = (isStyleAttr ? attrName.substring(attrName.indexOf('.') + 1) : "");
                    var attrValue;
                    if (isStyleAttr) {
                        attrValue = OBRAttributeChangeCollection.getStyleAttribute(styleAttrName);
                    } else {
                        attrValue = OBRAttributeChangeCollection.getAttribute(attrName);
                    }
                    OBRSetAttribute(fld, attrName, attrValue);
                }
            }
        }
    }
}

function OBRFireForPage(OBRActions) {

    if (getPageOBREnforcedActions() != null) {
        if (OBRActions) {
            OBRResetToOriginal(getPageOBREnforcedActions(), OBRActions)
        } else {
            OBRActions = getPageOBREnforcedActions();
        }
    }

    if (OBRActions) {
        OBRFireChanges(OBRActions);
        setPageOBREnforcedActions(OBRActions);
        // page Entitlement result should be always honored at the end, irrespective of what OBR does
        functionExists = eval("window.pageEntitlements");
        if (functionExists) {
            pageEntitlements(false);
        }
    }
}

function setPageOBREnforcedActions(actions) {
    document.body.setAttribute("OBREnforcedActions", actions);
}

function getPageOBREnforcedActions() {
    var actions = document.body.OBREnforcedActions;
    if (typeof (actions) == 'undefined') {
        actions = null;
    }
    return actions;
}

function OBRFireForGrid(gridId, rowId, OBRActions, recursiveStage) {
    var OBRTableDiv = getDivForGrid(gridId)
    var previousSelectedRowId = getTableProperty(getTableForGrid(gridId), "previousSelectedRowId");
    var gridIdInRenderingOrder = OBR_GridIdInRenderingOrder;

    if (previousSelectedRowId) {
        //Reset UI changes made by previous row.
        if (getOBRActionsOnTable(OBRTableDiv, previousSelectedRowId) != null) {
            OBRResetToOriginal(getOBRActionsOnTable(OBRTableDiv, previousSelectedRowId), OBRActions)
        }
    }
    if ((isEmpty(recursiveStage) || recursiveStage == "prior")) {
        if (getOBRActionsOnTable(OBRTableDiv, rowId) != null) {
            //reset for current row changes triggered via AJAX
            OBRResetToOriginal(getOBRActionsOnTable(OBRTableDiv, rowId), OBRActions)
        }
        if (isEmpty(recursiveStage)) {
            OBRFireForPage();
        }
    }
    if ((isEmpty(recursiveStage) || recursiveStage == "prior")) {
        if (gridIdInRenderingOrder) {
            var priorGridIdInRenderingOrder = gridIdInRenderingOrder.substring(0, ("," + gridIdInRenderingOrder + ",").indexOf("," + gridId + ",") - 1);
            if (priorGridIdInRenderingOrder) {
                var gridIdList = priorGridIdInRenderingOrder.split(",");
                var immediateParentGridId = gridIdList[0];
                if (immediateParentGridId) {
                    var immediateParentGridRowId = getTableProperty(getTableForGrid(immediateParentGridId), "selectedRowId");
                    if (immediateParentGridRowId) {
                        var OBRPriorTableDiv = getDivForGrid(immediateParentGridId)
                        if (getOBRActionsOnTable(OBRPriorTableDiv, immediateParentGridRowId) != null) {
                            var OBRPriorGridActions = getOBRActionsOnTable(OBRPriorTableDiv, immediateParentGridRowId);
                            OBRFireForGrid(immediateParentGridId, immediateParentGridRowId, OBRPriorGridActions, 'prior');
                        }
                    }
                }
            }
        }
    }

    OBRFireChanges(OBRActions);
    // page Entitlement result should be always honored at the end, irrespective of what OBR does
    functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(true, gridId);
    }

    if (gridIdInRenderingOrder && (recursiveStage == "post")) {
        var postGridIdInRenderingOrder = gridIdInRenderingOrder.substring(("," + gridIdInRenderingOrder + ",").indexOf("," + gridId + ","));
        if (postGridIdInRenderingOrder) {
            var gridIdList = postGridIdInRenderingOrder.split(",");
            var immediateChildGridId = gridIdList[1];  //0 index will always contain gridId - we need the subsequent grids starting from gridId
            if (immediateChildGridId) {
                var immediateChildGridRowId = getTableProperty(getTableForGrid(immediateChildGridId), "selectedRowId");
                if (immediateChildGridRowId) {
                    var OBRChildTableDiv = getDivForGrid(immediateChildGridId)
                    if (getOBRActionsOnTable(OBRChildTableDiv, immediateChildGridRowId) != null) {
                        var OBRChildGridActions = getOBRActionsOnTable(OBRChildTableDiv, immediateChildGridRowId);
                        OBRFireForGrid(immediateChildGridId, immediateChildGridRowId, OBRChildGridActions, 'post');
                    }
                }
            }
        }
    }
    setOBRActionsOnTable(OBRTableDiv, rowId, OBRActions);
}

var PREFIX_OBR_ACTIONS = "OBRActionsFor";

function getOBRActionsOnTable(tableDiv, rowId) {
    var actions = eval("tableDiv." + getAttributeNameForOBRActions(rowId));
    if (!actions) {
        actions = null;
    }
    return actions;
}

function setOBRActionsOnTable(tableDiv, rowId, actions) {
    tableDiv.setAttribute(getAttributeNameForOBRActions(rowId), actions)
}

function getAttributeNameForOBRActions(rowId){
    // convert number to string
    var processedRowId = "" + rowId;
    // some row id contains "^", ".", "|", "-", replace them with "_". For example: view claim log page
    processedRowId =  processedRowId.replace(new RegExp("\\^|\\.|\\||\-", "g"), '_');
    var attributeName = PREFIX_OBR_ACTIONS + processedRowId;
    return attributeName;
}

function fireOBR(OBRFor) {
    var url = window.document.location.pathname;
    url += (url.indexOf("?") > 0 ? '&' : '?')
    url += "process=fireOBR&OBRFor=" + OBRFor;
    // initiate call
    var ajaxResponseHandler = "OBRResponse";
    new AJAXRequest("post", url, "", eval(ajaxResponseHandler), false);
}

function OBRResponse() {
    if (this.AJAX.readyState == 4) {
        enforceOBRForPageFieldsAfterAJAX();
        var gridId = "";
        if (gridId==getCurrentlySelectedGridId()) {
            enforceOBRForGridFieldsAJAX(gridId, getSelectedRow(getCurrentlySelectedGridId()));
        }
    }
}

// call orb logic for save events before submit page
function obrExecuteBeforeSave() {
    isOKToProceed = false;

    if (getObjectValue(OBR_hasRuleForSave) === "true") {
        var url = getFormActionAttribute();
        var data = "process=ObrOnSave";

        data += generateAjaxDataForFieldList(OBR_AllAccessedFieldList);

        if (hasObject("txtXML")) {
            var gridId = tblPropArray[0].id;
            var gridXML = getXMLDataForGridName(gridId);
            var origGridXml = getOrigXMLData(gridXML);
            syncChanges(origGridXml, gridXML);
            var xmlData = getChangesForRecordSet(origGridXml);
            data += "&txtXML=" + encodeURIComponent(xmlData);
        }
        data += "&date=" + new Date();

        new AJAXRequest("post", url, data, handleReturnOnObrBeforeSave, false);
    } else {
        isOKToProceed = true;
    }

    return isOKToProceed;
}

/*
 * fieldListName  OBR_EnforcingFieldList
 *                OBR_ConsequenceFieldList
 *                OBR_AllAccessedFieldList
 */
function generateAjaxDataForFieldList(fieldListName) {
    var data = "";
    if (hasObject(fieldListName)) {
        var fieldListValue = getObjectValue(fieldListName);
        if (fieldListValue != "") {
            var fieldNames = fieldListValue.split(",");
            for (var i = 0; i < fieldNames.length; i++) {
                data += "&" + fieldNames[i] + "=";
                if (hasObject(fieldNames[i])) {
                    data += encodeURIComponent(getObjectValue(fieldNames[i]));
                }
            }
        }
    }
    return data;
}

/*
    generate txtXML for current row
 */
function generateTXTXmlForCurrentRow(gridId){
    var xmlData = getXMLDataForGridName(gridId);
    var currectId = xmlData.recordset("ID").value;
    var nodes = xmlData.documentElement.selectNodes("//ROW[@id='"+currectId + "']");
    var txtXMLString = generateTXTXmlBasedOnNodes(nodes);
    return txtXMLString;
}

// handle AJAX return in function obrExecuteBeforeSave
function handleReturnOnObrBeforeSave(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            isOKToProceed = true;
        } else {
            alert("There was a problem retrieving the response data:\n" + ajax.statusText);
        }
    }
}

/*
    Return all columns, not just the changed columns as listed in the ROW.col attribute.
*/
function getChangesForRecordSet(ReferenceXML)
{
    var nodes = ReferenceXML.documentElement.selectNodes("//ROW");
    var txtXMLString = generateTXTXmlBasedOnNodes(nodes);
    return txtXMLString;
}

function generateTXTXmlBasedOnNodes(nodes){
    var nodelen = nodes.length;
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
        rowNode = nodes.item(i);
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


function setOBREnforcingFieldList(OBRActions, enforcingFieldList) {
    OBRActions.setEnforcingFieldList(enforcingFieldList.split(",")) ;
}

function setOBREnforcedResult(OBRActions, enforcedResult) {
    if (enforcedResult != "") {
        var OBREnforcedResult = enforcedResult.split("~,~") ;
        for (var i=0;i<OBREnforcedResult.length;i++) {
            var fieldAndAttributes=OBREnforcedResult[i].split("~=~");
            var OBREnforcedResultField = fieldAndAttributes[0];
            var OBREnforcedResultFieldChanges = fieldAndAttributes[1].split("~;~");
            var OBREnforcedResultFieldChangeList = new Object();
            for (var j=0;j<OBREnforcedResultFieldChanges.length;j++) {
                var nameValuePair = OBREnforcedResultFieldChanges[j].split("~:~");
                var value = nameValuePair[1];
                value = replace(value, "&nbsp;", " ");
                OBREnforcedResultFieldChangeList[nameValuePair[0]] = value;
            }
            OBRActions.setEnforcedFieldAttributes(OBREnforcedResultField, OBREnforcedResultFieldChangeList);
        }
    }
}

function enforceOBRForPageFields() {
    if (hasObject(OBR_EnforcedResult) && hasObject(OBR_EnforcingFieldList)) {
        if (getObjectValue(OBR_EnforcedResult)) {
            var OBRActions = new OBRCollection();
            setOBREnforcingFieldList(OBRActions, getObjectValue(OBR_EnforcingFieldList));
            setOBREnforcedResult(OBRActions, getObjectValue(OBR_EnforcedResult)) ;
            OBRFireForPage(OBRActions);
        }
    }
    return;
}

function enforceOBRForPageFieldsAfterAJAX() {
    if (hasObject(OBR_EnforcedResult) && hasObject(OBR_EnforcingFieldList)) {
        var OBRActions = new OBRCollection();
        setOBREnforcingFieldList(OBRActions, getObjectValue(OBR_EnforcingFieldList));
        setOBREnforcedResult(OBRActions, getObjectValue(OBR_EnforcedResult)) ;
        OBRFireForPage (OBRActions);
    }
    return;
}

function enforceOBRForGridFields(gridId, rowId) {
    var OBRActions;
    // following if condition is not required because AJAX will update the corresponding <ROW> directly.
    var OBRChildTableDiv = getDivForGrid(gridId);
    if (getOBRActionsOnTable(OBRChildTableDiv, rowId) != null) {
        OBRActions = getOBRActionsOnTable(OBRChildTableDiv, rowId);
    } else {
        OBRActions = new OBRCollection();
        functionExists = eval("window." + gridId + "_getOBREnforcingFieldList");
        if (functionExists) {
            var dataGrid = getXMLDataForGridName(gridId);
            setOBREnforcingFieldList(OBRActions, eval("window." + gridId + "_getOBREnforcingFieldList()"));
            setOBREnforcedResult(OBRActions, dataGrid.recordset("OBR_ENFORCED_RESULT").value) ;
        }
    }
    OBRFireForGrid (gridId, rowId, OBRActions);

    return true;
}

function enforceOBRForGridFieldsAJAX(gridId, rowId) {
    var OBRActions = new OBRCollection();
    var functionExists = eval("window." + gridId + "_getOBREnforcingFieldList");
    if (functionExists) {
        var dataGrid = getXMLDataForGridName(gridId);
        setOBREnforcingFieldList(OBRActions, eval("window." + gridId + "_getOBREnforcingFieldList()"));
        setOBREnforcedResult(OBRActions, dataGrid.recordset("OBR_ENFORCED_RESULT").value) ;
    }
    OBRFireForGrid (gridId, rowId, OBRActions);
    OBRFireForGrid (gridId, rowId, OBRActions, 'post');

    return;
}

function fireOBROnChange(field,isBeforeFireAjax){

    var fireOBRFor = "";
    var fireOBRAtPageLevel = false;
    var fieldId = (isEmpty(field.name)?field.id:field.name);
    executingBeforeFireAjax = isBeforeFireAjax;
    if (typeof(fieldId) != undefined && hasObject(OBR_EnforcingFieldList) && hasObject(OBR_GridIdList) && hasObject(OBR_EnforcingUpdateIndicator)) {
        var OBREnforcingFields = getObjectValue(OBR_EnforcingFieldList);
        var OBREnforcingUpdateIndicator = getObjectValue(OBR_EnforcingUpdateIndicator);
        if (!isEmpty(OBREnforcingUpdateIndicator)) {
            fireOBRFor += pageCode;
            fireOBRAtPageLevel = true;
        }
        else {
            if (("," + OBREnforcingFields + ",").indexOf("," + fieldId + ",") != -1) {
                fireOBRFor += pageCode;
                fireOBRAtPageLevel = true;
            }
        }
        var OBRGridIdList = getObjectValue(OBR_GridIdList)
        var startLoc = 0;
        var endLoc = 0;
        if (!isEmpty(OBRGridIdList)) {
            OBRGridIdList = OBRGridIdList + ",";
            if (OBRGridIdList.indexOf(",", startLoc) > 0) {
                while ( (endLoc = OBRGridIdList.indexOf(",", startLoc)) > 0) {
                    var gridId = OBRGridIdList.substring(startLoc, endLoc);
                    startLoc = endLoc + 1;
                    if (!isEmpty(gridId)) {
                        if (gridId == "dataBean") {
                            gridId = OBR_GridIdInRenderingOrder;
                        }
                        var gridIds = gridId.split(",");
                        for (var i = 0; i < gridIds.length; i++) {
                            var functionName = gridIds[i] + "_getOBREnforcingUpdateIndicator";
                            // check whether the function exists
                            // On Find Policy page, there is no grid on page before clicking search
                            if (eval("window." + functionName)) {
                                var OBRGridEnforcingUpdateIndicator = eval(functionName + "()");
                                if (("," + OBRGridEnforcingUpdateIndicator + ",").indexOf(",Y,") != -1) {
                                    fireOBRFor += (fireOBRFor == "" ? "" : ",") + gridIds[i];
                                }
                                else {
                                    var functionName = gridIds[i] + "_getOBREnforcingFieldList";
                                    // check whether the function exists
                                    // On Find Policy page, there is no grid on page before clicking search
                                    if (eval("window." + functionName)) {
                                        OBREnforcingFields = eval(functionName + "()");
                                        if (("," + OBREnforcingFields + ",").indexOf("," + fieldId + ",") != -1) {
                                            fireOBRFor += (fireOBRFor == "" ? "" : ",") + gridIds[i];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isEmpty(fireOBRFor)) {
            fireOBRFor = fireOBRFor + ",";
            startLoc = 0;
            if (fireOBRAtPageLevel) {
                startLoc = fireOBRFor.indexOf(",") + 1;
                //Fire AJAX for page level processing
                //Variable pageCode contains the page code value.
                var url = getFormActionAttribute();
                var data = "process=ObrOnChange";
                if (hasObject("_isNonGridFieldChanged")) {
                    data += "&_isNonGridFieldChanged=" + getObjectValue("_isNonGridFieldChanged");
                }
                data += "&_FieldIdTriggeredEvent=" + fieldId;
                data += generateAjaxDataForFieldList(OBR_EnforcingFieldList);
                new AJAXRequest("post", url, data, handleReturnOnObrChange, false);
            }

            while ( (endLoc = fireOBRFor.indexOf(",", startLoc)) > 0) {
                var gridId = fireOBRFor.substring(startLoc, endLoc);
                startLoc = endLoc + 1;

                //Fire AJAX for the grid level processing.
                //Variable gridId contains the grid id value.
                var url = getFormActionAttribute();
                var data = "process=ObrOnChange";
                data += "&_FieldIdTriggeredEvent=" + fieldId;
                var xmlString = generateTXTXmlForCurrentRow(gridId);
                data += "&txtXML=" + encodeURIComponent(xmlString);
                currentGridIdForRuleOnChange = gridId;
                new AJAXRequest("post", url, data, handleReturnOnObrChange, false);
                currentGridIdForRuleOnChange = null;
            }
        }

    }
}

function retryOBRSetValuesAfterFireAjax() {
    var retryOBRSetValuesCopy = retryOBRSetValues;
    retryOBRSetValues = new Array();
    if (retryOBRSetValuesCopy.length > 0) {
        var fieldNameArray = retryOBRSetValuesCopy[0];
        var fieldValueArray = retryOBRSetValuesCopy[1];
        for (var i = 0; i < fieldNameArray.length; i++) {
            if (hasObject(fieldNameArray[i])) {
                if (getObjectValue(fieldNameArray[i]) != fieldValueArray[i]) {
                    setFieldValue(fieldNameArray[i],fieldValueArray[i],true);
                }
            }
        }
    }
}

function fireOBROnAdd(gridId) {
    var functionExists =  eval(gridId +"_getOBREnforcingFieldList");
    if (functionExists) {
        if (eval(gridId + "_getOBREnforcingFieldList()") !== "") {
            var url = getFormActionAttribute();
            var data = "process=ObrOnAdd";
            var xmlString = generateTXTXmlForCurrentRow(gridId);
            data += "&txtXML=" + encodeURIComponent(xmlString);
            currentGridIdForRuleOnAdd = gridId;
            new AJAXRequest("post", url, data, handleReturnOnObrAdd, false);
            currentGridIdForRuleOnAdd = null;
        }
    }
}

var currentGridIdForRuleOnChange = null;
var currentGridIdForRuleOnAdd = null;


// handle AJAX return for OBR OnChange
function handleReturnOnObrChange(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            handleAjaxMessages(data, null);
            var root = data.documentElement;
            var enforcedResult = root.getElementsByTagName("OBREnforcedResult");
            if (enforcedResult.length > 0) {
                // NonGrid fields
                var resultString = "";
                if (enforcedResult[0].firstChild != null) {
                    resultString = enforcedResult[0].firstChild.nodeValue;
                }
                getObject("OBREnforcedResult").value = resultString;
                enforceOBRForPageFieldsAfterAJAX();
                // set changed value
                var changedFields = root.getElementsByTagName("OBRChangedFields")[0];
                var fieldNameArray = new Array();
                var fieldValueArray = new Array();
                var count = 0;
                for (var i = 0; i < changedFields.childNodes.length; i++) {
                    var node = changedFields.childNodes[i];
                    var fieldId = node.tagName;
                    var fieldValue = "";
                    if (node.firstChild != null) {
                        fieldValue = node.firstChild.nodeValue;
                    }
                    if (hasObject(fieldId)) {
                        if (getObjectValue(fieldId) != fieldValue) {
                            var success = setFieldValue(fieldId, fieldValue, true);
                           if (!success && executingBeforeFireAjax) {
                                fieldNameArray[count] = fieldId;
                                fieldValueArray[count] = fieldValue;
                                count++;
                            }
                        }
                    }
                }
                retryOBRSetValues = new Array();
                if (count > 0) {
                    retryOBRSetValues[0] = fieldNameArray;
                    retryOBRSetValues[1] = fieldValueArray;
                }
            } else {
                setGridRowEnforcedResult(root, currentGridIdForRuleOnChange);
            }
        } else {
            alert("There was a problem retrieving the response data:\n" + ajax.statusText);
        }
    }
}

// handle AJAX return for OBR OnAdd
function handleReturnOnObrAdd(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (data.documentElement) {
                handleAjaxMessages(data, null);
                var root = data.documentElement;
                setGridRowEnforcedResult(root, currentGridIdForRuleOnAdd);
            }
        } else {
            alert("There was a problem retrieving the response data:\n" + ajax.statusText);
        }
    }
}

function setGridRowEnforcedResult(documentElement, gridId) {
    // Grid fields
    var rowNode = documentElement.getElementsByTagName("ROW");
    if (rowNode.length > 0) {
        var rowNode = rowNode.item(0);
        var rowId = "";
        var enforceResultString = "";

        var xmlData = getXMLDataForGridName(gridId);
        for (var i = 0; i < rowNode.childNodes.length; i ++) {
            var node = rowNode.childNodes.item(i);
            if (!dti.oasis.node.isElementNode(node)) {
                continue;
            }

            var tagName = node.tagName;
            var tagValue = "";
            if (node.firstChild != null) {
                tagValue = node.firstChild.nodeValue;
            }
            if (tagName == "ID") {
                rowId = tagValue;
            } else if (tagName == "OBR_ENFORCED_RESULT") {
                enforceResultString = tagValue;
            } else {
                // set changed value3
                var fieldId = tagName;
                var success = false;
                // try to find the input field first.
                if (hasObject(fieldId)) {
                    if (getObjectValue(fieldId) != tagValue) {
                        success = setFieldValue(fieldId, tagValue, true);
                    } else {
                        success = true;
                    }
                }
                if (!success) {
                    var columnName = "C" + fieldId.toUpperCase();
                    if (isFieldExistsInRecordset(xmlData.recordset, columnName)) {
                        xmlData.recordset(columnName).value = tagValue;
                    }
                }
            }
        }
        xmlData.recordset("OBR_ENFORCED_RESULT").value = enforceResultString;
        enforceOBRForGridFieldsAJAX(gridId, rowId);
    }
}

// return style name for IE
// see http://msdn.microsoft.com/en-us/library/ms535870(v=VS.85).aspx
function getIEStyleName(styleName) {
    var styleNameForIE = styleName;
    if (styleNameForIE.indexOf("-") >= 0) {
        var words = styleNameForIE.split("-");
        styleNameForIE = words[0];
        for (var i = 1; i < words.length; i++) {
            if (words[i].length > 0) {
                var firstChar = words[i].substring(0, 1);
                var leftString = words[i].substring(1);
                styleNameForIE += firstChar.toUpperCase() + leftString;
            }
        }
    }
    return styleNameForIE;
}

function setElementStyle(element, styleName, styleValue) {
    var styleObject = element.style;
    styleObject.setAttribute(getIEStyleName(styleName), styleValue);
}

function removeElementStyle(element, styleName) {
    var styleObject = element.style;
    styleObject.removeAttribute(getIEStyleName(styleName));
}
