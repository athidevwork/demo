 // The root URL for the RESTful services
var rootURL = "http://54.215.186.133:20001/fault";

function loginUser (request) {
	var restUrl = rootURL + '/creuser/validate';

	console.log ('Url : ' + restUrl + ", " + "Request : " + request);
	$.ajax({
	    url: restUrl,
	    data: request,
	    type: 'POST',
	    method: request.method,
	    dataType: request.dataType ||"json",
	    crossDomain: true,
	    contentType: request.contentType || "application/json; charset=utf-8",
	    enctype: 'multipart/form-data',
	    success: function(response) {
	    	console.log(response);
	    	if (response.userType == 'super') {
	    		$('#loggedUser').append("Welcome " + response.firstName + " " + response.lastName);
	    		window.location.href = "super/index.html";
	    		//window.open("super/index.html");
	    	}
	    	else {
	    		$('#loggedUser').append("Welcome " + response.firstName + " " + response.lastName);
	    		window.location.href = "tenant/index.html";
	    		//window.open("tenant/index.html");
	    	}
	    },
	    error: function(xhr) {
	    	console.log ("Failure occurred during processing user validation request : " + xhr);
	    }
	});		
}

function sendFaultRequest(request, mode) {
	var restUrl;
	
	if (mode == 'create')
		restUrl = rootURL + '/create';
	else if (mode == 'process')
		restUrl = rootURL + '/process';	
	else
		restUrl = rootURL + '/save';
	
	console.log ('Url : ' + restUrl);
	
	$.ajax({
	    url: restUrl,
	    data: request,
	    type: 'POST',
	    method: request.method,
	    dataType: request.dataType ||"json",
	    crossDomain: true,
	    contentType: request.contentType || "application/json; charset=utf-8",
        enctype: 'multipart/form-data',
	    success: function(response) {
	    	console.log(response);
	    	//document.getElementById('jqxTextAreaResponse').innerHTML = response;
	    	//$('#jqxTextAreaResponse').jqxTextArea('val', JSON.stringify(response));
        	$('#successMsg').empty().append('Submit action completed successfully.');
	    	window.setTimeout(function() {
	    	    $("#successMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000);
	    },
	    beforeSend: function(xhr) {
	        xhr.setRequestHeader('Access-Control-Allow-Origin', '*');
	    },
	    error: function(xhr) {
        	$('#errorMsg').empty().append('Submit action completed successfully.');
	    	window.setTimeout(function() {
	    	    $("#errorMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000);
	    }
	});	
}

function getFaults() {
	var restUrl = rootURL + '/faults';
	console.log('Url : ' + restUrl);
	
	$.ajax({
	    url: restUrl,
	    dataType: "json",
	    //crossDomain: true,
	    //dataType: 'jsonp',
	    success: function(response) {
	    	//console.log(response);
	    	//document.getElementById('policydata').innerHTML = response;	    	
	    	$('#jqxTextAreaGetFaults').jqxTextArea('val', JSON.stringify(response));
	    },
	    error: function(xhr) {
	    	console.log ("Failure occurred during table generation : " + xhr);
	    }
	});	
}

function setupGetUnitFaultsGrid(building, location, unit) {
	console.log('building : ' + building + ', location : ' + location + ', unit : ' + unit);
	//http://54.215.186.133:20001/fault/assets/buildinglocationandunit?building=Building 4&location=4th floor&unit=444
	var restUrl = rootURL + '/assets/buildinglocationandunit?building='+building+'&location='+location+'&unit='+unit;
	console.log('Url : ' + restUrl);
	
    //get data from web service and add to grid
	var respdata = new Array();
	
  	//Initializing the source property
    source = {
        datatype: 'json',
    	root: "faults",
        datafields: [
            { name: 'id', type: 'number' },
            { name: 'startDate', type: 'string' },
            { name: 'endDate', type: 'string' },
            { name: 'category', type: 'string' },
            { name: 'subCategory', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'fSignature', type: 'string' },
            { name: 'aibcStatus', type: 'string' },
            { name: 'aibcTrans', type: 'string' }
        ]
    };
    //Getting the source data with ajax GET request
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: restUrl,
        cache: false,
        contentType: 'application/json; charset=utf-8',
        success: function (data) {
        	source.localdata = data;
        	/*$('#successMsg').empty().append('Get Unit completed successfully.');
	    	window.setTimeout(function() {
	    	    $("#successMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000);*/
        },
        error: function (err) {
        	//alert('Error');
        	$('#errorMsg').empty().append('Get Unit failed.');
	    	window.setTimeout(function() {
	    	    $("#errorMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000);        	
        }
    });
    
    //Preparing the data for use
    var dataAdapter = new $.jqx.dataAdapter(source);

    // initialize jqxGrid
    $('#jqxUnitFaultsgrid').jqxGrid(
    {
        width: 1250,
        source: dataAdapter,
        editable: true,
        enabletooltips: true,
        sortable: true,
        selectionmode: 'multiplecellsadvanced',
        columns: [
            { text: 'ID', columntype: 'textbox', datafield: 'id', width: 30 },
            { text: 'Created Date', datafield: 'startDate', columntype: 'textbox', width: 160 },
            { text: 'Processed Date', columntype: 'dropdownlist', datafield: 'endDate', width: 160 },
            { text: 'Fault Category', columntype: 'dropdownlist', datafield: 'category', width: 150 },
            { text: 'Fault Sub Category', columntype: 'dropdownlist', datafield: 'subCategory', width: 150 },
            { text: 'Fault Description', columntype: 'dropdownlist', datafield: 'description', width: 175 },
            { text: 'Fault Signature', columntype: 'dropdownlist', datafield: 'fSignature', width: 200 },
            { text: 'Fault Status', columntype: 'dropdownlist', datafield: 'aibcStatus', width: 100 },
            { text: 'Blockchain Transaction', columntype: 'dropdownlist', datafield: 'aibcTrans', width: 100 }
        ]
    });       	
}

function setupGetAllFaultsGrid() {
	var restUrl = rootURL + '/faults';
	console.log('Url : ' + restUrl);
	
    //get data from web service and add to grid
  	//Initializing the source property
    source = {
        datatype: 'json',
        datafields: [
            { name: 'id', type: 'number' },
            { name: 'startDate', type: 'string' },
            { name: 'endDate', type: 'string' },
            { name: 'category', type: 'string' },
            { name: 'subCategory', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'fSignature', type: 'string' },
            { name: 'aibcStatus', type: 'string' },
            { name: 'aibcTrans', type: 'string' }
            ]
    };
    //Getting the source data with ajax GET request
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: restUrl,
        cache: false,
        contentType: 'application/json; charset=utf-8',
        success: function (data) {
        	source.localdata = data;
        },
        error: function (err) {
        	$('#errorMsg').empty().append('Get Unit failed.');
	    	window.setTimeout(function() {
	    	    $("#errorMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000); 
        }
    });
    
    //Preparing the data for use
    var dataAdapter = new $.jqx.dataAdapter(source);

    // initialize jqxGrid
    $('#jqxFaultsgrid').jqxGrid(
    {
        width: 1250,
        height: 950,
        source: dataAdapter,
        editable: true,
        enabletooltips: true,
        sortable: true,        
        selectionmode: 'multiplecellsadvanced',
        columns: [
            { text: 'ID', columntype: 'textbox', datafield: 'id', width: 30 },
            { text: 'Created Date', datafield: 'startDate', columntype: 'textbox', width: 145 },
            { text: 'Processed Date', columntype: 'dropdownlist', datafield: 'endDate', width: 145 },
            { text: 'Fault Category', columntype: 'dropdownlist', datafield: 'category', width: 145 },
            { text: 'Fault Sub Category', columntype: 'dropdownlist', datafield: 'subCategory', width: 150 },
            { text: 'Fault Description', columntype: 'dropdownlist', datafield: 'description', width: 175 },
            { text: 'Fault Signature', columntype: 'dropdownlist', datafield: 'fSignature', width: 200 },
            { text: 'Fault Status', columntype: 'dropdownlist', datafield: 'aibcStatus', width: 100 },
            { text: 'Blockchain Transaction', columntype: 'dropdownlist', datafield: 'aibcTrans', width: 200 }
            /*{ text: 'Available', datafield: 'available', columntype: 'checkbox', width: 67 },
            { text: 'Ship Date', datafield: 'date', columntype: 'datetimeinput', width: 110, align: 'right', cellsalign: 'right', cellsformat: 'd',
	            validation: function (cell, value) {
		            if (value == '')
		            return true;
		
		            var year = value.getFullYear();
		            if (year >= 2015) {
		            	return { result: false, message: 'Ship Date should be before 1/1/2015' };
		            }
		            return true;
	            }
            },
            { text: 'Quantity', datafield: 'quantity', width: 70, align: 'right', cellsalign: 'right', columntype: 'numberinput',
	            validation: function (cell, value) {
	            if (value < 0 || value > 150) {
	            return { result: false, message: 'Quantity should be in the 0-150 interval' };
	            }
	            return true;
	            },
	            createeditor: function (row, cellvalue, editor) {
	            editor.jqxNumberInput({ decimalDigits: 0, digits: 3 });
	            }
            },
            { text: 'Price', datafield: 'price', align: 'right', cellsalign: 'right', cellsformat: 'c2', columntype: 'numberinput',
	            validation: function (cell, value) {
	            if (value < 0 || value > 15) {
	            return { result: false, message: 'Price should be in the 0-15 interval' };
	            }
	            return true;
	            },
	            createeditor: function (row, cellvalue, editor) {
	            editor.jqxNumberInput({ digits: 3 });
	            }		
            }*/
        ]
    });

    // events
    /*$('#jqxgrid').on('cellbeginedit', function (event) {
        var args = event.args;
        $('#cellbegineditevent').text('Event Type: cellbeginedit, Column: ' + args.datafield + ', Row: ' + (1 + args.rowindex) + ', Value: ' + args.value);
    });

    $('#jqxgrid').on('cellendedit', function (event) {
        var args = event.args;
        $('#cellendeditevent').text('Event Type: cellendedit, Column: ' + args.datafield + ', Row: ' + (1 + args.rowindex) + ', Value: ' + args.value);
    });*/        	
}

function setupPendingFaultsGrid() {
	var restUrl = rootURL + '/pending/faults';
	console.log('Url : ' + restUrl);
	
    //get data from web service and add to grid
  	//Initializing the source property
    source = {
        datatype: 'json',
        datafields: [
            /*{ name: 'available', type: 'bool' },*/        	
            { name: 'id', type: 'number' },
            { name: 'startDate', type: 'string' },
            { name: 'endDate', type: 'string' },
            { name: 'category', type: 'string' },
            { name: 'subCategory', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'fSignature', type: 'string' },
            { name: 'aibcStatus', type: 'string' },
            /*{ name: 'aibcTrans', type: 'string' },*/
            ]
    };
    //Getting the source data with ajax GET request
    $.ajax({
        type: 'GET',
        dataType: 'json',
        async: false,
        url: restUrl,
        cache: false,
        contentType: 'application/json; charset=utf-8',
        success: function (data) {
        	source.localdata = data;
        },
        error: function (err) {
        	$('#errorMsg').empty().append('Get Unit failed.');
	    	window.setTimeout(function() {
	    	    $("#errorMsg").show().fadeTo(1000, 0).slideUp(1000, function(){
	    	        $(this).remove(); 
	    	    });
	    	}, 2000); 
        }
    });
    
    //Preparing the data for use
    var dataAdapter = new $.jqx.dataAdapter(source);

    // initialize jqxGrid
    $('#jqxPendingFaultsgrid').jqxGrid(
    {
        width: 1250,
        source: dataAdapter,
        editable: true,
        enabletooltips: true,
        sortable: true,
        ready: function () {
            $("#jqxPendingFaultsgrid").jqxGrid('selectrow', 0);
        },
        theme: 'energyblue',        
        //filterable: true,
        //autoshowfiltericon: true,
        //selectionmode: 'singlecell',
        editmode: 'click’',
        //selectionmode: 'multiplecellsadvanced',
        selectionmode: 'multiplerowsadvanced',
        columns: [
            /*{ text: '', datafield: 'available', columntype: 'checkbox', width: 30 },*/        	
            { text: 'ID', columntype: 'textbox', datafield: 'id', width: 30 },
            { text: 'Created Date', datafield: 'startDate', columntype: 'textbox', width: 140 },
            { text: 'Processed Date', columntype: 'textbox', datafield: 'endDate', width: 140 },
            { text: 'Fault Category', columntype: 'textbox', datafield: 'category', width: 140 },
            { text: 'Fault Sub Category', columntype: 'textbox', datafield: 'subCategory', width: 140 },
            { text: 'Fault Description', columntype: 'textbox', datafield: 'description', width: 150 },
            { text: 'Fault Signature', columntype: 'textbox', datafield: 'fSignature', width: 150 },
            { text: 'Fault Status', columntype: 'textbox', datafield: 'aibcStatus', width: 100 },
            /*{ text: 'Blockchain Transaction', columntype: 'dropdownlist', datafield: 'aibcTrans', width: 150 },*/
        ]
    });
    
    // events
    /*$("#jqxPendingFaultsgrid").on('cellbeginedit', function (event) {
        var args = event.args;
        $("#cellbegineditevent").text("Event Type: cellbeginedit, Column: " + args.datafield + ", Row: " + (1 + args.rowindex) + ", Value: " + args.value);
    });

    $("#jqxPendingFaultsgrid").on('cellendedit', function (event) {
        var args = event.args;
        $("#cellendeditevent").text("Event Type: cellendedit, Column: " + args.datafield + ", Row: " + (1 + args.rowindex) + ", Value: " + args.value);
    });*/
    
    // select or unselect rows when the checkbox is clicked.
    /*$("#jqxPendingFaultsgrid").bind('cellendedit', function (event) {
        if (event.args.value) {
            $("#jqxPendingFaultsgrid").jqxGrid('selectrow', event.args.rowindex);
        }
        else {
            $("#jqxPendingFaultsgrid").jqxGrid('unselectrow', event.args.rowindex);
        }
    });*/
    

    /*$("#enablehover").on('change', function (event) {
        $("#grid").jqxGrid('enablehover', event.args.checked);
    });*/

    // display selected row index.
    /*$("#jqxPendingFaultsgrid").on('rowselect', function (event) {
        $("#selectrowindex").text(event.args.rowindex);
    });

    // display unselected row index.
    $("#jqxPendingFaultsgrid").on('rowunselect', function (event) {
        $("#unselectrowindex").text(event.args.rowindex);
    });*/

    // get all selected records.
    $("#ProcessPendingFaults").click(function () {
    	var faultsRequest = {
    		    faults: []
    		};
        var rows = $("#jqxPendingFaultsgrid").jqxGrid('selectedrowindexes');
        var selectedRecords = new Array();
        for (var m = 0; m < rows.length; m++) {
            var row = $("#jqxPendingFaultsgrid").jqxGrid('getrowdata', rows[m]);
            selectedRecords[selectedRecords.length] = row;
            //alert(row.id);
            faultsRequest.faults.push({ 
            	"id" : row.id,        				
				"category" : row.category,
				"subCategory" : row.subCategory,
				"description" : row.description,
				"fSignature" : row.fSignature, 
				"aibcStatus" : row.aibcStatus
            });
        }
        /*console.log('selected rows :' + selectedRecords);
        var rowindex = $('#jqxPendingFaultsgrid').jqxGrid('getselectedrowindex');
        var data = $('#jqxPendingFaultsgrid').jqxGrid('getrowdata', rowindex);*/
        /*alert(data.id + " " + data.startDate + " " + data.endDate + " " + data.category + " " 
        		+ data.subCategory + " " + data.description + " " + data.aibcStatus);*/  
        /*var faults = {
        		"faults" : [
        			{
        				"id" : data.id,        				
        				"category" : data.category,
        				"subCategory" : data.subCategory,
        				"description" : data.description,
        				"fSignature" : data.fSignature, 
        				"aibcStatus" : data.aibcStatus        				
        			}
        		]
        	};*/
        faultsJson = JSON.stringify(faultsRequest);
        //$('#jqxTextAreaRequest').jqxTextArea('val', faultsJson);
        console.log(faultsJson);
        //send a rest client request
    	sendFaultRequest(faultsJson, 'process');        
    });    
}


function setupExampleGrid() {
	// prepare the data
	var data = new Array();
	var firstNames =
	[
	    "Andrew", "Nancy", "Shelley", "Regina", "Yoshi", "Antoni", "Mayumi", "Ian", "Peter", "Lars", "Petra", "Martin", "Sven", "Elio", "Beate", "Cheryl", "Michael", "Guylene"
	];
	var lastNames =
	[
	    "Fuller", "Davolio", "Burke", "Murphy", "Nagase", "Saavedra", "Ohno", "Devling", "Wilson", "Peterson", "Winkler", "Bein", "Petersen", "Rossi", "Vileid", "Saylor", "Bjorn", "Nodier"
	];
	var productNames =
	[
	    "Black Tea", "Green Tea", "Caffe Espresso", "Doubleshot Espresso", "Caffe Latte", "White Chocolate Mocha", "Cramel Latte", "Caffe Americano", "Cappuccino", "Espresso Truffle", "Espresso con Panna", "Peppermint Mocha Twist"
	];
	var priceValues =
	[
	    "2.25", "1.5", "3.0", "3.3", "4.5", "3.6", "3.8", "2.5", "5.0", "1.75", "3.25", "4.0"
	];
	for (var i = 0; i < 1000; i++) {
	    var row = {};
	    var productindex = Math.floor(Math.random() * productNames.length);
	    var price = parseFloat(priceValues[productindex]);
	    var quantity = 1 + Math.round(Math.random() * 10);
	    row["firstname"] = firstNames[Math.floor(Math.random() * firstNames.length)];
	    row["lastname"] = lastNames[Math.floor(Math.random() * lastNames.length)];
	    row["productname"] = productNames[productindex];
	    row["price"] = price;
	    row["quantity"] = quantity;
	    row["total"] = price * quantity;
		row["discontinued"] = false;
	    data[i] = row;
	}
	var source =
	{
	    localdata: data,
	    datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(source, {
		downloadComplete: function (data, status, xhr) { },
	    loadComplete: function (data) { },
	    loadError: function (xhr, status, error) { }    
	});
	$("#jqxgrid").jqxGrid(
	{
		//width: getWidth('jqxgrid'),
		width: "100%",
		source: dataAdapter,                
		pageable: true,
		autoheight: true,
		sortable: true,
		altrows: true,
		enabletooltips: true,
		editable: true,
		selectionmode: 'multiplecellsadvanced',
	    columns: [
	        { text: 'First Name', datafield: 'firstname', width: 100 },
	        { text: 'Last Name', datafield: 'lastname', width: 100 },
	        { text: 'Product', datafield: 'productname', width: 180 },
	        { text: 'Quantity', datafield: 'quantity', width: 80, cellsalign: 'right' },
	        { text: 'Unit Price', datafield: 'price', width: 90, cellsalign: 'right', cellsformat: 'c2' },
	        { text: 'Total', datafield: 'total', width: 100, cellsalign: 'right', cellsformat: 'c2' },
			{ text: 'Discontinued', columntype: 'checkbox', datafield: 'Discontinued' }
	    ]
	});              
}

/*function getIssuePolicy(env, policy) {
	var restUrl = rootURL + '/policy?env=' + env + '&policy=' + policy;
	console.log(restUrl);
	
	$.ajax({
	    url: restUrl,
	    dataType: "json",
	    success: function(response) {
	    	console.log(response);
	    	document.getElementById('policydata').innerHTML = response;
	    },
	    error: function(xhr) {
	    	console.log ("Failure occurred during table generation : " + data);
	    }
	});	
}

function getLink(env, product) {
	var restUrl = rootURL + '/link';
	
	$.ajax({
	    url: restUrl,
	    data: {"env": env,"product":product},
	    success: function(response) {
	    	console.log(response);
	    	document.getElementById(product).onclick = function() {
	    		document.getElementById(product).href=response; 
	    		return false;
	    	};
	    },
	    error: function(xhr) {
	    	console.log ("Failure occurred during processing link : " + data);
	    }
	});	
}

function getConfig(env, outputFormat) {
	var restUrl = '';
		
	if (outputFormat.trim() === "HTML")
		restUrl = rootURL + '/htmlconfig/' + env;
	else
		restUrl	= rootURL + '/config/' + env;
	
	console.log ("var env = " + env);
	console.log ("var format = " + outputFormat);	
	console.log ("var rest url : " + restUrl)
	$.ajax({
        type: 'GET',
        url: restUrl,
        dataType: outputFormat,
        success: function(data){
            console.log("rest response: " + data);
            //console.log("format =" + outputFormat);
            //$('#restresponse').show();
            $('#restresponse').empty();
            
            var response=jQuery.parseJSON(data);
            
            if (typeof response =='object') {
            	console.log ("is json");
            	$('#restresponse').append(
            		    $('<pre>').text(
            		        JSON.stringify(data, null, '  ')
            		    )
            	 );
            } else {
                var xml, xmlfound=false;
                var xmlDoc = $.parseXML( data );
                $data = $( xmlDoc );
                $xml.find("oasisConfigType").each(function(index,elem){
                    console.log ("is xml");
                	$('#restresponse').append($xml);
                });
            	
            	$('#restresponse').append(data);
            }
	        $('#restresponse').append(data);
            //$('#restresponse').append(JSON.stringify(data));
            //renderDetails(data);
        },
        fail: function(data){ console.log ("Failure occurred : " + data);}
    });
}*/

