//-----------------------------------------------------------------------------
// Functions to support Modify Entity
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 02/16/2007    kshen       Added dummy method personDbaGrid_selectRow,
//                           orgDbaGrid_selectRow. (iss68160)
// 07/02/2007    FWCH        Added changeEntityType function
// 07/27/2007    FWCH        Added date validating rules
// 08/17/2007    FWCH        Disable/Enable field entity_taxInfoEffectiveDate
//                           according to entity_defaultTaxID's status
// 10/11/2007    FWCH        Added function sendMailToEntity and openEntityWebSite
// 12/19/2007    zlzhu       Added policies to btnClick
// 01/24/2008    Kenney      Added specialhandle to btnClick
// 09/03/2008    kshen       Added e-mail icon for email fields.
// 05/28/2009    MSN         Added Org Group Tab
// 07/16/2009    hxk         Added logic so we can avoid doing the "Today" edit for insured since date
// 09/12/2009    hxk         Same as above for loss/claim free date
// 04/16/2010    kshen       Deleted the codes about adding email icon and send mail
// 08/11/2010    Ldong       Issue#110376: Add Note function
// 10/22/2010    kshen       #116258. Changed codes to skip validate email address fields by system parameters.
// 01/10/20111   syang       Issue 105832: Modified btnClick() to handle "DDL".
// 07/06/2011    Ldong       Issue 117873: Add Reference Number.
// 06/20/2012    Ldong       Issue 132748.
// 11/14/2012    Elvin       Issue 138471.
// 04/26/2013    bzhu        Issue 139501.
// 05/17/2013    Elvin       Issue 144482: Remove duplicated lossFreeDt and clmsFreeDt validation
// 07/01/2013    hxk         Issue 141840
//                           Add pk to open of DDL page.
// 09/13/2013    kshen       Issue 144341.
// 12/06/2013    Parker      Issue 148036 Refactor maintainRecordExists code to make one call per subsystem to the database.
// 03/17/2014    Elvin       Issue 151570: use getObjectValue instead getObject().value
// 04/29/2014    jld         Issue 153914. Fix initial display of suffix field when configured as multi-select popup.
// 12/23/2014    Elvin       Issue 159499: display description not code for multi-select popup suffix field
// 09/18/2015    Elvin       Issue 165751: add trim for suffix values from db
// 08/25/2017    ylu         Issue 166271: handle with displaying multi-select fields description by function
//                                         for both suffix name and Prof Designation
// 01/12/2018    ylu         Issue 190718: not to validate masked secured fields
// 04/20/2018    ylu         Issue 109088: refactor: move displayDeceasedDate() function to Field Dependence config
// 05/07/2018    jld         Issue 193125: Add npi_no.
// 05/23/2018    ylu         Issue 109088: due to 193125's fix, remove duplicate call of resetOKToSkipTaxIDDupsFlag()
// 06/20/2018    ylu                       move taxInfoEffectiveDate process to Field Dependence.
// 10/22/2018    dzou        grid replacement
//-----------------------------------------------------------------------------
var SSNFldID = "entity_socialSecurityNumber";
var TINFldID = "entity_federalTaxID";
var NPIFldID = "entity_npiNo";
var entTypeFldID = "entity_entityType";
var dobFldID = "entity_dateOfBirth";
var clmsFreeDtFldID = "entity_claimsFreeDate";
var lossFreeDtFldID = "entity_lossFreeDate";
var insrdSinceDtFldID = "entity_insuredSinceDate";
var firstNameFldID = "entity_firstName";
var lastNameFldID = "entity_lastName";
var orgNameFldID = "entity_organizationName";
var eMail1FldID = "entity_eMailAddress1";
var eMail2FldID = "entity_eMailAddress2";
var eMail3FldID = "entity_eMailAddress3";
var SSNVerBFldID = "entity_ssnVerifiedB";
var TINVerBFldID = "entity_federalTaxIDVerifiedB";
var dfltTaxIDFldID = "entity_defaultTaxID";
var vendorVerifyFldID = "CM_CHK_VENDOR_VERIFY";
var discardedFldID = "entity_discardedB";
var clientDiscardedMsg = "";
var clientDiscardedError = "N";
var deceasedBFldID = "entity_deceasedB";
var deceasedDateFldID = "entity_dateOfDeath";
var deceasedDateLabelFldID = "entity_dateOfDeathFLDLABEL";
var deceasedDateIconFldID = "ADT_entity_dateOfDeath";
var webAddress1FldID = "entity_webAddress1";
var taxEffDateFldID = "entity_taxInfoEffectiveDate";
var veryLongNameFldId = "entity_veryLongName";
var sysNoVldEmailaddr1FldId = "SYS_NO_VLD_EMAILADDR1";
var sysNoVldEmailaddr2FldId = "SYS_NO_VLD_EMAILADDR2";
var sysNoVldEmailaddr3FldId = "SYS_NO_VLD_EMAILADDR3";
var referenceNumberFldID = "entity_referenceNumber";
var suffixNameFldID = "entity_suffixName";
var profDesignationFldID = "entity_profDesignation";

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    if (!isOkToChangePages()) {
        return;
    }

    switch (asBtn) {
        case 'specialhandle':
            var path = getAppPath() + "/demographic/clientmgr/specialhandlingmgr/maintainSpecialHandling.do?pk=" + getObjectValue("pk");
            openPopup(path, "specialhandle", 900, 500, 10, 10);
            break;
        case "clientid":
            var path = getAppPath() + "/demographic/clientmgr/clientidmgr/ciMaintainClientId.do?pk=" + getObjectValue("pk");
            openPopup(path, "clientid", 800, 600, 10, 10);
            break;
        case "cvrHistory":
            var path = getTopNavApplicationUrl("Claims") + "/maintainEntityCVRHistory.do?pk=" + getObjectValue("pk");
            var divPopupId = openDivPopup("CVR Report History", path, true, true, "", "", "", "", "", "", "", true);
            break;
        case "mntdup":
            var path = getAppPath() + "/ciMaintainEntityDuplicate.do?process=iniEntityMntDuplicate&pk=" + getObjectValue("pk") + "&entityName=" + encodeUrl(getObjectValue("entityName"));
            var divPopupId = openDivPopup("Maintain Duplicate Clients", path, true, true, "", "", "", "", "", "", "", true);
            break;
        case "addlemail":
            var path = getAppPath() + "/entityAddlEmail.do?pk=" + getObjectValue("pk");
            var divPopupId = openDivPopup("Email for Electronic Distribution", path, true, true, '', '', 950, 600, '', '', "", true, "", "");
            break;
        case "DDL":
            var path = getCSPath() + "/disciplinedeclinemgr/maintainDisciplineDecline.do?process=loadAllDisciplineDeclineEntity&forDivPopupB=Y&ddlEntityId=" +
                getObjectValue("pk") + "&pk=" + getObjectValue("pk") + "&ddlEntityName=" + getObjectValue("entityName");
            var divPopupId = openDivPopup("", path, true, true, "", "", "800", "730", "", "", "", true);
            break;
        case 'policies':
            viewPolicySummary();
            break;
    }
}

function handleOnSubmit(action){
    var proceed = true;
    switch (action) {
        case 'loadEntityData':
            if (isChanged && !confirm(ciRefreshPageConfirmation)) {
                proceed = false;
            }
            break;
        case 'saveEntityData':
            if (!obrExecuteBeforeSave()){
                proceed = false;
            }
            if (!validateAllEntityFields()) {
                proceed = false;
            }
            break;
        case 'changeEntityType':
            var changeTypeConfirmation = getMessage("ci.entity.message.change.clientType") + "\n" +
                getMessage("ci.entity.message.change.riskType") + "\n" +
                getMessage("ci.entity.message.change.notRollback") + "\n" +
                getMessage("ci.entity.message.change.operationCancel");
            if (!confirm(changeTypeConfirmation)) {
                proceed = false;
            } else {
                var changeToType = (getObjectValue("entityType") == 'P') ? 'O' : 'P';
                if (changeToType == 'P' || changeToType == 'O') {
                    setObjectValue("entityType", changeToType);
                } else {
                    alert(getMessage("ci.entity.message.entityType.unknown"));
                    proceed = false;
                }
            }
            break;
    }

    return proceed;
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    var msg = '';

    switch (field.name) {
        case SSNFldID:
        case TINFldID:
        case NPIFldID:
            // Validate Identifier fields.
            validateEntityIdentifier(field);
            break;

        case eMail1FldID:
        case eMail2FldID:
        case eMail2FldID:
            // If the no validate email address of the field was turn on, skip the validation.
            if (!(field.name == eMail1FldID && getObjectValue(sysNoVldEmailaddr1FldId) == "Y")
                && !(field.name == eMail2FldID && getObjectValue(sysNoVldEmailaddr2FldId) == "Y")
                && !(field.name == eMail3FldID && getObjectValue(sysNoVldEmailaddr3FldId) == "Y")) {
                if ((getLabel(field.name).toUpperCase()).indexOf("MAIL") > 0) {
                    msg += validateEMail(field);
                }
            }
            break;
        case referenceNumberFldID:
            // Validate the Reference Number.
            if (isStringValue(getObjectValue("CI_REF_NUM_PREFIX"))) {
                msg += validateReferenceNumber(field);
            }
            break;
    }

    if (msg != '') {
        alert(msg);
        field.focus();
        postChangeReselectField(field);
        field.select();
        return false;
    }

    switch (field.name) {
        case discardedFldID:
            if (field.checked) {
                if (clientDiscardedError == 'Y') {
                    alert(clientDiscardedMsg);
                    field.checked = false;
                    return;
                }
                if (!confirm(clientDiscardedMsg)) {
                    field.checked = false;
                    return;
                }
            }
            break;

        case veryLongNameFldId:
            if (getObjectValue("CI_LONGNAME_OVERRIDE") == "Y" ) {
                var longName = field.value;
                if (isStringValue(longName)) {
                    if (getObject(orgNameFldID)) {
                        setObjectValue(orgNameFldID, longName.substring(0, 60));
                    }
                }
            }
            break;
    }
}

//-----------------------------------------------------------------------------
// Validate a date to make sure it is on or after the date of birth/inception.
//-----------------------------------------------------------------------------
function validateDateAgainstDOB(dobValue, otherDateValue, dobFldDesc, otherDateFldDesc) {
    if (dobValue == null || dobValue == '') {
        return '';
    }
    if (otherDateValue == null || otherDateValue == '') {
        return '';
    }
    // Make sure the date is in mm/dd/yyyy format.
    var reTestDate = /[0-9]{2}\/[0-9]{2}\/[0-9]{4}/;
    if (!reTestDate.test(dobValue) || !reTestDate.test(otherDateValue)) {
        return '';
    }
    // Put the dates in yyyymmdd format for the comparison.
    var newDobValue = dobValue.substr(6, 4) + dobValue.substr(0, 2) + dobValue.substr(3, 2);
    var newOtherDateValue = otherDateValue.substr(6, 4) + otherDateValue.substr(0, 2) + otherDateValue.substr(3, 2);
    if (newDobValue > newOtherDateValue) {
        return getMessage("ci.entity.message.dateValue.after", new Array(otherDateFldDesc, otherDateValue, dobFldDesc, dobValue)) + "\n";
    }
    else {
        return '';
    }
}

//-----------------------------------------------------------------------------
// Validate all fields.
//-----------------------------------------------------------------------------
function validateAllEntityFields() {
    var coll = document.forms[0].elements;
    var i = 0;
    var dobValue = '';
    var clmsFreeDtValue = '';
    var lossFreeDtValue = '';
    var insrdSinceDtValue = '';
    var deceasedDateValue = '';
    var dobLabel = '';
    var clmsFreeDtLabel = '';
    var lossFreeDtLabel = '';
    var insrdSinceDtLabel = '';
    var deceasedDateLabel = '';
    var msg = '';
    var dfltTaxIDFld = null;
    var SSNFld = null;
    var SSNVerBFld = null;
    var TINFld = null;
    var TINVerBFld = null;
    var vendorVerifyFld = '';
    var insuredSinceDateEdit =    getSysParmValue("CS_INS_SNC_DATE_EDIT");
    var lossFreeDateEdit     =    getSysParmValue("CS_LOSS_FR_DATE_EDIT");
    var claimFreeDateEdit    =    getSysParmValue("CS_CLM_FR_DATE_EDIT");

    for (i = 0; i < coll.length; i++) {

        if (isFieldMasked(coll[i])) continue;

        if (coll[i].name == SSNFldID) {
            msg += validateSSN(coll[i]);
            SSNFld = coll[i];
        }
        else if (coll[i].name == TINFldID) {
            msg += validateTIN(coll[i]);
            TINFld = coll[i];
        }
        else if (coll[i].name == SSNVerBFldID) {
            SSNVerBFld = coll[i];
        }
        else if (coll[i].name == TINVerBFldID) {
            TINVerBFld = coll[i];
        }
        else if (coll[i].name == dfltTaxIDFldID) {
            dfltTaxIDFld = coll[i];
        }
        else if (coll[i].name == NPIFldID) {
            msg += validateNPI(coll[i]);
            TINFld = coll[i];
        }
        else if (coll[i].name == eMail1FldID ||
                 coll[i].name == eMail2FldID ||
                 coll[i].name == eMail3FldID
                ) {
            // If the no validate email address of the field was turn on, skip the validation.
            if (!(coll[i].name == eMail1FldID && getObjectValue(sysNoVldEmailaddr1FldId) == "Y")
                    && !(coll[i].name == eMail2FldID && getObjectValue(sysNoVldEmailaddr2FldId) == "Y")
                    && !(coll[i].name == eMail3FldID && getObjectValue(sysNoVldEmailaddr3FldId) == "Y")) {
                if ((getLabel(coll[i].name).toUpperCase()).indexOf("MAIL") > 0) {
                    msg += validateEMail(coll[i]);
                }
            }
        }
        // First name and last name are always required for persons.
        // Org name is always required for orgs.
        // For persons, only first and last name will be in the form.
        // For orgs, only org name will be in the form.
        else if (coll[i].name == firstNameFldID ||
                 coll[i].name == lastNameFldID ||
                 coll[i].name == orgNameFldID) {
            if (!isValue(coll[i])) {
                msg += getMessage("ci.common.error.classCode.required", new Array(getLabel(coll[i].name))) + "\n";
            }
        }
        // Get the value and label for DOB.
        else if (coll[i].name == dobFldID) {
            dobValue = coll[i].value;
            dobLabel = getLabel(coll[i].name);
        }
        // Get the value and label for claims free date.
        else if (coll[i].name == clmsFreeDtFldID) {
            clmsFreeDtValue = coll[i].value;
            clmsFreeDtLabel = getLabel(coll[i].name);
        }
        // Get the value and label for loss free date.
        else if (coll[i].name == lossFreeDtFldID) {
            lossFreeDtValue = coll[i].value;
            lossFreeDtLabel = getLabel(coll[i].name);
        }
        // Get the value and label for insured since date.
        else if (coll[i].name == insrdSinceDtFldID) {
            insrdSinceDtValue = coll[i].value;
            insrdSinceDtLabel = getLabel(coll[i].name);
        }
        else if (coll[i].name == vendorVerifyFldID) {
            vendorVerifyFld = coll[i].value;
        }
        else if (coll[i].name == deceasedBFldID) {
            if (!coll[i].checked) {
                var deceasedDateFld = getObject(deceasedDateFldID);
                if (typeof deceasedDateFld != "undefined")
                    deceasedDateFld.value = '';
            }
        }
        else if (coll[i].name == deceasedDateFldID) {
            deceasedDateValue = coll[i].value;
            deceasedDateLabel = getLabel(coll[i].name);
        }
        // validate Reference Number.
        else if (coll[i].name == referenceNumberFldID) {
            if (isStringValue(getObjectValue("CI_REF_NUM_PREFIX"))) {
                msg += validateReferenceNumber(coll[i]);
            }
        }
    }

    msg += validateDateField(dobValue, dobLabel, true);
    msg += validateDateField(deceasedDateValue, deceasedDateLabel, true);


    // Only do this date check if sysparm = TODAY or ALL (the Default)
    if (insuredSinceDateEdit.indexOf("TODAY") > -1  || insuredSinceDateEdit.indexOf("ALL") > -1) {
        msg += validateDateField(insrdSinceDtValue, insrdSinceDtLabel, true);
    }
    if (claimFreeDateEdit.indexOf("TODAY") > -1  || claimFreeDateEdit.indexOf("ALL") > -1) {
        msg += validateDateField(clmsFreeDtValue, clmsFreeDtLabel, true);
    }
    if (lossFreeDateEdit.indexOf("TODAY") > -1  || lossFreeDateEdit.indexOf("ALL") > -1) {
        msg += validateDateField(lossFreeDtValue, lossFreeDtLabel, true);
    }

    msg += validateDefaultVerifiedTaxID(dfltTaxIDFld, SSNFld, SSNVerBFld,
            TINFld, TINVerBFld, vendorVerifyFld);

    if (insuredSinceDateEdit.indexOf("DOB") > -1 || insuredSinceDateEdit.indexOf("ALL") > -1) {
        msg += validateDateAgainstDOB(dobValue, insrdSinceDtValue, dobLabel, insrdSinceDtLabel);
    }
    if (claimFreeDateEdit.indexOf("DOB") > -1 || claimFreeDateEdit.indexOf("ALL") > -1) {
        msg += validateDateAgainstDOB(dobValue, clmsFreeDtValue, dobLabel, clmsFreeDtLabel);
    }
    if (lossFreeDateEdit.indexOf("DOB") > -1 || lossFreeDateEdit.indexOf("ALL") > -1) {
        msg += validateDateAgainstDOB(dobValue, lossFreeDtValue, dobLabel, lossFreeDtLabel);
    }

    msg += validateDateAgainstDOB(dobValue, deceasedDateValue, dobLabel, deceasedDateLabel);
    if (msg != '') {
        alert(msg);
        return false;
    }
    return true;
}

//-----------------------------------------------------------------------------
// The entity modify JSP needs to call this function if the process is
// "showDups".  When showing duplicates, the page should act as if data has been
// changed, because data changes have not been saved.
//-----------------------------------------------------------------------------
function setIsChangedFlag(booleanValue) {
    isChanged = booleanValue;
}

function getCurrentlySelectedGridId() {
    //override xmlproc's getCurrentlySelectedGridId() to avoid JS error popup,
    //when select row in Grid and then click button (submit form)
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
    if (isChanged) {
        if (!confirm(ciDataChangedConfirmation)) {
            return false;
        }
    }
    return cisEntityFolderIsOkToChangePages(id, url);
}
//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return isChanged;
}
//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}

function viewPolicySummary(){
    var url = "ciPolicies.do?pk=" + getObjectValue("pk");
    setWindowLocation(url);
}
function validateDateField(fldVal, label, beforeToday) {
    var todayVal = formatDate(new Date(), 'mm/dd/yyyy');
    if (!isStringValue(fldVal)) {
        return '';
    }
    if (beforeToday) {
        if (isDate2OnOrAfterDate1(fldVal, todayVal) != 'Y') {
            return (getMessage("ci.entity.message.dateValue.beforeToday", new Array(label, fldVal)) + "\n");
        } else {
            return '';
        }
    } else {
        if (isDate2OnOrAfterDate1(todayVal, fldVal) != 'Y') {
            return (getMessage("ci.entity.message.dateValue.afterToday", new Array(label, fldVal)) + "\n");
        } else {
            return '';
        }
    }
}

//-----------------------------------------------------------------------------
// Handle Onload event.
//-----------------------------------------------------------------------------
function handleOnLoad() {
    if (window.italicsArrayInPageLevel) {
        commonOnSetButtonItalics();
    }
}

function getRecordExistsUrl() {
    var url = getCSPath() + "/recordexistsmgr/maintainRecordExists.do?process=retrieveRecordExistsIndicator" +
        "&entityId=" + getObjectValue("pk") +
        "&pageCode=CI_ENTITY_MODIFY" +
        "&subsystemId=CS";
    return url;
}

//-----------------------------------------------------------------------------
// view special conditon warning messsages
//-----------------------------------------------------------------------------
function preViewSpecialConditionMessages() {
    var functionExists = eval("window.viewSpecialConditionMessages");
    if (functionExists) {
        var entityId = getObjectValue("pk");
        window.viewSpecialConditionMessages("OASIS PM", "CLIENT", "CLIENT_ID", entityId);
    }
}
