class PositionMaster extends React.Component{
    constructor(props){
      super(props);
      this.state={list:[],positionSet:{
      "id": "",
      "name": "",
      "deptdesig": {
        "id": "",
        "department": "",
        "designation": {
          "id": "",
          "name": "",
          "code": "",
          "description": "",
          "chartOfAccounts": null,
          "active": true,
          "tenantId": ""
        }
      },
      "isPostOutsourced": "",
      "active": "",
    },
      departmentsList:[],designationList:[]}
      this.handleChange=this.handleChange.bind(this);
      this.addOrUpdate=this.addOrUpdate.bind(this);
}
    componentWillMount()
    {
      this.setState({
        departmentsList:getCommonMaster("egov-common-masters","departments","Department").responseJSON["Department"] || [],
        designationList:getCommonMaster("hr-masters","designations","Designation").responseJSON["Designation"] || []
    })
    }

    handleChange(e,name){
      this.setState({
        positionSet:{
          ...this.state.positionSet,
          [name]:e.target.value
        }
      })

    }
    close(){
        // widow.close();
        open(location, '_self').close();
    }



    componentDidMount(){
      var type=getUrlVars()["type"];
      var id=getUrlVars()["id"];

      if(getUrlVars()["type"]==="view")
      {
          $("input,select").prop("disabled", true);
        }

        if(type==="view"||type==="update")
        {
            console.log(getCommonMasterById("hr-masters","positions","Position",id).responseJSON["Position"][0]);
            this.setState({
              positionSet:getCommonMasterById("hr-masters","positions","Position",id).responseJSON["Position"][0]
            })
        }
    }



    addOrUpdate(e,mode){
      e.preventDefault();
      console.log({name:this.state.positionSet.name,deptdesig:{designationList:this.state.positionSet.designation,departmentsList:this.state.positionSet.department},isPostOutsourced:this.state.positionSet.isPostOutsourced});
      if (mode==="update") {
          console.log("update");
      } else {
      this.setState({positionSet:{
      "id": "",
      "name": "",
      "deptdesig": {
        "id": "",
        "department": "",
        "designation": {
          "id": "",
          "name": "",
          "code": "",
          "description": "",
          "chartOfAccounts": null,
          "active": true,
          "tenantId": ""
        }
      },
      "isPostOutsourced": "",
      "active": "",
    },designationList:[],departmentsList:[]})
    }
  }

    render(){

      let {handleChange,addOrUpdate}=this;
      let {department,designation,name,isPostOutsourced,deptdesig}=this.state.positionSet;
      let mode =getUrlVars()["type"];


      const renderOption=function(list)
      {
          if(list)
          {
              return list.map((item)=>
              {
                  return (<option key={item.name} value={item.name}>
                          {item.name}
                    </option>)
              })
          }
      }
      const showActionButton=function() {
        if((!mode) ||mode==="update")
        {
          return (<button type="submit" className="btn btn-submit">{mode?"Update":"Add"}</button>);
        }
      };


      return (<div>
        <form  onSubmit={(e)=>{addOrUpdate(e,mode)}}>
        <fieldset>
        <div className="row">
          <div className="col-sm-6">
              <div className="row">
                  <div className="col-sm-6 label-text">
                    <label for="">Department  </label>
                  </div>
                  <div className="col-sm-6">
                  <div className="styled-select">
                      <select id="department" name="department" value={deptdesig.department} onChange={(e)=>{
                          handleChange(e,"department")
                      }}>
                        <option>Select Department</option>
                        {renderOption(this.state.departmentsList)}
                     </select>
                  </div>
                  </div>
              </div>
            </div>
            <div className="col-sm-6">
                <div className="row">
                    <div className="col-sm-6 label-text">
                      <label for="">Designation  </label>
                    </div>
                    <div className="col-sm-6">
                    <div className="styled-select">
                        <select id="designation" name="designation" value={deptdesig.designation.name} onChange={(e)=>{
                            handleChange(e,"designation")
                        }}>
                        <option>Select Designation</option>
                        {renderOption(this.state.designationList)}
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
                      <label for="">Position <span>*</span></label>
                  </div>
                  <div className="col-sm-6">
                  <input type="text" name="name" value={name} id= "name" onChange={(e)=>{
                      handleChange(e,"name")}}required/>
                  </div>
              </div>
            </div>
        </div>
        <div className="row">
          <div className="col-sm-6">
              <div className="row">
                  <div className="col-sm-6 label-text">
                      <label for="">Outsourced post </label>
                  </div>
                  <div className="col-sm-6">
                        <label className="radio-inline radioUi">
                          <input type="radio" name="isPostOutsourced" id="isPostOutsourced" value="yes"  onChange={(e)=>{
                              handleChange(e,"isPostOutsourced")}}/> Yes
                        </label>
                        <label className="radio-inline radioUi">
                          <input type="radio" name="isPostOutsourced" id="isPostOutsourced" value="No" onChange={(e)=>{
                              handleChange(e,"isPostOutsourced")}}/> No
                        </label>
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
      </div>);

    }
  }
  ReactDOM.render(
    <PositionMaster/>,
    document.getElementById('root')
  );