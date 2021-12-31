package com.workshopspring.libraryapi.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookCommand {

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;
}
