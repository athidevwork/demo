var addressPhoneIds = "";
var riskEntityIds = "";

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'CLOSE':
            baseCloseWindow();
            break;
        case 'COPYALL':
            if (validateAndGroupIdsForCopyAll()) {
                sendAJAXRequest("copyAllAddressPhone");
            }
            break;
    }
}

function addressPhoneForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn, "performRiskAddrPhoneCopyListGrid");
}

function riskNameForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn, "riskNameListGrid");
}

function validateAndGroupIdsForCopyAll() {
    var isSelected = false;
    // validate Address/Phone selection
    if (!isEmptyRecordset(performRiskAddrPhoneCopyListGrid1.recordset)) {
        var effDateStr = "";
        first(performRiskAddrPhoneCopyListGrid1);
        while (!performRiskAddrPhoneCopyListGrid1.recordset.eof) {
            var isGenerate = performRiskAddrPhoneCopyListGrid1.recordset("CSELECT_IND").value;
            if (isGenerate == "-1") {
                isSelected = true;
                addressPhoneIds += performRiskAddrPhoneCopyListGrid1.recordset("CTYPE").value + "|"
                                 + performRiskAddrPhoneCopyListGrid1.recordset("ID").value + "|"
                                 + performRiskAddrPhoneCopyListGrid1.recordset("CTYPECODE").value + ",";
                effDateStr += performRiskAddrPhoneCopyListGrid1.recordset("CEFFECTIVEFROMDATE").value + "|";
            }
            next(performRiskAddrPhoneCopyListGrid1);
        }
        first(performRiskAddrPhoneCopyListGrid1);
        if (addressPhoneIds.length > 0) {
            effDateStr = effDateStr.substring(0, effDateStr.length - 1);
            setInputFormField("addressPhoneIds", addressPhoneIds + effDateStr);
            addressPhoneIds = "";
            effDateStr = "";
        }
        else {
            setInputFormField("addressPhoneIds", "");
        }
        if (!isSelected) {
            //handleError(getMessage("pm.copyAddrPhone.addrPhone.noselection.error"));
        }
    }else{
        handleError(getMessage("pm.copyAddrPhone.addrPhone.noselection.error"));
        return false;
    }

    // validate Risk Name selection
    isSelected = false;
    if (!isEmptyRecordset(riskNameListGrid1.recordset)) {
        first(riskNameListGrid1);
        while (!riskNameListGrid1.recordset.eof) {
            var isGenerate = riskNameListGrid1.recordset("CSELECT_IND").value;
            if (isGenerate == "-1") {
                isSelected = true;
                riskEntityIds += riskNameListGrid1.recordset("CENTITYID").value + ",";
            }
            next(riskNameListGrid1);
        }
        first(riskNameListGrid1);
        if (riskEntityIds.length > 0) {
            riskEntityIds = riskEntityIds.substring(0, riskEntityIds.length - 1);
            setInputFormField("riskEntityIds", riskEntityIds);
            riskEntityIds = "";
        }
        else {
            setInputFormField("riskEntityIds", "");
        }
        if (!isSelected) {
            //handleError(getMessage("pm.copyAddrPhone.copyToRisk.noselection.error"));
        }
    }else{
        handleError(getMessage("pm.copyAddrPhone.copyToRisk.noselection.error"));
        return false;
    }

    return true;
}

function sendAJAXRequest(process) {
    // set url
    var url = getAppPath() + "/riskmgr/performRiskAddressPhoneCopy.do?"
        + commonGetMenuQueryString() + "&process=" + process;

    switch (process) {
        case 'copyAllAddressPhone':
            url += "&policyId=" + getObjectValue("policyId")
                   + "&changeEffectiveDate=" + getObjectValue("changeEffectiveDate")
                   + "&addressPhoneIds=" + getObjectValue("addressPhoneIds")
                   + "&riskEntityIds=" + getObjectValue("riskEntityIds");
            break;
    }

    // initiate call
    var ajaxResponseHandler = "handleOn" + process.substr(0, 1).toUpperCase() + process.substr(1);
    new AJAXRequest("get", url, "", eval(ajaxResponseHandler), false);
}

function handleOnCopyAllAddressPhone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;
        }
    }
}
