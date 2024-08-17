package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    List<Director> getAll();

    Optional<Director> getById(long directorId);

    List<Director> getByIds(List<Long> directorIds);

    Director save(Director director);

    Director update(Director newDirector);

    boolean delete(long directorId);

    List<Director> getDirectorsByFilmId(long filmId);
}
