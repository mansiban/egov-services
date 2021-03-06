package org.egov.pgr.read.web.contract;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReceivingModeResponse {

    private ResponseInfo responseInfo;
    private List<ReceivingMode> receivingModes;

}
