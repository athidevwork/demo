/**
 * The common js for Add / Select Contact page.
 *
 * <p>(C) 2013 Delphi Technology, inc. (dti)</p>
 * Date:   1/17/13
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */

var contactInfo = {
    "contactId": "",
    "contactName": ""
};

//----------------------------------------------------------------------------------------------------
// Open the Add / Select Contact window.
//----------------------------------------------------------------------------------------------------
function openAddSelectContact(entityId, contactIdFieldName, contactNameFiledName, eventName) {
    var url = getCISPath() + "/addSelectContact.do?&pk=" + entityId + "&eventName=" + eventName +
        "&contactIdFieldName=" + contactIdFieldName + "&contactNameFiledName=" + contactNameFiledName +
        "&date=" + new Date();

    openDivPopup("", url, true, true, null, null, null, null, null, null, null, true);
}

