package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(long userId);

    List<Event> getEventsOfUser(Long userId);

    User createUser(User user);

    User updateUser(User newUser);

    User deleteUserById(long id);
}
