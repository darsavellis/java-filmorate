package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserValidator {
    public static void validate(User user) {
        if (isEmailNotValid(user.getEmail())) {
            throw new ValidationException("Email should not be empty and must contain @ symbol");
        }
        if (isLoginNotValid(user.getLogin())) {
            throw new ValidationException(("Login should not be empty or blank"));
        }
        if (isBirthdayNotValid(user.getBirthday())) {
            throw new ValidationException("Birthday should not be in the future");
        }
        if (isNameEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
        log.info("User validation passed");
    }

    static boolean isEmailNotValid(String email) {
        return email.isBlank() || !email.contains("@");
    }

    static boolean isLoginNotValid(String login) {
        return login.isBlank();
    }

    static boolean isNameEmpty(String name) {
        return Objects.isNull(name) || name.isBlank();
    }

    static boolean isBirthdayNotValid(LocalDate birthday) {
        return Objects.isNull(birthday);
    }
}
