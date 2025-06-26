package com.surplusfood.ordermanagement.order_service.infrastructure.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.surplusfood.ordermanagement.order_service.infrastructure.persistence.repository"
)
@EntityScan(
        basePackages = "com.surplusfood.ordermanagement.order_service.infrastructure.persistence.entity"
)
public class PersistenceConfig {
    // Currently relying on Spring Boot's auto-configuration for:
    // - DataSource
    // - EntityManagerFactory
    // - PlatformTransactionManager
}
