/*
 Description: js file for processExperienceComponent.jsp

 Author:
 Date:


 Revision Date    Revised By  Description
 -----------------------------------------------------------------------------
 07/12/2017       lzhang      186847: Reflect grid replacement project changes
 -----------------------------------------------------------------------------
 (C) 2017 Delphi Technology, inc. (dti)
 */
function hideHeader() {
    hideShowElementByClassName(getObject("globalHeader"), true);
    hideShowElementByClassName(getObject("globalMenu"), true);
}

function handleOnButtonClick(asBtn) {
    switch (asBtn) {
        case 'PROCESS':
            showProcessingImgIndicator();
            document.forms[0].action = getAppPath() +
                                       "/componentmgr/experiencemgr/processExperienceComponent.do?"+ commonGetMenuQueryString() +
                          "&context=" + 'loadAllExperienceDetail'
;
            document.forms[0].process.value = 'loadAllExperienceDetail';
            submitFirstForm();
            break;
        case 'CLEAR':
            var url = getAppPath() + "/componentmgr/experiencemgr/processExperienceComponent.do"
            setWindowLocation(url);
            break;
    }
}