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

package org.egov.user.persistence.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "eg_role")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Role extends AbstractAuditable {
    private static final long serialVersionUID = -3174785753813418004L;

    public Role(org.egov.user.domain.model.Role domainRole) {
        this.id = domainRole.getId();
        this.name = domainRole.getName();
        this.code= domainRole.getCode();
        this.description = domainRole.getDescription();
        this.setLastModifiedDate(domainRole.getLastModifiedDate());
        this.setCreatedDate(domainRole.getCreatedDate());
        this.tenantId=domainRole.getTenantId();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "tenantid")
    private String tenantId;

    public org.egov.user.domain.model.Role toDomain() {
        return org.egov.user.domain.model.Role.builder()
                .id(id)
                .name(name)
                .code(code)
                .description(description)
                .createdDate(getCreatedDate())
                .lastModifiedDate(getLastModifiedDate())
                .createdBy(getCreatedBy())
                .tenantId(tenantId)
                .lastModifiedBy(getLastModifiedBy()).build();
    }
}
