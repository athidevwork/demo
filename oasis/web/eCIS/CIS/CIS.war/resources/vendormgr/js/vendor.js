//-----------------------------------------------------------------------------
// Functions to support Vendor page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 08/16/2007    kshen       Resized the popup phone log page.
// 05/13/2011    kshen       Added codes to check if banking field is empty for ETF payment option.
// 02/01/2012    kshen       Issue 108498
// 07/08/2016    dpang       Issue 177663 - Added windowName to prevent multiple phone log pages.
// 04/03/2018    JLD         Issue 109176. Refactor Vendor.
// 10/16/2018    Elvin       Issue 195835: grid replacement
//-----------------------------------------------------------------------------

function handleOnButtonClick(btnId) {
    switch(btnId) {
        case 'PHONELOG':
            openPhoneLog();
            break;
        case 'REFRESH':
            if (isOkToChangePages()) {
                reloadWindowLocation();
            }
            break;
        case 'VENDORSTATUSHIST':
            openClaimCodeHistory('VENDOR_STATUS');
            break;
        case 'VENDORTYPEHIST':
            openClaimCodeHistory('VENDOR_TYPE');
            break;
        default:
            break;
    }
}

function handleOnSubmit(action) {
    var countComputed = getObjectValue("countComputed");
    if (!isEmpty(countComputed) && parseFloat(countComputed) > 1) {
        alert(getMessage("ci.common.error.vendor.hasExisted", new Array(countComputed, getObjectValue("entityName"))));
        return false;
    }

    if (!validateAllVendorFields()) {
        return false;
    }
    return true;
}

//-----------------------------------------------------------------------------
// Validate all vendor fields.
//-----------------------------------------------------------------------------
function validateAllVendorFields() {
    var errorMessage = '';
    if (getObjectValue("CI_CHK_BANK_FOR_EFT") == "Y") {
        if (getObjectValue("paymentOption") == "ETRANS") {
            if ((hasObject("bankRoutingNumber") && getObjectValue("bankRoutingNumber") == "")
                    || (hasObject("bankAccountType") && getObjectValue("bankAccountType") == "")
                    || (hasObject("bankAccountName") && getObjectValue("bankAccountName") == "")
                    || (hasObject("bankAccountNumber") && getObjectValue("bankAccountNumber") == "")) {
                errorMessage += getMessage("ci.entity.vendor.bankInfoRequiredForEFT.message") + "\n";
            }
        }
    }

    var effectiveFromDate = getObjectValue("effectiveFromDate");
    var effectiveToDate = getObjectValue("effectiveToDate");
    if (isDate2OnOrAfterDate1(effectiveFromDate, effectiveToDate) == 'N') {
        errorMessage += getMessage("ci.common.error.certifiedDate.after", new Array(getLabel("effectiveFromDate"), getLabel("effectiveToDate"))) + "\n";
    }

    if (isEmpty(errorMessage)) {
        return true;
    } else {
        alert(errorMessage);
        return false;
    }
}

function isOkToChangePages(id, url) {
    if (isPageDataChanged()) {
        if (!confirm(getMessage("js.lose.changes.confirmation"))) {
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

function openPhoneLog() {
    if (isEmpty(getObjectValue("vendorId"))) {
        alert(getMessage("ci.entity.message.payment.before"));
        return;
    }

    var url = getAppPath() + "/phoneLog.do?entityFK=" + getObjectValue("pk") + "&vendorPK=" + getObjectValue("vendorId");
    window.open(url, 'phoneLog', 'top=10,left=10,width=1300,height=600,scrollbars=yes');
}

function openClaimCodeHistory(codeType) {
    var path = getAppPath() + "/claimcodehistory/claimCodeHistory.do?process=load";
    path += "&isCodeType=" + codeType;
    path += "&pk=" + getObjectValue("pk");
    path += "&entityName=" + getObjectValue("entityName");
    path += "&srcRecId=" + getObjectValue("vendorId");
    openDivPopup("View Claim Code History", path, true, true, "", "", "", "400", "", "", "", true);
}