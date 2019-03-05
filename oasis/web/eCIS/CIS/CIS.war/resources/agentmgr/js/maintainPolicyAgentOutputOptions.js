//-----------------------------------------------------------------------------
//  Description: maintain policy agent output option
//  Revision Date     Revised By      Description
//  10/17/2018        dzhang          Issue 195835: grid replacement: change to setObjectValue
//  ---------------------------------------------------------------------------


function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'SAVE':
            saveOutputOption();
            break;
    }
}

function polAgentOutputOptionListGrid_setInitialValues() {
    // set url
    var url = getAppPath() + "/agentmgr/maintainPolicyAgentOutputOptions.do?process=getInitialValuesForAddOutputOption"
            + "&currectTime=" + Date.parse(new Date());
    // initiate call
    var ajaxResponseHandler = "handleOnGetInitialValuesForAddOutputOption";
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnGetInitialValuesForAddOutputOption(ajax){
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                var selectedDataGrid = getXMLDataForGridName("polAgentOutputOptionListGrid");
                if (selectedDataGrid != null) {
                    setRecordsetByObject(selectedDataGrid, oValueList[0]);
                }
            }
        }
    }
}

function saveOutputOption() {
    commonOnSubmit("SAVE", true, false, false, true);
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            if (isPageDataChanged()) {
                setObjectValue("process", "saveAllAgentOutputOption");
            }
            else {
                proceed = false;
            }
            break;
        default:
            proceed = false;
    }
    return proceed;
}
