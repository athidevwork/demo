//-----------------------------------------------------------------------------
// Author: kshen
// Modifications:
//
// 01/08/2018    dpang  190606 - Create a hidden link element for opening email client without using a new window.
// 11/01/2018    kshen  196632. CS grid replacement.
//-----------------------------------------------------------------------------
function openEmailClient(parms) {
    var length = parms.length;
    var toEmailAddresses = "";
    var ccEmailAddresses = "";
    var bccEmailAddresses = "";
    var subject = "";
    var body = "";

    //location.href = "mailto:aaa@test.com?cc=test1@test.com;test2@test.com&bcc=test1@test.com&subject=test&body=test";
    for (var i = 0; i < length; i++) {
        var tempStr = parms[i];
        // Get the usage
        var usage = tempStr.substring(tempStr.indexOf("[") + 1, tempStr.indexOf("]"));
        tempStr = tempStr.substring(tempStr.indexOf("]") + 1);

        // Get the data type, text value or an entity
        var dataType = tempStr.substring(tempStr.indexOf("[") + 1, tempStr.indexOf("]"));
        tempStr = tempStr.substring(tempStr.indexOf("]") + 1);

        // Get the value.
        var value = tempStr.substring(tempStr.indexOf("[") + 1, tempStr.length - 1);
        // If it's a field id, get the value from object.
        if ((usage == "to" || usage == "cc" || usage == "bcc")
                && (value.substr(0, 1) == "^" && value.substr(value.length - 1) == "^")) {
            var objectId = value.substring(value.indexOf("^") + 1, value.length - 1);
            if (hasObject(objectId)) {
                value = getObjectValue(value.substring(value.indexOf("^") + 1, value.length - 1));
            } else {
                value = "";
            }
        }

        // If it's an entity, get all email address for the entity.
        if (dataType == "entity"
            && (usage == "to" || usage == "cc" || usage == "bcc")
            && value != "") {
            var url = getCISPath() + "/maintainEmailAddressAction.do?process=loadAllClientEmailAddressForAjax"
                + "&entityId=" + value;
            new AJAXRequest("get", url, '', function(ajax) {
                if (ajax.readyState == 4) {
                    if (ajax.status == 200) {
                        value = ajax.responseText;
                    }
                }
            }, false);
        }

        // append to email property.
        if (value != "") {
            switch (usage) {
                case "to":
                    if (toEmailAddresses != "") {
                        toEmailAddresses += ";";
                    }
                    toEmailAddresses += value;
                    break;
                case "cc":
                    if (ccEmailAddresses != "") {
                        ccEmailAddresses += ";";
                    }
                    ccEmailAddresses += value;
                    break;
                case "bcc":
                    if (bccEmailAddresses != "") {
                        bccEmailAddresses += ";";
                    }
                    bccEmailAddresses += value;
                    break;
                case "subject":
                    subject = value;
                    break;
                case "body":
                    body = value;
                    break;
            }
        }
    }

    if (toEmailAddresses != "" || ccEmailAddresses != "" || bccEmailAddresses != "" || subject != "" || body != "") {
        var emailUrl = "";
        if (emailClientType == "OASIS") {
            emailUrl = "/oasisMail.do?to=" + toEmailAddresses;
        } else {
            emailUrl = "mailto:" + toEmailAddresses + "?dummyParm=dummyValue";
        }

        if (ccEmailAddresses != "") {
            emailUrl += "&cc=" + ccEmailAddresses;
        }
        if (bccEmailAddresses != "") {
            emailUrl += "&bcc=" + bccEmailAddresses;
        }
        if (subject != "") {
            emailUrl += "&subject=" + escape(subject);
        }
        if (body != "") {
            emailUrl += "&body=" + escape(body);
        }

        if (emailClientType == "OASIS") {
            var isFrame = (window.frameElement != null);
            var path = "";
            if (isFrame) {
                path = getParentWindow().getCSPath();
            } else {
                path = getCSPath();
            }
            path += emailUrl;
            if (isFrame) {
                getParentWindow().openDivPopup("", path, false, true, null, null, 800, 480, "", "", "", true, "", "", false);
            } else {
                //openDivPopup("Oasis Mail", path, false, true, null, null, 650, 450, 650, 450);
                openDivPopup("", path, false, true, null, null, 800, 480, "", "", "", true, "", "", false);
            }
        } else {
            var mailToLink = $("<a id='mailToLink' style='display: none'/>").attr('href', emailUrl);
            mailToLink.appendTo("body");
            mailToLink[0].click();
            //Remove the link element after opening email client.
            mailToLink.remove();
        }
    } else {
        alert(getMessage("cs.oasisMail.noEmailAddressForClient"));
    }
}