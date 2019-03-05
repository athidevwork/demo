//-----------------------------------------------------------------------------
// for Extend Cancel
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/26/2013       adeng       117011 - Modified handleOnSubmit() to set value of new field "transactionComment2"
//                              to input form field "newTransactionComment2".
//-----------------------------------------------------------------------------
function handleOnLoad(){
 if (!isEmpty(getObjectValue("workflowState"))) {
  invokeSaveQuoteOfficialWorkflow(getObjectValue("policyNo"));
 }
}

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            showProcessingDivPopup();
        // Enable and hide all disabled fields in a form before submit
            enableFieldsForSubmit(document.forms[0]);
            setInputFormField("newTransactionComment2", getObjectValue("transactionComment2"));
            getObject("process").value = "extendCancelTerm";
            break;
    }
    return proceed;
}

function invokeSaveQuoteOfficialWorkflow(quoteNo) {
    window.frameElement.document.parentWindow.refreshPage(true);
    closeWindow();
}