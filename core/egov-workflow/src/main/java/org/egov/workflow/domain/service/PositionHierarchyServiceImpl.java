package org.egov.workflow.domain.service;

import java.util.List;

import org.egov.workflow.config.PropertiesManager;
import org.egov.workflow.domain.model.PositionHierarchyResponse;
import org.egov.workflow.domain.model.PositionHierarchyServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PositionHierarchyServiceImpl implements PositionHierarchyService {

    @Autowired
    private PropertiesManager propertiesManager;

    @Override
    public List<PositionHierarchyResponse> getByObjectTypeObjectSubTypeAndFromPosition(final String objectType,
            final String objectSubType, final Long fromPositionid) {
        String url = propertiesManager.getPositionHierarchyUrl();
        return getPositionHierarchyServiceResponse(url, objectType, objectSubType, fromPositionid)
                .getPositionHierarchy();
    }

    private PositionHierarchyServiceResponse getPositionHierarchyServiceResponse(final String url, final String objectType,
            final String objectSubType, final Long fromPositionid) {
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, PositionHierarchyServiceResponse.class, objectType, objectSubType,
                fromPositionid);
    }

}