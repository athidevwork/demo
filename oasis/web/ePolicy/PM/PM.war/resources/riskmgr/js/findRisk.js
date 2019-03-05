// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/26/2012       awu       130527 -  Modified function handleOnButtonoClick() SEARCH case, added condition CENTITYTYPE = ''
//                                      when search risk by organization name.
// 10/16/2018       clm       195889 -  Grid replacement, add function addRecordToRiskNameListForJqxGrid
//-----------------------------------------------------------------------------
var currentRiskId = "";
function riskNameListGrid_selectRow(id) {
    currentRiskId = id;
}
function handleOnKeyDown(field, event){
    var evt = fixEvent(event);
    var code = evt.keyCode;
    if (code == 13) {
        // Automatically search for the matching policies if the "Enter" key is pressed.
        commonOnButtonClick('SEARCH');
        return false;
    }
    return true;
}
function handleOnButtonClick(btn) {
    var parentWindow = getParentWindow();
    switch (btn) {
        case 'SEARCH':
            if (validateSearchCriteria()) {
                var refFirstName = (trim(getObjectValue("firstName"))).toLowerCase();
                var refLastName = (trim(getObjectValue("lastName"))).toLowerCase();
                var refOrganizationName = (trim(getObjectValue("organizationName"))).toLowerCase();
                origriskListGrid1 = parentWindow.riskListGrid1;
                if (!dti.oasis.page.useJqxGrid()) {
                    origriskNameListGrid1 = riskNameListGrid1.cloneNode(true);
                }
                doc = origriskNameListGrid1.documentElement;
                //remove existing record
                var modXML = doc.selectNodes("//ROW[DISPLAY_IND = 'Y']");
                for (i = 0; i < modXML.length; i++)
                {
                    doc.removeChild(modXML.item(i));
                }
                // for person name
                if (!isEmpty(refLastName)) {
                    modXML1 = origriskListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (CENTITYTYPE = 'P')]");
                    for (i = 0; i < modXML1.length; i++) {
                        currentRecord = modXML1.item(i);
                        var sourceFirstName = (currentRecord.selectNodes("CRISKFIRSTNAME").item(0).text).toLowerCase();
                        var sourceLastName = (currentRecord.selectNodes("CRISKLASTNAME").item(0).text).toLowerCase();
                        if ((sourceFirstName.indexOf(refFirstName) != -1) && (sourceLastName.indexOf(refLastName) != -1)) {
                            if (!dti.oasis.page.useJqxGrid()) {
                                addRecordToRiskNameList(origriskNameListGrid1, currentRecord);
                            }
                            else {
                                addRecordToRiskNameListForJqxGrid(currentRecord);
                            }
                        }
                    }
                }
                //for organization name
                else
                    if (!isEmpty(refOrganizationName)) {
                        modXML2 = origriskListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y') and (CENTITYTYPE = 'O' or CENTITYTYPE = '')]");
                        for (i = 0; i < modXML2.length; i++) {
                            currentRecord = modXML2.item(i);
                            var sourceOrganizationName = (currentRecord.selectNodes("CRISKNAME").item(0).text).toLowerCase();
                            if (sourceOrganizationName.indexOf(refOrganizationName) != -1) {
                                if (!dti.oasis.page.useJqxGrid()) {
                                    addRecordToRiskNameList(origriskNameListGrid1, currentRecord);
                                }
                                else {
                                    addRecordToRiskNameListForJqxGrid(currentRecord);
                                }
                            }
                        }
                    }
                var searchResult = origriskNameListGrid1.documentElement.selectNodes("//ROW[(DISPLAY_IND = 'Y')]");

                if (searchResult.length == 1) {
                    currentRiskId = searchResult.item(0).getAttribute("id");
                    parentWindow.selectRowById('riskListGrid', currentRiskId);
                    //commonOnButtonClick('CLOSE_RO_DIV');
                }
                else {
                    if (dti.oasis.page.useJqxGrid()) {
                        riskNameListGridGridInfo.data.columnNames = ["CRISKID", "CRISKNAME", "CENTITYID",
                            "UPDATE_IND", "DISPLAY_IND", "EDIT_IND", "OBR_ENFORCED_RESULT", "@id", "@index", "@col"];
                    }
                    document.forms[0].txtXML.value = getChanges(origriskNameListGrid1);
                    document.forms[0].action = getAppPath() +
                                               "/riskmgr/findRisk.do?" + commonGetMenuQueryString() + "&process=loadAllRisk";
                    showProcessingDivPopup();
                    submitFirstForm();
                }
            }
            break;
        case 'DONE':
            if (currentRiskId != "") {
                parentWindow.selectRowById('riskListGrid', currentRiskId);
            }
            commonOnButtonClick('CLOSE_RO_DIV');
            break;
        case 'CANCEL':
            commonOnButtonClick('CLOSE_RO_DIV');
            break;
    }
}

//validate search input criteria
function validateSearchCriteria() {
    var firstName = getObjectValue("firstName");
    var lastName = getObjectValue("lastName");
    var organizationName = getObjectValue("organizationName");
    if ((isEmpty(trim(firstName))) && (isEmpty(trim(lastName))) && (isEmpty(trim(organizationName)))) {
        handleError(getMessage("pm.searchRisk.noSearchCriteria.error"));
        return false;
    }
    else if ((!isEmpty(trim(organizationName))) && ((!isEmpty(trim(firstName))) || (!isEmpty(trim(lastName) )))) {
        handleError(getMessage("pm.searchRisk.personAndOrganization.error"));
        return false;
    }
    else if (!isEmpty(trim(firstName)) && (isEmpty(trim(lastName))) && (isEmpty(trim(organizationName)))) {
        handleError(getMessage("pm.searchRisk.onlyFirstName.error"));
        return false;
    }
    return true;
}

//add a node to riskNameList
function addRecordToRiskNameList(riskNameList, currentRecord) {
    var row = riskNameList.createElement("ROW");

    row.setAttributeNode(currentRecord.getAttributeNode("id").cloneNode(true));
    row.setAttributeNode(currentRecord.getAttributeNode("index").cloneNode(true));
    row.setAttributeNode(currentRecord.getAttributeNode("col").cloneNode(true));
    row.setAttribute("col", "1");
    row.appendChild(currentRecord.selectNodes("CRISKNAME").item(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CRISKID").item(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("CENTITYID").item(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("UPDATE_IND").item(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("DISPLAY_IND").item(0).cloneNode(true));
    row.appendChild(currentRecord.selectNodes("EDIT_IND").item(0).cloneNode(true));
    riskNameList.documentElement.appendChild(row);
}

function addRecordToRiskNameListForJqxGrid(currentRecord) {
    riskNameListGridGridInfo.data.rawData.push(
            {
                "CRISKID": currentRecord.selectNodes("CRISKID")[0].text,
                "CRISKNAME": currentRecord.selectNodes("CRISKNAME")[0].text,
                "CENTITYID": currentRecord.selectNodes("CENTITYID")[0].text,
                "UPDATE_IND": currentRecord.selectNodes("UPDATE_IND")[0].text,
                "DISPLAY_IND": currentRecord.selectNodes("DISPLAY_IND")[0].text,
                "EDIT_IND": currentRecord.selectNodes("EDIT_IND")[0].text,
                "@id": currentRecord.getAttribute("id"),
                "@index": currentRecord.getAttribute("index"),
                "@col": currentRecord.getAttribute("col")
            });
}
