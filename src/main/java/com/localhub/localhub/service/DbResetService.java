package com.localhub.localhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("local")
public class DbResetService {

    private final JdbcTemplate template;


    @Transactional
    public int reset() {

        String db = template.queryForObject("SELECT DATABASE()", String.class);

        List<String> tables = template.queryForList(
                """
                        SELECT table_name FROM information_schema.tables
                        WHERE table_schema = ? AND table_type = 'BASE TABLE'"
                        """,
                String.class, db
        );

        template.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String t : tables) {
            template.execute("TRUNCATE TABLE '" + t + "'");
        }
        template.execute("SET FOREIGN_KEY_CHECKS = 1");
        return tables.size();
    }
}
