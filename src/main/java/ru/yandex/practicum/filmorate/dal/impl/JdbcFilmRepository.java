package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
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
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcFilmRepository implements FilmRepository {
    static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films";
    static final String FIND_GENRES_QUERY = "SELECT f.genre_id AS id, g.name FROM film_genre AS f " +
            "JOIN genres g ON g.id = f.genre_id WHERE film_id = :film_id";
    static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    static final String FIND_RATINGS_QUERY = "SELECT * FROM ratings";
    static final String FIND_FILM_GENRE_QUERY = "SELECT * FROM film_genre";
    static final String FIND_FILM_DIRECTOR_QUERY = "SELECT * FROM film_director";
    static final String FIND_LIKES_QUERY = "SELECT * FROM likes";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = :id";
    static final String FIND_GENRES_BY_FILM_ID_QUERY = """
            SELECT f.genre_id AS id, g.name FROM film_genre AS f
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
    static final String FILM_DIRECTOR_DELETE_QUERY = "DELETE FROM film_director WHERE film_id = :film_id";
    static final String FILM_GENRE_INSERT_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) VALUES(:film_id, :genre_id)";
    static final String FILM_DIRECTOR_INSERT_QUERY =
            "INSERT INTO film_director (film_id, director_id) VALUES(:film_id, :director_id)";
    static final String FIND_TOP_WITH_LIMIT_QUERY = """
            SELECT * FROM films f
            ORDER BY (SELECT count(*) FROM likes l GROUP BY film_id HAVING f.id = l.film_id) DESC LIMIT :count
            """;
    static final String DELETE_QUERY = "DELETE * FROM films WHERE id = :id";
    static final String FIND_FILMS_BY_DIRECTOR = """
            SELECT * FROM film_director AS fd
            JOIN films AS f ON f.id = fd.film_id
            LEFT JOIN (SELECT f.ID,  count(l.user_id) AS l FROM likes l JOIN films f ON f.id  = l.film_id GROUP BY f.id )
            AS lt ON lt.id = f.ID WHERE fd.director_id = :director_id
            """;
    static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";
    static final String LIST_OF_COMMON_FILMS = "SELECT * from films JOIN likes ON films.id = likes.film_id " +
            " WHERE films.id = (SELECT film_id FROM likes WHERE film_id = " +
            "(SELECT film_id FROM likes WHERE user_id = :userId LIMIT 1) AND user_id = :friendId LIMIT 1)" +
            "GROUP BY films.id, likes.id ORDER BY COUNT(likes.user_id) DESC";

    static String LIST_OF_RECOMMENDED_FILMS = "SELECT * from films JOIN likes ON films.id = likes.film_id  WHERE films.id = (SELECT film_id FROM likes " +
            "WHERE film_id NOT IN (SELECT film_id FROM likes WHERE user_id = :userId) AND user_id = " +
            "(SELECT user_id FROM likes WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = :userId LIMIT 1) " +
            "AND user_id != :userId LIMIT 1)) GROUP BY films.id ORDER BY COUNT(likes.user_id) DESC";

    final NamedParameterJdbcOperations jdbc;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;
    final DirectorRowMapper directorRowMapper;
    final MpaRatingRowMapper ratingRowMapper;

    @Override
    public List<Film> getAll() {
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);
        Map<Long, MpaRating> ratingMap = getEntitiesMap(FIND_RATINGS_QUERY, ratingRowMapper, MpaRating::getId);
        Map<Long, Film> filmMap = getEntitiesMap(FIND_ALL_FILMS_QUERY, filmRowMapper, Film::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);
        fillLikes(filmMap);

        filmMap.forEach((filmId, film) -> film.setMpa(ratingMap.get(film.getMpa().getId())));

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Optional<Film> getById(long filmId) {
        try {
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
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getByDirectorId(long directorId, String sortBy) {
        String sortQuery = buildSortQuery(sortBy);

        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, MpaRating> ratingMap = getEntitiesMap(FIND_RATINGS_QUERY, ratingRowMapper, MpaRating::getId);

        Director director = getEntitiesMap(FIND_DIRECTOR_BY_ID, directorRowMapper, Director::getId,
                Map.of("id", directorId)).get(directorId);
        Map<Long, Film> filmMap = getEntitiesMap(sortQuery, filmRowMapper, Film::getId,
                Map.of("director_id", directorId));

        filmMap.forEach((filmId, film) -> {
            film.getDirectors().add(director);
            film.setMpa(ratingMap.get(film.getMpa().getId()));
        });

        fillGenres(filmMap, genreMap);
        fillLikes(filmMap);

        return new ArrayList<>(filmMap.values());
    }


    @Override
    public Film save(Film film) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource filmParam = getSqlFilmParameters(film);
        jdbc.update(FILM_INSERT_QUERY, filmParam, generatedKeyHolder, new String[]{"id"});
        Long generatedId = generatedKeyHolder.getKeyAs(Long.class);
        if (Objects.nonNull(generatedId)) {
            film.setId(generatedId);
        }
        return updateFilmFields(film);
    }

    @Override
    public Film update(Film newFilm) {
        SqlParameterSource filmParams = getSqlFilmParameters(newFilm);
        jdbc.update(UPDATE_QUERY, filmParams);
        return updateFilmFields(newFilm);
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

    private <T> Map<Long, T> getEntitiesMap(String query, RowMapper<T> rowMapper, Function<T, Long> idExtractor) {
        return jdbc.query(query, rowMapper).stream()
                .collect(Collectors.toMap(idExtractor, Function.identity()));
    }

    private <T> Map<Long, T> getEntitiesMap(String query, RowMapper<T> rowMapper,
                                            Function<T, Long> idExtractor, Map<String, ?> params) {
        return jdbc.query(query, params, rowMapper).stream()
                .collect(Collectors.toMap(idExtractor, Function.identity()));
    }

    private static String buildSortQuery(String sortBy) {
        return FIND_FILMS_BY_DIRECTOR + switch (sortBy.toLowerCase()) {
            case "year" -> "ORDER BY release_date ASC";
            case "likes" -> "ORDER BY l DESC";
            default -> throw new ValidationException("Unexpected value: " + sortBy.toLowerCase());
        };
    }

    private void fillLikes(Map<Long, Film> filmMap) {
        jdbc.query(FIND_LIKES_QUERY, (resultSet) -> {
            Film film = filmMap.get(resultSet.getLong("film_id"));
            if (Objects.nonNull(film)) {
                film.getLikes().add(resultSet.getLong("user_id"));
            }
        });
    }

    private void fillDirectors(Map<Long, Film> filmMap, Map<Long, Director> directorMap) {
        jdbc.query(FIND_FILM_DIRECTOR_QUERY, (resultSet) -> {
            Film film = filmMap.get(resultSet.getLong("film_id"));
            if (Objects.nonNull(film)) {
                film.getDirectors().add(directorMap.get(resultSet.getLong("director_id")));
            }

        });
    }

    private void fillGenres(Map<Long, Film> filmMap, Map<Long, Genre> genreMap) {
        jdbc.query(FIND_FILM_GENRE_QUERY, (resultSet) -> {
            Film film = filmMap.get(resultSet.getLong("film_id"));
            if (Objects.nonNull(film)) {
                film.getGenres().add(genreMap.get(resultSet.getLong("genre_id")));
            }
        });
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

    private Film updateFilmFields(Film film) {
        deleteFilmRelations(film);
        insertFilmRelations(film);
        return film;
    }

    private void insertFilmRelations(Film film) {
        jdbc.batchUpdate(FILM_GENRE_INSERT_QUERY, film.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genre.getId())).toArray(SqlParameterSource[]::new));
        jdbc.batchUpdate(FILM_DIRECTOR_INSERT_QUERY, film.getDirectors().stream()
                .map(director -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("director_id", director.getId())).toArray(SqlParameterSource[]::new));
    }

    private void deleteFilmRelations(Film film) {
        SqlParameterSource deleteFilmGenreParam = new MapSqlParameterSource()
                .addValue("film_id", film.getId());
        SqlParameterSource deleteFilmDirectorParam = new MapSqlParameterSource()
                .addValue("film_id", film.getId());

        jdbc.update(FILM_GENRE_DELETE_QUERY, deleteFilmGenreParam);
        jdbc.update(FILM_DIRECTOR_DELETE_QUERY, deleteFilmDirectorParam);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        Map<Long, Film> filmMap = new HashMap<>();

        jdbc.query(LIST_OF_COMMON_FILMS, Map.of("userId", userId, "friendId", friendId), filmRowMapper)
                .forEach(film -> filmMap.put(film.getId(), film));
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);
        Map<Long, MpaRating> ratingMap = getEntitiesMap(FIND_RATINGS_QUERY, ratingRowMapper, MpaRating::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);
        fillLikes(filmMap);

        filmMap.forEach((filmId, film) -> film.setMpa(ratingMap.get(film.getMpa().getId())));
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        Map<Long, Film> filmMap = new HashMap<>();

        jdbc.query(LIST_OF_RECOMMENDED_FILMS, Map.of("userId", userId), filmRowMapper)
                .forEach(film -> filmMap.put(film.getId(), film));
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);
        Map<Long, MpaRating> ratingMap = getEntitiesMap(FIND_RATINGS_QUERY, ratingRowMapper, MpaRating::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);
        fillLikes(filmMap);

        filmMap.forEach((filmId, film) -> film.setMpa(ratingMap.get(film.getMpa().getId())));

        return new ArrayList<>(filmMap.values());
    }
}
