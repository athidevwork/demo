// Common javascript file.
//
// (C) 2017 Delphi Technology, inc. (dti)
// Date:   August 22, 2017
// Author: kshen
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 02/21/2018       mlm         191625 - Refactored to support IE Edge mode in parseXML().
//-----------------------------------------------------------------------------

/**
 * The common method to handle XML.
 * The function will parse xml document to arrays of javascript object. If the parameter textContentHandler is provided,
 * the system will use it to handle the text content of xml element. If textContentHandler is not provided, the method
 * will try to get the method commonTextContentHandlerForParseXML to process text content value. Otherwise, the text
 * content value will not be changed.
 *
 * @param xmlDoc
 * @param textContentHandler Optional parameter. It's a function to process textContent of elements.
 * @returns {Array}
 */
function parseXML(xmlDoc, textContentHandler) {
    var rsArray = [];

    var handler = null;
    if (typeof textContentHandler != "undefined" && textContentHandler != null) {
        handler = textContentHandler;
    } else if (typeof commonTextContentHandlerForParseXML != "undefined") {
        handler = commonTextContentHandlerForParseXML;
    }

    root = xmlDoc.documentElement;
    if (root == null) {
        // report error if Ajax response is null
        alert(getMessage("appException.unexpected.error"));
    }
    else {
        // parse values in each row
        var rows = root.getElementsByTagName("ROW");
        if (rows != null) {
            var rowCount = rows.length;
            if (rowCount > 0) {
                var colCount = rows.item(0).childNodes.length;

                for (var i = 0; i < rowCount; i++) {
                    /* Create object for each row */
                    var oRow = {};
                    var row = rows.item(i);
                    /* Loop through columns */
                    for (var j = 0; j < colCount; j++) {
                        if (dti.oasis.node.isElementNode(row.childNodes.item(j))) {
                            var colName = row.childNodes.item(j).tagName;
                            var dataValue = $(row.childNodes.item(j)).text();

                            if (handler != null) {
                                dataValue = handler(dataValue);
                            }

                            oRow[colName] = dataValue;
                        }
                    }
                    rsArray[i] = oRow;
                }
            }
        }
    }

    return rsArray;
}

/**
 * common method to navigate to target source
 *
 * @param sourceNo - policy no, claim no, case no, etc
 * @param sourceTableName - the target system
 * @param sourcePk - source pk, like claimPk, cmPolicy_cmPolicyPk, cmInvoiceId, cmBatchId, or velocity policy pk
 * @param openInNewWindow - default to true
 * @param fullWindow - default to false
 */
function commonGoToSource(sourceNo, sourceTableName, sourcePk, openInNewWindow, fullWindow) {
    var functionExists;
    if (!isStringValue(sourceTableName)) {
        alert("Source not available.");
        return;
    } else {
        var isOkToProceed = true;
        functionExists = eval("window.handleGoToSource");
        if (functionExists) {
            var returnValue = handleGoToSource(sourceNo, sourceTableName, sourcePk);
            isOkToProceed = nvl(returnValue, true);
        }
        if (!isOkToProceed) {
            return;
        }
    }

    var actionUrl = '';
    sourceTableName = sourceTableName.toUpperCase();
    if (sourceTableName == 'OCCURRENCE') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/cmCaseSearch.do?process=globalSearch&occurrence_occurrenceNo=" + sourceNo;
    } else if (sourceTableName == 'CLAIM') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/cmClaimSearch.do?process=globalSearch&claimNo=" + sourceNo;
    } else if (sourceTableName == 'CLAIM_ENTRY_LOG') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/createClaim.do?process=loadClaimViaExistedLog&claimEntryLogId=" + sourcePk;
    } else if (sourceTableName == 'POLICY' || sourceTableName == 'OASIS PM') {
        actionUrl = getTopNavApplicationUrl("Policy") + "/policymgr/findPolicy.do?isGlobalSearch=Y&policyNoCriteria=" + sourceNo + "&termStatusCode=ALL&process=findAllPolicy";
    } else if(sourceTableName == 'OASIS WCPM') {
        actionUrl = getTopNavApplicationUrl("WCPM") + "/SearchPolicy?policySearch=" + sourceNo;
        actionUrl += "&_dialog=false";
    } else if (sourceTableName == 'RISK') {
        actionUrl = getTopNavApplicationUrl("Policy") + "/riskmgr/maintainRisk.do?process=getRiskSumB&policyNo=" + sourceNo;
        new AJAXRequest("get", actionUrl, "", function(ajax) {
            if (ajax.readyState == 4) {
                if (ajax.status == 200) {
                    var data = ajax.responseXML;
                    if (!handleAjaxMessages(data, null)) {
                        return;
                    }
                    var oValueList = parseXML(data);
                    if (oValueList.length > 0) {
                        if (oValueList[0]["riskSumB"] == 'Y') {
                            actionUrl = getTopNavApplicationUrl("Policy") + "/riskmgr/viewRiskSummary.do?&policyNo=" + sourceNo;
                        } else {
                            actionUrl = getTopNavApplicationUrl("Policy") + "/riskmgr/maintainRisk.do?&policyNo=" + sourceNo;
                        }
                    }
                }
            }
        }, false);
    } else if (sourceTableName == 'CM_POLICY' || sourceTableName == 'CM_RISK' || sourceTableName == 'CLAIM_POLICY') {
        actionUrl = getTopNavApplicationUrl("Claims") + "/maintainPolicy.do?process=loadPolicy&cmPolicy_cmPolicyPk=" + sourcePk;
    } else if (sourceTableName == 'BILLING_ACCOUNT') {
        actionUrl = getTopNavApplicationUrl("FM") + "/fullinquirymgr/viewAllTransactionsForAccount.do?accountNo=" + sourceNo;
    } else if (sourceTableName == 'VELOCITY_POLICY' || sourceTableName == 'VELOCITY_DIARY' || sourceTableName == 'AGREEMENT') {
        if (!isStringValue(getVelocityAppPath()) || !isStringValue(getViewVelocityPolicyURL())) {
            alert('Velocity Policy URL not configured.');
            return;
        } else {
            actionUrl = getVelocityAppPath() + getViewVelocityPolicyURL() + sourcePk;
            openInNewWindow = true;
        }
    } else {
        alert("Source not available: " + sourceTableName);
    }

    if (actionUrl != '') {
        openSourceWindow(actionUrl, openInNewWindow, fullWindow);
    }
}

//-----------------------------------------------------------------------------
// Commond method to handle get record exists informaton by Ajax
//-----------------------------------------------------------------------------
function commonHandleOnGetRecordExists(ajax) {
    // Loop through any italics items for the page
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            var selectedDataGrid;
            var cacheRecord;
            if (italicsCacheValueArray.length > 0) {
                selectedDataGrid = getXMLDataForGridName(italicsCurrentGridName);
                cacheRecord = getCacheItalicsValue(italicsCurrentGridName + selectedDataGrid.recordset("ID").value);
            }
            // do nothing if we don't have initial values or we got error
            if (!handleAjaxMessages(data, null))
                return;

            // parse and set initial values
            var oValueList = parseXML(data);
            if (cacheRecord != null) {
                setRecordExistsStyle(cacheRecord, oValueList);
            } else {
                setRecordExistsStyle("", oValueList);
            }
        }
    }
}

function commonOnSetButtonItalics(cached) {
    if (italicsCacheValueArray.length > 0) {
        var selectedDataGrid = getXMLDataForGridName(italicsCurrentGridName);
        var cacheRecord = getCacheItalicsValue(italicsCurrentGridName + selectedDataGrid.recordset("ID").value);
        if (cacheRecord != null) {
            if (cached) {
                setRecordExistsStyle(cacheRecord);
                return;
            }
        }
    }
    if (!isEmpty(italicsFieldIdList)) {
        var existsUrl = getRecordExistsUrl() +
            "&recordExistsFieldId=" + italicsFieldIdList + "&date=" + new Date();

        // initiate call
        var ajaxResponseHandler = "commonHandleOnGetRecordExists";
        new AJAXRequest("get", existsUrl, "", eval(ajaxResponseHandler), false);
    }
}


/*
 Validate the specified grid, returning true if there were no validations indicating it is ok to proceed.
 If there were any validation errors, false is returned.
 All hidden divs are ignored.
 */
var validateFieldId = "";
var validateGridId = "";
var validateRowId = "";

function commonValidateGrid(gridId, handleErrorFunction) {
    var proceed = true;
    if (gridId && getTableProperty(getTableForGrid(gridId), "hasrows")) {
        //make a call to page handler to get any overridden validation
        var functionExists = eval("window.handleValidateGrid");
        if (functionExists) {
            proceed = handleValidateGrid(gridId);
        } else {
            validateGridId = gridId;

            var dataGrid = getXMLDataForGridName(gridId);
            var rowIndex = 0;
            var currentRowId = getSelectedRow(gridId);

            first(dataGrid);
            while (!dataGrid.recordset.eof) {
                var upd = dataGrid.recordset("UPDATE_IND").value;
                validateRowId = dataGrid.recordset("ID").value;
                if (upd == 'I' || upd == 'Y') {
                    if (dti.oasis.page.useJqxGrid()) {
                        selectRowById(gridId, validateRowId);
                    }
                    proceed = validate(document.forms[0], true, (handleErrorFunction ? handleErrorFunction : "handleError"), rowIndex + 1);
                }

                if (!proceed) {
                    return;
                }
                rowIndex = rowIndex + 1;
                next(dataGrid);
            }

            selectRowById(gridId, currentRowId, rowIndex);
        }
    }
    return proceed;
}

/*
 Validate the form, returning true if there were no validations indicating it is ok to proceed.
 If there were any validation errors, false is returned.
 All hidden divs are ignored.
 */
function commonValidateForm() {
    return validate(document.forms[0], true, "handleError");
}
