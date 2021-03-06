/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.eis.service;

import java.util.List;

import org.egov.eis.broker.LeaveAllotmentProducer;
import org.egov.eis.model.LeaveAllotment;
import org.egov.eis.repository.LeaveAllotmentRepository;
import org.egov.eis.web.contract.LeaveAllotmentGetRequest;
import org.egov.eis.web.contract.LeaveAllotmentRequest;
import org.egov.eis.web.contract.LeaveAllotmentResponse;
import org.egov.eis.web.contract.RequestInfo;
import org.egov.eis.web.contract.ResponseInfo;
import org.egov.eis.web.contract.factory.ResponseInfoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LeaveAllotmentService {

	public static final Logger LOGGER = LoggerFactory.getLogger(LeaveAllotmentService.class);

	@Autowired
	private LeaveAllotmentRepository leaveAllotmentRepository;

	@Value("${kafka.topics.leaveallotment.create.name}")
	private String leaveAllotmentCreateTopic;

	@Value("${kafka.topics.leaveallotment.create.key}")
	private String leaveAllotmentCreateKey;

	@Value("${kafka.topics.leaveallotment.update.name}")
	private String leaveAllotmentUpdateTopic;

	@Value("${kafka.topics.leaveallotment.update.key}")
	private String leaveAllotmentUpdateKey;

	@Autowired
	private LeaveAllotmentProducer leaveAllotmentProducer;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public List<LeaveAllotment> getLeaveAllotments(LeaveAllotmentGetRequest leaveAllotmentGetRequest) {
		return leaveAllotmentRepository.findForCriteria(leaveAllotmentGetRequest);
	}

	public ResponseEntity<?> createLeaveAllotment(LeaveAllotmentRequest leaveAllotmentRequest) {
		List<LeaveAllotment> leaveAllotment = leaveAllotmentRequest.getLeaveAllotment();
		String leaveAllotmentRequestJson = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			leaveAllotmentRequestJson = mapper.writeValueAsString(leaveAllotmentRequest);
			LOGGER.info("leaveAllotmentRequestJson::" + leaveAllotmentRequestJson);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while converting Employee to JSON", e);
			e.printStackTrace();
		}
		try {
			leaveAllotmentProducer.sendMessage(leaveAllotmentCreateTopic, leaveAllotmentCreateKey,
					leaveAllotmentRequestJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getSuccessResponseForCreate(leaveAllotment, leaveAllotmentRequest.getRequestInfo());
	}

	public ResponseEntity<?> updateLeaveAllotment(LeaveAllotmentRequest leaveAllotmentRequest) {
		List<LeaveAllotment> leaveAllotment = leaveAllotmentRequest.getLeaveAllotment();
		String leaveAllotmentRequestJson = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			leaveAllotmentRequestJson = mapper.writeValueAsString(leaveAllotmentRequest);
			LOGGER.info("leaveAllotmentRequestJson::" + leaveAllotmentRequestJson);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while converting Employee to JSON", e);
			e.printStackTrace();
		}
		try {
			leaveAllotmentProducer.sendMessage(leaveAllotmentUpdateTopic, leaveAllotmentUpdateKey,
					leaveAllotmentRequestJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getSuccessResponseForCreate(leaveAllotment, leaveAllotmentRequest.getRequestInfo());
	}

	/**
	 * Populate LeaveAllotmentResponse object & returns ResponseEntity of type
	 * LeaveAllotmentResponse containing ResponseInfo & array of LeaveAllotment
	 * objects
	 * 
	 * @param leaveAllotment
	 * @param requestInfo
	 * @param headers
	 * @return ResponseEntity<?>
	 */
	public ResponseEntity<?> getSuccessResponseForCreate(List<LeaveAllotment> leaveAllotment, RequestInfo requestInfo) {
		LeaveAllotmentResponse leaveAllotmentResponse = new LeaveAllotmentResponse();
		leaveAllotmentResponse.getLeaveAllotment().addAll(leaveAllotment);

		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		responseInfo.setStatus(HttpStatus.OK.toString());
		leaveAllotmentResponse.setResponseInfo(responseInfo);
		return new ResponseEntity<LeaveAllotmentResponse>(leaveAllotmentResponse, HttpStatus.OK);
	}

	public void create(LeaveAllotmentRequest leaveAllotmentRequest) {
		leaveAllotmentRepository.create(leaveAllotmentRequest);
	}

	public void update(LeaveAllotmentRequest leaveAllotmentRequest) {
		leaveAllotmentRepository.update(leaveAllotmentRequest);
	}

}