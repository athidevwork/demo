/**
 * Created by mgitelman on 11/17/2015.
 *
 * Modification
 * Date         By          Description
 * -------------------------------------------------------------------------------------------
 * 03/21/2017   Elvin       Issue 169066: add applicationId to support multiple application
 *                                          running at the same time
 * -------------------------------------------------------------------------------------------
 */
var dti = dti || {};
dti.applicationContext = dti.applicationContext || {};
dti.applicationContext.properties = dti.applicationContext.properties || {};

dti.applicationContext.properties = {
};

// load only these properties from the white-list
dti.applicationContext.propertyKeys = dtiAppContextKeys || [    // allow overrides
    "applicationId",
    "contextPath",
    "commonRoot",
    "commonJspRoot",
    "dti.rootHelpUrl",
    "dti.module.rootHelpUrl",
    "dti.technicalSupport.emailAddress",
    "dti.technicalSupport.subject",
    "dti.technicalSupport.enableEmailing"
];

dti.applicationContext.getProperty = function(name) {
    return this.properties[name];
};

dti.applicationContext.loadExternalData = function(requestType, restURI, sendData, callbackFn) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        callbackFn(xhttp);
    };

    var rqType = requestType || "POST";
    xhttp.open(rqType, restURI, true);
    xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhttp.send(JSON.stringify(sendData));
};

dti.applicationContext.loadExternalProperties = function(keys) {
    var propertiesRestURI = dtiContextPath + "/rest/Property/For";

    dti.applicationContext.loadExternalData("POST", propertiesRestURI, keys, function(xhttp) {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            dti.applicationContext.properties = JSON.parse(xhttp.responseText);
            if (dti.applicationContext.useSessionStorage) {
                sessionStorage["dti.applicationContext.properties"] = JSON.stringify(dti.applicationContext.properties ); // store
                dti.applicationContext.reloadedFromServer = true;
            }

            dti.applicationContext.loadUserId();
        }
    });
};

dti.applicationContext.loadUserId = function(keys) {
    // if(!dtiOasisUser) {
    //     var propertiesRestURI = dtiContextPath + "/rest/Users/getCurrent";
    //
    //     dti.applicationContext.loadExternalData("GET", propertiesRestURI, keys, function (xhttp) {
    //         if (xhttp.readyState == 4 && xhttp.status == 200) {
    //             dti.applicationContext.properties["publicUser"] = JSON.parse(xhttp.responseText);
    //             var node = document.querySelector(".dti-user-name");
    //             node.innerText = dti.applicationContext.properties.publicUser.userName;
    //         }
    //     });
    // } else {
    //     dti.applicationContext.properties["publicUser"] = dtiOasisUser;
    //     //alert("Oasis User: "+dti.applicationContext.properties.publicUser.userName +" / "+dti.applicationContext.properties.publicUser.userId)
    //     var node = document.querySelector(".dti-user-name");
    //     node.innerText = dti.applicationContext.properties.publicUser.userName;
    // }
    var propertiesRestURI = dtiContextPath + "/rest/Users/getCurrent";

    dti.applicationContext.loadExternalData("GET", propertiesRestURI, keys, function (xhttp) {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            dti.applicationContext.properties["publicUser"] = JSON.parse(xhttp.responseText);
            var node = document.querySelector(".dti-user-name");
            node.innerText = dti.applicationContext.properties.publicUser.userName;
        }
    });
};


(function loadApplicationProperties(keys) {
    function hasMissingProperties(propertiesCandidate) {
        var propertyKeys = dti.applicationContext.propertyKeys;
        for (var i = 0; i < propertyKeys.length; i++) {
            var key = propertyKeys[i];
            if (!propertiesCandidate[key]) {
                return true;
            }
        }

        return false;
    }

    if (typeof window.sessionStorage != "undefined" ) {
        dti.applicationContext.useSessionStorage = true;
        var propsStored = sessionStorage["dti.applicationContext.properties"];  // retrieve
        if (propsStored) {
            var properties = JSON.parse(propsStored);

            // add support for multiple applications
            var propertiesRestURI = dtiContextPath + "/rest/Property/applicationId";
            dti.applicationContext.loadExternalData("GET", propertiesRestURI, keys, function(xhttp) {
                if (xhttp.readyState == 4 && xhttp.status == 200) {
                    var currentAppId = xhttp.responseText;
                    if (properties.applicationId == currentAppId && !hasMissingProperties(properties)) {
                        dti.applicationContext.properties = JSON.parse(propsStored);
                        dti.applicationContext.loadUserId();
                    } else {
                        dti.applicationContext.loadExternalProperties(keys);
                    }
                }
            });
        } else {
            dti.applicationContext.loadExternalProperties(keys);
        }
    }

})(dti.applicationContext.propertyKeys);


// helper functions to map to JSP-like usage

function getContext() {
    return dti.applicationContext;
}

// if (typeof getContextPath == "undefined" && typeof serverContextPath == "undefined") {     // allow overrides
//     getContextPath = function() {
//         return getContext().properties.contextPath;
//     };
// }

function getContextPath() {
    return getContext().properties.contextPath;
}