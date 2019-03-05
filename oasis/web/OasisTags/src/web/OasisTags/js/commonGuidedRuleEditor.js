var newLineIdSequence = -3000;
var isRuleChanged = false;

var MAPPING_SCOPE_OPTION = "option";

var INPUT_TYPE_FIELD = "FIELD";
var INPUT_TYPE_DATATYPE = "DATATYPE";
var INPUT_TYPE_DYNAMIC_OPERATOR = "DYNAMICOPERATOR";
var INPUT_TYPE_DYNAMIC_INPUT= "DYNAMICINPUT";
var INPUT_TYPE_DATE= "DATE";
var INPUT_TYPE_NAVIGATION= "MENU";

var DATA_TYPE_STRING = ".string";
var DATA_TYPE_DATE = ".date";
var DATA_TYPE_NUMBER = ".number";

var STYLE_CLASS_RULE_TR = "guidedRuleTR";
var STYLE_CLASS_RULE_TD = "guidedRuleTD";
var STYLE_CLASS_RULE_TEXT = "guidedRuleText";
var STYLE_CLASS_RULE_INPUT = "guidedRuleInput";
var STYLE_CLASS_RULE_LINK = "guidedRuleLink";
var STYLE_CLASS_RULE_DELETE_BUTTON = "guidedRuleDeleteButton";
var STYLE_CLASS_RULE_BUTTON_TD = "guidedRuleButtonTD";

var OPERATOR_IN = "in";
var OPERATOR_NOT_IN = "not in";

var ATTRIBUTE_IS_MULTIPLE_VALUE = "isMultipleValue";

function handleOnLoad() {
    generateHTMLForScope(MAPPING_SCOPE_WHEN);
    generateHTMLForScope(MAPPING_SCOPE_THEN);
    generateHTMLForScope(MAPPING_SCOPE_OPTION);
}

function generateHTMLForScope(scope) {
    var xmlNode = getXMLNodeForScope(scope);
    var lineNodes = xmlNode.childNodes;
    for (var i = 0; i < lineNodes.length; i++) {
        var newTRElement = generateTRForLineItem(scope, lineNodes[i]);
        var htmlParentNode = getHTMLTableBody(scope);
        htmlParentNode.appendChild(newTRElement);
    }
}

function getHTMLTableBody(scope) {
    var htmlTableObject = null;
    if (scope == MAPPING_SCOPE_WHEN) {
        htmlTableObject = getObject("guidedRuleWhenTable");
    } else if (scope == MAPPING_SCOPE_THEN) {
        htmlTableObject = getObject("guidedRuleThenTable");
    } else {
        htmlTableObject = getObject("guidedRuleOptionTable");
    }
    var tableBody = htmlTableObject.childNodes[0];
    return tableBody;
}

function getXMLNodeForScope(scope) {
    var node = null;
    if (scope == MAPPING_SCOPE_WHEN) {
        node = getXMLDocumentForRule().getElementsByTagName(TAG_NAME_WHEN).item(0);
    } else if (scope == MAPPING_SCOPE_THEN) {
        node = getXMLDocumentForRule().getElementsByTagName(TAG_NAME_THEN).item(0);
    } else {
        node = getXMLDocumentForRule().getElementsByTagName(TAG_NAME_OPTION).item(0);
    }
    return node;
}

function getXMLDocumentForRule() {
    return GuidedRuleXML.documentElement.ownerDocument;
}

function getXMLDocumentForPageFields() {
    return PageFieldsXML.documentElement.ownerDocument;
}

function getLineNodeByID(id) {
    return getXMLDocumentForRule().selectSingleNode("//Line[@id='" + id + "']");
}

function getInputNodeByID(id) {
    return getXMLDocumentForRule().selectSingleNode("//Input[@id='" + id + "']");
}

function generateTRForLineItem(scope, lineNode) {
    var lineId = lineNode.getAttribute("id");
    // create TR
    var elementBuilder = new ElementBuilder("tr");
    elementBuilder.appendAttribute("id", lineId);
    elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_TR);
    var trElement = document.createElement(elementBuilder.toString());
    // create TD
    elementBuilder = new ElementBuilder("td");
    elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_TD);
    var tdElement = document.createElement(elementBuilder.toString());
    var inputCount = 0;
    var dataType = null;
    var isMultipleValue = "N";
    for (var i = 0; i < lineNode.childNodes.length; i++) {
        var node = lineNode.childNodes[i];
        if (node.tagName == TAG_NAME_TEXT) {
            elementBuilder = new ElementBuilder("span");
            elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_TEXT);
            var spanElement = document.createElement(elementBuilder.toString());
            var nodeText  = node.text;
            nodeText = nodeText.replace(/\\~/g, ':::dti.tilde:::').replace(/~/g, '').replace(/:::dti.tilde:::/g, '\\~') ;
            spanElement.innerText = nodeText;
            tdElement.appendChild(spanElement);
        } else if (node.tagName == TAG_NAME_INPUT) {
            inputCount ++;
            var inputName = node.getAttribute("id");
            var inputType = node.getAttribute("type");
            if (inputType == null || inputType == "") {
                inputType = INPUT_TYPE_NORMAL;
            }
            var inputValue = node.text;
            var onChangeText = "guidedRuleDataChange(this, '" + lineId + "', '" + inputName + "')";
            if (inputType == INPUT_TYPE_NORMAL || inputType == INPUT_TYPE_FIELD || inputType == INPUT_TYPE_NAVIGATION) {
                var elementBuilder = new ElementBuilder("input");
                elementBuilder.appendAttribute("name", inputName);
                elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_INPUT);
                elementBuilder.appendAttribute("onchange", onChangeText);
                var inputElement = document.createElement(elementBuilder.toString());
                if (inputType == INPUT_TYPE_FIELD || inputType == INPUT_TYPE_NAVIGATION) {
                    inputValue = inputValue.replace(/~/g, '') ;
                }
                inputElement.value = inputValue;
                tdElement.appendChild(inputElement);
            }
            if (inputType == INPUT_TYPE_FIELD) {
                var data = getFieldData(scope, lineNode);
                addAutoCompletePicker(inputElement, data, window.formatFieldData, 700);
            } else if (inputType == INPUT_TYPE_NAVIGATION) {
                var data = getNavigationItemData();
                addAutoCompletePicker(inputElement, data, window.formatNavigationItemData, 600);
            } else if (inputType == INPUT_TYPE_DATATYPE) {
                elementBuilder = new ElementBuilder("select");
                elementBuilder.appendAttribute("name", inputName);
                elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_INPUT);
                elementBuilder.appendAttribute("onchange", onChangeText);
                var selectElement = document.createElement(elementBuilder.toString());
                addOptionItem(selectElement, "Text", DATA_TYPE_STRING);
                addOptionItem(selectElement, "Date", DATA_TYPE_DATE);
                addOptionItem(selectElement, "Number", DATA_TYPE_NUMBER);
                if (hasSelectOption(selectElement, inputValue)) {
                    selectElement.value = inputValue;
                } else {
                    // set default value
                    inputValue = DATA_TYPE_STRING;
                    node.text = inputValue;
                }
                dataType = inputValue;
                tdElement.appendChild(selectElement);
            } else if (inputType == INPUT_TYPE_DYNAMIC_OPERATOR) {
                elementBuilder = new ElementBuilder("select");
                elementBuilder.appendAttribute("name", inputName);
                elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_INPUT);
                elementBuilder.appendAttribute("onchange", onChangeText);
                var selectElement = document.createElement(elementBuilder.toString());

                var hasDynamicInput = getNextInputNode(node, INPUT_TYPE_DYNAMIC_INPUT) == null ? false : true;
                setOptionsForOperator(selectElement, dataType, hasDynamicInput);

                if (hasSelectOption(selectElement, inputValue)) {
                    selectElement.value = inputValue;
                } else {
                    // set default value
                    inputValue = "==";
                    node.text = inputValue;
                }
                if (inputValue == OPERATOR_IN || inputValue == OPERATOR_NOT_IN) {
                    isMultipleValue = "Y";
                }
                tdElement.appendChild(selectElement);
            } else if (inputType == INPUT_TYPE_DYNAMIC_INPUT) {
                // set dataType on Input Node
                node.setAttribute("dataType", dataType);
                // set isMultipleValue
                node.setAttribute(ATTRIBUTE_IS_MULTIPLE_VALUE, isMultipleValue);
                // add input
                var elementBuilder = new ElementBuilder("input");
                elementBuilder.appendAttribute("name", inputName);
                elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_INPUT);
                elementBuilder.appendAttribute("onchange", "dynamicInputOnChange(this, '" + lineId + "', '" + inputName + "')");
                elementBuilder.appendAttribute("onkeydown", "guidedRuleOnKeyDown(this, '" + lineId + "', '" + inputName + "')");
                var inputElement = document.createElement(elementBuilder.toString());

                if (isMultipleValue == "Y") {
                    // remove parenthesis
                    if (inputValue.startsWith("(")) {
                        inputValue = inputValue.substring(1);
                    }
                    if (inputValue.endsWith(")")) {
                        inputValue = inputValue.substring(0, inputValue.length - 1);
                    }
                    var valueList = inputValue.split(",");
                    for (var j = 0; j < valueList.length; j ++) {
                        if (j == 0) {
                            inputValue = removeDoubleQuote(dataType, valueList[j]);
                        } else {
                            inputValue += "," + removeDoubleQuote(dataType, valueList[j]);
                        }
                    }
                } else {
                    inputValue = removeDoubleQuote(dataType, inputValue);
                }
                node.text = inputValue;
                inputElement.value = inputValue;
                tdElement.appendChild(inputElement);
                // add date picker
                var linkElement = createDatePicker(inputName);
                // hide date picker for other data type
                if (dataType != DATA_TYPE_DATE) {
                    linkElement.style.display = "none";
                }
                tdElement.appendChild(document.createTextNode(" "));
                tdElement.appendChild(linkElement);
            }
        }
    }
    tdElement.appendChild(document.createTextNode(" "));
    tdElement.appendChild(generateDeleteButton(scope, lineId));
    trElement.appendChild(tdElement);
    if (scope == MAPPING_SCOPE_WHEN || scope == MAPPING_SCOPE_THEN) {
        trElement.appendChild(generateTDWithButton(scope, lineId));
    }
    return trElement;
}

function generateDeleteButton(scope, lineId) {
    var linkElement = createLinkElement("javascript:deleteLine('" + scope + "', '" + lineId + "');", STYLE_CLASS_RULE_DELETE_BUTTON);
    linkElement.appendChild(createLinkImage("delete.gif"));
    return linkElement;
}

function generateTDWithButton(scope, lineId) {
    var elementBuilder = new ElementBuilder("td");
    elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_BUTTON_TD);
    var tdElement = document.createElement(elementBuilder.toString());
    var addAfterButton = createLinkElement("javascript:addLineAfter('" + scope + "', '" + lineId + "');", STYLE_CLASS_RULE_LINK);
    addAfterButton.appendChild(createLinkImage("addafter.png"));
    tdElement.appendChild(addAfterButton);
    tdElement.appendChild(document.createTextNode(" "));
    var moveUpButton = createLinkElement("javascript:moveLineUp('" + scope + "', '" + lineId + "');", STYLE_CLASS_RULE_LINK);
    moveUpButton.appendChild(createLinkImage("moveup.png"));
    tdElement.appendChild(moveUpButton);
    tdElement.appendChild(document.createTextNode(" "));
    var moveDownButton = createLinkElement("javascript:moveLineDown('" + scope + "', '" + lineId + "');", STYLE_CLASS_RULE_LINK);
    moveDownButton.appendChild(createLinkImage("movedown.png"));
    tdElement.appendChild(moveDownButton);
    return tdElement;
}

function createLinkElement(href, className) {
    var elementBuilder = new ElementBuilder("a");
    elementBuilder.appendAttribute("href", href);
    elementBuilder.appendAttribute("class", className);
    var linkElement = document.createElement(elementBuilder.toString());
    return linkElement;
}

function createLinkImage(imageName) {
    var elementBuilder = new ElementBuilder("img");
    elementBuilder.appendAttribute("src", getCorePath() + "/images/" + imageName);
    var imageElement = document.createElement(elementBuilder.toString());
    return imageElement;
}

function createDatePicker(inputName) {
    var elementBuilder = new ElementBuilder("a");
    elementBuilder.appendAttribute("id", inputName + "_DATE_PICKER");
    elementBuilder.appendAttribute("class", STYLE_CLASS_RULE_LINK);
    elementBuilder.appendAttribute("href", "javascript:calendar('" + inputName + "');");
    var linkElement = document.createElement(elementBuilder.toString());
    linkElement.appendChild(createLinkImage("cal.gif"));
    return linkElement;
}

function guidedRuleDataChange(field, lineId, inputName) {
    
    var inputNode = getInputNodeByID(inputName);
    inputNode.text = field.value;
    isRuleChanged = true;

    var inputType = inputNode.getAttribute("type");
    if (inputType == INPUT_TYPE_FIELD) {
        var fieldId = inputNode.text;
        var fieldNode = getFieldNodeByID(inputNode.text);
        if (fieldNode != null) {
            var dataType = getDataType(fieldNode);
            var nextInputNode = getNextInputNode(inputNode, INPUT_TYPE_DATATYPE);
            if (nextInputNode != null) {
                var nodeId = nextInputNode.getAttribute("id");
                var object = getObject(nodeId);
                if (object.value != dataType) {
                    object.value = dataType;
                    object.fireEvent("onchange");
                }
            }
        }
    } else if (inputType == INPUT_TYPE_DATATYPE) {
        var dataType = inputNode.text;
        var dynamicOperatorInputNode = getNextInputNode(inputNode, INPUT_TYPE_DYNAMIC_OPERATOR);
        var dynamicInputInputNode = getNextInputNode(inputNode, INPUT_TYPE_DYNAMIC_INPUT);
        var hasDynamicInput = dynamicInputInputNode == null ? false : true;
        if (dynamicOperatorInputNode != null) {
            var nodeId = dynamicOperatorInputNode.getAttribute("id");
            var object = getObject(nodeId);
            var currentValue = object.value;
            setOptionsForOperator(object, dataType, hasDynamicInput);
            if (hasSelectOption(object, currentValue)) {
                // value is not changed
                object.value = currentValue;
            } else {
                if (object.options.length > 0) {
                    object.value = object.options[0].value;
                } else {
                    object.value = "";
                }
                object.fireEvent("onchange");
            }
        }
        if (dynamicInputInputNode != null) {
            // set dataType to Input Node
            dynamicInputInputNode.setAttribute("dataType", dataType);
            var nodeId = dynamicInputInputNode.getAttribute("id");
            var object = getObject(nodeId);
            if (dataType == DATA_TYPE_STRING) {
                hideShowDatePicker(nodeId, true);
            } else if (dataType == DATA_TYPE_DATE) {
                hideShowDatePicker(nodeId, false);
            } else if (dataType == DATA_TYPE_NUMBER) {
                hideShowDatePicker(nodeId, true);
                if (!checknum(object.value)) {
                    object.value = "";
                    object.fireEvent("onchange");
                }
            }
        }
    } else if (inputType == INPUT_TYPE_DYNAMIC_OPERATOR) {
        var nextInputNode = getNextInputNode(inputNode, INPUT_TYPE_DYNAMIC_INPUT);
        if (nextInputNode != null) {
            var operator = inputNode.text;
            var isMultipleValue = "N";
            if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) {
                isMultipleValue = "Y";
            }
            nextInputNode.setAttribute(ATTRIBUTE_IS_MULTIPLE_VALUE, isMultipleValue);
        }
    }
}

function dynamicInputOnChange(field, lineId, inputName) {
    var inputNode = getInputNodeByID(inputName);
    var dataType = inputNode.getAttribute("dataType");
    if (dataType == DATA_TYPE_NUMBER) {
        // reset value if number is invalid
        var isMultipleValue = inputNode.getAttribute(ATTRIBUTE_IS_MULTIPLE_VALUE);
        if (isMultipleValue == "Y") {
            var valueList = field.value.split(",");
            for (var i = 0; i < valueList.length; i ++) {
                if (!isNumberString(valueList[i])) {
                    field.value = "";
                    break;
                }
            }
        } else {
            if (!isNumberString(field.value)) {
                field.value = "";
            }
        }
    }
    guidedRuleDataChange(field, lineId, inputName);
}

function guidedRuleOnKeyDown(field, lineId, inputName) {
    var inputNode = getInputNodeByID(inputName);
    var inputType = inputNode.getAttribute("type");
    if (inputType == INPUT_TYPE_DYNAMIC_INPUT) {
        var dataType = inputNode.getAttribute("dataType");
        if (dataType == DATA_TYPE_NUMBER) {
            var evt = window.event;
            var isMultipleValue = inputNode.getAttribute(ATTRIBUTE_IS_MULTIPLE_VALUE);
            if (isMultipleValue == "Y" && evt.keyCode == 188) {
                // allow comma for multiple value
            } else {
                numformat();
            }
        }
    }
}

function isNumberString(numstr) {
    if (isNaN(parseInt(Number(numstr), 10))) {
        alert("Enter a numeric value in value field")
        return false
    }
    return true
}

function addOption() {
    isRuleChanged = true;
    var scope = MAPPING_SCOPE_OPTION;
    var xmlNodeForScope = getXMLNodeForScope(scope);
    // create line node
    var newLineNode = getXMLDocumentForRule().createElement(TAG_NAME_LINE);
    var newLineId = LINE_ID_PREFIX + (newLineIdSequence ++);
    newLineNode.setAttribute("id", newLineId);

    // create input node
    var inputNode = createElementWithText(TAG_NAME_INPUT, "");
    var inputName = newLineId + INPUT_NAME_STRING + "1";
    inputNode.setAttribute("id", inputName);
    inputNode.setAttribute("type", INPUT_TYPE_NORMAL);
    newLineNode.appendChild(inputNode);

    // add new node to xml
    xmlNodeForScope.appendChild(newLineNode);
    // add to html document
    var newTRElement = generateTRForLineItem(scope, newLineNode);
    getHTMLTableBody(scope).appendChild(newTRElement);
}

function addFirstWhen() {
    addLineAfter(MAPPING_SCOPE_WHEN, "");
}

function addFirstThen() {
    addLineAfter(MAPPING_SCOPE_THEN, "");
}

function addLineAfter(scope, lineId) {
    var url = getPageURL();
    url += "?process=selectItem";
    url += "&scope=" + scope;
    url += "&lineId=" + lineId;
    url += "&isRuleForSaveEvent=" + isRuleForSaveEvent();
    url += "&date=" + new Date();
    openDivPopup("", url, true, true, "", "", 640, 480, "", "", "", true, "", "", true);
}

function finishAddItem(scope, positionLineId, languageExpression) {
    isRuleChanged = true;

    var xmlNodeForScope = getXMLNodeForScope(scope);
    // create line node
    var newLineNode = getXMLDocumentForRule().createElement(TAG_NAME_LINE);
    var newLineId = LINE_ID_PREFIX + (newLineIdSequence ++);
    newLineNode.setAttribute("id", newLineId);

    // create text node or input node
    var count = 0;
    var startPosition = languageExpression.indexOf("{");
    var endPosition = 0;
    while (startPosition != -1) {
        count ++ ;
        if (startPosition > 0) {
            var text = languageExpression.substring(0, startPosition);
            newLineNode.appendChild(createElementWithText(TAG_NAME_TEXT, text));
        }
        endPosition = languageExpression.indexOf("}");
        // get input type
        var inputType = INPUT_TYPE_NORMAL;
        var placeholderString = languageExpression.substring(startPosition + 1, endPosition);
        var position = placeholderString.indexOf("_");
        if (position >= 0) {
            inputType = placeholderString.substring(position + 1);
        }
        var inputNode = createElementWithText(TAG_NAME_INPUT, "");
        var inputName = newLineId + INPUT_NAME_STRING + count;
        inputNode.setAttribute("id", inputName);
        inputNode.setAttribute("type", inputType);
        newLineNode.appendChild(inputNode);

        languageExpression = languageExpression.substring(endPosition + 1);
        startPosition = languageExpression.indexOf("{");
    }
    if (languageExpression != "") {
        newLineNode.appendChild(createElementWithText(TAG_NAME_TEXT, languageExpression));
    }
    // add new node to xml
    if (positionLineId == "") {
        if (xmlNodeForScope.hasChildNodes()) {
            var targetNode = xmlNodeForScope.childNodes[0];
            xmlNodeForScope.insertBefore(newLineNode, targetNode);
        } else {
            xmlNodeForScope.appendChild(newLineNode);
        }
    } else {
        var targetNode = getLineNodeByID(positionLineId);
        insertAfter(newLineNode, targetNode);
    }

    // generate HTML code for line
    var newTRElement = generateTRForLineItem(scope, newLineNode);
    // add to html document
    var targetObject = null;
    if (positionLineId == "") {
        targetObject = getHTMLTableBody(scope).childNodes[0];
    } else {
        targetObject = getObject(positionLineId);
    }
    insertAfter(newTRElement, targetObject);
}

function createElementWithText(tagName, text) {
    var element = getXMLDocumentForRule().createElement(tagName);
    var textNode = getXMLDocumentForRule().createTextNode(text);
    element.appendChild(textNode);
    return element;
}

function deleteLine(scope, lineId) {
    isRuleChanged = true;

    // remove it from XML
    var lineNode = getLineNodeByID(lineId);
    lineNode.parentNode.removeChild(lineNode);
    // remove it from HTML
    var trObject = getObject(lineId);
    trObject.parentNode.removeChild(trObject);

}


function moveLineUp(scope, lineId) {
    // change xml
    var lineNode = getLineNodeByID(lineId);
    var parentNode = lineNode.parentNode;
    if (parentNode.firstChild == lineNode) {
        // do nothing
    } else {
        isRuleChanged = true;

        var previousNode = lineNode.previousSibling;
        parentNode.removeChild(lineNode);
        parentNode.insertBefore(lineNode, previousNode);

        // change HTML
        var trObject = getObject(lineId);
        var parentObject = trObject.parentNode;
        var previousTRObject = trObject.previousSibling;
        parentObject.removeChild(trObject);
        parentObject.insertBefore(trObject, previousTRObject);
    }
}

function moveLineDown(scope, lineId) {
    // change xml
    var lineNode = getLineNodeByID(lineId);
    var parentNode = lineNode.parentNode;
    if (parentNode.lastChild == lineNode) {
        // do nothing
    } else {
        isRuleChanged = true;

        var nextNode = lineNode.nextSibling;
        parentNode.removeChild(lineNode);
        insertAfter(lineNode, nextNode);

        // change HTML
        var trObject = getObject(lineId);
        var parentObject = trObject.parentNode;
        var nextTRObject = trObject.nextSibling;
        parentObject.removeChild(trObject);
        insertAfter(trObject, nextTRObject);
    }
}

function finishEditRule() {
    if (isRuleChanged) {
        var ruleText = generateRuleText();
        getObject("ruleText").value = ruleText;
        window.frameElement.document.parentWindow.updateBusinessRule(ruleText);
    }
    closeThisDivPopup(true);
}

function generateRuleText() {
    var ruleText = "";
    // option
    var optionNode = getXMLNodeForScope(MAPPING_SCOPE_OPTION);
    for (var i = 0; i < optionNode.childNodes.length; i++) {
        var lineNode = optionNode.childNodes[i];
        ruleText += lineNode.childNodes[0].text;
        ruleText += "\r\n";
    }
    ruleText += "when" + generateRuleTextForScope(MAPPING_SCOPE_WHEN);
    ruleText += "\r\nthen" + generateRuleTextForScope(MAPPING_SCOPE_THEN);
    return ruleText;
}

function generateRuleTextForScope(scope) {
    var ruleText = "";
    var xmlNode = getXMLNodeForScope(scope);
    for (var i = 0; i < xmlNode.childNodes.length; i++) {
        var lineNode = xmlNode.childNodes[i];
        ruleText += "\r\n    ";
        ruleText += generateRuleForLine(lineNode);

    }
    return ruleText;
}

function generateRuleForLine(lineNode) {
    var ruleText = "";
    for (var j = 0; j < lineNode.childNodes.length; j++) {
        var node = lineNode.childNodes[j];
        if (node.tagName == TAG_NAME_TEXT) {
            // No trim for Text node.
            ruleText += node.firstChild.nodeValue;
        } else if (node.tagName == TAG_NAME_INPUT) {
            var value = node.text;
            var inputType = node.getAttribute("type");

            if (value.length > 0 && (inputType == INPUT_TYPE_FIELD || inputType == INPUT_TYPE_NAVIGATION)) {
                value = (value.substring(0,1)=='~' ? value :  ('~' + value + '~'));
            } else if (inputType == INPUT_TYPE_DYNAMIC_INPUT) {
                var dataType = node.getAttribute("dataType");
                var isMultipleValue = node.getAttribute("isMultipleValue");
                if (isMultipleValue == "Y"){
                    var valueList = value.split(",");
                    for (var i = 0; i < valueList.length; i ++) {
                        if (i == 0) {
                            value = "(";
                        } else {
                            value += ",";
                        }
                        value += addDoubleQuote(dataType, trim(valueList[i]));
                        if (i == valueList.length - 1) {
                            value += ")";
                        }
                    }
                } else {
                    value = addDoubleQuote(dataType, value);
                }
            }
            if (value == "") {
                ruleText += PLACE_HOLDER_FOR_BLANK_VALUE;
            } else {
                ruleText += value;
            }
        }
    }
    return  ruleText;
}

function addDoubleQuote(dataType, value) {
    var add = false;
    if (dataType == DATA_TYPE_STRING) {
        add = true;
    } else if (dataType == DATA_TYPE_NUMBER) {
        add = false;
    } else if (dataType == DATA_TYPE_DATE) {
        if (value == "today") {
            add = false;
        } else {
            add = true;
        }
    }
    var returnValue = value;
    if (add) {
        returnValue = "\"" + value + "\"";
    }
    return returnValue;
}

function removeDoubleQuote(dataType, value) {
    var remove = false;
    if (dataType == DATA_TYPE_STRING) {
        remove = true;
    } else if (dataType == DATA_TYPE_NUMBER) {
        remove = false;
    } else if (dataType == DATA_TYPE_DATE) {
        if (value == "today") {
            remove = false;
        } else {
            remove = true;
        }
    }
    var returnValue = value;
    if (remove) {
        if (returnValue.startsWith("\"")) {
            returnValue = returnValue.substring(1);
        }
        if (returnValue.endsWith("\"")) {
            returnValue = returnValue.substring(0, returnValue.length - 1);
        }
    }
    return returnValue;
}


function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE_DIV':
            if (isRuleChanged) {
                if (confirm(getMessageForDataChange())) {
                    closeThisDivPopup(true);
                } else {
                    return false;
                }
            }
            break;
    }
}

function isOkToChangePages(){
    return !isRuleChanged;
}

function getMappingItem() {
    return GuidedRuleXML.getElementsByTagName("MappingItem");
}

function insertAfter(newElement, targetElement) {
    var parentElement = targetElement.parentNode;
    if (parentElement.lastChild == targetElement) {
        parentElement.appendChild(newElement);
    } else {
        parentElement.insertBefore(newElement, targetElement.nextSibling);
    }
}

function validateRule() {
    var url = getPageURL();
    var ruleText = generateRuleText();
    ruleText = ruleText.replace(/\\~/g, ':::dti.tilde:::').replace(/~/g, '').replace(/:::dti.tilde:::/g, '\\~');
    ruleText = encodeURIComponent(ruleText);
    url += "?process=validateRule";
    url += "&ruleId=" + getObjectValue("ruleId");
    url += "&ruleText=" + ruleText;
    url += "&pageId=" + getObjectValue("contextPageId");
    url += "&date=" + new Date();
    openDivPopup("", url, true, true, "", "", 640, 480, "", "", "", true, "", "", true);
}

function viewSource() {
    var url = getPageURL();
    var ruleText = generateRuleText();
    ruleText = encodeURIComponent(ruleText);
    url += "?process=viewSource";
    url += "&ruleId=" + getObjectValue("ruleId");
    url += "&ruleText=" + ruleText;
    url += "&pageId=" + getObjectValue("contextPageId");
    url += "&date=" + new Date();
    openDivPopup("", url, true, true, "", "", 640, 480, "", "", "", true, "", "", true);
}

function isRuleForSaveEvent(){
    var isForSave = false;
    var xmlNode = getXMLNodeForScope(MAPPING_SCOPE_WHEN);
    for (var i = 0; i < xmlNode.childNodes.length; i++) {
        var lineNode = xmlNode.childNodes[i];
        for (var j = 0; j < lineNode.childNodes.length; j++) {
            var node = lineNode.childNodes[j];
            if (node.tagName == TAG_NAME_TEXT) {
                var text = node.firstChild.nodeValue;
                if (text == "Execute On Save" || text.indexOf("OnSaveEvent") >= 0) {
                    isForSave = true;
                    break;
                }
            }
        }
        if (isForSave) {
            break;
        }
    }
    return isForSave;
}

function addOptionItem(selectObject, label, value, currentValue) {
    var newOption = document.createElement("option");
    newOption.text = label;
    newOption.value = value;
    if (value == currentValue) {
        newOption.selected = "selected";
    }
    selectObject.options.add(newOption);
}

function hasSelectOption(selectObject, value) {
    var success = false;
    for (var i = 0; i < selectObject.options.length; i++) {
        var option = selectObject.options[i];
        if (option.value == value) {
            success = true;
            break;
        }
    }
    return success;
}

function clearSelectOption(selectObject) {
    for (var i = 0; i < selectObject.options.length;) {
        selectObject.removeChild(selectObject.options[i]);
    }
}

function getDataType(fieldNode) {
    var returnDataType = null;
    var dataType = fieldNode.getAttribute("dataType");
    switch (dataType) {
        case "ST":
            returnDataType = DATA_TYPE_STRING;
            break;
        case "CU":
        case "CF":
        case "NM":
            returnDataType = DATA_TYPE_NUMBER;
            break;
        case "DT":
            returnDataType = DATA_TYPE_DATE;
            break;
        default:
            returnDataType = DATA_TYPE_STRING;
    }
    return returnDataType;
}

function getFieldNodeByID(fieldId) {
    return getXMLDocumentForPageFields().selectSingleNode("/Page/Field[.='" + fieldId + "']");
}

function getNextInputNode(currentNode, targetInputType) {
    var foundNode = null;
    var nextNode = currentNode.nextSibling;
    while (nextNode != null) {
        if (nextNode.tagName == TAG_NAME_INPUT) {
            var inputType = nextNode.getAttribute("type");
            if (inputType == targetInputType) {
                foundNode = nextNode;
                break;
            }
        }
        nextNode = nextNode.nextSibling;
    }
    return foundNode;
}

function setOptionsForOperator(selectElement, dataType, hasDynamicInput) {
    clearSelectOption(selectElement);
    addOptionItem(selectElement, "==", "==");
    addOptionItem(selectElement, "!=", "!=");
    if (dataType == DATA_TYPE_DATE || dataType == DATA_TYPE_NUMBER) {
        addOptionItem(selectElement, ">", ">");
        addOptionItem(selectElement, ">=", ">=");
        addOptionItem(selectElement, "<", "<");
        addOptionItem(selectElement, "<=", "<=");
    }
    if (hasDynamicInput && (dataType == DATA_TYPE_STRING || dataType == DATA_TYPE_NUMBER)) {
        addOptionItem(selectElement, "In", OPERATOR_IN);
        addOptionItem(selectElement, "Not In", OPERATOR_NOT_IN);
    }
}

function hideShowDatePicker(inputName, isHidden) {
    var linkId = inputName + "_DATE_PICKER";
    if (hasObject(linkId)) {
        var linkObject = getObject(linkId);
        if (isHidden) {
            linkObject.style.display = "none";
        } else {
            linkObject.style.display = "";
        }
    }
}

/**
 * an object for create element
 * @param tagName
 */
function ElementBuilder(tagName) {
    this.tagName = tagName;
    this.result = "<" + tagName;

    this.appendAttribute = function(attrName, value) {
        this.result = this.result + " " + attrName + "=\"" + value + "\"";
    }

    this.toString = function() {
        return this.result + "/>";
    }
}

function addAutoCompletePicker(inputElement, data, formatFunction, width) {
    var autoCompleteObject = $(inputElement).autocomplete({
            source:data,
            select:window.handleOnAutoCompleteSelect,
            change:window.handleOnAutoCompleteChange
        }
    );
    autoCompleteObject.data( "ui-autocomplete" )._renderItem = formatFunction;
}

function handleOnAutoCompleteSelect(event, ui){
    event.target.value = ui.item.value;
    event.target.fireEvent("onchange");
}

function handleOnAutoCompleteChange(event, ui){
    event.target.fireEvent("onchange");
}

var recordReg = new RegExp("^.*Record *\\(.*type *== *\"([-_a-zA-Z0-9]+)\".*$");

function getFieldData(scope, lineNode) {
    var fieldElements = getXMLDocumentForPageFields().getElementsByTagName(TAG_NAME_FIELD);
    if (scope == MAPPING_SCOPE_WHEN) {
        // try to get the record type
        var previousLine = lineNode.previousSibling;
        var ruleTextForParentObject = null;
        while (previousLine != null) {
            var ruleText = trim(generateRuleForLine(previousLine));
            if (!ruleText.startsWith("-")){
                // this line represents a fact object
                ruleTextForParentObject = ruleText;
                 break;
            }
            previousLine = previousLine.nextSibling;
        }
        if (ruleTextForParentObject != null) {
            var isForHeaderRecord = false;
            var isForOtherRecord = false;
            if (ruleText.indexOf("There is a header record") >= 0) {
                isForHeaderRecord = true;
            } else if (ruleText.indexOf("There is a grid record") >= 0 || ruleText.indexOf("There is a non-grid record") >= 0) {
                isForOtherRecord = true;
            } else {
                var result = recordReg.exec(ruleTextForParentObject);
                if (result) {
                    var recordType = result[1];
                    if (recordType == "Header") {
                        isForHeaderRecord = true;
                    } else if (recordType == "NonGrid" || recordType == "Grid") {
                        isForOtherRecord = true;
                    }
                }
            }
            if (isForHeaderRecord) {
                // only keep header field
                var data = new Array();
                for (var i = 0; i < fieldElements.length; i ++) {
                    var layerId = fieldElements[i].getAttribute("layerId");
                    if (layerId == "HEADER_FIELDS_LAYER") {
                        data.push(fieldElements[i]);
                    }
                }
                fieldElements = data;
            } else if (isForOtherRecord) {
                // remove header field
                var data = new Array();
                for (var i = 0; i < fieldElements.length; i ++) {
                    var layerId = fieldElements[i].getAttribute("layerId");
                    if (layerId != "HEADER_FIELDS_LAYER") {
                        data.push(fieldElements[i]);
                    }
                }
                fieldElements = data;
            }
        }
    }
    return convertToSource(fieldElements);
}

function getNavigationItemData() {
    var navigationElements = getXMLDocumentForPageFields().getElementsByTagName(TAG_NAME_NAVIGATION);
    return convertToSource(navigationElements);
}

function convertToSource(fieldElements){
    var array = new Array();

    for(var i=0;i<fieldElements.length;i++){
        var newitem = new Object();
        newitem.value = fieldElements[i].text;
        // put both value and label here for match
        newitem.label = fieldElements[i].getAttribute("label") + " " + newitem.value;
        // store the real label in desc
        newitem.desc = fieldElements[i].getAttribute("label");
        newitem.icon = "";
        newitem.visible = fieldElements[i].getAttribute("visibleB") == "Y" ? "Visible" : "Hidden";
        array.push(newitem);
    }
    return array;
}

function formatFieldData(ul, item) {
    var fieldId = item.value;
    var visibleB = item.visible;
    var label = item.desc;
    var htmlString = "<li><a><span style='width:250px' class='guidedRuleFirstSpan'>" + addBold(this, fieldId)
        + "</span> <span style='width:300px' class='guidedRuleOtherSpan'>" + addBold(this, label)
        + "</span> <span class='guidedRuleOtherSpan'>" + visibleB + "</span></a></li>";
    return $(htmlString).appendTo(ul);
}

function formatNavigationItemData(ul, item) {
    var navigationId = item.value;
    var label = item.desc;
    var htmlString = "<li><a><span style='width:250px' class='guidedRuleFirstSpan'>" + addBold(this, navigationId)
        + "</span> <span class='guidedRuleOtherSpan'>" + addBold(this, label) + "</span></a></li>";
    return $(htmlString).appendTo(ul);
}

function addBold(autoComplete, text) {
    var searchText = null;
    var result = text;
    try {
        if (autoComplete && autoComplete.element && autoComplete.element.length > 0) {
            searchText = autoComplete.element[0].value;
        }
        if (searchText != null && searchText.length > 0) {
            searchText = $.ui.autocomplete.escapeRegex(searchText);
            var reg = new RegExp("(" + searchText + ")", "ig");
            result = text.replace(reg, "<strong>$1</strong>");
        }
    } catch (e) {
        // do nothing
    }
    return result;
}
