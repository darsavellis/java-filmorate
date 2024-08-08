package ru.yandex.practicum.filmorate.service.impl;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;
import java.util.List;

public class BaseDirectorService implements DirectorService {
    @Override
    public Collection<Director> getDirectors() {
        return List.of();
    }

    @Override
    public Director getDirectorById(long directorId) {
        return null;
    }

    @Override
    public Director createDirector(Director director) {
        return null;
    }

    @Override
    public Director updateDirector(Director director) {
        return null;
    }

    @Override
    public Director deleteDirector(long directorId) {
        return null;
    }
}
