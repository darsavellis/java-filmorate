package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {

    private final String REVIEW_ID_NOT_FOUND = "Review ID=%s not found";

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    @Override
    public Review getReviewById(long reviewId) {
        return reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format(REVIEW_ID_NOT_FOUND, reviewId)));
    }

    @Override
    public List<Review> getReviewsByFilmId(Optional<Long> filmId, long count) {
        if (filmId.isPresent()) {
            return reviewRepository.getReviewsByFilmId(filmId.get(), count);
        } else {
            return reviewRepository.getAllReviews(count);
        }
    }

    @Override
    public Review createReview(Review review) {
        deepValidateReview(review);
        return reviewRepository.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        deepValidateReview(review);
        return reviewRepository.updateReview(review);
    }

    @Override
    public boolean deleteReview(long reviewId) {
        return reviewRepository.deleteReview(reviewId);
    }

    @Override
    public Review setLikeReview(long reviewId, long userId, boolean ifPositive) {
        reviewRepository.setLikeReview(reviewId, userId, ifPositive);
        return reviewRepository.getReviewById(reviewId).get();
    }

    @Override
    public Review deleteLikeReview(long reviewId, long userId) {
        reviewRepository.deleteLikeReview(reviewId, userId);
        return reviewRepository.getReviewById(reviewId).get();
    }

    @Override
    public Review deleteDislikeReview(long reviewId, long userId) {
        reviewRepository.deleteDislikeReview(reviewId, userId);
        return reviewRepository.getReviewById(reviewId).get();
    }

    private void deepValidateReview(Review review) {
        if (userRepository.findById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("The user specified in the review was not found");
        }
        if (filmRepository.getById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("The film specified in the review was not found");
        }
    }
}
