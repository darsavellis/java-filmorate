package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorRepository {
    List<Director> getAll();

    Director getById(long directorId);

    Director save(Director director);

    Director update(Director director);

    boolean delete(long directorId);
}
