class ShowPosition extends React.Component {
  constructor(props) {
    super(props);
    this.state={list:[],positionSet:{
    department:"",
    designation:"",
    name:"",
    isPostOutsourced:"",active:""}
    }

  }


  componentDidMount()
  {
    if(window.opener && window.opener.document) {
       var logo_ele = window.opener.document.getElementsByClassName("homepage_logo");
       if(logo_ele && logo_ele[0]) {
         document.getElementsByClassName("homepage_logo")[0].src = window.location.origin + logo_ele[0].getAttribute("src");
       }
     }
     
    try {
        var _position = commonApiPost("hr-masters","positions","_search",{tenantId,pageSize:500}).responseJSON["Position"] || [];
    } catch(e) {
        var _position = [];
    }

    try {
      var assignments_department = !localStorage.getItem("assignments_department") || localStorage.getItem("assignments_department") == "undefined" ? (localStorage.setItem("assignments_department", JSON.stringify(getCommonMaster("egov-common-masters", "departments", "Department").responseJSON["Department"] || [])), JSON.parse(localStorage.getItem("assignments_department"))) : JSON.parse(localStorage.getItem("assignments_department"));
    } catch (e) {
        console.log(e);
      var  assignments_department = [];
    }

    this.setState({
      list: _position,
      assignments_department
    });
  }

  componentDidUpdate(prevProps, prevState)
  {
      if (prevState.list.length!=this.state.list.length) {
          $('#positionTable').DataTable({
            dom: 'Bfrtip',
            buttons: [
                     'copy', 'csv', 'excel', 'pdf', 'print'
             ],
             ordering: false
          });
      }
  }


  close(){
      // widow.close();
      open(location, '_self').close();
  }

  render() {
    let {list,assignments_department}=this.state;
    let {department,designation,name,isPostOutsourced,active}=this.state.positionSet;
    var mode = getUrlVars()["type"];

    const renderAction=function(type,id){
      if (type==="update") {

              return (
                      <a href={`app/hr/master/position.html?id=${id}&type=${type}`} className="btn btn-default btn-action"><span className="glyphicon glyphicon-pencil"></span></a>
              );

    }else {
            return (
                    <a href={`app/hr/master/position.html?id=${id}&type=${type}`} className="btn btn-default btn-action"><span className="glyphicon glyphicon-modal-window"></span></a>
            );
        }
}


    const renderBody=function()
    {
      return list.map((item,index)=>
      {
            return (<tr key={index}>
                    <td>{index+1}</td>
                    <td data-label="department">{getNameById(assignments_department,item.deptdesig.department)}</td>
                    <td data-label="designation">{item.deptdesig.designation.name}</td>
                    <td data-label="name">{item.name}</td>
                    <td data-label="isPostOutsourced">{item.isPostOutsourced?"true":"false"}</td>
                    <td data-label="active">{item.active?"true":"false"}</td>

                    <td data-label="action">
                    {renderAction(getUrlVars()["type"],item.id)}
                    </td>
                </tr>
            );

      })
    }

      return (<div>
          <h3>{titleCase(getUrlVars()["type"])} Position</h3>
        <table id="positionTable" className="table table-bordered">
            <thead>
                <tr>
                    <th>Sl No.</th>
                    <th>Department</th>
                    <th>Designation</th>
                    <th>Position</th>
                    <th>Outsourced post</th>
                    <th>Active</th>
                    <th>Action</th>

                </tr>
            </thead>

            <tbody id="employeeSearchResultTableBody">
                {
                    renderBody()
                }
            </tbody>

        </table>
        <div className="text-center">
            <button type="button" className="btn btn-close"onClick={(e)=>{this.close()}}>Close</button>
        </div>
      </div>
    );
  }
}


ReactDOM.render(
  <ShowPosition />,
  document.getElementById('root')
);
