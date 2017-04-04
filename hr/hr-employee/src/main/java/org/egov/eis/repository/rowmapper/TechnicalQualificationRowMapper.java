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

package org.egov.eis.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.eis.model.TechnicalQualification;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TechnicalQualificationRowMapper implements RowMapper<TechnicalQualification> {

	@Override
	public TechnicalQualification mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
		TechnicalQualification technicalQualification = new TechnicalQualification();
		technicalQualification.setId(rs.getLong("id"));
		technicalQualification.setSkill(rs.getString("skill"));
		technicalQualification.setGrade(rs.getString("grade"));
		technicalQualification.setYearOfPassing(rs.getInt("yearofpassing"));
		technicalQualification.setRemarks(rs.getString("remarks"));
		technicalQualification.setCreatedBy(rs.getLong("createdby"));
		technicalQualification.setCreatedDate(rs.getDate("createddate"));
		technicalQualification.setLastModifiedBy(rs.getLong("lastmodifiedby"));
		technicalQualification.setLastModifiedDate(rs.getDate("lastmodifieddate"));
		technicalQualification.setTenantId(rs.getString("tenantid"));
		
		return technicalQualification;
	}
}