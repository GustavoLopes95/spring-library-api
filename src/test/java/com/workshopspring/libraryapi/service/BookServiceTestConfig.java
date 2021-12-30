package com.workshopspring.libraryapi.service;

import com.workshopspring.libraryapi.repositories.BookRepository;
import com.workshopspring.libraryapi.services.BookService;
import com.workshopspring.libraryapi.services.impl.BookServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookServiceTestConfig {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }
}
