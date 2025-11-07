package com.example.poc_java_db_integration_tests.controller;

import com.example.poc_java_db_integration_tests.model.Book;
import com.example.poc_java_db_integration_tests.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {


    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public Iterable getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/search")
    public List searchBooksByKeyword(@RequestParam String keyword) {
        return bookRepository.searchByKeyword(keyword);
    }

    @GetMapping("/title/{title}")
    public List getBooksByTitle(@PathVariable String title) {
        return bookRepository.findByTitle(title);
    }

    @GetMapping("/author/{author}")
    public List getBooksByAuthor(@PathVariable String author) {
        return bookRepository.findByAuthor(author);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookRepository.findById(id).ifPresent(bookRepository::delete);
    }
}
