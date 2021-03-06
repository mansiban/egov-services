package org.egov.egf.persistence.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.egf.domain.exception.InvalidDataException;
import org.egov.egf.persistence.entity.AccountDetailType;
import org.egov.egf.persistence.entity.ChartOfAccount;
import org.egov.egf.persistence.entity.ChartOfAccountDetail;
import org.egov.egf.persistence.queue.contract.ChartOfAccountDetailContract;
import org.egov.egf.persistence.queue.contract.ChartOfAccountDetailContractRequest;
import org.egov.egf.persistence.repository.ChartOfAccountDetailRepository;
import org.egov.egf.persistence.specification.ChartOfAccountDetailSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

@Service
@Transactional(readOnly = true)
public class ChartOfAccountDetailService {

    private final ChartOfAccountDetailRepository chartOfAccountDetailRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ChartOfAccountDetailService(final ChartOfAccountDetailRepository chartOfAccountDetailRepository) {
        this.chartOfAccountDetailRepository = chartOfAccountDetailRepository;
    }

    @Autowired
    private SmartValidator validator;
    @Autowired
    private ChartOfAccountService chartOfAccountService;
    @Autowired
    private AccountDetailTypeService accountDetailTypeService;

    @Transactional
    public ChartOfAccountDetail create(final ChartOfAccountDetail chartOfAccountDetail) {
        setChartOfAccountDetail(chartOfAccountDetail);
        return chartOfAccountDetailRepository.save(chartOfAccountDetail);
    }

    private void setChartOfAccountDetail(final ChartOfAccountDetail chartOfAccountDetail) {
        if (chartOfAccountDetail.getChartOfAccount() != null) {
            ChartOfAccount chartOfAccount = chartOfAccountService
                    .findOne(chartOfAccountDetail.getChartOfAccount().getId());
            if (chartOfAccount == null) {
                throw new InvalidDataException("chartOfAccount", "chartOfAccount.invalid",
                        " Invalid chartOfAccount");
            }
            chartOfAccountDetail.setChartOfAccount(chartOfAccount);
        }
        if (chartOfAccountDetail.getAccountDetailType() != null) {
            AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(chartOfAccountDetail.getAccountDetailType().getId());
            if (accountDetailType == null) {
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            }
            chartOfAccountDetail.setAccountDetailType(accountDetailType);
        }
    }

    @Transactional
    public ChartOfAccountDetail update(final ChartOfAccountDetail chartOfAccountDetail) {
        setChartOfAccountDetail(chartOfAccountDetail);
        return chartOfAccountDetailRepository.save(chartOfAccountDetail);
    }

    public List<ChartOfAccountDetail> findAll() {
        return chartOfAccountDetailRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public ChartOfAccountDetail findOne(Long id) {
        return chartOfAccountDetailRepository.findOne(id);
    }

    public Page<ChartOfAccountDetail> search(ChartOfAccountDetailContractRequest chartOfAccountDetailContractRequest) {
        final ChartOfAccountDetailSpecification specification = new ChartOfAccountDetailSpecification(
                chartOfAccountDetailContractRequest.getChartOfAccountDetail());
        Pageable page = new PageRequest(chartOfAccountDetailContractRequest.getPage().getOffSet(),
                chartOfAccountDetailContractRequest.getPage().getPageSize());
        return chartOfAccountDetailRepository.findAll(specification, page);
    }

    public BindingResult validate(ChartOfAccountDetailContractRequest chartOfAccountDetailContractRequest,
            String method, BindingResult errors) {

        try {
            switch (method) {
            case "update":
                Assert.notNull(chartOfAccountDetailContractRequest.getChartOfAccountDetail(),
                        "ChartOfAccountDetail to edit must not be null");
                validator.validate(chartOfAccountDetailContractRequest.getChartOfAccountDetail(), errors);
                break;
            case "view":
                // validator.validate(chartOfAccountDetailContractRequest.getChartOfAccountDetail(),
                // errors);
                break;
            case "create":
                Assert.notNull(chartOfAccountDetailContractRequest.getChartOfAccountDetails(),
                        "ChartOfAccountDetails to create must not be null");
                for (ChartOfAccountDetailContract b : chartOfAccountDetailContractRequest.getChartOfAccountDetails()) {
                    validator.validate(b, errors);
                }
                break;
            case "updateAll":
                Assert.notNull(chartOfAccountDetailContractRequest.getChartOfAccountDetails(),
                        "ChartOfAccountDetails to create must not be null");
                for (ChartOfAccountDetailContract b : chartOfAccountDetailContractRequest.getChartOfAccountDetails()) {
                    validator.validate(b, errors);
                }
                break;
            default:
                validator.validate(chartOfAccountDetailContractRequest.getRequestInfo(), errors);
            }
        } catch (IllegalArgumentException e) {
            errors.addError(new ObjectError("Missing data", e.getMessage()));
        }
        return errors;

    }

    public ChartOfAccountDetailContractRequest fetchRelatedContracts(
            ChartOfAccountDetailContractRequest chartOfAccountDetailContractRequest) {
        ModelMapper model = new ModelMapper();
        for (ChartOfAccountDetailContract chartOfAccountDetail : chartOfAccountDetailContractRequest
                .getChartOfAccountDetails()) {
            if (chartOfAccountDetail.getChartOfAccount() != null) {
                ChartOfAccount chartOfAccount = chartOfAccountService
                        .findOne(chartOfAccountDetail.getChartOfAccount().getId());
                if (chartOfAccount == null) {
                    throw new InvalidDataException("chartOfAccount", "chartOfAccount.invalid",
                            " Invalid chartOfAccount");
                }
                model.map(chartOfAccount, chartOfAccountDetail.getChartOfAccount());
            }
            if (chartOfAccountDetail.getAccountDetailType() != null) {
                AccountDetailType accountDetailType = accountDetailTypeService
                        .findOne(chartOfAccountDetail.getAccountDetailType().getId());
                if (accountDetailType == null) {
                    throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                            " Invalid accountDetailType");
                }
                model.map(accountDetailType, chartOfAccountDetail.getAccountDetailType());
            }
        }
        ChartOfAccountDetailContract chartOfAccountDetail = chartOfAccountDetailContractRequest
                .getChartOfAccountDetail();
        if (chartOfAccountDetail.getChartOfAccount() != null) {
            ChartOfAccount chartOfAccount = chartOfAccountService
                    .findOne(chartOfAccountDetail.getChartOfAccount().getId());
            if (chartOfAccount == null) {
                throw new InvalidDataException("chartOfAccount", "chartOfAccount.invalid", " Invalid chartOfAccount");
            }
            model.map(chartOfAccount, chartOfAccountDetail.getChartOfAccount());
        }
        if (chartOfAccountDetail.getAccountDetailType() != null) {
            AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(chartOfAccountDetail.getAccountDetailType().getId());
            if (accountDetailType == null) {
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            }
            model.map(accountDetailType, chartOfAccountDetail.getAccountDetailType());
        }
        return chartOfAccountDetailContractRequest;
    }
}