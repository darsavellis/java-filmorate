package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseUserService implements UserService {
    static final String USER_ID_NOT_FOUND = "User ID=%s not found";
    static final String NOT_FOUND_EVENTS_FOR_USER = "Not found events for user %s";

    final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_NOT_FOUND, userId)));
    }

    @Override
    public List<Event> getEventsOfUser(Long userId) {
        Collection<Event> event = userRepository.getUserEvents(userId);
        if (event.isEmpty()) {
            throw new NotFoundException(String.format(NOT_FOUND_EVENTS_FOR_USER, userId));
        }

        return userRepository.getUserEvents(userId);
    }

    @Override
    public User createUser(User user) throws ValidationException {
        UserValidator.validate(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User newUser) throws ValidationException {
        UserValidator.validate(newUser);
        userRepository.getById(newUser.getId())
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_NOT_FOUND, newUser.getId())));

        return userRepository.update(newUser);
    }

    @Override
    public User deleteUserById(long userId) throws ValidationException {
        UserValidator.validate(getUserById(userId));
        User user = userRepository.getById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_NOT_FOUND, userId)));

        userRepository.delete(userId);
        return user;
    }
}
