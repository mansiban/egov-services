package org.egov.pgr.employee.enrichment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
public class WorkflowResponse {

    @JsonProperty("state_id")
    private String stateId;

    @JsonProperty("assignee")
    private String assignee;

}