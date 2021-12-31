package com.workshopspring.libraryapi.controllers;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.commands.UpdateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService service;

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody CreateBookCommand command) {
        var book = service.save(command);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/id").buildAndExpand(book.getId())
                .toUri();
        return ResponseEntity.created(uri).body(book);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Book> find(@PathVariable Long id) {
        var book = service.findById(id);
        return ResponseEntity.ok().body(book);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @Valid @RequestBody UpdateBookCommand command) {
        var book = service.update(id, command);
        return ResponseEntity.ok().body(book);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
