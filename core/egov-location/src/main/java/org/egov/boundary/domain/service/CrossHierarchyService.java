/*
 		1* eGov suite of products aim to improve the internal efficiency,transparency,
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

package org.egov.boundary.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.boundary.persistence.entity.Boundary;
import org.egov.boundary.persistence.entity.BoundaryType;
import org.egov.boundary.persistence.entity.CrossHierarchy;
import org.egov.boundary.persistence.repository.CrossHierarchyRepository;
import org.egov.boundary.web.contract.CrossHierarchyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CrossHierarchyService {

	private static final String CROSSHIERARCHY_BOUNDARYTYPES = "CrosshierarchyBoundaryTypes";
	private static final String ADMINISTRATION = "Administration";

	private final CrossHierarchyRepository crossHierarchyRepository;

	@Autowired
	private BoundaryTypeService boundaryTypeService;

	@Autowired
	public CrossHierarchyService(final CrossHierarchyRepository crossHierarchyRepository) {
		this.crossHierarchyRepository = crossHierarchyRepository;
	}

	@Transactional
	public CrossHierarchy create(final CrossHierarchy crossHierarchy) {
		return crossHierarchyRepository.save(crossHierarchy);
	}

	@Transactional
	public CrossHierarchy update(final CrossHierarchy crossHierarchy) {
		return crossHierarchyRepository.save(crossHierarchy);
	}

	@Transactional
	public void delete(final CrossHierarchy crossHierarchy) {
		crossHierarchyRepository.delete(crossHierarchy);
	}

	public List<Boundary> getCrossHierarchyChildrens(final Boundary boundary, final BoundaryType boundaryType) {
		return crossHierarchyRepository.findByParentAndChildBoundaryType(boundary, boundaryType);
	}

	public List<CrossHierarchy> getChildBoundaryNameAndBndryTypeAndHierarchyTypeAndTenantId(
			final String boundaryTypeName, final String hierarchyTypeName, final String parenthierarchyTypeName,
			final String name, final String tenantId) {
		return crossHierarchyRepository.findActiveBoundariesByNameAndBndryTypeNameAndHierarchyTypeNameAndTenantId(
				boundaryTypeName, hierarchyTypeName, parenthierarchyTypeName, name, tenantId);
	}

	public List<Boundary> getChildBoundariesNameAndBndryTypeAndHierarchyType(final String boundaryTypeName,
			final String hierarchyTypeName) {
		return crossHierarchyRepository.findChildBoundariesNameAndBndryTypeAndHierarchyType(boundaryTypeName,
				hierarchyTypeName);
	}

	public List<Boundary> getParentBoundaryByChildBoundaryAndParentBoundaryType(final Long childId,
			final Long parentTypeId) {
		return crossHierarchyRepository.findParentBoundaryByChildBoundaryAndParentBoundaryType(childId, parentTypeId);
	}

	public List<Boundary> getActiveChildBoundariesByBoundaryIdAndTenantId(final Long id, final String tenantId) {
		return crossHierarchyRepository.findActiveBoundariesByIdAndTenantId(id, tenantId);
	}

	public CrossHierarchy findById(final Long id) {
		return crossHierarchyRepository.findOne(id);
	}

	public List<CrossHierarchy> findAllByBoundaryTypes(final BoundaryType parentType, final BoundaryType childType) {
		return crossHierarchyRepository.findByParentTypeAndChildType(parentType, childType);
	}

	public List<Boundary> findChildBoundariesByParentBoundary(final String boundaryTypeName,
			final String hierarchyTypeName, final String parentBoundary) {
		return crossHierarchyRepository.findChildBoundariesByParentBoundary(boundaryTypeName, hierarchyTypeName,
				parentBoundary);
	}

	public CrossHierarchy findAllByParentAndChildBoundary(final Long parentId, final Long childId) {
		return crossHierarchyRepository.findBoundariesByParentAndChildBoundary(parentId, childId);
	}

	public List<BoundaryType> getCrossHierarchyBoundaryTypes() {
		final List<BoundaryType> boundaryTypes = new ArrayList<BoundaryType>();

		/*
		 * final String appConfigValue = appConfigValueService
		 * .getConfigValuesByModuleAndKey(ADMINISTRATION,
		 * CROSSHIERARCHY_BOUNDARYTYPES).get(0).getValue(); final List<String>
		 * configList = new ArrayList<String>();
		 *
		 * if (StringUtils.isNotBlank(appConfigValue)) { final List<String>
		 * boundaryHierarchyType = Arrays.asList(appConfigValue.split(",")); for
		 * (final String bhType : boundaryHierarchyType) configList.add(bhType);
		 * for (final String bhType : configList) { final List<String>
		 * boundaryTypeList = Arrays.asList(bhType.split("-")); final
		 * BoundaryType boundaryType = boundaryTypeService
		 * .getBoundaryTypeByNameAndHierarchyTypeName(boundaryTypeList.get(1),
		 * boundaryTypeList.get(0)); boundaryTypes.add(boundaryType); } }
		 */
		return boundaryTypes;
	}

	public CrossHierarchy findByCodeAndTenantId(String code, String tenantId) {
		return crossHierarchyRepository.findByCodeAndTenantId(code, tenantId);
	}

	public List<CrossHierarchy> getAllCrossHierarchys(CrossHierarchyRequest crossHierarchyRequest) {
		List<CrossHierarchy> crossHierarchy = new ArrayList<CrossHierarchy>();
		if (crossHierarchyRequest.getCrossHierarchy() != null
				&& crossHierarchyRequest.getCrossHierarchy().getTenantId() != null
				&& !crossHierarchyRequest.getCrossHierarchy().getTenantId().isEmpty()) {
			if (crossHierarchyRequest.getCrossHierarchy().getId() != null) {
				crossHierarchy.add(
						crossHierarchyRepository.findByIdAndTenantId(crossHierarchyRequest.getCrossHierarchy().getId(),
								crossHierarchyRequest.getCrossHierarchy().getTenantId()));

			} else {
				if (crossHierarchyRequest.getCrossHierarchy().getCode() != null) {
					crossHierarchy.add(findByCodeAndTenantId(crossHierarchyRequest.getCrossHierarchy().getCode(),
							crossHierarchyRequest.getCrossHierarchy().getTenantId()));

				} else {
					crossHierarchy.addAll(crossHierarchyRepository
							.findAllByTenantId(crossHierarchyRequest.getCrossHierarchy().getTenantId()));
				}
			}
		}
		return crossHierarchy;
	}
}
