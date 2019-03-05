//-----------------------------------------------------------------------------
// Functions to support Relationships
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 09/17/2010    Kenney      Issue#112060: Modified filterRelationshipList
// 09/23/2010    kshen       Issue 102450: Added bulk expire relation function.
// 07/01/2013    hxk         Issue 141840
//                           If entity is readonly, don't allow edit.
// 11/21/2014    Elvin       Issue 157913: encode entity name when goToRelationshipModify
// 06/08/2017    jdingle     Issue 190314. Save performance.
// 10/11/2018    dmeng       Grid replacement
// 11/09/2018    Elvin       Issue 195835: grid replacement
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function testgrid_selectRow(pk) {
}

function btnClick(btnID) {
    if (btnID == 'add') {
        openEntitySelectWinFullName("entityChildFK", "nameComputed", 'validateChildEntity()');
    } else if (btnID == 'refresh') {
        refreshPage();
    } else if (btnID == 'save') {
        setObjectValue("process", "expireRelationships");
        var keys = getSelectedKeysString(testgrid1, "SELECT_IND", "^");
        if (!isStringValue(keys)) {
            alert(getMessage("ci.CIRelationship.selectRow.msg"));
            return;
        }

        if (!isStringValue(getObjectValue("expDate"))) {
            alert(getMessage("ci.CIRelationship.expireDate.required.msg"));
            return;
        }

        setObjectValue("selectedRecordIds", keys);
        submitFirstForm();
    }
}

// this function will be called by the CIS Search popup when an entity is selected
function validateChildEntity() {
    var pk = getObjectValue("pk");
    var entityChildFK = getObjectValue("entityChildFK");
    if (pk == entityChildFK) {
        alert(getMessage("ci.entity.message.client.relate"));
        return;
    }
    goToRelationshipModify();
}

function editRelationship(entityRelationPK) {
    if (isEntityReadOnlyYN == "Y")  {
        return;
    }
    goToRelationshipModify(entityRelationPK);
}

function goToRelationshipModify(entityRelationPK) {
    var path = getCISPath() + "/ciRelationshipModify.do";
    path += "?pk=" + getObjectValue("pk");
    if (entityRelationPK) {
        path += "&entityRelationId=" + entityRelationPK;
    }
    path += "&entityChildId=" + getObjectValue("entityChildFK");
    path += "&entityParentId=" + getObjectValue("pk");
    path += "&nameComputed=" + encodeURIComponent(getObjectValue("nameComputed"));
    window.open(path, 'RelationshipModify', 'top=20,left=20, width=900,height=450,innerHeight=600,innerWidth=875,scrollbars');
}

function refreshPage() {
    showProcessingImgIndicator();
    setObjectValue("process", "loadAllRelationShip");
    submitFirstForm();
}

//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csloadnotes.js.
// loadNotes(sourceRecordFk, sourceTableName, noteGroupCode)
//-----------------------------------------------------------------------------
function openNotesForCISRelation(pk) {
    if (window.loadNotesWithReloadOption) {
        loadNotesWithReloadOption(pk, 'ENTITY_RELATION', 'ENTITY_REL', false, false, 'handleNotesExist');
    } else {
        alert(getMessage("ci.entity.message.notesError.notAvailable"));
    }
}

//-----------------------------------------------------------------------------
// Handle Notes Exist.
//-----------------------------------------------------------------------------
function handleNotesExist(notesExist, sourceTableName, sourceRecordId) {
    if (sourceRecordId == testgrid1.recordset("id").value) {
        if (notesExist) {
            testgrid1.recordset("CNOTESIND").value = "Yes";
        } else {
            testgrid1.recordset("CNOTESIND").value = "No";
        }
    }
}

function CIRelationshipForm_btnClick(btnID) {
    switch (btnID) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            break;
    }
}

function displayRelationIndicatorImage(row, columnfield, value, defaulthtml, columnproperties) {
    return "<div align='center'><img src='" + getCorePath() + "/" + value + "' style='margin-top:5px'/></div>";
}

function handleGetCustomPageOptions() {
    return dti.oasis.page.newCustomPageOptions().cellsRenderer("testgrid", "CREVERSERELATIONINDICATOR", displayRelationIndicatorImage);
}