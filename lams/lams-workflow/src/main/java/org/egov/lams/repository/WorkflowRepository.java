package org.egov.lams.repository;

import org.egov.lams.config.PropertiesManager;
import org.egov.lams.contract.AgreementRequest;
import org.egov.lams.contract.ProcessInstanceRequest;
import org.egov.lams.contract.TaskRequest;
import org.egov.lams.contract.TaskResponse;
import org.egov.lams.contract.WorkflowRequestInfo;
import org.egov.lams.model.Agreement;
import org.egov.lams.model.Position;
import org.egov.lams.model.ProcessInstance;
import org.egov.lams.model.Task;
import org.egov.lams.model.WorkFlowDetails;
import org.egov.lams.producers.AgreementProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class WorkflowRepository {
	public static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRepository.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AgreementProducer agreementProducer;

	@Autowired
	private PropertiesManager propertiesManager;

	public ProcessInstance startWorkflow(AgreementRequest agreementRequest) {

		Agreement agreement = agreementRequest.getAgreement();
		WorkFlowDetails workFlowDetails = agreement.getWorkflowDetails();
		System.err.println(agreement);

		Position assignee = new Position();
		assignee.setId(workFlowDetails.getAssignee());

		ProcessInstanceRequest processInstanceRequest = new ProcessInstanceRequest();

		ProcessInstance processInstance = new ProcessInstance();
		processInstance.setBusinessKey(propertiesManager.getWorkflowServiceBusinessKey());
		processInstance.setType(propertiesManager.getWorkflowServiceBusinessKey());
		processInstance.setAssignee(assignee);
		processInstance.setComments("statrting workflow from consumer app");
		processInstanceRequest.setProcessInstance(processInstance);
		
		WorkflowRequestInfo workflowRequestInfo = new WorkflowRequestInfo();
		workflowRequestInfo.setTenantId(agreement.getTenantId());
		processInstanceRequest.setRequestInfo(new WorkflowRequestInfo());
		
		processInstance.setTenantId(agreement.getTenantId());
		// TODO put business key ina pp.propscomments

		String url = propertiesManager.getWorkflowServiceHostName()
					+ propertiesManager.getWorkflowServiceStartPath();
		LOGGER.info("string url of the workflow appp : "+url);

		ProcessInstance  processInstanceRes = null;
		try {
			processInstanceRes = restTemplate.postForObject(url, processInstanceRequest,ProcessInstance.class);
		} catch (Exception e) {
			LOGGER.info(e.toString());
			throw e;
		}
		//FIXME the response should be always processInstanceResponse
		return processInstanceRes;
	}

	public TaskResponse updateWorkflow(AgreementRequest agreementRequest) {

		Agreement agreement = agreementRequest.getAgreement();
		WorkFlowDetails workFlowDetails = agreement.getWorkflowDetails();

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setRequestInfo(agreementRequest.getRequestInfo());

		Task task = new Task();
		task.setAction(workFlowDetails.getAction());
		// FIXME business key get confirmed form workflow module
		task.setBusinessKey("agreement");
		task.setType("agreement");
		task.setId(agreement.getStateId());
		task.setStatus(workFlowDetails.getStatus());
		// task.setStatus(workFlowDetails.get); FIXME ask mani about status	// issue not updating

		Position assignee = new Position();
		assignee.setId(workFlowDetails.getAssignee());
		task.setAssignee(assignee);
		taskRequest.setTask(task);

		String url = propertiesManager.getWorkflowServiceHostName()
					+ propertiesManager.getWorkflowServiceTaskPAth() 
					+ "/"+agreement.getStateId()
					+ propertiesManager.getWorkflowServiceUpdatePath();

		TaskResponse taskResponse = null;
		try {
			taskResponse = restTemplate.postForObject(url, taskRequest, TaskResponse.class);
		} catch (Exception e) {
			LOGGER.info(e.toString());
			e.printStackTrace();
		}
			
		return taskResponse;
	}

	public void saveAgreement(AgreementRequest agreementRequest) {
		String agreementRequestMessage = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try{
			agreementRequestMessage	= objectMapper.writeValueAsString(agreementRequest);
		}catch (Exception e) {
			LOGGER.debug("WorkflowRepositorysaveAgreement : " +e);
			throw new RuntimeException(e);
		}
		agreementProducer.sendMessage(propertiesManager.getKafkaSaveAgreementTopic(), "key", agreementRequestMessage);
	}

}