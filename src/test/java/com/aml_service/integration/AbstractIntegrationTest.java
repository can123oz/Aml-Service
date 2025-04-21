package com.aml_service.integration;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestPropertySource(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16.0"))
            .withDatabaseName("aml-service-db-test")
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test");

    static DockerImageName kafkaImage = DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
            .asCompatibleSubstituteFor("confluentinc/cp-kafka");

    @Container
    static final KafkaContainer kafka = new KafkaContainer(kafkaImage)
            .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true");


    static {
        postgres.start();
        kafka.start();
        System.out.println("🚀 PostgreSQL Testcontainers running on: " + postgres.getJdbcUrl());
        System.out.println("🔗 PostgreSQL Connect using: Host=" + postgres.getHost() + " | Port=" + postgres.getMappedPort(5432));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


}
