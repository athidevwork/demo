/**
 * Action class handle risk copy all process
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Feb 13, 2008
 *
 * @author yhchen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 * 07/11/2016       lzhang      177681 - Add handleAfterViewValidation() function after close page.
 * 01/11/2017       lzhang      182312 - Add skipHandleAfterViewValidationB.
 * 11/29/2018       clm         195889 - Grid replacement using getParentWindow, closeThisDivPopup.
 * ---------------------------------------------------
 */
function handleOnButtonClick(btn) {
    switch (btn) {
        case 'Close':
            if (isEmpty(getObjectValue("workflowState"))) {
                closeThisDivPopup(true);
            }
            else {
                commonOnSubmit('closePage', true, true, true);
            }
            if (!getObject(skipHandleAfterViewValidationB)){
                handleAfterViewValidation();
            }
            break;
    }

    return true;
}


function handleAfterViewValidation() {
    var functionExists = eval("getParentWindow().handleAfterViewValidation");
    if (functionExists) {
        getParentWindow().handleAfterViewValidation();
    }
}