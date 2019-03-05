//-----------------------------------------------------------------------------
/*
 Description: js file for maintainEntityGlance.jsp

 Author: xjli
 Date: Sept 19, 2011


 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 09/19/2011       xjli        121133: Blue Sky - Entity at a glance new screen.
 10/30/2013       xnie        148240: Added function goToPolicy() to open policy information page.
 11/06/2014       kxiang      158411: Modified function goToSource() to change the send url.
 07/13/2016       Elvin       Issue 177515: change goToSource to commonGoToSource
 10/22/2018       dpang       195835 - Grid replacement.
 11/13/2018       wreeder     196147 - Added $.when(dti.oasis.grid.getLoadingPromise(gridId, …)).then(function() {   …}); surrounding logic that depends on the grid loading to be complete
 -----------------------------------------------------------------------------
 (C) 2011 Delphi Technology, inc. (dti)
 */

function setNoteFileImg(fieldId) {
    if (getObject(fieldId)) {
        var val = getObjectValue(fieldId);
        getObject(fieldId + "ROSPAN").className = "notes";
        getObject(fieldId + "ROSPAN1").innerText = '';
        var imageElement = document.createElement("img");
        imageElement.name = "noteImg";
        imageElement.src = getCorePath() + ((val == "Yes" || val == "Y") ? notesImage : noNotesImage);
        imageElement.border = "0";
        getObject(fieldId + "ROSPAN1").appendChild(imageElement);
    }
}

function filterChildGrids(gridId, rowId) {
    // alert('filterChildGrids--gridId='+gridId+'--rowId--'+rowId);

    if (gridId == "relationshipGrid") {
        // Filter second grid "Form versions"
        // Reset "selectedTableRowNo" for second grid before filter
        $.when(dti.oasis.grid.getLoadingPromise("claimGrid")).then(function () {
            setTableProperty(eval("claimGrid"), "selectedTableRowNo", null);
            //claimGrid_filter();
            //policyGrid_filter();
            // Show/hide grid form
            //alert(isEmptyRecordset(claimGrid1.recordset))
            if (isEmptyRecordset(claimGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(claimGrid1));
                // Filter out third grid as empty grid since it's parent grid is empty
                filterChildGrids("claimGrid", claimGrid1.recordset("CCLAIMID").value);
            }
            else {
                showNonEmptyTable(getTableForXMLData(claimGrid1));
                //reconnectAllFields(document.forms[0]);
            }
        });

        $.when(dti.oasis.grid.getLoadingPromise("policyGrid")).then(function () {
            // alert(isEmptyRecordset(policyGrid1.recordset))
            if (isEmptyRecordset(policyGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(policyGrid1));
                // Filter out third grid as empty grid since it's parent grid is empty
                filterChildGrids("policyGrid", policyGrid1.recordset("CPOLICYID").value);
            }
            else {
                showNonEmptyTable(getTableForXMLData(policyGrid1));
                //reconnectAllFields(document.forms[0]);
            }
        });

    } else if (gridId == "claimGrid") {

        $.when(dti.oasis.grid.getLoadingPromise("participantGrid")).then(function () {
            // Filter third grid "questions in version"
            // Reset "selectedTableRowNo" for third grid before filter
            setTableProperty(eval("participantGrid"), "selectedTableRowNo", null);
            if (rowId != '') {
                participantGrid_filter("CCMCLAIMID=" + rowId);
            } else {
                participantGrid_filter("CCMCLAIMID=-4321");
            }

            // Show/hide grid form
            if (isEmptyRecordset(participantGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(participantGrid1));
            }
            else {
                showNonEmptyTable(getTableForXMLData(participantGrid1));
            }
        });

    } else if (gridId == "policyGrid") {

        $.when(dti.oasis.grid.getLoadingPromise("transactionGrid")).then(function () {
            setTableProperty(eval("transactionGrid"), "selectedTableRowNo", null);
            if (rowId != '') {
                var nodeValue = policyGrid1.recordset('CPOLICYTERMBASEHISTORYID').value;
                transactionGrid_filter("CTRANSPOLICYTERMHISTORYID=" + nodeValue);
                //transactionGrid_filter("CPOLICYTERMHISTORYID=" + rowId);
            } else {
                transactionGrid_filter("CTRANSPOLICYTERMHISTORYID=-4321");
                //transactionGrid_filter("CPOLICYTERMHISTORYID=-4321");
            }
            // Show/hide grid form
            if (isEmptyRecordset(transactionGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(transactionGrid1));
            }
            else {
                showNonEmptyTable(getTableForXMLData(transactionGrid1));
            }
        });

    } else if (gridId == "financialGrid") {

        $.when(dti.oasis.grid.getLoadingPromise("financialFormGrid")).then(function () {
            setTableProperty(eval("financialFormGrid"), "selectedTableRowNo", null);
            if (rowId != '') {
                financialFormGrid_filter("CFMUFESOURCERECORDID=" + rowId);
            } else {
                financialFormGrid_filter("CFMUFESOURCERECORDID=-4321");
            }
            // Show/hide grid form
            if (isEmptyRecordset(financialFormGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(financialFormGrid1));
            }
            else {
                showNonEmptyTable(getTableForXMLData(financialFormGrid1));
            }
        });

    } else if (gridId == "transactionGrid") {

        $.when(dti.oasis.grid.getLoadingPromise("transactionFormGrid")).then(function () {
            setTableProperty(eval("transactionFormGrid"), "selectedTableRowNo", null);
            if (rowId != '') {
                transactionFormGrid_filter("CUFESOURCERECORDID=" + rowId);
                //transactionFormGrid_filter("CPOLICYTERMHISTORYID=" + rowId);
            } else {
                transactionFormGrid_filter("CUFESOURCERECORDID=-4321");
                //transactionFormGrid_filter("CPOLICYTERMHISTORYID=-4321");
            }
            // Show/hide grid form
            if (isEmptyRecordset(transactionFormGrid1.recordset)) {
                hideEmptyTable(getTableForXMLData(transactionFormGrid1));
            }
            else {
                showNonEmptyTable(getTableForXMLData(transactionFormGrid1));
            }
        });

    }
}
function financialGrid_selectRow(id) {
    var acctNo = financialGrid1.recordset("CACCOUNTNO").value;
    var url = getTopNavApplicationUrl("FM")
            + "/fullinquirymgr/viewInvoiceForAccount.do?"
            + "accountNo=" + acctNo
            + "&headerHidden=Y"
            + "&isAccountHeaderHidden=Y"
            + "&isTabMenuHidden=Y"
            + "&date=" + new Date();
    getObject("iframeInvoice").src = url;
}
function handleGoToSource(sourceNo, sourceTableName, sourcePk) {
    var isOkToProceed = true;
    if (sourceTableName == 'OCCURRENCE') {
        if ("N" == claimGrid1.recordset("CCLAIMACCESSIBLEFLAG").value) {
            alert(getMessage("ci.claim.restrict.message.noAuthority.case"));
            isOkToProceed = false;
        }
    } else if (sourceTableName == 'CLAIM') {
        if ("N" == claimGrid1.recordset("CCLAIMACCESSIBLEFLAG").value) {
            alert(getMessage("ci.claim.restrict.message.noAuthority.claim"));
            isOkToProceed = false;
        }
    }
    return isOkToProceed;
}
function viewUFEForms(filePath, fileName) {
    var finalURL = getCSPath() + "/outputmgr/processOutput.do?process=viewDocument&decodedFileFullPath=" + filePath + '\\' + fileName;
    window.open(finalURL, "", "location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
}

function viewNoteSource(sourceRecordId, sourceTableName, noteGroupCode, openInDiv, noteReload) {
    if (sourceTableName == 'ENTITY') {
        sourceRecordId = document.forms[0].pk.value;
    }

    if (window.loadNotesWithReloadOption) {
        // Function loadNotesWithReloadOption is available from csLoadNotes.js.
        loadNotesWithReloadOption(sourceRecordId, sourceTableName, noteGroupCode, openInDiv, false, 'entityGlanceNotesExist');
    }
}
function viewFileSource(sourceRecordId, sourceTableName) {
    if (sourceTableName == 'ENTITY') {
        sourceRecordId = document.forms[0].pk.value;
    }
    var finalURL = getCSPath() + "/csAttach.do?sourceRecordFk=" + sourceRecordId +
            "&sourceTableName=" + sourceTableName;
    openDivPopup("", finalURL, true, true, 10, 10, 850, 700, "", "", "", true, "", "", true);
}

function entityGlanceNotesExist(notesExist, srcTblName, srcRecFK) {
    switch (srcTblName) {
        case 'ENTITY':
            if (srcRecFK != document.forms[0].pk.value) {
                return;
            }
            var testObj = getObject("entityNotesExistB");

            if (testObj) {
                testObj.className = "notes";
                testObj = getSingleObject('entityNotesExistBROSPAN1');

                if (testObj) {
                    testObj.childNodes[0].src = getCorePath() + (notesExist ? notesImage : noNotesImage);
                    document.forms[0].entityNotesExistB.value = (notesExist ? 'Y' : 'N');
                }
            }
            break;
        case 'CLAIM':
            if (srcRecFK == claimGrid1.recordset("id").value) {
                claimGrid1.recordset("CCLAIMNOTESEXISTBLOVLABEL").value = getExistsValue(notesExist);
            }
            break;
        case 'POLICY':
            if (srcRecFK == policyGrid1.recordset("CPOLICYID").value) {
                policyGrid1.recordset("CPOLICYNOTESEXISTBLOVLABEL").value = getExistsValue(notesExist);
            }
            break;
        case 'ACCOUNT':
            if (srcRecFK == financialGrid1.recordset("id").value) {
                financialGrid1.recordset("CACCOUNTNOTESEXISTBLOVLABEL").value = getExistsValue(notesExist);

            }
            break;
    }
}

function entityGlanceFilesExist(srcTblName, srcRecFK, filesExist) {
    filesExist = filesExist == "Y";

    switch (srcTblName) {
        case 'ENTITY':
            if (srcRecFK != document.forms[0].pk.value) {
                return;
            }

            var testObj = getObject("entityFilesExistB");
            if (testObj) {
                testObj.className = "notes";
                testObj = getSingleObject('entityFilesExistBROSPAN1');

                if (testObj) {
                    testObj.childNodes[0].src = getCorePath() + (filesExist ? notesImage : noNotesImage);
                    document.forms[0].entityFilesExistB.value = (filesExist ? 'Y' : 'N');
                }
            }
            break;
        case 'CLAIM':
            if (srcRecFK == claimGrid1.recordset("id").value) {
                claimGrid1.recordset("CCLAIMFILESEXISTBLOVLABEL").value = getExistsValue(filesExist);
            }
            break;
        case 'POLICY':
            if (srcRecFK == policyGrid1.recordset("CPOLICYID").value) {
                policyGrid1.recordset("CPOLICYFILESEXISTBLOVLABEL").value = getExistsValue(filesExist);
            }
            break;
        case 'ACCOUNT':
            if (srcRecFK == financialGrid1.recordset("id").value) {
                financialGrid1.recordset("CACCOUNTFILESEXISTBLOVLABEL").value = getExistsValue(filesExist);
            }

            break;
    }
}

function getExistsValue(exist) {
    return exist ? "Yes" : "No";
}