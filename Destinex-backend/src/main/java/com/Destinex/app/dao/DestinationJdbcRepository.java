package com.Destinex.app.dao;


import com.Destinex.app.entity.Destination;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DestinationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public DestinationJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Batch insert method
    public void batchInsert(List<Destination> destinations) {
        String sql = "INSERT INTO destination (country, capital, currency, population, image_url, region) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Destination d = destinations.get(i);
                ps.setString(1, d.getCountry());
                ps.setString(2, d.getCapital());
                ps.setString(3, d.getCurrency());
                ps.setLong(4, d.getPopulation());
                ps.setString(5, d.getImageUrl());
                ps.setString(6, d.getRegion());
            }

            @Override
            public int getBatchSize() {
                return destinations.size();
            }
        });
    }
}
