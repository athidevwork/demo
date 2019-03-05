//-----------------------------------------------------------------------------
// Functions to support Audit tab pages.
// Author:
// Date:
// Modifications:
//-----------------------------------------------------------------------------
// 07/13/2016    Elvin    Issue 177515: change goToSource to commonGoToSource
// 07/14/2016    ylu      177660: skip data change check, due to it is read only page
// 04/02/2018    hxk      Issue 109175: Entity Role refactor
// 05/28/2018    ylu      Issue 109175: fix bug for refactor: click clear button pop JS error
// 10/9/2018     dzou     Grid replacement
//-----------------------------------------------------------------------------
var rowid = "-1";
function CIEntityRoleListForm_btnClick(asBtn) {
    switch (asBtn) {
        case 'SELECT':
            testgrid_updatenode("CSELECT_IND", -1);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
        case 'DESELECT':
            testgrid_updatenode("CSELECT_IND", 0);
            first(testgrid1);
            gotopage(testgrid, 'F');
            selectFirstRowInGrid("testgrid");
            break;
    }
}
//-----------------------------------------------------------------------------
// Button handler
//-----------------------------------------------------------------------------
function btnClick(btnID) {

  if (btnID == 'entity') {
    goToEntityModify(getObjectValue("pk"),
        getObjectValue("entityType"));
  }
  else if (btnID == 'entityrole') {
    return;
  }
  else if (btnID == 'address'
    || btnID == 'phonenumber'
    || btnID == 'entityclass'
    || btnID == 'vendor'
    || btnID == 'vendorAddress') {
    // Go to the appropriate page.
    goToEntityModule(btnID, getObjectValue("pk"),
        getObjectValue("entityName"),
        getObjectValue("entityType"));
  }
  else if (btnID == 'clear') {
    setObjectValue("searchCriteria_roleTypeCode", "");
    setObjectValue("searchCriteria_effectiveFromDate", "");
    setObjectValue("searchCriteria_effectiveToDate", "");
    setObjectValue("searchCriteria_externalId", "");
  }
  else if (btnID == 'generateCoi') {
      if (validateCoiHolderSelection()) {
          var url = getTopNavApplicationUrl("Policy") + "/riskmgr/coimgr/standaloneGenerateCoi.do?entityId=" +
                    getObjectValue("pk") + "&parentGridId=testgrid";
          var divPopupId = openDivPopup("", url, true, true, "", "", "700", "600", "", "", "", false);
      }
  }
  else {
    if (isDate2OnOrAfterDate1(
            getObjectValue("searchCriteria_effectiveFromDate"),
            getObjectValue("searchCriteria_effectiveToDate")) == 'N') {
      var labelFrom = getLabel(getObject("searchCriteria_effectiveFromDate"));
      var labelTo = getLabel(getObject("searchCriteria_effectiveToDate"));
      alert(getMessage("ci.common.error.certifiedDate.after", new Array(labelTo, labelFrom)));
    }
    else {
      showProcessingImgIndicator();
      setObjectValue("process", "loadRoleList");
      submitFirstForm();
    }
  }

}

function handleOnChange(field) {
  return;
}

function testgrid_selectRow(pk) {
  rowid = pk;
}

//-----------------------------------------------------------------------------
// Determines if OK to change pages.
//-----------------------------------------------------------------------------
function isOkToChangePages(id, url) {
  return cisEntityFolderIsOkToChangePages(id, url);
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
  return cisEntityFolderGetMenuQueryString(id, url);
}

//-----------------------------------------------------------------------------
// To validate if no COI Holder is selected. Joe 12/21/2007
//-----------------------------------------------------------------------------
function validateCoiHolderSelection() {
    if (typeof testgrid1 != "undefined") {
        if (!isEmptyRecordset(testgrid1.recordset)) {
            var validRecords = testgrid1.documentElement.selectNodes(
                    "//ROW[CSELECT_IND='-1' and (CROLETYPECODE='COI_HOLDER' or CROLETYPECODE='COI_HOLDER(PENDING)') and CEFFECTIVETODATE='01/01/3000']");

            //alert(invalidRecords2.length + "|" + invalidRecords.length);

            if (validRecords.length <= 0) {
                alert(getMessage("ci.entity.message.coiHolder.select"));
                return false;
            }
            var invalidRecords2= testgrid1.documentElement.selectNodes(
                "//ROW[CSELECT_IND='-1' and CROLETYPECODE='COI_HOLDER(PENDING)' and CEFFECTIVETODATE='01/01/3000']");

            if (invalidRecords2.length > 0 ) {
                alert(getMessage("ci.entity.message.coiHolder.Pendingselect"));
                return false;
            }
            var invalidRecords = testgrid1.documentElement.selectNodes(
                "//ROW[CSELECT_IND='-1' and CROLETYPECODE='COI_HOLDER' and CEFFECTIVETODATE!='01/01/3000' " +
                "or CSELECT_IND='-1' and CROLETYPECODE!='COI_HOLDER']");
            if (invalidRecords.length > 0) {
                alert(getMessage("ci.entity.message.selection.invalid", new Array("\n")));
                return false;
            }
        } else {
            alert(getMessage("ci.entity.message.coiHolder.oneSelect"));
            return false;
        }
    } else {
        alert(getMessage("ci.entity.message.coiHolder.oneSelect"));
        return false;
    }
    return true;
}

function handleGoToSource(sourceNo, sourceTableName, sourcePk) {
    var isOkToProceed = true;
    if (sourceTableName == 'OCCURRENCE') {
        if (getObjectValue("restrictSourceList").indexOf("," + sourceNo + ",") >= 0){
            alert(getMessage("ci.claim.restrict.message.noAuthority.case"));
            isOkToProceed = false;
        }
    } else if(sourceTableName == 'CLAIM') {
        if (getObjectValue("restrictSourceList").indexOf("," + sourceNo + ",") >= 0){
            alert(getMessage("ci.claim.restrict.message.noAuthority.claim"));
            isOkToProceed = false;
        }
    }
    return isOkToProceed;
}

function getGridId(){
    return 'testgrid';
}
