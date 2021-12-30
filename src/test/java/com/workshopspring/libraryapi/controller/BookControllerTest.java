package com.workshopspring.libraryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.services.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.regex.Matcher;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("API - should create a book with success")
    public void createBookTest() throws Exception {
        var command = new CreateBookCommand("My book", "Gustavo", "001");
        var savedBook = new Book(1L, "My book", "Gustavo", "001");
        BDDMockito.given(service.save(Mockito.any(CreateBookCommand.class))).willReturn(savedBook);

        var json = new ObjectMapper().writeValueAsString(command);

        var request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(savedBook.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(command.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(command.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(command.getIsbn()))
                .andExpect(MockMvcResultMatchers.header().exists("location"));
    }

    @Test
    @DisplayName("API - should throw validation exception when try create a book")
    public void createInvalidBookTest() throws Exception {
        var json = new ObjectMapper().writeValueAsString(new CreateBookCommand());

        var request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }
}