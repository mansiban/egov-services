package org.egov.lams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.ToString;

@Configuration
@Getter
@ToString
public class PropertiesManager {

	@Value("${egov.services.asset_service.hostname}")
	private String assetServiceHostName;

	@Value("${egov.services.asset_service.basepath}")
	private String assetServiceBasePAth;

	@Value("${egov.services.asset_service.searchpath}")
	private String assetServiceSearchPath;

	@Value("${egov.services.allottee_service.hostname}")
	private String allotteeServiceHostName;

	@Value("${egov.services.allottee_service.basepath}")
	private String allotteeServiceBasePAth;

	@Value("${egov.services.allottee_service.searchpath}")
	private String allotteeServiceSearchPath;

	@Value("${egov.services.allottee_service.createpath}")
	private String allotteeServiceCreatePAth;
	
	@Value("${egov.services.lams.ulb_number}")
	private String ulbNumber;

	@Value("${egov.services.lams.agreementnumber_sequence}")
	private String agreementNumberSequence;

	@Value("${egov.services.lams.agreementnumber_prefix}")
	private String lamsPrefix;

	@Value("${egov.services.lams.acknowledgementnumber_sequence}")
	private String acknowledgementNumberSequence;
	
	@Value("${egov.services.demand_service.hostname}")
	private String demandServiceHostName;
	
	@Value("${egov.services.demand_reason_service.searchpath}")
	private String demandReasonSearchService;
	
	@Value("${egov.services.demand_service.createdemand}")
	private String createDemandSevice;
	
	@Value("${egov.services.demand_service.moduleName}")
	private String demandModuleName;
	
	@Value("${egov.services.demand_service.taxCategoryName}")
	private String taxCategoryName;

	@Value("${kafka.topics.start.workflow}")
	private String startWorkflowTopic;
	
	@Value("${kafka.topics.update.workflow}")
	private String updateWorkflowTopic;
	
	@Value("${egov.services.demand_service.searchpath}")
	private String demandSearchService;
	
	@Value("${egov.services.demand_service.bill.create}")
	private String demandBillCreateService;
	
	@Value("${egov.services.lams.billnumber_sequence}")
	private String billNumberSequence;
	
	@Value("${egov.services.lams.billnumber_prefix}")
	private String lamsBillNumberPrefix;

	@Value("${egov.services.demand_service.updatedemandbasepath}")
	private String updateDemandBasePath;
	
	@Value("${egov.services.demand_service.updatedemand}")
	private String updateDemandService;
	
	@Value("${egov.services.demand_service.bill.search}")
	private String demandBillSearchService;

	@Value("${egov.services.collection_service.hostname}")
	public String purposeHostName;

	@Value("${egov.services.collection_service.purpose}")
	public String purposeService;
	
	@Value("${egov.services.financial.hostname}")
	public String financialServiceHostName;

	@Value("${egov.services.financial.chartofaccounts}")
	public String financialGetChartOfAccountsService;
	
	@Value("${egov.services.boundary_service.hostname}")
	public String boundaryServiceHostName;
	
	@Value("${egov.services.boundary_service.searchpath}")
	public String boundaryServiceSearchPath;
	
	@Value("${egov.services.employee_service.hostname}")
	public String employeeServiceHostName;
	
	@Value("${egov.services.employee_service.searchpath}")
	public String employeeServiceSearchPath;
	
	@Value("${egov.services.employee_service.searchpath.pathvariable}")
	public String employeeServiceSearchPathVariable;
	
	@Value("${egov.services.lams.workflow_initiator_position_key}")
	public String workflowInitiatorPositionkey;
	
	@Value("${egov.services.lams.rentincrement_assetcategories}")
	public String rentIncrementAssetCategoryKey;
	
	@Value("${kafka.topics.save.agreement}")
	private String saveAgreementTopic;
	
	@Value("${kafka.topics.update.agreement}")
	private String updateAgreementTopic;
}
