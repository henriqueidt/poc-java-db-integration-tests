package com.example.poc_java_db_integration_tests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.example.poc_java_db_integration_tests.repository")
@EntityScan("com.example.poc_java_db_integration_tests.model")
@SpringBootApplication
public class PocJavaDbIntegrationTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PocJavaDbIntegrationTestsApplication.class, args);
	}

}
