package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    Collection<Director> getDirectors();

    Director getDirectorById(long directorId);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    Director deleteDirector(long directorId);
}
