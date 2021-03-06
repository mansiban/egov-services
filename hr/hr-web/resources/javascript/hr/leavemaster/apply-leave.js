class ApplyLeave extends React.Component {
  constructor(props) {
    super(props);
    this.state={list:[],leaveSet:{
      "employee": "",
      "name":"",
      "code":"",
       "leaveType": {
       	"id" : ""
       },
       "fromDate" : "",
       "toDate": "",
       "availableDays": "",
       "leaveDays":"",
       "reason": "",
       "status": "",
       "stateId": "",
       "tenantId" : tenantId,
       "workflowDetails": {
        "department": "",
        "designation": "",
        "assignee": "",
        "action": "",
        "status": ""
      }

    },leaveList:[],stateId:"",owner:"",leaveNumber:""}
    this.handleChange=this.handleChange.bind(this);
    this.addOrUpdate=this.addOrUpdate.bind(this);
    this.handleChangeThreeLevel=this.handleChangeThreeLevel.bind(this);
    this.getPrimaryAssigmentDep=this.getPrimaryAssigmentDep.bind(this);
  }

  componentDidMount() {
    if(window.opener && window.opener.document) {
       var logo_ele = window.opener.document.getElementsByClassName("homepage_logo");
       if(logo_ele && logo_ele[0]) {
         document.getElementsByClassName("homepage_logo")[0].src = window.location.origin + logo_ele[0].getAttribute("src");
       }
     }
    var type = getUrlVars()["type"], _this = this;
    var id = getUrlVars()["id"];
    var asOnDate = _this.state.leaveSet.toDate;


    if(getUrlVars()["type"]==="view")
    {
        $("input,select,textarea").prop("disabled", true);
      }

      if(type === "view"){
        var _leaveSet = getCommonMasterById("hr-leave","leaveapplications","LeaveApplication",id).responseJSON["LeaveApplication"][0];
        var employee = commonApiPost("hr-employee", "employees", "_search", {
            tenantId,
            id: _leaveSet.employee
        }).responseJSON["Employee"][0];
        _leaveSet.name = employee.name;
        _leaveSet.code = employee.code;
        _this.setState({
           leaveSet: _leaveSet,
           leaveNumber:_leaveSet.applicationNumber
        })
      }
      else{
        $('#fromDate').datepicker({
            format: 'dd/mm/yyyy',
            autoclose:true

        });
        $('#fromDate').on("change", function(e) {
          var from = $('#fromDate').val();
          var to = $('#toDate').val();
          var dateParts = from.split("/");
          var newDateStr = dateParts[1] + "/" + dateParts[0] + "/ " + dateParts[2];
          var date1 = new Date(newDateStr);

          var dateParts = to.split("/");
          var newDateStr = dateParts[1] + "/" + dateParts[0] + "/" + dateParts[2];
          var date2 = new Date(newDateStr);
          if (date1 > date2) {
              showError("From date must be before of End date");
              $('#fromDate').val("");
          }
          _this.setState({
                leaveSet: {
                    ..._this.state.leaveSet,
                    "fromDate":$("#fromDate").val()
                }
          })

          });

          $('#toDate').datepicker({
              format: 'dd/mm/yyyy',
              autoclose:true

          });
          $('#toDate').on("change", function(e) {
            var from = $('#fromDate').val();
            var to = $('#toDate').val();
            var dateParts = from.split("/");
            var newDateStr = dateParts[1] + "/" + dateParts[0] + "/ " + dateParts[2];
            var date1 = new Date(newDateStr);

            var dateParts = to.split("/");
            var newDateStr = dateParts[1] + "/" + dateParts[0] + "/" + dateParts[2];
            var date2 = new Date(newDateStr);
            if (date1 > date2) {
                showError("End date must be after of From date");
                $('#toDate').val("");
            }

            _this.setState({
                  leaveSet: {
                      ..._this.state.leaveSet,
                      "toDate":$("#toDate").val()
                  }

            })
            var start = $('#fromDate').datepicker('getDate');
            var end   = $('#toDate').datepicker('getDate');
            var timeDiff = 1 + Math.round(end.getTime() - start.getTime());
                 var days = Math.ceil(timeDiff / (1000 * 3600 * 24));

                 if(days){
                       // Subtract two weekend days for every week in between
                       var weeks = Math.floor(days / 7);
                       days = days - (weeks * 2);

                       // Handle special cases
                       var startDay = start.getDay();
                       var endDay = end.getDay();

                       // Remove weekend not previously removed.
                       if (startDay - endDay > 1)
                           days = days - 2;

                       // Remove start day if span starts on Sunday but ends before Saturday
                       if (startDay == 0 && endDay != 6)
                           days = days - 1;

                       // Remove end day if span ends on Saturday but starts after Sunday
                       if (endDay == 6 && startDay != 0)
                           days = days - 1;
                   }

                   if (_this.state.leaveSet.toDate && _this.state.leaveSet.leaveType.id) {
                     try{
                        var leaveType = _this.state.leaveSet.leaveType.id;
                        var asOnDate = _this.state.leaveSet.toDate;
                        var employeeid =getUrlVars()["id"];
                        var object =  commonApiPost("hr-leave","eligibleleaves","_search",{leaveType,tenantId,asOnDate,employeeid}).responseJSON["EligibleLeave"][0];
                         _this.setState({
                             leaveSet:{
                                 ..._this.state.leaveSet,
                                 availableDays: object.noOfDays,
                                 leaveDays:days
                             }
                         })

                         }
                         catch (e){
                           console.log(e);
                         }
                    } else{
                      _this.setState({
                        leaveSet:{
                            ..._this.state.leaveSet,
                            leaveDays:days
                          }
                      })
                  }
            });

            if (!id) {

             var obj = commonApiPost("hr-employee","employees","_loggedinemployee",{tenantId}).responseJSON["Employee"][0];
            } else {
              var obj = getCommonMasterById("hr-employee","employees","Employee",id).responseJSON["Employee"][0];
            }

            _this.setState({
              leaveSet:{
                  ..._this.state.leaveSet,
                  name:obj.name,
                  code:obj.code,
                  employee:obj.id,
                  // workflowDetails:{
                  //   ..._this.state.leaveSet.workflowDetails,
                  //   assignee: assignee || ""
                  // }

                },
               departmentId:this.getPrimaryAssigmentDep(obj,"department")

            })



      }

    }

    getPrimaryAssigmentDep(obj,type)
    {

      for (var i = 0; i < obj.assignments.length; i++) {
        if(obj.assignments[i].isPrimary)
        {
          return obj.assignments[i][type];
        }
      }
    }

  componentWillMount()
  {
    try {
        var _leaveTypes = getCommonMaster("hr-leave","leavetypes","LeaveType").responseJSON["LeaveType"] || [];
    } catch(e) {
        var _leaveTypes = [];
    }
    this.setState({
    leaveList:_leaveTypes
      });
  }



  handleChangeThreeLevel(e,pName,name) {
    var _this=this;
    if(pName=="leaveType"&&_this.state.leaveSet.toDate){

      try{
      var leaveType = e.target.value;
      var asOnDate = _this.state.leaveSet.toDate;
      var employeeid = getUrlVars()["id"] || _this.state.leaveSet.employee;
        var object =  commonApiPost("hr-leave","eligibleleaves","_search",{leaveType,tenantId,asOnDate,employeeid}).responseJSON["EligibleLeave"][0];
          this.setState({
            leaveSet:{
              ...this.state.leaveSet,
              availableDays: object.noOfDays,
              [pName]:{
                  ...this.state.leaveSet[pName],
                  [name]:e.target.value
              }

            }
          })
        }
        catch (e){
          console.log(e);
        }


    } else {
      this.setState({
          leaveSet:{
              ...this.state.leaveSet,
              [pName]:{
                  ...this.state.leaveSet[pName],
                  [name]:e.target.value
              }
          }
      })
    }
  }

  handleChange(e,name) {
    this.setState({
        leaveSet:{
            ...this.state.leaveSet,
            [name]:e.target.value
        }
    })

}


  close(){
      // widow.close();
      open(location, '_self').close();
  }

addOrUpdate(e,mode)
{

        e.preventDefault();
        console.log(this.state.leaveNumber);
        var employee;
        var asOnDate = this.state.leaveSet.toDate;
        var departmentId = this.state.departmentId;
        var leaveNumber = this.state.leaveNumber;
        var owner = this.state.owner;
        var tempInfo=Object.assign({},this.state.leaveSet) , type = getUrlVars()["type"];
        delete  tempInfo.name;
        delete tempInfo.code;
        var res = commonApiPost("hr-employee","hod/employees","_search",{tenantId,asOnDate,departmentId})
          if(res && res.responseJSON && res.responseJSON["Employee"] && res.responseJSON["Employee"][0]){
            employee = res.responseJSON["Employee"][0]
          }
          else{
            employee={};
          }
          var hodname = employee.name;
          tempInfo.workflowDetails.assignee = employee.assignments && employee.assignments[0] ? employee.assignments[0].position : "";
        var body={
            "RequestInfo":requestInfo,
            "LeaveApplication":[tempInfo]
          },_this=this;
              $.ajax({
                    url: baseUrl+"/hr-leave/leaveapplications/_create?tenantId=" + tenantId,
                    type: 'POST',
                    dataType: 'json',
                    data:JSON.stringify(body),

                    contentType: 'application/json',
                    headers:{
                      'auth-token': authToken
                    },
                    success: function(res) {

                    var leaveNumber = res.LeaveApplication[0].applicationNumber;
                    window.location.href=`app/hr/leavemaster/ack-page.html?type=Apply&applicationNumber=${leaveNumber}&owner=${hodname}`;

                    },
                    error: function(err) {
                        showError(err);

                    }
                });
        }


  render() {
    let {handleChange,addOrUpdate,handleChangeThreeLevel}=this;
    let {leaveSet}=this.state;
    let {name,code,leaveDays,availableDays,fromDate,toDate,reason,leaveType}=leaveSet;
    let mode=getUrlVars()["type"];


    const showActionButton=function() {
      if((!mode) ||mode==="create")
      {
        return (<button type="submit" className="btn btn-submit">{mode?"Approve":"Apply"}</button>);
      }
    };

    const renderOption=function(list)
    {
      if(list)
      {
        return list.map((item)=>
        {
            return (<option key={item.id} value={item.id}>
                    {item.name}
              </option>)
        })
      }

    }


    return (
      <div>
        <h3>{ getUrlVars()["type"] ? titleCase(getUrlVars()["type"]) : "Apply"} Leave Application </h3>
        <form onSubmit={(e)=>{addOrUpdate(e)}}>
          <fieldset>
              <div className="row">
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">Employee Name</label>
                          </div>
                          <div className="col-sm-6">
                              <input type="text" id="name" name="name" value={name}
                              onChange={(e)=>{handleChange(e,"name")}}/>
                              </div>
                          </div>
                    </div>
                    <div className="col-sm-6">
                        <div className="row">
                            <div className="col-sm-6 label-text">
                              <label for="">Employee Code</label>
                            </div>
                            <div className="col-sm-6">
                                <input type="text" id="code" name="code" value={code}
                                onChange={(e)=>{handleChange(e,"code")}}/>
                            </div>
                        </div>
                      </div>
                    </div>


                <div className="row">
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">From Date <span>*</span></label>
                          </div>
                          <div className="col-sm-6">
                          <div className="text-no-ui">
                          <span><i className="glyphicon glyphicon-calendar"></i></span>
                          <input type="text" id="fromDate" name="fromDate" value="fromDate" value={fromDate}
                          onChange={(e)=>{handleChange(e,"fromDate")}}required/>

                          </div>
                      </div>
                    </div>
                </div>
                    <div className="col-sm-6">
                        <div className="row">
                            <div className="col-sm-6 label-text">
                              <label for="">To Date <span>*</span> </label>
                            </div>
                            <div className="col-sm-6">
                            <div className="text-no-ui">
                          <span><i className="glyphicon glyphicon-calendar"></i></span>
                          <input type="text"  id="toDate" name="toDate" value={toDate}
                          onChange={(e)=>{
                              handleChange(e,"toDate")}}required/>
                          </div>
                      </div>
                  </div>
                </div>
            </div>


            <div className="row">
                <div className="col-sm-6">
                    <div className="row">
                        <div className="col-sm-6 label-text">
                            <label for="leaveType">Leave Type<span>*</span></label>
                        </div>
                        <div className="col-sm-6">
                            <div className="styled-select">
                            <select id="leaveType" name="leaveType" value={leaveType.id} required="true" onChange={(e)=>{
                                handleChangeThreeLevel(e,"leaveType","id")
                            }}>
                            <option value=""> Select Leave Type</option>
                            {renderOption(this.state.leaveList)}
                           </select>

                            </div>
                        </div>
                    </div>
                </div>


                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                              <label for="Reason">Reason <span>*</span></label>
                          </div>
                          <div className="col-sm-6">
                          <textarea rows="4" cols="50" id="reason" name="reason" value={reason}
                          onChange={(e)=>{handleChange(e,"reason")}}required></textarea>
                          </div>
                      </div>
                  </div>
              </div>


              <div className="row">
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">Working Days</label>
                          </div>
                          <div className="col-sm-6">

                              <input type="number" id="leaveDays" name="leaveDays" value={leaveDays}
                              onChange={(e)=>{handleChange(e,"leaveDays")}}/>
                          </div>
                      </div>
                  </div>
                    <div className="col-sm-6">
                        <div className="row">
                            <div className="col-sm-6 label-text">
                              <label for="">Available Leave</label>
                            </div>
                            <div className="col-sm-6">
                                <input  type="number" id="availableDays" name="availableDays" value={availableDays}
                                onChange={(e)=>{handleChange(e,"availableDays")}}/>
                            </div>
                        </div>
                      </div>
                  </div>



            <div className="text-center">
            {showActionButton()} &nbsp;&nbsp;
            <button type="button" className="btn btn-close" onClick={(e)=>{this.close()}}>Close</button>

            </div>
          </fieldset>
          </form>
      </div> );

  }
}






ReactDOM.render(
  <ApplyLeave />,
  document.getElementById('root')
);
