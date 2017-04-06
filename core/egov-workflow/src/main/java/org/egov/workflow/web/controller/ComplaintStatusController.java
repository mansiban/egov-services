package org.egov.workflow.web.controller;

import org.egov.workflow.domain.service.ComplaintStatusService;
import org.egov.workflow.web.contract.ComplaintStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class ComplaintStatusController {
    @Autowired
    private ComplaintStatusService complaintStatusService;

    @PostMapping("/_statuses")
    public List<ComplaintStatus> getAllStatus(@RequestParam final String tenantId) {
        return complaintStatusService.findAll()
                .stream()
                .map(ComplaintStatus::new)
                .collect(Collectors.toList());
    }

}