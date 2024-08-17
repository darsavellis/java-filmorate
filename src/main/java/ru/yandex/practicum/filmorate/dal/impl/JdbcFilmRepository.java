package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.impl.extractors.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcFilmRepository implements FilmRepository {
    static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    static final String FIND_ALL_FILMS_QUERY = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        """;
    static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    static final String FIND_FILM_GENRE_QUERY = "SELECT * FROM film_genre";
    static final String FIND_FILM_DIRECTOR_QUERY = "SELECT * FROM film_director";
    static final String FIND_BY_ID_QUERY = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description,d.id AS director_id,
        d.name AS director_name, g.id AS genre_id, g.name AS genre_name
        FROM films f
        LEFT JOIN ratings r ON r.id = f.rating_id
        LEFT JOIN film_director fd ON fd.film_id = f.id
        LEFT JOIN directors d ON d.id = fd.director_id
        LEFT JOIN film_genre fg ON fg.film_id = f.id
        LEFT JOIN genres g ON g.id = fg.genre_id
        WHERE f.id = :id""";
    static final String FIND_GENRES_BY_FILM_ID_QUERY = """
        SELECT f.genre_id AS id, g.name
        FROM film_genre AS f
        JOIN genres g ON g.id = f.genre_id
        WHERE film_id = :film_id
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
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        ORDER BY (SELECT count(*) FROM likes l GROUP BY film_id HAVING f.id = l.film_id) DESC
        LIMIT :count
        """;
    static final String DELETE_QUERY = "DELETE FROM films WHERE id = :id";
    static final String FIND_FILMS_BY_DIRECTOR = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM film_director AS fd
        JOIN films AS f ON f.id = fd.film_id
        JOIN ratings r ON r.id = f.rating_id
        LEFT JOIN (SELECT f.ID, count(l.user_id) AS l FROM likes l JOIN films f ON f.id  = l.film_id GROUP BY f.id)
        AS lt ON lt.id = f.ID
        WHERE fd.director_id = :director_id
        """;
    static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";
    static final String LIST_OF_COMMON_FILMS = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        JOIN likes ON f.id = likes.film_id
        WHERE f.id = (SELECT film_id FROM likes WHERE film_id =
        (SELECT film_id FROM likes WHERE user_id = :userId LIMIT 1) AND user_id = :friendId LIMIT 1)
        GROUP BY f.id, likes.id ORDER BY COUNT(likes.user_id) DESC
        """;
    static final String LIST_OF_RECOMMENDED_FILMS = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        JOIN likes ON f.id = likes.film_id
        WHERE f.id = (SELECT film_id FROM likes WHERE film_id NOT IN
        (SELECT film_id FROM likes WHERE user_id = :userId) AND user_id =
        (SELECT user_id FROM likes WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = :userId LIMIT 1)
        AND user_id != :userId LIMIT 1)) GROUP BY f.id ORDER BY COUNT(likes.user_id) DESC
        """;
    static final String FIND_FILMS_BY_GENRE_ID_AND_RELEASE_DATE = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description, count(l.user_id) AS likes
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        LEFT JOIN FILM_GENRE fg ON fg.FILM_ID = f.ID
        LEFT JOIN LIKES l ON l.FILM_ID = f.ID
        WHERE (EXTRACT(YEAR FROM f.RELEASE_DATE) = :year OR :year IS NULL)
        AND (fg.GENRE_ID = :genre_id OR :genre_id IS NULL)
        GROUP BY f.ID
        """;
    static final String SEARCH_FILMS_QUERY = """
        SELECT f.*, r.name AS rating_name, r.description AS rating_description
        FROM films f
        JOIN ratings r ON r.id = f.rating_id
        LEFT JOIN FILM_DIRECTOR fd ON fd.FILM_ID =f.ID
        LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.ID
        LEFT JOIN LIKES l ON l.FILM_ID = f.ID
        WHERE (f.name ILIKE :film_name) OR (d.name ILIKE :director_name)
        GROUP BY f.ID
        ORDER BY count(l.USER_ID) DESC
        """;

    final NamedParameterJdbcOperations jdbc;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;
    final DirectorRowMapper directorRowMapper;
    final FilmResultSetExtractor filmResultSetExtractor;

    @Override
    public List<Film> getAll() {
        Map<Long, Film> filmMap = getEntitiesMap(FIND_ALL_FILMS_QUERY, filmRowMapper, Film::getId);
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Optional<Film> getById(long filmId) {
        try {
            List<Film> resultFilm = jdbc.query(FIND_BY_ID_QUERY, Map.of("id", filmId), filmResultSetExtractor);
            return Optional.ofNullable(Objects.requireNonNull(resultFilm).getFirst());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getByDirectorId(long directorId, String sortBy) {
        String sortQuery = buildSortQuery(sortBy);

        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);

        Director director = getEntitiesMap(FIND_DIRECTOR_BY_ID, directorRowMapper, Director::getId,
            Map.of("id", directorId)).get(directorId);
        Map<Long, Film> filmMap = getEntitiesMap(sortQuery, filmRowMapper, Film::getId,
            Map.of("director_id", directorId));

        filmMap.forEach((filmId, film) -> film.getDirectors().add(director));
        fillGenres(filmMap, genreMap);

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
        insertFilmRelations(film);
        return film;
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

    @Override
    public List<Film> searchFilms(String query, String by) {
        Map<Long, Film> filmMap = getEntitiesMap(SEARCH_FILMS_QUERY, filmRowMapper,
            Film::getId, buildParams(query, by).getValues(), LinkedHashMap::new);

        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        Map<Long, Film> filmMap = new HashMap<>();

        jdbc.query(LIST_OF_COMMON_FILMS, Map.of("userId", userId, "friendId", friendId), filmRowMapper)
            .forEach(film -> filmMap.put(film.getId(), film));
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        Map<Long, Film> filmMap = new HashMap<>();

        jdbc.query(LIST_OF_RECOMMENDED_FILMS, Map.of("userId", userId), filmRowMapper)
            .forEach(film -> filmMap.put(film.getId(), film));
        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public List<Film> getTopPopularFilms(Long limit, Long genreId, Long year) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("genre_id", genreId)
            .addValue("year", year)
            .addValue("limit", limit);

        Map<Long, Genre> genreMap = getEntitiesMap(FIND_ALL_GENRES_QUERY, genreRowMapper, Genre::getId);
        Map<Long, Director> directorMap = getEntitiesMap(FIND_ALL_DIRECTORS_QUERY, directorRowMapper, Director::getId);

        Map<Long, Film> filmMap = getEntitiesMap(buildTopFilmsQuery(genreId, year), filmRowMapper, Film::getId,
            params.getValues());

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);

        return new ArrayList<>(filmMap.values());
    }

    MapSqlParameterSource buildParams(String query, String by) {
        query = "%" + query + "%";
        return switch (by) {
            case "title" -> new MapSqlParameterSource("film_name", query)
                .addValue("director_name", null);
            case "director" -> new MapSqlParameterSource("director_name", query)
                .addValue("film_name", null);
            case "director,title", "title,director" -> new MapSqlParameterSource("film_name", query)
                .addValue("director_name", query);
            default -> throw new ValidationException(String.format("Operation %s not supported", by));
        };
    }

    <T> Map<Long, T> getEntitiesMap(String query, RowMapper<T> rowMapper, Function<T, Long> idExtractor) {
        return getEntitiesMap(query, rowMapper, idExtractor, new HashMap<>(), HashMap::new);
    }

    <T> Map<Long, T> getEntitiesMap(String query, RowMapper<T> rowMapper, Function<T, Long> idExtractor,
                                    Map<String, ?> params) {
        return getEntitiesMap(query, rowMapper, idExtractor, params, LinkedHashMap::new);
    }

    <T> Map<Long, T> getEntitiesMap(String query, RowMapper<T> rowMapper,
                                    Function<T, Long> idExtractor, Map<String, ?> params,
                                    Supplier<Map<Long, T>> mapSupplier) {
        return getEntitiesByCollector(query, rowMapper, idExtractor, params, Collectors.toMap(
            idExtractor, Function.identity(), (oldFilm, newFilm) -> oldFilm, mapSupplier
        ));
    }

    <T> Map<Long, T> getEntitiesByCollector(String query, RowMapper<T> rowMapper, Function<T, Long> idExtractor,
                                            Map<String, ?> params, Collector<T, ?, Map<Long, T>> collector) {
        return jdbc.query(query, params, rowMapper).stream().collect(collector);
    }

    String buildSortQuery(String sortBy) {
        return FIND_FILMS_BY_DIRECTOR + switch (sortBy.toLowerCase()) {
            case "year" -> "ORDER BY release_date ASC";
            case "likes" -> "ORDER BY l DESC";
            default -> throw new ValidationException("Unexpected value: " + sortBy.toLowerCase());
        };
    }

    void fillDirectors(Map<Long, Film> filmMap, Map<Long, Director> directorMap) {
        jdbc.query(FIND_FILM_DIRECTOR_QUERY, (resultSet) -> {
            Film film = filmMap.get(resultSet.getLong("film_id"));
            if (Objects.nonNull(film)) {
                film.getDirectors().add(directorMap.get(resultSet.getLong("director_id")));
            }
        });
    }

    void fillGenres(Map<Long, Film> filmMap, Map<Long, Genre> genreMap) {
        jdbc.query(FIND_FILM_GENRE_QUERY, (resultSet) -> {
            Film film = filmMap.get(resultSet.getLong("film_id"));
            if (Objects.nonNull(film)) {
                film.getGenres().add(genreMap.get(resultSet.getLong("genre_id")));
            }
        });
    }

    SqlParameterSource getSqlFilmParameters(Film film) {
        return new MapSqlParameterSource()
            .addValue("name", film.getName())
            .addValue("description", film.getDescription())
            .addValue("release_date", film.getReleaseDate())
            .addValue("duration", film.getDuration())
            .addValue("rating_id", film.getMpa().getId())
            .addValue("id", film.getId());
    }

    Film updateFilmFields(Film film) {
        deleteFilmRelations(film);
        insertFilmRelations(film);
        return film;
    }

    void insertFilmRelations(Film film) {
        jdbc.batchUpdate(FILM_GENRE_INSERT_QUERY, film.getGenres().stream()
            .map(genre -> new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("genre_id", genre.getId())).toArray(SqlParameterSource[]::new));
        jdbc.batchUpdate(FILM_DIRECTOR_INSERT_QUERY, film.getDirectors().stream()
            .map(director -> new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("director_id", director.getId())).toArray(SqlParameterSource[]::new));
    }

    void deleteFilmRelations(Film film) {
        SqlParameterSource deleteFilmGenreParam = new MapSqlParameterSource()
            .addValue("film_id", film.getId());
        SqlParameterSource deleteFilmDirectorParam = new MapSqlParameterSource()
            .addValue("film_id", film.getId());

        jdbc.update(FILM_GENRE_DELETE_QUERY, deleteFilmGenreParam);
        jdbc.update(FILM_DIRECTOR_DELETE_QUERY, deleteFilmDirectorParam);
    }

    String buildTopFilmsQuery(Long genreId, Long year) {
        if (Objects.isNull(genreId) && Objects.isNull(year)) {
            return FIND_FILMS_BY_GENRE_ID_AND_RELEASE_DATE + "ORDER BY likes DESC LIMIT :limit";
        } else {
            return FIND_FILMS_BY_GENRE_ID_AND_RELEASE_DATE + "LIMIT :limit";
        }
    }
}
