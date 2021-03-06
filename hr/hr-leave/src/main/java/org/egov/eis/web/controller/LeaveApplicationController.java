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

package org.egov.eis.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.eis.model.LeaveApplication;
import org.egov.eis.service.LeaveApplicationService;
import org.egov.eis.web.contract.LeaveApplicationGetRequest;
import org.egov.eis.web.contract.LeaveApplicationRequest;
import org.egov.eis.web.contract.LeaveApplicationResponse;
import org.egov.eis.web.contract.LeaveApplicationSingleRequest;
import org.egov.eis.web.contract.RequestInfo;
import org.egov.eis.web.contract.RequestInfoWrapper;
import org.egov.eis.web.contract.ResponseInfo;
import org.egov.eis.web.contract.factory.ResponseInfoFactory;
import org.egov.eis.web.errorhandlers.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaveapplications")
public class LeaveApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveApplicationController.class);

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @Autowired
    private ErrorHandler errorHandler;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping("_search")
    @ResponseBody
    public ResponseEntity<?> search(@ModelAttribute @Valid final LeaveApplicationGetRequest leaveApplicationGetRequest,
            final BindingResult modelAttributeBindingResult, @RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
            final BindingResult requestBodyBindingResult) {
        final RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();

        // validate input params
        if (modelAttributeBindingResult.hasErrors())
            return errorHandler.getErrorResponseEntityForMissingParameters(modelAttributeBindingResult, requestInfo);

        // validate input params
        if (requestBodyBindingResult.hasErrors())
            return errorHandler.getErrorResponseEntityForMissingRequestInfo(requestBodyBindingResult, requestInfo);

        // Call service
        List<LeaveApplication> leaveApplicationsList = null;
        try {
            leaveApplicationsList = leaveApplicationService.getLeaveApplications(leaveApplicationGetRequest);
        } catch (final Exception exception) {
            logger.error("Error while processing request " + leaveApplicationGetRequest, exception);
            return errorHandler.getResponseEntityForUnexpectedErrors(requestInfo);
        }

        return getSuccessResponse(leaveApplicationsList, requestInfo);
    }

    /**
     * Maps Post Requests for _create & returns ResponseEntity of either LeaveApplicationResponse type or ErrorResponse type
     *
     * @param LeaveApplicationRequest
     * @param BindingResult
     * @return ResponseEntity<?>
     */

    @PostMapping("_create")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody final LeaveApplicationRequest leaveApplicationRequest,
            final BindingResult bindingResult) {

        final ResponseEntity<?> errorResponseEntity = validateLeaveApplicationRequests(leaveApplicationRequest,
                bindingResult);
        if (errorResponseEntity != null)
            return errorResponseEntity;

        final List<LeaveApplication> applications = leaveApplicationService.createLeaveApplication(leaveApplicationRequest);
        return getSuccessResponse(applications, leaveApplicationRequest.getRequestInfo());
    }

    @PostMapping("/{leaveApplicationId}/_update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestBody final LeaveApplicationSingleRequest leaveApplicationRequest,
            @PathVariable(required = true, name = "leaveApplicationId") final Long leaveApplicationId,
            final BindingResult bindingResult) {

        final ResponseEntity<?> errorResponseEntity = validateLeaveApplicationRequest(leaveApplicationRequest,
                bindingResult);
        if (errorResponseEntity != null)
            return errorResponseEntity;

        leaveApplicationRequest.getLeaveApplication().setId(leaveApplicationId);

        final LeaveApplication leaveApplication = leaveApplicationService.updateLeaveApplication(leaveApplicationRequest);
        final List<LeaveApplication> applications = new ArrayList<>();
        applications.add(leaveApplication);
        return getSuccessResponse(applications, leaveApplicationRequest.getRequestInfo());
    }

    /**
     * Populate Response object and return leaveApplicationsList
     *
     * @param leaveApplicationsList
     * @return
     */
    private ResponseEntity<?> getSuccessResponse(final List<LeaveApplication> leaveApplicationsList,
            final RequestInfo requestInfo) {
        final LeaveApplicationResponse leaveApplicationRes = new LeaveApplicationResponse();
        leaveApplicationRes.setLeaveApplication(leaveApplicationsList);
        final ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
        responseInfo.setStatus(HttpStatus.OK.toString());
        leaveApplicationRes.setResponseInfo(responseInfo);
        return new ResponseEntity<LeaveApplicationResponse>(leaveApplicationRes, HttpStatus.OK);

    }

    /**
     * Validate LeaveApplicationSingleRequest object & returns ErrorResponseEntity if there are any errors or else returns null
     *
     * @param LeaveApplicationSingleRequest
     * @param bindingResult
     * @return ResponseEntity<?>
     */
    private ResponseEntity<?> validateLeaveApplicationRequest(final LeaveApplicationSingleRequest leaveApplicationRequest,
            final BindingResult bindingResult) {
        // validate input params that can be handled by annotations
        if (bindingResult.hasErrors())
            return errorHandler.getErrorResponseEntityForBindingErrors(bindingResult,
                    leaveApplicationRequest.getRequestInfo());
        return null;
    }
    
    /**
     * Validate LeaveApplicationRequests object & returns ErrorResponseEntity if there are any errors or else returns null
     *
     * @param LeaveApplicationRequest
     * @param bindingResult
     * @return ResponseEntity<?>
     */
    private ResponseEntity<?> validateLeaveApplicationRequests(final LeaveApplicationRequest leaveApplicationRequest,
            final BindingResult bindingResult) {
        // validate input params that can be handled by annotations
        if (bindingResult.hasErrors())
            return errorHandler.getErrorResponseEntityForBindingErrors(bindingResult,
                    leaveApplicationRequest.getRequestInfo());
        return null;
    }

}
