package com.workshopspring.libraryapi.services.impl;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.commands.UpdateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.exceptions.DuplicatedISBN;
import com.workshopspring.libraryapi.exceptions.ResourceNotFoundException;
import com.workshopspring.libraryapi.repositories.BookRepository;
import com.workshopspring.libraryapi.services.BookService;
import org.hibernate.validator.constraints.ISBN;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
        if(repository.existsByIsbn(command.getIsbn())) throw new DuplicatedISBN("Duplicated ISBN", "Book");
        var book = mapper.map(command, Book.class);
        return repository.save(book);
    }

    @Override
    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public Book update(Long id, UpdateBookCommand book) {
        return null;
    }

    @Override
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    @Override
    public void updateData(Book entity, UpdateBookCommand command) {
        entity.setTitle(command.getTitle());
        entity.setAuthor(command.getAuthor());
    }
}
