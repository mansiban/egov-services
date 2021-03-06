$('#close').on("click", function() {
    window.close();
})


var agreement = {};
var employees = [];

$(".disabled").attr("disabled", true);
//Getting data for user input
$("input").on("keyup", function() {
    fillValueToObject(this);
    if (this.id == "rent") {
        $('#securityDeposit').val(this.value * 3);
        $("#securityDeposit").attr({
            "min": this.value * 3
        });
        agreement["securityDeposit"] = this.value * 3;
    }
});

//Getting data for user input
$("select").on("change", function() {
    // console.log(this.value);
    if (this.id == "natureOfAllotment") {
        if (this.value == "DIRECT") {
            $(".disabled").attr("disabled", false);
        } else {
            $(".disabled").attr("disabled", true);
        }
    }

    if (($("#approverDepartment").val() != "" && $("#approverDesignation").val() != "") && (this.id == "approverDepartment" || this.id == "approverDesignation")) {
        employees = commonApiPost("hr-employee", "employees", "_search", {
            tenantId,
            departmentId: $("#approverDepartment").val(),
            designationId: $("#approverDesignation").val()
        }).responseJSON["Employee"] || [];
        //$(`#approverName`).html(`<option value=''>Select</option>`)

        for (var i = 0; i < employees.length; i++) {
            $(`#approverName`).append(`<option value='${employees[i]['id']}'>${employees[i]['name']}</option>`)
        }
    }

    // agreement[this.id] = this.value;
    fillValueToObject(this);

});

//file change handle for file upload
$("input[type=file]").on("change", function(evt) {
    agreement["documents"] = evt.currentTarget.files;
});


$(".onlyNumber").on("keydown", function(e) {
    var key = e.keyCode ? e.keyCode : e.which;
    if (!([8, 9, 13, 27, 46, 110, 190].indexOf(key) !== -1 ||
            (key == 65 && (e.ctrlKey || e.metaKey)) ||
            (key >= 35 && key <= 40) ||
            (key >= 48 && key <= 57 && !(e.shiftKey || e.altKey)) ||
            (key >= 96 && key <= 105)
        )) {
        e.preventDefault();
    }
    /*if(this.value.length > 11 && [8, 46, 37, 39].indexOf(key) == -1) {
      e.preventDefault();
    }*/
});

//it will split object string where it has .
function fillValueToObject(currentState) {
    if (currentState.id.includes(".")) {
        var splitResult = currentState.id.split(".");
        if (agreement.hasOwnProperty(splitResult[0])) {
            agreement[splitResult[0]][splitResult[1]] = currentState.value;
        } else {
            agreement[splitResult[0]] = {};
            agreement[splitResult[0]][splitResult[1]] = currentState.value;
        }



    } else {

        agreement[currentState.id] = currentState.value;

    }
}


var validationRules = {};
var finalValidatinRules = {};
var commomFieldsRules = {
    name: {
        required: true
    },
    address: {
        required: true
    },
    natureOfAllotment: {
        required: true
    },
    "allottee.aadhaarNumber": {
        required: false,
        aadhar: true
    },
    "allottee.pan": {
        required: false,
        panNo: true
    },
    "allottee.emailId": {
        required: true,
        email: true
    },
    "allottee.mobileNumber": {
        required: true,
        phone: true
    },
    tenderNumber: {
        required: true
    },
    tenderDate: {
        required: true
    },
    tin: {
        required: false
    },
    tradeLicenseNumber: {
        required: false
    },
    caseNumber: {
        required: true
    },
    orderDetails: {
        required: true
    },
    rrReadingNumber: {
        required: decodeURIComponent(getUrlVars()["type"]) != "land" ? true : false
    },
    registrationFee: {
        required: true
    },
    councilNumber: {
        required: true
    },
    councilDate: {
        required: true
    },
    bankGuaranteeAmount: {
        required: true
    },
    bankGuaranteeDate: {
        required: true
    },
    agreementNumber: {
        required: true
    },
    agreementDate: {
        required: true
    },
    securityDepositDate: {
        required: true
    },
    securityDeposit: {
        required: true
    },
    commencementDate: {
        required: true
    },
    expiryDate: {
        required: true
    },
    rent: {
        required: true
    },
    paymentCycle: {
        required: true
    },
    approverName: {
        required: true
    },
    rentIncrementMethod: {
        required: (decodeURIComponent(getUrlVars()["type"]) == "land" || decodeURIComponent(getUrlVars()["type"]) == "shop") ? true : false
    },
    remarks: {
        required: false
    },
    solvencyCertificateNo: {
        required: true
    },
    solvencyCertificateDate: {
        required: true
    },
    timePeriod: {
        required: true
    }
};
if (decodeURIComponent(getUrlVars()["type"]) == "land") {
    // validation rules for land agreement
    validationRules = {
            // landRegisterNumber: {
            //     required: true
            // },
            // particularsOfLand: {
            //     required: true
            // },
            // resurveyNumber: {
            //     required: true
            // },
            // landAddress: {
            //     required: true
            // },
            // townSurveyNumber: {
            //     required: true
            // }
            // ,
            // assetCategory: {
            //     required: true
            // },
            // assetName: {
            //     required: true
            // },
            // assetCode: {
            //     required: true
            // },
            // assetArea: {
            //     required: true
            // },
            // assetLocality: {
            //     required: true
            // },
            // assetStreet: {
            //     required: true
            // },
            // assetRevenueZone: {
            //     required: true
            // },
            // assetrevenueWards: {
            //     required: true
            // },
            // assetRevenueBlock: {
            //     required: true
            // },
            // assetElectionWard: {
            //     required: true
            // },
            // assetAssetAddress: {
            //     required: true
            // }
        }
        // remove all other Asset Details block from DOM except land asset related fields
    $("#shopAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateTwo,#agreementDetailsBlockTemplateThree").remove();
    //disabling input tag of asset details
    $("#landAssetDetailsBlock input").attr("disabled", true);
    //disabling text tag of asset details
    $("#landAssetDetailsBlock textarea").attr("disabled", true);

    //append category text
    $(".categoryType").prepend("Land ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "shop") {
    // validation rules for shop agreement
    validationRules = {
        // shoppingComplexName: {
        //     required: true
        // },
        // shoppingComplexNo: {
        //     required: true
        // },
        // shoppingComplexShopNo: {
        //     required: true
        // },
        // shoppingComplexFloorNo: {
        //     required: true
        // },
        // shopArea: {
        //     required: true
        // },
        // shoppingComplexAddress: {
        //     required: true
        // }
        // ,
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateThree").remove();
    //disabling input tag of asset details
    $("#shopAssetDetailsBlock input").attr("disabled", true);
    //disabling textarea tag of asset details
    $("#shopAssetDetailsBlock textarea").attr("disabled", true);
    //disabling select tag of asset details
    $("#shopAssetDetailsBlock select").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Shop ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Market") {
    // validation rules for shop agreement
    validationRules = {
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetArea: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#marketAssetDetailsBlock input").attr("disabled", true);
    $("#marketAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Market ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Kalyana Mandapam") {
    // validation rules for shop agreement
    validationRules = {
        // kalyanamandapamName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#kalyanamandapamAssetDetailsBlock input").attr("disabled", true);
    $("#kalyanamandapamAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Kalyanamandapam ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Parking Space") {
    // validation rules for shop agreement
    validationRules = {
        // parkingSpaceName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetArea: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#parkingSpaceAssetDetailsBlock input").attr("disabled", true);
    $("#parkingSpaceAssetDetailsBlock textarea").attr("disabled", true);

    //append category text
    $(".categoryType").prepend("Parking Space ");

} else if (decodeURIComponent(getUrlVars()["type"]) == "Slaughter House") {
    // validation rules for shop agreement
    validationRules = {
        // slaughterHouseName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#slaughterHousesAssetDetailsBlock input").attr("disabled", true);
    $("#slaughterHousesAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Slaughter House ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Usufruct") {
    // validation rules for shop agreement
    validationRules = {
        // usfructName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#usfructsAssetDetailsBlock input").attr("disabled", true);
    $("#usfructsAssetDetailsBlock textarea").attr("disabled", true);

    //append category text
    $(".categoryType").prepend("Usfructs ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Community Toilet Complex") {
    // validation rules for shop agreement
    validationRules = {
        // toiletComplexName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#communityAssetDetailsBlock input").attr("disabled", true);
    $("#communityAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Community ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Fish Tanks") {
    // validation rules for shop agreement
    validationRules = {
        // fishTankName: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#fishTankAssetDetailsBlock input").attr("disabled", true);
    $("#fishTankAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Fish Tank ");
} else if (decodeURIComponent(getUrlVars()["type"]) == "Parks") {
    // validation rules for shop agreement
    validationRules = {
        // park_name: {
        //     required: true
        // },
        // assetCategory: {
        //     required: true
        // },
        // assetName: {
        //     required: true
        // },
        // assetCode: {
        //     required: true
        // },
        // assetLocality: {
        //     required: true
        // },
        // assetStreet: {
        //     required: true
        // },
        // assetRevenueZone: {
        //     required: true
        // },
        // assetrevenueWards: {
        //     required: true
        // },
        // assetRevenueBlock: {
        //     required: true
        // },
        // assetElectionWard: {
        //     required: true
        // },
        // assetAssetAddress: {
        //     required: true
        // }
    }

    // remove all other Asset Details block from DOM except shop asset related fields
    $("#rendCalculatedMethod,#shopAssetDetailsBlock, #landAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo").remove();
    //disabling input tag of asset details
    $("#parkingSpaceAssetDetailsBlock input").attr("disabled", true);
    $("#parkingSpaceAssetDetailsBlock textarea").attr("disabled", true);
    //append category text
    $(".categoryType").prepend("Park ");
} else {
    // remove all other Asset Details block from DOM except land asset related fields
    $("#landAssetDetailsBlock,#shopAssetDetailsBlock, #marketAssetDetailsBlock, #kalyanamandapamAssetDetailsBlock, #parkingSpaceAssetDetailsBlock, #slaughterHousesAssetDetailsBlock, #usfructsAssetDetailsBlock, #communityAssetDetailsBlock, #fishTankAssetDetailsBlock, #parkAssetDetailsBlock").remove();
    //remove agreement template two and three from screen
    $("#agreementDetailsBlockTemplateOne,#agreementDetailsBlockTemplateTwo,#agreementDetailsBlockTemplateThree").remove();
    alert("Agreement is not applicable for selected category");
}

try {
    rentInc = commonApiPost("lams-services", "getrentincrements", "", {
        tenantId
    }).responseJSON;

    if (rentInc && rentInc.constructor == Array) {
        for (var i = 0; i < rentInc.length; i++) {
            $(`#rentIncrementMethod`).append(`<option value='${rentInc[i]['id']}'>${rentInc[i]['type']}</option>`)
        }
    }
} catch (e) {
    console.log(e);
}

// finalValidatinRules = Object.assign(validationRules, commomFieldsRules);
finalValidatinRules = Object.assign({}, commomFieldsRules);

for (var key in finalValidatinRules) {
    if (finalValidatinRules[key].required) {
        if (key.split(".").length == "2") {
            $(`label[for=${key.split(".")[0]}\\.${key.split(".")[1]}]`).append(`<span> *</span>`);
        } else {
            $(`label[for=${key}]`).append(`<span> *</span>`);
        }
    }
    // $(`#${key}`).attr("disabled",true);
};



$.validator.addMethod('phone', function(value) {
    return /^[0-9]{10}$/.test(value);
}, 'Please enter a valid phone number.');

$.validator.addMethod('aadhar', function(value) {
    return value ? /^[0-9]{12}$/.test(value) : true;
}, 'Please enter a valid aadhar.');

$.validator.addMethod('panNo', function(value) {
    return /^(?:[0-9]+[a-z]|[a-z]+[0-9])[a-z0-9]*$/i.test(value) && value.length === 10;
}, 'Please enter a valid pan.');

finalValidatinRules["messages"] = {
    "allottee.name": {
        required: "Enter Name of the Allottee/Lessee"
    },
    "allottee.permanentAddress": {
        required: "Enter Address of the Allottee/Lessee"
    },
    natureOfAllotment: {
        required: "Enter Nature of allotment of the agreement"
    },
    "allottee.aadhaarNumber": {
        required: "Enter Aadhar no. of Allottee"
    },
    "allottee.pan": {
        required: "Enter PAN no. of Allottee"
    },
    "allottee.emailId": {
        required: "Enter Email ID of Allottee to get Notifications"
    },
    "allottee.mobileNumber": {
        required: "Enter Mobile number of the Allottee to get Notifications"
    },
    tenderNumber: {
        required: "Enter Tender/Auction no. of the agreement"
    },
    tenderDate: {
        required: "Enter Tender/Auction date of the agreement"
    },
    tin: {
        required: "Enter valid TIN number"
    },
    tradeLicenseNumber: {
        required: "Enter respective Trade license number"
    },
    // caseNumber: {
    //     required: true
    // },
    // orderDetails: {
    //     required: true
    // },
    rrReadingNumber: {
        required: decodeURIComponent(getUrlVars()["type"]) != "land" ? "Enter Electricity reading number" : ""
    },
    registrationFee: {
        required: "Enter Registration fee paid"
    },
    councilNumber: {
        required: "Enter Council/Standing committee resolution number"
    },
    councilDate: {
        required: "Enter Council/Standing committee resolution date"
    },
    bankGuaranteeAmount: {
        required: "Enter Bank guarantee amount"
    },
    bankGuaranteeDate: {
        required: "Enter Bank guarantee date"
    },
    agreementNumber: {
        required: "Enter Agreement Number"
    },
    agreementDate: {
        required: "Enter Agreement Date"
    },
    securityDepositDate: {
        required: "Enter security deposit received date by ULB"
    },
    securityDeposit: {
        required: "Enter Security deposit for Agreement"
    },
    commencementDate: {
        required: "Enter Date of commencement of asset"
    },
    // expiryDate: {
    //     required: true
    // },
    rent: {
        required: "Enter shop rent per month"
    },
    // paymentCycle: {
    //     required: true
    // },
    rentIncrementMethod: {
        required: decodeURIComponent(getUrlVars()["type"]) == "land" || decodeURIComponent(getUrlVars()["type"]) == "shop" ? "Select increase in monthly rent at the time of renewal" : ""
    },
    // remarks: {
    //     required: "Enter Remarks if any"
    // },
    solvencyCertificateNo: {
        required: "Enter Solvency certificate number"
    },
    solvencyCertificateDate: {
        required: "Enter Solvency certificate date"
    }


}

// $("#"+name).val("murali");

var assetDetails = commonApiPost("asset-services", "assets", "_search", { id: getUrlVars()["assetId"], tenantId }).responseJSON["Assets"][0] || {};
// var natureOfAllotments=commonApiPost("lams-services","","getnatureofallotment",{}).responseJSON ||{};
// var designation= getCommonMaster("hr-masters", "designations", "Designation").responseJSON["Designation"] || [];
// var department= getCommonMaster("egov-common-masters", "departments", "Department").responseJSON["Department"] || [];

for (var variable in natureOfAllotments) {
    if (natureOfAllotments.hasOwnProperty(variable)) {
        $(`#natureOfAllotment`).append(`<option value='${variable}'>${natureOfAllotments[variable]}</option>`)

    }
}

for (var variable in paymentCycle) {
    if (paymentCycle.hasOwnProperty(variable)) {
        $(`#paymentCycle`).append(`<option value='${variable}'>${paymentCycle[variable]}</option>`)

    }
}

$(`#approverDepartment`).html(`<option value=''>Select</option>`);
for (var variable in department) {
    $(`#approverDepartment`).append(`<option value='${department[variable]["id"]}'>${department[variable]["name"]}</option>`)

}
$(`#approverDesignation`).html(`<option value=''>Select</option>`)

/*for (var variable in designation) {


    $(`#approverDesignation`).append(`<option value='${designation[variable]["id"]}'>${designation[variable]["name"]}</option>`)


}*/

getDesignations(null, function(designations) {
    for (let variable in designations) {
        if (!designations[variable]["id"]) {
            var _res = commonApiPost("hr-masters", "designations", "_search", { tenantId, name: designations[variable]["name"] });
            designations[variable]["id"] = _res && _res.responseJSON && _res.responseJSON["Designation"] && _res.responseJSON["Designation"][0] ? _res.responseJSON["Designation"][0].id : "";
        }

        $(`#approverDesignation`).append(`<option value='${designations[variable]["id"]}'>${designations[variable]["name"]}</option>`);
    }
});

// var locality=commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"LOCALITY",hierarchyTypeName:"LOCATION"}).responseJSON["Boundary"] || [],
// var electionwards=commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"WARD",hierarchyTypeName:"ADMINISTRATION"}).responseJSON["Boundary"] || [],
// var revenueWardss=commonApiPost("v1/location/boundarys","boundariesByBndryTypeNameAndHierarchyTypeName","",{boundaryTypeName:"WARD",hierarchyTypeName:"REVENUE"}).responseJSON["Boundary"] || []
if (assetDetails && Object.keys(assetDetails).length) {
    $("#assetCategory\\.name").val(assetDetails["assetCategory"]["name"]);

    $("#aName").val(assetDetails["name"]);

    $("#totalArea").val(assetDetails["totalArea"]);

    $("#code").val(assetDetails["code"]);

    $("#locationDetails\\.locality").val(getNameById(locality, assetDetails["locationDetails"]["locality"]));

    $("#locationDetails\\.street").val(getNameById(street, assetDetails["locationDetails"]["street"]));

    $("#locationDetails\\.zone").val(getNameById(revenueZone, assetDetails["locationDetails"]["zone"]));

    $("#locationDetails\\.revenueWards").val(getNameById(revenueWards, assetDetails["locationDetails"]["revenueWards"]));

    $("#locationDetails\\.block").val(getNameById(revenueBlock, assetDetails["locationDetails"]["block"]));

    $("#locationDetails\\.electionWard").val(getNameById(electionwards, assetDetails["locationDetails"]["electionWard"]));
}

$('.datetimepicker').datetimepicker({
    format: 'DD/MM/YYYY'
});

$(".datetimepicker").on("dp.change", function() {
    // alert('hey');
    fillValueToObject(this);
});

// printValue("",assetDetails)
//
//
// function printValue(object="",values) {
//   if (object != "") {
//
//   }
//   else {
//     for (var key in values)
//     {
//             if (typeof(values[key])=="object") {
//                 for (var variable in values[key]) {
//                     $("#"+key+"\\."+variable).val(values[key][variable]);
//                 }
//             }
//             else {
//                   if(key=="name")
//                   {
//                     $("#aName").val(values[key]);
//
//                   }
//                   else {
//                     $("#"+key).val(values[key]);
//
//                   }
//             }
//
//     }
//     // for (var key in values) {
//     //     if (key.search('date')>0) {
//     //         // console.log(key);
//     //             var d=new Date(values[key]);
//     //             $(`#${key}`).val(`${d.getDate()}-${d.getMonth()+1}-${d.getFullYear()}`);
//     //     }
//     // }
//
//   }
// }

$("#createAgreement").on("click", function(e) {
    e.preventDefault();
    $("#createAgreementForm").submit();
    // switchValidation("final_validatin_rules");
})


function getPositionId(id) {
    var tempEmploye = {};
    for (var i = 0; i < employees.length; i++) {
        if (employees[i].id == id) {
            tempEmploye = employees[i];
        }
    }

    for (var i = 0; i < tempEmploye.assignments.length; i++) {
        if (tempEmploye.assignments[i].isPrimary) {
            return tempEmploye.assignments[i].position;
        }
    }
}

// Adding Jquery validation dynamically
$("#createAgreementForm").validate({
    rules: finalValidatinRules,
    messages: finalValidatinRules["messages"],
    submitHandler: function(form) {
        // form.submit();
        // form.preventDefault();
        agreement["workflowDetails"] = {};
        agreement["workflowDetails"]["assignee"] = getPositionId(agreement["approverName"]);
        agreement["asset"] = {};
        agreement["asset"]["id"] = getUrlVars()["assetId"];
        agreement["asset"]["name"] = assetDetails["name"];
        agreement["asset"]["code"] = assetDetails["code"];
        agreement["asset"]["assetCategory"] = {};
        agreement["asset"]["assetCategory"]["id"] = assetDetails["assetCategory"]["id"];
        agreement["asset"]["assetCategory"]["code"] = assetDetails["assetCategory"]["code"];
        agreement["asset"]["assetCategory"]["name"] = assetDetails["assetCategory"]["name"];

        agreement["rentIncrementMethod"] = {};
        agreement["rentIncrementMethod"]["id"] = $("#rentIncrementMethod").val();
        agreement["tenantId"] = tenantId;
        uploadFiles(agreement, function(err, _agreement) {
            if (err) {
                //Handle error
            } else {
                // $.post(`${baseUrl}agreements?tenant_id=kul.am`, {
                //     RequestInfo: requestInfo,
                //     Agreement: agreement
                // }, function(response) {
                //     // alert("submit");
                //     // window.open("../../../../app/search-assets/create-agreement-ack.html?&agreement_id=aeiou", "", "width=1200,height=800")
                //     // console.log(response);
                // })

                var response = $.ajax({
                    url: baseUrl + "/lams-services/agreements/_create?tenantId=" + tenantId,
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify({
                        RequestInfo: requestInfo,
                        Agreement: _agreement
                    }),
                    async: false,
                    headers: {
                        'auth-token': authToken
                    },
                    contentType: 'application/json'
                });

                if (response["status"] === 201) {
                    if (typeof(response["responseJSON"]["Error"]) != "undefined") {
                        showError(response["responseJSON"]["Error"]["message"]);
                    } else {
                        window.location.href = "app/search-assets/create-agreement-ack.html?name=" + getNameById(employees, agreement["approverName"]) + "&ackNo=" + response.responseJSON["Agreements"][0]["acknowledgementNumber"];
                    }

                } else {
                    showError(err);
                }
            }
        })
    }
})

function uploadFiles(agreement, cb) {
    if (agreement.documents && agreement.documents.constructor == FileList) {
        let counter = agreement.documents.length,
            breakout = 0;
        for (let i = 0; len = agreement.documents.length, i < len; i++) {
            makeAjaxUpload(agreement.documents[i], function(err, res) {
                if (breakout == 1)
                    return;
                else if (err) {
                    cb(err);
                    breakout = 1;
                } else {
                    counter--;
                    agreement.documents[i] = `/filestore/v1/files/id?fileStoreId=${res.files[0].fileStoreId}`;
                    if (counter == 0 && breakout == 0)
                        uploadFiles(agreement, cb);
                }
            })
        }
    } else {
        cb(null, agreement);
    }
}

function makeAjaxUpload(file, cb) {
    let formData = new FormData();
    formData.append("jurisdictionId", "ap.public");
    formData.append("module", "PGR");
    formData.append("file", file);
    $.ajax({
        url: baseUrl + "/filestore/v1/files?tenantId=" + tenantId,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        type: 'POST',
        success: function(res) {
            cb(null, res);
        },
        error: function(jqXHR, exception) {
            cb(jqXHR.responseText || jqXHR.statusText);
        }
    });
}
