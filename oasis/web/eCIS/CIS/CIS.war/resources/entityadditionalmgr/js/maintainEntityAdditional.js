//-----------------------------------------------------------------------------
// Functions to support EntityAdditional page.
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
//   02/11/2011      Michael      for issue 113889
//   04/26/2011      Michael      for issue 119486
//   05/18/2011      Michael      for issue 120505
//  10/28/2011       Michael      for issue 126724
//   10/16/2018        dpang      195835 - Grid replacement.
//-----------------------------------------------------------------------------

var isChanged = false;
//Confirmed with Michael Li to use below date field ids.
var effFrDtFldID = "requestEffectiveDate";
var effToDtFldID = "requestExpirationDate";
var rowid = "-1";

//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {
    switch (btnID) {
        case 'save':
            if (!validate(document.forms[0])) {
                return;
            }
            if (!validateAllEntityAdditionalFields()) {
                return;
            }
            commonOnSubmit('saveAllEntityAdditionals', true, true, true);
            break;

        case 'refresh':
            if (isChanged && !confirm(ciRefreshPageConfirmation)) {
                return;
            }

            if (getObjectValue("sqlOperation") == 'INSERT') {
                clearFormFields(document.forms[0]);
            }
            setObjectValue("process", 'loadAllAvailableEntityAdditionals');
            submitFirstForm();
            break;
    }
}

//-----------------------------------------------------------------------------
// Validate all entityAdditional fields.
//-----------------------------------------------------------------------------
function validateAllEntityAdditionalFields() {
  var coll = document.forms[0].elements;
  var effFrDtValue = '';
  var effFrDtLabel = '';
  var effToDtValue = '';
  var effToDtLabel = '';
  var msg = '';

  for (var i = 0; i < coll.length; i++) {
    if (coll[i].name == effFrDtFldID) {
      effFrDtValue = coll[i].value;
      effFrDtLabel = getLabel(coll[i].name);
    }
    else if (coll[i].name == effToDtFldID) {
      effToDtValue = coll[i].value;
      effToDtLabel = getLabel(coll[i].name);
    }
  }
  var date1OnOrAfterDate2 = isDate2OnOrAfterDate1(effFrDtValue, effToDtValue);
  if (date1OnOrAfterDate2 == 'N') {
    msg += getMessage("ci.common.error.certifiedDate.after", [effToDtLabel + ' (' + effToDtValue + ')', effFrDtLabel + ' (' + effFrDtValue + ')']) + "\n";
  }
  if (msg != '') {
    alert(msg);
    return false;
  }
  return true;
}
//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
  isChanged = true;
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
  if (isChanged) {
    if (!confirm(ciDataChangedConfirmation) ) {
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


//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return isChanged;
}

function find(findId) {
    if (findId.toUpperCase() == "PRODUCERNAME") {
        if (getObject("producerName") && !getObject("producerName").disabled) {
            var functionExists = eval("window.openEntitySelectWinFullName");

            if (functionExists) {
                openEntitySelectWinFullName('producerEntityId', 'producerName', 'handleOnFindClient()');
            }
        }
    }
}

function handleOnFindClient() {
    isChanged = true;
}