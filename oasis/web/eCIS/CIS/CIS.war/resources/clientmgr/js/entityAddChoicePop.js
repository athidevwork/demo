//-----------------------------------------------------------------------------
// Functions to support Add Entity Choice Popup page.
// Author: kshen
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 02/16/2007    kshen       Modified the size of add entity window. (iss68160)
// 12/08/2008    Leo         change for issue 88609.
// 08/25/2010    kshen       Encoded the URL for adding entity.
// 11/10/2011    kshen       Issue 126394
// 03/12/2014    hxk         Issue 152518
//                           1)  Get value from radio button to determine entity type and
//                               add entType parameter to URL.
// 03/20/2014                Issue 151540
//                           1)  add DBA Name field's data into URL in js to pass it,
//                               so it can be accessed from the request and set into the form field.
// 10/30/2014    Elvin       Issue 158667: pass in country code/email address from search
//-----------------------------------------------------------------------------
function handleOnChange(field) {
}
function btnClick(btnID) {
    if(btnID=='cancel') {
        if(getParentWindow() && !getParentWindow().closed) {
            getParentWindow().focus();
        }
        baseCloseWindow();
    } else if(btnID=='add') {

        window.resizeTo(850, 600);
        var chooseAddingEntity = getRadioButtonValue(getObject("chooseAddingEntity"));
        var url = '';
        if(chooseAddingEntity == 'addOrg' && getObjectValue('pageSource') == 'entitySearch')
            url = 'ciEntityOrgAdd.do';
        else if(chooseAddingEntity == 'addPer' && getObjectValue('pageSource') == 'entitySearch')
            url = 'ciEntityPersonAdd.do';
        else if(chooseAddingEntity == 'addOrg')
            url = 'ciEntityOrgAddPop.do';
        else if(chooseAddingEntity == 'addPer')
            url = 'ciEntityPersonAddPop.do';

        var entTypeRadio =  getObject("chooseAddingEntity") ;
        var entType = "";

        for (var i = 0;entTypeRadio.length;i++) {
            if  (entTypeRadio[i].checked) {
                entType = entTypeRadio[i].value;
                break;
            }
        }
        if (entType == "addOrg") {
            entType = "O";

        }  else {
            entType = 'P';
        }

        url = url+"?lNm=" +encodeURIComponent(getObjectValue('lNm'))+
                   "&fNm=" + encodeURIComponent(getObjectValue('fNm')) +
                   "&taxId="+getObjectValue('taxId')+
                   "&dob="+getObjectValue('dob')+
                   "&cls="+getObjectValue('cls')+
                   "&city="+encodeURIComponent(getObjectValue('city'))+
                   "&st="+getObjectValue('st')+
                   "&zip=" + getObjectValue('zip') +
                   "&cnty=" + getObjectValue('cnty') +
                   "&subCls=" + getObjectValue("subCls") +
                   "&subType=" + getObjectValue("subType") +
                   "&dbaName=" + encodeURIComponent(getObjectValue("dbaName")) +
                   "&countryCode=" + getObjectValue('countryCode') +
                   "&emailAddress=" + encodeURIComponent(getObjectValue('emailAddress')) +
                   "&entType=" + entType;

        if (getObject('pageSource').value == 'entitySearch'){
            getParentWindow().location.href = url;
            baseCloseWindow();
        } else {
            url = url + "&process=initPage";
            setWindowLocation(url);
        }

    }
}
