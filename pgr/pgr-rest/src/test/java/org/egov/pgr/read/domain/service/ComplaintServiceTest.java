package org.egov.pgr.read.domain.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.common.contract.ServiceRequest;
import org.egov.pgr.common.contract.SevaRequest;
import org.egov.pgr.common.model.AuthenticatedUser;
import org.egov.pgr.common.model.Complainant;
import org.egov.pgr.common.model.UserType;
import org.egov.pgr.common.repository.ComplaintJpaRepository;
import org.egov.pgr.common.repository.UserRepository;
import org.egov.pgr.read.domain.model.*;
import org.egov.pgr.read.persistence.repository.ComplaintRepository;
import org.egov.pgr.read.web.contract.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComplaintServiceTest {

    private static final String CRN = "crn";
    private static final String TENANT_ID = "tenantId";
    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SevaNumberGeneratorService sevaNumberGeneratorService;

    @Mock
    private ComplaintJpaRepository complaintJpaRepository;

    @InjectMocks
    private ComplaintService complaintService;

    @Test
    public void testShouldValidateComplaintOnSave() {
        final Complaint complaint = mock(Complaint.class);
        when(complaint.getAuthenticatedUser()).thenReturn(getCitizen());
        final SevaRequest sevaRequest = getSevaRequest();
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.save(complaint, sevaRequest);

        verify(complaint, times(1)).validate();
    }

    @Test
    public void testShouldValidateComplaintOnUpdate() {
        final Complaint complaint = mock(Complaint.class);
        when(complaint.getAuthenticatedUser()).thenReturn(getCitizen());
        final SevaRequest sevaRequest = getSevaRequest();
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.update(complaint, sevaRequest);

        verify(complaint, times(1)).validate();
    }

    @Test
    public void testShouldUpdateSevaRequestWithDomainComplaintOnSave() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = mock(SevaRequest.class);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.save(complaint, sevaRequest);

        verify(sevaRequest).update(complaint);
    }

    @Test
    public void testShouldUpdateSevaRequestWithDomainComplaintOnUpdate() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = mock(SevaRequest.class);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.update(complaint, sevaRequest);

        verify(sevaRequest).update(complaint);
    }

    @Test
    public void testShouldSetGeneratedCrnToDomainComplaintOnSave() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = getSevaRequest();
        when(sevaNumberGeneratorService.generate()).thenReturn(CRN);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.save(complaint, sevaRequest);

        assertEquals(CRN, complaint.getCrn());
    }

    @Test
    public void test_should_set_anonymous_user_when_user_is_not_authenticated() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = getSevaRequest();
        sevaRequest.getRequestInfo().setUserInfo(null);
        when(sevaNumberGeneratorService.generate()).thenReturn(CRN);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());

        complaintService.save(complaint, sevaRequest);

        final org.egov.common.contract.request.User userInfo = sevaRequest.getRequestInfo().getUserInfo();
        assertNotNull(userInfo);
        assertEquals(Long.valueOf(1L), userInfo.getId());
        assertEquals("SYSTEM", userInfo.getType());
    }

    @Test
    public void test_should_not_manipulate_user_info_when_when_user_is_authenticated() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = getSevaRequest();
        final org.egov.common.contract.request.User user = org.egov.common.contract.request.User.builder()
            .type("CITIZEN")
            .id(2L)
            .build();
        sevaRequest.getRequestInfo().setUserInfo(user);
        when(sevaNumberGeneratorService.generate()).thenReturn(CRN);

        complaintService.save(complaint, sevaRequest);

        final org.egov.common.contract.request.User userInfo = sevaRequest.getRequestInfo().getUserInfo();
        assertNotNull(userInfo);
        assertEquals(Long.valueOf(2L), userInfo.getId());
        assertEquals("CITIZEN", userInfo.getType());
    }


    @Test
    public void testShouldPersistSevaRequestOnSave() {
        final Complaint complaint = getComplaint();
        final ServiceRequest serviceRequest = getServiceRequest();
        final SevaRequest sevaRequest = new SevaRequest(new RequestInfo(), serviceRequest);
        when(sevaNumberGeneratorService.generate()).thenReturn(CRN);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.save(complaint, sevaRequest);

        verify(complaintRepository).save(sevaRequest);
    }

    @Test
    public void testShouldPersistSevaRequestOnUpdate() {
        final Complaint complaint = getComplaint();
        final ServiceRequest serviceRequest = getServiceRequest();
        final SevaRequest sevaRequest = new SevaRequest(new RequestInfo(), serviceRequest);
        when(userRepository.getUserByUserName("anonymous","tenantId")).thenReturn(populateUser());
        complaintService.update(complaint, sevaRequest);

        verify(complaintRepository).update(sevaRequest);
    }

    @Test
    public void testShouldFindAllComplaintsBySearchCriteria() {
        final ComplaintSearchCriteria searchCriteria = ComplaintSearchCriteria.builder().build();
        final Complaint expectedComplaint = getComplaint();
        when(complaintRepository.findAll(searchCriteria)).thenReturn(Collections.singletonList(expectedComplaint));

        final List<Complaint> actualComplaints = complaintService.findAll(searchCriteria);

        assertEquals(1, actualComplaints.size());
        assertEquals(expectedComplaint, actualComplaints.get(0));
    }

    @Test
    public void testShouldUpdateLastAccessedTime() {
       complaintService.updateLastAccessedTime("crn", "tenantId");
        verify(complaintJpaRepository).updateLastAccessedTime(any(Date.class), eq("crn"), eq("tenantId"));
        }
    
    public void test_should_fetch_all_modified_citizen_complaints_by_user_id() {
        final Complaint expectedComplaint = getComplaint();
        when(complaintRepository.getAllModifiedComplaintsForCitizen(any(Long.class),any(String.class)))
            .thenReturn(Collections.singletonList(expectedComplaint));
        final List<Complaint> actualComplaints = complaintService.getAllModifiedCitizenComplaints(1L, "tenantId");
        assertEquals(1, actualComplaints.size());
        assertEquals(expectedComplaint, actualComplaints.get(0));
    }

    @Test
    public void test_should_fetch_empty_list_for_invalid_userid() {
        when(complaintRepository.getAllModifiedComplaintsForCitizen(any(Long.class),any(String.class)))
            .thenReturn(new ArrayList<>());
        final List<Complaint> actualComplaints = complaintService.getAllModifiedCitizenComplaints(1L, "tenantId");
        assertEquals(0, actualComplaints.size());
    }

    private Complaint getComplaint() {
        final Coordinates coordinates = new Coordinates(0d, 0d, "tenantId");
        final ComplaintLocation complaintLocation = new ComplaintLocation(coordinates, "id", null, "tenantId");
        final Complainant complainant = Complainant.builder()
            .userId("userId")
            .firstName("first name")
            .mobile("mobile number")
            .build();
        return Complaint.builder()
            .complainant(complainant)
            .authenticatedUser(getCitizen())
            .complaintLocation(complaintLocation)
            .tenantId(TENANT_ID)
            .description("description")
            .crn("crn")
            .lastAccessedTime(new Date())
            .department("2")
            .complaintType(new ComplaintType(null, "complaintCode", "tenantId"))
            .build();
    }

    private AuthenticatedUser getCitizen() {
        return AuthenticatedUser.builder().id(1L).type(UserType.CITIZEN).build();
    }

    private SevaRequest getSevaRequest() {
        final ServiceRequest serviceRequest = ServiceRequest.builder().tenantId("tenantId").build();
        return new SevaRequest(new RequestInfo(), serviceRequest);
    }

    private ServiceRequest getServiceRequest() {
        return ServiceRequest.builder().tenantId("tenantId").build();
    }

    private User populateUser() {
        return User.builder().id(1L).name("user").build();
    }

}