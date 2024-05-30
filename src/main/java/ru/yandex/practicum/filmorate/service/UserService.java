package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User createUser(User user) {
        try {
            userValidator(user);
            user.setId(getNextID());
            log.debug("ID {} assigned to new user", user.getId());
            users.put(user.getId(), user);
            log.debug("User with ID {} is added to storage", user.getId());
            return user;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public User updateUser(User newUser) {
        try {
            if (users.containsKey(newUser.getId())) {
                userValidator(newUser);
                log.info("Validation passed");

                User oldUser = users.get(newUser.getId());

                oldUser.setEmail(newUser.getEmail());
                log.debug("User {} email updated to {}", oldUser.getId(), oldUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                log.debug("User {} login updated to {}", oldUser.getId(), oldUser.getLogin());
                oldUser.setName(newUser.getName());
                log.debug("User {} name updated to {}", oldUser.getId(), oldUser.getName());
                oldUser.setBirthday(newUser.getBirthday());
                log.debug("User {} birthday updated to {}", oldUser.getId(), oldUser.getBirthday());
                return oldUser;
            } else {
                throw new NotFoundException("User not found in storage");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    void userValidator(User user) {
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
    }

    boolean isEmailNotValid(String email) {
        return email.isBlank() || !email.contains("@");
    }

    boolean isLoginNotValid(String login) {
        return login.isBlank() || login.contains(" ");
    }

    boolean isNameEmpty(String name) {
        return name == null || name.isBlank();
    }

    boolean isBirthdayNotValid(LocalDate birthday) {
        return !LocalDate.now().isAfter(birthday);
    }

    long getNextID() {
        long currentMaxID = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}
