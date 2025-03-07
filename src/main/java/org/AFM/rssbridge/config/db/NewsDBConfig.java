package org.AFM.rssbridge.config.db;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.AFM.rssbridge.news.repository",
        entityManagerFactoryRef = "newsEntityManagerFactory",
        transactionManagerRef = "newsTransactionManager"
)
public class NewsDBConfig {
    private static final Logger logger = LoggerFactory.getLogger(NewsDBConfig.class);

    @Bean(name = "newsDataSourceProperties")
    @ConfigurationProperties("spring.datasource.news")
    public DataSourceProperties newsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "newsDataSource")
    public DataSource newsDataSource(@Qualifier("newsDataSourceProperties") DataSourceProperties properties) {
        logger.info("Attempting to connect to News Database: {}", properties.getUrl());
        try {
            HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
            logger.info("Successfully created News Database connection pool.");
            return dataSource;
        } catch (Exception e) {
            logger.error("Failed to connect to News Database", e);
            throw e;
        }
    }

    @Bean(name = "newsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("newsDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("org.AFM.rssbridge.news.model")
                .persistenceUnit("newsDB")
                .build();
    }

    @Bean(name = "newsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("newsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
