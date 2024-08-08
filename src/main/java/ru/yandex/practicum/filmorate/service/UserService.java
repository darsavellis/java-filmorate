package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> getUsers();

    User getUserById(long userId);

    User createUser(User user);

    User updateUser(User newUser);

    List<Film> getRecommendations(long userId);
}
