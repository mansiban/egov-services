package org.egov.web.indexer.adaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.egov.web.indexer.config.IndexerPropertiesManager;
import org.egov.web.indexer.contract.Assignment;
import org.egov.web.indexer.contract.Boundary;
import org.egov.web.indexer.contract.City;
import org.egov.web.indexer.contract.ComplaintType;
import org.egov.web.indexer.contract.ServiceRequest;
import org.egov.web.indexer.models.ComplaintIndex;
import org.egov.web.indexer.service.AssignmentService;
import org.egov.web.indexer.service.BoundaryService;
import org.egov.web.indexer.service.CityService;
import org.egov.web.indexer.service.ComplaintTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComplaintAdapterTest {

	@Mock
	private IndexerPropertiesManager propertiesManager;

	@Mock
	private BoundaryService boundaryService;

	@Mock
	private ComplaintTypeService complaintTypeService;

	@Mock
	private CityService cityService;

	@Mock
	private AssignmentService assignmentService;

	private ComplaintAdapter complaintAdapter;

	@Before
	public void before() {
		complaintAdapter = new ComplaintAdapter(propertiesManager, boundaryService, complaintTypeService, cityService,
				assignmentService);

	}

	@Test
	public void test_create_index() {
		ServiceRequest serviceRequest = setUpServiceRequest();
		ComplaintIndex complaintIndex = complaintAdapter.indexOnCreate(serviceRequest);
		assertNotNull(complaintIndex);
	}

	private ServiceRequest setUpServiceRequest() {
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setCreatedDate("2016-10-20");
		serviceRequest.setEscalationDate("2016-10-21");
		serviceRequest.setFirstName("abc");
		serviceRequest.setLastName("xyz");
		serviceRequest.setComplaintTypeCode("AOS");
		Map<String, String> values = serviceRequest.getValues();
		values.put("receivingMode", "");
		values.put("userType", "");
		values.put("locationId", "1L");
		values.put("childLocationId", "2L");
		values.put("tenantId", "1L");
		values.put("assignmentId", "1L");
		final ComplaintType expectedComplaintType = getComplaintType();
		final Boundary expectedBoundary = getExpectedBoundary();
		final City expectedCityContent = getExpectedCityContent();
		final Assignment expectedAssignment = getExpectedAssignment();
		when(complaintTypeService.fetchComplaintTypeByCode("AOS")).thenReturn(expectedComplaintType);
		when(boundaryService.fetchBoundaryById(Long.getLong(values.get("locationId")))).thenReturn(expectedBoundary);
		when(cityService.fetchCityById(Long.getLong(values.get("tenantId")))).thenReturn(expectedCityContent);
		when(assignmentService.fetchAssignmentById(Long.getLong(values.get("assignmentId"))))
				.thenReturn(expectedAssignment);
		return serviceRequest;
	}

	private ComplaintType getComplaintType() {
		ComplaintType complaintType = new ComplaintType();
		complaintType.setSlaHours(10);
		return complaintType;
	}

	private Boundary getExpectedBoundary() {
		Boundary boundary = new Boundary();
		boundary.setName("Kurnool");
		boundary.setLatitude(14.46F);
		boundary.setLongitude(78.82F);
		boundary.setBoundaryNum(3L);
		return boundary;
	}

	private City getExpectedCityContent() {
		City city = new City();
		city.setName("Kurnool");
		city.setCode("KC");
		city.setDistrictCode("KC01");
		city.setDistrictName("Kurnool District");
		city.setGrade("Grade A");
		city.setDomainURL("http://localhost");
		city.setRegionName("Kurnool Region");
		return city;
	}

	private Assignment getExpectedAssignment() {
		Assignment assignment = new Assignment();
		assignment.setId(1L);
		assignment.setName("Raman");
		assignment.setMobileNumber("1234567890");
		assignment.setDepartmentName("Revenu Dept");
		assignment.setDepartmentCode("RD");
		assignment.setDesignationName("Asst.Eng.");
		return assignment;
	}

}