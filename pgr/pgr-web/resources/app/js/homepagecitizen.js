/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
var RequestInfo = new $.RequestInfo(localStorage.getItem("auth"));
var requestInfo = {};
requestInfo['RequestInfo'] = RequestInfo.requestInfo;
$(document).ready(function()
{	
	preventBack();

	var obj = {};
	
	var userArray = [localStorage.getItem('id')]

	obj['RequestInfo'] = RequestInfo.requestInfo;
	obj['id'] = userArray;

	$.ajax({
		url : "/user/_search",
		type : 'POST',
		processData : false,
		contentType: "application/json",
		data : JSON.stringify(obj),
		success : function(response){
			$('.profile-text').html(response.user[0].userName);
		},
		error : function(){
			bootbox.alert('User api failed');
		}
	});
	
	$('.menu-item').click(function(e)
	{
		$('.citizen-screens').hide();
		$('.hr-menu li').removeClass('active');
		$(this).parent().addClass('active');
		$($(this).data('show-screen')).show();
	});
	
	$("#sortby_drop li a").click(function(){
		$("#sortby_drop > .btn > span > b").html($(this).html());
	});
	
	$('.tabs-style-topline nav li').click(function(){
		if($(this).attr('data-section') == "newrequest")
		{
			if($(this).attr('data-newreq-section') == '#section-newrequest-1')
			{
				$('.tabs-style-topline nav li').removeClass('tab-current-newreq');
				$('.content-wrap section').removeClass('content-current-newreq');
				$(this).addClass('tab-current-newreq');
				$($(this).attr('data-newreq-section')).addClass('content-current-newreq');
			}
			
		}else if($(this).attr('data-section') == "myaccount")
		{
			$('.tabs-style-topline nav li').removeClass('tab-current-myacc');
			$('.content-wrap section').removeClass('content-current-myacc');
			$(this).addClass('tab-current-myacc');
			$($(this).attr('data-myaccount-section')).addClass('content-current-myacc');
		}
	});
	
	$('.check-password').blur(function(){
		if(($('#new-pass').val()!="") && ($('#retype-pass').val()!=""))
		{
			if ($('#new-pass').val() === $('#retype-pass').val()) {
				
				}else{
				$('.password-error').show();
				$('#retype-pass').addClass('error');
			}
		}
	});
	
	$(".ico-menu").bind('mouseover', function () {
		$(this).addClass('open');
	});
	
	$(".ico-menu").bind('mouseout', function () { 
		$(this).removeClass('open');
	});

	loadComplaints();

	$('.inboxLoad').click(function(){
		loadComplaints();
	});
	
	$(document).on('click','.open_popup',function(e){
		var srn = $(this).data('srn');
		openPopUp('view-complaint.html?srn='+srn,srn);
		e.stopPropagation();
	});
		
});

function loadComplaints(){
	$.ajax({
		url : "/pgr/seva/_search?tenantId=ap.public&user_id="+localStorage.getItem("id"),
		type : 'POST',
		dataType: 'json',
		processData : false,
		contentType: "application/json",
		data : JSON.stringify(requestInfo),
		beforeSend : function(){
			showLoader();
		},
		success : function(response){
			//$("#grievance-template").html('');
			var source   = $("#grievance-template").html();
			var template = Handlebars.compile(source);
			var html = template(response.service_requests);
			$('.reloadtemplate').remove();
			$('.grievanceresponse').append(html);
		},
		error : function(){
			bootbox.alert('Error!')
		},
		complete : function(){
			hideLoader();
			search($('.searchinbox'));
		}
	});
}

function search(elem) {
	var searchText = $(elem).val(); 
	if($.trim(searchText)){
		$(".reloadtemplate").each(function() {
	         var $this = $(this)
	         if ($this.find('div').text().toUpperCase().search(searchText.toUpperCase()) === -1) {
	             $this.hide();
	         }else {
		         $this.show();
		     }
	    });
	}
};