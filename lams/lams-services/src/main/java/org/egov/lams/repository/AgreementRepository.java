package org.egov.lams.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.lams.config.PropertiesManager;
import org.egov.lams.model.Agreement;
import org.egov.lams.model.AgreementCriteria;
import org.egov.lams.model.Allottee;
import org.egov.lams.model.Asset;
import org.egov.lams.repository.builder.AgreementQueryBuilder;
import org.egov.lams.repository.helper.AgreementHelper;
import org.egov.lams.repository.helper.AllotteeHelper;
import org.egov.lams.repository.helper.AssetHelper;
import org.egov.lams.repository.rowmapper.AgreementRowMapper;
import org.egov.lams.service.AllotteeService;
import org.egov.lams.service.AssetService;
import org.egov.lams.web.contract.AllotteeResponse;
import org.egov.lams.web.contract.AssetResponse;
import org.egov.lams.web.contract.RequestInfo;
import org.egov.lams.web.contract.RequestInfoWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AgreementRepository {
	public static final Logger logger = LoggerFactory.getLogger(AgreementRepository.class);

	@Autowired
	private AssetHelper assetHelper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AssetService assetService;

	@Autowired
	private AllotteeService allotteeService;

	@Autowired
	private AllotteeHelper allotteeHelper;

	@Autowired
	private AgreementHelper agreementHelper;

	@Autowired
	private PropertiesManager propertiesManager;

	public Agreement findAgreementById(String acknowledgementNumber) {

		Agreement agreement = null;
		String sql = AgreementQueryBuilder.agreementQuery;
		Object[] preparedStatementValues = new Object[] { acknowledgementNumber };

		try {
			agreement = jdbcTemplate.queryForObject(sql, preparedStatementValues, Agreement.class);
		} catch (DataAccessException e) {
			logger.info("exception in getagreementbyid :: " + e);
			throw new RuntimeException(e.getMessage());
		}
		return agreement;
	}

	public List<Agreement> findByAllotee(AgreementCriteria agreementCriteria) {
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;

		List<Allottee> allottees = getAllottees(agreementCriteria);
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdList(allottees));
		String queryStr = AgreementQueryBuilder.agreementQueryBuilder(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			// FIXME log exception here
			// FIXME see if it can be better than RunTimeException rethrown
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements; // empty agreement list is returned
		// throw new RuntimeException("The criteria provided did not match any
		// agreements");
		agreementCriteria.setAsset(assetHelper.getAssetIdListByAgreements(agreements));

		List<Asset> assets = getAssets(agreementCriteria);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAsset(AgreementCriteria agreementCriteria) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAsset");
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;
		System.out.println("before calling get asset method");
		List<Asset> assets = getAssets(agreementCriteria);
		System.out.println("after calling get asset method : lengeth of result is" + assets.size());
		if (assets.size() > 1000) // FIXME
			throw new RuntimeException("Asset criteria is too big");
		agreementCriteria.setAsset(assetHelper.getAssetIdList(assets));
		String queryStr = AgreementQueryBuilder.agreementQueryBuilder(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			logger.info(e.getMessage(), e.getCause());
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			return agreements;
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(agreementCriteria);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreement(AgreementCriteria fetchAgreementsModel) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAgreement");
		List<Object> preparedStatementValues = new ArrayList<>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.agreementQueryBuilder(fetchAgreementsModel, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			throw new RuntimeException("The criteria provided did not match any agreements");
		fetchAgreementsModel.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		fetchAgreementsModel.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Asset> assets = getAssets(fetchAgreementsModel);
		List<Allottee> allottees = getAllottees(fetchAgreementsModel);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreementAndAllotee(AgreementCriteria agreementCriteria) {
		logger.info(
				"AgreementController SearchAgreementService AgreementRepository : inside findByAgreementAndAllotee");
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.agreementQueryBuilder(agreementCriteria, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			throw new RuntimeException("The criteria provided did not match any agreements");
		agreementCriteria.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(agreementCriteria);
		agreementCriteria.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		List<Asset> assets = getAssets(agreementCriteria);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	public List<Agreement> findByAgreementAndAsset(AgreementCriteria fetchAgreementsModel) {
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside findByAgreementAndAsset");
		List<Object> preparedStatementValues = new ArrayList<Object>();
		List<Agreement> agreements = null;

		String queryStr = AgreementQueryBuilder.agreementQueryBuilder(fetchAgreementsModel, preparedStatementValues);
		try {
			agreements = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(), new AgreementRowMapper());
		} catch (DataAccessException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (agreements.isEmpty())
			throw new RuntimeException("The criteria provided did not match any agreements");
		fetchAgreementsModel.setAsset(assetHelper.getAssetIdListByAgreements(agreements));
		List<Asset> assets = getAssets(fetchAgreementsModel);
		fetchAgreementsModel.setAllottee(allotteeHelper.getAllotteeIdListByAgreements(agreements));
		List<Allottee> allottees = getAllottees(fetchAgreementsModel);
		agreements = agreementHelper.filterAndEnrichAgreements(agreements, allottees, assets);

		return agreements;
	}

	/*
	 * method to return a list of Allottee objects by making an API call to
	 * Allottee API
	 */
	public List<Allottee> getAllottees(AgreementCriteria agreementCriteria) {
		// FIXME TODO urgent allottee helper has to be changed for post
		// String queryString =
		// allotteeHelper.getAllotteeParams(agreementCriteria);
		logger.info("AgreementController SearchAgreementService AgreementRepository : inside Allottee API caller");
		AllotteeResponse allotteeResponse = allotteeService.getAllottees(agreementCriteria, new RequestInfo());
		if (allotteeResponse.getAllottee() == null || allotteeResponse.getAllottee().size() <= 0)
			throw new RuntimeException("No allottee found for given criteria");
		System.err.println("the result allottee response from allottee api call : " + allotteeResponse.getAllottee());
		return allotteeResponse.getAllottee();
	}

	/*
	 * method to return a list of Asset objects by calling AssetService API
	 */
	public List<Asset> getAssets(AgreementCriteria agreementCriteria) {
		System.out.println("inside get asset method");
		String queryString = assetHelper.getAssetParams(agreementCriteria);
		AssetResponse assetResponse = assetService.getAssets(queryString, new RequestInfoWrapper());
		if (assetResponse.getAssets() == null || assetResponse.getAssets().size() <= 0)
			throw new RuntimeException("No assets found for given criteria");
		// FIXME empty response exception
		System.err.println("the result asset response from asset api call : " + assetResponse.getAssets());
		return assetResponse.getAssets();
	}
}
