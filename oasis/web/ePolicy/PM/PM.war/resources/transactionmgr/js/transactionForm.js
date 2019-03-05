//    function viewDocument(fullPath) {
//        if (fullPath.length < 2 || // too short to be a file identifier
//            fullPath.lastIndexOf("/") + 1 == fullPath.length) {
//            // it ends with /, so it is not a complete file identifier
//            return;
//        }
//        var url= "";
//        url = "file:" + fullPath;
//        window.open(url, "", "location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
//    }

    // use the link instead of the view button:
//-----------------------------------------------------------------------------
// View document
//-----------------------------------------------------------------------------
function viewDocument(decodedFileFullPath) {
    if (decodedFileFullPath.length < 2 || // too short to be a file identifier
        decodedFileFullPath.lastIndexOf("/") + 1 == decodedFileFullPath.length) {
        // it ends with /, so it is not a complete file identifier
        return;
    }
    var url = getAppPath() +
              "/transactionmgr/maintainTransaction.do?process=viewDocument&decodedFileFullPath=" + decodedFileFullPath;
    window.open(url, "", "location=no,menubar=no,toolbar=no,directories=no,resizable=yes,opyhistory=no");
}