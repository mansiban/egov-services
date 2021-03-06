package org.egov.pgr.read.domain.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Coordinates {
    private Double latitude;
    private Double longitude;
    private String tenantId;

    public boolean isAbsent() {
        return (latitude == null || latitude <= 0) && (longitude == null || longitude <= 0);
    }
}
