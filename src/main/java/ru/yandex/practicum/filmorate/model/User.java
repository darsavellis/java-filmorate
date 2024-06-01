package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    String login;
    @NotNull
    String name;
    @NotNull
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
}
