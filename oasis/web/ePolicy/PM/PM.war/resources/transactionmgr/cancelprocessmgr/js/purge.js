//-----------------------------------------------------------------------------
// Javascript file
//
// (C) 2013 Delphi Technology, inc. (dti)
// Date:   July 01, 2013
// Author: adeng
//
// Revision Date    Revised By  Description
//---------------------------------------------------------------------------------------------------------------------
// 07/01/2013       adeng       117011 - Modified purgePolicy() to set value of new field
//                                       "transactionComment2" to input form field "transactionComment2".
//---------------------------------------------------------------------------------------------------------------------
function purge() {
    captureTransactionDetails("PURGE", "purgePolicy");
}

function purgePolicy() {
    if(objectComment2){
        setInputFormField("transactionComment2", objectComment2.value);
    }
    postAjaxSubmit("/transactionmgr/cancelprocessmgr/purgePolicy.do", "purgePolicy", false, false, handleOnPurgePolicyDone);
}

function handleOnPurgePolicyDone(ajax) {
    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            refreshPage();
        }
    }
}