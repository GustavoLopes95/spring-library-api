package com.workshopspring.libraryapi.services;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.commands.UpdateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface BookService {
    Book save(CreateBookCommand book);

    Book getById(Long id);

    void delete(Long id);

    Book update(Long id, UpdateBookCommand book);
}
