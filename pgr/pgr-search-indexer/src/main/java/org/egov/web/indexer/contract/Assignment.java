package org.egov.web.indexer.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Assignment {
	// TODO : remove default values once the dependant rest service is ready.
	private Long id = 1L;
	private String name = "Raman";
	private String mobileNumber = "1234567890";
	private String departmentName = "Revenu Dept";
	private String departmentCode = "RD";
	private String designationName = "Asst.Eng.";
}