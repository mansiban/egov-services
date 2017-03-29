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

package org.egov.eis.repository.builder;

import java.util.List;

import org.egov.eis.config.ApplicationProperties;
import org.egov.eis.web.contract.LeaveAllotmentGetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveAllotmentQueryBuilder {

	private static final Logger logger = LoggerFactory.getLogger(LeaveAllotmentQueryBuilder.class);

	@Autowired
	private ApplicationProperties applicationProperties;

	private static final String BASE_QUERY = "SELECT la_id, la_designation, la_noOfDays, la_createdBy,"
			+ " la_createdDate, la_lastModifiedBy, la_lastModifiedDate, la_tenantId"
			+ " lt_id, lt_name, lt_description, lt_halfdayAllowed, lt_payEligible, lt_accumulative, lt_encashable,"
			+ " lt_active, lt_createdBy, lt_createdDate, lt_lastModifiedBy, lt_lastModifiedDate"
			+ " FROM egeis_leaveAllotment la"
			+ " JOIN egeis_leaveType lt ON la.leaveTypeId = lt.id";

	@SuppressWarnings("rawtypes")
	public String getQuery(LeaveAllotmentGetRequest leaveAllotmentGetRequest, List preparedStatementValues) {
		StringBuilder selectQuery = new StringBuilder(BASE_QUERY);

		addWhereClause(selectQuery, preparedStatementValues, leaveAllotmentGetRequest);
		addOrderByClause(selectQuery, leaveAllotmentGetRequest);
		addPagingClause(selectQuery, preparedStatementValues, leaveAllotmentGetRequest);

		logger.debug("Query : " + selectQuery);
		return selectQuery.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addWhereClause(StringBuilder selectQuery, List preparedStatementValues,
			LeaveAllotmentGetRequest leaveAllotmentGetRequest) {

		if (leaveAllotmentGetRequest.getId() == null && leaveAllotmentGetRequest.getDesignationId() == null
				&& leaveAllotmentGetRequest.getLeaveType() == null && leaveAllotmentGetRequest.getTenantId() == null)
			return;

		selectQuery.append(" WHERE");
		boolean isAppendAndClause = false;

		if (leaveAllotmentGetRequest.getTenantId() != null) {
			isAppendAndClause = true;
			selectQuery.append(" tenantId = ?");
			preparedStatementValues.add(leaveAllotmentGetRequest.getTenantId());
		}

		if (leaveAllotmentGetRequest.getId() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" la_id IN " + getIdQuery(leaveAllotmentGetRequest.getId()));
		}

		if (leaveAllotmentGetRequest.getDesignationId() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" la_designation = ?");
			preparedStatementValues.add(leaveAllotmentGetRequest.getDesignationId());
		}

		if (leaveAllotmentGetRequest.getLeaveType() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
			selectQuery.append(" la_leaveTypeId IN " + getIdQuery(leaveAllotmentGetRequest.getLeaveType()));
		}
	}

	private void addOrderByClause(StringBuilder selectQuery, LeaveAllotmentGetRequest leaveAllotmentGetRequest) {
		String sortBy = (leaveAllotmentGetRequest.getSortBy() == null ? "name" : leaveAllotmentGetRequest.getSortBy());
		String sortOrder = (leaveAllotmentGetRequest.getSortOrder() == null ? "ASC"
				: leaveAllotmentGetRequest.getSortOrder());
		selectQuery.append(" ORDER BY lt_" + sortBy + " " + sortOrder);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addPagingClause(StringBuilder selectQuery, List preparedStatementValues,
			LeaveAllotmentGetRequest leaveAllotmentGetRequest) {
		// handle limit(also called pageSize) here
		selectQuery.append(" LIMIT ?");
		long pageSize = Integer.parseInt(applicationProperties.hrLeaveSearchPageSizeDefault());
		if (leaveAllotmentGetRequest.getPageSize() != null)
			pageSize = leaveAllotmentGetRequest.getPageSize();
		preparedStatementValues.add(pageSize); // Set limit to pageSize

		// handle offset here
		selectQuery.append(" OFFSET ?");
		int pageNumber = 0; // Default pageNo is zero meaning first page
		if (leaveAllotmentGetRequest.getPageNumber() != null)
			pageNumber = leaveAllotmentGetRequest.getPageNumber() - 1;
		preparedStatementValues.add(pageNumber * pageSize); // Set offset to pageNo * pageSize
	}

	/**
	 * This method is always called at the beginning of the method so that and
	 * is prepended before the field's predicate is handled.
	 * 
	 * @param appendAndClauseFlag
	 * @param queryString
	 * @return boolean indicates if the next predicate should append an "AND"
	 */
	private boolean addAndClauseIfRequired(boolean appendAndClauseFlag, StringBuilder queryString) {
		if (appendAndClauseFlag)
			queryString.append(" AND");

		return true;
	}

	private static String getIdQuery(List<Long> idList) {
		StringBuilder query = new StringBuilder("(");
		if (idList.size() >= 1) {
			query.append(idList.get(0).toString());
			for (int i = 1; i < idList.size(); i++) {
				query.append(", " + idList.get(i));
			}
		}
		return query.append(")").toString();
	}
}