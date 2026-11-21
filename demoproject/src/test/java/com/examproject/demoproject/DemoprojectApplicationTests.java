package com.examproject.demoproject;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Start Spring for this test. The Docker database must be running.
@SpringBootTest
class DemoprojectApplicationTests
{
    // Spring supplies JdbcTemplate with the configured database connection.
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection_runSimpleQuery_returnsOne()
    {
        Integer expectedResult = Integer.valueOf(1);

        // Check the connection without needing tables or changing data.
        Integer actualResult = this.jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        assertEquals(expectedResult, actualResult);
    }
}