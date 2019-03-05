//-----------------------------------------------------------------------------
// Opens the entity mini popup window.The thisopenEntityMiniPopupWin in common.js uses openDivPopup().
// It seems openDivPopup() has some problems when the main page is in a small iframe.
// The iframe will limit the popup div's size.So here we create a new function.
// (C) 2010 Delphi Technology, inc. (dti)
// Date:
// Author:
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 09/13/2010       wfu         111776: Replaced hardcode string with resource definition
// 06/08/2015       wdang       163197: 1) Removed the function thisopenEntityMiniPopupWin().
//                                      2) Added function isOpenEntityMiniPopupByFrame() which will be called
//                                         by thisopenEntityMiniPopupWin() in common.js file.
//-----------------------------------------------------------------------------
function isOpenEntityMiniPopupByFrame() {
    return true;
}
