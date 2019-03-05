//Execute AJAX call to get workflow diary status

getDiaryStatus();



/*
*  function to initiate a call to the server
*  it reset's the status if it is on the workflow diary screen
*
*/

function getDiaryStatus(){
  var url = 'workflowdiaryStatus.do';
  var data = '';

  var menuItem = getSingleObject('CM_WFDIARY_MI');
  if(menuItem){
     if(menuItem.childNodes[0] && menuItem.childNodes[0].className == 'selectedMenu'){
       data = 'reset=true';
     }
     new AJAXRequest('post', url, data, setDiaryNotify);
  }
}


/*
*  function to change the menu style based on the return
*
*/

function setDiaryNotify(ajax) {

	var styleName = 'workflowAttention';

 	if(ajax.readyState==4) {
 		if(ajax.status==200) {
			if(getSingleObject('CM_WFDIARY_MI').childNodes[0].className != 'selectedMenu'){
 				var txtResponse = ajax.responseText;
				if(txtResponse.lastIndexOf("true") != -1){
					getSingleObject('CM_WFDIARY_MI').className=styleName;
				}
		    }
 		}
	}
}