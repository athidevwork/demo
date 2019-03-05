/// Functions used on the Main page to initiate and handle the response from denyQuote.jsp
//this function is executed in the main parent page
function denyQuote() {
    postAjaxSubmit("/policymgr/denyQuote.do", "validateDenyQuote");
}
function submitDeny() {
    postAjaxSubmit("/policymgr/denyQuote.do", "denyQuote")
}
function handleOnCaptureDenyDone(denyReason, denyEffDate, comments) {
    // insert fields dyanamically to the caller's form
    // This function is called by  clicking the button Done from deny page.
    objectDenyReason = setInputFormField("denyReason", denyReason);
    objectDenyEffDate = setInputFormField("denyEffDate", denyEffDate);
    objectComments = setInputFormField("comments", comments);
    submitDeny();
}
