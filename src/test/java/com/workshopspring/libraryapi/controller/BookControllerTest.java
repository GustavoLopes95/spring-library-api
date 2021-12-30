package com.workshopspring.libraryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshopspring.libraryapi.TestBase;
import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.exceptions.DuplicatedISBN;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest extends TestBase {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("API - should create a book with success")
    public void createBookTest() throws Exception {
        var command = this.makeCreateBookCommand();
        var savedBook = this.makeBook();
        BDDMockito.given(service.save(Mockito.any(CreateBookCommand.class))).willReturn(savedBook);

        var json = new ObjectMapper().writeValueAsString(command);
        var request = this.sendRequestToApi(json);

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
        var command = this.makeCreateBookCommand();
        var json = new ObjectMapper().writeValueAsString(command);
        var request = this.sendRequestToApi(json);

        mvc.perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    @DisplayName("API - should throw domain exception when try create two book with same isn")
    public void createBookWithDuplicatedIsnTest() throws Exception {
        var command = this.makeCreateBookCommand();
        var json = new ObjectMapper().writeValueAsString(command);
        var errorMessage = "Duplicated ISBN";
        BDDMockito.given(service.save(Mockito.any(CreateBookCommand.class)))
                .willThrow(new DuplicatedISBN(errorMessage, "Book"));

        var request = this.sendRequestToApi(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(errorMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)));
    }

    private MockHttpServletRequestBuilder sendRequestToApi(String content) {
        return MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);
    }
}
