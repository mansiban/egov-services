package org.egov.lams.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.egov.lams.config.PropertiesManager;
import org.egov.lams.model.Demand;
import org.egov.lams.model.DemandDetails;
import org.egov.lams.model.DemandReason;
import org.egov.lams.repository.helper.DemandHelper;
import org.egov.lams.web.contract.AgreementRequest;
import org.egov.lams.web.contract.DemandReasonResponse;
import org.egov.lams.web.contract.DemandRequest;
import org.egov.lams.web.contract.DemandResponse;
import org.egov.lams.web.contract.DemandSearchCriteria;
import org.egov.lams.web.contract.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class DemandRepository {

	private static final Logger LOGGER = Logger.getLogger(DemandRepository.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	DemandHelper demandHelper;

	public List<DemandReason> getDemandReason(AgreementRequest agreementRequest) {
		
		String url = propertiesManager.getDemandServiceHostName()
				   + propertiesManager.getDemandReasonSearchService()
				   + demandHelper.getDemandReasonUrlParams(agreementRequest);
		
		System.out.println("DemandRepository getDemandReason url:" + url);
		DemandReasonResponse demandReasonResponse = null;
		try {
			demandReasonResponse = restTemplate.postForObject(url, agreementRequest.getRequestInfo(),
					DemandReasonResponse.class);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("DemandRepository : " + exception.getMessage(), exception.getCause());
		}
		System.out.println("demandReasonResponse:" + demandReasonResponse);
		// Todo if api returns exception object
		return demandReasonResponse.getDemandReasons();
	}

	public List<Demand> getDemandList(AgreementRequest agreementRequest, List<DemandReason> demandReasons) {

		List<Demand> demands = new ArrayList<>();
		List<DemandDetails> demandDetails = new ArrayList<>();
		Demand demand = new Demand();
		demand.setTenantId(agreementRequest.getAgreement().getTenantId());
		demand.setInstallment(demandReasons.get(0).getTaxPeriod());
		demand.setModuleName("Leases And Agreements");

		DemandDetails demandDetail = null;
		for (DemandReason demandReason : demandReasons) {
			demandDetail = new DemandDetails();
			// rent has to be not null
			demandDetail.setTaxAmount(BigDecimal.valueOf(agreementRequest.getAgreement().getRent()));
			demandDetail.setCollectionAmount(BigDecimal.ZERO);
			demandDetail.setRebateAmount(BigDecimal.ZERO);
			demandDetail.setTaxReason(demandReason.getName());
			demandDetail.setTaxPeriod(demandReason.getTaxPeriod());

			demandDetails.add(demandDetail);
		}
		demand.setDemandDetails(demandDetails);
		demands.add(demand);
		System.err.println(demand);

		return demands;
	}

	public DemandResponse createDemand(List<Demand> demands, RequestInfo requestInfo) {
		System.out.println("DemandRepository createDemand demands:" + demands.toString());
		DemandRequest demandRequest = new DemandRequest();
		demandRequest.setRequestInfo(requestInfo);
		demandRequest.setDemand(demands);

		String url = propertiesManager.getDemandServiceHostName() + propertiesManager.getCreateDemandSevice();

		return restTemplate.postForObject(url, demandRequest, DemandResponse.class);
	}

	public DemandResponse getDemandBySearch(DemandSearchCriteria demandSearchCriteria, RequestInfo requestInfo) {

		String url = propertiesManager.getDemandServiceHostName() + propertiesManager.getDemandSearchService()
				+ "?demandId=" + demandSearchCriteria.getDemandId();
		LOGGER.info("the url of demand search API call ::: is " + url);

		if (requestInfo == null) {
			// FIXME remove this when application is running good
			LOGGER.info("requestInfo ::: is null ");
			requestInfo = new RequestInfo();
			requestInfo.setApiId("apiid");
			requestInfo.setVer("ver");
			requestInfo.setTs(new Date());
		}

		DemandResponse demandResponse = null;
		try {
			demandResponse = restTemplate.postForObject(url, requestInfo, DemandResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("the exception thrown from demand search api call ::: " + e);
		}
		LOGGER.info("the response form demand search api call ::: " + demandResponse);
		return demandResponse;
	}

	public DemandResponse updateDemand(List<Demand> demands, RequestInfo requestInfo) {

		System.out.println("DemandRepository createDemand demands:" + demands.toString());
		DemandRequest demandRequest = new DemandRequest();
		demandRequest.setRequestInfo(requestInfo);
		demandRequest.setDemand(demands);

		String url = propertiesManager.getDemandServiceHostName() + propertiesManager.getUpdateDemandBasePath()
				+ demands.get(0).getId() + propertiesManager.getUpdateDemandService();
		LOGGER.info("the url for update demand API call is  ::: " + url);

		DemandResponse demandResponse = null;
		try {
			demandResponse = restTemplate.postForObject(url, demandRequest, DemandResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("the exception raised during update demand API call ::: " + e);
		}
		LOGGER.info("the exception raised during update demand API call ::: " +demandResponse );
		return demandResponse;
	}
}
