package org.egov.workflow.domain.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;

import org.egov.workflow.persistence.entity.State;
import org.egov.workflow.persistence.entity.StateHistory;
import org.egov.workflow.persistence.entity.Task;
import org.egov.workflow.persistence.repository.EmployeeRepository;
import org.egov.workflow.web.contract.Assignment;
import org.egov.workflow.web.contract.Attribute;
import org.egov.workflow.web.contract.Department;
import org.egov.workflow.web.contract.Employee;
import org.egov.workflow.web.contract.EmployeeRes;
import org.egov.workflow.web.contract.ProcessInstance;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

@RunWith(MockitoJUnitRunner.class)
public class PgrWorkflowImplTest {

    private static final String TENANT_ID = "tenantId";

    @MockBean
    private PgrWorkflowImpl pgrWorkflowImpl;

    @MockBean
    private ComplaintRouterService complaintRouterService;

    @Mock
    private StateService stateService;

    @Mock
    private EmployeeRepository employeeRepository;
    
    @InjectMocks
    private PgrWorkflowImpl workflow;

    @Before
    public void before() {
        pgrWorkflowImpl = new PgrWorkflowImpl(complaintRouterService, stateService, employeeRepository);
    }
    
    @Test
    public void test_should_close_workflow() throws Exception {
        final State expectedState = new State();
        expectedState.setId(119L);
        expectedState.setComments("Workflow Terminated");

        final ProcessInstance expectedProcessInstance = ProcessInstance.builder()
                .type("Complaint")
                .description("Workflow Terminated")
                .assignee(2L)
                .values(new HashMap<String,Attribute>())
                .businessKey("765")
                .build();
        expectedProcessInstance.setStateId(119L);

        when(stateService.getStateById(119L))
                .thenReturn(expectedState);

        workflow.end(TENANT_ID,expectedProcessInstance);
    }

    @Test
    public void test_should_get_workflow_history_by_id() {
        State state = prepareState();
        EmployeeRes employeeRes = getEmployee();
        when(stateService.getStateById(2l)).thenReturn(state);
        when(employeeRepository.getEmployeeForUserId(1l)).thenReturn(employeeRes);
        when(employeeRepository.getEmployeeForPosition(3l, new LocalDate())).thenReturn(employeeRes);
        List<Task> actualHistory = pgrWorkflowImpl.getHistoryDetail(TENANT_ID, "2");
        assertEquals(3, actualHistory.size());
    }

    private State prepareState() {
        State state = new State();
        state.setOwnerPosition(3L);
        state.setId(2l);
        state.setOwnerUser(1L);
        state.setLastModifiedBy(1L);
        state.addStateHistory(prepareStateHistoryWithOwnerUser(state));
        state.addStateHistory(prepareStateHistoryWithOutOwnerUser(state));
        return state;
    }

    private EmployeeRes getEmployee() {
        EmployeeRes employeeRes = new EmployeeRes();
        Employee employee = new Employee();
        employee.getAssignments().add(prepareAssignment());
        employee.setName("narasappa");
        employee.setUsername("egovernments");
        employee.setId(1L);
        employee.setCode("001");
        employeeRes.addEmployeeItem(employee);
        return employeeRes;
    }

    private Assignment prepareAssignment() {
        Assignment assignment = new Assignment();
        Department dept = new Department();
        dept.setId(1L);
        dept.setCode("A");
        dept.setName("Accounts");
        assignment.setDepartment(dept);
        return assignment;
    }

    private StateHistory prepareStateHistoryWithOwnerUser(State state) {
        StateHistory stateHistory = new StateHistory(state);
        stateHistory.setId(2L);
        return stateHistory;
    }

    private StateHistory prepareStateHistoryWithOutOwnerUser(State state) {
        StateHistory stateHistory = new StateHistory(state);
        stateHistory.setId(2L);
        stateHistory.setOwnerUser(null);
        return stateHistory;
    }

}
