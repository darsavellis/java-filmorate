package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    @NotNull
    private String name;
    @Past
    private LocalDate birthday;
}
