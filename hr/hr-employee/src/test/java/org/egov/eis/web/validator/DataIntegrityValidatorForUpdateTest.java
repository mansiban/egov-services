package org.egov.eis.web.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.egov.eis.model.Employee;
import org.egov.eis.model.EmployeeDocument;
import org.egov.eis.model.enums.EntityType;
import org.egov.eis.repository.EmployeeDocumentsRepository;
import org.egov.eis.repository.EmployeeRepository;
import org.egov.eis.utils.FileUtils;
import org.egov.eis.web.contract.EmployeeResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class DataIntegrityValidatorForUpdateTest {

	@Mock
	private EmployeeRepository employeeRepository;
	
	@Mock
	private EmployeeDocumentsRepository employeeDocumentsRepository;
	
	@InjectMocks
	private DataIntegrityValidatorForUpdate dataIntegrityValidatorForUpdate;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testValidateEmployeeId() {
		//The employee id and all the entity ids are valid
		assertFalse(validateEmployee(true, "employees1.json").hasErrors());
		//The employee id is invalid
		assertTrue(validateEmployee(false, "employees1.json").hasErrors());
	}
	
	@Test
	public void testUniqueValuesOfEmployee() {
		// The gpf number and passport number are present for some other employee and hence has errors.
		assertTrue(validateEmployeeForUniqueValues(true, "employees1.json").hasErrors());
	}

	@Test
	public void testEntityIdsOfEmployee(){
		//The entity ids are invalid i.e which are not present in db.
		assertTrue(validateEmployee(true, "employees2.json").hasErrors());
	}
	
	@Test
	public void checkForErrorsWhenDocumentsWithDuplicatesWithinInput() {
		// documents within input has duplicates(repeated) and hence invalid
		assertTrue(validateEmployee(true, "employeesWithDuplicateDocumentsWithin.json").hasErrors());
	}
	
	@Test
	public void checkForErrorsWhenDocumentsWithDuplicatesWithinInputAcrossEntities() {
		// documents within input has duplicates(repeated) and hence invalid
		assertTrue(validateEmployee(true, "employeesWithDuplicateDocumentsWithin2.json").hasErrors());
	}
	
	@Test
	public void checkForErrorsWhenDocumentsWithDuplicatesInDb() {
		List<EmployeeDocument> documentsFromDb = new ArrayList<>();
		EmployeeDocument ed1 = EmployeeDocument.builder().id(100L).document("three")
				.referenceType(EntityType.EMPLOYEE_HEADER.getValue()).build();
		documentsFromDb.add(ed1);
		when(employeeDocumentsRepository.findByDocuments(any(), anyString()))
		.thenReturn(documentsFromDb);
		
		// documentsin db has duplicates and hance last argument is false
		assertTrue(validateEmployee(true, "employees1.json").hasErrors());
	}

	private Errors validateEmployee(boolean validEmployeeId, String employeeFileName) {
		List<Long> entityIds = new ArrayList<>();
		entityIds.add(10L);
		entityIds.add(11L);
		entityIds.add(12L);
		entityIds.add(13L);
		entityIds.add(14L);
		entityIds.add(15L);
		
		when(employeeRepository.checkIfEmployeeExists(100L, "1"))
		.thenReturn(validEmployeeId); 
		when(employeeRepository.getId("egeis_employee", "gpfNo", "123932", "1"))
        .thenReturn(0L);
		when(employeeRepository.getId("egeis_employee", "passportNo", "ASD234234", "1"))
        .thenReturn(100L);
		when(employeeRepository.getId("egeis_employee", "code", "00100", "1"))
		.thenReturn(100L);
		when(employeeRepository.getListOfIds(any(), any(), anyString()))
		.thenReturn(entityIds);
		
		Employee employee = null;
		try {
			employee = getEmployee(employeeFileName);
		} catch(Exception e) {
			e.printStackTrace();
			fail();
		}
		
		EmployeeResponse employeeResponse = new EmployeeResponse();
		BindException errors = new BindException(employeeResponse, "employeeResponse");
		ValidationUtils.invokeValidator(dataIntegrityValidatorForUpdate, employee, errors);	
		return errors;
	}
	
	private BindException validateEmployeeForUniqueValues(boolean validEmployeeId, String employeeFileName) {
		List<Long> entityIds = new ArrayList<>();
		entityIds.add(10L);
		entityIds.add(11L);
		entityIds.add(12L);
		entityIds.add(13L);
		entityIds.add(14L);
		entityIds.add(15L);
		
		when(employeeRepository.checkIfEmployeeExists(100L, "1"))
		.thenReturn(validEmployeeId); 
		when(employeeRepository.getId("egeis_employee", "gpfNo", "123932", "1"))
        .thenReturn(0L);
		when(employeeRepository.getId("egeis_employee", "passportNo", "ASD234234", "1"))
        .thenReturn(102L);
		when(employeeRepository.getId("egeis_employee", "code", "00100", "1"))
		.thenReturn(101L);
		when(employeeRepository.getListOfIds(any(), any(), anyString()))
		.thenReturn(entityIds);
		
		// get employee from different files based on test case
		Employee employee = null;
		try {
			employee = getEmployee(employeeFileName);
		} catch(Exception e) {  
			e.printStackTrace();
			fail();
		}
		
		EmployeeResponse employeeResponse = new EmployeeResponse();
		BindException errors = new BindException(employeeResponse, "employeeResponse");
		ValidationUtils.invokeValidator(dataIntegrityValidatorForUpdate, employee, errors);	
		return errors;
	}
	
	private Employee getEmployee(String fileName) throws IOException {
		String empJson = new FileUtils().getFileContents(
				"org/egov/eis/web/validator/DataIntegrityValidatorForUpdate/" + fileName);
		return new ObjectMapper().readValue(empJson, Employee.class);
	}
}
