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

import org.egov.eis.broker.LeaveOpeningBalanceProducer;
import org.egov.eis.model.LeaveOpeningBalance;
import org.egov.eis.repository.LeaveOpeningBalanceRepository;
import org.egov.eis.web.contract.LeaveOpeningBalanceGetRequest;
import org.egov.eis.web.contract.LeaveOpeningBalanceRequest;
import org.egov.eis.web.contract.LeaveOpeningBalanceResponse;
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
public class LeaveOpeningBalanceService {

	public static final Logger LOGGER = LoggerFactory.getLogger(LeaveOpeningBalanceService.class);

	@Value("${kafka.topics.leaveopeningbalance.create.name}")
	private String leaveOpeningBalanceCreateTopic;

	@Value("${kafka.topics.leaveopeningbalance.create.key}")
	private String leaveOpeningBalanceCreateKey;

	@Value("${kafka.topics.leaveopeningbalance.update.name}")
	private String leaveOpeningBalanceUpdateTopic;

	@Value("${kafka.topics.leaveopeningbalance.update.key}")
	private String leaveOpeningBalanceUpdateKey;

	@Autowired
	private LeaveOpeningBalanceProducer leaveOpeningBalanceProducer;

	@Autowired
	private LeaveOpeningBalanceRepository leaveOpeningBalanceRepository;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public List<LeaveOpeningBalance> getLeaveOpeningBalances(
			LeaveOpeningBalanceGetRequest leaveOpeningBalanceGetRequest) {
		return leaveOpeningBalanceRepository.findForCriteria(leaveOpeningBalanceGetRequest);
	}

	public ResponseEntity<?> createLeaveOpeningBalance(LeaveOpeningBalanceRequest leaveOpeningBalanceRequest) {
		List<LeaveOpeningBalance> leaveOpeningBalance = leaveOpeningBalanceRequest.getLeaveOpeningBalance();
		String leaveOpeningBalanceRequestJson = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			leaveOpeningBalanceRequestJson = mapper.writeValueAsString(leaveOpeningBalanceRequest);
			LOGGER.info("leaveOpeningBalanceRequestJson::" + leaveOpeningBalanceRequestJson);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while converting Employee to JSON", e);
			e.printStackTrace();
		}
		try {
			leaveOpeningBalanceProducer.sendMessage(leaveOpeningBalanceCreateTopic, leaveOpeningBalanceCreateKey,
					leaveOpeningBalanceRequestJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getSuccessResponseForCreate(leaveOpeningBalance, leaveOpeningBalanceRequest.getRequestInfo());
	}

	public ResponseEntity<?> updateLeaveOpeningBalance(LeaveOpeningBalanceRequest leaveOpeningBalanceRequest) {
		List<LeaveOpeningBalance> leaveOpeningBalance = leaveOpeningBalanceRequest.getLeaveOpeningBalance();
		String leaveOpeningBalanceRequestJson = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			leaveOpeningBalanceRequestJson = mapper.writeValueAsString(leaveOpeningBalanceRequest);
			LOGGER.info("leaveOpeningBalanceRequestJson::" + leaveOpeningBalanceRequestJson);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error while converting Employee to JSON", e);
			e.printStackTrace();
		}
		try {
			leaveOpeningBalanceProducer.sendMessage(leaveOpeningBalanceUpdateTopic, leaveOpeningBalanceUpdateKey,
					leaveOpeningBalanceRequestJson);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return getSuccessResponseForCreate(leaveOpeningBalance, leaveOpeningBalanceRequest.getRequestInfo());
	}

	/**
	 * Populate LeaveOpeningBalanceResponse object & returns ResponseEntity of
	 * type LeaveOpeningBalanceResponse containing ResponseInfo & array of
	 * LeaveOpeningBalance objects
	 * 
	 * @param leaveOpeningBalance
	 * @param requestInfo
	 * @param headers
	 * @return ResponseEntity<?>
	 */
	public ResponseEntity<?> getSuccessResponseForCreate(List<LeaveOpeningBalance> leaveOpeningBalance,
			RequestInfo requestInfo) {
		LeaveOpeningBalanceResponse leaveOpeningBalanceResponse = new LeaveOpeningBalanceResponse();
		leaveOpeningBalanceResponse.getLeaveOpeningBalance().addAll(leaveOpeningBalance);

		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		responseInfo.setStatus(HttpStatus.OK.toString());
		leaveOpeningBalanceResponse.setResponseInfo(responseInfo);
		return new ResponseEntity<LeaveOpeningBalanceResponse>(leaveOpeningBalanceResponse, HttpStatus.OK);
	}

	public void create(LeaveOpeningBalanceRequest leaveOpeningBalanceRequest) {
		leaveOpeningBalanceRepository.create(leaveOpeningBalanceRequest);
	}

	public void update(LeaveOpeningBalanceRequest leaveOpeningBalanceRequest) {
		leaveOpeningBalanceRepository.update(leaveOpeningBalanceRequest);
	}
}