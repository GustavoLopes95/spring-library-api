package com.workshopspring.libraryapi.service;

import com.workshopspring.libraryapi.TestBase;
import com.workshopspring.libraryapi.entity.Book;
import com.workshopspring.libraryapi.exceptions.DuplicatedISBN;
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
        var savedBook = new Book(1L,"Clean Code", "Robert C. Martin", "001");

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(savedBook);

        var entity = service.save(command);

        Assertions.assertThat(entity.getId()).isNotNull();
        Assertions.assertThat(entity.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(entity.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(entity.getIsbn()).isEqualTo(book.getIsbn());
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
}
