package org.egov.egf.persistence.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.egf.domain.exception.InvalidDataException;
import org.egov.egf.persistence.entity.Bank;
import org.egov.egf.persistence.entity.BankBranch;
import org.egov.egf.persistence.queue.contract.BankBranchContract;
import org.egov.egf.persistence.queue.contract.BankBranchContractRequest;
import org.egov.egf.persistence.repository.BankBranchRepository;
import org.egov.egf.persistence.specification.BankBranchSpecification;
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
public class BankBranchService {

    private final BankBranchRepository bankBranchRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BankBranchService(final BankBranchRepository bankBranchRepository) {
        this.bankBranchRepository = bankBranchRepository;
    }

    @Autowired
    private SmartValidator validator;
    @Autowired
    private BankService bankService;

    @Transactional
    public BankBranch create(final BankBranch bankBranch) {
        setBankBranch(bankBranch);
        return bankBranchRepository.save(bankBranch);
    }

    @Transactional
    public BankBranch update(final BankBranch bankBranch) {
        setBankBranch(bankBranch);
        return bankBranchRepository.save(bankBranch);
    }

    private void setBankBranch(final BankBranch bankBranch) {
        if (bankBranch.getBank() != null) {
            Bank bank = bankService.findOne(bankBranch.getBank());
            if (bank == null) {
                throw new InvalidDataException("bank", "bank.invalid", " Invalid bank");
            }
            bankBranch.setBank(bank.getId());
        }
    }

    public List<BankBranch> findAll() {
        return bankBranchRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public BankBranch findByName(String name) {
        return bankBranchRepository.findByName(name);
    }

    public BankBranch findByCode(String code) {
        return bankBranchRepository.findByCode(code);
    }

    public BankBranch findOne(Long id) {
        return bankBranchRepository.findOne(id);
    }

    public Page<BankBranch> search(BankBranchContractRequest bankBranchContractRequest) {
        final BankBranchSpecification specification = new BankBranchSpecification(
                bankBranchContractRequest.getBankBranch());
        Pageable page = new PageRequest(bankBranchContractRequest.getPage().getOffSet(),
                bankBranchContractRequest.getPage().getPageSize());
        return bankBranchRepository.findAll(specification, page);
    }

    public BindingResult validate(BankBranchContractRequest bankBranchContractRequest, String method,
            BindingResult errors) {

        try {
            switch (method) {
            case "update":
                Assert.notNull(bankBranchContractRequest.getBankBranch(), "BankBranch to edit must not be null");
                validator.validate(bankBranchContractRequest.getBankBranch(), errors);
                break;
            case "view":
                // validator.validate(bankBranchContractRequest.getBankBranch(),
                // errors);
                break;
            case "create":
                Assert.notNull(bankBranchContractRequest.getBankBranches(), "BankBranches to create must not be null");
                for (BankBranchContract b : bankBranchContractRequest.getBankBranches()) {
                    validator.validate(b, errors);
                }
                break;
            case "updateAll":
                Assert.notNull(bankBranchContractRequest.getBankBranches(), "BankBranches to create must not be null");
                for (BankBranchContract b : bankBranchContractRequest.getBankBranches()) {
                    validator.validate(b, errors);
                }
                break;
            default:
                validator.validate(bankBranchContractRequest.getRequestInfo(), errors);
            }
        } catch (IllegalArgumentException e) {
            errors.addError(new ObjectError("Missing data", e.getMessage()));
        }
        return errors;

    }

    public BankBranchContractRequest fetchRelatedContracts(BankBranchContractRequest bankBranchContractRequest) {
        ModelMapper model = new ModelMapper();
        for (BankBranchContract bankBranch : bankBranchContractRequest.getBankBranches()) {
            if (bankBranch.getBank() != null) {
                Bank bank = bankService.findOne(bankBranch.getBank().getId());
                if (bank == null) {
                    throw new InvalidDataException("bank", "bank.invalid", " Invalid bank");
                }
                model.map(bank, bankBranch.getBank());
            }
        }
        BankBranchContract bankBranch = bankBranchContractRequest.getBankBranch();
        if (bankBranch.getBank() != null) {
            Bank bank = bankService.findOne(bankBranch.getBank().getId());
            if (bank == null) {
                throw new InvalidDataException("bank", "bank.invalid", " Invalid bank");
            }
            model.map(bank, bankBranch.getBank());
        }
        return bankBranchContractRequest;
    }
}