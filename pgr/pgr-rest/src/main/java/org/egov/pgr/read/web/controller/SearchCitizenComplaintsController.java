package org.egov.pgr.read.web.controller;

import org.egov.pgr.common.contract.GetUserByIdResponse;
import org.egov.pgr.common.contract.ServiceRequest;
import org.egov.pgr.common.repository.UserRepository;
import org.egov.pgr.read.domain.model.Complaint;
import org.egov.pgr.read.domain.service.ComplaintService;
import org.egov.pgr.read.web.contract.RequestInfoBody;
import org.egov.pgr.read.web.contract.ResponseInfo;
import org.egov.pgr.read.web.contract.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = { "/searchcitizencomplaints" })
public class SearchCitizenComplaintsController {

    private UserRepository userRepository;
    private ComplaintService complaintService;

    @Autowired
    public SearchCitizenComplaintsController(UserRepository userRepository, ComplaintService complaintService) {
        this.userRepository = userRepository;
        this.complaintService = complaintService;
    }

    @PostMapping
    @ResponseBody
    public ServiceResponse getComplaints(@RequestParam(value = "tenantId", required = true) final String tenantId,
            @RequestParam(value = "userId", required = true) Long userId, @RequestBody RequestInfoBody requestInfo) {
        List<Complaint> complaints = new ArrayList<>();
        if (tenantId != null && !tenantId.isEmpty()) {
            GetUserByIdResponse user = userRepository.findUserByIdAndTenantId(userId,tenantId);
            if (!user.getUser().isEmpty() && user.getUser().get(0).getType().equalsIgnoreCase("CITIZEN")) {
                complaints = complaintService.getAllModifiedCitizenComplaints(userId,tenantId);
            }
        }
        return createResponse(complaints);
    }

    private ServiceResponse createResponse(List<Complaint> complaints) {
        final List<ServiceRequest> serviceRequests = complaints.stream().map(ServiceRequest::new)
                .collect(Collectors.toList());
        return new ServiceResponse(new ResponseInfo("", "", "", "", "", "Successful response"), serviceRequests);
    }
}
