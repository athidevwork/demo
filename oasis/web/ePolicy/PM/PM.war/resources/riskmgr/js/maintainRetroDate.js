//-----------------------------------------------------------------------------
// Javascript file for maintainRetroDate.jsp.
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   July 07, 2016
// Author: lzhang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 07/11/2016       lzhang       177681 - Initial version: function handleOnSubmit()
//                                        to save retro date
// 03/10/2017       eyin         180675 - Add function handleOnButtonClick() to auto-save
//                                        retro date in new UI tab style.
// 05/23/2017       lzhang       185079 - pass parameter when call getParentWindow()
//-----------------------------------------------------------------------------

function handleOnSubmit(action) {
    var proceed = true;
    switch (action) {
        case 'SAVE':
            document.forms[0].process.value = "saveAllRetroDate";
            break;

        default:
            proceed = false;
    }
    return proceed;
}

function handleOnButtonClick(asBtn){
    switch (asBtn) {
        case 'CLOSE_DIV':
            if(isTabStyle()){
                if(getParentWindow(true).switchPrimaryTabFlg){
                    var functionExists = eval("getParentWindow(true).callBackAutoSaveForFrame");
                    if(functionExists){
                        getParentWindow(true).callBackAutoSaveForFrame(true);
                    }
                }else{
                    var functionExists = eval("getParentWindow(true).refreshPage");
                    if(functionExists){
                        getParentWindow(true).refreshPage();
                    }
                }
            }
            break;
    }
}
