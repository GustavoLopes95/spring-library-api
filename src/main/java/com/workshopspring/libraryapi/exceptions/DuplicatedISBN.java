package com.workshopspring.libraryapi.exceptions;

import java.util.Map;

public class DuplicatedISBN extends DomainException {

    public DuplicatedISBN(String message, String entity) {
        super(message, entity);
        this.errors = Map.of("ISN", "ISBN already registered!");
    }
}
