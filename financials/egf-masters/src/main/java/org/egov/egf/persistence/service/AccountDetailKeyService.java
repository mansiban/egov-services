package org.egov.egf.persistence.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.egf.domain.exception.InvalidDataException;
import org.egov.egf.persistence.entity.AccountDetailKey;
import org.egov.egf.persistence.entity.AccountDetailType;
import org.egov.egf.persistence.queue.contract.AccountDetailKeyContract;
import org.egov.egf.persistence.queue.contract.AccountDetailKeyContractRequest;
import org.egov.egf.persistence.repository.AccountDetailKeyRepository;
import org.egov.egf.persistence.specification.AccountDetailKeySpecification;
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
public class AccountDetailKeyService {

    private final AccountDetailKeyRepository accountDetailKeyRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public AccountDetailKeyService(final AccountDetailKeyRepository accountDetailKeyRepository) {
        this.accountDetailKeyRepository = accountDetailKeyRepository;
    }

    @Autowired
    private SmartValidator validator;
    @Autowired
    private AccountDetailTypeService accountDetailTypeService;

    @Transactional
    public AccountDetailKey create(final AccountDetailKey accountDetailKey) {
        setAccountDetailKey(accountDetailKey);
        return accountDetailKeyRepository.save(accountDetailKey);
    }

    private void setAccountDetailKey(final AccountDetailKey accountDetailKey) {
        if (accountDetailKey.getAccountDetailType() != null) {
            final AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(accountDetailKey.getAccountDetailType().getId());
            if (accountDetailType == null)
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            accountDetailKey.setAccountDetailType(accountDetailType);
        }
    }

    @Transactional
    public AccountDetailKey update(final AccountDetailKey accountDetailKey) {
        setAccountDetailKey(accountDetailKey);
        return accountDetailKeyRepository.save(accountDetailKey);
    }

    public List<AccountDetailKey> findAll() {
        return accountDetailKeyRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public AccountDetailKey findByName(final String name) {
        return accountDetailKeyRepository.findByName(name);
    }

    public AccountDetailKey findOne(final Long id) {
        return accountDetailKeyRepository.findOne(id);
    }

    public Page<AccountDetailKey> search(final AccountDetailKeyContractRequest accountDetailKeyContractRequest) {
        final AccountDetailKeySpecification specification = new AccountDetailKeySpecification(
                accountDetailKeyContractRequest.getAccountDetailKey());
        final Pageable page = new PageRequest(accountDetailKeyContractRequest.getPage().getOffSet(),
                accountDetailKeyContractRequest.getPage().getPageSize());
        return accountDetailKeyRepository.findAll(specification, page);
    }

    public BindingResult validate(final AccountDetailKeyContractRequest accountDetailKeyContractRequest, final String method,
            final BindingResult errors) {

        try {
            switch (method) {
            case "update":
                Assert.notNull(accountDetailKeyContractRequest.getAccountDetailKey(),
                        "AccountDetailKey to edit must not be null");
                validator.validate(accountDetailKeyContractRequest.getAccountDetailKey(), errors);
                break;
            case "view":
                // validator.validate(accountDetailKeyContractRequest.getAccountDetailKey(),
                // errors);
                break;
            case "create":
                Assert.notNull(accountDetailKeyContractRequest.getAccountDetailKeys(),
                        "AccountDetailKeys to create must not be null");
                for (final AccountDetailKeyContract b : accountDetailKeyContractRequest.getAccountDetailKeys())
                    validator.validate(b, errors);
                break;
            case "updateAll":
                Assert.notNull(accountDetailKeyContractRequest.getAccountDetailKeys(),
                        "AccountDetailKeys to create must not be null");
                for (final AccountDetailKeyContract b : accountDetailKeyContractRequest.getAccountDetailKeys())
                    validator.validate(b, errors);
                break;
            default:
                validator.validate(accountDetailKeyContractRequest.getRequestInfo(), errors);
            }
        } catch (final IllegalArgumentException e) {
            errors.addError(new ObjectError("Missing data", e.getMessage()));
        }
        return errors;

    }

    public AccountDetailKeyContractRequest fetchRelatedContracts(
            final AccountDetailKeyContractRequest accountDetailKeyContractRequest) {
        final ModelMapper model = new ModelMapper();
        for (final AccountDetailKeyContract accountDetailKey : accountDetailKeyContractRequest.getAccountDetailKeys())
            if (accountDetailKey.getAccountDetailType() != null) {
                final AccountDetailType accountDetailType = accountDetailTypeService
                        .findOne(accountDetailKey.getAccountDetailType().getId());
                if (accountDetailType == null)
                    throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                            " Invalid accountDetailType");
                model.map(accountDetailType, accountDetailKey.getAccountDetailType());
            }
        final AccountDetailKeyContract accountDetailKey = accountDetailKeyContractRequest.getAccountDetailKey();
        if (accountDetailKey.getAccountDetailType() != null) {
            final AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(accountDetailKey.getAccountDetailType().getId());
            if (accountDetailType == null)
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            model.map(accountDetailType, accountDetailKey.getAccountDetailType());
        }
        return accountDetailKeyContractRequest;
    }
}