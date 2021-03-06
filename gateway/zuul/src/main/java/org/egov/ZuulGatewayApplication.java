package org.egov;

import org.egov.filters.pre.AuthFilter;
import org.egov.filters.pre.AuthPreCheckFilter;
import org.egov.filters.pre.RbacFilter;
import org.egov.filters.pre.RbacPreCheckFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@EnableZuulProxy
@SpringBootApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }

    @Value("${egov.user-info-header}")
    private String userInfoHeader;

    @Value("#{'${egov.open-endpoints-whitelist}'.split(',')}")
    private String[] openEndpointsWhitelist;

    @Value("#{'${egov.anonymous-endpoints-whitelist}'.split(',')}")
    private String[] anonymousEndpointsWhitelist;

    @Value("${egov.auth-service-host}")
    private String authServiceHost;

    @Value("${egov.auth-service-uri}")
    private String authServiceUri;

    @Value("#{'${egov.rbac-whitelist}'.split(',')}")
    private String[] rbacWhiteList;

    @Bean
    public AuthPreCheckFilter authCheckFilter() {
        return new AuthPreCheckFilter(new HashSet<>(Arrays.asList(openEndpointsWhitelist)),
            new HashSet<>(Arrays.asList(anonymousEndpointsWhitelist)));
    }

    @Bean
    public AuthFilter authFilter() {
        RestTemplate restTemplate = new RestTemplate();
        final ProxyRequestHelper proxyRequestHelper = new ProxyRequestHelper();
        return new AuthFilter(proxyRequestHelper, restTemplate, authServiceHost, authServiceUri);
    }

    @Bean
    public RbacFilter rbacFilter() {
        return new RbacFilter(new ArrayList<>(Arrays.asList(rbacWhiteList)));
    }

    @Bean
    public RbacPreCheckFilter rbacCheckFilter() {
        return new RbacPreCheckFilter(new HashSet<>(Arrays.asList(openEndpointsWhitelist)),
            new HashSet<>(Arrays.asList(anonymousEndpointsWhitelist)),
            new ArrayList<>(Arrays.asList(rbacWhiteList)));
    }
}