//Added by Fred on 1/11/2007
//To confirm changes.
function confirmChanges() {
    return isChanged;
}

//-----------------------------------------------------------------------------
// Add parameters to the menu query string. this is common function.
//-----------------------------------------------------------------------------
function getMenuQueryString(id, url) {
    return cisEntityFolderGetMenuQueryString(id, url);
}