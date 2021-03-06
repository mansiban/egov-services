function getValueByName(name, id) {
    for (var i = 0; i < assetCategories.length; i++) {
        if (assetCategories[i].id == id) {
            return assetCategories[i][name];
        }
    }
}

var _type;
$(document).ready(function() {
    $('.datetimepicker').datetimepicker({
        format: 'DD/MM/YYYY'
    });
    $("#viewDcb").on("click", function() {
        //clear cookies and logout
        // $("#login").hide();
        // $("#dashboard").show();
        window.location = "app/search-agreement/view-dcb.html"
    });

    $("#close").on("click", function() {
        open(location, '_self').close();
    })

    // $('#printNotice').on("click", function() {
    //     // call api
    //     // alert("asda");
    //
    // })

    function printNotice(noticeData) {
        var doc = new jsPDF();
        doc.setFontType("bold");
        doc.text(15, 20, tenantId.split(".")[1] + ' Municipal Corporation');
        doc.text(15, 30, tenantId.split(".")[1] + ' District');
        doc.text(15, 40, 'Asset Category Lease/Agreement Notice');
        doc.setLineWidth(0.5);
        doc.line(15, 42, 195, 42);
        doc.text(15, 47, 'Lease details:');
        doc.text(110, 47, 'Agreement No:' + noticeData.agreementNumber);
        doc.text(15, 57, 'Lease Name:   ' + noticeData.allotteeName);
        doc.text(110, 57, 'Asset No:' + noticeData.assetNo);
        doc.text(15, 67, noticeData.allotteeMobileNumber + "," + noticeData.doorNo + "," + noticeData.allotteeAddress + "," + tenantId.split(".")[1] + ".");

        doc.setFontType("normal");
        doc.text(15, 77, doc.splitTextToSize('1.The period of lease shall be __' + noticeData.agreementPeriod * 12 + '____ months commencing from _____' + noticeData.commencementDate + '_____(dd-mm-yyyy) to ____' + noticeData.expiryDate + '__________(dd-mm-yyyy).', (210 - 15 - 15)));
        doc.text(15, 93, doc.splitTextToSize('2.The property leased is shop No___' + noticeData.assetNo + '_____and shall be leased for a sum of Rs. ____' + noticeData.rent + '____/-DDD (Rupees _____' + noticeData.rentInWord + '______ only) per month exclusive of the payment of electricity and other charges.', (210 - 15 - 15)));
        doc.text(15, 123, doc.splitTextToSize('3.The lessee has paid a sum of Rs. ____' + noticeData.securityDeposit + '____/- (Rupees ____' + noticeData.securityDepositInWord + '____ only) as security deposit for the tenancy and the said sum is repayable or adjusted only at the end of the tenancy on the lease delivery vacant possession of the shop let out, subject to deductions, if any, lawfully and legally payable by the lessee under the terms of this lease deed and in law.', (210 - 15 - 15)));
        doc.text(15, 163, doc.splitTextToSize('4.The rent for every month shall be payable on or before __' + noticeData.rentPayableDate + '___ of the succeeding month.', (210 - 15 - 15)));
        doc.text(15, 178, doc.splitTextToSize('5.The lessee shall pay electricity charges to the Electricity Board every month without fail.', (210 - 15 - 15)));
        doc.text(15, 193, doc.splitTextToSize('6.The lessor or his agent shall have a right to inspect the shop at any hour during the day time.', (210 - 15 - 15)));
        doc.text(15, 208, doc.splitTextToSize('7.The Lessee shall use the shop let out duly for the business of General Merchandise and not use the same for any other purpose.  (The lessee shall not enter into partnership) and conduct the business in the premises in the name of the firm.  The lessee can only use the premises for his own business.', (210 - 15 - 15)));
        doc.addPage()
        doc.text(15, 20, doc.splitTextToSize('8.The lessee shall not have any right to assign, sub-let, re-let, under-let or transfer the tenancy or any portion thereof.', (210 - 15 - 15)));
        doc.text(15, 35, doc.splitTextToSize('9.The lessee shall not carry out any addition or alteration to the shop without the previous consent and approval in writing of the lessor.', (210 - 15 - 15)));
        doc.text(15, 50, doc.splitTextToSize('10. The lessee on the expiry of the lease period of __' + noticeData.expiryDate + '___ months shall hand over vacant possession of the ceased shop peacefully or the lease agreement can be renewed for a further period on mutually agreed terms.', (210 - 15 - 15)));

        doc.text(15, 90, noticeData.commissionerName);
        doc.text(160, 90, 'LESSEE');
        doc.text(15, 100, 'Signature:   ');
        doc.text(160, 100, 'Signature:  ');
        doc.text(15, 110, tenantId.split(".")[1]);

        doc.save('Notice.pdf');
    }

    // function wordWrap(doc, paragraph, lMargin, rMargin, pdfInMM) {
    //     return doc.splitTextToSize(paragraph, (pdfInMM - lMargin - rMargin));
    // }

    try {
        if (getUrlVars()["agreementNumber"]) {
            var agreementDetail = commonApiPost("lams-services", "agreements", "_search", {
                agreementNumber: getUrlVars()["agreementNumber"],
                tenantId
            }).responseJSON["Agreements"][0] || {};
        } else if (getUrlVars()["acknowledgementNumber"]) {
            var agreementDetail = commonApiPost("lams-services", "agreements", "_search", {
                acknowledgementNumber: getUrlVars()["acknowledgementNumber"],
                tenantId
            }).responseJSON["Agreements"][0] || {};
        } else {
            var agreementDetail = commonApiPost("lams-services", "agreements", "_search", {
                stateId: getUrlVars()["state"],
                tenantId
            }).responseJSON["Agreements"][0] || {};
        }


        var assetDetails = commonApiPost("asset-services", "assets", "_search", {
            id: (getUrlVars()["assetId"] || agreementDetail.asset.id),
            tenantId
        }).responseJSON["Assets"][0] || {};


        printValue("", agreementDetail);
        printValue("", assetDetails, true);
    } catch (e) {
        console.log(e);
    }

    function printValue(object = "", values, isAsset) {
        if (object != "") {

        } else {
            for (var key in values) {
                if (typeof values[key] === "object") {
                    for (ckey in values[key]) {
                        if (values[key][ckey]) {
                            //Get description
                            if (isAsset && key == "locationDetails" && ["locality", "electionWard", "street", "revenueWard", "revenueZone", "revenueBlock"].indexOf(ckey) > -1) {
                                var _obj;
                                switch (ckey) {
                                    case 'locality':
                                        _obj = locality;
                                        break;
                                    case 'electionWard':
                                        _obj = electionwards;
                                        break;
                                    case 'street':
                                        _obj = street;
                                        break;
                                    case 'revenueWard':
                                        _obj = revenueWards;
                                        break;
                                    case 'revenueZone':
                                        _obj = revenueZone;
                                        break;
                                    case 'revenueBlock':
                                        _obj = revenueZone;
                                        break;
                                }
                                $("[name='" + (isAsset ? "asset." : "") + key + "." + ckey + "']").text(getNameById(_obj, ckey) || "NA");
                            } else
                                $("[name='" + (isAsset ? "asset." : "") + key + "." + ckey + "']").text(values[key][ckey] ? values[key][ckey] : "NA");
                        }
                    }
                } else if (values[key]) {
                    $("[name='" + (isAsset ? "asset." : "") + key + "']").text(values[key]);
                } else {
                    $("[name='" + (isAsset ? "asset." : "") + key + "']").text("NA");
                }

                if (key.search('date') > 0) {
                    var d = new Date(values[key]);
                    $(`#${key}`).val(`${d.getDate()}-${d.getMonth()+1}-${d.getFullYear()}`);
                }
            }
        }
    }

    //base url for api_id
    // var baseUrl = "https://peaceful-headland-36194.herokuapp.com/v1/mSevaAndLA/";
    // //request info from cookies
    // var requestInfo = {
    //     "api_id": "string",
    //     "ver": "string",
    //     "ts": "2017-01-18T07:18:23.130Z",
    //     "action": "string",
    //     "did": "string",
    //     "key": "string",
    //     "msg_id": "string",
    //     "requester_id": "string",
    //     "auth_token": "aeiou"
    // };

    //agreementDetail = {};


    var renewAgreement = {};
    //Getting data for user input
    $("input").on("keyup", function() {
        // console.log(this.value);
        renewAgreement[this.id] = this.value;
    });

    //Getting data for user input
    $("select").on("change", function() {
        // console.log(this.value);
        renewAgreement[this.id] = this.value;

        if (($("#approver_department").val() != "" && $("#approver_designation").val() != "") && (this.id == "approver_department" || this.id == "approver_designation")) {
            employees = commonApiPost("hr-employee", "employees", "_search", {
                tenantId,
                departmentId: $("#approver_department").val(),
                designationId: $("#approver_designation").val()
            }).responseJSON["Employee"] || [];

            for (var i = 0; i < employees.length; i++) {
                $(`#approver_name`).append(`<option value='${employees[i]['id']}'>${employees[i]['name']}</option>`)
            }
        }
    });

    //file change handle for file upload
    $("input[type=file]").on("change", function(evt) {
        // console.log(this.value);
        // renewAgreement[this.id] = this.value;
        renewAgreement[this.id] = evt.currentTarget.files;

        //call post api update and update that url in pur agrement object
    });

    var validation_rules = {};
    var final_validatin_rules = {};

    if (getUrlVars()["view"] == "cancel") {
        var commom_fields_rules = {
            cancel_reason: {
                required: true
            },
            cancel_termination_date: {
                required: true
            },
            cancel_order_no: {
                required: true
            },
            cancel_order_date: {
                required: true
            }
        };
    } else if(getUrlVars()["view"] == "eviction") {
        var commom_fields_rules = {
            evict_reason: {
                required: true
            },
            evict_proceed_date: {
                required: true
            },
            evict_proceed_no: {
                required: true
            }
        };
    } else {
        var commom_fields_rules = {
            renew_order_no: {
                required: true
            },
            renew_advance_payment: {
                required: true
            },
            renew_rent: {
                required: true
            },
            renew_reason: {
                required: true
            },
            renew_rent_increment_method: {
                required: true
            },
            date_of_expiry: {
                required: true
            },
            renew_order_date: {
                required: true
            },
            renew_period: {
                required: true
            }
        };
    }

    //remove renew part and related buttons from dom
    if (getUrlVars()["view"] == "new") {
        //removing renew section and renew button
        $("#renew,#workFlowDetails,#renewBtn,#cancel,#evict").remove();
    } else if (getUrlVars()["view"] == "inbox") {
        $("#historyTable").show();
        $("#cancel,#evict,#renew,#renewBtn").remove();
        //Fetch workFlow
        var workflow = commonApiPost("egov-common-workflows", "history", "", {
            tenantId: tenantId,
            workflowId: agreementDetail.stateId
        }).responseJSON["tasks"];

        var process = commonApiPost("egov-common-workflows", "process", "_search", {
            tenantId: tenantId,
            id: agreementDetail.stateId
        }).responseJSON["processInstance"];

        if (workflow && workflow.length) {
            workflow = workflow.sort();
            for (var i = 0; i < workflow.length; i++) {
                $("#historyTable tbody").append(`<tr>
                    <td data-label="createdDate">${workflow[i].createdDate}</td>
                    <td data-label="updatedBy">${workflow[i].senderName}</td>
                    <td data-label="status">${workflow[i].status}</td>
                    <td data-label="comments">${workflow[i].comments}</td>
                    </tr>
                `);
            }
        }

        if (process && process.attributes && process.attributes.validActions && process.attributes.validActions.values && process.attributes.validActions.values.length) {
            for (var i = 0; i < process.attributes.validActions.values.length; i++) {
                if (process.attributes.validActions.values[i].key)
                    $("#footer-btn-grp").append($(`<button data-action=${process.attributes.validActions.values[i].key} id=${process.attributes.validActions.values[i].key} type="button" class="btn btn-submit">${process.attributes.validActions.values[i].name}<button/>`));
                if (process.attributes.validActions.values[i].key.toLowerCase() == "approve" || process.attributes.validActions.values[i].key.toLowerCase() == "print notice") {
                    $("#workFlowDetails").remove();
                }
            }
        } else {
            $("#workFlowDetails").remove();
        }

        if (process) {
            getDesignations(process.status, function(designations) {
                for (var variable in designations) {
                    $(`#approver_designation`).append(`<option value='${designations[variable]["id"]}'>${designations[variable]["name"]}</option>`)
                }
            });
        }
    } else if (getUrlVars()["view"] == "renew") {
        $("#cancel,#evict").remove();
        $(`label[for=renew_rent]`).text(getUrlVars()["type"] == "shop" ? "Shop Rent (Rs)" : "Land Rent (Rs)");
    } else if (["cancel", "eviction"].indexOf(getUrlVars()["view"]) > -1) {
        $("#renew").remove();
        //$("#renewBtn").text("Submit");
        $(".hide-sec").hide();
        $("#viewDetBtn").show();
        $("#minDetSec").show();
        if(getUrlVars()["view"] == "cancel") {
            $("#evict").remove();
        } else {
            $("#cancel").remove();
        }
    } else {
        $("#viewDcb,#cancel,#evict").remove();
    }

    for (var variable in department) {
        $(`#approver_department`).append(`<option value='${department[variable]["id"]}'>${department[variable]["name"]}</option>`)
    }
    /* for (var variable in designation) {
       $(`#approver_designation`).append(`<option value='${designation[variable]["id"]}'>${designation[variable]["name"]}</option>`)
     }*/

    _type = getUrlVars()["type"] ? decodeURIComponent(getUrlVars()["type"]) : getValueByName("name", agreementDetail.asset.assetCategory.id);
    if (_type == "land") {

        // remove all other Asset Details block from DOM except land asset related fields
        $(".shopAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateTwo,.agreementDetailsBlockTemplateThree").remove();

    } else if (_type == "shop") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateThree").remove();

    } else if (_type == "market") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    } else if (_type == "kalyanamandapam") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    } else if (_type == "parking_space") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();

    } else if (_type == "slaughter_house") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();

    } else if (_type == "usfructs") {


        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    } else if (_type == "community") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .fishTankAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    } else if (_type == "Fish Tank") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .parkAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    } else if (_type == "park") {

        // remove all other Asset Details block from DOM except shop asset related fields
        $(".rendCalculatedMethod,.shopAssetDetailsBlock, .landAssetDetailsBlock, .marketAssetDetailsBlock, .kalyanamandapamAssetDetailsBlock, .parkingSpaceAssetDetailsBlock, .slaughterHousesAssetDetailsBlock, .usfructsAssetDetailsBlock, .communityAssetDetailsBlock, .fishTankAssetDetailsBlock").remove();
        //remove agreement template two and three from screen
        $(".agreementDetailsBlockTemplateOne,.agreementDetailsBlockTemplateTwo").remove();
    }
    final_validatin_rules = Object.assign(validation_rules, commom_fields_rules);
    for (var key in final_validatin_rules) {
        if (final_validatin_rules[key].required) {
            $(`label[for=${key}]`).append(`<span> *</span>`);
        }
        // $(`#${key}`).attr("disabled",true);
    };



    $('body').on('click', 'button', function(e) {
        e.preventDefault();
        if (!e.target.id) return;
        var data = $("#" + e.target.id).data();
        if (data.action && data.action != "Print Notice") {
            var _agrmntDet = Object.assign({}, agreementDetail);
            _agrmntDet.workflowDetails = {
                "businessKey": process.businessKey,
                "type": "Agreement",
                "assignee": $("#approver_name") && $("#approver_name").val() ? $("#approver_name").val() : process.initiatorPosition,
                "status": process.status,
                "action": data.action
            };

            var response = $.ajax({
                url: baseUrl + `/lams-services/agreements/_update/${agreementDetail.acknowledgementNumber}?tenantId=` + tenantId,
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify({
                    RequestInfo: requestInfo,
                    Agreement: _agrmntDet
                }),
                async: false,
                headers: {
                    'auth-token': authToken
                },
                contentType: 'application/json'
            });
            if (response["status"] === 201) {
                window.location.href = "app/search-assets/create-agreement-ack.html?name=" + getNameById(employees, _agrmntDet["assignee"]) + "&ackNo=" + responseJSON["Agreements"][0]["acknowledgementNumber"];
            } else {
                showError(response["statusText"]);
            }
        } else {
            var response = $.ajax({
                url: baseUrl + `/lams-services/agreement/notice/_create?tenantId=` + tenantId,
                type: 'POST',
                dataType: 'json',
                data: JSON.stringify({
                    RequestInfo: requestInfo,
                    Notice: {
                        tenantId,
                        agreementNumber: _agrmntDet.agreementNumber
                    }
                }),
                async: false,
                headers: {
                    'auth-token': authToken
                },
                contentType: 'application/json'
            });

            if (response["status"] === 201) {
                printNotice(response["responseJSON"].Notices[0]);
                // window.location.href = "app/search-assets/create-agreement-ack.html?name=" + getNameById(employees, agreement["approverName"]) + "&ackNo=" + responseJSON["Agreements"][0]["acknowledgementNumber"];
            } else {
                console.log("Handle error.");
            }
        }
    });

    // Adding Jquery validation dynamically
    $("#renewAgreementForm").validate({
        rules: final_validatin_rules,
        submitHandler: function(form) {

        }
    })

    $("#cancelAgreementForm").validate({
        rules: final_validatin_rules,
        submitHandler: function(form) {

        }
    })
});
