var url = getAppPath()+"/batchrenewalprocessmgr/createCommonAnniversaryBatchRenewalProcess.do?process=createCommonAnniversaryBatchRenewalProcess";

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'RENEWAL':
            showProcessingDivPopup();
            enableFieldsForSubmit(document.forms[0]);
            getObject("process").value = "createCommonAnniversaryBatchRenewalProcess";
            break;
        default:
            proceed = false;
            alert(getMessage("pm.batchRenewalProcess.save.error"));
    }
    return proceed;
}
