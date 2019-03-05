//-----------------------------------------------------------------------------
//  Description: js file for process policy mailing page.
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  11/17/2010       dzhang      Modify mailingAttributeListGrid_setInitialValues() set the currentlySelectedGridId
//                               to "mailingAttributeListGrid";
//  11/18/2010       dzhang      114228 - Force fire ajax when set initial value for mailingAttributeListGrid done.
//  01/28/2011       fcb         117193 - document.all(gridDetailDivId) replaced with hasObject(gridDetailDivId)
//  06/06/2014       adeng       152052 - Modified mailingAttributeListGrid_setInitialValues() to use
//                                        the fireEvent("onChange") to instead of the fireAjax function.
//  07/12/2017       lzhang      186847 - Reflect grid replacement project changes
//  ---------------------------------------------------
var policyMailingId;
var generateDate;
var toBeSelectedRowId;
//if has confirmed to discard data during delete or add
var proceedConfirmed = false;

var fromDeleteEvent = false;

function validateGrid() {
    return true;
}


function mailingEventListGrid_selectRow(id) {
    var currentMailingEventGrid = getXMLDataForGridName("mailingEventListGrid");
    generateDate = currentMailingEventGrid.recordset("CGENERATEDATE").value;
    if (policyMailingId == id) {
        return;
    }
    //uncomplete data, return
    if (!fromDeleteEvent) {
        if (!validateEventAndAttributeGridBeforeChanged()) {
            return;
        }
    }
    else {
        fromDeleteEvent = false;
    }

    //policyMailing exist and no data changes
    if ((!proceedConfirmed) && (policyMailingId) && (iframeMailingRecipient.isPageGridsDataChanged) && (iframeMailingRecipient.isPageGridsDataChanged())) {
        if (!confirm(getMessage("pm.maintainPolicyMailing.unsavedData.error"))) {
            selectRowById("mailingEventListGrid", policyMailingId);
            return;
        }
    }
    proceedConfirmed = false;
    policyMailingId = id;
    setTableProperty(eval("mailingAttributeListGrid"), "selectedTableRowNo", null);
    mailingAttributeListGrid_filter("CPOLICYMAILINGID=" + id);
    var detailXmlData = getXMLDataForGridName("mailingAttributeListGrid");
    if (isEmptyRecordset(detailXmlData.recordset)) {
        hideEmptyTable(getTableForXMLData(detailXmlData));
        hideGridDetailDiv("mailingAttributeDetailDiv");
    }
    else {
        showNonEmptyTable(getTableForXMLData(detailXmlData));
        reconnectAllFields(document.forms[0]);
        hideShowElementByClassName(getObject("mailingAttributeDetailDiv"), false);
    }
    loadMailingRecipient();
}

function mailingEventListGrid_setInitialValues() {
    var path = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
        + commonGetMenuQueryString() + "&process=getInitialValuesForMailingEvent";
    new AJAXRequest("get", path, '', setInitialValuesForMailingEvent, false);
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'ADD_EVENT':
            if (!validateEventAndAttributeGridBeforeChanged()) {
                return;
            }
            if ((iframeMailingRecipient.isPageGridsDataChanged) && iframeMailingRecipient.isPageGridsDataChanged()) {
                if (!confirm(getMessage("pm.maintainPolicyMailing.unsavedData.error"))) {
                    selectRowById("mailingEventListGrid", policyMailingId);
                    break;
                }
                else {
                    proceedConfirmed = true;

                }
            }
            commonAddRow("mailingEventListGrid");

            break;
        case 'ADD_ATTRIBUTE':
            hideShowElementByClassName(getObject("mailingAttributeDetailDiv"), false);
            commonAddRow("mailingAttributeListGrid");
            setTableProperty(eval("mailingAttributeListGrid"), "selectedTableRowNo", null);
            syncSelectResendLable();
            break;
        case 'DELETE_EVENT':
            fromDeleteEvent = true;
            var mailingAttributeRecords = mailingAttributeListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (UPDATE_IND != 'D') and (CPOLICYMAILINGID= '" + policyMailingId + "')]");
            var mailingRecipientRecords = iframeMailingRecipient.mailingRecipientListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (UPDATE_IND != 'D') and (CPOLICYMAILINGID= '" + policyMailingId + "')]");
            if (mailingAttributeRecords.length > 0) {
                handleError(getMessage("pm.maintainPolicyMailing.undeletedMailingAttribute.error"));
            }
            else if (mailingRecipientRecords.length > 0) {
                handleError(getMessage("pm.maintainPolicyMailing.undeletedMailingRecipient.error"));
            }
            else {
                if ((iframeMailingRecipient.isPageGridsDataChanged) && iframeMailingRecipient.isPageGridsDataChanged()) {
                    if (!confirm(getMessage("pm.maintainPolicyMailing.unsavedData.error"))) {
                        selectRowById("mailingEventListGrid", policyMailingId);
                        break;
                    }
                    else {
                        proceedConfirmed = true;

                    }
                }
                var gridId = "mailingEventListGrid";
                commonDeleteRow(gridId);
                var officalRecordId = null;
                setTableProperty(eval("mailingAttributeListGrid"), "selectedTableRowNo", null);
            }
            break;
        case 'DELETE_ATTRIBUTE':
            var currentMailingAttriubteGrid = getXMLDataForGridName("mailingAttributeListGrid");
            var updateInd = currentMailingAttriubteGrid.recordset("UPDATE_IND").value;
            if ((updateInd != "I") && !isEmpty(generateDate)) {
                handleError(getMessage("pm.maintainPolicyMailing.deleteAttributeAfterGenerated.error"));
            }
            else {
                var gridId = "mailingAttributeListGrid";
                commonDeleteRow(gridId);
                setTableProperty(eval("mailingAttributeListGrid"), "selectedTableRowNo", null);
            }
            break;
        case 'SEARCH_MAILING':
            document.forms[0].action = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" + "&process=loadAllPolicyMailing" + "&fromPage=processPolicyMailing";
            showProcessingDivPopup();             
            submitFirstForm();
            break;
        case 'CLEAR_MAILING':
            document.forms[0].action = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" + "&process=clearAllPolicyMailing";
            showProcessingDivPopup();
            submitFirstForm();
            break;
        case 'SAVE_EVENT':
            if (!isPageGridsDataChanged() && ((!iframeMailingRecipient.mailingRecipientListGrid1) || (!iframeMailingRecipient.isPageGridsDataChanged()))) {
                return;
            }
            if (validateEventGrid() && validateAttributeGrid() && validateRecipientGrid()) {
                alternateGrid_update('mailingEventListGrid');
                alternateGrid_update('mailingAttributeListGrid');
                if (iframeMailingRecipient.mailingRecipientListGrid1) {
                    iframeMailingRecipient.alternateGrid_update('mailingRecipientListGrid');
                    var txtXml = iframeMailingRecipient.document.forms[0].mailingRecipientListGridtxtXML.value;
                    setInputFormField('mailingRecipientListGridtxtXML', txtXml);
                }
                document.forms[0].action = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" + "&process=saveAllPolicyMailing";
                showProcessingDivPopup();
                submitFirstForm();
            }
            break;

        case 'GENERATE':
            if (isEmptyRecordset(iframeMailingRecipient.mailingRecipientListGrid1.recordset)) {
                handleError(getMessage("pm.generatePolicyMailing.noPolicy.error"));
            }
            else if (iframeMailingRecipient.isPageGridsDataChanged() || isPageGridsDataChanged()) {
                handleError(getMessage("pm.maitainolicyMailing.unsavedData.error"));
            }
            else {
                var generateMailingUrl = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
                    + "process=invokeProcessMsg&fromButton=generate&policyMailingId=" + policyMailingId;
                openDivPopup("", generateMailingUrl, true, true, "", "", "", "", "", "", "", false);
            }
            break;
        case 'REPRINT':
            if (iframeMailingRecipient.isPageGridsDataChanged() || isPageGridsDataChanged()) {
                handleError(getMessage("pm.maitainolicyMailing.unsavedData.error"));
            }
            else {
                var reprintMailingUrl = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
                    + "process=invokeProcessMsg&fromButton=reprint&policyMailingId=" + policyMailingId;
                openDivPopup("", reprintMailingUrl, true, true, "", "", "", "", "", "", "", false);
            }
            break;
    }
}

function mailingAttributeListGrid_setInitialValues() {
    var currentMailingEventGrid = getXMLDataForGridName("mailingEventListGrid");
    policyMailingId = currentMailingEventGrid.recordset("ID").value;

    var url = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" +
              "policyMailingId=" + policyMailingId + "&process=getInitialValuesForMailingAttribute&date=" + new Date();

    currentlySelectedGridId = "mailingAttributeListGrid";
    // initiate async call
    new AJAXRequest("get", url, '', commonHandleOnGetInitialValues, false);
    var ajaxInfoField = null;
    try {
        ajaxInfoField = eval("ajaxInfoFor" + "productMailingId");
    }
    catch(ex) {
        ajaxInfoField = null;
    }
    if (ajaxInfoField != null) {
        getObject("productMailingId").fireEvent("onChange");
    }
    reconnectAllFields(document.forms[0]);
}

function setInitialValuesForMailingEvent(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                /* no default value found */
                return;
            }
            var selectedDataGrid = getXMLDataForGridName("mailingEventListGrid");
            /* Parse xml and get inital values(s) */
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                setRecordsetByObject(selectedDataGrid, oValueList[0]);
            }
        }
    }
}

function handleOnChange(field) {
    if (field.name == 'productMailingResendId') {
        var productMailingResendId = getObjectValue("productMailingResendId");
        if (!isEmpty(productMailingResendId)) {
            var path = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?"
                + "productMailingResendId=" + productMailingResendId + "&process=getResendDaysBySelectedResend";
            new AJAXRequest("get", path, '', getResendDaysBySelectedResend, false);
        }
        else {
            document.forms[0].resendDays.value = "0";
        }
    }
    if (field.name == 'resendType') {
        var resendType = getObjectValue("resendType");
        if (resendType == 'SPECIFICDT') {
            document.forms[0].resendDays.value = "0";
            document.forms[0].resendDays.disabled = true;
            document.forms[0].resendDate.disabled = false;
        }
        else if (resendType == 'POSTGEN') {
            if (!isEmpty(generateDate)) {
                handleError(getMessage("pm.maintainPolicyMailing.postGenNotAvailable.error"))
                var currentGrid = getXMLDataForGridName("mailingAttributeListGrid");
                currentGrid.recordset("CRESENDTYPE").value = "";
                syncToLovLabelIfExists(field);
            }

            else {
                document.forms[0].resendDate.value = "";
                document.forms[0].resendDays.disabled = false;
                document.forms[0].resendDate.disabled = true;
            }

        }

    }
    //    else if(field.name == 'productMailingId'){
    //          xPath = "//ROW[(UPDATE_IND != 'D') and (CPOLICYMAILINGID= '" + policyMailingId + "')]";
    //                mailingAttributeRecords = mailingAttibuteListGrid1.documentElement.selectNodes(xPath);
    //                for (i = 0; i < mailingAttributeRecords.length; i++) {
    //                    currentRecord = mailingAttributeRecords.item(i);
    //                        currentRecord.selectNodes("CRESENDDAYS")(0).text = "0";
    //                        currentRecord.selectNodes("UPDATE_IND")(0).text = "Y";
    //    }
}

function validateEventGrid() {
    var result = true;
    var xPath = "//ROW[(UPDATE_IND != 'D') and (DISPLAY_IND = 'Y')]";
    var mailingEventRecords = mailingEventListGrid1.documentElement.selectNodes(xPath);
    for (var i = 0; i < mailingEventRecords.length; i++) {
        var currentRecord = mailingEventRecords.item(i);
        mailingType = currentRecord.selectNodes("CPRODUCTMAILINGID")(0).text;
        rowId = currentRecord.getAttribute("id");
        if (isEmpty(mailingType)) {
            var paras = new Array("Mailing Type");
            handleError(getMessage("pm.maintainPolicyMailing.fieldRequried.error", paras));
            selectRowById('mailingEventListGrid', rowId);
            policyMailingId = rowId;
            result = false;
            break;
        }
    }
    return result;
}

function validateAttributeGrid() {
    var result = true;
    var xPath = "//ROW[(UPDATE_IND != 'D') and (DISPLAY_IND = 'Y')]";
    var mailingAttributeRecords = mailingAttributeListGrid1.documentElement.selectNodes(xPath);
    for (var i = 0; i < mailingAttributeRecords.length; i++) {
        var currentRecord = mailingAttributeRecords.item(i);
        seletedResend = currentRecord.selectNodes("CPRODUCTMAILINGRESENDID")(0).text;
        resendType = currentRecord.selectNodes("CRESENDTYPE")(0).text;
        rowId = currentRecord.getAttribute("id");
        if (isEmpty(seletedResend)) {
            var paras = new Array("Selected Resend");
            handleError(getMessage("pm.maintainPolicyMailing.fieldRequried.error", paras));
            selectRowById('mailingAttributeListGrid', rowId);
            result = false;
            break;
        }
        else if (isEmpty(resendType)) {
            var paras = new Array("Resend Type");
            handleError(getMessage("pm.maintainPolicyMailing.fieldRequried.error", paras));
            selectRowById('mailingAttributeListGrid', rowId);
            result = false;
            break;
        }
    }
    return result;
}
function validateRecipientGrid() {
    var result = true;
    var xPath = "//ROW[(UPDATE_IND != 'D') and (DISPLAY_IND = 'Y')]";
    var mailingRecipientRecords = iframeMailingRecipient.mailingRecipientListGrid1.documentElement.selectNodes(xPath);
    for (var i = 0; i < mailingRecipientRecords.length; i++) {
        var currentRecord = mailingRecipientRecords.item(i);
        name = currentRecord.selectNodes("CNAME")(0).text;
        rowId = currentRecord.getAttribute("id");
        if (isEmpty(name)) {
            handleError(getMessage("pm.maintainPolicyMailing.InvalidPolicyNo.error"));
            iframeMailingRecipient.selectRowById('mailingRecipientListGrid', rowId);
            result = false;
            break;
        }
    }
    return result;
}

function validateEventAndAttributeGridBeforeChanged() {
    var result = true;
    if (isEmpty(policyMailingId)) {
        return true;
    }
    else {
        //check mailig event
        var xPath = "//ROW[(UPDATE_IND != 'D') and (DISPLAY_IND = 'Y') and (@id= '" + policyMailingId + "')]";
        var mailingEventRecords = mailingEventListGrid1.documentElement.selectNodes(xPath);
        var currentRecord = mailingEventRecords.item(0);
        mailingType = currentRecord.selectNodes("CPRODUCTMAILINGID")(0).text;
        if (isEmpty(mailingType)) {
            var paras = new Array("Mailing Type");
            handleError(getMessage("pm.maintainPolicyMailing.unCompeletedData.error", paras));
            selectRowById('mailingEventListGrid', policyMailingId);
            result = false;
        }

        if (result) {
            //check mailing attribute
            var xPath = "//ROW[(UPDATE_IND != 'D') and (DISPLAY_IND = 'Y') and (CPOLICYMAILINGID= '" + policyMailingId + "')]";

            var mailingAttributeRecords = mailingAttributeListGrid1.documentElement.selectNodes(xPath);
            for (var i = 0; i < mailingAttributeRecords.length; i++) {
                var currentRecord = mailingAttributeRecords.item(i);

                seletedResend = currentRecord.selectNodes("CPRODUCTMAILINGRESENDID")(0).text;
                resendType = currentRecord.selectNodes("CRESENDTYPE")(0).text;
                rowId = currentRecord.getAttribute("id");
                if (isEmpty(seletedResend)) {
                    var paras = new Array("Selected Resend");
                    handleError(getMessage("pm.maintainPolicyMailing.unCompeletedData.error", paras));
                    selectRowById('mailingEventListGrid', policyMailingId);
                    selectRowById('mailingAttributeListGrid', rowId);
                    result = false;
                    break;
                }
                else if (isEmpty(resendType)) {
                    var paras = new Array("Resend Type");
                    handleError(getMessage("pm.maintainPolicyMailing.unCompeletedData.error", paras));
                    selectRowById('mailingEventListGrid', policyMailingId);
                    selectRowById('mailingAttributeListGrid', rowId);
                    result = false;
                    break;
                }

            }
        }

    }
    return result;
}
function getResendDaysBySelectedResend(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                document.forms[0].name.value = "";
                return false;
            }
            if (data.getElementsByTagName("RESENDDAYS")[0].firstChild) {
                var resendDays = data.getElementsByTagName("RESENDDAYS")[0].firstChild.data;
                document.forms[0].resendDays.value = resendDays;
            }

        }
    }

}


function loadMailingRecipient(policyNo, policyHolderName) {
    var url = getAppPath() + "/policymgr/mailingmgr/maintainPolicyMailing.do?" + commonGetMenuQueryString() + "&process=loadAllMailingRecipient" + "&policyMailingId=" + policyMailingId;
    if(!isEmpty(policyNo)){
        url = url + '&policyNoCriteria=' + policyNo;
    }
    if(!isEmpty(policyHolderName)){
        url = url + '&nameCriteria=' + policyHolderName;
    }

    getObject("iframeMailingRecipient").src = url;
}
//-----------------------------------------------------------------------------
// Overwrite hideShowForm
//-----------------------------------------------------------------------------
function hideShowForm() {
    // Get the currently selected grid
    var currentGrid = "mailingEventListGrid";
    var currentTbl = getTableForXMLData(getXMLDataForGridName(currentGrid));
    var gridDetailDivId = getTableProperty(currentTbl, "gridDetailDivId");

    if (isStringValue(gridDetailDivId)) {
        if (hasObject(gridDetailDivId))
            hideShowElementByClassName(getSingleObject(gridDetailDivId),
                    ( (getTableProperty(currentTbl, "hasrows") == true)) ? false : true);
    }
}
//for goto error
function getParentGridId() {
    return "mailingEventListGrid";
}

function getChildGridId() {
    return "mailingAttributeListGrid";
}
//load iframe
function handleOnLoad() {
    selectRowById('mailingEventListGrid', toBeSelectedRowId);
}

function handleReadyStateReady(table) {
    //sync lable for select resend
    if (table.id == "mailingAttributeListGrid") {
        syncSelectResendLable();
    }
}
function syncSelectResendLable() {
    var mailingAttributeRecords = mailingAttributeListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (UPDATE_IND != 'D') and (CPOLICYMAILINGID= '" + policyMailingId + "')]");
    for (i = 0; i < mailingAttributeRecords.length; i++) {
        var currentRecord = mailingAttributeRecords.item(i);
        var selectResendValue = currentRecord.selectNodes("CPRODUCTMAILINGRESENDID")(0).text;
        currentRecord.selectNodes("CPRODUCTMAILINGRESENDIDLOVLABEL")(0).text = getLableForSelectResend(selectResendValue);
    }
}
function getLableForSelectResend(value) {
    var text = value;
    var selectResendOptions = getObject('productMailingResendId').options;
    for (var i = 0; i < selectResendOptions.options.length; i++) {
        if (selectResendOptions.options[i].value == value) {
            text = selectResendOptions.options[i].text;
            break;
        }
    }
    return text;
}
