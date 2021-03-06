package org.egov.tenant.persistence.rowmapper;


import org.egov.tenant.domain.model.TenantType;
import org.egov.tenant.persistence.entity.Tenant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.egov.tenant.persistence.entity.Tenant.*;

public class TenantRowMapper implements RowMapper<Tenant> {

    @Override
    public Tenant mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        return builder()
            .id(rs.getLong(ID))
            .code(rs.getString(CODE))
            .description(rs.getString(DESCRIPTION))
            .domainUrl(rs.getString(DOMAIN_URL))
            .logoId(rs.getString(LOGO_ID))
            .imageId(rs.getString(IMAGE_ID))
            .type(TenantType.valueOf(rs.getString(TYPE)))
            .build();
    }
}
