function handleOnLoad() {
    var pageTitle = "";
    if (parameterScope == MAPPING_SCOPE_WHEN) {
        pageTitle = "Select A Condition";
    } else if (parameterScope == MAPPING_SCOPE_THEN) {
        pageTitle = "Select An Action";
    }
    if (hasObject("pageTitleForpageHeader")) {
        getObject("pageTitleForpageHeader").innerText = pageTitle;
    }
    if (hasObject("apptitleSpan")){
        getObject("apptitleSpan").innerHTML = "<br>&nbsp;" + pageTitle;
    }
    loadDropDownListFromParent();
}

function loadDropDownListFromParent() {
    var selectItem = getObject("selectItem");
    var mappingItem = window.frameElement.document.parentWindow.getMappingItem();
    for (var i = 0; i < mappingItem.length; i++) {
        var scope = mappingItem[i].childNodes[0].text;
        if (scope == parameterScope) {
            var addOption = true;
            if (parameterScope == MAPPING_SCOPE_THEN && isRuleForSaveEvent == "true") {
                var isMappingForSaveEvent = mappingItem[i].childNodes[3].text;
                if (isMappingForSaveEvent != "true") {
                    addOption = false;
                }
            }
            var language = mappingItem[i].childNodes[1].text;
            if (isRuleForSaveEvent == "false" && language.indexOf("recordVariableName") >= 0) {
                addOption = false;
            }
            if (addOption) {
                var newOption = document.createElement("option");
                newOption.text = getDisplayTextForMapping(language);
                newOption.value = language;
                selectItem.options.add(newOption);
            }
        }
    }
    selectItem.value = selectItem.options[0].value;
}

function getDisplayTextForMapping(languageExpression) {
    var displayText = "";
    var tempString = languageExpression;
    var startPosition = tempString.indexOf("{");
    var endPosition = 0;
    while (startPosition != -1) {
        if (startPosition > 0) {
            var text = tempString.substring(0, startPosition);
            displayText += text;
        }
        endPosition = tempString.indexOf("}");

        var variableName = tempString.substring(startPosition + 1, endPosition);
        var position = variableName.indexOf("_");
        if (position >= 0) {
            variableName = variableName.substring(0, position);
        }
        displayText += " [ " + variableName + " ] ";

        tempString = tempString.substring(endPosition + 1);
        startPosition = tempString.indexOf("{");
    }
    if (tempString != "") {
        displayText += tempString;
    }
    return displayText;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'OK':
            var selectItem = getObjectValue("selectItem");
            window.frameElement.document.parentWindow.finishAddItem(parameterScope, parameterLineId, selectItem);
            closeThisDivPopup(false);
            break;
    }
}