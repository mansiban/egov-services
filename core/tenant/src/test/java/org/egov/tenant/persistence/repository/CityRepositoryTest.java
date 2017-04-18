package org.egov.tenant.persistence.repository;

import org.egov.tenant.domain.model.City;
import org.egov.tenant.persistence.repository.builder.CityQueryBuilder;
import org.egov.tenant.persistence.rowmapper.CityRowMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CityRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private CityQueryBuilder cityQueryBuilder;

    private CityRepository cityRepository;

    @Before
    public void setUp() throws Exception {
        cityRepository = new CityRepository(jdbcTemplate, cityQueryBuilder);
    }

    @Test
    public void test_should_save_city() {
        when(cityQueryBuilder.getInsertQuery()).thenReturn("insert query");

        List<Object> fields = new ArrayList<Object>() {{
            add("name");
            add("localname");
            add("districtcode");
            add("districtname");
            add("regionname");
            add(37.345);
            add(75.232);
            add("tenantCode");
            add(1L);
            add(1L);
        }};

        when(jdbcTemplate.update(
                eq("insert query"),
                eq("name"),
                eq("localname"),
                eq("districtcode"),
                eq("districtname"),
                eq("regionname"),
                eq(37.345),
                eq(75.232),
                eq("tenantCode"),
                eq(1L),
                any(Date.class),
                eq(1L),
                any(Date.class))
        ).thenReturn(1);

        City city = City.builder()
                .name("name")
                .localName("localname")
                .districtCode("districtcode")
                .districtName("districtname")
                .regionName("regionname")
                .longitude(37.345)
                .latitude(75.232)
                .build();

        cityRepository.save(city, "tenantCode");

        verify(jdbcTemplate).update(
                eq("insert query"),
                eq("name"),
                eq("localname"),
                eq("districtcode"),
                eq("districtname"),
                eq("regionname"),
                eq(37.345),
                eq(75.232),
                eq("tenantCode"),
                eq(1L),
                any(Date.class),
                eq(1L),
                any(Date.class)
        );
    }

    @Test
    public void test_should_retrieve_city() {
        City cityModel = mock(City.class);
        org.egov.tenant.persistence.entity.City cityEntity = mock(org.egov.tenant.persistence.entity.City.class);
        when(cityEntity.toDomain()).thenReturn(cityModel);

        when(cityQueryBuilder.getSelectQuery("tenantCode")).thenReturn("select query");
        List<org.egov.tenant.persistence.entity.City> listOfEntity = Collections.singletonList(cityEntity);
        when(jdbcTemplate.query(eq("select query"), any(CityRowMapper.class))).thenReturn(listOfEntity);

        City result = cityRepository.find("tenantCode");

        assertThat(result).isEqualTo(cityModel);
    }

}