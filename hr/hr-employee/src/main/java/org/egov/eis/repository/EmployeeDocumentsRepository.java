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

package org.egov.eis.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDocumentsRepository {

	public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeDocumentsRepository.class);

	public static final String INSERT_EMPLOYEE_DOCUMENTS_QUERY = "INSERT INTO egeis_employeeDocuments"
			+ " (id, employeeId, document, referenceType, referenceId, tenantId)"
			+ " VALUES (nextval('seq_egeis_employeeDocuments'),?,?,?,?,?)";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void save(Long employeeId, List<String> documents, String referenceType, Long referenceId, String tenantId) {
		System.err.println("documents: " + documents);
		jdbcTemplate.batchUpdate(INSERT_EMPLOYEE_DOCUMENTS_QUERY, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, employeeId);
				ps.setString(2, documents.get(i));
				ps.setString(3, referenceType);
				ps.setLong(4, referenceId);
				ps.setString(5, tenantId);
			}

			@Override
			public int getBatchSize() {
				return documents.size();
			}
		});
	}
}