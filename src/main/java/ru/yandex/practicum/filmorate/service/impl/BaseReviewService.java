package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {

    static final String REVIEW_ID_NOT_FOUND = "Review ID=%s not found";

    final ReviewRepository reviewRepository;
    final UserRepository userRepository;
    final FilmRepository filmRepository;

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
        Review newReview = reviewRepository.createReview(review);
        reviewRepository.eventReview(newReview.getUserId(), newReview.getReviewId(), OperationType.ADD);
        return newReview;
    }

    @Override
    public Review updateReview(Review review) {
        deepValidateReview(review);
        Review newReview = reviewRepository.updateReview(review);
        reviewRepository.eventReview(newReview.getUserId(), newReview.getReviewId(), OperationType.UPDATE);
        return newReview;
    }

    @Override
    public boolean deleteReview(long reviewId) {
        Optional<Review> review = reviewRepository.getReviewById(reviewId);
        if (review.isPresent()) {
            if (reviewRepository.deleteReview(reviewId)) {
                reviewRepository.eventReview(review.get().getUserId(), reviewId, OperationType.REMOVE);
                return true;
            }
        }
        return false;
    }

    @Override
    public Review setLikeReview(long reviewId, long userId, boolean isPositive) {
        reviewRepository.setLikeReview(reviewId, userId, isPositive);
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
