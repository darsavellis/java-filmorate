package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcReviewRepository implements ReviewRepository {
    final NamedParameterJdbcOperations jdbc;
    final ReviewRowMapper reviewRowMapper;

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        final String findReviewQuery = "SELECT * FROM reviews WHERE id = :review_id";
        final String countReviewScore = "SELECT SUM((is_like - 1 + is_like % 2)) FROM review_user " +
            "WHERE review_id = :review_id";

        try {
            Optional<Review> review = Optional.ofNullable(jdbc.queryForObject(
                findReviewQuery,
                Map.of("review_id", reviewId),
                reviewRowMapper
            ));
            Long score = jdbc.queryForObject(countReviewScore, Map.of("review_id", reviewId), Long.class);

            if (Objects.nonNull(score) && review.isPresent()) {
                review.get().setUseful(score);
            }
            return review;
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, long count) {
        final String findFilmReviewsQuery = "SELECT * FROM reviews WHERE film_id = :film_id LIMIT :count";
        final String countFilmsReviewsScores = "SELECT r.id, SUM((ru.is_like - 1 + ru.is_like % 2)) " +
            "AS useful FROM review_user ru RIGHT JOIN reviews r ON r.id = ru.review_id " +
            "WHERE r.film_id = :film_id GROUP BY r.id";

        Map<Long, Long> scoreMap = new HashMap<>(); // мап пост id - рейтинг
        Map<Long, Review> reviewMap = new HashMap<>();

        jdbc.query(countFilmsReviewsScores, Map.of("film_id", filmId), (resultSet) -> {
            Long id = resultSet.getLong("id");
            Long score = resultSet.getLong("useful");
            scoreMap.put(id, score);
        });
        jdbc.query(findFilmReviewsQuery, Map.of("film_id", filmId, "count", count), reviewRowMapper)
            .forEach((review) -> {
                review.setUseful(scoreMap.get(review.getReviewId()));
                reviewMap.put(review.getReviewId(), review);
            });

        return reviewMap.values().stream().sorted(Comparator.comparing(Review::getUseful).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> getAllReviews(long count) {
        final String findAllReviewsQuery = "SELECT * FROM reviews LIMIT :count";
        final String countAllReviewsScores = "SELECT r.id, SUM((ru.is_like - 1 + ru.is_like % 2)) " +
            "AS useful FROM review_user ru RIGHT JOIN reviews r ON r.id = ru.review_id GROUP BY r.id";

        Map<Long, Long> scoreMap = new HashMap<>();
        Map<Long, Review> reviewMap = new HashMap<>();

        jdbc.query(countAllReviewsScores, (resultSet) -> {
            scoreMap.put(resultSet.getLong("id"),
                resultSet.getLong("useful"));
        });
        jdbc.query(findAllReviewsQuery, Map.of("count", count), reviewRowMapper).forEach((review) -> {
            review.setUseful(scoreMap.get(review.getReviewId()));
            reviewMap.put(review.getReviewId(), review);
        });

        return reviewMap.values().stream().sorted(Comparator.comparing(Review::getUseful).reversed())
            .collect(Collectors.toList());
    }

    @Override
    public Review createReview(Review review) {
        final String reviewInsertQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id, timestamp) " +
            "VALUES (:content, :is_positive, :user_id, :film_id, :timestamp)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        Timestamp timestamp = Timestamp.from(Instant.now());
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("content", review.getContent())
            .addValue("film_id", review.getFilmId())
            .addValue("user_id", review.getUserId())
            .addValue("is_positive", review.getIsPositive())
            .addValue("timestamp", timestamp);

        jdbc.update(reviewInsertQuery, parameters, generatedKeyHolder, new String[]{"id"});

        Long id = generatedKeyHolder.getKeyAs(Long.class);

        if (Objects.nonNull(id)) {
            review.setReviewId(id);
            return review;
        }
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        final String reviewUpdateQuery = "UPDATE reviews SET content = :content, " +
            "is_positive = :is_positive, timestamp = :timestamp WHERE id = :id";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        Timestamp timestamp = Timestamp.from(Instant.now());
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("id", review.getReviewId())
            .addValue("content", review.getContent())
            .addValue("is_positive", review.getIsPositive())
            .addValue("timestamp", timestamp);
        jdbc.update(reviewUpdateQuery, parameters);
        return review;
    }

    @Override
    public boolean deleteReview(long reviewId) {
        final String reviewDeleteQuery = "DELETE FROM reviews WHERE id = :id";

        int rows = jdbc.update(reviewDeleteQuery, Map.of("id", reviewId));
        return rows > 0;
    }

    @Override
    public void setLikeReview(long reviewId, long userId, boolean isPositive) {
        final String addLikeQuery = "MERGE INTO review_user (review_id, user_id, is_like) " +
            "VALUES (:review_id, :user_id, :is_like)";

        jdbc.update(addLikeQuery, Map.of("review_id", reviewId, "user_id", userId, "is_like", isPositive));
    }

    @Override
    public void deleteLikeReview(long reviewId, long userId) {
        final String removeAnyLikeQuery = "DELETE FROM review_user WHERE review_id = :review_id AND " +
            "user_id = :user_id";

        int rows = jdbc.update(removeAnyLikeQuery, Map.of("review_id", reviewId, "user_id", userId));
    }

    @Override
    public void deleteDislikeReview(long reviewId, long userId) {
        final String removeCertainLikeQuery = "DELETE FROM review_user WHERE review_id = :review_id AND " +
            "user_id = :user_id AND is_like = :is_like";

        int rows = jdbc.update(removeCertainLikeQuery, Map.of("review_id", reviewId, "user_id", userId,
            "is_like", false));
    }

    @Override
    public void eventReview(long userId, long reviewId, OperationType operationType) {
        final String insertEventQuery = "INSERT INTO events (user_id, entity_id, timestamp, type_id, operation_id) " +
            "SELECT :user_id, :entity_id, :timestamp, t.id , o.id FROM event_types t, operation_types o " +
            "WHERE t.name = :event_type AND o.name = :operation_type";

        Timestamp timestamp = Timestamp.from(Instant.now());
        jdbc.update(insertEventQuery, Map.of("user_id", userId, "entity_id", reviewId,
            "timestamp", timestamp, "event_type", EventType.REVIEW.toString(),
            "operation_type", operationType.toString()));
    }
}
