var url = getAppPath()+"/batchrenewalprocessmgr/createBatchRenewalProcess.do?process=createBatchRenewalProcess";

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'RENEWAL':
            processSelectAll(getObject("policyType"));
            processSelectAll(getObject("issueState"));
            showProcessingDivPopup();
            // Enable and hide all disabled fields in a form before submit
            enableFieldsForSubmit(document.forms[0]);
            getObject("process").value = "createBatchRenewalProcess";
            break;
        default:
            proceed = false;
            alert(getMessage("pm.batchRenewalProcess.save.error"));
    }
    return proceed;
}
function processSelectAll(selecElem){
     //find if current selected value == ALL
     //and total options > 1
     if(selecElem.selectedIndex>=0
         &&selecElem.options[selecElem.selectedIndex].value=="ALL"
         &&selecElem.options.length>1){
         for(var j=0;j<selecElem.options.length;j++){
             if(selecElem.options[j].value!="ALL"){
                selecElem.options[j].selected=true;
             }else{
                selecElem.options[j].selected=false; 
             }
         }
     }
}
