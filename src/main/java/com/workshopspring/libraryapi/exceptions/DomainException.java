package com.workshopspring.libraryapi.exceptions;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class DomainException extends RuntimeException {
    private String entity;
    protected Map<String, String> errors;

    public DomainException(String message, String entity) {
        super(message);
        this.entity = entity;
    }
}
