package com.workshopspring.libraryapi;

import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.commands.UpdateBookCommand;
import com.workshopspring.libraryapi.entity.Book;

public class TestBase {

    protected CreateBookCommand makeCreateBookCommand() {
        return new CreateBookCommand("Clean Code", "Robert C. Martin", "001");
    }

    protected UpdateBookCommand makeUpdateBookCommand() {
        return new UpdateBookCommand("Refactoring", "Robert C. Martin");
    }

    protected Book makeBook() {
        return new Book(1L,"Clean Code", "Robert C. Martin", "001");
    }
}
