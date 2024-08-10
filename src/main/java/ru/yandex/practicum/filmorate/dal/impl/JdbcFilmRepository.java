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
    static final String FIND_GENRES_BY_FILM_ID_QUERY = """
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

    final NamedParameterJdbcOperations jdbc;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;
    final DirectorRowMapper directorRowMapper;
    final MpaRatingRowMapper mpaRatingRowMapper;

    @Override
    public List<Film> getAll() {
        Map<Long, Genre> genreMap = getGenresMap();
        Map<Long, Director> directorMap = getDirectorsMap();
        Map<Long, MpaRating> mpaRatingMap = getMpaRatingsMap();
        Map<Long, Film> filmMap = getFilmsWithMpaRating(mpaRatingMap);

        fillGenres(filmMap, genreMap);
        fillDirectors(filmMap, directorMap);
        fillLikes(filmMap);

        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Optional<Film> getById(long filmId) {
        Film film = jdbc.queryForObject(
            FIND_BY_ID_QUERY,
            new MapSqlParameterSource("id", filmId),
            filmRowMapper
        );
        Set<Genre> genres = new HashSet<>(jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, Map.of("film_id", filmId), genreRowMapper));
        if (Objects.nonNull(film)) {
            film.setGenres(genres);
        }
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getByDirectorId(long directorId, String sortBy) {
        return null;
    }


    @Override
    public Film save(Film film) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource filmParameters = getSqlFilmParameters(film);
        jdbc.update(FILM_INSERT_QUERY, filmParameters, generatedKeyHolder, new String[]{"id"});
        film.setId(generatedKeyHolder.getKeyAs(Long.class));
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

    private Map<Long, Film> getFilmsWithMpaRating(Map<Long, MpaRating> mpaRatingMap) {
        Map<Long, Film> filmMap = new HashMap<>();
        jdbc.query(FIND_ALL_FILMS_QUERY, filmRowMapper)
            .forEach(film -> {
                film.setMpa(mpaRatingMap.get(film.getId()));
                filmMap.put(film.getId(), film);
            });
        return filmMap;
    }

    private Map<Long, Director> getDirectorsMap() {
        Map<Long, Director> directorMap = new HashMap<>();
        jdbc.query(FIND_ALL_DIRECTORS_QUERY, directorRowMapper)
            .forEach(director -> directorMap.put(director.getId(), director));
        return directorMap;
    }

    private Map<Long, MpaRating> getMpaRatingsMap() {
        Map<Long, MpaRating> mpaRatingMap = new HashMap<>();
        jdbc.query(FIND_ALL_MPA_RATINGS_QUERY, mpaRatingRowMapper)
            .forEach(rating -> mpaRatingMap.put(rating.getId(), rating));
        return mpaRatingMap;
    }

    private Map<Long, Genre> getGenresMap() {
        Map<Long, Genre> genreMap = new HashMap<>();
        jdbc.query(FIND_ALL_GENRES_QUERY, genreRowMapper).forEach(genre -> genreMap.put(genre.getId(), genre));
        return genreMap;
    }

    private void fillLikes(Map<Long, Film> filmMap) {
        jdbc.query(FIND_LIKES_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getLikes().add(resultSet.getLong("user_id"));
        });
    }

    private void fillDirectors(Map<Long, Film> filmMap, Map<Long, Director> directorMap) {
        jdbc.query(FIND_FILM_DIRECTOR_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getDirectors().add(directorMap.get(resultSet.getLong("director_id")));
        });
    }

    private void fillGenres(Map<Long, Film> filmMap, Map<Long, Genre> genreMap) {
        jdbc.query(FIND_FILM_GENRE_QUERY, (resultSet) -> {
            long id = resultSet.getLong("film_id");
            filmMap.get(id).getGenres().add(genreMap.get(resultSet.getLong("genre_id")));
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
        SqlParameterSource deleteFilmGenreParam = new MapSqlParameterSource()
            .addValue("film_id", film.getId());
        SqlParameterSource deleteFilmDirectorParam = new MapSqlParameterSource()
            .addValue("film_id", film.getId());
        List<SqlParameterSource> insFilmGenreParam = new ArrayList<>();
        List<SqlParameterSource> insertFilmDirectorParam = new ArrayList<>();
        film.getGenres().forEach(genre -> insFilmGenreParam.add(new MapSqlParameterSource(
            Map.of("film_id", film.getId(), "genre_id", genre.getId()))
        ));
        film.getDirectors().forEach(director -> insertFilmDirectorParam.add(new MapSqlParameterSource(
            Map.of("film_id", film.getId(), "director_id", director.getId()))
        ));

        jdbc.update(FILM_GENRE_DELETE_QUERY, deleteFilmGenreParam);
        jdbc.update(FILM_DIRECTOR_DELETE_QUERY, deleteFilmDirectorParam);
        jdbc.batchUpdate(FILM_GENRE_INSERT_QUERY, insFilmGenreParam.toArray(new SqlParameterSource[]{}));
        jdbc.batchUpdate(FILM_DIRECTOR_INSERT_QUERY, insertFilmDirectorParam.toArray(new SqlParameterSource[]{}));
        return film;
    }
}
