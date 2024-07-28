package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcMpaRatingRepository implements MpaRatingRepository {
    static String FIND_ALL_QUERY = "SELECT * FROM ratings";
    static String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = :id";

    final NamedParameterJdbcOperations jdbc;
    final MpaRatingRowMapper mpaRatingRowMapper;

    @Override
    public List<MpaRating> getMpaRatings() {
        return jdbc.query(FIND_ALL_QUERY, mpaRatingRowMapper);
    }

    @Override
    public Optional<MpaRating> getById(long mpaRatingId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    FIND_BY_ID_QUERY, new MapSqlParameterSource("id", mpaRatingId), mpaRatingRowMapper
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
