package org.egov.pgr.common.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.common.model.AuthenticatedUser;
import org.egov.pgr.common.model.Complainant;
import org.egov.pgr.common.model.UserType;
import org.egov.pgr.read.domain.model.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SevaRequestTest {

    private static final String CRN = "crn";
    private static final String TENANT_ID = "tenantId";

    @Test
    public void test_should_set_generated_crn_to_seva_request() {
        final Complaint complaint = getComplaint();
        final SevaRequest sevaRequest = getSevaRequest();

        sevaRequest.update(complaint);

        assertEquals(CRN, sevaRequest.getServiceRequest().getCrn());
    }

    private Complaint getComplaint() {
        final AuthenticatedUser user = getAuthenticatedUser();
        final Coordinates coordinates = new Coordinates(0d, 0d, TENANT_ID);
        final ComplaintLocation complaintLocation = ComplaintLocation.builder().coordinates(coordinates)
                .crossHierarchyId("id").build();

        return Complaint.builder().tenantId(TENANT_ID).authenticatedUser(user)
                .complainant(Complainant.builder().build()).crn(CRN).complaintLocation(complaintLocation)
                .complaintType(new ComplaintType(null, null, TENANT_ID)).build();
    }

    private AuthenticatedUser getAuthenticatedUser() {
        return AuthenticatedUser.builder()
                .id(1L)
                .type(UserType.CITIZEN)
                .build();
    }

    private SevaRequest getSevaRequest() {
        final RequestInfo requestInfo = new RequestInfo();
        final ServiceRequest serviceRequest = ServiceRequest.builder().build();
        return new SevaRequest(requestInfo, serviceRequest);
    }

}