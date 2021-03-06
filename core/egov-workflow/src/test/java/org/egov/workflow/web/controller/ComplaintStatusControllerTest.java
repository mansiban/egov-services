package org.egov.workflow.web.controller;


import org.egov.workflow.Resources;
import org.egov.workflow.domain.exception.InvalidComplaintStatusException;
import org.egov.workflow.domain.exception.InvalidComplaintStatusSearchException;
import org.egov.workflow.domain.model.ComplaintStatus;
import org.egov.workflow.domain.model.ComplaintStatusSearchCriteria;
import org.egov.workflow.domain.service.ComplaintStatusService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ComplaintStatusController.class)
public class ComplaintStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintStatusService complaintStatusService;

    Resources resources = new Resources();

    @Test
    public void findAllStatusTest() throws Exception {
        List<ComplaintStatus> complaintStatuses = Collections.singletonList(
                new ComplaintStatus(1L, "REGISTERED")
        );

        when(complaintStatusService.findAll()).thenReturn(complaintStatuses);

        mockMvc.perform(
                    post("/statuses/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resources.getFileContents("complaintStatusRequest.json"))
                )
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(new Resources().getFileContents("complaintStatusResponse.json")));
    }

    @Test
    public void testComplaintStatusMappingService() throws Exception {
        String status = "REGISTERED";
        List<ComplaintStatus> complaintStatuses = Collections.singletonList(
                new ComplaintStatus(1L, "REGISTERED")
        );

        ComplaintStatusSearchCriteria complaintStatusSearchCriteria =
                new ComplaintStatusSearchCriteria(status, Arrays.asList(1L, 2L));
        when(complaintStatusService.getNextStatuses(complaintStatusSearchCriteria)).thenReturn(complaintStatuses);

        mockMvc.perform(
                    post("/nextstatuses/_search")
                        .param("currentStatus", status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resources.getFileContents("complaintStatusRequest.json"))
                )
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(resources.getFileContents("complaintStatusResponse.json")));
    }

    @Test
    public void should_return_400_when_request_fields_are_invalid() throws Exception {
        InvalidComplaintStatusSearchException exception =
                new InvalidComplaintStatusSearchException(
                        new ComplaintStatusSearchCriteria("", Collections.emptyList())
                );
        when(complaintStatusService.getNextStatuses(any(ComplaintStatusSearchCriteria.class))).thenThrow(exception);

        mockMvc.perform(
                    post("/nextstatuses/_search")
                        .param("currentStatus", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resources.getFileContents("complaintStatusRequest.json"))
                )
                .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(resources.getFileContents("getNextStatusErrorResponseForFieldValidationErrors.json")));
    }

    @Test
    public void should_return_400_when_current_status_does_not_exist() throws Exception {
        final String CURRENT_STATUS = "SLEEPING";
        ComplaintStatus complaintStatus = new ComplaintStatus(0L, CURRENT_STATUS);
        InvalidComplaintStatusException exception = new InvalidComplaintStatusException(complaintStatus);
        when(complaintStatusService.getNextStatuses(any(ComplaintStatusSearchCriteria.class))).thenThrow(exception);

        mockMvc.perform(
                    post("/nextstatuses/_search")
                            .param("currentStatus", CURRENT_STATUS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(resources.getFileContents("complaintStatusRequest.json"))
                )
                .andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(resources.getFileContents("getNextStatusErrorResponseForInvalidCurrentStatus.json")));

    }
}