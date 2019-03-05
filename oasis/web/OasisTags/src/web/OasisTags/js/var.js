//-----------------------------------------------------------------------------
// NOTE: This file is a common one for all applications.
//       It should be maintained in OasisTags.
//-----------------------------------------------------------------------------
/*
  Revision Date    Revised By  Description
  -----------------------------------------------------------------------------
  11/05/2007       FWCH        Reset the original selected row in the grid.
  07/21/2016       dpang       Removed references to dwr.
  -----------------------------------------------------------------------------
*/
function VARuleObj () {
  /* Object properties*/
  this.formID;
  this.formIDonPage;
  this.values;
  this.original;
  this.urlVAR;
  this.isActive;
  this.isGrid;
  this.gridXmlID;
  this.returnValue;
  this.needOriginal;
  this.processFieldName="";
  this.processFieldValue="";
}
/*Object Methods */
VARuleObj.prototype.collectFormVlaues = function (form){
    var txt = formValueToJSONString(form);
		var v = eval("("+txt+")");
    this.values=v;

}
VARuleObj.prototype.resetSelectedRowInGrid=function(grid) {
    var nodes = grid.documentElement.selectNodes("//ROW[UPDATE_IND != 'N' and UPDATE_IND != 'D']");
    if (nodes && nodes.length > 0) {
        //If there are more than one updated rows,
        //set the first one as selected
        node = nodes.item(0);
        var firstId = node.getAttribute("id");
        getRow(grid, firstId);
     }
}
VARuleObj.prototype.collectGridValues=function(grid){
	var txt = xmlToJSONString(grid);	
	var v = eval("("+txt+")");
    this.values=v;
}
VARuleObj.prototype.requestVAR=function(){
    if(this.processFieldName.length>0){
      try {
        if(document.forms(this.formIDonPage).elements(this.processFieldName).value!=this.processFieldValue){
          // skip VAR request unless specified process taking place
          return true;
        }
      }
      catch(err) { /* the process field may not be there */
          return true;
      }
    }
    if(this.isGrid){
      this.collectGridValues(this.gridXmlID);
    }else{
      this.collectFormVlaues(document.forms(this.formIDonPage));
    }
	this.sendVARequest();
    if (this.isGrid) {
        this.resetSelectedRowInGrid(this.gridXmlID);
    }
/*    var data="formid="+this.formID+"&values="+escape(values2xml(this.vaules))+"&orig="+escape(values2xml(this.original));
//alert(data);
  	new AJAXRequest("POST", this.urlVAR, data, this.handleVAResult, false);
*/
		return this.returnValue;
}
VARuleObj.prototype.handleVAResult = function (ajax){
 	if(ajax.readyState==4) {
 		if(ajax.status==200) {
 			var xml = ajax.responseXML;
//alert(xml.xml);
 			if(xml.documentElement) {
			 if(processErrors(xml)){
			   if(processWarning(xml)){
				   processMessage(xml);
					 this.returnValue=true;
					 varRule.returnValue=true;
					 return true;
				 }else {
					 this.returnValue=false;
					 varRule.returnValue=false;
				 	 return false;
				 }
			 }else {
					 this.returnValue=false;
					 varRule.returnValue=false;
				 		return false;
			 }
			}else {
		    	alert("Contact Customer Service - XML retrieval problem:\n" + ajax.statusText);
					 this.returnValue=false;
					 varRule.returnValue=false;
				 	return false;
			}
 		}
 	}
 }
 VARuleObj.prototype.sendVARequest=function(){
    var data="formid="+this.formID+"&values="+escape(values2xml(this.values))+"&orig="+escape(values2xml(this.original));
//alert(data);
  	new AJAXRequest("POST", this.urlVAR, data, this.handleVAResult, false);
		return this.returnValue;
}

/****************************************/
/****************************************/
 function processErrors(xml){
   			var doc = xml.documentElement;
				var errMsgList=doc.selectNodes("ERROR/msg");
				var msg="Error: \n";
				var count=errMsgList.length;
				if (count <1 ){
				  return true;
				}
				for( i=0; i<count; i++){
				  msg+=errMsgList[i].text+"\n";
		    }
				alert(msg);
				return false;
 }
 function processWarning(xml){
   			var doc = xml.documentElement;
				var msgList=doc.selectNodes("WARNING/msg");
				var msg="Warning: \n";
				var count=msgList.length;
				for( i=0; i<count; i++){
				  if(!confirm(msgList[i].text)){
					  return false;
				  }
		    }
				return true;
 }
 function processMessage(xml){
   			var doc = xml.documentElement;
				var msgList=doc.selectNodes("MESSAGE/msg");
				var msg="Information: \n";
				var count=msgList.length;
				if (count<1) return;
				for( i=0; i<count; i++){
				  msg+=msgList[i].text+"\n";
		    }
				alert(msg);
				return true;
 }
/*********************************************
Utility methods
**********************************************/
function formValueToJSONString(form){
  var formValueJSONString='{"data" :[{';
	var fieldCount=form.elements.length;
	var first=true;
	for(var i=0; i<fieldCount; i++){
	 if(form.elements[i].id || form.elements[i].name){
		if(!first){
				formValueJSONString+=",";
		}
		first = false;
         var fieldName = form.elements[i].id;
         if(fieldName==""){
             fieldName = form.elements[i].name;
         }
         formValueJSONString+='"'+fieldName+'" : "'+ form.elements[i].value+'"';
	 }
	}
  formValueJSONString+="}]}";
	return formValueJSONString;
}

function xmlToJSONString(xml){
  var xmlJSONString='{"data":[';
	var rows=xml.selectNodes("//ROW");
	var rowCount=rows.length;
	var firsRow=true;
	for(var i=0; i<rowCount; i++){
	   if(!firsRow) {
		   xmlJSONString+=","
		 }
		 firsRow = false;
		 xmlJSONString+="{";
	   var row=rows.item(i);
		 var cols=row.getElementsByTagName("*");
		 var fieldCount=cols.length;
		 var firstCol=true;
		 for(var j=0; j<fieldCount; j++){
		   if(!firstCol){
				xmlJSONString+=",";
		   }
		   firstCol = false;
	     xmlJSONString+='"'+cols.item(j).tagName+'" : "'+ cols.item(j).text+'"';
	   }
		 xmlJSONString+="}";
	}
  xmlJSONString+="]}";
	return xmlJSONString;
}

function values2xml(jo){
 var xml="";
 var rowCount=jo.data.length;
 xml+="<RS>";
 for(var i=0; i<rowCount; i++){
 	xml+="<ROW>";
	var r=jo.data[i];
	for( f in r){
		xml+="<"+f+">";
		xml+="<![CDATA["+r[f]+"]]>";
		xml+="</"+f+">";
	}
 	xml+="</ROW>";
 }
 xml+="</RS>";
 return xml;
}

/************************************************************************
Method to attache to event such as onsubmit
e.g. document.forms(varRule.formIDonPage).attachEvent("onsubmit", varChecking);
*************************************************************************/
function varChecking(){
 if(event!=undefined){
   if(event.returnValue==false){
	 return false;
   }
 }
 if(varRule.isActive){
   var ok=varRule.requestVAR();
   if(event!=undefined) {
      event.returnValue=ok;
   }
   return ok;
 }
 else {
//    alert("nothing to check")
	if(event!=undefined) {
      event.returnValue=true;
   }
	return true;
 }
}
function attachVARToForm(){
 document.forms(varRule.formIDonPage).attachEvent("onsubmit", varChecking);
}
