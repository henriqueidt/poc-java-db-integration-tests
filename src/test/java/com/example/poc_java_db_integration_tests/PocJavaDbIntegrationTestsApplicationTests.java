package com.example.poc_java_db_integration_tests;

import com.example.poc_java_db_integration_tests.model.Book;
import com.example.poc_java_db_integration_tests.repository.BookRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTest {

	@LocalServerPort
	private Integer port;

	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

	@BeforeAll
	static void beforeAll() {
		postgreSQLContainer.start();
	}

	@AfterAll
	static void afterAll() {
		postgreSQLContainer.stop();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Autowired
	BookRepository bookRepository;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost:" + port;
		bookRepository.deleteAll();
	}

	@Test
	void shouldGetAllBooks() {
		List<Book> books = List.of(
				new Book("The Hobbit", "J.R.R. Tolkien"),
				new Book("1984", "George Orwell"),
				new Book("To Kill a Mockingbird", "Harper Lee")
		);
		bookRepository.saveAll(books);

		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/books")
				.then()
				.statusCode(200)
				.body(".", hasSize(3));
	}
}