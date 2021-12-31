package com.workshopspring.libraryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshopspring.libraryapi.TestBase;
import com.workshopspring.libraryapi.commands.CreateBookCommand;
import com.workshopspring.libraryapi.exceptions.DuplicatedISBN;
import com.workshopspring.libraryapi.exceptions.ResourceNotFoundException;
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

import java.util.Objects;
import java.util.Optional;

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
        var request = this.createPostRequest(json);

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
        var request = this.createPostRequest(json);

        mvc.perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    @DisplayName("API - should throw domain exception when try create two book with same isn")
    public void createBookWithDuplicatedIsbnTest() throws Exception {
        var command = this.makeCreateBookCommand();
        var json = new ObjectMapper().writeValueAsString(command);
        var errorMessage = "Duplicated ISBN";
        BDDMockito.given(service.save(Mockito.any(CreateBookCommand.class)))
                .willThrow(new DuplicatedISBN(errorMessage, "Book"));

        var request = this.createPostRequest(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(errorMessage))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("API - should recover book details")
    public void findBookDetailsTest() throws Exception {
        var id = Long.valueOf(1L);
        var book = this.makeBook();

        BDDMockito.given(service.getById(id)).willReturn(book);
        var request = this.createGetRequest("/1");

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("API - should throw exception when try find nonexistent book")
    public void notFoundBookTest() throws Exception {
        var id = Long.valueOf(1L);
        var book = this.makeBook();

        BDDMockito.given(service.getById(Mockito.anyLong())).willThrow(new ResourceNotFoundException(id));
        var request = this.createGetRequest("/1");

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    @DisplayName("API - should delete a book")
    public void deleteBookTest() throws Exception {
        var id = Long.valueOf(1L);

        BDDMockito.mock(BookService.class).delete(id);
        var request = this.createDeleteRequest("/1");

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("API - should throw exception when try delete a nonexistent book")
    public void deleteNonExistentBookTest() throws Exception {
        var id = Long.valueOf(1L);

        BDDMockito.willThrow(new ResourceNotFoundException(id)).given(service).delete(id);
        var request = this.createDeleteRequest("/1");

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    @DisplayName("API - should update a book")
    public void updateBookTest() throws Exception {
        var id = Long.valueOf(1L);
        var updatedBook = this.makeBook();
        var command = this.makeUpdateBookCommand();
        updatedBook.setAuthor(command.getAuthor());
        updatedBook.setTitle(command.getTitle());
        BDDMockito.given(service.update(id, command)).willReturn(updatedBook);

        var json = new ObjectMapper().writeValueAsString(command);
        var request = this.createPutRequest("/1", json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(updatedBook.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(updatedBook.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(updatedBook.getIsbn()));
    }

    @Test
    @DisplayName("API - should throw exception when try update a nonexistent book")
    public void updateNonExistentBookTest() throws Exception {
        var command = makeUpdateBookCommand();
        var id = Long.valueOf(1L);

        BDDMockito.willThrow(new ResourceNotFoundException(id)).given(service).update(Mockito.anyLong(), command);
        var payload = new ObjectMapper().writeValueAsString(command);
        var request = this.createPutRequest("/1", payload);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    private MockHttpServletRequestBuilder createPostRequest(String content) {
         var request = MockMvcRequestBuilders.post(BOOK_API);
         return this.createRequest(request, content);
    }

    private MockHttpServletRequestBuilder createGetRequest(String path) {
        var request = MockMvcRequestBuilders.get(BOOK_API.concat(path));
        return this.createRequest(request, null);
    }

    private MockHttpServletRequestBuilder createDeleteRequest(String path) {
        var request = MockMvcRequestBuilders.delete(BOOK_API.concat(path));
        return this.createRequest(request, null);
    }

    private MockHttpServletRequestBuilder createPutRequest(String path, String content) {
        var request = MockMvcRequestBuilders.put(BOOK_API.concat(path));
        return this.createRequest(request, content);
    }

    private MockHttpServletRequestBuilder createRequest(MockHttpServletRequestBuilder request, String content) {
        request.contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        if(!Objects.isNull(content)) request.content(content);

        return request;
    }
}
