package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    long id;
    @Email
    String email;
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]+")
    String login;
    @Pattern(regexp = "[a-zA-Z0-9 ]+")
    String name;
    @NotNull
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
}
