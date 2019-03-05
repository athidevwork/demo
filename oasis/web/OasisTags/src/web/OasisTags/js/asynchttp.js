/* Starts a refresh, passing all input elements in a form
* to a URL.  This now uses the AJAX approach.
*
* Parameters:
* theform  - your form object. OPTIONAL - If you do not provide it, the first form document.forms[0] will be used.
* url      - The URL to invoke.  OPTIONAL - If you do not provide it, the URL
*            defaults to theform.action
* callbackFunction - The JavaScript function to invoke when the refresh
*                    is complete.  If you do not provide it, the callback
*                    function defaults to the function "downloadDone" which is
*                    in this JS file.
* async    - create Ajax Request in async mode

* Revision Date    Revised By  Description
* ----------------------------------------------------------------------------
* 08/11/2010       wfu         109874: Changed to handle url encoding
* 08/12/2010       wfu         109874: Changed function name to urlEncode
* 12/10/2010       tzhao       115361: Changed startRefresh() function to fix the encoding defect of chinese characters in ajax data.
* 12/13/2010       tzhao       115361: Changed encodeURI() to encodeURIComponent() to encode special characters such as "&".
* 12/27/2017       kshen       Grid replacement: fix the problem about call AJAX in an early version of firefox.
* 03/19/2018       cesar       189605 - Added updatePageToken() to update page token.
* 11/13/2018       wreeder     196147 - Refactor startRefresh to expose logic for getting the form fields for Ajax and preparing the URL for an Ajax request
*                                     - Create updatePageTokenForJson() method to update the CSRF token from JSON data returned from an Ajax request
* 12/10/2018       cesar       197486 - Refactored parseXMLForCSRFToken() getting token from the AJAX_RESPONSE.
* ----------------------------------------------------------------------------
*/
function startRefresh(theform, url, callbackFunction, async) {
    if (typeof theform == 'undefined' || theform == null) {
        theform = document.forms[0];
    }
    // default to the form's action
    if (url == null)
        url = theform.getAttribute("action");
    var data = getFormFieldsForAjaxData(theform);
    new AJAXRequest("post", url, data, callbackFunction, async);
}

function getFormFieldsForAjaxData(theform, dataType) {
    if (typeof theform == 'undefined' || theform == null) {
        theform = document.forms[0];
    }
    var asJson = true;
    if (typeof dataType === "undefined" || dataType.toLowerCase() !== "json") {
        asJson = false;
    }

    // default to the form's action
    var data = "";
    if (asJson) {
        data = "{ ";
    }
    // build URL
    var els = theform.elements;
    var sz = els.length;
    var isFirst = true;
    for (var i = 0; i < sz; i++) {
        // No need to submit the CROWID, exclude it!
        if (els[i].name != "CROWID") {
            var object = els[i];
            if (object.tagName.toLowerCase() == "select" && object.type && object.type == "select-multiple") {
                // If the type is multi select list, add a parameter for each selected option
                var hasSelectedOption = false;
                for (var j = 0; j < object.options.length; j++) {
                    if (object.options[j].selected) {
                        hasSelectedOption = true;
                        data = addParameter(data, object.name, object.id, object.options[j].value, isFirst, asJson);
                        isFirst = false;
                    }
                }
                if (!hasSelectedOption) {
                    data = addParameter(data, object.name, object.id, "", isFirst, asJson);
                    isFirst = false;
                }
            } else {
                if (object.type.toLowerCase() !== "button") {
                    data = addParameter(data, object.name, object.id, object.value, isFirst, asJson);
                    isFirst = false;
                }
            }
        }
    }
    if (asJson) {
        data += " }";
        data = JSON.parse(data);
    }
    return data;
}

function addParameter(data, parameterName, parameterId, value, isFirst, asJson){

    var result =  data;

    parameterName = (parameterName && parameterName !== "") ? parameterName : parameterId;
    if (asJson) {
        if (!isFirst) {
            result += ", ";
        }
        result += "\"" + parameterName + "\": \"" + encodeURIComponent(value) + "\"";
    }
    else {
        if (!isFirst) {
            result += "&";
        }
        // escape() couldn't handle Chinese character encoding correctly.
        // Change escape() to encodeURIComponent().
        result += parameterName + "=" + encodeURIComponent(value);
    }
    return result;
}

/* event handler for dropdown data loaded.
* This was left in for backward compatibility.  Please use
* processAJAXReturn replaces this.
*/
function downloadDone(ajax) {
	processAJAXReturn(ajax);
}

// reset SELECT options
function clearDropDown(obj) {
  if (obj) {
    if (obj.type == 'select-one' || obj.type == 'select-multiple') {
      var options = obj.options;
      // if no options list, get out
      if (!options)
        return;
      while (options.length > 0)
        options.remove(0);
    }
  }
}

// update the SELECT options
function setDropDown(obj,doc) {
  if (obj) {
    if (obj.type == 'select-one' || obj.type == 'select-multiple') {
      var selval = obj.value;
      if (!obj.options)
        return;
      clearDropDown(obj);
      var options = doc.getElementsByTagName("row");
      for(var i=0;i<options.length;i++) {
        var val = options.item(i).getElementsByTagName("value").item(0).firstChild.nodeValue;
        var label = options.item(i).getElementsByTagName("label").item(0).firstChild.nodeValue;
        var oOption = document.createElement("OPTION");
        obj.options.add(oOption);
        oOption.innerText = label;
        oOption.value = val;

        if (val==selval)
          obj.selectedIndex=i;

      }
    }
  }
}

/*
 * AJAXRequest: An encapsulated AJAX request. To run, call
 * new AJAXRequest( method, url, async, process, data )
 *
 * method = POST or GET, defaults to POST
 * url = The URL to send the request to
 * async = true for async processing, false for sync processing. Defaults to true
 * process = Callback function, defaults to processAJAXReturn
 * data = data to send in the case of a POST
 * dosend = true to send the request, defaults to true.
 *
 */
function AJAXRequest( method, url, data, process, async, dosend) {
    // self = this; creates a pointer to the current function
    try {
        var self = this;

        self.AJAX = new XMLHttpRequest();

        // if no callback process is specified, then assing a default which executes the code returned by the server
        if (typeof process == 'undefined' || process == null) {
            process = processAJAXReturn;
        }

        self.process = process;

        // if no method specified, then default to POST
        if (!method) {
            method = "POST";
        }

        method = method.toUpperCase();

        if (typeof async == 'undefined' || async == null) {
            async = true;
        }

        url = prepareUrlForAjax(url);

        // create an inner function to log state changes
        // Define and set the callback function before open AJAX connection to fix the error in an early version of
        // firefox about the undefined error of the function doCallback.
        self.AJAX.onreadystatechange = function () {
            if (self.AJAX.readyState == 4) {
                try {
                    if (self.AJAX.status == 200) {
                        updatePageToken(this.responseXML);
                    }
                }catch(e){

                }
            }
            if (self.process != undefined && self.process != "") {
                self.process(self.AJAX);
            }
            if (self.AJAX.readyState == 4) {
                // Release xml request object
                self.AJAX = null;
            }
        };

        self.AJAX.open(method, url, async);

        if (method == "POST") {
            try {
                self.AJAX.setRequestHeader("Connection", "close");
            } catch (e) {
                // Chrome doesn't support setting Connection property.
            }

            self.AJAX.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            self.AJAX.setRequestHeader("Method", "POST " + url + "HTTP/1.1");
        }

        // if dosend is true or undefined, send the request
        // only fails is dosend is false
        // you'd do this to set special request headers
        if ( dosend || typeof dosend == 'undefined' ) {
            if ( !data ) data="";
            baseOnBeforeSendAjaxRequest(self);
            self.AJAX.send(data);
        }


    } catch (ex){
//        alert("Error creating AJAX Request "+ex.message);
    }
}

function prepareUrlForAjax(url) {

    // For Chinese character parameter values support
    url = urlEncode(url);

    // Add logic to add __isAjaxRequest request parameter
    if(url.indexOf('?')>-1)
        url+='&__isAjaxRequest=true';
    else
        url+='?__isAjaxRequest=true';

    // add parameter to avoid browser cache
    url += '&_avoidCacheDate=' + new Date();

    // add page view state id information, so that AJAX call will utilize the page state view cache correctly.
    if (isDefined(PAGE_VIEW_STATE_CACHE_KEY)) {
        if (getObject(PAGE_VIEW_STATE_CACHE_KEY)) {
            url += "&" + PAGE_VIEW_STATE_CACHE_KEY;
            url += "=" + getObjectValue(PAGE_VIEW_STATE_CACHE_KEY);
        }
    }

    // add page token.
    if (getObject(dti.csrf.getCSRFTokenLabel())) {
        url = dti.csrf.setupCSRFTokenForUrl(url);
    }

    return url;
}

/*
 * Processes the return information from an Ajax request.
 * The default implementation looks for XML return data for LOVS.
 */
function processAJAXReturn(ajax) {
 	if(ajax.readyState==4) {
 		if(ajax.status==200) {
 			var xml = ajax.responseXML;
 			if(xml.documentElement) {
 				doc = xml.documentElement;
 			    var lovs = doc.getElementsByTagName("lov");
    			for(var i=0;i<lovs.length;i++) {
        			var lov = lovs.item(i);
        			var field = lov.attributes.item(0).nodeValue;
        			setDropDown(document.all(field),lov);
        		}
		    }
		    else 
		    	alert("Contact Customer Service - XML retrieval problem:\n" + ajax.statusText);
 		}
 	}
}

function baseOnBeforeSendAjaxRequest(ajaxRequest) {
    // reset session timeout object
    resetSessionTimeoutObject();    

    var returnValue;
    // Execute commonOnBeforeSendAjaxRequest if it exists
    var functionExists = eval("window.commonOnBeforeSendAjaxRequest");
    if (functionExists) {
        returnValue = commonOnBeforeSendAjaxRequest(ajaxRequest);
    }

    // Execute handleOnBeforeSendAjaxRequest if it exists
    if (returnValue) {
        functionExists = eval("window.handleOnBeforeSendAjaxRequest");
        if (functionExists) {
            handleOnBeforeSendAjaxRequest(ajaxRequest);
        }
    }
}

function updatePageToken(responseXML){
    try{
        if (isDefined(responseXML) && responseXML != null) {
            var token = parseXMLForCSRFToken(responseXML);
            dti.csrf.updatePageToken(token);
        }
    } catch(e) {
    }
}

function parseXMLForCSRFToken(xmlDoc) {
    var token = "";
    var root = xmlDoc.documentElement;
    if (isDefined(token) && token != null) {
        var csrfToken = root.getElementsByTagName("CSRF_TOKEN");
        if(isDefined(csrfToken)  && csrfToken != null) {
            var token = csrfToken.item(0).textContent;
        }
    }
    return token;
}

function updatePageTokenForJson(data){
    try{
        if (data) {
        var token = data[dti.csrf.getCSRFTokenLabel()]
            if (isDefined(token) && token != null) {
                dti.csrf.updatePageToken(token);
            }
        }
    } catch(e) {
    }
}
