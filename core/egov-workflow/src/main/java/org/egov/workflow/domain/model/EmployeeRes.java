package org.egov.workflow.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeRes {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;

	@JsonProperty("Employees")
	private List<Employee> employees = new ArrayList<Employee>();

	public EmployeeRes responseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
		return this;
	}

	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}

	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public EmployeeRes employees(List<Employee> employees) {
		this.employees = employees;
		return this;
	}

	public EmployeeRes addEmployeeItem(Employee employeeItem) {
		this.employees.add(employeeItem);
		return this;
	}

}