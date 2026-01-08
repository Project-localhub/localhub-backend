package com.localhub.localhub.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mysql.datasource")
@Getter
public class MysqlDataSourceProperties {
    private String url;
    private String username;
    private String password;
}