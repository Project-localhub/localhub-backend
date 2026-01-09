package com.localhub.localhub.config;

import org.springframework.beans.factory.annotation.Value;
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
public class PostgresSqlDatabaseConfig {


    @Value("${POSTGRE_DB_URL}")
    String url;
    @Value("${POSTGRE_DB_USERNAME}")
    String username;
    @Value("${POSTGRE_DB_PASSWORD}")
    String password;

    @Bean(name = "postgisDataSource")
    public DataSource postgisDataSource() {

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}
