package com.x2bee.batch;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan(basePackages = "com.x2bee.batch.app.repository")
class FrameworkDemoBatchApplicationTests {

	@Test
	void contextLoads() {
	}

}
