package com.example.poc_java_db_integration_tests.repository;

import com.example.poc_java_db_integration_tests.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.author LIKE %:keyword% OR b.title LIKE %:keyword%")
    List<Book> searchByKeyword(String keyword);
}
