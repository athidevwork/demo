//-----------------------------------------------------------------------------
// Javascript file for SelectManuscript.jsp.
//
// (C) 2003 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 05/01/2012       sxm         133179 - Reset the record set to first after looping through it.
// 03/13/2017       eyin        180675 - Used 'getReturnCtxOfDivPopUp()' to call 'addManuscripts'.
// 11/29/2018       huixu       195889 - gird replacement

//-----------------------------------------------------------------------------
function handleOnButtonClick(asBtn) {
    var oManuscriptList = new Array();
    switch (asBtn) {
        case 'DONE':
            var count = 0;
            first(selectManuscriptGrid1);
            while (!selectManuscriptGrid1.recordset.eof) {
                if (selectManuscriptGrid1.recordset("CSELECT_IND").value == "-1") {
                    oManuscriptList[count] = getObjectFromRecordset(selectManuscriptGrid1);
                    count++;
                }
                next(selectManuscriptGrid1);
            }
            first(selectManuscriptGrid1);
            if (count == 0) {
                handleError(getMessage("pm.maintainManu.addManu.noSelection.error"));
            }
            else {
                getReturnCtxOfDivPopUp().addManuscripts(oManuscriptList, true);
                commonOnButtonClick("CLOSE_RO_DIV");
            }
            break;
        case 'CANCEL':
            commonOnButtonClick("CLOSE_RO_DIV");
            break;
    }
}

function selectManuscriptForm_btnClick(asBtn) {
    updateAllSelectInd(asBtn);
}
