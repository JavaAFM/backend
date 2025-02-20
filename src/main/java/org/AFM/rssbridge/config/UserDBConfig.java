package org.AFM.rssbridge.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.AFM.rssbridge.user.repository",
        entityManagerFactoryRef = "usersEntityManagerFactory",
        transactionManagerRef = "usersTransactionManager"
)
public class UserDBConfig {

    private final Logger logger = LoggerFactory.getLogger(UserDBConfig.class);
    @Primary
    @Bean(name = "userDataSourceProperties")
    @ConfigurationProperties("spring.datasource.user")
    public DataSourceProperties userDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "userDataSource")
    public DataSource userDataSource(@Qualifier("userDataSourceProperties") DataSourceProperties properties) {
        logger.info("Attempting to connect to User Database: {}", properties.getUrl());

        try {
            HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
            logger.info("Successfully created User Database connection pool.");
            return dataSource;
        } catch (Exception e) {
            logger.error("Failed to connect to User Database", e);
            throw e;
        }
    }

    @Primary
    @Bean(name = "usersEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("userDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("org.AFM.rssbridge.user.model")
                .persistenceUnit("usersDB")
                .build();
    }

    @Primary
    @Bean(name = "usersTransactionManager")
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

