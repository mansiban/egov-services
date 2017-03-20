package org.egov.workflow.persistence.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.workflow.persistence.entity.State;
import org.egov.workflow.persistence.entity.State.StateStatus;
import org.egov.workflow.persistence.repository.StateRepository;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.SmartValidator;

@Service
@Transactional(readOnly = true)
public class StateService {

	private final StateRepository stateRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public StateService(final StateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	@Autowired
	private SmartValidator validator;
 

	@Transactional
	public State create(final State state) {
		return stateRepository.save(state);
	}

	@Transactional
	public State update(final State state) {
		return stateRepository.save(state);
	}

	public List<State> findAll() {
		return stateRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
	}

	public State findOne(Long id) {
		return stateRepository.findOne(id);
	}
	
	
	  public  List<State>  getStates(List<Long> ownerIds,List<String> types,Long userId)
	    {
	     return getSession().createCriteria(State.class)
	            .setFlushMode(FlushMode.MANUAL).setReadOnly(true).setCacheable(true)
	            .add(Restrictions.in("type", types))
	            .add(Restrictions.in("ownerPosition.id", ownerIds))
	            .add(Restrictions.ne("status", StateStatus.ENDED))
	            .add(Restrictions.not(Restrictions.conjunction().add(Restrictions.eq("status", StateStatus.STARTED))
	                    .add(Restrictions.eq("createdBy.id", userId)))).addOrder(Order.desc("createdDate"))
	                    .list();  
	    
	    }

	private Session getSession() {
		return entityManager.unwrap(Session.class);
	}

/*
	public Page<State> search(StateContractRequest stateContractRequest) {
		final StateSpecification specification = new StateSpecification(stateContractRequest.getState());
		Pageable page = new PageRequest(stateContractRequest.getPage().getOffSet(),
				stateContractRequest.getPage().getPageSize());
		return stateRepository.findAll(specification, page);
	}

	public BindingResult validate(StateContractRequest stateContractRequest, String method, BindingResult errors) {

		try {
			switch (method) {
			case "update":
				Assert.notNull(stateContractRequest.getState(), "State to edit must not be null");
				validator.validate(stateContractRequest.getState(), errors);
				break;
			case "view":
				// validator.validate(stateContractRequest.getState(), errors);
				break;
			case "create":
				Assert.notNull(stateContractRequest.getStates(), "States to create must not be null");
				for (StateContract b : stateContractRequest.getStates())
					validator.validate(b, errors);
				break;
			case "updateAll":
				Assert.notNull(stateContractRequest.getStates(), "States to create must not be null");
				for (StateContract b : stateContractRequest.getStates())
					validator.validate(b, errors);
				break;
			default:
				validator.validate(stateContractRequest.getRequestInfo(), errors);
			}
		} catch (IllegalArgumentException e) {
			errors.addError(new ObjectError("Missing data", e.getMessage()));
		}
		return errors;

	}

	public StateContractRequest fetchRelatedContracts(StateContractRequest stateContractRequest) {
		ModelMapper model = new ModelMapper();
		for (StateContract state : stateContractRequest.getStates()) {
			if (state.getStatus() != null) {
				StateStatus status = stateStatusService.findOne(state.getStatus().getId());
				if (status == null) {
					throw new InvalidDataException("status", "status.invalid", " Invalid status");
				}
				model.map(status, state.getStatus());
			}
		}
		StateContract state = stateContractRequest.getState();
		if (state.getStatus() != null) {
			StateStatus status = stateStatusService.findOne(state.getStatus().getId());
			if (status == null) {
				throw new InvalidDataException("status", "status.invalid", " Invalid status");
			}
			model.map(status, state.getStatus());
		}
		return stateContractRequest;
	}*/
}