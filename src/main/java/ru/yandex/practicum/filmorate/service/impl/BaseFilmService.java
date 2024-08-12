package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {
    static final String MPA_RATING_ID_NOT_VALID = "MpaRating ID=%s not valid";
    static final String FILM_ID_NOT_FOUND = "Film ID=%s not found";
    public static final String GENRES_NOT_VALID = "Genres %s not valid";
    final FilmRepository filmRepository;
    final MpaRatingRepository mpaRatingRepository;
    final GenreRepository genreRepository;
    final LikeRepository likeRepository;

    public List<Film> getFilms() {
        return filmRepository.getAll().stream().peek(film -> {
            MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId()).orElseThrow(
                    () -> new ValidationException(String.format(MPA_RATING_ID_NOT_VALID, film.getMpa().getId())));

            Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());
            film.setMpa(mpaRating);
            film.setLikes(likes);
        }).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(long filmId) {
        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, filmId)));

        MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId())
                .orElseThrow(() -> new ValidationException(String.format(MPA_RATING_ID_NOT_VALID, filmId)));

        Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());

        film.setMpa(mpaRating);
        film.setLikes(likes);

        return film;
    }

    public Film createFilm(Film film) {
        FilmValidator.validate(film);
        MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId()).orElseThrow(
                () -> new ValidationException(String.format(MPA_RATING_ID_NOT_VALID, film.getMpa().getId())));

        List<Long> genreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        Set<Genre> genres = genreRepository.getByIds(genreIds);
        Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());

        if (genreIds.size() != genres.size()) {
            throw new ValidationException(String.format(GENRES_NOT_VALID, genreIds));
        }

        film.setMpa(mpaRating);
        film.setGenres(genres);
        film.setLikes(likes);

        return mapToSortedFields(filmRepository.save(film));
    }

    public Film updateFilm(Film newFilm) {
        FilmValidator.validate(newFilm);

        Film film = filmRepository.getById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, newFilm.getId())));

        MpaRating mpaRating = mpaRatingRepository.getById(newFilm.getMpa().getId()).orElseThrow(
                () -> new NotFoundException(String.format(MPA_RATING_ID_NOT_VALID, newFilm.getMpa().getId())));

        List<Long> genreIds = newFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        Set<Genre> genres = genreRepository.getByIds(genreIds);
        Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());

        if (genreIds.size() != genres.size()) {
            throw new ValidationException(String.format(GENRES_NOT_VALID, genreIds));
        }

        film.setName(newFilm.getName());
        film.setDescription(newFilm.getDescription());
        film.setReleaseDate(newFilm.getReleaseDate());
        film.setDuration(newFilm.getDuration());
        film.setMpa(mpaRating);
        film.setGenres(genres);
        film.setLikes(likes);

        return filmRepository.update(film);
    }

    public Film likeFilm(long filmId, long userId) {
        return editLike(filmId, userId, likeRepository::addLike);
    }

    public Film removeLike(long filmId, long userId) {
        return editLike(filmId, userId, likeRepository::removeLike);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam Optional<Long> count) {
        return filmRepository.getTop(count.orElse(10L));
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmRepository.getCommonFilms(userId, friendId).stream().peek(film -> {
            MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId()).orElseThrow(
                    () -> new ValidationException(String.format(MPA_RATING_ID_NOT_VALID, film.getMpa().getId())));

            Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());
            film.setMpa(mpaRating);
            film.setLikes(likes);
        }).collect(Collectors.toList());
    }

    private Film editLike(long filmId, long userId, BiConsumer<Long, Long> action) {
        action.accept(filmId, userId);
        return getFilmById(filmId);
    }

    private Film mapToSortedFields(Film film) {
        film.setGenres(film.getGenres()
                .stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        return film;
    }
}
