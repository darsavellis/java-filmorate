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

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcReviewRepository implements ReviewRepository {
    final NamedParameterJdbcOperations jdbc;
    final ReviewRowMapper reviewRowMapper;

    static final String FIND_REVIEW_QUERY = "SELECT * FROM reviews WHERE id = :review_id";
    static final String FIND_ALL_REVIEWS_QUERY = "SELECT * FROM reviews LIMIT :count";
    static final String FIND_FILM_REVIEWS_QUERY = "SELECT * FROM reviews WHERE film_id = :film_id LIMIT :count";
    static final String COUNT_REVIEW_SCORE = "SELECT SUM((is_like - 1 + is_like % 2)) FROM review_user " +
            "WHERE review_id = :review_id";
    static final String COUNT_FILMS_REVIEWS_SCORES = "SELECT r.id, SUM((ru.is_like - 1 + ru.is_like % 2)) " +
            "AS useful FROM review_user ru RIGHT JOIN reviews r ON r.id = ru.review_id WHERE r.film_id = :film_id GROUP BY r.id";

    static final String COUNT_ALL_REVIEWS_SCORES = "SELECT r.id, SUM((ru.is_like - 1 + ru.is_like % 2)) " +
            "AS useful FROM review_user ru RIGHT JOIN reviews r ON r.id = ru.review_id GROUP BY r.id";

    static final String REVIEW_INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id, timestamp) " +
            "VALUES (:content, :is_positive, :user_id, :film_id, :timestamp)";

    static final String REVIEW_UPDATE_QUERY = "UPDATE reviews SET content = :content, " +
            "is_positive = :is_positive, user_id = :user_id, film_id = :film_id, timestamp = :timestamp WHERE id = :id";

    static final String REVIEW_DELETE_QUERY = "DELETE FROM reviews WHERE id = :id";

    static final String ADD_LIKE_QUERY = "MERGE INTO review_user (review_id, user_id, is_like) " +
            "VALUES (:review_id, :user_id, :is_like)";

    static final String REMOVE_ANY_LIKE_QUERY = "DELETE FROM review_user WHERE review_id = :review_id AND " +
            "user_id = :user_id";

    static final String REMOVE_CERTAIN_LIKE_QUERY = "DELETE FROM review_user WHERE review_id = :review_id AND " +
            "user_id = :user_id AND is_like = :is_like";

    static final String INSERT_EVENT_QUERY = "INSERT INTO events (user_id, entity_id, timestamp, type_id, operation_id) " +
            "SELECT :user_id, :entity_id, :timestamp, t.id , o.id FROM event_types t, operation_types o " +
            "WHERE t.name = :event_type AND o.name = :operation_type";

    @Override
    public Optional<Review> getReviewById(long reviewId) {

        try {
            Optional<Review> review = Optional.ofNullable(jdbc.queryForObject(
                    FIND_REVIEW_QUERY,
                    Map.of("review_id", reviewId),
                    reviewRowMapper
            ));
            Long score = jdbc.queryForObject(COUNT_REVIEW_SCORE, Map.of("review_id", reviewId), Long.class);

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
        Map<Long, Long> scoreMap = new HashMap<>(); // мап пост id - рейтинг
        Map<Long, Review> reviewMap = new HashMap<>();

        jdbc.query(COUNT_FILMS_REVIEWS_SCORES, Map.of("film_id", filmId), (resultSet) -> {
            Long id = resultSet.getLong("id");
            Long score = resultSet.getLong("useful");
            scoreMap.put(id, score);
        });
        jdbc.query(FIND_FILM_REVIEWS_QUERY, Map.of("film_id", filmId, "count", count), reviewRowMapper)
                .forEach((review) -> {
                    review.setUseful(scoreMap.get(review.getReviewId()));
                    reviewMap.put(review.getReviewId(), review);
                });

        return new ArrayList<>(reviewMap.values());
    }

    @Override
    public List<Review> getAllReviews(long count) {
        Map<Long, Long> scoreMap = new HashMap<>();
        Map<Long, Review> reviewMap = new HashMap<>();

        jdbc.query(COUNT_ALL_REVIEWS_SCORES, (resultSet) -> {
            scoreMap.put(resultSet.getLong("id"),
                    resultSet.getLong("useful"));
        });
        jdbc.query(FIND_ALL_REVIEWS_QUERY, Map.of("count", count), reviewRowMapper).forEach((review) -> {
            review.setUseful(scoreMap.get(review.getReviewId()));
            reviewMap.put(review.getReviewId(), review);
        });

        return new ArrayList<>(reviewMap.values());
    }

    @Override
    public Review createReview(Review review) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        Timestamp timestamp = Timestamp.from(Instant.now());
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId())
                .addValue("is_positive", review.getIsPositive())
                .addValue("timestamp", timestamp);

        jdbc.update(REVIEW_INSERT_QUERY, parameters, generatedKeyHolder, new String[]{"id"});

        Long id = generatedKeyHolder.getKeyAs(Long.class);
        if (Objects.nonNull(id)) {
            review.setReviewId(id);
            return review;
        } else {
            return null;
        }
    }

    @Override
    public Review updateReview(Review review) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        Timestamp timestamp = Timestamp.from(Instant.now());
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", review.getReviewId())
                .addValue("content", review.getContent())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId())
                .addValue("is_positive", review.getIsPositive())
                .addValue("timestamp", timestamp);
        jdbc.update(REVIEW_UPDATE_QUERY, parameters);
        return review;
    }

    @Override
    public boolean deleteReview(long reviewId) {
        int rows = jdbc.update(REVIEW_DELETE_QUERY, Map.of("id", reviewId));
        return rows > 0;
    }

    @Override
    public void setLikeReview(long reviewId, long userId, boolean isPositive) {
        jdbc.update(ADD_LIKE_QUERY, Map.of("review_id", reviewId, "user_id", userId, "is_like", isPositive));
    }

    @Override
    public boolean deleteLikeReview(long reviewId, long userId) {
        // REMOVE_ANY_LIKE_QUERY
        int rows = jdbc.update(REMOVE_ANY_LIKE_QUERY, Map.of("review_id", reviewId, "user_id", userId));
        return rows > 0;
    }

    @Override
    public boolean deleteDislikeReview(long reviewId, long userId) {
        int rows = jdbc.update(REMOVE_CERTAIN_LIKE_QUERY, Map.of("review_id", reviewId, "user_id", userId,
                "is_like", false));
        return rows > 0;
    }

    @Override
    public void eventReview(long userId, long reviewId, OperationType operationType) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        jdbc.update(INSERT_EVENT_QUERY, Map.of("user_id", userId, "entity_id", reviewId,
                "timestamp", timestamp, "event_type", EventType.REVIEW.toString(),
                "operation_type", operationType.toString()));
    }
}
