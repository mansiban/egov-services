var flag = 0;
class SearchLeaveApplication extends React.Component {
  constructor(props) {
    super(props);
    this.state={employees:[],employ:{},searchSet:{

    name:"",
    code:"",
    departmentId:"",
    designationId:""},isSearchClicked:false,assignments_department:[],assignments_designation:[]}
    this.handleChange=this.handleChange.bind(this);
    this.search=this.search.bind(this);
    this.handleBlur=this.handleBlur.bind(this);

  }

  componentDidMount(){
    if(window.opener && window.opener.document) {
       var logo_ele = window.opener.document.getElementsByClassName("homepage_logo");
       if(logo_ele && logo_ele[0]) {
         document.getElementsByClassName("homepage_logo")[0].src = window.location.origin + logo_ele[0].getAttribute("src");
       }
     }
  }


  search(e)
  {
    let {
    name,
    code,
    departmentId,
    designationId}=this.state.searchSet;
    e.preventDefault();
    //call api call
    var employees=[];
    try{
      employees=commonApiPost("hr-employee","employees","_search",{tenantId,
      departmentId: departmentId || null,
      designationId: designationId || null,
      code: code || null,
      pageSize: 500},this.state.searchSet).responseJSON["Employee"] || [];
    } catch(e){
      employees = [];
    }

    flag = 1;
    this.setState({
      isSearchClicked:true,
      employees
    })
  }


  componentWillMount()
  {
    try {
      var assignments_designation = !localStorage.getItem("assignments_designation") || localStorage.getItem("assignments_designation") == "undefined" ? (localStorage.setItem("assignments_designation", JSON.stringify(getCommonMaster("hr-masters", "designations", "Designation").responseJSON["Designation"] || [])), JSON.parse(localStorage.getItem("assignments_designation"))) : JSON.parse(localStorage.getItem("assignments_designation"));
    } catch (e) {
        console.log(e);
         var assignments_designation = [];
    }

    try {
      var assignments_department = !localStorage.getItem("assignments_department") || localStorage.getItem("assignments_department") == "undefined" ? (localStorage.setItem("assignments_department", JSON.stringify(getCommonMaster("egov-common-masters", "departments", "Department").responseJSON["Department"] || [])), JSON.parse(localStorage.getItem("assignments_department"))) : JSON.parse(localStorage.getItem("assignments_department"));
    } catch (e) {
        console.log(e);
      var  assignments_department = [];
    }

    this.setState({
      assignments_department:assignments_department,
      assignments_designation:assignments_designation,
      assignments_designation,
      assignments_department
  })
  }

  handleChange(e,name)
  {

      this.setState({
          searchSet:{
              ...this.state.searchSet,
              [name]:e.target.value
          }
      })
  }

  componentWillUpdate() {
    if(flag == 1) {
      flag = 0;
      $('#employeeTable').dataTable().fnDestroy();
    }
  }

  handleBlur(e) {
    setTimeout(function(){
       if(document.activeElement.id == "sub") {
          $("#sub").click();
       }
    }, 100);
    var _this = this;
      if (e.target.value) {
          try {
              var code = e.target.value;
              //Make get employee call
              var obj = commonApiPost("hr-employee", "employees", "_search", { code, tenantId }).responseJSON["Employee"][0];
              _this.setState({
                  searchSet: {
                      ..._this.state.searchSet,
                      name: obj.name
                  }
              })
          } catch (e) {
              console.log(e);
          }
      } else {
        _this.setState({
          searchSet: {
            ..._this.state.searchSet,
            name: ""
          }
        })
      }
    }





  close(){
      // widow.close();
      open(location, '_self').close();
  }


  componentDidUpdate(prevProps, prevState)
  {
      if (prevState.employees.length!=this.state.employees.length) {
          // $('#employeeTable').DataTable().draw();
          // alert(prevState.employees.length);
          // alert(this.state.employees.length);
          // alert('updated');
          $('#employeeTable').DataTable({
            dom: 'Bfrtip',
            buttons: [
                     'copy', 'csv', 'excel', 'pdf', 'print'
             ],
             ordering: false,
             bDestroy: true,
             language: {
              "emptyTable": "No Records"
            }
          });
      }
  }

  render() {
    let {handleChange,search,handleBlur}=this;
    let {isSearchClicked,employees,assignments_designation,assignments_department}=this.state;
    let {name,
    code,
    departmentId,
    designationId}=this.state.searchSet;
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


    const renderAction=function(type,id){
      if (type==="create") {

              return (
                      <a href={`app/hr/leavemaster/apply-leave.html?id=${id}&type=${type}`}>Apply Leave</a>
              );

    }else {
            return (
                    <a href={`app/hr/leavemaster/view-apply.html?id=${id}&type=${type}`}>View</a>
            );
        }
}


    const showTable=function()
    {
      if(isSearchClicked)
      {
          return (
            <table id="employeeTable" className="table table-bordered">
                <thead>
                    <tr>
                        <th>Employee Code</th>
                        <th>Employee Name</th>
                        <th>Employee Designation</th>
                        <th>Employee Department</th>
                        <th>master</th>

                    </tr>
                </thead>

                <tbody id="employeeSearchResultTableBody">
                    {
                        renderBody()
                    }
                </tbody>

            </table>

          )


      }

    }
    const renderBody=function()
    {
      if(employees.length>0)
      {
      return employees.map((item,index,id)=>
      {
            return (<tr key={index}>

                    <td data-label="code">{item.code}</td>
                    <td data-label="name">{item.name}</td>
                    <td data-label="designation">{getNameById(assignments_designation,item.assignments[0].designation)}</td>
                    <td data-label="department">{getNameById(assignments_department,item.assignments[0].department)}</td>
                    <td data-label="action">
                    {renderAction(getUrlVars()["type"],item.id)}
                    </td>

                </tr>
            );
      })
    }
}
    return (
      <div>
        <h3>Search employee to {getUrlVars()["type"]} a leave application</h3>
          <form onSubmit={(e)=>{search(e)}}>
          <fieldset>
          <div className="row">
                <div className="col-sm-6">
                    <div className="row">
                        <div className="col-sm-6 label-text">
                          <label for="">Designation  </label>
                        </div>
                        <div className="col-sm-6">
                          <div className="styled-select">
                            <select id="designationId" name="designationId" value={designationId} onChange={(e)=>{
                                handleChange(e,"designationId")}}>
                            <option value= "">Select Designation</option>
                            {renderOption(this.state.assignments_designation)}
                           </select>
                        </div>
                        </div>
                    </div>
                  </div>
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">Department  </label>
                          </div>
                          <div className="col-sm-6">
                          <div className="styled-select">
                              <select id="departmentId" name="departmentId" value={departmentId}
                              onChange={(e)=>{ handleChange(e,"departmentId")}}>
                                <option  value= "">Select Department</option>
                                {renderOption(this.state.assignments_department)}
                             </select>
                          </div>
                          </div>
                      </div>
                    </div>
                </div>


              <div className="row">
                <div className="col-sm-6">
                    <div className="row">
                        <div className="col-sm-6 label-text">
                          <label for="">Employee Code</label>
                        </div>
                        <div className="col-sm-6">
                            <input type="text" id="code" name="code" value={code}
                              onChange={(e)=>{handleChange(e,"code")}} onBlur={(e)=>{handleBlur(e)}}/>
                        </div>
                    </div>
                  </div>
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">Employee Name</label>
                          </div>
                          <div className="col-sm-6">
                              <input type="text" id="name" name="name" value={name}
                                onChange={(e)=>{  handleChange(e,"name")}} disabled/>
                          </div>
                      </div>
                    </div>
            </div>

                {/*<div className="row">
                      <div className="col-sm-6">
                          <div className="row">
                              <div className="col-sm-6 label-text">
                                <label for=""> Status</label>
                              </div>
                              <div className="col-sm-6">
                              <div className="styled-select">
                                  <select id="employeeStatusCode" name="employeeStatusCode" value={employeeStatusCode}
                                    onChange={(e)=>{ handleChange(e,"employeeStatusCode") }}>

                                      <option value="">Select Status</option>
                                      {renderOption(this.state.employeeStatus)}
                                 </select>
                              </div>
                              </div>
                          </div>
                        </div>
                        <div className="col-sm-6">
                            <div className="row">
                                <div className="col-sm-6 label-text">
                                  <label for="">Functionary  </label>
                                </div>
                                <div className="col-sm-6">
                                <div className="styled-select">
                                    <select id="functionaryCode" name="functionaryCode" value={functionaryCode}
                                      onChange={(e)=>{  handleChange(e,"functionaryCode")}}>

                                    <option>Select Functionary</option>
                                    {renderOption(this.state.assignments_functionary)}
                                   </select>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>

              <div className="row">
                  <div className="col-sm-6">
                      <div className="row">
                          <div className="col-sm-6 label-text">
                            <label for="">Type  </label>
                          </div>
                          <div className="col-sm-6">
                            <div className="styled-select">
                              <select id="employeeTypeCode" name="employeeTypeCode" value={employeeTypeCode} onChange={(e)=>{
                                  handleChange(e,"employeeTypeCode")}}>
                              <option>Select Type</option>
                              {renderOption(this.state.employeeType)}
                             </select>
                          </div>
                          </div>
                      </div>
                    </div>
                </div>*/}


            <div className="text-center">
                <button id="sub" type="submit"  className="btn btn-submit">Search</button> &nbsp;&nbsp;
                <button type="button" className="btn btn-close" onClick={(e)=>{this.close()}}>Close</button>
            </div>
          </fieldset>
          </form>
          <br/>
          {showTable()}
      </div>
    );
  }
}






ReactDOM.render(
  <SearchLeaveApplication />,
  document.getElementById('root')
);
