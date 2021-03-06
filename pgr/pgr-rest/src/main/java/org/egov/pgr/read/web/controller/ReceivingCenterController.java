package org.egov.pgr.read.web.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.egov.pgr.read.domain.service.ReceivingCenterService;
import org.egov.pgr.read.web.contract.ReceivingCenterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receivingcenter")
public class ReceivingCenterController {

	@Autowired
	private ReceivingCenterService receivingCenterService;

	@PostMapping("/_search")
	public ReceivingCenterResponse getAllReceivingCenters(
			@RequestParam(value = "tenantId", defaultValue = "default") String tenantId,
			@RequestParam(value = "id", required = false) Long id) {
		List<org.egov.pgr.common.model.ReceivingCenter> receivingCenters;
        org.egov.pgr.common.model.ReceivingCenter receivingCenter;
		if (id == null) {
			receivingCenters = receivingCenterService.getAllReceivingCenters(tenantId);
			List<org.egov.pgr.read.web.contract.ReceivingCenter> receiveCenters = receivingCenters.stream()
					.map(org.egov.pgr.read.web.contract.ReceivingCenter::new).collect(Collectors.toList());
			return new ReceivingCenterResponse(null, receiveCenters);
		} else {
			receivingCenter = receivingCenterService.getReceivingCenterById(tenantId, id);
			org.egov.pgr.read.web.contract.ReceivingCenter receiveCenter = new org.egov.pgr.read.web.contract.ReceivingCenter(
					receivingCenter);

			return new ReceivingCenterResponse(null, Collections.singletonList(receiveCenter));

		}
}
}
