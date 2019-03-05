/**
 *  Created by jdingle on 03/08/2016.
 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
   05/12/2016     jld         refresh account grid on fee reversal.
   10/15/2018     dpang       195835: Grid replacement
 -----------------------------------------------------------------------------
 */

function handleOnLoad() {
    if (hasObject("feeProcessed")) {
        if (getObjectValue("feeProcessed") == "Y") {
            closeWindow(function () {
                var parentWindow = getParentWindow();
                if (parentWindow && !parentWindow.closed && parentWindow.refreshPage) {
                    parentWindow.refreshPage();
                }
            });
        }
    }
}

//-----------------------------------------------------------------------------
// click on button
//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
 switch (asBtn) {
  case 'process':
   var dataArray = getSelectedKeys(testGrid1);
   if (dataArray.length == 0) {
    alert(getMessage("js.select.row"));
    return;
   }
   for (var i = 0; i < dataArray.length; i++) {
    selectRow("testGrid", dataArray[i]);
    testGrid1.recordset("UPDATE_IND").value = 'Y';
   }
   setInputFormField("txtXML", getSelectedRowData("testGrid"));
   setInputFormField("process", "saveAllServiceCharges");
   submitFirstForm();
   break;
  case 'close':
   closeWindow();
   break;
  default:
   break;
 }
}

