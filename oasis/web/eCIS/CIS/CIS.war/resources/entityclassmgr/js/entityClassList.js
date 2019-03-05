//-----------------------------------------------------------------------------
//  Description: Javascript file for Entity Class List Page
//
//  Author: unknown
//  Date: unknown
//
//
//  Revision Date    Revised By  Description
//  ---------------------------------------------------
//  05/10/2007       kshen       Modified popup window size.
//  09/18/2009       Jacky       Add function 'openNotesForListClass' for issue #98128
//  07/01/2013       hxk         Issue 141840
//                               If entity is readonly, don't allow modify.
//  07/18/2013       hxk         Issue 141840
//                               If the entity is readonly, we don't want to check
//                               for changes.
//  01/16/2014       hxk         Issue 151361
//                               1)  Add dummy commonOnChange so that we do not set
//                                   isChanged when we select filter criteria.
//  12/20/2017       ylu         Issue 190193: accommodate code change with
//                               ADD_SELECT_OPTION] in Class Code filter LOV
//  09/17/2018       ylu         Issue 195835: grid replacement:
//                               1).use getObjectValue & setObjectValue
//                               2).use commonDeleteRow().
//                               3).remove unused validateGrid().
//-----------------------------------------------------------------------------

currentlySelectedGridId = "entityClassListGrid";
function handleOnSubmit(action) {
    var proceed = true;

    switch (action) {
        case 'query':
            if (isPageGridsDataChanged()) {
                if (!confirm(ciDataChangedConfirmation) ) {
                    return false;
                }
            }
            setObjectValue("process", "loadAllEntityClass");
            break;

        case 'refresh':
            if (isPageGridsDataChanged()) {
                if (!confirm(ciDataChangedConfirmation) ) {
                    return false;
                }
            }
            setObjectValue("process", "loadAllEntityClass");
            break;

        case 'saveAllClassifications':
            setObjectValue("process", "saveAllEntityClass");
            break;
        default:
            proceed = false;
    }
    return proceed;
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'add':
            openAddWin();
            break;
        case 'clear':
            clearCriteria();
            break;
        case 'delete':
            deleteEntityClasses();
            break;
        case 'edit':
            editEntityClass();
            break;
    }
}

//-----------------------------------------------------------------------------
// Handles button click events on the form.
//-----------------------------------------------------------------------------
function CIEntityClassListForm_btnClick(asBtn) {
    switch(asBtn) {
        case 'SELECT':
          entityClassListGrid_updatenode("CSELECT_IND", -1);
          break;
        case 'DESELECT':
          entityClassListGrid_updatenode("CSELECT_IND", 0);
          break;
    }
}

//-----------------------------------------------------------------------------
// Clear the filter criteria.
//-----------------------------------------------------------------------------
function clearCriteria() {
    // class code is drop down list, so its empty value is "-1".
    setObjectValue("filterCriteria_entityClassCode", "");
}

function deleteEntityClasses() {
    var dataArray = getSelectedKeys(entityClassListGrid1);
    for (var i = 0; i < dataArray.length; i++) {
        selectRowById("entityClassListGrid", dataArray[i]);

        var userCanUpdate = entityClassListGrid1.recordset("CUSERCANUPDATEB").value;
        var classDesc = entityClassListGrid1.recordset("CENTITYCLASSCODEDESC").value;

        if (userCanUpdate != null && userCanUpdate !== 'Y') {
            alert(getMessage("ci.entity.message.classification.delete", new Array(classDesc)));
            return;
        }
    }
    commonDeleteRow("entityClassListGrid");
}

function editEntityClass() {
    var dataArray = getSelectedKeys(entityClassListGrid1);
    if (dataArray.length != 1) {
        alert(getMessage("ci.common.error.row.noSelect"));
        return;
    }
    selectRowById("entityClassListGrid", dataArray[0]);
    if (!isEmptyRecordset(entityClassListGrid1.recordset)) {
        openModifyWin(entityClassListGrid1.recordset("ID").value);
    }
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isPageGridsDataChanged()) {
        if (!confirm(ciDataChangedConfirmation) ) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// open the class add page in a new window
//-----------------------------------------------------------------------------
function openAddWin() {
    if (isPageGridsDataChanged()) {
        if (!confirm(ciDataChangedConfirmation) ) {
            return;
        }
    }
    // Get the value for entity FK.
    var entityFK = getObjectValue("pk");
    var path = "ciEntityClassAdd.do?entityId=" + entityFK;
    var mainwin = window.open(path, 'EntityClassDetail',
        'width=800,height=500,innerHeight=400,innerWidth=725,scrollbars' );
    mainwin.focus();
}

//-----------------------------------------------------------------------------
// open the class modify page in a new window
//-----------------------------------------------------------------------------
function openModifyWin(pk) {
    if (isPageGridsDataChanged()) {
        if (!confirm(ciDataChangedConfirmation) ) {
            return;
        }
    }

    if (isEntityReadOnlyYN == "Y")  {
        return;
    }
    // Get the value for entity FK.
    var entityFK = getObjectValue("pk");
    var path = "ciEntityClassModify.do?entityClassId=" + pk +
        "&entityId=" + entityFK;
    var mainwin = window.open(path, 'EntityClassDetail',
        'width=800,height=500,innerHeight=400,innerWidth=725,scrollbars' );
    mainwin.focus();
}

//-----------------------------------------------------------------------------
// Open notes window by calling function loadNotes in csloadnotes.js.
//-----------------------------------------------------------------------------
function openNotesForListClass(pk) {
    if(pk == null){
        pk = entityClassListGrid1.recordset("ID").value;
    }
    if (window.loadNotesWithReloadOption) {
        loadNotesWithReloadOption(pk, 'ENTITY_CLASS', 'ENTITY_CLASS', true, false, 'handleNotesExist');
    } else {
        alert(getMessage("ci.entity.message.entityNotes.notAvailable"));
    }
}

//-----------------------------------------------------------------------------
// Handle Notes Exist.
//-----------------------------------------------------------------------------
function handleNotesExist(notesExist, sourceTableName, sourceRecordId) {
    if (sourceRecordId == entityClassListGrid1.recordset("id").value) {
        if (notesExist) {
            entityClassListGrid1.recordset("CNOTEIND").value = "Yes";
        } else {
            entityClassListGrid1.recordset("CNOTEIND").value = "No";
        }
    }
}

function refreshPage() {
    if (isPageGridsDataChanged()) {
        if (!confirm(ciDataChangedConfirmation) ) {
            return;
        }
    }
    setInputFormField("process", "loadAllEntityClass");
    showProcessingImgIndicator();
    submitFirstForm();
}