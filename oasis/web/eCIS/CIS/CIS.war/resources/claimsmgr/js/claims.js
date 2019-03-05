//-----------------------------------------------------------------------------
// Functions to support CIS Claims Summary
// Author: unknown
// Date:   unknown
// Modifications:
//-----------------------------------------------------------------------------
// 11/12/2018    hxk         Issue  196950
//                           1)  If we are restricting at case or claim level
//                           don't allow access to claim page.
//                           2)  If we are restricting at case level,
//                           don't allow access to case page.

var isChanged = false;
var rowid = -1;


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

function participantsGrid_selectRow(pk) {
    rowid = pk;
    getRow(participantsGrid1, pk);
}

function participantsGrid_setInitialValues() {
}

function companionGrid_selectRow(pk) {
    rowid = pk;
    getRow(companionGrid1, pk);
}

function companionGrid_setInitialValues() {
}

function btnClick(btnID) {
    if (btnID == 'address'
            || btnID == 'phonenumber'
            || btnID == 'entityclass'
            || btnID == 'entityrole'
            || btnID == 'vendor'
            || btnID == 'vendorAddress') {
        // Go to the appropriate page.
        goToEntityModule(btnID, getObjectValue("pk"),
                    getObjectValue("entityName"),
                    getObjectValue("entityType"));
    } else if (btnID == 'entity') {
        goToEntityModify(getObjectValue("pk"),
            getObjectValue("entityType"));
    } else if (btnID == 'add') {
        testgrid_insertrow();
        getRow(testgrid1, lastInsertedId);
        setItem(testgrid1, "CENTITYPK", getObjectValue("pk"));
        setObjectValue("process", "add");
    } else if (btnID == 'refresh') {
        setObjectValue("claimPK", "");
        setObjectValue("process", "loadClaim");
        submitFirstForm();
    } else {
        // Added by Larry on 2007/02/16 for issue #66258 
        // retrieve the web context root for current application
        var appPath = getAppPath();
        // pick up the parent context root
        var parentContextRoot = appPath.replace(/\/[^\/]+$/,'');
        // infer the context url for eClaim
        var cmRoot = getTopNavApplicationUrl("Claims");

        // the following statement commented is for web root config eClaimContextRoot in CIS-Claims Page
        //var cmRoot = getObject("eClaimContextRoot").value;
        var actionUrl = '';
        if (btnID == 'goToClaim') {
            if (getObjectValue("restrictB") == "Y"|
                getObjectValue("restrictCaseB") == "Y"){
                alert("You are not authorized to view this claim");
                return;
            }
            if (!getObject("claimPK")) {
                return;
            }
            var claimPK = getObjectValue("claimPK");
            if (claimPK == null || claimPK == '')
                return;
            var claimNo = getObject("claimPK").options[getObject("claimPK").selectedIndex].text;
            actionUrl = "/cmClaimSearch.do?process=globalSearch&claimNo="+claimNo;
        } else if(btnID == 'goToCase') {
            if (getObjectValue("restrictCaseB") == "Y"){
                alert("You are not authorized to view this case");
                return;
            }
            if (!getObject("casePK")) {
                return;
            }
            var casePK = getObjectValue("casePK");
            if (casePK == null || casePK == '') {
                alert(' This claim does not belong to any case.')
                return;
            }
            actionUrl = "/cmCaseClaimList.do?process=edit&occurrencePK="+casePK;
        }
        var url = cmRoot+actionUrl;
        var mainwin = window.open(url ,'CM','width=1000,height=650,resizable=yes,scrollbars=yes,status=yes,top=5,left=5' );
        mainwin.focus();
    }
}

//-----------------------------------------------------------------------------
// OnChange event handler
//-----------------------------------------------------------------------------
function handleOnChange(field) {
    isChanged = true;
    if (field.name == 'claimPK') {
        setObjectValue("process", "loadClaim");
        submitFirstForm();
    }
}
//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return isChanged;
}