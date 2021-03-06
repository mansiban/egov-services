/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.eis.model.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BloodGroup {
	A_POSITIVE("A+"), B_POSITIVE("B+"), AB_POSITIVE("AB+"), O_POSITIVE("O+"),
	A_NEGATIVE("A-"), B_NEGATIVE("B-"), AB_NEGATIVE("AB-"), O_NEGATIVE("O-");

	private String value;

	BloodGroup(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
    public String toString() {
        return StringUtils.capitalize(name());
    }

	public static List<Map<String, String>> getBloodGroups() {
		List<Map<String, String>> bloodGroups = new ArrayList<>();
		for (BloodGroup obj : BloodGroup.values()) {
			Map<String, String> bloodGroup = new HashMap<>();
			bloodGroup.put("id", obj.toString());
			bloodGroup.put("name", obj.value);
			bloodGroups.add(bloodGroup);
		}
		return bloodGroups;
	}

	@JsonCreator
	public static BloodGroup fromValue(String passedValue) {
		for (BloodGroup obj : BloodGroup.values()) {
			if (String.valueOf(obj.value).equals(passedValue.toUpperCase())) {
				return obj;
			}
		}
		return null;
	}
}