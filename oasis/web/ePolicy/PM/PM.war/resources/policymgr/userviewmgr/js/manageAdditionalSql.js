//-----------------------------------------------------------------------------
// javascript file.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
//   11/30/2018       xjli        195889 - Reflect grid replacement project changes.
//-----------------------------------------------------------------------------
function processSqlErrMsg(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return false;
            }
            closeWindow(function (){
                getParentWindow().setAdditionalSql(getObjectValue("additionalSql"));
            });
        }
    }

}
function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'OK':
            var additionalSql = getObjectValue("additionalSql");
            var existingSql = getObjectValue("existingSql");
            var path = getAppPath() + "/policymgr/userviewmgr/manageAddtionalSql.do?"
                + "additionalSql=" + additionalSql + "&existingSql=" + existingSql + "&process=validateAdditionalSql";
            new AJAXRequest("get", path, '', processSqlErrMsg, false);
            break;
    }
}
function handleOnLoad() {
    getObject("existingSql").readOnly = true;
}