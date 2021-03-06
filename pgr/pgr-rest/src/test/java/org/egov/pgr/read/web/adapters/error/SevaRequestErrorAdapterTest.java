package org.egov.pgr.read.web.adapters.error;


import org.egov.pgr.read.domain.model.Complaint;
import org.egov.pgr.read.web.contract.ErrorField;
import org.egov.pgr.read.web.contract.ErrorResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SevaRequestErrorAdapterTest {

    @Mock
    private Complaint complaint;

    @InjectMocks
    private SevaRequestErrorAdapter errorAdapter;

    @Test
    public void testShouldSetErrorsWhenLocationDetailsAreNotPresentForCreateRequest() {
        when(complaint.isLocationAbsent()).thenReturn(true);
        when(complaint.isRawLocationAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(3, errorFields.size());
        assertEquals("pgr.0001", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.lat", errorFields.get(0).getField());
        assertEquals("latitude/longitude or cross hierarcy id is required", errorFields.get(0).getMessage());
        assertEquals("pgr.0002", errorFields.get(1).getCode());
        assertEquals("ServiceRequest.lng", errorFields.get(1).getField());
        assertEquals("latitude/longitude or cross hierarcy id is required", errorFields.get(1).getMessage());
        assertEquals("pgr.0003", errorFields.get(2).getCode());
        assertEquals("ServiceRequest.addressId", errorFields.get(2).getField());
        assertEquals("latitude/longitude or cross hierarcy id is required", errorFields.get(2).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenLocationIdIsNotPresentForAnUpdateRequest() {
        when(complaint.isLocationAbsent()).thenReturn(true);
        when(complaint.isLocationIdAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0004", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.values.locationId", errorFields.get(0).getField());
        assertEquals("Location id is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenFirstNameIsNotPresent() {
        when(complaint.isComplainantAbsent()).thenReturn(true);
        when(complaint.isComplainantFirstNameAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0005", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.firstName", errorFields.get(0).getField());
        assertEquals("First name is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenMobileNumberIsNotPresent() {
        when(complaint.isComplainantAbsent()).thenReturn(true);
        when(complaint.isComplainantPhoneAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0006", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.phone", errorFields.get(0).getField());
        assertEquals("Phone is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenReceivingModeIsNotPresent() {
        when(complaint.isReceivingModeAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0009", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.values.receivingMode", errorFields.get(0).getField());
        assertEquals("Receiving mode is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenReceivingCenterIsNotPresent() {
        when(complaint.isReceivingCenterAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0010", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.values.receivingCenter", errorFields.get(0).getField());
        assertEquals("Receiving center is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenTenantIdIsNotPresent() {
        when(complaint.isTenantIdAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0011", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.tenantId", errorFields.get(0).getField());
        assertEquals("Tenant id is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenCompaintTypeCodeIsNotPresent() {
        when(complaint.isComplaintTypeAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0012", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.serviceCode", errorFields.get(0).getField());
        assertEquals("Service code is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenDescriptionIsNotPresent() {
        when(complaint.isDescriptionAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0013", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.description", errorFields.get(0).getField());
        assertEquals("Description is required", errorFields.get(0).getMessage());
    }

    @Test
    public void testShouldSetErrorWhenCrnIsNotPresent() {
        when(complaint.isCrnAbsent()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0014", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.serviceRequestId", errorFields.get(0).getField());
        assertEquals("Service request id is required", errorFields.get(0).getMessage());
    }
    
    @Test
    public void testShouldSetErrorWhenDescriptionIsLess() {
        when(complaint.descriptionLength()).thenReturn(true);

        final ErrorResponse errorResponse = errorAdapter.adapt(complaint);

        final List<ErrorField> errorFields = errorResponse.getErrorFields();
        assertNotNull(errorFields);
        assertEquals(1, errorFields.size());
        assertEquals("pgr.0015", errorFields.get(0).getCode());
        assertEquals("ServiceRequest.description", errorFields.get(0).getField());
        assertEquals("Description must have minimum 10 characters", errorFields.get(0).getMessage());
    }

}