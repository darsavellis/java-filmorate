package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorRepository {
    List<Director> getAll();

    Optional<Director> getById(long directorId);

    Set<Director> getByIds(List<Long> directorIds);

    Director save(Director director);

    Director update(Director newDirector);

    boolean delete(long directorId);
}
