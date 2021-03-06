var baseUrl = window.location.origin;




var authToken=localStorage.getItem("auth-token");

//request info from cookies
var requestInfo = {
    "apiId":"org.egov.pgr",
    "ver":"1.0",
    "ts": "2015-04-12",
    "action":"asd",
    "did":"4354648646",
    "key":"xyz",
    "msgId":"654654",
    "requesterId":"61",
    "authToken":authToken
};

var tenantId = "ap." + window.location.origin.split("-")[0].split("//")[1];


// var response=$.ajax({
//           url: window.location.origin+"/user/_login?tenantId=ap.public&username=ramakrishna&password=demo&grant_type=password&scope=read",
//           type: 'POST',
//           dataType: 'json',
//           // data:JSON.stringify({RequestInfo: requestInfo}),
//           async: false,
//           contentType: 'application/json',
//           headers:{
//             'Authorization' :'Basic ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0'
//           }
//       });
//
//       if(response["statusText"]==="OK")
//       {
//           localStorage.setItem("auth-token", response.responseJSON["access_token"]);
//           authToken=response.responseJSON["access_token"];
//         // alert("Successfully added");
//       }
//       else {
//         alert(response["statusText"]);
//       }



function getCommonMaster(mainRoute,resource,returnObject) {
    return $.ajax({
              url: baseUrl+"/"+mainRoute+"/"+resource+"/_search?tenantId="+tenantId,
              type: 'POST',
              dataType: 'json',
              data:JSON.stringify({RequestInfo: requestInfo}),
              async: false,
              // crossDomain: true, // set this to ensure our $.ajaxPrefilter hook fires
              // processData: false, // We want this to remain an object for  $.ajaxPrefilter
              headers: {
                      'auth-token': authToken
              },
              contentType: 'application/json'
              // ,
              // success: function (result) {
              //     return result[returnObject];
              //     // console.log(result);
              //    // CallBack(result);
              // },
              // error: function (error) {
              //     return [];
              //     // console.log(error);
              // }
          });
    // return response.statusText==="Ok"?response.responseJSON[returnObject]:[];
}

function commonApiPost(context,resource="",action="",queryObject={}) {
  var url=baseUrl+"/"+context+(resource?"/"+resource:"")+(action?"/"+action:"")+(queryObject?"?":"");
  for (var variable in queryObject) {
      if (queryObject[variable]) {
        url+="&"+variable+"="+queryObject[variable];
      }
  }
  return $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            data:JSON.stringify({RequestInfo: requestInfo}),
            async: false,
            contentType: 'application/json',
            headers:{
              'auth-token' :authToken
            }
        });
}

function commonApiGet(context,resource="",action="",queryObject={}) {
  var url=baseUrl+"/"+context+(resource?"/"+resource:"")+(action?"/"+action:"")+(queryObject?"?":"");
  for (var variable in queryObject) {
      if (queryObject[variable]) {
        url+="&"+variable+"="+queryObject[variable];
      }
  }
  return $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            headers: {
                    'auth-token': authToken
            },
            // data:JSON.stringify({RequestInfo: requestInfo}),
            async: false,
            contentType: 'application/json'
        });
}

function getUrlVars() {
    var vars = [],
        hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getCommonMasterById(mainRoute,resource,returnObject,id) {
    return $.ajax({
              url: baseUrl+"/"+mainRoute+"/"+resource+"/_search?tenantId="+tenantId+"&"+"id="+id,
              type: 'POST',
              dataType: 'json',
              data:JSON.stringify({RequestInfo: requestInfo}),
              async: false,
              // crossDomain: true, // set this to ensure our $.ajaxPrefilter hook fires
              // processData: false, // We want this to remain an object for  $.ajaxPrefilter
              headers: {
                      'auth-token': authToken
              },
              contentType: 'application/json'
              // ,
              // success: function (result) {
              //     return result[returnObject];
              //     // console.log(result);
              //    // CallBack(result);
              // },
              // error: function (error) {
              //     return [];
              //     // console.log(error);
              // }
          });
    // return response.statusText==="Ok"?response.responseJSON[returnObject]:[];
}

// commonApiPost("asset","assetCategories","",{boundaryTypeName:"LOCALITY",hierarchyTypeName:"LOCATION"})
