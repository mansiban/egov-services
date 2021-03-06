package org.pgr.batch.repository;

import org.egov.common.contract.request.User;
import org.pgr.batch.repository.contract.UserRequest;
import org.pgr.batch.repository.contract.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserRepository {

    private RestTemplate restTemplate;
    private String userHost;
    private String getUserByUserNameUrl;

    public UserRepository(RestTemplate restTemplate,
                          @Value("${egov.service.user.service.host}") String userHostUrl,
                          @Value("${egov.services.user.get_user_by_username}") String getUserByUserNameUrl) {
        this.restTemplate = restTemplate;
        this.userHost = userHostUrl;
        this.getUserByUserNameUrl = getUserByUserNameUrl;
    }

    public User getUserByUserName(String userName) {
        UserRequest request = new UserRequest();
        request.setUserName(userName);
        String url = userHost + getUserByUserNameUrl;
        return restTemplate.postForObject(url, request, UserResponse.class).getUser().get(0);
    }
}
