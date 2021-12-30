package com.workshopspring.libraryapi;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.entity.Book;

public class TestBase {

    protected CreateBookCommand makeCreateBookCommand() {
        return new CreateBookCommand("My book", "Gustavo", "001");
    }

    protected Book makeBook() {
        return new Book(1L,"Clean Code", "Robert C. Martin", "001");
    }
}
