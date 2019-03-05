//-----------------------------------------------------------------------------
// Javascript file for closeMultiCancelPage.jsp
//
// (C) 2010 Delphi Technology, inc. (dti)
// Date:   Sep 02, 2010
// Author: syang
//
// Revision Date    Revised By  Description
//-----------------------------------------------------------------------------
// 08/30/2010       syang       Issue 111417 - Modified performClosePage(), the rating step has been added to multi
//                                             cancellation workflow, we needn't invoke it again when close this page.
// 08/20/2014       jyang       issue 156829 - Removed useless functions, because the closeMultiCancelPage only works
//                                             for multi-cancel COI holder now.
// 11/02/2018       clm         195889 -  Grid replacement using getParentWindow
//-----------------------------------------------------------------------------
function refreshPage() {
    getParentWindow().refreshPage();
}