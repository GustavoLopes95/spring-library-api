package com.workshopspring.libraryapi.services;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import org.springframework.stereotype.Service;

public interface BookService {
    Book save(CreateBookCommand book);
}
