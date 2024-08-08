package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcReviewRepository implements ReviewRepository {

    final NamedParameterJdbcOperations jdbc;
    final ReviewRowMapper reviewRowMapper;


    private static final String FIND_REVIEW_QUERY = "SELECT * FROM reviews WHERE id = :id";
    private static final String FIND_ALL_REVIEWS_QUERY = "SELECT * FROM reviews LIMIT :count";
    private static final String FIND_FILM_REVIEWS_QUERY = "SELECT * FROM reviews WHERE film_id = :filmId LIMIT :count";
    private static final String COUNT_REVIEW_SCORE = "SELECT SUM((is_like - 1 + is_like % 2)) FROM review_user " +
            "WHERE review_id = :reviewId";

    private static final String COUNT_FILMS_REVIEWS_SCORES = "SELECT r.id, SUM((ru.is_like - 1 + ru.is_like % 2)) " +
            "AS useful FROM review_user ru JOIN reviews r ON r.id = ru.review_id WHERE r.film_id = :filmId GROUP BY r.id";

    @Override
    public Optional<Review> getReviewById(long reviewId) {

        Review review = jdbc.queryForObject(
                FIND_REVIEW_QUERY,
                new MapSqlParameterSource("id", reviewId),
                reviewRowMapper
        );
        Long score = jdbc.queryForObject(COUNT_REVIEW_SCORE, new MapSqlParameterSource("reviewId",
                reviewId), Long.class);

        if (Objects.nonNull(score)) {
            review.setUseful(score);
        }
        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, long count) {
        Map<Long, Long> scoreMap = new HashMap<>(); // мапа пост - рейтинг
        Map<Long, Review> reviewMap = new HashMap<>();

        jdbc.query(COUNT_FILMS_REVIEWS_SCORES, (resultSet) -> {
            scoreMap.put(resultSet.getLong("id"),
                    resultSet.getLong("useful"));
        });
        jdbc.query(FIND_FILM_REVIEWS_QUERY, reviewRowMapper).forEach((review) -> {
            review.setUseful(scoreMap.get(review.getId()));
            reviewMap.put(review.getId(), review);
        });

        return new ArrayList<>(reviewMap.values());
    }

    @Override
    public List<Review> getAllReviews(long count) {
        return null;
    }

    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public Review deleteReview(long reviewId) {
        return null;
    }

    @Override
    public Review setLikeReview(long reviewId, long userId, boolean ifPositive) {
        return null;
    }

    @Override
    public Review deleteLikeReview(long reviewId, long userId) {
        return null;
    }

    @Override
    public Review deleteDislikeReview(long reviewId, long userId) {
        return null;
    }
}
