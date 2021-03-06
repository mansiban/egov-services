package org.egov.workflow.web.controller;

import org.egov.common.contract.request.Role;
import org.egov.workflow.domain.model.ComplaintStatusSearchCriteria;
import org.egov.workflow.domain.service.ComplaintStatusService;
import org.egov.workflow.web.contract.ComplaintStatus;
import org.egov.workflow.web.contract.ComplaintStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class ComplaintStatusController {
    @Autowired
    private ComplaintStatusService complaintStatusService;

    @PostMapping("/statuses/_search")
    public List<ComplaintStatus> getAllStatus(@RequestBody final ComplaintStatusRequest request) {
        return complaintStatusService.findAll()
                .stream()
                .map(ComplaintStatus::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/nextstatuses/_search")
    public List<ComplaintStatus> getNextStatuses(@RequestBody final ComplaintStatusRequest request,
                                                 @RequestParam final String currentStatus) {
        List<Long> roles = request.getRequestInfo().getUserInfo().getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        ComplaintStatusSearchCriteria complaintStatusSearchCriteria =
                new ComplaintStatusSearchCriteria(currentStatus, roles);

        return complaintStatusService.getNextStatuses(complaintStatusSearchCriteria).stream()
                .map(ComplaintStatus::new).collect(Collectors.toList());
    }

}
