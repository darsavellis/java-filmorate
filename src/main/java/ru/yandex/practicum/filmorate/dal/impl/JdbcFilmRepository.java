package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcFilmRepository implements FilmRepository {
    static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films";
    static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    static final String FIND_ALL_MPA_RATINGS_QUERY = "SELECT * FROM ratings";
    static final String FIND_FILM_GENRE_QUERY = "SELECT * FROM film_genre";
    static final String FIND_FILM_DIRECTOR_QUERY = "SELECT * FROM film_director";
    static final String FIND_LIKES_QUERY = "SELECT * FROM likes";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = :id";
    static final String FIND_GENRES_QUERY = """
        SELECT f.genre_id AS id, g.name FROM film_genre AS f\s
        JOIN genres g ON g.id = f.genre_id WHERE film_id = :film_id
        """;
    static final String FILM_INSERT_QUERY = """
        INSERT INTO films (name, description, release_date, duration, rating_id)
        VALUES(:name, :description, :release_date, :duration, :rating_id)
        """;
    static final String UPDATE_QUERY = """
        UPDATE films SET name = :name, description = :description,
        release_date = :release_date, duration = :duration, rating_id = :rating_id WHERE id = :id
        """;
    static final String FILM_GENRE_DELETE_QUERY = "DELETE FROM film_genre WHERE film_id = :film_id";
    static final String FILM_GENRE_INSERT_QUERY =
        "INSERT INTO film_genre (film_id, genre_id) VALUES(:film_id, :genre_id)";
    static final String FIND_TOP_WITH_LIMIT_QUERY = """
        SELECT * FROM films f
        ORDER BY (SELECT count(*) FROM likes l GROUP BY film_id HAVING f.id = l.film_id) DESC LIMIT :count
        """;
    static final String DELETE_QUERY = "DELETE * FROM films WHERE id = :id";

    final NamedParameterJdbcOperations jdbc;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;
    final DirectorRowMapper directorRowMapper;
    final MpaRatingRowMapper mpaRatingRowMapper;

    @Override
    public List<Film> getAll() {
        Map<Long, Genre> genreMap = new HashMap<>();
        Map<Long, Film> filmMap = new HashMap<>();
        Map<Long, Director> directorMap = new HashMap<>();
        Map<Long, MpaRating> mpaRatingMap = new HashMap<>();

        jdbc.query(FIND_ALL_GENRES_QUERY, genreRowMapper).forEach(genre -> genreMap.put(genre.getId(), genre));
        jdbc.query(FIND_ALL_MPA_RATINGS_QUERY, mpaRatingRowMapper)
            .forEach(rating -> mpaRatingMap.put(rating.getId(), rating));
        jdbc.query(FIND_ALL_DIRECTORS_QUERY, directorRowMapper)
            .forEach(director -> directorMap.put(director.getId(), director));
        jdbc.query(FIND_ALL_FILMS_QUERY, filmRowMapper)
            .forEach(film -> {
                film.setMpa(mpaRatingMap.get(film.getId()));
                filmMap.put(film.getId(), film);
            });

        jdbc.query(FIND_FILM_GENRE_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getGenres().add(genreMap.get(resultSet.getLong("genre_id")));
        });

        jdbc.query(FIND_FILM_DIRECTOR_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getDirectors().add(directorMap.get(resultSet.getLong("director_id")));
        });

        jdbc.query(FIND_LIKES_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getLikes().add(resultSet.getLong("user_id"));
        });

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Optional<Film> getById(long filmId) {
        Film film = jdbc.queryForObject(
            FIND_BY_ID_QUERY,
            new MapSqlParameterSource("id", filmId),
            filmRowMapper
        );
        Set<Genre> genres = new HashSet<>(jdbc.query(FIND_GENRES_QUERY, Map.of("film_id", filmId), genreRowMapper));
        if (Objects.nonNull(film)) {
            film.setGenres(genres);
        }
        return Optional.ofNullable(film);
    }

    @Override
    public Film save(Film film) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource filmParameters = getSqlFilmParameters(film);

        jdbc.update(FILM_INSERT_QUERY, filmParameters, generatedKeyHolder, new String[]{"id"});

        long id = generatedKeyHolder.getKeyAs(Long.class);
        film.setId(id);

        SqlParameterSource filmGenreParameters = new MapSqlParameterSource()
            .addValue("film_id", film.getId());

        jdbc.update(FILM_GENRE_DELETE_QUERY, filmGenreParameters);

        for (Genre genre : film.getGenres()) {
            jdbc.update(FILM_GENRE_INSERT_QUERY, Map.of("film_id", film.getId(), "genre_id", genre.getId()));
        }

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        SqlParameterSource filmParameters = getSqlFilmParameters(newFilm);

        jdbc.update(UPDATE_QUERY, filmParameters);

        SqlParameterSource filmGenreParameters = new MapSqlParameterSource()
            .addValue("film_id", newFilm.getId());

        jdbc.update(FILM_GENRE_DELETE_QUERY, filmGenreParameters);

        newFilm.getGenres().forEach((genre) -> jdbc.update(
            FILM_GENRE_INSERT_QUERY, Map.of("film_id", newFilm.getId(), "genre_id", genre.getId())
        ));
        return newFilm;
    }

    @Override
    public List<Film> getTop(long count) {
        return jdbc.query(FIND_TOP_WITH_LIMIT_QUERY, Map.of("count", count), filmRowMapper);
    }

    @Override
    public boolean delete(long filmId) {
        int rows = jdbc.update(DELETE_QUERY, Map.of("id", filmId));
        return rows > 0;
    }

    private SqlParameterSource getSqlFilmParameters(Film film) {
        return new MapSqlParameterSource()
            .addValue("name", film.getName())
            .addValue("description", film.getDescription())
            .addValue("release_date", film.getReleaseDate())
            .addValue("duration", film.getDuration())
            .addValue("rating_id", film.getMpa().getId())
            .addValue("id", film.getId());
    }
}
