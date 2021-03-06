package org.egov.pgr.read.web.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ComplaintTypeCategoryResponse {

    private ResponseInfo responseInfo;

    private List<ComplaintTypeCategory> complaintTypeCategories;

}
