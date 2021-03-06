package org.egov.user.web.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.model.User;

@AllArgsConstructor
@Getter
public class CreateUserRequest {
    private RequestInfo requestInfo;
    private UserRequest user;

    public User toDomain() {
        return user.toDomain(loggedInUserId());
    }

    private Long loggedInUserId() {
		return requestInfo.getUserInfo() == null ? null : requestInfo.getUserInfo().getId();
	}
    
}

