package com.workshopspring.libraryapi.services.impl;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.repositories.BookRepository;
import com.workshopspring.libraryapi.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {


    private BookRepository repository;
    private ModelMapper mapper;

    @Autowired
    public BookServiceImpl(BookRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(CreateBookCommand command) {
        var book = mapper.map(command, Book.class);
        return repository.save(book);
    }
}
