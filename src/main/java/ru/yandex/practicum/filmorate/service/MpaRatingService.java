package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingService {
    List<MpaRating> getMpaRatings();

    MpaRating getMpaRatingById(long mpaRatingId);
}
