package org.egov.pgr.employee.enrichment.model;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pgr.employee.enrichment.json.ObjectMapperFactory;
import org.egov.pgr.employee.enrichment.repository.contract.Attribute;
import org.egov.pgr.employee.enrichment.repository.contract.WorkflowRequest;
import org.egov.pgr.employee.enrichment.repository.contract.WorkflowResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.egov.pgr.employee.enrichment.repository.contract.WorkflowResponse.STATE_ID;

public class SevaRequest {

    public final static String SERVICE_REQUEST = "serviceRequest";
    public final static String REQUEST_INFO = "RequestInfo";
    public static final String VALUES_ASSIGNEE_ID = "assignmentId";
    public static final String VALUES_STATE_ID = "stateId";
    private static final String VALUES_DESIGNATION_ID = "designationId";
    private static final String VALUES_DEPARTMENT_ID = "departmentId";
    public static final String VALUES = "values";
    public static final String VALUES_COMLAINT_TYPE_CODE = "complaintTypeCode";
    public static final String BOUNDARY_ID = "boundaryId";
    public static final String STATE_DETAILS = "stateDetails";
    private static final String WORKFLOW_TYPE = "Complaint";
    public static final String STATUS = "status";
    public static final String SERVICE_CODE = "serviceCode";
    public static final String VALUES_LOCATION_ID = "locationId";
    public static final String VALUES_APPROVAL_COMMENT = "approvalComments";
    public static final String USER_ROLE = "userRole";
    private static final String SERVICE_REQUEST_ID = "serviceRequestId";
    public static final String DEPARTMENT_ID = "departmentId";
    private static final String COMPLAINT_CRN = "crn";
    private static final String EXPECTED_DATETIME = "expectedDatetime";
    private static final String TENANT_ID = "tenantId";
    private static final String ESCALATION_HOURS = "escalationHours";

    private HashMap<String, Object> sevaRequestMap;

    public SevaRequest(HashMap<String, Object> sevaRequestMap) {
        this.sevaRequestMap = sevaRequestMap;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> getValues() {
        HashMap<String, Object> serviceRequest = getServiceRequest();
        return (HashMap<String, String>) serviceRequest.get(VALUES);
    }

    public Long getAssignee() {
        return Long.valueOf(getValues().get(VALUES_ASSIGNEE_ID));
    }

    private void setAssignee(String assignee) {
        getValues().put(VALUES_ASSIGNEE_ID, assignee);
    }

    private void setStateId(String stateId) {
        getValues().put(VALUES_STATE_ID, stateId);
    }

    public void setEscalationHours(String escalationHours) {
        getValues().put(ESCALATION_HOURS, escalationHours);
    }

    public RequestInfo getRequestInfo() {
        return ObjectMapperFactory.create().convertValue(sevaRequestMap.get(REQUEST_INFO), RequestInfo.class);
    }

    public HashMap<String, Object> getRequestMap() {
        return sevaRequestMap;
    }

    public String getComplaintTypeCode() {
        return (String) this.getServiceRequest().get(SERVICE_CODE);
    }

    public WorkflowRequest getWorkFlowRequest() {
        HashMap<String, Object> serviceRequest = getServiceRequest();
        RequestInfo requestInfo = getRequestInfo();
        HashMap<String, String> values = getValues();
        String complaintType = (String) serviceRequest.get(SERVICE_CODE);
        String crn = (String) serviceRequest.get(SERVICE_REQUEST_ID);
        Map<String, Attribute> valuesToSet = getWorkFlowRequestValues(values, complaintType);
        valuesToSet.put(COMPLAINT_CRN, Attribute.asStringAttr(COMPLAINT_CRN, crn));
        WorkflowRequest.WorkflowRequestBuilder workflowRequestBuilder = WorkflowRequest.builder()
            .assignee(getCurrentAssignee(values))
            .action(WorkflowRequest.Action.forComplaintStatus(values.get(STATUS)))
            .requestInfo(requestInfo)
            .values(valuesToSet)
            .status(values.get(STATUS))
            .type(WORKFLOW_TYPE)
            .businessKey(WORKFLOW_TYPE)
            .tenantId(getTenantId())
            .crn(crn);

        return workflowRequestBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Object> getServiceRequest() {
        return (HashMap<String, Object>) sevaRequestMap.get(SERVICE_REQUEST);
    }

    public void update(WorkflowResponse workflowResponse) {
        setAssignee(workflowResponse.getAssignee());
        setStateId(workflowResponse.getValueForKey(STATE_ID));
    }

    public void update(Position position) {
        setDesignation(position.getDesignationId());
        setDepartment(position.getDepartmentId());
    }

    public void setEscalationDate(Date date) {
        getServiceRequest().put(EXPECTED_DATETIME, date);
    }

    public String getTenantId() {
        return (String) getServiceRequest().get(TENANT_ID);
    }

    private Map<String, Attribute> getWorkFlowRequestValues(HashMap<String, String> values, String complaintType) {
        Map<String, Attribute> valuesToSet = new HashMap<>();
        valuesToSet.put(VALUES_COMLAINT_TYPE_CODE, Attribute.asStringAttr(VALUES_COMLAINT_TYPE_CODE, complaintType));
        valuesToSet.put(BOUNDARY_ID, Attribute.asStringAttr(BOUNDARY_ID, values.get(VALUES_LOCATION_ID)));
        valuesToSet.put(STATE_DETAILS, Attribute.asStringAttr(STATE_DETAILS, StringUtils.EMPTY));
        valuesToSet.put(USER_ROLE, Attribute.asStringAttr(USER_ROLE, getUserType()));
        valuesToSet.put(VALUES_STATE_ID, Attribute.asStringAttr(VALUES_STATE_ID, getCurrentStateId(values)));
        valuesToSet.put(VALUES_APPROVAL_COMMENT, Attribute.asStringAttr(VALUES_APPROVAL_COMMENT, values.get
            (VALUES_APPROVAL_COMMENT)));
        valuesToSet.put(DEPARTMENT_ID, Attribute.asStringAttr(DEPARTMENT_ID, values.get(DEPARTMENT_ID)));
        return valuesToSet;
    }

    private String getUserType() {
        final User userInfo = getRequestInfo().getUserInfo();
        return userInfo != null ? userInfo.getType() : null;
    }

    private String getCurrentStateId(HashMap<String, String> values) {
        return Objects.isNull(values.get(VALUES_STATE_ID)) ? null : values.get(VALUES_STATE_ID);
    }

    private Long getCurrentAssignee(HashMap<String, String> values) {
        final String assignee = values.get(VALUES_ASSIGNEE_ID);
        return assignee != null ? Long.valueOf(String.valueOf(assignee)) : null;
    }

    public boolean isCreate() {
        return this.getRequestInfo().getAction().equals("POST");
    }

    public void setDesignation(String designationId) {
        getValues().put(VALUES_DESIGNATION_ID, designationId);
    }

    public String getDesignation() {
        return getValues().get(VALUES_DESIGNATION_ID);
    }

    public void setDepartment(String departmentId) {
        getValues().put(VALUES_DEPARTMENT_ID, departmentId);
    }

}