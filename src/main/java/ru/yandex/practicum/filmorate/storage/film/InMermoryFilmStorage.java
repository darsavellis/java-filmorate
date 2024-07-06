package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMermoryFilmStorage implements FilmStorage {
    final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextID());
        log.debug("ID {} assigned to new film", film.getId());
        films.put(film.getId(), film);
        log.info("Film with ID {} is added to storage", film.getId());
        return film;
    }

    @Override
    public Film removeFilm(Film film) {
        return films.remove(film.getId());
    }

    @Override
    public Film updateFilm(Film newFilm) throws NotFoundException {
        if (isFilmExist(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            log.debug("Film {} name updated to {}", newFilm.getId(), newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            log.debug("Film {} description updated to {}", newFilm.getId(), newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            log.debug("Film {} duration updated to {}", newFilm.getId(), newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.debug("Film {} release date updated to {}", newFilm.getId(), newFilm.getReleaseDate());
            return oldFilm;
        } else {
            throw new NotFoundException("Film not found in storage");
        }
    }

    @Override
    public Film getFilmById(long filmId) {
        return films.get(filmId);
    }

    @Override
    public boolean isFilmExist(long filmId) {
        return films.containsKey(filmId);
    }

    long getNextID() {
        long currentMaxID = films.values().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}
