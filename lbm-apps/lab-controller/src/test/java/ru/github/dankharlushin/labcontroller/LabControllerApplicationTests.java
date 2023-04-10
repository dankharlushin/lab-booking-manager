package ru.github.dankharlushin.labcontroller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class LabControllerApplicationTests {

    @Container
    public static PostgreSQLContainer<?> postgresDb = new PostgreSQLContainer<>("postgres:12.11")
            .withDatabaseName("lbm_test")
            .withUsername("lbm")
            .withPassword("lbm")
            .withInitScript("ddl.sql");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDb::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDb::getUsername);
        registry.add("spring.datasource.password", postgresDb::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        JdbcDatabaseDelegate containerDelegate = new JdbcDatabaseDelegate(postgresDb, "");
        ScriptUtils.runInitScript(containerDelegate, "mock_data.sql");
    }

    @Test
    void name() {

    }
}
