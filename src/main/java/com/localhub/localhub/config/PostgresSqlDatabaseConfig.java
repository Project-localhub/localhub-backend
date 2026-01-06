package com.localhub.localhub.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Profile("!test")
@Configuration
@EnableJpaRepositories(

        basePackages = "com.localhub.localhub.geo",
        entityManagerFactoryRef = "postgresEntityManager",
        transactionManagerRef = "postgresTransactionManager"
)
public class PostgresSqlDatabaseConfig {

    @Bean(name = "postgisDataSource")
    public DataSource postgisDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5433/localhub_postgres")
                .username("postgres")
                .password("1234")
                .build();
    }
    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManager() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(postgisDataSource());
        em.setPackagesToScan(new String[]{"com.localhub.localhub.geo"});
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");


        em.setJpaPropertyMap(properties);

        return em;

    }


    @Bean
    public PlatformTransactionManager postgresTransactionManager() {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(postgresEntityManager().getObject());

        return transactionManager;
    }
}
