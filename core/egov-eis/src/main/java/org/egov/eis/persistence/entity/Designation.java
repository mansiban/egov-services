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

package org.egov.eis.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.egov.eis.persistence.regex.Constants;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "eg_designation")
@SequenceGenerator(name = Designation.SEQ_DESIGNATION, sequenceName = Designation.SEQ_DESIGNATION, allocationSize = 1)
@NamedQuery(name = "getDesignationForListOfDesgNames", query = "from Designation where trim(upper(name)) in(:param_0)")
@JsonIgnoreProperties(value = { "handler", "hibernateLazyInitializer" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Designation extends AbstractAuditable {

	public static final String SEQ_DESIGNATION = "SEQ_EG_DESIGNATION";
	private static final long serialVersionUID = -3775503109625394145L;
	@Id
	@GeneratedValue(generator = SEQ_DESIGNATION, strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotBlank
	@SafeHtml
	@Pattern(regexp = Constants.ALLTYPESOFALPHABETS_WITHMIXEDCHAR, message = "Name should contain letters with space and (-,_)")
	private String name;
	@NotBlank
	@SafeHtml
	private String code;
	@SafeHtml
	private String description;
	@JoinColumn(name = "chartofaccounts")
	private Long chartOfAccounts;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "egeis_desig_rolemapping", joinColumns = @JoinColumn(name = "designationid"), inverseJoinColumns = @JoinColumn(name = "roleid"))
	private Set<Role> roles = new HashSet<>();
	
	@Column(name = "tenantid")
    private String tenantId;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}
}