package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dal.impl.extractors.MpaRatingResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcMpaRatingRepository implements MpaRatingRepository {
    final NamedParameterJdbcOperations jdbc;
    final MpaRatingRowMapper mpaRatingRowMapper;
    final MpaRatingResultSetExtractor mpaRatingResultSetExtractor;

    @Override
    public List<MpaRating> getMpaRatings() {
        String findAllQuery = "SELECT * FROM ratings";

        return jdbc.query(findAllQuery, mpaRatingRowMapper);
    }

    @Override
    public Optional<MpaRating> getById(long mpaRatingId) {
        String findByIdQuery = "SELECT * FROM ratings WHERE id = :id";

        return Optional.ofNullable(
            jdbc.query(findByIdQuery, Map.of("id", mpaRatingId), mpaRatingResultSetExtractor)
        );
    }
}
