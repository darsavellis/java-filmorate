package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingService {
    Collection<MpaRating> getMpaRatings();

    MpaRating getMpaRatingById(long mpaRatingId);
}
