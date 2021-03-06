package org.egov.egf.web.controller;

import java.util.ArrayList;
import java.util.Date;

import javax.validation.Valid;

import org.egov.egf.domain.exception.CustomBindException;
import org.egov.egf.persistence.entity.AccountDetailKey;
import org.egov.egf.persistence.queue.contract.AccountDetailKeyContract;
import org.egov.egf.persistence.queue.contract.AccountDetailKeyContractRequest;
import org.egov.egf.persistence.queue.contract.AccountDetailKeyContractResponse;
import org.egov.egf.persistence.queue.contract.Pagination;
import org.egov.egf.persistence.queue.contract.RequestInfo;
import org.egov.egf.persistence.queue.contract.ResponseInfo;
import org.egov.egf.persistence.service.AccountDetailKeyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountdetailkeys")  
public class AccountDetailKeyController {
	@Autowired
	private AccountDetailKeyService  accountDetailKeyService;

	@PostMapping("_create")
	@ResponseStatus(HttpStatus.CREATED)
	public  AccountDetailKeyContractResponse create(@RequestBody @Valid AccountDetailKeyContractRequest accountDetailKeyContractRequest, BindingResult errors) {
		ModelMapper modelMapper=new ModelMapper();
		accountDetailKeyService.validate(accountDetailKeyContractRequest,"create",errors);
		if (errors.hasErrors()) {
		  throw	new CustomBindException(errors);
		}
		accountDetailKeyService.fetchRelatedContracts(accountDetailKeyContractRequest);
		AccountDetailKeyContractResponse accountDetailKeyContractResponse = new AccountDetailKeyContractResponse();
		accountDetailKeyContractResponse.setAccountDetailKeys(new ArrayList<AccountDetailKeyContract>());
		for(AccountDetailKeyContract accountDetailKeyContract:accountDetailKeyContractRequest.getAccountDetailKeys())
		{
		
		AccountDetailKey	accountDetailKeyEntity=	modelMapper.map(accountDetailKeyContract, AccountDetailKey.class);
		accountDetailKeyEntity = accountDetailKeyService.create(accountDetailKeyEntity);
		AccountDetailKeyContract resp=modelMapper.map(accountDetailKeyEntity, AccountDetailKeyContract.class);
		accountDetailKeyContract.setId(accountDetailKeyEntity.getId());
		accountDetailKeyContractResponse.getAccountDetailKeys().add(resp);
		}

		accountDetailKeyContractResponse.setResponseInfo(getResponseInfo(accountDetailKeyContractRequest.getRequestInfo()));
		 
		return accountDetailKeyContractResponse;
	}

	@PostMapping(value = "/{uniqueId}/_update")
	@ResponseStatus(HttpStatus.OK)
	public AccountDetailKeyContractResponse update(@RequestBody @Valid AccountDetailKeyContractRequest accountDetailKeyContractRequest, BindingResult errors,
			@PathVariable Long uniqueId) {
		
		accountDetailKeyService.validate(accountDetailKeyContractRequest,"update",errors);
		
		if (errors.hasErrors()) {
			  throw	new CustomBindException(errors);
			}
		accountDetailKeyService.fetchRelatedContracts(accountDetailKeyContractRequest);
		AccountDetailKey accountDetailKeyFromDb = accountDetailKeyService.findOne(uniqueId);
		
		AccountDetailKeyContract accountDetailKey = accountDetailKeyContractRequest.getAccountDetailKey();
		//ignoring internally passed id if the put has id in url
	    accountDetailKey.setId(uniqueId);
		ModelMapper model=new ModelMapper();
	 	model.map(accountDetailKey, accountDetailKeyFromDb);
		accountDetailKeyFromDb = accountDetailKeyService.update(accountDetailKeyFromDb);
		AccountDetailKeyContractResponse accountDetailKeyContractResponse = new AccountDetailKeyContractResponse();
		accountDetailKeyContractResponse.setAccountDetailKey(accountDetailKey);  
		accountDetailKeyContractResponse.setResponseInfo(getResponseInfo(accountDetailKeyContractRequest.getRequestInfo()));
		accountDetailKeyContractResponse.getResponseInfo().setStatus(HttpStatus.OK.toString());
		return accountDetailKeyContractResponse;
	}
	
	@GetMapping(value = "/{uniqueId}")
	@ResponseStatus(HttpStatus.OK)
	public AccountDetailKeyContractResponse view(@ModelAttribute AccountDetailKeyContractRequest accountDetailKeyContractRequest, BindingResult errors,
			@PathVariable Long uniqueId) {
		accountDetailKeyService.validate(accountDetailKeyContractRequest,"view",errors);
		if (errors.hasErrors()) {
			  throw	new CustomBindException(errors);
			}
		accountDetailKeyService.fetchRelatedContracts(accountDetailKeyContractRequest);
		RequestInfo requestInfo = accountDetailKeyContractRequest.getRequestInfo();
		AccountDetailKey accountDetailKeyFromDb = accountDetailKeyService.findOne(uniqueId);
		AccountDetailKeyContract accountDetailKey = accountDetailKeyContractRequest.getAccountDetailKey();
		
		ModelMapper model=new ModelMapper();
	 	model.map(accountDetailKeyFromDb,accountDetailKey );
		
		AccountDetailKeyContractResponse accountDetailKeyContractResponse = new AccountDetailKeyContractResponse();
		accountDetailKeyContractResponse.setAccountDetailKey(accountDetailKey);  
		accountDetailKeyContractResponse.setResponseInfo(getResponseInfo(accountDetailKeyContractRequest.getRequestInfo()));
		accountDetailKeyContractResponse.getResponseInfo().setStatus(HttpStatus.CREATED.toString());
		return accountDetailKeyContractResponse ;
	}
	
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public AccountDetailKeyContractResponse updateAll(@RequestBody @Valid AccountDetailKeyContractRequest accountDetailKeyContractRequest, BindingResult errors) {
		accountDetailKeyService.validate(accountDetailKeyContractRequest,"updateAll",errors);
		if (errors.hasErrors()) {
			  throw	new CustomBindException(errors);
			}
		accountDetailKeyService.fetchRelatedContracts(accountDetailKeyContractRequest);		
 
		AccountDetailKeyContractResponse accountDetailKeyContractResponse =new  AccountDetailKeyContractResponse();
		accountDetailKeyContractResponse.setAccountDetailKeys(new ArrayList<AccountDetailKeyContract>());
		for(AccountDetailKeyContract accountDetailKeyContract:accountDetailKeyContractRequest.getAccountDetailKeys())
		{
		AccountDetailKey accountDetailKeyFromDb = accountDetailKeyService.findOne(accountDetailKeyContract.getId());
		
		ModelMapper model=new ModelMapper();
	 	model.map(accountDetailKeyContract, accountDetailKeyFromDb);
		accountDetailKeyFromDb = accountDetailKeyService.update(accountDetailKeyFromDb);
		model.map(accountDetailKeyFromDb,accountDetailKeyContract);
		accountDetailKeyContractResponse.getAccountDetailKeys().add(accountDetailKeyContract);  
		}

		accountDetailKeyContractResponse.setResponseInfo(getResponseInfo(accountDetailKeyContractRequest.getRequestInfo()));
		accountDetailKeyContractResponse.getResponseInfo().setStatus(HttpStatus.OK.toString());
		
		return accountDetailKeyContractResponse;
	}
	

	
	@PostMapping("/_search")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public AccountDetailKeyContractResponse search(@ModelAttribute AccountDetailKeyContract accountDetailKeyContracts,@RequestBody RequestInfo requestInfo,BindingResult errors) {
	    AccountDetailKeyContractRequest accountDetailKeyContractRequest = new AccountDetailKeyContractRequest();
	    accountDetailKeyContractRequest.setAccountDetailKey(accountDetailKeyContracts);
	    accountDetailKeyContractRequest.setRequestInfo(requestInfo);
	    accountDetailKeyService.validate(accountDetailKeyContractRequest,"search",errors);
            if (errors.hasErrors()) {
                throw new CustomBindException(errors);
            }
            accountDetailKeyService.fetchRelatedContracts(accountDetailKeyContractRequest);
            AccountDetailKeyContractResponse accountDetailKeyContractResponse = new AccountDetailKeyContractResponse();
            accountDetailKeyContractResponse.setAccountDetailKeys(new ArrayList<AccountDetailKeyContract>());
            accountDetailKeyContractResponse.setPage(new Pagination());
            Page<AccountDetailKey> allAccountDetailKeys;
            ModelMapper model = new ModelMapper();
    
            allAccountDetailKeys = accountDetailKeyService.search(accountDetailKeyContractRequest);
            AccountDetailKeyContract accountDetailKeyContract = null;
            for (AccountDetailKey b : allAccountDetailKeys) {
                accountDetailKeyContract = new AccountDetailKeyContract();
                model.map(b, accountDetailKeyContract);
                accountDetailKeyContractResponse.getAccountDetailKeys().add(accountDetailKeyContract);
            }
            accountDetailKeyContractResponse.getPage().map(allAccountDetailKeys);
            accountDetailKeyContractResponse.setResponseInfo(getResponseInfo(accountDetailKeyContractRequest.getRequestInfo()));
            accountDetailKeyContractResponse.getResponseInfo().setStatus(HttpStatus.OK.toString());
            return accountDetailKeyContractResponse;
	}

	
	private ResponseInfo getResponseInfo(RequestInfo requestInfo) {
        new ResponseInfo();
		return ResponseInfo.builder()
                .apiId(requestInfo.getApiId())
                .ver(requestInfo.getVer())
                .ts(new Date())
                .resMsgId(requestInfo.getMsgId())
                .resMsgId("placeholder")
                .status("placeholder")
                .build();
    }

}