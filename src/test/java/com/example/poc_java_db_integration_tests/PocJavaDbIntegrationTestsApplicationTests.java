package com.example.poc_java_db_integration_tests;

import com.example.poc_java_db_integration_tests.model.Book;
import com.example.poc_java_db_integration_tests.repository.BookRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
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
	private MockMvc mockMvc;

	@Autowired
	BookRepository bookRepository;

	@BeforeEach
	void setup() {
		bookRepository.deleteAll();
	}

	@Test
	void shouldGetAllBooks() throws Exception {
		List<Book> books = List.of(
				new Book("The Hobbit", "J.R.R. Tolkien"),
				new Book("1984", "George Orwell"),
				new Book("To Kill a Mockingbird", "Harper Lee")
		);
		bookRepository.saveAll(books);

		mockMvc.perform(get("/api/books")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)));
	}

	@Test
	void shouldGetAllBooksMatchingKeyword() throws Exception {
		List<Book> books = List.of(
				new Book("The Hobbit", "J.R.R. Tolkien"),
				new Book("1984", "George Orwell"),
				new Book("The lord of the Rings", "J.R.R. Tolkien"),
				new Book("To Kill a Mockingbird", "Harper Lee")
		);

		List<Book> expectedBooks = List.of(
				new Book("The Hobbit", "J.R.R. Tolkien"),
				new Book("The lord of the Rings", "J.R.R. Tolkien")
		);

		ObjectMapper objectMapper = new ObjectMapper();
		String expectedJson = objectMapper.writeValueAsString(expectedBooks);

		bookRepository.saveAll(books);

		mockMvc.perform(get("/api/books/search")
						.param("keyword", "Tolkien")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].title").value("The Hobbit"))
				.andExpect(jsonPath("$[0].author").value("J.R.R. Tolkien"))
				.andExpect(jsonPath("$[1].title").value("The lord of the Rings"))
				.andExpect(jsonPath("$[1].author").value("J.R.R. Tolkien"));



	}
}