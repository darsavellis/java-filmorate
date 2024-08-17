package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {
    static final String FILM_ID_NOT_FOUND = "Film ID=%s not found";
    static final String GENRES_NOT_VALID = "Genres %s not valid";
    static final String DIRECTORS_NOT_VALID = "Directors %s not valid";
    static final String USER_ID_NOT_FOUND = "User ID=%s not found";
    static final String DIRECTOR_ID_NOT_FOUND = "Director ID=%s not found";

    final FilmRepository filmRepository;
    final GenreRepository genreRepository;
    final LikeRepository likeRepository;
    final DirectorRepository directorRepository;
    final UserRepository userRepository;

    @Override
    public List<Film> getFilms() {
        return filmRepository.getAll();
    }

    @Override
    public Film getFilmById(long filmId) {
        Film film = filmRepository.getById(filmId)
            .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, filmId)));

        List<Genre> genres = genreRepository.getGenresByFilmId(filmId);
        List<Director> directors = directorRepository.getDirectorsByFilmId(filmId);

        film.setGenres(new HashSet<>(genres));
        film.setDirectors(new HashSet<>(directors));

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        FilmValidator.validate(film);
        List<Genre> genres = getValidatedEntities(film.getGenres(), Genre::getId,
            genreRepository::getByIds, GENRES_NOT_VALID);
        List<Director> directors = getValidatedEntities(film.getDirectors(), Director::getId,
            directorRepository::getByIds, DIRECTORS_NOT_VALID);

        film.setGenres(new HashSet<>(genres));
        film.setDirectors(new HashSet<>(directors));

        return mapToSortedFields(filmRepository.save(film));
    }

    @Override
    public Film updateFilm(Film newFilm) {
        FilmValidator.validate(newFilm);
        filmRepository.getById(newFilm.getId())
            .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, newFilm.getId())));

        List<Genre> genres = getValidatedEntities(newFilm.getGenres(), Genre::getId,
            genreRepository::getByIds, GENRES_NOT_VALID);
        List<Director> directors = getValidatedEntities(newFilm.getDirectors(), Director::getId,
            directorRepository::getByIds, DIRECTORS_NOT_VALID);

        newFilm.setGenres(new HashSet<>(genres));
        newFilm.setDirectors(new HashSet<>(directors));

        return filmRepository.update(newFilm);
    }

    @Override
    public Film likeFilm(long filmId, long userId) {
        Film result = editLike(filmId, userId, likeRepository::addLike);
        likeRepository.eventLike(filmId, userId, OperationType.ADD);
        return result;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        Film result = editLike(filmId, userId, likeRepository::removeLike);
        likeRepository.eventLike(filmId, userId, OperationType.REMOVE);
        return result;
    }

    @Override
    public List<Film> getMostPopularFilms(long count) {
        return filmRepository.getTop(count);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        userRepository.findById(userId);
        userRepository.findById(friendId);
        return filmRepository.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        directorRepository.getById(directorId)
            .orElseThrow(() -> new NotFoundException(String.format(DIRECTOR_ID_NOT_FOUND, directorId)));
        return filmRepository.getByDirectorId(directorId, sortBy);
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        userRepository.findById(userId);
        return filmRepository.getRecommendations(userId);
    }

    @Override
    public Film deleteFilmById(long filmId) {
        Film film = filmRepository.getById(filmId)
            .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, filmId)));
        filmRepository.delete(filmId);
        return film;
    }

    @Override
    public List<Film> getTopPopularFilms(Long limit, Long genreId, Long year) {
        return filmRepository.getTopPopularFilms(limit, genreId, year);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        return filmRepository.searchFilms(query, by);
    }

    <T> List<T> getValidatedEntities(Set<T> entitySet, Function<T, Long> idExtractor,
                                     Function<List<Long>, List<T>> convertIds, String errorMessage) {
        List<Long> entityIds = entitySet.stream().map(idExtractor).toList();
        List<T> entities = convertIds.apply(entityIds);
        if (entityIds.size() != entities.size()) {
            throw new ValidationException(String.format(errorMessage, entities));
        }
        return entities;
    }


    Film editLike(long filmId, long userId, BiConsumer<Long, Long> action) {
        Film film = filmRepository.getById(filmId)
            .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, filmId)));
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_NOT_FOUND, userId)));
        action.accept(filmId, userId);
        return film;
    }

    Film mapToSortedFields(Film film) {
        film.setGenres(film.getGenres()
            .stream()
            .sorted(Comparator.comparingLong(Genre::getId))
            .collect(Collectors.toCollection(LinkedHashSet::new)));
        return film;
    }
}
