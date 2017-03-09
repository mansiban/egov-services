package org.egov.persistence.repository;

import org.egov.domain.model.OtpRequest;
import org.egov.persistence.contract.OtpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OtpRepository {

    private final String otpCreateUrl;
    private RestTemplate restTemplate;

    public OtpRepository(RestTemplate restTemplate,
                         @Value("${otp.host}") String otpHost,
                         @Value("${otp.create.url}") String otpCreateUrl) {
        this.restTemplate = restTemplate;
        this.otpCreateUrl = otpHost + otpCreateUrl;
    }

    public String fetchOtp(OtpRequest otpRequest) {
        final org.egov.persistence.contract.OtpRequest request =
                new org.egov.persistence.contract.OtpRequest(otpRequest);
        final OtpResponse otpResponse =
                restTemplate.postForObject(otpCreateUrl, request, OtpResponse.class);
        return otpResponse != null ? otpResponse.getOtpNumber() : null;
    }
}