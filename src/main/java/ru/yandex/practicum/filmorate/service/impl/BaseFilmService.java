package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {
    static final String MPA_RATING_ID_NOT_VALID = "MpaRating ID=%s not valid";
    static final String FILM_ID_NOT_FOUND = "Film ID=%s not found";
    static final String GENRES_NOT_VALID = "Genres %s not valid";
    static final String DIRECTORS_NOT_VALID = "Directors %s not valid";

    final FilmRepository filmRepository;
    final MpaRatingRepository mpaRatingRepository;
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

        MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId())
            .orElseThrow(() -> new ValidationException(String.format(MPA_RATING_ID_NOT_VALID, filmId)));

        Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());

        film.setMpa(mpaRating);
        film.setLikes(likes);

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        FilmValidator.validate(film);
        editFilm(film, () -> new ValidationException(String.format(FILM_ID_NOT_FOUND, film.getId())));

        return mapToSortedFields(filmRepository.save(film));
    }

    @Override
    public Film updateFilm(Film newFilm) {
        FilmValidator.validate(newFilm);
        filmRepository.getById(newFilm.getId())
            .orElseThrow(() -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, newFilm.getId())));

        editFilm(newFilm, () -> new NotFoundException(String.format(FILM_ID_NOT_FOUND, newFilm.getId())));

        return filmRepository.update(newFilm);
    }

    @Override
    public Film likeFilm(long filmId, long userId) {
        Film result = editLike(filmId, userId, likeRepository::addLike);
        likeRepository.eventLike(filmId, userId, OperationType.ADD);
        return result;
    }

    public Film deleteLike(long filmId, long userId) {
        Film result = editLike(filmId, userId, likeRepository::removeLike);
        likeRepository.eventLike(filmId, userId, OperationType.REMOVE);
        return result;
    }

    @Override
    public List<Film> getMostPopularFilms(Optional<Long> count) {
        return filmRepository.getTop(count.orElse(10L));
    }

    private void editFilm(Film film, Supplier<? extends RuntimeException> exceptionSupplier) {
        MpaRating mpaRating = mpaRatingRepository.getById(film.getMpa().getId()).orElseThrow(exceptionSupplier);

        Set<Genre> genres = getValidatedEntities(film.getGenres(), Genre::getId,
            genreRepository::getByIds, GENRES_NOT_VALID);
        Set<Director> directors = getValidatedEntities(film.getDirectors(), Director::getId,
            directorRepository::getByIds, DIRECTORS_NOT_VALID);
        Set<Long> likes = likeRepository.getLikesByFilmId(film.getId());

        updateFilmFields(film, mpaRating, genres, directors, likes);
    }

    private <T> Set<T> getValidatedEntities(Set<T> entitySet, Function<T, Long> idExtractor,
                                            Function<List<Long>, Set<T>> convertIds, String errorMessage) {
        List<Long> entityIds = entitySet.stream().map(idExtractor).toList();
        Set<T> entities = convertIds.apply(entityIds);
        if (entityIds.size() != entities.size()) {
            throw new ValidationException(String.format(errorMessage, entities));
        }
        return entities;
    }

    private void updateFilmFields(Film film, MpaRating mpaRating, Set<Genre> genres,
                                  Set<Director> directors, Set<Long> likes) {
        film.setMpa(mpaRating);
        film.setGenres(genres);
        film.setDirectors(directors);
        film.setLikes(likes);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        userRepository.findById(userId);
        userRepository.findById(friendId);
        return filmRepository.getCommonFilms(userId, friendId);
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

    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
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
        List<Film> top = filmRepository.getAll().stream()
            .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
            .collect(Collectors.toList());

        if (genreId != null || year != null) {
            return top.stream()
                .filter(film -> {
                    if (genreId != null) {
                        return film.getGenres().stream().anyMatch(genre -> genre.getId() == genreId);
                    } else {
                        return true;
                    }
                })
                .filter(film -> {
                    if (year != null) {
                        return film.getReleaseDate().getYear() == year;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        }
        if (limit != null) {
            top = top.stream().limit(limit).toList();
        }
        return top;
    }

}
