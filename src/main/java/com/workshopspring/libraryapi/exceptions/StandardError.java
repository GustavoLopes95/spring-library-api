package com.workshopspring.libraryapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardError<T> {

    private String message;
    private List<T> errors;

    public StandardError(String message) {
        this.message = message;
        this.errors = new ArrayList<>();
    }
}
