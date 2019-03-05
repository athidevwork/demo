//-----------------------------------------------------------------------------
// for view related policy page
//
// (C) 2017 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 10/19/2017       xnie        188957 - Refresh page after submit form.
// 11/08/2017       lzhang      189535 - Revert 188957
//-----------------------------------------------------------------------------
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            if (isEmpty(getObjectValue("workflowState"))) {
                commonOnButtonClick("CLOSE_RO_DIV");
            }
            else {
                document.forms[0].process.value = "closePage";
                document.forms[0].action=getAppPath()+"/transactionmgr/viewRelatedPolicy.do";
                submitFirstForm();
            }
            break;
    }      
}


