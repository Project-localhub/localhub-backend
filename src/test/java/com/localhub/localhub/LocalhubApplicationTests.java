package com.localhub.localhub;

import com.localhub.localhub.config.TestExternalConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@Import(TestExternalConfig.class)
class LocalhubApplicationTests {

	@Test
	void contextLoads() {
	}

}
