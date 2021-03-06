/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
var loadDD = new $.loadDD();
var tableContainer = $("#complaintSearchResults");
var complaintList, department, ward = [];
var RequestInfo = new $.RequestInfo(localStorage.getItem("auth"));
var requestInfo = {};
requestInfo['RequestInfo'] = RequestInfo.requestInfo;
 $(document).ready(function() {

	$.when(
		
		/*Show loader*/
		showLoader(),

		//Location
		loadWard(),

		//load receiving mode
		loadReceivingMode(),

		/*load complaint types*/
		complaintType(),

		loadDeparment(),

		loadBoundarys(),

		loadStatus()

	).then(function() {
		//Hide Loader
		hideLoader();
	});

    $('#toggle-searchcomp').click(function () {
        if ($(this).data('translate') == "core.lbl.more") {
            $(this).data('translate', 'core.lbl.less');
            $(this).html(translate('core.lbl.less'));
            $('.show-searchcomp-more').show();
		} else {
	        $(this).data('translate', 'core.lbl.more');
	        $(this).html(translate('core.lbl.more'));
	        $('.show-searchcomp-more').hide();
		}
		
	});

	$("#when_date").change(function () {
        populatedate($('#when_date').val());
	});

    $('#searchComplaints').click(function () {

    	var formData = $("form :input")
	    .filter(function(index, element) {
	        return $(element).val() != "";
	    }).serialize();// does the job!

	    if(formData.length == 0){
	    	bootbox.alert(translate('core.msg.criteria.required'));
	    	return;
	    }

	    var searchURL = '/pgr/seva/_search?tenantId=ap.public&'+formData;

    	tableContainer = $("#complaintSearchResults").DataTable( {
    		"ajax": {
	            "url": searchURL,
	            type: 'POST',
	            contentType: "application/json",
	            processData : true,
	            data: function ( requestInfo ) {
			      return JSON.stringify( requestInfo );
			    },
	            "dataSrc": "service_requests",
	            beforeSend : function(){
					showLoader();
				},
				error: function(){
					bootbox.alert('Error loading data!')
				},
				complete : function(){
					$('#complaintSearchResults').removeClass('hide');
					hideLoader();
				}
	        },
			destroy:true,
			"deferRender": true,
			autoWidth: false,
			"aaSorting": [],
			dom: "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
			buttons: [
			    'excel','print',
			    {
				    extend: 'pdf',
				    filename: 'Grievance List',
				    pageSize : 'LEGAL',
			        orientation : 'landscape',
				    exportOptions: {
				        columns: ':visible',
				    }
				}
			],
			columns: [
				{data: 'service_request_id'},
				{data: 'service_name'},
				{data: 'first_name'},
				{"render": function ( data, type, full, meta ) {
					var wardname = getBoundariesbyId(full.values.locationId);
					var localityname = getBoundariesbyId(full.values.childLocationId);
					return (wardname+' - '+localityname);
			    } },
				{data: 'values.complaintStatus'},
				{data: 'values.departmentId', "render": function ( data, type, full, meta ) {
					var depart = getDepartmentbyId(full.values.departmentId);
					return depart;
			    } },
				{data : 'requested_datetime'}
			]
	    });

	});

});

$("#complaintSearchResults").on('click','tbody tr',function(event) {
	var srn = tableContainer.row( this ).data().service_request_id;
	openPopUp('view-complaint.html?srn='+srn, srn);
});

function loadDeparment(){
	$.ajax({
		url: "/eis/departments",
		type : 'GET'
	}).done(function(data) {
		department = data.Department;
	});
}

function getDepartmentbyId(departmentId){
	var depObj = department.filter(function( obj ) {
	  return obj.id == departmentId;
	});
	var value = (depObj[0]) ? (Object.values(depObj[0])[1]) : '-';
	return value;
}

function loadBoundarys(){
	$.ajax({
		url : '/v1/location/boundarys?boundary.tenantId=ap.kurnool',
		success : function(data){
			ward = data.Boundary;
		}
	})
}

function getBoundariesbyId(wardId){
	var wardObj = ward.filter(function( obj ) {
	  return obj.id == wardId;
	});
	var value = (wardObj[0]) ? (Object.values(wardObj[0])[1]) : '-';
	return value;
}
 
function populatedate(id) {
    var d = new Date();
    var quarter = getquarter(d);
    var start, end;
    switch (id) {
		
		case "lastsevendays":
		$("#end_date").datepicker("setDate", d);
		start = new Date(d.setDate((d.getDate() - 7)));
		var start = new Date(start.getFullYear(), start.getMonth(), start.getDate());
		$("#start_date").datepicker("setDate", start);
		break;
		
		case "lastthirtydays":
		$("#end_date").datepicker("setDate", d);
		start = new Date(d.setDate((d.getDate() - 30)));
		var start = new Date(start.getFullYear(), start.getMonth(), start.getDate());
		$("#start_date").datepicker("setDate", start);
		break;
		
		case "lastninetydays":
		$("#end_date").datepicker("setDate", d);
		start = new Date(d.setDate((d.getDate() - 90)));
		var start = new Date(start.getFullYear(), start.getMonth(), start.getDate());
		$("#start_date").datepicker("setDate", start);
		break;
		
		case "today":
		$("#end_date").datepicker("setDate", d);
		$("#start_date").datepicker("setDate", d);
		break;
		
		case "all":
		$("#end_date").val("");
		$("#start_date").val("");
		break;
		
	}
}

function getquarter(d) {
	if (d.getMonth() >= 0 && d.getMonth() <= 2) {
        quarter = 4;
		} else if (d.getMonth() >= 3 && d.getMonth() <= 5) {
        quarter = 1;
		} else if (d.getMonth() >= 6 && d.getMonth() <= 8) {
		quarter = 2;
		} else if (d.getMonth() >= 9 && d.getMonth() <= 11) {
		quarter = 3;
	}
	
    return quarter;
}

function loadReceivingMode(){
	$.ajax({
		url : "/pgr/receivingmode?tenantId=ap.public",
		success : function(response){
			loadDD.load({
				element:$('#receivingMode'),
				placeholder : 'Select Receiving Mode',
				data:response,
				keyValue:'id',
				keyDisplayName:'name'
			});
		},
		error: function(){
			bootbox.alert('Receiving mode failed!')
		}
	});
}

function complaintType(){
	$.ajax({
		url: "/pgr/services?type=all&tenantId=ap.public",
		type : 'POST',
		data : JSON.stringify(requestInfo),
		dataType: 'json',
		processData : false,
		contentType: "application/json"
	}).done(function(data) {
		loadDD.load({
			element:$('#complaintType'),
			data:data.ComplaintType,
			placeholder : 'Select Complaint Type',
			keyValue:'serviceCode',
			keyDisplayName:'serviceName'
		});
	});
}					

function loadStatus(){
	$.ajax({
		url: "/workflow/statuses/_search",
		type : 'POST',
		dataType: 'json',
		processData : false,
		contentType: "application/json",
		data : JSON.stringify(requestInfo)
	}).done(function(data) {
		loadDD.load({
			element:$('#status'),
			placeholder : 'Select Status',
			data:data,
			keyValue:'name',
			keyDisplayName:'name'
		});
	});
}

function loadWard(){
	$.ajax({
		url: "/v1/location/boundarys/boundariesByBndryTypeNameAndHierarchyTypeName?boundaryTypeName=Ward&hierarchyTypeName=Administration",
		type : 'POST',
		dataType: 'json',
		processData : false,
		contentType: "application/json",
		data : JSON.stringify(requestInfo)
	}).done(function(data) {
		loadDD.load({
			element:$('#ct-location'),
			placeholder : 'Select Location', // default - Select(optional)
			data:data.Boundary,
			keyValue:'id',
			keyDisplayName:'name'
		});
	});
}