/*
  Description: js file for maintainBilling.jsp

  Author: Dzhang
  Date: Oct 08, 2010


  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  10/08/2010       dzhang      112464: added function processDeps, hideAllLayers and checkFieldLayerDep.
  04/03/2012       jshen       132152: 1) Rename policyHolderNameEntityId to policyHolderEntityId
  04/17/2013       htwang      135366: Determine if accountNo and accountHolderName are editable in pageEntitlement
  07/22/2014       kmv         155454: Fix to set accountNo and accountHolder name when AcctHolderIsPolHolderB = 'Y'
  12/16/2014       jyang2      157750: Modified handleOnValidateAccountExistsForEntity. Handle the special character in
                                       policyHolderName which is appended to URL.
  09/22/2015       kmv         162634: Fix bug to set NBD, Frequency, leadDays and MMDD to NULL when singlePolicyB = Y
  02/09/2016       kmv         162634: Fix to set NBD, leadDays and MMDD to NULL and disabled when frequency is
                                       pre populated to "Based On Policy"
  06/28/2016       kmv         176363: Fix to not overwrite accountNo value while toggling AcctHolderIsPolHolderB flag
  07/12/2017       lzhang      186847: Reflect grid replacement project changes
  04/20/2018       fhuang      192568 - Modified confirmWithYesNo() to replace vbscript msgbox with built-in confirm() function.
  09/17/2018       htwang      194069 - Made the billing setup popup page bigger
  -----------------------------------------------------------------------------
  (C) 2013 Delphi Technology, inc. (dti)
 */
var initValueForPaymentPlanIdIndex;
var billingRelationExists

var initValueForAccountNo
var initValueForAccountHolderName
var initValueForAccountHolderEntityId
var policyHolderEntityId

function handleOnChange(field) {
    if (field.name == "shoreMoreFlag") {
        showMoreOrLess();
    }

    // for acctHolderIsPolHolderB
    else if (field.name == "acctHolderIsPolHolderB") {
        modifyFieldsForAcctHolderIsPolHolderB(field.value);
    }

    // for singlePolicyB, It was checkBox, but upon Jason's suggestions,
    // changed it to be select dropdown list.
    else if (field.name == "singlePolicyB") {
        modifyMoreFieldsForSinglePolicy(field.value);
    }

    // for billingFrequency list
    else if (field.name == "billingFrequency") {
        modifyMoreFieldsForBillingFrequency(field.value);
    }

    // for brlCheck
    else if (field.name == "brlCheck") {
        modifyFieldsForBrlCheck(field.value);
    }
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case "CLOSE":
          if (confirmWithYesNo(getMessage("pm.maintainBilling.confirm.close"))) {
              commonOnSubmit('saveBilling',false,true,true,true);
              break;
          }
          else {
              closeThisDivPopup(false);
              break;
          }
    }
}

// modify accountNo, paymentPlanlist, accountHolderName fields
// when acctHolderIsPolicyHolderB is checked/uncheked
function modifyFieldsForAcctHolderIsPolHolderB(isChecked) {
    if (isChecked == "Y") {
        // accountNO should be set with derivedPolicyNo, but before we do so, let us store it for later use
        initValueForAccountNo = getObjectValue("accountNo");

        document.forms[0].accountNo.value = getObjectValue("derivedPolicyNo");
        getObject("accountNoROSPAN").innerText = getObjectValue("derivedPolicyNo");

        // accountHolderName should be set with policyHolderName
        document.forms[0].accountHolderName.value = getObjectValue("policyHolderName");
        getObject("accountHolderNameROSPAN").innerText = getObjectValue("policyHolderName");

        setInputFormField("isAccountNoEditable", "N");
        setInputFormField("isAccountHolderNameEditable", "N");
        // Hide the button Account Maintenance, system will show the button More automatically.
        setInputFormField("isAcctMntAvailable", "N");
    }
    else {
        // if not checked, the acccountNo is enabled for edit
        // but, we first store the value just in case user wants to
        // check the checkBox,   in this case, we should restore the value
        getObject("accountNo").value = initValueForAccountNo;
        getObject("accountNoROSPAN").innerText = initValueForAccountNo;

        // enable it only needs to..
        if (getSysParmValue("FM_BS_ENTER_ACCT") == 'Y') {
            getObject("accountHolderName").value = "";
            setInputFormField("isAccountNoEditable", "Y");
            setInputFormField("isAccountHolderNameEditable", "Y");
        }
       // Show the button Account Maintenance, system will hide the button More automatically.
        setInputFormField("isAcctMntAvailable", "Y");

        if (getObject("showMoreFlag").value == 'Y') {
            showMoreOrLess();
        }
    }
    // Call the pageEntitlement to show/hiden the button Account Maintenance
    var functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(false);
    }
}

//-----------------------------------------------------------------------------
// If the "Relate Entire Policy at Account Level" is checked, the Payment Plan field is enabled, else it should be disabled.
//-----------------------------------------------------------------------------
function modifyFieldsForBrlCheck(isChecked) {
    if (isChecked == "Y") {
        enableDisableField(getObject("paymentPlanId"), false);
        setInputFormField("isBillingSetupAvailable", "N");
    }
    else {
        enableDisableField(getObject("paymentPlanId"), true);
        setInputFormField("isBillingSetupAvailable", "Y");
    }
    // Call the pageEntitlement to show/hiden the button Billing Setup
    var functionExists = eval("window.pageEntitlements");
    if (functionExists) {
        pageEntitlements(false);
    }
}
// nullout values from more divs when singlePolicyB is checked
var origBaseBillMonthDay;
var origBillLeadDays;
var origBillingFrequency;
var origNextBillingDay;

function modifyMoreFieldsForSinglePolicy(fieldValue) {
    var singlePolicyBChecked;
    if (fieldValue == 'Y') {
        singlePolicyBChecked = true;
    }
    else {
        singlePolicyBChecked = false;
    }
    modifyMoreFieldsForSinglePolicyB(singlePolicyBChecked);
}

function modifyMoreFieldsForSinglePolicyB(isChecked) {
    if (isChecked) {
        // store the original values just in case we need them later
        origBaseBillMonthDay = getObjectValue("baseBillMonthDay");
        origBillLeadDays = getObjectValue("billLeadDays");
        origBillingFrequency = getObjectValue("billingFrequency");
        origNextBillingDay = getObjectValue("nextBillingDate");

        // null the fields out
        getObject("baseBillMonthDay").value = "";
        getObject("baseBillMonthDayROSPAN").innerHTML = "";
        getObject("billLeadDays").value = "";
        getObject("billLeadDaysROSPAN").innerHTML = "";
        getObject("billingFrequency").value = "";
        getObject("billingFrequencyLOVLABELSPAN").innerHTML = "";
        getObject("nextBillingDate").value = "";
        getObject("nextBillingDateROSPAN").innerHTML = "";

        // disable the fields
        enableDisableFieldsForSinglePolicy(true);
    }
    else {
        // put the original values back
        if (typeof origBaseBillMonthDay != "undefined") {
            getObject("baseBillMonthDay").value = origBaseBillMonthDay;
            getObject("baseBillMonthDayROSPAN").innerHTML = origBaseBillMonthDay;
        }
        if (typeof origBillLeadDays != "undefined") {
            getObject("billLeadDays").value = origBillLeadDays;
            getObject("billLeadDaysROSPAN").innerHTML = origBillLeadDays;
        }
        if (typeof origBillingFrequency != "undefined") {
            getObject("billingFrequency").value = origBillingFrequency;
            getObject("billingFrequencyLOVLABELSPAN").innerHTML = origBillingFrequency;
        }
        if (typeof origNextBillingDay != "undefined") {
            getObject("nextBillingDate").value = origNextBillingDay;
            getObject("nextBillingDateROSPAN").innerHTML = origNextBillingDay;
        }
        // enable the fields
        enableDisableFieldsForSinglePolicy(false);
    }
}

function modifyMoreFieldsForBillingFrequency(fieldValue) {
    if (fieldValue == 'P') {
        // store the original values just in case we need them later
        origBaseBillMonthDay = getObjectValue("baseBillMonthDay");
        origBillLeadDays = getObjectValue("billLeadDays");
        origNextBillingDay = getObjectValue("nextBillingDate");

        // null the fields out
        getObject("baseBillMonthDay").value = "";
        getObject("baseBillMonthDayROSPAN").innerHTML = "";
        getObject("billLeadDays").value = "";
        getObject("billLeadDaysROSPAN").innerHTML = "";
        getObject("nextBillingDate").value = "";
        getObject("nextBillingDateROSPAN").innerHTML = "";

        // disable the fields
       enableDisableFieldsForBillingFrequency(true);
    }
    else {
        // put the original values back
        if (typeof (origBaseBillMonthDay) != "undefined") {
            getObject("baseBillMonthDay").value = origBaseBillMonthDay;
            getObject("baseBillMonthDayROSPAN").innerHTML = origBaseBillMonthDay;
        }
        if (typeof (origBillLeadDays) != "undefined") {
            getObject("billLeadDays").value = origBillLeadDays;
            getObject("billLeadDaysROSPAN").innerHTML = origBillLeadDays;
        }
        if (typeof (origNextBillingDay) != "undefined") {
            getObject("nextBillingDate").value = origNextBillingDay;
            getObject("nextBillingDateROSPAN").innerHTML = origNextBillingDay;
        }
         // enable the fields
        enableDisableFieldsForBillingFrequency(false);
    }
}

// althought we have the logic in java (BillingManagerImpl),
// for performance reason, let us do that here as well:
//
function validateSinglePolicyBBeforeSubmit() {
    var continueToSubmit = true;
    var billingFrequency = getObjectValue("billingFrequency");
    var baseBillMonthDay = getObjectValue("baseBillMonthDay")
    var billLeadDays = getObjectValue("billLeadDays");
    var nextBillingDate = getObjectValue("nextBillingDate");
    if (billingFrequency == "") {
        billingFrequency = null;
    }
    if (baseBillMonthDay == "") {
        baseBillMonthDay = null;
    }
    if (billLeadDays == "") {
        billLeadDays = null;
    }
    if (nextBillingDate == "") {
        nextBillingDate = null;
    }
    if (//window.document.forms[0].singlePolicyB.checked &&
        window.document.forms[0].singlePolicyB.value == "Y" &&
        getObjectValue("enableMoreOptionB") == "Y") {
        if (billingFrequency != null ||
            baseBillMonthDay != null ||
            billLeadDays != null ||
            nextBillingDate != null) {

            var checkSinglePolicyBMessage = getMessage("pm.maintainBilling.confirm.checkSinglePolicyB");

            if (confirm(checkSinglePolicyBMessage)) {
                continueToSubmit = true;
            }
            else {
                continueToSubmit = false;
            }
        }
    }
    return continueToSubmit;
}

function validateMoreFieldsBeforeSubmit() {
    var continueToSubmit = true;
    var billingFrequency = getObjectValue("billingFrequency");
    var baseBillMonthDay = getObjectValue("baseBillMonthDay")
    var billLeadDays = getObjectValue("billLeadDays");
    var acctHolderIsPolHolderB = getObjectValue("acctHolderIsPolHolderB");
    var singlePolicyB = getObjectValue("singlePolicyB");
    var showMoreFlag = getObjectValue("showMoreFlag");
    if (billingFrequency == "") {
        billingFrequency = null;
    }
    if (baseBillMonthDay == "") {
        baseBillMonthDay = null;
    }
    if (billLeadDays == "") {
        billLeadDays = null;
    }

    if (acctHolderIsPolHolderB == "Y" && showMoreFlag == "Y" && singlePolicyB != "Y" && billingFrequency != "P") {
        // If we have any one of these we need all in the More>> mode
        if (billingFrequency == null) {
            if (baseBillMonthDay != null ||
                billLeadDays != null) {
                continueToSubmit = false;
                var selectBillFrequencyMessage = getMessage("pm.maintainBilling.validate.error.noBillingFrequency");
                handleError(selectBillFrequencyMessage, "billingFrequency");
            }
        }
        else { // billingFrequency is not null
            if (baseBillMonthDay == null) {
                continueToSubmit = false;
                var noBaseBillMonthDayMessage = getMessage("pm.maintainBilling.validate.error.noBaseBillMonthDay");
                handleError(noBaseBillMonthDayMessage, "baseBillMonthDay");
            }
            if (billLeadDays == null) {
                continueToSubmit = false;
                var noBillLeadDaysMessage = getMessage("pm.maintainBilling.validate.error.noBillLeadDays");
                handleError(noBillLeadDaysMessage, "billLeadDays");
            }
        }
    }
    return continueToSubmit;
}

function validateAccountNoBeforeSubmit() {
    var continueToSubmit = true;
    var accountNo = getObjectValue("accountNo");
    if (accountNo == null || accountNo == "") {
        var noAccountNoEnteredMessage = getMessage("pm.maintainBilling.validate.error.noAccountNoEntered");
        handleError(noAccountNoEnteredMessage, "accountNo");
        continueToSubmit = false;
    }
    return continueToSubmit;
}

function showMoreOrLess() {
    if (getObject("showMoreFlag").value == 'N') {
        window.document.forms[0].showMoreFlag.value = 'Y';
        getObject("PM_BILLNG_MORE").value = getMessage("pm.maintainBilling.label.buttonMoreLess.less");
       // getObject("buttonMoreLess").value = getMessage("pm.maintainBilling.label.buttonMoreLess.less");
    }
    else {
        window.document.forms[0].showMoreFlag.value = 'N';
        getObject("PM_BILLNG_MORE").value =getMessage("pm.maintainBilling.label.buttonMoreLess.more");
      //  getObject("buttonMoreLess").value =getMessage("pm.maintainBilling.label.buttonMoreLess.more");
    }
    if (eval("window.processDeps")) {
       processDeps();
    }
}

function find(field) {
    if (field == "accountHolderName") {
        if (getSysParmValue('FM_BS_ENTER_ACCT') == 'N') {
            var parameterNotConfiguredFMBSENTERACCT = getMessage("pm.maintainBilling.validation.parameterNotConfiguredFMBSENTERACCT");
            handleError(parameterNotConfiguredFMBSENTERACCT);
            return;
        }
        if (getObjectValue("billingRelationExistsB") == "Y") {
            var backToPolicyMesage = getMessag("pm.maintainBilling.validate.alert.billingRelationExists");
            handleError(backToPolicyMesage);
            backToPolicy();
        }
        else {
            if (document.forms[0].accountNo.disabled || getObjectValue("acctHolderIsPolHolderB") == 'Y') {
                var enableSearchByAcctHolderMessage = getMessage("pm.maintainBilling.alert.enableSearchByAcctHolder");
                handleError(enableSearchByAcctHolderMessage, "acctHolderIsPolHolderB");
            }
            else {
                // store 2 values prior to call openEntitySelectWinFullName
                // just in case if user clicks cancel. we need to restore the values
                initValueForAccountHolderEntityId = getObjectValue("accountHolderEntityId");
                initValueForAccountHolderName = getObjectValue("accountHolderName");
                openEntitySelectWinFullName("accountHolderEntityId", "accountHolderName", "captureEntity()");
            }
        }
    }
}

function captureEntity() {
    // because for openEntitySelectWinFullName, only a list of functions can be used as the callback function
    // captureEntity is one of them, can not use populateAccountNoForAccountHolder directly.
    // so, use captureEntity to call populateAccountNoForAccountHolder.
    populateAccountNoForAccountHolder();
}

function populateAccountNoForAccountHolder() {

    var accountHolderEntityId;
    var effectiveFromDate;
    var effectiveToDate;
    if (hasObject("accountHolderEntityId")) {
        accountHolderEntityId = getObjectValue("accountHolderEntityId");
    }

    if (hasObject("termEffectiveFromDate")) {
        effectiveFromDate = getObjectValue("termEffectiveFromDate");
    }

    if (hasObject("termEffectiveToDate")) {
        effectiveToDate = getObjectValue("termEffectiveToDate");
    }
    else {
        effectiveToDate = "01/01/3000";
    }

    if (typeof accountHolderEntityId != "undefined" ||
        typeof effectiveFromDate != "undefined") {

        // call lookupAccount with the values from above
        lookupAccount(accountHolderEntityId, effectiveFromDate, effectiveToDate, 'billingAccountId', "accountNo","postProcessAccountLookupData");
    }
}

//
function postProcessAccountLookupData(btn){
    var upperBtn = btn.toUpperCase();
    switch (upperBtn) {
      case "SELECT":
           break;
      case "CANCEL":
           // user clicked cancel, we ought to revert the changes made by openEntitySelectWinFullName
           getObject("accountHolderName").value = initValueForAccountHolderName;
           getObject("accountHolderEntityId").value = initValueForAccountHolderEntityId;
           break;
    }
}
function handleOnSubmit(process) {
    if (process == "saveBilling") {

        // doing js validations.. (logic is implemented in java BillingManagerImpl as well.)
        var okToContinue1 = validateMoreFieldsBeforeSubmit();
        var okToContinue2 = validateSinglePolicyBBeforeSubmit();
        var okToContinue3 = validateAccountNoBeforeSubmit();

        //enable all disabled fields for submit, so they are passed into server within request        
        enableFieldsForSubmit(document.forms[0]);
        return (okToContinue1 && okToContinue2 && okToContinue3);
    }
}

function handleOnLoad(){

    if (getSysParmValue("showMultiAccount") == "Y") {
        policyHolderEntityId = getObjectValue("policyHolderEntityId");

        var url = getAppPath() + "/billingmgr/maintainBilling.do?entityId=" + policyHolderEntityId ;
        postAjaxSubmit(url, "validateAccountExistsForEntity", false, false, handleOnValidateAccountExistsForEntity);
    }

    // If save successfully, system should close this page.
    if (getObject("billingSetupB") && getObjectValue("billingSetupB") == 'Y') {
        var divPopup = window.frameElement.document.parentWindow.getDivPopupFromDivPopupControl(this.frameElement);
        window.frameElement.document.parentWindow.closeDiv(divPopup);
    }

    // Fix 106696, system should initialize these variables.
    initValueForAccountNo = getObjectValue("accountNo");
    initValueForAccountHolderName = getObjectValue("accountHolderName");

    // take care of the initial for acctHolderIsPolHolderB 
    modifyFieldsForAcctHolderIsPolHolderB(getObject("acctHolderIsPolHolderB").value);

  // take care of the initial status for singplePolicyB's dependant fields
    modifyMoreFieldsForBillingFrequency(getObject("billingFrequency").value);

    // take care of the initial status for billingFrequency's dependant fields
    modifyMoreFieldsForSinglePolicy(getObject("singlePolicyB").value);
    // System defaults to show "More >>>" and set showMoreFlag to 'N'
    getObject("PM_BILLNG_MORE").value = getMessage("pm.maintainBilling.label.buttonMoreLess.more");
    window.document.forms[0].showMoreFlag.value = 'N';

    if (eval("window.processDeps")) {
       processDeps();
    }
}

function handleOnValidateAccountExistsForEntity(ajax) {

    if (ajax.readyState == 4) {
        if (ajax.status == 200) {
            var data = ajax.responseXML;
            if (!handleAjaxMessages(data, null)) {
                return;
            }
            var oValueList = parseXML(data);
            if (oValueList.length > 0) {
                if (oValueList[0]["EXISTB"] == 'Y') {
                    var policyHolderName =   getObjectValue("policyHolderName");
                    var url = getTopNavApplicationUrl("FM") + "/billingrelationmgr/viewAccountsForEntity.do?entityId=" + policyHolderEntityId + "&policyHolderName=" + encodeURIComponent(policyHolderName);
                    var divPopupId = openDivPopup("", url, true, true, "", "", "700", "425", "", "", "", false);
                }
            }
        }
    }
}

function enableDisableFieldsForSinglePolicy(toDisable) {
    // disable/enable the SingplePolicy's dependant fields
    enableDisableField(getObject("billingFrequency"), toDisable);
    if (toDisable) {
      enableDisableFieldsForBillingFrequency(toDisable);
    } else {
      modifyMoreFieldsForBillingFrequency(getObject("billingFrequency").value);
    }
}

function enableDisableFieldsForBillingFrequency(toDisable) {
    // disable/enable the BillingFrequency's dependant fields
    enableDisableField(getObject("baseBillMonthDay"), toDisable);
    enableDisableField(getObject("billLeadDays"), toDisable);
    enableDisableField(getObject("nextBillingDate"), toDisable);
}
function viewAccountMaintenance() {

    var fmUrl = getTopNavApplicationUrl("FM") + "/accountmgr/maintainAccount.do?" +
                "process=newAccount&isAccountNoRequiredInd=N" + "&headerHidden=Y";
    var divPopupId = openDivPopup("", fmUrl, true, true, "", "", "900", "580", "880", "550", "", "", true, "", true);

}

function viewBillingSetup() {
    var policyId = getObjectValue("policyId");
    var policyNo = getObjectValue("policyNo");
    var fmUrl = getTopNavApplicationUrl("FM") + "/billingrelationmgr/maintainBillingRelation.do?process=loadAllBillingRelation&policyId="
                        + policyId + "&policyNo=" + policyNo + "&headerHidden=Y";

    var divPopupId = openDivPopup("", fmUrl, true, true, "", "", "1200", "950", "", "", "", true);
}

function viewApw() {
    var policyId = getObjectValue("policyId");
    var policyNo = getObjectValue("policyNo");
    var fmUrl = getTopNavApplicationUrl("FM") + "/apwlistmgr/apwList.do?process=loadAPWDetails&policyId="
                        + policyId + "&policyNo=" + policyNo + "&headerHidden=Y";

    var divPopupId = openDivPopup("", fmUrl, true, true, "", "", "850", "700", "", "", "", true);
}

function hideAllLayers() {
    var obj = getObject("moreBillingSetupFields2");
    if (obj) {
        if (obj.length) {
            for (var i = 0; i < obj.length; i++)
                hideShowElementByClassName(obj[i], true);
        }
        else
            hideShowElementByClassName(obj, true);
    }

    var obj = getObject("moreBillingSetupFields1");
    if (obj) {
        if (obj.length) {
            for (var i = 0; i < obj.length; i++)
                hideShowElementByClassName(obj[i], true);
        }
        else
            hideShowElementByClassName(obj, true);
    }
}

function checkFieldLayerDep(fieldObj) {
    if (!fieldObj) return;
    if (fieldObj.length) {
        for (var i = 0; i < fieldObj.length; i++) {
            if (fieldObj[i].checked) {
                fieldObj = fieldObj[i];
                break;
            }
        }
    }
    var obj;
    if (fieldObj.name == "showMoreFlag") {
        if (getObjectValue("showMoreFlag") == "Y") {
            obj = getObject("moreBillingSetupFields2");
            if (obj) {
                if (obj.length) {
                    for (var i = 0; i < obj.length; i++)
                        hideShowElementByClassName(obj[i], false);
                }
                else
                    hideShowElementByClassName(obj, false);
            }

            obj = getObject("moreBillingSetupFields1");
            if (obj) {
                if (obj.length) {
                    for (var i = 0; i < obj.length; i++)
                        hideShowElementByClassName(obj[i], false);
                }
                else
                    hideShowElementByClassName(obj, false);
            }
        }
    }
}

function processDeps() {
    hideAllLayers();
    checkFieldLayerDep(getObject("showMoreFlag"));
}

function confirmWithYesNo(str) {
    var msg = str.replace(/0x000A/g, "\n");
    return confirm(msg);
}
