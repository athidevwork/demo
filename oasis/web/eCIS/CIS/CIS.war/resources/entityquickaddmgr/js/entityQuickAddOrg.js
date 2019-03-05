/**
 * Created by jdingle on 8/12/2016.
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 09/21/2017       ylu         187921: add a "break" in order to navigate to Entity Modify
 11/16/2018       Elvin       Issue 195835: grid replacement
 -----------------------------------------------------------------------------
 */

function btnClick(asBtn) {
    switch (asBtn) {
        case 'save':
            if (!validate(document.forms[0], true)) {
                return;
            }

            currentAddressPrefix = "address_";
            if (!validateAllEntityAddFields()) {
                return;
            }
            currentAddressPrefix = "address2_";
            if (!validateCommonAddressFields()) {
                return false;
            }

            if (!validateCommonFields()) {
                return;
            }
            setInputFormField("process", "saveAllEntity");
            submitFirstForm();
            break;
        case 'saveclose':
            setObjectValue("saveAndClose", "Y");
            btnClick("save");
            break;
        case 'close':
            goToModule('search');
            break;
        default:
            break;
    }
}