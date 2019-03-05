/**
 * Created by jdingle on 8/12/2016.
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 09/26/2018       kshen       195835. CIS grid replacement.
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

            if (!validateAdditionalPersonFields()) {
                return;
            }

            setInputFormField("process", "saveAllEntity");
            showProcessingImgIndicator();
            submitFirstForm();
            break;
        case 'saveclose':
            setObjectValue("saveAndClose","Y");
            btnClick("save");
            break;
        case 'close':
            goToModule('search');
            break;
        default:
            break;
    }
}

//-----------------------------------------------------------------------------
// validation rules for person
// -----------------------------------------------------------------------------
function validateAdditionalPersonFields() {
    /* validate education */
    if (!validateEducationFields("")){
        return false;
    }
    if (!validateEducationFields("2")) {
        return false;
    }
    if (!validateEducationFields("3")) {
        return false;
    }
    return true;
}

function validateEducationFields(suffix) {
    if (!isStringValue(suffix)) {
        suffix = "";
    }
    var i ="";
    if (suffix =="") {
        i="1";
    } else {
        i=suffix;
    }
    var name = getObjectValue("educationProfile"+suffix+"_institutionName");
    if (!isStringValue(name)) {
        return true;
    }
    var fromBd = getObjectValue(dobFldID);
    var fromDd = getObjectValue(deceasedDateFldID);
    var fromGy = getObjectValue("educationProfile"+suffix+"_graduationYear");
    var fromGy2 = parseInt(fromGy);
    var fromDt =  getObjectValue("educationProfile"+suffix+"_effectiveFromDate");
    var toDt =  getObjectValue("educationProfile"+suffix+"_effectiveToDate");
    var fromSc = getObjectValue("educationProfile"+suffix+"_institutionStateCode");
    var fromCc = getObjectValue("educationProfile"+suffix+"_institutionCountryCode");
    if (fromGy != '' && fromGy.length != 4) {
        alert(replace(getMessage("ci.entity.message.year.invalid", new Array(i)),"row","column"));
        return false;
    }
    else if (fromGy2 >= 3000 || fromGy2 <= 1900) {
        alert(replace(getMessage("ci.entity.message.year.outOfRange", new Array(i)),"row","column"));
        return false;
    }
    if (fromGy !='' && fromBd !='') {                                                  //bug fix
        if (fromGy <= new Date(fromBd).getFullYear()) {
            alert(getMessage("ci.entity.message.year.later", new Array(fromBd)));
            return false;
        }
    }
    // Set up numeric dates for some validation
    if (fromDt != '')  {
        var numFromDt = parseFloat(fromDt.substr(6, 4) +
            fromDt.substr(0,2) +
            fromDt.substr(3,2));
    }

    if (fromBd != '' ) {
        var numBDt = parseFloat(fromBd.substr(0, 4) +
            fromBd.substr(5,2) +
            fromBd.substr(8,2)) ;
    }
    if (toDt != '') {
        var numToDt = parseFloat(toDt.substr(6, 4) +
            toDt.substr(0,2) +
            toDt.substr(3,2));
    }
    if (fromDd != '') {
        var numDDt = parseFloat(fromDd.substr(0, 4) +
            fromDd.substr(5,2) +
            fromDd.substr(8,2));
    }

    if (fromDt != '' && toDt != '' ) {
        if (numFromDt > numToDt) {
            alert(replace(getMessage("ci.entity.message.endDate.afterStartDate", new Array((i))),"row","column"));
            return false;
        }
    }
    if (fromDt == '' && toDt != '') {
        alert(replace(getMessage("ci.entity.message.startDate.entered", new Array((i))),"row","column"));
        return false;
    }

    if (fromDt != '' && fromBd != '') {
        if ( new Date(fromDt) < new Date(fromBd)){  //bug fix
            alert(replace(getMessage("ci.entity.message.startDate.earlier", new Array(fromBd.substr(0,10), i)),"row","column"));
            return false;
        }
    }
    if (fromDt != '' && fromDd != '') {
        if ( new Date(fromDt) > new Date(fromDd)) {  //bug fix
            alert(replace(getMessage("ci.entity.message.startDate.notLater", new Array(fromDd.substr(0,10), i)),"row","column"));
            return false;
        }
    }
    if (toDt != '' && fromDd != '') {
        if (new Date(toDt) > new Date(fromDd)) {
            alert(replace(getMessage("ci.entity.message.endDate.notLater", new Array(fromDd.substr(0,10), i)),"row","column"));
            return false;
        }
    }

    if ((fromSc != '' && fromCc == '') && ( fromCc == '' && fromSc != '-1')) {
        setObjectValue("educationProfile"+suffix+"_institutionCountryCode", "USA");
        fromCc = "USA";
    }
    if (((fromCc == 'USA' && fromSc == "") || (fromCc == 'USA' && fromSc == "-1")) || (( fromCc == 'U.S.A' && fromSc == "") || (fromCc == 'U.S.A' && fromSc == "-1"))) {
        alert(replace(getMessage("ci.entity.message.stateCode.required", new Array(i)),"row","column"));
        return false;
    }
    if ((fromCc.valueOf() != '' && fromCc.valueOf() != 'USA') && (fromCc.valueOf() != '' && fromCc.valueOf() != 'U.S.A')) {
        setObjectValue("educationProfile"+suffix+"_institutionStateCode","");
    }

    return true;
}


