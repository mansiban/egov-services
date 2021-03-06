package org.egov.egf.persistence.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.egf.domain.exception.InvalidDataException;
import org.egov.egf.persistence.entity.AccountDetailType;
import org.egov.egf.persistence.entity.AccountEntity;
import org.egov.egf.persistence.queue.contract.AccountEntityContract;
import org.egov.egf.persistence.queue.contract.AccountEntityContractRequest;
import org.egov.egf.persistence.repository.AccountEntityRepository;
import org.egov.egf.persistence.specification.AccountEntitySpecification;
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
public class AccountEntityService {

    private final AccountEntityRepository accountEntityRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public AccountEntityService(final AccountEntityRepository accountEntityRepository) {
        this.accountEntityRepository = accountEntityRepository;
    }

    @Autowired
    private SmartValidator validator;
    @Autowired
    private AccountDetailTypeService accountDetailTypeService;

    @Transactional
    public AccountEntity create(final AccountEntity accountEntity) {
        setAccountEntity(accountEntity);
        return accountEntityRepository.save(accountEntity);
    }

    @Transactional
    public AccountEntity update(final AccountEntity accountEntity) {
        setAccountEntity(accountEntity);
        return accountEntityRepository.save(accountEntity);
    }

    private void setAccountEntity(final AccountEntity accountEntity) {
        if (accountEntity.getAccountDetailType() != null) {
            final AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(accountEntity.getAccountDetailType().getId());
            if (accountDetailType == null)
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            accountEntity.setAccountDetailType(accountDetailType);
        }
    }

    public List<AccountEntity> findAll() {
        return accountEntityRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public AccountEntity findByName(final String name) {
        return accountEntityRepository.findByName(name);
    }

    public AccountEntity findByCode(final String code) {
        return accountEntityRepository.findByCode(code);
    }

    public AccountEntity findOne(final Long id) {
        return accountEntityRepository.findOne(id);
    }

    public Page<AccountEntity> search(final AccountEntityContractRequest accountEntityContractRequest) {
        final AccountEntitySpecification specification = new AccountEntitySpecification(
                accountEntityContractRequest.getAccountEntity());
        final Pageable page = new PageRequest(accountEntityContractRequest.getPage().getOffSet(),
                accountEntityContractRequest.getPage().getPageSize());
        return accountEntityRepository.findAll(specification, page);
    }

    public BindingResult validate(final AccountEntityContractRequest accountEntityContractRequest, final String method,
            final BindingResult errors) {

        try {
            switch (method) {
            case "update":
                Assert.notNull(accountEntityContractRequest.getAccountEntity(),
                        "AccountEntity to edit must not be null");
                validator.validate(accountEntityContractRequest.getAccountEntity(), errors);
                break;
            case "view":
                // validator.validate(accountEntityContractRequest.getAccountEntity(),
                // errors);
                break;
            case "create":
                Assert.notNull(accountEntityContractRequest.getAccountEntities(),
                        "AccountEntities to create must not be null");
                for (final AccountEntityContract b : accountEntityContractRequest.getAccountEntities())
                    validator.validate(b, errors);
                break;
            case "updateAll":
                Assert.notNull(accountEntityContractRequest.getAccountEntities(),
                        "AccountEntities to create must not be null");
                for (final AccountEntityContract b : accountEntityContractRequest.getAccountEntities())
                    validator.validate(b, errors);
                break;
            default:
                validator.validate(accountEntityContractRequest.getRequestInfo(), errors);
            }
        } catch (final IllegalArgumentException e) {
            errors.addError(new ObjectError("Missing data", e.getMessage()));
        }
        return errors;

    }

    public AccountEntityContractRequest fetchRelatedContracts(
            final AccountEntityContractRequest accountEntityContractRequest) {
        final ModelMapper model = new ModelMapper();
        for (final AccountEntityContract accountEntity : accountEntityContractRequest.getAccountEntities())
            if (accountEntity.getAccountDetailType() != null) {
                final AccountDetailType accountDetailType = accountDetailTypeService
                        .findOne(accountEntity.getAccountDetailType().getId());
                if (accountDetailType == null)
                    throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                            " Invalid accountDetailType");
                model.map(accountDetailType, accountEntity.getAccountDetailType());
            }
        final AccountEntityContract accountEntity = accountEntityContractRequest.getAccountEntity();
        if (accountEntity.getAccountDetailType() != null) {
            final AccountDetailType accountDetailType = accountDetailTypeService
                    .findOne(accountEntity.getAccountDetailType().getId());
            if (accountDetailType == null)
                throw new InvalidDataException("accountDetailType", "accountDetailType.invalid",
                        " Invalid accountDetailType");
            model.map(accountDetailType, accountEntity.getAccountDetailType());
        }
        return accountEntityContractRequest;
    }
}