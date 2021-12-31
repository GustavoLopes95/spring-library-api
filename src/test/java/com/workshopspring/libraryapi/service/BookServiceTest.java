package com.workshopspring.libraryapi.service;

import com.workshopspring.libraryapi.TestBase;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.exceptions.DuplicatedISBN;
import com.workshopspring.libraryapi.exceptions.ResourceNotFoundException;
import com.workshopspring.libraryapi.repositories.BookRepository;
import com.workshopspring.libraryapi.services.BookService;
import com.workshopspring.libraryapi.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = BookServiceTestConfig.class)
public class BookServiceTest extends TestBase {

    BookService service;

    @Autowired
    private ModelMapper mapper;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository, mapper);
    }

    @Test
    @DisplayName("Service - Should save book")
    public void shouldSaveBookTest() {
        var command = this.makeCreateBookCommand();
        var book = this.makeBook();
        var savedBook = this.makeBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(savedBook);

        var response = service.save(command);

        Assertions.assertThat(response.getId()).isEqualTo(savedBook.getId());
        Assertions.assertThat(response.getAuthor()).isEqualTo(savedBook.getAuthor());
        Assertions.assertThat(response.getTitle()).isEqualTo(savedBook.getTitle());
        Assertions.assertThat(response.getIsbn()).isEqualTo(savedBook.getIsbn());
    }

    @Test
    @DisplayName("Service - should throw domain exception when try create two book with same isn")
    public void createBookWithDuplicatedIsnTest() {
        var command = this.makeCreateBookCommand();
        var book = this.makeBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        var exception = Assertions.catchThrowable(() -> service.save(command));
        Assertions.assertThat(exception)
                .isInstanceOf(DuplicatedISBN.class)
                .hasMessage("Duplicated ISBN");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Service - should get a book by id")
    public void findByIdTest() {
        var book = this.makeBook();

        Mockito.when(repository.findById(book.getId())).thenReturn(Optional.of(book));

        var response = service.findById(book.getId());

        Assertions.assertThat(response.getId()).isEqualTo(book.getId());
        Assertions.assertThat(response.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(response.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(response.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Service - should throw not found exception when try find a nonexistent book")
    public void findByIdNonExistentBookTest() {
        var book = this.makeBook();

        Mockito.when(repository.findById(book.getId())).thenThrow(new ResourceNotFoundException(book.getId()));
        var exception = Assertions.catchThrowable(() -> service.findById(book.getId()));
        Assertions.assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found id: " + book.getId());
    }


    @Test
    @DisplayName("Service - should delete a book")
    public void deleteBookTest() {
        var book = this.makeBook();

        Mockito.mock(BookRepository.class).deleteById(book.getId());
        service.delete(book.getId());
        Mockito.when(repository.findById(book.getId())).thenThrow(new ResourceNotFoundException(book.getId()));
        var exception = Assertions.catchThrowable(() -> service.findById(book.getId()));

        Mockito.verify(repository, Mockito.times(1)).deleteById(book.getId());
        Assertions.assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found id: " + book.getId());
    }

    @Test
    @DisplayName("Service - should throw not found exception when try delete a nonexistent book")
    public void deleteNonExistentBookTest() {
        var book = this.makeBook();

        Mockito.doThrow(new ResourceNotFoundException(book.getId())).when(repository).deleteById(book.getId());
        var exception = Assertions.catchThrowable(() -> service.delete(book.getId()));

        Assertions.assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found id: " + book.getId());
    }

    @Test
    @DisplayName("Service - should update a book")
    public void updateBookTest() {
        var book = this.makeBook();
        var command = this.makeUpdateBookCommand();
        var updatedBook = this.makeBook();
        updatedBook.setAuthor(command.getAuthor());
        updatedBook.setTitle(command.getTitle());
        Mockito.when(repository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(repository.save(updatedBook)).thenReturn(updatedBook);
        var entity = service.findById(book.getId());
        service.updateData(entity, command);
        var response = repository.save(entity);

        Mockito.verify(repository, Mockito.times(1)).findById(book.getId());
        Mockito.verify(repository, Mockito.times(1)).save(updatedBook);

        Assertions.assertThat(response.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(response.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(response.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(response.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Service - should throw not found exception when try update a nonexistent book")
    public void updateNonExistentBookTest() {
        var book = this.makeBook();
        var command = this.makeUpdateBookCommand();
        var updatedBook = this.makeBook();
        updatedBook.setAuthor(command.getAuthor());
        updatedBook.setTitle(command.getTitle());
        Mockito.when(repository.findById(book.getId())).thenThrow(new ResourceNotFoundException(book.getId()));

        var exception = Assertions.catchThrowable(() -> service.findById(book.getId()));

        Mockito.verify(repository, Mockito.times(1)).findById(book.getId());
        Mockito.verify(repository, Mockito.never()).save(updatedBook);
        Assertions.assertThat(exception)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found id: " + book.getId());
    }
}
