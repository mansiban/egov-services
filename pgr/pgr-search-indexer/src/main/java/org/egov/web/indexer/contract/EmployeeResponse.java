package org.egov.web.indexer.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EmployeeResponse {

    @JsonProperty("Employees")
    private List<Employee> employees = new ArrayList<Employee>();


    public EmployeeResponse employees(List<Employee> employees) {
        this.employees = employees;
        return this;
    }

    public EmployeeResponse addEmployeeItem(Employee employeeItem) {
        this.employees.add(employeeItem);
        return this;
    }

}
